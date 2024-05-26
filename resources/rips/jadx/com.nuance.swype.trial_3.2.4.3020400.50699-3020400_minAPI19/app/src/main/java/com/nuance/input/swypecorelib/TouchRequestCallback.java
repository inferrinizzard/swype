package com.nuance.input.swypecorelib;

/* loaded from: classes.dex */
public interface TouchRequestCallback {
    void keyboardLoaded(int i, int i2);

    void setMultiTapTimerTimeOutRequest(int i);

    void setTouchTimerTimeOutRequest(int i);

    void touchCanceled(boolean z, int i);

    void touchEnded(boolean z, int i, KeyType keyType, int i2, char c, boolean z2);

    void touchStarted(boolean z, int i, KeyType keyType, int i2, char c, boolean z2);

    void touchUpdated(boolean z, int i, KeyType keyType, int i2, char c, boolean z2);
}
