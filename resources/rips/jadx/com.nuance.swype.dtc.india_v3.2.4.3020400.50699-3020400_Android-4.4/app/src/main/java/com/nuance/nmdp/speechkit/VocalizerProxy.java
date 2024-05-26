package com.nuance.nmdp.speechkit;

import com.nuance.connect.common.Strings;
import com.nuance.nmdp.speechkit.Vocalizer;
import com.nuance.nmdp.speechkit.oem.OemCallbackProxyBase;
import com.nuance.nmdp.speechkit.util.JobRunner;
import com.nuance.swype.input.InputFieldInfo;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class VocalizerProxy extends OemCallbackProxyBase implements Vocalizer {
    private final SpeechKitInternal _kit;
    private Vocalizer.Listener _listener;
    private VocalizerImpl _vocalizer;

    public VocalizerProxy(SpeechKitInternal kit, final String voice, final String language, Vocalizer.Listener listener, Object callbackHandler) {
        super(callbackHandler);
        this._listener = listener;
        this._kit = kit;
        JobRunner.addJob(new Runnable() { // from class: com.nuance.nmdp.speechkit.VocalizerProxy.1
            @Override // java.lang.Runnable
            public void run() {
                VocalizerProxy.this._vocalizer = new VocalizerImpl(VocalizerProxy.this._kit.getRunner(), voice, language, VocalizerProxy.this.createVocalizerListener());
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Vocalizer.Listener listener() {
        Vocalizer.Listener listener;
        synchronized (this._listenerSync) {
            listener = this._listener;
        }
        return listener;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Vocalizer.Listener createVocalizerListener() {
        return new Vocalizer.Listener() { // from class: com.nuance.nmdp.speechkit.VocalizerProxy.2
            @Override // com.nuance.nmdp.speechkit.Vocalizer.Listener
            public void onSpeakingBegin(Vocalizer vocalizer, final String text, final Object context) {
                VocalizerProxy.this.callback(new Runnable() { // from class: com.nuance.nmdp.speechkit.VocalizerProxy.2.1
                    @Override // java.lang.Runnable
                    public void run() {
                        VocalizerProxy.this.listener().onSpeakingBegin(VocalizerProxy.this, text, context);
                    }
                });
            }

            @Override // com.nuance.nmdp.speechkit.Vocalizer.Listener
            public void onSpeakingDone(Vocalizer vocalizer, final String text, final SpeechError error, final Object context) {
                VocalizerProxy.this.callback(new Runnable() { // from class: com.nuance.nmdp.speechkit.VocalizerProxy.2.2
                    @Override // java.lang.Runnable
                    public void run() {
                        VocalizerProxy.this.listener().onSpeakingDone(VocalizerProxy.this, text, error, context);
                    }
                });
            }
        };
    }

    @Override // com.nuance.nmdp.speechkit.Vocalizer
    public final void cancel() {
        this._kit.checkForInvalid();
        JobRunner.addJob(new Runnable() { // from class: com.nuance.nmdp.speechkit.VocalizerProxy.3
            @Override // java.lang.Runnable
            public void run() {
                VocalizerProxy.this._vocalizer.cancel();
            }
        });
    }

    @Override // com.nuance.nmdp.speechkit.Vocalizer
    public final void speakMarkupString(final String text, final Object context) {
        this._kit.checkForInvalid();
        SpeechKitInternal.checkArgForNull(text, InputFieldInfo.INPUT_TYPE_TEXT);
        JobRunner.addJob(new Runnable() { // from class: com.nuance.nmdp.speechkit.VocalizerProxy.4
            @Override // java.lang.Runnable
            public void run() {
                VocalizerProxy.this._vocalizer.speakMarkupString(text, context);
            }
        });
    }

    @Override // com.nuance.nmdp.speechkit.Vocalizer
    public final void speakString(final String text, final Object context) {
        this._kit.checkForInvalid();
        SpeechKitInternal.checkArgForNull(text, InputFieldInfo.INPUT_TYPE_TEXT);
        JobRunner.addJob(new Runnable() { // from class: com.nuance.nmdp.speechkit.VocalizerProxy.5
            @Override // java.lang.Runnable
            public void run() {
                VocalizerProxy.this._vocalizer.speakString(text, context);
            }
        });
    }

    @Override // com.nuance.nmdp.speechkit.Vocalizer
    public final void setLanguage(final String language) {
        this._kit.checkForInvalid();
        SpeechKitInternal.checkStringArgForNullOrEmpty(language, Strings.MESSAGE_BUNDLE_LANGUAGE);
        JobRunner.addJob(new Runnable() { // from class: com.nuance.nmdp.speechkit.VocalizerProxy.6
            @Override // java.lang.Runnable
            public void run() {
                VocalizerProxy.this._vocalizer.setLanguage(language);
            }
        });
    }

    @Override // com.nuance.nmdp.speechkit.Vocalizer
    public final void setVoice(final String voice) {
        this._kit.checkForInvalid();
        SpeechKitInternal.checkStringArgForNullOrEmpty(voice, "voice");
        JobRunner.addJob(new Runnable() { // from class: com.nuance.nmdp.speechkit.VocalizerProxy.7
            @Override // java.lang.Runnable
            public void run() {
                VocalizerProxy.this._vocalizer.setVoice(voice);
            }
        });
    }

    @Override // com.nuance.nmdp.speechkit.Vocalizer
    public final void setListener(Vocalizer.Listener listener) {
        SpeechKitInternal.checkArgForNull(listener, "listener");
        synchronized (this._listenerSync) {
            this._listener = listener;
        }
    }
}
