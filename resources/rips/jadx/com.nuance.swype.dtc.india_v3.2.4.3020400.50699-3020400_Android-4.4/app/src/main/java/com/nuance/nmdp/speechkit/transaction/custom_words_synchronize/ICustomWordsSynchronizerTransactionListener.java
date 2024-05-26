package com.nuance.nmdp.speechkit.transaction.custom_words_synchronize;

import com.nuance.nmdp.speechkit.CustomWordsSynchronizeResult;
import com.nuance.nmdp.speechkit.transaction.ITransaction;
import com.nuance.nmdp.speechkit.transaction.ITransactionListener;

/* loaded from: classes.dex */
public interface ICustomWordsSynchronizerTransactionListener extends ITransactionListener {
    void result(ITransaction iTransaction, CustomWordsSynchronizeResult customWordsSynchronizeResult);
}
