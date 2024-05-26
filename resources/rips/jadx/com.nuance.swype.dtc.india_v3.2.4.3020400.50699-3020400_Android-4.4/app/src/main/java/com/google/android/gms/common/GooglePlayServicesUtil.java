package com.google.android.gms.common;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import com.google.android.gms.R;
import com.google.android.gms.common.internal.zzab;
import com.google.android.gms.common.internal.zzh;
import com.google.android.gms.common.util.zzi;
import com.google.android.gms.common.util.zzs;
import com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.xmlResults.XMLResultsHandler;

/* loaded from: classes.dex */
public final class GooglePlayServicesUtil extends zze {

    @Deprecated
    public static final int GOOGLE_PLAY_SERVICES_VERSION_CODE = zze.GOOGLE_PLAY_SERVICES_VERSION_CODE;

    private GooglePlayServicesUtil() {
    }

    public static Resources getRemoteResource(Context context) {
        return zze.getRemoteResource(context);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void zza(int i, Context context, PendingIntent pendingIntent) {
        Notification build;
        Notification notification;
        boolean z;
        int i2;
        Resources resources = context.getResources();
        String zzbv = zzbv(context);
        String zzg = zzh.zzg(context, i);
        if (zzg == null) {
            zzg = resources.getString(R.string.common_google_play_services_notification_ticker);
        }
        String zzd = zzh.zzd(context, i, zzbv);
        if (zzi.zzck(context)) {
            zzab.zzbn(zzs.zzhb(16));
            build = new Notification.Builder(context).setSmallIcon(R.drawable.common_ic_googleplayservices).setPriority(2).setAutoCancel(true).setStyle(new Notification.BigTextStyle().bigText(new StringBuilder(String.valueOf(zzg).length() + 1 + String.valueOf(zzd).length()).append(zzg).append(XMLResultsHandler.SEP_SPACE).append(zzd).toString())).addAction(R.drawable.common_full_open_on_phone, resources.getString(R.string.common_open_on_phone), pendingIntent).build();
        } else {
            String string = resources.getString(R.string.common_google_play_services_notification_ticker);
            if (zzs.zzhb(11)) {
                Notification.Builder autoCancel = new Notification.Builder(context).setSmallIcon(android.R.drawable.stat_sys_warning).setContentTitle(zzg).setContentText(zzd).setContentIntent(pendingIntent).setTicker(string).setAutoCancel(true);
                if (zzs.zzhb(20)) {
                    autoCancel.setLocalOnly(true);
                }
                if (zzs.zzhb(16)) {
                    autoCancel.setStyle(new Notification.BigTextStyle().bigText(zzd));
                    notification = autoCancel.build();
                } else {
                    notification = autoCancel.getNotification();
                }
                if (Build.VERSION.SDK_INT == 19) {
                    notification.extras.putBoolean("android.support.localOnly", true);
                }
                build = notification;
            } else {
                NotificationCompat.Builder ticker = new NotificationCompat.Builder(context).setSmallIcon(android.R.drawable.stat_sys_warning).setTicker(string);
                ticker.mNotification.when = System.currentTimeMillis();
                NotificationCompat.Builder autoCancel$7abcb88d = ticker.setAutoCancel$7abcb88d();
                autoCancel$7abcb88d.mContentIntent = pendingIntent;
                build = autoCancel$7abcb88d.setContentTitle(zzg).setContentText(zzd).build();
            }
        }
        switch (i) {
            case 1:
            case 2:
            case 3:
            case 18:
            case 42:
                z = true;
                break;
            default:
                z = false;
                break;
        }
        if (z) {
            rv.set(false);
            i2 = 10436;
        } else {
            i2 = 39789;
        }
        ((NotificationManager) context.getSystemService("notification")).notify(i2, build);
    }
}
