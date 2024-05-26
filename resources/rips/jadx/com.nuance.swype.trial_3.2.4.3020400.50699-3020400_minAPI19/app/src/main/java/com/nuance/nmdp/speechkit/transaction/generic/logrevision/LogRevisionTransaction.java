package com.nuance.nmdp.speechkit.transaction.generic.logrevision;

import com.nuance.nmdp.speechkit.transaction.ITransactionListener;
import com.nuance.nmdp.speechkit.transaction.TransactionConfig;
import com.nuance.nmdp.speechkit.transaction.generic.GenericTransaction;
import com.nuance.nmdp.speechkit.transaction.generic.IGenericParam;
import com.nuance.nmdp.speechkit.util.parsers.IPdxParser;
import com.nuance.nmsp.client.sdk.components.core.pdx.Dictionary;
import com.nuance.nmsp.client.sdk.components.resource.common.Manager;
import java.util.List;

/* loaded from: classes.dex */
public class LogRevisionTransaction extends GenericTransaction {
    private final String _appSessionId;

    public LogRevisionTransaction(Manager mgr, TransactionConfig config, List<IGenericParam> params, String appSessionId, IPdxParser<?> resultParser, ITransactionListener listener) {
        super(mgr, config, config.getLogRevisionCmd(), params, resultParser, listener);
        this._appSessionId = appSessionId;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.nmdp.speechkit.transaction.generic.GenericTransaction, com.nuance.nmdp.speechkit.transaction.TransactionBase
    public void addCustomKeys(Dictionary dict) {
        super.addCustomKeys(dict);
        dict.addUTF8String("application_session_id", this._appSessionId);
    }
}
