package com.nuance.nmdp.speechkit.util;

import com.nuance.nmdp.speechkit.oem.OemLogger;

/* loaded from: classes.dex */
public final class Logger {
    private static final String LIB_NAME = "SpeechKit";

    public static void info(Object source, String text) {
        OemLogger.info(LIB_NAME, appendToName(source, text));
    }

    public static void warn(Object source, String text) {
        OemLogger.warn(LIB_NAME, appendToName(source, text));
    }

    public static void error(Object source, String text) {
        OemLogger.error(LIB_NAME, appendToName(source, text));
    }

    public static void error(Object source, String text, Throwable tr) {
        OemLogger.error(LIB_NAME, appendToName(source, text), tr);
    }

    private static String appendToName(Object o, String text) {
        String name;
        int lastPeriod;
        if (!Config.OBFUSCATED && o != null && (name = o.getClass().getName()) != null && name.length() != 0) {
            int lastPeriod2 = name.lastIndexOf(46);
            if (lastPeriod2 >= 0 && (lastPeriod = lastPeriod2 + 1) < name.length()) {
                name = name.substring(lastPeriod);
            }
            return name + ": " + text;
        }
        return text;
    }
}
