package com.nuance.connect.internal;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.nuance.connect.api.PlatformUpdateService;
import com.nuance.connect.common.ConnectFeature;
import com.nuance.connect.common.Strings;
import com.nuance.connect.internal.common.APIHandlers;
import com.nuance.connect.internal.common.InternalMessages;
import com.nuance.connect.util.ConcurrentCallbackSet;
import com.nuance.connect.util.Logger;
import java.io.File;

/* loaded from: classes.dex */
public class PlatformUpdateServiceInternal extends AbstractService implements PlatformUpdateService {
    private static final InternalMessages[] MESSAGE_IDS = {InternalMessages.MESSAGE_HOST_CATEGORY_PLATFORM_UPDATE_AVAILABLE, InternalMessages.MESSAGE_HOST_CATEGORY_PLATFORM_UPDATE_INSTALL_READY, InternalMessages.MESSAGE_HOST_CATEGORY_PLATFORM_UPDATE_PROGRESS, InternalMessages.MESSAGE_HOST_CATEGORY_PLATFORM_UPDATE_CANCEL_ACK, InternalMessages.MESSAGE_HOST_CATEGORY_PLATFORM_UPDATE_DOWNLOAD_FAILED};
    private ConnectServiceManagerInternal connectService;
    private PlatformUpdateService.DownloadCallback downloadCallback;
    private boolean updateAvailable;
    private String updateCategoryId;
    private final ConcurrentCallbackSet<PlatformUpdateService.UpdateCallback> callbacks = new ConcurrentCallbackSet<>();
    private final Logger.Log log = Logger.getLog(Logger.LoggerType.DEVELOPER);
    private ConnectHandler handler = new ConnectHandler() { // from class: com.nuance.connect.internal.PlatformUpdateServiceInternal.1
        @Override // com.nuance.connect.internal.ConnectHandler
        public String getHandlerName() {
            return APIHandlers.UPGRADE_HANDLER;
        }

        @Override // com.nuance.connect.internal.ConnectHandler
        public int[] getMessageIDs() {
            int[] iArr = new int[PlatformUpdateServiceInternal.MESSAGE_IDS.length];
            for (int i = 0; i < PlatformUpdateServiceInternal.MESSAGE_IDS.length; i++) {
                iArr[i] = PlatformUpdateServiceInternal.MESSAGE_IDS[i].ordinal();
            }
            return iArr;
        }

        @Override // com.nuance.connect.internal.ConnectHandler
        public void handleMessage(Handler handler, Message message) {
            PlatformUpdateServiceInternal.this.handleMessage(handler, message);
        }

        @Override // com.nuance.connect.internal.ConnectHandler
        public void onPostUpgrade() {
        }
    };
    private final Handler messageHandler = new Handler(Looper.getMainLooper());

    /* JADX INFO: Access modifiers changed from: package-private */
    public PlatformUpdateServiceInternal(ConnectServiceManagerInternal connectServiceManagerInternal) {
        this.connectService = connectServiceManagerInternal;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleMessage(Handler handler, Message message) {
        switch (InternalMessages.fromInt(message.what)) {
            case MESSAGE_HOST_CATEGORY_PLATFORM_UPDATE_AVAILABLE:
                this.updateCategoryId = message.getData().getString(Strings.DEFAULT_KEY);
                if (this.updateCategoryId != null) {
                    this.updateAvailable = true;
                    for (PlatformUpdateService.UpdateCallback updateCallback : (PlatformUpdateService.UpdateCallback[]) this.callbacks.toArray(new PlatformUpdateService.UpdateCallback[0])) {
                        updateCallback.updateAvailable();
                    }
                    return;
                }
                return;
            case MESSAGE_HOST_CATEGORY_PLATFORM_UPDATE_INSTALL_READY:
                this.log.d("MESSAGE_HOST_CATEGORY_PLATFORM_UPDATE_INSTALL_READY");
                if (this.downloadCallback != null) {
                    this.downloadCallback.downloadComplete(new File(message.getData().getString(Strings.DEFAULT_KEY)));
                    return;
                }
                return;
            case MESSAGE_HOST_CATEGORY_PLATFORM_UPDATE_PROGRESS:
                this.log.d("MESSAGE_HOST_CATEGORY_PLATFORM_UPDATE_PROGRESS");
                int i = message.getData().getInt(Strings.DEFAULT_KEY);
                if (this.downloadCallback != null) {
                    if (i == 0) {
                        this.downloadCallback.downloadStarted();
                        return;
                    } else {
                        this.downloadCallback.downloadPercentage(i);
                        return;
                    }
                }
                return;
            case MESSAGE_HOST_CATEGORY_PLATFORM_UPDATE_CANCEL_ACK:
                this.log.d("MESSAGE_HOST_CATEGORY_PLATFORM_UPDATE_CANCEL_ACK");
                if (this.downloadCallback != null) {
                    this.downloadCallback.downloadStopped(3);
                    return;
                }
                return;
            case MESSAGE_HOST_CATEGORY_PLATFORM_UPDATE_DOWNLOAD_FAILED:
                this.log.d("MESSAGE_HOST_CATEGORY_PLATFORM_UPDATE_DOWNLOAD_FAILED");
                if (this.downloadCallback != null) {
                    this.downloadCallback.downloadFailed(message.getData().getInt(Strings.DEFAULT_KEY));
                    return;
                }
                return;
            default:
                return;
        }
    }

    @Override // com.nuance.connect.api.PlatformUpdateService
    public void cancelDownload() {
        this.log.d("cancelDownload()");
        if (!this.updateAvailable || this.downloadCallback == null) {
            return;
        }
        this.connectService.getBinder().sendConnectMessage(InternalMessages.MESSAGE_CLIENT_CATEGORY_PLATFORM_UPDATE_CANCEL, this.updateCategoryId);
    }

    @Override // com.nuance.connect.api.PlatformUpdateService
    public void downloadUpdate(PlatformUpdateService.DownloadCallback downloadCallback) {
        this.downloadCallback = downloadCallback;
        if (this.updateAvailable) {
            this.connectService.getBinder().sendConnectPriorityMessage(InternalMessages.MESSAGE_CLIENT_CATEGORY_PLATFORM_UPDATE_DOWNLOAD, this.updateCategoryId);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.connect.internal.AbstractService
    public ConnectFeature[] getDependencies() {
        return ConnectFeature.UPDATE.values();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.connect.internal.AbstractService
    public ConnectHandler[] getHandlers() {
        return new ConnectHandler[]{this.handler};
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.connect.internal.AbstractService
    public String getServiceName() {
        return ConnectFeature.UPDATE.name();
    }

    @Override // com.nuance.connect.api.PlatformUpdateService
    public boolean isAvailable() {
        return this.updateAvailable;
    }

    @Override // com.nuance.connect.api.PlatformUpdateService
    public void registerCallback(PlatformUpdateService.UpdateCallback updateCallback) {
        if (this.updateAvailable) {
            updateCallback.updateAvailable();
        }
        this.callbacks.add(updateCallback);
    }

    @Override // com.nuance.connect.api.PlatformUpdateService
    public void unregisterCallback(PlatformUpdateService.DownloadCallback downloadCallback) {
        if (this.downloadCallback == null || !this.downloadCallback.equals(downloadCallback)) {
            return;
        }
        this.downloadCallback = null;
    }

    @Override // com.nuance.connect.api.PlatformUpdateService
    public void unregisterCallback(PlatformUpdateService.UpdateCallback updateCallback) {
        this.callbacks.remove(updateCallback);
    }

    @Override // com.nuance.connect.api.PlatformUpdateService
    public void unregisterCallbacks() {
        this.callbacks.clear();
    }
}
