package com.google.android.gms.ads.internal.formats;

import android.os.Bundle;
import com.google.android.gms.ads.internal.formats.zzh;
import com.google.android.gms.internal.zzdr;
import com.google.android.gms.internal.zzdv;
import com.google.android.gms.internal.zzin;
import com.nuance.connect.comm.MessageAPI;
import java.util.List;

@zzin
/* loaded from: classes.dex */
public class zzd extends zzdv.zza implements zzh.zza {
    private Bundle mExtras;
    private Object zzail = new Object();
    private String zzbfg;
    private List<zzc> zzbfh;
    private String zzbfi;
    private zzdr zzbfj;
    private String zzbfk;
    private double zzbfl;
    private String zzbfm;
    private String zzbfn;
    private zza zzbfo;
    private zzh zzbfp;

    public zzd(String str, List list, String str2, zzdr zzdrVar, String str3, double d, String str4, String str5, zza zzaVar, Bundle bundle) {
        this.zzbfg = str;
        this.zzbfh = list;
        this.zzbfi = str2;
        this.zzbfj = zzdrVar;
        this.zzbfk = str3;
        this.zzbfl = d;
        this.zzbfm = str4;
        this.zzbfn = str5;
        this.zzbfo = zzaVar;
        this.mExtras = bundle;
    }

    @Override // com.google.android.gms.internal.zzdv
    public void destroy() {
        this.zzbfg = null;
        this.zzbfh = null;
        this.zzbfi = null;
        this.zzbfj = null;
        this.zzbfk = null;
        this.zzbfl = 0.0d;
        this.zzbfm = null;
        this.zzbfn = null;
        this.zzbfo = null;
        this.mExtras = null;
        this.zzail = null;
        this.zzbfp = null;
    }

    @Override // com.google.android.gms.internal.zzdv
    public String getBody() {
        return this.zzbfi;
    }

    @Override // com.google.android.gms.internal.zzdv
    public String getCallToAction() {
        return this.zzbfk;
    }

    @Override // com.google.android.gms.ads.internal.formats.zzh.zza
    public String getCustomTemplateId() {
        return "";
    }

    @Override // com.google.android.gms.internal.zzdv
    public Bundle getExtras() {
        return this.mExtras;
    }

    @Override // com.google.android.gms.internal.zzdv
    public String getHeadline() {
        return this.zzbfg;
    }

    @Override // com.google.android.gms.internal.zzdv
    public List getImages() {
        return this.zzbfh;
    }

    @Override // com.google.android.gms.internal.zzdv
    public String getPrice() {
        return this.zzbfn;
    }

    @Override // com.google.android.gms.internal.zzdv
    public double getStarRating() {
        return this.zzbfl;
    }

    @Override // com.google.android.gms.internal.zzdv
    public String getStore() {
        return this.zzbfm;
    }

    @Override // com.google.android.gms.ads.internal.formats.zzh.zza
    public void zzb(zzh zzhVar) {
        synchronized (this.zzail) {
            this.zzbfp = zzhVar;
        }
    }

    @Override // com.google.android.gms.internal.zzdv
    public zzdr zzku() {
        return this.zzbfj;
    }

    @Override // com.google.android.gms.internal.zzdv
    public com.google.android.gms.dynamic.zzd zzkv() {
        return com.google.android.gms.dynamic.zze.zzac(this.zzbfp);
    }

    @Override // com.google.android.gms.ads.internal.formats.zzh.zza
    public String zzkw() {
        return MessageAPI.DELAYED_FROM;
    }

    @Override // com.google.android.gms.ads.internal.formats.zzh.zza
    public zza zzkx() {
        return this.zzbfo;
    }
}
