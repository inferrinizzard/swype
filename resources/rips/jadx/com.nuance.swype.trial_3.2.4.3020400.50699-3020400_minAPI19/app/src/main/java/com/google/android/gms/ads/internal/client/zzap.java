package com.google.android.gms.ads.internal.client;

import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.internal.client.zzac;

/* loaded from: classes.dex */
public final class zzap extends zzac.zza {
    private final VideoController.VideoLifecycleCallbacks zzain;

    public zzap(VideoController.VideoLifecycleCallbacks videoLifecycleCallbacks) {
        this.zzain = videoLifecycleCallbacks;
    }

    @Override // com.google.android.gms.ads.internal.client.zzac
    public final void onVideoEnd() {
        this.zzain.onVideoEnd();
    }

    @Override // com.google.android.gms.ads.internal.client.zzac
    public final void zzjb() {
    }

    @Override // com.google.android.gms.ads.internal.client.zzac
    public final void zzjc() {
    }

    @Override // com.google.android.gms.ads.internal.client.zzac
    public final void zzjd() {
    }
}
