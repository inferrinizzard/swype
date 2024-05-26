package com.nuance.swype.connect;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Environment;
import com.nuance.android.compat.NotificationCompat;
import com.nuance.android.compat.UserManagerCompat;
import com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.xmlResults.XMLResultsHandler;
import com.nuance.swype.input.AppPreferences;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.R;
import com.nuance.swype.input.settings.UpdatesDispatch;
import com.nuance.swype.util.LogManager;
import com.nuance.swypeconnect.ac.ACException;
import com.nuance.swypeconnect.ac.ACManager;
import com.nuance.swypeconnect.ac.ACPlatformUpdateService;
import java.io.File;

/* loaded from: classes.dex */
public class SDKUpdateManager {
    public static final int OUT_OF_SPACE_ERROR = -999;
    private static final int UPDATE_NOTIFICATION_ID = 714928067;
    public static final int UPGRADE_STORAGE_FREE_SPACE_THRESHOLD = 18874368;
    private static final LogManager.Log log = LogManager.getLog("SDKUpdateManager");
    private boolean available;
    private Connect connect;
    private PlatformUpdateDownloadCallbackWrapper downloadCallback;
    private boolean isDownloaded;
    private boolean isOutOfSpace;
    private File updateFile;
    private boolean updateNotificationSent;
    private ACPlatformUpdateService updateService;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SDKUpdateManager(Connect connect) {
        this.connect = connect;
        AppPreferences appPrefs = IMEApplication.from(connect.getContext()).getAppPreferences();
        this.updateNotificationSent = appPrefs.getUpdateNotificationSent();
        String updatePath = appPrefs.getUpdateFilePath();
        if (updatePath != null && updatePath.length() > 0) {
            this.updateFile = new File(updatePath);
            this.isDownloaded = true;
        }
        sendUpgradeAvailableNotification();
    }

    public void cancelDownload() {
        if (this.downloadCallback != null) {
            this.downloadCallback.cancel();
        }
    }

    public void downloadUpdate(ACPlatformUpdateService.ACPlatformUpdateDownloadCallback callback) throws ACException {
        getUpdateService();
        if (this.updateService != null && !showNoSpaceNotificationIfShortStorage()) {
            this.isOutOfSpace = false;
            this.downloadCallback = new PlatformUpdateDownloadCallbackWrapper(this, callback);
            this.updateService.downloadUpdate(this.downloadCallback);
        }
    }

    public boolean showNoSpaceNotificationIfShortStorage() {
        Context ctx = this.connect.getContext();
        if (ctx != null && ctx.getFilesDir().getFreeSpace() < 18874368) {
            sendOutOfSpaceNotification();
            this.isOutOfSpace = true;
            return true;
        }
        this.isOutOfSpace = false;
        return false;
    }

    public boolean isOutOfSpace() {
        return this.isOutOfSpace;
    }

    public boolean resetOutOfSpace() {
        this.isOutOfSpace = false;
        return false;
    }

    public boolean isAvailable() {
        getUpdateService();
        if (this.updateService != null && !this.available && this.updateService.isAvailable()) {
            IMEApplication.from(this.connect.getContext()).getAppPreferences().setUpdateAvailable(true);
            this.available = true;
            sendUpgradeAvailableNotification();
        }
        return this.available;
    }

    public void registerCallback(ACPlatformUpdateService.ACPlatformUpdateCallback callback) {
        getUpdateService();
        if (this.updateService != null) {
            this.updateService.registerCallback(callback);
        }
    }

    public void unregisterCallback(ACPlatformUpdateService.ACPlatformUpdateCallback callback) {
        getUpdateService();
        if (this.updateService != null) {
            this.updateService.unregisterCallback(callback);
        }
    }

    public boolean isUpdateDownloaded() {
        return this.isDownloaded;
    }

    /* JADX WARN: Removed duplicated region for block: B:28:0x00ab  */
    /* JADX WARN: Removed duplicated region for block: B:30:0x00e4  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void installUpdate() {
        /*
            Method dump skipped, instructions count: 252
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.nuance.swype.connect.SDKUpdateManager.installUpdate():void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public ACPlatformUpdateService getUpdateService() {
        if (this.updateService != null) {
            return this.updateService;
        }
        if (!UserManagerCompat.isUserUnlocked(this.connect.getContext()) || !this.connect.isStarted()) {
            log.e("Connect not available");
            return null;
        }
        try {
            this.updateService = (ACPlatformUpdateService) this.connect.getSDKManager().getService(ACManager.PACKAGE_UPDATER_SERVICE);
        } catch (ACException e) {
            if (e.getReasonCode() != 104) {
                log.e("Error getting update service: " + e.getMessage());
            }
        }
        return this.updateService;
    }

    public boolean isLegalAccepted() {
        return this.connect.getLegal().isOptInAccepted() && this.connect.getLegal().isTosAccepted();
    }

    public static SDKUpdateManager from(Context context) {
        return Connect.from(context).getUpdates();
    }

    public void upgrade() {
        log.d("SDKUpdateManager.upgrade()");
        if (this.updateFile != null) {
            String fileName = this.updateFile.getName();
            String downloadExternalStorage = Environment.getExternalStorageDirectory() + "/download/";
            File sdFile = new File(downloadExternalStorage, fileName);
            if (sdFile.exists() && sdFile.isFile()) {
                log.d("Cleaning up update file from SD card.");
                if (!sdFile.delete()) {
                    log.w("Could not remove update file from SD card. " + sdFile.getAbsoluteFile());
                }
            }
            if (this.updateFile.exists() && this.updateFile.isFile()) {
                log.d("Cleaning up temp file.");
                if (!this.updateFile.delete()) {
                    log.w("Could not remove update file from sd card. " + this.updateFile.getAbsoluteFile());
                }
            }
        }
        AppPreferences prefs = IMEApplication.from(this.connect.getContext()).getAppPreferences();
        this.isDownloaded = false;
        this.available = false;
        this.updateFile = null;
        prefs.setUpdateNotificationSent(false);
        prefs.setUpdateFilePath(null);
    }

    public void downloadComplete(File file) {
        this.updateFile = file;
        this.isDownloaded = true;
        this.downloadCallback = null;
        IMEApplication.from(this.connect.getContext()).getAppPreferences().setUpdateFilePath(file.getAbsolutePath());
        sendInstallAvailableNotification();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class PlatformUpdateDownloadCallbackWrapper implements ACPlatformUpdateService.ACPlatformUpdateDownloadCallback {
        private static final int MAX_RETRIES = 3;
        private boolean autoResume;
        ACPlatformUpdateService.ACPlatformUpdateDownloadCallback callback;
        private ConnectedStatus connection;
        final SDKUpdateManager parent;
        private int retryCount;
        boolean downloading = true;
        int progress = 0;

        PlatformUpdateDownloadCallbackWrapper(SDKUpdateManager updateManager, ACPlatformUpdateService.ACPlatformUpdateDownloadCallback callback) {
            this.parent = updateManager;
            this.callback = callback;
            this.connection = new ConnectedStatus(this.parent.connect.getContext()) { // from class: com.nuance.swype.connect.SDKUpdateManager.PlatformUpdateDownloadCallbackWrapper.1
                @Override // com.nuance.swype.connect.ConnectedStatus
                public void onConnectionChanged(boolean isConnected) {
                    SDKUpdateManager.log.d("onConnectionChanged(", Boolean.valueOf(isConnected), ")");
                    if (isConnected && PlatformUpdateDownloadCallbackWrapper.this.autoResume) {
                        PlatformUpdateDownloadCallbackWrapper.this.autoResume = false;
                        PlatformUpdateDownloadCallbackWrapper.this.parent.getUpdateService();
                        if (PlatformUpdateDownloadCallbackWrapper.this.parent.updateService != null) {
                            try {
                                PlatformUpdateDownloadCallbackWrapper.this.parent.updateService.downloadUpdate(PlatformUpdateDownloadCallbackWrapper.this);
                                return;
                            } catch (ACException e) {
                                ACPlatformUpdateService.ACPlatformUpdateDownloadCallback c = PlatformUpdateDownloadCallbackWrapper.this.callback;
                                PlatformUpdateDownloadCallbackWrapper.this.finish();
                                PlatformUpdateDownloadCallbackWrapper.this.progress = 0;
                                c.downloadFailed(7);
                                return;
                            }
                        }
                        ACPlatformUpdateService.ACPlatformUpdateDownloadCallback c2 = PlatformUpdateDownloadCallbackWrapper.this.callback;
                        PlatformUpdateDownloadCallbackWrapper.this.finish();
                        PlatformUpdateDownloadCallbackWrapper.this.progress = 0;
                        c2.downloadFailed(7);
                    }
                }
            };
            this.connection.register();
        }

        @Override // com.nuance.swypeconnect.ac.ACPlatformUpdateService.ACPlatformUpdateDownloadCallback
        public void downloadStopped(int reasonCode) {
            if (this.callback == null || !this.parent.showNoSpaceNotificationIfShortStorage()) {
                SDKUpdateManager.log.d("downloadStopped(", Integer.valueOf(reasonCode), ")");
                ACPlatformUpdateService.ACPlatformUpdateDownloadCallback c = this.callback;
                finish();
                this.progress = 0;
                if (c != null) {
                    c.downloadStopped(reasonCode);
                    return;
                }
                return;
            }
            SDKUpdateManager.log.d("downloadStopped: out of space");
            this.callback.downloadFailed(SDKUpdateManager.OUT_OF_SPACE_ERROR);
        }

        @Override // com.nuance.swypeconnect.ac.ACPlatformUpdateService.ACPlatformUpdateDownloadCallback
        public void downloadStarted() {
            SDKUpdateManager.log.d("downloadStarted()");
            this.autoResume = false;
            this.callback.downloadStarted();
        }

        @Override // com.nuance.swypeconnect.ac.ACPlatformUpdateService.ACPlatformUpdateDownloadCallback
        public void downloadPercentage(int percent) {
            SDKUpdateManager.log.d("downloadPercentage(", Integer.valueOf(percent), ")");
            if (this.callback == null || !this.parent.showNoSpaceNotificationIfShortStorage()) {
                this.parent.isOutOfSpace = false;
                this.progress = percent;
                this.autoResume = false;
                if (this.callback != null) {
                    this.callback.downloadPercentage(percent);
                    return;
                }
                return;
            }
            SDKUpdateManager.log.d("downloadPercentage: out of space");
            this.callback.downloadFailed(SDKUpdateManager.OUT_OF_SPACE_ERROR);
        }

        @Override // com.nuance.swypeconnect.ac.ACPlatformUpdateService.ACPlatformUpdateDownloadCallback
        public void downloadFailed(int reasonCode) {
            SDKUpdateManager.log.d("downloadFailed(", Integer.valueOf(reasonCode), ")");
            ACPlatformUpdateService.ACPlatformUpdateDownloadCallback c = this.callback;
            int i = this.retryCount;
            this.retryCount = i + 1;
            if (i < 3) {
                SDKUpdateManager.log.d("autoResume");
                this.autoResume = true;
                this.progress = 0;
                this.callback.downloadPercentage(this.progress);
                return;
            }
            finish();
            this.progress = 0;
            c.downloadFailed(reasonCode);
        }

        @Override // com.nuance.swypeconnect.ac.ACPlatformUpdateService.ACPlatformUpdateDownloadCallback
        public void downloadComplete(File file) {
            SDKUpdateManager.log.d("downloadComplete(", file.getAbsoluteFile(), ")");
            this.parent.downloadComplete(file);
            ACPlatformUpdateService.ACPlatformUpdateDownloadCallback c = this.callback;
            finish();
            c.downloadComplete(file);
        }

        public boolean isDownloading() {
            return this.downloading;
        }

        public int getProgress() {
            return this.progress;
        }

        public void cancel() {
            this.parent.getUpdateService();
            if (!this.autoResume) {
                if (this.parent.updateService != null) {
                    this.parent.updateService.cancelDownload();
                    return;
                }
                return;
            }
            downloadStopped(3);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void finish() {
            this.callback = null;
            this.downloading = false;
            this.autoResume = false;
            this.parent.isOutOfSpace = false;
            this.connection.unregister();
        }

        void updateCallback(ACPlatformUpdateService.ACPlatformUpdateDownloadCallback callback) {
            this.callback = callback;
        }
    }

    public boolean isDownloading() {
        return this.downloadCallback != null && this.downloadCallback.isDownloading();
    }

    public int getProgress() {
        if (this.downloadCallback != null) {
            return this.downloadCallback.getProgress();
        }
        return 0;
    }

    public void updateDownloadCallback(ACPlatformUpdateService.ACPlatformUpdateDownloadCallback callback) {
        if (this.downloadCallback != null) {
            this.downloadCallback.updateCallback(callback);
        }
    }

    private void sendUpgradeAvailableNotification() {
        log.d("sendUpgradeAvailableNotification() ", Boolean.valueOf(this.available), XMLResultsHandler.SEP_SPACE, Boolean.valueOf(this.updateNotificationSent));
        if (this.available && !this.updateNotificationSent) {
            log.d("sendUpgradeAvailableNotification() ", " sending...");
            NotificationCompat.Builder notification = new NotificationCompat.Builder(this.connect.getContext());
            Intent intent = new Intent(this.connect.getContext(), (Class<?>) UpdatesDispatch.class).setFlags(268435456);
            PendingIntent resultPendingIntent = PendingIntent.getActivity(this.connect.getContext(), 0, intent, 134217728);
            Resources res = this.connect.getContext().getResources();
            notification.setContentTitle(res.getString(R.string.update_download_title)).setContentText(res.getString(R.string.notification_language_update)).setSmallIcon(R.drawable.swype_icon).setContentIntent(resultPendingIntent).setAutoCancel(true);
            getNotificationManager().notify(UPDATE_NOTIFICATION_ID, notification.build());
            this.updateNotificationSent = true;
            IMEApplication.from(this.connect.getContext()).getAppPreferences().setUpdateNotificationSent(true);
        }
    }

    private void sendOutOfSpaceNotification() {
        log.d("sendOutOfSpaceNotification() ", " sending...");
        NotificationCompat.Builder notification = new NotificationCompat.Builder(this.connect.getContext());
        Intent intent = new Intent(this.connect.getContext(), (Class<?>) UpdatesDispatch.class).setFlags(268435456);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this.connect.getContext(), 0, intent, 134217728);
        Resources res = this.connect.getContext().getResources();
        notification.setContentTitle(res.getString(R.string.notification_default_title)).setContentText(res.getString(R.string.error_out_of_disc_space)).setSmallIcon(R.drawable.swype_icon).setContentIntent(resultPendingIntent).setAutoCancel(true);
        getNotificationManager().notify(UPDATE_NOTIFICATION_ID, notification.build());
    }

    private void sendInstallAvailableNotification() {
        log.d("sendInstallAvailableNotification() ", Boolean.valueOf(this.available));
        if (this.available && this.isDownloaded) {
            log.d("sendInstallAvailableNotification() ", " sending...");
            NotificationCompat.Builder notification = new NotificationCompat.Builder(this.connect.getContext());
            Intent intent = new Intent(this.connect.getContext(), (Class<?>) UpdatesDispatch.class).setFlags(268435456);
            PendingIntent resultPendingIntent = PendingIntent.getActivity(this.connect.getContext(), 0, intent, 134217728);
            Resources res = this.connect.getContext().getResources();
            notification.setContentTitle(res.getString(R.string.notification_default_title)).setContentText(res.getString(R.string.update_install_title)).setSmallIcon(R.drawable.swype_icon).setContentIntent(resultPendingIntent).setAutoCancel(true);
            getNotificationManager().notify(UPDATE_NOTIFICATION_ID, notification.build());
            this.updateNotificationSent = true;
            IMEApplication.from(this.connect.getContext()).getAppPreferences().setUpdateNotificationSent(true);
        }
    }

    private void sendSDCardNotification() {
        log.d("sendSDCardNotification() ", " sending...");
        NotificationCompat.Builder notification = new NotificationCompat.Builder(this.connect.getContext());
        Intent intent = new Intent(this.connect.getContext(), (Class<?>) UpdatesDispatch.class).setFlags(268435456);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this.connect.getContext(), 0, intent, 134217728);
        Resources res = this.connect.getContext().getResources();
        notification.setContentTitle(res.getString(R.string.notification_default_title)).setContentText(res.getString(R.string.upgrade_inserted)).setSmallIcon(R.drawable.swype_icon).setContentIntent(resultPendingIntent).setAutoCancel(true);
        getNotificationManager().notify(UPDATE_NOTIFICATION_ID, notification.build());
        this.updateNotificationSent = true;
        IMEApplication.from(this.connect.getContext()).getAppPreferences().setUpdateNotificationSent(true);
    }

    private NotificationManager getNotificationManager() {
        return (NotificationManager) this.connect.getContext().getSystemService("notification");
    }
}
