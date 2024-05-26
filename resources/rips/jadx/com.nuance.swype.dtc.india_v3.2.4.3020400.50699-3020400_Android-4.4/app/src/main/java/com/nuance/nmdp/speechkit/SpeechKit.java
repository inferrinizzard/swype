package com.nuance.nmdp.speechkit;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.os.Handler;
import com.nuance.nmdp.speechkit.CustomWordsSynchronizer;
import com.nuance.nmdp.speechkit.DataUploadCommand;
import com.nuance.nmdp.speechkit.GenericCommand;
import com.nuance.nmdp.speechkit.NluRecognizer;
import com.nuance.nmdp.speechkit.Recognizer;
import com.nuance.nmdp.speechkit.TextRecognizer;
import com.nuance.nmdp.speechkit.Vocalizer;
import com.nuance.nmdp.speechkit.oem.prompts.OemAudioPrompt;
import com.nuance.nmdp.speechkit.util.JobRunner;
import com.nuance.nmdp.speechkit.util.dataupload.DataBlock;
import com.nuance.nmdp.speechkit.util.grammar.Grammar;
import com.nuance.nmdp.speechkit.util.pdx.PdxValue;
import java.io.IOException;
import java.util.Vector;

/* loaded from: classes.dex */
public final class SpeechKit extends SpeechKitBase {
    private final Context _appContext;
    private final SpeechKitInternal _kit;

    /* loaded from: classes.dex */
    public enum PartialResultsMode {
        NO_PARTIAL_RESULTS,
        UTTERANCE_DETECTION_DEFAULT,
        UTTERANCE_DETECTION_VERY_AGRESSIVE,
        UTTERANCE_DETECTION_AGRESSIVE,
        UTTERANCE_DETECTION_AVERAGE,
        UTTERANCE_DETECTION_CONSERVATIVE,
        CONTINUOUS_STREAMING_RESULTS
    }

    /* loaded from: classes.dex */
    public enum CmdSetType {
        NVC(0),
        DRAGON_NLU(1);

        private int index;

        CmdSetType(int index) {
            this.index = index;
        }

        public final int getIndex() {
            return this.index;
        }
    }

    public static SpeechKit initialize(Context appContext, String id, String host, int port, boolean ssl, byte[] applicationKey) {
        return initialize(appContext, "", id, host, port, null, ssl, applicationKey, CmdSetType.NVC);
    }

    public static SpeechKit initialize(Context appContext, String appVersion, String id, String host, int port, boolean ssl, byte[] applicationKey, CmdSetType type) {
        return initialize(appContext, appVersion, id, host, port, null, ssl, applicationKey, type);
    }

    public static SpeechKit initialize(Context appContext, String appVersion, String id, String host, int port, String subscriptionId, boolean ssl, byte[] applicationKey, CmdSetType type) {
        SpeechKit kit;
        SpeechKitInternal kitInternal = SpeechKitInternal.initialize(appContext, appVersion, id, host, port, subscriptionId, ssl, applicationKey, type);
        if (kitInternal != null) {
            synchronized (SpeechKitInternal.getSync()) {
                kit = (SpeechKit) kitInternal.getWrapper();
                if (kit == null) {
                    kit = new SpeechKit(kitInternal, appContext);
                    kitInternal.setWrapper(kit);
                }
            }
            return kit;
        }
        return null;
    }

    private SpeechKit(SpeechKitInternal speechKit, Context appContext) {
        this._kit = speechKit;
        this._appContext = appContext;
    }

    public final void release() {
        this._kit.release();
    }

    public final void connect() {
        this._kit.connect();
    }

    public final Recognizer createRecognizer(String type, int detection, String language, Recognizer.Listener listener, Handler callbackHandler) {
        return this._kit.createRecognizer(type, detection, language, listener, callbackHandler);
    }

    public final Recognizer createRecognizer(String type, int detection, PartialResultsMode partialResultsMode, String language, Recognizer.Listener listener, Handler callbackHandler) {
        return this._kit.createRecognizer(type, detection, partialResultsMode, language, listener, callbackHandler);
    }

    public final Recognizer createConstraintRecognizer(String type, int detection, String language, Vector<Grammar> grammarList, Recognizer.Listener listener, Object callbackHandler) {
        return this._kit.createConstraintRecognizer(type, detection, language, grammarList, listener, callbackHandler);
    }

    public final Recognizer createConstraintRecognizer(String type, int detection, PartialResultsMode partialResultsMode, String language, Vector<Grammar> grammarList, Recognizer.Listener listener, Object callbackHandler) {
        return this._kit.createConstraintRecognizer(type, detection, partialResultsMode, language, grammarList, listener, callbackHandler);
    }

    public final NluRecognizer createNluRecognizer(String type, int detection, String language, String appSessionId, PdxValue.Dictionary requestParams, NluRecognizer.Listener listener, Handler callbackHandler) {
        return this._kit.createNluRecognizer(type, detection, language, appSessionId, requestParams, listener, callbackHandler);
    }

    public final TextRecognizer createTextRecognizer(String language, String appSessionId, PdxValue.Dictionary requestParams, TextRecognizer.Listener listener, Handler callbackHandler) {
        return this._kit.createTextRecognizer(language, appSessionId, requestParams, listener, callbackHandler);
    }

    public final GenericCommand createResetUserProfileCmd(GenericCommand.Listener listener, Handler callbackHandler) {
        return this._kit.createResetUserProfileCmd(listener, callbackHandler);
    }

    public final GenericCommand createLogRevisionCmd(String event, PdxValue.Dictionary content, String appSessionId, GenericCommand.Listener listener, Handler callbackHandler) {
        return this._kit.createLogRevisionCmd(event, content, appSessionId, listener, callbackHandler);
    }

    public final DataUploadCommand createDataUploadCmd(DataBlock datablock, int currentChecksum, int newChecksum, DataUploadCommand.Listener listener, Handler callbackHandler) {
        return this._kit.createDataUploadCmd(datablock, currentChecksum, newChecksum, listener, callbackHandler);
    }

    public final Vocalizer createVocalizerWithLanguage(String language, Vocalizer.Listener listener, Handler callbackHandler) {
        return this._kit.createVocalizerWithLanguage(language, listener, callbackHandler);
    }

    public final Vocalizer createVocalizerWithVoice(String voice, Vocalizer.Listener listener, Handler callbackHandler) {
        return this._kit.createVocalizerWithVoice(voice, listener, callbackHandler);
    }

    public final String getSessionId() {
        return this._kit.getSessionId();
    }

    public final Prompt defineAudioPrompt(int resourceId) {
        AssetFileDescriptor afd = this._appContext.getResources().openRawResourceFd(resourceId);
        if (afd == null) {
            throw new IllegalArgumentException("resourceId must refer to an uncompressed resource");
        }
        return defineAudioPrompt(afd);
    }

    private Prompt defineAudioPrompt(final AssetFileDescriptor file) {
        if (file == null) {
            return null;
        }
        final OemAudioPrompt audio = new OemAudioPrompt();
        Prompt ret = new Prompt(audio, this._kit);
        synchronized (SpeechKitInternal.getSync()) {
            if (!this._kit.isValid()) {
                try {
                    file.close();
                } catch (IOException e) {
                }
                SpeechKitInternal.throwInvalidException();
            }
            this._kit.definePrompt(ret);
        }
        JobRunner.addJob(new Runnable() { // from class: com.nuance.nmdp.speechkit.SpeechKit.1
            @Override // java.lang.Runnable
            public void run() {
                audio.init(file);
            }
        });
        return ret;
    }

    public final void setDefaultRecognizerPrompts(Prompt recordingStart, Prompt recordingStop, Prompt result, Prompt error) {
        this._kit.setDefaultRecognizerPrompts(recordingStart, recordingStop, result, error);
    }

    public final void cancelCurrent() {
        this._kit.cancelCurrent();
    }

    public final void setCmdSetType(CmdSetType type) {
        this._kit.setCmdSetType(type);
    }

    public final CmdSetType getCmdSetType() {
        return this._kit.getCmdSetType();
    }

    public final Recognizer createTextContextRecognizer(String type, int detection, PartialResultsMode partialResultsMode, String language, TextContext textContext, Recognizer.Listener listener, Handler callbackHandler) {
        return this._kit.createTextContextRecognizer(type, detection, partialResultsMode, language, textContext, listener, callbackHandler);
    }

    public final CustomWordsSynchronizer createCustomWordsSynchronizer(String dictationType, String language, CustomWordsSynchronizer.Listener listener, Handler callbackHandler) {
        return this._kit.createCustomWordsSynchronizer(dictationType, language, listener, callbackHandler);
    }
}
