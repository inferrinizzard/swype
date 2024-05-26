package com.google.android.gms.ads.internal.client;

import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;
import com.google.android.gms.ads.internal.client.zzu;
import com.google.android.gms.ads.internal.client.zzv;
import com.google.android.gms.dynamic.zzg;
import com.google.android.gms.internal.zzgj;
import com.google.android.gms.internal.zzin;

@zzin
/* loaded from: classes.dex */
public class zze extends com.google.android.gms.dynamic.zzg<zzv> {
    public zze() {
        super("com.google.android.gms.ads.AdManagerCreatorImpl");
    }

    public zzu zza(Context context, AdSizeParcel adSizeParcel, String str, zzgj zzgjVar, int i) {
        try {
            return zzu.zza.zzn(zzcr(context).zza(com.google.android.gms.dynamic.zze.zzac(context), adSizeParcel, str, zzgjVar, com.google.android.gms.common.internal.zze.xM, i));
        } catch (RemoteException | zzg.zza e) {
            com.google.android.gms.ads.internal.util.client.zzb.zza("Could not create remote AdManager.", e);
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.gms.dynamic.zzg
    public final /* synthetic */ zzv zzc(IBinder iBinder) {
        return zzv.zza.zzo(iBinder);
    }
}
