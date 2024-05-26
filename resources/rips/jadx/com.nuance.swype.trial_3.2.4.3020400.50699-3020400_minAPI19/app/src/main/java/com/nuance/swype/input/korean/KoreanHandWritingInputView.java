package com.nuance.swype.input.korean;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputConnection;
import com.nuance.android.compat.ActivityManagerCompat;
import com.nuance.android.util.WeakReferenceHandler;
import com.nuance.input.swypecorelib.Candidates;
import com.nuance.input.swypecorelib.Shift;
import com.nuance.input.swypecorelib.T9Write;
import com.nuance.input.swypecorelib.T9WriteKorean;
import com.nuance.input.swypecorelib.T9WriteRecognizerListener;
import com.nuance.input.swypecorelib.WordCandidate;
import com.nuance.input.swypecorelib.XT9CoreInput;
import com.nuance.input.swypecorelib.XT9CoreKoreanInput;
import com.nuance.swype.input.AppPreferences;
import com.nuance.swype.input.CandidatesListView;
import com.nuance.swype.input.Composition;
import com.nuance.swype.input.HandWritingView;
import com.nuance.swype.input.IME;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.InputFieldInfo;
import com.nuance.swype.input.InputView;
import com.nuance.swype.input.KeyboardEx;
import com.nuance.swype.input.KeyboardSwitcher;
import com.nuance.swype.input.QuickToast;
import com.nuance.swype.input.R;
import com.nuance.swype.input.SpeechWrapper;
import com.nuance.swype.input.Stroke;
import com.nuance.swype.input.UserPreferences;
import com.nuance.swype.input.chinese.CJKCandidatesListView;
import com.nuance.swype.input.chinese.CJKWordListViewContainer;
import com.nuance.swype.input.emoji.EmojiInfo;
import com.nuance.swype.input.emoji.EmojiLoader;
import com.nuance.swype.input.keyboard.DefaultHWRTouchKeyHandler;
import com.nuance.swype.input.keyboard.InputContextRequestDispatcher;
import com.nuance.swype.input.settings.InputPrefs;
import com.nuance.swype.input.view.InputContainerView;
import com.nuance.swype.util.CharacterUtilities;
import com.nuance.swype.util.LogManager;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class KoreanHandWritingInputView extends InputView implements T9WriteRecognizerListener.OnWriteRecognizerListener, HandWritingView.InSelectionAreaListener, HandWritingView.OnWritingAction, CJKCandidatesListView.OnWordSelectActionListener {
    private static final int END_SELECTION_GRID_CONTAINER = -1;
    private static final int EVENT_ON_CANDIDATELIST = 1;
    private static final int EVENT_ON_INITIAL = -1;
    private static final int EVENT_ON_KEYBOARD = 0;
    private static final int HIDE_IME = -2;
    private static final int MSG_DELAY_RECOGNIZER = 1;
    static final int MSG_DELAY_RECOGNIZER_ADD_STROKE = 2;
    private static final int MSG_DELAY_SHOWING_FULLSCREEN = 1;
    private static final int MSG_HANDLE_CHAR = 3;
    private static final int MSG_HANDLE_SHOW_START_OF_WORD_CANDIDATE = 5;
    private static final int MSG_HANDLE_SUGGESTION_CANDIDATES = 2;
    private static final int MSG_HANDLE_TEXT = 4;
    private static final int MSG_SHOW_HOW_TO_TOAST = 503;
    private static final int WRITING_MODE_SYMBOLS_AND_DIGITS = 1;
    private static final int WRITING_MODE_TEXT = 0;
    private final Handler.Callback delayRecognizerHandlerCallback;
    private final Handler.Callback delayShowingFullScreenHandlerCallback;
    private final Handler.Callback hwrHandlerCallback;
    private boolean isContextCandidates;
    private List<Point> mCachedPoints;
    private final Composition mComposition;
    private KoreanHandWritingContainerView mContainer;
    private int mCurrentWritingMode;
    private KoreanHandWritingView mCurrentWritingPad;
    private final Handler mDelayRecognizeHandler;
    private final Handler mDelayShowingFullScreenHandler;
    private boolean mFullScreenHandWriting;
    private final Handler mHwrHandler;
    int mKeyboardHeight;
    private XT9CoreKoreanInput mKoreanInput;
    private final Handler mPopupViewHandler;
    private List<CharSequence> mRecognitionCandidates;
    private int mSelectionAreaOption;
    private T9WriteKorean mWriteKorean;
    private boolean mWritingOrRecognizing;
    private final Handler.Callback popupViewHandlerCallback;
    private static int mWordListHeight = 0;
    private static final LogManager.Log log = LogManager.getLog("KoreanHandWritingInputView");

    public KoreanHandWritingInputView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KoreanHandWritingInputView(Context context, AttributeSet attrs, int defaultStyle) {
        super(context, attrs, defaultStyle);
        this.isContextCandidates = false;
        this.mCurrentWritingMode = 0;
        this.mSelectionAreaOption = -1;
        this.mKeyboardHeight = 0;
        this.mCachedPoints = new ArrayList();
        this.mWritingOrRecognizing = false;
        this.hwrHandlerCallback = new Handler.Callback() { // from class: com.nuance.swype.input.korean.KoreanHandWritingInputView.1
            @Override // android.os.Handler.Callback
            public boolean handleMessage(Message msg) {
                char ch;
                switch (msg.what) {
                    case 2:
                        if (msg.obj != null && KoreanHandWritingInputView.this.mCurrentWritingPad != null) {
                            List<CharSequence> list = ((T9WriteRecognizerListener.CandidatesWriteEvent) msg.obj).mChCandidates;
                            char chGesture = 0;
                            if (list.size() > 0) {
                                CharSequence seq = list.get(0);
                                if (seq.length() == 1 && ((ch = seq.charAt(0)) == '\b' || ch == '\n' || ch == '\r' || ch == ' ' || ch == '\t')) {
                                    list = null;
                                    chGesture = ch;
                                }
                            }
                            if (chGesture != 0) {
                                KoreanHandWritingInputView.this.handleChar(chGesture);
                            }
                            KoreanHandWritingInputView.this.isContextCandidates = false;
                            KoreanHandWritingInputView.log.d("updateCandidatesList()", " called  :: MSG_HANDLE_SUGGESTION_CANDIDATES : ");
                            KoreanHandWritingInputView.this.updateCandidatesList(list);
                            if (KoreanHandWritingInputView.this.mRecognitionCandidates != null && KoreanHandWritingInputView.this.mRecognitionCandidates.isEmpty()) {
                                KoreanHandWritingInputView.this.acceptCurrentActiveWord();
                                KoreanHandWritingInputView.this.setNotMatchToolTipSuggestion();
                                break;
                            }
                        }
                        break;
                    case 3:
                        KoreanHandWritingInputView.this.handleChar(((T9WriteRecognizerListener.CharWriteEvent) msg.obj).mChar);
                        break;
                    case 4:
                        KoreanHandWritingInputView.log.d("updateCandidatesList()", " called  :: MSG_HANDLE_TEXT : ");
                        KoreanHandWritingInputView.this.handleText(((T9WriteRecognizerListener.TextWriteEvent) msg.obj).mText);
                        break;
                    case 5:
                        KoreanHandWritingInputView.log.d("updateCandidatesList()", " called  :: MSG_HANDLE_SHOW_START_OF_WORD_CANDIDATE : ");
                        KoreanHandWritingInputView.this.showStartOfWordCandidateList();
                        break;
                    default:
                        return false;
                }
                return true;
            }
        };
        this.mHwrHandler = WeakReferenceHandler.create(this.hwrHandlerCallback);
        this.delayRecognizerHandlerCallback = new Handler.Callback() { // from class: com.nuance.swype.input.korean.KoreanHandWritingInputView.2
            @Override // android.os.Handler.Callback
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        KoreanHandWritingInputView.log.d("updateCandidatesList()", " called  :: MSG_DELAY_RECOGNIZER : ");
                        KoreanHandWritingInputView.this.performDelayRecognition();
                        if (KoreanHandWritingInputView.this.mCurrentWritingPad != null) {
                            KoreanHandWritingInputView.this.mCurrentWritingPad.setNewSession(true);
                            break;
                        }
                        break;
                    case 2:
                        KoreanHandWritingInputView.log.d("updateCandidatesList()", " called  :: MSG_DELAY_RECOGNIZER_ADD_STROKE : ");
                        KoreanHandWritingInputView.this.performDelayRecognitionStroke();
                        break;
                    default:
                        return false;
                }
                return true;
            }
        };
        this.mDelayRecognizeHandler = WeakReferenceHandler.create(this.delayRecognizerHandlerCallback);
        this.popupViewHandlerCallback = new Handler.Callback() { // from class: com.nuance.swype.input.korean.KoreanHandWritingInputView.3
            @Override // android.os.Handler.Callback
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case 11:
                        KoreanHandWritingInputView.this.resumeSpeech();
                        return true;
                    case 503:
                        if (KoreanHandWritingInputView.this.mCurrentWritingPad == null || !KoreanHandWritingInputView.this.mCurrentWritingPad.isShown()) {
                            KoreanHandWritingInputView.this.mPopupViewHandler.sendEmptyMessageDelayed(503, 5L);
                        } else {
                            KoreanHandWritingInputView.this.showHowToUseToast();
                        }
                        return true;
                    default:
                        return false;
                }
            }
        };
        this.mPopupViewHandler = WeakReferenceHandler.create(this.popupViewHandlerCallback);
        this.delayShowingFullScreenHandlerCallback = new Handler.Callback() { // from class: com.nuance.swype.input.korean.KoreanHandWritingInputView.4
            @Override // android.os.Handler.Callback
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        KoreanHandWritingInputView.this.showDelayFullScreen();
                        return true;
                    default:
                        return false;
                }
            }
        };
        this.mDelayShowingFullScreenHandler = WeakReferenceHandler.create(this.delayShowingFullScreenHandlerCallback);
        this.mComposition = new Composition();
    }

    @Override // com.nuance.swype.input.InputView
    public void create(IME ime, XT9CoreInput xt9coreinput, T9Write t9write, SpeechWrapper speechWrapper) {
        super.create(ime, xt9coreinput, t9write, speechWrapper);
        this.mKoreanInput = IMEApplication.from(ime).getSwypeCoreLibMgr().getXT9CoreKoreanInputSession();
        this.mWriteKorean = (T9WriteKorean) t9write;
        this.mKeyboardSwitcher = new KeyboardSwitcher(ime, xt9coreinput);
        this.mKeyboardSwitcher.setInputView(this);
        setOnKeyboardActionListener(ime);
    }

    @Override // com.nuance.swype.input.InputView
    public void destroy() {
        super.destroy();
        this.mWriteKorean.removeRecognizeListener(this);
        if (this.mFullScreenHandWriting) {
            hideFullScreenHandWritingView();
        }
        removeAllPendingMsg();
        this.mCurrentWritingPad = null;
        this.mKeyboardSwitcher = null;
    }

    @Override // com.nuance.swype.input.InputView
    public void clearCurrentActiveWord() {
        InputConnection ic = getCurrentInputConnection();
        if (ic != null) {
            ic.beginBatchEdit();
            ic.setComposingText("", 0);
            cancelCurrentDefaultWord();
            ic.endBatchEdit();
        }
    }

    @Override // com.nuance.swype.input.InputView
    public void updateSelection(int oldSelStart, int oldSelEnd, int newSelStart, int newSelEnd, int[] candidatesIndices) {
        log.d("updateSelection()", " called  koreanhandwriting");
        super.updateSelection(oldSelStart, oldSelEnd, newSelStart, newSelEnd, candidatesIndices);
        if (this.mComposition != null && this.mComposition.length() > 0 && (newSelStart != candidatesIndices[1] || newSelEnd != candidatesIndices[1])) {
            acceptCurrentDefaultWord();
            updateCandidatesList(null);
        }
        if (this.mComposition != null) {
            this.mComposition.setSelection(newSelStart, newSelEnd);
        }
        if (newSelStart != newSelEnd && this.isContextCandidates) {
            updateCandidatesList(null);
            return;
        }
        if (newSelStart != newSelEnd || this.mWritingOrRecognizing) {
            return;
        }
        if (this.mRecognitionCandidates == null || this.mRecognitionCandidates.isEmpty() || this.isContextCandidates) {
            List<CharSequence> list = getContextCandidates(null);
            if (list != null) {
                this.candidatesListViewCJK.setSuggestions(list, -1);
            }
            syncCandidateDisplayStyleToMode();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    @SuppressLint({"PrivateResource"})
    public void showHowToUseToast() {
        AppPreferences sm = AppPreferences.from(getContext());
        if (this.mFullScreenHandWriting) {
            if (sm.getBoolean("show_how_to_toggle_half_screen_mode_en", true)) {
                sm.setBoolean("show_how_to_toggle_half_screen_mode_en", false);
                QuickToast.show(getContext(), getResources().getString(R.string.how_to_toggle_half_screen_mode), 1, (getHeight() + this.mCurrentWritingPad.getHeight()) / 2);
                return;
            }
            return;
        }
        if (sm.getBoolean("show_how_to_toggle_full_screen_mode_en", true)) {
            sm.setBoolean("show_how_to_toggle_full_screen_mode_en", false);
            QuickToast.show(getContext(), getResources().getString(R.string.how_to_toggle_full_screen_mode), 1, (getHeight() + this.mCurrentWritingPad.getHeight()) / 2);
        }
    }

    private void postHowToUseToastMsg() {
        this.mPopupViewHandler.sendEmptyMessageDelayed(503, 10L);
    }

    private void hideHowToUseToast() {
        QuickToast.hide();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.InputView
    public void removeAllMessages() {
        this.mPopupViewHandler.removeMessages(503);
        for (int msg = 1; msg <= 19; msg++) {
            this.mPopupViewHandler.removeMessages(msg);
        }
        hideHowToUseToast();
    }

    private void setKeyboardForTextEntry(InputFieldInfo inputFieldInfo) {
        this.mKeyboardSwitcher.createKeyboardForTextInput(inputFieldInfo, this.mCurrentInputLanguage.getCurrentInputMode(), !this.mCurrentInputLanguage.getCurrentInputMode().isHandwriting());
        this.mWriteKorean.clearCategory();
        if (inputFieldInfo.isNumericModeField()) {
            this.mWriteKorean.addDigitsAndSymbolsOnlyCategory();
            this.mWriteKorean.addPunctuationCategory();
            this.mWriteKorean.addGestureCategory();
            this.mCurrentWritingMode = 1;
            return;
        }
        this.mIme.getInputMethods().addTextCategory(this.mWriteKorean, this.mCurrentInputLanguage);
        this.mWriteKorean.addGestureCategory();
        this.mCurrentWritingMode = 0;
    }

    @Override // com.nuance.swype.input.InputView
    public void flushCurrentActiveWord() {
        if (this.mComposition != null && this.candidatesListViewCJK != null && this.mWriteKorean != null && this.mComposition.length() > 0) {
            this.mComposition.acceptCurrentInline();
            this.mRecognitionCandidates = null;
            this.mWriteKorean.noteSelectedCandidate(0);
            endArcsAddingSequence();
        }
        if (this.mComposition != null) {
            this.mComposition.clearCurrentInline();
        }
        if (this.mRecognitionCandidates != null) {
            this.mRecognitionCandidates.clear();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void acceptCurrentActiveWord() {
        log.d("acceptCurrentActiveWord()", " called ");
        if (this.mComposition != null && this.candidatesListViewCJK != null && this.mWriteKorean != null && this.mComposition.length() > 0) {
            this.mComposition.acceptCurrentInline();
            this.mRecognitionCandidates = null;
            this.mWriteKorean.noteSelectedCandidate(0);
            endArcsAddingSequence();
        }
        updateCandidatesList(this.mRecognitionCandidates);
    }

    @Override // com.nuance.swype.input.InputView
    public void onText(CharSequence text, long eventTime) {
        log.d("onText()", " called ");
        if (isPendingRecognizeMessage()) {
            processPendingRecognizing();
        }
        this.mWriteKorean.queueText(text);
    }

    @Override // com.nuance.swype.input.InputView
    public KeyboardEx.KeyboardLayerType getKeyboardLayer() {
        if (this.mKeyboardSwitcher.isNumMode()) {
            return KeyboardEx.KeyboardLayerType.KEYBOARD_NUM;
        }
        if (this.mKeyboardSwitcher.isEditMode()) {
            return KeyboardEx.KeyboardLayerType.KEYBOARD_EDIT;
        }
        if (this.mCurrentWritingMode == 0) {
            return KeyboardEx.KeyboardLayerType.KEYBOARD_TEXT;
        }
        if (this.mCurrentWritingMode == 1) {
            return KeyboardEx.KeyboardLayerType.KEYBOARD_SYMBOLS;
        }
        return KeyboardEx.KeyboardLayerType.KEYBOARD_INVALID;
    }

    @Override // com.nuance.swype.input.InputView
    public void setKeyboardLayer(KeyboardEx.KeyboardLayerType keyboardLayer) {
        dismissPopupKeyboard();
        acceptCurrentActiveWord();
        KeyboardEx.KeyboardLayerType curLayer = getKeyboardLayer();
        if (keyboardLayer == KeyboardEx.KeyboardLayerType.KEYBOARD_NUM || keyboardLayer == KeyboardEx.KeyboardLayerType.KEYBOARD_EDIT) {
            if (curLayer != keyboardLayer) {
                this.mKeyboardSwitcher.createKeyboardForTextInput(keyboardLayer, this.mInputFieldInfo, this.mCurrentInputLanguage.getCurrentInputMode());
                hideFullScreenHandWritingView();
                this.mContainer.hideHWFrameAndCharacterList();
            }
        } else if (curLayer == KeyboardEx.KeyboardLayerType.KEYBOARD_NUM || curLayer == KeyboardEx.KeyboardLayerType.KEYBOARD_EDIT) {
            this.mKeyboardSwitcher.createKeyboardForTextInput(this.mInputFieldInfo, this.mCurrentInputLanguage.getCurrentInputMode(), !this.mCurrentInputLanguage.getCurrentInputMode().isHandwriting());
            showLastSavedHandWritingScreen();
        }
        boolean changed = false;
        if (keyboardLayer == KeyboardEx.KeyboardLayerType.KEYBOARD_TEXT && this.mCurrentWritingMode != 0) {
            this.mCurrentWritingMode = 0;
            changed = true;
        } else if (keyboardLayer == KeyboardEx.KeyboardLayerType.KEYBOARD_SYMBOLS && this.mCurrentWritingMode != 1) {
            this.mCurrentWritingMode = 1;
            changed = true;
        }
        if (changed && !ActivityManagerCompat.isUserAMonkey()) {
            updateWritingSettings();
            toggleNextWritingMode(this.mCurrentWritingMode);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.InputView
    public boolean handleBackspace(int repeatedCount) {
        this.candidatesListViewCJK.hideTooltip();
        if (isPendingRecognizeMessage()) {
            processPendingRecognizing();
        }
        this.mWriteKorean.queueChar('\b');
        return true;
    }

    @Override // com.nuance.swype.input.InputView
    public void handleCharKey(Point point, int primaryCode, int[] keyCodes, long eventTime) {
        if (isPendingRecognizeMessage()) {
            processPendingRecognizing();
        }
        this.mWriteKorean.queueChar((char) primaryCode);
    }

    @Override // com.nuance.swype.input.InputView
    public boolean handleKey(int primaryCode, boolean quickPressed, int repeatedCount) {
        hideHowToUseToast();
        switch (primaryCode) {
            case KeyboardEx.KEYCODE_KEYBOARD_RESIZE /* 2900 */:
                super.handleKey(primaryCode, quickPressed, repeatedCount);
                clearArcs();
                return true;
            case KeyboardEx.KEYCODE_SPEECH /* 6463 */:
                flushCurrentActiveWord();
                if (quickPressed) {
                    return true;
                }
                super.startSpeech();
                return true;
            case KeyboardEx.KEYCODE_MODE_BACK /* 43576 */:
                this.mKeyboardSwitcher.toggleLastKeyboard();
                return true;
            default:
                return super.handleKey(primaryCode, quickPressed, repeatedCount);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.InputView
    public boolean handleSpace(boolean quickPressed, int repeatedCount) {
        InputConnection ic;
        this.candidatesListViewCJK.hideTooltip();
        if (isPendingRecognizeMessage()) {
            processPendingRecognizing();
            this.mWriteKorean.queueChar(' ');
        } else if (hasRecognitionCandidates()) {
            this.mWriteKorean.queueChar(' ');
        } else {
            acceptCurrentDefaultWord();
            boolean addSpace = true;
            if (quickPressed && repeatedCount < 2 && this.mAutoPunctuationOn && (ic = getCurrentInputConnection()) != null) {
                ic.beginBatchEdit();
                CharSequence cSeqBefore = ic.getTextBeforeCursor(2, 0);
                CharacterUtilities charUtil = IMEApplication.from(this.mIme).getCharacterUtilities();
                if (cSeqBefore != null && cSeqBefore.length() == 2 && CharacterUtilities.isWhiteSpace(cSeqBefore.charAt(1)) && !CharacterUtilities.isWhiteSpace(cSeqBefore.charAt(0)) && !charUtil.isPunctuationOrSymbol(cSeqBefore.charAt(0))) {
                    handleAutoPunct();
                    addSpace = false;
                }
                ic.endBatchEdit();
            }
            if (addSpace) {
                sendSpace();
            }
        }
        return true;
    }

    private void handleAutoPunct() {
        InputConnection ic = getCurrentInputConnection();
        if (ic != null) {
            ic.deleteSurroundingText(1, 0);
            ic.commitText(".", 1);
            sendSpace();
        }
    }

    private boolean hasRecognitionCandidates() {
        return (this.mRecognitionCandidates == null || this.mRecognitionCandidates.isEmpty()) ? false : true;
    }

    private CharSequence removeOneCharacterFromCandidateList() {
        if (!hasRecognitionCandidates()) {
            return null;
        }
        CharSequence seq = getPureCandidate(this.mRecognitionCandidates.get(0));
        return seq.subSequence(0, seq.length() - 1);
    }

    private void handleSpace() {
        acceptCurrentDefaultWord();
        sendSpace();
    }

    private void deleteOneCharacter() {
        if (hasRecognitionCandidates() && !this.isContextCandidates) {
            endArcsAddingSequence();
            CharSequence seq = removeOneCharacterFromCandidateList();
            if (seq == null || seq.length() == 0) {
                cancelCurrentDefaultWord();
                return;
            }
            this.mRecognitionCandidates.clear();
            this.mRecognitionCandidates.add(0, seq);
            updateCandidatesList(this.mRecognitionCandidates);
            return;
        }
        this.mIme.sendBackspace(0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleChar(char ch) {
        if (ch == '\r') {
            ch = '\n';
        }
        switch (ch) {
            case '\b':
                deleteOneCharacter();
                return;
            case ' ':
                handleSpace();
                return;
            default:
                acceptCurrentDefaultWord();
                sendKeyChar(ch);
                return;
        }
    }

    private boolean isPendingRecognizeMessage() {
        return this.mDelayRecognizeHandler.hasMessages(1);
    }

    private void processPendingRecognizing() {
        this.mDelayRecognizeHandler.removeMessages(1);
        performDelayRecognition();
    }

    private void cancelCurrentDefaultWord() {
        if (this.mComposition != null && this.candidatesListViewCJK != null && this.mWriteKorean != null && this.mComposition.length() > 0) {
            this.mComposition.clearCurrentInline();
            CJKCandidatesListView cJKCandidatesListView = this.candidatesListViewCJK;
            this.mRecognitionCandidates = null;
            cJKCandidatesListView.setSuggestions(null, 0);
            syncCandidateDisplayStyleToMode();
            this.mWriteKorean.noteSelectedCandidate(-1);
            endArcsAddingSequence();
        }
    }

    private void acceptCurrentDefaultWord() {
        if (this.mComposition != null && this.candidatesListViewCJK != null && this.mWriteKorean != null && this.mComposition.length() > 0) {
            log.d("acceptCurrentDefaultWord()", " called ");
            this.mComposition.acceptCurrentInline();
            CJKCandidatesListView cJKCandidatesListView = this.candidatesListViewCJK;
            this.mRecognitionCandidates = null;
            cJKCandidatesListView.setSuggestions(null, 0);
            syncCandidateDisplayStyleToMode();
            this.mWriteKorean.noteSelectedCandidate(0);
            endArcsAddingSequence();
        }
    }

    private void startArcsAddingSequence() {
        log.d("startArcsAddingSequence()", " called  ");
        if (this.mWriteKorean != null && !ActivityManagerCompat.isUserAMonkey()) {
            this.mWriteKorean.startArcsAddingSequence();
        }
    }

    private void endArcsAddingSequence() {
        log.d("endArcsAddingSequence()", " called  ");
        this.mWriteKorean.endArcsAddingSequence();
    }

    @Override // com.nuance.swype.input.chinese.CJKCandidatesListView.OnWordSelectActionListener
    public void selectWord(int index, CharSequence word, View source) {
        log.d("selectWord()", " called >>>>>>>>>>>> ");
        boolean autospaceOn = this.mCurrentInputLanguage.getCurrentInputMode().isAutoSpaceEnabled();
        if (this.mInputFieldInfo.isPasswordField()) {
            autospaceOn = false;
        }
        String selected_emoji = word.toString();
        if (selected_emoji != null && EmojiLoader.isEmoji(selected_emoji)) {
            selected_emoji = this.candidatesListViewCJK.getEmojis().get(selected_emoji);
            if (selected_emoji != null && !selected_emoji.equals(word.toString())) {
                this.mComposition.insertText(selected_emoji);
            } else {
                this.mComposition.insertText(word);
            }
        } else {
            this.mComposition.insertText(word);
        }
        if (autospaceOn && source != null) {
            sendSpace();
            this.mWriteKorean.learnNewWord(word.toString());
        }
        this.mWriteKorean.noteSelectedCandidate(index);
        endArcsAddingSequence();
        List<CharSequence> list = getContextCandidates(word);
        if (list != null) {
            this.mRecognitionCandidates = list;
        } else {
            this.mRecognitionCandidates = null;
        }
        this.candidatesListViewCJK.setSuggestions(this.mRecognitionCandidates, -1);
        syncCandidateDisplayStyleToMode();
        if (selected_emoji != null) {
            super.addEmojiInRecentCat(selected_emoji, word.toString());
        }
    }

    @Override // com.nuance.swype.input.HandWritingView.OnWritingAction
    public void penDown(View src) {
        dismissPopupKeyboard();
        removeDelayRecognitionMsg();
        removeDelayRecognitionStroke();
        if (this.mCurrentWritingPad != null) {
            this.mWriteKorean.setWidth(this.mCurrentWritingPad.getWidth());
            this.mWriteKorean.setHeight(this.mCurrentWritingPad.getHeight());
            this.mCurrentWritingPad.mFaddingStrokeQueue.startActionFadingPenDown();
        }
        if (this.mWriteKorean != null && !ActivityManagerCompat.isUserAMonkey()) {
            this.mWriteKorean.applyChangedSettings();
        }
        if (!this.mWritingOrRecognizing) {
            acceptCurrentDefaultWord();
        }
        startArcsAddingSequence();
        this.mWritingOrRecognizing = true;
    }

    @Override // com.nuance.swype.input.HandWritingView.OnWritingAction
    public void penUp(Stroke.Arc[] arcs, View Src) {
        this.mCachedPoints.addAll(new ArrayList(arcs[0].mPoints));
        this.mCachedPoints.add(new Point(0, 0));
        if (this.mWriteKorean != null) {
            if (isPendingRecognizeStrokeMessage()) {
                removeDelayRecognitionStroke();
                this.mDelayRecognizeHandler.sendEmptyMessageDelayed(2, 100L);
            } else {
                this.mDelayRecognizeHandler.sendEmptyMessageDelayed(2, 100L);
            }
        }
        if (this.mWriteKorean != null && this.mBuildInfo != null) {
            if (isPendingRecognizeMessage()) {
                log.d("penUp()", " called  ");
                removeDelayRecognitionMsg();
                this.mDelayRecognizeHandler.sendEmptyMessageDelayed(1, this.mWriteKorean.getRecognizerDelay());
                return;
            }
            this.mDelayRecognizeHandler.sendEmptyMessageDelayed(1, this.mWriteKorean.getRecognizerDelay());
        }
    }

    private void removeDelayRecognitionMsg() {
        this.mDelayRecognizeHandler.removeMessages(1);
    }

    @Override // com.nuance.swype.input.InputView
    public CharSequence getCurrentExactWord() {
        log.d("getCurrentExactWord()", " called  ");
        if (this.mComposition == null || this.candidatesListViewCJK == null || this.mRecognitionCandidates == null || this.mRecognitionCandidates.size() <= 0 || IME.getLastShownCandidatesSource().toString().equals(Candidates.Source.NEXT_WORD_PREDICTION.toString())) {
            return null;
        }
        return getPureCandidate(this.mRecognitionCandidates.get(0));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateCandidatesList(List<CharSequence> candidates) {
        log.d("updateCandidatesList()", " called  :: candidates : " + candidates);
        if (!this.mStarted) {
            this.mRecognitionCandidates = null;
            this.isContextCandidates = false;
            return;
        }
        if (this.mRecognitionCandidates == null) {
            this.isContextCandidates = false;
        }
        this.mRecognitionCandidates = candidates;
        this.pendingCandidateSource = Candidates.Source.INVALID;
        if (this.mRecognitionCandidates != null && this.mRecognitionCandidates.size() > 0) {
            this.candidatesListViewCJK.setSuggestions(this.mRecognitionCandidates, 0);
            this.mComposition.showInline(getPureCandidate(this.mRecognitionCandidates.get(0)));
            syncCandidateDisplayStyleToMode();
        } else {
            CJKCandidatesListView cJKCandidatesListView = this.candidatesListViewCJK;
            this.mRecognitionCandidates = null;
            cJKCandidatesListView.setSuggestions(null, 0);
            syncCandidateDisplayStyleToMode();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleText(CharSequence text) {
        acceptCurrentDefaultWord();
        this.mComposition.insertText(text);
    }

    private void removeDelayRecognitionStroke() {
        this.mDelayRecognizeHandler.removeMessages(2);
    }

    private boolean isPendingRecognizeStrokeMessage() {
        return this.mDelayRecognizeHandler.hasMessages(2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void performDelayRecognitionStroke() {
        log.d("performDelayRecognitionStroke()", " called  :: ");
        int start = 0;
        for (int i = 0; i < this.mCachedPoints.size(); i++) {
            if (this.mCachedPoints.get(i).x == 0 && this.mCachedPoints.get(i).y == 0) {
                int end = i - 1;
                if (this.mWriteKorean != null && start >= 0 && start < end) {
                    this.mWriteKorean.queueAddArcs(this.mCachedPoints.subList(start, end), null, null);
                }
                start = i + 1;
            }
        }
        if (this.mWriteKorean != null) {
            this.mWriteKorean.queueRecognition(null);
        }
        this.mCachedPoints.clear();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void performDelayRecognition() {
        log.d("performDelayRecognition()", " called  :: ");
        if (this.mWriteKorean != null) {
            endArcsAddingSequence();
        }
        this.mCachedPoints.clear();
        this.mWritingOrRecognizing = false;
        if (this.mCurrentWritingPad != null) {
            this.mCurrentWritingPad.mFaddingStrokeQueue.clearAll();
        }
    }

    private void removeAllPendingMsg() {
        this.mHwrHandler.removeMessages(2);
        this.mHwrHandler.removeMessages(4);
        this.mHwrHandler.removeMessages(3);
        this.mHwrHandler.removeMessages(5);
        removeDelayRecognitionMsg();
    }

    @Override // com.nuance.input.swypecorelib.T9WriteRecognizerListener.OnWriteRecognizerListener
    public void onHandleWriteEvent(T9WriteRecognizerListener.WriteEvent event) {
        if (this.mIme == null || isValidBuild()) {
            switch (event.mType) {
                case 1:
                    this.mHwrHandler.sendMessageDelayed(this.mHwrHandler.obtainMessage(2, event), 5L);
                    return;
                case 2:
                    this.mHwrHandler.sendMessageDelayed(this.mHwrHandler.obtainMessage(3, event), 5L);
                    return;
                case 3:
                    this.mHwrHandler.sendMessageDelayed(this.mHwrHandler.obtainMessage(4, event), 5L);
                    return;
                default:
                    return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showStartOfWordCandidateList() {
        String lastWord = IME.getLastSavedActiveWordAndSet();
        log.d("showStartOfWordCandidateList()", " called  :: lastWord : " + lastWord);
        if (lastWord != null && lastWord.length() > 0) {
            this.mComposition.showInline(lastWord);
            this.mComposition.acceptCurrentInline();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.InputView
    public void changeKeyboardMode() {
        boolean changed = false;
        if (this.mKeyboardSwitcher.isNumMode()) {
            if (this.mInputFieldInfo != null) {
                String inputMode = this.mKeyboardSwitcher.getCurrentInputMode();
                this.mKeyboardSwitcher.createKeyboardForTextInput(KeyboardEx.KeyboardLayerType.KEYBOARD_TEXT, this.mInputFieldInfo, this.mCurrentInputLanguage.getInputMode(inputMode));
            }
            showLastSavedHandWritingScreen();
            return;
        }
        if (this.mCurrentWritingMode == 0) {
            this.mCurrentWritingMode = 1;
            changed = true;
        } else if (this.mCurrentWritingMode == 1) {
            this.mCurrentWritingMode = 0;
            changed = true;
        }
        if (changed && !ActivityManagerCompat.isUserAMonkey()) {
            if (isPendingRecognizeMessage()) {
                processPendingRecognizing();
            } else {
                acceptCurrentDefaultWord();
            }
            updateWritingSettings();
        }
        toggleNextWritingMode(this.mCurrentWritingMode);
    }

    private void updateWritingSettings() {
        this.mWriteKorean.clearCategory();
        if (this.mCurrentWritingMode == 1) {
            this.mWriteKorean.addDigitsAndSymbolsOnlyCategory();
            this.mWriteKorean.addPunctuationCategory();
            this.mWriteKorean.addGestureCategory();
        } else if (this.mCurrentWritingMode == 0) {
            this.mIme.getInputMethods().addTextCategory(this.mWriteKorean, this.mCurrentInputLanguage);
            this.mWriteKorean.addGestureCategory();
        }
        this.mWriteKorean.applyChangedSettings();
    }

    @SuppressLint({"PrivateResource"})
    private void toggleNextWritingMode(int mode) {
        List<KeyboardEx.Key> keys = getKeyboard().getKeys();
        if (keys != null) {
            KeyboardEx.Key altKey = findKeyByKeyCode(keys, KeyboardEx.KEYCODE_MODE_CHANGE);
            if (altKey != null) {
                if (mode == 0) {
                    altKey.label = this.mIme.getResources().getText(R.string.label_symbol_key);
                } else if (mode == 1) {
                    altKey.label = this.mIme.getResources().getText(R.string.label_korean_key);
                }
                invalidateKey(altKey);
            } else {
                log.e("toggleNextWritingMode() altKey not found!");
            }
        } else {
            log.e("toggleNextWritingMode() no modifier keys found!");
        }
        if (this.mCurrentWritingPad != null) {
            this.mCurrentWritingPad.invalidate();
        }
    }

    public void setHandWritingView(HandWritingView view) {
        if (view instanceof KoreanHandWritingView) {
            this.mCurrentWritingPad = (KoreanHandWritingView) view;
            this.mCurrentWritingPad.setFullScreen(this.mFullScreenHandWriting);
        }
    }

    public int getHandWritingMode() {
        return this.mCurrentWritingMode;
    }

    @Override // com.nuance.swype.input.InputView
    public void onApplicationUnbind() {
        super.onApplicationUnbind();
        removeAllMessages();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.InputView
    public void resumeSpeech() {
        if (this.mContainer != null) {
            super.resumeSpeech();
        }
    }

    public void acceptInlineAndClearCandidates() {
        acceptCurrentDefaultWord();
        clearSuggestions();
    }

    @Override // com.nuance.swype.input.InputView
    public void clearSuggestions() {
        if (this.candidatesListViewCJK != null) {
            this.candidatesListViewCJK.clear();
        }
        syncCandidateDisplayStyleToMode();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.InputView, com.nuance.swype.input.KeyboardViewEx
    public boolean handleScrollDown() {
        if (this.mIme != null) {
            if (!this.mFullScreenHandWriting) {
                this.mIme.requestHideSelf(0);
                return true;
            }
            if (IMEApplication.from(this.mIme).getIMEHandlerManager() != null) {
                this.mIme.handlerManager.toggleFullScreenHW();
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.KeyboardViewEx
    public boolean handleScrollUp() {
        if (this.mFullScreenHandWriting || IMEApplication.from(this.mIme).getIMEHandlerManager() == null) {
            return false;
        }
        this.mIme.handlerManager.toggleFullScreenHW();
        return true;
    }

    @Override // com.nuance.swype.input.InputView
    public void onSpeechViewShowedUp() {
        if (this.mFullScreenHandWriting) {
            this.mContainer.hideFullScreenHandWritingFrame();
        }
    }

    @Override // com.nuance.swype.input.InputView
    public void handleEmotionKey() {
        if (this.mFullScreenHandWriting) {
            setKeyboardLayer(KeyboardEx.KeyboardLayerType.KEYBOARD_EDIT);
        }
        if (this.mRecognitionCandidates != null) {
            this.mRecognitionCandidates.clear();
        }
        super.handleEmotionKey();
    }

    @Override // com.nuance.swype.input.InputView
    public void onSpeechViewDismissed() {
        if (!this.mKeyboardSwitcher.isNumMode()) {
            showLastSavedHandWritingScreen();
        }
    }

    @Override // com.nuance.swype.input.InputView
    public boolean isLanguageSwitchableOnSpace() {
        return true;
    }

    @Override // com.nuance.swype.input.InputView
    @SuppressLint({"InflateParams", "PrivateResource"})
    public View createCandidatesView(CandidatesListView.CandidateListener onSelectListener) {
        if (this.wordListViewContainerCJK == null) {
            IMEApplication app = IMEApplication.from(this.mIme);
            LayoutInflater inflater = app.getThemedLayoutInflater(this.mIme.getLayoutInflater());
            app.getThemeLoader().setLayoutInflaterFactory(inflater);
            this.wordListViewContainerCJK = (CJKWordListViewContainer) inflater.inflate(R.layout.korean_handwriting_candidates, (ViewGroup) null);
            app.getThemeLoader().applyTheme(this.wordListViewContainerCJK);
            this.wordListViewContainerCJK.initViews();
            this.candidatesListViewCJK = this.wordListViewContainerCJK.getCJKCandidatesListView();
        }
        this.candidatesListViewCJK.setOnWordSelectActionListener(this);
        return this.wordListViewContainerCJK;
    }

    @Override // com.nuance.swype.input.InputView
    public View getWordCandidateListContainer() {
        return this.wordListViewContainerCJK;
    }

    @Override // com.nuance.swype.input.InputView
    @SuppressLint({"PrivateResource"})
    public void startInput(InputFieldInfo inputFieldInfo, boolean restarting) {
        this.mStarted = true;
        dismissPopupKeyboard();
        removeDelayShowingFullScreenMsg();
        hideFullScreenHandWritingView();
        super.startInput(inputFieldInfo, restarting);
        if (this.candidatesListViewCJK != null) {
            this.candidatesListViewCJK.updateCandidatesSize();
        }
        if (this.mContainer != null) {
            this.mContainer.updateHandwritingPadSize();
        }
        this.mWriteKorean.addRecognizeListener(this);
        cancelCurrentDefaultWord();
        endArcsAddingSequence();
        syncCandidateDisplayStyleToMode();
        setKeyboardForTextEntry(inputFieldInfo);
        this.mComposition.setInputConnection(getCurrentInputConnection());
        this.mWriteKorean.startSession(this.mCurrentInputLanguage.getCoreLanguageId());
        Resources res = getContext().getResources();
        int delay = InputPrefs.getAutoDelay(UserPreferences.from(getContext()), UserPreferences.HWR_AUTO_ACCEPT_RECOGNIZE, res.getInteger(R.integer.handwriting_auto_recognize_default_ms));
        this.mWriteKorean.setRecognizerDelay(delay);
        if (UserPreferences.from(getContext()).isHwrScrmodeEnabled()) {
            this.mWriteKorean.setRecognitionMode(0);
        } else {
            this.mWriteKorean.setRecognitionMode(1);
            if (getContext().getResources().getConfiguration().orientation == 2) {
                this.mWriteKorean.setWritingDirection(0);
            } else {
                this.mWriteKorean.setWritingDirection(3);
            }
        }
        this.mWriteKorean.applyChangedSettings();
        if (UserPreferences.from(getContext()).isHwrRotationEnabled()) {
            this.mWriteKorean.updateRotationSetting(true);
        } else {
            this.mWriteKorean.updateRotationSetting(false);
        }
        this.mWriteKorean.updateAttachingCommonWordsLDB(false);
        if (this.mKoreanInput != null) {
            this.mKoreanInput.startSession();
            this.mCurrentInputLanguage.setLanguage(this.mKoreanInput);
        }
        clearArcs();
        int selectionHeight = getResources().getDimensionPixelSize(R.dimen.candidates_list_height);
        this.mKeyboardHeight = getResources().getDimensionPixelSize(R.dimen.key_height) + selectionHeight;
        if (inputFieldInfo.getEditorInfo().inputType == 0) {
            removeDelayShowingFullScreenMsg();
        }
        this.mKeyboardSwitcher.setShiftState(Shift.ShiftState.OFF);
        showLastSavedHandWritingScreen();
        if (getLastFullScreenState(this.mCurrentInputLanguage.getCoreLanguageId())) {
            updateCandidatesList(null);
        }
        toggleNextWritingMode(this.mCurrentWritingMode);
        removeAllMessages();
        if (!this.mInputFieldInfo.isPasswordField()) {
            postDelayShowingStartOfWordCandidate();
        }
        postHowToUseToastMsg();
        postDelayResumeSpeech();
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

    @Override // com.nuance.swype.input.InputView
    public void handleClose() {
        super.handleClose();
        this.mStarted = false;
        dismissPopupKeyboard();
        hideFullScreenHandWritingView();
        acceptCurrentActiveWord();
        if (this.mCurrentWritingPad != null) {
            this.mCurrentWritingPad.setNewSession(true);
        }
    }

    @Override // com.nuance.swype.input.InputView
    public void finishInput() {
        super.finishInput();
        this.mStarted = false;
        if (this.mCurrentWritingPad != null) {
            this.mCurrentWritingPad.setNewSession(true);
        }
        this.isOnceAction = false;
        this.mWritingOrRecognizing = false;
        dismissPopupKeyboard();
        hideFullScreenHandWritingView();
        removeAllMessages();
        hideHowToUseToast();
        acceptCurrentDefaultWord();
        clearSuggestions();
        endArcsAddingSequence();
        this.mWriteKorean.removeRecognizeListener(this);
        this.mWriteKorean.finishSession();
        if (this.mKoreanInput != null) {
            this.mKoreanInput.finishSession();
        }
        log.d("finishInput()", " called ");
        this.keyboardTouchEventDispatcher.unwrapTouchEvent(this);
        this.xt9coreinput.setInputContextRequestListener(null);
    }

    public void setContainerView(KoreanHandWritingContainerView container) {
        this.mContainer = container;
    }

    @Override // com.nuance.swype.input.InputView, com.nuance.swype.input.KeyboardViewEx
    public boolean isFullScreenHandWritingView() {
        return this.mFullScreenHandWriting;
    }

    @Override // com.nuance.swype.input.InputView
    public boolean isHandWritingInputView() {
        return true;
    }

    private void setFullScreenHandWritingFrame() {
        this.mFullScreenHandWriting = true;
        this.mContainer.setFullScreenHandWritingFrame();
    }

    @Override // com.nuance.swype.input.InputView
    public void handlePossibleActionAfterResize() {
        if (this.mIme != null) {
            InputContainerView containerView = this.mIme.getInputContainerView();
            if (containerView != null && (isMiniKeyboardMode() || isDockMiniKeyboardMode())) {
                containerView.requestLayout();
                this.isOnceAction = true;
            }
            if (this.mFullScreenHandWriting) {
                if (containerView != null && isMiniKeyboardMode()) {
                    containerView.setAllowedMovement(true, false);
                }
                this.mContainer.hideFullScreenHandWritingFrame();
                this.mContainer.setFullScreenHandWritingFrame();
                showFullScreenHandWritingView();
            }
        }
    }

    @Override // com.nuance.swype.input.InputView
    public void handlePossibleActionAfterUniversalKeyboardResize(boolean hideSymbolView) {
        this.mContainer.hideFullScreenHandWritingFrame();
        if (this.mFullScreenHandWriting && isNormalTextInputMode()) {
            this.mContainer.setFullScreenHandWritingFrame();
            showFullScreenHandWritingView();
        }
    }

    private void postDelayShowingStartOfWordCandidate() {
        this.mHwrHandler.sendEmptyMessageDelayed(5, 10L);
    }

    @SuppressLint({"PrivateResource"})
    private void changeAltIconOfSwitchingLayout(boolean aFullScreen) {
        if (getKeyboard() != null) {
            List<KeyboardEx.Key> keys = getKeyboard().getKeys();
            for (int i = 0; i < keys.size(); i++) {
                KeyboardEx.Key key = keys.get(i);
                if (key.altCode == 43579) {
                    if (aFullScreen) {
                        if (key.altIcon != null) {
                            IMEApplication app = IMEApplication.from(getContext());
                            key.altIcon = app.getThemedDrawable(R.attr.cjkAltIconMinHwCommaKey);
                            key.altPreviewIcon = app.getThemedDrawable(R.attr.cjkAltPreMinHwCommaKey);
                        }
                    } else if (key.altIcon != null) {
                        IMEApplication app2 = IMEApplication.from(getContext());
                        key.altIcon = app2.getThemedDrawable(R.attr.cjkAltIconMaxHwCommaKey);
                        key.altPreviewIcon = app2.getThemedDrawable(R.attr.cjkAltPreMaxHwCommaKey);
                    }
                    invalidateKey(key);
                }
            }
        }
    }

    private void setNormalHandScreenWritingFrame() {
        this.mFullScreenHandWriting = false;
        this.mContainer.hideFullScreenHandWritingFrame();
        this.mContainer.setNormalHandScreenWritingFrame();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.KeyboardViewEx
    @SuppressLint({"PrivateResource"})
    public boolean showSymbolSelectPopup(KeyboardEx.Key popupKey) {
        if (popupKey == null) {
            return false;
        }
        if (isFullScreenHandWritingView() && popupKey.label != null && popupKey.popupResId == R.xml.popup_smileys_with_return && popupKey.label.equals(":-)")) {
            return showStaticSelectPopup(popupKey);
        }
        return super.showSymbolSelectPopup(popupKey);
    }

    public void toggleHandWritingFrame() {
        InputContainerView containerView;
        dismissPopupKeyboard();
        if (!this.mFullScreenHandWriting) {
            this.mFullScreenHandWriting = true;
            handlePossibleActionAfterResize();
        } else {
            if (this.mIme != null && (containerView = this.mIme.getInputContainerView()) != null && isMiniKeyboardMode()) {
                containerView.setAllowedMovement(true, true);
            }
            setNormalHandScreenWritingFrame();
        }
        boolean fullscreenMode = isFullScreenHandWritingView();
        setLastFullScreenState(fullscreenMode);
        changeAltIconOfSwitchingLayout(fullscreenMode);
        postHowToUseToastMsg();
    }

    private void showHandWritingView() {
        if ((this.mSpeechWrapper == null || !this.mSpeechWrapper.isSpeechViewShowing()) && !this.mKeyboardSwitcher.isNumMode() && !this.mKeyboardSwitcher.isEditMode()) {
            changeAltIconOfSwitchingLayout(this.mFullScreenHandWriting);
            if (this.mFullScreenHandWriting) {
                showFullScreenHandWritingView();
            } else {
                this.mContainer.setNormalHandScreenWritingFrame();
            }
        }
    }

    private void showFullScreenHandWritingView() {
        int status_bar_height = IMEApplication.from(this.mIme).getIMEHandlerManager() != null ? this.mIme.handlerManager.statusBarHeight() : 0;
        DisplayMetrics display = IMEApplication.from(getContext()).getDisplay();
        int w = display.widthPixels;
        int fullHandWritingScreenYOffset = display.heightPixels - status_bar_height;
        int fullHandWritingScreenHeight = fullHandWritingScreenYOffset;
        log.d("showFullScreenHandWritingView...fullHandWritingScreenHeight: ", Integer.valueOf(fullHandWritingScreenHeight));
        if (IMEApplication.from(getContext()).isScreenLayoutTablet() && ((isMiniKeyboardMode() || isDockMiniKeyboardMode()) && this.mIme != null)) {
            InputContainerView containerView = this.mIme.getInputContainerView();
            if (containerView != null) {
                if (this.isOnceAction) {
                    postDelayShowingFullScreenMsg();
                    this.isOnceAction = false;
                    return;
                } else if (containerView.getRootViewHeight() <= 0) {
                    postDelayShowingFullScreenMsg();
                    return;
                } else {
                    fullHandWritingScreenHeight = fullHandWritingScreenYOffset - containerView.getRootViewHeight();
                    log.d("showFullScreenHandWritingView...root view height: ", Integer.valueOf(containerView.getRootViewHeight()));
                }
            }
            fullHandWritingScreenYOffset -= getKeyboard().getHeight();
        }
        changeAltIconOfSwitchingLayout(true);
        this.mContainer.showFullScreenHandWritingFrame(0, -fullHandWritingScreenYOffset, w, fullHandWritingScreenHeight);
    }

    private void hideFullScreenHandWritingView() {
        this.mFullScreenHandWriting = false;
        changeAltIconOfSwitchingLayout(false);
        this.mContainer.hideFullScreenHandWritingFrame();
        if (this.mStarted) {
            this.mContainer.setNormalHandScreenWritingFrame();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showDelayFullScreen() {
        if (getWindowToken() != null) {
            showHandWritingView();
        } else {
            postDelayShowingFullScreenMsg();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void postDelayShowingFullScreenMsg() {
        this.mDelayShowingFullScreenHandler.sendEmptyMessageDelayed(1, 50L);
    }

    private void removeDelayShowingFullScreenMsg() {
        this.mDelayShowingFullScreenHandler.removeMessages(1);
    }

    private void showLastSavedHandWritingScreen() {
        if (getLastFullScreenState(this.mCurrentInputLanguage.getCoreLanguageId())) {
            setFullScreenHandWritingFrame();
            changeAltIconOfSwitchingLayout(true);
            updateKeyboardDockMode();
            postDelayShowingFullScreenMsg();
            return;
        }
        changeAltIconOfSwitchingLayout(this.mFullScreenHandWriting);
        if (this.mFullScreenHandWriting) {
            setFullScreenHandWritingFrame();
            postDelayShowingFullScreenMsg();
        } else {
            setNormalHandScreenWritingFrame();
        }
    }

    private String getFullScreenSettingKey(int language) {
        return AppPreferences.CJK_FULL_SCREEN_ENABLED + language;
    }

    private void setLastFullScreenState(boolean fullscreen) {
        AppPreferences.from(getContext()).setBoolean(getFullScreenSettingKey(this.mCurrentInputLanguage.getCoreLanguageId()), fullscreen);
    }

    private boolean getLastFullScreenState(int language) {
        return AppPreferences.from(getContext()).getBoolean(getFullScreenSettingKey(language), false);
    }

    @Override // com.nuance.swype.input.chinese.CJKCandidatesListView.OnWordSelectActionListener
    public boolean isScrollable(CJKCandidatesListView aSource) {
        return false;
    }

    @Override // com.nuance.swype.input.chinese.CJKCandidatesListView.OnWordSelectActionListener
    public void nextBtnPressed(CJKCandidatesListView aSource) {
    }

    @Override // com.nuance.swype.input.chinese.CJKCandidatesListView.OnWordSelectActionListener
    public void prevBtnPressed(CJKCandidatesListView aSource) {
    }

    @Override // com.nuance.swype.input.chinese.CJKCandidatesListView.OnWordSelectActionListener
    public void onCandidateLongPressed(int index, String word, int wdSource, CJKCandidatesListView aSource) {
        log.d("onCandidateLongPressed()", " called ::  wdSource  :: " + wdSource + " , word :: " + word);
        if (word != null && EmojiLoader.isEmoji(word.toString())) {
            EmojiInfo emojiInfo = aSource.getEmojiInfoList().get(index);
            String selected_emoji = this.candidatesListViewCJK.getEmojis().get(word.toString());
            super.showSkinTonePopupForCJK(emojiInfo, selected_emoji, word.toString());
        }
    }

    @Override // com.nuance.swype.input.chinese.CJKCandidatesListView.OnWordSelectActionListener
    public boolean onPressReleaseCandidate(int index, String word, int wdSource) {
        log.d("onPressReleaseCandidate()", " called ::  wdSource  :: " + wdSource + " , word :: " + word);
        if (word == null) {
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

    @Override // com.nuance.swype.input.HandWritingView.OnWritingAction
    public void penUp(List<Point> arc, View src) {
    }

    @Override // com.nuance.swype.input.HandWritingView.InSelectionAreaListener
    @SuppressLint({"PrivateResource"})
    public boolean pointInSelectionArea(int x, int y) {
        int boundaryHandWritingScreenHeight;
        if (x <= 0 || y <= 0) {
            return false;
        }
        int status_bar_height = IMEApplication.from(this.mIme).getIMEHandlerManager() != null ? this.mIme.handlerManager.statusBarHeight() : 0;
        if (mWordListHeight == 0) {
            setWordListHeight(this.candidatesListViewCJK.getMeasuredHeight());
        }
        int keyboard_height = getResources().getDimensionPixelSize(R.dimen.key_height);
        int screen_height = getResources().getDisplayMetrics().heightPixels;
        if (this.wordListViewContainerCJK.isShown()) {
            boundaryHandWritingScreenHeight = ((screen_height - mWordListHeight) - keyboard_height) - status_bar_height;
        } else {
            boundaryHandWritingScreenHeight = (screen_height - keyboard_height) - status_bar_height;
        }
        return y >= boundaryHandWritingScreenHeight;
    }

    @Override // com.nuance.swype.input.HandWritingView.InSelectionAreaListener
    public void resetArea(int areaOption) {
        if (areaOption == -2) {
            this.mIme.requestHideSelf(0);
        } else {
            this.mSelectionAreaOption = areaOption;
        }
    }

    private void clearArcs() {
        if (this.mCurrentWritingPad != null) {
            this.mCurrentWritingPad.clearArcs();
        }
    }

    @Override // com.nuance.swype.input.HandWritingView.InSelectionAreaListener
    public boolean getCurrentScreenMode() {
        return false;
    }

    @Override // com.nuance.swype.input.HandWritingView.InSelectionAreaListener
    @SuppressLint({"PrivateResource"})
    public boolean transferKeyEvent(MotionEvent me) {
        if (mWordListHeight == 0) {
            setWordListHeight(this.wordListViewContainerCJK.getMeasuredHeight());
        }
        int keyboard_height = getResources().getDimensionPixelSize(R.dimen.key_height);
        int screen_height = getResources().getDisplayMetrics().heightPixels;
        int screen_width = getResources().getDisplayMetrics().widthPixels;
        int status_bar_height = 0;
        if (this.mIme != null) {
            status_bar_height = IMEApplication.from(this.mIme).getIMEHandlerManager() != null ? this.mIme.handlerManager.statusBarHeight() : 0;
        }
        long downTime = me.getDownTime();
        long eventTime = me.getEventTime();
        int actionType = me.getAction();
        float posX = me.getX();
        float posY = me.getY();
        int mState = me.getMetaState();
        float heightToBottom = (screen_height - posY) - status_bar_height;
        if ((heightToBottom <= keyboard_height && this.mSelectionAreaOption == -1) || this.mSelectionAreaOption == 0) {
            if (actionType == 0) {
                this.mSelectionAreaOption = 0;
            }
            MotionEvent motionEvent = MotionEvent.obtain(downTime, eventTime, actionType, posX, keyboard_height - heightToBottom, mState);
            dispatchTouchEvent(motionEvent);
            motionEvent.recycle();
            return true;
        }
        int boundarySize = ((screen_height - status_bar_height) - mWordListHeight) - keyboard_height;
        if ((posY > boundarySize && this.mSelectionAreaOption == -1) || this.mSelectionAreaOption == 1) {
            if (actionType == 0) {
                this.mSelectionAreaOption = 1;
            }
            float posY2 = posY - boundarySize;
            if (posX < this.candidatesListViewCJK.getLeftWidth()) {
                if (actionType == 0) {
                    MotionEvent motionEvent2 = MotionEvent.obtain(downTime, eventTime, actionType, posX, posY2, mState);
                    this.wordListViewContainerCJK.dispatchTouchEvent(motionEvent2);
                    motionEvent2.recycle();
                }
                if (actionType == 1 || actionType == 3) {
                    this.mSelectionAreaOption = -1;
                    this.mCurrentWritingPad.setPointStatus(-1);
                }
            } else if (posX <= screen_width || this.candidatesListViewCJK.getRightWidth() < screen_width) {
                MotionEvent motionEvent3 = MotionEvent.obtain(downTime, eventTime, actionType, posX - this.candidatesListViewCJK.getLeftWidth(), posY2, mState);
                this.candidatesListViewCJK.dispatchTouchEvent(motionEvent3);
                motionEvent3.recycle();
            } else {
                if (actionType == 0) {
                    MotionEvent motionEvent4 = MotionEvent.obtain(downTime, eventTime, actionType, posX, posY2, mState);
                    this.wordListViewContainerCJK.dispatchTouchEvent(motionEvent4);
                    motionEvent4.recycle();
                }
                if (actionType == 1 || actionType == 3) {
                    this.mSelectionAreaOption = -1;
                    this.mCurrentWritingPad.setPointStatus(-1);
                }
            }
            return true;
        }
        return false;
    }

    private static void setWordListHeight(int wordListHeight) {
        mWordListHeight = wordListHeight;
    }

    @Override // com.nuance.swype.input.HandWritingView.InSelectionAreaListener
    public boolean isSpeechPopupShowing() {
        return super.isSpeechViewShowing();
    }

    @Override // com.nuance.swype.input.InputView
    public void setNotMatchToolTipSuggestion() {
        syncCandidateDisplayStyleToMode();
        setCandidatesViewShown(true);
        this.candidatesListViewCJK.showNotMatchTootip();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.InputView
    public void setSwypeKeytoolTipSuggestion() {
        if (this.candidatesListViewCJK != null) {
            this.candidatesListViewCJK.showSwypeTooltip();
            syncCandidateDisplayStyleToMode();
            setCandidatesViewShown(true);
        }
    }

    @Override // com.nuance.swype.input.InputView, com.nuance.swype.input.KeyboardViewEx
    public void setKeyboard(KeyboardEx keyboard) {
        updateDockModeForHandwriting(keyboard);
        super.setKeyboard(keyboard);
    }

    public void updateKeyboardDockMode() {
        KeyboardEx keyboard = getKeyboard();
        if (keyboard != null) {
            setKeyboard(keyboard);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.KeyboardViewEx
    public MotionEvent getExtendedEvent(MotionEvent me) {
        KeyboardEx keyboard;
        return (!this.mFullScreenHandWriting || (keyboard = getKeyboard()) == null || keyboard.getKeyboardDockMode() == KeyboardEx.KeyboardDockMode.MOVABLE_MINI) ? super.getExtendedEvent(me) : me;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.InputView
    public void preUpdateSpeechText() {
        acceptCurrentActiveWord();
    }

    @Override // com.nuance.swype.input.chinese.CJKCandidatesListView.OnWordSelectActionListener
    public void onScrollContentChanged() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.InputView
    public Rect getSpeechPopupRectInWindowCoord() {
        if (this.mFullScreenHandWriting) {
            Rect rect = this.mIme.getSpeechPopupRectInWindowCoord();
            if (rect != null) {
                rect.top -= this.candidatesListViewCJK != null ? this.candidatesListViewCJK.getMeasuredHeight() : 0;
                return rect;
            }
            return rect;
        }
        return super.getSpeechPopupRectInWindowCoord();
    }

    private CharSequence getPureCandidate(CharSequence candidate) {
        String cand = candidate.toString();
        int index = cand.lastIndexOf(36);
        return index <= 0 ? candidate : cand.substring(0, index);
    }

    private List<CharSequence> getContextCandidates(CharSequence wordJustSelected) {
        if (this.mKoreanInput == null || this.mWriteKorean.getRecognitionMode() == 0) {
            return null;
        }
        log.d("getContextCandidates()", " called  :: wordJustSelected== " + ((Object) wordJustSelected));
        CharSequence seqBeforeCursor = getCurrentInputConnection().getTextBeforeCursor(17, 0);
        if (seqBeforeCursor == null) {
            return null;
        }
        this.mKoreanInput.clearAllKeys();
        this.mKoreanInput.setContext(null);
        this.mKoreanInput.setContext(seqBeforeCursor.toString().toCharArray());
        if (wordJustSelected != null) {
            this.mKoreanInput.noteWordDone(wordJustSelected.toString());
            this.pendingCandidateSource = Candidates.Source.NEXT_WORD_PREDICTION;
        }
        Candidates candidates = this.mKoreanInput.getCandidates(Candidates.Source.NEXT_WORD_PREDICTION);
        if (candidates == null || candidates.count() <= 0) {
            return null;
        }
        List<WordCandidate> words = candidates.getCandidates();
        List<CharSequence> list = new ArrayList<>();
        for (WordCandidate word : words) {
            list.add(word.toString());
        }
        this.isContextCandidates = true;
        return list;
    }

    @Override // com.nuance.swype.input.InputView
    public void showNextWordPrediction() {
        if (this.mNextWordPredictionOn) {
            log.d("showNextWordPrediction()", " called  ");
            String lastWord = IME.getLastSavedActiveWord();
            if (lastWord == null || lastWord.length() <= 0) {
                updateWordContext();
                updateSuggestions(Candidates.Source.NEXT_WORD_PREDICTION, true);
            }
        }
    }
}
