package com.google.api.client.util;

/* loaded from: classes.dex */
public interface NanoClock {
    public static final NanoClock SYSTEM = new NanoClock() { // from class: com.google.api.client.util.NanoClock.1
        @Override // com.google.api.client.util.NanoClock
        public final long nanoTime() {
            return System.nanoTime();
        }
    };

    long nanoTime();
}
