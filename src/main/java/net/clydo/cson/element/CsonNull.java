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

package net.clydo.cson.element;

public class CsonNull extends CsonElement {
    public static final CsonNull INSTANCE = new CsonNull();

    private CsonNull() {
    }

    @Override
    public CsonElement deepCopy() {
        return INSTANCE;
    }

    @Override
    public int hashCode() {
        return CsonNull.class.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof CsonNull;
    }
}
