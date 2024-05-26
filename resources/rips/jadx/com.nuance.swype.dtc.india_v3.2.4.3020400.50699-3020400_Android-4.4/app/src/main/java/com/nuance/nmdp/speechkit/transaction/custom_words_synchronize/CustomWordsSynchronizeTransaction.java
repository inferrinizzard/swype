package com.nuance.nmdp.speechkit.transaction.custom_words_synchronize;

import android.content.Context;
import android.preference.PreferenceManager;
import com.nuance.nmdp.speechkit.CustomWordsSynchronizerConfig;
import com.nuance.nmdp.speechkit.RecognizerConstants;
import com.nuance.nmdp.speechkit.transaction.ITransactionListener;
import com.nuance.nmdp.speechkit.transaction.TransactionBase;
import com.nuance.nmdp.speechkit.transaction.TransactionConfig;
import com.nuance.nmsp.client.sdk.components.core.pdx.Dictionary;
import com.nuance.nmsp.client.sdk.components.resource.common.Manager;
import com.nuance.swype.input.AppPreferences;
import java.util.Set;

/* loaded from: classes.dex */
public class CustomWordsSynchronizeTransaction extends TransactionBase implements ICustomWordsSynchronizerTransaction {
    private Set<String> _customWordsAddSet;
    private Set<String> _customWordsRemoveSet;
    private boolean _deleteAll;
    private String _dictationType;
    private boolean _needClearAllCustomWords;

    public CustomWordsSynchronizeTransaction(Manager mgr, TransactionConfig config, String language, ITransactionListener listener) {
        super(mgr, config, language, listener);
        this._customWordsAddSet = null;
        this._customWordsRemoveSet = null;
        this._dictationType = null;
        this._deleteAll = false;
        this._needClearAllCustomWords = false;
        this._currentState = new IdleState(this);
    }

    public CustomWordsSynchronizeTransaction(Manager mgr, TransactionConfig config, String dictationType, String language, Set<String> customWordsAddSet, Set<String> customWordsRemoveSet, ITransactionListener listener) {
        super(mgr, config, language, listener);
        this._customWordsAddSet = null;
        this._customWordsRemoveSet = null;
        this._dictationType = null;
        this._deleteAll = false;
        this._needClearAllCustomWords = false;
        this._dictationType = dictationType;
        this._customWordsAddSet = customWordsAddSet;
        this._customWordsRemoveSet = customWordsRemoveSet;
        this._deleteAll = false;
        this._needClearAllCustomWords = false;
        this._currentState = new IdleState(this);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.nmdp.speechkit.transaction.TransactionBase
    public void addCustomKeys(Dictionary dict) {
        dict.addUTF8String("dictation_type", this._dictationType == null ? RecognizerConstants.RecognizerType.Dictation : this._dictationType);
        dict.addUTF8String(AppPreferences.DICTATION_LANGUAGE, this._language);
        dict.addUTF8String("phone_network", this._config.networkType());
    }

    public String getDictationType() {
        return this._dictationType;
    }

    public String getLanguage() {
        return this._language;
    }

    public Set<String> getToBeAddedCustomWords() {
        return this._customWordsAddSet;
    }

    public Set<String> getToBeRemovedCustomWords() {
        return this._customWordsRemoveSet;
    }

    public String getOldChecksum() {
        return CustomWordsSynchronizerConfig.getCurrentChecksum(PreferenceManager.getDefaultSharedPreferences((Context) this._config.context()));
    }

    public String getNewChecksum() {
        return CustomWordsSynchronizerConfig.getNewChecksum(PreferenceManager.getDefaultSharedPreferences((Context) this._config.context()));
    }

    public void setNewChecksum(String checksum) {
        CustomWordsSynchronizerConfig.setNewChecksum(PreferenceManager.getDefaultSharedPreferences((Context) this._config.context()), checksum);
    }

    public String getAlgorithmID() {
        return CustomWordsSynchronizerConfig.getAlgorithmID();
    }

    public boolean getDeleteAllFlag() {
        return this._deleteAll;
    }

    @Override // com.nuance.nmdp.speechkit.transaction.custom_words_synchronize.ICustomWordsSynchronizerTransaction
    public void setDeleteAllFlag(boolean deleteAll) {
        this._deleteAll = deleteAll;
    }

    public boolean getNeedClearAllCustomWordsFlag() {
        return this._needClearAllCustomWords;
    }

    @Override // com.nuance.nmdp.speechkit.transaction.custom_words_synchronize.ICustomWordsSynchronizerTransaction
    public void setNeedClearAllCustomWordsFlag(boolean needClearAll) {
        this._needClearAllCustomWords = needClearAll;
    }
}
