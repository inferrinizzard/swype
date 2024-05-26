package com.nuance.nmsp.client.sdk.common.oem.api;

/* loaded from: classes.dex */
public interface TimerSystem {

    /* loaded from: classes.dex */
    public interface TimerSystemTask extends Runnable {
    }

    boolean cancel(TimerSystemTask timerSystemTask);

    void schedule(TimerSystemTask timerSystemTask, long j);

    void stop();
}
