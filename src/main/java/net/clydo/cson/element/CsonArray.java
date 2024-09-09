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
import com.google.gson.internal.NonNullElementWrapperList;
import lombok.val;
import net.clydo.cson.util.CsonUtil;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class CsonArray extends CsonElement implements Iterable<CsonElement> {
    private final ArrayList<CsonElement> elements;

    public CsonArray(Collection<CsonElement> list) {
        this.elements = new ArrayList<>(list);
    }

    public CsonArray(CsonElement @NotNull ... elements) {
        this.elements = new ArrayList<>(elements.length);
        this.addAll(elements);
    }

    public CsonArray() {
        this.elements = new ArrayList<>();
    }

    public CsonArray(int capacity) {
        this.elements = new ArrayList<>(capacity);
    }

    @Override
    public CsonElement deepCopy() {
        if (this.elements.isEmpty()) {
            return new CsonArray();
        } else {
            val result = new CsonArray(this.elements.size());
            for (CsonElement element : this.elements) {
                result.add(element.deepCopy());
            }
            return result;
        }
    }

    public CsonArray addUnknown(Object value) {
        val psonElement = CsonUtil.toElement(value);
        this.elements.add(psonElement);
        return this;
    }

    @Contract("_ -> this")
    public CsonArray addAllUnknown(Object @NotNull ... values) {
        for (Object value : values) {
            this.addUnknown(value);
        }
        return this;
    }

    public CsonArray addAll(@NotNull CsonArray array) {
        return this.addAll(array.elements);
    }

    public CsonArray addAll(Object... values) {
        return this.addAllUnknown(values);
    }

    public CsonArray addAll(CsonElement... elements) {
        return this.addAll(List.of(elements));
    }

    public CsonArray addAll(Collection<CsonElement> elements) {
        this.elements.addAll(elements);
        return this;
    }

    public CsonArray add(CsonElement value) {
        return this.addUnknown(value);
    }

    public CsonArray add(Boolean value) {
        return this.addUnknown(value);
    }

    public CsonArray add(Character value) {
        return this.addUnknown(value);
    }

    public CsonArray add(Number value) {
        return this.addUnknown(value);
    }

    public CsonArray add(String value) {
        return this.addUnknown(value);
    }

    @CanIgnoreReturnValue
    public CsonElement set(int index, CsonElement element) {
        return this.elements.set(index, element == null ? CsonNull.INSTANCE : element);
    }

    @CanIgnoreReturnValue
    public boolean remove(CsonElement element) {
        return this.elements.remove(element);
    }

    @CanIgnoreReturnValue
    public CsonElement remove(int index) {
        return this.elements.remove(index);
    }

    public boolean contains(CsonElement element) {
        return this.elements.contains(element);
    }

    public int size() {
        return this.elements.size();
    }

    public boolean isEmpty() {
        return this.elements.isEmpty();
    }

    @Override
    public @NotNull Iterator<CsonElement> iterator() {
        return this.elements.iterator();
    }

    public CsonElement get(int i) {
        return this.elements.get(i);
    }

    private CsonElement asSingle() {
        val size = this.elements.size();
        if (size == 1) {
            return this.elements.get(0);
        }

        throw new IllegalStateException("Array must have size 1, but has size " + size);
    }

    @Contract(value = " -> new", pure = true)
    public @NotNull List<CsonElement> asList() {
        return new NonNullElementWrapperList<>(this.elements);
    }

    @Override
    public boolean asBoolean() {
        return this.asSingle().asBoolean();
    }

    @Override
    public String asString() {
        return this.asSingle().asString();
    }

    @Override
    public char asChar() {
        return this.asSingle().asChar();
    }

    @Override
    public Number asNumber() {
        return this.asSingle().asNumber();
    }

    @Override
    public byte asByte() {
        return this.asSingle().asByte();
    }

    @Override
    public short asShort() {
        return this.asSingle().asShort();
    }

    @Override
    public int asInt() {
        return this.asSingle().asInt();
    }

    @Override
    public float asFloat() {
        return this.asSingle().asFloat();
    }

    @Override
    public double asDouble() {
        return this.asSingle().asDouble();
    }

    @Override
    public long asLong() {
        return this.asSingle().asLong();
    }

    @Override
    public BigInteger asBigInteger() {
        return this.asSingle().asBigInteger();
    }

    @Override
    public BigDecimal asBigDecimal() {
        return this.asSingle().asBigDecimal();
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this || obj instanceof CsonArray psonArray && psonArray.elements.equals(this.elements);
    }

    @Override
    public int hashCode() {
        return this.elements.hashCode();
    }


}
