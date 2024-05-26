package com.google.api.client.http.javanet;

import com.google.api.client.http.HttpMethods;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.util.Preconditions;
import com.google.api.client.util.SecurityUtils;
import com.google.api.client.util.SslUtils;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.util.Arrays;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

/* loaded from: classes.dex */
public final class NetHttpTransport extends HttpTransport {
    private static final String[] SUPPORTED_METHODS;
    private final ConnectionFactory connectionFactory;
    private final HostnameVerifier hostnameVerifier;
    private final SSLSocketFactory sslSocketFactory;

    static {
        String[] strArr = {"DELETE", "GET", HttpMethods.HEAD, HttpMethods.OPTIONS, "POST", "PUT", HttpMethods.TRACE};
        SUPPORTED_METHODS = strArr;
        Arrays.sort(strArr);
    }

    public NetHttpTransport() {
        this((ConnectionFactory) null, (SSLSocketFactory) null, (HostnameVerifier) null);
    }

    NetHttpTransport(Proxy proxy, SSLSocketFactory sslSocketFactory, HostnameVerifier hostnameVerifier) {
        this(new DefaultConnectionFactory(proxy), sslSocketFactory, hostnameVerifier);
    }

    NetHttpTransport(ConnectionFactory connectionFactory, SSLSocketFactory sslSocketFactory, HostnameVerifier hostnameVerifier) {
        this.connectionFactory = connectionFactory == null ? new DefaultConnectionFactory() : connectionFactory;
        this.sslSocketFactory = sslSocketFactory;
        this.hostnameVerifier = hostnameVerifier;
    }

    @Override // com.google.api.client.http.HttpTransport
    public final boolean supportsMethod(String method) {
        return Arrays.binarySearch(SUPPORTED_METHODS, method) >= 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.api.client.http.HttpTransport
    public final NetHttpRequest buildRequest(String method, String url) throws IOException {
        Preconditions.checkArgument(supportsMethod(method), "HTTP method %s not supported", method);
        URL connUrl = new URL(url);
        HttpURLConnection connection = this.connectionFactory.openConnection(connUrl);
        connection.setRequestMethod(method);
        if (connection instanceof HttpsURLConnection) {
            HttpsURLConnection secureConnection = (HttpsURLConnection) connection;
            if (this.hostnameVerifier != null) {
                secureConnection.setHostnameVerifier(this.hostnameVerifier);
            }
            if (this.sslSocketFactory != null) {
                secureConnection.setSSLSocketFactory(this.sslSocketFactory);
            }
        }
        return new NetHttpRequest(connection);
    }

    /* loaded from: classes.dex */
    public static final class Builder {
        private ConnectionFactory connectionFactory;
        private HostnameVerifier hostnameVerifier;
        private Proxy proxy;
        private SSLSocketFactory sslSocketFactory;

        public final Builder setProxy(Proxy proxy) {
            this.proxy = proxy;
            return this;
        }

        public final Builder setConnectionFactory(ConnectionFactory connectionFactory) {
            this.connectionFactory = connectionFactory;
            return this;
        }

        public final SSLSocketFactory getSslSocketFactory() {
            return this.sslSocketFactory;
        }

        public final Builder setSslSocketFactory(SSLSocketFactory sslSocketFactory) {
            this.sslSocketFactory = sslSocketFactory;
            return this;
        }

        public final HostnameVerifier getHostnameVerifier() {
            return this.hostnameVerifier;
        }

        public final Builder setHostnameVerifier(HostnameVerifier hostnameVerifier) {
            this.hostnameVerifier = hostnameVerifier;
            return this;
        }

        public final NetHttpTransport build() {
            return this.proxy == null ? new NetHttpTransport(this.connectionFactory, this.sslSocketFactory, this.hostnameVerifier) : new NetHttpTransport(this.proxy, this.sslSocketFactory, this.hostnameVerifier);
        }

        public final Builder trustCertificatesFromJavaKeyStore(InputStream keyStoreStream, String storePass) throws GeneralSecurityException, IOException {
            KeyStore trustStore = KeyStore.getInstance("JKS");
            SecurityUtils.loadKeyStore(trustStore, keyStoreStream, storePass);
            return trustCertificates(trustStore);
        }

        public final Builder trustCertificatesFromStream(InputStream certificateStream) throws GeneralSecurityException, IOException {
            KeyStore trustStore = KeyStore.getInstance("JKS");
            trustStore.load(null, null);
            SecurityUtils.loadKeyStoreFromCertificates(trustStore, CertificateFactory.getInstance("X.509"), certificateStream);
            return trustCertificates(trustStore);
        }

        public final Builder trustCertificates(KeyStore trustStore) throws GeneralSecurityException {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            SslUtils.initSslContext(sslContext, trustStore, TrustManagerFactory.getInstance("PKIX"));
            return setSslSocketFactory(sslContext.getSocketFactory());
        }

        public final Builder doNotValidateCertificate() throws GeneralSecurityException {
            this.hostnameVerifier = new HostnameVerifier() { // from class: com.google.api.client.util.SslUtils.2
                @Override // javax.net.ssl.HostnameVerifier
                public final boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }
            };
            this.sslSocketFactory = SslUtils.trustAllSSLContext().getSocketFactory();
            return this;
        }
    }
}
