package com.google.android.gms.internal;

import android.content.Intent;
import android.os.RemoteException;
import com.google.android.gms.ads.purchase.InAppPurchaseResult;

@zzin
/* loaded from: classes.dex */
public final class zzhv implements InAppPurchaseResult {
    private final zzhr zzbxp;

    public zzhv(zzhr zzhrVar) {
        this.zzbxp = zzhrVar;
    }

    @Override // com.google.android.gms.ads.purchase.InAppPurchaseResult
    public final void finishPurchase() {
        try {
            this.zzbxp.finishPurchase();
        } catch (RemoteException e) {
            com.google.android.gms.ads.internal.util.client.zzb.zzd("Could not forward finishPurchase to InAppPurchaseResult", e);
        }
    }

    @Override // com.google.android.gms.ads.purchase.InAppPurchaseResult
    public final String getProductId() {
        try {
            return this.zzbxp.getProductId();
        } catch (RemoteException e) {
            com.google.android.gms.ads.internal.util.client.zzb.zzd("Could not forward getProductId to InAppPurchaseResult", e);
            return null;
        }
    }

    @Override // com.google.android.gms.ads.purchase.InAppPurchaseResult
    public final Intent getPurchaseData() {
        try {
            return this.zzbxp.getPurchaseData();
        } catch (RemoteException e) {
            com.google.android.gms.ads.internal.util.client.zzb.zzd("Could not forward getPurchaseData to InAppPurchaseResult", e);
            return null;
        }
    }

    @Override // com.google.android.gms.ads.purchase.InAppPurchaseResult
    public final int getResultCode() {
        try {
            return this.zzbxp.getResultCode();
        } catch (RemoteException e) {
            com.google.android.gms.ads.internal.util.client.zzb.zzd("Could not forward getPurchaseData to InAppPurchaseResult", e);
            return 0;
        }
    }

    @Override // com.google.android.gms.ads.purchase.InAppPurchaseResult
    public final boolean isVerified() {
        try {
            return this.zzbxp.isVerified();
        } catch (RemoteException e) {
            com.google.android.gms.ads.internal.util.client.zzb.zzd("Could not forward isVerified to InAppPurchaseResult", e);
            return false;
        }
    }
}
