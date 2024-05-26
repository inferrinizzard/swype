package com.nuance.nmdp.speechkit.transaction.generic;

import com.nuance.nmdp.speechkit.transaction.ITransactionListener;
import com.nuance.nmdp.speechkit.transaction.TransactionBase;
import com.nuance.nmdp.speechkit.transaction.TransactionConfig;
import com.nuance.nmdp.speechkit.transaction.TransactionException;
import com.nuance.nmdp.speechkit.util.Logger;
import com.nuance.nmdp.speechkit.util.PdxParamUtils;
import com.nuance.nmdp.speechkit.util.parsers.IPdxParser;
import com.nuance.nmsp.client.sdk.components.core.pdx.Dictionary;
import com.nuance.nmsp.client.sdk.components.resource.common.Manager;
import com.nuance.swype.input.AppPreferences;
import java.util.List;

/* loaded from: classes.dex */
public class GenericTransaction extends TransactionBase {
    private final String _name;
    private final List<IGenericParam> _params;
    private final IPdxParser<?> _parser;

    public GenericTransaction(Manager mgr, TransactionConfig config, String name, List<IGenericParam> params, IPdxParser<?> resultParser, ITransactionListener listener) {
        super(mgr, config, listener);
        this._params = params;
        this._parser = resultParser;
        this._name = name;
        this._currentState = new IdleState(this);
    }

    @Override // com.nuance.nmdp.speechkit.transaction.TransactionBase
    public void addCustomKeys(Dictionary dict) {
        super.addCustomKeys(dict);
        dict.addUTF8String(AppPreferences.DICTATION_LANGUAGE, "eng-USA");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void sendParams() throws TransactionException {
        if (this._params != null) {
            for (IGenericParam param : this._params) {
                String name = param.getName();
                IGenericParamValue value = param.getValue();
                switch (value.getType()) {
                    case 0:
                        sendTextParam(name, value.getStringValue());
                        break;
                    case 1:
                        Logger.error(this, "INT is an unsupported param type");
                        break;
                    case 2:
                        sendDictParam(name, PdxParamUtils.createDictFromValue(this, value.getDictValue()));
                        break;
                    case 3:
                        Logger.error(this, "SEQ is an unsupported param type");
                        break;
                    case 5:
                        sendSeqStartParam(name, PdxParamUtils.createDictFromValue(this, value.getDictValue()));
                        break;
                    case 6:
                        sendSeqChunkParam(name, PdxParamUtils.createDictFromValue(this, value.getDictValue()));
                        break;
                    case 7:
                        sendSeqEndParam(name, PdxParamUtils.createDictFromValue(this, value.getDictValue()));
                        break;
                }
                Logger.info(this, "Send custom param " + name);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final String getName() {
        return this._name;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final IPdxParser<?> getResultParser() {
        return this._parser;
    }
}
