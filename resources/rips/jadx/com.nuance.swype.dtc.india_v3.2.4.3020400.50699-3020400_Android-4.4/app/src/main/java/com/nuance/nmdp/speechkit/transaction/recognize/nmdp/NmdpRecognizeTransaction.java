package com.nuance.nmdp.speechkit.transaction.recognize.nmdp;

import com.nuance.nmdp.speechkit.SpeechKit;
import com.nuance.nmdp.speechkit.transaction.TransactionConfig;
import com.nuance.nmdp.speechkit.transaction.TransactionException;
import com.nuance.nmdp.speechkit.transaction.generic.IGenericParamValue;
import com.nuance.nmdp.speechkit.transaction.recognize.IRecognizeTransactionListener;
import com.nuance.nmdp.speechkit.transaction.recognize.ISignalEnergyListener;
import com.nuance.nmdp.speechkit.transaction.recognize.RecognizeTransaction;
import com.nuance.nmdp.speechkit.util.PdxParamUtils;
import com.nuance.nmdp.speechkit.util.PdxParamValue;
import com.nuance.nmdp.speechkit.util.audio.IPrompt;
import com.nuance.nmdp.speechkit.util.parsers.IPdxParser;
import com.nuance.nmdp.speechkit.util.pdx.PdxValue;
import com.nuance.nmsp.client.sdk.common.defines.NMSPDefines;
import com.nuance.nmsp.client.sdk.components.core.pdx.Dictionary;
import com.nuance.nmsp.client.sdk.components.resource.common.Manager;
import com.nuance.swype.input.AppPreferences;
import com.nuance.swype.input.InputFieldInfo;

/* loaded from: classes.dex */
public class NmdpRecognizeTransaction extends RecognizeTransaction {
    private PdxValue.Sequence _grammar_list;
    private final String _type;

    public NmdpRecognizeTransaction(Manager mgr, TransactionConfig config, String type, int detectionType, int endOfSpeechDuration, int startOfSpeechTimeout, SpeechKit.PartialResultsMode partialResultsMode, String language, PdxValue.Sequence grammarList, IPrompt startPrompt, IPrompt stopPrompt, IPrompt resultPrompt, IPrompt errorPrompt, IPdxParser<?> resultParser, ISignalEnergyListener energyListener, IRecognizeTransactionListener listener) {
        super(mgr, config, detectionType, endOfSpeechDuration, startOfSpeechTimeout, partialResultsMode, language, startPrompt, stopPrompt, resultPrompt, errorPrompt, resultParser, null, energyListener, listener);
        this._type = type;
        this._grammar_list = grammarList;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.nmdp.speechkit.transaction.TransactionBase
    public void addCustomKeys(Dictionary dict) {
        super.addCustomKeys(dict);
        dict.addUTF8String(AppPreferences.DICTATION_LANGUAGE, this._language);
        dict.addUTF8String("dictation_type", this._type);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.nmdp.speechkit.transaction.recognize.RecognizeTransaction
    public String getCommandName() {
        return this._config.getAsrCmd();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.nmdp.speechkit.transaction.recognize.RecognizeTransaction
    public String getAudioParamName() {
        return "AUDIO_INFO";
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.nmdp.speechkit.transaction.recognize.RecognizeTransaction
    public void sendParams() throws TransactionException {
        Dictionary d = createDictionary();
        d.addInteger("start", 0);
        d.addInteger("end", 0);
        d.addUTF8String(InputFieldInfo.INPUT_TYPE_TEXT, "");
        if (this._grammar_list != null) {
            IGenericParamValue value = new PdxParamValue(this._grammar_list);
            d.addSequence("grammar_list", PdxParamUtils.createSeqFromValue(this, value.getSeqValue()));
        }
        d.addInteger("binary_results", 1);
        switch (this._partialResultsMode) {
            case UTTERANCE_DETECTION_DEFAULT:
                d.addUTF8String("intermediate_response_mode", "UtteranceDetectionWithCompleteRecognition");
                d.addInteger("enable_intermediate_response", 1);
                break;
            case UTTERANCE_DETECTION_VERY_AGRESSIVE:
                d.addUTF8String("intermediate_response_mode", "UtteranceDetectionWithCompleteRecognition");
                d.addUTF8String(NMSPDefines.NMSP_DEFINES_ENDPOINTER_UTTERANCE_SILENCE_DURATION, "VeryAggressive");
                break;
            case UTTERANCE_DETECTION_AGRESSIVE:
                d.addUTF8String("intermediate_response_mode", "UtteranceDetectionWithCompleteRecognition");
                d.addUTF8String(NMSPDefines.NMSP_DEFINES_ENDPOINTER_UTTERANCE_SILENCE_DURATION, "Aggressive");
                break;
            case UTTERANCE_DETECTION_AVERAGE:
                d.addUTF8String("intermediate_response_mode", "UtteranceDetectionWithCompleteRecognition");
                d.addUTF8String(NMSPDefines.NMSP_DEFINES_ENDPOINTER_UTTERANCE_SILENCE_DURATION, "Average");
                break;
            case UTTERANCE_DETECTION_CONSERVATIVE:
                d.addUTF8String("intermediate_response_mode", "UtteranceDetectionWithCompleteRecognition");
                d.addUTF8String(NMSPDefines.NMSP_DEFINES_ENDPOINTER_UTTERANCE_SILENCE_DURATION, "Conservative");
                break;
            case CONTINUOUS_STREAMING_RESULTS:
                d.addUTF8String("intermediate_response_mode", "NoUtteranceDetectionWithPartialRecognition");
                break;
        }
        sendDictParam("REQUEST_INFO", d);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.nmdp.speechkit.transaction.recognize.RecognizeTransaction
    public boolean supportsRecording() {
        return true;
    }
}
