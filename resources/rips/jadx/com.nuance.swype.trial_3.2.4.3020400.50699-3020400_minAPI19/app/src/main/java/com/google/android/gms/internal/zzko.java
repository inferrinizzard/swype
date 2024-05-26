package com.google.android.gms.internal;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

@zzin
/* loaded from: classes.dex */
public final class zzko {
    private HandlerThread zzcmm = null;
    public Handler mHandler = null;
    public int zzcmn = 0;
    public final Object zzail = new Object();

    public final Looper zztq() {
        Looper looper;
        synchronized (this.zzail) {
            if (this.zzcmn != 0) {
                com.google.android.gms.common.internal.zzab.zzb(this.zzcmm, "Invalid state: mHandlerThread should already been initialized.");
            } else if (this.zzcmm == null) {
                zzkd.v("Starting the looper thread.");
                this.zzcmm = new HandlerThread("LooperProvider");
                this.zzcmm.start();
                this.mHandler = new Handler(this.zzcmm.getLooper());
                zzkd.v("Looper thread started.");
            } else {
                zzkd.v("Resuming the looper thread");
                this.zzail.notifyAll();
            }
            this.zzcmn++;
            looper = this.zzcmm.getLooper();
        }
        return looper;
    }
}
