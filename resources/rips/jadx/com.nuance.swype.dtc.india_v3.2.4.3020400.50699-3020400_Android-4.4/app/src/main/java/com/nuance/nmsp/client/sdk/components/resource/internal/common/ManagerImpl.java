package com.nuance.nmsp.client.sdk.components.resource.internal.common;

import com.facebook.internal.ServerProtocol;
import com.nuance.nmsp.client.sdk.common.defines.NMSPDefines;
import com.nuance.nmsp.client.sdk.common.oem.api.LogFactory;
import com.nuance.nmsp.client.sdk.components.core.calllog.SessionEvent;
import com.nuance.nmsp.client.sdk.components.core.calllog.SessionEventBuilder;
import com.nuance.nmsp.client.sdk.components.core.internal.calllog.CalllogImpl;
import com.nuance.nmsp.client.sdk.components.core.internal.calllog.CalllogListener;
import com.nuance.nmsp.client.sdk.components.general.NMSPManager;
import com.nuance.nmsp.client.sdk.components.general.Parameter;
import com.nuance.nmsp.client.sdk.components.resource.common.Manager;
import com.nuance.nmsp.client.sdk.components.resource.common.ManagerListener;
import com.nuance.swypeconnect.ac.ACBuildConfigRuntime;
import java.util.Vector;

/* loaded from: classes.dex */
public class ManagerImpl extends NMSPManager implements CalllogListener, Manager {
    private static final LogFactory.Log a = LogFactory.getLog(ManagerImpl.class);
    public String applicationId;
    private NMSPSession b;
    private Object c;
    private CalllogImpl d;
    private ManagerListener e;

    public ManagerImpl(String str, short s, String str2, byte[] bArr, String str3, NMSPDefines.Codec codec, NMSPDefines.Codec codec2, Vector vector, ManagerListener managerListener) {
        super(str, s, str3, codec, codec2);
        Vector vector2;
        boolean z;
        int i;
        String str4;
        int i2;
        if (str2 == null) {
            throw new IllegalArgumentException(" application id is null.");
        }
        this.applicationId = str2;
        if (bArr == null) {
            throw new IllegalArgumentException(" application key is null");
        }
        this.e = managerListener;
        int i3 = 7;
        String str5 = null;
        int i4 = CalllogImpl.CALLLOG_CHUNK_SIZE_DEFAULT;
        boolean z2 = false;
        if (vector != null && vector.size() != 0) {
            vector2 = new Vector(vector.size());
            int i5 = 0;
            while (true) {
                int i6 = i5;
                if (i6 >= vector.size()) {
                    z = z2;
                    i = i4;
                    str4 = str5;
                    i2 = i3;
                    break;
                }
                Parameter parameter = (Parameter) vector.elementAt(i6);
                if (parameter.getType() == Parameter.Type.SDK) {
                    if (parameter.getName().equals(NMSPDefines.DEVICE_CMD_LOG_TO_SERVER_ENABLED)) {
                        this.c = new Vector();
                    } else if (parameter.getName().equals(NMSPDefines.NMSP_DEFINES_CALLLOG_DISABLE)) {
                        if (new String(parameter.getValue()).equals("TRUE") || new String(parameter.getValue()).equals(ServerProtocol.DIALOG_RETURN_SCOPES_TRUE)) {
                            z2 = true;
                        } else if (new String(parameter.getValue()).equals("FALSE") || new String(parameter.getValue()).equals(ACBuildConfigRuntime.DEVELOPER_LOG_ENABLED)) {
                            z2 = false;
                        }
                    } else if (parameter.getName().equals(NMSPDefines.NMSP_DEFINES_CALLLOG_ROOT_ID)) {
                        str5 = new String(parameter.getValue());
                    } else if (parameter.getName().equals(NMSPDefines.NMSP_DEFINES_CALLLOG_RETENTION_DAYS)) {
                        i3 = Integer.parseInt(new String(parameter.getValue()));
                    } else if (parameter.getName().equals(NMSPDefines.NMSP_DEFINES_CALLLOG_CHUNK_SIZE) && (i4 = Integer.parseInt(new String(parameter.getValue()))) < 20000) {
                        throw new IllegalArgumentException("Minimum chunk size for calllog is 20000");
                    }
                }
                vector2.addElement(parameter);
                i5 = i6 + 1;
            }
        } else {
            vector2 = new Vector();
            z = false;
            i = 1000000;
            str4 = null;
            i2 = 7;
        }
        this.b = new NMSPSession(getGatewayIP(), getGatewayPort(), str2, bArr, str3, vector2, getMsgSys(), managerListener);
        if (z) {
            return;
        }
        this.d = new CalllogImpl(str, s, this.applicationId, bArr, i, i2, str4, vector, this.uid, getMsgSys(), this);
        this.b.a(this.d);
    }

    @Override // com.nuance.nmsp.client.sdk.components.core.internal.calllog.CalllogListener
    public void calllogDataGenerated(byte[] bArr) {
        this.e.callLogDataGenerated(bArr);
    }

    public void clearResourceLogs() {
        this.c = null;
    }

    @Override // com.nuance.nmsp.client.sdk.components.resource.common.Manager
    public void flushCallLogData() {
        if (this.d == null) {
            a.error("Trying to flush logs while calllog is disabled");
        } else {
            this.d.flushLogsToListener();
        }
    }

    public CalllogImpl getCalllog() {
        return this.d;
    }

    @Override // com.nuance.nmsp.client.sdk.components.resource.common.Manager
    public String getCalllogRootParentId() {
        if (this.d != null) {
            return this.d.getRootParentId();
        }
        a.error("Trying to get root parent id while calllog is disabled");
        return "";
    }

    public SessionEvent getCalllogSession() {
        return this.b.getSessionEvent();
    }

    public Object getResourceLogs() {
        return this.c;
    }

    public NMSPSession getSession() {
        return this.b;
    }

    @Override // com.nuance.nmsp.client.sdk.components.resource.common.Manager
    public SessionEventBuilder logAppEvent(String str) {
        if (this.d != null) {
            return this.d.getRootSessionEvent().createChildEventBuilder(str);
        }
        a.error("Trying to add log while calllog is disabled");
        return null;
    }

    @Override // com.nuance.nmsp.client.sdk.components.general.NMSPManager, com.nuance.nmsp.client.sdk.components.resource.common.Manager
    public void shutdown() {
        a.debug("shutdown");
        if (this.d != null) {
            SessionEvent rootSessionEvent = this.d.getRootSessionEvent();
            if (rootSessionEvent != null) {
                rootSessionEvent.createChildEventBuilder(SessionEvent.NMSP_CALLLOG_SHUTDOWN_EVENT).commit();
            }
            this.d.flushLogsToListener();
        }
        this.b.disconnectAndShutdown();
    }
}
