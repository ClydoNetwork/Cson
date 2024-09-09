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

import com.google.gson.internal.LazilyParsedNumber;
import com.google.gson.internal.NumberLimits;
import lombok.val;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

public class CsonPrimitive extends CsonElement {
    private final Object value;

    public CsonPrimitive(Object object) {
        this.value = Objects.requireNonNull(object);
    }

    public CsonPrimitive(Boolean bool) {
        this.value = Objects.requireNonNull(bool);
    }

    public CsonPrimitive(Number number) {
        this.value = Objects.requireNonNull(number);
    }

    public CsonPrimitive(String string) {
        this.value = Objects.requireNonNull(string);
    }

    public CsonPrimitive(Character character) {
        this.value = Objects.requireNonNull(character).toString();
    }

    @Override
    public CsonElement deepCopy() {
        return this;
    }

    @Override
    public boolean isBoolean() {
        return this.value instanceof Boolean;
    }

    @Override
    public boolean isString() {
        return this.value instanceof String;
    }

    @Override
    public boolean isNumber() {
        return this.value instanceof Number;
    }

    @Override
    public boolean asBoolean() {
        return this.isBoolean() ? (Boolean) this.value : Boolean.parseBoolean(this.asString());
    }

    @Override
    public String asString() {
        if (this.value instanceof String) {
            return (String) this.value;
        } else if (this.value instanceof Character character) {
            return String.valueOf(character);
        } else if (this.isNumber()) {
            return this.asNumber().toString();
        } else if (this.isBoolean()) {
            return ((Boolean) this.value).toString();
        }
        throw new AssertionError("Unexpected value type: " + this.value.getClass());
    }

    @Override
    public char asChar() {
        val s = this.asString();
        if (s.length() == 1) {
            return s.charAt(0);
        }
        throw new UnsupportedOperationException("String value length must be 1 but is " + s.length());
    }

    @Override
    public char asChar(final int index) {
        val s = this.asString();
        if (!s.isEmpty()) {
            return s.charAt(index);
        }
        throw new UnsupportedOperationException("String value is empty");
    }

    @Override
    public Number asNumber() {
        if (this.value instanceof Number number) {
            return number;
        } else if (this.value instanceof String string) {
            return new LazilyParsedNumber(string);
        }
        throw new UnsupportedOperationException("Primitive is neither a number nor a string");
    }

    @Override
    public byte asByte() {
        return this.isNumber() ? this.asNumber().byteValue() : Byte.parseByte(this.asString());
    }

    @Override
    public short asShort() {
        return this.isNumber() ? this.asNumber().shortValue() : Short.parseShort(this.asString());
    }

    @Override
    public int asInt() {
        return this.isNumber() ? this.asNumber().intValue() : Integer.parseInt(this.asString());
    }

    @Override
    public float asFloat() {
        return this.isNumber() ? this.asNumber().floatValue() : Float.parseFloat(this.asString());
    }

    @Override
    public double asDouble() {
        return this.isNumber() ? this.asNumber().doubleValue() : Double.parseDouble(this.asString());
    }

    @Override
    public long asLong() {
        return this.isNumber() ? this.asNumber().longValue() : Long.parseLong(this.asString());
    }

    @Override
    public BigInteger asBigInteger() {
        return this.value instanceof BigInteger bigInteger ? bigInteger : (isIntegral(this) ? BigInteger.valueOf(this.asNumber().longValue()) : NumberLimits.parseBigInteger(this.asString()));
    }

    @Override
    public BigDecimal asBigDecimal() {
        return this.value instanceof BigDecimal bigDecimal ? bigDecimal : NumberLimits.parseBigDecimal(this.asString());
    }

    @Override
    public int hashCode() {
        if (this.value == null) {
            return 31;
        }
        // Using recommended hashing algorithm from Effective Java for longs and doubles
        if (isIntegral(this)) {
            long value = asNumber().longValue();
            return Long.hashCode(value);
        }
        if (this.value instanceof Number) {
            return Double.hashCode(asNumber().doubleValue());
        }
        return this.value.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        CsonPrimitive other = (CsonPrimitive) obj;
        if (this.value == null) {
            return other.value == null;
        }
        if (isIntegral(this) && isIntegral(other)) {
            return (this.value instanceof BigInteger || other.value instanceof BigInteger)
                    ? this.asBigInteger().equals(other.asBigInteger())
                    : this.asNumber().longValue() == other.asNumber().longValue();
        }
        if (this.value instanceof Number && other.value instanceof Number) {
            if (this.value instanceof BigDecimal && other.value instanceof BigDecimal) {
                // Uses compareTo to ignore scale of values, e.g. `0` and `0.00` should be considered equal
                return this.asBigDecimal().compareTo(other.asBigDecimal()) == 0;
            }

            double thisAsDouble = this.asDouble();
            double otherAsDouble = other.asDouble();
            // Don't use Double.compare(double, double) because that considers -0.0 and +0.0 not equal
            return (thisAsDouble == otherAsDouble)
                    || (Double.isNaN(thisAsDouble) && Double.isNaN(otherAsDouble));
        }
        return this.value.equals(other.value);
    }

    /**
     * Returns true if the specified number is an integral type (Long, Integer, Short, Byte,
     * BigInteger)
     */
    @Contract(pure = true)
    private static boolean isIntegral(@NotNull CsonPrimitive primitive) {
        if (primitive.value instanceof Number number) {
            return number instanceof BigInteger
                    || number instanceof Long
                    || number instanceof Integer
                    || number instanceof Short
                    || number instanceof Byte;
        }
        return false;
    }
}
