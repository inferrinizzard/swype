package com.nuance.nmdp.speechkit;

import com.nuance.nmdp.speechkit.CustomWordsSynchronizer;
import com.nuance.nmdp.speechkit.transaction.ITransaction;
import com.nuance.nmdp.speechkit.transaction.TransactionRunner;
import com.nuance.nmdp.speechkit.transaction.custom_words_synchronize.ICustomWordsSynchronizerTransaction;
import com.nuance.nmdp.speechkit.transaction.custom_words_synchronize.ICustomWordsSynchronizerTransactionListener;
import com.nuance.nmdp.speechkit.util.List;
import com.nuance.nmdp.speechkit.util.Logger;
import java.util.Set;

/* loaded from: classes.dex */
public final class CustomWordsSynchronizerImpl implements CustomWordsSynchronizer {
    private String _dictationType;
    private String _language;
    private final CustomWordsSynchronizer.Listener _listener;
    private final TransactionRunner _tr;
    private final ICustomWordsSynchronizerTransactionListener _transactionListener = createTransactionListener();
    private ICustomWordsSynchronizerTransaction _currentTransaction = null;
    private CustomWordsSynchronizerAction _currentAction = null;
    private SpeechError _transactionError = null;
    private final List _actions = new List();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class CustomWordsSynchronizerAction {
        private final int ActionType;
        private final Set<String> CustomWordsAddSet;
        private final Set<String> CustomWordsRemoveSet;
        private final String DictationType;
        private final boolean IsToClearAllCustomWords;
        private final boolean IsToDeleteAllUserInformation;
        private final String Language;

        public CustomWordsSynchronizerAction(int actionType, String dictationType, String language, Set<String> words, boolean clearAllCustomWords) {
            this.ActionType = actionType;
            this.DictationType = dictationType;
            this.Language = language;
            this.CustomWordsAddSet = actionType == 0 ? words : null;
            this.CustomWordsRemoveSet = actionType != 1 ? null : words;
            this.IsToClearAllCustomWords = clearAllCustomWords;
            this.IsToDeleteAllUserInformation = actionType == 3;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CustomWordsSynchronizerImpl(TransactionRunner transactionRunner, String dictationType, String language, CustomWordsSynchronizer.Listener listener) {
        this._tr = transactionRunner;
        this._dictationType = dictationType;
        this._language = language;
        this._listener = listener;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleTransactionError(int actionType, SpeechError error) {
        this._listener.onError(this, actionType, error);
        int numActions = this._actions.size();
        for (int i = 0; i < numActions; i++) {
            CustomWordsSynchronizerAction action = (CustomWordsSynchronizerAction) this._actions.get(i);
            this._listener.onError(this, action.ActionType, error);
        }
        this._actions.clear();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startTransaction(CustomWordsSynchronizerAction action) {
        if (!this._tr.isValid()) {
            Logger.error(this, "Unable to start custom words synchronize transaction. Transaction runner is invalid.");
            this._listener.onError(this, action.ActionType, new SpeechErrorImpl(0, null, null));
            return;
        }
        this._transactionError = null;
        this._currentTransaction = this._tr.createCustomWordsSynchronizeTransaction(action.DictationType, action.Language, action.CustomWordsAddSet, action.CustomWordsRemoveSet, this._transactionListener);
        if (this._currentTransaction != null) {
            this._currentTransaction.setNeedClearAllCustomWordsFlag(action.IsToClearAllCustomWords);
            this._currentTransaction.setDeleteAllFlag(action.IsToDeleteAllUserInformation);
            this._currentAction = action;
            this._currentTransaction.start();
            return;
        }
        Logger.error(this, "Unable to create custom words synchronize transaction.");
        this._listener.onError(this, action.ActionType, new SpeechErrorImpl(0, null, null));
    }

    @Override // com.nuance.nmdp.speechkit.CustomWordsSynchronizer
    public final void addCustomWordsSet(Set<String> words, boolean clearAllCustomWords) {
        CustomWordsSynchronizerAction action = new CustomWordsSynchronizerAction(0, this._dictationType, this._language, words, clearAllCustomWords);
        if (this._currentTransaction == null) {
            startTransaction(action);
        } else {
            this._actions.add(action);
        }
    }

    @Override // com.nuance.nmdp.speechkit.CustomWordsSynchronizer
    public final void removeCustomWordsSet(Set<String> words) {
        CustomWordsSynchronizerAction action = new CustomWordsSynchronizerAction(1, this._dictationType, this._language, words, false);
        if (this._currentTransaction == null) {
            startTransaction(action);
        } else {
            this._actions.add(action);
        }
    }

    @Override // com.nuance.nmdp.speechkit.CustomWordsSynchronizer
    public final void clearAllCustomWords() {
        CustomWordsSynchronizerAction action = new CustomWordsSynchronizerAction(2, this._dictationType, this._language, null, true);
        if (this._currentTransaction == null) {
            startTransaction(action);
        } else {
            this._actions.add(action);
        }
    }

    @Override // com.nuance.nmdp.speechkit.CustomWordsSynchronizer
    public final void deleteAllUserInformation() {
        CustomWordsSynchronizerAction action = new CustomWordsSynchronizerAction(3, this._dictationType, this._language, null, false);
        if (this._currentTransaction == null) {
            startTransaction(action);
        } else {
            this._actions.add(action);
        }
    }

    @Override // com.nuance.nmdp.speechkit.CustomWordsSynchronizer
    public final void setLanguage(String language) {
        this._language = language;
    }

    @Override // com.nuance.nmdp.speechkit.CustomWordsSynchronizer
    public final void setDictationType(String dictationType) {
        this._dictationType = dictationType;
    }

    @Override // com.nuance.nmdp.speechkit.CustomWordsSynchronizer
    public final void cancel() {
        if (this._currentTransaction != null) {
            this._currentTransaction.cancel();
        }
    }

    private ICustomWordsSynchronizerTransactionListener createTransactionListener() {
        return new ICustomWordsSynchronizerTransactionListener() { // from class: com.nuance.nmdp.speechkit.CustomWordsSynchronizerImpl.1
            @Override // com.nuance.nmdp.speechkit.transaction.ITransactionListener
            public void error(ITransaction t, int errorCode, String errorText, String suggestion) {
                if (CustomWordsSynchronizerImpl.this._currentTransaction == t) {
                    CustomWordsSynchronizerImpl.this._transactionError = new SpeechErrorImpl(errorCode, errorText, suggestion);
                }
            }

            @Override // com.nuance.nmdp.speechkit.transaction.ITransactionListener
            public void transactionDone(ITransaction t) {
                if (CustomWordsSynchronizerImpl.this._transactionError != null) {
                    if (CustomWordsSynchronizerImpl.this._currentTransaction == t) {
                        CustomWordsSynchronizerImpl.this.handleTransactionError(CustomWordsSynchronizerImpl.this._currentAction.ActionType, CustomWordsSynchronizerImpl.this._transactionError);
                        CustomWordsSynchronizerImpl.this._currentTransaction = null;
                        CustomWordsSynchronizerImpl.this._currentAction = null;
                        CustomWordsSynchronizerImpl.this._transactionError = null;
                    }
                } else if (CustomWordsSynchronizerImpl.this._currentTransaction == t) {
                    if (CustomWordsSynchronizerImpl.this._actions.size() > 0) {
                        CustomWordsSynchronizerAction action = (CustomWordsSynchronizerAction) CustomWordsSynchronizerImpl.this._actions.removeAt(0);
                        CustomWordsSynchronizerImpl.this.startTransaction(action);
                    } else {
                        CustomWordsSynchronizerImpl.this._currentTransaction = null;
                        CustomWordsSynchronizerImpl.this._currentAction = null;
                    }
                }
                if (CustomWordsSynchronizerImpl.this._currentTransaction == t) {
                    CustomWordsSynchronizerImpl.this._currentTransaction = null;
                }
            }

            @Override // com.nuance.nmdp.speechkit.transaction.custom_words_synchronize.ICustomWordsSynchronizerTransactionListener
            public void result(ITransaction t, CustomWordsSynchronizeResult result) {
                if (CustomWordsSynchronizerImpl.this._currentTransaction == t) {
                    CustomWordsSynchronizerImpl.this._listener.onResults(CustomWordsSynchronizerImpl.this, CustomWordsSynchronizerImpl.this._currentAction.ActionType, result);
                }
            }
        };
    }
}
