package com.nuance.swype.input.emoji.finger;

/* loaded from: classes.dex */
public interface Fingerable {

    /* loaded from: classes.dex */
    public interface FingerableMapper {
        Fingerable map(float f, float f2);
    }

    boolean isDoubleTapSupported();

    boolean isPressHoldSupported();
}
