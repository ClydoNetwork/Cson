/*
 * This file is part of Cson.
 *
 * Cson is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * Cson is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cson.  If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * Copyright (C) 2024 ClydoNetwork
 */

package net.clydo.cson.writer;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.gson.stream.JsonWriter;
import net.clydo.cson.element.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This writer creates a CsonElement.
 */
public final class CsonJsonTreeWriter extends JsonWriter {
    private static final Writer UNWRITABLE_WRITER =
            new Writer() {
                @Override
                public void write(char @NotNull [] buffer, int offset, int counter) {
                    throw new AssertionError();
                }

                @Override
                public void flush() {
                    throw new AssertionError();
                }

                @Override
                public void close() {
                    throw new AssertionError();
                }
            };

    /**
     * Added to the top of the stack when this writer is closed to cause following ops to fail.
     */
    private static final CsonPrimitive SENTINEL_CLOSED = new CsonPrimitive("closed");

    /**
     * The CsonElements and CsonArrays under modification, outermost to innermost.
     */
    private final List<CsonElement> stack = new ArrayList<>();

    /**
     * The name for the next JSON object value. If non-null, the top of the stack is a CsonObject.
     */
    private String pendingName;

    /**
     * the JSON element constructed by this writer.
     */
    private CsonElement product = CsonNull.INSTANCE; // TODO: is this really what we want?;

    public CsonJsonTreeWriter() {
        super(UNWRITABLE_WRITER);
    }

    /**
     * Returns the top level object produced by this writer.
     */
    public CsonElement get() {
        if (!stack.isEmpty()) {
            throw new IllegalStateException("Expected one JSON element but was " + stack);
        }
        return product;
    }

    private CsonElement peek() {
        return stack.get(stack.size() - 1);
    }

    private void put(CsonElement value) {
        if (pendingName != null) {
            if (!value.isNull() || getSerializeNulls()) {
                CsonObject object = (CsonObject) peek();
                object.put(pendingName, value);
            }
            pendingName = null;
        } else if (stack.isEmpty()) {
            product = value;
        } else {
            CsonElement element = peek();
            if (element instanceof CsonArray) {
                ((CsonArray) element).add(value);
            } else {
                throw new IllegalStateException();
            }
        }
    }

    @CanIgnoreReturnValue
    @Override
    public JsonWriter beginArray() throws IOException {
        CsonArray array = new CsonArray();
        put(array);
        stack.add(array);
        return this;
    }

    @CanIgnoreReturnValue
    @Override
    public JsonWriter endArray() throws IOException {
        if (stack.isEmpty() || pendingName != null) {
            throw new IllegalStateException();
        }
        CsonElement element = peek();
        if (element instanceof CsonArray) {
            stack.remove(stack.size() - 1);
            return this;
        }
        throw new IllegalStateException();
    }

    @CanIgnoreReturnValue
    @Override
    public JsonWriter beginObject() throws IOException {
        CsonObject object = new CsonObject();
        put(object);
        stack.add(object);
        return this;
    }

    @CanIgnoreReturnValue
    @Override
    public JsonWriter endObject() throws IOException {
        if (stack.isEmpty() || pendingName != null) {
            throw new IllegalStateException();
        }
        CsonElement element = peek();
        if (element instanceof CsonObject) {
            stack.remove(stack.size() - 1);
            return this;
        }
        throw new IllegalStateException();
    }

    @CanIgnoreReturnValue
    @Override
    public JsonWriter name(String name) throws IOException {
        Objects.requireNonNull(name, "name == null");
        if (stack.isEmpty() || pendingName != null) {
            throw new IllegalStateException("Did not expect a name");
        }
        CsonElement element = peek();
        if (element instanceof CsonObject) {
            pendingName = name;
            return this;
        }
        throw new IllegalStateException("Please begin an object before writing a name.");
    }

    @CanIgnoreReturnValue
    @Override
    public JsonWriter value(String value) throws IOException {
        if (value == null) {
            return nullValue();
        }
        put(new CsonPrimitive(value));
        return this;
    }

    @CanIgnoreReturnValue
    @Override
    public JsonWriter value(boolean value) throws IOException {
        put(new CsonPrimitive(value));
        return this;
    }

    @CanIgnoreReturnValue
    @Override
    public JsonWriter value(Boolean value) throws IOException {
        if (value == null) {
            return nullValue();
        }
        put(new CsonPrimitive(value));
        return this;
    }

    @CanIgnoreReturnValue
    @Override
    public JsonWriter value(float value) throws IOException {
        if (!isLenient() && (Float.isNaN(value) || Float.isInfinite(value))) {
            throw new IllegalArgumentException("JSON forbids NaN and infinities: " + value);
        }
        put(new CsonPrimitive(value));
        return this;
    }

    @CanIgnoreReturnValue
    @Override
    public JsonWriter value(double value) throws IOException {
        if (!isLenient() && (Double.isNaN(value) || Double.isInfinite(value))) {
            throw new IllegalArgumentException("JSON forbids NaN and infinities: " + value);
        }
        put(new CsonPrimitive(value));
        return this;
    }

    @CanIgnoreReturnValue
    @Override
    public JsonWriter value(long value) throws IOException {
        put(new CsonPrimitive(value));
        return this;
    }

    @CanIgnoreReturnValue
    @Override
    public JsonWriter value(Number value) throws IOException {
        if (value == null) {
            return nullValue();
        }

        if (!isLenient()) {
            double d = value.doubleValue();
            if (Double.isNaN(d) || Double.isInfinite(d)) {
                throw new IllegalArgumentException("JSON forbids NaN and infinities: " + value);
            }
        }

        put(new CsonPrimitive(value));
        return this;
    }

    @CanIgnoreReturnValue
    @Override
    public JsonWriter nullValue() throws IOException {
        put(CsonNull.INSTANCE);
        return this;
    }

    @Override
    public JsonWriter jsonValue(String value) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void flush() throws IOException {
    }

    @Override
    public void close() throws IOException {
        if (!stack.isEmpty()) {
            throw new IOException("Incomplete document");
        }
        stack.add(SENTINEL_CLOSED);
    }
}
