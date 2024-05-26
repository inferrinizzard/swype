package com.nuance.android.compat;

import com.nuance.swype.util.LogManager;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/* loaded from: classes.dex */
public class CompatUtil {
    protected static final LogManager.Log log = LogManager.getLog("CompatUtil");

    private CompatUtil() {
    }

    public static Constructor<?> getConstructor(Class<?> targetClass, Class<?>... types) {
        if (targetClass == null || types == null) {
            return null;
        }
        try {
            return targetClass.getConstructor(types);
        } catch (NoSuchMethodException e) {
            return null;
        } catch (SecurityException e2) {
            return null;
        }
    }

    public static Field getDeclaredField(Class<?> clazz, String fieldName) {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            log.e(String.format("No such field: %s.%s", clazz.toString(), fieldName), e);
            return null;
        }
    }

    public static Field getDeclaredFieldIgnoreAccess(Class<?> clazz, String fieldName) {
        Field field = getDeclaredField(clazz, fieldName);
        if (field != null) {
            try {
                field.setAccessible(true);
                return field;
            } catch (SecurityException e) {
                log.e(String.format("Can't set field accessible: %s.%s", clazz.toString(), fieldName), e);
            }
        } else {
            log.e(String.format("Field is null: %s.%s", clazz.toString(), fieldName));
        }
        return null;
    }

    public static Object getStaticFieldValue(String className, String fieldName) {
        try {
            return Class.forName(className).getDeclaredField(fieldName).get(null);
        } catch (Exception e) {
            return null;
        }
    }

    public static Object getStaticFieldValue(Class<?> clazz, String fieldName) {
        try {
            return clazz.getDeclaredField(fieldName).get(null);
        } catch (Exception e) {
            return null;
        }
    }

    public static Method getMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        if (clazz == null) {
            log.e(String.format("Failed to get method %s (class is null) ", methodName));
            return null;
        }
        try {
            return clazz.getMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    public static Method getMethod(String className, String methodName, Class<?>... parameterTypes) {
        try {
            return Class.forName(className).getMethod(methodName, parameterTypes);
        } catch (Exception e) {
            return null;
        }
    }

    public static Constructor<?> getConstructor(String className, Class<?>... parameterTypes) {
        try {
            return Class.forName(className).getConstructor(parameterTypes);
        } catch (Exception e) {
            log.e("Failed to find construtor for class: " + className, e);
            return null;
        }
    }

    public static Object getEnum(String classNameEnum, String enumName) {
        Class<?> clazz = getClass(classNameEnum);
        if (clazz != null && clazz.isEnum()) {
            return getStaticFieldValue(clazz, enumName);
        }
        log.e("Not an enum: " + classNameEnum);
        return null;
    }

    public static Class<?> getClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public static Object invoke(Method method, Object obj, Object... args) {
        try {
            return method.invoke(obj, args);
        } catch (IllegalAccessException ie) {
            throw new IllegalStateException(ie);
        } catch (InvocationTargetException e) {
            throw unpackInvocationTargetException(e);
        }
    }

    public static Object newInstance(Constructor<?> constructor, Object... args) {
        try {
            return constructor.newInstance(args);
        } catch (Exception e) {
            log.e("Failed to instantiate class: " + constructor.getName(), e);
            return null;
        }
    }

    public static Object newInstance(String className) {
        try {
            return Class.forName(className).newInstance();
        } catch (Exception e) {
            log.e("Failed to instantiate class: " + className, e);
            return null;
        }
    }

    public static RuntimeException unpackInvocationTargetException(InvocationTargetException ite) {
        Throwable cause = ite.getCause();
        if (cause instanceof RuntimeException) {
            throw ((RuntimeException) cause);
        }
        if (cause instanceof Error) {
            throw ((Error) cause);
        }
        return new RuntimeException(ite);
    }

    public static Boolean doesClassExist(String className) {
        return Boolean.valueOf(getClass(className) != null);
    }

    public static void setFieldFloatIgnoreAccess(Object ob, String name, float val) {
        Field field = getDeclaredFieldIgnoreAccess(ob.getClass(), name);
        if (field != null) {
            try {
                field.setFloat(ob, val);
                return;
            } catch (IllegalAccessException e) {
                return;
            } catch (IllegalArgumentException e2) {
                return;
            }
        }
        log.w("setFieldFloatIgnoreAccess(): field is null: " + name);
    }

    public static void setFieldIntIgnoreAccess(Object ob, String name, int val) {
        Field field = getDeclaredFieldIgnoreAccess(ob.getClass(), name);
        if (field != null) {
            try {
                field.setInt(ob, val);
                return;
            } catch (IllegalAccessException e) {
                return;
            } catch (IllegalArgumentException e2) {
                return;
            }
        }
        log.w("setFieldIntIgnoreAccess(): field is null: " + name);
    }
}
