package io.fabric.sdk.android;

import android.util.Log;

/* loaded from: classes.dex */
public final class DefaultLogger implements Logger {
    private int logLevel = 3;

    public DefaultLogger() {
    }

    public DefaultLogger(byte b) {
    }

    @Override // io.fabric.sdk.android.Logger
    public final boolean isLoggable$505cff18(int level) {
        return this.logLevel <= level;
    }

    @Override // io.fabric.sdk.android.Logger
    public final void w(String tag, String text, Throwable throwable) {
        if (isLoggable$505cff18(5)) {
            Log.w(tag, text, throwable);
        }
    }

    @Override // io.fabric.sdk.android.Logger
    public final void e(String tag, String text, Throwable throwable) {
        if (isLoggable$505cff18(6)) {
            Log.e(tag, text, throwable);
        }
    }

    @Override // io.fabric.sdk.android.Logger
    public final void w(String tag, String text) {
        w(tag, text, null);
    }

    @Override // io.fabric.sdk.android.Logger
    public final void e(String tag, String text) {
        e(tag, text, null);
    }

    @Override // io.fabric.sdk.android.Logger
    public final void i(String tag, String text) {
        if (!isLoggable$505cff18(4)) {
            return;
        }
        Log.i(tag, text, null);
    }

    @Override // io.fabric.sdk.android.Logger
    public final void log$6ef37c42(String tag, String msg) {
        if (!isLoggable$505cff18(4)) {
            return;
        }
        Log.println(4, tag, msg);
    }
}
