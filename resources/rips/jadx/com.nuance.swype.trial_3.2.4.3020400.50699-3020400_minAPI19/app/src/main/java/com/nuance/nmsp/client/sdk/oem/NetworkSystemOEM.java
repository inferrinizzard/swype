package com.nuance.nmsp.client.sdk.oem;

import com.facebook.internal.ServerProtocol;
import com.nuance.nmsp.client.sdk.common.defines.NMSPDefines;
import com.nuance.nmsp.client.sdk.common.oem.api.LogFactory;
import com.nuance.nmsp.client.sdk.common.oem.api.MessageSystem;
import com.nuance.nmsp.client.sdk.common.oem.api.NetworkSystem;
import com.nuance.nmsp.client.sdk.components.general.Parameter;
import com.nuance.nmsp.client.sdk.oem.socket.ssl.SSLSettings;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Vector;

/* loaded from: classes.dex */
public class NetworkSystemOEM implements MessageSystem.MessageHandler, NetworkSystem {
    private static final LogFactory.Log a = LogFactory.getLog(NetworkSystemOEM.class);
    private static final Integer c = new Integer(0);
    private static final Integer d = new Integer(1);
    private static final Integer e = new Integer(2);
    private static final Integer f = new Integer(3);
    private MessageSystem b;
    private Object g = new Object();

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class a {
        Socket a;
        NetworkSystem.CloseSocketCallback b;
        InputStream c;
        OutputStream d;
        Object e;
        c f;

        public a(Socket socket, NetworkSystem.CloseSocketCallback closeSocketCallback, Object obj, InputStream inputStream, OutputStream outputStream, c cVar) {
            this.c = null;
            this.d = null;
            this.e = null;
            this.f = null;
            this.a = socket;
            this.b = closeSocketCallback;
            this.e = obj;
            this.c = inputStream;
            this.d = outputStream;
            this.f = cVar;
        }
    }

    /* loaded from: classes.dex */
    class b {
        byte[] a;
        int b;
        int c;
        NetworkSystem.ReadSocketCallback d;
        Object e;

        public b(NetworkSystemOEM networkSystemOEM, NetworkSystem.NetworkReadMode networkReadMode, byte[] bArr, int i, int i2, NetworkSystem.ReadSocketCallback readSocketCallback, Object obj) {
            this.a = bArr;
            this.b = i;
            this.c = i2;
            this.d = readSocketCallback;
            this.e = obj;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class c extends Thread {
        private String a;
        private int b;
        private NetworkSystem.OpenSocketCallback c;
        private NetworkSystem.CloseSocketCallback d;
        private Object e;
        private NetworkSystem.NetworkStatus f;
        private boolean g;
        private Vector h;
        private SSLSettings i;
        private boolean j;

        public c(String str, int i, NetworkSystem.OpenSocketCallback openSocketCallback, NetworkSystem.CloseSocketCallback closeSocketCallback, Object obj) {
            this.a = null;
            this.c = null;
            this.d = null;
            this.e = null;
            this.f = NetworkSystem.NetworkStatus.NETWORK_OK;
            this.g = false;
            this.h = null;
            this.i = null;
            this.j = false;
            this.a = str;
            this.b = i;
            this.c = openSocketCallback;
            this.d = closeSocketCallback;
            this.e = obj;
            this.h = new Vector();
        }

        public c(String str, int i, SSLSettings sSLSettings, NetworkSystem.OpenSocketCallback openSocketCallback, NetworkSystem.CloseSocketCallback closeSocketCallback, Object obj) {
            this.a = null;
            this.c = null;
            this.d = null;
            this.e = null;
            this.f = NetworkSystem.NetworkStatus.NETWORK_OK;
            this.g = false;
            this.h = null;
            this.i = null;
            this.j = false;
            this.a = str;
            this.b = i;
            this.c = openSocketCallback;
            this.d = closeSocketCallback;
            this.e = obj;
            this.h = new Vector();
            this.j = true;
            this.i = sSLSettings;
        }

        public final NetworkSystem.NetworkStatus a(b bVar) {
            NetworkSystem.NetworkStatus networkStatus;
            Object obj;
            if (this.g) {
                if (NetworkSystemOEM.a.isErrorEnabled()) {
                    NetworkSystemOEM.a.error("SocketReadThread is already stopping!");
                }
                return NetworkSystem.NetworkStatus.NETWORK_ERROR;
            }
            NetworkSystem.NetworkStatus networkStatus2 = NetworkSystem.NetworkStatus.NETWORK_OK;
            synchronized (NetworkSystemOEM.this.g) {
                if (this.h == null) {
                    networkStatus = NetworkSystem.NetworkStatus.NETWORK_ERROR;
                    obj = "SocketReadThread: queue is null!!";
                } else {
                    this.h.addElement(bVar);
                    networkStatus = networkStatus2;
                    obj = null;
                }
                if (NetworkSystemOEM.a.isDebugEnabled()) {
                    NetworkSystemOEM.a.debug("addNewJob() notifying");
                }
                NetworkSystemOEM.this.g.notify();
            }
            if (!NetworkSystemOEM.a.isErrorEnabled() || obj == null) {
                return networkStatus;
            }
            NetworkSystemOEM.a.error(obj);
            return networkStatus;
        }

        public final void a() {
            synchronized (NetworkSystemOEM.this.g) {
                this.g = true;
                this.h.removeAllElements();
                this.h = null;
                NetworkSystemOEM.this.g.notify();
            }
        }

        public final void b() {
            String str = null;
            synchronized (NetworkSystemOEM.this.g) {
                if (this.h == null) {
                    str = "SocketReadThread.cleanPendingJobs(): queue is null!!";
                } else {
                    this.h.removeAllElements();
                }
            }
            if (!NetworkSystemOEM.a.isErrorEnabled() || str == null) {
                return;
            }
            NetworkSystemOEM.a.error(str);
        }

        /* JADX WARN: Removed duplicated region for block: B:114:0x02af  */
        /* JADX WARN: Removed duplicated region for block: B:116:? A[RETURN, SYNTHETIC] */
        @Override // java.lang.Thread, java.lang.Runnable
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public final void run() {
            /*
                Method dump skipped, instructions count: 1292
                To view this dump change 'Code comments level' option to 'DEBUG'
            */
            throw new UnsupportedOperationException("Method not decompiled: com.nuance.nmsp.client.sdk.oem.NetworkSystemOEM.c.run():void");
        }
    }

    public NetworkSystemOEM(MessageSystem messageSystem) {
        this.b = null;
        this.b = messageSystem;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void a(Object[] objArr) {
        this.b.send(objArr, this, this.b.getMyAddr(), this.b.getVRAddr()[0]);
    }

    public static void terminate() {
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.NetworkSystem
    public void clearPendingOps(Object obj) {
        a aVar = (a) obj;
        if (aVar.f != null) {
            aVar.f.b();
            return;
        }
        if (a.isDebugEnabled()) {
            a.debug("SOCKET WRITE ERROR: socket read thread is null");
        }
        closeSocket(obj);
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.NetworkSystem
    public void closeSocket(Object obj) {
        boolean z;
        a aVar = (a) obj;
        if (aVar == null) {
            return;
        }
        synchronized (this.g) {
            if (aVar.f == null) {
                return;
            }
            if (aVar.f.g) {
                z = false;
            } else {
                aVar.f.a();
                aVar.f = null;
                z = true;
            }
            if (aVar.a == null || !z) {
                return;
            }
            try {
                aVar.c.close();
                aVar.c = null;
                aVar.a.close();
                aVar.a = null;
                a(new Object[]{d, aVar.b, NetworkSystem.NetworkStatus.NETWORK_OK, obj, aVar.e});
            } catch (Throwable th) {
                if (a.isErrorEnabled()) {
                    a.error("Socket Close Expception - [" + th.getClass().getName() + "] Message - [" + th.getMessage() + "]");
                }
                a(new Object[]{d, aVar.b, NetworkSystem.NetworkStatus.NETWORK_ERROR, obj, aVar.e});
            }
        }
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.MessageSystem.MessageHandler
    public void handleMessage(Object obj, Object obj2) {
        Object[] objArr = (Object[]) obj;
        if (objArr[0] == c) {
            ((NetworkSystem.OpenSocketCallback) objArr[1]).openSocketCallback((NetworkSystem.NetworkStatus) objArr[2], objArr[3], objArr[4]);
            return;
        }
        if (objArr[0] == d) {
            ((NetworkSystem.CloseSocketCallback) objArr[1]).closeSocketCallback((NetworkSystem.NetworkStatus) objArr[2], objArr[3], objArr[4]);
        } else if (objArr[0] == e) {
            ((NetworkSystem.ReadSocketCallback) objArr[1]).readSocketCallback((NetworkSystem.NetworkStatus) objArr[2], objArr[3], (byte[]) objArr[4], ((Integer) objArr[5]).intValue(), ((Integer) objArr[6]).intValue(), ((Integer) objArr[7]).intValue(), objArr[8]);
        } else if (objArr[0] == f) {
            ((NetworkSystem.WriteSocketCallback) objArr[1]).writeSocketCallback((NetworkSystem.NetworkStatus) objArr[2], objArr[3], (byte[]) objArr[4], ((Integer) objArr[5]).intValue(), ((Integer) objArr[6]).intValue(), ((Integer) objArr[7]).intValue(), objArr[8]);
        }
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.NetworkSystem
    public boolean isNetworkHealthy() {
        return true;
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.NetworkSystem
    public void openSSLSocket(String str, int i, Vector vector, NetworkSystem.OpenSocketCallback openSocketCallback, NetworkSystem.CloseSocketCallback closeSocketCallback, Object obj) {
        SSLSettings sSLSettings = new SSLSettings();
        Enumeration elements = vector.elements();
        while (elements.hasMoreElements()) {
            Parameter parameter = (Parameter) elements.nextElement();
            if (parameter.getName().equals(NMSPDefines.NMSP_DEFINES_SSL_SELFSIGNED_CERT) && (new String(parameter.getValue()).equals("TRUE") || new String(parameter.getValue()).equals(ServerProtocol.DIALOG_RETURN_SCOPES_TRUE))) {
                sSLSettings.enableSelfSignedCert = true;
            }
            if (parameter.getName().equals(NMSPDefines.NMSP_DEFINES_SSL_CERT_SUMMARY)) {
                sSLSettings.certSummary = new String(parameter.getValue());
            }
            if (parameter.getName().equals(NMSPDefines.NMSP_DEFINES_SSL_CERT_DATA)) {
                sSLSettings.certData = new String(parameter.getValue());
            }
        }
        try {
            new c(str, i, sSLSettings, openSocketCallback, closeSocketCallback, obj).start();
        } catch (Exception e2) {
            if (a.isErrorEnabled()) {
                a.error("Open Socket Exception - [" + e2.getClass().getName() + "] Message - [" + e2.getMessage() + "]");
            }
            a(new Object[]{c, openSocketCallback, NetworkSystem.NetworkStatus.NETWORK_ERROR, null, obj});
        }
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.NetworkSystem
    public void openSocket(String str, int i, NetworkSystem.OpenSocketCallback openSocketCallback, NetworkSystem.CloseSocketCallback closeSocketCallback, Object obj) {
        try {
            new c(str, i, openSocketCallback, closeSocketCallback, obj).start();
        } catch (Exception e2) {
            if (a.isErrorEnabled()) {
                a.error("Open Socket Exception - [" + e2.getClass().getName() + "] Message - [" + e2.getMessage() + "]");
            }
            a(new Object[]{c, openSocketCallback, NetworkSystem.NetworkStatus.NETWORK_ERROR, null, obj});
        }
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.NetworkSystem
    public void setSessionId(Object obj, byte[] bArr) {
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.NetworkSystem
    public NetworkSystem.NetworkStatus socketRead(Object obj, NetworkSystem.NetworkReadMode networkReadMode, byte[] bArr, int i, int i2, NetworkSystem.ReadSocketCallback readSocketCallback, Object obj2) {
        if (networkReadMode != NetworkSystem.NetworkReadMode.NETWORK_READ_FULL) {
            if (a.isErrorEnabled()) {
                a.error("Blackberry NetworkSystem only supports NETWORK_READ_FULL");
            }
            return NetworkSystem.NetworkStatus.NETWORK_ERROR;
        }
        a aVar = (a) obj;
        if (aVar.f == null) {
            if (a.isDebugEnabled()) {
                a.debug("SOCKET READ ERROR: socket read thread is null");
            }
            return NetworkSystem.NetworkStatus.NETWORK_ERROR;
        }
        if (!aVar.f.g) {
            return aVar.f.a(new b(this, networkReadMode, bArr, i, i2, readSocketCallback, obj2));
        }
        if (a.isErrorEnabled()) {
            a.error("socket read thread is stopping");
        }
        return NetworkSystem.NetworkStatus.NETWORK_ERROR;
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.NetworkSystem
    public NetworkSystem.NetworkStatus socketWrite(Object obj, byte[] bArr, int i, int i2, NetworkSystem.WriteSocketCallback writeSocketCallback, Object obj2) {
        if (a.isDebugEnabled()) {
            a.debug("socketWrite(bufferLen:" + i2 + ") start");
        }
        a aVar = (a) obj;
        if (aVar.a == null || aVar.d == null) {
            return NetworkSystem.NetworkStatus.NETWORK_ERROR;
        }
        OutputStream outputStream = aVar.d;
        try {
            outputStream.write(bArr, i, i2);
            outputStream.flush();
            a(new Object[]{f, writeSocketCallback, NetworkSystem.NetworkStatus.NETWORK_OK, obj, bArr, new Integer(i), new Integer(i2), new Integer(i2), obj2});
            if (a.isDebugEnabled()) {
                a.debug("socketWrite(bufferLen:" + i2 + ") end");
            }
            return NetworkSystem.NetworkStatus.NETWORK_OK;
        } catch (Exception e2) {
            if (a.isErrorEnabled()) {
                a.error("Socket Write Exception - [" + e2.getClass().getName() + "] Message - [" + e2.getMessage() + "]");
            }
            a(new Object[]{f, writeSocketCallback, NetworkSystem.NetworkStatus.NETWORK_ERROR, obj, bArr, new Integer(i), new Integer(i2), new Integer(0), obj2});
            closeSocket(obj);
            return NetworkSystem.NetworkStatus.NETWORK_ERROR;
        }
    }
}
