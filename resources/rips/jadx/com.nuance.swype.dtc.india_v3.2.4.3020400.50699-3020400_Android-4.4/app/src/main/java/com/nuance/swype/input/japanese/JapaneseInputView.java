package com.nuance.swype.input.japanese;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputConnection;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;
import com.nuance.android.compat.ActivityManagerCompat;
import com.nuance.android.util.WeakReferenceHandler;
import com.nuance.connect.common.Integers;
import com.nuance.input.swypecorelib.Candidates;
import com.nuance.input.swypecorelib.KeyType;
import com.nuance.input.swypecorelib.Shift;
import com.nuance.input.swypecorelib.XT9CoreInput;
import com.nuance.input.swypecorelib.XT9CoreJapaneseInput;
import com.nuance.swype.input.AppPreferences;
import com.nuance.swype.input.CandidatesListView;
import com.nuance.swype.input.IME;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.InputFieldInfo;
import com.nuance.swype.input.InputMethods;
import com.nuance.swype.input.InputView;
import com.nuance.swype.input.KeyboardEx;
import com.nuance.swype.input.KeyboardSwitcher;
import com.nuance.swype.input.KeyboardViewEx;
import com.nuance.swype.input.QuickToast;
import com.nuance.swype.input.R;
import com.nuance.swype.input.SpeechWrapper;
import com.nuance.swype.input.UserPreferences;
import com.nuance.swype.input.chinese.CJKCandidatesListView;
import com.nuance.swype.input.keyboard.InputContextRequestDispatcher;
import com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher;
import com.nuance.swype.plugin.TypedArrayWrapper;
import com.nuance.swype.util.CharacterUtilities;
import com.nuance.swype.util.InputConnectionUtils;
import com.nuance.swype.util.LogManager;
import com.nuance.swype.widget.PreviewView;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import jp.co.omronsoft.openwnn.JAJP.OpenWnnEngineJAJP;
import jp.co.omronsoft.openwnn.WnnWord;

/* loaded from: classes.dex */
public class JapaneseInputView extends InputView implements View.OnClickListener, CJKCandidatesListView.OnWordSelectActionListener {
    private static final int ALL_CONFIRM = -1;
    private static final int CONV_CANDIDATE_END = 0;
    private static final int CONV_CANDIDATE_SELECTED = 2;
    private static final int CONV_CANDIDATE_START = 1;
    private static final int FLICKER_BETWEEN_INTERVAL = 2;
    private static final int JP_KEYPAD_LONG_VOWEL_KEY = 12540;
    private static final int JP_KEYPAD_YA_KEY = 12420;
    private static final int KEYCODE_Z = 122;
    private static final double KEYPAD_FLICKER_RADIUS_PARAM = 2.0d;
    private static final double KEYPAD_ISOLATION_AREA_PARM = 1.8d;
    private static final int KEYPAD_KEYBOARD_TYPE = 2;
    private static final int LAST_INPUT_BACKSPACE = 4;
    private static final int LAST_INPUT_SPACE = 3;
    private static final int MAXDIVINFOLEN = 256;
    protected static final int MSG_DELAY_RESET_INLINE_FLAG = 21;
    protected static final int MSG_DELAY_SHOW_SEGMENTATION_POP = 23;
    protected static final int MSG_DELAY_UPDATE_SEGMENTATION_POP = 22;
    private static final int QWERTY_KEYBOARD_TYPE = 1;
    private static final int RC_PHRASE_CONVERSION = 1;
    private static final int RC_PHRASE_CONVERSION_AGAIN = 4;
    private static final int RC_PHRASE_REDUCTION = 2;
    private static final int RC_PHREAE_EXPANTION = 3;
    private static final int TRACE_GESTURE = -1;
    protected static final LogManager.Log log = LogManager.getLog("JapaneseInputView");
    Handler.Callback JpInputViewCallback;
    Handler JpInputViewHandler;
    private PreviewView[] arrayFlickerPreview;
    private View candidatesPopup;
    private CharacterUtilities charUtils;
    private View closeSegmentationButton;
    private StringBuilder covertingMidashigo;
    private StringBuilder covertingYomi;
    private int flickerKeyHeight;
    private Drawable flickerKeyPopupBackground;
    private Drawable flickerKeyPopupSelectedBackground;
    private int flickerKeyTextSize;
    private double flickerMiddleKeyRadius;
    private boolean gridViewFunctionButtonPressed;
    private InputContextRequestHandler inputContextRequestHandler;
    private boolean isKeypadFlickrInputSupported;
    private boolean isKeypadMultitapInputSupported;
    private boolean isSegmentationPopupNeeded;
    private boolean isTopKeyFlicerUp;
    protected int japaneseFlickerKeyIndex;
    protected int japaneseFlickerKeyRelativeIndex;
    private ExtractedText lastTimeEditorText;
    private CharSequence lastTimeInlineText;
    private View leftArrowButton;
    private BackgroundColorSpan mBKMultiptappingCharSpan;
    private BackgroundColorSpan mBKNextWordPredictionSpan;
    private BackgroundColorSpan mBKRangeConversionDefaultSpan;
    private BackgroundColorSpan mBKRemainSpan;
    private BackgroundColorSpan mBKWordErrorSpan;
    private ForegroundColorSpan mColorRecommended;
    private StringBuilder mConfirmedInlineRomaji;
    private StringBuilder mConfirmedKanji;
    private final StringBuilder mContextBuffer;
    private int mConvert;
    private int mCurrentCursorPosInline;
    private int mCurrentKeyNav;
    private int mCursorPosCount;
    private final StyleSpan mExactWordSpan;
    private ForegroundColorSpan mFGMultiptappingCharSpan;
    private ForegroundColorSpan mFGNextWordPredictionSpan;
    private ForegroundColorSpan mFGWordErrorSpan;
    private View mFlickerView;
    protected CharSequence mInlineTextBak;
    protected SpannableStringBuilder mInlineWord;
    private XT9CoreJapaneseInput mJapaneseInput;
    private PopupWindow mJapaneseNavPopup;
    private JapaneseWordListViewContainer mJapaneseWordListViewContainer;
    protected JapaneseWordPageContainer mJpWordPageContainer;
    private int[] mKanaDivInfoOfInline;
    private int mLastSelEnd;
    private int mLastSelStart;
    private boolean mNavOn;
    private OpenWnnEngineJAJP mOpenwnnEngine;
    protected int mPopupPreviewX;
    protected int mPopupPreviewY;
    private int[] mRCOutPhraseDivInfo;
    private int[] mRCReadingDivInfo;
    private int[] mRomajiDivInfoOfInline;
    protected List<ArrayList<KeyboardEx.GridKeyInfo>> mRows;
    private boolean mScrollable;
    protected SpannableStringBuilder mSuggestedWord;
    private final AtomicInteger mSuggestedWordIndex;
    private boolean mUpdatingInline;
    private final UnderlineSpan mWordComposeSpan;
    protected List<CharSequence> mWordList;
    protected List<CharSequence> mWordListBak;
    private int portraitInputOptions;
    private View rightArrowButton;
    private PopupWindow segmentationPopup;
    private View segmentationView;
    TouchKeyActionHandler touchKeyActionHandler;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.KeyboardViewEx
    public boolean isMultitapHandledInCore() {
        return isKeypadInput();
    }

    protected void addWordtoList(String word) {
        boolean found = false;
        int nListSize = this.mWordList.size();
        int nWordLen = word.length();
        int i = 0;
        while (true) {
            if (i >= nListSize) {
                break;
            }
            CharSequence cs = this.mWordList.get(i);
            if (cs.length() == nWordLen) {
                int l = 0;
                while (l < cs.length() && cs.charAt(l) == word.charAt(l)) {
                    l++;
                }
                if (l == cs.length()) {
                    found = true;
                    break;
                }
            }
            i++;
        }
        if (!found) {
            SpannableStringBuilder newSpanBuffer = new SpannableStringBuilder();
            newSpanBuffer.append((CharSequence) String.valueOf(word.toCharArray(), 0, word.length()));
            this.mWordList.add(nListSize, newSpanBuffer);
        }
    }

    public boolean addDigitsFromUILayer(String inlineWord) {
        if (inlineWord.length() <= 0) {
            return false;
        }
        char[] charsin = new char[64];
        int i = 0;
        while (i < inlineWord.length() && i < 64) {
            charsin[i] = inlineWord.charAt(i);
            i++;
        }
        if (JapaneseCharacterSetUtils.isHiraIndex(charsin[0]) > 0) {
            char[] charsout = new char[64];
            int[] dlen = new int[1];
            JapaneseCharacterSetUtils.convertHira2DigitalFull(charsin, i, charsout, dlen);
            addWordtoList(String.valueOf(charsout, 0, dlen[0]));
            char[] charsout2 = new char[64];
            int[] dlen2 = new int[1];
            JapaneseCharacterSetUtils.convertHira2Digital(charsin, i, charsout2, dlen2);
            addWordtoList(String.valueOf(charsout2, 0, dlen2[0]));
        }
        return true;
    }

    private void updateWordListView(List<CharSequence> wordList, int defaultWordIndex, boolean bShowHighlight) {
        if (bShowHighlight) {
            this.candidatesListViewCJK.enableHighlight();
        } else {
            this.candidatesListViewCJK.disableHighlight();
        }
        this.candidatesListViewCJK.setSuggestions(wordList, defaultWordIndex);
        syncCandidateDisplayStyleToMode();
        setCandidatesViewShown(true);
    }

    @Override // com.nuance.swype.input.InputView
    public void clearSuggestions() {
        if (isValidBuild() && this.candidatesListViewCJK != null) {
            updateWordListView(null, 0, false);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.InputView
    public void removeAllMessages() {
        for (int msg = 1; msg <= 19; msg++) {
            this.JpInputViewHandler.removeMessages(msg);
        }
        this.JpInputViewHandler.removeMessages(21);
        this.JpInputViewHandler.removeMessages(22);
        this.JpInputViewHandler.removeMessages(23);
    }

    public void setContainerView(JapaneseWordPageContainer container) {
        this.mJpWordPageContainer = container;
    }

    private void removeToastMsg(int msg) {
        QuickToast.hide();
        this.JpInputViewHandler.removeMessages(msg);
    }

    private void postToastMsg(int msg) {
        removeToastMsg(msg);
        this.JpInputViewHandler.sendEmptyMessageDelayed(msg, 250L);
    }

    @Override // com.nuance.swype.input.InputView
    public void create(IME ime, XT9CoreInput xt9coreinput, SpeechWrapper speechWrapper) {
        super.create(ime, xt9coreinput, speechWrapper);
        this.mJapaneseInput = (XT9CoreJapaneseInput) xt9coreinput;
        this.mKeyboardSwitcher = new KeyboardSwitcher(this.mIme, this.mJapaneseInput);
        this.mKeyboardSwitcher.setInputView(this);
        setOnKeyboardActionListener(this.mIme);
        IMEApplication imeApp = IMEApplication.from(getContext());
        this.charUtils = imeApp.getCharacterUtilities();
        readStyles(getContext());
        this.mOpenwnnEngine = IMEApplication.from(getContext()).getSwypeCoreLibMgr().createOpenWnnEngineJAJP(imeApp.getBuildInfo().getCoreLibName());
        this.mOpenwnnEngine.init();
    }

    @Override // com.nuance.swype.input.InputView
    public void destroy() {
        super.destroy();
        setOnKeyboardActionListener((IME) null);
        this.mKeyboardSwitcher = null;
    }

    @Override // com.nuance.swype.input.InputView
    @SuppressLint({"InflateParams", "PrivateResource"})
    public View createCandidatesView(CandidatesListView.CandidateListener onSelectListener) {
        if (this.mJapaneseInput == null) {
            return null;
        }
        if (this.mJapaneseWordListViewContainer == null) {
            IMEApplication app = IMEApplication.from(this.mIme);
            LayoutInflater inflater = app.getThemedLayoutInflater(this.mIme.getLayoutInflater());
            app.getThemeLoader().setLayoutInflaterFactory(inflater);
            this.mJapaneseWordListViewContainer = (JapaneseWordListViewContainer) inflater.inflate(R.layout.japanese_candidates, (ViewGroup) null);
            app.getThemeLoader().applyTheme(this.mJapaneseWordListViewContainer);
            this.mJapaneseWordListViewContainer.initViews();
            this.mJapaneseWordListViewContainer.requestLayout();
            this.candidatesListViewCJK = (CJKCandidatesListView) this.mJapaneseWordListViewContainer.findViewById(R.id.cjk_candidates);
        }
        this.candidatesListViewCJK.setOnWordSelectActionListener(this);
        this.candidatesListViewCJK.disableHighlight();
        this.wordListViewContainerCJK = this.mJapaneseWordListViewContainer;
        return this.mJapaneseWordListViewContainer;
    }

    @Override // com.nuance.swype.input.InputView
    public View getWordCandidateListContainer() {
        return this.mJapaneseWordListViewContainer;
    }

    @Override // com.nuance.swype.input.InputView
    public void clearCurrentActiveWord() {
        clearCurrentInline(getCurrentInputConnection());
        clearAllKeys();
    }

    @Override // com.nuance.swype.input.InputView
    public void startInput(InputFieldInfo inputFieldInfo, boolean restarting) {
        boolean z = false;
        InputConnection ic = getCurrentInputConnection();
        if (this.mJapaneseInput != null && ic != null) {
            if (this.candidatesListViewCJK != null) {
                this.candidatesListViewCJK.updateCandidatesSize();
            }
            this.keyboardTouchEventDispatcher.resisterTouchKeyHandler(this.touchKeyActionHandler);
            this.keyboardTouchEventDispatcher.registerFlingGestureHandler(this.flingGestureListener);
            this.keyboardTouchEventDispatcher.wrapTouchEvent(this);
            this.mJapaneseInput.setInputContextRequestListener(InputContextRequestDispatcher.getDispatcherInstance().setHandler(this.inputContextRequestHandler));
            this.mIme.getEditState().startSession();
            dismissPopupKeyboard();
            flushCurrentActiveWord();
            super.startInput(inputFieldInfo, restarting);
            this.mCompletionOn = false;
            loadSettings();
            this.mRecaptureOn = false;
            InputMethods.InputMode inputMode = this.mCurrentInputLanguage.getCurrentInputMode();
            this.JpInputViewHandler.removeMessages(15);
            this.JpInputViewHandler.sendEmptyMessageDelayed(15, 5L);
            KeyboardEx.KeyboardLayerType currentLayer = this.mKeyboardSwitcher.currentKeyboardLayer();
            if (restarting && currentLayer != KeyboardEx.KeyboardLayerType.KEYBOARD_INVALID) {
                z = true;
            }
            this.isKeepingKeyboard = z;
            if (this.isKeepingKeyboard) {
                this.mKeyboardSwitcher.createKeyboardForTextInput(currentLayer, inputFieldInfo, inputMode);
            } else {
                this.mKeyboardSwitcher.createKeyboardForTextInput(inputFieldInfo, inputMode);
            }
            this.mCompletions.clear();
            this.candidatesListViewCJK.clear();
            if (isValidBuild()) {
                if (inputFieldInfo.isPasswordField()) {
                    this.mTextInputPredictionOn = true;
                }
                if (this.mIme.isFullscreenMode() && (this.mInputFieldInfo.isCompletionField() || this.mInputFieldInfo.isURLField())) {
                    this.mTextInputPredictionOn = true;
                }
            }
            clearKeyOffsets();
            initSegmentationPopup();
            postDelayResumeSpeech();
            showUserThemeWclMessage(this.JpInputViewHandler);
            showTrialExpireWclMessage("CJK");
        }
    }

    private void postDelayResumeSpeech() {
        if (this.JpInputViewHandler.hasMessages(11)) {
            this.JpInputViewHandler.removeMessages(11);
        }
        this.JpInputViewHandler.sendEmptyMessageDelayed(11, 1L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void postDelayShowSegmentationPopupMessage() {
        if (this.JpInputViewHandler.hasMessages(23)) {
            this.JpInputViewHandler.removeMessages(23);
        }
        this.JpInputViewHandler.sendEmptyMessageDelayed(23, 5L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startInputSession() {
        if (this.mJapaneseInput != null) {
            this.mJapaneseInput.startSession();
            this.mJapaneseInput.setShiftState(Shift.ShiftState.OFF);
            setLanguage(this.mJapaneseInput);
            this.mJapaneseInput.setExplicitLearning(false, false);
            this.mJapaneseInput.setPunctuationBreaking(isKeypadMultitapInputSupported() ? false : true);
            this.mJapaneseInput.setAttribute(101, this.mWordCompletionPoint);
            this.mLastSelStart = -1;
            this.mLastSelEnd = -1;
            InputConnection ic = getCurrentInputConnection();
            if (ic != null) {
                updateContextBufferIfNecessary(ic);
            }
        }
    }

    private void endInputSession() {
        if (this.mJapaneseInput != null) {
            this.mJapaneseInput.breakContext();
            this.mJapaneseInput.finishSession();
        }
    }

    @Override // com.nuance.swype.input.InputView
    public void finishInput() {
        removeAllMessages();
        if (this.mJapaneseInput != null) {
            this.mEditState.endSession();
            flushCurrentActiveWord();
            endInputSession();
            this.mConvert = 0;
            this.mLastInput = 0;
            dismissSegmentationPopup();
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
            ic.endBatchEdit();
            clearAllKeys();
            this.mConvert = 0;
            hideGridCandidatesView();
            this.mConfirmedInlineRomaji.setLength(0);
            this.mConfirmedKanji.setLength(0);
        }
        dismissSegmentationPopup();
    }

    @Override // com.nuance.swype.input.InputView
    public void updateSelection(int oldSelStart, int oldSelEnd, int newSelStart, int newSelEnd, int[] candidatesIndices) {
        ExtractedText text;
        boolean cursorChanged = true;
        super.updateSelection(oldSelStart, oldSelEnd, newSelStart, newSelEnd, candidatesIndices);
        InputConnection ic = getCurrentInputConnection();
        if (ic != null) {
            if (isConverting()) {
                if (this.mJapaneseInput != null) {
                    if ((newSelStart != oldSelStart || newSelEnd != oldSelEnd) && oldSelStart != oldSelEnd) {
                        if (candidatesIndices[0] == candidatesIndices[1] && candidatesIndices[0] == -1) {
                            flushCurrWordAndClearWCL();
                        } else if (newSelStart == newSelEnd && (text = ic.getExtractedText(new ExtractedTextRequest(), 0)) != null && this.lastTimeEditorText != null && text.text != null && this.lastTimeEditorText.text != null && !text.text.equals(this.lastTimeEditorText.text) && this.lastTimeInlineText != null && this.mInlineWord != null && this.lastTimeInlineText.equals(this.mInlineWord.toString())) {
                            flushCurrWordAndClearWCL();
                        }
                    }
                    this.lastTimeEditorText = ic.getExtractedText(new ExtractedTextRequest(), 0);
                    if (this.mInlineWord != null) {
                        this.lastTimeInlineText = this.mInlineWord.toString();
                        return;
                    } else {
                        this.lastTimeInlineText = "";
                        return;
                    }
                }
                return;
            }
            this.lastTimeEditorText = ic.getExtractedText(new ExtractedTextRequest(), 0);
            if (this.mInlineWord != null) {
                this.lastTimeInlineText = this.mInlineWord.toString();
            } else {
                this.lastTimeInlineText = "";
            }
            if (this.mJapaneseInput != null) {
                CharSequence charBeforeCusor = ic.getTextBeforeCursor(1, 0);
                if (charBeforeCusor == null || charBeforeCusor.length() == 0) {
                    this.mJapaneseInput.breakContext();
                } else if (this.mJapaneseInput.getKeyCount() == 0) {
                    this.mJapaneseInput.breakContext();
                }
                this.mEditState.cursorChanged(charBeforeCusor);
                if (newSelStart == candidatesIndices[1] && newSelEnd == candidatesIndices[1] && candidatesIndices[1] != -1) {
                    cursorChanged = false;
                }
                if (!this.mUpdatingInline && cursorChanged && this.candidatesListViewCJK.wordCount() > 0) {
                    if (this.mJapaneseInput.getKeyCount() == 0) {
                        clearCurrentInline(getCurrentInputConnection());
                    }
                    if (this.mInlineWord != null && this.mInlineWord.length() > 0) {
                        flushCurrWordAndClearWCL();
                    }
                    this.mEditState.start();
                } else if (this.mRecaptureOn && newSelEnd > newSelStart && this.mLastSelStart != newSelStart && this.mLastSelEnd != newSelEnd) {
                    ic.beginBatchEdit();
                    ic.setSelection(newSelEnd, newSelEnd);
                    ic.setSelection(newSelStart, newSelEnd);
                    ic.endBatchEdit();
                    if (this.candidatesListViewCJK.wordCount() > 0) {
                        flushCurrentActiveWord();
                    }
                }
            }
            this.mLastSelStart = newSelStart;
            this.mLastSelEnd = newSelEnd;
            this.mUpdatingInline = false;
            this.JpInputViewHandler.removeMessages(21);
        }
    }

    @Override // com.nuance.swype.input.InputView
    public void displayCompletions(CompletionInfo[] completions) {
        if (this.mCompletionOn && !this.mTextInputPredictionOn) {
            this.mCompletions.update(completions);
            this.candidatesListViewCJK.setSuggestions(this.mCompletions.getDisplayItems(), 0);
            syncCandidateDisplayStyleToMode();
        }
    }

    @Override // com.nuance.swype.input.InputView
    public boolean handleKeyDown(int keyCode, KeyEvent event) {
        if (!super.handleKeyDown(keyCode, event) && this.mJapaneseInput != null && this.candidatesListViewCJK != null && (keyCode == 21 || keyCode == 22 || keyCode == 23)) {
            if (this.mSuggestedWord != null && this.mSuggestedWord.length() > 0 && this.mJapaneseInput.getKeyCount() > 0) {
                String selectedWord = this.mSuggestedWord.toString();
                selectWord(this.mSuggestedWordIndex.get(), selectedWord, null);
                return true;
            }
            if (this.candidatesListViewCJK.wordCount() > 0) {
                clearCurrentInline(getCurrentInputConnection());
                clearAllKeys();
            }
        }
        return false;
    }

    private boolean isWordAcceptingSymbol(char ch) {
        return this.charUtils.isPunctuationOrSymbol(ch) || this.charUtils.isDiacriticMark(ch);
    }

    @Override // com.nuance.swype.input.InputView
    @SuppressLint({"DefaultLocale"})
    public void onText(CharSequence text, long eventTime) {
        if (text != null && text.length() != 0) {
            if (this.mJapaneseInput == null || !this.mTextInputPredictionOn) {
                InputConnection ic = getCurrentInputConnection();
                if (ic != null) {
                    this.mConvert = 0;
                    ic.beginBatchEdit();
                    ic.commitText(text, 1);
                    ic.endBatchEdit();
                    return;
                }
                return;
            }
            if (text.length() > 0) {
                if (composingState()) {
                    String selectedWord = this.mInlineWord.toString();
                    if (isConverting()) {
                        flushCurrWordAndClearWCL();
                    } else {
                        selectWord(-1, selectedWord, null);
                    }
                } else {
                    clearCurrentInline(getCurrentInputConnection());
                    clearAllKeys();
                }
                InputConnection ic2 = getCurrentInputConnection();
                if (ic2 != null) {
                    this.mConvert = 0;
                    ic2.beginBatchEdit();
                    ic2.commitText(text, 1);
                    ic2.endBatchEdit();
                }
                this.mEditState.punctuationOrSymbols();
                return;
            }
            if (Character.isDigit(text.charAt(text.length() - 1)) && !composingState()) {
                clearCurrentInline(getCurrentInputConnection());
                clearAllKeys();
                InputConnection ic3 = getCurrentInputConnection();
                if (ic3 != null) {
                    this.mConvert = 0;
                    ic3.beginBatchEdit();
                    ic3.commitText(text, 1);
                    ic3.endBatchEdit();
                }
                this.mEditState.punctuationOrSymbols();
                return;
            }
            if (text.length() == 1 && this.mKeyboardSwitcher.isSymbolMode()) {
                InputConnection ic4 = getCurrentInputConnection();
                if (ic4 != null) {
                    this.mConvert = 0;
                    ic4.beginBatchEdit();
                    ic4.commitText(text, 1);
                    ic4.endBatchEdit();
                    return;
                }
                return;
            }
            if (this.mKeyboardSwitcher.isKeypadInput()) {
                int textcode = text.charAt(0);
                if (this.mJapaneseInput.processKey(null, textcode, Shift.ShiftState.OFF, eventTime)) {
                    this.mEditState.characterTyped((char) textcode);
                }
                updateCandidates(Candidates.Source.INVALID);
            }
        }
    }

    private void handleFlickerPopup() {
        log.d("handleFlickerPopup()... japaneseFlickerKeyIndex: ", String.valueOf(this.japaneseFlickerKeyIndex));
        if (isValidFlickerRelativeIndex()) {
            if (this.mInlineWord == null || this.mInlineWord.length() < 64) {
                KeyboardEx.Key[] visibleKeys = this.mKeys;
                int basecode = visibleKeys[this.japaneseFlickerKeyIndex].codes[0];
                int textcode = 0;
                if (basecode == JP_KEYPAD_YA_KEY) {
                    if (this.japaneseFlickerKeyRelativeIndex == 0 || this.japaneseFlickerKeyRelativeIndex == 2 || this.japaneseFlickerKeyRelativeIndex == 4) {
                        textcode = visibleKeys[this.japaneseFlickerKeyIndex].codes[this.japaneseFlickerKeyRelativeIndex / 2];
                    }
                } else {
                    textcode = visibleKeys[this.japaneseFlickerKeyIndex].codes[this.japaneseFlickerKeyRelativeIndex];
                }
                if (textcode != 0) {
                    if (this.mJapaneseInput == null || !this.mTextInputPredictionOn) {
                        InputConnection ic = getCurrentInputConnection();
                        if (ic != null) {
                            ic.beginBatchEdit();
                            sendKeyChar((char) textcode);
                            ic.endBatchEdit();
                            return;
                        }
                        return;
                    }
                    if (this.mConvert != 0) {
                        flushCurrentActiveWord();
                        this.mConvert = 0;
                    }
                    int hiraIndex = JapaneseCharacterSetUtils.isHiraIndex((char) textcode);
                    if (textcode >= 12353 && textcode <= JP_KEYPAD_LONG_VOWEL_KEY && 256 <= hiraIndex && hiraIndex < 512 && this.mJapaneseInput.getKeyCount() < 32) {
                        if (this.mJapaneseInput.processKey(null, textcode, Shift.ShiftState.OFF, SystemClock.uptimeMillis())) {
                            this.mEditState.characterTyped((char) textcode);
                        }
                        updateCandidates(Candidates.Source.INVALID);
                    }
                }
            }
        }
    }

    @Override // com.nuance.swype.input.InputView
    public void handleCharKey(Point point, int primaryCode, int[] keyCodes, long eventTime) {
        super.handleCharKey(point, primaryCode, keyCodes, eventTime);
        removeToastMsg(5);
        if (this.mJapaneseInput == null || !this.mTextInputPredictionOn) {
            this.mEditState.end();
            showNav(-1);
            if (!isMultitapping()) {
                sendKeyChar((char) primaryCode);
            } else {
                this.mInlineWord.clear();
                this.mInlineWord.clearSpans();
                this.mInlineWord.append((char) primaryCode);
                this.mInlineWord.setSpan(this.mBKMultiptappingCharSpan, 0, this.mInlineWord.length(), R.styleable.ThemeTemplate_emojiCategoryIconSmiles);
                this.mInlineWord.setSpan(this.mFGMultiptappingCharSpan, 0, this.mInlineWord.length(), R.styleable.ThemeTemplate_emojiCategoryIconSmiles);
                InputConnection ic = getCurrentInputConnection();
                if (ic != null) {
                    ic.setComposingText(this.mInlineWord, 1);
                }
            }
        } else if (CharacterUtilities.isWhiteSpace((char) primaryCode)) {
            if (isConverting()) {
                if (this.mWordList.size() > 0 && this.mSuggestedWordIndex.get() >= 0) {
                    selectWord(this.mSuggestedWordIndex.get(), this.mWordList.get(this.mSuggestedWordIndex.get()), null);
                }
            } else {
                handleWhiteSpaces(primaryCode);
                this.mJapaneseInput.clearAllKeys();
                this.mInlineWord.clear();
                this.mInlineWord.clearSpans();
            }
        } else if (isMultitapping()) {
            if (this.mInlineWord.length() != 1 || !isCommaOrPeriod(this.mInlineWord.charAt(0))) {
                if (this.mKeyboardLayoutId == 2304) {
                    flushCurrWordAndClearWCL();
                } else if (this.mKeyboardLayoutId == 1536 && isCommaOrPeriod(primaryCode) && this.mInlineWord.length() > 0) {
                    flushCurrWordByIndexAndClearWCL(0, this.mInlineWord.length());
                }
            }
            handleExplicitInput(primaryCode);
        } else if (this.mKeyboardLayoutId == 1536 && this.mKeyboardSwitcher.isAlphabetMode() && !isCommaOrPeriod(primaryCode)) {
            if (this.isKeypadFlickrInputSupported && !this.isKeypadMultitapInputSupported && keyCodes != null && this.mConvert != 0 && this.mInlineWord.length() > 0) {
                flushCurrentActiveWord();
                this.mConvert = 0;
                if (isValidFlickerIndex() && this.mJapaneseNavPopup != null && this.mJapaneseNavPopup.isShowing()) {
                    showNav(-1);
                    resetFlickerIndex();
                }
            }
            handleExplicitInput(primaryCode);
        } else if (isWordAcceptingSymbol((char) primaryCode) || CharacterUtilities.isDigit((char) primaryCode) || !this.mKeyboardSwitcher.isAlphabetMode()) {
            handlePunctOrSymbol(primaryCode);
        } else {
            if (isRomajiAlphaCharacterConverting(primaryCode)) {
                if (isFullScreenMode()) {
                    cleanInlineWordSpanAndFlush();
                }
                flushCurrentActiveWord();
            }
            handleExplicitInput(primaryCode);
        }
        recordUsedTimeTapDisplaySelectionList();
        if (this.charUtils.isWordCompounder((char) primaryCode) && composingState() && !this.mKeyboardSwitcher.isAlphabetMode() && this.mKeyboardSwitcher.supportsAlphaMode()) {
            toggleKeyboardMode();
        }
    }

    @Override // com.nuance.swype.input.InputView
    public void handleEmotionKey() {
        super.handleEmotionKey();
        flushCurrentActiveWord();
    }

    private boolean isRomajiAlphaCharacterConverting(int keyCode) {
        return isConverting() && this.mKeyboardLayoutId == 2304 && ((97 <= keyCode && keyCode <= 122) || keyCode == JP_KEYPAD_LONG_VOWEL_KEY);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void flushCurrWordAndClearWCL() {
        flushCurrentActiveWord();
        if (this.mJapaneseWordListViewContainer != null) {
            this.mJapaneseWordListViewContainer.clearPhraseListView();
        }
    }

    private void flushCurrWordByIndexAndClearWCL(int index, int count) {
        InputConnection ic = getCurrentInputConnection();
        if (ic != null) {
            ic.beginBatchEdit();
            ic.finishComposingText();
            ic.endBatchEdit();
            this.mJapaneseInput.clearKeyByIndex(index, count);
            this.mConvert = 0;
            hideGridCandidatesView();
            this.mConfirmedInlineRomaji.setLength(0);
            this.mConfirmedKanji.setLength(0);
        }
        dismissSegmentationPopup();
        if (this.mJapaneseWordListViewContainer != null) {
            this.mJapaneseWordListViewContainer.clearPhraseListView();
        }
    }

    private boolean hasActiveKeySeq() {
        return this.mJapaneseInput.getKeyCount() > 0 || multitapIsInvalid();
    }

    private boolean composingState() {
        return this.candidatesListViewCJK != null && this.candidatesListViewCJK.wordCount() > 0 && this.mJapaneseInput.getKeyCount() > 0 && this.mEditState.current() != 8;
    }

    @Override // com.nuance.swype.input.InputView
    public void handleTrace(KeyboardViewEx.TracePoints trace) {
        if (this.mKeyboardSwitcher.isKeypadInput()) {
            if (isValidFlickerIndex()) {
                handleFlickerPopup();
                showNav(-1);
                resetFlickerIndex();
                return;
            }
            return;
        }
        if (isConverting() || this.mLastInput == 1) {
            this.mConvert = 0;
        }
        if (this.touchKeyActionHandler.processStoredTouch()) {
            int status = updateCandidates(Candidates.Source.TRACE);
            if (status <= 0) {
                this.mLastInput = 0;
                if (status != -1) {
                    if (this.mJapaneseWordListViewContainer != null) {
                        this.mJapaneseWordListViewContainer.clearPhraseListView();
                    }
                    setNotMatchToolTipSuggestion();
                }
                this.mJapaneseInput.clearAllKeys();
                return;
            }
            this.mLastInput = 2;
        }
    }

    public boolean isKeypadInput() {
        return this.mKeyboardSwitcher.isKeypadInput() && this.mKeyboardSwitcher.isAlphabetMode();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.KeyboardViewEx
    public void bufferDrawTrace(Canvas canvas) {
        if (!isKeypadInput()) {
            super.bufferDrawTrace(canvas);
        }
    }

    public boolean isKeyboardInput() {
        return this.mKeyboardLayoutId == 2304 && this.mKeyboardSwitcher.isAlphabetMode();
    }

    @Override // com.nuance.swype.input.InputView, com.nuance.swype.input.KeyboardViewEx
    public void setKeyboard(KeyboardEx keyboard) {
        super.setKeyboard(keyboard);
        if (getKeyboard() == null) {
            this.isTraceEnabledOnKeyboard = false;
            return;
        }
        setKeyboarLayoutDatabase();
        setMultitapOrAmbigMode();
        if (this.mKeyboardLayoutId == 1536) {
            setPressDownPreviewEnabled(false);
        } else {
            setPressDownPreviewEnabled(true);
        }
        this.isTraceEnabledOnKeyboard = isValidBuild() && ((this.mNavOn && isKeypadInput() && this.isKeypadFlickrInputSupported) || (isKeyboardInput() && this.mTraceInputSuggestionOn)) && this.mKeyboardSwitcher.isAlphabetMode() && getKeyboard().getKeyboardDockMode() != KeyboardEx.KeyboardDockMode.DOCK_SPLIT;
        this.mJapaneseInput.enableTrace(this.isTraceEnabledOnKeyboard);
        this.mJapaneseInput.setAttribute(99, this.autoCorrectionEnabled && isKeyboardInput());
    }

    private void setKeyboarLayoutDatabase() {
        if (getKeyboard() != null) {
            if (this.mKeyboardLayoutId == 1536) {
                this.mJapaneseInput.setKeyboardType(2);
            } else if (this.mKeyboardLayoutId == 2304) {
                this.mJapaneseInput.setKeyboardType(1);
            } else {
                this.mJapaneseInput.setKeyboardType(0);
            }
        }
    }

    private void toggleKeyboardMode() {
        this.mKeyboardSwitcher.toggleSymbolKeyboard();
        abortKey();
        setMultitapOrAmbigMode();
    }

    private void updateRomajiInlineDivisionInfo() {
        this.mCursorPosCount = this.mJapaneseInput.getInlineDivInfo(256, this.mRomajiDivInfoOfInline, this.mKanaDivInfoOfInline);
        this.mCurrentCursorPosInline = this.mCursorPosCount;
    }

    private void clearOneSymbol() {
        this.mJapaneseInput.clearKeyByIndex(getRomajiPosByCursorPos(this.mCurrentCursorPosInline - 1), getRomajiBytesOfCurrentPos(this.mCurrentCursorPosInline - 1));
        updateRomajiInlineDivisionInfo();
    }

    @Override // com.nuance.swype.input.InputView
    public boolean handleBackspace(int repeatedCount) {
        if (this.mKeyboardSwitcher.isKeypadInput() && this.mInlineWord.length() > 0) {
            repeatedCount = 1;
        }
        timeoutWhenMultitappingCommaOrPeriod();
        InputConnection ic = getCurrentInputConnection();
        if (this.mJapaneseInput == null || ic == null) {
            this.mLastInput = 4;
            sendBackspace(repeatedCount);
            this.mEditState.backSpace();
            return true;
        }
        if (this.mConvert == 1 || this.mConvert == 2) {
            updateCandidates(Candidates.Source.INVALID);
            this.mConvert = 0;
            return true;
        }
        if (this.mLastInput == 2 || this.mJapaneseInput.hasTraceInput()) {
            this.mConvert = 0;
            this.mLastInput = 4;
            ic.beginBatchEdit();
            ic.commitText("", 1);
            ic.endBatchEdit();
            clearAllKeys();
            clearSuggestions();
            this.mEditState.backSpace();
            if (this.mEditState.current() != 1) {
                return true;
            }
            this.mEditState.start();
            return true;
        }
        if (isMultitapping() && !isMultitapHandledInCore()) {
            if (this.mCurrentCursorPosInline <= 0) {
                return true;
            }
            clearOneSymbol();
            return true;
        }
        if (this.mJapaneseInput.getKeyCount() > 0) {
            clearOneSymbol();
            if (this.mCurrentCursorPosInline == 0) {
                this.mLastInput = 4;
                ic.beginBatchEdit();
                ic.commitText("", 1);
                ic.endBatchEdit();
                clearAllKeys();
                clearSuggestions();
                this.mEditState.backSpace();
                if (this.mEditState.current() != 1) {
                    return true;
                }
                this.mEditState.start();
                return true;
            }
            if (this.mJapaneseInput.getKeyCount() > 0) {
                updateCandidates(Candidates.Source.INVALID);
                return true;
            }
            this.mLastInput = 4;
            ic.beginBatchEdit();
            ic.commitText("", 1);
            ic.endBatchEdit();
            clearAllKeys();
            clearSuggestions();
            this.mEditState.backSpace();
            if (this.mEditState.current() != 1) {
                return true;
            }
            this.mEditState.start();
            return true;
        }
        this.mLastInput = 4;
        sendBackspace(repeatedCount);
        clearAllKeys();
        clearSuggestions();
        this.mEditState.backSpace();
        return !TextUtils.isEmpty(ic.getTextBeforeCursor(1, 0));
    }

    @Override // com.nuance.swype.input.InputView
    public boolean handleSpace(boolean quickPressed, int repeatedCount) {
        InputConnection ic = getCurrentInputConnection();
        if (this.mJapaneseInput == null || ic == null) {
            sendSpace();
        } else if (isConverting()) {
            moveHighlightToNearCandidateSoftKeyboard(22);
        } else if (!timeoutWhenMultitappingCommaOrPeriod() && this.mInlineWord.length() > 0 && !this.mKeyboardSwitcher.isSymbolMode()) {
            doConversion();
        } else {
            this.mLastInput = 3;
            boolean addSpace = true;
            boolean displayNWP = false;
            if (this.mSuggestedWordIndex.get() != -1 && this.mInlineWord.length() > 0) {
                displayNWP = this.mNextWordPredictionOn;
                String selectedWord = this.mInlineWord.toString();
                selectWord(-2, selectedWord, null);
                this.mSuggestedWordIndex.set(-1);
                addSpace = false;
            }
            if (quickPressed && repeatedCount < 2 && this.mAutoPunctuationOn && this.mTextInputPredictionOn) {
                ic.beginBatchEdit();
                CharSequence cSeqBefore = ic.getTextBeforeCursor(2, 0);
                ic.endBatchEdit();
                if (cSeqBefore != null && cSeqBefore.length() == 2 && CharacterUtilities.isWhiteSpace(cSeqBefore.charAt(1)) && !CharacterUtilities.isWhiteSpace(cSeqBefore.charAt(0)) && !this.charUtils.isPunctuationOrSymbol(cSeqBefore.charAt(0))) {
                    handleAutoPunct();
                    addSpace = false;
                }
            }
            if (addSpace) {
                processSpaceKeyState(ic);
                sendKeyChar(' ');
            } else {
                this.mEditState.start();
            }
            if (displayNWP) {
                updateCandidates(Candidates.Source.INVALID);
            }
        }
        return true;
    }

    @Override // com.nuance.swype.input.InputView
    public void handleClose() {
        flushCurrentActiveWord();
        removeAllMessages();
        super.handleClose();
    }

    private void ShowNextWordPrediction(CharSequence hiragana, CharSequence word) {
        WnnWord previousWord;
        List<String> predictionList;
        this.mWordList.clear();
        if (this.mOpenwnnEngine != null) {
            if (hiragana != null) {
                previousWord = this.mOpenwnnEngine.doExactSearch(hiragana.toString(), word.toString());
            } else {
                previousWord = this.mOpenwnnEngine.doExactSearch(word.toString(), word.toString());
            }
            if (previousWord != null && (predictionList = this.mOpenwnnEngine.doPredictionSearch(previousWord)) != null) {
                for (String aWord : predictionList) {
                    this.mWordList.add(new SpannableStringBuilder(aWord));
                }
            }
        }
        this.mSuggestedWord.clear();
        updateWordListView(this.mWordList, 0, false);
    }

    @Override // com.nuance.swype.input.chinese.CJKCandidatesListView.OnWordSelectActionListener
    public void selectWord(int index, CharSequence word, View source) {
        InputConnection ic = getCurrentInputConnection();
        if (isConverting()) {
            if (ic != null) {
                ic.beginBatchEdit();
                ic.commitText(word.toString(), 1);
                ic.endBatchEdit();
            }
            this.covertingMidashigo.append(word);
            boolean allConfirmed = confirmSelectedWordWhenConverting(index);
            if (index == -1 || allConfirmed) {
                this.mConvert = 0;
                this.mLastInput = 0;
                if (!this.mInputFieldInfo.isUDBSubstitutionField() && !this.mInputFieldInfo.isPasswordField()) {
                    this.mJapaneseInput.noteWordDone(this.covertingYomi.toString(), this.covertingMidashigo.toString());
                }
                resetConversionStrings();
                SpannableStringBuilder kana = new SpannableStringBuilder();
                this.mJapaneseInput.getInlineText(kana);
                if (kana.length() > 0) {
                    ShowNextWordPrediction(kana.toString(), word.toString());
                } else {
                    ShowNextWordPrediction(word.toString(), word.toString());
                }
                if (allConfirmed) {
                    clearAllKeys();
                }
                if (isKeyboardInput()) {
                    dismissSegmentationPopup();
                    return;
                }
                return;
            }
            this.mConvert = 2;
            this.mLastInput = 4;
            return;
        }
        if (isKeyboardInput()) {
            dismissSegmentationPopup();
        }
        if (this.mWordList == null) {
            if (this.mCompletionOn && index >= 0 && index < this.mCompletions.size()) {
                ic.commitCompletion(this.mCompletions.get(index));
                return;
            }
            return;
        }
        this.mEditState.selectWord(word, this.mSuggestedWord.toString());
        if (ic != null) {
            ic.beginBatchEdit();
            ic.commitText(word.toString(), 1);
            ic.endBatchEdit();
        }
        this.mSuggestedWordIndex.set(-1);
        if (getSpeechAlternateCandidates().size() > 0) {
            super.speechChooseCandidate(index);
        } else if (this.mKeyboardSwitcher.isKeypadInput() && this.mInlineWord.length() > 0 && JapaneseCharacterSetUtils.isHiraIndex(this.mInlineWord.charAt(0)) == 0) {
            int index2 = 255;
            if (this.mWordListBak != null) {
                int i = 0;
                while (true) {
                    if (i >= this.mWordListBak.size()) {
                        break;
                    }
                    if (!this.mWordListBak.get(i).toString().equalsIgnoreCase(word.toString())) {
                        i++;
                    } else {
                        index2 = i;
                        break;
                    }
                }
            }
            if (!this.mInputFieldInfo.isUDBSubstitutionField() && !this.mInputFieldInfo.isPasswordField()) {
                this.mJapaneseInput.wordSelected(index2, false);
            }
        } else if (!this.mKeyboardSwitcher.isKeypadInput() && index >= 0 && this.mInlineWord.length() > 0 && isLetterAndDigit(this.mInlineWord) && this.mInlineWord.toString().equalsIgnoreCase(word.toString())) {
            if (!this.mInputFieldInfo.isUDBSubstitutionField() && !this.mInputFieldInfo.isPasswordField()) {
                this.mJapaneseInput.wordSelected(index, false);
            }
        } else if (index >= 0) {
            int index3 = 255;
            if (this.mWordListBak != null) {
                int i2 = 0;
                while (true) {
                    if (i2 >= this.mWordListBak.size()) {
                        break;
                    }
                    if (!this.mWordListBak.get(i2).toString().equalsIgnoreCase(word.toString())) {
                        i2++;
                    } else {
                        index3 = i2;
                        break;
                    }
                }
            }
            if (index3 != 255) {
                this.mJapaneseInput.wordSelected(index3, false);
            }
        } else {
            int index4 = 255;
            int i3 = 0;
            while (true) {
                if (i3 >= this.mWordList.size()) {
                    break;
                }
                if (!this.mWordList.get(i3).toString().equalsIgnoreCase(word.toString())) {
                    i3++;
                } else {
                    index4 = i3;
                    break;
                }
            }
            if (index4 != 255 && !this.mInputFieldInfo.isUDBSubstitutionField() && !this.mInputFieldInfo.isPasswordField()) {
                this.mJapaneseInput.wordSelected(index4, false);
            }
        }
        this.mLastInput = 4;
        ShowNextWordPrediction(word.toString(), word.toString());
        clearAllKeys();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.InputView
    public void changeKeyboardMode() {
        flushCurrWordAndClearWCL();
        clearAllKeys();
        clearSuggestions();
        if (this.mKeyboardSwitcher.isEditMode() || this.mKeyboardSwitcher.isNumMode()) {
            if (this.mInputFieldInfo != null) {
                String inputMode = this.mKeyboardSwitcher.getCurrentInputMode();
                this.mKeyboardSwitcher.createKeyboardForTextInput(KeyboardEx.KeyboardLayerType.KEYBOARD_TEXT, this.mInputFieldInfo, this.mCurrentInputLanguage.getInputMode(inputMode));
                setShiftState(Shift.ShiftState.OFF);
                return;
            }
            return;
        }
        this.mKeyboardSwitcher.toggleSymbolKeyboard();
        setMultitapOrAmbigMode();
        setShiftState(Shift.ShiftState.OFF);
    }

    @Override // com.nuance.swype.input.InputView
    public void setKeyboardLayer(KeyboardEx.KeyboardLayerType keyboardLayer) {
        if (getKeyboardLayer() != keyboardLayer) {
            super.setKeyboardLayer(keyboardLayer);
            this.mEditState.startSession();
            dismissPopupKeyboard();
            flushCurrentActiveWord();
            clearSuggestions();
            this.mKeyboardSwitcher.createKeyboardForTextInput(keyboardLayer, this.mInputFieldInfo, this.mCurrentInputLanguage.getCurrentInputMode());
            setShiftState(Shift.ShiftState.OFF);
        }
    }

    @Override // com.nuance.swype.input.InputView
    public void handleMultitapToggle() {
        if (this.mKeyboardInputSuggestionOn) {
            toggleAmbigMode();
            String inputMode = this.mKeyboardSwitcher.getCurrentInputMode();
            setCorrectionLevel(inputMode);
            AppPreferences.from(getContext()).setMultitapMode(inputMode.equals(InputMethods.MULTITAP_INPUT_MODE));
            if (this.mSuggestedWord.length() > 0) {
                String selectedWord = this.mSuggestedWord.toString();
                selectWord(this.mSuggestedWordIndex.get(), selectedWord, null);
            }
            this.mIme.refreshLanguageOnSpaceKey(getKeyboard(), this);
        }
    }

    @Override // com.nuance.swype.input.InputView
    public void onMultitapTimeout() {
        if (isKeypadInput() && !this.mInMultiTap) {
            if (this.mInlineWord != null && this.mInlineWord.length() > 0) {
                this.mInlineWord.removeSpan(this.mBKMultiptappingCharSpan);
                this.mInlineWord.removeSpan(this.mFGMultiptappingCharSpan);
                InputConnection ic = getCurrentInputConnection();
                if (ic != null) {
                    ic.setComposingText(this.mInlineWord, 1);
                    return;
                }
                return;
            }
            return;
        }
        if (this.mJapaneseInput == null || !this.mTextInputPredictionOn) {
            InputConnection ic2 = getCurrentInputConnection();
            if (ic2 != null) {
                if (this.mInlineWord.length() > 0) {
                    this.mInlineWord.clearSpans();
                    ic2.beginBatchEdit();
                    ic2.commitText(this.mInlineWord, 1);
                    ic2.endBatchEdit();
                    this.mInlineWord.clear();
                }
            } else {
                return;
            }
        } else {
            this.mInlineWord.removeSpan(this.mBKMultiptappingCharSpan);
            this.mInlineWord.removeSpan(this.mFGMultiptappingCharSpan);
            InputConnection ic3 = getCurrentInputConnection();
            if (ic3 != null) {
                if (this.mInlineWord.length() > 0) {
                    ic3.setComposingText(this.mInlineWord, 1);
                }
                if (this.mSuggestedWord.length() > 0) {
                    char lastSymbol = this.mSuggestedWord.charAt(this.mSuggestedWord.length() - 1);
                    if ((isWordAcceptingSymbol(lastSymbol) || (this.mSuggestedWord.length() == 1 && this.charUtils.isPunctuationOrSymbol(lastSymbol))) && this.mInlineWord.length() > 0 && this.mInlineWord.charAt(this.mInlineWord.length() - 1) == lastSymbol) {
                        String selectedWord = this.mSuggestedWord.toString();
                        clearAllKeys();
                        ic3.beginBatchEdit();
                        ic3.commitText(selectedWord, 1);
                        ic3.endBatchEdit();
                        this.mEditState.punctuationOrSymbols();
                    }
                }
            } else {
                return;
            }
        }
        invalidateKeyboardImage();
    }

    private void processSpaceKeyState(InputConnection ic) {
        this.mEditState.spaceKey();
        if (6 == this.mEditState.current() && ic != null) {
            ic.beginBatchEdit();
            ic.endBatchEdit();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isKeypadMultitapInputSupported() {
        return this.mKeyboardLayoutId == 1536 && this.isKeypadMultitapInputSupported;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setMultitapOrAmbigMode() {
        this.mJapaneseInput.setMultiTapInputMode(isKeypadMultitapInputSupported());
    }

    private void setCorrectionLevel(String inputMode) {
        if (this.mKeyboardInputSuggestionOn) {
            if (InputMethods.MULTITAP_INPUT_MODE.equals(inputMode)) {
                this.mJapaneseInput.setAttribute(99, false);
            } else {
                this.mJapaneseInput.setAttribute(101, this.mWordCompletionPoint);
                this.mJapaneseInput.setAttribute(99, this.autoCorrectionEnabled);
            }
        }
    }

    @Override // com.nuance.swype.input.InputView
    public void refreshBTAutoCorrection() {
        super.refreshBTAutoCorrection();
        this.mJapaneseInput.setAttribute(99, this.autoCorrectionEnabled);
    }

    public void toggleAmbigMode() {
        if (this.mKeyboardSwitcher.isKeypadInput()) {
            String currentInputMode = this.mKeyboardSwitcher.getCurrentInputMode();
            boolean notHandwriting = !this.mCurrentInputLanguage.getCurrentInputMode().isHandwriting();
            if (currentInputMode.equals(InputMethods.MULTITAP_INPUT_MODE)) {
                this.mKeyboardSwitcher.createKeyboardForTextInput(this.mInputFieldInfo, this.mCurrentInputLanguage.getInputMode(this.mCurrentInputLanguage.mDefaultInputMode), notHandwriting);
                this.mJapaneseInput.setMultiTapInputMode(false);
            } else {
                this.mKeyboardSwitcher.createKeyboardForTextInput(this.mInputFieldInfo, this.mCurrentInputLanguage.getInputMode(InputMethods.MULTITAP_INPUT_MODE), notHandwriting);
                this.mJapaneseInput.setMultiTapInputMode(true);
            }
        }
    }

    @Override // com.nuance.swype.input.InputView
    public void handleKeyboardResize() {
        timeoutWhenMultitappingCommaOrPeriod();
        super.handleKeyboardResize();
        dismissJapanesePopup();
        clearKeyOffsets();
    }

    @SuppressLint({"PrivateResource"})
    private void handleAutoPunct() {
        InputConnection ic = getCurrentInputConnection();
        if (ic != null) {
            CharSequence autopunct = getContext().getApplicationContext().getResources().getString(R.string.jp_period_symbol);
            ic.beginBatchEdit();
            ic.deleteSurroundingText(1, 0);
            ic.commitText(autopunct, 1);
            ic.endBatchEdit();
            sendKeyChar(' ');
        }
    }

    private void extractWordBeforeCursor(CharSequence seqBeforCusor, StringBuilder word) {
        word.setLength(0);
        if (seqBeforCusor != null && seqBeforCusor.length() > 0) {
            int end = seqBeforCusor.length() - 1;
            while (end >= 0 && CharacterUtilities.isWhiteSpace(seqBeforCusor.charAt(end))) {
                end--;
            }
            if (end >= 0) {
                boolean valideSeq = true;
                int start = end - 1;
                while (true) {
                    if (start < 0 || CharacterUtilities.isWhiteSpace(seqBeforCusor.charAt(start))) {
                        break;
                    }
                    if (CharacterUtilities.isValidAlphabeticChar(seqBeforCusor.charAt(start))) {
                        start--;
                    } else {
                        valideSeq = false;
                        break;
                    }
                }
                if (valideSeq) {
                    word.append(seqBeforCusor.subSequence(start + 1, end + 1));
                }
            }
        }
    }

    private void updateContextBufferIfNecessary(InputConnection ic) {
        CharSequence seqBeforeCursor;
        if (this.mEditState.current() == 0 && (seqBeforeCursor = ic.getTextBeforeCursor(128, 0)) != null && seqBeforeCursor.length() > 0) {
            extractWordBeforeCursor(seqBeforeCursor, this.mContextBuffer);
            if (this.mContextBuffer.length() > 0) {
                this.mJapaneseInput.setContext(this.mContextBuffer.toString().toCharArray());
            } else {
                this.mJapaneseInput.breakContext();
            }
        }
    }

    private void handleExplicitInput(int primaryCode) {
        if (!isConverting() && primaryCode != 0 && getCurrentInputConnection() != null && this.mJapaneseInput != null) {
            showNav(-1);
            log.d("...primaryCode: ", Integer.valueOf(primaryCode), " getKeyCount(): ", Integer.valueOf(this.mJapaneseInput.getKeyCount()));
            log.d("handleExplicitInput...mInlineWord: ", this.mInlineWord.toString());
            if (this.mJapaneseInput.getKeyCount() < 32) {
                if (isKeyboardInput()) {
                    String romajiStr = convertText2Romaji(this.mInlineWord.toString());
                    log.d("handleExplicitInput...romajiStr: ", romajiStr, "length: ", Integer.valueOf(romajiStr.length()));
                    if (romajiStr.length() >= 32) {
                        return;
                    }
                }
                if (isWordAcceptingSymbol((char) primaryCode)) {
                    this.mEditState.characterTyped((char) primaryCode);
                    SpannableStringBuilder newSpanBuffer = new SpannableStringBuilder();
                    char[] char1 = {(char) primaryCode, 0};
                    newSpanBuffer.append((CharSequence) String.valueOf(char1, 0, 1));
                    if (this.mWordList != null) {
                        this.mWordList.clear();
                    } else {
                        this.mWordList = new ArrayList();
                    }
                    this.mWordList.add(newSpanBuffer);
                    int wordCandidatesCount = this.mWordList.size();
                    this.mInlineWord.clear();
                    this.mInlineWord.append(this.mWordList.get(0));
                    this.mSuggestedWordIndex.set(0);
                    this.mSuggestedWord.clear();
                    this.mSuggestedWord.append((CharSequence) this.mInlineWord);
                    if (this.mInlineWord.length() > 0) {
                        this.mInlineWord.setSpan(this.mWordComposeSpan, 0, this.mInlineWord.length(), R.styleable.ThemeTemplate_emojiCategoryIconSmiles);
                        if (this.mJapaneseInput.getKeyCount() == 0) {
                            this.mInlineWord.setSpan(this.mBKNextWordPredictionSpan, 0, this.mInlineWord.length(), R.styleable.ThemeTemplate_emojiCategoryIconSmiles);
                            this.mInlineWord.setSpan(this.mFGNextWordPredictionSpan, 0, this.mInlineWord.length(), R.styleable.ThemeTemplate_emojiCategoryIconSmiles);
                        } else if (wordCandidatesCount > 0 && !this.mJapaneseInput.isInlineKnown() && this.mSuggestedWordIndex.get() != 0) {
                            this.mInlineWord.setSpan(this.mBKWordErrorSpan, 0, this.mInlineWord.length(), R.styleable.ThemeTemplate_emojiCategoryIconSmiles);
                            this.mInlineWord.setSpan(this.mFGWordErrorSpan, 0, this.mInlineWord.length(), R.styleable.ThemeTemplate_emojiCategoryIconSmiles);
                        }
                        if (isMultitapping()) {
                            this.mInlineWord.setSpan(this.mBKMultiptappingCharSpan, this.mInlineWord.length() - 1, this.mInlineWord.length(), R.styleable.ThemeTemplate_emojiCategoryIconSmiles);
                            this.mInlineWord.setSpan(this.mFGMultiptappingCharSpan, this.mInlineWord.length() - 1, this.mInlineWord.length(), R.styleable.ThemeTemplate_emojiCategoryIconSmiles);
                        }
                        this.JpInputViewHandler.removeMessages(21);
                        this.JpInputViewHandler.sendEmptyMessageDelayed(21, 10L);
                        this.mUpdatingInline = true;
                        InputConnection ic = getCurrentInputConnection();
                        if (ic != null) {
                            ic.setComposingText(this.mInlineWord, 1);
                        }
                        if (this.mInlineWord.toString().compareTo("ActivateKlingonMode") == 0) {
                            Toast.makeText(getContext(), "QoS 'oH 'oHbe' chenmoHta'", 1).show();
                        }
                    }
                    if (!isCommaOrPeriod(primaryCode)) {
                        updateCandidates(Candidates.Source.INVALID);
                        return;
                    }
                    return;
                }
                if (this.mJapaneseInput.getKeyCount() + 1 <= 32 && addExplicit(primaryCode, getShiftState())) {
                    this.mEditState.characterTyped((char) primaryCode);
                    updateCandidates(Candidates.Source.INVALID);
                }
            }
        }
    }

    private boolean addExplicit(int keyCode, Shift.ShiftState shiftState) {
        return this.inputContextRequestHandler.addExplicitSymbol(new char[]{(char) keyCode}, shiftState);
    }

    private void handleWhiteSpaces(int primaryCode) {
        if (primaryCode == 32) {
            handleSpace(false, 1);
        } else {
            handlePunctOrSymbol(primaryCode);
        }
    }

    private boolean timeoutWhenMultitappingCommaOrPeriod() {
        if (!this.mKeyboardSwitcher.isKeypadInput() || !isMultitapping() || this.mInlineWord.length() != 1 || !isCommaOrPeriod(this.mInlineWord.charAt(this.mInlineWord.length() - 1))) {
            return false;
        }
        this.mJapaneseInput.multiTapTimeOut();
        onMultitapTimeout();
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isCommaOrPeriod(int code) {
        return code == 12289 || code == 12290;
    }

    private void clearCurrentInline(InputConnection ic) {
        if (this.mInlineWord != null && this.mInlineWord.length() > 0) {
            this.mInlineWord.clear();
            this.mInlineWord.clearSpans();
            if (ic != null) {
                ic.beginBatchEdit();
                ic.setComposingText("", 0);
                ic.endBatchEdit();
            }
        }
    }

    private void handlePunctOrSymbol(int primaryCode) {
        if (primaryCode == 32) {
            handleSpace(false, 1);
            return;
        }
        if (primaryCode == 10 && this.mJapaneseInput != null && (hasActiveKeySeq() || this.mInlineWord.length() != 0)) {
            this.mJapaneseInput.setContext(null);
        }
        if (this.mJapaneseInput != null && (composingState() || this.mInlineWord.length() != 0)) {
            String selectedWord = this.mInlineWord.toString();
            if (isConverting()) {
                if (isFullScreenMode()) {
                    cleanInlineWordSpanAndFlush();
                }
                flushCurrWordAndClearWCL();
            } else {
                selectWord(-1, selectedWord, null);
            }
        } else {
            clearCurrentInline(getCurrentInputConnection());
        }
        this.mEditState.punctuationOrSymbols();
        this.mConvert = 0;
        sendKeyChar((char) primaryCode);
        this.mLastInput = 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int updateCandidates(Candidates.Source source) {
        if (this.mJapaneseInput == null || this.candidatesListViewCJK == null || !isValidBuild()) {
            return 0;
        }
        this.mSuggestedWord.clear();
        this.mSuggestedWord.clearSpans();
        this.mInlineWord.clear();
        this.mInlineWord.clearSpans();
        if (source == Candidates.Source.TRACE) {
            Candidates candidates = this.mJapaneseInput.getCandidates();
            if (handleGesture(candidates)) {
                this.mJapaneseInput.clearAllKeys();
                return -1;
            }
            if (this.mInputFieldInfo != null && this.mInputFieldInfo.isPasswordField() && this.mKeyboardSwitcher != null && !this.mKeyboardSwitcher.isKeypadInput()) {
                return 0;
            }
            this.mWordList = getWords(this.mSuggestedWord, this.mSuggestedWordIndex);
        } else {
            this.mWordList = getWords(this.mSuggestedWord, this.mSuggestedWordIndex);
        }
        if (this.mWordListBak == null) {
            this.mWordListBak = new ArrayList();
        } else {
            this.mWordListBak.clear();
        }
        for (CharSequence item : this.mWordList) {
            this.mWordListBak.add(item);
        }
        int wordCandidatesCount = this.mWordList.size();
        SpannableStringBuilder inlineText = new SpannableStringBuilder();
        this.mJapaneseInput.getInlineText(inlineText);
        this.mInlineWord.clear();
        this.mInlineWord.append((CharSequence) inlineText);
        updateRomajiInlineDivisionInfo();
        this.mEditState.composeWordCandidate();
        if (this.mInlineWord.length() > 0) {
            if (isKeypadInput()) {
                addDigitsFromUILayer(this.mInlineWord.toString());
            }
            if (wordCandidatesCount > 0) {
                addExactEnWordsFromUILayer();
            }
            wordCandidatesCount = this.mWordList.size();
            this.mInlineWord.setSpan(this.mWordComposeSpan, 0, this.mInlineWord.length(), R.styleable.ThemeTemplate_emojiCategoryIconSmiles);
            if (this.mJapaneseInput.getKeyCount() == 0) {
                this.mInlineWord.setSpan(this.mBKNextWordPredictionSpan, 0, this.mInlineWord.length(), R.styleable.ThemeTemplate_emojiCategoryIconSmiles);
                this.mInlineWord.setSpan(this.mFGNextWordPredictionSpan, 0, this.mInlineWord.length(), R.styleable.ThemeTemplate_emojiCategoryIconSmiles);
            } else if (wordCandidatesCount > 0 && !this.mJapaneseInput.isInlineKnown() && this.mSuggestedWordIndex.get() != 0) {
                this.mInlineWord.setSpan(this.mBKWordErrorSpan, 0, this.mInlineWord.length(), R.styleable.ThemeTemplate_emojiCategoryIconSmiles);
                this.mInlineWord.setSpan(this.mFGWordErrorSpan, 0, this.mInlineWord.length(), R.styleable.ThemeTemplate_emojiCategoryIconSmiles);
            }
            if (isMultitapping()) {
                this.mInlineWord.setSpan(this.mBKMultiptappingCharSpan, this.mInlineWord.length() - 1, this.mInlineWord.length(), R.styleable.ThemeTemplate_emojiCategoryIconSmiles);
                this.mInlineWord.setSpan(this.mFGMultiptappingCharSpan, this.mInlineWord.length() - 1, this.mInlineWord.length(), R.styleable.ThemeTemplate_emojiCategoryIconSmiles);
            }
            this.JpInputViewHandler.removeMessages(21);
            this.JpInputViewHandler.sendEmptyMessageDelayed(21, 10L);
            this.mUpdatingInline = true;
            InputConnection ic = getCurrentInputConnection();
            if (ic != null) {
                ic.beginBatchEdit();
                InputConnectionUtils.setComposingText(ic, this.mInlineWord);
                ic.endBatchEdit();
            }
        }
        if (wordCandidatesCount > 0) {
            this.mUpdatingInline = true;
            updateWordListView(this.mWordList, this.mSuggestedWordIndex.get(), false);
            return wordCandidatesCount;
        }
        syncCandidateDisplayStyleToMode();
        return wordCandidatesCount;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.InputView
    public void clearAllKeys() {
        if (this.mInlineWord != null) {
            this.mInlineWord.clearSpans();
            this.mInlineWord.clear();
        }
        if (this.mSuggestedWord != null) {
            this.mSuggestedWord.clearSpans();
            this.mSuggestedWord.clear();
            this.mSuggestedWordIndex.set(-1);
        }
        if (this.mJapaneseInput != null) {
            this.mJapaneseInput.clearAllKeys();
        }
        this.mLastInput = 0;
    }

    @Override // com.nuance.swype.input.InputView
    public void handleShiftKey() {
        this.mKeyboardSwitcher.cycleShiftState();
        invalidateKeyboardImage();
    }

    @SuppressLint({"PrivateResource"})
    private void loadSettings() {
        int layoutId;
        boolean z = false;
        if (UserPreferences.from(getContext()).getAutoCorrection() && this.mInputFieldInfo.isNameField()) {
            this.autoCorrectionEnabled = true;
        }
        this.mTraceInputSuggestionOn = this.mCurrentInputLanguage.getCurrentInputMode().isTraceEnabled() && this.mInputFieldInfo.textInputFieldWithSuggestionEnabled();
        this.mRecaptureOn = this.mCurrentInputLanguage.getCurrentInputMode().isRecaptureEnabled() && this.mTextInputPredictionOn;
        this.mNextWordPredictionOn = this.mCurrentInputLanguage.getCurrentInputMode().isNextWordPredictionEnabled() && this.mTextInputPredictionOn;
        this.portraitInputOptions = this.mCurrentInputLanguage.getCurrentInputMode().getDefaultPortraitLayoutOptions();
        if ((this.portraitInputOptions & 16) == 0) {
            layoutId = InputMethods.QWERTY_KEYBOARD_LAYOUT;
        } else {
            layoutId = InputMethods.KEYPAD_KEYBOARD_LAYOUT;
        }
        if (getResources().getConfiguration().orientation == 1 && this.mKeyboardLayoutId != layoutId) {
            this.mCurrentInputLanguage.getCurrentInputMode().getNextLayout();
            this.mKeyboardLayoutId = layoutId;
        }
        this.isKeypadMultitapInputSupported = (this.portraitInputOptions == 0 || this.portraitInputOptions == 17) ? false : true;
        if (this.portraitInputOptions != 0 && this.portraitInputOptions != 16) {
            z = true;
        }
        this.isKeypadFlickrInputSupported = z;
        this.mNavOn = true;
        int flickerKeyWidth = getResources().getDimensionPixelSize(R.dimen.key_height_5row);
        this.flickerMiddleKeyRadius = flickerKeyWidth / KEYPAD_FLICKER_RADIUS_PARAM;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public JapaneseInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mRows = new ArrayList();
        this.mWordComposeSpan = new UnderlineSpan();
        this.mExactWordSpan = new StyleSpan(2);
        this.mContextBuffer = new StringBuilder();
        this.mInlineWord = new SpannableStringBuilder();
        this.mSuggestedWord = new SpannableStringBuilder();
        this.mSuggestedWordIndex = new AtomicInteger();
        this.isKeypadMultitapInputSupported = true;
        this.isKeypadFlickrInputSupported = true;
        this.mScrollable = false;
        this.mConfirmedInlineRomaji = new StringBuilder();
        this.mConfirmedKanji = new StringBuilder();
        this.mRCReadingDivInfo = new int[R.styleable.ThemeTemplate_chineseFunctionBarEmoji];
        this.mRCOutPhraseDivInfo = new int[R.styleable.ThemeTemplate_chineseFunctionBarEmoji];
        this.mConvert = 0;
        this.mCurrentKeyNav = -1;
        this.japaneseFlickerKeyIndex = -1;
        this.japaneseFlickerKeyRelativeIndex = -1;
        this.isTopKeyFlicerUp = false;
        this.covertingYomi = new StringBuilder();
        this.covertingMidashigo = new StringBuilder();
        this.mRomajiDivInfoOfInline = new int[R.styleable.ThemeTemplate_chineseFunctionBarEmoji];
        this.mKanaDivInfoOfInline = new int[R.styleable.ThemeTemplate_chineseFunctionBarEmoji];
        this.JpInputViewCallback = new Handler.Callback() { // from class: com.nuance.swype.input.japanese.JapaneseInputView.1
            /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
            /* JADX WARN: Code restructure failed: missing block: B:3:0x0007, code lost:            return true;     */
            @Override // android.os.Handler.Callback
            @android.annotation.SuppressLint({"PrivateResource"})
            /*
                Code decompiled incorrectly, please refer to instructions dump.
                To view partially-correct code enable 'Show inconsistent code' option in preferences
            */
            public boolean handleMessage(android.os.Message r6) {
                /*
                    r5 = this;
                    r4 = 1
                    r1 = 0
                    int r0 = r6.what
                    switch(r0) {
                        case 5: goto L8;
                        case 11: goto L33;
                        case 15: goto L53;
                        case 21: goto L2d;
                        case 22: goto L71;
                        case 23: goto L77;
                        case 125: goto L7d;
                        default: goto L7;
                    }
                L7:
                    return r4
                L8:
                    com.nuance.swype.input.japanese.JapaneseInputView r0 = com.nuance.swype.input.japanese.JapaneseInputView.this
                    android.content.Context r0 = r0.getContext()
                    com.nuance.swype.input.japanese.JapaneseInputView r1 = com.nuance.swype.input.japanese.JapaneseInputView.this
                    android.content.res.Resources r1 = r1.getResources()
                    int r2 = com.nuance.swype.input.R.string.multitap_toggle_tip
                    java.lang.String r1 = r1.getString(r2)
                    com.nuance.swype.input.japanese.JapaneseInputView r2 = com.nuance.swype.input.japanese.JapaneseInputView.this
                    int r2 = r2.getHeight()
                    com.nuance.swype.input.japanese.JapaneseInputView r3 = com.nuance.swype.input.japanese.JapaneseInputView.this
                    com.nuance.swype.input.chinese.CJKCandidatesListView r3 = r3.candidatesListViewCJK
                    int r3 = r3.getHeight()
                    int r2 = r2 + r3
                    com.nuance.swype.input.QuickToast.show(r0, r1, r4, r2)
                    goto L7
                L2d:
                    com.nuance.swype.input.japanese.JapaneseInputView r0 = com.nuance.swype.input.japanese.JapaneseInputView.this
                    com.nuance.swype.input.japanese.JapaneseInputView.access$002(r0, r1)
                    goto L7
                L33:
                    com.nuance.swype.input.japanese.JapaneseInputView r0 = com.nuance.swype.input.japanese.JapaneseInputView.this
                    com.nuance.swype.input.SpeechWrapper r0 = r0.mSpeechWrapper
                    if (r0 == 0) goto L48
                    com.nuance.swype.input.japanese.JapaneseInputView r0 = com.nuance.swype.input.japanese.JapaneseInputView.this
                    com.nuance.swype.input.SpeechWrapper r0 = r0.mSpeechWrapper
                    boolean r0 = r0.isResumable()
                    if (r0 == 0) goto L48
                    com.nuance.swype.input.japanese.JapaneseInputView r0 = com.nuance.swype.input.japanese.JapaneseInputView.this
                    r0.flushCurrentActiveWord()
                L48:
                    com.nuance.swype.input.japanese.JapaneseInputView r0 = com.nuance.swype.input.japanese.JapaneseInputView.this
                    com.nuance.swype.input.japanese.JapaneseInputView.access$100(r0)
                    com.nuance.swype.input.japanese.JapaneseInputView r0 = com.nuance.swype.input.japanese.JapaneseInputView.this
                    com.nuance.swype.input.japanese.JapaneseInputView.access$200(r0)
                    goto L7
                L53:
                    com.nuance.swype.input.japanese.JapaneseInputView r0 = com.nuance.swype.input.japanese.JapaneseInputView.this
                    com.nuance.swype.input.japanese.JapaneseInputView.access$300(r0)
                    com.nuance.swype.input.japanese.JapaneseInputView r0 = com.nuance.swype.input.japanese.JapaneseInputView.this
                    boolean r0 = com.nuance.swype.input.japanese.JapaneseInputView.access$400(r0)
                    if (r0 != 0) goto L7
                    com.nuance.swype.input.japanese.JapaneseInputView r0 = com.nuance.swype.input.japanese.JapaneseInputView.this
                    com.nuance.swype.input.KeyboardSwitcher r0 = com.nuance.swype.input.japanese.JapaneseInputView.access$500(r0)
                    com.nuance.input.swypecorelib.Shift$ShiftState r1 = com.nuance.input.swypecorelib.Shift.ShiftState.OFF
                    r0.setShiftState(r1)
                    com.nuance.swype.input.japanese.JapaneseInputView r0 = com.nuance.swype.input.japanese.JapaneseInputView.this
                    com.nuance.swype.input.japanese.JapaneseInputView.access$600(r0)
                    goto L7
                L71:
                    com.nuance.swype.input.japanese.JapaneseInputView r0 = com.nuance.swype.input.japanese.JapaneseInputView.this
                    r0.updateSegemetationPopup(r1, r1)
                    goto L7
                L77:
                    com.nuance.swype.input.japanese.JapaneseInputView r0 = com.nuance.swype.input.japanese.JapaneseInputView.this
                    r0.showSegmentationPopup()
                    goto L7
                L7d:
                    com.nuance.swype.input.japanese.JapaneseInputView r0 = com.nuance.swype.input.japanese.JapaneseInputView.this
                    com.nuance.swype.input.InputView$WclPrompt r0 = com.nuance.swype.input.japanese.JapaneseInputView.access$700(r0)
                    java.lang.String r1 = "CJK"
                    r0.showMessage(r1)
                    goto L7
                */
                throw new UnsupportedOperationException("Method not decompiled: com.nuance.swype.input.japanese.JapaneseInputView.AnonymousClass1.handleMessage(android.os.Message):boolean");
            }
        };
        this.JpInputViewHandler = WeakReferenceHandler.create(this.JpInputViewCallback);
        this.inputContextRequestHandler = new InputContextRequestHandler();
        this.touchKeyActionHandler = new TouchKeyActionHandler();
        this.mRCOutPhraseDivInfo[0] = -1;
        this.mRCReadingDivInfo[0] = -1;
        prepareFlickerPopup();
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public JapaneseInputView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mRows = new ArrayList();
        this.mWordComposeSpan = new UnderlineSpan();
        this.mExactWordSpan = new StyleSpan(2);
        this.mContextBuffer = new StringBuilder();
        this.mInlineWord = new SpannableStringBuilder();
        this.mSuggestedWord = new SpannableStringBuilder();
        this.mSuggestedWordIndex = new AtomicInteger();
        this.isKeypadMultitapInputSupported = true;
        this.isKeypadFlickrInputSupported = true;
        this.mScrollable = false;
        this.mConfirmedInlineRomaji = new StringBuilder();
        this.mConfirmedKanji = new StringBuilder();
        this.mRCReadingDivInfo = new int[R.styleable.ThemeTemplate_chineseFunctionBarEmoji];
        this.mRCOutPhraseDivInfo = new int[R.styleable.ThemeTemplate_chineseFunctionBarEmoji];
        this.mConvert = 0;
        this.mCurrentKeyNav = -1;
        this.japaneseFlickerKeyIndex = -1;
        this.japaneseFlickerKeyRelativeIndex = -1;
        this.isTopKeyFlicerUp = false;
        this.covertingYomi = new StringBuilder();
        this.covertingMidashigo = new StringBuilder();
        this.mRomajiDivInfoOfInline = new int[R.styleable.ThemeTemplate_chineseFunctionBarEmoji];
        this.mKanaDivInfoOfInline = new int[R.styleable.ThemeTemplate_chineseFunctionBarEmoji];
        this.JpInputViewCallback = new Handler.Callback() { // from class: com.nuance.swype.input.japanese.JapaneseInputView.1
            @Override // android.os.Handler.Callback
            @SuppressLint({"PrivateResource"})
            public boolean handleMessage(Message message) {
                /*  JADX ERROR: Method code generation error
                    java.lang.NullPointerException: Cannot invoke "jadx.core.dex.nodes.IContainer.get(jadx.api.plugins.input.data.attributes.IJadxAttrType)" because "cont" is null
                    	at jadx.core.codegen.RegionGen.declareVars(RegionGen.java:70)
                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:65)
                    	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:297)
                    	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:276)
                    	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:406)
                    	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:335)
                    	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$3(ClassGen.java:301)
                    	at java.base/java.util.stream.ForEachOps$ForEachOp$OfRef.accept(Unknown Source)
                    	at java.base/java.util.ArrayList.forEach(Unknown Source)
                    	at java.base/java.util.stream.SortedOps$RefSortingSink.end(Unknown Source)
                    	at java.base/java.util.stream.Sink$ChainedReference.end(Unknown Source)
                    */
                /*
                    this = this;
                    r4 = 1
                    r1 = 0
                    int r0 = r6.what
                    switch(r0) {
                        case 5: goto L8;
                        case 11: goto L33;
                        case 15: goto L53;
                        case 21: goto L2d;
                        case 22: goto L71;
                        case 23: goto L77;
                        case 125: goto L7d;
                        default: goto L7;
                    }
                L7:
                    return r4
                L8:
                    com.nuance.swype.input.japanese.JapaneseInputView r0 = com.nuance.swype.input.japanese.JapaneseInputView.this
                    android.content.Context r0 = r0.getContext()
                    com.nuance.swype.input.japanese.JapaneseInputView r1 = com.nuance.swype.input.japanese.JapaneseInputView.this
                    android.content.res.Resources r1 = r1.getResources()
                    int r2 = com.nuance.swype.input.R.string.multitap_toggle_tip
                    java.lang.String r1 = r1.getString(r2)
                    com.nuance.swype.input.japanese.JapaneseInputView r2 = com.nuance.swype.input.japanese.JapaneseInputView.this
                    int r2 = r2.getHeight()
                    com.nuance.swype.input.japanese.JapaneseInputView r3 = com.nuance.swype.input.japanese.JapaneseInputView.this
                    com.nuance.swype.input.chinese.CJKCandidatesListView r3 = r3.candidatesListViewCJK
                    int r3 = r3.getHeight()
                    int r2 = r2 + r3
                    com.nuance.swype.input.QuickToast.show(r0, r1, r4, r2)
                    goto L7
                L2d:
                    com.nuance.swype.input.japanese.JapaneseInputView r0 = com.nuance.swype.input.japanese.JapaneseInputView.this
                    com.nuance.swype.input.japanese.JapaneseInputView.access$002(r0, r1)
                    goto L7
                L33:
                    com.nuance.swype.input.japanese.JapaneseInputView r0 = com.nuance.swype.input.japanese.JapaneseInputView.this
                    com.nuance.swype.input.SpeechWrapper r0 = r0.mSpeechWrapper
                    if (r0 == 0) goto L48
                    com.nuance.swype.input.japanese.JapaneseInputView r0 = com.nuance.swype.input.japanese.JapaneseInputView.this
                    com.nuance.swype.input.SpeechWrapper r0 = r0.mSpeechWrapper
                    boolean r0 = r0.isResumable()
                    if (r0 == 0) goto L48
                    com.nuance.swype.input.japanese.JapaneseInputView r0 = com.nuance.swype.input.japanese.JapaneseInputView.this
                    r0.flushCurrentActiveWord()
                L48:
                    com.nuance.swype.input.japanese.JapaneseInputView r0 = com.nuance.swype.input.japanese.JapaneseInputView.this
                    com.nuance.swype.input.japanese.JapaneseInputView.access$100(r0)
                    com.nuance.swype.input.japanese.JapaneseInputView r0 = com.nuance.swype.input.japanese.JapaneseInputView.this
                    com.nuance.swype.input.japanese.JapaneseInputView.access$200(r0)
                    goto L7
                L53:
                    com.nuance.swype.input.japanese.JapaneseInputView r0 = com.nuance.swype.input.japanese.JapaneseInputView.this
                    com.nuance.swype.input.japanese.JapaneseInputView.access$300(r0)
                    com.nuance.swype.input.japanese.JapaneseInputView r0 = com.nuance.swype.input.japanese.JapaneseInputView.this
                    boolean r0 = com.nuance.swype.input.japanese.JapaneseInputView.access$400(r0)
                    if (r0 != 0) goto L7
                    com.nuance.swype.input.japanese.JapaneseInputView r0 = com.nuance.swype.input.japanese.JapaneseInputView.this
                    com.nuance.swype.input.KeyboardSwitcher r0 = com.nuance.swype.input.japanese.JapaneseInputView.access$500(r0)
                    com.nuance.input.swypecorelib.Shift$ShiftState r1 = com.nuance.input.swypecorelib.Shift.ShiftState.OFF
                    r0.setShiftState(r1)
                    com.nuance.swype.input.japanese.JapaneseInputView r0 = com.nuance.swype.input.japanese.JapaneseInputView.this
                    com.nuance.swype.input.japanese.JapaneseInputView.access$600(r0)
                    goto L7
                L71:
                    com.nuance.swype.input.japanese.JapaneseInputView r0 = com.nuance.swype.input.japanese.JapaneseInputView.this
                    r0.updateSegemetationPopup(r1, r1)
                    goto L7
                L77:
                    com.nuance.swype.input.japanese.JapaneseInputView r0 = com.nuance.swype.input.japanese.JapaneseInputView.this
                    r0.showSegmentationPopup()
                    goto L7
                L7d:
                    com.nuance.swype.input.japanese.JapaneseInputView r0 = com.nuance.swype.input.japanese.JapaneseInputView.this
                    com.nuance.swype.input.InputView$WclPrompt r0 = com.nuance.swype.input.japanese.JapaneseInputView.access$700(r0)
                    java.lang.String r1 = "CJK"
                    r0.showMessage(r1)
                    goto L7
                */
                throw new UnsupportedOperationException("Method not decompiled: com.nuance.swype.input.japanese.JapaneseInputView.AnonymousClass1.handleMessage(android.os.Message):boolean");
            }
        };
        this.JpInputViewHandler = WeakReferenceHandler.create(this.JpInputViewCallback);
        this.inputContextRequestHandler = new InputContextRequestHandler();
        this.touchKeyActionHandler = new TouchKeyActionHandler();
        this.mRCOutPhraseDivInfo[0] = -1;
        this.mRCReadingDivInfo[0] = -1;
        prepareFlickerPopup();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.KeyboardViewEx
    public boolean isTraceInputEnabled() {
        return this.isTraceEnabledOnKeyboard;
    }

    @Override // com.nuance.swype.input.InputView
    public void onApplicationUnbind() {
        super.onApplicationUnbind();
        clearSuggestions();
        clearAllKeys();
    }

    @SuppressLint({"PrivateResource"})
    private void readStyles(Context context) {
        TypedArrayWrapper a = IMEApplication.from(context).getThemeLoader().obtainStyledAttributes$6d3b0587(context, null, R.styleable.InlineStringAlpha, 0, R.style.InlineStringAlpha, R.xml.defaults, "InlineStringAlpha");
        int n = a.delegateTypedArray.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.InlineStringAlpha_wordErrorForegroundColor) {
                this.mFGWordErrorSpan = new ForegroundColorSpan(a.getColor(attr, -65536));
            } else if (attr == R.styleable.InlineStringAlpha_wordErrorBackgroundColor) {
                this.mBKWordErrorSpan = new BackgroundColorSpan(a.getColor(attr, 0));
            } else if (attr == R.styleable.InlineStringAlpha_wordNextWordPredictionForegroundColor) {
                this.mFGNextWordPredictionSpan = new ForegroundColorSpan(-3355444);
            } else if (attr == R.styleable.InlineStringAlpha_wordNextWordPredictionBackgroundColor) {
                this.mBKNextWordPredictionSpan = new BackgroundColorSpan(0);
            } else if (attr == R.styleable.InlineStringAlpha_multitapForegroundColor) {
                this.mFGMultiptappingCharSpan = new ForegroundColorSpan(a.getColor(attr, -1));
            } else if (attr == R.styleable.InlineStringAlpha_multitapBackgroundColor) {
                this.mBKMultiptappingCharSpan = new BackgroundColorSpan(a.getColor(attr, -65536));
            }
        }
        a.recycle();
        TypedArrayWrapper a2 = IMEApplication.from(context).getThemeLoader().obtainStyledAttributes$6d3b0587(context, null, R.styleable.WordListView, 0, R.style.WordListView, R.xml.defaults, "WordListView");
        this.mColorRecommended = new ForegroundColorSpan(a2.getColor(R.styleable.WordListView_candidateNormal, -65536));
        a2.recycle();
        TypedArrayWrapper a3 = IMEApplication.from(context).getThemeLoader().obtainStyledAttributes$6d3b0587(context, null, R.styleable.InlineStringJapanese, 0, R.style.InlineStringJapanese, R.xml.defaults, "InlineStringJapanese");
        this.mBKRangeConversionDefaultSpan = new BackgroundColorSpan(a3.getColor(R.styleable.InlineStringJapanese_convertingBackgroundColor, -16711681));
        this.mBKRemainSpan = new BackgroundColorSpan(a3.getColor(R.styleable.InlineStringJapanese_remainingBackgroundColor, -1));
        a3.recycle();
        IMEApplication app = IMEApplication.from(context);
        this.flickerKeyPopupBackground = app.getThemedDrawable(R.attr.keyboardPopupBackgroundFlick);
        this.flickerKeyPopupSelectedBackground = app.getThemedDrawable(R.attr.keyboardKeyFeedbackSelectedBackground);
        Resources resources = context.getResources();
        this.flickerKeyTextSize = resources.getDimensionPixelSize(R.dimen.japanese_flicker_menu_key_text_size);
        this.flickerKeyHeight = resources.getDimensionPixelSize(R.dimen.key_height_5row);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.KeyboardViewEx
    public boolean isSupportMultitouchGesture() {
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.KeyboardViewEx
    public boolean handleScrollLeft() {
        return cycleKeyboard();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.KeyboardViewEx
    public boolean handleScrollRight() {
        return cycleKeyboard();
    }

    private boolean cycleKeyboard() {
        boolean landscape = getResources().getConfiguration().orientation == 2;
        boolean alphaMode = this.mKeyboardSwitcher.isAlphabetMode();
        if (IMEApplication.from(this.mIme).getIMEHandlerManager() == null || landscape || !alphaMode) {
            return false;
        }
        AppPreferences appPrefs = AppPreferences.from(getContext());
        this.portraitInputOptions = this.mCurrentInputLanguage.getCurrentInputMode().getDefaultPortraitLayoutOptions();
        if (isKeypadInput()) {
            appPrefs.setString(this.mCurrentInputLanguage.getCurrentInputMode().getPortaitKeypadOptionsPrefKey(), Integer.toString(this.portraitInputOptions));
            appPrefs.setString(this.mCurrentInputLanguage.getCurrentInputMode().getPortaitLayoutOptionsPrefKey(), Integer.toString(0));
            AppPreferences.from(getContext().getApplicationContext()).setString(this.mCurrentInputLanguage.getCurrentInputMode().getPortaitLayoutOptionsPrefKey(), "0");
        } else {
            int keypadInputOptions = this.mCurrentInputLanguage.getCurrentInputMode().getDefaultPortraitKeypadOptions();
            appPrefs.setString(this.mCurrentInputLanguage.getCurrentInputMode().getPortaitLayoutOptionsPrefKey(), Integer.toString(keypadInputOptions));
            AppPreferences.from(getContext().getApplicationContext()).setString(this.mCurrentInputLanguage.getCurrentInputMode().getPortaitLayoutOptionsPrefKey(), String.valueOf(keypadInputOptions));
        }
        this.mIme.handlerManager.toggleKeyboard();
        return true;
    }

    @Override // com.nuance.swype.input.chinese.CJKCandidatesListView.OnWordSelectActionListener
    public boolean isScrollable(CJKCandidatesListView aSource) {
        return this.mScrollable;
    }

    public void setScrollable(boolean val) {
        this.mScrollable = val;
    }

    @Override // com.nuance.swype.input.chinese.CJKCandidatesListView.OnWordSelectActionListener
    public void nextBtnPressed(CJKCandidatesListView aSource) {
        List<CharSequence> suggestions;
        dismissPopupKeyboard();
        if (aSource == this.candidatesListViewCJK) {
            if (this.candidatesListViewCJK != null && this.candidatesListViewCJK.wordCount() > 0) {
                if (this.mIme.isHardKeyboardActive()) {
                    setScrollable(true);
                    this.candidatesListViewCJK.scrollNext();
                    setScrollable(false);
                    return;
                }
                showGridCandidatesView(this.candidatesListViewCJK.suggestions());
                return;
            }
            if (this.mCompletionOn && this.mCompletions.size() > 0 && (suggestions = this.candidatesListViewCJK.suggestions()) != null && !suggestions.isEmpty()) {
                showGridCandidatesView(suggestions);
            }
        }
    }

    @Override // com.nuance.swype.input.chinese.CJKCandidatesListView.OnWordSelectActionListener
    public void prevBtnPressed(CJKCandidatesListView aSource) {
        List<CharSequence> suggestions;
        if (aSource == this.candidatesListViewCJK) {
            if (this.candidatesListViewCJK != null && this.candidatesListViewCJK.wordCount() > 0) {
                if (this.mIme.isHardKeyboardActive()) {
                    setScrollable(true);
                    this.candidatesListViewCJK.scrollPrev();
                    setScrollable(false);
                    return;
                }
                showGridCandidatesView(this.candidatesListViewCJK.suggestions());
                return;
            }
            if (this.mCompletionOn && this.mCompletions.size() > 0 && (suggestions = this.candidatesListViewCJK.suggestions()) != null && !suggestions.isEmpty()) {
                showGridCandidatesView(suggestions);
            }
        }
    }

    private boolean isSegmentationPopupShowing() {
        return this.segmentationPopup != null && this.segmentationPopup.isShowing();
    }

    @SuppressLint({"InflateParams", "ClickableViewAccessibility", "PrivateResource"})
    private void showGridCandidatesView(List<CharSequence> aDataList) {
        if (aDataList != null && !aDataList.isEmpty() && !this.mContextWindowShowing && !super.isSpeechViewShowing()) {
            if (isSegmentationPopupShowing()) {
                this.isSegmentationPopupNeeded = true;
                dismissSegmentationPopup();
            }
            int height = getHeight() + this.mJapaneseWordListViewContainer.getHeight();
            int width = getKeyboard().getMinWidth();
            this.mJpWordPageContainer.setMinimumHeight(height);
            setCandidatesViewShown(false);
            if (this.candidatesPopup != null && this.candidatesPopup.getMeasuredWidth() != width) {
                log.d("recreate candidatesPopup....candidatesPopup.getMeasuredWidth(): ", Integer.valueOf(this.candidatesPopup.getMeasuredWidth()), "keyboard width: ", Integer.valueOf(width));
                this.candidatesPopup = null;
            }
            if (this.candidatesPopup == null) {
                LayoutInflater inflater = IMEApplication.from(getContext()).getThemedLayoutInflater(LayoutInflater.from(getContext()));
                IMEApplication.from(getContext()).getThemeLoader().setLayoutInflaterFactory(inflater);
                this.candidatesPopup = inflater.inflate(R.layout.candidates_popup, (ViewGroup) null);
                IMEApplication.from(getContext()).getThemeLoader().applyTheme(this.candidatesPopup);
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(-2, -2);
                this.candidatesPopup.setLayoutParams(lp);
                int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(width, Integers.STATUS_SUCCESS);
                int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(height, Integers.STATUS_SUCCESS);
                this.candidatesPopup.measure(widthMeasureSpec, heightMeasureSpec);
                ImageButton closeButton = (ImageButton) this.candidatesPopup.findViewById(R.id.closeButton);
                closeButton.setImageDrawable(getResources().getDrawable(R.drawable.keyboard_popup_btn_close_cjk));
                closeButton.setOnClickListener(new View.OnClickListener() { // from class: com.nuance.swype.input.japanese.JapaneseInputView.2
                    @Override // android.view.View.OnClickListener
                    public void onClick(View v) {
                        JapaneseInputView.this.hideGridCandidatesView();
                        JapaneseInputView.this.setCandidatesViewShown(true);
                        if (JapaneseInputView.this.isSegmentationPopupNeeded) {
                            JapaneseInputView.this.postDelayShowSegmentationPopupMessage();
                        }
                    }
                });
                closeButton.setOnTouchListener(new View.OnTouchListener() { // from class: com.nuance.swype.input.japanese.JapaneseInputView.3
                    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
                    /* JADX WARN: Code restructure failed: missing block: B:3:0x0008, code lost:            return false;     */
                    @Override // android.view.View.OnTouchListener
                    /*
                        Code decompiled incorrectly, please refer to instructions dump.
                        To view partially-correct code enable 'Show inconsistent code' option in preferences
                    */
                    public boolean onTouch(android.view.View r4, android.view.MotionEvent r5) {
                        /*
                            r3 = this;
                            r2 = 0
                            int r0 = r5.getAction()
                            switch(r0) {
                                case 0: goto L9;
                                case 1: goto L10;
                                case 2: goto L9;
                                case 3: goto L10;
                                default: goto L8;
                            }
                        L8:
                            return r2
                        L9:
                            com.nuance.swype.input.japanese.JapaneseInputView r0 = com.nuance.swype.input.japanese.JapaneseInputView.this
                            r1 = 1
                            com.nuance.swype.input.japanese.JapaneseInputView.access$1402(r0, r1)
                            goto L8
                        L10:
                            com.nuance.swype.input.japanese.JapaneseInputView r0 = com.nuance.swype.input.japanese.JapaneseInputView.this
                            com.nuance.swype.input.japanese.JapaneseInputView.access$1402(r0, r2)
                            goto L8
                        */
                        throw new UnsupportedOperationException("Method not decompiled: com.nuance.swype.input.japanese.JapaneseInputView.AnonymousClass3.onTouch(android.view.View, android.view.MotionEvent):boolean");
                    }
                });
            }
            ScrollView scrollView = (ScrollView) this.candidatesPopup.findViewById(R.id.scroll_view);
            scrollView.scrollTo(0, 0);
            KeyboardViewEx keyboardViewEx = (KeyboardViewEx) this.candidatesPopup.findViewById(R.id.keyboardViewEx);
            setGridCandidates(aDataList, scrollView.getMeasuredWidth());
            final KeyboardEx keyboard = new KeyboardEx(getContext(), R.xml.kbd_chinese_popup_template, this.mRows, scrollView.getMeasuredWidth(), height);
            keyboardViewEx.setKeyboard(keyboard);
            keyboardViewEx.setDoubleBuffered(false);
            keyboardViewEx.setContextCandidatesView(true);
            keyboardViewEx.setIme(this.mIme);
            keyboardViewEx.setOnKeyboardActionListener(new KeyboardViewEx.KeyboardActionAdapter() { // from class: com.nuance.swype.input.japanese.JapaneseInputView.4
                @Override // com.nuance.swype.input.KeyboardViewEx.OnKeyboardActionListener
                public void onText(CharSequence text, long eventTime) {
                    if (!JapaneseInputView.this.gridViewFunctionButtonPressed) {
                        JapaneseInputView.this.hideGridCandidatesView();
                        JapaneseInputView.this.setCandidatesViewShown(true);
                        if (JapaneseInputView.this.isSegmentationPopupNeeded) {
                            JapaneseInputView.this.postDelayShowSegmentationPopupMessage();
                        }
                        if (JapaneseInputView.this.mWordList != null) {
                            for (int i = 0; i < JapaneseInputView.this.mWordList.size(); i++) {
                                if (JapaneseInputView.this.mWordList.get(i).toString().equals(text.toString())) {
                                    JapaneseInputView.this.selectWord(i, text, JapaneseInputView.this.candidatesListViewCJK);
                                    return;
                                }
                            }
                        }
                    }
                }

                @Override // com.nuance.swype.input.KeyboardViewEx.KeyboardActionAdapter, com.nuance.swype.input.KeyboardViewEx.OnKeyboardActionListener
                public void onRelease(int primaryCode) {
                    keyboard.clearStickyKeys();
                }
            });
            this.candidatesPopup.setLayoutParams(new FrameLayout.LayoutParams(getKeyboard().getMinWidth(), height));
            this.mJpWordPageContainer.showContextWindow(this.candidatesPopup);
        }
    }

    @SuppressLint({"PrivateResource"})
    public void setGridCandidates(List<CharSequence> aLableList, int keyboardWidth) {
        ArrayList<KeyboardEx.GridKeyInfo> curRowKeys;
        ArrayList<Integer> curRowKeyWidth;
        int textSize = IMEApplication.from(this.mIme).getResources().getDimensionPixelSize(R.dimen.key_text_size);
        Paint paint = new Paint();
        paint.setTypeface(Typeface.DEFAULT);
        paint.setTextSize(textSize);
        paint.setTextAlign(Paint.Align.CENTER);
        int paddingWidth = (textSize / 3) + 1;
        int curRowWidth = 0;
        int curRowNumber = 0;
        List<ArrayList<Integer>> rowKeyWidth = new ArrayList<>();
        this.mRows.clear();
        int i = 0;
        while (i < aLableList.size()) {
            if (rowKeyWidth.size() <= curRowNumber || this.mRows.size() <= curRowNumber) {
                curRowKeys = new ArrayList<>();
                this.mRows.add(curRowKeys);
                curRowKeyWidth = new ArrayList<>();
                rowKeyWidth.add(curRowKeyWidth);
            } else {
                curRowKeys = this.mRows.get(curRowNumber);
                curRowKeyWidth = rowKeyWidth.get(curRowNumber);
            }
            String str = aLableList.get(i).toString();
            int keyWidth = ((int) paint.measureText(str)) + 2 + (paddingWidth * 2);
            if (keyWidth < (paddingWidth * 2) + textSize) {
                keyWidth = textSize + (paddingWidth * 2);
            }
            if (curRowWidth + keyWidth > keyboardWidth) {
                if (curRowKeyWidth.isEmpty() || curRowKeys.isEmpty()) {
                    curRowKeys.add(new KeyboardEx.GridKeyInfo(keyboardWidth, str));
                    curRowKeyWidth.add(Integer.valueOf(keyboardWidth));
                } else {
                    int addingWidth = (keyboardWidth - curRowWidth) / curRowKeyWidth.size();
                    int refreshedRowWidth = 0;
                    for (int j = 0; j < curRowKeyWidth.size(); j++) {
                        int newKeyWidth = curRowKeyWidth.get(j).intValue() + addingWidth;
                        refreshedRowWidth += newKeyWidth;
                        if (curRowKeyWidth.size() - 1 == j) {
                            newKeyWidth += keyboardWidth - refreshedRowWidth;
                        }
                        curRowKeyWidth.set(j, Integer.valueOf(newKeyWidth));
                        curRowKeys.get(j).width = newKeyWidth;
                    }
                    i--;
                }
                curRowWidth = 0;
                curRowNumber++;
            } else {
                curRowWidth += keyWidth;
                curRowKeys.add(new KeyboardEx.GridKeyInfo(keyWidth, str));
                curRowKeyWidth.add(Integer.valueOf(keyWidth));
            }
            i++;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hideGridCandidatesView() {
        if (this.mContextWindowShowing) {
            this.mJpWordPageContainer.hideContextWindow(this.candidatesPopup);
            this.mJpWordPageContainer.setMinimumHeight(0);
        }
    }

    @Override // com.nuance.swype.input.InputView
    public boolean handleKey(int primaryCode, boolean quickPressed, int repeatedCount) {
        removeToastMsg(5);
        switch (primaryCode) {
            case KeyboardEx.KEYCODE_MULTITAP_TOGGLE /* 2940 */:
                if (!this.mKeyboardInputSuggestionOn) {
                    return true;
                }
                postToastMsg(5);
                return true;
            case KeyboardEx.KEYCODE_DPAD_LEFT /* 4061 */:
                if (!timeoutWhenMultitappingCommaOrPeriod() && this.mInlineWord.length() > 0) {
                    if (isConverting()) {
                        reduceRangeConversion();
                        return true;
                    }
                    doConversionEx();
                    return true;
                }
                this.mIme.sendLeftRightKey(KeyboardEx.KEYCODE_DPAD_LEFT, 0);
                return true;
            case KeyboardEx.KEYCODE_DPAD_RIGHT /* 4062 */:
                if (this.mInlineWord.length() > 0) {
                    if (timeoutWhenMultitappingCommaOrPeriod() || !isConverting()) {
                        return true;
                    }
                    expandRangeConversion();
                    return true;
                }
                this.mIme.sendLeftRightKey(KeyboardEx.KEYCODE_DPAD_RIGHT, 0);
                return true;
            case KeyboardEx.KEYCODE_MODE_CHANGE /* 6462 */:
                return super.handleKey(primaryCode, quickPressed, repeatedCount);
            case KeyboardEx.KEYCODE_SPEECH /* 6463 */:
                flushCurrentActiveWord();
                if (quickPressed) {
                    return true;
                }
                clearSuggestions();
                startSpeech();
                return true;
            case KeyboardEx.KEYCODE_MODE_BACK /* 43576 */:
                this.mKeyboardSwitcher.toggleLastKeyboard();
                return true;
            case 43577:
                if (isConverting() || !this.mKeyboardSwitcher.isKeypadInput() || this.mInlineWord.length() <= 0) {
                    return true;
                }
                if (JapaneseCharacterSetUtils.convertHira(this.mInlineWord.charAt(this.mInlineWord.length() - 1)) != 0) {
                    this.mJapaneseInput.clearKey();
                }
                handleExplicitInput(JapaneseCharacterSetUtils.convertHira(this.mInlineWord.charAt(this.mInlineWord.length() - 1)));
                return true;
            case KeyboardEx.KEYCODE_HIRACYCLE /* 43578 */:
                if (isConverting() || !this.mKeyboardSwitcher.isKeypadInput() || timeoutWhenMultitappingCommaOrPeriod() || this.mInlineWord.length() <= 0) {
                    return true;
                }
                this.mJapaneseInput.clearKey();
                handleExplicitInput(JapaneseCharacterSetUtils.hiraCycle(this.mInlineWord.charAt(this.mInlineWord.length() - 1)));
                return true;
            case KeyboardEx.KEYCODE_SEGMENTATION /* 43580 */:
                showSegmentationPopup();
                return true;
            default:
                return super.handleKey(primaryCode, quickPressed, repeatedCount);
        }
    }

    public boolean isConverting() {
        return this.mConvert != 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int indexOfInline(String inline) {
        if (this.mWordList == null) {
            return 255;
        }
        for (int i = 0; i < this.mWordList.size(); i++) {
            if (this.mWordList.get(i).toString().equalsIgnoreCase(inline)) {
                int index = i;
                return index;
            }
        }
        return 255;
    }

    private void resetConversionStrings() {
        this.covertingMidashigo.setLength(0);
        this.covertingYomi.setLength(0);
    }

    private boolean doConversion() {
        if (isConverting()) {
            return false;
        }
        resetConversionStrings();
        this.mJapaneseInput.getExactType(this.covertingYomi);
        this.mJapaneseInput.wordSelected(indexOfInline(this.mInlineWord.toString()), false);
        this.mJapaneseInput.startRangeConversion(4, this.mInlineWord.toString().toCharArray(), this.mInlineWord.toString().toCharArray().length);
        this.mConvert = 1;
        this.mSuggestedWordIndex.set(0);
        updateCandlistWhenRC(this.mSuggestedWordIndex.get());
        updateInlineWhenRC(this.mSuggestedWordIndex.get());
        return true;
    }

    private boolean doConversionEx() {
        resetConversionStrings();
        this.mJapaneseInput.getExactType(this.covertingYomi);
        this.mJapaneseInput.startRangeConversion(1, this.mInlineWord.toString().toCharArray(), this.mInlineWord.toString().toCharArray().length);
        this.mJapaneseInput.wordSelected(indexOfInline(this.mInlineWord.toString()), false);
        this.mConvert = 1;
        this.mSuggestedWordIndex.set(0);
        updateCandlistWhenRC(this.mSuggestedWordIndex.get());
        updateInlineWhenRC(this.mSuggestedWordIndex.get());
        return false;
    }

    private boolean reduceRangeConversion() {
        if (this.mRCReadingDivInfo[1] != 1 || this.mSuggestedWordIndex.get() <= 0) {
            this.mSuggestedWordIndex.set(0);
            this.mJapaneseInput.startRangeConversion(2, this.mInlineWord.toString().toCharArray(), this.mInlineWord.toString().toCharArray().length);
            updateCandlistWhenRC(this.mSuggestedWordIndex.get());
            updateInlineWhenRC(this.mSuggestedWordIndex.get());
        }
        return true;
    }

    private boolean expandRangeConversion() {
        if (this.mRCReadingDivInfo[0] != 1 || this.mSuggestedWordIndex.get() <= 0) {
            this.mSuggestedWordIndex.set(0);
            this.mJapaneseInput.startRangeConversion(3, this.mInlineWord.toString().toCharArray(), this.mInlineWord.toString().toCharArray().length);
            updateCandlistWhenRC(this.mSuggestedWordIndex.get());
            updateInlineWhenRC(this.mSuggestedWordIndex.get());
        }
        return true;
    }

    public boolean confirmSelectedWordWhenConverting(int index) {
        boolean bFinish = !this.mJapaneseInput.confirmRangeConvWord(index);
        if (!bFinish) {
            this.mInlineWord.clear();
            if (this.mInlineTextBak.length() > 0) {
                this.mInlineWord.append(this.mInlineTextBak);
            }
            this.mJapaneseInput.clearAllKeys();
            for (int i = 0; i < this.mInlineWord.length(); i++) {
                this.mJapaneseInput.processKey(null, this.mInlineWord.charAt(i), getShiftState(), SystemClock.uptimeMillis());
            }
            this.mSuggestedWordIndex.set(0);
            updateInlineWhenRC(this.mSuggestedWordIndex.get());
            updateCandlistWhenRC(this.mSuggestedWordIndex.get());
        }
        return bFinish;
    }

    private boolean updateCandlistWhenRC(int index) {
        this.mWordList = this.mJapaneseInput.getRangeConvCandidateList(this.mSuggestedWord, index);
        if (this.mWordList.size() > 0) {
            updateWordListView(this.mWordList, index, true);
        }
        return true;
    }

    private boolean updateInlineWhenRC(int index) {
        char[] phrasebuf = new char[256];
        int phraseLen = this.mJapaneseInput.getRangeConvertedPhrase(index, this.mRCReadingDivInfo, phrasebuf, this.mRCOutPhraseDivInfo);
        if (phraseLen <= 0) {
            this.mInlineWord.clear();
            this.mInlineWord.clearSpans();
            this.mInlineTextBak = "";
            return false;
        }
        this.mInlineWord.clear();
        this.mInlineWord.clearSpans();
        this.mInlineWord.append((CharSequence) String.valueOf(phrasebuf, 0, phraseLen));
        this.mInlineWord.setSpan(this.mWordComposeSpan, 0, this.mInlineWord.length(), R.styleable.ThemeTemplate_emojiCategoryIconSmiles);
        this.mInlineTextBak = "";
        if (this.mRCReadingDivInfo[0] != 0) {
            if (this.mRCOutPhraseDivInfo[0] < this.mInlineWord.length()) {
                this.mInlineWord.setSpan(this.mBKRangeConversionDefaultSpan, 0, this.mRCOutPhraseDivInfo[0], R.styleable.ThemeTemplate_emojiCategoryIconSmiles);
                this.mInlineWord.setSpan(this.mBKRemainSpan, this.mRCOutPhraseDivInfo[0], this.mInlineWord.length(), 33);
                this.mInlineTextBak = this.mInlineWord.subSequence(this.mRCOutPhraseDivInfo[0], this.mInlineWord.length());
            } else {
                this.mInlineWord.setSpan(this.mBKRangeConversionDefaultSpan, 0, this.mInlineWord.length(), R.styleable.ThemeTemplate_emojiCategoryIconSmiles);
            }
        }
        InputConnection ic = getCurrentInputConnection();
        if (ic != null) {
            ic.beginBatchEdit();
            ic.setComposingText(this.mInlineWord, 1);
            ic.endBatchEdit();
        }
        return true;
    }

    private boolean isCharactersAllKana(String str) {
        if (str == null || str.length() <= 0) {
            return false;
        }
        for (int i = 0; i < str.length(); i++) {
            if (!JapaneseCharacterSetUtils.isHiragana(str.charAt(i)) && !JapaneseCharacterSetUtils.isKatakana(str.charAt(i)) && JP_KEYPAD_LONG_VOWEL_KEY != str.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isLetterAndDigit(CharSequence text) {
        int length = text.length();
        if (length <= 0) {
            return false;
        }
        for (int i = 0; i < length; i++) {
            char ch = text.charAt(i);
            if (!Character.isLetter(ch) && !Character.isDigit(ch)) {
                return false;
            }
        }
        return true;
    }

    private int getRomajiPosByCursorPos(int iCursorPos) {
        int inlinePos = 0;
        for (int i = 0; i < iCursorPos && i < this.mCursorPosCount; i++) {
            inlinePos += this.mRomajiDivInfoOfInline[i];
        }
        return inlinePos;
    }

    private int getRomajiBytesOfCurrentPos(int iPos) {
        if (iPos < 0 || iPos >= this.mCursorPosCount) {
            return 0;
        }
        return this.mRomajiDivInfoOfInline[iPos];
    }

    private boolean moveHighlightToNearCandidateSoftKeyboard(int keyCode) {
        CharSequence oldword;
        if (this.mJapaneseWordListViewContainer == null || this.candidatesListViewCJK == null || this.mWordList == null || this.mSuggestedWord == null || this.mWordList.isEmpty()) {
            return false;
        }
        if (this.candidatesListViewCJK.wordCount() == 0) {
            return false;
        }
        int wordCandidatesCount = this.mWordList.size();
        Boolean isRight = false;
        if (keyCode == 22 || keyCode == 20) {
            isRight = true;
        } else if (keyCode == 21 || keyCode == 19) {
            isRight = false;
        }
        if (isRight.booleanValue() && this.mSuggestedWordIndex.get() >= wordCandidatesCount - 1) {
            this.mSuggestedWordIndex.set(-1);
            this.candidatesListViewCJK.setSuggestions(this.mWordList, 0);
            syncCandidateDisplayStyleToMode();
        }
        int OldSelectIndex = this.mSuggestedWordIndex.get();
        if (isRight.booleanValue()) {
            this.mSuggestedWordIndex.incrementAndGet();
        } else {
            this.mSuggestedWordIndex.decrementAndGet();
        }
        if (this.mSuggestedWordIndex.get() < 0) {
            this.mSuggestedWordIndex.set(0);
        }
        this.mSuggestedWord.clear();
        CharSequence word = this.mWordList.get(this.mSuggestedWordIndex.get());
        if (word != null) {
            this.mSuggestedWord.append(word);
            this.candidatesListViewCJK.touchWord(this.mSuggestedWordIndex.get(), this.mSuggestedWord);
            if (word instanceof SpannableStringBuilder) {
                ((SpannableStringBuilder) word).setSpan(this.mColorRecommended, 0, word.length(), R.styleable.ThemeTemplate_emojiCategoryIconSmiles);
                ((SpannableStringBuilder) word).setSpan(this.mExactWordSpan, 0, word.length(), R.styleable.ThemeTemplate_emojiCategoryIconSmiles);
            }
            if (OldSelectIndex >= 0 && OldSelectIndex != this.mSuggestedWordIndex.get() && (oldword = this.mWordList.get(OldSelectIndex)) != null && (oldword instanceof SpannableStringBuilder)) {
                ((SpannableStringBuilder) oldword).clearSpans();
            }
        }
        syncCandidateDisplayStyleToMode();
        if (isRight.booleanValue() && this.candidatesListViewCJK.isKeyOutofRightBound(this.mSuggestedWordIndex.get())) {
            setScrollable(true);
            this.candidatesListViewCJK.scrollNext();
        } else if (!isRight.booleanValue() && this.candidatesListViewCJK.isKeyOutofLeftBound(this.mSuggestedWordIndex.get())) {
            setScrollable(true);
            this.candidatesListViewCJK.scrollPrev();
        }
        int defaultSpanNum = this.mSuggestedWord.length();
        if (this.mInlineWord.length() > 0 && this.mRCOutPhraseDivInfo[0] > 0 && this.mRCOutPhraseDivInfo[0] <= this.mInlineWord.length()) {
            this.mSuggestedWord.append(this.mInlineWord.subSequence(this.mRCOutPhraseDivInfo[0], this.mInlineWord.length()));
        }
        InputConnection ic = getCurrentInputConnection();
        this.mSuggestedWord.clearSpans();
        this.mSuggestedWord.setSpan(this.mWordComposeSpan, 0, this.mSuggestedWord.length(), R.styleable.ThemeTemplate_emojiCategoryIconSmiles);
        this.mSuggestedWord.setSpan(this.mBKRangeConversionDefaultSpan, 0, defaultSpanNum, R.styleable.ThemeTemplate_emojiCategoryIconSmiles);
        this.mSuggestedWord.setSpan(this.mBKRemainSpan, defaultSpanNum, this.mSuggestedWord.length(), 33);
        updateInlineWhenRC(this.mSuggestedWordIndex.get());
        ic.setComposingText(this.mSuggestedWord, 1);
        setScrollable(false);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.InputView
    public boolean shouldShowTips() {
        return this.mKeyboardSwitcher.isAlphabetMode() && isTraceInputEnabled();
    }

    @Override // com.nuance.swype.input.chinese.CJKCandidatesListView.OnWordSelectActionListener
    public void onCandidateLongPressed(int index, String word, int wdSource, CJKCandidatesListView aSource) {
    }

    @Override // com.nuance.swype.input.chinese.CJKCandidatesListView.OnWordSelectActionListener
    public boolean onPressReleaseCandidate(int index, String word, int wdSource) {
        return false;
    }

    @Override // com.nuance.swype.input.InputView
    public XT9CoreInput getXT9CoreInput() {
        return this.mJapaneseInput;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.KeyboardViewEx
    public boolean popupMiniKeyboardOrLongPress() {
        if (isMultitapping()) {
            if (this.mTapCount >= 0) {
                multitapTimeOut();
            } else {
                this.mHandler.removeMessages(106);
                resetMultiTap();
            }
        }
        if (this.mPopupLayout == 0 || this.mCurrentKey < 0 || this.mCurrentKey >= this.mKeys.length) {
            return false;
        }
        KeyboardEx.Key popupKey = this.mKeys[this.mCurrentKey];
        if (popupKey == null || ((popupKey.altCode != 2937 && isKeypadInput()) || popupKey.popupCharacters != null)) {
            return onLongPress(popupKey);
        }
        return false;
    }

    public void showNav(int keyindex) {
        log.d("showNav()...keyindex: ", Integer.valueOf(keyindex));
        if (isKeypadInput()) {
            PopupWindow previewPopup = this.mJapaneseNavPopup;
            KeyboardEx.Key[] keys = this.mKeys;
            if (((this.mCurrentKey >= 0 && this.mCurrentKey < this.mKeys.length && getVisibility() != 8) || previewPopup.isShowing()) && getWindowToken() != null) {
                if (this.mCurrentKey >= 0) {
                    this.mCurrentKeyNav = this.mCurrentKey;
                }
                KeyboardEx.Key key = keys[this.mCurrentKeyNav];
                if (this.arrayFlickerPreview == null) {
                    this.arrayFlickerPreview = new PreviewView[5];
                    this.arrayFlickerPreview[0] = (PreviewView) this.mFlickerView.findViewById(R.id.flicker_zero);
                    this.arrayFlickerPreview[1] = (PreviewView) this.mFlickerView.findViewById(R.id.flicker_one);
                    this.arrayFlickerPreview[2] = (PreviewView) this.mFlickerView.findViewById(R.id.flicker_two);
                    this.arrayFlickerPreview[3] = (PreviewView) this.mFlickerView.findViewById(R.id.flicker_three);
                    this.arrayFlickerPreview[4] = (PreviewView) this.mFlickerView.findViewById(R.id.flicker_four);
                }
                int index = keyindex;
                if ((keyindex >= 0 && key.codes[0] <= 12352) || key.codes[0] == 43575) {
                    index = -1;
                }
                if (index == -1) {
                    previewPopup.dismiss();
                    return;
                }
                if (keyindex >= 0 && keyindex < 5) {
                    for (int i = 0; i < 5; i++) {
                        this.arrayFlickerPreview[i].setTextColor(this.mPopupTextColor);
                        this.arrayFlickerPreview[i].setBackgroundDrawable(this.flickerKeyPopupBackground);
                        this.arrayFlickerPreview[i].setText("");
                    }
                    for (int i2 = 0; i2 < 5; i2++) {
                        this.arrayFlickerPreview[i2].setTextSizePixels(this.flickerKeyTextSize);
                        if (i2 == keyindex) {
                            log.d("showNav()...show default keyindex: ", Integer.valueOf(keyindex));
                            this.arrayFlickerPreview[i2].setTextColor(this.miTraceColor);
                            this.arrayFlickerPreview[i2].setBackgroundDrawable(this.flickerKeyPopupSelectedBackground);
                        }
                    }
                }
                char[] c = new char[2];
                c[1] = 0;
                int popupWidth = (this.flickerKeyHeight * 3) + 4;
                for (int icodes = 0; icodes < key.codes.length; icodes++) {
                    c[0] = (char) key.codes[icodes];
                    boolean isHiraBig = JapaneseCharacterSetUtils.isBigHiragana(c[0]);
                    if ((icodes >= 0 && icodes < 5 && isHiraBig) || c[0] == JP_KEYPAD_LONG_VOWEL_KEY) {
                        if (key.codes[0] == JP_KEYPAD_YA_KEY) {
                            if (icodes == 0 || icodes == 1 || icodes == 2) {
                                this.arrayFlickerPreview[icodes * 2].setText(String.valueOf(c, 0, 1));
                            }
                        } else {
                            this.arrayFlickerPreview[icodes].setText(String.valueOf(c, 0, 1));
                        }
                    }
                }
                this.mPopupPreviewX = (key.x + (key.width / 2)) - (popupWidth / 2);
                this.mPopupPreviewY = (key.y + (key.height / 2)) - (popupWidth / 2);
                if (this.mOffsetInWindow == null) {
                    recalculateOffsets();
                }
                int interval = this.flickerKeyHeight * 2;
                if (previewPopup.isShowing()) {
                    previewPopup.update(this.mPopupPreviewX + this.mOffsetInWindow[0], (this.mPopupPreviewY + this.mOffsetInWindow[1]) - interval, popupWidth, popupWidth);
                } else if (!ActivityManagerCompat.isUserAMonkey()) {
                    previewPopup.setWidth(popupWidth);
                    previewPopup.setHeight(popupWidth);
                    previewPopup.showAtLocation(this, 0, this.mPopupPreviewX + this.mOffsetInWindow[0], (this.mPopupPreviewY - interval) + this.mOffsetInWindow[1]);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.KeyboardViewEx
    public void clearKeyboardState() {
        super.clearKeyboardState();
        dismissJapanesePopup();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.KeyboardViewEx
    public boolean isPressHoldFlickrMessage() {
        return isKeypadInput() && this.isKeypadFlickrInputSupported;
    }

    @Override // com.nuance.swype.input.KeyboardViewEx
    public boolean handleCallbackMessage(Message msg) {
        KeyboardEx.Key currentKey;
        int message = msg.what;
        if (message == 104) {
            int keyIndex = msg.arg1;
            int pointerId = msg.arg2;
            if (keyIndex != -1 && keyIndex < this.mKeys.length && (currentKey = this.mKeys[keyIndex]) != null && currentKey.codes != null) {
                if (currentKey.codes[0] == 4077 || currentKey.codes[0] == 4074 || currentKey.codes[0] == 43581 || ((this.mKeyboardSwitcher != null && !this.mKeyboardSwitcher.isAlphabetMode()) || currentKey.codes[0] == 6462 || currentKey.codes[0] == 2939 || currentKey.codes[0] == 122 || currentKey.codes[0] == 10)) {
                    super.handleCallbackMessage(msg);
                } else if (!handleMessageJP(msg)) {
                    this.mHandler.removeMessages(1005);
                    resetMultiTap();
                    this.mHandler.removeMessages(106);
                    if (hasAltSymbolOrCode(currentKey)) {
                        if (!hasAltSymbol(currentKey) || !currentKey.showPopup) {
                            if (onLongPress(currentKey) && isLongPressableBackspaceKey(currentKey)) {
                                this.mHandler.sendEmptyMessageDelayed(1002, 60L);
                                showAltSymbolPopup(currentKey, pointerId);
                                if (!this.mHandler.hasMessages(104)) {
                                    this.mKeyRepeated = true;
                                    this.repeatCount++;
                                    int expValue = (int) Math.exp(this.repeatCount);
                                    int timeOut = 100;
                                    if (expValue >= 0 && expValue < this.mShortLongPressTimeout) {
                                        timeOut = this.mShortLongPressTimeout - expValue;
                                    }
                                    this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(104, keyIndex, pointerId), timeOut);
                                }
                            }
                        } else {
                            this.mHandler.sendEmptyMessageDelayed(1005, this.mLongPressTimeout);
                            this.mHandler.sendEmptyMessageDelayed(1002, 60L);
                            showAltSymbolPopup(currentKey, pointerId);
                        }
                    }
                }
            }
        } else if (message == 1007) {
            handleMessageJP(msg);
        } else if (message == 1005) {
            handleMessageJP(msg);
        } else {
            super.handleCallbackMessage(msg);
        }
        return true;
    }

    public boolean handleMessageJP(Message message) {
        KeyboardEx.Key akey;
        KeyboardEx.Key akey2;
        boolean z = false;
        int msg = message.what;
        if (this.mCurrentKey == -1 || this.mCurrentKey >= this.mKeys.length) {
            return false;
        }
        KeyboardEx.Key currentKey = this.mKeys[this.mCurrentKey];
        switch (msg) {
            case 104:
                if (currentKey == null || !isKeypadInput()) {
                    return false;
                }
                if ((currentKey.codes != null && currentKey.codes.length > 0 && (currentKey.codes[0] == 32 || currentKey.codes[0] == 43575)) || currentKey.altCode == 2937) {
                    return false;
                }
                return true;
            case 1005:
                if (currentKey == null) {
                    return false;
                }
                if (isKeypadInput() && this.isKeypadFlickrInputSupported && currentKey.altCode != 2937) {
                    if (this.mCurrentKey >= 0) {
                        akey = this.mKeys[this.mCurrentKey];
                    } else if (this.mCurrentKeyNav >= 0) {
                        akey = this.mKeys[this.mCurrentKeyNav];
                    } else {
                        akey = null;
                        showNav(-1);
                        popupMiniKeyboardOrLongPress();
                    }
                    if (akey != null && akey.codes.length > 0 && akey.codes[0] > 12352 && akey.codes[0] <= JP_KEYPAD_LONG_VOWEL_KEY) {
                        z = true;
                    }
                    if (!z) {
                        showNav(-1);
                        popupMiniKeyboardOrLongPress();
                    }
                    if (this.mInMultiTap) {
                        resetMultiTap();
                    }
                    return true;
                }
                if (isKeypadInput() && this.isKeypadMultitapInputSupported && JapaneseCharacterSetUtils.isHiragana((char) currentKey.codes[0])) {
                    return false;
                }
                popupMiniKeyboardOrLongPress();
                return false;
            case 1007:
                if (currentKey == null || !isKeypadInput() || !this.isKeypadFlickrInputSupported || currentKey.altCode == 2937) {
                    return false;
                }
                if (this.mCurrentKey >= 0) {
                    akey2 = this.mKeys[this.mCurrentKey];
                } else if (this.mCurrentKeyNav >= 0) {
                    akey2 = this.mKeys[this.mCurrentKeyNav];
                } else {
                    akey2 = null;
                    showNav(-1);
                }
                if (this.mJapaneseNavPopup != null && this.mJapaneseNavPopup.isShowing()) {
                    this.mHandler.removeMessages(104);
                    this.mHandler.removeMessages(1005);
                    this.mHandler.removeMessages(1007);
                    return false;
                }
                if (akey2 == null || akey2.codes.length <= 0 || akey2.codes[0] <= 12352 || akey2.codes[0] > JP_KEYPAD_LONG_VOWEL_KEY) {
                    return false;
                }
                this.japaneseFlickerKeyRelativeIndex = 0;
                this.japaneseFlickerKeyIndex = this.mCurrentKey;
                showNav(0);
                this.mHandler.removeMessages(104);
                this.mHandler.removeMessages(1005);
                this.mHandler.removeMessages(1007);
                return false;
            default:
                return false;
        }
    }

    public final void prepareFlickerPopup() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        LayoutInflater inflater2 = IMEApplication.from(getContext()).getThemedLayoutInflater(inflater);
        this.mJapaneseNavPopup = new PopupWindow(getContext());
        this.mFlickerView = inflater2.inflate(this.mFlickerPopup, (ViewGroup) null);
        this.mJapaneseNavPopup.setContentView(this.mFlickerView);
        this.mJapaneseNavPopup.setBackgroundDrawable(null);
        this.mJapaneseNavPopup.setTouchable(false);
    }

    @SuppressLint({"InflateParams", "PrivateResource"})
    private void initSegmentationPopup() {
        if (!isKeypadInput()) {
            if (this.segmentationView == null) {
                IMEApplication app = IMEApplication.from(this.mIme);
                LayoutInflater inflater = app.getThemedLayoutInflater(LayoutInflater.from(getContext()));
                app.getThemeLoader().setLayoutInflaterFactory(inflater);
                this.segmentationView = inflater.inflate(R.layout.japanese_segmentation_popup, (ViewGroup) null);
                app.getThemeLoader().applyTheme(this.segmentationView);
                this.leftArrowButton = this.segmentationView.findViewById(R.id.leftArrow);
                if (this.leftArrowButton != null) {
                    this.leftArrowButton.setOnClickListener(this);
                }
                this.rightArrowButton = this.segmentationView.findViewById(R.id.rightArrow);
                if (this.rightArrowButton != null) {
                    this.rightArrowButton.setOnClickListener(this);
                }
                this.closeSegmentationButton = this.segmentationView.findViewById(R.id.closeArrow);
                if (this.closeSegmentationButton != null) {
                    this.closeSegmentationButton.setOnClickListener(this);
                }
            }
            if (this.segmentationPopup == null) {
                this.segmentationPopup = new PopupWindow(getContext());
                this.segmentationPopup.setContentView(this.segmentationView);
                this.segmentationPopup.setBackgroundDrawable(null);
            }
        }
    }

    @SuppressLint({"PrivateResource"})
    public void showSegmentationPopup() {
        this.isSegmentationPopupNeeded = false;
        if (this.segmentationPopup != null) {
            recalculateOffsets();
            int y = 0;
            if (this.mJapaneseWordListViewContainer.isShown()) {
                y = this.mJapaneseWordListViewContainer.getHeight() + 0;
            }
            if (Build.VERSION.SDK_INT == 21) {
                this.leftArrowButton.setBackground(getResources().getDrawable(R.drawable.btn_keyboard_action_key_jp));
                this.rightArrowButton.setBackground(getResources().getDrawable(R.drawable.btn_keyboard_action_key_jp));
            }
            if (this.segmentationPopup.isShowing()) {
                this.segmentationPopup.update(this.mOffsetInWindow[0] + 0, this.mOffsetInWindow[1], getWidth(), getHeight());
            } else if (!ActivityManagerCompat.isUserAMonkey()) {
                this.segmentationPopup.update(0, y, getWidth(), getHeight());
                this.segmentationPopup.showAtLocation(this, 0, this.mOffsetInWindow[0] + 0, this.mOffsetInWindow[1]);
            }
        }
    }

    public void updateSegemetationPopup(int dx, int dy) {
        if (this.segmentationPopup != null) {
            recalculateOffsets();
            if (this.segmentationPopup.isShowing()) {
                this.segmentationPopup.update(this.mOffsetInWindow[0] + 0 + dx, this.mOffsetInWindow[1] + dy, getWidth(), getHeight());
            }
        }
    }

    @Override // com.nuance.swype.input.KeyboardViewEx, android.view.View.OnClickListener
    public void onClick(View v) {
        if (v == this.leftArrowButton) {
            if (this.mInlineWord != null && this.mInlineWord.length() > 0) {
                if (isConverting()) {
                    reduceRangeConversion();
                    return;
                } else {
                    doConversionEx();
                    return;
                }
            }
            if (this.mIme != null) {
                this.mIme.sendLeftRightKey(KeyboardEx.KEYCODE_DPAD_LEFT, 0);
                return;
            }
            return;
        }
        if (v == this.rightArrowButton) {
            if (this.mInlineWord != null && this.mInlineWord.length() > 0) {
                if (isConverting()) {
                    expandRangeConversion();
                    return;
                }
                return;
            } else {
                if (this.mIme != null) {
                    this.mIme.sendLeftRightKey(KeyboardEx.KEYCODE_DPAD_RIGHT, 0);
                    return;
                }
                return;
            }
        }
        if (v == this.closeSegmentationButton) {
            dismissSegmentationPopup();
        } else {
            super.onClick(v);
        }
    }

    public void dismissSegmentationPopup() {
        if (this.segmentationPopup != null && this.segmentationPopup.isShowing()) {
            this.segmentationPopup.dismiss();
        }
    }

    public void dismissJapanesePopup() {
        if (this.mJapaneseNavPopup != null && this.mJapaneseNavPopup.isShowing()) {
            this.mJapaneseNavPopup.dismiss();
        }
    }

    protected int calculateDirectionFlickerPopup(int verLen, int horLen) {
        int dir = 0;
        double dimenSquare = (horLen * horLen) + (verLen * verLen);
        double radiusSquare = this.flickerMiddleKeyRadius * this.flickerMiddleKeyRadius;
        if (dimenSquare <= radiusSquare) {
            dir = 0;
        } else if (dimenSquare <= KEYPAD_ISOLATION_AREA_PARM * radiusSquare) {
            if (this.japaneseFlickerKeyRelativeIndex != -1) {
                dir = this.japaneseFlickerKeyRelativeIndex;
            }
        } else if (horLen < 0 && Math.abs(horLen) >= Math.abs(verLen)) {
            dir = 1;
        } else if (verLen < 0 && Math.abs(verLen) > Math.abs(horLen)) {
            dir = 2;
        } else if (horLen > 0 && horLen >= Math.abs(verLen)) {
            dir = 3;
        } else {
            dir = 4;
        }
        log.d("calculateDirectionFlickerPopup()... dir: ", Integer.valueOf(dir));
        return dir;
    }

    protected boolean isValidFlickerIndex() {
        log.d("isValidFlickerIndex()...japaneseFlickerKeyIndex: ", Integer.valueOf(this.japaneseFlickerKeyIndex));
        KeyboardEx.Key[] visibleKeys = this.mKeys;
        if (this.japaneseFlickerKeyIndex < 0 || this.japaneseFlickerKeyIndex >= visibleKeys.length) {
            return false;
        }
        return JapaneseCharacterSetUtils.isHiragana((char) visibleKeys[this.japaneseFlickerKeyIndex].codes[0]);
    }

    protected boolean isValidFlickerRelativeIndex() {
        log.d("isValidFlickerRelativeIndex()...japaneseFlickerKeyRelativeIndex: ", Integer.valueOf(this.japaneseFlickerKeyRelativeIndex));
        if (isValidFlickerIndex()) {
            KeyboardEx.Key[] visibleKeys = this.mKeys;
            if (this.japaneseFlickerKeyRelativeIndex < visibleKeys[this.japaneseFlickerKeyIndex].codes.length && this.japaneseFlickerKeyRelativeIndex >= 0) {
                return true;
            }
        }
        return false;
    }

    protected void resetFlickerIndex() {
        log.d("resetFlickerIndex");
        this.japaneseFlickerKeyIndex = -1;
        this.japaneseFlickerKeyRelativeIndex = -1;
        this.isTopKeyFlicerUp = false;
        if (this.mJapaneseNavPopup != null && this.mJapaneseNavPopup.isShowing()) {
            this.mJapaneseNavPopup.dismiss();
        }
    }

    @SuppressLint({"DefaultLocale"})
    private int addExactEnWordsFromUILayer() {
        StringBuilder sb = new StringBuilder();
        if (isKeyboardInput()) {
            this.mJapaneseInput.getExactType(sb);
        } else {
            sb.append((CharSequence) this.mInlineWord);
        }
        if (sb.length() > 0) {
            String lowerCase = sb.toString().toLowerCase();
            String upperCase = sb.toString().toUpperCase();
            String lowerCase2 = sb.toString().toLowerCase();
            int length = lowerCase2.length();
            String str = lowerCase2;
            if (length > 1) {
                char charAt = lowerCase2.charAt(0);
                str = lowerCase2.replaceFirst(Character.toString(charAt), Character.toString(charAt).toUpperCase());
            }
            if (!lowerCase.equals(upperCase) || !lowerCase.equals(str)) {
                if (this.mWordList.size() > 0) {
                    int size = this.mWordList.size();
                    int i = 0;
                    while (i < size) {
                        CharSequence charSequence = this.mWordList.get(i);
                        if (charSequence.toString().equals(lowerCase) || charSequence.toString().equals(upperCase) || charSequence.toString().equals(str)) {
                            this.mWordList.remove(charSequence);
                            i--;
                            size--;
                        }
                        i++;
                    }
                }
                this.mWordList.add(new SpannableStringBuilder(lowerCase));
                if (!lowerCase.equals(upperCase)) {
                    this.mWordList.add(new SpannableStringBuilder(upperCase));
                }
                if (!str.equals(lowerCase) && !str.equals(upperCase)) {
                    this.mWordList.add(new SpannableStringBuilder(str));
                }
            }
        }
        return 0;
    }

    private String convertText2Romaji(String textStr) {
        StringBuilder romajiString = new StringBuilder();
        char[] kana = new char[64];
        char[] romaji = new char[64];
        int[] plen = new int[1];
        for (int i = 0; i < textStr.length(); i++) {
            kana[0] = textStr.charAt(i);
            if (this.mJapaneseInput.kanatoRomaji(kana, 1, romaji, plen) == 0) {
                romaji[plen[0]] = 0;
                if (plen[0] > 0) {
                    romajiString.append(String.valueOf(romaji, 0, plen[0]));
                }
            }
        }
        return romajiString.toString();
    }

    @Override // com.nuance.swype.input.InputView
    public void handlePossibleActionAfterResize() {
        clearKeyOffsets();
    }

    @Override // com.nuance.swype.input.InputView
    public void handlePossibleActionAfterUniversalKeyboardResize(boolean hideSymbolView) {
        handlePossibleActionAfterResize();
    }

    private void cleanInlineWordSpanAndFlush() {
        if (this.mInlineWord != null && this.mInlineWord.length() > 0) {
            this.mInlineWord.setSpan(this.mWordComposeSpan, 0, this.mInlineWord.length(), R.styleable.ThemeTemplate_emojiCategoryIconSmiles);
            InputConnection ic = getCurrentInputConnection();
            if (ic != null) {
                ic.beginBatchEdit();
                ic.setComposingText(this.mInlineWord, 1);
                ic.endBatchEdit();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.InputView
    public void setSwypeKeytoolTipSuggestion() {
        timeoutWhenMultitappingCommaOrPeriod();
        if (this.candidatesListViewCJK != null) {
            this.candidatesListViewCJK.showSwypeTooltip();
            syncCandidateDisplayStyleToMode();
        }
    }

    @Override // com.nuance.swype.input.InputView
    public void setNotMatchToolTipSuggestion() {
        syncCandidateDisplayStyleToMode();
        this.candidatesListViewCJK.showNotMatchTootip();
    }

    @Override // com.nuance.swype.input.InputView
    public void handleHardkeyCharKey(int primaryCode, KeyEvent event, boolean shiftable) {
        if (primaryCode != 0) {
            int[] keyCodes = {primaryCode};
            handleCharKey(null, primaryCode, keyCodes, event != null ? event.getEventTime() : 0L);
        }
    }

    @Override // com.nuance.swype.input.InputView
    public boolean handleHardkeyBackspace(int repeatedCount) {
        if (this.mKeyboardSwitcher.isKeypadInput() && this.mInlineWord.length() > 0) {
            repeatedCount = 1;
        }
        InputConnection ic = getCurrentInputConnection();
        if (this.mJapaneseInput == null || ic == null) {
            this.mLastInput = 4;
            sendBackspace(repeatedCount);
            this.mEditState.backSpace();
        } else if (this.mConvert == 1 || this.mConvert == 2) {
            updateCandidates(Candidates.Source.INVALID);
            this.mConvert = 0;
        } else if (this.mLastInput == 2) {
            this.mConvert = 0;
            this.mLastInput = 4;
            ic.beginBatchEdit();
            ic.commitText("", 1);
            ic.endBatchEdit();
            clearAllKeys();
            clearSuggestions();
            this.mEditState.backSpace();
            if (this.mEditState.current() == 1) {
                this.mEditState.start();
            }
        } else if (isMultitapping()) {
            if (this.mJapaneseInput.getKeyCount() > 0) {
                this.mJapaneseInput.clearKey();
            }
        } else if (this.mJapaneseInput.getKeyCount() > 0) {
            this.mJapaneseInput.clearKey();
            if (this.mJapaneseInput.getKeyCount() == 0) {
                this.mLastInput = 4;
                ic.beginBatchEdit();
                ic.commitText("", 1);
                ic.endBatchEdit();
                clearAllKeys();
                clearSuggestions();
                this.mEditState.backSpace();
                if (this.mEditState.current() == 1) {
                    this.mEditState.start();
                }
            } else if (this.mJapaneseInput.getKeyCount() > 0) {
                updateCandidates(Candidates.Source.INVALID);
            } else {
                this.mLastInput = 4;
                ic.beginBatchEdit();
                ic.commitText("", 1);
                ic.endBatchEdit();
                clearAllKeys();
                clearSuggestions();
                this.mEditState.backSpace();
                if (this.mEditState.current() == 1) {
                    this.mEditState.start();
                }
            }
        } else {
            this.mLastInput = 4;
            if (this.mJapaneseInput.getKeyCount() == 0 && this.mSuggestedWord.length() > 0) {
                ic.beginBatchEdit();
                ic.commitText("", 0);
                ic.endBatchEdit();
            } else {
                sendBackspace(repeatedCount);
            }
            clearAllKeys();
            clearSuggestions();
            this.mEditState.backSpace();
        }
        return true;
    }

    @Override // com.nuance.swype.input.InputView
    public boolean handleHardkeySpace(boolean quickPressed, int repeatedCount) {
        if (this.mWordList != null && this.mWordList.size() > 0 && this.mInlineWord.length() == 0 && this.mSuggestedWordIndex.get() != -1 && this.mSuggestedWord.length() != 0) {
            return handleHardkeyEnter();
        }
        dismissSegmentationPopup();
        return handleSpace(quickPressed, repeatedCount);
    }

    @Override // com.nuance.swype.input.InputView
    public boolean handleHardkeyDelete(int repeatedCount) {
        InputConnection ic = getCurrentInputConnection();
        if (this.mJapaneseInput == null || ic == null) {
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
                clearSuggestions();
                CharSequence cSeqAfter = ic.getTextAfterCursor(1, 0);
                if (cSeqAfter != null && cSeqAfter.length() > 0) {
                    ic.beginBatchEdit();
                    ic.deleteSurroundingText(0, 1);
                    ic.endBatchEdit();
                }
                clearAllKeys();
                this.mJapaneseInput.setContext(null);
                this.mEditState.backSpace();
            }
        }
        return true;
    }

    @Override // com.nuance.swype.input.InputView
    public boolean handleHardkeyEnter() {
        if (this.mWordList != null && this.candidatesListViewCJK.wordCount() > 0 && this.mSuggestedWordIndex.get() >= 0 && this.mSuggestedWordIndex.get() < this.mWordList.size()) {
            this.mSuggestedWord.clear();
            CharSequence word = this.mWordList.get(this.mSuggestedWordIndex.get());
            if (word != null) {
                this.mSuggestedWord.append(word);
                this.candidatesListViewCJK.touchWord(this.mSuggestedWordIndex.get(), this.mSuggestedWord);
                this.candidatesListViewCJK.selectActiveWord();
            }
        } else {
            sendKeyToApplication(66);
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
        switch (keycode) {
            case 19:
            case 20:
                if (hasInputQueue()) {
                    flushCurrentActiveWord();
                    clearSuggestions();
                    clearCurrentActiveWord();
                    return false;
                }
                if (this.mWordList == null || this.mWordList.size() <= 0 || this.mInlineWord.length() != 0) {
                    return false;
                }
                if (this.mSuggestedWordIndex.get() == -1 && this.mSuggestedWord.length() == 0) {
                    return moveHightlightToCenterOrLeftRight(keycode);
                }
                clearSuggestions();
                clearCurrentActiveWord();
                return false;
            case 21:
            case 22:
                if (this.mWordList != null && this.mWordList.size() > 0 && this.mInlineWord.length() == 0) {
                    if (this.mSuggestedWordIndex.get() != -1 && this.mSuggestedWord.length() != 0) {
                        return moveHightlightToCenterOrLeftRight(keycode);
                    }
                    clearSuggestions();
                    clearCurrentActiveWord();
                    return false;
                }
                return moveHighlightToNearCandidate(keycode);
            default:
                return false;
        }
    }

    private boolean moveHighlightToNearCandidate(int keyCode) {
        if (this.mJapaneseWordListViewContainer == null || this.candidatesListViewCJK == null || this.mWordList == null || this.mSuggestedWord == null || this.mWordList.isEmpty() || this.candidatesListViewCJK.wordCount() == 0) {
            return false;
        }
        if (keyCode == 22) {
            onClick(this.rightArrowButton);
        } else if (keyCode == 21) {
            onClick(this.leftArrowButton);
        }
        return true;
    }

    private boolean moveHightlightToCenterOrLeftRight(int keyCode) {
        if (this.mJapaneseWordListViewContainer == null || this.candidatesListViewCJK == null || this.mWordList == null || this.mSuggestedWord == null || this.mWordList.isEmpty() || this.candidatesListViewCJK.wordCount() == 0) {
            return false;
        }
        if (keyCode == 20) {
            this.mSuggestedWordIndex.set(this.candidatesListViewCJK.getCenterCandidateIndex());
        } else if (keyCode == 21) {
            this.mSuggestedWordIndex.decrementAndGet();
        } else {
            if (keyCode != 22) {
                return false;
            }
            this.mSuggestedWordIndex.incrementAndGet();
        }
        this.mConvert = 0;
        if (this.mSuggestedWordIndex.get() < 0) {
            this.mSuggestedWordIndex.set(0);
        } else if (this.mSuggestedWordIndex.get() >= this.mWordList.size()) {
            this.mSuggestedWordIndex.set(this.mWordList.size() - 1);
        }
        this.mSuggestedWord.clear();
        CharSequence word = this.mWordList.get(this.mSuggestedWordIndex.get());
        if (word != null) {
            this.candidatesListViewCJK.enableHighlight();
            this.candidatesListViewCJK.touchWord(this.mSuggestedWordIndex.get(), this.mSuggestedWord);
            syncCandidateDisplayStyleToMode();
            if (keyCode == 22 && this.candidatesListViewCJK.isKeyOutofRightBound(this.mSuggestedWordIndex.get())) {
                setScrollable(true);
                this.candidatesListViewCJK.scrollNext();
            } else if (keyCode == 21 && this.candidatesListViewCJK.isKeyOutofLeftBound(this.mSuggestedWordIndex.get())) {
                setScrollable(true);
                this.candidatesListViewCJK.scrollPrev();
            }
            this.mSuggestedWord.clear();
            this.mSuggestedWord.clearSpans();
            this.mSuggestedWord.append(word);
            UnderlineSpan span = new UnderlineSpan();
            this.mSuggestedWord.setSpan(span, 0, this.mSuggestedWord.length(), R.styleable.ThemeTemplate_emojiCategoryIconSmiles);
            this.mSuggestedWord.setSpan(span, 0, this.mSuggestedWord.length(), R.styleable.ThemeTemplate_emojiCategoryIconSmiles);
            InputConnection ic = getCurrentInputConnection();
            if (ic != null) {
                ic.beginBatchEdit();
                ic.setComposingText(this.mSuggestedWord, 1);
                ic.endBatchEdit();
            }
            setScrollable(false);
        }
        return true;
    }

    private boolean hasInputQueue() {
        return this.mJapaneseInput != null && this.mJapaneseInput.getKeyCount() > 0 && this.mInlineWord != null && this.mInlineWord.length() > 0;
    }

    protected void sendHardKeyChar(char character) {
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

    @Override // com.nuance.swype.input.chinese.CJKCandidatesListView.OnWordSelectActionListener
    public void onScrollContentChanged() {
        if (this.mJapaneseWordListViewContainer != null) {
            this.mJapaneseWordListViewContainer.updateScrollArrowVisibility();
        }
    }

    @Override // com.nuance.swype.input.InputView, com.nuance.swype.input.KeyboardViewEx, com.nuance.swype.input.view.InputLayout.DragListener
    public void onBeginDrag() {
        super.onBeginDrag();
        clearKeyOffsets();
    }

    @Override // com.nuance.swype.input.KeyboardViewEx, com.nuance.swype.input.view.InputLayout.DragListener
    public void onDrag(int dx, int dy) {
        super.onDrag(dx, dy);
        updateSegemetationPopup(dx, dy);
    }

    @Override // com.nuance.swype.input.KeyboardViewEx, com.nuance.swype.input.view.InputLayout.DragListener
    public void onEndDrag() {
        super.onEndDrag();
        if (this.JpInputViewHandler.hasMessages(22)) {
            this.JpInputViewHandler.removeMessages(22);
        }
        this.JpInputViewHandler.sendEmptyMessageDelayed(22, 5L);
    }

    @Override // com.nuance.swype.input.KeyboardViewEx, com.nuance.swype.input.view.InputLayout.DragListener
    public void onSnapToEdge(int dx, int dy) {
        super.onSnapToEdge(dx, dy);
        if (this.JpInputViewHandler.hasMessages(22)) {
            this.JpInputViewHandler.removeMessages(22);
        }
        this.JpInputViewHandler.sendEmptyMessageDelayed(22, 5L);
    }

    @Override // com.nuance.swype.input.InputView
    public boolean isInputSessionStarted() {
        return !this.JpInputViewHandler.hasMessages(15);
    }

    private List<CharSequence> getWords(SpannableStringBuilder defaultWord, AtomicInteger defaultWordIndex) {
        int[] scratchDefaultWordIndex = new int[1];
        List<CharSequence> list = this.mJapaneseInput.getWords(this.mSuggestedWord, scratchDefaultWordIndex, 32);
        defaultWordIndex.set(scratchDefaultWordIndex[0]);
        return list;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.InputView
    public void setLanguage(XT9CoreInput coreInput) {
        int newLanguageId;
        if (this.mKeyboardSwitcher.isKeypadInput()) {
            newLanguageId = (this.mCurrentInputLanguage.getCoreLanguageId() & 255) | 1280;
        } else {
            newLanguageId = (this.mCurrentInputLanguage.getCoreLanguageId() & 255) | 256;
        }
        coreInput.setLanguage(newLanguageId);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleFunctionKey(int keyCode, boolean quickPress, int repeatCount) {
        if ((shouldHandleKeyViaIME(keyCode) || !handleKey(keyCode, quickPress, repeatCount)) && !handleGesture(keyCode)) {
            this.mIme.onKey(null, keyCode, null, null, 0L);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class InputContextRequestHandler implements InputContextRequestDispatcher.InputContextRequestHandler {
        private final char[] phantomCapTextBuffer;

        private InputContextRequestHandler() {
            this.phantomCapTextBuffer = new char[]{'x', ' '};
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean addExplicitSymbol(char[] symbols, Shift.ShiftState shiftState) {
            return JapaneseInputView.this.mJapaneseInput.addExplicit(symbols, symbols.length, shiftState);
        }

        private char[] getTextBeforeCursor(int maxBufferLen) {
            InputConnection ic = JapaneseInputView.this.getCurrentInputConnection();
            CharSequence textBeforeCursor = ic != null ? ic.getTextBeforeCursor(maxBufferLen, 0) : null;
            if (textBeforeCursor != null) {
                return textBeforeCursor.toString().toCharArray();
            }
            return null;
        }

        @Override // com.nuance.swype.input.keyboard.InputContextRequestDispatcher.InputContextRequestHandler
        public char[] getContextBuffer(int maxBufferLen) {
            return getTextBeforeCursor(maxBufferLen);
        }

        @Override // com.nuance.swype.input.keyboard.InputContextRequestDispatcher.InputContextRequestHandler
        public char[] getAutoCapitalizationTextBuffer(int maxBufferLen) {
            return this.phantomCapTextBuffer;
        }

        @Override // com.nuance.swype.input.keyboard.InputContextRequestDispatcher.InputContextRequestHandler
        public boolean autoAccept(boolean addSeparator) {
            StringBuffer selectWord = new StringBuffer();
            if (JapaneseInputView.this.mWordList != null) {
                selectWord.append(JapaneseInputView.this.mInlineWord.toString());
                JapaneseInputView.this.getCurrentInputConnection().commitText(selectWord, 1);
                JapaneseInputView.this.mJapaneseInput.wordSelected(JapaneseInputView.this.indexOfInline(JapaneseInputView.this.mInlineWord.toString()), false);
                JapaneseInputView.this.mInlineWord.clear();
                JapaneseInputView.this.clearAllKeys();
                JapaneseInputView.this.clearSuggestions();
            }
            JapaneseInputView.this.mLastInput = 4;
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class TouchKeyActionHandler implements KeyboardTouchEventDispatcher.TouchKeyActionHandler {
        final char[] functionKey;
        private final SparseIntArray keyIndices;
        private boolean touchCanceled;

        private TouchKeyActionHandler() {
            this.keyIndices = new SparseIntArray();
            this.functionKey = new char[1];
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean processStoredTouch() {
            if (JapaneseInputView.this.isValidBuild()) {
                return JapaneseInputView.this.mJapaneseInput.processStoredTouch(-1, this.functionKey);
            }
            return false;
        }

        private boolean handleTouchActionFlickr(int pointerId, int keyIndex, float[] xcoords, float[] ycoords, boolean canBeTraced) {
            int dir;
            if (!JapaneseInputView.this.isKeypadFlickrInputSupported || !JapaneseInputView.this.isValidFlickerIndex() || pointerId != 0 || !JapaneseInputView.this.isKeypadInput()) {
                return false;
            }
            if (!JapaneseInputView.this.isTraceInputEnabled() && !JapaneseInputView.this.mJapaneseNavPopup.isShowing()) {
                return false;
            }
            int[] keys = JapaneseInputView.this.getKeyboard().getNearestKeys((int) xcoords[0], (int) ycoords[0]);
            if (keys.length == 0) {
                return false;
            }
            int hLen = (int) (xcoords[xcoords.length - 1] - xcoords[0]);
            int vLen = (int) (ycoords[ycoords.length - 1] - ycoords[0]);
            if (JapaneseInputView.this.japaneseFlickerKeyRelativeIndex == 2 && JapaneseInputView.this.isTopKeyFlicerUp && Math.abs(ycoords[ycoords.length - 1]) > 0.8d * JapaneseInputView.this.flickerMiddleKeyRadius && keys[0] <= 3 && keys[0] > 0) {
                JapaneseInputView.log.e("current index is 0 when flick from 2");
                dir = 0;
            } else {
                dir = JapaneseInputView.this.calculateDirectionFlickerPopup(vLen, hLen);
            }
            JapaneseInputView.log.d("current index is=" + dir + " current keys[0]:" + keys[0]);
            JapaneseInputView.log.d("y value of the end of path:" + ycoords[ycoords.length - 1]);
            if (dir != 4 && vLen < 0 && keys[0] <= 3 && keys[0] >= 0 && ycoords[ycoords.length - 1] == 0.0f) {
                dir = 2;
                JapaneseInputView.log.d("current index = 2 when flick from 0");
                JapaneseInputView.this.isTopKeyFlicerUp = true;
            }
            JapaneseInputView.this.japaneseFlickerKeyRelativeIndex = dir;
            JapaneseInputView.this.japaneseFlickerKeyIndex = JapaneseInputView.this.mCurrentKey;
            if (dir == 0 && JapaneseInputView.this.isTopKeyFlicerUp) {
                JapaneseInputView.this.isTopKeyFlicerUp = false;
            }
            JapaneseInputView.this.showNav(dir);
            return true;
        }

        @Override // com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher.TouchKeyActionHandler
        public void touchStarted(int pointerId, int keyIndex, KeyType keyType, float x, float y, int eventTime) {
            JapaneseInputView.this.pressKey(JapaneseInputView.this.mKeys, keyIndex);
            JapaneseInputView.this.setKeyState(keyIndex, KeyboardViewEx.ShowKeyState.Pressed);
            JapaneseInputView.this.showPreviewKey(keyIndex, pointerId);
            JapaneseInputView.this.resetTrace(pointerId);
            JapaneseInputView.this.resetFlickerIndex();
            this.keyIndices.put(pointerId, keyIndex);
            this.touchCanceled = false;
        }

        @Override // com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher.TouchKeyActionHandler
        public void touchMoved(int pointerId, int keyIndex, float[] xcoords, float[] ycoords, int[] times, boolean canBeTraced) {
            if (canBeTraced || keyIndex != this.keyIndices.get(pointerId)) {
                JapaneseInputView.this.releaseKey(JapaneseInputView.this.mKeys, this.keyIndices.get(pointerId), false);
                JapaneseInputView.this.hideKeyPreview(pointerId);
                JapaneseInputView.this.setKeyState(-1, KeyboardViewEx.ShowKeyState.Released);
                this.keyIndices.put(pointerId, keyIndex);
            }
            JapaneseInputView.this.onTouchMoved(pointerId, xcoords, ycoords, times, canBeTraced, keyIndex);
        }

        @Override // com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher.TouchKeyActionHandler
        public void touchEnded(int pointerId, int keyIndex, KeyType keyType, boolean isTraced, boolean quickPressed, float x, float y, int eventTime) {
            JapaneseInputView.this.releaseKey(JapaneseInputView.this.mKeys, this.keyIndices.get(pointerId, keyIndex), true);
            JapaneseInputView.this.hideKeyPreview(pointerId);
            JapaneseInputView.this.setKeyState(-1, KeyboardViewEx.ShowKeyState.Released);
            if (!this.touchCanceled) {
                if (quickPressed && keyType.isFunctionKey()) {
                    quickPress(pointerId, keyIndex, keyType);
                } else {
                    KeyboardEx.Key key = JapaneseInputView.this.getKey(keyIndex);
                    if (isTraced) {
                        JapaneseInputView.this.handleTrace(null);
                    } else if (keyType.isFunctionKey()) {
                        JapaneseInputView.this.handleFunctionKey(key.codes[0], false, 0);
                    } else if (keyType.isString() || JapaneseInputView.this.getKeycode(key) == 4063) {
                        JapaneseInputView.this.onText(JapaneseInputView.this.getText(key), 0L);
                    } else {
                        if (JapaneseInputView.this.mKeyboardSwitcher.isAlphabetMode()) {
                            if (!keyType.isLetter() || JapaneseInputView.this.mJapaneseInput.getKeyCount() >= 32) {
                                if (JapaneseInputView.this.isKeypadMultitapInputSupported() && JapaneseInputView.this.isCommaOrPeriod(key.codes[0])) {
                                    if (processStoredTouch()) {
                                        StringBuilder exact = new StringBuilder();
                                        JapaneseInputView.this.mJapaneseInput.getExactType(exact);
                                        JapaneseInputView.this.handleCharKey(null, exact.charAt(exact.length() - 1), null, 0L);
                                    } else {
                                        JapaneseInputView.this.handleCharKey(null, JapaneseInputView.this.getKeycode(key), null, 0L);
                                    }
                                }
                            } else {
                                if (JapaneseInputView.this.isMultitapping() && JapaneseInputView.this.mInlineWord.length() == 1 && JapaneseInputView.this.isCommaOrPeriod(JapaneseInputView.this.mInlineWord.charAt(JapaneseInputView.this.mInlineWord.length() - 1))) {
                                    JapaneseInputView.this.mJapaneseInput.multiTapTimeOut();
                                    JapaneseInputView.this.onMultitapTimeout();
                                } else if (JapaneseInputView.this.isConverting()) {
                                    JapaneseInputView.this.flushCurrWordAndClearWCL();
                                }
                                if (processStoredTouch()) {
                                    JapaneseInputView.this.updateCandidates(Candidates.Source.TAP);
                                } else {
                                    JapaneseInputView.this.handleCharKey(null, JapaneseInputView.this.getKeycode(key), null, 0L);
                                }
                            }
                        }
                        JapaneseInputView.this.handleCharKey(null, JapaneseInputView.this.getKeycode(key), null, 0L);
                    }
                }
                JapaneseInputView.this.mLastInput = isTraced ? 2 : 1;
            }
            this.keyIndices.put(pointerId, -1);
            if (isTraced) {
                JapaneseInputView.this.finishTrace(pointerId);
            }
        }

        @Override // com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher.TouchKeyActionHandler
        public void touchCanceled(int pointerId, int keyIndex) {
            this.touchCanceled = true;
            JapaneseInputView.this.isTopKeyFlicerUp = false;
            JapaneseInputView.this.resetTrace(pointerId);
            if (keyIndex == -1) {
                JapaneseInputView.this.showNav(-1);
                if (this.keyIndices.get(pointerId, -1) != -1) {
                    JapaneseInputView.this.releaseKey(JapaneseInputView.this.mKeys, this.keyIndices.get(pointerId), true);
                }
                JapaneseInputView.this.hideKeyPreview(pointerId);
                JapaneseInputView.this.setKeyState(-1, KeyboardViewEx.ShowKeyState.Released);
            }
        }

        @Override // com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher.TouchKeyActionHandler
        public boolean touchHeld(int pointerId, int keyIndex, KeyType keyType) {
            KeyboardEx.Key key = JapaneseInputView.this.getKey(keyIndex);
            if (!JapaneseInputView.this.mKeyboardSwitcher.isKeypadInput() || key == null || key.codes.length <= 0 || key.codes[0] <= 12352 || key.codes[0] > JapaneseInputView.JP_KEYPAD_LONG_VOWEL_KEY) {
                return JapaneseInputView.this.onShortPress(JapaneseInputView.this.getKey(keyIndex), keyIndex, pointerId);
            }
            if (JapaneseInputView.this.isKeypadFlickrInputSupported) {
                JapaneseInputView.this.mCurrentKey = keyIndex;
                JapaneseInputView.this.japaneseFlickerKeyRelativeIndex = 0;
                JapaneseInputView.this.japaneseFlickerKeyIndex = JapaneseInputView.this.mCurrentKey;
                JapaneseInputView.this.showNav(0);
            }
            return true;
        }

        @Override // com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher.TouchKeyActionHandler
        public boolean touchHeldRepeat(int pointerId, int keyIndex, KeyType keyType, int repeatCount) {
            if (JapaneseInputView.this.mIme == null || !keyType.isFunctionKey()) {
                return false;
            }
            KeyboardEx.Key key = JapaneseInputView.this.getKey(keyIndex);
            if (key.codes[0] == 8) {
                return JapaneseInputView.this.handleBackspace(repeatCount);
            }
            if (!KeyboardEx.isArrowKeys(key.codes[0])) {
                return false;
            }
            JapaneseInputView.this.handleFunctionKey(key.codes[0], false, repeatCount);
            return true;
        }

        public void quickPress(int pointerId, int keyIndex, KeyType keyType) {
            if (keyType.isFunctionKey()) {
                KeyboardEx.Key key = JapaneseInputView.this.getKey(keyIndex);
                JapaneseInputView.this.handleKey(key.codes[0], true, 0);
            }
        }

        @Override // com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher.TouchKeyActionHandler
        public void touchHeldMove(int pointerId, int keyIndex, float[] xcoords, float[] ycoords, int[] times) {
            if (!handleTouchActionFlickr(pointerId, keyIndex, xcoords, ycoords, false)) {
                JapaneseInputView.this.onTouchHeldMoved(pointerId, xcoords, ycoords, times);
            }
        }

        @Override // com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher.TouchKeyActionHandler
        public void touchHeldEnded(int pointerId, int keyIndex, KeyType keyType) {
            JapaneseInputView.this.onTouchHeldEnded(pointerId, JapaneseInputView.this.getKey(keyIndex));
            JapaneseInputView.this.releaseKey(JapaneseInputView.this.mKeys, keyIndex, true);
            JapaneseInputView.this.hideKeyPreview(pointerId);
            JapaneseInputView.this.setKeyState(-1, KeyboardViewEx.ShowKeyState.Released);
        }

        @Override // com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher.TouchKeyActionHandler
        public void touchHelpRepeatEnded(int pointerId, int keyIndex, KeyType keyType) {
            JapaneseInputView.this.releaseKey(JapaneseInputView.this.mKeys, keyIndex, true);
            JapaneseInputView.this.mIme.releaseRepeatKey();
            JapaneseInputView.this.hideKeyPreview(pointerId);
            JapaneseInputView.this.setKeyState(-1, KeyboardViewEx.ShowKeyState.Released);
        }

        @Override // com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher.TouchKeyActionHandler
        public void multiTapTimerTimeoutActive() {
            JapaneseInputView.this.mInMultiTap = true;
        }

        @Override // com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher.TouchKeyActionHandler
        public void multiTapTimerTimeOut() {
            JapaneseInputView.this.onMultitapTimeout();
            JapaneseInputView.this.mInMultiTap = false;
        }

        @Override // com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher.TouchKeyActionHandler
        public void handleKeyboardShiftState(float x) {
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.KeyboardViewEx
    public void onTouchHeldEnded(int pointerId, KeyboardEx.Key key) {
        CharSequence charSequence;
        JapaneseInputView japaneseInputView;
        if (this.mKeyboardSwitcher.isKeypadInput() && isValidFlickerIndex()) {
            handleFlickerPopup();
            showNav(-1);
            resetFlickerIndex();
            return;
        }
        if (!touchUpHandleBySlideSelectPopup(this.mHshPointers.get(pointerId), null)) {
            if (this.mKeyboardSwitcher.isKeypadInput() && this.mKeyboardSwitcher.isAlphabetMode()) {
                if (!this.isKeypadFlickrInputSupported && key != null && key.codes.length > 0 && key.codes[0] > 12352 && key.codes[0] <= JP_KEYPAD_LONG_VOWEL_KEY) {
                    notifyKeyboardListenerOnKey(null, key.codes[0], null, null, 0L);
                }
            } else {
                if (isShifted()) {
                    charSequence = key.shiftedLabel;
                    japaneseInputView = this;
                } else if (key.altLabel != null) {
                    charSequence = key.altLabel;
                    japaneseInputView = this;
                } else {
                    charSequence = key.label;
                    japaneseInputView = this;
                }
                japaneseInputView.notifyKeyboardListenerOnText(charSequence, 0L);
            }
        }
        dismissPopupKeyboard();
        dismissSingleAltCharPopup();
        dismissPreviewPopup();
    }
}
