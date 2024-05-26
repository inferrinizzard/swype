package com.nuance.nmdp.speechkit;

import com.nuance.nmdp.speechkit.SpeechKit;
import com.nuance.nmdp.speechkit.oem.OemCallbackProxyBase;
import com.nuance.nmdp.speechkit.transaction.TransactionRunner;
import com.nuance.nmdp.speechkit.transaction.recognize.ISignalEnergyListener;
import com.nuance.nmdp.speechkit.util.JobRunner;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public abstract class RecognizerProxyBase<ResultType> extends OemCallbackProxyBase implements IRecognizer {
    private String _appSessionId;
    private Runnable _initRunnable;
    private final SpeechKitInternal _kit;
    private float _level;
    private final Object _levelSync;
    private SpeechKit.PartialResultsMode _partialResultsMode;
    private RecognizerBase<ResultType> _recognizer;

    protected abstract RecognizerBase<ResultType> createRecognizer(TransactionRunner transactionRunner, String str, int i, SpeechKit.PartialResultsMode partialResultsMode, String str2, String str3, ISignalEnergyListener iSignalEnergyListener);

    public RecognizerProxyBase(SpeechKitInternal kit, final String type, final int detectionType, SpeechKit.PartialResultsMode partialResultsMode, final String language, String appSessionId, Object callbackHandler) {
        super(callbackHandler);
        this._kit = kit;
        this._level = 0.0f;
        this._levelSync = new Object();
        this._appSessionId = appSessionId;
        this._partialResultsMode = partialResultsMode;
        this._initRunnable = new Runnable() { // from class: com.nuance.nmdp.speechkit.RecognizerProxyBase.1
            @Override // java.lang.Runnable
            public void run() {
                RecognizerProxyBase.this._recognizer = RecognizerProxyBase.this.createRecognizer(RecognizerProxyBase.this._kit.getRunner(), type, detectionType, RecognizerProxyBase.this._partialResultsMode, language, RecognizerProxyBase.this._appSessionId, RecognizerProxyBase.this.createEnergyListener());
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void init() {
        if (this._initRunnable != null) {
            JobRunner.addJob(this._initRunnable);
            this._initRunnable = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public ISignalEnergyListener createEnergyListener() {
        return new ISignalEnergyListener() { // from class: com.nuance.nmdp.speechkit.RecognizerProxyBase.2
            @Override // com.nuance.nmdp.speechkit.transaction.recognize.ISignalEnergyListener
            public void energyUpdate(float level) {
                synchronized (RecognizerProxyBase.this._levelSync) {
                    RecognizerProxyBase.this._level = level;
                }
            }
        };
    }

    @Override // com.nuance.nmdp.speechkit.IRecognizer
    public void setPrompt(final int promptType, final Prompt p) {
        this._kit.checkForInvalid();
        JobRunner.addJob(new Runnable() { // from class: com.nuance.nmdp.speechkit.RecognizerProxyBase.3
            @Override // java.lang.Runnable
            public void run() {
                RecognizerProxyBase.this._recognizer.setPrompt(promptType, p);
            }
        });
    }

    @Override // com.nuance.nmdp.speechkit.IRecognizer
    public void start() {
        this._kit.checkForInvalid();
        JobRunner.addJob(new Runnable() { // from class: com.nuance.nmdp.speechkit.RecognizerProxyBase.4
            @Override // java.lang.Runnable
            public void run() {
                RecognizerProxyBase.this._recognizer.start();
            }
        });
    }

    @Override // com.nuance.nmdp.speechkit.IRecognizer
    public void stopRecording() {
        this._kit.checkForInvalid();
        JobRunner.addJob(new Runnable() { // from class: com.nuance.nmdp.speechkit.RecognizerProxyBase.5
            @Override // java.lang.Runnable
            public void run() {
                RecognizerProxyBase.this._recognizer.stopRecording();
            }
        });
    }

    @Override // com.nuance.nmdp.speechkit.IRecognizer
    public void cancel() {
        this._kit.checkForInvalid();
        JobRunner.addJob(new Runnable() { // from class: com.nuance.nmdp.speechkit.RecognizerProxyBase.6
            @Override // java.lang.Runnable
            public void run() {
                RecognizerProxyBase.this._recognizer.cancel();
            }
        });
    }

    @Override // com.nuance.nmdp.speechkit.IRecognizer
    public float getAudioLevel() {
        float f;
        synchronized (this._levelSync) {
            f = this._level;
        }
        return f;
    }

    @Override // com.nuance.nmdp.speechkit.IRecognizer
    public void setEndOfSpeechDuration(final int duration) {
        this._kit.checkForInvalid();
        JobRunner.addJob(new Runnable() { // from class: com.nuance.nmdp.speechkit.RecognizerProxyBase.7
            @Override // java.lang.Runnable
            public void run() {
                RecognizerProxyBase.this._recognizer.setEndOfSpeechDuration(duration);
            }
        });
    }

    @Override // com.nuance.nmdp.speechkit.IRecognizer
    public void setStartOfSpeechTimeout(final int timeout) {
        this._kit.checkForInvalid();
        JobRunner.addJob(new Runnable() { // from class: com.nuance.nmdp.speechkit.RecognizerProxyBase.8
            @Override // java.lang.Runnable
            public void run() {
                RecognizerProxyBase.this._recognizer.setStartOfSpeechTimeout(timeout);
            }
        });
    }
}
