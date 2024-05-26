package com.google.android.gms.internal;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

@zzin
/* loaded from: classes.dex */
public final class zzke extends Handler {
    public zzke(Looper looper) {
        super(looper);
    }

    @Override // android.os.Handler
    public final void handleMessage(Message message) {
        try {
            super.handleMessage(message);
        } catch (Exception e) {
            com.google.android.gms.ads.internal.zzu.zzft().zzb((Throwable) e, false);
            throw e;
        }
    }
}
