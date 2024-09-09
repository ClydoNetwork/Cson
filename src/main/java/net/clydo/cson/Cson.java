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

package net.clydo.cson;

import com.google.gson.*;
import com.google.gson.internal.Primitives;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import lombok.val;
import net.clydo.cson.adapter.CsonTypeAdapters;
import net.clydo.cson.element.CsonElement;
import net.clydo.cson.element.CsonNull;
import net.clydo.cson.reader.CsonJsonTreeReader;
import net.clydo.cson.writer.CsonJsonTreeWriter;
import org.jetbrains.annotations.NotNull;

import java.io.Reader;
import java.lang.reflect.Type;

public class Cson {

    private final Gson gson;

    public Cson() {
        this(new GsonBuilder());
    }

    public Cson(FormattingStyle formattingStyle) {
        this(new GsonBuilder(), formattingStyle);
    }

    public Cson(@NotNull GsonBuilder gsonBuilder, FormattingStyle formattingStyle) {
        this(gsonBuilder.setFormattingStyle(formattingStyle));
    }

    public Cson(@NotNull GsonBuilder gsonBuilder) {
        gsonBuilder.registerTypeAdapter(CsonElement.class, CsonTypeAdapters.CSON_ELEMENT);
        gsonBuilder.registerTypeAdapterFactory(CsonTypeAdapters.CSON_ELEMENT_FACTORY);
        this.gson = gsonBuilder.create();
    }

    //region FromJson
    public <T> T fromJson(String json, Class<T> classOfT) throws JsonSyntaxException {
        return this.gson.fromJson(json, classOfT);
    }

    public <T> T fromJson(String json, Type typeOfT) throws JsonSyntaxException {
        return this.gson.fromJson(json, typeOfT);
    }

    public <T> T fromJson(String json, TypeToken<T> typeOfT) throws JsonSyntaxException {
        return this.gson.fromJson(json, typeOfT);
    }

    public <T> T fromJson(Reader json, Class<T> classOfT) throws JsonSyntaxException, JsonIOException {
        return this.gson.fromJson(json, classOfT);
    }

    @SuppressWarnings("TypeParameterUnusedInFormals")
    public <T> T fromJson(Reader json, Type typeOfT) throws JsonIOException, JsonSyntaxException {
        return this.gson.fromJson(json, typeOfT);
    }

    public <T> T fromJson(Reader json, TypeToken<T> typeOfT) throws JsonIOException, JsonSyntaxException {
        return this.gson.fromJson(json, typeOfT);
    }

    public <T> T fromJson(JsonReader reader, Type typeOfT) throws JsonIOException, JsonSyntaxException {
        return this.gson.fromJson(reader, typeOfT);
    }

    public <T> T fromJson(JsonReader reader, TypeToken<T> typeOfT) throws JsonIOException, JsonSyntaxException {
        return this.gson.fromJson(reader, typeOfT);
    }

    public <T> T fromJson(CsonElement json, Class<T> classOfT) throws JsonSyntaxException {
        T object = fromJson(json, TypeToken.get(classOfT));
        return Primitives.wrap(classOfT).cast(object);
    }

    @SuppressWarnings({"unchecked", "TypeParameterUnusedInFormals"})
    public <T> T fromJson(CsonElement json, Type typeOfT) throws JsonSyntaxException {
        return (T) fromJson(json, TypeToken.get(typeOfT));
    }

    public <T> T fromJson(CsonElement json, TypeToken<T> typeOfT) throws JsonSyntaxException {
        if (json == null) {
            return null;
        }
        return fromJson(new CsonJsonTreeReader(json), typeOfT);
    }
    //endregion

    //region ToJson
    public CsonElement toJsonTree(Object src) {
        if (src == null) {
            return CsonNull.INSTANCE;
        }
        return toJsonTree(src, src.getClass());
    }

    public CsonElement toJsonTree(Object src, Type typeOfSrc) {
        val writer = new CsonJsonTreeWriter();
        this.toJson(src, typeOfSrc, writer);
        return writer.get();
    }

    public String toJson(Object src) {
        return this.gson.toJson(src);
    }

    public String toJson(Object src, Type typeOfSrc) {
        return this.gson.toJson(src, typeOfSrc);
    }

    public void toJson(Object src, Appendable writer) throws JsonIOException {
        this.gson.toJson(src, writer);
    }

    public void toJson(Object src, Type typeOfSrc, Appendable writer) throws JsonIOException {
        this.gson.toJson(src, typeOfSrc, writer);
    }

    public void toJson(Object src, Type typeOfSrc, JsonWriter writer) throws JsonIOException {
        this.gson.toJson(src, typeOfSrc, writer);
    }

    public String toJson(CsonElement csonElement) {
        return this.gson.toJson(csonElement);
    }

    public void toJson(CsonElement csonElement, Appendable writer) throws JsonIOException {
        this.gson.toJson(csonElement, writer);
    }

    public void toJson(CsonElement csonElement, JsonWriter writer) throws JsonIOException {
        this.gson.toJson(csonElement, CsonElement.class, writer);
    }
    //endregion
}