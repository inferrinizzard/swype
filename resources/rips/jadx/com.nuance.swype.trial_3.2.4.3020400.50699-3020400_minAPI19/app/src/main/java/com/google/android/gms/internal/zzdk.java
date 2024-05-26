package com.google.android.gms.internal;

import android.text.TextUtils;
import com.facebook.internal.NativeProtocol;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@zzin
/* loaded from: classes.dex */
public final class zzdk {
    public boolean zzbdo;
    public String zzbeh;
    zzdi zzbei;
    zzdk zzbej;
    private final List<zzdi> zzbef = new LinkedList();
    private final Map<String, String> zzbeg = new LinkedHashMap();
    public final Object zzail = new Object();

    public zzdk(boolean z, String str, String str2) {
        this.zzbdo = z;
        this.zzbeg.put(NativeProtocol.WEB_DIALOG_ACTION, str);
        this.zzbeg.put("ad_format", str2);
    }

    public final boolean zza(zzdi zzdiVar, long j, String... strArr) {
        synchronized (this.zzail) {
            for (String str : strArr) {
                this.zzbef.add(new zzdi(j, str, zzdiVar));
            }
        }
        return true;
    }

    public final boolean zza(zzdi zzdiVar, String... strArr) {
        if (!this.zzbdo || zzdiVar == null) {
            return false;
        }
        return zza(zzdiVar, com.google.android.gms.ads.internal.zzu.zzfu().elapsedRealtime(), strArr);
    }

    public final zzdi zzc(long j) {
        if (this.zzbdo) {
            return new zzdi(j, null, null);
        }
        return null;
    }

    public final zzdi zzkg() {
        return zzc(com.google.android.gms.ads.internal.zzu.zzfu().elapsedRealtime());
    }

    public final zzdi zzkj() {
        zzdi zzdiVar;
        synchronized (this.zzail) {
            zzdiVar = this.zzbei;
        }
        return zzdiVar;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Map<String, String> zzm() {
        Map<String, String> zza;
        synchronized (this.zzail) {
            zzde zzsl = com.google.android.gms.ads.internal.zzu.zzft().zzsl();
            zza = (zzsl == null || this.zzbej == null) ? this.zzbeg : zzsl.zza(this.zzbeg, this.zzbej.zzm());
        }
        return zza;
    }

    public final String zzki() {
        String sb;
        StringBuilder sb2 = new StringBuilder();
        synchronized (this.zzail) {
            for (zzdi zzdiVar : this.zzbef) {
                long j = zzdiVar.zzbeb;
                String str = zzdiVar.zzbec;
                zzdi zzdiVar2 = zzdiVar.zzbed;
                if (zzdiVar2 != null && j > 0) {
                    sb2.append(str).append('.').append(j - zzdiVar2.zzbeb).append(',');
                }
            }
            this.zzbef.clear();
            if (!TextUtils.isEmpty(this.zzbeh)) {
                sb2.append(this.zzbeh);
            } else if (sb2.length() > 0) {
                sb2.setLength(sb2.length() - 1);
            }
            sb = sb2.toString();
        }
        return sb;
    }

    public final void zzh(String str, String str2) {
        zzde zzsl;
        if (!this.zzbdo || TextUtils.isEmpty(str2) || (zzsl = com.google.android.gms.ads.internal.zzu.zzft().zzsl()) == null) {
            return;
        }
        synchronized (this.zzail) {
            zzdh zzaq = zzsl.zzaq(str);
            Map<String, String> map = this.zzbeg;
            map.put(str, zzaq.zzg(map.get(str), str2));
        }
    }
}
