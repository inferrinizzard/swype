package com.google.android.gms.internal;

import com.google.android.gms.ads.purchase.InAppPurchaseListener;
import com.google.android.gms.internal.zzho;

@zzin
/* loaded from: classes.dex */
public final class zzht extends zzho.zza {
    private final InAppPurchaseListener zzawh;

    public zzht(InAppPurchaseListener inAppPurchaseListener) {
        this.zzawh = inAppPurchaseListener;
    }

    @Override // com.google.android.gms.internal.zzho
    public final void zza(zzhn zzhnVar) {
        this.zzawh.onInAppPurchaseRequested(new zzhw(zzhnVar));
    }
}
