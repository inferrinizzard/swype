package com.android.support.v4.preference;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/* loaded from: classes.dex */
public class PreferenceManagerCompat {
    private static final String TAG = PreferenceManagerCompat.class.getSimpleName();

    /* loaded from: classes.dex */
    interface OnPreferenceTreeClickListener {
        boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static PreferenceManager newInstance$65cee2cb(Activity activity) {
        try {
            Constructor<PreferenceManager> c = PreferenceManager.class.getDeclaredConstructor(Activity.class, Integer.TYPE);
            c.setAccessible(true);
            return c.newInstance(activity, 100);
        } catch (Exception e) {
            Log.w(TAG, "Couldn't call constructor PreferenceManager by reflection", e);
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void setFragment$7b6d5139() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void setOnPreferenceTreeClickListener(PreferenceManager manager, final OnPreferenceTreeClickListener listener) {
        try {
            Field onPreferenceTreeClickListener = PreferenceManager.class.getDeclaredField("mOnPreferenceTreeClickListener");
            onPreferenceTreeClickListener.setAccessible(true);
            if (listener != null) {
                Object proxy = Proxy.newProxyInstance(onPreferenceTreeClickListener.getType().getClassLoader(), new Class[]{onPreferenceTreeClickListener.getType()}, new InvocationHandler() { // from class: com.android.support.v4.preference.PreferenceManagerCompat.1
                    @Override // java.lang.reflect.InvocationHandler
                    public final Object invoke(Object proxy2, Method method, Object[] args) {
                        if (method.getName().equals("onPreferenceTreeClick")) {
                            return Boolean.valueOf(OnPreferenceTreeClickListener.this.onPreferenceTreeClick((PreferenceScreen) args[0], (Preference) args[1]));
                        }
                        return null;
                    }
                });
                onPreferenceTreeClickListener.set(manager, proxy);
            } else {
                onPreferenceTreeClickListener.set(manager, null);
            }
        } catch (Exception e) {
            Log.w(TAG, "Couldn't set PreferenceManager.mOnPreferenceTreeClickListener by reflection", e);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static PreferenceScreen inflateFromIntent(PreferenceManager manager, Intent intent, PreferenceScreen screen) {
        try {
            Method m = PreferenceManager.class.getDeclaredMethod("inflateFromIntent", Intent.class, PreferenceScreen.class);
            m.setAccessible(true);
            return (PreferenceScreen) m.invoke(manager, intent, screen);
        } catch (Exception e) {
            Log.w(TAG, "Couldn't call PreferenceManager.inflateFromIntent by reflection", e);
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static PreferenceScreen inflateFromResource(PreferenceManager manager, Activity activity, int resId, PreferenceScreen screen) {
        try {
            Method m = PreferenceManager.class.getDeclaredMethod("inflateFromResource", Context.class, Integer.TYPE, PreferenceScreen.class);
            m.setAccessible(true);
            return (PreferenceScreen) m.invoke(manager, activity, Integer.valueOf(resId), screen);
        } catch (Exception e) {
            Log.w(TAG, "Couldn't call PreferenceManager.inflateFromResource by reflection", e);
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static PreferenceScreen getPreferenceScreen(PreferenceManager manager) {
        try {
            Method m = PreferenceManager.class.getDeclaredMethod("getPreferenceScreen", new Class[0]);
            m.setAccessible(true);
            return (PreferenceScreen) m.invoke(manager, new Object[0]);
        } catch (Exception e) {
            Log.w(TAG, "Couldn't call PreferenceManager.getPreferenceScreen by reflection", e);
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void dispatchActivityResult(PreferenceManager manager, int requestCode, int resultCode, Intent data) {
        try {
            Method m = PreferenceManager.class.getDeclaredMethod("dispatchActivityResult", Integer.TYPE, Integer.TYPE, Intent.class);
            m.setAccessible(true);
            m.invoke(manager, Integer.valueOf(requestCode), Integer.valueOf(resultCode), data);
        } catch (Exception e) {
            Log.w(TAG, "Couldn't call PreferenceManager.dispatchActivityResult by reflection", e);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void dispatchActivityStop(PreferenceManager manager) {
        try {
            Method m = PreferenceManager.class.getDeclaredMethod("dispatchActivityStop", new Class[0]);
            m.setAccessible(true);
            m.invoke(manager, new Object[0]);
        } catch (Exception e) {
            Log.w(TAG, "Couldn't call PreferenceManager.dispatchActivityStop by reflection", e);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void dispatchActivityDestroy(PreferenceManager manager) {
        try {
            Method m = PreferenceManager.class.getDeclaredMethod("dispatchActivityDestroy", new Class[0]);
            m.setAccessible(true);
            m.invoke(manager, new Object[0]);
        } catch (Exception e) {
            Log.w(TAG, "Couldn't call PreferenceManager.dispatchActivityDestroy by reflection", e);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean setPreferences(PreferenceManager manager, PreferenceScreen screen) {
        try {
            Method m = PreferenceManager.class.getDeclaredMethod("setPreferences", PreferenceScreen.class);
            m.setAccessible(true);
            return ((Boolean) m.invoke(manager, screen)).booleanValue();
        } catch (Exception e) {
            Log.w(TAG, "Couldn't call PreferenceManager.setPreferences by reflection", e);
            return false;
        }
    }
}
