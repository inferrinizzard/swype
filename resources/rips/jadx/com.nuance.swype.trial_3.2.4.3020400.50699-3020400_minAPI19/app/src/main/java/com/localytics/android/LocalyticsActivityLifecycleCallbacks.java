package com.localytics.android;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

@TargetApi(14)
/* loaded from: classes.dex */
public class LocalyticsActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {
    public LocalyticsActivityLifecycleCallbacks(Context context) {
        this(context, null);
    }

    public LocalyticsActivityLifecycleCallbacks(Context context, String localyticsKey) {
        Localytics.integrate(context, localyticsKey);
        Localytics.setIsAutoIntegrate(true);
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivityStarted(Activity activity) {
        if (!Localytics.isAppInForeground()) {
            Localytics.openSession();
            Localytics.upload();
        }
        Localytics.incrementActivityCounter();
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivityResumed(Activity activity) {
        Localytics.setInAppMessageDisplayActivity(activity);
        Localytics.handleTestMode(activity.getIntent());
        Localytics.handlePushNotificationOpened(activity.getIntent());
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivityPaused(Activity activity) {
        Localytics.dismissCurrentInAppMessage();
        Localytics.clearInAppMessageDisplayActivity();
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivityStopped(Activity activity) {
        Localytics.decrementActivityCounter();
        if (!Localytics.isAppInForeground()) {
            Localytics.closeSession();
            Localytics.upload();
        }
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivityDestroyed(Activity activity) {
    }
}
