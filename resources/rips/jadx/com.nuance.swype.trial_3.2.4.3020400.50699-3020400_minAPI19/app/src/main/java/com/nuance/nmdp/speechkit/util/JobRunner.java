package com.nuance.nmdp.speechkit.util;

import com.nuance.nmdp.speechkit.oem.OemJobRunner;

/* loaded from: classes.dex */
public final class JobRunner extends Thread {
    private static int _initializeCount;
    private static OemJobRunner _instance = null;
    private static final Object _sync = new Object();

    /* loaded from: classes.dex */
    static final class PendingRunnable {
        public final int Delay;
        public final Runnable Runnable;

        public PendingRunnable(Runnable r, int delay) {
            this.Runnable = r;
            this.Delay = delay;
        }
    }

    public static void initialize() {
        synchronized (_sync) {
            if (_instance == null) {
                _instance = new OemJobRunner(_sync);
            }
            _initializeCount++;
        }
    }

    public static void shutdown() {
        synchronized (_sync) {
            if (_instance != null) {
                int i = _initializeCount - 1;
                _initializeCount = i;
                if (i < 0) {
                    Logger.warn(_instance, "JobRunner shutdown more often than initialized");
                    _initializeCount = 0;
                }
                if (_initializeCount <= 0) {
                    final OemJobRunner instance = _instance;
                    addJob(new Runnable() { // from class: com.nuance.nmdp.speechkit.util.JobRunner.1
                        @Override // java.lang.Runnable
                        public final void run() {
                            OemJobRunner.this.quit();
                        }
                    });
                    _instance = null;
                }
            } else {
                Logger.error(null, "JobRunner shutdown when not active");
            }
        }
    }

    public static void addJob(final Runnable r, int delay) {
        synchronized (_sync) {
            if (_instance == null) {
                Logger.error(null, "Job added while JobRunner not active");
            } else {
                _instance.addJob(new Runnable() { // from class: com.nuance.nmdp.speechkit.util.JobRunner.2
                    @Override // java.lang.Runnable
                    public final void run() {
                        try {
                            r.run();
                        } catch (Throwable tr) {
                            Logger.error(JobRunner._instance, "Error running job", tr);
                        }
                    }
                }, delay);
            }
        }
    }

    public static void addJob(Runnable r) {
        addJob(r, 0);
    }
}
