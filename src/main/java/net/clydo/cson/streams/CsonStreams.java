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

package net.clydo.cson.streams;

import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.google.gson.stream.MalformedJsonException;
import lombok.experimental.UtilityClass;
import lombok.val;
import net.clydo.cson.adapter.CsonTypeAdapters;
import net.clydo.cson.element.CsonElement;
import net.clydo.cson.element.CsonNull;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.EOFException;
import java.io.IOException;
import java.io.Writer;
import java.util.Objects;

@UtilityClass
public final class CsonStreams {
    public CsonElement parse(@NotNull JsonReader reader) throws JsonParseException {
        var isEmpty = true;
        try {
            val ignored = reader.peek();
            isEmpty = false;
            return CsonTypeAdapters.CSON_ELEMENT.read(reader);
        } catch (EOFException e) {
            if (isEmpty) {
                return CsonNull.INSTANCE;
            }
            throw new JsonSyntaxException(e);
        } catch (MalformedJsonException | NumberFormatException e) {
            throw new JsonSyntaxException(e);
        } catch (IOException e) {
            throw new JsonIOException(e);
        }
    }

    public void write(CsonElement element, JsonWriter writer) throws IOException {
        CsonTypeAdapters.CSON_ELEMENT.write(writer, element);
    }

    @Contract("null -> new")
    public @NotNull Writer writerForAppendable(Appendable appendable) {
        return appendable instanceof Writer ? (Writer) appendable : new AppendableWriter(appendable);
    }

    private final class AppendableWriter extends Writer {
        private final Appendable appendable;
        private final CurrentWrite currentWrite = new CurrentWrite();

        AppendableWriter(Appendable appendable) {
            this.appendable = appendable;
        }

        @SuppressWarnings("UngroupedOverloads")
        @Override
        public void write(char @NotNull [] chars, int offset, int length) throws IOException {
            currentWrite.setChars(chars);
            appendable.append(currentWrite, offset, offset + length);
        }

        @Override
        public void flush() {
        }

        @Override
        public void close() {
        }

        @Override
        public void write(int i) throws IOException {
            appendable.append((char) i);
        }

        @Override
        public void write(@NotNull String str, int off, int len) throws IOException {
            Objects.requireNonNull(str);
            appendable.append(str, off, off + len);
        }

        @Override
        public Writer append(CharSequence csq) throws IOException {
            appendable.append(csq);
            return this;
        }

        @Override
        public Writer append(CharSequence csq, int start, int end) throws IOException {
            appendable.append(csq, start, end);
            return this;
        }

        private static class CurrentWrite implements CharSequence {
            private char[] chars;
            private String cachedString;

            void setChars(char[] chars) {
                this.chars = chars;
                this.cachedString = null;
            }

            @Override
            public int length() {
                return chars.length;
            }

            @Override
            public char charAt(int i) {
                return chars[i];
            }

            @Override
            public @NotNull CharSequence subSequence(int start, int end) {
                return new String(chars, start, end - start);
            }

            @Override
            public @NotNull String toString() {
                if (cachedString == null) {
                    cachedString = new String(chars);
                }
                return cachedString;
            }
        }
    }
}
