package com.google.android.gms.internal;

import com.google.api.client.http.HttpMethods;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;

/* loaded from: classes.dex */
public final class zzz implements zzy {
    private final zza zzce;
    private final SSLSocketFactory zzcf;

    /* loaded from: classes.dex */
    public interface zza {
        String zzh$16915f7f();
    }

    public zzz() {
        this((byte) 0);
    }

    private zzz(byte b) {
        this((char) 0);
    }

    private zzz(char c) {
        this.zzce = null;
        this.zzcf = null;
    }

    private static HttpEntity zza(HttpURLConnection httpURLConnection) {
        InputStream errorStream;
        BasicHttpEntity basicHttpEntity = new BasicHttpEntity();
        try {
            errorStream = httpURLConnection.getInputStream();
        } catch (IOException e) {
            errorStream = httpURLConnection.getErrorStream();
        }
        basicHttpEntity.setContent(errorStream);
        basicHttpEntity.setContentLength(httpURLConnection.getContentLength());
        basicHttpEntity.setContentEncoding(httpURLConnection.getContentEncoding());
        basicHttpEntity.setContentType(httpURLConnection.getContentType());
        return basicHttpEntity;
    }

    private static void zzb(HttpURLConnection httpURLConnection, zzk<?> zzkVar) throws IOException, com.google.android.gms.internal.zza {
        byte[] zzp = zzkVar.zzp();
        if (zzp != null) {
            httpURLConnection.setDoOutput(true);
            httpURLConnection.addRequestProperty("Content-Type", zzk.zzo());
            DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
            dataOutputStream.write(zzp);
            dataOutputStream.close();
        }
    }

    @Override // com.google.android.gms.internal.zzy
    public final HttpResponse zza(zzk<?> zzkVar, Map<String, String> map) throws IOException, com.google.android.gms.internal.zza {
        String str;
        String str2 = zzkVar.zzae;
        HashMap hashMap = new HashMap();
        hashMap.putAll(zzkVar.getHeaders());
        hashMap.putAll(map);
        if (this.zzce != null) {
            str = this.zzce.zzh$16915f7f();
            if (str == null) {
                String valueOf = String.valueOf(str2);
                throw new IOException(valueOf.length() != 0 ? "URL blocked by rewriter: ".concat(valueOf) : new String("URL blocked by rewriter: "));
            }
        } else {
            str = str2;
        }
        URL url = new URL(str);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        int zzs = zzkVar.zzs();
        httpURLConnection.setConnectTimeout(zzs);
        httpURLConnection.setReadTimeout(zzs);
        httpURLConnection.setUseCaches(false);
        httpURLConnection.setDoInput(true);
        if ("https".equals(url.getProtocol()) && this.zzcf != null) {
            ((HttpsURLConnection) httpURLConnection).setSSLSocketFactory(this.zzcf);
        }
        for (String str3 : hashMap.keySet()) {
            httpURLConnection.addRequestProperty(str3, (String) hashMap.get(str3));
        }
        switch (zzkVar.zzad) {
            case -1:
                break;
            case 0:
                httpURLConnection.setRequestMethod("GET");
                break;
            case 1:
                httpURLConnection.setRequestMethod("POST");
                zzb(httpURLConnection, zzkVar);
                break;
            case 2:
                httpURLConnection.setRequestMethod("PUT");
                zzb(httpURLConnection, zzkVar);
                break;
            case 3:
                httpURLConnection.setRequestMethod("DELETE");
                break;
            case 4:
                httpURLConnection.setRequestMethod(HttpMethods.HEAD);
                break;
            case 5:
                httpURLConnection.setRequestMethod(HttpMethods.OPTIONS);
                break;
            case 6:
                httpURLConnection.setRequestMethod(HttpMethods.TRACE);
                break;
            case 7:
                httpURLConnection.setRequestMethod(HttpMethods.PATCH);
                zzb(httpURLConnection, zzkVar);
                break;
            default:
                throw new IllegalStateException("Unknown method type.");
        }
        ProtocolVersion protocolVersion = new ProtocolVersion("HTTP", 1, 1);
        if (httpURLConnection.getResponseCode() == -1) {
            throw new IOException("Could not retrieve response code from HttpUrlConnection.");
        }
        BasicHttpResponse basicHttpResponse = new BasicHttpResponse(new BasicStatusLine(protocolVersion, httpURLConnection.getResponseCode(), httpURLConnection.getResponseMessage()));
        basicHttpResponse.setEntity(zza(httpURLConnection));
        for (Map.Entry<String, List<String>> entry : httpURLConnection.getHeaderFields().entrySet()) {
            if (entry.getKey() != null) {
                basicHttpResponse.addHeader(new BasicHeader(entry.getKey(), entry.getValue().get(0)));
            }
        }
        return basicHttpResponse;
    }
}
