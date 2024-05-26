package com.google.android.gms.ads.internal.client;

import android.content.Context;
import android.os.RemoteException;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.internal.zzgi;
import com.google.android.gms.internal.zzin;

@zzin
/* loaded from: classes.dex */
public class zzag {
    private static final Object zzamr = new Object();
    private static zzag zzawr;
    private zzz zzaws;
    private RewardedVideoAd zzawt;

    private zzag() {
    }

    public static zzag zzjo() {
        zzag zzagVar;
        synchronized (zzamr) {
            if (zzawr == null) {
                zzawr = new zzag();
            }
            zzagVar = zzawr;
        }
        return zzagVar;
    }

    public RewardedVideoAd getRewardedVideoAdInstance(Context context) {
        RewardedVideoAd rewardedVideoAd;
        synchronized (zzamr) {
            if (this.zzawt != null) {
                rewardedVideoAd = this.zzawt;
            } else {
                this.zzawt = new com.google.android.gms.ads.internal.reward.client.zzi(context, zzm.zzix().zza(context, new zzgi()));
                rewardedVideoAd = this.zzawt;
            }
        }
        return rewardedVideoAd;
    }

    public void setAppMuted(boolean z) {
        com.google.android.gms.common.internal.zzab.zza(this.zzaws != null, "MobileAds.initialize() must be called prior to setting the app volume.");
        try {
            this.zzaws.setAppMuted(z);
        } catch (RemoteException e) {
            com.google.android.gms.ads.internal.util.client.zzb.zzb("Unable to set app mute state.", e);
        }
    }

    public void setAppVolume(float f) {
        com.google.android.gms.common.internal.zzab.zzb(0.0f <= f && f <= 1.0f, "The app volume must be a value between 0 and 1 inclusive.");
        com.google.android.gms.common.internal.zzab.zza(this.zzaws != null, "MobileAds.initialize() must be called prior to setting the app volume.");
        try {
            this.zzaws.setAppVolume(f);
        } catch (RemoteException e) {
            com.google.android.gms.ads.internal.util.client.zzb.zzb("Unable to set app volume.", e);
        }
    }

    public void zza(Context context, String str, zzah zzahVar) {
        synchronized (zzamr) {
            if (this.zzaws != null) {
                return;
            }
            if (context == null) {
                throw new IllegalArgumentException("Context cannot be null.");
            }
            try {
                this.zzaws = zzm.zzix().zzl(context);
                this.zzaws.initialize();
                if (str != null) {
                    this.zzaws.zzu(str);
                }
            } catch (RemoteException e) {
                com.google.android.gms.ads.internal.util.client.zzb.zzd("Fail to initialize or set applicationCode on mobile ads setting manager", e);
            }
        }
    }
}
