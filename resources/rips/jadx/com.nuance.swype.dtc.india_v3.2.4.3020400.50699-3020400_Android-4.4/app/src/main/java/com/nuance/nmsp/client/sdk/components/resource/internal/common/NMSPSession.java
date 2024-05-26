package com.nuance.nmsp.client.sdk.components.resource.internal.common;

import com.nuance.connect.comm.MessageAPI;
import com.nuance.nmsp.client.sdk.common.defines.NMSPDefines;
import com.nuance.nmsp.client.sdk.common.oem.api.LogFactory;
import com.nuance.nmsp.client.sdk.common.oem.api.MessageSystem;
import com.nuance.nmsp.client.sdk.common.protocols.ProtocolBuilder;
import com.nuance.nmsp.client.sdk.common.protocols.ProtocolDefines;
import com.nuance.nmsp.client.sdk.common.protocols.XModeMsgHeader;
import com.nuance.nmsp.client.sdk.common.util.ByteConversion;
import com.nuance.nmsp.client.sdk.common.util.Util;
import com.nuance.nmsp.client.sdk.components.core.XMode;
import com.nuance.nmsp.client.sdk.components.core.calllog.SessionEvent;
import com.nuance.nmsp.client.sdk.components.core.calllog.SessionEventAlreadyCommittedException;
import com.nuance.nmsp.client.sdk.components.core.calllog.SessionEventBuilder;
import com.nuance.nmsp.client.sdk.components.core.internal.calllog.CalllogImpl;
import com.nuance.nmsp.client.sdk.components.general.NMSPAudioSink;
import com.nuance.nmsp.client.sdk.components.general.TransactionProcessingException;
import com.nuance.nmsp.client.sdk.components.resource.common.ManagerListener;
import com.nuance.nmsp.client.sdk.components.resource.common.Resource;
import com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.xmlResults.XMLResultsHandler;
import com.nuance.swype.input.R;
import java.io.ByteArrayOutputStream;
import java.util.Hashtable;
import java.util.Vector;

/* loaded from: classes.dex */
public class NMSPSession implements MessageSystem.MessageHandler, XMode.XModeListener {
    private static final LogFactory.Log a = LogFactory.getLog(NMSPSession.class);
    private static byte[] m = new byte[16];
    private static int p = 1;
    private static String[] x = {"0", "1", MessageAPI.DELAYED_FROM, MessageAPI.SESSION_ID, MessageAPI.TRANSACTION_ID, MessageAPI.DEVICE_ID, "6", MessageAPI.MESSAGE, "8", MessageAPI.PROPERTIES_TO_VALIDATE, "A", "B", "C", "D", "E", "F"};
    private XMode e;
    private String f;
    private short g;
    private MessageSystem i;
    private ManagerListener j;
    private NMSPDefines.Codec n;
    private NMSPDefines.Codec o;
    private long q;
    private SessionEvent v;
    private CalllogImpl w;
    private byte[] l = null;
    private boolean r = false;
    private long t = 1;
    private byte u = 1;
    private Resource s = null;
    private Vector k = new Vector();
    private Hashtable b = new Hashtable();
    private Hashtable c = new Hashtable();
    private Hashtable d = new Hashtable();
    private Vector h = new Vector();

    public NMSPSession(String str, short s, String str2, byte[] bArr, String str3, Vector vector, MessageSystem messageSystem, ManagerListener managerListener) {
        this.f = str;
        this.g = s;
        this.i = messageSystem;
        this.j = managerListener;
        this.e = new XMode(this.f, this.g, str2, bArr, str3, this, vector, messageSystem);
    }

    public static String FormatUuid(byte[] bArr) {
        if (bArr == null) {
            return "";
        }
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < 16; i++) {
            byte b = bArr[i];
            stringBuffer.append(x[(byte) (((byte) (((byte) (b & 240)) >>> 4)) & ProtocolDefines.XMODE_PROTOCOL_BB_HANDSHAKE)] + x[(byte) (b & ProtocolDefines.XMODE_PROTOCOL_BB_HANDSHAKE)]);
            if (i == 3 || i == 5 || i == 7 || i == 9) {
                stringBuffer.append(XMLResultsHandler.SEP_HYPHEN);
            }
        }
        return stringBuffer.toString().toLowerCase();
    }

    private void a() {
        while (!this.k.isEmpty()) {
            MessageSystem.MessageData messageData = (MessageSystem.MessageData) this.k.firstElement();
            this.k.removeElementAt(0);
            switch (messageData.command) {
                case 1:
                    a((byte) 1, messageData.data);
                    break;
                case 2:
                    a((byte) 2, messageData.data);
                    break;
                case 4:
                    a((byte) 4, messageData.data);
                    break;
                case 5:
                    a((byte) 5, messageData.data);
                    break;
                case 6:
                    a((byte) 6, messageData.data);
                    break;
                case 7:
                    a((byte) 7, messageData.data);
                    break;
                case 8:
                    a((byte) 8, messageData.data);
                    break;
            }
        }
    }

    private void a(byte b, Object obj) {
        this.i.send(new MessageSystem.MessageData(b, obj), this, Thread.currentThread(), this.i.getVRAddr()[0]);
    }

    private void a(String str) {
        if (this.v != null) {
            SessionEventBuilder createChildEventBuilder = this.v.createChildEventBuilder(str);
            if (str.compareTo(SessionEvent.NMSP_CALLLOG_CONNECTED_EVENT) == 0) {
                try {
                    createChildEventBuilder.putString("SessionID", FormatUuid(this.l));
                } catch (SessionEventAlreadyCommittedException e) {
                }
            }
            createChildEventBuilder.commit();
        }
    }

    private void a(byte[] bArr) {
        byte b = bArr[16];
        short bytesToShort = ByteConversion.bytesToShort(bArr, 17);
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 >= this.h.size()) {
                return;
            }
            ((NMSPSessionListener) this.h.elementAt(i2)).onBcpEvent(b, bytesToShort);
            i = i2 + 1;
        }
    }

    private void b(byte[] bArr) {
        int bytesToInt = ByteConversion.bytesToInt(bArr, 0);
        if (this.c.size() == 0) {
            return;
        }
        NMSPAudioSink nMSPAudioSink = (NMSPAudioSink) this.c.get(new Integer(bytesToInt));
        if (nMSPAudioSink == null) {
            a.error("Could not find the audio sink associated with AID: " + bytesToInt);
            return;
        }
        int bytesToInt2 = ByteConversion.bytesToInt(bArr, 4);
        int i = 8;
        if (Util.isSpeexCodec(this.o) || Util.isOpusCodec(this.o)) {
            while ((bArr[i] & 128) > 0) {
                i++;
                bytesToInt2--;
            }
            i++;
            bytesToInt2--;
        }
        if (bytesToInt2 > 0 && bytesToInt2 <= bArr.length - i) {
            try {
                nMSPAudioSink.addAudioBuf(bArr, i, bytesToInt2, false);
            } catch (TransactionProcessingException e) {
                a.error(e.getMessage());
            }
        }
        if (this.d.size() != 0) {
            NMSPSessionListener nMSPSessionListener = (NMSPSessionListener) this.d.get(new Integer(bytesToInt));
            if (nMSPSessionListener == null) {
                a.error("parseXModeMsgVapPlay:: Could not find the session listener associated with AID: " + bytesToInt);
            } else {
                nMSPSessionListener.onVapPlayReceived();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void a(CalllogImpl calllogImpl) {
        this.w = calllogImpl;
    }

    public void addAudioSink(int i, NMSPAudioSink nMSPAudioSink, NMSPSessionListener nMSPSessionListener) {
        this.c.put(new Integer(i), nMSPAudioSink);
        this.d.put(new Integer(i), nMSPSessionListener);
    }

    public void connect(NMSPDefines.Codec codec, NMSPDefines.Codec codec2) {
        if (a.isDebugEnabled()) {
            a.debug("connect()");
        }
        createSessionEvent(this.w);
        a(SessionEvent.NMSP_CALLLOG_CONNECT_EVENT);
        this.n = codec;
        this.o = codec2;
        a((byte) 1, null);
    }

    @Override // com.nuance.nmsp.client.sdk.components.core.XMode.XModeListener
    public void copConnected() {
    }

    public void createSessionEvent(CalllogImpl calllogImpl) {
        SessionEvent rootSessionEvent;
        a.debug("Creating new SDK calllog tree");
        if (this.w == null || (rootSessionEvent = this.w.getRootSessionEvent()) == null) {
            return;
        }
        this.v = rootSessionEvent.createChildEventBuilder(SessionEvent.NMSP_CALLLOG_NMSPSESSION).commit();
        this.e.createSessionEvent(this.v);
    }

    public void disconnect() {
        if (a.isDebugEnabled()) {
            a.debug("disconnect");
        }
        a((byte) 2, null);
    }

    public void disconnectAndShutdown() {
        if (a.isDebugEnabled()) {
            a.debug("disconnectAndShutdown");
        }
        a((byte) 3, null);
    }

    public void freeResource(byte b, int i, NMSPSessionListener nMSPSessionListener) {
        a.debug("freeResource, TID: " + ((int) b) + ", disconnect timeout: " + i);
        this.h.removeElement(nMSPSessionListener);
        Object[] objArr = {new Byte(b), new Integer(i)};
        if (this.l == null || !this.k.isEmpty()) {
            this.k.addElement(new MessageSystem.MessageData((byte) 4, objArr));
        } else {
            a((byte) 4, objArr);
        }
    }

    public NMSPDefines.Codec getInputCodec() {
        return this.n;
    }

    public Vector getMsgQueue() {
        return this.k;
    }

    public MessageSystem getMsgSystem() {
        return this.i;
    }

    public synchronized int getNewAudioId() {
        int i;
        i = p;
        p = i + 1;
        if (p == Integer.MIN_VALUE) {
            p = 1;
        }
        return i;
    }

    public long getResourceId() {
        long j = this.t;
        this.t = j + 1;
        if (this.t == Long.MIN_VALUE) {
            this.t = 1L;
        }
        return j;
    }

    public SessionEvent getSessionEvent() {
        return this.v;
    }

    public byte[] getSessionId() {
        return this.l;
    }

    public byte getTransactionId() {
        byte b = this.u;
        this.u = (byte) (b + 1);
        if (this.u == Byte.MIN_VALUE) {
            this.u = (byte) 1;
        }
        return b;
    }

    public XMode getXmode() {
        return this.e;
    }

    public void handleDisconnect() {
        this.e.disconnect();
    }

    public void handleDisconnectAndShutdown() {
        this.r = true;
        this.e.disconnect();
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.MessageSystem.MessageHandler
    public void handleMessage(Object obj, Object obj2) {
        MessageSystem.MessageData messageData = (MessageSystem.MessageData) obj;
        switch (messageData.command) {
            case 1:
                this.e.connect(this.n.getValue(), this.o.getValue());
                return;
            case 2:
                handleDisconnect();
                return;
            case 3:
                handleDisconnectAndShutdown();
                return;
            case 4:
                if (this.l != null) {
                    Object[] objArr = (Object[]) messageData.data;
                    byte byteValue = ((Byte) objArr[0]).byteValue();
                    int intValue = ((Integer) objArr[1]).intValue();
                    byte[] bArr = new byte[5];
                    bArr[0] = byteValue;
                    ByteConversion.intToBytes(intValue, bArr, 1);
                    this.e.sendXModeMsg(ProtocolBuilder.buildXModeBuf((byte) 2, ProtocolDefines.XMODE_VERSION_BCP, ProtocolDefines.XMODE_BCP_COMMAND_FREE_RESOURCE, ProtocolBuilder.appendBCPSessionUUID(bArr, this.l)), 3, XMode.NET_CONTEXT_SEND_BCP_FREE_RESOURCE);
                    return;
                }
                return;
            case 5:
                if (this.l != null) {
                    this.e.startStreaming(((Integer) messageData.data).intValue());
                    return;
                }
                return;
            case 6:
                if (this.l == null) {
                    return;
                }
                Object[] objArr2 = (Object[]) messageData.data;
                byte[] bArr2 = (byte[]) objArr2[0];
                int intValue2 = ((Integer) objArr2[1]).intValue();
                int i = 0;
                while (true) {
                    int i2 = i;
                    if (i2 >= this.h.size()) {
                        this.e.sendVapRecordMsg(bArr2, intValue2);
                        return;
                    } else {
                        ((NMSPSessionListener) this.h.elementAt(i2)).onVapSending();
                        a.debug("onVapSending is called to reset command time out task");
                        i = i2 + 1;
                    }
                }
            case 7:
                if (this.l != null) {
                    this.e.sendVapRecordEnd(((Integer) messageData.data).intValue());
                    return;
                }
                return;
            case 8:
                if (this.l != null) {
                    Object[] objArr3 = (Object[]) messageData.data;
                    short shortValue = ((Short) objArr3[0]).shortValue();
                    String str = (String) objArr3[1];
                    byte[] bArr3 = (byte[]) objArr3[2];
                    byte[] bArr4 = (byte[]) objArr3[3];
                    byte byteValue2 = ((Byte) objArr3[4]).byteValue();
                    long longValue = ((Long) objArr3[5]).longValue();
                    NMSPSessionListener nMSPSessionListener = (NMSPSessionListener) objArr3[6];
                    boolean booleanValue = ((Boolean) objArr3[7]).booleanValue();
                    this.b.put(new Long(longValue), nMSPSessionListener);
                    int length = bArr3.length + 5;
                    if (shortValue == 2585) {
                        length += 4;
                    }
                    byte[] bArr5 = new byte[length];
                    bArr5[0] = byteValue2;
                    int i3 = 1;
                    if (shortValue == 2585) {
                        ByteConversion.intToBytes((int) longValue, bArr5, 1);
                        i3 = 5;
                    }
                    ByteConversion.intToBytes(bArr3.length, bArr5, i3);
                    System.arraycopy(bArr3, 0, bArr5, i3 + 4, bArr3.length);
                    byte[] appendBCPSessionUUID = ProtocolBuilder.appendBCPSessionUUID(bArr5, this.l != null ? this.l : m);
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    byteArrayOutputStream.write(appendBCPSessionUUID, 0, appendBCPSessionUUID.length);
                    if (bArr4 != null) {
                        byteArrayOutputStream.write(bArr4, 0, bArr4.length);
                    }
                    this.e.sendXModeMsg(ProtocolBuilder.buildXModeBuf((byte) 2, ProtocolDefines.XMODE_VERSION_BCP, shortValue, byteArrayOutputStream.toByteArray()), 3, str);
                    if (booleanValue) {
                        if (shortValue == 2581) {
                            if (nMSPSessionListener != null) {
                                nMSPSessionListener.onBcpSetParamsComplete(byteValue2, longValue, (short) 200, null);
                                return;
                            }
                            return;
                        } else {
                            if (shortValue != 2608 || nMSPSessionListener == null) {
                                return;
                            }
                            this.h.removeElement(nMSPSessionListener);
                            nMSPSessionListener.onBcpFreeResourceId();
                            return;
                        }
                    }
                    return;
                }
                return;
            default:
                a.error("Unknown command");
                return;
        }
    }

    public boolean isNetworkHealthy() {
        return this.e.isNetworkHealthy();
    }

    public void postBcpMessage(short s, String str, byte[] bArr, byte[] bArr2, byte b, long j, NMSPSessionListener nMSPSessionListener, boolean z) {
        if (a.isDebugEnabled()) {
            a.debug("postBcpMessage, BCP: " + ((int) s) + ", TID: " + ((int) b) + ", RID: " + j);
        }
        Object[] objArr = {new Short(s), str, bArr, bArr2, new Byte(b), new Long(j), nMSPSessionListener, new Boolean(z)};
        if (this.l == null || !this.k.isEmpty()) {
            this.k.addElement(new MessageSystem.MessageData((byte) 8, objArr));
        } else {
            a((byte) 8, objArr);
        }
    }

    public void removeSessionListener(NMSPSessionListener nMSPSessionListener) {
        this.h.removeElement(nMSPSessionListener);
    }

    public void setDefaultReqId(long j) {
        this.q = j;
    }

    public void setResource(Resource resource) {
        this.s = resource;
    }

    public void setSessionListener(NMSPSessionListener nMSPSessionListener) {
        if (this.h.contains(nMSPSessionListener)) {
            return;
        }
        this.h.addElement(nMSPSessionListener);
    }

    @Override // com.nuance.nmsp.client.sdk.components.core.XMode.XModeListener
    public void socketClosed(short s, short s2) {
        if (a.isDebugEnabled()) {
            a.debug("socketClosed, reason: " + ((int) s));
        }
        if (a.isDebugEnabled()) {
            a.debug("socketClosed() sessionListeners.size():" + this.h.size());
        }
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 >= this.h.size()) {
                break;
            }
            ((NMSPSessionListener) this.h.elementAt(i2)).onSessionDisconnected(s);
            i = i2 + 1;
        }
        if (!this.k.isEmpty()) {
            this.k.removeAllElements();
        }
        this.h.removeAllElements();
        if (s == 4 || s == 5) {
            this.j.connectionFailed(this.s, (short) 9);
        } else if (s == 7) {
            this.j.connectionFailed(this.s, s2);
        } else if (s == 8 && this.l == null) {
            this.j.connectionFailed(this.s, (short) 9);
        } else if (this.l != null) {
            this.j.disconnected(this.s, s2);
        }
        if (s == 1 && this.r) {
            this.i.stop();
            this.j.shutdownCompleted();
        }
        this.l = null;
    }

    @Override // com.nuance.nmsp.client.sdk.components.core.XMode.XModeListener
    public void socketOpened() {
    }

    public void vapRecord(byte[] bArr, int i) {
        if (a.isDebugEnabled()) {
            a.debug("vapRecord, AID: " + i);
        }
        Object[] objArr = {bArr, new Integer(i)};
        if (this.l == null || !this.k.isEmpty()) {
            this.k.addElement(new MessageSystem.MessageData((byte) 6, objArr));
        } else {
            a((byte) 6, objArr);
        }
    }

    public void vapRecordBegin(int i) {
        if (a.isDebugEnabled()) {
            a.debug("vapRecordBegin, AID: " + i);
        }
        Integer num = new Integer(i);
        if (this.l == null || !this.k.isEmpty()) {
            this.k.addElement(new MessageSystem.MessageData((byte) 5, num));
        } else {
            a((byte) 5, num);
        }
    }

    public void vapRecordEnd(int i) {
        if (a.isDebugEnabled()) {
            a.debug("vapRecordEnd, AID: " + i);
        }
        Integer num = new Integer(i);
        if (this.l == null || !this.k.isEmpty()) {
            this.k.addElement(new MessageSystem.MessageData((byte) 7, num));
        } else {
            a((byte) 7, num);
        }
    }

    @Override // com.nuance.nmsp.client.sdk.components.core.XMode.XModeListener
    public void xmodeMsgCallback(XModeMsgHeader xModeMsgHeader, byte[] bArr) {
        int i = 0;
        if (a.isDebugEnabled()) {
            a.debug("xmodeMsgCallback, protocol: " + ((int) xModeMsgHeader.protocol) + ", command: " + ((int) xModeMsgHeader.cmd));
        }
        switch (xModeMsgHeader.protocol) {
            case 1:
                switch (xModeMsgHeader.cmd) {
                    case 512:
                        b(bArr);
                        return;
                    case 528:
                        int bytesToInt = ByteConversion.bytesToInt(bArr, 0);
                        if (this.d.size() != 0) {
                            NMSPSessionListener nMSPSessionListener = (NMSPSessionListener) this.d.get(new Integer(bytesToInt));
                            if (nMSPSessionListener == null) {
                                a.error("parseVapPlayBegin:: Could not find the session listener associated with AID: " + bytesToInt);
                                return;
                            } else {
                                nMSPSessionListener.onVapPlayBeginReceived();
                                return;
                            }
                        }
                        return;
                    case 1024:
                        int bytesToInt2 = ByteConversion.bytesToInt(bArr, 0);
                        if (this.c.size() != 0) {
                            NMSPAudioSink nMSPAudioSink = (NMSPAudioSink) this.c.remove(new Integer(bytesToInt2));
                            if (nMSPAudioSink == null) {
                                a.error("Could not find the audio sink associated with AID: " + bytesToInt2);
                                return;
                            }
                            try {
                                nMSPAudioSink.addAudioBuf(null, 0, 0, true);
                            } catch (TransactionProcessingException e) {
                                a.error(e.getMessage());
                            }
                            NMSPSessionListener nMSPSessionListener2 = (NMSPSessionListener) this.d.remove(new Integer(bytesToInt2));
                            if (nMSPSessionListener2 == null) {
                                a.error("parseXModeMsgVapPlayEnd:: Could not find the session listener associated with AID: " + bytesToInt2);
                                return;
                            } else {
                                nMSPSessionListener2.onVapPlayEndReceived();
                                return;
                            }
                        }
                        return;
                    default:
                        return;
                }
            case 2:
                switch (xModeMsgHeader.cmd) {
                    case 2576:
                        byte b = bArr[16];
                        int bytesToInt3 = ByteConversion.bytesToInt(bArr, 17);
                        short bytesToShort = ByteConversion.bytesToShort(bArr, 21);
                        short bytesToShort2 = ByteConversion.bytesToShort(bArr, 23);
                        short bytesToShort3 = ByteConversion.bytesToShort(bArr, 25);
                        NMSPSessionListener nMSPSessionListener3 = (NMSPSessionListener) this.b.get(new Long(bytesToInt3));
                        if (nMSPSessionListener3 != null) {
                            nMSPSessionListener3.onBcpResponse(b, bytesToInt3, bytesToShort, bytesToShort2, bytesToShort3);
                            if (bytesToShort != 200) {
                                this.b.remove(new Long(bytesToInt3));
                                return;
                            }
                            return;
                        }
                        return;
                    case 2577:
                        byte b2 = bArr[16];
                        long bytesToInt4 = ByteConversion.bytesToInt(bArr, 17);
                        long bytesToInt5 = ByteConversion.bytesToInt(bArr, 21);
                        short bytesToShort4 = ByteConversion.bytesToShort(bArr, 25);
                        NMSPSessionListener nMSPSessionListener4 = (NMSPSessionListener) this.b.remove(new Long(bytesToInt4));
                        if (nMSPSessionListener4 != null) {
                            nMSPSessionListener4.onBcpGenerateAudioComplete(b2, bytesToInt4, bytesToInt5, bytesToShort4);
                            return;
                        }
                        return;
                    case 2578:
                        return;
                    case 2579:
                        byte[] bArr2 = null;
                        byte b3 = bArr[16];
                        long bytesToInt6 = ByteConversion.bytesToInt(bArr, 17);
                        short bytesToShort5 = ByteConversion.bytesToShort(bArr, 21);
                        int bytesToInt7 = ByteConversion.bytesToInt(bArr, 23);
                        if (bytesToInt7 > 0 && bytesToInt7 <= bArr.length - 27) {
                            bArr2 = new byte[bytesToInt7];
                            System.arraycopy(bArr, 27, bArr2, 0, bytesToInt7);
                        }
                        NMSPSessionListener nMSPSessionListener5 = (NMSPSessionListener) this.b.remove(new Long(bytesToInt6));
                        if (nMSPSessionListener5 != null) {
                            nMSPSessionListener5.onBcpRecognitionComplete(b3, bytesToInt6, bytesToShort5, bArr2);
                            return;
                        }
                        return;
                    case 2580:
                        byte[] bArr3 = null;
                        byte b4 = bArr[16];
                        long bytesToInt8 = ByteConversion.bytesToInt(bArr, 17);
                        short bytesToShort6 = ByteConversion.bytesToShort(bArr, 21);
                        int bytesToInt9 = ByteConversion.bytesToInt(bArr, 23);
                        if (bytesToInt9 > 0 && bytesToInt9 <= bArr.length - 27) {
                            bArr3 = new byte[bytesToInt9];
                            System.arraycopy(bArr, 27, bArr3, 0, bytesToInt9);
                        }
                        NMSPSessionListener nMSPSessionListener6 = (NMSPSessionListener) this.b.get(new Long(bytesToInt8));
                        if (nMSPSessionListener6 != null) {
                            nMSPSessionListener6.onBcpRecognitionIntermediateResults(b4, bytesToInt8, bytesToShort6, bArr3);
                            return;
                        }
                        return;
                    case 2582:
                        byte[] bArr4 = null;
                        byte b5 = bArr[16];
                        int bytesToInt10 = ByteConversion.bytesToInt(bArr, 17);
                        short bytesToShort7 = ByteConversion.bytesToShort(bArr, 21);
                        int bytesToInt11 = ByteConversion.bytesToInt(bArr, 23);
                        if (bytesToInt11 > 0 && bytesToInt11 <= bArr.length - 27) {
                            bArr4 = new byte[bytesToInt11];
                            System.arraycopy(bArr, 27, bArr4, 0, bytesToInt11);
                        }
                        NMSPSessionListener nMSPSessionListener7 = (NMSPSessionListener) this.b.remove(new Long(bytesToInt10));
                        if (nMSPSessionListener7 != null) {
                            nMSPSessionListener7.onBcpSetParamsComplete(b5, bytesToInt10, bytesToShort7, bArr4);
                            return;
                        }
                        return;
                    case 2584:
                        byte[] bArr5 = null;
                        byte b6 = bArr[16];
                        int bytesToInt12 = ByteConversion.bytesToInt(bArr, 17);
                        short bytesToShort8 = ByteConversion.bytesToShort(bArr, 21);
                        int bytesToInt13 = ByteConversion.bytesToInt(bArr, 23);
                        if (bytesToInt13 > 0 && bytesToInt13 <= bArr.length - 27) {
                            bArr5 = new byte[bytesToInt13];
                            System.arraycopy(bArr, 27, bArr5, 0, bytesToInt13);
                        }
                        NMSPSessionListener nMSPSessionListener8 = (NMSPSessionListener) this.b.remove(new Long(bytesToInt12));
                        if (nMSPSessionListener8 != null) {
                            nMSPSessionListener8.onBcpGetParamsComplete(b6, bytesToInt12, bytesToShort8, bArr5);
                            return;
                        }
                        return;
                    case 2585:
                        byte b7 = bArr[16];
                        int bytesToInt14 = ByteConversion.bytesToInt(bArr, 21);
                        if (bytesToInt14 <= 0 || bytesToInt14 > bArr.length - 25) {
                            return;
                        }
                        byte[] bArr6 = new byte[bytesToInt14];
                        System.arraycopy(bArr, 25, bArr6, 0, bytesToInt14);
                        NMSPSessionListener nMSPSessionListener9 = (NMSPSessionListener) this.b.get(new Long(this.q));
                        if (nMSPSessionListener9 != null) {
                            nMSPSessionListener9.onBcpData(b7, this.q, bArr6);
                            return;
                        }
                        return;
                    case 2600:
                        a(bArr);
                        return;
                    default:
                        a.error("Unknown BCP command");
                        return;
                }
            case 3:
                switch (xModeMsgHeader.cmd) {
                    case R.styleable.ThemeTemplate_chineseFunctionBarEmoji /* 257 */:
                        this.l = this.e.sessionId;
                        if (a.isDebugEnabled()) {
                            a.debug("connected(" + FormatUuid(this.l) + ") called on " + this.j);
                        }
                        a(SessionEvent.NMSP_CALLLOG_CONNECTED_EVENT);
                        this.j.connected(FormatUuid(this.l), this.s);
                        while (true) {
                            int i2 = i;
                            if (i2 >= this.h.size()) {
                                a();
                                return;
                            } else {
                                ((NMSPSessionListener) this.h.elementAt(i2)).onSessionConnected(this.l);
                                i = i2 + 1;
                            }
                        }
                    case 512:
                    case 768:
                    default:
                        return;
                }
            default:
                a.error("Unknown Xmode protocol");
                return;
        }
    }

    @Override // com.nuance.nmsp.client.sdk.components.core.XMode.XModeListener
    public void xmodeMsgNotSent(String str, Object obj) {
    }

    @Override // com.nuance.nmsp.client.sdk.components.core.XMode.XModeListener
    public void xmodeMsgSent(String str, Object obj) {
    }
}
