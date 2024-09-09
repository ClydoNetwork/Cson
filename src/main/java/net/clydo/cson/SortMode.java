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

import lombok.RequiredArgsConstructor;
import lombok.val;
import net.clydo.cson.element.CsonArray;
import net.clydo.cson.element.CsonElement;
import net.clydo.cson.element.CsonObject;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.StreamSupport;

@RequiredArgsConstructor
public enum SortMode {
    NONE(false, false),
    OBJECT(true, false),
    ARRAY(false, true),
    OBJECT_ARRAY(true, true);

    private final boolean sortObject;
    private final boolean sortArray;
    private Comparator<String> objectComparator = Comparator.naturalOrder();
    private Comparator<String> arrayComparator = Comparator.naturalOrder();

    public void setObjectComparator(Comparator<String> objectComparator) {
        this.objectComparator = objectComparator == null ? Comparator.naturalOrder() : objectComparator;
    }

    public void setArrayComparator(Comparator<String> arrayComparator) {
        this.arrayComparator = arrayComparator == null ? Comparator.naturalOrder() : arrayComparator;
    }

    public @NotNull Iterator<CsonElement> sortArray(CsonArray psonArray) {
        return !this.sortArray
                ? psonArray.iterator()
                : StreamSupport.stream(psonArray.spliterator(), false)
                .sorted(
                        (o1, o2) -> (o1.isPrimitive() && o2.isPrimitive())
                                ? this.arrayComparator.compare(o1.asString(), o2.asString())
                                : 0
                )
                .iterator();
    }

    public @NotNull Iterator<Map.Entry<String, CsonElement>> sortObject(@NotNull CsonObject psonObject) {
        val entries = psonObject.entrySet();
        if (!this.sortObject) {
            return entries.iterator();
        }

        return entries.stream()
                .sorted(Map.Entry.comparingByKey(this.objectComparator))
                .iterator();
    }
}