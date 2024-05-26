package com.nuance.swypeconnect.ac;

import android.annotation.SuppressLint;
import com.nuance.connect.api.AddonDictionariesService;
import com.nuance.connect.common.FeaturesLastUsed;
import com.nuance.connect.store.PersistentDataStore;
import com.nuance.connect.util.ConcurrentCallbackSet;
import com.nuance.connect.util.Logger;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public final class ACChineseDictionaryDownloadService extends ACService {
    public static final int REASON_FAILED_CANCELED = 2;
    public static final int REASON_FAILED_HTTP = 0;
    public static final int REASON_FAILED_MAX_RETRY = 1;
    public static final int REASON_NETWORK_TIMEOUT = 4;
    public static final int REASON_USER_CANCELED = 3;
    private AddonDictionariesService addonDictService;
    private ACManager manager;

    @SuppressLint({"UseSparseArrays"})
    private final PersistentDataStore store;
    private final List<String> preinstalledDictionaries = new ArrayList();
    private boolean bInitialized = false;
    private Logger.Log log = Logger.getLog(Logger.LoggerType.OEM, getClass().getSimpleName());
    private final ConcurrentCallbackSet<ACChineseDictionaryDownloadListCallback> listCallbacks = new ConcurrentCallbackSet<>();
    private final AddonDictionariesService.AddonDictionaryListCallback dictionaryListCallback = new AddonDictionariesService.AddonDictionaryListCallback() { // from class: com.nuance.swypeconnect.ac.ACChineseDictionaryDownloadService.1
        List<String> available = new ArrayList();
        List<String> downloaded = new ArrayList();
        List<String> updatable = new ArrayList();

        List<String> dictionaryListToStrings(List<ACChineseDictionary> list) {
            ArrayList arrayList = new ArrayList();
            Iterator<ACChineseDictionary> it = list.iterator();
            while (it.hasNext()) {
                arrayList.add(it.next().getKey());
            }
            return arrayList;
        }

        boolean isListUpdated(List<String> list, List<String> list2) {
            return (!list.containsAll(list2)) || !list2.containsAll(list);
        }

        @Override // com.nuance.connect.api.AddonDictionariesService.AddonDictionaryListCallback
        public void listUpdated() {
            boolean z;
            boolean z2;
            boolean z3;
            ACChineseDictionaryDownloadService.this.log.d("dictionaryListCallback.listUpdated() available=", Boolean.valueOf(ACChineseDictionaryDownloadService.this.isDictionaryListAvailable()));
            if (ACChineseDictionaryDownloadService.this.isShutdown || !ACChineseDictionaryDownloadService.this.isDictionaryListAvailable()) {
                return;
            }
            ACChineseDictionaryDownloadService.this.markPreinstalledDictionaries();
            List<ACChineseDictionary> availableDictionaries = ACChineseDictionaryDownloadService.this.getAvailableDictionaries();
            List<String> dictionaryListToStrings = dictionaryListToStrings(availableDictionaries);
            if (isListUpdated(this.available, dictionaryListToStrings)) {
                this.available = dictionaryListToStrings;
                z = true;
            } else {
                z = false;
            }
            List<ACChineseDictionary> downloadedDictionaries = ACChineseDictionaryDownloadService.this.getDownloadedDictionaries();
            List<String> dictionaryListToStrings2 = dictionaryListToStrings(downloadedDictionaries);
            if (isListUpdated(this.downloaded, dictionaryListToStrings2)) {
                this.downloaded = dictionaryListToStrings2;
                z2 = true;
            } else {
                z2 = false;
            }
            List<ACChineseDictionary> updatableDictionaries = ACChineseDictionaryDownloadService.this.getUpdatableDictionaries();
            List<String> dictionaryListToStrings3 = dictionaryListToStrings(updatableDictionaries);
            if (isListUpdated(this.updatable, dictionaryListToStrings3)) {
                this.updatable = dictionaryListToStrings3;
                z3 = true;
            } else {
                z3 = false;
            }
            ACChineseDictionaryDownloadService.this.log.d("dictionaryListCallback.listUpdated() notifyAvailable=", Boolean.valueOf(z), " notifyDownloaded=", Boolean.valueOf(z2), " notifyUpdatable=", Boolean.valueOf(z3));
            for (ACChineseDictionaryDownloadListCallback aCChineseDictionaryDownloadListCallback : (ACChineseDictionaryDownloadListCallback[]) ACChineseDictionaryDownloadService.this.listCallbacks.toArray(new ACChineseDictionaryDownloadListCallback[0])) {
                if (z) {
                    aCChineseDictionaryDownloadListCallback.availableDictionaries(availableDictionaries);
                }
                if (z2) {
                    aCChineseDictionaryDownloadListCallback.downloadedDictionaries(downloadedDictionaries);
                }
                if (z3) {
                    aCChineseDictionaryDownloadListCallback.updatableDictionaries(updatableDictionaries);
                }
            }
            if (ACChineseDictionaryDownloadService.this.bInitialized) {
                return;
            }
            ACChineseDictionaryDownloadService.this.manager.serviceInitialized(ACChineseDictionaryDownloadService.this.getName());
            ACChineseDictionaryDownloadService.this.bInitialized = true;
        }
    };

    /* loaded from: classes.dex */
    public static final class ACChineseDictionary {
        private static final String SEPARATOR = "-";
        private AddonDictionariesService.AddonDictionary dictionary;

        protected ACChineseDictionary(AddonDictionariesService.AddonDictionary addonDictionary) {
            this.dictionary = addonDictionary;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public String getDictionary() {
            return this.dictionary.getDictionary();
        }

        public final String getCategory() {
            return this.dictionary.getCategoryTranslated();
        }

        public final int getId() {
            return this.dictionary.getId();
        }

        public final String getKey() {
            return this.dictionary.getLanguage() + "-" + this.dictionary.getCategory() + "-" + this.dictionary.getName();
        }

        public final int getLanguage() {
            return this.dictionary.getLanguage();
        }

        public final String getName() {
            return this.dictionary.getNameTranslated();
        }

        public final int getRank() {
            return this.dictionary.getRank();
        }
    }

    /* loaded from: classes.dex */
    public interface ACChineseDictionaryDownloadFileCallback {
        boolean downloadComplete(File file);

        void downloadFailed(int i);

        void downloadPercentage(int i);

        void downloadStarted();

        void downloadStopped(int i);
    }

    /* loaded from: classes.dex */
    public interface ACChineseDictionaryDownloadListCallback {
        void availableDictionaries(List<ACChineseDictionary> list);

        void downloadedDictionaries(List<ACChineseDictionary> list);

        void updatableDictionaries(List<ACChineseDictionary> list);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ACChineseDictionaryDownloadService(AddonDictionariesService addonDictionariesService, PersistentDataStore persistentDataStore, ACManager aCManager) {
        this.store = persistentDataStore;
        this.manager = aCManager;
        this.addonDictService = addonDictionariesService;
        addonDictionariesService.registerDictionaryListCallback(this.dictionaryListCallback);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void markPreinstalledDictionaries() {
        String[] strArr;
        if (isDictionaryListAvailable()) {
            List<AddonDictionariesService.AddonDictionary> availableDictionaries = this.addonDictService.getAvailableDictionaries();
            synchronized (this.preinstalledDictionaries) {
                strArr = (String[]) this.preinstalledDictionaries.toArray(new String[0]);
            }
            for (AddonDictionariesService.AddonDictionary addonDictionary : availableDictionaries) {
                for (String str : strArr) {
                    ACChineseDictionary aCChineseDictionary = new ACChineseDictionary(addonDictionary);
                    if (aCChineseDictionary.getKey().equals(str)) {
                        this.addonDictService.markDictionaryInstalled(aCChineseDictionary.getDictionary());
                    }
                }
            }
        }
    }

    public final void cancelDownload(ACChineseDictionary aCChineseDictionary) throws ACException {
        this.log.d("cancelDownload id=", aCChineseDictionary != null ? aCChineseDictionary.getDictionary() : null);
        if (aCChineseDictionary == null) {
            this.log.e("ACChineseDictionaryDownloadService.cancelDownload dict is null");
        } else {
            this.addonDictService.cancelDownload(aCChineseDictionary.getDictionary());
        }
    }

    public final void downloadDictionary(ACChineseDictionary aCChineseDictionary, final ACChineseDictionaryDownloadFileCallback aCChineseDictionaryDownloadFileCallback) throws ACException {
        this.log.d("downloadDictionary id=", aCChineseDictionary != null ? aCChineseDictionary.getDictionary() : null);
        if (aCChineseDictionaryDownloadFileCallback == null) {
            this.log.e("ACChineseDictionaryDownloadService.downloadDictionary callback is null");
        } else if (aCChineseDictionary != null) {
            this.addonDictService.installDictionary(aCChineseDictionary.getDictionary(), new AddonDictionariesService.AddonDictionaryDownloadCallback() { // from class: com.nuance.swypeconnect.ac.ACChineseDictionaryDownloadService.2
                boolean started = false;

                @Override // com.nuance.connect.api.AddonDictionariesService.AddonDictionaryDownloadCallback
                public boolean downloadComplete(File file) {
                    if (ACChineseDictionaryDownloadService.this.isShutdown) {
                        return false;
                    }
                    ACChineseDictionaryDownloadService.this.manager.getConnect().updateFeatureLastUsed(FeaturesLastUsed.Feature.CHINESEDICTIONARIES, System.currentTimeMillis());
                    downloadStarted();
                    return aCChineseDictionaryDownloadFileCallback.downloadComplete(file);
                }

                @Override // com.nuance.connect.api.AddonDictionariesService.AddonDictionaryDownloadCallback
                public void downloadFailed(int i) {
                    if (ACChineseDictionaryDownloadService.this.isShutdown) {
                        return;
                    }
                    aCChineseDictionaryDownloadFileCallback.downloadFailed(i);
                }

                @Override // com.nuance.connect.api.AddonDictionariesService.AddonDictionaryDownloadCallback
                public void downloadPercentage(int i) {
                    if (ACChineseDictionaryDownloadService.this.isShutdown) {
                        return;
                    }
                    downloadStarted();
                    aCChineseDictionaryDownloadFileCallback.downloadPercentage(i);
                }

                @Override // com.nuance.connect.api.AddonDictionariesService.AddonDictionaryDownloadCallback
                public void downloadStarted() {
                    if (ACChineseDictionaryDownloadService.this.isShutdown || this.started) {
                        return;
                    }
                    aCChineseDictionaryDownloadFileCallback.downloadStarted();
                    this.started = true;
                }

                @Override // com.nuance.connect.api.AddonDictionariesService.AddonDictionaryDownloadCallback
                public void downloadStopped(int i) {
                    if (ACChineseDictionaryDownloadService.this.isShutdown) {
                        return;
                    }
                    aCChineseDictionaryDownloadFileCallback.downloadStopped(i);
                }
            });
        } else {
            this.log.e("ACChineseDictionaryDownloadService.downloadDictionary dict is null");
            aCChineseDictionaryDownloadFileCallback.downloadFailed(5);
        }
    }

    public final List<ACChineseDictionary> getAvailableDictionaries() {
        ArrayList arrayList = new ArrayList();
        if (isDictionaryListAvailable()) {
            for (AddonDictionariesService.AddonDictionary addonDictionary : this.addonDictService.getAvailableDictionaries()) {
                if (!addonDictionary.isInstalled()) {
                    arrayList.add(new ACChineseDictionary(addonDictionary));
                }
            }
        }
        return arrayList;
    }

    public final List<ACChineseDictionary> getDownloadedDictionaries() {
        ArrayList arrayList = new ArrayList();
        if (isDictionaryListAvailable()) {
            for (AddonDictionariesService.AddonDictionary addonDictionary : this.addonDictService.getAvailableDictionaries()) {
                if (!addonDictionary.hasUpdate() && addonDictionary.isSubscribed() && addonDictionary.isInstalled()) {
                    arrayList.add(new ACChineseDictionary(addonDictionary));
                }
            }
        }
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.swypeconnect.ac.ACService
    public final String getName() {
        return ACManager.CHINESE_DICTIONARY_SERVICE;
    }

    public final List<ACChineseDictionary> getUpdatableDictionaries() {
        ArrayList arrayList = new ArrayList();
        if (isDictionaryListAvailable()) {
            for (AddonDictionariesService.AddonDictionary addonDictionary : this.addonDictService.getAvailableDictionaries()) {
                if (addonDictionary.hasUpdate()) {
                    arrayList.add(new ACChineseDictionary(addonDictionary));
                }
            }
        }
        return arrayList;
    }

    public final boolean isDictionaryListAvailable() {
        return this.addonDictService.isDictionaryListAvailable();
    }

    public final void preinstallDictionary(String str) {
        if (str == null) {
            this.log.e("preinstallDictionary key is null");
            return;
        }
        synchronized (this.preinstalledDictionaries) {
            this.preinstalledDictionaries.add(str);
        }
        markPreinstalledDictionaries();
        this.addonDictService.notifyDictionariesofStatus();
    }

    public final void registerCallback(ACChineseDictionaryDownloadListCallback aCChineseDictionaryDownloadListCallback, boolean z) {
        this.listCallbacks.add(aCChineseDictionaryDownloadListCallback);
        if (z) {
            aCChineseDictionaryDownloadListCallback.availableDictionaries(getAvailableDictionaries());
            aCChineseDictionaryDownloadListCallback.downloadedDictionaries(getDownloadedDictionaries());
            aCChineseDictionaryDownloadListCallback.updatableDictionaries(getUpdatableDictionaries());
        }
    }

    public final void removeDictionary(ACChineseDictionary aCChineseDictionary) {
        this.log.d("removeDictionary id=", aCChineseDictionary != null ? aCChineseDictionary.getDictionary() : null);
        if (aCChineseDictionary == null) {
            this.log.e("ACChineseDictionaryDownloadService.removeDictionary dict is null");
            return;
        }
        synchronized (this.preinstalledDictionaries) {
            if (this.preinstalledDictionaries.contains(aCChineseDictionary.getKey())) {
                this.preinstalledDictionaries.remove(aCChineseDictionary.getKey());
            }
        }
        this.addonDictService.removeDictionary(aCChineseDictionary.getDictionary());
        this.addonDictService.notifyDictionariesofStatus();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.swypeconnect.ac.ACService
    public final boolean requireInitialization() {
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.swypeconnect.ac.ACService
    public final boolean requiresDocument(int i) {
        return i == 1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.swypeconnect.ac.ACService
    public final void shutdown() {
        unregisterCallbacks();
        this.isShutdown = true;
        this.bInitialized = false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.swypeconnect.ac.ACService
    public final void start() {
        this.isShutdown = false;
        this.bInitialized = false;
        this.dictionaryListCallback.listUpdated();
    }

    public final void unregisterCallback(ACChineseDictionaryDownloadListCallback aCChineseDictionaryDownloadListCallback) {
        this.listCallbacks.remove(aCChineseDictionaryDownloadListCallback);
    }

    public final void unregisterCallbacks() {
        this.listCallbacks.clear();
    }
}
