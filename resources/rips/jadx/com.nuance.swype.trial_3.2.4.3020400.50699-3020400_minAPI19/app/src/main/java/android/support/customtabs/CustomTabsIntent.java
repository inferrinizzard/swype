package android.support.customtabs;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.BundleCompatDonut;
import android.util.Log;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

/* loaded from: classes.dex */
public final class CustomTabsIntent {
    public final Intent intent;
    public final Bundle startAnimationBundle;

    /* synthetic */ CustomTabsIntent(Intent x0, Bundle x1, byte b) {
        this(x0, x1);
    }

    private CustomTabsIntent(Intent intent, Bundle startAnimationBundle) {
        this.intent = intent;
        this.startAnimationBundle = startAnimationBundle;
    }

    /* loaded from: classes.dex */
    public static final class Builder {
        private ArrayList<Bundle> mActionButtons;
        private final Intent mIntent;
        private ArrayList<Bundle> mMenuItems;
        private Bundle mStartAnimationBundle;

        public Builder() {
            this(null);
        }

        public Builder(CustomTabsSession session) {
            this.mIntent = new Intent("android.intent.action.VIEW");
            this.mMenuItems = null;
            this.mStartAnimationBundle = null;
            this.mActionButtons = null;
            if (session != null) {
                this.mIntent.setPackage(session.mComponentName.getPackageName());
            }
            Bundle bundle = new Bundle();
            IBinder asBinder = session == null ? null : session.mCallback.asBinder();
            if (Build.VERSION.SDK_INT >= 18) {
                bundle.putBinder("android.support.customtabs.extra.SESSION", asBinder);
            } else {
                if (!BundleCompatDonut.sPutIBinderMethodFetched) {
                    try {
                        Method method = Bundle.class.getMethod("putIBinder", String.class, IBinder.class);
                        BundleCompatDonut.sPutIBinderMethod = method;
                        method.setAccessible(true);
                    } catch (NoSuchMethodException e) {
                        Log.i("BundleCompatDonut", "Failed to retrieve putIBinder method", e);
                    }
                    BundleCompatDonut.sPutIBinderMethodFetched = true;
                }
                if (BundleCompatDonut.sPutIBinderMethod != null) {
                    try {
                        BundleCompatDonut.sPutIBinderMethod.invoke(bundle, "android.support.customtabs.extra.SESSION", asBinder);
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e2) {
                        Log.i("BundleCompatDonut", "Failed to invoke putIBinder via reflection", e2);
                        BundleCompatDonut.sPutIBinderMethod = null;
                    }
                }
            }
            this.mIntent.putExtras(bundle);
        }

        public final CustomTabsIntent build() {
            if (this.mMenuItems != null) {
                this.mIntent.putParcelableArrayListExtra("android.support.customtabs.extra.MENU_ITEMS", this.mMenuItems);
            }
            if (this.mActionButtons != null) {
                this.mIntent.putParcelableArrayListExtra("android.support.customtabs.extra.TOOLBAR_ITEMS", this.mActionButtons);
            }
            return new CustomTabsIntent(this.mIntent, this.mStartAnimationBundle, (byte) 0);
        }
    }
}
