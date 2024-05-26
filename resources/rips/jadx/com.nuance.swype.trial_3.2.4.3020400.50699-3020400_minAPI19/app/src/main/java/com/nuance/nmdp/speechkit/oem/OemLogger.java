package com.nuance.nmdp.speechkit.oem;

import android.util.Log;

/* loaded from: classes.dex */
public class OemLogger {
    public static void info(String tag, String text) {
        Log.i(tag, text);
    }

    public static void warn(String tag, String text) {
        Log.w(tag, text);
    }

    public static void error(String tag, String text) {
        Log.e(tag, text);
    }

    public static void error(String tag, String text, Throwable tr) {
        Log.e(tag, text, tr);
    }
}
