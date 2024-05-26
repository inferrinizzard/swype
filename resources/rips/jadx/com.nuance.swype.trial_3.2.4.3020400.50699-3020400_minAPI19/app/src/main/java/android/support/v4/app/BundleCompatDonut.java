package android.support.v4.app;

import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/* loaded from: classes.dex */
public final class BundleCompatDonut {
    private static Method sGetIBinderMethod;
    private static boolean sGetIBinderMethodFetched;
    public static Method sPutIBinderMethod;
    public static boolean sPutIBinderMethodFetched;

    public static IBinder getBinder(Bundle bundle, String key) {
        if (!sGetIBinderMethodFetched) {
            try {
                Method method = Bundle.class.getMethod("getIBinder", String.class);
                sGetIBinderMethod = method;
                method.setAccessible(true);
            } catch (NoSuchMethodException e) {
                Log.i("BundleCompatDonut", "Failed to retrieve getIBinder method", e);
            }
            sGetIBinderMethodFetched = true;
        }
        if (sGetIBinderMethod != null) {
            try {
                return (IBinder) sGetIBinderMethod.invoke(bundle, key);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e2) {
                Log.i("BundleCompatDonut", "Failed to invoke getIBinder via reflection", e2);
                sGetIBinderMethod = null;
            }
        }
        return null;
    }
}
