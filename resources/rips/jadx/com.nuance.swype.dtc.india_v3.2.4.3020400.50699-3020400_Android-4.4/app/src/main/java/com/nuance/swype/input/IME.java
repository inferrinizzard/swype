package com.nuance.swype.input;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.AbstractInputMethodService;
import android.inputmethodservice.InputMethodService;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.os.Vibrator;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.util.SparseIntArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.CursorAnchorInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import com.google.android.voiceime.VoiceRecognitionTrigger;
import com.nuance.android.compat.ActivityManagerCompat;
import com.nuance.android.compat.ConfigurationCompat;
import com.nuance.android.compat.InputMethodServiceCompat;
import com.nuance.android.compat.KeyguardManagerCompat;
import com.nuance.android.compat.UserManagerCompat;
import com.nuance.android.compat.ViewCompat;
import com.nuance.android.util.WeakReferenceHandler;
import com.nuance.connect.util.TimeConversion;
import com.nuance.input.swypecorelib.Candidates;
import com.nuance.input.swypecorelib.Shift;
import com.nuance.input.swypecorelib.SwypeCoreLibrary;
import com.nuance.input.swypecorelib.XT9CoreAlphaInput;
import com.nuance.input.swypecorelib.XT9CoreInput;
import com.nuance.speech.dragon.DragonDictationLanguageListAdapter;
import com.nuance.swype.connect.Connect;
import com.nuance.swype.connect.LanguageUpdateWithTOS;
import com.nuance.swype.connect.SDKDownloadManager;
import com.nuance.swype.connect.SDKUpdateManager;
import com.nuance.swype.input.AbstractTapDetector;
import com.nuance.swype.input.InputMethods;
import com.nuance.swype.input.KeyboardEx;
import com.nuance.swype.input.KeyboardViewEx;
import com.nuance.swype.input.ThemeManager;
import com.nuance.swype.input.accessibility.AccessibilityInfo;
import com.nuance.swype.input.accessibility.AccessibilityNotification;
import com.nuance.swype.input.accessibility.ExtendedPointTracker;
import com.nuance.swype.input.accessibility.SettingsChangeListener;
import com.nuance.swype.input.accessibility.statemachine.WordSelectionState;
import com.nuance.swype.input.appspecific.AppSpecificBehavior;
import com.nuance.swype.input.appspecific.AppSpecificInputConnection;
import com.nuance.swype.input.hardkey.HardKeyIMEHandler;
import com.nuance.swype.input.hardkey.HardKeyboardManager;
import com.nuance.swype.input.udb.NewWordsBucketFactory;
import com.nuance.swype.input.view.InputContainerView;
import com.nuance.swype.startup.StartupSequenceInfo;
import com.nuance.swype.stats.StatisticsEnabledEditState;
import com.nuance.swype.stats.StatisticsManager;
import com.nuance.swype.stats.UsageManager;
import com.nuance.swype.usagedata.UsageData;
import com.nuance.swype.util.AdsUtil;
import com.nuance.swype.util.CharacterUtilities;
import com.nuance.swype.util.EmojiUtils;
import com.nuance.swype.util.InputConnectionUtils;
import com.nuance.swype.util.LogManager;
import com.nuance.swype.util.drawable.KeyboardBackgroundManager;
import com.nuance.swype.widget.PopupLanguageList;
import com.nuance.swypeconnect.ac.ACPlatformUpdateService;
import java.io.File;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public class IME extends InputMethodService implements View.OnFocusChangeListener, KeyboardViewEx.OnKeyboardActionListener, SettingsChangeListener.SystemSettingsChangeListener, AppSpecificInputConnection.ExtractViewState, ACPlatformUpdateService.ACPlatformUpdateCallback {
    protected static final String ANDROID_DEFAULT_IME_ID = "com.google.android.inputmethod.latin/com.android.inputmethod.latin.LatinIME";
    private static final int ARROW_KEYS_ACCELERATE_AT = 20;
    private static final int DELETE_ACCELERATE_AT = 20;
    private static final float FX_VOLUME = -1.0f;
    private static final int HARD_KEYBOARD_REOPEN_INPUTVIEW = 1010;
    private static final long INITIAL_DELAY_IN_MILLIS = 5000;
    public static final int MAX_DLM_TEXTS_TO_SCAN = 1;
    private static final int MAX_TRIAL_USE_COUNT = 5;
    public static final int MAX_WORDS_TO_SCAN = 10;
    public static final int MSG_ADD_OEM_LDB_WORDS = 125;
    public static final int MSG_BACKGROUND_LOADED = 124;
    public static final int MSG_CYCLING_KEYBOARD_INPUTS = 103;
    public static final int MSG_CYCLING_LANGUAGE = 102;
    public static final int MSG_DELAY_CHECK_LANGUAGE_UPDATE = 119;
    public static final int MSG_FINISHVIEW_RUNNABLE_POSTED = 123;
    public static final int MSG_FIRST = 100;
    public static final int MSG_HARDKEY_HANDLE_CUSTOM_KEY_EVENT = 116;
    public static final int MSG_HARDKEY_START_HIDE_WCL = 115;
    public static final int MSG_HARDKEY_START_SHOW_WCL = 117;
    public static final int MSG_LAST = 125;
    public static final int MSG_LAUNCH_SETTINGS = 105;
    public static final int MSG_NEW_WORDS_SCAN = 113;
    public static final int MSG_REFRESH_INPUTVIEW = 108;
    public static final int MSG_RELEASE_EMOJI_INPUT = 120;
    public static final int MSG_RESUME_EMOJI_INPUT = 121;
    public static final int MSG_SHOW_EMOJI_INPUT = 122;
    public static final int MSG_SHOW_FIRST_TIME_MESSAGES = 109;
    public static final int MSG_SWITCH_INPUTVIEW = 100;
    public static final int MSG_SWITCH_INPUTVIEW_NO_RESTARTING = 101;
    public static final int MSG_SWITCH_LANGUAGE = 111;
    public static final int MSG_TOGGLE_FULLSCREEN_HWR = 107;
    public static final int MSG_TOGGLE_HWR_KEYBOARD = 106;
    public static final int MSG_TOGGLE_TO_ANDROID_KEYBOARD = 112;
    public static final int MSG_UPDATE_INCOMPATIBLE_LANGUAGE = 118;
    public static final long NEXT_SCAN_IN_MILLIS = 2000;
    private static final int QUICK_PRESS = 750;
    public static final long RETRY_DELAY_IN_MILLIS = 10000;
    private static final String SAMSUNG = "Samsung";
    private static final int WAIT_TIME_FILTER_STARTINPUT = 700;
    public static final boolean skipLvlError = true;
    private boolean allowCandidateViewShown;
    private XT9CoreAlphaInput alphaInput;
    AppPreferences appPrefs;
    private AppSpecificInputConnection appSpecificInputConnection;
    private AudioManager audioManager;
    private License currentLicense;
    private DragonDictationLanguageListAdapter dictationLanguageAdapter;
    public int emojiExtraRegion;
    private ExtendedPointTracker extendedPointTracker;
    public IMEHandlerManager handlerManager;
    private boolean ignoreFirstUpdateSelectionPostOrientationChange;
    private boolean inUse;
    private InputContainerView inputContainerView;
    private boolean isCandidateViewOpening;
    private boolean isEditorAttributeChanged;
    private boolean isHardKeyboardAttached;
    private boolean isHardkeyboardEnabled;
    private boolean isOrientationChanged;
    private boolean isStartInputPending;
    private boolean isStartupActivityShown;
    protected boolean keySoundOn;
    public KeyboardInputInflater keyboardInputInflater;
    private LanguageListAdapter languageAdapter;
    private long lastAllowedTime;
    private Configuration lastConfiguration;
    private AccessibilityInfo mAccessibilityInfo;
    public AlertDialog mAlertMessageDialog;
    public BuildInfo mBuildInfo;
    public InputMethods.Language mCurrentInputLanguage;
    private EditState mEditState;
    public InputFieldInfo mInputFieldInfo;
    public InputMethods mInputMethods;
    private KeyboardBackgroundManager mKeyboardBackgroundManager;
    public int mLastKey;
    private long mLastKeyTime;
    private boolean mNeedLanguageInToast;
    private AlertDialog mNetworkPromptMessage;
    public AlertDialog mOptionsDialog;
    private int mRepeatedKeyCount;
    private ShowFirstTimeStartupMessages mShowFirstTimeStartupMessages;
    private ThirdPartyLicense mThirdPartyLicense;
    private boolean mUsedEditLayer;
    private VoiceRecognitionTrigger mVoiceRecognitionTrigger;
    public boolean mWantToast;
    private SwypeInputMethodImpl myInputMethodImpl;
    private PopupLanguageList popupLanguageList;
    private Whitelist portraitCandidatesViewFilter;
    private RecaptureHandler recaptureHandler;
    private boolean simulateTapOnPostOrientationChangePending;
    private boolean startupActivityShown;
    public AbstractTapDetector tapDetector;
    private int trialCheckCount;
    private boolean upgrade;
    protected boolean vibrateOn;
    protected static final LogManager.Log log = LogManager.getLog("IME");
    protected static final LogManager.Trace trace = LogManager.getTrace();
    private static StringBuilder mLastActiveWord = new StringBuilder("");
    private static Candidates.Source mLastShownCandidatesSource = Candidates.Source.INVALID;
    public String mCurrentInputViewName = KeyboardInputInflater.NO_INPUTVIEW;
    private boolean needRrefreshKeyboard = false;
    private final KeyboardState savedKeyboardState = new KeyboardState();
    private int prevLastInput = 0;
    private boolean hwAccelEnabled = false;
    private boolean lastAccessibility = false;
    private boolean isAccessibilityChanged = false;
    protected boolean recaptureWhenSwitching = false;
    private boolean mValidBulid = true;
    private ImageButton toggleButton = null;
    private View view = null;
    private final SparseIntArray checkedLanguageUpdateList = new SparseIntArray();
    private final String devanagariInputMode = "devanagariAlphabetic";
    private DragKeyboardInsetAdjustMode dragInsetAdjustMode = DragKeyboardInsetAdjustMode.NONE;
    private Runnable pendingHibernateState = new Runnable() { // from class: com.nuance.swype.input.IME.1
        @Override // java.lang.Runnable
        public void run() {
            if (!IME.this.isImeInUse() && !IME.this.hasNewWordsScanning()) {
                IME.log.d("running hibernation task.");
                IMEApplication.from(IME.this).getSwypeCoreLibMgr().setRunningState(SwypeCoreLibrary.RUNNING_STATE_BACKGROUND_HIBERNATE);
                IME.this.checkPackageUpdate();
            }
        }
    };
    private Runnable finishInputViewRunnable = new Runnable() { // from class: com.nuance.swype.input.IME.3
        @Override // java.lang.Runnable
        public void run() {
            IME.this.mHandler.removeMessages(123);
            IME.this.doFinishInputView(false, true);
        }
    };
    private Runnable checkPackageUpdateRunnable = new Runnable() { // from class: com.nuance.swype.input.IME.4
        @Override // java.lang.Runnable
        public void run() {
            IME.this.checkPackageUpdate();
        }
    };
    public final BroadcastReceiver mReceiver = new BroadcastReceiver() { // from class: com.nuance.swype.input.IME.7
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.intent.action.ACTION_SHUTDOWN")) {
                IME.this.cleanupAccessibility();
                IME.this.onDestroy();
                return;
            }
            if (intent.getAction().equals("android.intent.action.SCREEN_OFF")) {
                IME.this.cleanupAccessibility();
                IME.this.stopSpeech();
                KeyguardManager kgm = (KeyguardManager) IME.this.getSystemService("keyguard");
                if (kgm != null && KeyguardManagerCompat.isKeyguardLocked(kgm) && KeyguardManagerCompat.isKeyguardSecure(kgm)) {
                    IME.this.savedKeyboardState.clear();
                    return;
                }
                return;
            }
            if (intent.getAction().equals("android.intent.action.SCREEN_ON")) {
                IME.this.cleanupAccessibility();
                return;
            }
            if (intent.getAction().equals("android.intent.action.CLOSE_SYSTEM_DIALOGS")) {
                IME.this.cleanupAccessibility();
                boolean wasShowingFirstTimeMessage = false;
                if (IME.this.mShowFirstTimeStartupMessages != null) {
                    wasShowingFirstTimeMessage = IME.this.mShowFirstTimeStartupMessages.isDialogShowing();
                }
                IME.this.closeDialogs();
                if (wasShowingFirstTimeMessage) {
                    IME.this.showStartupMessage();
                    return;
                }
                return;
            }
            if (intent.getAction().equals(SDKDownloadManager.LANGUAGE_UPDATE_NOTIFICATION)) {
                if (IME.this.isValidBuild()) {
                    int languageId = intent.getIntExtra(LanguageUpdateWithTOS.LANGUAGE_ID, 0);
                    Intent languageUpdateIntent = new Intent(IMEApplication.from(IME.this), (Class<?>) LanguageUpdateWithTOS.class);
                    languageUpdateIntent.setFlags(IME.this.getIntentFlags());
                    languageUpdateIntent.putExtra(LanguageUpdateWithTOS.LANGUAGE_ID, languageId);
                    IME.this.startActivity(languageUpdateIntent);
                    IME.this.hideWindow();
                    return;
                }
                return;
            }
            if (intent.getAction().equals("android.intent.action.USER_UNLOCKED")) {
                IME.log.d("ACTION_USER_UNLOCKED...");
                IMEApplication imeApp = IMEApplication.from(IME.this);
                if (!imeApp.isUserUnlockFinished()) {
                    imeApp.postUserUnlock();
                    IME.this.resetInputView(true);
                }
            }
        }
    };
    private Runnable closeRunnable = new Runnable() { // from class: com.nuance.swype.input.IME.8
        @Override // java.lang.Runnable
        public void run() {
            IMEApplication imeApp = IMEApplication.from(IME.this);
            imeApp.setWCLMessage(false);
            imeApp.setUserTapKey(false);
            if (!BuildInfo.from(IME.this).isDTCbuild() || !imeApp.getStartupSequenceInfo().shouldShowStartup(IME.this.getCurrentInputEditorInfo(), IME.this.mInputFieldInfo)) {
                imeApp.startScrapingServices();
            }
            InputView inputView = IME.this.getCurrentInputView();
            if (inputView != null) {
                inputView.handleClose();
            }
            if (imeApp.isLowEndDeviceBuild()) {
                imeApp.releaseInstances();
            }
        }
    };
    private final Handler.Callback handlerCallback = new Handler.Callback() { // from class: com.nuance.swype.input.IME.14
        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message msg) {
            InputView inputview;
            InputView inputview2;
            switch (msg.what) {
                case 100:
                    IME.this.updateFullscreenMode();
                    IME.this.switchInputView(false);
                    IME.this.getCurrentInputView().updateCandidatesView();
                    break;
                case 101:
                    IME.this.switchInputView(false);
                    IME.this.needRrefreshKeyboard = false;
                    break;
                case 102:
                case 103:
                case 104:
                case 107:
                case 110:
                case 114:
                case 120:
                case 121:
                case 122:
                case 123:
                default:
                    if (IMEApplication.from(IME.this).getIMEHandlerInstance() == null) {
                        return false;
                    }
                    IME.this.handlerManager.handleMessage(msg);
                    return false;
                case 105:
                    IME.this.launchSettings();
                    break;
                case 106:
                    if (IMEApplication.from(IME.this).getIMEHandlerInstance() != null) {
                        IME.this.handlerManager.getIMEInstance().switchToHandwritingModeCJK();
                        break;
                    } else {
                        IME.this.toggleHwrAndKeyboardInputMode();
                        break;
                    }
                case 108:
                    IME.this.refreshInputViewLanguage();
                    break;
                case 109:
                    if (IME.this.keyboardInputInflater.getInputView(IME.this.mCurrentInputViewName) == null || IME.this.keyboardInputInflater.getInputView(IME.this.mCurrentInputViewName).getWindowToken() == null) {
                        IME.this.mHandler.sendEmptyMessageDelayed(109, 100L);
                        break;
                    } else {
                        IME.this.showFirstTimeMessages();
                        break;
                    }
                    break;
                case 111:
                    IME.this.recaptureWhenSwitching = true;
                    ((LangSwitchAction) msg.obj).execute(IME.this);
                    break;
                case 112:
                    IME.this.toggleToAndroidKeyboard();
                    break;
                case 113:
                    if (IMEApplication.from(IME.this).getSwypeCoreLibMgr().getSwypeCoreLibInstance().getRunningState() != SwypeCoreLibrary.RUNNING_STATE_BACKGROUND_HIBERNATE) {
                        if (IMEApplication.from(IME.this).getIMEHandlerInstance() == null) {
                            IME.this.handleNewWordsDelayScanning((NewWordsBucketFactory.NewWordsBucket) msg.obj);
                            break;
                        } else {
                            IME.this.handlerManager.getIMEInstance().handleNewWordsDelayScanning((NewWordsBucketFactory.NewWordsBucket) msg.obj);
                            break;
                        }
                    }
                    break;
                case 115:
                    if (IME.this.isHardKeyboardActive() && (inputview2 = IME.this.getCurrentInputView()) != null && !inputview2.isShown()) {
                        IME.this.setCandidatesViewShown(false);
                        break;
                    }
                    break;
                case 116:
                    if (IME.this.isHardKeyboardActive()) {
                        IME.this.onKeyDownInner(msg.arg1, msg.arg2, (KeyEvent) msg.obj);
                        break;
                    }
                    break;
                case 117:
                    if (IME.this.isHardKeyboardActive() && (inputview = IME.this.getCurrentInputView()) != null && !inputview.isShown()) {
                        IME.this.setCandidatesViewShown(true);
                        break;
                    }
                    break;
                case 118:
                    if (IME.this.isValidBuild()) {
                        int languageId = msg.arg1;
                        Intent intent = new Intent(IMEApplication.from(IME.this), (Class<?>) LanguageUpdateWithTOS.class);
                        intent.setFlags(IME.this.getIntentFlags());
                        intent.putExtra(LanguageUpdateWithTOS.LANGUAGE_ID, languageId);
                        IME.this.startActivity(intent);
                        IME.this.hideWindow();
                        break;
                    }
                    break;
                case 119:
                    if (IME.this.isValidBuild()) {
                        IME.this.checkForLanguageUpgrade(IME.this.mCurrentInputLanguage);
                        break;
                    }
                    break;
                case 124:
                    Drawable background = (Drawable) msg.obj;
                    InputView iv = IME.this.getCurrentInputView();
                    if (iv != null) {
                        iv.updateKeyboardBackground(background);
                        break;
                    }
                    break;
                case 125:
                    if (!IME.this.mCurrentInputLanguage.isCJK()) {
                        OemLdbWordsManager.from(IME.this.getApplicationContext()).AddOemLdbWordsForAlpha((XT9CoreAlphaInput) IME.this.getCurrentInputView().getXT9CoreInput(), IME.this.mCurrentInputLanguage.getCoreLanguageId());
                        break;
                    }
                    break;
            }
            return true;
        }
    };
    private final Handler mHandler = WeakReferenceHandler.create(this.handlerCallback);
    private boolean initializeCoreNeeded = true;
    private LanguageUpdateNotificationRunnable languageUpdateNotificationRunnable1 = null;
    private LanguageUpdateNotificationRunnable languageUpdateNotificationRunnable2 = null;
    private Rect mTouchableRegionRect = new Rect();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public interface IntentDecorator {
        void decorate(Intent intent);
    }

    /* loaded from: classes.dex */
    public enum DragKeyboardInsetAdjustMode {
        NONE,
        VISIBLE_INSETS,
        ALL_INSETS;

        public static DragKeyboardInsetAdjustMode fromInt(int value) {
            DragKeyboardInsetAdjustMode[] map = values();
            return (value < 0 || value >= map.length) ? NONE : map[value];
        }
    }

    public IME() {
        setTheme(R.style.InputMethodTheme);
    }

    protected final void doEnableHardwaredAcceleration() {
        this.hwAccelEnabled = UserPreferences.from(this).isHardwareAccelerationEnabled(this);
        if (this.hwAccelEnabled) {
            this.hwAccelEnabled = InputMethodServiceCompat.enableHardwareAcceleration(this);
        }
        log.i("IME(): hardware accel enabled: " + this.hwAccelEnabled);
    }

    private boolean isHardwareAccelerationEnabled() {
        return this.hwAccelEnabled;
    }

    public void enableHwAccel(View view) {
        if (isHardwareAccelerationEnabled()) {
            ViewCompat.enableHardwareLayer(view, null);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class SwypeInputMethodImpl extends InputMethodService.InputMethodImpl {
        IBinder myToken;

        private SwypeInputMethodImpl() {
            super(IME.this);
        }

        @Override // android.inputmethodservice.InputMethodService.InputMethodImpl, android.view.inputmethod.InputMethod
        public void attachToken(IBinder token) {
            super.attachToken(token);
            this.myToken = token;
            IME.this.onTokenAttached();
        }
    }

    protected void onTokenAttached() {
    }

    public IBinder getToken() {
        if (this.myInputMethodImpl != null) {
            return this.myInputMethodImpl.myToken;
        }
        return null;
    }

    @Override // android.inputmethodservice.InputMethodService, android.inputmethodservice.AbstractInputMethodService
    public AbstractInputMethodService.AbstractInputMethodImpl onCreateInputMethodInterface() {
        this.myInputMethodImpl = new SwypeInputMethodImpl();
        return this.myInputMethodImpl;
    }

    public static void setLastActiveWord(CharSequence word) {
        if (mLastActiveWord.length() > 0) {
            mLastActiveWord.delete(0, mLastActiveWord.length());
        }
        if (word != null) {
            mLastActiveWord.append(word);
        }
    }

    public static String getLastSavedActiveWordAndSet() {
        String word = mLastActiveWord.toString();
        setLastActiveWord(null);
        return word;
    }

    public static String getLastSavedActiveWord() {
        return mLastActiveWord.toString();
    }

    public static void setLastShownCandidatesSource(Candidates.Source source) {
        mLastShownCandidatesSource = source;
    }

    public static Candidates.Source getLastShownCandidatesSource() {
        Candidates.Source last = mLastShownCandidatesSource;
        setLastShownCandidatesSource(Candidates.Source.INVALID);
        return last;
    }

    private void setCurrentInputViewName(String newView) {
        this.mCurrentInputViewName = newView;
    }

    public AbstractTapDetector createTapDetector(List<AbstractTapDetector.TapHandler> tapHandlers) {
        return new TapDetectorWindow((AbstractTapDetector.TapHandler[]) tapHandlers.toArray(new AbstractTapDetector.TapHandler[tapHandlers.size()]), getWindow().getWindow());
    }

    public EditState getEditState() {
        if (this.mEditState == null) {
            IMEApplication app = IMEApplication.from(this);
            if (Connect.from(this).isStatisticsCollectionEnabled()) {
                this.mEditState = new StatisticsEnabledEditState(app);
            } else {
                this.mEditState = new EditState(app);
            }
            InputView inputView = getCurrentInputView();
            if (inputView != null) {
                inputView.setEditState(this.mEditState);
            }
        }
        return this.mEditState;
    }

    @Override // android.inputmethodservice.AbstractInputMethodService, android.app.Service, android.content.ComponentCallbacks2
    public void onTrimMemory(int level) {
        log.d("onTrimMemory(", Integer.valueOf(level), ")");
        if (level == 20) {
            setRunningState(1);
        }
    }

    private void setRunningState(int runningStage) {
        IMEApplication.from(this).getSwypeCoreLibMgr().setRunningState(runningStage);
        this.mHandler.removeCallbacks(this.pendingHibernateState);
        if (runningStage == 1) {
            this.mHandler.postDelayed(this.pendingHibernateState, 300000L);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkPackageUpdate() {
        if (isImeInUse() || hasNewWordsScanning()) {
            UserPreferences.from(this).setCheckPackageUpdate(false);
            return;
        }
        log.d("begin check if Package available to upgrade.");
        if (!SDKUpdateManager.from(getApplicationContext()).isAvailable()) {
            SDKUpdateManager.from(getApplicationContext()).registerCallback(this);
        }
    }

    @Override // com.nuance.swypeconnect.ac.ACPlatformUpdateService.ACPlatformUpdateCallback
    public void updateAvailable() {
        checkPackageUpdate();
    }

    @Override // android.inputmethodservice.InputMethodService
    public void onViewClicked(boolean focusChanged) {
        SpeechWrapper spw = IMEApplication.from(this).getSpeechWrapper();
        if (spw != null && spw.isSpeechViewShowing()) {
            spw.cancelSpeech();
        }
        if (this.tapDetector != null) {
            this.tapDetector.onViewClicked(focusChanged);
        }
    }

    @Override // android.inputmethodservice.InputMethodService, android.app.Service
    public void onCreate() {
        IMEApplication imeApp = IMEApplication.from(this);
        if (UserManagerCompat.isUserUnlocked(this)) {
            DatabaseConfig.refreshDatabaseConfig(this, imeApp.getBuildInfo().getBuildDate());
        }
        if (UserManagerCompat.isUserUnlocked(this) && !imeApp.isUserUnlockFinished()) {
            imeApp.postUserUnlock();
        }
        AdsUtil.sAdsSupported = getResources().getBoolean(R.bool.enable_ads);
        AdsUtil.sTagForChildDirectedTreatment = UserPreferences.from(this).getTagForChildDirectedTreatment();
        this.keyboardInputInflater = new KeyboardInputInflater(this);
        this.mKeyboardBackgroundManager = new KeyboardBackgroundManager(getApplicationContext());
        imeApp.setIME(this);
        imeApp.getEmojiInputViewController().setContext(imeApp.getThemedContext());
        this.appPrefs = AppPreferences.from(this);
        this.upgrade = this.appPrefs.isUpgrade();
        this.mBuildInfo = imeApp.getBuildInfo();
        doEnableHardwaredAcceleration();
        log.i(String.format("%s version:%s BuildType:%s BuildDate:%s", getString(R.string.ime_name), this.mBuildInfo.getBuildVersion(), this.mBuildInfo.getBuildType().toString(), this.mBuildInfo.getBuildDateStr()));
        super.onCreate();
        Window window = getWindow().getWindow();
        this.extendedPointTracker = new ExtendedPointTracker(window.getCallback());
        window.setCallback(this.extendedPointTracker);
        this.mWantToast = true;
        this.mNeedLanguageInToast = true;
        registerReceiverMessages();
        setLastActiveWord(null);
        setLastShownCandidatesSource(Candidates.Source.INVALID);
        this.isHardkeyboardEnabled = UserPreferences.from(this).isHardwareKeyboardEnabled(this);
        imeApp.getSystemState().openWatch(this);
        SettingsChangeListener.getInstance().registerObserver(this);
        SettingsChangeListener.getInstance().addListener(this);
        this.portraitCandidatesViewFilter = imeApp.createPortraitCandidatesViewFilter();
        this.dragInsetAdjustMode = DragKeyboardInsetAdjustMode.fromInt(getResources().getInteger(R.integer.drag_inset_adjust_mode));
        String vendorCheckerClassName = getResources().getString(R.string.vendor_licensing);
        if (vendorCheckerClassName != null && vendorCheckerClassName.length() != 0) {
            this.mThirdPartyLicense = new ThirdPartyLicense(this);
        } else {
            this.mThirdPartyLicense = null;
        }
        onIMEUpgrading();
    }

    @Override // android.inputmethodservice.InputMethodService, android.inputmethodservice.AbstractInputMethodService, android.app.Service
    public void onDestroy() {
        log.d("onDestroy()", "called >>>>>>>>>>");
        if (this.mVoiceRecognitionTrigger != null) {
            VoiceRecognitionTrigger voiceRecognitionTrigger = this.mVoiceRecognitionTrigger;
            if (voiceRecognitionTrigger.mTrigger != null) {
                voiceRecognitionTrigger.mTrigger.onDestroy();
            }
        }
        IMEApplication imeApp = IMEApplication.from(this);
        UserPreferences userPrefs = UserPreferences.from(this);
        if (ThemeManager.isDownloadableThemesEnabled() && userPrefs.getMlsHotWordsImported() && !userPrefs.isImportedMlsHotWordsOver()) {
            NewWordsBucketFactory.NewWordsBucket bucket = imeApp.getNewWordsBucketFactory().getMlsThemeWordsBucketInstance();
            if (bucket.size() > 0) {
                log.d("scanning mls bucket interrupted");
                userPrefs.setMlsHotWordsImportedOver(false);
                userPrefs.setInt(UserPreferences.MLS_HOT_WORDS_LEFT_NUM, bucket.size());
            } else {
                log.d("scanning mls bucket over");
                userPrefs.setMlsHotWordsImportedOver(true);
                userPrefs.setInt(UserPreferences.MLS_HOT_WORDS_LEFT_NUM, -1);
            }
        }
        userPrefs.setCheckPackageUpdate(false);
        imeApp.getEmojiInputViewController().hide();
        super.hideWindow();
        SettingsChangeListener.getInstance().removeListener(this);
        SettingsChangeListener.getInstance().unregisterObserver();
        setLastActiveWord(null);
        setLastShownCandidatesSource(Candidates.Source.INVALID);
        removeAllPendingMsgs();
        resetInputView(true);
        try {
            unregisterReceiver(this.mReceiver);
        } catch (IllegalArgumentException ex) {
            log.e(ex.getMessage());
        }
        imeApp.destroySyncDataProviderManager();
        imeApp.destroySpeechWrapperInstance();
        imeApp.getSystemState().closeWatch(this);
        if (imeApp.getHardKeyIMEHandlerInstance() != null) {
            imeApp.getHardKeyIMEHandlerInstance().onDestroy();
        }
        this.mInputMethods = null;
        if (this.myInputMethodImpl != null) {
            this.myInputMethodImpl.myToken = null;
            this.myInputMethodImpl = null;
        }
        KeyboardStyle.recycleDrawable();
        AppSpecificInputConnection ic = getAppSpecificInputConnection();
        if (ic != null) {
            ic.clearExtractedTextCache();
        }
        imeApp.setIME(null);
        imeApp.releaseBillboardManager();
        SDKUpdateManager.from(getApplicationContext()).unregisterCallback(this);
        imeApp.getKeyboardManager().evictAll();
        super.onDestroy();
    }

    @Override // android.inputmethodservice.InputMethodService
    public InputConnection getCurrentInputConnection() {
        InputConnection ic = super.getCurrentInputConnection();
        if (ic == null) {
            return null;
        }
        if (ic != this.appSpecificInputConnection) {
            if (this.appSpecificInputConnection == null) {
                this.appSpecificInputConnection = new AppSpecificInputConnection(ic, this, getAppSpecificBehavior(), (ClipboardManager) getSystemService("clipboard"), IMEApplication.from(this).getCharacterUtilities());
            } else {
                this.appSpecificInputConnection.setTarget(ic, getAppSpecificBehavior());
            }
            return this.appSpecificInputConnection;
        }
        return ic;
    }

    public AppSpecificInputConnection getAppSpecificInputConnection() {
        InputConnection ic = getCurrentInputConnection();
        if (ic == null) {
            return null;
        }
        return (AppSpecificInputConnection) ic;
    }

    public void updateInputMethods(InputMethods im) {
        if (this.mInputMethods != null) {
            this.mInputMethods = im;
            setCurrentInputLanguage();
        } else {
            this.mInputMethods = im;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isEditorComposingText() {
        InputView inputView = getCurrentInputView();
        AppSpecificInputConnection ic = getAppSpecificInputConnection();
        if (!getAppSpecificBehavior().supportsGetTextWithStyles()) {
            return inputView != null && inputView.isComposingText();
        }
        if (inputView == null || !inputView.isTraceComposingText()) {
            return ic != null && ic.hasComposing();
        }
        return true;
    }

    public InputView getCurrentInputView() {
        return this.keyboardInputInflater.getInputView(this.mCurrentInputViewName);
    }

    public void resetInputView(boolean resetCore) {
        if (this.recaptureHandler != null) {
            this.recaptureHandler.clearMessages();
            this.recaptureHandler = null;
        }
        this.tapDetector = null;
        this.savedKeyboardState.save(getCurrentInputView());
        this.keyboardInputInflater.callAllInputViewToDestroy(resetCore);
        this.keyboardInputInflater.reset();
        this.mCurrentInputViewName = KeyboardInputInflater.NO_INPUTVIEW;
        resetContainerView();
        this.inputContainerView = null;
    }

    public void resetContainerView() {
        if (this.inputContainerView != null) {
            this.inputContainerView.removeItem();
        }
    }

    protected List<AbstractTapDetector.TapHandler> createTapHandlers(InputView inputView) {
        List<AbstractTapDetector.TapHandler> handlers = new ArrayList<>();
        if (this.recaptureHandler != null) {
            handlers.add(this.recaptureHandler);
        }
        handlers.add(inputView);
        return handlers;
    }

    public View createInputViewFor(String inputViewName) {
        View view = this.keyboardInputInflater.inflateKeyboardInput(inputViewName);
        InputView inputView = this.keyboardInputInflater.getInputView(inputViewName);
        enableHwAccel(view);
        if (view != inputView) {
            enableHwAccel(inputView);
        }
        XT9CoreInput coreInput = inputView.getXT9CoreInput();
        if (coreInput != null) {
            IMEApplication.from(this).setInputCategory(coreInput.getInputCoreCategory(), this.mCurrentInputLanguage.getCoreLanguageId());
        }
        BuildInfo buildInfo = BuildInfo.from(this);
        if (buildInfo.isTrialBuild()) {
            buildInfo.updateExpirationPeriod();
        }
        checkBuildValid();
        if (isValidBuild()) {
            if (this.tapDetector == null || this.recaptureHandler == null || !this.recaptureHandler.isUsingInputView(inputView)) {
                if (this.recaptureHandler != null) {
                    this.recaptureHandler.clearMessages();
                    this.recaptureHandler = null;
                }
                if (this.mCurrentInputLanguage.isRecaptureEnabled()) {
                    this.recaptureHandler = new RecaptureHandler(this, inputView, false);
                    this.tapDetector = createTapDetector(createTapHandlers(inputView));
                } else {
                    this.tapDetector = createTapDetector(createTapHandlers(inputView));
                }
            }
        } else {
            if (this.recaptureHandler != null) {
                this.recaptureHandler.clearMessages();
                this.recaptureHandler = null;
            }
            this.tapDetector = null;
        }
        return view;
    }

    public RecaptureHandler getRecaptureHandler() {
        return this.recaptureHandler;
    }

    public View getCandidatesViewCreate() {
        View cv = null;
        final IMEApplication imeApp = IMEApplication.from(this);
        InputView iv = getCurrentInputView();
        final AppPreferences appPreferences = AppPreferences.from(getApplicationContext());
        if (iv != null) {
            cv = iv.getWordCandidateListContainer();
            iv.updateCandidatesView();
            if (cv == null) {
                cv = getCurrentInputView().createCandidatesView(this.recaptureHandler);
                if (isAccessibilitySupportEnabled()) {
                    iv.addCandidateListener(WordSelectionState.getInstance());
                }
                enableHwAccel(cv);
            } else {
                final ImageButton toggle = (ImageButton) cv.findViewById(R.id.toggle);
                if (this.mCurrentInputLanguage.isHindiLanguage() && !this.mCurrentInputLanguage.isBilingualLanguage()) {
                    toggle.setOnClickListener(new View.OnClickListener() { // from class: com.nuance.swype.input.IME.2
                        @Override // android.view.View.OnClickListener
                        public void onClick(View v) {
                            if (!IME.this.mCurrentInputLanguage.getCurrentInputMode().mInputMode.equals(InputMethods.KEYBOARD_TRANSLITERATION)) {
                                toggle.setBackground(imeApp.getThemedDrawable(R.attr.iconTranslitHiQwerty));
                                IME.this.toggleInputMode(InputMethods.KEYBOARD_TRANSLITERATION);
                            } else {
                                toggle.setBackground(imeApp.getThemedDrawable(R.attr.iconTranslitHiHindi));
                                String mode = appPreferences.getString(AppPreferences.PREF_HINDI_INPUT_MODE, "devanagariAlphabetic");
                                IME.this.toggleInputMode(mode);
                            }
                        }
                    });
                } else if (toggle != null) {
                    toggle.setVisibility(8);
                }
            }
        }
        return cv;
    }

    @Override // android.inputmethodservice.InputMethodService
    public void onUnbindInput() {
        log.d("onUnbindInput() : method called : ");
        super.onUnbindInput();
        InputView view = getCurrentInputView();
        if (view != null) {
            view.onApplicationUnbind();
        }
    }

    public void onInitializeInterfaceCommon() {
        SpeechWrapper spw;
        IMEApplication imeApp = IMEApplication.from(this);
        Configuration config = getResources().getConfiguration();
        InputView inputView = getCurrentInputView();
        boolean isLocaleChanged = false;
        if (inputView != null) {
            this.prevLastInput = inputView.getLastInput();
            inputView.onConfigurationChanged(this.lastConfiguration, config, getCurrentInputConnection());
        }
        if (this.lastConfiguration != null) {
            int diffMask = config.diff(this.lastConfiguration);
            this.isOrientationChanged = (diffMask & 128) != 0;
            isLocaleChanged = (diffMask & 4) != 0;
        }
        this.lastConfiguration = new Configuration(config);
        this.simulateTapOnPostOrientationChangePending = false;
        if ((this.isOrientationChanged || this.isAccessibilityChanged) && inputView != null) {
            this.simulateTapOnPostOrientationChangePending = true;
            this.ignoreFirstUpdateSelectionPostOrientationChange = isEditorComposingText();
        }
        if (isLocaleChanged) {
            this.popupLanguageList = null;
        }
        IMEApplication.from(this).getEmojiInputViewController().onConfigChanged(this.isOrientationChanged);
        if (!(config.hardKeyboardHidden == 2) && (spw = imeApp.getSpeechWrapper()) != null) {
            log.d("onInitializeInterfaceCommon(): cancel speech...");
            spw.cancelSpeech();
        }
        resetInputView(false);
        removeAllPendingMsgs();
        runLicenseCheck();
        setLastActiveWord(null);
        setLastShownCandidatesSource(Candidates.Source.INVALID);
        if (this.tapDetector != null) {
            this.tapDetector.onInitializeInterface();
        }
    }

    @Override // android.inputmethodservice.InputMethodService
    public void onInitializeInterface() {
        IMEApplication imeApp = IMEApplication.from(this);
        imeApp.onConfigurationChanged(getResources().getConfiguration());
        onInitializeInterfaceCommon();
        if (imeApp.getIMEHandlerInstance() != null) {
            imeApp.getIMEHandlerInstance().onInitializeInterface();
        }
    }

    @Override // android.inputmethodservice.InputMethodService
    public View onCreateInputView() {
        startLoadingKeyboardBackground();
        if (IMEApplication.from(this).getIMEHandlerInstance() != null) {
            return IMEApplication.from(this).getIMEHandlerInstance().onCreateInputView();
        }
        this.mAccessibilityInfo = getAccessibilityInfo();
        if (this.mAccessibilityInfo != null) {
            this.mAccessibilityInfo.syncWithDeviceAccessiblityState();
        }
        if (this.mInputFieldInfo == null) {
            this.mInputFieldInfo = new InputFieldInfo(this);
        }
        this.mInputMethods = getInputMethods();
        setCurrentInputLanguage();
        setCurrentInputViewName(getAlphaInputViewName(this.mCurrentInputLanguage.getCurrentInputMode()));
        View inputAreaView = createInputViewFor(this.mCurrentInputViewName);
        getCurrentInputView().setCurrentInputLanguage(this.mCurrentInputLanguage);
        if (this.mAccessibilityInfo != null) {
            this.mAccessibilityInfo.updateCurrentLanguage(this.mCurrentInputLanguage);
        }
        initInputContainerView(getCandidatesView(), inputAreaView);
        return this.inputContainerView;
    }

    private void startLoadingKeyboardBackground() {
        final KeyboardEx.KeyboardDockMode dockMode = UserPreferences.from(this).getKeyboardDockingMode();
        final int orientation = getResources().getConfiguration().orientation;
        log.d("\t Dock mode is: " + dockMode.toString());
        log.d("\t Orientation is: " + orientation);
        ThemeManager.SwypeTheme theme = IMEApplication.from(this).getCurrentTheme();
        if ((this.mKeyboardBackgroundManager.mReloadRequiredFromResources || this.mKeyboardBackgroundManager.hasConfigChanged(theme.getSku(), dockMode, orientation)) && this.mKeyboardBackgroundManager.shouldLoadFromDisk(theme, dockMode)) {
            log.d("Config has changed, loading the background");
            final KeyboardBackgroundManager keyboardBackgroundManager = this.mKeyboardBackgroundManager;
            final String sku = theme.getSku();
            final Handler handler = this.mHandler;
            new Thread(new Runnable() { // from class: com.nuance.swype.util.drawable.KeyboardBackgroundManager.1
                @Override // java.lang.Runnable
                public final void run() {
                    KeyboardBackgroundManager.log.d("Started new thread to load background");
                    KeyboardBackgroundManager.this.loadBackground(sku, dockMode, orientation, handler);
                }
            }).start();
            return;
        }
        log.d("Config has not changed or should not load from disk.");
    }

    @Override // android.inputmethodservice.InputMethodService
    public View onCreateCandidatesView() {
        if (this.mInputFieldInfo == null) {
            this.mInputFieldInfo = new InputFieldInfo(this);
            return null;
        }
        return null;
    }

    public boolean setCurrentInputLanguage() {
        this.mInputMethods = getInputMethods();
        if (this.mInputMethods == null) {
            return false;
        }
        this.mInputMethods.syncWithCurrentUserConfiguration();
        return setInputLanguage(this.mInputMethods.getCurrentInputLanguage());
    }

    public InputMethods getInputMethods() {
        if (this.mInputMethods == null) {
            this.mInputMethods = InputMethods.from(this);
        }
        return this.mInputMethods;
    }

    private boolean setInputLanguage(InputMethods.Language inputLanguage) {
        if (inputLanguage == null) {
            log.i("IME::setInputLanguage(): inputLanguage is null");
            inputLanguage = this.mInputMethods.getDefaultAlphabeticInputLanguage();
            this.mInputMethods.setCurrentLanguage(inputLanguage.getLanguageId());
        }
        boolean langChanged = this.mCurrentInputLanguage == null || !this.mCurrentInputLanguage.equals(inputLanguage);
        if (langChanged) {
            this.mWantToast = true;
        }
        this.mNeedLanguageInToast = this.mWantToast || this.mCurrentInputLanguage == null || langChanged;
        this.mCurrentInputLanguage = inputLanguage;
        IMEApplication.from(this).setCurrentLanguage(inputLanguage);
        this.mAccessibilityInfo = getAccessibilityInfo();
        if (this.mAccessibilityInfo != null) {
            this.mAccessibilityInfo.updateCurrentLanguage(this.mCurrentInputLanguage);
        }
        return langChanged;
    }

    String getAlphaInputViewName(InputMethods.InputMode inputMode) {
        return inputMode.isHandwriting() ? KeyboardInputInflater.ALPHA_HANDWRITING : KeyboardInputInflater.ALPHA_KEYBOARDINPUT;
    }

    public String retrieveCurrentInputViewName() {
        boolean overrideCurrentMode = false;
        if (this.mInputFieldInfo != null) {
            overrideCurrentMode = this.mInputFieldInfo.isPhoneNumberField();
        }
        setCurrentInputLanguage();
        this.mKeyboardBackgroundManager.setReloadRequiredFromResources(true);
        InputMethods.InputMode inputMode = this.mCurrentInputLanguage.getCurrentInputMode();
        if (this.mCurrentInputLanguage.isKoreanLanguage()) {
            if (inputMode.isHandwriting()) {
                if (overrideCurrentMode) {
                    this.mCurrentInputViewName = KeyboardInputInflater.KOREAN_INPUT;
                } else {
                    this.mCurrentInputViewName = KeyboardInputInflater.KOREAN_HANDWRITING;
                }
            } else {
                this.mCurrentInputViewName = KeyboardInputInflater.KOREAN_INPUT;
            }
        } else if (this.mCurrentInputLanguage.isChineseLanguage()) {
            if (inputMode.isHandwriting()) {
                if (overrideCurrentMode) {
                    this.mCurrentInputViewName = KeyboardInputInflater.CHINESE_INPUT;
                } else {
                    AppPreferences appPrefs = AppPreferences.from(this);
                    if (appPrefs.getBoolean(AppPreferences.CJK_FULL_SCREEN_ENABLED + this.mCurrentInputLanguage.getCoreLanguageId(), appPrefs.getDefaultFullscreenHandwriting())) {
                        this.mCurrentInputViewName = KeyboardInputInflater.CHINESEFS_HANDWRITING;
                    } else {
                        this.mCurrentInputViewName = KeyboardInputInflater.CHINESE_HANDWRITING;
                    }
                }
            } else {
                this.mCurrentInputViewName = KeyboardInputInflater.CHINESE_INPUT;
            }
        } else if (this.mCurrentInputLanguage.isJapaneseLanguage()) {
            if (inputMode.isHandwriting()) {
                if (overrideCurrentMode) {
                    this.mCurrentInputViewName = KeyboardInputInflater.JAPANESE_INPUT;
                } else {
                    this.mCurrentInputViewName = KeyboardInputInflater.JAPANESE_HANDWRITING;
                }
            } else {
                this.mCurrentInputViewName = KeyboardInputInflater.JAPANESE_INPUT;
            }
        } else if (inputMode.isHandwriting()) {
            this.mCurrentInputViewName = overrideCurrentMode ? KeyboardInputInflater.ALPHA_KEYBOARDINPUT : KeyboardInputInflater.ALPHA_HANDWRITING;
        } else {
            this.mCurrentInputViewName = KeyboardInputInflater.ALPHA_KEYBOARDINPUT;
        }
        return this.mCurrentInputViewName;
    }

    public InputContainerView getInputContainerView() {
        return this.inputContainerView;
    }

    @SuppressLint({"InflateParams"})
    protected InputContainerView createInputContainerView() {
        log.d("createInputContainerView()");
        InputContainerView out = (InputContainerView) IMEApplication.from(this).getThemedLayoutInflater(getLayoutInflater()).inflate(R.layout.input_container_view, (ViewGroup) null);
        enableHwAccel(out);
        return out;
    }

    protected void initInputContainerView(View candidatesView, View inputView) {
        if (this.inputContainerView == null) {
            this.inputContainerView = createInputContainerView();
        }
        KeyboardEx.KeyboardDockMode mode = UserPreferences.from(this).getKeyboardDockingMode();
        boolean miniFloatSupported = IMEApplication.from(getApplicationContext()).isMiniKeyboardSupported(getResources().getConfiguration().orientation);
        boolean forceUseWing = (InputMethodServiceCompat.isTouchableRegionSupported() && miniFloatSupported && (!miniFloatSupported || mode == KeyboardEx.KeyboardDockMode.MOVABLE_MINI)) ? false : true;
        this.inputContainerView.init(inputView, candidatesView, !forceUseWing);
        this.inputContainerView.setFullScreenMode(isFullscreenMode());
        this.inputContainerView.showInputArea(!isHardKeyboardActive());
        boolean showCandidate = true;
        InputFieldInfo inputFieldInfo = getInputFieldInfo();
        if (inputFieldInfo != null) {
            showCandidate = !inputFieldInfo.isPasswordField();
        }
        this.inputContainerView.showCandidates(showCandidate);
        this.inputContainerView.setAllowedMovement(true, getCurrentInputView().isFullScreenHandWritingView() ? false : true);
        if (!IMEApplication.from(this).isScreenLayoutTablet() && getCurrentInputView().isHandWritingInputView() && getCurrentInputView().isNormalTextInputMode()) {
            mode = KeyboardEx.KeyboardDockMode.DOCK_FULL;
        }
        this.inputContainerView.setMode(mode);
    }

    @Override // android.inputmethodservice.InputMethodService, android.inputmethodservice.AbstractInputMethodService, android.app.Service
    protected void dump(FileDescriptor fd, PrintWriter fout, String[] args) {
        super.dump(fd, fout, args);
    }

    public void switchHardInputView(boolean restarting) {
        switchInputView(restarting);
    }

    public void setModeForInputContainerView() {
        if (this.inputContainerView != null) {
            KeyboardEx.KeyboardDockMode mode = UserPreferences.from(this).getKeyboardDockingMode();
            boolean miniFloatSupported = IMEApplication.from(getApplicationContext()).isMiniKeyboardSupported(getResources().getConfiguration().orientation);
            boolean forceUseWing = (InputMethodServiceCompat.isTouchableRegionSupported() && miniFloatSupported && (!miniFloatSupported || mode == KeyboardEx.KeyboardDockMode.MOVABLE_MINI)) ? false : true;
            this.inputContainerView.refresh(!forceUseWing);
            this.inputContainerView.setFullScreenMode(isFullscreenMode());
            this.inputContainerView.showInputArea(!isHardKeyboardActive());
            this.inputContainerView.showCandidates(!getInputFieldInfo().isPasswordField());
            this.inputContainerView.setAllowedMovement(true, getCurrentInputView().isFullScreenHandWritingView() ? false : true);
            if (!IMEApplication.from(this).isScreenLayoutTablet() && getCurrentInputView().isHandWritingInputView() && getCurrentInputView().isNormalTextInputMode()) {
                mode = KeyboardEx.KeyboardDockMode.DOCK_FULL;
            }
            this.inputContainerView.setMode(mode);
            setInputView(this.inputContainerView);
            updateInputViewShown();
        }
    }

    public void switchInputView(boolean restarting) {
        retrieveCurrentInputViewName();
        if (AdsUtil.sAdsSupported) {
            InputMethods.InputMode inputMode = this.mCurrentInputLanguage.getCurrentInputMode();
            log.d("Setting handwriting to ", Boolean.valueOf(inputMode.isHandwriting()), " when switching input view");
            IMEApplication.from(getApplicationContext()).getAdSessionTracker().setHandwriting(inputMode.isHandwriting());
        }
        View inputAreaView = createInputViewFor(this.mCurrentInputViewName);
        initInputContainerView(getCandidatesView(), inputAreaView);
        setupInputView(restarting);
        setInputView(this.inputContainerView);
        updateInputViewShown();
        UsageManager usageMgr = UsageManager.from(this);
        XT9CoreInput coreInput = getCurrentInputView().getXT9CoreInput();
        if (coreInput != null && usageMgr != null) {
            usageMgr.getKeyboardUsageScribe().recordKeyboardPageXML(coreInput.getKeyboardPageXML());
        }
        if (getCurrentInputView().isInputSessionStarted()) {
            doRecaptureWhenSwitching();
        }
        if (UserManagerCompat.isUserUnlocked(this) && IMEApplication.from(this).isUserUnlockFinished()) {
            postDelayCheckLanguageUpdateMessage();
        }
    }

    private View getCandidatesView() {
        log.d("Getting candidates view");
        return getCandidatesViewCreate();
    }

    void setupInputView(boolean restarting) {
        this.mHandler.removeMessages(100);
        this.mLastKey = -1;
        if (this.mInputFieldInfo == null) {
            this.mInputFieldInfo = new InputFieldInfo(this);
        }
        showCurrentLanguage();
        getCandidatesView();
        InputView currentInputView = getCurrentInputView();
        currentInputView.setCurrentInputLanguage(this.mCurrentInputLanguage);
        currentInputView.startInput(this.mInputFieldInfo, restarting);
        setSpaceKeyAsLanguageSwitchKey();
        currentInputView.setOnKeyboardActionListener(this);
    }

    @Override // android.inputmethodservice.InputMethodService
    public void onStartInput(EditorInfo attribute, boolean restarting) {
        boolean z;
        this.mHandler.removeMessages(117);
        if (this.mInputFieldInfo == null) {
            z = true;
        } else {
            z = !this.mInputFieldInfo.isEquivalentTo(attribute);
        }
        this.isEditorAttributeChanged = z;
        if (!getAppSpecificBehavior().shouldSkipWrongStartInputView() || this.isEditorAttributeChanged || !restarting) {
            if (this.mInputFieldInfo == null) {
                this.mInputFieldInfo = new InputFieldInfo(this);
            }
            this.mInputFieldInfo.setEditorInfo(attribute);
            this.isStartInputPending = true;
            super.onStartInput(attribute, restarting);
            if (this.tapDetector != null) {
                this.tapDetector.onStartInput();
            }
            HardKeyIMEHandler hardKeyHandler = (HardKeyIMEHandler) IMEApplication.from(this).getHardKeyIMEHandlerInstance();
            if (hardKeyHandler != null) {
                hardKeyHandler.setIME(this);
                IMEApplication.from(this).getHardKeyIMEHandlerInstance().onStartInput(attribute, restarting);
            }
        }
    }

    private void doStartInputInternal() {
        updateFullscreenMode();
        this.mAccessibilityInfo = getAccessibilityInfo();
        if (this.mAccessibilityInfo != null) {
            this.mAccessibilityInfo.syncWithDeviceAccessiblityState();
        }
        detectAccessibilityChange();
        if (this.mInputMethods != null) {
            this.mInputMethods.detectLocaleChange();
        }
    }

    public void onLocaleChanged() {
    }

    public boolean onStartInputViewProjectOverride(boolean restarting, boolean editorChanged, boolean orientation) {
        if (restarting || editorChanged || !orientation) {
            return false;
        }
        return true;
    }

    public void recaptureOnSingleTap(boolean restarting, boolean orientationChanged) {
        if (this.recaptureHandler != null) {
            this.recaptureHandler.onStartInputView();
            if (!this.allowCandidateViewShown) {
                setCandidatesViewShown(false);
            }
            if (restarting) {
                return;
            }
            if (!this.isOrientationChanged || this.simulateTapOnPostOrientationChangePending) {
                this.recaptureHandler.onSingleTap(false, orientationChanged);
            }
        }
    }

    public boolean reconstructByTap() {
        return this.recaptureHandler != null && this.recaptureHandler.reconstructByTap();
    }

    public void onStartInputViewCommon(EditorInfo attribute, boolean restarting) {
        Connect.from(this).onStartInput(attribute, restarting);
        IMEApplication imeApp = IMEApplication.from(this);
        imeApp.setCurrentFieldInfo(attribute.inputType);
        imeApp.setCurrentApplicationName(attribute.packageName);
        if (!restarting && AdsUtil.sAdsSupported) {
            log.d("Requesting incrementing session tracker");
            imeApp.getAdSessionTracker().requestIncrement();
        }
        setInputFieldInfo(attribute);
        loadSettings();
        setImeInUse(true);
        boolean restoreLayer = onStartInputViewProjectOverride(restarting, this.isEditorAttributeChanged, this.isOrientationChanged);
        if (this.isEditorAttributeChanged || this.isAccessibilityChanged) {
            restarting = false;
        }
        setRunningState(SwypeCoreLibrary.RUNNING_STATE_FOREGROUND_UI);
        setImeInUse(true);
        if (!restarting || getCurrentInputView() == null) {
            this.mUsedEditLayer = false;
            switchInputView(restarting);
        } else {
            getCurrentInputView().startInput(getInputFieldInfo(), restarting);
        }
        if (this.keyboardInputInflater.isEmpty()) {
            log.d("onStartInput(): no input view");
            return;
        }
        InputView currentInputView = getCurrentInputView();
        if (this.keyboardInputInflater.isEmpty()) {
            log.d("onStartInput(): no input view");
            return;
        }
        if (restoreLayer) {
            this.savedKeyboardState.restore(currentInputView, this.isOrientationChanged || this.isAccessibilityChanged);
        } else if (imeApp.hasActiveIMEManagerInstance()) {
            imeApp.getIMEHandlerManager().toggleLanguageOrRestore(this.mInputFieldInfo, getCurrentInputView());
        }
        setModeForInputContainerView();
        this.simulateTapOnPostOrientationChangePending = false;
        if (!KeyboardInputInflater.CHINESEFS_HANDWRITING.equals(this.mCurrentInputViewName)) {
            showStartupMessage();
        }
        if (this.isOrientationChanged) {
            InputView curInputView = getCurrentInputView();
            StatisticsManager stats = StatisticsManager.from(this);
            if (curInputView != null && curInputView.getKeyboard() != null && stats != null && stats.getSessionStatsScribe() != null) {
                StringBuilder size = new StringBuilder();
                size.append(curInputView.getKeyboard().getMinWidth());
                size.append('x');
                size.append(curInputView.getKeyboard().getHeight());
                stats.getSessionStatsScribe().recordKeyboardSizeChange(size.toString());
            }
            if (this.prevLastInput != 0) {
                if (curInputView != null) {
                    curInputView.setLastInput(this.prevLastInput);
                }
                this.prevLastInput = 0;
            }
        }
        imeApp.getEmojiInputViewController().onStartInputView(restarting);
        this.isOrientationChanged = false;
        this.isAccessibilityChanged = false;
        if (isAccessibilitySupportEnabled() && currentInputView != null) {
            String languageInfo = null;
            if (this.mNeedLanguageInToast) {
                languageInfo = this.mCurrentInputLanguage.getDisplayName();
                this.mNeedLanguageInToast = false;
            }
            AccessibilityNotification.getInstance().notifyKeyboardOpen(getApplicationContext(), getResources().getConfiguration().orientation, currentInputView.getKeyboardLayer(), currentInputView.getShiftState(), languageInfo);
        }
        if (SettingsChangeListener.isScreenMagnificationOn()) {
            setCandidatesViewShown(true);
        }
        if (UserManagerCompat.isUserUnlocked(this) && imeApp.isUserUnlockFinished()) {
            postDelayCheckLanguageUpdateMessage();
            postDelayAddOemLDBWordsMessage();
        }
    }

    private void postDelayCheckLanguageUpdateMessage() {
        if (this.mCurrentInputLanguage != null && this.checkedLanguageUpdateList.size() > 0) {
            if (this.mCurrentInputLanguage instanceof BilingualLanguage) {
                BilingualLanguage bi = (BilingualLanguage) this.mCurrentInputLanguage;
                if (this.checkedLanguageUpdateList.indexOfValue(bi.getFirstLanguage().mCoreLanguageId) >= 0 && this.checkedLanguageUpdateList.indexOfValue(bi.getSecondLanguage().mCoreLanguageId) >= 0) {
                    return;
                }
            } else if (this.checkedLanguageUpdateList.indexOfValue(this.mCurrentInputLanguage.mCoreLanguageId) >= 0) {
                return;
            }
        }
        if (this.mHandler.hasMessages(119)) {
            this.mHandler.removeMessages(119);
        }
        this.mHandler.sendEmptyMessageDelayed(119, 1000L);
    }

    public void checkLanguageUpdates(int langId, boolean add) {
        if (this.checkedLanguageUpdateList != null) {
            if (add) {
                this.checkedLanguageUpdateList.put(langId, langId);
            } else {
                this.checkedLanguageUpdateList.delete(langId);
            }
        }
    }

    private void postDelayAddOemLDBWordsMessage() {
        if (this.mCurrentInputLanguage != null && !this.mCurrentInputLanguage.isCJK()) {
            if (this.mHandler.hasMessages(125)) {
                this.mHandler.removeMessages(125);
            }
            this.mHandler.sendEmptyMessageDelayed(125, 5L);
        }
    }

    private void learnContextBuffer(String contextBuffer) {
        boolean z = false;
        if (isHardKeyboardActive() || ActivityManagerCompat.isUserAMonkey()) {
            log.d("input view is null or monkey, skipping learn context");
            return;
        }
        log.d("context text on restart input: ", contextBuffer);
        LogManager.Log log2 = log;
        Object[] objArr = new Object[2];
        objArr[0] = "wcl active: ";
        if (getCurrentInputView().getSuggestionCandidates() != null && getCurrentInputView().getSuggestionCandidates().count() > 0) {
            z = true;
        }
        objArr[1] = Boolean.valueOf(z);
        log2.d(objArr);
        connectLearnContextBuffer(contextBuffer);
        getCurrentInputView().markActiveWordUsedIfAny();
    }

    private void registerCursorMonitor() {
        if (Build.VERSION.SDK_INT >= 21 && getAppSpecificBehavior().shouldMonitoringCursorChange()) {
            if (this.mInputFieldInfo.isCompletionField()) {
                log.d("registerCursorMonitor");
                if (this.appSpecificInputConnection != null) {
                    this.appSpecificInputConnection.requestCursorUpdates(2);
                    return;
                }
                return;
            }
            this.appSpecificInputConnection.requestCursorUpdates(0);
        }
    }

    @Override // android.inputmethodservice.InputMethodService
    public void onStartInputView(EditorInfo attribute, boolean restarting) {
        log.d("onStartInputView...restarting: ", Boolean.valueOf(restarting));
        checkBuildValid();
        this.mHandler.removeMessages(117);
        getAppSpecificBehavior().onStartInputView(attribute, restarting);
        if (restarting && this.appSpecificInputConnection != null) {
            learnContextBuffer(this.appSpecificInputConnection.getCachedContextBuffer());
        }
        if (this.mKeyboardBackgroundManager.mReloadRequiredFromResources) {
            startLoadingKeyboardBackground();
        }
        this.mHandler.removeCallbacks(this.closeRunnable);
        this.mHandler.removeCallbacks(this.pendingHibernateState);
        if (!getAppSpecificBehavior().shouldSkipWrongStartInputView() || this.isEditorAttributeChanged || !restarting) {
            if (this.isStartInputPending) {
                doStartInputInternal();
                this.isStartInputPending = false;
            }
            if (this.mHandler.hasMessages(123)) {
                this.mHandler.removeCallbacks(this.finishInputViewRunnable);
                this.mHandler.removeMessages(123);
                doFinishInputView(false, false);
            }
            super.onStartInputView(attribute, restarting);
            if (restarting && !this.isEditorAttributeChanged && getAppSpecificBehavior().shouldFilterInputViewRestarts()) {
                long currentTimestamp = SystemClock.uptimeMillis();
                if (currentTimestamp - this.lastAllowedTime > 700) {
                    this.lastAllowedTime = currentTimestamp;
                } else {
                    return;
                }
            }
            onStartInputViewCommon(attribute, restarting);
            setEmojiExtraRegion(0);
            IMEApplication imeApp = IMEApplication.from(this);
            if (imeApp.hasActiveIMEManagerInstance() && imeApp.getIMEHandlerInstance() != null) {
                imeApp.getIMEHandlerInstance().onStartInputView(attribute, restarting);
                imeApp.getIMEHandlerManager().toggleLanguageOrRestore(this.mInputFieldInfo, getCurrentInputView());
            }
            if (this.mVoiceRecognitionTrigger != null) {
                VoiceRecognitionTrigger voiceRecognitionTrigger = this.mVoiceRecognitionTrigger;
                if (voiceRecognitionTrigger.mTrigger != null ? voiceRecognitionTrigger.mTrigger.hasRecognitionResultToCommit() : false) {
                    log.d("cursor onStartInputView");
                    getCurrentInputView().flushCurrentActiveWord();
                }
                VoiceRecognitionTrigger voiceRecognitionTrigger2 = this.mVoiceRecognitionTrigger;
                if (voiceRecognitionTrigger2.mTrigger != null) {
                    voiceRecognitionTrigger2.mTrigger.onStartInputView();
                }
                voiceRecognitionTrigger2.mTrigger = voiceRecognitionTrigger2.getTrigger();
            }
            registerCursorMonitor();
        }
    }

    public void onFinishInputViewProjectOverride(boolean finishingInput, InputView inputView, KeyboardState savedState) {
        if (inputView.mKeyboardSwitcher != null) {
            inputView.mKeyboardSwitcher.resetLayerState();
        }
        inputView.finishInput();
        inputView.setOnKeyboardActionListener((IME) null);
        if (finishingInput) {
            savedState.clear();
        } else {
            savedState.save(inputView);
        }
    }

    @Override // android.inputmethodservice.InputMethodService
    public void onFinishInputView(boolean finishingInput) {
        log.d("onFinishInputView() : method called : ");
        if (!this.keyboardInputInflater.isEmpty() && getCurrentInputView() != null) {
            if (finishingInput) {
                if (this.appSpecificInputConnection != null) {
                    learnContextBuffer(this.appSpecificInputConnection.getCachedContextBuffer());
                }
                doFinishInputView(finishingInput, false);
            } else {
                this.mHandler.removeCallbacks(this.finishInputViewRunnable);
                this.mHandler.post(this.finishInputViewRunnable);
                this.mHandler.removeMessages(123);
                this.mHandler.sendEmptyMessage(123);
            }
            IMEApplication.from(this).getEmojiInputViewController().onFinishInputView(finishingInput);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doFinishInputView(boolean finishingInput, boolean startScan) {
        setImeInUse(false);
        if (!this.keyboardInputInflater.isEmpty()) {
            getAppSpecificBehavior().onFinishInputView(finishingInput);
            closeDialogs();
            removeAllPendingMsgs();
            InputView inputView = getCurrentInputView();
            if (inputView != null) {
                onFinishInputViewProjectOverride(finishingInput, inputView, this.savedKeyboardState);
            }
            IMEApplication imeApp = IMEApplication.from(this);
            if (imeApp.hasActiveIMEManagerInstance() && imeApp.getIMEHandlerInstance() != null) {
                imeApp.getIMEHandlerInstance().onFinishInputView(finishingInput);
            }
            if (isAccessibilitySupportEnabled()) {
                AccessibilityNotification.getInstance().notifyKeyboardClose(getApplicationContext());
            }
            if (startScan && UserManagerCompat.isUserUnlocked(this)) {
                startPendingScanning();
            }
        }
    }

    public void clearSavedKeyboardState() {
        this.savedKeyboardState.clear();
    }

    @Override // android.inputmethodservice.InputMethodService
    public void onFinishInput() {
        log.d("onFinishInput() : method called :");
        this.isStartInputPending = false;
        setCandidatesViewShown(false);
        IMEApplication imeApp = IMEApplication.from(this);
        if (imeApp.getHardKeyIMEHandler() != null) {
            imeApp.getHardKeyIMEHandler().onFinishInput();
        }
        if (imeApp.hasActiveIMEManagerInstance() && imeApp.getIMEHandlerInstance() != null) {
            imeApp.getIMEHandlerInstance().onFinishInput();
        }
    }

    public void setInputFieldInfo(EditorInfo editorInfo) {
        Resources res;
        Configuration config;
        this.mInputFieldInfo.setEditorInfo(editorInfo);
        if (this.mInputFieldInfo.isEditDictionaryField()) {
            this.allowCandidateViewShown = false;
            return;
        }
        if (isHardKeyboardActive() && this.mInputFieldInfo.isInputTypeNull()) {
            this.allowCandidateViewShown = false;
            return;
        }
        this.allowCandidateViewShown = true;
        if (!isFullscreenMode() && (config = (res = getResources()).getConfiguration()) != null) {
            if (config.orientation == 1) {
                String what = this.mInputFieldInfo.getPackageName();
                if (!this.portraitCandidatesViewFilter.allows(what)) {
                    this.allowCandidateViewShown = false;
                }
                String type = this.mInputFieldInfo.getInputType();
                if (!TextUtils.isEmpty(type) && !this.portraitCandidatesViewFilter.allows(what + ":" + type)) {
                    this.allowCandidateViewShown = false;
                    return;
                }
                return;
            }
            if (res.getBoolean(R.bool.enable_candidates_sw_threshold_check)) {
                float swThreshold = res.getDimension(R.dimen.landscape_candidates_sw_threshold);
                if (swThreshold > 0.0f) {
                    float swThreshold2 = swThreshold / res.getDisplayMetrics().density;
                    int smallestScreenWidthDp = ConfigurationCompat.getSmallestScreenWidthDp(config);
                    if (smallestScreenWidthDp > 0 && smallestScreenWidthDp < swThreshold2) {
                        if (!((this.mInputFieldInfo.isCompletionField() || this.mInputFieldInfo.isNoSuggestionOnField() || this.mInputFieldInfo.isFieldWithFilterList()) ? false : true) && !this.mCurrentInputLanguage.isChineseLanguage()) {
                            this.allowCandidateViewShown = false;
                        }
                    }
                }
            }
        }
    }

    public InputFieldInfo getInputFieldInfo() {
        return this.mInputFieldInfo;
    }

    public void setSpaceKeyAsLanguageSwitchKey() {
        InputView inputView = getCurrentInputView();
        if (inputView != null) {
            refreshLanguageOnSpaceKey(inputView.getKeyboard(), inputView);
        }
    }

    public void refreshLanguageOnSpaceKey(KeyboardEx keyboard, KeyboardViewEx view) {
        if (this.mInputMethods != null && (keyboard instanceof XT9Keyboard)) {
            XT9Keyboard xt9Keyboard = (XT9Keyboard) keyboard;
            InputMethods.Language language = this.mInputMethods.getCurrentInputLanguage();
            for (KeyboardEx.Key key : xt9Keyboard.setLanguageSwitchKey(language, this.mInputMethods.countEnabledLanguageMode())) {
                view.invalidateKey(key);
            }
        }
    }

    public void showCurrentLanguage() {
        String langName = this.mCurrentInputLanguage.getDisplayName();
        if (langName != null && this.mWantToast && AppPreferences.from(this).showToolTip()) {
            if (!isAccessibilitySupportEnabled()) {
                if (isDeviceExploreByTouchOn() && this.mCurrentInputLanguage.isCJK()) {
                    InputMethodToast.show(this, langName + "\n" + getResources().getString(R.string.keyboard_not_support_explore_by_touch), 1);
                } else {
                    InputMethodToast.show(this, langName, 0);
                }
            }
            this.mWantToast = false;
        }
    }

    @Override // android.inputmethodservice.InputMethodService
    public void onUpdateSelection(int oldSelStart, int oldSelEnd, int newSelStart, int newSelEnd, int candidatesStart, int candidatesEnd) {
        super.onUpdateSelection(oldSelStart, oldSelEnd, newSelStart, newSelEnd, candidatesStart, candidatesEnd);
        log.d("onUpdateSelection() : method called : after ");
        log.d("onUpdateSelection(): oss: " + oldSelStart + "; ose: " + oldSelEnd + "; nss: " + newSelStart + "; nse: " + newSelEnd + "; cs: " + candidatesStart + "; " + candidatesEnd);
        if (this.ignoreFirstUpdateSelectionPostOrientationChange) {
            this.ignoreFirstUpdateSelectionPostOrientationChange = false;
            return;
        }
        if (!this.inUse || this.keyboardInputInflater.isEmpty() || getCurrentInputView() == null) {
            log.d("onUpdateSelection()...no input view");
            return;
        }
        int[] candidateIndices = {candidatesStart, candidatesEnd};
        getCurrentInputView().updateSelection(oldSelStart, oldSelEnd, newSelStart, newSelEnd, candidateIndices);
        if (this.recaptureHandler != null) {
            getCurrentInputView().recordStartTimeDisplaySelection();
            this.recaptureHandler.onUpdateSelection(oldSelStart, oldSelEnd, newSelStart, newSelEnd, candidateIndices[0], candidateIndices[1]);
            if (!ActivityManagerCompat.isUserAMonkey()) {
                getCurrentInputView().recordUsedTimeReselectDisplaySelectionList();
            }
        }
    }

    @Override // android.inputmethodservice.InputMethodService
    public void hideWindow() {
        log.d("hideWindow() : method called : ");
        if (this.mEditState != null && (this.mEditState instanceof StatisticsEnabledEditState)) {
            connectLearnContextBuffer(((StatisticsEnabledEditState) this.mEditState).getHideWindowInputContents());
        }
        super.hideWindow();
        setHardKeyboardAttached(false);
        handleClose();
        UsageData.recordKeyboardOpen(getApplicationContext());
        UserPreferences userPrefs = UserPreferences.from(this);
        if (!userPrefs.hasCheckedPackageUpdate()) {
            postBackgroundCheckPackageUpdate();
            userPrefs.setCheckPackageUpdate(true);
        }
        if (!IMEApplication.from(this).getStartupSequenceInfo().isInputFieldStartupOrPassword(getCurrentInputEditorInfo(), this.mInputFieldInfo)) {
            IMEApplication.from(this).getAppPreferences().setNewThemeAvailableInStore(false);
        }
    }

    @Override // android.inputmethodservice.InputMethodService
    public void onWindowShown() {
        log.d("onWindowShown...");
        super.onWindowShown();
        UsageData.startSession(getApplicationContext());
    }

    @Override // android.inputmethodservice.InputMethodService
    public void onWindowHidden() {
        log.d("onWindowHidden...");
        super.onWindowHidden();
        IMEApplication.from(this).updateCustomDimensions();
        UsageData.endSession(getApplicationContext());
    }

    private void postBackgroundCheckPackageUpdate() {
        if (this.mHandler != null) {
            this.mHandler.removeCallbacks(this.checkPackageUpdateRunnable);
            this.mHandler.postDelayed(this.checkPackageUpdateRunnable, 120000L);
        }
    }

    @Override // android.inputmethodservice.InputMethodService
    public void onDisplayCompletions(CompletionInfo[] completions) {
        log.d("onDisplayCompletions: ", completions);
        InputView inputView = getCurrentInputView();
        if (inputView != null) {
            inputView.displayCompletions(completions);
        }
    }

    @Override // android.inputmethodservice.InputMethodService
    public void onUpdateCursorAnchorInfo(CursorAnchorInfo info) {
        InputView inputView;
        log.d("onUpdateCursorAnchorInfo: ", info);
        if (Build.VERSION.SDK_INT >= 21 && info.getComposingTextStart() == -1 && (inputView = getCurrentInputView()) != null && inputView.isComposingText()) {
            inputView.clearSuggestions();
            getAppSpecificInputConnection().reSyncFromEditor();
            recaptureOnSingleTap(false, false);
        }
    }

    @Override // android.inputmethodservice.InputMethodService
    public void onExtractedTextClicked() {
        if (this.tapDetector != null) {
            this.tapDetector.onExtractedTextClicked();
        }
    }

    @Override // android.inputmethodservice.InputMethodService
    public boolean onShowInputRequested(int flags, boolean configChange) {
        if (this.isStartInputPending) {
            doStartInputInternal();
            this.isStartInputPending = false;
        }
        if (showStartupActivity()) {
            this.mWantToast = false;
            this.startupActivityShown = true;
            this.isStartupActivityShown = true;
            return true;
        }
        if (this.tapDetector != null) {
            this.tapDetector.onShowInputRequested(flags, configChange);
        }
        try {
            boolean showWindow = super.onShowInputRequested(flags, configChange);
            if (!showWindow && (flags & 1) == 0 && getResources().getConfiguration().keyboard != 1) {
                return true;
            }
            return showWindow;
        } catch (NullPointerException e) {
            log.e(e.getMessage());
            return false;
        }
    }

    @Override // android.inputmethodservice.InputMethodService
    public void onUpdateExtractedText(int token, ExtractedText text) {
        super.onUpdateExtractedText(token, text);
        if (!this.inUse || this.keyboardInputInflater.isEmpty()) {
            log.d("onUpdateSelection()...no input view");
        } else {
            getCurrentInputView().updateExtractedText(token, text);
        }
    }

    public boolean isShowCandidatesViewAllowed() {
        return this.allowCandidateViewShown;
    }

    protected boolean onSetCandidatesViewShown(boolean shown) {
        if (!shown && SettingsChangeListener.isScreenMagnificationOn() && isImeInUse()) {
            shown = true;
        }
        return shown && isShowCandidatesViewAllowed();
    }

    @Override // android.inputmethodservice.InputMethodService
    public void setCandidatesView(View view) {
        log.d("setCandidatesView(): ignoring (shouldn't be called)");
    }

    @Override // android.inputmethodservice.InputMethodService
    @SuppressLint({"MissingSuperCall"})
    public boolean onEvaluateInputViewShown() {
        super.onEvaluateInputViewShown();
        return true;
    }

    @Override // android.inputmethodservice.InputMethodService
    public void setCandidatesViewShown(boolean shown) {
        boolean shown2 = onSetCandidatesViewShown(shown);
        if (this.inputContainerView != null) {
            this.inputContainerView.showCandidates(shown2);
        }
    }

    public void setCoverView(View view, boolean enableAnim) {
        if (this.inputContainerView != null) {
            if (view != null && this.inputContainerView.isCoverShowing()) {
                log.w("setCoverView(): cover already showing (clobbering)");
            }
            this.inputContainerView.setCover(view, enableAnim);
        }
    }

    public void setCoverView(View view, boolean enableAnim, int width, int height) {
        if (this.inputContainerView != null) {
            this.inputContainerView.setCover(view, enableAnim, width, height);
        }
    }

    @Override // android.inputmethodservice.InputMethodService
    public void onConfigureWindow(Window win, boolean isFullscreen, boolean isCandidatesOnly) {
        if (this.startupActivityShown) {
            WindowManager.LayoutParams lp = win.getAttributes();
            lp.height = 0;
            win.setAttributes(lp);
            this.startupActivityShown = false;
        } else {
            super.onConfigureWindow(win, isFullscreen, isCandidatesOnly);
            this.isStartupActivityShown = false;
        }
        closeNetworkDialog();
    }

    @Override // android.inputmethodservice.InputMethodService
    public boolean onEvaluateFullscreenMode() {
        boolean isMiniFloatingKeyboardSupported = IMEApplication.from(this).isMiniKeyboardSupported(getResources().getConfiguration().orientation);
        return (!super.onEvaluateFullscreenMode() || isMiniFloatingKeyboardSupported || InputFieldInfo.noFullscreenMode(getCurrentInputEditorInfo())) ? false : true;
    }

    public Rect getSpeechPopupRectInWindowCoord() {
        if (getInputContainerView() != null) {
            return getInputContainerView().getVisibleWindowRect();
        }
        return null;
    }

    @Override // android.inputmethodservice.InputMethodService
    public void onComputeInsets(InputMethodService.Insets outInsets) {
        Region widgetsRegion;
        Region region;
        super.onComputeInsets(outInsets);
        InputContainerView containerView = getInputContainerView();
        if (containerView != null && containerView.isShown()) {
            View decor = getWindow().getWindow().getDecorView();
            Rect rect = containerView.getVisibleWindowRect();
            InputMethodServiceCompat.setTouchableRegion(outInsets, rect);
            if (containerView.isFullAppAreaMode()) {
                InputMethodServiceCompat.setTouchableRegion(outInsets, rect);
                this.mTouchableRegionRect.set(rect);
                if (DragKeyboardInsetAdjustMode.NONE == this.dragInsetAdjustMode) {
                    outInsets.contentTopInsets = decor.getHeight();
                    outInsets.visibleTopInsets = decor.getHeight();
                } else {
                    outInsets.visibleTopInsets = rect.top;
                    if (DragKeyboardInsetAdjustMode.ALL_INSETS == this.dragInsetAdjustMode) {
                        outInsets.contentTopInsets = outInsets.visibleTopInsets;
                    } else {
                        outInsets.contentTopInsets = decor.getHeight();
                    }
                }
            } else {
                outInsets.visibleTopInsets = rect.top;
                if (isFullscreenMode() && this.mInputFieldInfo != null && !this.mInputFieldInfo.isInputTypeNull()) {
                    outInsets.contentTopInsets = decor.getHeight();
                } else {
                    outInsets.contentTopInsets = outInsets.visibleTopInsets;
                }
                this.mTouchableRegionRect.set(0, outInsets.contentTopInsets, decor.getWidth(), decor.getHeight());
                InputMethodServiceCompat.setTouchableRegion(outInsets, this.mTouchableRegionRect);
            }
            InputView curInputView = getCurrentInputView();
            if (curInputView != null && curInputView.hasWidgetViews() && (widgetsRegion = containerView.getWidgetViewTouchableRegion()) != null && (region = InputMethodServiceCompat.getTouchableRegion(outInsets)) != null) {
                region.op(region, widgetsRegion, Region.Op.UNION);
            }
        }
    }

    public void onKeyDownInner(int keyCode, int indicator, KeyEvent event) {
        if (indicator == HARD_KEYBOARD_REOPEN_INPUTVIEW) {
            this.isCandidateViewOpening = false;
        }
        onKeyDown(keyCode, event);
    }

    private boolean isSamsungDevice() {
        return SAMSUNG.equalsIgnoreCase(Build.BRAND) || SAMSUNG.equalsIgnoreCase(Build.MANUFACTURER);
    }

    @Override // android.inputmethodservice.InputMethodService, android.view.KeyEvent.Callback
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        InputMethodManager imm;
        if ((!isHardKeyboardEnabled() || (event.getFlags() & 8) > 0) && getResources().getConfiguration().keyboard == 1 && getResources().getConfiguration().hardKeyboardHidden != 2) {
            return super.onKeyDown(keyCode, event);
        }
        if (getResources().getConfiguration().keyboard != 2 && getResources().getConfiguration().keyboard != 3 && getResources().getConfiguration().hardKeyboardHidden != 1 && (getResources().getConfiguration().hardKeyboardHidden != 2 || !isSamsungDevice())) {
            return super.onKeyDown(keyCode, event);
        }
        if (!isValidBuild() || event.isSystem() || keyCode == 4 || keyCode == 82 || keyCode == 27 || keyCode == 26 || keyCode == 25 || keyCode == 164 || keyCode == 24 || keyCode == 84 || keyCode == 3 || keyCode > 219) {
            return super.onKeyDown(keyCode, event);
        }
        if (keyCode == 111 && !this.inUse) {
            return super.onKeyDown(keyCode, event);
        }
        if (isInputViewShown() && event.getAction() == 0) {
            setHardKeyboardAttached(true);
        }
        HardKeyIMEHandler hardKeyHandler = (HardKeyIMEHandler) IMEApplication.from(this).getHardKeyIMEHandlerInstance();
        if (hardKeyHandler == null) {
            return false;
        }
        hardKeyHandler.setIME(this);
        if (!hardKeyHandler.isValidInputField()) {
            return false;
        }
        setImeInUse(true);
        if (this.mCurrentInputLanguage != null) {
            if (this.mInputFieldInfo == null || this.mInputFieldInfo.isInputTypeNull()) {
                return false;
            }
            UserPreferences userPrefs = UserPreferences.from(this);
            if (!hardKeyHandler.isLanguageSupportedByHardKeyboard()) {
                if (!userPrefs.getBoolean(this.mCurrentInputLanguage.mLanguageAbbr + HardKeyboardManager.HardKB_SUFFIX, false)) {
                    userPrefs.setBoolean(this.mCurrentInputLanguage.mLanguageAbbr + HardKeyboardManager.HardKB_SUFFIX, true);
                    InputMethodToast.show(this, getResources().getString(R.string.language_not_supported), 0);
                }
                return super.onKeyDown(keyCode, event);
            }
            if (!hardKeyHandler.isInputModeSupportedByHardKeyboard()) {
                return super.onKeyDown(keyCode, event);
            }
            if (!userPrefs.getBoolean(HardKeyboardManager.SHORTCUT_SETTINGS, false)) {
                userPrefs.setBoolean(HardKeyboardManager.SHORTCUT_SETTINGS, true);
                InputMethodToast.show(this, getResources().getString(R.string.hardkeyboard_shortcut_settings), 0);
            }
        }
        if (!isInputViewShown() && event.getAction() == 0 && (imm = (InputMethodManager) getSystemService("input_method")) != null && getToken() != null) {
            imm.showSoftInputFromInputMethod(getToken(), 0);
            setHardKeyboardAttached(true);
            this.isCandidateViewOpening = true;
            if (this.mHandler != null) {
                this.mHandler.removeMessages(117);
                this.mHandler.sendEmptyMessageDelayed(117, 0L);
                this.mHandler.removeMessages(116);
                this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(116, keyCode, HARD_KEYBOARD_REOPEN_INPUTVIEW, event), 0L);
            }
            return true;
        }
        if (this.isCandidateViewOpening && this.mHandler != null) {
            this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(116, keyCode, 0, event), 0L);
            return true;
        }
        if (hardKeyHandler.onKeyDown(keyCode, event)) {
            return true;
        }
        InputView inputView = getCurrentInputView();
        if (inputView == null || !inputView.handleKeyDown(keyCode, event)) {
            return super.onKeyDown(keyCode, event);
        }
        return true;
    }

    @Override // android.inputmethodservice.InputMethodService, android.view.KeyEvent.Callback
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if ((!isHardKeyboardEnabled() || (event.getFlags() & 8) > 0) && getResources().getConfiguration().keyboard == 1 && getResources().getConfiguration().hardKeyboardHidden != 2) {
            return super.onKeyUp(keyCode, event);
        }
        if (getResources().getConfiguration().keyboard != 2 && getResources().getConfiguration().keyboard != 3 && getResources().getConfiguration().hardKeyboardHidden != 1 && (getResources().getConfiguration().hardKeyboardHidden != 2 || !isSamsungDevice())) {
            return super.onKeyUp(keyCode, event);
        }
        if (!isValidBuild() || event.isSystem() || keyCode == 4 || keyCode == 82 || keyCode == 27 || keyCode == 26 || keyCode == 25 || keyCode == 164 || keyCode == 24 || keyCode == 84 || keyCode == 3 || keyCode > 219) {
            return super.onKeyUp(keyCode, event);
        }
        if (keyCode == 111 && !this.inUse) {
            return super.onKeyUp(keyCode, event);
        }
        if (IMEApplication.from(this).getHardKeyIMEHandlerInstance() == null || !((HardKeyIMEHandler) IMEApplication.from(this).getHardKeyIMEHandlerInstance()).onKeyUp(keyCode, event)) {
            return super.onKeyUp(keyCode, event);
        }
        return true;
    }

    @Override // com.nuance.swype.input.KeyboardViewEx.OnKeyboardActionListener
    public void onHardwareCharKey(int primaryCode, int[] keyCodes) {
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Failed to find 'out' block for switch in B:35:0x008b. Please report as an issue. */
    /* JADX WARN: Removed duplicated region for block: B:41:0x00a5  */
    /* JADX WARN: Removed duplicated region for block: B:55:0x00da  */
    /* JADX WARN: Removed duplicated region for block: B:57:0x00e6  */
    /* JADX WARN: Removed duplicated region for block: B:67:0x032e  */
    /* JADX WARN: Removed duplicated region for block: B:69:0x010f  */
    /* JADX WARN: Removed duplicated region for block: B:72:0x011c  */
    @Override // com.nuance.swype.input.KeyboardViewEx.OnKeyboardActionListener
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void onKey(android.graphics.Point r24, int r25, int[] r26, com.nuance.swype.input.KeyboardEx.Key r27, long r28) {
        /*
            Method dump skipped, instructions count: 988
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.nuance.swype.input.IME.onKey(android.graphics.Point, int, int[], com.nuance.swype.input.KeyboardEx$Key, long):void");
    }

    public void setUseEditLayer(boolean used) {
        this.mUsedEditLayer = used;
    }

    public void handleGoogleMapGesture() {
        AppSpecificInputConnection ic = getAppSpecificInputConnection();
        String selectedText = ic == null ? null : ic.getSelectedTextInEditor(getInputFieldInfo());
        String geoUrl = "geo:0,0";
        if (selectedText != null && selectedText.length() > 0) {
            geoUrl = "geo:0,0?q=" + Uri.encode(selectedText);
        }
        startApp("com.google.android.apps.maps", Uri.parse(geoUrl));
    }

    public boolean isLangPopupMenuShowing() {
        return this.popupLanguageList != null && this.popupLanguageList.isShowing();
    }

    public void dismissLangPopupMenu() {
        if (isLangPopupMenuShowing()) {
            this.popupLanguageList.dismiss();
        }
    }

    public boolean onHardKeyLangPopupMenu(int keyCode, KeyEvent event) {
        int i;
        if (!isLangPopupMenuShowing()) {
            return false;
        }
        PopupLanguageList popupLanguageList = this.popupLanguageList;
        int size = popupLanguageList.languageViews.size();
        if (!popupLanguageList.moreLanguages.isPressed()) {
            Iterator<View> it = popupLanguageList.languageViews.iterator();
            i = -1;
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                if (!it.next().isPressed()) {
                    i++;
                } else {
                    i++;
                    break;
                }
            }
        } else {
            i = -1;
        }
        if (keyCode == 19) {
            if (i == -1) {
                popupLanguageList.languageViews.get(size - 1).setPressed(true);
                popupLanguageList.moreLanguages.setPressed(false);
            } else {
                popupLanguageList.languageViews.get(i).setPressed(false);
                int i2 = i - 1;
                if (i2 >= 0) {
                    popupLanguageList.languageViews.get(i2).setPressed(true);
                } else {
                    popupLanguageList.moreLanguages.setPressed(true);
                }
            }
        } else if (keyCode == 20) {
            if (i == -1) {
                popupLanguageList.languageViews.get(0).setPressed(true);
                popupLanguageList.moreLanguages.setPressed(false);
            } else {
                popupLanguageList.languageViews.get(i).setPressed(false);
                int i3 = i + 1;
                if (i3 < size) {
                    popupLanguageList.languageViews.get(i3).setPressed(true);
                } else {
                    popupLanguageList.moreLanguages.setPressed(true);
                }
            }
        } else if (keyCode == 66 || keyCode == 62) {
            if (popupLanguageList.listener != null) {
                if (popupLanguageList.moreLanguages.isPressed()) {
                    popupLanguageList.listener.onMoreLanguages();
                } else {
                    Iterator<View> it2 = popupLanguageList.languageViews.iterator();
                    while (true) {
                        if (!it2.hasNext()) {
                            break;
                        }
                        View next = it2.next();
                        if (next.isPressed()) {
                            popupLanguageList.listener.onHardLangSelected(next.getTag().toString());
                            break;
                        }
                    }
                }
            }
            popupLanguageList.dismiss();
        }
        return true;
    }

    public void showLanguagePopupMenu(KeyboardEx.Key key) {
        IMEApplication app = IMEApplication.from(this);
        LayoutInflater inflater = app.getThemedLayoutInflater(LayoutInflater.from(app));
        app.getThemeLoader().setLayoutInflaterFactory(inflater);
        this.popupLanguageList = new PopupLanguageList(inflater, app.getCurrentThemeId());
        this.popupLanguageList.listener = new PopupLanguageList.PopupLanguageListener() { // from class: com.nuance.swype.input.IME.6
            @Override // com.nuance.swype.widget.PopupLanguageList.PopupLanguageListener
            public void onMoreLanguages() {
                IME.this.close();
                IMEApplication.from(IME.this).showLanguages();
            }

            @Override // com.nuance.swype.widget.PopupLanguageList.PopupLanguageListener
            public void onLanguageSelected(String languageId) {
                if (!ActivityManagerCompat.isUserAMonkey()) {
                    IME.this.getKeyboardBackgroundManager().setReloadRequiredFromResources(true);
                    InputMethods.Language language = IME.this.mInputMethods.findInputLanguage(languageId);
                    IME.this.mInputMethods.setCurrentLanguage(languageId);
                    IME.this.mWantToast = true;
                    InputView inputView = IME.this.getCurrentInputView();
                    if (inputView != null) {
                        inputView.finishInput();
                    }
                    IME.this.mHandler.removeMessages(100);
                    IME.this.mHandler.sendEmptyMessageDelayed(100, 5L);
                    IME.this.recaptureWhenSwitching = true;
                    Connect.from(IME.this).setCurrentLanguage(language, 2);
                }
            }

            @Override // com.nuance.swype.widget.PopupLanguageList.PopupLanguageListener
            public void onHardLangSelected(String languageId) {
                InputMethods.Language language = IME.this.mInputMethods.findInputLanguage(languageId);
                IME.this.mInputMethods.setCurrentLanguage(languageId);
                IME.this.mWantToast = true;
                InputView inputView = IME.this.getCurrentInputView();
                if (inputView != null) {
                    inputView.finishInput();
                }
                IMEApplication.from(IME.this).getIME().switchHardInputView(false);
                Connect.from(IME.this).setCurrentLanguage(language, 2);
            }
        };
        PopupLanguageList popupLanguageList = this.popupLanguageList;
        List<InputMethods.Language> recentLanguages = InputMethods.from(this).getRecentLanguages();
        ViewGroup viewGroup = (ViewGroup) popupLanguageList.getContentView().findViewById(R.id.language_list);
        viewGroup.removeAllViews();
        popupLanguageList.languageViews.clear();
        int size = recentLanguages.size() - 1;
        for (int i = size; i >= 0; i--) {
            InputMethods.Language language = recentLanguages.get(i);
            View inflate = popupLanguageList.inflater.inflate(R.layout.popup_language_list_item, (ViewGroup) null);
            TextView textView = (TextView) inflate.findViewById(R.id.language_label);
            textView.setText(language.getDisplayName());
            PopupLanguageList.setPopupBackground(textView);
            PopupLanguageList.setPopupColorText(textView);
            int dimensionPixelSize = inflate.getResources().getDimensionPixelSize(R.dimen.popup_language_list_padding);
            textView.setPadding(dimensionPixelSize, dimensionPixelSize, dimensionPixelSize, dimensionPixelSize);
            inflate.setTag(language.getLanguageId());
            viewGroup.addView(inflate);
            popupLanguageList.languageViews.add(inflate);
        }
        if (size >= 0) {
            popupLanguageList.languageViews.get(size).setPressed(true);
            popupLanguageList.moreLanguages.setPressed(false);
        } else {
            popupLanguageList.moreLanguages.setPressed(true);
        }
        if (!ActivityManagerCompat.isUserAMonkey()) {
            InputView inputView = getCurrentInputView();
            int[] pos = new int[2];
            if (key != null) {
                inputView.calcKeyTopCenterInWindow(key, pos);
            } else {
                this.inputContainerView.getVisiblePosInWindow(pos, 1);
            }
            pos[1] = pos[1] - inputView.getPopupYAdjust(key);
            View rootView = getWindow().getWindow().getDecorView();
            pos[0] = pos[0] - (rootView.getWidth() / 2);
            pos[1] = rootView.getHeight() - pos[1];
            if (key == null) {
                List<KeyboardEx.Key> keys = inputView.getKeyboard().getKeys();
                if (keys != null) {
                    key = inputView.getKey(keys.size() - 1);
                }
                if (key != null) {
                    this.popupLanguageList.showAtLocation(inputView, 81, pos[0], key.height * 2);
                    return;
                }
                return;
            }
            this.popupLanguageList.showAtLocation(inputView, 81, pos[0], (inputView.getHeight() - key.y) + getBottomBarHeight(inputView));
        }
    }

    void refreshInputViewLanguage() {
        InputView inputView = getCurrentInputView();
        inputView.finishInput();
        setCurrentInputLanguage();
        setupInputView(true);
        inputView.invalidate();
    }

    public void reloadKeyboard() {
        this.mEditState = null;
        if (this.recaptureHandler != null) {
            this.recaptureHandler.clearMessages();
            this.recaptureHandler = null;
        }
        this.tapDetector = null;
        this.savedKeyboardState.save(getCurrentInputView());
        if (!KeyboardInputInflater.NO_INPUTVIEW.equals(this.mCurrentInputViewName) && IMEApplication.from(this).getIMEHandlerInstance() == null) {
            this.keyboardInputInflater.reset();
            switchInputView(true);
        }
    }

    public boolean isNeedRefreshKeyboard() {
        return this.needRrefreshKeyboard;
    }

    public void refreshKeyboard() {
        log.d("refreshKeyboard()");
        this.mHandler.removeMessages(101);
        this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(101), 10L);
    }

    private void handleBackspace(int repeatedCount) {
        InputView inputView = getCurrentInputView();
        if (inputView == null || !inputView.handleKey(8, false, repeatedCount)) {
            sendBackspace(repeatedCount);
        }
    }

    private void handleSpace(boolean quickPressed, int repeatedCount) {
        InputView inputView = getCurrentInputView();
        if (inputView != null && inputView.handleKey(32, quickPressed, repeatedCount)) {
            if (inputView.isAutoReturnToEditorDefaultLayerEnabled()) {
                KeyboardSwitcher switcher = inputView.getKeyboardSwitcher();
                if (switcher.getDefaultLayer() == KeyboardEx.KeyboardLayerType.KEYBOARD_TEXT) {
                    switcher.returnToDefaultLayer();
                    return;
                }
                return;
            }
            return;
        }
        sendSpace();
    }

    private void handleLeftRightKey(int primaryCode, int repeatedCount) {
        InputView inputView = getCurrentInputView();
        if (inputView == null || !inputView.handleKey(primaryCode, false, 0)) {
            sendLeftRightKey(primaryCode, repeatedCount);
        }
    }

    @Override // com.nuance.swype.input.KeyboardViewEx.OnKeyboardActionListener
    public void onText(CharSequence text, long eventTime) {
        if (this.inUse && !getCurrentInputView().shouldDisableInput(KeyboardEx.KEYCODE_INVALID)) {
            if (this.recaptureHandler != null) {
                this.recaptureHandler.onText(text);
            }
            getCurrentInputView().onText(text, eventTime);
        }
    }

    public boolean performAction() {
        int idAction = this.mInputFieldInfo.getActionId();
        if (idAction == 1) {
            return false;
        }
        InputConnection ic = getCurrentInputConnection();
        if (ic == null) {
            return true;
        }
        ic.performEditorAction(idAction);
        return true;
    }

    public void sendCharOrPerformAction(char charCode) {
        if ('\n' != charCode || !performAction()) {
            sendChar(charCode);
        }
    }

    protected boolean shouldSendCharAsKeyEvent() {
        return this.mInputFieldInfo == null || this.mInputFieldInfo.isInputTypeNull() || !this.mInputFieldInfo.targetsAtLeast(16);
    }

    protected boolean shouldSendCharAsKeyEvent(char charCode) {
        if ('\n' == charCode) {
            return getAppSpecificBehavior().shouldSendReturnAsKeyEvent() || shouldSendCharAsKeyEvent();
        }
        return shouldSendCharAsKeyEvent();
    }

    protected boolean shouldSendKeyAsKeyEvent(int keyCode) {
        return shouldSendCharAsKeyEvent();
    }

    public void sendChar(char charCode) {
        InputConnection ic = getCurrentInputConnection();
        if (ic != null) {
            if (shouldSendCharAsKeyEvent(charCode)) {
                sendKeyChar(charCode);
            } else {
                String text = new String(new int[]{charCode}, 0, 1);
                ic.commitText(text, text.length());
            }
        }
    }

    public void sendSpace() {
        sendChar(' ');
    }

    protected void sendKeyUpDownEvents(int keyCode, int count) {
        while (true) {
            int count2 = count;
            count = count2 - 1;
            if (count2 > 0) {
                sendDownUpKeyEvents(keyCode);
            } else {
                return;
            }
        }
    }

    public void sendBackspace(int repeatedCount) {
        AppSpecificInputConnection ic = getAppSpecificInputConnection();
        if (ic == null) {
            return;
        }
        int deleteCount = repeatedCount > 20 ? 2 : 1;
        if (!getAppSpecificBehavior().forceTypeNullForBackspace() && (getAppSpecificBehavior().ignoreTypeNullCheckForBackspace() || !this.mInputFieldInfo.isInputTypeNull())) {
            ExtractedText eText = ic.getExtractedText(new ExtractedTextRequest(), 0);
            if (eText == null || eText.text == null || eText.text.length() <= 0 || (eText.selectionStart == 0 && eText.selectionEnd == 0)) {
                sendKeyUpDownEvents(67, deleteCount);
                return;
            }
            int[] selection = InputConnectionUtils.getSelection(eText);
            if (selection != null && selection[0] != selection[1]) {
                ic.beginBatchEdit();
                ic.commitText("", 0);
                ic.endBatchEdit();
                return;
            }
            if (isAccessibilitySupportEnabled()) {
                getCurrentInputView().playSoundIfTextIsEmpty();
            }
            if (!shouldSendKeyAsKeyEvent(67)) {
                if (getAppSpecificBehavior().shouldCheckSmileyWhenDeleting()) {
                    CharacterUtilities utility = CharacterUtilities.from(this);
                    int start = selection[0] > utility.maxSmileyLength ? selection[0] - utility.maxSmileyLength : 0;
                    String lastEmiley = eText.text.subSequence(start, selection[0]).toString();
                    if (utility.isSmiley(lastEmiley)) {
                        sendKeyUpDownEvents(67, deleteCount);
                        return;
                    }
                }
                ic.deleteSurroundingText(EmojiUtils.characterBefore(eText.text, selection[0], deleteCount), 0);
                CharSequence lastChar = ic.getTextBeforeCursor(1, 0);
                if (TextUtils.isEmpty(lastChar) || !Character.isHighSurrogate(lastChar.charAt(0))) {
                    return;
                }
                ic.deleteSurroundingText(1, 0);
                return;
            }
        }
        sendKeyUpDownEvents(67, deleteCount);
    }

    public void sendLeftRightKey(int primaryCode, int repeatedCount) {
        int keyEventCode;
        if (primaryCode == 4061) {
            keyEventCode = 21;
        } else if (primaryCode == 4062) {
            keyEventCode = 22;
        } else {
            keyEventCode = primaryCode;
        }
        sendDownUpKeyEvents(keyEventCode);
        if (repeatedCount > 20) {
            sendDownUpKeyEvents(67);
        }
    }

    @Override // com.nuance.swype.input.KeyboardViewEx.OnKeyboardActionListener
    public void swipeRight() {
    }

    @Override // com.nuance.swype.input.KeyboardViewEx.OnKeyboardActionListener
    public void swipeLeft() {
    }

    @Override // com.nuance.swype.input.KeyboardViewEx.OnKeyboardActionListener
    public void swipeDown() {
        close();
    }

    @Override // com.nuance.swype.input.KeyboardViewEx.OnKeyboardActionListener
    public void swipeUp() {
    }

    @Override // com.nuance.swype.input.KeyboardViewEx.OnKeyboardActionListener
    public void onDoublePress(int PrimaryCode) {
        vibrate();
    }

    @Override // com.nuance.swype.input.KeyboardViewEx.OnKeyboardActionListener
    public void onPress(int primaryCode) {
        if (primaryCode != 0) {
            vibrate();
            playKeyClick(primaryCode);
            this.mRepeatedKeyCount = 0;
        }
    }

    public void releaseRepeatKey() {
        this.mRepeatedKeyCount = 0;
    }

    @Override // com.nuance.swype.input.KeyboardViewEx.OnKeyboardActionListener
    public void onRelease(int primaryCode) {
        if (this.recaptureHandler != null) {
            this.recaptureHandler.onRelease(primaryCode, this.mRepeatedKeyCount);
        }
    }

    @Override // com.nuance.swype.input.KeyboardViewEx.OnKeyboardActionListener
    public void onMultitapTimeout() {
        getCurrentInputView().onMultitapTimeout();
    }

    @Override // com.nuance.swype.input.KeyboardViewEx.OnKeyboardActionListener
    public void onTrace(KeyboardViewEx.TracePoints trace2) {
        if (this.inUse && isValidBuild()) {
            getCurrentInputView().handleTrace(trace2);
        }
    }

    public void registerReceiverMessages() {
        registerReceiver(this.mReceiver, new IntentFilter("android.intent.action.ACTION_SHUTDOWN"));
        registerReceiver(this.mReceiver, new IntentFilter("android.intent.action.SCREEN_OFF"));
        registerReceiver(this.mReceiver, new IntentFilter("android.intent.action.SCREEN_ON"));
        registerReceiver(this.mReceiver, new IntentFilter("android.intent.action.CLOSE_SYSTEM_DIALOGS"));
        registerReceiver(this.mReceiver, new IntentFilter(SDKDownloadManager.LANGUAGE_UPDATE_NOTIFICATION));
        registerReceiver(this.mReceiver, new IntentFilter("android.intent.action.USER_UNLOCKED"));
    }

    public void playKeyClick(int primaryCode) {
        if (this.audioManager == null) {
            this.audioManager = (AudioManager) getSystemService("audio");
        }
        if (isKeySoundAllowed()) {
            int sound = 5;
            switch (primaryCode) {
                case 8:
                    sound = 7;
                    break;
                case 10:
                    sound = 8;
                    break;
                case 32:
                    sound = 6;
                    break;
            }
            this.audioManager.playSoundEffect(sound, FX_VOLUME);
        }
    }

    public void vibrate() {
        if (isVibrateAllowed()) {
            InputView inputView = getCurrentInputView();
            if (!AppPreferences.from(this).isSetVibrationDurationAllowed()) {
                if (inputView != null && this.vibrateOn) {
                    inputView.performHapticFeedback(3, 3);
                    return;
                }
                return;
            }
            if (inputView != null) {
                inputView.setHapticFeedbackEnabled(false);
            }
            Vibrator vibrator = IMEApplication.from(this).getSystemState().getVibrator();
            if (vibrator != null) {
                vibrator.vibrate(UserPreferences.from(this).getVibrationDuration());
            }
        }
    }

    void closeDialogs() {
        IMEApplication.from(this).getToolTips().dimissTip();
        this.mHandler.removeMessages(109);
        if (this.mShowFirstTimeStartupMessages != null) {
            this.mShowFirstTimeStartupMessages.dismiss();
            this.mShowFirstTimeStartupMessages = null;
        }
        if (this.mAlertMessageDialog != null && this.mAlertMessageDialog.isShowing()) {
            this.mAlertMessageDialog.dismiss();
            this.mAlertMessageDialog = null;
        }
        if (this.mOptionsDialog != null && this.mOptionsDialog.isShowing()) {
            this.mOptionsDialog.dismiss();
            this.mOptionsDialog = null;
        }
        if (this.popupLanguageList != null && this.popupLanguageList.isShowing()) {
            this.popupLanguageList.dismiss();
        }
        InputView inputView = getCurrentInputView();
        if (inputView != null) {
            inputView.closeDialogs();
        }
        closeNetworkDialog();
    }

    void cleanupAccessibility() {
        InputView inputView;
        if (isAccessibilitySupportEnabled() && (inputView = getCurrentInputView()) != null) {
            inputView.cleanupAccessibility();
        }
    }

    void pauseSpeech() {
        SpeechWrapper spw = IMEApplication.from(this).getSpeechWrapper();
        if (spw != null) {
            spw.pauseSpeech();
        }
        closeDictationLanguageMenu();
    }

    void stopSpeech() {
        SpeechWrapper spw = IMEApplication.from(this).getSpeechWrapper();
        if (spw != null) {
            spw.stopSpeech();
        }
        closeDictationLanguageMenu();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void closeDictationLanguageMenu() {
        if (this.mOptionsDialog != null && this.mOptionsDialog.isShowing() && this.mOptionsDialog.getListView() != null && (this.mOptionsDialog.getListView().getAdapter() instanceof DragonDictationLanguageListAdapter)) {
            this.mOptionsDialog.dismiss();
            this.mOptionsDialog = null;
        }
    }

    void closeNetworkDialog() {
        if (getResources().getBoolean(R.bool.enable_china_connect_special) && this.mNetworkPromptMessage != null && this.mNetworkPromptMessage.isShowing()) {
            this.mNetworkPromptMessage.dismiss();
            ((ChinaNetworkNotificationDialog) this.mNetworkPromptMessage).resetStatics();
            this.mNetworkPromptMessage = null;
        }
    }

    private void connectLearnContextBuffer(final String contextBuffer) {
        this.mHandler.postDelayed(new Runnable() { // from class: com.nuance.swype.input.IME.9
            @Override // java.lang.Runnable
            public void run() {
                boolean enterKeySelected = false;
                if (IME.this.mEditState != null && (IME.this.mEditState instanceof StatisticsEnabledEditState)) {
                    enterKeySelected = ((StatisticsEnabledEditState) IME.this.mEditState).isEnterSent();
                }
                Connect.from(IME.this).learnContextBuffer(contextBuffer, enterKeySelected);
            }
        }, 5L);
    }

    void handleClose() {
        log.d("handleClose");
        closeDialogs();
        this.mHandler.removeCallbacks(this.closeRunnable);
        this.mHandler.postDelayed(this.closeRunnable, 1000L);
    }

    public void show() {
        InputMethodManager imm = (InputMethodManager) getSystemService("input_method");
        if (imm != null && getToken() != null) {
            imm.showSoftInputFromInputMethod(getToken(), 0);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void close() {
        requestHideSelf(0);
    }

    void launchSettings() {
        if (!ActivityManagerCompat.isUserAMonkey()) {
            IMEApplication imeApp = IMEApplication.from(this);
            UsageData.recordScreenVisited(UsageData.Screen.SWYPE_KEY);
            imeApp.showMainSettings();
        }
    }

    public void closeAndLaunchSettings() {
        close();
        launchSettings();
    }

    public void loadSettings() {
        UserPreferences settings = UserPreferences.from(this);
        this.vibrateOn = settings.isVibrateOn();
        this.keySoundOn = settings.isKeySoundOn();
        if (AdsUtil.sAdsSupported) {
            IMEApplication.from(this).getAdSessionTracker().setKeyboardHeight(settings.getKeyboardScalePortrait());
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void showDictationLanguageMenu() {
        final SpeechWrapper spw;
        if ((this.mOptionsDialog == null || !this.mOptionsDialog.isShowing()) && (spw = IMEApplication.from(this).getSpeechWrapper()) != null && spw.isAllowedShowingLanguageMenu()) {
            Context ctx = getApplicationContext();
            AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
            this.dictationLanguageAdapter = new DragonDictationLanguageListAdapter(ctx);
            builder.setCancelable(true);
            builder.setOnKeyListener(new DialogInterface.OnKeyListener() { // from class: com.nuance.swype.input.IME.10
                @Override // android.content.DialogInterface.OnKeyListener
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == 5 && event.getAction() == 1) {
                        dialog.dismiss();
                        return false;
                    }
                    if (keyCode == 4) {
                        dialog.dismiss();
                        spw.restartDictation();
                        return false;
                    }
                    return false;
                }
            });
            builder.setIcon(R.drawable.swype_logo);
            builder.setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() { // from class: com.nuance.swype.input.IME.11
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface di, int position) {
                    spw.restartDictation();
                }
            });
            builder.setAdapter(this.dictationLanguageAdapter, new DialogInterface.OnClickListener() { // from class: com.nuance.swype.input.IME.12
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface di, int position) {
                    String langName = (String) IME.this.dictationLanguageAdapter.getItem(position);
                    spw.onChangeLanguage(IME.this.getApplicationContext(), langName);
                }
            });
            builder.setTitle(getString(R.string.voice_lang_title));
            this.mOptionsDialog = builder.create();
            attachDialog(this.mOptionsDialog);
            final ListView listView = this.mOptionsDialog.getListView();
            listView.post(new Runnable() { // from class: com.nuance.swype.input.IME.13
                @Override // java.lang.Runnable
                public void run() {
                    listView.setSelection(IME.this.dictationLanguageAdapter.getCheckItem());
                }
            });
            this.mOptionsDialog.show();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void hideDictationLanguageMenu() {
        if (this.mOptionsDialog != null && this.mOptionsDialog.isShowing() && (this.mOptionsDialog.getListView().getAdapter() instanceof DragonDictationLanguageListAdapter)) {
            this.mOptionsDialog.dismiss();
            this.mOptionsDialog = null;
        }
    }

    public void switchLanguageAsync(LangSwitchAction info, int msAfter) {
        this.mHandler.removeMessages(111);
        this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(111, info), msAfter);
    }

    public void switchLanguageAsync(LangSwitchAction info) {
        this.mKeyboardBackgroundManager.setReloadRequiredFromResources(true);
        switchLanguageAsync(info, 10);
    }

    public void switchInputViewAsync(int msAfter) {
        this.mHandler.removeMessages(100);
        this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(100), msAfter);
    }

    public void switchInputViewAsync() {
        switchInputViewAsync(10);
    }

    public void attachDialog(Dialog dialog) {
        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.token = this.keyboardInputInflater.getInputView(this.mCurrentInputViewName).getWindowToken();
        lp.type = 1003;
        window.setAttributes(lp);
        window.addFlags(HardKeyboardManager.META_META_LEFT_ON);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showFirstTimeMessages() {
        if (UserManagerCompat.isUserUnlocked(this)) {
            if (!this.upgrade && !isLicenseValid()) {
                if (getCurrentInputView() != null && getCurrentInputView().getWindowToken() != null && !this.isStartupActivityShown) {
                    if (this.mThirdPartyLicense != null) {
                        showThirdPartyNonLicenseAppDialog();
                        return;
                    } else {
                        showNonLicenseAppDialog();
                        return;
                    }
                }
                return;
            }
            if (this.mThirdPartyLicense != null && !this.mThirdPartyLicense.isValid() && getCurrentInputView() != null && getCurrentInputView().getWindowToken() != null && !this.isStartupActivityShown) {
                showThirdPartyNonLicenseAppDialog();
            }
            if (this.mShowFirstTimeStartupMessages == null) {
                this.mShowFirstTimeStartupMessages = IMEApplication.from(this).createFirstTimeStartupMessages();
            }
            if (this.trialCheckCount >= 5) {
                this.trialCheckCount = 0;
                showTrialExpireMessage(this.keyboardInputInflater.getInputView(this.mCurrentInputViewName).getWindowToken());
            }
            if (isTrialBuildFirstMessage()) {
                this.mShowFirstTimeStartupMessages.onInstallMessage(this.keyboardInputInflater.getInputView(this.mCurrentInputViewName).getWindowToken());
            }
            if (getResources().getBoolean(R.bool.enable_china_connect_special) && !this.isStartupActivityShown && this.mInputFieldInfo != null && !this.mInputFieldInfo.isPasswordField() && this.mCurrentInputLanguage != null && UserPreferences.from(this).getShowNetworkDialogFromKeyboard() && !ActivityManagerCompat.isUserAMonkey() && !IMEApplication.from(getApplicationContext()).getStartupSequenceInfo().isInputFieldStartupOrPassword(getCurrentInputEditorInfo(), this.mInputFieldInfo) && (this.mNetworkPromptMessage == null || !((ChinaNetworkNotificationDialog) this.mNetworkPromptMessage).hasBeenShown())) {
                this.mNetworkPromptMessage = ChinaNetworkNotificationDialog.create(this, null);
                ((ChinaNetworkNotificationDialog) this.mNetworkPromptMessage).setShowFromKeyboard(true);
                attachDialog(this.mNetworkPromptMessage);
                this.mNetworkPromptMessage.show();
            }
            if (isTrialBuildFirstMessage()) {
                this.appPrefs.setOnInstallFirstMessage(false);
            }
        }
    }

    public void toggleHwrAndKeyboardInputMode() {
        InputMethods.InputMode currentInputMode = this.mCurrentInputLanguage.getCurrentInputMode();
        InputMethods.InputMode nextInputMode = this.mCurrentInputLanguage.toggleHandwritingAndInputMode();
        if (nextInputMode != null && !nextInputMode.equals(currentInputMode)) {
            this.mKeyboardBackgroundManager.setReloadRequiredFromResources(true);
            this.keyboardInputInflater.getInputView(this.mCurrentInputViewName).finishInput();
            switchInputViewAsync(50);
        }
    }

    public void removeAllPendingMsgs() {
        if (this.recaptureHandler != null) {
            this.recaptureHandler.clearMessages();
        }
        for (int msg = 100; msg <= 125; msg++) {
            this.mHandler.removeMessages(msg);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    @TargetApi(11)
    public int getIntentFlags() {
        return 268468224;
    }

    public void showEmojiInputView() {
        IMEApplication.from(this).getEmojiInputViewController().show();
    }

    public Handler getHandler() {
        return this.mHandler;
    }

    @Override // android.view.View.OnFocusChangeListener
    public void onFocusChange(View v, boolean hasFocus) {
    }

    public Dialog getSoftInputWindow() {
        return getWindow();
    }

    @Override // com.nuance.swype.input.KeyboardViewEx.OnKeyboardActionListener
    public void onWrite(List<Point> write) {
        InputView inputView;
        if (this.inUse && isValidBuild() && (inputView = getCurrentInputView()) != null && inputView.isWriteInputEnabled()) {
            inputView.handleWrite(write);
        }
    }

    public void showCapLockState(boolean isCapslock) {
        if (getResources() != null) {
            if (isCapslock) {
                InputMethodToast.show(this, getResources().getString(R.string.caps_lock_on), 0);
            } else {
                InputMethodToast.show(this, getResources().getString(R.string.caps_lock_off), 0);
            }
        }
    }

    public void checkBuildValid() {
        this.mValidBulid = !IMEApplication.from(this).isTrialExpired() && isLicenseValid();
    }

    public boolean isValidBuild() {
        return this.mValidBulid;
    }

    public boolean isLicenseValid() {
        if (!Connect.from(this).isLicensed()) {
            return false;
        }
        if (this.currentLicense != null && (!this.currentLicense.isValid(this) || this.currentLicense.isDisabled())) {
            return false;
        }
        if (this.mThirdPartyLicense != null && !this.mThirdPartyLicense.isValid()) {
            return false;
        }
        AppPreferences.from(this).setThirdPartyLicenseMessageTimes(0);
        return true;
    }

    private void showNonLicenseAppDialog() {
        if (this.mAlertMessageDialog != null && this.mAlertMessageDialog.isShowing()) {
            this.mAlertMessageDialog.dismiss();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        builder.setCancelable(true).setIcon(R.drawable.swype_logo).setTitle(getResources().getString(R.string.notification_default_title)).setNegativeButton(android.R.string.ok, (DialogInterface.OnClickListener) null);
        this.mAlertMessageDialog = builder.create();
        this.mAlertMessageDialog.setMessage(getResources().getString(R.string.unlicensed_dialog_title));
        Window window = this.mAlertMessageDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.token = this.keyboardInputInflater.getInputView(this.mCurrentInputViewName).getWindowToken();
            lp.type = 1003;
            window.setAttributes(lp);
            window.addFlags(HardKeyboardManager.META_META_LEFT_ON);
        }
        this.mAlertMessageDialog.show();
    }

    private void showThirdPartyNonLicenseAppDialog() {
        Intent thirdPartyIntent;
        if (this.mThirdPartyLicense != null && (thirdPartyIntent = this.mThirdPartyLicense.getActivityIntentForInvalidLicense()) != null) {
            int showTimes = AppPreferences.from(this).getThirdPartyLicenseMessageTimes() + 1;
            AppPreferences.from(this).setThirdPartyLicenseMessageTimes(showTimes);
            if (showTimes == 1 || showTimes == 2 || showTimes == 4 || showTimes == 8) {
                log.d("ThirdPartyLicense", String.format("The third party license checking failed %d times", Integer.valueOf(showTimes)));
                Bundle data = thirdPartyIntent.getExtras();
                String appTitle = data.getString("app_title");
                String appMessage = data.getString("app_message");
                if (this.mAlertMessageDialog != null && this.mAlertMessageDialog.isShowing()) {
                    this.mAlertMessageDialog.dismiss();
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                builder.setCancelable(true).setIcon(R.drawable.swype_logo).setTitle(appTitle).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() { // from class: com.nuance.swype.input.IME.17
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        IME.log.d("ThirdPartyLicense", "User canceled the invalid third party license dialog");
                    }
                }).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() { // from class: com.nuance.swype.input.IME.16
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent;
                        dialog.dismiss();
                        IME.log.d("ThirdPartyLicense", "User selected OK in the invalid third party license dialog");
                        if (IME.this.mThirdPartyLicense != null && (intent = IME.this.mThirdPartyLicense.getActivityIntentForInvalidLicense()) != null) {
                            IME.log.d("ThirdPartyLicense", "User selected OK and show the page of third party");
                            intent.addFlags(268435456);
                            try {
                                IME.this.startActivity(intent);
                            } catch (Throwable th) {
                                IME.log.d("ThirdPartyLicense", "not found the page of third party");
                            }
                        }
                    }
                }).setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: com.nuance.swype.input.IME.15
                    @Override // android.content.DialogInterface.OnCancelListener
                    public void onCancel(DialogInterface dialog) {
                        dialog.dismiss();
                        IME.log.d("ThirdPartyLicense", "User canceled the invalid third party license dialog");
                    }
                });
                this.mAlertMessageDialog = builder.create();
                this.mAlertMessageDialog.setMessage(appMessage);
                Window window = this.mAlertMessageDialog.getWindow();
                if (window != null) {
                    WindowManager.LayoutParams lp = window.getAttributes();
                    lp.token = this.keyboardInputInflater.getInputView(this.mCurrentInputViewName).getWindowToken();
                    lp.type = 1003;
                    window.setAttributes(lp);
                    window.addFlags(HardKeyboardManager.META_META_LEFT_ON);
                }
                this.mAlertMessageDialog.show();
            }
        }
    }

    public void runLicenseCheck() {
        if (!ActivityManagerCompat.isUserAMonkey() && this.currentLicense == null) {
            this.currentLicense = License.getLicense(this);
        }
    }

    public boolean replaceLicense(File newLicense) {
        File oldFile = new File(getFilesDir(), License.LICENSE_FILE);
        if (!newLicense.renameTo(oldFile)) {
            return false;
        }
        this.currentLicense = null;
        runLicenseCheck();
        return true;
    }

    public void renewLicense() {
        this.currentLicense = null;
        runLicenseCheck();
    }

    public AppSpecificBehavior getAppSpecificBehavior() {
        return IMEApplication.from(this).getAppSpecificBehavior();
    }

    public void deleteWord(AppSpecificInputConnection ic) {
        deleteWord(ic, Integer.MAX_VALUE);
    }

    public void deleteWord(AppSpecificInputConnection ic, int maxWordLen) {
        ExtractedText eText;
        if (ic != null && (eText = ic.getExtractedText(new ExtractedTextRequest(), 0)) != null && !TextUtils.isEmpty(eText.text)) {
            CharacterUtilities charUtils = CharacterUtilities.from(this);
            int searchStart = Math.min(eText.selectionStart, eText.selectionEnd);
            char ch = eText.text.charAt(searchStart - 1);
            if (CharacterUtilities.isWhiteSpace(ch) || charUtils.isTerminalPunctuation(ch)) {
                searchStart--;
            }
            int wordStart = searchStart;
            while (wordStart > 0) {
                maxWordLen--;
                if (maxWordLen < 0) {
                    break;
                }
                char ch2 = eText.text.charAt(wordStart - 1);
                if (CharacterUtilities.isWhiteSpace(ch2) || charUtils.isTerminalPunctuation(ch2)) {
                    break;
                } else {
                    wordStart--;
                }
            }
            UsageManager usageMgr = UsageManager.from(this);
            if (usageMgr != null) {
                usageMgr.getKeyboardUsageScribe().recordDeletedWord(eText.text.subSequence(wordStart, eText.text.length() - 1).toString());
            }
            ic.deleteSurroundingText(eText.selectionStart - wordStart, 0);
        }
    }

    public boolean startApp(String packageName, Uri uri) {
        return startApp(packageName, uri, 268435456, null);
    }

    private boolean startApp(String packageName, Uri uri, int flags, IntentDecorator decorator) {
        Intent intent;
        List<ResolveInfo> list;
        if (packageName == null) {
            return false;
        }
        if (uri == null) {
            intent = new Intent("android.intent.action.MAIN");
            intent.addCategory("android.intent.category.LAUNCHER");
            list = getPackageManager().queryIntentActivities(intent, 0);
        } else {
            intent = new Intent("android.intent.action.VIEW", uri);
            list = getPackageManager().queryIntentActivities(intent, 65536);
        }
        for (ResolveInfo info : list) {
            if (packageName.equals(info.activityInfo.packageName)) {
                intent.setClassName(packageName, info.activityInfo.name);
                intent.setFlags(flags);
                if (decorator != null) {
                    decorator.decorate(intent);
                }
                try {
                    startActivity(intent);
                    hideWindow();
                } catch (Exception ex) {
                    log.e(packageName, ex);
                }
                return true;
            }
        }
        return false;
    }

    public void showStartupMessage() {
        if (!ActivityManagerCompat.isUserAMonkey()) {
            this.mHandler.removeMessages(109);
            this.mHandler.sendEmptyMessageDelayed(109, 15L);
        }
    }

    private boolean showStartupActivity() {
        if (ActivityManagerCompat.isUserAMonkey() || !UserManagerCompat.isUserUnlocked(this) || this.mShowFirstTimeStartupMessages != null) {
            return false;
        }
        this.mShowFirstTimeStartupMessages = IMEApplication.from(this).createFirstTimeStartupMessages();
        if (!this.mShowFirstTimeStartupMessages.canShow(getCurrentInputEditorInfo())) {
            return false;
        }
        boolean needToClearPreviousTask = false;
        if (!getPackageName().equalsIgnoreCase(getCurrentInputEditorInfo().packageName)) {
            needToClearPreviousTask = true;
        }
        return this.mShowFirstTimeStartupMessages.showStartup(false, needToClearPreviousTask, getCurrentInputEditorInfo(), this.mInputFieldInfo);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class KeyboardState {
        private KeyboardEx.KeyboardLayerType prevKeyboardLayer = KeyboardEx.KeyboardLayerType.KEYBOARD_INVALID;
        private Shift.ShiftState prevShiftState;

        KeyboardState() {
        }

        void save(InputView inputView) {
            if (inputView != null) {
                IME.log.d("cursor KeyboardState save");
                inputView.flushCurrentActiveWord();
                this.prevKeyboardLayer = inputView.getKeyboardLayer();
                this.prevShiftState = inputView.getShiftState();
            }
        }

        void restore(InputView inputView) {
            restore(inputView, false);
        }

        void restore(InputView inputView, boolean isOrChange) {
            if ((inputView == null || inputView.getCurrentInputLanguage() == null || !inputView.getCurrentInputLanguage().isChineseLanguage()) && inputView != null && this.prevKeyboardLayer != KeyboardEx.KeyboardLayerType.KEYBOARD_INVALID) {
                inputView.setKeyboardLayer(this.prevKeyboardLayer);
                if (this.prevShiftState == Shift.ShiftState.LOCKED || this.prevKeyboardLayer != KeyboardEx.KeyboardLayerType.KEYBOARD_TEXT || isOrChange) {
                    inputView.setShiftState(this.prevShiftState);
                }
            }
        }

        void clear() {
            this.prevKeyboardLayer = KeyboardEx.KeyboardLayerType.KEYBOARD_INVALID;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void toggleToAndroidKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService("input_method");
        try {
            imm.setInputMethod(this.myInputMethodImpl.myToken, ANDROID_DEFAULT_IME_ID);
        } catch (IllegalArgumentException ex) {
            log.e(ex.getMessage());
        }
    }

    public boolean isKeySoundAllowed() {
        return IMEApplication.from(this).getSystemState().isKeySoundEnabled() && this.keySoundOn;
    }

    public boolean isVibrateAllowed() {
        return IMEApplication.from(this).getSystemState().isVibrateEnabled();
    }

    public boolean isImeInUse() {
        return this.inUse;
    }

    public void setImeInUse(boolean use) {
        if (!this.inUse && use) {
            this.trialCheckCount++;
        }
        this.inUse = use;
        if (!this.inUse) {
            this.allowCandidateViewShown = false;
        }
        IMEApplication.from(this).onImeInUse(this.inUse);
    }

    private void startPendingScanning() {
        NewWordsBucketFactory.NewWordsBucket[] buckets = IMEApplication.from(this).getNewWordsBucketFactory().getNewWordBuckets();
        for (NewWordsBucketFactory.NewWordsBucket newWordsBucket : buckets) {
            startDelayScanning(newWordsBucket);
        }
        UserPreferences userPrefs = UserPreferences.from(this);
        int leftNum = userPrefs.getInt(UserPreferences.MLS_HOT_WORDS_LEFT_NUM, -1);
        if (ThemeManager.isDownloadableThemesEnabled() && userPrefs.getMlsHotWordsImported() && !userPrefs.isImportedMlsHotWordsOver() && leftNum > 0) {
            IMEApplication imeApp = IMEApplication.from(this);
            if (IMEApplication.from(this).getNewWordsBucketFactory().getMlsThemeWordsBucketInstance().isEmpty()) {
                imeApp.getThemeManager().importMls(this, leftNum);
            }
        }
    }

    public boolean hasNewWordsScanning() {
        NewWordsBucketFactory.NewWordsBucket[] buckets = IMEApplication.from(this).getNewWordsBucketFactory().getNewWordBuckets();
        for (NewWordsBucketFactory.NewWordsBucket newWordsBucket : buckets) {
            if (!newWordsBucket.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public void startDelayScanning(NewWordsBucketFactory.NewWordsBucket bucket) {
        StartupSequenceInfo ssInfo = IMEApplication.from(this).getStartupSequenceInfo();
        if (!BuildInfo.from(this).isDTCbuild() || !ssInfo.shouldShowStartup(getCurrentInputEditorInfo(), this.mInputFieldInfo)) {
            log.d("startDelayScanning...");
            if (IMEApplication.from(this).isUserUnlockFinished() && !bucket.isEmpty()) {
                sendDelayNewWordsScanning(bucket, INITIAL_DELAY_IN_MILLIS);
            }
        }
    }

    public void sendDelayNewWordsScanning(NewWordsBucketFactory.NewWordsBucket bucket, long delay) {
        if (this.mHandler.hasMessages(113)) {
            this.mHandler.removeMessages(113);
        }
        this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(113, bucket), delay);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleNewWordsDelayScanning(NewWordsBucketFactory.NewWordsBucket bucket) {
        log.d("handleNewWordsDelayScanning()");
        if (!isImeInUse()) {
            if (!bucket.isEmpty()) {
                if (this.initializeCoreNeeded) {
                    initializedCore();
                    this.initializeCoreNeeded = false;
                }
                if (this.alphaInput != null) {
                    scan(bucket, getMaxItemToScan(bucket));
                    if (!bucket.isEmpty()) {
                        sendDelayNewWordsScanning(bucket, NEXT_SCAN_IN_MILLIS);
                        return;
                    }
                    return;
                }
                return;
            }
            this.initializeCoreNeeded = true;
            sendDelayNewWordsScanning(bucket, RETRY_DELAY_IN_MILLIS);
        }
    }

    private void scan(NewWordsBucketFactory.NewWordsBucket bucket, int maxItemToScan) {
        log.d("scan() itemsToScan = ", Integer.valueOf(maxItemToScan));
        int itemScanned = 0;
        bucket.setScanContext(this.alphaInput);
        while (itemScanned < maxItemToScan) {
            String words = bucket.remove();
            if (words == null) {
                break;
            }
            itemScanned++;
            if (bucket.isBigramDlm() && words.contains(",") && !bucket.sentenceBasedLearning) {
                String[] names = words.split(",");
                if (names != null && !TextUtils.isEmpty(names[0]) && !TextUtils.isEmpty(names[1])) {
                    this.alphaInput.dlmImplicitScanBuf(names[0], bucket.isHighQualityWord, bucket.sentenceBasedLearning, false, names[1]);
                }
                if (bucket.isEmpty()) {
                    log.d("scan mls over");
                    UserPreferences.from(this).setMlsHotWordsImportedOver(true);
                }
            } else {
                this.alphaInput.dlmImplicitScanBuf(words, bucket.isHighQualityWord, bucket.sentenceBasedLearning, false, null);
            }
            log.d("scan() words = ", words);
            ThemeWordListManager.getInstance(getApplicationContext()).updateStateForBucket(bucket);
        }
        log.d("scan() itemScanned = ", Integer.valueOf(itemScanned));
    }

    private int getMaxItemToScan(NewWordsBucketFactory.NewWordsBucket bucket) {
        return 1;
    }

    public void initializedCore() {
        log.d("initializedCore()");
        InputMethods im = getInputMethods();
        if (im != null && !im.getCurrentInputLanguage().isCJK()) {
            this.alphaInput = IMEApplication.from(this).getSwypeCoreLibMgr().getXT9CoreAlphaInputSession();
            im.getCurrentInputLanguage().setLanguage(this.alphaInput);
        } else {
            this.alphaInput = null;
        }
    }

    public int getCurrentActiveCoreId() {
        InputView view;
        XT9CoreInput coreInput;
        if (this.keyboardInputInflater == null || (view = getCurrentInputView()) == null || (coreInput = view.getXT9CoreInput()) == null) {
            return 0;
        }
        return coreInput.getInputCoreCategory();
    }

    public boolean hasCurrentActiveCore() {
        InputView view;
        if (this.keyboardInputInflater == null || (view = getCurrentInputView()) == null) {
            return false;
        }
        return view.getXT9CoreInput().hasInputContext();
    }

    private boolean isLVLEnabled() {
        return AppPreferences.from(this).getDefaultBoolean(R.bool.google_play_show_lvl_default);
    }

    public Point getExtendedPoint() {
        return this.extendedPointTracker.getExtendedPoint();
    }

    public MotionEvent getExtendedEventForView(MotionEvent event, View view) {
        return this.extendedPointTracker.getExtendedEventForView(event, view);
    }

    public AccessibilityInfo getAccessibilityInfo() {
        AppPreferences appPref;
        IMEApplication imeApplication = IMEApplication.from(this);
        if (imeApplication == null || (appPref = imeApplication.getAppPreferences()) == null) {
            return null;
        }
        return appPref.getAccessibilityInfo();
    }

    public boolean isAccessibilitySupportEnabled() {
        return (isHardKeyboardActive() || this.mAccessibilityInfo == null || !this.mAccessibilityInfo.isAccessibilitySupportEnabled()) ? false : true;
    }

    public boolean isDeviceExploreByTouchOn() {
        return (isHardKeyboardActive() || this.mAccessibilityInfo == null || !this.mAccessibilityInfo.isDeviceExploreByTouchEnabled()) ? false : true;
    }

    public void onPackageChanged(Context context, String packageName) {
        if (getAppSpecificBehavior() != null) {
            getAppSpecificBehavior().onPackageChanged(context, packageName);
        }
    }

    public void startVoiceRecognition(String language) {
        if (this.mVoiceRecognitionTrigger == null) {
            this.mVoiceRecognitionTrigger = new VoiceRecognitionTrigger(this);
        }
        VoiceRecognitionTrigger voiceRecognitionTrigger = this.mVoiceRecognitionTrigger;
        if (voiceRecognitionTrigger.mTrigger == null) {
            return;
        }
        voiceRecognitionTrigger.mTrigger.startVoiceRecognition(language);
    }

    public final void detectAccessibilityChange() {
        if (this.lastAccessibility != isAccessibilitySupportEnabled()) {
            this.isAccessibilityChanged = true;
            this.lastAccessibility = isAccessibilitySupportEnabled();
        }
    }

    public void showLanguageMenu() {
        if (this.mOptionsDialog == null || !this.mOptionsDialog.isShowing()) {
            Context ctx = getApplicationContext();
            AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
            InputMethods inputMethods = InputMethods.from(this);
            List<InputMethods.Language> languageList = inputMethods.getInputLanguages();
            this.languageAdapter = new LanguageListAdapter(ctx, languageList, inputMethods.getCurrentInputLanguage());
            builder.setCancelable(true);
            builder.setOnKeyListener(new DialogInterface.OnKeyListener() { // from class: com.nuance.swype.input.IME.18
                @Override // android.content.DialogInterface.OnKeyListener
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == 5 && event.getAction() == 1) {
                        dialog.dismiss();
                        return false;
                    }
                    return false;
                }
            });
            builder.setIcon(R.drawable.swype_logo);
            builder.setNegativeButton(R.string.cancel_button, (DialogInterface.OnClickListener) null);
            builder.setAdapter(this.languageAdapter, new DialogInterface.OnClickListener() { // from class: com.nuance.swype.input.IME.19
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface di, int position) {
                    if (!ActivityManagerCompat.isUserAMonkey()) {
                        InputMethods.Language language = (InputMethods.Language) IME.this.languageAdapter.getItem(position);
                        IME.this.keyboardInputInflater.getInputView(IME.this.mCurrentInputViewName).finishInput();
                        IME.this.mInputMethods.setCurrentLanguage(language.getLanguageId());
                        IME.this.switchInputViewAsync();
                        IME.this.mWantToast = true;
                    }
                }
            });
            builder.setTitle(getString(R.string.language_category_title));
            this.mOptionsDialog = builder.create();
            attachDialog(this.mOptionsDialog);
            final ListView listView = this.mOptionsDialog.getListView();
            listView.post(new Runnable() { // from class: com.nuance.swype.input.IME.20
                @Override // java.lang.Runnable
                public void run() {
                    listView.setSelection(IME.this.languageAdapter.getCheckItem());
                }
            });
            this.mOptionsDialog.show();
        }
    }

    public int getBottomBarHeight(InputView inputView) {
        int[] winOffset = new int[2];
        inputView.getLocationInWindow(winOffset);
        return getWindow().getWindow().getDecorView().getHeight() - (inputView.getHeight() + winOffset[1]);
    }

    public void closeLanguageList() {
        if (this.popupLanguageList != null && this.popupLanguageList.isShowing()) {
            this.popupLanguageList.dismiss();
        }
    }

    public void setRecaptureWhenSwitching(boolean recapture) {
        this.recaptureWhenSwitching = recapture;
    }

    private void doRecaptureWhenSwitching() {
        if (this.recaptureWhenSwitching) {
            if (this.recaptureHandler != null && !isHardKeyboardActive()) {
                this.recaptureHandler.onSingleTap(false, false);
            }
            this.recaptureWhenSwitching = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class LanguageUpdateNotificationRunnable implements Runnable {
        private final InputMethods.Language language;
        private boolean notificationPosted;
        private final SDKDownloadManager sdkMgr;

        private LanguageUpdateNotificationRunnable(InputMethods.Language language, SDKDownloadManager sdkMgr) {
            this.language = language;
            this.sdkMgr = sdkMgr;
        }

        @Override // java.lang.Runnable
        public final void run() {
            this.notificationPosted = true;
            this.sdkMgr.postLanguageUpdateNotification(this.language);
        }
    }

    private LanguageUpdateNotificationRunnable checkForLanguageUpdateRunnable(InputMethods.Language language, LanguageUpdateNotificationRunnable runnable) {
        log.d("checkForLanguageUpdateRunnable: ", language.mEnglishName);
        SDKDownloadManager sdkMgr = Connect.from(this).getDownloadManager();
        if (!sdkMgr.isLanguageAvailableForUpdate(language)) {
            log.d("checkForLanguageUpdateRunnable: ", language.mEnglishName, " no update available");
            if (runnable == null) {
                return null;
            }
            this.mHandler.removeCallbacks(runnable);
            return null;
        }
        if (runnable != null && !runnable.notificationPosted && language.getCoreLanguageId() == runnable.language.getCoreLanguageId()) {
            log.d("checkForLanguageUpdateRunnable: ", language.mEnglishName, " pending");
            return runnable;
        }
        if (runnable != null) {
            this.mHandler.removeCallbacks(runnable);
        }
        LanguageUpdateNotificationRunnable runnable2 = new LanguageUpdateNotificationRunnable(language, sdkMgr);
        this.mHandler.postDelayed(runnable2, TimeConversion.MILLIS_IN_MINUTE);
        return runnable2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkForLanguageUpgrade(InputMethods.Language currentLanguage) {
        log.d("checkForLanguageUpgrade: ", currentLanguage.mEnglishName);
        if (UserManagerCompat.isUserUnlocked(this)) {
            StartupSequenceInfo ssInfo = IMEApplication.from(this).getStartupSequenceInfo();
            if (!BuildInfo.from(this).isDTCbuild() || !ssInfo.shouldShowStartup(getCurrentInputEditorInfo(), this.mInputFieldInfo)) {
                if (currentLanguage instanceof BilingualLanguage) {
                    BilingualLanguage bi = (BilingualLanguage) currentLanguage;
                    int firstLangId = bi.getFirstLanguage().mCoreLanguageId;
                    int secondLangId = bi.getSecondLanguage().mCoreLanguageId;
                    if (this.checkedLanguageUpdateList.size() == 0 || this.checkedLanguageUpdateList.indexOfValue(firstLangId) < 0) {
                        this.languageUpdateNotificationRunnable1 = checkForLanguageUpdateRunnable(bi.getFirstLanguage(), this.languageUpdateNotificationRunnable1);
                    }
                    if (this.checkedLanguageUpdateList.size() == 0 || this.checkedLanguageUpdateList.indexOfValue(secondLangId) < 0) {
                        this.languageUpdateNotificationRunnable2 = checkForLanguageUpdateRunnable(bi.getSecondLanguage(), this.languageUpdateNotificationRunnable2);
                        return;
                    }
                    return;
                }
                this.languageUpdateNotificationRunnable1 = checkForLanguageUpdateRunnable(currentLanguage, this.languageUpdateNotificationRunnable1);
            }
        }
    }

    protected boolean isHardKeyboardEnabled() {
        return this.isHardkeyboardEnabled;
    }

    public boolean isHardKeyboardActive() {
        return this.isHardKeyboardAttached && this.isHardkeyboardEnabled;
    }

    public void setHardKeyboardAttached(boolean attached) {
        if (this.isHardKeyboardAttached != attached) {
            this.isHardKeyboardAttached = attached;
            if (this.inputContainerView != null) {
                this.inputContainerView.showInputArea(!isHardKeyboardActive());
                InputView inputView = getCurrentInputView();
                if (inputView != null) {
                    if (attached) {
                        inputView.refreshBTAutoCorrection();
                        log.d("BTlocalytics inputview:", inputView);
                        UsageData.recordBTKbUsage();
                    }
                    inputView.dismissPopupKeyboard();
                    dismissLangPopupMenu();
                }
            }
        }
    }

    public boolean isTalkBackOn() {
        AppPreferences appPref;
        AccessibilityInfo ai;
        IMEApplication imeApplication = IMEApplication.from(this);
        return (imeApplication == null || (appPref = imeApplication.getAppPreferences()) == null || (ai = appPref.getAccessibilityInfo()) == null || !ai.isTalkBackOn()) ? false : true;
    }

    public boolean isUsingSpellCheckerService() {
        return false;
    }

    public boolean cursorUpdateReceivedFromTap() {
        return this.tapDetector != null && this.tapDetector.tapDetected();
    }

    @Override // com.nuance.swype.input.appspecific.AppSpecificInputConnection.ExtractViewState
    public void onFlushActiveWord() {
        InputView inputView = getCurrentInputView();
        if (inputView != null) {
            log.d("cursor onClearActiveWord");
            inputView.selectDefaultCompostingText();
        }
    }

    public void dragLock(boolean lock) {
        if (this.inputContainerView != null) {
            this.inputContainerView.dragLock(lock);
        }
    }

    public final void showNonLVLLicenseAppDialog() {
    }

    /* renamed from: com.nuance.swype.input.IME$21, reason: invalid class name */
    /* loaded from: classes.dex */
    class AnonymousClass21 implements DialogInterface.OnClickListener {
        AnonymousClass21() {
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialog, int which) {
            Intent marketIntent = new Intent("android.intent.action.VIEW", Uri.parse("http://market.android.com/details?id=" + IME.this.getPackageName()));
            marketIntent.addFlags(268435456);
            IME.this.startActivity(marketIntent);
        }
    }

    /* renamed from: com.nuance.swype.input.IME$22, reason: invalid class name */
    /* loaded from: classes.dex */
    class AnonymousClass22 implements DialogInterface.OnClickListener {
        AnonymousClass22() {
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialog, int which) {
            IME.this.close();
        }
    }

    @Override // com.nuance.swype.input.accessibility.SettingsChangeListener.SystemSettingsChangeListener
    public void onExploreByTouchChanged(boolean value) {
        InputView iv = getCurrentInputView();
        if (iv != null && value) {
            iv.addCandidateListener(WordSelectionState.getInstance());
        }
        this.mAccessibilityInfo = getAccessibilityInfo();
        if (this.mAccessibilityInfo != null) {
            this.mAccessibilityInfo.setExploreByTouch(value);
        }
        refreshKeyboard();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isChangingOrientation() {
        return this.isOrientationChanged;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean cursorUpdateReceivedFromSingleTap() {
        return this.tapDetector != null && this.tapDetector.singleTapDetected();
    }

    @Override // com.nuance.swype.input.KeyboardViewEx.OnKeyboardActionListener
    public void onUpdateSpeechText(CharSequence text, boolean isStreaming, boolean isFinal) {
    }

    public void setEmojiExtraRegion(int extraRegion) {
        this.emojiExtraRegion = extraRegion;
    }

    private void onIMEUpgrading() {
        String swib = BuildInfo.from(this).getSwib();
        if (!swib.isEmpty()) {
            String currentSWIB = AppPreferences.from(this).getCurrentSWIB();
            if (currentSWIB.isEmpty()) {
                AppPreferences.from(this).setCurrentSWIB(swib);
            } else if (!currentSWIB.equalsIgnoreCase(swib)) {
                AppPreferences.from(this).setCurrentSWIB(swib);
                DatabaseConfig.removeIncompatibleDBFiles(this, null);
            }
        }
    }

    public KeyboardBackgroundManager getKeyboardBackgroundManager() {
        return this.mKeyboardBackgroundManager;
    }

    public boolean isTrialBuildFirstMessage() {
        return this.appPrefs.getOnInstallFirstMessage() && !IMEApplication.from(getApplicationContext()).getStartupSequenceInfo().isInputFieldStartupOrPassword(getCurrentInputEditorInfo(), this.mInputFieldInfo);
    }

    public void setTransliterationToggleButtonOff() {
        this.view = getCurrentInputView().createCandidatesView(this.recaptureHandler);
        this.toggleButton = (ImageButton) this.view.findViewById(R.id.toggle);
        this.toggleButton.setVisibility(4);
    }

    public void setTransliterationToggleButtonOn() {
        this.view = getCurrentInputView().createCandidatesView(this.recaptureHandler);
        this.toggleButton = (ImageButton) this.view.findViewById(R.id.toggle);
        this.toggleButton.setVisibility(0);
    }

    public void showTrialExpireMessage(IBinder binder) {
        if (this.mShowFirstTimeStartupMessages == null) {
            this.mShowFirstTimeStartupMessages = IMEApplication.from(this).createFirstTimeStartupMessages();
        }
        this.mShowFirstTimeStartupMessages.checkAndShowTrialExpired(binder);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void toggleInputMode(String inputModeName) {
        for (InputMethods.InputMode inputMode : this.mCurrentInputLanguage.getAllInputModes()) {
            if (inputMode.mInputMode.equals(inputModeName)) {
                inputMode.setCurrent();
                refreshKeyboard();
                return;
            }
        }
    }
}
