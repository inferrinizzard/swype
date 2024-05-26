package com.nuance.input.swypecorelib.usagedata;

import com.nuance.input.swypecorelib.Candidates;
import com.nuance.input.swypecorelib.WordCandidate;

/* loaded from: classes.dex */
public interface SessionDataChangedListener {
    void onAutoCorrectionEnabled(boolean z);

    void onCandidateSelected(WordCandidate wordCandidate, Candidates candidates, boolean z);

    void onChangeKeyboard(int i, int i2, int i3);

    void onChangeLanguage(int i, String str);

    void onFinishInputSession();

    void onRecapture(char[] cArr);

    void onRecaptureEdit(char[] cArr);

    void onStartInputSession();

    void onTraced(float[] fArr, float[] fArr2);
}
