package com.nuance.nmdp.speechkit.transaction.vocalize;

import com.nuance.nmdp.speechkit.util.Logger;
import com.nuance.nmsp.client.sdk.components.resource.nmas.QueryResult;

/* loaded from: classes.dex */
final class PlayingState extends VocalizeTransactionStateBase {
    private boolean _audioFinished;
    private boolean _commandFinished;

    public PlayingState(VocalizeTransaction transaction) {
        super(transaction);
        this._commandFinished = false;
        this._audioFinished = false;
    }

    @Override // com.nuance.nmdp.speechkit.transaction.ITransactionState
    public final void enter() {
        try {
            this._transaction.createNmasResource();
            this._transaction.createPdxCommand(this._transaction.getCommandName(), 30000);
            this._transaction.sendTTSParam(this._transaction.getType(), this._transaction.getText(), this._transaction.getAudioSink());
            this._transaction.endPdxCommand();
            this._transaction.startPlayer();
        } catch (Throwable tr) {
            Logger.error(this, "Error starting PlayingState", tr);
            error(4);
        }
    }

    @Override // com.nuance.nmdp.speechkit.transaction.TransactionStateBase, com.nuance.nmdp.speechkit.transaction.ITransactionState
    public final void leave() {
        this._transaction.stopPlayer();
    }

    @Override // com.nuance.nmdp.speechkit.transaction.TransactionStateBase, com.nuance.nmdp.speechkit.transaction.ITransactionState
    public final void cancel() {
        error(5);
    }

    @Override // com.nuance.nmdp.speechkit.transaction.vocalize.VocalizeTransactionStateBase, com.nuance.nmdp.speechkit.transaction.vocalize.IVocalizeTransactionState
    public final void audioError() {
        error(4, "audioError");
    }

    @Override // com.nuance.nmdp.speechkit.transaction.vocalize.VocalizeTransactionStateBase, com.nuance.nmdp.speechkit.transaction.vocalize.IVocalizeTransactionState
    public final void audioStarted() {
        getListener().audioStarted(this._transaction);
    }

    @Override // com.nuance.nmdp.speechkit.transaction.vocalize.VocalizeTransactionStateBase, com.nuance.nmdp.speechkit.transaction.vocalize.IVocalizeTransactionState
    public final void audioStopped() {
        this._audioFinished = true;
        if (this._commandFinished) {
            this._transaction.switchToState(new FinishedState(this._transaction));
        }
    }

    @Override // com.nuance.nmdp.speechkit.transaction.TransactionStateBase, com.nuance.nmdp.speechkit.transaction.ITransactionState
    public final void createCommandFailed() {
        error(4, "createCommandFailed");
    }

    @Override // com.nuance.nmdp.speechkit.transaction.TransactionStateBase, com.nuance.nmdp.speechkit.transaction.ITransactionState
    public final void commandEvent(short event) {
        error(1);
    }

    @Override // com.nuance.nmdp.speechkit.transaction.TransactionStateBase, com.nuance.nmdp.speechkit.transaction.ITransactionState
    public final void queryError(int code, String error) {
        error(4, error);
    }

    @Override // com.nuance.nmdp.speechkit.transaction.TransactionStateBase, com.nuance.nmdp.speechkit.transaction.ITransactionState
    public final void queryRetry(String prompt, String name) {
        error(2, null, prompt);
    }

    @Override // com.nuance.nmdp.speechkit.transaction.TransactionStateBase, com.nuance.nmdp.speechkit.transaction.ITransactionState
    public final void queryResult(QueryResult result) {
        this._commandFinished = true;
        if (this._audioFinished) {
            this._transaction.switchToState(new FinishedState(this._transaction));
        }
    }
}
