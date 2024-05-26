package com.google.android.gms.internal;

import android.content.Context;

/* loaded from: classes.dex */
public final class zzrp {
    private static zzrp Bv = new zzrp();
    private zzro Bu = null;

    private synchronized zzro zzcp(Context context) {
        if (this.Bu == null) {
            if (context.getApplicationContext() != null) {
                context = context.getApplicationContext();
            }
            this.Bu = new zzro(context);
        }
        return this.Bu;
    }

    public static zzro zzcq(Context context) {
        return Bv.zzcp(context);
    }
}
