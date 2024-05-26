package android.support.customtabs;

import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

/* loaded from: classes.dex */
public final class CustomTabsSessionToken {
    private final CustomTabsCallback mCallback = new CustomTabsCallback() { // from class: android.support.customtabs.CustomTabsSessionToken.1
        @Override // android.support.customtabs.CustomTabsCallback
        public final void onNavigationEvent(int navigationEvent, Bundle extras) {
            try {
                CustomTabsSessionToken.this.mCallbackBinder.onNavigationEvent(navigationEvent, extras);
            } catch (RemoteException e) {
                Log.e("CustomTabsSessionToken", "RemoteException during ICustomTabsCallback transaction");
            }
        }
    };
    final ICustomTabsCallback mCallbackBinder;

    /* JADX INFO: Access modifiers changed from: package-private */
    public CustomTabsSessionToken(ICustomTabsCallback callbackBinder) {
        this.mCallbackBinder = callbackBinder;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final IBinder getCallbackBinder() {
        return this.mCallbackBinder.asBinder();
    }

    public final int hashCode() {
        return getCallbackBinder().hashCode();
    }

    public final boolean equals(Object o) {
        if (o instanceof CustomTabsSessionToken) {
            return ((CustomTabsSessionToken) o).getCallbackBinder().equals(this.mCallbackBinder.asBinder());
        }
        return false;
    }
}
