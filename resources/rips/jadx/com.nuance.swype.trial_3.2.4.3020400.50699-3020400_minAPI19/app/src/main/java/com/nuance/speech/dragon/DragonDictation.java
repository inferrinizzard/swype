package com.nuance.speech.dragon;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import com.nuance.nmdp.speechkit.BinaryRecognitionResult;
import com.nuance.nmdp.speechkit.Recognition;
import com.nuance.nmdp.speechkit.Recognizer;
import com.nuance.nmdp.speechkit.RecognizerConstants;
import com.nuance.nmdp.speechkit.SpeechError;
import com.nuance.nmdp.speechkit.SpeechKit;
import com.nuance.nmdp.speechkit.TextContext;
import com.nuance.nmsp.client.sdk.common.defines.NMSPDefines;
import com.nuance.speech.CustomWordSynchronizer;
import com.nuance.speech.Dictation;
import com.nuance.speech.DictationEventListener;
import com.nuance.swype.input.BuildInfo;
import com.nuance.swype.input.IME;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.InputFieldInfo;
import com.nuance.swype.input.accessibility.SoundResources;
import com.nuance.swype.input.udb.UserDictionaryIterator;
import com.nuance.swype.util.LogManager;

/* loaded from: classes.dex */
public final class DragonDictation extends Dictation {
    private static final LogManager.Log log = LogManager.getLog("DragonDictation");
    private StringBuilder bufferForAccessibility;
    private final boolean isExploredByTouch;
    private Recognizer mCurrentRecognizer;
    private final Recognizer.Listener mListener;
    private final SpeechKitWrapper mSpeechKitWrapper;

    public DragonDictation(Context context, String languageName, boolean isExploredByTouch) {
        super(languageName);
        this.mSpeechKitWrapper = new SpeechKitWrapper(context, languageName);
        this.mListener = createListener();
        this.bufferForAccessibility = new StringBuilder();
        this.isExploredByTouch = isExploredByTouch;
        log.d("nmsp version: ", NMSPDefines.VERSION);
    }

    @Override // com.nuance.speech.Dictation
    public final void release() {
        this.mSpeechKitWrapper.release();
    }

    private String getRecognizerTypeForCurrentInput() {
        return isWebSearchField() ? RecognizerConstants.RecognizerType.Search : RecognizerConstants.RecognizerType.Dictation;
    }

    private TextContext getTextContext() {
        TextContext context = null;
        if (this.mSpeechResultTextBuffer != null) {
            InputFieldInfo inputFieldInfo = this.mSpeechResultTextBuffer.getInputFieldInfo();
            context = new TextContext(this.mSpeechResultTextBuffer.getTextBuffer(), this.mSpeechResultTextBuffer.getCursorBegin(), this.mSpeechResultTextBuffer.getCursorEnd());
            if (inputFieldInfo != null) {
                context.addCustomLog("input_type", inputFieldInfo.getInputType());
                context.addCustomLog("app_package_name", inputFieldInfo.getPackageName());
                context.addCustomLog("field_id", inputFieldInfo.getFieldId());
                BuildInfo buildInfo = BuildInfo.from(this.mSpeechKitWrapper.getContext());
                if (buildInfo != null) {
                    context.addCustomLog("script_version", buildInfo.getBuildVersion());
                    context.addCustomLog("build_type", buildInfo.getBuildType().toString());
                    context.addCustomLog("build_date", Long.toString(buildInfo.getBuildDate()));
                }
            }
        }
        return context;
    }

    private boolean isDictationInProgress() {
        return this.mCurrentRecognizer != null && (getDictationState() == Dictation.DictationState.Dictation_Inprogress || getRecordingState() == Dictation.RecordingState.Recording_Inprogress);
    }

    @Override // com.nuance.speech.Dictation
    public final boolean startRecording() {
        log.d("startRecording...");
        TextContext textContext = getTextContext();
        if (this.mSpeechKitWrapper != null && textContext != null) {
            if (!isDictationInProgress()) {
                this.mCurrentRecognizer = this.mSpeechKitWrapper.createTextContextRecognizer(getRecognizerTypeForCurrentInput(), isEndOfSpeechDetectionEnabled() ? 3 : 0, textContext, this.mListener, new Handler());
                this.mCurrentRecognizer.setStartOfSpeechTimeout(8);
                this.mCurrentRecognizer.start();
            }
            log.d("startRecording...done");
            return true;
        }
        log.d("startRecording...failed");
        return false;
    }

    @Override // com.nuance.speech.Dictation
    public final void cancel() {
        if (isDictationInProgress()) {
            log.d("cancel");
            setDictationState(Dictation.DictationState.Dictation_Canceled);
            setRecordingState(Dictation.RecordingState.Recording_Stopped);
            cancelDictationInprogress();
        }
    }

    @Override // com.nuance.speech.Dictation
    public final void stopRecording() {
        LogManager.Log log2 = log;
        Object[] objArr = new Object[1];
        objArr[0] = "stopRecording...mCurrentRecognizer is null.." + (this.mCurrentRecognizer == null);
        log2.d(objArr);
        if (this.mCurrentRecognizer != null) {
            setRecordingState(Dictation.RecordingState.Recording_Stopped);
            this.mCurrentRecognizer.stopRecording();
        }
        log.d("stopRecording...done");
    }

    private void cancelDictationInprogress() {
        if (this.mCurrentRecognizer != null) {
            log.d("cancelDictationInprogress...");
            this.mCurrentRecognizer.cancel();
        }
    }

    private Recognizer.Listener createListener() {
        return new Recognizer.Listener() { // from class: com.nuance.speech.dragon.DragonDictation.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // com.nuance.nmdp.speechkit.IRecognizerListener
            public void onRecordingBegin(Recognizer recognizer) {
                DragonDictation.log.d("onRecordingBegin()");
                DragonDictation.this.playStartRecordingSound();
                DragonDictation.this.bufferForAccessibility.setLength(0);
                DragonDictation.this.setRecordingState(Dictation.RecordingState.Recording_Inprogress);
                DragonDictation.this.setDictationState(Dictation.DictationState.Dictation_Stopped);
                DragonDictation.this.dispatchDictationEvent(DictationEventListener.DictationEvent.Speech_Started, null);
                final Handler handler = new Handler();
                new Runnable() { // from class: com.nuance.speech.dragon.DragonDictation.1.1
                    @Override // java.lang.Runnable
                    public void run() {
                        DragonDictation.log.d("Audio level Runnable..mRecordingState.." + DragonDictation.this.mRecordingState.toString());
                        LogManager.Log log2 = DragonDictation.log;
                        Object[] objArr = new Object[1];
                        objArr[0] = "Audio level Runnable..mCurrentRecognizer is null.." + (DragonDictation.this.mCurrentRecognizer == null);
                        log2.d(objArr);
                        if (DragonDictation.this.mRecordingState == Dictation.RecordingState.Recording_Inprogress && DragonDictation.this.mCurrentRecognizer != null) {
                            handler.postDelayed(this, 50L);
                            if (DragonDictation.this.mDictationListener != null) {
                                DragonDictation.log.d("onRecordingBegin() audio level = " + DragonDictation.this.mCurrentRecognizer.getAudioLevel());
                                DragonDictation.this.dispatchDictationEvent(DictationEventListener.DictationEvent.Speech_AudioLevel, Integer.valueOf(DragonDictation.this.convertAudioLevel(Float.valueOf(DragonDictation.this.mCurrentRecognizer.getAudioLevel()))));
                            }
                        }
                    }
                }.run();
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // com.nuance.nmdp.speechkit.IRecognizerListener
            public void onRecordingDone(Recognizer recognizer) {
                DragonDictation.log.d("onRecordingDone()");
                DragonDictation.this.setRecordingState(Dictation.RecordingState.Recording_Stopped);
                if (DragonDictation.this.getDictationState() != Dictation.DictationState.Dictation_Canceled && DragonDictation.this.getDictationState() != Dictation.DictationState.Dictation_Failed) {
                    if (DragonDictation.this.mSpeechKitWrapper.getSpeechResultsMode() == SpeechKit.PartialResultsMode.CONTINUOUS_STREAMING_RESULTS) {
                        DragonDictation.this.setDictationState(Dictation.DictationState.Dictation_Stopped);
                        DragonDictation.this.dispatchDictationEvent(DictationEventListener.DictationEvent.Dictation_Stopped, null);
                    } else {
                        DragonDictation.this.setDictationState(Dictation.DictationState.Dictation_Inprogress);
                        DragonDictation.this.dispatchDictationEvent(DictationEventListener.DictationEvent.Dictation_Started, null);
                    }
                }
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // com.nuance.nmdp.speechkit.IRecognizerListener
            public void onError(Recognizer recognizer, SpeechError error) {
                DragonDictation.log.e("onError() error = " + error.getErrorCode());
                switch (error.getErrorCode()) {
                    case 1:
                        DragonDictation.this.setRecordingState(Dictation.RecordingState.Recording_Stopped);
                        DragonDictation.this.setDictationState(Dictation.DictationState.Dictation_Failed);
                        DragonDictation.this.dispatchDictationEvent(DictationEventListener.DictationEvent.Connection_Failed, null);
                        DragonDictation.this.mCurrentRecognizer = null;
                        return;
                    case 5:
                        DragonDictation.this.setDictationState(Dictation.DictationState.Dictation_Canceled);
                        DragonDictation.this.setRecordingState(Dictation.RecordingState.Recording_Stopped);
                        DragonDictation.this.dispatchDictationEvent(DictationEventListener.DictationEvent.Dictation_Canceled, null);
                        DragonDictation.this.mCurrentRecognizer = null;
                        return;
                    default:
                        DragonDictation.this.setRecordingState(Dictation.RecordingState.Recording_Stopped);
                        DragonDictation.this.setDictationState(Dictation.DictationState.Dictation_Failed);
                        if (!IMEApplication.from(DragonDictation.this.mSpeechKitWrapper.getContext()).getPlatformUtil().checkForDataConnection()) {
                            DragonDictation.this.dispatchDictationEvent(DictationEventListener.DictationEvent.Connection_Failed, null);
                        } else {
                            DragonDictation.this.dispatchDictationEvent(DictationEventListener.DictationEvent.Dictation_Failed, null);
                        }
                        DragonDictation.this.mCurrentRecognizer = null;
                        return;
                }
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // com.nuance.nmdp.speechkit.IRecognizerListener
            public void onResults(Recognizer recognizer, Recognition results) {
                BinaryRecognitionResult binResult = (BinaryRecognitionResult) results;
                boolean isFinalResponse = binResult.isFinalResponse();
                DragonDictation.log.d("onResults... isFinalResponse = ", Boolean.valueOf(isFinalResponse));
                if (DragonDictation.this.mDictationListener == null || DragonDictation.this.mSpeechResultTextBuffer == null) {
                    DragonDictation.log.d("onResults... mDictationListener and mSpeechResultTextBuffer are null...");
                    DragonDictation.this.setRecordingState(Dictation.RecordingState.Recording_Stopped);
                    DragonDictation.this.setDictationState(Dictation.DictationState.Dictation_Failed);
                    DragonDictation.this.dispatchDictationEvent(DictationEventListener.DictationEvent.Dictation_Failed, null);
                    DragonDictation.this.mCurrentRecognizer = null;
                    return;
                }
                boolean isFinal = isFinalResponse && DragonDictation.this.getRecordingState() == Dictation.RecordingState.Recording_Stopped;
                if (isFinal) {
                    DragonDictation.this.setDictationState(Dictation.DictationState.Dictation_Stopped);
                    DragonDictation.this.setRecordingState(Dictation.RecordingState.Recording_Stopped);
                }
                boolean streaming = DragonDictation.this.mSpeechKitWrapper.getSpeechResultsMode() == SpeechKit.PartialResultsMode.CONTINUOUS_STREAMING_RESULTS;
                if (binResult != null) {
                    CharSequence result = DragonDictation.this.mSpeechResultTextBuffer.updateResult(binResult.results(), DragonDictation.isLanguageSupportLeadingSpace(DragonDictation.this.getCurrentLanguageName()));
                    if (DragonDictation.this.isExploredByTouch) {
                        DragonDictation.this.bufferForAccessibility.append(result);
                    }
                    if (!TextUtils.isEmpty(result) && !DragonDictation.this.isExploredByTouch) {
                        DragonDictation.this.dispatchDictationEvent(DictationEventListener.DictationEvent.Dictation_UpdateResult, new Dictation.SpeechResult(result, streaming, isFinal));
                    }
                }
                if (isFinal) {
                    DragonDictation.this.mCurrentRecognizer = null;
                    if (DragonDictation.this.isExploredByTouch && !TextUtils.isEmpty(DragonDictation.this.bufferForAccessibility)) {
                        DragonDictation.this.dispatchDictationEvent(DictationEventListener.DictationEvent.Dictation_UpdateResult, new Dictation.SpeechResult(DragonDictation.this.bufferForAccessibility, streaming, isFinal));
                    }
                }
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void playStartRecordingSound() {
        playSoundForAccessiblity(3, 1);
    }

    private void playEndOfRecordingSound() {
        playSoundForAccessiblity(3, 2);
    }

    private void playSoundForAccessiblity(int soundId, int times) {
        SoundResources instance;
        IME ime = IMEApplication.from(this.mSpeechKitWrapper.getContext()).getIME();
        if (ime != null && ime.isAccessibilitySupportEnabled() && (instance = SoundResources.getInstance()) != null) {
            instance.play(soundId, times);
        }
    }

    public static boolean isLanguageSupportLeadingSpace(String languageName) {
        return !isChinese(languageName);
    }

    public static boolean isChinese(String languageName) {
        return "Chinese_CN".equals(languageName) || "Chinese_TW".equals(languageName) || "Chinese_HK".equals(languageName);
    }

    public static boolean isCJK(String languageName) {
        return isChinese(languageName) || "Korean".equals(languageName) || "Japanese".equals(languageName);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int convertAudioLevel(Float audioLevel) {
        return (int) ((audioLevel.floatValue() - 46.0d) / 5.6d);
    }

    @Override // com.nuance.speech.Dictation
    public final void canelCustomWordSync() {
        this.mSpeechKitWrapper.cancelCustomWordSync();
    }

    @Override // com.nuance.speech.Dictation
    public final synchronized CustomWordSynchronizer getCustomWordsSync(UserDictionaryIterator iterator) {
        return this.mSpeechKitWrapper.getCustomWordsSyncWrapper(iterator);
    }

    @Override // com.nuance.speech.Dictation
    public final SpeechKit.PartialResultsMode getSpeechResultsMode() {
        return this.mSpeechKitWrapper.getSpeechResultsMode();
    }
}
