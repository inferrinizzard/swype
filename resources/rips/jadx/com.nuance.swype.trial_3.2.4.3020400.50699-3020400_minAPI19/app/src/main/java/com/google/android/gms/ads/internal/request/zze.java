package com.google.android.gms.ads.internal.request;

import android.content.Context;
import android.os.DeadObjectException;
import android.os.IBinder;
import android.os.Looper;
import com.google.android.gms.ads.internal.request.zzk;
import com.google.android.gms.common.internal.zzd;
import com.google.android.gms.internal.zzin;

@zzin
/* loaded from: classes.dex */
public class zze extends com.google.android.gms.common.internal.zzd<zzk> {
    final int zzcap;

    public zze(Context context, Looper looper, zzd.zzb zzbVar, zzd.zzc zzcVar, int i) {
        super(context, looper, 8, zzbVar, zzcVar, null);
        this.zzcap = i;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.gms.common.internal.zzd
    public final String zzqz() {
        return "com.google.android.gms.ads.service.START";
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.gms.common.internal.zzd
    public final String zzra() {
        return "com.google.android.gms.ads.internal.request.IAdRequestService";
    }

    public zzk zzrb() throws DeadObjectException {
        return (zzk) super.zzasa();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.gms.common.internal.zzd
    public final /* synthetic */ zzk zzbb(IBinder iBinder) {
        return zzk.zza.zzbc(iBinder);
    }
}
