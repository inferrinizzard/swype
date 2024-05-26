package com.nuance.nmsp.client.sdk.components.core;

import com.facebook.internal.ServerProtocol;
import com.nuance.nmsp.client.sdk.common.defines.NMSPDefines;
import com.nuance.nmsp.client.sdk.common.oem.api.LogFactory;
import com.nuance.nmsp.client.sdk.common.oem.api.MessageSystem;
import com.nuance.nmsp.client.sdk.common.oem.api.NetworkSystem;
import com.nuance.nmsp.client.sdk.common.oem.api.TimerSystem;
import com.nuance.nmsp.client.sdk.common.protocols.ProtocolBuilder;
import com.nuance.nmsp.client.sdk.common.protocols.ProtocolDefines;
import com.nuance.nmsp.client.sdk.common.protocols.XModeMsgHeader;
import com.nuance.nmsp.client.sdk.common.util.ByteConversion;
import com.nuance.nmsp.client.sdk.components.core.calllog.SessionEvent;
import com.nuance.nmsp.client.sdk.components.core.internal.calllog.SessionEventBuilderImpl;
import com.nuance.nmsp.client.sdk.components.general.Parameter;
import com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.xmlResults.XMLResultsHandler;
import com.nuance.swype.input.R;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;

/* loaded from: classes.dex */
public class XMode implements MessageSystem.MessageHandler, NetworkSystem.CloseSocketCallback, NetworkSystem.OpenSocketCallback, NetworkSystem.ReadSocketCallback, NetworkSystem.WriteSocketCallback {
    public static final String NET_CONTEXT_SEND_BCP_BEGIN = "SEND_BCP_BEGIN";
    public static final String NET_CONTEXT_SEND_BCP_CANCEL = "SEND_BCP_CANCEL";
    public static final String NET_CONTEXT_SEND_BCP_DATA = "SEND_BCP_DATA";
    public static final String NET_CONTEXT_SEND_BCP_FREE_RESOURCE = "SEND_BCP_FREE_RESOURCE";
    public static final String NET_CONTEXT_SEND_BCP_FREE_RESOURCE_ID = "SEND_BCP_FREE_RESOURCE_ID";
    public static final String NET_CONTEXT_SEND_BCP_GENERATE_AUDIO = "SEND_BCP_GENERATE_AUDIO";
    public static final String NET_CONTEXT_SEND_BCP_GET_PARAMS = "SEND_BCP_GET_PARAMS";
    public static final String NET_CONTEXT_SEND_BCP_LOAD_GRAMMAR = "SEND_BCP_LOAD_GRAMMAR";
    public static final String NET_CONTEXT_SEND_BCP_LOAD_RESOURCE = "SEND_BCP_LOAD_RESOURCE";
    public static final String NET_CONTEXT_SEND_BCP_LOG = "SEND_BCP_LOG";
    public static final String NET_CONTEXT_SEND_BCP_RECOGNIZE = "SEND_BCP_RECOGNIZE";
    public static final String NET_CONTEXT_SEND_BCP_SET_PARAMS = "SEND_BCP_SET_PARAMS";
    public static final int SEND_MSG_PRIORITY_HIGH = 3;
    public static final int SEND_MSG_PRIORITY_LOW = 1;
    public static final byte STATE_CLOSED = 3;
    public static final byte STATE_CLOSING = 2;
    public static final byte STATE_CONNECTED = 1;
    public static final byte STATE_CONNECTING = 0;
    public static final short XMODE_SESSION_IDLE_FOR_TOO_LONG = 3;
    public static final short XMODE_SOCKET_CLOSED_COP_CONNECT_FAILED = 7;
    public static final short XMODE_SOCKET_CLOSED_COP_CONNECT_TIMEOUT = 5;
    public static final short XMODE_SOCKET_CLOSED_COP_DISCONNECT = 6;
    public static final short XMODE_SOCKET_CLOSED_DISCONNECT = 1;
    public static final short XMODE_SOCKET_CLOSED_NETWORK_ERROR = 8;
    public static final short XMODE_SOCKET_CLOSED_OPEN_SOCKET_FAILED = 4;
    public static final short XMODE_SOCKET_CLOSED_REMOTE_DISCONNECT = 0;
    private static LogFactory.Log a = LogFactory.getLog(XMode.class);
    private TimerSystem.TimerSystemTask b;
    private int c;
    private TimerSystem.TimerSystemTask d;
    private int e;
    private TimerSystem.TimerSystemTask f;
    private Vector g;
    private boolean h;
    private Vector i;
    protected short inputCodec;
    private MessageSystem j;
    private Object k;
    private String o;
    protected short outputCodec;
    private String p;
    protected int pingRequestId;
    protected short port;
    private byte[] q;
    protected String server;
    public byte[] sessionId;
    protected byte state;
    private XModeListener t;
    private SessionEvent u;
    private XModeMsgHeader l = null;
    private short m = 0;
    private short n = 9;
    private NetworkSystem r = null;
    private String s = "";
    private SessionEventBuilderImpl v = null;
    private Queue w = new LinkedList();
    private Queue x = new LinkedList();
    private boolean y = false;

    /* loaded from: classes.dex */
    public interface XModeListener {
        void copConnected();

        void socketClosed(short s, short s2);

        void socketOpened();

        void xmodeMsgCallback(XModeMsgHeader xModeMsgHeader, byte[] bArr);

        void xmodeMsgNotSent(String str, Object obj);

        void xmodeMsgSent(String str, Object obj);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class a {
        byte[] a;
        Object b;

        public a(XMode xMode, byte[] bArr, Object obj) {
            this.a = bArr;
            this.b = obj;
        }
    }

    public XMode(String str, short s, String str2, byte[] bArr, String str3, XModeListener xModeListener, Vector vector, MessageSystem messageSystem) {
        this.c = 30;
        this.e = 50;
        this.server = null;
        this.port = (short) 0;
        this.h = false;
        this.i = null;
        this.j = null;
        this.o = "Not specified";
        this.p = "Not specified";
        this.q = null;
        this.server = str;
        this.port = s;
        this.o = str2;
        this.q = bArr;
        this.p = str3;
        this.t = xModeListener;
        if (vector != null) {
            this.g = vector;
        } else {
            this.g = new Vector();
        }
        this.j = messageSystem;
        if (a.isDebugEnabled()) {
            a.debug("XMode() server: " + str + " port: " + ((int) s));
        }
        Enumeration elements = this.g.elements();
        while (elements.hasMoreElements()) {
            Parameter parameter = (Parameter) elements.nextElement();
            if (a.isDebugEnabled()) {
                a.debug("XMode() " + parameter.getType() + " : " + parameter.getName() + " = " + new String(parameter.getValue()));
            }
            if (parameter.getType() == Parameter.Type.SDK) {
                if (parameter.getName().equals(NMSPDefines.IdleSessionTimeout)) {
                    int parseInt = Integer.parseInt(new String(parameter.getValue()));
                    if (parseInt > 0) {
                        this.e = parseInt;
                    }
                } else if (parameter.getName().equals(NMSPDefines.ConnectionTimeout)) {
                    this.c = Integer.parseInt(new String(parameter.getValue()));
                } else if (parameter.getName().equals(NMSPDefines.NMSP_DEFINES_SSL_SOCKET) || parameter.getName().equals(NMSPDefines.NMSP_DEFINES_SSL_CERT_SUMMARY) || parameter.getName().equals(NMSPDefines.NMSP_DEFINES_SSL_CERT_DATA) || parameter.getName().equals(NMSPDefines.NMSP_DEFINES_SSL_SELFSIGNED_CERT)) {
                    if (parameter.getName().equals(NMSPDefines.NMSP_DEFINES_SSL_SOCKET) && (new String(parameter.getValue()).equals("TRUE") || new String(parameter.getValue()).equals(ServerProtocol.DIALOG_RETURN_SCOPES_TRUE))) {
                        this.h = true;
                    }
                    if (this.i == null) {
                        this.i = new Vector();
                    }
                    this.i.addElement(parameter);
                }
            }
        }
        this.state = (byte) 3;
    }

    static /* synthetic */ SessionEventBuilderImpl a(XMode xMode, SessionEventBuilderImpl sessionEventBuilderImpl) {
        xMode.v = null;
        return null;
    }

    static /* synthetic */ short a(XMode xMode, short s) {
        xMode.m = (short) 3;
        return (short) 3;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void a(byte b, Object obj) {
        try {
            this.j.send(new MessageSystem.MessageData(b, obj), this, Thread.currentThread(), this.j.getVRAddr()[0]);
        } catch (Exception e) {
            if (a.isErrorEnabled()) {
                a.error("XMode.sendCmdMsg() " + e.getMessage());
            }
        }
    }

    private void a(XModeMsgHeader xModeMsgHeader, byte[] bArr) {
        if (a.isDebugEnabled()) {
            a.debug("XMode.parseXModeMsg() protocol: " + ((int) xModeMsgHeader.protocol) + " cmd: " + ((int) xModeMsgHeader.cmd));
        }
        switch (xModeMsgHeader.protocol) {
            case 1:
                if (this.f != null && this.j.cancelTask(this.f)) {
                    this.f = new TimerSystem.TimerSystemTask() { // from class: com.nuance.nmsp.client.sdk.components.core.XMode.8
                        @Override // java.lang.Runnable
                        public final void run() {
                            try {
                                XMode.a(XMode.this, (short) 3);
                                if (XMode.a.isErrorEnabled()) {
                                    XMode.a.error("Session Idle for too long, longer than [" + XMode.this.e + "] (initiated from XMode.parseVap)");
                                }
                                XMode.this.state = (byte) 2;
                                XMode.this.a((byte) 4, (Object) null);
                            } catch (Exception e) {
                                if (XMode.a.isErrorEnabled()) {
                                    XMode.a.error("XMode.parseXModeMsg() " + e.getClass().getName() + XMLResultsHandler.SEP_SPACE + e.getMessage());
                                }
                            }
                        }
                    };
                    this.j.scheduleTask(this.f, this.e * 1000);
                }
                this.t.xmodeMsgCallback(xModeMsgHeader, bArr);
                return;
            case 2:
                if (this.f != null && this.j.cancelTask(this.f)) {
                    this.f = new TimerSystem.TimerSystemTask() { // from class: com.nuance.nmsp.client.sdk.components.core.XMode.7
                        @Override // java.lang.Runnable
                        public final void run() {
                            try {
                                XMode.a(XMode.this, (short) 3);
                                if (XMode.a.isErrorEnabled()) {
                                    XMode.a.error("Session Idle for too long, longer than [" + XMode.this.e + "] (initiated from XMode.parseBcp)");
                                }
                                XMode.this.state = (byte) 2;
                                XMode.this.a((byte) 4, (Object) null);
                            } catch (Exception e) {
                                if (XMode.a.isErrorEnabled()) {
                                    XMode.a.error("XMode.parseXModeMsg() " + e.getClass().getName() + XMLResultsHandler.SEP_SPACE + e.getMessage());
                                }
                            }
                        }
                    };
                    this.j.scheduleTask(this.f, this.e * 1000);
                }
                if (xModeMsgHeader.cmd != 2641) {
                    this.t.xmodeMsgCallback(xModeMsgHeader, bArr);
                    return;
                } else {
                    if (a.isWarnEnabled()) {
                        a.warn("XMode.parseXModeMsg() BCP_LOG_RESPONSE is deprecated");
                        return;
                    }
                    return;
                }
            case 3:
                switch (xModeMsgHeader.cmd) {
                    case R.styleable.ThemeTemplate_chineseFunctionBarEmoji /* 257 */:
                        this.j.cancelTask(this.d);
                        if (this.v != null) {
                            this.v.commit();
                            this.v = null;
                        }
                        this.sessionId = new byte[16];
                        System.arraycopy(bArr, 0, this.sessionId, 0, 16);
                        this.j.setSessionId(this.sessionId);
                        this.r.setSessionId(this.k, this.sessionId);
                        this.s = LogFactory.Log.FormatUuid(this.sessionId);
                        a.setCurrentSession(this.s);
                        try {
                            if (a.isDebugEnabled()) {
                                a.debug("Received COP_Connected " + this.s);
                            }
                            a((byte) 10, (Object) null);
                            this.t.copConnected();
                            a.unsetCurrentSession();
                            a(SessionEvent.NMSP_CALLLOG_SOCKETOPENED_EVENT);
                            this.f = new TimerSystem.TimerSystemTask() { // from class: com.nuance.nmsp.client.sdk.components.core.XMode.6
                                @Override // java.lang.Runnable
                                public final void run() {
                                    try {
                                        XMode.a(XMode.this, (short) 3);
                                        if (XMode.a.isErrorEnabled()) {
                                            XMode.a.error("Session Idle for too long, longer than [" + XMode.this.e + "] (initiated from XMode.parseCopConnected)");
                                        }
                                        XMode.this.state = (byte) 2;
                                        XMode.this.a((byte) 4, (Object) null);
                                    } catch (Exception e) {
                                        if (XMode.a.isErrorEnabled()) {
                                            XMode.a.error("XMode.parseXModeMsg() " + e.getClass().getName() + XMLResultsHandler.SEP_SPACE + e.getMessage());
                                        }
                                    }
                                }
                            };
                            this.j.scheduleTask(this.f, this.e * 1000);
                            break;
                        } catch (Throwable th) {
                            a.unsetCurrentSession();
                            throw th;
                        }
                    case R.styleable.ThemeTemplate_chineseFunctionBarHandwritingOn /* 258 */:
                        this.pingRequestId = ByteConversion.bytesToInt(bArr, 0);
                        a((byte) 8, (Object) null);
                        break;
                    case 512:
                        if (this.f != null) {
                            this.j.cancelTask(this.f);
                        }
                        this.n = ByteConversion.bytesToShort(bArr, 0);
                        this.state = (byte) 2;
                        this.m = (short) 6;
                        if (a.isWarnEnabled()) {
                            a.warn("XMode.parseXModeMsgCopDisconnect() Received COP DISCONNECT. ");
                        }
                        a((byte) 4, (Object) null);
                        break;
                    case 768:
                        short bytesToShort = ByteConversion.bytesToShort(bArr, 0);
                        ByteConversion.bytesToInt(bArr, 2);
                        this.m = (short) 7;
                        this.n = bytesToShort;
                        this.state = (byte) 2;
                        if (a.isErrorEnabled()) {
                            a.error("XMode.parseXModeMsgCopConnectFailed() COP CONNECT failure. ");
                        }
                        a((byte) 4, (Object) null);
                        break;
                }
                this.t.xmodeMsgCallback(xModeMsgHeader, bArr);
                return;
            case 15:
                return;
            default:
                if (a.isErrorEnabled()) {
                    a.error("XMode.parseXModeMsg() unknown protocol: " + Integer.toHexString(xModeMsgHeader.protocol));
                    return;
                }
                return;
        }
    }

    private void a(String str) {
        if (this.u == null || this.y) {
            return;
        }
        this.u.createChildEventBuilder(str).commit();
    }

    public void clearPendingNetworkOps() {
        this.r.clearPendingOps(this.k);
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.NetworkSystem.CloseSocketCallback
    public void closeSocketCallback(NetworkSystem.NetworkStatus networkStatus, Object obj, Object obj2) {
        if (a.isDebugEnabled()) {
            a.debug("XMode.closeSocketCallback() " + this.s);
        }
        this.state = (byte) 3;
        if (this.b != null) {
            this.j.cancelTask(this.b);
            this.b = null;
        }
        if (this.f != null) {
            this.j.cancelTask(this.f);
            this.f = null;
        }
        if (this.k != null) {
            this.t.socketClosed(this.m, this.n);
        }
        this.k = null;
        this.r = null;
        this.sessionId = null;
        this.s = "";
    }

    public void connect(short s, short s2) {
        if (a.isDebugEnabled()) {
            a.debug("XMode.connect() codec: " + ((int) s2));
        }
        if (this.state == 1 || this.state == 0) {
            return;
        }
        if (this.state != 2) {
            if (this.state == 3) {
                a(SessionEvent.NMSP_CALLLOG_OPENSOCKET_EVENT);
                this.inputCodec = s;
                this.outputCodec = s2;
                this.state = (byte) 0;
                a((byte) 1, (Object) null);
                return;
            }
            return;
        }
        if (this.b != null) {
            this.j.cancelTask(this.b);
        }
        if (this.f != null) {
            this.j.cancelTask(this.f);
        }
        a(SessionEvent.NMSP_CALLLOG_OPENSOCKET_EVENT);
        this.k = null;
        this.r = null;
        this.sessionId = null;
        this.s = "";
        this.inputCodec = s;
        this.outputCodec = s2;
        this.state = (byte) 0;
        a((byte) 1, (Object) null);
    }

    public void createSessionEvent(SessionEvent sessionEvent) {
        if (sessionEvent != null) {
            this.u = sessionEvent.createChildEventBuilder(SessionEvent.NMSP_CALLLOG_NMSPSOCKET_EVENT).commit();
        }
    }

    public void disconnect() {
        if (a.isDebugEnabled()) {
            a.debug("XMode.disconnect() state:" + ((int) this.state) + ", socket:" + this.k);
        }
        if (this.state == 3) {
            this.m = (short) 1;
            this.t.socketClosed(this.m, this.n);
            return;
        }
        if (this.state != 2) {
            if (this.state != 0) {
                if (this.state == 1) {
                    this.state = (byte) 2;
                    this.m = (short) 1;
                    a((byte) 2, (Object) null);
                    return;
                }
                return;
            }
            a(SessionEvent.NMSP_CALLLOG_CLOSESOCKET_EVENT);
            this.m = (short) 1;
            this.state = (byte) 2;
            if (this.k != null) {
                this.r.closeSocket(this.k);
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:48:0x0165  */
    /* JADX WARN: Removed duplicated region for block: B:51:0x0390  */
    @Override // com.nuance.nmsp.client.sdk.common.oem.api.MessageSystem.MessageHandler
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void handleMessage(java.lang.Object r12, java.lang.Object r13) {
        /*
            Method dump skipped, instructions count: 952
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.nuance.nmsp.client.sdk.components.core.XMode.handleMessage(java.lang.Object, java.lang.Object):void");
    }

    public boolean isNetworkHealthy() {
        NetworkSystem networkSystem = this.r;
        if (networkSystem != null) {
            return networkSystem.isNetworkHealthy();
        }
        return false;
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.NetworkSystem.OpenSocketCallback
    public void openSocketCallback(NetworkSystem.NetworkStatus networkStatus, Object obj, Object obj2) {
        if (a.isDebugEnabled()) {
            a.debug("XMode.openSocketCallback() ");
        }
        if (networkStatus == NetworkSystem.NetworkStatus.NETWORK_OK) {
            this.k = obj;
            if (this.state != 0) {
                this.state = (byte) 2;
                this.r.closeSocket(this.k);
                return;
            } else if (this.u == null) {
                a((byte) 7, (Object) null);
                return;
            } else {
                a((byte) 13, (Object) null);
                return;
            }
        }
        if (networkStatus != NetworkSystem.NetworkStatus.NETWORK_ERROR) {
            if (networkStatus == NetworkSystem.NetworkStatus.NETWORK_MEMORY_ERROR) {
                this.m = (short) 4;
                if (a.isErrorEnabled()) {
                    a.error("XMode.openSocketCallback() NETWORK_MEMORY_ERROR");
                    return;
                }
                return;
            }
            return;
        }
        if (a.isErrorEnabled()) {
            a.error("XMode.openSocketCallback() NETWORK_ERROR");
        }
        this.state = (byte) 3;
        this.m = (short) 4;
        this.t.socketClosed(this.m, this.n);
        this.k = null;
        this.r = null;
        this.sessionId = null;
        this.s = "";
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.NetworkSystem.ReadSocketCallback
    public void readSocketCallback(NetworkSystem.NetworkStatus networkStatus, Object obj, byte[] bArr, int i, int i2, int i3, Object obj2) {
        String str = (String) obj2;
        if (a.isDebugEnabled()) {
            a.debug("Read callback");
        }
        if (a.isTraceEnabled()) {
            a.traceBuffer(bArr);
        }
        if (networkStatus != NetworkSystem.NetworkStatus.NETWORK_OK) {
            if (networkStatus == NetworkSystem.NetworkStatus.NETWORK_ERROR) {
                if (this.m != 1 && this.m != 3 && this.m != 6) {
                    this.m = (short) 8;
                    if (a.isErrorEnabled()) {
                        a.error("XMode.readSocketCallback() NETWORK_ERROR");
                    }
                }
                a((byte) 4, (Object) null);
                return;
            }
            if (networkStatus != NetworkSystem.NetworkStatus.NETWORK_MEMORY_ERROR || this.m == 1 || this.m == 3) {
                return;
            }
            this.m = (short) 8;
            if (a.isErrorEnabled()) {
                a.error("XMode.readSocketCallback() NETWORK_MEMORY_ERROR");
                return;
            }
            return;
        }
        if (!str.equals("READ_XMODE_HEADER")) {
            if (str.equals("READ_XMODE_PAYLOAD")) {
                if (i3 == 0) {
                    if (a.isDebugEnabled()) {
                        a.debug(Integer.toHexString(this.l.cmd) + " payload not read bytesRead is 0");
                    }
                    this.b = new TimerSystem.TimerSystemTask() { // from class: com.nuance.nmsp.client.sdk.components.core.XMode.3
                        @Override // java.lang.Runnable
                        public final void run() {
                            try {
                                XMode.this.a((byte) 6, (Object) null);
                            } catch (Exception e) {
                                if (XMode.a.isErrorEnabled()) {
                                    XMode.a.error("XMode.readSocketCallback() " + e.getClass().getName() + XMLResultsHandler.SEP_SPACE + e.getMessage());
                                }
                            }
                        }
                    };
                    this.j.scheduleTask(this.b, 20L);
                    return;
                }
                if (i3 == i2) {
                    if (this.l.len <= i2) {
                        a(this.l, bArr);
                    }
                    a((byte) 5, (Object) null);
                    return;
                } else {
                    if (a.isErrorEnabled()) {
                        a.error("----***---- readSocketCallback fatal error in readSocketCallback NET_CONTEXT_READ_XMODE_PAYLOAD bytesRead:[" + i3 + "] bufferLen:[" + i2 + "]");
                        return;
                    }
                    return;
                }
            }
            return;
        }
        if (i3 == 0) {
            this.b = new TimerSystem.TimerSystemTask() { // from class: com.nuance.nmsp.client.sdk.components.core.XMode.2
                @Override // java.lang.Runnable
                public final void run() {
                    try {
                        XMode.this.a((byte) 5, (Object) null);
                    } catch (Exception e) {
                        if (XMode.a.isErrorEnabled()) {
                            XMode.a.error("XMode.readSocketCallback() " + e.getClass().getName() + XMLResultsHandler.SEP_SPACE + e.getMessage());
                        }
                    }
                }
            };
            this.j.scheduleTask(this.b, 20L);
            return;
        }
        if (i3 != i2) {
            if (a.isErrorEnabled()) {
                a.error("----***---- readSocketCallback fatal error in readSocketCallback NET_CONTEXT_READ_XMODE_HEADER bytesRead:[" + i3 + "] bufferLen:[" + i2 + "]");
                return;
            }
            return;
        }
        this.l = new XModeMsgHeader(bArr);
        if (this.l.len == 0) {
            a(this.l, (byte[]) null);
            a((byte) 5, (Object) null);
        } else if (this.l.len <= 512000 && this.l.len >= 0) {
            a((byte) 6, (Object) null);
        } else {
            this.r.clearPendingOps(obj);
            a((byte) 5, (Object) null);
        }
    }

    public void sendVapRecordEnd(int i) {
        if (a.isDebugEnabled()) {
            a.debug("XMode.sendVapRecordEnd() audio id: " + i);
        }
        if (this.state != 1) {
            return;
        }
        byte[] bArr = new byte[4];
        ByteConversion.intToBytes(i, bArr, 0);
        sendXModeMsg(ProtocolBuilder.buildXModeBuf((byte) 1, ProtocolDefines.XMODE_VERSION_VAP, (short) 256, bArr), 3, "SEND_VAP_RECORD_END");
    }

    public void sendVapRecordMsg(byte[] bArr, int i) {
        if (a.isDebugEnabled()) {
            a.debug("XMode.sendVapRecordMsg() audio id: " + i);
        }
        if (this.state != 1) {
            return;
        }
        int length = bArr.length;
        byte[] bArr2 = new byte[length + 8];
        ByteConversion.intToBytes(i, bArr2, 0);
        ByteConversion.intToBytes(length, bArr2, 4);
        System.arraycopy(bArr, 0, bArr2, 8, length);
        sendXModeMsg(ProtocolBuilder.buildXModeBuf((byte) 1, ProtocolDefines.XMODE_VERSION_VAP, ProtocolDefines.XMODE_VAP_COMMAND_RECORD, bArr2), 3, "SEND_VAP_RECORD");
    }

    public void sendXModeMsg(byte[] bArr, int i, Object obj) {
        a aVar;
        byte[] bArr2;
        if (a.isDebugEnabled()) {
            a.debug("XMode.sendXModeMsg() " + obj + ", buffer.length:" + bArr.length);
        }
        if (this.f != null && this.j.cancelTask(this.f)) {
            this.f = new TimerSystem.TimerSystemTask() { // from class: com.nuance.nmsp.client.sdk.components.core.XMode.1
                @Override // java.lang.Runnable
                public final void run() {
                    try {
                        XMode.a(XMode.this, (short) 3);
                        if (XMode.a.isErrorEnabled()) {
                            XMode.a.error("Session Idle for too long, longer than [" + XMode.this.e + "] ()");
                        }
                        XMode.this.state = (byte) 2;
                        XMode.this.a((byte) 4, (Object) null);
                    } catch (Exception e) {
                        if (XMode.a.isErrorEnabled()) {
                            XMode.a.error("XMode.sendXModeMsg() " + e.getClass().getName() + XMLResultsHandler.SEP_SPACE + e.getMessage());
                        }
                    }
                }
            };
            this.j.scheduleTask(this.f, this.e * 1000);
        }
        if (this.r == null || this.k == null) {
            if (i == 1) {
                this.x.add(new a(this, bArr, obj));
            }
        } else {
            if (i != 3 && i != 2 && (this.w.size() != 0 || this.x.size() != 0)) {
                this.x.add(new a(this, bArr, obj));
                return;
            }
            if (i == 2) {
                a aVar2 = (a) this.x.remove();
                bArr2 = aVar2.a;
                aVar = aVar2;
            } else {
                aVar = new a(this, null, obj);
                bArr2 = bArr;
            }
            this.w.add(aVar);
            this.r.socketWrite(this.k, bArr2, 0, bArr2.length, this, aVar);
        }
    }

    public void setForLogOnly() {
        this.y = true;
    }

    public void startStreaming(int i) {
        if (a.isDebugEnabled()) {
            a.debug("XMode.startStreaming() audio id: " + i);
        }
        if (this.state != 1) {
            return;
        }
        byte[] bArr = new byte[6];
        ByteConversion.intToBytes(i, bArr, 0);
        ByteConversion.shortToBytes(this.inputCodec, bArr, 4);
        sendXModeMsg(ProtocolBuilder.buildXModeBuf((byte) 1, ProtocolDefines.XMODE_VERSION_VAP, (short) 257, bArr), 3, "SEND_VAP_RECORD_BEGIN");
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.NetworkSystem.WriteSocketCallback
    public void writeSocketCallback(NetworkSystem.NetworkStatus networkStatus, Object obj, byte[] bArr, int i, int i2, int i3, Object obj2) {
        int i4 = 0;
        String str = (String) ((a) obj2).b;
        if (networkStatus == NetworkSystem.NetworkStatus.NETWORK_OK && i2 == i3) {
            if (str.equals("SEND_COP_CONNECT")) {
                a((byte) 5, (Object) null);
            } else if (str.equals("SEND_COP_DISCONNECT")) {
                this.r.closeSocket(obj);
            }
            a((byte) 11, str);
            this.w.remove(obj2);
            if (this.w.size() != 0 || this.x.size() <= 0) {
                return;
            }
            a((byte) 14, (Object) null);
            return;
        }
        if (networkStatus == NetworkSystem.NetworkStatus.NETWORK_ERROR) {
            if (this.m != 1 && this.m != 3) {
                this.m = (short) 8;
                if (a.isErrorEnabled()) {
                    a.error("XMode.writeSocketCallback() NETWORK_ERROR");
                }
            }
            a((byte) 12, str);
            this.w.remove(obj2);
            if (this.w.size() == 0) {
                int size = this.x.size();
                while (i4 < size) {
                    a((byte) 12, ((a) this.x.remove()).b);
                    i4++;
                }
                return;
            }
            return;
        }
        if (networkStatus == NetworkSystem.NetworkStatus.NETWORK_MEMORY_ERROR) {
            if (this.m != 1 && this.m != 3) {
                this.m = (short) 8;
                if (a.isErrorEnabled()) {
                    a.error("XMode.writeSocketCallback() NETWORK_MEMORY_ERROR");
                }
            }
            a((byte) 12, str);
            this.w.remove(obj2);
            if (this.w.size() == 0) {
                int size2 = this.x.size();
                while (i4 < size2) {
                    a((byte) 12, ((a) this.x.remove()).b);
                    i4++;
                }
            }
        }
    }
}
