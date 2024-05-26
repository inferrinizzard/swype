package com.nuance.swypeconnect.ac;

import com.nuance.connect.api.ConnectServiceManager;
import com.nuance.connect.api.LivingLanguageService;
import com.nuance.connect.common.ConnectFeature;
import com.nuance.connect.common.FeaturesLastUsed;
import com.nuance.connect.store.PersistentDataStore;
import com.nuance.connect.util.Logger;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public final class ACLivingLanguageService extends ACService {
    public static final int CATEGORY_TYPE_HOTWORDS = 3;
    public static final int CATEGORY_TYPE_UDA = 1;
    ConnectServiceManager connect;
    LivingLanguageService service;
    private Logger.Log log = Logger.getLog(Logger.LoggerType.OEM);
    private final Map<ACLivingLanguageCallback, LivingLanguageService.Callback> livinglanguageCallbacks = new HashMap();

    /* loaded from: classes.dex */
    public interface ACLivingLanguageCallback {
        void downloadProgress(int i, int i2, int i3, String str, String str2, int i4);

        void subscribed(int i, int i2, int i3, String str, String str2, int i4);

        void unsubscribed(int i, int i2, int i3, String str, String str2);

        void updated(int i, int i2, int i3, String str, String str2, int i4);

        void updatesAvailable(boolean z);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ACLivingLanguageService(ConnectServiceManager connectServiceManager, PersistentDataStore persistentDataStore) {
        this.connect = connectServiceManager;
        this.service = (LivingLanguageService) connectServiceManager.getFeatureService(ConnectFeature.LIVING_LANGUAGE);
    }

    public final void cancelDownloads() {
        this.service.cancelLivingDownloads();
    }

    public final void disableLivingLanguage() {
        this.service.disableLivingLanguage();
    }

    public final void enableLivingLanguage() throws ACException {
        this.service.enableLivingLanguage();
    }

    public final int getMaxNumberOfEvents() {
        return this.service.getMaxNumberOfLivingEvents();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.swypeconnect.ac.ACService
    public final String getName() {
        return ACManager.LIVING_LANGUAGE_SERVICE;
    }

    public final boolean isHotWordsEnabled() {
        return this.service.isHotWordsEnabled() && this.service.livingLanguageAvailable();
    }

    public final boolean isLivingLanguageEnabled() {
        return this.service.isLivingLanguageEnabled() && this.service.livingLanguageAvailable();
    }

    public final boolean isUDAEnabled() {
        return this.service.isUDAEnabled() && this.service.livingLanguageAvailable();
    }

    public final void registerCallback(final ACLivingLanguageCallback aCLivingLanguageCallback) {
        synchronized (this.livinglanguageCallbacks) {
            if (aCLivingLanguageCallback != null) {
                if (!this.livinglanguageCallbacks.containsKey(aCLivingLanguageCallback)) {
                    LivingLanguageService.Callback callback = new LivingLanguageService.Callback() { // from class: com.nuance.swypeconnect.ac.ACLivingLanguageService.1
                        Boolean available = null;

                        @Override // com.nuance.connect.api.LivingLanguageService.Callback
                        public void downloadProgress(int i, int i2, int i3, String str, String str2, int i4) {
                            if (ACLivingLanguageService.this.isShutdown) {
                                return;
                            }
                            aCLivingLanguageCallback.downloadProgress(i, i2, i3, str, str2, i4);
                            ACLivingLanguageService.this.log.d("download progress categoryId=", Integer.valueOf(i), "type=", Integer.valueOf(i2), " languageId=", Integer.valueOf(i3), " locale=", str, " country=", str2, " percent=", Integer.valueOf(i4));
                        }

                        @Override // com.nuance.connect.api.LivingLanguageService.Callback
                        public void subscriptionAdded(int i, int i2, int i3, String str, String str2, int i4) {
                            if (ACLivingLanguageService.this.isShutdown) {
                                return;
                            }
                            if (i == 1024) {
                                ACLivingLanguageService.this.connect.updateFeatureLastUsed(FeaturesLastUsed.Feature.HOTWORDS, System.currentTimeMillis());
                            } else if (i == 1025) {
                                ACLivingLanguageService.this.connect.updateFeatureLastUsed(FeaturesLastUsed.Feature.LLUDA, System.currentTimeMillis());
                            }
                            aCLivingLanguageCallback.subscribed(i, i2, i3, str, str2, i4);
                            ACLivingLanguageService.this.log.d("subscribed id=", Integer.valueOf(i), " type=", Integer.valueOf(i2), " languageId=", Integer.valueOf(i3), " locale=", str, " country=", str2, " count=", Integer.valueOf(i4));
                        }

                        @Override // com.nuance.connect.api.LivingLanguageService.Callback
                        public void subscriptionRemoved(int i, int i2, int i3, String str, String str2) {
                            if (ACLivingLanguageService.this.isShutdown) {
                                return;
                            }
                            aCLivingLanguageCallback.unsubscribed(i, i2, i3, str, str2);
                            ACLivingLanguageService.this.log.d("unsubscribed id=", Integer.valueOf(i), "type=", Integer.valueOf(i2), " languageId=", Integer.valueOf(i3), " locale=", str, " country=", str2);
                        }

                        @Override // com.nuance.connect.api.LivingLanguageService.Callback
                        public void subscriptionUpdated(int i, int i2, int i3, String str, String str2, int i4) {
                            if (ACLivingLanguageService.this.isShutdown) {
                                return;
                            }
                            if (i == 1024) {
                                ACLivingLanguageService.this.connect.updateFeatureLastUsed(FeaturesLastUsed.Feature.HOTWORDS, System.currentTimeMillis());
                            } else if (i == 1025) {
                                ACLivingLanguageService.this.connect.updateFeatureLastUsed(FeaturesLastUsed.Feature.LLUDA, System.currentTimeMillis());
                            }
                            aCLivingLanguageCallback.updated(i, i2, i3, str, str2, i4);
                            ACLivingLanguageService.this.log.d("updated id=", Integer.valueOf(i), "type=", Integer.valueOf(i2), " languageId=", Integer.valueOf(i3), " locale=", str, " country=", str2, " count=", Integer.valueOf(i4));
                        }

                        @Override // com.nuance.connect.api.LivingLanguageService.Callback
                        public void updatesAvailable(boolean z) {
                            ACLivingLanguageService.this.log.d("updatesAvailable(", Boolean.valueOf(z), ")");
                            boolean z2 = false;
                            if (this.available == null || z != this.available.booleanValue()) {
                                this.available = Boolean.valueOf(z);
                                z2 = true;
                            }
                            if (ACLivingLanguageService.this.isShutdown || !z2) {
                                return;
                            }
                            aCLivingLanguageCallback.updatesAvailable(this.available.booleanValue());
                        }
                    };
                    synchronized (this.livinglanguageCallbacks) {
                        this.livinglanguageCallbacks.put(aCLivingLanguageCallback, callback);
                        this.service.registerLivingCallback(callback);
                    }
                }
            }
            this.log.d("callback is null or already registered");
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.swypeconnect.ac.ACService
    public final boolean requiresDocument(int i) {
        return i == 1;
    }

    public final void setLivingLanguageStatus(boolean z, boolean z2) {
        this.service.setLivingLanguageStatus(z, z2);
    }

    public final void setMaxNumberOfEvents(int i) {
        this.service.setMaxNumberOfLivingEvents(i);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.swypeconnect.ac.ACService
    public final void shutdown() {
        this.isShutdown = true;
        unregisterCallbacks();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.swypeconnect.ac.ACService
    public final void start() {
        this.isShutdown = false;
    }

    public final void unregisterCallback(ACLivingLanguageCallback aCLivingLanguageCallback) {
        synchronized (this.livinglanguageCallbacks) {
            this.service.unregisterLivingCallback(this.livinglanguageCallbacks.remove(aCLivingLanguageCallback));
        }
    }

    public final void unregisterCallbacks() {
        synchronized (this.livinglanguageCallbacks) {
            this.livinglanguageCallbacks.clear();
            this.service.unregisterLivingCallbacks();
        }
    }
}
