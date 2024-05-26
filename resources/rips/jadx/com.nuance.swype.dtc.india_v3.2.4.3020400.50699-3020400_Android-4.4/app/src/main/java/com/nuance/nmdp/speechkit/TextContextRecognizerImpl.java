package com.nuance.nmdp.speechkit;

import com.nuance.nmdp.speechkit.SpeechKit;
import com.nuance.nmdp.speechkit.transaction.TransactionRunner;
import com.nuance.nmdp.speechkit.transaction.recognize.IRecognizeTransaction;
import com.nuance.nmdp.speechkit.transaction.recognize.IRecognizeTransactionListener;
import com.nuance.nmdp.speechkit.transaction.recognize.ISignalEnergyListener;
import com.nuance.nmdp.speechkit.util.audio.IPrompt;
import com.nuance.nmdp.speechkit.util.parsers.IPdxParser;

/* loaded from: classes.dex */
abstract class TextContextRecognizerImpl extends RecognizerBase<Recognition> {
    private final TextContext _textContext;
    private final String _type;

    /* JADX INFO: Access modifiers changed from: package-private */
    public TextContextRecognizerImpl(TransactionRunner transactionRunner, String type, int detectionType, SpeechKit.PartialResultsMode partialResultsMode, String language, TextContext textContext, ISignalEnergyListener energyListener) {
        super(transactionRunner, detectionType, 0, partialResultsMode, language, null, energyListener);
        this._type = type;
        this._textContext = textContext;
    }

    @Override // com.nuance.nmdp.speechkit.RecognizerBase
    protected IRecognizeTransaction createTransaction(TransactionRunner transactionRunner, int detectionType, int endOfSpeechDuration, int startOfSpeechTimeout, SpeechKit.PartialResultsMode partialResultsMode, String language, String appSessionId, ISignalEnergyListener energyListener, IPrompt startPrompt, IPrompt stopPrompt, IPrompt resultPrompt, IPrompt errorPrompt, IPdxParser<Recognition> resultParser, IRecognizeTransactionListener listener) {
        return transactionRunner.createTextContextNmdpRecognizeTransaction(this._type, detectionType, endOfSpeechDuration, startOfSpeechTimeout, partialResultsMode, language, this._textContext, startPrompt, stopPrompt, resultPrompt, errorPrompt, resultParser, energyListener, listener);
    }
}
