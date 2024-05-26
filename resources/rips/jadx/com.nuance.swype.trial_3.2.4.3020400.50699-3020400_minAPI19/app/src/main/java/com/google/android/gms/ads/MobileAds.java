package com.google.android.gms.ads;

import android.content.Context;
import com.google.android.gms.ads.internal.client.zzag;
import com.google.android.gms.ads.internal.client.zzah;
import com.google.android.gms.ads.reward.RewardedVideoAd;

/* loaded from: classes.dex */
public class MobileAds {

    /* loaded from: classes.dex */
    public static final class Settings {
        final zzah zzaik = new zzah();

        @Deprecated
        public final String getTrackingId() {
            return this.zzaik.getTrackingId();
        }

        @Deprecated
        public final boolean isGoogleAnalyticsEnabled() {
            return this.zzaik.isGoogleAnalyticsEnabled();
        }

        @Deprecated
        public final Settings setGoogleAnalyticsEnabled(boolean z) {
            this.zzaik.zzp(z);
            return this;
        }

        @Deprecated
        public final Settings setTrackingId(String str) {
            this.zzaik.zzao(str);
            return this;
        }
    }

    private MobileAds() {
    }

    public static RewardedVideoAd getRewardedVideoAdInstance(Context context) {
        return zzag.zzjo().getRewardedVideoAdInstance(context);
    }

    @Deprecated
    public static void initialize(Context context) {
        initialize(context, null, null);
    }

    public static void initialize(Context context, String str) {
        initialize(context, str, null);
    }

    public static void setAppMuted(boolean z) {
        zzag.zzjo().setAppMuted(z);
    }

    public static void setAppVolume(float f) {
        zzag.zzjo().setAppVolume(f);
    }

    @Deprecated
    public static void initialize(Context context, String str, Settings settings) {
        zzag.zzjo().zza(context, str, settings == null ? null : settings.zzaik);
    }
}
