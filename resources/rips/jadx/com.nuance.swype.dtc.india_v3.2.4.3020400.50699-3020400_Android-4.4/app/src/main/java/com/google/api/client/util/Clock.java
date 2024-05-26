package com.google.api.client.util;

/* loaded from: classes.dex */
public interface Clock {
    public static final Clock SYSTEM = new Clock() { // from class: com.google.api.client.util.Clock.1
        @Override // com.google.api.client.util.Clock
        public final long currentTimeMillis() {
            return System.currentTimeMillis();
        }
    };

    long currentTimeMillis();
}
