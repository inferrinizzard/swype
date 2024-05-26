package com.nuance.swype.input;

import android.os.Message;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputConnection;
import com.nuance.android.compat.InputConnectionCompat;
import com.nuance.input.swypecorelib.Candidates;
import com.nuance.input.swypecorelib.RecaptureInfo;
import com.nuance.input.swypecorelib.WordCandidate;
import com.nuance.input.swypecorelib.XT9CoreInput;
import com.nuance.swype.input.AbstractTapDetector;
import com.nuance.swype.input.CandidatesListView;
import com.nuance.swype.input.KeyboardEx;
import com.nuance.swype.input.appspecific.AppSpecificInputConnection;
import com.nuance.swype.stats.UsageManager;
import com.nuance.swype.util.CharacterUtilities;
import com.nuance.swype.util.InputConnectionUtils;
import com.nuance.swype.util.LogManager;
import com.nuance.swype.util.SafeMessageHandler;
import com.nuance.swype.util.WordDecorator;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class RecaptureHandler implements AbstractTapDetector.TapHandler, CandidatesListView.CandidateListener {
    private static final int MAX_RECAPTURE_SIZE = 64;
    private static final int MSG_CURSOR_RESTORE = 1;
    private static final int MSG_RECAPTURE = 2;
    private static final int PREVIOUS_CURSOR_COUNT = 3;
    private static final int RECAPTURE_DELAY = 100;
    private static final int UNKNOWN_INDEX = -1;
    protected static final LogManager.Log log = LogManager.getLog("RecaptureHandler");
    private final XT9CoreInput core;
    private final EditState editState;
    private MsgHandler handler;
    private final IME ime;
    private IMEApplication imeApp;
    private final InputView inputView;
    private final int[] previousCursorPositions;
    private boolean recaptureDisabled;
    private boolean recaptureNextOnUpdateSelection;
    private boolean restoreCursor;
    private final boolean selectOnDoubleTap;
    private boolean suppressNextRecaptureOnUpdateSelection;
    private boolean undoLastAccept;
    private int recaptureStart = -1;
    private int recaptureEnd = -1;
    private EditorContext editorContext = null;
    private List<CharSequence> smileyDisableReacupture = new ArrayList();

    private boolean shouldDisableRecapture() {
        return this.imeApp.getAppSpecificBehavior().shouldDisableRecapture();
    }

    private boolean shouldRecaptureForSingleTapWhenFocusInURLField() {
        return this.ime.getAppSpecificBehavior().shouldRecaptureForSingleTapWhenFocusInURLField();
    }

    private boolean shouldRecaptureForCaseEdit() {
        return this.ime.getAppSpecificBehavior().shouldRecaptureForCaseEdit();
    }

    private boolean shouldRecaptureForSwypeKey() {
        return this.ime.getAppSpecificBehavior().shouldRecaptureForSwypeKey();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class EditorContext {
        public final boolean shouldRecapture;
        public final int startOffset;
        public final String textBefore;
        public final String word;

        public EditorContext(String word, String textBefore, boolean shouldRecapture, int startOffset) {
            this.word = word;
            this.textBefore = textBefore;
            this.shouldRecapture = shouldRecapture;
            this.startOffset = startOffset;
        }
    }

    public RecaptureHandler(IME ime, InputView inputView, boolean selectOnDoubleTap) {
        this.ime = ime;
        this.editState = ime.getEditState();
        this.inputView = inputView;
        this.core = inputView.isSupportRecapture() ? inputView.getXT9CoreInput() : null;
        this.selectOnDoubleTap = selectOnDoubleTap;
        this.previousCursorPositions = new int[3];
        this.imeApp = IMEApplication.from(ime);
        this.handler = new MsgHandler(this);
        for (String symbol : ime.getResources().getStringArray(R.array.smiley_contains_English_character)) {
            this.smileyDisableReacupture.add(symbol);
        }
    }

    public boolean isUsingInputView(InputView inputView) {
        return this.inputView.equals(inputView);
    }

    public void onStartInputView() {
        this.recaptureDisabled = shouldDisableRecapture() || this.imeApp.getCurrentLanguage().isNonSpacedLanguage();
        this.undoLastAccept = false;
    }

    public boolean onKey(int primaryCode, int repeatedCount) {
        int[] selection;
        boolean hasSelection;
        BackgroundColorSpan[] spans;
        if (!this.inputView.isPredictionOn()) {
            return false;
        }
        boolean handled = false;
        AppSpecificInputConnection ic = this.ime.getAppSpecificInputConnection();
        if (ic == null) {
            return false;
        }
        switch (primaryCode) {
            case KeyboardEx.GESTURE_KEYCODE_CASE_EDIT /* -110 */:
                if (!this.imeApp.getCurrentLanguage().isNonSpacedLanguage()) {
                    log.d("cursor GESTURE_KEYCODE_CASE_EDIT onKey");
                    this.handler.removeMessages(2);
                    this.recaptureDisabled = (shouldDisableRecapture() && !shouldRecaptureForCaseEdit()) || this.imeApp.getCurrentLanguage().isNonSpacedLanguage();
                    wordRecapture(false, false);
                    this.inputView.updateSuggestions(Candidates.Source.CAPS_EDIT);
                    this.inputView.setCandidateListener(this);
                }
                return true;
            case 8:
                this.recaptureDisabled = (shouldDisableRecapture() && !shouldRecaptureForSingleTapWhenFocusInURLField()) || this.imeApp.getCurrentLanguage().isNonSpacedLanguage();
                if (repeatedCount <= 1 && shouldRecaptureEditState(ic, primaryCode)) {
                    this.handler.removeMessages(2);
                    wordRecapture(false, false);
                    boolean isRecaptured = false;
                    boolean hadSelection = ic.hasSelection();
                    if (hadSelection) {
                        ic.clearHighlightedText();
                        this.inputView.clearSelection();
                    } else {
                        if (this.inputView.getCurrentWordCandidatesSource() == Candidates.Source.RECAPTURE_BY_TEXT_SELECTION && "com.facebook.katana".equals(this.ime.getInputFieldInfo().getPackageName())) {
                            CharSequence textBeforeCursor = ic.getTarget().getTextBeforeCursor(255, 1);
                            if ((textBeforeCursor instanceof Spanned) && (spans = (BackgroundColorSpan[]) ((Spanned) textBeforeCursor).getSpans(0, textBeforeCursor.length(), BackgroundColorSpan.class)) != null && spans.length > 0 && spans[0].getBackgroundColor() == -2564118) {
                                this.ime.sendBackspace(0);
                                this.handler.removeMessages(2);
                                this.handler.sendMessageDelayed(this.handler.obtainMessage(2, 0, 0), 100L);
                                return true;
                            }
                        }
                        isRecaptured = editStateRecapture(ic, true);
                    }
                    boolean isSelectedTextAccepted = hadSelection && !ic.hasSelection();
                    if (!isRecaptured && !isSelectedTextAccepted) {
                        return false;
                    }
                    return hadSelection;
                }
                if (repeatedCount > 1) {
                    log.d("KeyboardEx.KEYCODE_DELETE...repeatedCount: ", Integer.valueOf(repeatedCount));
                    this.recaptureDisabled = true;
                    return false;
                }
                if (!isCursorWithinWord(ic) || this.ime.isEditorComposingText() || this.inputView.getLastInput() == 6) {
                    return false;
                }
                this.recaptureNextOnUpdateSelection = true;
                this.ime.sendBackspace(0);
                if (!(this.suppressNextRecaptureOnUpdateSelection || this.inputView.getLastInput() == 4 || this.inputView.hasPendingSuggestionsUpdate() || (this.inputView.isHandlingTrace() && !(this.inputView.tracedGesture() && this.inputView.getLastInput() == 2)) || this.inputView.isShowingCandidatesFor(Candidates.Source.UDB_EDIT) || (this.imeApp.getSpeechWrapper() != null && this.imeApp.getSpeechWrapper().isSpeechDictationInProgress()))) {
                    this.handler.removeMessages(2);
                    this.handler.sendMessageDelayed(this.handler.obtainMessage(2, 0, 0), 100L);
                }
                return true;
            case KeyboardEx.KEYCODE_SHIFT /* 4068 */:
            case KeyboardEx.KEYCODE_SHIFT_RIGHT /* 6445 */:
                if (this.inputView.getKeyboardLayer() != KeyboardEx.KeyboardLayerType.KEYBOARD_TEXT || (selection = InputConnectionUtils.getSelection(ic.getExtractedText(new ExtractedTextRequest(), 0))) == null || selection[0] != this.recaptureStart || selection[1] != this.recaptureEnd) {
                    return false;
                }
                this.inputView.updateSuggestions(Candidates.Source.CAPS_EDIT);
                return false;
            case KeyboardEx.KEYCODE_SWYPE /* 43575 */:
                if ((shouldDisableRecapture() && !shouldRecaptureForSwypeKey()) || isExploreByTouchOn()) {
                    return false;
                }
                ic.setByPassCache(true);
                int[] selection2 = ic.getSelectionRangeInEditor();
                ic.setByPassCache(false);
                if (selection2 != null) {
                    hasSelection = selection2[0] != selection2[1];
                } else {
                    hasSelection = !TextUtils.isEmpty(ic.getSelectedTextInEditor(this.ime.getInputFieldInfo()));
                }
                if (!hasSelection) {
                    if (!this.imeApp.getCurrentLanguage().isNonSpacedLanguage()) {
                        EditorContext editorContext = extractContext(true);
                        if (editorContext.shouldRecapture) {
                            this.handler.removeMessages(2);
                            this.handler.obtainMessage(2, 0, 0).sendToTarget();
                            handled = true;
                        } else if (!TextUtils.isEmpty(editorContext.word)) {
                            handled = true;
                        }
                    } else {
                        int[] composing = ic.getComposingRangeInEditor();
                        if (composing != null) {
                            ic.finishComposingText();
                            ic.setSelection(composing[0], composing[1]);
                            if (this.inputView.isHandWritingInputView()) {
                                this.inputView.showNextWordPrediction();
                            }
                            handled = true;
                        }
                    }
                }
                if (handled) {
                    this.inputView.updateShiftKeyState();
                    return handled;
                }
                return handled;
            default:
                return false;
        }
    }

    public void onRelease(int primaryCode, int repeatedCount) {
        this.recaptureDisabled = shouldDisableRecapture() || this.imeApp.getCurrentLanguage().isNonSpacedLanguage();
        if (repeatedCount > 1 && primaryCode == 8) {
            log.d("cursor onRelease");
            this.handler.removeMessages(2);
            this.handler.obtainMessage(2, 0, 0).sendToTarget();
        }
    }

    public boolean onCharKey(char c) {
        AppSpecificInputConnection ic = this.ime.getAppSpecificInputConnection();
        if (ic == null || this.core == null) {
            return false;
        }
        CharacterUtilities charUtils = CharacterUtilities.from(this.ime);
        if ((Character.isLetter(c) || charUtils.isDiacriticMark(c)) && shouldRecaptureEditState(ic, c)) {
            if (ic.hasSelection()) {
                ic.clearHighlightedText();
                this.inputView.clearSelection();
            }
            editStateRecapture(ic, false);
            return true;
        }
        if (isCursorWithinWord(ic)) {
            this.recaptureNextOnUpdateSelection = true;
            return false;
        }
        if (this.inputView.getCurrentWordCandidatesSource() != Candidates.Source.RECAPTURE_BY_TEXT_SELECTION) {
            return false;
        }
        this.inputView.onCancelNonEditStateRecaptureViaCharKey(c);
        this.inputView.clearSuggestions();
        this.inputView.setLastInput(4);
        this.core.clearAllKeys();
        return true;
    }

    public void onText(CharSequence text) {
        this.suppressNextRecaptureOnUpdateSelection = true;
    }

    @Override // com.nuance.swype.input.CandidatesListView.CandidateListener
    public boolean onSelectCandidate(WordCandidate candidate, Candidates candidates) {
        ExtractedText eText;
        int[] selection;
        boolean absorbSelection = false;
        if (candidates.source() == Candidates.Source.RECAPTURE_BY_TEXT_SELECTION || candidates.source() == Candidates.Source.CAPS_EDIT) {
            InputConnection ic = this.ime.getCurrentInputConnection();
            if (this.restoreCursor) {
                this.restoreCursor = false;
                if (ic != null && (selection = InputConnectionUtils.getSelection((eText = ic.getExtractedText(new ExtractedTextRequest(), 0)))) != null && selection[0] + eText.startOffset == this.recaptureStart && selection[1] + eText.startOffset == this.recaptureEnd) {
                    for (int i = 0; i < 3; i++) {
                        int cursorPosition = this.previousCursorPositions[i];
                        if (cursorPosition <= this.recaptureStart || this.recaptureEnd <= cursorPosition) {
                            if (cursorPosition > this.recaptureStart) {
                                int oldLength = this.recaptureEnd - this.recaptureStart;
                                cursorPosition += candidate.length() - oldLength;
                            }
                            this.handler.obtainMessage(1, cursorPosition, 0).sendToTarget();
                        }
                    }
                }
            }
            if (this.editorContext != null && this.editorContext.word != null && this.imeApp.getAppSpecificBehavior().noReplacingReselectedWordWhenMatching() && this.editorContext.word.equals(candidate.toString())) {
                absorbSelection = true;
                this.inputView.clearSuggestions();
                if (ic != null && this.recaptureEnd >= 0) {
                    ic.setSelection(this.recaptureEnd, this.recaptureEnd);
                }
            } else if (ic != null) {
                deleteRecapturedText(ic);
            }
        }
        return absorbSelection;
    }

    public void onUpdateSelection(int oldSelStart, int oldSelEnd, int newSelStart, int newSelEnd, int candidatesStart, int candidatesEnd) {
        if (newSelStart == newSelEnd) {
            for (int i = 2; i > 0; i--) {
                this.previousCursorPositions[i] = this.previousCursorPositions[i - 1];
            }
            this.previousCursorPositions[0] = newSelStart;
        }
        boolean cursorValid = (oldSelStart != newSelStart && oldSelEnd != newSelEnd && newSelStart == newSelEnd && candidatesStart == candidatesEnd) || (newSelStart == newSelEnd && newSelStart == Math.min(oldSelStart, oldSelEnd) && oldSelStart != oldSelEnd);
        boolean refreshRecapture = (this.recaptureStart == -1 || this.recaptureEnd == -1 || (newSelStart >= this.recaptureStart && this.recaptureEnd >= newSelEnd && !this.recaptureNextOnUpdateSelection)) ? false : true;
        boolean shouldClearAndRecapture = true;
        if ((this.inputView.isUsingVietnameseTelexInputMode() || this.inputView.isUsingVietnameseNationalInputMode()) && this.inputView.isComposingText()) {
            shouldClearAndRecapture = false;
        }
        if (newSelStart == newSelEnd && newSelStart == Math.min(oldSelStart, oldSelEnd) && oldSelStart != oldSelEnd && shouldClearAndRecapture) {
            this.inputView.clearSuggestions();
        }
        if (!(this.suppressNextRecaptureOnUpdateSelection || this.inputView.getLastInput() == 4 || this.inputView.getLastInput() == 6 || this.inputView.hasPendingSuggestionsUpdate() || (this.inputView.isHandlingTrace() && !(this.inputView.tracedGesture() && this.inputView.getLastInput() == 2)) || this.inputView.isShowingCandidatesFor(Candidates.Source.UDB_EDIT) || ((this.imeApp.getSpeechWrapper() != null && this.imeApp.getSpeechWrapper().isSpeechDictationInProgress()) || !shouldClearAndRecapture)) && cursorValid && (refreshRecapture || this.inputView.isEmptyCandidateList() || this.inputView.isShowingCandidatesFor(Candidates.Source.NEXT_WORD_PREDICTION))) {
            log.d("onUpdateSelection(): wordRecapture");
            this.handler.removeMessages(2);
            this.handler.sendMessageDelayed(this.handler.obtainMessage(2, 0, 0), 100L);
        }
        this.recaptureNextOnUpdateSelection = false;
        this.suppressNextRecaptureOnUpdateSelection = false;
    }

    @Override // com.nuance.swype.input.CandidatesListView.CandidateListener
    public boolean onPressHoldCandidate(WordCandidate candidate, Candidates candidates) {
        return false;
    }

    @Override // com.nuance.swype.input.CandidatesListView.CandidateListener
    public boolean onPressReleaseCandidate(WordCandidate candidate, Candidates candidates) {
        return false;
    }

    @Override // com.nuance.swype.input.CandidatesListView.CandidateListener
    public void onPressMoveCandidate(float xPos, float yPos, float xOffset) {
    }

    @Override // com.nuance.swype.input.CandidatesListView.CandidateListener
    public void onCandidatesUpdated(Candidates candidates) {
    }

    @Override // com.nuance.swype.input.AbstractTapDetector.TapHandler
    public boolean onSingleTap(boolean shouldResyncCache, boolean orientationChanged) {
        boolean speechDictationInProgress = this.imeApp.getSpeechWrapper() != null && this.imeApp.getSpeechWrapper().isSpeechDictationInProgress();
        boolean isCurrentLanguageCJ = this.imeApp.getCurrentLanguage() != null && (this.imeApp.getCurrentLanguage().isChineseLanguage() || this.imeApp.getCurrentLanguage().isJapaneseLanguage());
        boolean hasActiveAlphaWord = (this.imeApp.getCurrentLanguage() == null || this.imeApp.getCurrentLanguage().isCJK() || IME.getLastSavedActiveWord() == null || IME.getLastSavedActiveWord().length() <= 0) ? false : true;
        if (speechDictationInProgress || this.ime.isEditorComposingText() || ((this.inputView.isTracing() && !this.inputView.tracedGesture()) || isCurrentLanguageCJ || hasActiveAlphaWord)) {
            return true;
        }
        this.recaptureDisabled = (shouldDisableRecapture() && !shouldRecaptureForSingleTapWhenFocusInURLField()) || this.imeApp.getCurrentLanguage().isNonSpacedLanguage();
        this.handler.removeMessages(2);
        this.handler.sendMessageDelayed(this.handler.obtainMessage(2, shouldResyncCache ? 1 : 0, orientationChanged ? 1 : 0), 100L);
        return false;
    }

    @Override // com.nuance.swype.input.AbstractTapDetector.TapHandler
    public boolean onDoubleTap() {
        InputConnection ic = this.ime.getCurrentInputConnection();
        if (ic != null) {
            int[] selection = InputConnectionUtils.getSelection(ic.getExtractedText(new ExtractedTextRequest(), 0));
            if (selection != null && this.recaptureStart <= selection[0] && selection[1] <= this.recaptureEnd) {
                if (this.selectOnDoubleTap) {
                    ic.setSelection(this.recaptureStart, this.recaptureEnd);
                    this.inputView.updateShiftKeyState();
                }
                this.restoreCursor = true;
            } else {
                onSingleTap(false, false);
            }
        }
        return false;
    }

    public boolean reconstructByTap() {
        this.handler.removeMessages(2);
        if (reconstruct(false) == RecaptureInfo.EMPTY_RECAPTURE_INFO || this.recaptureEnd <= 0 || this.recaptureStart < 0) {
            return false;
        }
        this.inputView.updateSuggestions(Candidates.Source.TAP, false);
        this.editState.recapture();
        return true;
    }

    private boolean editStateRecapture(AppSpecificInputConnection ic, boolean checkHistory) {
        RecaptureInfo recaptureInfo = reconstruct(checkHistory);
        if (recaptureInfo != RecaptureInfo.EMPTY_RECAPTURE_INFO && this.recaptureEnd > 0 && this.recaptureStart >= 0) {
            if (!ic.setComposingRegion(this.recaptureStart, this.recaptureEnd)) {
                deleteRecapturedText(ic);
                ic.setComposingText(recaptureInfo.recapturedWord, 1);
            }
            this.inputView.updateSuggestions(Candidates.Source.RECAPTURE, false);
            this.editState.recapture();
        }
        return recaptureInfo != RecaptureInfo.EMPTY_RECAPTURE_INFO;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean wordRecapture(boolean shouldResyncCache, boolean orientationChanged) {
        if (this.recaptureDisabled) {
            if (this.imeApp.getCurrentLanguage().isNonSpacedLanguage()) {
                AppSpecificInputConnection ic = this.ime.getAppSpecificInputConnection();
                if (shouldResyncCache && ic != null) {
                    ic.reSyncFromEditor();
                }
                this.inputView.flushCurrentActiveWord();
                if (!this.inputView.isPredictionOn()) {
                    return false;
                }
                if (!this.ime.isChangingOrientation() && !orientationChanged) {
                    this.inputView.updateShiftKeyState();
                }
                this.inputView.showNextWordPrediction();
            }
            return false;
        }
        this.restoreCursor = false;
        AppSpecificInputConnection ic2 = this.ime.getAppSpecificInputConnection();
        if (shouldResyncCache && ic2 != null) {
            ic2.reSyncFromEditor();
        }
        log.d("cursor wordRecapture...shouldResyncCache: ", Boolean.valueOf(shouldResyncCache), "...orientationChanged: ", Boolean.valueOf(orientationChanged));
        this.inputView.flushCurrentActiveWord();
        if (!this.inputView.isPredictionOn()) {
            return false;
        }
        boolean recaptured = false;
        List<CharSequence> lstCandidates = this.inputView.getSpeechAlternateCandidates();
        if (lstCandidates != null && lstCandidates.size() > 0) {
            this.editState.reselectToReplace();
            this.inputView.setSuggestions(this, lstCandidates, 0, Candidates.Source.SPEECH_ALTERNATES);
        } else if (!this.inputView.isHighlightedTextSpeechDictated()) {
            RecaptureInfo recaptureInfo = tryToRecaptureWord();
            if (recaptureInfo != RecaptureInfo.EMPTY_RECAPTURE_INFO) {
                this.editState.reselectToReplace();
                Candidates candidates = this.core.getRecaptureCandidates(Candidates.Source.RECAPTURE_BY_TEXT_SELECTION, recaptureInfo);
                if (candidates != null && candidates.count() > 0) {
                    String defaultWord = candidates.getDefaultCandidateString().toString();
                    String exactWord = candidates.getExactCandidateString().toString();
                    String recapturedWord = recaptureInfo.recapturedWord;
                    if (ic2 != null && defaultWord.equals(exactWord) && !recapturedWord.equals(exactWord)) {
                        this.suppressNextRecaptureOnUpdateSelection = true;
                        ic2.beginBatchEdit();
                        this.inputView.setInlineWord(exactWord);
                        if (ic2.setComposingRegionBeforeCursor(recapturedWord, 1, true) != -1) {
                            WordDecorator wd = this.inputView.getWordDecorator();
                            if (wd != null) {
                                ic2.commitText(wd.decorateUnrecognizedWord(candidates.getExactCandidateString()), 1);
                            } else {
                                ic2.commitText(candidates.getExactCandidateString(), 1);
                            }
                        }
                        this.inputView.setInlineWord("");
                        ic2.endBatchEdit();
                    }
                    this.inputView.setSuggestions(this, candidates, this.inputView.getWordListFormat(candidates));
                    this.inputView.updateShiftKeyState();
                    recaptured = true;
                }
            } else {
                if (!this.ime.isChangingOrientation() && !orientationChanged) {
                    this.inputView.updateShiftKeyState();
                }
                this.inputView.showNextWordPrediction();
            }
        }
        this.inputView.wordReCaptureComplete();
        this.recaptureDisabled = shouldDisableRecapture() || this.imeApp.getCurrentLanguage().isNonSpacedLanguage();
        return recaptured;
    }

    private RecaptureInfo tryToRecaptureWord() {
        UsageManager usageMgr;
        RecaptureInfo recaptureInfo = RecaptureInfo.EMPTY_RECAPTURE_INFO;
        if (this.core != null) {
            EditorContext editorContext = extractContext(false);
            if (editorContext.shouldRecapture) {
                this.core.setContext(editorContext.textBefore.toCharArray());
                recaptureInfo = this.inputView.getUndoAcceptHandler().undoAccept(editorContext.word);
                if (recaptureInfo == RecaptureInfo.EMPTY_RECAPTURE_INFO) {
                    char[] wordArray = editorContext.word.toCharArray();
                    recaptureInfo = this.core.recaptureWord(wordArray, true);
                }
                if (recaptureInfo != RecaptureInfo.EMPTY_RECAPTURE_INFO && (usageMgr = UsageManager.from(this.ime)) != null) {
                    usageMgr.getKeyboardUsageScribe().recordRecapture(recaptureInfo.recapturedWord, this.recaptureStart);
                }
            }
        }
        return recaptureInfo;
    }

    private RecaptureInfo reconstruct(boolean checkHistory) {
        boolean recaptured;
        RecaptureInfo recaptureInfo = RecaptureInfo.EMPTY_RECAPTURE_INFO;
        if (this.core != null) {
            EditorContext editorContext = extractContext(false);
            if (editorContext.shouldRecapture) {
                this.core.clearAllKeys();
                String reconstructContext = editorContext.word;
                boolean recaptured2 = false;
                AlphaInputView alphaInputView = null;
                if (this.inputView instanceof AlphaInputView) {
                    alphaInputView = (AlphaInputView) this.inputView;
                    alphaInputView.setReconstructWord(reconstructContext);
                    if (alphaInputView.isUsingVietnameseTelexInputMode()) {
                        alphaInputView.reconstructWord();
                        recaptureInfo = RecaptureInfo.makeRecaptureInfo(reconstructContext.toCharArray());
                        recaptured2 = true;
                    }
                    alphaInputView.setInlineWord(reconstructContext);
                }
                if (!recaptured2) {
                    if (checkHistory) {
                        recaptureInfo = this.core.recaptureWord(reconstructContext.toCharArray(), true);
                        recaptured = recaptureInfo != RecaptureInfo.EMPTY_RECAPTURE_INFO;
                    } else {
                        char[] word = reconstructContext.toCharArray();
                        recaptured = this.core.reconstructWord(word);
                        if (recaptured) {
                            recaptureInfo = RecaptureInfo.makeRecaptureInfo(word);
                        }
                    }
                    if (!recaptured && alphaInputView != null) {
                        alphaInputView.setReconstructWord((String) null);
                        alphaInputView.setInlineWord(null);
                    }
                }
            }
        }
        return recaptureInfo;
    }

    private EditorContext extractContext(boolean isSwypeKeyPress) {
        ExtractedText extractedText;
        int[] selection;
        this.recaptureStart = -1;
        this.recaptureEnd = -1;
        this.editorContext = null;
        String contextWord = null;
        String contextBefore = null;
        int startOffset = 0;
        boolean shouldRecapture = false;
        AppSpecificInputConnection ic = this.ime.getAppSpecificInputConnection();
        if (ic != null && (selection = InputConnectionUtils.getSelection((extractedText = ic.getExtractedText(new ExtractedTextRequest(), 0)))) != null) {
            CharacterUtilities charUtils = CharacterUtilities.from(this.ime);
            String text = extractedText.text.toString();
            int extractedLength = text.length();
            if (extractedLength > 0) {
                int selectionStart = selection[0];
                int contextStart = Math.min(selectionStart, extractedLength - 1);
                if (contextStart < 0) {
                    EditorContext editorContext = new EditorContext(null, null, false, 0);
                    this.editorContext = editorContext;
                    return editorContext;
                }
                char ch = text.charAt(contextStart);
                if (!charUtils.isWordBoundary(ch)) {
                    while (contextStart > 0) {
                        char ch2 = text.charAt(contextStart - 1);
                        if (charUtils.isWordBoundary(ch2)) {
                            break;
                        }
                        contextStart--;
                    }
                } else {
                    boolean isWordFoundScanningLeft = false;
                    contextStart = Math.min(selectionStart, extractedLength);
                    while (contextStart > 0) {
                        char ch3 = text.charAt(contextStart - 1);
                        if (charUtils.isWordBoundary(ch3)) {
                            if (!isSwypeKeyPress || isWordFoundScanningLeft) {
                                break;
                            }
                        } else {
                            isWordFoundScanningLeft = true;
                        }
                        contextStart--;
                    }
                }
                int contextEnd = contextStart;
                int contextCount = 0;
                while (contextEnd < extractedLength) {
                    contextCount++;
                    if (contextCount > 64) {
                        break;
                    }
                    char ch4 = text.charAt(contextEnd);
                    if (charUtils.isWordBoundary(ch4)) {
                        break;
                    }
                    contextEnd++;
                }
                if (contextStart != contextEnd && contextCount <= 64) {
                    startOffset = extractedText.startOffset;
                    int newSelectionStart = contextStart + startOffset;
                    int newSelectionEnd = contextEnd + startOffset;
                    if (isSwypeKeyPress && (newSelectionStart != selection[0] || newSelectionEnd != selection[1])) {
                        ic.setSelection(newSelectionStart, newSelectionStart);
                        ic.setSelection(newSelectionStart, newSelectionEnd);
                        ic.setSelection(newSelectionStart, newSelectionEnd);
                    }
                    contextWord = text.substring(contextStart, contextEnd);
                    contextBefore = text.substring(0, contextStart);
                    boolean foundLetter = false;
                    boolean foundSurrogatePart = false;
                    int wordLength = contextWord.length();
                    for (int i = 0; i < wordLength; i++) {
                        char ch5 = contextWord.charAt(i);
                        if (Character.isLetter(ch5)) {
                            foundLetter = true;
                        } else if (Character.isHighSurrogate(ch5) || Character.isLowSurrogate(ch5)) {
                            foundSurrogatePart = true;
                        }
                        if (foundLetter && foundSurrogatePart) {
                            break;
                        }
                    }
                    shouldRecapture = (!TextUtils.isEmpty(contextWord) && foundLetter && !foundSurrogatePart) && !CharacterUtilities.endsWithSurrogatePair(contextWord);
                    if (this.imeApp.getAppSpecificBehavior().shouldCheckSmileyWhenDeleting() && selection[0] >= 2) {
                        CharacterUtilities utility = CharacterUtilities.from(this.ime);
                        int start = selection[0] > utility.maxSmileyLength ? selection[0] - utility.maxSmileyLength : 0;
                        String lastEmiley = text.subSequence(start, selection[0]).toString();
                        if (utility.isSmiley(lastEmiley)) {
                            shouldRecapture = false;
                        }
                    }
                    if (shouldRecapture) {
                        this.recaptureStart = extractedText.startOffset + contextStart;
                        this.recaptureEnd = extractedText.startOffset + contextEnd;
                    }
                }
            }
        }
        EditorContext editorContext2 = new EditorContext(contextWord, contextBefore, shouldRecapture, startOffset);
        this.editorContext = editorContext2;
        return editorContext2;
    }

    private void deleteRecapturedText(InputConnection ic) {
        if (this.recaptureStart != this.recaptureEnd && this.recaptureStart < this.recaptureEnd && this.recaptureEnd > 0) {
            if (this.imeApp.getAppSpecificBehavior().selectTextToReplace()) {
                InputConnectionUtils.setSelection(ic, this.recaptureStart, this.recaptureEnd);
                if (InputConnectionCompat.getSelectedText(ic, 0) == null) {
                    InputConnectionCompat.setComposingRegion(ic, this.recaptureStart, this.recaptureEnd);
                }
            } else {
                ExtractedText eText = ic.getExtractedText(new ExtractedTextRequest(), 0);
                if (eText != null) {
                    ic.beginBatchEdit();
                    ic.commitText("", 1);
                    ic.deleteSurroundingText((eText.startOffset + eText.selectionStart) - this.recaptureStart, this.recaptureEnd - (eText.startOffset + eText.selectionEnd));
                    ic.endBatchEdit();
                }
            }
            this.recaptureStart = -1;
            this.recaptureEnd = -1;
            this.editorContext = null;
        }
    }

    private boolean shouldRecaptureEditState(AppSpecificInputConnection ic, int keyCode) {
        CharSequence cSeqBefore;
        int lengthBefore;
        if (this.recaptureDisabled || !this.inputView.isPredictionOn() || this.inputView.hasPendingSuggestionsUpdate()) {
            return false;
        }
        Candidates.Source candidatesSource = this.inputView.getCurrentWordCandidatesSource();
        if (candidatesSource != Candidates.Source.INVALID && candidatesSource != Candidates.Source.NEXT_WORD_PREDICTION && candidatesSource != Candidates.Source.RECAPTURE_BY_TEXT_SELECTION && candidatesSource != Candidates.Source.UDB_EDIT && candidatesSource != Candidates.Source.CAPS_EDIT) {
            return false;
        }
        if ((keyCode != 8 && (this.inputView.getLastInput() == 2 || this.inputView.getLastInput() == 4)) || this.inputView.getLastInput() == 6 || (cSeqBefore = ic.getTextBeforeCursor(50, 0)) == null || isStringEndOfEmoticon(cSeqBefore) || isCursorWithinWord(ic) || (lengthBefore = cSeqBefore.length()) == 0) {
            return false;
        }
        if (keyCode == 8 && (lengthBefore == 1 || CharacterUtilities.isWhiteSpace(cSeqBefore.charAt(lengthBefore - 2)))) {
            return false;
        }
        char lastChar = cSeqBefore.charAt(lengthBefore - 1);
        CharacterUtilities charUtils = CharacterUtilities.from(this.ime);
        if (CharacterUtilities.isWhiteSpace(lastChar)) {
            return false;
        }
        if (charUtils.isPunctuationOrSymbol(lastChar) && lastChar != '\'') {
            return false;
        }
        for (int i = lengthBefore - 1; i >= 0; i--) {
            char c = cSeqBefore.charAt(i);
            if (c == ' ') {
                break;
            }
            if (CharacterUtilities.isValidCJChar(c)) {
                return false;
            }
        }
        return true;
    }

    private boolean isStringEndOfEmoticon(CharSequence str) {
        for (CharSequence strSmiley : this.smileyDisableReacupture) {
            if (str.toString().endsWith(strSmiley.toString())) {
                return true;
            }
        }
        return false;
    }

    private boolean isCursorWithinWord(AppSpecificInputConnection ic) {
        CharacterUtilities charUtils = CharacterUtilities.from(this.ime);
        return ic.isCursorWithinWord(charUtils);
    }

    public void removePendingRecaptureMessage() {
        this.handler.removeMessages(2);
    }

    public boolean hasPendingRecaptureMessage() {
        return this.handler.hasMessages(2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class MsgHandler extends SafeMessageHandler<RecaptureHandler> {
        public MsgHandler(RecaptureHandler parent) {
            super(parent);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.nuance.swype.util.SafeMessageHandler
        public void handleMessage(Message msg, RecaptureHandler parent) {
            switch (msg.what) {
                case 1:
                    InputConnection ic = parent.ime.getCurrentInputConnection();
                    if (ic != null) {
                        ic.setSelection(msg.arg1, msg.arg1);
                        if (parent.inputView.hasPendingSuggestionsUpdate()) {
                            parent.inputView.clearPendingSuggestionsUpdate();
                        }
                        parent.wordRecapture(false, false);
                        return;
                    }
                    return;
                case 2:
                    RecaptureHandler.log.d("cursor MSG_RECAPTURE handleMessage");
                    parent.wordRecapture(msg.arg1 == 1, msg.arg2 == 1);
                    return;
                default:
                    return;
            }
        }
    }

    protected boolean isExploreByTouchOn() {
        if (this.ime == null) {
            return false;
        }
        boolean exploreByTouch = this.ime.isAccessibilitySupportEnabled();
        return exploreByTouch;
    }

    public void clearMessages() {
        this.handler.removeCallbacksAndMessages(null);
    }
}
