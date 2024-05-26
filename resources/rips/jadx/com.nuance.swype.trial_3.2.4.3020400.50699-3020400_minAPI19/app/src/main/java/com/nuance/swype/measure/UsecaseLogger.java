package com.nuance.swype.measure;

import com.nuance.swype.measure.UsecaseStopwatch;
import com.nuance.swype.util.LogManager;

/* loaded from: classes.dex */
public final class UsecaseLogger implements UsecaseStopwatch.IUsecaseLogger {
    protected static final LogManager.Log log = LogManager.getLog("UsecaseLogger");

    @Override // com.nuance.swype.measure.UsecaseStopwatch.IUsecaseLogger
    public final void log(String message) {
        log.i(message);
    }
}
