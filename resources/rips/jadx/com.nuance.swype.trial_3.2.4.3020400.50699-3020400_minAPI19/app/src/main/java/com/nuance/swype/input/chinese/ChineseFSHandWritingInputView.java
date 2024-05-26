package com.nuance.swype.input.chinese;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import com.nuance.swype.input.view.InputContainerView;
import com.nuance.swype.util.CharacterUtilities;
import com.nuance.swype.util.ContactUtils;
import com.nuance.swype.util.LogManager;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressLint({"DefaultLocale"})
/* loaded from: classes.dex */
public class ChineseFSHandWritingInputView extends InputView implements T9WriteRecognizerListener.OnWriteRecognizerListener, HandWritingView.InSelectionAreaListener, HandWritingView.OnWritingAction, CJKCandidatesListView.OnWordSelectActionListener {
    private static final int END_SELECTION_GRID_CONTAINER = -1;
    public static final int EVENT_ON_CANDIDATELIST = 1;
    public static final int EVENT_ON_INITIAL = -1;
    public static final int EVENT_ON_KEYBOARD = 0;
    public static final int EVENT_ON_SPELLLIST = 2;
    private static final int GRID_NUM = 5;
    private static final int HIDE_IME = -2;
    public static final int HWR_123_MODE = 1;
    public static final int HWR_ABC_MODE = 2;
    public static final int HWR_CHN_MODE = 0;
    private static final int INPUT_MODE_PINYIN = 0;
    static final int MSG_DELAY_RECOGNIZER = 1;
    static final int MSG_DELAY_RECOGNIZER_ADD_STROKE = 2;
    private static final int MSG_DELAY_SHOWING_FULLSCREEN = 1;
    private static final int MSG_HANDLE_CHAR = 3;
    private static final int MSG_HANDLE_SUGGESTION_CANDIDATES = 2;
    private static final int MSG_HANDLE_TEXT = 4;
    static final int MSG_SHOW_HOW_TO_TOAST = 503;
    private View candidatesPopup;
    private List<CharSequence> candidatesPopupDataList;
    private ChineseKeyboardViewEx candidatesPopupKeyboardViewEx;
    private ScrollView candidatesPopupScrollView;
    private final Handler.Callback delayRecognizeCallback;
    private final Handler.Callback fsHandlerCallback;
    private boolean gridViewFunctionButtonPressed;
    private final Handler.Callback handlerCallback;
    private boolean isOnceAction;
    private List<Point> mCachedPoints;
    protected SpellPhraseHandWritingViewContainer mCharacterContextViewContainer;
    private XT9CoreChineseInput mChineseInput;
    protected Composition mComposition;
    protected ChineseFSHandWritingContainerView mContainer;
    private List<CharSequence> mContextCandidates;
    private ChineseHandWritingView mCurrentWritingPad;
    Handler mDelayRecognizeHandler;
    private final Handler mDelayShowingFullScreenHandler;
    private boolean mEditKeyboardOn;
    Handler mFSHandler;
    private FunctionBarListView mFunctionBarListView;
    private boolean mInputModeOn;
    protected boolean mIsIMEActive;
    protected int mKeyboardHeight;
    private boolean mLanguageOptionOn;
    private boolean mNumberKeyboardOn;
    private boolean mPopupCandidateListShowable;
    Handler mPopupViewHandler;
    private List<CharSequence> mRecognitionCandidates;
    protected List<ArrayList<KeyboardEx.GridKeyInfo>> mRows;
    private int mSelectionAreaOption;
    protected int mSelectionHeight;
    private boolean mSettingsOn;
    private boolean mShowFunctionBar;
    private boolean mShownFuntionBar;
    public String mStringHandWriting123Mode;
    public String mStringHandWritingABCMode;
    private int mWordListHeight;
    private T9WriteChinese mWriteChinese;
    private boolean mWritingOrRecognizing;
    Handler.Callback popupViewCallback;
    private SymbolInputController symInputController;
    private static final LogManager.Log log = LogManager.getLog("ChineseFSHandWritingInputView");
    private static int CHINESE_CATEGORY = 100;
    private static int ALPHA_CATEGORY = 101;
    private static int SYMBOL_CATEGORY = 102;

    public ChineseFSHandWritingInputView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChineseFSHandWritingInputView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mWordListHeight = 0;
        this.mContextCandidates = new ArrayList();
        this.mCachedPoints = new ArrayList();
        this.mPopupCandidateListShowable = true;
        this.mShownFuntionBar = false;
        this.mSelectionAreaOption = -1;
        this.mSelectionHeight = 0;
        this.mKeyboardHeight = 0;
        this.mRows = new ArrayList();
        this.mWritingOrRecognizing = false;
        this.handlerCallback = new Handler.Callback() { // from class: com.nuance.swype.input.chinese.ChineseFSHandWritingInputView.1
            @Override // android.os.Handler.Callback
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        ChineseFSHandWritingInputView.this.showDelayFullScreen();
                        return true;
                    default:
                        return true;
                }
            }
        };
        this.mDelayShowingFullScreenHandler = WeakReferenceHandler.create(this.handlerCallback);
        this.popupViewCallback = new Handler.Callback() { // from class: com.nuance.swype.input.chinese.ChineseFSHandWritingInputView.2
            @Override // android.os.Handler.Callback
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case 11:
                        if (ChineseFSHandWritingInputView.this.mSpeechWrapper != null && ChineseFSHandWritingInputView.this.mSpeechWrapper.isResumable()) {
                            ChineseFSHandWritingInputView.this.flushCurrentActiveWord();
                        }
                        ChineseFSHandWritingInputView.this.setSpeechViewHost();
                        ChineseFSHandWritingInputView.this.resumeSpeech();
                        return true;
                    case 503:
                        if (ChineseFSHandWritingInputView.this.mCurrentWritingPad != null && ChineseFSHandWritingInputView.this.mCurrentWritingPad.isShown()) {
                            ChineseFSHandWritingInputView.this.showHowToUseToast();
                            return true;
                        }
                        ChineseFSHandWritingInputView.this.mPopupViewHandler.sendEmptyMessageDelayed(503, 5L);
                        return true;
                    default:
                        return true;
                }
            }
        };
        this.mPopupViewHandler = WeakReferenceHandler.create(this.popupViewCallback);
        this.fsHandlerCallback = new Handler.Callback() { // from class: com.nuance.swype.input.chinese.ChineseFSHandWritingInputView.3
            /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
            /* JADX WARN: Code restructure failed: missing block: B:3:0x0009, code lost:            return true;     */
            @Override // android.os.Handler.Callback
            /*
                Code decompiled incorrectly, please refer to instructions dump.
                To view partially-correct code enable 'Show inconsistent code' option in preferences
            */
            public boolean handleMessage(android.os.Message r9) {
                /*
                    r8 = this;
                    r7 = 8
                    r4 = 0
                    r6 = 1
                    int r3 = r9.what
                    switch(r3) {
                        case 2: goto L22;
                        case 3: goto La;
                        case 4: goto L16;
                        case 15: goto Ldd;
                        default: goto L9;
                    }
                L9:
                    return r6
                La:
                    com.nuance.swype.input.chinese.ChineseFSHandWritingInputView r4 = com.nuance.swype.input.chinese.ChineseFSHandWritingInputView.this
                    java.lang.Object r3 = r9.obj
                    com.nuance.input.swypecorelib.T9WriteRecognizerListener$CharWriteEvent r3 = (com.nuance.input.swypecorelib.T9WriteRecognizerListener.CharWriteEvent) r3
                    char r3 = r3.mChar
                    com.nuance.swype.input.chinese.ChineseFSHandWritingInputView.access$300(r4, r3)
                    goto L9
                L16:
                    com.nuance.swype.input.chinese.ChineseFSHandWritingInputView r4 = com.nuance.swype.input.chinese.ChineseFSHandWritingInputView.this
                    java.lang.Object r3 = r9.obj
                    com.nuance.input.swypecorelib.T9WriteRecognizerListener$TextWriteEvent r3 = (com.nuance.input.swypecorelib.T9WriteRecognizerListener.TextWriteEvent) r3
                    java.lang.CharSequence r3 = r3.mText
                    com.nuance.swype.input.chinese.ChineseFSHandWritingInputView.access$400(r4, r3)
                    goto L9
                L22:
                    com.nuance.swype.input.chinese.ChineseFSHandWritingInputView r3 = com.nuance.swype.input.chinese.ChineseFSHandWritingInputView.this
                    com.nuance.swype.input.chinese.symbol.SymbolInputController r3 = com.nuance.swype.input.chinese.ChineseFSHandWritingInputView.access$500(r3)
                    if (r3 == 0) goto L36
                    com.nuance.swype.input.chinese.ChineseFSHandWritingInputView r3 = com.nuance.swype.input.chinese.ChineseFSHandWritingInputView.this
                    com.nuance.swype.input.chinese.symbol.SymbolInputController r3 = com.nuance.swype.input.chinese.ChineseFSHandWritingInputView.access$500(r3)
                    boolean r3 = r3.isActive()
                    if (r3 != 0) goto L9
                L36:
                    java.lang.Object r3 = r9.obj
                    if (r3 == 0) goto Lbb
                    com.nuance.swype.input.chinese.ChineseFSHandWritingInputView r3 = com.nuance.swype.input.chinese.ChineseFSHandWritingInputView.this
                    com.nuance.swype.input.chinese.ChineseHandWritingView r3 = com.nuance.swype.input.chinese.ChineseFSHandWritingInputView.access$100(r3)
                    if (r3 == 0) goto Lbb
                    com.nuance.swype.input.chinese.ChineseFSHandWritingInputView r5 = com.nuance.swype.input.chinese.ChineseFSHandWritingInputView.this
                    java.lang.Object r3 = r9.obj
                    com.nuance.input.swypecorelib.T9WriteRecognizerListener$CandidatesWriteEvent r3 = (com.nuance.input.swypecorelib.T9WriteRecognizerListener.CandidatesWriteEvent) r3
                    java.util.List<java.lang.CharSequence> r3 = r3.mChCandidates
                    com.nuance.swype.input.chinese.ChineseFSHandWritingInputView.access$602(r5, r3)
                    r1 = 0
                    com.nuance.swype.input.chinese.ChineseFSHandWritingInputView r3 = com.nuance.swype.input.chinese.ChineseFSHandWritingInputView.this
                    java.util.List r3 = com.nuance.swype.input.chinese.ChineseFSHandWritingInputView.access$600(r3)
                    if (r3 == 0) goto Ldb
                    com.nuance.swype.input.chinese.ChineseFSHandWritingInputView r3 = com.nuance.swype.input.chinese.ChineseFSHandWritingInputView.this
                    java.util.List r3 = com.nuance.swype.input.chinese.ChineseFSHandWritingInputView.access$600(r3)
                    int r3 = r3.size()
                L60:
                    if (r3 <= 0) goto L9a
                    com.nuance.swype.input.chinese.ChineseFSHandWritingInputView r3 = com.nuance.swype.input.chinese.ChineseFSHandWritingInputView.this
                    java.util.List r3 = com.nuance.swype.input.chinese.ChineseFSHandWritingInputView.access$600(r3)
                    java.lang.Object r2 = r3.get(r4)
                    java.lang.CharSequence r2 = (java.lang.CharSequence) r2
                    int r3 = r2.length()
                    if (r3 != r6) goto L9a
                    char r0 = r2.charAt(r4)
                    if (r0 == r7) goto L8a
                    r3 = 10
                    if (r0 == r3) goto L8a
                    r3 = 13
                    if (r0 == r3) goto L8a
                    r3 = 32
                    if (r0 == r3) goto L8a
                    r3 = 9
                    if (r0 != r3) goto L9a
                L8a:
                    com.nuance.swype.input.chinese.ChineseFSHandWritingInputView r3 = com.nuance.swype.input.chinese.ChineseFSHandWritingInputView.this
                    java.util.List r3 = com.nuance.swype.input.chinese.ChineseFSHandWritingInputView.access$600(r3)
                    r3.clear()
                    com.nuance.swype.input.chinese.ChineseFSHandWritingInputView r3 = com.nuance.swype.input.chinese.ChineseFSHandWritingInputView.this
                    r4 = 0
                    com.nuance.swype.input.chinese.ChineseFSHandWritingInputView.access$602(r3, r4)
                    r1 = r0
                L9a:
                    com.nuance.swype.input.chinese.ChineseFSHandWritingInputView r3 = com.nuance.swype.input.chinese.ChineseFSHandWritingInputView.this
                    com.nuance.swype.input.chinese.ChineseFSHandWritingInputView.access$700(r3)
                    if (r1 == 0) goto La6
                    com.nuance.swype.input.chinese.ChineseFSHandWritingInputView r3 = com.nuance.swype.input.chinese.ChineseFSHandWritingInputView.this
                    com.nuance.swype.input.chinese.ChineseFSHandWritingInputView.access$300(r3, r1)
                La6:
                    com.nuance.swype.input.chinese.ChineseFSHandWritingInputView r3 = com.nuance.swype.input.chinese.ChineseFSHandWritingInputView.this
                    java.util.List r3 = com.nuance.swype.input.chinese.ChineseFSHandWritingInputView.access$800(r3)
                    r3.clear()
                    if (r1 == r7) goto Lbb
                    com.nuance.swype.input.chinese.ChineseFSHandWritingInputView r3 = com.nuance.swype.input.chinese.ChineseFSHandWritingInputView.this
                    r3.showInline()
                    com.nuance.swype.input.chinese.ChineseFSHandWritingInputView r3 = com.nuance.swype.input.chinese.ChineseFSHandWritingInputView.this
                    com.nuance.swype.input.chinese.ChineseFSHandWritingInputView.access$900(r3)
                Lbb:
                    com.nuance.swype.input.chinese.ChineseFSHandWritingInputView r3 = com.nuance.swype.input.chinese.ChineseFSHandWritingInputView.this
                    java.util.List r3 = com.nuance.swype.input.chinese.ChineseFSHandWritingInputView.access$600(r3)
                    if (r3 == 0) goto Ld4
                    com.nuance.swype.input.chinese.ChineseFSHandWritingInputView r3 = com.nuance.swype.input.chinese.ChineseFSHandWritingInputView.this
                    java.util.List r3 = com.nuance.swype.input.chinese.ChineseFSHandWritingInputView.access$600(r3)
                    boolean r3 = r3.isEmpty()
                    if (r3 == 0) goto Ld4
                    com.nuance.swype.input.chinese.ChineseFSHandWritingInputView r3 = com.nuance.swype.input.chinese.ChineseFSHandWritingInputView.this
                    r3.setNotMatchToolTipSuggestion()
                Ld4:
                    com.nuance.swype.input.chinese.ChineseFSHandWritingInputView r3 = com.nuance.swype.input.chinese.ChineseFSHandWritingInputView.this
                    com.nuance.swype.input.chinese.ChineseFSHandWritingInputView.access$1002(r3, r6)
                    goto L9
                Ldb:
                    r3 = r4
                    goto L60
                Ldd:
                    com.nuance.swype.input.chinese.ChineseFSHandWritingInputView r3 = com.nuance.swype.input.chinese.ChineseFSHandWritingInputView.this
                    com.nuance.input.swypecorelib.XT9CoreChineseInput r3 = com.nuance.swype.input.chinese.ChineseFSHandWritingInputView.access$1100(r3)
                    r3.enableTrace(r6)
                    com.nuance.swype.input.chinese.ChineseFSHandWritingInputView r3 = com.nuance.swype.input.chinese.ChineseFSHandWritingInputView.this
                    com.nuance.swype.input.chinese.ChineseFSHandWritingInputView.access$1200(r3)
                    goto L9
                */
                throw new UnsupportedOperationException("Method not decompiled: com.nuance.swype.input.chinese.ChineseFSHandWritingInputView.AnonymousClass3.handleMessage(android.os.Message):boolean");
            }
        };
        this.mFSHandler = WeakReferenceHandler.create(this.fsHandlerCallback);
        this.delayRecognizeCallback = new Handler.Callback() { // from class: com.nuance.swype.input.chinese.ChineseFSHandWritingInputView.4
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
                    com.nuance.swype.input.chinese.ChineseFSHandWritingInputView r0 = com.nuance.swype.input.chinese.ChineseFSHandWritingInputView.this
                    com.nuance.swype.input.chinese.ChineseFSHandWritingInputView.access$1300(r0)
                    com.nuance.swype.input.chinese.ChineseFSHandWritingInputView r0 = com.nuance.swype.input.chinese.ChineseFSHandWritingInputView.this
                    com.nuance.swype.input.chinese.ChineseHandWritingView r0 = com.nuance.swype.input.chinese.ChineseFSHandWritingInputView.access$100(r0)
                    if (r0 == 0) goto L6
                    com.nuance.swype.input.chinese.ChineseFSHandWritingInputView r0 = com.nuance.swype.input.chinese.ChineseFSHandWritingInputView.this
                    com.nuance.swype.input.chinese.ChineseHandWritingView r0 = com.nuance.swype.input.chinese.ChineseFSHandWritingInputView.access$100(r0)
                    r0.setNewSession(r1)
                    goto L6
                L1e:
                    com.nuance.swype.input.chinese.ChineseFSHandWritingInputView r0 = com.nuance.swype.input.chinese.ChineseFSHandWritingInputView.this
                    com.nuance.swype.input.chinese.ChineseFSHandWritingInputView.access$1400(r0)
                    goto L6
                */
                throw new UnsupportedOperationException("Method not decompiled: com.nuance.swype.input.chinese.ChineseFSHandWritingInputView.AnonymousClass4.handleMessage(android.os.Message):boolean");
            }
        };
        this.mDelayRecognizeHandler = WeakReferenceHandler.create(this.delayRecognizeCallback);
        this.mComposition = new Composition();
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
            this.mIsIMEActive = true;
            this.mSelectionAreaOption = -1;
            this.mPopupCandidateListShowable = true;
            super.startInput(inputFieldInfo, restarting);
            this.mCompletionOn = false;
            cancelCurrentDefaultWord();
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
            this.mChineseInput.setMSDB(this.mCurrentInputLanguage.getCoreLanguageId(), UserPreferences.from(this.mIme).isEmojiSuggestionsEnabled());
            this.mChineseInput.setInputMode(0);
            this.mChineseInput.setAttribute(101, this.mFuzzyPinyin);
            this.mChineseInput.clearCommonChar();
            this.mWriteChinese.removeAllRecognizeListener();
            this.mWriteChinese.addRecognizeListener(this);
            this.mComposition.setInputConnection(getCurrentInputConnection());
            this.mIme.getInputMethods().addTextCategory(this.mWriteChinese, this.mCurrentInputLanguage);
            this.mWriteChinese.addGestureCategory();
            this.mWriteChinese.startSession(this.mCurrentInputLanguage.getCoreLanguageId());
            this.mContextCandidates.clear();
            if (this.mRecognitionCandidates != null) {
                this.mRecognitionCandidates.clear();
            }
            if (this.mContainer != null) {
                this.mContainer.hidePopupCandidatesView();
            }
            this.gridCandidateTableVisible = false;
            setKeyboardForTextEntry(inputFieldInfo);
            this.mKeyboardSwitcher.setShiftState(Shift.ShiftState.OFF);
            setHWFrame();
            postDelayShowingFullScreenMsg();
            Resources res = getResources();
            if (this.mCurrentInputLanguage.getCoreLanguageId() == 225) {
                this.mStringHandWriting123Mode = res.getText(R.string.handwriting_123_mode_simp).toString();
            } else if (this.mCurrentInputLanguage.getCoreLanguageId() == 224) {
                this.mStringHandWriting123Mode = res.getText(R.string.handwriting_123_mode_trad).toString();
            } else if (this.mCurrentInputLanguage.getCoreLanguageId() == 226) {
                this.mStringHandWriting123Mode = res.getText(R.string.handwriting_123_mode_trad).toString();
            }
            this.mStringHandWritingABCMode = res.getText(R.string.handwriting_abc_mode).toString();
            int delayMS = InputPrefs.getAutoDelay(UserPreferences.from(getContext()), UserPreferences.HWR_AUTO_ACCEPT_RECOGNIZE, res.getInteger(R.integer.handwriting_auto_recognize_default_ms));
            this.mWriteChinese.setRecognizerDelay(delayMS);
            clearArcs();
            if (this.mCharacterContextViewContainer != null) {
                this.mCharacterContextViewContainer.hideSpellPrefixSuffixList();
            }
            updateCandidatesList();
            this.mSelectionHeight = getResources().getDimensionPixelSize(R.dimen.spell_list_height) + getResources().getDimensionPixelSize(R.dimen.candidates_list_height);
            this.mKeyboardHeight = this.mSelectionHeight + getResources().getDimensionPixelSize(R.dimen.key_height);
            removeHowToUseToastMsg();
            postHowToUseToastMsg();
            postDelayResumeSpeech();
            if (this.mWriteChinese != null) {
                this.mWriteChinese.setWidth(this.mCurrentWritingPad.getWidth());
                this.mWriteChinese.setHeight(this.mCurrentWritingPad.getHeight());
                if (UserPreferences.from(getContext()).isHwrScrmodeEnabled()) {
                    this.mWriteChinese.setRecognitionMode(0);
                } else {
                    this.mWriteChinese.setRecognitionMode(1);
                    if (getContext().getResources().getConfiguration().orientation == 1) {
                        this.mWriteChinese.setWritingDirection(3);
                    } else {
                        this.mWriteChinese.setWritingDirection(0);
                    }
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
                this.mCharacterContextViewContainer.hideFunctionBarListView();
                this.mCharacterContextViewContainer.hideSpellPrefixSuffixList();
                this.mCharacterContextViewContainer.showPhraseListView();
            }
            this.mFSHandler.removeMessages(15);
            this.mFSHandler.sendMessageDelayed(this.mFSHandler.obtainMessage(15, inputFieldInfo.isNameField() ? 1 : 0, this.isKeepingKeyboard ? 1 : 0), 5L);
            showTrialExpireWclMessage("CJK");
            this.keyboardTouchEventDispatcher.resisterTouchKeyHandler(new DefaultHWRTouchKeyHandler(this.mIme, this, this.xt9coreinput, getDefaultKeyUIStateHandler()));
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
        if (appPrefs.getBoolean("show_how_to_toggle_half_screen_mode_chn", true)) {
            appPrefs.setBoolean("show_how_to_toggle_half_screen_mode_chn", false);
            QuickToast.show(getContext(), getResources().getString(R.string.how_to_toggle_half_screen_mode), 1, (getHeight() + this.mCurrentWritingPad.getHeight()) / 2);
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

    @SuppressLint({"PrivateResource"})
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
        if (getKeyboard() != null) {
            List<KeyboardEx.Key> keys = getKeyboard().getKeys();
            for (int i = 0; i < keys.size(); i++) {
                KeyboardEx.Key key = keys.get(i);
                if (key.altCode == 43579) {
                    if (key.altIcon != null) {
                        IMEApplication app = IMEApplication.from(getContext());
                        key.altIcon = app.getThemedDrawable(R.attr.cjkAltIconMinHwCommaKey);
                        key.altPreviewIcon = app.getThemedDrawable(R.attr.cjkAltPreMinHwCommaKey);
                        invalidateKey(key);
                        return;
                    }
                    return;
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
    public void finishInput() {
        super.finishInput();
        removeHowToUseToastMsg();
        hideHowToUseToast();
        this.mWritingOrRecognizing = false;
        this.isOnceAction = false;
        this.mIsIMEActive = false;
        acceptCurrentDefaultWord();
        dismissPopupKeyboard();
        hideFullScreenHandWritingView(true);
        this.mFunctionBarListView.recycleBitmap();
        this.mCharacterContextViewContainer.clear();
        this.mDelayShowingFullScreenHandler.removeMessages(1);
        resetWrite();
        this.mWriteChinese.removeRecognizeListener(this);
        this.mWriteChinese.finishSession();
        if (this.mRecognitionCandidates != null) {
            this.mRecognitionCandidates.clear();
            this.mRecognitionCandidates = null;
        }
        this.mContextCandidates.clear();
        if (this.mContainer != null) {
            this.mContainer.hidePopupCandidatesView();
        }
        this.gridCandidateTableVisible = false;
        if (this.mChineseInput != null) {
            dimissRemoveUdbWordDialog();
            this.mChineseInput.finishSession();
            this.keyboardTouchEventDispatcher.unwrapTouchEvent(this);
            this.xt9coreinput.setInputContextRequestListener(null);
        }
    }

    @Override // com.nuance.swype.input.InputView
    public void handleClose() {
        this.mIsIMEActive = false;
        hideFullScreenHandWritingView(true);
        this.mDelayShowingFullScreenHandler.removeMessages(1);
        resetWrite();
        this.mFunctionBarListView.recycleBitmap();
        dimissRemoveUdbWordDialog();
        super.handleClose();
    }

    @Override // com.nuance.swype.input.InputView
    public boolean handleKeyDown(int keyCode, KeyEvent event) {
        return this.mComposition != null && this.mComposition.length() > 0 && (keyCode == 21 || keyCode == 22 || keyCode == 19 || keyCode == 20);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.InputView, com.nuance.swype.input.KeyboardViewEx
    public boolean handleLongPress(KeyboardEx.Key key) {
        if (getOnKeyboardActionListener() == null || key.codes.length == 0) {
            return super.handleLongPress(key);
        }
        if (key.altCode == 43579 || key.altCode == 4085) {
            setCandidatesViewShown(true);
            this.mCharacterContextViewContainer.hideSpellPrefixSuffixList();
            this.mCharacterContextViewContainer.clear();
        }
        return super.handleLongPress(key);
    }

    @Override // com.nuance.swype.input.InputView
    public boolean handleKey(int primaryCode, boolean quickPressed, int repeatedCount) {
        hideHowToUseToast();
        switch (primaryCode) {
            case KeyboardEx.KEYCODE_KEYBOARD_RESIZE /* 2900 */:
                if (!isNormalTextInputMode()) {
                    hideFullScreenHandWritingView(true);
                } else {
                    showFullScreenHandWritingView();
                }
                if (this.gridCandidateTableVisible && this.mContainer != null) {
                    this.mContainer.hidePopupCandidatesView();
                }
                this.gridCandidateTableVisible = false;
                this.candidatesPopup = null;
                acceptCurrentActiveWord();
                this.mContextCandidates.clear();
                clearLinePath();
                this.mWordSourceList.clear();
                if (this.mShowFunctionBar) {
                    this.mCharacterContextViewContainer.clear();
                    this.mCharacterContextViewContainer.hidePhraseListView();
                    this.mCharacterContextViewContainer.showFunctionBarListView();
                }
                return super.handleKey(primaryCode, quickPressed, repeatedCount);
            case KeyboardEx.KEYCODE_LANGUAGE_QUICK_SWITCH /* 2939 */:
                this.mCharacterContextViewContainer.clear();
                return super.handleKey(primaryCode, quickPressed, repeatedCount);
            case KeyboardEx.KEYCODE_SPEECH /* 6463 */:
                if (quickPressed) {
                    return true;
                }
                acceptCurrentActiveWord();
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
    public void handlePossibleActionAfterResize() {
        InputContainerView containerView;
        if (this.mIme != null && (containerView = this.mIme.getInputContainerView()) != null && (isMiniKeyboardMode() || isDockMiniKeyboardMode())) {
            containerView.requestLayout();
            this.isOnceAction = true;
        }
        hideFullScreenHandWritingView(false);
        showFullScreenHandWritingView();
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
            if (text2.length() > 1 && !charUtil.isPunctuationOrSymbol(text2.charAt(0)) && !charUtil.isPunctuationOrSymbol(text2.charAt(1))) {
                text2 = text2.subSequence(0, 1);
            }
            this.mWriteChinese.queueText(text2);
            return;
        }
        this.mWriteChinese.queueText(text);
    }

    private void addDigitOnlyCategory() {
        if (this.mCurrentInputLanguage != null) {
            this.mWriteChinese.addNumberOnlyCategory();
            this.mWriteChinese.addGestureCategory();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.InputView
    public void setCandidatesViewShown(boolean shown) {
        super.setCandidatesViewShown(shown);
        if (this.mCharacterContextViewContainer != null) {
            if (shown) {
                this.mCharacterContextViewContainer.setVisibility(0);
            } else {
                this.mCharacterContextViewContainer.setVisibility(8);
            }
        }
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
        InputConnection ic;
        if (isPendingRecognizeMessage()) {
            processPendingRecognizing();
            if (this.mWriteChinese != null) {
                this.mWriteChinese.queueChar(' ');
            }
        } else if (this.mRecognitionCandidates != null && !this.mRecognitionCandidates.isEmpty()) {
            if (this.mWriteChinese != null) {
                acceptCurrentDefaultWord();
                updateCandidatesList();
            }
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
        if (this.mRecognitionCandidates != null && !this.mRecognitionCandidates.isEmpty()) {
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
        ContactUtils.cancelQueryTask();
        acceptCurrentActiveWord();
        dimissRemoveUdbWordDialog();
        hideFullScreenHandWritingView(true);
        clearLinePath();
    }

    private void acceptCurrentActiveWord() {
        if (isValidBuild()) {
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
            if (this.candidatesListViewCJK != null) {
                this.candidatesListViewCJK.clear();
            }
            clearArcs();
        }
    }

    private boolean cancelCurrentDefaultWord() {
        if (this.mComposition == null || this.candidatesListViewCJK == null || this.mComposition.length() <= 0) {
            return false;
        }
        this.mComposition.clearCurrentInline();
        if (this.mRecognitionCandidates != null) {
            this.mRecognitionCandidates.clear();
        }
        CJKCandidatesListView cJKCandidatesListView = this.candidatesListViewCJK;
        this.mRecognitionCandidates = null;
        cJKCandidatesListView.setSuggestions(null, 0);
        return true;
    }

    private void hideChineseListAndShowFunctionBar() {
        if (this.candidatesListViewCJK != null) {
            this.candidatesListViewCJK.clear();
        }
        if (this.mCharacterContextViewContainer != null) {
            this.mCharacterContextViewContainer.clear();
            this.mCharacterContextViewContainer.hidePhraseListView();
            this.mCharacterContextViewContainer.showFunctionBarListView();
            this.mShownFuntionBar = true;
            syncCandidateDisplayStyleToMode();
        }
    }

    private boolean acceptCurrentDefaultWord() {
        if (this.mRecognitionCandidates != null && !this.mRecognitionCandidates.isEmpty()) {
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
        acceptCurrentActiveWord();
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
        if (this.mRecognitionCandidates != null && n >= 0 && n < this.mRecognitionCandidates.size()) {
            this.mComposition.showInline(this.mRecognitionCandidates.get(n));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateCandidatesList() {
        if (!this.mIsIMEActive) {
            if (this.mRecognitionCandidates != null) {
                this.mRecognitionCandidates.clear();
                this.mRecognitionCandidates = null;
                return;
            }
            return;
        }
        if (this.mWriteChinese != null && isNormalTextInputMode() && !this.gridCandidateTableVisible) {
            setCandidatesViewShown(true);
            if (isValidBuild()) {
                if (this.mComposition != null && this.mComposition.length() != 0 && this.mRecognitionCandidates != null && !this.mRecognitionCandidates.isEmpty()) {
                    this.mWordSourceList.clear();
                    for (int i = 0; i < this.mRecognitionCandidates.size(); i++) {
                        this.mWordSourceList.add(this.mWordSourceList.size(), new AtomicInteger(0));
                    }
                    if (this.candidatesListViewCJK != null) {
                        this.candidatesListViewCJK.setSuggestions(getPureCandidates(this.mRecognitionCandidates), 0, this.mWordSourceList);
                    }
                } else {
                    if (this.mCharacterContextViewContainer != null) {
                        this.mCharacterContextViewContainer.clearSpellPrefixSuffixListView();
                    }
                    if (this.candidatesListViewCJK != null && this.candidatesListViewCJK.isCandidateListEmpty() && this.mFunctionBarListView != null && !this.mFunctionBarListView.isFunctionBarDisabledOrZeroItem()) {
                        this.mWriteChinese.setContext("");
                        this.mContextCandidates.clear();
                        this.mWordSourceList.clear();
                    } else {
                        if (this.mComposition != null) {
                            this.mWriteChinese.setContext(this.mComposition.getTextBeforeCursor(2, 0));
                        }
                        getContextCandidates();
                        if (this.candidatesListViewCJK != null && this.mContextCandidates != null && this.mContextCandidates.size() > 0) {
                            for (int i2 = 0; i2 < this.mWordSourceList.size(); i2++) {
                                if (this.mWordSourceList.get(i2).get() != 7) {
                                    this.mWordSourceList.set(i2, new AtomicInteger(0));
                                }
                            }
                            this.candidatesListViewCJK.setSuggestions(this.mContextCandidates, 0, this.mWordSourceList);
                        }
                    }
                }
                if (this.mCharacterContextViewContainer != null) {
                    this.mCharacterContextViewContainer.hideSpellPrefixSuffixList();
                }
                if ((this.mRecognitionCandidates != null || this.mContextCandidates.size() > 0) && this.mInputFieldInfo != null) {
                    this.mFunctionBarListView.clear();
                    if (this.mCharacterContextViewContainer != null) {
                        this.mCharacterContextViewContainer.hideFunctionBarListView();
                        this.mCharacterContextViewContainer.showPhraseListView();
                    }
                } else {
                    if (this.candidatesListViewCJK != null) {
                        this.candidatesListViewCJK.clear();
                    }
                    if (this.mCharacterContextViewContainer != null) {
                        this.mCharacterContextViewContainer.clear();
                        this.mCharacterContextViewContainer.hidePhraseListView();
                        this.mCharacterContextViewContainer.hideSpellPrefixSuffixList();
                        this.mShowFunctionBar = true;
                        this.mCharacterContextViewContainer.showFunctionBarListView();
                    }
                }
                syncCandidateDisplayStyleToMode();
            }
        }
    }

    public void showInline() {
        if (this.mRecognitionCandidates != null && !this.mRecognitionCandidates.isEmpty()) {
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
            this.mCurrentWritingPad.setFullScreen(true);
            if (this.mCurrentInputLanguage != null) {
                this.mCurrentWritingPad.setIntegratedEnabled(isIntegratedHandwriting());
            }
        }
        clearArcs();
    }

    @Override // com.nuance.swype.input.HandWritingView.OnWritingAction
    public void penDown(View src) {
        if (this.mCurrentWritingPad != null) {
            this.mCurrentWritingPad.mFaddingStrokeQueue.startActionFadingPenDown();
        }
        dismissPopupKeyboard();
        removeDelayRecognitionMsg();
        removeDelayRecognitionStroke();
        if (src != null && this.mCurrentWritingPad != src && (src instanceof ChineseHandWritingView)) {
            this.mCurrentWritingPad = (ChineseHandWritingView) src;
            if (this.mWriteChinese != null && !isIntegratedHandwriting()) {
                this.mWriteChinese.setWidth(this.mCurrentWritingPad.getWidth());
                this.mWriteChinese.setHeight(this.mCurrentWritingPad.getHeight());
                this.mWriteChinese.applyChangedSettings();
            }
        }
        if (!this.mWritingOrRecognizing) {
            acceptCurrentDefaultWord();
            if (isNormalTextInputMode()) {
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
            this.mCurrentWritingPad.mFaddingStrokeQueue.startFading();
            this.mCurrentWritingPad.mFaddingStrokeQueue.startActionFading();
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

    private void showContextPopupListView() {
        if (this.mContextCandidates != null && !this.mContextCandidates.isEmpty()) {
            showPopupCandidatesView(this.mContextCandidates, this.mWordSourceList);
        }
    }

    private void showCharacterListView() {
        if (this.mRecognitionCandidates != null && !this.mRecognitionCandidates.isEmpty()) {
            showPopupCandidatesView(this.mRecognitionCandidates);
        }
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
            if (this.mCurrentWritingPad != null) {
                this.mCurrentWritingPad.mFaddingStrokeQueue.stopFading();
                this.mCurrentWritingPad.setNewSession(true);
            }
        }
        this.mRecognitionCandidates = null;
        this.mContextCandidates.clear();
        updateCandidatesList();
        if (selected_emoji != null) {
            super.addEmojiInRecentCat(selected_emoji, word.toString());
        }
    }

    @Override // com.nuance.swype.input.InputView
    public void updateSelection(int oldSelStart, int oldSelEnd, int newSelStart, int newSelEnd, int[] candidatesIndices) {
        boolean cursorChanged = true;
        super.updateSelection(oldSelStart, oldSelEnd, newSelStart, newSelEnd, candidatesIndices);
        if (((newSelStart == candidatesIndices[1] && newSelEnd == candidatesIndices[1]) || candidatesIndices[1] == -1) && (candidatesIndices[1] != -1 || (newSelStart == oldSelStart && newSelEnd == oldSelEnd))) {
            cursorChanged = false;
        }
        SpeechWrapper spw = IMEApplication.from(getContext()).getSpeechWrapper();
        if ((this.mRecognitionCandidates == null || this.mRecognitionCandidates.isEmpty()) && !cursorChanged) {
            if (spw == null || !spw.isStreamingDictation()) {
                if (this.mComposition != null && this.mComposition.length() > 0) {
                    this.mComposition.acceptCurrentInline();
                } else {
                    InputConnection ic = getCurrentInputConnection();
                    if (ic != null) {
                        ic.finishComposingText();
                    }
                }
            }
        } else if (this.mComposition != null && cursorChanged && (spw == null || !spw.isStreamingDictation())) {
            if (this.mComposition.length() > 0) {
                this.mComposition.acceptCurrentInline();
                this.mContextCandidates.clear();
            } else {
                InputConnection ic2 = getCurrentInputConnection();
                if (ic2 != null) {
                    ic2.finishComposingText();
                }
            }
            clearArcs();
            this.mRecognitionCandidates = null;
        }
        dismissPopupKeyboard();
        if (this.mKeyboardSwitcher != null && this.mContainer != null && !this.mKeyboardSwitcher.isSymbolMode() && !this.mKeyboardSwitcher.isNumMode() && !this.mKeyboardSwitcher.isEditMode() && this.gridCandidateTableVisible) {
            closeGridCanditesPopup();
        }
        if ((spw == null || !spw.isStreamingDictation()) && isNormalTextInputMode() && cursorChanged) {
            showHandWritingView();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void closeGridCanditesPopup() {
        InputContainerView containerView;
        this.mContainer.hidePopupCandidatesView();
        this.gridCandidateTableVisible = false;
        if (this.mCharacterContextViewContainer != null) {
            this.mCharacterContextViewContainer.showPhraseListView();
        }
        setCandidatesViewShown(true);
        if (this.mIme != null && (containerView = this.mIme.getInputContainerView()) != null && (isMiniKeyboardMode() || isDockMiniKeyboardMode())) {
            containerView.requestLayout();
            this.isOnceAction = true;
        }
        this.mSelectionAreaOption = -1;
        if (this.mCurrentWritingPad != null) {
            this.mCurrentWritingPad.setPointStatus(-1);
        }
    }

    @Override // com.nuance.input.swypecorelib.T9WriteRecognizerListener.OnWriteRecognizerListener
    public void onHandleWriteEvent(T9WriteRecognizerListener.WriteEvent event) {
        if (this.mIme == null || isValidBuild()) {
            switch (event.mType) {
                case 1:
                    this.mFSHandler.sendMessageDelayed(this.mFSHandler.obtainMessage(2, event), 5L);
                    return;
                case 2:
                    this.mFSHandler.sendMessageDelayed(this.mFSHandler.obtainMessage(3, event), 5L);
                    return;
                case 3:
                    this.mFSHandler.sendMessageDelayed(this.mFSHandler.obtainMessage(4, event), 5L);
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
        if (ch != '\b' && isNormalTextInputMode()) {
            updateCandidatesList();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.InputView
    public void removeAllMessages() {
        this.mFSHandler.removeMessages(2);
        this.mFSHandler.removeMessages(3);
        this.mFSHandler.removeMessages(4);
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
            int i = 0;
            while (i < this.mCachedPoints.size()) {
                if (this.mCachedPoints.get(i).x == 0 && this.mCachedPoints.get(i).y == 0) {
                    int end = i > 0 ? i - 1 : 0;
                    if (this.mWriteChinese != null && start >= 0 && start < end) {
                        this.mWriteChinese.queueAddArcs(this.mCachedPoints.subList(start, end), null, null);
                    }
                    start = i + 1;
                }
                i++;
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
        if (this.mCurrentInputLanguage != null) {
            if (!isIntegratedHandwriting()) {
                int writingPadWidth = this.mCurrentWritingPad.getWidth();
                if (this.mCachedPoints.get(0).x < writingPadWidth / 2) {
                    category = ALPHA_CATEGORY;
                } else {
                    category = SYMBOL_CATEGORY;
                }
                if (category != CHINESE_CATEGORY) {
                    for (int i = 1; i < this.mCachedPoints.size(); i++) {
                        Point pt = this.mCachedPoints.get(i);
                        if ((pt.x != 0 || pt.y != 0) && ((pt.x > writingPadWidth / 2 && category == ALPHA_CATEGORY) || (pt.x < writingPadWidth / 2 && category == SYMBOL_CATEGORY))) {
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
            if (this.mWriteChinese != null) {
                if (!isIntegratedHandwriting()) {
                    this.mWriteChinese.startArcsAddingSequence();
                    int start = 0;
                    int i2 = 0;
                    while (i2 < this.mCachedPoints.size()) {
                        if (this.mCachedPoints.get(i2).x == 0 && this.mCachedPoints.get(i2).y == 0) {
                            int end = i2 > 0 ? i2 - 1 : 0;
                            if (start >= 0 && start < end) {
                                this.mWriteChinese.queueAddArcs(this.mCachedPoints.subList(start, end), null, null);
                            }
                            start = i2 + 1;
                        }
                        i2++;
                    }
                    this.mWriteChinese.queueRecognition(null);
                }
                this.mWriteChinese.endArcsAddingSequence();
            }
        }
        this.mWritingOrRecognizing = false;
        this.mCurrentWritingPad.mFaddingStrokeQueue.stopFading();
        this.mCachedPoints.clear();
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

    private void showPopupCandidatesView(List<CharSequence> aDataList) {
        showPopupCandidatesView(aDataList, null);
    }

    @SuppressLint({"InflateParams", "PrivateResource"})
    private void showPopupCandidatesView(List<CharSequence> aDataList, List<AtomicInteger> aWordSourceList) {
        if (aDataList != null && !aDataList.isEmpty()) {
            this.candidatesPopupDataList = getPureCandidates(aDataList);
            hideFullScreenHandWritingView(false);
            int height = this.mContainer.getHeight() * 5;
            int width = this.mContainer.getWidth();
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
                closeButton.setOnClickListener(new View.OnClickListener() { // from class: com.nuance.swype.input.chinese.ChineseFSHandWritingInputView.5
                    @Override // android.view.View.OnClickListener
                    public void onClick(View v) {
                        ChineseFSHandWritingInputView.this.closeGridCanditesPopup();
                        ChineseFSHandWritingInputView.this.showFullScreenHandWritingView();
                    }
                });
                closeButton.setOnTouchListener(new View.OnTouchListener() { // from class: com.nuance.swype.input.chinese.ChineseFSHandWritingInputView.6
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
                            com.nuance.swype.input.chinese.ChineseFSHandWritingInputView r0 = com.nuance.swype.input.chinese.ChineseFSHandWritingInputView.this
                            r1 = 1
                            com.nuance.swype.input.chinese.ChineseFSHandWritingInputView.access$1702(r0, r1)
                            goto L8
                        L10:
                            com.nuance.swype.input.chinese.ChineseFSHandWritingInputView r0 = com.nuance.swype.input.chinese.ChineseFSHandWritingInputView.this
                            com.nuance.swype.input.chinese.ChineseFSHandWritingInputView.access$1702(r0, r2)
                            goto L8
                        */
                        throw new UnsupportedOperationException("Method not decompiled: com.nuance.swype.input.chinese.ChineseFSHandWritingInputView.AnonymousClass6.onTouch(android.view.View, android.view.MotionEvent):boolean");
                    }
                });
                this.candidatesPopupScrollView = (ScrollView) this.candidatesPopup.findViewById(R.id.scroll_view);
                this.candidatesPopupKeyboardViewEx = (ChineseKeyboardViewEx) this.candidatesPopup.findViewById(R.id.keyboardViewEx);
            }
            this.candidatesPopupScrollView.scrollTo(0, 0);
            this.candidatesPopupKeyboardViewEx.setInputView(this);
            this.candidatesPopupKeyboardViewEx.setWordSource(aWordSourceList);
            this.candidatesPopupKeyboardViewEx.setGridCandidates(this.mRows, this.candidatesPopupDataList, this.candidatesPopupScrollView.getMeasuredWidth());
            final KeyboardEx keyboard = new KeyboardEx(getContext(), R.xml.kbd_chinese_popup_template, this.mRows, this.candidatesPopupScrollView.getMeasuredWidth(), height, getKeyboard().getKeyboardDockMode());
            this.candidatesPopupKeyboardViewEx.setKeyboard(keyboard);
            this.candidatesPopupKeyboardViewEx.setIme(this.mIme);
            this.gridCandidateTableVisible = true;
            this.candidatesPopupKeyboardViewEx.setOnKeyboardActionListener(new KeyboardViewEx.KeyboardActionAdapter() { // from class: com.nuance.swype.input.chinese.ChineseFSHandWritingInputView.7
                @Override // com.nuance.swype.input.KeyboardViewEx.OnKeyboardActionListener
                public void onText(CharSequence text, long eventTime) {
                    if (!ChineseFSHandWritingInputView.this.gridViewFunctionButtonPressed) {
                        ChineseFSHandWritingInputView.this.closeGridCanditesPopup();
                        ChineseFSHandWritingInputView.this.showFullScreenHandWritingView();
                        for (int i = 0; i < ChineseFSHandWritingInputView.this.candidatesPopupDataList.size(); i++) {
                            if (ChineseFSHandWritingInputView.this.getPureCandidate((CharSequence) ChineseFSHandWritingInputView.this.candidatesPopupDataList.get(i)).toString().equals(text.toString())) {
                                ChineseFSHandWritingInputView.this.selectWord(i, text, ChineseFSHandWritingInputView.this.candidatesListViewCJK);
                            }
                        }
                    }
                }

                @Override // com.nuance.swype.input.KeyboardViewEx.KeyboardActionAdapter, com.nuance.swype.input.KeyboardViewEx.OnKeyboardActionListener
                public void onRelease(int primaryCode) {
                    keyboard.clearStickyKeys();
                }
            });
            FrameLayout.LayoutParams lp2 = new FrameLayout.LayoutParams(width, height);
            this.candidatesPopup.setLayoutParams(lp2);
            this.mContainer.showPopupCandidatesView(this.candidatesPopup);
            setCandidatesViewShown(false);
        }
    }

    @Override // com.nuance.swype.input.InputView
    @SuppressLint({"InflateParams", "PrivateResource"})
    public View createCandidatesView(CandidatesListView.CandidateListener onSelectListener) {
        if (this.mChineseInput == null) {
            return null;
        }
        if (this.mCharacterContextViewContainer == null) {
            LayoutInflater inflater = IMEApplication.from(this.mIme).getThemedLayoutInflater(this.mIme.getLayoutInflater());
            IMEApplication.from(this.mIme).getThemeLoader().setLayoutInflaterFactory(inflater);
            this.mCharacterContextViewContainer = (SpellPhraseHandWritingViewContainer) inflater.inflate(R.layout.chinese_handwriting_candidates, (ViewGroup) null);
            IMEApplication.from(this.mIme).getThemeLoader().applyTheme(this.mCharacterContextViewContainer);
            this.mCharacterContextViewContainer.initViews();
            this.candidatesListViewCJK = this.mCharacterContextViewContainer.getCJKCandidatesListView();
            this.mFunctionBarListView = (FunctionBarListView) this.mCharacterContextViewContainer.findViewById(R.id.functionbar);
            if (!isValidBuild()) {
                this.mCharacterContextViewContainer.showLeftArrow(false);
            }
        }
        this.candidatesListViewCJK.setOnWordSelectActionListener(this);
        this.wordListViewContainerCJK = this.mCharacterContextViewContainer;
        this.mFunctionBarListView.setOnFunctionBarListener(new DefaultChineseFunctionBarHandler(this.mIme, this));
        return this.mCharacterContextViewContainer;
    }

    @Override // com.nuance.swype.input.InputView
    public View getWordCandidateListContainer() {
        return this.mCharacterContextViewContainer;
    }

    public void setContainerView(ChineseFSHandWritingContainerView container) {
        this.mContainer = container;
    }

    public void setHWFrame() {
        this.mContainer.setFullScreenHandWritingFrame();
    }

    public void showHandWritingView() {
        if (this.mSpeechWrapper == null || !this.mSpeechWrapper.isSpeechViewShowing()) {
            if (this.gridCandidateTableVisible && this.mContainer != null) {
                closeGridCanditesPopup();
            }
            if (this.mWordListHeight == 0) {
                this.mWordListHeight = this.mCharacterContextViewContainer.getMeasuredHeight();
            }
            changeAltIconOfSwitchingLayout(true);
            showFullScreenHandWritingView();
            if (this.mShownFuntionBar && this.mFunctionBarListView.isFunctionBarShowing()) {
                this.mShownFuntionBar = false;
            } else {
                updateCandidatesList();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showFullScreenHandWritingView() {
        if (!ActivityManagerCompat.isUserAMonkey()) {
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
                    } else {
                        if (containerView.getRootViewHeight() <= 0) {
                            postDelayShowingFullScreenMsg();
                            return;
                        }
                        fullHandWritingScreenHeight = fullHandWritingScreenYOffset - containerView.getRootViewHeight();
                    }
                }
                fullHandWritingScreenYOffset -= getKeyboard().getHeight();
            }
            this.mContainer.showFullScreenHandWritingFrame(0, -fullHandWritingScreenYOffset, w, fullHandWritingScreenHeight);
            changeAltIconOfSwitchingLayout(true);
        }
    }

    protected void hideFullScreenHandWritingView(boolean aFlag) {
        this.mContainer.hideFullScreenHandWritingFrame(aFlag);
    }

    @Override // com.nuance.swype.input.InputView
    public void handleEmotionKey() {
        setKeyboardLayer(KeyboardEx.KeyboardLayerType.KEYBOARD_EMOJI);
        super.handleEmotionKey();
    }

    @Override // com.nuance.swype.input.InputView
    public void handleUniversalKeyboardResize(int keyCode) {
        if (!isNormalTextInputMode()) {
            hideFullScreenHandWritingView(true);
        } else {
            showFullScreenHandWritingView();
        }
        this.mContainer.hidePopupCandidatesView();
        this.gridCandidateTableVisible = false;
        this.candidatesPopup = null;
        acceptCurrentActiveWord();
        this.mContextCandidates.clear();
        this.mWordSourceList.clear();
        if (this.mShowFunctionBar) {
            this.mCharacterContextViewContainer.clear();
            this.mCharacterContextViewContainer.hidePhraseListView();
            this.mCharacterContextViewContainer.showFunctionBarListView();
        }
        super.handleUniversalKeyboardResize(keyCode);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showDelayFullScreen() {
        if (getWindowToken() != null) {
            if (isNormalTextInputMode()) {
                showHandWritingView();
                this.mIme.showStartupMessage();
                return;
            }
            return;
        }
        postDelayShowingFullScreenMsg();
    }

    private void clearLinePath() {
        if (this.mCurrentWritingPad != null) {
            this.mCurrentWritingPad.clearLinePath();
        }
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

    protected void postDelayShowingFullScreenMsg() {
        if (this.mDelayShowingFullScreenHandler.hasMessages(1)) {
            this.mDelayShowingFullScreenHandler.removeMessages(1);
        }
        this.mDelayShowingFullScreenHandler.sendEmptyMessageDelayed(1, 50L);
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
            if (this.mRecognitionCandidates != null && !this.mRecognitionCandidates.isEmpty()) {
                if (this.mPopupCandidateListShowable) {
                    this.gridCandidateTableVisible = true;
                    showCharacterListView();
                }
            } else if (this.mPopupCandidateListShowable) {
                showContextPopupListView();
            }
            dismissPopupKeyboard();
        }
    }

    @Override // com.nuance.swype.input.chinese.CJKCandidatesListView.OnWordSelectActionListener
    public void prevBtnPressed(CJKCandidatesListView aSource) {
        if (!this.gridCandidateTableVisible) {
            nextBtnPressed(aSource);
        }
    }

    @Override // com.nuance.swype.input.chinese.CJKCandidatesListView.OnWordSelectActionListener
    public boolean isScrollable(CJKCandidatesListView aSource) {
        return false;
    }

    @Override // com.nuance.swype.input.InputView
    public void onApplicationUnbind() {
        dimissRemoveUdbWordDialog();
        super.onApplicationUnbind();
    }

    public void updateKeyboardDockMode() {
        KeyboardEx keyboard = getKeyboard();
        if (keyboard != null) {
            setKeyboard(keyboard);
        }
    }

    @Override // com.nuance.swype.input.InputView
    @SuppressLint({"PrivateResource"})
    public void setKeyboardLayer(KeyboardEx.KeyboardLayerType keyboardLayer) {
        if (keyboardLayer == KeyboardEx.KeyboardLayerType.KEYBOARD_EMOJI) {
            keyboardLayer = KeyboardEx.KeyboardLayerType.KEYBOARD_SYMBOLS;
        }
        if (getKeyboardLayer() != keyboardLayer) {
            super.setKeyboardLayer(keyboardLayer);
            dismissPopupKeyboard();
            acceptCurrentActiveWord();
            InputMethods.InputMode inputMode = this.mCurrentInputLanguage.getCurrentInputMode();
            this.mKeyboardSwitcher.createKeyboardForTextInput(keyboardLayer, this.mInputFieldInfo, inputMode);
            if (!isNormalTextInputMode()) {
                this.mCharacterContextViewContainer.hideSpellPrefixSuffixList();
                hideFullScreenHandWritingView(false);
                updateKeyboardDockMode();
                this.mCharacterContextViewContainer.clear();
                this.mCharacterContextViewContainer.hidePhraseListView();
                this.mCharacterContextViewContainer.showFunctionBarListView();
            } else {
                updateKeyboardDockMode();
                setHWFrame();
                postDelayShowingFullScreenMsg();
                this.mIme.refreshLanguageOnSpaceKey(getKeyboard(), this);
                this.mCharacterContextViewContainer.hideSpellPrefixSuffixList();
                if (getKeyboard() != null) {
                    List<KeyboardEx.Key> keys = getKeyboard().getKeys();
                    int i = 0;
                    while (true) {
                        if (i >= keys.size()) {
                            break;
                        }
                        KeyboardEx.Key key = keys.get(i);
                        if (key.altCode != 43579) {
                            i++;
                        } else if (key.altIcon != null) {
                            IMEApplication app = IMEApplication.from(getContext());
                            key.altIcon = app.getThemedDrawable(R.attr.cjkAltIconMinHwCommaKey);
                            key.altPreviewIcon = app.getThemedDrawable(R.attr.cjkAltPreMinHwCommaKey);
                            invalidateKey(key);
                        }
                    }
                }
            }
            setShiftState(Shift.ShiftState.OFF);
            if (keyboardLayer == KeyboardEx.KeyboardLayerType.KEYBOARD_SYMBOLS && keyboardLayer == keyboardLayer) {
                if (this.symInputController == null) {
                    this.symInputController = new SymbolInputController();
                }
                this.symInputController.setContext(IMEApplication.from(this.mIme).getThemedContext());
                this.symInputController.show();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.InputView
    public void resumeSpeech() {
        if (this.mContainer != null) {
            super.resumeSpeech();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.InputView, com.nuance.swype.input.KeyboardViewEx
    public boolean handleScrollDown() {
        if (IMEApplication.from(this.mIme).getIMEHandlerManager() == null) {
            return false;
        }
        this.mIme.handlerManager.toggleFullScreenHW();
        return true;
    }

    @Override // com.nuance.swype.input.InputView
    public void onSpeechViewShowedUp() {
        hideFullScreenHandWritingView(false);
    }

    @Override // com.nuance.swype.input.InputView
    public void onSpeechViewDismissed() {
        if (isNormalTextInputMode()) {
            updateCandidatesList();
            this.mCharacterContextViewContainer.setVisibility(0);
            setHWFrame();
            postDelayShowingFullScreenMsg();
        }
    }

    @Override // com.nuance.swype.input.InputView
    public boolean isLanguageSwitchableOnSpace() {
        return isNormalTextInputMode();
    }

    @Override // com.nuance.swype.input.InputView, com.nuance.swype.input.RemoveUdbWordDialog.RemoveUdbWordListener
    @SuppressLint({"PrivateResource"})
    public void onHandleUdbWordRemoval(String word) {
        if (this.mChineseInput != null) {
            boolean deleteStatus = this.mChineseInput.dlmDelete(word);
            if (this.mCurrentWritingPad != null) {
                if (deleteStatus) {
                    String removeMgs = "\"" + word + "\" " + getContext().getApplicationContext().getResources().getString(R.string.delete_success);
                    QuickToast.show(getContext(), removeMgs, 0, this.mCurrentWritingPad.getHeight() / 2);
                    this.mComposition.clearCurrentInline();
                    if (this.mRecognitionCandidates != null) {
                        this.mRecognitionCandidates.clear();
                        this.mRecognitionCandidates = null;
                    }
                    this.mContextCandidates.clear();
                    this.mWordSourceList.clear();
                    updateCandidatesList();
                    this.mContainer.hidePopupCandidatesView();
                    this.gridCandidateTableVisible = false;
                    showFullScreenHandWritingView();
                    this.mSelectionAreaOption = -1;
                    this.mCurrentWritingPad.setPointStatus(-1);
                    return;
                }
                String removeMgs2 = "\"" + word + "\" " + getContext().getApplicationContext().getResources().getString(R.string.delete_failed);
                QuickToast.show(getContext(), removeMgs2, 0, this.mCurrentWritingPad.getHeight() / 2);
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

    @Override // com.nuance.swype.input.HandWritingView.InSelectionAreaListener
    @SuppressLint({"PrivateResource"})
    public boolean pointInSelectionArea(int x, int y) {
        int boundaryHandWritingScreenHeight;
        if (x <= 0 || y <= 0) {
            return false;
        }
        int status_bar_height = IMEApplication.from(this.mIme).getIMEHandlerManager() != null ? this.mIme.handlerManager.statusBarHeight() : 0;
        if (this.mWordListHeight == 0) {
            this.mWordListHeight = this.mCharacterContextViewContainer.getMeasuredHeight();
        }
        int keyboard_height = getResources().getDimensionPixelSize(R.dimen.key_height);
        int screen_height = getResources().getDisplayMetrics().heightPixels;
        if (this.mCharacterContextViewContainer.isShown()) {
            boundaryHandWritingScreenHeight = ((screen_height - this.mWordListHeight) - keyboard_height) - status_bar_height;
        } else {
            boundaryHandWritingScreenHeight = (screen_height - keyboard_height) - status_bar_height;
        }
        return y >= boundaryHandWritingScreenHeight;
    }

    @Override // com.nuance.swype.input.HandWritingView.InSelectionAreaListener
    @SuppressLint({"PrivateResource"})
    public boolean transferKeyEvent(MotionEvent me) {
        int candidateArea;
        if (this.mWordListHeight == 0) {
            this.mWordListHeight = this.mCharacterContextViewContainer.getMeasuredHeight();
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
        int boundarySize = ((screen_height - status_bar_height) - this.mWordListHeight) - keyboard_height;
        if ((posY > boundarySize && this.mSelectionAreaOption == -1) || this.mSelectionAreaOption == 1) {
            if (actionType == 0) {
                this.mSelectionAreaOption = 1;
            }
            float posY2 = posY - boundarySize;
            if (posX >= this.candidatesListViewCJK.getLeftWidth()) {
                if (this.mCharacterContextViewContainer.isRightPhraseArrowShowable()) {
                    candidateArea = screen_width - this.mCharacterContextViewContainer.getRightPhraseImageButtonWidth();
                } else {
                    candidateArea = screen_width;
                }
                if (posX <= candidateArea) {
                    MotionEvent motionEvent2 = MotionEvent.obtain(downTime, eventTime, actionType, posX - this.candidatesListViewCJK.getLeftWidth(), posY2, mState);
                    this.candidatesListViewCJK.dispatchTouchEvent(motionEvent2);
                    this.mFunctionBarListView.dispatchTouchEvent(motionEvent2);
                    motionEvent2.recycle();
                } else {
                    if (actionType == 0) {
                        MotionEvent motionEvent3 = MotionEvent.obtain(downTime, eventTime, actionType, posX, posY2, mState);
                        this.mCharacterContextViewContainer.dispatchTouchEvent(motionEvent3);
                        motionEvent3.recycle();
                    }
                    if (actionType == 1 || actionType == 3) {
                        if (this.mSelectionAreaOption == 1) {
                            this.candidatesListViewCJK.dispatchTouchEvent(MotionEvent.obtain(downTime, eventTime, actionType, posX, posY2, mState));
                        } else {
                            this.mSelectionAreaOption = -1;
                            this.mCurrentWritingPad.setPointStatus(-1);
                        }
                    }
                }
            } else {
                if (actionType == 0) {
                    MotionEvent motionEvent4 = MotionEvent.obtain(downTime, eventTime, actionType, posX, posY2, mState);
                    this.mCharacterContextViewContainer.dispatchTouchEvent(motionEvent4);
                    motionEvent4.recycle();
                }
                if (actionType == 1 || actionType == 3) {
                    if (this.mSelectionAreaOption == 1) {
                        this.candidatesListViewCJK.dispatchTouchEvent(MotionEvent.obtain(downTime, eventTime, actionType, posX, posY2, mState));
                    } else {
                        this.mSelectionAreaOption = -1;
                        this.mCurrentWritingPad.setPointStatus(-1);
                    }
                }
            }
            return true;
        }
        return false;
    }

    @Override // com.nuance.swype.input.HandWritingView.InSelectionAreaListener
    public void resetArea(int areaOption) {
        if (areaOption == -2) {
            this.mIme.requestHideSelf(0);
        } else {
            this.mSelectionAreaOption = areaOption;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.KeyboardViewEx
    @SuppressLint({"PrivateResource"})
    public boolean showSymbolSelectPopup(KeyboardEx.Key popupKey) {
        if (popupKey == null) {
            return false;
        }
        if (popupKey.label != null && popupKey.popupResId == R.xml.popup_smileys_with_return && popupKey.label.equals(":-)")) {
            return showStaticSelectPopup(popupKey);
        }
        return super.showSymbolSelectPopup(popupKey);
    }

    @Override // com.nuance.swype.input.HandWritingView.InSelectionAreaListener
    public boolean getCurrentScreenMode() {
        return false;
    }

    @Override // com.nuance.swype.input.HandWritingView.InSelectionAreaListener
    public boolean isSpeechPopupShowing() {
        return super.isSpeechViewShowing();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.InputView
    public boolean isNormalTextInputMode() {
        return this.mKeyboardSwitcher.isAlphabetMode();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.InputView
    public void setSwypeKeytoolTipSuggestion() {
        if (!this.mShowFunctionBar) {
            if (this.mCharacterContextViewContainer != null) {
                this.mCharacterContextViewContainer.hideFunctionBarListView();
                this.mCharacterContextViewContainer.showPhraseListView();
            }
            syncCandidateDisplayStyleToMode();
            this.candidatesListViewCJK.showSwypeTooltip();
        }
    }

    @Override // com.nuance.swype.input.InputView
    public void setNotMatchToolTipSuggestion() {
        if (!this.mShowFunctionBar) {
            if (this.mCharacterContextViewContainer != null) {
                this.mCharacterContextViewContainer.hideFunctionBarListView();
                this.mCharacterContextViewContainer.showPhraseListView();
            }
            syncCandidateDisplayStyleToMode();
            this.candidatesListViewCJK.showNotMatchTootip();
        }
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

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.KeyboardViewEx
    public MotionEvent getExtendedEvent(MotionEvent me) {
        KeyboardEx keyboard = getKeyboard();
        return (keyboard == null || keyboard.getKeyboardDockMode() == KeyboardEx.KeyboardDockMode.MOVABLE_MINI || (keyboard.getKeyboardDockMode() == KeyboardEx.KeyboardDockMode.DOCK_FULL && this.mKeyboardSwitcher != null && this.mKeyboardSwitcher.isSymbolMode()) || (keyboard.getKeyboardDockMode() == KeyboardEx.KeyboardDockMode.DOCK_SPLIT && this.mKeyboardSwitcher != null && this.mKeyboardSwitcher.isSymbolMode())) ? super.getExtendedEvent(me) : me;
    }

    @Override // com.nuance.swype.input.InputView
    public boolean shouldDisableInput(int primaryCode) {
        return (isValidBuild() || primaryCode == 4087) ? false : true;
    }

    @Override // com.nuance.swype.input.InputView, com.nuance.swype.input.KeyboardViewEx
    public boolean isFullScreenHandWritingView() {
        return true;
    }

    @Override // com.nuance.swype.input.InputView
    public boolean isHandWritingInputView() {
        return true;
    }

    @Override // com.nuance.swype.input.chinese.CJKCandidatesListView.OnWordSelectActionListener
    public void onScrollContentChanged() {
        if (this.mCharacterContextViewContainer != null) {
            this.mCharacterContextViewContainer.updateScrollArrowVisibility();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.InputView
    public Rect getSpeechPopupRectInWindowCoord() {
        Rect rect = this.mIme.getSpeechPopupRectInWindowCoord();
        if (rect != null) {
            rect.top -= this.mCharacterContextViewContainer != null ? this.mCharacterContextViewContainer.getMeasuredHeight() : 0;
        }
        return rect;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public CharSequence getPureCandidate(CharSequence candidate) {
        String cand = candidate.toString();
        int index = cand.lastIndexOf(36);
        return index <= 0 ? candidate : cand.substring(0, index);
    }

    private List<CharSequence> getPureCandidates(List<CharSequence> candidates) {
        int size = candidates.size();
        List<CharSequence> pureCandidates = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            pureCandidates.add(getPureCandidate(candidates.get(i)));
        }
        return pureCandidates;
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
