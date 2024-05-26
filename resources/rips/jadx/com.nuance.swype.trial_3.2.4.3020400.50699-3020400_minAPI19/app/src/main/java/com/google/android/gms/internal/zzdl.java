package com.google.android.gms.internal;

import android.view.View;
import com.google.android.gms.internal.zzdn;

@zzin
/* loaded from: classes.dex */
public final class zzdl extends zzdn.zza {
    private final com.google.android.gms.ads.internal.zzh zzbek;
    private final String zzbel;
    private final String zzbem;

    public zzdl(com.google.android.gms.ads.internal.zzh zzhVar, String str, String str2) {
        this.zzbek = zzhVar;
        this.zzbel = str;
        this.zzbem = str2;
    }

    @Override // com.google.android.gms.internal.zzdn
    public final String getContent() {
        return this.zzbem;
    }

    @Override // com.google.android.gms.internal.zzdn
    public final void recordClick() {
        this.zzbek.zzeh();
    }

    @Override // com.google.android.gms.internal.zzdn
    public final void recordImpression() {
        this.zzbek.zzei();
    }

    @Override // com.google.android.gms.internal.zzdn
    public final void zzi(com.google.android.gms.dynamic.zzd zzdVar) {
        if (zzdVar == null) {
            return;
        }
        this.zzbek.zzc((View) com.google.android.gms.dynamic.zze.zzad(zzdVar));
    }

    @Override // com.google.android.gms.internal.zzdn
    public final String zzkk() {
        return this.zzbel;
    }
}
