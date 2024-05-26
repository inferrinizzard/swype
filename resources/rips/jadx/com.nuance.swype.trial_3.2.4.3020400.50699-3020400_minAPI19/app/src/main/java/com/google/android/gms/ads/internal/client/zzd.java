package com.google.android.gms.ads.internal.client;

import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;
import com.google.android.gms.ads.internal.client.zzs;
import com.google.android.gms.ads.internal.client.zzt;
import com.google.android.gms.dynamic.zzg;
import com.google.android.gms.internal.zzgj;
import com.google.android.gms.internal.zzin;

@zzin
/* loaded from: classes.dex */
public final class zzd extends com.google.android.gms.dynamic.zzg<zzt> {
    public zzd() {
        super("com.google.android.gms.ads.AdLoaderBuilderCreatorImpl");
    }

    public final zzs zza(Context context, String str, zzgj zzgjVar) {
        try {
            return zzs.zza.zzl(zzcr(context).zza(com.google.android.gms.dynamic.zze.zzac(context), str, zzgjVar, com.google.android.gms.common.internal.zze.xM));
        } catch (RemoteException e) {
            com.google.android.gms.ads.internal.util.client.zzb.zzd("Could not create remote builder for AdLoader.", e);
            return null;
        } catch (zzg.zza e2) {
            com.google.android.gms.ads.internal.util.client.zzb.zzd("Could not create remote builder for AdLoader.", e2);
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.gms.dynamic.zzg
    public final /* synthetic */ zzt zzc(IBinder iBinder) {
        return zzt.zza.zzm(iBinder);
    }
}
