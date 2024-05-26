package com.google.android.gms.ads.internal.formats;

import android.support.v4.util.SimpleArrayMap;
import com.google.android.gms.ads.internal.formats.zzh;
import com.google.android.gms.internal.zzdr;
import com.google.android.gms.internal.zzdz;
import com.google.android.gms.internal.zzin;
import com.nuance.connect.comm.MessageAPI;
import java.util.Arrays;
import java.util.List;

@zzin
/* loaded from: classes.dex */
public class zzf extends zzdz.zza implements zzh.zza {
    private final Object zzail = new Object();
    private final zza zzbfo;
    private zzh zzbfp;
    private final String zzbfs;
    private final SimpleArrayMap<String, zzc> zzbft;
    private final SimpleArrayMap<String, String> zzbfu;

    public zzf(String str, SimpleArrayMap<String, zzc> simpleArrayMap, SimpleArrayMap<String, String> simpleArrayMap2, zza zzaVar) {
        this.zzbfs = str;
        this.zzbft = simpleArrayMap;
        this.zzbfu = simpleArrayMap2;
        this.zzbfo = zzaVar;
    }

    @Override // com.google.android.gms.internal.zzdz
    public List<String> getAvailableAssetNames() {
        int i = 0;
        String[] strArr = new String[this.zzbft.size() + this.zzbfu.size()];
        int i2 = 0;
        for (int i3 = 0; i3 < this.zzbft.size(); i3++) {
            strArr[i2] = this.zzbft.keyAt(i3);
            i2++;
        }
        while (i < this.zzbfu.size()) {
            strArr[i2] = this.zzbfu.keyAt(i);
            i++;
            i2++;
        }
        return Arrays.asList(strArr);
    }

    @Override // com.google.android.gms.internal.zzdz, com.google.android.gms.ads.internal.formats.zzh.zza
    public String getCustomTemplateId() {
        return this.zzbfs;
    }

    @Override // com.google.android.gms.internal.zzdz
    public void performClick(String str) {
        synchronized (this.zzail) {
            if (this.zzbfp == null) {
                com.google.android.gms.ads.internal.util.client.zzb.e("Attempt to call performClick before ad initialized.");
            } else {
                this.zzbfp.zza(str, null, null, null);
            }
        }
    }

    @Override // com.google.android.gms.internal.zzdz
    public void recordImpression() {
        synchronized (this.zzail) {
            if (this.zzbfp == null) {
                com.google.android.gms.ads.internal.util.client.zzb.e("Attempt to perform recordImpression before ad initialized.");
            } else {
                this.zzbfp.recordImpression();
            }
        }
    }

    @Override // com.google.android.gms.internal.zzdz
    public String zzat(String str) {
        return this.zzbfu.get(str);
    }

    @Override // com.google.android.gms.internal.zzdz
    public zzdr zzau(String str) {
        return this.zzbft.get(str);
    }

    @Override // com.google.android.gms.ads.internal.formats.zzh.zza
    public void zzb(zzh zzhVar) {
        synchronized (this.zzail) {
            this.zzbfp = zzhVar;
        }
    }

    @Override // com.google.android.gms.ads.internal.formats.zzh.zza
    public String zzkw() {
        return MessageAPI.SESSION_ID;
    }

    @Override // com.google.android.gms.ads.internal.formats.zzh.zza
    public zza zzkx() {
        return this.zzbfo;
    }
}
