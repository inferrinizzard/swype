package com.google.api.client.http.apache;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import org.apache.http.conn.ssl.SSLSocketFactory;

/* loaded from: classes.dex */
final class SSLSocketFactoryExtension extends SSLSocketFactory {
    private final javax.net.ssl.SSLSocketFactory socketFactory;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SSLSocketFactoryExtension(SSLContext sslContext) throws KeyManagementException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException {
        super(null);
        this.socketFactory = sslContext.getSocketFactory();
    }

    @Override // org.apache.http.conn.ssl.SSLSocketFactory, org.apache.http.conn.scheme.SocketFactory
    public final Socket createSocket() throws IOException {
        return this.socketFactory.createSocket();
    }

    @Override // org.apache.http.conn.ssl.SSLSocketFactory, org.apache.http.conn.scheme.LayeredSocketFactory
    public final Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
        SSLSocket sslSocket = (SSLSocket) this.socketFactory.createSocket(socket, host, port, autoClose);
        getHostnameVerifier().verify(host, sslSocket);
        return sslSocket;
    }
}
