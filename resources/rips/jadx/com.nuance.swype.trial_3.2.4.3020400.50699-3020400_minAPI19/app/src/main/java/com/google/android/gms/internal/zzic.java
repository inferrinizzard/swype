package com.google.android.gms.internal;

import android.content.Context;
import com.google.android.gms.ads.internal.request.AdResponseParcel;
import com.google.android.gms.internal.zzju;

@zzin
/* loaded from: classes.dex */
public final class zzic {

    /* loaded from: classes.dex */
    public interface zza {
        void zzb(zzju zzjuVar);
    }

    public static zzkj zza(Context context, com.google.android.gms.ads.internal.zza zzaVar, zzju.zza zzaVar2, zzas zzasVar, zzlh zzlhVar, zzgj zzgjVar, zza zzaVar3, zzdk zzdkVar) {
        zzkj zzieVar;
        AdResponseParcel adResponseParcel = zzaVar2.zzciq;
        if (adResponseParcel.zzcby) {
            zzieVar = new zzif(context, zzaVar2, zzgjVar, zzaVar3, zzdkVar, zzlhVar);
        } else if (adResponseParcel.zzauu) {
            if (!(zzaVar instanceof com.google.android.gms.ads.internal.zzq)) {
                String valueOf = String.valueOf(zzaVar != null ? zzaVar.getClass().getName() : "null");
                throw new IllegalArgumentException(new StringBuilder(String.valueOf(valueOf).length() + 65).append("Invalid NativeAdManager type. Found: ").append(valueOf).append("; Required: NativeAdManager.").toString());
            }
            zzieVar = new zzig(context, (com.google.android.gms.ads.internal.zzq) zzaVar, zzaVar2, zzasVar, zzaVar3);
        } else if (adResponseParcel.zzcce) {
            zzieVar = new zzia(context, zzaVar2, zzlhVar, zzaVar3);
        } else {
            zzieVar = (((Boolean) com.google.android.gms.ads.internal.zzu.zzfz().zzd(zzdc.zzazs)).booleanValue() && com.google.android.gms.common.util.zzs.zzhb(19) && !com.google.android.gms.common.util.zzs.zzhb(21) && zzlhVar != null && zzlhVar.zzdn().zzaus) ? new zzie(context, zzaVar2, zzlhVar, zzaVar3) : new zzid(context, zzaVar2, zzlhVar, zzaVar3);
        }
        String valueOf2 = String.valueOf(zzieVar.getClass().getName());
        zzkd.zzcv(valueOf2.length() != 0 ? "AdRenderer: ".concat(valueOf2) : new String("AdRenderer: "));
        zzieVar.zzpy();
        return zzieVar;
    }
}
