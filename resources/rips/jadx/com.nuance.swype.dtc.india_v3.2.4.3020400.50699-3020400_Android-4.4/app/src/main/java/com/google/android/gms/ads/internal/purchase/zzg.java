package com.google.android.gms.ads.internal.purchase;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import com.google.android.gms.ads.internal.zzu;
import com.google.android.gms.internal.zzhr;
import com.google.android.gms.internal.zzin;
import com.google.android.gms.internal.zzkd;

@zzin
/* loaded from: classes.dex */
public final class zzg extends zzhr.zza implements ServiceConnection {
    private Context mContext;
    private int mResultCode;
    zzb zzbws;
    private String zzbwy;
    private zzf zzbxc;
    private boolean zzbxi;
    private Intent zzbxj;

    public zzg(Context context, String str, boolean z, int i, Intent intent, zzf zzfVar) {
        this.zzbxi = false;
        this.zzbwy = str;
        this.mResultCode = i;
        this.zzbxj = intent;
        this.zzbxi = z;
        this.mContext = context;
        this.zzbxc = zzfVar;
    }

    @Override // com.google.android.gms.internal.zzhr
    public final void finishPurchase() {
        int zzd = zzu.zzga().zzd(this.zzbxj);
        if (this.mResultCode == -1 && zzd == 0) {
            this.zzbws = new zzb(this.mContext);
            Intent intent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
            intent.setPackage("com.android.vending");
            com.google.android.gms.common.stats.zzb.zzaux().zza$31a3108d(this.mContext, intent, this);
        }
    }

    @Override // com.google.android.gms.internal.zzhr
    public final String getProductId() {
        return this.zzbwy;
    }

    @Override // com.google.android.gms.internal.zzhr
    public final Intent getPurchaseData() {
        return this.zzbxj;
    }

    @Override // com.google.android.gms.internal.zzhr
    public final int getResultCode() {
        return this.mResultCode;
    }

    @Override // com.google.android.gms.internal.zzhr
    public final boolean isVerified() {
        return this.zzbxi;
    }

    @Override // android.content.ServiceConnection
    public final void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        zzkd.zzcw("In-app billing service connected.");
        this.zzbws.zzas(iBinder);
        String zzbz = zzu.zzga().zzbz(zzu.zzga().zze(this.zzbxj));
        if (zzbz == null) {
            return;
        }
        if (this.zzbws.zzm(this.mContext.getPackageName(), zzbz) == 0) {
            zzh.zzs(this.mContext).zza(this.zzbxc);
        }
        com.google.android.gms.common.stats.zzb.zzaux();
        com.google.android.gms.common.stats.zzb.zza(this.mContext, this);
        this.zzbws.destroy();
    }

    @Override // android.content.ServiceConnection
    public final void onServiceDisconnected(ComponentName componentName) {
        zzkd.zzcw("In-app billing service disconnected.");
        this.zzbws.destroy();
    }
}
