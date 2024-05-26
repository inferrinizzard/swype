package com.nuance.nmsp.client.sdk.components.resource.internal.common;

import com.facebook.internal.ServerProtocol;
import com.nuance.nmsp.client.sdk.common.defines.NMSPDefines;
import com.nuance.nmsp.client.sdk.common.oem.api.LogFactory;
import com.nuance.nmsp.client.sdk.common.oem.api.MessageSystem;
import com.nuance.nmsp.client.sdk.common.protocols.ProtocolDefines;
import com.nuance.nmsp.client.sdk.common.util.ByteConversion;
import com.nuance.nmsp.client.sdk.common.util.Util;
import com.nuance.nmsp.client.sdk.components.core.XMode;
import com.nuance.nmsp.client.sdk.components.general.Parameter;
import com.nuance.nmsp.client.sdk.components.resource.common.Manager;
import com.nuance.nmsp.client.sdk.components.resource.common.Resource;
import com.nuance.nmsp.client.sdk.components.resource.common.ResourceException;
import com.nuance.nmsp.client.sdk.components.resource.common.ResourceListener;
import com.nuance.nmsp.client.sdk.oem.BluetoothSystemOEM;
import java.io.ByteArrayOutputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/* loaded from: classes.dex */
public class ResourceImpl implements MessageSystem.MessageHandler, Resource, NMSPSessionListener {
    public static final byte CALLLOG = 6;
    protected static final byte CMD_FREE_RESOURCE = 3;
    protected static final byte CMD_FREE_RESOURCE_ID = 4;
    protected static final byte CMD_GET_PARAM = 2;
    protected static final byte CMD_LOAD_RESOURCE = 0;
    protected static final byte CMD_SET_PARAM = 1;
    public static final byte DICT = 4;
    protected static final byte FREE_RESOURCE_REQUEST = 3;
    protected static final byte GET_PARAM_REQUEST = 2;
    protected static final byte LOAD_RESOURCE_REQUEST = 0;
    public static final byte NMAS = 5;
    public static final byte NR9_RECO = 1;
    public static final byte NR9_TTS = 2;
    public static final byte OPER = 3;
    protected static final String RESOURCE_UNLOADED_EXCEPTION = "the resource was unloaded. ";
    protected static final byte SET_PARAM_REQUEST = 1;
    protected static final int STATE_RESOURCE_LOADED = 2;
    protected static final int STATE_RESOURCE_LOADING = 1;
    protected static final int STATE_RESOURCE_UNLOADED = 0;
    protected static final long defaultReqId = 0;
    private ResourceListener b;
    private NMSPSessionListener c;
    private byte d;
    protected NMSPDefines.Codec inputCodec;
    public Manager manager;
    protected MessageSystem messageSystem;
    protected NMSPDefines.Codec outputCodec;
    protected long resourceId;
    public NMSPSession session;
    public byte tranId;
    private static final LogFactory.Log a = LogFactory.getLog(ResourceImpl.class);
    private static long e = 1;
    public int resourceState = 0;
    public Object syncObject = new Object();
    protected Hashtable requestIds = new Hashtable();
    private Hashtable f = new Hashtable();
    public Vector parameters = new Vector();

    public ResourceImpl(ManagerImpl managerImpl, String str, ResourceListener resourceListener, Vector vector, byte b) {
        int i = 0;
        this.tranId = (byte) 0;
        this.manager = managerImpl;
        this.session = managerImpl.getSession();
        this.b = resourceListener;
        this.inputCodec = managerImpl.getInputCodec();
        this.outputCodec = managerImpl.getOutputCodec();
        this.d = b;
        this.messageSystem = managerImpl.getMsgSys();
        this.tranId = this.session.getTransactionId();
        this.resourceId = this.session.getResourceId();
        if (vector == null) {
            return;
        }
        while (true) {
            int i2 = i;
            if (i2 >= vector.size()) {
                return;
            }
            this.parameters.addElement(((Parameter) vector.elementAt(i2)).m41clone());
            i = i2 + 1;
        }
    }

    private static Parameter.Type a(String str) {
        if (str.equalsIgnoreCase(ServerProtocol.DIALOG_PARAM_SDK_VERSION)) {
            return Parameter.Type.SDK;
        }
        if (str.equalsIgnoreCase("nmsp")) {
            return Parameter.Type.NMSP;
        }
        if (str.equalsIgnoreCase("app")) {
            return Parameter.Type.APP;
        }
        if (str.equalsIgnoreCase("nss")) {
            return Parameter.Type.NSS;
        }
        if (str.equalsIgnoreCase("slog")) {
            return Parameter.Type.SLOG;
        }
        if (str.equalsIgnoreCase("nsslog")) {
            return Parameter.Type.NSSLOG;
        }
        if (str.equalsIgnoreCase("gwlog")) {
            return Parameter.Type.GWLOG;
        }
        if (str.equalsIgnoreCase("svsp")) {
            return Parameter.Type.SVSP;
        }
        if (str.equalsIgnoreCase("sip")) {
            return Parameter.Type.SIP;
        }
        if (str.equalsIgnoreCase("sdp")) {
            return Parameter.Type.SDP;
        }
        return null;
    }

    private void a(Vector vector, long j) {
        if (this.resourceState != 2) {
            this.b.getParameterFailed((short) 407, (short) 14, j);
        }
        byte[] bArr = new byte[0];
        this.requestIds.put(new Long(j), new Byte((byte) 2));
        this.f.put(new Long(j), vector);
        String str = "<gp><rid>" + j + "</rid>";
        for (int i = 0; i < vector.size(); i++) {
            Parameter parameter = (Parameter) vector.elementAt(i);
            if (parameter.getType() == Parameter.Type.APP || parameter.getType() == Parameter.Type.NMSP || parameter.getType() == Parameter.Type.NSS || parameter.getType() == Parameter.Type.SVSP) {
                str = str + "<" + parameter.getType() + " p=\"" + parameter.getName() + "\"/>";
            }
        }
        this.session.postBcpMessage(ProtocolDefines.XMODE_BCP_COMMAND_GET_PARAMS, XMode.NET_CONTEXT_SEND_BCP_GET_PARAMS, (str + "</gp>").getBytes(), bArr, this.tranId, j, this.c, false);
    }

    public static long getDefaultRequestId() {
        return 0L;
    }

    @Override // com.nuance.nmsp.client.sdk.components.resource.common.Resource
    public void freeResource(int i) throws ResourceException {
        a.debug("freeResource, disconnect timeout: " + i);
        synchronized (this.syncObject) {
            if (this.resourceState != 2) {
                a.error("ResourceException the resource was unloaded. ");
                throw new ResourceException(RESOURCE_UNLOADED_EXCEPTION);
            }
            this.resourceState = 0;
            this.messageSystem.send(new MessageSystem.MessageData((byte) 3, new Integer(i)), this, Thread.currentThread(), this.messageSystem.getVRAddr()[0]);
        }
    }

    public void freeResourceId(int i) throws ResourceException {
        a.debug("freeResourceId, disconnect timeout: " + i);
        synchronized (this.syncObject) {
            if (this.resourceState != 2) {
                a.error("ResourceException the resource was unloaded. ");
                throw new ResourceException(RESOURCE_UNLOADED_EXCEPTION);
            }
            this.resourceState = 0;
            this.messageSystem.send(new MessageSystem.MessageData((byte) 4, new Integer(i)), this, Thread.currentThread(), this.messageSystem.getVRAddr()[0]);
        }
    }

    public Manager getManager() {
        return this.manager;
    }

    @Override // com.nuance.nmsp.client.sdk.components.resource.common.Resource
    public long getParams(Vector vector) throws ResourceException {
        long requestId;
        Parameter parameter;
        int i = 0;
        a.debug("getParams");
        if (vector == null) {
            a.error("NullPointerException parameters is NULL. ");
            throw new NullPointerException("parameters can not be null!");
        }
        while (true) {
            int i2 = i;
            if (i2 >= vector.size()) {
                synchronized (this.syncObject) {
                    requestId = getRequestId();
                    this.messageSystem.send(new MessageSystem.MessageData((byte) 2, new Object[]{vector, new Long(requestId)}), this, Thread.currentThread(), this.messageSystem.getVRAddr()[0]);
                }
                return requestId;
            }
            parameter = (Parameter) vector.elementAt(i2);
            if (parameter.getType() == Parameter.Type.SDK || parameter.getType() == Parameter.Type.SLOG || parameter.getType() == Parameter.Type.NSSLOG || parameter.getType() == Parameter.Type.GWLOG || parameter.getType() == Parameter.Type.SIP || parameter.getType() == Parameter.Type.SDP) {
                break;
            }
            i = i2 + 1;
        }
        a.error("IllegalArgumentException Parameter type: " + parameter.getType() + " not allowed. ");
        throw new IllegalArgumentException("Parameter type: " + parameter.getType() + " not allowed. ");
    }

    public synchronized long getRequestId() {
        long j;
        j = e;
        e = 1 + j;
        if (e == Long.MIN_VALUE) {
            e = 1L;
        }
        return j;
    }

    protected byte getResourceType() {
        return this.d;
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.MessageSystem.MessageHandler
    public void handleMessage(Object obj, Object obj2) {
        MessageSystem.MessageData messageData = (MessageSystem.MessageData) obj;
        switch (messageData.command) {
            case 1:
                Object[] objArr = (Object[]) messageData.data;
                Vector vector = (Vector) objArr[0];
                long longValue = ((Long) objArr[1]).longValue();
                if (this.resourceState != 2) {
                    this.b.setParameterFailed((short) 407, (short) 14, longValue);
                    return;
                }
                boolean z = true;
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byte[] bArr = new byte[4];
                this.requestIds.put(new Long(longValue), new Byte((byte) 1));
                int i = 0;
                int i2 = 1;
                String str = "<sp><rid>" + longValue + "</rid>";
                while (true) {
                    int i3 = i;
                    if (i3 >= vector.size()) {
                        this.session.postBcpMessage(ProtocolDefines.XMODE_BCP_COMMAND_SET_PARAMS, XMode.NET_CONTEXT_SEND_BCP_SET_PARAMS, (str + "</sp>").getBytes(), byteArrayOutputStream.toByteArray(), this.tranId, longValue, this.c, z);
                        try {
                            byteArrayOutputStream.close();
                            return;
                        } catch (Exception e2) {
                            return;
                        }
                    }
                    Parameter parameter = (Parameter) vector.elementAt(i3);
                    if (parameter.getType() == Parameter.Type.APP || parameter.getType() == Parameter.Type.NMSP || parameter.getType() == Parameter.Type.NSS) {
                        z = false;
                        str = str + "<" + parameter.getType() + " p=\"" + parameter.getName() + "\" v=\"" + Util.escapeXML(new String(parameter.getValue())) + "\"/>";
                    } else {
                        if (parameter.getType() == Parameter.Type.SVSP) {
                            z = false;
                        }
                        str = str + "<" + parameter.getType().toString() + " p=\"" + parameter.getName() + "\" v=\"" + i2 + "\"/>";
                        byte[] value = parameter.getValue();
                        ByteConversion.intToBytes(value.length, bArr, 0);
                        byteArrayOutputStream.write(bArr, 0, 4);
                        byteArrayOutputStream.write(value, 0, value.length);
                        i2++;
                    }
                    i = i3 + 1;
                }
                break;
            case 2:
                Object[] objArr2 = (Object[]) messageData.data;
                a((Vector) objArr2[0], ((Long) objArr2[1]).longValue());
                return;
            case 3:
                this.session.freeResource(this.tranId, ((Integer) messageData.data).intValue(), this.c);
                this.b.resourceUnloaded((short) 0);
                return;
            case 4:
                int intValue = ((Integer) messageData.data).intValue();
                long requestId = getRequestId();
                this.session.postBcpMessage(ProtocolDefines.XMODE_BCP_COMMAND_FREE_RESOURCE_ID, XMode.NET_CONTEXT_SEND_BCP_FREE_RESOURCE_ID, ((((((("<fr><rid>" + requestId + "</rid>") + "<n>1</n>") + "<resids>") + "<res1><id>" + this.resourceId + "</id>") + "<timeout>" + intValue + "<timeout></res1>") + "</resids>") + "</fr>").getBytes(), null, this.tranId, requestId, this.c, true);
                return;
            default:
                return;
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Failed to find 'out' block for switch in B:10:0x006b. Please report as an issue. */
    public void loadResource(NMSPSessionListener nMSPSessionListener, Vector vector) {
        String str;
        a.debug("loadResource");
        if (new BluetoothSystemOEM(this.parameters).isBluetoothHeadsetConnected()) {
            this.inputCodec = Util.adjustCodecForBluetooth(this.inputCodec);
            this.outputCodec = Util.adjustCodecForBluetooth(this.outputCodec);
            ((ManagerImpl) this.manager).setInputCodec(this.inputCodec);
            ((ManagerImpl) this.manager).setOutputCodec(this.outputCodec);
        }
        this.c = nMSPSessionListener;
        this.session.setSessionListener(nMSPSessionListener);
        if (this.resourceState == 0) {
            if (this.session.getSessionId() != null) {
                this.resourceState = 2;
            } else {
                this.session.connect(this.inputCodec, this.outputCodec);
                this.resourceState = 1;
            }
            long requestId = getRequestId();
            String str2 = "<lr><rid>" + requestId + "</rid>";
            switch (this.d) {
                case 1:
                    str2 = str2 + "<nr9><reco/></nr9>";
                    this.session.postBcpMessage(ProtocolDefines.XMODE_BCP_COMMAND_LOAD_RESOURCE, XMode.NET_CONTEXT_SEND_BCP_LOAD_RESOURCE, (str2 + "</lr>").getBytes(), null, this.tranId, requestId, nMSPSessionListener, false);
                    return;
                case 2:
                    str2 = str2 + "<nr9><tts/></nr9>";
                    this.session.postBcpMessage(ProtocolDefines.XMODE_BCP_COMMAND_LOAD_RESOURCE, XMode.NET_CONTEXT_SEND_BCP_LOAD_RESOURCE, (str2 + "</lr>").getBytes(), null, this.tranId, requestId, nMSPSessionListener, false);
                    return;
                case 3:
                    str2 = str2 + "<oper></oper>";
                    this.session.postBcpMessage(ProtocolDefines.XMODE_BCP_COMMAND_LOAD_RESOURCE, XMode.NET_CONTEXT_SEND_BCP_LOAD_RESOURCE, (str2 + "</lr>").getBytes(), null, this.tranId, requestId, nMSPSessionListener, false);
                    return;
                case 4:
                    String str3 = str2 + "<dict>";
                    if (vector != null) {
                        str = str3;
                        for (int i = 0; i < vector.size(); i++) {
                            Parameter parameter = (Parameter) vector.elementAt(i);
                            if (parameter.getType() == Parameter.Type.SIP || parameter.getType() == Parameter.Type.SDP || parameter.getType() == Parameter.Type.APP || parameter.getType() == Parameter.Type.NSS) {
                                str = str + "<" + parameter.getType().toString() + " p=\"" + parameter.getName() + "\" v=\"" + Util.escapeXML(new String(parameter.getValue())) + "\"/>";
                            }
                        }
                    } else {
                        str = str3;
                    }
                    str2 = str + "</dict>";
                    this.session.postBcpMessage(ProtocolDefines.XMODE_BCP_COMMAND_LOAD_RESOURCE, XMode.NET_CONTEXT_SEND_BCP_LOAD_RESOURCE, (str2 + "</lr>").getBytes(), null, this.tranId, requestId, nMSPSessionListener, false);
                    return;
                case 5:
                    return;
                default:
                    this.session.postBcpMessage(ProtocolDefines.XMODE_BCP_COMMAND_LOAD_RESOURCE, XMode.NET_CONTEXT_SEND_BCP_LOAD_RESOURCE, (str2 + "</lr>").getBytes(), null, this.tranId, requestId, nMSPSessionListener, false);
                    return;
            }
        }
    }

    @Override // com.nuance.nmsp.client.sdk.components.resource.internal.common.NMSPSessionListener
    public void onBcpData(byte b, long j, byte[] bArr) {
    }

    @Override // com.nuance.nmsp.client.sdk.components.resource.internal.common.NMSPSessionListener
    public void onBcpEvent(byte b, short s) {
        a.debug("onBcpEvent, TID: " + ((int) b) + ", event code: " + ((int) s));
        if (this.resourceState == 2) {
            this.resourceState = 0;
            Enumeration keys = this.requestIds.keys();
            while (keys.hasMoreElements()) {
                Long l = (Long) keys.nextElement();
                switch (((Byte) this.requestIds.remove(l)).byteValue()) {
                    case 1:
                        this.b.setParameterFailed((short) 407, (short) 14, l.longValue());
                        break;
                    case 2:
                        this.b.getParameterFailed((short) 407, (short) 14, l.longValue());
                        break;
                }
            }
            this.b.resourceUnloaded(s);
            this.session.removeSessionListener(this);
        }
    }

    @Override // com.nuance.nmsp.client.sdk.components.resource.internal.common.NMSPSessionListener
    public void onBcpFreeResourceId() {
        this.b.resourceUnloaded((short) 0);
    }

    @Override // com.nuance.nmsp.client.sdk.components.resource.internal.common.NMSPSessionListener
    public void onBcpGenerateAudioComplete(byte b, long j, long j2, short s) {
    }

    @Override // com.nuance.nmsp.client.sdk.components.resource.internal.common.NMSPSessionListener
    public void onBcpGetParamsComplete(byte b, long j, short s, byte[] bArr) {
        int i;
        int i2 = 0;
        a.debug("onBcpGetParamsComplete, TID: " + ((int) b) + ", RID: " + j);
        if (this.requestIds.remove(new Long(j)) == null) {
            a.warn("onBcpGetParamsComplete, RID: " + j + " already removed!");
            this.f.remove(new Long(j));
            return;
        }
        Vector vector = (Vector) this.f.remove(new Long(j));
        if (vector == null) {
            a.error("Could not find the grammars associated with RID: " + j);
            return;
        }
        String str = new String(bArr);
        if (str.startsWith(";")) {
            i2 = 1;
            i = 0;
        } else {
            i = 0;
        }
        while (true) {
            int indexOf = str.indexOf(";", i2);
            if (indexOf == -1) {
                break;
            }
            String substring = str.substring(i2, indexOf);
            if (substring.indexOf(":") == -1) {
                Parameter parameter = (Parameter) vector.elementAt(i);
                vector.setElementAt(new Parameter(parameter.getName(), substring.getBytes(), parameter.getType()), i);
            }
            i++;
            i2 = indexOf + 1;
        }
        if (i2 < str.length()) {
            String substring2 = str.substring(i2);
            if (substring2.indexOf(":") == -1) {
                Parameter parameter2 = (Parameter) vector.elementAt(i);
                vector.setElementAt(new Parameter(parameter2.getName(), substring2.getBytes(), parameter2.getType()), i);
            }
        }
        if (s == 200 || s == 201) {
            this.b.getParameterCompleted(s, vector, j);
        } else {
            this.b.getParameterFailed(s, ResourceListener.COMPLETION_CAUSE_EMPTY, j);
        }
    }

    @Override // com.nuance.nmsp.client.sdk.components.resource.internal.common.NMSPSessionListener
    public void onBcpRecognitionComplete(byte b, long j, short s, byte[] bArr) {
    }

    @Override // com.nuance.nmsp.client.sdk.components.resource.internal.common.NMSPSessionListener
    public void onBcpRecognitionIntermediateResults(byte b, long j, short s, byte[] bArr) {
    }

    @Override // com.nuance.nmsp.client.sdk.components.resource.internal.common.NMSPSessionListener
    public void onBcpResponse(byte b, long j, short s, short s2, short s3) {
        Byte b2;
        a.debug("onBcpResponse, TID: " + ((int) b) + ", RID: " + j + ", status code: " + ((int) s) + " , request state: " + ((int) s2) + ", completion cause: " + ((int) s3));
        if (s == 200 || (b2 = (Byte) this.requestIds.get(new Long(j))) == null) {
            return;
        }
        switch (b2.byteValue()) {
            case 1:
                this.requestIds.remove(new Long(j));
                this.b.setParameterFailed(s, s3, j);
                return;
            case 2:
                this.requestIds.remove(new Long(j));
                this.b.getParameterFailed(s, s3, j);
                return;
            default:
                return;
        }
    }

    @Override // com.nuance.nmsp.client.sdk.components.resource.internal.common.NMSPSessionListener
    public void onBcpSetParamsComplete(byte b, long j, short s, byte[] bArr) {
        String substring;
        int indexOf;
        a.debug("onBcpSetParamsComplete, TID: " + ((int) b) + ", RID: " + j + ", status code: " + ((int) s));
        if (this.requestIds.remove(new Long(j)) == null) {
            a.warn("onBcpSetParamsComplete, RID: " + j + " already removed!");
            return;
        }
        Vector vector = new Vector();
        if (bArr != null) {
            String str = new String(bArr);
            int i = str.startsWith(";") ? 1 : 0;
            while (true) {
                int indexOf2 = str.indexOf(";", i);
                if (indexOf2 == -1) {
                    break;
                }
                String substring2 = str.substring(i, indexOf2);
                int indexOf3 = substring2.indexOf(".");
                if (indexOf3 != -1) {
                    vector.addElement(new Parameter(substring2.substring(indexOf3 + 1), a(substring2.substring(0, indexOf3))));
                }
                i = indexOf2 + 1;
            }
            if (i < str.length() && (indexOf = (substring = str.substring(i)).indexOf(".")) != -1) {
                vector.addElement(new Parameter(substring.substring(indexOf + 1), a(substring.substring(0, indexOf))));
            }
        }
        if (s == 200 || s == 201) {
            this.b.setParameterCompleted(s, vector, j);
        } else {
            this.b.setParameterFailed(s, ResourceListener.COMPLETION_CAUSE_EMPTY, j);
        }
    }

    @Override // com.nuance.nmsp.client.sdk.components.resource.internal.common.NMSPSessionListener
    public void onMsgNotSent(String str, Object obj) {
    }

    @Override // com.nuance.nmsp.client.sdk.components.resource.internal.common.NMSPSessionListener
    public void onMsgSent(String str, Object obj) {
    }

    @Override // com.nuance.nmsp.client.sdk.components.resource.internal.common.NMSPSessionListener
    public void onSessionConnected(byte[] bArr) {
        if (a.isDebugEnabled()) {
            a.debug("onSessionConnected, SID: " + bArr);
        }
        synchronized (this.syncObject) {
            this.resourceState = 2;
        }
    }

    @Override // com.nuance.nmsp.client.sdk.components.resource.internal.common.NMSPSessionListener
    public void onSessionDisconnected(short s) {
        short s2;
        if (a.isDebugEnabled()) {
            a.debug("onSessionDisconnected, reason code: " + ((int) s));
        }
        switch (s) {
            case 1:
                s2 = 0;
                break;
            case 2:
            default:
                s2 = 1;
                break;
            case 3:
                s2 = 3;
                break;
            case 4:
                s2 = 1;
                break;
            case 5:
                s2 = 1;
                break;
            case 6:
                s2 = 6;
                break;
            case 7:
                s2 = 1;
                break;
        }
        Enumeration keys = this.requestIds.keys();
        while (keys.hasMoreElements()) {
            Long l = (Long) keys.nextElement();
            switch (((Byte) this.requestIds.remove(l)).byteValue()) {
                case 1:
                    this.b.setParameterFailed((short) 407, (short) 14, l.longValue());
                    break;
                case 2:
                    this.b.getParameterFailed((short) 407, (short) 14, l.longValue());
                    break;
            }
        }
        synchronized (this.syncObject) {
            if (this.resourceState == 2) {
                this.b.resourceUnloaded(s2);
            }
            this.resourceState = 0;
        }
    }

    @Override // com.nuance.nmsp.client.sdk.components.resource.internal.common.NMSPSessionListener
    public void onVapPlayBeginReceived() {
    }

    @Override // com.nuance.nmsp.client.sdk.components.resource.internal.common.NMSPSessionListener
    public void onVapPlayEndReceived() {
    }

    @Override // com.nuance.nmsp.client.sdk.components.resource.internal.common.NMSPSessionListener
    public void onVapPlayReceived() {
    }

    @Override // com.nuance.nmsp.client.sdk.components.resource.internal.common.NMSPSessionListener
    public void onVapSending() {
    }

    @Override // com.nuance.nmsp.client.sdk.components.resource.common.Resource
    public long setParams(Vector vector) throws ResourceException {
        long requestId;
        int i = 0;
        a.debug("setParams");
        if (vector == null) {
            a.error("NullPointerException parameters is NULL. ");
            throw new NullPointerException("parameters can not be null!");
        }
        while (true) {
            int i2 = i;
            if (i2 >= vector.size()) {
                synchronized (this.syncObject) {
                    requestId = getRequestId();
                    this.messageSystem.send(new MessageSystem.MessageData((byte) 1, new Object[]{vector, new Long(requestId)}), this, Thread.currentThread(), this.messageSystem.getVRAddr()[0]);
                }
                return requestId;
            }
            Parameter parameter = (Parameter) vector.elementAt(i2);
            if (parameter.getType() == Parameter.Type.SDK) {
                a.error("IllegalArgumentException Parameter type: " + parameter.getType() + " not allowed. ");
                throw new IllegalArgumentException("Parameter type: " + parameter.getType() + " not allowed. ");
            }
            i = i2 + 1;
        }
    }
}
