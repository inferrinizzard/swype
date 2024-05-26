package com.nuance.nmsp.client.sdk.components.core.calllog;

import com.nuance.nmsp.client.sdk.components.core.calllog.SessionEvent;

/* loaded from: classes.dex */
public interface SessionEventBuilder {
    SessionEvent commit();

    SessionEvent commit(SessionEvent.SessionEventCommittedListener sessionEventCommittedListener);

    SessionEventBuilder putBinary(String str, byte[] bArr) throws SessionEventAlreadyCommittedException;

    SessionEventBuilder putBoolean(String str, boolean z) throws SessionEventAlreadyCommittedException;

    SessionEventBuilder putEventReference(String str, SessionEvent sessionEvent) throws SessionEventAlreadyCommittedException;

    SessionEventBuilder putFloat(String str, double d) throws SessionEventAlreadyCommittedException;

    SessionEventBuilder putInteger(String str, int i) throws SessionEventAlreadyCommittedException;

    SessionEventBuilder putLong(String str, long j) throws SessionEventAlreadyCommittedException;

    SessionEventBuilder putString(String str, String str2) throws SessionEventAlreadyCommittedException;
}
