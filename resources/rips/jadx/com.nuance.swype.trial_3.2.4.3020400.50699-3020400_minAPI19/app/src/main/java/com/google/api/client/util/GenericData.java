package com.google.api.client.util;

import com.google.api.client.util.DataMap;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/* loaded from: classes.dex */
public class GenericData extends AbstractMap<String, Object> implements Cloneable {
    final ClassInfo classInfo;
    Map<String, Object> unknownFields;

    /* loaded from: classes.dex */
    public enum Flags {
        IGNORE_CASE
    }

    public GenericData() {
        this(EnumSet.noneOf(Flags.class));
    }

    public GenericData(EnumSet<Flags> flags) {
        this.unknownFields = ArrayMap.create();
        this.classInfo = ClassInfo.of(getClass(), flags.contains(Flags.IGNORE_CASE));
    }

    @Override // java.util.AbstractMap, java.util.Map
    public final Object get(Object name) {
        if (!(name instanceof String)) {
            return null;
        }
        String fieldName = (String) name;
        FieldInfo fieldInfo = this.classInfo.getFieldInfo(fieldName);
        if (fieldInfo != null) {
            return fieldInfo.getValue(this);
        }
        if (this.classInfo.ignoreCase) {
            fieldName = fieldName.toLowerCase();
        }
        return this.unknownFields.get(fieldName);
    }

    @Override // java.util.AbstractMap, java.util.Map
    public final Object put(String fieldName, Object value) {
        FieldInfo fieldInfo = this.classInfo.getFieldInfo(fieldName);
        if (fieldInfo != null) {
            Object oldValue = fieldInfo.getValue(this);
            fieldInfo.setValue(this, value);
            return oldValue;
        }
        if (this.classInfo.ignoreCase) {
            fieldName = fieldName.toLowerCase();
        }
        Object oldValue2 = this.unknownFields.put(fieldName, value);
        return oldValue2;
    }

    public GenericData set(String fieldName, Object value) {
        FieldInfo fieldInfo = this.classInfo.getFieldInfo(fieldName);
        if (fieldInfo != null) {
            fieldInfo.setValue(this, value);
        } else {
            if (this.classInfo.ignoreCase) {
                fieldName = fieldName.toLowerCase();
            }
            this.unknownFields.put(fieldName, value);
        }
        return this;
    }

    @Override // java.util.AbstractMap, java.util.Map
    public final void putAll(Map<? extends String, ?> map) {
        for (Map.Entry<? extends String, ?> entry : map.entrySet()) {
            set(entry.getKey(), entry.getValue());
        }
    }

    @Override // java.util.AbstractMap, java.util.Map
    public final Object remove(Object name) {
        if (!(name instanceof String)) {
            return null;
        }
        String fieldName = (String) name;
        if (this.classInfo.getFieldInfo(fieldName) != null) {
            throw new UnsupportedOperationException();
        }
        if (this.classInfo.ignoreCase) {
            fieldName = fieldName.toLowerCase();
        }
        return this.unknownFields.remove(fieldName);
    }

    @Override // java.util.AbstractMap, java.util.Map
    public Set<Map.Entry<String, Object>> entrySet() {
        return new EntrySet();
    }

    @Override // java.util.AbstractMap
    public GenericData clone() {
        try {
            GenericData result = (GenericData) super.clone();
            Data.deepCopy(this, result);
            result.unknownFields = (Map) Data.clone(this.unknownFields);
            return result;
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException(e);
        }
    }

    public final Map<String, Object> getUnknownKeys() {
        return this.unknownFields;
    }

    public final void setUnknownKeys(Map<String, Object> unknownFields) {
        this.unknownFields = unknownFields;
    }

    public final ClassInfo getClassInfo() {
        return this.classInfo;
    }

    /* loaded from: classes.dex */
    final class EntrySet extends AbstractSet<Map.Entry<String, Object>> {
        private final DataMap.EntrySet dataEntrySet;

        EntrySet() {
            this.dataEntrySet = new DataMap(GenericData.this, GenericData.this.classInfo.ignoreCase).entrySet();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
        public final Iterator<Map.Entry<String, Object>> iterator() {
            return new EntryIterator(this.dataEntrySet);
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public final int size() {
            return GenericData.this.unknownFields.size() + this.dataEntrySet.size();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public final void clear() {
            GenericData.this.unknownFields.clear();
            this.dataEntrySet.clear();
        }
    }

    /* loaded from: classes.dex */
    final class EntryIterator implements Iterator<Map.Entry<String, Object>> {
        private final Iterator<Map.Entry<String, Object>> fieldIterator;
        private boolean startedUnknown;
        private final Iterator<Map.Entry<String, Object>> unknownIterator;

        EntryIterator(DataMap.EntrySet dataEntrySet) {
            this.fieldIterator = dataEntrySet.iterator();
            this.unknownIterator = GenericData.this.unknownFields.entrySet().iterator();
        }

        @Override // java.util.Iterator
        public final boolean hasNext() {
            return this.fieldIterator.hasNext() || this.unknownIterator.hasNext();
        }

        @Override // java.util.Iterator
        public final void remove() {
            if (this.startedUnknown) {
                this.unknownIterator.remove();
            }
            this.fieldIterator.remove();
        }

        @Override // java.util.Iterator
        public final /* bridge */ /* synthetic */ Map.Entry<String, Object> next() {
            if (!this.startedUnknown) {
                if (this.fieldIterator.hasNext()) {
                    return this.fieldIterator.next();
                }
                this.startedUnknown = true;
            }
            return this.unknownIterator.next();
        }
    }
}
