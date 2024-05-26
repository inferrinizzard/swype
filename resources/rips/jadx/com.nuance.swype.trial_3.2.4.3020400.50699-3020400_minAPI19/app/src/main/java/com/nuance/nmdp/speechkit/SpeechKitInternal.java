package com.nuance.nmdp.speechkit;

import android.os.Handler;
import com.nuance.connect.common.Strings;
import com.nuance.nmdp.speechkit.CustomWordsSynchronizer;
import com.nuance.nmdp.speechkit.DataUploadCommand;
import com.nuance.nmdp.speechkit.GenericCommand;
import com.nuance.nmdp.speechkit.LanguageTableRequest;
import com.nuance.nmdp.speechkit.NluRecognizer;
import com.nuance.nmdp.speechkit.Recognizer;
import com.nuance.nmdp.speechkit.SpeechKit;
import com.nuance.nmdp.speechkit.TextRecognizer;
import com.nuance.nmdp.speechkit.Vocalizer;
import com.nuance.nmdp.speechkit.transaction.ITransaction;
import com.nuance.nmdp.speechkit.transaction.ITransactionListener;
import com.nuance.nmdp.speechkit.transaction.TransactionConfig;
import com.nuance.nmdp.speechkit.transaction.TransactionRunner;
import com.nuance.nmdp.speechkit.util.JobRunner;
import com.nuance.nmdp.speechkit.util.List;
import com.nuance.nmdp.speechkit.util.Logger;
import com.nuance.nmdp.speechkit.util.dataupload.Data;
import com.nuance.nmdp.speechkit.util.dataupload.DataBlock;
import com.nuance.nmdp.speechkit.util.grammar.Grammar;
import com.nuance.nmdp.speechkit.util.pdx.PdxValue;
import java.util.Iterator;
import java.util.Vector;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class SpeechKitInternal {
    private static SpeechKitInternal _instance = null;
    private static Object _sync = new Object();
    private static Object _syncUpload = new Object();
    private final TransactionConfig _config;
    private DataBlock _datablock;
    private final String _host;
    private final String _id;
    private final int _port;
    private CommandProxyBase _genericCommand = null;
    private boolean _isCommandInit = false;
    private int _currentChecksum = 0;
    private int _newChecksum = 0;
    protected boolean _isValid = true;
    private TransactionRunner _transactionRunner = null;
    private final List _prompts = new List();
    private Object _speechKitWrapper = null;
    private Prompt _defaultStartPrompt = null;
    private Prompt _defaultEndPrompt = null;
    private Prompt _defaultResultPrompt = null;
    private Prompt _defaultErrorPrompt = null;

    public static SpeechKitInternal initialize(Object appContext, String appVersion, String id, String host, int port, String subscriptionId, boolean useSsl, byte[] applicationKey, SpeechKit.CmdSetType type) {
        checkArgForNull(appContext, "appContext");
        checkArgForNull(id, "id");
        checkStringArgForNullOrEmpty(host, "host");
        checkArgForNull(appVersion, "appVersion");
        checkArgForNull(applicationKey, "applicationKey");
        if (port < 0 || port > 65535) {
            throwException(new IllegalArgumentException("port must be between 0 and 65535"));
        }
        checkArgForNull(appContext, "type");
        synchronized (_sync) {
            Logger.info(null, "Initializing SpeechKit");
            if (_instance == null) {
                JobRunner.initialize();
            }
            if (_instance != null && !_instance.isCurrent(id, host, port)) {
                Logger.info(null, "Releasing old SpeechKit before creating new instance");
                _instance.dispose();
                _instance = null;
            }
            if (_instance == null) {
                Logger.info(null, "Creating fresh SpeechKit instance");
                _instance = new SpeechKitInternal(appContext, appVersion, id, host, port, subscriptionId, useSsl, applicationKey, type);
            }
        }
        return _instance;
    }

    private SpeechKitInternal(Object appContext, String appVersion, String id, String host, int port, String subscriptionId, boolean useSsl, byte[] appKey, SpeechKit.CmdSetType type) {
        this._id = id;
        this._host = host;
        this._port = port;
        this._config = new TransactionConfig(appContext, appVersion, this._host, this._port, subscriptionId, useSsl, this._id, appKey, type);
        JobRunner.addJob(new Runnable() { // from class: com.nuance.nmdp.speechkit.SpeechKitInternal.1
            @Override // java.lang.Runnable
            public void run() {
                SpeechKitInternal.this._transactionRunner = new TransactionRunner(SpeechKitInternal.this._config);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean isValid() {
        return this._isValid;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Object getSync() {
        return _sync;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final TransactionRunner getRunner() {
        return this._transactionRunner;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void setWrapper(Object speechKitWrapper) {
        this._speechKitWrapper = speechKitWrapper;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Object getWrapper() {
        return this._speechKitWrapper;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void deletePrompt(Prompt p) {
        if (this._prompts.contains(p)) {
            this._prompts.remove(p);
        }
        p.getPrompt().dispose();
        if (p == this._defaultStartPrompt) {
            this._defaultStartPrompt = null;
        }
        if (p == this._defaultEndPrompt) {
            this._defaultEndPrompt = null;
        }
        if (p == this._defaultResultPrompt) {
            this._defaultResultPrompt = null;
        }
        if (p == this._defaultErrorPrompt) {
            this._defaultErrorPrompt = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final void checkArgForNull(Object arg, String argName) {
        if (arg == null) {
            throwException(new IllegalArgumentException(argName + " must not be null"));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final void checkStringArgForNullOrEmpty(String arg, String argName) {
        if (arg == null || arg.length() == 0) {
            throwException(new IllegalArgumentException(argName + " must not be null or empty"));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void checkForInvalid() {
        synchronized (_sync) {
            if (!this._isValid) {
                throwInvalidException();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final void throwInvalidException() {
        throwException(new IllegalStateException("SpeechKit instance is released"));
    }

    static final void throwException(RuntimeException ex) {
        Logger.error(null, ex.getMessage());
        throw ex;
    }

    private boolean isCurrent(String id, String host, int port) {
        return this._id.equals(id) && this._host.equals(host) && this._port == port;
    }

    private void dispose() {
        this._isValid = false;
        JobRunner.addJob(new Runnable() { // from class: com.nuance.nmdp.speechkit.SpeechKitInternal.2
            @Override // java.lang.Runnable
            public void run() {
                SpeechKitInternal.this._transactionRunner.dispose();
                int promptCount = SpeechKitInternal.this._prompts.size();
                for (int i = 0; i < promptCount; i++) {
                    ((Prompt) SpeechKitInternal.this._prompts.removeAt(0)).getPrompt().dispose();
                }
            }
        });
    }

    public final void release() {
        synchronized (_sync) {
            if (this == _instance) {
                Logger.info(this, "Releasing SpeechKit instance");
                dispose();
                _instance = null;
                JobRunner.addJob(new Runnable() { // from class: com.nuance.nmdp.speechkit.SpeechKitInternal.3
                    @Override // java.lang.Runnable
                    public void run() {
                        JobRunner.shutdown();
                    }
                });
            } else {
                Logger.warn(this, "SpeechKit instance already released");
            }
        }
    }

    public final void connect() {
        synchronized (_sync) {
            if (!this._isValid) {
                throwInvalidException();
            }
            JobRunner.addJob(new Runnable() { // from class: com.nuance.nmdp.speechkit.SpeechKitInternal.4
                @Override // java.lang.Runnable
                public void run() {
                    ITransaction t = SpeechKitInternal.this._transactionRunner.createConnectTransaction(new ITransactionListener() { // from class: com.nuance.nmdp.speechkit.SpeechKitInternal.4.1
                        @Override // com.nuance.nmdp.speechkit.transaction.ITransactionListener
                        public void error(ITransaction t2, int errorCode, String errorText, String suggestion) {
                        }

                        @Override // com.nuance.nmdp.speechkit.transaction.ITransactionListener
                        public void transactionDone(ITransaction t2) {
                        }
                    }, true);
                    if (t != null) {
                        t.start();
                    }
                }
            });
        }
    }

    private void setupRecognizerPrompts(IRecognizer recognizer, boolean recorderPrompts, boolean resultPrompts) {
        if (recorderPrompts && this._defaultStartPrompt != null) {
            recognizer.setPrompt(0, this._defaultStartPrompt);
        }
        if (recorderPrompts && this._defaultEndPrompt != null) {
            recognizer.setPrompt(1, this._defaultEndPrompt);
        }
        if (resultPrompts && this._defaultResultPrompt != null) {
            recognizer.setPrompt(2, this._defaultResultPrompt);
        }
        if (resultPrompts && this._defaultErrorPrompt != null) {
            recognizer.setPrompt(3, this._defaultErrorPrompt);
        }
    }

    public final Recognizer createRecognizer(String type, int detection, String language, Recognizer.Listener listener, Object callbackHandler) {
        RecognizerProxy recognizer;
        checkArgForNull(type, "type");
        checkArgForNull(language, Strings.MESSAGE_BUNDLE_LANGUAGE);
        checkArgForNull(listener, "listener");
        synchronized (_sync) {
            if (!this._isValid) {
                throwInvalidException();
            }
            recognizer = new RecognizerProxy(this, type, detection, SpeechKit.PartialResultsMode.NO_PARTIAL_RESULTS, language, listener, callbackHandler);
            setupRecognizerPrompts(recognizer, true, true);
        }
        return recognizer;
    }

    public final Recognizer createRecognizer(String type, int detection, SpeechKit.PartialResultsMode partialResultsMode, String language, Recognizer.Listener listener, Object callbackHandler) {
        RecognizerProxy recognizer;
        checkArgForNull(type, "type");
        checkArgForNull(language, Strings.MESSAGE_BUNDLE_LANGUAGE);
        checkArgForNull(listener, "listener");
        synchronized (_sync) {
            if (!this._isValid) {
                throwInvalidException();
            }
            recognizer = new RecognizerProxy(this, type, detection, partialResultsMode, language, listener, callbackHandler);
            setupRecognizerPrompts(recognizer, true, true);
        }
        return recognizer;
    }

    public final Recognizer createConstraintRecognizer(String type, int detection, String language, Vector<Grammar> grammarList, Recognizer.Listener listener, Object callbackHandler) {
        ConstraintRecognizerProxy recognizer;
        checkArgForNull(type, "type");
        checkArgForNull(language, Strings.MESSAGE_BUNDLE_LANGUAGE);
        checkArgForNull(listener, "listener");
        checkArgForNull(grammarList, "grammarList");
        synchronized (_sync) {
            if (!this._isValid) {
                throwInvalidException();
            }
            PdxValue.Sequence grammarSeq = null;
            if (grammarList != null) {
                grammarSeq = new PdxValue.Sequence();
                Iterator i$ = grammarList.iterator();
                while (i$.hasNext()) {
                    Grammar grammar = i$.next();
                    grammarSeq.add(grammar.getGrammarDictionary());
                }
            }
            recognizer = new ConstraintRecognizerProxy(this, type, detection, SpeechKit.PartialResultsMode.NO_PARTIAL_RESULTS, language, grammarSeq, listener, callbackHandler);
            setupRecognizerPrompts(recognizer, true, true);
        }
        return recognizer;
    }

    public final Recognizer createConstraintRecognizer(String type, int detection, SpeechKit.PartialResultsMode partialResultsMode, String language, Vector<Grammar> grammarList, Recognizer.Listener listener, Object callbackHandler) {
        ConstraintRecognizerProxy recognizer;
        checkArgForNull(type, "type");
        checkArgForNull(language, Strings.MESSAGE_BUNDLE_LANGUAGE);
        checkArgForNull(listener, "listener");
        checkArgForNull(grammarList, "grammarList");
        synchronized (_sync) {
            if (!this._isValid) {
                throwInvalidException();
            }
            PdxValue.Sequence grammarSeq = null;
            if (grammarList != null) {
                grammarSeq = new PdxValue.Sequence();
                Iterator i$ = grammarList.iterator();
                while (i$.hasNext()) {
                    Grammar grammar = i$.next();
                    grammarSeq.add(grammar.getGrammarDictionary());
                }
            }
            recognizer = new ConstraintRecognizerProxy(this, type, detection, partialResultsMode, language, grammarSeq, listener, callbackHandler);
            setupRecognizerPrompts(recognizer, true, true);
        }
        return recognizer;
    }

    public final NluRecognizer createNluRecognizer(String type, int detection, String language, String appSessionId, PdxValue.Dictionary requestParams, NluRecognizer.Listener listener, Object callbackHandler) {
        NluRecognizerProxy recognizer;
        checkArgForNull(type, "type");
        checkArgForNull(language, Strings.MESSAGE_BUNDLE_LANGUAGE);
        checkArgForNull(listener, "listener");
        checkArgForNull(requestParams, "requestParams");
        synchronized (_sync) {
            if (!this._isValid) {
                throwInvalidException();
            }
            recognizer = new NluRecognizerProxy(this, type, detection, language, appSessionId, requestParams, listener, callbackHandler);
            setupRecognizerPrompts(recognizer, true, true);
        }
        return recognizer;
    }

    public final TextRecognizer createTextRecognizer(String language, String appSessionId, PdxValue.Dictionary requestParams, TextRecognizer.Listener listener, Object callbackHandler) {
        TextRecognizerProxy textRecognizerProxy;
        checkArgForNull(language, Strings.MESSAGE_BUNDLE_LANGUAGE);
        checkArgForNull(listener, "listener");
        checkArgForNull(requestParams, "requestParams");
        synchronized (_sync) {
            if (!this._isValid) {
                throwInvalidException();
            }
            textRecognizerProxy = new TextRecognizerProxy(this, language, appSessionId, requestParams, null, listener, callbackHandler);
        }
        return textRecognizerProxy;
    }

    public final GenericCommand createResetUserProfileCmd(GenericCommand.Listener listener, Object callbackHandler) {
        ResetUserProfileProxy resetUserProfileProxy;
        checkArgForNull(listener, "listener");
        synchronized (_sync) {
            if (!this._isValid) {
                throwInvalidException();
            }
            resetUserProfileProxy = new ResetUserProfileProxy(this, listener, callbackHandler);
        }
        return resetUserProfileProxy;
    }

    public final GenericCommand createLogRevisionCmd(String event, PdxValue.Dictionary content, String appSessionId, GenericCommand.Listener listener, Object callbackHandler) {
        LogRevisionProxy command;
        checkArgForNull(event, "event");
        checkArgForNull(content, "content");
        checkArgForNull(listener, "listener");
        synchronized (_sync) {
            if (!this._isValid) {
                throwInvalidException();
            }
            command = new LogRevisionProxy(this, appSessionId, listener, callbackHandler);
            PdxValue.Dictionary logDict = new PdxValue.Dictionary();
            logDict.put("event", event);
            logDict.put("content", content);
            command.addParam("LOG_CONTENT", logDict);
        }
        return command;
    }

    public final DataUploadCommand createDataUploadCmd(DataBlock datablock, int currentChecksum, int newChecksum, DataUploadCommand.Listener listener, Object callbackHandler) {
        DataUploadProxy command;
        checkArgForNull(datablock, "datablock");
        checkArgForNull(listener, "listener");
        synchronized (_sync) {
            if (!this._isValid) {
                throwInvalidException();
            }
            command = new DataUploadProxy(this, listener, callbackHandler);
            this._genericCommand = command;
            this._datablock = datablock;
            this._currentChecksum = currentChecksum;
            this._newChecksum = newChecksum;
            JobRunner.addJob(new Runnable() { // from class: com.nuance.nmdp.speechkit.SpeechKitInternal.5
                @Override // java.lang.Runnable
                public void run() {
                    synchronized (SpeechKitInternal._syncUpload) {
                        SpeechKitInternal.this._genericCommand.addParam("DATA_BLOCK", SpeechKitInternal.this._datablock.getDataBlockDictionary());
                        PdxValue.Dictionary dictionary = new PdxValue.Dictionary();
                        PdxValue.Sequence checksums = new PdxValue.Sequence();
                        dictionary.put("num_data_blocks", 1);
                        Vector<Data> datalist = SpeechKitInternal.this._datablock.getDataList();
                        for (int i = 0; i < datalist.size(); i++) {
                            PdxValue.Dictionary checksum = new PdxValue.Dictionary();
                            checksum.put("id", datalist.elementAt(i).getId());
                            checksum.put("type", datalist.elementAt(i).getTypeStr());
                            checksum.put("current_checksum", Integer.toString(SpeechKitInternal.this._currentChecksum));
                            checksum.put("new_checksum", Integer.toString(SpeechKitInternal.this._newChecksum));
                            checksum.put("algorithm_id", "MD5");
                            checksums.add(checksum);
                        }
                        dictionary.put("checksums", checksums);
                        SpeechKitInternal.this._genericCommand.addParam("UPLOAD_DONE", dictionary);
                        SpeechKitInternal.this._isCommandInit = true;
                        SpeechKitInternal._syncUpload.notify();
                    }
                }
            });
            synchronized (_syncUpload) {
                while (!this._isCommandInit) {
                    try {
                        _syncUpload.wait();
                    } catch (InterruptedException e) {
                    }
                }
                command.setReady();
            }
        }
        return command;
    }

    public final LanguageTableRequest createLanguageRequest(LanguageTableRequest.Listener listener, Object callbackHandler) {
        LanguageTableRequestProxy request;
        checkArgForNull(listener, "listener");
        synchronized (_sync) {
            if (!this._isValid) {
                throwInvalidException();
            }
            request = new LanguageTableRequestProxy(this, listener, callbackHandler);
            request.addParam("TRANSFER_ID", new PdxValue.String("1234567890"));
        }
        return request;
    }

    public final Vocalizer createVocalizerWithLanguage(String language, Vocalizer.Listener listener, Object callbackHandler) {
        VocalizerProxy vocalizerProxy;
        checkArgForNull(language, Strings.MESSAGE_BUNDLE_LANGUAGE);
        checkArgForNull(listener, "listener");
        synchronized (_sync) {
            if (!this._isValid) {
                throwInvalidException();
            }
            vocalizerProxy = new VocalizerProxy(this, null, language, listener, callbackHandler);
        }
        return vocalizerProxy;
    }

    public final Vocalizer createVocalizerWithVoice(String voice, Vocalizer.Listener listener, Object callbackHandler) {
        VocalizerProxy vocalizerProxy;
        checkArgForNull(voice, "voice");
        checkArgForNull(listener, "listener");
        synchronized (_sync) {
            if (!this._isValid) {
                throwInvalidException();
            }
            vocalizerProxy = new VocalizerProxy(this, voice, null, listener, callbackHandler);
        }
        return vocalizerProxy;
    }

    public final String getSessionId() {
        String sessionId;
        synchronized (_sync) {
            sessionId = this._transactionRunner != null ? this._transactionRunner.getSessionId() : null;
        }
        return sessionId;
    }

    public final void definePrompt(Prompt prompt) {
        synchronized (_sync) {
            this._prompts.add(prompt);
        }
    }

    public final void setDefaultRecognizerPrompts(final Prompt recordingStart, final Prompt recordingStop, final Prompt result, final Prompt error) {
        synchronized (_sync) {
            if (!this._isValid) {
                throwInvalidException();
            }
        }
        JobRunner.addJob(new Runnable() { // from class: com.nuance.nmdp.speechkit.SpeechKitInternal.6
            @Override // java.lang.Runnable
            public void run() {
                SpeechKitInternal.this._defaultStartPrompt = null;
                SpeechKitInternal.this._defaultEndPrompt = null;
                SpeechKitInternal.this._defaultResultPrompt = null;
                SpeechKitInternal.this._defaultErrorPrompt = null;
                if (recordingStart != null) {
                    if (!recordingStart.getPrompt().isDisposed()) {
                        SpeechKitInternal.this._defaultStartPrompt = recordingStart;
                    } else {
                        Logger.warn(this, "Recording start prompt is invalid");
                    }
                }
                if (recordingStop != null) {
                    if (!recordingStop.getPrompt().isDisposed()) {
                        SpeechKitInternal.this._defaultEndPrompt = recordingStop;
                    } else {
                        Logger.warn(this, "Recording stop prompt is invalid");
                    }
                }
                if (result != null) {
                    if (!result.getPrompt().isDisposed()) {
                        SpeechKitInternal.this._defaultResultPrompt = result;
                    } else {
                        Logger.warn(this, "Result prompt is invalid");
                    }
                }
                if (error != null) {
                    if (!error.getPrompt().isDisposed()) {
                        SpeechKitInternal.this._defaultErrorPrompt = error;
                    } else {
                        Logger.warn(this, "Error prompt is invalid");
                    }
                }
            }
        });
    }

    public final void cancelCurrent() {
        synchronized (_sync) {
            if (!this._isValid) {
                throwInvalidException();
            }
            JobRunner.addJob(new Runnable() { // from class: com.nuance.nmdp.speechkit.SpeechKitInternal.7
                @Override // java.lang.Runnable
                public void run() {
                    SpeechKitInternal.this._transactionRunner.cancelCurrent();
                }
            });
        }
    }

    public final String getAsrCmd() {
        return this._config.getAsrCmd();
    }

    final void setAsrCmd(String command) {
        this._config.setAsrCmd(command);
    }

    public final String getDataUploadCmd() {
        return this._config.getDataUploadCmd();
    }

    final void setDataUploadCmd(String command) {
        this._config.setDataUploadCmd(command);
    }

    public final String getLogRevisionCmd() {
        return this._config.getLogRevisionCmd();
    }

    final void setLogRevisionCmd(String command) {
        this._config.setLogRevisionCmd(command);
    }

    public final String getResetUerProfileCmd() {
        return this._config.getResetUerProfileCmd();
    }

    final void setResetUerProfileCmd(String command) {
        this._config.setResetUerProfileCmd(command);
    }

    public final String getAppserverCmd() {
        return this._config.getAppserverCmd();
    }

    final void setAppserverCmd(String command) {
        this._config.setAppserverCmd(command);
    }

    public final String getLanguageCmd() {
        return this._config.getLanguageCmd();
    }

    final void setLanguageCmd(String command) {
        this._config.setLanguageCmd(command);
    }

    public final String getTtsCmd() {
        return this._config.getTtsCmd();
    }

    final void setTtsCmd(String command) {
        this._config.setTtsCmd(command);
    }

    public final void setCmdSetType(SpeechKit.CmdSetType type) {
        this._config.setCmdSetType(type);
    }

    public final SpeechKit.CmdSetType getCmdSetType() {
        return this._config.getCmdSetType();
    }

    public final Recognizer createTextContextRecognizer(String type, int detection, SpeechKit.PartialResultsMode partialResultsMode, String language, TextContext textContext, Recognizer.Listener listener, Object callbackHandler) {
        TextContextRecognizerProxy recognizer;
        checkArgForNull(type, "type");
        checkArgForNull(language, Strings.MESSAGE_BUNDLE_LANGUAGE);
        checkArgForNull(listener, "listener");
        synchronized (_sync) {
            if (!this._isValid) {
                throwInvalidException();
            }
            recognizer = new TextContextRecognizerProxy(this, type, detection, partialResultsMode, language, textContext, listener, callbackHandler);
            setupRecognizerPrompts(recognizer, true, true);
        }
        return recognizer;
    }

    public final CustomWordsSynchronizer createCustomWordsSynchronizer(String dictationType, String language, CustomWordsSynchronizer.Listener listener, Handler callbackHandler) {
        CustomWordsSynchronizerProxy customWordsSynchronizerProxy;
        checkArgForNull(listener, "listener");
        synchronized (_sync) {
            if (!this._isValid) {
                throwInvalidException();
            }
            customWordsSynchronizerProxy = new CustomWordsSynchronizerProxy(this, dictationType, language, listener, callbackHandler);
        }
        return customWordsSynchronizerProxy;
    }
}
