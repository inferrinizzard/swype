package com.bumptech.glide.load.model;

import android.text.TextUtils;
import java.net.URL;

/* loaded from: classes.dex */
public class GlideUrl {
    public final Headers headers;
    public String safeStringUrl;
    public URL safeUrl;
    public final String stringUrl;
    public final URL url;

    public GlideUrl(URL url) {
        this(url, Headers.DEFAULT);
    }

    public GlideUrl(String url) {
        this(url, Headers.DEFAULT);
    }

    private GlideUrl(URL url, Headers headers) {
        if (url == null) {
            throw new IllegalArgumentException("URL must not be null!");
        }
        if (headers == null) {
            throw new IllegalArgumentException("Headers must not be null");
        }
        this.url = url;
        this.stringUrl = null;
        this.headers = headers;
    }

    public GlideUrl(String url, Headers headers) {
        if (TextUtils.isEmpty(url)) {
            throw new IllegalArgumentException("String url must not be empty or null: " + url);
        }
        if (headers == null) {
            throw new IllegalArgumentException("Headers must not be null");
        }
        this.stringUrl = url;
        this.url = null;
        this.headers = headers;
    }

    public final String getCacheKey() {
        return this.stringUrl != null ? this.stringUrl : this.url.toString();
    }

    public String toString() {
        return getCacheKey() + '\n' + this.headers.toString();
    }

    public boolean equals(Object o) {
        if (!(o instanceof GlideUrl)) {
            return false;
        }
        GlideUrl other = (GlideUrl) o;
        return getCacheKey().equals(other.getCacheKey()) && this.headers.equals(other.headers);
    }

    public int hashCode() {
        int hashCode = getCacheKey().hashCode();
        return (hashCode * 31) + this.headers.hashCode();
    }
}
