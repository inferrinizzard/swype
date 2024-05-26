package com.nuance.nmdp.speechkit;

import com.nuance.nmdp.speechkit.SpeechKit;
import com.nuance.nmdp.speechkit.transaction.TransactionRunner;
import com.nuance.nmdp.speechkit.transaction.recognize.IRecognizeTransaction;
import com.nuance.nmdp.speechkit.transaction.recognize.IRecognizeTransactionListener;
import com.nuance.nmdp.speechkit.transaction.recognize.ISignalEnergyListener;
import com.nuance.nmdp.speechkit.util.audio.IPrompt;
import com.nuance.nmdp.speechkit.util.parsers.IPdxParser;

/* loaded from: classes.dex */
abstract class RecognizerImpl extends RecognizerBase<Recognition> {
    private final String _type;

    /* JADX INFO: Access modifiers changed from: package-private */
    public RecognizerImpl(TransactionRunner transactionRunner, String type, int detectionType, SpeechKit.PartialResultsMode partialResultsMode, int endOfSpeechDuration, String language, ISignalEnergyListener energyListener) {
        super(transactionRunner, detectionType, endOfSpeechDuration, partialResultsMode, language, null, energyListener);
        this._type = type;
    }

    @Override // com.nuance.nmdp.speechkit.RecognizerBase
    protected IRecognizeTransaction createTransaction(TransactionRunner transactionRunner, int detectionType, int endOfSpeechDuration, int startOfSpeechTimeout, SpeechKit.PartialResultsMode partialResultsMode, String language, String appSessionId, ISignalEnergyListener energyListener, IPrompt startPrompt, IPrompt stopPrompt, IPrompt resultPrompt, IPrompt errorPrompt, IPdxParser<Recognition> resultParser, IRecognizeTransactionListener listener) {
        return transactionRunner.createNmdpRecognizeTransaction(this._type, detectionType, endOfSpeechDuration, startOfSpeechTimeout, partialResultsMode, language, startPrompt, stopPrompt, resultPrompt, errorPrompt, resultParser, energyListener, listener);
    }

    @Override // com.nuance.nmdp.speechkit.RecognizerBase
    protected IPdxParser<Recognition> createResultParser() {
        return new RecognitionImpl().getParser();
    }
}
