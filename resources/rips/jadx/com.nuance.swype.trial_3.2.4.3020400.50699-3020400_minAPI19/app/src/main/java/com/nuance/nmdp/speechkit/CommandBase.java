package com.nuance.nmdp.speechkit;

import com.nuance.nmdp.speechkit.transaction.ITransaction;
import com.nuance.nmdp.speechkit.transaction.ITransactionListener;
import com.nuance.nmdp.speechkit.transaction.TransactionRunner;
import com.nuance.nmdp.speechkit.transaction.generic.IGenericParam;
import com.nuance.nmdp.speechkit.util.Logger;
import com.nuance.nmdp.speechkit.util.parsers.IPdxParser;
import java.util.List;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public abstract class CommandBase<ResultType> {
    private ITransaction _currentTransaction;
    private final String _name;
    private final List<IGenericParam> _params;
    private IPdxParser<ResultType> _resultParser;
    private final TransactionRunner _tr;
    private final ITransactionListener _transactionListener = createTransactionListener();
    private boolean _started = false;

    protected abstract IPdxParser<ResultType> createResultParser();

    protected abstract ITransaction createTransaction(TransactionRunner transactionRunner, String str, List<IGenericParam> list, IPdxParser<ResultType> iPdxParser, ITransactionListener iTransactionListener);

    protected abstract void onError(SpeechError speechError);

    protected abstract void onResults(ResultType resulttype);

    /* JADX INFO: Access modifiers changed from: package-private */
    public CommandBase(TransactionRunner transactionRunner, String name, List<IGenericParam> params) {
        this._tr = transactionRunner;
        this._name = name;
        this._params = params;
    }

    private ITransactionListener createTransactionListener() {
        return new ITransactionListener() { // from class: com.nuance.nmdp.speechkit.CommandBase.1
            @Override // com.nuance.nmdp.speechkit.transaction.ITransactionListener
            public void transactionDone(ITransaction t) {
                if (CommandBase.this._currentTransaction == t) {
                    if (CommandBase.this._resultParser != null) {
                        CommandBase.this.onResults(CommandBase.this._resultParser.getParsed());
                    } else {
                        CommandBase.this.onResults(null);
                    }
                    CommandBase.this._currentTransaction = null;
                }
            }

            @Override // com.nuance.nmdp.speechkit.transaction.ITransactionListener
            public void error(ITransaction t, int code, String error, String suggestion) {
                if (CommandBase.this._currentTransaction == t) {
                    CommandBase.this.onError(new SpeechErrorImpl(code, error, suggestion));
                }
            }
        };
    }

    public void start() {
        if (this._tr.isValid()) {
            if (!this._started) {
                this._resultParser = createResultParser();
                this._currentTransaction = createTransaction(this._tr, this._name, this._params, this._resultParser, this._transactionListener);
                if (this._currentTransaction == null) {
                    Logger.error(this, "Unable to create command transaction");
                    onError(new SpeechErrorImpl(0, null, null));
                    return;
                } else {
                    this._started = true;
                    this._currentTransaction.start();
                    return;
                }
            }
            return;
        }
        Logger.error(this, "Unable to create command transaction. Transaction runner is invalid.");
        onError(new SpeechErrorImpl(0, null, null));
    }

    public void cancel() {
        if (this._currentTransaction != null) {
            this._currentTransaction.cancel();
            this._currentTransaction = null;
        }
    }
}
