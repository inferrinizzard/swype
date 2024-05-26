package com.nuance.nmdp.speechkit;

import com.nuance.nmdp.speechkit.RecognizerConstants;
import com.nuance.nmdp.speechkit.SpeechKit;
import com.nuance.nmdp.speechkit.TextRecognizer;
import com.nuance.nmdp.speechkit.transaction.TransactionRunner;
import com.nuance.nmdp.speechkit.transaction.generic.IGenericParam;
import com.nuance.nmdp.speechkit.transaction.recognize.ISignalEnergyListener;
import com.nuance.nmdp.speechkit.util.Logger;
import com.nuance.nmdp.speechkit.util.pdx.PdxValue;
import java.util.List;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class TextRecognizerProxy extends RecognizerProxyBase<GenericRecognition> implements TextRecognizer {
    private TextRecognizer.Listener _listener;
    List<IGenericParam> _optionalParams;
    private final PdxValue.Dictionary _requestDict;

    public TextRecognizerProxy(SpeechKitInternal kit, String language, String appSessionId, PdxValue.Dictionary requestParams, List<IGenericParam> grammarParams, TextRecognizer.Listener listener, Object callbackHandler) {
        super(kit, RecognizerConstants.RecognizerType.Search, -1, SpeechKit.PartialResultsMode.NO_PARTIAL_RESULTS, language, appSessionId, callbackHandler);
        synchronized (this._listenerSync) {
            this._listener = listener;
        }
        this._requestDict = requestParams;
        this._optionalParams = grammarParams;
        init();
    }

    @Override // com.nuance.nmdp.speechkit.TextRecognizer
    public final void setListener(TextRecognizer.Listener listener) {
        SpeechKitInternal.checkArgForNull(listener, "listener");
        synchronized (this._listenerSync) {
            this._listener = listener;
        }
    }

    @Override // com.nuance.nmdp.speechkit.RecognizerProxyBase
    protected final RecognizerBase<GenericRecognition> createRecognizer(TransactionRunner runner, String type, int detectionType, SpeechKit.PartialResultsMode partialResultsMode, String language, String appSessionId, ISignalEnergyListener energyListener) {
        return new TextRecognizerImpl(runner, language, appSessionId, this._requestDict, this._optionalParams, energyListener) { // from class: com.nuance.nmdp.speechkit.TextRecognizerProxy.1
            @Override // com.nuance.nmdp.speechkit.RecognizerBase
            protected void onError(final SpeechError error) {
                TextRecognizerProxy.this.callback(new Runnable() { // from class: com.nuance.nmdp.speechkit.TextRecognizerProxy.1.1
                    @Override // java.lang.Runnable
                    public void run() {
                        if (TextRecognizerProxy.this._listener != null) {
                            TextRecognizerProxy.this._listener.onError(TextRecognizerProxy.this, error);
                        }
                    }
                });
            }

            @Override // com.nuance.nmdp.speechkit.RecognizerBase
            protected void onRecordingBegin() {
                Logger.info(TextRecognizerProxy.this, "onRecordingRegin is called for TextRecognizer for nothing.");
            }

            @Override // com.nuance.nmdp.speechkit.RecognizerBase
            protected void onRecordingDone() {
                Logger.info(TextRecognizerProxy.this, "onRecordingDone is called for TextRecognizer for nothing.");
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.nuance.nmdp.speechkit.RecognizerBase
            public void onResults(final GenericRecognition result) {
                TextRecognizerProxy.this.callback(new Runnable() { // from class: com.nuance.nmdp.speechkit.TextRecognizerProxy.1.2
                    @Override // java.lang.Runnable
                    public void run() {
                        if (TextRecognizerProxy.this._listener != null) {
                            TextRecognizerProxy.this._listener.onResults(TextRecognizerProxy.this, result);
                        }
                    }
                });
            }
        };
    }
}
