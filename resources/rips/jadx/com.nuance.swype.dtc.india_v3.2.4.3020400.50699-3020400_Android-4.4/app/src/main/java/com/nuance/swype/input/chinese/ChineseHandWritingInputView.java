package com.nuance.swype.input.chinese;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputConnection;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import com.nuance.android.compat.ActivityManagerCompat;
import com.nuance.android.util.WeakReferenceHandler;
import com.nuance.connect.common.Integers;
import com.nuance.input.swypecorelib.Shift;
import com.nuance.input.swypecorelib.T9Write;
import com.nuance.input.swypecorelib.T9WriteChinese;
import com.nuance.input.swypecorelib.T9WriteRecognizerListener;
import com.nuance.input.swypecorelib.XT9CoreChineseInput;
import com.nuance.input.swypecorelib.XT9CoreInput;
import com.nuance.swype.input.AppPreferences;
import com.nuance.swype.input.CandidatesListView;
import com.nuance.swype.input.Composition;
import com.nuance.swype.input.FunctionBarListView;
import com.nuance.swype.input.HandWritingView;
import com.nuance.swype.input.IME;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.InputFieldInfo;
import com.nuance.swype.input.InputMethods;
import com.nuance.swype.input.InputView;
import com.nuance.swype.input.KeyboardEx;
import com.nuance.swype.input.KeyboardViewEx;
import com.nuance.swype.input.QuickToast;
import com.nuance.swype.input.R;
import com.nuance.swype.input.SpeechWrapper;
import com.nuance.swype.input.Stroke;
import com.nuance.swype.input.UserPreferences;
import com.nuance.swype.input.chinese.CJKCandidatesListView;
import com.nuance.swype.input.chinese.symbol.SymbolInputController;
import com.nuance.swype.input.emoji.EmojiInfo;
import com.nuance.swype.input.emoji.EmojiLoader;
import com.nuance.swype.input.keyboard.DefaultHWRTouchKeyHandler;
import com.nuance.swype.input.keyboard.InputContextRequestDispatcher;
import com.nuance.swype.input.settings.InputPrefs;
import com.nuance.swype.util.CharacterUtilities;
import com.nuance.swype.util.ContactUtils;
import com.nuance.swype.util.LogManager;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/* loaded from: classes.dex */
public class ChineseHandWritingInputView extends InputView implements T9WriteRecognizerListener.OnWriteRecognizerListener, HandWritingView.OnWritingAction, CJKCandidatesListView.OnWordSelectActionListener {
    private static final int INPUT_MODE_PINYIN = 0;
    static final int MSG_DELAY_RECOGNIZER = 1;
    static final int MSG_DELAY_RECOGNIZER_ADD_STROKE = 2;
    private static final int MSG_HANDLE_CHAR = 3;
    private static final int MSG_HANDLE_SUGGESTION_CANDIDATES = 2;
    private static final int MSG_HANDLE_TEXT = 4;
    static final int MSG_SHOW_HOW_TO_TOAST = 503;
    private View candidatesPopup;
    private List<CharSequence> candidatesPopupDataList;
    private ChineseKeyboardViewEx candidatesPopupKeyboardViewEx;
    private ScrollView candidatesPopupScrollView;
    private final Handler.Callback delayRecognizeHandlerCallback;
    private boolean gridViewFunctionButtonPressed;
    private final Handler.Callback hwrInputViewCallback;
    private List<Point> mCachedPoints;
    private List<CharSequence> mCandidatesToShow;
    private XT9CoreChineseInput mChineseInput;
    protected Composition mComposition;
    private List<CharSequence> mContextCandidates;
    protected SpellPhraseViewContainer mContextListViewContainer;
    private ChineseHandWritingView mCurrentWritingPad;
    Handler mDelayRecognizeHandler;
    private FunctionBarListView mFunctionBarListView;
    protected ChineseHandWritingViewContainer mHWContainer;
    Handler mHwrInputViewHandler;
    private boolean mInitiativeAccept;
    protected boolean mIsIMEActive;
    private boolean mPopupCandidateListShowable;
    Handler mPopupViewHandler;
    private List<CharSequence> mRecognitionCandidates;
    protected List<ArrayList<KeyboardEx.GridKeyInfo>> mRows;
    private boolean mShownFuntionBar;
    public String mStringHandWriting123Mode;
    public String mStringHandWritingABCMode;
    private T9WriteChinese mWriteChinese;
    private boolean mWritingOrRecognizing;
    private final Handler.Callback popupHandlerCallback;
    private SymbolInputController symInputController;
    private static final LogManager.Log log = LogManager.getLog("ChineseHandWritingInputView");
    private static int CHINESE_CATEGORY = 100;
    private static int ALPHA_CATEGORY = 101;
    private static int SYMBOL_CATEGORY = 102;

    public ChineseHandWritingInputView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChineseHandWritingInputView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContextCandidates = new ArrayList();
        this.mCandidatesToShow = new ArrayList();
        this.mShownFuntionBar = false;
        this.mCachedPoints = new ArrayList();
        this.mRows = new ArrayList();
        this.mPopupCandidateListShowable = true;
        this.mInitiativeAccept = false;
        this.mWritingOrRecognizing = false;
        this.popupHandlerCallback = new Handler.Callback() { // from class: com.nuance.swype.input.chinese.ChineseHandWritingInputView.1
            @Override // android.os.Handler.Callback
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case 11:
                        if (ChineseHandWritingInputView.this.mSpeechWrapper != null && ChineseHandWritingInputView.this.mSpeechWrapper.isResumable()) {
                            ChineseHandWritingInputView.this.flushCurrentActiveWord();
                        }
                        ChineseHandWritingInputView.this.setSpeechViewHost();
                        ChineseHandWritingInputView.this.resumeSpeech();
                        return true;
                    case 503:
                        ChineseHandWritingInputView.this.showHowToUseToast();
                        return true;
                    default:
                        return true;
                }
            }
        };
        this.mPopupViewHandler = WeakReferenceHandler.create(this.popupHandlerCallback);
        this.hwrInputViewCallback = new Handler.Callback() { // from class: com.nuance.swype.input.chinese.ChineseHandWritingInputView.2
            /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
            /* JADX WARN: Code restructure failed: missing block: B:3:0x0009, code lost:            return true;     */
            @Override // android.os.Handler.Callback
            /*
                Code decompiled incorrectly, please refer to instructions dump.
                To view partially-correct code enable 'Show inconsistent code' option in preferences
            */
            public boolean handleMessage(android.os.Message r9) {
                /*
                    Method dump skipped, instructions count: 264
                    To view this dump change 'Code comments level' option to 'DEBUG'
                */
                throw new UnsupportedOperationException("Method not decompiled: com.nuance.swype.input.chinese.ChineseHandWritingInputView.AnonymousClass2.handleMessage(android.os.Message):boolean");
            }
        };
        this.mHwrInputViewHandler = WeakReferenceHandler.create(this.hwrInputViewCallback);
        this.delayRecognizeHandlerCallback = new Handler.Callback() { // from class: com.nuance.swype.input.chinese.ChineseHandWritingInputView.3
            /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
            /* JADX WARN: Code restructure failed: missing block: B:3:0x0006, code lost:            return true;     */
            @Override // android.os.Handler.Callback
            /*
                Code decompiled incorrectly, please refer to instructions dump.
                To view partially-correct code enable 'Show inconsistent code' option in preferences
            */
            public boolean handleMessage(android.os.Message r3) {
                /*
                    r2 = this;
                    r1 = 1
                    int r0 = r3.what
                    switch(r0) {
                        case 1: goto L7;
                        case 2: goto L1e;
                        default: goto L6;
                    }
                L6:
                    return r1
                L7:
                    com.nuance.swype.input.chinese.ChineseHandWritingInputView r0 = com.nuance.swype.input.chinese.ChineseHandWritingInputView.this
                    com.nuance.swype.input.chinese.ChineseHandWritingInputView.access$1500(r0)
                    com.nuance.swype.input.chinese.ChineseHandWritingInputView r0 = com.nuance.swype.input.chinese.ChineseHandWritingInputView.this
                    com.nuance.swype.input.chinese.ChineseHandWritingView r0 = com.nuance.swype.input.chinese.ChineseHandWritingInputView.access$700(r0)
                    if (r0 == 0) goto L6
                    com.nuance.swype.input.chinese.ChineseHandWritingInputView r0 = com.nuance.swype.input.chinese.ChineseHandWritingInputView.this
                    com.nuance.swype.input.chinese.ChineseHandWritingView r0 = com.nuance.swype.input.chinese.ChineseHandWritingInputView.access$700(r0)
                    r0.setNewSession(r1)
                    goto L6
                L1e:
                    com.nuance.swype.input.chinese.ChineseHandWritingInputView r0 = com.nuance.swype.input.chinese.ChineseHandWritingInputView.this
                    com.nuance.swype.input.chinese.ChineseHandWritingInputView.access$1600(r0)
                    goto L6
                */
                throw new UnsupportedOperationException("Method not decompiled: com.nuance.swype.input.chinese.ChineseHandWritingInputView.AnonymousClass3.handleMessage(android.os.Message):boolean");
            }
        };
        this.mDelayRecognizeHandler = WeakReferenceHandler.create(this.delayRecognizeHandlerCallback);
        this.mComposition = new Composition();
    }

    public void setHWContainer(ChineseHandWritingViewContainer aContainer) {
        this.mHWContainer = aContainer;
    }

    @Override // com.nuance.swype.input.InputView
    public void setCandidatesViewShown(boolean shown) {
        super.setCandidatesViewShown(shown);
        if (this.mContextListViewContainer != null) {
            if (shown) {
                this.mContextListViewContainer.setVisibility(0);
            } else {
                this.mContextListViewContainer.setVisibility(8);
            }
        }
    }

    @Override // com.nuance.swype.input.InputView
    @SuppressLint({"InflateParams", "PrivateResource"})
    public View createCandidatesView(CandidatesListView.CandidateListener onSelectListener) {
        if (this.mContextListViewContainer == null) {
            IMEApplication app = IMEApplication.from(this.mIme);
            LayoutInflater inflater = app.getThemedLayoutInflater(this.mIme.getLayoutInflater());
            app.getThemeLoader().setLayoutInflaterFactory(inflater);
            this.mContextListViewContainer = (SpellPhraseViewContainer) inflater.inflate(R.layout.chinese_hs_candidates, (ViewGroup) null);
            app.getThemeLoader().applyTheme(this.mContextListViewContainer);
            this.mContextListViewContainer.setKeyboardViewEx(this);
            this.mContextListViewContainer.initViews();
            this.candidatesListViewCJK = this.mContextListViewContainer.getCJKCandidatesListView();
            this.mFunctionBarListView = (FunctionBarListView) this.mContextListViewContainer.findViewById(R.id.functionbar);
            if (!isValidBuild()) {
                this.mContextListViewContainer.showLeftArrow(false);
            }
        }
        this.candidatesListViewCJK.setOnWordSelectActionListener(this);
        this.wordListViewContainerCJK = this.mContextListViewContainer;
        this.mFunctionBarListView.setOnFunctionBarListener(new DefaultChineseFunctionBarHandler(this.mIme, this));
        return this.mContextListViewContainer;
    }

    @Override // com.nuance.swype.input.InputView
    public View getWordCandidateListContainer() {
        return this.mContextListViewContainer;
    }

    @Override // com.nuance.swype.input.InputView
    public void create(IME ime, XT9CoreInput xt9coreinput, T9Write t9write, SpeechWrapper speechWrapper) {
        super.create(ime, xt9coreinput, t9write, speechWrapper);
        this.mChineseInput = (XT9CoreChineseInput) xt9coreinput;
        this.mWriteChinese = (T9WriteChinese) t9write;
        this.mKeyboardSwitcher = new ChineseKeyboardSwitcher(this.mIme, this.mChineseInput);
        this.mKeyboardSwitcher.setInputView(this);
        setOnKeyboardActionListener(ime);
    }

    @Override // com.nuance.swype.input.InputView
    public void destroy() {
        if (this.symInputController != null) {
            this.symInputController.hide();
            this.symInputController = null;
        }
        super.destroy();
        this.mWriteChinese.removeRecognizeListener(this);
        this.mKeyboardSwitcher = null;
        this.mCurrentWritingPad = null;
        ContactUtils.cancelQueryTask();
    }

    @Override // com.nuance.swype.input.InputView
    @SuppressLint({"PrivateResource"})
    public void startInput(InputFieldInfo inputFieldInfo, boolean restarting) {
        if (this.mChineseInput != null) {
            if (this.candidatesListViewCJK != null) {
                this.candidatesListViewCJK.updateCandidatesSize();
            }
            if (this.mHWContainer != null) {
                if (this.mHWContainer.getVerContainer() != null) {
                    this.mHWContainer.getVerContainer().updateCandidatesSize();
                }
                this.mHWContainer.updateHandwritingPadSize();
            }
            this.mIsIMEActive = true;
            this.mPopupCandidateListShowable = true;
            super.startInput(inputFieldInfo, restarting);
            this.mCompletionOn = false;
            dismissPopupKeyboard();
            if (this.symInputController != null) {
                this.symInputController.hide();
            }
            this.mShowFunctionBar = UserPreferences.from(getContext()).getShowFunctionBar();
            if (this.mShowFunctionBar) {
                this.mFunctionBarListView.recycleBitmap();
                addFunctionBarItem();
            }
            this.mChineseInput.startSession();
            setLanguage(this.mChineseInput);
            this.mChineseInput.setInputMode(0);
            this.mChineseInput.setAttribute(101, this.mFuzzyPinyin);
            this.mChineseInput.clearCommonChar();
            setHandWritingView(this.mCurrentWritingPad);
            this.mHWContainer.showHWFrameAndCharacterList();
            cancelCurrentDefaultWord();
            this.mWriteChinese.removeAllRecognizeListener();
            this.mWriteChinese.addRecognizeListener(this);
            this.mComposition.setInputConnection(getCurrentInputConnection());
            this.mIme.getInputMethods().addTextCategory(this.mWriteChinese, this.mCurrentInputLanguage);
            this.mWriteChinese.addGestureCategory();
            this.mWriteChinese.startSession(this.mCurrentInputLanguage.getCoreLanguageId());
            if (getContext().getResources().getConfiguration().orientation == 2) {
                this.mWriteChinese.setWritingDirection(0);
            } else {
                this.mWriteChinese.setWritingDirection(3);
            }
            this.mWriteChinese.applyChangedSettings();
            this.mContextCandidates.clear();
            this.mWordSourceList.clear();
            if (this.mRecognitionCandidates != null) {
                this.mRecognitionCandidates.clear();
            }
            onBack(this.candidatesPopup);
            setKeyboardForTextEntry(inputFieldInfo);
            Resources res = getResources();
            if (this.mCurrentInputLanguage.getCoreLanguageId() == 225) {
                this.mStringHandWriting123Mode = res.getText(R.string.handwriting_123_mode_simp).toString();
            } else {
                this.mStringHandWriting123Mode = res.getText(R.string.handwriting_123_mode_trad).toString();
            }
            this.mStringHandWritingABCMode = res.getText(R.string.handwriting_abc_mode).toString();
            int delayMS = InputPrefs.getAutoDelay(UserPreferences.from(getContext()), UserPreferences.HWR_AUTO_ACCEPT_RECOGNIZE, res.getInteger(R.integer.handwriting_auto_recognize_default_ms));
            this.mWriteChinese.setRecognizerDelay(delayMS);
            clearArcs();
            changeAltIconOfSwitchingLayout(false);
            updateCandidatesList();
            this.mKeyboardSwitcher.setShiftState(Shift.ShiftState.OFF);
            removeHowToUseToastMsg();
            postHowToUseToastMsg();
            this.mWriteChinese.enableUsageLogging(false);
            postDelayResumeSpeech();
            if (this.mWriteChinese != null) {
                this.mWriteChinese.setWidth(this.mCurrentWritingPad.getWidth());
                this.mWriteChinese.setHeight(this.mCurrentWritingPad.getHeight());
                if (UserPreferences.from(getContext()).isHwrScrmodeEnabled()) {
                    this.mWriteChinese.setRecognitionMode(0);
                } else {
                    this.mWriteChinese.setRecognitionMode(1);
                }
                this.mWriteChinese.applyChangedSettings();
            }
            if (UserPreferences.from(getContext()).isHwrRotationEnabled()) {
                this.mWriteChinese.updateRotationSetting(true);
            } else {
                this.mWriteChinese.updateRotationSetting(false);
            }
            if (UserPreferences.from(getContext()).isAttachingCommonWordsLDBAllowed()) {
                this.mWriteChinese.updateAttachingCommonWordsLDB(true);
            } else {
                this.mWriteChinese.updateAttachingCommonWordsLDB(false);
            }
            if (UserPreferences.from(getContext()).isHwrAlternativeDirectionEnabled()) {
                this.mWriteChinese.updateAlternativeDirection(true);
            } else {
                this.mWriteChinese.updateAlternativeDirection(false);
            }
            if (!isValidBuild()) {
                this.mContextListViewContainer.hideFunctionBarListView();
                this.mContextListViewContainer.hideSpellPrefixSuffixList();
            }
            showTrialExpireWclMessage("CJK");
            this.mHwrInputViewHandler.removeMessages(15);
            this.mHwrInputViewHandler.sendMessageDelayed(this.mHwrInputViewHandler.obtainMessage(15, inputFieldInfo.isNameField() ? 1 : 0, this.isKeepingKeyboard ? 1 : 0), 5L);
            this.keyboardTouchEventDispatcher.resisterTouchKeyHandler(new DefaultHWRTouchKeyHandler(this.mIme, this, this.xt9coreinput, getDefaultKeyUIStateHandler()));
            this.keyboardTouchEventDispatcher.registerFlingGestureHandler(this.flingGestureListener);
            this.keyboardTouchEventDispatcher.wrapTouchEvent(this);
            this.xt9coreinput.setInputContextRequestListener(InputContextRequestDispatcher.getDispatcherInstance().setHandler(getDefaultInputContextHandler()));
        }
    }

    private void postDelayResumeSpeech() {
        if (this.mPopupViewHandler.hasMessages(11)) {
            this.mPopupViewHandler.removeMessages(11);
        }
        this.mPopupViewHandler.sendEmptyMessageDelayed(11, 1L);
    }

    @SuppressLint({"PrivateResource"})
    void showHowToUseToast() {
        AppPreferences appPrefs = AppPreferences.from(getContext());
        if (appPrefs.getBoolean("show_how_to_toggle_full_screen_mode_chn", true)) {
            appPrefs.setBoolean("show_how_to_toggle_full_screen_mode_chn", false);
            QuickToast.show(getContext(), getResources().getString(R.string.how_to_toggle_full_screen_mode), 1, (getHeight() + this.mCurrentWritingPad.getHeight()) / 2);
        }
    }

    private void postHowToUseToastMsg() {
        this.mPopupViewHandler.sendEmptyMessageDelayed(503, 10L);
    }

    private void hideHowToUseToast() {
        QuickToast.hide();
    }

    private void removeHowToUseToastMsg() {
        this.mPopupViewHandler.removeMessages(503);
    }

    private void setKeyboardForTextEntry(InputFieldInfo inputFieldInfo) {
        this.mKeyboardSwitcher.createKeyboardForTextInput(inputFieldInfo, this.mCurrentInputLanguage.getCurrentInputMode(), !this.mCurrentInputLanguage.getCurrentInputMode().isHandwriting());
        if (inputFieldInfo.isNameField()) {
            this.mWriteChinese.setAttribute(100, 1);
        } else {
            this.mWriteChinese.setAttribute(100, 0);
        }
        if (inputFieldInfo.isNumericModeField()) {
            setCandidatesViewShown(false);
            addDigitOnlyCategory();
        } else {
            setCandidatesViewShown(true);
            this.mIme.getInputMethods().addTextCategory(this.mWriteChinese, this.mCurrentInputLanguage);
            this.mWriteChinese.addGestureCategory();
        }
    }

    @Override // com.nuance.swype.input.InputView
    public void finishInput() {
        super.finishInput();
        removeHowToUseToastMsg();
        hideHowToUseToast();
        this.mIsIMEActive = false;
        this.mWritingOrRecognizing = false;
        this.mFunctionBarListView.recycleBitmap();
        acceptCurrentDefaultWord();
        dismissPopupKeyboard();
        this.mHWContainer.clearCharacterList();
        this.mContextListViewContainer.clear();
        resetWrite();
        this.mWriteChinese.removeRecognizeListener(this);
        this.mWriteChinese.finishSession();
        if (this.mRecognitionCandidates != null) {
            this.mRecognitionCandidates.clear();
            this.mRecognitionCandidates = null;
        }
        this.mContextCandidates.clear();
        this.mWordSourceList.clear();
        this.mHWContainer.requestLayout();
        if (this.mChineseInput != null) {
            this.mChineseInput.finishSession();
            dimissRemoveUdbWordDialog();
            this.keyboardTouchEventDispatcher.unwrapTouchEvent(this);
            this.xt9coreinput.setInputContextRequestListener(null);
        }
    }

    @Override // com.nuance.swype.input.InputView
    public void handleClose() {
        this.mIsIMEActive = false;
        resetWrite();
        dimissRemoveUdbWordDialog();
        this.mFunctionBarListView.recycleBitmap();
        super.handleClose();
    }

    @Override // com.nuance.swype.input.InputView
    public boolean handleKeyDown(int keyCode, KeyEvent event) {
        return this.mComposition != null && this.mComposition.length() > 0 && (keyCode == 21 || keyCode == 22 || keyCode == 19 || keyCode == 20);
    }

    @Override // com.nuance.swype.input.InputView
    public boolean handleKey(int primaryCode, boolean quickPressed, int repeatedCount) {
        hideHowToUseToast();
        switch (primaryCode) {
            case KeyboardEx.KEYCODE_KEYBOARD_RESIZE /* 2900 */:
                if (this.gridCandidateTableVisible && this.mHWContainer != null) {
                    this.mHWContainer.hideCandidatesGridView(this.candidatesPopup);
                }
                this.gridCandidateTableVisible = false;
                this.candidatesPopup = null;
                flushCurrentActiveWord();
                if (this.mShowFunctionBar) {
                    hideChineseListAndShowFunctionBar();
                }
                clearLinePath();
                return super.handleKey(primaryCode, quickPressed, repeatedCount);
            case KeyboardEx.KEYCODE_LANGUAGE_QUICK_SWITCH /* 2939 */:
                this.mHWContainer.clearCharacterList();
                this.mContextListViewContainer.clear();
                return super.handleKey(primaryCode, quickPressed, repeatedCount);
            case KeyboardEx.KEYCODE_SPEECH /* 6463 */:
                if (quickPressed) {
                    return true;
                }
                flushCurrentActiveWord();
                super.startSpeech();
                return true;
            case KeyboardEx.KEYCODE_MODE_BACK /* 43576 */:
                this.mKeyboardSwitcher.toggleLastKeyboard();
                return true;
            default:
                return super.handleKey(primaryCode, quickPressed, repeatedCount);
        }
    }

    @Override // com.nuance.swype.input.InputView
    public boolean handleKeyUp(int keyCode, KeyEvent event) {
        return this.mComposition != null && this.mComposition.length() > 0 && (keyCode == 21 || keyCode == 22 || keyCode == 19 || keyCode == 20);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleText(CharSequence text) {
        acceptCurrentDefaultWord();
        this.mComposition.insertText(text);
    }

    @Override // com.nuance.swype.input.InputView
    @SuppressLint({"DefaultLocale"})
    public void onText(CharSequence text, long eventTime) {
        if (isPendingRecognizeMessage()) {
            processPendingRecognizing();
        }
        if (this.symInputController != null && this.symInputController.isActive()) {
            InputConnection ic = getCurrentInputConnection();
            ic.beginBatchEdit();
            ic.commitText(text, 1);
            ic.endBatchEdit();
            return;
        }
        if (isShifted() && this.mIme != null) {
            CharacterUtilities charUtil = IMEApplication.from(this.mIme).getCharacterUtilities();
            CharSequence text2 = text.toString().toUpperCase();
            if (text2.length() > 1 && !charUtil.isPunctuationOrSymbol(text2.charAt(0)) && !charUtil.isPunctuationOrSymbol(text2.charAt(1)) && !CharacterUtilities.endsWithSurrogatePair(text2)) {
                text2 = text2.subSequence(0, 1);
            }
            this.mWriteChinese.queueText(text2);
            return;
        }
        this.mWriteChinese.queueText(text);
    }

    private void addDigitOnlyCategory() {
        this.mWriteChinese.addNumberOnlyCategory();
        this.mWriteChinese.addGestureCategory();
    }

    @Override // com.nuance.swype.input.InputView
    public void handleCharKey(Point point, int primaryCode, int[] keyCodes, long eventTime) {
        if (isPendingRecognizeMessage()) {
            processPendingRecognizing();
        }
        this.mWriteChinese.queueChar((char) primaryCode);
    }

    private void handleSpace() {
        acceptCurrentDefaultWord();
        sendSpace();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.InputView
    public boolean handleSpace(boolean quickPressed, int repeatedCount) {
        if (isPendingRecognizeMessage()) {
            processPendingRecognizing();
            if (this.mWriteChinese != null) {
                this.mWriteChinese.queueChar(' ');
            }
        } else if (this.mRecognitionCandidates != null && this.mRecognitionCandidates.size() > 0) {
            if (this.mWriteChinese != null) {
                acceptCurrentDefaultWord();
                updateCandidatesList();
            }
        } else {
            acceptCurrentDefaultWord();
            boolean addSpace = true;
            if (quickPressed && repeatedCount < 2 && this.mAutoPunctuationOn) {
                InputConnection ic = getCurrentInputConnection();
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

    private boolean handleBackspace() {
        if (!cancelCurrentDefaultWord()) {
            if (this.mFunctionBarListView.isFunctionBarDisabledOrZeroItem()) {
                this.mIme.sendBackspace(0);
            } else if (this.candidatesListViewCJK.isCandidateListEmpty()) {
                this.mIme.sendBackspace(0);
            }
        }
        hideChineseListAndShowFunctionBar();
        return true;
    }

    private void hideChineseListAndShowFunctionBar() {
        if (this.mHWContainer != null) {
            this.mHWContainer.clearCharacterList();
        }
        if (this.candidatesListViewCJK != null) {
            this.candidatesListViewCJK.clear();
        }
        if (this.mContextListViewContainer != null) {
            this.mContextListViewContainer.clear();
            this.mContextListViewContainer.hidePhraseListView();
            this.mContextListViewContainer.hideSpellPrefixSuffixList();
            this.mShowFunctionBar = true;
            this.mContextListViewContainer.showFunctionBarListView();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.InputView
    public boolean handleBackspace(int repeatedCount) {
        if (isPendingRecognizeMessage()) {
            processPendingRecognizing();
            if (this.mWriteChinese != null) {
                this.mWriteChinese.queueChar('\b');
                return true;
            }
            return true;
        }
        if (this.mRecognitionCandidates != null && this.mRecognitionCandidates.size() > 0) {
            if (this.mWriteChinese != null) {
                this.mWriteChinese.queueChar('\b');
                return true;
            }
            return true;
        }
        handleBackspace();
        return true;
    }

    @Override // com.nuance.swype.input.InputView
    public void flushCurrentActiveWord() {
        if (isValidBuild()) {
            ContactUtils.cancelQueryTask();
            if (this.mChineseInput != null) {
                this.mChineseInput.clearAllKeys();
            }
            if (this.mComposition != null) {
                this.mComposition.acceptCurrentInline();
            }
            if (this.mRecognitionCandidates != null) {
                this.mRecognitionCandidates.clear();
                this.mRecognitionCandidates = null;
            }
            if (this.mContextCandidates != null) {
                this.mContextCandidates.clear();
            }
            this.mWordSourceList.clear();
            if (this.candidatesListViewCJK != null) {
                this.candidatesListViewCJK.clear();
            }
            this.mInitiativeAccept = true;
            dimissRemoveUdbWordDialog();
            clearArcs();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.InputView
    public void clearAllKeys() {
    }

    private boolean cancelCurrentDefaultWord() {
        if (this.mComposition == null || this.candidatesListViewCJK == null || this.mComposition.length() <= 0) {
            return false;
        }
        this.mComposition.clearCurrentInline();
        if (this.mRecognitionCandidates != null) {
            this.mRecognitionCandidates.clear();
            this.mRecognitionCandidates = null;
        }
        this.mContextListViewContainer.clear();
        return true;
    }

    private boolean acceptCurrentDefaultWord() {
        if (this.mRecognitionCandidates != null && this.mRecognitionCandidates.size() > 0) {
            if (this.mWriteChinese != null) {
                this.mWriteChinese.noteSelectedCandidate(0);
                this.mComposition.insertText(this.mRecognitionCandidates.get(0));
                this.mRecognitionCandidates.clear();
                this.mRecognitionCandidates = null;
                this.mContextCandidates.clear();
                this.mWordSourceList.clear();
            }
            return true;
        }
        flushCurrentActiveWord();
        return false;
    }

    private void getContextCandidates() {
        int i = 0;
        boolean success = true;
        this.mContextCandidates.clear();
        this.mWordSourceList.clear();
        while (i < 20 && success) {
            StringBuilder word = new StringBuilder(32);
            success = this.mWriteChinese.getWord(i, word, this.mWordSource);
            if (success) {
                this.mContextCandidates.add(word);
                if (this.mWordSource.get() == 7) {
                    this.mWordSourceList.add(i, new AtomicInteger(7));
                } else {
                    this.mWordSourceList.add(i, new AtomicInteger(1));
                }
                i++;
            }
        }
    }

    public void setActiveCandidate(int n) {
        if (this.mCandidatesToShow != null && n >= 0 && n < this.mCandidatesToShow.size()) {
            this.mComposition.showInline(this.mCandidatesToShow.get(n));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code restructure failed: missing block: B:29:0x0075, code lost:            if (r6.candidatesListViewCJK != null) goto L28;     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void updateCandidatesList() {
        /*
            Method dump skipped, instructions count: 381
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.nuance.swype.input.chinese.ChineseHandWritingInputView.updateCandidatesList():void");
    }

    public void showInline() {
        if (this.mRecognitionCandidates != null && !this.mRecognitionCandidates.isEmpty()) {
            log.d("showInline", this.mRecognitionCandidates.get(0));
            this.mComposition.showInline(getPureCandidate(this.mRecognitionCandidates.get(0)));
        }
    }

    public void setHandWritingView(HandWritingView view) {
        if (this.mComposition.length() > 0) {
            this.mComposition.clearCurrentInline();
        }
        if (view instanceof ChineseHandWritingView) {
            this.mCurrentWritingPad = (ChineseHandWritingView) view;
        } else {
            this.mCurrentWritingPad = null;
        }
        if (this.mCurrentWritingPad != null) {
            this.mCurrentWritingPad.setFullScreen(false);
            if (this.mCurrentInputLanguage != null) {
                this.mCurrentWritingPad.setIntegratedEnabled(isIntegratedHandwriting());
            }
        }
        clearArcs();
    }

    @Override // com.nuance.swype.input.HandWritingView.OnWritingAction
    public void penDown(View src) {
        dismissPopupKeyboard();
        removeDelayRecognitionMsg();
        removeDelayRecognitionStroke();
        if (this.mCurrentWritingPad != null) {
            this.mCurrentWritingPad.mFaddingStrokeQueue.startActionFadingPenDown();
        }
        if (this.mCurrentWritingPad != src && (src instanceof ChineseHandWritingView)) {
            this.mCurrentWritingPad = (ChineseHandWritingView) src;
            if (this.mWriteChinese != null && !isIntegratedHandwriting() && !ActivityManagerCompat.isUserAMonkey()) {
                this.mWriteChinese.setWidth(this.mCurrentWritingPad.getWidth());
                this.mWriteChinese.setWidth(this.mCurrentWritingPad.getHeight());
                this.mWriteChinese.applyChangedSettings();
            }
        }
        if (!this.mWritingOrRecognizing) {
            acceptCurrentDefaultWord();
            if (this.mKeyboardSwitcher != null && !this.mKeyboardSwitcher.isSymbolMode() && !this.mKeyboardSwitcher.isNumMode()) {
                updateCandidatesList();
            }
        }
        if (this.mWriteChinese != null && !this.mWritingOrRecognizing && isIntegratedHandwriting()) {
            this.mWriteChinese.startArcsAddingSequence();
        }
        this.mWritingOrRecognizing = true;
    }

    @Override // com.nuance.swype.input.HandWritingView.OnWritingAction
    public void penUp(List<Point> arc, View src) {
        if (this.mCurrentWritingPad != null) {
            this.mCurrentWritingPad.mFaddingStrokeQueue.startActionFading();
            this.mCurrentWritingPad.mFaddingStrokeQueue.startFading();
        }
        this.mCachedPoints.addAll(arc);
        if (this.mWriteChinese != null) {
            if (isPendingRecognizeStrokeMessage()) {
                removeDelayRecognitionStroke();
                this.mDelayRecognizeHandler.sendEmptyMessageDelayed(2, 100L);
            } else {
                this.mDelayRecognizeHandler.sendEmptyMessageDelayed(2, 100L);
            }
        }
        if (this.mWriteChinese != null) {
            if (isPendingRecognizeMessage()) {
                removeDelayRecognitionMsg();
                this.mDelayRecognizeHandler.sendEmptyMessageDelayed(1, this.mWriteChinese.getRecognizerDelay());
            } else {
                this.mDelayRecognizeHandler.sendEmptyMessageDelayed(1, this.mWriteChinese.getRecognizerDelay());
            }
        }
        this.mPopupCandidateListShowable = false;
    }

    private void showContextCandidatesView() {
        if (this.mCandidatesToShow != null && !this.mCandidatesToShow.isEmpty()) {
            showCandidatesView(this.mCandidatesToShow, this.mWordSourceList);
        }
    }

    public void showCharacterCandidatesView() {
    }

    @Override // com.nuance.swype.input.chinese.CJKCandidatesListView.OnWordSelectActionListener
    public void selectWord(int index, CharSequence word, View source) {
        if (this.mRecognitionCandidates != null) {
            int wordIndex = this.mRecognitionCandidates.indexOf(word);
            if (this.mWriteChinese != null) {
                if (wordIndex != -1) {
                    this.mWriteChinese.noteSelectedCandidate(wordIndex);
                } else {
                    this.mWriteChinese.noteSelectedCandidate(0);
                }
            }
        }
        log.d("selectWord showInline: select word:", word);
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
        if (isPendingRecognizeMessage()) {
            removeDelayRecognitionMsg();
            if (this.mWriteChinese != null && !ActivityManagerCompat.isUserAMonkey()) {
                this.mWriteChinese.queueRecognition(null);
                this.mWriteChinese.endArcsAddingSequence();
            }
            this.mCachedPoints.clear();
            this.mWritingOrRecognizing = false;
            this.mCurrentWritingPad.mFaddingStrokeQueue.stopFading();
            if (ActivityManagerCompat.isUserAMonkey()) {
                clearArcs();
            }
            if (this.mCurrentWritingPad != null) {
                this.mCurrentWritingPad.setNewSession(true);
            }
        }
        this.mRecognitionCandidates = null;
        this.mCandidatesToShow.clear();
        this.mContextCandidates.clear();
        this.mWordSourceList.clear();
        updateCandidatesList();
        if (selected_emoji != null) {
            super.addEmojiInRecentCat(selected_emoji, word.toString());
        }
    }

    @Override // com.nuance.swype.input.InputView
    public void updateSelection(int oldSelStart, int oldSelEnd, int newSelStart, int newSelEnd, int[] candidatesIndices) {
        SpeechWrapper spw;
        super.updateSelection(oldSelStart, oldSelEnd, newSelStart, newSelEnd, candidatesIndices);
        boolean cursorChanged = !((newSelStart == candidatesIndices[1] && newSelEnd == candidatesIndices[1]) || candidatesIndices[1] == -1) || (candidatesIndices[1] == -1 && newSelEnd == 0);
        if ((this.mRecognitionCandidates == null || this.mRecognitionCandidates.isEmpty()) && !cursorChanged) {
            SpeechWrapper spw2 = IMEApplication.from(getContext()).getSpeechWrapper();
            if (spw2 == null || !spw2.isStreamingDictation()) {
                if (this.mComposition != null && this.mComposition.length() > 0) {
                    this.mComposition.acceptCurrentInline();
                } else {
                    InputConnection ic = getCurrentInputConnection();
                    if (ic != null) {
                        ic.finishComposingText();
                    }
                }
            }
            if (this.mInitiativeAccept) {
                this.mInitiativeAccept = false;
            }
        } else if (!cursorChanged && candidatesIndices[0] == candidatesIndices[1] && candidatesIndices[0] == -1) {
            if (this.mInitiativeAccept) {
                this.mInitiativeAccept = false;
            } else {
                clearArcs();
                this.mRecognitionCandidates = null;
            }
        } else if (this.mComposition != null && cursorChanged && ((spw = IMEApplication.from(getContext()).getSpeechWrapper()) == null || !spw.isStreamingDictation())) {
            if (this.mComposition.length() > 0) {
                this.mComposition.acceptCurrentInline();
            } else {
                InputConnection ic2 = getCurrentInputConnection();
                if (ic2 != null) {
                    ic2.finishComposingText();
                }
            }
            clearArcs();
            this.mRecognitionCandidates = null;
        }
        if (!isEmojiKeyboardShown()) {
            dismissPopupKeyboard();
        }
        if (this.mKeyboardSwitcher != null && this.mHWContainer != null && !this.mKeyboardSwitcher.isSymbolMode() && !this.mKeyboardSwitcher.isNumMode() && !this.mKeyboardSwitcher.isEditMode()) {
            this.mHWContainer.hideCandidatesGridView(this.candidatesPopup);
            this.gridCandidateTableVisible = false;
        }
        if (this.mShownFuntionBar && this.mFunctionBarListView.isFunctionBarShowing()) {
            this.mShownFuntionBar = false;
            return;
        }
        if (this.mKeyboardSwitcher != null && !this.mKeyboardSwitcher.isSymbolMode() && !this.mKeyboardSwitcher.isNumMode()) {
            updateCandidatesList();
        }
        if (this.mHWContainer != null) {
            this.mHWContainer.setMinimumHeight(0);
        }
    }

    @Override // com.nuance.input.swypecorelib.T9WriteRecognizerListener.OnWriteRecognizerListener
    public void onHandleWriteEvent(T9WriteRecognizerListener.WriteEvent event) {
        if (this.mIme == null || isValidBuild()) {
            switch (event.mType) {
                case 1:
                    this.mHwrInputViewHandler.sendMessageDelayed(this.mHwrInputViewHandler.obtainMessage(2, event), 5L);
                    return;
                case 2:
                    this.mHwrInputViewHandler.sendMessageDelayed(this.mHwrInputViewHandler.obtainMessage(3, event), 5L);
                    return;
                case 3:
                    this.mHwrInputViewHandler.sendMessageDelayed(this.mHwrInputViewHandler.obtainMessage(4, event), 5L);
                    return;
                default:
                    return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void clearArcs() {
        if (this.mCurrentWritingPad != null) {
            this.mCurrentWritingPad.clearArcs();
        }
    }

    private void clearLinePath() {
        if (this.mCurrentWritingPad != null) {
            this.mCurrentWritingPad.clearLinePath();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleChar(char ch) {
        if (ch == '\r') {
            ch = '\n';
        } else if (ch == '\t') {
            ch = ' ';
        }
        if (ch == '\b') {
            handleBackspace();
        } else if (ch == ' ') {
            handleSpace();
        } else {
            acceptCurrentDefaultWord();
            sendKeyChar(ch);
        }
        if (this.mRecognitionCandidates != null) {
            this.mRecognitionCandidates.clear();
            this.mRecognitionCandidates = null;
        }
        if (ch != '\b' && this.mKeyboardSwitcher != null && !this.mKeyboardSwitcher.isSymbolMode() && !this.mKeyboardSwitcher.isNumMode()) {
            updateCandidatesList();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.InputView
    public void removeAllMessages() {
        this.mHwrInputViewHandler.removeMessages(2);
        this.mHwrInputViewHandler.removeMessages(3);
        this.mHwrInputViewHandler.removeMessages(4);
        for (int msg = 1; msg <= 19; msg++) {
            this.mPopupViewHandler.removeMessages(msg);
        }
        removeDelayRecognitionMsg();
    }

    @Override // com.nuance.swype.input.InputView
    public void handleShiftKey() {
        this.mKeyboardSwitcher.cycleShiftState();
        invalidateKeyboardImage();
    }

    private void removeDelayRecognitionMsg() {
        this.mDelayRecognizeHandler.removeMessages(1);
    }

    private boolean isPendingRecognizeMessage() {
        return this.mDelayRecognizeHandler.hasMessages(1);
    }

    private void processPendingRecognizing() {
        this.mDelayRecognizeHandler.removeMessages(1);
        performDelayRecognition();
    }

    private void removeDelayRecognitionStroke() {
        this.mDelayRecognizeHandler.removeMessages(2);
    }

    private boolean isPendingRecognizeStrokeMessage() {
        return this.mDelayRecognizeHandler.hasMessages(2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void performDelayRecognitionStroke() {
        if (isIntegratedHandwriting()) {
            int start = 0;
            for (int i = 0; i < this.mCachedPoints.size(); i++) {
                if (this.mCachedPoints.get(i).x == 0 && this.mCachedPoints.get(i).y == 0) {
                    int end = i - 1;
                    if (this.mWriteChinese != null && start >= 0 && start < end) {
                        this.mWriteChinese.queueAddArcs(this.mCachedPoints.subList(start, end), null, null);
                    }
                    start = i + 1;
                }
            }
            if (this.mWriteChinese != null) {
                this.mWriteChinese.queueRecognition(null);
            }
            this.mCachedPoints.clear();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void performDelayRecognition() {
        int category;
        if (this.mCurrentInputLanguage != null && !ActivityManagerCompat.isUserAMonkey()) {
            if (!isIntegratedHandwriting()) {
                int writingPadWidth = this.mCurrentWritingPad.getWidth() - getCharacterListWidth();
                if (this.mCachedPoints.get(0).x - getCharacterListWidth() < writingPadWidth / 2) {
                    category = ALPHA_CATEGORY;
                } else {
                    category = SYMBOL_CATEGORY;
                }
                if (category != CHINESE_CATEGORY) {
                    for (int i = 1; i < this.mCachedPoints.size(); i++) {
                        Point pt = this.mCachedPoints.get(i);
                        if ((pt.x != 0 || pt.y != 0) && ((pt.x - getCharacterListWidth() > writingPadWidth / 2 && category == ALPHA_CATEGORY) || (pt.x - getCharacterListWidth() < writingPadWidth / 2 && category == SYMBOL_CATEGORY))) {
                            category = CHINESE_CATEGORY;
                            break;
                        }
                    }
                }
                if (this.mWriteChinese != null) {
                    this.mWriteChinese.clearCategory();
                    if (category == CHINESE_CATEGORY) {
                        this.mWriteChinese.addOnlyTextCategory();
                        this.mWriteChinese.addGestureCategory();
                    } else if (category == ALPHA_CATEGORY) {
                        this.mWriteChinese.addOnlyLatinLetterCategory();
                        this.mWriteChinese.addGestureCategory();
                    } else if (category == SYMBOL_CATEGORY) {
                        this.mWriteChinese.addDigitsAndSymbolsOnlyCategory();
                        this.mWriteChinese.addPunctuationCategory();
                        this.mWriteChinese.addGestureCategory();
                    }
                    this.mWriteChinese.applyChangedSettings();
                }
            }
            if (this.mWriteChinese != null && !ActivityManagerCompat.isUserAMonkey()) {
                if (!isIntegratedHandwriting()) {
                    this.mWriteChinese.startArcsAddingSequence();
                    int start = 0;
                    for (int i2 = 0; i2 < this.mCachedPoints.size(); i2++) {
                        if (this.mCachedPoints.get(i2).x == 0 && this.mCachedPoints.get(i2).y == 0) {
                            int end = i2 - 1;
                            if (start >= 0 && start < end) {
                                this.mWriteChinese.queueAddArcs(this.mCachedPoints.subList(start, end), null, null);
                            }
                            start = i2 + 1;
                        }
                    }
                    this.mWriteChinese.queueRecognition(null);
                }
                this.mWriteChinese.endArcsAddingSequence();
            }
        }
        this.mCachedPoints.clear();
        this.mWritingOrRecognizing = false;
        this.mCurrentWritingPad.mFaddingStrokeQueue.stopFading();
        if (ActivityManagerCompat.isUserAMonkey()) {
            clearArcs();
        }
    }

    private boolean isIntegratedHandwriting() {
        return this.mCurrentInputLanguage.getCurrentInputMode().isMixLetterAndIntegratedEnabled();
    }

    private void handleAutoPunct() {
        InputConnection ic = getCurrentInputConnection();
        if (ic != null) {
            ic.deleteSurroundingText(1, 0);
            ic.commitText("。", 1);
        }
    }

    @SuppressLint({"InflateParams", "ClickableViewAccessibility", "PrivateResource"})
    private void showCandidatesView(List<CharSequence> aDataList, List<AtomicInteger> aWordSourceList) {
        if (aDataList != null && !aDataList.isEmpty()) {
            this.candidatesPopupDataList = aDataList;
            int height = this.mHWContainer.getHeight() + this.mContextListViewContainer.getHeight();
            int width = this.mHWContainer.getWidth();
            this.mHWContainer.setMinimumHeight(height);
            setCandidatesViewShown(false);
            if (this.candidatesPopup != null && this.candidatesPopup.getMeasuredWidth() != width) {
                log.d("recreate candidatesPopup....candidatesPopup.getMeasuredWidth(): " + this.candidatesPopup.getMeasuredWidth() + "keyboard width: " + width);
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
                closeButton.setOnClickListener(new View.OnClickListener() { // from class: com.nuance.swype.input.chinese.ChineseHandWritingInputView.4
                    @Override // android.view.View.OnClickListener
                    public void onClick(View v) {
                        ChineseHandWritingInputView.this.mHWContainer.hideCandidatesGridView(ChineseHandWritingInputView.this.candidatesPopup);
                        ChineseHandWritingInputView.this.gridCandidateTableVisible = false;
                        ChineseHandWritingInputView.this.setCandidatesViewShown(true);
                        if (ChineseHandWritingInputView.this.mContextListViewContainer != null) {
                            ChineseHandWritingInputView.this.mContextListViewContainer.showPhraseListView();
                        }
                    }
                });
                closeButton.setOnTouchListener(new View.OnTouchListener() { // from class: com.nuance.swype.input.chinese.ChineseHandWritingInputView.5
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
                            com.nuance.swype.input.chinese.ChineseHandWritingInputView r0 = com.nuance.swype.input.chinese.ChineseHandWritingInputView.this
                            r1 = 1
                            com.nuance.swype.input.chinese.ChineseHandWritingInputView.access$1802(r0, r1)
                            goto L8
                        L10:
                            com.nuance.swype.input.chinese.ChineseHandWritingInputView r0 = com.nuance.swype.input.chinese.ChineseHandWritingInputView.this
                            com.nuance.swype.input.chinese.ChineseHandWritingInputView.access$1802(r0, r2)
                            goto L8
                        */
                        throw new UnsupportedOperationException("Method not decompiled: com.nuance.swype.input.chinese.ChineseHandWritingInputView.AnonymousClass5.onTouch(android.view.View, android.view.MotionEvent):boolean");
                    }
                });
                this.candidatesPopupScrollView = (ScrollView) this.candidatesPopup.findViewById(R.id.scroll_view);
                this.candidatesPopupKeyboardViewEx = (ChineseKeyboardViewEx) this.candidatesPopup.findViewById(R.id.keyboardViewEx);
            }
            this.candidatesPopupScrollView.scrollTo(0, 0);
            this.candidatesPopupKeyboardViewEx.setInputView(this);
            this.candidatesPopupKeyboardViewEx.setWordSource(aWordSourceList);
            this.candidatesPopupKeyboardViewEx.setGridCandidates(this.mRows, aDataList, this.candidatesPopupScrollView.getMeasuredWidth());
            final KeyboardEx keyboard = new KeyboardEx(getContext(), R.xml.kbd_chinese_popup_template, this.mRows, this.candidatesPopupScrollView.getMeasuredWidth(), height, getKeyboard().getKeyboardDockMode());
            this.candidatesPopupKeyboardViewEx.setKeyboard(keyboard);
            this.candidatesPopupKeyboardViewEx.setIme(this.mIme);
            this.gridCandidateTableVisible = true;
            this.candidatesPopupKeyboardViewEx.setOnKeyboardActionListener(new KeyboardViewEx.KeyboardActionAdapter() { // from class: com.nuance.swype.input.chinese.ChineseHandWritingInputView.6
                @Override // com.nuance.swype.input.KeyboardViewEx.OnKeyboardActionListener
                public void onText(CharSequence text, long eventTime) {
                    if (!ChineseHandWritingInputView.this.gridViewFunctionButtonPressed) {
                        ChineseHandWritingInputView.this.mHWContainer.hideCandidatesGridView(ChineseHandWritingInputView.this.candidatesPopup);
                        ChineseHandWritingInputView.this.gridCandidateTableVisible = false;
                        ChineseHandWritingInputView.this.setCandidatesViewShown(true);
                        for (int i = 0; i < ChineseHandWritingInputView.this.candidatesPopupDataList.size(); i++) {
                            if (((CharSequence) ChineseHandWritingInputView.this.candidatesPopupDataList.get(i)).toString().equals(text.toString())) {
                                ChineseHandWritingInputView.this.selectWord(i, text, ChineseHandWritingInputView.this.candidatesListViewCJK);
                            }
                        }
                    }
                }

                @Override // com.nuance.swype.input.KeyboardViewEx.KeyboardActionAdapter, com.nuance.swype.input.KeyboardViewEx.OnKeyboardActionListener
                public void onRelease(int primaryCode) {
                    keyboard.clearStickyKeys();
                }
            });
            FrameLayout.LayoutParams lp2 = new FrameLayout.LayoutParams(this.mHWContainer.getWidth(), height);
            this.candidatesPopup.setLayoutParams(lp2);
            this.mHWContainer.showCandidatesGridView(this.candidatesPopup);
        }
    }

    public void onBack(View aContainer) {
        if (aContainer != null && aContainer.equals(this.candidatesPopup)) {
            if (this.mHWContainer != null) {
                this.mHWContainer.hideCandidatesGridView(this.candidatesPopup);
                this.gridCandidateTableVisible = false;
                this.mHWContainer.setMinimumHeight(0);
            }
            setCandidatesViewShown(true);
        }
    }

    @Override // com.nuance.swype.input.HandWritingView.OnWritingAction
    public void penUp(Stroke.Arc[] arcs, View Src) {
        penUp(arcs[0].mPoints, Src);
    }

    private void resetWrite() {
        if (this.mCachedPoints != null) {
            this.mCachedPoints.clear();
        }
        clearArcs();
        if (this.mCurrentWritingPad != null) {
            this.mCurrentWritingPad.setNewSession(true);
        }
    }

    @Override // com.nuance.swype.input.chinese.CJKCandidatesListView.OnWordSelectActionListener
    public void nextBtnPressed(CJKCandidatesListView aSource) {
        if (!this.gridCandidateTableVisible) {
            if (aSource instanceof VerCandidatesListView) {
                showCharacterCandidatesView();
                return;
            }
            if (this.mPopupCandidateListShowable) {
                log.d("showinline nextButtnPressed");
                this.gridCandidateTableVisible = true;
                showContextCandidatesView();
            }
            dismissPopupKeyboard();
        }
    }

    @Override // com.nuance.swype.input.chinese.CJKCandidatesListView.OnWordSelectActionListener
    public void prevBtnPressed(CJKCandidatesListView aSource) {
        if (!this.gridCandidateTableVisible) {
            if (aSource instanceof VerCandidatesListView) {
                showCharacterCandidatesView();
            } else {
                nextBtnPressed(null);
            }
        }
    }

    @Override // com.nuance.swype.input.chinese.CJKCandidatesListView.OnWordSelectActionListener
    public boolean isScrollable(CJKCandidatesListView aSource) {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.InputView, com.nuance.swype.input.KeyboardViewEx
    public boolean handleLongPress(KeyboardEx.Key key) {
        if (getOnKeyboardActionListener() == null || key.codes.length == 0) {
            return super.handleLongPress(key);
        }
        if (key.altCode == 43579 || key.altCode == 4085) {
            this.mHWContainer.clearCharacterList();
            this.mContextListViewContainer.clear();
        }
        return super.handleLongPress(key);
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
            InputMethods.InputMode inputMode = this.mCurrentInputLanguage.getCurrentInputMode();
            this.mKeyboardSwitcher.createKeyboardForTextInput(keyboardLayer, this.mInputFieldInfo, inputMode);
            if (this.mKeyboardSwitcher.isEditMode() || this.mKeyboardSwitcher.isNumMode() || this.mKeyboardSwitcher.isSymbolMode()) {
                this.mHWContainer.hideHWFrameAndCharacterList();
                hideChineseListAndShowFunctionBar();
            } else {
                this.mHWContainer.showHWFrameList();
                updateCandidatesList();
                this.mIme.refreshLanguageOnSpaceKey(getKeyboard(), this);
            }
            setShiftState(Shift.ShiftState.OFF);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.InputView, com.nuance.swype.input.KeyboardViewEx
    public boolean handleScrollDown() {
        if (this.mIme == null) {
            return false;
        }
        finishInput();
        this.mIme.requestHideSelf(0);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.KeyboardViewEx
    public boolean handleScrollUp() {
        if (IMEApplication.from(this.mIme).getIMEHandlerManager() == null) {
            return false;
        }
        this.mIme.handlerManager.toggleFullScreenHW();
        return true;
    }

    @Override // com.nuance.swype.input.InputView
    public boolean isLanguageSwitchableOnSpace() {
        return (this.mKeyboardSwitcher.isSymbolMode() || this.mKeyboardSwitcher.isNumMode()) ? false : true;
    }

    @Override // com.nuance.swype.input.InputView, com.nuance.swype.input.RemoveUdbWordDialog.RemoveUdbWordListener
    @SuppressLint({"PrivateResource"})
    public void onHandleUdbWordRemoval(String word) {
        if (this.mChineseInput != null) {
            boolean deleteStatus = this.mChineseInput.dlmDelete(word);
            if (this.mCurrentWritingPad != null) {
                if (deleteStatus) {
                    String removeMgs = "\"" + word + "\" " + getContext().getApplicationContext().getResources().getString(R.string.delete_success);
                    QuickToast.show(getContext(), removeMgs, 0, this.mCurrentWritingPad.getHeight() + this.candidatesListViewCJK.getHeight());
                    this.mComposition.clearCurrentInline();
                    if (this.mRecognitionCandidates != null) {
                        this.mRecognitionCandidates.clear();
                        this.mRecognitionCandidates = null;
                    }
                    this.mContextCandidates.clear();
                    this.mWordSourceList.clear();
                    updateCandidatesList();
                    this.mHWContainer.hideCandidatesGridView(this.candidatesPopup);
                    this.gridCandidateTableVisible = false;
                    return;
                }
                String removeMgs2 = "\"" + word + "\" " + getContext().getApplicationContext().getResources().getString(R.string.delete_failed);
                QuickToast.show(getContext(), removeMgs2, 0, this.mCurrentWritingPad.getHeight() + this.candidatesListViewCJK.getHeight());
            }
        }
    }

    @Override // com.nuance.swype.input.chinese.CJKCandidatesListView.OnWordSelectActionListener
    public void onCandidateLongPressed(int index, String word, int wdSource, CJKCandidatesListView aSource) {
        switch (wdSource) {
            case 5:
            case 6:
            case 14:
                showRemoveUdbWordDialog(word);
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
            case 10:
            case 11:
            case 12:
            case 13:
            default:
                return;
            case 9:
                ContactUtils.getContactNumberFromPhoneBook(getContext(), word, 9);
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
        return true;
    }

    @Override // com.nuance.swype.input.InputView, com.nuance.swype.input.CandidatesListView.CandidateListener
    public void onPressMoveCandidate(float xPos, float yPos, float xOffset) {
        log.d("onPressMoveCandidate()", " called ::  xPos:: " + xPos + ", yPos:: " + yPos + ", xOffset :: " + xOffset);
        super.touchMoveHandle(xPos, yPos, xOffset);
    }

    public void showHWFrameAndCharacterList() {
        this.mHWContainer.showHWFrameAndCharacterList();
    }

    public int getCandidateHeight() {
        if (this.mContextListViewContainer != null) {
            return this.mContextListViewContainer.getHeight();
        }
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.InputView
    public void setSwypeKeytoolTipSuggestion() {
        if (!UserPreferences.from(getContext()).getShowFunctionBar()) {
            if (this.mContextListViewContainer != null) {
                this.mContextListViewContainer.hideFunctionBarListView();
                this.mContextListViewContainer.showPhraseListView();
            }
            this.candidatesListViewCJK.showSwypeTooltip();
            syncCandidateDisplayStyleToMode();
            setCandidatesViewShown(true);
        }
    }

    @Override // com.nuance.swype.input.InputView
    public void setNotMatchToolTipSuggestion() {
        if (!UserPreferences.from(getContext()).getShowFunctionBar()) {
            if (this.mContextListViewContainer != null) {
                this.mContextListViewContainer.hideFunctionBarListView();
                this.mContextListViewContainer.showPhraseListView();
            }
            this.candidatesListViewCJK.showNotMatchTootip();
            syncCandidateDisplayStyleToMode();
            setCandidatesViewShown(true);
        }
    }

    public int getCharacterListWidth() {
        if (this.mHWContainer != null) {
            return this.mHWContainer.getCharacterListWidth();
        }
        return 0;
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

    @SuppressLint({"PrivateResource"})
    public void changeAltIconOfSwitchingLayout(boolean aFullScreen) {
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

    @Override // com.nuance.swype.input.InputView, com.nuance.swype.input.KeyboardViewEx
    public void setKeyboard(KeyboardEx keyboard) {
        updateDockModeForHandwriting(keyboard);
        super.setKeyboard(keyboard);
    }

    @Override // com.nuance.swype.input.InputView
    public boolean shouldDisableInput(int primaryCode) {
        return (isValidBuild() || primaryCode == 4087) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.InputView
    public boolean isNormalTextInputMode() {
        return this.mKeyboardSwitcher.isAlphabetMode();
    }

    @Override // com.nuance.swype.input.chinese.CJKCandidatesListView.OnWordSelectActionListener
    public void onScrollContentChanged() {
        if (this.mContextListViewContainer != null) {
            this.mContextListViewContainer.updateScrollArrowVisibility();
        }
    }

    @Override // com.nuance.swype.input.InputView
    public boolean isHandWritingInputView() {
        return true;
    }

    private CharSequence getPureCandidate(CharSequence candidate) {
        String cand = candidate.toString();
        int index = cand.lastIndexOf(36);
        return index <= 0 ? candidate : cand.substring(0, index);
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
                String s = new StringBuilder().append((Object) ic.getTextBeforeCursor(2000, 0)).toString();
                if (s.length() > 0) {
                    ic.setSelection(s.length() - 1, s.length() - 1);
                    return;
                }
                return;
            }
        }
    }
}
