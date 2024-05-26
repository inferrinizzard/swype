package com.google.android.gms.ads.internal.util.client;

import com.google.android.gms.ads.internal.client.zzm;
import com.google.android.gms.ads.internal.util.client.zza;
import com.google.android.gms.internal.zzin;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

@zzin
/* loaded from: classes.dex */
public class zzc implements zza.InterfaceC0039zza {
    private final String zzbjf;

    public zzc() {
        this(null);
    }

    public zzc(String str) {
        this.zzbjf = str;
    }

    @Override // com.google.android.gms.ads.internal.util.client.zza.InterfaceC0039zza
    public void zzcr(String str) {
        try {
            String valueOf = String.valueOf(str);
            zzb.zzcv(valueOf.length() != 0 ? "Pinging URL: ".concat(valueOf) : new String("Pinging URL: "));
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(str).openConnection();
            try {
                zzm.zziw().zza(true, httpURLConnection, this.zzbjf);
                int responseCode = httpURLConnection.getResponseCode();
                if (responseCode < 200 || responseCode >= 300) {
                    zzb.zzcx(new StringBuilder(String.valueOf(str).length() + 65).append("Received non-success response code ").append(responseCode).append(" from pinging URL: ").append(str).toString());
                }
            } finally {
                httpURLConnection.disconnect();
            }
        } catch (IOException e) {
            String valueOf2 = String.valueOf(e.getMessage());
            zzb.zzcx(new StringBuilder(String.valueOf(str).length() + 27 + String.valueOf(valueOf2).length()).append("Error while pinging URL: ").append(str).append(". ").append(valueOf2).toString());
        } catch (IndexOutOfBoundsException e2) {
            String valueOf3 = String.valueOf(e2.getMessage());
            zzb.zzcx(new StringBuilder(String.valueOf(str).length() + 32 + String.valueOf(valueOf3).length()).append("Error while parsing ping URL: ").append(str).append(". ").append(valueOf3).toString());
        } catch (RuntimeException e3) {
            String valueOf4 = String.valueOf(e3.getMessage());
            zzb.zzcx(new StringBuilder(String.valueOf(str).length() + 27 + String.valueOf(valueOf4).length()).append("Error while pinging URL: ").append(str).append(". ").append(valueOf4).toString());
        }
    }
}
