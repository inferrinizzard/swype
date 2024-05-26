package com.google.android.gms.internal;

import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.google.android.gms.internal.zzeb;

@zzin
/* loaded from: classes.dex */
public final class zzeg extends zzeb.zza {
    private final NativeAppInstallAd.OnAppInstallAdLoadedListener zzbhi;

    public zzeg(NativeAppInstallAd.OnAppInstallAdLoadedListener onAppInstallAdLoadedListener) {
        this.zzbhi = onAppInstallAdLoadedListener;
    }

    @Override // com.google.android.gms.internal.zzeb
    public final void zza(zzdv zzdvVar) {
        this.zzbhi.onAppInstallAdLoaded(new zzdw(zzdvVar));
    }
}
