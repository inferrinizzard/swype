package com.nuance.nmsp.client.sdk.components.resource.nmas;

import com.nuance.nmsp.client.sdk.components.general.TransactionAlreadyFinishedException;
import com.nuance.nmsp.client.sdk.components.general.TransactionExpiredException;

/* loaded from: classes.dex */
public interface Command {
    public static final CompletionCause ABORT_KEY_BACK;
    public static final CompletionCause ABORT_KEY_END;
    public static final CompletionCause ABORT_NEW;
    public static final CompletionCause PREEMPTED;
    public static final CompletionCause STOPPED_TOO_SOON;

    /* loaded from: classes.dex */
    public static class CompletionCause {
        private String a;

        private CompletionCause(String str) {
            this.a = str;
        }

        /* synthetic */ CompletionCause(String str, byte b) {
            this(str);
        }

        public String toString() {
            return this.a;
        }
    }

    static {
        byte b = 0;
        ABORT_KEY_END = new CompletionCause("ABORT_END", b);
        ABORT_KEY_BACK = new CompletionCause("ABORT_BACK", b);
        ABORT_NEW = new CompletionCause("ABORT_NEW", b);
        STOPPED_TOO_SOON = new CompletionCause("STOPPED_TOO_SOON", b);
        PREEMPTED = new CompletionCause("PREEMPTED", b);
    }

    void end() throws TransactionAlreadyFinishedException, TransactionExpiredException;

    boolean isNetworkHealthy();

    void sendEnrollmentAudio(byte[] bArr) throws TransactionAlreadyFinishedException, TransactionExpiredException;

    void sendParam(Parameter parameter) throws TransactionAlreadyFinishedException, TransactionExpiredException;

    void setLog(CompletionCause completionCause);
}
