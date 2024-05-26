package io.fabric.sdk.android.services.common;

/* loaded from: classes.dex */
public abstract class Crash {
    public final String sessionId;

    public Crash(String sessionId) {
        this.sessionId = sessionId;
    }

    /* loaded from: classes.dex */
    public static class FatalException extends Crash {
        public FatalException(String sessionId) {
            super(sessionId);
        }
    }
}
