package com.nuance.nmdp.speechkit.transaction.custom_words_synchronize;

import com.nuance.nmdp.speechkit.transaction.ITransaction;

/* loaded from: classes.dex */
public interface ICustomWordsSynchronizerTransaction extends ITransaction {
    void setDeleteAllFlag(boolean z);

    void setNeedClearAllCustomWordsFlag(boolean z);
}
