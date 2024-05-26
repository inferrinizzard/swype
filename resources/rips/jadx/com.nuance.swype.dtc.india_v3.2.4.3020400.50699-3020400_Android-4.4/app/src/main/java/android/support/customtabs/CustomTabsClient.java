package android.support.customtabs;

import android.content.ComponentName;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.customtabs.ICustomTabsCallback;

/* loaded from: classes.dex */
public class CustomTabsClient {
    private final ICustomTabsService mService;
    private final ComponentName mServiceComponentName;

    /* JADX INFO: Access modifiers changed from: package-private */
    public CustomTabsClient(ICustomTabsService service, ComponentName componentName) {
        this.mService = service;
        this.mServiceComponentName = componentName;
    }

    public final boolean warmup$1349f3() {
        try {
            return this.mService.warmup(0L);
        } catch (RemoteException e) {
            return false;
        }
    }

    public final CustomTabsSession newSession$6f4c7b26() {
        ICustomTabsCallback.Stub wrapper = new ICustomTabsCallback.Stub() { // from class: android.support.customtabs.CustomTabsClient.1
            final /* synthetic */ CustomTabsCallback val$callback = null;

            @Override // android.support.customtabs.ICustomTabsCallback
            public final void onNavigationEvent(int navigationEvent, Bundle extras) {
                if (this.val$callback != null) {
                    this.val$callback.onNavigationEvent(navigationEvent, extras);
                }
            }

            @Override // android.support.customtabs.ICustomTabsCallback
            public final void extraCallback(String callbackName, Bundle args) throws RemoteException {
            }
        };
        try {
            if (this.mService.newSession(wrapper)) {
                return new CustomTabsSession(this.mService, wrapper, this.mServiceComponentName);
            }
            return null;
        } catch (RemoteException e) {
            return null;
        }
    }
}
