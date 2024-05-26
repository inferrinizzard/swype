package com.google.android.gms.internal;

import com.google.android.gms.ads.formats.NativeContentAd;
import com.google.android.gms.internal.zzec;

@zzin
/* loaded from: classes.dex */
public final class zzeh extends zzec.zza {
    private final NativeContentAd.OnContentAdLoadedListener zzbhj;

    public zzeh(NativeContentAd.OnContentAdLoadedListener onContentAdLoadedListener) {
        this.zzbhj = onContentAdLoadedListener;
    }

    @Override // com.google.android.gms.internal.zzec
    public final void zza(zzdx zzdxVar) {
        this.zzbhj.onContentAdLoaded(new zzdy(zzdxVar));
    }
}
