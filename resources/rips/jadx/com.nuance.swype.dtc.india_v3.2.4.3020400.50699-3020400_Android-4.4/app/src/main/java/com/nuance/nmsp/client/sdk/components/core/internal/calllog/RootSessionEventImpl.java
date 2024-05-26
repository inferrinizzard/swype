package com.nuance.nmsp.client.sdk.components.core.internal.calllog;

import com.nuance.nmsp.client.sdk.components.core.calllog.SessionEvent;
import com.nuance.nmsp.client.sdk.components.core.pdx.Sequence;

/* loaded from: classes.dex */
public class RootSessionEventImpl extends SessionEventImpl {
    protected SessionEvent _appSessionEvent;
    private String d;
    private int e;
    private RemoteSessionEventImpl f;
    private String g;
    private String h;
    private int i;

    /* JADX INFO: Access modifiers changed from: package-private */
    public RootSessionEventImpl(RemoteSessionEventImpl remoteSessionEventImpl, String str) {
        super(str, null, null, remoteSessionEventImpl.b);
        this.d = null;
        this.e = 1;
        this.g = remoteSessionEventImpl.a.getApplicationName();
        this.h = remoteSessionEventImpl.a.getSchemaVersion();
        this.i = remoteSessionEventImpl.a.getRetentionDays();
        this._appSessionEvent = null;
        this.a = this;
        this.f = remoteSessionEventImpl;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public RootSessionEventImpl(String str, String str2, String str3, int i, CalllogImpl calllogImpl, String str4) {
        super(str4, null, null, calllogImpl);
        this.d = null;
        this.e = 1;
        this.g = str2;
        this.h = str3;
        this.i = i;
        this._appSessionEvent = null;
        this.d = str;
        this.a = this;
    }

    @Override // com.nuance.nmsp.client.sdk.components.core.internal.calllog.SessionEventImpl
    protected Sequence buildMetaSeq() {
        Sequence buildMetaSeq = super.buildMetaSeq();
        addEntryToSequence(buildMetaSeq, SessionEventImpl.NMSP_CALLLOG_META_APPLICATION_NAME, this.g);
        addEntryToSequence(buildMetaSeq, SessionEventImpl.NMSP_CALLLOG_META_SCHEMA_VERSION, this.h);
        addEntryToSequence(buildMetaSeq, SessionEventImpl.NMSP_CALLLOG_META_RETENTION_DAYS, new Integer(this.i));
        return buildMetaSeq;
    }

    @Override // com.nuance.nmsp.client.sdk.components.core.internal.calllog.SessionEventImpl
    protected void genSeqId() {
        super.genSeqId();
        if (this.f != null) {
            this.d = this.f.a();
        }
    }

    public String getApplicationName() {
        return this.g;
    }

    public int getRetentionDays() {
        return this.i;
    }

    public String getRootParentId() {
        return this.d;
    }

    public String getSchemaVersion() {
        return this.h;
    }

    public int getSequenceId() {
        int i = this.e;
        this.e = i + 1;
        return i;
    }
}
