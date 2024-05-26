package com.nuance.swype.input.emoji.finger;

/* loaded from: classes.dex */
public interface FingerStateListener {
    void onFingerCancel(Fingerable fingerable, FingerInfo fingerInfo);

    void onFingerMove(Fingerable fingerable, FingerInfo fingerInfo);

    void onFingerPress(Fingerable fingerable, FingerInfo fingerInfo);

    void onFingerRelease(Fingerable fingerable, FingerInfo fingerInfo, boolean z);
}
