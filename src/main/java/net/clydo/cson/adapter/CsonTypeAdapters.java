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

import com.google.gson.*;
import com.google.gson.internal.bind.TypeAdapters;
import lombok.experimental.UtilityClass;
import net.clydo.cson.element.CsonElement;

@UtilityClass
public class CsonTypeAdapters {
    public static final TypeAdapter<CsonElement> CSON_ELEMENT;
    public static final TypeAdapterFactory CSON_ELEMENT_FACTORY;

    static {
        CSON_ELEMENT = new CsonTypeAdapter();
        CSON_ELEMENT_FACTORY = TypeAdapters.newTypeHierarchyFactory(CsonElement.class, CSON_ELEMENT);
    }
}
