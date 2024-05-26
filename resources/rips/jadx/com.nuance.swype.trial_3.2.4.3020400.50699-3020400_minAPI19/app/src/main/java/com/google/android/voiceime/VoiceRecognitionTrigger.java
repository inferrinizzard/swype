package com.google.android.voiceime;

import android.content.Intent;
import android.inputmethodservice.InputMethodService;
import android.os.Build;
import android.view.inputmethod.InputMethodInfo;

/* loaded from: classes.dex */
public final class VoiceRecognitionTrigger {
    private ImeTrigger mImeTrigger;
    private final InputMethodService mInputMethodService;
    private IntentApiTrigger mIntentApiTrigger;
    public Trigger mTrigger = getTrigger();

    public VoiceRecognitionTrigger(InputMethodService inputMethodService) {
        this.mInputMethodService = inputMethodService;
    }

    public final Trigger getTrigger() {
        InputMethodInfo voiceImeInputMethodInfo;
        if (!(Build.VERSION.SDK_INT >= 19 && (voiceImeInputMethodInfo = ImeTrigger.getVoiceImeInputMethodInfo(ImeTrigger.getInputMethodManager(this.mInputMethodService))) != null && voiceImeInputMethodInfo.getSubtypeCount() > 0)) {
            if (!(this.mInputMethodService.getPackageManager().queryIntentActivities(new Intent("android.speech.action.RECOGNIZE_SPEECH"), 0).size() > 0)) {
                return null;
            }
            if (this.mIntentApiTrigger == null) {
                this.mIntentApiTrigger = new IntentApiTrigger(this.mInputMethodService);
            }
            return this.mIntentApiTrigger;
        }
        if (this.mImeTrigger == null) {
            this.mImeTrigger = new ImeTrigger(this.mInputMethodService);
        }
        return this.mImeTrigger;
    }
}
