package com.nuance.swype.input;

import android.text.TextUtils;
import com.nuance.input.swypecorelib.RecaptureInfo;
import com.nuance.input.swypecorelib.XT9CoreInput;

/* loaded from: classes.dex */
public class UndoAcceptHandler {
    String acceptedCandidate;
    private final XT9CoreInput coreInput;
    boolean wasDefaultOverride;

    private UndoAcceptHandler() {
        this(null);
    }

    public UndoAcceptHandler(XT9CoreInput coreInput) {
        this.coreInput = coreInput;
    }

    public void onAcceptCandidate(String acceptedCandidate, String exactCandidate, boolean userExplicitPick) {
        this.acceptedCandidate = acceptedCandidate;
        this.wasDefaultOverride = (userExplicitPick || TextUtils.isEmpty(exactCandidate) || TextUtils.isEmpty(acceptedCandidate) || acceptedCandidate.equals(exactCandidate)) ? false : true;
    }

    public RecaptureInfo undoAccept(String word) {
        if (TextUtils.isEmpty(word)) {
            throw new IllegalArgumentException("recapture word must not empty");
        }
        if (!this.wasDefaultOverride || !word.equals(this.acceptedCandidate)) {
            return RecaptureInfo.EMPTY_RECAPTURE_INFO;
        }
        char[] wordArray = word.toCharArray();
        return this.coreInput.undoAccept(wordArray, wordArray.length);
    }
}
