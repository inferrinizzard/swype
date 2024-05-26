package com.nuance.nmsp.client.sdk.components.core.internal.calllog;

import com.nuance.nmsp.client.sdk.common.oem.api.LogFactory;
import com.nuance.nmsp.client.sdk.common.oem.api.MessageSystem;
import com.nuance.nmsp.client.sdk.common.oem.api.MiscSystem;
import com.nuance.nmsp.client.sdk.common.protocols.ProtocolBuilder;
import com.nuance.nmsp.client.sdk.common.protocols.ProtocolDefines;
import com.nuance.nmsp.client.sdk.common.protocols.XModeMsgHeader;
import com.nuance.nmsp.client.sdk.common.util.ByteConversion;
import com.nuance.nmsp.client.sdk.components.core.XMode;
import com.nuance.nmsp.client.sdk.components.core.calllog.CalllogSender;
import com.nuance.nmsp.client.sdk.components.core.internal.calllog.CalllogImpl;
import com.nuance.nmsp.client.sdk.components.general.Parameter;
import com.nuance.nmsp.client.sdk.oem.MessageSystemOEM;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Vector;

/* loaded from: classes.dex */
public class CalllogSenderImpl implements MessageSystem.MessageHandler, XMode.XModeListener, CalllogSender {
    private static final LogFactory.Log a = LogFactory.getLog(CalllogSenderImpl.class);
    private CalllogSender.SenderListener c;
    private final MessageSystem b = new MessageSystemOEM();
    private Queue d = new LinkedList();
    private XMode e = null;
    private final List f = new LinkedList();
    private int g = 1;
    private String h = null;
    private short i = 0;
    private String j = null;
    private byte[] k = null;
    private Vector l = null;
    private String m = null;
    private int n = 1;
    private int o = 0;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.nuance.nmsp.client.sdk.components.core.internal.calllog.CalllogSenderImpl$1, reason: invalid class name */
    /* loaded from: classes.dex */
    public static /* synthetic */ class AnonymousClass1 {
        public int a;

        private AnonymousClass1(CalllogSenderImpl calllogSenderImpl) {
            this.a = 0;
        }

        /* synthetic */ AnonymousClass1(CalllogSenderImpl calllogSenderImpl, byte b) {
            this(calllogSenderImpl);
        }
    }

    public CalllogSenderImpl(CalllogSender.SenderListener senderListener) {
        this.c = null;
        this.c = senderListener;
    }

    private List a(byte[] bArr) {
        LinkedList linkedList = new LinkedList();
        AnonymousClass1 anonymousClass1 = new AnonymousClass1(this, 0 == true ? 1 : 0);
        byte[] bArr2 = new byte[20];
        this.l = new Vector();
        int bytesToInt = ByteConversion.bytesToInt(a(bArr, anonymousClass1), 0);
        System.arraycopy(bArr, 8, bArr2, 0, 20);
        byte[] HMAC_SHA1 = MiscSystem.HMAC_SHA1(CalllogImpl.INTERNAL_SHA1_KEY.getBytes(), bArr, 28);
        anonymousClass1.a += 20;
        for (int i = 0; i < HMAC_SHA1.length; i++) {
            if (HMAC_SHA1[i] != bArr2[i]) {
                if (a.isErrorEnabled()) {
                    a.error("unserializeEvents() hashes do not match");
                }
                return null;
            }
        }
        try {
            this.h = new String(a(bArr, anonymousClass1));
            this.i = ByteBuffer.wrap(a(bArr, anonymousClass1)).order(ByteOrder.LITTLE_ENDIAN).getShort();
            this.j = new String(a(bArr, anonymousClass1));
            this.k = a(bArr, anonymousClass1);
            this.m = new String(a(bArr, anonymousClass1));
            byte[] a2 = a(bArr, anonymousClass1);
            for (int i2 = 0; i2 < a2[0]; i2++) {
                String str = new String(a(bArr, anonymousClass1));
                a(bArr, anonymousClass1);
                this.l.add(new Parameter(str, a(bArr, anonymousClass1), Parameter.Type.SDK));
            }
            while (anonymousClass1.a < bytesToInt) {
                CalllogImpl.SessionEventData sessionEventData = new CalllogImpl.SessionEventData();
                sessionEventData.data = a(bArr, anonymousClass1);
                sessionEventData.sessionEventId = new String(a(bArr, anonymousClass1));
                linkedList.add(sessionEventData);
            }
            return linkedList;
        } catch (Exception e) {
            if (a.isErrorEnabled()) {
                a.error("unserializeEvents() failed!!! : " + e.getLocalizedMessage());
            }
            return null;
        }
    }

    private void a() {
        if (this.o == 0 && this.d.size() > 0) {
            this.f.addAll(a((byte[]) this.d.peek()));
            this.o = this.f.size();
        }
        if (a.isDebugEnabled()) {
            a.debug("trySendingLogs() , _events.size()" + this.f.size());
        }
        while (this.f.size() != 0) {
            CalllogImpl.SessionEventData sessionEventData = (CalllogImpl.SessionEventData) this.f.remove(0);
            if (a.isDebugEnabled()) {
                a.debug("bcpLog, packet len [" + sessionEventData.data.length + "]");
            }
            this.b.send(new MessageSystem.MessageData((byte) 2, new Object[]{sessionEventData}), this, Thread.currentThread(), this.b.getVRAddr()[0]);
        }
    }

    private byte[] a(byte[] bArr, AnonymousClass1 anonymousClass1) {
        byte[] bArr2 = new byte[4];
        System.arraycopy(bArr, anonymousClass1.a, bArr2, 0, 4);
        anonymousClass1.a += 4;
        byte[] bArr3 = new byte[(bArr2[0] & 255) + ((bArr2[3] & 255) << 24) + ((bArr2[2] & 255) << 16) + ((bArr2[1] & 255) << 8)];
        System.arraycopy(bArr, anonymousClass1.a, bArr3, 0, bArr3.length);
        anonymousClass1.a += bArr3.length;
        return bArr3;
    }

    @Override // com.nuance.nmsp.client.sdk.components.core.XMode.XModeListener
    public void copConnected() {
        this.n = 3;
        a();
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.MessageSystem.MessageHandler
    public void handleMessage(Object obj, Object obj2) {
        MessageSystem.MessageData messageData = (MessageSystem.MessageData) obj;
        switch (messageData.command) {
            case 1:
                byte[] bArr = (byte[]) ((Object[]) messageData.data)[0];
                if (bArr == null) {
                    this.c.failed((short) 3, bArr);
                    return;
                }
                this.d.add(bArr);
                if (this.n != 1) {
                    if (this.n == 3) {
                        a();
                        return;
                    }
                    return;
                } else {
                    this.n = 2;
                    a(bArr);
                    this.e = new XMode(this.h, this.i, this.j, this.k, this.m, this, this.l, this.b);
                    this.e.setForLogOnly();
                    this.e.connect((short) 0, (short) 0);
                    return;
                }
            case 2:
                CalllogImpl.SessionEventData sessionEventData = (CalllogImpl.SessionEventData) ((Object[]) messageData.data)[0];
                int i = this.g;
                this.g = i + 1;
                sessionEventData.requestId = i;
                byte[] bArr2 = new byte[sessionEventData.data.length + 25];
                bArr2[0] = 0;
                ByteConversion.intToBytes(sessionEventData.requestId, bArr2, 1);
                byte[] appendBCPSessionUUID = ProtocolBuilder.appendBCPSessionUUID(bArr2, sessionEventData.sessionEventId.getBytes());
                ByteConversion.intToBytes(sessionEventData.data.length, appendBCPSessionUUID, 21);
                System.arraycopy(sessionEventData.data, 0, appendBCPSessionUUID, 25, sessionEventData.data.length);
                this.e.sendXModeMsg(ProtocolBuilder.buildXModeBuf((byte) 2, ProtocolDefines.XMODE_VERSION_BCP, ProtocolDefines.XMODE_BCP_COMMAND_BCP_LOG, appendBCPSessionUUID), 3, XMode.NET_CONTEXT_SEND_BCP_LOG);
                return;
            default:
                return;
        }
    }

    @Override // com.nuance.nmsp.client.sdk.components.core.calllog.CalllogSender
    public void send(byte[] bArr) {
        this.g = 1;
        this.b.send(new MessageSystem.MessageData((byte) 1, new Object[]{bArr}), this, Thread.currentThread(), this.b.getVRAddr()[0]);
    }

    @Override // com.nuance.nmsp.client.sdk.components.core.XMode.XModeListener
    public void socketClosed(short s, short s2) {
        if (a.isDebugEnabled()) {
            a.debug("socketClosed() reason [" + ((int) s) + "] subReason [" + ((int) s2) + "]");
        }
        this.n = 1;
        this.b.stop();
    }

    @Override // com.nuance.nmsp.client.sdk.components.core.XMode.XModeListener
    public void socketOpened() {
        if (a.isDebugEnabled()) {
            a.debug("socketOpened()");
        }
    }

    @Override // com.nuance.nmsp.client.sdk.components.core.XMode.XModeListener
    public void xmodeMsgCallback(XModeMsgHeader xModeMsgHeader, byte[] bArr) {
    }

    @Override // com.nuance.nmsp.client.sdk.components.core.XMode.XModeListener
    public void xmodeMsgNotSent(String str, Object obj) {
        if (str.startsWith(XMode.NET_CONTEXT_SEND_BCP_LOG)) {
            if (a.isDebugEnabled()) {
                a.debug("onMsgNotSent(" + str + ")");
            }
            if (this.o > 0) {
                this.o = 0;
                this.f.clear();
                this.d.clear();
                this.e.disconnect();
                this.e = null;
                this.c.failed((short) 2, (byte[]) this.d.remove());
            }
        }
    }

    @Override // com.nuance.nmsp.client.sdk.components.core.XMode.XModeListener
    public void xmodeMsgSent(String str, Object obj) {
        if (str.startsWith(XMode.NET_CONTEXT_SEND_BCP_LOG)) {
            if (a.isDebugEnabled()) {
                a.debug("onMsgSent(" + str + ")");
            }
            this.o--;
            if (this.o == 0) {
                this.c.succeeded((byte[]) this.d.remove());
                a();
            }
        }
    }
}
