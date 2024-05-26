package io.fabric.sdk.android;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import java.util.HashSet;
import java.util.Set;

/* loaded from: classes.dex */
public final class ActivityLifecycleManager {
    private final Application application;
    ActivityLifecycleCallbacksWrapper callbacksWrapper;

    /* loaded from: classes.dex */
    public static abstract class Callbacks {
        public void onActivityCreated$9bb446d(Activity activity) {
        }

        public void onActivityStarted(Activity activity) {
        }

        public void onActivityResumed(Activity activity) {
        }
    }

    public ActivityLifecycleManager(Context context) {
        this.application = (Application) context.getApplicationContext();
        if (Build.VERSION.SDK_INT >= 14) {
            this.callbacksWrapper = new ActivityLifecycleCallbacksWrapper(this.application);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class ActivityLifecycleCallbacksWrapper {
        final Application application;
        final Set<Application.ActivityLifecycleCallbacks> registeredCallbacks = new HashSet();

        ActivityLifecycleCallbacksWrapper(Application application) {
            this.application = application;
        }
    }
}
