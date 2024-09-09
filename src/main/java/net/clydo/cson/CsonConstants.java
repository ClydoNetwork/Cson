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

import com.google.gson.FormattingStyle;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class CsonConstants {
    @Getter
    @Setter
    private static FormattingStyle globalFormattingStyle = FormattingStyle.COMPACT;

    @Getter
    @NotNull
    private static SortMode sortMode = SortMode.NONE;

    public static void setSortMode(SortMode sortMode) {
        CsonConstants.sortMode = sortMode == null ? SortMode.NONE : sortMode;
    }
}
