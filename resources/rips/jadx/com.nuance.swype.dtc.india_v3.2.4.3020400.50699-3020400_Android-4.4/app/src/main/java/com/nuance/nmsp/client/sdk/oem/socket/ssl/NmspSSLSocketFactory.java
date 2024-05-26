package com.nuance.nmsp.client.sdk.oem.socket.ssl;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

/* loaded from: classes.dex */
public class NmspSSLSocketFactory {
    private static SSLSocketFactory a;
    private static SSLContext b;
    private static NmspX509TrustManager[] c = new NmspX509TrustManager[1];

    public static Socket createSocket(String str, int i, SSLSettings sSLSettings) throws UnknownHostException, IOException, SecurityException {
        try {
            c[0] = new NmspX509TrustManager(sSLSettings);
            SSLContext sSLContext = SSLContext.getInstance("TLS");
            b = sSLContext;
            sSLContext.init(null, c, null);
            a = b.getSocketFactory();
            return a.createSocket(str, i);
        } catch (KeyManagementException e) {
            throw new SecurityException("Failed to initialize the client-side SSLContext " + e);
        } catch (NoSuchAlgorithmException e2) {
            throw new SecurityException("No such algorithm exception " + e2);
        } catch (GeneralSecurityException e3) {
            throw new SecurityException("General security exception " + e3);
        }
    }
}
