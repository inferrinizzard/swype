package com.nuance.nmdp.speechkit;

import com.nuance.nmdp.speechkit.transaction.ITransaction;
import com.nuance.nmdp.speechkit.transaction.ITransactionListener;
import com.nuance.nmdp.speechkit.transaction.TransactionRunner;
import com.nuance.nmdp.speechkit.transaction.generic.IGenericParam;
import com.nuance.nmdp.speechkit.util.parsers.IPdxParser;
import java.util.List;

/* loaded from: classes.dex */
abstract class LogRevisionCmdImpl extends CommandBase<GenericResult> {
    private final String _appSessionId;

    /* JADX INFO: Access modifiers changed from: package-private */
    public LogRevisionCmdImpl(TransactionRunner transactionRunner, String name, String appSessionId, List<IGenericParam> params) {
        super(transactionRunner, name, params);
        this._appSessionId = appSessionId;
    }

    @Override // com.nuance.nmdp.speechkit.CommandBase
    protected ITransaction createTransaction(TransactionRunner transactionRunner, String name, List<IGenericParam> params, IPdxParser<GenericResult> resultParser, ITransactionListener listener) {
        return transactionRunner.createLogRevisionTransaction(params, this._appSessionId, resultParser, listener);
    }

    @Override // com.nuance.nmdp.speechkit.CommandBase
    protected IPdxParser<GenericResult> createResultParser() {
        return new GenericResultImpl().getParser();
    }
}
