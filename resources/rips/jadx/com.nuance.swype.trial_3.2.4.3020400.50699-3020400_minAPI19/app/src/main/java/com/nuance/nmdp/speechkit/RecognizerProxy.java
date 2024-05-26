package com.nuance.nmdp.speechkit;

import com.nuance.nmdp.speechkit.Recognizer;
import com.nuance.nmdp.speechkit.SpeechKit;
import com.nuance.nmdp.speechkit.transaction.TransactionRunner;
import com.nuance.nmdp.speechkit.transaction.recognize.ISignalEnergyListener;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class RecognizerProxy extends RecognizerProxyBase<Recognition> implements Recognizer {
    private Recognizer.Listener _listener;

    public RecognizerProxy(SpeechKitInternal kit, String type, int detection, SpeechKit.PartialResultsMode partialResultsMode, String language, Recognizer.Listener listener, Object callbackHandler) {
        super(kit, type, detection, partialResultsMode, language, null, callbackHandler);
        synchronized (this._listenerSync) {
            this._listener = listener;
        }
        init();
    }

    @Override // com.nuance.nmdp.speechkit.RecognizerProxyBase
    protected final RecognizerBase<Recognition> createRecognizer(TransactionRunner runner, String type, int detectionType, SpeechKit.PartialResultsMode partialResultsMode, String language, String appSessionId, ISignalEnergyListener energyListener) {
        return new RecognizerImpl(runner, type, detectionType, partialResultsMode, 0, language, energyListener) { // from class: com.nuance.nmdp.speechkit.RecognizerProxy.1
            @Override // com.nuance.nmdp.speechkit.RecognizerBase
            protected void onError(final SpeechError error) {
                RecognizerProxy.this.callback(new Runnable() { // from class: com.nuance.nmdp.speechkit.RecognizerProxy.1.1
                    @Override // java.lang.Runnable
                    public void run() {
                        if (RecognizerProxy.this._listener != null) {
                            RecognizerProxy.this._listener.onError((Recognizer) RecognizerProxy.this, error);
                        }
                    }
                });
            }

            @Override // com.nuance.nmdp.speechkit.RecognizerBase
            protected void onRecordingBegin() {
                RecognizerProxy.this.callback(new Runnable() { // from class: com.nuance.nmdp.speechkit.RecognizerProxy.1.2
                    @Override // java.lang.Runnable
                    public void run() {
                        if (RecognizerProxy.this._listener != null) {
                            RecognizerProxy.this._listener.onRecordingBegin((Recognizer) RecognizerProxy.this);
                        }
                    }
                });
            }

            @Override // com.nuance.nmdp.speechkit.RecognizerBase
            protected void onRecordingDone() {
                RecognizerProxy.this.callback(new Runnable() { // from class: com.nuance.nmdp.speechkit.RecognizerProxy.1.3
                    @Override // java.lang.Runnable
                    public void run() {
                        if (RecognizerProxy.this._listener != null) {
                            RecognizerProxy.this._listener.onRecordingDone((Recognizer) RecognizerProxy.this);
                        }
                    }
                });
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.nuance.nmdp.speechkit.RecognizerBase
            public void onResults(final Recognition result) {
                RecognizerProxy.this.callback(new Runnable() { // from class: com.nuance.nmdp.speechkit.RecognizerProxy.1.4
                    @Override // java.lang.Runnable
                    public void run() {
                        if (RecognizerProxy.this._listener != null) {
                            RecognizerProxy.this._listener.onResults((Recognizer) RecognizerProxy.this, result);
                        }
                    }
                });
            }
        };
    }

    @Override // com.nuance.nmdp.speechkit.Recognizer
    public final void setListener(Recognizer.Listener listener) {
        SpeechKitInternal.checkArgForNull(listener, "listener");
        synchronized (this._listenerSync) {
            this._listener = listener;
        }
    }
}
