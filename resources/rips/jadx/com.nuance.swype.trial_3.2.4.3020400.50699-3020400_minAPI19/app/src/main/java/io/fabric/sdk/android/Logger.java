package io.fabric.sdk.android;

/* loaded from: classes.dex */
public interface Logger {
    void e(String str, String str2);

    void e(String str, String str2, Throwable th);

    void i(String str, String str2);

    boolean isLoggable$505cff18(int i);

    void log$6ef37c42(String str, String str2);

    void w(String str, String str2);

    void w(String str, String str2, Throwable th);
}
