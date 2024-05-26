package com.nuance.swype.input;

import android.os.SystemClock;
import com.nuance.swype.input.AbstractTapDetector;

/* loaded from: classes.dex */
public class TapDetector extends AbstractTapDetector {
    private static final int WAIT_TIME_AFTER_ROTATION_STARTINPUT = 1000;

    public TapDetector(AbstractTapDetector.TapHandler[] tapHandlers) {
        super(tapHandlers);
    }

    @Override // com.nuance.swype.input.AbstractTapDetector
    public void onShowInputRequested(int flags, boolean configChange) {
        boolean userRequested = (flags & 1) != 0;
        if (!configChange && userRequested) {
            onTap();
        }
    }

    @Override // com.nuance.swype.input.AbstractTapDetector
    public void onStartInput() {
        setNextAllowedTapTime(SystemClock.uptimeMillis() + 1000);
    }

    @Override // com.nuance.swype.input.AbstractTapDetector
    public void onInitializeInterface() {
        setNextAllowedTapTime(SystemClock.uptimeMillis() + 1000);
    }
}
