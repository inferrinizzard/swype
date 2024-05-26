package com.nuance.nmsp.client.sdk.components.core.internal.calllog;

import com.nuance.nmsp.client.sdk.components.core.calllog.SessionEvent;
import com.nuance.nmsp.client.sdk.components.core.calllog.SessionEventAlreadyCommittedException;
import com.nuance.nmsp.client.sdk.components.core.calllog.SessionEventBuilder;
import java.util.Hashtable;

/* loaded from: classes.dex */
public class SessionEventBuilderImpl implements SessionEventBuilder {
    private final SessionEventImpl a;
    private final Hashtable b = new Hashtable();
    private boolean c = false;

    /* loaded from: classes.dex */
    public interface GenSeqIdCallback {
        void seqIdGenerated(SessionEventImpl sessionEventImpl, String str);
    }

    private SessionEventBuilderImpl(SessionEventImpl sessionEventImpl, GenSeqIdCallback genSeqIdCallback) {
        this.a = sessionEventImpl;
        this.a.getCalllog().genSeqId(sessionEventImpl, genSeqIdCallback);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static SessionEventBuilderImpl a(RemoteSessionEventImpl remoteSessionEventImpl, String str) {
        if (str == null) {
            throw new IllegalArgumentException("eventName is null.");
        }
        return new SessionEventBuilderImpl(new RootSessionEventImpl(remoteSessionEventImpl, str), null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static SessionEventBuilderImpl a(SessionEventImpl sessionEventImpl, String str) {
        if (str == null) {
            throw new IllegalArgumentException("eventName is null.");
        }
        return new SessionEventBuilderImpl(new RegularSessionEventImpl(sessionEventImpl, str), null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static SessionEventBuilderImpl a(String str, String str2, String str3, int i, CalllogImpl calllogImpl, String str4) {
        if (str4 == null) {
            throw new IllegalArgumentException("eventName is null.");
        }
        return new SessionEventBuilderImpl(new RootSessionEventImpl(str, str2, str3, i, calllogImpl, str4), null);
    }

    public static SessionEventBuilderImpl createRemoteSessionEventBuilder(SessionEventImpl sessionEventImpl, String str, GenSeqIdCallback genSeqIdCallback) {
        if (str == null) {
            throw new IllegalArgumentException("eventName is null.");
        }
        return new SessionEventBuilderImpl(new RemoteSessionEventImpl(sessionEventImpl, str), genSeqIdCallback);
    }

    public SessionEvent cancel() {
        if (this.a instanceof RemoteSessionEventImpl) {
            ((RemoteSessionEventImpl) this.a)._cancelled = true;
        }
        if (!this.c) {
            this.a.done(this.b, null);
            this.c = true;
        }
        return this.a;
    }

    @Override // com.nuance.nmsp.client.sdk.components.core.calllog.SessionEventBuilder
    public SessionEvent commit() {
        return commit(null);
    }

    @Override // com.nuance.nmsp.client.sdk.components.core.calllog.SessionEventBuilder
    public SessionEvent commit(SessionEvent.SessionEventCommittedListener sessionEventCommittedListener) {
        if (!this.c) {
            this.a.done(this.b, sessionEventCommittedListener);
            this.c = true;
        }
        return this.a;
    }

    @Override // com.nuance.nmsp.client.sdk.components.core.calllog.SessionEventBuilder
    public SessionEventBuilder putBinary(String str, byte[] bArr) throws SessionEventAlreadyCommittedException {
        if (this.c) {
            throw new SessionEventAlreadyCommittedException("SessionEvent is already committed.");
        }
        this.b.put(str, bArr);
        return this;
    }

    @Override // com.nuance.nmsp.client.sdk.components.core.calllog.SessionEventBuilder
    public SessionEventBuilder putBoolean(String str, boolean z) throws SessionEventAlreadyCommittedException {
        if (this.c) {
            throw new SessionEventAlreadyCommittedException("SessionEvent is already committed.");
        }
        this.b.put(str, new Boolean(z));
        return this;
    }

    @Override // com.nuance.nmsp.client.sdk.components.core.calllog.SessionEventBuilder
    public SessionEventBuilder putEventReference(String str, SessionEvent sessionEvent) throws SessionEventAlreadyCommittedException {
        if (this.c) {
            throw new SessionEventAlreadyCommittedException("SessionEvent is already committed.");
        }
        this.b.put(str, sessionEvent);
        return this;
    }

    @Override // com.nuance.nmsp.client.sdk.components.core.calllog.SessionEventBuilder
    public SessionEventBuilder putFloat(String str, double d) throws SessionEventAlreadyCommittedException {
        if (this.c) {
            throw new SessionEventAlreadyCommittedException("SessionEvent is already committed.");
        }
        this.b.put(str, new Double(d));
        return this;
    }

    @Override // com.nuance.nmsp.client.sdk.components.core.calllog.SessionEventBuilder
    public SessionEventBuilder putInteger(String str, int i) throws SessionEventAlreadyCommittedException {
        if (this.c) {
            throw new SessionEventAlreadyCommittedException("SessionEvent is already committed.");
        }
        this.b.put(str, new Integer(i));
        return this;
    }

    @Override // com.nuance.nmsp.client.sdk.components.core.calllog.SessionEventBuilder
    public SessionEventBuilder putLong(String str, long j) throws SessionEventAlreadyCommittedException {
        if (this.c) {
            throw new SessionEventAlreadyCommittedException("SessionEvent is already committed.");
        }
        this.b.put(str, new Long(j));
        return this;
    }

    @Override // com.nuance.nmsp.client.sdk.components.core.calllog.SessionEventBuilder
    public SessionEventBuilder putString(String str, String str2) throws SessionEventAlreadyCommittedException {
        if (this.c) {
            throw new SessionEventAlreadyCommittedException("SessionEvent is already committed.");
        }
        this.b.put(str, str2);
        return this;
    }
}
