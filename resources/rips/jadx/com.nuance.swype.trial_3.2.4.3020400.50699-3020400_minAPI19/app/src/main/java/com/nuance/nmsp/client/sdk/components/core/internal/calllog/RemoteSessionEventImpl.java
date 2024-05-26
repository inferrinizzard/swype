package com.nuance.nmsp.client.sdk.components.core.internal.calllog;

import com.nuance.nmsp.client.sdk.components.core.calllog.SessionEventBuilder;
import com.nuance.nmsp.client.sdk.components.core.pdx.Sequence;

/* loaded from: classes.dex */
public class RemoteSessionEventImpl extends RegularSessionEventImpl {
    protected boolean _cancelled;
    private String d;

    /* JADX INFO: Access modifiers changed from: package-private */
    public RemoteSessionEventImpl(SessionEventImpl sessionEventImpl, String str) {
        super(sessionEventImpl, str);
        this._cancelled = false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final String a() {
        return this.d;
    }

    @Override // com.nuance.nmsp.client.sdk.components.core.internal.calllog.RegularSessionEventImpl, com.nuance.nmsp.client.sdk.components.core.internal.calllog.SessionEventImpl
    protected Sequence buildMetaSeq() {
        Sequence buildMetaSeq = super.buildMetaSeq();
        addEntryToSequence(buildMetaSeq, SessionEventImpl.NMSP_CALLLOG_META_REFERENCE_ID, this.d + ".1");
        if (this._cancelled) {
            addEntryToSequence(buildMetaSeq, SessionEventImpl.NMSP_CALLLOG_META_CANCEL, new Boolean(true));
        }
        return buildMetaSeq;
    }

    @Override // com.nuance.nmsp.client.sdk.components.core.internal.calllog.SessionEventImpl
    protected void genSeqId() {
        super.genSeqId();
        this.d = this.a.getRootParentId() + "." + this.c;
    }

    public SessionEventBuilder startNewEventBuilder(String str) {
        return SessionEventBuilderImpl.a(this, str);
    }
}
