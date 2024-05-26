package com.bumptech.glide.load.data;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.util.ContentLengthInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

/* loaded from: classes.dex */
public final class HttpUrlFetcher implements DataFetcher<InputStream> {
    private static final HttpUrlConnectionFactory DEFAULT_CONNECTION_FACTORY = new DefaultHttpUrlConnectionFactory(0);
    private final HttpUrlConnectionFactory connectionFactory;
    private final GlideUrl glideUrl;
    private volatile boolean isCancelled;
    private InputStream stream;
    private HttpURLConnection urlConnection;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public interface HttpUrlConnectionFactory {
        HttpURLConnection build(URL url) throws IOException;
    }

    public HttpUrlFetcher(GlideUrl glideUrl) {
        this(glideUrl, DEFAULT_CONNECTION_FACTORY);
    }

    private HttpUrlFetcher(GlideUrl glideUrl, HttpUrlConnectionFactory connectionFactory) {
        this.glideUrl = glideUrl;
        this.connectionFactory = connectionFactory;
    }

    private InputStream loadDataWithRedirects(URL url, int redirects, URL lastUrl, Map<String, String> headers) throws IOException {
        while (redirects < 5) {
            if (lastUrl != null) {
                try {
                    if (url.toURI().equals(lastUrl.toURI())) {
                        throw new IOException("In re-direct loop");
                        break;
                    }
                } catch (URISyntaxException e) {
                }
            }
            this.urlConnection = this.connectionFactory.build(url);
            for (Map.Entry<String, String> headerEntry : headers.entrySet()) {
                this.urlConnection.addRequestProperty(headerEntry.getKey(), headerEntry.getValue());
            }
            this.urlConnection.setConnectTimeout(2500);
            this.urlConnection.setReadTimeout(2500);
            this.urlConnection.setUseCaches(false);
            this.urlConnection.setDoInput(true);
            this.urlConnection.connect();
            if (this.isCancelled) {
                return null;
            }
            int statusCode = this.urlConnection.getResponseCode();
            if (statusCode / 100 == 2) {
                HttpURLConnection httpURLConnection = this.urlConnection;
                if (TextUtils.isEmpty(httpURLConnection.getContentEncoding())) {
                    this.stream = ContentLengthInputStream.obtain(httpURLConnection.getInputStream(), httpURLConnection.getContentLength());
                } else {
                    if (Log.isLoggable("HttpUrlFetcher", 3)) {
                        new StringBuilder("Got non empty content encoding: ").append(httpURLConnection.getContentEncoding());
                    }
                    this.stream = httpURLConnection.getInputStream();
                }
                return this.stream;
            }
            if (statusCode / 100 == 3) {
                String redirectUrlString = this.urlConnection.getHeaderField("Location");
                if (TextUtils.isEmpty(redirectUrlString)) {
                    throw new IOException("Received empty or null redirect url");
                }
                URL redirectUrl = new URL(url, redirectUrlString);
                redirects++;
                lastUrl = url;
                url = redirectUrl;
            } else {
                if (statusCode == -1) {
                    throw new IOException("Unable to retrieve response code from HttpUrlConnection.");
                }
                throw new IOException("Request failed " + statusCode + ": " + this.urlConnection.getResponseMessage());
            }
        }
        throw new IOException("Too many (> 5) redirects!");
    }

    @Override // com.bumptech.glide.load.data.DataFetcher
    public final void cleanup() {
        if (this.stream != null) {
            try {
                this.stream.close();
            } catch (IOException e) {
            }
        }
        if (this.urlConnection != null) {
            this.urlConnection.disconnect();
        }
    }

    @Override // com.bumptech.glide.load.data.DataFetcher
    public final String getId() {
        return this.glideUrl.getCacheKey();
    }

    @Override // com.bumptech.glide.load.data.DataFetcher
    public final void cancel() {
        this.isCancelled = true;
    }

    /* loaded from: classes.dex */
    private static class DefaultHttpUrlConnectionFactory implements HttpUrlConnectionFactory {
        private DefaultHttpUrlConnectionFactory() {
        }

        /* synthetic */ DefaultHttpUrlConnectionFactory(byte b) {
            this();
        }

        @Override // com.bumptech.glide.load.data.HttpUrlFetcher.HttpUrlConnectionFactory
        public final HttpURLConnection build(URL url) throws IOException {
            return (HttpURLConnection) url.openConnection();
        }
    }

    @Override // com.bumptech.glide.load.data.DataFetcher
    public final /* bridge */ /* synthetic */ InputStream loadData(Priority x0) throws Exception {
        GlideUrl glideUrl = this.glideUrl;
        if (glideUrl.safeUrl == null) {
            if (TextUtils.isEmpty(glideUrl.safeStringUrl)) {
                String str = glideUrl.stringUrl;
                if (TextUtils.isEmpty(str)) {
                    str = glideUrl.url.toString();
                }
                glideUrl.safeStringUrl = Uri.encode(str, "@#&=*+-_.,:!?()/~'%");
            }
            glideUrl.safeUrl = new URL(glideUrl.safeStringUrl);
        }
        return loadDataWithRedirects(glideUrl.safeUrl, 0, null, this.glideUrl.headers.getHeaders());
    }
}
