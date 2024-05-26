package com.google.android.gms.internal;

import android.app.Activity;
import android.os.IBinder;
import android.os.RemoteException;
import com.google.android.gms.dynamic.zzg;
import com.google.android.gms.internal.zzhp;
import com.google.android.gms.internal.zzhq;

@zzin
/* loaded from: classes.dex */
public final class zzhu extends com.google.android.gms.dynamic.zzg<zzhq> {
    public zzhu() {
        super("com.google.android.gms.ads.InAppPurchaseManagerCreatorImpl");
    }

    public final zzhp zzg(Activity activity) {
        try {
            return zzhp.zza.zzav(zzcr(activity).zzo(com.google.android.gms.dynamic.zze.zzac(activity)));
        } catch (RemoteException e) {
            com.google.android.gms.ads.internal.util.client.zzb.zzd("Could not create remote InAppPurchaseManager.", e);
            return null;
        } catch (zzg.zza e2) {
            com.google.android.gms.ads.internal.util.client.zzb.zzd("Could not create remote InAppPurchaseManager.", e2);
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.gms.dynamic.zzg
    public final /* synthetic */ zzhq zzc(IBinder iBinder) {
        return zzhq.zza.zzaw(iBinder);
    }
}
