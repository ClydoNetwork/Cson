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

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.gson.Strictness;
import com.google.gson.stream.JsonWriter;
import net.clydo.cson.CsonConstants;
import net.clydo.cson.streams.CsonStreams;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;

public abstract class CsonElement {
    protected CsonElement() {
    }

    public abstract CsonElement deepCopy();

    //endregion
    //region This is ...
    //region CsonObject
    public final boolean isObject() {
        return this instanceof CsonObject;
    }

    //endregion
    //region CsonArray
    public final boolean isArray() {
        return this instanceof CsonArray;
    }

    //endregion
    //region CsonPrimitive
    public final boolean isPrimitive() {
        return this instanceof CsonPrimitive;
    }

    //endregion
    //region CsonNull
    public final boolean isNull() {
        return this instanceof CsonNull;
    }

    //endregion
    //region Boolean
    public boolean isBoolean() {
        return this.isPrimitive() && this.asPrimitive().isBoolean();
    }

    //endregion
    //region String & Char
    public boolean isString() {
        return this.isPrimitive() && this.asPrimitive().isString();
    }

    //endregion
    //region Number
    public boolean isNumber() {
        return this.isPrimitive() && this.asPrimitive().isNumber();
    }
    //endregion
    //endregion

    //region This as ...
    //region CsonObject
    public final CsonObject asObject() {
        if (this.isObject()) {
            return (CsonObject) this;
        }
        throw new IllegalStateException("Not a JSON Object: " + this);
    }

    //endregion
    //region CsonArray
    public final CsonArray asArray() {
        if (this.isArray()) {
            return (CsonArray) this;
        }
        throw new IllegalStateException("Not a JSON Array: " + this);
    }

    //endregion
    //region CsonPrimitive
    public final CsonPrimitive asPrimitive() {
        if (this.isPrimitive()) {
            return (CsonPrimitive) this;
        }
        throw new IllegalStateException("Not a JSON Primitive: " + this);
    }

    //endregion
    //region CsonNull
    @CanIgnoreReturnValue
    public final CsonNull asNull() {
        if (this.isNull()) {
            return (CsonNull) this;
        }
        throw new IllegalStateException("Not a JSON Null: " + this);
    }

    //endregion
    //region Boolean
    public boolean asBoolean() {
        throw new UnsupportedOperationException(this.getClass().getSimpleName());
    }

    public boolean asBoolean(final boolean defaultValue) {
        if (this instanceof CsonPrimitive csonPrimitive && csonPrimitive.isBoolean()) {
            return csonPrimitive.asBoolean();
        }
        return defaultValue;
    }

    //endregion
    //region String & Char
    public String asString() {
        throw new UnsupportedOperationException(this.getClass().getSimpleName());
    }

    public String asStringOr(final String defaultValue) {
        if (this instanceof CsonPrimitive csonPrimitive && csonPrimitive.isString()) {
            return csonPrimitive.asString();
        }
        return defaultValue;
    }

    public char asChar() {
        throw new UnsupportedOperationException(this.getClass().getSimpleName());
    }

    public char asChar(final int index) {
        throw new UnsupportedOperationException(this.getClass().getSimpleName());
    }

    public char asChar(final char defaultValue) {
        if (this instanceof CsonPrimitive csonPrimitive && csonPrimitive.isString()) {
            return csonPrimitive.asChar();
        }
        return defaultValue;
    }

    public char asChar(final int index, final char defaultValue) {
        if (this instanceof CsonPrimitive csonPrimitive && csonPrimitive.isString()) {
            return csonPrimitive.asChar(index);
        }
        return defaultValue;
    }

    //endregion
    //region Numbers
    public Number asNumber() {
        throw new UnsupportedOperationException(this.getClass().getSimpleName());
    }

    public Number asNumber(final Number defaultValue) {
        if (this instanceof CsonPrimitive csonPrimitive && csonPrimitive.isNumber()) {
            return csonPrimitive.asNumber();
        }
        return defaultValue;
    }

    public byte asByte() {
        throw new UnsupportedOperationException(this.getClass().getSimpleName());
    }

    public byte asByte(final byte defaultValue) {
        if (this instanceof CsonPrimitive csonPrimitive && csonPrimitive.isNumber()) {
            return csonPrimitive.asByte();
        }
        return defaultValue;
    }

    public short asShort() {
        throw new UnsupportedOperationException(this.getClass().getSimpleName());
    }

    public short asShort(final short defaultValue) {
        if (this instanceof CsonPrimitive csonPrimitive && csonPrimitive.isNumber()) {
            return csonPrimitive.asShort();
        }
        return defaultValue;
    }

    public int asInt() {
        throw new UnsupportedOperationException(this.getClass().getSimpleName());
    }

    public int asInt(final int defaultValue) {
        if (this instanceof CsonPrimitive csonPrimitive && csonPrimitive.isNumber()) {
            return csonPrimitive.asInt();
        }
        return defaultValue;
    }

    public float asFloat() {
        throw new UnsupportedOperationException(this.getClass().getSimpleName());
    }

    public float asFloat(final float defaultValue) {
        if (this instanceof CsonPrimitive csonPrimitive && csonPrimitive.isNumber()) {
            return csonPrimitive.asFloat();
        }
        return defaultValue;
    }

    public double asDouble() {
        throw new UnsupportedOperationException(this.getClass().getSimpleName());
    }

    public double asDouble(final double defaultValue) {
        if (this instanceof CsonPrimitive csonPrimitive && csonPrimitive.isNumber()) {
            return csonPrimitive.asDouble();
        }
        return defaultValue;
    }

    public long asLong() {
        throw new UnsupportedOperationException(this.getClass().getSimpleName());
    }

    public long asLong(final long defaultValue) {
        if (this instanceof CsonPrimitive csonPrimitive && csonPrimitive.isNumber()) {
            return csonPrimitive.asLong();
        }
        return defaultValue;
    }

    public BigInteger asBigInteger() {
        throw new UnsupportedOperationException(this.getClass().getSimpleName());
    }

    public BigInteger asBigInteger(final BigInteger defaultValue) {
        if (this instanceof CsonPrimitive csonPrimitive && csonPrimitive.isNumber()) {
            return csonPrimitive.asBigInteger();
        }
        return defaultValue;
    }

    public BigDecimal asBigDecimal() {
        throw new UnsupportedOperationException(this.getClass().getSimpleName());
    }

    public BigDecimal asBigDecimal(final BigDecimal defaultValue) {
        if (this instanceof CsonPrimitive csonPrimitive && csonPrimitive.isNumber()) {
            return csonPrimitive.asBigDecimal();
        }
        return defaultValue;
    }
    //endregion
    //endregion

    //region key of this as ...
    //region CsonObject
    public CsonObject asObject(final String key) {
        throw new UnsupportedOperationException(this.getClass().getSimpleName());
    }

    public CsonObject asObjectNullable(final String key) {
        throw new UnsupportedOperationException(this.getClass().getSimpleName());
    }

    public CsonObject asObject(final String key, final CsonObject defaultValue) {
        throw new UnsupportedOperationException(this.getClass().getSimpleName());
    }

    //endregion
    //region CsonArray
    public CsonArray asArray(final String key) {
        throw new UnsupportedOperationException(this.getClass().getSimpleName());
    }

    public CsonArray asArrayNullable(final String key) {
        throw new UnsupportedOperationException(this.getClass().getSimpleName());
    }

    public CsonArray asArray(final String key, final CsonArray defaultValue) {
        throw new UnsupportedOperationException(this.getClass().getSimpleName());
    }

    //endregion
    //region CsonPrimitive
    public CsonPrimitive asPrimitive(final String key) {
        throw new UnsupportedOperationException(this.getClass().getSimpleName());
    }

    public CsonPrimitive asPrimitiveNullable(final String key) {
        throw new UnsupportedOperationException(this.getClass().getSimpleName());
    }

    public CsonPrimitive asPrimitive(final String key, final CsonPrimitive defaultValue) {
        throw new UnsupportedOperationException(this.getClass().getSimpleName());
    }

    //endregion
    //region CsonNull
    @CanIgnoreReturnValue
    public CsonNull asNull(final String key) {
        throw new UnsupportedOperationException(this.getClass().getSimpleName());
    }

    @CanIgnoreReturnValue
    public CsonNull asNullNullable(final String key) {
        throw new UnsupportedOperationException(this.getClass().getSimpleName());
    }

    @CanIgnoreReturnValue
    public CsonNull asNull(final String key, final CsonNull defaultValue) {
        throw new UnsupportedOperationException(this.getClass().getSimpleName());
    }

    //endregion
    //region Boolean
    public boolean asBoolean(final String key) {
        throw new UnsupportedOperationException(this.getClass().getSimpleName());
    }

    public boolean asBooleanOrFalse(final String key) {
        throw new UnsupportedOperationException(this.getClass().getSimpleName());
    }

    public boolean asBoolean(final String key, final boolean defaultValue) {
        throw new UnsupportedOperationException(this.getClass().getSimpleName());
    }

    //endregion
    //region String & Char
    public String asString(final String key) {
        throw new UnsupportedOperationException(this.getClass().getSimpleName());
    }

    public String asStringNullable(final String key) {
        throw new UnsupportedOperationException(this.getClass().getSimpleName());
    }

    public String asString(final String key, final String defaultValue) {
        throw new UnsupportedOperationException(this.getClass().getSimpleName());
    }

    public char asChar(final String key) {
        throw new UnsupportedOperationException(this.getClass().getSimpleName());
    }

    public char asCharOrMin(final String key) {
        throw new UnsupportedOperationException(this.getClass().getSimpleName());
    }

    public char asChar(final String key, final char defaultValue) {
        throw new UnsupportedOperationException(this.getClass().getSimpleName());
    }

    public char asChar(final String key, final int index) {
        throw new UnsupportedOperationException(this.getClass().getSimpleName());
    }

    public char asCharOrMin(final String key, final int index) {
        throw new UnsupportedOperationException(this.getClass().getSimpleName());
    }

    public char asChar(final String key, final char defaultValue, final int index) {
        throw new UnsupportedOperationException(this.getClass().getSimpleName());
    }

    //endregion
    //region Numbers
    public Number asNumber(final String key) {
        throw new UnsupportedOperationException(this.getClass().getSimpleName());
    }

    public Number asNumberNullable(final String key) {
        throw new UnsupportedOperationException(this.getClass().getSimpleName());
    }

    public Number asNumber(final String key, final Number defaultValue) {
        throw new UnsupportedOperationException(this.getClass().getSimpleName());
    }

    public byte asByte(final String key) {
        throw new UnsupportedOperationException(this.getClass().getSimpleName());
    }

    public byte asByteOrZero(final String key) {
        throw new UnsupportedOperationException(this.getClass().getSimpleName());
    }

    public byte asByte(final String key, final byte defaultValue) {
        throw new UnsupportedOperationException(this.getClass().getSimpleName());
    }

    public short asShort(final String key) {
        throw new UnsupportedOperationException(this.getClass().getSimpleName());
    }

    public short asShortOrZero(final String key) {
        throw new UnsupportedOperationException(this.getClass().getSimpleName());
    }

    public short asShort(final String key, final short defaultValue) {
        throw new UnsupportedOperationException(this.getClass().getSimpleName());
    }

    public int asInt(final String key) {
        throw new UnsupportedOperationException(this.getClass().getSimpleName());
    }

    public int asIntOrZero(final String key) {
        throw new UnsupportedOperationException(this.getClass().getSimpleName());
    }

    public int asInt(final String key, final int defaultValue) {
        throw new UnsupportedOperationException(this.getClass().getSimpleName());
    }

    public float asFloat(final String key) {
        throw new UnsupportedOperationException(this.getClass().getSimpleName());
    }

    public float asFloatOrZero(final String key) {
        throw new UnsupportedOperationException(this.getClass().getSimpleName());
    }

    public float asFloat(final String key, final float defaultValue) {
        throw new UnsupportedOperationException(this.getClass().getSimpleName());
    }

    public double asDouble(final String key) {
        throw new UnsupportedOperationException(this.getClass().getSimpleName());
    }

    public double asDoubleOrZero(final String key) {
        throw new UnsupportedOperationException(this.getClass().getSimpleName());
    }

    public double asDouble(final String key, final double defaultValue) {
        throw new UnsupportedOperationException(this.getClass().getSimpleName());
    }

    public long asLong(final String key) {
        throw new UnsupportedOperationException(this.getClass().getSimpleName());
    }

    public long asLongOrZero(final String key) {
        throw new UnsupportedOperationException(this.getClass().getSimpleName());
    }

    public long asLong(final String key, final long defaultValue) {
        throw new UnsupportedOperationException(this.getClass().getSimpleName());
    }

    public BigInteger asBigInteger(final String key) {
        throw new UnsupportedOperationException(this.getClass().getSimpleName());
    }

    public BigInteger asBigIntegerNullable(final String key) {
        throw new UnsupportedOperationException(this.getClass().getSimpleName());
    }

    public BigInteger asBigInteger(final String key, final BigInteger defaultValue) {
        throw new UnsupportedOperationException(this.getClass().getSimpleName());
    }

    public BigDecimal asBigDecimal(final String key) {
        throw new UnsupportedOperationException(this.getClass().getSimpleName());
    }

    public BigDecimal asBigDecimalNullable(final String key) {
        throw new UnsupportedOperationException(this.getClass().getSimpleName());
    }

    public BigDecimal asBigDecimal(final String key, final BigDecimal defaultValue) {
        throw new UnsupportedOperationException(this.getClass().getSimpleName());
    }
    //endregion
    //endregion

    @Override
    public String toString() {
        try {
            StringWriter stringWriter = new StringWriter();
            JsonWriter jsonWriter = new JsonWriter(stringWriter);
            jsonWriter.setStrictness(Strictness.LENIENT);
            jsonWriter.setFormattingStyle(CsonConstants.getGlobalFormattingStyle());
            CsonStreams.write(this, jsonWriter);
            return stringWriter.toString();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }
}
