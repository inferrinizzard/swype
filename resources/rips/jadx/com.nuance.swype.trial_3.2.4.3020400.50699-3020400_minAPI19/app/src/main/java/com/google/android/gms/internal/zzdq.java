package com.google.android.gms.internal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsServiceConnection;
import android.support.customtabs.CustomTabsSession;
import android.text.TextUtils;
import java.util.List;

@zzin
/* loaded from: classes.dex */
public final class zzdq implements zzaqc {
    private CustomTabsSession zzbeo;
    CustomTabsClient zzbep;
    private CustomTabsServiceConnection zzbeq;
    zza zzber;

    /* loaded from: classes.dex */
    public interface zza {
        void zzkn();

        void zzko();
    }

    public static boolean zzo(Context context) {
        PackageManager packageManager = context.getPackageManager();
        if (packageManager == null) {
            return false;
        }
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse("http://www.example.com"));
        ResolveInfo resolveActivity = packageManager.resolveActivity(intent, 0);
        List<ResolveInfo> queryIntentActivities = packageManager.queryIntentActivities(intent, 65536);
        if (queryIntentActivities == null || resolveActivity == null) {
            return false;
        }
        for (int i = 0; i < queryIntentActivities.size(); i++) {
            if (resolveActivity.activityInfo.name.equals(queryIntentActivities.get(i).activityInfo.name)) {
                return resolveActivity.activityInfo.packageName.equals(zzaqa.zzex(context));
            }
        }
        return false;
    }

    @Override // com.google.android.gms.internal.zzaqc
    public final void zza(CustomTabsClient customTabsClient) {
        this.zzbep = customTabsClient;
        this.zzbep.warmup$1349f3();
        if (this.zzber != null) {
            this.zzber.zzkn();
        }
    }

    public final void zzd(Activity activity) {
        if (this.zzbeq == null) {
            return;
        }
        activity.unbindService(this.zzbeq);
        this.zzbep = null;
        this.zzbeo = null;
        this.zzbeq = null;
    }

    public final CustomTabsSession zzkl() {
        if (this.zzbep == null) {
            this.zzbeo = null;
        } else if (this.zzbeo == null) {
            this.zzbeo = this.zzbep.newSession$6f4c7b26();
        }
        return this.zzbeo;
    }

    @Override // com.google.android.gms.internal.zzaqc
    public final void zzkm() {
        this.zzbep = null;
        this.zzbeo = null;
        if (this.zzber != null) {
            this.zzber.zzko();
        }
    }

    public final void zze(Activity activity) {
        String zzex;
        if (this.zzbep == null && (zzex = zzaqa.zzex(activity)) != null) {
            this.zzbeq = new zzaqb(this);
            CustomTabsServiceConnection customTabsServiceConnection = this.zzbeq;
            Intent intent = new Intent("android.support.customtabs.action.CustomTabsService");
            if (!TextUtils.isEmpty(zzex)) {
                intent.setPackage(zzex);
            }
            activity.bindService(intent, customTabsServiceConnection, 33);
        }
    }
}
