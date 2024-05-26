package com.nuance.nmdp.speechkit.transaction.vocalize;

import com.nuance.nmdp.speechkit.transaction.ITransaction;
import com.nuance.nmdp.speechkit.transaction.ITransactionListener;

/* loaded from: classes.dex */
public interface IVocalizeTransactionListener extends ITransactionListener {
    void audioStarted(ITransaction iTransaction);
}
