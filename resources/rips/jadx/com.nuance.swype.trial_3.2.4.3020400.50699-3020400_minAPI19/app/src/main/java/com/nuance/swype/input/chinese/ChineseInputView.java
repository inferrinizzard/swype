package com.nuance.swype.input.chinese;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
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
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputConnection;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.nuance.android.util.WeakReferenceHandler;
import com.nuance.connect.api.ChinesePredictionService;
import com.nuance.connect.common.Integers;
import com.nuance.dlm.ACCoreInputDLM;
import com.nuance.input.swypecorelib.Candidates;
import com.nuance.input.swypecorelib.KeyType;
import com.nuance.input.swypecorelib.Shift;
import com.nuance.input.swypecorelib.T9Write;
import com.nuance.input.swypecorelib.T9WriteRecognizerListener;
import com.nuance.input.swypecorelib.XT9CoreChineseInput;
import com.nuance.input.swypecorelib.XT9CoreInput;
import com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.xmlResults.XMLResultsHandler;
import com.nuance.swype.connect.Connect;
import com.nuance.swype.connect.ConnectLegal;
import com.nuance.swype.input.AppPreferences;
import com.nuance.swype.input.CandidatesListView;
import com.nuance.swype.input.CategoryDBList;
import com.nuance.swype.input.FunctionBarListView;
import com.nuance.swype.input.IME;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.InputFieldInfo;
import com.nuance.swype.input.InputMethods;
import com.nuance.swype.input.InputView;
import com.nuance.swype.input.KeyboardEx;
import com.nuance.swype.input.KeyboardViewEx;
import com.nuance.swype.input.PlatformUtil;
import com.nuance.swype.input.QuickToast;
import com.nuance.swype.input.R;
import com.nuance.swype.input.SpeechWrapper;
import com.nuance.swype.input.UserPreferences;
import com.nuance.swype.input.XT9Keyboard;
import com.nuance.swype.input.appspecific.AppSpecificInputConnection;
import com.nuance.swype.input.chinese.CJKCandidatesListView;
import com.nuance.swype.input.chinese.ChineseCloudPrediction;
import com.nuance.swype.input.chinese.symbol.SymbolInputController;
import com.nuance.swype.input.emoji.EmojiInfo;
import com.nuance.swype.input.emoji.EmojiLoader;
import com.nuance.swype.input.keyboard.InputContextRequestDispatcher;
import com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher;
import com.nuance.swype.input.settings.ThemesPrefs;
import com.nuance.swype.input.view.InputContainerView;
import com.nuance.swype.plugin.TypedArrayWrapper;
import com.nuance.swype.stats.StatisticsEnabledEditState;
import com.nuance.swype.stats.UsageManager;
import com.nuance.swype.util.CharacterUtilities;
import com.nuance.swype.util.ContactUtils;
import com.nuance.swype.util.LogManager;
import com.nuance.swype.util.RightAlignedHorizontalScrollView;
import com.nuance.swype.widget.CustomHorizontalScrollView;
import com.nuance.swypeconnect.ac.ACChinesePredictionService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

/* loaded from: classes.dex */
public class ChineseInputView extends InputView implements T9WriteRecognizerListener.OnWriteRecognizerListener, CJKCandidatesListView.OnWordSelectActionListener, ChineseCloudPrediction.ChinesePredictionListener, CustomHorizontalScrollView.ScrollListener {
    private static final int CHINESE_BILINGUAL_ACTIVE_INDEX = 254;
    private static final int CLOUD_PREDICTION_SCROLL_OFFSET = 20;
    public static final char COMPONENT_MARKER = 40959;
    private static final int DELAY_SENDKEY = 100;
    private static final int GESTURE_CANDIDATE = -1;
    protected static final int MSG_DELAY_REFRESH_GRID_TABLE_PREFIX = 500;
    private static final int PRIMARY_KEYCODE_NUM_NINE_KEYPAD = 87;
    private static final int PRIMARY_KEYCODE_NUM_NINE_KEYPAD_ALT_CHARACTER = 57;
    private static final int PRIMARY_KEYCODE_NUM_ONE_KEYPAD = 49;
    private static final int PRIMARY_KEYCODE_NUM_TWO_KEYPAD = 65;
    private static final int PRIMARY_KEYCODE_NUM_TWO_KEYPAD_ALT_CHARACTER = 50;
    private static final int PRIMARY_KEYCODE_NUM_ZERO_KEYPAD = 48;
    private static final int STROKE_KEYCODE_NUM_FOUR = 20022;
    private static final int STROKE_KEYCODE_NUM_TWO = 20008;
    private static final LogManager.Log log = LogManager.getLog("ChineseInputView");
    private StringBuilder altCharacterText;
    private View candidatesPopup;
    private List<CharSequence> candidatesPopupDataList;
    private ChineseKeyboardViewEx candidatesPopupKeyboardViewEx;
    private ScrollView candidatesPopupScrollView;
    private View chinesePredictionView;
    private List<CharSequence> chineseSymbolList;
    private ImageView cloudImage;
    private Rect cloudRec;
    private CustomHorizontalScrollView cloudScrollView;
    private TextView cloudText;
    private List<CharSequence> conversionHistory;
    private List<CharSequence> convertedCharacterList;
    private InputMethods.InputMode currentInputMode;
    private ChinesePredictionService.ChinesePrediction currentPredictionResult;
    protected StringBuilder defaultHighlightWord;
    private List<AtomicInteger> delimiterPlaceholderIndexList;
    private AtomicInteger doublePinyinTailKeyUnicode;
    private ChinesePredictionService.ChinesePrediction duplicatePredictionResult;
    private StringBuilder exactInputText;
    private boolean gridViewFunctionButtonPressed;
    private View inlineContainerView;
    private RightAlignedHorizontalScrollView inlineScrollView;
    private InputContextRequestDispatcher.InputContextRequestHandler inputContextRequestHandler;
    Handler.Callback inputViewCallback;
    Handler inputViewHandler;
    private boolean isCandidateSelectedEver;
    private boolean isCloudAnimationStarted;
    private boolean isCloudPredictionAllowed;
    private boolean isDealGesture;
    private boolean isDelimiterPressedEver;
    private boolean isPressedNumOneKey;
    private boolean isShiftedPopupMenu;
    private boolean isSourceFromPressSpace;
    private boolean isTracedEver;
    private boolean lastKeypadBilingualState;
    private ChineseCloudPrediction mChineseCloudPrediction;
    private XT9CoreChineseInput mChineseInput;
    private ACChinesePredictionService.ACChinesePredictionResult mChinesePredictionResult;
    private SpellPhraseViewContainer mChnPinyinAndPhraseListViewContainer;
    private boolean mDealingBackspace;
    private AtomicInteger mDefaultPrefixIndex;
    protected SpannableStringBuilder mDefaultWord;
    private AtomicInteger mDefaultWordIndex;
    private BackgroundColorSpan mErrorFlashBackground;
    private StringBuffer mExactWord;
    private FunctionBarListView mFunctionBarListView;
    private ChineseGetMoreWordsHandler mGetMoreWordsHandler;
    HandWritingOnKeyboardHandler mHandWritingOnKeyboardHandler;
    private boolean mHandwritingOn;
    private boolean mInitiativeAccept;
    public StringBuilder mInsertText;
    private ForegroundColorSpan mInvalidForeground;
    private boolean mIsIMEActive;
    private boolean mIsUpdateInline;
    protected ChineseOneStepPYContainer mOneStepPYContainer;
    private char mPYDelimiter;
    private List<CharSequence> mPinyinCandidates;
    private StringBuilder mPredictionSpell;
    protected List<ArrayList<KeyboardEx.GridKeyInfo>> mRows;
    private String mSelectWord;
    private StringBuilder mSelection;
    private boolean mTerminateSession;
    private Paint mTextMeasurePaint;
    private boolean mTone;
    private boolean mTraceOn;
    private UnderlineSpan mUnderline;
    private boolean mbNeedDefer;
    private boolean mbNeedUpdate;
    private List<AtomicInteger> numZeroPlaceholderIndexList;
    private byte[] requestCloudDataCompare;
    private List<CharSequence> showDataList;
    private SpellListView spellPrefixSuffixListView;
    private SymbolInputController symInputController;
    private TextView topInlineView;
    private TouchKeyActionHandler touchKeyActionHandler;
    private Drawable wclCjkChineseCloudBackgroundHighlight;
    private Drawable wclCjkChineseCloudBackgroundImage;

    /* loaded from: classes.dex */
    private static class ChineseGetMoreWordsHandler implements CJKCandidatesListView.GetMoreWordsHandler {
        ChineseInputView mCIV;

        ChineseGetMoreWordsHandler(ChineseInputView civ) {
            this.mCIV = civ;
        }

        @Override // com.nuance.swype.input.chinese.CJKCandidatesListView.GetMoreWordsHandler
        public void requestMoreWords() {
            this.mCIV.postAddMoreSuggestions();
        }
    }

    @Override // com.nuance.swype.input.InputView
    @SuppressLint({"PrivateResource"})
    public void create(IME ime, XT9CoreInput xt9coreinput, T9Write t9write, SpeechWrapper speechWrapper) {
        super.create(ime, xt9coreinput, t9write, speechWrapper);
        this.mChineseInput = (XT9CoreChineseInput) xt9coreinput;
        if (this.mHandWritingOnKeyboardHandler == null) {
            this.mHandWritingOnKeyboardHandler = new HandWritingOnKeyboardHandler(ime, this);
        }
        this.mHandWritingOnKeyboardHandler.onCreate(t9write);
        this.mKeyboardSwitcher = new ChineseKeyboardSwitcher(ime, this.mChineseInput);
        this.mKeyboardSwitcher.setInputView(this);
        this.mGetMoreWordsHandler = new ChineseGetMoreWordsHandler(this);
        setOnKeyboardActionListener(ime);
        setProximityCorrectionEnabled(true);
        this.mPYDelimiter = (char) getContext().getApplicationContext().getResources().getInteger(R.integer.chinese_delimiter);
        readStyles(getContext());
        initialSymbolList();
        this.mChineseCloudPrediction = ChineseCloudPrediction.getInstance(getContext());
        this.mChineseCloudPrediction.setChinesePredictionListener(this);
        ACCoreInputDLM.initializeACChineseDLM(Connect.from(getContext()));
    }

    @SuppressLint({"PrivateResource"})
    private void initialSymbolList() {
        String[] chinese_symbol = getResources().getStringArray(R.array.chinese_symbol);
        Collections.addAll(this.chineseSymbolList, chinese_symbol);
    }

    @Override // com.nuance.swype.input.InputView
    public void destroy() {
        if (this.symInputController != null) {
            this.symInputController.hide();
            this.symInputController = null;
        }
        super.destroy();
        ACCoreInputDLM.destroyACChineseDLM();
        if (this.mHandWritingOnKeyboardHandler != null) {
            this.mHandWritingOnKeyboardHandler.onDestroy();
            this.mHandWritingOnKeyboardHandler = null;
        }
        this.mKeyboardSwitcher = null;
        ContactUtils.cancelQueryTask();
        this.mChineseCloudPrediction.destroy();
    }

    @Override // com.nuance.swype.input.InputView
    public XT9CoreInput getXT9CoreInput() {
        return this.mChineseInput;
    }

    @Override // com.nuance.swype.input.InputView
    @SuppressLint({"InflateParams", "PrivateResource"})
    public View createCandidatesView(CandidatesListView.CandidateListener onSelectListener) {
        if (this.mChineseInput == null) {
            return null;
        }
        if (this.mChnPinyinAndPhraseListViewContainer == null) {
            LayoutInflater inflater = IMEApplication.from(this.mIme).getThemedLayoutInflater(this.mIme.getLayoutInflater());
            IMEApplication.from(this.mIme).getThemeLoader().setLayoutInflaterFactory(inflater);
            this.mChnPinyinAndPhraseListViewContainer = (SpellPhraseViewContainer) inflater.inflate(R.layout.chinese_candidates, (ViewGroup) null);
            this.mChnPinyinAndPhraseListViewContainer.setKeyboardViewEx(this);
            IMEApplication.from(this.mIme).getThemeLoader().applyTheme(this.mChnPinyinAndPhraseListViewContainer);
            this.mChnPinyinAndPhraseListViewContainer.initViews();
            this.candidatesListViewCJK = this.mChnPinyinAndPhraseListViewContainer.getCJKCandidatesListView();
            this.spellPrefixSuffixListView = (SpellListView) this.mChnPinyinAndPhraseListViewContainer.getSpellPrefixSuffixWordListView();
            this.candidatesListViewCJK.setOnWordSelectActionListener(this);
            this.mFunctionBarListView = (FunctionBarListView) this.mChnPinyinAndPhraseListViewContainer.getFunctionBarListView();
            this.mFunctionBarListView.setOnFunctionBarListener(new DefaultChineseFunctionBarHandler(this.mIme, this) { // from class: com.nuance.swype.input.chinese.ChineseInputView.2
                @Override // com.nuance.swype.input.chinese.DefaultChineseFunctionBarHandler
                protected void switchHWOnKeyboard() {
                    ChineseInputView.this.switchHandWritingOrTraceOnKeyboard();
                }
            });
            this.candidatesListViewCJK.setGetMoreWordsHandler(this.mGetMoreWordsHandler);
            this.spellPrefixSuffixListView.setOnWordSelectActionListener(this);
        }
        this.mChnPinyinAndPhraseListViewContainer.hideSpellPrefixSuffixList();
        this.mChnPinyinAndPhraseListViewContainer.hidePhraseListView();
        this.mChnPinyinAndPhraseListViewContainer.clear();
        this.wordListViewContainerCJK = this.mChnPinyinAndPhraseListViewContainer;
        return this.mChnPinyinAndPhraseListViewContainer;
    }

    @Override // com.nuance.swype.input.InputView
    public View getWordCandidateListContainer() {
        return this.mChnPinyinAndPhraseListViewContainer;
    }

    @Override // com.nuance.swype.input.InputView
    public void startInput(InputFieldInfo inputFieldInfo, boolean restarting) {
        InputContainerView containerView;
        if (this.mChineseInput != null) {
            this.keyboardTouchEventDispatcher.resisterTouchKeyHandler(this.touchKeyActionHandler);
            this.keyboardTouchEventDispatcher.registerFlingGestureHandler(this.flingGestureListener);
            this.keyboardTouchEventDispatcher.wrapTouchEvent(this);
            this.touchKeyActionHandler.clearAllKeys();
            this.mChineseInput.setInputContextRequestListener(InputContextRequestDispatcher.getDispatcherInstance().setHandler(this.inputContextRequestHandler));
            if (this.candidatesListViewCJK != null) {
                this.candidatesListViewCJK.updateCandidatesSize();
            }
            if (this.spellPrefixSuffixListView != null) {
                this.spellPrefixSuffixListView.updateCandidatesSize();
            }
            if (this.mOneStepPYContainer != null && this.mOneStepPYContainer.getVerContainer() != null) {
                this.mOneStepPYContainer.getVerContainer().updateCandidatesSize();
            }
            this.mChineseInput.finishSession();
            if (this.mPinyinCandidates != null) {
                this.mPinyinCandidates.clear();
            }
            this.conversionHistory.clear();
            if (this.mChnPinyinAndPhraseListViewContainer != null) {
                this.mChnPinyinAndPhraseListViewContainer.clear();
                this.mChnPinyinAndPhraseListViewContainer.showLeftArrow(false);
            }
            this.inputViewHandler.removeMessages(1);
            this.inputViewHandler.removeMessages(4);
            this.mIsIMEActive = true;
            super.startInput(inputFieldInfo, restarting);
            this.mCompletionOn = false;
            this.mDealingBackspace = false;
            this.mFunctionBarListView.recycleBitmap();
            this.mShowFunctionBar = UserPreferences.from(getContext()).getShowFunctionBar();
            dismissPopupKeyboard();
            this.mChineseInput.startSession();
            clearInternalStatus();
            if (checkCurLanguage()) {
                if (this.candidatesListViewCJK != null && this.candidatesListViewCJK.getAltCharacterConverted()) {
                    this.candidatesListViewCJK.setAltCharacterConverted(false);
                }
                KeyboardEx.KeyboardLayerType currentLayer = this.mKeyboardSwitcher.currentKeyboardLayer();
                this.currentInputMode = this.mCurrentInputLanguage.getCurrentInputMode();
                this.isKeepingKeyboard = restarting && currentLayer != KeyboardEx.KeyboardLayerType.KEYBOARD_INVALID;
                if (this.isKeepingKeyboard) {
                    this.mKeyboardSwitcher.createKeyboardForTextInput(currentLayer, inputFieldInfo, this.currentInputMode);
                } else {
                    this.mKeyboardSwitcher.createKeyboardForTextInput(inputFieldInfo, this.currentInputMode);
                }
                this.inputViewHandler.removeMessages(15);
                this.inputViewHandler.sendMessageDelayed(this.inputViewHandler.obtainMessage(15, inputFieldInfo.isNameField() ? 1 : 0, this.isKeepingKeyboard ? 1 : 0), 5L);
                detectCurrentHandwritingOn();
                if (this.mHandWritingOnKeyboardHandler != null) {
                    this.mHandWritingOnKeyboardHandler.setHandwritingOn(this.mHandwritingOn);
                }
                if (this.mHandWritingOnKeyboardHandler != null) {
                    this.mHandWritingOnKeyboardHandler.onStartInput();
                }
                if (this.mShowFunctionBar) {
                    this.mFunctionBarListView.recycleBitmap();
                    addFunctionBarItem();
                }
                if (this.spellPrefixSuffixListView != null) {
                    this.spellPrefixSuffixListView.disableHighlight();
                }
                if (inputFieldInfo.isNumericModeField() || inputFieldInfo.isPhoneNumberField()) {
                    setCandidatesViewShown(false);
                } else {
                    setCandidatesViewShown(true);
                    this.inputViewHandler.sendMessageDelayed(this.inputViewHandler.obtainMessage(4, 1, 0), 5L);
                }
                hideGridCandidatesView();
                if (inputFieldInfo.isPasswordField()) {
                    triggerPasswordTip();
                }
                postDelayResumeSpeech();
                if ((this.mKeyboardSwitcher.isKeypadInput() || isModeStroke()) && isNormalTextInputMode()) {
                    showPinyinSelectionList(this.chineseSymbolList, -1, false);
                } else {
                    this.mOneStepPYContainer.hideSymbolList();
                }
                this.mKeyboardSwitcher.setShiftState(Shift.ShiftState.OFF);
                this.isCloudAnimationStarted = false;
                this.isCloudPredictionAllowed = allowChinesePrediction();
                if (this.symInputController != null) {
                    this.symInputController.hide();
                }
                if (this.mIme != null && (containerView = this.mIme.getInputContainerView()) != null) {
                    containerView.hideWidgetView(this.chinesePredictionView);
                    containerView.hideWidgetView(this.inlineContainerView);
                }
                if (!isValidBuild()) {
                    this.mChnPinyinAndPhraseListViewContainer.hideFunctionBarListView();
                    this.mChnPinyinAndPhraseListViewContainer.hideSpellPrefixSuffixList();
                    this.mChnPinyinAndPhraseListViewContainer.showPhraseListView();
                }
                showTrialExpireWclMessage("CJK");
                showUserThemeWclMessage(this.inputViewHandler);
            }
        }
    }

    private void postDelayUpdateInlineString() {
        if (this.inputViewHandler.hasMessages(17)) {
            this.inputViewHandler.removeMessages(17);
        }
        this.inputViewHandler.sendEmptyMessageDelayed(17, 1L);
    }

    private void postDelayResumeSpeech() {
        if (this.inputViewHandler.hasMessages(11)) {
            this.inputViewHandler.removeMessages(11);
        }
        this.inputViewHandler.sendEmptyMessageDelayed(11, 1L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int inputModeName2IntegralValue(String inputModeName) {
        if (InputMethods.isChineseInputModePinyin(inputModeName)) {
            return 0;
        }
        if (InputMethods.isChineseInputModeZhuyin(inputModeName)) {
            return 1;
        }
        if (InputMethods.CHINESE_INPUT_MODE_STROKE.compareTo(inputModeName) == 0) {
            return 2;
        }
        if (InputMethods.CHINESE_INPUT_MODE_DOUBLEPINYIN.equalsIgnoreCase(inputModeName)) {
            return 3;
        }
        if (InputMethods.isChineseInputModeCangjie(inputModeName)) {
            return 4;
        }
        if (!InputMethods.isChineseInputModeQuickCangjie(inputModeName)) {
            return -1;
        }
        return 5;
    }

    private String getInputContents() {
        ExtractedText eText;
        AppSpecificInputConnection ic = getCurrentInputConnection();
        return (ic == null || (eText = ic.getExtractedText(new ExtractedTextRequest(), 0)) == null) ? "" : String.valueOf(eText.text);
    }

    private void recordTextBuffer() {
        String text;
        UsageManager.KeyboardUsageScribe keyboardScribe = UsageManager.getKeyboardUsageScribe(getContext());
        if (keyboardScribe != null && (text = getInputContents()) != null && text.length() > 0) {
            keyboardScribe.recordTextBuffer(text);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.InputView
    public void removeAllMessages() {
        for (int msg = 1; msg <= 19; msg++) {
            this.inputViewHandler.removeMessages(msg);
        }
        this.inputViewHandler.removeMessages(500);
        this.pendingCandidateSource = Candidates.Source.INVALID;
        if (this.mHandWritingOnKeyboardHandler != null) {
            this.mHandWritingOnKeyboardHandler.removeAllMessages();
        }
    }

    @Override // com.nuance.swype.input.InputView
    public void finishInput() {
        InputContainerView containerView;
        this.conversionHistory.clear();
        if (this.mChineseInput != null) {
            if (this.mIme != null && (containerView = this.mIme.getInputContainerView()) != null) {
                containerView.hideWidgetView(this.chinesePredictionView);
                containerView.hideWidgetView(this.inlineContainerView);
                containerView.removeActiveWidegtView(this.inlineContainerView);
                containerView.removeActiveWidegtView(this.chinesePredictionView);
            }
            this.mHandWritingOnKeyboardHandler.acceptHWRRecognitionWord();
            this.mEditState.endSession();
            super.finishInput();
            deactivateCategoryDatabase();
            this.mIsIMEActive = false;
            this.lastKeypadBilingualState = false;
            clearInternalStatus();
            hideGridCandidatesView();
            this.altCharacterText.setLength(0);
            if (this.mHandWritingOnKeyboardHandler != null) {
                this.mHandWritingOnKeyboardHandler.onFinishInput();
            }
            removeAllMessages();
            this.mChineseCloudPrediction.finish();
            if (this.mIsDelimiterKeyLabelUpdated && !this.mKeyboardSwitcher.isSymbolMode()) {
                updateDelimiterKeyLabel(false);
            }
            if (this.mIsClearKeyLabelUpdated && !this.mKeyboardSwitcher.isSymbolMode()) {
                updateClearKeyLabel(false);
            }
            this.mChineseInput.finishSession();
            dimissRemoveUdbWordDialog();
            this.mFunctionBarListView.recycleBitmap();
            syncCandidateDisplayStyleToMode();
            this.keyboardTouchEventDispatcher.unwrapTouchEvent(this);
        }
    }

    private void commitComposingText() {
        InputConnection ic = getCurrentInputConnection();
        if (ic != null) {
            ic.beginBatchEdit();
            ic.finishComposingText();
            ic.endBatchEdit();
        }
        this.conversionHistory.clear();
    }

    private void setContext(CharSequence context, boolean update) {
        if (context != null && this.mChineseInput != null) {
            this.mChineseInput.setContext(context, update);
        }
    }

    private boolean updateContext() {
        InputConnection ic;
        CharSequence newContext;
        if (this.mChineseInput == null || hasActiveKeySeq() || (ic = getCurrentInputConnection()) == null || (newContext = ic.getTextBeforeCursor(2, 0)) == null) {
            return false;
        }
        if (newContext.length() == 0) {
            this.mChineseInput.breakContext();
            return true;
        }
        this.mChineseInput.setContext(newContext);
        return this.mIsIMEActive;
    }

    private void selectDefaultCandidate() {
        List<CharSequence> candidates;
        InputConnection ic = getCurrentInputConnection();
        if (ic != null && (candidates = this.candidatesListViewCJK.suggestions()) != null && candidates.size() > 0) {
            ic.beginBatchEdit();
            ic.commitText(candidates.get(0), 1);
            ic.endBatchEdit();
        }
    }

    @Override // com.nuance.swype.input.InputView
    public void flushCurrentActiveWord() {
        ContactUtils.cancelQueryTask();
        if (isCangjieKeypad() && hasActiveKeySeq() && multitapIsInvalid()) {
            multitapClearInvalid();
            this.mChineseInput.clearKey();
        }
        if (this.mChineseInput.hasBilingualPrefix() && this.mKeyboardSwitcher.isKeypadInput() && this.mChineseInput.getActivePrefixIndex() == 254) {
            this.mDefaultWord.clear();
            selectDefaultCandidate();
            showFunctionBarList();
        } else if (isModeStroke() && hasActiveKeySeq()) {
            selectStrokeDefaultCandidate();
            showFunctionBarList();
        } else {
            selectDefaultSuggestion();
        }
        if (this.mHandWritingOnKeyboardHandler != null && !this.mHandWritingOnKeyboardHandler.acceptHWRRecognitionWord()) {
            if (this.mIsDelimiterKeyLabelUpdated && !this.mKeyboardSwitcher.isSymbolMode()) {
                updateDelimiterKeyLabel(false);
            }
            if (this.mIsClearKeyLabelUpdated && !this.mKeyboardSwitcher.isSymbolMode()) {
                updateClearKeyLabel(false);
            }
        }
        clearInternalStatus();
        dimissRemoveUdbWordDialog();
        this.altCharacterText.setLength(0);
        this.mTerminateSession = false;
        this.isCandidateSelectedEver = false;
        this.isTracedEver = false;
        flushInineAltCharacters();
        postUpdateChinesePredictionMessage(false, null, 0, 100L);
    }

    @Override // com.nuance.swype.input.InputView
    public void clearCurrentActiveWord() {
        clearInlineWord();
        flushCurrentActiveWord();
    }

    public void selectDefaultSuggestion() {
        if (this.mDefaultWord.length() > 0 && hasActiveKeySeq() && this.candidatesListViewCJK != null) {
            selectDefault();
        }
        if (!this.mIsUseHardkey) {
            showFunctionBarList();
        }
    }

    private void clearInlineWord() {
        InputConnection ic;
        if (this.mDefaultWord.length() > 0 && (ic = getCurrentInputConnection()) != null) {
            this.mDefaultWord.clear();
            this.mChineseInput.breakContext();
            ic.beginBatchEdit();
            ic.commitText("", 1);
            ic.endBatchEdit();
        }
    }

    private void clearInternalStatus() {
        this.mInitiativeAccept = false;
        if (hasActiveKeySeq()) {
            this.mChineseInput.clearAllKeys();
            multitapTimeOut();
            this.mInvalidKey = -1;
            this.mCurrentIndex = -1;
            this.mInvalidIndex = -1;
            this.mInvalidTapCount = -1;
        }
        if (this.candidatesListViewCJK != null) {
            this.candidatesListViewCJK.setExactKeyboardShowable(false);
            this.candidatesListViewCJK.setLeftArrowStatus(false);
        }
        if (this.mHandWritingOnKeyboardHandler != null) {
            this.mHandWritingOnKeyboardHandler.clearInternalStatus();
        }
        this.mDefaultWord.clear();
    }

    @Override // com.nuance.swype.input.InputView
    public void clearSuggestions() {
        if (this.candidatesListViewCJK != null) {
            this.candidatesListViewCJK.setSuggestions(null, 0);
        }
        syncCandidateDisplayStyleToMode();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.InputView
    public void clearAllKeys() {
        this.touchKeyActionHandler.clearAllKeys();
        this.mDefaultWord.clear();
    }

    /* JADX WARN: Code restructure failed: missing block: B:102:0x00cc, code lost:            if (r2 == false) goto L66;     */
    @Override // com.nuance.swype.input.InputView
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void updateSelection(int r11, int r12, int r13, int r14, int[] r15) {
        /*
            Method dump skipped, instructions count: 366
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.nuance.swype.input.chinese.ChineseInputView.updateSelection(int, int, int, int, int[]):void");
    }

    @Override // com.nuance.swype.input.InputView
    public void displayCompletions(CompletionInfo[] completions) {
        if (this.mCompletionOn) {
            this.mCompletions.update(completions);
            this.candidatesListViewCJK.setSuggestions(this.mCompletions.getDisplayItems(), 0);
        }
    }

    @Override // com.nuance.swype.input.InputView
    public boolean handleKeyDown(int keyCode, KeyEvent event) {
        if (this.mIme.isHardKeyboardActive()) {
            if (this.mChineseInput != null && hasActiveKeySeq() && (keyCode == 21 || keyCode == 22 || keyCode == 19 || keyCode == 20 || keyCode == 67)) {
                if (keyCode != 67) {
                    return true;
                }
                handleBackspace(event.getRepeatCount());
                return true;
            }
        } else if (this.mChineseInput != null && hasActiveKeySeq() && (keyCode == 21 || keyCode == 22 || keyCode == 19 || keyCode == 20)) {
            return true;
        }
        return false;
    }

    @Override // com.nuance.swype.input.InputView
    public void onSpeechViewDismissed() {
        showFunctionBarList();
    }

    @Override // com.nuance.swype.input.InputView
    public void onSpeechViewShowedUp() {
    }

    @Override // com.nuance.swype.input.InputView
    public boolean handleKey(int primaryCode, boolean quickPressed, int repeatedCount) {
        log.d("handleKey() :  method called chinese");
        IMEApplication.from(this.mIme).setUserTapKey(true);
        switch (primaryCode) {
            case KeyboardEx.KEYCODE_KEYBOARD_RESIZE /* 2900 */:
                if (this.gridCandidateTableVisible) {
                    hideGridCandidatesView();
                }
                this.candidatesPopup = null;
                flushCurrentActiveWord();
                super.handleKey(primaryCode, quickPressed, repeatedCount);
                if (this.mShowFunctionBar) {
                    showFunctionBarList();
                } else {
                    clearSuggestions();
                }
                if (!this.mKeyboardSwitcher.isKeypadInput() && !isModeStroke()) {
                    return true;
                }
                this.mOneStepPYContainer.hideSymbolList();
                if (!isNormalTextInputMode()) {
                    return true;
                }
                showPinyinSelectionList(this.chineseSymbolList, -1, false);
                return true;
            case KeyboardEx.KEYCODE_CLEAR_WORD /* 4065 */:
                clearInlineWord();
                showFunctionBarList();
                if (this.candidatesListViewCJK != null && this.candidatesListViewCJK.getAltCharacterConverted()) {
                    this.candidatesListViewCJK.setAltCharacterConverted(false);
                }
                this.lastKeypadBilingualState = false;
                showPinyinSelectionList(this.chineseSymbolList, -1, false);
                flushCurrentActiveWord();
                updateListViews(true, true);
                return true;
            case KeyboardEx.KEYCODE_SPEECH /* 6463 */:
                if (quickPressed) {
                    return true;
                }
                flushCurrentActiveWord();
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

    private void selectStrokeDefaultCandidate() {
        List<CharSequence> candidates;
        InputConnection ic = getCurrentInputConnection();
        if (ic != null && (candidates = this.candidatesListViewCJK.suggestions()) != null && candidates.size() > 0) {
            ic.beginBatchEdit();
            for (CharSequence candidate : candidates) {
                if (candidate != null && (candidate.length() == 1 || (candidate.length() > 1 && candidate.charAt(1) != '~'))) {
                    if (candidate.charAt(0) == 40959 && candidate.length() > 1) {
                        candidate = candidate.toString().substring(1);
                    }
                    ic.commitText(candidate.toString(), 1);
                    ic.endBatchEdit();
                }
            }
            ic.endBatchEdit();
        }
    }

    @Override // com.nuance.swype.input.InputView
    public void handleEmotionKey() {
        flushCurrentActiveWord();
        super.handleEmotionKey();
    }

    @Override // com.nuance.swype.input.InputView
    public boolean handleKeyUp(int keyCode, KeyEvent event) {
        return this.mChineseInput != null && hasActiveKeySeq() && (keyCode == 21 || keyCode == 22 || keyCode == 19 || keyCode == 20);
    }

    @Override // com.nuance.swype.input.InputView
    public void onText(CharSequence text, long eventTime) {
        InputConnection ic = getCurrentInputConnection();
        if (ic != null && text != null && text.length() > 0) {
            setDefaultWordType(StatisticsEnabledEditState.DefaultSelectionType.GENERIC);
            if (flushInineAltCharacters()) {
                ic.beginBatchEdit();
                ic.commitText(text, 1);
                ic.endBatchEdit();
                return;
            }
            if (this.mChineseInput.hasBilingualPrefix() && this.mKeyboardSwitcher.isKeypadInput() && this.mChineseInput.getActivePrefixIndex() == 254) {
                selectDefaultCandidate();
                ic.beginBatchEdit();
                ic.commitText(text, 1);
                ic.endBatchEdit();
                return;
            }
            acceptHWRAndUpdateListView();
            if (this.mChineseInput == null || !hasActiveKeySeq()) {
                ic.beginBatchEdit();
                ic.commitText(text, 1);
                ic.endBatchEdit();
                clearSuggestions();
                return;
            }
            if ((!this.mChineseInput.isFullSentenceActive() && isHasSegmentDelimiter()) || isComponent()) {
                flashError();
                return;
            }
            if (!isModeStroke() || !flashErrorIfNeededInStroke()) {
                if (isCangjieKeypad() && multitapIsInvalid()) {
                    multitapClearInvalid();
                    this.mChineseInput.clearKey();
                }
                selectDefault();
                if (this.mChnPinyinAndPhraseListViewContainer != null) {
                    this.mChnPinyinAndPhraseListViewContainer.hideSpellPrefixSuffixList();
                }
                ic.beginBatchEdit();
                ic.commitText(text, 1);
                ic.endBatchEdit();
                updateListViews(false, false);
            }
        }
    }

    private boolean flashErrorIfNeededInStroke() {
        int count = getInputDelimiterCount();
        if (count <= 0) {
            return false;
        }
        AtomicInteger wdSource = new AtomicInteger();
        this.mChineseInput.getWord(0, this.defaultHighlightWord, wdSource);
        if (this.defaultHighlightWord.length() == count + 1) {
            return false;
        }
        flashRedError();
        this.defaultHighlightWord.setLength(0);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isPinyinQwertyShiftedState() {
        return isShifted() && isQwertyLayout() && isModePinyin();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateInput(Point point, int primaryCode, int[] keyCodes, long eventTime, boolean addExplicitSymbol) {
        if (!isValidBuild()) {
            sendKeyChar((char) primaryCode);
            return;
        }
        this.isSourceFromPressSpace = false;
        this.mDealingBackspace = false;
        int max = this.mChineseInput.getKeyCount();
        if (max < 64 || (max == 64 && primaryCode == 10)) {
            if (primaryCode != 48 || ((!isModeCangjie() && !isModePinyin()) || !this.mKeyboardSwitcher.isKeypadInput() || !isNormalTextInputMode())) {
                acceptHWRAndUpdateListView();
            }
            if (isPinyinQwertyShiftedState()) {
                char ch = (char) primaryCode;
                if ('a' <= ch && ch <= 'z') {
                    ch = Character.toUpperCase(ch);
                }
                if (hasActiveKeySeq()) {
                    if ('A' <= ch && ch <= 'Z') {
                        this.mChineseInput.setShiftState(Shift.ShiftState.ON);
                        if (addExplicitSymbol) {
                            this.mChineseInput.addExplicitSymbol(primaryCode, getShiftState());
                        }
                        if (this.mChineseInput.getKeyCount() >= 64) {
                            this.mChineseInput.setShiftState(Shift.ShiftState.OFF);
                            return;
                        }
                        updateListViews(true, true);
                        startChinesePrediction();
                        this.mChineseInput.setShiftState(Shift.ShiftState.OFF);
                        return;
                    }
                    selectDefault();
                }
                sendKeyChar(ch);
                return;
            }
            if (this.mChineseInput == null) {
                this.mEditState.end();
                sendKeyChar((char) primaryCode);
            } else if (isDilimiter(primaryCode)) {
                if (isQwertyLayout() && flushInineAltCharacters()) {
                    sendKeyChar((char) primaryCode);
                    return;
                }
                if (isAltCharacterToggled()) {
                    if (this.mKeyboardSwitcher.isKeypadInput() && isModePinyin()) {
                        sendPlaceHolderKey(65, this.delimiterPlaceholderIndexList, eventTime, addExplicitSymbol);
                        return;
                    }
                    return;
                }
                if (hasActiveKeySeq()) {
                    if (!this.mKeyboardSwitcher.isSymbolMode()) {
                        boolean success = this.mChineseInput.cycleTone();
                        if (this.candidatesListViewCJK != null) {
                            this.candidatesListViewCJK.setLeftArrowStatus(false);
                            this.candidatesListViewCJK.setExactKeyboardShowable(false);
                        }
                        this.isDelimiterPressedEver = true;
                        this.mTone = true;
                        if (success) {
                            updateListViews(true, true);
                            startChinesePrediction();
                        } else {
                            return;
                        }
                    } else {
                        return;
                    }
                } else {
                    InputConnection ic = getCurrentInputConnection();
                    ic.beginBatchEdit();
                    ic.commitText("'", 1);
                    ic.endBatchEdit();
                }
            } else if (isAddingZhuyinTone(primaryCode)) {
                if (this.mChineseInput.addToneForZhuyin(primaryCode)) {
                    updateListViews(true, true);
                    startChinesePrediction();
                } else {
                    flashRedError();
                }
            } else if (primaryCode == 10) {
                if (hasActiveKeySeq()) {
                    if (isModeStroke()) {
                        selectWord(getValidStrokeCandidateIndex(), null, this.candidatesListViewCJK);
                        return;
                    } else {
                        selectWord(0, null, this.candidatesListViewCJK);
                        return;
                    }
                }
                sendKeyChar((char) primaryCode);
            } else {
                CharacterUtilities charUtil = IMEApplication.from(this.mIme).getCharacterUtilities();
                if (this.mKeyboardLayoutId == 2304 && UserPreferences.from(this.mIme).getEnableChineseBilingual() && keyCodes != null && point == null && isDigit(primaryCode) && isNormalTextInputMode() && !isNumberTypeInputField() && isModePinyin()) {
                    if (!hasActiveKeySeq()) {
                        handleSeparator(primaryCode);
                    } else {
                        this.touchKeyActionHandler.shouldIgnoreAutoAccept = true;
                        this.mChineseInput.addExplicitKey(primaryCode, Shift.ShiftState.ON);
                        updateListViews(true, true);
                        startChinesePrediction();
                    }
                } else if (isPopupKeyboardShowing() && isDigit(primaryCode)) {
                    handleSeparator(primaryCode);
                } else if (keyCodes != null && primaryCode == 48 && isModePinyin() && this.mKeyboardSwitcher.isKeypadInput() && isNormalTextInputMode()) {
                    if (isModePinyin() && this.mChineseInput.getActivePrefixIndex() != 254) {
                        if (!isNumberTypeInputField()) {
                            if (hasActiveKeySeq() && !isAltCharacterToggled()) {
                                if (!isAlphabeticCharacter(this.mDefaultWord.length() > 0 ? this.mDefaultWord.charAt(0) : (char) 0) || hasDigitTone()) {
                                    return;
                                }
                            }
                            if (this.mHandWritingOnKeyboardHandler != null && this.mHandWritingOnKeyboardHandler.recognitionCandidatesAvailable() && !isAltCharacterToggled() && this.mDefaultWord.length() > 0) {
                                flushInlineWord();
                            }
                            if (!isAltCharacterToggled()) {
                                showPinyinSelectionList(this.chineseSymbolList, -1, false);
                                this.isPressedNumOneKey = true;
                            }
                            sendPlaceHolderKey(87, this.numZeroPlaceholderIndexList, eventTime, true);
                        } else {
                            return;
                        }
                    } else {
                        flashError();
                    }
                } else if (this.mKeyboardLayoutId == 1536 && keyCodes != null && isDigit(primaryCode) && isNormalTextInputMode() && !isNumberTypeInputField()) {
                    if (isDigit(primaryCode) && point == null && (isPhoneticKeypad() || isCangjieKeypad())) {
                        handleSeparator(primaryCode);
                    } else {
                        handlePrediction(primaryCode, keyCodes, addExplicitSymbol);
                    }
                } else if ((charUtil.isPunctuationOrSymbol(primaryCode) && (!isModeCangjie() || this.mKeyboardLayoutId != 2304 || !isCangjieWildCard(primaryCode))) || ((keyCodes == null && isDigit(primaryCode)) || !this.mKeyboardSwitcher.isAlphabetMode())) {
                    handleSeparator(primaryCode);
                } else if (isNumberTypeInputField() && this.mKeyboardSwitcher.isAlphabetMode()) {
                    sendKeyChar((char) primaryCode);
                } else if (point == null && isModePinyin()) {
                    if (UserPreferences.from(this.mIme).getEnableChineseBilingual() && isAlphabeticCharacter((char) primaryCode) && isQwertyLayout() && hasActiveKeySeq()) {
                        if (65 <= primaryCode && primaryCode <= 90) {
                            this.mChineseInput.setShiftState(Shift.ShiftState.ON);
                            this.mChineseInput.processUpperLetterPress(primaryCode);
                            if (this.mChineseInput.getKeyCount() >= 64) {
                                this.mChineseInput.setShiftState(Shift.ShiftState.OFF);
                                return;
                            } else {
                                updateListViews(true, true);
                                startChinesePrediction();
                                this.mChineseInput.setShiftState(Shift.ShiftState.OFF);
                            }
                        } else if (97 <= primaryCode && primaryCode <= 122) {
                            selectDefault();
                            sendKeyChar((char) primaryCode);
                        }
                    } else {
                        selectDefaultSuggestion();
                        sendKeyChar((char) primaryCode);
                    }
                } else {
                    handlePrediction(primaryCode, keyCodes, addExplicitSymbol);
                }
            }
            recordUsedTimeTapDisplaySelectionList();
        }
    }

    @Override // com.nuance.swype.input.InputView
    public void handleCharKey(Point point, int primaryCode, int[] keyCodes, long eventTime) {
        super.handleCharKey(point, primaryCode, keyCodes, eventTime);
        if (!IMEApplication.from(this.mIme).getCharacterUtilities().isPunctuationOrSymbol(primaryCode) || isAddingZhuyinTone(primaryCode)) {
            if (isCangjieKeypad() && hasActiveKeySeq() && multitapIsInvalid()) {
                multitapClearInvalid();
                this.mChineseInput.clearKey();
            }
            updateInput(point, primaryCode, keyCodes, eventTime, true);
            return;
        }
        if (isDilimiter(primaryCode)) {
            updateInput(point, primaryCode, keyCodes, eventTime, false);
        } else {
            onText(new StringBuilder().append((char) primaryCode).toString(), 0L);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isAltCharacterToggled() {
        return this.candidatesListViewCJK != null && this.candidatesListViewCJK.getAltCharacterConverted();
    }

    private void sendPlaceHolderKey(int placeHolder, List<AtomicInteger> indexList, long eventTime, boolean addExplilcitSymbol) {
        if (addExplilcitSymbol) {
            this.mChineseInput.addExplicitSymbol(Character.toLowerCase(placeHolder), getShiftState());
            this.mChineseInput.tryBuildingWordCandidateList(true);
        }
        updateDelimiterKeyLabel(true);
        updateClearKeyLabel(true);
        produceAltCharacterText();
        if (this.isCloudPredictionAllowed && this.mChineseInput != null) {
            this.requestCloudDataCompare = this.mChineseInput.predictionCloudGetData();
        }
        this.candidatesListViewCJK.setAltCharacterConverted(true);
        if (this.mChineseInput != null) {
            if (hasActiveKeySeq()) {
                this.exactInputText.setLength(0);
                if (this.mChineseInput.getExactInputText(this.exactInputText)) {
                    indexList.add(new AtomicInteger(this.exactInputText.length() - 1));
                    convertToAltCharacters(this.exactInputText);
                }
            }
            this.convertedCharacterList.clear();
            this.convertedCharacterList.add(this.exactInputText);
            this.candidatesListViewCJK.setSuggestions(this.convertedCharacterList, 0);
            if (this.mHandWritingOnKeyboardHandler != null && this.mHandWritingOnKeyboardHandler.onSendPlaceHolderKey()) {
                showFunctionBarList();
            }
            syncCandidateDisplayStyleToMode();
            setCandidatesViewShown(true);
            updateInlineAltCharacters();
        }
    }

    private boolean isNumberTypeInputField() {
        return this.mInputFieldInfo != null && this.mInputFieldInfo.isNumericModeField();
    }

    private boolean isZhuyinTone(int code) {
        return code <= 181 && code >= 177;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isAddingZhuyinTone(int primaryCode) {
        return isModeBPMF() && isZhuyinTone(primaryCode);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isDilimiter(int primaryCode) {
        return primaryCode == 39;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.InputView
    public boolean isNormalTextInputMode() {
        return this.mKeyboardSwitcher != null && this.mKeyboardSwitcher.isAlphabetMode();
    }

    @Override // com.nuance.swype.input.InputView
    public boolean handleBackspace(int repeatedCount) {
        InputConnection ic;
        if (hasActiveKeySeq()) {
            if (isCangjieKeypad() && multitapIsInvalid()) {
                multitapClearInvalid();
                this.touchKeyActionHandler.deleteOneKeyAndRefresh();
            }
            this.touchKeyActionHandler.deleteOneKeyAndRefresh();
            if (isMultitapping()) {
                this.touchKeyActionHandler.multiTapTimerTimeOut();
                this.mChineseInput.multiTapTimeOut();
            }
            if (this.mKeyboardSwitcher.isSymbolMode()) {
                updateListViews(false, true);
            } else {
                updateListViews(true, true);
            }
            if (this.mDefaultWord.length() == 0 && (ic = getCurrentInputConnection()) != null) {
                ic.setComposingText("", 1);
                if (!this.mIme.isFullscreenMode()) {
                    updateInlineString(true);
                }
            }
            if (this.mIsDelimiterKeyLabelUpdated && !hasActiveKeySeq()) {
                updateDelimiterKeyLabel(false);
            }
            if (this.mIsClearKeyLabelUpdated && !hasActiveKeySeq()) {
                updateClearKeyLabel(false);
            }
            if (shouldStartChinesePrediction()) {
                startChinesePrediction();
            } else {
                cancelChinesePrediction();
            }
        } else if (this.mHandWritingOnKeyboardHandler != null && this.mHandWritingOnKeyboardHandler.onHandleBackspace()) {
            clearInlineWord();
            this.mDealingBackspace = true;
            showFunctionBarList();
            syncCandidateDisplayStyleToMode();
        } else {
            if (this.mFunctionBarListView.isFunctionBarDisabledOrZeroItem()) {
                this.mIme.sendBackspace(repeatedCount);
            } else {
                if (this.candidatesListViewCJK.isCandidateListEmpty()) {
                    this.mChineseInput.breakContext();
                    this.mIme.sendBackspace(repeatedCount);
                }
                this.mDealingBackspace = true;
                if (isNormalTextInputMode()) {
                    showFunctionBarList();
                }
            }
            syncCandidateDisplayStyleToMode();
            if (this.mIsDelimiterKeyLabelUpdated && !hasActiveKeySeq() && !this.mKeyboardSwitcher.isSymbolMode()) {
                updateDelimiterKeyLabel(false);
            }
            if (this.mIsClearKeyLabelUpdated && !hasActiveKeySeq() && !this.mKeyboardSwitcher.isSymbolMode()) {
                updateClearKeyLabel(false);
            }
            cancelChinesePrediction();
        }
        return true;
    }

    @Override // com.nuance.swype.input.InputView
    public boolean handleSpace(boolean quickPressed, int repeatedCount) {
        InputConnection ic;
        this.isSourceFromPressSpace = true;
        if (this.mChineseInput != null && hasActiveKeySeq()) {
            if (!flushInineAltCharacters()) {
                if (isModeBPMF() && this.mKeyboardLayoutId == 2304) {
                    this.touchKeyActionHandler.shouldIgnoreAutoAccept = true;
                    if (this.mChineseInput.addToneForZhuyin(177)) {
                        updateListViews(true, true);
                        startChinesePrediction();
                    }
                }
                if (isCangjieKeypad() && multitapIsInvalid()) {
                    multitapClearInvalid();
                    this.mChineseInput.clearKey();
                }
                setDefaultWordType(StatisticsEnabledEditState.DefaultSelectionType.TAPPED_SPACE);
                if (isModeCangjie()) {
                    selectDefault();
                } else if (this.mKeyboardSwitcher != null && !this.mKeyboardSwitcher.isSymbolMode()) {
                    if (isModeStroke()) {
                        selectWord(getValidStrokeCandidateIndex(), null, this.candidatesListViewCJK);
                    } else {
                        selectWord(this.candidatesListViewCJK.isExactKeyboardShowable() ? 1 : 0, null, this.candidatesListViewCJK);
                    }
                } else {
                    flashError();
                }
            }
        } else if (this.mHandWritingOnKeyboardHandler != null && this.mHandWritingOnKeyboardHandler.acceptHWRRecognitionWord()) {
            this.isSourceFromPressSpace = false;
            updateContext();
            updateListViews(true, true);
        } else {
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
            this.mEditState.spaceKey();
            if (addSpace) {
                sendKeyChar(' ');
            }
        }
        return true;
    }

    @Override // com.nuance.swype.input.InputView
    public void handleShiftKey() {
        if (isNormalTextInputMode()) {
            if (isTabletDevice() && (isModeCangjie() || isDoublePinyinMode())) {
                setKeyboardLayer(KeyboardEx.KeyboardLayerType.KEYBOARD_SYMBOLS);
            } else {
                if (isModePinyin() && !UserPreferences.from(this.mIme).getEnableChineseBilingual()) {
                    selectDefaultSuggestion();
                }
                this.mKeyboardSwitcher.updateShiftStateToggle();
                UsageManager.KeyboardUsageScribe keyboardScribe = UsageManager.getKeyboardUsageScribe(getContext());
                if (keyboardScribe != null) {
                    keyboardScribe.recordShiftState(this.mKeyboardSwitcher.getCurrentShiftState());
                }
                invalidateKeyboardImage();
            }
        } else {
            this.mKeyboardSwitcher.cycleShiftState();
            invalidateKeyboardImage();
        }
        if (hasActiveKeySeq()) {
            updateDelimiterKeyLabel(isShifted() ? false : true);
        } else {
            updateDelimiterKeyLabel(false);
        }
    }

    @Override // com.nuance.swype.input.InputView
    public void handleClose() {
        recordTextBuffer();
        if (this.mHandWritingOnKeyboardHandler != null) {
            this.mHandWritingOnKeyboardHandler.onHandleClose();
        }
        this.mIsIMEActive = false;
        dimissRemoveUdbWordDialog();
        this.mFunctionBarListView.recycleBitmap();
        super.handleClose();
    }

    @Override // com.nuance.swype.input.InputView
    public void handleWrite(List<Point> write) {
        if (checkCurLanguage() && this.mHandWritingOnKeyboardHandler != null) {
            this.mHandWritingOnKeyboardHandler.onHandleWrite(write);
        }
    }

    @Override // com.nuance.swype.input.InputView
    public void handleTrace(KeyboardViewEx.TracePoints trace) {
        IMEApplication.from(this.mIme).setUserTapKey(true);
        InputConnection ic = getCurrentInputConnection();
        if (ic != null) {
            if (this.mSpeechWrapper == null || !this.mSpeechWrapper.isSpeechViewShowing()) {
                ic.beginBatchEdit();
                if (!this.mChineseInput.isFullSentenceActive() && isHasSegmentDelimiter()) {
                    flashError();
                    ic.endBatchEdit();
                    return;
                }
                if (this.mChineseInput.hasActiveInput()) {
                    int wordCount = updateSuggestions(Candidates.Source.TRACE);
                    if (this.mIsUpdateInline) {
                        updateInlineString(false);
                    }
                    this.mIsUpdateInline = true;
                    if (wordCount == 0) {
                        setNotMatchToolTipSuggestion();
                    } else {
                        this.isTracedEver = true;
                    }
                } else {
                    flashError();
                    this.touchKeyActionHandler.clearAllKeys();
                    updateListViews(true, true);
                }
                ic.endBatchEdit();
            }
        }
    }

    @Override // com.nuance.swype.input.InputView, com.nuance.swype.input.KeyboardViewEx
    public void setKeyboard(KeyboardEx keyboard) {
        super.setKeyboard(keyboard);
        if (getKeyboard() == null) {
            this.isTraceEnabledOnKeyboard = false;
            return;
        }
        if (isNormalTextInputMode() && !hasActiveKeySeq()) {
            updateDelimiterKeyLabel(false);
            updateClearKeyLabel(false);
        }
        updateKbdTraceState();
        this.mChineseInput.enableTrace(this.isTraceEnabledOnKeyboard);
        this.mChineseInput.setMultiTapInputMode(isCangjieKeypad());
    }

    private void updateKbdTraceState() {
        this.isTraceEnabledOnKeyboard = (!isValidBuild() || !IMEApplication.from(getContext()).getSwypeCoreLibMgr().getSwypeCoreLibInstance().isTraceBuildEnabled() || !this.mTraceOn || this.mHandwritingOn || this.mKeyboardSwitcher.isSymbolMode() || this.mKeyboardSwitcher.isPhoneMode() || this.mKeyboardSwitcher.isNumMode() || this.mKeyboardSwitcher.isEditMode() || getKeyboard().getKeyboardDockMode() == KeyboardEx.KeyboardDockMode.DOCK_SPLIT) ? false : true;
    }

    @Override // com.nuance.swype.input.chinese.CJKCandidatesListView.OnWordSelectActionListener
    public void selectWord(int index, CharSequence word, View source) {
        String selected_emoji = null;
        String original_emoji = null;
        if (source == this.candidatesListViewCJK && word != null) {
            selected_emoji = word.toString();
            original_emoji = selected_emoji;
            logSelectedWordForChinesePrediction(index, word.toString());
        }
        cancelChinesePrediction();
        if (!shouldDisableInput(KeyboardEx.KEYCODE_INVALID)) {
            if (isCangjieKeypad() && hasActiveKeySeq() && multitapIsInvalid()) {
                multitapClearInvalid();
                this.mChineseInput.clearKey();
            }
            InputConnection ic = getCurrentInputConnection();
            if (source instanceof VerCandidatesListView) {
                acceptHWRAndUpdateListView();
                if (!hasActiveKeySeq() && (this.mKeyboardSwitcher.isKeypadInput() || isModeStroke())) {
                    if (ic != null) {
                        ic.beginBatchEdit();
                        ic.commitText(this.chineseSymbolList.get(index), 1);
                        moveCursorToMiddle(ic, this.chineseSymbolList.get(index).toString());
                        ic.endBatchEdit();
                        return;
                    }
                    return;
                }
                if ((isAltCharacterToggled() && this.mKeyboardSwitcher.isKeypadInput()) || (hasActiveKeySeq() && (isModeStroke() || isModeCangjie()))) {
                    if (isModeStroke()) {
                        if (isComponent()) {
                            flashRedError();
                            return;
                        } else if (flashErrorIfNeededInStroke()) {
                            return;
                        }
                    }
                    selectDefault();
                    flushInineAltCharacters();
                    if (ic != null) {
                        ic.beginBatchEdit();
                        ic.commitText(this.chineseSymbolList.get(index), 1);
                        moveCursorToMiddle(ic, this.chineseSymbolList.get(index).toString());
                        ic.endBatchEdit();
                        return;
                    }
                    return;
                }
                dismissPopupKeyboard();
                List<CharSequence> candidates = ((VerCandidatesListView) source).suggestions();
                if (isModePinyin() && this.mChineseInput.hasBilingualPrefix()) {
                    if (index == candidates.size() - 1) {
                        this.mChineseInput.setActivePrefixIndex(254);
                        postUpdateChinesePredictionMessage(false, null, 0, 10L);
                    } else {
                        this.mChineseInput.setActivePrefixIndex(index);
                        startChinesePrediction();
                    }
                } else {
                    this.mChineseInput.setActivePrefixIndex(index);
                    startChinesePrediction();
                }
                updateListViews(false, true);
                return;
            }
            if (!flushInineAltCharacters()) {
                dismissPopupKeyboard();
                if (ic != null) {
                    ic.beginBatchEdit();
                    if (this.mCompletionOn && index >= 0 && index < this.mCompletions.size()) {
                        ic.commitCompletion(this.mCompletions.get(index));
                        this.candidatesListViewCJK.clear();
                        ic.endBatchEdit();
                        return;
                    }
                    if (source == this.candidatesListViewCJK) {
                        if (word != null) {
                            setDefaultWordType(StatisticsEnabledEditState.DefaultSelectionType.SELECTION_WCL);
                        }
                        if (this.mHandWritingOnKeyboardHandler != null && this.mHandWritingOnKeyboardHandler.recognitionCandidatesAvailable()) {
                            log.d("selectWord...index: if  :: actual word ::called >>>");
                            this.mHandWritingOnKeyboardHandler.clearRecognitionCandidates();
                            if (word == null) {
                                ic.endBatchEdit();
                                return;
                            }
                            this.mInsertText = new StringBuilder(word);
                            log.d("selectWord...index: if  :: actual word :: ", word, "...word: ", this.mInsertText.toString(), "mExactWord ::", this.mExactWord);
                            if (this.mInsertText.length() > 0) {
                                ic.commitText(this.mInsertText, 1);
                                setContext(ic.getTextBeforeCursor(2, 0), true);
                            }
                        } else {
                            log.d("selectWord...index: else   :: actual word ::called >>>");
                            CharSequence defaultCandidate = "";
                            List<CharSequence> candidates2 = this.candidatesListViewCJK.suggestions();
                            if (index == 0 && word != null && this.candidatesListViewCJK.isExactKeyboardShowable()) {
                                log.d("selectWord...index: inside else  ::  if  :: actual word ::called >>>");
                                this.mDefaultWord.clear();
                                getExactWord(this.mExactWord);
                                ic.commitText(this.mExactWord, 1);
                                this.touchKeyActionHandler.clearAllKeys();
                                if (!hasActiveKeySeq()) {
                                    this.spellPrefixSuffixListView.clear();
                                }
                                if (candidates2 != null && candidates2.size() > 1 && candidates2.get(1) != null) {
                                    defaultCandidate = candidates2.get(1);
                                }
                                this.mTerminateSession = false;
                                this.isCandidateSelectedEver = false;
                            } else if ((index == 0 && word == null && this.candidatesListViewCJK.isExactKeyboardShowable()) || !this.candidatesListViewCJK.isExactKeyboardShowable()) {
                                log.d("selectWord...index: inside else  ::  else if  :: actual word ::called >>>");
                                List<AtomicInteger> wdSourceList = this.mChineseInput.getWordsSource();
                                int selectedWordSource = wdSourceList.size() > index ? wdSourceList.get(index).intValue() : 0;
                                this.mChineseInput.selectWord(index, this.mInsertText);
                                if (word != null) {
                                    this.conversionHistory.add(word.toString());
                                }
                                log.d("conversionHistory...index: ", Integer.valueOf(index), "...exact keyboard visible: ", Boolean.valueOf(this.candidatesListViewCJK.isExactKeyboardShowable()), "...added: ", word);
                                if (!hasActiveKeySeq() && !this.mInitiativeAccept) {
                                    this.spellPrefixSuffixListView.clear();
                                }
                                if (this.mInsertText.length() > 0) {
                                    if (word != null && EmojiLoader.isEmoji(word.toString())) {
                                        selected_emoji = this.candidatesListViewCJK.getEmojis().get(selected_emoji);
                                        if (selected_emoji != null && !selected_emoji.equals(word.toString())) {
                                            ic.commitText(selected_emoji, 1);
                                        } else {
                                            ic.commitText(this.mInsertText, 1);
                                        }
                                    } else {
                                        ic.commitText(this.mInsertText, 1);
                                    }
                                    if (this.mInputFieldInfo.isPasswordField()) {
                                        removeDLMPhraseByConversionHistory(this.mInsertText, selectedWordSource);
                                    }
                                    this.conversionHistory.clear();
                                }
                                if (candidates2 != null && candidates2.size() > 0) {
                                    defaultCandidate = candidates2.get(0);
                                }
                                this.mTerminateSession = true;
                                this.isCandidateSelectedEver = true;
                            } else {
                                List<AtomicInteger> wdSourceList2 = this.mChineseInput.getWordsSource();
                                int selectedWordSource2 = wdSourceList2.size() > index ? wdSourceList2.get(index).intValue() : 0;
                                this.mChineseInput.selectWord(index - 1, this.mInsertText);
                                if (word != null) {
                                    this.conversionHistory.add(word.toString());
                                }
                                log.d("conversionHistory...index: ", Integer.valueOf(index), "...exact keyboard visible: ", Boolean.valueOf(this.candidatesListViewCJK.isExactKeyboardShowable()), "...added: ", word);
                                if (!hasActiveKeySeq()) {
                                    this.spellPrefixSuffixListView.clear();
                                }
                                if (this.mInsertText.length() > 0) {
                                    ic.commitText(this.mInsertText, 1);
                                    if (this.mInputFieldInfo.isPasswordField()) {
                                        removeDLMPhraseByConversionHistory(this.mInsertText, selectedWordSource2);
                                    }
                                    setContext(ic.getTextBeforeCursor(2, 0), false);
                                    this.conversionHistory.clear();
                                }
                                if (candidates2 != null && candidates2.size() > 1) {
                                    defaultCandidate = candidates2.get(1);
                                }
                                this.mTerminateSession = true;
                                this.isCandidateSelectedEver = true;
                            }
                            if (this.mInsertText.length() > 0 && defaultCandidate.toString().equals(this.mInsertText.toString()) && !this.mInputFieldInfo.isPasswordField()) {
                                recordCommittedSentence(this.mInsertText.toString());
                            }
                            if (word != null) {
                                this.mEditState.selectWord(word, defaultCandidate);
                            }
                        }
                        multitapClearInvalid();
                        if (this.candidatesListViewCJK != null) {
                            this.candidatesListViewCJK.setLeftArrowStatus(false);
                            this.candidatesListViewCJK.setExactKeyboardShowable(false);
                        }
                        this.mbNeedUpdate = false;
                        this.mbNeedDefer = true;
                        updateListViews(true, true);
                        this.spellPrefixSuffixListView.disableHighlight();
                    } else if (source == this.spellPrefixSuffixListView) {
                        this.spellPrefixSuffixListView.enableHighlight();
                        List<CharSequence> candidates3 = ((SpellListView) source).suggestions();
                        if (isModePinyin() && this.mChineseInput.hasBilingualPrefix()) {
                            if (index == candidates3.size() - 1) {
                                this.mChineseInput.setActivePrefixIndex(254);
                            } else {
                                this.mChineseInput.setActivePrefixIndex(index);
                            }
                        } else {
                            this.mChineseInput.setActivePrefixIndex(index);
                        }
                        setDefaultWordType(StatisticsEnabledEditState.DefaultSelectionType.TAPPED_PREFIX);
                        if (this.candidatesListViewCJK != null) {
                            this.candidatesListViewCJK.setLeftArrowStatus(false);
                            this.candidatesListViewCJK.setExactKeyboardShowable(false);
                        }
                        if (this.gridCandidateTableVisible) {
                            hideGridCandidatesView();
                            updateInlineString(true);
                            this.spellPrefixSuffixListView.drawCandidates(index);
                            updateGridViewByPrefix(this.candidatesListViewCJK);
                        } else {
                            updateListViews(false, true);
                        }
                    }
                    if (this.mIsDelimiterKeyLabelUpdated && !hasActiveKeySeq() && !this.mKeyboardSwitcher.isSymbolMode()) {
                        updateDelimiterKeyLabel(false);
                    }
                    if (this.mIsClearKeyLabelUpdated && !hasActiveKeySeq() && !this.mKeyboardSwitcher.isSymbolMode()) {
                        updateClearKeyLabel(false);
                    }
                    ic.endBatchEdit();
                    this.isTracedEver = false;
                    if (source == this.spellPrefixSuffixListView && isModePinyin() && index == ((SpellListView) source).suggestions().size() - 1 && this.mChineseInput.hasBilingualPrefix()) {
                        postUpdateChinesePredictionMessage(false, null, 0, 10L);
                    } else {
                        startChinesePrediction();
                    }
                    if (word != null) {
                        super.addEmojiInRecentCat(selected_emoji, original_emoji);
                    }
                }
            }
        }
    }

    private void removeDLMPhraseByConversionHistory(CharSequence fullPhrase, int selectedWordSource) {
        log.d("removeDLMPhraseByConversionHistory...segment count: ", Integer.valueOf(this.conversionHistory.size()));
        if (selectedWordSource == 14 || selectedWordSource == 6) {
            log.d("removeDLMPhraseByConversionHistory...don't delete UDB word which already exists.");
            return;
        }
        for (CharSequence converted : this.conversionHistory) {
            this.mChineseInput.dlmDelete(converted.toString());
            log.d("removeDLMPhraseByConversionHistory...segment: ", converted);
        }
        this.mChineseInput.dlmDelete(fullPhrase.toString());
        log.d("removeDLMPhraseByConversionHistory...remove full: ", fullPhrase);
    }

    private void recordCommittedSentence(String sentence) {
        UsageManager.KeyboardUsageScribe keyboardScribe = UsageManager.getKeyboardUsageScribe(getContext());
        if (keyboardScribe != null) {
            keyboardScribe.recordCommittedSentence(sentence);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.InputView
    public void setCandidatesViewShown(boolean shown) {
        super.setCandidatesViewShown(shown);
        if (this.mChnPinyinAndPhraseListViewContainer != null) {
            if (shown) {
                this.mChnPinyinAndPhraseListViewContainer.setVisibility(0);
            } else {
                this.mChnPinyinAndPhraseListViewContainer.setVisibility(8);
            }
        }
    }

    public boolean isMaxKeysCangjieOrQuickCangjie() {
        String mode = this.currentInputMode.mInputMode;
        return (InputMethods.isChineseInputModeCangjie(mode) && this.mChineseInput.getKeyCount() > 5) || (InputMethods.isChineseInputModeQuickCangjie(mode) && this.mChineseInput.getKeyCount() > 2);
    }

    private void handlePrediction(int primaryCode, int[] keyCodes, boolean addExplicitSymbol) {
        boolean success = false;
        if (this.mIme.isHardKeyboardActive()) {
            success = this.mChineseInput.processKey(null, primaryCode, getShiftState(), 0L);
            log.d("kathy handlePrediction()... processKey sucess ? ", Boolean.valueOf(success));
        }
        if (this.currentInputMode.mInputMode.compareTo(InputMethods.CHINESE_INPUT_MODE_STROKE) == 0) {
            int inlineWordlen = this.mDefaultWord.length();
            if (primaryCode != 3 || inlineWordlen != 2 || this.mDefaultWord.charAt(inlineWordlen - 1) != STROKE_KEYCODE_NUM_TWO || this.mDefaultWord.charAt(inlineWordlen - 2) != STROKE_KEYCODE_NUM_FOUR) {
                success = true;
                if (addExplicitSymbol) {
                    this.mChineseInput.addExplicitSymbol(primaryCode, getShiftState());
                }
                this.mEditState.characterTyped((char) primaryCode);
            } else {
                return;
            }
        } else if (keyCodes != null) {
            success = true;
            if (addExplicitSymbol) {
                this.mChineseInput.addExplicitSymbol(primaryCode, getShiftState());
            }
            this.mEditState.characterTyped((char) primaryCode);
            if (((ChineseKeyboardSwitcher) this.mKeyboardSwitcher).isMixedInput() && !this.mTerminateSession && !this.mTone) {
                if (isModeBPMF()) {
                    getExactWord(this.mExactWord);
                    if (isValidExactKeyboardPhrase(this.mExactWord.toString())) {
                        if (this.mExactWord.toString().trim().length() > 0 && this.mExactWord.toString().trim().length() <= 5) {
                            if (this.candidatesListViewCJK != null) {
                                this.candidatesListViewCJK.setExactKeyboardShowable(true);
                                this.candidatesListViewCJK.setLeftArrowStatus(false);
                            }
                        } else {
                            this.candidatesListViewCJK.setExactKeyboardShowable(false);
                            this.candidatesListViewCJK.setLeftArrowStatus(true);
                        }
                    }
                }
            } else {
                this.candidatesListViewCJK.setExactKeyboardShowable(false);
                this.candidatesListViewCJK.setLeftArrowStatus(false);
                this.mTerminateSession = true;
            }
            if (hasTone()) {
                this.candidatesListViewCJK.setExactKeyboardShowable(false);
                this.candidatesListViewCJK.setLeftArrowStatus(false);
            }
        }
        if (!success) {
            flashError();
            return;
        }
        updateListViews(true, true);
        if (shouldStartChinesePrediction()) {
            startChinesePrediction();
        } else {
            cancelChinesePrediction();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.KeyboardViewEx
    public boolean isMultitapHandledInCore() {
        return !isModeCangjie();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleSeparator(int primaryCode) {
        if (primaryCode != 63 || ((!isPhoneticKeypad() || !isNormalTextInputMode()) && !isModeStroke())) {
            this.mEditState.punctuationOrSymbols();
            if (flushInineAltCharacters()) {
                sendKeyChar((char) primaryCode);
                return;
            }
            if (hasActiveKeySeq()) {
                if ((!this.mChineseInput.isFullSentenceActive() && isHasSegmentDelimiter()) || isComponent()) {
                    flashError();
                    return;
                }
                if (!isModeStroke() || !flashErrorIfNeededInStroke()) {
                    setDefaultWordType(StatisticsEnabledEditState.DefaultSelectionType.TAPPED_PUNCTUATION);
                    if (this.mIme.isHardKeyboardActive()) {
                        selectWord(this.candidatesListViewCJK.mSelectedIndex, null, this.candidatesListViewCJK);
                        commitComposingText();
                    } else {
                        selectDefault();
                    }
                    sendKeyChar((char) primaryCode);
                    return;
                }
                return;
            }
            if (this.mHandWritingOnKeyboardHandler != null) {
                this.mHandWritingOnKeyboardHandler.onHandleSeparator();
            }
            sendKeyChar((char) primaryCode);
            updateListViews(true, !this.mIme.isHardKeyboardActive());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    @SuppressLint({"PrivateResource"})
    public void updatePrefixes() {
        if (isPhoneticKeypad() && !isTraceInputEnabled()) {
            boolean updatePrefixList = true;
            boolean isPrefix = true;
            List<CharSequence> prefixList = this.mChineseInput.getPrefixes(this.mDefaultPrefixIndex);
            if (prefixList.size() == 0) {
                isPrefix = false;
            }
            int prefixSize = this.mPinyinCandidates.size();
            boolean currentBilingual = this.mChineseInput.hasBilingualPrefix();
            if (prefixSize > 0 && prefixList.size() == prefixSize) {
                updatePrefixList = false;
                if (!this.mPinyinCandidates.get(0).toString().equals(prefixList.get(0).toString()) || ((prefixSize > 1 && !this.mPinyinCandidates.get(prefixSize - 1).toString().equals(prefixList.get(prefixSize - 1).toString())) || this.lastKeypadBilingualState != currentBilingual)) {
                    this.lastKeypadBilingualState = currentBilingual;
                    updatePrefixList = true;
                }
            }
            if (updatePrefixList) {
                this.mPinyinCandidates.clear();
                for (CharSequence prefix : prefixList) {
                    this.mPinyinCandidates.add(prefix.toString());
                }
                if (prefixList.size() > 0) {
                    this.mDefaultPrefixIndex.set(-1);
                }
                if (prefixList.size() > 0 && !this.mKeyboardSwitcher.isSymbolMode()) {
                    if (isPrefix && isModePinyin() && currentBilingual) {
                        prefixList.add(getResources().getString(R.string.label_chn_eng_key));
                    }
                    showPinyinSelectionList(prefixList, this.mDefaultPrefixIndex.get(), true);
                    return;
                }
                if (isNormalTextInputMode()) {
                    showPinyinSelectionList(this.chineseSymbolList, -1, false);
                    return;
                }
                return;
            }
            if (prefixList.size() > 0) {
                this.mDefaultPrefixIndex.set(-1);
                if (this.mOneStepPYContainer != null) {
                    if (currentBilingual && !this.mKeyboardSwitcher.isSymbolMode() && isModePinyin()) {
                        prefixList.add(getResources().getString(R.string.label_chn_eng_key));
                    }
                    this.mOneStepPYContainer.showPrefixList(prefixList, -1, true);
                    return;
                }
                return;
            }
            return;
        }
        if ((isTraceInputEnabled() && this.mChineseInput.isHasTraceInfo()) || ((ChineseKeyboardSwitcher) this.mKeyboardSwitcher).isMixedInput()) {
            List<CharSequence> prefixList2 = this.mChineseInput.getPrefixes(this.mDefaultPrefixIndex);
            boolean isPrefix2 = true;
            if (prefixList2.size() == 0) {
                isPrefix2 = false;
            }
            if (this.mDefaultPrefixIndex.get() > this.mChineseInput.getPrefixesCount()) {
                this.mDefaultPrefixIndex.set(-1);
            }
            if (prefixList2.size() > 0 && !this.mKeyboardSwitcher.isSymbolMode()) {
                if (isPrefix2) {
                    if (isQwertyLayout() && isModePinyin() && this.mChineseInput.hasBilingualPrefix()) {
                        prefixList2.add(getResources().getString(R.string.label_chn_eng_key));
                        if (this.mDefaultPrefixIndex.get() == -1 && this.mChineseInput.getActivePrefixIndex() == 254) {
                            this.mDefaultPrefixIndex.set(prefixList2.size() - 1);
                        }
                    }
                    showPinyinSelectionList(prefixList2, this.mDefaultPrefixIndex.get(), true);
                    return;
                }
                return;
            }
            if (prefixList2.isEmpty() && this.gridCandidateTableVisible && this.mChnPinyinAndPhraseListViewContainer != null) {
                this.spellPrefixSuffixListView.clear();
                this.mChnPinyinAndPhraseListViewContainer.showSpellPrefixSuffixList();
            }
        }
    }

    @Override // com.nuance.swype.input.InputView
    public int updateSuggestions(Candidates.Source source) {
        List<CharSequence> wordList;
        List<AtomicInteger> wdSourceList;
        if (!isValidBuild()) {
            return 0;
        }
        if (isActiveWCL()) {
            this.mChnPinyinAndPhraseListViewContainer.hideFunctionBarListView();
            this.mChnPinyinAndPhraseListViewContainer.hideSpellPrefixSuffixList();
            this.mChnPinyinAndPhraseListViewContainer.showPhraseListView();
            return -1;
        }
        if (this.candidatesListViewCJK == null || this.mChnPinyinAndPhraseListViewContainer == null) {
            return 0;
        }
        if (!isNormalTextInputMode()) {
            return 0;
        }
        if (!this.mIsIMEActive) {
            if (this.mHandWritingOnKeyboardHandler != null) {
                this.mHandWritingOnKeyboardHandler.clearRecognitionCandidates();
            }
            return 0;
        }
        int wordListCount = 0;
        if (this.mHandWritingOnKeyboardHandler != null && this.mHandWritingOnKeyboardHandler.recognitionCandidatesAvailable()) {
            if (this.mHandWritingOnKeyboardHandler.getT9WriteChinese() == null) {
                return 0;
            }
            setCandidatesViewShown(true);
            if (this.candidatesListViewCJK != null) {
                this.candidatesListViewCJK.setSuggestions(getPureCandidates(this.mHandWritingOnKeyboardHandler.getHWRecognitionCandidates()), 0);
                this.mChnPinyinAndPhraseListViewContainer.hideFunctionBarListView();
                this.mChnPinyinAndPhraseListViewContainer.hideSpellPrefixSuffixList();
                this.mChnPinyinAndPhraseListViewContainer.showPhraseListView();
                syncCandidateDisplayStyleToMode();
            }
            this.mDefaultWord.clear();
            this.mDefaultWordIndex.set(0);
            this.mDefaultWord.append(getPureCandidate(this.mHandWritingOnKeyboardHandler.getHWRecognitionCandidates().get(0)));
            return 0;
        }
        if (this.mChineseInput == null) {
            return 0;
        }
        this.mDefaultWord.clear();
        if (isAltCharacterToggled()) {
            int wordListCount2 = updateAltCharacters();
            return wordListCount2;
        }
        this.mSelectWord = this.mChineseInput.getChineseWordsInline().toString();
        if (source == Candidates.Source.TRACE) {
            Candidates candidates = this.mChineseInput.getCandidates();
            this.isDealGesture = isGesture(candidates);
            if (!handleGesture(candidates)) {
                wordList = getWords(this.mDefaultWord, this.mDefaultWordIndex);
                wdSourceList = this.mChineseInput.getWordsSource();
                if (wordList.size() > 0) {
                    int defaultCandidateIndex = candidates.getDefaultCandidateIndex();
                    this.mDefaultWordIndex.set(defaultCandidateIndex);
                    this.mDefaultWord.append(wordList.get(defaultCandidateIndex));
                }
                this.isDealGesture = false;
                wordListCount = wordList.size();
            } else {
                this.touchKeyActionHandler.clearAllKeys();
                this.isDealGesture = false;
                showFunctionBarList();
                this.mFunctionBarListView.showFunctionBar();
                syncCandidateDisplayStyleToMode();
                if (this.mChnPinyinAndPhraseListViewContainer != null) {
                    this.mChnPinyinAndPhraseListViewContainer.clear();
                }
                return -1;
            }
        } else {
            wordList = getWords(this.mDefaultWord, this.mDefaultWordIndex);
            wdSourceList = this.mChineseInput.getWordsSource();
        }
        if (this.candidatesListViewCJK.isExactKeyboardShowable()) {
            getExactWord(this.mExactWord);
            if (isValidExactKeyboardPhrase(this.mExactWord.toString())) {
                StringBuilder word = new StringBuilder(this.mExactWord);
                wordList.add(0, word);
                wdSourceList.add(0, new AtomicInteger(0));
            }
        }
        this.candidatesListViewCJK.setLeftArrowStatus(false);
        this.mChnPinyinAndPhraseListViewContainer.showLeftArrow(false);
        if (wordList.size() > 0 && (!this.mInputFieldInfo.isPasswordField() || (this.mInputFieldInfo.isPasswordField() && hasActiveKeySeq()))) {
            setWordListToSuggestions(wordList, wdSourceList);
        } else {
            if (!isSpeechViewShowing()) {
                showFunctionBarList();
            }
            this.mFunctionBarListView.showFunctionBar();
            this.isPressedNumOneKey = false;
            this.numZeroPlaceholderIndexList.clear();
            this.delimiterPlaceholderIndexList.clear();
        }
        syncCandidateDisplayStyleToMode();
        return wordListCount;
    }

    private int getValidStrokeCandidateIndex() {
        List<CharSequence> wordList = this.candidatesListViewCJK.suggestions();
        if (isModeStroke() && wordList.size() > 0 && wordList.get(0).charAt(0) == 40959 && wordList.get(0).charAt(1) == '~') {
            this.mDefaultWord.clear();
            for (int i = 1; i < wordList.size(); i++) {
                if (wordList.get(i).charAt(0) == 40959) {
                    if (wordList.get(i).charAt(0) == 40959 && wordList.get(i).charAt(1) != '~') {
                        return i;
                    }
                } else {
                    return i;
                }
            }
        }
        return 0;
    }

    private int updateAltCharacters() {
        if (((!hasTone() && isQwertyLayout()) || this.mKeyboardSwitcher.isKeypadInput()) && this.mChineseInput != null && hasActiveKeySeq() && this.candidatesListViewCJK != null && ((this.mHandWritingOnKeyboardHandler == null || !this.mHandWritingOnKeyboardHandler.recognitionCandidatesAvailable()) && isModePinyin())) {
            confirmDisplayConvertedCandidates();
            if (isAllNumberOneRemoved()) {
                this.lastKeypadBilingualState = false;
                List<CharSequence> wordList = getWords(this.mDefaultWord, this.mDefaultWordIndex);
                List<AtomicInteger> wdSourceList = this.mChineseInput.getWordsSource();
                setWordListToSuggestions(wordList, wdSourceList);
                List<CharSequence> prefixList = this.mChineseInput.getPrefixes(this.mDefaultPrefixIndex);
                if (prefixList.size() > 0) {
                    showPinyinSelectionList(prefixList, this.mDefaultPrefixIndex.get(), true);
                }
                updateListViews(true, true);
            }
            syncCandidateDisplayStyleToMode();
            return 1;
        }
        if (this.candidatesListViewCJK != null) {
            this.candidatesListViewCJK.setAltCharacterConverted(false);
        }
        showFunctionBarList();
        this.mFunctionBarListView.showFunctionBar();
        this.isPressedNumOneKey = false;
        this.numZeroPlaceholderIndexList.clear();
        this.delimiterPlaceholderIndexList.clear();
        syncCandidateDisplayStyleToMode();
        return 0;
    }

    private void setWordListToSuggestions(List<CharSequence> wordList, List<AtomicInteger> wdSourceList) {
        this.isPressedNumOneKey = false;
        this.numZeroPlaceholderIndexList.clear();
        this.candidatesListViewCJK.setAltCharacterConverted(false);
        this.mFunctionBarListView.clear();
        this.mChnPinyinAndPhraseListViewContainer.hideFunctionBarListView();
        this.mChnPinyinAndPhraseListViewContainer.showPhraseListView();
        this.mChnPinyinAndPhraseListViewContainer.hideSpellPrefixSuffixList();
        if (isModeBPMF()) {
            getExactWord(this.mExactWord);
            if (isValidExactKeyboardPhrase(this.mExactWord.toString())) {
                this.mDefaultWordIndex.set(this.candidatesListViewCJK.isExactKeyboardShowable() ? 1 : 0);
            }
        }
        this.candidatesListViewCJK.setSuggestions(wordList, this.mDefaultWordIndex.get(), wdSourceList);
    }

    public void updateListViews(boolean updateSpells, boolean setEmptyComposingText) {
        if (isAltCharacterToggled()) {
            updateSuggestions(Candidates.Source.INVALID);
            if (isAltCharacterToggled()) {
                updateInlineAltCharacters();
                return;
            }
        } else {
            if (updateSpells && (!isQwertyLayout() || this.gridCandidateTableVisible)) {
                updatePrefixes();
            }
            updateSuggestions(Candidates.Source.INVALID);
        }
        if (this.mIsUpdateInline) {
            updateInlineString(setEmptyComposingText);
        }
        this.mIsUpdateInline = true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void addMoreSuggestions() {
        if (this.mChineseInput != null && !isAltCharacterToggled()) {
            if (this.mHandWritingOnKeyboardHandler == null || !this.mHandWritingOnKeyboardHandler.recognitionCandidatesAvailable()) {
                List<CharSequence> wordList = this.mChineseInput.getMoreWords(this.candidatesListViewCJK.isExactKeyboardShowable(), 30);
                List<AtomicInteger> wdSourceList = this.mChineseInput.getWordsSource();
                if (wordList.size() > 0) {
                    this.candidatesListViewCJK.setMoreSuggestions(wordList, wdSourceList);
                }
            }
        }
    }

    public void postAddMoreSuggestions() {
        if (!this.inputViewHandler.hasMessages(1)) {
            this.inputViewHandler.sendMessageDelayed(this.inputViewHandler.obtainMessage(1), 200L);
        }
    }

    public ChineseInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mPinyinCandidates = new ArrayList();
        this.showDataList = new ArrayList();
        this.mDefaultWordIndex = new AtomicInteger();
        this.mDefaultPrefixIndex = new AtomicInteger();
        this.mInsertText = new StringBuilder();
        this.conversionHistory = new ArrayList();
        this.mSelectWord = "";
        this.mDefaultWord = new SpannableStringBuilder();
        this.defaultHighlightWord = new StringBuilder(112);
        this.mIsUpdateInline = true;
        this.mExactWord = new StringBuffer();
        this.mTextMeasurePaint = new Paint();
        this.mRows = new ArrayList();
        this.mbNeedDefer = false;
        this.mPredictionSpell = new StringBuilder();
        this.exactInputText = new StringBuilder(112);
        this.altCharacterText = new StringBuilder(112);
        this.convertedCharacterList = new ArrayList();
        this.numZeroPlaceholderIndexList = new ArrayList();
        this.delimiterPlaceholderIndexList = new ArrayList();
        this.mbNeedUpdate = true;
        this.doublePinyinTailKeyUnicode = new AtomicInteger();
        this.chineseSymbolList = new ArrayList();
        this.mSelection = new StringBuilder();
        this.inputViewCallback = new Handler.Callback() { // from class: com.nuance.swype.input.chinese.ChineseInputView.1
            /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
            /* JADX WARN: Code restructure failed: missing block: B:3:0x000b, code lost:            return true;     */
            @Override // android.os.Handler.Callback
            @android.annotation.SuppressLint({"PrivateResource"})
            /*
                Code decompiled incorrectly, please refer to instructions dump.
                To view partially-correct code enable 'Show inconsistent code' option in preferences
            */
            public boolean handleMessage(android.os.Message r8) {
                /*
                    Method dump skipped, instructions count: 550
                    To view this dump change 'Code comments level' option to 'DEBUG'
                */
                throw new UnsupportedOperationException("Method not decompiled: com.nuance.swype.input.chinese.ChineseInputView.AnonymousClass1.handleMessage(android.os.Message):boolean");
            }
        };
        this.inputViewHandler = WeakReferenceHandler.create(this.inputViewCallback);
        this.touchKeyActionHandler = new TouchKeyActionHandler();
        this.inputContextRequestHandler = new InputContextRequestDispatcher.InputContextRequestHandler() { // from class: com.nuance.swype.input.chinese.ChineseInputView.10
            private char[] getTextBeforeCursor(int maxBufferLen) {
                ChineseInputView.log.d("inputContextRequestHandler...getTextBeforeCursor...maxBufferLen: ", Integer.valueOf(maxBufferLen));
                InputConnection ic = ChineseInputView.this.getCurrentInputConnection();
                CharSequence textBeforeCursor = ic != null ? ic.getTextBeforeCursor(maxBufferLen, 0) : null;
                if (textBeforeCursor != null) {
                    return textBeforeCursor.toString().toCharArray();
                }
                return null;
            }

            @Override // com.nuance.swype.input.keyboard.InputContextRequestDispatcher.InputContextRequestHandler
            public char[] getContextBuffer(int maxBufferLen) {
                ChineseInputView.log.d("inputContextRequestHandler...getContextBuffer...maxBufferLen: ", Integer.valueOf(maxBufferLen));
                return getTextBeforeCursor(maxBufferLen);
            }

            @Override // com.nuance.swype.input.keyboard.InputContextRequestDispatcher.InputContextRequestHandler
            public char[] getAutoCapitalizationTextBuffer(int maxBufferLen) {
                ChineseInputView.log.d("inputContextRequestHandler...getAutoCapitalizationTextBuffer...maxBufferLen: ", Integer.valueOf(maxBufferLen));
                return getTextBeforeCursor(maxBufferLen);
            }

            @Override // com.nuance.swype.input.keyboard.InputContextRequestDispatcher.InputContextRequestHandler
            public boolean autoAccept(boolean addSeparator) {
                LogManager.Log log2 = ChineseInputView.log;
                Object[] objArr = new Object[4];
                objArr[0] = "inputContextRequestHandler...autoAccept...addSeparator: ";
                objArr[1] = Boolean.valueOf(addSeparator);
                objArr[2] = "...mLastInput is LAST_INPUT_TRACE: ";
                objArr[3] = Boolean.valueOf(ChineseInputView.this.mLastInput == 2);
                log2.d(objArr);
                if (!ChineseInputView.this.touchKeyActionHandler.shouldIgnoreAutoAccept && (!UserPreferences.from(ChineseInputView.this.getContext()).getEnableChineseBilingual() || !ChineseInputView.this.isPinyinQwertyShiftedState())) {
                    ChineseInputView.this.selectDefault();
                }
                return true;
            }
        };
    }

    public ChineseInputView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mPinyinCandidates = new ArrayList();
        this.showDataList = new ArrayList();
        this.mDefaultWordIndex = new AtomicInteger();
        this.mDefaultPrefixIndex = new AtomicInteger();
        this.mInsertText = new StringBuilder();
        this.conversionHistory = new ArrayList();
        this.mSelectWord = "";
        this.mDefaultWord = new SpannableStringBuilder();
        this.defaultHighlightWord = new StringBuilder(112);
        this.mIsUpdateInline = true;
        this.mExactWord = new StringBuffer();
        this.mTextMeasurePaint = new Paint();
        this.mRows = new ArrayList();
        this.mbNeedDefer = false;
        this.mPredictionSpell = new StringBuilder();
        this.exactInputText = new StringBuilder(112);
        this.altCharacterText = new StringBuilder(112);
        this.convertedCharacterList = new ArrayList();
        this.numZeroPlaceholderIndexList = new ArrayList();
        this.delimiterPlaceholderIndexList = new ArrayList();
        this.mbNeedUpdate = true;
        this.doublePinyinTailKeyUnicode = new AtomicInteger();
        this.chineseSymbolList = new ArrayList();
        this.mSelection = new StringBuilder();
        this.inputViewCallback = new Handler.Callback() { // from class: com.nuance.swype.input.chinese.ChineseInputView.1
            @Override // android.os.Handler.Callback
            @SuppressLint({"PrivateResource"})
            public boolean handleMessage(Message message) {
                /*  JADX ERROR: Method code generation error
                    java.lang.NullPointerException
                    */
                /*
                    Method dump skipped, instructions count: 550
                    To view this dump change 'Code comments level' option to 'DEBUG'
                */
                throw new UnsupportedOperationException("Method not decompiled: com.nuance.swype.input.chinese.ChineseInputView.AnonymousClass1.handleMessage(android.os.Message):boolean");
            }
        };
        this.inputViewHandler = WeakReferenceHandler.create(this.inputViewCallback);
        this.touchKeyActionHandler = new TouchKeyActionHandler();
        this.inputContextRequestHandler = new InputContextRequestDispatcher.InputContextRequestHandler() { // from class: com.nuance.swype.input.chinese.ChineseInputView.10
            private char[] getTextBeforeCursor(int maxBufferLen) {
                ChineseInputView.log.d("inputContextRequestHandler...getTextBeforeCursor...maxBufferLen: ", Integer.valueOf(maxBufferLen));
                InputConnection ic = ChineseInputView.this.getCurrentInputConnection();
                CharSequence textBeforeCursor = ic != null ? ic.getTextBeforeCursor(maxBufferLen, 0) : null;
                if (textBeforeCursor != null) {
                    return textBeforeCursor.toString().toCharArray();
                }
                return null;
            }

            @Override // com.nuance.swype.input.keyboard.InputContextRequestDispatcher.InputContextRequestHandler
            public char[] getContextBuffer(int maxBufferLen) {
                ChineseInputView.log.d("inputContextRequestHandler...getContextBuffer...maxBufferLen: ", Integer.valueOf(maxBufferLen));
                return getTextBeforeCursor(maxBufferLen);
            }

            @Override // com.nuance.swype.input.keyboard.InputContextRequestDispatcher.InputContextRequestHandler
            public char[] getAutoCapitalizationTextBuffer(int maxBufferLen) {
                ChineseInputView.log.d("inputContextRequestHandler...getAutoCapitalizationTextBuffer...maxBufferLen: ", Integer.valueOf(maxBufferLen));
                return getTextBeforeCursor(maxBufferLen);
            }

            @Override // com.nuance.swype.input.keyboard.InputContextRequestDispatcher.InputContextRequestHandler
            public boolean autoAccept(boolean addSeparator) {
                LogManager.Log log2 = ChineseInputView.log;
                Object[] objArr = new Object[4];
                objArr[0] = "inputContextRequestHandler...autoAccept...addSeparator: ";
                objArr[1] = Boolean.valueOf(addSeparator);
                objArr[2] = "...mLastInput is LAST_INPUT_TRACE: ";
                objArr[3] = Boolean.valueOf(ChineseInputView.this.mLastInput == 2);
                log2.d(objArr);
                if (!ChineseInputView.this.touchKeyActionHandler.shouldIgnoreAutoAccept && (!UserPreferences.from(ChineseInputView.this.getContext()).getEnableChineseBilingual() || !ChineseInputView.this.isPinyinQwertyShiftedState())) {
                    ChineseInputView.this.selectDefault();
                }
                return true;
            }
        };
    }

    boolean isDigit(int ch) {
        return 48 <= ch && ch <= 57;
    }

    private boolean isModeCangjie() {
        String mode = this.currentInputMode.mInputMode;
        return InputMethods.isChineseInputModeCangjie(mode) || InputMethods.isChineseInputModeQuickCangjie(mode);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isCangjieKeypad() {
        String mode = this.currentInputMode.mInputMode;
        return InputMethods.CHINESE_INPUT_MODE_CANGJIE_NINE_KEYS.equals(mode) || InputMethods.CHINESE_INPUT_MODE_QUICK_CANGJIE_NINE_KEYS.equals(mode);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isCangjieQwerty() {
        String mode = this.currentInputMode.mInputMode;
        log.d("isCangjieQwerty...mode:", mode);
        return InputMethods.CHINESE_INPUT_MODE_CANGJIE_QWERTY.equals(mode) || InputMethods.CHINESE_INPUT_MODE_QUICK_CANGJIE_QWERTY.equals(mode);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isCangjieWildCard(int code) {
        return code == 65311;
    }

    private boolean isPhoneticKeypad() {
        return InputMethods.CHINESE_INPUT_MODE_PINYIN_NINE_KEYS.equals(this.currentInputMode.mInputMode) || InputMethods.CHINESE_INPUT_MODE_ZHUYIN_NINE_KEYS.equals(this.currentInputMode.mInputMode);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isModePinyin() {
        return InputMethods.isChineseInputModePinyin(this.currentInputMode.mInputMode);
    }

    private boolean isModeNineKeyPinyin() {
        return InputMethods.isChineseNineKeyPinyin(this.currentInputMode.mInputMode);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isModeDoublePinyin() {
        return this.currentInputMode.mInputMode.compareTo(InputMethods.CHINESE_INPUT_MODE_DOUBLEPINYIN) == 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isModeBPMF() {
        return InputMethods.isChineseInputModeZhuyin(this.currentInputMode.mInputMode);
    }

    @Override // com.nuance.swype.input.InputView
    public boolean isModeStroke() {
        return this.currentInputMode != null && this.currentInputMode.mInputMode.equals(InputMethods.CHINESE_INPUT_MODE_STROKE);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void selectDefault() {
        selectWord(0, null, this.candidatesListViewCJK);
        this.touchKeyActionHandler.clearAllKeys();
        multitapClearInvalid();
    }

    private void updateInlineString(boolean setEmptyComposingText) {
        updateInline();
        InputConnection ic = getCurrentInputConnection();
        if (ic != null && ((setEmptyComposingText || this.mDefaultWord.length() > 0) && isNormalTextInputMode())) {
            if (this.mIme.isFullscreenMode()) {
                ic.setComposingText(this.mDefaultWord, 1);
            } else if (this.mHandWritingOnKeyboardHandler != null && this.mHandWritingOnKeyboardHandler.recognitionCandidatesAvailable()) {
                ic.setComposingText(this.mDefaultWord, 1);
            } else {
                updatInlineTextView();
            }
            if (!this.mIsDelimiterKeyLabelUpdated && hasActiveKeySeq() && !this.mKeyboardSwitcher.isSymbolMode() && !this.isDealGesture) {
                updateDelimiterKeyLabel(true);
            }
            if (!this.mIsClearKeyLabelUpdated && hasActiveKeySeq() && !this.mKeyboardSwitcher.isSymbolMode() && !this.isDealGesture) {
                updateClearKeyLabel(true);
            }
        }
        if (isModeDoublePinyin()) {
            invalidateKeyboardImage();
        }
    }

    private void updateInline() {
        if (this.mHandWritingOnKeyboardHandler == null || !this.mHandWritingOnKeyboardHandler.recognitionCandidatesAvailable()) {
            if (this.mChineseInput != null) {
                this.mChineseInput.getInlineString(this.mDefaultWord);
                if (isModeDoublePinyin()) {
                    String wd = this.mDefaultWord.toString();
                    log.d("word length: ", Integer.valueOf(wd.length()));
                    if (wd.length() == 1 && wd.equals("\n")) {
                        this.mDefaultWord.clear();
                        this.mDefaultWord.append((CharSequence) XMLResultsHandler.SEP_HYPHEN);
                    }
                }
            } else {
                return;
            }
        }
        if (isModeDoublePinyin()) {
            this.doublePinyinTailKeyUnicode.set(0);
            this.mChineseInput.getTailDoublePinyinUnicode(this.doublePinyinTailKeyUnicode);
            log.d("Tail double pinyin unicode value: ", Integer.toHexString(this.doublePinyinTailKeyUnicode.intValue()));
        }
        if (this.mDefaultWord.length() > 0) {
            this.mDefaultWord.setSpan(this.mUnderline, 0, this.mDefaultWord.length(), R.styleable.ThemeTemplate_emojiCategoryIconSmiles);
            return;
        }
        this.mTerminateSession = false;
        this.isCandidateSelectedEver = false;
        this.isDelimiterPressedEver = false;
        this.isTracedEver = false;
        this.mTone = false;
        if (this.candidatesListViewCJK != null) {
            this.candidatesListViewCJK.setLeftArrowStatus(false);
        }
    }

    private void updateDelimiterKeyLabel(boolean isDelimiter) {
        if (!isModeDoublePinyin() && isNormalTextInputMode() && !isModeCangjie()) {
            if (isShifted()) {
                isDelimiter = false;
            }
            KeyboardEx keyboard = getKeyboard();
            if (keyboard != null && (keyboard instanceof XT9Keyboard) && !this.mIme.isHardKeyboardActive()) {
                KeyboardEx.Key key = ((XT9Keyboard) keyboard).updateDelimiterKeyLabel(isDelimiter, this.mKeyboardSwitcher.isKeypadInput(), isModePinyin(), isModeStroke(), isTabletDevice());
                if (key != null) {
                    invalidateKey(key);
                    this.mIsDelimiterKeyLabelUpdated = isDelimiter;
                }
            }
        }
    }

    private void updateClearKeyLabel(boolean isClearKey) {
        if (!isModeDoublePinyin() && isNormalTextInputMode()) {
            if (isMultitapping() && isCangjieKeypad()) {
                isClearKey = true;
            }
            if (!KeyboardEx.shouldEnableSpeechKey(getContext())) {
                isClearKey = true;
            }
            KeyboardEx keyboard = getKeyboard();
            if (keyboard != null && (keyboard instanceof XT9Keyboard)) {
                KeyboardEx.Key key = ((XT9Keyboard) keyboard).updateClearKeyLabel(isClearKey, this.mKeyboardSwitcher.isKeypadInput(), isModePinyin(), isModeStroke(), isTabletDevice());
                if (key != null) {
                    invalidateKey(key);
                    this.mIsClearKeyLabelUpdated = isClearKey;
                }
            }
        }
    }

    private boolean isQwertyLayout() {
        return this.mKeyboardLayoutId == 2304;
    }

    private boolean isHasSegmentDelimiter() {
        return this.mChineseInput.isHasSegmentDelimiter();
    }

    private void flashError() {
        if (this.mIme.isFullscreenMode() && this.mDefaultWord.length() > 0) {
            flashBackground();
            updateInlineString(true);
        }
    }

    private void flashRedError() {
        if (this.mIme.isFullscreenMode() && this.mDefaultWord.length() > 0) {
            flashBackground();
            this.inputViewHandler.sendMessageDelayed(this.inputViewHandler.obtainMessage(4, 0, 0), 100L);
        }
    }

    private void flashBackground() {
        this.mDefaultWord.setSpan(this.mErrorFlashBackground, 0, this.mDefaultWord.length(), R.styleable.ThemeTemplate_emojiCategoryIconSmiles);
        InputConnection ic = getCurrentInputConnection();
        if (ic != null) {
            ic.setComposingText(this.mDefaultWord, 1);
        }
    }

    @SuppressLint({"PrivateResource"})
    private void readStyles(Context context) {
        TypedArrayWrapper a = IMEApplication.from(context).getThemeLoader().obtainStyledAttributes$6d3b0587(context, null, R.styleable.FormatTextChinese, 0, R.style.FormatTextChinese, R.xml.defaults, "FormatTextChinese");
        int n = a.delegateTypedArray.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.FormatTextChinese_isUnderlined) {
                if (a.getBoolean(attr, true)) {
                    this.mUnderline = new UnderlineSpan();
                }
            } else if (attr == R.styleable.FormatTextChinese_errorFlashBackgroundColor) {
                this.mErrorFlashBackground = new BackgroundColorSpan(a.getColor(attr, -65536));
            } else if (attr == R.styleable.FormatTextChinese_invalidForegroundColor) {
                this.mInvalidForeground = new ForegroundColorSpan(a.getColor(attr, -65536));
            }
        }
        a.recycle();
        if (this.mHandWritingOnKeyboardHandler != null) {
            this.mHandWritingOnKeyboardHandler.onReadStyles(context);
        }
        IMEApplication app = IMEApplication.from(context);
        this.wclCjkChineseCloudBackgroundImage = app.getThemedDrawable(R.attr.wclCjkChineseCloudBackgroundImage);
        this.wclCjkChineseCloudBackgroundHighlight = app.getThemedDrawable(R.attr.wclCjkChineseCloudBackgroundPressedImage);
    }

    private boolean checkCurLanguage() {
        return this.mCurrentInputLanguage != null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean hasActiveKeySeq() {
        return (this.mChineseInput != null && this.mChineseInput.getKeyCount() > 0) || multitapIsInvalid();
    }

    public void setContainerView(ChineseOneStepPYContainer container) {
        this.mOneStepPYContainer = container;
    }

    private void handleAutoPunct() {
        InputConnection ic = getCurrentInputConnection();
        if (ic != null) {
            this.mEditState.punctuationOrSymbols();
            ic.deleteSurroundingText(1, 0);
            ic.commitText("。", 1);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hideGridCandidatesView() {
        if (this.mContextWindowShowing) {
            this.mOneStepPYContainer.hideContextWindow(this.candidatesPopup);
            this.mOneStepPYContainer.setMinimumHeight(0);
        }
        this.gridCandidateTableVisible = false;
    }

    private void showPinyinSelectionList(List<CharSequence> aDataList, int activePrefixIndex, boolean highlightPrefix) {
        if (aDataList != null && !aDataList.isEmpty()) {
            if (this.gridCandidateTableVisible && this.spellPrefixSuffixListView != null && ((ChineseKeyboardSwitcher) this.mKeyboardSwitcher).isMixedInput()) {
                this.spellPrefixSuffixListView.setSuggestions(aDataList, activePrefixIndex);
                if ((this.mInputFieldInfo == null || !this.mInputFieldInfo.isNumericModeField()) && this.gridCandidateTableVisible) {
                    this.mChnPinyinAndPhraseListViewContainer.showSpellPrefixSuffixList();
                    if (this.inputViewHandler.hasMessages(11)) {
                        this.inputViewHandler.removeMessages(11);
                    }
                    this.inputViewHandler.removeMessages(500);
                    this.inputViewHandler.sendEmptyMessageDelayed(500, 1L);
                }
            }
            if (this.mKeyboardSwitcher.isKeypadInput() || isModeStroke()) {
                if ((!isTraceInputEnabled() || aDataList.get(0).toString().equals(this.chineseSymbolList.get(0).toString())) && this.mOneStepPYContainer != null) {
                    this.mOneStepPYContainer.setSpellPrefix(!isTabletDevice() && isPhoneticKeypad() && hasActiveKeySeq() && !isAltCharacterToggled());
                    this.mOneStepPYContainer.showPrefixList(aDataList, activePrefixIndex, highlightPrefix);
                }
            }
        }
    }

    private void showGridCandidatesView(List<CharSequence> aDataList) {
        showGridCandidatesView(aDataList, null);
    }

    @SuppressLint({"InflateParams", "PrivateResource"})
    private void showGridCandidatesView(List<CharSequence> aDataList, List<AtomicInteger> aWordSourceList) {
        int height;
        if (aDataList != null && !aDataList.isEmpty() && !this.mContextWindowShowing && !super.isSpeechViewShowing()) {
            this.candidatesPopupDataList = getPureCandidates(aDataList);
            if (isModeStroke()) {
                for (CharSequence cs : aDataList) {
                    if (cs.charAt(0) != 40959 || cs.charAt(1) != '~') {
                        this.showDataList.add(cs);
                    }
                }
            }
            this.mChnPinyinAndPhraseListViewContainer.hidePhraseListView();
            if (!((ChineseKeyboardSwitcher) this.mKeyboardSwitcher).isMixedInput()) {
                setCandidatesViewShown(false);
                this.mChnPinyinAndPhraseListViewContainer.hideSpellPrefixSuffixList();
                int candidateHeight = this.mChnPinyinAndPhraseListViewContainer.getHeight();
                if (candidateHeight != 0) {
                    height = getHeight() + candidateHeight;
                } else {
                    height = getHeight() + getResources().getDimensionPixelSize(R.dimen.candidates_list_height);
                }
            } else {
                setCandidatesViewShown(true);
                height = getHeight();
            }
            int width = getKeyboard().getMinWidth();
            this.mOneStepPYContainer.setMinimumHeight(height);
            if (this.candidatesPopup != null && this.candidatesPopup.getMeasuredWidth() != width) {
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
                closeButton.setOnClickListener(new View.OnClickListener() { // from class: com.nuance.swype.input.chinese.ChineseInputView.3
                    @Override // android.view.View.OnClickListener
                    public void onClick(View v) {
                        ChineseInputView.this.hideGridCandidatesView();
                        ChineseInputView.this.setCandidatesViewShown(true);
                        if (ChineseInputView.this.mChnPinyinAndPhraseListViewContainer != null && !ChineseInputView.this.mKeyboardSwitcher.isKeypadInput()) {
                            ChineseInputView.this.mChnPinyinAndPhraseListViewContainer.hideSpellPrefixSuffixList();
                            ChineseInputView.this.mChnPinyinAndPhraseListViewContainer.showPhraseListView();
                            if (ChineseInputView.this.mChineseInput.getActivePrefixIndex() == 254) {
                                ChineseInputView.this.mChineseInput.setActivePrefixIndex(-1);
                            }
                        }
                        if (ChineseInputView.this.mKeyboardSwitcher.isKeypadInput() && (ChineseInputView.this.isModeBPMF() || ChineseInputView.this.isModePinyin())) {
                            ChineseInputView.this.updatePrefixes();
                        }
                        ChineseInputView.this.mTerminateSession = true;
                        ChineseInputView.this.updateSuggestions(Candidates.Source.INVALID);
                        if (ChineseInputView.this.shouldStartChinesePrediction() && ChineseInputView.this.mChinesePredictionResult != null) {
                            ChineseInputView.this.postUpdateChinesePredictionMessage(true, ChineseInputView.this.mChinesePredictionResult, 2, 10L);
                        }
                    }
                });
                closeButton.setOnTouchListener(new View.OnTouchListener() { // from class: com.nuance.swype.input.chinese.ChineseInputView.4
                    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
                    /* JADX WARN: Code restructure failed: missing block: B:3:0x0008, code lost:            return false;     */
                    @Override // android.view.View.OnTouchListener
                    @android.annotation.SuppressLint({"ClickableViewAccessibility"})
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
                            com.nuance.swype.input.chinese.ChineseInputView r0 = com.nuance.swype.input.chinese.ChineseInputView.this
                            r1 = 1
                            com.nuance.swype.input.chinese.ChineseInputView.access$4202(r0, r1)
                            goto L8
                        L10:
                            com.nuance.swype.input.chinese.ChineseInputView r0 = com.nuance.swype.input.chinese.ChineseInputView.this
                            com.nuance.swype.input.chinese.ChineseInputView.access$4202(r0, r2)
                            goto L8
                        */
                        throw new UnsupportedOperationException("Method not decompiled: com.nuance.swype.input.chinese.ChineseInputView.AnonymousClass4.onTouch(android.view.View, android.view.MotionEvent):boolean");
                    }
                });
                this.candidatesPopupScrollView = (ScrollView) this.candidatesPopup.findViewById(R.id.scroll_view);
                this.candidatesPopupKeyboardViewEx = (ChineseKeyboardViewEx) this.candidatesPopup.findViewById(R.id.keyboardViewEx);
                this.candidatesPopupKeyboardViewEx.setContextCandidatesView(true);
            }
            this.candidatesPopupScrollView.scrollTo(0, 0);
            this.candidatesPopupKeyboardViewEx.setInputView(this);
            this.candidatesPopupKeyboardViewEx.setWordSource(aWordSourceList);
            this.candidatesPopupKeyboardViewEx.setDoubleBuffered(false);
            List<CharSequence> keyboardDataList = isModeStroke() ? this.showDataList : this.candidatesPopupDataList;
            this.candidatesPopupKeyboardViewEx.setGridCandidates(this.mRows, keyboardDataList, this.candidatesPopupScrollView.getMeasuredWidth());
            final KeyboardEx keyboard = new KeyboardEx(getContext(), R.xml.kbd_chinese_popup_template, this.mRows, this.candidatesPopupScrollView.getMeasuredWidth(), height);
            this.candidatesPopupKeyboardViewEx.setKeyboard(keyboard);
            this.candidatesPopupKeyboardViewEx.setIme(this.mIme);
            this.gridCandidateTableVisible = true;
            if (this.showDataList != null) {
                this.showDataList.clear();
            }
            this.candidatesPopupKeyboardViewEx.setOnKeyboardActionListener(new KeyboardViewEx.KeyboardActionAdapter() { // from class: com.nuance.swype.input.chinese.ChineseInputView.5
                @Override // com.nuance.swype.input.KeyboardViewEx.OnKeyboardActionListener
                public void onText(CharSequence text, long eventTime) {
                    ChineseInputView.log.d("Chinese inputview onText:", text);
                    if (!ChineseInputView.this.gridViewFunctionButtonPressed && text != null) {
                        int i = 0;
                        while (true) {
                            if (i >= ChineseInputView.this.candidatesPopupDataList.size()) {
                                break;
                            }
                            if (!((CharSequence) ChineseInputView.this.candidatesPopupDataList.get(i)).toString().equals(text.toString())) {
                                i++;
                            } else {
                                ChineseInputView chineseInputView = ChineseInputView.this;
                                if (ChineseInputView.this.candidatesListViewCJK.isExactKeyboardShowable()) {
                                    i++;
                                }
                                chineseInputView.selectWord(i, text, ChineseInputView.this.candidatesListViewCJK);
                            }
                        }
                        ChineseInputView.this.hideGridCandidatesView();
                        if (!ChineseInputView.this.hasActiveKeySeq() || !((ChineseKeyboardSwitcher) ChineseInputView.this.mKeyboardSwitcher).isMixedInput()) {
                            if (ChineseInputView.this.hasActiveKeySeq() && ChineseInputView.this.mKeyboardSwitcher.isKeypadInput()) {
                                ChineseInputView.this.setCandidatesViewShown(true);
                                ChineseInputView.this.mChnPinyinAndPhraseListViewContainer.showPhraseListView();
                                return;
                            } else {
                                ChineseInputView.this.setCandidatesViewShown(true);
                                return;
                            }
                        }
                        ChineseInputView.this.nextBtnPressed(ChineseInputView.this.candidatesListViewCJK);
                    }
                }

                @Override // com.nuance.swype.input.KeyboardViewEx.KeyboardActionAdapter, com.nuance.swype.input.KeyboardViewEx.OnKeyboardActionListener
                public void onRelease(int primaryCode) {
                    keyboard.clearStickyKeys();
                }
            });
            this.candidatesPopup.setLayoutParams(new FrameLayout.LayoutParams(getKeyboard().getMinWidth(), height));
            this.mOneStepPYContainer.showContextWindow(this.candidatesPopup);
        }
    }

    public void handleChar(char ch) {
        if (ch == '\r') {
            ch = '\n';
        } else if (ch == '\t') {
            ch = ' ';
        }
        if (ch == '\b') {
            sendBackspace(1);
        } else if (ch == ' ') {
            if (this.mHandWritingOnKeyboardHandler != null) {
                this.mHandWritingOnKeyboardHandler.acceptHWRRecognitionWord();
            }
            sendSpace();
        } else {
            if (this.mHandWritingOnKeyboardHandler != null) {
                this.mHandWritingOnKeyboardHandler.acceptHWRRecognitionWord();
            }
            sendKeyChar(ch);
        }
        if (this.mHandWritingOnKeyboardHandler != null) {
            this.mHandWritingOnKeyboardHandler.clearRecognitionCandidates();
        }
        updateSuggestions(Candidates.Source.TAP);
    }

    private void acceptHWRAndUpdateListView() {
        if (this.mHandWritingOnKeyboardHandler != null && this.mHandWritingOnKeyboardHandler.acceptHWRRecognitionWord()) {
            updateListViews(true, true);
        }
    }

    @Override // com.nuance.input.swypecorelib.T9WriteRecognizerListener.OnWriteRecognizerListener
    public void onHandleWriteEvent(T9WriteRecognizerListener.WriteEvent event) {
        if (this.mHandWritingOnKeyboardHandler != null) {
            this.mHandWritingOnKeyboardHandler.onHandleWriteEvent(event);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.KeyboardViewEx
    public boolean isWriteInputEnabled() {
        return (!IMEApplication.from(getContext()).getSwypeCoreLibMgr().getSwypeCoreLibInstance().isWriteChineseBuildEnabled() || !this.mHandwritingOn || hasActiveKeySeq() || this.mTraceOn || this.mKeyboardSwitcher.isSymbolMode() || this.mKeyboardSwitcher.isPhoneMode() || this.mKeyboardSwitcher.isNumMode() || this.mKeyboardSwitcher.isEditMode() || getKeyboard().getKeyboardDockMode() == KeyboardEx.KeyboardDockMode.DOCK_SPLIT || isAltCharacterToggled() || isPopupKeyboardShowing()) ? false : true;
    }

    @Override // com.nuance.swype.input.KeyboardViewEx, android.view.View
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isWriteInputEnabled() && this.mHandWritingOnKeyboardHandler != null) {
            this.mHandWritingOnKeyboardHandler.onDraw(canvas);
        }
    }

    @Override // com.nuance.swype.input.KeyboardViewEx
    public boolean isWriting() {
        return this.mHandWritingOnKeyboardHandler != null && this.mHandWritingOnKeyboardHandler.isWriting();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.KeyboardViewEx
    public void clearKeyboardState() {
        super.clearKeyboardState();
        if (this.mHandWritingOnKeyboardHandler != null) {
            this.mHandWritingOnKeyboardHandler.onClearKeyboardState();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.KeyboardViewEx
    public boolean popupMiniKeyboardOrLongPress() {
        return !isModeStroke() && super.popupMiniKeyboardOrLongPress();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.KeyboardViewEx
    public boolean onLongPress(KeyboardEx.Key key) {
        if (isModePinyin() && this.mKeyboardSwitcher.isKeypadInput() && key.altCode == 49) {
            return false;
        }
        if (isModeCangjie() && this.mKeyboardSwitcher.isKeypadInput() && key.altCode == 48) {
            return false;
        }
        if (key.codes[0] == 4065 && (this.mKeyboardSwitcher.isKeypadInput() || isModeStroke())) {
            return false;
        }
        return super.onLongPress(key);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.KeyboardViewEx
    public boolean onShortPress(KeyboardEx.Key key, int keyIndex, int pointerId) {
        if (isModeDoublePinyin() && key.label != null && key.label.length() > 0 && key.label.charAt(0) >= 'a' && key.label.charAt(0) <= 'z') {
            return false;
        }
        if (isModePinyin()) {
            boolean hasAltKeyEvent = hasAltSymbolOrCode(key);
            boolean hasSymbolSelectPopup = hasSymbolSelectPopupResource(key) || (key.altLabel != null && key.altLabel.equals("1"));
            boolean isAltDigitQwerty = isQwertyLayout() && !isShifted();
            boolean isSymbolKey = key.codes != null && key.codes[0] == 4077;
            boolean isEnterKey = key.codes != null && (key.codes[0] == 10 || key.codes[0] == 4074);
            if (hasAltKeyEvent && hasSymbolSelectPopup && hasActiveKeySeq() && !isAltDigitQwerty && !isSymbolKey && !isEnterKey && displaysAltSymbol(key) && isSlideSelectEnabled()) {
                if (!this.mHandler.hasMessages(1005)) {
                    return false;
                }
                this.mHandler.removeMessages(1005);
                return false;
            }
            if (isShifted() && hasSymbolSelectPopup && isModePinyin()) {
                this.mKeyboardSwitcher.setShiftState(Shift.ShiftState.OFF);
                this.isShiftedPopupMenu = true;
            }
        }
        return super.onShortPress(key, keyIndex, pointerId);
    }

    @Override // com.nuance.swype.input.KeyboardViewEx
    public Shift.ShiftState getShiftState() {
        Shift.ShiftState shiftState = Shift.ShiftState.OFF;
        if (this.isShiftedPopupMenu) {
            return shiftState;
        }
        Shift.ShiftState shiftState2 = super.getShiftState();
        return shiftState2;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.KeyboardViewEx
    public void handleTouchAction(MotionEvent me, KeyboardViewEx.Pointer pointer, int action) {
        if (subHandleTouchInitialized(me, pointer, action) && pointer.getCurrentLocation() != null) {
            switch (action) {
                case 0:
                    super.subHandleTouchDown(me, pointer, action);
                    return;
                case 1:
                    super.subHandleTouchUp(me, pointer, action);
                    if (this.isShiftedPopupMenu) {
                        this.isShiftedPopupMenu = false;
                        this.mKeyboardSwitcher.setShiftState(Shift.ShiftState.LOCKED);
                        return;
                    }
                    return;
                case 2:
                    super.subHandleTouchMove(me, pointer, action);
                    return;
                default:
                    return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.KeyboardViewEx
    public boolean isTraceInputEnabled() {
        return this.isTraceEnabledOnKeyboard && !isAltCharacterToggled();
    }

    public boolean flushInlineWord() {
        InputConnection ic = getCurrentInputConnection();
        if (ic != null) {
            ic.beginBatchEdit();
            if (this.mIme.isFullscreenMode()) {
                ic.finishComposingText();
            } else if (this.mDefaultWord.length() > 0) {
                ic.commitText(this.mDefaultWord.toString(), 1);
            }
            this.mInitiativeAccept = true;
            this.mbNeedUpdate = false;
            this.mDefaultWord.clear();
            ic.endBatchEdit();
        }
        return true;
    }

    private boolean hasTone() {
        return !isAltCharacterToggled() && this.mTone;
    }

    private int getInputDelimiterCount() {
        int count = 0;
        for (int i = 0; i < this.mDefaultWord.length(); i++) {
            if (this.mDefaultWord.charAt(i) == this.mPYDelimiter) {
                count++;
            }
        }
        return count;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isComponent() {
        if ((this.mHandWritingOnKeyboardHandler != null && this.mHandWritingOnKeyboardHandler.recognitionCandidatesAvailable()) || isAltCharacterToggled()) {
            return false;
        }
        List<CharSequence> wordList = getWords(this.mDefaultWord, this.mDefaultWordIndex);
        return wordList != null && wordList.size() > 0 && wordList.get(0).charAt(0) == 40959;
    }

    private boolean getCurrentInputModeHandwritingOnkeyboardKey(boolean defaultValue) {
        AppPreferences appPrefs = AppPreferences.from(getContext());
        return this.currentInputMode.isHandwritingEnabled() && appPrefs.getBoolean(this.currentInputMode.getHandwritingOnKeyboardKey(), defaultValue);
    }

    private boolean detectCurrentHandwritingOn() {
        this.mHandwritingOn = false;
        if (IMEApplication.from(getContext()).getSwypeCoreLibMgr().getSwypeCoreLibInstance().isWriteChineseBuildEnabled()) {
            if ((isModeBPMF() || isModePinyin() || isModeDoublePinyin()) && this.mKeyboardLayoutId == 2304 && !IMEApplication.from(getContext()).getSwypeCoreLibMgr().getSwypeCoreLibInstance().isTraceBuildEnabled()) {
                this.mHandwritingOn = getCurrentInputModeHandwritingOnkeyboardKey(true);
            } else {
                this.mHandwritingOn = getCurrentInputModeHandwritingOnkeyboardKey(false);
            }
            this.mHandwritingOn = this.mHandwritingOn && UserPreferences.from(getContext()).isHandwritingEnabled();
        }
        return this.mHandwritingOn;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void loadSettings() {
        AppPreferences appPrefs = AppPreferences.from(getContext());
        detectCurrentHandwritingOn();
        boolean lastHandwritingOn = this.mHandwritingOn;
        if (IMEApplication.from(getContext()).getSwypeCoreLibMgr().getSwypeCoreLibInstance().isWriteChineseBuildEnabled() && !this.mHandwritingOn) {
            if (lastHandwritingOn && this.mHandWritingOnKeyboardHandler != null) {
                this.mHandWritingOnKeyboardHandler.onDestroy();
            }
            appPrefs.setBoolean(this.currentInputMode.getHandwritingOnKeyboardKey(), false);
            if (this.mKeyboardLayoutId == 1536) {
                appPrefs.setBoolean(this.currentInputMode.getTraceOnKeyboardKey(), false);
            } else if (!isModeBPMF() && !isModePinyin() && !isModeDoublePinyin()) {
                appPrefs.setBoolean(this.currentInputMode.getTraceOnKeyboardKey(), false);
            } else {
                appPrefs.setBoolean(this.currentInputMode.getTraceOnKeyboardKey(), true);
            }
        }
        if (IMEApplication.from(getContext()).getSwypeCoreLibMgr().getSwypeCoreLibInstance().isTraceBuildEnabled()) {
            this.mTraceOn = appPrefs.getBoolean(this.currentInputMode.getTraceOnKeyboardKey(), true);
        }
        if ((!isModeBPMF() && !isModePinyin() && !isModeDoublePinyin()) || this.mKeyboardLayoutId != 2304) {
            this.mTraceOn = false;
        }
        updateKbdTraceState();
        if ((this.mKeyboardSwitcher.isAlphabetMode() && this.mKeyboardLayoutId == 1536) || isModeStroke()) {
            setPressDownPreviewEnabled(false);
        }
    }

    @Override // android.view.View
    protected void onWindowVisibilityChanged(int visibility) {
        if (this.mHandWritingOnKeyboardHandler != null) {
            this.mHandWritingOnKeyboardHandler.onWindowVisibilityChanged();
        }
        super.onWindowVisibilityChanged(visibility);
    }

    @Override // com.nuance.swype.input.chinese.CJKCandidatesListView.OnWordSelectActionListener
    public void nextBtnPressed(CJKCandidatesListView aSource) {
        if (!(aSource instanceof VerCandidatesListView)) {
            dismissPopupKeyboard();
            if (aSource == this.candidatesListViewCJK) {
                if (this.mHandWritingOnKeyboardHandler == null || this.mHandWritingOnKeyboardHandler.isWritingRecognitionDone()) {
                    if (this.mHandWritingOnKeyboardHandler != null && this.mHandWritingOnKeyboardHandler.recognitionCandidatesAvailable()) {
                        showGridCandidatesView(this.mHandWritingOnKeyboardHandler.getHWRecognitionCandidates());
                    } else {
                        List<CharSequence> candList = this.mChineseInput.getPopupList();
                        List<AtomicInteger> wdSourceList = this.mChineseInput.getPopupWordSourceList();
                        showGridCandidatesView(candList, wdSourceList);
                    }
                    if (((ChineseKeyboardSwitcher) this.mKeyboardSwitcher).isMixedInput()) {
                        updatePrefixes();
                    }
                    if (this.candidatesListViewCJK != null) {
                        this.candidatesListViewCJK.setExactKeyboardShowable(false);
                        this.candidatesListViewCJK.setLeftArrowStatus(false);
                    }
                }
            }
        }
    }

    @SuppressLint({"PrivateResource"})
    public void updateGridViewByPrefix(CJKCandidatesListView aSource) {
        if (!(aSource instanceof VerCandidatesListView) && aSource == this.candidatesListViewCJK) {
            if (this.mHandWritingOnKeyboardHandler == null || this.mHandWritingOnKeyboardHandler.isWritingRecognitionDone()) {
                List<CharSequence> candList = this.mChineseInput.getPopupList();
                List<AtomicInteger> wdSourceList = this.mChineseInput.getPopupWordSourceList();
                if (candList != null && !candList.isEmpty()) {
                    this.candidatesPopupDataList = getPureCandidates(candList);
                    int height = getHeight();
                    this.mOneStepPYContainer.setMinimumHeight(height);
                    this.candidatesPopupScrollView.scrollTo(0, 0);
                    this.candidatesPopupKeyboardViewEx.setInputView(this);
                    this.candidatesPopupKeyboardViewEx.setWordSource(wdSourceList);
                    this.candidatesPopupKeyboardViewEx.setGridCandidates(this.mRows, this.candidatesPopupDataList, this.candidatesPopupScrollView.getMeasuredWidth());
                    final KeyboardEx keyboard = new KeyboardEx(getContext(), R.xml.kbd_chinese_popup_template, this.mRows, this.candidatesPopupScrollView.getMeasuredWidth(), height);
                    this.candidatesPopupKeyboardViewEx.setKeyboard(keyboard);
                    this.gridCandidateTableVisible = true;
                    this.candidatesPopupKeyboardViewEx.setOnKeyboardActionListener(new KeyboardViewEx.KeyboardActionAdapter() { // from class: com.nuance.swype.input.chinese.ChineseInputView.6
                        @Override // com.nuance.swype.input.KeyboardViewEx.OnKeyboardActionListener
                        public void onText(CharSequence text, long eventTime) {
                            if (!ChineseInputView.this.gridViewFunctionButtonPressed && text != null) {
                                int i = 0;
                                while (true) {
                                    if (i >= ChineseInputView.this.candidatesPopupDataList.size()) {
                                        break;
                                    }
                                    if (!((CharSequence) ChineseInputView.this.candidatesPopupDataList.get(i)).toString().equals(text.toString())) {
                                        i++;
                                    } else {
                                        ChineseInputView.this.selectWord(i, text, ChineseInputView.this.candidatesListViewCJK);
                                        break;
                                    }
                                }
                                ChineseInputView.this.hideGridCandidatesView();
                                if (!ChineseInputView.this.hasActiveKeySeq() || !((ChineseKeyboardSwitcher) ChineseInputView.this.mKeyboardSwitcher).isMixedInput()) {
                                    if (ChineseInputView.this.hasActiveKeySeq() && ChineseInputView.this.mKeyboardSwitcher.isKeypadInput()) {
                                        ChineseInputView.this.setCandidatesViewShown(true);
                                        ChineseInputView.this.mChnPinyinAndPhraseListViewContainer.showPhraseListView();
                                        ChineseInputView.this.updatePrefixes();
                                        return;
                                    }
                                    ChineseInputView.this.setCandidatesViewShown(true);
                                    return;
                                }
                                ChineseInputView.this.nextBtnPressed(ChineseInputView.this.candidatesListViewCJK);
                            }
                        }

                        @Override // com.nuance.swype.input.KeyboardViewEx.KeyboardActionAdapter, com.nuance.swype.input.KeyboardViewEx.OnKeyboardActionListener
                        public void onRelease(int primaryCode) {
                            keyboard.clearStickyKeys();
                        }
                    });
                    this.candidatesPopup.setLayoutParams(new FrameLayout.LayoutParams(getKeyboard().getMinWidth(), height));
                    this.mOneStepPYContainer.showContextWindow(this.candidatesPopup);
                }
            }
        }
    }

    @Override // com.nuance.swype.input.chinese.CJKCandidatesListView.OnWordSelectActionListener
    public void prevBtnPressed(CJKCandidatesListView aSource) {
        if (!(aSource instanceof VerCandidatesListView)) {
            if (aSource == this.candidatesListViewCJK && !this.isDelimiterPressedEver && isModeBPMF()) {
                getExactWord(this.mExactWord);
                if (this.candidatesListViewCJK != null && isValidExactKeyboardPhrase(this.mExactWord.toString())) {
                    this.candidatesListViewCJK.setLeftArrowStatus(false);
                    this.candidatesListViewCJK.setExactKeyboardShowable(false);
                    this.mChnPinyinAndPhraseListViewContainer.showLeftArrow(false);
                    updateListViews(false, true);
                    return;
                }
            }
            if (aSource == this.candidatesListViewCJK) {
                if (this.mHandWritingOnKeyboardHandler == null || this.mHandWritingOnKeyboardHandler.isWritingRecognitionDone()) {
                    if (this.mHandWritingOnKeyboardHandler != null && this.mHandWritingOnKeyboardHandler.recognitionCandidatesAvailable()) {
                        showGridCandidatesView(this.mHandWritingOnKeyboardHandler.getHWRecognitionCandidates());
                    } else {
                        List<CharSequence> candList = this.mChineseInput.getPopupList();
                        List<AtomicInteger> wdSourceList = this.mChineseInput.getPopupWordSourceList();
                        showGridCandidatesView(candList, wdSourceList);
                    }
                    if (((ChineseKeyboardSwitcher) this.mKeyboardSwitcher).isMixedInput()) {
                        updatePrefixes();
                    }
                    if (this.candidatesListViewCJK != null) {
                        this.candidatesListViewCJK.setExactKeyboardShowable(false);
                        this.candidatesListViewCJK.setLeftArrowStatus(false);
                    }
                }
            }
        }
    }

    @Override // com.nuance.swype.input.chinese.CJKCandidatesListView.OnWordSelectActionListener
    public boolean isScrollable(CJKCandidatesListView aSource) {
        if (!aSource.equals(this.spellPrefixSuffixListView) && aSource.equals(this.candidatesListViewCJK)) {
            return this.mIme.isHardKeyboardActive() && (!isShown() || this.mIsUseHardkey);
        }
        return true;
    }

    @Override // com.nuance.swype.input.InputView
    public void onApplicationUnbind() {
        dimissRemoveUdbWordDialog();
        super.onApplicationUnbind();
    }

    @Override // com.nuance.swype.input.InputView
    public void setKeyboardLayer(KeyboardEx.KeyboardLayerType keyboardLayer) {
        if (getKeyboardLayer() != keyboardLayer) {
            super.setKeyboardLayer(keyboardLayer);
            if (keyboardLayer == KeyboardEx.KeyboardLayerType.KEYBOARD_NUM || keyboardLayer == KeyboardEx.KeyboardLayerType.KEYBOARD_SYMBOLS) {
                if (!this.candidatesListViewCJK.getAltCharacterConverted() || (this.numZeroPlaceholderIndexList.isEmpty() && this.delimiterPlaceholderIndexList.isEmpty())) {
                    if (isAltCharacterToggled()) {
                        this.candidatesListViewCJK.setAltCharacterConverted(false);
                        List<CharSequence> prefixList = this.mChineseInput.getPrefixes(this.mDefaultPrefixIndex);
                        if (prefixList.size() > 0) {
                            showPinyinSelectionList(prefixList, this.mDefaultPrefixIndex.get(), true);
                        }
                        updateListViews(true, true);
                        if (this.isCloudPredictionAllowed && this.mChineseInput != null) {
                            byte[] requestCldInputData = this.mChineseInput.predictionCloudGetData();
                            if (this.requestCloudDataCompare == null || requestCldInputData == null || this.requestCloudDataCompare.length != requestCldInputData.length) {
                                startChinesePrediction();
                                return;
                            }
                            return;
                        }
                        return;
                    }
                    if (this.isPressedNumOneKey || !this.mKeyboardSwitcher.isKeypadInput() || !isModePinyin() || !hasActiveKeySeq() || !hasDigitTone()) {
                        if (keyboardLayer == KeyboardEx.KeyboardLayerType.KEYBOARD_SYMBOLS && !this.isCandidateSelectedEver && ((isQwertyLayout() && !hasTone() && !this.isTracedEver) || (this.mKeyboardSwitcher.isKeypadInput() && hasActiveKeySeq()))) {
                            if (isAlphabeticCharacter(this.mDefaultWord.length() > 0 ? this.mDefaultWord.charAt(0) : (char) 0) && isModePinyin()) {
                                this.lastKeypadBilingualState = false;
                                produceAltCharacterText();
                                if (this.isCloudPredictionAllowed && this.mChineseInput != null) {
                                    this.requestCloudDataCompare = this.mChineseInput.predictionCloudGetData();
                                }
                                this.candidatesListViewCJK.setAltCharacterConverted(true);
                                if (confirmDisplayConvertedCandidates()) {
                                    updateInlineAltCharacters();
                                    showPinyinSelectionList(this.chineseSymbolList, -1, false);
                                    syncCandidateDisplayStyleToMode();
                                    return;
                                }
                                this.candidatesListViewCJK.setAltCharacterConverted(false);
                            }
                        }
                    } else {
                        return;
                    }
                } else {
                    return;
                }
            }
            this.mEditState.startSession();
            dismissPopupKeyboard();
            flushCurrentActiveWord();
            if (keyboardLayer == KeyboardEx.KeyboardLayerType.KEYBOARD_SYMBOLS) {
                if (this.symInputController == null) {
                    this.symInputController = new SymbolInputController();
                }
                this.symInputController.setContext(IMEApplication.from(this.mIme).getThemedContext());
                this.symInputController.show();
                return;
            }
            clearSuggestions();
            this.mKeyboardSwitcher.createKeyboardForTextInput(keyboardLayer, this.mInputFieldInfo, this.currentInputMode);
            if (!isNormalTextInputMode()) {
                showFunctionBarList();
                this.mFunctionBarListView.showFunctionBar();
            }
            if ((this.mKeyboardSwitcher.isKeypadInput() || isModeStroke()) && isNormalTextInputMode()) {
                showPinyinSelectionList(this.chineseSymbolList, -1, false);
            } else {
                this.mOneStepPYContainer.hideSymbolList();
            }
            setShiftState(Shift.ShiftState.OFF);
            if (this.mKeyboardLayoutId == 1536 || isModeStroke()) {
                setPressDownPreviewEnabled(false);
            }
            startChinesePrediction();
        }
    }

    private boolean hasDigitTone() {
        if (isAltCharacterToggled()) {
            return false;
        }
        for (int i = 0; i < this.mDefaultWord.length(); i++) {
            if (this.mDefaultWord.charAt(i) >= '1' && this.mDefaultWord.charAt(i) <= '5') {
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.InputView, com.nuance.swype.input.KeyboardViewEx
    public boolean handleScrollDown() {
        if (this.mIme == null) {
            return false;
        }
        this.mEditState.end();
        this.mChnPinyinAndPhraseListViewContainer.clear();
        this.mIme.requestHideSelf(0);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.KeyboardViewEx
    public boolean handleScrollLeft() {
        if (IMEApplication.from(this.mIme).getIMEHandlerManager() == null || !isNormalTextInputMode()) {
            return false;
        }
        setDefaultWordType(StatisticsEnabledEditState.DefaultSelectionType.MULTITAP_TOGGLE);
        this.mEditState.end();
        this.mIme.handlerManager.toggleKeyboard();
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.KeyboardViewEx
    public boolean handleScrollRight() {
        if (IMEApplication.from(this.mIme).getIMEHandlerManager() == null || !isNormalTextInputMode()) {
            return false;
        }
        setDefaultWordType(StatisticsEnabledEditState.DefaultSelectionType.MULTITAP_TOGGLE);
        this.mEditState.end();
        this.mIme.handlerManager.toggleKeyboard();
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.KeyboardViewEx
    public boolean isSupportMultitouchGesture() {
        return true;
    }

    @Override // com.nuance.swype.input.InputView
    public boolean isLanguageSwitchableOnSpace() {
        return !this.mKeyboardSwitcher.isSymbolMode();
    }

    @Override // com.nuance.swype.input.InputView, com.nuance.swype.input.RemoveUdbWordDialog.RemoveUdbWordListener
    @SuppressLint({"PrivateResource"})
    public void onHandleUdbWordRemoval(String word) {
        if (this.mChineseInput != null) {
            if (this.mChineseInput.dlmDelete(word)) {
                String removeMgs = "\"" + word + "\" " + getContext().getApplicationContext().getResources().getString(R.string.delete_success);
                QuickToast.show(getContext(), removeMgs, 0, getHeight() + this.candidatesListViewCJK.getHeight());
                clearInlineWord();
                showFunctionBarList();
                this.lastKeypadBilingualState = false;
                showPinyinSelectionList(this.chineseSymbolList, -1, false);
                flushCurrentActiveWord();
                updateListViews(true, true);
                return;
            }
            String removeMgs2 = "\"" + word + "\" " + getContext().getApplicationContext().getResources().getString(R.string.delete_failed);
            QuickToast.show(getContext(), removeMgs2, 0, getHeight() + this.candidatesListViewCJK.getHeight());
        }
    }

    @Override // com.nuance.swype.input.InputView
    public void showRemoveUdbWordDialog(String word, int wordSource) {
        if ((wordSource != 5 && wordSource != 14 && wordSource != 6 && wordSource != 9) || hasActiveKeySeq()) {
            super.showRemoveUdbWordDialog(word);
        }
    }

    @Override // com.nuance.swype.input.chinese.CJKCandidatesListView.OnWordSelectActionListener
    public void onCandidateLongPressed(int index, String word, int wdSource, CJKCandidatesListView aSource) {
        switch (wdSource) {
            case 4:
            case 6:
                ContactUtils.getContactNumberFromPhoneBook(getContext(), word, 6);
                return;
            case 5:
            case 14:
                showRemoveUdbWordDialog(word, wdSource);
                return;
            case 7:
                if (word != null && EmojiLoader.isEmoji(word.toString())) {
                    EmojiInfo emojiInfo = aSource.getEmojiInfoList().get(index);
                    String selected_emoji = this.candidatesListViewCJK.getEmojis().get(word.toString());
                    super.showSkinTonePopupForCJK(emojiInfo, selected_emoji, word.toString());
                    return;
                }
                return;
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            default:
                return;
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
        logSelectedWordForChinesePrediction(index, word.toString());
        cancelChinesePrediction();
        StringBuilder mInsertText = new StringBuilder(word);
        this.mChineseInput.selectWord(index, mInsertText);
        this.conversionHistory.add(word.toString());
        log.d("conversionHistory...index: ", Integer.valueOf(index), "...exact keyboard visible: ", Boolean.valueOf(this.candidatesListViewCJK.isExactKeyboardShowable()), "...added: ", word);
        if (!hasActiveKeySeq() && !this.mInitiativeAccept) {
            this.spellPrefixSuffixListView.clear();
        }
        this.mbNeedUpdate = false;
        updateListViews(true, true);
        if (this.spellPrefixSuffixListView != null) {
            this.spellPrefixSuffixListView.disableHighlight();
        }
        startChinesePrediction();
        this.mTerminateSession = true;
        this.isCandidateSelectedEver = true;
        return true;
    }

    @Override // com.nuance.swype.input.InputView, com.nuance.swype.input.CandidatesListView.CandidateListener
    public void onPressMoveCandidate(float xPos, float yPos, float xOffset) {
        log.d("onPressMoveCandidate()", " called ::  xPos:: " + xPos + ", yPos:: " + yPos + ", xOffset :: " + xOffset);
        super.touchMoveHandle(xPos, yPos, xOffset);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.InputView
    public boolean shouldShowTips() {
        return this.mKeyboardSwitcher.isAlphabetMode() && isTraceInputEnabled();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.InputView
    public void setSwypeKeytoolTipSuggestion() {
        if (!hasActiveKeySeq()) {
            setCandidatesViewShown(true);
            if (this.mChnPinyinAndPhraseListViewContainer != null) {
                this.mChnPinyinAndPhraseListViewContainer.clear();
                this.mChnPinyinAndPhraseListViewContainer.showLeftArrow(false);
                this.mChnPinyinAndPhraseListViewContainer.hideFunctionBarListView();
                this.mChnPinyinAndPhraseListViewContainer.hideSpellPrefixSuffixList();
                this.mChnPinyinAndPhraseListViewContainer.showPhraseListView();
            }
            this.candidatesListViewCJK.setLeftArrowStatus(false);
            syncCandidateDisplayStyleToMode();
            this.candidatesListViewCJK.showSwypeTooltip();
        }
    }

    @Override // com.nuance.swype.input.InputView
    public void handlePossibleActionAfterUniversalKeyboardResize(boolean hideSymbolView) {
        hideGridCandidatesView();
        this.candidatesPopup = null;
        if (hideSymbolView) {
            flushCurrentActiveWord();
            if (this.mShowFunctionBar) {
                showFunctionBarList();
            } else {
                clearSuggestions();
            }
            if (this.mKeyboardSwitcher.isKeypadInput() || isModeStroke()) {
                this.mOneStepPYContainer.hideSymbolList();
                if (isNormalTextInputMode()) {
                    showPinyinSelectionList(this.chineseSymbolList, -1, false);
                }
            }
        }
    }

    @Override // com.nuance.swype.input.InputView
    public void setNotMatchToolTipSuggestion() {
        setCandidatesViewShown(true);
        if (this.mChnPinyinAndPhraseListViewContainer != null) {
            this.mChnPinyinAndPhraseListViewContainer.clear();
            this.mChnPinyinAndPhraseListViewContainer.showLeftArrow(false);
            this.mChnPinyinAndPhraseListViewContainer.hideFunctionBarListView();
            this.mChnPinyinAndPhraseListViewContainer.hideSpellPrefixSuffixList();
            this.mChnPinyinAndPhraseListViewContainer.showPhraseListView();
        }
        this.candidatesListViewCJK.setLeftArrowStatus(false);
        syncCandidateDisplayStyleToMode();
        this.candidatesListViewCJK.showNotMatchTootip();
    }

    private void addFunctionBarItem() {
        UserPreferences userPrefs = UserPreferences.from(getContext());
        this.mLanguageOptionOn = userPrefs.getLanguageOption();
        this.mSettingsOn = userPrefs.getSettings();
        this.mInputModeOn = userPrefs.getInputMode();
        this.mQuickToggleOn = userPrefs.getQuickToggle();
        this.mEditKeyboardOn = userPrefs.getEditKeyboard();
        this.mNumberKeyboardOn = userPrefs.getNumberKeyboard();
        this.mThemesOn = userPrefs.getThemes();
        this.mAddOnDictionariesOn = userPrefs.getAddOnDictionaries();
        this.mChineseSettingsOn = userPrefs.getChineseSettings();
        this.mEmojiFunctionBarOn = userPrefs.getEmojiOnFunctionBar();
        if (this.mSettingsOn) {
            this.mFunctionBarListView.addToolBarItem(101);
        }
        if (this.mLanguageOptionOn) {
            this.mFunctionBarListView.addToolBarItem(103);
        }
        if (this.mInputModeOn) {
            this.mFunctionBarListView.addToolBarItem(104);
        }
        if (!IMEApplication.from(getContext()).isScreenLayoutTablet()) {
            if (this.mEditKeyboardOn) {
                this.mFunctionBarListView.addToolBarItem(110);
            }
            if (this.mNumberKeyboardOn) {
                this.mFunctionBarListView.addToolBarItem(109);
            }
        }
        if (this.mQuickToggleOn && UserPreferences.from(getContext()).isHandwritingEnabled()) {
            if (this.mHandwritingOn) {
                this.mFunctionBarListView.addToolBarItem(114);
            } else {
                this.mFunctionBarListView.addToolBarItem(117);
            }
        }
        if (this.mThemesOn) {
            this.mFunctionBarListView.addToolBarItem(111);
        }
        if (this.mEmojiFunctionBarOn) {
            this.mFunctionBarListView.addToolBarItem(118);
        }
        if (this.mChineseSettingsOn) {
            this.mFunctionBarListView.addToolBarItem(113);
        }
        if (this.mAddOnDictionariesOn) {
            this.mFunctionBarListView.addToolBarItem(112);
        }
        this.mFunctionBarListView.addToolBarItem(115);
    }

    private boolean isAllNumberOneRemoved() {
        return this.mKeyboardSwitcher != null && this.mKeyboardSwitcher.isKeypadInput() && this.isPressedNumOneKey && this.numZeroPlaceholderIndexList.isEmpty();
    }

    @SuppressLint({"PrivateResource"})
    private void produceAltCharacterText() {
        if (this.altCharacterText.length() <= 0 && this.mKeys != null && isModePinyin()) {
            if (this.mKeyboardSwitcher.isKeypadInput()) {
                this.altCharacterText.append(getContext().getApplicationContext().getString(R.string.mappings_keypad_pinyin).toCharArray());
                return;
            }
            SortedSet<KeyboardEx.Key> keySet = new TreeSet<>(new Comparator<KeyboardEx.Key>() { // from class: com.nuance.swype.input.chinese.ChineseInputView.7
                @Override // java.util.Comparator
                public int compare(KeyboardEx.Key key1, KeyboardEx.Key key2) {
                    if (key1.label != null) {
                        return key1.label.toString().compareTo(key2.label.toString());
                    }
                    return 0;
                }
            });
            for (KeyboardEx.Key key : this.mKeys) {
                if (key.label != null && isAlphabeticCharacter(key.label.charAt(0))) {
                    keySet.add(key);
                }
            }
            for (KeyboardEx.Key key2 : keySet) {
                if (key2.label.length() <= 1) {
                    CharSequence csAltLabel = key2.altLabel;
                    if (csAltLabel == null) {
                        this.altCharacterText.append(' ');
                    } else {
                        this.altCharacterText.append(csAltLabel);
                    }
                }
            }
            keySet.clear();
        }
    }

    private boolean isAlphabeticCharacter(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    private boolean convertToAltCharacters(StringBuilder s) {
        if (s == null || s.length() == 0 || !isModePinyin()) {
            return false;
        }
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (this.mKeyboardSwitcher.isKeypadInput() && isDilimiter(c)) {
                s.setCharAt(i, '1');
            } else {
                if ('A' <= c && c <= 'Z') {
                    s.setCharAt(i, this.altCharacterText.charAt(c - 'A'));
                } else if ('a' <= c && c <= 'z') {
                    s.setCharAt(i, this.altCharacterText.charAt(c - 'a'));
                }
                int j = 0;
                while (j < this.numZeroPlaceholderIndexList.size()) {
                    int index = this.numZeroPlaceholderIndexList.get(j).get();
                    if (index >= s.length()) {
                        this.numZeroPlaceholderIndexList.remove(j);
                        j--;
                    } else if (s.charAt(index) == '9') {
                        s.setCharAt(i, '0');
                    }
                    j++;
                }
                int k = 0;
                while (k < this.delimiterPlaceholderIndexList.size()) {
                    int index2 = this.delimiterPlaceholderIndexList.get(k).get();
                    if (index2 >= s.length()) {
                        this.delimiterPlaceholderIndexList.remove(k);
                        k--;
                    } else if (s.charAt(index2) == '2') {
                        s.setCharAt(i, '1');
                    }
                    k++;
                }
            }
        }
        return true;
    }

    @Override // com.nuance.swype.input.InputView
    public void showFunctionBar() {
        if (this.mShowFunctionBar && this.candidatesListViewCJK != null && this.candidatesListViewCJK.isCandidateListEmpty() && !hasActiveKeySeq()) {
            showFunctionBarList();
        }
    }

    @Override // com.nuance.swype.input.InputView
    public void hideFunctionBar() {
        if (this.mShowFunctionBar && this.candidatesListViewCJK != null) {
            hideFunctionBarList();
        }
    }

    private boolean showConvertedCandidates() {
        if (this.mChineseInput == null || !isModePinyin()) {
            return false;
        }
        this.exactInputText.setLength(0);
        if (this.mKeyboardSwitcher.isKeypadInput()) {
            this.mChineseInput.getExactInputText(this.exactInputText);
        } else {
            this.mChineseInput.getExactWord(this.mExactWord);
            if (this.mExactWord.toString().trim().length() <= 0) {
                return false;
            }
            this.exactInputText.append(this.mExactWord);
        }
        boolean success = convertToAltCharacters(this.exactInputText);
        if (success) {
            this.convertedCharacterList.clear();
            this.convertedCharacterList.add(this.exactInputText);
            if (this.candidatesListViewCJK != null) {
                this.candidatesListViewCJK.setSuggestions(this.convertedCharacterList, 0);
                syncCandidateDisplayStyleToMode();
                setCandidatesViewShown(true);
                return success;
            }
            return success;
        }
        return success;
    }

    private void updateInlineAltCharacters() {
        InputConnection ic = getCurrentInputConnection();
        if (ic != null) {
            this.mDefaultWord.clear();
            this.mDefaultWord.append((CharSequence) this.exactInputText);
            if (this.mDefaultWord.length() > 0) {
                this.mDefaultWord.setSpan(this.mUnderline, 0, this.mDefaultWord.length(), R.styleable.ThemeTemplate_emojiCategoryIconSmiles);
            }
            if (this.mIme.isFullscreenMode() || (this.exactInputText.length() > 0 && this.exactInputText.charAt(0) == '0')) {
                ic.setComposingText(this.mDefaultWord, 1);
            }
            showTopInline(false);
        }
    }

    private boolean flushInineAltCharacters() {
        InputContainerView containerView;
        if (!isAltCharacterToggled()) {
            return false;
        }
        if (this.mIme != null && (containerView = this.mIme.getInputContainerView()) != null) {
            containerView.hideWidgetView(this.chinesePredictionView);
            containerView.hideWidgetView(this.inlineContainerView);
        }
        this.candidatesListViewCJK.setAltCharacterConverted(false);
        flushInlineWord();
        this.touchKeyActionHandler.clearAllKeys();
        this.convertedCharacterList.clear();
        this.exactInputText.setLength(0);
        this.numZeroPlaceholderIndexList.clear();
        this.delimiterPlaceholderIndexList.clear();
        this.mTerminateSession = false;
        this.isCandidateSelectedEver = false;
        this.isTracedEver = false;
        this.isDelimiterPressedEver = false;
        this.mTone = false;
        updateDelimiterKeyLabel(false);
        updateClearKeyLabel(false);
        clearInternalStatus();
        showFunctionBarList();
        syncCandidateDisplayStyleToMode();
        return true;
    }

    private void showFunctionBarList() {
        if (isValidBuild() && this.candidatesListViewCJK != null && this.mChnPinyinAndPhraseListViewContainer != null) {
            showChinesePrediction(false);
            showTopInline(false);
            if (this.mIme != null && ((this.mHandWritingOnKeyboardHandler == null || !this.mHandWritingOnKeyboardHandler.recognitionCandidatesAvailable()) && this.mIme.isHardKeyboardActive())) {
                this.candidatesListViewCJK.clear();
                this.mChnPinyinAndPhraseListViewContainer.clear();
                this.mChnPinyinAndPhraseListViewContainer.hideFunctionBarListView();
                this.mChnPinyinAndPhraseListViewContainer.showPhraseListView();
                syncCandidateDisplayStyleToMode();
                return;
            }
            this.candidatesListViewCJK.clear();
            this.mChnPinyinAndPhraseListViewContainer.clear();
            this.mChnPinyinAndPhraseListViewContainer.hidePhraseListView();
            this.mChnPinyinAndPhraseListViewContainer.hideSpellPrefixSuffixList();
            this.mChnPinyinAndPhraseListViewContainer.showFunctionBarListView();
            syncCandidateDisplayStyleToMode();
        }
    }

    private void hideFunctionBarList() {
        if (this.mChnPinyinAndPhraseListViewContainer != null) {
            this.mChnPinyinAndPhraseListViewContainer.hideFunctionBarListView();
        }
    }

    public boolean confirmDisplayConvertedCandidates() {
        return ((!hasTone() && isQwertyLayout()) || this.mKeyboardSwitcher.isKeypadInput()) && this.mChineseInput != null && hasActiveKeySeq() && this.candidatesListViewCJK != null && (this.mHandWritingOnKeyboardHandler == null || !this.mHandWritingOnKeyboardHandler.recognitionCandidatesAvailable()) && isModePinyin() && this.mChineseInput.getActivePrefixIndex() != 254 && showConvertedCandidates();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void activateCategoryDatabase() {
        AppPreferences appPrefs = AppPreferences.from(getContext());
        CategoryDBList cdbList = new CategoryDBList(getContext(), true);
        List<String> cdbNames = cdbList.getAvailableCDBs(this.mCurrentInputLanguage.mEnglishName);
        if (cdbNames != null) {
            for (String st : cdbNames) {
                String name = cdbList.getFileName(st);
                int id = cdbList.getFileId(st);
                if (appPrefs.getBoolean(name, false)) {
                    this.mChineseInput.setCategoryDb(this.mCurrentInputLanguage.getCoreLanguageId(), id, true);
                }
            }
        }
    }

    private void deactivateCategoryDatabase() {
        CategoryDBList cdbList = new CategoryDBList(getContext(), true);
        List<String> cdbNames = cdbList.getAvailableCDBs(this.mCurrentInputLanguage.mEnglishName);
        if (cdbNames != null) {
            for (String st : cdbNames) {
                int id = cdbList.getFileId(st);
                this.mChineseInput.setCategoryDb(this.mCurrentInputLanguage.getCoreLanguageId(), id, false);
            }
        }
    }

    @Override // com.nuance.swype.input.KeyboardViewEx
    public int getHighlightedKeyArea(KeyboardEx.Key key) {
        if (key.label == null || !isModeDoublePinyin()) {
            return HighLightKeyArea.NONE.getValue();
        }
        String[] vowels = getHighlightVowels(this.doublePinyinTailKeyUnicode.intValue());
        String label = key.label.toString();
        String altLabel = null;
        if (key.altLabel != null) {
            altLabel = key.altLabel.toString();
        }
        String leftAltLabel = null;
        if (key.leftAltLabel != null) {
            leftAltLabel = key.leftAltLabel.toString();
        }
        if (vowels == null) {
            if (isZhChSh(label)) {
                return HighLightKeyArea.CONSONANT_LABLE.getValue();
            }
            if (altLabel != null && isZhChSh(altLabel)) {
                return HighLightKeyArea.CONSONANT_ALTLABLE.getValue();
            }
            if (leftAltLabel != null && isZhChSh(leftAltLabel)) {
                return HighLightKeyArea.CONSONANT_LEFTALTLABLE.getValue();
            }
            return HighLightKeyArea.NONE.getValue();
        }
        log.d("isToHighlightKey... key label: ", label);
        int ret = HighLightKeyArea.NONE.getValue();
        for (String vowel : vowels) {
            if (vowel.equals(label)) {
                ret |= HighLightKeyArea.VOWEL_LABLE.getValue();
            }
            if (altLabel != null && vowel.equals(altLabel)) {
                ret |= HighLightKeyArea.VOWEL_ALTLABLE.getValue();
            }
            if (leftAltLabel != null && vowel.equals(leftAltLabel)) {
                ret |= HighLightKeyArea.VOWEL_LEFTALTLABLE.getValue();
            }
        }
        log.d("isToHighlightKey...ret: ", Integer.valueOf(ret));
        return ret;
    }

    @SuppressLint({"PrivateResource"})
    String[] getHighlightVowels(int consonant) {
        switch (consonant) {
            case ChineseDoublePinyinUtils.B_CONSONANTS /* 62001 */:
                return getResources().getStringArray(R.array.b_vowels);
            case ChineseDoublePinyinUtils.C_CONSONANTS /* 62002 */:
                return getResources().getStringArray(R.array.c_vowels);
            case ChineseDoublePinyinUtils.CH_CONSONANTS /* 62003 */:
                return getResources().getStringArray(R.array.ch_vowels);
            case ChineseDoublePinyinUtils.D_CONSONANTS /* 62004 */:
                return getResources().getStringArray(R.array.d_vowels);
            case 62005:
            case 62014:
            default:
                return null;
            case ChineseDoublePinyinUtils.F_CONSONANTS /* 62006 */:
                return getResources().getStringArray(R.array.f_vowels);
            case ChineseDoublePinyinUtils.G_CONSONANTS /* 62007 */:
                return getResources().getStringArray(R.array.g_vowels);
            case ChineseDoublePinyinUtils.H_CONSONANTS /* 62008 */:
                return getResources().getStringArray(R.array.h_vowels);
            case ChineseDoublePinyinUtils.J_CONSONANTS /* 62009 */:
                return getResources().getStringArray(R.array.j_vowels);
            case ChineseDoublePinyinUtils.K_CONSONANTS /* 62010 */:
                return getResources().getStringArray(R.array.k_vowels);
            case ChineseDoublePinyinUtils.L_CONSONANTS /* 62011 */:
                return getResources().getStringArray(R.array.l_vowels);
            case ChineseDoublePinyinUtils.M_CONSONANTS /* 62012 */:
                return getResources().getStringArray(R.array.m_vowels);
            case ChineseDoublePinyinUtils.N_CONSONANTS /* 62013 */:
                return getResources().getStringArray(R.array.n_vowels);
            case ChineseDoublePinyinUtils.P_CONSONANTS /* 62015 */:
                return getResources().getStringArray(R.array.p_vowels);
            case ChineseDoublePinyinUtils.Q_CONSONANTS /* 62016 */:
                return getResources().getStringArray(R.array.q_vowels);
            case ChineseDoublePinyinUtils.R_CONSONANTS /* 62017 */:
                return getResources().getStringArray(R.array.r_vowels);
            case ChineseDoublePinyinUtils.S_CONSONANTS /* 62018 */:
                return getResources().getStringArray(R.array.s_vowels);
            case ChineseDoublePinyinUtils.SH_CONSONANTS /* 62019 */:
                return getResources().getStringArray(R.array.sh_vowels);
            case ChineseDoublePinyinUtils.T_CONSONANTS /* 62020 */:
                return getResources().getStringArray(R.array.t_vowels);
            case ChineseDoublePinyinUtils.W_CONSONANTS /* 62021 */:
                return getResources().getStringArray(R.array.w_vowels);
            case ChineseDoublePinyinUtils.X_CONSONANTS /* 62022 */:
                return getResources().getStringArray(R.array.x_vowels);
            case ChineseDoublePinyinUtils.Y_CONSONANTS /* 62023 */:
                return getResources().getStringArray(R.array.y_vowels);
            case ChineseDoublePinyinUtils.Z_CONSONANTS /* 62024 */:
                return getResources().getStringArray(R.array.z_vowels);
            case ChineseDoublePinyinUtils.ZH_CONSONANTS /* 62025 */:
                return getResources().getStringArray(R.array.zh_vowels);
            case ChineseDoublePinyinUtils.STAR_CONSONANTS /* 62026 */:
                return getResources().getStringArray(R.array.star_vowels);
        }
    }

    boolean isZhChSh(String str) {
        if (!"Zh".equals(str) && !"Ch".equals(str) && !"Sh".equals(str)) {
            return false;
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    @SuppressLint({"PrivateResource"})
    public void switchHandWritingOrTraceOnKeyboard() {
        AppPreferences appPrefs = AppPreferences.from(this.mIme);
        if (this.mHandwritingOn) {
            if (this.mHandWritingOnKeyboardHandler != null) {
                this.mHandWritingOnKeyboardHandler.onDestroy();
            }
            appPrefs.setBoolean(this.currentInputMode.getHandwritingOnKeyboardKey(), false);
            appPrefs.setBoolean(this.currentInputMode.getTraceOnKeyboardKey(), true);
            QuickToast.show(getContext(), getContext().getApplicationContext().getResources().getString(R.string.functionbar_hwtoggle_toast_off), 0, getHeight() + this.candidatesListViewCJK.getHeight());
        } else {
            appPrefs.setBoolean(this.currentInputMode.getHandwritingOnKeyboardKey(), true);
            appPrefs.setBoolean(this.currentInputMode.getTraceOnKeyboardKey(), false);
            QuickToast.show(getContext(), getContext().getApplicationContext().getResources().getString(R.string.functionbar_hwtoggle_toast_on), 0, getHeight() + this.candidatesListViewCJK.getHeight());
        }
        this.mIme.keyboardInputInflater.getInputView(this.mIme.mCurrentInputViewName).finishInput();
        this.mIme.keyboardInputInflater.getInputView(this.mIme.mCurrentInputViewName).startInput(this.mIme.mInputFieldInfo, false);
    }

    private void getExactWord(StringBuffer exactWord) {
        exactWord.setLength(0);
        if (this.mKeyboardSwitcher != null && !this.mKeyboardSwitcher.isKeypadInput()) {
            this.mChineseInput.getExactWord(exactWord);
        }
    }

    @Override // com.nuance.swype.input.KeyboardViewEx
    public boolean isDoublePinyinMode() {
        return isModeDoublePinyin();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.KeyboardViewEx
    public void detectAndSendKeyWrapper(int x, int y, long eventTime) {
        if (this.mbNeedDefer || this.mHandler.hasMessages(1008)) {
            this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(1008, x, y, Long.valueOf(eventTime + 100)), 100L);
        } else {
            detectAndSendKey(x, y, eventTime);
        }
    }

    @Override // com.nuance.swype.input.InputView
    public boolean shouldDisableInput(int primaryCode) {
        return false;
    }

    @Override // com.nuance.swype.input.InputView
    @SuppressLint({"PrivateResource"})
    public void moveCursorToMiddle(InputConnection ic, String text) {
        char[] mapping = getContext().getString(R.string.mappings_symbol).toCharArray();
        StringBuilder map = new StringBuilder();
        for (char c : mapping) {
            map.append(c);
        }
        for (int i = 0; i < text.length(); i++) {
            if (map.toString().indexOf(text.charAt(i)) != -1) {
                if (getAppSpecificBehavior() != null && getAppSpecificBehavior().shouldRestrictGetTextLengthFromCursor()) {
                    if (ic != null && ic.getTextBeforeCursor(10, 0) != null) {
                        this.mIme.sendDownUpKeyEvents(21);
                        return;
                    }
                    return;
                }
                CharSequence s = ic.getTextBeforeCursor(2000, 0);
                if (s != null && s.length() > 0) {
                    ic.setSelection(s.length() - 1, s.length() - 1);
                    return;
                }
                return;
            }
        }
    }

    @Override // com.nuance.swype.input.InputView
    public boolean handleHardKeyCapsLock(boolean iscapslockon) {
        handleShiftKey();
        return true;
    }

    @Override // com.nuance.swype.input.InputView
    public boolean handleHardkeyDelete(int repeatedCount) {
        if (hasActiveKeySeq() && this.mChineseInput != null && this.wordListViewContainerCJK != null && this.wordListViewContainerCJK.isShown() && this.candidatesListViewCJK.mSuggestions.size() > 0 && !onHardKeySelectionForHandWriting()) {
            selectWord(this.mDefaultWordIndex.get(), null, this.candidatesListViewCJK);
        }
        flushCurrentActiveWord();
        InputConnection ic = getCurrentInputConnection();
        if (this.mChineseInput != null && ic != null && !TextUtils.isEmpty(ic.getTextAfterCursor(1, 0))) {
            ic.beginBatchEdit();
            ic.deleteSurroundingText(0, 1);
            ic.endBatchEdit();
        }
        return true;
    }

    @Override // com.nuance.swype.input.InputView
    public boolean handleHardKeyDirectionKey(int keycode) {
        if (this.mChineseInput == null || this.mChnPinyinAndPhraseListViewContainer == null || this.candidatesListViewCJK == null || this.candidatesListViewCJK.mSuggestions.size() == 0 || !this.candidatesListViewCJK.isShown()) {
            if (hasActiveKeySeq()) {
                InputConnection ic = getCurrentInputConnection();
                if (ic != null) {
                    this.mChineseInput.clearAllKeys();
                    ic.commitText("", 0);
                    updateListViews(false, true);
                    this.candidatesListViewCJK.clear();
                    setCandidatesViewShown(false);
                }
            } else if (keycode == 21 || keycode == 22 || keycode == 19 || keycode == 20) {
                return false;
            }
            sendKeyToApplication(keycode);
            return false;
        }
        switch (keycode) {
            case 19:
            case 20:
                handleHardKeySelectHighlightedCandidate();
                flushCurrentActiveWord();
                return false;
            case 21:
            case 22:
                moveHighlightToWordListNearCandidate(keycode);
                break;
        }
        return true;
    }

    @Override // com.nuance.swype.input.InputView
    public boolean handleHardKeyEndKey() {
        return moveHighlightToHeardOrTail(false);
    }

    @Override // com.nuance.swype.input.InputView
    public boolean handleHardKeyHomeKey() {
        return moveHighlightToHeardOrTail(true);
    }

    @Override // com.nuance.swype.input.InputView
    public boolean handleHardkeyShiftKey(Shift.ShiftState state) {
        handleShiftKey();
        return true;
    }

    @Override // com.nuance.swype.input.InputView
    public boolean handleHardkeyBackspace(int repeatedCount) {
        return handleBackspace(repeatedCount);
    }

    @Override // com.nuance.swype.input.InputView
    public boolean handleHardkeyEnter() {
        if (this.mChineseInput != null && this.wordListViewContainerCJK != null && this.wordListViewContainerCJK.isShown() && this.candidatesListViewCJK.mSuggestions.size() > 0) {
            if (!onHardKeySelectionForHandWriting()) {
                int index = this.mDefaultWordIndex.get();
                if (index == 0 && this.candidatesListViewCJK.isExactKeyboardShowable()) {
                    selectWord(index, this.candidatesListViewCJK.mSuggestions.get(0), this.candidatesListViewCJK);
                } else {
                    selectWord(index, null, this.candidatesListViewCJK);
                }
            }
        } else {
            if (this.mHandWritingOnKeyboardHandler != null && this.mHandWritingOnKeyboardHandler.acceptHWRRecognitionWord()) {
                return true;
            }
            sendKeyToApplication(66);
        }
        return false;
    }

    public void handleHardKeySelectHighlightedCandidate() {
        int index;
        if (this.mChineseInput != null && this.wordListViewContainerCJK != null && this.wordListViewContainerCJK.isShown() && this.candidatesListViewCJK.mSuggestions.size() > 0 && !onHardKeySelectionForHandWriting() && this.mKeyboardSwitcher != null && !this.mKeyboardSwitcher.isSymbolMode() && (index = this.mDefaultWordIndex.get()) <= this.candidatesListViewCJK.mSuggestions.size() - 1 && index >= 0) {
            selectWord(index, null, this.candidatesListViewCJK);
        }
    }

    @Override // com.nuance.swype.input.InputView
    public boolean handleHardkeySpace(boolean quickPressed, int repeatedCount) {
        InputConnection ic;
        if (this.mChineseInput != null && hasActiveKeySeq()) {
            if (!flushInineAltCharacters()) {
                if (isModeBPMF() && this.mKeyboardLayoutId == 2304 && this.mChineseInput.addToneForZhuyin(177)) {
                    updateListViews(true, true);
                    startChinesePrediction();
                } else {
                    setDefaultWordType(StatisticsEnabledEditState.DefaultSelectionType.TAPPED_SPACE);
                    if (this.mHandWritingOnKeyboardHandler != null && this.mHandWritingOnKeyboardHandler.recognitionCandidatesAvailable()) {
                        onHardKeySelectionForHandWriting();
                    } else if (isModeCangjie()) {
                        selectWord(this.mDefaultWordIndex.get(), null, this.candidatesListViewCJK);
                    } else if (this.mKeyboardSwitcher != null && !this.mKeyboardSwitcher.isSymbolMode()) {
                        selectWord(this.mDefaultWordIndex.get(), null, this.candidatesListViewCJK);
                    } else {
                        flashError();
                    }
                }
            }
        } else if (onHardKeySelectionForHandWriting()) {
            updateListViews(true, true);
        } else {
            boolean addSpace = true;
            if (quickPressed && repeatedCount < 2 && this.mAutoPunctuationOn && (ic = getCurrentInputConnection()) != null) {
                ic.beginBatchEdit();
                CharSequence cSeqBefore = ic.getTextBeforeCursor(2, 0);
                CharacterUtilities charUtil = IMEApplication.from(this.mIme).getCharacterUtilities();
                if (cSeqBefore != null && cSeqBefore.length() == 2 && CharacterUtilities.isWhiteSpace(cSeqBefore.charAt(1)) && !CharacterUtilities.isWhiteSpace(cSeqBefore.charAt(0)) && !charUtil.isPunctuationOrSymbol(cSeqBefore.charAt(0)) && !this.mInputFieldInfo.isPasswordField()) {
                    handleAutoPunct();
                    addSpace = false;
                }
                ic.endBatchEdit();
            }
            this.mEditState.spaceKey();
            if (addSpace) {
                sendKeyChar(' ');
            }
        }
        return true;
    }

    @Override // com.nuance.swype.input.InputView
    public boolean handleHardKeyEscapeKey() {
        InputConnection ic;
        if (hasActiveKeySeq()) {
            InputConnection ic2 = getCurrentInputConnection();
            if (ic2 == null) {
                return false;
            }
            this.mChineseInput.clearAllKeys();
            ic2.commitText("", 0);
            updateListViews(false, true);
            this.candidatesListViewCJK.clear();
            setCandidatesViewShown(false);
            sendKeyToApplication(111);
            return true;
        }
        if (this.wordListViewContainerCJK != null && this.wordListViewContainerCJK.isShown() && this.candidatesListViewCJK.mSuggestions.size() > 0) {
            this.candidatesListViewCJK.clear();
            setCandidatesViewShown(false);
            if (!isMultitapping()) {
                updateContext();
                if ((this.mKeyboardSwitcher instanceof ChineseKeyboardSwitcher) && this.mKeyboardSwitcher.isSymbolMode()) {
                    updateListViews(false, true);
                } else {
                    updateListViews(true, true);
                }
                if (this.mDefaultWord.length() == 0 && (ic = getCurrentInputConnection()) != null) {
                    ic.setComposingText("", 1);
                }
            }
            if (!this.mIsDelimiterKeyLabelUpdated || hasActiveKeySeq()) {
                return true;
            }
            updateDelimiterKeyLabel(false);
            return true;
        }
        sendKeyToApplication(111);
        setCandidatesViewShown(false);
        return false;
    }

    @Override // com.nuance.swype.input.InputView
    public void handleHardkeyCharKey(int primaryCode, int[] keyCodes, KeyEvent event, boolean shiftable) {
        if (primaryCode != 0) {
            if (keyCodes == null) {
                keyCodes = new int[]{primaryCode};
            }
            if (primaryCode == 122 && isModeCangjie()) {
                primaryCode = 65311;
            }
            long eventTime = event != null ? event.getEventTime() : 0L;
            super.handleCharKey(null, primaryCode, keyCodes, eventTime);
            if (this.mChineseInput.getKeyCount() < 64) {
                if ((primaryCode != 48 || ((!isModeCangjie() && !isModePinyin()) || !isHardkeyKeypadInput() || !isNormalTextInputMode())) && this.mHandWritingOnKeyboardHandler != null) {
                    this.mHandWritingOnKeyboardHandler.acceptHWRRecognitionWord();
                }
                if (isPinyinQwertyShiftedState()) {
                    char ch = (char) primaryCode;
                    if ('a' <= ch && ch <= 'z') {
                        ch = Character.toUpperCase(ch);
                    }
                    if (hasActiveKeySeq()) {
                        if ('A' <= ch && ch <= 'Z') {
                            this.mChineseInput.setShiftState(Shift.ShiftState.ON);
                            this.mChineseInput.processUpperLetterPress(ch);
                            if (this.mChineseInput.getKeyCount() >= 64) {
                                this.mChineseInput.setShiftState(Shift.ShiftState.OFF);
                                return;
                            } else {
                                updateListViews(true, true);
                                this.mChineseInput.setShiftState(Shift.ShiftState.OFF);
                                return;
                            }
                        }
                        selectDefault();
                    }
                    sendKeyChar(ch);
                    return;
                }
                if (this.mChineseInput == null) {
                    this.mEditState.end();
                    sendKeyChar((char) primaryCode);
                } else if (isDilimiter(primaryCode)) {
                    if (isQwertyLayout() && flushInineAltCharacters()) {
                        sendKeyChar((char) primaryCode);
                        return;
                    }
                    if (isAltCharacterToggled()) {
                        if (this.mKeyboardSwitcher.isKeypadInput() && isModePinyin()) {
                            sendPlaceHolderKey(65, this.delimiterPlaceholderIndexList, eventTime, true);
                            return;
                        }
                        return;
                    }
                    if (hasActiveKeySeq()) {
                        if (!this.mKeyboardSwitcher.isSymbolMode()) {
                            boolean success = this.mChineseInput.cycleTone();
                            if (this.candidatesListViewCJK != null) {
                                this.candidatesListViewCJK.setLeftArrowStatus(false);
                            }
                            this.mTone = true;
                            if (success) {
                                updateListViews(true, true);
                                startChinesePrediction();
                            } else {
                                return;
                            }
                        } else {
                            return;
                        }
                    } else {
                        sendKeyChar((char) primaryCode);
                    }
                } else if (isAddingZhuyinTone(primaryCode)) {
                    if (this.mChineseInput.addToneForZhuyin(primaryCode)) {
                        setCandidatesViewShown(true);
                        updateListViews(true, true);
                        startChinesePrediction();
                    } else {
                        flashRedError();
                    }
                } else if (primaryCode == 10) {
                    if (hasActiveKeySeq()) {
                        flushCurrentActiveWord();
                    }
                    sendKeyChar((char) primaryCode);
                } else {
                    CharacterUtilities charUtil = IMEApplication.from(this.mIme).getCharacterUtilities();
                    if (isPopupKeyboardShowing() && isDigit(primaryCode)) {
                        handleSeparator(primaryCode);
                    } else if (primaryCode != 48 || !isModePinyin() || !isHardkeyKeypadInput() || !isNormalTextInputMode()) {
                        if (isHardkeyKeypadInput() && isDigit(primaryCode) && isNormalTextInputMode() && !isNumberTypeInputField()) {
                            if (isDigit(primaryCode) && isPopupKeyboardShowing() && (isPhoneticKeypad() || isCangjieKeypad())) {
                                handleSeparator(primaryCode);
                            } else {
                                if (this.mDefaultWordIndex.get() > 0 || !hasActiveKeySeq()) {
                                    this.mDefaultWord.clear();
                                }
                                this.mDefaultWordIndex.set(-1);
                                this.mIsIMEActive = true;
                                setCandidatesViewShown(true);
                                handlePrediction(primaryCode, keyCodes, false);
                            }
                        } else if (this.mKeyboardLayoutId == 2304 && UserPreferences.from(this.mIme).getEnableChineseBilingual() && isPopupKeyboardShowing() && isDigit(primaryCode) && isNormalTextInputMode() && !isNumberTypeInputField() && isModePinyin()) {
                            if (hasActiveKeySeq()) {
                                this.mChineseInput.addExplicitKey(primaryCode, Shift.ShiftState.ON);
                                updateListViews(true, true);
                            } else {
                                handleSeparator(primaryCode);
                            }
                        } else if ((charUtil.isPunctuationOrSymbol(primaryCode) && (!isModeCangjie() || this.mKeyboardLayoutId != 2304 || !isCangjieWildCard(primaryCode))) || isDigit(primaryCode) || !this.mKeyboardSwitcher.isAlphabetMode()) {
                            handleSeparator(primaryCode);
                        } else if (!isNumberTypeInputField() || !this.mKeyboardSwitcher.isAlphabetMode()) {
                            if (isPopupKeyboardShowing() && isModePinyin()) {
                                if (UserPreferences.from(this.mIme).getEnableChineseBilingual() && isAlphabeticCharacter((char) primaryCode) && isQwertyLayout() && hasActiveKeySeq()) {
                                    if (65 <= primaryCode && primaryCode <= 90) {
                                        this.mChineseInput.setShiftState(Shift.ShiftState.ON);
                                        this.mChineseInput.processUpperLetterPress(primaryCode);
                                        if (this.mChineseInput.getKeyCount() >= 64) {
                                            this.mChineseInput.setShiftState(Shift.ShiftState.OFF);
                                            return;
                                        } else {
                                            updateListViews(true, true);
                                            this.mChineseInput.setShiftState(Shift.ShiftState.OFF);
                                        }
                                    } else if (97 <= primaryCode && primaryCode <= 122) {
                                        selectDefault();
                                        sendKeyChar((char) primaryCode);
                                    }
                                } else {
                                    selectDefault();
                                    sendKeyChar((char) primaryCode);
                                }
                            } else {
                                if (this.mDefaultWordIndex.get() > 0 || !hasActiveKeySeq()) {
                                    this.mDefaultWord.clear();
                                }
                                this.mDefaultWordIndex.set(-1);
                                this.mIsIMEActive = true;
                                setCandidatesViewShown(true);
                                handlePrediction(primaryCode, keyCodes, false);
                            }
                        }
                    }
                }
                recordUsedTimeTapDisplaySelectionList();
            }
        }
    }

    private boolean onHardKeySelectionForHandWriting() {
        if (this.mHandWritingOnKeyboardHandler == null || !this.mHandWritingOnKeyboardHandler.recognitionCandidatesAvailable()) {
            return false;
        }
        int index = this.mDefaultWordIndex.get();
        if (index <= this.mHandWritingOnKeyboardHandler.getHWRecognitionCandidates().size() - 1 && index >= 0) {
            selectWord(index, this.mHandWritingOnKeyboardHandler.getHWRecognitionCandidates().get(index), this.candidatesListViewCJK);
        }
        return true;
    }

    private void moveHighlightToWordListNearCandidate(int keyCode) {
        if (this.candidatesListViewCJK.isShown()) {
            this.candidatesListViewCJK.setScrollContainer(true);
            this.candidatesListViewCJK.enableHighlight();
            int wordCandidatesCount = this.candidatesListViewCJK.mSuggestions.size();
            Boolean isRight = null;
            if (keyCode == 22) {
                isRight = true;
            } else if (keyCode == 21) {
                isRight = false;
            }
            if (isRight == null || !isRight.booleanValue() || (this.mDefaultWordIndex != null && this.mDefaultWordIndex.get() < wordCandidatesCount - 1)) {
                if (isRight != null && !isRight.booleanValue() && this.mDefaultWordIndex.get() <= 0) {
                    if (this.candidatesListViewCJK.getLeftArrowStatus()) {
                        prevBtnPressed(this.candidatesListViewCJK);
                        return;
                    }
                    return;
                }
                if (isRight != null && isRight.booleanValue()) {
                    this.mDefaultWordIndex.addAndGet(1);
                } else if (isRight != null) {
                    this.mDefaultWordIndex.decrementAndGet();
                } else {
                    return;
                }
                this.mDefaultWord.clear();
                this.mDefaultWord.append(this.candidatesListViewCJK.mSuggestions.get(this.mDefaultWordIndex.get()));
                this.candidatesListViewCJK.touchWord(this.mDefaultWordIndex.get(), this.mDefaultWord);
                this.mChnPinyinAndPhraseListViewContainer.requestLayout();
                if (isRight.booleanValue() && this.candidatesListViewCJK.isKeyOutofRightBound(this.mDefaultWordIndex.get())) {
                    this.candidatesListViewCJK.updateScrollPosition(this.candidatesListViewCJK.mWordX[this.mDefaultWordIndex.get()] + this.candidatesListViewCJK.getLeftDistance());
                } else if (!isRight.booleanValue() && this.candidatesListViewCJK.isKeyOutofLeftBound(this.mDefaultWordIndex.get())) {
                    if (!this.mKeyboardSwitcher.isKeypadInput() || this.mDefaultWordIndex.get() > 0) {
                        if (this.mDefaultWordIndex.get() > 0 || (hasActiveKeySeq() && (isModePinyin() || isModeBPMF()))) {
                            this.candidatesListViewCJK.scrollPrev();
                        }
                    } else {
                        return;
                    }
                }
                this.candidatesListViewCJK.invalidate();
            }
        }
    }

    @SuppressLint({"WrongCall"})
    private boolean moveHighlightToHeardOrTail(boolean head) {
        if (this.mChnPinyinAndPhraseListViewContainer == null || this.candidatesListViewCJK == null || this.candidatesListViewCJK.mSuggestions.size() == 0) {
            if (head) {
                sendKeyToApplication(122);
            } else {
                sendKeyToApplication(123);
            }
            return false;
        }
        if (!head) {
            int wordCandidatesCount = 0;
            boolean istoomany = false;
            while (true) {
                if (wordCandidatesCount == this.candidatesListViewCJK.mSuggestions.size() - 1) {
                    break;
                }
                wordCandidatesCount = this.candidatesListViewCJK.mSuggestions.size() - 1;
                if (wordCandidatesCount - this.mDefaultWordIndex.get() >= 60) {
                    istoomany = true;
                    break;
                }
                addMoreSuggestions();
            }
            this.candidatesListViewCJK.onDraw(null);
            this.mChnPinyinAndPhraseListViewContainer.requestLayout();
            this.mChnPinyinAndPhraseListViewContainer.invalidate();
            if (this.mDefaultWordIndex.get() >= wordCandidatesCount) {
                return true;
            }
            this.mDefaultWordIndex.set(wordCandidatesCount);
            this.mDefaultWord.clear();
            CharSequence word = this.candidatesListViewCJK.mSuggestions.get(this.mDefaultWordIndex.get());
            if (word == null) {
                return true;
            }
            this.mDefaultWord.append(word);
            this.candidatesListViewCJK.touchWord(this.mDefaultWordIndex.get(), this.mDefaultWord);
            if (!istoomany) {
                this.candidatesListViewCJK.scrollEnd();
                return true;
            }
            this.candidatesListViewCJK.updateScrollPosition(this.candidatesListViewCJK.mWordX[this.mDefaultWordIndex.get()] + this.candidatesListViewCJK.getLeftDistance());
            return true;
        }
        if (this.mDefaultWordIndex.get() == 0) {
            return true;
        }
        this.candidatesListViewCJK.onDraw(null);
        this.mChnPinyinAndPhraseListViewContainer.requestLayout();
        this.mChnPinyinAndPhraseListViewContainer.invalidate();
        this.mDefaultWordIndex.set(0);
        this.mDefaultWord.clear();
        CharSequence word2 = this.candidatesListViewCJK.mSuggestions.get(this.mDefaultWordIndex.get());
        if (word2 == null) {
            return true;
        }
        this.mDefaultWord.append(word2);
        this.candidatesListViewCJK.touchWord(this.mDefaultWordIndex.get(), this.mDefaultWord);
        this.candidatesListViewCJK.scrollHead();
        return true;
    }

    @SuppressLint({"InflateParams", "PrivateResource"})
    public void closeGridCandidatesView() {
        hideGridCandidatesView();
        if (this.candidatesPopup == null) {
            this.candidatesPopup = LayoutInflater.from(getContext()).inflate(R.layout.candidates_popup, (ViewGroup) null);
            this.candidatesPopup.findViewById(R.id.closeButton).setOnClickListener(new View.OnClickListener() { // from class: com.nuance.swype.input.chinese.ChineseInputView.8
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    ChineseInputView.this.hideGridCandidatesView();
                    ChineseInputView.this.setCandidatesViewShown(true);
                    if (ChineseInputView.this.mChnPinyinAndPhraseListViewContainer != null) {
                        ChineseInputView.this.mChnPinyinAndPhraseListViewContainer.hideSpellPrefixSuffixList();
                        ChineseInputView.this.mChnPinyinAndPhraseListViewContainer.showPhraseListView();
                        if (ChineseInputView.this.mChineseInput.getActivePrefixIndex() == 254) {
                            ChineseInputView.this.mChineseInput.setActivePrefixIndex(0);
                        }
                    }
                    if (ChineseInputView.this.mKeyboardSwitcher.isKeypadInput() && (ChineseInputView.this.isModeBPMF() || ChineseInputView.this.isModePinyin())) {
                        ChineseInputView.this.updatePrefixes();
                    }
                    ChineseInputView.this.mTerminateSession = true;
                    ChineseInputView.this.updateSuggestions(Candidates.Source.INVALID);
                }
            });
            this.candidatesPopupScrollView = (ScrollView) this.candidatesPopup.findViewById(R.id.scroll_view);
            this.candidatesPopupKeyboardViewEx = (ChineseKeyboardViewEx) this.candidatesPopup.findViewById(R.id.keyboardViewEx);
        }
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(-2, -2);
        this.candidatesPopup.setLayoutParams(lp);
        int height = getHeight() + this.mChnPinyinAndPhraseListViewContainer.getHeight();
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(getKeyboard().getMinWidth(), Integers.STATUS_SUCCESS);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(height, Integers.STATUS_SUCCESS);
        this.candidatesPopup.measure(widthMeasureSpec, heightMeasureSpec);
        if (this.mChnPinyinAndPhraseListViewContainer != null) {
            this.mChnPinyinAndPhraseListViewContainer.hideSpellPrefixSuffixList();
            this.mChnPinyinAndPhraseListViewContainer.showPhraseListView();
            if (this.mChineseInput.getActivePrefixIndex() == 254) {
                this.mChineseInput.setActivePrefixIndex(0);
            }
        }
        if (this.mKeyboardSwitcher.isKeypadInput() && (isModeBPMF() || isModePinyin())) {
            updatePrefixes();
        }
        this.mTerminateSession = true;
        updateSuggestions(Candidates.Source.INVALID);
    }

    @Override // com.nuance.swype.input.chinese.CJKCandidatesListView.OnWordSelectActionListener
    public void onScrollContentChanged() {
        if (this.mChnPinyinAndPhraseListViewContainer != null) {
            this.mChnPinyinAndPhraseListViewContainer.updateScrollArrowVisibility();
        }
    }

    public void handleHardKeyResize() {
        if (this.mKeyboardSwitcher != null && this.mKeyboardSwitcher.isKeypadInput()) {
            this.mOneStepPYContainer.hideSymbolList();
            if (isNormalTextInputMode()) {
                showPinyinSelectionList(this.chineseSymbolList, -1, false);
            }
        }
    }

    private boolean isValidExactKeyboardPhrase(String exactWord) {
        String inlineWord;
        boolean valid = false;
        if (exactWord.trim().length() > 0) {
            String delimiter = Character.toString(this.mChineseInput.chineseDelimiter());
            if (!exactWord.contains(delimiter)) {
                valid = true;
            }
        }
        if (isModeBPMF() && (inlineWord = this.mDefaultWord.toString()) != null) {
            int i = 0;
            while (true) {
                if (i >= this.mChineseInput.getBPMFTones().length) {
                    break;
                }
                if (!inlineWord.contains(String.valueOf(this.mChineseInput.getBPMFTones()[i]))) {
                    i++;
                } else {
                    valid = false;
                    if (this.candidatesListViewCJK != null) {
                        this.candidatesListViewCJK.setExactKeyboardShowable(false);
                    }
                }
            }
        }
        return valid;
    }

    @Override // com.nuance.swype.input.KeyboardViewEx
    public void showHardKeyPopupKeyboard(int keyCode) {
        super.showHardKeyPopupKeyboard(keyCode);
        updateSuggestions(Candidates.Source.INVALID);
    }

    @Override // com.nuance.swype.input.InputView
    public boolean isInputSessionStarted() {
        return !this.inputViewHandler.hasMessages(15);
    }

    @SuppressLint({"PrivateResource"})
    private boolean startChinesePrediction() {
        if (!this.isCloudPredictionAllowed || !shouldStartChinesePrediction()) {
            return false;
        }
        if (this.mIme != null && this.mIme.getResources().getBoolean(R.bool.enable_china_connect_special) && !UserPreferences.from(this.mIme).getNetworkAgreement()) {
            return false;
        }
        cancelChinesePrediction();
        if (this.inputViewHandler.hasMessages(18)) {
            this.inputViewHandler.removeMessages(18);
        }
        this.inputViewHandler.sendMessageDelayed(this.inputViewHandler.obtainMessage(18), 200L);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void requestChinesePrediction() {
        if (this.mChineseInput != null) {
            byte[] requestCldInputData = this.mChineseInput.predictionCloudGetData();
            int characterSet = this.mChineseInput.predictionCloudGetCharacterset();
            if (this.mChineseCloudPrediction != null && this.mCurrentInputLanguage != null) {
                this.mChineseCloudPrediction.requestChinesePrediction(requestCldInputData, this.mCurrentInputLanguage.getCoreLanguageId(), characterSet);
                postUpdateChinesePredictionMessage(true, null, 0, 20L);
            }
        }
    }

    private void cancelChinesePrediction() {
        this.mChinesePredictionResult = null;
        this.mChineseCloudPrediction.cancelChinesePrediction();
        postUpdateChinesePredictionMessage(false, null, 0, 50L);
    }

    @Override // com.nuance.swype.input.chinese.ChineseCloudPrediction.ChinesePredictionListener
    public void showPredictionResult(boolean show, ACChinesePredictionService.ACChinesePredictionResult result) {
        this.mChinesePredictionResult = result;
        postUpdateChinesePredictionMessage(show, result, 0, 10L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void postUpdateChinesePredictionMessage(boolean show, ACChinesePredictionService.ACChinesePredictionResult result, int param, long delayMillis) {
        if (this.inputViewHandler != null) {
            this.inputViewHandler.removeMessages(19);
            this.inputViewHandler.sendMessageDelayed(this.inputViewHandler.obtainMessage(19, show ? 1 : 0, param, result), delayMillis);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    @SuppressLint({"InflateParams", "ClickableViewAccessibility"})
    public void updateChinesePredictionView(boolean show, ACChinesePredictionService.ACChinesePredictionResult result, int param) {
        InputContainerView containerView;
        if (this.mIme != null && Connect.from(this.mIme).isInitialized() && (containerView = this.mIme.getInputContainerView()) != null) {
            ChinesePredictionService.ChinesePrediction resultToShow = null;
            if (!show || !shouldStartChinesePrediction()) {
                containerView.hideWidgetView(this.chinesePredictionView);
                return;
            }
            if (result != null) {
                resultToShow = filterChinesePreditionResult(result.getPredictions());
            }
            if (this.chinesePredictionView == null) {
                IMEApplication app = IMEApplication.from(getContext());
                LayoutInflater inflater = app.getThemedLayoutInflater(LayoutInflater.from(getContext()));
                app.getThemeLoader().setLayoutInflaterFactory(inflater);
                this.chinesePredictionView = inflater.inflate(R.layout.chinese_prediction_view, (ViewGroup) null);
                app.getThemeLoader().applyTheme(this.chinesePredictionView);
                containerView.addWidgetView(this.chinesePredictionView);
                this.cloudScrollView = (CustomHorizontalScrollView) this.chinesePredictionView.findViewById(R.id.cloudPredictionscrollView);
                this.cloudScrollView.setScrollListener(this);
                this.cloudText = (TextView) this.cloudScrollView.findViewById(R.id.search_result);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((getKeyboard().getMinWidth() * 3) / 5, -1);
                this.cloudScrollView.setLayoutParams(params);
                this.cloudScrollView.setHorizontalScrollBarEnabled(false);
                this.cloudScrollView.setAlpha(0.8f);
                this.cloudScrollView.setOnTouchListener(new View.OnTouchListener() { // from class: com.nuance.swype.input.chinese.ChineseInputView.9
                    float lastDistance;
                    float lastX;
                    float xDistance;

                    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
                    /* JADX WARN: Failed to find 'out' block for switch in B:2:0x0008. Please report as an issue. */
                    @Override // android.view.View.OnTouchListener
                    @SuppressLint({"ClickableViewAccessibility"})
                    public boolean onTouch(View v, MotionEvent event) {
                        int status;
                        switch (event.getAction()) {
                            case 0:
                                this.xDistance = 0.0f;
                                this.lastX = event.getX();
                                ChineseInputView.this.cloudScrollView.setBackgroundDrawable(ChineseInputView.this.wclCjkChineseCloudBackgroundHighlight);
                                ChineseInputView.this.cloudRec = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
                                return false;
                            case 1:
                            case 3:
                                if (this.xDistance < 20.0f || ChineseInputView.this.isCloudCandidateFixed()) {
                                    if (!ChineseInputView.this.isCloudCandidateFixed() || ChineseInputView.this.cloudRec.contains(v.getLeft() + ((int) event.getX()), v.getTop() + ((int) event.getY()))) {
                                        if (ChineseInputView.this.currentPredictionResult != null) {
                                            ChineseInputView.this.mChineseInput.getInlineSelection(ChineseInputView.this.mSelection);
                                            synchronized (this) {
                                                status = ChineseInputView.this.mChineseInput.predictionCloudCommitPhrase(ChineseInputView.this.currentPredictionResult.getPhrase(), ChineseInputView.this.currentPredictionResult.getSpell(), ChineseInputView.this.currentPredictionResult.getFullSpell(), ChineseInputView.this.currentPredictionResult.getAttributes());
                                            }
                                            ChineseInputView.log.d("ChinesePrediction id:", ChineseInputView.this.currentPredictionResult.getPredictionId(), " word:", ChineseInputView.this.currentPredictionResult.getPhrase(), " status:", Integer.valueOf(status));
                                            ChineseInputView.this.selectPredictionWord();
                                            ChineseInputView.this.mbNeedUpdate = true;
                                            ChineseInputView.this.mChineseCloudPrediction.logSelectedWordForChinesePrediction(ChineseInputView.this.currentPredictionResult);
                                            if (ChineseInputView.this.mInputFieldInfo.isPasswordField()) {
                                                ChineseInputView.this.mChineseInput.dlmDelete(ChineseInputView.this.currentPredictionResult.getPhrase());
                                            }
                                            ChineseInputView.this.currentPredictionResult = null;
                                            ChineseInputView.this.postUpdateChinesePredictionMessage(false, null, 0, 10L);
                                        }
                                    } else {
                                        ChineseInputView.this.cloudScrollView.setBackgroundDrawable(ChineseInputView.this.wclCjkChineseCloudBackgroundImage);
                                        return false;
                                    }
                                }
                                ChineseInputView.this.cloudScrollView.setBackgroundDrawable(ChineseInputView.this.wclCjkChineseCloudBackgroundImage);
                                return false;
                            case 2:
                                float curX = event.getX();
                                this.xDistance += Math.abs(curX - this.lastX);
                                this.lastX = curX;
                                this.lastDistance = this.xDistance;
                                if (!ChineseInputView.this.cloudRec.contains(v.getLeft() + ((int) event.getX()), v.getTop() + ((int) event.getY()))) {
                                    ChineseInputView.this.cloudScrollView.setBackgroundDrawable(ChineseInputView.this.wclCjkChineseCloudBackgroundImage);
                                    this.lastDistance = 0.0f;
                                } else if (ChineseInputView.this.isCloudCandidateFixed()) {
                                    ChineseInputView.this.cloudScrollView.setBackgroundDrawable(ChineseInputView.this.wclCjkChineseCloudBackgroundHighlight);
                                } else if (((int) this.lastDistance) != ((int) this.xDistance) || ((int) this.lastDistance) == 0) {
                                    ChineseInputView.this.cloudScrollView.setBackgroundDrawable(ChineseInputView.this.wclCjkChineseCloudBackgroundHighlight);
                                } else {
                                    ChineseInputView.this.cloudScrollView.setBackgroundDrawable(ChineseInputView.this.wclCjkChineseCloudBackgroundImage);
                                }
                                return false;
                            default:
                                return false;
                        }
                    }
                });
                this.cloudImage = (ImageView) this.chinesePredictionView.findViewById(R.id.search_image);
            }
            this.currentPredictionResult = resultToShow;
            Rect keyboardRec = this.mIme.getInputContainerView().getVisibleWindowRect();
            Rect inuputRec = this.mIme.getInputContainerView().getInputWindowRect();
            if (keyboardRec != null && inuputRec != null) {
                Rect rect = new Rect();
                rect.bottom = keyboardRec.top;
                rect.top = rect.bottom;
                rect.right = inuputRec.right;
                containerView.showWidgetView(this.chinesePredictionView);
                if (result == null) {
                    this.cloudImage.setVisibility(0);
                    this.cloudScrollView.setBackgroundDrawable(null);
                    showCloudPredictionTextView(8);
                    this.chinesePredictionView.measure(0, 0);
                    rect.left = rect.right - this.cloudImage.getMeasuredWidth();
                } else {
                    this.cloudImage.setVisibility(8);
                    if (resultToShow != null) {
                        this.cloudScrollView.setBackgroundDrawable(this.wclCjkChineseCloudBackgroundImage);
                        showCloudPredictionTextView(0);
                        String resultPhrase = resultToShow.getPhrase();
                        if (resultPhrase == null || resultPhrase.isEmpty()) {
                            showChinesePrediction(false);
                            return;
                        }
                        this.cloudText.setText(resultPhrase);
                        this.cloudText.setTextSize(0, getCloudTextSize());
                        this.chinesePredictionView.measure(0, 0);
                        int textViewWidth = (getKeyboard().getMinWidth() * 5) / 10;
                        int contentWidth = this.cloudText.getMeasuredWidth() + this.cloudScrollView.getPaddingRight() + this.cloudScrollView.getPaddingLeft();
                        boolean isPredictionThinner = textViewWidth >= contentWidth;
                        int width = isPredictionThinner ? contentWidth : textViewWidth;
                        if (isPredictionThinner && !resultPhrase.isEmpty() && CharacterUtilities.isDigit(resultPhrase.charAt(resultPhrase.length() - 1))) {
                            width += 5;
                        }
                        rect.left = rect.right - width;
                    } else {
                        this.cloudScrollView.setBackgroundDrawable(null);
                        showCloudPredictionTextView(8);
                        showChinesePrediction(false);
                        return;
                    }
                }
                rect.top -= this.chinesePredictionView.getMeasuredHeight();
                if (result == null) {
                    ((AnimationDrawable) this.cloudImage.getDrawable()).start();
                } else if (param == 0 && !this.isCloudAnimationStarted) {
                    TranslateAnimation animation = new TranslateAnimation(1, 0.0f, 1, 0.0f, 1, 1.0f, 1, 0.0f);
                    animation.setDuration(500L);
                    this.chinesePredictionView.startAnimation(animation);
                    this.isCloudAnimationStarted = true;
                }
                containerView.moveWidgetView(this.chinesePredictionView, rect);
            }
        }
    }

    @SuppressLint({"PrivateResource"})
    private float getCloudTextSize() {
        UserPreferences sp = IMEApplication.from(getContext()).getUserPreferences();
        return getContext().getResources().getDimensionPixelSize(R.dimen.cloud_prediction_text_size) * ThemesPrefs.getCandidatesSize(sp, "Candidates_Size", 1.0f);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isCloudCandidateFixed() {
        int totalWidth = (getKeyboard().getMinWidth() * 5) / 10;
        return this.cloudText.getMeasuredWidth() <= totalWidth;
    }

    @Override // com.nuance.swype.widget.CustomHorizontalScrollView.ScrollListener
    public void onScrollChanged(int scrollX) {
        int totalWidth;
        int predictionWidth;
        if (this.cloudText != null && (predictionWidth = this.cloudText.getMeasuredWidth()) > (totalWidth = (getKeyboard().getMinWidth() * 5) / 10)) {
            log.d("scrolling X:", Integer.valueOf(scrollX), "cloudWidth:", Integer.valueOf(predictionWidth), " Total:", Integer.valueOf(totalWidth));
        }
    }

    private void showCloudPredictionTextView(int visibility) {
        this.cloudText.setVisibility(visibility);
    }

    @Override // com.nuance.swype.input.InputView
    public boolean hasWidgetViews() {
        InputContainerView containerView;
        if (this.mIme == null) {
            return false;
        }
        if ((this.chinesePredictionView == null && this.inlineContainerView == null) || (containerView = this.mIme.getInputContainerView()) == null) {
            return false;
        }
        return containerView.isActiveWidgetView(this.chinesePredictionView) || containerView.isActiveWidgetView(this.inlineContainerView);
    }

    private boolean allowChinesePrediction() {
        String cloudOpt = AppPreferences.from(getContext()).getChineseCloudNetworkOption();
        if (cloudOpt == null || cloudOpt.isEmpty() || cloudOpt.equals(AppPreferences.CHINESE_CLOUD_DISABLED) || !Connect.from(this.mIme).isDataUsageOptInAccepted()) {
            return false;
        }
        PlatformUtil util = IMEApplication.from(this.mIme).getPlatformUtil();
        if (cloudOpt.equals(AppPreferences.CHINESE_CLOUD_WIFI_ONLY) && util.isWifiEnabled()) {
            return true;
        }
        if (cloudOpt.equals(AppPreferences.CHINESE_CLOUD_ALL)) {
            return util.isWifiEnabled() || (UserPreferences.from(getContext()).connectUseCellularData() && util.isMobileEnabled());
        }
        return false;
    }

    @SuppressLint({"PrivateResource"})
    private ChinesePredictionService.ChinesePrediction filterChinesePreditionResult(ChinesePredictionService.ChinesePrediction[] predictions) {
        if (predictions == null || predictions.length == 0) {
            return null;
        }
        for (int i = 0; i < predictions.length; i++) {
            log.d("ChinesePrediction result:", Integer.valueOf(i), XMLResultsHandler.SEP_SPACE, predictions[i].getPhrase());
        }
        if (this.candidatesListViewCJK == null) {
            return null;
        }
        List<CharSequence> suggestions = new ArrayList<>(this.candidatesListViewCJK.mSuggestions);
        if (suggestions.size() == 0) {
            return predictions[0];
        }
        if (getContext().getResources().getBoolean(R.bool.always_show_chinese_prediction_result)) {
            return predictions[0];
        }
        if (Math.min(predictions.length, 1) <= 0) {
            return null;
        }
        ChinesePredictionService.ChinesePrediction prediction = predictions[0];
        for (int j = 0; j < suggestions.size(); j++) {
            if (suggestions.get(j).toString().equals(prediction.getPhrase())) {
                this.duplicatePredictionResult = predictions[0];
                return null;
            }
        }
        return prediction;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean shouldStartChinesePrediction() {
        if (!ConnectLegal.from(getContext()).isTosAccepted() || this.mChineseInput == null || this.candidatesListViewCJK == null || this.mChineseInput.isHasTraceInfo() || this.candidatesListViewCJK.mSuggestions == null || this.candidatesListViewCJK.mSuggestions.size() == 0 || this.candidatesListViewCJK.getAltCharacterConverted()) {
            return false;
        }
        if (!isModePinyin() && !isModeBPMF() && !isModeDoublePinyin()) {
            return false;
        }
        if ((this.mChineseInput.hasBilingualPrefix() && isModeNineKeyPinyin() && this.mChineseInput.getActivePrefixIndex() == 254) || getKeyboardLayer() != KeyboardEx.KeyboardLayerType.KEYBOARD_TEXT) {
            return false;
        }
        if ((this.mInputFieldInfo == null || !this.mInputFieldInfo.isPasswordField()) && hasActiveKeySeq()) {
            return this.mChineseInput.getKeyCount() > 6 || this.mChineseInput.getDelimiterSize() > 0;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void selectPredictionWord() {
        clearCurrentActiveWord();
        InputConnection ic = getCurrentInputConnection();
        if (ic != null) {
            ic.beginBatchEdit();
            ic.commitText(((Object) this.mSelection) + this.currentPredictionResult.getPhrase(), 1);
            ic.endBatchEdit();
            updateDelimiterKeyLabel(false);
        }
    }

    private void logSelectedWordForChinesePrediction(int index, String word) {
        if (word != null && this.mChineseCloudPrediction != null) {
            this.mChineseInput.getWordSpell(index, this.mPredictionSpell);
            if (this.candidatesListViewCJK != null && this.duplicatePredictionResult != null && word.equals(this.duplicatePredictionResult.getPhrase())) {
                this.mChineseCloudPrediction.logSelectedWordForChinesePrediction(this.duplicatePredictionResult);
            } else {
                this.mChineseCloudPrediction.logSelectedWordForChinesePrediction(word, this.mPredictionSpell.toString());
            }
        }
    }

    private void showChinesePrediction(boolean visible) {
        if (this.chinesePredictionView != null && this.chinesePredictionView.isShown()) {
            this.chinesePredictionView.setVisibility(visible ? 0 : 4);
        }
    }

    private void showTopInline(boolean visible) {
        if (this.inlineContainerView != null && this.inlineContainerView.isShown()) {
            this.inlineContainerView.setVisibility(visible ? 0 : 4);
        }
    }

    @Override // com.nuance.swype.input.InputView, com.nuance.swype.input.KeyboardViewEx, com.nuance.swype.input.view.InputLayout.DragListener
    public void onBeginDrag() {
        super.onBeginDrag();
        showChinesePrediction(false);
        showTopInline(false);
    }

    @Override // com.nuance.swype.input.KeyboardViewEx, com.nuance.swype.input.view.InputLayout.DragListener
    public void onDrag(int dx, int dy) {
        super.onDrag(dx, dy);
        showChinesePrediction(false);
        showTopInline(false);
    }

    @Override // com.nuance.swype.input.KeyboardViewEx, com.nuance.swype.input.view.InputLayout.DragListener
    public void onEndDrag() {
        if (this.chinesePredictionView != null && this.mChinesePredictionResult != null && !this.chinesePredictionView.isShown()) {
            postUpdateChinesePredictionMessage(true, this.mChinesePredictionResult, 2, 50L);
        }
        super.onEndDrag();
        postDelayUpdateInlineString();
    }

    @Override // com.nuance.swype.input.KeyboardViewEx, com.nuance.swype.input.view.InputLayout.DragListener
    public void onSnapToEdge(int dx, int dy) {
        if (this.chinesePredictionView != null && this.mChinesePredictionResult != null && !this.chinesePredictionView.isShown()) {
            postUpdateChinesePredictionMessage(true, this.mChinesePredictionResult, 2, 50L);
        }
        super.onSnapToEdge(dx, dy);
        postDelayUpdateInlineString();
    }

    /* JADX INFO: Access modifiers changed from: private */
    @SuppressLint({"InflateParams"})
    public void updatInlineTextView() {
        InputContainerView containerView;
        if (this.mIme != null && (containerView = this.mIme.getInputContainerView()) != null) {
            log.d("#keyboard width:", Integer.valueOf(getKeyboard().getDisplayWidth()));
            int inlineWidth = (getKeyboard().getDisplayWidth() * 4) / 10;
            if (this.inlineContainerView == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                this.inlineContainerView = IMEApplication.from(getContext()).getThemedLayoutInflater(inflater).inflate(R.layout.inline_popup_view, (ViewGroup) null);
                containerView.addWidgetView(this.inlineContainerView);
                this.topInlineView = (TextView) this.inlineContainerView.findViewById(R.id.inline_text);
                this.topInlineView.setAlpha(0.8f);
                this.inlineScrollView = (RightAlignedHorizontalScrollView) this.inlineContainerView.findViewById(R.id.ontopinlinescrollView);
                this.inlineScrollView.setLayoutParams(new FrameLayout.LayoutParams(inlineWidth, -1));
                this.inlineScrollView.setHorizontalScrollBarEnabled(false);
            }
            scrollInlineWhenSelect(this.mSelectWord, this.mDefaultWord.toString());
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) this.inlineScrollView.getLayoutParams();
            if (params.width != inlineWidth) {
                params.width = inlineWidth;
                this.inlineScrollView.setLayoutParams(params);
            }
            if (this.topInlineView != null) {
                if (this.mDefaultWord.toString().length() == 0 || !hasActiveKeySeq()) {
                    if (this.inlineContainerView != null) {
                        containerView.hideWidgetView(this.inlineContainerView);
                        return;
                    }
                    return;
                }
                this.topInlineView.setText(this.mDefaultWord.toString());
                log.d("InlineText " + this.mDefaultWord.toString());
                Rect keyboardRec = this.mIme.getInputContainerView().getVisibleWindowRect();
                if (keyboardRec != null) {
                    if (getKeyboard().getKeyboardDockMode() == KeyboardEx.KeyboardDockMode.DOCK_RIGHT) {
                        log.d("#keyboard total width:", Integer.valueOf(IMEApplication.from(this.mIme).getDisplayWidth()));
                        keyboardRec.left = IMEApplication.from(this.mIme).getDisplayWidth() - getKeyboard().getDisplayWidth();
                        log.d("#keyboard left width:", Integer.valueOf(keyboardRec.left));
                    }
                    Rect rec = new Rect();
                    rec.bottom = keyboardRec.top;
                    rec.top = rec.bottom;
                    containerView.showWidgetView(this.inlineContainerView);
                    this.inlineContainerView.measure(0, 0);
                    int width = inlineWidth > this.topInlineView.getMeasuredWidth() ? this.topInlineView.getMeasuredWidth() : inlineWidth;
                    rec.left = keyboardRec.left;
                    rec.right = rec.left + width;
                    rec.top -= this.inlineContainerView.getMeasuredHeight();
                    containerView.moveWidgetView(this.inlineContainerView, rec);
                }
            }
        }
    }

    private void scrollInlineWhenSelect(String selectWord, String entireWord) {
        if (selectWord == null || selectWord.isEmpty() || entireWord == null || entireWord.isEmpty()) {
            if (this.mChineseInput.getChineseWordsInline().toString().isEmpty()) {
                this.inlineScrollView.mPercentOfWidth = 1.0f;
                return;
            } else {
                this.inlineScrollView.mPercentOfWidth = 0.0f;
                return;
            }
        }
        if (!selectWord.isEmpty() && selectWord.length() > 1) {
            selectWord = selectWord.substring(0, selectWord.length() - 1);
        }
        float commitWidth = this.mTextMeasurePaint.measureText(selectWord);
        float entireWidth = this.mTextMeasurePaint.measureText(entireWord);
        float scrollToPercent = commitWidth / entireWidth;
        if (this.inlineScrollView == null) {
            return;
        }
        this.inlineScrollView.mPercentOfWidth = scrollToPercent;
    }

    public List<CharSequence> getWords(SpannableStringBuilder defaultWord, AtomicInteger defaultWordIndex) {
        int[] scratchDefaultWordIndex = new int[1];
        List<CharSequence> list = this.mChineseInput.getWords(defaultWord, scratchDefaultWordIndex, 30);
        defaultWordIndex.set(scratchDefaultWordIndex[0]);
        return list;
    }

    private CharSequence getPureCandidate(CharSequence candidate) {
        String cand;
        int index;
        return (this.mHandwritingOn && (index = (cand = candidate.toString()).lastIndexOf(36)) > 0) ? cand.substring(0, index) : candidate;
    }

    private List<CharSequence> getPureCandidates(List<CharSequence> candidates) {
        int size = candidates.size();
        List<CharSequence> pureCandidates = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            pureCandidates.add(getPureCandidate(candidates.get(i)));
        }
        return pureCandidates;
    }

    private String getExactTypeAsInline() {
        StringBuilder inline = new StringBuilder();
        this.mChineseInput.getExactTypeAsInline(inline);
        return inline.toString();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleFunctionKey(char keyCode, boolean isMultiTapping) {
        if (isDilimiter(keyCode)) {
            this.touchKeyActionHandler.shouldIgnoreAutoAccept = true;
        }
        if (keyCode == '\n') {
            if (hasActiveKeySeq()) {
                if (isModeStroke()) {
                    selectWord(getValidStrokeCandidateIndex(), null, this.candidatesListViewCJK);
                    return;
                } else {
                    selectWord(0, null, this.candidatesListViewCJK);
                    return;
                }
            }
            if (this.mHandWritingOnKeyboardHandler != null && this.mHandWritingOnKeyboardHandler.recognitionCandidatesAvailable()) {
                acceptHWRAndUpdateListView();
            }
            sendKeyChar(keyCode);
            return;
        }
        if ((shouldHandleKeyViaIME(keyCode) || !handleKey(keyCode, false, 0)) && !handleGesture(keyCode)) {
            this.mIme.onKey(null, keyCode, null, null, 0L);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class TouchKeyActionHandler implements KeyboardTouchEventDispatcher.TouchKeyActionHandler {
        private final char[] functionKey;
        private final SparseIntArray keyIndices;
        private boolean shouldClearInvalidKey;
        private boolean shouldIgnoreAutoAccept;
        private boolean touchCanceled;

        private TouchKeyActionHandler() {
            this.functionKey = new char[1];
            this.keyIndices = new SparseIntArray();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void deleteOneKeyAndRefresh() {
            ChineseInputView.this.mChineseInput.deleteOneKeyAndRefresh();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearAllKeys() {
            ChineseInputView.this.mChineseInput.clearAllKeys();
        }

        @Override // com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher.TouchKeyActionHandler
        public void touchStarted(int pointerId, int keyIndex, KeyType keyType, float x, float y, int eventTime) {
            ChineseInputView.log.d("touchKeyActionHandler...touchStarted...pointerId: ", Integer.valueOf(pointerId), "...keyIndex: ", Integer.valueOf(keyIndex), "...keyType: ", keyType);
            if (ChineseInputView.this.mChineseInput.hasActiveInput() || ChineseInputView.this.mHandWritingOnKeyboardHandler == null || !ChineseInputView.this.mHandWritingOnKeyboardHandler.onTouchStarted(pointerId)) {
                ChineseInputView.this.pressKey(ChineseInputView.this.mKeys, keyIndex);
                ChineseInputView.this.setKeyState(keyIndex, KeyboardViewEx.ShowKeyState.Pressed);
                ChineseInputView.this.showPreviewKey(keyIndex, pointerId);
                ChineseInputView.this.resetTrace(pointerId);
                this.shouldIgnoreAutoAccept = false;
                this.touchCanceled = false;
                ChineseInputView.this.isShiftedPopupMenu = false;
                this.keyIndices.put(pointerId, keyIndex);
            }
        }

        @Override // com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher.TouchKeyActionHandler
        public void touchMoved(int pointerId, int keyIndex, float[] xcoords, float[] ycoords, int[] times, boolean canBeTraced) {
            ChineseInputView.log.d("touchKeyActionHandler...touchMoved...pointerId: ", Integer.valueOf(pointerId), "...keyIndex: ", Integer.valueOf(keyIndex), "...canBeTraced: ", Boolean.valueOf(canBeTraced), "...xcoords: ", Integer.valueOf(xcoords.length), "...ycoords: ", Integer.valueOf(ycoords.length));
            if (canBeTraced || keyIndex != this.keyIndices.get(pointerId)) {
                ChineseInputView.this.releaseKey(ChineseInputView.this.mKeys, this.keyIndices.get(pointerId), false);
                ChineseInputView.this.hideKeyPreview(pointerId);
                this.keyIndices.put(pointerId, keyIndex);
                if (!ChineseInputView.this.mChineseInput.hasActiveInput() && ChineseInputView.this.mHandWritingOnKeyboardHandler != null && ChineseInputView.this.mHandWritingOnKeyboardHandler.onTouchMoved(pointerId, keyIndex, xcoords, ycoords, times, canBeTraced)) {
                    return;
                }
            } else if (!ChineseInputView.this.mChineseInput.hasActiveInput() && ChineseInputView.this.mHandWritingOnKeyboardHandler != null && ChineseInputView.this.mHandWritingOnKeyboardHandler.onTouchMoved(pointerId, keyIndex, xcoords, ycoords, times, canBeTraced)) {
                if (ChineseInputView.this.mHandWritingOnKeyboardHandler.isWriting()) {
                    ChineseInputView.this.releaseKey(ChineseInputView.this.mKeys, keyIndex, false);
                    ChineseInputView.this.hideKeyPreview(pointerId);
                    return;
                }
                return;
            }
            ChineseInputView.this.onTouchMoved(pointerId, xcoords, ycoords, times, canBeTraced, keyIndex);
        }

        @Override // com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher.TouchKeyActionHandler
        public void touchEnded(int pointerId, int keyIndex, KeyType keyType, boolean isTraced, boolean quickPressed, float x, float y, int eventTime) {
            ChineseInputView.log.d("touchKeyActionHandler...touchEnded...pointerId: ", Integer.valueOf(pointerId), "...keyIndex: ", Integer.valueOf(keyIndex), "...keyType: ", keyType, "...isTraced: ", Boolean.valueOf(isTraced), "...quickPressed: ", Boolean.valueOf(quickPressed));
            ChineseInputView.this.releaseKey(ChineseInputView.this.mKeys, this.keyIndices.get(pointerId), true);
            ChineseInputView.this.hideKeyPreview(pointerId);
            ChineseInputView.this.setKeyState(-1, KeyboardViewEx.ShowKeyState.Released);
            if (ChineseInputView.this.mChineseInput.hasActiveInput() || ChineseInputView.this.mHandWritingOnKeyboardHandler == null || !ChineseInputView.this.mHandWritingOnKeyboardHandler.onTouchEnded(pointerId, keyIndex, keyType, isTraced)) {
                if (!this.touchCanceled) {
                    if (quickPressed && keyType.isFunctionKey()) {
                        quickPress(pointerId, keyIndex, keyType);
                    } else {
                        KeyboardEx.Key key = ChineseInputView.this.getKey(keyIndex);
                        if (isTraced) {
                            if (!ChineseInputView.this.isPinyinQwertyShiftedState() && ChineseInputView.this.mChineseInput.processStoredTouch(-1, this.functionKey)) {
                                ChineseInputView.this.handleTrace(null);
                            }
                        } else if (keyType.isFunctionKey()) {
                            if (!ChineseInputView.this.isDilimiter(key.codes[0]) || !ChineseInputView.this.hasActiveKeySeq() || ChineseInputView.this.mLastInput != 2) {
                                ChineseInputView.this.handleFunctionKey((char) key.codes[0], false);
                            } else {
                                processStoredTouch(key, keyType);
                            }
                        } else if (keyType.isLetter() || ChineseInputView.this.isAddingZhuyinTone(key.codes[0])) {
                            if (ChineseInputView.this.isPinyinQwertyShiftedState()) {
                                ChineseInputView.this.updateInput(null, Character.toUpperCase(key.codes[0]), null, 0L, true);
                            } else {
                                ChineseInputView.this.mCurrentIndex = keyIndex;
                                processStoredTouch(key, keyType);
                            }
                        } else if (keyType.isString()) {
                            if (!ChineseInputView.this.isComponent()) {
                                ChineseInputView.this.onText(key.text, 0L);
                            }
                        } else if (ChineseInputView.this.isDilimiter(key.codes[0])) {
                            if (!ChineseInputView.this.isAltCharacterToggled()) {
                                ChineseInputView.this.handleFunctionKey((char) key.codes[0], false);
                            } else {
                                processStoredTouch(key, keyType);
                            }
                        } else if (ChineseInputView.this.isCangjieWildCard(key.codes[0]) && ChineseInputView.this.isCangjieQwerty()) {
                            processStoredTouch(key, keyType);
                        } else if (key.codes[0] != 48 || !ChineseInputView.this.isModePinyin() || !ChineseInputView.this.mKeyboardSwitcher.isKeypadInput() || !ChineseInputView.this.isNormalTextInputMode()) {
                            ChineseInputView.this.handleSeparator(ChineseInputView.this.getKeycode(key));
                            ChineseInputView.this.mChineseInput.clearAllKeys();
                        } else {
                            ChineseInputView.this.updateInput(null, key.codes[0], key.codes, 0L, true);
                        }
                    }
                }
                ChineseInputView.this.mLastInput = isTraced ? 2 : 1;
                this.keyIndices.put(pointerId, -1);
                this.shouldIgnoreAutoAccept = false;
                if (isTraced) {
                    ChineseInputView.this.finishTrace(pointerId);
                }
            }
        }

        private void processStoredTouch(KeyboardEx.Key key, KeyType keyType) {
            this.shouldIgnoreAutoAccept = ChineseInputView.this.isDilimiter(key.codes[0]) || ChineseInputView.this.isAddingZhuyinTone(key.codes[0]);
            if (!ChineseInputView.this.mChineseInput.processStoredTouch(-1, this.functionKey)) {
                ChineseInputView.this.mChineseInput.clearAllKeys();
                return;
            }
            if (this.functionKey[0] != 0) {
                ChineseInputView.this.handleFunctionKey((char) key.codes[0], false);
                return;
            }
            if (ChineseInputView.this.mChineseInput.hasActiveInput()) {
                if (!ChineseInputView.this.isCangjieKeypad() || !ChineseInputView.this.isMultitapping()) {
                    int preKeyCount = ChineseInputView.this.mChineseInput.getKeyCount();
                    boolean success = ChineseInputView.this.mChineseInput.tryBuildingWordCandidateList(true);
                    int postKeyCount = ChineseInputView.this.mChineseInput.getKeyCount();
                    boolean success2 = preKeyCount <= postKeyCount && success;
                    ChineseInputView.this.updateInput(keyType.isLetter() ? new Point(key.x, key.y) : null, key.codes[0], key.codes, 0L, ChineseInputView.this.isDilimiter(key.codes[0]) && !success2);
                    return;
                }
                if (ChineseInputView.this.multitapIsInvalid() && (ChineseInputView.this.mCurrentIndex != ChineseInputView.this.mInvalidIndex || this.shouldClearInvalidKey)) {
                    ChineseInputView.this.multitapClearInvalid();
                    int keyCount = ChineseInputView.this.mChineseInput.getKeyCount();
                    if (keyCount >= 2) {
                        ChineseInputView.this.mChineseInput.clearKeyByIndex(keyCount - 2, 1);
                    }
                }
                if (ChineseInputView.this.mChineseInput.multiTapBuildWordCandidateList()) {
                    ChineseInputView.this.updateInput(keyType.isLetter() ? new Point(key.x, key.y) : null, key.codes[0], key.codes, 0L, false);
                    ChineseInputView.this.multitapClearInvalid();
                    return;
                }
                if (ChineseInputView.this.isMaxKeysCangjieOrQuickCangjie()) {
                    ChineseInputView.this.mChineseInput.multiTapTimeOut();
                    ChineseInputView.this.multitapClearInvalid();
                    ChineseInputView.this.mChineseInput.tryBuildingWordCandidateList(true);
                    ChineseInputView.this.updateInput(keyType.isLetter() ? new Point(key.x, key.y) : null, key.codes[0], key.codes, 0L, false);
                    return;
                }
                ChineseInputView.this.mChineseInput.backupWordSymbolInfo();
                ChineseInputView.this.mInvalidKey = key.codes[0];
                ChineseInputView.this.mInvalidIndex = ChineseInputView.this.mCurrentIndex;
                ChineseInputView.this.mChineseInput.tryBuildingWordCandidateList(true);
                ChineseInputView.this.updateInput(keyType.isLetter() ? new Point(key.x, key.y) : null, key.codes[0], key.codes, 0L, false);
                ChineseInputView.this.mChineseInput.restoreWordSymbolInfo();
                ChineseInputView.this.mChineseInput.setMultiTapHasInvalidKey(true);
            }
        }

        public void quickPress(int pointerId, int keyIndex, KeyType keyType) {
            ChineseInputView.log.d("touchKeyActionHandler...quickPress...keyIndex: ", Integer.valueOf(keyIndex), "...keyType: ", keyType);
            if (keyType.isFunctionKey()) {
                ChineseInputView.this.handleKey(ChineseInputView.this.getKey(keyIndex).codes[0], true, 0);
            }
        }

        @Override // com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher.TouchKeyActionHandler
        public void touchCanceled(int pointerId, int keyIndex) {
            ChineseInputView.log.d("touchKeyActionHandler...touchCanceled...keyIndex: ", Integer.valueOf(keyIndex));
            this.touchCanceled = true;
            ChineseInputView.this.resetTrace(pointerId);
            ChineseInputView.this.mInMultiTap = false;
            if (keyIndex == -1) {
                if (this.keyIndices.get(pointerId, -1) != -1) {
                    ChineseInputView.this.releaseKey(ChineseInputView.this.mKeys, this.keyIndices.get(pointerId), true);
                }
                ChineseInputView.this.hideKeyPreview(pointerId);
                ChineseInputView.this.setKeyState(-1, KeyboardViewEx.ShowKeyState.Released);
            }
            if (ChineseInputView.this.mHandWritingOnKeyboardHandler != null) {
                ChineseInputView.this.mHandWritingOnKeyboardHandler.onTouchEnded(pointerId, keyIndex, KeyType.UNKNOWN, false);
            }
        }

        @Override // com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher.TouchKeyActionHandler
        public boolean touchHeld(int pointerId, int keyIndex, KeyType keyType) {
            ChineseInputView.log.d("touchKeyActionHandler...touchHeld...keyIndex: ", Integer.valueOf(keyIndex), "...keyType: ", keyType);
            if (ChineseInputView.this.mHandWritingOnKeyboardHandler == null || !ChineseInputView.this.mHandWritingOnKeyboardHandler.isWriting()) {
                return ChineseInputView.this.onShortPress(ChineseInputView.this.getKey(keyIndex), keyIndex, pointerId);
            }
            return true;
        }

        @Override // com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher.TouchKeyActionHandler
        public boolean touchHeldRepeat(int pointerId, int keyIndex, KeyType keyType, int repeatCount) {
            ChineseInputView.log.d("touchKeyActionHandler...touchHeldRepeat...keyIndex: ", Integer.valueOf(keyIndex), "...keyType: ", keyType);
            if (ChineseInputView.this.mIme != null && keyType.isFunctionKey()) {
                KeyboardEx.Key key = ChineseInputView.this.getKey(keyIndex);
                if (key.codes[0] == 8) {
                    return ChineseInputView.this.handleBackspace(repeatCount);
                }
                if (KeyboardEx.isArrowKeys(key.codes[0])) {
                    if (!ChineseInputView.this.handleKey(key.codes[0], false, repeatCount)) {
                        ChineseInputView.this.mIme.onKey(null, key.codes[0], null, null, 0L);
                    }
                    return true;
                }
            }
            return false;
        }

        @Override // com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher.TouchKeyActionHandler
        public void touchHeldMove(int pointerId, int keyIndex, float[] xcoords, float[] ycoords, int[] times) {
            ChineseInputView.log.d("touchKeyActionHandler...touchHeldMove...pointerId:", "...keyIndex: ", Integer.valueOf(keyIndex));
            if (ChineseInputView.this.mHandWritingOnKeyboardHandler != null && ChineseInputView.this.mHandWritingOnKeyboardHandler.isWriting()) {
                ChineseInputView.this.releaseKey(ChineseInputView.this.mKeys, keyIndex, true);
                ChineseInputView.this.hideKeyPreview(pointerId);
                ChineseInputView.this.setKeyState(-1, KeyboardViewEx.ShowKeyState.Released);
                return;
            }
            ChineseInputView.this.onTouchHeldMoved(pointerId, xcoords, ycoords, times);
        }

        @Override // com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher.TouchKeyActionHandler
        public void touchHeldEnded(int pointerId, int keyIndex, KeyType keyType) {
            ChineseInputView.log.d("touchKeyActionHandler...touchHeldEnded...pointerId:", "...keyIndex: ", Integer.valueOf(keyIndex));
            if (ChineseInputView.this.mHandWritingOnKeyboardHandler != null && ChineseInputView.this.mHandWritingOnKeyboardHandler.isWriting()) {
                ChineseInputView.this.releaseKey(ChineseInputView.this.mKeys, keyIndex, true);
                ChineseInputView.this.hideKeyPreview(pointerId);
                ChineseInputView.this.setKeyState(-1, KeyboardViewEx.ShowKeyState.Released);
            } else {
                ChineseInputView.this.onTouchHeldEnded(pointerId, ChineseInputView.this.getKey(keyIndex));
                ChineseInputView.this.releaseKey(ChineseInputView.this.mKeys, keyIndex, true);
                ChineseInputView.this.hideKeyPreview(pointerId);
                ChineseInputView.this.setKeyState(-1, KeyboardViewEx.ShowKeyState.Released);
                ChineseInputView.this.mLastInput = 1;
            }
        }

        @Override // com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher.TouchKeyActionHandler
        public void touchHelpRepeatEnded(int pointerId, int keyIndex, KeyType keyType) {
            ChineseInputView.this.releaseKey(ChineseInputView.this.mKeys, keyIndex, true);
            ChineseInputView.this.mIme.releaseRepeatKey();
            ChineseInputView.this.hideKeyPreview(pointerId);
            ChineseInputView.this.setKeyState(-1, KeyboardViewEx.ShowKeyState.Released);
        }

        @Override // com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher.TouchKeyActionHandler
        public void multiTapTimerTimeOut() {
            ChineseInputView.log.d("touchKeyActionHandler...multiTapTimerTimeOut");
            ChineseInputView.this.mInMultiTap = false;
            if (ChineseInputView.this.hasActiveKeySeq() && ChineseInputView.this.multitapIsInvalid()) {
                this.shouldClearInvalidKey = true;
            }
        }

        @Override // com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher.TouchKeyActionHandler
        public void handleKeyboardShiftState(float x) {
        }

        @Override // com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher.TouchKeyActionHandler
        public void multiTapTimerTimeoutActive() {
            ChineseInputView.log.d("touchKeyActionHandler...multiTapTimerTimeoutActive");
            ChineseInputView.this.mInMultiTap = true;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.KeyboardViewEx
    public void multitapClearInvalid() {
        super.multitapClearInvalid();
        this.mInvalidIndex = -1;
        this.touchKeyActionHandler.shouldClearInvalidKey = false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.KeyboardViewEx
    public boolean multitapIsInvalid() {
        return super.multitapIsInvalid() && this.mInvalidIndex != -1;
    }

    @Override // com.nuance.swype.input.KeyboardViewEx
    public KeyboardViewEx.OnKeyboardActionListener getOnKeyboardActionListener() {
        return this.mKeyboardActionListener;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.KeyboardViewEx
    public void onTouchHeldEnded(int pointerId, KeyboardEx.Key key) {
        if (!touchUpHandleBySlideSelectPopup(this.mHshPointers.get(pointerId), null)) {
            if ((isModeBPMF() || isModeDoublePinyin()) && !this.mKeyboardSwitcher.isKeypadInput() && this.mKeyboardSwitcher.isAlphabetMode()) {
                notifyKeyboardListenerOnKey(null, key.codes[0], key.codes, null, 0L);
            } else {
                notifyKeyboardListenerOnText(key.altLabel != null ? key.altLabel : key.label, 0L);
            }
        }
        if (this.isShiftedPopupMenu) {
            setShiftState(Shift.ShiftState.LOCKED);
        }
        this.isShiftedPopupMenu = false;
        dismissPopupKeyboard();
        dismissSingleAltCharPopup();
        dismissPreviewPopup();
    }
}
