package com.google.api.client.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;

/* loaded from: classes.dex */
public final class ArrayValueMap {
    private final Object destination;
    private final Map<String, ArrayValue> keyMap = ArrayMap.create();
    private final Map<Field, ArrayValue> fieldMap = ArrayMap.create();

    /* loaded from: classes.dex */
    static class ArrayValue {
        final Class<?> componentType;
        final ArrayList<Object> values = new ArrayList<>();

        ArrayValue(Class<?> componentType) {
            this.componentType = componentType;
        }

        final Object toArray() {
            return Types.toArray(this.values, this.componentType);
        }
    }

    public ArrayValueMap(Object destination) {
        this.destination = destination;
    }

    public final void setValues() {
        for (Map.Entry<String, ArrayValue> entry : this.keyMap.entrySet()) {
            ((Map) this.destination).put(entry.getKey(), entry.getValue().toArray());
        }
        for (Map.Entry<Field, ArrayValue> entry2 : this.fieldMap.entrySet()) {
            FieldInfo.setFieldValue(entry2.getKey(), this.destination, entry2.getValue().toArray());
        }
    }

    public final void put(Field field, Class<?> arrayComponentType, Object value) {
        ArrayValue arrayValue = this.fieldMap.get(field);
        if (arrayValue == null) {
            arrayValue = new ArrayValue(arrayComponentType);
            this.fieldMap.put(field, arrayValue);
        }
        com.google.api.client.repackaged.com.google.common.base.Preconditions.checkArgument(arrayComponentType == arrayValue.componentType);
        arrayValue.values.add(value);
    }
}
