package com.google.android.gms.internal;

import android.os.RemoteException;
import com.google.android.gms.ads.purchase.InAppPurchase;

@zzin
/* loaded from: classes.dex */
public final class zzhw implements InAppPurchase {
    private final zzhn zzbxb;

    public zzhw(zzhn zzhnVar) {
        this.zzbxb = zzhnVar;
    }

    @Override // com.google.android.gms.ads.purchase.InAppPurchase
    public final String getProductId() {
        try {
            return this.zzbxb.getProductId();
        } catch (RemoteException e) {
            com.google.android.gms.ads.internal.util.client.zzb.zzd("Could not forward getProductId to InAppPurchase", e);
            return null;
        }
    }

    @Override // com.google.android.gms.ads.purchase.InAppPurchase
    public final void recordPlayBillingResolution(int i) {
        try {
            this.zzbxb.recordPlayBillingResolution(i);
        } catch (RemoteException e) {
            com.google.android.gms.ads.internal.util.client.zzb.zzd("Could not forward recordPlayBillingResolution to InAppPurchase", e);
        }
    }

    @Override // com.google.android.gms.ads.purchase.InAppPurchase
    public final void recordResolution(int i) {
        try {
            this.zzbxb.recordResolution(i);
        } catch (RemoteException e) {
            com.google.android.gms.ads.internal.util.client.zzb.zzd("Could not forward recordResolution to InAppPurchase", e);
        }
    }
}
