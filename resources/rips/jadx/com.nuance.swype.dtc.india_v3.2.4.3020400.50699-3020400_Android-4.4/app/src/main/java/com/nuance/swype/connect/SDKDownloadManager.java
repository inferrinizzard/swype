package com.nuance.swype.connect;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
import android.util.SparseArray;
import com.nuance.android.compat.ActivityManagerCompat;
import com.nuance.android.compat.NotificationCompat;
import com.nuance.connect.common.Integers;
import com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.xmlResults.XMLResultsHandler;
import com.nuance.swype.input.DatabaseConfig;
import com.nuance.swype.input.IME;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.InputMethods;
import com.nuance.swype.input.LanguageList;
import com.nuance.swype.input.R;
import com.nuance.swype.input.settings.LanguageDispatch;
import com.nuance.swype.util.LogManager;
import com.nuance.swypeconnect.ac.ACException;
import com.nuance.swypeconnect.ac.ACLanguageDownloadService;
import com.nuance.swypeconnect.ac.ACManager;
import java.io.File;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/* loaded from: classes.dex */
public class SDKDownloadManager {
    public static final String BASELINE_ALM_INSTALL_ACTION = "com.nuance.swype.input.baseline2ALMUpgrade";
    private static final String COMPRESSED_FILE_EXTENSION = ".mp3";
    public static final String EXTRA_INSTALL_LANGUAGE_ID = "extra.install.language.id";
    public static final String LANGUAGE_UPDATE_NOTIFICATION = "language_update_notification";
    public static final String SELECT_INSTALLED_LANGUAGE = "select_installed_language";
    private final ConnectedStatus connection;
    private final Context context;
    private Map<String, InputMethods.Language> langLookup;
    private boolean languageSetupComplete;
    private Set<String> mFiles;
    private final ACManager manager;
    NotificationManager notificationManager;
    private static final LogManager.Trace trace = LogManager.getTrace();
    private static long INTERNAL_STORAGE_FREE_SPACE_THRESHOLD = 3145728;
    private static final LogManager.Log log = LogManager.getLog("SDKDownloadManager");

    @SuppressLint({"UseSparseArrays"})
    private final HashMap<Integer, DownloadableLanguage> languageDownloadList = new HashMap<>();
    private final Map<Integer, DownloadableLanguage> languageSettingList = new HashMap();
    private final SparseArray<String> coreIdToLanguageNameMap = new SparseArray<>();
    public final ArrayList<DataCallback> callbackList = new ArrayList<>();

    /* loaded from: classes.dex */
    public interface DataCallback {
        void languageUpdated(int i);

        void listUpdated();

        void onError(int i);
    }

    public SDKDownloadManager(Context context, ACManager acManager) {
        this.context = context;
        this.manager = acManager;
        this.notificationManager = (NotificationManager) context.getSystemService("notification");
        init();
        if (isLanguageListAvailable() && isLanguageSetupComplete()) {
            setupLanguages();
        }
        this.connection = new ConnectedStatus(context) { // from class: com.nuance.swype.connect.SDKDownloadManager.1
            @Override // com.nuance.swype.connect.ConnectedStatus
            public void onConnectionChanged(boolean isConnected) {
                SDKDownloadManager.log.d("onConnectionChanged(", Boolean.valueOf(isConnected), ")");
                for (DownloadableLanguage callback : SDKDownloadManager.this.languageDownloadList.values()) {
                    if (isConnected && !isStalled() && callback != null && callback.getStatus() == DownloadableLanguage.Status.STOPPED) {
                        SDKDownloadManager.log.d("Found stopped language ", callback.getLanguageDisplayName(), " request download resume.");
                        SDKDownloadManager.this.languageDownload(callback.getLanguageId());
                    }
                }
                SDKDownloadManager.log.d("onConnectionChanged...languageDownloadList: ", SDKDownloadManager.this.languageDownloadList);
                if (SDKDownloadManager.this.languageDownloadList.isEmpty() && isConnected) {
                    SDKDownloadManager.this.init();
                }
            }
        };
        this.connection.register();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean init() {
        ACLanguageDownloadService.ACLanguageListCallback callback = new ACLanguageDownloadService.ACLanguageListCallback() { // from class: com.nuance.swype.connect.SDKDownloadManager.2
            @Override // com.nuance.swypeconnect.ac.ACLanguageDownloadService.ACLanguageListCallback
            public void onLanguageListUpdate() {
                SDKDownloadManager.this.setupLanguages();
            }
        };
        boolean result = registerLanguageListCallback(callback);
        callback.onLanguageListUpdate();
        return result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void languageSetupComplete() {
        this.languageSetupComplete = true;
        if (isLanguageListAvailable()) {
            listUpdated();
        }
    }

    private void languageDownloadInternal(int languageId) {
        log.d("languageDownloadInternal(", Integer.valueOf(languageId), ")");
        synchronized (this.languageDownloadList) {
            if (!this.languageDownloadList.containsKey(Integer.valueOf(languageId))) {
                log.d("languageDownloadInternal(", Integer.valueOf(languageId), ") unknown language.");
            } else {
                DownloadableLanguage downloadableLanguage = this.languageDownloadList.get(Integer.valueOf(languageId));
                if (downloadableLanguage != null) {
                    switch (downloadableLanguage.getStatus()) {
                        case STOPPED:
                        case CANCELED:
                            log.d("languageDownloadInternal(", Integer.valueOf(languageId), ") download was stopped.  resuming. ", downloadableLanguage.getStatus());
                            downloadableLanguage.setStatus(DownloadableLanguage.Status.DOWNLOADING, true);
                            download(downloadableLanguage);
                            break;
                        case DOWNLOAD_AVAILABLE:
                            log.d("languageDownloadInternal(", Integer.valueOf(languageId), ") start new download.");
                            downloadableLanguage.setStatus(DownloadableLanguage.Status.DOWNLOADING, true);
                            download(downloadableLanguage);
                            break;
                        case UPDATE_AVAILABLE:
                            log.d("languageDownloadInternal(", Integer.valueOf(languageId), ") upgrading existing installed language.");
                            downloadableLanguage.setStatus(DownloadableLanguage.Status.DOWNLOADING, true);
                            download(downloadableLanguage);
                            break;
                    }
                }
            }
        }
    }

    public void languageDownload(int languageId) {
        log.d("languageDownload(", Integer.valueOf(languageId), ")");
        if (!this.languageSettingList.containsKey(Integer.valueOf(languageId))) {
            log.e("trying to install unknown languageId");
            return;
        }
        if (!this.languageDownloadList.containsKey(Integer.valueOf(languageId))) {
            ACLanguageDownloadService languageService = null;
            try {
                languageService = (ACLanguageDownloadService) this.manager.getService(ACManager.LANGUAGE_SERVICE);
            } catch (ACException e) {
                e.printStackTrace();
            }
            if (languageService == null) {
                return;
            } else {
                ((LocalDownloadableLanguage) this.languageSettingList.get(Integer.valueOf(languageId))).addToSupportedOrExistingLanguageServiceList(languageService);
            }
        }
        IME ime = IMEApplication.from(this.context).getIME();
        if (ime != null) {
            ime.checkLanguageUpdates(languageId, false);
        }
        languageDownloadInternal(languageId);
    }

    private void download(DownloadableLanguage language) {
        try {
            ACLanguageDownloadService languageService = (ACLanguageDownloadService) this.manager.getService(ACManager.LANGUAGE_SERVICE);
            String flavor = new LanguageList(this.context).getLanguageFlavor(language.getEnglishName());
            if (flavor != null) {
                log.d("download flavor: ", flavor);
                languageService.downloadLanguageFlavor(language.getCoreLanguageId(), flavor, language);
            } else {
                languageService.downloadLanguage(language.getCoreLanguageId(), language);
            }
        } catch (ACException e) {
            e.printStackTrace();
        }
    }

    public boolean languageCancel(int languageId, boolean removeNotification) {
        log.d("languageCancel(", Integer.valueOf(languageId), ")");
        synchronized (this.languageDownloadList) {
            DownloadableLanguage downloadLanguage = this.languageDownloadList.get(Integer.valueOf(languageId));
            if (downloadLanguage != null) {
                downloadLanguage.setStatus(DownloadableLanguage.Status.CANCELED, true);
                downloadLanguage.setProgress(0);
            }
            if (removeNotification && this.notificationManager != null) {
                this.notificationManager.cancel(languageId);
            }
            if (downloadLanguage != null) {
                try {
                    ((ACLanguageDownloadService) this.manager.getService(ACManager.LANGUAGE_SERVICE)).cancelDownload(languageId);
                    return true;
                } catch (ACException e) {
                }
            }
            return false;
        }
    }

    public boolean languageUninstall(int languageId) {
        boolean isAlreadyInDownloadingList;
        DownloadableLanguage.Status newStatus;
        DownloadableLanguage.Type newType;
        InputMethods.Language lang = getLanguageFromId(languageId);
        if (lang == null) {
            log.d("Failed to uninstall language ", Integer.valueOf(languageId), ", language not found.");
            return false;
        }
        if (!this.languageSettingList.containsKey(Integer.valueOf(languageId))) {
            log.e(languageId + " is not in the download setting list");
            return false;
        }
        LanguageInstall langInstaller = new LanguageInstall(lang.mEnglishName, this.context);
        try {
            ACLanguageDownloadService languageService = (ACLanguageDownloadService) this.manager.getService(ACManager.LANGUAGE_SERVICE);
            LocalDownloadableLanguage downloadableLanguage = (LocalDownloadableLanguage) this.languageSettingList.get(Integer.valueOf(languageId));
            synchronized (this.languageDownloadList) {
                isAlreadyInDownloadingList = this.languageDownloadList.containsKey(Integer.valueOf(languageId));
            }
            if (!isAlreadyInDownloadingList) {
                downloadableLanguage.addToSupportedOrExistingLanguageServiceList(languageService);
            }
            langInstaller.uninstall();
            languageService.languageUninstalled(languageId);
            if (downloadableLanguage.getOriginalStatus() == DownloadableLanguage.Status.UPDATED) {
                newStatus = DownloadableLanguage.Status.UPDATE_AVAILABLE;
                newType = DownloadableLanguage.Type.UPDATABLE;
            } else {
                newStatus = DownloadableLanguage.Status.DOWNLOAD_AVAILABLE;
                newType = DownloadableLanguage.Type.AVAILABLE;
            }
            downloadableLanguage.changeOriginalStatus(newStatus);
            downloadableLanguage.setType(newType);
            downloadableLanguage.setProgress(0);
            log.d("uninstall language ", Integer.valueOf(languageId), " complete");
            return true;
        } catch (Exception e) {
            log.e("Exception uninstalling: lang=" + languageId + " message: " + e.getMessage(), e);
            return false;
        }
    }

    public boolean isLanguageListAvailable() {
        try {
            ACLanguageDownloadService languageService = (ACLanguageDownloadService) this.manager.getService(ACManager.LANGUAGE_SERVICE);
            log.d("isLanguageListAvailable()", Boolean.valueOf(languageService.isLanguageListAvailable()));
            return languageService.isLanguageListAvailable();
        } catch (ACException e) {
            log.e("Exception isLanguageListAvailable get service: LANGUAGE_SERVICE message: " + e.getMessage(), e);
            return false;
        }
    }

    public boolean isLanguageSetupComplete() {
        return this.languageSetupComplete;
    }

    public Map<Integer, DownloadableLanguage> getSettingDownloadLanguageList() {
        return getSettingDownloadLanguageList(true);
    }

    public Map<Integer, DownloadableLanguage> getSettingDownloadLanguageList(boolean excludeBuiltinALMLanguages) {
        Map<Integer, DownloadableLanguage> localList;
        synchronized (this.languageSettingList) {
            localList = new HashMap<>(this.languageSettingList);
            if (excludeBuiltinALMLanguages) {
                Iterator<Map.Entry<Integer, DownloadableLanguage>> it = localList.entrySet().iterator();
                while (it.hasNext()) {
                    if (it.next().getValue().getType() == DownloadableLanguage.Type.BUILTIN) {
                        it.remove();
                    }
                }
            }
        }
        return localList;
    }

    public Map<Integer, DownloadableLanguage> getDownloadingLanguageList() {
        Map<Integer, DownloadableLanguage> map;
        synchronized (this.languageDownloadList) {
            map = (Map) this.languageDownloadList.clone();
        }
        return map;
    }

    public InputMethods.Language getLanguageFromId(int coreLanguageId) {
        if (this.langLookup == null) {
            this.langLookup = InputMethods.from(this.context).getAllLanguages();
            for (Map.Entry<String, InputMethods.Language> entry : this.langLookup.entrySet()) {
                this.coreIdToLanguageNameMap.append(entry.getValue().getCoreLanguageId(), entry.getKey());
            }
        }
        return this.langLookup.get(this.coreIdToLanguageNameMap.get(coreLanguageId));
    }

    /* loaded from: classes.dex */
    public static class DownloadableLanguage implements ACLanguageDownloadService.ACLanguageDownloadFileCallback, Comparable<DownloadableLanguage> {
        private static final int MAX_RETRIES = 3;
        private static final Comparator<InputMethods.Language> displayLanguageCompare = new Comparator<InputMethods.Language>() { // from class: com.nuance.swype.connect.SDKDownloadManager.DownloadableLanguage.1
            private final Collator c = Collator.getInstance(Locale.getDefault());

            @Override // java.util.Comparator
            public final int compare(InputMethods.Language lhs, InputMethods.Language rhs) {
                return this.c.compare(lhs.getDisplayName(), rhs.getDisplayName());
            }
        };
        private Status currentState;
        protected SDKDownloadManager downloadManager;
        private volatile boolean isAllowedCancel = true;
        private InputMethods.Language language;
        private NotificationCompat.Builder notificationBuilder;
        private Status originalStatus;
        private int progress;
        private int retryCount;
        private Type type;

        /* loaded from: classes.dex */
        public enum Status {
            DOWNLOAD_AVAILABLE,
            INSTALLED,
            UPDATE_AVAILABLE,
            UPDATED,
            DOWNLOADING,
            CANCELED,
            STOPPED,
            COMPLETE,
            DOWNLOAD_FAILED_OFFLINE
        }

        /* loaded from: classes.dex */
        public enum Type {
            AVAILABLE,
            DOWNLOADED,
            UPDATABLE,
            BUILTIN
        }

        DownloadableLanguage(InputMethods.Language language, Status status, Type type, SDKDownloadManager downloadManager) {
            this.language = language;
            this.currentState = status;
            this.originalStatus = this.currentState;
            this.type = type;
            this.downloadManager = downloadManager;
        }

        @Override // java.lang.Comparable
        public int compareTo(DownloadableLanguage another) {
            return displayLanguageCompare.compare(getLanguage(), another.getLanguage());
        }

        public String getDisplayName() {
            return this.language.getDisplayName();
        }

        public String getEnglishName() {
            return this.language.mEnglishName;
        }

        public InputMethods.Language getLanguage() {
            return this.language;
        }

        public int getCoreLanguageId() {
            return this.language.getCoreLanguageId();
        }

        public Status getOriginalStatus() {
            return this.originalStatus;
        }

        public void changeOriginalStatus(Status status) {
            this.originalStatus = status;
            setStatus(status, true);
        }

        public void setStatus(Status status, boolean force) {
            SDKDownloadManager.log.d("#### ", getDisplayName(), XMLResultsHandler.SEP_SPACE, status.name());
            if ((force || this.currentState != Status.DOWNLOADING) && this.currentState != status) {
                this.currentState = status;
                this.downloadManager.languageUpdated(getLanguageId());
            }
        }

        public Status getStatus() {
            return this.currentState;
        }

        public void setType(Type type) {
            this.type = type;
        }

        public Type getType() {
            return this.type;
        }

        public void setProgress(int progress) {
            this.progress = progress;
            this.downloadManager.languageUpdated(getLanguageId());
        }

        public int getProgress() {
            return this.progress;
        }

        public int getLanguageId() {
            return this.language.getCoreLanguageId();
        }

        public String getLanguageDisplayName() {
            return this.language.getDisplayName();
        }

        public boolean allowedCancel() {
            return this.isAllowedCancel;
        }

        public boolean showNoSpaceNotificationIfShortStorage() {
            ConnectedStatus status;
            Context ctx;
            if (this.downloadManager == null || (status = this.downloadManager.getConnectedStatus()) == null || !status.isConnected() || (ctx = getContext()) == null || ctx.getFilesDir().getFreeSpace() >= SDKDownloadManager.INTERNAL_STORAGE_FREE_SPACE_THRESHOLD) {
                return false;
            }
            setNotificationIndeterminant(R.string.error_out_of_disc_space);
            this.downloadManager.languageCancel(getLanguageId(), false);
            setStatus(Status.CANCELED, true);
            return true;
        }

        @Override // com.nuance.swypeconnect.ac.ACLanguageDownloadService.ACLanguageDownloadFileCallback
        public void downloadStopped(int reasonCode) {
            SDKDownloadManager.log.d("[SDKDownloadFileCallback]", "downloadStopped:", Integer.valueOf(reasonCode));
            if (reasonCode == 0) {
                int i = this.retryCount;
                this.retryCount = i + 1;
                if (i < 3) {
                    setStatus(Status.STOPPED, true);
                    setProgress(0);
                    if (this.downloadManager.getConnectedStatus().isConnected() && !this.downloadManager.getConnectedStatus().isStalled()) {
                        SDKDownloadManager.log.d("[SDKDownloadFileCallback]", "[", Integer.valueOf(getLanguageId()), "] ", " connection available, attempting to resume. retryCount: " + this.retryCount);
                        setNotificationIndeterminant(R.string.startup_internet_connection);
                        this.downloadManager.languageDownload(getLanguageId());
                        return;
                    }
                    return;
                }
            }
            if (!showNoSpaceNotificationIfShortStorage()) {
                getNotificationManager().cancel(getLanguageId());
                SDKDownloadManager.log.d("[SDKDownloadFileCallback]", "downloadStopped...originalStatus: ", this.originalStatus);
                setStatus(this.originalStatus, true);
                this.isAllowedCancel = true;
            }
        }

        @Override // com.nuance.swypeconnect.ac.ACLanguageDownloadService.ACLanguageDownloadFileCallback
        public void downloadStarted() {
            SDKDownloadManager.log.d("[SDKDownloadFileCallback]", "[", Integer.valueOf(this.language.mCoreLanguageId), "] ", "downloadStarted");
            setStatus(Status.DOWNLOADING, true);
            this.isAllowedCancel = true;
            initNotification();
            Resources res = getResources();
            if (res != null && this.notificationBuilder != null) {
                this.notificationBuilder.setContentText(res.getString(R.string.startup_lang_download_status));
                getNotificationManager().notify(getLanguageId(), this.notificationBuilder.build());
            }
        }

        @Override // com.nuance.swypeconnect.ac.ACLanguageDownloadService.ACLanguageDownloadFileCallback
        public void downloadPercentage(int percent) {
            SDKDownloadManager.log.d("[SDKDownloadFileCallback]", "[", Integer.valueOf(this.language.mCoreLanguageId), "] ", "downloadPercent:", Integer.valueOf(percent));
            if (percent != getProgress()) {
                setProgress(percent);
                if (!showNoSpaceNotificationIfShortStorage() && this.notificationBuilder != null && !ActivityManagerCompat.isUserAMonkey()) {
                    this.notificationBuilder.setProgress(100, percent, false);
                    getNotificationManager().notify(getLanguageId(), this.notificationBuilder.build());
                }
            }
        }

        @Override // com.nuance.swypeconnect.ac.ACLanguageDownloadService.ACLanguageDownloadFileCallback
        public void downloadFailed(int reasonCode) {
            SDKDownloadManager.log.d("[SDKDownloadFileCallback]", "[", Integer.valueOf(getLanguageId()), "] ", "downloadFailed:", Integer.valueOf(reasonCode));
            if (reasonCode == 2) {
                setStatus(this.originalStatus, true);
                setProgress(0);
                setNotificationIndeterminant(R.string.startup_internet_connection);
                this.downloadManager.notifyLanguageDownloadError(2);
            } else {
                if (reasonCode == 0) {
                    int i = this.retryCount;
                    this.retryCount = i + 1;
                    if (i < 3) {
                        setStatus(Status.STOPPED, true);
                        setProgress(0);
                        if (this.downloadManager.getConnectedStatus().isConnected() && !this.downloadManager.getConnectedStatus().isStalled()) {
                            SDKDownloadManager.log.d("[SDKDownloadFileCallback]", "[", Integer.valueOf(getLanguageId()), "] ", " connection available, attempting to resume. retryCount: " + this.retryCount);
                            setNotificationIndeterminant(R.string.startup_internet_connection);
                            this.downloadManager.languageDownload(getLanguageId());
                            return;
                        }
                    }
                }
                if (reasonCode == 0 && this.retryCount >= 3) {
                    reasonCode = 1;
                }
                SDKDownloadManager.log.d("[SDKDownloadFileCallback]", "[", Integer.valueOf(getLanguageId()), "] ", " download failed, reason code: ", Integer.valueOf(reasonCode));
                getNotificationManager().cancel(getLanguageId());
                setStatus(this.originalStatus, true);
                this.retryCount = 0;
            }
            switch (reasonCode) {
                case 0:
                case 2:
                    setNotificationIndeterminant(R.string.startup_internet_connection);
                    return;
                case 6:
                    setNotificationIndeterminant(R.string.error_out_of_disc_space);
                    return;
                default:
                    setNotificationIndeterminant(R.string.accessibility_setting_fail_language_installation);
                    return;
            }
        }

        @Override // com.nuance.swypeconnect.ac.ACLanguageDownloadService.ACLanguageDownloadFileCallback
        public boolean downloadComplete(File file) {
            setStatus(Status.COMPLETE, true);
            SDKDownloadManager.log.d("[SDKDownloadFileCallback]", "[", Integer.valueOf(this.language.mCoreLanguageId), "] ", "downloadComplete");
            this.isAllowedCancel = false;
            IMEApplication.from(getContext()).onPreInstallLanguage(this.downloadManager.getLanguageFromId(getLanguageId()).mEnglishName);
            new InstallTask(Integer.valueOf(getLanguageId()), getContext(), this.downloadManager).execute(file.getPath());
            if (getResources() != null && this.notificationBuilder != null) {
                setNotificationIndeterminant(R.string.accessibility_setting_installing);
            }
            return true;
        }

        void installed() {
            Resources res = getResources();
            Context context = getContext();
            if (this.originalStatus == Status.UPDATE_AVAILABLE) {
                SDKDownloadManager.log.d(getEnglishName(), " is upgraded");
                setStatus(Status.UPDATED, true);
            } else {
                setStatus(Status.INSTALLED, true);
            }
            this.isAllowedCancel = true;
            if (res != null && this.notificationBuilder != null && context != null) {
                Intent resultIntent = new Intent(context, (Class<?>) LanguageDispatch.class).setFlags(268435456);
                PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, 134217728);
                this.notificationBuilder.setProgress(0, 0, false).setContentText(res.getString(R.string.pref_download_language_installed)).setAutoCancel(true).setOngoing(false).setContentIntent(resultPendingIntent);
                getNotificationManager().notify(getLanguageId(), this.notificationBuilder.build());
            }
        }

        private void setNotificationIndeterminant(int context) {
            Resources res = getResources();
            if (this.notificationBuilder == null) {
                initNotification();
            }
            if (res != null && this.notificationBuilder != null) {
                this.notificationBuilder.setProgress(0, 0, false);
                if (context != Integer.MIN_VALUE) {
                    this.notificationBuilder.setContentText(res.getString(context));
                }
                this.notificationBuilder.setAutoCancel(true);
                this.notificationBuilder.setOngoing(false);
                getNotificationManager().notify(getLanguageId(), this.notificationBuilder.build());
            }
        }

        private Resources getResources() {
            if (this.downloadManager.getContext() != null) {
                return this.downloadManager.getContext().getResources();
            }
            return null;
        }

        private Context getContext() {
            return this.downloadManager.getContext();
        }

        private NotificationManager getNotificationManager() {
            return this.downloadManager.getNotificationManager();
        }

        private void initNotification() {
            Resources res = getResources();
            Context context = getContext();
            if (res != null && context != null) {
                this.notificationBuilder = new NotificationCompat.Builder(getContext());
                PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, new Intent(), 134217728);
                this.notificationBuilder.setContentTitle(getLanguageDisplayName());
                this.notificationBuilder.setContentText(res.getString(R.string.accessibility_voice_input_wait)).setSmallIcon(R.drawable.swype_icon).setProgress(0, 0, true).setContentIntent(resultPendingIntent).setOngoing(true);
            }
        }
    }

    /* loaded from: classes.dex */
    private static class InstallTask extends AsyncTask<String, Void, Integer> {
        Context context;
        final Integer languageId;
        SDKDownloadManager manager;

        InstallTask(Integer languageId, Context context, SDKDownloadManager manager) {
            this.languageId = languageId;
            this.context = context;
            this.manager = manager;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Integer doInBackground(String... params) {
            SDKDownloadManager.log.d("Install language (", this.languageId, ") beginning");
            String installLanguagePath = params[0];
            LanguageInstall langInstaller = new LanguageInstall(this.manager.getLanguageFromId(this.languageId.intValue()).mEnglishName, this.context);
            try {
                langInstaller.install(installLanguagePath);
                SDKDownloadManager.log.d("Install language (", this.languageId, ") file copy completed successfully");
                return Integer.valueOf(Integers.STATUS_SUCCESS);
            } catch (Exception e) {
                SDKDownloadManager.log.e("Exception Installing: lang=" + this.languageId + " message: " + e.getMessage(), e);
                File f = new File(installLanguagePath);
                long freeSize = this.context.getFilesDir().getFreeSpace();
                if (f.length() > freeSize || freeSize < SDKDownloadManager.INTERNAL_STORAGE_FREE_SPACE_THRESHOLD) {
                    return 6;
                }
                return 1;
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Integer result) {
            super.onPostExecute((InstallTask) result);
            if (result.intValue() == Integer.MIN_VALUE) {
                SDKDownloadManager.log.d("Install language (", this.languageId, ") begin adding to InputMethods");
                LanguageList list = new LanguageList(this.context);
                Map<String, InputMethods.Language> allLanguanges = InputMethods.from(this.context).getAllLanguages();
                InputMethods.Language installLang = null;
                for (Map.Entry<String, InputMethods.Language> entry : allLanguanges.entrySet()) {
                    if (entry.getValue().getCoreLanguageId() == this.languageId.intValue()) {
                        InputMethods.Language installLang2 = entry.getValue();
                        installLang = installLang2;
                    }
                }
                if (installLang == null) {
                    SDKDownloadManager.log.d("Language install failed, couldn't find Language for " + this.languageId);
                    this.manager.languageInstallFailed(this.languageId.intValue(), 0);
                    return;
                }
                String langName = installLang.mEnglishName;
                SDKDownloadManager.log.d("###", " LanguageID: " + langName);
                list.addDownloadedLanguage(langName);
                DatabaseConfig.updateLanguage(this.context, langName);
                IMEApplication imeApp = IMEApplication.from(this.context);
                imeApp.refreshInputMethods();
                imeApp.onUpdateLanguage(langName);
                imeApp.onPostInstallLanguage(langName);
                if (!list.getLanguageList().contains(langName)) {
                    SDKDownloadManager.log.e("Error Installing: lang=" + this.languageId);
                } else {
                    this.manager.languageInstallCompleted(this.languageId.intValue());
                    return;
                }
            } else if (result.intValue() == 6) {
                this.manager.languageInstallFailed(this.languageId.intValue(), 6);
                return;
            }
            this.manager.languageInstallFailed(this.languageId.intValue(), 0);
        }
    }

    public void registerDataCallback(DataCallback callback) {
        synchronized (this.callbackList) {
            if (!this.callbackList.contains(callback)) {
                this.callbackList.add(callback);
            }
        }
    }

    public void unregisterDataCallback(DataCallback callback) {
        synchronized (this.callbackList) {
            if (this.callbackList.contains(callback)) {
                this.callbackList.remove(callback);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void languageUpdated(int languageId) {
        List<DataCallback> callbacks;
        synchronized (this.callbackList) {
            callbacks = (ArrayList) this.callbackList.clone();
        }
        Iterator<DataCallback> it = callbacks.iterator();
        while (it.hasNext()) {
            it.next().languageUpdated(languageId);
        }
    }

    private void listUpdated() {
        List<DataCallback> callbacks;
        synchronized (this.callbackList) {
            callbacks = (ArrayList) this.callbackList.clone();
        }
        Iterator<DataCallback> it = callbacks.iterator();
        while (it.hasNext()) {
            it.next().listUpdated();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyLanguageDownloadError(int reason) {
        List<DataCallback> callbacks;
        synchronized (this.callbackList) {
            callbacks = (ArrayList) this.callbackList.clone();
        }
        Iterator<DataCallback> it = callbacks.iterator();
        while (it.hasNext()) {
            it.next().onError(reason);
        }
    }

    public boolean registerLanguageListCallback(ACLanguageDownloadService.ACLanguageListCallback callback) {
        try {
            ((ACLanguageDownloadService) this.manager.getService(ACManager.LANGUAGE_SERVICE)).registerCallback(callback);
            return true;
        } catch (ACException e) {
            return false;
        }
    }

    public void unregisterLanguageListCallback(ACLanguageDownloadService.ACLanguageListCallback callback) {
        try {
            ((ACLanguageDownloadService) this.manager.getService(ACManager.LANGUAGE_SERVICE)).unregisterCallback(callback);
        } catch (ACException e) {
        }
    }

    DownloadableLanguage.Status statusFromType(DownloadableLanguage.Type type) {
        if (type == DownloadableLanguage.Type.AVAILABLE) {
            return DownloadableLanguage.Status.DOWNLOAD_AVAILABLE;
        }
        if (type == DownloadableLanguage.Type.UPDATABLE) {
            return DownloadableLanguage.Status.UPDATE_AVAILABLE;
        }
        return DownloadableLanguage.Status.INSTALLED;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public NotificationManager getNotificationManager() {
        return this.notificationManager;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public ConnectedStatus getConnectedStatus() {
        return this.connection;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Context getContext() {
        return this.context;
    }

    public void setupLanguages() {
        log.d("setupLanguages");
        try {
            for (ACLanguageDownloadService.ACLanguageDbInfo info : ((ACLanguageDownloadService) this.manager.getService(ACManager.LANGUAGE_SERVICE)).getDatabaseList()) {
                int languageId = info.getXt9LanguageId();
                DownloadableLanguage.Type type = DownloadableLanguage.Type.AVAILABLE;
                boolean almUpdateAvailable = false;
                if (this.languageSettingList.containsKey(Integer.valueOf(languageId))) {
                    almUpdateAvailable = this.languageSettingList.get(Integer.valueOf(languageId)).getType() == DownloadableLanguage.Type.UPDATABLE && this.languageSettingList.get(Integer.valueOf(languageId)).getStatus() == DownloadableLanguage.Status.UPDATE_AVAILABLE;
                    log.d("languageId: " + languageId + " almUpdateAvailable: " + almUpdateAvailable);
                }
                if (info.isNewerVersionAvailable() || almUpdateAvailable) {
                    type = DownloadableLanguage.Type.UPDATABLE;
                } else if (info.isInstalled()) {
                    if (!info.isPreinstalled()) {
                        type = DownloadableLanguage.Type.DOWNLOADED;
                    }
                }
                if (this.languageDownloadList.containsKey(Integer.valueOf(languageId))) {
                    DownloadableLanguage dlLang = this.languageDownloadList.get(Integer.valueOf(languageId));
                    if (dlLang.getType() != type) {
                        dlLang.setType(type);
                        dlLang.setStatus(statusFromType(type), true);
                    }
                } else if (this.languageSettingList.containsKey(Integer.valueOf(languageId))) {
                    this.languageDownloadList.put(Integer.valueOf(languageId), this.languageSettingList.get(Integer.valueOf(languageId)));
                    if (type == DownloadableLanguage.Type.UPDATABLE) {
                        this.languageDownloadList.get(Integer.valueOf(languageId)).changeOriginalStatus(statusFromType(type));
                    }
                    if (type == DownloadableLanguage.Type.AVAILABLE) {
                        languageDownload(languageId);
                    }
                } else {
                    InputMethods.Language lang = getLanguageFromId(languageId);
                    if (lang != null) {
                        this.languageDownloadList.put(Integer.valueOf(languageId), new DownloadableLanguage(lang, statusFromType(type), type, this));
                    }
                }
            }
            if (isLanguageSetupComplete()) {
                listUpdated();
            }
        } catch (ACException e) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void languageInstallCompleted(int languageId) {
        DownloadableLanguage callback = this.languageDownloadList.get(Integer.valueOf(languageId));
        if (callback != null) {
            callback.installed();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void languageInstallFailed(int languageId, int reason) {
        log.d("languageInstallFailed(", Integer.valueOf(languageId), ")");
        DownloadableLanguage callback = this.languageDownloadList.get(Integer.valueOf(languageId));
        if (callback != null) {
            callback.downloadFailed(reason);
        }
    }

    private PendingIntent getPendingIntent(int languageId) {
        Intent intent = new Intent(LANGUAGE_UPDATE_NOTIFICATION);
        intent.putExtra(LanguageUpdateWithTOS.LANGUAGE_ID, languageId);
        return PendingIntent.getBroadcast(this.context.getApplicationContext(), 0, intent, 134217728);
    }

    public void postLanguageUpdateNotification(InputMethods.Language language) {
        if (Connect.from(getContext()).isLicensed() && !IMEApplication.from(this.context).isTrialExpired()) {
            int languageId = language.getCoreLanguageId();
            if (this.languageSettingList.containsKey(Integer.valueOf(languageId)) && this.languageSettingList.get(Integer.valueOf(languageId)).getStatus() == DownloadableLanguage.Status.UPDATE_AVAILABLE) {
                log.d("postLanguageUpdateNotification: ", language.mEnglishName);
                NotificationCompat.Builder notification = new NotificationCompat.Builder(this.context);
                if (Build.VERSION.SDK_INT >= 19) {
                    getPendingIntent(languageId).cancel();
                }
                Resources res = this.context.getResources();
                String title = String.format(res.getString(R.string.notification_language_update_title), language.getDisplayName());
                notification.setContentTitle(title).setContentText(res.getString(R.string.notification_language_update)).setSmallIcon(R.drawable.swype_icon).setContentIntent(getPendingIntent(languageId));
                this.notificationManager.notify(languageId, notification.build());
                IME ime = IMEApplication.from(getContext()).getIME();
                if (ime != null) {
                    ime.checkLanguageUpdates(languageId, true);
                }
            }
        }
    }

    public boolean isLanguageAvailableForUpdate(InputMethods.Language language) {
        boolean z;
        int languageId = language.getCoreLanguageId();
        if (!this.languageSetupComplete) {
            return false;
        }
        synchronized (this.languageSettingList) {
            if (this.languageSettingList.containsKey(Integer.valueOf(languageId)) && this.languageSettingList.get(Integer.valueOf(languageId)).getStatus() == DownloadableLanguage.Status.UPDATE_AVAILABLE) {
                z = true;
            } else if (this.languageSettingList.containsKey(Integer.valueOf(languageId))) {
                z = false;
            } else {
                String ldbFile = DatabaseConfig.getOldLanguageIdAndFileMappingTable(this.context).get(languageId);
                if (ldbFile == null) {
                    z = false;
                } else {
                    List<DatabaseConfig.LanguageDB> dbList = DatabaseConfig.getLanguageDBList(this.context, null).get(language.mEnglishName);
                    boolean almUpdateAvailable = isALMUpdateAvailable(ldbFile, dbList, false);
                    String filePath = this.context.getFileStreamPath(ldbFile).getPath();
                    if (!almUpdateAvailable) {
                        log.d("isLanguageAvailableForUpdate: ", ldbFile, " is already ALM");
                        z = false;
                    } else {
                        log.d("isLanguageAvailableForUpdate: filePath() ", filePath, " added to baseline to ALM update list");
                        addBaseLineToALMUpdateLanguage(language, new String[]{filePath});
                        z = true;
                    }
                }
            }
        }
        return z;
    }

    public boolean isALMUpdateAvailable(String ldbFilePath, List<DatabaseConfig.LanguageDB> dbList, boolean checkAssetOnly) {
        boolean updateAvailable = false;
        for (DatabaseConfig.LanguageDB db : dbList) {
            if (db.isALMLDB()) {
                updateAvailable = isLDBUpdateAvailable(ldbFilePath, db, checkAssetOnly);
            }
        }
        return updateAvailable;
    }

    public boolean isTraceLDBUpdateAvailable(String ldbFilePath, List<DatabaseConfig.LanguageDB> dbList, boolean checkAssetOnly) {
        boolean updateAvailable = false;
        for (DatabaseConfig.LanguageDB db : dbList) {
            if (db.isTraceLDB()) {
                updateAvailable = isLDBUpdateAvailable(ldbFilePath, db, checkAssetOnly);
            }
        }
        return updateAvailable;
    }

    public void addInstallAvailable(InputMethods.Language language, String[] filePathList) {
        if (language != null) {
            log.d("addInstallAvailable: ", language.mEnglishName);
            synchronized (this.languageSettingList) {
                if (language != null) {
                    if (!this.languageSettingList.containsKey(Integer.valueOf(language.getCoreLanguageId()))) {
                        DownloadableLanguage downloadableLanguage = new LocalDownloadableLanguage(language, DownloadableLanguage.Status.DOWNLOAD_AVAILABLE, DownloadableLanguage.Type.AVAILABLE, filePathList, this);
                        this.languageSettingList.put(Integer.valueOf(language.getCoreLanguageId()), downloadableLanguage);
                    }
                }
            }
        }
    }

    public void addInstalledLanguage(InputMethods.Language language, String[] filePathList) {
        if (language != null) {
            log.d("addInstalledLanguage: ", language.mEnglishName);
            synchronized (this.languageSettingList) {
                if (language != null) {
                    if (!this.languageSettingList.containsKey(Integer.valueOf(language.getCoreLanguageId()))) {
                        DownloadableLanguage downloadableLanguage = new LocalDownloadableLanguage(language, DownloadableLanguage.Status.INSTALLED, DownloadableLanguage.Type.DOWNLOADED, filePathList, this);
                        this.languageSettingList.put(Integer.valueOf(language.getCoreLanguageId()), downloadableLanguage);
                    }
                }
            }
        }
    }

    public void addUpdatedLanguage(InputMethods.Language language, String[] filePathList) {
        if (language != null) {
            log.d("addUpdatedLanguage: ", language.mEnglishName);
            synchronized (this.languageSettingList) {
                if (language != null) {
                    if (!this.languageSettingList.containsKey(Integer.valueOf(language.getCoreLanguageId()))) {
                        DownloadableLanguage downloadableLanguage = new LocalDownloadableLanguage(language, DownloadableLanguage.Status.UPDATED, DownloadableLanguage.Type.UPDATABLE, filePathList, this);
                        this.languageSettingList.put(Integer.valueOf(language.getCoreLanguageId()), downloadableLanguage);
                    }
                }
            }
        }
    }

    public void addBuiltinLanguage(InputMethods.Language language, String[] filePathList) {
        log.d("addBuiltinLanguage: ", language.mEnglishName);
        synchronized (this.languageSettingList) {
            if (language != null) {
                if (!this.languageSettingList.containsKey(Integer.valueOf(language.getCoreLanguageId()))) {
                    DownloadableLanguage downloadableLanguage = new LocalDownloadableLanguage(language, DownloadableLanguage.Status.INSTALLED, DownloadableLanguage.Type.BUILTIN, filePathList, this);
                    this.languageSettingList.put(Integer.valueOf(language.getCoreLanguageId()), downloadableLanguage);
                }
            }
        }
    }

    public void addBaseLineToALMUpdateLanguage(InputMethods.Language language, String[] filePathList) {
        log.d("addBaseLineToALMUpdateLanguage: ", language.mEnglishName);
        synchronized (this.languageSettingList) {
            if (language != null) {
                if (!this.languageSettingList.containsKey(Integer.valueOf(language.getCoreLanguageId()))) {
                    DownloadableLanguage downloadableLanguage = new LocalDownloadableLanguage(language, DownloadableLanguage.Status.UPDATE_AVAILABLE, DownloadableLanguage.Type.UPDATABLE, filePathList, this);
                    this.languageSettingList.put(Integer.valueOf(language.getCoreLanguageId()), downloadableLanguage);
                }
            }
        }
    }

    public void addTraceLDBUpdateLanguage(InputMethods.Language language, String[] filePathList) {
        synchronized (this.languageSettingList) {
            if (language != null) {
                if (!this.languageSettingList.containsKey(Integer.valueOf(language.getCoreLanguageId()))) {
                    DownloadableLanguage downloadableLanguage = new LocalDownloadableLanguage(language, DownloadableLanguage.Status.UPDATE_AVAILABLE, DownloadableLanguage.Type.UPDATABLE, filePathList, this);
                    this.languageSettingList.put(Integer.valueOf(language.getCoreLanguageId()), downloadableLanguage);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class LocalDownloadableLanguage extends DownloadableLanguage {
        private final String[] filePathList;

        private LocalDownloadableLanguage(InputMethods.Language language, DownloadableLanguage.Status status, DownloadableLanguage.Type type, String[] filePathList, SDKDownloadManager mgr) {
            super(language, status, type, mgr);
            this.filePathList = filePathList;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void addToSupportedOrExistingLanguageServiceList(ACLanguageDownloadService languageDownloadService) {
            if (getType() == DownloadableLanguage.Type.UPDATABLE || getStatus() == DownloadableLanguage.Status.UPDATED) {
                languageDownloadService.addExistingLanguage(Integer.valueOf(getCoreLanguageId()), this.filePathList);
                this.downloadManager.setupLanguages();
            } else if (getType() == DownloadableLanguage.Type.AVAILABLE || getStatus() == DownloadableLanguage.Status.INSTALLED) {
                languageDownloadService.addSupportedLanguage(Integer.valueOf(getLanguageId()));
                this.downloadManager.setupLanguages();
            }
        }
    }

    public void removeLanguageFromList(int languageId) {
        synchronized (this.languageSettingList) {
            this.languageSettingList.remove(Integer.valueOf(languageId));
        }
    }

    public boolean isLDBUpdateAvailable(String ldbFilePath, DatabaseConfig.LanguageDB db, boolean checkAssetOnly) {
        if (this.context != null) {
            if (DatabaseConfig.getExternalDatabasePath(this.context).length <= 0) {
                if (this.mFiles != null && (this.mFiles.contains(db.getFileName() + ".mp3") || this.mFiles.contains(db.getFileName()))) {
                    return false;
                }
            } else if (DatabaseConfig.foundFileInExternalPath(db.getFileName() + ".mp3") || DatabaseConfig.foundFileInExternalPath(db.getFileName())) {
                return false;
            }
        }
        if (checkAssetOnly) {
            boolean foundFileInExternalPath = DatabaseConfig.foundFileInExternalPath(new StringBuilder().append(db.getFileName()).append(".mp3").toString()) || DatabaseConfig.foundFileInExternalPath(db.getFileName());
            if (!ldbFilePath.contains(db.getFileName()) && !foundFileInExternalPath) {
                ldbFilePath.contains(db.getFileName());
            }
            return true;
        }
        boolean downloadedLDB = false;
        File langFile = new File(getContext().getFilesDir(), ldbFilePath);
        if (langFile.exists() && langFile.isFile()) {
            downloadedLDB = true;
        }
        return (ldbFilePath.contains(db.getFileName()) && !downloadedLDB) || !ldbFilePath.contains(db.getFileName());
    }
}
