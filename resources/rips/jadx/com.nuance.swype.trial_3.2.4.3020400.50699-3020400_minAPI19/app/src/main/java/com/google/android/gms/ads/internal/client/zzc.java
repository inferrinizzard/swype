package com.google.android.gms.ads.internal.client;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.internal.client.zzq;
import com.google.android.gms.internal.zzin;

@zzin
/* loaded from: classes.dex */
public final class zzc extends zzq.zza {
    private final AdListener zzatl;

    public zzc(AdListener adListener) {
        this.zzatl = adListener;
    }

    @Override // com.google.android.gms.ads.internal.client.zzq
    public final void onAdClosed() {
        this.zzatl.onAdClosed();
    }

    @Override // com.google.android.gms.ads.internal.client.zzq
    public final void onAdFailedToLoad(int i) {
        this.zzatl.onAdFailedToLoad(i);
    }

    @Override // com.google.android.gms.ads.internal.client.zzq
    public final void onAdLeftApplication() {
        this.zzatl.onAdLeftApplication();
    }

    @Override // com.google.android.gms.ads.internal.client.zzq
    public final void onAdLoaded() {
        this.zzatl.onAdLoaded();
    }

    @Override // com.google.android.gms.ads.internal.client.zzq
    public final void onAdOpened() {
        this.zzatl.onAdOpened();
    }
}
