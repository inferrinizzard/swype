package com.google.api.client.util;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class DataMap extends AbstractMap<String, Object> {
    final ClassInfo classInfo;
    final Object object;

    @Override // java.util.AbstractMap, java.util.Map
    public final /* bridge */ /* synthetic */ Object put(Object x0, Object x1) {
        String str = (String) x0;
        FieldInfo fieldInfo = this.classInfo.getFieldInfo(str);
        Preconditions.checkNotNull(fieldInfo, "no field of key " + str);
        Object value = fieldInfo.getValue(this.object);
        fieldInfo.setValue(this.object, com.google.api.client.repackaged.com.google.common.base.Preconditions.checkNotNull(x1));
        return value;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DataMap(Object object, boolean ignoreCase) {
        this.object = object;
        this.classInfo = ClassInfo.of(object.getClass(), ignoreCase);
        com.google.api.client.repackaged.com.google.common.base.Preconditions.checkArgument(!this.classInfo.clazz.isEnum());
    }

    @Override // java.util.AbstractMap, java.util.Map
    public final EntrySet entrySet() {
        return new EntrySet();
    }

    @Override // java.util.AbstractMap, java.util.Map
    public final boolean containsKey(Object key) {
        return get(key) != null;
    }

    @Override // java.util.AbstractMap, java.util.Map
    public final Object get(Object key) {
        FieldInfo fieldInfo;
        if ((key instanceof String) && (fieldInfo = this.classInfo.getFieldInfo((String) key)) != null) {
            return fieldInfo.getValue(this.object);
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public final class EntrySet extends AbstractSet<Map.Entry<String, Object>> {
        EntrySet() {
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
        public final EntryIterator iterator() {
            return new EntryIterator();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public final int size() {
            int result = 0;
            for (String name : DataMap.this.classInfo.names) {
                if (DataMap.this.classInfo.getFieldInfo(name).getValue(DataMap.this.object) != null) {
                    result++;
                }
            }
            return result;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public final void clear() {
            for (String name : DataMap.this.classInfo.names) {
                DataMap.this.classInfo.getFieldInfo(name).setValue(DataMap.this.object, null);
            }
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public final boolean isEmpty() {
            for (String name : DataMap.this.classInfo.names) {
                if (DataMap.this.classInfo.getFieldInfo(name).getValue(DataMap.this.object) != null) {
                    return false;
                }
            }
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public final class EntryIterator implements Iterator<Map.Entry<String, Object>> {
        private FieldInfo currentFieldInfo;
        private boolean isComputed;
        private boolean isRemoved;
        private FieldInfo nextFieldInfo;
        private Object nextFieldValue;
        private int nextKeyIndex = -1;

        EntryIterator() {
        }

        @Override // java.util.Iterator
        public final boolean hasNext() {
            if (!this.isComputed) {
                this.isComputed = true;
                this.nextFieldValue = null;
                while (this.nextFieldValue == null) {
                    int i = this.nextKeyIndex + 1;
                    this.nextKeyIndex = i;
                    if (i >= DataMap.this.classInfo.names.size()) {
                        break;
                    }
                    this.nextFieldInfo = DataMap.this.classInfo.getFieldInfo(DataMap.this.classInfo.names.get(this.nextKeyIndex));
                    this.nextFieldValue = this.nextFieldInfo.getValue(DataMap.this.object);
                }
            }
            return this.nextFieldValue != null;
        }

        @Override // java.util.Iterator
        public final void remove() {
            com.google.api.client.repackaged.com.google.common.base.Preconditions.checkState((this.currentFieldInfo == null || this.isRemoved) ? false : true);
            this.isRemoved = true;
            this.currentFieldInfo.setValue(DataMap.this.object, null);
        }

        @Override // java.util.Iterator
        public final /* bridge */ /* synthetic */ Map.Entry<String, Object> next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            this.currentFieldInfo = this.nextFieldInfo;
            Object obj = this.nextFieldValue;
            this.isComputed = false;
            this.isRemoved = false;
            this.nextFieldInfo = null;
            this.nextFieldValue = null;
            return new Entry(this.currentFieldInfo, obj);
        }
    }

    /* loaded from: classes.dex */
    final class Entry implements Map.Entry<String, Object> {
        private final FieldInfo fieldInfo;
        private Object fieldValue;

        Entry(FieldInfo fieldInfo, Object fieldValue) {
            this.fieldInfo = fieldInfo;
            this.fieldValue = com.google.api.client.repackaged.com.google.common.base.Preconditions.checkNotNull(fieldValue);
        }

        /* JADX INFO: Access modifiers changed from: private */
        @Override // java.util.Map.Entry
        public String getKey() {
            String result = this.fieldInfo.name;
            if (DataMap.this.classInfo.ignoreCase) {
                return result.toLowerCase();
            }
            return result;
        }

        @Override // java.util.Map.Entry
        public final Object getValue() {
            return this.fieldValue;
        }

        @Override // java.util.Map.Entry
        public final Object setValue(Object value) {
            Object oldValue = this.fieldValue;
            this.fieldValue = com.google.api.client.repackaged.com.google.common.base.Preconditions.checkNotNull(value);
            this.fieldInfo.setValue(DataMap.this.object, value);
            return oldValue;
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
            return getKey().equals(other.getKey()) && getValue().equals(other.getValue());
        }
    }
}
