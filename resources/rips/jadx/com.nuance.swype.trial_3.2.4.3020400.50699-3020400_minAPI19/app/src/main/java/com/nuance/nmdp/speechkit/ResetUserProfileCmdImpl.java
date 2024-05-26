package com.nuance.nmdp.speechkit;

import com.nuance.nmdp.speechkit.transaction.ITransaction;
import com.nuance.nmdp.speechkit.transaction.ITransactionListener;
import com.nuance.nmdp.speechkit.transaction.TransactionRunner;
import com.nuance.nmdp.speechkit.transaction.generic.IGenericParam;
import com.nuance.nmdp.speechkit.util.parsers.IPdxParser;
import java.util.List;

/* loaded from: classes.dex */
abstract class ResetUserProfileCmdImpl extends CommandBase<GenericResult> {
    /* JADX INFO: Access modifiers changed from: package-private */
    public ResetUserProfileCmdImpl(TransactionRunner transactionRunner, String name) {
        super(transactionRunner, name, null);
    }

    @Override // com.nuance.nmdp.speechkit.CommandBase
    protected ITransaction createTransaction(TransactionRunner transactionRunner, String name, List<IGenericParam> params, IPdxParser<GenericResult> resultParser, ITransactionListener listener) {
        return transactionRunner.createGenericTransaction(name, params, resultParser, listener);
    }

    @Override // com.nuance.nmdp.speechkit.CommandBase
    protected IPdxParser<GenericResult> createResultParser() {
        return new GenericResultImpl().getParser();
    }
}
