package com.nuance.input.swypecorelib;

import android.text.SpannableStringBuilder;
import com.nuance.input.swypecorelib.Shift;
import com.nuance.input.swypecorelib.usagedata.SessionDataCollectorAbstract;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/* loaded from: classes.dex */
public class XT9CoreJapaneseInput extends XT9CoreInput {
    public static final int MAXRAWINPUTLEN = 32;
    public static final int MAXROMAJICANDIDATESNUM = 256;
    public static final int MAXROMAJIWORDLEN = 64;
    public static final int MAXWORDLIST = 32;
    private static final int WORDLEN_INDEX = 0;
    private static final int WORDSUBSTITUTIONLEN_INDEX = 2;
    private final XT9CoreAlphaInput alphaCoreInput;
    private final char[] mWordBuffer;
    private final int[] mWordLenResutls;
    private final List<CharSequence> mWordList;
    private final List<CharSequence> mWordPool;
    private final char[] mWordSubBuffer;

    private static native int buildSelectionList(long j, int[] iArr);

    private static native boolean confirmRangeConvWord(long j, int i);

    private static native long create_context(String str);

    private static native void destroy_context(long j);

    private static native void finish(long j);

    private static native int getExactType(long j, char[] cArr, int i);

    private static native int getInlineDivInfo(long j, int i, int[] iArr, int[] iArr2);

    private static native int getInlineText(long j, char[] cArr, int i);

    private static native boolean getPredictionWord(long j, int i, char[] cArr, char[] cArr2, int[] iArr, int i2);

    private static native int getPredictionWordCount(long j);

    private static native boolean getRangeConvCandidateWord(long j, int i, char[] cArr, int[] iArr);

    private static native int getRangeConvCandidateWordCount(long j);

    private static native int getRangeConvertedPhrase(long j, int i, int[] iArr, char[] cArr, int[] iArr2);

    private static native boolean getWord(long j, int i, char[] cArr, char[] cArr2, int[] iArr, int i2);

    private static native boolean hasTraceInput(long j);

    private static native int initialize(long j);

    private static native int kanatoromaji(long j, char[] cArr, int i, char[] cArr2, int i2, int[] iArr, int i3);

    private static native boolean noteWordDone(long j, char[] cArr, char[] cArr2);

    private static native void resetConversionEngineDictionary();

    private static native void setKeyboardType(long j, int i);

    private static native void setRomajiOnlyFlag(long j, int i);

    private static native boolean start(long j);

    private static native boolean startRangeConversion(long j, int i, char[] cArr, int i2);

    /* JADX INFO: Access modifiers changed from: package-private */
    public XT9CoreJapaneseInput(XT9CoreAlphaInput alphaCoreInput, SessionDataCollectorAbstract sessionDataCollector) {
        super(sessionDataCollector);
        this.mWordBuffer = new char[64];
        this.mWordSubBuffer = new char[64];
        this.mWordLenResutls = new int[3];
        this.mWordList = new ArrayList();
        this.mWordPool = new ArrayList();
        this.alphaCoreInput = alphaCoreInput;
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    protected long create_native_context(String databaseConfigFile) {
        return create_context(databaseConfigFile);
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    protected XT9Status initialize_native_core(long inputContext) {
        return XT9Status.from(initialize(inputContext));
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    protected void destroy_native_context(long inputContext) {
        destroy_context(inputContext);
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    public void startSession() {
        start(this.inputContext);
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    public void finishSession() {
        finish(this.inputContext);
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    public void persistUserDatabase() {
        this.alphaCoreInput.persistUserDatabase();
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    public boolean clearKey() {
        return this.alphaCoreInput.clearKey();
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    public boolean clearAllKeys() {
        return this.alphaCoreInput.clearAllKeys();
    }

    public void setExplicitLearning(boolean enableUserAction, boolean enableScanAction) {
        this.alphaCoreInput.setExplicitLearning(enableUserAction, enableScanAction);
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    public boolean clearKeyByIndex(int index, int count) {
        return this.alphaCoreInput.clearKeyByIndex(index, count);
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    public int getKeyCount() {
        return this.alphaCoreInput.getKeyCount();
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    public boolean hasActiveInput() {
        return this.alphaCoreInput.hasActiveInput();
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    public boolean addExplicit(char[] charBuffer, int len, Shift.ShiftState shiftState) {
        return this.alphaCoreInput.addExplicit(charBuffer, len, shiftState);
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    public void setMultiTapInputMode(boolean isMultiTap) {
        this.alphaCoreInput.setMultiTapInputMode(isMultiTap);
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    public void setTouchRequestCallback(TouchRequestCallback listener) {
        this.alphaCoreInput.setTouchRequestCallback(listener);
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    public void setInputContextRequestListener(InputContextRequest listener) {
        this.alphaCoreInput.setInputContextRequestListener(listener);
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    public void setShiftState(Shift.ShiftState shift) {
        this.alphaCoreInput.setShiftState(shift);
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    public Shift.ShiftState getShiftState() {
        return this.alphaCoreInput.getShiftState();
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    public void setAttribute(int id, int value) {
        this.alphaCoreInput.setAttribute(id, value);
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    public void setAttribute(int id, boolean value) {
        this.alphaCoreInput.setAttribute(id, value);
    }

    public void setPunctuationBreaking(boolean enabled) {
        this.alphaCoreInput.setPunctuationBreaking(enabled);
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    public List<CharSequence> getWords(SpannableStringBuilder defaultWord, int[] defaultWordIndex, int countToGet) {
        recycleWordPool();
        setRomajiOnlyFlag(this.inputContext, 0);
        int wordCount = buildSelectionList(this.inputContext, defaultWordIndex);
        if (wordCount > 0) {
            for (int wordIndex = 0; wordIndex < wordCount && wordIndex < 32; wordIndex++) {
                if (getWord(this.inputContext, wordIndex, this.mWordBuffer, this.mWordSubBuffer, this.mWordLenResutls, 64)) {
                    int wordPoolSize = this.mWordPool.size();
                    SpannableStringBuilder word = wordPoolSize > 0 ? (SpannableStringBuilder) this.mWordPool.remove(wordPoolSize - 1) : new SpannableStringBuilder();
                    word.clear();
                    word.clearSpans();
                    if (getKeyCount() > 1 && this.mWordLenResutls[2] > 0) {
                        word.append((CharSequence) String.valueOf(this.mWordBuffer), 0, this.mWordLenResutls[2]);
                    } else {
                        if (this.mWordLenResutls[0] <= 0) {
                            break;
                        }
                        word.append((CharSequence) String.valueOf(this.mWordBuffer), 0, this.mWordLenResutls[0]);
                    }
                    if (defaultWordIndex[0] == wordIndex) {
                        if (getKeyCount() > 1 && this.mWordLenResutls[2] > 0) {
                            StringBuilder asWord = new StringBuilder(64);
                            asWord.append(this.mWordSubBuffer, 0, this.mWordLenResutls[2]);
                            defaultWord.clear();
                            defaultWord.append((CharSequence) asWord);
                        } else {
                            defaultWord.clear();
                            defaultWord.append((CharSequence) word);
                        }
                    }
                    this.mWordList.add(new SpannableStringBuilder(word));
                }
            }
        }
        return this.mWordList;
    }

    public List<CharSequence> getPredictionWords(SpannableStringBuilder defaultWord, AtomicInteger defaultWordIndex) {
        recycleWordPool();
        int wordCount = getPredictionWordCount(this.inputContext);
        for (int wordIndex = 0; wordIndex < wordCount && wordIndex < 32; wordIndex++) {
            if (getPredictionWord(this.inputContext, wordIndex, this.mWordBuffer, this.mWordSubBuffer, this.mWordLenResutls, 64)) {
                int wordPoolSize = this.mWordPool.size();
                SpannableStringBuilder word = wordPoolSize > 0 ? (SpannableStringBuilder) this.mWordPool.remove(wordPoolSize - 1) : new SpannableStringBuilder();
                word.clear();
                word.clearSpans();
                if (this.mWordLenResutls[0] <= 0) {
                    break;
                }
                word.append((CharSequence) String.valueOf(this.mWordBuffer), 0, this.mWordLenResutls[0]);
                if (defaultWordIndex.get() == wordIndex) {
                    defaultWord.clear();
                    defaultWord.append((CharSequence) word);
                }
                this.mWordList.add(wordIndex, new SpannableStringBuilder(word));
            }
        }
        return this.mWordList;
    }

    public void breakContext() {
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    public void setContext(char[] wordContext) {
    }

    public void getExactType(StringBuilder exactBuffer) {
        if (exactBuffer != null) {
            exactBuffer.setLength(0);
            int len = getExactType(this.inputContext, this.mWordBuffer, 256);
            if (len > 0) {
                exactBuffer.append(this.mWordBuffer, 0, len);
            }
        }
    }

    public void getInlineText(SpannableStringBuilder inlineBuffer) {
        if (inlineBuffer != null) {
            inlineBuffer.clear();
            int len = getInlineText(this.inputContext, this.mWordBuffer, 64);
            if (len > 0) {
                for (int i = 0; i < len; i++) {
                    inlineBuffer.append(this.mWordBuffer[i]);
                }
            }
        }
    }

    public boolean isInlineKnown() {
        return false;
    }

    private void recycleWordPool() {
        int wordPoolSize = this.mWordPool.size();
        for (int wordListSize = this.mWordList.size(); wordPoolSize < 32 && wordListSize > 0; wordListSize--) {
            CharSequence collect = this.mWordList.remove(wordListSize - 1);
            if (collect != null) {
                this.mWordPool.add(collect);
                wordPoolSize++;
            }
        }
        this.mWordList.clear();
    }

    public int kanatoRomaji(char[] kana, int len, char[] romaji, int[] plen) {
        return kanatoromaji(this.inputContext, kana, len, romaji, 64, plen, 0);
    }

    public boolean startRangeConversion(int type, char[] wordToConv, int wordLen) {
        return startRangeConversion(this.inputContext, type, wordToConv, wordLen);
    }

    public int getRangeConvertedPhrase(int index, int[] readingDivInfo, char[] phrase, int[] outPhraseInfo) {
        return getRangeConvertedPhrase(this.inputContext, index, readingDivInfo, phrase, outPhraseInfo);
    }

    public boolean getRangeConvCandidateWord(int index, char[] word, int[] wordLen) {
        return getRangeConvCandidateWord(this.inputContext, index, word, wordLen);
    }

    public int getRangeConvCandidateWordCount() {
        return getRangeConvCandidateWordCount(this.inputContext);
    }

    public boolean confirmRangeConvWord(int index) {
        return confirmRangeConvWord(this.inputContext, index);
    }

    public boolean noteWordDone(String yomi, String midashigo) {
        return noteWordDone(this.inputContext, yomi.toCharArray(), midashigo.toCharArray());
    }

    public void setKeyboardType(int type) {
        setKeyboardType(this.inputContext, type);
    }

    public List<CharSequence> getRangeConvCandidateList(SpannableStringBuilder defaultWord, int defaultWordIndex) {
        int[] wordLen = new int[1];
        recycleWordPool();
        int wordCount = getRangeConvCandidateWordCount();
        int listIndex = 0;
        for (int wordIndex = 0; wordIndex < wordCount && listIndex < 32; wordIndex++) {
            if (getRangeConvCandidateWord(wordIndex, this.mWordBuffer, wordLen)) {
                int wordPoolSize = this.mWordPool.size();
                SpannableStringBuilder word = wordPoolSize > 0 ? (SpannableStringBuilder) this.mWordPool.remove(wordPoolSize - 1) : new SpannableStringBuilder();
                word.clear();
                word.clearSpans();
                if (wordLen[0] <= 0) {
                    break;
                }
                word.append((CharSequence) String.valueOf(this.mWordBuffer), 0, wordLen[0]);
                if (defaultWordIndex == wordIndex) {
                    defaultWord.clear();
                    defaultWord.append((CharSequence) word);
                }
                this.mWordList.add(listIndex, new SpannableStringBuilder(word));
                listIndex++;
            }
        }
        return this.mWordList;
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    public int getInputCoreCategory() {
        return 3;
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    protected boolean exportDlmAsEvents() {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    public void onDlmEvent(byte[] event, int len, boolean highPriority) throws Exception {
        super.onDlmEvent(event, len, highPriority);
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    public boolean isLanguageHaveEmojiPrediction() {
        return false;
    }

    public int getInlineDivInfo(int maxSize, int[] romajiInDivInfo, int[] kanaOutDivInfo) {
        Arrays.fill(romajiInDivInfo, 0);
        Arrays.fill(kanaOutDivInfo, 0);
        return getInlineDivInfo(this.inputContext, maxSize, romajiInDivInfo, kanaOutDivInfo);
    }

    public boolean hasTraceInput() {
        return hasTraceInput(this.inputContext);
    }

    public static void resetUserDictionary() {
        if (SwypeCoreLibrary.isJapaneseEnabled()) {
            resetConversionEngineDictionary();
        } else {
            XT9CoreJapaneseInput.class.getSimpleName();
        }
    }
}
