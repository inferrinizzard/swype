package com.google.android.gms.internal;

import android.os.Process;

/* loaded from: classes.dex */
final class zzrn implements Runnable {
    private final int mPriority;
    private final Runnable zzw;

    public zzrn(Runnable runnable, int i) {
        this.zzw = runnable;
        this.mPriority = i;
    }

    @Override // java.lang.Runnable
    public final void run() {
        Process.setThreadPriority(this.mPriority);
        this.zzw.run();
    }
}
