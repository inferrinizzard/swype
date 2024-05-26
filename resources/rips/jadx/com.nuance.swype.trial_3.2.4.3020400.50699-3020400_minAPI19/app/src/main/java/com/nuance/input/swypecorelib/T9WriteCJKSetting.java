package com.nuance.input.swypecorelib;

/* loaded from: classes.dex */
public class T9WriteCJKSetting extends T9WriteSetting {
    public static final int MAX_RESULT_CHARACTERS = 20;

    public T9WriteCJKSetting() {
        setRecognitionMode(1);
    }

    @Override // com.nuance.input.swypecorelib.T9WriteSetting
    public void setWritingDirection(int direction) {
        super.setWritingDirection(direction);
    }

    @Override // com.nuance.input.swypecorelib.T9WriteSetting
    public void setRecognitionMode(int mode) {
        super.setRecognitionMode(mode);
    }
}
