package com.nuance.nmdp.speechkit;

import com.nuance.nmdp.speechkit.Vocalizer;
import com.nuance.nmdp.speechkit.transaction.ITransaction;
import com.nuance.nmdp.speechkit.transaction.TransactionRunner;
import com.nuance.nmdp.speechkit.transaction.vocalize.IVocalizeTransaction;
import com.nuance.nmdp.speechkit.transaction.vocalize.IVocalizeTransactionListener;
import com.nuance.nmdp.speechkit.util.List;
import com.nuance.nmdp.speechkit.util.Logger;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class VocalizerImpl implements Vocalizer {
    private String _language;
    private final Vocalizer.Listener _listener;
    private final TransactionRunner _tr;
    private String _voice;
    private final IVocalizeTransactionListener _transactionListener = createTransactionListener();
    private IVocalizeTransaction _currentTransaction = null;
    private VocalizerAction _currentText = null;
    private SpeechError _transactionError = null;
    private final List _actions = new List();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class VocalizerAction {
        private final Object Context;
        private final boolean IsMarkup;
        private final String Language;
        private final String Text;
        private final String Voice;

        public VocalizerAction(boolean markup, String text, String voice, String language, Object context) {
            this.IsMarkup = markup;
            this.Text = text;
            this.Voice = voice;
            this.Language = language;
            this.Context = context;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public VocalizerImpl(TransactionRunner transactionRunner, String voice, String language, Vocalizer.Listener listener) {
        this._voice = voice;
        this._language = language;
        this._listener = listener;
        this._tr = transactionRunner;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleTransactionError(String text, Object context, SpeechError error) {
        this._listener.onSpeakingDone(this, text, error, context);
        int numActions = this._actions.size();
        for (int i = 0; i < numActions; i++) {
            VocalizerAction action = (VocalizerAction) this._actions.get(i);
            this._listener.onSpeakingDone(this, action.Text, error, action.Context);
        }
        this._actions.clear();
    }

    private IVocalizeTransactionListener createTransactionListener() {
        return new IVocalizeTransactionListener() { // from class: com.nuance.nmdp.speechkit.VocalizerImpl.1
            @Override // com.nuance.nmdp.speechkit.transaction.ITransactionListener
            public void transactionDone(ITransaction t) {
                if (VocalizerImpl.this._transactionError != null) {
                    if (VocalizerImpl.this._currentTransaction == t) {
                        VocalizerImpl.this.handleTransactionError(VocalizerImpl.this._currentText.Text, VocalizerImpl.this._currentText.Context, VocalizerImpl.this._transactionError);
                        VocalizerImpl.this._currentTransaction = null;
                        VocalizerImpl.this._currentText = null;
                        VocalizerImpl.this._transactionError = null;
                        return;
                    }
                    return;
                }
                if (VocalizerImpl.this._currentTransaction == t) {
                    VocalizerImpl.this._listener.onSpeakingDone(VocalizerImpl.this, VocalizerImpl.this._currentText.Text, null, VocalizerImpl.this._currentText.Context);
                    if (VocalizerImpl.this._actions.size() > 0) {
                        VocalizerAction act = (VocalizerAction) VocalizerImpl.this._actions.removeAt(0);
                        VocalizerImpl.this.startTransaction(act);
                    } else {
                        VocalizerImpl.this._currentTransaction = null;
                        VocalizerImpl.this._currentText = null;
                    }
                }
            }

            @Override // com.nuance.nmdp.speechkit.transaction.ITransactionListener
            public void error(ITransaction t, int code, String error, String suggestion) {
                if (VocalizerImpl.this._currentTransaction == t) {
                    VocalizerImpl.this._transactionError = new SpeechErrorImpl(code, error, suggestion);
                }
            }

            @Override // com.nuance.nmdp.speechkit.transaction.vocalize.IVocalizeTransactionListener
            public void audioStarted(ITransaction t) {
                if (VocalizerImpl.this._currentTransaction == t) {
                    VocalizerImpl.this._listener.onSpeakingBegin(VocalizerImpl.this, VocalizerImpl.this._currentText.Text, VocalizerImpl.this._currentText.Context);
                }
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startTransaction(VocalizerAction action) {
        if (this._tr.isValid()) {
            this._transactionError = null;
            this._currentTransaction = this._tr.createVocalizeTransaction(action.Text, action.Voice, action.Language, action.IsMarkup, this._transactionListener);
            if (this._currentTransaction == null) {
                Logger.error(this, "Unable to create TTS transaction");
                handleTransactionError(action.Text, action.Context, new SpeechErrorImpl(0, null, null));
                return;
            } else {
                this._currentText = action;
                this._currentTransaction.start();
                return;
            }
        }
        Logger.error(this, "Unable to create TTS transaction. Transaction runner is invalid.");
        handleTransactionError(action.Text, action.Context, new SpeechErrorImpl(0, null, null));
    }

    @Override // com.nuance.nmdp.speechkit.Vocalizer
    public final void speakString(String text, Object context) {
        VocalizerAction action = new VocalizerAction(false, text, this._voice, this._language, context);
        if (this._currentTransaction == null) {
            startTransaction(action);
        } else {
            this._actions.add(action);
        }
    }

    @Override // com.nuance.nmdp.speechkit.Vocalizer
    public final void speakMarkupString(String text, Object context) {
        VocalizerAction action = new VocalizerAction(true, text, this._voice, this._language, context);
        if (this._currentTransaction == null) {
            startTransaction(action);
        } else {
            this._actions.add(action);
        }
    }

    @Override // com.nuance.nmdp.speechkit.Vocalizer
    public final void cancel() {
        if (this._currentTransaction != null) {
            this._currentTransaction.cancel();
        }
    }

    @Override // com.nuance.nmdp.speechkit.Vocalizer
    public final void setLanguage(String language) {
        this._language = language;
        this._voice = null;
    }

    @Override // com.nuance.nmdp.speechkit.Vocalizer
    public final void setVoice(String voice) {
        this._language = null;
        this._voice = voice;
    }

    @Override // com.nuance.nmdp.speechkit.Vocalizer
    public final void setListener(Vocalizer.Listener listener) {
    }
}
