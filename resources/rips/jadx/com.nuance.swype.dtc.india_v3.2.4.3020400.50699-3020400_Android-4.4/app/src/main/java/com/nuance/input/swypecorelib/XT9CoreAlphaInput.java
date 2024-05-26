package com.nuance.input.swypecorelib;

import android.text.SpannableStringBuilder;
import com.nuance.input.swypecorelib.Shift;
import com.nuance.input.swypecorelib.XT9CoreInput;
import com.nuance.input.swypecorelib.usagedata.SessionDataCollector;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class XT9CoreAlphaInput extends XT9CoreInput {
    public static final int ET9AEXACTINLIST_DEFAULT = 3;
    public static final int ET9AEXACTINLIST_DEFAULT_ALLOW_DLM_CORRECTIONS = 4;
    public static final int ET9AEXACTINLIST_FIRST = 1;
    public static final int ET9AEXACTINLIST_FIRST_ALLOW_MULTITAP_CORRECTIONS = 5;
    public static final int ET9AEXACTINLIST_LAST = 2;
    public static final int ET9AEXACTINLIST_OFF = 0;
    private static final int MAX_TERMINAL_PUNCT = 16;
    private DLMWipeEventCallback dlmWipeCallback;
    private ExplicitLearningApprovalCallback explicitApprovalCb;
    private final char[] mWordBuffer;
    private final int[] mWordLenResults;
    private final List<CharSequence> mWordList;
    private final List<CharSequence> mWordPool;
    private XT9CoreAlphaInputTextChecker textChecker;
    private WordRecaptureCallback wordRecaptureCallback;

    /* loaded from: classes.dex */
    public interface DLMWipeEventCallback {
        boolean onRequestLoggingDLMWipeEvent(String str, int i, int i2, int i3);
    }

    /* loaded from: classes.dex */
    public interface ExplicitLearningApprovalCallback {
        boolean onRequestExplicitLearningApproval(String str, int i);
    }

    /* loaded from: classes.dex */
    public interface WordRecaptureCallback {
        void recapture(char[] cArr);
    }

    private static native boolean addCustomSymbolSet(long j, char[] cArr, int i, int i2);

    private static native boolean addCustomWords(long j, char[] cArr, int i);

    private static native boolean addDlmCategoryWord(long j, int i, char[] cArr);

    private static native boolean addExplicit(long j, char[] cArr, int i, int i2);

    private static native int buildSelectionList(long j, int[] iArr);

    private static native boolean clearAllKeys(long j);

    private static native void clearApplicationPredictionContext(long j);

    private static native boolean clearKey(long j);

    private static native boolean clearKeyByIndex(long j, int i, int i2);

    private static native boolean createDlmCategoryInfo(long j, int i, char[] cArr, char[] cArr2);

    private static native long create_context(String str);

    private static native boolean deleteDlmCategory(long j, int i);

    private static native void destroy_context(long j);

    private static native boolean dlm_add(long j, char[] cArr, int i);

    private static native boolean dlm_addBlackWordForLanguage(long j, char[] cArr, int i, int i2);

    private static native boolean dlm_addNewWordForLanguage(long j, char[] cArr, int i, int i2);

    private static native boolean dlm_backup(long j);

    private static native int dlm_count(long j);

    private static native boolean dlm_delete(long j, char[] cArr, int i);

    private static native long dlm_export(long j, String str);

    private static native boolean dlm_find(long j, char[] cArr, int i);

    private static native boolean dlm_getNext(long j, char[] cArr, int[] iArr, int i);

    private static native boolean dlm_implicit_scanBuf(long j, char[] cArr, int i, int i2, boolean z, boolean z2, char[] cArr2, int i3);

    private static native void dlm_reset(long j);

    private static native boolean dlm_scanBuf(long j, char[] cArr, int i, int i2, boolean z, boolean z2);

    private static native boolean dlm_swap_language(long j, int i, int i2);

    private static native void explicitLearningAddAllRecentWords(long j);

    private static native void explicitLearningAddLastWord(long j);

    private static native void explicitLearningAddRecentWord(long j, int i);

    private static native void finish(long j);

    private static native char getDefaultWordSeparator(long j);

    private static native int getDlmCategoryCount(long j);

    private static native boolean getDlmCategoryInfo(long j, int i, int[] iArr, char[] cArr, int[] iArr2, char[] cArr2, int[] iArr3);

    private static native int getExactType(long j, char[] cArr, int i);

    private static native void getExplicitLearning(long j, boolean[] zArr);

    private static native int getKeyCount(long j);

    private static native int getShiftGestureMargin(long j);

    private static native int getShiftState(long j);

    private static native boolean getWord(long j, int i, char[] cArr, int[] iArr, int i2);

    private static native int get_terminal_punct(long j, char[] cArr, int i);

    private static native boolean hasActiveInput(long j);

    private static native int initialize(long j, XT9CoreAlphaInput xT9CoreAlphaInput);

    private static native boolean isEmojiSupported(long j);

    private static native boolean isInlineKnown(long j);

    private static native boolean isLDBSupportALM(long j);

    private static native boolean isLowerSymbol(long j, char c);

    private static native boolean isNullLdb(long j);

    private static native boolean isShiftGesture(long j);

    private static native boolean isUpperSymbol(long j, char c);

    private static native boolean is_known_word(long j, char[] cArr, int i);

    private static native boolean learnNewWords(long j, char[] cArr, int i);

    private static native boolean noteWordChanged(long j, char[] cArr, int i, int i2, char[] cArr2);

    private static native void persistUserDatabase(long j);

    private static native void registerDLMWipeCallback(long j, XT9CoreAlphaInput xT9CoreAlphaInput);

    private static native void registerExplicitLearningApprovalCallback(long j, XT9CoreAlphaInput xT9CoreAlphaInput);

    private static native boolean removeSpaceBeforeWord(long j, int i);

    private static native void setApplicationPredictionContext(long j, byte[] bArr, byte[] bArr2, byte[] bArr3);

    private static native void setAttribute(long j, int i, int i2);

    private static native void setAutoSpace(long j, boolean z);

    private static native void setBackCorrection(long j, boolean z);

    private static native void setExactInList(long j, int i);

    private static native void setExplicitLearning(long j, boolean z, boolean z2);

    private static native void setPunctuationBreaking(long j, boolean z);

    private static native void setShiftGestureMargin(long j, int i);

    private static native void setShiftState(long j, int i);

    private static native boolean setVietInputMode(long j, int i);

    private static native void setWordQuarantineLevel(long j, int i, int i2, int i3);

    private static native boolean start(long j);

    private static native char toLowerSymbol(long j, char c);

    private static native char toUpperSymbol(long j, char c);

    private static native void unRegisterDLMWipeCallback(long j);

    private static native void unRegisterExplicitLearningApprovalCallback(long j);

    private static native boolean undoAccept(long j, char[] cArr, int i, int[] iArr);

    /* JADX INFO: Access modifiers changed from: protected */
    public XT9CoreAlphaInput(SessionDataCollector sessionDataCollector) {
        super(sessionDataCollector);
        this.mWordBuffer = new char[64];
        this.mWordLenResults = new int[3];
        this.mWordList = new ArrayList();
        this.mWordPool = new ArrayList();
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    protected long create_native_context(String databaseConfigFile) {
        return create_context(databaseConfigFile);
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    protected XT9Status initialize_native_core(long inputContext) {
        XT9Status status = XT9Status.from(initialize(inputContext, this));
        registerExplicitLearningApprovalCallback(inputContext, this);
        registerDLMWipeCallback(inputContext, this);
        return status;
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    protected void destroy_native_context(long inputContext) {
        unRegisterDLMWipeCallback(inputContext);
        unRegisterExplicitLearningApprovalCallback(inputContext);
        destroy_context(inputContext);
        this.textChecker = null;
    }

    public XT9CoreAlphaInputTextChecker getAlphaInputTextCheckerInstance() {
        if (this.textChecker == null) {
            this.textChecker = new XT9CoreAlphaInputTextChecker(this);
        }
        return this.textChecker;
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    public void startSession() {
        start(this.inputContext);
        registerExplicitLearningApprovalCallback(this.inputContext, this);
        this.sessionDataCollector.onStartInputSession();
    }

    public void setBackCorrection(boolean value) {
        setBackCorrection(this.inputContext, value);
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    public void finishSession() {
        finish(this.inputContext);
        this.sessionDataCollector.onFinishInputSession();
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    public void persistUserDatabase() {
        persistUserDatabase(this.inputContext);
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    public boolean clearKey() {
        return clearKey(this.inputContext);
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    public boolean clearAllKeys() {
        return clearAllKeys(this.inputContext);
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    public boolean clearKeyByIndex(int index, int count) {
        return clearKeyByIndex(this.inputContext, index, count);
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    public int getKeyCount() {
        return getKeyCount(this.inputContext);
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    public boolean hasActiveInput() {
        return hasActiveInput(this.inputContext);
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    public void setAttribute(int id, boolean value) {
        setAttribute(this.inputContext, id, value ? 1 : 0);
        if (id == 99) {
            this.sessionDataCollector.onAutoCorrectionEnabled(value);
        }
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    public void setAttribute(int id, int value) {
        setAttribute(this.inputContext, id, value);
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    public boolean addExplicit(char[] charBuffer, int len, Shift.ShiftState shiftState) {
        return addExplicit(this.inputContext, charBuffer, len, shiftState.getIndex());
    }

    public boolean addCustomSymbolSet(char[] charBuffer, int len, Shift.ShiftState shiftState) {
        return addCustomSymbolSet(this.inputContext, charBuffer, len, shiftState.getIndex());
    }

    public boolean addCustomSymbol(char chr, Shift.ShiftState shiftState) {
        return addCustomSymbolSet(new char[]{chr, 7615}, 2, shiftState);
    }

    public int buildSelectionList(int[] defaultWordIndex) {
        return buildSelectionList(this.inputContext, defaultWordIndex);
    }

    public boolean getWord(int wordIndex, char[] word, int[] wordLen) {
        return getWord(this.inputContext, wordIndex, word, wordLen, 64);
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    public List<CharSequence> getWords(SpannableStringBuilder defaultWord, int[] defaultWordIndex, int maxWords) {
        recycleWordPool();
        int[] wordLen = new int[1];
        int wordCount = buildSelectionList(defaultWordIndex);
        if (wordCount > 0) {
            for (int wordIndex = 0; wordIndex < wordCount && wordIndex < 10; wordIndex++) {
                if (getWord(wordIndex, this.mWordBuffer, wordLen)) {
                    int wordPoolSize = this.mWordPool.size();
                    SpannableStringBuilder word = wordPoolSize > 0 ? (SpannableStringBuilder) this.mWordPool.remove(wordPoolSize - 1) : new SpannableStringBuilder();
                    word.clear();
                    word.clearSpans();
                    if (wordLen[0] > 0) {
                        word.append((CharSequence) String.valueOf(this.mWordBuffer), 0, wordLen[0]);
                    }
                    if (defaultWordIndex[0] == wordIndex) {
                        defaultWord.clear();
                        defaultWord.append((CharSequence) word);
                    }
                    this.mWordList.add(wordIndex, new SpannableStringBuilder(word));
                }
            }
        }
        return this.mWordList;
    }

    private void recycleWordPool() {
        int wordPoolSize = this.mWordPool.size();
        for (int wordListSize = this.mWordList.size(); wordPoolSize < 10 && wordListSize > 0; wordListSize--) {
            CharSequence collect = this.mWordList.remove(wordListSize - 1);
            if (collect != null) {
                this.mWordPool.add(collect);
                wordPoolSize++;
            }
        }
        this.mWordList.clear();
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    public boolean noteWordDone(String buffer, int cursorPosition) {
        return learnNewWords(this.inputContext, buffer.toCharArray(), cursorPosition);
    }

    public boolean noteWordWordChanged(String buffer, int startOfNewWord, int newWordLength, String oldWord) {
        return noteWordChanged(this.inputContext, buffer.toCharArray(), startOfNewWord, newWordLength, oldWord.toCharArray());
    }

    public boolean addCustomWords(String buffer, int wordQuality) {
        if (buffer == null || buffer.length() <= 0) {
            return false;
        }
        return addCustomWords(this.inputContext, buffer.toCharArray(), wordQuality);
    }

    public void getExactType(StringBuilder exactBuffer) {
        if (exactBuffer != null) {
            exactBuffer.setLength(0);
            int len = getExactType(this.inputContext, this.mWordBuffer, 64);
            if (len > 0) {
                exactBuffer.append(this.mWordBuffer, 0, len);
            }
        }
    }

    public String getTerminalPunct() {
        int len = get_terminal_punct(this.inputContext, this.mWordBuffer, 16);
        if (len <= 0) {
            return "";
        }
        String terminalPunct = new String(this.mWordBuffer, 0, len);
        return terminalPunct;
    }

    public boolean isInlineKnown() {
        return isInlineKnown(this.inputContext);
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    public boolean removeSpaceBeforeWord(int wordIndex) {
        return removeSpaceBeforeWord(this.inputContext, wordIndex);
    }

    public boolean isSymbolUpperCase(char symbol) {
        return isUpperSymbol(this.inputContext, symbol);
    }

    public boolean isSymbolLowerCase(char symbol) {
        return isLowerSymbol(this.inputContext, symbol);
    }

    public char toUpperSymbol(char symbol) {
        return toUpperSymbol(this.inputContext, symbol);
    }

    public char toLowerSymbol(char symbol) {
        return toLowerSymbol(this.inputContext, symbol);
    }

    public boolean isLanguageSupportALM() {
        return isLDBSupportALM(this.inputContext);
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    public void setShiftState(Shift.ShiftState shift) {
        setShiftState(this.inputContext, shift.getIndex());
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    public Shift.ShiftState getShiftState() {
        return Shift.ShiftState.valueOf(getShiftState(this.inputContext));
    }

    public boolean isKnownWord(String word) {
        return is_known_word(this.inputContext, word.toCharArray(), word.length());
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    public void setWordQuarantineLevel(int userImplicitAction, int userExplicitAction, int scanUsage) {
        setWordQuarantineLevel(this.inputContext, userImplicitAction, userExplicitAction, scanUsage);
    }

    public void setExactInList(int location) {
        setExactInList(this.inputContext, location);
    }

    public boolean dlmAdd(String word) {
        return dlm_add(this.inputContext, word.toCharArray(), word.length());
    }

    public boolean dlmDelete(String word) {
        return dlm_delete(this.inputContext, word.toCharArray(), word.length());
    }

    public boolean dlmFind(String word) {
        return dlm_find(this.inputContext, word.toCharArray(), word.length());
    }

    public boolean dlmGetNext(StringBuilder word) {
        this.mWordLenResults[0] = word.length();
        if (word.length() != 0) {
            word.getChars(0, word.length(), this.mWordBuffer, 0);
        }
        word.setLength(0);
        if (dlm_getNext(this.inputContext, this.mWordBuffer, this.mWordLenResults, 64)) {
            word.append(this.mWordBuffer, 0, this.mWordLenResults[0]);
        }
        return word.length() != 0;
    }

    public int dlmCount() {
        return dlm_count(this.inputContext);
    }

    public void dlmReset() {
        dlm_reset(this.inputContext);
    }

    public void resetUserDatabases() {
        dlmReset();
    }

    public boolean dlmScanBuf(String words, boolean isHighQuality, boolean sentenceBased, boolean rescanning) {
        return dlm_scanBuf(this.inputContext, words.toCharArray(), words.length(), isHighQuality ? 1 : 0, sentenceBased, rescanning);
    }

    public boolean setVietInputMode(XT9CoreInput.XT9InputMode inputMode) {
        return setVietInputMode(this.inputContext, inputMode.value());
    }

    public boolean dlmImplicitScanBuf(String words, boolean isHighQuality, boolean sentenceBased, boolean rescanning, String prediction) {
        return dlm_implicit_scanBuf(this.inputContext, words.toCharArray(), words.length(), isHighQuality ? 1 : 0, sentenceBased, rescanning, prediction != null ? prediction.toCharArray() : null, prediction != null ? prediction.length() : 0);
    }

    public long dlmExport(String filePath) {
        return dlm_export(this.inputContext, filePath);
    }

    public void setPunctuationBreaking(boolean enable) {
        setPunctuationBreaking(this.inputContext, enable);
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    public int getInputCoreCategory() {
        return 1;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    public boolean exportDlmAsEvents() {
        return dlm_backup(this.inputContext);
    }

    public void setWordRecaptureCallback(WordRecaptureCallback cb) {
        this.wordRecaptureCallback = cb;
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    public RecaptureInfo recaptureWord(char[] charBuffer, boolean isSelection) {
        RecaptureInfo recaptureInfo = super.recaptureWord(charBuffer, isSelection);
        if (recaptureInfo.totalWord > 0 && this.wordRecaptureCallback != null) {
            this.wordRecaptureCallback.recapture(charBuffer);
        }
        if (recaptureInfo != RecaptureInfo.EMPTY_RECAPTURE_INFO) {
            this.sessionDataCollector.onRecapture(charBuffer);
        }
        return recaptureInfo;
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    public RecaptureInfo undoAccept(char[] word, int cursorPosition) {
        int[] undoAcceptInfo = RecaptureInfo.allocateRecaptureInfoFieldInfoArray();
        if (!undoAccept(this.inputContext, word, cursorPosition, undoAcceptInfo)) {
            return RecaptureInfo.EMPTY_RECAPTURE_INFO;
        }
        this.sessionDataCollector.onRecapture(word);
        return new RecaptureInfo(undoAcceptInfo, word);
    }

    public void candidateSelected(WordCandidate candidate, Candidates candidates, boolean autoAccepted) {
        wordSelected(candidate.id(), !autoAccepted);
        this.sessionDataCollector.onCandidateSelected(candidate, candidates, autoAccepted);
    }

    public int getDlmCategoryCount() {
        return getDlmCategoryCount(this.inputContext);
    }

    public DlmCategoryInfo getDlmCategoryInfo(int categoryIndex) {
        return getDlmCategoryInfo(this.inputContext, categoryIndex, DlmCategoryInfo.nativeID, DlmCategoryInfo.nativeName, DlmCategoryInfo.nativeNameLen, DlmCategoryInfo.nativeInfo, DlmCategoryInfo.nativeInfoLen) ? new DlmCategoryInfo(DlmCategoryInfo.nativeID[0], new String(DlmCategoryInfo.nativeName, 0, DlmCategoryInfo.nativeNameLen[0]), new String(DlmCategoryInfo.nativeInfo, 0, DlmCategoryInfo.nativeInfoLen[0])) : DlmCategoryInfo.EMPTY;
    }

    public boolean createDlmCategoryInfo(int categoryID, String name, String info) {
        return createDlmCategoryInfo(this.inputContext, categoryID, name.toCharArray(), info.toCharArray());
    }

    public boolean deleteDlmCategory(int categoryID) {
        return deleteDlmCategory(this.inputContext, categoryID);
    }

    public boolean addDlmCategoryWord(int categoryID, String word) {
        return addDlmCategoryWord(this.inputContext, categoryID, word.toCharArray());
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    public boolean reconstructWord(char[] charBuffer) {
        return reconstructWord(charBuffer, true);
    }

    public boolean reconstructWord(char[] charBuffer, boolean shouldRecapture) {
        boolean ret = super.reconstructWord(charBuffer);
        if (shouldRecapture && ret && this.wordRecaptureCallback != null) {
            this.wordRecaptureCallback.recapture(charBuffer);
        }
        if (ret) {
            this.sessionDataCollector.onRecaptureEdit(charBuffer);
        }
        return ret;
    }

    public char getDefaultWordSeparator() {
        return getDefaultWordSeparator(this.inputContext);
    }

    public boolean requestExplicitLearningApproval(String oov, int syncId) {
        if (this.explicitApprovalCb != null) {
            return this.explicitApprovalCb.onRequestExplicitLearningApproval(oov, syncId);
        }
        return false;
    }

    public boolean requestLoggingDLMWipeEvent(String word, int requestType, int reasonCode, int langaugeId) {
        if (this.dlmWipeCallback != null) {
            return this.dlmWipeCallback.onRequestLoggingDLMWipeEvent(word, requestType, reasonCode, langaugeId);
        }
        return false;
    }

    public void setExplicitLearning(boolean enableUserAction, boolean enableScanAction) {
        setExplicitLearning(this.inputContext, enableUserAction, enableScanAction);
    }

    public boolean[] getExplicitLearning() {
        boolean[] actions = new boolean[2];
        getExplicitLearning(this.inputContext, actions);
        return actions;
    }

    public void explicitLearningAddLastWord() {
        explicitLearningAddLastWord(this.inputContext);
    }

    public void explicitLearningAddAllRecentWords() {
        explicitLearningAddAllRecentWords(this.inputContext);
    }

    public void explicitLearningAddRecentWord(int syncId) {
        explicitLearningAddRecentWord(this.inputContext, syncId);
    }

    public void registerExplicitLearningApprovalCallback(ExplicitLearningApprovalCallback cb) {
        this.explicitApprovalCb = cb;
    }

    public void unRegisterExplicitLearningApprovalCallback(ExplicitLearningApprovalCallback cb) {
        this.explicitApprovalCb = null;
    }

    public void registerLoggingDLMWipeEventCallback(DLMWipeEventCallback cb) {
        this.dlmWipeCallback = cb;
    }

    public void unRegisterLoggingDLMWipeCallback(DLMWipeEventCallback cb) {
        this.dlmWipeCallback = null;
    }

    public void clearApplicationPredictionContext() {
        clearApplicationPredictionContext(this.inputContext);
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    public boolean dlmSwapLanguage(int from, int to) {
        this.mLanguageID = to;
        return dlm_swap_language(this.inputContext, from, to);
    }

    public int getShiftGestureMargin() {
        return getShiftGestureMargin(this.inputContext);
    }

    public void setShiftGestureMargin(int topOfMargin) {
        setShiftGestureMargin(this.inputContext, topOfMargin);
    }

    public boolean isShiftGesture() {
        return isShiftGesture(this.inputContext);
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    protected void onDlmEvent(byte[] event, int len, boolean highPriority) throws Exception {
        super.onDlmEvent(event, len, highPriority);
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    public boolean isNullLdb() {
        return isNullLdb(this.inputContext);
    }

    public boolean dlmAddBlackWordForLanguage(String word, int languageId) {
        char[] wordChars = word.toCharArray();
        int length = word.length();
        return dlm_addBlackWordForLanguage(this.inputContext, wordChars, length, languageId);
    }

    public boolean dlmAddNewWordForLanguage(String word, int languageId) {
        char[] wordChars = word.toCharArray();
        int length = word.length();
        return dlm_addNewWordForLanguage(this.inputContext, wordChars, length, languageId);
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    public boolean isLanguageHaveEmojiPrediction() {
        return isEmojiSupported(this.inputContext);
    }

    public void setAutoSpace(boolean value) {
        setAutoSpace(this.inputContext, value);
    }
}
