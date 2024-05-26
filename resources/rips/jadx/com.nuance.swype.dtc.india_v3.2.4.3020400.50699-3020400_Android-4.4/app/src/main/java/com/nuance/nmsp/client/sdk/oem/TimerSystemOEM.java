package com.nuance.nmsp.client.sdk.oem;

import com.nuance.nmsp.client.sdk.common.oem.api.LogFactory;
import com.nuance.nmsp.client.sdk.common.oem.api.TimerSystem;

/* loaded from: classes.dex */
public class TimerSystemOEM implements TimerSystem {
    static {
        LogFactory.getLog(TimerSystemOEM.class);
    }

    public static void terminate() {
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.TimerSystem
    public boolean cancel(TimerSystem.TimerSystemTask timerSystemTask) {
        return false;
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.TimerSystem
    public void schedule(TimerSystem.TimerSystemTask timerSystemTask, long j) {
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.TimerSystem
    public void stop() {
    }
}
