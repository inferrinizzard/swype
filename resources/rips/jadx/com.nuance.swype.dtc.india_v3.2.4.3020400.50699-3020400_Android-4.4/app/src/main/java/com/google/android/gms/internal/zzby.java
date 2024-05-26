package com.google.android.gms.internal;

import android.content.Context;
import android.net.Uri;
import android.view.MotionEvent;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.internal.zzca;

/* loaded from: classes.dex */
public final class zzby extends zzca.zza {
    private final zzar zzaiq;
    private final zzas zzair;
    private final zzap zzais;
    private boolean zzait = false;

    public zzby(String str, Context context, boolean z) {
        this.zzaiq = zzar.zza(str, context, false);
        this.zzair = new zzas(this.zzaiq);
        this.zzais = zzap.zze(context);
    }

    @Override // com.google.android.gms.internal.zzca
    public final com.google.android.gms.dynamic.zzd zza(com.google.android.gms.dynamic.zzd zzdVar, com.google.android.gms.dynamic.zzd zzdVar2) {
        return zza(zzdVar, zzdVar2, true);
    }

    @Override // com.google.android.gms.internal.zzca
    public final boolean zza(com.google.android.gms.dynamic.zzd zzdVar) {
        return this.zzair.zza((Uri) com.google.android.gms.dynamic.zze.zzad(zzdVar));
    }

    @Override // com.google.android.gms.internal.zzca
    public final com.google.android.gms.dynamic.zzd zzb(com.google.android.gms.dynamic.zzd zzdVar, com.google.android.gms.dynamic.zzd zzdVar2) {
        return zza(zzdVar, zzdVar2, false);
    }

    @Override // com.google.android.gms.internal.zzca
    public final boolean zzb(com.google.android.gms.dynamic.zzd zzdVar) {
        return this.zzair.zzc((Uri) com.google.android.gms.dynamic.zze.zzad(zzdVar));
    }

    @Override // com.google.android.gms.internal.zzca
    public final void zzd(com.google.android.gms.dynamic.zzd zzdVar) {
        this.zzair.zza((MotionEvent) com.google.android.gms.dynamic.zze.zzad(zzdVar));
    }

    @Override // com.google.android.gms.internal.zzca
    public final String zzdf() {
        return "ms";
    }

    @Override // com.google.android.gms.internal.zzca
    public final void zzb(String str, String str2) {
        zzas zzasVar = this.zzair;
        zzasVar.zzafu = str;
        zzasVar.zzafv = str2;
    }

    @Override // com.google.android.gms.internal.zzca
    public final void zzk(String str) {
        this.zzair.zzafx = str.split(",");
    }

    @Override // com.google.android.gms.internal.zzca
    public final String zzc(com.google.android.gms.dynamic.zzd zzdVar) {
        Context context = (Context) com.google.android.gms.dynamic.zze.zzad(zzdVar);
        String zza = this.zzaiq.zza(context, (String) null, false);
        if (this.zzais == null || !this.zzait) {
            return zza;
        }
        String zza2 = zzap.zza(zza, this.zzais.zza(context, (String) null, false));
        this.zzait = false;
        return zza2;
    }

    @Override // com.google.android.gms.internal.zzca
    public final String zza(com.google.android.gms.dynamic.zzd zzdVar, String str) {
        return this.zzaiq.zza((Context) com.google.android.gms.dynamic.zze.zzad(zzdVar), str, true);
    }

    @Override // com.google.android.gms.internal.zzca
    public final boolean zzb(String str, boolean z) {
        if (this.zzais == null) {
            return false;
        }
        this.zzais.zzafm = new AdvertisingIdClient.Info(str, z);
        this.zzait = true;
        return true;
    }

    private com.google.android.gms.dynamic.zzd zza(com.google.android.gms.dynamic.zzd zzdVar, com.google.android.gms.dynamic.zzd zzdVar2, boolean z) {
        try {
            Uri uri = (Uri) com.google.android.gms.dynamic.zze.zzad(zzdVar);
            Context context = (Context) com.google.android.gms.dynamic.zze.zzad(zzdVar2);
            return com.google.android.gms.dynamic.zze.zzac(z ? this.zzair.zza(uri, context, null, false) : this.zzair.zzb(uri, context));
        } catch (zzat e) {
            return null;
        }
    }
}
