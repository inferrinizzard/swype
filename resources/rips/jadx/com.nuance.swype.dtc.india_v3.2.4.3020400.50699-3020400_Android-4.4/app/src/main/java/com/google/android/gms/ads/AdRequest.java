package com.google.android.gms.ads;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.internal.client.zzad;
import com.google.android.gms.ads.mediation.MediationAdapter;
import com.google.android.gms.ads.mediation.NetworkExtras;
import com.google.android.gms.ads.mediation.customevent.CustomEvent;
import com.google.android.gms.common.internal.zzab;
import java.util.Date;
import java.util.Set;

/* loaded from: classes.dex */
public final class AdRequest {
    public static final String DEVICE_ID_EMULATOR = zzad.DEVICE_ID_EMULATOR;
    public static final int ERROR_CODE_INTERNAL_ERROR = 0;
    public static final int ERROR_CODE_INVALID_REQUEST = 1;
    public static final int ERROR_CODE_NETWORK_ERROR = 2;
    public static final int ERROR_CODE_NO_FILL = 3;
    public static final int GENDER_FEMALE = 2;
    public static final int GENDER_MALE = 1;
    public static final int GENDER_UNKNOWN = 0;
    public static final int MAX_CONTENT_URL_LENGTH = 512;
    private final zzad zzaic;

    /* loaded from: classes.dex */
    public static final class Builder {
        private final zzad.zza zzaid = new zzad.zza();

        public Builder() {
            this.zzaid.zzag(AdRequest.DEVICE_ID_EMULATOR);
        }

        public final Builder addCustomEventExtrasBundle(Class<? extends CustomEvent> cls, Bundle bundle) {
            this.zzaid.zzb(cls, bundle);
            return this;
        }

        public final Builder addKeyword(String str) {
            this.zzaid.zzaf(str);
            return this;
        }

        public final Builder addNetworkExtras(NetworkExtras networkExtras) {
            this.zzaid.zza(networkExtras);
            return this;
        }

        public final Builder addNetworkExtrasBundle(Class<? extends MediationAdapter> cls, Bundle bundle) {
            this.zzaid.zza(cls, bundle);
            if (cls.equals(AdMobAdapter.class) && bundle.getBoolean("_emulatorLiveAds")) {
                this.zzaid.zzah(AdRequest.DEVICE_ID_EMULATOR);
            }
            return this;
        }

        public final Builder addTestDevice(String str) {
            this.zzaid.zzag(str);
            return this;
        }

        public final AdRequest build() {
            return new AdRequest(this, (byte) 0);
        }

        public final Builder setBirthday(Date date) {
            this.zzaid.zza(date);
            return this;
        }

        public final Builder setContentUrl(String str) {
            zzab.zzb(str, "Content URL must be non-null.");
            zzab.zzh(str, "Content URL must be non-empty.");
            zzab.zzb(str.length() <= 512, "Content URL must not exceed %d in length.  Provided length was %d.", 512, Integer.valueOf(str.length()));
            this.zzaid.zzai(str);
            return this;
        }

        public final Builder setGender(int i) {
            this.zzaid.zzt(i);
            return this;
        }

        public final Builder setIsDesignedForFamilies(boolean z) {
            this.zzaid.zzo(z);
            return this;
        }

        public final Builder setLocation(Location location) {
            this.zzaid.zzb(location);
            return this;
        }

        public final Builder setRequestAgent(String str) {
            this.zzaid.zzak(str);
            return this;
        }

        public final Builder tagForChildDirectedTreatment(boolean z) {
            this.zzaid.zzn(z);
            return this;
        }
    }

    private AdRequest(Builder builder) {
        this.zzaic = new zzad(builder.zzaid);
    }

    /* synthetic */ AdRequest(Builder builder, byte b) {
        this(builder);
    }

    public final Date getBirthday() {
        return this.zzaic.getBirthday();
    }

    public final String getContentUrl() {
        return this.zzaic.getContentUrl();
    }

    public final <T extends CustomEvent> Bundle getCustomEventExtrasBundle(Class<T> cls) {
        return this.zzaic.getCustomEventExtrasBundle(cls);
    }

    public final int getGender() {
        return this.zzaic.getGender();
    }

    public final Set<String> getKeywords() {
        return this.zzaic.getKeywords();
    }

    public final Location getLocation() {
        return this.zzaic.getLocation();
    }

    @Deprecated
    public final <T extends NetworkExtras> T getNetworkExtras(Class<T> cls) {
        return (T) this.zzaic.getNetworkExtras(cls);
    }

    public final <T extends MediationAdapter> Bundle getNetworkExtrasBundle(Class<T> cls) {
        return this.zzaic.getNetworkExtrasBundle(cls);
    }

    public final boolean isTestDevice(Context context) {
        return this.zzaic.isTestDevice(context);
    }

    public final zzad zzdc() {
        return this.zzaic;
    }
}
