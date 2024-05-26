package android.support.customtabs;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.customtabs.ICustomTabsService;

/* loaded from: classes.dex */
public abstract class CustomTabsServiceConnection implements ServiceConnection {
    public abstract void onCustomTabsServiceConnected$51e7d8cd(CustomTabsClient customTabsClient);

    @Override // android.content.ServiceConnection
    public final void onServiceConnected(ComponentName name, IBinder service) {
        onCustomTabsServiceConnected$51e7d8cd(new CustomTabsClient(ICustomTabsService.Stub.asInterface(service), name) { // from class: android.support.customtabs.CustomTabsServiceConnection.1
        });
    }
}
