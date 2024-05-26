package com.nuance.nmsp.client.sdk.components.core.internal.calllog;

import com.nuance.nmsp.client.sdk.common.defines.NMSPDefines;
import com.nuance.nmsp.client.sdk.common.oem.api.LogFactory;
import com.nuance.nmsp.client.sdk.common.oem.api.MessageSystem;
import com.nuance.nmsp.client.sdk.common.oem.api.MiscSystem;
import com.nuance.nmsp.client.sdk.common.protocols.ProtocolDefines;
import com.nuance.nmsp.client.sdk.common.util.ByteConversion;
import com.nuance.nmsp.client.sdk.common.util.Checker;
import com.nuance.nmsp.client.sdk.components.core.calllog.SessionEvent;
import com.nuance.nmsp.client.sdk.components.core.calllog.SessionEventAlreadyCommittedException;
import com.nuance.nmsp.client.sdk.components.core.internal.calllog.SessionEventBuilderImpl;
import com.nuance.nmsp.client.sdk.components.general.Parameter;
import com.nuance.nmsp.client.sdk.components.general.ResourceUtil;
import com.nuance.swype.input.BuildInfo;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.Vector;

/* loaded from: classes.dex */
public class CalllogImpl implements MessageSystem.MessageHandler {
    public static final int CALLLOG_CHUNK_SIZE_DEFAULT = 1000000;
    public static final int CALLLOG_CHUNK_SIZE_MINIMUM = 20000;
    public static final int CALLLOG_RETENTION_DAYS_DEFAULT = 7;
    public static final String INTERNAL_SHA1_KEY = "b84798d890d847063ac0145b627a1ecfe4538006";
    private static final LogFactory.Log a = LogFactory.getLog(CalllogImpl.class);
    private SessionEvent d;
    private MessageSystem f;
    private int h;
    private CalllogListener i;
    private final List b = new LinkedList();
    private long c = 0;
    private boolean e = false;
    private byte[] g = null;

    /* loaded from: classes.dex */
    public static class SessionEventData {
        public byte[] data;
        public int requestId;
        public String sessionEventId;
    }

    public CalllogImpl(String str, short s, String str2, byte[] bArr, int i, int i2, String str3, Vector vector, String str4, MessageSystem messageSystem, CalllogListener calllogListener) {
        this.h = 0;
        this.i = null;
        Checker.checkArgForNull("CalllogImpl(uid)", str4);
        a(str, s, bArr, str2, str4, vector);
        this.h = i;
        this.f = messageSystem;
        this.i = calllogListener;
        try {
            this.d = SessionEventBuilderImpl.a(str3 == null ? UUID.randomUUID().toString() : str3, SessionEvent.NMSP_CALLLOG_NMSPSDK_EVENT, BuildInfo.DRAGON_SPEECH_VERSION, i2, this, SessionEvent.NMSP_CALLLOG_NMSPSDK_EVENT).putString("Uid", str4).putString("Nmaid", str2).putString("client_hardware_model", ResourceUtil.getDeviceModel()).putString("client_hardware_submodel", ResourceUtil.getDeviceSubModel()).putString("client_os_type", ResourceUtil.getOSType()).putString("client_os_version", ResourceUtil.getOSVersion()).putString("client_sdk_release", NMSPDefines.VERSION).commit();
        } catch (SessionEventAlreadyCommittedException e) {
        }
    }

    private void a() {
        if (this.b.size() == 0) {
            a.warn("Trying to close with zero events");
        }
        byte[] a2 = a(this.b);
        if (a2 == null) {
            a.error("CalllogImpl.close serialized data is null");
        }
        this.b.clear();
        this.c = 0L;
        this.i.calllogDataGenerated(a2);
    }

    private void a(String str, short s, byte[] bArr, String str2, String str3, List list) {
        byte[] bytes = str.getBytes();
        byte[] bytes2 = str2.getBytes();
        byte[] bytes3 = str3.getBytes();
        byte[] bArr2 = {(byte) s, (byte) (s >> 8)};
        byte[] bArr3 = {(byte) list.size()};
        int i = 0;
        int length = bytes.length + 5 + 4 + 2 + 4 + bytes2.length + 4 + bArr.length + 4 + bytes3.length + 4;
        while (true) {
            int i2 = i;
            if (i2 >= list.size()) {
                ByteBuffer allocate = ByteBuffer.allocate(length);
                try {
                    a(bytes, allocate, true);
                    a(bArr2, allocate, true);
                    a(bytes2, allocate, true);
                    a(bArr, allocate, true);
                    a(bytes3, allocate, true);
                } catch (Exception e) {
                    allocate.clear();
                    if (a.isErrorEnabled()) {
                        a.error("serializeEvents() failed : " + e.getMessage());
                    }
                }
                a(bArr3, allocate, true);
                int i3 = 0;
                while (true) {
                    int i4 = i3;
                    if (i4 >= list.size()) {
                        this.g = allocate.array();
                        return;
                    }
                    Parameter parameter = (Parameter) list.get(i4);
                    try {
                        byte[] bArr4 = {(byte) parameter.getType().getValue(), (byte) (parameter.getType().getValue() >> 8)};
                        a(parameter.getName().getBytes(), allocate, true);
                        a(bArr4, allocate, true);
                        a(parameter.getValue(), allocate, true);
                    } catch (Exception e2) {
                        allocate.clear();
                        if (a.isErrorEnabled()) {
                            a.error("serializeEvents() failed : " + e2.getMessage());
                        }
                    }
                    i3 = i4 + 1;
                }
            } else {
                if (!(list.get(i2) instanceof Parameter)) {
                    return;
                }
                Parameter parameter2 = (Parameter) list.get(i2);
                length = length + 4 + parameter2.getName().length() + 4 + 2 + 4 + parameter2.getValue().length;
                i = i2 + 1;
            }
        }
    }

    private void a(byte[] bArr, ByteBuffer byteBuffer, boolean z) {
        if (z) {
            int length = bArr.length;
            byte[][] bArr2 = {new byte[]{4}, new byte[]{(byte) length, (byte) (length >> 8), (byte) (length >> 16), (byte) (length >>> 24)}};
            byteBuffer.put(bArr2[1], 0, bArr2[1].length);
        }
        byteBuffer.put(bArr, 0, bArr.length);
    }

    private byte[] a(List list) {
        byte[] bArr = new byte[4];
        int length = this.g.length + 28;
        for (int i = 0; i < list.size(); i++) {
            SessionEventData sessionEventData = (SessionEventData) list.get(i);
            length = length + 4 + sessionEventData.data.length + 4 + sessionEventData.sessionEventId.getBytes().length;
        }
        ByteBuffer allocate = ByteBuffer.allocate(length);
        ByteConversion.intToBytes(length, bArr, 0);
        a(bArr, allocate, true);
        allocate.put(new byte[20]);
        a(this.g, allocate, false);
        for (int i2 = 0; i2 < list.size(); i2++) {
            SessionEventData sessionEventData2 = (SessionEventData) list.get(i2);
            if (sessionEventData2.data.length != 0) {
                try {
                    a(sessionEventData2.data, allocate, true);
                    a(sessionEventData2.sessionEventId.getBytes(), allocate, true);
                } catch (Exception e) {
                    allocate.clear();
                    if (a.isErrorEnabled()) {
                        a.error("serializeEvents() failed : " + e.getMessage());
                    }
                }
            }
        }
        byte[] HMAC_SHA1 = MiscSystem.HMAC_SHA1(INTERNAL_SHA1_KEY.getBytes(), allocate.array(), 28);
        byte[] array = allocate.array();
        System.arraycopy(HMAC_SHA1, 0, array, 8, HMAC_SHA1.length);
        return array;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void a(SessionEventImpl sessionEventImpl, SessionEvent.SessionEventCommittedListener sessionEventCommittedListener) {
        if (this.e) {
            return;
        }
        this.f.send(new MessageSystem.MessageData(ProtocolDefines.XMODE_SERVER_SESSION_UUID_SIZE, new Object[]{sessionEventImpl, sessionEventCommittedListener}), this, Thread.currentThread(), this.f.getVRAddr()[0]);
    }

    public void dispose() {
        if (a.isDebugEnabled()) {
            a.debug("onSessionDisconnected()");
        }
        this.e = true;
    }

    public void flushLogsToListener() {
        this.f.send(new MessageSystem.MessageData((byte) 21, null), this, Thread.currentThread(), this.f.getVRAddr()[0]);
    }

    public void genSeqId(SessionEventImpl sessionEventImpl, SessionEventBuilderImpl.GenSeqIdCallback genSeqIdCallback) {
        this.f.send(new MessageSystem.MessageData((byte) 20, new Object[]{sessionEventImpl, genSeqIdCallback}), this, Thread.currentThread(), this.f.getVRAddr()[0]);
    }

    public String getRootParentId() {
        return ((RootSessionEventImpl) this.d).getRootParentId();
    }

    public SessionEvent getRootSessionEvent() {
        return this.d;
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.MessageSystem.MessageHandler
    public void handleMessage(Object obj, Object obj2) {
        MessageSystem.MessageData messageData = (MessageSystem.MessageData) obj;
        switch (messageData.command) {
            case 16:
                Object[] objArr = (Object[]) messageData.data;
                SessionEventImpl sessionEventImpl = (SessionEventImpl) objArr[0];
                SessionEvent.SessionEventCommittedListener sessionEventCommittedListener = (SessionEvent.SessionEventCommittedListener) objArr[1];
                SessionEventData sessionEventData = new SessionEventData();
                sessionEventData.data = sessionEventImpl.getBinaryFormat();
                sessionEventData.sessionEventId = sessionEventImpl.a.getRootParentId() + sessionEventImpl.c;
                if (this.c + 4 + 4 + this.g.length + 4 + sessionEventData.data.length + 4 + sessionEventData.sessionEventId.getBytes().length > this.h) {
                    a();
                }
                this.b.add(sessionEventData);
                this.c += sessionEventData.data.length;
                this.c += 4;
                this.c = sessionEventData.sessionEventId.getBytes().length + this.c;
                this.c += 4;
                if (sessionEventCommittedListener != null) {
                    sessionEventCommittedListener.committed(sessionEventImpl);
                    return;
                }
                return;
            case 17:
            case 18:
            case 19:
            default:
                return;
            case 20:
                Object[] objArr2 = (Object[]) messageData.data;
                SessionEventImpl sessionEventImpl2 = (SessionEventImpl) objArr2[0];
                SessionEventBuilderImpl.GenSeqIdCallback genSeqIdCallback = (SessionEventBuilderImpl.GenSeqIdCallback) objArr2[1];
                sessionEventImpl2.genSeqId();
                if (genSeqIdCallback != null) {
                    if (sessionEventImpl2 instanceof RemoteSessionEventImpl) {
                        genSeqIdCallback.seqIdGenerated(sessionEventImpl2, ((RemoteSessionEventImpl) sessionEventImpl2).a());
                        return;
                    } else {
                        genSeqIdCallback.seqIdGenerated(sessionEventImpl2, null);
                        return;
                    }
                }
                return;
            case 21:
                a();
                break;
            case 22:
                break;
        }
        this.i.calllogDataGenerated(null);
    }

    public void stopCalllog() {
        this.f.send(new MessageSystem.MessageData((byte) 22, null), this, Thread.currentThread(), this.f.getVRAddr()[0]);
    }
}
