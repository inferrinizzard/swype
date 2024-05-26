package com.google.android.gms.common;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzo;
import com.google.android.gms.common.util.zzi;
import com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.xmlResults.XMLResultsHandler;

/* loaded from: classes.dex */
public class zzc {
    public static final int GOOGLE_PLAY_SERVICES_VERSION_CODE = zze.GOOGLE_PLAY_SERVICES_VERSION_CODE;
    private static final zzc rf = new zzc();

    public static zzc zzang() {
        return rf;
    }

    public static void zzbp(Context context) {
        zze.zzbp(context);
    }

    private static String zzm(Context context, String str) {
        StringBuilder sb = new StringBuilder();
        sb.append("gcore_");
        sb.append(GOOGLE_PLAY_SERVICES_VERSION_CODE);
        sb.append(XMLResultsHandler.SEP_HYPHEN);
        if (!TextUtils.isEmpty(str)) {
            sb.append(str);
        }
        sb.append(XMLResultsHandler.SEP_HYPHEN);
        if (context != null) {
            sb.append(context.getPackageName());
        }
        sb.append(XMLResultsHandler.SEP_HYPHEN);
        if (context != null) {
            try {
                sb.append(context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode);
            } catch (PackageManager.NameNotFoundException e) {
            }
        }
        return sb.toString();
    }

    public PendingIntent getErrorResolutionPendingIntent(Context context, int i, int i2) {
        return zza(context, i, i2, null);
    }

    public int isGooglePlayServicesAvailable(Context context) {
        int isGooglePlayServicesAvailable = zze.isGooglePlayServicesAvailable(context);
        if (zze.zzc(context, isGooglePlayServicesAvailable)) {
            return 18;
        }
        return isGooglePlayServicesAvailable;
    }

    public boolean isUserResolvableError(int i) {
        return zze.isUserRecoverableError(i);
    }

    public PendingIntent zza(Context context, int i, int i2, String str) {
        if (zzi.zzck(context) && i == 2) {
            i = 42;
        }
        Intent zza = zza(context, i, str);
        if (zza == null) {
            return null;
        }
        return PendingIntent.getActivity(context, i2, zza, 268435456);
    }

    public Intent zza(Context context, int i, String str) {
        switch (i) {
            case 1:
            case 2:
                return zzo.zzad("com.google.android.gms", zzm(context, str));
            case 3:
                return zzo.zzho("com.google.android.gms");
            case 42:
                return zzo.zzata();
            default:
                return null;
        }
    }

    public int zzbn(Context context) {
        return zze.zzbn(context);
    }

    public boolean zzc(Context context, int i) {
        return zze.zzc(context, i);
    }

    @Deprecated
    public Intent zzfc(int i) {
        return zza(null, i, null);
    }
}
