package com.google.api.client.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.WeakHashMap;

/* loaded from: classes.dex */
public final class ClassInfo {
    private static final Map<Class<?>, ClassInfo> CACHE = new WeakHashMap();
    private static final Map<Class<?>, ClassInfo> CACHE_IGNORE_CASE = new WeakHashMap();
    final Class<?> clazz;
    final boolean ignoreCase;
    public final IdentityHashMap<String, FieldInfo> nameToFieldInfoMap = new IdentityHashMap<>();
    final List<String> names;

    public static ClassInfo of(Class<?> underlyingClass) {
        return of(underlyingClass, false);
    }

    public static ClassInfo of(Class<?> underlyingClass, boolean ignoreCase) {
        ClassInfo classInfo;
        if (underlyingClass == null) {
            return null;
        }
        Map<Class<?>, ClassInfo> cache = ignoreCase ? CACHE_IGNORE_CASE : CACHE;
        synchronized (cache) {
            classInfo = cache.get(underlyingClass);
            if (classInfo == null) {
                classInfo = new ClassInfo(underlyingClass, ignoreCase);
                cache.put(underlyingClass, classInfo);
            }
        }
        return classInfo;
    }

    public final FieldInfo getFieldInfo(String name) {
        if (name != null) {
            if (this.ignoreCase) {
                name = name.toLowerCase();
            }
            name = name.intern();
        }
        return this.nameToFieldInfoMap.get(name);
    }

    private ClassInfo(Class<?> srcClass, boolean ignoreCase) {
        this.clazz = srcClass;
        this.ignoreCase = ignoreCase;
        Preconditions.checkArgument((ignoreCase && srcClass.isEnum()) ? false : true, "cannot ignore case on an enum: " + srcClass);
        TreeSet<String> nameSet = new TreeSet<>(new Comparator<String>() { // from class: com.google.api.client.util.ClassInfo.1
            @Override // java.util.Comparator
            public final /* bridge */ /* synthetic */ int compare(String str, String str2) {
                String str3 = str;
                String str4 = str2;
                if (str3 == str4) {
                    return 0;
                }
                if (str3 == null) {
                    return -1;
                }
                if (str4 == null) {
                    return 1;
                }
                return str3.compareTo(str4);
            }
        });
        Field[] arr$ = srcClass.getDeclaredFields();
        for (Field field : arr$) {
            FieldInfo fieldInfo = FieldInfo.of(field);
            if (fieldInfo != null) {
                String fieldName = fieldInfo.name;
                fieldName = ignoreCase ? fieldName.toLowerCase().intern() : fieldName;
                FieldInfo conflictingFieldInfo = this.nameToFieldInfoMap.get(fieldName);
                boolean z = conflictingFieldInfo == null;
                Object[] objArr = new Object[4];
                objArr[0] = ignoreCase ? "case-insensitive " : "";
                objArr[1] = fieldName;
                objArr[2] = field;
                objArr[3] = conflictingFieldInfo == null ? null : conflictingFieldInfo.field;
                Preconditions.checkArgument(z, "two fields have the same %sname <%s>: %s and %s", objArr);
                this.nameToFieldInfoMap.put(fieldName, fieldInfo);
                nameSet.add(fieldName);
            }
        }
        Class<?> superClass = srcClass.getSuperclass();
        if (superClass != null) {
            ClassInfo superClassInfo = of(superClass, ignoreCase);
            nameSet.addAll(superClassInfo.names);
            for (Map.Entry<String, FieldInfo> e : superClassInfo.nameToFieldInfoMap.entrySet()) {
                String name = e.getKey();
                if (!this.nameToFieldInfoMap.containsKey(name)) {
                    this.nameToFieldInfoMap.put(name, e.getValue());
                }
            }
        }
        this.names = nameSet.isEmpty() ? Collections.emptyList() : Collections.unmodifiableList(new ArrayList(nameSet));
    }
}
