package android.support.customtabs;

import android.content.ComponentName;
import android.net.Uri;
import android.os.RemoteException;

/* loaded from: classes.dex */
public final class CustomTabsSession {
    final ICustomTabsCallback mCallback;
    final ComponentName mComponentName;
    private final ICustomTabsService mService;

    /* JADX INFO: Access modifiers changed from: package-private */
    public CustomTabsSession(ICustomTabsService service, ICustomTabsCallback callback, ComponentName componentName) {
        this.mService = service;
        this.mCallback = callback;
        this.mComponentName = componentName;
    }

    public final boolean mayLaunchUrl$31eb0de9(Uri url) {
        try {
            return this.mService.mayLaunchUrl(this.mCallback, url, null, null);
        } catch (RemoteException e) {
            return false;
        }
    }
}
