package android.support.customtabs;

import android.app.Service;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.customtabs.ICustomTabsService;
import android.support.v4.util.ArrayMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/* loaded from: classes.dex */
public abstract class CustomTabsService extends Service {
    private final Map<IBinder, IBinder.DeathRecipient> mDeathRecipientMap = new ArrayMap();
    private ICustomTabsService.Stub mBinder = new ICustomTabsService.Stub() { // from class: android.support.customtabs.CustomTabsService.1
        @Override // android.support.customtabs.ICustomTabsService
        public final boolean warmup(long flags) {
            return CustomTabsService.this.warmup$1349f3();
        }

        @Override // android.support.customtabs.ICustomTabsService
        public final boolean newSession(ICustomTabsCallback callback) {
            final CustomTabsSessionToken sessionToken = new CustomTabsSessionToken(callback);
            try {
                IBinder.DeathRecipient deathRecipient = new IBinder.DeathRecipient() { // from class: android.support.customtabs.CustomTabsService.1.1
                    @Override // android.os.IBinder.DeathRecipient
                    public final void binderDied() {
                        CustomTabsService.this.cleanUpSession(sessionToken);
                    }
                };
                synchronized (CustomTabsService.this.mDeathRecipientMap) {
                    callback.asBinder().linkToDeath(deathRecipient, 0);
                    CustomTabsService.this.mDeathRecipientMap.put(callback.asBinder(), deathRecipient);
                }
                return CustomTabsService.this.newSession$26d819e();
            } catch (RemoteException e) {
                return false;
            }
        }

        @Override // android.support.customtabs.ICustomTabsService
        public final boolean mayLaunchUrl(ICustomTabsCallback callback, Uri url, Bundle extras, List<Bundle> otherLikelyBundles) {
            CustomTabsService customTabsService = CustomTabsService.this;
            new CustomTabsSessionToken(callback);
            return customTabsService.mayLaunchUrl$28898044();
        }

        @Override // android.support.customtabs.ICustomTabsService
        public final Bundle extraCommand(String commandName, Bundle args) {
            return CustomTabsService.this.extraCommand$5f15a07();
        }

        @Override // android.support.customtabs.ICustomTabsService
        public final boolean updateVisuals(ICustomTabsCallback callback, Bundle bundle) {
            CustomTabsService customTabsService = CustomTabsService.this;
            new CustomTabsSessionToken(callback);
            return customTabsService.updateVisuals$7708c702();
        }
    };

    protected abstract Bundle extraCommand$5f15a07();

    protected abstract boolean mayLaunchUrl$28898044();

    protected abstract boolean newSession$26d819e();

    protected abstract boolean updateVisuals$7708c702();

    protected abstract boolean warmup$1349f3();

    protected final boolean cleanUpSession(CustomTabsSessionToken sessionToken) {
        try {
            synchronized (this.mDeathRecipientMap) {
                IBinder binder = sessionToken.getCallbackBinder();
                IBinder.DeathRecipient deathRecipient = this.mDeathRecipientMap.get(binder);
                binder.unlinkToDeath(deathRecipient, 0);
                this.mDeathRecipientMap.remove(binder);
            }
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }
}
