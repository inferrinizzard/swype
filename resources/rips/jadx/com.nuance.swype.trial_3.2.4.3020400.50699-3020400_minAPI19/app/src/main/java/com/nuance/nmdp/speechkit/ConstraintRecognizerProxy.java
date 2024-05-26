package com.nuance.nmdp.speechkit;

import com.nuance.nmdp.speechkit.Recognizer;
import com.nuance.nmdp.speechkit.SpeechKit;
import com.nuance.nmdp.speechkit.transaction.TransactionRunner;
import com.nuance.nmdp.speechkit.transaction.recognize.ISignalEnergyListener;
import com.nuance.nmdp.speechkit.util.pdx.PdxValue;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class ConstraintRecognizerProxy extends RecognizerProxyBase<Recognition> implements Recognizer {
    private final PdxValue.Sequence _grammar_list;
    private Recognizer.Listener _listener;

    public ConstraintRecognizerProxy(SpeechKitInternal kit, String type, int detection, SpeechKit.PartialResultsMode partialResultsMode, String language, PdxValue.Sequence grammarList, Recognizer.Listener listener, Object callbackHandler) {
        super(kit, type, detection, partialResultsMode, language, null, callbackHandler);
        synchronized (this._listenerSync) {
            this._listener = listener;
        }
        this._grammar_list = grammarList;
        init();
    }

    @Override // com.nuance.nmdp.speechkit.Recognizer
    public final void setListener(Recognizer.Listener listener) {
        SpeechKitInternal.checkArgForNull(listener, "listener");
        synchronized (this._listenerSync) {
            this._listener = listener;
        }
    }

    @Override // com.nuance.nmdp.speechkit.RecognizerProxyBase
    protected final RecognizerBase<Recognition> createRecognizer(TransactionRunner runner, String type, int detection, SpeechKit.PartialResultsMode partialResultsMode, String language, String appSessionId, ISignalEnergyListener energyListener) {
        return new ConstraintRecognizerImpl(runner, type, detection, partialResultsMode, 0, language, this._grammar_list, energyListener) { // from class: com.nuance.nmdp.speechkit.ConstraintRecognizerProxy.1
            @Override // com.nuance.nmdp.speechkit.RecognizerBase
            protected void onError(final SpeechError error) {
                ConstraintRecognizerProxy.this.callback(new Runnable() { // from class: com.nuance.nmdp.speechkit.ConstraintRecognizerProxy.1.1
                    @Override // java.lang.Runnable
                    public void run() {
                        if (ConstraintRecognizerProxy.this._listener != null) {
                            ConstraintRecognizerProxy.this._listener.onError((Recognizer) ConstraintRecognizerProxy.this, error);
                        }
                    }
                });
            }

            @Override // com.nuance.nmdp.speechkit.RecognizerBase
            protected void onRecordingBegin() {
                ConstraintRecognizerProxy.this.callback(new Runnable() { // from class: com.nuance.nmdp.speechkit.ConstraintRecognizerProxy.1.2
                    @Override // java.lang.Runnable
                    public void run() {
                        if (ConstraintRecognizerProxy.this._listener != null) {
                            ConstraintRecognizerProxy.this._listener.onRecordingBegin((Recognizer) ConstraintRecognizerProxy.this);
                        }
                    }
                });
            }

            @Override // com.nuance.nmdp.speechkit.RecognizerBase
            protected void onRecordingDone() {
                ConstraintRecognizerProxy.this.callback(new Runnable() { // from class: com.nuance.nmdp.speechkit.ConstraintRecognizerProxy.1.3
                    @Override // java.lang.Runnable
                    public void run() {
                        if (ConstraintRecognizerProxy.this._listener != null) {
                            ConstraintRecognizerProxy.this._listener.onRecordingDone((Recognizer) ConstraintRecognizerProxy.this);
                        }
                    }
                });
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.nuance.nmdp.speechkit.RecognizerBase
            public void onResults(final Recognition result) {
                ConstraintRecognizerProxy.this.callback(new Runnable() { // from class: com.nuance.nmdp.speechkit.ConstraintRecognizerProxy.1.4
                    @Override // java.lang.Runnable
                    public void run() {
                        if (ConstraintRecognizerProxy.this._listener != null) {
                            ConstraintRecognizerProxy.this._listener.onResults((Recognizer) ConstraintRecognizerProxy.this, result);
                        }
                    }
                });
            }
        };
    }
}
