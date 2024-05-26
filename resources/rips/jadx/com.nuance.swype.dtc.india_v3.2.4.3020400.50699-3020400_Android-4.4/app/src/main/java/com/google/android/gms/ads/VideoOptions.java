package com.google.android.gms.ads;

import com.google.android.gms.internal.zzin;

@zzin
/* loaded from: classes.dex */
public final class VideoOptions {
    private final boolean zzaio;

    /* loaded from: classes.dex */
    public static final class Builder {
        private boolean zzaio = false;

        public final VideoOptions build() {
            return new VideoOptions(this, (byte) 0);
        }

        public final Builder setStartMuted(boolean z) {
            this.zzaio = z;
            return this;
        }
    }

    private VideoOptions(Builder builder) {
        this.zzaio = builder.zzaio;
    }

    /* synthetic */ VideoOptions(Builder builder, byte b) {
        this(builder);
    }

    public final boolean getStartMuted() {
        return this.zzaio;
    }
}
