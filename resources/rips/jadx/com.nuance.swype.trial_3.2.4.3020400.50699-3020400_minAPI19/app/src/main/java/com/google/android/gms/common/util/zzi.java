package com.google.android.gms.common.util;

import android.annotation.TargetApi;
import android.content.Context;

/* loaded from: classes.dex */
public final class zzi {
    public static Boolean AX;
    public static Boolean AY;
    private static Boolean AZ;
    private static Boolean Ba;

    @TargetApi(20)
    public static boolean zzck(Context context) {
        if (AZ == null) {
            AZ = Boolean.valueOf(zzs.zzhb(20) && context.getPackageManager().hasSystemFeature("android.hardware.type.watch"));
        }
        return AZ.booleanValue();
    }

    @TargetApi(21)
    public static boolean zzcl(Context context) {
        if (Ba == null) {
            Ba = Boolean.valueOf(zzs.zzhb(21) && context.getPackageManager().hasSystemFeature("cn.google"));
        }
        return Ba.booleanValue();
    }
}
