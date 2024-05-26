package com.nuance.nmdp.speechkit.transaction;

import com.nuance.nmdp.speechkit.util.JobRunner;
import com.nuance.nmdp.speechkit.util.Logger;

/* loaded from: classes.dex */
public abstract class ErrorStateBase extends TransactionStateBase {
    public final int _code;
    private final boolean _immediate;
    private final String _string;
    private final String _suggestion;

    public ErrorStateBase(TransactionBase transaction, int errorCode, String errorString, String suggestion, boolean reportImmediately) {
        super(transaction);
        this._code = errorCode;
        this._string = errorString;
        this._suggestion = suggestion;
        this._immediate = reportImmediately;
    }

    @Override // com.nuance.nmdp.speechkit.transaction.ITransactionState
    public void enter() {
        Logger.info(this, "Transaction error code: " + this._code);
        if (this._string != null && this._string.length() > 0) {
            Logger.info(this, "Transaction error text: " + this._string);
        }
        if (this._suggestion != null && this._suggestion.length() > 0) {
            Logger.info(this, "Transaction suggestion: " + this._suggestion);
        }
        this._transaction.freeNmasResource();
        final ITransactionListener l = this._transaction.getListener();
        if (this._immediate) {
            l.error(this._transaction, this._code, this._string, this._suggestion);
            l.transactionDone(this._transaction);
        } else {
            JobRunner.addJob(new Runnable() { // from class: com.nuance.nmdp.speechkit.transaction.ErrorStateBase.1
                @Override // java.lang.Runnable
                public void run() {
                    l.error(ErrorStateBase.this._transaction, ErrorStateBase.this._code, ErrorStateBase.this._string, ErrorStateBase.this._suggestion);
                    l.transactionDone(ErrorStateBase.this._transaction);
                }
            });
        }
    }
}
