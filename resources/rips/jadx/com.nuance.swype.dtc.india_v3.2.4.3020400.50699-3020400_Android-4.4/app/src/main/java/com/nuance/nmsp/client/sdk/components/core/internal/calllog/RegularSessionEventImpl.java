package com.nuance.nmsp.client.sdk.components.core.internal.calllog;

import com.nuance.nmsp.client.sdk.components.core.pdx.Sequence;

/* loaded from: classes.dex */
public class RegularSessionEventImpl extends SessionEventImpl {
    /* JADX INFO: Access modifiers changed from: package-private */
    public RegularSessionEventImpl(SessionEventImpl sessionEventImpl, String str) {
        super(str, sessionEventImpl, sessionEventImpl.a, sessionEventImpl.b);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.nmsp.client.sdk.components.core.internal.calllog.SessionEventImpl
    public Sequence buildMetaSeq() {
        return super.buildMetaSeq();
    }
}
