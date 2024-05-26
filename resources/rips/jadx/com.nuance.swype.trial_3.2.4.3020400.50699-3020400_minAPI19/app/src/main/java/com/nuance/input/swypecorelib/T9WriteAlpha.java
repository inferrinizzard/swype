package com.nuance.input.swypecorelib;

import android.graphics.Point;
import com.nuance.input.swypecorelib.Candidates;
import com.nuance.input.swypecorelib.T9WriteRecognizerListener;
import com.nuance.input.swypecorelib.WriteThreadQueue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* loaded from: classes.dex */
public class T9WriteAlpha extends T9Write {
    private boolean mEnableInstantGesture;
    private int[] mIsInstantGesture;
    private int[] mResultCount;
    private char[] mStartWord;

    private static native int Write_Alpha_addArc(long j, List<Point> list, List<Point> list2, int[] iArr, boolean z);

    private static native int Write_Alpha_beginArc(long j);

    private static native int Write_Alpha_changeSettings(long j, T9WriteSetting t9WriteSetting);

    private static native long Write_Alpha_create(String str);

    private static native void Write_Alpha_destroy(long j);

    private static native void Write_Alpha_enableUsageLogging(long j, boolean z);

    private static native int Write_Alpha_endArc(long j);

    private static native int Write_Alpha_finish(long j);

    private static native int Write_Alpha_getCandidates(long j, List<T9WriteRecognizeCandidate> list, int i);

    private static native String Write_Alpha_getDatabaseVersion(long j);

    private static native String Write_Alpha_getVersion(long j);

    private static native int Write_Alpha_noteSelectedCandidate(long j, int i);

    private static native int Write_Alpha_recognize(long j, char[] cArr, int[] iArr);

    private static native int Write_Alpha_start(long j, T9WriteSetting t9WriteSetting, int i);

    /* JADX INFO: Access modifiers changed from: package-private */
    public T9WriteAlpha() {
        super(new T9WriteAlphaSetting());
        this.mResultCount = new int[1];
        this.mIsInstantGesture = new int[1];
        this.mStartWord = new char[65];
        this.mEnableInstantGesture = true;
        Arrays.fill(this.mStartWord, (char) 0);
    }

    @Override // com.nuance.input.swypecorelib.T9Write
    protected long create_native_context(String databaseConfigFile) {
        this.nativeContext = Write_Alpha_create(databaseConfigFile);
        return this.nativeContext;
    }

    @Override // com.nuance.input.swypecorelib.T9Write
    protected void destroy_native_context(long nativeContext) {
        Write_Alpha_destroy(nativeContext);
    }

    @Override // com.nuance.input.swypecorelib.T9Write
    public void startSession(int languageID) {
        synchronized (this.mLock) {
            if (hasSession()) {
                this.mLanguageID = languageID;
                Arrays.fill(this.mStartWord, (char) 0);
                Write_Alpha_start(this.nativeContext, this.settings, languageID);
            }
        }
    }

    @Override // com.nuance.input.swypecorelib.T9Write
    public void finishSession() {
        synchronized (this.mLock) {
            if (hasSession()) {
                this.mWriteThreadQueue.clearQueue();
                Write_Alpha_finish(this.nativeContext);
                Arrays.fill(this.mStartWord, (char) 0);
            }
        }
    }

    @Override // com.nuance.input.swypecorelib.T9Write
    public String getVersion() {
        String Write_Alpha_getVersion;
        synchronized (this.mLock) {
            Write_Alpha_getVersion = this.nativeContext != 0 ? Write_Alpha_getVersion(this.nativeContext) : "";
        }
        return Write_Alpha_getVersion;
    }

    @Override // com.nuance.input.swypecorelib.T9Write
    public String getDatabaseVersion() {
        String Write_Alpha_getDatabaseVersion;
        synchronized (this.mLock) {
            Write_Alpha_getDatabaseVersion = this.nativeContext != 0 ? Write_Alpha_getDatabaseVersion(this.nativeContext) : "";
        }
        return Write_Alpha_getDatabaseVersion;
    }

    private Candidates buildXT9WordCandiates(T9WriteRecognizeCandidate first, int max) {
        return null;
    }

    private void addXT9WorCandidates(Candidates hwrWordCandidates, Candidates xt9WrodCandidates) {
        if (xt9WrodCandidates != null) {
            for (int i = 0; i < xt9WrodCandidates.count(); i++) {
                hwrWordCandidates.add(xt9WrodCandidates.get(i));
            }
        }
    }

    @Override // com.nuance.input.swypecorelib.WriteThreadQueue.OnWriteThreadQueueConsumer
    public void consume(WriteThreadQueue.QueueItem item) {
        if (this.nativeContext != 0) {
            synchronized (this.mLock) {
                if (hasSession()) {
                    switch (item.mType) {
                        case 1:
                            if (item instanceof WriteThreadQueue.ArcQueueItem) {
                                Write_Alpha_addArc(this.nativeContext, ((WriteThreadQueue.ArcQueueItem) item).mArc1, ((WriteThreadQueue.ArcQueueItem) item).mArc2, this.mIsInstantGesture, this.mEnableInstantGesture);
                                if (this.mIsInstantGesture[0] == 1) {
                                    Arrays.fill(this.mStartWord, (char) 0);
                                    if (Write_Alpha_recognize(this.nativeContext, this.mStartWord, this.mResultCount) == 0) {
                                        List<T9WriteRecognizeCandidate> recognizeCandidates = new ArrayList<>();
                                        if (Write_Alpha_getCandidates(this.nativeContext, recognizeCandidates, this.mResultCount[0]) > 0 && recognizeCandidates.get(0).mEndWithInstantGesture != 0) {
                                            char gestureChar = normalizeReturnChar(recognizeCandidates.get(0).mCandidate.charAt(recognizeCandidates.get(0).mCandidate.length() - 1));
                                            Candidates candidates = new Candidates(Candidates.Source.HWR);
                                            for (int i = 0; i < recognizeCandidates.size(); i++) {
                                                T9WriteRecognizeCandidate hwrCandidate = recognizeCandidates.get(i);
                                                hwrCandidate.mCandidate = trimInstantGestureCharacterRight(hwrCandidate.mCandidate);
                                                if (hwrCandidate.mCandidate.length() != 0) {
                                                    candidates.add(hwrCandidate.toWordCandidate());
                                                } else {
                                                    Write_Alpha_endArc(this.nativeContext);
                                                    notifyWriteEventListeners(new T9WriteRecognizerListener.InstantGestureWriteEvent(gestureChar, candidates));
                                                    break;
                                                }
                                            }
                                            Write_Alpha_endArc(this.nativeContext);
                                            notifyWriteEventListeners(new T9WriteRecognizerListener.InstantGestureWriteEvent(gestureChar, candidates));
                                        }
                                    }
                                }
                            }
                            break;
                        case 2:
                            if (item instanceof WriteThreadQueue.RecognizeQueueItem) {
                                CharSequence startWord = ((WriteThreadQueue.RecognizeQueueItem) item).mStartWord;
                                copyWordArray(this.mStartWord, startWord);
                                if (Write_Alpha_recognize(this.nativeContext, this.mStartWord, this.mResultCount) == 0) {
                                    List<T9WriteRecognizeCandidate> recognizeCandidates2 = new ArrayList<>();
                                    if (Write_Alpha_getCandidates(this.nativeContext, recognizeCandidates2, this.mResultCount[0]) > 0) {
                                        Candidates candidates2 = new Candidates(Candidates.Source.HWR);
                                        for (int i2 = 0; i2 < recognizeCandidates2.size(); i2++) {
                                            T9WriteRecognizeCandidate hwrCandidate2 = recognizeCandidates2.get(i2);
                                            hwrCandidate2.mCandidate = trimInstantGestureCharacterRight(hwrCandidate2.mCandidate);
                                            if (hwrCandidate2.mCandidate.length() > 0) {
                                                candidates2.add(hwrCandidate2.toWordCandidate());
                                            }
                                        }
                                        Write_Alpha_endArc(this.nativeContext);
                                        if (candidates2.count() > 0) {
                                            Candidates xt9Candidates = buildXT9WordCandiates(recognizeCandidates2.get(0), 10 - candidates2.count());
                                            addXT9WorCandidates(candidates2, xt9Candidates);
                                            notifyWriteEventListeners(new T9WriteRecognizerListener.CandidatesWriteEvent(candidates2));
                                            break;
                                        }
                                    } else {
                                        Write_Alpha_endArc(this.nativeContext);
                                        break;
                                    }
                                }
                            }
                            break;
                        case 3:
                            if (item instanceof WriteThreadQueue.CharQueueItem) {
                                char ch = ((WriteThreadQueue.CharQueueItem) item).mChar;
                                notifyWriteEventListeners(new T9WriteRecognizerListener.CharWriteEvent(ch));
                                break;
                            }
                            break;
                        case 4:
                            if (item instanceof WriteThreadQueue.TextQueueItem) {
                                CharSequence text = ((WriteThreadQueue.TextQueueItem) item).mText;
                                notifyWriteEventListeners(new T9WriteRecognizerListener.TextWriteEvent(text));
                                break;
                            }
                            break;
                        case 5:
                            if (item instanceof WriteThreadQueue.ChangeSettings) {
                                T9WriteSetting settings = ((WriteThreadQueue.ChangeSettings) item).mSettings;
                                Write_Alpha_changeSettings(this.nativeContext, settings);
                                break;
                            }
                            break;
                        case 6:
                            Write_Alpha_beginArc(this.nativeContext);
                            break;
                        case 7:
                            Write_Alpha_endArc(this.nativeContext);
                            break;
                        case 8:
                            if (item instanceof WriteThreadQueue.KeyQueueItem) {
                                int key = ((WriteThreadQueue.KeyQueueItem) item).mKey;
                                notifyWriteEventListeners(new T9WriteRecognizerListener.KeyWriteEvent(key));
                                break;
                            }
                            break;
                    }
                }
            }
        }
    }

    public static void copyWordArray(char[] wordArray, CharSequence word) {
        int i = 0;
        if (word != null) {
            int nchars = Math.min(word.length(), wordArray.length - 1);
            while (i < nchars) {
                wordArray[i] = word.charAt(i);
                i++;
            }
        }
        wordArray[i] = 0;
    }

    private static char normalizeReturnChar(char c) {
        if (c == '\r') {
            return '\n';
        }
        return c;
    }

    private String trimInstantGestureCharacterRight(String text) {
        int end = text.length();
        while (end > 0 && isGestureCharacter(text.charAt(end - 1))) {
            end--;
        }
        if (end < text.length()) {
            return text.substring(0, end);
        }
        return text;
    }

    private boolean isGestureCharacter(char c) {
        return T9WriteCategory.isInstantGestureCharacter(c) || T9WriteCategory.isMultiTouchGesturesCharacter(c);
    }

    public int noteSelectedCandidate(int index) {
        int Write_Alpha_noteSelectedCandidate;
        synchronized (this.mLock) {
            Write_Alpha_noteSelectedCandidate = this.nativeContext != 0 ? Write_Alpha_noteSelectedCandidate(this.nativeContext, index) : -1;
        }
        return Write_Alpha_noteSelectedCandidate;
    }

    public int enableUsageLogging(boolean isEnabled) {
        int i;
        synchronized (this.mLock) {
            if (this.nativeContext != 0) {
                Write_Alpha_enableUsageLogging(this.nativeContext, isEnabled);
                i = 0;
            } else {
                i = -1;
            }
        }
        return i;
    }

    public void enableInstantGesture(boolean value) {
        this.mEnableInstantGesture = value;
    }
}
