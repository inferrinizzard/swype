package com.nuance.swype.input.keyboard;

import com.nuance.swype.input.RecaptureHandler;

/* loaded from: classes.dex */
public class WordRecaptureWrapper {
    public boolean reselect(int keyCode, RecaptureHandler recaptureHandler) {
        if (recaptureHandler != null) {
            return recaptureHandler.onKey(keyCode, 0);
        }
        return false;
    }

    public boolean onCharKey(char ch, RecaptureHandler recaptureHandler) {
        if (recaptureHandler != null) {
            return recaptureHandler.onCharKey(ch);
        }
        return false;
    }

    public void onText(String text, RecaptureHandler recaptureHandler) {
        if (recaptureHandler != null) {
            recaptureHandler.onText(text);
        }
    }

    public void removePendingRecaptureMessage(RecaptureHandler recaptureHandler) {
        if (recaptureHandler != null) {
            recaptureHandler.removePendingRecaptureMessage();
        }
    }

    public boolean hasPendingRecaptureMessage(RecaptureHandler recaptureHandler) {
        if (recaptureHandler != null) {
            recaptureHandler.hasPendingRecaptureMessage();
            return false;
        }
        return false;
    }
}
