package com.google.android.gms.ads.internal.reward.client;

import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;
import com.google.android.gms.ads.internal.reward.client.zzb;
import com.google.android.gms.ads.internal.reward.client.zzc;
import com.google.android.gms.dynamic.zzg;
import com.google.android.gms.internal.zzgj;
import com.google.android.gms.internal.zzin;

@zzin
/* loaded from: classes.dex */
public class zzf extends com.google.android.gms.dynamic.zzg<zzc> {
    public zzf() {
        super("com.google.android.gms.ads.reward.RewardedVideoAdCreatorImpl");
    }

    public zzb zzb(Context context, zzgj zzgjVar) {
        try {
            return zzb.zza.zzbf(zzcr(context).zza(com.google.android.gms.dynamic.zze.zzac(context), zzgjVar, com.google.android.gms.common.internal.zze.xM));
        } catch (RemoteException | zzg.zza e) {
            com.google.android.gms.ads.internal.util.client.zzb.zzd("Could not get remote RewardedVideoAd.", e);
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.gms.dynamic.zzg
    public final /* synthetic */ zzc zzc(IBinder iBinder) {
        return zzc.zza.zzbg(iBinder);
    }
}
