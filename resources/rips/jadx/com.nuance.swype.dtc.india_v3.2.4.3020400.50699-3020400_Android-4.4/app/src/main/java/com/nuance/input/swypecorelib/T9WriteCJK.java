package com.nuance.input.swypecorelib;

import android.graphics.Point;
import com.nuance.input.swypecorelib.T9WriteRecognizerListener;
import com.nuance.input.swypecorelib.WriteThreadQueue;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/* loaded from: classes.dex */
public abstract class T9WriteCJK extends T9Write {
    public static final int MAXWORDLEN = 32;
    protected int[] mEndsWithInstGest;
    protected int[] mResultCount;
    protected int[] mResultType;
    protected char[] mScratchBuffer;
    protected int[] mScratchInt;
    protected int[] mScratchIntWordSource;
    protected char[] mStartWord;
    protected String mTextContext;
    protected char[] mWordBuffer;
    protected boolean nativeContextStarted;

    private static native int Write_CJK_addArc(long j, List<Point> list);

    private static native int Write_CJK_beginArc(long j);

    private static native int Write_CJK_changeSettings(long j, T9WriteSetting t9WriteSetting);

    private static native int Write_CJK_clearCommonChar(long j);

    public static native void Write_CJK_enableUsageLogging(long j, boolean z);

    private static native int Write_CJK_endArc(long j);

    private static native int Write_CJK_finish(long j);

    private static native String Write_CJK_getDatabaseVersion(long j);

    private static native int Write_CJK_getRecognitionCandidate(long j, int i, char[] cArr, int i2, int[] iArr, int[] iArr2, int[] iArr3);

    private static native String Write_CJK_getVersion(long j);

    private static native boolean Write_CJK_getWord(long j, int i, char[] cArr, int[] iArr, int i2, int[] iArr2);

    private static native int Write_CJK_noteSelectedCandidate(long j, int i);

    private static native int Write_CJK_recognize(long j, char[] cArr, int[] iArr);

    private static native boolean Write_CJK_setAttribute(long j, int i, int i2);

    private static native int Write_CJK_setCommonChar(long j);

    private static native boolean Write_CJK_setContext(long j, char[] cArr, int i);

    private static native int Write_CJK_start(long j, T9WriteSetting t9WriteSetting, int i);

    public static native void Write_CJK_updateAlternativeDirection(long j, boolean z);

    public static native void Write_CJK_updateAttachingCommonWordsLDB(long j, boolean z);

    public static native void Write_CJK_updateRotationSetting(long j, boolean z);

    /* JADX INFO: Access modifiers changed from: package-private */
    public T9WriteCJK(T9WriteCJKSetting settings) {
        super(settings);
        this.mScratchBuffer = new char[32];
        this.mScratchInt = new int[1];
        this.mStartWord = new char[21];
        this.mWordBuffer = new char[20];
        this.mResultCount = new int[1];
        this.mEndsWithInstGest = new int[1];
        this.mResultType = new int[1];
        this.mTextContext = "";
        this.mScratchIntWordSource = new int[1];
    }

    @Override // com.nuance.input.swypecorelib.T9Write
    public void startSession(int languageID) {
        if (this.nativeContext != 0) {
            this.mLanguageID = languageID;
            this.mWriteThreadQueue.clearQueue();
            this.nativeContextStarted = Write_CJK_start(this.nativeContext, this.settings, languageID) == 0;
        }
    }

    @Override // com.nuance.input.swypecorelib.T9Write
    public void finishSession() {
        synchronized (this.mLock) {
            this.mWriteThreadQueue.clearQueue();
            if (this.nativeContext != 0) {
                Write_CJK_finish(this.nativeContext);
            }
            this.nativeContextStarted = false;
        }
    }

    @Override // com.nuance.input.swypecorelib.T9Write
    public String getVersion() {
        return this.nativeContext != 0 ? Write_CJK_getVersion(this.nativeContext) : "";
    }

    @Override // com.nuance.input.swypecorelib.T9Write
    public String getDatabaseVersion() {
        return this.nativeContext != 0 ? Write_CJK_getDatabaseVersion(this.nativeContext) : "";
    }

    @Override // com.nuance.input.swypecorelib.WriteThreadQueue.OnWriteThreadQueueConsumer
    public void consume(WriteThreadQueue.QueueItem item) {
        if (this.nativeContext != 0) {
            synchronized (this.mLock) {
                if (this.nativeContextStarted && this.nativeContext != 0) {
                    switch (item.mType) {
                        case 1:
                            if (item instanceof WriteThreadQueue.ArcQueueItem) {
                                Write_CJK_addArc(this.nativeContext, ((WriteThreadQueue.ArcQueueItem) item).mArc1);
                                break;
                            }
                            break;
                        case 2:
                            List<CharSequence> candidates = getRecognizeCandidates();
                            notifyWriteEventListeners(new T9WriteRecognizerListener.CandidatesWriteEvent(candidates));
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
                                Write_CJK_changeSettings(this.nativeContext, settings);
                                break;
                            }
                            break;
                        case 6:
                            Write_CJK_beginArc(this.nativeContext);
                            break;
                        case 7:
                            Write_CJK_endArc(this.nativeContext);
                            break;
                    }
                }
            }
        }
    }

    private List<CharSequence> getRecognizeCandidates() {
        LinkedList<CharSequence> candidates = new LinkedList<>();
        this.mStartWord[0] = 0;
        if (Write_CJK_recognize(this.nativeContext, this.mStartWord, this.mResultCount) == 0) {
            for (int i = 0; i < this.mResultCount[0]; i++) {
                if (Write_CJK_getRecognitionCandidate(this.nativeContext, i, this.mWordBuffer, 20, this.mScratchInt, this.mEndsWithInstGest, this.mResultType) == 0 && (i <= 0 || this.mEndsWithInstGest[0] != 1)) {
                    candidates.add(ChineseConversionUtil.appendWithTransform(new StringBuilder(), this.mWordBuffer, 0, this.mScratchInt[0], isChineseTraditional()));
                }
            }
        }
        return candidates;
    }

    public boolean isChineseTraditional() {
        return this.mLanguageID == 224 || this.mLanguageID == 226;
    }

    public boolean setContext(CharSequence newContext) {
        String context;
        if (newContext == null) {
            context = "";
        } else {
            context = newContext.toString();
        }
        if (this.mTextContext.compareTo(context) == 0) {
            return false;
        }
        boolean b = Write_CJK_setContext(this.nativeContext, context.toCharArray(), context.length());
        if (b) {
            this.mTextContext = context;
            return b;
        }
        return b;
    }

    public boolean getWord(int index, StringBuilder word, AtomicInteger wordSource) {
        wordSource.set(0);
        word.setLength(0);
        boolean success = Write_CJK_getWord(this.nativeContext, index, this.mScratchBuffer, this.mScratchInt, this.mScratchBuffer.length, this.mScratchIntWordSource);
        if (success) {
            ChineseConversionUtil.appendWithTransform(word, this.mScratchBuffer, 0, this.mScratchInt[0], isChineseTraditional());
            wordSource.set(this.mScratchIntWordSource[0]);
        }
        return success;
    }

    public boolean setAttribute(int id, int value) {
        return Write_CJK_setAttribute(this.nativeContext, id, value);
    }

    public boolean noteSelectedCandidate(int wordIndex) {
        return Write_CJK_noteSelectedCandidate(this.nativeContext, wordIndex) == 0;
    }

    public int learnNewWord(CharSequence word) {
        return 0;
    }

    public int setCommonChar() {
        return Write_CJK_setCommonChar(this.nativeContext);
    }

    public int clearCommonChar() {
        return Write_CJK_clearCommonChar(this.nativeContext);
    }

    public int enableUsageLogging(boolean isEnabled) {
        if (this.nativeContext == 0) {
            return -1;
        }
        Write_CJK_enableUsageLogging(this.nativeContext, isEnabled);
        return 0;
    }

    @Override // com.nuance.input.swypecorelib.T9Write
    public void setWritingDirection(int direction) {
        this.settings.setWritingDirection(direction);
    }

    public void updateRotationSetting(boolean isEnabled) {
        if (this.nativeContext != 0) {
            Write_CJK_updateRotationSetting(this.nativeContext, isEnabled);
        }
    }

    public void updateAttachingCommonWordsLDB(boolean isEnabled) {
        if (this.nativeContext != 0) {
            Write_CJK_updateAttachingCommonWordsLDB(this.nativeContext, isEnabled);
        }
    }

    public void updateAlternativeDirection(boolean isEnabled) {
        if (this.nativeContext != 0) {
            Write_CJK_updateAlternativeDirection(this.nativeContext, isEnabled);
        }
    }
}
