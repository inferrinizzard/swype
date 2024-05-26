package com.nuance.nmdp.speechkit;

import com.nuance.connect.common.Strings;
import com.nuance.nmdp.speechkit.CustomWordsSynchronizer;
import com.nuance.nmdp.speechkit.oem.OemCallbackProxyBase;
import com.nuance.nmdp.speechkit.util.JobRunner;
import java.util.Set;

/* loaded from: classes.dex */
public final class CustomWordsSynchronizerProxy extends OemCallbackProxyBase implements CustomWordsSynchronizer {
    private final SpeechKitInternal _kit;
    private CustomWordsSynchronizer.Listener _listener;
    private CustomWordsSynchronizerImpl _synchronizer;

    public CustomWordsSynchronizerProxy(SpeechKitInternal kit, final String dictationType, final String language, CustomWordsSynchronizer.Listener listener, Object callbackHandler) {
        super(callbackHandler);
        this._listener = listener;
        this._kit = kit;
        JobRunner.addJob(new Runnable() { // from class: com.nuance.nmdp.speechkit.CustomWordsSynchronizerProxy.1
            @Override // java.lang.Runnable
            public void run() {
                CustomWordsSynchronizerProxy.this._synchronizer = new CustomWordsSynchronizerImpl(CustomWordsSynchronizerProxy.this._kit.getRunner(), dictationType, language, CustomWordsSynchronizerProxy.this.createCustomWordsSynchronizerListener());
            }
        });
    }

    @Override // com.nuance.nmdp.speechkit.CustomWordsSynchronizer
    public final void addCustomWordsSet(final Set<String> words, final boolean clearAllCustomWords) {
        this._kit.checkForInvalid();
        JobRunner.addJob(new Runnable() { // from class: com.nuance.nmdp.speechkit.CustomWordsSynchronizerProxy.2
            @Override // java.lang.Runnable
            public void run() {
                CustomWordsSynchronizerProxy.this._synchronizer.addCustomWordsSet(words, clearAllCustomWords);
            }
        });
    }

    @Override // com.nuance.nmdp.speechkit.CustomWordsSynchronizer
    public final void removeCustomWordsSet(final Set<String> words) {
        this._kit.checkForInvalid();
        JobRunner.addJob(new Runnable() { // from class: com.nuance.nmdp.speechkit.CustomWordsSynchronizerProxy.3
            @Override // java.lang.Runnable
            public void run() {
                CustomWordsSynchronizerProxy.this._synchronizer.removeCustomWordsSet(words);
            }
        });
    }

    @Override // com.nuance.nmdp.speechkit.CustomWordsSynchronizer
    public final void clearAllCustomWords() {
        this._kit.checkForInvalid();
        JobRunner.addJob(new Runnable() { // from class: com.nuance.nmdp.speechkit.CustomWordsSynchronizerProxy.4
            @Override // java.lang.Runnable
            public void run() {
                CustomWordsSynchronizerProxy.this._synchronizer.clearAllCustomWords();
            }
        });
    }

    @Override // com.nuance.nmdp.speechkit.CustomWordsSynchronizer
    public final void deleteAllUserInformation() {
        this._kit.checkForInvalid();
        JobRunner.addJob(new Runnable() { // from class: com.nuance.nmdp.speechkit.CustomWordsSynchronizerProxy.5
            @Override // java.lang.Runnable
            public void run() {
                CustomWordsSynchronizerProxy.this._synchronizer.deleteAllUserInformation();
            }
        });
    }

    @Override // com.nuance.nmdp.speechkit.CustomWordsSynchronizer
    public final void setLanguage(final String language) {
        this._kit.checkForInvalid();
        SpeechKitInternal.checkStringArgForNullOrEmpty(language, Strings.MESSAGE_BUNDLE_LANGUAGE);
        JobRunner.addJob(new Runnable() { // from class: com.nuance.nmdp.speechkit.CustomWordsSynchronizerProxy.6
            @Override // java.lang.Runnable
            public void run() {
                CustomWordsSynchronizerProxy.this._synchronizer.setLanguage(language);
            }
        });
    }

    @Override // com.nuance.nmdp.speechkit.CustomWordsSynchronizer
    public final void setDictationType(final String dictationType) {
        this._kit.checkForInvalid();
        SpeechKitInternal.checkStringArgForNullOrEmpty(dictationType, "Dictation Type");
        JobRunner.addJob(new Runnable() { // from class: com.nuance.nmdp.speechkit.CustomWordsSynchronizerProxy.7
            @Override // java.lang.Runnable
            public void run() {
                CustomWordsSynchronizerProxy.this._synchronizer.setDictationType(dictationType);
            }
        });
    }

    @Override // com.nuance.nmdp.speechkit.CustomWordsSynchronizer
    public final void cancel() {
        this._kit.checkForInvalid();
        JobRunner.addJob(new Runnable() { // from class: com.nuance.nmdp.speechkit.CustomWordsSynchronizerProxy.8
            @Override // java.lang.Runnable
            public void run() {
                CustomWordsSynchronizerProxy.this._synchronizer.cancel();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public CustomWordsSynchronizer.Listener listener() {
        CustomWordsSynchronizer.Listener listener;
        synchronized (this._listenerSync) {
            listener = this._listener;
        }
        return listener;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public CustomWordsSynchronizer.Listener createCustomWordsSynchronizerListener() {
        return new CustomWordsSynchronizer.Listener() { // from class: com.nuance.nmdp.speechkit.CustomWordsSynchronizerProxy.9
            @Override // com.nuance.nmdp.speechkit.CustomWordsSynchronizer.Listener
            public void onResults(CustomWordsSynchronizer synchronizer, final int actionType, final CustomWordsSynchronizeResult result) {
                CustomWordsSynchronizerProxy.this.callback(new Runnable() { // from class: com.nuance.nmdp.speechkit.CustomWordsSynchronizerProxy.9.1
                    @Override // java.lang.Runnable
                    public void run() {
                        CustomWordsSynchronizerProxy.this.listener().onResults(CustomWordsSynchronizerProxy.this, actionType, result);
                    }
                });
            }

            @Override // com.nuance.nmdp.speechkit.CustomWordsSynchronizer.Listener
            public void onError(CustomWordsSynchronizer synchronizer, final int actionType, final SpeechError error) {
                CustomWordsSynchronizerProxy.this.callback(new Runnable() { // from class: com.nuance.nmdp.speechkit.CustomWordsSynchronizerProxy.9.2
                    @Override // java.lang.Runnable
                    public void run() {
                        CustomWordsSynchronizerProxy.this.listener().onError(CustomWordsSynchronizerProxy.this, actionType, error);
                    }
                });
            }
        };
    }
}
