package com.crashlytics.android.internal.models;

/* loaded from: classes.dex */
public final class ThreadData {
    public final FrameData[] frames;
    public final int importance;

    /* loaded from: classes.dex */
    public static final class FrameData {
        public final long address;
        public final String file;
        public final int importance;
        public final long offset;
        public final String symbol;
    }
}
