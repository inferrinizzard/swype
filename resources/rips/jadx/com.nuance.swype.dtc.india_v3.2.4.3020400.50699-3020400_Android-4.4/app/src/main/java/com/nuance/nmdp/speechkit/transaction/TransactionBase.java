package com.nuance.nmdp.speechkit.transaction;

import com.nuance.nmdp.speechkit.util.JobRunner;
import com.nuance.nmdp.speechkit.util.Logger;
import com.nuance.nmsp.client.sdk.common.defines.NMSPDefines;
import com.nuance.nmsp.client.sdk.components.core.pdx.Dictionary;
import com.nuance.nmsp.client.sdk.components.core.pdx.Sequence;
import com.nuance.nmsp.client.sdk.components.general.NMSPAudioSink;
import com.nuance.nmsp.client.sdk.components.general.Parameter;
import com.nuance.nmsp.client.sdk.components.general.TransactionAlreadyFinishedException;
import com.nuance.nmsp.client.sdk.components.general.TransactionExpiredException;
import com.nuance.nmsp.client.sdk.components.resource.common.Manager;
import com.nuance.nmsp.client.sdk.components.resource.common.ResourceException;
import com.nuance.nmsp.client.sdk.components.resource.nmas.AudioParam;
import com.nuance.nmsp.client.sdk.components.resource.nmas.Command;
import com.nuance.nmsp.client.sdk.components.resource.nmas.NMASResource;
import com.nuance.nmsp.client.sdk.components.resource.nmas.NMASResourceFactory;
import com.nuance.nmsp.client.sdk.components.resource.nmas.NMASResourceListener;
import com.nuance.nmsp.client.sdk.components.resource.nmas.PDXCommandListener;
import com.nuance.nmsp.client.sdk.components.resource.nmas.QueryError;
import com.nuance.nmsp.client.sdk.components.resource.nmas.QueryResult;
import com.nuance.nmsp.client.sdk.components.resource.nmas.QueryRetry;
import java.util.Vector;

/* loaded from: classes.dex */
public abstract class TransactionBase implements ITransaction {
    private Command _cmd;
    private final PDXCommandListener _cmdListener;
    public final TransactionConfig _config;
    public ITransactionState _currentState;
    public final String _language;
    private ITransactionListener _listener;
    public final Manager _mgr;
    private final NMASResourceListener _resourceListener;
    protected NMASResource _rsc;

    public TransactionBase(Manager mgr, TransactionConfig config, ITransactionListener listener) {
        this(mgr, config, config.defaultLanguage(), listener);
    }

    public TransactionBase(Manager mgr, TransactionConfig config, String language, ITransactionListener listener) {
        this._listener = listener;
        this._mgr = mgr;
        this._config = config;
        this._language = language == null ? config.defaultLanguage() : language;
        this._resourceListener = createNmasResourceListener();
        this._cmdListener = createCommandListener();
        this._rsc = null;
        this._cmd = null;
    }

    @Override // com.nuance.nmdp.speechkit.transaction.ITransaction
    public void start() {
        this._currentState.start();
    }

    @Override // com.nuance.nmdp.speechkit.transaction.ITransaction
    public void cancel() {
        this._currentState.cancel();
    }

    public ITransactionListener getListener() {
        return this._listener;
    }

    public void switchToState(ITransactionState newState) {
        this._currentState.leave();
        this._currentState = newState;
        this._currentState.enter();
    }

    public void createNmasResource() {
        Vector<Parameter> params = new Vector<>();
        params.add(new Parameter(NMSPDefines.NMSP_DEFINES_ANDROID_CONTEXT, this._config.context(), Parameter.Type.SDK));
        this._rsc = NMASResourceFactory.createNMASResource(this._mgr, this._resourceListener, params, this._config.applicationId());
    }

    public void freeNmasResource() {
        if (this._rsc != null) {
            try {
                this._rsc.freeResource(0);
            } catch (ResourceException e) {
            }
            this._rsc = null;
        }
    }

    public void addCustomKeys(Dictionary dict) {
    }

    public void createPdxCommand(String command, int timeout) throws ResourceException {
        if (this._rsc != null) {
            Dictionary dict = createDictionary();
            String carrier = this._config.carrier();
            String model = this._config.model();
            String os = this._config.os();
            String locale = this._config.locale();
            String networkType = this._config.networkType();
            String client = this._config.clientVersion();
            String nmdpVersion = this._config.nmdpVersion();
            String appId = this._config.applicationId();
            String subscriptionId = this._config.subscriptionId();
            dict.addUTF8String("ui_language", this._language.substring(0, 2).toLowerCase());
            dict.addUTF8String("phone_submodel", model);
            dict.addUTF8String("phone_OS", os);
            dict.addUTF8String("locale", locale);
            dict.addUTF8String("nmdp_version", nmdpVersion);
            dict.addUTF8String("nmaid", appId);
            dict.addUTF8String("network_type", networkType);
            if (subscriptionId != null) {
                dict.addUTF8String("subscriber_id", subscriptionId);
            }
            byte[] transactionId = new byte[100];
            try {
                transactionId = this._config.message(transactionId, null);
            } catch (Throwable th) {
            }
            dict.addByteString("app_transaction_id", transactionId);
            addCustomKeys(dict);
            this._cmd = this._rsc.createCommand(this._cmdListener, command, client, this._language, carrier, model, dict, timeout);
        }
    }

    public void endPdxCommand() throws TransactionException {
        try {
            this._cmd.end();
        } catch (TransactionAlreadyFinishedException e) {
            throw new TransactionException("Error ending PDX command (TransactionAlreadyFinishedException)", e);
        } catch (TransactionExpiredException e2) {
            throw new TransactionException("Error ending PDX command (TransactionExpiredException)", e2);
        }
    }

    public AudioParam sendAudioParam(String name) throws TransactionException {
        AudioParam p = this._rsc.createPDXAudioParam(name);
        sendParam(p);
        return p;
    }

    public void sendTextParam(String name, String text) throws TransactionException {
        com.nuance.nmsp.client.sdk.components.resource.nmas.Parameter p = this._rsc.createPDXTextParam(name, text);
        sendParam(p);
    }

    public void sendDictParam(String name, Dictionary dictionary) throws TransactionException {
        com.nuance.nmsp.client.sdk.components.resource.nmas.Parameter p = this._rsc.createPDXDictParam(name, dictionary);
        sendParam(p);
    }

    public void sendSeqStartParam(String name, Dictionary dictionary) throws TransactionException {
        com.nuance.nmsp.client.sdk.components.resource.nmas.Parameter p = this._rsc.createPDXSeqStartParam(name, dictionary);
        sendParam(p);
    }

    public void sendSeqChunkParam(String name, Dictionary dictionary) throws TransactionException {
        com.nuance.nmsp.client.sdk.components.resource.nmas.Parameter p = this._rsc.createPDXSeqChunkParam(name, dictionary);
        sendParam(p);
    }

    public void sendSeqEndParam(String name, Dictionary dictionary) throws TransactionException {
        com.nuance.nmsp.client.sdk.components.resource.nmas.Parameter p = this._rsc.createPDXSeqEndParam(name, dictionary);
        sendParam(p);
    }

    public void sendTTSParam(String type, String text, NMSPAudioSink audioSink) throws TransactionException {
        Dictionary dict = createDictionary();
        dict.addUTF8String("tts_input", text);
        dict.addUTF8String("tts_type", type);
        com.nuance.nmsp.client.sdk.components.resource.nmas.Parameter p = this._rsc.createPDXTTSParam("TEXT_TO_READ", dict, audioSink);
        sendParam(p);
    }

    public void sendParam(com.nuance.nmsp.client.sdk.components.resource.nmas.Parameter param) throws TransactionException {
        try {
            this._cmd.sendParam(param);
        } catch (TransactionAlreadyFinishedException e) {
            throw new TransactionException("Error sending parameter (TransactionAlreadyFinishedException)", e);
        } catch (TransactionExpiredException e2) {
            throw new TransactionException("Error sending parameter (TransactionExpiredException)", e2);
        }
    }

    public final Dictionary createDictionary() {
        return this._rsc.createPDXDictionary();
    }

    public final Sequence createSequence() {
        return this._rsc.createPDXSequence();
    }

    private NMASResourceListener createNmasResourceListener() {
        return new NMASResourceListener() { // from class: com.nuance.nmdp.speechkit.transaction.TransactionBase.1
            @Override // com.nuance.nmsp.client.sdk.components.resource.nmas.NMASResourceListener
            public void PDXCommandCreated(final String sessionId) {
                JobRunner.addJob(new Runnable() { // from class: com.nuance.nmdp.speechkit.transaction.TransactionBase.1.1
                    @Override // java.lang.Runnable
                    public void run() {
                        TransactionBase.this._currentState.commandCreated(sessionId);
                    }
                });
            }

            @Override // com.nuance.nmsp.client.sdk.components.resource.nmas.NMASResourceListener
            public void PDXCreateCommandFailed() {
                Logger.error(TransactionBase.this, "PDX Create Command Failed");
                JobRunner.addJob(new Runnable() { // from class: com.nuance.nmdp.speechkit.transaction.TransactionBase.1.2
                    @Override // java.lang.Runnable
                    public void run() {
                        TransactionBase.this._currentState.createCommandFailed();
                    }
                });
            }

            @Override // com.nuance.nmsp.client.sdk.components.resource.common.ResourceListener
            public void getParameterCompleted(short arg0, Vector arg1, long arg2) {
            }

            @Override // com.nuance.nmsp.client.sdk.components.resource.common.ResourceListener
            public void getParameterFailed(short arg0, short arg1, long arg2) {
            }

            @Override // com.nuance.nmsp.client.sdk.components.resource.common.ResourceListener
            public void resourceUnloaded(short arg0) {
            }

            @Override // com.nuance.nmsp.client.sdk.components.resource.common.ResourceListener
            public void setParameterCompleted(short arg0, Vector arg1, long arg2) {
            }

            @Override // com.nuance.nmsp.client.sdk.components.resource.common.ResourceListener
            public void setParameterFailed(short arg0, short arg1, long arg2) {
            }
        };
    }

    private PDXCommandListener createCommandListener() {
        return new PDXCommandListener() { // from class: com.nuance.nmdp.speechkit.transaction.TransactionBase.2
            @Override // com.nuance.nmsp.client.sdk.components.resource.nmas.PDXCommandListener
            public void PDXCommandEvent(final short event) {
                switch (event) {
                    case 1:
                    case 3:
                        Logger.info(TransactionBase.this, "PDX Command Event: " + ((int) event));
                        JobRunner.addJob(new Runnable() { // from class: com.nuance.nmdp.speechkit.transaction.TransactionBase.2.1
                            @Override // java.lang.Runnable
                            public void run() {
                                TransactionBase.this._currentState.commandEvent(event);
                            }
                        });
                        return;
                    case 2:
                    default:
                        return;
                }
            }

            @Override // com.nuance.nmsp.client.sdk.components.resource.nmas.PDXCommandListener
            public void PDXQueryErrorReturned(final QueryError error) {
                Logger.info(TransactionBase.this, "PDX Query Error Returned: " + error.getError() + "(" + error.getDescription() + ")");
                JobRunner.addJob(new Runnable() { // from class: com.nuance.nmdp.speechkit.transaction.TransactionBase.2.2
                    @Override // java.lang.Runnable
                    public void run() {
                        TransactionBase.this._currentState.queryError(error.getError(), error.getDescription());
                    }
                });
            }

            @Override // com.nuance.nmsp.client.sdk.components.resource.nmas.PDXCommandListener
            public void PDXQueryResultReturned(final QueryResult result) {
                Logger.info(TransactionBase.this, "PDX Query Result Returned");
                JobRunner.addJob(new Runnable() { // from class: com.nuance.nmdp.speechkit.transaction.TransactionBase.2.3
                    @Override // java.lang.Runnable
                    public void run() {
                        TransactionBase.this._currentState.queryResult(result);
                    }
                });
            }

            @Override // com.nuance.nmsp.client.sdk.components.resource.nmas.PDXCommandListener
            public void PDXQueryRetryReturned(QueryRetry retry) {
                Logger.info(TransactionBase.this, "PDX Query Retry Returned: " + retry.getCause() + "(" + retry.getName() + ")");
                final String prompt = retry.getPrompt();
                final String name = retry.getName();
                JobRunner.addJob(new Runnable() { // from class: com.nuance.nmdp.speechkit.transaction.TransactionBase.2.4
                    @Override // java.lang.Runnable
                    public void run() {
                        TransactionBase.this._currentState.queryRetry(prompt, name);
                    }
                });
            }
        };
    }
}
