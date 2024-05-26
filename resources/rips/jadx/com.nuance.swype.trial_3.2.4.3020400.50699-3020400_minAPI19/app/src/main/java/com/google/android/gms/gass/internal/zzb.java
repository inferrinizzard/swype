package com.google.android.gms.gass.internal;

import android.content.Context;
import android.os.DeadObjectException;
import android.os.IBinder;
import android.os.Looper;
import com.google.android.gms.common.internal.zzd;
import com.google.android.gms.gass.internal.zze;

/* loaded from: classes.dex */
public final class zzb extends com.google.android.gms.common.internal.zzd<zze> {
    public zzb(Context context, Looper looper, zzd.zzb zzbVar, zzd.zzc zzcVar) {
        super(context, looper, 116, zzbVar, zzcVar, null);
    }

    public final zze zzblb() throws DeadObjectException {
        return (zze) super.zzasa();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.gms.common.internal.zzd
    public final String zzqz() {
        return "com.google.android.gms.gass.START";
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.gms.common.internal.zzd
    public final String zzra() {
        return "com.google.android.gms.gass.internal.IGassService";
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.gms.common.internal.zzd
    public final /* synthetic */ zze zzbb(IBinder iBinder) {
        return zze.zza.zzgl(iBinder);
    }
}
