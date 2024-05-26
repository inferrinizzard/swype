package com.google.android.gms.ads.formats;

import com.google.android.gms.internal.zzin;

@zzin
/* loaded from: classes.dex */
public final class NativeAdOptions {
    public static final int ADCHOICES_BOTTOM_LEFT = 3;
    public static final int ADCHOICES_BOTTOM_RIGHT = 2;
    public static final int ADCHOICES_TOP_LEFT = 0;
    public static final int ADCHOICES_TOP_RIGHT = 1;
    public static final int ORIENTATION_ANY = 0;
    public static final int ORIENTATION_LANDSCAPE = 2;
    public static final int ORIENTATION_PORTRAIT = 1;
    private final boolean zzaiv;
    private final int zzaiw;
    private final boolean zzaix;
    private final int zzaiy;

    /* loaded from: classes.dex */
    public @interface AdChoicesPlacement {
    }

    /* loaded from: classes.dex */
    public static final class Builder {
        private boolean zzaiv = false;
        private int zzaiw = 0;
        private boolean zzaix = false;
        private int zzaiy = 1;

        public final NativeAdOptions build() {
            return new NativeAdOptions(this, (byte) 0);
        }

        public final Builder setAdChoicesPlacement(@AdChoicesPlacement int i) {
            this.zzaiy = i;
            return this;
        }

        public final Builder setImageOrientation(int i) {
            this.zzaiw = i;
            return this;
        }

        public final Builder setRequestMultipleImages(boolean z) {
            this.zzaix = z;
            return this;
        }

        public final Builder setReturnUrlsForImageAssets(boolean z) {
            this.zzaiv = z;
            return this;
        }
    }

    private NativeAdOptions(Builder builder) {
        this.zzaiv = builder.zzaiv;
        this.zzaiw = builder.zzaiw;
        this.zzaix = builder.zzaix;
        this.zzaiy = builder.zzaiy;
    }

    /* synthetic */ NativeAdOptions(Builder builder, byte b) {
        this(builder);
    }

    public final int getAdChoicesPlacement() {
        return this.zzaiy;
    }

    public final int getImageOrientation() {
        return this.zzaiw;
    }

    public final boolean shouldRequestMultipleImages() {
        return this.zzaix;
    }

    public final boolean shouldReturnUrlsForImageAssets() {
        return this.zzaiv;
    }
}
