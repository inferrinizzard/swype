package com.nuance.nmdp.speechkit.util;

/* loaded from: classes.dex */
public final class Config {
    public static final boolean OBFUSCATED;

    static {
        OBFUSCATED = !Config.class.getName().endsWith("Config");
    }

    public static String getClientVersion() {
        return "NuSwypeSpeechKit";
    }

    public static String getNmdpVersion() {
        return "1.6.3.0B3 r19007";
    }
}
