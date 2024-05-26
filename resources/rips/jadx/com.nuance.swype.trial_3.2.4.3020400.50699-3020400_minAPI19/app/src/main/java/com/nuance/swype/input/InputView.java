package com.nuance.swype.input;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputConnection;
import android.widget.FrameLayout;
import com.nuance.android.compat.ActivityManagerCompat;
import com.nuance.android.compat.UserManagerCompat;
import com.nuance.android.compat.ViewCompat;
import com.nuance.input.swypecorelib.Candidates;
import com.nuance.input.swypecorelib.Shift;
import com.nuance.input.swypecorelib.T9Write;
import com.nuance.input.swypecorelib.WordCandidate;
import com.nuance.input.swypecorelib.XT9CoreInput;
import com.nuance.input.swypecorelib.XT9Status;
import com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.xmlResults.XMLResultsHandler;
import com.nuance.speech.SpeechResultTextBuffer;
import com.nuance.swype.connect.Connect;
import com.nuance.swype.input.AbstractTapDetector;
import com.nuance.swype.input.CandidatesListView;
import com.nuance.swype.input.ChinaNetworkNotificationDialog;
import com.nuance.swype.input.InputMethods;
import com.nuance.swype.input.KeyboardEx;
import com.nuance.swype.input.KeyboardViewEx;
import com.nuance.swype.input.RemoveUdbWordDialog;
import com.nuance.swype.input.accessibility.AccessibilityNotification;
import com.nuance.swype.input.accessibility.SoundResources;
import com.nuance.swype.input.accessibility.statemachine.AccessibilityStateManager;
import com.nuance.swype.input.appspecific.AppSpecificBehavior;
import com.nuance.swype.input.appspecific.AppSpecificInputConnection;
import com.nuance.swype.input.chinese.CJKCandidatesListView;
import com.nuance.swype.input.chinese.CJKWordListViewContainer;
import com.nuance.swype.input.emoji.Emoji;
import com.nuance.swype.input.emoji.EmojiCacheManager;
import com.nuance.swype.input.emoji.EmojiInfo;
import com.nuance.swype.input.emoji.EmojiInputController;
import com.nuance.swype.input.emoji.EmojiLoader;
import com.nuance.swype.input.emoji.EmojiSkinAdapter;
import com.nuance.swype.input.emoji.PopupEmojiSkinList;
import com.nuance.swype.input.hardkey.HardKeyboardManager;
import com.nuance.swype.input.keyboard.DefaultHWRTouchKeyHandler;
import com.nuance.swype.input.keyboard.GestureHandler;
import com.nuance.swype.input.keyboard.InputContextRequestDispatcher;
import com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher;
import com.nuance.swype.input.view.InputContainerView;
import com.nuance.swype.stats.StatisticsEnabledEditState;
import com.nuance.swype.stats.StatisticsManager;
import com.nuance.swype.stats.UsageManager;
import com.nuance.swype.usagedata.UsageData;
import com.nuance.swype.util.CharacterUtilities;
import com.nuance.swype.util.LogManager;
import com.nuance.swype.util.WordDecorator;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/* loaded from: classes.dex */
public abstract class InputView extends KeyboardViewEx implements AbstractTapDetector.TapHandler, CandidatesListView.CandidateListener, RemoveUdbWordDialog.RemoveUdbWordListener, EmojiSkinAdapter.OnItemClickListener {
    protected static final String CYCLINGKEYBOARD = "cyclingkeyboard";
    protected static final String EDITKEYBOARD = "editkeyboard";
    protected static final String HANDWRITINGMODE = "handwritingmode";
    protected static final String INPUTMODE = "inputmode";
    protected static final String LANGUAGE_OPTION = "language_option";
    public static final int LAST_INPUT_CANDIDATE_SELECTION = 4;
    public static final int LAST_INPUT_NWP_CANDIDATE_SELECTION = 6;
    public static final int LAST_INPUT_TAP = 1;
    public static final int LAST_INPUT_TOUCH = 3;
    public static final int LAST_INPUT_TRACE = 2;
    public static final int LAST_INPUT_UNDEFINED = 0;
    public static final int LAST_INPUT_WORD = 5;
    protected static final int MAX_FUNCTION_ITEM = 4;
    protected static final int MAX_SIZE_SELECTION_LIST = 1000;
    protected static final int MSG_DELAY_POPULATING_WCL_FOR_TRACE = 12;
    protected static final int MSG_DELAY_RECAPTURE = 16;
    protected static final int MSG_DELAY_RESUME_SPEECH = 11;
    protected static final int MSG_DELAY_SHOW_ABC_XT9_KEY_TOAST = 5;
    protected static final int MSG_DELAY_SHOW_AUTO_SPACE_TIP = 10;
    protected static final int MSG_DELAY_SHOW_TRACE_AUTO_ACCEPT_TIP = 6;
    protected static final int MSG_DELAY_SHOW_TRACE_INPUT_TIP = 7;
    protected static final int MSG_DELAY_START_INPUT_SESSION = 15;
    protected static final int MSG_DELAY_UPDATE_CAPS_EDIT = 13;
    protected static final int MSG_DELAY_UPDATE_DISPLAY = 4;
    protected static final int MSG_DELAY_UPDATE_INLINE_STRING = 17;
    protected static final int MSG_FIRST = 1;
    protected static final int MSG_GET_MORE_SUGGESTIONS = 1;
    protected static final int MSG_LAST = 19;
    public static final int MSG_PROMPT_THEME_STORE = 125;
    public static final int MSG_PROMPT_TRIAL_EXPIRE = 127;
    protected static final int MSG_REQUEST_CHINESE_PREDICTION = 18;
    protected static final int MSG_SHOW_START_OF_WORD_CANDIDATE = 8;
    protected static final int MSG_UPDATE_SUGGESTIONS = 9;
    private static final int NOT_A_CURSOR_POSITION = -1;
    protected static final String NUMBERKEYBOARD = "numberkeyboard";
    protected static final String ONKEYBOARD = "onkeyboard";
    protected static final String SETTINGS = "settings";
    protected static final int STROKE_RECOGNITION_TIME_OUT = 100;
    protected static final int WORD_DELETE = 0;
    public static final int selectKeyLevel = 0;
    public static final int selectKeyMiniLevel = 2;
    protected static final String wclAlphaReqMessage = "alphaInput";
    protected static final String wclReqMessage = "CJK";
    protected final int MSG_UPDATE_CHINESE_PREDICTION_RESULT;
    public boolean autoCorrectionEnabled;
    private View billboardHolder;
    protected int candidatesEndCache;
    public CandidatesListView candidatesListView;
    public CJKCandidatesListView candidatesListViewCJK;
    protected int candidatesStartCache;
    private ChinaNetworkNotificationDialog.ChinaNetworkNotificationDialogListener cnNwDlgListener;
    private SpannableStringBuilder composingSpeechText;
    private EmojiCacheManager<String, Integer> emojiCacheManager;
    public View emojiCandidatesHolder;
    public CandidatesListView emojiCandidatesListView;
    private boolean enableSymbolSelectPopupAllLayers;
    private int endSelection;
    public final KeyboardTouchEventDispatcher.FlingGestureListener flingGestureListener;
    protected GestureHandler gestureHandler;
    public boolean gridCandidateTableVisible;
    private IMEApplication imeApp;
    private boolean isAutoReturnToEditorDefaultLayerEnabled;
    private boolean isExploredByTouch;
    public boolean isKeepingKeyboard;
    public boolean isLDBCompatible;
    public boolean isOnceAction;
    public boolean isTraceEnabledOnKeyboard;
    private StyleSpan italicSpan;
    public KeyboardTouchEventDispatcher keyboardTouchEventDispatcher;
    public boolean mAddOnDictionariesOn;
    public boolean mAutoCap;
    public boolean mAutoPunctuationOn;
    public boolean mAutoSpace;
    public BuildInfo mBuildInfo;
    protected boolean mCapsLock;
    public boolean mChineseSettingsOn;
    protected int mCommittedLength;
    public boolean mCompletionOn;
    public Completions mCompletions;
    public boolean mContextWindowShowing;
    public InputMethods.Language mCurrentInputLanguage;
    protected boolean mDismissKeyboardOn;
    public boolean mEditKeyboardOn;
    public EditState mEditState;
    public boolean mEmojiFunctionBarOn;
    protected boolean mEnableEmojiChoiceList;
    protected boolean mEnableHandwriting;
    public int mFuzzyPinyin;
    protected int mHardkeyKeyboardLayoutId;
    public boolean mInURLEmail;
    public InputFieldInfo mInputFieldInfo;
    public boolean mInputModeOn;
    public boolean mIsUseHardkey;
    public boolean mKeyboardInputSuggestionOn;
    public int mKeyboardLayoutId;
    public boolean mLanguageOptionOn;
    public int mLastInput;
    private AlertDialog mNetworkPromptMessage;
    public boolean mNextWordPredictionOn;
    public boolean mNumberKeyboardOn;
    public boolean mPreferExplicit;
    public boolean mQuickToggleOn;
    public boolean mRecaptureOn;
    protected String mReconstructWord;
    public boolean mSettingsOn;
    public boolean mShowFunctionBar;
    protected boolean mShowInternalCandidates;
    public SpeechWrapper mSpeechWrapper;
    public boolean mStarted;
    protected boolean mSwitchIMEOn;
    public boolean mTextInputPredictionOn;
    public boolean mThemesOn;
    public boolean mTraceInputSuggestionOn;
    public int mWordCompletionPoint;
    public AtomicInteger mWordSource;
    public List<AtomicInteger> mWordSourceList;
    protected int newSelEndCache;
    protected int newSelStartCache;
    protected int oldSelEndCache;
    protected int oldSelStartCache;
    public Candidates.Source pendingCandidateSource;
    private PopupEmojiSkinList popupEmojiSkinList;
    private boolean preProcessOnSpeechDictation;
    protected boolean promptToAddWords;
    protected RemoveUdbWordDialog removeUdbWordDialog;
    private KeyboardEx.Key selectKey;
    private List<Emoji> skinToneList;
    private int startSelection;
    private long startTimeDisplaySelectionList;
    public Candidates suggestionCandidates;
    public WclPrompt themeStoreWclPrompt;
    protected boolean traceAutoAcceptOn;
    protected WclPrompt trialWclPrompt;
    protected UndoAcceptHandler undoAcceptHandler;
    protected List<Long> usedTimeListReselectDisplaySelection;
    protected List<Long> usedTimeListTapDisplaySelection;
    private WordDecorator wordDecorator;
    private String wordDeletedString;
    private View wordListHolder;
    public WordListViewContainer wordListViewContainer;
    public CJKWordListViewContainer wordListViewContainerCJK;
    public XT9CoreInput xt9coreinput;

    public InputView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InputView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.MSG_UPDATE_CHINESE_PREDICTION_RESULT = 19;
        this.mLastInput = 0;
        this.mKeyboardLayoutId = 0;
        this.usedTimeListTapDisplaySelection = new ArrayList();
        this.usedTimeListReselectDisplaySelection = new ArrayList();
        this.isTraceEnabledOnKeyboard = false;
        this.enableSymbolSelectPopupAllLayers = true;
        this.mCompletions = new Completions();
        this.mWordSource = new AtomicInteger();
        this.mWordSourceList = new ArrayList();
        this.pendingCandidateSource = Candidates.Source.INVALID;
        this.removeUdbWordDialog = null;
        this.isLDBCompatible = true;
        this.startSelection = -1;
        this.endSelection = -1;
        this.preProcessOnSpeechDictation = true;
        this.mContextWindowShowing = false;
        this.cnNwDlgListener = new ChinaNetworkNotificationDialog.ChinaNetworkNotificationDialogListener() { // from class: com.nuance.swype.input.InputView.1
            @Override // com.nuance.swype.input.ChinaNetworkNotificationDialog.ChinaNetworkNotificationDialogListener
            public boolean onPositiveButtonClick() {
                InputView.this.actualStartSpeech();
                return false;
            }

            @Override // com.nuance.swype.input.ChinaNetworkNotificationDialog.ChinaNetworkNotificationDialogListener
            public boolean onNegativeButtonClick() {
                return false;
            }
        };
        this.italicSpan = new StyleSpan(2);
        this.composingSpeechText = new SpannableStringBuilder();
        this.mHardkeyKeyboardLayoutId = 0;
        this.flingGestureListener = new KeyboardTouchEventDispatcher.FlingGestureListener() { // from class: com.nuance.swype.input.InputView.2
            @Override // com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher.FlingGestureListener
            public boolean onFlingLeft() {
                return InputView.this.handleScrollLeft();
            }

            @Override // com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher.FlingGestureListener
            public boolean onFlingRight() {
                return InputView.this.handleScrollRight();
            }

            @Override // com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher.FlingGestureListener
            public boolean onFlingUp() {
                return InputView.this.handleScrollUp();
            }

            @Override // com.nuance.swype.input.keyboard.KeyboardTouchEventDispatcher.FlingGestureListener
            public boolean onFlingDown() {
                return InputView.this.handleScrollDown();
            }
        };
        this.mBuildInfo = BuildInfo.from(context);
        Resources res = context.getResources();
        this.enableSymbolSelectPopupAllLayers = res.getBoolean(R.bool.enable_symbol_select_popup_all_layers);
        this.wordDeletedString = res.getString(R.string.accessibility_note_WordDelete);
        this.imeApp = IMEApplication.from(getContext());
    }

    public void setCurrentInputLanguage(InputMethods.Language inputLanguage) {
        this.mCurrentInputLanguage = inputLanguage;
    }

    public InputMethods.Language getCurrentInputLanguage() {
        return this.mCurrentInputLanguage;
    }

    public void create(IME ime, XT9CoreInput xt9coreinput, SpeechWrapper speechWrapper) {
        create(ime, xt9coreinput, null, speechWrapper);
    }

    public void create(IME ime, XT9CoreInput xt9coreinput, T9Write t9write, SpeechWrapper speechWrapper) {
        this.mIme = ime;
        this.mSpeechWrapper = speechWrapper;
        this.xt9coreinput = xt9coreinput;
        this.mEditState = ime.getEditState();
        this.isExploredByTouch = this.mIme.isAccessibilitySupportEnabled();
        this.undoAcceptHandler = new UndoAcceptHandler(xt9coreinput);
        this.gestureHandler = new GestureHandler(ime, this, xt9coreinput, IMEApplication.from(ime).getGestureManager());
        this.keyboardTouchEventDispatcher = new KeyboardTouchEventDispatcher(getContext(), xt9coreinput);
    }

    public XT9CoreInput getXT9CoreInput() {
        return this.xt9coreinput;
    }

    public UndoAcceptHandler getUndoAcceptHandler() {
        return this.undoAcceptHandler;
    }

    public void removeAllMessages() {
    }

    public EmojiInputController getEmojiInputViewController() {
        return IMEApplication.from(this.mIme).getEmojiInputViewController();
    }

    public void destroy() {
        closing();
        removeAllMessages();
        this.mIme = null;
        this.mCurrentInputLanguage = null;
        this.mInputFieldInfo = null;
        if (this.keyboardTouchEventDispatcher != null) {
            this.keyboardTouchEventDispatcher.unresisterTouchKeyHandler();
            this.keyboardTouchEventDispatcher = null;
        }
        InputContextRequestDispatcher.getDispatcherInstance().setHandler(null);
        AccessibilityStateManager.getInstance().setCurrentView(null);
    }

    public KeyboardEx.KeyboardDockMode getNextKeyboardDockMode(KeyboardEx.KeyboardDockMode currentMode) {
        KeyboardEx.KeyboardDockMode newMode;
        KeyboardEx.KeyboardDockMode keyboardDockMode = KeyboardEx.KeyboardDockMode.DOCK_FULL;
        int i = 0;
        int dockModeLength = KeyboardEx.KeyboardDockMode.values().length;
        EnumSet<KeyboardEx.KeyboardDockMode> invalidDockModes = KeyboardEx.KeyboardDockMode.from(getKeyboard().keyboardStyle.getInt(R.attr.invalidDockModes, 0));
        do {
            switch (currentMode) {
                case DOCK_FULL:
                    newMode = KeyboardEx.KeyboardDockMode.DOCK_LEFT;
                    break;
                case DOCK_LEFT:
                    newMode = KeyboardEx.KeyboardDockMode.DOCK_RIGHT;
                    break;
                case DOCK_RIGHT:
                    newMode = KeyboardEx.KeyboardDockMode.MOVABLE_MINI;
                    break;
                case MOVABLE_MINI:
                    newMode = KeyboardEx.KeyboardDockMode.DOCK_SPLIT;
                    break;
                case DOCK_SPLIT:
                    newMode = KeyboardEx.KeyboardDockMode.DOCK_FULL;
                    break;
                default:
                    newMode = currentMode;
                    break;
            }
            if (invalidDockModes.contains(newMode)) {
                currentMode = newMode;
                i++;
            }
            return newMode;
        } while (i < dockModeLength);
        return newMode;
    }

    private void setNewDockModePref(KeyboardEx.KeyboardDockMode mode) {
        StatisticsManager.SessionStatsScribe sessionScribe = StatisticsManager.getSessionStatsScribe(getContext());
        UserPreferences userPrefs = UserPreferences.from(getContext());
        if (sessionScribe != null) {
            KeyboardEx.KeyboardDockMode previousMode = UserPreferences.from(getContext()).getKeyboardDockingMode();
            userPrefs.setKeyboardDockingMode(mode);
            sessionScribe.recordSettingsChange("KeyboardDockMode", mode.getName(getResources()), previousMode.getName(getResources()));
            return;
        }
        userPrefs.setKeyboardDockingMode(mode);
    }

    public boolean isVerticalDragAllowed() {
        return !(isFullScreenHandWritingView() && isNormalTextInputMode());
    }

    public void updateDockModeForHandwriting(KeyboardEx keyboard) {
        InputContainerView containerView;
        if (keyboard != null) {
            if (!IMEApplication.from(getContext()).isScreenLayoutTablet()) {
                KeyboardEx.KeyboardDockMode dockMode = isNormalTextInputMode() ? KeyboardEx.KeyboardDockMode.DOCK_FULL : UserPreferences.from(getContext()).getKeyboardDockingMode();
                boolean dockChanged = keyboard.getKeyboardDockMode() != dockMode;
                keyboard.setKeyboardDockMode(dockMode);
                if (dockChanged) {
                    setKeyboardDatabaseForDockChanged();
                }
            }
            if (this.mIme != null && (containerView = this.mIme.getInputContainerView()) != null) {
                containerView.requestLayout();
                if (keyboard.getKeyboardDockMode() == KeyboardEx.KeyboardDockMode.MOVABLE_MINI) {
                    containerView.showDragFrame(true);
                    containerView.setMode(KeyboardEx.KeyboardDockMode.MOVABLE_MINI);
                } else {
                    containerView.showDragFrame(false);
                    containerView.setMode(keyboard.getKeyboardDockMode());
                }
                this.isOnceAction = true;
            }
        }
    }

    public void switchKeyboardDockMode(KeyboardEx.KeyboardDockMode dockMode) {
        EnumSet<KeyboardEx.KeyboardDockMode> invalidDockModes = getKeyboard().getInvalidDockModes();
        KeyboardEx.KeyboardDockMode currentMode = getKeyboard().getKeyboardDockMode();
        log.d("switchKeyboardDockMode(): " + currentMode + " -> " + dockMode);
        if (invalidDockModes.contains(dockMode)) {
            log.d("switchKeyboardDockMode(): invalid:" + dockMode);
            dockMode = currentMode;
        }
        if (dockMode != currentMode) {
            setNewDockModePref(dockMode);
            this.mKeyboardSwitcher.createKeyboardForTextInput(this.mKeyboardSwitcher.currentKeyboardLayer(), getInputFieldInfo(), this.mCurrentInputLanguage.getCurrentInputMode());
        }
        this.mKeyboardSwitcher.createKeyboardForTextInput(this.mKeyboardSwitcher.currentKeyboardLayer(), getInputFieldInfo(), this.mCurrentInputLanguage.getCurrentInputMode());
        UserPreferences.from(getContext()).setKeyboardDockingMode(getKeyboard().getKeyboardDockMode());
    }

    public void handleKeyboardResize() {
        KeyboardEx.KeyboardDockMode currentMode = getKeyboard().getKeyboardDockMode();
        KeyboardEx.KeyboardDockMode newMode = getNextKeyboardDockMode(currentMode);
        switchKeyboardDockMode(newMode);
    }

    public void handlePossibleActionAfterResize() {
    }

    public void handleUniversalKeyboardResize(int keyCode) {
        KeyboardEx.KeyboardDockMode mode = KeyboardEx.KeyboardDockMode.DOCK_FULL;
        switch (keyCode) {
            case KeyboardEx.KEYCODE_RESIZE_MINIMOVABLE_SCREEN /* -117 */:
                mode = KeyboardEx.KeyboardDockMode.MOVABLE_MINI;
                break;
            case KeyboardEx.KEYCODE_RESIZE_SPLIT_SCREEN /* -116 */:
                mode = KeyboardEx.KeyboardDockMode.DOCK_SPLIT;
                break;
            case KeyboardEx.KEYCODE_RESIZE_MINIRIGHT_SCREEN /* -115 */:
                mode = KeyboardEx.KeyboardDockMode.DOCK_RIGHT;
                break;
            case KeyboardEx.KEYCODE_RESIZE_MINILEFT_SCREEN /* -114 */:
                mode = KeyboardEx.KeyboardDockMode.DOCK_LEFT;
                break;
            case KeyboardEx.KEYCODE_RESIZE_FULL_SCREEN /* -113 */:
                mode = KeyboardEx.KeyboardDockMode.DOCK_FULL;
                break;
        }
        switchKeyboardDockMode(mode);
    }

    public void handlePossibleActionAfterUniversalKeyboardResize(boolean hideSymbolView) {
    }

    @Override // com.nuance.swype.input.KeyboardViewEx
    public KeyboardSwitcher getKeyboardSwitcher() {
        return this.mKeyboardSwitcher;
    }

    @SuppressLint({"InflateParams"})
    public View createCandidatesView(CandidatesListView.CandidateListener onSelectListener) {
        if (this.wordListViewContainer == null) {
            LayoutInflater inflater = IMEApplication.from(this.mIme).getThemedLayoutInflater(this.mIme.getLayoutInflater());
            IMEApplication.from(this.mIme).getThemeLoader().setLayoutInflaterFactory(inflater);
            this.wordListViewContainer = (WordListViewContainer) inflater.inflate(R.layout.candidates, (ViewGroup) null);
            this.wordListHolder = this.wordListViewContainer.findViewById(R.id.wordlist_holder);
            this.billboardHolder = this.wordListViewContainer.findViewById(R.id.billboard);
            IMEApplication.from(this.mIme).getThemeLoader().applyTheme(this.wordListHolder);
            this.candidatesListView = (CandidatesListView) this.wordListViewContainer.findViewById(R.id.candidates);
            this.emojiCandidatesHolder = this.wordListViewContainer.findViewById(R.id.emojilist_holder);
            this.emojiCandidatesListView = (CandidatesListView) this.wordListViewContainer.findViewById(R.id.emojiCandidates);
            Drawable background = IMEApplication.from(this.mIme).getThemedDrawable(R.attr.emojeenieBackground);
            ViewCompat.setBackground(this.emojiCandidatesListView, background);
            this.wordListViewContainer.updateView();
        }
        if (this.candidatesListView != null) {
            this.candidatesListView.clearOnWordSelectActionListeners();
            this.candidatesListView.addOnWordSelectActionListener(onSelectListener);
            this.candidatesListView.addOnWordSelectActionListener(this);
        }
        if (this.emojiCandidatesListView != null) {
            this.emojiCandidatesListView.clearOnWordSelectActionListeners();
            this.emojiCandidatesListView.addOnWordSelectActionListener(onSelectListener);
            this.emojiCandidatesListView.addOnWordSelectActionListener(this);
        }
        return this.wordListViewContainer;
    }

    public View getWordCandidateListContainer() {
        return this.wordListViewContainer;
    }

    public void startInput(InputFieldInfo inputFieldInfo, boolean restarting) {
        this.mStarted = true;
        this.undoAcceptHandler.onAcceptCandidate(null, null, false);
        this.startSelection = inputFieldInfo.getEditorInfo().initialSelStart;
        this.endSelection = inputFieldInfo.getEditorInfo().initialSelEnd;
        this.mInputFieldInfo = inputFieldInfo;
        setOnKeyboardActionListener(this.mIme);
        this.mCompletionOn = inputFieldInfo.isCompletionField() && this.mIme.isFullscreenMode();
        this.mPreferExplicit = inputFieldInfo.isNameField() || inputFieldInfo.isEmailAddressField() || inputFieldInfo.isURLField() || inputFieldInfo.isCompletionField() || inputFieldInfo.isPostalAddress() || inputFieldInfo.isNoSuggestionOnField() || inputFieldInfo.isFieldWithFilterList() || inputFieldInfo.isWebSearchField() || inputFieldInfo.isPasswordField() || getAppSpecificBehavior().shouldTreatEditTextAsWebSearchField();
        this.mInURLEmail = inputFieldInfo.isEmailAddressField() || inputFieldInfo.isURLField();
        multitapClearInvalid();
        checkAccessibility();
        KeyboardManager.from(this.mIme).forceSetKeyboardDatabase(true);
        readSettings(inputFieldInfo);
        if (getCurrentInputConnection() != null && !this.mInputFieldInfo.isPasswordField()) {
            updateExtractedText();
        }
        if (restarting) {
            pauseSpeech();
        } else {
            stopSpeech();
        }
        this.keyboardTouchEventDispatcher.setShiftGestureOffsetValue(getShiftGestureOffset());
        if (getEmojiInputViewController().isActive()) {
            setCandidatesViewShown(true);
        }
    }

    public void finishInput() {
        this.mStarted = false;
        dismissEmojiPopup();
        if (this.emojiCandidatesListView != null) {
            this.emojiCandidatesListView.setHorizontalScroll(true);
        }
        setOnKeyboardActionListener((IME) null);
        multitapClearInvalid();
        closing();
        removeAllMessages();
    }

    public CharSequence getCurrentDefaultWord() {
        if (isEmptyCandidateList() || this.suggestionCandidates.source() == Candidates.Source.NEXT_WORD_PREDICTION || this.suggestionCandidates.source() == Candidates.Source.COMPLETIONS || this.suggestionCandidates.source() == Candidates.Source.RECAPTURE_BY_TEXT_SELECTION) {
            return null;
        }
        return this.suggestionCandidates.getDefaultCandidateString();
    }

    public CharSequence getCurrentExactWord() {
        if (isEmptyCandidateList() || this.suggestionCandidates.source() == Candidates.Source.NEXT_WORD_PREDICTION || this.suggestionCandidates.source() == Candidates.Source.COMPLETIONS || this.suggestionCandidates.source() == Candidates.Source.RECAPTURE_BY_TEXT_SELECTION) {
            return null;
        }
        return this.suggestionCandidates.getExactCandidateString();
    }

    public Candidates.Source getCurrentWordCandidatesSource() {
        if (this.pendingCandidateSource != Candidates.Source.INVALID) {
            return this.pendingCandidateSource;
        }
        if (this.suggestionCandidates != null) {
            return this.suggestionCandidates.source();
        }
        return Candidates.Source.INVALID;
    }

    public void clearCurrentActiveWord() {
    }

    public void updateSelection(int oldSelStart, int oldSelEnd, int newSelStart, int newSelEnd, int[] candidatesIndices) {
        log.d("updateSelection() :  method called parent");
        this.imeApp.setUserTapKey(true);
        AppSpecificInputConnection ic = getCurrentInputConnection();
        if (this.mIme != null && ic != null) {
            Configuration config = getResources().getConfiguration();
            if (!this.mIme.isShowCandidatesViewAllowed() && config.orientation == 2 && this.mInputFieldInfo.isWebSearchField()) {
                this.mIme.setCandidatesViewShown(false);
                return;
            }
            boolean userTapInEditor = this.mIme.cursorUpdateReceivedFromTap();
            if (this.mIme.isInputViewShown() && (userTapInEditor || !ic.isCursorUpdateFromKeyboard(oldSelStart, newSelStart, oldSelEnd, newSelEnd))) {
                ic.resetInternalEditingStates(newSelStart, newSelEnd, !userTapInEditor);
                if ((this.startSelection != newSelStart) && !this.mIme.cursorUpdateReceivedFromSingleTap()) {
                    updateShiftKeyState();
                }
            }
            this.startSelection = newSelStart;
            this.endSelection = newSelEnd;
            if (this.mStarted && ic != null && this.mSpeechWrapper != null) {
                this.oldSelStartCache = oldSelStart;
                this.oldSelEndCache = oldSelEnd;
                this.newSelStartCache = newSelStart;
                this.newSelEndCache = newSelEnd;
                this.candidatesStartCache = candidatesIndices[0];
                this.candidatesEndCache = candidatesIndices[1];
                this.mSpeechWrapper.updateSelection(ic, oldSelStart, oldSelEnd, newSelStart, newSelEnd, candidatesIndices[0], candidatesIndices[1]);
            }
        }
    }

    public boolean handleKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case 4:
                if (event.getRepeatCount() == 0) {
                    return handleBack();
                }
            default:
                return false;
        }
    }

    public boolean handleKeyUp(int keyCode, KeyEvent event) {
        return false;
    }

    public void handleCharKey(Point point, int primaryCode, int[] keyCodes, long eventTime) {
        handleCharKey(point, primaryCode, eventTime);
    }

    public void handleCharKey(Point pointTapped, int primaryCode, long eventTime) {
        recordStartTimeDisplaySelection();
    }

    public void recordStartTimeDisplaySelection() {
        if (StatisticsManager.from(this.mIme) != null && this.mKeyboardSwitcher != null && this.mKeyboardSwitcher.isAlphabetMode()) {
            this.startTimeDisplaySelectionList = System.currentTimeMillis();
        }
    }

    public void recordUsedTimeTapDisplaySelectionList() {
        UsageManager usageMgr = UsageManager.from(this.mIme);
        if (usageMgr != null && this.mKeyboardSwitcher != null && this.mKeyboardSwitcher.isAlphabetMode() && this.startTimeDisplaySelectionList > 0) {
            this.usedTimeListTapDisplaySelection.add(Long.valueOf(System.currentTimeMillis() - this.startTimeDisplaySelectionList));
            this.startTimeDisplaySelectionList = 0L;
            if (this.usedTimeListTapDisplaySelection.size() > MAX_SIZE_SELECTION_LIST) {
                usageMgr.getKeyboardUsageScribe().recordUsedTimeSelectionListDisplay("Tap " + this.mCurrentInputLanguage.mEnglishName + " Total:" + this.usedTimeListTapDisplaySelection.size(), "Average:" + LogManager.calculateAverage(this.usedTimeListTapDisplaySelection) + "ms");
                this.usedTimeListTapDisplaySelection.clear();
            }
        }
    }

    public void recordUsedTimeReselectDisplaySelectionList() {
        UsageManager usageMgr = UsageManager.from(this.mIme);
        if (usageMgr != null && this.mKeyboardSwitcher != null && this.mKeyboardSwitcher.isAlphabetMode() && this.startTimeDisplaySelectionList > 0) {
            this.usedTimeListReselectDisplaySelection.add(Long.valueOf(System.currentTimeMillis() - this.startTimeDisplaySelectionList));
            this.startTimeDisplaySelectionList = 0L;
            if (this.usedTimeListReselectDisplaySelection.size() > MAX_SIZE_SELECTION_LIST) {
                usageMgr.getKeyboardUsageScribe().recordUsedTimeSelectionListDisplay("Reselect " + this.mCurrentInputLanguage.mEnglishName + " Total:" + this.usedTimeListReselectDisplaySelection.size(), "Average:" + LogManager.calculateAverage(this.usedTimeListReselectDisplaySelection) + "ms");
                this.usedTimeListReselectDisplaySelection.clear();
            }
        }
    }

    public void handleTrace(KeyboardViewEx.TracePoints trace) {
    }

    public void handleWrite(List<Point> write) {
    }

    public void onText(CharSequence text, long eventTime) {
    }

    public void onSpeechUpdate(CharSequence text, boolean isOnlyToCommitFinal, boolean isFinal) {
    }

    public boolean handleKey(int primaryCode, boolean quickPressed, int repeatedCount) {
        switch (primaryCode) {
            case 8:
                return handleBackspace(repeatedCount);
            case 32:
                return handleSpace(quickPressed, repeatedCount);
            case KeyboardEx.KEYCODE_KEYBOARD_RESIZE /* 2900 */:
                handleKeyboardResize();
                return true;
            case KeyboardEx.KEYCODE_LANGUAGE_QUICK_SWITCH /* 2939 */:
                if (!UserManagerCompat.isUserUnlocked(this.mIme)) {
                    return true;
                }
                toggleKeyboard(false);
                return true;
            case KeyboardEx.KEYCODE_MULTITAP_DEADKEY /* 2942 */:
                handleMultitapDeadkey();
                return true;
            case KeyboardEx.KEYCODE_SHIFT /* 4068 */:
            case KeyboardEx.KEYCODE_SHIFT_RIGHT /* 6445 */:
                handleShiftKey();
                return true;
            case KeyboardEx.KEYCODE_EMOTICON /* 4074 */:
                handleEmotionKey();
                return true;
            case KeyboardEx.KEYCODE_MODE_CHANGE /* 6462 */:
                changeKeyboardMode();
                return true;
            case KeyboardEx.KEYCODE_SWYPE /* 43575 */:
                if (!isValidBuild()) {
                    return true;
                }
                setSwypeKeytoolTipSuggestion();
                return true;
            case 43577:
                return true;
            default:
                return false;
        }
    }

    public void handleEmotionKey() {
        selectDefaultSuggestion(StatisticsEnabledEditState.DefaultSelectionType.GENERIC);
        this.mIme.showEmojiInputView();
    }

    public void handlePostTap(Point point, int primaryCode) {
        AppSpecificInputConnection ic;
        if ((primaryCode == -200 || primaryCode == -118) && point == null && this.mIme.isAccessibilitySupportEnabled() && (ic = getCurrentInputConnection()) != null) {
            ic.beginBatchEdit();
            ic.commitText(":-)", 1);
            ic.endBatchEdit();
        }
    }

    public void handleClose() {
        long distance;
        log.d("handleClose..", "getLeft()..", Integer.valueOf(getLeft()), "..getRight()..", Integer.valueOf(getRight()));
        cancelSpeech();
        this.mStarted = false;
        StatisticsManager statsMgr = StatisticsManager.from(getContext());
        if (statsMgr != null) {
            if (this.swypeDistanceSum != 0.0f) {
                int distanceRounded = Math.round(this.swypeDistanceSum);
                try {
                    distance = AppPreferences.from(getContext()).getLong(AppPreferences.TOTAL_SWYPE_DISTANCE, 0L);
                } catch (ClassCastException e) {
                    distance = 0;
                }
                AppPreferences.from(getContext()).setLong(AppPreferences.TOTAL_SWYPE_DISTANCE, distance + distanceRounded);
                statsMgr.getSessionStatsScribe().trackDistanceSwyped(distanceRounded);
            }
            this.swypeDistanceSum = 0.0f;
        }
        UsageManager usageMgr = UsageManager.from(this.mIme);
        if (usageMgr != null) {
            this.startTimeDisplaySelectionList = 0L;
            if (this.usedTimeListTapDisplaySelection.size() > 0) {
                usageMgr.getKeyboardUsageScribe().recordUsedTimeSelectionListDisplay("Tap " + this.mCurrentInputLanguage.mEnglishName + " Total:" + this.usedTimeListTapDisplaySelection.size(), "Average:" + LogManager.calculateAverage(this.usedTimeListTapDisplaySelection) + "ms");
                log.d("Tap ", this.mCurrentInputLanguage.mEnglishName, " Total:", Integer.valueOf(this.usedTimeListTapDisplaySelection.size()), " Average:", LogManager.calculateAverage(this.usedTimeListTapDisplaySelection), "ms");
                this.usedTimeListTapDisplaySelection.clear();
            }
            if (this.usedTimeListReselectDisplaySelection.size() > 0) {
                usageMgr.getKeyboardUsageScribe().recordUsedTimeSelectionListDisplay("Reselect " + this.mCurrentInputLanguage.mEnglishName + " Total:" + this.usedTimeListReselectDisplaySelection.size(), "Average:" + LogManager.calculateAverage(this.usedTimeListReselectDisplaySelection) + "ms");
                this.usedTimeListReselectDisplaySelection.clear();
            }
        }
        if (getResources().getBoolean(R.bool.enable_china_connect_special) && this.mNetworkPromptMessage != null && this.mNetworkPromptMessage.isShowing()) {
            this.mNetworkPromptMessage.dismiss();
            this.mNetworkPromptMessage = null;
        }
        removeAllMessages();
    }

    public boolean handleBackspace(int repeatedCount) {
        return false;
    }

    public void onMultitapTimeout() {
    }

    public void flushCurrentActiveWord() {
    }

    public void displayCompletions(CompletionInfo[] completions) {
        if (this.mCompletionOn && !this.mTextInputPredictionOn) {
            this.mCompletions.update(completions);
            if (this.mCompletions.size() == 0) {
                clearSuggestions();
            } else {
                setSuggestions(this, this.mCompletions.getDisplayItems(), 0, Candidates.Source.COMPLETIONS);
            }
        }
    }

    public boolean handleSpace(boolean quickPressed, int repeatedCount) {
        sendSpace();
        return true;
    }

    public void handleMultitapToggle() {
    }

    protected void handleMultitapDeadkey() {
        this.xt9coreinput.multiTapTimeOut();
    }

    public void handleShiftKey() {
    }

    public void handleKeyWithModifiers(int keyCode) {
        handleKeyWithModifiers(keyCode, false);
    }

    public void handleKeyWithModifiers(int keyCode, boolean forceAlt) {
        AppSpecificInputConnection ic;
        int keyCode2;
        int metaState;
        int metaState2;
        if (this.mIme != null && (ic = (AppSpecificInputConnection) this.mIme.getCurrentInputConnection()) != null) {
            switch (keyCode) {
                case KeyboardEx.KEYCODE_ARROW_LEFT /* 4029 */:
                    keyCode2 = 21;
                    this.mLastInput = 1;
                    if (isExploreByTouchOn()) {
                        playSoundIfTextIsEmpty();
                        break;
                    }
                    break;
                case KeyboardEx.KEYCODE_ARROW_UP /* 4045 */:
                    keyCode2 = 19;
                    this.mLastInput = 1;
                    break;
                case KeyboardEx.KEYCODE_ARROW_RIGHT /* 4059 */:
                    keyCode2 = 22;
                    this.mLastInput = 1;
                    break;
                case KeyboardEx.KEYCODE_ARROW_DOWN /* 4060 */:
                    keyCode2 = 20;
                    this.mLastInput = 1;
                    break;
                default:
                    return;
            }
            if (isComposingText()) {
                log.d("cursor handleKeyWithModifiers");
                flushCurrentActiveWord();
            }
            boolean shifted = isEditModeSelectKeyOn();
            long eventTime = SystemClock.uptimeMillis();
            switch (keyCode2) {
                case 19:
                    if (getAppSpecificBehavior().shouldEditModeUseAlternativeSelectMethod()) {
                        InputConnection inputConnection = ic.getTarget();
                        ExtractedText et = inputConnection.getExtractedText(new ExtractedTextRequest(), 0);
                        if (shifted) {
                            inputConnection.setSelection(et.selectionStart, 0);
                            return;
                        } else {
                            inputConnection.setSelection(0, 0);
                            return;
                        }
                    }
                    break;
                case 20:
                    if (getAppSpecificBehavior().shouldEditModeUseAlternativeSelectMethod()) {
                        InputConnection inputConnection2 = ic.getTarget();
                        ExtractedText et2 = inputConnection2.getExtractedText(new ExtractedTextRequest(), 0);
                        if (shifted) {
                            inputConnection2.setSelection(et2.selectionStart, et2.text.length());
                            return;
                        } else {
                            int pos = et2.text.length();
                            inputConnection2.setSelection(pos, pos);
                            return;
                        }
                    }
                    break;
                case 21:
                    if (getAppSpecificBehavior().shouldEditModeUseAlternativeSelectMethod()) {
                        InputConnection inputConnection3 = ic.getTarget();
                        ExtractedText et3 = inputConnection3.getExtractedText(new ExtractedTextRequest(), 0);
                        if (shifted) {
                            inputConnection3.setSelection(et3.selectionStart, et3.selectionEnd + (-1) >= 0 ? et3.selectionEnd - 1 : 0);
                            return;
                        } else {
                            int pos2 = et3.selectionStart + (-1) >= 0 ? et3.selectionStart - 1 : 0;
                            inputConnection3.setSelection(pos2, pos2);
                            return;
                        }
                    }
                    break;
                case 22:
                    if (getAppSpecificBehavior().shouldEditModeUseAlternativeSelectMethod()) {
                        InputConnection inputConnection4 = ic.getTarget();
                        ExtractedText et4 = inputConnection4.getExtractedText(new ExtractedTextRequest(), 0);
                        int length = et4.text.length();
                        if (shifted) {
                            int i = et4.selectionStart;
                            if (et4.selectionEnd + 1 <= length) {
                                length = et4.selectionEnd + 1;
                            }
                            inputConnection4.setSelection(i, length);
                            return;
                        }
                        int pos3 = et4.selectionStart + 1 <= length ? et4.selectionStart + 1 : length;
                        inputConnection4.setSelection(pos3, pos3);
                        return;
                    }
                    break;
            }
            if (!forceAlt) {
                metaState = 0;
            } else {
                ic.sendKeyEvent(new KeyEvent(eventTime, eventTime, 0, 57, 0, 0, 0, 0, 6));
                metaState = 50;
            }
            if (shifted) {
                ic.sendKeyEvent(new KeyEvent(eventTime, eventTime, 0, 59, 0, 0, 0, 0, 6));
                metaState2 = metaState | 64 | 128 | 1;
            } else {
                metaState2 = metaState;
            }
            ic.sendKeyEvent(new KeyEvent(eventTime, eventTime, 0, keyCode2, 0, metaState2, 0, 0, 6));
            ic.sendKeyEvent(new KeyEvent(eventTime, SystemClock.uptimeMillis(), 1, keyCode2, 0, metaState2, 0, 0, 6));
            if (forceAlt) {
                ic.sendKeyEvent(new KeyEvent(eventTime, eventTime, 1, 57, 0, 0, 0, 0, 6));
            }
            if (shifted) {
                ic.sendKeyEvent(new KeyEvent(eventTime, eventTime, 1, 59, 0, 0, 0, 0, 6));
            }
        }
    }

    public void changeKeyboardMode() {
    }

    public void toggleKeyboard(boolean isImplicit) {
        if (IMEApplication.from(this.mIme).getIMEHandlerManager() != null) {
            this.mIme.handlerManager.fastSwitchLanguage(isImplicit);
        }
    }

    public void setCandidatesViewShown(boolean shown) {
        if (this.mIme != null) {
            this.mIme.setCandidatesViewShown(shown && this.mIme.isImeInUse());
        }
    }

    protected boolean isLeftCursorWordWhiteSpace(CharSequence wordJustSelected) {
        CharSequence seqBeforeCursor;
        InputConnection ic = this.mIme.getCurrentInputConnection();
        if (ic == null || (seqBeforeCursor = ic.getTextBeforeCursor(65, 0)) == null) {
            return true;
        }
        CharSequence seqBeforeCursor2 = seqBeforeCursor.toString().trim();
        int index = seqBeforeCursor2.length() - 1;
        if (index <= 0) {
            return true;
        }
        while (index > 0 && !CharacterUtilities.isWhiteSpace(seqBeforeCursor2.charAt(index))) {
            index--;
        }
        if (CharacterUtilities.isWhiteSpace(seqBeforeCursor2.charAt(index))) {
            index++;
        }
        String compoundWord = seqBeforeCursor2.toString().substring(index);
        return compoundWord.length() <= wordJustSelected.length() || compoundWord.length() > 64;
    }

    public boolean isLikelyDomain(CharSequence text) {
        String domain = text.toString();
        return domain.startsWith(".com") || domain.startsWith(".net") || domain.startsWith(".org") || domain.startsWith(".gov") || domain.startsWith(".edu") || domain.startsWith(".tv");
    }

    public void sendKeyToApplication(int keyCode) {
        InputConnection ic = getCurrentInputConnection();
        if (ic != null) {
            ic.sendKeyEvent(new KeyEvent(0, keyCode));
            ic.sendKeyEvent(new KeyEvent(1, keyCode));
        }
    }

    public void sendBackspace(int repeatedCount) {
        if (this.mIme != null) {
            this.mIme.sendBackspace(repeatedCount);
        }
        this.mLastInput = 1;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void sendTextAsKeys(CharSequence text) {
        if (!TextUtils.isEmpty(text) && this.mIme != null) {
            int textLength = text.length();
            for (int i = 0; i < textLength; i++) {
                this.mIme.sendKeyChar(text.charAt(i));
            }
        }
    }

    public void sendKeyChar(char character) {
        if (this.mIme != null) {
            this.mIme.sendCharOrPerformAction(character);
        }
        this.mLastInput = 1;
    }

    public void sendSpace() {
        sendKeyChar(' ');
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void resetAutoSpace() {
        boolean z;
        if (this.mCurrentInputLanguage.isAutoSpaceSupported()) {
            UserPreferences userPrefs = UserPreferences.from(getContext());
            if (userPrefs.getAutoSpace() && !notATextInputField(this.mInputFieldInfo)) {
                z = true;
                this.mAutoSpace = z;
            }
        }
        z = false;
        this.mAutoSpace = z;
    }

    private boolean notATextInputField(InputFieldInfo inputFieldInfo) {
        return inputFieldInfo.isNumericModeField() || inputFieldInfo.isPhoneNumberField() || inputFieldInfo.isPasswordField();
    }

    /* JADX WARN: Removed duplicated region for block: B:52:0x00dd  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void readSettings(com.nuance.swype.input.InputFieldInfo r9) {
        /*
            Method dump skipped, instructions count: 372
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.nuance.swype.input.InputView.readSettings(com.nuance.swype.input.InputFieldInfo):void");
    }

    private void refreshEmojiChoiceListEnable() {
        AppSpecificBehavior appSpecificBehavior = getAppSpecificBehavior();
        UserPreferences userPrefs = UserPreferences.from(getContext());
        this.mEnableEmojiChoiceList = (!userPrefs.isEmojiChoiceListEnabled() || this.mCurrentInputLanguage == null || this.mCurrentInputLanguage.isBilingualLanguage() || isOrientationLandscape() || !isValidBuild() || appSpecificBehavior.shouldDisableEmojiCandidatesList() || this.mInputFieldInfo.isNumberField() || this.mIme.isAccessibilitySupportEnabled() || this.mInputFieldInfo.isEmailAddressField()) ? false : true;
    }

    public void refreshBTAutoCorrection() {
        UserPreferences userPrefs = UserPreferences.from(getContext());
        this.autoCorrectionEnabled = userPrefs.getAutoCorrection() && !this.mInputFieldInfo.isNameField();
        this.mKeyboardInputSuggestionOn = this.autoCorrectionEnabled || this.mWordCompletionPoint != 0;
    }

    public boolean isAutoReturnToEditorDefaultLayerEnabled() {
        return this.isAutoReturnToEditorDefaultLayerEnabled;
    }

    private void readLongPressDuration() {
        this.mLongPressTimeout = UserPreferences.from(this.mIme).getLongPressDelay();
        this.mShortLongPressTimeout = this.mLongPressTimeout;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void readNextWordPredictionSettings() {
        this.mNextWordPredictionOn = this.mTextInputPredictionOn && !this.mInURLEmail && UserPreferences.from(getContext()).isNextWordPredictionEnabled();
    }

    @Override // com.nuance.swype.input.KeyboardViewEx
    public boolean handleLongPress(KeyboardEx.Key key) {
        if (!this.mStarted) {
            return true;
        }
        if (getOnKeyboardActionListener() == null || key.codes.length == 0) {
            return super.handleLongPress(key);
        }
        switch (key.codes[0]) {
            case KeyboardEx.KEYCODE_LANGUAGE_QUICK_SWITCH /* 2939 */:
                getOnKeyboardActionListener().onKey(null, KeyboardEx.KEYCODE_LANGUAGE_MENU, null, key, 0L);
                return true;
            case KeyboardEx.KEYCODE_MULTITAP_TOGGLE /* 2940 */:
                handleMultitapToggle();
                return true;
            case KeyboardEx.KEYCODE_XT9_LANGUAGE_CYCLING /* 2941 */:
                getOnKeyboardActionListener().onKey(null, KeyboardEx.KEYCODE_LANGUAGE_MENU, null, key, 0L);
                return true;
            default:
                if (key.codes[0] == 39 && this.mIsDelimiterKeyLabelUpdated) {
                    return false;
                }
                int keyCode = getLongPressKeycode(key);
                switch (keyCode) {
                    case KeyboardEx.KEYCODE_ACTIONONKEYBOARD_MENU /* 6446 */:
                        getOnKeyboardActionListener().onKey(null, KeyboardEx.KEYCODE_ACTIONONKEYBOARD_MENU, null, key, 0L);
                        return true;
                    case KeyboardEx.KEYCODE_INPUTMODE_MENU /* 6447 */:
                        getOnKeyboardActionListener().onKey(null, KeyboardEx.KEYCODE_INPUTMODE_MENU, null, key, 0L);
                        return true;
                    case KeyboardEx.KEYCODE_SWITCH_WRITE_SCREEN /* 43579 */:
                        this.mIme.handlerManager.toggleFullScreenHW();
                        return true;
                    default:
                        boolean handled = super.handleLongPress(key);
                        if (!handled && keyCode != 4063 && keyCode == key.altCode) {
                            clearKeyboardState();
                            getOnKeyboardActionListener().onKey(null, keyCode, key.codes, key, 0L);
                            return true;
                        }
                        return handled;
                }
        }
    }

    public void setEditState(EditState editState) {
        this.mEditState = editState;
    }

    public boolean handleDeleteWordBack(KeyboardEx.Key key) {
        String deletedStr;
        AppSpecificInputConnection ic = getCurrentInputConnection();
        if (ic != null) {
            String cs = new StringBuilder().append((Object) ic.getTextBeforeCursor(256, 0)).toString();
            int[] selection = ic.getSelectionRangeInEditor();
            boolean hasSelection = false;
            if (selection != null && !TextUtils.isEmpty(cs)) {
                hasSelection = selection[0] != selection[1];
            }
            if (TextUtils.isEmpty(cs)) {
                if (this.mIme != null) {
                    this.mIme.sendBackspace(1);
                    updateShiftKeyState();
                    if (this.mIme.isAccessibilitySupportEnabled()) {
                        playEndOfListSound();
                    }
                    if (isEmptyCandidateList()) {
                        showNextWordPrediction();
                    }
                }
                return false;
            }
            ic.beginBatchEdit();
            ic.finishComposingText();
            if (hasSelection) {
                ic.commitText("", 1);
            } else {
                UsageManager usageMgr = UsageManager.from(this.mIme);
                String[] parts = cs.trim().split("\\s+");
                CharSequence lastChar = ic.getTextBeforeCursor(2, 0);
                if (parts.length > 1) {
                    int lastIndex = cs.lastIndexOf(parts[parts.length - 1]);
                    int editablelength = cs.length();
                    int beforeLen = editablelength > lastIndex ? editablelength - lastIndex : 0;
                    deletedStr = cs.subSequence(cs.length() - beforeLen, cs.length()).toString();
                    if (usageMgr != null && cs.length() >= beforeLen) {
                        usageMgr.getKeyboardUsageScribe().recordDeletedWord(deletedStr);
                    }
                    if (lastChar != null && Character.isLetterOrDigit(lastChar.charAt(0))) {
                        ic.deleteSurroundingText(beforeLen, 0);
                    } else {
                        sendBackspace(this.repeatCount);
                    }
                } else {
                    deletedStr = cs;
                    if (usageMgr != null) {
                        usageMgr.getKeyboardUsageScribe().recordDeletedWord(cs);
                    }
                    if (lastChar != null && !Character.isLetterOrDigit(lastChar.charAt(0))) {
                        sendBackspace(this.repeatCount);
                    } else {
                        ic.deleteSurroundingText(deletedStr.length(), 0);
                    }
                }
                if (this.mIme.isAccessibilitySupportEnabled() && !TextUtils.isEmpty(deletedStr) && getKeyboardAccessibilityState().shouldSpeakForPassword()) {
                    AccessibilityNotification.getInstance().announceNotification(getContext(), String.format(this.wordDeletedString, deletedStr), true);
                }
            }
            ic.endBatchEdit();
            clearSuggestions();
            updateShiftKeyState();
            return true;
        }
        return false;
    }

    protected int getLongPressKeycode(KeyboardEx.Key key) {
        if (isShifted() || key.shiftCode == 4063) {
            return key.shiftTransition != KeyboardEx.ShiftTransition.DROP_HIDE ? key.altCode : KeyboardEx.KEYCODE_INVALID;
        }
        return key.shiftCode;
    }

    public void setOnKeyboardActionListener(IME ime) {
        EditState state;
        KeyboardViewEx.OnKeyboardActionListener decorated = ime;
        if (ime != null && Connect.from(getContext()).isStatisticsCollectionEnabled() && (state = ime.getEditState()) != null && (state instanceof StatisticsEnabledEditState)) {
            decorated = ((StatisticsEnabledEditState) state).watchOnKeyboardActionListener(decorated);
        }
        super.setOnKeyboardActionListener(decorated);
    }

    @Override // com.nuance.swype.input.KeyboardViewEx, android.view.View
    public boolean onTouchEvent(MotionEvent me) {
        if (this.mStarted) {
            return super.onTouchEvent(me);
        }
        return true;
    }

    public void onApplicationUnbind() {
        this.mStarted = false;
        closing();
        if (this.mSpeechWrapper != null) {
            this.mSpeechWrapper.cancelSpeech();
        }
    }

    public InputFieldInfo getInputFieldInfo() {
        return this.mInputFieldInfo;
    }

    public void setKeyboardLayer(KeyboardEx.KeyboardLayerType layer) {
        UsageManager usageMgr;
        if (getKeyboardLayer() != layer && (usageMgr = UsageManager.from(getContext())) != null) {
            usageMgr.getKeyboardUsageScribe().recordKeyboardLayerChange(getKeyboardLayer(), layer);
        }
    }

    @Override // com.nuance.swype.input.KeyboardViewEx
    protected boolean isSymbolSelectPopupEnabledForCurrentLayer() {
        if (this.enableSymbolSelectPopupAllLayers || this.mKeyboardSwitcher == null) {
            return true;
        }
        return KeyboardEx.KeyboardLayerType.KEYBOARD_TEXT.equals(this.mKeyboardSwitcher.currentKeyboardLayer());
    }

    public KeyboardEx.KeyboardLayerType getKeyboardLayer() {
        return this.mKeyboardSwitcher != null ? this.mKeyboardSwitcher.currentKeyboardLayer() : KeyboardEx.KeyboardLayerType.KEYBOARD_INVALID;
    }

    public void updateExtractedText(int token, ExtractedText text) {
    }

    protected void updateExtractedText() {
        InputConnection ic;
        if (this.mStarted && (ic = getCurrentInputConnection()) != null) {
            ExtractedTextRequest request = new ExtractedTextRequest();
            request.flags = 1;
            ExtractedText text = ic.getExtractedText(request, 0);
            if (text != null) {
                if (this.startSelection != text.selectionStart + text.startOffset) {
                    this.startSelection = text.selectionStart + text.startOffset;
                }
                if (this.endSelection != text.selectionEnd) {
                    this.endSelection = text.selectionEnd + text.startOffset;
                }
            }
            if (text != null && text.text != null && this.mSpeechWrapper != null) {
                this.mSpeechWrapper.updateText(text.text.toString(), text.selectionStart, text.selectionEnd);
            }
        }
    }

    public CharSequence getTextBufferCursor(int max) {
        InputConnection ic = getCurrentInputConnection();
        if (ic != null) {
            return ic.getTextBeforeCursor(max, 0);
        }
        return null;
    }

    public CharSequence getContextBuffer() {
        return getTextBufferCursor(129);
    }

    private void setContextBuffer(CharSequence textBufferBeforeCursor) {
        if (textBufferBeforeCursor != null) {
            this.xt9coreinput.setContext(textBufferBeforeCursor.toString().toCharArray());
        }
    }

    public void updateWordContext() {
        setContextBuffer(getContextBuffer());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void learnNewWords() {
        ExtractedText extractedText;
        AppSpecificInputConnection ic = getCurrentInputConnection();
        if (ic != null && (extractedText = ic.getExtractedText(new ExtractedTextRequest(), 0)) != null && !TextUtils.isEmpty(extractedText.text)) {
            this.xt9coreinput.noteWordDone(extractedText.text.toString(), Math.max(extractedText.selectionEnd, extractedText.selectionStart));
        }
    }

    public final void startSpeech() {
        if ((IMEApplication.from(getContext()).getSpeechProvider() == 0) && ContextCompat.checkSelfPermission(getContext(), "android.permission.RECORD_AUDIO") != 0) {
            Intent intent = new Intent(getContext(), (Class<?>) PermissionRequestActivity.class);
            intent.addFlags(268435456);
            this.mIme.startActivity(intent);
        } else if (isValidBuild()) {
            UserPreferences userPrefs = UserPreferences.from(this.mIme);
            if (this.mIme.getResources().getBoolean(R.bool.enable_china_connect_special) && userPrefs.shouldShowNetworkAgreementDialog()) {
                this.mNetworkPromptMessage = ChinaNetworkNotificationDialog.create(this.mIme, this.cnNwDlgListener);
                this.mIme.attachDialog(this.mNetworkPromptMessage);
                this.mNetworkPromptMessage.show();
                return;
            }
            actualStartSpeech();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void actualStartSpeech() {
        if (isValidBuild()) {
            if (IMEApplication.from(this.mIme).getSpeechProvider() == 1) {
                this.mIme.startVoiceRecognition(this.mCurrentInputLanguage.getIETFLanguageTag());
            } else if (!ActivityManagerCompat.isUserAMonkey() && this.mSpeechWrapper != null) {
                this.mSpeechWrapper.startSpeech(this, getSpeechPopupRectInWindowCoord());
            }
        }
    }

    public void resumeSpeech() {
        if (this.mSpeechWrapper != null && this.mIme != null) {
            if (this.mSpeechWrapper.isResumable()) {
                log.d("cursor resumeSpeech");
                flushCurrentActiveWord();
            }
            this.mSpeechWrapper.resumeSpeech(this, getSpeechPopupRectInWindowCoord());
        }
    }

    public Rect getSpeechPopupRectInWindowCoord() {
        if (this.mIme != null) {
            return this.mIme.getSpeechPopupRectInWindowCoord();
        }
        return null;
    }

    public boolean isSpeechViewShowing() {
        return this.mSpeechWrapper != null && this.mSpeechWrapper.isSpeechViewShowing();
    }

    public void setSpeechViewHost() {
        if (this.mSpeechWrapper != null) {
            this.mSpeechWrapper.setHost(this);
        }
    }

    protected void stopSpeech() {
        if (this.mSpeechWrapper != null) {
            this.mSpeechWrapper.stopSpeech();
        }
        if (this.mIme != null) {
            this.mIme.closeDictationLanguageMenu();
        }
    }

    protected void pauseSpeech() {
        if (this.mSpeechWrapper != null) {
            this.mSpeechWrapper.pauseSpeech();
        }
        if (this.mIme != null) {
            this.mIme.closeDictationLanguageMenu();
        }
    }

    protected void cancelSpeech() {
        if (this.mSpeechWrapper != null) {
            this.mSpeechWrapper.cancelSpeech();
        }
    }

    protected void destroySpeechWrapper() {
        if (this.mSpeechWrapper != null) {
            stopSpeech();
            this.mSpeechWrapper.onDestroy();
        }
    }

    public boolean isSupportRecapture() {
        return true;
    }

    public boolean isMiniKeyboardMode() {
        return UserPreferences.from(getContext()).getKeyboardDockingMode() == KeyboardEx.KeyboardDockMode.MOVABLE_MINI;
    }

    public boolean isDockMiniKeyboardMode() {
        KeyboardEx.KeyboardDockMode mode = UserPreferences.from(getContext()).getKeyboardDockingMode();
        return mode == KeyboardEx.KeyboardDockMode.DOCK_LEFT || mode == KeyboardEx.KeyboardDockMode.DOCK_RIGHT;
    }

    public List<CharSequence> getSpeechAlternateCandidates() {
        List<CharSequence> result = null;
        if (this.mSpeechWrapper != null) {
            result = this.mSpeechWrapper.getAlternateCandidates();
        }
        if (result != null) {
            return result;
        }
        List<CharSequence> result2 = new ArrayList<>();
        return result2;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isHighlightedTextSpeechDictated() {
        return this.mSpeechWrapper != null && this.mSpeechWrapper.isHighlightedTextSpeechDictated();
    }

    public void speechChooseCandidate(int index) {
        if (this.mSpeechWrapper != null) {
            this.mSpeechWrapper.speechChooseCandidate(index);
        }
    }

    public boolean isLanguageSwitchableOnSpace() {
        return false;
    }

    public void setSwypeKeytoolTipSuggestion() {
        if (this.candidatesListView != null && UserManagerCompat.isUserUnlocked(this.mIme)) {
            this.suggestionCandidates = this.candidatesListView.setSuggestions(IMEApplication.from(getContext()).getToolTips().createSwypeKeyTip(), CandidatesListView.Format.NONE);
            syncCandidateDisplayStyleToMode();
            setCandidatesViewShown(true);
        }
    }

    protected int getContainerX() {
        ViewGroup.LayoutParams lp = getLayoutParams();
        if (lp instanceof FrameLayout.LayoutParams) {
            return ((FrameLayout.LayoutParams) lp).leftMargin;
        }
        return 0;
    }

    public void syncCandidateDisplayStyleToMode() {
        KeyboardEx keyboard = getKeyboard();
        boolean isSplit = (keyboard != null ? keyboard.getKeyboardDockMode() : UserPreferences.from(getContext()).getKeyboardDockingMode()) == KeyboardEx.KeyboardDockMode.DOCK_SPLIT;
        if (this.candidatesListView != null) {
            this.candidatesListView.setFullScroll(isSplit);
            this.candidatesListView.setAlignLeft(isSplit);
            this.candidatesListView.invalidate();
        }
        if (this.emojiCandidatesListView != null) {
            this.emojiCandidatesListView.setFullScroll(isSplit);
            this.emojiCandidatesListView.setAlignLeft(isSplit);
            this.emojiCandidatesListView.invalidate();
        }
        if (this.candidatesListViewCJK != null) {
            this.candidatesListViewCJK.setFullScroll(isSplit);
            this.candidatesListViewCJK.setAlignLeft(isSplit);
            this.candidatesListViewCJK.invalidate();
        }
        updateEmojiKeyboardPosition();
    }

    public boolean isShowingCandidatesForOneOf(Candidates.Source... types) {
        if (this.suggestionCandidates == null) {
            return false;
        }
        for (Candidates.Source type : types) {
            if (this.suggestionCandidates.source() == type) {
                return true;
            }
        }
        return false;
    }

    public boolean isShowingCandidatesFor(Candidates.Source type) {
        return this.suggestionCandidates != null && this.suggestionCandidates.source() == type;
    }

    public void setSuggestions(CandidatesListView.CandidateListener listener, Candidates candidates, CandidatesListView.Format format) {
        if (!isActiveWCL()) {
            setCommonSuggestion(listener, candidates, format);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setCommonSuggestion(CandidatesListView.CandidateListener listener, Candidates candidates, CandidatesListView.Format format) {
        this.suggestionCandidates = null;
        this.pendingCandidateSource = Candidates.Source.INVALID;
        if (this.candidatesListView != null) {
            this.candidatesListView.setCandidateListener(listener);
            this.suggestionCandidates = this.candidatesListView.setSuggestions(candidates, format);
            setCandidatesViewShown(true);
            syncCandidateDisplayStyleToMode();
        }
        updateSuggestionsEmoji(format);
    }

    protected void updateSuggestionsEmoji(CandidatesListView.Format format) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setCandidateListener(CandidatesListView.CandidateListener listener) {
        if (this.candidatesListView != null) {
            this.candidatesListView.setCandidateListener(listener);
        }
    }

    public void setSuggestions(CandidatesListView.CandidateListener listener, List<CharSequence> suggestions, int defaultWordIndex, Candidates.Source source) {
        Candidates candidates = Candidates.createCandidates(suggestions, defaultWordIndex, source);
        setSuggestions(listener, candidates, getWordListFormat(candidates));
    }

    public void clearSuggestions() {
        if (this.mIme != null && isValidBuild() && !isActiveWCL()) {
            this.suggestionCandidates = null;
            this.pendingCandidateSource = Candidates.Source.INVALID;
            if (this.candidatesListView != null) {
                this.candidatesListView.setSuggestions(null, CandidatesListView.Format.NONE);
                syncCandidateDisplayStyleToMode();
            }
            clearAllKeys();
        }
    }

    public void clearAllKeys() {
    }

    public void setNotMatchToolTipSuggestion() {
        this.suggestionCandidates = this.candidatesListView.setSuggestions(IMEApplication.from(getContext()).getToolTips().createNoMatchTip(), CandidatesListView.Format.NONE);
        syncCandidateDisplayStyleToMode();
        setCandidatesViewShown(true);
    }

    public CandidatesListView.Format getWordListFormat(Candidates candidates) {
        return IMEApplication.from(getContext()).isScreenLayoutTablet() ? CandidatesListView.Format.FIT_AS_MUCH : CandidatesListView.Format.DEFAULT;
    }

    public boolean onSelectCandidate(WordCandidate candidate, Candidates candidates) {
        UsageData.EmojiSelectedSource emojiSelectedSource;
        Candidates.Source source = candidates.source();
        if (source == Candidates.Source.EMOJEENIE) {
            emojiSelectedSource = UsageData.EmojiSelectedSource.EMOJEENIE;
        } else if (this.xt9coreinput.isLikelyEmoji(candidate.toString())) {
            emojiSelectedSource = source == Candidates.Source.NEXT_WORD_PREDICTION ? UsageData.EmojiSelectedSource.NWP : UsageData.EmojiSelectedSource.SHORTCUT;
        } else {
            return false;
        }
        UsageData.recordEmojiSelected(emojiSelectedSource);
        return false;
    }

    public boolean onPressHoldCandidate(WordCandidate candidate, Candidates candidates) {
        return false;
    }

    public boolean onPressReleaseCandidate(WordCandidate candidate, Candidates candidates) {
        return false;
    }

    public void onPressMoveCandidate(float xPos, float yPos, float xOffset) {
    }

    public void onCandidatesUpdated(Candidates candidates) {
    }

    public void onSpeechStarted() {
    }

    public void onSpeechPaused() {
    }

    public void onSpeechStopped() {
    }

    public void onSpeechResumed() {
    }

    public void onSpeechViewShowedUp() {
        this.preProcessOnSpeechDictation = true;
    }

    public void onSpeechViewDismissed() {
        this.preProcessOnSpeechDictation = false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int getLastInput() {
        return this.mLastInput;
    }

    public void setLastInput(int lastInput) {
        this.mLastInput = lastInput;
    }

    public boolean isEmptyCandidateList() {
        return isEmptyWCL() || isReadOnlyCandidateList();
    }

    protected boolean isReadOnlyCandidateList() {
        return this.suggestionCandidates == null || this.suggestionCandidates.isReadOnly();
    }

    protected boolean isEmptyWCL() {
        return this.suggestionCandidates == null || this.suggestionCandidates.count() == 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.KeyboardViewEx
    public boolean currentLanguageSupportsCapitalization() {
        if (this.mCurrentInputLanguage != null) {
            return this.mCurrentInputLanguage.isCapitalizationSupported();
        }
        return true;
    }

    public boolean handleGesture(Candidates candidates) {
        return candidates.count() > 0 && this.gestureHandler.handle(candidates.getDefaultCandidate());
    }

    public boolean handleGesture(int gestureCode) {
        return this.gestureHandler.handle(gestureCode);
    }

    public boolean isGesture(Candidates candidates) {
        return candidates != null && candidates.count() > 0 && candidates.getDefaultCandidate().source() == WordCandidate.Source.WORD_SOURCE_GESTURE;
    }

    @Override // com.nuance.swype.input.KeyboardViewEx
    public void setKeyboard(KeyboardEx keyboard) {
        super.setKeyboard(keyboard);
        this.selectKey = keyboard.findKey(43577);
        if (this.selectKey != null && this.selectKey.icon != null) {
            if (isMiniKeyboardMode()) {
                this.selectKey.icon.setLevel(2);
            } else {
                this.selectKey.icon.setLevel(0);
            }
        }
        syncCandidateDisplayStyleToMode();
    }

    public boolean hasEditModeSelectKey() {
        return this.selectKey != null && this.selectKey.visible;
    }

    private boolean isEditModeSelectKeyOn() {
        return hasEditModeSelectKey() && this.selectKey.on;
    }

    public void closeDialogs() {
    }

    public int updateSuggestions(Candidates.Source source, boolean noComposingText) {
        Candidates candidates = this.xt9coreinput.getCandidates(source);
        setSuggestions(this, candidates, getWordListFormat(candidates));
        if (candidates != null) {
            return candidates.count();
        }
        return 0;
    }

    public int updateSuggestions(Candidates.Source source) {
        return updateSuggestions(source, source == Candidates.Source.NEXT_WORD_PREDICTION);
    }

    public boolean hasPendingSuggestionsUpdate() {
        return false;
    }

    public void clearPendingSuggestionsUpdate() {
    }

    public boolean onSingleTap(boolean shouldResyncCache, boolean orientationChanged) {
        this.mLastInput = 3;
        return false;
    }

    @Override // com.nuance.swype.input.AbstractTapDetector.TapHandler
    public boolean onDoubleTap() {
        this.mLastInput = 3;
        return false;
    }

    public boolean shouldShowTips() {
        return this.mKeyboardSwitcher.isAlphabetMode() && isTraceInputEnabled();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void triggerAutoDefaultCandidateAcceptTip() {
        if (shouldShowTips() && this.mLastInput == 2) {
            ToolTips.from(getContext()).triggerAutoAcceptTip(this);
        }
    }

    public void triggerPasswordTip() {
        if (shouldShowTips()) {
            ToolTips.from(getContext()).triggerPasswordTip(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void triggerPunctuationGestureTip() {
        if (shouldShowTips()) {
            ToolTips.from(getContext()).triggerPunctGestureTip(this);
        }
    }

    public boolean shouldSelectDefaultCandidate() {
        AppSpecificInputConnection ic;
        boolean bVal = (!this.mTextInputPredictionOn || isEmptyCandidateList() || this.suggestionCandidates.source() == Candidates.Source.RECAPTURE_BY_TEXT_SELECTION || this.suggestionCandidates.source() == Candidates.Source.NEXT_WORD_PREDICTION || this.suggestionCandidates.source() == Candidates.Source.CAPS_EDIT) ? false : true;
        if (bVal && (ic = getCurrentInputConnection()) != null) {
            return (ic.hasSelection() ? false : true) & true;
        }
        return bVal;
    }

    public boolean isPredictionOn() {
        return this.mTextInputPredictionOn;
    }

    public void selectDefaultSuggestion(StatisticsEnabledEditState.DefaultSelectionType type) {
    }

    public void setDefaultWordType(StatisticsEnabledEditState.DefaultSelectionType type) {
    }

    public boolean validateComposingText(int oldSelStart, int oldSelEnd, int newSelStart, int newSelEnd, int[] candidatesIndices, CharSequence composingText) {
        boolean valid = true;
        AppSpecificInputConnection ic = getCurrentInputConnection();
        if (ic == null) {
            return true;
        }
        if ((candidatesIndices[1] != -1 || candidatesIndices[0] != -1) && candidatesIndices[0] == candidatesIndices[1]) {
            return true;
        }
        if ((isHandlingTrace() && getLastInput() == 2 && ic.isCursorUpdateFromKeyboard(oldSelStart, newSelStart, oldSelEnd, newSelEnd)) || !this.mTextInputPredictionOn) {
            return true;
        }
        boolean hasComposingText = candidatesIndices[1] != -1;
        boolean cursorAfterComposingText = newSelStart == newSelEnd && newSelEnd == candidatesIndices[1];
        boolean selectionIsComposingText = newSelStart == candidatesIndices[0] && newSelEnd == candidatesIndices[1];
        if (hasComposingText && !cursorAfterComposingText && !selectionIsComposingText) {
            if (this.suggestionCandidates != null && !TextUtils.isEmpty(composingText)) {
                if (selectingCompostingText(ic, this.suggestionCandidates.getDefaultCandidateString(), composingText)) {
                    candidatesIndices[1] = -1;
                    candidatesIndices[0] = -1;
                }
                valid = false;
            }
        } else if (hasComposingText && TextUtils.isEmpty(composingText)) {
            ic.finishComposingText();
            ic.reSyncFromEditor();
            if (!ic.hasComposing()) {
                candidatesIndices[1] = -1;
                candidatesIndices[0] = -1;
            }
            valid = false;
        } else {
            boolean lostComposingText = candidatesIndices[0] == candidatesIndices[1] && newSelStart == newSelEnd && newSelStart != oldSelStart && !TextUtils.isEmpty(composingText);
            boolean scoopBug3410TempFix = getResources().getBoolean(R.bool.scoop_bug_3410_temp_fix);
            if (!getAppSpecificBehavior().shouldIgnoreLostComposingText() && !scoopBug3410TempFix && lostComposingText) {
                if (this.suggestionCandidates != null) {
                    selectingCompostingText(ic, this.suggestionCandidates.getDefaultCandidateString(), composingText);
                }
                valid = false;
            }
        }
        return valid;
    }

    private boolean selectingCompostingText(InputConnection ic, CharSequence defaultCandidate, CharSequence composingText) {
        int wordSelected;
        if (composingText.equals(this.suggestionCandidates.getDefaultCandidateString())) {
            wordSelected = this.suggestionCandidates.getDefaultCandidateIndex();
        } else {
            wordSelected = this.suggestionCandidates.getExactCandidateIndex();
        }
        this.xt9coreinput.wordSelected(wordSelected, false);
        log.d("cursor selectingCompostingText");
        boolean bRslt = ic.finishComposingText();
        clearSuggestions();
        return bRslt;
    }

    public void selectDefaultCompostingText() {
        if (this.suggestionCandidates != null) {
            this.xt9coreinput.wordSelected(this.suggestionCandidates.getDefaultCandidateIndex(), false);
        }
        InputConnection ic = getCurrentInputConnection();
        if (ic != null) {
            ic.beginBatchEdit();
            ic.finishComposingText();
            clearSuggestions();
            ic.endBatchEdit();
        }
    }

    public boolean isInsideWord() {
        InputConnection ic;
        if (isComposingText() || (ic = getCurrentInputConnection()) == null) {
            return false;
        }
        CharSequence cSeqBefore = ic.getTextBeforeCursor(1, 0);
        if (TextUtils.isEmpty(cSeqBefore) || CharacterUtilities.isWhiteSpace(cSeqBefore.charAt(0))) {
            return false;
        }
        CharSequence cSeqAfter = ic.getTextAfterCursor(1, 0);
        return (TextUtils.isEmpty(cSeqAfter) || CharacterUtilities.isWhiteSpace(cSeqAfter.charAt(0))) ? false : true;
    }

    public boolean isComposingText() {
        Candidates.Source currentSource = getCurrentWordCandidatesSource();
        return (!this.xt9coreinput.hasActiveInput() || currentSource == Candidates.Source.RECAPTURE_BY_TEXT_SELECTION || currentSource == Candidates.Source.NEXT_WORD_PREDICTION || currentSource == Candidates.Source.UDB_EDIT) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isTraceComposingText() {
        Candidates.Source currentSource = getCurrentWordCandidatesSource();
        return this.xt9coreinput.hasActiveInput() && currentSource == Candidates.Source.TRACE;
    }

    public boolean clearSelectionKeys() {
        int start = Math.min(this.startSelection, this.endSelection);
        int end = Math.max(this.startSelection, this.endSelection);
        for (int i = start; i < end; i++) {
            this.xt9coreinput.clearCharacter();
        }
        boolean selected = end > start;
        clearSelection();
        return selected;
    }

    public void clearComposingTextAndSelection() {
        InputConnection ic = getCurrentInputConnection();
        if (ic != null) {
            log.d("cursor clearComposingTextAndSelection");
            int start = Math.min(this.startSelection, this.endSelection);
            int end = Math.max(this.startSelection, this.endSelection);
            ic.finishComposingText();
            if (end > start) {
                ic.setSelection(end, end);
                clearSelection();
            }
        }
    }

    public boolean isFullScreenMode() {
        return this.mIme != null && this.mIme.isFullscreenMode();
    }

    public void showRemoveUdbWordDialog(String word) {
        if (this.removeUdbWordDialog == null || !this.removeUdbWordDialog.isShowing()) {
            Context context = getContext();
            this.removeUdbWordDialog = new RemoveUdbWordDialog(this, word);
            this.removeUdbWordDialog.createDialog(context);
            this.removeUdbWordDialog.show(getWindowToken(), context);
        }
    }

    public void showRemoveUdbWordDialog(String word, int wordSource) {
        showRemoveUdbWordDialog(word);
    }

    public void dimissRemoveUdbWordDialog() {
        if (this.removeUdbWordDialog != null) {
            this.removeUdbWordDialog.dismiss();
        }
        this.removeUdbWordDialog = null;
    }

    public void onHandleUdbWordRemoval(String word) {
    }

    public boolean isTabletDevice() {
        return IMEApplication.from(getContext()).isScreenLayoutTablet();
    }

    public void updateShiftKeyState() {
    }

    public void wordReCaptureComplete() {
    }

    public CandidatesListView getCandidatesListView() {
        return this.candidatesListView;
    }

    public void addCandidateListener(CandidatesListView.CandidateListener listener) {
        if (this.candidatesListView != null) {
            this.candidatesListView.addOnWordSelectActionListener(listener);
        }
    }

    public Candidates getSuggestionCandidates() {
        return this.suggestionCandidates;
    }

    public void markActiveWordUsedIfAny() {
    }

    public void updateCandidatesView() {
    }

    protected BackspaceRevertHandler getBackspaceRevertHandler() {
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public WordDecorator getWordDecorator() {
        return this.wordDecorator;
    }

    public boolean isOrientationLandscape() {
        return getContext().getResources().getConfiguration().orientation == 2;
    }

    public void preUpdateSpeechText() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onUpdateSpeechText(CharSequence text, boolean isStreaming, boolean isFinal) {
        InputConnection ic = getCurrentInputConnection();
        if (ic != null) {
            if (this.preProcessOnSpeechDictation) {
                preUpdateSpeechText();
                this.preProcessOnSpeechDictation = false;
            }
            ic.beginBatchEdit();
            SpeechResultTextBuffer.onResultCheckLeadingSpace(ic, text);
            this.composingSpeechText.clearSpans();
            this.composingSpeechText.clear();
            log.d("speech result: ", text);
            if (this.mKeyboardActionListener != null) {
                this.mKeyboardActionListener.onUpdateSpeechText(text, isStreaming, isFinal);
            }
            if (isStreaming) {
                if (isFinal && this.isExploredByTouch) {
                    this.composingSpeechText.append((CharSequence) (((Object) text) + XMLResultsHandler.SEP_SPACE));
                    this.composingSpeechText.setSpan(this.italicSpan, 0, text.length() + 1, R.styleable.ThemeTemplate_emojiCategoryIconSmiles);
                    ic.setComposingText(this.composingSpeechText, 1);
                } else {
                    this.composingSpeechText.append(text);
                    ic.setComposingText(this.composingSpeechText, 1);
                }
                if (isFinal) {
                    log.d("cursor onUpdateSpeechText");
                    ic.finishComposingText();
                    this.composingSpeechText.clear();
                }
            } else if (isFinal && this.isExploredByTouch) {
                ic.commitText(((Object) text) + XMLResultsHandler.SEP_SPACE, 1);
            } else {
                ic.commitText(text, 1);
            }
            setLastInput(4);
            ic.endBatchEdit();
        }
    }

    public void onCancelSpeech() {
        AppSpecificInputConnection ic = getCurrentInputConnection();
        if (ic != null) {
            ic.beginBatchEdit();
            ic.finishComposingText();
            this.composingSpeechText.clear();
            setLastInput(3);
            ic.endBatchEdit();
        }
    }

    public void onConfigurationChanged(Configuration prev, Configuration current, InputConnection ic) {
        boolean orientationChanged = false;
        boolean densityChanged = false;
        if (prev != null) {
            int diffMask = current.diff(prev);
            orientationChanged = (diffMask & 128) != 0;
            densityChanged = (diffMask & HardKeyboardManager.META_CTRL_ON) != 0;
        }
        if (densityChanged) {
            IMEApplication.from(getContext()).getKeyboardManager().evictAll();
        }
        boolean hardKeyboardHidden = current.hardKeyboardHidden == 2;
        log.d("orientationChanged = ", Boolean.valueOf(orientationChanged));
        log.d("hardKeyboardHidden = ", Boolean.valueOf(hardKeyboardHidden));
        log.d("composingSpeechText.length() = ", Integer.valueOf(this.composingSpeechText.length()));
        if (this.mSpeechWrapper != null) {
            if (!hardKeyboardHidden) {
                this.mSpeechWrapper.stopSpeech();
            } else if (orientationChanged) {
                this.mSpeechWrapper.pauseSpeech();
            }
            if (ic != null && this.composingSpeechText.length() > 0) {
                if (hardKeyboardHidden) {
                    log.d("cursor onConfigurationChanged");
                    ic.finishComposingText();
                } else if (orientationChanged) {
                    ic.commitText("", 1);
                }
            }
        }
    }

    public boolean shouldDisableInput(int primaryCode) {
        return false;
    }

    protected void setKeyboardDatabaseForDockChanged() {
        int kdbId = IMEApplication.from(getContext()).getKeyboardManager().getKeyboardId();
        this.xt9coreinput.setKeyboardDatabase(kdbId, 0, true);
    }

    public void onCancelNonEditStateRecaptureViaCharKey(char ch) {
    }

    public boolean handleHardkeySpace(boolean quickPressed, int repeatedCount) {
        sendSpace();
        return true;
    }

    public void handleHardkeyCharKey(int primaryCode, int[] keyCodes, KeyEvent event, boolean shiftable) {
    }

    public void handleHardkeyCharKey(int primaryCode, KeyEvent event, boolean shiftable) {
    }

    public boolean handleHardkeyBackspace(int repeatedCount) {
        return false;
    }

    public boolean handleHardkeyDelete(int repeatedCount) {
        return false;
    }

    public boolean handleHardkeyEnter() {
        return false;
    }

    public boolean handleHardkeyShiftKey(Shift.ShiftState state) {
        return false;
    }

    public boolean handleHardKeyCapsLock(boolean iscapslockon) {
        return false;
    }

    public boolean handleHardKeyDirectionKey(int keycode) {
        return false;
    }

    public boolean handleHardKeyHomeKey() {
        return false;
    }

    public boolean handleHardKeyEndKey() {
        return false;
    }

    public boolean handleHardKeyEscapeKey() {
        return false;
    }

    public boolean handleHardKeyStringEvent(String multistring, boolean multitap) {
        return false;
    }

    public void useKDBHardkey(boolean isUseHardkey) {
        this.mIsUseHardkey = isUseHardkey;
    }

    public void setHardkeyLayoutID(int layoutid) {
        this.mHardkeyKeyboardLayoutId = layoutid;
    }

    public boolean isHardkeyKeypadInput() {
        return this.mHardkeyKeyboardLayoutId == 1536 || this.mHardkeyKeyboardLayoutId == 1792 || this.mHardkeyKeyboardLayoutId == 1808 || this.mHardkeyKeyboardLayoutId == 1824;
    }

    public boolean removeHardKeyboardRecaptureCandidates() {
        if ((this.mIme.mCurrentInputLanguage.isLatinLanguage() || this.mIme.mCurrentInputLanguage.isKoreanLanguage()) && this.candidatesListView != null && this.suggestionCandidates != null && this.suggestionCandidates.source() != Candidates.Source.TAP && this.suggestionCandidates.source() != Candidates.Source.NEXT_WORD_PREDICTION && (!this.mIme.mCurrentInputLanguage.isKoreanLanguage() || this.suggestionCandidates.source() != Candidates.Source.TRACE)) {
            clearSuggestions();
        }
        return true;
    }

    public void clearHardText() {
        InputConnection ic = getCurrentInputConnection();
        if (ic != null) {
            ic.beginBatchEdit();
            ic.commitText("", 1);
            ic.endBatchEdit();
        }
    }

    public boolean addHardKeyOOVToDictionary() {
        return false;
    }

    public boolean isShowingAddToDictionaryTip() {
        return false;
    }

    public boolean isAddToDictionaryTipHighlighted() {
        return false;
    }

    public void setHardKeyboardSystemSettings() {
    }

    public void onHardKeyText(CharSequence text) {
    }

    public void showFunctionBar() {
    }

    public void hideFunctionBar() {
    }

    @Override // com.nuance.swype.input.KeyboardViewEx
    public boolean isFullScreenHandWritingView() {
        return false;
    }

    public boolean isHandWritingInputView() {
        return false;
    }

    public boolean isNormalTextInputMode() {
        return this.mKeyboardSwitcher != null && this.mKeyboardSwitcher.isAlphabetMode();
    }

    public void playSoundIfTextIsEmpty() {
        if (IsTextFieldEmpty()) {
            playEndOfListSound();
        }
    }

    private void playEndOfListSound() {
        SoundResources instance = SoundResources.getInstance();
        if (instance != null) {
            instance.play(3);
        }
    }

    public void showNextWordPrediction() {
        if (!this.mNextWordPredictionOn) {
            updateSuggestionsEmoji(CandidatesListView.Format.DEFAULT);
            return;
        }
        updateWordContext();
        updateSuggestions(Candidates.Source.NEXT_WORD_PREDICTION, true);
        if (!this.isEmojiKeyboardShown) {
            dismissPopupKeyboard();
        }
    }

    public boolean IsTextFieldEmpty() {
        CharSequence cs;
        AppSpecificInputConnection ic = getCurrentInputConnection();
        return (ic == null || (cs = ic.getTextBeforeCursor(1, 0)) == null || !TextUtils.isEmpty(cs) || ic.hasSelection()) ? false : true;
    }

    protected void setReconstructWord(int primaryCode) {
    }

    public void reconstructWord() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setInlineWord(String word) {
    }

    public void promptUserToUpdateLanguage() {
        Message msg = new Message();
        msg.what = 118;
        msg.arg1 = this.mCurrentInputLanguage.getCoreLanguageId();
        if (this.mIme != null && this.mIme.getHandler() != null) {
            this.mIme.getHandler().sendMessage(msg);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void clearSelection() {
        this.endSelection = -1;
        this.startSelection = -1;
    }

    public boolean isInputSessionStarted() {
        return true;
    }

    public void requestAutospaceOverrideTo(boolean enabled) {
    }

    public boolean hasWidgetViews() {
        return false;
    }

    public void setLanguage(XT9CoreInput coreInput) {
        XT9Status status = this.mCurrentInputLanguage.setLanguage(coreInput);
        if (status == XT9Status.ET9STATUS_DB_CORE_INCOMP || status == XT9Status.ET9STATUS_LDB_VERSION_ERROR) {
            DatabaseConfig.removeIncompatibleDBFiles(getContext(), this.mCurrentInputLanguage.mEnglishName);
            XT9Status status2 = this.mCurrentInputLanguage.setLanguage(coreInput);
            if (status2 == XT9Status.ET9STATUS_DB_CORE_INCOMP || status2 == XT9Status.ET9STATUS_LDB_VERSION_ERROR) {
                this.isLDBCompatible = false;
                promptUserToUpdateLanguage();
                toggleKeyboard(false);
            }
        }
    }

    public void setContextWindowShowing(boolean showing) {
        this.mContextWindowShowing = showing;
    }

    public boolean isUsingVietnameseTelexInputMode() {
        return false;
    }

    public boolean isUsingVietnameseNationalInputMode() {
        return false;
    }

    public boolean isModeStroke() {
        return false;
    }

    public void moveCursorToMiddle(InputConnection ic, String text) {
    }

    public boolean shouldHandleKeyViaIME(int primaryCode) {
        return primaryCode == 2900 || primaryCode == 39;
    }

    /* loaded from: classes.dex */
    public class WclPrompt implements CandidatesListView.CandidateListener, CJKCandidatesListView.OnWordSelectActionListener {
        Candidates candidates = null;
        protected boolean hasVisitedStore;
        private final Context mContext;

        public WclPrompt(Context context) {
            this.mContext = context;
        }

        public void setMessage(int message) {
            String prompt = this.mContext.getResources().getString(message);
            this.candidates = new Candidates(Candidates.Source.UDB_EDIT);
            this.candidates.addAttribute(1);
            this.candidates.addAttribute(2);
            this.candidates.add(new DrawableCandidate(InputView.this.imeApp.getThemedDrawable(R.attr.hwclSwypeKeyIcon)));
            this.candidates.add(new WordCandidate(prompt));
        }

        public void showMessage(String type) {
            if (!InputView.this.imeApp.isTrialExpired()) {
                if (!InputView.this.imeApp.isUserTapKey()) {
                    if (type.equals(InputView.wclAlphaReqMessage)) {
                        InputView.this.setCommonSuggestion(this, InputView.this.themeStoreWclPrompt.candidates, CandidatesListView.Format.FIT_AS_MUCH);
                        InputView.this.imeApp.setWCLMessage(true);
                        return;
                    } else {
                        InputView.this.candidatesListViewCJK.showMessageOnWCL(InputView.this.themeStoreWclPrompt.candidates);
                        InputView.this.candidatesListViewCJK.setOnWCLMessageSelectActionListener(this);
                        InputView.this.imeApp.setWCLMessage(true);
                        return;
                    }
                }
                return;
            }
            if (type.equals(InputView.wclAlphaReqMessage)) {
                InputView.this.setSuggestions(this, InputView.this.trialWclPrompt.candidates, CandidatesListView.Format.FIT_AS_MUCH);
            } else {
                InputView.this.candidatesListViewCJK.showMessageOnWCL(InputView.this.trialWclPrompt.candidates);
                InputView.this.candidatesListViewCJK.setOnWCLMessageSelectActionListener(this);
            }
        }

        public void wclMessageHandler(Handler handler, int msg) {
            handler.sendMessageDelayed(handler.obtainMessage(msg), 70L);
        }

        @Override // com.nuance.swype.input.CandidatesListView.CandidateListener
        public boolean onSelectCandidate(WordCandidate candidate, Candidates candidates) {
            if (InputView.this.imeApp.isTrialExpired()) {
                InputView.this.showPlayStore();
                return true;
            }
            InputView.this.showThemeStore();
            return true;
        }

        @Override // com.nuance.swype.input.CandidatesListView.CandidateListener
        public boolean onPressHoldCandidate(WordCandidate candidate, Candidates candidates) {
            return InputView.this.imeApp.isTrialExpired();
        }

        @Override // com.nuance.swype.input.CandidatesListView.CandidateListener
        public void onCandidatesUpdated(Candidates candidates) {
        }

        @Override // com.nuance.swype.input.CandidatesListView.CandidateListener
        public boolean onPressReleaseCandidate(WordCandidate candidate, Candidates candidates) {
            return false;
        }

        @Override // com.nuance.swype.input.CandidatesListView.CandidateListener
        public void onPressMoveCandidate(float xPos, float yPos, float xOffset) {
        }

        @Override // com.nuance.swype.input.chinese.CJKCandidatesListView.OnWordSelectActionListener
        public void selectWord(int index, CharSequence word, View source) {
            if (InputView.this.imeApp.isTrialExpired()) {
                InputView.this.showPlayStore();
            } else {
                InputView.this.showThemeStore();
            }
        }

        @Override // com.nuance.swype.input.chinese.CJKCandidatesListView.OnWordSelectActionListener
        public void prevBtnPressed(CJKCandidatesListView aSource) {
        }

        @Override // com.nuance.swype.input.chinese.CJKCandidatesListView.OnWordSelectActionListener
        public void nextBtnPressed(CJKCandidatesListView aSource) {
        }

        @Override // com.nuance.swype.input.chinese.CJKCandidatesListView.OnWordSelectActionListener
        public boolean isScrollable(CJKCandidatesListView aSource) {
            return false;
        }

        @Override // com.nuance.swype.input.chinese.CJKCandidatesListView.OnWordSelectActionListener
        public void onCandidateLongPressed(int index, String word, int wdSource, CJKCandidatesListView aSource) {
        }

        @Override // com.nuance.swype.input.chinese.CJKCandidatesListView.OnWordSelectActionListener
        public boolean onPressReleaseCandidate(int index, String word, int wdSource) {
            return false;
        }

        @Override // com.nuance.swype.input.chinese.CJKCandidatesListView.OnWordSelectActionListener
        public void onScrollContentChanged() {
        }
    }

    @Override // com.nuance.swype.input.KeyboardViewEx
    public boolean handleScrollDown() {
        if (this.mIme == null) {
            return false;
        }
        this.mIme.requestHideSelf(0);
        return true;
    }

    public void showTrialExpireWclMessage(String inputViewType) {
        if (IMEApplication.from(this.mIme).isTrialExpired()) {
            this.trialWclPrompt = new WclPrompt(getContext());
            this.trialWclPrompt.setMessage(R.string.trial_period_expiration_msg);
            this.trialWclPrompt.showMessage(inputViewType);
        }
        refreshEmojiChoiceListEnable();
    }

    public void showUserThemeWclMessage(Handler handler) {
        if (this.imeApp.getBuildInfo().isDownloadableThemesEnabled() && !this.imeApp.getStartupSequenceInfo().isInputFieldStartupOrPassword(getCurrentInputEditorInfo(), this.mInputFieldInfo)) {
            if (this.imeApp.isWCLMessage()) {
                this.imeApp.setUserTapKey(true);
            }
            AppPreferences prefs = this.imeApp.getAppPreferences();
            if (prefs.isNewThemeAvailableInStore()) {
                if (!prefs.isUserVisitedStore() && !this.imeApp.isWCLMessage()) {
                    this.themeStoreWclPrompt = new WclPrompt(getContext());
                    this.themeStoreWclPrompt.setMessage(R.string.notification_new_themes_general);
                    this.themeStoreWclPrompt.wclMessageHandler(handler, 125);
                    return;
                }
                this.imeApp.setUserTapKey(true);
            }
        }
    }

    public void showThemeStore() {
        this.imeApp.showMainSettings();
        this.themeStoreWclPrompt.hasVisitedStore = true;
    }

    public void showPlayStore() {
        int urlId = this.imeApp.getBuildInfo().getPaidVersionUrl();
        Intent marketIntent = new Intent("android.intent.action.VIEW", Uri.parse(this.imeApp.getString(urlId)));
        marketIntent.addFlags(268435456);
        this.imeApp.startActivity(marketIntent);
    }

    public boolean isActiveWCL() {
        return (this.imeApp.isUserTapKey() || !this.imeApp.getAppPreferences().isNewThemeAvailableInStore() || this.themeStoreWclPrompt == null || this.themeStoreWclPrompt.hasVisitedStore) ? false : true;
    }

    public boolean isValidBuild() {
        return this.mIme.isValidBuild();
    }

    public DefaultHWRTouchKeyHandler.KeyUIState getDefaultKeyUIStateHandler() {
        return new DefaultHWRTouchKeyHandler.KeyUIState() { // from class: com.nuance.swype.input.InputView.3
            @Override // com.nuance.swype.input.keyboard.DefaultHWRTouchKeyHandler.KeyUIState
            public void onPress(int pointerId, int keyIndex) {
                InputView.this.pressKey(InputView.this.mKeys, keyIndex);
                InputView.this.setKeyState(keyIndex, KeyboardViewEx.ShowKeyState.Pressed);
                InputView.this.showPreviewKey(keyIndex, pointerId);
            }

            @Override // com.nuance.swype.input.keyboard.DefaultHWRTouchKeyHandler.KeyUIState
            public void onRelease(int pointerId, int keyIndex) {
                InputView.this.releaseKey(InputView.this.mKeys, keyIndex, true);
                InputView.this.hideKeyPreview(pointerId);
                InputView.this.setKeyState(-1, KeyboardViewEx.ShowKeyState.Released);
            }
        };
    }

    public InputContextRequestDispatcher.InputContextRequestHandler getDefaultInputContextHandler() {
        return new InputContextRequestDispatcher.InputContextRequestHandler() { // from class: com.nuance.swype.input.InputView.4
            private final char[] phantomCapTextBuffer = {'x', ' '};

            @Override // com.nuance.swype.input.keyboard.InputContextRequestDispatcher.InputContextRequestHandler
            public char[] getContextBuffer(int maxBufferLen) {
                InputConnection ic = InputView.this.getCurrentInputConnection();
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
            public char[] getAutoCapitalizationTextBuffer(int maxBufferLen) {
                return this.phantomCapTextBuffer;
            }

            @Override // com.nuance.swype.input.keyboard.InputContextRequestDispatcher.InputContextRequestHandler
            public boolean autoAccept(boolean addSeparator) {
                return false;
            }
        };
    }

    public void loadKeyboardGesture() {
        if (getKeyboard() != null && !this.xt9coreinput.isGestureLoaded()) {
            GestureManager gestureManager = IMEApplication.from(getContext()).getGestureManager();
            gestureManager.loadGestures();
            List<XT9CoreInput.Gesture> gestures = gestureManager.getGestures();
            this.xt9coreinput.loadGestures(gestures);
        }
    }

    @Override // com.nuance.swype.input.KeyboardViewEx, com.nuance.swype.input.view.InputLayout.DragListener
    public void onBeginDrag() {
        log.d("onBeginDrag(): XXX");
        super.onBeginDrag();
        dismissEmojiPopup();
        IMEApplication.from(getContext()).getEmojiInputViewController().dismissSkinTonePopup();
    }

    public void addEmojiInRecentCat(String selectedEmoji, String original_emoji) {
        log.d("addEmojiInRecentCat()", " called ::  selectedEmoji  :: " + selectedEmoji + " , original_emoji :: " + original_emoji);
        if (selectedEmoji != null && original_emoji != null && EmojiLoader.isEmoji(original_emoji)) {
            boolean isAdded = addSelectedEmoji(original_emoji, selectedEmoji);
            log.d("addEmojiInRecentCat()", " called ::  isAdded  :: " + isAdded);
        }
    }

    public void addEmojiInRecentCat(WordCandidate candidate) {
        if (candidate != null) {
            if (this.suggestionCandidates != null && this.suggestionCandidates.source() == Candidates.Source.EMOJEENIE) {
                addSelectedEmoji(candidate);
            } else if (EmojiLoader.isEmoji(candidate.toString())) {
                addSelectedEmoji(candidate);
            }
        }
    }

    public void addSelectedEmoji(WordCandidate candidate) {
        boolean isAdded = addSelectedEmoji(candidate.getEmojiUnicode(), candidate.toString());
        log.d("onSelectCandidate()", " called ::  emoji added :: " + isAdded);
    }

    private boolean addSelectedEmoji(String original, String selected) {
        EmojiInputController ic = IMEApplication.from(getContext()).getEmojiInputViewController();
        Emoji emoji = EmojiLoader.getEmoji(original);
        if (!selected.toString().equals(original)) {
            Iterator<Emoji> it = emoji.getEmojiSkinToneList().iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                Emoji skinTone = it.next();
                if (skinTone.getEmojiDisplayCode().equals(selected)) {
                    emoji = skinTone;
                    break;
                }
            }
        }
        boolean isAdded = ic.addEmojiInRecentCat(emoji);
        log.d("addSelectedEmoji()", " called ::  emoji added :: " + isAdded);
        return isAdded;
    }

    public void showSkinTonePopup(WordCandidate candidate, Candidates candidates) {
        if (this.wordListViewContainer != null) {
            if (this.mEnableEmojiChoiceList) {
                initSkinPopupView(this.wordListViewContainer);
            } else {
                initSkinPopupView(this.wordListHolder);
            }
            setSkinToneAdapter(candidate, candidates, this);
        }
    }

    public void showSkinTonePopupForCJK(EmojiInfo emojiInfo, String selectedEmoji, String original_emoji) {
        log.d("showSkinTonePopupForCJK()", " called ::  selectedEmoji  :: " + selectedEmoji + " , original_emoji :: " + original_emoji);
        if (selectedEmoji != null && original_emoji != null && this.wordListViewContainerCJK != null) {
            initSkinPopupView(this.wordListViewContainerCJK);
            setSkinToneAdapter(emojiInfo, selectedEmoji, original_emoji, this);
        }
    }

    private void initSkinPopupView(View mContainerView) {
        IMEApplication app = IMEApplication.from(getContext());
        LayoutInflater inflater = app.getThemedLayoutInflater(LayoutInflater.from(app));
        app.getThemeLoader().setLayoutInflaterFactory(inflater);
        int billboardHeight = 0;
        if (this.billboardHolder != null && this.billboardHolder.getVisibility() == 0) {
            billboardHeight = this.billboardHolder.getMeasuredHeight();
        }
        this.popupEmojiSkinList = new PopupEmojiSkinList(getContext(), inflater, mContainerView, this.mEnableEmojiChoiceList, billboardHeight);
    }

    private void setSkinToneAdapter(EmojiInfo emojiInfo, String selectedEmoji, String original_emoji, View parent) {
        Emoji emoji = getEmoji(original_emoji);
        if (emoji != null) {
            this.skinToneList = emoji.getEmojiSkinToneList();
        }
        if (this.skinToneList != null && this.skinToneList.size() > 1) {
            int mTargetScroll = 0;
            if (this.candidatesListViewCJK != null) {
                this.candidatesListViewCJK.setHorizontalScroll(false);
                mTargetScroll = this.candidatesListViewCJK.getTargetScrollX();
            }
            emojiInfo.xPos -= mTargetScroll;
            int selectedSkinTone = getDefaultSkinTone(selectedEmoji);
            this.popupEmojiSkinList.setSkinToneAdapter(this.skinToneList, parent, emojiInfo, selectedSkinTone);
        }
    }

    private void setSkinToneAdapter(WordCandidate candidate, Candidates candidates, View parent) {
        String emojiCode;
        Emoji emoji;
        if (candidate != null && candidates != null && (emojiCode = candidate.getEmojiUnicode()) != null && (emoji = getEmoji(emojiCode)) != null) {
            this.skinToneList = emoji.getEmojiSkinToneList();
            if (this.skinToneList != null && this.skinToneList.size() > 1) {
                int mTargetScroll = 0;
                if (this.emojiCandidatesListView != null) {
                    this.emojiCandidatesListView.setHorizontalScroll(false);
                    mTargetScroll = this.emojiCandidatesListView.getTargetScrollX();
                } else if (this.candidatesListView != null) {
                    this.candidatesListView.setHorizontalScroll(false);
                    mTargetScroll = this.candidatesListView.getTargetScrollX();
                }
                EmojiInfo emojiInfo = new EmojiInfo();
                emojiInfo.xPos = candidate.getLeft() - mTargetScroll;
                emojiInfo.yPos = candidate.getTop();
                emojiInfo.width = candidate.getWidth();
                emojiInfo.height = candidate.getHeight();
                int selectedSkinTone = getDefaultSkinTone(candidate.toString());
                this.popupEmojiSkinList.setSkinToneAdapter(this.skinToneList, parent, emojiInfo, selectedSkinTone);
            }
        }
    }

    private int getDefaultSkinTone(String selectedEmoji) {
        for (Emoji emoji : this.skinToneList) {
            if (emoji.getEmojiDisplayCode().equals(selectedEmoji)) {
                if (emoji.getEmojiSkinType() == null) {
                    return -1;
                }
                int defaultSkinTone = emoji.getEmojiSkinType().getSkinValue();
                return defaultSkinTone;
            }
        }
        return -1;
    }

    private Emoji getEmoji(String emoji_unicode) {
        return EmojiLoader.getEmoji(emoji_unicode);
    }

    public boolean isSupportSkinTone(String emoji_unicode) {
        Emoji emoji = getEmoji(emoji_unicode);
        if (emoji != null) {
            return emoji.isSkinToneSupport();
        }
        return false;
    }

    public void dismissEmojiPopup() {
        if (this.popupEmojiSkinList != null && this.popupEmojiSkinList.isShowing()) {
            log.d("dismissEmojiPopup()", "called >>>>>: ");
            this.popupEmojiSkinList.dismiss();
            this.popupEmojiSkinList = null;
        }
    }

    public boolean touchMoveHandle(float xPos, float yPos, float xOffset) {
        log.d("touchMoveHandle()", "called >>>>>: " + xOffset);
        if (this.popupEmojiSkinList == null) {
            return false;
        }
        this.popupEmojiSkinList.touchMoveHandleBySkinPopup((int) xPos, (int) yPos, (int) xOffset);
        return true;
    }

    @Override // com.nuance.swype.input.emoji.EmojiSkinAdapter.OnItemClickListener
    public void onItemClick(View view, int position, boolean isLongClick) {
        log.d("onItemClick()", "called >>>>>: " + position);
        setSelectedSkinTone(position);
    }

    public void setSelectedSkinTone(int position) {
        this.emojiCacheManager = EmojiCacheManager.from(getContext());
        UserPreferences userPrefs = UserPreferences.from(getContext());
        Emoji emoji = this.skinToneList.get(position);
        if (emoji != null) {
            AppSpecificInputConnection ic = getCurrentInputConnection();
            String wordSelected = emoji.getEmojiDisplayCode();
            if (ic != null && wordSelected != null) {
                ic.commitText(wordSelected, 1);
            }
            if (IMEApplication.from(getContext()).getEmojiInputViewController().addEmojiInRecentCat(emoji)) {
                String emojiCode = emoji.getEmojiCode();
                if (emoji.getEmojiSkinType() != null) {
                    int skinToneValue = emoji.getEmojiSkinType().getSkinValue();
                    this.emojiCacheManager.addObjectToCache(emojiCode, Integer.valueOf(skinToneValue));
                    userPrefs.setCachedEmojiSkinTone(emojiCode, skinToneValue);
                }
            }
            if (this.emojiCandidatesListView != null) {
                this.emojiCandidatesListView.setHorizontalScroll(true);
            }
            if (this.candidatesListView != null) {
                this.candidatesListView.setHorizontalScroll(true);
            }
            if (this.candidatesListViewCJK != null) {
                this.candidatesListViewCJK.setHorizontalScroll(false);
            }
        }
    }

    public void setCurrentSkinTone() {
        log.d("onPressReleaseCandidate()", " setCurrentSkinTone called ::");
        if (isShowingSkinTonePopup()) {
            this.popupEmojiSkinList.getDefaultSkinTonePopupView();
        }
    }

    public boolean isShowingSkinTonePopup() {
        return this.popupEmojiSkinList != null && this.popupEmojiSkinList.isShowing();
    }

    public boolean handlePressHoldCandidate(WordCandidate candidate, Candidates candidates) {
        if (isShowingSkinTonePopup()) {
            dismissEmojiPopup();
        }
        showSkinTonePopup(candidate, candidates);
        if (!isShowingSkinTonePopup()) {
            return false;
        }
        if (this.emojiCandidatesListView != null) {
            this.emojiCandidatesListView.setHorizontalScroll(false);
        }
        if (this.candidatesListView != null) {
            this.candidatesListView.setHorizontalScroll(false);
        }
        return true;
    }
}
