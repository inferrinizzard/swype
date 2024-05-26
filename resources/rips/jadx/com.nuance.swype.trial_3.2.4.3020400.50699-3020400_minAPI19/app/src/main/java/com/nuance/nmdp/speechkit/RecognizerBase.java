package com.nuance.nmdp.speechkit;

import com.nuance.nmdp.speechkit.SpeechKit;
import com.nuance.nmdp.speechkit.transaction.ITransaction;
import com.nuance.nmdp.speechkit.transaction.TransactionRunner;
import com.nuance.nmdp.speechkit.transaction.recognize.IRecognizeTransaction;
import com.nuance.nmdp.speechkit.transaction.recognize.IRecognizeTransactionListener;
import com.nuance.nmdp.speechkit.transaction.recognize.ISignalEnergyListener;
import com.nuance.nmdp.speechkit.util.Logger;
import com.nuance.nmdp.speechkit.util.audio.IPrompt;
import com.nuance.nmdp.speechkit.util.parsers.IPdxParser;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public abstract class RecognizerBase<ResultType> {
    private String _appSessionId;
    private IRecognizeTransaction _currentTransaction;
    private final int _detectionType;
    private int _endOfSpeechDuration;
    private final ISignalEnergyListener _energyListener;
    private Prompt _errorPrompt;
    private final String _language;
    private final SpeechKit.PartialResultsMode _partialResultsMode;
    private IPdxParser<ResultType> _resultParser;
    private Prompt _resultPrompt;
    private Prompt _startPrompt;
    private Prompt _stopPrompt;
    private final TransactionRunner _tr;
    private int _startOfSpeechTimeout = 0;
    private final IRecognizeTransactionListener _transactionListener = createTransactionListener();
    private boolean _started = false;

    protected abstract IPdxParser<ResultType> createResultParser();

    protected abstract IRecognizeTransaction createTransaction(TransactionRunner transactionRunner, int i, int i2, int i3, SpeechKit.PartialResultsMode partialResultsMode, String str, String str2, ISignalEnergyListener iSignalEnergyListener, IPrompt iPrompt, IPrompt iPrompt2, IPrompt iPrompt3, IPrompt iPrompt4, IPdxParser<ResultType> iPdxParser, IRecognizeTransactionListener iRecognizeTransactionListener);

    protected abstract void onError(SpeechError speechError);

    protected abstract void onRecordingBegin();

    protected abstract void onRecordingDone();

    protected abstract void onResults(ResultType resulttype);

    /* JADX INFO: Access modifiers changed from: package-private */
    public RecognizerBase(TransactionRunner transactionRunner, int detectionType, int endOfSpeechDuration, SpeechKit.PartialResultsMode partialResultsMode, String language, String appSessionId, ISignalEnergyListener energyListener) {
        this._tr = transactionRunner;
        this._detectionType = detectionType;
        this._endOfSpeechDuration = endOfSpeechDuration;
        this._partialResultsMode = partialResultsMode;
        this._language = language;
        this._energyListener = energyListener;
        this._appSessionId = appSessionId;
    }

    private IRecognizeTransactionListener createTransactionListener() {
        return new IRecognizeTransactionListener() { // from class: com.nuance.nmdp.speechkit.RecognizerBase.1
            @Override // com.nuance.nmdp.speechkit.transaction.ITransactionListener
            public void transactionDone(ITransaction t) {
                if (RecognizerBase.this._currentTransaction == t) {
                    RecognizerBase.this._currentTransaction = null;
                }
            }

            @Override // com.nuance.nmdp.speechkit.transaction.ITransactionListener
            public void error(ITransaction t, int code, String error, String suggestion) {
                if (RecognizerBase.this._currentTransaction == t) {
                    RecognizerBase.this.onError(new SpeechErrorImpl(code, error, suggestion));
                }
            }

            @Override // com.nuance.nmdp.speechkit.transaction.recognize.IRecognizeTransactionListener
            public void recordingStarted(ITransaction t) {
                if (RecognizerBase.this._currentTransaction == t) {
                    RecognizerBase.this.onRecordingBegin();
                }
            }

            @Override // com.nuance.nmdp.speechkit.transaction.recognize.IRecognizeTransactionListener
            public void recordingStopped(ITransaction t) {
                if (RecognizerBase.this._currentTransaction == t) {
                    RecognizerBase.this.onRecordingDone();
                }
            }

            @Override // com.nuance.nmdp.speechkit.transaction.recognize.IRecognizeTransactionListener
            public void results(ITransaction t) {
                if (RecognizerBase.this._currentTransaction == t) {
                    RecognizerBase.this.onResults(RecognizerBase.this._resultParser.getParsed());
                }
            }
        };
    }

    public void setPrompt(int promptType, Prompt p) {
        switch (promptType) {
            case 0:
                this._startPrompt = p;
                return;
            case 1:
                this._stopPrompt = p;
                return;
            case 2:
                this._resultPrompt = p;
                return;
            case 3:
                this._errorPrompt = p;
                return;
            default:
                return;
        }
    }

    public void start() {
        if (this._tr.isValid()) {
            if (!this._started) {
                IPrompt startPrompt = this._startPrompt == null ? null : this._startPrompt.getPrompt();
                IPrompt stopPrompt = this._stopPrompt == null ? null : this._stopPrompt.getPrompt();
                IPrompt resultPrompt = this._resultPrompt == null ? null : this._resultPrompt.getPrompt();
                IPrompt errorPrompt = this._errorPrompt == null ? null : this._errorPrompt.getPrompt();
                this._resultParser = createResultParser();
                this._currentTransaction = createTransaction(this._tr, this._detectionType, this._endOfSpeechDuration, this._startOfSpeechTimeout, this._partialResultsMode, this._language, this._appSessionId, this._energyListener, startPrompt, stopPrompt, resultPrompt, errorPrompt, this._resultParser, this._transactionListener);
                if (this._currentTransaction == null) {
                    Logger.error(this, "Unable to create recognition transaction");
                    onError(new SpeechErrorImpl(0, null, null));
                    return;
                } else {
                    this._started = true;
                    this._currentTransaction.start();
                    return;
                }
            }
            return;
        }
        Logger.error(this, "Unable to create recognition transaction. Transaction runner is invalid.");
        onError(new SpeechErrorImpl(0, null, null));
    }

    public void stopRecording() {
        if (this._currentTransaction != null) {
            this._currentTransaction.stop();
        }
    }

    public void cancel() {
        if (this._currentTransaction != null) {
            this._currentTransaction.cancel();
            this._currentTransaction = null;
        }
    }

    public void setEndOfSpeechDuration(int duration) {
        this._endOfSpeechDuration = duration;
    }

    public void setStartOfSpeechTimeout(int timeout) {
        this._startOfSpeechTimeout = timeout;
    }
}
