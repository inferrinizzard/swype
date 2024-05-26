package android.support.v4.util;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/* loaded from: classes.dex */
public abstract class MapCollections<K, V> {

    /* JADX WARN: Incorrect inner types in field signature: Landroid/support/v4/util/MapCollections<TK;TV;>.android/support/v4/util/MapCollections$android/support/v4/util/MapCollections$android/support/v4/util/MapCollections$android/support/v4/util/MapCollections$EntrySet; */
    EntrySet mEntrySet;

    /* JADX WARN: Incorrect inner types in field signature: Landroid/support/v4/util/MapCollections<TK;TV;>.android/support/v4/util/MapCollections$android/support/v4/util/MapCollections$android/support/v4/util/MapCollections$android/support/v4/util/MapCollections$KeySet; */
    KeySet mKeySet;

    /* JADX WARN: Incorrect inner types in field signature: Landroid/support/v4/util/MapCollections<TK;TV;>.android/support/v4/util/MapCollections$android/support/v4/util/MapCollections$android/support/v4/util/MapCollections$android/support/v4/util/MapCollections$ValuesCollection; */
    ValuesCollection mValues;

    protected abstract void colClear();

    protected abstract Object colGetEntry(int i, int i2);

    protected abstract Map<K, V> colGetMap();

    protected abstract int colGetSize();

    protected abstract int colIndexOfKey(Object obj);

    protected abstract int colIndexOfValue(Object obj);

    protected abstract void colPut(K k, V v);

    protected abstract void colRemoveAt(int i);

    protected abstract V colSetValue(int i, V v);

    /* loaded from: classes.dex */
    final class ArrayIterator<T> implements Iterator<T> {
        boolean mCanRemove = false;
        int mIndex;
        final int mOffset;
        int mSize;

        ArrayIterator(int offset) {
            this.mOffset = offset;
            this.mSize = MapCollections.this.colGetSize();
        }

        @Override // java.util.Iterator
        public final boolean hasNext() {
            return this.mIndex < this.mSize;
        }

        @Override // java.util.Iterator
        public final T next() {
            T t = (T) MapCollections.this.colGetEntry(this.mIndex, this.mOffset);
            this.mIndex++;
            this.mCanRemove = true;
            return t;
        }

        @Override // java.util.Iterator
        public final void remove() {
            if (!this.mCanRemove) {
                throw new IllegalStateException();
            }
            this.mIndex--;
            this.mSize--;
            this.mCanRemove = false;
            MapCollections.this.colRemoveAt(this.mIndex);
        }
    }

    /* loaded from: classes.dex */
    final class MapIterator implements Iterator<Map.Entry<K, V>>, Map.Entry<K, V> {
        int mEnd;
        boolean mEntryValid = false;
        int mIndex = -1;

        MapIterator() {
            this.mEnd = MapCollections.this.colGetSize() - 1;
        }

        @Override // java.util.Iterator
        public final boolean hasNext() {
            return this.mIndex < this.mEnd;
        }

        @Override // java.util.Iterator
        public final void remove() {
            if (!this.mEntryValid) {
                throw new IllegalStateException();
            }
            MapCollections.this.colRemoveAt(this.mIndex);
            this.mIndex--;
            this.mEnd--;
            this.mEntryValid = false;
        }

        @Override // java.util.Map.Entry
        public final K getKey() {
            if (!this.mEntryValid) {
                throw new IllegalStateException("This container does not support retaining Map.Entry objects");
            }
            return (K) MapCollections.this.colGetEntry(this.mIndex, 0);
        }

        @Override // java.util.Map.Entry
        public final V getValue() {
            if (!this.mEntryValid) {
                throw new IllegalStateException("This container does not support retaining Map.Entry objects");
            }
            return (V) MapCollections.this.colGetEntry(this.mIndex, 1);
        }

        @Override // java.util.Map.Entry
        public final V setValue(V v) {
            if (!this.mEntryValid) {
                throw new IllegalStateException("This container does not support retaining Map.Entry objects");
            }
            return (V) MapCollections.this.colSetValue(this.mIndex, v);
        }

        @Override // java.util.Map.Entry
        public final boolean equals(Object o) {
            if (!this.mEntryValid) {
                throw new IllegalStateException("This container does not support retaining Map.Entry objects");
            }
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry<?, ?> e = (Map.Entry) o;
            return ContainerHelpers.equal(e.getKey(), MapCollections.this.colGetEntry(this.mIndex, 0)) && ContainerHelpers.equal(e.getValue(), MapCollections.this.colGetEntry(this.mIndex, 1));
        }

        @Override // java.util.Map.Entry
        public final int hashCode() {
            if (!this.mEntryValid) {
                throw new IllegalStateException("This container does not support retaining Map.Entry objects");
            }
            Object key = MapCollections.this.colGetEntry(this.mIndex, 0);
            Object value = MapCollections.this.colGetEntry(this.mIndex, 1);
            return (value != null ? value.hashCode() : 0) ^ (key == null ? 0 : key.hashCode());
        }

        public final String toString() {
            return getKey() + "=" + getValue();
        }

        @Override // java.util.Iterator
        public final /* bridge */ /* synthetic */ Object next() {
            this.mIndex++;
            this.mEntryValid = true;
            return this;
        }
    }

    /* loaded from: classes.dex */
    final class EntrySet implements Set<Map.Entry<K, V>> {
        /* JADX INFO: Access modifiers changed from: package-private */
        public EntrySet() {
        }

        @Override // java.util.Set, java.util.Collection
        public final boolean addAll(Collection<? extends Map.Entry<K, V>> collection) {
            int oldSize = MapCollections.this.colGetSize();
            for (Map.Entry<K, V> entry : collection) {
                MapCollections.this.colPut(entry.getKey(), entry.getValue());
            }
            return oldSize != MapCollections.this.colGetSize();
        }

        @Override // java.util.Set, java.util.Collection
        public final void clear() {
            MapCollections.this.colClear();
        }

        @Override // java.util.Set, java.util.Collection
        public final boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry<?, ?> e = (Map.Entry) o;
            int index = MapCollections.this.colIndexOfKey(e.getKey());
            if (index >= 0) {
                return ContainerHelpers.equal(MapCollections.this.colGetEntry(index, 1), e.getValue());
            }
            return false;
        }

        @Override // java.util.Set, java.util.Collection
        public final boolean containsAll(Collection<?> collection) {
            Iterator<?> it = collection.iterator();
            while (it.hasNext()) {
                if (!contains(it.next())) {
                    return false;
                }
            }
            return true;
        }

        @Override // java.util.Set, java.util.Collection
        public final boolean isEmpty() {
            return MapCollections.this.colGetSize() == 0;
        }

        @Override // java.util.Set, java.util.Collection, java.lang.Iterable
        public final Iterator<Map.Entry<K, V>> iterator() {
            return new MapIterator();
        }

        @Override // java.util.Set, java.util.Collection
        public final boolean remove(Object object) {
            throw new UnsupportedOperationException();
        }

        @Override // java.util.Set, java.util.Collection
        public final boolean removeAll(Collection<?> collection) {
            throw new UnsupportedOperationException();
        }

        @Override // java.util.Set, java.util.Collection
        public final boolean retainAll(Collection<?> collection) {
            throw new UnsupportedOperationException();
        }

        @Override // java.util.Set, java.util.Collection
        public final int size() {
            return MapCollections.this.colGetSize();
        }

        @Override // java.util.Set, java.util.Collection
        public final Object[] toArray() {
            throw new UnsupportedOperationException();
        }

        @Override // java.util.Set, java.util.Collection
        public final <T> T[] toArray(T[] array) {
            throw new UnsupportedOperationException();
        }

        @Override // java.util.Set, java.util.Collection
        public final boolean equals(Object object) {
            return MapCollections.equalsSetHelper(this, object);
        }

        @Override // java.util.Set, java.util.Collection
        public final int hashCode() {
            int result = 0;
            for (int i = MapCollections.this.colGetSize() - 1; i >= 0; i--) {
                Object key = MapCollections.this.colGetEntry(i, 0);
                Object value = MapCollections.this.colGetEntry(i, 1);
                result += (value == null ? 0 : value.hashCode()) ^ (key == null ? 0 : key.hashCode());
            }
            return result;
        }

        @Override // java.util.Set, java.util.Collection
        public final /* bridge */ /* synthetic */ boolean add(Object obj) {
            throw new UnsupportedOperationException();
        }
    }

    /* loaded from: classes.dex */
    final class KeySet implements Set<K> {
        /* JADX INFO: Access modifiers changed from: package-private */
        public KeySet() {
        }

        @Override // java.util.Set, java.util.Collection
        public final boolean add(K object) {
            throw new UnsupportedOperationException();
        }

        @Override // java.util.Set, java.util.Collection
        public final boolean addAll(Collection<? extends K> collection) {
            throw new UnsupportedOperationException();
        }

        @Override // java.util.Set, java.util.Collection
        public final void clear() {
            MapCollections.this.colClear();
        }

        @Override // java.util.Set, java.util.Collection
        public final boolean contains(Object object) {
            return MapCollections.this.colIndexOfKey(object) >= 0;
        }

        @Override // java.util.Set, java.util.Collection
        public final boolean containsAll(Collection<?> collection) {
            Map<K, V> colGetMap = MapCollections.this.colGetMap();
            Iterator<?> it = collection.iterator();
            while (it.hasNext()) {
                if (!colGetMap.containsKey(it.next())) {
                    return false;
                }
            }
            return true;
        }

        @Override // java.util.Set, java.util.Collection
        public final boolean isEmpty() {
            return MapCollections.this.colGetSize() == 0;
        }

        @Override // java.util.Set, java.util.Collection, java.lang.Iterable
        public final Iterator<K> iterator() {
            return new ArrayIterator(0);
        }

        @Override // java.util.Set, java.util.Collection
        public final boolean remove(Object object) {
            int index = MapCollections.this.colIndexOfKey(object);
            if (index < 0) {
                return false;
            }
            MapCollections.this.colRemoveAt(index);
            return true;
        }

        @Override // java.util.Set, java.util.Collection
        public final boolean removeAll(Collection<?> collection) {
            Map<K, V> colGetMap = MapCollections.this.colGetMap();
            int size = colGetMap.size();
            Iterator<?> it = collection.iterator();
            while (it.hasNext()) {
                colGetMap.remove(it.next());
            }
            return size != colGetMap.size();
        }

        @Override // java.util.Set, java.util.Collection
        public final boolean retainAll(Collection<?> collection) {
            return MapCollections.retainAllHelper(MapCollections.this.colGetMap(), collection);
        }

        @Override // java.util.Set, java.util.Collection
        public final int size() {
            return MapCollections.this.colGetSize();
        }

        @Override // java.util.Set, java.util.Collection
        public final Object[] toArray() {
            return MapCollections.this.toArrayHelper(0);
        }

        @Override // java.util.Set, java.util.Collection
        public final <T> T[] toArray(T[] tArr) {
            return (T[]) MapCollections.this.toArrayHelper(tArr, 0);
        }

        @Override // java.util.Set, java.util.Collection
        public final boolean equals(Object object) {
            return MapCollections.equalsSetHelper(this, object);
        }

        @Override // java.util.Set, java.util.Collection
        public final int hashCode() {
            int result = 0;
            for (int i = MapCollections.this.colGetSize() - 1; i >= 0; i--) {
                Object obj = MapCollections.this.colGetEntry(i, 0);
                result += obj == null ? 0 : obj.hashCode();
            }
            return result;
        }
    }

    /* loaded from: classes.dex */
    final class ValuesCollection implements Collection<V> {
        /* JADX INFO: Access modifiers changed from: package-private */
        public ValuesCollection() {
        }

        @Override // java.util.Collection
        public final boolean add(V object) {
            throw new UnsupportedOperationException();
        }

        @Override // java.util.Collection
        public final boolean addAll(Collection<? extends V> collection) {
            throw new UnsupportedOperationException();
        }

        @Override // java.util.Collection
        public final void clear() {
            MapCollections.this.colClear();
        }

        @Override // java.util.Collection
        public final boolean contains(Object object) {
            return MapCollections.this.colIndexOfValue(object) >= 0;
        }

        @Override // java.util.Collection
        public final boolean containsAll(Collection<?> collection) {
            Iterator<?> it = collection.iterator();
            while (it.hasNext()) {
                if (!contains(it.next())) {
                    return false;
                }
            }
            return true;
        }

        @Override // java.util.Collection
        public final boolean isEmpty() {
            return MapCollections.this.colGetSize() == 0;
        }

        @Override // java.util.Collection, java.lang.Iterable
        public final Iterator<V> iterator() {
            return new ArrayIterator(1);
        }

        @Override // java.util.Collection
        public final boolean remove(Object object) {
            int index = MapCollections.this.colIndexOfValue(object);
            if (index < 0) {
                return false;
            }
            MapCollections.this.colRemoveAt(index);
            return true;
        }

        @Override // java.util.Collection
        public final boolean removeAll(Collection<?> collection) {
            int N = MapCollections.this.colGetSize();
            boolean changed = false;
            int i = 0;
            while (i < N) {
                Object cur = MapCollections.this.colGetEntry(i, 1);
                if (collection.contains(cur)) {
                    MapCollections.this.colRemoveAt(i);
                    i--;
                    N--;
                    changed = true;
                }
                i++;
            }
            return changed;
        }

        @Override // java.util.Collection
        public final boolean retainAll(Collection<?> collection) {
            int N = MapCollections.this.colGetSize();
            boolean changed = false;
            int i = 0;
            while (i < N) {
                Object cur = MapCollections.this.colGetEntry(i, 1);
                if (!collection.contains(cur)) {
                    MapCollections.this.colRemoveAt(i);
                    i--;
                    N--;
                    changed = true;
                }
                i++;
            }
            return changed;
        }

        @Override // java.util.Collection
        public final int size() {
            return MapCollections.this.colGetSize();
        }

        @Override // java.util.Collection
        public final Object[] toArray() {
            return MapCollections.this.toArrayHelper(1);
        }

        @Override // java.util.Collection
        public final <T> T[] toArray(T[] tArr) {
            return (T[]) MapCollections.this.toArrayHelper(tArr, 1);
        }
    }

    public static <K, V> boolean retainAllHelper(Map<K, V> map, Collection<?> collection) {
        int oldSize = map.size();
        Iterator<K> it = map.keySet().iterator();
        while (it.hasNext()) {
            if (!collection.contains(it.next())) {
                it.remove();
            }
        }
        return oldSize != map.size();
    }

    public final Object[] toArrayHelper(int offset) {
        int N = colGetSize();
        Object[] result = new Object[N];
        for (int i = 0; i < N; i++) {
            result[i] = colGetEntry(i, offset);
        }
        return result;
    }

    public final <T> T[] toArrayHelper(T[] tArr, int i) {
        int colGetSize = colGetSize();
        if (tArr.length < colGetSize) {
            tArr = (T[]) ((Object[]) Array.newInstance(tArr.getClass().getComponentType(), colGetSize));
        }
        for (int i2 = 0; i2 < colGetSize; i2++) {
            tArr[i2] = colGetEntry(i2, i);
        }
        if (tArr.length > colGetSize) {
            tArr[colGetSize] = null;
        }
        return tArr;
    }

    public static <T> boolean equalsSetHelper(Set<T> set, Object object) {
        if (set == object) {
            return true;
        }
        if (!(object instanceof Set)) {
            return false;
        }
        Set<?> s = (Set) object;
        try {
            if (set.size() == s.size()) {
                if (set.containsAll(s)) {
                    return true;
                }
            }
            return false;
        } catch (ClassCastException e) {
            return false;
        } catch (NullPointerException e2) {
            return false;
        }
    }
}
