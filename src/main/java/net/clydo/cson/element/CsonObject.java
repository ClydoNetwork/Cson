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
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.LinkedTreeMap;
import lombok.val;
import net.clydo.cson.util.CsonUtil;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class CsonObject extends CsonElement implements Iterable<Map.Entry<String, CsonElement>> {
    private final LinkedTreeMap<String, CsonElement> members;

    public CsonObject() {
        this.members = new LinkedTreeMap<>(false);
    }

    public CsonObject(Map<String, CsonElement> members) {
        this();
        this.members.putAll(members);
    }

    public CsonObject putUnknown(String key, Object value) {
        val psonElement = CsonUtil.toElement(value);
        this.members.put(key, psonElement);
        return this;
    }

    public CsonObject putAllUnknown(Object[] entries) {
        if (entries == null) {
            throw new IllegalArgumentException("entries cannot be null");
        }

        if (entries.length % 2 != 0) {
            throw new IllegalArgumentException("entries must be even");
        }

        for (int i = 0; i < entries.length; i += 2) {
            val keyObj = entries[i];
            if (!(keyObj instanceof String keyString)) {
                throw new IllegalArgumentException("key must be a string");
            }
            val value = entries[i + 1];
            this.putUnknown(keyString, value);
        }
        return this;
    }

    public CsonObject putAll(Object... elements) {
        return this.putAllUnknown(elements);
    }

    public CsonObject putOther(String key, Object... elements) {
        return this.put(key, new CsonObject().putAll(elements));
    }

    public CsonObject putArray(String key, Object... values) {
        return this.putUnknown(key, values);
    }

    @CanIgnoreReturnValue
    public CsonObject put(String key, CsonElement value) {
        return this.putUnknown(key, value);
    }

    public CsonObject put(String key, String value) {
        return this.putUnknown(key, value);
    }

    public CsonObject put(String key, Number value) {
        return this.putUnknown(key, value);
    }

    public CsonObject put(String key, Boolean value) {
        return this.putUnknown(key, value);
    }

    public CsonObject put(String key, Character value) {
        return this.putUnknown(key, value);
    }

    @CanIgnoreReturnValue
    public CsonElement remove(String key) {
        return this.members.remove(key);
    }

    public @NotNull Set<Map.Entry<String, CsonElement>> entrySet() {
        return this.members.entrySet();
    }

    public @NotNull Set<String> keySet() {
        return this.members.keySet();
    }

    public Map<String, CsonElement> asMap() {
        return this.members;
    }

    public int size() {
        return this.members.size();
    }

    public boolean isEmpty() {
        return this.members.isEmpty();
    }

    public boolean has(String key) {
        return this.members.containsKey(key);
    }

    public CsonElement get(String key) {
        return this.members.get(key);
    }

    @Override
    public @NotNull Iterator<Map.Entry<String, CsonElement>> iterator() {
        return this.members.entrySet().iterator();
    }

    @Override
    public CsonElement deepCopy() {
        val result = new CsonObject();
        for (val entry : this.members.entrySet()) {
            result.put(entry.getKey(), entry.getValue().deepCopy());
        }
        return result;
    }

    @Override
    public CsonObject asObject(final String key) {
        val element = this.get(key);
        if (element == null) {
            throw new JsonSyntaxException("Missing: " + key);
        }

        return element.asObject();
    }

    @Override
    public CsonObject asObjectNullable(final String key) {
        return this.asObject(key, null);
    }

    @Override
    public CsonObject asObject(final String key, final CsonObject defaultValue) {
        val element = this.get(key);
        if (element != null) {
            return element.asObject();
        }

        return defaultValue;
    }

    @Override
    public CsonArray asArray(final String key) {
        val element = this.get(key);
        if (element != null) {
            return element.asArray();
        }

        throw new JsonSyntaxException("Missing: " + key);
    }

    @Override
    public CsonArray asArrayNullable(final String key) {
        return this.asArray(key, null);
    }

    @Override
    public CsonArray asArray(final String key, final CsonArray defaultValue) {
        val element = this.get(key);
        if (element != null) {
            return element.asArray();
        }

        return defaultValue;
    }

    @Override
    public CsonPrimitive asPrimitive(final String key) {
        val element = this.get(key);
        if (element != null) {
            return element.asPrimitive();
        }

        throw new JsonSyntaxException("Missing: " + key);
    }

    @Override
    public CsonPrimitive asPrimitiveNullable(final String key) {
        return this.asPrimitive(key, null);
    }

    @Override
    public CsonPrimitive asPrimitive(final String key, final CsonPrimitive defaultValue) {
        val element = this.get(key);
        if (element != null) {
            return element.asPrimitive();
        }

        return defaultValue;
    }

    @CanIgnoreReturnValue
    @Override
    public CsonNull asNull(final String key) {
        val element = this.get(key);
        if (element != null) {
            return element.asNull();
        }

        throw new JsonSyntaxException("Missing: " + key);
    }

    @CanIgnoreReturnValue
    @Override
    public CsonNull asNullNullable(final String key) {
        return this.asNull(key, null);
    }

    @CanIgnoreReturnValue
    @Override
    public CsonNull asNull(final String key, final CsonNull defaultValue) {
        val element = this.get(key);
        if (element != null) {
            return element.asNull();
        }

        return defaultValue;
    }

    @Override
    public boolean asBoolean(final String key) {
        val element = this.get(key);
        if (element != null) {
            return element.asBoolean();
        }

        throw new JsonSyntaxException("Missing: " + key);
    }

    @Override
    public boolean asBooleanOrFalse(final String key) {
        return this.asBoolean(key, false);
    }

    @Override
    public boolean asBoolean(final String key, final boolean defaultValue) {
        val element = this.get(key);
        if (element != null) {
            return element.asBoolean();
        }

        return defaultValue;
    }

    @Override
    public String asString(final String key) {
        val element = this.get(key);
        if (element != null) {
            return element.asString();
        }

        throw new JsonSyntaxException("Missing: " + key);
    }

    @Override
    public String asStringNullable(final String key) {
        return this.asString(key, null);
    }

    @Override
    public String asString(final String key, final String defaultValue) {
        val element = this.get(key);
        if (element != null) {
            return element.asString();
        }

        return defaultValue;
    }

    @Override
    public char asChar(final String key) {
        val element = this.get(key);
        if (element != null) {
            return element.asChar();
        }
        throw new JsonSyntaxException("Missing: " + key);
    }

    @Override
    public char asCharOrMin(final String key) {
        return this.asChar(key, Character.MIN_VALUE);
    }

    @Override
    public char asChar(final String key, final char defaultValue) {
        val element = this.get(key);
        if (element != null) {
            return element.asChar();
        }

        return defaultValue;
    }

    @Override
    public char asChar(final String key, final int index) {
        val element = this.get(key);
        if (element != null) {
            return element.asChar(index);
        }

        throw new JsonSyntaxException("Missing: " + key);
    }

    @Override
    public char asCharOrMin(final String key, final int index) {
        return this.asChar(key, Character.MIN_VALUE, index);
    }

    @Override
    public char asChar(final String key, final char defaultValue, final int index) {
        val element = this.get(key);
        if (element != null) {
            return element.asChar(index);
        }

        return defaultValue;
    }

    @Override
    public Number asNumber(final String key) {
        val element = this.get(key);
        if (element != null) {
            return element.asNumber();
        }

        throw new JsonSyntaxException("Missing: " + key);
    }

    @Override
    public Number asNumberNullable(final String key) {
        return this.asNumber(key, null);
    }

    @Override
    public Number asNumber(final String key, final Number defaultValue) {
        val element = this.get(key);
        if (element != null) {
            return element.asNumber();
        }

        return defaultValue;
    }

    @Override
    public byte asByte(final String key) {
        val element = this.get(key);
        if (element != null) {
            return element.asByte();
        }

        throw new JsonSyntaxException("Missing: " + key);
    }

    @Override
    public byte asByteOrZero(final String key) {
        return this.asByte(key, (byte) 0);
    }

    @Override
    public byte asByte(final String key, final byte defaultValue) {
        val element = this.get(key);
        if (element != null) {
            return element.asByte();
        }

        return defaultValue;
    }

    @Override
    public short asShort(final String key) {
        val element = this.get(key);
        if (element != null) {
            return element.asShort();
        }

        throw new JsonSyntaxException("Missing: " + key);
    }

    @Override
    public short asShortOrZero(final String key) {
        return this.asShort(key, (short) 0);
    }

    @Override
    public short asShort(final String key, final short defaultValue) {
        val element = this.get(key);
        if (element != null) {
            return element.asShort();
        }

        return defaultValue;
    }

    @Override
    public int asInt(final String key) {
        val element = this.get(key);
        if (element != null) {
            return element.asInt();
        }

        throw new JsonSyntaxException("Missing: " + key);
    }

    @Override
    public int asIntOrZero(final String key) {
        return this.asInt(key, 0);
    }

    @Override
    public int asInt(final String key, final int defaultValue) {
        val element = this.get(key);
        if (element != null) {
            return element.asInt();
        }

        return defaultValue;
    }

    @Override
    public float asFloat(final String key) {
        val element = this.get(key);
        if (element != null) {
            return element.asFloat();
        }

        throw new JsonSyntaxException("Missing: " + key);
    }

    @Override
    public float asFloatOrZero(final String key) {
        return this.asFloat(key, 0);
    }

    @Override
    public float asFloat(final String key, final float defaultValue) {
        val element = this.get(key);
        if (element != null) {
            return element.asFloat();
        }

        return defaultValue;
    }

    @Override
    public double asDouble(final String key) {
        val element = this.get(key);
        if (element != null) {
            return element.asDouble();
        }

        throw new JsonSyntaxException("Missing: " + key);
    }

    @Override
    public double asDoubleOrZero(final String key) {
        return this.asDouble(key, 0);
    }

    @Override
    public double asDouble(final String key, final double defaultValue) {
        val element = this.get(key);
        if (element != null) {
            return element.asDouble();
        }

        return defaultValue;
    }

    @Override
    public long asLong(final String key) {
        val element = this.get(key);
        if (element != null) {
            return element.asLong();
        }

        throw new JsonSyntaxException("Missing: " + key);
    }

    @Override
    public long asLongOrZero(final String key) {
        return this.asLong(key, 0);
    }

    @Override
    public long asLong(final String key, final long defaultValue) {
        val element = this.get(key);
        if (element != null) {
            return element.asLong();
        }

        return defaultValue;
    }

    @Override
    public BigInteger asBigInteger(final String key) {
        val element = this.get(key);
        if (element != null) {
            return element.asBigInteger();
        }

        throw new JsonSyntaxException("Missing: " + key);
    }

    @Override
    public BigInteger asBigIntegerNullable(final String key) {
        return this.asBigInteger(key, null);
    }

    @Override
    public BigInteger asBigInteger(final String key, final BigInteger defaultValue) {
        val element = this.get(key);
        if (element != null) {
            return element.asBigInteger();
        }

        return defaultValue;
    }

    @Override
    public BigDecimal asBigDecimal(final String key) {
        val element = this.get(key);
        if (element != null) {
            return element.asBigDecimal();
        }

        throw new JsonSyntaxException("Missing: " + key);
    }

    @Override
    public BigDecimal asBigDecimalNullable(final String key) {
        return this.asBigDecimal(key, null);
    }

    @Override
    public BigDecimal asBigDecimal(final String key, final BigDecimal defaultValue) {
        val element = this.get(key);
        if (element != null) {
            return element.asBigDecimal();
        }

        return defaultValue;
    }

    @Override
    public boolean equals(Object o) {
        return (o == this) || (o instanceof CsonObject csonObject && csonObject.members.equals(this.members));
    }

    @Override
    public int hashCode() {
        return members.hashCode();
    }
}
