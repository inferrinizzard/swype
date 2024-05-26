package com.google.api.client.util;

import java.io.IOException;

/* loaded from: classes.dex */
public final class BackOffUtils {
    public static boolean next(Sleeper sleeper, BackOff backOff) throws InterruptedException, IOException {
        long backOffTime = backOff.nextBackOffMillis();
        if (backOffTime == -1) {
            return false;
        }
        sleeper.sleep(backOffTime);
        return true;
    }
}
