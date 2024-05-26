package com.nuance.swype.measure;

import android.os.SystemClock;

/* loaded from: classes.dex */
public final class UsecaseStopwatch {
    private static UsecaseStopwatch sInstance;
    private final IUsecaseLogger mLogger = new UsecaseLogger();

    /* loaded from: classes.dex */
    public interface IUsecaseLogger {
        void log(String str);
    }

    protected UsecaseStopwatch() {
    }

    public static UsecaseStopwatch getInstance() {
        if (sInstance == null) {
            sInstance = new UsecaseStopwatch();
        }
        return sInstance;
    }

    public final void start(Usecase usecase) {
        this.mLogger.log("Starting [" + usecase.mName + "]");
        usecase.mStartTime = SystemClock.elapsedRealtime();
    }

    /* loaded from: classes.dex */
    public static class Usecase {
        private String mDetails;
        long mElapsedTime;
        long mEndTime;
        String mName;
        public long mStartTime;

        private Usecase(String name, String details) {
            this.mName = name;
            this.mDetails = details;
        }

        public Usecase(String name) {
            this(name, "");
        }
    }

    public final void stop(Usecase usecase) {
        usecase.mEndTime = SystemClock.elapsedRealtime();
        usecase.mElapsedTime = usecase.mEndTime - usecase.mStartTime;
        this.mLogger.log("Finished [" + usecase.mName + "]. Time: " + usecase.mElapsedTime + " ms");
    }
}
