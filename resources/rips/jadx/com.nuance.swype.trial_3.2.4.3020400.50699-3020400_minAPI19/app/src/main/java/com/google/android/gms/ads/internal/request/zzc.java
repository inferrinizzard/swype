package com.google.android.gms.ads.internal.request;

import android.content.Context;
import com.google.android.gms.ads.internal.request.zzd;
import com.google.android.gms.ads.internal.util.client.VersionInfoParcel;
import com.google.android.gms.ads.internal.zzu;
import com.google.android.gms.internal.zzdc;
import com.google.android.gms.internal.zzin;
import com.google.android.gms.internal.zzkd;
import com.google.android.gms.internal.zzkj;
import com.google.android.gms.internal.zzla;

@zzin
/* loaded from: classes.dex */
public final class zzc {

    /* loaded from: classes.dex */
    public interface zza {
        void zzb(AdResponseParcel adResponseParcel);
    }

    /* loaded from: classes.dex */
    interface zzb {
        boolean zza(VersionInfoParcel versionInfoParcel);
    }

    public static zzkj zza(final Context context, VersionInfoParcel versionInfoParcel, zzla<AdRequestInfoParcel> zzlaVar, zza zzaVar) {
        if (new zzb() { // from class: com.google.android.gms.ads.internal.request.zzc.1
            @Override // com.google.android.gms.ads.internal.request.zzc.zzb
            public final boolean zza(VersionInfoParcel versionInfoParcel2) {
                if (!versionInfoParcel2.zzcnm) {
                    if (com.google.android.gms.common.util.zzi.zzcl(context)) {
                        if (!((Boolean) zzu.zzfz().zzd(zzdc.zzayz)).booleanValue()) {
                        }
                    }
                    return false;
                }
                return true;
            }
        }.zza(versionInfoParcel)) {
            zzkd.zzcv("Fetching ad response from local ad request service.");
            zzd.zza zzaVar2 = new zzd.zza(context, zzlaVar, zzaVar);
            zzaVar2.zzpy();
            return zzaVar2;
        }
        zzkd.zzcv("Fetching ad response from remote ad request service.");
        if (com.google.android.gms.ads.internal.client.zzm.zziw().zzar(context)) {
            return new zzd.zzb(context, versionInfoParcel, zzlaVar, zzaVar);
        }
        zzkd.zzcx("Failed to connect to remote ad request service.");
        return null;
    }
}
