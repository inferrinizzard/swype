package com.google.ads;

import android.content.Context;
import com.nuance.swype.input.R;
import jp.co.omronsoft.openwnn.JAJP.OpenWnnEngineJAJP;

@Deprecated
/* loaded from: classes.dex */
public final class AdSize {
    public static final int AUTO_HEIGHT = -2;
    public static final int FULL_WIDTH = -1;
    public static final int LANDSCAPE_AD_HEIGHT = 32;
    public static final int LARGE_AD_HEIGHT = 90;
    public static final int PORTRAIT_AD_HEIGHT = 50;
    private final com.google.android.gms.ads.AdSize zzcj;
    public static final AdSize SMART_BANNER = new AdSize(-1, -2, (byte) 0);
    public static final AdSize BANNER = new AdSize(R.styleable.ThemeTemplate_japaneseLeftPopup, 50, (byte) 0);
    public static final AdSize IAB_MRECT = new AdSize(300, 250, (byte) 0);
    public static final AdSize IAB_BANNER = new AdSize(468, 60, (byte) 0);
    public static final AdSize IAB_LEADERBOARD = new AdSize(728, 90, (byte) 0);
    public static final AdSize IAB_WIDE_SKYSCRAPER = new AdSize(160, OpenWnnEngineJAJP.FREQ_LEARN, (byte) 0);

    public AdSize(int i, int i2) {
        this(new com.google.android.gms.ads.AdSize(i, i2));
    }

    private AdSize(int i, int i2, byte b) {
        this(new com.google.android.gms.ads.AdSize(i, i2));
    }

    public AdSize(com.google.android.gms.ads.AdSize adSize) {
        this.zzcj = adSize;
    }

    public final boolean equals(Object obj) {
        if (obj instanceof AdSize) {
            return this.zzcj.equals(((AdSize) obj).zzcj);
        }
        return false;
    }

    public final AdSize findBestSize(AdSize... adSizeArr) {
        float f;
        AdSize adSize;
        AdSize adSize2 = null;
        if (adSizeArr != null) {
            float f2 = 0.0f;
            int width = getWidth();
            int height = getHeight();
            int length = adSizeArr.length;
            int i = 0;
            while (i < length) {
                AdSize adSize3 = adSizeArr[i];
                if (isSizeAppropriate(adSize3.getWidth(), adSize3.getHeight())) {
                    f = (r0 * r8) / (width * height);
                    if (f > 1.0f) {
                        f = 1.0f / f;
                    }
                    if (f > f2) {
                        adSize = adSize3;
                        i++;
                        adSize2 = adSize;
                        f2 = f;
                    }
                }
                f = f2;
                adSize = adSize2;
                i++;
                adSize2 = adSize;
                f2 = f;
            }
        }
        return adSize2;
    }

    public final int getHeight() {
        return this.zzcj.getHeight();
    }

    public final int getHeightInPixels(Context context) {
        return this.zzcj.getHeightInPixels(context);
    }

    public final int getWidth() {
        return this.zzcj.getWidth();
    }

    public final int getWidthInPixels(Context context) {
        return this.zzcj.getWidthInPixels(context);
    }

    public final int hashCode() {
        return this.zzcj.hashCode();
    }

    public final boolean isAutoHeight() {
        return this.zzcj.isAutoHeight();
    }

    public final boolean isCustomAdSize() {
        return false;
    }

    public final boolean isFullWidth() {
        return this.zzcj.isFullWidth();
    }

    public final boolean isSizeAppropriate(int i, int i2) {
        int width = getWidth();
        int height = getHeight();
        return ((float) i) <= ((float) width) * 1.25f && ((float) i) >= ((float) width) * 0.8f && ((float) i2) <= ((float) height) * 1.25f && ((float) i2) >= ((float) height) * 0.8f;
    }

    public final String toString() {
        return this.zzcj.toString();
    }
}
