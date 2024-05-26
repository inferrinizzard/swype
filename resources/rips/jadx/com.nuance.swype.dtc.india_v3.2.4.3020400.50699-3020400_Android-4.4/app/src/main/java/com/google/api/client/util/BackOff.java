package com.google.api.client.util;

import java.io.IOException;

/* loaded from: classes.dex */
public interface BackOff {
    public static final BackOff ZERO_BACKOFF = new BackOff() { // from class: com.google.api.client.util.BackOff.1
        @Override // com.google.api.client.util.BackOff
        public final long nextBackOffMillis() throws IOException {
            return 0L;
        }
    };
    public static final BackOff STOP_BACKOFF = new BackOff() { // from class: com.google.api.client.util.BackOff.2
        @Override // com.google.api.client.util.BackOff
        public final long nextBackOffMillis() throws IOException {
            return -1L;
        }
    };

    long nextBackOffMillis() throws IOException;
}
