package com.crashlytics.android;

import android.app.Activity;
import android.content.Context;
import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.services.common.ApiKey;
import io.fabric.sdk.android.services.common.BackgroundPriorityRunnable;
import io.fabric.sdk.android.services.settings.Settings;
import io.fabric.sdk.android.services.settings.SettingsData;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class ReportUploader {
    private final CreateReportSpiCall createReportCall;
    private final Object fileAccessLock = new Object();
    private Thread uploadThread;
    private static final FilenameFilter crashFileFilter = new FilenameFilter() { // from class: com.crashlytics.android.ReportUploader.1
        @Override // java.io.FilenameFilter
        public final boolean accept(File dir, String filename) {
            return filename.endsWith(".cls") && !filename.contains("Session");
        }
    };
    static final Map<String, String> HEADER_INVALID_CLS_FILE = Collections.singletonMap("X-CRASHLYTICS-INVALID-SESSION", "1");
    private static final short[] RETRY_INTERVALS = {10, 20, 30, 60, 120, 300};

    static /* synthetic */ Thread access$002$9bb8a18(ReportUploader x0) {
        x0.uploadThread = null;
        return null;
    }

    public ReportUploader(CreateReportSpiCall createReportCall) {
        if (createReportCall == null) {
            throw new IllegalArgumentException("createReportCall must not be null.");
        }
        this.createReportCall = createReportCall;
    }

    public final synchronized void uploadReports(float delay) {
        if (this.uploadThread == null) {
            this.uploadThread = new Thread(new Worker(delay), "Crashlytics Report Uploader");
            this.uploadThread.start();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean forceUpload(Report report) {
        boolean removed = false;
        synchronized (this.fileAccessLock) {
            try {
                Context context = Crashlytics.getInstance().context;
                new ApiKey();
                CreateReportRequest requestData = new CreateReportRequest(ApiKey.getValue(context), report);
                boolean sent = this.createReportCall.invoke(requestData);
                Fabric.getLogger().i("Fabric", "Crashlytics report upload " + (sent ? "complete: " : "FAILED: ") + report.getFileName());
                if (sent) {
                    report.remove();
                    removed = true;
                }
            } catch (Exception e) {
                Fabric.getLogger().e("Fabric", "Error occurred sending report " + report, e);
            }
        }
        return removed;
    }

    final List<Report> findReports() {
        File[] clsFiles;
        Fabric.getLogger();
        synchronized (this.fileAccessLock) {
            clsFiles = Crashlytics.getInstance().getSdkDirectory().listFiles(crashFileFilter);
        }
        List<Report> reports = new LinkedList<>();
        for (File file : clsFiles) {
            Fabric.getLogger();
            new StringBuilder("Found crash report ").append(file.getPath());
            reports.add(new SessionReport(file));
        }
        if (reports.isEmpty()) {
            Fabric.getLogger();
        }
        return reports;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class Worker extends BackgroundPriorityRunnable {
        private final float delay;

        Worker(float delay) {
            this.delay = delay;
        }

        @Override // io.fabric.sdk.android.services.common.BackgroundPriorityRunnable
        public final void onRun() {
            Settings settings;
            try {
                Fabric.getLogger();
                new StringBuilder("Starting report processing in ").append(this.delay).append(" second(s)...");
                if (this.delay > 0.0f) {
                    try {
                        Thread.sleep(this.delay * 1000.0f);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                final Crashlytics crashlytics = Crashlytics.getInstance();
                CrashlyticsUncaughtExceptionHandler crashlyticsUncaughtExceptionHandler = crashlytics.handler;
                List<Report> findReports = ReportUploader.this.findReports();
                if (!crashlyticsUncaughtExceptionHandler.isHandlingException.get()) {
                    if (!findReports.isEmpty()) {
                        settings = Settings.LazyHolder.INSTANCE;
                        if (!((Boolean) settings.withSettings(new Settings.SettingsAccess<Boolean>() { // from class: com.crashlytics.android.Crashlytics.6
                            @Override // io.fabric.sdk.android.services.settings.Settings.SettingsAccess
                            public final /* bridge */ /* synthetic */ Boolean usingSettings(SettingsData x0) {
                                Activity activity;
                                boolean z;
                                Fabric fabric = Crashlytics.this.fabric;
                                if (fabric.activity != null) {
                                    activity = fabric.activity.get();
                                } else {
                                    activity = null;
                                }
                                if (activity != null && !activity.isFinishing() && Crashlytics.this.shouldPromptUserBeforeSendingCrashReports()) {
                                    z = Crashlytics.this.getSendDecisionFromUser(activity, x0.promptData);
                                } else {
                                    z = true;
                                }
                                return Boolean.valueOf(z);
                            }
                        }, true)).booleanValue()) {
                            Fabric.getLogger();
                            new StringBuilder("User declined to send. Removing ").append(findReports.size()).append(" Report(s).");
                            Iterator<Report> it = findReports.iterator();
                            while (it.hasNext()) {
                                it.next().remove();
                            }
                        }
                    }
                    List<Report> list = findReports;
                    int i = 0;
                    while (!list.isEmpty() && !Crashlytics.getInstance().handler.isHandlingException.get()) {
                        Fabric.getLogger();
                        new StringBuilder("Attempting to send ").append(list.size()).append(" report(s)");
                        Iterator<Report> it2 = list.iterator();
                        while (it2.hasNext()) {
                            ReportUploader.this.forceUpload(it2.next());
                        }
                        List<Report> findReports2 = ReportUploader.this.findReports();
                        if (findReports2.isEmpty()) {
                            list = findReports2;
                        } else {
                            int i2 = i + 1;
                            long j = ReportUploader.RETRY_INTERVALS[Math.min(i, ReportUploader.RETRY_INTERVALS.length - 1)];
                            Fabric.getLogger();
                            new StringBuilder("Report submisson: scheduling delayed retry in ").append(j).append(" seconds");
                            try {
                                Thread.sleep(j * 1000);
                                i = i2;
                                list = findReports2;
                            } catch (InterruptedException e2) {
                                Thread.currentThread().interrupt();
                            }
                        }
                    }
                }
            } catch (Exception e3) {
                Fabric.getLogger().e("Fabric", "An unexpected error occurred while attempting to upload crash reports.", e3);
            }
            ReportUploader.access$002$9bb8a18(ReportUploader.this);
        }
    }
}
