package com.nuance.nmdp.speechkit.transaction;

import com.nuance.nmdp.speechkit.CustomWordsSynchronizeResult;
import com.nuance.nmdp.speechkit.SpeechKit;
import com.nuance.nmdp.speechkit.TextContext;
import com.nuance.nmdp.speechkit.transaction.connect.ConnectTransaction;
import com.nuance.nmdp.speechkit.transaction.custom_words_synchronize.CustomWordsSynchronizeTransaction;
import com.nuance.nmdp.speechkit.transaction.custom_words_synchronize.ICustomWordsSynchronizerTransaction;
import com.nuance.nmdp.speechkit.transaction.custom_words_synchronize.ICustomWordsSynchronizerTransactionListener;
import com.nuance.nmdp.speechkit.transaction.generic.GenericTransaction;
import com.nuance.nmdp.speechkit.transaction.generic.IGenericParam;
import com.nuance.nmdp.speechkit.transaction.generic.logrevision.LogRevisionTransaction;
import com.nuance.nmdp.speechkit.transaction.languages.LanguagesTransaction;
import com.nuance.nmdp.speechkit.transaction.ping.PingTransaction;
import com.nuance.nmdp.speechkit.transaction.recognize.IRecognizeTransaction;
import com.nuance.nmdp.speechkit.transaction.recognize.IRecognizeTransactionListener;
import com.nuance.nmdp.speechkit.transaction.recognize.ISignalEnergyListener;
import com.nuance.nmdp.speechkit.transaction.recognize.nlu.NluRecognizeTransaction;
import com.nuance.nmdp.speechkit.transaction.recognize.nmdp.NmdpRecognizeTransaction;
import com.nuance.nmdp.speechkit.transaction.recognize.nmdp.TextContextNmdpRecognizeTransaction;
import com.nuance.nmdp.speechkit.transaction.recognize.text.TextRecognizeTransaction;
import com.nuance.nmdp.speechkit.transaction.vocalize.IVocalizeTransaction;
import com.nuance.nmdp.speechkit.transaction.vocalize.IVocalizeTransactionListener;
import com.nuance.nmdp.speechkit.transaction.vocalize.VocalizeTransaction;
import com.nuance.nmdp.speechkit.util.List;
import com.nuance.nmdp.speechkit.util.Logger;
import com.nuance.nmdp.speechkit.util.audio.IPrompt;
import com.nuance.nmdp.speechkit.util.parsers.IPdxParser;
import com.nuance.nmdp.speechkit.util.pdx.PdxValue;
import com.nuance.nmsp.client.sdk.common.defines.NMSPDefines;
import com.nuance.nmsp.client.sdk.components.core.calllog.CalllogSender;
import com.nuance.nmsp.client.sdk.components.core.calllog.SessionEvent;
import com.nuance.nmsp.client.sdk.components.general.Parameter;
import com.nuance.nmsp.client.sdk.components.resource.common.Manager;
import com.nuance.nmsp.client.sdk.components.resource.common.ManagerFactory;
import com.nuance.nmsp.client.sdk.components.resource.common.ManagerListener;
import com.nuance.nmsp.client.sdk.components.resource.common.Resource;
import java.util.Set;

/* loaded from: classes.dex */
public class TransactionRunner {
    private TransactionConfig _config;
    private boolean _isValid;
    private NMSPDefines.Codec _playerCodec;
    private NMSPDefines.Codec _recorderCodec;
    private SessionEvent _appLogSession = null;
    private ITransaction _currentTransaction = null;
    private final ManagerListener _managerListener = createManagerListener();
    private final CalllogSender.SenderListener _senderListener = createSenderListener();
    private String _sessionId = null;
    private final Object _sessionIdSync = new Object();
    private Manager _nmspManager = createManager();

    public TransactionRunner(TransactionConfig config) {
        this._config = config;
        this._isValid = true;
        this._playerCodec = this._config.playerCodec();
        this._recorderCodec = this._config.recorderCodec();
        if (this._nmspManager == null) {
            this._isValid = false;
            this._config = null;
        }
    }

    private Manager createManager() {
        try {
            short port = (short) this._config.port();
            String host = this._config.host();
            String apn = this._config.apn();
            boolean ssl = this._config.ssl();
            List params = new List();
            params.add(new Parameter(NMSPDefines.NMSP_DEFINES_ANDROID_CONTEXT, this._config.context(), Parameter.Type.SDK));
            if (apn != null) {
                params.add(new Parameter(NMSPDefines.SocketConnectionSetting, apn.getBytes(), Parameter.Type.SDK));
            }
            if (ssl) {
                params.add(new Parameter(NMSPDefines.NMSP_DEFINES_SSL_SOCKET, "TRUE".getBytes(), Parameter.Type.SDK));
            }
            params.add(new Parameter(NMSPDefines.NMSP_DEFINES_CALLLOG_DISABLE, "TRUE".getBytes(), Parameter.Type.SDK));
            return ManagerFactory.createManager(host, port, this._config.applicationId(), this._config.applicationKey(), this._config.uid(), this._recorderCodec, this._playerCodec, params.toVector(), this._managerListener);
        } catch (Throwable tr) {
            Logger.error(this, "Unable to create NMSP manager", tr);
            return null;
        }
    }

    private void checkManager() {
        NMSPDefines.Codec playerCodec = this._config.playerCodec();
        NMSPDefines.Codec recorderCodec = this._config.recorderCodec();
        if (this._playerCodec != playerCodec || this._recorderCodec != recorderCodec) {
            Logger.warn(this, "Supported codecs changed, restarting NMSP manager");
            this._currentTransaction = null;
            this._playerCodec = playerCodec;
            this._recorderCodec = recorderCodec;
            this._nmspManager.shutdown();
            this._nmspManager = createManager();
            if (this._nmspManager == null) {
                this._config = null;
                this._isValid = false;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void restartManager() {
        Logger.warn(this, "Restarting NMSP manager");
        this._playerCodec = this._config.playerCodec();
        this._recorderCodec = this._config.recorderCodec();
        this._currentTransaction = null;
        this._nmspManager.shutdown();
        this._nmspManager = createManager();
        if (this._nmspManager == null) {
            this._config = null;
            this._isValid = false;
        }
    }

    public void dispose() {
        this._isValid = false;
        if (this._currentTransaction != null) {
            this._currentTransaction.cancel();
            this._currentTransaction = null;
        }
        if (this._nmspManager != null) {
            this._nmspManager.shutdown();
            this._nmspManager = null;
        }
        this._config = null;
    }

    public boolean isValid() {
        return this._isValid;
    }

    public IRecognizeTransaction createNmdpRecognizeTransaction(String type, int detectionType, int endOfSpeechDuration, int startOfSpeechTimeout, SpeechKit.PartialResultsMode partialResultsMode, String language, IPrompt startPrompt, IPrompt stopPrompt, IPrompt resultPrompt, IPrompt errorPrompt, IPdxParser<?> resultParser, ISignalEnergyListener energyListener, final IRecognizeTransactionListener listener) {
        if (!this._isValid) {
            return null;
        }
        if (this._currentTransaction != null) {
            this._currentTransaction.cancel();
        }
        checkManager();
        NmdpRecognizeTransaction t = new NmdpRecognizeTransaction(this._nmspManager, this._config, type, detectionType, endOfSpeechDuration, startOfSpeechTimeout, partialResultsMode, language, null, startPrompt, stopPrompt, resultPrompt, errorPrompt, resultParser, energyListener, new IRecognizeTransactionListener() { // from class: com.nuance.nmdp.speechkit.transaction.TransactionRunner.1
            @Override // com.nuance.nmdp.speechkit.transaction.ITransactionListener
            public void transactionDone(ITransaction t2) {
                TransactionRunner.this.transactionDone(t2);
                listener.transactionDone(t2);
            }

            @Override // com.nuance.nmdp.speechkit.transaction.ITransactionListener
            public void error(ITransaction t2, int errorCode, String errorText, String suggestion) {
                listener.error(t2, errorCode, errorText, suggestion);
                if (errorCode == 1) {
                    TransactionRunner.this.restartManager();
                }
            }

            @Override // com.nuance.nmdp.speechkit.transaction.recognize.IRecognizeTransactionListener
            public void recordingStarted(ITransaction t2) {
                listener.recordingStarted(t2);
            }

            @Override // com.nuance.nmdp.speechkit.transaction.recognize.IRecognizeTransactionListener
            public void recordingStopped(ITransaction t2) {
                listener.recordingStopped(t2);
            }

            @Override // com.nuance.nmdp.speechkit.transaction.recognize.IRecognizeTransactionListener
            public void results(ITransaction t2) {
                listener.results(t2);
            }
        });
        this._currentTransaction = t;
        return t;
    }

    public IRecognizeTransaction createConstraintRecognizeTransaction(String type, int detectionType, int endOfSpeechDuration, int startOfSpeechTimeout, SpeechKit.PartialResultsMode partialResultsMode, String language, PdxValue.Sequence grammarList, IPrompt startPrompt, IPrompt stopPrompt, IPrompt resultPrompt, IPrompt errorPrompt, IPdxParser<?> resultParser, ISignalEnergyListener energyListener, final IRecognizeTransactionListener listener) {
        if (!this._isValid) {
            return null;
        }
        if (this._currentTransaction != null) {
            this._currentTransaction.cancel();
        }
        checkManager();
        NmdpRecognizeTransaction t = new NmdpRecognizeTransaction(this._nmspManager, this._config, type, detectionType, endOfSpeechDuration, startOfSpeechTimeout, partialResultsMode, language, grammarList, startPrompt, stopPrompt, resultPrompt, errorPrompt, resultParser, energyListener, new IRecognizeTransactionListener() { // from class: com.nuance.nmdp.speechkit.transaction.TransactionRunner.2
            @Override // com.nuance.nmdp.speechkit.transaction.ITransactionListener
            public void transactionDone(ITransaction t2) {
                TransactionRunner.this.transactionDone(t2);
                listener.transactionDone(t2);
            }

            @Override // com.nuance.nmdp.speechkit.transaction.ITransactionListener
            public void error(ITransaction t2, int errorCode, String errorText, String suggestion) {
                listener.error(t2, errorCode, errorText, suggestion);
                if (errorCode == 1) {
                    TransactionRunner.this.restartManager();
                }
            }

            @Override // com.nuance.nmdp.speechkit.transaction.recognize.IRecognizeTransactionListener
            public void recordingStarted(ITransaction t2) {
                listener.recordingStarted(t2);
            }

            @Override // com.nuance.nmdp.speechkit.transaction.recognize.IRecognizeTransactionListener
            public void recordingStopped(ITransaction t2) {
                listener.recordingStopped(t2);
            }

            @Override // com.nuance.nmdp.speechkit.transaction.recognize.IRecognizeTransactionListener
            public void results(ITransaction t2) {
                listener.results(t2);
            }
        });
        this._currentTransaction = t;
        return t;
    }

    public IRecognizeTransaction createNluRecognizeTransaction(String type, int detectionType, String language, String appSessionId, PdxValue.Dictionary requestParams, IPrompt startPrompt, IPrompt stopPrompt, IPrompt resultPrompt, IPrompt errorPrompt, IPdxParser<?> resultParser, ISignalEnergyListener energyListener, final IRecognizeTransactionListener listener) {
        if (!this._isValid) {
            return null;
        }
        if (this._currentTransaction != null) {
            this._currentTransaction.cancel();
        }
        checkManager();
        NluRecognizeTransaction t = new NluRecognizeTransaction(this._nmspManager, this._config, type, detectionType, language, appSessionId, requestParams, startPrompt, stopPrompt, resultPrompt, errorPrompt, resultParser, energyListener, new IRecognizeTransactionListener() { // from class: com.nuance.nmdp.speechkit.transaction.TransactionRunner.3
            @Override // com.nuance.nmdp.speechkit.transaction.ITransactionListener
            public void transactionDone(ITransaction t2) {
                TransactionRunner.this.transactionDone(t2);
                listener.transactionDone(t2);
            }

            @Override // com.nuance.nmdp.speechkit.transaction.ITransactionListener
            public void error(ITransaction t2, int errorCode, String errorText, String suggestion) {
                listener.error(t2, errorCode, errorText, suggestion);
                if (errorCode == 1) {
                    TransactionRunner.this.restartManager();
                }
            }

            @Override // com.nuance.nmdp.speechkit.transaction.recognize.IRecognizeTransactionListener
            public void recordingStarted(ITransaction t2) {
                listener.recordingStarted(t2);
            }

            @Override // com.nuance.nmdp.speechkit.transaction.recognize.IRecognizeTransactionListener
            public void recordingStopped(ITransaction t2) {
                listener.recordingStopped(t2);
            }

            @Override // com.nuance.nmdp.speechkit.transaction.recognize.IRecognizeTransactionListener
            public void results(ITransaction t2) {
                listener.results(t2);
            }
        });
        this._currentTransaction = t;
        return t;
    }

    public IRecognizeTransaction createTextRecognizeTransaction(String language, String appSessionId, PdxValue.Dictionary requestParams, java.util.List<IGenericParam> grammarParams, IPdxParser<?> resultParser, ISignalEnergyListener energyListener, final IRecognizeTransactionListener listener) {
        if (!this._isValid) {
            return null;
        }
        if (this._currentTransaction != null) {
            this._currentTransaction.cancel();
        }
        checkManager();
        TextRecognizeTransaction t = new TextRecognizeTransaction(this._nmspManager, this._config, language, appSessionId, requestParams, grammarParams, resultParser, energyListener, new IRecognizeTransactionListener() { // from class: com.nuance.nmdp.speechkit.transaction.TransactionRunner.4
            @Override // com.nuance.nmdp.speechkit.transaction.ITransactionListener
            public void transactionDone(ITransaction t2) {
                TransactionRunner.this.transactionDone(t2);
                listener.transactionDone(t2);
            }

            @Override // com.nuance.nmdp.speechkit.transaction.ITransactionListener
            public void error(ITransaction t2, int errorCode, String errorText, String suggestion) {
                listener.error(t2, errorCode, errorText, suggestion);
                if (errorCode == 1) {
                    TransactionRunner.this.restartManager();
                }
            }

            @Override // com.nuance.nmdp.speechkit.transaction.recognize.IRecognizeTransactionListener
            public void recordingStarted(ITransaction t2) {
                listener.recordingStarted(t2);
            }

            @Override // com.nuance.nmdp.speechkit.transaction.recognize.IRecognizeTransactionListener
            public void recordingStopped(ITransaction t2) {
                listener.recordingStopped(t2);
            }

            @Override // com.nuance.nmdp.speechkit.transaction.recognize.IRecognizeTransactionListener
            public void results(ITransaction t2) {
                listener.results(t2);
            }
        });
        this._currentTransaction = t;
        return t;
    }

    public ITransaction createGenericTransaction(String name, java.util.List<IGenericParam> params, IPdxParser<?> resultParser, final ITransactionListener listener) {
        if (!this._isValid) {
            return null;
        }
        if (this._currentTransaction != null) {
            this._currentTransaction.cancel();
        }
        checkManager();
        GenericTransaction t = new GenericTransaction(this._nmspManager, this._config, name, params, resultParser, new ITransactionListener() { // from class: com.nuance.nmdp.speechkit.transaction.TransactionRunner.5
            @Override // com.nuance.nmdp.speechkit.transaction.ITransactionListener
            public void error(ITransaction t2, int errorCode, String errorText, String suggestion) {
                listener.error(t2, errorCode, errorText, suggestion);
                if (errorCode == 1) {
                    TransactionRunner.this.restartManager();
                }
            }

            @Override // com.nuance.nmdp.speechkit.transaction.ITransactionListener
            public void transactionDone(ITransaction t2) {
                TransactionRunner.this.transactionDone(t2);
                listener.transactionDone(t2);
            }
        });
        this._currentTransaction = t;
        return t;
    }

    public ITransaction createLogRevisionTransaction(java.util.List<IGenericParam> params, String appSessionId, IPdxParser<?> resultParser, final ITransactionListener listener) {
        if (!this._isValid) {
            return null;
        }
        if (this._currentTransaction != null) {
            this._currentTransaction.cancel();
        }
        checkManager();
        GenericTransaction t = new LogRevisionTransaction(this._nmspManager, this._config, params, appSessionId, resultParser, new ITransactionListener() { // from class: com.nuance.nmdp.speechkit.transaction.TransactionRunner.6
            @Override // com.nuance.nmdp.speechkit.transaction.ITransactionListener
            public void error(ITransaction t2, int errorCode, String errorText, String suggestion) {
                listener.error(t2, errorCode, errorText, suggestion);
                if (errorCode == 1) {
                    TransactionRunner.this.restartManager();
                }
            }

            @Override // com.nuance.nmdp.speechkit.transaction.ITransactionListener
            public void transactionDone(ITransaction t2) {
                TransactionRunner.this.transactionDone(t2);
                listener.transactionDone(t2);
            }
        });
        this._currentTransaction = t;
        return t;
    }

    public ITransaction createLanguageTransaction(java.util.List<IGenericParam> params, IPdxParser<?> resultParser, final ITransactionListener listener) {
        if (!this._isValid) {
            return null;
        }
        if (this._currentTransaction != null) {
            this._currentTransaction.cancel();
        }
        checkManager();
        LanguagesTransaction t = new LanguagesTransaction(params, resultParser, this._nmspManager, this._config, new ITransactionListener() { // from class: com.nuance.nmdp.speechkit.transaction.TransactionRunner.7
            @Override // com.nuance.nmdp.speechkit.transaction.ITransactionListener
            public void error(ITransaction t2, int errorCode, String errorText, String suggestion) {
                listener.error(t2, errorCode, errorText, suggestion);
                if (errorCode == 1) {
                    TransactionRunner.this.restartManager();
                }
            }

            @Override // com.nuance.nmdp.speechkit.transaction.ITransactionListener
            public void transactionDone(ITransaction t2) {
                TransactionRunner.this.transactionDone(t2);
                listener.transactionDone(t2);
            }
        });
        this._currentTransaction = t;
        return t;
    }

    public IVocalizeTransaction createVocalizeTransaction(String text, String voice, String language, boolean markup, final IVocalizeTransactionListener listener) {
        if (!this._isValid) {
            return null;
        }
        if (this._currentTransaction != null) {
            this._currentTransaction.cancel();
        }
        checkManager();
        IVocalizeTransaction t = new VocalizeTransaction(this._nmspManager, this._config, text, voice, language, markup, this._playerCodec, new IVocalizeTransactionListener() { // from class: com.nuance.nmdp.speechkit.transaction.TransactionRunner.8
            @Override // com.nuance.nmdp.speechkit.transaction.ITransactionListener
            public void error(ITransaction t2, int errorCode, String errorText, String suggestion) {
                listener.error(t2, errorCode, errorText, suggestion);
                if (errorCode == 1) {
                    TransactionRunner.this.restartManager();
                }
            }

            @Override // com.nuance.nmdp.speechkit.transaction.ITransactionListener
            public void transactionDone(ITransaction t2) {
                TransactionRunner.this.transactionDone(t2);
                listener.transactionDone(t2);
            }

            @Override // com.nuance.nmdp.speechkit.transaction.vocalize.IVocalizeTransactionListener
            public void audioStarted(ITransaction t2) {
                listener.audioStarted(t2);
            }
        });
        this._currentTransaction = t;
        return t;
    }

    public ITransaction createConnectTransaction(final ITransactionListener listener, boolean ping) {
        ITransaction t = null;
        if (this._isValid && this._currentTransaction == null) {
            checkManager();
            ITransactionListener l = new ITransactionListener() { // from class: com.nuance.nmdp.speechkit.transaction.TransactionRunner.9
                @Override // com.nuance.nmdp.speechkit.transaction.ITransactionListener
                public void error(ITransaction t2, int errorCode, String errorText, String suggestion) {
                    listener.error(t2, errorCode, errorText, suggestion);
                    if (errorCode == 1) {
                        TransactionRunner.this.restartManager();
                    }
                }

                @Override // com.nuance.nmdp.speechkit.transaction.ITransactionListener
                public void transactionDone(ITransaction t2) {
                    TransactionRunner.this.transactionDone(t2);
                    listener.transactionDone(t2);
                }
            };
            t = ping ? new PingTransaction(this._nmspManager, this._config, l) : new ConnectTransaction(this._nmspManager, this._config, l);
            this._currentTransaction = t;
        }
        return t;
    }

    public String getSessionId() {
        String str;
        synchronized (this._sessionIdSync) {
            str = this._sessionId;
        }
        return str;
    }

    public void cancelCurrent() {
        if (this._currentTransaction != null) {
            this._currentTransaction.cancel();
            this._currentTransaction = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void transactionDone(ITransaction t) {
        if (t == this._currentTransaction) {
            this._currentTransaction = null;
        }
    }

    private ManagerListener createManagerListener() {
        return new ManagerListener() { // from class: com.nuance.nmdp.speechkit.transaction.TransactionRunner.10
            @Override // com.nuance.nmsp.client.sdk.components.resource.common.ManagerListener
            public void connected(String sessionId, Resource resource) {
                synchronized (TransactionRunner.this._sessionIdSync) {
                    Logger.info(TransactionRunner.this, "Connected with session ID " + sessionId);
                    TransactionRunner.this._sessionId = sessionId;
                }
            }

            @Override // com.nuance.nmsp.client.sdk.components.resource.common.ManagerListener
            public void connectionFailed(Resource resource, short reason) {
                Logger.warn(TransactionRunner.this, "Connection failed reasoncode [" + ((int) reason) + "]");
            }

            @Override // com.nuance.nmsp.client.sdk.components.resource.common.ManagerListener
            public void disconnected(Resource resource, short reason) {
                Logger.info(TransactionRunner.this, "Disconnected reasoncode [" + ((int) reason) + "]");
            }

            @Override // com.nuance.nmsp.client.sdk.components.resource.common.ManagerListener
            public void shutdownCompleted() {
            }

            @Override // com.nuance.nmsp.client.sdk.components.resource.common.ManagerListener
            public void callLogDataGenerated(byte[] callLogData) {
            }
        };
    }

    private CalllogSender.SenderListener createSenderListener() {
        return new CalllogSender.SenderListener() { // from class: com.nuance.nmdp.speechkit.transaction.TransactionRunner.11
            @Override // com.nuance.nmsp.client.sdk.components.core.calllog.CalllogSender.SenderListener
            public void succeeded(byte[] data) {
            }

            @Override // com.nuance.nmsp.client.sdk.components.core.calllog.CalllogSender.SenderListener
            public void failed(short errorCode, byte[] data) {
            }
        };
    }

    public ICustomWordsSynchronizerTransaction createCustomWordsSynchronizeTransaction(String dictationType, String language, Set<String> customWordsAddSet, Set<String> customWordsRemoveSet, final ICustomWordsSynchronizerTransactionListener listener) {
        if (!this._isValid) {
            return null;
        }
        if (this._currentTransaction != null) {
            this._currentTransaction.cancel();
        }
        checkManager();
        ICustomWordsSynchronizerTransaction t = new CustomWordsSynchronizeTransaction(this._nmspManager, this._config, dictationType, language, customWordsAddSet, customWordsRemoveSet, new ICustomWordsSynchronizerTransactionListener() { // from class: com.nuance.nmdp.speechkit.transaction.TransactionRunner.12
            @Override // com.nuance.nmdp.speechkit.transaction.ITransactionListener
            public void transactionDone(ITransaction t2) {
                TransactionRunner.this.transactionDone(t2);
                listener.transactionDone(t2);
            }

            @Override // com.nuance.nmdp.speechkit.transaction.ITransactionListener
            public void error(ITransaction t2, int errorCode, String errorText, String suggestion) {
                listener.error(t2, errorCode, errorText, suggestion);
                if (errorCode == 1) {
                    TransactionRunner.this.restartManager();
                }
            }

            @Override // com.nuance.nmdp.speechkit.transaction.custom_words_synchronize.ICustomWordsSynchronizerTransactionListener
            public void result(ITransaction t2, CustomWordsSynchronizeResult result) {
                listener.result(t2, result);
            }
        });
        this._currentTransaction = t;
        return t;
    }

    public IRecognizeTransaction createTextContextNmdpRecognizeTransaction(String type, int detectionType, int endOfSpeechDuration, int startOfSpeechTimeout, SpeechKit.PartialResultsMode partialResultsMode, String language, TextContext textContext, IPrompt startPrompt, IPrompt stopPrompt, IPrompt resultPrompt, IPrompt errorPrompt, IPdxParser<?> resultParser, ISignalEnergyListener energyListener, final IRecognizeTransactionListener listener) {
        if (!this._isValid) {
            return null;
        }
        if (this._currentTransaction != null) {
            this._currentTransaction.cancel();
        }
        checkManager();
        TextContextNmdpRecognizeTransaction t = new TextContextNmdpRecognizeTransaction(this._nmspManager, this._config, type, detectionType, endOfSpeechDuration, startOfSpeechTimeout, partialResultsMode, language, textContext, startPrompt, stopPrompt, resultPrompt, errorPrompt, resultParser, energyListener, new IRecognizeTransactionListener() { // from class: com.nuance.nmdp.speechkit.transaction.TransactionRunner.13
            @Override // com.nuance.nmdp.speechkit.transaction.ITransactionListener
            public void transactionDone(ITransaction t2) {
                TransactionRunner.this.transactionDone(t2);
                listener.transactionDone(t2);
            }

            @Override // com.nuance.nmdp.speechkit.transaction.ITransactionListener
            public void error(ITransaction t2, int errorCode, String errorText, String suggestion) {
                listener.error(t2, errorCode, errorText, suggestion);
                if (errorCode == 1) {
                    TransactionRunner.this.restartManager();
                }
            }

            @Override // com.nuance.nmdp.speechkit.transaction.recognize.IRecognizeTransactionListener
            public void recordingStarted(ITransaction t2) {
                listener.recordingStarted(t2);
            }

            @Override // com.nuance.nmdp.speechkit.transaction.recognize.IRecognizeTransactionListener
            public void recordingStopped(ITransaction t2) {
                listener.recordingStopped(t2);
            }

            @Override // com.nuance.nmdp.speechkit.transaction.recognize.IRecognizeTransactionListener
            public void results(ITransaction t2) {
                listener.results(t2);
            }
        });
        this._currentTransaction = t;
        return t;
    }
}
