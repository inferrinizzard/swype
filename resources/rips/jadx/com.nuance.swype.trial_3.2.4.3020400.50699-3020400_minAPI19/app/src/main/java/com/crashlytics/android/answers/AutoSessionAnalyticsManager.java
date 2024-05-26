package com.crashlytics.android.answers;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import com.crashlytics.android.answers.SessionEvent;
import io.fabric.sdk.android.services.common.CommonUtils;

/* JADX INFO: Access modifiers changed from: package-private */
@TargetApi(14)
/* loaded from: classes.dex */
public final class AutoSessionAnalyticsManager extends SessionAnalyticsManager {
    private final Application.ActivityLifecycleCallbacks activityLifecycleCallbacks;
    private final Application application;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AutoSessionAnalyticsManager(SessionEventMetadata metadata, SessionEventsHandler eventsHandler, Application application) {
        super(metadata, eventsHandler);
        this.activityLifecycleCallbacks = new Application.ActivityLifecycleCallbacks() { // from class: com.crashlytics.android.answers.AutoSessionAnalyticsManager.1
            @Override // android.app.Application.ActivityLifecycleCallbacks
            public final void onActivityCreated(Activity activity, Bundle bundle) {
                AutoSessionAnalyticsManager autoSessionAnalyticsManager = AutoSessionAnalyticsManager.this;
                autoSessionAnalyticsManager.eventsHandler.recordEventAsync(SessionEvent.buildActivityLifecycleEvent(autoSessionAnalyticsManager.metadata, SessionEvent.Type.CREATE, activity), false);
            }

            @Override // android.app.Application.ActivityLifecycleCallbacks
            public final void onActivityDestroyed(Activity activity) {
                AutoSessionAnalyticsManager autoSessionAnalyticsManager = AutoSessionAnalyticsManager.this;
                autoSessionAnalyticsManager.eventsHandler.recordEventAsync(SessionEvent.buildActivityLifecycleEvent(autoSessionAnalyticsManager.metadata, SessionEvent.Type.DESTROY, activity), false);
            }

            @Override // android.app.Application.ActivityLifecycleCallbacks
            public final void onActivityPaused(Activity activity) {
                AutoSessionAnalyticsManager autoSessionAnalyticsManager = AutoSessionAnalyticsManager.this;
                autoSessionAnalyticsManager.eventsHandler.recordEventAsync(SessionEvent.buildActivityLifecycleEvent(autoSessionAnalyticsManager.metadata, SessionEvent.Type.PAUSE, activity), false);
            }

            @Override // android.app.Application.ActivityLifecycleCallbacks
            public final void onActivityResumed(Activity activity) {
                AutoSessionAnalyticsManager autoSessionAnalyticsManager = AutoSessionAnalyticsManager.this;
                autoSessionAnalyticsManager.eventsHandler.recordEventAsync(SessionEvent.buildActivityLifecycleEvent(autoSessionAnalyticsManager.metadata, SessionEvent.Type.RESUME, activity), false);
            }

            @Override // android.app.Application.ActivityLifecycleCallbacks
            public final void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
                AutoSessionAnalyticsManager autoSessionAnalyticsManager = AutoSessionAnalyticsManager.this;
                autoSessionAnalyticsManager.eventsHandler.recordEventAsync(SessionEvent.buildActivityLifecycleEvent(autoSessionAnalyticsManager.metadata, SessionEvent.Type.SAVE_INSTANCE_STATE, activity), false);
            }

            @Override // android.app.Application.ActivityLifecycleCallbacks
            public final void onActivityStarted(Activity activity) {
                AutoSessionAnalyticsManager autoSessionAnalyticsManager = AutoSessionAnalyticsManager.this;
                autoSessionAnalyticsManager.eventsHandler.recordEventAsync(SessionEvent.buildActivityLifecycleEvent(autoSessionAnalyticsManager.metadata, SessionEvent.Type.START, activity), false);
            }

            @Override // android.app.Application.ActivityLifecycleCallbacks
            public final void onActivityStopped(Activity activity) {
                AutoSessionAnalyticsManager autoSessionAnalyticsManager = AutoSessionAnalyticsManager.this;
                autoSessionAnalyticsManager.eventsHandler.recordEventAsync(SessionEvent.buildActivityLifecycleEvent(autoSessionAnalyticsManager.metadata, SessionEvent.Type.STOP, activity), false);
            }
        };
        this.application = application;
        CommonUtils.logControlled$5ffc00fd(Answers.getInstance().context);
        application.registerActivityLifecycleCallbacks(this.activityLifecycleCallbacks);
    }

    @Override // com.crashlytics.android.answers.SessionAnalyticsManager
    public final void disable() {
        CommonUtils.logControlled$5ffc00fd(Answers.getInstance().context);
        this.application.unregisterActivityLifecycleCallbacks(this.activityLifecycleCallbacks);
        super.disable();
    }
}
