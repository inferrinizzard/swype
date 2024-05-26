package com.google.android.gms.clearcut;

import android.content.Context;

/* loaded from: classes.dex */
public final class zza {
    private static int pX = -1;
    public static final zza pY = new zza();

    protected zza() {
    }

    public static int zzbk(Context context) {
        if (pX < 0) {
            pX = context.getSharedPreferences("bootCount", 0).getInt("bootCount", 1);
        }
        return pX;
    }
}
