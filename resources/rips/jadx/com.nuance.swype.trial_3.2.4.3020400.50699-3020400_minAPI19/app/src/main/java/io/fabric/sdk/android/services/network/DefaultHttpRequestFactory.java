package io.fabric.sdk.android.services.network;

import io.fabric.sdk.android.DefaultLogger;
import io.fabric.sdk.android.Logger;
import java.util.Locale;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

/* loaded from: classes.dex */
public final class DefaultHttpRequestFactory implements HttpRequestFactory {
    private boolean attemptedSslInit;
    private final Logger logger;
    private PinningInfoProvider pinningInfo;
    private SSLSocketFactory sslSocketFactory;

    public DefaultHttpRequestFactory() {
        this(new DefaultLogger((byte) 0));
    }

    public DefaultHttpRequestFactory(Logger logger) {
        this.logger = logger;
    }

    @Override // io.fabric.sdk.android.services.network.HttpRequestFactory
    public final void setPinningInfoProvider(PinningInfoProvider pinningInfo) {
        if (this.pinningInfo != pinningInfo) {
            this.pinningInfo = pinningInfo;
            resetSSLSocketFactory();
        }
    }

    private synchronized void resetSSLSocketFactory() {
        this.attemptedSslInit = false;
        this.sslSocketFactory = null;
    }

    @Override // io.fabric.sdk.android.services.network.HttpRequestFactory
    public final HttpRequest buildHttpRequest(HttpMethod method, String url, Map<String, String> queryParams) {
        HttpRequest httpRequest;
        SSLSocketFactory sslSocketFactory;
        switch (method) {
            case GET:
                httpRequest = HttpRequest.get$6df498ee(url, queryParams);
                break;
            case POST:
                httpRequest = HttpRequest.post$6df498ee(url, queryParams);
                break;
            case PUT:
                httpRequest = HttpRequest.put(url);
                break;
            case DELETE:
                httpRequest = HttpRequest.delete(url);
                break;
            default:
                throw new IllegalArgumentException("Unsupported HTTP method!");
        }
        if ((url != null && url.toLowerCase(Locale.US).startsWith("https")) && this.pinningInfo != null && (sslSocketFactory = getSSLSocketFactory()) != null) {
            ((HttpsURLConnection) httpRequest.getConnection()).setSSLSocketFactory(sslSocketFactory);
        }
        return httpRequest;
    }

    private synchronized SSLSocketFactory getSSLSocketFactory() {
        if (this.sslSocketFactory == null && !this.attemptedSslInit) {
            this.sslSocketFactory = initSSLSocketFactory();
        }
        return this.sslSocketFactory;
    }

    private synchronized SSLSocketFactory initSSLSocketFactory() {
        SSLSocketFactory sSLSocketFactory = null;
        synchronized (this) {
            this.attemptedSslInit = true;
            try {
                PinningInfoProvider pinningInfoProvider = this.pinningInfo;
                SSLContext sSLContext = SSLContext.getInstance("TLS");
                sSLContext.init(null, new TrustManager[]{new PinningTrustManager(new SystemKeyStore(pinningInfoProvider.getKeyStoreStream(), pinningInfoProvider.getKeyStorePassword()), pinningInfoProvider)}, null);
                sSLSocketFactory = sSLContext.getSocketFactory();
            } catch (Exception e) {
                this.logger.e("Fabric", "Exception while validating pinned certs", e);
            }
        }
        return sSLSocketFactory;
    }
}
