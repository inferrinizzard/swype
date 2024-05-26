package com.crashlytics.android;

import android.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.util.Log;
import android.widget.ScrollView;
import android.widget.TextView;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.beta.Beta;
import com.crashlytics.android.internal.CrashEventDataProvider;
import com.crashlytics.android.internal.models.SessionEventData;
import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.Kit;
import io.fabric.sdk.android.KitGroup;
import io.fabric.sdk.android.services.common.ApiKey;
import io.fabric.sdk.android.services.common.CommonUtils;
import io.fabric.sdk.android.services.common.Crash;
import io.fabric.sdk.android.services.concurrency.DependsOn;
import io.fabric.sdk.android.services.concurrency.Priority;
import io.fabric.sdk.android.services.concurrency.PriorityCallable;
import io.fabric.sdk.android.services.concurrency.Task;
import io.fabric.sdk.android.services.concurrency.UnmetDependencyException;
import io.fabric.sdk.android.services.network.DefaultHttpRequestFactory;
import io.fabric.sdk.android.services.network.HttpRequestFactory;
import io.fabric.sdk.android.services.persistence.FileStoreImpl;
import io.fabric.sdk.android.services.persistence.PreferenceStoreImpl;
import io.fabric.sdk.android.services.settings.PromptSettingsData;
import io.fabric.sdk.android.services.settings.SessionSettingsData;
import io.fabric.sdk.android.services.settings.Settings;
import io.fabric.sdk.android.services.settings.SettingsData;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@DependsOn({CrashEventDataProvider.class})
/* loaded from: classes.dex */
public class Crashlytics extends Kit<Void> implements KitGroup {
    final ConcurrentHashMap<String, String> attributes;
    private String buildId;
    private float delay;
    private boolean disabled;
    private CrashlyticsExecutorServiceWrapper executorServiceWrapper;
    CrashEventDataProvider externalCrashEventDataProvider;
    CrashlyticsUncaughtExceptionHandler handler;
    private HttpRequestFactory httpRequestFactory;
    private File initializationMarkerFile;
    String installerPackageName;
    private final Collection<Kit<Boolean>> kits;
    private CrashlyticsListener listener;
    String packageName;
    private final PinningInfoProvider pinningInfo;
    private final long startTime;
    String userEmail;
    String userId;
    String userName;
    String versionCode;
    String versionName;

    public Crashlytics() {
        this((byte) 0);
    }

    /* JADX WARN: Illegal instructions before constructor call */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private Crashlytics(byte r3) {
        /*
            r2 = this;
            java.lang.String r0 = "Crashlytics Exception Handler"
            java.util.concurrent.ThreadFactory r1 = io.fabric.sdk.android.services.common.ExecutorUtils.getNamedThreadFactory(r0)
            java.util.concurrent.ExecutorService r1 = java.util.concurrent.Executors.newSingleThreadExecutor(r1)
            io.fabric.sdk.android.services.common.ExecutorUtils.addDelayedShutdownHook(r0, r1)
            r2.<init>(r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.crashlytics.android.Crashlytics.<init>(byte):void");
    }

    private Crashlytics(ExecutorService crashHandlerExecutor) {
        this.userId = null;
        this.userEmail = null;
        this.userName = null;
        this.attributes = new ConcurrentHashMap<>();
        this.startTime = System.currentTimeMillis();
        this.delay = 1.0f;
        this.listener = null;
        this.pinningInfo = null;
        this.disabled = false;
        this.executorServiceWrapper = new CrashlyticsExecutorServiceWrapper(crashHandlerExecutor);
        this.kits = Collections.unmodifiableCollection(Arrays.asList(new Answers(), new Beta()));
    }

    private boolean onPreExecute(Context context) {
        if (this.disabled) {
            return false;
        }
        new ApiKey();
        if (ApiKey.getValue(context) == null) {
            return false;
        }
        Fabric.getLogger().i("Fabric", "Initializing Crashlytics 2.2.2.37");
        this.initializationMarkerFile = new File(getSdkDirectory(), "initialization_marker");
        boolean initializeSynchronously = false;
        try {
            try {
                CrashlyticsPinningInfoProvider crashlyticsPinningInfoProvider = this.pinningInfo != null ? new CrashlyticsPinningInfoProvider(this.pinningInfo) : null;
                this.httpRequestFactory = new DefaultHttpRequestFactory(Fabric.getLogger());
                this.httpRequestFactory.setPinningInfoProvider(crashlyticsPinningInfoProvider);
                try {
                    this.packageName = context.getPackageName();
                    this.installerPackageName = this.idManager.getInstallerPackageName();
                    Fabric.getLogger();
                    new StringBuilder("Installer package name is: ").append(this.installerPackageName);
                    PackageInfo packageInfo = context.getPackageManager().getPackageInfo(this.packageName, 0);
                    this.versionCode = Integer.toString(packageInfo.versionCode);
                    this.versionName = packageInfo.versionName == null ? "0.0" : packageInfo.versionName;
                    this.buildId = CommonUtils.resolveBuildId(context);
                } catch (Exception e) {
                    Fabric.getLogger().e("Fabric", "Error setting up app properties", e);
                }
                this.idManager.getBluetoothMacAddress();
                BuildIdValidator buildIdValidator = new BuildIdValidator(this.buildId, CommonUtils.getBooleanResourceValue(context, "com.crashlytics.RequireBuildId", true));
                if (CommonUtils.isNullOrEmpty(buildIdValidator.buildId) && buildIdValidator.requiringBuildId) {
                    Log.e("Fabric", ".");
                    Log.e("Fabric", ".     |  | ");
                    Log.e("Fabric", ".     |  |");
                    Log.e("Fabric", ".     |  |");
                    Log.e("Fabric", ".   \\ |  | /");
                    Log.e("Fabric", ".    \\    /");
                    Log.e("Fabric", ".     \\  /");
                    Log.e("Fabric", ".      \\/");
                    Log.e("Fabric", ".");
                    Log.e("Fabric", "This app relies on Crashlytics. Please sign up for access at https://fabric.io/sign_up,\ninstall an Android build tool and ask a team member to invite you to this app's organization.");
                    Log.e("Fabric", ".");
                    Log.e("Fabric", ".      /\\");
                    Log.e("Fabric", ".     /  \\");
                    Log.e("Fabric", ".    /    \\");
                    Log.e("Fabric", ".   / |  | \\");
                    Log.e("Fabric", ".     |  |");
                    Log.e("Fabric", ".     |  |");
                    Log.e("Fabric", ".     |  |");
                    Log.e("Fabric", ".");
                    throw new CrashlyticsMissingDependencyException("This app relies on Crashlytics. Please sign up for access at https://fabric.io/sign_up,\ninstall an Android build tool and ask a team member to invite you to this app's organization.");
                }
                if (!buildIdValidator.requiringBuildId) {
                    Fabric.getLogger();
                }
                try {
                    SessionDataWriter sessionDataWriter = new SessionDataWriter(this.context, this.buildId, this.packageName);
                    Fabric.getLogger();
                    this.handler = new CrashlyticsUncaughtExceptionHandler(Thread.getDefaultUncaughtExceptionHandler(), this.executorServiceWrapper, this.idManager, sessionDataWriter, this);
                    initializeSynchronously = ((Boolean) this.executorServiceWrapper.executeSyncLoggingException(new Callable<Boolean>() { // from class: com.crashlytics.android.Crashlytics.4
                        @Override // java.util.concurrent.Callable
                        public final /* bridge */ /* synthetic */ Boolean call() throws Exception {
                            return Boolean.valueOf(Crashlytics.this.initializationMarkerFile.exists());
                        }
                    })).booleanValue();
                    final CrashlyticsUncaughtExceptionHandler crashlyticsUncaughtExceptionHandler = this.handler;
                    crashlyticsUncaughtExceptionHandler.executorServiceWrapper.executeAsync(new Callable<Void>() { // from class: com.crashlytics.android.CrashlyticsUncaughtExceptionHandler.10
                        @Override // java.util.concurrent.Callable
                        public final /* bridge */ /* synthetic */ Void call() throws Exception {
                            if (!(CrashlyticsUncaughtExceptionHandler.this.listSessionBeginFiles().length > 0)) {
                                CrashlyticsUncaughtExceptionHandler.this.doOpenSession();
                                return null;
                            }
                            return null;
                        }
                    });
                    Thread.setDefaultUncaughtExceptionHandler(this.handler);
                    Fabric.getLogger();
                } catch (Exception e2) {
                    Fabric.getLogger().e("Fabric", "There was a problem installing the exception handler.", e2);
                }
                if (!initializeSynchronously || !CommonUtils.canTryConnection(context)) {
                    return true;
                }
                finishInitSynchronously();
                return false;
            } catch (CrashlyticsMissingDependencyException e3) {
                throw new UnmetDependencyException(e3);
            }
        } catch (Exception e4) {
            Fabric.getLogger().e("Fabric", "Crashlytics was not started due to an exception during initialization", e4);
            return false;
        }
    }

    @Override // io.fabric.sdk.android.Kit
    public final String getIdentifier() {
        return "com.crashlytics.sdk.android:crashlytics";
    }

    @Override // io.fabric.sdk.android.Kit
    public final String getVersion() {
        return "2.2.2.37";
    }

    @Override // io.fabric.sdk.android.KitGroup
    public final Collection<? extends Kit> getKits() {
        return this.kits;
    }

    public static Crashlytics getInstance() {
        try {
            return (Crashlytics) Fabric.getKit(Crashlytics.class);
        } catch (IllegalStateException ex) {
            Fabric.getLogger().e("Fabric", "Crashlytics must be initialized by calling Fabric.with(Context) prior to calling Crashlytics.getInstance()", null);
            throw ex;
        }
    }

    private void finishInitSynchronously() {
        PriorityCallable<Void> callable = new PriorityCallable<Void>() { // from class: com.crashlytics.android.Crashlytics.1
            @Override // io.fabric.sdk.android.services.concurrency.PriorityTask, io.fabric.sdk.android.services.concurrency.PriorityProvider
            public final Priority getPriority() {
                return Priority.IMMEDIATE;
            }

            @Override // java.util.concurrent.Callable
            public final /* bridge */ /* synthetic */ Object call() throws Exception {
                return Crashlytics.this.doInBackground();
            }
        };
        for (Task task : this.initializationTask.getDependencies()) {
            callable.addDependency(task);
        }
        Future<Void> future = this.fabric.executorService.submit(callable);
        Fabric.getLogger();
        try {
            future.get(4L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Fabric.getLogger().e("Fabric", "Crashlytics was interrupted during initialization.", e);
        } catch (ExecutionException e2) {
            Fabric.getLogger().e("Fabric", "Problem encountered during Crashlytics initialization.", e2);
        } catch (TimeoutException e3) {
            Fabric.getLogger().e("Fabric", "Crashlytics timed out during initialization.", e3);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void recordFatalExceptionEvent(String sessionId) {
        Answers answers = (Answers) Fabric.getKit(Answers.class);
        if (answers != null) {
            answers.onException(new Crash.FatalException(sessionId));
        }
    }

    private void markInitializationComplete() {
        this.executorServiceWrapper.executeAsync(new Callable<Boolean>() { // from class: com.crashlytics.android.Crashlytics.3
            /* JADX INFO: Access modifiers changed from: private */
            @Override // java.util.concurrent.Callable
            public Boolean call() throws Exception {
                try {
                    boolean removed = Crashlytics.this.initializationMarkerFile.delete();
                    Fabric.getLogger();
                    return Boolean.valueOf(removed);
                } catch (Exception e) {
                    Fabric.getLogger().e("Fabric", "Problem encountered deleting Crashlytics initialization marker.", e);
                    return false;
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final File getSdkDirectory() {
        return new FileStoreImpl(this).getFilesDir();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final CreateReportSpiCall getCreateReportSpiCall(SettingsData settingsData) {
        if (settingsData != null) {
            return new DefaultCreateReportSpiCall(this, CommonUtils.getStringsFileValue(getInstance().context, "com.crashlytics.ApiEndpoint"), settingsData.appData.reportsUrl, this.httpRequestFactory);
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean getSendDecisionFromUser(final Activity context, final PromptSettingsData promptData) {
        final DialogStringResolver stringResolver = new DialogStringResolver(context, promptData);
        final OptInLatch latch = new OptInLatch(this, (byte) 0);
        context.runOnUiThread(new Runnable() { // from class: com.crashlytics.android.Crashlytics.7
            @Override // java.lang.Runnable
            public final void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                DialogInterface.OnClickListener sendClickListener = new DialogInterface.OnClickListener() { // from class: com.crashlytics.android.Crashlytics.7.1
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialog, int which) {
                        latch.setOptIn(true);
                        dialog.dismiss();
                    }
                };
                float density = context.getResources().getDisplayMetrics().density;
                int textViewPadding = Crashlytics.access$300$6be4ac93(density, 5);
                TextView textView = new TextView(context);
                textView.setAutoLinkMask(15);
                DialogStringResolver dialogStringResolver = stringResolver;
                textView.setText(dialogStringResolver.resourceOrFallbackValue("com.crashlytics.CrashSubmissionPromptMessage", dialogStringResolver.promptData.message));
                textView.setTextAppearance(context, R.style.TextAppearance.Medium);
                textView.setPadding(textViewPadding, textViewPadding, textViewPadding, textViewPadding);
                textView.setFocusable(false);
                ScrollView scrollView = new ScrollView(context);
                scrollView.setPadding(Crashlytics.access$300$6be4ac93(density, 14), Crashlytics.access$300$6be4ac93(density, 2), Crashlytics.access$300$6be4ac93(density, 10), Crashlytics.access$300$6be4ac93(density, 12));
                scrollView.addView(textView);
                AlertDialog.Builder view = builder.setView(scrollView);
                DialogStringResolver dialogStringResolver2 = stringResolver;
                AlertDialog.Builder cancelable = view.setTitle(dialogStringResolver2.resourceOrFallbackValue("com.crashlytics.CrashSubmissionPromptTitle", dialogStringResolver2.promptData.title)).setCancelable(false);
                DialogStringResolver dialogStringResolver3 = stringResolver;
                cancelable.setNeutralButton(dialogStringResolver3.resourceOrFallbackValue("com.crashlytics.CrashSubmissionSendTitle", dialogStringResolver3.promptData.sendButtonTitle), sendClickListener);
                if (promptData.showCancelButton) {
                    DialogInterface.OnClickListener cancelClickListener = new DialogInterface.OnClickListener() { // from class: com.crashlytics.android.Crashlytics.7.2
                        @Override // android.content.DialogInterface.OnClickListener
                        public final void onClick(DialogInterface dialog, int id) {
                            latch.setOptIn(false);
                            dialog.dismiss();
                        }
                    };
                    DialogStringResolver dialogStringResolver4 = stringResolver;
                    builder.setNegativeButton(dialogStringResolver4.resourceOrFallbackValue("com.crashlytics.CrashSubmissionCancelTitle", dialogStringResolver4.promptData.cancelButtonTitle), cancelClickListener);
                }
                if (promptData.showAlwaysSendButton) {
                    DialogInterface.OnClickListener alwaysSendClickListener = new DialogInterface.OnClickListener() { // from class: com.crashlytics.android.Crashlytics.7.3
                        @Override // android.content.DialogInterface.OnClickListener
                        public final void onClick(DialogInterface dialog, int id) {
                            PreferenceStoreImpl preferenceStoreImpl = new PreferenceStoreImpl(Crashlytics.this);
                            preferenceStoreImpl.save(preferenceStoreImpl.edit().putBoolean("always_send_reports_opt_in", true));
                            latch.setOptIn(true);
                            dialog.dismiss();
                        }
                    };
                    DialogStringResolver dialogStringResolver5 = stringResolver;
                    builder.setPositiveButton(dialogStringResolver5.resourceOrFallbackValue("com.crashlytics.CrashSubmissionAlwaysSendTitle", dialogStringResolver5.promptData.alwaysSendButtonTitle), alwaysSendClickListener);
                }
                builder.show();
            }
        });
        Fabric.getLogger();
        try {
            latch.latch.await();
        } catch (InterruptedException e) {
        }
        return latch.send;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class OptInLatch {
        final CountDownLatch latch;
        boolean send;

        private OptInLatch() {
            this.send = false;
            this.latch = new CountDownLatch(1);
        }

        /* synthetic */ OptInLatch(Crashlytics x0, byte b) {
            this();
        }

        final void setOptIn(boolean optIn) {
            this.send = optIn;
            this.latch.countDown();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // io.fabric.sdk.android.Kit
    public final boolean onPreExecute() {
        Context context = this.context;
        return onPreExecute(context);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // io.fabric.sdk.android.Kit
    public final Void doInBackground() {
        SettingsData settingsData;
        this.executorServiceWrapper.executeSyncLoggingException(new Callable<Void>() { // from class: com.crashlytics.android.Crashlytics.2
            @Override // java.util.concurrent.Callable
            public final /* bridge */ /* synthetic */ Void call() throws Exception {
                Crashlytics.this.initializationMarkerFile.createNewFile();
                Fabric.getLogger();
                return null;
            }
        });
        final CrashlyticsUncaughtExceptionHandler crashlyticsUncaughtExceptionHandler = this.handler;
        crashlyticsUncaughtExceptionHandler.executorServiceWrapper.executeAsync(new Runnable() { // from class: com.crashlytics.android.CrashlyticsUncaughtExceptionHandler.12
            @Override // java.lang.Runnable
            public final void run() {
                final CrashlyticsUncaughtExceptionHandler crashlyticsUncaughtExceptionHandler2 = CrashlyticsUncaughtExceptionHandler.this;
                File[] listFilesMatching = CrashlyticsUncaughtExceptionHandler.this.listFilesMatching(ClsFileOutputStream.TEMP_FILENAME_FILTER);
                File file = new File(crashlyticsUncaughtExceptionHandler2.crashlytics.getSdkDirectory(), "invalidClsFiles");
                if (file.exists()) {
                    if (file.isDirectory()) {
                        for (File file2 : file.listFiles()) {
                            file2.delete();
                        }
                    }
                    file.delete();
                }
                for (File file3 : listFilesMatching) {
                    Fabric.getLogger();
                    new StringBuilder("Found invalid session part file: ").append(file3);
                    final String sessionIdFromSessionFile = CrashlyticsUncaughtExceptionHandler.getSessionIdFromSessionFile(file3);
                    FilenameFilter filenameFilter = new FilenameFilter() { // from class: com.crashlytics.android.CrashlyticsUncaughtExceptionHandler.13
                        @Override // java.io.FilenameFilter
                        public final boolean accept(File f, String name) {
                            return name.startsWith(sessionIdFromSessionFile);
                        }
                    };
                    Fabric.getLogger();
                    for (File file4 : crashlyticsUncaughtExceptionHandler2.listFilesMatching(filenameFilter)) {
                        Fabric.getLogger();
                        new StringBuilder("Deleting session file: ").append(file4);
                        file4.delete();
                    }
                }
            }
        });
        boolean reportingDisabled = true;
        try {
            try {
                settingsData = Settings.LazyHolder.access$100().awaitSettingsData();
            } catch (Exception e) {
                Fabric.getLogger().e("Fabric", "Error dealing with settings", e);
            }
        } catch (Exception e2) {
            Fabric.getLogger().e("Fabric", "Problem encountered during Crashlytics initialization.", e2);
        } finally {
            markInitializationComplete();
        }
        if (settingsData == null) {
            Fabric.getLogger().w("Fabric", "Received null settings, skipping initialization!");
            return null;
        }
        if (settingsData.featuresData.collectReports) {
            reportingDisabled = false;
            final CrashlyticsUncaughtExceptionHandler crashlyticsUncaughtExceptionHandler2 = this.handler;
            ((Boolean) crashlyticsUncaughtExceptionHandler2.executorServiceWrapper.executeSyncLoggingException(new Callable<Boolean>() { // from class: com.crashlytics.android.CrashlyticsUncaughtExceptionHandler.11
                @Override // java.util.concurrent.Callable
                public final /* bridge */ /* synthetic */ Boolean call() throws Exception {
                    if (!CrashlyticsUncaughtExceptionHandler.this.isHandlingException.get()) {
                        Crashlytics crashlytics = CrashlyticsUncaughtExceptionHandler.this.crashlytics;
                        SessionEventData sessionEventData = null;
                        if (crashlytics.externalCrashEventDataProvider != null) {
                            sessionEventData = crashlytics.externalCrashEventDataProvider.getCrashEventData();
                        }
                        if (sessionEventData != null) {
                            CrashlyticsUncaughtExceptionHandler.access$800(CrashlyticsUncaughtExceptionHandler.this, sessionEventData);
                        }
                        CrashlyticsUncaughtExceptionHandler.this.doCloseSessions();
                        CrashlyticsUncaughtExceptionHandler.this.doOpenSession();
                        Fabric.getLogger();
                        return true;
                    }
                    Fabric.getLogger();
                    return false;
                }
            })).booleanValue();
            CreateReportSpiCall call = getCreateReportSpiCall(settingsData);
            if (call != null) {
                new ReportUploader(call).uploadReports(this.delay);
            } else {
                Fabric.getLogger().w("Fabric", "Unable to create a call to upload reports.");
            }
        }
        if (reportingDisabled) {
            Fabric.getLogger();
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean shouldPromptUserBeforeSendingCrashReports() {
        return ((Boolean) Settings.LazyHolder.access$100().withSettings(new Settings.SettingsAccess<Boolean>() { // from class: com.crashlytics.android.Crashlytics.5
            @Override // io.fabric.sdk.android.services.settings.Settings.SettingsAccess
            public final /* bridge */ /* synthetic */ Boolean usingSettings(SettingsData x0) {
                if (x0.featuresData.promptEnabled) {
                    return Boolean.valueOf(new PreferenceStoreImpl(Crashlytics.this).get().getBoolean("always_send_reports_opt_in", false) ? false : true);
                }
                return false;
            }
        }, false)).booleanValue();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static SessionSettingsData getSessionSettingsData() {
        SettingsData settingsData = Settings.LazyHolder.access$100().awaitSettingsData();
        if (settingsData == null) {
            return null;
        }
        return settingsData.sessionData;
    }

    static /* synthetic */ int access$300$6be4ac93(float x1, int x2) {
        return (int) (x2 * x1);
    }
}
