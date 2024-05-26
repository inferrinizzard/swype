package com.nuance.swypeconnect.ac;

import com.nuance.connect.util.ConcurrentCallbackSet;
import com.nuance.swypeconnect.ac.ACLanguageDownloadService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Deprecated
/* loaded from: classes.dex */
public class ACLanguageDownloadCompatService extends ACService {
    public static final int REASON_FAILED_DISK_FULL = 6;
    public static final int REASON_FAILED_HTTP = 0;
    public static final int REASON_FAILED_MAX_RETRY = 1;
    public static final int REASON_NETWORK_TIMEOUT = 4;
    public static final int REASON_USER_CANCELED = 3;
    private final ACLanguageDownloadServiceBase languageService;
    private final ACManager manager;
    private final ConcurrentCallbackSet<ACLanguageDownloadListCallback> languageListCallbacks = new ConcurrentCallbackSet<>();
    private boolean almsAreUpgrades = true;
    private final ACLanguageDownloadService.ACLanguageListCallback listCallback = new ACLanguageDownloadService.ACLanguageListCallback() { // from class: com.nuance.swypeconnect.ac.ACLanguageDownloadCompatService.1
        List<Integer> available = new ArrayList();
        List<Integer> downloaded = new ArrayList();
        List<Integer> updatable = new ArrayList();

        boolean isListUpdated(List<Integer> list, List<Integer> list2) {
            return (!list.containsAll(list2)) || !list2.containsAll(list);
        }

        @Override // com.nuance.swypeconnect.ac.ACLanguageDownloadService.ACLanguageListCallback
        public void onLanguageListUpdate() {
            boolean z;
            boolean z2;
            boolean z3;
            if (!ACLanguageDownloadCompatService.this.isShutdown) {
                List<Integer> availableLanguages = ACLanguageDownloadCompatService.this.getAvailableLanguages();
                if (isListUpdated(this.available, availableLanguages)) {
                    this.available = new ArrayList(availableLanguages);
                    z = true;
                } else {
                    z = false;
                }
                List<Integer> downloadedLanguages = ACLanguageDownloadCompatService.this.getDownloadedLanguages();
                if (isListUpdated(this.downloaded, downloadedLanguages)) {
                    this.downloaded = new ArrayList(downloadedLanguages);
                    z2 = true;
                } else {
                    z2 = false;
                }
                List<Integer> updatableLanguages = ACLanguageDownloadCompatService.this.getUpdatableLanguages();
                if (isListUpdated(this.updatable, updatableLanguages)) {
                    this.updatable = new ArrayList(updatableLanguages);
                    z3 = true;
                } else {
                    z3 = false;
                }
                for (ACLanguageDownloadListCallback aCLanguageDownloadListCallback : (ACLanguageDownloadListCallback[]) ACLanguageDownloadCompatService.this.languageListCallbacks.toArray(new ACLanguageDownloadListCallback[0])) {
                    if (z) {
                        aCLanguageDownloadListCallback.availableLanguages(this.available);
                    }
                    if (z2) {
                        aCLanguageDownloadListCallback.downloadedLanguages(this.downloaded);
                    }
                    if (z3) {
                        aCLanguageDownloadListCallback.updatableLanguages(this.updatable);
                    }
                }
            }
            if (ACLanguageDownloadCompatService.this.manager.isConnectInitialized()) {
                return;
            }
            ACLanguageDownloadCompatService.this.manager.serviceInitialized(ACLanguageDownloadCompatService.this.getName());
        }
    };

    /* loaded from: classes.dex */
    public interface ACLanguageDownloadFileCallback extends ACLanguageDownloadService.ACLanguageDownloadFileCallback {
    }

    /* loaded from: classes.dex */
    public interface ACLanguageDownloadListCallback {
        void availableLanguages(List<Integer> list);

        void downloadedLanguages(List<Integer> list);

        void updatableLanguages(List<Integer> list);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ACLanguageDownloadCompatService(ACManager aCManager, ACLanguageDownloadServiceBase aCLanguageDownloadServiceBase) {
        this.manager = aCManager;
        this.languageService = aCLanguageDownloadServiceBase;
        aCLanguageDownloadServiceBase.registerCallback(this.listCallback);
    }

    private boolean isUpdateAvailable(ACLanguageDownloadService.ACLanguageDbInfo aCLanguageDbInfo) {
        if (!aCLanguageDbInfo.isPreinstalled()) {
            if (aCLanguageDbInfo.isDownloaded()) {
                return aCLanguageDbInfo.isNewerVersionAvailable();
            }
            return false;
        }
        if (aCLanguageDbInfo.isDownloaded() || !this.almsAreUpgrades || !aCLanguageDbInfo.isInstalled() || aCLanguageDbInfo.getEnhancedLanguageModels() == null) {
            return aCLanguageDbInfo.isNewerVersionAvailable();
        }
        return true;
    }

    public void addExistingLanguage(Integer num, String[] strArr) {
        this.languageService.addExistingLanguage(num, strArr);
        this.listCallback.onLanguageListUpdate();
    }

    public void addExistingLanguage(String str) {
        this.languageService.addExistingLanguage(str);
        this.listCallback.onLanguageListUpdate();
    }

    public void addSupportedLanguage(Integer num) {
        this.languageService.addSupportedLanguage(num);
        this.listCallback.onLanguageListUpdate();
    }

    public void addSupportedLanguages(List<Integer> list) {
        this.languageService.addSupportedLanguages(list);
        this.listCallback.onLanguageListUpdate();
    }

    public void cancelDownload(int i) throws ACException {
        this.languageService.cancelDownload(i);
    }

    public void downloadLanguage(int i, ACLanguageDownloadFileCallback aCLanguageDownloadFileCallback) throws ACException {
        this.languageService.downloadLanguage(i, aCLanguageDownloadFileCallback);
    }

    public void downloadLanguageFlavor(int i, String str, ACLanguageDownloadFileCallback aCLanguageDownloadFileCallback) throws ACException {
        if (ACLanguageDownloadService.ACLdbType.MLM.toString().equalsIgnoreCase(str)) {
            this.languageService.downloadLanguage(i, ACLanguageDownloadService.ACLdbType.MLM, aCLanguageDownloadFileCallback);
        } else if (ACLanguageDownloadService.ACLdbType.Baseline.toString().equalsIgnoreCase(str)) {
            this.languageService.downloadLanguage(i, ACLanguageDownloadService.ACLdbType.Baseline, aCLanguageDownloadFileCallback);
        } else {
            this.languageService.downloadLanguageFlavor(i, str, aCLanguageDownloadFileCallback);
        }
    }

    public List<Integer> getAvailableLanguages() {
        ArrayList arrayList = new ArrayList();
        for (ACLanguageDownloadService.ACLanguageDbInfo aCLanguageDbInfo : this.languageService.getDatabaseList()) {
            if (!aCLanguageDbInfo.isPreinstalled() && !aCLanguageDbInfo.isDownloaded()) {
                arrayList.add(Integer.valueOf(aCLanguageDbInfo.getXt9LanguageId()));
            }
        }
        return arrayList;
    }

    public List<Integer> getDownloadedLanguages() {
        ArrayList arrayList = new ArrayList();
        for (ACLanguageDownloadService.ACLanguageDbInfo aCLanguageDbInfo : this.languageService.getDatabaseList()) {
            if (aCLanguageDbInfo.isDownloaded() && !isUpdateAvailable(aCLanguageDbInfo)) {
                arrayList.add(Integer.valueOf(aCLanguageDbInfo.getXt9LanguageId()));
            }
        }
        return arrayList;
    }

    public List<Integer> getExistingLanguages() {
        ArrayList arrayList = new ArrayList();
        for (ACLanguageDownloadService.ACLanguageDbInfo aCLanguageDbInfo : this.languageService.getDatabaseList()) {
            if (aCLanguageDbInfo.isPreinstalled()) {
                arrayList.add(Integer.valueOf(aCLanguageDbInfo.getXt9LanguageId()));
            }
        }
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swypeconnect.ac.ACService
    public String getName() {
        return ACManager.LANGUAGE_COMPAT_SERVICE;
    }

    public List<Integer> getUpdatableLanguages() {
        ArrayList arrayList = new ArrayList();
        for (ACLanguageDownloadService.ACLanguageDbInfo aCLanguageDbInfo : this.languageService.getDatabaseList()) {
            if (isUpdateAvailable(aCLanguageDbInfo)) {
                arrayList.add(Integer.valueOf(aCLanguageDbInfo.getXt9LanguageId()));
            }
        }
        return arrayList;
    }

    public boolean isFlavorAvailable(int i, String str) {
        if (str == null) {
            return false;
        }
        try {
            ACLanguageDownloadService.ACLanguageDbInfo database = this.languageService.getDatabase(i);
            String[] flavors = database.getFlavors();
            if (flavors != null && Arrays.asList(flavors).contains(str)) {
                return true;
            }
            List asList = Arrays.asList(database.getTypes());
            if (asList.contains(ACLanguageDownloadService.ACLdbType.MLM) && str.equalsIgnoreCase(ACLanguageDownloadService.ACLdbType.MLM.toString())) {
                return true;
            }
            if (asList.contains(ACLanguageDownloadService.ACLdbType.Baseline)) {
                return str.equalsIgnoreCase(ACLanguageDownloadService.ACLdbType.Baseline.toString());
            }
            return false;
        } catch (ACException e) {
            return false;
        }
    }

    public boolean isLanguageListAvailable() {
        return this.languageService.isLanguageListAvailable();
    }

    public void languageUninstalled(int i) throws ACException {
        this.languageService.languageUninstalled(i);
    }

    public void registerCallback(ACLanguageDownloadListCallback aCLanguageDownloadListCallback, boolean z) {
        this.languageListCallbacks.add(aCLanguageDownloadListCallback);
        if (z) {
            aCLanguageDownloadListCallback.availableLanguages(getAvailableLanguages());
            aCLanguageDownloadListCallback.downloadedLanguages(getDownloadedLanguages());
            aCLanguageDownloadListCallback.updatableLanguages(getUpdatableLanguages());
        }
    }

    public void removeSupportedLanguage(Integer num) {
        this.languageService.removeSupportedLanguage(num);
        this.listCallback.onLanguageListUpdate();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swypeconnect.ac.ACService
    public boolean requireInitialization() {
        return this.languageService.requireInitialization();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swypeconnect.ac.ACService
    public boolean requiresDocument(int i) {
        return this.languageService.requiresDocument(i);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.swypeconnect.ac.ACService
    public void shutdown() {
        this.isShutdown = true;
        unregisterCallbacks();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.swypeconnect.ac.ACService
    public void start() {
        this.isShutdown = false;
    }

    public void unregisterCallback(ACLanguageDownloadListCallback aCLanguageDownloadListCallback) {
        this.languageListCallbacks.remove(aCLanguageDownloadListCallback);
    }

    public void unregisterCallbacks() {
        this.languageListCallbacks.clear();
    }

    public void useOnlyVersionForUpdate() {
        this.almsAreUpgrades = false;
        this.listCallback.onLanguageListUpdate();
    }
}
