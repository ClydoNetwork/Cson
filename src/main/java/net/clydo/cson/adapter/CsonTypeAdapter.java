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

package net.clydo.cson.adapter;

import com.google.gson.JsonElement;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.LazilyParsedNumber;
import com.google.gson.internal.bind.JsonTreeReader;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import lombok.val;
import net.clydo.cson.CsonConstants;
import net.clydo.cson.element.*;
import net.clydo.cson.reader.CsonJsonTreeReader;
import net.clydo.cson.util.CsonUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.Deque;

public class CsonTypeAdapter extends TypeAdapter<CsonElement> {
    private final Method nextJsonElementMethod;

    public CsonTypeAdapter() {
        try {
            this.nextJsonElementMethod = JsonTreeReader.class.getDeclaredMethod("nextJsonElement");
            this.nextJsonElementMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void write(JsonWriter out, CsonElement value) throws IOException {
        if (value != null && !value.isNull()) {
            if (value.isPrimitive()) {
                val primitive = value.asPrimitive();
                if (primitive.isNumber()) {
                    out.value(primitive.asNumber());
                } else if (primitive.isBoolean()) {
                    out.value(primitive.asBoolean());
                } else {
                    out.value(primitive.asString());
                }
            } else {
                if (value.isArray()) {
                    out.beginArray();

                    val array = value.asArray();
                    val iterator = CsonConstants.getSortMode().sortArray(array);
                    while (iterator.hasNext()) {
                        this.write(out, iterator.next());
                    }

                    out.endArray();
                } else {
                    if (!value.isObject()) {
                        throw new IllegalArgumentException("Couldn't write " + value.getClass());
                    }

                    out.beginObject();

                    val object = value.asObject();
                    val iterator = CsonConstants.getSortMode().sortObject(object);
                    while (iterator.hasNext()) {
                        val entry = iterator.next();
                        out.name(entry.getKey());
                        this.write(out, entry.getValue());
                    }

                    out.endObject();
                }
            }
        } else {
            out.nullValue();
        }
    }

    public JsonElement nextJsonElement(JsonTreeReader reader)  {
        try {
            return (JsonElement) this.nextJsonElementMethod.invoke(reader);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CsonElement read(JsonReader in) throws IOException {
        if (in instanceof JsonTreeReader jsonTreeReader) {
            val jsonElement = this.nextJsonElement(jsonTreeReader);
            return CsonUtil.toCsonElement(jsonElement);
        } else if (in instanceof CsonJsonTreeReader csonJsonTreeReader) {
            return csonJsonTreeReader.nextCsonElement();
        }

        JsonToken peeked = in.peek();
        CsonElement current = this.tryBeginNesting(in, peeked);
        if (current == null) {
            return this.readTerminal(in, peeked);
        } else {
            Deque<CsonElement> stack = new ArrayDeque<>();

            while (true) {
                while (!in.hasNext()) {
                    if (current instanceof CsonArray) {
                        in.endArray();
                    } else {
                        in.endObject();
                    }

                    if (stack.isEmpty()) {
                        return current;
                    }

                    current = stack.removeLast();
                }

                String name = null;
                if (current instanceof CsonObject) {
                    name = in.nextName();
                }

                peeked = in.peek();
                CsonElement value = this.tryBeginNesting(in, peeked);
                boolean isNesting = value != null;
                if (value == null) {
                    value = this.readTerminal(in, peeked);
                }

                if (current instanceof CsonArray csonArray) {
                    csonArray.add(value);
                } else if (current instanceof CsonObject csonObject) {
                    csonObject.put(name, value);
                }

                if (isNesting) {
                    stack.addLast(current);
                    current = value;
                }
            }
        }
    }

    private @Nullable CsonElement tryBeginNesting(JsonReader in, @NotNull JsonToken peeked) throws IOException {
        return switch (peeked) {
            case BEGIN_ARRAY -> {
                in.beginArray();
                yield new CsonArray();
            }
            case BEGIN_OBJECT -> {
                in.beginObject();
                yield new CsonObject();
            }
            default -> null;
        };
    }

    private CsonElement readTerminal(JsonReader in, @NotNull JsonToken peeked) throws IOException {
        return switch (peeked) {
            case NUMBER -> {
                String number = in.nextString();
                yield new CsonPrimitive(new LazilyParsedNumber(number));
            }
            case STRING -> new CsonPrimitive(in.nextString());
            case BOOLEAN -> new CsonPrimitive(in.nextBoolean());
            case NULL -> {
                in.nextNull();
                yield CsonNull.INSTANCE;
            }
            default -> throw new IllegalStateException("Unexpected token: " + peeked);
        };
    }
}
