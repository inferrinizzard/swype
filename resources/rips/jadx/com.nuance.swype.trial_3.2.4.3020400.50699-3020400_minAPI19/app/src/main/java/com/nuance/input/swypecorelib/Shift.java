package com.nuance.input.swypecorelib;

/* loaded from: classes.dex */
public class Shift {
    private static final int NATIVE_SHIFT_LOCKED = 2;
    private static final int NATIVE_SHIFT_OFF = 0;
    private static final int NATIVE_SHIFT_ON = 1;

    /* loaded from: classes.dex */
    public enum ShiftState {
        OFF(0),
        ON(1),
        LOCKED(2);

        private final int index;

        ShiftState(int index) {
            this.index = index;
        }

        public final int getIndex() {
            return this.index;
        }

        public static ShiftState valueOf(int value) {
            switch (value) {
                case 1:
                    return ON;
                case 2:
                    return LOCKED;
                default:
                    return OFF;
            }
        }
    }
}
