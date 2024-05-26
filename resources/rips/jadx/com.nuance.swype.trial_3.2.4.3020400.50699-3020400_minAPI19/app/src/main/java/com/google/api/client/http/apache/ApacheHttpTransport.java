package com.google.api.client.http.apache;

import com.google.api.client.http.HttpMethods;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.repackaged.com.google.common.base.Preconditions;
import com.google.api.client.util.SecurityUtils;
import com.google.api.client.util.SslUtils;
import com.nuance.swype.input.R;
import java.io.IOException;
import java.io.InputStream;
import java.net.ProxySelector;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import org.apache.http.HttpHost;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.conn.ProxySelectorRoutePlanner;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

/* loaded from: classes.dex */
public final class ApacheHttpTransport extends HttpTransport {
    private final HttpClient httpClient;

    public ApacheHttpTransport() {
        this(newDefaultHttpClient());
    }

    public ApacheHttpTransport(HttpClient httpClient) {
        this.httpClient = httpClient;
        HttpParams params = httpClient.getParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        params.setBooleanParameter("http.protocol.handle-redirects", false);
    }

    public static DefaultHttpClient newDefaultHttpClient() {
        return newDefaultHttpClient(SSLSocketFactory.getSocketFactory(), newDefaultHttpParams(), ProxySelector.getDefault());
    }

    static HttpParams newDefaultHttpParams() {
        HttpParams params = new BasicHttpParams();
        HttpConnectionParams.setStaleCheckingEnabled(params, false);
        HttpConnectionParams.setSocketBufferSize(params, 8192);
        ConnManagerParams.setMaxTotalConnections(params, 200);
        ConnManagerParams.setMaxConnectionsPerRoute(params, new ConnPerRouteBean(20));
        return params;
    }

    static DefaultHttpClient newDefaultHttpClient(SSLSocketFactory socketFactory, HttpParams params, ProxySelector proxySelector) {
        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        registry.register(new Scheme("https", socketFactory, R.styleable.ThemeTemplate_wclCjkChineseCloudBackgroundImage));
        ClientConnectionManager connectionManager = new ThreadSafeClientConnManager(params, registry);
        DefaultHttpClient defaultHttpClient = new DefaultHttpClient(connectionManager, params);
        defaultHttpClient.setHttpRequestRetryHandler(new DefaultHttpRequestRetryHandler(0, false));
        if (proxySelector != null) {
            defaultHttpClient.setRoutePlanner(new ProxySelectorRoutePlanner(registry, proxySelector));
        }
        return defaultHttpClient;
    }

    @Override // com.google.api.client.http.HttpTransport
    public final boolean supportsMethod(String method) {
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.api.client.http.HttpTransport
    public final ApacheHttpRequest buildRequest(String method, String url) {
        HttpRequestBase requestBase;
        if (method.equals("DELETE")) {
            requestBase = new HttpDelete(url);
        } else if (method.equals("GET")) {
            requestBase = new HttpGet(url);
        } else if (method.equals(HttpMethods.HEAD)) {
            requestBase = new HttpHead(url);
        } else if (method.equals("POST")) {
            requestBase = new HttpPost(url);
        } else if (method.equals("PUT")) {
            requestBase = new HttpPut(url);
        } else if (method.equals(HttpMethods.TRACE)) {
            requestBase = new HttpTrace(url);
        } else if (method.equals(HttpMethods.OPTIONS)) {
            requestBase = new HttpOptions(url);
        } else {
            requestBase = new HttpExtensionMethod(method, url);
        }
        return new ApacheHttpRequest(this.httpClient, requestBase);
    }

    @Override // com.google.api.client.http.HttpTransport
    public final void shutdown() {
        this.httpClient.getConnectionManager().shutdown();
    }

    public final HttpClient getHttpClient() {
        return this.httpClient;
    }

    /* loaded from: classes.dex */
    public static final class Builder {
        private SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();
        private HttpParams params = ApacheHttpTransport.newDefaultHttpParams();
        private ProxySelector proxySelector = ProxySelector.getDefault();

        public final Builder setProxy(HttpHost proxy) {
            ConnRouteParams.setDefaultProxy(this.params, proxy);
            if (proxy != null) {
                this.proxySelector = null;
            }
            return this;
        }

        public final Builder setProxySelector(ProxySelector proxySelector) {
            this.proxySelector = proxySelector;
            if (proxySelector != null) {
                ConnRouteParams.setDefaultProxy(this.params, null);
            }
            return this;
        }

        public final Builder doNotValidateCertificate() throws GeneralSecurityException {
            this.socketFactory = new SSLSocketFactoryExtension(SslUtils.trustAllSSLContext());
            this.socketFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            return this;
        }

        public final SSLSocketFactory getSSLSocketFactory() {
            return this.socketFactory;
        }

        public final HttpParams getHttpParams() {
            return this.params;
        }

        public final ApacheHttpTransport build() {
            return new ApacheHttpTransport(ApacheHttpTransport.newDefaultHttpClient(this.socketFactory, this.params, this.proxySelector));
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
            return setSocketFactory(new SSLSocketFactoryExtension(sslContext));
        }

        public final Builder setSocketFactory(SSLSocketFactory socketFactory) {
            this.socketFactory = (SSLSocketFactory) Preconditions.checkNotNull(socketFactory);
            return this;
        }
    }
}
