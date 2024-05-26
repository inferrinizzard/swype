package android.support.v4.util;

import android.support.v4.util.MapCollections;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/* loaded from: classes.dex */
public class ArrayMap<K, V> extends SimpleArrayMap<K, V> implements Map<K, V> {
    MapCollections<K, V> mCollections;

    public ArrayMap() {
    }

    public ArrayMap(int capacity) {
        super(capacity);
    }

    private MapCollections<K, V> getCollection() {
        if (this.mCollections == null) {
            this.mCollections = new MapCollections<K, V>() { // from class: android.support.v4.util.ArrayMap.1
                @Override // android.support.v4.util.MapCollections
                protected final int colGetSize() {
                    return ArrayMap.this.mSize;
                }

                @Override // android.support.v4.util.MapCollections
                protected final Object colGetEntry(int index, int offset) {
                    return ArrayMap.this.mArray[(index << 1) + offset];
                }

                @Override // android.support.v4.util.MapCollections
                protected final int colIndexOfKey(Object key) {
                    return ArrayMap.this.indexOfKey(key);
                }

                @Override // android.support.v4.util.MapCollections
                protected final int colIndexOfValue(Object value) {
                    return ArrayMap.this.indexOfValue(value);
                }

                @Override // android.support.v4.util.MapCollections
                protected final Map<K, V> colGetMap() {
                    return ArrayMap.this;
                }

                @Override // android.support.v4.util.MapCollections
                protected final void colPut(K key, V value) {
                    ArrayMap.this.put(key, value);
                }

                @Override // android.support.v4.util.MapCollections
                protected final V colSetValue(int index, V value) {
                    return ArrayMap.this.setValueAt(index, value);
                }

                @Override // android.support.v4.util.MapCollections
                protected final void colRemoveAt(int index) {
                    ArrayMap.this.removeAt(index);
                }

                @Override // android.support.v4.util.MapCollections
                protected final void colClear() {
                    ArrayMap.this.clear();
                }
            };
        }
        return this.mCollections;
    }

    @Override // java.util.Map
    public void putAll(Map<? extends K, ? extends V> map) {
        ensureCapacity(this.mSize + map.size());
        for (Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override // java.util.Map
    public Set<Map.Entry<K, V>> entrySet() {
        MapCollections<K, V> collection = getCollection();
        if (collection.mEntrySet == null) {
            collection.mEntrySet = new MapCollections.EntrySet();
        }
        return collection.mEntrySet;
    }

    @Override // java.util.Map
    public Set<K> keySet() {
        MapCollections<K, V> collection = getCollection();
        if (collection.mKeySet == null) {
            collection.mKeySet = new MapCollections.KeySet();
        }
        return collection.mKeySet;
    }

    @Override // java.util.Map
    public Collection<V> values() {
        MapCollections<K, V> collection = getCollection();
        if (collection.mValues == null) {
            collection.mValues = new MapCollections.ValuesCollection();
        }
        return collection.mValues;
    }
}
