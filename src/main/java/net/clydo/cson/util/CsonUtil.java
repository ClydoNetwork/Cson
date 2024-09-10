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

package net.clydo.cson.util;

import com.google.gson.JsonElement;
import lombok.experimental.UtilityClass;
import lombok.val;
import net.clydo.cson.element.*;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class CsonUtil {
    public CsonElement toCsonElement(final JsonElement jsonElement) {
        if (jsonElement != null) {
            if (jsonElement.isJsonPrimitive()) {
                val jsonPrimitive = jsonElement.getAsJsonPrimitive();
                if (jsonPrimitive.isBoolean()) {
                    return new CsonPrimitive(jsonPrimitive.getAsBoolean());
                } else if (jsonPrimitive.isNumber()) {
                    return new CsonPrimitive(jsonPrimitive.getAsBoolean());
                } else if (jsonPrimitive.isString()) {
                    return new CsonPrimitive(jsonPrimitive.getAsBoolean());
                }
            } else if (jsonElement.isJsonArray()) {
                val psonArray = new CsonArray();

                for (JsonElement element : jsonElement.getAsJsonArray()) {
                    psonArray.add(CsonUtil.toCsonElement(element));
                }

                return psonArray;
            } else if (jsonElement.isJsonObject()) {
                val psonObject = new CsonObject();

                for (val entry : jsonElement.getAsJsonObject().entrySet()) {
                    val key = entry.getKey();
                    val value = entry.getValue();

                    psonObject.put(key, CsonUtil.toCsonElement(value));
                }

                return psonObject;
            } else if (jsonElement.isJsonNull()) {
                return CsonNull.INSTANCE;
            }
        } else {
            return CsonNull.INSTANCE;
        }

        throw new IllegalStateException("invalid json element: " + jsonElement);
    }

    public CsonArray toArray(Object @NotNull ... values) {
        val psonElements = new CsonArray(values.length);
        for (Object value : values) {
            psonElements.addUnknown(value);
        }
        return psonElements;
    }

    public CsonElement toElement(Object value) {
        if (value == null) {
            return CsonNull.INSTANCE;
        } else if (value instanceof CsonElement psonElement) {
            return psonElement;
        } else if (value instanceof Boolean || value instanceof Number || value instanceof String || value instanceof Character) {
            return new CsonPrimitive(value);
        } else if (value instanceof Object[] objects) {
            return CsonUtil.toArray(objects);
        }

        throw new IllegalArgumentException("Cannot convert " + value + " to a CsonElement");
    }

//    public static String getType(@Nullable CsonElement json) {
//        String s = abbreviateMiddle(String.valueOf(json), "...", 20);
//
//        if (json == null) {
//            return "null (missing)";
//        } else if (json.isNull()) {
//            return "null (json)";
//        } else if (json.isArray()) {
//            return "an array (" + s + ")";
//        } else if (json.isObject()) {
//            return "an object (" + s + ")";
//        } else {
//            if (json.isPrimitive()) {
//                val primitive = json.asPrimitive();
//                if (primitive.isNumber()) {
//                    return "a number (" + s + ")";
//                }
//                if (primitive.isBoolean()) {
//                    return "a boolean (" + s + ")";
//                }
//            }
//            return s;
//        }
//    }

//    @SuppressWarnings("SameParameterValue")
//    private static String abbreviateMiddle(String str, String middle, int length) {
//        if (str == null || middle == null || str.isEmpty() || middle.isEmpty() || length >= str.length() || length < middle.length() + 2) {
//            return str;
//        }
//
//        int targetString = length - middle.length();
//        int startOffset = (targetString + 1) / 2;
//        int endOffset = str.length() - targetString / 2;
//        return str.substring(0, startOffset) + middle + str.substring(endOffset);
//    }
}
