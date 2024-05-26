package com.google.api.client.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.WeakHashMap;

/* loaded from: classes.dex */
public final class FieldInfo {
    private static final Map<Field, FieldInfo> CACHE = new WeakHashMap();
    public final Field field;
    public final boolean isPrimitive;
    public final String name;

    public static FieldInfo of(Enum<?> enumValue) {
        try {
            FieldInfo result = of(enumValue.getClass().getField(enumValue.name()));
            Preconditions.checkArgument(result != null, "enum constant missing @Value or @NullValue annotation: %s", enumValue);
            return result;
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public static FieldInfo of(Field field) {
        String fieldName;
        if (field == null) {
            return null;
        }
        synchronized (CACHE) {
            FieldInfo fieldInfo = CACHE.get(field);
            boolean isEnumContant = field.isEnumConstant();
            if (fieldInfo == null && (isEnumContant || !Modifier.isStatic(field.getModifiers()))) {
                if (isEnumContant) {
                    Value value = (Value) field.getAnnotation(Value.class);
                    if (value != null) {
                        fieldName = value.value();
                    } else {
                        if (((NullValue) field.getAnnotation(NullValue.class)) == null) {
                            return null;
                        }
                        fieldName = null;
                    }
                } else {
                    Key key = (Key) field.getAnnotation(Key.class);
                    if (key == null) {
                        return null;
                    }
                    fieldName = key.value();
                    field.setAccessible(true);
                }
                if ("##default".equals(fieldName)) {
                    fieldName = field.getName();
                }
                fieldInfo = new FieldInfo(field, fieldName);
                CACHE.put(field, fieldInfo);
            }
            return fieldInfo;
        }
    }

    private FieldInfo(Field field, String name) {
        this.field = field;
        this.name = name == null ? null : name.intern();
        this.isPrimitive = Data.isPrimitive(this.field.getType());
    }

    public final boolean isFinal() {
        return Modifier.isFinal(this.field.getModifiers());
    }

    public final Object getValue(Object obj) {
        return getFieldValue(this.field, obj);
    }

    public final void setValue(Object obj, Object value) {
        setFieldValue(this.field, obj, value);
    }

    public final <T extends Enum<T>> T enumValue() {
        return (T) Enum.valueOf(this.field.getDeclaringClass(), this.field.getName());
    }

    private static Object getFieldValue(Field field, Object obj) {
        try {
            return field.get(obj);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static void setFieldValue(Field field, Object obj, Object value) {
        if (Modifier.isFinal(field.getModifiers())) {
            Object finalValue = getFieldValue(field, obj);
            if (value == null) {
                if (finalValue == null) {
                    return;
                }
            } else if (value.equals(finalValue)) {
                return;
            }
            throw new IllegalArgumentException("expected final value <" + finalValue + "> but was <" + value + "> on " + field.getName() + " field in " + obj.getClass().getName());
        }
        try {
            field.set(obj, value);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        } catch (SecurityException e2) {
            throw new IllegalArgumentException(e2);
        }
    }
}
