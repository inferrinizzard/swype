package android.support.v4.app;

import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;

/* loaded from: classes.dex */
public final class BundleCompat {
    public static IBinder getBinder(Bundle bundle, String key) {
        return Build.VERSION.SDK_INT >= 18 ? bundle.getBinder(key) : BundleCompatDonut.getBinder(bundle, key);
    }
}
