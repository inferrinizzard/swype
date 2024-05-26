package com.nuance.nmdp.speechkit;

import com.nuance.nmdp.speechkit.transaction.ITransaction;
import com.nuance.nmdp.speechkit.transaction.ITransactionListener;
import com.nuance.nmdp.speechkit.transaction.TransactionRunner;
import com.nuance.nmdp.speechkit.transaction.generic.IGenericParam;
import com.nuance.nmdp.speechkit.util.parsers.IPdxParser;
import java.util.List;

/* loaded from: classes.dex */
abstract class DataUploadCmdImpl extends CommandBase<DataUploadResult> {
    /* JADX INFO: Access modifiers changed from: package-private */
    public DataUploadCmdImpl(TransactionRunner transactionRunner, String name, List<IGenericParam> params) {
        super(transactionRunner, name, params);
    }

    @Override // com.nuance.nmdp.speechkit.CommandBase
    protected ITransaction createTransaction(TransactionRunner transactionRunner, String name, List<IGenericParam> params, IPdxParser<DataUploadResult> resultParser, ITransactionListener listener) {
        return transactionRunner.createGenericTransaction(name, params, resultParser, listener);
    }

    @Override // com.nuance.nmdp.speechkit.CommandBase
    protected IPdxParser<DataUploadResult> createResultParser() {
        return new DataUploadResultImpl().getParser();
    }
}
