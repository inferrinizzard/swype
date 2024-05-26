package com.nuance.swype.connect;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Patterns;
import android.util.SparseArray;
import android.view.inputmethod.EditorInfo;
import com.nuance.android.compat.NotificationCompat;
import com.nuance.android.compat.UserManagerCompat;
import com.nuance.android.util.WeakReferenceHandler;
import com.nuance.connect.comm.MessageAPI;
import com.nuance.connect.internal.common.Document;
import com.nuance.connect.store.DataStoreFactory;
import com.nuance.connect.store.PersistentDataStore;
import com.nuance.dlm.ACAlphaInput;
import com.nuance.input.swypecorelib.SwypeCoreLibrary;
import com.nuance.nmsp.client.sdk.components.core.calllog.SessionEvent;
import com.nuance.swype.connect.ConnectLegal;
import com.nuance.swype.connect.api.ConnectAccount;
import com.nuance.swype.inapp.CatalogManager;
import com.nuance.swype.input.AppPreferences;
import com.nuance.swype.input.BilingualLanguage;
import com.nuance.swype.input.BuildInfo;
import com.nuance.swype.input.CategoryDBList;
import com.nuance.swype.input.DatabaseConfig;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.InputMethods;
import com.nuance.swype.input.LanguageList;
import com.nuance.swype.input.License;
import com.nuance.swype.input.R;
import com.nuance.swype.input.UserPreferences;
import com.nuance.swype.input.settings.ConnectAccountDispatch;
import com.nuance.swype.measure.UsecaseStopwatch;
import com.nuance.swype.measure.Usecases;
import com.nuance.swype.stats.StatisticsManager;
import com.nuance.swype.stats.UsageManager;
import com.nuance.swype.util.LogManager;
import com.nuance.swypeconnect.ac.ACAccount;
import com.nuance.swypeconnect.ac.ACAccountService;
import com.nuance.swypeconnect.ac.ACCatalogService;
import com.nuance.swypeconnect.ac.ACChineseDictionaryDownloadService;
import com.nuance.swypeconnect.ac.ACChinesePredictionService;
import com.nuance.swypeconnect.ac.ACConfiguration;
import com.nuance.swypeconnect.ac.ACDLMConnector;
import com.nuance.swypeconnect.ac.ACDLMSyncService;
import com.nuance.swypeconnect.ac.ACDevice;
import com.nuance.swypeconnect.ac.ACDeviceService;
import com.nuance.swypeconnect.ac.ACException;
import com.nuance.swypeconnect.ac.ACLanguage;
import com.nuance.swypeconnect.ac.ACLanguageDownloadService;
import com.nuance.swypeconnect.ac.ACLivingLanguageService;
import com.nuance.swypeconnect.ac.ACManager;
import com.nuance.swypeconnect.ac.ACReportingService;
import com.nuance.swypeconnect.ac.ACScannerService;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* loaded from: classes.dex */
public class Connect {
    private static final String ACCOUNT_PREF = "account_ACTIVE_ACCOUNT";
    private static final String ACCOUNT_PREF_OLD = "account_ACCOUNT";
    private static final String COMPRESSION_EXTENSION = ".mp3";
    private static final String CORE_VERSION = "9.12";
    private static final int MESSAGE_POST_START = 101;
    private static final int MESSAGE_START = 100;
    private static final int NOTIFY_STATS_SETTING_CHANGED = 102;
    private static final LogManager.Log log = LogManager.getLog(SessionEvent.NMSP_CALLLOG_CONNECT_EVENT);
    private static final LogManager.Trace trace = LogManager.getTrace();
    private volatile ACAccountService accountService;
    private volatile ACCatalogService catalogService;
    private volatile ACChinesePredictionService chinesePredictionService;
    private volatile ACChineseDictionaryDownloadService chineseService;
    private ConnectLegal connectLegal;
    private ConnectedStatus connection;
    private Context context;
    private volatile DefaultSyncCallback defaultSyncCallback;
    private SDKDictionaryDownloadManager dictionaryDownloadManager;
    private SDKDownloadManager downloadManager;
    private boolean imeIdle;
    private volatile boolean isDataUsageOptInAccepted;
    private volatile boolean isStaticsCollectionEnabled;
    private volatile boolean isUsageCollectionEnabled;
    private Boolean licenseFileValid;
    private volatile ACLivingLanguageService livingLanguageService;
    private NotificationManager notificationManager;
    private volatile ACReportingService reportingService;
    private volatile ACScannerService scannerService;
    private volatile ACDLMSyncService syncService;
    private SDKUpdateManager updateManager;
    private Pattern mCoreVersionPattern = Pattern.compile("(\\d+\\.)(\\d+)");
    protected int maxConnectionsWifi = 1;
    protected int maxConnectionsCellular = 1;
    private volatile ACManager acManager = null;
    private volatile ACLanguage language = null;
    private volatile Locale initialLocale = Locale.getDefault();
    private volatile int[] initialLanguage = null;
    protected volatile boolean wifiConnected = false;
    protected volatile boolean cellularConnected = false;
    private boolean isLicensed = true;
    private final Queue<Runnable> postStartEvents = new ConcurrentLinkedQueue();
    private final Handler.Callback handlerCallback = new Handler.Callback() { // from class: com.nuance.swype.connect.Connect.1
        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 100:
                    Connect.this.start();
                    return true;
                case 101:
                    if (!Connect.this.postStartEvents.isEmpty()) {
                        ((Runnable) Connect.this.postStartEvents.remove()).run();
                        if (!Connect.this.postStartEvents.isEmpty()) {
                            Connect.this.queue.sendEmptyMessage(101);
                        }
                    }
                    return true;
                case 102:
                    IMEApplication.from(Connect.this.context).statsSettingChanged();
                    return true;
                default:
                    return false;
            }
        }
    };
    private Handler queue = WeakReferenceHandler.create(this.handlerCallback);
    private final ACChineseDictionaryDownloadService.ACChineseDictionaryDownloadListCallback chineseListCallback = new ACChineseDictionaryDownloadService.ACChineseDictionaryDownloadListCallback() { // from class: com.nuance.swype.connect.Connect.2
        @Override // com.nuance.swypeconnect.ac.ACChineseDictionaryDownloadService.ACChineseDictionaryDownloadListCallback
        public void availableDictionaries(List<ACChineseDictionaryDownloadService.ACChineseDictionary> dictionaries) {
            Connect.log.d("Chinese Dictionaries: ### Available Dictionaries and categories; count=", Integer.valueOf(dictionaries.size()));
        }

        @Override // com.nuance.swypeconnect.ac.ACChineseDictionaryDownloadService.ACChineseDictionaryDownloadListCallback
        public void downloadedDictionaries(List<ACChineseDictionaryDownloadService.ACChineseDictionary> dictionaries) {
            Connect.log.d("Chinese Dictionaries: ### downloaded Dictionaries and categories starting; count=", Integer.valueOf(dictionaries.size()));
        }

        @Override // com.nuance.swypeconnect.ac.ACChineseDictionaryDownloadService.ACChineseDictionaryDownloadListCallback
        public void updatableDictionaries(List<ACChineseDictionaryDownloadService.ACChineseDictionary> dictionaries) {
            Connect.log.d("Chinese Dictionaries: ### Updatable Dictionaries and categories starting; count=", Integer.valueOf(dictionaries.size()));
            for (ACChineseDictionaryDownloadService.ACChineseDictionary dict : dictionaries) {
                Connect.log.d("Chinese Dictionaries: Updatable ### name = " + dict.getName() + " category = " + dict.getCategory() + " key = " + dict.getKey() + " language = " + dict.getLanguage());
                Connect.this.getDictionaryDownloadManager().dictionaryDownload(dict);
            }
            Connect.log.d("Chinese Dictionaries: ### Updatable dictionaries and categories complete");
        }
    };
    private volatile boolean created = false;
    private final CountDownLatch creatorLatch = new CountDownLatch(1);
    private final Thread creatorThread = new Thread() { // from class: com.nuance.swype.connect.Connect.3
        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            LogManager.Trace unused = Connect.trace;
            BuildInfo info = BuildInfo.from(Connect.this.context);
            String appkey = info.getAppKey() + Document.ID_SEPARATOR + info.getSwib();
            Connect.this.migrateInternalAccountData();
            LogManager.Trace unused2 = Connect.trace;
            final String[] services = {ACManager.LANGUAGE_SERVICE, ACManager.LIVING_LANGUAGE_SERVICE, ACManager.ACCOUNT_SERVICE, ACManager.DLM_SYNC_SERVICE, ACManager.CHINESE_DICTIONARY_SERVICE, ACManager.SCANNER_SERVICE, ACManager.CATALOG_SERVICE, ACManager.CHINESE_CLOUD_PREDICTION, ACManager.REPORTING_SERVICE, ACManager.PACKAGE_UPDATER_SERVICE};
            ACManager.ACInitializeInfo initializeInfo = new ACManager.ACInitializeInfo(Connect.this.context, appkey) { // from class: com.nuance.swype.connect.Connect.3.1
                @Override // com.nuance.swypeconnect.ac.ACManager.ACInitializeInfo
                public String legacySecretKey() {
                    return ACAlphaInput.acAlphaLegacySecretKey();
                }

                @Override // com.nuance.swypeconnect.ac.ACManager.ACInitializeInfo
                public String[] services() {
                    return services;
                }
            };
            Connect.this.acManager = new ACManager(initializeInfo);
            LogManager.Trace unused3 = Connect.trace;
            Connect.this.notificationManager = (NotificationManager) Connect.this.context.getSystemService("notification");
            try {
                SwypeCoreLibrary corelib = IMEApplication.from(Connect.this.context).getSwypeCoreLibMgr().getSwypeCoreLibInstance();
                String coreVersionAlpha = Connect.CORE_VERSION;
                String coreVersionChinese = (corelib.isChineseInputBuildEnabled() && info.isChineseCoreEnabled()) ? Connect.CORE_VERSION : null;
                String coreVersionJapanese = info.isJapaneseCoreEnabled() ? Connect.CORE_VERSION : null;
                String coreVersionKorean = info.isKoreanCoreEnabled() ? Connect.CORE_VERSION : null;
                String coreLibVersion = corelib.getXT9CoreVersion();
                Matcher mMatcherCoreVersion = Connect.this.mCoreVersionPattern.matcher(coreLibVersion);
                if (mMatcherCoreVersion.find()) {
                    coreVersionAlpha = mMatcherCoreVersion.group(1) + Integer.valueOf(mMatcherCoreVersion.group(2));
                    coreVersionChinese = (corelib.isChineseInputBuildEnabled() && info.isChineseCoreEnabled()) ? coreVersionAlpha : null;
                    coreVersionJapanese = info.isJapaneseCoreEnabled() ? coreVersionAlpha : null;
                    coreVersionKorean = info.isKoreanCoreEnabled() ? coreVersionAlpha : null;
                }
                Connect.this.acManager.setCoreVersions(coreVersionAlpha, coreVersionChinese, coreVersionJapanese, coreVersionKorean);
                Connect.this.acManager.getConfiguration().setMinimumSSLProtocol(ACConfiguration.ACSSLProtocol.TLSv1_2);
            } catch (ACException ex) {
                Connect.log.e("Failed to set core versions: " + ex.getMessage());
            }
            Connect.this.acManager.getConfiguration().setCustomerString(info.getSwib());
            setProperties(Connect.this.acManager);
            boolean cellularEnabled = IMEApplication.from(Connect.this.context).getUserPreferences().connectUseCellularData();
            Connect.this.enableCellularData(cellularEnabled);
            Connect.this.language = Connect.this.acManager.getLanguageSettings();
            if (Connect.this.imeIdle) {
                Connect.this.language.onFinishInput();
            }
            if (Connect.this.initialLocale != null) {
                Connect.this.language.setActiveLocale(Connect.this.initialLocale);
                Connect.this.initialLocale = null;
            }
            Connect.this.queue.sendEmptyMessage(100);
            Connect.this.creatorLatch.countDown();
            LogManager.Trace unused4 = Connect.trace;
        }

        private void setProperties(ACManager acManager) {
            ACConfiguration config = acManager.getConfiguration();
            try {
                Connect.log.d("Setting PAYMENT_PROCESSOR: ", "GOOGLE_PLAY");
                config.setConfigurationProperty("PAYMENT_PROCESSOR", "GOOGLE_PLAY", true);
                Connect.log.d("Setting THEME_ENGINE: ", MessageAPI.SESSION_ID);
                config.setConfigurationProperty("THEME_ENGINE", MessageAPI.SESSION_ID, true);
                BuildInfo info = BuildInfo.from(Connect.this.context);
                String swib = info.getSwib();
                Connect.log.d("Setting SKA_SWIB: ", swib);
                config.setConfigurationProperty("SKA_SWIB", swib, true);
                String buildType = info.getBuildType().toString();
                Connect.log.d("Setting SKA_TYPE: ", buildType);
                config.setConfigurationProperty("SKA_TYPE", buildType, true);
                String version = info.getBuildVersion();
                Connect.log.d("Setting SKA_VERSION: ", version);
                config.setConfigurationProperty("SKA_VERSION", version, true);
                String robWorkspace = info.getRobWorkspace();
                Connect.log.d("Setting SWYPE_OEM_ID: ", robWorkspace);
                config.setConfigurationProperty("SWYPE_OEM_ID", robWorkspace, false);
            } catch (ACException e) {
                Connect.log.d("Error setting config. ", e.getLocalizedMessage());
            }
        }
    };
    final ACLivingLanguageService.ACLivingLanguageCallback livingLanguageCallback = new ACLivingLanguageService.ACLivingLanguageCallback() { // from class: com.nuance.swype.connect.Connect.6
        @Override // com.nuance.swypeconnect.ac.ACLivingLanguageService.ACLivingLanguageCallback
        public void subscribed(int categoryId, int categoryType, int languageId, String locale, String country, int records) {
            Connect.log.d("Living Language: subscribed categoryId=" + categoryId + " categoryType=" + categoryType + " languageId=" + languageId + " locale=" + locale + " count=" + records);
        }

        @Override // com.nuance.swypeconnect.ac.ACLivingLanguageService.ACLivingLanguageCallback
        public void unsubscribed(int categoryId, int categoryType, int languageId, String locale, String country) {
            Connect.log.d("[Living Language: unsubscribed categoryId=" + categoryId + " categoryType=" + categoryType + " languageId=" + languageId + " locale=" + locale);
        }

        @Override // com.nuance.swypeconnect.ac.ACLivingLanguageService.ACLivingLanguageCallback
        public void updated(int categoryId, int categoryType, int languageId, String locale, String country, int records) {
            Connect.log.d("Living Language: updated categoryId=" + categoryId + " categoryType=" + categoryType + " languageId=" + languageId + " locale=" + locale + " count=" + records);
        }

        @Override // com.nuance.swypeconnect.ac.ACLivingLanguageService.ACLivingLanguageCallback
        public void downloadProgress(int categoryId, int categoryType, int languageId, String locale, String country, int percent) {
            Connect.log.d("Living Language: download progress categoryId=" + categoryId + " categoryType=" + categoryType + " languageId=" + languageId + " locale=" + locale + " progress=" + percent);
        }

        @Override // com.nuance.swypeconnect.ac.ACLivingLanguageService.ACLivingLanguageCallback
        public void updatesAvailable(boolean updates) {
            Connect.log.d("Living Language: updates available status=" + updates);
        }
    };
    private ACDLMSyncService.ACDLMSyncCallback acDLMSyncCallback = new ACDLMSyncService.ACDLMSyncCallback() { // from class: com.nuance.swype.connect.Connect.7
        private void persistUserDatabase() {
            SwypeCoreLibrary corelib = IMEApplication.from(Connect.this.context).getSwypeCoreLibMgr().getSwypeCoreLibInstance();
            if (corelib.isAlphaInputHasContext()) {
                corelib.getAlphaCoreInstance().persistUserDatabase();
            } else if (corelib.isChineseInputHasContext()) {
                corelib.getChineseCoreInstance().persistUserDatabase();
            }
        }

        @Override // com.nuance.swypeconnect.ac.ACDLMSyncService.ACDLMSyncCallback
        public void backupOccurred(int i, int i1) {
        }

        @Override // com.nuance.swypeconnect.ac.ACDLMSyncService.ACDLMSyncCallback
        public void restoreOccurred(int i, int i1) {
            Connect.log.d("restoreOccurred");
            persistUserDatabase();
        }

        @Override // com.nuance.swypeconnect.ac.ACDLMSyncService.ACDLMSyncCallback
        public void sentEvents(int i, int i1) {
        }

        @Override // com.nuance.swypeconnect.ac.ACDLMSyncService.ACDLMSyncCallback
        public void receivedEvents(int i, int i1) {
            Connect.log.d("restoreOccurred");
            persistUserDatabase();
        }

        @Override // com.nuance.swypeconnect.ac.ACDLMSyncService.ACDLMSyncCallback
        public void onError(int i, String s) {
        }
    };

    /* loaded from: classes.dex */
    public interface LegalDocumentPresenter {
        void accepted();

        boolean presentLegalRequirements(boolean z, boolean z2, Runnable runnable);
    }

    /* loaded from: classes.dex */
    public class LivingLanguage {
        private LegalDocumentPresenter presenter;

        LivingLanguage(LegalDocumentPresenter presenter) {
            this.presenter = presenter;
        }

        public boolean isEnabled() {
            if (Connect.this.livingLanguageService == null) {
                Connect.this.getLivingLanguageService(Connect.this.acManager);
            }
            return Connect.this.livingLanguageService != null && Connect.this.livingLanguageService.isLivingLanguageEnabled();
        }

        public void enable() {
            boolean legalAccepted = Connect.this.getLegal().isOptInAccepted() && Connect.this.getLegal().isTosAccepted();
            if (Connect.this.livingLanguageService == null || !legalAccepted) {
                if (Connect.this.getLegal().isTosAccepted() && Connect.this.getLegal().isOptInAccepted()) {
                    Connect.this.getLivingLanguageService(Connect.this.acManager);
                } else {
                    if (this.presenter == null) {
                        Connect.log.d("Could not enable living language because the", "TOS / Opt-In could not be accepted");
                        return;
                    }
                    this.presenter.presentLegalRequirements(true, true, new Runnable() { // from class: com.nuance.swype.connect.Connect.LivingLanguage.1
                        @Override // java.lang.Runnable
                        public void run() {
                            Connect.log.d("running enable");
                            LivingLanguage.this.enable();
                        }
                    });
                }
            }
            if (Connect.this.livingLanguageService != null && legalAccepted) {
                try {
                    StatisticsManager.SessionStatsScribe sessionScribe = StatisticsManager.getSessionStatsScribe(Connect.this.context);
                    if (sessionScribe != null && !isEnabled()) {
                        sessionScribe.recordSettingsChange("Living Language", true, false);
                    }
                    Connect.this.livingLanguageService.enableLivingLanguage();
                } catch (ACException e) {
                    Connect.log.e("Error enabling living language: " + e.getMessage());
                }
            }
        }

        public void disable() {
            if (Connect.this.livingLanguageService != null) {
                StatisticsManager.SessionStatsScribe sessionScribe = StatisticsManager.getSessionStatsScribe(Connect.this.context);
                if (sessionScribe != null && isEnabled()) {
                    sessionScribe.recordSettingsChange("Living Language", false, true);
                }
                Connect.this.livingLanguageService.disableLivingLanguage();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class DefaultSyncCallback implements ACAccountService.ACAccountCallback {
        private final Context context;
        private NotificationCompat.Builder notificationBuilder = null;
        private int NOTIFICATION_ID = 55523121;

        DefaultSyncCallback(Context context) {
            this.context = context;
            Connect.this.notificationManager = (NotificationManager) context.getSystemService("notification");
        }

        private void displayNotification(Intent i, int titleId, int messageId) {
            if (this.notificationBuilder != null) {
                Connect.this.notificationManager.cancel(this.NOTIFICATION_ID);
            }
            Resources res = Connect.this.getContext().getApplicationContext().getResources();
            if (i == null) {
                i = new Intent();
            }
            Bundle bundle = new Bundle();
            bundle.putInt(ConnectAccountDispatch.PASS_ACTIVATION_CODE_KEY, 100);
            i.putExtras(bundle);
            if (res != null) {
                this.notificationBuilder = new NotificationCompat.Builder(Connect.this.getContext());
                this.notificationBuilder.setContentTitle(res.getString(titleId)).setContentText(res.getString(messageId)).setSmallIcon(R.drawable.swype_icon).setAutoCancel(true);
                PendingIntent resultPendingIntent = PendingIntent.getActivity(this.context, 0, i, 134217728);
                this.notificationBuilder.setContentIntent(resultPendingIntent);
                Connect.this.notificationManager.notify(this.NOTIFICATION_ID, this.notificationBuilder.build());
            }
        }

        @Override // com.nuance.swypeconnect.ac.ACAccountService.ACAccountCallback
        public void created() {
            Intent i = new Intent(this.context, (Class<?>) ConnectAccountDispatch.class).setFlags(268435456);
            i.setAction(ConnectAccountDispatch.DISPLAY_ACTIVATION_CODE);
            displayNotification(i, R.string.pref_connect_activation_code, R.string.notification_activation_code_description);
            Connect.this.dismissAccountAlarm();
        }

        @Override // com.nuance.swypeconnect.ac.ACAccountService.ACAccountCallback
        public void linked() {
            Connect.log.d("Default Sync to on");
            Sync s = Connect.from(this.context).getSync();
            if (s.isAvailable()) {
                s.enable();
            }
            Connect.this.unregisterDefaultSyncCallback();
            displayNotification(null, R.string.notification_account_created_title, R.string.notification_account_created_description);
        }

        @Override // com.nuance.swypeconnect.ac.ACAccountService.ACAccountCallback
        public void verifyFailed() {
        }

        @Override // com.nuance.swypeconnect.ac.ACAccountService.ACAccountCallback
        public void onError(int type, String message) {
        }

        @Override // com.nuance.swypeconnect.ac.ACAccountService.ACAccountCallback
        public void status(int type, String message) {
        }

        @Override // com.nuance.swypeconnect.ac.ACAccountService.ACAccountCallback
        public void devicesUpdated(ACDevice[] devices) {
        }
    }

    /* loaded from: classes.dex */
    public class Accounts {
        public Accounts() {
        }

        public ACAccount getActiveAccount() {
            Connect.this.getAccountService();
            if (Connect.this.accountService != null) {
                return Connect.this.accountService.getAccount();
            }
            return null;
        }

        public void registerCallback(ACAccountService.ACAccountCallback accountCallback) {
            Connect.this.getAccountService();
            if (Connect.this.accountService != null) {
                Connect.this.accountService.registerCallback(accountCallback);
            }
        }

        public void unregisterCallback(ACAccountService.ACAccountCallback accountCallback) {
            Connect.this.getAccountService();
            if (Connect.this.accountService != null) {
                Connect.this.accountService.unregisterCallback(accountCallback);
            }
        }

        public boolean verifyAccount(String code) {
            Connect.this.getAccountService();
            boolean processed = false;
            try {
                if (Connect.this.accountService != null) {
                    Connect.this.registerDefaultSyncCallback();
                    Connect.this.accountService.verifyAccount(code);
                    processed = true;
                }
                return processed;
            } catch (ACAccountService.ACAccountException e) {
                Connect.log.e("Exception while verifying account. ", e);
                return false;
            }
        }

        public boolean isValidEmail(String email) {
            Pattern emailPattern = Patterns.EMAIL_ADDRESS;
            return email != null && emailPattern.matcher(email).matches();
        }

        public boolean createAccount(String email, boolean isTablet, String name, boolean enableSync) {
            Connect.this.getAccountService();
            if (Connect.this.accountService == null) {
                return true;
            }
            try {
                Connect.this.accountService.createAccount(1, email, isTablet ? ACDevice.ACDeviceType.TABLET : ACDevice.ACDeviceType.PHONE, name);
                Connect.this.registerDefaultSyncCallback();
                return true;
            } catch (ACAccountService.ACAccountException e) {
                return false;
            }
        }

        public ACDevice[] getDevices() {
            Connect.this.getAccountService();
            if (Connect.this.accountService != null) {
                return Connect.this.accountService.getDevices();
            }
            return null;
        }

        public void deleteActiveAccount(boolean deleteData) {
            Connect.this.getAccountService();
            if (Connect.this.accountService != null) {
                try {
                    Connect.this.accountService.deleteAccount(deleteData);
                } catch (ACAccountService.ACAccountException e) {
                    Connect.log.e("Error deleting the account: " + e.getMessage());
                }
            }
        }

        public void unlinkDevice(ACDevice device) {
            Connect.this.getAccountService();
            if (Connect.this.accountService != null) {
                try {
                    Connect.this.accountService.removeDeviceFromAccount(device);
                } catch (ACAccountService.ACAccountException e) {
                    Connect.log.e("Error removing a device from the account: " + e.getMessage());
                }
            }
        }

        public void refreshDevicesList() {
            Connect.this.getAccountService();
            if (Connect.this.accountService != null) {
                Connect.this.accountService.refreshDeviceList();
            }
        }

        public void reverify() {
            Connect.this.getAccountService();
            if (Connect.this.accountService != null) {
                try {
                    Connect.this.accountService.reverify();
                } catch (ACAccountService.ACAccountException e) {
                    Connect.log.e("Error on reverify: " + e.getMessage());
                }
            }
        }

        public boolean checkAccountExists() {
            boolean exists = getActiveAccount() != null;
            if (!exists) {
                PersistentDataStore store = DataStoreFactory.getDataStore(Connect.this.context, "com.nuance.swype.connect.store.FilePreference", ACAlphaInput.acAlphaLegacySecretKey());
                if (store.exists(Connect.ACCOUNT_PREF_OLD) || store.exists(Connect.ACCOUNT_PREF)) {
                    return true;
                }
                return exists;
            }
            return exists;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void registerDefaultSyncCallback() {
        if (this.defaultSyncCallback == null) {
            this.defaultSyncCallback = new DefaultSyncCallback(getContext());
            getAccounts().registerCallback(this.defaultSyncCallback);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void unregisterDefaultSyncCallback() {
        if (this.defaultSyncCallback != null) {
            getAccounts().unregisterCallback(this.defaultSyncCallback);
            this.defaultSyncCallback = null;
        }
    }

    /* loaded from: classes.dex */
    public class Sync {
        private LegalDocumentPresenter mPresenter;

        public Sync(LegalDocumentPresenter presenter) {
            this.mPresenter = presenter;
        }

        public void registerCallback(ACDLMSyncService.ACDLMSyncCallback callback) {
            if (Connect.this.syncService != null) {
                Connect.this.syncService.registerCallback(callback);
            }
        }

        public void unregisterCallback(ACDLMSyncService.ACDLMSyncCallback callback) {
            if (Connect.this.syncService != null) {
                Connect.this.syncService.unregisterCallback(callback);
            }
        }

        public void doSync() {
            if (Connect.this.syncService != null) {
                try {
                    Connect.this.syncService.sync();
                } catch (ACException e) {
                    Connect.log.e("Error doing a sync: " + e.getMessage());
                }
            }
        }

        public void enable() {
            Connect.log.d("Sync enable()...");
            boolean legalAccepted = Connect.this.getLegal().isOptInAccepted() && Connect.this.getLegal().isTosAccepted();
            if (Connect.this.syncService == null || !legalAccepted) {
                if (legalAccepted) {
                    Connect.this.getDlmSyncService();
                } else {
                    if (this.mPresenter == null) {
                        Connect.log.d("Could not enable Backup & Sync because the ", "TOS / Opt-In could not be accepted");
                        return;
                    }
                    this.mPresenter.presentLegalRequirements(true, true, new Runnable() { // from class: com.nuance.swype.connect.Connect.Sync.1
                        @Override // java.lang.Runnable
                        public void run() {
                            Connect.log.d("running enable");
                            Sync.this.enable();
                        }
                    });
                }
            }
            if (Connect.this.syncService != null) {
                try {
                    Connect.this.syncService.enableSync();
                    Connect.this.setContributeUsageData(true);
                } catch (ACException e) {
                    Connect.log.e("Error trying to enable backup and sync: " + e.getMessage());
                }
            }
        }

        public void disable() {
            if (Connect.this.syncService != null) {
                Connect.this.syncService.disableSync();
            }
        }

        public boolean isEnabled() {
            return Connect.this.syncService != null && Connect.this.syncService.isSyncEnabled();
        }

        public boolean isAvailable() {
            return UserPreferences.from(Connect.this.context).isConnectDLMBackupAllowed();
        }

        public void requestBackup(int coreId) {
            if (Connect.this.syncService != null) {
                try {
                    Connect.this.syncService.requestBackup(coreId);
                } catch (ACException e) {
                    Connect.log.e("Error attempting a backup: " + e.getMessage());
                }
            }
        }

        public void requestRestore(int coreId) {
            if (Connect.this.syncService != null) {
                try {
                    Connect.this.syncService.requestRestore(coreId);
                } catch (ACException e) {
                    Connect.log.e("Error attempting a restore: " + e.getMessage());
                }
            }
        }

        public void upgrade() {
            if (UserPreferences.from(Connect.this.context).isConnectDLMBackupAllowed()) {
                Connect.log.d("Sync.upgrade() enabling sync");
                enable();
            } else {
                Connect.log.d("Sync.upgrade() disabling sync");
                disable();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void start() {
        if (!isExistFilesDir()) {
            log.e("Get files dir error!");
            return;
        }
        if (UserManagerCompat.isUserUnlocked(this.context)) {
            UserPreferences prefs = UserPreferences.from(this.context);
            boolean isEnableConnectForNetworkAccess = true;
            if (this.context.getResources().getBoolean(R.bool.enable_china_connect_special) && !prefs.getNetworkAgreement()) {
                isEnableConnectForNetworkAccess = false;
            }
            if (isEnableConnectForNetworkAccess) {
                BuildInfo info = BuildInfo.from(this.context);
                if (info.isTrialBuild()) {
                    info.updateExpirationPeriod();
                }
                if (!isLicenseFileValid() || IMEApplication.from(this.context).isTrialExpired() || !info.isConnectEnabled()) {
                    if (isStarted()) {
                        stop();
                    }
                    log.d("license file is not valid; disabling sdk service");
                    return;
                }
                migrateChineseDictionaryData();
                this.maxConnectionsCellular = info.getMaxConnectionsCellular();
                this.maxConnectionsWifi = info.getMaxConnectionsWifi();
                if (!this.acManager.isConnectStarted()) {
                    try {
                        log.v("acManager.start()");
                        this.acManager.start();
                        if (this.initialLanguage != null) {
                            this.language.setActiveLanguages(this.initialLanguage);
                            this.initialLanguage = null;
                        }
                        setupACListeners(this.acManager);
                        setupSupportedLanguages(this.acManager);
                        this.connection = new ConnectedStatus(this.context) { // from class: com.nuance.swype.connect.Connect.4
                            @Override // com.nuance.swype.connect.ConnectedStatus
                            public void onInitialized() {
                                super.onInitialized();
                                Connect.this.doUpgrade();
                            }

                            @Override // com.nuance.swype.connect.ConnectedStatus
                            public void onForegroundConnection(int type) {
                                super.onForegroundConnection(type);
                                switch (type) {
                                    case 0:
                                        try {
                                            Connect.log.d("Updating connection limit for type MOBILE to ", Integer.valueOf(Connect.this.maxConnectionsCellular));
                                            Connect.this.acManager.getConfiguration().setConcurrentConnectionLimit(Connect.this.maxConnectionsCellular);
                                            return;
                                        } catch (ACException e) {
                                            Connect.log.e("Issue changing the MOBILE connection limit with error " + e.getMessage());
                                            e.printStackTrace();
                                            return;
                                        }
                                    case 1:
                                        try {
                                            Connect.log.d("Updating connection limit for type WIFI to ", Integer.valueOf(Connect.this.maxConnectionsWifi));
                                            Connect.this.acManager.getConfiguration().setConcurrentConnectionLimit(Connect.this.maxConnectionsWifi);
                                            return;
                                        } catch (ACException e2) {
                                            Connect.log.e("Issue changing the WIFI connection limit with error " + e2.getMessage());
                                            e2.printStackTrace();
                                            return;
                                        }
                                    default:
                                        return;
                                }
                            }

                            @Override // com.nuance.swype.connect.ConnectedStatus
                            public void onConnectionStatus(int status, String message) {
                                super.onConnectionStatus(status, message);
                                switch (status) {
                                    case 14:
                                        Connect.this.isLicensed = false;
                                        return;
                                    case 15:
                                        Connect.this.isLicensed = true;
                                        return;
                                    default:
                                        return;
                                }
                            }
                        };
                        this.connection.register();
                        if (this.connection.isInitialized()) {
                            doUpgrade();
                        }
                    } catch (ACException e) {
                        log.e("Failed to start service: " + e.getMessage());
                    }
                }
                this.created = true;
                this.queue.sendEmptyMessage(101);
                determineAccountAlarm();
            }
        }
    }

    public void stop() {
        if (this.acManager != null && this.acManager.isConnectStarted()) {
            if (this.connection != null) {
                this.connection.unregister();
            }
            if (this.syncService != null) {
                this.syncService.unregisterCallback(this.acDLMSyncCallback);
            }
            this.acManager.shutdown();
            this.created = false;
            this.queue.removeCallbacksAndMessages(null);
        }
    }

    public Connect(Context context) {
        this.context = context;
        UserPreferences userPrefs = UserPreferences.from(context);
        this.isStaticsCollectionEnabled = userPrefs.isStatisticsCollectionEnabled();
        this.isDataUsageOptInAccepted = userPrefs.isDataUsageOptAccepted();
        this.isUsageCollectionEnabled = userPrefs.isUsageCollectionEnabled();
        if (UserManagerCompat.isUserUnlocked(context)) {
            this.creatorThread.start();
        }
    }

    public static Connect from(Context context) {
        return IMEApplication.from(context).getConnect();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Context getContext() {
        return this.context;
    }

    @TargetApi(24)
    public void startCreatorThread() {
        if (!this.creatorThread.isAlive()) {
            log.d("startCreatorThread...");
            this.creatorThread.start();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ACManager getSDKManager() {
        if (this.created || !UserManagerCompat.isUserUnlocked(this.context) || !IMEApplication.from(this.context).isUserUnlockFinished()) {
            return this.acManager;
        }
        try {
            this.creatorLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.queue.removeCallbacksAndMessages(null);
        start();
        return this.acManager;
    }

    public void doPostStart(Runnable r) {
        if (this.created) {
            r.run();
        } else {
            this.postStartEvents.add(r);
        }
    }

    public void clearPostStartEvents() {
        this.queue.removeMessages(101);
        this.postStartEvents.clear();
    }

    public boolean isStarted() {
        return this.created && getSDKManager().isConnectStarted();
    }

    public boolean isCreated() {
        return this.created;
    }

    public boolean isForegroundWifiConnected() {
        return this.wifiConnected;
    }

    public boolean isForegroundCellularConnected() {
        return this.cellularConnected;
    }

    public ConnectLegal getLegal() {
        if (this.connectLegal == null) {
            this.connectLegal = new ConnectLegal(this, new ConnectLegal.DocumentAcceptedCallback() { // from class: com.nuance.swype.connect.Connect.5
                @Override // com.nuance.swype.connect.ConnectLegal.DocumentAcceptedCallback
                public void documentAccepted(int documentType) {
                    switch (documentType) {
                        case 1:
                            if (!Connect.this.getLegal().isEulaAccepted()) {
                                Connect.this.postTosTasks();
                            }
                            Connect.this.enableCatalogService();
                            return;
                        case 2:
                            if (!Connect.this.getLegal().isTosAccepted()) {
                                Connect.this.postTosTasks();
                                return;
                            }
                            return;
                        case 3:
                        default:
                            return;
                        case 4:
                            Connect.this.setContributeUsageData(true);
                            Connect.this.enableChineseCloudAll();
                            return;
                    }
                }
            });
        }
        return this.connectLegal;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void enableChineseCloudAll() {
        if (IMEApplication.from(this.context).getBuildInfo().isGooglePlayChina() && ConnectLegal.from(getContext()).isTosAccepted() && ConnectLegal.from(getContext()).isOptInAccepted()) {
            AppPreferences.from(getContext()).setChineseCloudNetworkOption(AppPreferences.CHINESE_CLOUD_ALL);
        }
    }

    public void enableCellularData(boolean enabled) {
        if (this.acManager != null) {
            ACConfiguration config = this.acManager.getConfiguration();
            try {
                log.d("backgroundData(true,", Boolean.valueOf(enabled), ", enabled)");
                config.backgroundData(true, enabled, enabled);
                config.foregroundData(true, enabled, enabled);
                log.d("foregroundData(true,", Boolean.valueOf(enabled), ", ", Boolean.valueOf(enabled), ")");
            } catch (ACException e) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void postTosTasks() {
        setupACListeners(getSDKManager());
        setupSupportedLanguages(getSDKManager());
        enableChineseCloudAll();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void enableCatalogService() {
        UsecaseStopwatch.getInstance().start(Usecases.ENABLE_CATALOG_SERVICE);
        CatalogManager cm = IMEApplication.from(getContext()).getCatalogManager();
        if (cm != null) {
            cm.setUpService();
        }
    }

    private boolean isLicenseFileValid() {
        log.d("isLicenseFileValid()");
        if (this.licenseFileValid == null) {
            License currentLicense = License.getLicense(this.context);
            LogManager.Log log2 = log;
            Object[] objArr = new Object[6];
            objArr[0] = "currentLicense ";
            objArr[1] = currentLicense;
            objArr[2] = " currentLicense.isValid(context) ";
            objArr[3] = Boolean.valueOf(currentLicense != null && currentLicense.isValid(this.context));
            objArr[4] = " currentLicense.isDisabled() ";
            objArr[5] = Boolean.valueOf(currentLicense != null && currentLicense.isDisabled());
            log2.d(objArr);
            if (currentLicense != null && (!currentLicense.isValid(this.context) || currentLicense.isDisabled())) {
                this.licenseFileValid = Boolean.FALSE;
            } else {
                this.licenseFileValid = Boolean.TRUE;
            }
        }
        return this.licenseFileValid.booleanValue();
    }

    public ACDLMConnector getDLMConnector() {
        try {
            ACDLMConnector dlmConnector = getSDKManager().getDLMConnector();
            return dlmConnector;
        } catch (Exception e) {
            log.e("getDLMConnector error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public ACChineseDictionaryDownloadService getChineseDictionaryDownloadService() {
        if (this.chineseService != null) {
            return this.chineseService;
        }
        if (!IMEApplication.from(this.context).isUserUnlockFinished() || !isStarted()) {
            log.w("Connect not available");
            return null;
        }
        try {
            ACManager manager = getSDKManager();
            this.chineseService = (ACChineseDictionaryDownloadService) manager.getService(ACManager.CHINESE_DICTIONARY_SERVICE);
            this.chineseService.registerCallback(this.chineseListCallback, this.chineseService.isDictionaryListAvailable());
        } catch (ACException e) {
            log.e("Error getting the chinese dictionary download service: " + e.getMessage());
        }
        return this.chineseService;
    }

    public ACChinesePredictionService getChinesePredictionService() {
        if (this.chinesePredictionService != null) {
            return this.chinesePredictionService;
        }
        if (!IMEApplication.from(this.context).isUserUnlockFinished()) {
            return null;
        }
        try {
            ACManager manager = getSDKManager();
            this.chinesePredictionService = (ACChinesePredictionService) manager.getService(ACManager.CHINESE_CLOUD_PREDICTION);
        } catch (ACException e) {
            log.e("Error getting the chinese cloud prediction service: " + e.getMessage());
        }
        return this.chinesePredictionService;
    }

    public ACReportingService getReportingService() {
        if (this.reportingService != null) {
            return this.reportingService;
        }
        if (!IMEApplication.from(this.context).isUserUnlockFinished()) {
            return null;
        }
        ConnectLegal legal = ConnectLegal.from(this.context);
        if (!this.created || !legal.isTosAccepted() || !legal.isOptInAccepted()) {
            return null;
        }
        try {
            ACManager manager = getSDKManager();
            this.reportingService = (ACReportingService) manager.getService(ACManager.REPORTING_SERVICE);
            updateReportingConfiguration();
        } catch (ACException e) {
            log.e("Error getting the reporting service: " + e.getMessage());
        }
        return this.reportingService;
    }

    private void updateReportingConfiguration() {
        if (this.reportingService != null) {
            if (ConnectLegal.from(this.context).isOptInAccepted() && this.isDataUsageOptInAccepted) {
                this.reportingService.enableReporting();
                return;
            } else {
                this.reportingService.disableReporting();
                return;
            }
        }
        log.d("updateReportingConfiguration: reportingService is null!");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public ACLivingLanguageService getLivingLanguageService(ACManager manager) {
        if (this.livingLanguageService != null) {
            return this.livingLanguageService;
        }
        if (manager == null || !manager.isConnectStarted()) {
            log.e("Connect not available");
            return null;
        }
        try {
            this.livingLanguageService = (ACLivingLanguageService) manager.getService(ACManager.LIVING_LANGUAGE_SERVICE);
            if (this.livingLanguageService != null) {
                this.livingLanguageService.registerCallback(this.livingLanguageCallback);
            }
        } catch (ACException e) {
            if (e.getReasonCode() != 104 && e.getReasonCode() != 126) {
                log.e("Error getting living language service: " + e.getMessage());
            }
        }
        return this.livingLanguageService;
    }

    public LivingLanguage getLivingLanguage(LegalDocumentPresenter presenter) {
        return new LivingLanguage(presenter);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public ACAccountService getAccountService() {
        log.d("getAccountService() ", this.accountService);
        if (this.accountService != null) {
            return this.accountService;
        }
        if (!IMEApplication.from(this.context).isUserUnlockFinished() || !isStarted()) {
            log.e("Connect not available");
            return null;
        }
        try {
            this.accountService = (ACAccountService) getSDKManager().getService(ACManager.ACCOUNT_SERVICE);
            log.d("getAccountService: ", this.accountService);
        } catch (ACException e) {
            if (e.getReasonCode() != 104) {
                log.e("Error getting account service: " + e.getMessage());
            }
        }
        return this.accountService;
    }

    private void determineAccountAlarm() {
        if (this.context.getResources().getBoolean(R.bool.enable_backup_and_sync)) {
            AppPreferences prefs = IMEApplication.from(this.context).getAppPreferences();
            if (prefs.getAccountNotificationPending()) {
                long time = prefs.getAccountNotificationShowAt();
                if (time == Long.MIN_VALUE) {
                    Calendar cal = Calendar.getInstance();
                    BuildInfo info = BuildInfo.from(this.context);
                    cal.add(5, info.getAccountNotificationInDays());
                    time = cal.getTimeInMillis();
                    prefs.setAccountNotificationShowAt(time);
                }
                ((AlarmManager) this.context.getSystemService("alarm")).set(0, time, getAccountAlarmIntent());
            }
        }
    }

    public void dismissAccountAlarm() {
        IMEApplication.from(this.context).getAppPreferences().setAccountNotificationPending(false);
        ((AlarmManager) this.context.getSystemService("alarm")).cancel(getAccountAlarmIntent());
    }

    private PendingIntent getAccountAlarmIntent() {
        Intent intent = new Intent(this.context, (Class<?>) AccountCreationBroadcastReciever.class);
        intent.setAction(AccountCreationBroadcastReciever.ACTION_NOTIFICATION);
        return PendingIntent.getBroadcast(this.context, 192837, intent, 134217728);
    }

    public Accounts getAccounts() {
        getAccountService();
        return new Accounts();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public ACDLMSyncService getDlmSyncService() {
        if (this.syncService != null) {
            return this.syncService;
        }
        boolean legalAccepted = getLegal().isOptInAccepted() && getLegal().isTosAccepted();
        if (!IMEApplication.from(this.context).isUserUnlockFinished() || !isStarted()) {
            log.e("Connect not available");
            return null;
        }
        if (!legalAccepted) {
            log.e("legal is not accepted.");
            return null;
        }
        try {
            this.syncService = (ACDLMSyncService) this.acManager.getService(ACManager.DLM_SYNC_SERVICE);
            this.syncService.registerCallback(this.acDLMSyncCallback);
            return this.syncService;
        } catch (ACException e) {
            log.e("getSyncService() " + e.getMessage());
            return null;
        }
    }

    public Sync getSync() {
        getDlmSyncService();
        return new Sync(null);
    }

    public Sync getSync(LegalDocumentPresenter presenter) {
        getDlmSyncService();
        return new Sync(presenter);
    }

    private void setupACListeners(ACManager manager) {
        if (getLivingLanguageService(manager) != null) {
            this.livingLanguageService.registerCallback(this.livingLanguageCallback);
        }
        try {
            this.chineseService = (ACChineseDictionaryDownloadService) manager.getService(ACManager.CHINESE_DICTIONARY_SERVICE);
            this.chineseService.registerCallback(this.chineseListCallback, true);
        } catch (ACException e) {
        } catch (NullPointerException e2) {
        }
    }

    protected void setupSupportedLanguages(final ACManager manager) {
        getDownloadManager();
        new AsyncTask<InputMethods, Void, Boolean>() { // from class: com.nuance.swype.connect.Connect.8
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public Boolean doInBackground(InputMethods... params) {
                LogManager.Trace unused = Connect.trace;
                ACLanguageDownloadService languageDownload = null;
                InputMethods inputMethods = params[0];
                try {
                    languageDownload = (ACLanguageDownloadService) manager.getService(ACManager.LANGUAGE_SERVICE);
                } catch (ACException e) {
                } catch (NullPointerException e2) {
                }
                SDKDownloadManager sdkDownloadManager = Connect.this.getDownloadManager();
                Map<String, List<DatabaseConfig.LanguageDB>> dbTable = DatabaseConfig.getLanguageDBList(Connect.this.context, null);
                LanguageList langList = new LanguageList(Connect.this.context);
                List<String> builtInLanguages = langList.getBuiltinLanguageList();
                List<String> allLanguages = langList.getLanguageList();
                List<String> firstProcessedLanguages = new ArrayList<>();
                Map<String, String> deprecatedLanguages = langList.getInstalledDeprecatedLanguages();
                if (allLanguages != null && allLanguages.size() > 0) {
                    Connect.log.d("processing downloaded languages");
                    SparseArray<String> oldFileMappings = DatabaseConfig.getOldLanguageIdAndFileMappingTable(Connect.this.context);
                    if (oldFileMappings == null || oldFileMappings.size() == 0) {
                        Connect.log.e("oldFileMappings: is empty");
                    }
                    for (String language : allLanguages) {
                        Connect.log.d("setupSupportedLanguages: installedLanguageName: ", language);
                        boolean isDeprecatedLang = deprecatedLanguages.containsKey(language);
                        InputMethods.Language lang = inputMethods.findLanguageFromName(language);
                        if (lang == null && isDeprecatedLang) {
                            Connect.log.d("processing deprecated language ");
                            lang = inputMethods.findLanguageFromName(langList.getNonDeprecatedLanguageName(language));
                        }
                        if (lang == null) {
                            Connect.log.d("unrecognized language: ", language);
                        } else {
                            int coreId = lang.getCoreLanguageId();
                            List<String> fileList = new ArrayList<>();
                            if (isDeprecatedLang) {
                                File langFile = new File(Connect.this.getContext().getFilesDir(), deprecatedLanguages.get(language));
                                if (langFile.exists() && langFile.isFile()) {
                                    fileList.add(langFile.getAbsolutePath());
                                }
                                if (fileList.size() > 0 && languageDownload != null) {
                                    Connect.log.d("updating deprecated language ", language);
                                    languageDownload.addExistingLanguage(Integer.valueOf(coreId), (String[]) fileList.toArray(new String[fileList.size()]));
                                    firstProcessedLanguages.add(language);
                                }
                            } else {
                                for (String fileName : lang.getDictionaryList()) {
                                    File langFile2 = new File(Connect.this.getContext().getFilesDir(), fileName);
                                    if (langFile2.exists() && langFile2.isFile()) {
                                        fileList.add(langFile2.getAbsolutePath());
                                    }
                                }
                                if (fileList.size() > 0) {
                                    if (builtInLanguages.contains(language)) {
                                        Connect.log.d(language, " is already updated");
                                        sdkDownloadManager.addUpdatedLanguage(lang, (String[]) fileList.toArray(new String[fileList.size()]));
                                        firstProcessedLanguages.add(language);
                                    } else {
                                        List<DatabaseConfig.LanguageDB> dbList = dbTable.get(language);
                                        boolean installedALM = false;
                                        boolean ALMLDBSupported = false;
                                        String ldbFile = "";
                                        for (String file : fileList) {
                                            for (DatabaseConfig.LanguageDB db : dbList) {
                                                if (db.isALMLDB()) {
                                                    ALMLDBSupported = true;
                                                }
                                                if (file.contains(db.getFileName())) {
                                                    ldbFile = db.getFileName();
                                                    if (db.isALMLDB()) {
                                                        installedALM = true;
                                                    }
                                                }
                                            }
                                        }
                                        if (installedALM) {
                                            Connect.log.d(language, " already installed ALM LDB");
                                            sdkDownloadManager.addInstalledLanguage(lang, (String[]) fileList.toArray(new String[fileList.size()]));
                                            if (languageDownload != null) {
                                                Connect.log.d(language, " add existing language to languageDownload");
                                                languageDownload.addExistingLanguage(Integer.valueOf(coreId), (String[]) fileList.toArray(new String[fileList.size()]));
                                                firstProcessedLanguages.add(language);
                                            }
                                        } else {
                                            if (ALMLDBSupported) {
                                                sdkDownloadManager.addBaseLineToALMUpdateLanguage(lang, new String[]{ldbFile});
                                                Connect.log.d(language, " already installed basic LDB, update is available");
                                            } else {
                                                sdkDownloadManager.addInstalledLanguage(lang, (String[]) fileList.toArray(new String[fileList.size()]));
                                                Connect.log.d(language, " already installed LDB, language doesn't support ALM LDB");
                                            }
                                            languageDownload.addExistingLanguage(Integer.valueOf(coreId), (String[]) fileList.toArray(new String[fileList.size()]));
                                            firstProcessedLanguages.add(language);
                                        }
                                    }
                                } else {
                                    String ldbFile2 = oldFileMappings.get(coreId);
                                    List<DatabaseConfig.LanguageDB> dbList2 = dbTable.get(language);
                                    if (ldbFile2 != null) {
                                        if (builtInLanguages.contains(language)) {
                                            if (sdkDownloadManager.isALMUpdateAvailable(ldbFile2, dbList2, true)) {
                                                Connect.log.d("baseline ", language, "(", ldbFile2, ")", " is available for update");
                                                sdkDownloadManager.addBaseLineToALMUpdateLanguage(lang, new String[]{ldbFile2});
                                            } else {
                                                boolean ALMLDBSupported2 = false;
                                                Iterator<DatabaseConfig.LanguageDB> it = dbList2.iterator();
                                                while (it.hasNext()) {
                                                    if (it.next().isALMLDB()) {
                                                        ALMLDBSupported2 = true;
                                                    }
                                                }
                                                String[] files = ldbFile2.endsWith(".mp3") ? new String[]{ldbFile2} : new String[]{ldbFile2, ldbFile2 + ".mp3"};
                                                if (ALMLDBSupported2) {
                                                    Connect.log.d(language, " is builtin ALM");
                                                    sdkDownloadManager.addBuiltinLanguage(lang, files);
                                                    if (languageDownload != null) {
                                                        Connect.log.d(language, " add existing language to languageDownload");
                                                        languageDownload.addExistingLanguage(Integer.valueOf(coreId), files);
                                                    }
                                                } else {
                                                    Connect.log.d(language, " is builtin baseline and only support baseline");
                                                    sdkDownloadManager.addBuiltinLanguage(lang, files);
                                                }
                                            }
                                            firstProcessedLanguages.add(language);
                                        } else {
                                            File langFile3 = new File(Connect.this.getContext().getFilesDir(), ldbFile2);
                                            if (langFile3.exists() && langFile3.isFile()) {
                                                if (sdkDownloadManager.isALMUpdateAvailable(ldbFile2, dbList2, true)) {
                                                    Connect.log.d("pre-apk baseline ", language, " is available for update");
                                                    sdkDownloadManager.addBaseLineToALMUpdateLanguage(lang, new String[]{ldbFile2});
                                                } else if (!sdkDownloadManager.isTraceLDBUpdateAvailable(ldbFile2, dbList2, true)) {
                                                    Connect.log.d("pre-apk ALM ", language, " already installed");
                                                    sdkDownloadManager.addInstalledLanguage(lang, new String[]{langFile3.getAbsolutePath()});
                                                } else {
                                                    sdkDownloadManager.addTraceLDBUpdateLanguage(lang, new String[]{ldbFile2});
                                                }
                                                firstProcessedLanguages.add(language);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                List<String> supportedLanguages = langList.getSupportedLanguageList();
                if (supportedLanguages != null && supportedLanguages.size() > 0) {
                    for (String language2 : supportedLanguages) {
                        if (!firstProcessedLanguages.contains(language2)) {
                            InputMethods.Language lang2 = inputMethods.findLanguageFromName(language2);
                            List<DatabaseConfig.LanguageDB> dbList3 = dbTable.get(language2);
                            File baselineLDBFile = null;
                            File almLDBFile = null;
                            for (DatabaseConfig.LanguageDB dbFile : dbList3) {
                                if (dbFile.isRegularLDB()) {
                                    baselineLDBFile = new File(Connect.this.getContext().getFilesDir(), dbFile.getFileName());
                                } else if (dbFile.isALMLDB()) {
                                    almLDBFile = new File(Connect.this.getContext().getFilesDir(), dbFile.getFileName());
                                }
                            }
                            Connect.log.d("process supported language: ", language2);
                            if (baselineLDBFile != null && almLDBFile != null) {
                                boolean baseIn = baselineLDBFile.exists();
                                boolean almIn = almLDBFile.exists();
                                if (baseIn) {
                                    Connect.log.d("supported ", language2, " is available for update");
                                    sdkDownloadManager.addBaseLineToALMUpdateLanguage(lang2, new String[]{almLDBFile.getAbsolutePath()});
                                } else if (almIn) {
                                    Connect.log.d("supported ", language2, " already installed");
                                    if (languageDownload != null) {
                                        int coreId2 = lang2.getCoreLanguageId();
                                        Connect.log.d(language2, " add existing language to languageDownload");
                                        languageDownload.addExistingLanguage(Integer.valueOf(coreId2), new String[]{almLDBFile.getAbsolutePath()});
                                    }
                                    sdkDownloadManager.addInstalledLanguage(lang2, new String[]{almLDBFile.getAbsolutePath()});
                                } else {
                                    Connect.log.d("supported ", language2, " is available for download");
                                    sdkDownloadManager.addInstallAvailable(lang2, new String[]{almLDBFile.getAbsolutePath()});
                                }
                            } else if (baselineLDBFile != null) {
                                Connect.log.d(language2, " add supported language for basic only languages.");
                                addSupportedLanguage(baselineLDBFile, lang2, sdkDownloadManager);
                            } else {
                                Connect.log.d(language2, " add supported language for ALM only language.");
                                addSupportedLanguage(almLDBFile, lang2, sdkDownloadManager);
                            }
                        }
                    }
                }
                LogManager.Trace unused2 = Connect.trace;
                return Boolean.TRUE;
            }

            private void addSupportedLanguage(File ldbFile, InputMethods.Language lang, SDKDownloadManager sdkDownloadManager) {
                Connect.log.d("addSupportedLanguage ", Connect.this.language, "...ldbFile..." + ldbFile.toString());
                if (ldbFile == null || !ldbFile.exists()) {
                    Connect.log.d("supported ", Connect.this.language, " is available for download");
                    sdkDownloadManager.addInstallAvailable(lang, null);
                } else {
                    Connect.log.d("supported ", Connect.this.language, " already installed");
                    sdkDownloadManager.addInstalledLanguage(lang, new String[]{ldbFile.getAbsolutePath()});
                }
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public void onPostExecute(Boolean result) {
                if (result.booleanValue()) {
                    Connect.this.getDownloadManager().languageSetupComplete();
                }
                super.onPostExecute((AnonymousClass8) result);
            }
        }.execute(InputMethods.from(this.context));
    }

    public SDKUpdateManager getUpdates() {
        if (this.updateManager == null) {
            this.updateManager = new SDKUpdateManager(this);
        }
        return this.updateManager;
    }

    public void setRunningState(int runningState) {
        log.d("setRunningState state=", Integer.valueOf(runningState));
    }

    public synchronized SDKDownloadManager getDownloadManager() {
        if (this.downloadManager == null) {
            this.downloadManager = new SDKDownloadManager(this.context, getSDKManager());
        }
        this.downloadManager.init();
        return this.downloadManager;
    }

    public SDKDictionaryDownloadManager getDictionaryDownloadManager() {
        if (this.dictionaryDownloadManager == null) {
            this.dictionaryDownloadManager = new SDKDictionaryDownloadManager(this.context, getSDKManager());
        }
        return this.dictionaryDownloadManager;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class SessionState implements ACLanguage.InputSessionState {
        private final boolean enterKeySelected;

        SessionState(boolean enterKeySelected) {
            this.enterKeySelected = enterKeySelected;
        }

        @Override // com.nuance.swypeconnect.ac.ACLanguage.InputSessionState
        public boolean getEnterKeySelected() {
            return this.enterKeySelected;
        }
    }

    public void onStartInput(EditorInfo attribute, boolean restarting) {
        log.d("onStartInput language=", this.language);
        if (this.language != null) {
            this.language.onStartInput(attribute, restarting);
        } else {
            this.imeIdle = false;
        }
    }

    public void onFinishInput(String buffer, boolean enterKeySelected) {
        log.d("onFinishInput language=", this.language, ", buffer=", buffer, " enterKeySelected=", Boolean.valueOf(enterKeySelected));
        if (this.language != null) {
            this.language.onFinishInput(UsageManager.from(this.context).getKeyboardUsageScribe().filterInputBuffer(buffer), new SessionState(enterKeySelected));
        } else {
            this.imeIdle = true;
        }
    }

    public void learnContextBuffer(String contextBuffer, boolean enterKeySelected) {
        onFinishInput(contextBuffer, enterKeySelected);
    }

    public void setCurrentLanguage(InputMethods.Language l) {
        setCurrentLanguage(l, 0);
    }

    public void setCurrentLanguage(InputMethods.Language l, int selectedFrom) {
        log.d("setCurrentLanguage(", l.mNativeLanguageName, ",", Integer.valueOf(selectedFrom), ")");
        if (l != null) {
            int[] langId = {l.getCoreLanguageId()};
            String[] possibleBilingualLangs = BilingualLanguage.getLanguageIds(l.getLanguageId());
            if (possibleBilingualLangs.length == 2) {
                int lang1 = 0;
                int lang2 = 0;
                try {
                    lang1 = Integer.parseInt(possibleBilingualLangs[0], 16);
                    lang2 = Integer.parseInt(possibleBilingualLangs[1], 16);
                } catch (NumberFormatException e) {
                }
                if (lang1 != 0 && lang2 != 0) {
                    langId = new int[]{lang1, lang2};
                }
            }
            if (this.language != null && this.acManager != null && this.acManager.isConnectStarted()) {
                this.initialLanguage = null;
                this.language.setActiveLanguages(langId, selectedFrom);
            } else {
                this.initialLanguage = langId;
            }
        }
    }

    public void doUpgrade() {
        AppPreferences prefs = IMEApplication.from(this.context).getAppPreferences();
        log.d("doUpgrade() isUpgrade: ", Boolean.valueOf(prefs.getUpgradeConnect()));
        if (prefs.getUpgradeConnect()) {
            if (UserPreferences.from(this.context).isConnectLivingLanguageAllowed_Deprecated()) {
                getLivingLanguage(null).enable();
            }
            UserPreferences.from(this.context).removeConnectLivingLanguageAllowed_Deprecated();
            getDictionaryDownloadManager().upgrade();
            getSync().upgrade();
            getUpdates().upgrade();
            prefs.setUpgradeConnect(false);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void migrateInternalAccountData() {
        AppPreferences prefs = IMEApplication.from(this.context).getAppPreferences();
        log.d("migrateInternalAccountData() upgrade: ", Boolean.valueOf(prefs.getUpgradeConnect()));
        if (prefs.getUpgradeConnect()) {
            PersistentDataStore store = DataStoreFactory.getDataStore(this.context, "com.nuance.swype.connect.store.FilePreference", ACAlphaInput.acAlphaLegacySecretKey());
            if (store.exists(ACCOUNT_PREF_OLD)) {
                log.d("Found old data format, converting to JSON");
                try {
                    HashMap<String, ConnectAccount> accounts = (HashMap) store.readObject(ACCOUNT_PREF_OLD);
                    ConnectAccount activeAccount = null;
                    Iterator<Map.Entry<String, ConnectAccount>> it = accounts.entrySet().iterator();
                    while (it.hasNext()) {
                        ConnectAccount acct = it.next().getValue();
                        if (acct.getAccountState().equals(ConnectAccount.AccountState.REGISTERED) || acct.getAccountState().equals(ConnectAccount.AccountState.VERIFIED)) {
                            activeAccount = acct;
                            break;
                        }
                    }
                    if (activeAccount != null) {
                        store.saveString(ACCOUNT_PREF, activeAccount.toJsonString());
                    }
                } catch (Exception e) {
                    log.d("Unable to convert old account data.");
                }
                store.delete(ACCOUNT_PREF_OLD);
            }
        }
    }

    private void migrateChineseDictionaryData() {
        AppPreferences prefs = IMEApplication.from(this.context).getAppPreferences();
        log.d("migrateChineseDictionaryData() upgrade: ", Boolean.valueOf(prefs.getUpgradeConnect()));
        if (prefs.getUpgradeConnect()) {
            log.d("Removing old Chinese DBs");
            InputMethods im = IMEApplication.from(this.context).getInputMethods();
            CategoryDBList cdbList = new CategoryDBList(this.context);
            for (Map.Entry<String, List<String>> entry : cdbList.getAvailableCatDbList().entrySet()) {
                InputMethods.Language lang = im.findLanguageFromName(entry.getKey());
                for (String catInfo : entry.getValue()) {
                    log.d("Removing... File=", cdbList.getFileName(catInfo), ", Id=", Integer.valueOf(cdbList.getFileId(catInfo)), ", Lang=", Integer.valueOf(lang.mCoreLanguageId));
                    cdbList.uninstallDownloadedCategoryDB(cdbList.getFileName(catInfo), cdbList.getFileId(catInfo), lang.mCoreLanguageId);
                }
            }
        }
    }

    public void setContributeUsageData(boolean enabled) {
        UserPreferences userPrefs = UserPreferences.from(this.context);
        userPrefs.setDataUsageOptStatus(enabled);
        this.isDataUsageOptInAccepted = enabled;
        userPrefs.setUsageCollectionEnabled(enabled);
        this.isUsageCollectionEnabled = enabled;
        userPrefs.setStatisticsCollectionEnabled(enabled);
        this.isStaticsCollectionEnabled = enabled;
        if (!enabled) {
            IMEApplication.from(this.context).getAppPreferences().setChineseCloudNetworkOption(AppPreferences.CHINESE_CLOUD_DISABLED);
            getLivingLanguage(null).disable();
            getSync().disable();
        }
        updateReportingConfiguration();
        this.queue.sendEmptyMessage(102);
    }

    public boolean isDataUsageOptInAccepted() {
        return this.isDataUsageOptInAccepted;
    }

    public boolean isStatisticsCollectionEnabled() {
        return this.isStaticsCollectionEnabled;
    }

    public boolean isUsageCollectionEnabled() {
        return this.isUsageCollectionEnabled;
    }

    public boolean isLicensed() {
        return this.isLicensed && isLicenseFileValid();
    }

    public void doDelayedStart() {
        if (!isStarted()) {
            start();
        }
    }

    public String getSwyperId() {
        if (this.created && isStarted()) {
            try {
                return ((ACDeviceService) this.acManager.getService(ACManager.DEVICE_SERVICE)).getSwyperId();
            } catch (ACException e) {
            }
        }
        return "";
    }

    public String getDeviceId() {
        if (this.created && isStarted()) {
            try {
                return ((ACDeviceService) this.acManager.getService(ACManager.DEVICE_SERVICE)).getDeviceId();
            } catch (ACException e) {
            }
        }
        return "";
    }

    public String getVersion() {
        return this.acManager != null ? this.acManager.getBuildInfo().getVersion() : "";
    }

    public String getOemId() {
        return this.acManager != null ? this.acManager.getBuildInfo().getOemId() : "";
    }

    public ACCatalogService getCatalogService() {
        log.d("Getting catalog service");
        if (this.catalogService != null) {
            log.d("Returning cached catalog service");
            return this.catalogService;
        }
        if (!IMEApplication.from(this.context).isUserUnlockFinished() || !isStarted()) {
            log.e("Connect not available");
            return null;
        }
        if (!getLegal().isTosAccepted()) {
            log.e("legal is not accepted.");
            return null;
        }
        if (!BuildInfo.from(this.context).isDownloadableThemesEnabled()) {
            log.d("Disable CatalogService for builds without the In-app Store");
            return null;
        }
        try {
            ACManager manager = getSDKManager();
            this.catalogService = (ACCatalogService) manager.getService(ACManager.CATALOG_SERVICE);
            log.d("Catalog service retrieved.");
            if (this.catalogService != null && getLegal().isOptInAccepted()) {
                this.catalogService.enableCatalog();
                log.d("Catalog service enabled.");
            }
        } catch (ACException e) {
            log.e("Error getting the catalog service: " + e.getMessage());
        }
        return this.catalogService;
    }

    public boolean isInitialized() {
        return this.connection != null && this.connection.isInitialized();
    }

    public void removeInvalidatedLanguage(int languageId) {
        getDownloadManager().removeLanguageFromList(languageId);
        setupSupportedLanguages(getSDKManager());
    }

    public String getISOCountry() {
        if (isStarted()) {
            try {
                ACManager.ACLocation location = this.acManager.getLocation();
                if (location != null) {
                    return location.getISOCountry();
                }
            } catch (ACException e) {
                log.e("get location failed:" + e.getMessage());
                e.printStackTrace();
            }
        }
        return "";
    }

    public ACScannerService getScannerService() {
        log.d("Getting scanner service");
        if (this.scannerService != null) {
            log.d("Returning cached scanner service");
            return this.scannerService;
        }
        if (!IMEApplication.from(this.context).isUserUnlockFinished() || !isStarted()) {
            log.e("Connect not available");
            return null;
        }
        if (!getLegal().isTosAccepted()) {
            log.e("legal is not accepted.");
            return null;
        }
        try {
            ACManager manager = getSDKManager();
            this.scannerService = (ACScannerService) manager.getService(ACManager.SCANNER_SERVICE);
        } catch (ACException e) {
            log.e("Error getting the scanner service: " + e.getMessage());
        }
        return this.scannerService;
    }

    private boolean isExistFilesDir() {
        return this.context.getFilesDir() != null;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
