package com.nuance.android.compat;

import android.content.Context;
import com.nuance.swype.util.LogManager;
import java.lang.reflect.Method;

/* loaded from: classes.dex */
public class SystemPropertiesProxy {
    private static final LogManager.Log log = LogManager.getLog("SystemPropertiesProxy");
    Method SystemProperties_getBoolean;

    public SystemPropertiesProxy(Context context) {
        ClassLoader cl = context.getClassLoader();
        try {
            Class<?> class_SystemProperties = cl.loadClass("android.os.SystemProperties");
            this.SystemProperties_getBoolean = CompatUtil.getMethod(class_SystemProperties, "getBoolean", (Class<?>[]) new Class[]{String.class, Boolean.TYPE});
            if (this.SystemProperties_getBoolean == null) {
                log.w("method 'getBoolean' not found");
            }
        } catch (ClassNotFoundException ex) {
            log.d(ex);
        }
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return this.SystemProperties_getBoolean != null ? ((Boolean) CompatUtil.invoke(this.SystemProperties_getBoolean, null, key, Boolean.valueOf(defaultValue))).booleanValue() : defaultValue;
    }
}
