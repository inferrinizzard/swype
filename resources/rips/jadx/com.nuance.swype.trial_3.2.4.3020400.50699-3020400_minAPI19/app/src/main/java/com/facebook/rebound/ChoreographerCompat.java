package com.facebook.rebound;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.Choreographer;

/* loaded from: classes.dex */
public class ChoreographerCompat {
    private static final boolean IS_JELLYBEAN_OR_HIGHER;
    private static final long ONE_FRAME_MILLIS = 17;
    private static ChoreographerCompat __instance;
    private Choreographer mChoreographer;
    private Handler mHandler;

    static {
        IS_JELLYBEAN_OR_HIGHER = Build.VERSION.SDK_INT >= 16;
        __instance = new ChoreographerCompat();
    }

    public static ChoreographerCompat getInstance() {
        return __instance;
    }

    private ChoreographerCompat() {
        if (IS_JELLYBEAN_OR_HIGHER) {
            this.mChoreographer = getChoreographer();
        } else {
            this.mHandler = new Handler(Looper.getMainLooper());
        }
    }

    public void postFrameCallback(FrameCallback callbackWrapper) {
        if (IS_JELLYBEAN_OR_HIGHER) {
            choreographerPostFrameCallback(callbackWrapper.getFrameCallback());
        } else {
            this.mHandler.postDelayed(callbackWrapper.getRunnable(), 0L);
        }
    }

    public void postFrameCallbackDelayed(FrameCallback callbackWrapper, long delayMillis) {
        if (IS_JELLYBEAN_OR_HIGHER) {
            choreographerPostFrameCallbackDelayed(callbackWrapper.getFrameCallback(), delayMillis);
        } else {
            this.mHandler.postDelayed(callbackWrapper.getRunnable(), ONE_FRAME_MILLIS + delayMillis);
        }
    }

    public void removeFrameCallback(FrameCallback callbackWrapper) {
        if (IS_JELLYBEAN_OR_HIGHER) {
            choreographerRemoveFrameCallback(callbackWrapper.getFrameCallback());
        } else {
            this.mHandler.removeCallbacks(callbackWrapper.getRunnable());
        }
    }

    @TargetApi(16)
    private Choreographer getChoreographer() {
        return Choreographer.getInstance();
    }

    @TargetApi(16)
    private void choreographerPostFrameCallback(Choreographer.FrameCallback frameCallback) {
        this.mChoreographer.postFrameCallback(frameCallback);
    }

    @TargetApi(16)
    private void choreographerPostFrameCallbackDelayed(Choreographer.FrameCallback frameCallback, long delayMillis) {
        this.mChoreographer.postFrameCallbackDelayed(frameCallback, delayMillis);
    }

    @TargetApi(16)
    private void choreographerRemoveFrameCallback(Choreographer.FrameCallback frameCallback) {
        this.mChoreographer.removeFrameCallback(frameCallback);
    }

    /* loaded from: classes.dex */
    public static abstract class FrameCallback {
        private Choreographer.FrameCallback mFrameCallback;
        private Runnable mRunnable;

        public abstract void doFrame(long j);

        @TargetApi(16)
        Choreographer.FrameCallback getFrameCallback() {
            if (this.mFrameCallback == null) {
                this.mFrameCallback = new Choreographer.FrameCallback() { // from class: com.facebook.rebound.ChoreographerCompat.FrameCallback.1
                    @Override // android.view.Choreographer.FrameCallback
                    public void doFrame(long frameTimeNanos) {
                        FrameCallback.this.doFrame(frameTimeNanos);
                    }
                };
            }
            return this.mFrameCallback;
        }

        Runnable getRunnable() {
            if (this.mRunnable == null) {
                this.mRunnable = new Runnable() { // from class: com.facebook.rebound.ChoreographerCompat.FrameCallback.2
                    @Override // java.lang.Runnable
                    public void run() {
                        FrameCallback.this.doFrame(System.nanoTime());
                    }
                };
            }
            return this.mRunnable;
        }
    }
}
