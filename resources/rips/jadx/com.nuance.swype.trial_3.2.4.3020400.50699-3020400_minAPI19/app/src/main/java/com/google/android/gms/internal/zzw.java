package com.google.android.gms.internal;

import com.google.api.client.http.HttpMethods;
import java.io.IOException;
import java.net.URI;
import java.util.Map;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

/* loaded from: classes.dex */
public final class zzw implements zzy {
    protected final HttpClient zzcd;

    /* loaded from: classes.dex */
    public static final class zza extends HttpEntityEnclosingRequestBase {
        public zza() {
        }

        public zza(String str) {
            setURI(URI.create(str));
        }

        @Override // org.apache.http.client.methods.HttpRequestBase, org.apache.http.client.methods.HttpUriRequest
        public final String getMethod() {
            return HttpMethods.PATCH;
        }
    }

    public zzw(HttpClient httpClient) {
        this.zzcd = httpClient;
    }

    private static void zza(HttpEntityEnclosingRequestBase httpEntityEnclosingRequestBase, zzk<?> zzkVar) throws com.google.android.gms.internal.zza {
        byte[] zzp = zzkVar.zzp();
        if (zzp != null) {
            httpEntityEnclosingRequestBase.setEntity(new ByteArrayEntity(zzp));
        }
    }

    private static void zza(HttpUriRequest httpUriRequest, Map<String, String> map) {
        for (String str : map.keySet()) {
            httpUriRequest.setHeader(str, map.get(str));
        }
    }

    @Override // com.google.android.gms.internal.zzy
    public final HttpResponse zza(zzk<?> zzkVar, Map<String, String> map) throws IOException, com.google.android.gms.internal.zza {
        HttpRequestBase httpRequestBase;
        switch (zzkVar.zzad) {
            case -1:
                httpRequestBase = new HttpGet(zzkVar.zzae);
                break;
            case 0:
                httpRequestBase = new HttpGet(zzkVar.zzae);
                break;
            case 1:
                HttpPost httpPost = new HttpPost(zzkVar.zzae);
                httpPost.addHeader("Content-Type", zzk.zzo());
                zza(httpPost, zzkVar);
                httpRequestBase = httpPost;
                break;
            case 2:
                HttpPut httpPut = new HttpPut(zzkVar.zzae);
                httpPut.addHeader("Content-Type", zzk.zzo());
                zza(httpPut, zzkVar);
                httpRequestBase = httpPut;
                break;
            case 3:
                httpRequestBase = new HttpDelete(zzkVar.zzae);
                break;
            case 4:
                httpRequestBase = new HttpHead(zzkVar.zzae);
                break;
            case 5:
                httpRequestBase = new HttpOptions(zzkVar.zzae);
                break;
            case 6:
                httpRequestBase = new HttpTrace(zzkVar.zzae);
                break;
            case 7:
                zza zzaVar = new zza(zzkVar.zzae);
                zzaVar.addHeader("Content-Type", zzk.zzo());
                zza(zzaVar, zzkVar);
                httpRequestBase = zzaVar;
                break;
            default:
                throw new IllegalStateException("Unknown request method.");
        }
        zza(httpRequestBase, map);
        zza(httpRequestBase, zzkVar.getHeaders());
        HttpParams params = httpRequestBase.getParams();
        int zzs = zzkVar.zzs();
        HttpConnectionParams.setConnectionTimeout(params, 5000);
        HttpConnectionParams.setSoTimeout(params, zzs);
        return this.zzcd.execute(httpRequestBase);
    }
}
