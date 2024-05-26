package com.google.android.gms.ads;

import android.content.Context;
import com.facebook.login.widget.ProfilePictureView;
import com.google.android.gms.ads.internal.client.AdSizeParcel;
import com.google.android.gms.ads.internal.client.zzm;
import com.nuance.swype.input.R;
import jp.co.omronsoft.openwnn.JAJP.OpenWnnEngineJAJP;

/* loaded from: classes.dex */
public final class AdSize {
    public static final int AUTO_HEIGHT = -2;
    public static final int FULL_WIDTH = -1;
    private final int zzaie;
    private final int zzaif;
    private final String zzaig;
    public static final AdSize BANNER = new AdSize(R.styleable.ThemeTemplate_japaneseLeftPopup, 50, "320x50_mb");
    public static final AdSize FULL_BANNER = new AdSize(468, 60, "468x60_as");
    public static final AdSize LARGE_BANNER = new AdSize(R.styleable.ThemeTemplate_japaneseLeftPopup, 100, "320x100_as");
    public static final AdSize LEADERBOARD = new AdSize(728, 90, "728x90_as");
    public static final AdSize MEDIUM_RECTANGLE = new AdSize(300, 250, "300x250_as");
    public static final AdSize WIDE_SKYSCRAPER = new AdSize(160, OpenWnnEngineJAJP.FREQ_LEARN, "160x600_as");
    public static final AdSize SMART_BANNER = new AdSize(-1, -2, "smart_banner");
    public static final AdSize FLUID = new AdSize(-3, -4, "fluid");
    public static final AdSize SEARCH = new AdSize(-3, 0, "search_v2");

    /* JADX WARN: Illegal instructions before constructor call */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public AdSize(int r7, int r8) {
        /*
            r6 = this;
            r0 = -1
            if (r7 != r0) goto L50
            java.lang.String r0 = "FULL"
            r1 = r0
        L7:
            r0 = -2
            if (r8 != r0) goto L56
            java.lang.String r0 = "AUTO"
        Ld:
            java.lang.String r2 = "_as"
            java.lang.String r2 = java.lang.String.valueOf(r2)
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            java.lang.String r4 = java.lang.String.valueOf(r1)
            int r4 = r4.length()
            int r4 = r4 + 1
            java.lang.String r5 = java.lang.String.valueOf(r0)
            int r5 = r5.length()
            int r4 = r4 + r5
            java.lang.String r5 = java.lang.String.valueOf(r2)
            int r5 = r5.length()
            int r4 = r4 + r5
            r3.<init>(r4)
            java.lang.StringBuilder r1 = r3.append(r1)
            java.lang.String r3 = "x"
            java.lang.StringBuilder r1 = r1.append(r3)
            java.lang.StringBuilder r0 = r1.append(r0)
            java.lang.StringBuilder r0 = r0.append(r2)
            java.lang.String r0 = r0.toString()
            r6.<init>(r7, r8, r0)
            return
        L50:
            java.lang.String r0 = java.lang.String.valueOf(r7)
            r1 = r0
            goto L7
        L56:
            java.lang.String r0 = java.lang.String.valueOf(r8)
            goto Ld
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.ads.AdSize.<init>(int, int):void");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AdSize(int i, int i2, String str) {
        if (i < 0 && i != -1 && i != -3) {
            throw new IllegalArgumentException(new StringBuilder(37).append("Invalid width for AdSize: ").append(i).toString());
        }
        if (i2 < 0 && i2 != -2 && i2 != -4) {
            throw new IllegalArgumentException(new StringBuilder(38).append("Invalid height for AdSize: ").append(i2).toString());
        }
        this.zzaie = i;
        this.zzaif = i2;
        this.zzaig = str;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof AdSize)) {
            return false;
        }
        AdSize adSize = (AdSize) obj;
        return this.zzaie == adSize.zzaie && this.zzaif == adSize.zzaif && this.zzaig.equals(adSize.zzaig);
    }

    public final int getHeight() {
        return this.zzaif;
    }

    public final int getHeightInPixels(Context context) {
        switch (this.zzaif) {
            case ProfilePictureView.LARGE /* -4 */:
            case ProfilePictureView.NORMAL /* -3 */:
                return -1;
            case -2:
                return AdSizeParcel.zzb(context.getResources().getDisplayMetrics());
            default:
                return zzm.zziw().zza(context, this.zzaif);
        }
    }

    public final int getWidth() {
        return this.zzaie;
    }

    public final int getWidthInPixels(Context context) {
        switch (this.zzaie) {
            case ProfilePictureView.LARGE /* -4 */:
            case ProfilePictureView.NORMAL /* -3 */:
                return -1;
            case -2:
            default:
                return zzm.zziw().zza(context, this.zzaie);
            case -1:
                return AdSizeParcel.zza(context.getResources().getDisplayMetrics());
        }
    }

    public final int hashCode() {
        return this.zzaig.hashCode();
    }

    public final boolean isAutoHeight() {
        return this.zzaif == -2;
    }

    public final boolean isFluid() {
        return this.zzaie == -3 && this.zzaif == -4;
    }

    public final boolean isFullWidth() {
        return this.zzaie == -1;
    }

    public final String toString() {
        return this.zzaig;
    }
}
