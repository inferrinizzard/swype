package com.google.api.client.util;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

/* loaded from: classes.dex */
public final class Data {
    private static final ConcurrentHashMap<Class<?>, Object> NULL_CACHE;
    public static final Boolean NULL_BOOLEAN = new Boolean(true);
    public static final String NULL_STRING = new String();
    public static final Character NULL_CHARACTER = new Character(0);
    public static final Byte NULL_BYTE = new Byte((byte) 0);
    public static final Short NULL_SHORT = new Short((short) 0);
    public static final Integer NULL_INTEGER = new Integer(0);
    public static final Float NULL_FLOAT = new Float(0.0f);
    public static final Long NULL_LONG = new Long(0);
    public static final Double NULL_DOUBLE = new Double(0.0d);
    public static final BigInteger NULL_BIG_INTEGER = new BigInteger("0");
    public static final BigDecimal NULL_BIG_DECIMAL = new BigDecimal("0");
    public static final DateTime NULL_DATE_TIME = new DateTime();

    static {
        ConcurrentHashMap<Class<?>, Object> concurrentHashMap = new ConcurrentHashMap<>();
        NULL_CACHE = concurrentHashMap;
        concurrentHashMap.put(Boolean.class, NULL_BOOLEAN);
        NULL_CACHE.put(String.class, NULL_STRING);
        NULL_CACHE.put(Character.class, NULL_CHARACTER);
        NULL_CACHE.put(Byte.class, NULL_BYTE);
        NULL_CACHE.put(Short.class, NULL_SHORT);
        NULL_CACHE.put(Integer.class, NULL_INTEGER);
        NULL_CACHE.put(Float.class, NULL_FLOAT);
        NULL_CACHE.put(Long.class, NULL_LONG);
        NULL_CACHE.put(Double.class, NULL_DOUBLE);
        NULL_CACHE.put(BigInteger.class, NULL_BIG_INTEGER);
        NULL_CACHE.put(BigDecimal.class, NULL_BIG_DECIMAL);
        NULL_CACHE.put(DateTime.class, NULL_DATE_TIME);
    }

    public static <T> T nullOf(Class<?> cls) {
        Object obj = (T) NULL_CACHE.get(cls);
        if (obj == null) {
            synchronized (NULL_CACHE) {
                obj = NULL_CACHE.get(cls);
                if (obj == null) {
                    if (cls.isArray()) {
                        int i = 0;
                        Class<?> cls2 = cls;
                        do {
                            cls2 = cls2.getComponentType();
                            i++;
                        } while (cls2.isArray());
                        obj = (T) Array.newInstance(cls2, new int[i]);
                    } else if (cls.isEnum()) {
                        FieldInfo fieldInfo = ClassInfo.of(cls).getFieldInfo(null);
                        Object[] objArr = {cls};
                        if (fieldInfo != null) {
                            obj = fieldInfo.enumValue();
                        } else {
                            throw new NullPointerException(com.google.api.client.repackaged.com.google.common.base.Preconditions.format("enum missing constant with @NullValue annotation: %s", objArr));
                        }
                    } else {
                        obj = Types.newInstance(cls);
                    }
                    NULL_CACHE.put(cls, obj);
                }
            }
        }
        return (T) obj;
    }

    public static boolean isNull(Object object) {
        return object != null && object == NULL_CACHE.get(object.getClass());
    }

    public static Map<String, Object> mapOf(Object data) {
        if (data == null || isNull(data)) {
            return Collections.emptyMap();
        }
        if (data instanceof Map) {
            return (Map) data;
        }
        return new DataMap(data, false);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static <T> T clone(T t) {
        T t2;
        if (t == 0 || isPrimitive(t.getClass())) {
            return t;
        }
        if (t instanceof GenericData) {
            return (T) ((GenericData) t).clone();
        }
        Class<?> cls = t.getClass();
        if (cls.isArray()) {
            t2 = (T) Array.newInstance(cls.getComponentType(), Array.getLength(t));
        } else if (t instanceof ArrayMap) {
            t2 = (T) ((ArrayMap) t).clone();
        } else {
            if ("java.util.Arrays$ArrayList".equals(cls.getName())) {
                Object[] array = ((List) t).toArray();
                deepCopy(array, array);
                return (T) Arrays.asList(array);
            }
            t2 = (T) Types.newInstance(cls);
        }
        deepCopy(t, t2);
        return t2;
    }

    public static void deepCopy(Object src, Object dest) {
        Class<?> srcClass = src.getClass();
        com.google.api.client.repackaged.com.google.common.base.Preconditions.checkArgument(srcClass == dest.getClass());
        if (!srcClass.isArray()) {
            if (Collection.class.isAssignableFrom(srcClass)) {
                Collection<Object> srcCollection = (Collection) src;
                if (ArrayList.class.isAssignableFrom(srcClass)) {
                    ((ArrayList) dest).ensureCapacity(srcCollection.size());
                }
                Collection<Object> destCollection = (Collection) dest;
                Iterator i$ = srcCollection.iterator();
                while (i$.hasNext()) {
                    destCollection.add(clone(i$.next()));
                }
                return;
            }
            boolean isGenericData = GenericData.class.isAssignableFrom(srcClass);
            if (isGenericData || !Map.class.isAssignableFrom(srcClass)) {
                ClassInfo classInfo = isGenericData ? ((GenericData) src).classInfo : ClassInfo.of(srcClass);
                for (String fieldName : classInfo.names) {
                    FieldInfo fieldInfo = classInfo.getFieldInfo(fieldName);
                    if (!fieldInfo.isFinal() && (!isGenericData || !fieldInfo.isPrimitive)) {
                        Object srcValue = fieldInfo.getValue(src);
                        if (srcValue != null) {
                            fieldInfo.setValue(dest, clone(srcValue));
                        }
                    }
                }
                return;
            }
            if (ArrayMap.class.isAssignableFrom(srcClass)) {
                ArrayMap<Object, Object> destMap = (ArrayMap) dest;
                ArrayMap<Object, Object> srcMap = (ArrayMap) src;
                int size = srcMap.size();
                for (int i = 0; i < size; i++) {
                    destMap.set(i, clone(srcMap.getValue(i)));
                }
                return;
            }
            Map map = (Map) dest;
            for (Map.Entry<String, Object> srcEntry : ((Map) src).entrySet()) {
                map.put(srcEntry.getKey(), clone(srcEntry.getValue()));
            }
            return;
        }
        com.google.api.client.repackaged.com.google.common.base.Preconditions.checkArgument(Array.getLength(src) == Array.getLength(dest));
        int index = 0;
        for (Object value : Types.iterableOf(src)) {
            Array.set(dest, index, clone(value));
            index++;
        }
    }

    public static boolean isPrimitive(Type type) {
        if (type instanceof WildcardType) {
            type = Types.getBound((WildcardType) type);
        }
        if (!(type instanceof Class)) {
            return false;
        }
        Class<?> typeClass = (Class) type;
        return typeClass.isPrimitive() || typeClass == Character.class || typeClass == String.class || typeClass == Integer.class || typeClass == Long.class || typeClass == Short.class || typeClass == Byte.class || typeClass == Float.class || typeClass == Double.class || typeClass == BigInteger.class || typeClass == BigDecimal.class || typeClass == DateTime.class || typeClass == Boolean.class;
    }

    public static boolean isValueOfPrimitiveType(Object fieldValue) {
        return fieldValue == null || isPrimitive(fieldValue.getClass());
    }

    public static Object parsePrimitiveValue(Type type, String stringValue) {
        Class<?> primitiveClass = type instanceof Class ? (Class) type : null;
        if (type == null || primitiveClass != null) {
            if (primitiveClass == Void.class) {
                return null;
            }
            if (stringValue != null && primitiveClass != null && !primitiveClass.isAssignableFrom(String.class)) {
                if (primitiveClass == Character.class || primitiveClass == Character.TYPE) {
                    if (stringValue.length() != 1) {
                        throw new IllegalArgumentException("expected type Character/char but got " + primitiveClass);
                    }
                    return Character.valueOf(stringValue.charAt(0));
                }
                if (primitiveClass == Boolean.class || primitiveClass == Boolean.TYPE) {
                    return Boolean.valueOf(stringValue);
                }
                if (primitiveClass == Byte.class || primitiveClass == Byte.TYPE) {
                    return Byte.valueOf(stringValue);
                }
                if (primitiveClass == Short.class || primitiveClass == Short.TYPE) {
                    return Short.valueOf(stringValue);
                }
                if (primitiveClass == Integer.class || primitiveClass == Integer.TYPE) {
                    return Integer.valueOf(stringValue);
                }
                if (primitiveClass == Long.class || primitiveClass == Long.TYPE) {
                    return Long.valueOf(stringValue);
                }
                if (primitiveClass == Float.class || primitiveClass == Float.TYPE) {
                    return Float.valueOf(stringValue);
                }
                if (primitiveClass == Double.class || primitiveClass == Double.TYPE) {
                    return Double.valueOf(stringValue);
                }
                if (primitiveClass == DateTime.class) {
                    return DateTime.parseRfc3339(stringValue);
                }
                if (primitiveClass == BigInteger.class) {
                    return new BigInteger(stringValue);
                }
                if (primitiveClass == BigDecimal.class) {
                    return new BigDecimal(stringValue);
                }
                if (primitiveClass.isEnum()) {
                    return ClassInfo.of(primitiveClass).getFieldInfo(stringValue).enumValue();
                }
            } else {
                return stringValue;
            }
        }
        throw new IllegalArgumentException("expected primitive class, but got: " + type);
    }

    public static Collection<Object> newCollectionInstance(Type type) {
        if (type instanceof WildcardType) {
            type = Types.getBound((WildcardType) type);
        }
        if (type instanceof ParameterizedType) {
            type = ((ParameterizedType) type).getRawType();
        }
        Class<?> collectionClass = type instanceof Class ? (Class) type : null;
        if (type == null || (type instanceof GenericArrayType) || (collectionClass != null && (collectionClass.isArray() || collectionClass.isAssignableFrom(ArrayList.class)))) {
            return new ArrayList();
        }
        if (collectionClass == null) {
            throw new IllegalArgumentException("unable to create new instance of type: " + type);
        }
        if (collectionClass.isAssignableFrom(HashSet.class)) {
            return new HashSet();
        }
        if (collectionClass.isAssignableFrom(TreeSet.class)) {
            return new TreeSet();
        }
        return (Collection) Types.newInstance(collectionClass);
    }

    public static Map<String, Object> newMapInstance(Class<?> mapClass) {
        if (mapClass == null || mapClass.isAssignableFrom(ArrayMap.class)) {
            return ArrayMap.create();
        }
        if (mapClass.isAssignableFrom(TreeMap.class)) {
            return new TreeMap();
        }
        return (Map) Types.newInstance(mapClass);
    }

    public static Type resolveWildcardTypeOrTypeVariable(List<Type> context, Type type) {
        if (type instanceof WildcardType) {
            type = Types.getBound((WildcardType) type);
        }
        while (type instanceof TypeVariable) {
            Type resolved = Types.resolveTypeVariable(context, (TypeVariable) type);
            if (resolved != null) {
                type = resolved;
            }
            if (type instanceof TypeVariable) {
                type = ((TypeVariable) type).getBounds()[0];
            }
        }
        return type;
    }
}
