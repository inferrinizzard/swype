package com.nuance.swype.input;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputConnection;
import com.nuance.android.compat.UserManagerCompat;
import com.nuance.android.util.WeakReferenceHandler;
import com.nuance.dlm.ACCoreInputDLM;
import com.nuance.input.swypecorelib.Candidates;
import com.nuance.input.swypecorelib.KeyType;
import com.nuance.input.swypecorelib.Shift;
import com.nuance.input.swypecorelib.WordCandidate;
import com.nuance.input.swypecorelib.XT9CoreAlphaInput;
import com.nuance.input.swypecorelib.XT9CoreInput;
import com.nuance.input.swypecorelib.XT9Status;
import com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.xmlResults.XMLResultsHandler;
import com.nuance.speech.CustomWordSynchronizer;
import com.nuance.speech.Dictation;
import com.nuance.swype.connect.Connect;
import com.nuance.swype.input.CandidatesListView;
import com.nuance.swype.input.InputMethods;
import com.nuance.swype.input.KeyboardEx;
import com.nuance.swype.input.KeyboardViewEx;
import com.nuance.swype.input.accessibility.AccessibilityNotification;
import com.nuance.swype.input.accessibility.KeyboardModel;
import com.nuance.swype.input.appspecific.AppSpecificInputConnection;
import com.nuance.swype.input.emoji.EmojiLoader;
import com.nuance.swype.input.hardkey.HardKeyboardManager;
import com.nuance.swype.input.keyboard.CandidatesBuilderHandler;
import com.nuance.swype.input.keyboard.InputContextRequestDispatcher;
import com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher;
import com.nuance.swype.input.keyboard.WordRecaptureWrapper;
import com.nuance.swype.input.udb.UserDictionaryIterator;
import com.nuance.swype.plugin.TypedArrayWrapper;
import com.nuance.swype.stats.StatisticsEnabledEditState;
import com.nuance.swype.stats.UsageManager;
import com.nuance.swype.usagedata.UsageData;
import com.nuance.swype.usagedata.WordCandidateDataPoint;
import com.nuance.swype.usagedata.WordCandidateDataPointWriter;
import com.nuance.swype.util.AdsUtil;
import com.nuance.swype.util.CharacterUtilities;
import com.nuance.swype.util.InputConnectionUtils;
import com.nuance.swype.util.LogManager;
import com.nuance.swype.util.LruWordCache;
import com.nuance.swype.util.WordDecorator;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@SuppressLint({"DefaultLocale"})
/* loaded from: classes.dex */
public class AlphaInputView extends InputView {
    private static final LogManager.Trace traceLog = LogManager.getTrace();
    private final Handler.Callback alphaInputViewCallback;
    private LruWordCache autoCorrectOverrideWords;
    private AutospaceHandler autospaceHandler;
    private BackspaceRevertHandler backspaceRevertHandler;
    private CandidatesBuilderHandler.CandidatesBuilderResult candidateBuilderResult;
    CandidatesBuilderHandler candidatesBuilderHandler;
    private CharacterUtilities charUtils;
    private final StringBuilder exactType;
    InputContextRequestHandler inputContextRequestHandler;
    private boolean isAddToDictionaryTipHighlighted;
    private boolean isLongPressed;
    private boolean isManualShift;
    private boolean isOrientationChangedOnce;
    private boolean isTelexMode;
    private XT9CoreAlphaInput mAlphaInput;
    private final Handler mAlphaInputViewHandler;
    private BackgroundColorSpan mBKMultiTappingCharSpan;
    private BackgroundColorSpan mBKWordErrorSpan;
    private ForegroundColorSpan mFGMultiTappingCharSpan;
    private ForegroundColorSpan mFGWordErrorSpan;
    private final SpannableStringBuilder mInlineWord;
    private LoggingDLMWipeEvent mLoggingDLMWipeEvent;
    private Shift.ShiftState mPreTraceShiftedState;
    private final UnderlineSpan mWordComposeSpan;
    private OOVWordHistory oovWordHistory;
    private PromptForAddingOOVCandidate promptForAddingOOVCandidate;
    private final RecaptureEditWord recaptureEditWord;
    private int shiftGestureOffset;
    private boolean shiftGestureOn;
    private String terminalPunct;
    int topOfGestureMargin;
    private KeyboardTouchEventDispatcher.TouchKeyActionHandler touchKeyActionHandler;
    private final UpdateSelectionCallback updateSelectionCallback;
    private WordCandidateDataPoint wordCandidateDataPoint;
    private WordCandidateDataPointWriter wordCandidateDataPointWriter;
    WordRecaptureWrapper wordRecaptureWrapper;

    /* loaded from: classes.dex */
    interface UpdateSelectionCallback {
        void updateSelection(boolean z, int i, int i2, InputConnection inputConnection);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.InputView
    public void removeAllMessages() {
        this.mAlphaInputViewHandler.removeCallbacksAndMessages(null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showStartOfWordCandidateList() {
        String lastWord = IME.getLastSavedActiveWordAndSet();
        if (lastWord != null && lastWord.length() > 0) {
            char[] chArray = new char[1];
            for (int iCharPos = 0; iCharPos < lastWord.length(); iCharPos++) {
                chArray[0] = lastWord.charAt(iCharPos);
                if (this.mAlphaInput.isSymbolUpperCase(chArray[0])) {
                    this.mAlphaInput.addExplicit(chArray, 1, Shift.ShiftState.ON);
                } else {
                    this.mAlphaInput.addExplicit(chArray, 1, Shift.ShiftState.OFF);
                }
            }
            InputConnection ic = getCurrentInputConnection();
            if (ic != null) {
                displayExactTypeAsInline(null, ic);
            }
            updateSuggestions(Candidates.Source.TAP);
            setShiftState(Shift.ShiftState.OFF);
            return;
        }
        if (IME.getLastShownCandidatesSource() == Candidates.Source.NEXT_WORD_PREDICTION) {
            updateSuggestions(Candidates.Source.NEXT_WORD_PREDICTION);
        }
    }

    private void removeToastMsg(int msg) {
        QuickToast.hide();
        this.mAlphaInputViewHandler.removeMessages(msg);
    }

    private void postToastMsg(int msg) {
        removeToastMsg(msg);
        this.mAlphaInputViewHandler.sendEmptyMessageDelayed(msg, 250L);
    }

    @Override // com.nuance.swype.input.InputView
    public void create(IME ime, XT9CoreInput xt9coreinput, SpeechWrapper speechWrapper) {
        super.create(ime, xt9coreinput, speechWrapper);
        this.mAlphaInput = (XT9CoreAlphaInput) xt9coreinput;
        createCandidatesBuilderHandler();
        IMEApplication imeApp = IMEApplication.from(getContext());
        this.charUtils = imeApp.getCharacterUtilities();
        this.mAlphaInput.setCandidateFactory(new CandidateFactory(this));
        this.mAlphaInput.setWordRecaptureCallback(this.recaptureEditWord);
        this.mAlphaInput.setWordQuarantineLevel(1, 1, 1);
        this.mKeyboardSwitcher = new KeyboardSwitcher(this.mIme, this.mAlphaInput);
        this.mKeyboardSwitcher.setInputView(this);
        setOnKeyboardActionListener(this.mIme);
        readStyles(getContext());
        this.autospaceHandler = new AutospaceHandler(this.mIme);
        this.shiftGestureOffset = (int) getResources().getDimension(R.dimen.candidates_list_height);
        this.topOfGestureMargin = (int) getResources().getDimension(R.dimen.shift_gesture_margin_top);
        this.mAlphaInput.setShiftGestureMargin(this.topOfGestureMargin);
        log.d("acAlpha create");
        ACCoreInputDLM.initializeACAlphaInput(Connect.from(getContext()));
        int acOverrideCacheSize = getResources().getInteger(R.integer.ac_override_cache_size);
        if (acOverrideCacheSize > 0) {
            this.autoCorrectOverrideWords = new LruWordCache(acOverrideCacheSize);
        }
        this.promptForAddingOOVCandidate = new PromptForAddingOOVCandidate();
        this.mLoggingDLMWipeEvent = new LoggingDLMWipeEvent();
        this.mLoggingDLMWipeEvent.enableLoggingDLMWipeEvent();
        this.wordCandidateDataPointWriter = new WordCandidateDataPointWriter(UsageManager.getKeyboardUsageScribe(getContext()));
        this.wordCandidateDataPoint = new WordCandidateDataPoint(this.mAlphaInput, getContext(), Locale.getDefault().getCountry(), getResources().getDisplayMetrics());
    }

    @Override // com.nuance.swype.input.InputView
    public void onCancelNonEditStateRecaptureViaCharKey(char ch) {
        handleBackSpaceRevertCancelWordRecapture(ch);
    }

    @Override // com.nuance.swype.input.InputView
    protected BackspaceRevertHandler getBackspaceRevertHandler() {
        return this.backspaceRevertHandler;
    }

    @Override // com.nuance.swype.input.InputView
    public void destroy() {
        super.destroy();
        setOnTouchListener(null);
        setOnKeyboardActionListener((IME) null);
        this.mLoggingDLMWipeEvent.disableLoggingDLMWipeEvent();
        this.mLoggingDLMWipeEvent = null;
        ACCoreInputDLM.destroyACAlphaInput();
        this.mKeyboardSwitcher = null;
        this.terminalPunct = null;
        this.promptForAddingOOVCandidate = null;
        this.mAlphaInput.registerExplicitLearningApprovalCallback(null);
        dimissRemoveUdbWordDialog();
        setOnTouchListener(null);
        this.xt9coreinput.setInputContextRequestListener(null);
        if (this.candidatesBuilderHandler != null) {
            if (this.candidatesBuilderHandler.hasPendingBuild()) {
                this.candidatesBuilderHandler.removePendingBuild();
            }
            this.candidatesBuilderHandler = null;
        }
    }

    @Override // com.nuance.swype.input.InputView
    public View createCandidatesView(CandidatesListView.CandidateListener onSelectListener) {
        if (this.mAlphaInput == null) {
            return null;
        }
        return super.createCandidatesView(onSelectListener);
    }

    public boolean isExactCandidateNewWord() {
        return this.suggestionCandidates != null && this.suggestionCandidates.count() > 0 && this.suggestionCandidates.getExactCandidate().source() == WordCandidate.Source.WORD_SOURCE_NEW_WORD;
    }

    @Override // com.nuance.swype.input.InputView
    public void clearCurrentActiveWord() {
        clearCurrentInline(getCurrentInputConnection());
        clearSuggestions();
    }

    @Override // com.nuance.swype.input.InputView
    public void updateCandidatesView() {
        View cv = getWordCandidateListContainer();
        if (cv != null) {
            WordListViewContainer wcl = (WordListViewContainer) cv;
            if (AdsUtil.sAdsSupported) {
                wcl.setupAdsView();
            }
            wcl.updateView();
            setHindiTransliterationMode(wcl);
        }
    }

    public void setHindiTransliterationMode(WordListViewContainer wcl) {
        IME ime = IMEApplication.from(getContext()).getIME();
        if (ime == null) {
            log.e("setHindiTransliterationMode : ime reference is null");
        }
        if (ime != null && ime.mCurrentInputLanguage.isHindiLanguage() && !ime.mCurrentInputLanguage.isBilingualLanguage()) {
            wcl.showTransliterationToggleButton(ime);
        } else {
            wcl.hideTransliterationToggleButton();
        }
    }

    @Override // com.nuance.swype.input.InputView
    protected void updateSuggestionsEmoji(CandidatesListView.Format format) {
        if (this.mEnableEmojiChoiceList && this.emojiCandidatesListView != null) {
            Candidates candidates = this.xt9coreinput.getCandidatesEmoji();
            if (candidates.count() > 0) {
                this.emojiCandidatesHolder.setVisibility(0);
                if (this.suggestionCandidates != null && this.suggestionCandidates.source() == Candidates.Source.RECAPTURE_BY_TEXT_SELECTION) {
                    candidates.setSource(this.suggestionCandidates.source());
                }
                this.emojiCandidatesListView.setSuggestions(candidates, format);
                log.d("updateSuggestionsEmoji: emoji to show, showing emojiCandidatesHolder");
                return;
            }
            this.emojiCandidatesHolder.setVisibility(8);
            log.d("updateSuggestionsEmoji: no emoji to show, hiding emojiCandidatesHolder");
        }
    }

    @Override // com.nuance.swype.input.InputView
    public void startInput(InputFieldInfo inputFieldInfo, boolean restarting) {
        if (this.mAlphaInput != null) {
            if (restarting) {
                clearPendingSuggestionsUpdate();
            } else if (this.autoCorrectOverrideWords != null) {
                this.autoCorrectOverrideWords.words.clear();
            }
            if (this.candidatesListView != null) {
                this.candidatesListView.updateRtlStatus(this.mCurrentInputLanguage.locale.getLanguage());
                this.candidatesListView.updateCandidatesSize();
            }
            if (this.emojiCandidatesListView != null) {
                this.emojiCandidatesListView.updateRtlStatus(this.mCurrentInputLanguage.locale.getLanguage());
                this.emojiCandidatesListView.updateCandidatesSize();
            }
            super.startInput(inputFieldInfo, restarting);
            this.keyboardTouchEventDispatcher.registerFlingGestureHandler(this.flingGestureListener);
            this.keyboardTouchEventDispatcher.resisterTouchKeyHandler(this.touchKeyActionHandler);
            this.keyboardTouchEventDispatcher.wrapTouchEvent(this);
            this.keyboardTouchEventDispatcher.wrapHoverEvent(this);
            this.mAlphaInput.setInputContextRequestListener(InputContextRequestDispatcher.getDispatcherInstance().setHandler(this.inputContextRequestHandler));
            this.mEditState.startSession();
            dismissPopupKeyboard();
            flushCurrentActiveWord();
            if (restarting && getAppSpecificBehavior().shouldRemoveUpdateWCLMessage() && this.mAlphaInputViewHandler != null) {
                this.mAlphaInputViewHandler.removeMessages(9);
            }
            InputMethods.InputMode inputMode = this.mCurrentInputLanguage.getCurrentInputMode();
            KeyboardEx.KeyboardLayerType currentLayer = this.mKeyboardSwitcher.currentKeyboardLayer();
            this.isKeepingKeyboard = restarting && currentLayer != KeyboardEx.KeyboardLayerType.KEYBOARD_INVALID;
            if (this.isKeepingKeyboard) {
                this.mKeyboardSwitcher.createKeyboardForTextInput(currentLayer, inputFieldInfo, inputMode);
            } else {
                this.mKeyboardSwitcher.createKeyboardForTextInput(inputFieldInfo, inputMode);
                if (!this.mIme.isHardKeyboardActive() && !this.isOrientationChangedOnce) {
                    this.mKeyboardSwitcher.setShiftState(Shift.ShiftState.OFF);
                }
            }
            if ((inputFieldInfo.isInputTextClass() || inputFieldInfo.isPhoneNumberField()) && !this.isOrientationChangedOnce) {
                updateShiftKeyState(inputFieldInfo.mEditorInfo);
            }
            if (this.mIme != null && this.mIme.isChangingOrientation()) {
                this.isOrientationChangedOnce = this.mIme.isChangingOrientation();
            }
            this.mAlphaInputViewHandler.removeMessages(15);
            this.mAlphaInputViewHandler.sendMessageDelayed(this.mAlphaInputViewHandler.obtainMessage(15, 0, this.isKeepingKeyboard ? 1 : 0), 5L);
            this.mAlphaInputViewHandler.removeMessages(16);
            this.mAlphaInputViewHandler.sendMessageDelayed(this.mAlphaInputViewHandler.obtainMessage(16, restarting ? 1 : 0, this.isOrientationChangedOnce ? 1 : 0), 5L);
            this.mAlphaInputViewHandler.removeMessages(8);
            if (!inputFieldInfo.isPhoneNumberField() && !inputFieldInfo.isPasswordField()) {
                this.mAlphaInputViewHandler.sendEmptyMessageDelayed(8, 10L);
            }
            if (inputFieldInfo.isPasswordField()) {
                triggerPasswordTip();
            }
            if (!this.mAutoSpace) {
                this.autospaceHandler.setEnabled(false, this.mCurrentInputLanguage.isAutoSpaceSupported() ? false : true);
            } else {
                this.autospaceHandler.setEnabled(true, this.mCurrentInputLanguage.isAutoSpaceSupported() ? false : true);
                this.autospaceHandler.onUpdateEditorInfo(inputFieldInfo);
            }
            this.recaptureEditWord.clear();
            postDelayResumeSpeech();
            this.oovWordHistory.clear();
            showUserThemeWclMessage(this.mAlphaInputViewHandler);
            showTrialExpireWclMessage("alphaInput");
            if (!this.mEnableEmojiChoiceList) {
                this.emojiCandidatesHolder.setVisibility(8);
            }
        }
    }

    private void postDelayResumeSpeech() {
        if (this.mAlphaInputViewHandler.hasMessages(11)) {
            this.mAlphaInputViewHandler.removeMessages(11);
        }
        this.mAlphaInputViewHandler.sendEmptyMessageDelayed(11, 1L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startInputSession() {
        if (this.mCurrentInputLanguage != null) {
            this.wordCandidateDataPoint.write(getInputFieldInfo().getEditorInfo(), this.wordCandidateDataPointWriter);
            setLanguage(this.mAlphaInput);
            this.mAlphaInput.startSession();
            this.mAlphaInput.setPunctuationBreaking(true);
            this.mAlphaInput.setLDBEmoji(UserPreferences.from(this.mIme).isEmojiSuggestionsEnabled());
            this.mAlphaInput.setMultiTapInputMode(false);
            readNextWordPredictionSettings();
            this.mAlphaInput.setAttribute(101, this.mWordCompletionPoint);
            this.mAlphaInput.setBackCorrection(this.autoCorrectionEnabled);
            int autoSpaceAttr = (this.traceAutoAcceptOn && this.mAutoSpace) ? 1 : 0;
            this.mAlphaInput.setAttribute(104, autoSpaceAttr);
            this.mAlphaInput.setAttribute(99, this.autoCorrectionEnabled && !getXT9CoreInput().isNullLdb());
            this.terminalPunct = this.mAlphaInput.getTerminalPunct();
            this.promptForAddingOOVCandidate.setExplicitPromptState();
        }
    }

    private void setAppContextPrediction() {
        EditorInfo editorInfo = getCurrentInputEditorInfo();
        String packageName = editorInfo != null ? editorInfo.packageName : null;
        String groupName = IMEApplication.from(getContext()).getAppContextPredictionSetter().getGroupName(packageName);
        String groupSlot = groupName != null ? "grp:" + groupName : null;
        Slots slots = new Slots();
        slots.add(packageName);
        slots.add(groupSlot);
    }

    /* loaded from: classes.dex */
    public static class Slots {
        private final ArrayList<String> slots = new ArrayList<>();

        public void clear() {
            this.slots.clear();
        }

        public void add(String item) {
            if (item != null) {
                this.slots.add(item);
            }
        }

        public String get(int index) {
            if (index < this.slots.size()) {
                return this.slots.get(index);
            }
            return null;
        }
    }

    private void endInputSession() {
        if (this.mAlphaInput != null) {
            this.mAlphaInput.clearApplicationPredictionContext();
            this.mAlphaInput.finishSession();
            if (getInputFieldInfo() != null) {
                this.wordCandidateDataPoint.write(getInputFieldInfo().getEditorInfo(), this.wordCandidateDataPointWriter);
            }
        }
        this.isTelexMode = false;
        this.terminalPunct = null;
    }

    @Override // com.nuance.swype.input.InputView
    public void wordReCaptureComplete() {
        this.isOrientationChangedOnce = false;
    }

    @Override // com.nuance.swype.input.InputView
    public void finishInput() {
        if (this.mAlphaInput != null) {
            removeAllMessages();
            dimissRemoveUdbWordDialog();
            flushCurrentActiveWord();
            this.mEditState.endSession();
            endInputSession();
            this.mAlphaInput.registerExplicitLearningApprovalCallback(null);
            this.oovWordHistory.clear();
            if (!this.isOrientationChangedOnce) {
                this.mLastInput = 0;
            }
            super.finishInput();
            this.keyboardTouchEventDispatcher.unwrapTouchEvent(this);
            this.keyboardTouchEventDispatcher.unwrapHoverEvent(this);
            this.xt9coreinput.setInputContextRequestListener(null);
        }
    }

    @Override // com.nuance.swype.input.InputView
    public void markActiveWordUsedIfAny() {
        if (composingWordCandidates() && this.suggestionCandidates != null) {
            WordCandidate wordCandidate = this.suggestionCandidates.getDefaultCandidate();
            this.mAlphaInput.wordSelected(wordCandidate.id(), false);
            this.mEditState.selectWord(wordCandidate.toString(), wordCandidate.toString());
        }
    }

    @Override // com.nuance.swype.input.InputView
    public void flushCurrentActiveWord() {
        InputConnection ic = getCurrentInputConnection();
        if (ic != null) {
            ic.beginBatchEdit();
            log.d("cursor flushCurrentActiveWord");
            ic.finishComposingText();
            ic.endBatchEdit();
        }
        clearSuggestions();
    }

    @Override // com.nuance.swype.input.InputView
    public void updateSelection(int oldSelStart, int oldSelEnd, int newSelStart, int newSelEnd, int[] candidatesIndices) {
        this.inputContextRequestHandler.resetLastAutoSpaceInserted();
        if (!this.inputContextRequestHandler.cursorChangedLikelyCauseByUs()) {
            boolean validComposingText = validateComposingText(oldSelStart, oldSelEnd, newSelStart, newSelEnd, candidatesIndices, this.mInlineWord);
            if (!validComposingText) {
                this.autospaceHandler.resetSingleAutospaceFlags();
            }
            super.updateSelection(oldSelStart, oldSelEnd, newSelStart, newSelEnd, candidatesIndices);
            if (oldSelStart == oldSelEnd && newSelStart < newSelEnd) {
                this.mLastInput = 1;
            }
            if (isShowingCandidatesFor(Candidates.Source.UDB_EDIT)) {
                boolean hadSel = oldSelEnd != oldSelStart;
                boolean hasSel = newSelEnd != newSelStart;
                boolean lostSel = hadSel && !hasSel;
                if (validComposingText && lostSel && candidatesIndices[1] != -1) {
                    Candidates candidates = this.mAlphaInput.getCandidates(Candidates.Source.TAP);
                    setSuggestions(this, candidates, getWordListFormat(candidates));
                }
            }
            this.updateSelectionCallback.updateSelection(composingWordCandidates(), newSelStart, newSelEnd, getCurrentInputConnection());
        }
    }

    @Override // com.nuance.swype.input.InputView
    public boolean handleKey(int primaryCode, boolean quickPressed, int repeatedCount) {
        String selectedText;
        boolean handled = false;
        switch (primaryCode) {
            case KeyboardEx.GESTURE_KEYCODE_SUPPRESS_AUTOSPACE /* -106 */:
            case KeyboardEx.KEYCODE_PASTE /* 2899 */:
                this.autospaceHandler.onKey(primaryCode);
                return false;
            case 10:
                this.inputContextRequestHandler.autoAccept(false);
                sendKeyChar((char) primaryCode);
                showNextWordPrediction();
                return true;
            case 32:
                if (this.mEditState.current() == 7 || this.mEditState.current() == 4 || this.mEditState.current() == 5) {
                    triggerAutoSpaceTip();
                }
                boolean handled2 = handleSpace(quickPressed, repeatedCount);
                if (!this.mInputFieldInfo.isNumberField() && this.mKeyboardSwitcher.isSymbolMode()) {
                    setKeyboardLayer(KeyboardEx.KeyboardLayerType.KEYBOARD_TEXT);
                    return handled2;
                }
                return handled2;
            case KeyboardEx.KEYCODE_MULTITAP_TOGGLE /* 2940 */:
                if (this.mKeyboardInputSuggestionOn) {
                    postToastMsg(5);
                }
                return true;
            case KeyboardEx.KEYCODE_SPEECH /* 6463 */:
                if (!quickPressed) {
                    startSpeech();
                }
                return true;
            case KeyboardEx.KEYCODE_SWYPE /* 43575 */:
                if (isExploreByTouchOn()) {
                    return true;
                }
                if (this.mCurrentInputLanguage.isAutoSpaceSupported() || this.mCurrentInputLanguage.isNonSpacedLanguage()) {
                    AppSpecificInputConnection ic = getCurrentInputConnection();
                    if (isValidBuild() && ic != null && (selectedText = ic.getSelectedTextInEditor(this.mInputFieldInfo)) != null && selectedText.length() > 0) {
                        handleAddRemoveDlmWord(selectedText, true);
                        handled = true;
                    }
                } else {
                    AppSpecificInputConnection ic2 = getCurrentInputConnection();
                    if (ic2 != null) {
                        int[] composing = ic2.getComposingRangeInEditor();
                        handled = composing != null && ic2.setSelection(composing[0], composing[1]);
                    }
                }
                if (!handled) {
                    handled = super.handleKey(primaryCode, quickPressed, repeatedCount);
                }
                updateShiftKeyState();
                return handled;
            case KeyboardEx.KEYCODE_MODE_BACK /* 43576 */:
                this.mKeyboardSwitcher.toggleLastKeyboard();
                return true;
            default:
                return super.handleKey(primaryCode, quickPressed, repeatedCount);
        }
    }

    private boolean isAddSpaceRequired() {
        CharSequence text;
        boolean required = isComposingText();
        if (this.suggestionCandidates != null) {
            required = required || this.suggestionCandidates.source() == Candidates.Source.NEXT_WORD_PREDICTION;
        }
        InputConnection ic = getCurrentInputConnection();
        if (ic != null && (text = ic.getTextBeforeCursor(1, 0)) != null && text.length() > 0 && text.equals(XMLResultsHandler.SEP_SPACE)) {
            return false;
        }
        return required;
    }

    @Override // com.nuance.swype.input.InputView
    public boolean handleKeyDown(int keyCode, KeyEvent event) {
        if (super.handleKeyDown(keyCode, event) || this.mAlphaInput == null || this.suggestionCandidates == null) {
            return false;
        }
        if (keyCode == 4029 || keyCode == 4059) {
            if (composingWordCandidates() || (this.suggestionCandidates != null && this.suggestionCandidates.count() > 0 && this.suggestionCandidates.source() == Candidates.Source.RECAPTURE_BY_TEXT_SELECTION)) {
                selectCandidate(this.suggestionCandidates.getDefaultCandidate(), null, StatisticsEnabledEditState.DefaultSelectionType.CURSOR_REPOSITION);
                return false;
            }
            flushCurrentActiveWord();
            return false;
        }
        return false;
    }

    @Override // com.nuance.swype.input.InputView
    public void onText(CharSequence text, long eventTime) {
        AppSpecificInputConnection ic = getCurrentInputConnection();
        if (ic != null && text != null && text.length() != 0) {
            boolean shouldAutoSpace = this.autospaceHandler.onText(text, this.mLastInput, false);
            if (this.mAlphaInput == null || !this.mTextInputPredictionOn) {
                if (shouldAutoSpace) {
                    this.mIme.sendSpace();
                }
                ic.beginBatchEdit();
                if (isShifted()) {
                    ic.commitText(text.toString().toUpperCase(), 1);
                } else {
                    ic.commitText(text, 1);
                }
                ic.endBatchEdit();
                updateShiftKeyState();
                updateMultitapDeadkey(true);
                return;
            }
            processDeferredSuggestionUpdates();
            boolean isDomain = isLikelyDomain(text);
            boolean isShiftedText = isShifted();
            boolean wordAcceptingSymbol = text.length() == 1 && this.charUtils.isWordAcceptingSymbol(text.charAt(0));
            boolean punctuationOrSymbol = text.length() == 1 && this.charUtils.isPunctuationOrSymbol(text.charAt(0));
            if ((!composingWordCandidates() && punctuationOrSymbol) || wordAcceptingSymbol) {
                if (shouldAutoSpace) {
                    this.mIme.sendSpace();
                }
                handlePunctOrSymbol(text.charAt(0));
                return;
            }
            CharSequence symbols = text;
            if (isShiftedText && isDomain) {
                symbols = text.toString().toUpperCase();
            }
            if (symbols.length() > 1 || wordAcceptingSymbol || getEmojiInputViewController().isActive()) {
                ic.beginBatchEdit();
                if (shouldAutoSpace) {
                    symbols = XMLResultsHandler.SEP_SPACE + ((Object) symbols);
                }
                String selectedText = ic.getSelectedTextInEditor(this.mInputFieldInfo);
                if (composingWordCandidates() && (selectedText == null || selectedText.length() == 0)) {
                    setDefaultWordType(StatisticsEnabledEditState.DefaultSelectionType.GENERIC);
                    selectCandidate(this.suggestionCandidates.getDefaultCandidate(), null, true, false, StatisticsEnabledEditState.DefaultSelectionType.GENERIC, !isDomain);
                    if (symbols.length() > 0) {
                        ic.commitText(symbols, 1);
                    }
                } else {
                    ic.commitText(symbols, 1);
                    clearSuggestions();
                }
                if (isDomain) {
                    this.promptForAddingOOVCandidate.clearOOVWords();
                    learnNewWords();
                    this.promptForAddingOOVCandidate.promptToAddIfAny();
                }
                this.mEditState.punctuationOrSymbols();
                updateShiftKeyState();
                this.mAlphaInput.clearAllKeys();
                showNextWordPrediction();
                ic.endBatchEdit();
            } else if (CharacterUtilities.isDigit(symbols.charAt(symbols.length() - 1)) && !composingWordCandidates()) {
                if (shouldAutoSpace) {
                    this.mIme.sendSpace();
                }
                ic.beginBatchEdit();
                clearCurrentInline(ic);
                clearSuggestions();
                ic.commitText(symbols, 1);
                this.mEditState.punctuationOrSymbols();
                ic.endBatchEdit();
            } else {
                if (shouldAutoSpace) {
                    this.mIme.sendSpace();
                }
                if (!composingWordCandidates()) {
                    clearCurrentInline(ic);
                    clearSuggestions();
                }
                this.mAlphaInput.addExplicit(symbols.toString().toCharArray(), symbols.length(), getShiftState());
                displayExactTypeAsInline(null, ic);
                updateSuggestions(Candidates.Source.TAP);
            }
            updateShiftKeyState();
            updateMultitapDeadkey(true);
            this.mLastInput = 5;
        }
    }

    @Override // com.nuance.swype.input.InputView
    public void handleCharKey(Point pointTapped, int primaryCode, long eventTime) {
        super.handleCharKey(pointTapped, primaryCode, eventTime);
        if (this.mCurrentInputLanguage.isNonSpacedLanguage() && clearSelectionKeys()) {
            clearSuggestions();
        }
        if (isCursorWithinWord()) {
            this.mAlphaInput.clearAllKeys();
            char ch = (char) primaryCode;
            this.inputContextRequestHandler.commitText(String.valueOf(getShiftState() != Shift.ShiftState.OFF ? this.mAlphaInput.toUpperSymbol(ch) : this.mAlphaInput.toLowerSymbol(ch)));
            updateShiftKeyState();
            return;
        }
        if (Character.isAlphabetic(primaryCode) || CharacterUtilities.isDigit(primaryCode)) {
            KeyType type = Character.isAlphabetic(primaryCode) ? KeyType.LETTER : KeyType.NUMBER;
            autoSpace(type, this.mLastInput, false);
        }
        updateExplicitInput((char) primaryCode);
        if (this.charUtils.isWordCompounder(primaryCode) && composingWordCandidates() && !this.mKeyboardSwitcher.isAlphabetMode() && this.mKeyboardSwitcher.supportsAlphaMode()) {
            setKeyboardLayer(KeyboardEx.KeyboardLayerType.KEYBOARD_TEXT);
        }
    }

    private boolean composingWordCandidates() {
        if (this.pendingCandidateSource == Candidates.Source.TRACE || this.pendingCandidateSource == Candidates.Source.TAP || this.pendingCandidateSource == Candidates.Source.RECAPTURE) {
            return true;
        }
        return !isEmptyCandidateList() && (this.suggestionCandidates.source() == Candidates.Source.TRACE || this.suggestionCandidates.source() == Candidates.Source.TAP || this.suggestionCandidates.source() == Candidates.Source.RECAPTURE);
    }

    @Override // com.nuance.swype.input.KeyboardViewEx
    protected void handlePreTrace(KeyboardViewEx.TracePoints points) {
        processDeferredSuggestionUpdates();
        selectDefaultSuggestion(StatisticsEnabledEditState.DefaultSelectionType.SWYPE_NEXT_WORD);
        if (this.isManualShift && this.mKeyboardSwitcher.isAlphabetMode()) {
            triggerCapitalizationGestureTip();
        }
        this.mPreTraceShiftedState = getShiftState();
        if (this.mPreTraceShiftedState == Shift.ShiftState.ON && getKeyboardLayer() != KeyboardEx.KeyboardLayerType.KEYBOARD_SYMBOLS) {
            setShiftState(Shift.ShiftState.OFF);
        }
    }

    @Override // com.nuance.swype.input.InputView
    public void handleTrace(KeyboardViewEx.TracePoints trace) {
        log.d("handleTrace called");
        this.shiftGestureOn = false;
        if (this.mAlphaInput.hasActiveInput()) {
            createCandidatesBuilderHandler();
            this.candidatesBuilderHandler.build(Candidates.Source.TRACE);
        } else {
            showNextWordPrediction();
        }
    }

    @Override // com.nuance.swype.input.KeyboardViewEx
    public int getShiftGestureOffset() {
        return this.shiftGestureOffset;
    }

    private boolean handleBackspaceDuringMultitap() {
        if (isMultitapping()) {
            if (!this.mTextInputPredictionOn || !isComposingText()) {
                return true;
            }
            this.mAlphaInput.clearKey();
            return true;
        }
        return false;
    }

    @Override // com.nuance.swype.input.InputView
    public boolean handleBackspace(int repeatedCount) {
        boolean isComposing;
        AppSpecificInputConnection ic = getCurrentInputConnection();
        if (this.mAlphaInput == null || ic == null) {
            sendBackspace(repeatedCount);
        } else if (!handleBackspaceDuringMultitap()) {
            boolean showNWP = false;
            if (this.suggestionCandidates != null && this.suggestionCandidates.count() > 0 && !isComposingText()) {
                if (this.mInlineWord.length() > 0) {
                    clearCurrentInline(ic);
                }
                if (this.pendingCandidateSource == Candidates.Source.RECAPTURE) {
                    ic.setComposingText("", 1);
                    clearSuggestions();
                    showNWP = true;
                }
                if (!IsTextFieldEmpty() && !showNWP) {
                    clearSuggestions();
                }
            }
            if (!InputMethods.MULTITAP_INPUT_MODE.equals(this.mKeyboardSwitcher.getCurrentInputMode()) && this.suggestionCandidates == null && this.mTextInputPredictionOn && this.pendingCandidateSource == Candidates.Source.INVALID) {
                this.mEditState.backSpace();
                if (ic.hasSelection()) {
                    ic.clearHighlightedText();
                } else {
                    sendBackspace(repeatedCount);
                }
                if (IsTextFieldEmpty() || showNWP) {
                    showNextWordPrediction();
                }
            } else {
                if (this.mTextInputPredictionOn) {
                    if (isComposingText()) {
                        if (this.mAlphaInputViewHandler.hasMessages(9)) {
                            this.mAlphaInputViewHandler.removeMessages(9);
                        }
                        if (!clearSelectionKeys()) {
                            int strLen = this.mInlineWord.length();
                            if (strLen > 0 && getCurrentWordCandidatesSource() == Candidates.Source.RECAPTURE && !this.isTelexMode) {
                                this.mInlineWord.delete(strLen - 1, strLen);
                                this.mAlphaInput.clearKey();
                                isComposing = this.mInlineWord.length() > 0;
                            } else {
                                this.mAlphaInput.clearKey();
                                isComposing = isComposingText();
                            }
                            if (CharacterUtilities.endsWithSurrogatePair(this.exactType)) {
                                this.mAlphaInput.clearKey();
                                isComposing = isComposingText();
                            }
                        } else {
                            isComposing = isComposingText();
                        }
                        if (isComposing) {
                            boolean bRecaptureNext = getCurrentWordCandidatesSource() == Candidates.Source.RECAPTURE;
                            if (bRecaptureNext && this.mInlineWord != null && this.mInlineWord.length() > 0 && !this.isTelexMode) {
                                displayExactTypeAsInline(this.mInlineWord.toString(), ic);
                            } else {
                                this.mAlphaInput.getExactType(this.exactType);
                                if (this.exactType.length() > 0) {
                                    displayExactTypeAsInline(this.exactType.toString(), ic);
                                } else {
                                    updateSuggestions(Candidates.Source.TAP);
                                    processDeferredSuggestionUpdates();
                                    if (this.suggestionCandidates != null) {
                                        displayDefaultCandidateAsInline(ic, this.suggestionCandidates);
                                    }
                                }
                            }
                            this.mLastInput = 1;
                            if (IsTextFieldEmpty()) {
                                showNextWordPrediction();
                            } else if (bRecaptureNext) {
                                if (!this.mIme.reconstructByTap()) {
                                    flushCurrentActiveWord();
                                }
                            } else {
                                updateSuggestions(Candidates.Source.TAP);
                            }
                            updateShiftKeyState();
                        } else {
                            ic.setComposingText("", 1);
                            clearSuggestions();
                            if (this.backspaceRevertHandler != null) {
                                this.backspaceRevertHandler.onBackToWordBegin();
                            }
                            this.mEditState.backSpaceClearCompositingWordCandiateList();
                            updateShiftKeyState();
                            if (IsTextFieldEmpty()) {
                                showNextWordPrediction();
                            }
                        }
                    } else if ((!isEmptyCandidateList() || this.pendingCandidateSource != Candidates.Source.INVALID) && !getAppSpecificBehavior().shouldSendBackSpaceToDeleteBreakLine() && !getAppSpecificBehavior().shouldDeleteSurroundingTextUsingKeyEvent()) {
                        if (ic.hasComposing()) {
                            ic.setComposingText("", 1);
                        } else if (ic.hasSelection()) {
                            ic.commitText("", 0);
                        } else {
                            sendBackspace(repeatedCount);
                        }
                        clearPendingSuggestionsUpdate();
                        this.mEditState.backSpace();
                        this.mEditState.backSpaceClearCompositingWordCandiateList();
                        updateShiftKeyState();
                    }
                }
                sendBackspace(repeatedCount);
                clearAllKeys();
                this.mAlphaInput.setContext(null);
                this.mEditState.backSpace();
                if (IsTextFieldEmpty()) {
                    showNextWordPrediction();
                }
            }
        }
        return true;
    }

    @Override // com.nuance.swype.input.InputView
    public boolean handleDeleteWordBack(KeyboardEx.Key key) {
        boolean deleted = super.handleDeleteWordBack(key);
        if (deleted && this.backspaceRevertHandler != null) {
            this.backspaceRevertHandler.onBackToWordBegin();
        }
        return deleted;
    }

    @Override // com.nuance.swype.input.InputView
    public boolean handleSpace(boolean quickPressed, int repeatedCount) {
        AppSpecificInputConnection ic = getCurrentInputConnection();
        boolean displayNWP = this.mNextWordPredictionOn;
        this.inputContextRequestHandler.resetLastAutoSpaceInserted();
        if (this.mAlphaInput == null || ic == null) {
            sendKeyChar(' ');
            return true;
        }
        boolean isSpaceAfterRevert = this.backspaceRevertHandler != null && this.backspaceRevertHandler.isOverrideActive();
        if (this.backspaceRevertHandler != null) {
            this.backspaceRevertHandler.onPreSpace(this.suggestionCandidates);
        }
        processDeferredSuggestionUpdates();
        CharSequence cSeqBef = ic.getTextBeforeCursor(1, 0);
        if (cSeqBef != null && cSeqBef.length() == 1) {
            if (isTerminalPunctuation(cSeqBef.charAt(0))) {
                triggerPunctuationGestureTip();
            }
            if (cSeqBef.charAt(0) == ' ') {
                displayNWP = false;
            }
        }
        ic.beginBatchEdit();
        Shift.ShiftState prevShiftState = getShiftState();
        if (composingWordCandidates() || (this.mIsUseHardkey && this.mInlineWord.length() > 0 && this.suggestionCandidates != null && this.suggestionCandidates.source() == Candidates.Source.NEXT_WORD_PREDICTION)) {
            if (!this.suggestionCandidates.getDefaultCandidate().equals(this.suggestionCandidates.getExactCandidate())) {
                this.oovWordHistory.add(this.suggestionCandidates.getExactCandidate().word());
            }
            displayNWP = this.mNextWordPredictionOn;
            setDefaultWordType(StatisticsEnabledEditState.DefaultSelectionType.TAPPED_SPACE);
            selectCandidate(this.suggestionCandidates.getDefaultCandidate(), null, StatisticsEnabledEditState.DefaultSelectionType.TAPPED_SPACE);
            if (this.mLastInput == 2) {
                triggerAutoSpaceTip();
            }
        } else if (this.suggestionCandidates != null && this.suggestionCandidates.source() != Candidates.Source.NEXT_WORD_PREDICTION) {
            if (isSpaceAfterRevert) {
                handleOverrideCacheAddSelectedCandidate(this.suggestionCandidates);
                selectCandidate(this.suggestionCandidates.getDefaultCandidate(), null, false, false, StatisticsEnabledEditState.DefaultSelectionType.TAPPED_SPACE, true);
            } else {
                boolean isKnown = true;
                if (this.suggestionCandidates.source() == Candidates.Source.RECAPTURE_BY_TEXT_SELECTION || this.suggestionCandidates.source() == Candidates.Source.CAPS_EDIT) {
                    CharSequence wordBeforeCursor = InputConnectionUtils.getWordBeforeCursor$498a830e(ic, this.charUtils);
                    if (this.promptToAddWords && wordBeforeCursor != null && wordBeforeCursor.length() > 1 && !(isKnown = this.mAlphaInput.isKnownWord(wordBeforeCursor.toString()))) {
                        setDefaultWordType(StatisticsEnabledEditState.DefaultSelectionType.TAPPED_SPACE);
                        clearSuggestions();
                        clearAllKeys();
                        handleAddRemoveDlmWord(wordBeforeCursor.toString(), false);
                    }
                }
                if (isKnown) {
                    clearSuggestions();
                }
            }
        }
        if (!isExploreByTouchOn() && this.mCurrentInputLanguage.getTerminalPunctuation() != null && this.mCurrentInputLanguage.getTerminalPunctuation().length() != 0 && quickPressed && this.mAutoPunctuationOn && this.mTextInputPredictionOn) {
            CharSequence cSeqBefore = ic.getTextBeforeCursor(2, 0);
            CharSequence autopunct = this.mCurrentInputLanguage.getTerminalPunctuation();
            if (cSeqBefore != null && cSeqBefore.length() == 2 && CharacterUtilities.isWhiteSpace(cSeqBefore.charAt(1)) && !CharacterUtilities.isWhiteSpace(cSeqBefore.charAt(0)) && !this.charUtils.isPunctuationOrSymbol(cSeqBefore.charAt(0)) && autopunct.charAt(0) != cSeqBefore.charAt(0)) {
                ic.deleteSurroundingText(1, 0);
                ic.commitText(autopunct, 1);
                displayNWP = this.mNextWordPredictionOn;
            }
        }
        this.mEditState.spaceKey();
        sendKeyChar(' ');
        updateMultitapDeadkey(true);
        if (!isShowingCandidatesFor(Candidates.Source.UDB_EDIT)) {
            if (displayNWP) {
                clearSuggestions();
                log.d("handleSpace...displayNWP...shiftState: ", getShiftState());
                this.mAlphaInput.setShiftState(getShiftState());
                updateSuggestions(Candidates.Source.NEXT_WORD_PREDICTION);
            } else if (this.suggestionCandidates != null && this.suggestionCandidates.count() > 0 && this.suggestionCandidates.source() == Candidates.Source.NEXT_WORD_PREDICTION && prevShiftState != getShiftState()) {
                log.d("handleSpace...shiftState: ", getShiftState());
                this.mAlphaInput.setShiftState(getShiftState());
                updateSuggestions(Candidates.Source.NEXT_WORD_PREDICTION);
            }
        }
        ic.endBatchEdit();
        return true;
    }

    @Override // com.nuance.swype.input.InputView
    public void handleClose() {
        dimissRemoveUdbWordDialog();
        selectDefaultSuggestion(false, StatisticsEnabledEditState.DefaultSelectionType.GENERIC);
        flushCurrentActiveWord();
        removeAllMessages();
        super.handleClose();
    }

    @Override // com.nuance.swype.input.InputView, com.nuance.swype.input.CandidatesListView.CandidateListener
    public boolean onPressHoldCandidate(WordCandidate candidate, Candidates candidates) {
        if (candidate != null) {
            log.d("onPressHoldCandidate()", " draw called ::  left:: " + candidate.getLeft() + ", top:: " + candidate.getTop() + ", Width :: " + candidate.getWidth() + " , height :: " + candidate.getHeight());
            if (EmojiLoader.isEmoji(candidate.toString())) {
                if (isShowingSkinTonePopup()) {
                    dismissEmojiPopup();
                }
                if (!isSupportSkinTone(candidate.getEmojiUnicode())) {
                    return true;
                }
                if (getCurrentInputConnection() != null && candidates != null) {
                    this.autospaceHandler.onSelectCandidate(candidates.source(), candidates.getDefaultCandidate(), candidate, this.mLastInput);
                }
                super.showSkinTonePopup(candidate, candidates);
                if (!isShowingSkinTonePopup()) {
                    return true;
                }
                if (this.emojiCandidatesListView != null) {
                    this.emojiCandidatesListView.setHorizontalScroll(false);
                }
                if (this.candidatesListView == null) {
                    return true;
                }
                this.candidatesListView.setHorizontalScroll(false);
                return true;
            }
            if (candidate.isRemovable()) {
                showRemoveUdbWordDialog(candidate.toString());
                return true;
            }
        }
        return false;
    }

    @Override // com.nuance.swype.input.InputView, com.nuance.swype.input.CandidatesListView.CandidateListener
    public boolean onPressReleaseCandidate(WordCandidate candidate, Candidates candidates) {
        log.d("onPressReleaseCandidate()", " called ::");
        if (isShowingSkinTonePopup()) {
            super.setCurrentSkinTone();
            super.dismissEmojiPopup();
            clearSuggestions();
            showNextWordPrediction();
            return true;
        }
        if (this.removeUdbWordDialog != null && this.removeUdbWordDialog.isShowing()) {
            return false;
        }
        onSelectCandidate(candidate, candidates);
        return false;
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
        if (this.mAlphaInput != null) {
            this.mAlphaInput.dlmDelete(word);
        }
        showNextWordPrediction();
    }

    @Override // com.nuance.swype.input.InputView, com.nuance.swype.input.CandidatesListView.CandidateListener
    public boolean onSelectCandidate(WordCandidate candidate, Candidates candidates) {
        super.onSelectCandidate(candidate, candidates);
        log.d("onSelectCandidate(): " + candidate);
        this.suggestionCandidates = candidates;
        Candidates.Source source = this.suggestionCandidates.source();
        if (getCurrentInputConnection() != null) {
            if (candidates != null) {
                this.autospaceHandler.onSelectCandidate(candidates.source(), candidates.getDefaultCandidate(), candidate, this.mLastInput);
            }
            boolean composingWordList = composingWordCandidates();
            boolean isSelectRevertedWord = candidates.getDefaultCandidate().equals(candidate) && this.backspaceRevertHandler != null && this.backspaceRevertHandler.isOverrideActive();
            log.d("onSelectCandidate(): is revert word: " + isSelectRevertedWord);
            if (isSelectRevertedWord) {
                handleOverrideCacheAddSelectedCandidate(this.suggestionCandidates);
            }
            selectCandidate(candidate, null, true, true, StatisticsEnabledEditState.DefaultSelectionType.SELECTION_WCL, true);
            if (composingWordList) {
                if (candidate.isDefault()) {
                    triggerAutoDefaultCandidateAcceptTip();
                } else if (candidates.source() == Candidates.Source.TRACE && candidate.containsDoubleLetters()) {
                    triggerDoubleLettersGestureTip();
                }
            }
            if (this.suggestionCandidates == null || this.suggestionCandidates.count() == 0 || Candidates.Source.UDB_EDIT != this.suggestionCandidates.source()) {
                updateShiftKeyState();
                log.d("onSelectCandidate...shiftState: ", getShiftState());
                this.mAlphaInput.setShiftState(getShiftState());
                showNextWordPrediction();
            }
        }
        if (candidate != null) {
            super.addEmojiInRecentCat(candidate);
        }
        this.mLastInput = source == Candidates.Source.NEXT_WORD_PREDICTION ? 6 : 4;
        return true;
    }

    private void selectCandidate(WordCandidate candidate, CharSequence symbolUsedInToSelectWord, boolean shouldCommit, boolean userExplicitPick, StatisticsEnabledEditState.DefaultSelectionType selectionType, boolean allowAddWordPrompt) {
        log.d("selectCandidate(): " + candidate + "; should commit: " + shouldCommit + "; user pick: " + userExplicitPick);
        setDefaultWordType(selectionType);
        AppSpecificInputConnection ic = getCurrentInputConnection();
        if (ic != null && !isEmptyCandidateList()) {
            this.promptForAddingOOVCandidate.setExplicitPromptState();
            this.mEditState.selectWord(candidate.toString(), this.suggestionCandidates.getDefaultCandidateString());
            int index = candidate.id();
            if (this.suggestionCandidates.source() == Candidates.Source.COMPLETIONS) {
                if (this.mCompletionOn && index >= 0 && index < this.mCompletions.size() && shouldCommit) {
                    ic.commitCompletion(this.mCompletions.get(index));
                }
            } else if (this.suggestionCandidates.source() == Candidates.Source.SPEECH_ALTERNATES) {
                if (shouldCommit) {
                    ic.commitText(candidate.toString(), 1);
                }
                speechChooseCandidate(index);
            } else {
                if (shouldCommit) {
                    this.undoAcceptHandler.onAcceptCandidate(candidate.word(), this.suggestionCandidates.getExactCandidate().word(), userExplicitPick);
                }
                if (this.suggestionCandidates.source() != Candidates.Source.EMOJEENIE) {
                    this.mAlphaInput.candidateSelected(candidate, this.suggestionCandidates, !userExplicitPick);
                }
                boolean isKnownWord = this.mAlphaInput.isKnownWord(candidate.toString());
                String wordSelected = candidate.toString();
                log.d("selectCandidate(): is known: " + isKnownWord + "; word: " + candidate);
                if (shouldCommit) {
                    WordDecorator wd = getWordDecorator();
                    if (candidate.contextKillLength() != 0) {
                        log.d("contextKillLength: ", Integer.valueOf(candidate.contextKillLength()));
                        ic.commitText("", 1);
                        this.mIme.deleteWord(ic, candidate.contextKillLength());
                    }
                    if (!isKnownWord && wd != null) {
                        ic.commitText(wd.decorateUnrecognizedWord(wordSelected), 1);
                    } else {
                        ic.commitText(wordSelected, 1);
                    }
                }
                if (symbolUsedInToSelectWord != null) {
                    if (shouldCommit) {
                        ic.commitText(symbolUsedInToSelectWord, 1);
                    }
                    this.mAlphaInput.setContext(null);
                } else {
                    noteWordChanged(candidate.toString(), userExplicitPick);
                }
            }
            clearSuggestions();
            if (allowAddWordPrompt) {
                if (this.mIme.isHardKeyboardActive()) {
                    UserPreferences userPrefs = UserPreferences.from(this.mIme);
                    if (!userPrefs.getBoolean(HardKeyboardManager.SHORTCUT_ADD_TO_DICTIONARY, false)) {
                        userPrefs.setBoolean(HardKeyboardManager.SHORTCUT_ADD_TO_DICTIONARY, true);
                        InputMethodToast.show(this.mIme, getResources().getString(R.string.hardkeyboard_shortcut_add_to_dictionary), 0);
                    }
                }
                this.promptForAddingOOVCandidate.promptToAddIfAny();
            }
        }
    }

    protected void selectWord(int wordIndex, boolean userExplicitPick) {
        this.mAlphaInput.setWordQuarantineLevel(1, 1, 1);
        this.mAlphaInput.wordSelected(wordIndex, userExplicitPick);
    }

    private void noteWordChanged(String candidate, boolean userExplicitPick) {
        SpeechWrapper spw;
        ExtractedText eText;
        if (!this.mCurrentInputLanguage.isNonSpacedLanguage()) {
            int udbCountBefore = this.mAlphaInput.dlmCount();
            AppSpecificInputConnection ic = getCurrentInputConnection();
            if (ic != null && (eText = ic.getExtractedText(new ExtractedTextRequest(), 0)) != null && !TextUtils.isEmpty(eText.text)) {
                int selectionEnd = eText.selectionEnd;
                int newWordLength = candidate.length();
                String oldWord = this.recaptureEditWord.getWord();
                if (userExplicitPick && oldWord.length() > 0) {
                    int startOfNewWord = Math.max(0, selectionEnd - newWordLength);
                    if (startOfNewWord == 0 || CharacterUtilities.isWhiteSpace(eText.text.charAt(startOfNewWord - 1))) {
                        this.mAlphaInput.noteWordWordChanged(eText.text.toString(), startOfNewWord, newWordLength, oldWord);
                    } else {
                        this.mAlphaInput.noteWordDone(eText.text.toString(), selectionEnd);
                    }
                } else {
                    this.mAlphaInput.noteWordDone(eText.text.toString(), selectionEnd);
                }
                this.recaptureEditWord.clear();
            }
            if (udbCountBefore != this.mAlphaInput.dlmCount() && (spw = IMEApplication.from(getContext()).getSpeechWrapper()) != null) {
                spw.addCustomWord(UserDictionaryIterator.createAlphaIterator(this.mAlphaInput, this.mCurrentInputLanguage, this.mSpeechWrapper), this.mInputFieldInfo, candidate);
            }
        }
    }

    private void selectCandidate(WordCandidate candidate, CharSequence symbolUsedInToSelectWord, StatisticsEnabledEditState.DefaultSelectionType type) {
        selectCandidate(candidate, symbolUsedInToSelectWord, true, false, type, true);
    }

    private void updateMultitapDeadkey(boolean invalidateKeyNow) {
        XT9Keyboard keyboard;
        KeyboardEx.Key key;
        if (this.mKeyboardSwitcher != null && this.mKeyboardLayoutId != 2304 && (keyboard = (XT9Keyboard) getKeyboard()) != null) {
            if (!this.mKeyboardInputSuggestionOn) {
                key = keyboard.setXT9KeyState(-1);
                if (key != null) {
                    key.codes[0] = 2942;
                }
            } else if (this.mKeyboardSwitcher.getCurrentInputMode().equals(InputMethods.MULTITAP_INPUT_MODE)) {
                if (isComposingText()) {
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
            setCorrectionLevel(inputMode);
            AppPreferences.from(getContext()).setMultitapMode(inputMode.equals(InputMethods.MULTITAP_INPUT_MODE));
            selectDefaultSuggestion(StatisticsEnabledEditState.DefaultSelectionType.MULTITAP_TOGGLE);
            updateMultitapDeadkey(true);
            this.mIme.refreshLanguageOnSpaceKey(getKeyboard(), this);
        }
    }

    @Override // com.nuance.swype.input.InputView
    public void onMultitapTimeout() {
        boolean z = true;
        InputConnection ic = getCurrentInputConnection();
        if (ic != null) {
            if (this.mAlphaInput == null || (this.mInlineWord.length() > 0 && !isComposingText())) {
                this.mInlineWord.clearSpans();
                ic.commitText(this.mInlineWord, 1);
                this.mInlineWord.clear();
            } else {
                if (this.mInlineWord.length() > 0) {
                    this.mInlineWord.removeSpan(this.mBKMultiTappingCharSpan);
                    this.mInlineWord.removeSpan(this.mFGMultiTappingCharSpan);
                    ic.setComposingText(this.mInlineWord, 1);
                }
                if (!isEmptyCandidateList()) {
                    CharSequence candidate = this.suggestionCandidates.getDefaultCandidateString();
                    char lastSymbol = candidate.charAt(candidate.length() - 1);
                    if (!this.charUtils.isWordAcceptingSymbol(lastSymbol) && (candidate.length() != 1 || !this.charUtils.isPunctuationOrSymbol(lastSymbol))) {
                        z = false;
                    }
                    if (z) {
                        selectCandidate(this.suggestionCandidates.getDefaultCandidate(), null, StatisticsEnabledEditState.DefaultSelectionType.MULTITAP_TIMEOUT);
                        this.mEditState.punctuationOrSymbols();
                    }
                }
            }
            updateShiftKeyState();
            invalidateKeyboardImage();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setMultitapOrAmbigMode() {
        if (this.mKeyboardLayoutId != 2304 && !this.mKeyboardSwitcher.getCurrentInputMode().equals(InputMethods.MULTITAP_INPUT_MODE)) {
            if (AppPreferences.from(getContext()).getMultitapMode(false)) {
                toggleAmbigMode();
                setCorrectionLevel(InputMethods.MULTITAP_INPUT_MODE);
            } else {
                setCorrectionLevel(null);
            }
        }
    }

    private void setCorrectionLevel(String inputMode) {
        if (this.mKeyboardInputSuggestionOn) {
            if (InputMethods.MULTITAP_INPUT_MODE.equals(inputMode)) {
                this.mAlphaInput.setAttribute(99, false);
            } else {
                this.mAlphaInput.setAttribute(99, this.autoCorrectionEnabled);
                this.mAlphaInput.setAttribute(101, this.mWordCompletionPoint);
            }
        }
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

    private boolean flushOnSingleSymbol(char symbol) {
        if (this.mAlphaInput.getKeyCount() != 1 || (!this.charUtils.isPunctuationOrSymbol(symbol) && !CharacterUtilities.isDigit(symbol))) {
            return false;
        }
        this.inputContextRequestHandler.commitText(String.valueOf(symbol));
        this.mAlphaInput.clearAllKeys();
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handlePrediction(int primaryCode) {
        if (getCurrentInputConnection() != null && this.mKeyboardSwitcher != null && this.mAlphaInput != null) {
            if (!isComposingText() && this.isManualShift && this.mKeyboardSwitcher.isAlphabetMode()) {
                triggerCapitalizationGestureTip();
            }
            char charCode = (char) primaryCode;
            if (this.mAlphaInput.hasActiveInput()) {
                this.mEditState.characterTyped(charCode);
                if (!flushOnSingleSymbol(charCode)) {
                    createCandidatesBuilderHandler();
                    this.candidatesBuilderHandler.build(Candidates.Source.TAP);
                    return;
                }
                return;
            }
            this.inputContextRequestHandler.commitText(String.valueOf(charCode));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void displayExactTypeAsInline(String composingTxt, InputConnection ic) {
        this.mInlineWord.clear();
        this.mInlineWord.clearSpans();
        if (composingTxt == null) {
            this.mAlphaInput.getExactType(this.exactType);
            log.d("exactType:", this.exactType);
            this.mInlineWord.append((CharSequence) this.exactType);
        } else {
            this.mInlineWord.append((CharSequence) composingTxt);
        }
        if (this.mInlineWord.length() > 0) {
            this.mInlineWord.setSpan(this.mWordComposeSpan, 0, this.mInlineWord.length(), R.styleable.ThemeTemplate_emojiCategoryIconSmiles);
        }
        WordDecorator wd = getWordDecorator();
        if (wd != null && !this.mAlphaInput.isKnownWord(this.mInlineWord.toString())) {
            wd.decorateUnrecognizedWord(this.mInlineWord);
        }
        if (ic != null) {
            ic.setComposingText(this.mInlineWord, 1);
        }
    }

    private void displayDefaultCandidateAsInline(InputConnection ic, Candidates candidates) {
        this.mAlphaInputViewHandler.removeMessages(9);
        this.pendingCandidateSource = Candidates.Source.INVALID;
        this.mInlineWord.clear();
        this.mInlineWord.clearSpans();
        this.mInlineWord.append(candidates.getExactCandidateString());
        if (this.mInlineWord.length() > 0) {
            if (ic != null) {
                ic.beginBatchEdit();
                if (this.mTextInputPredictionOn) {
                    this.mInlineWord.clearSpans();
                    if (getWordDecorator() == null) {
                        this.mInlineWord.toString().equals(candidates.getDefaultCandidate().toString());
                        if (getAppSpecificBehavior().shouldSetComosingSpan() || candidates.source() == Candidates.Source.TAP) {
                            this.mInlineWord.setSpan(this.mWordComposeSpan, 0, this.mInlineWord.length(), R.styleable.ThemeTemplate_emojiCategoryIconSmiles);
                        }
                    }
                    InputConnectionUtils.setComposingText(ic, this.mInlineWord);
                } else if (this.mInputFieldInfo.isInputTypeNull()) {
                    sendTextAsKeys(this.mInlineWord);
                } else {
                    ic.commitText(this.mInlineWord, 1);
                    log.d("cursor displayDefaultCandidateAsInline");
                    ic.finishComposingText();
                }
                ic.endBatchEdit();
            }
            this.mEditState.composeWordCandidate();
            if (candidates.source() == Candidates.Source.TRACE) {
                this.mLastInput = 2;
            }
        }
    }

    private boolean processDeferredSuggestionUpdates() {
        if (!this.mAlphaInputViewHandler.hasMessages(9) && !this.candidatesBuilderHandler.hasPendingBuild()) {
            return false;
        }
        this.candidatesBuilderHandler.removePendingBuild();
        this.mAlphaInputViewHandler.removeMessages(9);
        this.suggestionCandidates = this.mAlphaInput.getCandidates(this.pendingCandidateSource);
        this.pendingCandidateSource = Candidates.Source.INVALID;
        return true;
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
            this.mInlineWord.clear();
            ic.commitText("", 1);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handlePunctOrSymbol(int primaryCode) {
        if (getCurrentInputConnection() != null && this.mAlphaInput != null) {
            if (primaryCode == 32) {
                handleSpace(false, 1);
                return;
            }
            StringBuilder symbolUsedInSelectWord = null;
            if (!isEmptyCandidateList()) {
                Candidates.Source wlSource = this.suggestionCandidates.source();
                if (wlSource != Candidates.Source.RECAPTURE_BY_TEXT_SELECTION && wlSource != Candidates.Source.NEXT_WORD_PREDICTION && wlSource != Candidates.Source.TOOL_TIP && wlSource != Candidates.Source.CAPS_EDIT) {
                    if (!CharacterUtilities.isWhiteSpace((char) primaryCode)) {
                        symbolUsedInSelectWord = new StringBuilder();
                        symbolUsedInSelectWord.append((char) primaryCode);
                    }
                    if (symbolUsedInSelectWord != null && this.terminalPunct != null && this.terminalPunct.contains(symbolUsedInSelectWord) && this.suggestionCandidates.getDefaultCandidate().source() != WordCandidate.Source.WORD_SOURCE_NEW_WORD && !this.promptToAddWords) {
                        selectCandidate(this.suggestionCandidates.getDefaultCandidate(), null, StatisticsEnabledEditState.DefaultSelectionType.TAPPED_PUNCTUATION);
                        promotingTermPunctOrSingleLetter(symbolUsedInSelectWord.charAt(0), Shift.ShiftState.OFF);
                    } else {
                        selectCandidate(this.suggestionCandidates.getDefaultCandidate(), symbolUsedInSelectWord, StatisticsEnabledEditState.DefaultSelectionType.TAPPED_PUNCTUATION);
                    }
                } else if (wlSource != Candidates.Source.NEXT_WORD_PREDICTION) {
                    clearSuggestions();
                }
            } else {
                flushCurrentActiveWord();
            }
            this.mEditState.punctuationOrSymbols();
            if (symbolUsedInSelectWord == null) {
                if (this.mIme.isHardKeyboardActive()) {
                    sendHardKeyChar((char) primaryCode);
                } else if (this.terminalPunct != null && this.terminalPunct.indexOf((char) primaryCode) != -1) {
                    promotingTermPunctOrSingleLetter((char) primaryCode, Shift.ShiftState.OFF);
                } else {
                    sendKeyChar((char) primaryCode);
                }
            }
            updateMultitapDeadkey(true);
            if (this.candidatesListView != null && this.candidatesListView.isEditingUDBWords()) {
                if (primaryCode == 10) {
                    this.mAlphaInput.clearAllKeys();
                }
            } else if (this.mNextWordPredictionOn && !Character.isDigit(primaryCode)) {
                if (!this.charUtils.isTerminalPunctuation(primaryCode) || this.charUtils.isWordCompounder(primaryCode)) {
                    updateShiftKeyState();
                    this.mAlphaInput.clearAllKeys();
                    updateSuggestions(Candidates.Source.NEXT_WORD_PREDICTION);
                } else {
                    updateShiftKeyState();
                    this.mAlphaInput.clearAllKeys();
                    clearSuggestions();
                }
            }
        }
    }

    private void promotingTermPunctOrSingleLetter(char punctOrLetter, Shift.ShiftState shift) {
        this.mAlphaInput.addExplicit(new char[]{punctOrLetter}, 1, shift);
        Candidates candidates = this.mAlphaInput.getCandidates(Candidates.Source.TAP);
        selectWord(candidates.getDefaultCandidate().id(), false);
        getCurrentInputConnection().commitText(candidates.getDefaultCandidate().toString(), 1);
        this.mAlphaInput.clearAllKeys();
    }

    private boolean isTerminalPunctuation(char ch) {
        if (this.terminalPunct == null) {
            this.terminalPunct = this.mAlphaInput.getTerminalPunct();
        }
        return this.terminalPunct.indexOf(ch) != -1;
    }

    @Override // com.nuance.swype.input.InputView
    public int updateSuggestions(Candidates.Source source, boolean noComposingText) {
        int suggestionCount = 0;
        if (source == Candidates.Source.NEXT_WORD_PREDICTION) {
            if (this.mKeyboardSwitcher == null) {
                return 0;
            }
            this.mAlphaInputViewHandler.removeMessages(9);
            this.pendingCandidateSource = Candidates.Source.INVALID;
            this.mAlphaInputViewHandler.sendMessageDelayed(this.mAlphaInputViewHandler.obtainMessage(9, noComposingText ? 1 : 0, 0, source), 70L);
            this.pendingCandidateSource = source;
            if (!hasInputQueue() && this.mInlineWord.length() == 0 && this.mIme.isHardKeyboardActive()) {
                UserPreferences userPrefs = UserPreferences.from(this.mIme);
                if (!userPrefs.getBoolean(HardKeyboardManager.SHORTCUT_TO_SELECT_NWP, false)) {
                    userPrefs.setBoolean(HardKeyboardManager.SHORTCUT_TO_SELECT_NWP, true);
                    InputMethodToast.show(this.mIme, getResources().getString(R.string.hardkeyboard_input_next_word_prediction), 0);
                }
            }
        } else if (source == Candidates.Source.CAPS_EDIT) {
            boolean hasPendingUpdates = this.mAlphaInputViewHandler.hasMessages(9);
            if (!hasPendingUpdates && !isEmptyCandidateList()) {
                String candidate = this.suggestionCandidates.getDefaultCandidate().toString();
                if (candidate.length() > 0) {
                    String lowerCase = candidate.toLowerCase();
                    String upperCase = candidate.toUpperCase();
                    char[] shiftedChars = lowerCase.toCharArray();
                    shiftedChars[0] = Character.toUpperCase(shiftedChars[0]);
                    String str = new String(shiftedChars);
                    ArrayList arrayList = new ArrayList();
                    if (getCurrentInputLanguage().isGermanLanguage()) {
                        arrayList.add(str);
                        if (!str.contentEquals(lowerCase)) {
                            arrayList.add(lowerCase);
                        }
                        if (!str.contentEquals(upperCase) && !lowerCase.contentEquals(upperCase)) {
                            arrayList.add(upperCase);
                        }
                    } else {
                        arrayList.add(lowerCase);
                        if (!lowerCase.contentEquals(upperCase)) {
                            arrayList.add(upperCase);
                        }
                        if (!lowerCase.contentEquals(str) && !upperCase.contentEquals(str)) {
                            arrayList.add(str);
                        }
                    }
                    if (arrayList.size() > 1) {
                        setSuggestions(this, arrayList, 0, source);
                        suggestionCount = arrayList.size();
                    }
                }
            } else if (hasPendingUpdates) {
                this.mAlphaInputViewHandler.removeMessages(13);
                this.mAlphaInputViewHandler.sendEmptyMessageDelayed(13, 70L);
                this.pendingCandidateSource = Candidates.Source.CAPS_EDIT;
            }
        } else if (source == Candidates.Source.TAP) {
            if (this.mAlphaInputViewHandler.hasMessages(9)) {
                this.mAlphaInputViewHandler.removeMessages(9);
            }
            this.mAlphaInputViewHandler.sendMessageDelayed(this.mAlphaInputViewHandler.obtainMessage(9, 1, 0, source), 70L);
            this.pendingCandidateSource = Candidates.Source.TAP;
        } else if (source == Candidates.Source.RECAPTURE || source == Candidates.Source.RECAPTURE_BY_TEXT_SELECTION) {
            this.mAlphaInputViewHandler.removeMessages(9);
            this.mAlphaInputViewHandler.sendMessageDelayed(this.mAlphaInputViewHandler.obtainMessage(9, source), 70L);
            this.pendingCandidateSource = source;
        } else {
            this.mAlphaInputViewHandler.removeMessages(9);
            this.mAlphaInputViewHandler.sendMessageDelayed(this.mAlphaInputViewHandler.obtainMessage(9, noComposingText ? 1 : 0, 0, source), 70L);
            this.pendingCandidateSource = source;
        }
        return suggestionCount;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void displaySuggestions(Candidates candidates, boolean noComposingText) {
        Candidates.Source source = Candidates.Source.INVALID;
        if (candidates != null) {
            source = candidates.source();
        }
        if (this.mPreferExplicit && candidates != null && source != Candidates.Source.RECAPTURE) {
            candidates.setDefaultIndex(0);
            candidates.setExactIndex(0);
        }
        int wordCandidatesCount = 0;
        if (candidates != null) {
            wordCandidatesCount = candidates.count();
        }
        InputConnection ic = getCurrentInputConnection();
        if (ic != null) {
            ic.beginBatchEdit();
        }
        if (this.mTextInputPredictionOn) {
            setSuggestions(this, candidates, getWordListFormat(candidates));
        }
        if (noComposingText) {
            if (ic != null) {
                ic.endBatchEdit();
                return;
            }
            return;
        }
        if (wordCandidatesCount > 0) {
            boolean bRefresh = false;
            if (this.mInlineWord.length() == 0) {
                this.mInlineWord.clear();
                this.mInlineWord.clearSpans();
                this.mInlineWord.append(candidates.getExactCandidateString());
                bRefresh = true;
            }
            if (this.mInlineWord.length() > 0) {
                if (ic != null) {
                    if (this.mTextInputPredictionOn) {
                        WordDecorator wd = getWordDecorator();
                        if (bRefresh || wd != null) {
                            this.mInlineWord.clearSpans();
                            if (source != Candidates.Source.TRACE) {
                                this.mInlineWord.setSpan(this.mWordComposeSpan, 0, this.mInlineWord.length(), R.styleable.ThemeTemplate_emojiCategoryIconSmiles);
                            }
                            if (wd != null) {
                                if (source != Candidates.Source.TRACE) {
                                    this.mAlphaInput.isKnownWord(this.mInlineWord.toString());
                                }
                            } else if (getAppSpecificBehavior().shouldSetComosingSpan()) {
                                this.mInlineWord.setSpan(this.mWordComposeSpan, 0, this.mInlineWord.length(), R.styleable.ThemeTemplate_emojiCategoryIconSmiles);
                            }
                            InputConnectionUtils.setComposingText(ic, this.mInlineWord);
                        }
                    } else if (this.mInputFieldInfo.isInputTypeNull()) {
                        sendTextAsKeys(this.mInlineWord);
                    } else {
                        ic.commitText(this.mInlineWord, 1);
                        log.d("cursor displaySuggestions");
                        ic.finishComposingText();
                    }
                }
                this.mEditState.composeWordCandidate();
                if (candidates.source() == Candidates.Source.TRACE) {
                    this.mLastInput = 2;
                }
            }
        } else {
            flushCurrentActiveWord();
            if (source != Candidates.Source.NEXT_WORD_PREDICTION && source == Candidates.Source.TRACE && UserManagerCompat.isUserUnlocked(this.mIme)) {
                setNotMatchToolTipSuggestion();
            }
        }
        if (ic != null) {
            ic.endBatchEdit();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void displaySuggestions(Candidates.Source source, int arg1, int arg2) {
        boolean noComposingText = true;
        if (this.mAlphaInput != null && this.candidatesListView != null && this.mCurrentInputLanguage != null && this.mStarted) {
            if (source == Candidates.Source.RECAPTURE_BY_TEXT_SELECTION || source == Candidates.Source.RECAPTURE) {
                noComposingText = false;
            } else if (arg1 != 1) {
                noComposingText = false;
            }
            displaySuggestions(this.mAlphaInput.getCandidates(source), noComposingText);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.InputView
    public void clearAllKeys() {
        this.mInlineWord.clearSpans();
        this.mInlineWord.clear();
        if (this.mAlphaInput != null) {
            this.mAlphaInput.clearAllKeys();
        }
        updateMultitapDeadkey(true);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.InputView
    public void updateShiftKeyState() {
        updateShiftKeyState(getCurrentInputEditorInfo());
    }

    private void updateShiftKeyState(EditorInfo attr) {
        if (attr != null) {
            if (this.mKeyboardSwitcher.isAlphabetMode()) {
                int caps = 0;
                AppSpecificInputConnection ic = getCurrentInputConnection();
                if (this.mAutoCap && attr != null && attr.inputType != 0) {
                    caps = InputConnectionUtils.getCapsMode(ic, attr);
                }
                Shift.ShiftState currentShiftState = this.mKeyboardSwitcher.getCurrentShiftState();
                if (currentShiftState != Shift.ShiftState.LOCKED) {
                    currentShiftState = capsModeToShiftState(caps);
                }
                setShiftState(currentShiftState);
                return;
            }
            if (this.mKeyboardSwitcher.isSymbolMode() && getShiftState() == Shift.ShiftState.ON && this.mAlphaInput.getShiftState() == Shift.ShiftState.OFF) {
                log.d("updateShiftKeyState...ShiftState.ON");
                this.mAlphaInput.setShiftState(Shift.ShiftState.ON);
            } else if (this.mKeyboardSwitcher.isPhoneMode()) {
                this.mAlphaInput.setShiftState(Shift.ShiftState.OFF);
                log.d("updateShiftKeyState...ShiftState.OFF");
            }
        }
    }

    private void updateNWP() {
        if ((this.suggestionCandidates != null && this.suggestionCandidates.source() == Candidates.Source.NEXT_WORD_PREDICTION) || this.pendingCandidateSource == Candidates.Source.NEXT_WORD_PREDICTION) {
            updateSuggestions(Candidates.Source.NEXT_WORD_PREDICTION);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.InputView
    public void handleShiftKey() {
        if (this.isLongPressed && isExploreByTouchOn()) {
            toggleCapsLock(true);
            this.isLongPressed = false;
        } else {
            this.mKeyboardSwitcher.cycleShiftState();
        }
        if (isExploreByTouchOn()) {
            AccessibilityNotification.getInstance().speakShiftState(getContext(), getShiftState(), getKeyboardLayer());
        }
        UsageManager.KeyboardUsageScribe keyboardScribe = UsageManager.getKeyboardUsageScribe(getContext());
        if (keyboardScribe != null) {
            keyboardScribe.recordShiftState(this.mKeyboardSwitcher.getCurrentShiftState());
        }
        if (getShiftState() == Shift.ShiftState.ON) {
            this.isManualShift = true;
        }
        log.d("handleShiftKey...shiftState: ", getShiftState());
        this.mAlphaInput.setShiftState(getShiftState());
        if (this.mKeyboardSwitcher.isAlphabetMode()) {
            updateNWP();
        }
        invalidateKeyboardImage();
    }

    @Override // com.nuance.swype.input.InputView, com.nuance.swype.input.AbstractTapDetector.TapHandler
    public boolean onSingleTap(boolean shouldResyncCache, boolean orientationChanged) {
        updateShiftKeyState();
        return super.onSingleTap(shouldResyncCache, orientationChanged);
    }

    @Override // com.nuance.swype.input.KeyboardViewEx
    public boolean setShiftState(Shift.ShiftState shiftState) {
        log.d("setShiftState...shiftState: ", shiftState.toString());
        this.isManualShift = false;
        super.setShiftState(shiftState);
        this.mAlphaInput.setShiftState(getShiftState());
        return true;
    }

    private boolean toggleCapsLock(boolean overrideKeyLockableFlag) {
        Shift.ShiftState currentShiftState;
        if (!this.mKeyboardSwitcher.isAlphabetMode()) {
            return false;
        }
        if (this.mKeyboardSwitcher.getCurrentShiftState() == Shift.ShiftState.LOCKED) {
            currentShiftState = Shift.ShiftState.OFF;
        } else {
            currentShiftState = Shift.ShiftState.LOCKED;
        }
        this.mKeyboardSwitcher.setShiftState(currentShiftState, overrideKeyLockableFlag);
        log.d("toggleCapsLock...shiftState: ", getShiftState());
        this.mAlphaInput.setShiftState(getShiftState());
        UsageManager.KeyboardUsageScribe keyboardScribe = UsageManager.getKeyboardUsageScribe(getContext());
        if (keyboardScribe != null) {
            keyboardScribe.recordShiftState(this.mKeyboardSwitcher.getCurrentShiftState());
        }
        updateNWP();
        return true;
    }

    private boolean announceToggleCapsLock() {
        if (!isExploreByTouchOn() || !this.mKeyboardSwitcher.isAlphabetMode()) {
            return false;
        }
        this.isLongPressed = true;
        Shift.ShiftState currentShiftState = this.mKeyboardSwitcher.getCurrentShiftState();
        CharSequence accessibilityLabel = KeyboardModel.getInstance().getAccessibilityLabel().getLongPressAccessibilityLabelForShiftKey(currentShiftState, this.mKeyboardSwitcher.currentKeyboardLayer());
        if (accessibilityLabel != null) {
            AccessibilityNotification.getInstance().announceNotification(getContext(), accessibilityLabel.toString(), true);
        }
        return true;
    }

    private boolean handleShiftPressedHold() {
        if (!currentLanguageSupportsCapitalization()) {
            return false;
        }
        if (isExploreByTouchOn()) {
            return announceToggleCapsLock();
        }
        if (!toggleCapsLock(true)) {
            return false;
        }
        invalidateKeyboardImage();
        return true;
    }

    public AlphaInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mWordComposeSpan = new UnderlineSpan();
        this.isManualShift = false;
        this.recaptureEditWord = new RecaptureEditWord();
        this.updateSelectionCallback = this.recaptureEditWord;
        this.exactType = new StringBuilder();
        this.isLongPressed = false;
        this.oovWordHistory = new OOVWordHistory();
        this.mInlineWord = new SpannableStringBuilder();
        this.alphaInputViewCallback = new Handler.Callback() { // from class: com.nuance.swype.input.AlphaInputView.1
            @Override // android.os.Handler.Callback
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case 5:
                        QuickToast.show(AlphaInputView.this.getContext(), AlphaInputView.this.getResources().getString(R.string.multitap_toggle_tip), 1, AlphaInputView.this.getHeight() + AlphaInputView.this.wordListViewContainer.getHeight());
                        break;
                    case 8:
                        AlphaInputView.this.showStartOfWordCandidateList();
                        break;
                    case 9:
                        AlphaInputView.this.pendingCandidateSource = Candidates.Source.INVALID;
                        AlphaInputView.this.displaySuggestions((Candidates.Source) msg.obj, msg.arg1, msg.arg2);
                        break;
                    case 11:
                        AlphaInputView.this.setSpeechViewHost();
                        AlphaInputView.this.resumeSpeech();
                        break;
                    case 13:
                        AlphaInputView.this.updateSuggestions(Candidates.Source.CAPS_EDIT);
                        break;
                    case 15:
                        if (UserManagerCompat.isUserUnlocked(AlphaInputView.this.mIme)) {
                            AlphaInputView.this.startInputSession();
                            if (msg.arg2 == 0) {
                                AlphaInputView.this.setMultitapOrAmbigMode();
                                break;
                            }
                        }
                        break;
                    case 16:
                        if (!AlphaInputView.this.mIme.isHardKeyboardActive()) {
                            AlphaInputView.this.mIme.recaptureOnSingleTap(msg.arg1 == 1, msg.arg2 == 1);
                            break;
                        }
                        break;
                    case 125:
                        AlphaInputView.this.themeStoreWclPrompt.showMessage("alphaInput");
                        break;
                    default:
                        return false;
                }
                return true;
            }
        };
        this.mAlphaInputViewHandler = WeakReferenceHandler.create(this.alphaInputViewCallback);
        this.mPreTraceShiftedState = Shift.ShiftState.OFF;
        this.wordRecaptureWrapper = new WordRecaptureWrapper();
        this.candidateBuilderResult = new CandidatesBuilderHandler.CandidatesBuilderResult() { // from class: com.nuance.swype.input.AlphaInputView.2
            private boolean shouldRemovePreviousAutoSpace(Candidates candidates) {
                WordCandidate exactCandidate = candidates.getExactCandidate();
                if (exactCandidate != null) {
                    return ((AlphaInputView.this.gestureHandler.isCustomGesture(exactCandidate) && ((short) exactCandidate.toString().charAt(0)) != 2899) || (exactCandidate.isDefault() && exactCandidate.shouldRemoveSpaceBefore())) && AlphaInputView.this.inputContextRequestHandler.lastAutoSpaceInserted;
                }
                return false;
            }

            @Override // com.nuance.swype.input.keyboard.CandidatesBuilderHandler.CandidatesBuilderResult
            public void onBuildResult(Candidates candidates, Object parameter) {
                AppSpecificInputConnection ic;
                if (shouldRemovePreviousAutoSpace(candidates)) {
                    AlphaInputView.this.inputContextRequestHandler.removeSpaceBeforeCursor();
                    AlphaInputView.this.wordRecaptureWrapper.onText("", AlphaInputView.this.mIme.getRecaptureHandler());
                }
                WordCandidate exactCandidate = candidates.getExactCandidate();
                if (exactCandidate != null && AlphaInputView.this.gestureHandler.isGesture(exactCandidate)) {
                    if (AlphaInputView.this.gestureHandler.isCustomGesture(exactCandidate)) {
                        AlphaInputView.this.handleGesture(candidates);
                    } else {
                        AlphaInputView.this.handleGesture(candidates);
                        AlphaInputView.this.inputContextRequestHandler.setLastAutoSpaceInserted();
                    }
                } else {
                    if (candidates.source() == Candidates.Source.TAP && (ic = AlphaInputView.this.getCurrentInputConnection()) != null) {
                        AlphaInputView.this.displayExactTypeAsInline(null, ic);
                    }
                    AlphaInputView.this.displaySuggestions(candidates, candidates.source() == Candidates.Source.NEXT_WORD_PREDICTION);
                }
                AlphaInputView.this.updateShiftKeyState();
            }
        };
        this.inputContextRequestHandler = new InputContextRequestHandler();
        this.touchKeyActionHandler = new KeyboardTouchEventDispatcher.TouchKeyActionHandler() { // from class: com.nuance.swype.input.AlphaInputView.3
            char[] functionKey = new char[1];
            SparseBooleanArray canceledKeys = new SparseBooleanArray();
            final SparseIntArray keyDownIndices = new SparseIntArray();

            private void setCancelKey(int pointerId, boolean canceled) {
                this.canceledKeys.put(pointerId, canceled);
            }

            private boolean isKeyCanceled(int pointerId) {
                return this.canceledKeys.get(pointerId, false);
            }

            char getCaseLetter(char letter, KeyboardEx.Key key) {
                return AlphaInputView.this.mCurrentInputLanguage.isLatinLanguage() ? AlphaInputView.this.isShifted() ? Character.toUpperCase(letter) : letter : AlphaInputView.this.isShifted() ? (char) AlphaInputView.this.getKeycode(key) : letter;
            }

            @Override // com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher.TouchKeyActionHandler
            public void touchStarted(int pointerId, int keyIndex, KeyType keyType, float x, float y, int eventTime) {
                AlphaInputView.this.pressKey(AlphaInputView.this.mKeys, keyIndex);
                AlphaInputView.this.setKeyState(keyIndex, KeyboardViewEx.ShowKeyState.Pressed);
                AlphaInputView.this.showPreviewKey(keyIndex, pointerId);
                AlphaInputView.this.resetTrace(pointerId);
                this.keyDownIndices.put(pointerId, keyIndex);
                setCancelKey(pointerId, false);
                AlphaInputView.this.onTouchStarted(pointerId, keyIndex, x, y, eventTime);
            }

            @Override // com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher.TouchKeyActionHandler
            public void touchMoved(int pointerId, int keyIndex, float[] xcoords, float[] ycoords, int[] times, boolean canBeTraced) {
                if (UserManagerCompat.isUserUnlocked(AlphaInputView.this.mIme)) {
                    if (canBeTraced || (!AlphaInputView.this.mAlphaInput.isTraceEnabled() && this.keyDownIndices.get(pointerId) != keyIndex)) {
                        AlphaInputView.this.releaseKey(AlphaInputView.this.mKeys, this.keyDownIndices.get(pointerId), false);
                        AlphaInputView.this.hideKeyPreview(pointerId);
                        this.keyDownIndices.put(pointerId, keyIndex);
                    }
                    AlphaInputView.this.onTouchMoved(pointerId, xcoords, ycoords, times, canBeTraced, keyIndex);
                }
            }

            @Override // com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher.TouchKeyActionHandler
            public void touchEnded(int pointerId, int keyIndex, KeyType keyType, boolean isTraced, boolean quickPrssed, float x, float y, int eventTime) {
                int i;
                boolean touchCanceled = isKeyCanceled(pointerId);
                setCancelKey(pointerId, false);
                AlphaInputView.this.releaseKey(AlphaInputView.this.mKeys, this.keyDownIndices.get(pointerId, keyIndex), true);
                AlphaInputView.this.hideKeyPreview(pointerId);
                AlphaInputView.this.setKeyState(-1, KeyboardViewEx.ShowKeyState.Released);
                if (!touchCanceled) {
                    AlphaInputView.this.wordRecaptureWrapper.removePendingRecaptureMessage(AlphaInputView.this.mIme.getRecaptureHandler());
                    KeyboardEx.Key key = AlphaInputView.this.getKey(keyIndex);
                    if (isTraced) {
                        KeyboardViewEx.log.d("> handling trace");
                        if (AlphaInputView.this.suggestionCandidates != null && AlphaInputView.this.suggestionCandidates.source() == Candidates.Source.RECAPTURE_BY_TEXT_SELECTION) {
                            AlphaInputView.this.clearSuggestions();
                            AlphaInputView.this.mAlphaInput.clearAllKeys();
                            AlphaInputView.this.mLastInput = 4;
                        }
                        AlphaInputView.this.autoSpace(keyType, AlphaInputView.this.mLastInput, true);
                        if (AlphaInputView.this.mAlphaInput.processStoredTouch(-1, this.functionKey)) {
                            AlphaInputView.this.handleTrace(null);
                        }
                    } else {
                        char charKey = keyType.isLetter() ? getCaseLetter((char) key.codes[0], key) : (char) AlphaInputView.this.getKeycode(key);
                        boolean isFunctionKey = false;
                        if (AlphaInputView.this.mCurrentInputLanguage.isNonSpacedLanguage() && (!keyType.isFunctionKey() || key.codes[0] == 32)) {
                            Shift.ShiftState savedShiftState = AlphaInputView.this.getShiftState();
                            if (AlphaInputView.this.clearSelectionKeys()) {
                                AlphaInputView.this.clearSuggestions();
                            }
                            AlphaInputView.this.setShiftState(savedShiftState);
                        }
                        if (keyType.isFunctionKey()) {
                            isFunctionKey = true;
                            if (AlphaInputView.this.mAlphaInput.processStoredTouch(-1, this.functionKey)) {
                                isFunctionKey = this.functionKey[0] != 0;
                            }
                        }
                        if (!isFunctionKey) {
                            AlphaInputView.this.autoSpace(keyType, AlphaInputView.this.mLastInput, false);
                            AlphaInputView.this.autospaceHandler.onCharKey(charKey);
                            Shift.ShiftState savedShiftState2 = AlphaInputView.this.getShiftState();
                            if (AlphaInputView.this.wordRecaptureWrapper.onCharKey(charKey, AlphaInputView.this.mIme.getRecaptureHandler())) {
                                AlphaInputView.this.setShiftState(savedShiftState2);
                            }
                        } else {
                            AlphaInputView.this.autospaceHandler.onKey(key.codes[0]);
                        }
                        if (isFunctionKey) {
                            KeyboardViewEx.log.d("> handling fn key");
                            AlphaInputView.this.handleFunctionKey(key, quickPrssed, 0);
                        } else if (!keyType.isLetter() && !AlphaInputView.this.mAlphaInput.hasActiveInput()) {
                            if (keyType.isString()) {
                                KeyboardViewEx.log.d("> handling text");
                                AlphaInputView.this.onText(key.text, 0L);
                            } else {
                                KeyboardViewEx.log.d("> handling punct or symbol");
                                AlphaInputView.this.handlePunctOrSymbol(charKey);
                            }
                        } else {
                            KeyboardViewEx.log.d("shift state before processStoredTouch: ", AlphaInputView.this.mAlphaInput.getShiftState());
                            if (AlphaInputView.this.mAlphaInput.processStoredTouch(-1, this.functionKey)) {
                                KeyboardViewEx.log.d("shift state after processStoredTouch: ", AlphaInputView.this.mAlphaInput.getShiftState());
                                if (AlphaInputView.this.mTextInputPredictionOn) {
                                    if (keyType.isString()) {
                                        KeyboardViewEx.log.d("> handling text when prediction on");
                                        AlphaInputView.this.onText(key.text, 0L);
                                    } else {
                                        KeyboardViewEx.log.d("> handling prediction");
                                        if (AlphaInputView.this.isCursorWithinWord()) {
                                            AlphaInputView.this.mAlphaInput.clearAllKeys();
                                            AlphaInputView.this.inputContextRequestHandler.commitText(String.valueOf(charKey));
                                        } else {
                                            AlphaInputView.this.handlePrediction(charKey);
                                        }
                                    }
                                } else {
                                    KeyboardViewEx.log.d("> handling text when prediction off");
                                    StringBuilder exactType = new StringBuilder();
                                    AlphaInputView.this.mAlphaInput.getExactType(exactType);
                                    AlphaInputView.this.mAlphaInput.clearAllKeys();
                                    AlphaInputView.this.inputContextRequestHandler.commitText(exactType);
                                    AlphaInputView.this.updateShiftKeyState();
                                }
                            }
                        }
                        if (AlphaInputView.this.charUtils.isWordCompounder((char) key.codes[0]) && !AlphaInputView.this.mKeyboardSwitcher.isAlphabetMode() && AlphaInputView.this.mKeyboardSwitcher.supportsAlphaMode()) {
                            AlphaInputView.this.setKeyboardLayer(KeyboardEx.KeyboardLayerType.KEYBOARD_TEXT);
                        }
                    }
                    AlphaInputView alphaInputView = AlphaInputView.this;
                    if (isTraced) {
                        i = 2;
                    } else {
                        i = !keyType.isFunctionKey() ? 1 : AlphaInputView.this.mLastInput;
                    }
                    alphaInputView.mLastInput = i;
                }
                AlphaInputView.this.onTouchEnded(pointerId, keyIndex, x, y, eventTime);
                if (isTraced) {
                    AlphaInputView.this.finishTrace(pointerId);
                }
                this.keyDownIndices.put(pointerId, -1);
            }

            @Override // com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher.TouchKeyActionHandler
            public void touchCanceled(int pointerId, int keyIndex) {
                setCancelKey(pointerId, true);
                AlphaInputView.this.resetTrace(pointerId);
                if (keyIndex == -1) {
                    int keyIndex2 = this.keyDownIndices.get(pointerId, -1);
                    if (keyIndex2 != -1) {
                        AlphaInputView.this.releaseKey(AlphaInputView.this.mKeys, keyIndex2, true);
                    }
                    AlphaInputView.this.hideKeyPreview(pointerId);
                    AlphaInputView.this.setKeyState(-1, KeyboardViewEx.ShowKeyState.Released);
                }
            }

            @Override // com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher.TouchKeyActionHandler
            public boolean touchHeld(int pointerId, int keyIndex, KeyType keyType) {
                return AlphaInputView.this.onShortPress(AlphaInputView.this.getKey(keyIndex), keyIndex, pointerId);
            }

            @Override // com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher.TouchKeyActionHandler
            public boolean touchHeldRepeat(int pointerId, int keyIndex, KeyType keyType, int repeatCount) {
                if (AlphaInputView.this.mIme == null || !keyType.isFunctionKey()) {
                    return false;
                }
                KeyboardEx.Key key = AlphaInputView.this.getKey(keyIndex);
                if (key.codes[0] == 8) {
                    if (AlphaInputView.this.mCurrentInputLanguage.isNonSpacedLanguage()) {
                        return AlphaInputView.this.handleBackspace(repeatCount);
                    }
                    return AlphaInputView.this.handleDeleteWordBack(key);
                }
                if (!KeyboardEx.isArrowKeys(key.codes[0])) {
                    return false;
                }
                AlphaInputView.this.handleFunctionKey(key, false, repeatCount);
                return true;
            }

            @Override // com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher.TouchKeyActionHandler
            public void touchHeldMove(int pointerId, int keyIndex, float[] xcoords, float[] ycoords, int[] times) {
                AlphaInputView.this.onTouchHeldMoved(pointerId, xcoords, ycoords, times);
            }

            @Override // com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher.TouchKeyActionHandler
            public void touchHeldEnded(int pointerId, int keyIndex, KeyType keyType) {
                KeyboardEx.Key key = AlphaInputView.this.getKey(keyIndex);
                AlphaInputView.this.onTouchHeldEnded(pointerId, key);
                if (this.canceledKeys.get(pointerId, false) && KeyboardEx.isShiftKey(key.codes[0])) {
                    AlphaInputView.this.releaseKey(AlphaInputView.this.mKeys, keyIndex, false);
                } else {
                    AlphaInputView.this.releaseKey(AlphaInputView.this.mKeys, keyIndex, true);
                }
                AlphaInputView.this.hideKeyPreview(pointerId);
                AlphaInputView.this.setKeyState(-1, KeyboardViewEx.ShowKeyState.Released);
                this.canceledKeys.put(pointerId, false);
            }

            @Override // com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher.TouchKeyActionHandler
            public void touchHelpRepeatEnded(int pointerId, int keyIndex, KeyType keyType) {
                AlphaInputView.this.releaseKey(AlphaInputView.this.mKeys, keyIndex, true);
                AlphaInputView.this.mIme.releaseRepeatKey();
                AlphaInputView.this.hideKeyPreview(pointerId);
                AlphaInputView.this.setKeyState(-1, KeyboardViewEx.ShowKeyState.Released);
                this.canceledKeys.put(pointerId, false);
            }

            @Override // com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher.TouchKeyActionHandler
            public void multiTapTimerTimeOut() {
                AlphaInputView.this.mInMultiTap = false;
            }

            @Override // com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher.TouchKeyActionHandler
            public void handleKeyboardShiftState(float y) {
                AlphaInputView.this.movePointer(y);
            }

            @Override // com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher.TouchKeyActionHandler
            public void multiTapTimerTimeoutActive() {
                AlphaInputView.this.mInMultiTap = true;
            }
        };
    }

    public AlphaInputView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mWordComposeSpan = new UnderlineSpan();
        this.isManualShift = false;
        this.recaptureEditWord = new RecaptureEditWord();
        this.updateSelectionCallback = this.recaptureEditWord;
        this.exactType = new StringBuilder();
        this.isLongPressed = false;
        this.oovWordHistory = new OOVWordHistory();
        this.mInlineWord = new SpannableStringBuilder();
        this.alphaInputViewCallback = new Handler.Callback() { // from class: com.nuance.swype.input.AlphaInputView.1
            @Override // android.os.Handler.Callback
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case 5:
                        QuickToast.show(AlphaInputView.this.getContext(), AlphaInputView.this.getResources().getString(R.string.multitap_toggle_tip), 1, AlphaInputView.this.getHeight() + AlphaInputView.this.wordListViewContainer.getHeight());
                        break;
                    case 8:
                        AlphaInputView.this.showStartOfWordCandidateList();
                        break;
                    case 9:
                        AlphaInputView.this.pendingCandidateSource = Candidates.Source.INVALID;
                        AlphaInputView.this.displaySuggestions((Candidates.Source) msg.obj, msg.arg1, msg.arg2);
                        break;
                    case 11:
                        AlphaInputView.this.setSpeechViewHost();
                        AlphaInputView.this.resumeSpeech();
                        break;
                    case 13:
                        AlphaInputView.this.updateSuggestions(Candidates.Source.CAPS_EDIT);
                        break;
                    case 15:
                        if (UserManagerCompat.isUserUnlocked(AlphaInputView.this.mIme)) {
                            AlphaInputView.this.startInputSession();
                            if (msg.arg2 == 0) {
                                AlphaInputView.this.setMultitapOrAmbigMode();
                                break;
                            }
                        }
                        break;
                    case 16:
                        if (!AlphaInputView.this.mIme.isHardKeyboardActive()) {
                            AlphaInputView.this.mIme.recaptureOnSingleTap(msg.arg1 == 1, msg.arg2 == 1);
                            break;
                        }
                        break;
                    case 125:
                        AlphaInputView.this.themeStoreWclPrompt.showMessage("alphaInput");
                        break;
                    default:
                        return false;
                }
                return true;
            }
        };
        this.mAlphaInputViewHandler = WeakReferenceHandler.create(this.alphaInputViewCallback);
        this.mPreTraceShiftedState = Shift.ShiftState.OFF;
        this.wordRecaptureWrapper = new WordRecaptureWrapper();
        this.candidateBuilderResult = new CandidatesBuilderHandler.CandidatesBuilderResult() { // from class: com.nuance.swype.input.AlphaInputView.2
            private boolean shouldRemovePreviousAutoSpace(Candidates candidates) {
                WordCandidate exactCandidate = candidates.getExactCandidate();
                if (exactCandidate != null) {
                    return ((AlphaInputView.this.gestureHandler.isCustomGesture(exactCandidate) && ((short) exactCandidate.toString().charAt(0)) != 2899) || (exactCandidate.isDefault() && exactCandidate.shouldRemoveSpaceBefore())) && AlphaInputView.this.inputContextRequestHandler.lastAutoSpaceInserted;
                }
                return false;
            }

            @Override // com.nuance.swype.input.keyboard.CandidatesBuilderHandler.CandidatesBuilderResult
            public void onBuildResult(Candidates candidates, Object parameter) {
                AppSpecificInputConnection ic;
                if (shouldRemovePreviousAutoSpace(candidates)) {
                    AlphaInputView.this.inputContextRequestHandler.removeSpaceBeforeCursor();
                    AlphaInputView.this.wordRecaptureWrapper.onText("", AlphaInputView.this.mIme.getRecaptureHandler());
                }
                WordCandidate exactCandidate = candidates.getExactCandidate();
                if (exactCandidate != null && AlphaInputView.this.gestureHandler.isGesture(exactCandidate)) {
                    if (AlphaInputView.this.gestureHandler.isCustomGesture(exactCandidate)) {
                        AlphaInputView.this.handleGesture(candidates);
                    } else {
                        AlphaInputView.this.handleGesture(candidates);
                        AlphaInputView.this.inputContextRequestHandler.setLastAutoSpaceInserted();
                    }
                } else {
                    if (candidates.source() == Candidates.Source.TAP && (ic = AlphaInputView.this.getCurrentInputConnection()) != null) {
                        AlphaInputView.this.displayExactTypeAsInline(null, ic);
                    }
                    AlphaInputView.this.displaySuggestions(candidates, candidates.source() == Candidates.Source.NEXT_WORD_PREDICTION);
                }
                AlphaInputView.this.updateShiftKeyState();
            }
        };
        this.inputContextRequestHandler = new InputContextRequestHandler();
        this.touchKeyActionHandler = new KeyboardTouchEventDispatcher.TouchKeyActionHandler() { // from class: com.nuance.swype.input.AlphaInputView.3
            char[] functionKey = new char[1];
            SparseBooleanArray canceledKeys = new SparseBooleanArray();
            final SparseIntArray keyDownIndices = new SparseIntArray();

            private void setCancelKey(int pointerId, boolean canceled) {
                this.canceledKeys.put(pointerId, canceled);
            }

            private boolean isKeyCanceled(int pointerId) {
                return this.canceledKeys.get(pointerId, false);
            }

            char getCaseLetter(char letter, KeyboardEx.Key key) {
                return AlphaInputView.this.mCurrentInputLanguage.isLatinLanguage() ? AlphaInputView.this.isShifted() ? Character.toUpperCase(letter) : letter : AlphaInputView.this.isShifted() ? (char) AlphaInputView.this.getKeycode(key) : letter;
            }

            @Override // com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher.TouchKeyActionHandler
            public void touchStarted(int pointerId, int keyIndex, KeyType keyType, float x, float y, int eventTime) {
                AlphaInputView.this.pressKey(AlphaInputView.this.mKeys, keyIndex);
                AlphaInputView.this.setKeyState(keyIndex, KeyboardViewEx.ShowKeyState.Pressed);
                AlphaInputView.this.showPreviewKey(keyIndex, pointerId);
                AlphaInputView.this.resetTrace(pointerId);
                this.keyDownIndices.put(pointerId, keyIndex);
                setCancelKey(pointerId, false);
                AlphaInputView.this.onTouchStarted(pointerId, keyIndex, x, y, eventTime);
            }

            @Override // com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher.TouchKeyActionHandler
            public void touchMoved(int pointerId, int keyIndex, float[] xcoords, float[] ycoords, int[] times, boolean canBeTraced) {
                if (UserManagerCompat.isUserUnlocked(AlphaInputView.this.mIme)) {
                    if (canBeTraced || (!AlphaInputView.this.mAlphaInput.isTraceEnabled() && this.keyDownIndices.get(pointerId) != keyIndex)) {
                        AlphaInputView.this.releaseKey(AlphaInputView.this.mKeys, this.keyDownIndices.get(pointerId), false);
                        AlphaInputView.this.hideKeyPreview(pointerId);
                        this.keyDownIndices.put(pointerId, keyIndex);
                    }
                    AlphaInputView.this.onTouchMoved(pointerId, xcoords, ycoords, times, canBeTraced, keyIndex);
                }
            }

            @Override // com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher.TouchKeyActionHandler
            public void touchEnded(int pointerId, int keyIndex, KeyType keyType, boolean isTraced, boolean quickPrssed, float x, float y, int eventTime) {
                int i;
                boolean touchCanceled = isKeyCanceled(pointerId);
                setCancelKey(pointerId, false);
                AlphaInputView.this.releaseKey(AlphaInputView.this.mKeys, this.keyDownIndices.get(pointerId, keyIndex), true);
                AlphaInputView.this.hideKeyPreview(pointerId);
                AlphaInputView.this.setKeyState(-1, KeyboardViewEx.ShowKeyState.Released);
                if (!touchCanceled) {
                    AlphaInputView.this.wordRecaptureWrapper.removePendingRecaptureMessage(AlphaInputView.this.mIme.getRecaptureHandler());
                    KeyboardEx.Key key = AlphaInputView.this.getKey(keyIndex);
                    if (isTraced) {
                        KeyboardViewEx.log.d("> handling trace");
                        if (AlphaInputView.this.suggestionCandidates != null && AlphaInputView.this.suggestionCandidates.source() == Candidates.Source.RECAPTURE_BY_TEXT_SELECTION) {
                            AlphaInputView.this.clearSuggestions();
                            AlphaInputView.this.mAlphaInput.clearAllKeys();
                            AlphaInputView.this.mLastInput = 4;
                        }
                        AlphaInputView.this.autoSpace(keyType, AlphaInputView.this.mLastInput, true);
                        if (AlphaInputView.this.mAlphaInput.processStoredTouch(-1, this.functionKey)) {
                            AlphaInputView.this.handleTrace(null);
                        }
                    } else {
                        char charKey = keyType.isLetter() ? getCaseLetter((char) key.codes[0], key) : (char) AlphaInputView.this.getKeycode(key);
                        boolean isFunctionKey = false;
                        if (AlphaInputView.this.mCurrentInputLanguage.isNonSpacedLanguage() && (!keyType.isFunctionKey() || key.codes[0] == 32)) {
                            Shift.ShiftState savedShiftState = AlphaInputView.this.getShiftState();
                            if (AlphaInputView.this.clearSelectionKeys()) {
                                AlphaInputView.this.clearSuggestions();
                            }
                            AlphaInputView.this.setShiftState(savedShiftState);
                        }
                        if (keyType.isFunctionKey()) {
                            isFunctionKey = true;
                            if (AlphaInputView.this.mAlphaInput.processStoredTouch(-1, this.functionKey)) {
                                isFunctionKey = this.functionKey[0] != 0;
                            }
                        }
                        if (!isFunctionKey) {
                            AlphaInputView.this.autoSpace(keyType, AlphaInputView.this.mLastInput, false);
                            AlphaInputView.this.autospaceHandler.onCharKey(charKey);
                            Shift.ShiftState savedShiftState2 = AlphaInputView.this.getShiftState();
                            if (AlphaInputView.this.wordRecaptureWrapper.onCharKey(charKey, AlphaInputView.this.mIme.getRecaptureHandler())) {
                                AlphaInputView.this.setShiftState(savedShiftState2);
                            }
                        } else {
                            AlphaInputView.this.autospaceHandler.onKey(key.codes[0]);
                        }
                        if (isFunctionKey) {
                            KeyboardViewEx.log.d("> handling fn key");
                            AlphaInputView.this.handleFunctionKey(key, quickPrssed, 0);
                        } else if (!keyType.isLetter() && !AlphaInputView.this.mAlphaInput.hasActiveInput()) {
                            if (keyType.isString()) {
                                KeyboardViewEx.log.d("> handling text");
                                AlphaInputView.this.onText(key.text, 0L);
                            } else {
                                KeyboardViewEx.log.d("> handling punct or symbol");
                                AlphaInputView.this.handlePunctOrSymbol(charKey);
                            }
                        } else {
                            KeyboardViewEx.log.d("shift state before processStoredTouch: ", AlphaInputView.this.mAlphaInput.getShiftState());
                            if (AlphaInputView.this.mAlphaInput.processStoredTouch(-1, this.functionKey)) {
                                KeyboardViewEx.log.d("shift state after processStoredTouch: ", AlphaInputView.this.mAlphaInput.getShiftState());
                                if (AlphaInputView.this.mTextInputPredictionOn) {
                                    if (keyType.isString()) {
                                        KeyboardViewEx.log.d("> handling text when prediction on");
                                        AlphaInputView.this.onText(key.text, 0L);
                                    } else {
                                        KeyboardViewEx.log.d("> handling prediction");
                                        if (AlphaInputView.this.isCursorWithinWord()) {
                                            AlphaInputView.this.mAlphaInput.clearAllKeys();
                                            AlphaInputView.this.inputContextRequestHandler.commitText(String.valueOf(charKey));
                                        } else {
                                            AlphaInputView.this.handlePrediction(charKey);
                                        }
                                    }
                                } else {
                                    KeyboardViewEx.log.d("> handling text when prediction off");
                                    StringBuilder exactType = new StringBuilder();
                                    AlphaInputView.this.mAlphaInput.getExactType(exactType);
                                    AlphaInputView.this.mAlphaInput.clearAllKeys();
                                    AlphaInputView.this.inputContextRequestHandler.commitText(exactType);
                                    AlphaInputView.this.updateShiftKeyState();
                                }
                            }
                        }
                        if (AlphaInputView.this.charUtils.isWordCompounder((char) key.codes[0]) && !AlphaInputView.this.mKeyboardSwitcher.isAlphabetMode() && AlphaInputView.this.mKeyboardSwitcher.supportsAlphaMode()) {
                            AlphaInputView.this.setKeyboardLayer(KeyboardEx.KeyboardLayerType.KEYBOARD_TEXT);
                        }
                    }
                    AlphaInputView alphaInputView = AlphaInputView.this;
                    if (isTraced) {
                        i = 2;
                    } else {
                        i = !keyType.isFunctionKey() ? 1 : AlphaInputView.this.mLastInput;
                    }
                    alphaInputView.mLastInput = i;
                }
                AlphaInputView.this.onTouchEnded(pointerId, keyIndex, x, y, eventTime);
                if (isTraced) {
                    AlphaInputView.this.finishTrace(pointerId);
                }
                this.keyDownIndices.put(pointerId, -1);
            }

            @Override // com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher.TouchKeyActionHandler
            public void touchCanceled(int pointerId, int keyIndex) {
                setCancelKey(pointerId, true);
                AlphaInputView.this.resetTrace(pointerId);
                if (keyIndex == -1) {
                    int keyIndex2 = this.keyDownIndices.get(pointerId, -1);
                    if (keyIndex2 != -1) {
                        AlphaInputView.this.releaseKey(AlphaInputView.this.mKeys, keyIndex2, true);
                    }
                    AlphaInputView.this.hideKeyPreview(pointerId);
                    AlphaInputView.this.setKeyState(-1, KeyboardViewEx.ShowKeyState.Released);
                }
            }

            @Override // com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher.TouchKeyActionHandler
            public boolean touchHeld(int pointerId, int keyIndex, KeyType keyType) {
                return AlphaInputView.this.onShortPress(AlphaInputView.this.getKey(keyIndex), keyIndex, pointerId);
            }

            @Override // com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher.TouchKeyActionHandler
            public boolean touchHeldRepeat(int pointerId, int keyIndex, KeyType keyType, int repeatCount) {
                if (AlphaInputView.this.mIme == null || !keyType.isFunctionKey()) {
                    return false;
                }
                KeyboardEx.Key key = AlphaInputView.this.getKey(keyIndex);
                if (key.codes[0] == 8) {
                    if (AlphaInputView.this.mCurrentInputLanguage.isNonSpacedLanguage()) {
                        return AlphaInputView.this.handleBackspace(repeatCount);
                    }
                    return AlphaInputView.this.handleDeleteWordBack(key);
                }
                if (!KeyboardEx.isArrowKeys(key.codes[0])) {
                    return false;
                }
                AlphaInputView.this.handleFunctionKey(key, false, repeatCount);
                return true;
            }

            @Override // com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher.TouchKeyActionHandler
            public void touchHeldMove(int pointerId, int keyIndex, float[] xcoords, float[] ycoords, int[] times) {
                AlphaInputView.this.onTouchHeldMoved(pointerId, xcoords, ycoords, times);
            }

            @Override // com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher.TouchKeyActionHandler
            public void touchHeldEnded(int pointerId, int keyIndex, KeyType keyType) {
                KeyboardEx.Key key = AlphaInputView.this.getKey(keyIndex);
                AlphaInputView.this.onTouchHeldEnded(pointerId, key);
                if (this.canceledKeys.get(pointerId, false) && KeyboardEx.isShiftKey(key.codes[0])) {
                    AlphaInputView.this.releaseKey(AlphaInputView.this.mKeys, keyIndex, false);
                } else {
                    AlphaInputView.this.releaseKey(AlphaInputView.this.mKeys, keyIndex, true);
                }
                AlphaInputView.this.hideKeyPreview(pointerId);
                AlphaInputView.this.setKeyState(-1, KeyboardViewEx.ShowKeyState.Released);
                this.canceledKeys.put(pointerId, false);
            }

            @Override // com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher.TouchKeyActionHandler
            public void touchHelpRepeatEnded(int pointerId, int keyIndex, KeyType keyType) {
                AlphaInputView.this.releaseKey(AlphaInputView.this.mKeys, keyIndex, true);
                AlphaInputView.this.mIme.releaseRepeatKey();
                AlphaInputView.this.hideKeyPreview(pointerId);
                AlphaInputView.this.setKeyState(-1, KeyboardViewEx.ShowKeyState.Released);
                this.canceledKeys.put(pointerId, false);
            }

            @Override // com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher.TouchKeyActionHandler
            public void multiTapTimerTimeOut() {
                AlphaInputView.this.mInMultiTap = false;
            }

            @Override // com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher.TouchKeyActionHandler
            public void handleKeyboardShiftState(float y) {
                AlphaInputView.this.movePointer(y);
            }

            @Override // com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher.TouchKeyActionHandler
            public void multiTapTimerTimeoutActive() {
                AlphaInputView.this.mInMultiTap = true;
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.InputView, com.nuance.swype.input.KeyboardViewEx
    public boolean handleLongPress(KeyboardEx.Key key) {
        if (KeyboardEx.isShiftKey(key.codes[0]) && handleShiftPressedHold()) {
            return true;
        }
        if (8 == key.codes[0]) {
            return handleDeleteWordBack(key);
        }
        return super.handleLongPress(key);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.KeyboardViewEx
    public boolean isTraceInputEnabled() {
        return this.isTraceEnabledOnKeyboard;
    }

    @Override // com.nuance.swype.input.InputView, com.nuance.swype.input.KeyboardViewEx
    public void setKeyboard(KeyboardEx keyboard) {
        super.setKeyboard(keyboard);
        this.isTraceEnabledOnKeyboard = (keyboard == null || !this.mTraceInputSuggestionOn || (!this.mKeyboardSwitcher.isAlphabetMode() && !this.mKeyboardSwitcher.isNumMode() && !this.mKeyboardSwitcher.isSymbolMode() && !getKeyboard().isForcedSwypeable()) || getKeyboard().getKeyboardDockMode() == KeyboardEx.KeyboardDockMode.DOCK_SPLIT || this.mCurrentInputLanguage.getCurrentInputMode().mInputMode.equals(InputMethods.KEYBOARD_TRANSLITERATION)) ? false : true;
        this.isTraceEnabledOnKeyboard = this.isTraceEnabledOnKeyboard && !isExploreByTouchOn() && isValidBuild();
        this.mAlphaInput.enableTrace(this.isTraceEnabledOnKeyboard);
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
            dismissPopupKeyboard();
            this.mKeyboardSwitcher.createKeyboardForTextInput(keyboardLayer, this.mInputFieldInfo, this.mCurrentInputLanguage.getCurrentInputMode());
            if (isExploreByTouchOn()) {
                AccessibilityNotification.getInstance().speakKeyboardLayer(getContext(), keyboardLayer);
            }
            if (keyboardLayer == KeyboardEx.KeyboardLayerType.KEYBOARD_TEXT) {
                updateShiftKeyState();
            } else {
                setShiftState(Shift.ShiftState.OFF);
            }
        }
    }

    private void readStyles(Context context) {
        TypedArrayWrapper a = IMEApplication.from(context).getThemeLoader().obtainStyledAttributes$6d3b0587(context, null, R.styleable.InlineStringAlpha, 0, R.style.InlineStringAlpha, R.xml.defaults, "InlineStringAlpha");
        int n = a.delegateTypedArray.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.InlineStringAlpha_wordErrorForegroundColor) {
                this.mFGWordErrorSpan = new ForegroundColorSpan(a.getColor(attr, -65536));
            } else if (attr == R.styleable.InlineStringAlpha_wordErrorBackgroundColor) {
                this.mBKWordErrorSpan = new BackgroundColorSpan(a.getColor(attr, 0));
            } else if (attr == R.styleable.InlineStringAlpha_multitapForegroundColor) {
                this.mFGMultiTappingCharSpan = new ForegroundColorSpan(a.getColor(attr, -1));
            } else if (attr == R.styleable.InlineStringAlpha_multitapBackgroundColor) {
                this.mBKMultiTappingCharSpan = new BackgroundColorSpan(a.getColor(attr, -65536));
            }
        }
        a.recycle();
    }

    private void triggerAutoSpaceTip() {
        if (shouldShowTips() && this.mAutoSpace) {
            ToolTips.from(getContext()).triggerAutoSpaceTip(this);
        }
    }

    private void triggerDoubleLettersGestureTip() {
        if (shouldShowTips()) {
            ToolTips.from(getContext()).triggerDoubleLettersGestureTip(this);
        }
    }

    private void triggerCapitalizationGestureTip() {
        if (shouldShowTips() && currentLanguageSupportsCapitalization()) {
            ToolTips.from(getContext()).triggerCaptitalizationGestureTip(this);
        }
    }

    private void handleAddRemoveDlmWord(String text, boolean selectedText) {
        if (text != null && text.trim().length() != 0) {
            if (selectedText && this.mAlphaInput.dlmFind(text)) {
                showRemoveUdbWordDialog(text);
            } else {
                new PromptForAddingOOVWordFromSelectedText(text).promptToAddIfAny();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.InputView
    public void sendBackspace(int repeatedCount) {
        super.sendBackspace(repeatedCount);
        updateShiftKeyState();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.InputView
    public void sendKeyChar(char character) {
        super.sendKeyChar(character);
        updateShiftKeyState();
    }

    private void setSuggestionsHelper(Candidates candidates) {
        this.oovWordHistory.overrideDefault(candidates);
        if (this.backspaceRevertHandler != null) {
            this.backspaceRevertHandler.onSetSuggestions(candidates);
        }
        handleOverrideCacheFilterSuggestions(candidates);
    }

    @Override // com.nuance.swype.input.InputView
    public void setSuggestions(CandidatesListView.CandidateListener listener, Candidates candidates, CandidatesListView.Format format) {
        setSuggestionsHelper(candidates);
        super.setSuggestions(listener, candidates, format);
    }

    private void selectDefaultSuggestion(boolean shouldCommitText, StatisticsEnabledEditState.DefaultSelectionType selectionType) {
        if (shouldSelectDefaultCandidate()) {
            selectCandidate(this.suggestionCandidates.getDefaultCandidate(), null, shouldCommitText, false, selectionType, true);
        } else {
            flushCurrentActiveWord();
        }
    }

    @Override // com.nuance.swype.input.InputView
    public void handleEmotionKey() {
        this.mIme.showEmojiInputView();
    }

    @Override // com.nuance.swype.input.InputView
    public void selectDefaultSuggestion(StatisticsEnabledEditState.DefaultSelectionType selectionType) {
        selectDefaultSuggestion(true, selectionType);
    }

    @Override // com.nuance.swype.input.KeyboardViewEx
    protected void clipTouchPoint(Point pt) {
        pt.x = pt.x > 0 ? pt.x : 0;
    }

    @Override // com.nuance.swype.input.InputView
    public void closeDialogs() {
        dimissRemoveUdbWordDialog();
    }

    @Override // com.nuance.swype.input.InputView
    public boolean hasPendingSuggestionsUpdate() {
        return this.mAlphaInputViewHandler.hasMessages(9) || this.candidatesBuilderHandler.hasPendingBuild();
    }

    @Override // com.nuance.swype.input.InputView
    public void clearPendingSuggestionsUpdate() {
        this.mAlphaInputViewHandler.removeMessages(9);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.InputView
    public void setDefaultWordType(StatisticsEnabledEditState.DefaultSelectionType type) {
        if (this.mEditState != null && (this.mEditState instanceof StatisticsEnabledEditState)) {
            ((StatisticsEnabledEditState) this.mEditState).setDefaultWordType(type);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.InputView
    public void onUpdateSpeechText(CharSequence text, boolean isStreaming, boolean isFinal) {
        Dictation dictation;
        CustomWordSynchronizer cws;
        super.onUpdateSpeechText(text, isStreaming, isFinal);
        log.d("onUpdateSpeechText: isFinal = ", Boolean.valueOf(isFinal));
        this.mAlphaInput.setWordQuarantineLevel(1, 1, 1);
        log.d("onUpdateSpeechText: dlm update, buffer len = ", Integer.valueOf(text.length()));
        this.mAlphaInput.dlmImplicitScanBuf(text.toString(), true, true, true, null);
        int udbCurrentCount = this.mAlphaInput.dlmCount();
        int udbServerCount = AppPreferences.from(getContext()).getCustomWordsSynchronizationServerWordsCount();
        if (udbCurrentCount != udbServerCount && (dictation = this.mSpeechWrapper.getCurrentDictation()) != null && (cws = dictation.getCustomWordsSync(UserDictionaryIterator.createAlphaIterator(this.mAlphaInput, getCurrentInputLanguage(), this.mSpeechWrapper))) != null) {
            cws.resyncAllUserWords(getInputFieldInfo());
        }
    }

    /* loaded from: classes.dex */
    static class CandidateFactory extends XT9CoreInput.DefaultCandidateFactory {
        private CandidateBehavior englishSubjectiveFirstPersonBehavior = CandidateBehavior.SWAP;
        private final WeakReference<AlphaInputView> parentView;

        /* loaded from: classes.dex */
        public enum CandidateBehavior {
            DISABLE,
            AUTO_CORRECTION,
            SWAP
        }

        public CandidateFactory(AlphaInputView view) {
            this.parentView = new WeakReference<>(view);
        }

        public void setEnglishSubjectiveFirstPersonBehavior(CandidateBehavior behavior) {
            this.englishSubjectiveFirstPersonBehavior = behavior;
        }

        private Candidates createEnglishTapCandidates(InputView view, List<WordCandidate> wordCandidates, Candidates.Source source) {
            if (wordCandidates == null || view.autoCorrectionEnabled || this.englishSubjectiveFirstPersonBehavior == CandidateBehavior.DISABLE || wordCandidates.size() <= 2) {
                return null;
            }
            WordCandidate first = wordCandidates.get(0);
            if (!first.isDefault() || !first.isExact() || !first.toString().equals("i")) {
                return null;
            }
            WordCandidate second = wordCandidates.get(1);
            if (!second.toString().equals("I")) {
                return null;
            }
            if (CandidateBehavior.AUTO_CORRECTION == this.englishSubjectiveFirstPersonBehavior) {
                XT9CoreInput.ensureSecondIsDefault(wordCandidates);
                return new Candidates(wordCandidates, source);
            }
            if (CandidateBehavior.SWAP != this.englishSubjectiveFirstPersonBehavior) {
                return null;
            }
            wordCandidates.set(0, second);
            wordCandidates.set(1, first);
            return new Candidates(wordCandidates, source, 0, 0);
        }

        private static boolean hasLang(String langRegionCode, String lang) {
            int end = langRegionCode.indexOf(45);
            if (-1 == end) {
                end = langRegionCode.length();
            }
            return langRegionCode.substring(0, end).equals(lang);
        }

        @Override // com.nuance.input.swypecorelib.XT9CoreInput.DefaultCandidateFactory, com.nuance.input.swypecorelib.XT9CoreInput.ICandidateFactory
        public Candidates createCandidates(List<WordCandidate> wordCandidates, Candidates.Source source) {
            AlphaInputView view;
            InputMethods.Language lang;
            Candidates out = null;
            if (source == Candidates.Source.TAP && (view = this.parentView.get()) != null && (lang = view.getCurrentInputLanguage()) != null && hasLang(lang.mLangRegionCode, "en")) {
                out = createEnglishTapCandidates(view, wordCandidates, source);
            }
            if (out == null) {
                return super.createCandidates(wordCandidates, source);
            }
            return out;
        }
    }

    @Override // com.nuance.swype.input.InputView
    public void refreshBTAutoCorrection() {
        super.refreshBTAutoCorrection();
        this.mAlphaInput.setAttribute(99, this.autoCorrectionEnabled);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doAddWord(String text) {
        AppSpecificInputConnection ic;
        boolean z;
        if (getWordDecorator() != null && !TextUtils.isEmpty(text) && (ic = getCurrentInputConnection()) != null) {
            Context context = getContext();
            ExtractedTextRequest extractedTextRequest = new ExtractedTextRequest();
            extractedTextRequest.flags = 1;
            ExtractedText extractedText = ic.getExtractedText(extractedTextRequest, 0);
            if (extractedText == null || TextUtils.isEmpty(extractedText.text)) {
                z = false;
            } else {
                ic.beginBatchEdit();
                WordDecorator.removeDecoration(context, ic, extractedText, text);
                ic.setSelection(extractedText.startOffset + extractedText.selectionStart, extractedText.selectionEnd + extractedText.startOffset);
                ic.endBatchEdit();
                z = true;
            }
            if (!z) {
                ic.beginBatchEdit();
                if (ic.hasSelection() || this.mIme.isEditorComposingText()) {
                    ic.commitText(text, 1);
                } else {
                    setInlineWord(text);
                    int resetPoint = ic.setComposingRegionBeforeCursor(text, 2, false);
                    if (resetPoint != -1) {
                        ic.commitText(text, 1);
                        ic.setSelection(resetPoint, resetPoint);
                    }
                    setInlineWord(null);
                }
                ic.endBatchEdit();
            }
        }
        clearComposingTextAndSelection();
        updateShiftKeyState();
        clearSuggestions();
        showNextWordPrediction();
        this.mLastInput = 4;
        this.mAlphaInput.setWordQuarantineLevel(1, 1, 1);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public abstract class PromptForAddingOOVWord implements XT9CoreAlphaInput.ExplicitLearningApprovalCallback, CandidatesListView.CandidateListener {
        final List<String> oovLearningWords;

        protected abstract void addOOVWord();

        protected abstract String getSelectedText();

        protected abstract void promptToAddIfAny();

        protected abstract void setExplicitPromptState();

        private PromptForAddingOOVWord() {
            this.oovLearningWords = new ArrayList();
        }

        void clearOOVWords() {
            this.oovLearningWords.clear();
        }

        void promptToAddNewWord(String prompt) {
            Drawable addIcon = IMEApplication.from(AlphaInputView.this.getContext()).getThemedDrawable(R.attr.hwclAddIcon);
            Candidates candidates = new Candidates(Candidates.Source.UDB_EDIT);
            candidates.addAttribute(1);
            candidates.addAttribute(2);
            candidates.add(new DrawableCandidate(addIcon));
            candidates.add(new WordCandidate(prompt));
            AlphaInputView.this.setSuggestions(this, candidates, CandidatesListView.Format.DEFAULT);
            AlphaInputView.this.setCandidateListener(this);
        }

        @Override // com.nuance.swype.input.CandidatesListView.CandidateListener
        public boolean onSelectCandidate(WordCandidate candidate, Candidates candidates) {
            AlphaInputView.this.mAlphaInput.setWordQuarantineLevel(0, 0, 0);
            addOOVWord();
            AlphaInputView.this.doAddWord(getSelectedText());
            return true;
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
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class PromptForAddingOOVCandidate extends PromptForAddingOOVWord {
        String word;

        private PromptForAddingOOVCandidate() {
            super();
        }

        @Override // com.nuance.swype.input.AlphaInputView.PromptForAddingOOVWord
        protected void setExplicitPromptState() {
            clearOOVWords();
            this.word = null;
            if (AlphaInputView.this.mInputFieldInfo.isUDBSubstitutionField() || AlphaInputView.this.mInputFieldInfo.isPasswordField()) {
                AlphaInputView.this.mAlphaInput.setExplicitLearning(true, true);
                AlphaInputView.this.mAlphaInput.registerExplicitLearningApprovalCallback(null);
            } else if (AlphaInputView.this.promptToAddWords) {
                AlphaInputView.this.mAlphaInput.setExplicitLearning(true, true);
                AlphaInputView.this.mAlphaInput.registerExplicitLearningApprovalCallback(this);
            } else {
                AlphaInputView.this.mAlphaInput.setExplicitLearning(false, false);
            }
        }

        @Override // com.nuance.swype.input.AlphaInputView.PromptForAddingOOVWord
        protected void addOOVWord() {
            AlphaInputView.this.mAlphaInput.setWordQuarantineLevel(0, 0, 0);
            AlphaInputView.this.mAlphaInput.explicitLearningAddLastWord();
        }

        @Override // com.nuance.swype.input.AlphaInputView.PromptForAddingOOVWord
        protected void promptToAddIfAny() {
            if (!this.oovLearningWords.isEmpty()) {
                this.word = this.oovLearningWords.get(this.oovLearningWords.size() - 1);
                String prompt = AlphaInputView.this.getResources().getString(R.string.hwcl_add_udb_word, this.word);
                this.oovLearningWords.clear();
                promptToAddNewWord(prompt);
            }
        }

        @Override // com.nuance.input.swypecorelib.XT9CoreAlphaInput.ExplicitLearningApprovalCallback
        public boolean onRequestExplicitLearningApproval(String oov, int syncId) {
            this.oovLearningWords.add(oov);
            AlphaInputView.this.oovWordHistory.add(oov);
            return false;
        }

        @Override // com.nuance.swype.input.AlphaInputView.PromptForAddingOOVWord
        protected String getSelectedText() {
            return this.word;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class PromptForAddingOOVWordFromSelectedText extends PromptForAddingOOVWord {
        private final String selectedText;

        private PromptForAddingOOVWordFromSelectedText(String selectedText) {
            super();
            this.selectedText = selectedText;
        }

        @Override // com.nuance.swype.input.AlphaInputView.PromptForAddingOOVWord
        protected void addOOVWord() {
            this.oovLearningWords.clear();
            AlphaInputView.this.mAlphaInput.explicitLearningAddAllRecentWords();
        }

        @Override // com.nuance.input.swypecorelib.XT9CoreAlphaInput.ExplicitLearningApprovalCallback
        public boolean onRequestExplicitLearningApproval(String oov, int syncId) {
            this.oovLearningWords.add(oov);
            AlphaInputView.this.oovWordHistory.add(oov);
            return false;
        }

        @Override // com.nuance.swype.input.AlphaInputView.PromptForAddingOOVWord
        protected void setExplicitPromptState() {
            AlphaInputView.this.mAlphaInput.setExplicitLearning(true, true);
            AlphaInputView.this.mAlphaInput.registerExplicitLearningApprovalCallback(this);
        }

        @Override // com.nuance.swype.input.AlphaInputView.PromptForAddingOOVWord
        protected void promptToAddIfAny() {
            String prompt;
            clearOOVWords();
            setExplicitPromptState();
            AlphaInputView.this.mAlphaInput.dlmScanBuf(this.selectedText, true, true, true);
            if (!this.oovLearningWords.isEmpty()) {
                if (this.oovLearningWords.size() > 1) {
                    prompt = AlphaInputView.this.getResources().getString(R.string.hwcl_add_udb_words);
                } else {
                    prompt = AlphaInputView.this.getResources().getString(R.string.hwcl_add_udb_word, this.oovLearningWords.get(0));
                }
                promptToAddNewWord(prompt);
            }
        }

        @Override // com.nuance.swype.input.AlphaInputView.PromptForAddingOOVWord
        protected String getSelectedText() {
            return this.selectedText;
        }
    }

    private void handleBackSpaceRevertCancelWordRecapture(char ch) {
        if (this.backspaceRevertHandler != null) {
            boolean isAcceptRevert = this.charUtils.isWordAcceptingSymbol(ch) && this.backspaceRevertHandler.isOverrideActive();
            log.d("handleBackSpaceRevertCancelWordRecapture(): is accept revert: " + isAcceptRevert);
            if (isAcceptRevert) {
                handleOverrideCacheAddSelectedCandidate(this.suggestionCandidates);
                processDeferredSuggestionUpdates();
                selectCandidate(this.suggestionCandidates.getDefaultCandidate(), null, false, false, StatisticsEnabledEditState.DefaultSelectionType.TAPPED_PUNCTUATION, true);
            }
        }
    }

    private void handleOverrideCacheFilterSuggestions(Candidates candidates) {
        if (this.autoCorrectOverrideWords != null && candidates.source().equals(Candidates.Source.TAP)) {
            String cand = candidates.getExactCandidateString().toString();
            if (this.autoCorrectOverrideWords.words.contains(cand.toLowerCase())) {
                log.d("setSuggestions(): setting default to exact (word cache)");
                candidates.setDefaultIndex(candidates.getExactCandidateIndex());
            }
        }
    }

    private void handleOverrideCacheAddSelectedCandidate(Candidates candidates) {
        if (this.autoCorrectOverrideWords == null) {
            return;
        }
        if (!Candidates.match(this.suggestionCandidates, Candidates.Source.RECAPTURE_BY_TEXT_SELECTION)) {
            log.d("handleAddAcOverrideCache(): unexpected cand type: " + this.suggestionCandidates);
            return;
        }
        String word = this.suggestionCandidates.getDefaultCandidate().toString();
        if (!this.mAlphaInput.isKnownWord(word)) {
            log.d("handleAddAcOverrideCache(): add: " + word);
            LruWordCache lruWordCache = this.autoCorrectOverrideWords;
            String lowerCase = word.toLowerCase();
            lruWordCache.words.remove(lowerCase);
            lruWordCache.words.add(lowerCase);
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:59:0x016a, code lost:            if (composingWordCandidates() == false) goto L95;     */
    @Override // com.nuance.swype.input.InputView
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void handleHardkeyCharKey(int r11, android.view.KeyEvent r12, boolean r13) {
        /*
            Method dump skipped, instructions count: 379
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.nuance.swype.input.AlphaInputView.handleHardkeyCharKey(int, android.view.KeyEvent, boolean):void");
    }

    @Override // com.nuance.swype.input.InputView
    public boolean handleHardkeyBackspace(int repeatedCount) {
        return handleBackspace(repeatedCount);
    }

    @Override // com.nuance.swype.input.InputView
    public boolean handleHardkeySpace(boolean quickPressed, int repeatedCount) {
        WordCandidate selectedCandidate;
        CharSequence seq;
        this.isAddToDictionaryTipHighlighted = false;
        InputConnection ic = getCurrentInputConnection();
        if (ic != null && this.mInlineWord.length() > 0 && this.suggestionCandidates != null && this.suggestionCandidates.source() == Candidates.Source.NEXT_WORD_PREDICTION && (selectedCandidate = this.suggestionCandidates.getDefaultCandidate()) != null && selectedCandidate.shouldRemoveSpaceBefore() && selectedCandidate.length() > 0 && (seq = ic.getTextBeforeCursor(2, 0)) != null && seq.length() == 2 && CharacterUtilities.isWhiteSpace(seq.charAt(0)) && seq.charAt(seq.length() - 1) != ' ') {
            ic.deleteSurroundingText(1, 0);
        }
        return handleSpace(quickPressed, repeatedCount);
    }

    @Override // com.nuance.swype.input.InputView
    public boolean handleHardkeyDelete(int repeatedCount) {
        InputConnection ic = getCurrentInputConnection();
        if (this.mAlphaInput == null || ic == null) {
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
                updateShiftKeyState(getCurrentInputEditorInfo());
                clearAllKeys();
                this.mAlphaInput.setContext(null);
                this.mEditState.backSpace();
            }
        }
        return true;
    }

    public void movePointer(float y) {
        if (getKeyboardLayer() == KeyboardEx.KeyboardLayerType.KEYBOARD_TEXT && this.keyboardTouchEventDispatcher.isTracing()) {
            if ((this.shiftGestureOn || getShiftState() == Shift.ShiftState.ON) && y >= 0.0f) {
                this.mKeyboardSwitcher.setShiftState(Shift.ShiftState.OFF);
                this.shiftGestureOn = false;
                invalidateKeyboardImage();
            } else if (getShiftState() == Shift.ShiftState.OFF && y < 0.0f) {
                this.mKeyboardSwitcher.setShiftState(Shift.ShiftState.ON);
                this.shiftGestureOn = true;
                invalidateKeyboardImage();
            }
        }
    }

    @Override // com.nuance.swype.input.InputView
    public boolean isShowingAddToDictionaryTip() {
        boolean oneCandidate = (this.suggestionCandidates == null || this.candidatesListView == null || this.candidatesListView.mSuggestions == null || this.candidatesListView.mSuggestions.source() != Candidates.Source.UDB_EDIT) ? false : true;
        CandidatesListView.CandidateListener listener = this.candidatesListView.getCandidateListener();
        return oneCandidate && listener != null && (listener instanceof PromptForAddingOOVWord);
    }

    @Override // com.nuance.swype.input.InputView
    public boolean isAddToDictionaryTipHighlighted() {
        return this.isAddToDictionaryTipHighlighted;
    }

    @Override // com.nuance.swype.input.InputView
    public boolean addHardKeyOOVToDictionary() {
        this.candidatesListView.trySelect();
        return true;
    }

    @Override // com.nuance.swype.input.InputView
    public boolean handleHardkeyEnter() {
        InputConnection ic = getCurrentInputConnection();
        this.isAddToDictionaryTipHighlighted = false;
        if (this.mAlphaInput == null || ic == null) {
            sendKeyToApplication(66);
        } else if (this.suggestionCandidates == null || this.suggestionCandidates.source() == Candidates.Source.RECAPTURE || this.suggestionCandidates.source() == Candidates.Source.RECAPTURE_BY_TEXT_SELECTION) {
            sendKeyToApplication(66);
        } else {
            ic.beginBatchEdit();
            if (InputConnectionUtils.isComposingTextSelected(ic)) {
                clearSuggestions();
                clearAllKeys();
            } else if (composingWordCandidates() || (this.mIsUseHardkey && this.mInlineWord.length() > 0 && this.suggestionCandidates != null && this.suggestionCandidates.source() == Candidates.Source.NEXT_WORD_PREDICTION)) {
                selectCandidate(this.suggestionCandidates.getDefaultCandidate(), null, StatisticsEnabledEditState.DefaultSelectionType.TAPPED_SPACE);
                if (this.mLastInput == 2) {
                    triggerAutoSpaceTip();
                }
                updateShiftKeyState();
                updateMultitapDeadkey(true);
                ic.endBatchEdit();
            } else if (this.suggestionCandidates != null && this.suggestionCandidates.source() != Candidates.Source.NEXT_WORD_PREDICTION) {
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
        if (iscapslockon) {
            this.mKeyboardSwitcher.setShiftState(Shift.ShiftState.LOCKED);
        } else {
            this.mKeyboardSwitcher.setShiftState(Shift.ShiftState.OFF);
        }
        if (getShiftState() == Shift.ShiftState.ON) {
            this.isManualShift = true;
        }
        updateNWP();
        invalidateKeyboardImage();
        return true;
    }

    @Override // com.nuance.swype.input.InputView
    public boolean handleHardkeyShiftKey(Shift.ShiftState state) {
        if (this.mKeyboardSwitcher.isAlphabetMode()) {
            this.mKeyboardSwitcher.setShiftState(state);
            if (getShiftState() == Shift.ShiftState.ON) {
                this.isManualShift = true;
            }
            updateNWP();
            invalidateKeyboardImage();
        }
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

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Failed to find 'out' block for switch in B:6:0x000d. Please report as an issue. */
    /* JADX WARN: Removed duplicated region for block: B:57:0x00bc  */
    /* JADX WARN: Removed duplicated region for block: B:59:0x00c7  */
    @Override // com.nuance.swype.input.InputView
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public boolean handleHardKeyDirectionKey(int r5) {
        /*
            Method dump skipped, instructions count: 260
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.nuance.swype.input.AlphaInputView.handleHardKeyDirectionKey(int):boolean");
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

    private boolean moveHighlightToCenter(int keyCode) {
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

    @Override // com.nuance.swype.input.InputView
    public boolean handleHardKeyHomeKey() {
        if (this.candidatesListView == null || this.suggestionCandidates == null || (this.suggestionCandidates != null && this.suggestionCandidates.count() <= 0)) {
            sendKeyToApplication(122);
            return false;
        }
        if (this.suggestionCandidates == null || this.suggestionCandidates.source() == Candidates.Source.NEXT_WORD_PREDICTION || this.suggestionCandidates.source() == Candidates.Source.RECAPTURE || this.suggestionCandidates.source() == Candidates.Source.RECAPTURE_BY_TEXT_SELECTION) {
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
        if (this.suggestionCandidates == null || this.suggestionCandidates.source() == Candidates.Source.NEXT_WORD_PREDICTION || this.suggestionCandidates.source() == Candidates.Source.RECAPTURE || this.suggestionCandidates.source() == Candidates.Source.RECAPTURE_BY_TEXT_SELECTION) {
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
        return this.mAlphaInput != null && this.mAlphaInput.getKeyCount() > 0 && this.mInlineWord != null && this.mInlineWord.length() > 0;
    }

    private void sendHardKeyChar(char character) {
        switch (character) {
            case '\n':
                if (this.mIme != null && !this.mIme.sendDefaultEditorAction(true)) {
                    this.mIme.sendDownUpKeyEvents(66);
                    break;
                }
                break;
            default:
                InputConnection ic = getCurrentInputConnection();
                if (ic != null) {
                    ic.commitText(String.valueOf(character), 1);
                    break;
                }
                break;
        }
        this.mLastInput = 1;
        updateShiftKeyState();
    }

    @Override // com.nuance.swype.input.InputView
    public void setHardKeyboardSystemSettings() {
        SystemState sysState = IMEApplication.from(this.mIme).getSystemState();
        int capsValue = sysState.autoCapsValue();
        int punctuateValue = sysState.autoPunctuateValue();
        int replaceValue = sysState.autoReplaceValue();
        boolean autoCaps = capsValue > 0;
        boolean autoPunctuate = punctuateValue > 0;
        boolean autoReplace = replaceValue > 0;
        if (capsValue != -1) {
            this.mAutoCap = this.mCurrentInputLanguage.isCapitalizationSupported() && autoCaps;
        }
        if (replaceValue != -1) {
            this.autoCorrectionEnabled = autoReplace;
            this.mKeyboardInputSuggestionOn = this.autoCorrectionEnabled || this.mWordCompletionPoint != 0;
            this.mAlphaInput.setAttribute(99, this.autoCorrectionEnabled);
        }
        if (punctuateValue != -1) {
            this.mAutoPunctuationOn = autoPunctuate;
        }
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
            handleHardkeyCharKey(text.charAt(0), null, false);
        }
    }

    @Override // com.nuance.swype.input.KeyboardViewEx
    public void showHardKeyPopupKeyboard(int keyCode) {
        super.showHardKeyPopupKeyboard(keyCode);
        int len = this.mInlineWord.length();
        if (this.mInlineWord != null && len > 0 && isComposingText()) {
            InputConnection ic = getCurrentInputConnection();
            if (this.mAlphaInput != null && ic != null) {
                if (isComposingText()) {
                    updateSuggestions(Candidates.Source.TAP, true);
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
        InputConnection ic;
        boolean useSpace = isAddSpaceRequired();
        selectDefaultSuggestion(StatisticsEnabledEditState.DefaultSelectionType.GENERIC);
        if (isExploreByTouchOn() && useSpace && (ic = getCurrentInputConnection()) != null) {
            ic.commitText(XMLResultsHandler.SEP_SPACE, 1);
        }
    }

    @Override // com.nuance.swype.input.InputView
    public void showNextWordPrediction() {
        if (!this.mNextWordPredictionOn) {
            updateSuggestionsEmoji(CandidatesListView.Format.DEFAULT);
            return;
        }
        String lastWord = IME.getLastSavedActiveWord();
        if (lastWord == null || lastWord.length() <= 0) {
            updateSuggestions(Candidates.Source.NEXT_WORD_PREDICTION, true);
        }
    }

    @Override // com.nuance.swype.input.InputView
    protected void setReconstructWord(int primaryCode) {
        this.mReconstructWord = this.mInlineWord.toString() + ((char) primaryCode);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setReconstructWord(String word) {
        this.mReconstructWord = word;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.InputView
    public void setInlineWord(String word) {
        this.mInlineWord.clear();
        if (word != null) {
            this.mInlineWord.append((CharSequence) word);
        }
    }

    @Override // com.nuance.swype.input.InputView
    public void reconstructWord() {
        clearAllKeys();
        if (isUsingVietnameseTelexInputMode()) {
            this.mAlphaInput.setVietInputMode(XT9CoreInput.XT9InputMode.DEFAULT);
        }
        this.mAlphaInput.reconstructWord(this.mReconstructWord.toCharArray(), true);
        if (isUsingVietnameseTelexInputMode()) {
            this.mAlphaInput.setVietInputMode(XT9CoreInput.XT9InputMode.TELEX);
        }
    }

    @Override // com.nuance.swype.input.InputView
    public boolean isUsingVietnameseTelexInputMode() {
        return isModeUsing(InputMethods.VIETNAMESE_INPUT_MODE_TELEX);
    }

    @Override // com.nuance.swype.input.InputView
    public boolean isUsingVietnameseNationalInputMode() {
        return isModeUsing(InputMethods.VIETNAMESE_INPUT_MODE_NATIONAL);
    }

    private boolean isModeUsing(String mode) {
        String currentInputMode = "";
        if (this.mCurrentInputLanguage != null) {
            currentInputMode = this.mCurrentInputLanguage.getCurrentInputMode().mInputMode;
            if (!this.mCurrentInputLanguage.isVietnameseLanguage() && (this.mCurrentInputLanguage instanceof BilingualLanguage) && ((BilingualLanguage) this.mCurrentInputLanguage).getSecondLanguage().isVietnameseLanguage() && currentInputMode.contains(InputMethods.VIETNAMESE_INPUT_MODE_TELEX)) {
                currentInputMode = InputMethods.VIETNAMESE_INPUT_MODE_TELEX;
            }
        }
        return mode.equals(currentInputMode);
    }

    @Override // com.nuance.swype.input.InputView
    public CharSequence getCurrentDefaultWord() {
        processDeferredSuggestionUpdates();
        return super.getCurrentDefaultWord();
    }

    @Override // com.nuance.swype.input.InputView
    public CharSequence getCurrentExactWord() {
        processDeferredSuggestionUpdates();
        return super.getCurrentExactWord();
    }

    @Override // com.nuance.swype.input.InputView
    public boolean isInputSessionStarted() {
        return !this.mAlphaInputViewHandler.hasMessages(15);
    }

    @Override // com.nuance.swype.input.InputView
    public void requestAutospaceOverrideTo(boolean enabled) {
        if (enabled) {
            resetAutoSpace();
            if (!this.mAutoSpace) {
                this.autospaceHandler.setEnabled(false, this.mCurrentInputLanguage.isAutoSpaceSupported() ? false : true);
                return;
            } else {
                this.autospaceHandler.onUpdateEditorInfo(this.mInputFieldInfo);
                return;
            }
        }
        this.autospaceHandler.setEnabled(false, this.mCurrentInputLanguage.isAutoSpaceSupported() ? false : true);
    }

    @Override // com.nuance.swype.input.InputView
    public void handlePossibleActionAfterUniversalKeyboardResize(boolean hideSymbolView) {
        if (this.mInputFieldInfo.isPasswordField()) {
            setCandidatesViewShown(false);
        }
    }

    @Override // com.nuance.swype.input.InputView
    public void handlePossibleActionAfterResize() {
        if (this.mInputFieldInfo.isPasswordField()) {
            setCandidatesViewShown(false);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.InputView
    public void setLanguage(XT9CoreInput coreInput) {
        XT9CoreInput.XT9InputMode xt9InputMode;
        if (isUsingVietnameseTelexInputMode()) {
            xt9InputMode = XT9CoreInput.XT9InputMode.TELEX;
        } else if (isUsingVietnameseNationalInputMode()) {
            xt9InputMode = XT9CoreInput.XT9InputMode.VNI;
        } else if (this.mCurrentInputLanguage.isHindiLanguage() && this.mCurrentInputLanguage.getCurrentInputMode().mInputMode.equals(InputMethods.KEYBOARD_TRANSLITERATION)) {
            xt9InputMode = XT9CoreInput.XT9InputMode.TRANSLITERATION;
        } else {
            xt9InputMode = XT9CoreInput.XT9InputMode.DEFAULT;
        }
        XT9Status status = this.mCurrentInputLanguage.setLanguage(coreInput, xt9InputMode);
        if (status == XT9Status.ET9STATUS_DB_CORE_INCOMP || status == XT9Status.ET9STATUS_LDB_VERSION_ERROR) {
            DatabaseConfig.removeIncompatibleDBFiles(getContext(), this.mCurrentInputLanguage.mEnglishName);
            XT9Status status2 = this.mCurrentInputLanguage.setLanguage(coreInput, xt9InputMode);
            if (status2 == XT9Status.ET9STATUS_DB_CORE_INCOMP || status2 == XT9Status.ET9STATUS_LDB_VERSION_ERROR) {
                this.isLDBCompatible = false;
                promptUserToUpdateLanguage();
                if (this.mIme != null) {
                    this.mIme.getInputMethods().setCurrentLanguageById(265);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isCursorWithinWord() {
        return getCurrentInputConnection().isCursorWithinWord(CharacterUtilities.from(this.mIme));
    }

    private void updateExplicitInput(char keyCode) {
        if (this.mTextInputPredictionOn) {
            if (!Character.isAlphabetic(keyCode) && (!this.mAlphaInput.hasActiveInput() || (!CharacterUtilities.isDigit(keyCode) && (!this.charUtils.isWordCompounder(keyCode) || this.mLastInput != 1)))) {
                handlePunctOrSymbol(keyCode);
            } else if (this.inputContextRequestHandler.addExplicitSymbol(keyCode, getShiftState())) {
                handlePrediction(keyCode);
            }
            this.autospaceHandler.onCharKey(keyCode);
            this.mLastInput = 1;
        } else {
            Shift.ShiftState state = getShiftState();
            if (state == Shift.ShiftState.ON || state == Shift.ShiftState.LOCKED) {
                keyCode = Character.toUpperCase(keyCode);
            }
            sendKeyChar(keyCode);
        }
        updateShiftKeyState();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleFunctionKey(KeyboardEx.Key key, boolean quickPress, int repeatCount) {
        if (this.mTextInputPredictionOn) {
            int keyCode = key.codes[0];
            if ((keyCode == 10 || keyCode == 32) && this.candidatesBuilderHandler.hasPendingBuild()) {
                this.candidatesBuilderHandler.removePendingBuild();
                Candidates candidates = this.mAlphaInput.getCandidates();
                if (candidates != null) {
                    this.inputContextRequestHandler.commitText(((Object) candidates.getDefaultCandidateString()) + (keyCode == 32 ? XMLResultsHandler.SEP_SPACE : "\n"));
                    this.inputContextRequestHandler.setLastAutoSpaceInserted();
                    clearSuggestions();
                }
                log.d("accepting pending WCL");
                return;
            }
            if (!this.wordRecaptureWrapper.reselect(key.codes[0], this.mIme.getRecaptureHandler())) {
                if (shouldHandleKeyViaIME(key.codes[0]) || !handleKey(key.codes[0], quickPress, repeatCount)) {
                    if (KeyboardEx.isChangeKeyboardLayerKey(key.codes[0]) || !handleGesture(key.codes[0])) {
                        this.mIme.onKey(null, key.codes[0], key.codes, key, 0L);
                        return;
                    }
                    return;
                }
                return;
            }
            return;
        }
        if (shouldHandleKeyViaIME(key.codes[0]) || !handleKey(key.codes[0], quickPress, repeatCount)) {
            if (KeyboardEx.isChangeKeyboardLayerKey(key.codes[0]) || !handleGesture(key.codes[0])) {
                this.mIme.onKey(null, key.codes[0], key.codes, key, 0L);
            }
        }
    }

    private void createCandidatesBuilderHandler() {
        if (this.candidatesBuilderHandler == null) {
            this.candidatesBuilderHandler = new CandidatesBuilderHandler(this.mAlphaInput, this.candidateBuilderResult);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean noAutoSpace() {
        return (this.mAutoSpace && !this.mInputFieldInfo.isEmailAddressField() && (!this.mInputFieldInfo.isURLField() || this.mInputFieldInfo.isWebSearchField() || this.mInputFieldInfo.isURLWithSearchField() || getAppSpecificBehavior().shouldAutoSpaceInUrlField())) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void autoSpace(KeyType keyType, int lastInput, boolean isTracing) {
        log.d("autoSpace().... lastInput=", Integer.valueOf(lastInput), "   noAutoSpace:", Boolean.valueOf(noAutoSpace()));
        if (!noAutoSpace()) {
            if (lastInput == 4 || isTracing || lastInput == 6 || lastInput == 5) {
                log.d("hasActivitInput:", Boolean.valueOf(this.mAlphaInput.hasActiveInput()), "   autospaceHandler.shouldAutoSpace()", Boolean.valueOf(this.autospaceHandler.shouldAutoSpace()));
                if (this.mAlphaInput.hasActiveInput() || !this.autospaceHandler.shouldAutoSpace()) {
                    return;
                }
                if (keyType.isLetter() || keyType.isNumber()) {
                    CharSequence textBeforeCursor = getCurrentInputConnection().getTextBeforeCursor(1, 0);
                    if (TextUtils.isEmpty(textBeforeCursor) || textBeforeCursor.charAt(textBeforeCursor.length() - 1) == ' ') {
                        return;
                    }
                    this.inputContextRequestHandler.commitText(XMLResultsHandler.SEP_SPACE);
                    this.inputContextRequestHandler.setLastAutoSpaceInserted();
                    if (!isShifted()) {
                        if (!this.isManualShift) {
                            updateShiftKeyState();
                        } else {
                            this.isManualShift = false;
                        }
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class InputContextRequestHandler implements InputContextRequestDispatcher.InputContextRequestHandler {
        private final char[] emptyTextBuffer;
        private boolean lastAutoSpaceInserted;
        private long lastAutoSpaceTime;
        private long lastCursorChangedByUs;
        private final char[] noCapTextBuffer;

        private InputContextRequestHandler() {
            this.emptyTextBuffer = new char[0];
            this.noCapTextBuffer = new char[]{'x', ' '};
            this.lastAutoSpaceInserted = false;
            updateLastCursorChanged();
            this.lastAutoSpaceTime = SystemClock.uptimeMillis();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void resetLastAutoSpaceInserted() {
            if (SystemClock.uptimeMillis() - this.lastAutoSpaceTime > 250) {
                this.lastAutoSpaceInserted = false;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setLastAutoSpaceInserted() {
            this.lastAutoSpaceInserted = true;
            this.lastAutoSpaceTime = SystemClock.uptimeMillis();
        }

        private void updateLastCursorChanged() {
            this.lastCursorChangedByUs = SystemClock.uptimeMillis();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean cursorChangedLikelyCauseByUs() {
            return SystemClock.uptimeMillis() - this.lastCursorChangedByUs < 250;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void removeSpaceBeforeCursor() {
            InputConnection ic = AlphaInputView.this.getCurrentInputConnection();
            if (ic != null) {
                CharSequence text = ic.getTextBeforeCursor(1, 0);
                if (!TextUtils.isEmpty(text) && text.charAt(text.length() - 1) == ' ') {
                    ic.deleteSurroundingText(1, 0);
                    updateLastCursorChanged();
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void commitText(CharSequence text) {
            InputConnection ic = AlphaInputView.this.getCurrentInputConnection();
            if (ic != null) {
                ic.commitText(text, 1);
                updateLastCursorChanged();
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean addExplicitSymbol(char symbol, Shift.ShiftState shiftstate) {
            KeyboardViewEx.log.d("**** Core called addExplicitSymbol");
            return AlphaInputView.this.mAlphaInput.addCustomSymbol(symbol, shiftstate);
        }

        private char[] getTextBeforeCursor(int maxBufferLen) {
            InputConnection ic = AlphaInputView.this.getCurrentInputConnection();
            CharSequence textBeforeCursor = ic != null ? ic.getTextBeforeCursor(maxBufferLen, 0) : null;
            LogManager.Log log = KeyboardViewEx.log;
            Object[] objArr = new Object[2];
            objArr[0] = "**** Core called getTextBeforeCursor: ";
            objArr[1] = textBeforeCursor != null ? textBeforeCursor.toString() : "null";
            log.d(objArr);
            if (textBeforeCursor != null) {
                return textBeforeCursor.toString().toCharArray();
            }
            return null;
        }

        @Override // com.nuance.swype.input.keyboard.InputContextRequestDispatcher.InputContextRequestHandler
        public char[] getContextBuffer(int maxBufferLen) {
            KeyboardViewEx.log.d("**** Core called getContextBuffer(", Integer.valueOf(maxBufferLen), ")");
            return getTextBeforeCursor(maxBufferLen);
        }

        @Override // com.nuance.swype.input.keyboard.InputContextRequestDispatcher.InputContextRequestHandler
        public char[] getAutoCapitalizationTextBuffer(int maxBufferLen) {
            KeyboardViewEx.log.d("**** Core called getAutoCapitalizationTextBuffer(", Integer.valueOf(maxBufferLen), ") ");
            if (AlphaInputView.this.mCurrentInputLanguage.isCapitalizationSupported()) {
                EditorInfo editorInfo = AlphaInputView.this.getCurrentInputEditorInfo();
                if (editorInfo != null && editorInfo.inputType != 0 && InputConnectionUtils.getCapsMode(AlphaInputView.this.getCurrentInputConnection(), editorInfo) == 8192) {
                    return this.emptyTextBuffer;
                }
                return getTextBeforeCursor(maxBufferLen);
            }
            return this.noCapTextBuffer;
        }

        @Override // com.nuance.swype.input.keyboard.InputContextRequestDispatcher.InputContextRequestHandler
        public boolean autoAccept(boolean addSeparator) {
            AlphaInputView.this.getCurrentInputConnection().beginBatchEdit();
            KeyboardViewEx.log.d("**** Auto accept callback called. Add separator? ", Boolean.valueOf(addSeparator));
            AlphaInputView.this.mLastInput = 4;
            StringBuffer selectWord = new StringBuffer();
            if (AlphaInputView.this.suggestionCandidates != null && AlphaInputView.this.mAlphaInput.hasActiveInput() && AlphaInputView.this.suggestionCandidates.source() != Candidates.Source.NEXT_WORD_PREDICTION && AlphaInputView.this.suggestionCandidates.source() != Candidates.Source.TOOL_TIP && AlphaInputView.this.suggestionCandidates.source() != Candidates.Source.CAPS_EDIT && AlphaInputView.this.pendingCandidateSource != Candidates.Source.RECAPTURE) {
                AlphaInputView.this.wordRecaptureWrapper.onText("", AlphaInputView.this.mIme.getRecaptureHandler());
                if (!AlphaInputView.this.getCurrentInputConnection().hasSelection()) {
                    if (AlphaInputView.this.suggestionCandidates.source() != Candidates.Source.RECAPTURE_BY_TEXT_SELECTION) {
                        selectWord.append(AlphaInputView.this.suggestionCandidates.getDefaultCandidate().toString());
                    }
                    if (addSeparator && !AlphaInputView.this.noAutoSpace()) {
                        selectWord.append(AlphaInputView.this.mAlphaInput.getDefaultWordSeparator());
                        setLastAutoSpaceInserted();
                    }
                    if (AlphaInputView.this.suggestionCandidates.getDefaultCandidate().contextKillLength() != 0) {
                        commitText("");
                        AlphaInputView.this.mIme.deleteWord(AlphaInputView.this.getCurrentInputConnection(), AlphaInputView.this.suggestionCandidates.getDefaultCandidate().contextKillLength());
                    }
                    commitText(selectWord);
                }
                if (AlphaInputView.this.suggestionCandidates.source() != Candidates.Source.UDB_EDIT) {
                    AlphaInputView.this.mAlphaInput.candidateSelected(AlphaInputView.this.suggestionCandidates.getDefaultCandidate(), AlphaInputView.this.suggestionCandidates, true);
                    AlphaInputView.this.clearSuggestions();
                }
            }
            AlphaInputView.this.getCurrentInputConnection().endBatchEdit();
            return true;
        }
    }

    /* loaded from: classes.dex */
    private class LoggingDLMWipeEvent implements XT9CoreAlphaInput.DLMWipeEventCallback {
        private static final int ET9AW_DLM_REQUEST_CONTENTRESET = 3;
        private static final int ET9AW_DLM_REQUEST_WORDDISCARDED = 4;

        private LoggingDLMWipeEvent() {
        }

        @Override // com.nuance.input.swypecorelib.XT9CoreAlphaInput.DLMWipeEventCallback
        public boolean onRequestLoggingDLMWipeEvent(String word, int requestType, int reasonCode, int langaugeId) {
            KeyboardViewEx.log.d("onRequestLoggingDLMWipeEvent...word: ", word, "...requestType: ", Integer.valueOf(requestType), "...reasonCode: ", Integer.valueOf(reasonCode), "...langaugeId: ", Integer.valueOf(langaugeId));
            recordDLMWipeEvent(word, requestType, reasonCode, langaugeId);
            return false;
        }

        public void enableLoggingDLMWipeEvent() {
            AlphaInputView.this.mAlphaInput.registerLoggingDLMWipeEventCallback(this);
        }

        public void disableLoggingDLMWipeEvent() {
            AlphaInputView.this.mAlphaInput.unRegisterLoggingDLMWipeCallback(null);
        }

        private void recordDLMWipeEvent(String word, int requestType, int reasonCode, int langaugeId) {
            if (requestType == 3) {
                boolean isSyncEnabled = Connect.from(AlphaInputView.this.getContext()).getSync().isEnabled();
                int LangId = AlphaInputView.this.getCurrentInputLanguage().getCoreLanguageId();
                String langName = null;
                if (AlphaInputView.this.mIme != null && AlphaInputView.this.mIme.getInputMethods().getLanguageById(LangId) != null) {
                    langName = AlphaInputView.this.mIme.getInputMethods().getLanguageById(LangId).mEnglishName;
                }
                KeyboardViewEx.log.d("recordDLMWipeEvent...ET9AW_DLM_REQUEST_CONTENTRESET...", "isSyncEnabled: ", Boolean.valueOf(isSyncEnabled), "...langName: ", langName);
                UsageData.recordDLMWipe$627721fe(UsageData.DLMRequestType.DLM_CONTENT_RESET, isSyncEnabled, langName);
            }
        }
    }
}
