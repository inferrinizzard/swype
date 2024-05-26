package com.google.android.gms.internal;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import com.facebook.internal.AnalyticsEvents;
import com.google.android.gms.ads.AdActivity;

@zzin
/* loaded from: classes.dex */
public final class zzjy {
    final String zzcjm;
    public long zzckc = -1;
    public long zzckd = -1;
    public int zzcke = -1;
    public final Object zzail = new Object();
    public int zzckf = 0;
    public int zzckg = 0;

    public zzjy(String str) {
        this.zzcjm = str;
    }

    private static boolean zzab(Context context) {
        int identifier = context.getResources().getIdentifier("Theme.Translucent", AnalyticsEvents.PARAMETER_LIKE_VIEW_STYLE, "android");
        if (identifier == 0) {
            zzkd.zzcw("Please set theme of AdActivity to @android:style/Theme.Translucent to enable transparent background interstitial ad.");
            return false;
        }
        try {
            if (identifier == context.getPackageManager().getActivityInfo(new ComponentName(context.getPackageName(), AdActivity.CLASS_NAME), 0).theme) {
                return true;
            }
            zzkd.zzcw("Please set theme of AdActivity to @android:style/Theme.Translucent to enable transparent background interstitial ad.");
            return false;
        } catch (PackageManager.NameNotFoundException e) {
            zzkd.zzcx("Fail to fetch AdActivity theme");
            zzkd.zzcw("Please set theme of AdActivity to @android:style/Theme.Translucent to enable transparent background interstitial ad.");
            return false;
        }
    }

    public final Bundle zze(Context context, String str) {
        Bundle bundle;
        synchronized (this.zzail) {
            bundle = new Bundle();
            bundle.putString("session_id", this.zzcjm);
            bundle.putLong("basets", this.zzckd);
            bundle.putLong("currts", this.zzckc);
            bundle.putString("seq_num", str);
            bundle.putInt("preqs", this.zzcke);
            bundle.putInt("pclick", this.zzckf);
            bundle.putInt("pimp", this.zzckg);
            bundle.putBoolean("support_transparent_background", zzab(context));
        }
        return bundle;
    }
}
