package com.crashlytics.android.internal.models;

/* loaded from: classes.dex */
public final class SignalData {
    public final String code;
    public final long faultAddress = 0;
    public final String name;

    public SignalData(String name, String code) {
        this.name = name;
        this.code = code;
    }
}
