package com.nuance.swypeconnect.ac;

import android.os.Bundle;
import com.nuance.connect.api.ConfigService;
import com.nuance.connect.common.ActionFilterStrings;
import com.nuance.connect.util.ActionDelegateCallback;
import com.nuance.connect.util.ActionFilter;
import com.nuance.connect.util.ConcurrentCallbackSet;
import com.nuance.connect.util.ConnectAction;
import com.nuance.connect.util.Logger;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;

/* loaded from: classes.dex */
public final class ACDeviceService extends ACService {
    private ConfigService configService;
    private WeakReference<ACManager> managerRef;
    private final ConcurrentCallbackSet<ACDeviceCallback> deviceCallbacks = new ConcurrentCallbackSet<>();
    private ActionFilter swyperIdFilter = new ActionFilter(ActionFilterStrings.ACTION_DATA_AVAILABLE, ActionFilterStrings.TYPE_SWYPER_ID);
    private ActionFilter deviceIdFilter = new ActionFilter(ActionFilterStrings.ACTION_DATA_AVAILABLE, ActionFilterStrings.TYPE_DEVICE_ID);
    private ActionDelegateCallback infoCallback = new ActionDelegateCallback() { // from class: com.nuance.swypeconnect.ac.ACDeviceService.1
        @Override // com.nuance.connect.util.ActionDelegateCallback
        public Bundle handle(ConnectAction connectAction) {
            if (connectAction.getFilter().matches(ACDeviceService.this.swyperIdFilter)) {
                ACDeviceService.this.notifyCallbacksOfSwyperId();
                return null;
            }
            if (!connectAction.getFilter().matches(ACDeviceService.this.deviceIdFilter)) {
                return null;
            }
            ACDeviceService.this.notifyCallbacksOfDeviceId();
            return null;
        }
    };

    /* loaded from: classes.dex */
    public interface ACDeviceCallback {
        void deviceRegistered(String str);

        void swyperId(String str);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ACDeviceService(ACManager aCManager, ConfigService configService) {
        this.configService = configService;
        this.managerRef = new WeakReference<>(aCManager);
        if (configService.getSwyperId() == null || configService.getSwyperId().length() <= 0) {
            return;
        }
        Logger.getLog(Logger.LoggerType.CUSTOMER).i("Swyper Id: ", configService.getSwyperId());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyCallbacksOfDeviceId() {
        Iterator<ACDeviceCallback> it = this.deviceCallbacks.iterator();
        while (it.hasNext()) {
            it.next().deviceRegistered(this.configService.getDeviceId());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyCallbacksOfSwyperId() {
        if (this.configService.getSwyperId() != null && this.configService.getSwyperId().length() > 0) {
            Logger.getLog(Logger.LoggerType.CUSTOMER).i("Swyper Id: ", this.configService.getSwyperId());
        }
        Iterator<ACDeviceCallback> it = this.deviceCallbacks.iterator();
        while (it.hasNext()) {
            it.next().swyperId(this.configService.getSwyperId());
        }
    }

    public final String getDeviceId() {
        return this.configService.getDeviceId();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swypeconnect.ac.ACService
    public final String getName() {
        return ACManager.DEVICE_SERVICE;
    }

    public final String getSwyperId() {
        return this.configService.getSwyperId();
    }

    public final void registerCallback(ACDeviceCallback aCDeviceCallback) {
        ACManager aCManager;
        if (this.deviceCallbacks.isEmpty() && (aCManager = this.managerRef.get()) != null && aCManager.getConnect() != null) {
            ArrayList arrayList = new ArrayList();
            arrayList.add(this.swyperIdFilter);
            arrayList.add(this.deviceIdFilter);
            aCManager.getConnect().registerActionCallback(this.infoCallback, arrayList);
        }
        this.deviceCallbacks.add(aCDeviceCallback);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.swypeconnect.ac.ACService
    public final boolean requiresDocument(int i) {
        return false;
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

    public final void unregisterCallback(ACDeviceCallback aCDeviceCallback) {
        ACManager aCManager;
        this.deviceCallbacks.remove(aCDeviceCallback);
        if (!this.deviceCallbacks.isEmpty() || (aCManager = this.managerRef.get()) == null || aCManager.getConnect() == null) {
            return;
        }
        aCManager.getConnect().unregisterActionCallback(this.infoCallback);
    }

    public final void unregisterCallbacks() {
        this.deviceCallbacks.clear();
    }
}
