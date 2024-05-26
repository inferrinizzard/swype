package com.google.android.gms.internal;

import android.os.SystemClock;
import com.google.android.gms.internal.zzb;
import com.nuance.swype.input.hardkey.HardKeyboardManager;
import com.nuance.swypeconnect.ac.ACReportingService;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.cookie.DateUtils;

/* loaded from: classes.dex */
public final class zzt implements zzf {
    protected static final boolean DEBUG = zzs.DEBUG;
    private static int zzbn = ACReportingService.REASON_DATA_POINT_IDENTIFIER_NOT_FOUND;
    private static int zzbo = HardKeyboardManager.META_CTRL_ON;
    protected final zzy zzbp;
    protected final zzu zzbq;

    public zzt(zzy zzyVar) {
        this(zzyVar, new zzu(zzbo));
    }

    private zzt(zzy zzyVar, zzu zzuVar) {
        this.zzbp = zzyVar;
        this.zzbq = zzuVar;
    }

    private static Map<String, String> zza(Header[] headerArr) {
        TreeMap treeMap = new TreeMap(String.CASE_INSENSITIVE_ORDER);
        for (int i = 0; i < headerArr.length; i++) {
            treeMap.put(headerArr[i].getName(), headerArr[i].getValue());
        }
        return treeMap;
    }

    private byte[] zza(HttpEntity httpEntity) throws IOException, zzp {
        zzaa zzaaVar = new zzaa(this.zzbq, (int) httpEntity.getContentLength());
        try {
            InputStream content = httpEntity.getContent();
            if (content == null) {
                throw new zzp();
            }
            byte[] zzb = this.zzbq.zzb(1024);
            while (true) {
                int read = content.read(zzb);
                if (read == -1) {
                    break;
                }
                zzaaVar.write(zzb, 0, read);
            }
            byte[] byteArray = zzaaVar.toByteArray();
            try {
                httpEntity.consumeContent();
            } catch (IOException e) {
                zzs.zza("Error occured when calling consumingContent", new Object[0]);
            }
            this.zzbq.zza(zzb);
            zzaaVar.close();
            return byteArray;
        } catch (Throwable th) {
            try {
                httpEntity.consumeContent();
            } catch (IOException e2) {
                zzs.zza("Error occured when calling consumingContent", new Object[0]);
            }
            this.zzbq.zza(null);
            zzaaVar.close();
            throw th;
        }
    }

    @Override // com.google.android.gms.internal.zzf
    public final zzi zza(zzk<?> zzkVar) throws zzr {
        byte[] bArr;
        long elapsedRealtime = SystemClock.elapsedRealtime();
        while (true) {
            HttpResponse httpResponse = null;
            Map<String, String> emptyMap = Collections.emptyMap();
            try {
                try {
                    HashMap hashMap = new HashMap();
                    zzb.zza zzaVar = zzkVar.zzao;
                    if (zzaVar != null) {
                        if (zzaVar.zza != null) {
                            hashMap.put("If-None-Match", zzaVar.zza);
                        }
                        if (zzaVar.zzc > 0) {
                            hashMap.put("If-Modified-Since", DateUtils.formatDate(new Date(zzaVar.zzc)));
                        }
                    }
                    HttpResponse zza = this.zzbp.zza(zzkVar, hashMap);
                    try {
                        StatusLine statusLine = zza.getStatusLine();
                        int statusCode = statusLine.getStatusCode();
                        emptyMap = zza(zza.getAllHeaders());
                        if (statusCode == 304) {
                            zzb.zza zzaVar2 = zzkVar.zzao;
                            if (zzaVar2 == null) {
                                return new zzi(304, null, emptyMap, true, SystemClock.elapsedRealtime() - elapsedRealtime);
                            }
                            zzaVar2.zzf.putAll(emptyMap);
                            return new zzi(304, zzaVar2.data, zzaVar2.zzf, true, SystemClock.elapsedRealtime() - elapsedRealtime);
                        }
                        bArr = zza.getEntity() != null ? zza(zza.getEntity()) : new byte[0];
                        try {
                            long elapsedRealtime2 = SystemClock.elapsedRealtime() - elapsedRealtime;
                            if (DEBUG || elapsedRealtime2 > zzbn) {
                                Object[] objArr = new Object[5];
                                objArr[0] = zzkVar;
                                objArr[1] = Long.valueOf(elapsedRealtime2);
                                objArr[2] = bArr != null ? Integer.valueOf(bArr.length) : "null";
                                objArr[3] = Integer.valueOf(statusLine.getStatusCode());
                                objArr[4] = Integer.valueOf(zzkVar.zzan.zzd());
                                zzs.zzb("HTTP response for request=<%s> [lifetime=%d], [size=%s], [rc=%d], [retryCount=%s]", objArr);
                            }
                            if (statusCode < 200 || statusCode > 299) {
                                throw new IOException();
                            }
                            return new zzi(statusCode, bArr, emptyMap, false, SystemClock.elapsedRealtime() - elapsedRealtime);
                        } catch (IOException e) {
                            e = e;
                            httpResponse = zza;
                            if (httpResponse == null) {
                                throw new zzj(e);
                            }
                            int statusCode2 = httpResponse.getStatusLine().getStatusCode();
                            zzs.zzc("Unexpected response code %d for %s", Integer.valueOf(statusCode2), zzkVar.zzae);
                            if (bArr == null) {
                                throw new zzh((byte) 0);
                            }
                            zzi zziVar = new zzi(statusCode2, bArr, emptyMap, false, SystemClock.elapsedRealtime() - elapsedRealtime);
                            if (statusCode2 != 401 && statusCode2 != 403) {
                                throw new zzp(zziVar);
                            }
                            zza("auth", zzkVar, new zza(zziVar));
                        }
                    } catch (IOException e2) {
                        e = e2;
                        bArr = null;
                        httpResponse = zza;
                    }
                } catch (MalformedURLException e3) {
                    String valueOf = String.valueOf(zzkVar.zzae);
                    throw new RuntimeException(valueOf.length() != 0 ? "Bad URL ".concat(valueOf) : new String("Bad URL "), e3);
                } catch (SocketTimeoutException e4) {
                    zza("socket", zzkVar, new zzq());
                } catch (ConnectTimeoutException e5) {
                    zza("connection", zzkVar, new zzq());
                }
            } catch (IOException e6) {
                e = e6;
                bArr = null;
            }
        }
    }

    private static void zza(String str, zzk<?> zzkVar, zzr zzrVar) throws zzr {
        zzo zzoVar = zzkVar.zzan;
        int zzs = zzkVar.zzs();
        try {
            zzoVar.zza(zzrVar);
            zzkVar.zzc(String.format("%s-retry [timeout=%s]", str, Integer.valueOf(zzs)));
        } catch (zzr e) {
            zzkVar.zzc(String.format("%s-timeout-giveup [timeout=%s]", str, Integer.valueOf(zzs)));
            throw e;
        }
    }
}
