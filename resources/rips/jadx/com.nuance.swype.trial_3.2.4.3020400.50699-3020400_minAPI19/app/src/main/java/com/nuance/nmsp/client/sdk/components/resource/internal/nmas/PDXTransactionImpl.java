package com.nuance.nmsp.client.sdk.components.resource.internal.nmas;

import com.nuance.nmsp.client.sdk.common.defines.NMSPDefines;
import com.nuance.nmsp.client.sdk.common.oem.api.LogFactory;
import com.nuance.nmsp.client.sdk.common.oem.api.MessageSystem;
import com.nuance.nmsp.client.sdk.common.oem.api.TimerSystem;
import com.nuance.nmsp.client.sdk.common.protocols.ProtocolDefines;
import com.nuance.nmsp.client.sdk.common.util.Util;
import com.nuance.nmsp.client.sdk.components.core.XMode;
import com.nuance.nmsp.client.sdk.components.core.calllog.SessionEvent;
import com.nuance.nmsp.client.sdk.components.core.calllog.SessionEventAlreadyCommittedException;
import com.nuance.nmsp.client.sdk.components.core.calllog.SessionEventBuilder;
import com.nuance.nmsp.client.sdk.components.core.internal.calllog.SessionEventImpl;
import com.nuance.nmsp.client.sdk.components.core.internal.pdx.PDXDictionary;
import com.nuance.nmsp.client.sdk.components.core.internal.pdx.PDXSequence;
import com.nuance.nmsp.client.sdk.components.core.pdx.Dictionary;
import com.nuance.nmsp.client.sdk.components.core.pdx.Sequence;
import com.nuance.nmsp.client.sdk.components.general.TransactionAlreadyFinishedException;
import com.nuance.nmsp.client.sdk.components.general.TransactionExpiredException;
import com.nuance.nmsp.client.sdk.components.resource.internal.common.ManagerImpl;
import com.nuance.nmsp.client.sdk.components.resource.internal.common.NMSPSession;
import com.nuance.nmsp.client.sdk.components.resource.internal.common.ResourceImpl;
import com.nuance.nmsp.client.sdk.components.resource.nmas.Command;
import com.nuance.nmsp.client.sdk.components.resource.nmas.NMASResourceListener;
import com.nuance.nmsp.client.sdk.components.resource.nmas.PDXCommandListener;
import com.nuance.nmsp.client.sdk.components.resource.nmas.Parameter;
import com.nuance.nmsp.client.sdk.oem.BluetoothSystemOEM;
import com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.xmlResults.XMLResultsHandler;
import java.util.Vector;

/* loaded from: classes.dex */
public class PDXTransactionImpl implements MessageSystem.MessageHandler, Command {
    public static final String INTERNAL_COMPLETION_CAUSE_CONN_FAILED = "CONN_FAILED";
    public static final String INTERNAL_COMPLETION_CAUSE_FINAL_RESULT = "FINAL_RESULT";
    public static final String INTERNAL_COMPLETION_CAUSE_INTERNAL_ERROR = "INTERNAL_ERROR";
    public static final String INTERNAL_COMPLETION_CAUSE_QUERY_ERROR = "QUERY_ERROR";
    public static final String INTERNAL_COMPLETION_CAUSE_QUERY_RETRY = "QUERY_RETRY";
    public static final String INTERNAL_COMPLETION_CAUSE_REMOTE_DISC = "REMOTE_DISC";
    public static final String INTERNAL_COMPLETION_CAUSE_TIMEOUT_CMD = "TIMEOUT_CMD";
    public static final String INTERNAL_COMPLETION_CAUSE_TIMEOUT_IDLE = "TIMEOUT_IDLE";
    public static final String INTERNAL_COMPLETION_CAUSE_UNKNOWN_ERROR = "UNKNOWN_ERROR";
    public static final short STATE_BEGUN = 0;
    public static final short STATE_ENDED = 1;
    public static final short STATE_EXPIRED = 2;
    public static final short STATE_IDLE = -1;
    private static final LogFactory.Log a = LogFactory.getLog(PDXTransactionImpl.class);
    private NMASResourceImpl b;
    private NMASResourceListener c;
    private PDXCommandListener d;
    private NMSPSession e;
    private SessionEvent f;
    private short g;
    private MessageSystem h;
    private TimerSystem.TimerSystemTask i;
    private long j;
    private boolean k;
    private String l;
    private boolean m = false;
    private TransactionLogEntry n;
    private Object o;
    protected byte tranId;

    /* loaded from: classes.dex */
    public static class TransactionLogEntry {
        private int a;
        private String b;
        private String c;
        private PDXTransactionImpl d;
        private boolean e;

        protected TransactionLogEntry(int i, PDXTransactionImpl pDXTransactionImpl) {
            this.a = i;
            if (pDXTransactionImpl.e.getSessionId() != null) {
                this.b = NMSPSession.FormatUuid(pDXTransactionImpl.e.getSessionId());
            } else {
                this.b = "";
            }
            this.c = PDXTransactionImpl.INTERNAL_COMPLETION_CAUSE_INTERNAL_ERROR;
            this.d = pDXTransactionImpl;
            this.e = false;
        }

        static /* synthetic */ void a(TransactionLogEntry transactionLogEntry, String str) {
            if (transactionLogEntry.c.equals(PDXTransactionImpl.INTERNAL_COMPLETION_CAUSE_INTERNAL_ERROR)) {
                transactionLogEntry.c = str;
            } else {
                transactionLogEntry.c += ":" + str;
            }
            PDXTransactionImpl.a(transactionLogEntry.d, transactionLogEntry);
        }

        protected String getCompCause() {
            return this.c;
        }

        protected String getSessionId() {
            return this.b;
        }

        protected int getTranId() {
            return this.a;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public boolean isSent() {
            return this.e;
        }

        protected void setCompCause(Command.CompletionCause completionCause) {
            if (this.c.equals(PDXTransactionImpl.INTERNAL_COMPLETION_CAUSE_INTERNAL_ERROR)) {
                this.c = completionCause.toString();
            } else {
                this.c += ":" + completionCause.toString();
            }
            PDXTransactionImpl.a(this.d, this);
        }

        protected void setSent(boolean z) {
            this.e = z;
        }

        protected void setSessionId(String str) {
            this.b = str;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public PDXTransactionImpl(MessageSystem messageSystem, PDXCommandListener pDXCommandListener, String str, String str2, NMSPSession nMSPSession, String str3, String str4, String str5, String str6, String str7, String str8, NMSPDefines.Codec codec, String str9, short s, short s2, String str10, String str11, String str12, String str13, long j, Dictionary dictionary, NMASResourceImpl nMASResourceImpl, NMASResourceListener nMASResourceListener, byte b) {
        this.d = null;
        this.e = null;
        this.g = (short) -1;
        if (a.isDebugEnabled()) {
            a.debug("PDXTransactionImpl()");
        }
        this.o = new Object();
        this.h = messageSystem;
        this.e = nMSPSession;
        this.d = pDXCommandListener;
        this.j = j;
        this.b = nMASResourceImpl;
        this.c = nMASResourceListener;
        this.tranId = b;
        this.l = str;
        this.k = false;
        if (((ManagerImpl) nMASResourceImpl.getManager()).getResourceLogs() != null) {
            this.n = new TransactionLogEntry(this.tranId, this);
            a(this.n, INTERNAL_COMPLETION_CAUSE_INTERNAL_ERROR);
        }
        synchronized (this.o) {
            this.g = (short) 0;
        }
        Object[] objArr = new Object[18];
        objArr[0] = str3;
        objArr[1] = str4;
        objArr[2] = str5;
        objArr[3] = str6;
        objArr[4] = str7;
        objArr[5] = str8;
        objArr[6] = codec;
        objArr[7] = str9;
        objArr[8] = new Short(s);
        objArr[9] = new Short(s2);
        objArr[10] = str10;
        objArr[11] = str11;
        objArr[12] = str12;
        objArr[13] = str13;
        objArr[14] = str2;
        objArr[15] = str;
        objArr[16] = dictionary;
        messageSystem.send(new MessageSystem.MessageData((byte) 1, objArr), this, Thread.currentThread(), messageSystem.getVRAddr()[0]);
    }

    private static String a(byte[] bArr) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < bArr.length; i++) {
            String hexString = Integer.toHexString(bArr[i]);
            if (hexString.length() > 1) {
                stringBuffer.append(hexString.substring(hexString.length() - 2));
            } else {
                stringBuffer.append(hexString);
            }
            if (i == 3 || i == 5 || i == 7 || i == 9) {
                stringBuffer.append('-');
            }
        }
        return stringBuffer.toString();
    }

    static /* synthetic */ short a(PDXTransactionImpl pDXTransactionImpl, short s) {
        pDXTransactionImpl.g = (short) -1;
        return (short) -1;
    }

    private void a(SessionEvent sessionEvent) {
        if (sessionEvent != null) {
            try {
                this.f = sessionEvent.createChildEventBuilder(SessionEvent.NMSP_CALLLOG_NMASCOMMAND_EVENT).putString(SessionEventImpl.NMSP_CALLLOG_META_NAME, this.l).putString("Tid", new Integer(this.tranId).toString()).commit();
            } catch (SessionEventAlreadyCommittedException e) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void a(TransactionLogEntry transactionLogEntry, String str) {
        if (transactionLogEntry != null) {
            TransactionLogEntry.a(transactionLogEntry, str);
        }
    }

    static /* synthetic */ void a(PDXTransactionImpl pDXTransactionImpl, TransactionLogEntry transactionLogEntry) {
        Vector vector = (Vector) ((ManagerImpl) pDXTransactionImpl.b.getManager()).getResourceLogs();
        if (vector == null) {
            a.info("appendLogToResLogs: NMSPDefines.DEVICE_CMD_LOG_TO_SERVER_ENABLED is disabled.");
        } else {
            if (vector.contains(transactionLogEntry)) {
                return;
            }
            vector.addElement(transactionLogEntry);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void a(String str) {
        if (this.f != null) {
            this.f.createChildEventBuilder(str).commit();
        }
    }

    private void a(String str, int i) {
        if (this.f != null) {
            try {
                this.f.createChildEventBuilder(SessionEvent.NMSP_CALLLOG_NMASAUDIOPARAM_EVENT).putString(SessionEventImpl.NMSP_CALLLOG_META_NAME, str).putInteger("AudioId", i).commit();
            } catch (SessionEventAlreadyCommittedException e) {
            }
        }
    }

    private void a(String str, boolean z) {
        if (this.f != null) {
            SessionEventBuilder createChildEventBuilder = this.f.createChildEventBuilder(SessionEvent.NMSP_CALLLOG_NMASRESPONSE_EVENT);
            try {
                createChildEventBuilder = createChildEventBuilder.putString("ResultType", str);
                if (str.compareTo("QUERY_RESULT") == 0) {
                    createChildEventBuilder = createChildEventBuilder.putBoolean("IsFinal", z);
                }
            } catch (SessionEventAlreadyCommittedException e) {
            }
            createChildEventBuilder.commit();
        }
    }

    @Override // com.nuance.nmsp.client.sdk.components.resource.nmas.Command
    public void end() throws TransactionAlreadyFinishedException, TransactionExpiredException {
        if (a.isDebugEnabled()) {
            a.debug("PDXTransactionImpl.end()");
        }
        synchronized (this.o) {
            if (this.g == -1) {
                a.error("PDXTransactionImpl.end() transaction already finished!");
                throw new TransactionAlreadyFinishedException("transaction already finished!");
            }
            if (this.g == 0) {
                this.g = (short) 1;
                this.h.send(new MessageSystem.MessageData((byte) 4, null), this, Thread.currentThread(), this.h.getVRAddr()[0]);
            } else {
                if (this.g == 1) {
                    a.error("PDXTransactionImpl.end() transaction already finished!");
                    throw new TransactionAlreadyFinishedException("transaction already finished!");
                }
                if (this.g == 2) {
                    a.error("PDXTransactionImpl.end() transaction already expired!");
                    throw new TransactionExpiredException("transaction already expired!");
                }
            }
        }
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.MessageSystem.MessageHandler
    public void handleMessage(Object obj, Object obj2) {
        MessageSystem.MessageData messageData = (MessageSystem.MessageData) obj;
        Object obj3 = messageData.data;
        switch (messageData.command) {
            case 1:
                Object[] objArr = (Object[]) obj3;
                String str = (String) objArr[0];
                String str2 = (String) objArr[1];
                String str3 = (String) objArr[2];
                String str4 = (String) objArr[3];
                String str5 = (String) objArr[4];
                String str6 = (String) objArr[5];
                NMSPDefines.Codec codec = (NMSPDefines.Codec) objArr[6];
                String str7 = (String) objArr[7];
                short shortValue = ((Short) objArr[8]).shortValue();
                short shortValue2 = ((Short) objArr[9]).shortValue();
                String str8 = (String) objArr[10];
                String str9 = (String) objArr[11];
                String str10 = (String) objArr[12];
                String str11 = (String) objArr[13];
                byte[] sessionId = this.e.getSessionId();
                String str12 = (String) objArr[14];
                String str13 = (String) objArr[15];
                Dictionary dictionary = (Dictionary) objArr[16];
                if (new BluetoothSystemOEM(this.b.parameters).isBluetoothHeadsetConnected()) {
                    codec = Util.adjustCodecForBluetooth(codec);
                }
                long defaultRequestId = ResourceImpl.getDefaultRequestId();
                a(this.e.getSessionEvent());
                PDXQueryBegin pDXQueryBegin = new PDXQueryBegin(str, str2, str3, str4, str5, str6, codec, str7, shortValue, shortValue2, str8, str9, str10, str11, sessionId, str12, str13, dictionary);
                Vector vector = (Vector) ((ManagerImpl) this.b.getManager()).getResourceLogs();
                if (vector == null) {
                    a.info("appendLogToQueryBegin: NMSPDefines.DEVICE_CMD_LOG_TO_SERVER_ENABLED is disabled");
                } else if (vector.size() == 0) {
                    a.info("appendLogToQueryBegin: nmasResLogsToServer is empty, nothing to log to server");
                } else {
                    int size = vector.size();
                    Dictionary pDXDictionary = new PDXDictionary();
                    Sequence pDXSequence = new PDXSequence();
                    for (int i = 0; i < size; i++) {
                        TransactionLogEntry transactionLogEntry = (TransactionLogEntry) vector.elementAt(i);
                        if (transactionLogEntry.getTranId() != this.tranId) {
                            PDXDictionary pDXDictionary2 = new PDXDictionary();
                            pDXDictionary2.put("id", transactionLogEntry.getSessionId() + ":" + transactionLogEntry.getTranId(), (short) 193);
                            pDXDictionary2.put("status", transactionLogEntry.getCompCause().toString(), (short) 193);
                            pDXSequence.addDictionary(pDXDictionary2);
                            transactionLogEntry.setSent(true);
                        }
                    }
                    pDXDictionary.addSequence("device_log", pDXSequence);
                    pDXQueryBegin.addDictionary("app_info", pDXDictionary);
                }
                this.e.postBcpMessage(ProtocolDefines.XMODE_BCP_COMMAND_BCP_BEGIN, XMode.NET_CONTEXT_SEND_BCP_BEGIN + ((int) this.tranId), pDXQueryBegin.getMessage(), null, this.tranId, defaultRequestId, this.b, false);
                if (sessionId != null) {
                    try {
                        if (this.m) {
                            return;
                        }
                        this.m = true;
                        a.debug("PDXCommandCreated() called from handleInit()" + a(sessionId) + ":" + ((int) this.tranId) + " (" + this + "," + this.c + ")");
                        this.c.PDXCommandCreated(a(sessionId) + ":" + ((int) this.tranId));
                        return;
                    } catch (Throwable th) {
                        a.error("got exp in PDXCommandListener.PDXCommandCreated() [" + th.getClass().getName() + "] msg [" + th.getMessage() + "]");
                        return;
                    }
                }
                return;
            case 2:
                Object obj4 = (Parameter) obj3;
                if (((PDXParam) obj4).getType() == Byte.MAX_VALUE) {
                    this.e.addAudioSink(((PDXTTSParam) obj4).getAudioId(), ((PDXTTSParam) obj4).getAudioSink(), this.b);
                }
                long defaultRequestId2 = ResourceImpl.getDefaultRequestId();
                if (obj4 instanceof PDXAudioParam) {
                    a(((PDXAudioParam) obj4).getName(), ((PDXAudioParam) obj4).getBufferId());
                }
                this.e.postBcpMessage(ProtocolDefines.XMODE_BCP_COMMAND_BCP_DATA, XMode.NET_CONTEXT_SEND_BCP_DATA, new PDXQueryParameter((PDXParam) obj4).toByteArray(), null, this.tranId, defaultRequestId2, this.b, false);
                return;
            case 3:
                this.e.postBcpMessage(ProtocolDefines.XMODE_BCP_COMMAND_BCP_DATA, XMode.NET_CONTEXT_SEND_BCP_DATA, new PDXEnrollmentAudio((byte[]) obj3).toByteArray(), null, this.tranId, ResourceImpl.getDefaultRequestId(), this.b, false);
                return;
            case 4:
                this.e.postBcpMessage(ProtocolDefines.XMODE_BCP_COMMAND_BCP_DATA, XMode.NET_CONTEXT_SEND_BCP_DATA, new PDXQueryEnd().toByteArray(), null, this.tranId, ResourceImpl.getDefaultRequestId(), this.b, false);
                this.i = new TimerSystem.TimerSystemTask() { // from class: com.nuance.nmsp.client.sdk.components.resource.internal.nmas.PDXTransactionImpl.1
                    @Override // java.lang.Runnable
                    public final void run() {
                        try {
                            synchronized (PDXTransactionImpl.this.o) {
                                PDXTransactionImpl.a(PDXTransactionImpl.this, (short) -1);
                            }
                            PDXTransactionImpl.this.a(SessionEvent.NMSP_CALLLOG_COMMANDEXPIRED_EVENT);
                            if (PDXTransactionImpl.this.d != null) {
                                PDXTransactionImpl.a.debug("PDXTransactionImpl.end() timed out waiting for results. ");
                                try {
                                    PDXTransactionImpl pDXTransactionImpl = PDXTransactionImpl.this;
                                    PDXTransactionImpl.a(PDXTransactionImpl.this.n, PDXTransactionImpl.INTERNAL_COMPLETION_CAUSE_TIMEOUT_CMD);
                                    PDXTransactionImpl.this.d.PDXCommandEvent((short) 1);
                                } catch (Throwable th2) {
                                    PDXTransactionImpl.a.error("got exp in PDXCommandListener.PDXCommandEvent(TIMED_OUT_WAITING_FOR_RESULT) [" + th2.getClass().getName() + "] msg [" + th2.getMessage() + "]");
                                }
                            }
                        } catch (Exception e) {
                            PDXTransactionImpl.a.error("PDXTransactionImpl.run() " + e.getClass().getName() + XMLResultsHandler.SEP_SPACE + e.getMessage());
                        }
                    }
                };
                this.h.scheduleTask(this.i, this.j);
                return;
            case 5:
                this.d.PDXCommandEvent((short) 4);
                return;
            case 6:
                Command.CompletionCause completionCause = (Command.CompletionCause) obj3;
                TransactionLogEntry transactionLogEntry2 = this.n;
                if (transactionLogEntry2 != null) {
                    transactionLogEntry2.setCompCause(completionCause);
                    return;
                }
                return;
            default:
                return;
        }
    }

    @Override // com.nuance.nmsp.client.sdk.components.resource.nmas.Command
    public boolean isNetworkHealthy() {
        return this.e.isNetworkHealthy();
    }

    public void onQueryError(PDXQueryError pDXQueryError, byte b) {
        a.debug("PDXTransactionImpl.onQueryError()");
        if (b != this.tranId) {
            return;
        }
        synchronized (this.o) {
            if (this.g == -1 || this.g == 2) {
                return;
            }
            this.g = (short) -1;
            a(INTERNAL_COMPLETION_CAUSE_QUERY_ERROR, true);
            if (this.i != null) {
                this.h.cancelTask(this.i);
            }
            if (this.d != null) {
                try {
                    a(this.n, INTERNAL_COMPLETION_CAUSE_QUERY_ERROR);
                    this.d.PDXQueryErrorReturned(pDXQueryError);
                } catch (Throwable th) {
                    a.error("got exp in PDXCommandListener.PDXQueryErrorReturned() [" + th.getClass().getName() + "] msg [" + th.getMessage() + "]");
                }
            }
        }
    }

    public void onQueryResults(PDXQueryResult pDXQueryResult, byte b) {
        if (a.isDebugEnabled()) {
            a.debug("PDXTransactionImpl.onQueryResults()");
        }
        if (b != this.tranId) {
            return;
        }
        synchronized (this.o) {
            if (this.g == -1 || this.g == 2) {
                return;
            }
            this.k = true;
            a("QUERY_RESULT", pDXQueryResult.isFinalResponse());
            if (!pDXQueryResult.isFinalResponse()) {
                resetCommandTimeoutTask();
            } else if (this.i != null) {
                this.h.cancelTask(this.i);
            }
            if (this.d != null) {
                try {
                    if (pDXQueryResult.isFinalResponse()) {
                        a(this.n, INTERNAL_COMPLETION_CAUSE_FINAL_RESULT);
                    }
                    this.d.PDXQueryResultReturned(pDXQueryResult);
                } catch (Throwable th) {
                    a.error("got exp in PDXCommandListener.PDXQueryResultReturned() [" + th.getClass().getName() + "] msg [" + th.getMessage() + "]");
                }
            }
        }
    }

    public void onQueryRetry(PDXQueryRetry pDXQueryRetry, byte b) {
        a.debug("PDXTransactionImpl.onQueryRetry()");
        if (b != this.tranId) {
            return;
        }
        synchronized (this.o) {
            if (this.g == -1 || this.g == 2) {
                return;
            }
            this.g = (short) -1;
            a(INTERNAL_COMPLETION_CAUSE_QUERY_RETRY, true);
            if (this.i != null) {
                this.h.cancelTask(this.i);
            }
            if (this.d != null) {
                try {
                    a(this.n, INTERNAL_COMPLETION_CAUSE_QUERY_RETRY);
                    this.d.PDXQueryRetryReturned(pDXQueryRetry);
                } catch (Throwable th) {
                    a.error("got exp in PDXCommandListener.PDXQueryRetryReturned() [" + th.getClass().getName() + "] msg [" + th.getMessage() + "]");
                }
            }
        }
    }

    public void onSessionConnected(byte[] bArr) {
        if (a.isDebugEnabled()) {
            a.debug("PDXTransactionImpl.onSessionConnected()");
        }
        synchronized (this.o) {
            if (this.g == 0 || this.g == 1) {
                try {
                    String a2 = a(bArr);
                    TransactionLogEntry transactionLogEntry = this.n;
                    if (transactionLogEntry != null) {
                        transactionLogEntry.setSessionId(a2);
                    }
                    if (!this.m) {
                        this.m = true;
                        a.debug("PDXCommandCreated() called from onSessionConnected()" + a2 + ":" + ((int) this.tranId) + " (" + this + "," + this.c + ")");
                        this.c.PDXCommandCreated(a2 + ":" + ((int) this.tranId));
                    }
                } catch (Throwable th) {
                    a.error("got exp in PDXCommandListener.PDXCommandCreated() [" + th.getClass().getName() + "] msg [" + th.getMessage() + "]");
                }
            }
        }
    }

    public void onSessionDisconnected(short s) {
        if (a.isDebugEnabled()) {
            a.debug("PDXTransactionImpl.onSessionDisconnected() " + ((int) s));
        }
        synchronized (this.o) {
            if (this.g == 1 && this.i != null) {
                this.h.cancelTask(this.i);
            }
            if (this.g == 2) {
                return;
            }
            try {
            } catch (Throwable th) {
                a.error("got exp in PDXCommandListener.PDXCommandEvent() or PDXManagerListener.PDXManagerError() or PDXManagerListener.PDXManagerDisconnected() reasonCode [" + ((int) s) + "] [" + th.getClass().getName() + "] msg [" + th.getMessage() + "]");
            }
            if (s == 0) {
                if (this.g != -1) {
                    a(this.n, INTERNAL_COMPLETION_CAUSE_REMOTE_DISC);
                    this.d.PDXCommandEvent((short) 3);
                }
            } else if (s == 1) {
                if (this.g != -1) {
                    this.d.PDXCommandEvent((short) 4);
                }
            } else if (s == 3) {
                if (this.g != -1) {
                    a(this.n, INTERNAL_COMPLETION_CAUSE_TIMEOUT_IDLE);
                    this.d.PDXCommandEvent((short) 5);
                }
            } else if (s == 4) {
                if (this.g != -1) {
                    a(this.n, INTERNAL_COMPLETION_CAUSE_CONN_FAILED);
                    this.c.PDXCreateCommandFailed();
                }
            } else if (s == 5) {
                if (this.g != -1) {
                    a(this.n, INTERNAL_COMPLETION_CAUSE_CONN_FAILED);
                    this.c.PDXCreateCommandFailed();
                }
            } else if (s == 6) {
                if (this.g != -1) {
                    a(this.n, INTERNAL_COMPLETION_CAUSE_REMOTE_DISC);
                    this.d.PDXCommandEvent((short) 3);
                }
            } else {
                if (s != 7) {
                    if (s == 8) {
                        if (this.g != -1) {
                            a(this.n, INTERNAL_COMPLETION_CAUSE_REMOTE_DISC);
                            this.d.PDXCommandEvent((short) 3);
                        }
                    }
                    this.g = (short) 2;
                }
                if (this.g != -1) {
                    a(this.n, INTERNAL_COMPLETION_CAUSE_CONN_FAILED);
                    this.c.PDXCreateCommandFailed();
                }
            }
            this.g = (short) 2;
        }
    }

    public void onVapPlayBeginReceived() {
        resetCommandTimeoutTask();
    }

    public void onVapPlayEndReceived() {
        resetCommandTimeoutTask();
    }

    public void onVapPlayReceived() {
        resetCommandTimeoutTask();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void resetCommandTimeoutTask() {
        if (this.i != null) {
            if (this.h.cancelTask(this.i)) {
                this.i = new TimerSystem.TimerSystemTask() { // from class: com.nuance.nmsp.client.sdk.components.resource.internal.nmas.PDXTransactionImpl.2
                    @Override // java.lang.Runnable
                    public final void run() {
                        try {
                            synchronized (PDXTransactionImpl.this.o) {
                                PDXTransactionImpl.a(PDXTransactionImpl.this, (short) -1);
                            }
                            PDXTransactionImpl.this.a(SessionEvent.NMSP_CALLLOG_COMMANDEXPIRED_EVENT);
                            if (PDXTransactionImpl.this.d != null) {
                                PDXTransactionImpl.a.debug("PDXTransactionImpl.end() timed out waiting for results. ");
                                try {
                                    PDXTransactionImpl pDXTransactionImpl = PDXTransactionImpl.this;
                                    PDXTransactionImpl.a(PDXTransactionImpl.this.n, PDXTransactionImpl.INTERNAL_COMPLETION_CAUSE_TIMEOUT_CMD);
                                    PDXTransactionImpl.this.d.PDXCommandEvent((short) 1);
                                } catch (Throwable th) {
                                    PDXTransactionImpl.a.error("got exp in PDXCommandListener.PDXCommandEvent(TIMED_OUT_WAITING_FOR_RESULT) [" + th.getClass().getName() + "] msg [" + th.getMessage() + "]");
                                }
                            }
                        } catch (Exception e) {
                            PDXTransactionImpl.a.error("PDXTransactionImpl.run() " + e.getClass().getName() + XMLResultsHandler.SEP_SPACE + e.getMessage());
                        }
                    }
                };
            }
            this.h.scheduleTask(this.i, this.j);
        }
    }

    @Override // com.nuance.nmsp.client.sdk.components.resource.nmas.Command
    public void sendEnrollmentAudio(byte[] bArr) throws TransactionAlreadyFinishedException, TransactionExpiredException {
        a.debug("PDXTransactionImpl.sendEnrollmentAudio()");
        synchronized (this.o) {
            if (this.g == -1) {
                a.error("PDXTransactionImpl.sendEnrollmentAudio() transaction already finished!");
                throw new TransactionAlreadyFinishedException("transaction already finished!");
            }
            if (this.g == 0) {
                if (bArr == null) {
                    throw new NullPointerException("enrollment audio is null");
                }
                if (bArr.length == 0) {
                    throw new IllegalArgumentException("enrollment audio is empty");
                }
                byte[] bArr2 = new byte[bArr.length];
                System.arraycopy(bArr, 0, bArr2, 0, bArr2.length);
                this.h.send(new MessageSystem.MessageData((byte) 3, bArr2), this, Thread.currentThread(), this.h.getVRAddr()[0]);
            } else {
                if (this.g == 1) {
                    a.error("PDXTransactionImpl.sendEnrollmentAudio() transaction already finished!");
                    throw new TransactionAlreadyFinishedException("transaction already finished!");
                }
                if (this.g == 2) {
                    a.error("PDXTransactionImpl.sendEnrollmentAudio() transaction already expired!");
                    throw new TransactionExpiredException("transaction already expired!");
                }
            }
        }
    }

    @Override // com.nuance.nmsp.client.sdk.components.resource.nmas.Command
    public void sendParam(Parameter parameter) throws TransactionAlreadyFinishedException, TransactionExpiredException {
        if (a.isDebugEnabled()) {
            a.debug("PDXTransactionImpl.sendParam()");
        }
        if (parameter == null) {
            throw new NullPointerException("Parameter cannot be null");
        }
        synchronized (this.o) {
            if (this.g == -1) {
                a.error("PDXTransactionImpl.sendParam() transaction already finished!");
                throw new TransactionAlreadyFinishedException("transaction already finished!");
            }
            if (this.g == 0) {
                this.h.send(new MessageSystem.MessageData((byte) 2, parameter), this, Thread.currentThread(), this.h.getVRAddr()[0]);
            } else {
                if (this.g == 1) {
                    a.error("PDXTransactionImpl.sendParam() transaction already finished!");
                    throw new TransactionAlreadyFinishedException("transaction already finished!");
                }
                if (this.g == 2) {
                    a.error("PDXTransactionImpl.sendParam() transaction already expired!");
                    throw new TransactionExpiredException("transaction already expired!");
                }
            }
        }
    }

    @Override // com.nuance.nmsp.client.sdk.components.resource.nmas.Command
    public void setLog(Command.CompletionCause completionCause) {
        this.h.send(new MessageSystem.MessageData((byte) 6, completionCause), this, Thread.currentThread(), this.h.getVRAddr()[0]);
    }

    public void transactionOver() {
        synchronized (this.o) {
            if (this.g == 1 && this.i != null) {
                this.h.cancelTask(this.i);
            }
            if (this.g == 2) {
                return;
            }
            if (this.g != -1) {
                if (!this.k) {
                    a(SessionEvent.NMSP_CALLLOG_COMMANDABORT_EVENT);
                }
                this.h.send(new MessageSystem.MessageData((byte) 5, null), this, Thread.currentThread(), this.h.getVRAddr()[0]);
            }
            this.g = (short) 2;
        }
    }
}
