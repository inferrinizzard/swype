package com.google.android.gms.ads.internal.formats;

import android.os.Bundle;
import com.google.android.gms.ads.internal.formats.zzh;
import com.google.android.gms.internal.zzdr;
import com.google.android.gms.internal.zzdx;
import com.google.android.gms.internal.zzin;
import java.util.List;

@zzin
/* loaded from: classes.dex */
public class zze extends zzdx.zza implements zzh.zza {
    private Bundle mExtras;
    private Object zzail = new Object();
    private String zzbfg;
    private List<zzc> zzbfh;
    private String zzbfi;
    private String zzbfk;
    private zza zzbfo;
    private zzh zzbfp;
    private zzdr zzbfq;
    private String zzbfr;

    public zze(String str, List list, String str2, zzdr zzdrVar, String str3, String str4, zza zzaVar, Bundle bundle) {
        this.zzbfg = str;
        this.zzbfh = list;
        this.zzbfi = str2;
        this.zzbfq = zzdrVar;
        this.zzbfk = str3;
        this.zzbfr = str4;
        this.zzbfo = zzaVar;
        this.mExtras = bundle;
    }

    @Override // com.google.android.gms.internal.zzdx
    public void destroy() {
        this.zzbfg = null;
        this.zzbfh = null;
        this.zzbfi = null;
        this.zzbfq = null;
        this.zzbfk = null;
        this.zzbfr = null;
        this.zzbfo = null;
        this.mExtras = null;
        this.zzail = null;
        this.zzbfp = null;
    }

    @Override // com.google.android.gms.internal.zzdx
    public String getAdvertiser() {
        return this.zzbfr;
    }

    @Override // com.google.android.gms.internal.zzdx
    public String getBody() {
        return this.zzbfi;
    }

    @Override // com.google.android.gms.internal.zzdx
    public String getCallToAction() {
        return this.zzbfk;
    }

    @Override // com.google.android.gms.ads.internal.formats.zzh.zza
    public String getCustomTemplateId() {
        return "";
    }

    @Override // com.google.android.gms.internal.zzdx
    public Bundle getExtras() {
        return this.mExtras;
    }

    @Override // com.google.android.gms.internal.zzdx
    public String getHeadline() {
        return this.zzbfg;
    }

    @Override // com.google.android.gms.internal.zzdx
    public List getImages() {
        return this.zzbfh;
    }

    @Override // com.google.android.gms.ads.internal.formats.zzh.zza
    public void zzb(zzh zzhVar) {
        synchronized (this.zzail) {
            this.zzbfp = zzhVar;
        }
    }

    @Override // com.google.android.gms.internal.zzdx
    public com.google.android.gms.dynamic.zzd zzkv() {
        return com.google.android.gms.dynamic.zze.zzac(this.zzbfp);
    }

    @Override // com.google.android.gms.ads.internal.formats.zzh.zza
    public String zzkw() {
        return "1";
    }

    @Override // com.google.android.gms.ads.internal.formats.zzh.zza
    public zza zzkx() {
        return this.zzbfo;
    }

    @Override // com.google.android.gms.internal.zzdx
    public zzdr zzky() {
        return this.zzbfq;
    }
}
