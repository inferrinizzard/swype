package com.nuance.android.compat;

import android.content.SharedPreferences;
import java.lang.reflect.Method;

/* loaded from: classes.dex */
public class SharedPreferencesEditorCompat {
    private static final Method Editor_apply = CompatUtil.getMethod((Class<?>) SharedPreferences.Editor.class, "apply", (Class<?>[]) new Class[0]);

    private SharedPreferencesEditorCompat() {
    }

    public static void apply(SharedPreferences.Editor editor) {
        if (Editor_apply != null) {
            CompatUtil.invoke(Editor_apply, editor, new Object[0]);
        } else {
            editor.commit();
        }
    }
}
