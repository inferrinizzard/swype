package com.nuance.connect.compat;

import com.nuance.connect.util.Logger;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/* loaded from: classes.dex */
public class CompatUtil {
    private static final Logger.Log log = Logger.getLog(Logger.LoggerType.DEVELOPER);

    private CompatUtil() {
    }

    public static Boolean doesClassExist(String str) {
        return Boolean.valueOf(getClass(str) != null);
    }

    public static Class<?> getClass(String str) {
        try {
            return Class.forName(str);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public static Constructor<?> getConstructor(Class<?> cls, Class<?>... clsArr) {
        if (cls == null || clsArr == null) {
            return null;
        }
        try {
            return cls.getConstructor(clsArr);
        } catch (NoSuchMethodException e) {
            return null;
        } catch (SecurityException e2) {
            return null;
        }
    }

    public static Constructor<?> getConstructor(String str, Class<?>... clsArr) {
        try {
            return Class.forName(str).getConstructor(clsArr);
        } catch (Exception e) {
            log.e((Object) ("Failed to find construtor for class: " + str), (Throwable) e);
            return null;
        }
    }

    public static Field getDeclaredField(Class cls, String str) {
        try {
            return cls.getDeclaredField(str);
        } catch (NoSuchFieldException e) {
            return null;
        }
    }

    public static Field getDeclaredFieldIgnoreAccess(Class<?> cls, String str) {
        Field declaredField = getDeclaredField(cls, str);
        if (declaredField != null) {
            try {
                declaredField.setAccessible(true);
                return declaredField;
            } catch (SecurityException e) {
                log.e((Object) String.format("Can't set field accessible: %s.%s", cls.toString(), str), (Throwable) e);
            }
        } else {
            log.e(String.format("Field is null: %s.%s", cls.toString(), str));
        }
        return null;
    }

    public static Object getEnum(String str, String str2) {
        Class<?> cls = getClass(str);
        if (cls != null && cls.isEnum()) {
            return getStaticFieldValue(cls, str2);
        }
        log.e("Not an enum: " + str);
        return null;
    }

    public static Method getMethod(Class<?> cls, String str, Class... clsArr) {
        try {
            return cls.getMethod(str, clsArr);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    public static Method getMethod(String str, String str2, Class<?>... clsArr) {
        try {
            return Class.forName(str).getMethod(str2, clsArr);
        } catch (Exception e) {
            return null;
        }
    }

    public static Object getStaticFieldValue(Class<?> cls, String str) {
        try {
            return cls.getDeclaredField(str).get(null);
        } catch (Exception e) {
            return null;
        }
    }

    public static Object getStaticFieldValue(String str, String str2) {
        try {
            return Class.forName(str).getDeclaredField(str2).get(null);
        } catch (Exception e) {
            return null;
        }
    }

    public static Object invoke(Method method, Object obj, Object... objArr) {
        try {
            return method.invoke(obj, objArr);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        } catch (InvocationTargetException e2) {
            throw unpackInvocationTargetException(e2);
        }
    }

    public static Object newInstance(String str) {
        try {
            return Class.forName(str).newInstance();
        } catch (Exception e) {
            log.e((Object) ("Failed to instantiate class: " + str), (Throwable) e);
            return null;
        }
    }

    public static Object newInstance(Constructor<?> constructor, Object... objArr) {
        try {
            return constructor.newInstance(objArr);
        } catch (Exception e) {
            log.e((Object) ("Failed to instantiate class: " + constructor.getName()), (Throwable) e);
            return null;
        }
    }

    public static void setFieldFloatIgnoreAccess(Object obj, String str, float f) {
        Field declaredFieldIgnoreAccess = getDeclaredFieldIgnoreAccess(obj.getClass(), str);
        if (declaredFieldIgnoreAccess == null) {
            log.w("setFieldFloatIgnoreAccess(): field is null: " + str);
            return;
        }
        try {
            declaredFieldIgnoreAccess.setFloat(obj, f);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e2) {
            e2.printStackTrace();
        }
    }

    public static void setFieldIntIgnoreAccess(Object obj, String str, int i) {
        Field declaredFieldIgnoreAccess = getDeclaredFieldIgnoreAccess(obj.getClass(), str);
        if (declaredFieldIgnoreAccess == null) {
            log.w("setFieldIntIgnoreAccess(): field is null: " + str);
            return;
        }
        try {
            declaredFieldIgnoreAccess.setInt(obj, i);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e2) {
            e2.printStackTrace();
        }
    }

    public static RuntimeException unpackInvocationTargetException(InvocationTargetException invocationTargetException) {
        Throwable cause = invocationTargetException.getCause();
        if (cause instanceof RuntimeException) {
            throw ((RuntimeException) cause);
        }
        if (cause instanceof Error) {
            throw ((Error) cause);
        }
        return new RuntimeException(invocationTargetException);
    }
}
