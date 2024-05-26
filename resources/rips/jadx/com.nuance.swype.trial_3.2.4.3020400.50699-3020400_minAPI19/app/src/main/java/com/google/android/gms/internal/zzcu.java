package com.google.android.gms.internal;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;

@zzin
/* loaded from: classes.dex */
public final class zzcu {
    private final Context mContext;

    public zzcu(Context context) {
        com.google.android.gms.common.internal.zzab.zzb(context, "Context can not be null");
        this.mContext = context;
    }

    public final boolean zza(Intent intent) {
        com.google.android.gms.common.internal.zzab.zzb(intent, "Intent can not be null");
        return !this.mContext.getPackageManager().queryIntentActivities(intent, 0).isEmpty();
    }

    @TargetApi(14)
    public final boolean zzju() {
        return Build.VERSION.SDK_INT >= 14 && zza(new Intent("android.intent.action.INSERT").setType("vnd.android.cursor.dir/event"));
    }

    public final boolean zzjr() {
        return "mounted".equals(Environment.getExternalStorageState()) && this.mContext.checkCallingOrSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == 0;
    }
}
