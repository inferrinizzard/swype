package com.google.android.gms.internal;

import com.google.android.gms.ads.doubleclick.OnCustomRenderedAdLoadedListener;
import com.google.android.gms.internal.zzdo;

@zzin
/* loaded from: classes.dex */
public final class zzdp extends zzdo.zza {
    private final OnCustomRenderedAdLoadedListener zzawi;

    public zzdp(OnCustomRenderedAdLoadedListener onCustomRenderedAdLoadedListener) {
        this.zzawi = onCustomRenderedAdLoadedListener;
    }

    @Override // com.google.android.gms.internal.zzdo
    public final void zza(zzdn zzdnVar) {
        this.zzawi.onCustomRenderedAdLoaded(new zzdm(zzdnVar));
    }
}
