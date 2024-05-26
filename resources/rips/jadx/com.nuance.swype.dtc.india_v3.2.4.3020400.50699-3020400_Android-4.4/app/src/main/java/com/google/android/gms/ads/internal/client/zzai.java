package com.google.android.gms.ads.internal.client;

import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;
import com.google.android.gms.ads.internal.client.zzaa;
import com.google.android.gms.ads.internal.client.zzz;
import com.google.android.gms.dynamic.zzg;
import com.google.android.gms.internal.zzin;

@zzin
/* loaded from: classes.dex */
public class zzai extends com.google.android.gms.dynamic.zzg<zzaa> {
    public zzai() {
        super("com.google.android.gms.ads.MobileAdsSettingManagerCreatorImpl");
    }

    public zzz zzm(Context context) {
        try {
            return zzz.zza.zzr(zzcr(context).zza(com.google.android.gms.dynamic.zze.zzac(context), com.google.android.gms.common.internal.zze.xM));
        } catch (RemoteException e) {
            com.google.android.gms.ads.internal.util.client.zzb.zzd("Could not get remote MobileAdsSettingManager.", e);
            return null;
        } catch (zzg.zza e2) {
            com.google.android.gms.ads.internal.util.client.zzb.zzd("Could not get remote MobileAdsSettingManager.", e2);
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.gms.dynamic.zzg
    public final /* synthetic */ zzaa zzc(IBinder iBinder) {
        return zzaa.zza.zzs(iBinder);
    }
}
