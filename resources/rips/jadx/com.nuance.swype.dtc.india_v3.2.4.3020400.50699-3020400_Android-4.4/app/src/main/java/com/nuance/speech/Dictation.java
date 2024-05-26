package com.nuance.speech;

import com.nuance.nmdp.speechkit.SpeechKit;
import com.nuance.speech.DictationEventListener;
import com.nuance.swype.input.InputFieldInfo;
import com.nuance.swype.input.udb.UserDictionaryIterator;

/* loaded from: classes.dex */
public abstract class Dictation {
    protected String mCurrentLanguageName;
    public DictationEventListener mDictationListener;
    public SpeechResultTextBuffer mSpeechResultTextBuffer;
    public RecordingState mRecordingState = RecordingState.Recording_Stopped;
    protected DictationState mDictationState = DictationState.Dictation_Stopped;
    private boolean mEndofSpeechDetection = true;

    /* loaded from: classes.dex */
    public enum DictationState {
        Dictation_Failed,
        Dictation_Inprogress,
        Dictation_Canceled,
        Dictation_Stopped
    }

    /* loaded from: classes.dex */
    public enum RecordingState {
        Recording_Failed,
        Recording_Inprogress,
        Recording_Stopped
    }

    public abstract void cancel();

    public abstract void canelCustomWordSync();

    public abstract CustomWordSynchronizer getCustomWordsSync(UserDictionaryIterator userDictionaryIterator);

    public abstract SpeechKit.PartialResultsMode getSpeechResultsMode();

    public abstract void release();

    public abstract boolean startRecording();

    public abstract void stopRecording();

    /* loaded from: classes.dex */
    public class SpeechResult {
        public final boolean finalResult;
        public final boolean streaming;
        public final CharSequence text;

        public SpeechResult(CharSequence text, boolean streaming, boolean finalResult) {
            this.text = text;
            this.finalResult = finalResult;
            this.streaming = streaming;
        }
    }

    public Dictation(String langName) {
        this.mCurrentLanguageName = langName;
    }

    public RecordingState getRecordingState() {
        return this.mRecordingState;
    }

    public void setRecordingState(RecordingState state) {
        this.mRecordingState = state;
    }

    public void setDictationState(DictationState state) {
        this.mDictationState = state;
    }

    public DictationState getDictationState() {
        return this.mDictationState;
    }

    public void setDictationResultTextBuffer(SpeechResultTextBuffer speechTextBuffer) {
        this.mSpeechResultTextBuffer = speechTextBuffer;
    }

    public void setEndofSpeechDetection(boolean set) {
        this.mEndofSpeechDetection = set;
    }

    public boolean isEndOfSpeechDetectionEnabled() {
        return this.mEndofSpeechDetection;
    }

    public String getCurrentLanguageName() {
        return this.mCurrentLanguageName;
    }

    public void setDictationEventListener(DictationEventListener listener) {
        this.mDictationListener = listener;
    }

    protected String getInputType() {
        return (this.mSpeechResultTextBuffer == null || this.mSpeechResultTextBuffer.getInputFieldInfo() == null) ? "" : this.mSpeechResultTextBuffer.getInputFieldInfo().getInputType();
    }

    public boolean isWebSearchField() {
        boolean isUrlAsSearch = false;
        if (this.mSpeechResultTextBuffer != null && this.mSpeechResultTextBuffer.getInputFieldInfo() != null) {
            isUrlAsSearch = this.mSpeechResultTextBuffer.getInputFieldInfo().isURLField() && this.mSpeechResultTextBuffer.getInputFieldInfo().getIME().getAppSpecificBehavior().shouldUseSearchRecognizerTypeForUrl();
        }
        return getInputType().equals(InputFieldInfo.INPUT_TYPE_WEB_SEARCH) || isUrlAsSearch;
    }

    public void dispatchDictationEvent(DictationEventListener.DictationEvent event, Object eventData) {
        if (this.mDictationListener != null) {
            this.mDictationListener.handleDictationEvent(event, eventData);
        }
    }
}
