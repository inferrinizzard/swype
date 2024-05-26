package com.nuance.nmdp.speechkit.transaction.recognize;

import com.nuance.nmdp.speechkit.SpeechKit;
import com.nuance.nmdp.speechkit.transaction.TransactionBase;
import com.nuance.nmdp.speechkit.transaction.TransactionConfig;
import com.nuance.nmdp.speechkit.transaction.TransactionException;
import com.nuance.nmdp.speechkit.util.JobRunner;
import com.nuance.nmdp.speechkit.util.Logger;
import com.nuance.nmdp.speechkit.util.audio.IPlayerHelperListener;
import com.nuance.nmdp.speechkit.util.audio.IPrompt;
import com.nuance.nmdp.speechkit.util.audio.IRecorderHelperListener;
import com.nuance.nmdp.speechkit.util.audio.PromptHelper;
import com.nuance.nmdp.speechkit.util.audio.RecorderHelper;
import com.nuance.nmdp.speechkit.util.parsers.IPdxParser;
import com.nuance.nmsp.client.sdk.components.general.Parameter;
import com.nuance.nmsp.client.sdk.components.resource.common.Manager;
import com.nuance.nmsp.client.sdk.components.resource.nmas.AudioParam;
import java.util.List;

/* loaded from: classes.dex */
public abstract class RecognizeTransaction extends TransactionBase implements IRecognizeTransaction {
    private final int _detectDelay;
    private final int _detectionType;
    private final int _endOfSpeechDuration;
    private final ISignalEnergyListener _energyListener;
    private final PromptHelper _errorPromptHelper;
    public final SpeechKit.PartialResultsMode _partialResultsMode;
    private final IPlayerHelperListener _promptListener;
    private RecorderHelper _recorderHelper;
    private final IRecorderHelperListener _recorderListener;
    private final List<Parameter> _recorderParams;
    private final IPdxParser<?> _resultParser;
    private final PromptHelper _resultPromptHelper;
    private final int _startOfSpeechTimeout;
    private final PromptHelper _startPromptHelper;
    private final PromptHelper _stopPromptHelper;

    public abstract String getAudioParamName();

    public abstract String getCommandName();

    public abstract void sendParams() throws TransactionException;

    public abstract boolean supportsRecording();

    public RecognizeTransaction(Manager mgr, TransactionConfig config, int detectionType, int endOfSpeechDuration, int startOfSpeechTimeout, SpeechKit.PartialResultsMode partialResultsMode, String language, IPrompt startPrompt, IPrompt stopPrompt, IPrompt resultPrompt, IPrompt errorPrompt, IPdxParser<?> resultParser, List<Parameter> recorderParams, ISignalEnergyListener energyListener, IRecognizeTransactionListener listener) {
        super(mgr, config, language, listener);
        this._recorderParams = recorderParams;
        this._resultParser = resultParser;
        this._energyListener = energyListener;
        this._recorderListener = createRecorderListener();
        this._promptListener = createPromptListener();
        this._recorderHelper = null;
        this._detectionType = detectionType;
        this._detectDelay = 0;
        this._startPromptHelper = startPrompt == null ? null : new PromptHelper(startPrompt, config.context(), this, this._promptListener);
        this._stopPromptHelper = stopPrompt == null ? null : new PromptHelper(stopPrompt, config.context(), this, this._promptListener);
        this._resultPromptHelper = resultPrompt == null ? null : new PromptHelper(resultPrompt, config.context(), this, this._promptListener);
        this._errorPromptHelper = errorPrompt == null ? null : new PromptHelper(errorPrompt, config.context(), this, this._promptListener);
        this._currentState = new IdleState(this);
        this._partialResultsMode = partialResultsMode;
        this._endOfSpeechDuration = endOfSpeechDuration;
        this._startOfSpeechTimeout = startOfSpeechTimeout;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final IPdxParser<?> getResultParser() {
        return this._resultParser;
    }

    @Override // com.nuance.nmdp.speechkit.transaction.recognize.IRecognizeTransaction
    public void stop() {
        currentState().stop();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean playStartPrompt() {
        if (this._startPromptHelper == null) {
            return false;
        }
        this._startPromptHelper.start();
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void stopStartPrompt() {
        if (this._startPromptHelper != null) {
            this._startPromptHelper.stop();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void playStopPrompt() {
        if (this._stopPromptHelper != null) {
            this._stopPromptHelper.start();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void playResultPrompt() {
        if (this._resultPromptHelper != null) {
            this._resultPromptHelper.start();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void playErrorPrompt() {
        if (this._errorPromptHelper != null) {
            this._errorPromptHelper.start();
        }
    }

    public void createRecorder(AudioParam param) {
        this._recorderHelper = new RecorderHelper(this._mgr, this._detectionType, this._detectDelay, this._endOfSpeechDuration, this._startOfSpeechTimeout, this._partialResultsMode, this._recorderParams, param, this, this._config.context(), this._recorderListener);
    }

    public AudioParam sendAudioParam() throws TransactionException {
        return sendAudioParam(getAudioParamName());
    }

    public void startRecording() {
        this._recorderHelper.startRecorder();
    }

    public void startCapturing() {
        this._recorderHelper.startCapturing();
    }

    public void stopRecording() {
        if (this._recorderHelper != null) {
            this._recorderHelper.stopRecorder();
            this._recorderHelper = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public IRecognizeTransactionState currentState() {
        return (IRecognizeTransactionState) this._currentState;
    }

    private IRecorderHelperListener createRecorderListener() {
        return new IRecorderHelperListener() { // from class: com.nuance.nmdp.speechkit.transaction.recognize.RecognizeTransaction.1
            @Override // com.nuance.nmdp.speechkit.util.audio.IRecorderHelperListener
            public void signalEnergyUpdate(Object context, float energy) {
                RecognizeTransaction.this._energyListener.energyUpdate(energy);
            }

            @Override // com.nuance.nmdp.speechkit.util.audio.IRecorderHelperListener
            public void started(Object context) {
                JobRunner.addJob(new Runnable() { // from class: com.nuance.nmdp.speechkit.transaction.recognize.RecognizeTransaction.1.1
                    @Override // java.lang.Runnable
                    public void run() {
                        RecognizeTransaction.this.currentState().recordingStarted();
                    }
                });
            }

            @Override // com.nuance.nmdp.speechkit.util.audio.IRecorderHelperListener
            public void stopped(Object context, final int reason) {
                JobRunner.addJob(new Runnable() { // from class: com.nuance.nmdp.speechkit.transaction.recognize.RecognizeTransaction.1.2
                    @Override // java.lang.Runnable
                    public void run() {
                        if (reason != 4) {
                            RecognizeTransaction.this.currentState().recordingStopped();
                        } else {
                            Logger.error(RecognizeTransaction.this, "Recorder error");
                            RecognizeTransaction.this.currentState().recordingError();
                        }
                    }
                });
            }
        };
    }

    private IPlayerHelperListener createPromptListener() {
        return new IPlayerHelperListener() { // from class: com.nuance.nmdp.speechkit.transaction.recognize.RecognizeTransaction.2
            @Override // com.nuance.nmdp.speechkit.util.audio.IPlayerHelperListener
            public void error(Object context) {
                Logger.error(RecognizeTransaction.this, "Prompt error");
                JobRunner.addJob(new Runnable() { // from class: com.nuance.nmdp.speechkit.transaction.recognize.RecognizeTransaction.2.1
                    @Override // java.lang.Runnable
                    public void run() {
                        RecognizeTransaction.this.currentState().promptError();
                    }
                });
            }

            @Override // com.nuance.nmdp.speechkit.util.audio.IPlayerHelperListener
            public void started(Object context) {
            }

            @Override // com.nuance.nmdp.speechkit.util.audio.IPlayerHelperListener
            public void stopped(Object context) {
                JobRunner.addJob(new Runnable() { // from class: com.nuance.nmdp.speechkit.transaction.recognize.RecognizeTransaction.2.2
                    @Override // java.lang.Runnable
                    public void run() {
                        RecognizeTransaction.this.currentState().promptStopped();
                    }
                });
            }
        };
    }
}
