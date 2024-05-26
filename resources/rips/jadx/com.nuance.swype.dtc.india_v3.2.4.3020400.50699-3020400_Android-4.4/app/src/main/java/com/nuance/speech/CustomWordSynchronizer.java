package com.nuance.speech;

import com.nuance.swype.input.InputFieldInfo;
import java.util.Set;

/* loaded from: classes.dex */
public abstract class CustomWordSynchronizer {
    public static final int RECOGNITION_TYPE_DICTATION = 0;
    public static final int RECOGNITION_TYPE_WEB_SERACH = 1;

    public abstract void addCustomWord(int i, String str);

    public abstract void clearAllCustomWords();

    public abstract void removeCustomWord(int i, String str);

    public abstract void removeCustomWords(Set<String> set);

    public abstract void resyncAllUserWords(int i);

    public void resyncAllUserWords(InputFieldInfo info) {
        resyncAllUserWords(getRecognizerType(info));
    }

    public void removeCustomWord(InputFieldInfo info, String word) {
        removeCustomWord(getRecognizerType(info), word);
    }

    public void addCustomWord(InputFieldInfo info, String word) {
        addCustomWord(getRecognizerType(info), word);
    }

    protected int getRecognizerType(InputFieldInfo info) {
        return (info == null || info.isWebSearchField()) ? 1 : 0;
    }
}
