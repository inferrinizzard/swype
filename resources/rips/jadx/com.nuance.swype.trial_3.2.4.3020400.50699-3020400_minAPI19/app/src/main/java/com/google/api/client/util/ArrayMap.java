package com.google.api.client.util;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/* loaded from: classes.dex */
public class ArrayMap<K, V> extends AbstractMap<K, V> implements Cloneable {
    Object[] data;
    int size;

    public static <K, V> ArrayMap<K, V> create() {
        return new ArrayMap<>();
    }

    @Override // java.util.AbstractMap, java.util.Map
    public final int size() {
        return this.size;
    }

    public final V getValue(int index) {
        if (index < 0 || index >= this.size) {
            return null;
        }
        return valueAtDataIndex((index << 1) + 1);
    }

    public final V set(int index, V value) {
        int size = this.size;
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        int valueDataIndex = (index << 1) + 1;
        V result = valueAtDataIndex(valueDataIndex);
        this.data[valueDataIndex] = value;
        return result;
    }

    @Override // java.util.AbstractMap, java.util.Map
    public final boolean containsKey(Object key) {
        return -2 != getDataIndexOfKey(key);
    }

    @Override // java.util.AbstractMap, java.util.Map
    public final V get(Object key) {
        return valueAtDataIndex(getDataIndexOfKey(key) + 1);
    }

    @Override // java.util.AbstractMap, java.util.Map
    public final V remove(Object key) {
        return removeFromDataIndexOfKey(getDataIndexOfKey(key));
    }

    private void setData(int dataIndexOfKey, K key, V value) {
        Object[] data = this.data;
        data[dataIndexOfKey] = key;
        data[dataIndexOfKey + 1] = value;
    }

    private V valueAtDataIndex(int i) {
        if (i < 0) {
            return null;
        }
        return (V) this.data[i];
    }

    private int getDataIndexOfKey(Object key) {
        int dataSize = this.size << 1;
        Object[] data = this.data;
        for (int i = 0; i < dataSize; i += 2) {
            Object k = data[i];
            if (key == null) {
                if (k == null) {
                    return i;
                }
            } else if (key.equals(k)) {
                return i;
            }
        }
        return -2;
    }

    final V removeFromDataIndexOfKey(int dataIndexOfKey) {
        int dataSize = this.size << 1;
        if (dataIndexOfKey < 0 || dataIndexOfKey >= dataSize) {
            return null;
        }
        V valueAtDataIndex = valueAtDataIndex(dataIndexOfKey + 1);
        Object[] data = this.data;
        int moved = (dataSize - dataIndexOfKey) - 2;
        if (moved != 0) {
            System.arraycopy(data, dataIndexOfKey + 2, data, dataIndexOfKey, moved);
        }
        this.size--;
        setData(dataSize - 2, null, null);
        return valueAtDataIndex;
    }

    @Override // java.util.AbstractMap, java.util.Map
    public void clear() {
        this.size = 0;
        this.data = null;
    }

    @Override // java.util.AbstractMap, java.util.Map
    public final boolean containsValue(Object value) {
        int dataSize = this.size << 1;
        Object[] data = this.data;
        for (int i = 1; i < dataSize; i += 2) {
            Object v = data[i];
            if (value == null) {
                if (v == null) {
                    return true;
                }
            } else {
                if (value.equals(v)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override // java.util.AbstractMap, java.util.Map
    public final Set<Map.Entry<K, V>> entrySet() {
        return new EntrySet();
    }

    @Override // java.util.AbstractMap
    public final ArrayMap<K, V> clone() {
        try {
            ArrayMap<K, V> result = (ArrayMap) super.clone();
            Object[] data = this.data;
            if (data != null) {
                int length = data.length;
                Object[] resultData = new Object[length];
                result.data = resultData;
                System.arraycopy(data, 0, resultData, 0, length);
                return result;
            }
            return result;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    /* loaded from: classes.dex */
    final class EntrySet extends AbstractSet<Map.Entry<K, V>> {
        EntrySet() {
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
        public final Iterator<Map.Entry<K, V>> iterator() {
            return new EntryIterator();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public final int size() {
            return ArrayMap.this.size;
        }
    }

    /* loaded from: classes.dex */
    final class EntryIterator implements Iterator<Map.Entry<K, V>> {
        private int nextIndex;
        private boolean removed;

        EntryIterator() {
        }

        @Override // java.util.Iterator
        public final boolean hasNext() {
            return this.nextIndex < ArrayMap.this.size;
        }

        @Override // java.util.Iterator
        public final void remove() {
            int index = this.nextIndex - 1;
            if (this.removed || index < 0) {
                throw new IllegalArgumentException();
            }
            ArrayMap.this.removeFromDataIndexOfKey(index << 1);
            this.removed = true;
        }

        @Override // java.util.Iterator
        public final /* bridge */ /* synthetic */ Object next() {
            int i = this.nextIndex;
            if (i == ArrayMap.this.size) {
                throw new NoSuchElementException();
            }
            this.nextIndex++;
            return new Entry(i);
        }
    }

    /* loaded from: classes.dex */
    final class Entry implements Map.Entry<K, V> {
        private int index;

        Entry(int index) {
            this.index = index;
        }

        @Override // java.util.Map.Entry
        public final K getKey() {
            ArrayMap arrayMap = ArrayMap.this;
            int i = this.index;
            if (i < 0 || i >= arrayMap.size) {
                return null;
            }
            return (K) arrayMap.data[i << 1];
        }

        @Override // java.util.Map.Entry
        public final V getValue() {
            return (V) ArrayMap.this.getValue(this.index);
        }

        @Override // java.util.Map.Entry
        public final V setValue(V v) {
            return (V) ArrayMap.this.set(this.index, v);
        }

        @Override // java.util.Map.Entry
        public final int hashCode() {
            return getKey().hashCode() ^ getValue().hashCode();
        }

        @Override // java.util.Map.Entry
        public final boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof Map.Entry)) {
                return false;
            }
            Map.Entry<?, ?> other = (Map.Entry) obj;
            return Objects.equal(getKey(), other.getKey()) && Objects.equal(getValue(), other.getValue());
        }
    }

    @Override // java.util.AbstractMap, java.util.Map
    public final V put(K key, V value) {
        int index = getDataIndexOfKey(key) >> 1;
        if (index == -1) {
            index = this.size;
        }
        if (index < 0) {
            throw new IndexOutOfBoundsException();
        }
        int i = index + 1;
        if (i < 0) {
            throw new IndexOutOfBoundsException();
        }
        Object[] objArr = this.data;
        int i2 = i << 1;
        int length = objArr == null ? 0 : objArr.length;
        if (i2 > length) {
            int i3 = ((length / 2) * 3) + 1;
            if (i3 % 2 != 0) {
                i3++;
            }
            if (i3 >= i2) {
                i2 = i3;
            }
            if (i2 == 0) {
                this.data = null;
            } else {
                int i4 = this.size;
                Object[] objArr2 = this.data;
                if (i4 == 0 || i2 != objArr2.length) {
                    Object[] objArr3 = new Object[i2];
                    this.data = objArr3;
                    if (i4 != 0) {
                        System.arraycopy(objArr2, 0, objArr3, 0, i4 << 1);
                    }
                }
            }
        }
        int i5 = index << 1;
        V valueAtDataIndex = valueAtDataIndex(i5 + 1);
        setData(i5, key, value);
        if (i > this.size) {
            this.size = i;
        }
        return valueAtDataIndex;
    }
}
