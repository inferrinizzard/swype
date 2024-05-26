package com.nuance.swype.input;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Process;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.SparseIntArray;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.WindowManager;
import com.crashlytics.android.Crashlytics;
import com.nuance.android.compat.ActivityManagerCompat;
import com.nuance.android.compat.ConfigurationCompat;
import com.nuance.android.compat.CorrectionSpanFactory;
import com.nuance.android.compat.DisplayCompat;
import com.nuance.android.compat.UserManagerCompat;
import com.nuance.android.util.ThemedResources;
import com.nuance.input.swypecorelib.SwypeCoreLibrary;
import com.nuance.input.swypecorelib.XT9CoreChineseInput;
import com.nuance.sns.ScraperStatus;
import com.nuance.speech.SpeechConfig;
import com.nuance.speech.SpeechInfo;
import com.nuance.swype.connect.Connect;
import com.nuance.swype.inapp.CatalogManager;
import com.nuance.swype.input.BuildInfo;
import com.nuance.swype.input.InputMethods;
import com.nuance.swype.input.KeyboardEx;
import com.nuance.swype.input.ThemeManager;
import com.nuance.swype.input.ads.BillboardManager;
import com.nuance.swype.input.ads.BillboardSessionTracker;
import com.nuance.swype.input.appspecific.AppSpecificBehavior;
import com.nuance.swype.input.corelibmgr.SwypeCoreLibManager;
import com.nuance.swype.input.emoji.EmojiCacheManager;
import com.nuance.swype.input.emoji.EmojiInputController;
import com.nuance.swype.input.emoji.EmojiLoader;
import com.nuance.swype.input.hardkey.HardKeyIMEHandler;
import com.nuance.swype.input.settings.InputPrefs;
import com.nuance.swype.input.settings.ShowSettings;
import com.nuance.swype.input.udb.NewWordsBucketFactory;
import com.nuance.swype.input.udb.UserDictionaryIterator;
import com.nuance.swype.input.udb.sync.AndroidUserDictionarySyncDataProvider;
import com.nuance.swype.input.udb.sync.SyncDataProviderManager;
import com.nuance.swype.input.update.UpdateStatusManager;
import com.nuance.swype.plugin.ThemeLoader;
import com.nuance.swype.startup.StartupSequenceInfo;
import com.nuance.swype.stats.ScribeFilter;
import com.nuance.swype.stats.StatisticsManager;
import com.nuance.swype.stats.UsageManager;
import com.nuance.swype.usagedata.CustomDimension;
import com.nuance.swype.usagedata.UsageData;
import com.nuance.swype.util.AdsUtil;
import com.nuance.swype.util.CharacterUtilities;
import com.nuance.swype.util.LogManager;
import com.nuance.swype.util.Observer;
import com.nuance.swype.util.StringUtils;
import com.nuance.swype.util.Subject;
import com.nuance.swype.util.ThemeUtil;
import com.nuance.swype.util.WordDecorator;
import io.fabric.sdk.android.Fabric;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.Thread;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

/* loaded from: classes.dex */
public class IMEApplication extends Application {
    static final /* synthetic */ boolean $assertionsDisabled;
    private static final String ALPHA_HANDWRITING_DATABASE_NAME = "database_le.dat";
    private static final int MAX_HEAP_MEMORY_THRESHOLD = 128;
    private static final String THEMES_FOLDER_NAME = "themes";
    protected static final LogManager.Log log;
    protected static final LogManager.Trace trace;
    private Thread.UncaughtExceptionHandler androidDefaultUEH;
    private AppContextPredictionSetter appContextSetter;
    private AppPreferences appPrefs;
    private AppSpecificBehavior appSpecificBehavior;
    private Set<String> assetFileNames;
    private BuildInfo buildInfo;
    private SparseIntArray categoryMap;
    private CharacterUtilities charUtils;
    private Connect connect;
    private CorrectionSpanFactory correctionSpanFactory;
    private String currentAppName;
    private int currentFieldInfo;
    private InputMethods.Language currentLanguage;
    private DrawBufferManager drawBufferManager;
    private EmojiCacheManager emojiCacheManager;
    private EmojiInputController emojiInputViewController;
    private GestureManager gestureManager;
    private HardKeyIMEHandler hardKeyboardHandler;
    private IME ime;
    private InputMethods inputMethods;
    private boolean isCreated;
    private boolean isPhablet;
    private boolean isTablet;
    public boolean isUserTapKey;
    public boolean isWCLMessage;
    private KeyboardManager keyboardManager;
    private ThemeManager.SwypeTheme lastTheme;
    private int lastThemeResId;
    private boolean lowEndDeviceBuild;
    private BillboardSessionTracker mAdSessionTracker;
    private BillboardManager mBillboardManager;
    private CatalogManager mCatalogManager;
    private int maxMemoryInBytes;
    private NewWordsBucketFactory newWordsBucketFactory;
    private boolean phabletVerified;
    private PlatformUtil platformUtil;
    private Set<String> privateFiles;
    private ScraperStatus.ScraperStatusFactory scraperStatusFactory;
    private ScribeFilter scribeFilter;
    private ShowSettings settingsLauncher;
    SpeechConfig speechConfig;
    private SpeechWrapper speechWrapper;
    private StartupSequenceInfo startupSequenceInfo;
    private StatisticsManager statsManager;
    private SwypeCoreLibManager swypeCoreLibMgr;
    private SyncDataProviderManager syncDataProviderManager;
    protected SystemState sysState;
    private boolean tabletVerified;
    private ThemeLoader themeLoader;
    private ThemeManager themeManager;
    private ToolTips toolTips;
    private UpdateStatusManager updateStatusManager;
    private UsageManager usageManager;
    private UserPreferences userPrefs;
    private Subject statsContext = new Subject();
    private Subject imeSubject = new Subject();
    private boolean hasMicrophone = false;
    private boolean isUserUnlockFinished = true;

    static {
        $assertionsDisabled = !IMEApplication.class.desiredAssertionStatus();
        log = LogManager.getLog("IMEApplication");
        trace = LogManager.getTrace();
    }

    public static IMEApplication from(Context context) {
        return (IMEApplication) context.getApplicationContext();
    }

    @Override // android.app.Application
    public void onCreate() {
        super.onCreate();
        UsageData.init(getApplicationContext());
        EmojiLoader.init(getApplicationContext());
        if (getBuildInfo().isDTCbuild()) {
            Fabric.with(this, new Crashlytics());
        }
        this.androidDefaultUEH = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() { // from class: com.nuance.swype.input.IMEApplication.1
            @Override // java.lang.Thread.UncaughtExceptionHandler
            public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
                if (!ActivityManagerCompat.isUserAMonkey() || IMEApplication.this.ime == null || !IMEApplication.this.ime.mBuildInfo.isLicensingOn()) {
                    if (IMEApplication.this.androidDefaultUEH != null) {
                        IMEApplication.this.androidDefaultUEH.uncaughtException(paramThread, paramThrowable);
                    }
                } else {
                    if (IMEApplication.this.androidDefaultUEH != null) {
                        IMEApplication.this.androidDefaultUEH.uncaughtException(paramThread, paramThrowable);
                    }
                    IMEApplication.log.d("IMEApplication uncaughtException:", paramThrowable, " Process PID:", Integer.valueOf(Process.myPid()));
                    Process.killProcess(Process.myPid());
                }
            }
        });
        if (!UserManagerCompat.isUserUnlocked(this)) {
            this.isUserUnlockFinished = false;
        }
    }

    protected void checkCreate() {
        if (!this.isCreated) {
            this.isCreated = true;
            create();
        }
    }

    protected UserPreferences createUserPreferences() {
        if (UserManagerCompat.isUserUnlocked(this) || Build.VERSION.SDK_INT < 24) {
            return new UserPreferences(this, this.buildInfo);
        }
        Context deviceProtectedContext = createDeviceProtectedStorageContext();
        return new UserPreferences(deviceProtectedContext, this.buildInfo);
    }

    protected AppPreferences createAppPreferences() {
        if (UserManagerCompat.isUserUnlocked(this) || Build.VERSION.SDK_INT < 24) {
            return new AppPreferences(this, this.buildInfo);
        }
        Context deviceProtectedContext = createDeviceProtectedStorageContext();
        return new AppPreferences(deviceProtectedContext, this.buildInfo);
    }

    protected void create() {
        SharedPreferences sp;
        if (!UserManagerCompat.isUserUnlocked(this) && Build.VERSION.SDK_INT >= 24) {
            sp = PreferenceManager.getDefaultSharedPreferences(createDeviceProtectedStorageContext());
        } else {
            sp = PreferenceManager.getDefaultSharedPreferences(this);
        }
        this.buildInfo = new BuildInfo(this, sp);
        this.swypeCoreLibMgr = new SwypeCoreLibManager(this, this.buildInfo.getCoreLibName());
        this.appSpecificBehavior = new AppSpecificBehavior(this);
        this.userPrefs = createUserPreferences();
        this.appPrefs = createAppPreferences();
        this.appPrefs.moveFromPrevious(this.userPrefs);
        boolean upgrade = this.appPrefs.isUpgrade();
        this.appPrefs.setUpgradeConnect(upgrade);
        this.appPrefs.ackUpgrade();
        if (upgrade) {
            upgradeThemeSkuFromV1xToV2(this.userPrefs.getCurrentThemeId());
            removeAlphaHDBOnUpgrade();
        }
        this.themeLoader = ThemeLoader.getInstance();
        this.themeLoader.init(R.styleable.ThemeTemplate, R.style.SwypeReference);
        this.themeManager = ThemeManager.createThemeManager(this);
        this.lastTheme = getCurrentTheme();
        LogManager.Log log2 = log;
        Object[] objArr = new Object[2];
        objArr[0] = "lastTheme sku:";
        objArr[1] = this.lastTheme != null ? this.lastTheme.getSku() : "";
        log2.d(objArr);
        this.lastThemeResId = this.lastTheme != null ? this.lastTheme.getResId() : 0;
        log.d("lastThemeResId:", Integer.valueOf(this.lastThemeResId));
        this.settingsLauncher = createSettingsLauncher();
        this.toolTips = new ToolTips(this);
        this.updateStatusManager = new UpdateStatusManager(this);
        this.platformUtil = new PlatformUtil(this);
        this.scraperStatusFactory = new ScraperStatus.ScraperStatusFactory();
        this.newWordsBucketFactory = new NewWordsBucketFactory();
        this.statsManager = new StatisticsManager(this);
        this.usageManager = new UsageManager(this);
        this.gestureManager = new GestureManager(this);
        this.charUtils = createCharacterUtilities();
        this.categoryMap = new SparseIntArray();
        int themeColor = getThemedColor(R.attr.traceColor);
        UserPreferences up = getUserPreferences();
        if (!up.contains(UserPreferences.HWR_AUTO_ACCEPT_COLOR)) {
            InputPrefs.setHWRThemePenColor(up, UserPreferences.HWR_AUTO_ACCEPT_COLOR, themeColor, this);
        }
        setDeviceType(getDeviceType());
        if (!this.userPrefs.getNetworkAgreementNotAsk()) {
            this.userPrefs.setNetworkAgreement(false);
        }
        this.connect = new Connect(this);
        this.hasMicrophone = getPackageManager().hasSystemFeature("android.hardware.microphone");
        this.lowEndDeviceBuild = getResources().getBoolean(R.bool.LOW_END_DEVICE_BUILD);
        this.startupSequenceInfo = new StartupSequenceInfo(this);
        if (upgrade) {
            StartupSequenceInfo startupSequenceInfo = this.startupSequenceInfo;
            StartupSequenceInfo.log.d("setUpgradeFlags");
            startupSequenceInfo.setShowBackupSync(true);
            startupSequenceInfo.setShowCud(true);
            startupSequenceInfo.setShowGettingStarted(true);
            startupSequenceInfo.setShowSplash(true);
            startupSequenceInfo.setLanguageSelectedAndInstalled$1385ff();
        }
        this.mCatalogManager = new CatalogManager(this);
    }

    private boolean removeAlphaHDBOnUpgrade() {
        File filesDir = getFilesDir();
        File folder = new File(filesDir.getAbsolutePath());
        File hdbFile = new File(folder, ALPHA_HANDWRITING_DATABASE_NAME);
        if (!hdbFile.exists()) {
            return false;
        }
        boolean removed = hdbFile.delete();
        log.d("removeAlphaHWRDbOnUpgrade...alpha hdb exists and removed: ", Boolean.valueOf(removed));
        return removed;
    }

    private void upgradeThemeSkuFromV1xToV2(String sku) {
        if (sku != null && !sku.isEmpty()) {
            String newSku = convertSkuFromV1xToV2(sku);
            if (!newSku.equals(sku)) {
                this.userPrefs.setCurrentThemeId(newSku);
            }
        }
    }

    private String convertSkuFromV1xToV2(String sku) {
        String[] oldThemeNames = getResources().getStringArray(R.array.themeNamesInV1x);
        String[] newThemeNames = getResources().getStringArray(R.array.themeNamesInV2);
        for (int i = 0; i < oldThemeNames.length; i++) {
            if (oldThemeNames[i].equals(sku) && i < newThemeNames.length) {
                return newThemeNames[i];
            }
        }
        return sku;
    }

    protected CharacterUtilities createCharacterUtilities() {
        return new CharacterUtilities(this);
    }

    private ShowSettings createSettingsLauncher() {
        try {
            return (ShowSettings) Class.forName(getString(R.string.settings_launcher)).newInstance();
        } catch (ClassNotFoundException ex) {
            throw new IllegalStateException(ex);
        } catch (IllegalAccessException ex2) {
            throw new IllegalStateException(ex2);
        } catch (InstantiationException ex3) {
            throw new IllegalStateException(ex3);
        }
    }

    public BuildInfo getBuildInfo() {
        checkCreate();
        return this.buildInfo;
    }

    public CharacterUtilities getCharacterUtilities() {
        checkCreate();
        return this.charUtils;
    }

    public void setIME(IME ime) {
        checkCreate();
        this.ime = ime;
    }

    public IME getIME() {
        return this.ime;
    }

    public IMEHandlerManager getIMEHandlerManager() {
        checkCreate();
        if (this.ime == null) {
            return null;
        }
        if (this.ime.handlerManager != null) {
            this.ime.handlerManager.refreshIME(this.ime);
            return this.ime.handlerManager;
        }
        if (this.ime.handlerManager == null && getInputMethods().isCJKOnDevice()) {
            this.ime.handlerManager = new IMEHandlerManager(this.ime);
        }
        return this.ime.handlerManager;
    }

    public StartupSequenceInfo getStartupSequenceInfo() {
        checkCreate();
        return this.startupSequenceInfo;
    }

    public boolean hasActiveIMEManagerInstance() {
        return (this.ime == null || this.ime.handlerManager == null) ? false : true;
    }

    public IMEHandler getIMEHandlerInstance() {
        if (getIMEHandlerManager() != null) {
            return this.ime.handlerManager.getIMEInstance();
        }
        return null;
    }

    public PlatformUtil getPlatformUtil() {
        checkCreate();
        return this.platformUtil;
    }

    public AppPreferences getAppPreferences() {
        checkCreate();
        return this.appPrefs;
    }

    public KeyboardManager getKeyboardManager() {
        checkCreate();
        if (this.keyboardManager == null) {
            this.keyboardManager = createKeyboardManager();
        }
        return this.keyboardManager;
    }

    public DrawBufferManager getDrawBufferManager() {
        checkCreate();
        if (this.drawBufferManager == null) {
            this.drawBufferManager = new DrawBufferManager();
        }
        return this.drawBufferManager;
    }

    public EmojiCacheManager getEmojiCacheManager() {
        checkCreate();
        if (this.emojiCacheManager == null) {
            this.emojiCacheManager = new EmojiCacheManager();
        }
        return this.emojiCacheManager;
    }

    protected KeyboardManager createKeyboardManager() {
        return new KeyboardManager(this);
    }

    public ThemeManager getThemeManager() {
        checkCreate();
        if (this.themeManager == null) {
            log.d("creating new theme manager");
            this.themeManager = ThemeManager.createThemeManager(this);
        }
        return this.themeManager;
    }

    public ThemeLoader getThemeLoader() {
        checkCreate();
        if (this.themeLoader == null) {
            this.themeLoader = ThemeLoader.getInstance();
            this.themeLoader.init(R.styleable.ThemeTemplate, R.style.SwypeReference);
        }
        return this.themeLoader;
    }

    public UserPreferences getUserPreferences() {
        checkCreate();
        return this.userPrefs;
    }

    public void releaseInstances() {
        if (this.drawBufferManager != null) {
            this.drawBufferManager.evictAll();
            this.drawBufferManager = null;
        }
        if (this.keyboardManager != null) {
            this.keyboardManager.evictAll();
            this.keyboardManager = null;
        }
        this.themeLoader = null;
    }

    public void resetThemeManagedData() {
        if (this.themeManager != null) {
            this.themeManager.clearCache();
        }
        if (this.themeLoader != null) {
            this.themeLoader.clear();
        }
    }

    @TargetApi(19)
    public boolean isLowEndDeviceBuild() {
        boolean isLowMemoryDevice = false;
        if (Build.VERSION.SDK_INT >= 19) {
            isLowMemoryDevice = ((ActivityManager) getSystemService("activity")).isLowRamDevice();
            log.d("isLowMemoryDevice...", Boolean.valueOf(isLowMemoryDevice));
        }
        return this.lowEndDeviceBuild || isLowMemoryDevice;
    }

    public boolean isSmallImageCacheDevice() {
        boolean isSmallCache = false;
        if (getMaxMemoryInMegaBytes() <= 128) {
            isSmallCache = true;
        }
        log.d("isSmallImageCacheDevice...", Boolean.valueOf(isSmallCache));
        return isSmallCache;
    }

    public ShowSettings getSettingsLauncher() {
        if (this.settingsLauncher == null) {
            this.settingsLauncher = createSettingsLauncher();
        }
        return this.settingsLauncher;
    }

    public int getMaxMemoryInMegaBytes() {
        if (this.maxMemoryInBytes == 0) {
            ActivityManager am = (ActivityManager) getSystemService("activity");
            this.maxMemoryInBytes = am.getMemoryClass();
        }
        log.d("getMaxMemoryInMegaBytes max memory:", Integer.valueOf(this.maxMemoryInBytes));
        return this.maxMemoryInBytes;
    }

    public ThemeManager.SwypeTheme getCurrentTheme() {
        checkCreate();
        if (getThemeManager() == null) {
            log.d("getThemeManager() == null");
        }
        return getThemeManager().getSwypeTheme(this.userPrefs.getCurrentThemeId());
    }

    public String getCurrentThemeId() {
        return getCurrentTheme().getSku();
    }

    public boolean getThemedBoolean(int attrResId) {
        return !getThemeLoader().bThemeApkLoaded ? ThemeUtil.getBool(getThemedContext().obtainStyledAttributes(new int[]{attrResId})) : ThemeLoader.getThemedBoolean(attrResId);
    }

    public int getThemedColor(int attrResId) {
        return !getThemeLoader().bThemeApkLoaded ? ThemeUtil.getColor(getThemedContext().obtainStyledAttributes(new int[]{attrResId})) : ThemeLoader.getThemedColor(attrResId);
    }

    public ColorStateList getThemedColorStateList(int attrResId) {
        return !getThemeLoader().bThemeApkLoaded ? ThemeUtil.getColorStateList(obtainStyledAttributes(null, new int[]{attrResId}, 0, getCurrentTheme().getResId())) : ThemeLoader.getThemedColorStateList(attrResId);
    }

    public float getThemedDimension(int attrResId) {
        return !getThemeLoader().bThemeApkLoaded ? ThemeUtil.getDimen(getThemedContext().obtainStyledAttributes((AttributeSet) null, new int[]{attrResId})) : ThemeLoader.getThemedDimension(attrResId);
    }

    public Drawable getThemedDrawable(int attrResId) {
        return !getThemeLoader().bThemeApkLoaded ? ThemeUtil.getDrawable(getThemedContext().obtainStyledAttributes(new int[]{attrResId})) : ThemeLoader.getThemedDrawable(attrResId);
    }

    public Drawable getThemedDrawableOrNoOp(int attr) {
        Drawable drawable = getThemedDrawable(attr);
        if (drawable == null) {
            return new ColorDrawable(0) { // from class: com.nuance.swype.input.IMEApplication.2
                @Override // android.graphics.drawable.Drawable
                public int getIntrinsicWidth() {
                    return 0;
                }

                @Override // android.graphics.drawable.Drawable
                public int getIntrinsicHeight() {
                    return 0;
                }
            };
        }
        return drawable;
    }

    public Context getThemedContext() {
        return new ContextThemeWrapper(this, getCurrentTheme().getResId());
    }

    public void checkIfThemeLoaded() {
        applyDownloadedTheme(this.userPrefs.getActiveDownloadedTheme());
    }

    public LayoutInflater getThemedLayoutInflater(LayoutInflater inflater) {
        Context themedContext = getThemedContext();
        return inflater.cloneInContext(themedContext);
    }

    public Context getToastContext() {
        int toastStyleResId = ThemeUtil.getResId(new ContextThemeWrapper(this, ThemeUtil.getAppThemeId(this)).obtainStyledAttributes(new int[]{R.attr.toastStyle}), R.style.ToastStyle);
        return new ContextThemeWrapper(this, toastStyleResId);
    }

    public ToolTips getToolTips() {
        checkCreate();
        return this.toolTips;
    }

    public UpdateStatusManager getUpdateStatusManager() {
        checkCreate();
        return this.updateStatusManager;
    }

    public InputMethods getInputMethods() {
        return getInputMethods(false);
    }

    public void refreshInputMethods() {
        getInputMethods(true);
    }

    public void onPreInstallLanguage(String language) {
        InputMethods.Language installLang;
        if ((this.currentLanguage != null && this.currentLanguage.usesLanguage(language)) && (installLang = getInputMethods().getAllLanguages().get(language)) != null && installLang.isChineseLanguage()) {
            int coreLangId = installLang.getCoreLanguageId();
            SwypeCoreLibrary corelib = this.swypeCoreLibMgr.getSwypeCoreLibInstance();
            InputMethods im = getInputMethods();
            if (im != null) {
                if (!corelib.getChineseCoreInstance().hasInputContext()) {
                    XT9CoreChineseInput chineseInput = getSwypeCoreLibMgr().getXT9CoreChineseInputSession();
                    chineseInput.startSession();
                    im.getCurrentInputLanguage().setLanguage(chineseInput);
                }
                corelib.getChineseCoreInstance().onPreInstallLanguage(coreLangId, true);
            }
        }
    }

    public void onPostInstallLanguage(String language) {
        InputMethods.Language installLang;
        if ((this.currentLanguage != null && this.currentLanguage.usesLanguage(language)) && (installLang = getInputMethods().getAllLanguages().get(language)) != null && installLang.isChineseLanguage()) {
            int coreLangId = installLang.getCoreLanguageId();
            SwypeCoreLibrary corelib = this.swypeCoreLibMgr.getSwypeCoreLibInstance();
            InputMethods im = getInputMethods();
            if (im != null) {
                if (!corelib.getChineseCoreInstance().hasInputContext()) {
                    XT9CoreChineseInput chineseInput = getSwypeCoreLibMgr().getXT9CoreChineseInputSession();
                    chineseInput.startSession();
                    im.getCurrentInputLanguage().setLanguage(chineseInput);
                }
                corelib.getChineseCoreInstance().onPostInstallLanguage(coreLangId, true);
            }
        }
    }

    public void onUpdateLanguage(String language) {
        InputMethods.Language updateLang = getInputMethods().getAllLanguages().get(language);
        if (updateLang != null) {
            boolean isCurrentLanguage = this.currentLanguage != null && this.currentLanguage.usesLanguage(language);
            int coreLangId = updateLang.getCoreLanguageId();
            SwypeCoreLibrary corelib = this.swypeCoreLibMgr.getSwypeCoreLibInstance();
            if (updateLang.isChineseLanguage() && corelib.isChineseInputHasContext()) {
                corelib.getChineseCoreInstance().onUpdateLanguage(coreLangId, isCurrentLanguage);
                return;
            }
            if (updateLang.isKoreanLanguage() && corelib.isKoreanInputHasContext()) {
                corelib.getKoreanCoreInstance(corelib.getAlphaCoreInstance()).onUpdateLanguage(coreLangId, isCurrentLanguage);
                return;
            }
            if (updateLang.isJapaneseLanguage() && corelib.isJapaneseInputHasContext()) {
                corelib.getJapaneseCoreInstance(corelib.getAlphaCoreInstance()).onUpdateLanguage(coreLangId, isCurrentLanguage);
            } else if (corelib.isAlphaInputHasContext()) {
                corelib.getAlphaCoreInstance().onUpdateLanguage(coreLangId, isCurrentLanguage);
            }
        }
    }

    private InputMethods getInputMethods(boolean refresh) {
        if (refresh || this.inputMethods == null) {
            checkCreate();
            InputMethods lastInputMethods = this.inputMethods;
            DatabaseConfig.refreshDatabaseConfig(this, this.buildInfo.getBuildDate());
            this.inputMethods = createInputMethods(this);
            this.inputMethods.refreshRecentLanguages();
            if (this.ime != null) {
                this.ime.updateInputMethods(this.inputMethods);
            }
            if (lastInputMethods != null) {
                Set<String> langIds = new HashSet<>();
                for (InputMethods.Language lang : lastInputMethods.getInputLanguages()) {
                    langIds.add(lang.getLanguageId());
                }
                Iterator<InputMethods.Language> it = this.inputMethods.getInputLanguages().iterator();
                while (it.hasNext()) {
                    String id = it.next().getLanguageId();
                    if (!langIds.contains(id)) {
                        this.inputMethods.forceAddRecentLanguage(id);
                    }
                }
            }
        }
        return this.inputMethods;
    }

    public int getSpeechProvider() {
        Resources res = getResources();
        if (res.getBoolean(R.bool.dictation_enabled)) {
            return res.getInteger(R.integer.speech_provider);
        }
        return 99;
    }

    public ScraperStatus.ScraperStatusFactory getScraperStatusFactory() {
        checkCreate();
        return this.scraperStatusFactory;
    }

    public NewWordsBucketFactory getNewWordsBucketFactory() {
        checkCreate();
        return this.newWordsBucketFactory;
    }

    public void resetScrapperStatus() {
        if (this.scraperStatusFactory != null) {
            this.scraperStatusFactory.reset(this);
        }
    }

    public void notifyNewWordsForScanning(NewWordsBucketFactory.NewWordsBucket bucket) {
        IME imeLocal = getIME();
        if (imeLocal != null) {
            imeLocal.startDelayScanning(bucket);
        }
    }

    public GestureManager getGestureManager() {
        checkCreate();
        return this.gestureManager;
    }

    public void startScrapingServices() {
        if (this.syncDataProviderManager == null) {
            createSyncDataProviderManager();
            startScraperAndroidDictionary();
        }
    }

    protected void startScraperAndroidDictionary() {
        getSyncDataProviderManager().addProvider(this, new AndroidUserDictionarySyncDataProvider(this, getNewWordsBucketFactory().getAndroidNewWordsBucketInstance()));
    }

    public SharedPreferences getSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void showMainSettings() {
        checkCreate();
        getSettingsLauncher().showMain(this);
    }

    public void showSettingsPrefs() {
        checkCreate();
        getSettingsLauncher().showSettingsPrefs(this);
    }

    public void showTutorial() {
        checkCreate();
        getSettingsLauncher().showTutorial(this);
    }

    public void showGestures() {
        checkCreate();
        getSettingsLauncher().showGestures(this);
    }

    public void showLanguages() {
        checkCreate();
        getSettingsLauncher().showLanguages(this);
    }

    public void showLanguageDownloads() {
        checkCreate();
        getSettingsLauncher().showLanguageDownloads(this);
    }

    public void showMyWordsPrefs() {
        checkCreate();
        getSettingsLauncher().showMyWordsPrefs(this);
    }

    public void showUpdates() {
        checkCreate();
        getSettingsLauncher().showUpdates(this);
    }

    public void showThemes() {
        checkCreate();
        getSettingsLauncher().showThemes(this);
    }

    public void showAddonDictionaries() {
        showAddonDictionaries(true);
    }

    public void showAddonDictionaries(boolean shouldClearPreviousTask) {
        checkCreate();
        getSettingsLauncher().showAddonDictionaries(this, shouldClearPreviousTask);
    }

    public void showChineseSettings() {
        checkCreate();
        getSettingsLauncher().showChineseSettings(this);
    }

    public boolean isScreenLayoutTablet() {
        if (!this.tabletVerified) {
            Configuration config = getConfiguration();
            int swDp = ConfigurationCompat.getSmallestScreenWidthDp(config);
            if (swDp != 0) {
                this.isTablet = swDp >= 600;
            } else {
                this.isTablet = (config.screenLayout & 4) != 0;
            }
            this.tabletVerified = true;
        }
        return this.isTablet;
    }

    public boolean isScreenPhablet() {
        if (!this.phabletVerified) {
            if (!isScreenLayoutTablet()) {
                KeyboardEx.KeyboardDockMode leftMode = KeyboardEx.KeyboardDockMode.DOCK_LEFT;
                if (leftMode.isEnabled(getResources(), 1) && leftMode.isEnabled(getResources(), 2)) {
                    this.isPhablet = true;
                }
            }
            this.phabletVerified = true;
        }
        return this.isPhablet;
    }

    public int getSmallestScreenWidthDp() {
        return ConfigurationCompat.getSmallestScreenWidthDp(getConfiguration());
    }

    public boolean isMiniKeyboardSupported(int currentOrientation) {
        if (isScreenLayoutTablet()) {
            return true;
        }
        if (currentOrientation != 2) {
            KeyboardEx.KeyboardDockMode movable = KeyboardEx.KeyboardDockMode.MOVABLE_MINI;
            if (currentOrientation == 1 && movable.isEnabled(getResources(), 1)) {
                return true;
            }
        }
        return false;
    }

    public String getDeviceType() {
        if (isScreenLayoutTablet()) {
            return "Tablet";
        }
        if (isScreenPhablet()) {
            return "Phablet";
        }
        return "Phone";
    }

    public int getDisplayWidth() {
        return getDisplayRectSize(new Rect()).width();
    }

    public int getDisplayHeight() {
        return getDisplayRectSize(new Rect()).height();
    }

    public Rect getDisplayRectSize(Rect outSize) {
        if (!$assertionsDisabled && outSize == null) {
            throw new AssertionError();
        }
        DisplayCompat.getRectSize(((WindowManager) getSystemService("window")).getDefaultDisplay(), outSize);
        return outSize;
    }

    public Rect getScreenRectSize(Rect outSize) {
        Rect rect = getDisplayRectSize(outSize);
        DisplayMetrics dm = getDisplay();
        rect.right = rect.left + Math.max(rect.width(), dm.widthPixels);
        rect.bottom = rect.top + Math.max(rect.height(), dm.heightPixels);
        return rect;
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public DisplayMetrics getDisplay() {
        return getResources().getDisplayMetrics();
    }

    public Configuration getConfiguration() {
        return getResources().getConfiguration();
    }

    public StatisticsManager getStatisticsManager() {
        return this.statsManager;
    }

    public UsageManager getUsageManager() {
        return this.usageManager;
    }

    public void setCurrentApplicationName(String name) {
        this.currentAppName = name;
        this.statsContext.doNotify();
    }

    public void setCurrentLanguage(InputMethods.Language language) {
        this.currentLanguage = language;
        log.d("IMEApplication.currentLanguage:" + this.currentLanguage.getCoreLanguageId());
        Connect.from(this).setCurrentLanguage(language);
        this.statsContext.doNotify();
    }

    public void setCurrentFieldInfo(int info) {
        this.currentFieldInfo = info;
        this.statsContext.doNotify();
    }

    public String getCurrentApplicationName() {
        return this.currentAppName;
    }

    public InputMethods.Language getCurrentLanguage() {
        return this.currentLanguage;
    }

    public int getCurrentFieldInfo() {
        return this.currentFieldInfo;
    }

    public void setScribeFilter(ScribeFilter filter) {
        this.scribeFilter = filter;
        this.statsContext.doNotify();
    }

    public ScribeFilter getScribeFilter() {
        return this.scribeFilter;
    }

    public void registerContextObserver(Observer o) {
        this.statsContext.addObserver(o);
    }

    public void unregisterContextObserver(Observer o) {
        this.statsContext.removeObserver(o);
    }

    public void setDeviceType(String type) {
        checkCreate();
        if (!this.userPrefs.isDeviceTypeRecorded()) {
            this.userPrefs.setDeviceType(true);
        }
    }

    public SpeechConfig getSpeechConfig() {
        if (this.speechConfig == null) {
            SpeechInfo speechInfo = SpeechInfo.makeInstance(this);
            this.speechConfig = new SpeechConfig(speechInfo.getAppName(), speechInfo.getSpeechInfoTable());
        }
        return this.speechConfig;
    }

    public synchronized Set<String> getPrivateFiles() {
        if (this.privateFiles != null) {
            this.privateFiles.clear();
            this.privateFiles = null;
        }
        this.privateFiles = new HashSet(Arrays.asList(fileList()));
        return this.privateFiles;
    }

    public synchronized Set<String> getAssetFileNames(String path) {
        if (this.assetFileNames == null || this.assetFileNames.isEmpty()) {
            try {
                this.assetFileNames = new HashSet(Arrays.asList(getAssets().list(path)));
            } catch (IOException e) {
                this.assetFileNames = Collections.emptySet();
            }
        }
        return this.assetFileNames;
    }

    public SyncDataProviderManager createSyncDataProviderManager() {
        if (this.syncDataProviderManager == null) {
            this.syncDataProviderManager = new SyncDataProviderManager();
            this.syncDataProviderManager.initialize(this);
        }
        return this.syncDataProviderManager;
    }

    public void destroySyncDataProviderManager() {
        if (this.syncDataProviderManager != null) {
            this.syncDataProviderManager.deinitialize(this);
        }
        this.syncDataProviderManager = null;
    }

    public SyncDataProviderManager getSyncDataProviderManager() {
        return this.syncDataProviderManager;
    }

    public SpeechWrapper newSpeechWrapperInstance() {
        if (this.speechWrapper == null) {
            this.speechWrapper = new SpeechWrapper(this);
            this.speechWrapper.onCreate();
        }
        return this.speechWrapper;
    }

    public SpeechWrapper getSpeechWrapper() {
        if (this.speechWrapper == null && getSpeechProvider() == 0) {
            newSpeechWrapperInstance();
        }
        return this.speechWrapper;
    }

    public void destroySpeechWrapperInstance() {
        if (this.speechWrapper != null) {
            this.speechWrapper.onDestroy();
            this.speechWrapper = null;
        }
    }

    public EmojiInputController getEmojiInputViewController() {
        if (this.emojiInputViewController == null) {
            this.emojiInputViewController = new EmojiInputController();
        }
        return this.emojiInputViewController;
    }

    protected CorrectionSpanFactory createCorrectionSpanFactory() {
        return new CorrectionSpanFactory();
    }

    public CorrectionSpanFactory getCorrectionSpanFactory() {
        if (this.correctionSpanFactory == null) {
            this.correctionSpanFactory = createCorrectionSpanFactory();
        }
        return this.correctionSpanFactory;
    }

    public boolean isImeInUse() {
        return getIME() != null && getIME().isImeInUse();
    }

    public void onImeInUse(boolean inUse) {
        this.imeSubject.doNotify();
    }

    public void registerImeObserver(Observer observer) {
        this.imeSubject.addObserver(observer);
    }

    public void unregisterImeObserver(Observer observer) {
        this.imeSubject.removeObserver(observer);
    }

    public SystemState getSystemState() {
        if (this.sysState == null) {
            this.sysState = new SystemState(this);
        }
        return this.sysState;
    }

    public void setInputCategory(int category, int language) {
        if (category != 0) {
            this.categoryMap.put(language, category);
            this.statsContext.doNotify();
        }
    }

    public int getInputCategory(InputMethods.Language lang) {
        checkCreate();
        if (lang == null || this.ime == null || !this.ime.hasCurrentActiveCore()) {
            return 0;
        }
        return this.categoryMap.get(lang.getCoreLanguageId(), 0);
    }

    public void statsSettingChanged() {
        if (this.ime != null) {
            this.ime.reloadKeyboard();
        }
    }

    public boolean checkPermission(String permission) {
        return getApplicationContext().checkCallingOrSelfPermission(permission) == 0;
    }

    public boolean checkThemeChanged() {
        ThemeManager.SwypeTheme theme = getCurrentTheme();
        String activeDownloadedTheme = UserPreferences.from(getApplicationContext()).getActiveDownloadedTheme();
        if (!theme.equals(this.lastTheme) || !activeDownloadedTheme.equals("")) {
            if (this.lastTheme != null && !theme.equals(this.lastTheme) && !this.userPrefs.contains(UserPreferences.HWR_AUTO_ACCEPT_COLOR)) {
                int themeColor = getThemedColor(R.attr.traceColor);
                InputPrefs.setHWRThemePenColor(getUserPreferences(), UserPreferences.HWR_AUTO_ACCEPT_COLOR, themeColor, this);
            }
            this.lastTheme = theme;
            this.lastThemeResId = theme.getResId();
            ThemedResources.onThemeChanged();
            String apkPath = null;
            if (theme.getSku().endsWith(".apk")) {
                if (theme.getSku().contains("/")) {
                    apkPath = theme.getSku();
                }
                if (apkPath != null) {
                    getThemeLoader().loadThemeApkFile(getThemedContext(), apkPath);
                }
            } else if (!activeDownloadedTheme.equals("")) {
                checkIfThemeLoaded();
            } else {
                getThemeLoader().clear();
            }
            return true;
        }
        return false;
    }

    public boolean isThemeChanged() {
        return this.lastThemeResId != getCurrentTheme().getResId();
    }

    public Whitelist createFirstTimeStartWhitelist() {
        return getBuildInfo().getBuildType() == BuildInfo.BuildType.PRODUCTION ? Whitelist.fromWhitelist(this, R.array.first_time_startup_whitelist) : new Whitelist();
    }

    public Whitelist createPortraitCandidatesViewFilter() {
        return getResources().getBoolean(R.bool.enable_portrait_cv_blacklist) ? Whitelist.fromBlacklist(this, R.array.portrait_cv_blacklist) : new Whitelist();
    }

    public AppContextPredictionSetter getAppContextPredictionSetter() {
        if (this.appContextSetter == null) {
            this.appContextSetter = new AppContextPredictionSetter(this);
        }
        return this.appContextSetter;
    }

    public ShowFirstTimeStartupMessages createFirstTimeStartupMessages() {
        return new ShowFirstTimeStartupMessages(this, createFirstTimeStartWhitelist());
    }

    @Override // android.app.Application, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        ThemedResources.onConfigChanged();
        if (AdsUtil.sAdsSupported) {
            getAdSessionTracker().setScreenOrientation(newConfig.orientation);
        }
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public Resources getResources() {
        Resources res = super.getResources();
        return this.themeManager == null ? res : ThemedResources.from(res.getAssets(), res.getDisplayMetrics(), res.getConfiguration(), this);
    }

    public SwypeCoreLibManager getSwypeCoreLibMgr() {
        checkCreate();
        return this.swypeCoreLibMgr;
    }

    public UserDictionaryIterator createUserDictionaryIterator(InputMethods.Language language) {
        if (InputMethods.Language.isChineseLanguage(language.getCoreLanguageId())) {
            return UserDictionaryIterator.createChineseIterator(getSwypeCoreLibMgr().getXT9CoreChineseInputSession(), language, getSpeechWrapper());
        }
        return UserDictionaryIterator.createAlphaIterator(getSwypeCoreLibMgr().getXT9CoreAlphaInputSession(), language, getSpeechWrapper());
    }

    public boolean hasActiveHardKeyIMEHandlerInstance() {
        return (this.ime == null || this.hardKeyboardHandler == null || !this.ime.isHardKeyboardActive()) ? false : true;
    }

    public IMEHandler getHardKeyIMEHandlerInstance() {
        if (this.ime == null || !this.ime.isHardKeyboardActive()) {
            return null;
        }
        checkCreate();
        if (this.hardKeyboardHandler == null) {
            this.hardKeyboardHandler = new HardKeyIMEHandler(this.ime);
        }
        return this.hardKeyboardHandler;
    }

    public IMEHandler getHardKeyIMEHandler() {
        return this.hardKeyboardHandler;
    }

    public Connect getConnect() {
        checkCreate();
        return this.connect;
    }

    public AlertDialog.Builder getNoConnectionDialogBuilder(Context context, Runnable cancelAction) {
        return null;
    }

    public WordDecorator createUnrecognizedWordDecorator(InputView view) {
        return new WordDecorator(view);
    }

    public Intent getLegalActivitiesStartIntent(Intent intent) {
        return intent;
    }

    public boolean isMultipleEnabledSubtypeAvailable(int swypeActiveLangCount) {
        return swypeActiveLangCount > 1;
    }

    public void notifyLanguageInstalled(String langName, int status) {
    }

    public boolean getDefaultKeyStyleSetting(int[] codes, int attr, boolean fallback) {
        return fallback;
    }

    public boolean hasMicrophone() {
        return this.hasMicrophone;
    }

    public CatalogManager getCatalogManager() {
        checkCreate();
        return this.mCatalogManager;
    }

    public void applyDownloadedTheme(String themeSku) {
        if (themeSku == null || themeSku.equals("")) {
            log.d("themeSku is null");
            return;
        }
        log.d("themeSku is: ", themeSku);
        if (this.themeLoader == null) {
            this.themeLoader = ThemeLoader.getInstance();
            this.themeLoader.init(R.styleable.ThemeTemplate, R.style.SwypeReference);
        }
        String apkPath = getThemeApkPath(themeSku);
        if (apkPath != null) {
            log.d("Apk file path is: ", apkPath);
            this.themeLoader.loadThemeApkFile(getThemedContext(), apkPath);
            log.d("Theme loaded from APK");
            return;
        }
        log.d("Apk file path is null");
    }

    public String getNewThemeApkPath(String themeSku) {
        return getApplicationContext().getFilesDir().getAbsolutePath() + "/themes/" + themeSku + "/" + themeSku + ".apk";
    }

    public String getThemeApkPath(String themeSku) {
        File[] dirFiles;
        String apkPath = getNewThemeApkPath(themeSku);
        if (new File(apkPath).exists()) {
            return apkPath;
        }
        File dir = new File(getApplicationContext().getFilesDir().getAbsolutePath() + "/themes/" + themeSku);
        if (dir.exists() && (dirFiles = dir.listFiles(new FilenameFilter() { // from class: com.nuance.swype.input.IMEApplication.3
            @Override // java.io.FilenameFilter
            public boolean accept(File dir2, String name) {
                return name.toLowerCase(Locale.US).endsWith(".apk");
            }
        })) != null && dirFiles.length > 0) {
            apkPath = dirFiles[0].getPath();
        }
        return apkPath;
    }

    public void clearKeyboardCache() {
        if (this.keyboardManager != null) {
            this.keyboardManager.evictAll();
        }
    }

    protected InputMethods createInputMethods(Context context) {
        return new InputMethods(context);
    }

    public BillboardManager getBillboardManager() {
        if (this.mBillboardManager == null) {
            this.mBillboardManager = new BillboardManager(this);
            this.mBillboardManager.setBillboardDisplayStrategy(getAdSessionTracker());
        }
        return this.mBillboardManager;
    }

    public void releaseBillboardManager() {
        this.mBillboardManager = null;
    }

    public BillboardSessionTracker getAdSessionTracker() {
        if (this.mAdSessionTracker == null) {
            getApplicationContext();
            this.mAdSessionTracker = new BillboardSessionTracker(getResources().getInteger(R.integer.ad_config_show_first_on), getResources().getInteger(R.integer.ad_config_show_step_size), getResources().getInteger(R.integer.ad_config_max_ads_per_day));
        }
        return this.mAdSessionTracker;
    }

    public AppSpecificBehavior getAppSpecificBehavior() {
        return this.appSpecificBehavior;
    }

    public void updateCustomDimensions() {
        String cloudSetting;
        String str;
        int wordCount;
        String str2;
        String[] themePath;
        String themeSku = getCurrentTheme().getSku();
        if (StringUtils.isApkCompletePath(themeSku) && (themePath = themeSku.split("/")) != null && themePath.length > 2) {
            themeSku = themePath[themePath.length - 2];
        }
        int orientation = getResources().getConfiguration().orientation;
        String cloudOpt = AppPreferences.from(getApplicationContext()).getChineseCloudNetworkOption();
        if (cloudOpt == null || cloudOpt.isEmpty()) {
            cloudSetting = AppPreferences.CHINESE_CLOUD_DISABLED;
        } else {
            cloudSetting = cloudOpt;
        }
        InputMethods.Language language = this.currentLanguage;
        if (language == null) {
            getInputMethods().syncWithCurrentUserConfiguration();
            language = getInputMethods().getCurrentInputLanguage();
        }
        if (language == null) {
            log.d("updateCustomDimensions: ERROR: could not determine current language");
        } else {
            UsageData.setCustomDimension(CustomDimension.Dimension.KEYBOARD_LANGUAGE, language.mEnglishName);
            UsageData.setCustomDimension(CustomDimension.Dimension.BILINGUAL, CustomDimension.isEnabled((language instanceof BilingualLanguage) || (language.isChineseLanguage() && this.userPrefs.getEnableChineseBilingual())));
            String keyboardLayout = null;
            Configuration config = new Configuration(getResources().getConfiguration());
            config.setLocale(new Locale("en"));
            Resources englishRes = createConfigurationContext(config).getResources();
            int inputModeNameResId = language.getCurrentInputMode().getDisplayInputModeID();
            int layoutNameResId = language.getCurrentInputMode().getCurrentLayout().getDisplayLayoutNameID();
            if (inputModeNameResId > 0) {
                String inputMode = englishRes.getString(inputModeNameResId);
                log.d("updateCustomDimensions: inputModeNameResId " + inputMode);
                if (!englishRes.getString(R.string.full_keyboard).equals(inputMode)) {
                    keyboardLayout = inputMode;
                }
            }
            if (layoutNameResId > 0) {
                String layoutName = englishRes.getString(layoutNameResId);
                log.d("updateCustomDimensions: layoutNameResId " + layoutName);
                if (!englishRes.getString(R.string.full_keyboard).equals(layoutName)) {
                    keyboardLayout = englishRes.getString(layoutNameResId);
                }
            }
            if (keyboardLayout == null) {
                keyboardLayout = language.getCurrentInputMode().getDisplayInputMode();
                log.d("updateCustomDimensions: getDisplayInputMode " + keyboardLayout);
            }
            log.d("updateCustomDimensions: keyboardLayout final " + keyboardLayout);
            UsageData.setCustomDimension(CustomDimension.Dimension.KEYBOARD_LAYOUT, keyboardLayout);
            if (InputMethods.Language.isChineseLanguage(language.getCoreLanguageId())) {
                wordCount = getSwypeCoreLibMgr().getSwypeCoreLibInstance().getChineseCoreInstance().dlmCount();
            } else {
                wordCount = getSwypeCoreLibMgr().getSwypeCoreLibInstance().getAlphaCoreInstance().dlmCount();
            }
            log.d("updateCustomDimensions: wordCount " + wordCount);
            CustomDimension.Dimension dimension = CustomDimension.Dimension.DICTIONARY_WORDS;
            if (wordCount <= 0) {
                str2 = "0";
            } else if (wordCount <= 10) {
                str2 = "1-10";
            } else if (wordCount <= 25) {
                str2 = "11-25";
            } else if (wordCount <= 75) {
                str2 = "26-75";
            } else if (wordCount <= 125) {
                str2 = "76-125";
            } else if (wordCount <= 200) {
                str2 = "126-200";
            } else if (wordCount <= 400) {
                str2 = "201-400";
            } else if (wordCount <= 600) {
                str2 = "401-600";
            } else if (wordCount <= 800) {
                str2 = "601-800";
            } else if (wordCount <= 1000) {
                str2 = "801-1000";
            } else {
                str2 = "1000+";
            }
            UsageData.setCustomDimension(dimension, str2);
        }
        CustomDimension.DeviceType deviceType = isScreenLayoutTablet() ? CustomDimension.DeviceType.TABLET : CustomDimension.DeviceType.HANDSET;
        UsageData.setCustomDimension(CustomDimension.Dimension.DEVICE_TYPE, deviceType == null ? "Not Available" : deviceType.toString());
        UsageData.setCustomDimension(CustomDimension.Dimension.ABC_KEYBOARD_MODE, this.userPrefs.getKeyboardDockingMode(orientation).name());
        UsageData.setCustomDimension(CustomDimension.Dimension.THEME_NAME, themeSku);
        UsageData.setCustomDimension(CustomDimension.Dimension.AUTO_CORRECT, CustomDimension.isEnabled(this.userPrefs.getAutoCorrection()));
        UsageData.setCustomDimension(CustomDimension.Dimension.NUMBER_ROW, CustomDimension.isEnabled(this.userPrefs.getShowNumberRow()));
        log.d("showNumberRow: ", Boolean.valueOf(this.userPrefs.getShowNumberRow()));
        UsageData.setCustomDimension(CustomDimension.Dimension.SECONDARY_ENABLED, CustomDimension.isEnabled(!this.userPrefs.getKeyboardHideSecondaries()));
        LogManager.Log log2 = log;
        Object[] objArr = new Object[2];
        objArr[0] = "secondaryKeys: ";
        objArr[1] = Boolean.valueOf(!this.userPrefs.getKeyboardHideSecondaries());
        log2.d(objArr);
        UsageData.setCustomDimension(CustomDimension.Dimension.PACKAGE_ID, getApplicationContext().getPackageName());
        UsageData.setCustomDimension(CustomDimension.Dimension.ENABLE_HWR, CustomDimension.isEnabled(this.userPrefs.isHandwritingEnabled()));
        UsageData.setCustomDimension(CustomDimension.Dimension.KB_HEIGHT_PORTRAIT, (this.userPrefs.getKeyboardScalePortrait() * 100.0f) + "%");
        UsageData.setCustomDimension(CustomDimension.Dimension.KB_HEIGHT_LANDSCAPE, (this.userPrefs.getKeyboardScaleLandscape() * 100.0f) + "%");
        CustomDimension.DictionaryBehavior dictionaryBehavior = this.userPrefs.getAskBeforeAdd() ? CustomDimension.DictionaryBehavior.MANUAL : CustomDimension.DictionaryBehavior.AUTOMATIC;
        UsageData.setCustomDimension(CustomDimension.Dimension.DICTIONARY_BEHAVIOR, dictionaryBehavior == null ? null : dictionaryBehavior.toString());
        UsageData.setCustomDimension(CustomDimension.Dimension.SOUND_ON_KEYPRESS, CustomDimension.isEnabled(this.userPrefs.isKeySoundOn()));
        UsageData.setCustomDimension(CustomDimension.Dimension.NEXT_WORD_PREDICTION, CustomDimension.isEnabled(this.userPrefs.isNextWordPredictionEnabled()));
        int longPressDelay = this.userPrefs.getLongPressDelay();
        CustomDimension.Dimension dimension2 = CustomDimension.Dimension.LONG_PRESS_DELAY;
        long j = longPressDelay;
        if (j >= 0 && j <= 200) {
            str = "0-200ms";
        } else if (j > 200 && j <= 400) {
            str = "200-400ms";
        } else if (j > 400 && j <= 600) {
            str = "400-600ms";
        } else if (j > 600 && j <= 800) {
            str = "600-800ms";
        } else if (j > 800 && j <= 1000) {
            str = "800-1000ms";
        } else if (j > 1000) {
            str = "1000ms +";
        } else {
            str = "invalid";
        }
        UsageData.setCustomDimension(dimension2, str);
        UsageData.setCustomDimension(CustomDimension.Dimension.EMOJI_PREDICTION, CustomDimension.isEnabled(this.userPrefs.isEmojiSuggestionsEnabled()));
        UsageData.setCustomDimension(CustomDimension.Dimension.CLOUD_INPUT, cloudSetting);
    }

    @Override // android.content.ContextWrapper
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    public boolean isWCLMessage() {
        return this.isWCLMessage;
    }

    public void setWCLMessage(boolean WCLMessage) {
        this.isWCLMessage = WCLMessage;
    }

    public boolean isUserTapKey() {
        return this.isUserTapKey;
    }

    public void setUserTapKey(boolean userTapKey) {
        this.isUserTapKey = userTapKey;
    }

    public boolean isTrialExpired() {
        BuildInfo buildInfo = getBuildInfo();
        if (!buildInfo.isTrialBuild()) {
            return false;
        }
        buildInfo.updateExpirationPeriod();
        return buildInfo.isTrialPeriodExpired();
    }

    @TargetApi(24)
    public void updatePreferencesConfig() {
        this.appPrefs = createAppPreferences();
        this.userPrefs = createUserPreferences();
        if (Build.VERSION.SDK_INT >= 24) {
            Context deviceProtectedContext = createDeviceProtectedStorageContext();
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(deviceProtectedContext);
            this.userPrefs.importPreferences(sp);
            sp.edit().clear().commit();
            SharedPreferences appSp = deviceProtectedContext.getSharedPreferences(AppPreferences.APP_PREFS_FILE, 0);
            this.appPrefs.importPreferences(appSp);
            appSp.edit().clear().commit();
        }
        this.appPrefs.moveFromPrevious(this.userPrefs);
    }

    public boolean isUserUnlockFinished() {
        return this.isUserUnlockFinished;
    }

    public void setUserUnlockFinished(boolean userUnlockFinished) {
        this.isUserUnlockFinished = userUnlockFinished;
    }

    public void postUserUnlock() {
        updatePreferencesConfig();
        this.connect.setContext(this);
        this.connect.startCreatorThread();
        if (this.userPrefs.isUsageCollectionEnabled()) {
            Connect.from(getApplicationContext()).setContributeUsageData(this.userPrefs.isUsageCollectionEnabled());
        }
        this.mCatalogManager.mContext = this;
        this.themeManager.setThemeManagerContext(this);
        DatabaseConfig.refreshDatabaseConfig(this, getBuildInfo().getBuildDate());
        DatabaseConfig.refreshDatabaseConfigInJNI(this);
        getEmojiInputViewController().setContext(getThemedContext());
        refreshInputMethods();
        setUserUnlockFinished(true);
    }
}
