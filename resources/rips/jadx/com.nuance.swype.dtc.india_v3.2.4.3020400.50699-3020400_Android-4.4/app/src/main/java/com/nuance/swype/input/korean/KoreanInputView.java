package com.nuance.swype.input.korean;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.InputConnection;
import com.nuance.android.util.WeakReferenceHandler;
import com.nuance.dlm.ACCoreInputDLM;
import com.nuance.input.swypecorelib.Candidates;
import com.nuance.input.swypecorelib.KeyType;
import com.nuance.input.swypecorelib.Shift;
import com.nuance.input.swypecorelib.WordCandidate;
import com.nuance.input.swypecorelib.XT9CoreInput;
import com.nuance.input.swypecorelib.XT9CoreKoreanInput;
import com.nuance.input.swypecorelib.XT9Status;
import com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.xmlResults.XMLResultsHandler;
import com.nuance.swype.connect.Connect;
import com.nuance.swype.input.AppPreferences;
import com.nuance.swype.input.BilingualLanguage;
import com.nuance.swype.input.CandidatesListView;
import com.nuance.swype.input.DatabaseConfig;
import com.nuance.swype.input.IME;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.InputFieldInfo;
import com.nuance.swype.input.InputMethodToast;
import com.nuance.swype.input.InputMethods;
import com.nuance.swype.input.InputView;
import com.nuance.swype.input.KeyboardEx;
import com.nuance.swype.input.KeyboardSwitcher;
import com.nuance.swype.input.KeyboardViewEx;
import com.nuance.swype.input.QuickToast;
import com.nuance.swype.input.R;
import com.nuance.swype.input.SpeechWrapper;
import com.nuance.swype.input.UserPreferences;
import com.nuance.swype.input.XT9Keyboard;
import com.nuance.swype.input.appspecific.AppSpecificInputConnection;
import com.nuance.swype.input.emoji.EmojiLoader;
import com.nuance.swype.input.hardkey.HardKeyboardManager;
import com.nuance.swype.input.keyboard.InputContextRequestDispatcher;
import com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher;
import com.nuance.swype.plugin.TypedArrayWrapper;
import com.nuance.swype.stats.StatisticsEnabledEditState;
import com.nuance.swype.util.CharacterUtilities;
import com.nuance.swype.util.InputConnectionUtils;

/* loaded from: classes.dex */
public class KoreanInputView extends InputView implements CandidatesListView.CandidateListener {
    private static final int CHUNJIIN_KEYCODE_NUM_ONE = 12643;
    private static final int CHUNJIIN_KEYCODE_NUM_THREE = 12641;
    private static final int CHUNJIIN_KEYCODE_NUM_TWO = 12685;
    private static final int LONG_PRESS_THREASHOLD = 3;
    private static final int NARATGUL_KEYCODE_ADD_STROKE = 4442;
    private static final int NARATGUL_KEYCODE_DOUBLE_CONSONANTS = 4443;
    private static final String TERMINAL_PUNCT_PREDICTION = ",.!?";
    private final StringBuilder activeHangul;
    private InputContextRequestHandler inputContextRequestHandler;
    private final Handler.Callback inputViewHandlerCallback;
    private boolean isTimeoutBySpaceKey;
    private BackgroundColorSpan mBKMultiptappingCharSpan;
    private CharacterUtilities mCharUtils;
    private ForegroundColorSpan mFGMultiptappingCharSpan;
    private final SpannableStringBuilder mInlineWord;
    private final Handler mInputViewHandler;
    private XT9CoreKoreanInput mKoreanInput;
    private final UnderlineSpan mWordComposeSpan;
    TouchKeyActionHandler touchKeyActionHandler;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.InputView
    public void removeAllMessages() {
        for (int msg = 1; msg <= 19; msg++) {
            this.mInputViewHandler.removeMessages(msg);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showStartOfWordCandidateList() {
        String lastWord = IME.getLastSavedActiveWordAndSet();
        if (lastWord != null && lastWord.length() > 0) {
            char[] chArray = new char[1];
            for (int iCharPos = 0; iCharPos < lastWord.length(); iCharPos++) {
                chArray[0] = lastWord.charAt(iCharPos);
                if (CharacterUtilities.isUpperCaseLetter(chArray[0])) {
                    this.mKoreanInput.addExplicit(chArray, 1, Shift.ShiftState.ON);
                } else {
                    this.mKoreanInput.addExplicit(chArray, 1, Shift.ShiftState.OFF);
                }
            }
            this.mEditState.composeWordCandidate();
            InputConnection ic = getCurrentInputConnection();
            if (this.mKoreanInput.getKeyCount() > 0) {
                this.mKoreanInput.getInlineHangul(this.activeHangul);
                displayInlineHangul(ic, this.activeHangul, true);
            }
            updateSuggestions(Candidates.Source.TAP);
            return;
        }
        if (IME.getLastShownCandidatesSource() == Candidates.Source.NEXT_WORD_PREDICTION) {
            updateWordContext();
            updateSuggestions(Candidates.Source.NEXT_WORD_PREDICTION);
        } else if (isEmptyCandidateList()) {
            showNextWordPrediction();
        }
    }

    private void postDelayShowingStartOfWordCandidate() {
        this.mInputViewHandler.sendEmptyMessageDelayed(8, 10L);
    }

    private void removeToastMsg(int msg) {
        QuickToast.hide();
        this.mInputViewHandler.removeMessages(msg);
    }

    private void postToastMsg(int msg) {
        removeToastMsg(msg);
        this.mInputViewHandler.sendEmptyMessageDelayed(msg, 250L);
    }

    @Override // com.nuance.swype.input.InputView
    public void create(IME ime, XT9CoreInput xt9coreinput, SpeechWrapper speechWrapper) {
        super.create(ime, xt9coreinput, speechWrapper);
        this.mKoreanInput = (XT9CoreKoreanInput) xt9coreinput;
        this.mCharUtils = IMEApplication.from(getContext()).getCharacterUtilities();
        this.mKeyboardSwitcher = new KeyboardSwitcher(this.mIme, this.mKoreanInput);
        this.mKeyboardSwitcher.setInputView(this);
        setOnKeyboardActionListener(this.mIme);
        readStyles(getContext());
        ACCoreInputDLM.initializeACKoreanInput(Connect.from(getContext()));
    }

    @Override // com.nuance.swype.input.InputView
    public void destroy() {
        super.destroy();
        ACCoreInputDLM.destroyACKoreanInput();
        setOnKeyboardActionListener((IME) null);
        this.mKeyboardSwitcher = null;
        dimissRemoveUdbWordDialog();
    }

    @Override // com.nuance.swype.input.InputView
    public XT9CoreInput getXT9CoreInput() {
        return this.mKoreanInput;
    }

    @Override // com.nuance.swype.input.InputView
    public View getWordCandidateListContainer() {
        return this.wordListViewContainer;
    }

    @Override // com.nuance.swype.input.InputView
    public void clearCurrentActiveWord() {
        clearCurrentInline(getCurrentInputConnection());
        clearSuggestions();
    }

    @Override // com.nuance.swype.input.InputView
    @SuppressLint({"PrivateResource"})
    public void startInput(InputFieldInfo inputFieldInfo, boolean restarting) {
        InputConnection ic = getCurrentInputConnection();
        if (this.mKoreanInput != null && ic != null) {
            if (this.candidatesListView != null) {
                this.candidatesListView.updateCandidatesSize();
            }
            if (this.emojiCandidatesListView != null) {
                this.emojiCandidatesListView.updateCandidatesSize();
            }
            if (!restarting || this.mEditState.current() != 2) {
                this.keyboardTouchEventDispatcher.resisterTouchKeyHandler(this.touchKeyActionHandler);
                this.keyboardTouchEventDispatcher.registerFlingGestureHandler(this.flingGestureListener);
                this.keyboardTouchEventDispatcher.wrapTouchEvent(this);
                this.mKoreanInput.setInputContextRequestListener(InputContextRequestDispatcher.getDispatcherInstance().setHandler(this.inputContextRequestHandler));
                this.mIme.getEditState().startSession();
                dismissPopupKeyboard();
                flushCurrentActiveWord();
                super.startInput(inputFieldInfo, restarting);
                InputMethods.InputMode inputMode = this.mCurrentInputLanguage.getCurrentInputMode();
                this.mInputViewHandler.removeMessages(15);
                this.mInputViewHandler.sendEmptyMessageDelayed(15, 5L);
                int bilingualId = getResources().getInteger(R.integer.bilingual_keyboard_id);
                boolean bilingualEnabled = this.mCurrentInputLanguage instanceof BilingualLanguage;
                if (bilingualEnabled && this.mKeyboardLayoutId == 2304) {
                    inputMode.findLayout(bilingualId).saveAsCurrent();
                } else if (!bilingualEnabled && this.mKeyboardLayoutId == bilingualId) {
                    inputMode.findLayout(InputMethods.QWERTY_KEYBOARD_LAYOUT).saveAsCurrent();
                }
                KeyboardEx.KeyboardLayerType currentLayer = this.mKeyboardSwitcher.currentKeyboardLayer();
                if (restarting && currentLayer != KeyboardEx.KeyboardLayerType.KEYBOARD_INVALID) {
                    this.mKeyboardSwitcher.createKeyboardForTextInput(currentLayer, inputFieldInfo, inputMode);
                } else {
                    this.mKeyboardSwitcher.createKeyboardForTextInput(inputFieldInfo, inputMode);
                }
                this.mCompletions.clear();
                updateMultitapDeadkey(false);
                if (!inputFieldInfo.isPhoneNumberField() && !inputFieldInfo.isPasswordField()) {
                    postDelayShowingStartOfWordCandidate();
                }
                if (inputFieldInfo.isPasswordField()) {
                    triggerPasswordTip();
                }
                postDelayResumeSpeech();
                showUserThemeWclMessage(this.mInputViewHandler);
                showTrialExpireWclMessage("alphaInput");
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.KeyboardViewEx
    @SuppressLint({"PrivateResource"})
    public boolean useShiftAsAlt(KeyboardEx.Key key) {
        int bilingualId = getResources().getInteger(R.integer.bilingual_keyboard_id);
        return !((this.mCurrentInputLanguage instanceof BilingualLanguage) && (this.mKeyboardLayoutId == bilingualId || this.mKeyboardLayoutId == 2304)) && super.useShiftAsAlt(key);
    }

    private void postDelayResumeSpeech() {
        if (this.mInputViewHandler.hasMessages(11)) {
            this.mInputViewHandler.removeMessages(11);
        }
        this.mInputViewHandler.sendEmptyMessageDelayed(11, 1L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startInputSession() {
        int i = 0;
        if (this.mKoreanInput != null && this.mCurrentInputLanguage != null) {
            this.mKoreanInput.startSession();
            this.mKoreanInput.setExplicitLearning(false, false);
            setShiftState(Shift.ShiftState.OFF);
            this.mKoreanInput.setShiftState(Shift.ShiftState.OFF);
            this.mKoreanInput.setWordQuarantineLevel(1, 1, 1);
            this.mKoreanInput.setLDBEmoji(UserPreferences.from(this.mIme).isEmojiSuggestionsEnabled());
            this.mNextWordPredictionOn = this.mTextInputPredictionOn && !this.mInURLEmail && UserPreferences.from(getContext()).isNextWordPredictionEnabled();
            this.mKoreanInput.setAttribute(101, this.mWordCompletionPoint);
            this.mKoreanInput.setAttribute(99, this.autoCorrectionEnabled);
            XT9CoreKoreanInput xT9CoreKoreanInput = this.mKoreanInput;
            if (this.mCurrentInputLanguage != null && this.mCurrentInputLanguage.getCurrentInputMode().isTraceAutoAcceptEnabled()) {
                i = 1;
            }
            xT9CoreKoreanInput.setAttribute(104, i);
            this.mKoreanInput.enableConsonantInput(UserPreferences.from(getContext()).getEnableKoreanConsonantInput());
            setLanguage(this.mKoreanInput);
            if (this.autoCorrectionEnabled) {
                this.mKoreanInput.setExactInList(1);
            } else {
                this.mKoreanInput.setExactInList(3);
            }
        }
    }

    private void endInputSession() {
        if (this.mKoreanInput != null) {
            this.mKoreanInput.finishSession();
        }
    }

    @Override // com.nuance.swype.input.InputView
    public void finishInput() {
        removeAllMessages();
        if (this.mKoreanInput != null) {
            this.mEditState.endSession();
            endInputSession();
            flushCurrentActiveWord();
            this.mLastInput = 0;
            this.mInlineWord.clear();
            dimissRemoveUdbWordDialog();
            this.keyboardTouchEventDispatcher.unwrapTouchEvent(this);
            super.finishInput();
        }
    }

    @Override // com.nuance.swype.input.InputView
    public void flushCurrentActiveWord() {
        InputConnection ic = getCurrentInputConnection();
        if (ic != null) {
            ic.beginBatchEdit();
            ic.finishComposingText();
            clearSuggestions();
            ic.endBatchEdit();
        }
    }

    @Override // com.nuance.swype.input.InputView
    public void updateSelection(int oldSelStart, int oldSelEnd, int newSelStart, int newSelEnd, int[] candidatesIndices) {
        validateComposingText(oldSelStart, oldSelEnd, newSelStart, newSelEnd, candidatesIndices, this.mInlineWord);
        super.updateSelection(oldSelStart, oldSelEnd, newSelStart, newSelEnd, candidatesIndices);
        InputConnection ic = getCurrentInputConnection();
        if (ic != null) {
            if (this.mKoreanInput != null) {
                CharSequence charBeforeCusor = ic.getTextBeforeCursor(1, 0);
                this.mEditState.cursorChanged(charBeforeCusor);
            }
            if (getCurrentWordCandidatesSource() == Candidates.Source.NEXT_WORD_PREDICTION && !isEmptyCandidateList() && isInsideWord() && !this.mInputViewHandler.hasMessages(9)) {
                clearSuggestions();
            }
        }
    }

    @Override // com.nuance.swype.input.InputView
    public boolean handleKey(int primaryCode, boolean quickPressed, int repeatedCount) {
        removeToastMsg(5);
        switch (primaryCode) {
            case KeyboardEx.GESTURE_KEYCODE_CASE_EDIT /* -110 */:
                return true;
            case 32:
                return handleSpace(quickPressed, repeatedCount);
            case KeyboardEx.KEYCODE_KEYBOARD_RESIZE /* 2900 */:
                super.handleKey(primaryCode, quickPressed, repeatedCount);
                setMultitapOrAmbigMode();
                return true;
            case KeyboardEx.KEYCODE_MULTITAP_TOGGLE /* 2940 */:
                if (!this.mKeyboardInputSuggestionOn) {
                    return true;
                }
                postToastMsg(5);
                return true;
            case KeyboardEx.KEYCODE_SPEECH /* 6463 */:
                flushCurrentActiveWord();
                if (quickPressed) {
                    return true;
                }
                super.startSpeech();
                return true;
            case KeyboardEx.KEYCODE_SWYPE /* 43575 */:
                setSwypeKeytoolTipSuggestion();
                return true;
            case KeyboardEx.KEYCODE_MODE_BACK /* 43576 */:
                this.mKeyboardSwitcher.toggleLastKeyboard();
                return true;
            default:
                return super.handleKey(primaryCode, quickPressed, repeatedCount);
        }
    }

    private boolean isWordAcceptingSymbol(char ch) {
        return this.mCharUtils.isPunctuationOrSymbol(ch);
    }

    @Override // com.nuance.swype.input.InputView
    public void onHardKeyText(CharSequence text) {
        if (text != null && text.length() > 0) {
            if (!isComposingText()) {
                InputConnection ic = getCurrentInputConnection();
                ic.beginBatchEdit();
                ic.commitText("", 1);
                ic.endBatchEdit();
            }
            handleHardkeyCharKey(text.charAt(0), null, null, false);
        }
    }

    @Override // com.nuance.swype.input.InputView
    @SuppressLint({"DefaultLocale"})
    public void onText(CharSequence text, long eventTime) {
        boolean isEmoji = getEmojiInputViewController().isEmoji(text);
        if (text != null && text.length() == 1 && !isEmoji) {
            int primaryCode = text.charAt(0);
            int[] keyCodes = {primaryCode};
            handleCharKey(null, primaryCode, keyCodes, eventTime);
            return;
        }
        InputConnection ic = getCurrentInputConnection();
        if (ic != null && text != null && text.length() != 0) {
            if (this.mKoreanInput == null || !this.mTextInputPredictionOn) {
                ic.beginBatchEdit();
                if (isShifted()) {
                    text = text.toString().toUpperCase();
                }
                ic.commitText(text, 1);
                ic.endBatchEdit();
            } else if (text.length() > 1 || isWordAcceptingSymbol(text.charAt(text.length() - 1)) || isEmoji) {
                ic.beginBatchEdit();
                boolean wordAcceptingSymbol = text.length() == 1 && this.mCharUtils.isWordAcceptingSymbol(text.charAt(0));
                boolean punctGesture = text.length() == 2 && this.mCharUtils.isPunctuationOrSymbol(text.charAt(0));
                boolean displayNWP = this.mNextWordPredictionOn && (wordAcceptingSymbol || punctGesture);
                if (composingState()) {
                    selectDefaultSuggestion(false);
                } else {
                    clearCurrentInline(ic);
                    clearSuggestions();
                }
                ic.commitText(text, 1);
                this.mEditState.punctuationOrSymbols();
                if (displayNWP) {
                    this.mKoreanInput.clearAllKeys();
                    updateWordContext();
                    updateSuggestions(Candidates.Source.NEXT_WORD_PREDICTION);
                }
                ic.endBatchEdit();
            } else if (isDigit(text.charAt(text.length() - 1)) && !composingState()) {
                ic.beginBatchEdit();
                clearCurrentInline(ic);
                clearSuggestions();
                ic.commitText(text, 1);
                this.mEditState.punctuationOrSymbols();
                ic.endBatchEdit();
            } else {
                if (!composingState()) {
                    clearCurrentInline(ic);
                    clearSuggestions();
                }
                if (this.inputContextRequestHandler.addExplicitSymbol(text.toString().toCharArray(), Shift.ShiftState.OFF)) {
                    updateSuggestions(Candidates.Source.TAP);
                }
            }
            updateMultitapDeadkey(true);
            resetShiftState();
        }
    }

    @Override // com.nuance.swype.input.InputView
    @SuppressLint({"DefaultLocale"})
    public void onSpeechUpdate(CharSequence text, boolean isOnlyToCommitFinal, boolean isFinal) {
        InputConnection ic = getCurrentInputConnection();
        if (ic != null && text != null && text.length() != 0) {
            if (this.mKoreanInput == null || !this.mTextInputPredictionOn) {
                ic.beginBatchEdit();
                if (isShifted()) {
                    text = text.toString().toUpperCase();
                }
                if (isOnlyToCommitFinal) {
                    ic.commitText("", 1);
                    if (isFinal) {
                        ic.commitText(text, 1);
                        ic.finishComposingText();
                    } else {
                        ic.setComposingText(text, 1);
                    }
                } else {
                    ic.commitText(text, 1);
                }
                ic.endBatchEdit();
            } else if (text.length() > 1 || isWordAcceptingSymbol(text.charAt(text.length() - 1))) {
                ic.beginBatchEdit();
                boolean wordAcceptingSymbol = text.length() == 1 && this.mCharUtils.isWordAcceptingSymbol(text.charAt(0));
                boolean punctGesture = text.length() == 2 && this.mCharUtils.isPunctuationOrSymbol(text.charAt(0));
                boolean displayNWP = this.mNextWordPredictionOn && (wordAcceptingSymbol || punctGesture);
                if (composingState()) {
                    selectDefaultSuggestion(false);
                } else {
                    clearCurrentInline(ic);
                    clearSuggestions();
                }
                if (isOnlyToCommitFinal) {
                    ic.commitText("", 1);
                    if (isFinal) {
                        ic.commitText(text, 1);
                        ic.finishComposingText();
                    } else {
                        ic.setComposingText(text, 1);
                    }
                } else {
                    ic.commitText(text, 1);
                }
                this.mEditState.punctuationOrSymbols();
                if (displayNWP) {
                    this.mKoreanInput.clearAllKeys();
                    updateWordContext();
                    updateSuggestions(Candidates.Source.NEXT_WORD_PREDICTION);
                }
                ic.endBatchEdit();
            } else if (isDigit(text.charAt(text.length() - 1)) && !composingState()) {
                ic.beginBatchEdit();
                clearCurrentInline(ic);
                clearSuggestions();
                if (isOnlyToCommitFinal) {
                    ic.commitText("", 1);
                    if (isFinal) {
                        ic.commitText(text, 1);
                        ic.finishComposingText();
                    } else {
                        ic.setComposingText(text, 1);
                    }
                } else {
                    ic.commitText(text, 1);
                }
                this.mEditState.punctuationOrSymbols();
                ic.endBatchEdit();
            } else {
                if (!composingState()) {
                    clearCurrentInline(ic);
                    clearSuggestions();
                }
                this.mKoreanInput.addExplicit(text.toString().toCharArray(), text.length(), Shift.ShiftState.OFF);
                updateSuggestions(Candidates.Source.TAP);
            }
            updateMultitapDeadkey(true);
            resetShiftState();
        }
    }

    private boolean isDigit(char ch) {
        return '0' <= ch && ch <= '9';
    }

    @Override // com.nuance.swype.input.InputView
    public void handleCharKey(Point point, int primaryCode, int[] keyCodes, long eventTime) {
        super.handleCharKey(point, primaryCode, keyCodes, eventTime);
        removeToastMsg(5);
        if (this.mKoreanInput == null || (!this.mTextInputPredictionOn && !isMultiTapKeypad())) {
            if (isShifted()) {
                primaryCode = Character.toUpperCase(primaryCode);
            }
            sendKeyChar((char) primaryCode);
        } else {
            if (getCurrentWordCandidatesSource() == Candidates.Source.RECAPTURE_BY_TEXT_SELECTION) {
                clearAllKeys();
                updateWordContext();
            }
            if (CharacterUtilities.isWhiteSpace((char) primaryCode)) {
                handleWhiteSpaces(primaryCode);
            } else if (isWordAcceptingSymbol((char) primaryCode) || ((isDigit((char) primaryCode) || this.mCharUtils.isWordCompounder((char) primaryCode)) && !composingState())) {
                handlePunctOrSymbol(primaryCode);
            } else {
                handlePrediction(point, primaryCode, eventTime);
            }
        }
        if (this.mCharUtils.isWordCompounder((char) primaryCode) && composingState() && !this.mKeyboardSwitcher.isAlphabetMode() && this.mKeyboardSwitcher.supportsAlphaMode()) {
            toggleKeyboardMode();
        }
        if (isMultitapping()) {
            updateMultitapDeadkey(true);
        }
        recordUsedTimeTapDisplaySelectionList();
        this.mLastInput = 1;
    }

    private boolean composingState() {
        return this.candidatesListView != null && this.candidatesListView.wordCount() > 0 && this.mKoreanInput.getKeyCount() > 0 && this.mEditState.current() != 8;
    }

    @Override // com.nuance.swype.input.InputView
    public void handleTrace(KeyboardViewEx.TracePoints trace) {
        InputConnection ic = getCurrentInputConnection();
        if (ic != null) {
            ic.beginBatchEdit();
            if (this.touchKeyActionHandler.processStoredTouch() && updateSuggestions(Candidates.Source.TRACE, false) == 0) {
                setNotMatchToolTipSuggestion();
            }
            ic.endBatchEdit();
            resetShiftState();
        }
    }

    private void toggleKeyboardMode() {
        this.mKeyboardSwitcher.toggleSymbolKeyboard();
        abortKey();
        setMultitapOrAmbigMode();
    }

    @Override // com.nuance.swype.input.InputView
    public boolean handleBackspace(int repeatedCount) {
        AppSpecificInputConnection ic = getCurrentInputConnection();
        if (this.mKoreanInput == null || ic == null) {
            sendBackspace(repeatedCount);
        } else {
            CharSequence cSeqBefore = ic.getTextBeforeCursor(50, 0);
            if (cSeqBefore == null) {
                this.mEditState.backSpace();
                sendBackspace(repeatedCount);
                showNextWordPrediction();
                clearAllKeys();
            } else {
                if (this.mIme.isHardKeyboardActive() && isShowingCandidatesFor(Candidates.Source.NEXT_WORD_PREDICTION) && this.mInlineWord.length() > 0) {
                    clearCurrentInline(ic);
                    this.mInlineWord.clear();
                    if (!IsTextFieldEmpty()) {
                        clearSuggestions();
                    }
                }
                int lengthBefore = cSeqBefore.length();
                if (this.mKoreanInput.getKeyCount() == 0 && lengthBefore > 0) {
                    this.mEditState.backSpace();
                    sendBackspace(repeatedCount);
                    if (repeatedCount <= 3 && !isInsideWord()) {
                        showNextWordPrediction();
                    }
                    clearAllKeys();
                } else if (this.mKoreanInput.getKeyCount() > 0) {
                    this.activeHangul.setLength(0);
                    if (!clearSelectionKeys()) {
                        this.mKoreanInput.clearKey();
                    }
                    if (this.mKoreanInput.getKeyCount() > 0) {
                        this.mKoreanInput.getInlineHangul(this.activeHangul);
                        displayInlineHangul(ic, this.activeHangul, true);
                        updateSuggestions(Candidates.Source.TAP);
                    } else {
                        displayInlineHangul(ic, this.activeHangul, true);
                        showNextWordPrediction();
                        if (!this.mNextWordPredictionOn) {
                            clearSuggestions();
                        }
                        this.mEditState.backSpace();
                        if (this.mEditState.current() == 1) {
                            this.mEditState.start();
                        }
                    }
                } else {
                    if (ic.hasSelection()) {
                        ic.clearHighlightedText();
                    } else {
                        sendBackspace(repeatedCount);
                    }
                    clearAllKeys();
                    resetContextBuffer();
                    this.mEditState.backSpace();
                }
            }
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.KeyboardViewEx
    public void resetMultiTap() {
        KeyboardEx.Key currentKey;
        if (this.mInMultiTap && this.mCurrentKey != -1 && this.mCurrentKey < this.mKeys.length && (currentKey = this.mKeys[this.mCurrentKey]) != null && currentKey.codes.length > 0 && currentKey.codes[0] == 32) {
            this.isTimeoutBySpaceKey = true;
        }
        super.resetMultiTap();
    }

    @Override // com.nuance.swype.input.InputView
    public boolean handleSpace(boolean quickPressed, int repeatedCount) {
        CharSequence cSeqBefore;
        InputConnection ic = getCurrentInputConnection();
        if (this.mKoreanInput == null || ic == null) {
            sendSpace();
        } else {
            ic.beginBatchEdit();
            boolean addSpace = true;
            boolean displayNWP = this.mNextWordPredictionOn;
            if (this.isTimeoutBySpaceKey && ((this.mKeyboardLayoutId == 1536 || this.mKeyboardLayoutId == 1792 || this.mKeyboardLayoutId == 1808 || this.mKeyboardLayoutId == 1824) && this.mInlineWord.length() > 0)) {
                this.isTimeoutBySpaceKey = false;
                ic.endBatchEdit();
            } else {
                selectDefaultSuggestion(false);
                if (quickPressed && repeatedCount < 2 && this.mAutoPunctuationOn && this.mTextInputPredictionOn && !this.mKeyboardSwitcher.isKeypadInput() && (cSeqBefore = ic.getTextBeforeCursor(2, 0)) != null && cSeqBefore.length() == 2 && CharacterUtilities.isWhiteSpace(cSeqBefore.charAt(1)) && !CharacterUtilities.isWhiteSpace(cSeqBefore.charAt(0)) && !this.mCharUtils.isPunctuationOrSymbol(cSeqBefore.charAt(0))) {
                    handleAutoPunct();
                    addSpace = false;
                }
                if (addSpace) {
                    processSpaceKeyState(ic);
                    sendKeyChar(' ');
                } else {
                    this.mEditState.start();
                }
                if (displayNWP) {
                    updateWordContext();
                    updateSuggestions(Candidates.Source.NEXT_WORD_PREDICTION);
                }
                ic.endBatchEdit();
            }
        }
        return true;
    }

    @Override // com.nuance.swype.input.InputView
    public void handleClose() {
        dimissRemoveUdbWordDialog();
        if (this.mIme.isInputViewShown()) {
            selectDefaultSuggestion();
        } else {
            flushCurrentActiveWord();
        }
        removeAllMessages();
        super.handleClose();
    }

    @Override // com.nuance.swype.input.InputView, com.nuance.swype.input.CandidatesListView.CandidateListener
    public boolean onSelectCandidate(WordCandidate candidate, Candidates candidates) {
        boolean displayNextWordPrediction = false;
        boolean composingWordList = composingWordCandidates();
        if (((this.suggestionCandidates != null && this.suggestionCandidates.source() == Candidates.Source.NEXT_WORD_PREDICTION) || composingWordList) && this.mNextWordPredictionOn) {
            displayNextWordPrediction = true;
        }
        this.mLastInput = 4;
        processWordSelection(candidate, candidates, true);
        if (displayNextWordPrediction) {
            updateSuggestions(Candidates.Source.NEXT_WORD_PREDICTION);
        }
        super.addEmojiInRecentCat(candidate);
        return true;
    }

    private boolean composingWordCandidates() {
        return !isEmptyCandidateList() && (this.suggestionCandidates.source() == Candidates.Source.TRACE || this.suggestionCandidates.source() == Candidates.Source.TAP || this.suggestionCandidates.source() == Candidates.Source.RECAPTURE);
    }

    private void updateMultitapDeadkey(boolean invalidateKeyNow) {
        XT9Keyboard keyboard;
        KeyboardEx.Key key;
        if (this.mKeyboardLayoutId != 2304 && (keyboard = (XT9Keyboard) getKeyboard()) != null) {
            if (!this.mKeyboardInputSuggestionOn) {
                key = keyboard.setXT9KeyState(-1);
                if (key != null) {
                    key.codes[0] = 2942;
                }
            } else if (this.mKeyboardSwitcher.getCurrentInputMode().equals(InputMethods.MULTITAP_INPUT_MODE)) {
                if (this.mKoreanInput.getKeyCount() > 0) {
                    key = keyboard.setXT9KeyState(2);
                    if (key != null) {
                        key.codes[0] = 2942;
                    }
                } else {
                    key = keyboard.setXT9KeyState(0);
                    if (key != null) {
                        key.codes[0] = 2940;
                    }
                }
            } else {
                key = keyboard.setXT9KeyState(1);
                if (key != null) {
                    key.codes[0] = 2940;
                }
            }
            if (key != null && invalidateKeyNow) {
                invalidateKey(key);
            }
        }
    }

    @Override // com.nuance.swype.input.InputView
    public void handleMultitapToggle() {
        if (this.mKeyboardInputSuggestionOn) {
            toggleAmbigMode();
            String inputMode = this.mKeyboardSwitcher.getCurrentInputMode();
            setAutoCorrection();
            AppPreferences.from(getContext()).setMultitapMode(inputMode.equals(InputMethods.MULTITAP_INPUT_MODE));
            selectDefaultSuggestion(false);
            updateMultitapDeadkey(true);
            this.mIme.refreshLanguageOnSpaceKey(getKeyboard(), this);
        }
    }

    @Override // com.nuance.swype.input.InputView
    public void onMultitapTimeout() {
        InputConnection ic = getCurrentInputConnection();
        if (ic != null) {
            if (!this.mTextInputPredictionOn) {
                this.mInlineWord.clearSpans();
                ic.commitText(this.mInlineWord, 1);
                this.mInlineWord.clear();
                this.mKoreanInput.clearAllKeys();
            } else if (this.mInlineWord.length() > 0) {
                this.mInlineWord.removeSpan(this.mBKMultiptappingCharSpan);
                this.mInlineWord.removeSpan(this.mFGMultiptappingCharSpan);
                if (this.activeHangul.length() > 0) {
                    if (this.suggestionCandidates != null && this.suggestionCandidates.source() == Candidates.Source.TRACE) {
                        displayInlineHangul(ic, this.activeHangul, false);
                    } else {
                        displayInlineHangul(ic, this.activeHangul, true);
                    }
                    ic.setComposingText(this.mInlineWord, 1);
                }
            }
            invalidateKeyboardImage();
        }
    }

    private void addCompoundingWord(InputConnection ic, CharSequence wordJustSelected) {
        CharSequence seqBeforeCursor;
        if (ic != null && (seqBeforeCursor = ic.getTextBeforeCursor(17, 0)) != null) {
            CharSequence seqBeforeCursor2 = seqBeforeCursor.toString().trim();
            int index = seqBeforeCursor2.length() - 1;
            if (index > 0) {
                while (index > 0 && !CharacterUtilities.isWhiteSpace(seqBeforeCursor2.charAt(index))) {
                    index--;
                }
                if (CharacterUtilities.isWhiteSpace(seqBeforeCursor2.charAt(index))) {
                    index++;
                }
                String compoundWord = seqBeforeCursor2.toString().substring(index);
                String contextBeforeCompoundWord = seqBeforeCursor2.toString().substring(0, index);
                if (compoundWord.length() > wordJustSelected.length() && compoundWord.length() <= 16 && !this.mInputFieldInfo.isUDBSubstitutionField() && !this.mInputFieldInfo.isPasswordField()) {
                    this.mKoreanInput.setContext(contextBeforeCompoundWord.toCharArray());
                    this.mKoreanInput.noteWordDone(compoundWord);
                }
            }
        }
    }

    private void processSpaceKeyState(InputConnection ic) {
        this.mEditState.spaceKey();
        if (6 == this.mEditState.current() && ic != null) {
            addCompoundingWord(ic, this.mEditState.selectedWord());
        }
    }

    private boolean isMultiTapKeypad() {
        return this.mKeyboardLayoutId != 2304 && this.mKeyboardSwitcher.isKeypadInput();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setMultitapOrAmbigMode() {
        this.mKoreanInput.setMultiTapInputMode(isMultiTapKeypad());
    }

    private void setAutoCorrection() {
        if (isMultiTapKeypad()) {
            this.mKoreanInput.setAttribute(99, false);
        } else {
            this.mKoreanInput.setAttribute(99, this.autoCorrectionEnabled);
        }
    }

    @Override // com.nuance.swype.input.InputView
    public void handlePossibleActionAfterUniversalKeyboardResize(boolean hideSymbolView) {
        setMultitapOrAmbigMode();
    }

    private void toggleAmbigMode() {
        if (this.mKeyboardSwitcher.isKeypadInput()) {
            if (this.mKeyboardSwitcher.getCurrentInputMode().equals(InputMethods.MULTITAP_INPUT_MODE)) {
                this.mKeyboardSwitcher.createKeyboardForTextInput(this.mInputFieldInfo, this.mCurrentInputLanguage.getInputMode(this.mCurrentInputLanguage.mDefaultInputMode));
            } else {
                this.mKeyboardSwitcher.createKeyboardForTextInput(this.mInputFieldInfo, this.mCurrentInputLanguage.getInputMode(InputMethods.MULTITAP_INPUT_MODE));
            }
        }
    }

    private void handleAutoPunct() {
        InputConnection ic = getCurrentInputConnection();
        if (ic != null) {
            ic.deleteSurroundingText(1, 0);
            ic.commitText(".", 1);
            sendKeyChar(' ');
        }
    }

    private void sendBackCharOnFailProcess(char ch) {
        flushCurrentActiveWord();
        if (isShifted()) {
            ch = Character.toUpperCase(ch);
        }
        sendKeyChar(ch);
    }

    private void handlePrediction(Point point, int primaryCode, long eventTime) {
        boolean processStoredTouch;
        InputConnection ic = getCurrentInputConnection();
        if (ic != null) {
            if ((primaryCode != NARATGUL_KEYCODE_ADD_STROKE && primaryCode != NARATGUL_KEYCODE_DOUBLE_CONSONANTS) || this.mKoreanInput.getKeyCount() != 0) {
                if (this.mKeyboardSwitcher.isKeypadInput() && (primaryCode == CHUNJIIN_KEYCODE_NUM_ONE || primaryCode == CHUNJIIN_KEYCODE_NUM_TWO || primaryCode == CHUNJIIN_KEYCODE_NUM_THREE)) {
                    this.mKoreanInput.multiTapTimeOut();
                }
                clearSelectionKeys();
                if (point == null) {
                    processStoredTouch = this.mKoreanInput.processKey(null, primaryCode, getShiftState(), eventTime);
                } else {
                    processStoredTouch = this.touchKeyActionHandler.processStoredTouch();
                }
                if (processStoredTouch) {
                    this.mEditState.characterTyped((char) primaryCode);
                    if (this.mTextInputPredictionOn) {
                        if (this.mInputViewHandler.hasMessages(9)) {
                            this.mInputViewHandler.removeMessages(9);
                        }
                        this.mInputViewHandler.sendMessageDelayed(this.mInputViewHandler.obtainMessage(9, 0, 0, Candidates.Source.TAP), 70L);
                    }
                    this.mKoreanInput.getInlineHangul(this.activeHangul);
                    if (!this.mTextInputPredictionOn && isMultitapping()) {
                        displayMultiTapInline(ic, this.activeHangul);
                    } else {
                        displayInlineHangul(ic, this.activeHangul, true);
                    }
                    this.mKoreanInput.setShiftState(Shift.ShiftState.OFF);
                    updateUIShiftState(this.mKoreanInput.getShiftState());
                }
            }
        }
    }

    private void displayMultiTapInline(InputConnection ic, CharSequence hangul) {
        this.mInlineWord.clear();
        this.mInlineWord.clearSpans();
        this.mInlineWord.append(hangul);
        this.mInlineWord.setSpan(this.mBKMultiptappingCharSpan, 0, this.mInlineWord.length(), R.styleable.ThemeTemplate_emojiCategoryIconSmiles);
        this.mInlineWord.setSpan(this.mFGMultiptappingCharSpan, 0, this.mInlineWord.length(), R.styleable.ThemeTemplate_emojiCategoryIconSmiles);
        ic.setComposingText(this.mInlineWord, 1);
    }

    private void displayInlineHangul(InputConnection ic, CharSequence hangul, boolean setSpan) {
        this.mInlineWord.clear();
        this.mInlineWord.clearSpans();
        if (hangul.length() > 0) {
            this.mInlineWord.append(hangul);
            if (setSpan || getAppSpecificBehavior().shouldSetComosingSpan()) {
                this.mInlineWord.setSpan(this.mWordComposeSpan, 0, this.mInlineWord.length(), R.styleable.ThemeTemplate_emojiCategoryIconSmiles);
            }
            ic.setComposingText(this.mInlineWord, 1);
            return;
        }
        ic.commitText("", 1);
    }

    private void handleWhiteSpaces(int primaryCode) {
        if (primaryCode == 32) {
            handleSpace(false, 1);
        } else {
            handlePunctOrSymbol(primaryCode);
        }
    }

    private void clearCurrentInline(InputConnection ic) {
        if (this.mInlineWord != null && this.mInlineWord.length() > 0 && ic != null) {
            ic.setComposingText("", 0);
        }
    }

    private void handlePunctOrSymbol(int primaryCode) {
        if (primaryCode == 32) {
            handleSpace(false, 1);
            return;
        }
        if (this.mKoreanInput != null && composingState()) {
            selectDefaultSuggestion(true);
        } else {
            clearCurrentInline(getCurrentInputConnection());
        }
        clearSuggestions();
        if (primaryCode == 10) {
            resetContextBuffer();
        }
        char keyChar = (char) primaryCode;
        if (',' == keyChar || '.' == keyChar || '!' == keyChar || '?' == keyChar) {
            this.mKoreanInput.noteWordDone(String.valueOf(keyChar));
        }
        this.mEditState.punctuationOrSymbols();
        sendKeyChar(keyChar);
        updateMultitapDeadkey(true);
        if (this.mNextWordPredictionOn && !Character.isDigit(primaryCode)) {
            updateWordContext();
            if (primaryCode != 10) {
                updateSuggestions(Candidates.Source.NEXT_WORD_PREDICTION);
            }
        }
        this.mLastInput = 1;
    }

    @Override // com.nuance.swype.input.InputView
    @SuppressLint({"PrivateResource"})
    public int updateSuggestions(Candidates.Source source, boolean noComposingText) {
        InputConnection ic = getCurrentInputConnection();
        if (this.emojiCandidatesHolder != null) {
            this.emojiCandidatesHolder.setVisibility(8);
        }
        if (this.mKoreanInput == null || this.candidatesListView == null || ic == null || source == Candidates.Source.CAPS_EDIT) {
            return 0;
        }
        if (this.mInputViewHandler.hasMessages(9)) {
            this.mInputViewHandler.removeMessages(9);
        }
        Candidates candidates = this.mKoreanInput.getCandidates(source);
        if (!isValidBuild()) {
            candidates = null;
        }
        if (candidates == null) {
            return 0;
        }
        if (handleGesture(candidates)) {
            return 1;
        }
        if (this.mTextInputPredictionOn) {
            if (this.mPreferExplicit && candidates.source() != Candidates.Source.RECAPTURE) {
                candidates.setDefaultIndex(0);
                candidates.setExactIndex(0);
            }
            setSuggestions(this, candidates, getWordListFormat(candidates));
            if (noComposingText) {
                if (!hasInputQueue() && source == Candidates.Source.NEXT_WORD_PREDICTION) {
                    UserPreferences userPrefs = UserPreferences.from(this.mIme);
                    if (this.mIme.isHardKeyboardActive() && !userPrefs.getBoolean(HardKeyboardManager.SHORTCUT_TO_SELECT_NWP, false)) {
                        userPrefs.setBoolean(HardKeyboardManager.SHORTCUT_TO_SELECT_NWP, true);
                        InputMethodToast.show(this.mIme, getResources().getString(R.string.hardkeyboard_input_next_word_prediction), 0);
                    }
                }
                return candidates.count();
            }
            if (candidates.count() > 0) {
                if (this.mInlineWord.length() == 0 && source == Candidates.Source.TRACE) {
                    this.activeHangul.setLength(0);
                    this.activeHangul.append(candidates.getExactCandidateString());
                    displayInlineHangul(ic, this.activeHangul, false);
                } else if (isMultitapping()) {
                    this.mInlineWord.clear();
                    this.mInlineWord.clearSpans();
                    this.mInlineWord.append(candidates.getExactCandidateString());
                    this.mInlineWord.setSpan(this.mWordComposeSpan, 0, this.mInlineWord.length(), R.styleable.ThemeTemplate_emojiCategoryIconSmiles);
                    this.mInlineWord.setSpan(this.mBKMultiptappingCharSpan, this.mInlineWord.length() - 1, this.mInlineWord.length(), R.styleable.ThemeTemplate_emojiCategoryIconSmiles);
                    this.mInlineWord.setSpan(this.mFGMultiptappingCharSpan, this.mInlineWord.length() - 1, this.mInlineWord.length(), R.styleable.ThemeTemplate_emojiCategoryIconSmiles);
                    ic.setComposingText(this.mInlineWord, 1);
                }
            } else {
                flushCurrentActiveWord();
            }
        }
        return candidates.count();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.InputView
    public void clearAllKeys() {
        this.mInlineWord.clearSpans();
        this.mInlineWord.clear();
        if (this.mKoreanInput != null) {
            this.mKoreanInput.clearAllKeys();
        }
        updateMultitapDeadkey(true);
    }

    @Override // com.nuance.swype.input.InputView
    public void handleShiftKey() {
        this.mKeyboardSwitcher.cycleShiftState();
        this.mKoreanInput.setShiftState(getShiftState());
        invalidateKeyboardImage();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateUIShiftState(Shift.ShiftState coreShiftState) {
        if (getShiftState() != coreShiftState) {
            if (this.mKeyboardSwitcher.isAlphabetMode()) {
                setShiftState(coreShiftState);
                invalidateKeyboardImage();
            } else if (getShiftState() != Shift.ShiftState.OFF) {
                this.mKoreanInput.setShiftState(getShiftState());
            }
        }
    }

    @Override // com.nuance.swype.input.KeyboardViewEx
    public void setPressDownPreviewEnabled(boolean previewEnabled) {
        if (this.mKeyboardLayoutId == 1536 || this.mKeyboardLayoutId == 1792 || this.mKeyboardLayoutId == 1808 || this.mKeyboardLayoutId == 1824) {
            super.setPressDownPreviewEnabled(false);
        } else {
            super.setPressDownPreviewEnabled(previewEnabled);
        }
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public KoreanInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mWordComposeSpan = new UnderlineSpan();
        this.mInlineWord = new SpannableStringBuilder();
        this.activeHangul = new StringBuilder();
        this.inputViewHandlerCallback = new Handler.Callback() { // from class: com.nuance.swype.input.korean.KoreanInputView.1
            /* JADX WARN: Failed to find 'out' block for switch in B:2:0x0003. Please report as an issue. */
            @Override // android.os.Handler.Callback
            @SuppressLint({"PrivateResource"})
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case 5:
                        QuickToast.show(KoreanInputView.this.getContext(), KoreanInputView.this.getResources().getString(R.string.multitap_toggle_tip), 1, KoreanInputView.this.getHeight() + KoreanInputView.this.candidatesListView.getHeight());
                        return true;
                    case 6:
                        QuickToast.show(KoreanInputView.this.getContext(), KoreanInputView.this.getResources().getString(R.string.trace_auto_accept_tip), 1, KoreanInputView.this.getHeight() + KoreanInputView.this.candidatesListView.getHeight());
                        return true;
                    case 7:
                        QuickToast.show(KoreanInputView.this.getContext(), KoreanInputView.this.getResources().getString(R.string.trace_input_tip), 1, KoreanInputView.this.getHeight() + KoreanInputView.this.candidatesListView.getHeight());
                        return true;
                    case 8:
                        KoreanInputView.this.showStartOfWordCandidateList();
                        return true;
                    case 9:
                        KoreanInputView.this.updateSuggestions((Candidates.Source) msg.obj);
                        return true;
                    case 11:
                        if (KoreanInputView.this.mSpeechWrapper != null && KoreanInputView.this.mSpeechWrapper.isResumable()) {
                            KoreanInputView.this.flushCurrentActiveWord();
                        }
                        KoreanInputView.this.setSpeechViewHost();
                        KoreanInputView.this.resumeSpeech();
                        return true;
                    case 15:
                        KoreanInputView.this.startInputSession();
                        KoreanInputView.this.setMultitapOrAmbigMode();
                        return true;
                    case 125:
                        KoreanInputView.this.themeStoreWclPrompt.showMessage("alphaInput");
                        return true;
                    default:
                        return false;
                }
            }
        };
        this.mInputViewHandler = WeakReferenceHandler.create(this.inputViewHandlerCallback);
        this.inputContextRequestHandler = new InputContextRequestHandler();
        this.touchKeyActionHandler = new TouchKeyActionHandler();
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public KoreanInputView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mWordComposeSpan = new UnderlineSpan();
        this.mInlineWord = new SpannableStringBuilder();
        this.activeHangul = new StringBuilder();
        this.inputViewHandlerCallback = new Handler.Callback() { // from class: com.nuance.swype.input.korean.KoreanInputView.1
            /* JADX WARN: Failed to find 'out' block for switch in B:2:0x0003. Please report as an issue. */
            @Override // android.os.Handler.Callback
            @SuppressLint({"PrivateResource"})
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case 5:
                        QuickToast.show(KoreanInputView.this.getContext(), KoreanInputView.this.getResources().getString(R.string.multitap_toggle_tip), 1, KoreanInputView.this.getHeight() + KoreanInputView.this.candidatesListView.getHeight());
                        return true;
                    case 6:
                        QuickToast.show(KoreanInputView.this.getContext(), KoreanInputView.this.getResources().getString(R.string.trace_auto_accept_tip), 1, KoreanInputView.this.getHeight() + KoreanInputView.this.candidatesListView.getHeight());
                        return true;
                    case 7:
                        QuickToast.show(KoreanInputView.this.getContext(), KoreanInputView.this.getResources().getString(R.string.trace_input_tip), 1, KoreanInputView.this.getHeight() + KoreanInputView.this.candidatesListView.getHeight());
                        return true;
                    case 8:
                        KoreanInputView.this.showStartOfWordCandidateList();
                        return true;
                    case 9:
                        KoreanInputView.this.updateSuggestions((Candidates.Source) msg.obj);
                        return true;
                    case 11:
                        if (KoreanInputView.this.mSpeechWrapper != null && KoreanInputView.this.mSpeechWrapper.isResumable()) {
                            KoreanInputView.this.flushCurrentActiveWord();
                        }
                        KoreanInputView.this.setSpeechViewHost();
                        KoreanInputView.this.resumeSpeech();
                        return true;
                    case 15:
                        KoreanInputView.this.startInputSession();
                        KoreanInputView.this.setMultitapOrAmbigMode();
                        return true;
                    case 125:
                        KoreanInputView.this.themeStoreWclPrompt.showMessage("alphaInput");
                        return true;
                    default:
                        return false;
                }
            }
        };
        this.mInputViewHandler = WeakReferenceHandler.create(this.inputViewHandlerCallback);
        this.inputContextRequestHandler = new InputContextRequestHandler();
        this.touchKeyActionHandler = new TouchKeyActionHandler();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.KeyboardViewEx
    public boolean isTraceInputEnabled() {
        return this.isTraceEnabledOnKeyboard;
    }

    @Override // com.nuance.swype.input.InputView
    public void onApplicationUnbind() {
        super.onApplicationUnbind();
        dimissRemoveUdbWordDialog();
        clearSuggestions();
    }

    @Override // com.nuance.swype.input.InputView
    public void setKeyboardLayer(KeyboardEx.KeyboardLayerType keyboardLayer) {
        if (getKeyboardLayer() != keyboardLayer) {
            super.setKeyboardLayer(keyboardLayer);
            this.mEditState.startSession();
            dismissPopupKeyboard();
            this.mKeyboardSwitcher.createKeyboardForTextInput(keyboardLayer, this.mInputFieldInfo, this.mCurrentInputLanguage.getCurrentInputMode());
            setMultitapOrAmbigMode();
            setShiftState(Shift.ShiftState.OFF);
            this.mKoreanInput.setShiftState(Shift.ShiftState.OFF);
        }
    }

    @Override // com.nuance.swype.input.InputView, com.nuance.swype.input.KeyboardViewEx
    public void setKeyboard(KeyboardEx keyboard) {
        boolean z = false;
        super.setKeyboard(keyboard);
        if (getKeyboard() == null) {
            this.isTraceEnabledOnKeyboard = false;
            return;
        }
        if (this.mTextInputPredictionOn && this.mTraceInputSuggestionOn && this.mKeyboardSwitcher.isAlphabetMode() && !this.mKeyboardSwitcher.isKeypadInput() && getKeyboard().getKeyboardDockMode() != KeyboardEx.KeyboardDockMode.DOCK_SPLIT) {
            z = true;
        }
        this.isTraceEnabledOnKeyboard = z;
        this.mKoreanInput.enableTrace(this.isTraceEnabledOnKeyboard);
        setMultitapOrAmbigMode();
        setAutoCorrection();
        setLanguage(this.mKoreanInput);
    }

    @SuppressLint({"PrivateResource"})
    private void readStyles(Context context) {
        TypedArrayWrapper a = IMEApplication.from(context).getThemeLoader().obtainStyledAttributes$6d3b0587(context, null, R.styleable.InlineStringAlpha, 0, R.style.InlineStringAlpha, R.xml.defaults, "InlineStringAlpha");
        int n = a.delegateTypedArray.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.InlineStringAlpha_wordErrorForegroundColor) {
                ForegroundColorSpan FGWordErrorSpan = new ForegroundColorSpan(a.getColor(attr, -65536));
                Color.colorToHSV(FGWordErrorSpan.getForegroundColor(), hsv);
                float[] hsv = {0.0f, 0.0f, hsv[2] * 0.75f};
            } else if (attr == R.styleable.InlineStringAlpha_multitapForegroundColor) {
                this.mFGMultiptappingCharSpan = new ForegroundColorSpan(a.getColor(attr, -1));
            } else if (attr == R.styleable.InlineStringAlpha_multitapBackgroundColor) {
                this.mBKMultiptappingCharSpan = new BackgroundColorSpan(a.getColor(attr, -65536));
            }
        }
        a.recycle();
    }

    @Override // com.nuance.swype.input.InputView
    public boolean isLanguageSwitchableOnSpace() {
        return this.mKeyboardSwitcher.isSymbolMode();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.KeyboardViewEx
    public boolean handleScrollLeft() {
        if (IMEApplication.from(this.mIme).getIMEHandlerManager() == null) {
            return false;
        }
        this.mEditState.end();
        this.mIme.handlerManager.toggleKeyboard();
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.KeyboardViewEx
    public boolean handleScrollRight() {
        if (IMEApplication.from(this.mIme).getIMEHandlerManager() == null) {
            return false;
        }
        this.mEditState.end();
        this.mIme.handlerManager.toggleKeyboard();
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.KeyboardViewEx
    public boolean isSupportMultitouchGesture() {
        return true;
    }

    private void resetShiftState() {
        if (getKeyboardLayer() == KeyboardEx.KeyboardLayerType.KEYBOARD_TEXT) {
            if (getShiftState() == Shift.ShiftState.ON || getShiftState() == Shift.ShiftState.LOCKED) {
                setShiftState(Shift.ShiftState.OFF);
            }
        }
    }

    private void removeSpaceBeforeCursorIfNeeded(AppSpecificInputConnection ic, WordCandidate candidate) {
    }

    private void processWordSelection(WordCandidate candidate, Candidates candidates, boolean userExplicitPick) {
        int index = candidate.id();
        CharSequence word = candidate.toString();
        if (!TextUtils.isEmpty(word)) {
            if (this.suggestionCandidates != null) {
                this.mEditState.selectWord(word, this.suggestionCandidates.getDefaultCandidateString());
            }
            if (this.mCompletionOn && index >= 0 && index < this.mCompletions.size()) {
                getCurrentInputConnection().commitCompletion(this.mCompletions.get(index));
                clearSuggestions();
            } else if (getSpeechAlternateCandidates().isEmpty()) {
                this.mKoreanInput.setWordQuarantineLevel(1, 1, 1);
                this.mKoreanInput.wordSelected(index, userExplicitPick);
                removeSpaceBeforeCursorIfNeeded(getCurrentInputConnection(), candidate);
                if (!this.mInputFieldInfo.isUDBSubstitutionField() && !this.mInputFieldInfo.isPasswordField()) {
                    this.mKoreanInput.noteWordDone(word.toString());
                }
                clearAllKeys();
            } else {
                super.speechChooseCandidate(index);
            }
            InputConnection ic = getCurrentInputConnection();
            if (ic != null) {
                if (candidates.source() == Candidates.Source.NEXT_WORD_PREDICTION && word.length() == 1 && TERMINAL_PUNCT_PREDICTION.contains(String.valueOf(word.charAt(0)))) {
                    CharSequence seq = ic.getTextBeforeCursor(1, 0);
                    if (!TextUtils.isEmpty(seq) && Character.isWhitespace(seq.charAt(0))) {
                        sendBackspace(1);
                    }
                }
                ic.commitText(candidate.toString(), 1);
            }
            clearSuggestions();
        }
    }

    private void resetContextBuffer() {
        this.mKoreanInput.setContext(null);
    }

    private void selectDefaultSuggestion(boolean addCompoundingWord) {
        if (shouldSelectDefaultCandidate()) {
            WordCandidate defaultCandidate = this.suggestionCandidates.getDefaultCandidate();
            processWordSelection(defaultCandidate, this.suggestionCandidates, false);
            if (addCompoundingWord) {
                addCompoundingWord(getCurrentInputConnection(), defaultCandidate.toString());
                return;
            }
            return;
        }
        flushCurrentActiveWord();
    }

    private void selectDefaultSuggestion() {
        selectDefaultSuggestion(StatisticsEnabledEditState.DefaultSelectionType.GENERIC);
    }

    @Override // com.nuance.swype.input.InputView
    public void selectDefaultSuggestion(StatisticsEnabledEditState.DefaultSelectionType type) {
        setDefaultWordType(type);
        selectDefaultSuggestion(false);
    }

    @Override // com.nuance.swype.input.InputView
    public void handleEmotionKey() {
        this.mIme.showEmojiInputView();
    }

    @Override // com.nuance.swype.input.InputView
    public void setSwypeKeytoolTipSuggestion() {
        AppSpecificInputConnection ic = getCurrentInputConnection();
        String selectedText = ic == null ? null : ic.getSelectedTextInEditor(this.mInputFieldInfo);
        if (selectedText == null || selectedText.length() <= 0) {
            super.setSwypeKeytoolTipSuggestion();
        }
    }

    @Override // com.nuance.swype.input.InputView
    public void displayCompletions(CompletionInfo[] completions) {
        if (!this.mTextInputPredictionOn) {
            super.displayCompletions(completions);
        }
    }

    @Override // com.nuance.swype.input.InputView, com.nuance.swype.input.CandidatesListView.CandidateListener
    public boolean onPressHoldCandidate(WordCandidate candidate, Candidates candidates) {
        if (candidate != null) {
            if (this.mKoreanInput != null && EmojiLoader.isEmoji(candidate.toString())) {
                return handlePressHoldCandidate(candidate, candidates);
            }
            if ((candidate.source() == WordCandidate.Source.WORD_SOURCE_USER_ADDED && this.mKoreanInput != null && this.mKoreanInput.dlmFind(candidate.toString())) || (candidate.source() != WordCandidate.Source.WORD_SOURCE_USER_ADDED && candidate.source() != WordCandidate.Source.WORD_SOURCE_NEW_WORD)) {
                showRemoveUdbWordDialog(candidate.toString());
                return true;
            }
        }
        return false;
    }

    @Override // com.nuance.swype.input.InputView, com.nuance.swype.input.CandidatesListView.CandidateListener
    public boolean onPressReleaseCandidate(WordCandidate candidate, Candidates candidates) {
        if (candidate == null) {
            return false;
        }
        super.setCurrentSkinTone();
        super.dismissEmojiPopup();
        return true;
    }

    @Override // com.nuance.swype.input.InputView, com.nuance.swype.input.CandidatesListView.CandidateListener
    public void onPressMoveCandidate(float xPos, float yPos, float xOffset) {
        log.d("onPressMoveCandidate()", " called ::  xPos:: " + xPos + ", yPos:: " + yPos + ", xOffset :: " + xOffset);
        super.touchMoveHandle(xPos, yPos, xOffset);
    }

    @Override // com.nuance.swype.input.InputView, com.nuance.swype.input.RemoveUdbWordDialog.RemoveUdbWordListener
    public void onHandleUdbWordRemoval(String word) {
        clearCurrentActiveWord();
        clearComposingTextAndSelection();
        if (this.mKoreanInput != null) {
            this.mKoreanInput.dlmDelete(word);
        }
        showNextWordPrediction();
    }

    @Override // com.nuance.swype.input.InputView
    public void closeDialogs() {
        dimissRemoveUdbWordDialog();
    }

    @Override // com.nuance.swype.input.InputView
    public void handleHardkeyCharKey(int primaryCode, int[] keyCodes, KeyEvent event, boolean shiftable) {
        if (primaryCode != 0) {
            boolean isBilingual = this.mIme.mCurrentInputLanguage instanceof BilingualLanguage;
            handleCharKey(null, primaryCode, null, 0L);
            if (shiftable && isBilingual && isKoreanDoubleJamoKey(primaryCode)) {
                setShiftState(Shift.ShiftState.OFF);
            }
        }
    }

    private boolean isKoreanDoubleJamoKey(int primaryCode) {
        return primaryCode == 12611 || primaryCode == 12617 || primaryCode == 12600 || primaryCode == 12594 || primaryCode == 12614 || primaryCode == 12626 || primaryCode == 12630;
    }

    @Override // com.nuance.swype.input.InputView
    public boolean handleHardkeyBackspace(int repeatedCount) {
        return handleBackspace(repeatedCount);
    }

    @Override // com.nuance.swype.input.InputView
    public boolean handleHardkeySpace(boolean quickPressed, int repeatedCount) {
        return handleSpace(quickPressed, repeatedCount);
    }

    @Override // com.nuance.swype.input.InputView
    public boolean handleHardkeyDelete(int repeatedCount) {
        InputConnection ic = getCurrentInputConnection();
        if (this.mKoreanInput == null || ic == null) {
            sendBackspace(repeatedCount);
        } else {
            if (this.suggestionCandidates != null && this.suggestionCandidates.count() > 0 && (!isComposingText() || !hasInputQueue())) {
                if (this.mInlineWord.length() > 0) {
                    clearCurrentInline(ic);
                }
                clearSuggestions();
            }
            if (!InputMethods.MULTITAP_INPUT_MODE.equals(this.mKeyboardSwitcher.getCurrentInputMode()) && this.suggestionCandidates == null && this.mTextInputPredictionOn && this.mRecaptureOn) {
                if (!TextUtils.isEmpty(ic.getTextAfterCursor(1, 0))) {
                    ic.beginBatchEdit();
                    ic.deleteSurroundingText(0, 1);
                    ic.endBatchEdit();
                }
            } else {
                flushCurrentActiveWord();
                CharSequence cSeqAfter = ic.getTextAfterCursor(1, 0);
                if (cSeqAfter != null && cSeqAfter.length() > 0) {
                    ic.beginBatchEdit();
                    ic.deleteSurroundingText(0, 1);
                    ic.endBatchEdit();
                }
                clearAllKeys();
                this.mKoreanInput.setContext(null);
                this.mEditState.backSpace();
            }
        }
        return true;
    }

    @Override // com.nuance.swype.input.InputView
    public boolean handleHardkeyEnter() {
        InputConnection ic = getCurrentInputConnection();
        if (this.mKoreanInput == null || ic == null) {
            sendKeyToApplication(66);
        } else if (this.suggestionCandidates == null || this.suggestionCandidates.source() == Candidates.Source.RECAPTURE || this.suggestionCandidates.source() == Candidates.Source.RECAPTURE_BY_TEXT_SELECTION) {
            sendKeyToApplication(66);
        } else {
            ic.beginBatchEdit();
            if (InputConnectionUtils.isComposingTextSelected(ic)) {
                clearSuggestions();
                clearAllKeys();
            } else if (composingWordCandidates() || (this.mIsUseHardkey && this.mInlineWord.length() > 0 && this.suggestionCandidates.source() == Candidates.Source.NEXT_WORD_PREDICTION)) {
                setDefaultWordType(StatisticsEnabledEditState.DefaultSelectionType.TAPPED_SPACE);
                processWordSelection(this.suggestionCandidates.getDefaultCandidate(), this.suggestionCandidates, false);
                updateShiftKeyState();
                updateMultitapDeadkey(true);
                ic.endBatchEdit();
            } else if (this.suggestionCandidates.source() != Candidates.Source.NEXT_WORD_PREDICTION) {
                clearSuggestions();
            }
            sendKeyToApplication(66);
            updateShiftKeyState();
            updateMultitapDeadkey(true);
            ic.endBatchEdit();
        }
        return true;
    }

    @Override // com.nuance.swype.input.InputView
    public boolean handleHardKeyCapsLock(boolean iscapslockon) {
        return true;
    }

    @Override // com.nuance.swype.input.InputView
    public boolean handleHardkeyShiftKey(Shift.ShiftState state) {
        return true;
    }

    @Override // com.nuance.swype.input.InputView
    public boolean handleHardKeyEscapeKey() {
        if (hasInputQueue()) {
            clearCurrentActiveWord();
            return false;
        }
        return false;
    }

    @Override // com.nuance.swype.input.InputView
    public boolean handleHardKeyDirectionKey(int keycode) {
        if (this.suggestionCandidates == null || this.suggestionCandidates.source() == Candidates.Source.RECAPTURE || this.suggestionCandidates.source() == Candidates.Source.RECAPTURE_BY_TEXT_SELECTION) {
            sendKeyToApplication(keycode);
            return true;
        }
        switch (keycode) {
            case 19:
                break;
            case 20:
                if (hasInputQueue()) {
                    selectDefaultSuggestion();
                    flushCurrentActiveWord();
                    return false;
                }
                if (isShowingCandidatesFor(Candidates.Source.NEXT_WORD_PREDICTION)) {
                    if (this.candidatesListView.getSelectedIndex() != -1 && this.mInlineWord.length() == 0) {
                        return moveHightlightToCenter(keycode);
                    }
                    clearCurrentActiveWord();
                    return false;
                }
                break;
            case 21:
            case 22:
                if (isShowingCandidatesFor(Candidates.Source.NEXT_WORD_PREDICTION)) {
                    if (this.candidatesListView.getSelectedIndex() != -1 && this.mInlineWord.length() != 0) {
                        return moveHighlightToNearCandidateForNWP(keycode);
                    }
                    clearSuggestions();
                    return false;
                }
                return moveHighlightToNearCandidate(keycode);
            default:
                return false;
        }
        if (hasInputQueue()) {
            selectDefaultSuggestion();
            flushCurrentActiveWord();
            return false;
        }
        if (!isShowingCandidatesFor(Candidates.Source.NEXT_WORD_PREDICTION)) {
            return false;
        }
        clearCurrentActiveWord();
        return true;
    }

    private boolean moveHightlightToCenter(int keyCode) {
        if (this.candidatesListView == null || this.suggestionCandidates == null || this.suggestionCandidates.count() <= 0) {
            sendKeyToApplication(keyCode);
            return false;
        }
        boolean isDown = false;
        if (keyCode == 20) {
            isDown = true;
        }
        int wordCandidatesCount = this.suggestionCandidates.count();
        if (isDown) {
            int index = this.candidatesListView.getCenterCandidateIndex();
            if (index == -1) {
                return true;
            }
            if (index >= 0 && index < wordCandidatesCount) {
                this.suggestionCandidates.setDefaultIndex(index);
                this.candidatesListView.setHardwareKeyboardUsed(true);
                this.candidatesListView.touchWord(index, this.suggestionCandidates.get(index));
                syncCandidateDisplayStyleToMode();
            }
        }
        if ((this.suggestionCandidates.source() == Candidates.Source.RECAPTURE_BY_TEXT_SELECTION && !hasInputQueue()) || this.suggestionCandidates.source() != Candidates.Source.NEXT_WORD_PREDICTION || hasInputQueue()) {
            return true;
        }
        WordCandidate wordcandidate = this.candidatesListView.getCurrentSelectedCandidate();
        this.mInlineWord.clear();
        this.mInlineWord.clearSpans();
        this.mInlineWord.append((CharSequence) wordcandidate.toString());
        UnderlineSpan span = new UnderlineSpan();
        this.mInlineWord.setSpan(span, 0, this.mInlineWord.length(), R.styleable.ThemeTemplate_emojiCategoryIconSmiles);
        this.mInlineWord.setSpan(span, 0, this.mInlineWord.length(), R.styleable.ThemeTemplate_emojiCategoryIconSmiles);
        InputConnection ic = getCurrentInputConnection();
        if (ic == null) {
            return true;
        }
        ic.beginBatchEdit();
        ic.setComposingText(this.mInlineWord, 1);
        ic.endBatchEdit();
        return true;
    }

    private boolean moveHighlightToNearCandidateForNWP(int keyCode) {
        int index;
        if (this.candidatesListView == null || this.suggestionCandidates == null || this.suggestionCandidates.count() <= 0) {
            sendKeyToApplication(keyCode);
            return false;
        }
        int wordCandidatesCount = this.suggestionCandidates.count();
        boolean isRight = false;
        if (keyCode == 22) {
            isRight = true;
        }
        int index2 = this.candidatesListView.getSelectedIndex();
        if (isRight) {
            index = index2 + 1;
        } else {
            index = index2 - 1;
        }
        if (isRight && index >= wordCandidatesCount) {
            index = wordCandidatesCount - 1;
        } else if (!isRight && index < 0) {
            index = 0;
        }
        this.suggestionCandidates.setDefaultIndex(index);
        this.candidatesListView.setHardwareKeyboardUsed(true);
        this.candidatesListView.touchWord(index, this.suggestionCandidates.get(index));
        syncCandidateDisplayStyleToMode();
        if (isRight && this.candidatesListView.isKeyOutofRightBound(index)) {
            this.candidatesListView.scrollNext();
        } else if (!isRight && this.candidatesListView.isKeyOutofLeftBound(index)) {
            this.candidatesListView.scrollPrev();
        }
        if ((this.suggestionCandidates.source() == Candidates.Source.RECAPTURE_BY_TEXT_SELECTION && !hasInputQueue()) || this.suggestionCandidates.source() != Candidates.Source.NEXT_WORD_PREDICTION || hasInputQueue()) {
            return true;
        }
        WordCandidate wordcandidate = this.candidatesListView.getCurrentSelectedCandidate();
        this.mInlineWord.clear();
        this.mInlineWord.clearSpans();
        this.mInlineWord.append((CharSequence) wordcandidate.toString());
        UnderlineSpan span = new UnderlineSpan();
        this.mInlineWord.setSpan(span, 0, this.mInlineWord.length(), R.styleable.ThemeTemplate_emojiCategoryIconSmiles);
        this.mInlineWord.setSpan(span, 0, this.mInlineWord.length(), R.styleable.ThemeTemplate_emojiCategoryIconSmiles);
        InputConnection ic = getCurrentInputConnection();
        if (ic == null) {
            return true;
        }
        ic.beginBatchEdit();
        ic.setComposingText(this.mInlineWord, 1);
        ic.endBatchEdit();
        return true;
    }

    private boolean moveHighlightToNearCandidate(int keyCode) {
        int index;
        if (this.candidatesListView == null || this.suggestionCandidates == null || this.suggestionCandidates.count() <= 0) {
            sendKeyToApplication(keyCode);
            return false;
        }
        int wordCandidatesCount = this.suggestionCandidates.count();
        boolean isRight = false;
        if (keyCode == 22) {
            isRight = true;
        }
        int index2 = this.candidatesListView.getSelectedIndex();
        if (isRight) {
            index = index2 + 1;
        } else {
            index = index2 - 1;
        }
        if (isRight && index >= wordCandidatesCount) {
            index = wordCandidatesCount - 1;
        } else if (!isRight && index < 0) {
            index = 0;
        }
        this.suggestionCandidates.setDefaultIndex(index);
        this.candidatesListView.touchWord(index, this.suggestionCandidates.get(index));
        syncCandidateDisplayStyleToMode();
        if (isRight && this.candidatesListView.isKeyOutofRightBound(index)) {
            this.candidatesListView.scrollNext();
        } else if (!isRight && this.candidatesListView.isKeyOutofLeftBound(index)) {
            this.candidatesListView.scrollPrev();
        }
        if ((this.suggestionCandidates.source() == Candidates.Source.RECAPTURE_BY_TEXT_SELECTION && !hasInputQueue()) || !hasInputQueue()) {
            return true;
        }
        SpannableStringBuilder inlineword = new SpannableStringBuilder();
        UnderlineSpan span = new UnderlineSpan();
        WordCandidate wordcandidate = this.candidatesListView.getCurrentSelectedCandidate();
        inlineword.clear();
        inlineword.clearSpans();
        inlineword.append((CharSequence) wordcandidate.toString());
        inlineword.setSpan(span, 0, inlineword.length(), R.styleable.ThemeTemplate_emojiCategoryIconSmiles);
        inlineword.setSpan(span, 0, inlineword.length(), R.styleable.ThemeTemplate_emojiCategoryIconSmiles);
        InputConnection ic = getCurrentInputConnection();
        if (ic == null) {
            return true;
        }
        ic.beginBatchEdit();
        InputConnectionUtils.setComposingText(ic, inlineword);
        ic.endBatchEdit();
        return true;
    }

    @Override // com.nuance.swype.input.InputView
    public boolean handleHardKeyHomeKey() {
        if (this.candidatesListView == null || this.suggestionCandidates == null || this.suggestionCandidates.count() <= 0) {
            sendKeyToApplication(122);
            return false;
        }
        if (this.suggestionCandidates.source() == Candidates.Source.NEXT_WORD_PREDICTION || this.suggestionCandidates.source() == Candidates.Source.RECAPTURE || this.suggestionCandidates.source() == Candidates.Source.RECAPTURE_BY_TEXT_SELECTION) {
            sendKeyToApplication(122);
            return true;
        }
        if (this.candidatesListView.getSelectedIndex() == 0) {
            return true;
        }
        this.suggestionCandidates.setDefaultIndex(0);
        this.candidatesListView.touchWord(0, this.suggestionCandidates.get(0));
        this.candidatesListView.scrollHead();
        syncCandidateDisplayStyleToMode();
        if (!hasInputQueue()) {
            return true;
        }
        SpannableStringBuilder inlineword = new SpannableStringBuilder();
        UnderlineSpan span = new UnderlineSpan();
        WordCandidate wordcandidate = this.candidatesListView.getCurrentSelectedCandidate();
        inlineword.clear();
        inlineword.clearSpans();
        inlineword.append((CharSequence) wordcandidate.toString());
        inlineword.setSpan(span, 0, inlineword.length(), R.styleable.ThemeTemplate_emojiCategoryIconSmiles);
        inlineword.setSpan(span, 0, inlineword.length(), R.styleable.ThemeTemplate_emojiCategoryIconSmiles);
        InputConnection ic = getCurrentInputConnection();
        if (ic == null) {
            return true;
        }
        ic.beginBatchEdit();
        InputConnectionUtils.setComposingText(ic, inlineword);
        ic.endBatchEdit();
        return true;
    }

    @Override // com.nuance.swype.input.InputView
    public boolean handleHardKeyEndKey() {
        if (this.candidatesListView == null || this.suggestionCandidates == null || this.suggestionCandidates.count() <= 0) {
            sendKeyToApplication(123);
            return false;
        }
        if (this.suggestionCandidates.source() == Candidates.Source.NEXT_WORD_PREDICTION || this.suggestionCandidates.source() == Candidates.Source.RECAPTURE || this.suggestionCandidates.source() == Candidates.Source.RECAPTURE_BY_TEXT_SELECTION) {
            sendKeyToApplication(123);
            return true;
        }
        int wordCandidatesCount = this.suggestionCandidates.count() - 1;
        if (this.candidatesListView.getSelectedIndex() >= wordCandidatesCount) {
            return true;
        }
        this.suggestionCandidates.setDefaultIndex(wordCandidatesCount);
        this.candidatesListView.touchWord(wordCandidatesCount, this.suggestionCandidates.get(wordCandidatesCount));
        this.candidatesListView.scrollEnd();
        syncCandidateDisplayStyleToMode();
        if (!hasInputQueue()) {
            return true;
        }
        SpannableStringBuilder inlineword = new SpannableStringBuilder();
        UnderlineSpan span = new UnderlineSpan();
        WordCandidate wordcandidate = this.candidatesListView.getCurrentSelectedCandidate();
        inlineword.clear();
        inlineword.clearSpans();
        inlineword.append((CharSequence) wordcandidate.toString());
        inlineword.setSpan(span, 0, inlineword.length(), R.styleable.ThemeTemplate_emojiCategoryIconSmiles);
        inlineword.setSpan(span, 0, inlineword.length(), R.styleable.ThemeTemplate_emojiCategoryIconSmiles);
        InputConnection ic = getCurrentInputConnection();
        if (ic == null) {
            return true;
        }
        ic.beginBatchEdit();
        InputConnectionUtils.setComposingText(ic, inlineword);
        ic.endBatchEdit();
        return true;
    }

    private boolean hasInputQueue() {
        return this.mKoreanInput != null && this.mKoreanInput.getKeyCount() > 0 && this.mInlineWord != null && this.mInlineWord.length() > 0;
    }

    @Override // com.nuance.swype.input.KeyboardViewEx
    public void showHardKeyPopupKeyboard(int keyCode) {
        super.showHardKeyPopupKeyboard(keyCode);
        if (this.mInlineWord != null && this.mInlineWord.length() > 0 && isComposingText()) {
            InputConnection ic = getCurrentInputConnection();
            if (this.mKoreanInput != null && ic != null && this.mKoreanInput.getKeyCount() > 0) {
                this.activeHangul.setLength(0);
                if (this.mKoreanInput.getKeyCount() > 0) {
                    updateSuggestions(Candidates.Source.TAP);
                } else {
                    clearSuggestions();
                }
            }
        }
    }

    @Override // com.nuance.swype.input.InputView
    public void onSpeechViewDismissed() {
        super.onSpeechViewDismissed();
        if (isEmptyCandidateList()) {
            showNextWordPrediction();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.InputView
    public void preUpdateSpeechText() {
        selectDefaultSuggestion();
    }

    @Override // com.nuance.swype.input.InputView
    public boolean isInputSessionStarted() {
        return !this.mInputViewHandler.hasMessages(15);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.InputView
    public CharSequence getContextBuffer() {
        return getTextBufferCursor(17);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.InputView
    public void setLanguage(XT9CoreInput coreInput) {
        XT9CoreInput.XT9InputMode xt9InputMode;
        int primaryLanguageId = this.mCurrentInputLanguage.getCoreLanguageId();
        int secondaryLanguageId = 0;
        if (this.mKeyboardLayoutId == 1536 || this.mKeyboardLayoutId == 1792) {
            primaryLanguageId = InputMethods.Language.KOREAN_CJI_LANGUAGEID;
            xt9InputMode = XT9CoreInput.XT9InputMode.CHUNJIIN;
        } else if (this.mKeyboardLayoutId == 1808) {
            xt9InputMode = XT9CoreInput.XT9InputMode.VEGA;
        } else if (this.mKeyboardLayoutId == 1824) {
            xt9InputMode = XT9CoreInput.XT9InputMode.NARAGATGUL;
        } else {
            xt9InputMode = XT9CoreInput.XT9InputMode.DEFAULT;
            if (this.mCurrentInputLanguage instanceof BilingualLanguage) {
                secondaryLanguageId = 265;
            }
        }
        XT9Status status = coreInput.setLanguage(primaryLanguageId, secondaryLanguageId, xt9InputMode);
        if (status == XT9Status.ET9STATUS_DB_CORE_INCOMP || status == XT9Status.ET9STATUS_LDB_VERSION_ERROR) {
            DatabaseConfig.removeIncompatibleDBFiles(getContext(), this.mCurrentInputLanguage.mEnglishName);
            XT9Status status2 = coreInput.setLanguage(primaryLanguageId, secondaryLanguageId);
            if (status2 == XT9Status.ET9STATUS_DB_CORE_INCOMP || status2 == XT9Status.ET9STATUS_LDB_VERSION_ERROR) {
                this.isLDBCompatible = false;
                promptUserToUpdateLanguage();
                toggleKeyboard(false);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleFunctionKey(int keyCode, boolean quickPress, int repeatCount) {
        if (shouldHandleKeyViaIME(keyCode) || !handleKey(keyCode, quickPress, repeatCount)) {
            if (KeyboardEx.isChangeKeyboardLayerKey(keyCode) || !handleGesture(keyCode)) {
                this.mIme.onKey(null, keyCode, null, null, 0L);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class InputContextRequestHandler implements InputContextRequestDispatcher.InputContextRequestHandler {
        boolean autoAccept;

        private InputContextRequestHandler() {
            this.autoAccept = true;
        }

        private char[] getTextBeforeCursor(int maxBufferLen) {
            InputConnection ic = KoreanInputView.this.getCurrentInputConnection();
            CharSequence textBeforeCursor = ic != null ? ic.getTextBeforeCursor(maxBufferLen, 0) : null;
            if (textBeforeCursor != null) {
                return textBeforeCursor.toString().toCharArray();
            }
            return null;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean addExplicitSymbol(char[] symbols, Shift.ShiftState shiftState) {
            this.autoAccept = false;
            boolean processed = KoreanInputView.this.mKoreanInput.addExplicit(symbols, symbols.length, shiftState);
            this.autoAccept = true;
            return processed;
        }

        @Override // com.nuance.swype.input.keyboard.InputContextRequestDispatcher.InputContextRequestHandler
        public char[] getContextBuffer(int maxBufferLen) {
            return getTextBeforeCursor(maxBufferLen);
        }

        @Override // com.nuance.swype.input.keyboard.InputContextRequestDispatcher.InputContextRequestHandler
        public char[] getAutoCapitalizationTextBuffer(int maxBufferLen) {
            return getTextBeforeCursor(maxBufferLen);
        }

        @Override // com.nuance.swype.input.keyboard.InputContextRequestDispatcher.InputContextRequestHandler
        public boolean autoAccept(boolean addSeparator) {
            StringBuffer selectWord = new StringBuffer();
            if (KoreanInputView.this.suggestionCandidates != null && this.autoAccept && KoreanInputView.this.suggestionCandidates.source() != Candidates.Source.NEXT_WORD_PREDICTION && KoreanInputView.this.suggestionCandidates.source() != Candidates.Source.TOOL_TIP) {
                KoreanInputView.this.getCurrentInputConnection().beginBatchEdit();
                selectWord.append(KoreanInputView.this.suggestionCandidates.getDefaultCandidate().word());
                if (addSeparator && KoreanInputView.this.mAutoSpace) {
                    selectWord.append(XMLResultsHandler.SEP_SPACE);
                }
                KoreanInputView.this.getCurrentInputConnection().commitText(selectWord, 1);
                KoreanInputView.this.mKoreanInput.wordSelected(KoreanInputView.this.suggestionCandidates.getDefaultCandidate().id(), false);
                KoreanInputView.this.clearSuggestions();
                KoreanInputView.this.getCurrentInputConnection().endBatchEdit();
            }
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class TouchKeyActionHandler implements KeyboardTouchEventDispatcher.TouchKeyActionHandler {
        final char[] functionKey;
        private final SparseIntArray keyIndices;
        private int multiTapKeyIndex;
        boolean touchCanceled;

        private TouchKeyActionHandler() {
            this.keyIndices = new SparseIntArray();
            this.functionKey = new char[1];
            this.multiTapKeyIndex = -1;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean processStoredTouch() {
            return KoreanInputView.this.mKoreanInput.processStoredTouch(-1, this.functionKey);
        }

        @Override // com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher.TouchKeyActionHandler
        public void touchStarted(int pointerId, int keyIndex, KeyType keyType, float x, float y, int eventTime) {
            KoreanInputView.this.pressKey(KoreanInputView.this.mKeys, keyIndex);
            KoreanInputView.this.setKeyState(keyIndex, KeyboardViewEx.ShowKeyState.Pressed);
            KoreanInputView.this.showPreviewKey(keyIndex, pointerId);
            KoreanInputView.this.resetTrace(pointerId);
            this.keyIndices.put(pointerId, keyIndex);
            this.touchCanceled = false;
        }

        @Override // com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher.TouchKeyActionHandler
        public void touchMoved(int pointerId, int keyIndex, float[] xcoords, float[] ycoords, int[] times, boolean canBeTraced) {
            if (canBeTraced || keyIndex != this.keyIndices.get(pointerId)) {
                KoreanInputView.this.releaseKey(KoreanInputView.this.mKeys, this.keyIndices.get(pointerId), false);
                KoreanInputView.this.hideKeyPreview(pointerId);
                KoreanInputView.this.setKeyState(-1, KeyboardViewEx.ShowKeyState.Released);
                this.keyIndices.put(pointerId, keyIndex);
            }
            KoreanInputView.this.onTouchMoved(pointerId, xcoords, ycoords, times, canBeTraced, keyIndex);
        }

        @Override // com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher.TouchKeyActionHandler
        public void touchEnded(int pointerId, int keyIndex, KeyType keyType, boolean isTraced, boolean quickPressed, float x, float y, int eventTime) {
            KoreanInputView.this.releaseKey(KoreanInputView.this.mKeys, this.keyIndices.get(pointerId, keyIndex), true);
            KoreanInputView.this.hideKeyPreview(pointerId);
            KoreanInputView.this.setKeyState(-1, KeyboardViewEx.ShowKeyState.Released);
            if (!this.touchCanceled) {
                if (quickPressed && keyType.isFunctionKey()) {
                    quickPress(pointerId, keyIndex, keyType);
                } else {
                    KeyboardEx.Key key = KoreanInputView.this.getKey(keyIndex);
                    if (isTraced) {
                        KoreanInputView.this.handleTrace(null);
                        KoreanInputView.this.updateUIShiftState(KoreanInputView.this.mKoreanInput.getShiftState());
                    } else if (keyType.isFunctionKey()) {
                        if (!KoreanInputView.this.isMultitapping() || key.codes[0] != 32) {
                            KoreanInputView.this.handleFunctionKey(key.codes[0], false, 0);
                            if (!KeyboardEx.isShiftKey(key.codes[0])) {
                                KoreanInputView.this.mKoreanInput.setShiftState(Shift.ShiftState.OFF);
                            }
                        }
                        if (KoreanInputView.this.isMultitapping()) {
                            KoreanInputView.this.mKoreanInput.multiTapTimeOut();
                            KoreanInputView.this.touchKeyActionHandler.multiTapTimerTimeOut();
                        }
                    } else {
                        if (keyType.isString()) {
                            if (KoreanInputView.this.isMultitapping()) {
                                KoreanInputView.this.mKoreanInput.multiTapTimeOut();
                            }
                            KoreanInputView.this.onText(key.text, 0L);
                        } else {
                            if (KoreanInputView.this.isMultitapping() && this.multiTapKeyIndex != -1 && this.multiTapKeyIndex != keyIndex && !KoreanInputView.this.mTextInputPredictionOn) {
                                KoreanInputView.this.mKoreanInput.multiTapTimeOut();
                                KoreanInputView.this.onMultitapTimeout();
                            }
                            if (KoreanInputView.this.mKeyboardSwitcher.isAlphabetMode()) {
                                KoreanInputView.this.handleCharKey(new Point((int) x, (int) y), KoreanInputView.this.getKeycode(key), null, 0L);
                            } else {
                                KoreanInputView.this.handleCharKey(null, KoreanInputView.this.getKeycode(key), null, 0L);
                            }
                            if (!KoreanInputView.this.isMultitapping()) {
                                keyIndex = -1;
                            }
                            this.multiTapKeyIndex = keyIndex;
                        }
                        KoreanInputView.this.mKoreanInput.setShiftState(Shift.ShiftState.OFF);
                    }
                    KoreanInputView.this.updateUIShiftState(KoreanInputView.this.mKoreanInput.getShiftState());
                }
                KoreanInputView.this.mLastInput = isTraced ? 2 : 1;
            }
            this.keyIndices.put(pointerId, -1);
            if (isTraced) {
                KoreanInputView.this.finishTrace(pointerId);
            }
        }

        @Override // com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher.TouchKeyActionHandler
        public void touchCanceled(int pointerId, int keyIndex) {
            this.touchCanceled = true;
            KoreanInputView.this.resetTrace(pointerId);
            if (keyIndex == -1) {
                if (this.keyIndices.get(pointerId, -1) != -1) {
                    KoreanInputView.this.releaseKey(KoreanInputView.this.mKeys, this.keyIndices.get(pointerId), true);
                }
                KoreanInputView.this.hideKeyPreview(pointerId);
                KoreanInputView.this.setKeyState(-1, KeyboardViewEx.ShowKeyState.Released);
            }
        }

        @Override // com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher.TouchKeyActionHandler
        public boolean touchHeld(int pointerId, int keyIndex, KeyType keyType) {
            return KoreanInputView.this.onShortPress(KoreanInputView.this.getKey(keyIndex), keyIndex, pointerId);
        }

        @Override // com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher.TouchKeyActionHandler
        public boolean touchHeldRepeat(int pointerId, int keyIndex, KeyType keyType, int repeatCount) {
            if (KoreanInputView.this.mIme == null || !keyType.isFunctionKey()) {
                return false;
            }
            KeyboardEx.Key key = KoreanInputView.this.getKey(keyIndex);
            if (key.codes[0] == 8) {
                return KoreanInputView.this.handleBackspace(repeatCount);
            }
            if (!KeyboardEx.isArrowKeys(key.codes[0])) {
                return false;
            }
            KoreanInputView.this.handleFunctionKey(key.codes[0], false, repeatCount);
            return true;
        }

        public void quickPress(int pointerId, int keyIndex, KeyType keyType) {
            if (keyType.isFunctionKey()) {
                KeyboardEx.Key key = KoreanInputView.this.getKey(keyIndex);
                KoreanInputView.this.handleFunctionKey(key.codes[0], true, 0);
            }
        }

        @Override // com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher.TouchKeyActionHandler
        public void touchHeldMove(int pointerId, int keyIndex, float[] xcoords, float[] ycoords, int[] times) {
            KoreanInputView.this.onTouchHeldMoved(pointerId, xcoords, ycoords, times);
        }

        @Override // com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher.TouchKeyActionHandler
        public void touchHeldEnded(int pointerId, int keyIndex, KeyType keyType) {
            if (keyIndex != -1) {
                KoreanInputView.this.onTouchHeldEnded(pointerId, KoreanInputView.this.getKey(keyIndex));
                KoreanInputView.this.releaseKey(KoreanInputView.this.mKeys, keyIndex, true);
            }
            KoreanInputView.this.hideKeyPreview(pointerId);
            KoreanInputView.this.setKeyState(-1, KeyboardViewEx.ShowKeyState.Released);
        }

        @Override // com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher.TouchKeyActionHandler
        public void touchHelpRepeatEnded(int pointerId, int keyIndex, KeyType keyType) {
            KoreanInputView.this.releaseKey(KoreanInputView.this.mKeys, keyIndex, true);
            KoreanInputView.this.mIme.releaseRepeatKey();
            KoreanInputView.this.hideKeyPreview(pointerId);
            KoreanInputView.this.setKeyState(-1, KeyboardViewEx.ShowKeyState.Released);
        }

        @Override // com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher.TouchKeyActionHandler
        public void multiTapTimerTimeoutActive() {
            KoreanInputView.this.mInMultiTap = true;
        }

        @Override // com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher.TouchKeyActionHandler
        public void multiTapTimerTimeOut() {
            KoreanInputView.this.onMultitapTimeout();
            KoreanInputView.this.mInMultiTap = false;
            this.multiTapKeyIndex = -1;
        }

        @Override // com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher.TouchKeyActionHandler
        public void handleKeyboardShiftState(float x) {
        }
    }
}
