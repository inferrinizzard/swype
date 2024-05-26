package com.nuance.nmdp.speechkit;

import com.nuance.nmdp.speechkit.oem.prompts.OemVibratePrompt;
import com.nuance.nmdp.speechkit.util.JobRunner;
import com.nuance.nmdp.speechkit.util.audio.IPrompt;

/* loaded from: classes.dex */
public final class Prompt {
    private final IPrompt _prompt;
    private final SpeechKitInternal _speechKit;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Prompt(IPrompt prompt, SpeechKitInternal speechKit) {
        this._prompt = prompt;
        this._speechKit = speechKit;
    }

    public final void release() {
        JobRunner.addJob(new Runnable() { // from class: com.nuance.nmdp.speechkit.Prompt.1
            @Override // java.lang.Runnable
            public void run() {
                if (Prompt.this._speechKit != null) {
                    Prompt.this._speechKit.deletePrompt(Prompt.this);
                }
            }
        });
    }

    public static Prompt vibration(int duration) {
        if (duration <= 0) {
            return null;
        }
        OemVibratePrompt vibrate = new OemVibratePrompt(duration);
        return new Prompt(vibrate, null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final IPrompt getPrompt() {
        return this._prompt;
    }
}
