package com.nuance.swype.util;

import android.os.Handler;
import android.os.SystemClock;
import java.lang.Runnable;

/* loaded from: classes.dex */
public final class Callback<T extends Runnable> implements Runnable {
    private final Handler handler;
    private long interval;
    private boolean isPending;
    private final T runnable;
    private long nextTime = -1;
    private final boolean isRepeating = false;

    private Callback(Handler handler, T runnable, long interval) {
        this.handler = handler;
        this.runnable = runnable;
        this.interval = interval;
    }

    public static <T extends Runnable> Callback<T> create$afe0100(T runnable, long ms) {
        return new Callback<>(new Handler(), runnable, ms);
    }

    public static <T extends Runnable> Callback<T> create(T runnable) {
        return new Callback<>(new Handler(), runnable, 0L);
    }

    public final void stop() {
        this.isPending = false;
        this.handler.removeCallbacks(this);
    }

    private void restart() {
        stop();
        scheduleNext();
    }

    public final void restart(int ms) {
        this.interval = ms;
        restart();
    }

    public final void start() {
        if (!this.isPending) {
            restart();
        }
    }

    @Override // java.lang.Runnable
    public final void run() {
        if (this.isPending) {
            this.isPending = this.isRepeating;
            if (this.runnable != null) {
                this.runnable.run();
            }
            if (this.isRepeating && this.isPending) {
                scheduleNext();
            }
        }
    }

    private void scheduleNext() {
        if (!this.isPending) {
            this.isPending = true;
            this.nextTime = SystemClock.uptimeMillis();
        }
        this.nextTime += this.interval;
        this.handler.postAtTime(this, this.nextTime);
    }
}
