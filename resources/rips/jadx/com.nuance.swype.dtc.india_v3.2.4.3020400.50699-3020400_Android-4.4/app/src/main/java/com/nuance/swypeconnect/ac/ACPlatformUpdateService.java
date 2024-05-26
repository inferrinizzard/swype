package com.nuance.swypeconnect.ac;

import com.nuance.connect.api.PlatformUpdateService;
import com.nuance.connect.util.ConcurrentCallbackSet;
import com.nuance.connect.util.Logger;
import java.io.File;

/* loaded from: classes.dex */
public class ACPlatformUpdateService extends ACService {
    public static final int REASON_FAILED_DISK_FULL = 6;
    public static final int REASON_FAILED_HTTP = 0;
    public static final int REASON_FAILED_UNKNOWN = 7;
    public static final int REASON_NETWORK_TIMEOUT = 4;
    public static final int REASON_USER_CANCELED = 3;
    private DownloadCallbackWrapper downloadCallback;
    private PlatformUpdateService service;
    private final ConcurrentCallbackSet<ACPlatformUpdateCallback> platformUpdateCallbacks = new ConcurrentCallbackSet<>();
    private final PlatformUpdateService.UpdateCallback updateCallback = new PlatformUpdateService.UpdateCallback() { // from class: com.nuance.swypeconnect.ac.ACPlatformUpdateService.1
        @Override // com.nuance.connect.api.PlatformUpdateService.UpdateCallback
        public void updateAvailable() {
            for (ACPlatformUpdateCallback aCPlatformUpdateCallback : (ACPlatformUpdateCallback[]) ACPlatformUpdateService.this.platformUpdateCallbacks.toArray(new ACPlatformUpdateCallback[0])) {
                aCPlatformUpdateCallback.updateAvailable();
            }
        }
    };
    private final Logger.Log log = Logger.getLog(Logger.LoggerType.DEVELOPER, getClass().getSimpleName());

    /* loaded from: classes.dex */
    public interface ACPlatformUpdateCallback {
        void updateAvailable();
    }

    /* loaded from: classes.dex */
    public interface ACPlatformUpdateDownloadCallback {
        void downloadComplete(File file);

        void downloadFailed(int i);

        void downloadPercentage(int i);

        void downloadStarted();

        void downloadStopped(int i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class DownloadCallbackWrapper implements PlatformUpdateService.DownloadCallback {
        private ACPlatformUpdateDownloadCallback wrapped;

        private DownloadCallbackWrapper(ACPlatformUpdateDownloadCallback aCPlatformUpdateDownloadCallback) {
            this.wrapped = aCPlatformUpdateDownloadCallback;
        }

        @Override // com.nuance.connect.api.PlatformUpdateService.DownloadCallback
        public void downloadComplete(File file) {
            this.wrapped.downloadComplete(file);
        }

        @Override // com.nuance.connect.api.PlatformUpdateService.DownloadCallback
        public void downloadFailed(int i) {
            this.wrapped.downloadFailed(i);
        }

        @Override // com.nuance.connect.api.PlatformUpdateService.DownloadCallback
        public void downloadPercentage(int i) {
            this.wrapped.downloadPercentage(i);
        }

        @Override // com.nuance.connect.api.PlatformUpdateService.DownloadCallback
        public void downloadStarted() {
            this.wrapped.downloadStarted();
        }

        @Override // com.nuance.connect.api.PlatformUpdateService.DownloadCallback
        public void downloadStopped(int i) {
            this.wrapped.downloadStopped(i);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ACPlatformUpdateService(PlatformUpdateService platformUpdateService) {
        this.service = platformUpdateService;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void finishDownload() {
        this.service.unregisterCallback(this.downloadCallback);
        this.downloadCallback = null;
    }

    public void cancelDownload() {
        this.service.cancelDownload();
    }

    public void downloadUpdate(final ACPlatformUpdateDownloadCallback aCPlatformUpdateDownloadCallback) throws ACException {
        if (!isAvailable()) {
            throw new ACException(130, "There is currently no update available for download.");
        }
        if (this.downloadCallback == null) {
            this.downloadCallback = new DownloadCallbackWrapper(new ACPlatformUpdateDownloadCallback() { // from class: com.nuance.swypeconnect.ac.ACPlatformUpdateService.2
                @Override // com.nuance.swypeconnect.ac.ACPlatformUpdateService.ACPlatformUpdateDownloadCallback
                public void downloadComplete(File file) {
                    ACPlatformUpdateService.this.finishDownload();
                    if (aCPlatformUpdateDownloadCallback != null) {
                        aCPlatformUpdateDownloadCallback.downloadComplete(file);
                    }
                }

                @Override // com.nuance.swypeconnect.ac.ACPlatformUpdateService.ACPlatformUpdateDownloadCallback
                public void downloadFailed(int i) {
                    ACPlatformUpdateService.this.finishDownload();
                    if (aCPlatformUpdateDownloadCallback != null) {
                        aCPlatformUpdateDownloadCallback.downloadFailed(i);
                    }
                }

                @Override // com.nuance.swypeconnect.ac.ACPlatformUpdateService.ACPlatformUpdateDownloadCallback
                public void downloadPercentage(int i) {
                    if (aCPlatformUpdateDownloadCallback != null) {
                        aCPlatformUpdateDownloadCallback.downloadPercentage(i);
                    }
                }

                @Override // com.nuance.swypeconnect.ac.ACPlatformUpdateService.ACPlatformUpdateDownloadCallback
                public void downloadStarted() {
                    if (aCPlatformUpdateDownloadCallback != null) {
                        aCPlatformUpdateDownloadCallback.downloadStarted();
                    }
                }

                @Override // com.nuance.swypeconnect.ac.ACPlatformUpdateService.ACPlatformUpdateDownloadCallback
                public void downloadStopped(int i) {
                    ACPlatformUpdateService.this.finishDownload();
                    if (aCPlatformUpdateDownloadCallback != null) {
                        aCPlatformUpdateDownloadCallback.downloadStopped(i);
                    }
                }
            });
            this.service.downloadUpdate(this.downloadCallback);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.swypeconnect.ac.ACService
    public String getName() {
        return ACManager.PACKAGE_UPDATER_SERVICE;
    }

    public boolean isAvailable() {
        return this.service.isAvailable();
    }

    public void registerCallback(ACPlatformUpdateCallback aCPlatformUpdateCallback) {
        if (aCPlatformUpdateCallback == null) {
            return;
        }
        if (this.platformUpdateCallbacks.isEmpty()) {
            this.service.registerCallback(this.updateCallback);
        }
        this.platformUpdateCallbacks.add(aCPlatformUpdateCallback);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.swypeconnect.ac.ACService
    public boolean requiresDocument(int i) {
        return i == 1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.swypeconnect.ac.ACService
    public void shutdown() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.swypeconnect.ac.ACService
    public void start() {
    }

    public void unregisterCallback(ACPlatformUpdateCallback aCPlatformUpdateCallback) {
        this.platformUpdateCallbacks.remove(aCPlatformUpdateCallback);
        if (this.platformUpdateCallbacks.isEmpty()) {
            this.service.unregisterCallback(this.updateCallback);
        }
    }

    public void unregisterCallbacks() {
        this.platformUpdateCallbacks.clear();
        this.service.unregisterCallback(this.updateCallback);
    }
}
