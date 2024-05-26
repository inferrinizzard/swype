package com.nuance.input.swypecorelib;

import com.nuance.input.swypecorelib.WordCandidate;

/* loaded from: classes.dex */
public class T9WriteRecognizeCandidate {
    public static final int STEM_OF_WORD = 1;
    public static final int TERMINAL_WORD = 2;
    public static final int WORD_NOT_FROM_DICTIONARY = 0;
    public String mCandidate;
    public final int mEndWithGesture;
    public final int mEndWithInstantGesture;
    public final int mType;

    public T9WriteRecognizeCandidate(String candidate, int type, int endWithInstantGesture, int endWithGesture) {
        this.mCandidate = candidate;
        this.mType = type;
        this.mEndWithInstantGesture = endWithInstantGesture;
        this.mEndWithGesture = endWithGesture;
    }

    public WordCandidate toWordCandidate() {
        WordCandidate.Source source = this.mType == 0 ? WordCandidate.Source.WORD_SOURCE_NEW_WORD : WordCandidate.Source.WORD_SOURCE_UNKNOWN;
        return new WordCandidate(this.mCandidate, 0, source.value(), 0);
    }
}
