package com.nuance.swype.input;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.CompletionInfo;
import com.nuance.android.compat.ActivityManagerCompat;
import com.nuance.android.util.WeakReferenceHandler;
import com.nuance.input.swypecorelib.Candidates;
import com.nuance.input.swypecorelib.Shift;
import com.nuance.input.swypecorelib.T9Write;
import com.nuance.input.swypecorelib.T9WriteAlpha;
import com.nuance.input.swypecorelib.T9WriteCategory;
import com.nuance.input.swypecorelib.T9WriteRecognizerListener;
import com.nuance.input.swypecorelib.WordCandidate;
import com.nuance.input.swypecorelib.XT9CoreAlphaInput;
import com.nuance.input.swypecorelib.XT9CoreInput;
import com.nuance.input.swypecorelib.XT9Status;
import com.nuance.swype.input.AlphaHandWritingView;
import com.nuance.swype.input.CandidatesListView;
import com.nuance.swype.input.KeyboardEx;
import com.nuance.swype.input.Stroke;
import com.nuance.swype.input.appspecific.AppSpecificInputConnection;
import com.nuance.swype.input.emoji.EmojiLoader;
import com.nuance.swype.input.keyboard.DefaultHWRTouchKeyHandler;
import com.nuance.swype.input.keyboard.InputContextRequestDispatcher;
import com.nuance.swype.input.settings.InputPrefs;
import com.nuance.swype.stats.StatisticsEnabledEditState;
import com.nuance.swype.util.CharacterUtilities;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class AlphaHandWritingInputView extends InputView implements T9WriteRecognizerListener.OnWriteRecognizerListener, AlphaHandWritingView.OnWritingAction {
    private static final int MSG_DELAY_RECOGNIZER = 1;
    private static final int MSG_DISPLAY_NEXT_WORD_PREDICTION = 7;
    private static final int MSG_HANDLE_CHAR = 3;
    private static final int MSG_HANDLE_INSTANT_GESTURE_CHAR = 5;
    private static final int MSG_HANDLE_SHOW_START_OF_WORD_CANDIDATE = 6;
    private static final int MSG_HANDLE_SUGGESTION_CANDIDATES = 2;
    private static final int MSG_HANDLE_TEXT = 4;
    private static final int MSG_SHOW_HOW_TO_TOAST = 503;
    private static final int WRITING_MODE_SYMBOLS_AND_DIGITS = 1;
    private static final int WRITING_MODE_TEXT = 0;
    private XT9CoreAlphaInput alphaInput;
    private final Handler.Callback delayRecognizerCallback;
    private final Composition mComposition;
    private AlphaHandWritingContainerView mContainer;
    private int mCurrentWritingMode;
    private AlphaHandWritingView mCurrentWritingPad;
    private final Handler mDelayRecognizeHandler;
    private final Handler mPopupViewHandler;
    private T9WriteAlpha mWriteAlpha;
    private final Handler mWriteEventHandler;
    private final Handler.Callback popupViewCallback;
    private int shiftGestureOffset;
    private final Handler.Callback writeEventCallback;

    public AlphaHandWritingInputView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AlphaHandWritingInputView(Context context, AttributeSet attrs, int defaultStyle) {
        super(context, attrs, defaultStyle);
        this.mCurrentWritingMode = 0;
        this.writeEventCallback = new Handler.Callback() { // from class: com.nuance.swype.input.AlphaHandWritingInputView.1
            @Override // android.os.Handler.Callback
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case 2:
                        KeyboardViewEx.log.d("writeEventCallback MSG_HANDLE_SUGGESTION_CANDIDATES ");
                        if (!AlphaHandWritingInputView.this.mShowInternalCandidates) {
                            Candidates candidates = ((T9WriteRecognizerListener.CandidatesWriteEvent) msg.obj).mCandidates;
                            if (candidates != null && candidates.count() > 0) {
                                CharSequence first = candidates.get(0).toString();
                                AlphaHandWritingInputView.this.handleChar(first.charAt(0));
                                break;
                            }
                        } else if (AlphaHandWritingInputView.this.mSpeechWrapper == null || !AlphaHandWritingInputView.this.mSpeechWrapper.isSpeechViewShowing()) {
                            AlphaHandWritingInputView.this.updateCandidatesList(((T9WriteRecognizerListener.CandidatesWriteEvent) msg.obj).mCandidates);
                            break;
                        }
                        break;
                    case 3:
                        KeyboardViewEx.log.d("writeEventCallback MSG_HANDLE_CHAR: ", Character.valueOf(((T9WriteRecognizerListener.CharWriteEvent) msg.obj).mChar));
                        AlphaHandWritingInputView.this.handleChar(((T9WriteRecognizerListener.CharWriteEvent) msg.obj).mChar);
                        break;
                    case 4:
                        KeyboardViewEx.log.d("writeEventCallback MSG_HANDLE_TEXT: ", ((T9WriteRecognizerListener.TextWriteEvent) msg.obj).mText);
                        AlphaHandWritingInputView.this.handleText(((T9WriteRecognizerListener.TextWriteEvent) msg.obj).mText);
                        break;
                    case 5:
                        KeyboardViewEx.log.d("writeEventCallback MSG_HANDLE_INSTANT_GESTURE_CHAR: ", Character.valueOf(((T9WriteRecognizerListener.InstantGestureWriteEvent) msg.obj).mGestureChar));
                        AlphaHandWritingInputView.this.handleInstantGestureChar(((T9WriteRecognizerListener.InstantGestureWriteEvent) msg.obj).mGestureChar, ((T9WriteRecognizerListener.InstantGestureWriteEvent) msg.obj).mCandidates);
                        break;
                    case 6:
                        AlphaHandWritingInputView.this.showStartOfWordCandidateList();
                        break;
                    case 7:
                        AlphaHandWritingInputView.this.displayNextWordPrediction();
                        break;
                    default:
                        return false;
                }
                return true;
            }
        };
        this.mWriteEventHandler = WeakReferenceHandler.create(this.writeEventCallback);
        this.delayRecognizerCallback = new Handler.Callback() { // from class: com.nuance.swype.input.AlphaHandWritingInputView.2
            @Override // android.os.Handler.Callback
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        AlphaHandWritingInputView.this.performDelayRecognition();
                        return true;
                    default:
                        return false;
                }
            }
        };
        this.mDelayRecognizeHandler = WeakReferenceHandler.create(this.delayRecognizerCallback);
        this.popupViewCallback = new Handler.Callback() { // from class: com.nuance.swype.input.AlphaHandWritingInputView.3
            @Override // android.os.Handler.Callback
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case 11:
                        if (AlphaHandWritingInputView.this.mSpeechWrapper != null && AlphaHandWritingInputView.this.mSpeechWrapper.isResumable()) {
                            AlphaHandWritingInputView.this.flushCurrentActiveWord();
                        }
                        AlphaHandWritingInputView.this.setSpeechViewHost();
                        AlphaHandWritingInputView.this.resumeSpeech();
                        return true;
                    case 503:
                        AlphaHandWritingInputView.this.showHowToUseToast();
                        return true;
                    default:
                        return false;
                }
            }
        };
        this.mPopupViewHandler = WeakReferenceHandler.create(this.popupViewCallback);
        this.mComposition = new Composition();
    }

    public void setContainerView(AlphaHandWritingContainerView container) {
        this.mContainer = container;
    }

    @Override // com.nuance.swype.input.InputView
    protected int getContainerX() {
        return this.mContainer != null ? this.mContainer.getLeft() : getLeft();
    }

    private void showHandWritingView(boolean animation) {
        AlphaAnimation animate = null;
        if (animation) {
            animate = new AlphaAnimation(0.0f, 1.0f);
            animate.setDuration(500L);
        }
        if (animate != null) {
            this.mContainer.mHandwritingPadView.setAnimation(animate);
        }
    }

    @Override // com.nuance.swype.input.InputView
    public void create(IME ime, XT9CoreInput xt9coreinput, T9Write t9write, SpeechWrapper speechWrapper) {
        super.create(ime, xt9coreinput, t9write, speechWrapper);
        this.alphaInput = (XT9CoreAlphaInput) xt9coreinput;
        this.mWriteAlpha = (T9WriteAlpha) t9write;
        this.mKeyboardSwitcher = new KeyboardSwitcher(ime, xt9coreinput);
        this.mKeyboardSwitcher.setInputView(this);
        this.shiftGestureOffset = (int) getResources().getDimension(R.dimen.candidates_list_height);
        setOnKeyboardActionListener(ime);
    }

    @Override // com.nuance.swype.input.KeyboardViewEx
    public int getShiftGestureOffset() {
        return this.shiftGestureOffset;
    }

    @Override // com.nuance.swype.input.InputView
    public void destroy() {
        super.destroy();
        this.mWriteAlpha.removeRecognizeListener(this);
        this.mCurrentWritingPad = null;
    }

    @Override // com.nuance.swype.input.InputView
    public View createCandidatesView(CandidatesListView.CandidateListener onSelectListener) {
        return super.createCandidatesView(onSelectListener);
    }

    @Override // com.nuance.swype.input.InputView
    public void displayCompletions(CompletionInfo[] completions) {
        if (this.mCompletionOn && !this.mShowInternalCandidates) {
            this.mCompletions.update(completions);
            if (this.mCompletions.size() == 0) {
                clearSuggestions();
                return;
            }
            updateCandidatesList(null);
            this.suggestionCandidates = Candidates.createCandidates(this.mCompletions.getDisplayItems(), 0, Candidates.Source.COMPLETIONS);
            setSuggestions(this, this.suggestionCandidates, CandidatesListView.Format.DEFAULT);
            syncCandidateDisplayStyleToMode();
        }
    }

    @Override // com.nuance.swype.input.InputView
    public void updateSelection(int oldSelStart, int oldSelEnd, int newSelStart, int newSelEnd, int[] candidatesIndices) {
        super.updateSelection(oldSelStart, oldSelEnd, newSelStart, newSelEnd, candidatesIndices);
        if (this.suggestionCandidates != null && this.suggestionCandidates.count() > 0 && ((newSelStart != candidatesIndices[1] || newSelEnd != candidatesIndices[1]) && candidatesIndices[1] != -1)) {
            acceptCurrentDefaultWord();
            updateCandidatesList(null);
        }
        if (newSelStart < newSelEnd) {
            this.mComposition.setSelection(newSelStart, newSelEnd);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showStartOfWordCandidateList() {
        String lastWord = IME.getLastSavedActiveWordAndSet();
        if (this.candidatesListView != null && lastWord != null && lastWord.length() > 0) {
            ArrayList<CharSequence> list = new ArrayList<>();
            list.add(lastWord);
            updateCandidatesList(Candidates.createCandidates(list, 0, Candidates.Source.HWR));
        } else if (IME.getLastShownCandidatesSource() == Candidates.Source.NEXT_WORD_PREDICTION) {
            updateNextWordPrediction();
        } else if (isEmptyCandidateList()) {
            updateNextWordPrediction();
        }
        if (this.alphaInput.getShiftState() != Shift.ShiftState.LOCKED) {
            this.alphaInput.setShiftState(Shift.ShiftState.OFF);
        }
    }

    @Override // com.nuance.swype.input.InputView
    public void startInput(InputFieldInfo inputFieldInfo, boolean restarting) {
        super.startInput(inputFieldInfo, restarting);
        if (this.candidatesListView != null) {
            this.candidatesListView.updateCandidatesSize();
        }
        if (this.emojiCandidatesListView != null) {
            this.emojiCandidatesListView.updateCandidatesSize();
        }
        if (this.mContainer != null) {
            this.mContainer.updateHandwritingPadSize();
        }
        dismissPopupKeyboard();
        showHandWritingView(true);
        this.mWriteAlpha.addRecognizeListener(this);
        setKeyboardForTextEntry(inputFieldInfo);
        this.mComposition.setInputConnection(getCurrentInputConnection());
        flushCurrentActiveWord();
        this.mWriteAlpha.setWidth(0);
        this.mWriteAlpha.setHeight(0);
        changeWriteRecognitionMode();
        this.mWriteAlpha.startSession(this.mCurrentInputLanguage.getCoreLanguageId());
        if (inputFieldInfo.isNumericModeField() || inputFieldInfo.isPhoneNumberField()) {
            this.mCurrentWritingMode = 1;
            setNumbersAndSymbolsCategory(inputFieldInfo);
        } else {
            this.mCurrentWritingMode = 0;
            setTextCategory(inputFieldInfo);
        }
        int delayMS = InputPrefs.getAutoDelay(UserPreferences.from(getContext()), UserPreferences.HWR_AUTO_ACCEPT_RECOGNIZE_ALPHA, getResources().getInteger(R.integer.handwriting_auto_recognize_alpha_default_ms));
        this.mWriteAlpha.setRecognizerDelay(delayMS);
        startAlphaInput();
        if (!this.mInputFieldInfo.isPasswordField()) {
            postDelayShowingStartOfWordCandidate();
        }
        removeHowToUseToastMsg();
        postHowToUseToastMsg();
        postDelayResumeSpeech();
        this.mWriteAlpha.enableUsageLogging(false);
        OemLdbWordsManager.from(getContext()).AddOemLdbWordsForAlpha(this.alphaInput, this.mCurrentInputLanguage.getCoreLanguageId());
        this.keyboardTouchEventDispatcher.resisterTouchKeyHandler(new DefaultHWRTouchKeyHandler(this.mIme, this, this.xt9coreinput, getDefaultKeyUIStateHandler()));
        this.keyboardTouchEventDispatcher.registerFlingGestureHandler(this.flingGestureListener);
        this.keyboardTouchEventDispatcher.wrapTouchEvent(this);
        this.xt9coreinput.setInputContextRequestListener(InputContextRequestDispatcher.getDispatcherInstance().setHandler(getDefaultInputContextHandler()));
    }

    private void postDelayResumeSpeech() {
        if (this.mPopupViewHandler.hasMessages(11)) {
            this.mPopupViewHandler.removeMessages(11);
        }
        this.mPopupViewHandler.sendEmptyMessageDelayed(11, 1L);
    }

    private void startAlphaInput() {
        this.alphaInput.startSession();
        setLanguage(this.alphaInput);
        readNextWordPredictionSettings();
    }

    private void finishAlphaInput() {
        this.alphaInput.finishSession();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showHowToUseToast() {
        AppPreferences sm = AppPreferences.from(getContext());
        if (sm.showHowToUseHandWritingTip()) {
            if (this.mCurrentWritingPad != null) {
                sm.setShowHowToUseHandWritingTip(false);
                QuickToast.show(getContext(), getResources().getString(R.string.how_to_use_hwr), 1, (getHeight() + this.mCurrentWritingPad.getHeight()) / 2);
            } else {
                postHowToUseToastMsg();
            }
        }
    }

    private void hideHowToUseToast() {
        QuickToast.hide();
    }

    private void postHowToUseToastMsg() {
        this.mPopupViewHandler.sendEmptyMessageDelayed(503, 10L);
    }

    private void removeHowToUseToastMsg() {
        this.mPopupViewHandler.removeMessages(503);
    }

    private void setKeyboardForTextEntry(InputFieldInfo inputFieldInfo) {
        this.mKeyboardSwitcher.createKeyboardForTextInput(KeyboardEx.KeyboardLayerType.KEYBOARD_TEXT, inputFieldInfo, this.mCurrentInputLanguage.getCurrentInputMode());
    }

    private void setNumbersAndSymbolsCategory(InputFieldInfo inputFieldInfo) {
        if (this.mCurrentWritingMode == 1 && !ActivityManagerCompat.isUserAMonkey()) {
            if (inputFieldInfo.isPhoneNumberField()) {
                this.mWriteAlpha.addPhoneNumberOnlyCategory();
            } else {
                this.mWriteAlpha.addDigitsAndSymbolsOnlyCategory();
            }
            this.mWriteAlpha.addGestureCategory();
            this.mWriteAlpha.applyChangedSettings();
            updateKeyLabel(this.mCurrentWritingMode);
        }
    }

    private void setTextCategory(InputFieldInfo inputFieldInfo) {
        if (this.mCurrentWritingMode == 0 && !ActivityManagerCompat.isUserAMonkey()) {
            if (inputFieldInfo.isEmailAddressField()) {
                this.mWriteAlpha.addEmailOnlyCategory();
            } else if (inputFieldInfo.isURLField()) {
                this.mWriteAlpha.addUrlOnlyCategory();
            } else {
                this.mWriteAlpha.addTextCategory();
            }
            this.mWriteAlpha.addGestureCategory();
            this.mWriteAlpha.addNumberCategory();
            this.mWriteAlpha.applyChangedSettings();
            updateKeyLabel(this.mCurrentWritingMode);
        }
    }

    @Override // com.nuance.swype.input.InputView
    public void finishInput() {
        super.finishInput();
        removeHowToUseToastMsg();
        hideHowToUseToast();
        dismissPopupKeyboard();
        acceptCurrentDefaultWord();
        this.mWriteAlpha.removeRecognizeListener(this);
        this.mWriteAlpha.finishSession();
        finishAlphaInput();
        this.mWriteAlpha.setWidth(0);
        this.mWriteAlpha.setHeight(0);
        this.keyboardTouchEventDispatcher.unwrapTouchEvent(this);
        this.xt9coreinput.setInputContextRequestListener(null);
    }

    @Override // com.nuance.swype.input.InputView
    public boolean isInputSessionStarted() {
        return this.mWriteAlpha == null || !this.mWriteAlpha.isRecognizeListenerEmpty();
    }

    @Override // com.nuance.swype.input.InputView
    public void handleClose() {
        super.handleClose();
        dismissPopupKeyboard();
        removeHowToUseToastMsg();
        hideHowToUseToast();
        this.mWriteAlpha.setWidth(0);
        this.mWriteAlpha.setHeight(0);
    }

    /* JADX WARN: Failed to find 'out' block for switch in B:2:0x0004. Please report as an issue. */
    @Override // com.nuance.swype.input.InputView
    public boolean handleKey(int primaryCode, boolean quickPressed, int repeatedCount) {
        hideHowToUseToast();
        switch (primaryCode) {
            case KeyboardEx.KEYCODE_SPEECH /* 6463 */:
                if (isPendingRecognizeMessage()) {
                    processPendingRecognizing();
                }
                flushCurrentActiveWord();
                if (quickPressed) {
                    return true;
                }
                super.startSpeech();
                return true;
            case KeyboardEx.KEYCODE_SWYPE /* 43575 */:
                boolean handled = false;
                if (this.mCurrentInputLanguage.isNonSpacedLanguage()) {
                    AppSpecificInputConnection ic = getCurrentInputConnection();
                    if (ic != null && ic.getComposingRangeInEditor() != null) {
                        handled = true;
                    }
                } else {
                    this.mComposition.acceptCurrentInline();
                }
                if (handled) {
                    return true;
                }
                break;
            default:
                return super.handleKey(primaryCode, quickPressed, repeatedCount);
        }
    }

    @Override // com.nuance.swype.input.InputView
    public void onText(CharSequence text, long eventTime) {
        if (isPendingRecognizeMessage()) {
            processPendingRecognizing();
        }
        this.mWriteAlpha.queueText(text);
    }

    @Override // com.nuance.swype.input.InputView
    public boolean handleKeyDown(int keyCode, KeyEvent event) {
        if (super.handleKeyDown(keyCode, event) || keyCode != 67) {
            return false;
        }
        return handleBackspace(event.getRepeatCount());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.InputView
    public boolean handleBackspace(int repeatedCount) {
        if (isPendingRecognizeMessage()) {
            processPendingRecognizing();
        }
        this.mWriteAlpha.queueChar('\b');
        return true;
    }

    @Override // com.nuance.swype.input.InputView
    public void handleCharKey(Point pointTapped, int primaryCode, long eventTime) {
        if (isPendingRecognizeMessage()) {
            processPendingRecognizing();
        }
        this.mWriteAlpha.queueChar((char) primaryCode);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.InputView
    public boolean handleSpace(boolean quickPressed, int repeatedCount) {
        if (isPendingRecognizeMessage()) {
            processPendingRecognizing();
            this.mWriteAlpha.queueChar(' ');
            return true;
        }
        if (!isEmptyCandidateList()) {
            this.mWriteAlpha.queueChar(' ');
            return true;
        }
        handleSpace();
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.InputView, com.nuance.swype.input.KeyboardViewEx
    public boolean handleLongPress(KeyboardEx.Key key) {
        return 8 == key.codes[0] ? handleDeleteWordBack(key) : super.handleLongPress(key);
    }

    @Override // com.nuance.swype.input.InputView
    public CharSequence getCurrentDefaultWord() {
        if (isEmptyCandidateList() || isNextWordPredictionCandidates() || isCompletionCandidates()) {
            return null;
        }
        return this.suggestionCandidates.get(0).toString();
    }

    @Override // com.nuance.swype.input.InputView
    public Candidates.Source getCurrentWordCandidatesSource() {
        return this.suggestionCandidates != null ? this.suggestionCandidates.source() : Candidates.Source.INVALID;
    }

    @Override // com.nuance.swype.input.InputView
    public void clearCurrentActiveWord() {
        cancelCurrentDefaultWord();
    }

    private CharSequence removeOneCharacterFromCandidateList() {
        if (this.suggestionCandidates == null || this.suggestionCandidates.count() <= 0) {
            return null;
        }
        CharSequence seq = this.suggestionCandidates.get(0).toString();
        return seq.subSequence(0, seq.length() - 1);
    }

    private void handleSpace() {
        if (this.suggestionCandidates != null && !isNextWordPredictionCandidates()) {
            selectCandidate(this.suggestionCandidates.get(0), true, false);
        } else {
            sendSpace();
        }
    }

    private void deleteOneCharacter() {
        if (!isEmptyCandidateList()) {
            if (isNextWordPredictionCandidates()) {
                cancelCurrentDefaultWord();
                if (getCurrentInputConnection() != null && getCurrentInputConnection().hasSelection()) {
                    getCurrentInputConnection().clearHighlightedText();
                } else {
                    sendBackspace(0);
                }
                if (isEmptyCandidateList()) {
                    updateNextWordPrediction();
                    return;
                }
                return;
            }
            if (getCurrentInputLanguage() != null && getCurrentInputLanguage().isNonSpacedLanguage() && getCurrentInputConnection() != null && getCurrentInputConnection().hasSelection()) {
                getCurrentInputConnection().clearHighlightedText();
                if (isEmptyCandidateList()) {
                    updateNextWordPrediction();
                    return;
                }
                return;
            }
            if (this.suggestionCandidates.source() == Candidates.Source.COMPLETIONS) {
                this.mIme.sendBackspace(0);
                if (isEmptyCandidateList()) {
                    updateNextWordPrediction();
                    return;
                }
                return;
            }
            CharSequence seq = removeOneCharacterFromCandidateList();
            if (seq == null || seq.length() == 0) {
                cancelCurrentDefaultWord();
                if (isEmptyCandidateList()) {
                    updateNextWordPrediction();
                    return;
                }
                return;
            }
            this.suggestionCandidates = null;
            ArrayList<CharSequence> list = new ArrayList<>();
            list.add(seq);
            this.suggestionCandidates = Candidates.createCandidates(list, 0, Candidates.Source.HWR);
            updateCandidatesList(this.suggestionCandidates);
            return;
        }
        clearSuggestions();
        if (getCurrentInputConnection() != null && getCurrentInputConnection().hasSelection()) {
            getCurrentInputConnection().clearHighlightedText();
        } else {
            sendBackspace(0);
        }
        this.alphaInput.setContext(null);
        if (isEmptyCandidateList()) {
            updateNextWordPrediction();
        }
    }

    private boolean isNextWordPredictionCandidates() {
        return this.suggestionCandidates != null && this.suggestionCandidates.source() == Candidates.Source.NEXT_WORD_PREDICTION;
    }

    private boolean isCompletionCandidates() {
        return this.suggestionCandidates != null && this.suggestionCandidates.source() == Candidates.Source.COMPLETIONS;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleChar(char ch) {
        log.d("handleChar: ", Character.valueOf(ch));
        switch (ch) {
            case '\b':
                if (this.mWriteAlpha.getRecognitionMode() != 2) {
                    deleteOneCharacter();
                    return;
                }
                if (!isEmptyCandidateList() && this.suggestionCandidates.source() == Candidates.Source.HWR) {
                    CharSequence cs = getCurrentDefaultWord();
                    if (cs != null && cs.length() <= 1) {
                        deleteOneCharacter();
                        return;
                    } else {
                        cancelCurrentDefaultWord();
                        return;
                    }
                }
                deleteOneCharacter();
                return;
            case '\r':
                acceptCurrentDefaultWord();
                sendKeyChar('\n');
                return;
            case ' ':
                handleSpace();
                return;
            case T9WriteCategory.MULTITOUCH_HORIZONTAL_SWIPE_LEFT_UNICODE_VALUE /* 8656 */:
                if (!isEmptyCandidateList()) {
                    cancelCurrentDefaultWord();
                    return;
                } else {
                    deleteWordToLeftOfCursor();
                    return;
                }
            case T9WriteCategory.MULTITOUCH_VERTICAL_SWIPE_UP_UNICODE_VALUE /* 8657 */:
                if (!isEmptyCandidateList()) {
                    upShiftCandidateList();
                    return;
                }
                return;
            case T9WriteCategory.MULTITOUCH_HORIZONTAL_SWIPE_RIGHT_UNICODE_VALUE /* 8658 */:
                if (!isEmptyCandidateList()) {
                    cancelCurrentDefaultWord();
                    return;
                } else {
                    deleteWordToRightOfCursor();
                    return;
                }
            case T9WriteCategory.MULTITOUCH_VERTICAL_SWIPE_DOWN_UNICODE_VALUE /* 8659 */:
                if (!isEmptyCandidateList()) {
                    downShiftCandidateList();
                    return;
                }
                return;
            default:
                acceptCurrentDefaultWord();
                sendKeyChar(ch);
                return;
        }
    }

    @SuppressLint({"DefaultLocale"})
    private void upShiftCandidateList() {
        if (this.suggestionCandidates != null) {
            int defaultIndex = this.suggestionCandidates.getDefaultCandidateIndex();
            int exactIndex = this.suggestionCandidates.getExactCandidateIndex();
            Candidates shiftedCandidates = new Candidates(this.suggestionCandidates.source());
            for (int i = 0; i < this.suggestionCandidates.count(); i++) {
                WordCandidate word = this.suggestionCandidates.get(i);
                shiftedCandidates.add(new WordCandidate(word.toString().toUpperCase(), word.completionLength(), word.attribute(), word.id()));
            }
            shiftedCandidates.setDefaultIndex(defaultIndex);
            shiftedCandidates.setExactIndex(exactIndex);
            updateCandidatesList(shiftedCandidates);
        }
    }

    @SuppressLint({"DefaultLocale"})
    private void downShiftCandidateList() {
        if (this.suggestionCandidates != null) {
            int defaultIndex = this.suggestionCandidates.getDefaultCandidateIndex();
            int exactIndex = this.suggestionCandidates.getExactCandidateIndex();
            Candidates unShiftedCandidates = new Candidates(this.suggestionCandidates.source());
            for (int i = 0; i < this.suggestionCandidates.count(); i++) {
                WordCandidate word = this.suggestionCandidates.get(i);
                unShiftedCandidates.add(new WordCandidate(word.toString().toLowerCase(), word.completionLength(), word.attribute(), word.id()));
            }
            unShiftedCandidates.setDefaultIndex(defaultIndex);
            unShiftedCandidates.setExactIndex(exactIndex);
            updateCandidatesList(unShiftedCandidates);
        }
    }

    private void deleteWordToRightOfCursor() {
        this.mComposition.deleteWordToRightOfCursor();
    }

    private void deleteWordToLeftOfCursor() {
        this.mComposition.deleteWordToLeftOfCursor();
    }

    private boolean isPendingRecognizeMessage() {
        return this.mDelayRecognizeHandler.hasMessages(1);
    }

    private void processPendingRecognizing() {
        this.mDelayRecognizeHandler.removeMessages(1);
        performDelayRecognition();
    }

    private void cancelCurrentDefaultWord() {
        if (isInlineActive()) {
            this.mComposition.clearCurrentInline();
        }
        clearSuggestions();
    }

    private boolean isInlineActive() {
        return (this.candidatesListView == null || this.mWriteAlpha == null || this.mComposition.length() <= 0) ? false : true;
    }

    @Override // com.nuance.swype.input.InputView
    public void flushCurrentActiveWord() {
        acceptCurrentDefaultWord();
    }

    private void acceptCurrentDefaultWord() {
        this.mComposition.acceptCurrentInline();
        clearSuggestions();
    }

    @Override // com.nuance.swype.input.InputView, com.nuance.swype.input.CandidatesListView.CandidateListener
    public boolean onPressHoldCandidate(WordCandidate candidate, Candidates candidates) {
        if (candidate != null) {
            log.d("onPressHoldCandidate()", " draw called ::  left:: " + candidate.getLeft() + ", top:: " + candidate.getTop() + ", Width :: " + candidate.getWidth() + " , height :: " + candidate.getHeight());
            if (EmojiLoader.isEmoji(candidate.toString())) {
                return handlePressHoldCandidate(candidate, candidates);
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
        if (candidate == null) {
            return false;
        }
        super.setCurrentSkinTone();
        super.dismissEmojiPopup();
        clearSuggestions();
        showNextWordPrediction();
        sendSpace();
        return true;
    }

    @Override // com.nuance.swype.input.InputView, com.nuance.swype.input.CandidatesListView.CandidateListener
    public void onPressMoveCandidate(float xPos, float yPos, float xOffset) {
        log.d("onPressMoveCandidate()", " called ::  xPos:: " + xPos + ", yPos:: " + yPos + ", xOffset :: " + xOffset);
        super.touchMoveHandle(xPos, yPos, xOffset);
    }

    @Override // com.nuance.swype.input.InputView, com.nuance.swype.input.CandidatesListView.CandidateListener
    public boolean onSelectCandidate(WordCandidate candidate, Candidates candidates) {
        this.suggestionCandidates = candidates;
        boolean addSpace = (this.mInURLEmail || this.mCurrentInputLanguage.isNonSpacedLanguage() || this.mWriteAlpha.getRecognitionMode() == 0) ? false : true;
        selectCandidate(candidate, addSpace, true);
        if (candidate != null) {
            super.addEmojiInRecentCat(candidate);
        }
        return true;
    }

    @Override // com.nuance.swype.input.InputView, com.nuance.swype.input.CandidatesListView.CandidateListener
    public void onCandidatesUpdated(Candidates candidates) {
    }

    private void selectCandidate(WordCandidate candidate, boolean addSpace, boolean userExplicitPick) {
        CharSequence seq;
        AppSpecificInputConnection ic = getCurrentInputConnection();
        if (ic != null) {
            ic.beginBatchEdit();
            boolean showNWP = isNextWordPredictionCandidates() || (this.suggestionCandidates != null && this.suggestionCandidates.source() == Candidates.Source.HWR);
            if (this.suggestionCandidates.source() == Candidates.Source.HWR) {
                this.mWriteAlpha.noteSelectedCandidate(this.suggestionCandidates.getCandidates().indexOf(candidate));
            }
            int index = candidate.id();
            if (this.suggestionCandidates.source() == Candidates.Source.COMPLETIONS) {
                if (this.mCompletionOn && index >= 0 && index < this.mCompletions.size()) {
                    ic.commitCompletion(this.mCompletions.get(index));
                    addSpace = false;
                }
            } else if (!this.suggestionCandidates.isReadOnly()) {
                if ((candidate.source() != WordCandidate.Source.WORD_SOURCE_NEW_WORD) && this.alphaInput.removeSpaceBeforeWord(candidate.id()) && (seq = ic.getTextBeforeCursor(2, 0)) != null && seq.length() == 2 && !CharacterUtilities.isWhiteSpace(seq.charAt(0)) && seq.charAt(seq.length() - 1) == ' ') {
                    ic.deleteSurroundingText(1, 0);
                }
                this.mComposition.insertText(candidate.toString());
                clearSuggestions();
                if (this.mInputFieldInfo != null && !this.mInputFieldInfo.isPasswordField() && this.mInputFieldInfo.textInputFieldWithSuggestionEnabled()) {
                    this.alphaInput.setWordQuarantineLevel(1, 1, 1);
                    this.alphaInput.wordSelected(candidate.id(), userExplicitPick);
                    learnNewWords();
                }
            } else {
                clearSuggestions();
            }
            if (addSpace) {
                sendSpace();
            }
            ic.endBatchEdit();
            if (showNWP && this.mNextWordPredictionOn) {
                updateNextWordPrediction();
            }
            EditState state = this.mIme.getEditState();
            if (state != null && (state instanceof StatisticsEnabledEditState)) {
                ((StatisticsEnabledEditState) state).reportWritingResults(candidate.toString());
            }
        }
    }

    private void updateNextWordPrediction() {
        this.mWriteEventHandler.removeMessages(7);
        this.mWriteEventHandler.sendEmptyMessageDelayed(7, 10L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void displayNextWordPrediction() {
        if (this.mNextWordPredictionOn && this.mStarted) {
            updateWordContext();
            Candidates nwpCandidates = this.alphaInput.getCandidates(Candidates.Source.NEXT_WORD_PREDICTION);
            updateCandidatesList(nwpCandidates);
        }
    }

    private void changeWriteRecognitionMode() {
        if (!this.mShowInternalCandidates || UserPreferences.from(getContext()).isHwrScrmodeEnabled()) {
            this.mWriteAlpha.enableInstantGesture(false);
            this.mWriteAlpha.setRecognitionMode(0);
        } else if (this.mCurrentInputLanguage.getCurrentInputMode().isHandwritingUCRModeEnabled()) {
            this.mWriteAlpha.enableInstantGesture(false);
            this.mWriteAlpha.setRecognitionMode(2);
        } else {
            this.mWriteAlpha.enableInstantGesture(true);
            this.mWriteAlpha.setRecognitionMode(1);
        }
    }

    private void changeWriteSettings(AlphaHandWritingView view) {
        if (this.mWriteAlpha != null) {
            this.mWriteAlpha.setWidth(view.getWidth());
            this.mWriteAlpha.setHeight(view.getHeight());
            this.mWriteAlpha.applyChangedSettings();
        }
    }

    public void setHandWritingView(AlphaHandWritingView view) {
        this.mCurrentWritingPad = view;
    }

    @Override // com.nuance.swype.input.AlphaHandWritingView.OnWritingAction
    public void penDown(View src) {
        dismissPopupKeyboard();
        removeDelayRecognitionMsg();
        if (this.mCurrentWritingPad == null || this.mCurrentWritingPad != src) {
            this.mCurrentWritingPad = (AlphaHandWritingView) src;
        }
        if (this.mWriteAlpha != null && !ActivityManagerCompat.isUserAMonkey() && (this.mCurrentWritingPad.getWidth() != this.mWriteAlpha.getWidth() || this.mCurrentWritingPad.getHeight() != this.mWriteAlpha.getHeight())) {
            changeWriteSettings(this.mCurrentWritingPad);
        }
        if (this.mWriteAlpha != null && !ActivityManagerCompat.isUserAMonkey()) {
            this.mWriteAlpha.startArcsAddingSequence();
        }
    }

    @Override // com.nuance.swype.input.AlphaHandWritingView.OnWritingAction
    public void penUp(Stroke.Arc[] arcs, View src) {
        CharSequence startWord = null;
        if (!isEmptyCandidateList()) {
            startWord = this.suggestionCandidates.get(0).toString();
        }
        if (this.mCurrentWritingPad != null) {
            this.mCurrentWritingPad.startFading();
        }
        if (this.mWriteAlpha != null && !ActivityManagerCompat.isUserAMonkey()) {
            this.mWriteAlpha.queueAddArcs(new ArrayList(arcs[0].mPoints), arcs.length > 1 ? new ArrayList(arcs[1].mPoints) : null, startWord);
        }
        if (this.mDelayRecognizeHandler != null) {
            this.mDelayRecognizeHandler.sendEmptyMessageDelayed(1, this.mWriteAlpha.getRecognizerDelay());
        }
    }

    @Override // com.nuance.swype.input.AlphaHandWritingView.OnWritingAction
    public void penUp(List<Point> arc, View src) {
    }

    private void removeDelayRecognitionMsg() {
        this.mDelayRecognizeHandler.removeMessages(1);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateCandidatesList(Candidates candidates) {
        if (!this.mStarted) {
            this.suggestionCandidates = null;
            return;
        }
        this.suggestionCandidates = candidates;
        if (!isEmptyCandidateList()) {
            if ((this.suggestionCandidates.source() == Candidates.Source.NEXT_WORD_PREDICTION || this.suggestionCandidates.source() == Candidates.Source.COMPLETIONS) ? false : true) {
                this.mComposition.showInline(this.suggestionCandidates.get(0).toString());
            }
            if (candidates.source() == Candidates.Source.HWR) {
                appendCandidatesFromAlphaInput();
            }
            setSuggestions(this, this.suggestionCandidates, CandidatesListView.Format.DEFAULT);
        } else {
            this.suggestionCandidates = null;
            setSuggestions(this, null, CandidatesListView.Format.NONE);
        }
        syncCandidateDisplayStyleToMode();
    }

    private void appendCandidatesFromAlphaInput() {
        int hwrWordCount = this.suggestionCandidates.count();
        for (int i = 0; i < hwrWordCount; i++) {
            String candidate = this.suggestionCandidates.get(i).toString();
            if (candidate.contains("'") && !candidate.endsWith("'")) {
                return;
            }
        }
        Candidates[] candidatesList = new Candidates[hwrWordCount];
        int[] lastAddedWordIndex = new int[hwrWordCount];
        int[] addedWords = new int[hwrWordCount];
        int alphaInputWordCount = 10 - hwrWordCount;
        for (int i2 = 0; i2 < hwrWordCount; i2++) {
            candidatesList[i2] = getAlphaInputCandidates(this.suggestionCandidates.get(i2).toString(), 10);
        }
        int remainingWord = hwrWordCount;
        while (alphaInputWordCount > 0 && remainingWord > 0) {
            int alphaInputWordCountPerCandidate = alphaInputWordCount / remainingWord;
            for (int i3 = 0; i3 < hwrWordCount && alphaInputWordCount > 0; i3++) {
                if (lastAddedWordIndex[i3] == candidatesList[i3].count()) {
                    remainingWord--;
                } else {
                    int itemsAdded = 0;
                    int wordIndex = lastAddedWordIndex[i3];
                    while (true) {
                        if (wordIndex >= candidatesList[i3].count()) {
                            break;
                        }
                        int position = 0;
                        for (int j = 0; j <= i3; j++) {
                            position += addedWords[j];
                        }
                        if (this.suggestionCandidates.add(position + hwrWordCount, candidatesList[i3].get(wordIndex))) {
                            alphaInputWordCount--;
                            addedWords[i3] = addedWords[i3] + 1;
                            itemsAdded++;
                            if (itemsAdded >= alphaInputWordCountPerCandidate) {
                                wordIndex++;
                                break;
                            }
                        }
                        wordIndex++;
                    }
                    lastAddedWordIndex[i3] = wordIndex;
                    if (lastAddedWordIndex[i3] == candidatesList[i3].count()) {
                        remainingWord--;
                    }
                }
            }
        }
    }

    private Candidates getAlphaInputCandidates(CharSequence word, int maxWordList) {
        this.alphaInput.clearAllKeys();
        for (int i = 0; i < word.length(); i++) {
            Shift.ShiftState shiftState = Character.isUpperCase(word.charAt(i)) ? Shift.ShiftState.ON : Shift.ShiftState.OFF;
            this.alphaInput.addExplicit(new char[]{word.charAt(i)}, 1, shiftState);
        }
        Candidates result = this.alphaInput.getCandidates(Candidates.Source.HWR, maxWordList);
        this.alphaInput.clearAllKeys();
        return result;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleText(CharSequence text) {
        acceptCurrentDefaultWord();
        this.mComposition.insertText(text);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleInstantGestureChar(char gestureChar, Candidates candidates) {
        removeDelayRecognitionMsg();
        if (candidates.count() > 0) {
            this.mComposition.acceptCurrentInline();
            updateCandidatesList(candidates);
        }
        handleChar(gestureChar);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void performDelayRecognition() {
        CharSequence startWord = null;
        if (!isEmptyCandidateList() && !isNextWordPredictionCandidates() && this.suggestionCandidates.source() != Candidates.Source.COMPLETIONS) {
            startWord = this.suggestionCandidates.get(0).toString();
        }
        if (this.mWriteAlpha != null && !ActivityManagerCompat.isUserAMonkey()) {
            this.mWriteAlpha.queueRecognition(startWord);
        }
        if (this.mCurrentWritingPad != null) {
            this.mCurrentWritingPad.clearAll();
        }
        invalidate();
    }

    private void postDelayShowingStartOfWordCandidate() {
        this.mWriteEventHandler.sendEmptyMessageDelayed(6, 10L);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.InputView
    public void removeAllMessages() {
        this.mWriteEventHandler.removeMessages(2);
        this.mWriteEventHandler.removeMessages(4);
        this.mWriteEventHandler.removeMessages(3);
        this.mWriteEventHandler.removeMessages(5);
        this.mWriteEventHandler.removeMessages(6);
        this.mWriteEventHandler.removeMessages(7);
        for (int msg = 1; msg <= 19; msg++) {
            this.mPopupViewHandler.removeMessages(msg);
        }
        this.mPopupViewHandler.removeMessages(11);
        removeDelayRecognitionMsg();
    }

    @Override // com.nuance.input.swypecorelib.T9WriteRecognizerListener.OnWriteRecognizerListener
    public void onHandleWriteEvent(T9WriteRecognizerListener.WriteEvent event) {
        if (this.mIme == null || isValidBuild()) {
            switch (event.mType) {
                case 1:
                    this.mWriteEventHandler.sendMessageDelayed(this.mWriteEventHandler.obtainMessage(2, event), 5L);
                    return;
                case 2:
                    this.mWriteEventHandler.sendMessageDelayed(this.mWriteEventHandler.obtainMessage(3, event), 5L);
                    return;
                case 3:
                    this.mWriteEventHandler.sendMessageDelayed(this.mWriteEventHandler.obtainMessage(4, event), 5L);
                    return;
                case 4:
                    this.mWriteEventHandler.sendMessageDelayed(this.mWriteEventHandler.obtainMessage(5, event), 5L);
                    return;
                default:
                    return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.InputView
    public void changeKeyboardMode() {
        if (isPendingRecognizeMessage()) {
            processPendingRecognizing();
        }
        if (this.mCurrentWritingMode == 0) {
            this.mCurrentWritingMode = 1;
            setNumbersAndSymbolsCategory(getInputFieldInfo());
        } else if (this.mCurrentWritingMode == 1) {
            this.mCurrentWritingMode = 0;
            setTextCategory(getInputFieldInfo());
        }
    }

    @Override // com.nuance.swype.input.InputView
    public KeyboardEx.KeyboardLayerType getKeyboardLayer() {
        if (this.mCurrentWritingMode == 0) {
            return KeyboardEx.KeyboardLayerType.KEYBOARD_TEXT;
        }
        if (this.mCurrentWritingMode == 1) {
            return KeyboardEx.KeyboardLayerType.KEYBOARD_SYMBOLS;
        }
        return KeyboardEx.KeyboardLayerType.KEYBOARD_INVALID;
    }

    @Override // com.nuance.swype.input.InputView
    public void setKeyboardLayer(KeyboardEx.KeyboardLayerType layer) {
        super.setKeyboardLayer(layer);
        if (layer == KeyboardEx.KeyboardLayerType.KEYBOARD_TEXT && this.mCurrentWritingMode != 0) {
            this.mCurrentWritingMode = 0;
            setTextCategory(getInputFieldInfo());
        } else if (layer == KeyboardEx.KeyboardLayerType.KEYBOARD_SYMBOLS && this.mCurrentWritingMode != 1) {
            this.mCurrentWritingMode = 1;
            setNumbersAndSymbolsCategory(getInputFieldInfo());
        }
    }

    @Override // com.nuance.swype.input.InputView, com.nuance.swype.input.KeyboardViewEx
    public void setKeyboard(KeyboardEx keyboard) {
        updateDockModeForHandwriting(keyboard);
        super.setKeyboard(keyboard);
    }

    @SuppressLint({"DefaultLocale"})
    private void updateKeyLabel(int mode) {
        KeyboardEx.Key altKey;
        List<KeyboardEx.Key> keys = getKeyboard().getKeys();
        if (keys != null && (altKey = findKeyByKeyCode(keys, KeyboardEx.KEYCODE_MODE_CHANGE)) != null) {
            CharSequence currentMode = null;
            if (mode == 0) {
                altKey.on = false;
                currentMode = this.mIme.getResources().getText(R.string.label_alpha_key);
            } else if (mode == 1) {
                altKey.on = true;
                currentMode = this.mIme.getResources().getText(R.string.label_num_key);
            }
            if (this.mCurrentWritingPad != null && currentMode != null) {
                String writingMode = currentMode.toString().toLowerCase() + "...";
                this.mCurrentWritingPad.setWritingMode(writingMode);
                this.mCurrentWritingPad.invalidate();
            }
            invalidateKey(altKey);
        }
    }

    @Override // com.nuance.swype.input.InputView
    public void onApplicationUnbind() {
        super.onApplicationUnbind();
        removeHowToUseToastMsg();
        hideHowToUseToast();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.InputView
    public void resumeSpeech() {
        if (this.mContainer != null) {
            super.resumeSpeech();
        }
    }

    @Override // com.nuance.swype.input.InputView
    public boolean isSupportRecapture() {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.InputView
    public void preUpdateSpeechText() {
        flushCurrentActiveWord();
    }

    @Override // com.nuance.swype.input.InputView
    public void showNextWordPrediction() {
        if (this.mNextWordPredictionOn) {
            String lastWord = IME.getLastSavedActiveWord();
            if (lastWord == null || lastWord.length() <= 0) {
                updateSuggestions(Candidates.Source.NEXT_WORD_PREDICTION, true);
            }
        }
    }

    @Override // com.nuance.swype.input.InputView
    public boolean isHandWritingInputView() {
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.InputView
    public void setLanguage(XT9CoreInput coreInput) {
        XT9Status status = this.mCurrentInputLanguage.setLanguage(coreInput);
        if (status == XT9Status.ET9STATUS_DB_CORE_INCOMP || status == XT9Status.ET9STATUS_LDB_VERSION_ERROR) {
            DatabaseConfig.removeIncompatibleDBFiles(getContext(), this.mCurrentInputLanguage.mEnglishName);
            XT9Status status2 = this.mCurrentInputLanguage.setLanguage(coreInput);
            if (status2 == XT9Status.ET9STATUS_DB_CORE_INCOMP || status2 == XT9Status.ET9STATUS_LDB_VERSION_ERROR) {
                this.isLDBCompatible = false;
                promptUserToUpdateLanguage();
                if (this.mIme != null) {
                    this.mIme.getInputMethods().setCurrentLanguageById(265);
                    this.mIme.switchInputViewAsync(10);
                }
            }
        }
    }
}
