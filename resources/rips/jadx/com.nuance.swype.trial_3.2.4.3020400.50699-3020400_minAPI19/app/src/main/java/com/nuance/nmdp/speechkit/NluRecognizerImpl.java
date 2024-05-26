package com.nuance.nmdp.speechkit;

import com.nuance.nmdp.speechkit.SpeechKit;
import com.nuance.nmdp.speechkit.transaction.TransactionRunner;
import com.nuance.nmdp.speechkit.transaction.recognize.IRecognizeTransaction;
import com.nuance.nmdp.speechkit.transaction.recognize.IRecognizeTransactionListener;
import com.nuance.nmdp.speechkit.transaction.recognize.ISignalEnergyListener;
import com.nuance.nmdp.speechkit.util.audio.IPrompt;
import com.nuance.nmdp.speechkit.util.parsers.IPdxParser;
import com.nuance.nmdp.speechkit.util.pdx.PdxValue;

/* loaded from: classes.dex */
abstract class NluRecognizerImpl extends RecognizerBase<GenericRecognition> {
    private final PdxValue.Dictionary _requestDict;
    private final String _type;

    /* JADX INFO: Access modifiers changed from: package-private */
    public NluRecognizerImpl(TransactionRunner transactionRunner, String type, int detectionType, String language, String appSessionId, PdxValue.Dictionary requestParams, ISignalEnergyListener energyListener) {
        super(transactionRunner, detectionType, 0, SpeechKit.PartialResultsMode.NO_PARTIAL_RESULTS, language, appSessionId, energyListener);
        this._type = type;
        this._requestDict = requestParams;
    }

    @Override // com.nuance.nmdp.speechkit.RecognizerBase
    protected IRecognizeTransaction createTransaction(TransactionRunner transactionRunner, int detectionType, int endOfSpeechDuration, int startOfSpeechTimeout, SpeechKit.PartialResultsMode partialResults, String language, String appSessionId, ISignalEnergyListener energyListener, IPrompt startPrompt, IPrompt stopPrompt, IPrompt resultPrompt, IPrompt errorPrompt, IPdxParser<GenericRecognition> resultParser, IRecognizeTransactionListener listener) {
        return transactionRunner.createNluRecognizeTransaction(this._type, detectionType, language, appSessionId, this._requestDict, startPrompt, stopPrompt, resultPrompt, errorPrompt, resultParser, energyListener, listener);
    }

    @Override // com.nuance.nmdp.speechkit.RecognizerBase
    protected IPdxParser<GenericRecognition> createResultParser() {
        return new GenericRecognitionParser();
    }
}
