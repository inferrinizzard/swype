package com.nuance.swype.input;

import android.view.inputmethod.InputConnection;
import com.nuance.input.swypecorelib.XT9CoreAlphaInput;
import com.nuance.swype.input.AlphaInputView;
import com.nuance.swype.util.LogManager;

/* loaded from: classes.dex */
public class RecaptureEditWord implements XT9CoreAlphaInput.WordRecaptureCallback, AlphaInputView.UpdateSelectionCallback {
    protected static final LogManager.Log log = LogManager.getLog("RecaptureEditWord");
    private StringBuilder recaptureWord = new StringBuilder();

    private void set(char[] buffer) {
        this.recaptureWord.setLength(0);
        if (buffer != null && buffer.length > 0) {
            this.recaptureWord.append(buffer);
        }
        log.d("recaptureWord = ", this.recaptureWord);
    }

    public String getWord() {
        return this.recaptureWord.toString();
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreAlphaInput.WordRecaptureCallback
    public void recapture(char[] buffer) {
        if (this.recaptureWord.length() == 0) {
            set(buffer);
        }
    }

    public void clear() {
        set(null);
    }

    @Override // com.nuance.swype.input.AlphaInputView.UpdateSelectionCallback
    public void updateSelection(boolean composingWordCandidates, int selectionStart, int selectionEnd, InputConnection ic) {
    }
}
