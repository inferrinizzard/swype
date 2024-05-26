package com.nuance.nmdp.speechkit;

import com.nuance.nmdp.speechkit.NluRecognizer;
import com.nuance.nmdp.speechkit.SpeechKit;
import com.nuance.nmdp.speechkit.transaction.TransactionRunner;
import com.nuance.nmdp.speechkit.transaction.recognize.ISignalEnergyListener;
import com.nuance.nmdp.speechkit.util.pdx.PdxValue;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class NluRecognizerProxy extends RecognizerProxyBase<GenericRecognition> implements NluRecognizer {
    private NluRecognizer.Listener _listener;
    private final PdxValue.Dictionary _requestDict;

    public NluRecognizerProxy(SpeechKitInternal kit, String type, int detection, String language, String appSessionId, PdxValue.Dictionary requestParams, NluRecognizer.Listener listener, Object callbackHandler) {
        super(kit, type, detection, SpeechKit.PartialResultsMode.NO_PARTIAL_RESULTS, language, appSessionId, callbackHandler);
        synchronized (this._listenerSync) {
            this._listener = listener;
        }
        this._requestDict = requestParams;
        init();
    }

    @Override // com.nuance.nmdp.speechkit.NluRecognizer
    public final void setListener(NluRecognizer.Listener listener) {
        SpeechKitInternal.checkArgForNull(listener, "listener");
        synchronized (this._listenerSync) {
            this._listener = listener;
        }
    }

    @Override // com.nuance.nmdp.speechkit.RecognizerProxyBase
    protected final RecognizerBase<GenericRecognition> createRecognizer(TransactionRunner runner, String type, int detectionType, SpeechKit.PartialResultsMode partialResultsMode, String language, String appSessionId, ISignalEnergyListener energyListener) {
        return new NluRecognizerImpl(runner, type, detectionType, language, appSessionId, this._requestDict, energyListener) { // from class: com.nuance.nmdp.speechkit.NluRecognizerProxy.1
            @Override // com.nuance.nmdp.speechkit.RecognizerBase
            protected void onError(final SpeechError error) {
                NluRecognizerProxy.this.callback(new Runnable() { // from class: com.nuance.nmdp.speechkit.NluRecognizerProxy.1.1
                    @Override // java.lang.Runnable
                    public void run() {
                        if (NluRecognizerProxy.this._listener != null) {
                            NluRecognizerProxy.this._listener.onError((NluRecognizer) NluRecognizerProxy.this, error);
                        }
                    }
                });
            }

            @Override // com.nuance.nmdp.speechkit.RecognizerBase
            protected void onRecordingBegin() {
                NluRecognizerProxy.this.callback(new Runnable() { // from class: com.nuance.nmdp.speechkit.NluRecognizerProxy.1.2
                    @Override // java.lang.Runnable
                    public void run() {
                        if (NluRecognizerProxy.this._listener != null) {
                            NluRecognizerProxy.this._listener.onRecordingBegin((NluRecognizer) NluRecognizerProxy.this);
                        }
                    }
                });
            }

            @Override // com.nuance.nmdp.speechkit.RecognizerBase
            protected void onRecordingDone() {
                NluRecognizerProxy.this.callback(new Runnable() { // from class: com.nuance.nmdp.speechkit.NluRecognizerProxy.1.3
                    @Override // java.lang.Runnable
                    public void run() {
                        if (NluRecognizerProxy.this._listener != null) {
                            NluRecognizerProxy.this._listener.onRecordingDone((NluRecognizer) NluRecognizerProxy.this);
                        }
                    }
                });
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.nuance.nmdp.speechkit.RecognizerBase
            public void onResults(final GenericRecognition result) {
                NluRecognizerProxy.this.callback(new Runnable() { // from class: com.nuance.nmdp.speechkit.NluRecognizerProxy.1.4
                    @Override // java.lang.Runnable
                    public void run() {
                        if (NluRecognizerProxy.this._listener != null) {
                            NluRecognizerProxy.this._listener.onResults((NluRecognizer) NluRecognizerProxy.this, result);
                        }
                    }
                });
            }
        };
    }
}
