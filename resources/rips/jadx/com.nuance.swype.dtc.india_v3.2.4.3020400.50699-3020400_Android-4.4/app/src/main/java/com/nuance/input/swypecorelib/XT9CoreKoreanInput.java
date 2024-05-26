package com.nuance.input.swypecorelib;

import android.graphics.Point;
import android.text.SpannableStringBuilder;
import com.nuance.input.swypecorelib.Shift;
import com.nuance.input.swypecorelib.usagedata.SessionDataCollectorAbstract;
import java.util.List;

/* loaded from: classes.dex */
public class XT9CoreKoreanInput extends XT9CoreInput {
    public static final int MAX_CONTEXT_BUFFER_BEFORE_CURSOR = 16;
    private final XT9CoreAlphaInput alphaCoreInput;
    private final int[] mScratchLen;
    private final char[] mWordBuffer;

    private static native boolean clearSyllable(long j);

    private static native long create_context(String str);

    private static native void destroy_context(long j);

    private static native boolean enableConsonantInput(long j, boolean z);

    private static native void finish(long j);

    private static native int initialize(long j);

    private static native boolean start(long j);

    /* JADX INFO: Access modifiers changed from: package-private */
    public XT9CoreKoreanInput(XT9CoreAlphaInput alphaCoreInput, SessionDataCollectorAbstract sessionDataCollector) {
        super(sessionDataCollector);
        this.mWordBuffer = new char[64];
        this.mScratchLen = new int[1];
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

    public boolean enableConsonantInput(boolean enabled) {
        return enableConsonantInput(this.inputContext, enabled);
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    public boolean clearCharacter() {
        if (getKeyCount() == 0) {
            return false;
        }
        return clearSyllable(this.inputContext);
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

    public boolean addCustomSymbolSet(char[] charBuffer, int len, Shift.ShiftState shiftState) {
        return this.alphaCoreInput.addCustomSymbolSet(charBuffer, len, shiftState);
    }

    public void setExactInList(int location) {
        this.alphaCoreInput.setExactInList(location);
    }

    public void getExactType(StringBuilder exactBuffer) {
        this.alphaCoreInput.getExactType(exactBuffer);
    }

    public void getInlineHangul(StringBuilder inline) {
        if (buildSelectionList(null) > 0 && getWord(0, this.mWordBuffer, this.mScratchLen)) {
            inline.setLength(0);
            inline.append(this.mWordBuffer, 0, this.mScratchLen[0]);
        }
    }

    public int buildSelectionList(int[] defaultWordIndex) {
        return this.alphaCoreInput.buildSelectionList(defaultWordIndex);
    }

    public boolean getWord(int wordIndex, char[] word, int[] wordLen) {
        return this.alphaCoreInput.getWord(wordIndex, word, wordLen);
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    public List<CharSequence> getWords(SpannableStringBuilder defaultWord, int[] defaultWordIndex, int maxWords) {
        return this.alphaCoreInput.getWords(defaultWord, defaultWordIndex, maxWords);
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    public void setMultiTapInputMode(boolean isMultiTap) {
        this.alphaCoreInput.setMultiTapInputMode(isMultiTap);
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    public void setWordQuarantineLevel(int userImplicitAction, int userExplicitAction, int scanUsageAction) {
        this.alphaCoreInput.setWordQuarantineLevel(userImplicitAction, userExplicitAction, scanUsageAction);
    }

    public void setExplicitLearning(boolean enableUserAction, boolean enableScanAction) {
        this.alphaCoreInput.setExplicitLearning(enableUserAction, enableScanAction);
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    public void setTouchRequestCallback(TouchRequestCallback listener) {
        this.alphaCoreInput.setTouchRequestCallback(listener);
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    public void setInputContextRequestListener(InputContextRequest listener) {
        this.alphaCoreInput.setInputContextRequestListener(listener);
    }

    public boolean noteWordDone(String hanguls) {
        return this.alphaCoreInput.noteWordDone(hanguls, 0);
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

    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    public int getInputCoreCategory() {
        return 4;
    }

    public boolean dlmAdd(String word) {
        return this.alphaCoreInput.dlmAdd(word);
    }

    public boolean dlmDelete(String word) {
        return this.alphaCoreInput.dlmDelete(word);
    }

    public boolean dlmFind(String word) {
        return this.alphaCoreInput.dlmFind(word);
    }

    public boolean dlmGetNext(StringBuilder word) {
        return this.alphaCoreInput.dlmGetNext(word);
    }

    public void dlmReset() {
        this.alphaCoreInput.dlmReset();
    }

    public void resetUserDatabases() {
        dlmReset();
    }

    public boolean dlmScanBuf(String words, boolean ishighQuality, boolean sentence, boolean rescanning) {
        return this.alphaCoreInput.dlmScanBuf(words, ishighQuality, sentence, rescanning);
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    protected boolean exportDlmAsEvents() {
        return this.alphaCoreInput.exportDlmAsEvents();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    public void onDlmEvent(byte[] event, int len, boolean highPriority) throws Exception {
        super.onDlmEvent(event, len, highPriority);
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    public boolean processKey(Point point, int key, Shift.ShiftState shiftState, long eventTime) {
        return point == null ? super.processKey(key, shiftState, eventTime) : super.processKey(point, key, shiftState, eventTime);
    }

    @Override // com.nuance.input.swypecorelib.XT9CoreInput
    public boolean isLanguageHaveEmojiPrediction() {
        return true;
    }
}
