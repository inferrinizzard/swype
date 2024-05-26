package com.nuance.nmsp.client.sdk.components.core.internal.calllog;

import com.nuance.nmsp.client.sdk.common.oem.api.LogFactory;
import com.nuance.nmsp.client.sdk.components.core.calllog.SessionEvent;
import com.nuance.nmsp.client.sdk.components.core.calllog.SessionEventBuilder;
import com.nuance.nmsp.client.sdk.components.core.internal.pdx.PDXDictionary;
import com.nuance.nmsp.client.sdk.components.core.internal.pdx.PDXSequence;
import com.nuance.nmsp.client.sdk.components.core.pdx.Sequence;
import java.util.Hashtable;

/* loaded from: classes.dex */
public class SessionEventImpl implements SessionEvent {
    public static final String NMSP_CALLLOG_META_APPLICATION_NAME = "Application";
    public static final String NMSP_CALLLOG_META_CANCEL = "Cancel";
    public static final String NMSP_CALLLOG_META_CHANNEL = "Channel";
    public static final String NMSP_CALLLOG_META_COMPLETED_TIMESTAMP = "CompletedTimestamp";
    public static final String NMSP_CALLLOG_META_HOSTNAME = "Hostname";
    public static final String NMSP_CALLLOG_META_MERGED_NAME = "Merged";
    public static final String NMSP_CALLLOG_META_NAME = "Name";
    public static final String NMSP_CALLLOG_META_PARENT_ID = "ParentSeqId";
    public static final String NMSP_CALLLOG_META_REFERENCE_ID = "RefId";
    public static final String NMSP_CALLLOG_META_RETENTION_DAYS = "RetentionDays";
    public static final String NMSP_CALLLOG_META_ROOT_PARENT_ID = "RootParentId";
    public static final String NMSP_CALLLOG_META_SCHEMA_VERSION = "SchemaVersion";
    public static final String NMSP_CALLLOG_META_SEQUENCE_ID = "SeqId";
    public static final String NMSP_CALLLOG_META_TIMESTAMP = "Timestamp";
    private static final LogFactory.Log d = LogFactory.getLog(CalllogImpl.class);
    protected Hashtable _table;
    RootSessionEventImpl a;
    CalllogImpl b;
    int c;
    private SessionEventImpl e;
    private String f;
    private long g;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SessionEventImpl(String str, SessionEventImpl sessionEventImpl, RootSessionEventImpl rootSessionEventImpl, CalllogImpl calllogImpl) {
        this.f = str;
        this.e = sessionEventImpl;
        this.a = rootSessionEventImpl;
        this.b = calllogImpl;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean addEntryToSequence(Sequence sequence, String str, Object obj) {
        PDXDictionary pDXDictionary = new PDXDictionary();
        String str2 = obj instanceof byte[] ? "bin" : obj instanceof Boolean ? "bool" : obj instanceof Double ? "float" : obj instanceof Integer ? "int" : obj instanceof Long ? "long" : obj instanceof String ? "str" : obj instanceof SessionEvent ? "str" : "unknown";
        if (str2.compareTo("unknown") == 0) {
            return false;
        }
        pDXDictionary.addAsciiString("t", str2);
        pDXDictionary.addAsciiString("k", str);
        if (obj instanceof byte[]) {
            pDXDictionary.addByteString("v", (byte[]) obj);
        } else if (obj instanceof Boolean) {
            pDXDictionary.addAsciiString("v", ((Boolean) obj).toString());
        } else if (obj instanceof Double) {
            pDXDictionary.addAsciiString("v", ((Double) obj).toString());
        } else if (obj instanceof Integer) {
            pDXDictionary.addInteger("v", ((Integer) obj).intValue());
        } else if (obj instanceof Long) {
            pDXDictionary.addAsciiString("v", ((Long) obj).toString());
        } else if (obj instanceof String) {
            pDXDictionary.addUTF8String("v", (String) obj);
        } else {
            if (!(obj instanceof SessionEvent)) {
                return false;
            }
            pDXDictionary.addAsciiString("v", ((SessionEventImpl) obj).a.getRootParentId() + "." + ((SessionEventImpl) obj).c);
        }
        sequence.addDictionary(pDXDictionary);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Sequence buildMetaSeq() {
        PDXSequence pDXSequence = new PDXSequence();
        addEntryToSequence(pDXSequence, NMSP_CALLLOG_META_ROOT_PARENT_ID, this.a.getRootParentId());
        if (this.e != null) {
            addEntryToSequence(pDXSequence, NMSP_CALLLOG_META_PARENT_ID, new Integer(this.e.c));
        }
        addEntryToSequence(pDXSequence, NMSP_CALLLOG_META_SEQUENCE_ID, new Integer(this.c));
        addEntryToSequence(pDXSequence, NMSP_CALLLOG_META_NAME, this.f);
        addEntryToSequence(pDXSequence, NMSP_CALLLOG_META_TIMESTAMP, new Long(this.g));
        return pDXSequence;
    }

    @Override // com.nuance.nmsp.client.sdk.components.core.calllog.SessionEvent
    public SessionEventBuilder createChildEventBuilder(String str) {
        return SessionEventBuilderImpl.a(this, str);
    }

    public SessionEvent createRemoteChildEvent(String str) {
        return createRemoteChildEvent(str, null);
    }

    @Override // com.nuance.nmsp.client.sdk.components.core.calllog.SessionEvent
    public SessionEvent createRemoteChildEvent(String str, final SessionEvent.SessionEventCommittedListener sessionEventCommittedListener) {
        return SessionEventBuilderImpl.createRemoteSessionEventBuilder(this, str, null).commit(new SessionEvent.SessionEventCommittedListener(this) { // from class: com.nuance.nmsp.client.sdk.components.core.internal.calllog.SessionEventImpl.1
            @Override // com.nuance.nmsp.client.sdk.components.core.calllog.SessionEvent.SessionEventCommittedListener
            public final void committed(SessionEvent sessionEvent) {
                if (sessionEventCommittedListener != null) {
                    sessionEventCommittedListener.committed(sessionEvent);
                }
            }
        });
    }

    public void done(Hashtable hashtable, SessionEvent.SessionEventCommittedListener sessionEventCommittedListener) {
        this._table = hashtable;
        this.g = System.currentTimeMillis();
        this.b.a(this, sessionEventCommittedListener);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void genSeqId() {
        this.c = this.a.getSequenceId();
    }

    public byte[] getBinaryFormat() {
        PDXDictionary pDXDictionary = new PDXDictionary();
        Sequence buildMetaSeq = buildMetaSeq();
        PDXSequence pDXSequence = new PDXSequence();
        for (String str : this._table.keySet()) {
            addEntryToSequence(pDXSequence, str, this._table.get(str));
        }
        pDXDictionary.addSequence("meta", buildMetaSeq);
        if (pDXSequence.size() > 0) {
            pDXDictionary.addSequence("attr", pDXSequence);
        }
        return pDXDictionary.toByteArray();
    }

    public CalllogImpl getCalllog() {
        return this.b;
    }

    @Override // com.nuance.nmsp.client.sdk.components.core.calllog.SessionEvent
    public String getId() {
        if (this.c != 0) {
            return this.a.getRootParentId() + "." + this.c;
        }
        if (d.isErrorEnabled()) {
            d.error("getSessionEventId() has been called before builder commit has completed");
        }
        return null;
    }

    public SessionEvent getParent() {
        return this.e;
    }
}
