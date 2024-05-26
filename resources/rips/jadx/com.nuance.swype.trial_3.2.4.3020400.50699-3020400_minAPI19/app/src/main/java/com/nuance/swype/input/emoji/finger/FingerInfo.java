package com.nuance.swype.input.emoji.finger;

/* loaded from: classes.dex */
public interface FingerInfo {

    /* loaded from: classes.dex */
    public enum PressState {
        UNPRESSED,
        PRESSED,
        SHORT,
        LONG,
        DOUBLE
    }

    String getDesc();

    float getDownX();

    float getDownY();

    String getMoveDesc();

    float getOffsetX();

    float getOffsetY();

    int getPointerId();

    float getPosX();

    float getPosY();

    PressState getPressState();

    float getVelX();

    float getVelY();

    boolean isDown();
}
