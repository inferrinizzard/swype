package com.google.android.gms.internal;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;

/* loaded from: classes.dex */
public final class zzqi {
    final Object vl;

    public zzqi(Activity activity) {
        com.google.android.gms.common.internal.zzab.zzb(activity, "Activity must not be null");
        com.google.android.gms.common.internal.zzab.zzb(com.google.android.gms.common.util.zzs.zzhb(11) || (activity instanceof FragmentActivity), "This Activity is not supported before platform version 11 (3.0 Honeycomb). Please use FragmentActivity instead.");
        this.vl = activity;
    }
}
