package com.nuance.nmdp.speechkit.oem;

import android.os.Handler;
import android.os.Looper;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class OemJobRunner extends Thread {
    private Handler _handler = null;
    private final ArrayList<PendingRunnable> _pendingRunnables = new ArrayList<>();
    private final Object _sync;

    /* loaded from: classes.dex */
    static class PendingRunnable {
        public final int Delay;
        public final Runnable Runnable;

        public PendingRunnable(Runnable r, int delay) {
            this.Runnable = r;
            this.Delay = delay;
        }
    }

    public OemJobRunner(Object sync) {
        this._sync = sync;
        start();
    }

    public void quit() {
        Looper.myLooper().quit();
    }

    public void addJob(Runnable r, int delay) {
        if (this._handler != null) {
            if (delay > 0) {
                this._handler.postDelayed(r, delay);
                return;
            } else {
                this._handler.post(r);
                return;
            }
        }
        this._pendingRunnables.add(new PendingRunnable(r, delay));
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
        Looper.prepare();
        synchronized (this._sync) {
            this._handler = new Handler();
            int numRunnables = this._pendingRunnables.size();
            for (int i = 0; i < numRunnables; i++) {
                PendingRunnable r = this._pendingRunnables.get(i);
                if (r.Delay > 0) {
                    this._handler.postDelayed(r.Runnable, r.Delay);
                } else {
                    this._handler.post(r.Runnable);
                }
            }
            this._pendingRunnables.clear();
        }
        Looper.loop();
    }
}
