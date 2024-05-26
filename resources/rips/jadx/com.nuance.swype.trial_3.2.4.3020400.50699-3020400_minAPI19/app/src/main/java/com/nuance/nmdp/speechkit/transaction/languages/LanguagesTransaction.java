package com.nuance.nmdp.speechkit.transaction.languages;

import com.nuance.nmdp.speechkit.transaction.ITransactionListener;
import com.nuance.nmdp.speechkit.transaction.TransactionConfig;
import com.nuance.nmdp.speechkit.transaction.generic.GenericTransaction;
import com.nuance.nmdp.speechkit.transaction.generic.IGenericParam;
import com.nuance.nmdp.speechkit.util.parsers.IPdxParser;
import com.nuance.nmsp.client.sdk.components.resource.common.Manager;
import java.util.List;

/* loaded from: classes.dex */
public class LanguagesTransaction extends GenericTransaction {
    public LanguagesTransaction(List<IGenericParam> params, IPdxParser<?> resultParser, Manager mgr, TransactionConfig config, ITransactionListener listener) {
        super(mgr, config, config.getLanguageCmd(), params, resultParser, listener);
    }
}
