package com.nuance.nmdp.speechkit;

import com.nuance.nmdp.speechkit.Recognizer;
import com.nuance.nmdp.speechkit.SpeechKit;
import com.nuance.nmdp.speechkit.transaction.TransactionRunner;
import com.nuance.nmdp.speechkit.transaction.recognize.ISignalEnergyListener;
import com.nuance.nmdp.speechkit.util.parsers.IPdxParser;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class TextContextRecognizerProxy extends RecognizerProxyBase<Recognition> implements Recognizer {
    private Recognizer.Listener _listener;
    private final TextContext _textContext;

    public TextContextRecognizerProxy(SpeechKitInternal kit, String type, int detection, SpeechKit.PartialResultsMode partialResultsMode, String language, TextContext textContext, Recognizer.Listener listener, Object callbackHandler) {
        super(kit, type, detection, partialResultsMode, language, null, callbackHandler);
        synchronized (this._listenerSync) {
            this._textContext = textContext;
            this._listener = listener;
        }
        init();
    }

    @Override // com.nuance.nmdp.speechkit.RecognizerProxyBase
    protected final RecognizerBase<Recognition> createRecognizer(TransactionRunner runner, String type, int detectionType, SpeechKit.PartialResultsMode partialResultsMode, String language, String appSessionId, ISignalEnergyListener energyListener) {
        return new TextContextRecognizerImpl(runner, type, detectionType, partialResultsMode, language, this._textContext, energyListener) { // from class: com.nuance.nmdp.speechkit.TextContextRecognizerProxy.1
            @Override // com.nuance.nmdp.speechkit.RecognizerBase
            protected void onError(final SpeechError error) {
                TextContextRecognizerProxy.this.callback(new Runnable() { // from class: com.nuance.nmdp.speechkit.TextContextRecognizerProxy.1.1
                    @Override // java.lang.Runnable
                    public void run() {
                        if (TextContextRecognizerProxy.this._listener != null) {
                            TextContextRecognizerProxy.this._listener.onError((Recognizer) TextContextRecognizerProxy.this, error);
                        }
                    }
                });
            }

            @Override // com.nuance.nmdp.speechkit.RecognizerBase
            protected void onRecordingBegin() {
                TextContextRecognizerProxy.this.callback(new Runnable() { // from class: com.nuance.nmdp.speechkit.TextContextRecognizerProxy.1.2
                    @Override // java.lang.Runnable
                    public void run() {
                        if (TextContextRecognizerProxy.this._listener != null) {
                            TextContextRecognizerProxy.this._listener.onRecordingBegin((Recognizer) TextContextRecognizerProxy.this);
                        }
                    }
                });
            }

            @Override // com.nuance.nmdp.speechkit.RecognizerBase
            protected void onRecordingDone() {
                TextContextRecognizerProxy.this.callback(new Runnable() { // from class: com.nuance.nmdp.speechkit.TextContextRecognizerProxy.1.3
                    @Override // java.lang.Runnable
                    public void run() {
                        if (TextContextRecognizerProxy.this._listener != null) {
                            TextContextRecognizerProxy.this._listener.onRecordingDone((Recognizer) TextContextRecognizerProxy.this);
                        }
                    }
                });
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.nuance.nmdp.speechkit.RecognizerBase
            public void onResults(final Recognition result) {
                TextContextRecognizerProxy.this.callback(new Runnable() { // from class: com.nuance.nmdp.speechkit.TextContextRecognizerProxy.1.4
                    @Override // java.lang.Runnable
                    public void run() {
                        if (TextContextRecognizerProxy.this._listener != null) {
                            TextContextRecognizerProxy.this._listener.onResults((Recognizer) TextContextRecognizerProxy.this, result);
                        }
                    }
                });
            }

            @Override // com.nuance.nmdp.speechkit.RecognizerBase
            protected IPdxParser<Recognition> createResultParser() {
                return new BinaryRecognitionResult().getParser();
            }
        };
    }

    @Override // com.nuance.nmdp.speechkit.Recognizer
    public final void setListener(Recognizer.Listener listener) {
        synchronized (this._listenerSync) {
            this._listener = listener;
        }
    }
}
