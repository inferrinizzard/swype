package com.google.android.gms.internal;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import java.util.Locale;

@zzin
/* loaded from: classes.dex */
public final class zziv {
    public final int zzcbb;
    public final int zzcbc;
    public final float zzcbd;
    public final int zzcgd;
    public final boolean zzcge;
    public final boolean zzcgf;
    public final String zzcgg;
    public final String zzcgh;
    public final boolean zzcgi;
    public final boolean zzcgj;
    public final boolean zzcgk;
    public final boolean zzcgl;
    public final String zzcgm;
    public final String zzcgn;
    public final int zzcgo;
    public final int zzcgp;
    public final int zzcgq;
    public final int zzcgr;
    public final int zzcgs;
    public final int zzcgt;
    public final double zzcgu;
    public final boolean zzcgv;
    public final boolean zzcgw;
    public final int zzcgx;
    public final String zzcgy;
    public final boolean zzcgz;

    zziv(int i, boolean z, boolean z2, String str, String str2, boolean z3, boolean z4, boolean z5, boolean z6, String str3, String str4, int i2, int i3, int i4, int i5, int i6, int i7, float f, int i8, int i9, double d, boolean z7, boolean z8, int i10, String str5, boolean z9) {
        this.zzcgd = i;
        this.zzcge = z;
        this.zzcgf = z2;
        this.zzcgg = str;
        this.zzcgh = str2;
        this.zzcgi = z3;
        this.zzcgj = z4;
        this.zzcgk = z5;
        this.zzcgl = z6;
        this.zzcgm = str3;
        this.zzcgn = str4;
        this.zzcgo = i2;
        this.zzcgp = i3;
        this.zzcgq = i4;
        this.zzcgr = i5;
        this.zzcgs = i6;
        this.zzcgt = i7;
        this.zzcbd = f;
        this.zzcbb = i8;
        this.zzcbc = i9;
        this.zzcgu = d;
        this.zzcgv = z7;
        this.zzcgw = z8;
        this.zzcgx = i10;
        this.zzcgy = str5;
        this.zzcgz = z9;
    }

    /* loaded from: classes.dex */
    public static final class zza {
        private int zzcbb;
        private int zzcbc;
        private float zzcbd;
        private int zzcgd;
        private boolean zzcge;
        private boolean zzcgf;
        private String zzcgg;
        private String zzcgh;
        private boolean zzcgi;
        private boolean zzcgj;
        private boolean zzcgk;
        private boolean zzcgl;
        private String zzcgm;
        private String zzcgn;
        private int zzcgo;
        private int zzcgp;
        private int zzcgq;
        private int zzcgr;
        private int zzcgs;
        private int zzcgt;
        private double zzcgu;
        private boolean zzcgv;
        private boolean zzcgw;
        private int zzcgx;
        private String zzcgy;
        private boolean zzcgz;

        public zza(Context context) {
            DisplayMetrics displayMetrics;
            PackageManager packageManager = context.getPackageManager();
            zzv(context);
            zza(context, packageManager);
            zzw(context);
            Locale locale = Locale.getDefault();
            this.zzcge = zza(packageManager, "geo:0,0?q=donuts") != null;
            this.zzcgf = zza(packageManager, "http://www.google.com") != null;
            this.zzcgh = locale.getCountry();
            this.zzcgi = com.google.android.gms.ads.internal.client.zzm.zziw().zztw();
            this.zzcgj = com.google.android.gms.common.util.zzi.zzcl(context);
            this.zzcgm = locale.getLanguage();
            this.zzcgn = zza(packageManager);
            Resources resources = context.getResources();
            if (resources == null || (displayMetrics = resources.getDisplayMetrics()) == null) {
                return;
            }
            this.zzcbd = displayMetrics.density;
            this.zzcbb = displayMetrics.widthPixels;
            this.zzcbc = displayMetrics.heightPixels;
        }

        private static ResolveInfo zza(PackageManager packageManager, String str) {
            return packageManager.resolveActivity(new Intent("android.intent.action.VIEW", Uri.parse(str)), 65536);
        }

        private static String zza(PackageManager packageManager) {
            ActivityInfo activityInfo;
            ResolveInfo zza = zza(packageManager, "market://details?id=com.google.android.gms.ads");
            if (zza == null || (activityInfo = zza.activityInfo) == null) {
                return null;
            }
            try {
                PackageInfo packageInfo = packageManager.getPackageInfo(activityInfo.packageName, 0);
                if (packageInfo == null) {
                    return null;
                }
                int i = packageInfo.versionCode;
                String valueOf = String.valueOf(activityInfo.packageName);
                return new StringBuilder(String.valueOf(valueOf).length() + 12).append(i).append(".").append(valueOf).toString();
            } catch (PackageManager.NameNotFoundException e) {
                return null;
            }
        }

        @TargetApi(16)
        private void zza(Context context, PackageManager packageManager) {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService("phone");
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
            this.zzcgg = telephonyManager.getNetworkOperator();
            this.zzcgq = telephonyManager.getNetworkType();
            this.zzcgr = telephonyManager.getPhoneType();
            this.zzcgp = -2;
            this.zzcgw = false;
            this.zzcgx = -1;
            com.google.android.gms.ads.internal.zzu.zzfq();
            if (zzkh.zza(packageManager, context.getPackageName(), "android.permission.ACCESS_NETWORK_STATE")) {
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                if (activeNetworkInfo != null) {
                    this.zzcgp = activeNetworkInfo.getType();
                    this.zzcgx = activeNetworkInfo.getDetailedState().ordinal();
                } else {
                    this.zzcgp = -1;
                }
                if (Build.VERSION.SDK_INT >= 16) {
                    this.zzcgw = connectivityManager.isActiveNetworkMetered();
                }
            }
        }

        private void zzv(Context context) {
            com.google.android.gms.ads.internal.zzu.zzfq();
            AudioManager zzak = zzkh.zzak(context);
            if (zzak != null) {
                try {
                    this.zzcgd = zzak.getMode();
                    this.zzcgk = zzak.isMusicActive();
                    this.zzcgl = zzak.isSpeakerphoneOn();
                    this.zzcgo = zzak.getStreamVolume(3);
                    this.zzcgs = zzak.getRingerMode();
                    this.zzcgt = zzak.getStreamVolume(2);
                    return;
                } catch (Throwable th) {
                    com.google.android.gms.ads.internal.zzu.zzft().zzb(th, true);
                }
            }
            this.zzcgd = -2;
            this.zzcgk = false;
            this.zzcgl = false;
            this.zzcgo = 0;
            this.zzcgs = 0;
            this.zzcgt = 0;
        }

        private void zzw(Context context) {
            Intent registerReceiver = context.registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
            if (registerReceiver == null) {
                this.zzcgu = -1.0d;
                this.zzcgv = false;
            } else {
                int intExtra = registerReceiver.getIntExtra("status", -1);
                this.zzcgu = registerReceiver.getIntExtra("level", -1) / registerReceiver.getIntExtra("scale", -1);
                this.zzcgv = intExtra == 2 || intExtra == 5;
            }
        }

        public final zziv zzrn() {
            return new zziv(this.zzcgd, this.zzcge, this.zzcgf, this.zzcgg, this.zzcgh, this.zzcgi, this.zzcgj, this.zzcgk, this.zzcgl, this.zzcgm, this.zzcgn, this.zzcgo, this.zzcgp, this.zzcgq, this.zzcgr, this.zzcgs, this.zzcgt, this.zzcbd, this.zzcbb, this.zzcbc, this.zzcgu, this.zzcgv, this.zzcgw, this.zzcgx, this.zzcgy, this.zzcgz);
        }

        public zza(Context context, zziv zzivVar) {
            PackageManager packageManager = context.getPackageManager();
            zzv(context);
            zza(context, packageManager);
            zzw(context);
            this.zzcgy = Build.FINGERPRINT;
            this.zzcgz = zzdq.zzo(context);
            this.zzcge = zzivVar.zzcge;
            this.zzcgf = zzivVar.zzcgf;
            this.zzcgh = zzivVar.zzcgh;
            this.zzcgi = zzivVar.zzcgi;
            this.zzcgj = zzivVar.zzcgj;
            this.zzcgm = zzivVar.zzcgm;
            this.zzcgn = zzivVar.zzcgn;
            this.zzcbd = zzivVar.zzcbd;
            this.zzcbb = zzivVar.zzcbb;
            this.zzcbc = zzivVar.zzcbc;
        }
    }
}
