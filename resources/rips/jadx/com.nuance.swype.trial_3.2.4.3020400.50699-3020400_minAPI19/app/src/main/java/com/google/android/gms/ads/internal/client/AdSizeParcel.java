package com.google.android.gms.ads.internal.client;

import android.content.Context;
import android.os.Parcel;
import android.util.DisplayMetrics;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.internal.zzin;

@zzin
/* loaded from: classes.dex */
public class AdSizeParcel extends AbstractSafeParcelable {
    public static final zzi CREATOR = new zzi();
    public final int height;
    public final int heightPixels;
    public final int versionCode;
    public final int width;
    public final int widthPixels;
    public final String zzaur;
    public final boolean zzaus;
    public final AdSizeParcel[] zzaut;
    public final boolean zzauu;
    public final boolean zzauv;
    public boolean zzauw;

    public AdSizeParcel() {
        this(5, "interstitial_mb", 0, 0, true, 0, 0, null, false, false, false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AdSizeParcel(int i, String str, int i2, int i3, boolean z, int i4, int i5, AdSizeParcel[] adSizeParcelArr, boolean z2, boolean z3, boolean z4) {
        this.versionCode = i;
        this.zzaur = str;
        this.height = i2;
        this.heightPixels = i3;
        this.zzaus = z;
        this.width = i4;
        this.widthPixels = i5;
        this.zzaut = adSizeParcelArr;
        this.zzauu = z2;
        this.zzauv = z3;
        this.zzauw = z4;
    }

    public AdSizeParcel(Context context, AdSize adSize) {
        this(context, new AdSize[]{adSize});
    }

    public AdSizeParcel(Context context, AdSize[] adSizeArr) {
        int i;
        AdSize adSize = adSizeArr[0];
        this.versionCode = 5;
        this.zzaus = false;
        this.zzauv = adSize.isFluid();
        if (this.zzauv) {
            this.width = AdSize.BANNER.getWidth();
            this.height = AdSize.BANNER.getHeight();
        } else {
            this.width = adSize.getWidth();
            this.height = adSize.getHeight();
        }
        boolean z = this.width == -1;
        boolean z2 = this.height == -2;
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        if (z) {
            if (zzm.zziw().zzas(context) && zzm.zziw().zzat(context)) {
                this.widthPixels = zza(displayMetrics) - zzm.zziw().zzau(context);
            } else {
                this.widthPixels = zza(displayMetrics);
            }
            double d = this.widthPixels / displayMetrics.density;
            int i2 = (int) d;
            i = d - ((double) ((int) d)) >= 0.01d ? i2 + 1 : i2;
        } else {
            int i3 = this.width;
            this.widthPixels = zzm.zziw().zza(displayMetrics, this.width);
            i = i3;
        }
        int zzc = z2 ? zzc(displayMetrics) : this.height;
        this.heightPixels = zzm.zziw().zza(displayMetrics, zzc);
        if (z || z2) {
            this.zzaur = new StringBuilder(26).append(i).append("x").append(zzc).append("_as").toString();
        } else if (this.zzauv) {
            this.zzaur = "320x50_mb";
        } else {
            this.zzaur = adSize.toString();
        }
        if (adSizeArr.length > 1) {
            this.zzaut = new AdSizeParcel[adSizeArr.length];
            for (int i4 = 0; i4 < adSizeArr.length; i4++) {
                this.zzaut[i4] = new AdSizeParcel(context, adSizeArr[i4]);
            }
        } else {
            this.zzaut = null;
        }
        this.zzauu = false;
        this.zzauw = false;
    }

    public AdSizeParcel(AdSizeParcel adSizeParcel, AdSizeParcel[] adSizeParcelArr) {
        this(5, adSizeParcel.zzaur, adSizeParcel.height, adSizeParcel.heightPixels, adSizeParcel.zzaus, adSizeParcel.width, adSizeParcel.widthPixels, adSizeParcelArr, adSizeParcel.zzauu, adSizeParcel.zzauv, adSizeParcel.zzauw);
    }

    public static int zza(DisplayMetrics displayMetrics) {
        return displayMetrics.widthPixels;
    }

    public static int zzb(DisplayMetrics displayMetrics) {
        return (int) (zzc(displayMetrics) * displayMetrics.density);
    }

    private static int zzc(DisplayMetrics displayMetrics) {
        int i = (int) (displayMetrics.heightPixels / displayMetrics.density);
        if (i <= 400) {
            return 32;
        }
        return i <= 720 ? 50 : 90;
    }

    public static AdSizeParcel zzii() {
        return new AdSizeParcel(5, "reward_mb", 0, 0, true, 0, 0, null, false, false, false);
    }

    public static AdSizeParcel zzk(Context context) {
        return new AdSizeParcel(5, "320x50_mb", 0, 0, false, 0, 0, null, true, false, false);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        zzi.zza(this, parcel, i);
    }

    public AdSize zzij() {
        return com.google.android.gms.ads.zza.zza(this.width, this.height, this.zzaur);
    }

    public void zzk(boolean z) {
        this.zzauw = z;
    }
}
