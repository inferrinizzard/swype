package com.nuance.nmdp.speechkit.transaction.recognize.nmdp;

import android.content.Context;
import com.nuance.nmdp.speechkit.CustomWordsSynchronizerConfig;
import com.nuance.nmdp.speechkit.SpeechKit;
import com.nuance.nmdp.speechkit.TextContext;
import com.nuance.nmdp.speechkit.transaction.TransactionConfig;
import com.nuance.nmdp.speechkit.transaction.TransactionException;
import com.nuance.nmdp.speechkit.transaction.custom_words_synchronize.CustomWordsSynchronizeStartingState;
import com.nuance.nmdp.speechkit.transaction.recognize.IRecognizeTransactionListener;
import com.nuance.nmdp.speechkit.transaction.recognize.ISignalEnergyListener;
import com.nuance.nmdp.speechkit.util.audio.IPrompt;
import com.nuance.nmdp.speechkit.util.parsers.IPdxParser;
import com.nuance.nmsp.client.sdk.common.defines.NMSPDefines;
import com.nuance.nmsp.client.sdk.components.core.pdx.Dictionary;
import com.nuance.nmsp.client.sdk.components.core.pdx.Sequence;
import com.nuance.nmsp.client.sdk.components.resource.common.Manager;
import com.nuance.swype.input.InputFieldInfo;

/* loaded from: classes.dex */
public class TextContextNmdpRecognizeTransaction extends NmdpRecognizeTransaction {
    private final TextContext _textContext;

    public TextContextNmdpRecognizeTransaction(Manager mgr, TransactionConfig config, String type, int detectionType, int endOfSpeechDuration, int startOfSpeechTimeout, SpeechKit.PartialResultsMode partialResultsMode, String language, TextContext textContext, IPrompt startPrompt, IPrompt stopPrompt, IPrompt resultPrompt, IPrompt errorPrompt, IPdxParser<?> resultParser, ISignalEnergyListener energyListener, IRecognizeTransactionListener listener) {
        super(mgr, config, type, detectionType, endOfSpeechDuration, startOfSpeechTimeout, partialResultsMode, language, null, startPrompt, stopPrompt, resultPrompt, errorPrompt, resultParser, energyListener, listener);
        this._textContext = textContext;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.nmdp.speechkit.transaction.recognize.nmdp.NmdpRecognizeTransaction, com.nuance.nmdp.speechkit.transaction.TransactionBase
    public void addCustomKeys(Dictionary dict) {
        super.addCustomKeys(dict);
        for (String key : this._textContext.mCustomStringLog.keySet()) {
            dict.addUTF8String(key, this._textContext.mCustomStringLog.get(key));
        }
        for (String key2 : this._textContext.mCustomIntegerLog.keySet()) {
            dict.addInteger(key2, this._textContext.mCustomIntegerLog.get(key2).intValue());
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.nmdp.speechkit.transaction.recognize.nmdp.NmdpRecognizeTransaction, com.nuance.nmdp.speechkit.transaction.recognize.RecognizeTransaction
    public void sendParams() throws TransactionException {
        Dictionary d = createDictionary();
        d.addInteger("start", this._textContext.getStartCursor());
        d.addInteger("end", this._textContext.getEndCursor());
        d.addUTF8String(InputFieldInfo.INPUT_TYPE_TEXT, this._textContext.getTextUTF8());
        d.addInteger("binary_results", 1);
        if (this._textContext.needCustomWordRecogniztion()) {
            Sequence grammarList = createSequence();
            Dictionary grammarListDict = createDictionary();
            grammarListDict.addUTF8String("type", CustomWordsSynchronizeStartingState.CUSTOM_WORDS_TYPE);
            grammarListDict.addUTF8String("id", CustomWordsSynchronizeStartingState.XT9_UDB_ENTRIES);
            grammarListDict.addUTF8String("checksum", getCustomWordsChecksum());
            grammarList.addDictionary(grammarListDict);
            d.addSequence("grammar_list", grammarList);
        }
        switch (this._partialResultsMode) {
            case UTTERANCE_DETECTION_DEFAULT:
                d.addUTF8String("intermediate_response_mode", "UtteranceDetectionWithCompleteRecognition");
                d.addInteger("enable_intermediate_response", 1);
                break;
            case UTTERANCE_DETECTION_VERY_AGRESSIVE:
                d.addUTF8String("intermediate_response_mode", "UtteranceDetectionWithCompleteRecognition");
                d.addUTF8String(NMSPDefines.NMSP_DEFINES_ENDPOINTER_UTTERANCE_SILENCE_DURATION, "VeryAggressive");
                d.addInteger("enable_intermediate_response", 1);
                break;
            case UTTERANCE_DETECTION_AGRESSIVE:
                d.addUTF8String("intermediate_response_mode", "UtteranceDetectionWithCompleteRecognition");
                d.addUTF8String(NMSPDefines.NMSP_DEFINES_ENDPOINTER_UTTERANCE_SILENCE_DURATION, "Aggressive");
                d.addInteger("enable_intermediate_response", 1);
                break;
            case UTTERANCE_DETECTION_AVERAGE:
                d.addUTF8String("intermediate_response_mode", "UtteranceDetectionWithCompleteRecognition");
                d.addUTF8String(NMSPDefines.NMSP_DEFINES_ENDPOINTER_UTTERANCE_SILENCE_DURATION, "Average");
                d.addInteger("enable_intermediate_response", 1);
                break;
            case UTTERANCE_DETECTION_CONSERVATIVE:
                d.addUTF8String("intermediate_response_mode", "UtteranceDetectionWithCompleteRecognition");
                d.addUTF8String(NMSPDefines.NMSP_DEFINES_ENDPOINTER_UTTERANCE_SILENCE_DURATION, "Conservative");
                d.addInteger("enable_intermediate_response", 1);
                break;
            case CONTINUOUS_STREAMING_RESULTS:
                d.addUTF8String("intermediate_response_mode", "NoUtteranceDetectionWithPartialRecognition");
                d.addInteger("enable_intermediate_response", 1);
                break;
        }
        sendDictParam("REQUEST_INFO", d);
    }

    private String getCustomWordsChecksum() {
        return CustomWordsSynchronizerConfig.getCurrentChecksum((Context) this._config.context());
    }
}
