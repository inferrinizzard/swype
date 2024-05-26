package com.nuance.nmdp.speechkit.transaction.vocalize;

import com.nuance.nmdp.speechkit.transaction.TransactionBase;
import com.nuance.nmdp.speechkit.transaction.TransactionConfig;
import com.nuance.nmdp.speechkit.util.JobRunner;
import com.nuance.nmdp.speechkit.util.Logger;
import com.nuance.nmdp.speechkit.util.audio.IPlayerHelperListener;
import com.nuance.nmdp.speechkit.util.audio.PlayerHelper;
import com.nuance.nmsp.client.sdk.common.defines.NMSPDefines;
import com.nuance.nmsp.client.sdk.components.core.pdx.Dictionary;
import com.nuance.nmsp.client.sdk.components.general.NMSPAudioSink;
import com.nuance.nmsp.client.sdk.components.resource.common.Manager;
import com.nuance.swype.input.InputFieldInfo;

/* loaded from: classes.dex */
public final class VocalizeTransaction extends TransactionBase implements IVocalizeTransaction {
    private PlayerHelper _playerHelper;
    private final IPlayerHelperListener _playerListener;
    private final String _text;
    private final String _type;
    private final String _voice;

    public VocalizeTransaction(Manager mgr, TransactionConfig config, String text, String voice, String language, boolean markup, NMSPDefines.Codec audioCodec, IVocalizeTransactionListener listener) {
        super(mgr, config, language, listener);
        this._text = text;
        this._type = markup ? "ssml" : InputFieldInfo.INPUT_TYPE_TEXT;
        this._voice = voice;
        this._playerListener = createPlayerListener();
        this._playerHelper = new PlayerHelper(this._mgr, this, config.context(), this._playerListener, audioCodec);
        this._currentState = new IdleState(this);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.nmdp.speechkit.transaction.TransactionBase
    public final void addCustomKeys(Dictionary dict) {
        if (this._voice != null) {
            dict.addUTF8String("tts_voice", this._voice);
        } else {
            dict.addUTF8String("tts_language", this._language);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final String getCommandName() {
        return this._config.getTtsCmd();
    }

    public final String getText() {
        return this._text;
    }

    public final String getType() {
        return this._type;
    }

    public final void startPlayer() {
        this._playerHelper.startPlayer();
    }

    public final void stopPlayer() {
        this._playerHelper.stopPlayer();
    }

    public final NMSPAudioSink getAudioSink() {
        return this._playerHelper.getPlayer();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public IVocalizeTransactionState currentState() {
        return (IVocalizeTransactionState) this._currentState;
    }

    private IPlayerHelperListener createPlayerListener() {
        return new IPlayerHelperListener() { // from class: com.nuance.nmdp.speechkit.transaction.vocalize.VocalizeTransaction.1
            @Override // com.nuance.nmdp.speechkit.util.audio.IPlayerHelperListener
            public void error(Object context) {
                Logger.error(VocalizeTransaction.this, "Player error");
                JobRunner.addJob(new Runnable() { // from class: com.nuance.nmdp.speechkit.transaction.vocalize.VocalizeTransaction.1.1
                    @Override // java.lang.Runnable
                    public void run() {
                        VocalizeTransaction.this.currentState().audioError();
                    }
                });
            }

            @Override // com.nuance.nmdp.speechkit.util.audio.IPlayerHelperListener
            public void started(Object context) {
                JobRunner.addJob(new Runnable() { // from class: com.nuance.nmdp.speechkit.transaction.vocalize.VocalizeTransaction.1.2
                    @Override // java.lang.Runnable
                    public void run() {
                        VocalizeTransaction.this.currentState().audioStarted();
                    }
                });
            }

            @Override // com.nuance.nmdp.speechkit.util.audio.IPlayerHelperListener
            public void stopped(Object context) {
                JobRunner.addJob(new Runnable() { // from class: com.nuance.nmdp.speechkit.transaction.vocalize.VocalizeTransaction.1.3
                    @Override // java.lang.Runnable
                    public void run() {
                        VocalizeTransaction.this.currentState().audioStopped();
                    }
                });
            }
        };
    }
}
