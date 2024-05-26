package com.nuance.nmdp.speechkit.transaction.recognize.nlu;

import com.nuance.nmdp.speechkit.SpeechKit;
import com.nuance.nmdp.speechkit.transaction.TransactionConfig;
import com.nuance.nmdp.speechkit.transaction.TransactionException;
import com.nuance.nmdp.speechkit.transaction.generic.IGenericParamValue;
import com.nuance.nmdp.speechkit.transaction.recognize.IRecognizeTransactionListener;
import com.nuance.nmdp.speechkit.transaction.recognize.ISignalEnergyListener;
import com.nuance.nmdp.speechkit.transaction.recognize.RecognizeTransaction;
import com.nuance.nmdp.speechkit.util.Logger;
import com.nuance.nmdp.speechkit.util.PdxParamUtils;
import com.nuance.nmdp.speechkit.util.PdxParamValue;
import com.nuance.nmdp.speechkit.util.audio.IPrompt;
import com.nuance.nmdp.speechkit.util.parsers.IPdxParser;
import com.nuance.nmdp.speechkit.util.pdx.PdxValue;
import com.nuance.nmsp.client.sdk.components.core.pdx.Dictionary;
import com.nuance.nmsp.client.sdk.components.resource.common.Manager;
import com.nuance.swype.input.AppPreferences;
import com.nuance.swype.input.InputFieldInfo;

/* loaded from: classes.dex */
public class NluRecognizeTransaction extends RecognizeTransaction {
    private String _appSessionId;
    private PdxValue.Dictionary _requestDict;
    private final String _type;

    public NluRecognizeTransaction(Manager mgr, TransactionConfig config, String type, int detectionType, String language, String appSessionId, PdxValue.Dictionary requestParams, IPrompt startPrompt, IPrompt stopPrompt, IPrompt resultPrompt, IPrompt errorPrompt, IPdxParser<?> resultParser, ISignalEnergyListener energyListener, IRecognizeTransactionListener listener) {
        super(mgr, config, detectionType, 0, 0, SpeechKit.PartialResultsMode.NO_PARTIAL_RESULTS, language, startPrompt, stopPrompt, resultPrompt, errorPrompt, resultParser, null, energyListener, listener);
        this._type = type;
        this._requestDict = requestParams;
        this._appSessionId = appSessionId;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.nmdp.speechkit.transaction.TransactionBase
    public void addCustomKeys(Dictionary dict) {
        super.addCustomKeys(dict);
        dict.addUTF8String(AppPreferences.DICTATION_LANGUAGE, this._language);
        dict.addUTF8String("dictation_type", this._type);
        if (this._appSessionId != null) {
            dict.addUTF8String("application_session_id", this._appSessionId);
        }
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
        IGenericParamValue value = new PdxParamValue(this._requestDict);
        d.addDictionary("appserver_data", PdxParamUtils.createDictFromValue(this, value.getDictValue()));
        Logger.info(this, "REQUEST_INFO: " + d);
        sendDictParam("REQUEST_INFO", d);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.nmdp.speechkit.transaction.recognize.RecognizeTransaction
    public boolean supportsRecording() {
        return true;
    }
}
