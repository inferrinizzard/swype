package com.bumptech.glide.request;

/* loaded from: classes.dex */
public interface Request {
    void begin();

    void clear();

    boolean isCancelled();

    boolean isComplete();

    boolean isResourceSet();

    boolean isRunning();

    void pause();

    void recycle();
}
