package com.nuance.nmdp.speechkit;

import com.nuance.nmdp.speechkit.SpeechKit;
import com.nuance.nmdp.speechkit.transaction.TransactionRunner;
import com.nuance.nmdp.speechkit.transaction.generic.IGenericParam;
import com.nuance.nmdp.speechkit.transaction.recognize.IRecognizeTransaction;
import com.nuance.nmdp.speechkit.transaction.recognize.IRecognizeTransactionListener;
import com.nuance.nmdp.speechkit.transaction.recognize.ISignalEnergyListener;
import com.nuance.nmdp.speechkit.util.audio.IPrompt;
import com.nuance.nmdp.speechkit.util.parsers.IPdxParser;
import com.nuance.nmdp.speechkit.util.pdx.PdxValue;
import java.util.List;

/* loaded from: classes.dex */
abstract class TextRecognizerImpl extends RecognizerBase<GenericRecognition> {
    List<IGenericParam> _optionalParams;
    private final PdxValue.Dictionary _requestDict;

    /* JADX INFO: Access modifiers changed from: package-private */
    public TextRecognizerImpl(TransactionRunner transactionRunner, String language, String appSessionId, PdxValue.Dictionary requestParams, List<IGenericParam> grammarParams, ISignalEnergyListener energyListener) {
        super(transactionRunner, 0, 0, SpeechKit.PartialResultsMode.NO_PARTIAL_RESULTS, language, appSessionId, energyListener);
        this._requestDict = requestParams;
        this._optionalParams = grammarParams;
    }

    @Override // com.nuance.nmdp.speechkit.RecognizerBase
    protected IRecognizeTransaction createTransaction(TransactionRunner transactionRunner, int detectionType, int endOfSpeechDuration, int startOfSpeechTimeout, SpeechKit.PartialResultsMode partialResultsMode, String language, String appSessionId, ISignalEnergyListener energyListener, IPrompt startPrompt, IPrompt stopPrompt, IPrompt resultPrompt, IPrompt errorPrompt, IPdxParser<GenericRecognition> resultParser, IRecognizeTransactionListener listener) {
        return transactionRunner.createTextRecognizeTransaction(language, appSessionId, this._requestDict, this._optionalParams, resultParser, energyListener, listener);
    }

    @Override // com.nuance.nmdp.speechkit.RecognizerBase
    protected IPdxParser<GenericRecognition> createResultParser() {
        return new GenericRecognitionParser();
    }
}
