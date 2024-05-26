package com.flurry.sdk;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import com.flurry.sdk.ju;
import java.lang.ref.WeakReference;

/* loaded from: classes.dex */
public class jv {
    private static jv a;
    private static final String b = jv.class.getSimpleName();
    private Object c;

    public static synchronized jv a() {
        jv jvVar;
        synchronized (jv.class) {
            if (a == null) {
                a = new jv();
            }
            jvVar = a;
        }
        return jvVar;
    }

    private jv() {
        if (Build.VERSION.SDK_INT < 14 || this.c != null) {
            return;
        }
        Context context = jr.a().a;
        if (!(context instanceof Application)) {
            return;
        }
        this.c = new Application.ActivityLifecycleCallbacks() { // from class: com.flurry.sdk.jv.1
            @Override // android.app.Application.ActivityLifecycleCallbacks
            public final void onActivityCreated(Activity activity, Bundle bundle) {
                kf.a(3, jv.b, "onActivityCreated for activity:" + activity);
                a(activity, ju.a.kCreated);
            }

            @Override // android.app.Application.ActivityLifecycleCallbacks
            public final void onActivityStarted(Activity activity) {
                kf.a(3, jv.b, "onActivityStarted for activity:" + activity);
                a(activity, ju.a.kStarted);
            }

            @Override // android.app.Application.ActivityLifecycleCallbacks
            public final void onActivityResumed(Activity activity) {
                kf.a(3, jv.b, "onActivityResumed for activity:" + activity);
                a(activity, ju.a.kResumed);
            }

            @Override // android.app.Application.ActivityLifecycleCallbacks
            public final void onActivityPaused(Activity activity) {
                kf.a(3, jv.b, "onActivityPaused for activity:" + activity);
                a(activity, ju.a.kPaused);
            }

            @Override // android.app.Application.ActivityLifecycleCallbacks
            public final void onActivityStopped(Activity activity) {
                kf.a(3, jv.b, "onActivityStopped for activity:" + activity);
                a(activity, ju.a.kStopped);
            }

            @Override // android.app.Application.ActivityLifecycleCallbacks
            public final void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
                kf.a(3, jv.b, "onActivitySaveInstanceState for activity:" + activity);
                a(activity, ju.a.kSaveState);
            }

            @Override // android.app.Application.ActivityLifecycleCallbacks
            public final void onActivityDestroyed(Activity activity) {
                kf.a(3, jv.b, "onActivityDestroyed for activity:" + activity);
                a(activity, ju.a.kDestroyed);
            }

            private static void a(Activity activity, ju.a aVar) {
                ju juVar = new ju();
                juVar.a = new WeakReference<>(activity);
                juVar.b = aVar;
                juVar.b();
            }
        };
        ((Application) context).registerActivityLifecycleCallbacks((Application.ActivityLifecycleCallbacks) this.c);
    }

    public final boolean c() {
        return this.c != null;
    }
}
