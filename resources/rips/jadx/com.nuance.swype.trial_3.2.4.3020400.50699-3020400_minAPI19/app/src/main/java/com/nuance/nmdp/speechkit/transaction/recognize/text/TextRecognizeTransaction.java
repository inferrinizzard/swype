package com.nuance.nmdp.speechkit.transaction.recognize.text;

import com.nuance.nmdp.speechkit.SpeechKit;
import com.nuance.nmdp.speechkit.transaction.TransactionConfig;
import com.nuance.nmdp.speechkit.transaction.TransactionException;
import com.nuance.nmdp.speechkit.transaction.generic.IGenericParam;
import com.nuance.nmdp.speechkit.transaction.generic.IGenericParamValue;
import com.nuance.nmdp.speechkit.transaction.recognize.IRecognizeTransactionListener;
import com.nuance.nmdp.speechkit.transaction.recognize.ISignalEnergyListener;
import com.nuance.nmdp.speechkit.transaction.recognize.RecognizeTransaction;
import com.nuance.nmdp.speechkit.util.Logger;
import com.nuance.nmdp.speechkit.util.PdxParamUtils;
import com.nuance.nmdp.speechkit.util.PdxParamValue;
import com.nuance.nmdp.speechkit.util.parsers.IPdxParser;
import com.nuance.nmdp.speechkit.util.pdx.PdxValue;
import com.nuance.nmsp.client.sdk.components.core.pdx.Dictionary;
import com.nuance.nmsp.client.sdk.components.resource.common.Manager;
import com.nuance.swype.input.AppPreferences;
import java.util.List;

/* loaded from: classes.dex */
public class TextRecognizeTransaction extends RecognizeTransaction {
    private String _appSessionId;
    private final List<IGenericParam> _optionalParams;
    private PdxValue.Dictionary _requestDict;

    public TextRecognizeTransaction(Manager mgr, TransactionConfig config, String language, String appSessionId, PdxValue.Dictionary requestParams, List<IGenericParam> grammarParams, IPdxParser<?> resultParser, ISignalEnergyListener energyListener, IRecognizeTransactionListener listener) {
        super(mgr, config, 0, 0, 0, SpeechKit.PartialResultsMode.NO_PARTIAL_RESULTS, language, null, null, null, null, resultParser, null, energyListener, listener);
        this._optionalParams = grammarParams;
        this._requestDict = requestParams;
        this._appSessionId = appSessionId;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.nmdp.speechkit.transaction.TransactionBase
    public void addCustomKeys(Dictionary dict) {
        super.addCustomKeys(dict);
        dict.addUTF8String(AppPreferences.DICTATION_LANGUAGE, this._language);
        if (this._appSessionId != null) {
            dict.addUTF8String("application_session_id", this._appSessionId);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.nmdp.speechkit.transaction.recognize.RecognizeTransaction
    public String getCommandName() {
        return this._config.getAppserverCmd();
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
        IGenericParamValue value = new PdxParamValue(this._requestDict);
        d.addDictionary("appserver_data", PdxParamUtils.createDictFromValue(this, value.getDictValue()));
        Logger.info(this, "REQUEST_INFO: " + d);
        sendDictParam("REQUEST_INFO", d);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.nmdp.speechkit.transaction.recognize.RecognizeTransaction
    public boolean supportsRecording() {
        return false;
    }
}
