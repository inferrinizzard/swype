package com.google.android.gms.internal;

import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;
import com.google.android.gms.dynamic.zzg;
import com.google.android.gms.internal.zzca;
import com.google.android.gms.internal.zzcb;

/* loaded from: classes.dex */
public final class zzbz extends com.google.android.gms.dynamic.zzg<zzcb> {
    private static final zzbz zzaiu = new zzbz();

    private zzbz() {
        super("com.google.android.gms.ads.adshield.AdShieldCreatorImpl");
    }

    public static zzca zzb$10764fec(String str, Context context) {
        zzca zzc;
        return (com.google.android.gms.common.zzc.zzang().isGooglePlayServicesAvailable(context) != 0 || (zzc = zzaiu.zzc(str, context, false)) == null) ? new zzby(str, context, false) : zzc;
    }

    private zzca zzc(String str, Context context, boolean z) {
        try {
            return zzca.zza.zzd(zzcr(context).zzb(str, com.google.android.gms.dynamic.zze.zzac(context)));
        } catch (RemoteException | zzg.zza e) {
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.gms.dynamic.zzg
    public final /* synthetic */ zzcb zzc(IBinder iBinder) {
        return zzcb.zza.zze(iBinder);
    }
}
