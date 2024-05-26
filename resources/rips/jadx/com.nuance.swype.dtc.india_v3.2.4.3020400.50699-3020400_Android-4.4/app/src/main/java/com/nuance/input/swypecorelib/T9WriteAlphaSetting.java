package com.nuance.input.swypecorelib;

/* loaded from: classes.dex */
public class T9WriteAlphaSetting extends T9WriteSetting {
    public static final int HWR_TEMPLATE_DATABASE = 265;
    public static final int MAX_RESULT_CANDIDATES = 10;
    public static final int MAX_RESULT_CHARACTERS = 64;

    /* JADX INFO: Access modifiers changed from: protected */
    public T9WriteAlphaSetting() {
        setRecognitionMode(2);
        setWritingDirection(0);
        setInputGuide(0);
        addPunctuationCategory();
    }

    @Override // com.nuance.input.swypecorelib.T9WriteSetting
    public void addTextCategory() {
        addTextOnlyCategory();
        addPunctuationCategory();
    }
}
