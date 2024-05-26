package com.google.android.gms.ads.search;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import com.google.android.gms.ads.internal.client.zzad;
import com.google.android.gms.ads.mediation.MediationAdapter;
import com.google.android.gms.ads.mediation.NetworkExtras;
import com.google.android.gms.ads.mediation.customevent.CustomEvent;

/* loaded from: classes.dex */
public final class SearchAdRequest {
    public static final int BORDER_TYPE_DASHED = 1;
    public static final int BORDER_TYPE_DOTTED = 2;
    public static final int BORDER_TYPE_NONE = 0;
    public static final int BORDER_TYPE_SOLID = 3;
    public static final int CALL_BUTTON_COLOR_DARK = 2;
    public static final int CALL_BUTTON_COLOR_LIGHT = 0;
    public static final int CALL_BUTTON_COLOR_MEDIUM = 1;
    public static final String DEVICE_ID_EMULATOR = zzad.DEVICE_ID_EMULATOR;
    public static final int ERROR_CODE_INTERNAL_ERROR = 0;
    public static final int ERROR_CODE_INVALID_REQUEST = 1;
    public static final int ERROR_CODE_NETWORK_ERROR = 2;
    public static final int ERROR_CODE_NO_FILL = 3;
    private final int mBackgroundColor;
    final zzad zzaic;
    private final String zzanr;
    private final int zzcrb;
    private final int zzcrc;
    private final int zzcrd;
    private final int zzcre;
    private final int zzcrf;
    private final int zzcrg;
    private final int zzcrh;
    private final String zzcri;
    private final int zzcrj;
    private final String zzcrk;
    private final int zzcrl;
    private final int zzcrm;

    /* loaded from: classes.dex */
    public static final class Builder {
        private int mBackgroundColor;
        private String zzanr;
        private int zzcrb;
        private int zzcrc;
        private int zzcrd;
        private int zzcre;
        private int zzcrf;
        private int zzcrh;
        private String zzcri;
        private int zzcrj;
        private String zzcrk;
        private int zzcrl;
        private int zzcrm;
        private final zzad.zza zzaid = new zzad.zza();
        private int zzcrg = 0;

        public final Builder addCustomEventExtrasBundle(Class<? extends CustomEvent> cls, Bundle bundle) {
            this.zzaid.zzb(cls, bundle);
            return this;
        }

        public final Builder addNetworkExtras(NetworkExtras networkExtras) {
            this.zzaid.zza(networkExtras);
            return this;
        }

        public final Builder addNetworkExtrasBundle(Class<? extends MediationAdapter> cls, Bundle bundle) {
            this.zzaid.zza(cls, bundle);
            return this;
        }

        public final Builder addTestDevice(String str) {
            this.zzaid.zzag(str);
            return this;
        }

        public final SearchAdRequest build() {
            return new SearchAdRequest(this, (byte) 0);
        }

        public final Builder setAnchorTextColor(int i) {
            this.zzcrb = i;
            return this;
        }

        public final Builder setBackgroundColor(int i) {
            this.mBackgroundColor = i;
            this.zzcrc = Color.argb(0, 0, 0, 0);
            this.zzcrd = Color.argb(0, 0, 0, 0);
            return this;
        }

        public final Builder setBackgroundGradient(int i, int i2) {
            this.mBackgroundColor = Color.argb(0, 0, 0, 0);
            this.zzcrc = i2;
            this.zzcrd = i;
            return this;
        }

        public final Builder setBorderColor(int i) {
            this.zzcre = i;
            return this;
        }

        public final Builder setBorderThickness(int i) {
            this.zzcrf = i;
            return this;
        }

        public final Builder setBorderType(int i) {
            this.zzcrg = i;
            return this;
        }

        public final Builder setCallButtonColor(int i) {
            this.zzcrh = i;
            return this;
        }

        public final Builder setCustomChannels(String str) {
            this.zzcri = str;
            return this;
        }

        public final Builder setDescriptionTextColor(int i) {
            this.zzcrj = i;
            return this;
        }

        public final Builder setFontFace(String str) {
            this.zzcrk = str;
            return this;
        }

        public final Builder setHeaderTextColor(int i) {
            this.zzcrl = i;
            return this;
        }

        public final Builder setHeaderTextSize(int i) {
            this.zzcrm = i;
            return this;
        }

        public final Builder setLocation(Location location) {
            this.zzaid.zzb(location);
            return this;
        }

        public final Builder setQuery(String str) {
            this.zzanr = str;
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

    private SearchAdRequest(Builder builder) {
        this.zzcrb = builder.zzcrb;
        this.mBackgroundColor = builder.mBackgroundColor;
        this.zzcrc = builder.zzcrc;
        this.zzcrd = builder.zzcrd;
        this.zzcre = builder.zzcre;
        this.zzcrf = builder.zzcrf;
        this.zzcrg = builder.zzcrg;
        this.zzcrh = builder.zzcrh;
        this.zzcri = builder.zzcri;
        this.zzcrj = builder.zzcrj;
        this.zzcrk = builder.zzcrk;
        this.zzcrl = builder.zzcrl;
        this.zzcrm = builder.zzcrm;
        this.zzanr = builder.zzanr;
        this.zzaic = new zzad(builder.zzaid, this);
    }

    /* synthetic */ SearchAdRequest(Builder builder, byte b) {
        this(builder);
    }

    public final int getAnchorTextColor() {
        return this.zzcrb;
    }

    public final int getBackgroundColor() {
        return this.mBackgroundColor;
    }

    public final int getBackgroundGradientBottom() {
        return this.zzcrc;
    }

    public final int getBackgroundGradientTop() {
        return this.zzcrd;
    }

    public final int getBorderColor() {
        return this.zzcre;
    }

    public final int getBorderThickness() {
        return this.zzcrf;
    }

    public final int getBorderType() {
        return this.zzcrg;
    }

    public final int getCallButtonColor() {
        return this.zzcrh;
    }

    public final String getCustomChannels() {
        return this.zzcri;
    }

    public final <T extends CustomEvent> Bundle getCustomEventExtrasBundle(Class<T> cls) {
        return this.zzaic.getCustomEventExtrasBundle(cls);
    }

    public final int getDescriptionTextColor() {
        return this.zzcrj;
    }

    public final String getFontFace() {
        return this.zzcrk;
    }

    public final int getHeaderTextColor() {
        return this.zzcrl;
    }

    public final int getHeaderTextSize() {
        return this.zzcrm;
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

    public final String getQuery() {
        return this.zzanr;
    }

    public final boolean isTestDevice(Context context) {
        return this.zzaic.isTestDevice(context);
    }
}
