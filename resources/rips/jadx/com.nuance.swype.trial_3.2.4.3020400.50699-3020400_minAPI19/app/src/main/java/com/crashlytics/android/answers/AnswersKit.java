package com.crashlytics.android.answers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Looper;
import com.crashlytics.android.answers.SessionEvent;
import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.Kit;
import io.fabric.sdk.android.services.common.Crash;
import io.fabric.sdk.android.services.persistence.PreferenceStore;
import io.fabric.sdk.android.services.persistence.PreferenceStoreImpl;
import java.io.File;
import java.util.Collections;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public abstract class AnswersKit extends Kit<Boolean> {
    private long installedAt;
    private PreferenceStore preferenceStore;
    SessionAnalyticsManager sessionAnalyticsManager;
    private String versionCode;
    private String versionName;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // io.fabric.sdk.android.Kit
    @SuppressLint({"NewApi"})
    public final boolean onPreExecute() {
        try {
            this.preferenceStore = new PreferenceStoreImpl(this);
            Context context = this.context;
            PackageManager packageManager = context.getPackageManager();
            String packageName = context.getPackageName();
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
            this.versionCode = Integer.toString(packageInfo.versionCode);
            this.versionName = packageInfo.versionName == null ? "0.0" : packageInfo.versionName;
            if (Build.VERSION.SDK_INT >= 9) {
                this.installedAt = packageInfo.firstInstallTime;
            } else {
                ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), 0);
                this.installedAt = new File(appInfo.sourceDir).lastModified();
            }
            return true;
        } catch (Exception e) {
            Fabric.getLogger().e("Answers", "Error setting up app properties", e);
            return false;
        }
    }

    @Override // io.fabric.sdk.android.Kit
    public String getIdentifier() {
        return "com.crashlytics.sdk.android:answers";
    }

    @Override // io.fabric.sdk.android.Kit
    public String getVersion() {
        return "1.1.2.37";
    }

    public void onException(Crash.FatalException exception) {
        if (this.sessionAnalyticsManager != null) {
            SessionAnalyticsManager sessionAnalyticsManager = this.sessionAnalyticsManager;
            String str = exception.sessionId;
            if (Looper.myLooper() == Looper.getMainLooper()) {
                throw new IllegalStateException("onCrash called from main thread!!!");
            }
            sessionAnalyticsManager.eventsHandler.recordEventSync(SessionEvent.buildEvent(sessionAnalyticsManager.metadata, SessionEvent.Type.CRASH, Collections.singletonMap("sessionId", str)));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:16:0x00da A[Catch: Exception -> 0x0143, TRY_LEAVE, TryCatch #1 {Exception -> 0x0143, blocks: (B:3:0x0004, B:5:0x0084, B:7:0x008a, B:8:0x00b1, B:10:0x00c7, B:16:0x00da, B:35:0x011a), top: B:2:0x0004 }] */
    /* JADX WARN: Removed duplicated region for block: B:21:0x0114 A[Catch: Exception -> 0x0183, TRY_LEAVE, TryCatch #0 {Exception -> 0x0183, blocks: (B:19:0x010a, B:21:0x0114, B:24:0x014f, B:26:0x0157, B:28:0x0173), top: B:18:0x010a }] */
    /* JADX WARN: Removed duplicated region for block: B:24:0x014f A[Catch: Exception -> 0x0183, TRY_ENTER, TryCatch #0 {Exception -> 0x0183, blocks: (B:19:0x010a, B:21:0x0114, B:24:0x014f, B:26:0x0157, B:28:0x0173), top: B:18:0x010a }] */
    @Override // io.fabric.sdk.android.Kit
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public java.lang.Boolean doInBackground() {
        /*
            Method dump skipped, instructions count: 407
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.crashlytics.android.answers.AnswersKit.doInBackground():java.lang.Boolean");
    }
}
