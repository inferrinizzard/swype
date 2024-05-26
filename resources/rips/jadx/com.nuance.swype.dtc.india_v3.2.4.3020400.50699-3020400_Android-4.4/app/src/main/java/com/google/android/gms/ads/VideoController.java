package com.google.android.gms.ads;

import android.os.RemoteException;
import com.google.android.gms.ads.internal.client.zzab;
import com.google.android.gms.ads.internal.client.zzap;
import com.google.android.gms.ads.internal.util.client.zzb;
import com.google.android.gms.internal.zzin;

@zzin
/* loaded from: classes.dex */
public final class VideoController {
    private final Object zzail = new Object();
    private zzab zzaim;
    private VideoLifecycleCallbacks zzain;

    /* loaded from: classes.dex */
    public static abstract class VideoLifecycleCallbacks {
        public void onVideoEnd() {
        }
    }

    public final VideoLifecycleCallbacks getVideoLifecycleCallbacks() {
        VideoLifecycleCallbacks videoLifecycleCallbacks;
        synchronized (this.zzail) {
            videoLifecycleCallbacks = this.zzain;
        }
        return videoLifecycleCallbacks;
    }

    public final boolean hasVideoContent() {
        boolean z;
        synchronized (this.zzail) {
            z = this.zzaim != null;
        }
        return z;
    }

    public final void setVideoLifecycleCallbacks(VideoLifecycleCallbacks videoLifecycleCallbacks) {
        com.google.android.gms.common.internal.zzab.zzb(videoLifecycleCallbacks, "VideoLifecycleCallbacks may not be null.");
        synchronized (this.zzail) {
            this.zzain = videoLifecycleCallbacks;
            if (this.zzaim == null) {
                return;
            }
            try {
                this.zzaim.zza(new zzap(videoLifecycleCallbacks));
            } catch (RemoteException e) {
                zzb.zzb("Unable to call setVideoLifecycleCallbacks on video controller.", e);
            }
        }
    }

    public final void zza(zzab zzabVar) {
        synchronized (this.zzail) {
            this.zzaim = zzabVar;
            if (this.zzain != null) {
                setVideoLifecycleCallbacks(this.zzain);
            }
        }
    }
}
