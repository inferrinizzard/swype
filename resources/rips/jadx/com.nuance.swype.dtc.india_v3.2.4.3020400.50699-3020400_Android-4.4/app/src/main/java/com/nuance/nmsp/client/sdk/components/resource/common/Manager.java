package com.nuance.nmsp.client.sdk.components.resource.common;

import com.nuance.nmsp.client.sdk.components.core.calllog.SessionEventBuilder;

/* loaded from: classes.dex */
public interface Manager {
    void flushCallLogData();

    String getCalllogRootParentId();

    SessionEventBuilder logAppEvent(String str);

    void shutdown();
}
