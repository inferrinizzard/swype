package com.nuance.nmdp.speechkit.util.audio;

/* loaded from: classes.dex */
public interface IPrompt {
    void dispose();

    int getDuration();

    boolean isDisposed();

    void start(Object obj, IPlayerHelperListener iPlayerHelperListener, Object obj2);

    void stop();
}
