package com.crashlytics.android;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import com.crashlytics.android.internal.models.SessionEventData;
import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.services.common.ApiKey;
import io.fabric.sdk.android.services.common.CommonUtils;
import io.fabric.sdk.android.services.common.DeliveryMechanism;
import io.fabric.sdk.android.services.common.IdManager;
import io.fabric.sdk.android.services.common.QueueFile;
import io.fabric.sdk.android.services.settings.SessionSettingsData;
import io.fabric.sdk.android.services.settings.Settings;
import io.fabric.sdk.android.services.settings.SettingsData;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.Thread;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class CrashlyticsUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
    final Crashlytics crashlytics;
    private final Thread.UncaughtExceptionHandler defaultHandler;
    final CrashlyticsExecutorServiceWrapper executorServiceWrapper;
    private final File filesDir;
    private final IdManager idManager;
    private final LogFileManager logFileManager;
    private boolean powerConnected;
    private final BroadcastReceiver powerConnectedReceiver;
    private final BroadcastReceiver powerDisconnectedReceiver;
    private final SessionDataWriter sessionDataWriter;
    static final FilenameFilter SESSION_FILE_FILTER = new FilenameFilter() { // from class: com.crashlytics.android.CrashlyticsUncaughtExceptionHandler.1
        @Override // java.io.FilenameFilter
        public final boolean accept(File dir, String filename) {
            return filename.length() == 39 && filename.endsWith(".cls");
        }
    };
    static final Comparator<File> LARGEST_FILE_NAME_FIRST = new Comparator<File>() { // from class: com.crashlytics.android.CrashlyticsUncaughtExceptionHandler.2
        @Override // java.util.Comparator
        public final /* bridge */ /* synthetic */ int compare(File file, File file2) {
            return file2.getName().compareTo(file.getName());
        }
    };
    static final Comparator<File> SMALLEST_FILE_NAME_FIRST = new Comparator<File>() { // from class: com.crashlytics.android.CrashlyticsUncaughtExceptionHandler.3
        @Override // java.util.Comparator
        public final /* bridge */ /* synthetic */ int compare(File file, File file2) {
            return file.getName().compareTo(file2.getName());
        }
    };
    static final FilenameFilter ANY_SESSION_FILENAME_FILTER = new FilenameFilter() { // from class: com.crashlytics.android.CrashlyticsUncaughtExceptionHandler.4
        @Override // java.io.FilenameFilter
        public final boolean accept(File file, String filename) {
            return CrashlyticsUncaughtExceptionHandler.SESSION_FILE_PATTERN.matcher(filename).matches();
        }
    };
    private static final Pattern SESSION_FILE_PATTERN = Pattern.compile("([\\d|A-Z|a-z]{12}\\-[\\d|A-Z|a-z]{4}\\-[\\d|A-Z|a-z]{4}\\-[\\d|A-Z|a-z]{12}).+");
    private static final Map<String, String> SEND_AT_CRASHTIME_HEADER = Collections.singletonMap("X-CRASHLYTICS-SEND-FLAGS", "1");
    private final AtomicInteger eventCounter = new AtomicInteger(0);
    private final AtomicBoolean receiversRegistered = new AtomicBoolean(false);
    final AtomicBoolean isHandlingException = new AtomicBoolean(false);

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class FileNameContainsFilter implements FilenameFilter {
        private final String string;

        public FileNameContainsFilter(String s) {
            this.string = s;
        }

        @Override // java.io.FilenameFilter
        public final boolean accept(File dir, String filename) {
            return filename.contains(this.string) && !filename.endsWith(".cls_temp");
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class SessionPartFileFilter implements FilenameFilter {
        private final String sessionId;

        public SessionPartFileFilter(String sessionId) {
            this.sessionId = sessionId;
        }

        @Override // java.io.FilenameFilter
        public final boolean accept(File file, String fileName) {
            return (fileName.equals(new StringBuilder().append(this.sessionId).append(".cls").toString()) || !fileName.contains(this.sessionId) || fileName.endsWith(".cls_temp")) ? false : true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class AnySessionPartFileFilter implements FilenameFilter {
        private AnySessionPartFileFilter() {
        }

        /* synthetic */ AnySessionPartFileFilter(byte b) {
            this();
        }

        @Override // java.io.FilenameFilter
        public final boolean accept(File file, String fileName) {
            return !CrashlyticsUncaughtExceptionHandler.SESSION_FILE_FILTER.accept(file, fileName) && CrashlyticsUncaughtExceptionHandler.SESSION_FILE_PATTERN.matcher(fileName).matches();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CrashlyticsUncaughtExceptionHandler(Thread.UncaughtExceptionHandler handler, CrashlyticsExecutorServiceWrapper executorServiceWrapper, IdManager idManager, SessionDataWriter sessionDataWriter, Crashlytics crashlytics) {
        this.defaultHandler = handler;
        this.executorServiceWrapper = executorServiceWrapper;
        this.idManager = idManager;
        this.crashlytics = crashlytics;
        this.sessionDataWriter = sessionDataWriter;
        this.filesDir = crashlytics.getSdkDirectory();
        this.logFileManager = new LogFileManager(crashlytics.context, this.filesDir);
        Fabric.getLogger();
        File file = new File(this.crashlytics.getSdkDirectory(), "crash_marker");
        if (file.exists()) {
            file.delete();
        }
        this.powerConnectedReceiver = new BroadcastReceiver() { // from class: com.crashlytics.android.CrashlyticsUncaughtExceptionHandler.5
            @Override // android.content.BroadcastReceiver
            public final void onReceive(Context context, Intent intent) {
                CrashlyticsUncaughtExceptionHandler.this.powerConnected = true;
            }
        };
        IntentFilter powerConnectedFilter = new IntentFilter("android.intent.action.ACTION_POWER_CONNECTED");
        this.powerDisconnectedReceiver = new BroadcastReceiver() { // from class: com.crashlytics.android.CrashlyticsUncaughtExceptionHandler.6
            @Override // android.content.BroadcastReceiver
            public final void onReceive(Context context, Intent intent) {
                CrashlyticsUncaughtExceptionHandler.this.powerConnected = false;
            }
        };
        IntentFilter powerDisconnectedFilter = new IntentFilter("android.intent.action.ACTION_POWER_DISCONNECTED");
        Context context = crashlytics.context;
        context.registerReceiver(this.powerConnectedReceiver, powerConnectedFilter);
        context.registerReceiver(this.powerDisconnectedReceiver, powerDisconnectedFilter);
        this.receiversRegistered.set(true);
    }

    @Override // java.lang.Thread.UncaughtExceptionHandler
    public final synchronized void uncaughtException(final Thread thread, final Throwable ex) {
        this.isHandlingException.set(true);
        try {
            try {
                Fabric.getLogger();
                new StringBuilder("Crashlytics is handling uncaught exception \"").append(ex).append("\" from thread ").append(thread.getName());
                if (!this.receiversRegistered.getAndSet(true)) {
                    Fabric.getLogger();
                    Context context = this.crashlytics.context;
                    context.unregisterReceiver(this.powerConnectedReceiver);
                    context.unregisterReceiver(this.powerDisconnectedReceiver);
                }
                final Date now = new Date();
                this.executorServiceWrapper.executeSyncLoggingException(new Callable<Void>() { // from class: com.crashlytics.android.CrashlyticsUncaughtExceptionHandler.7
                    @Override // java.util.concurrent.Callable
                    public final /* bridge */ /* synthetic */ Void call() throws Exception {
                        CrashlyticsUncaughtExceptionHandler.access$200(CrashlyticsUncaughtExceptionHandler.this, now, thread, ex);
                        return null;
                    }
                });
            } finally {
                Fabric.getLogger();
                this.defaultHandler.uncaughtException(thread, ex);
                this.isHandlingException.set(false);
            }
        } catch (Exception e) {
            Fabric.getLogger().e("Fabric", "An error occurred in the uncaught exception handler", e);
            Fabric.getLogger();
            this.defaultHandler.uncaughtException(thread, ex);
            this.isHandlingException.set(false);
        }
    }

    private String getCurrentSessionId() {
        File[] sessionBeginFiles = listFilesMatching(new FileNameContainsFilter("BeginSession"));
        Arrays.sort(sessionBeginFiles, LARGEST_FILE_NAME_FIRST);
        if (sessionBeginFiles.length > 0) {
            return getSessionIdFromSessionFile(sessionBeginFiles[0]);
        }
        return null;
    }

    static String getSessionIdFromSessionFile(File sessionFile) {
        return sessionFile.getName().substring(0, 35);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doOpenSession() throws Exception {
        ClsFileOutputStream clsFileOutputStream;
        ClsFileOutputStream clsFileOutputStream2;
        ClsFileOutputStream clsFileOutputStream3;
        ClsFileOutputStream clsFileOutputStream4;
        SharedPreferences sharedPrefs;
        Date startedAt = new Date();
        String sessionIdentifier = new CLSUUID(this.idManager).toString();
        Fabric.getLogger();
        CodedOutputStream codedOutputStream = null;
        try {
            clsFileOutputStream = new ClsFileOutputStream(this.filesDir, sessionIdentifier + "BeginSession");
        } catch (Exception e) {
            e = e;
            clsFileOutputStream = null;
        } catch (Throwable th) {
            th = th;
            clsFileOutputStream = null;
            CommonUtils.flushOrLog(codedOutputStream, "Failed to flush to session begin file.");
            CommonUtils.closeOrLog(clsFileOutputStream, "Failed to close begin session file.");
            throw th;
        }
        try {
            try {
                codedOutputStream = CodedOutputStream.newInstance(clsFileOutputStream);
                SessionDataWriter.writeBeginSession(codedOutputStream, sessionIdentifier, String.format(Locale.US, "Crashlytics Android SDK/%s", this.crashlytics.getVersion()), startedAt.getTime() / 1000);
                CommonUtils.flushOrLog(codedOutputStream, "Failed to flush to session begin file.");
                CommonUtils.closeOrLog(clsFileOutputStream, "Failed to close begin session file.");
                CodedOutputStream codedOutputStream2 = null;
                try {
                    clsFileOutputStream2 = new ClsFileOutputStream(this.filesDir, sessionIdentifier + "SessionApp");
                } catch (Exception e2) {
                    e = e2;
                    clsFileOutputStream2 = null;
                } catch (Throwable th2) {
                    th = th2;
                    clsFileOutputStream2 = null;
                    CommonUtils.flushOrLog(codedOutputStream2, "Failed to flush to session app file.");
                    CommonUtils.closeOrLog(clsFileOutputStream2, "Failed to close session app file.");
                    throw th;
                }
                try {
                    try {
                        codedOutputStream2 = CodedOutputStream.newInstance(clsFileOutputStream2);
                        String str = this.crashlytics.packageName;
                        String str2 = this.crashlytics.versionCode;
                        String str3 = this.crashlytics.versionName;
                        String appInstallIdentifier = this.idManager.getAppInstallIdentifier();
                        int i = DeliveryMechanism.determineFrom(this.crashlytics.installerPackageName).id;
                        SessionDataWriter sessionDataWriter = this.sessionDataWriter;
                        ByteString copyFromUtf8 = ByteString.copyFromUtf8(str);
                        ByteString copyFromUtf82 = ByteString.copyFromUtf8(str2);
                        ByteString copyFromUtf83 = ByteString.copyFromUtf8(str3);
                        ByteString copyFromUtf84 = ByteString.copyFromUtf8(appInstallIdentifier);
                        codedOutputStream2.writeTag(7, 2);
                        int computeBytesSize = CodedOutputStream.computeBytesSize(1, copyFromUtf8) + 0 + CodedOutputStream.computeBytesSize(2, copyFromUtf82) + CodedOutputStream.computeBytesSize(3, copyFromUtf83);
                        int sessionAppOrgSize = sessionDataWriter.getSessionAppOrgSize();
                        codedOutputStream2.writeRawVarint32(computeBytesSize + sessionAppOrgSize + CodedOutputStream.computeTagSize(5) + CodedOutputStream.computeRawVarint32Size(sessionAppOrgSize) + CodedOutputStream.computeBytesSize(6, copyFromUtf84) + CodedOutputStream.computeEnumSize(10, i));
                        codedOutputStream2.writeBytes(1, copyFromUtf8);
                        codedOutputStream2.writeBytes(2, copyFromUtf82);
                        codedOutputStream2.writeBytes(3, copyFromUtf83);
                        codedOutputStream2.writeTag(5, 2);
                        codedOutputStream2.writeRawVarint32(sessionDataWriter.getSessionAppOrgSize());
                        new ApiKey();
                        codedOutputStream2.writeString$4f708078(ApiKey.getValue(sessionDataWriter.context));
                        codedOutputStream2.writeBytes(6, copyFromUtf84);
                        codedOutputStream2.writeEnum(10, i);
                        CommonUtils.flushOrLog(codedOutputStream2, "Failed to flush to session app file.");
                        CommonUtils.closeOrLog(clsFileOutputStream2, "Failed to close session app file.");
                        try {
                            try {
                                clsFileOutputStream3 = new ClsFileOutputStream(this.filesDir, sessionIdentifier + "SessionOS");
                            } catch (Exception e3) {
                                e = e3;
                                clsFileOutputStream3 = null;
                            } catch (Throwable th3) {
                                th = th3;
                                clsFileOutputStream2 = null;
                                CommonUtils.flushOrLog(null, "Failed to flush to session OS file.");
                                CommonUtils.closeOrLog(clsFileOutputStream2, "Failed to close session OS file.");
                                throw th;
                            }
                            try {
                                CodedOutputStream newInstance = CodedOutputStream.newInstance(clsFileOutputStream3);
                                boolean isRooted = CommonUtils.isRooted(this.crashlytics.context);
                                ByteString copyFromUtf85 = ByteString.copyFromUtf8(Build.VERSION.RELEASE);
                                ByteString copyFromUtf86 = ByteString.copyFromUtf8(Build.VERSION.CODENAME);
                                newInstance.writeTag(8, 2);
                                newInstance.writeRawVarint32(SessionDataWriter.getSessionOSSize$3c0313b2(copyFromUtf85, copyFromUtf86));
                                newInstance.writeEnum(1, 3);
                                newInstance.writeBytes(2, copyFromUtf85);
                                newInstance.writeBytes(3, copyFromUtf86);
                                newInstance.writeBool(4, isRooted);
                                CommonUtils.flushOrLog(newInstance, "Failed to flush to session OS file.");
                                CommonUtils.closeOrLog(clsFileOutputStream3, "Failed to close session OS file.");
                                ClsFileOutputStream clsFileOutputStream5 = null;
                                CodedOutputStream codedOutputStream3 = null;
                                try {
                                    clsFileOutputStream4 = new ClsFileOutputStream(this.filesDir, sessionIdentifier + "SessionDevice");
                                    try {
                                        CodedOutputStream newInstance2 = CodedOutputStream.newInstance(clsFileOutputStream4);
                                        try {
                                            Context context = this.crashlytics.context;
                                            StatFs statFs = new StatFs(Environment.getDataDirectory().getPath());
                                            IdManager idManager = this.idManager;
                                            String str4 = "";
                                            if (idManager.collectHardwareIds && (str4 = idManager.getAndroidId()) == null && (str4 = (sharedPrefs = CommonUtils.getSharedPrefs(idManager.appContext)).getString("crashlytics.installation.id", null)) == null) {
                                                str4 = idManager.createInstallationUUID(sharedPrefs);
                                            }
                                            SessionDataWriter.writeSessionDevice(newInstance2, str4, CommonUtils.getCpuArchitectureInt(), Build.MODEL, Runtime.getRuntime().availableProcessors(), CommonUtils.getTotalRamInBytes(), statFs.getBlockSize() * statFs.getBlockCount(), CommonUtils.isEmulator(context), this.idManager.getDeviceIdentifiers(), CommonUtils.getDeviceState(context), Build.MANUFACTURER, Build.PRODUCT);
                                            CommonUtils.flushOrLog(newInstance2, "Failed to flush session device info.");
                                            CommonUtils.closeOrLog(clsFileOutputStream4, "Failed to close session device file.");
                                        } catch (Exception e4) {
                                            clsFileOutputStream5 = clsFileOutputStream4;
                                            e = e4;
                                            codedOutputStream3 = newInstance2;
                                            try {
                                                ExceptionUtils.writeStackTraceIfNotNull(e, clsFileOutputStream5);
                                                throw e;
                                            } catch (Throwable th4) {
                                                th = th4;
                                                clsFileOutputStream4 = clsFileOutputStream5;
                                                CommonUtils.flushOrLog(codedOutputStream3, "Failed to flush session device info.");
                                                CommonUtils.closeOrLog(clsFileOutputStream4, "Failed to close session device file.");
                                                throw th;
                                            }
                                        } catch (Throwable th5) {
                                            codedOutputStream3 = newInstance2;
                                            th = th5;
                                            CommonUtils.flushOrLog(codedOutputStream3, "Failed to flush session device info.");
                                            CommonUtils.closeOrLog(clsFileOutputStream4, "Failed to close session device file.");
                                            throw th;
                                        }
                                    } catch (Exception e5) {
                                        e = e5;
                                        clsFileOutputStream5 = clsFileOutputStream4;
                                    } catch (Throwable th6) {
                                        th = th6;
                                    }
                                } catch (Exception e6) {
                                    e = e6;
                                } catch (Throwable th7) {
                                    th = th7;
                                    clsFileOutputStream4 = null;
                                }
                            } catch (Exception e7) {
                                e = e7;
                                ExceptionUtils.writeStackTraceIfNotNull(e, clsFileOutputStream3);
                                throw e;
                            }
                        } catch (Throwable th8) {
                            th = th8;
                            CommonUtils.flushOrLog(null, "Failed to flush to session OS file.");
                            CommonUtils.closeOrLog(clsFileOutputStream2, "Failed to close session OS file.");
                            throw th;
                        }
                    } catch (Throwable th9) {
                        th = th9;
                        CommonUtils.flushOrLog(codedOutputStream2, "Failed to flush to session app file.");
                        CommonUtils.closeOrLog(clsFileOutputStream2, "Failed to close session app file.");
                        throw th;
                    }
                } catch (Exception e8) {
                    e = e8;
                    ExceptionUtils.writeStackTraceIfNotNull(e, clsFileOutputStream2);
                    throw e;
                }
            } catch (Throwable th10) {
                th = th10;
                CommonUtils.flushOrLog(codedOutputStream, "Failed to flush to session begin file.");
                CommonUtils.closeOrLog(clsFileOutputStream, "Failed to close begin session file.");
                throw th;
            }
        } catch (Exception e9) {
            e = e9;
            ExceptionUtils.writeStackTraceIfNotNull(e, clsFileOutputStream);
            throw e;
        }
    }

    final File[] listSessionBeginFiles() {
        return listFilesMatching(new FileNameContainsFilter("BeginSession"));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final File[] listFilesMatching(FilenameFilter filter) {
        File[] listFiles = this.filesDir.listFiles(filter);
        return listFiles == null ? new File[0] : listFiles;
    }

    private void writeSessionEvent$42ff7c1f(CodedOutputStream cos, Date time, Thread thread, Throwable ex, String eventType) throws Exception {
        Map<String, String> attributes;
        Context context = this.crashlytics.context;
        long eventTime = time.getTime() / 1000;
        float batteryLevel = CommonUtils.getBatteryLevel(context);
        int batteryVelocity = CommonUtils.getBatteryVelocity(context, this.powerConnected);
        boolean proximityEnabled = CommonUtils.getProximitySensorEnabled(context);
        int orientation = context.getResources().getConfiguration().orientation;
        long usedRamBytes = CommonUtils.getTotalRamInBytes() - CommonUtils.calculateFreeRamInBytes(context);
        long diskUsedBytes = CommonUtils.calculateUsedDiskSpaceInBytes(Environment.getDataDirectory().getPath());
        ActivityManager.RunningAppProcessInfo runningAppProcessInfo = CommonUtils.getAppProcessInfo(context.getPackageName(), context);
        List<StackTraceElement[]> stacks = new LinkedList<>();
        StackTraceElement[] exceptionStack = ex.getStackTrace();
        Map<Thread, StackTraceElement[]> allStackTraces = Thread.getAllStackTraces();
        Thread[] threads = new Thread[allStackTraces.size()];
        int i = 0;
        for (Map.Entry<Thread, StackTraceElement[]> entry : allStackTraces.entrySet()) {
            threads[i] = entry.getKey();
            stacks.add(entry.getValue());
            i++;
        }
        if (!CommonUtils.getBooleanResourceValue(context, "com.crashlytics.CollectCustomKeys", true)) {
            attributes = new TreeMap<>();
        } else {
            attributes = Collections.unmodifiableMap(this.crashlytics.attributes);
            if (attributes != null && attributes.size() > 1) {
                attributes = new TreeMap<>(attributes);
            }
        }
        SessionDataWriter sessionDataWriter = this.sessionDataWriter;
        QueueFile queueFile = this.logFileManager.logFile;
        sessionDataWriter.runningAppProcessInfo = runningAppProcessInfo;
        sessionDataWriter.stacks = stacks;
        sessionDataWriter.exceptionStack = exceptionStack;
        sessionDataWriter.threads = threads;
        ByteString byteStringForLog = LogFileManager.getByteStringForLog(queueFile);
        if (byteStringForLog == null) {
            Fabric.getLogger();
        }
        CommonUtils.closeOrLog(queueFile, "There was a problem closing the Crashlytics log file.");
        cos.writeTag(10, 2);
        int computeUInt64Size = CodedOutputStream.computeUInt64Size(1, eventTime) + 0 + CodedOutputStream.computeBytesSize(2, ByteString.copyFromUtf8(eventType));
        int eventAppSize = sessionDataWriter.getEventAppSize(thread, ex, orientation, attributes);
        int computeTagSize = computeUInt64Size + eventAppSize + CodedOutputStream.computeTagSize(3) + CodedOutputStream.computeRawVarint32Size(eventAppSize);
        int eventDeviceSize$45a61cda = SessionDataWriter.getEventDeviceSize$45a61cda(batteryVelocity, orientation, usedRamBytes, diskUsedBytes);
        int computeTagSize2 = computeTagSize + eventDeviceSize$45a61cda + CodedOutputStream.computeTagSize(5) + CodedOutputStream.computeRawVarint32Size(eventDeviceSize$45a61cda);
        if (byteStringForLog != null) {
            int eventLogSize = SessionDataWriter.getEventLogSize(byteStringForLog);
            computeTagSize2 += eventLogSize + CodedOutputStream.computeTagSize(6) + CodedOutputStream.computeRawVarint32Size(eventLogSize);
        }
        cos.writeRawVarint32(computeTagSize2);
        cos.writeUInt64(1, eventTime);
        cos.writeBytes(2, ByteString.copyFromUtf8(eventType));
        cos.writeTag(3, 2);
        cos.writeRawVarint32(sessionDataWriter.getEventAppSize(thread, ex, orientation, attributes));
        sessionDataWriter.writeSessionEventAppExecution(cos, thread, ex);
        if (attributes != null && !attributes.isEmpty()) {
            SessionDataWriter.writeSessionEventAppCustomAttributes(cos, attributes);
        }
        if (sessionDataWriter.runningAppProcessInfo != null) {
            cos.writeBool(3, sessionDataWriter.runningAppProcessInfo.importance != 100);
        }
        cos.writeUInt32(4, orientation);
        cos.writeTag(5, 2);
        cos.writeRawVarint32(SessionDataWriter.getEventDeviceSize$45a61cda(batteryVelocity, orientation, usedRamBytes, diskUsedBytes));
        cos.writeFloat$255e752(batteryLevel);
        cos.writeSInt32$255f295(batteryVelocity);
        cos.writeBool(3, proximityEnabled);
        cos.writeUInt32(4, orientation);
        cos.writeUInt64(5, usedRamBytes);
        cos.writeUInt64(6, diskUsedBytes);
        if (byteStringForLog == null) {
            return;
        }
        cos.writeTag(6, 2);
        cos.writeRawVarint32(SessionDataWriter.getEventLogSize(byteStringForLog));
        cos.writeBytes(1, byteStringForLog);
    }

    private static void writeNonFatalEventsTo(CodedOutputStream cos, File[] nonFatalFiles, String sessionId) {
        Arrays.sort(nonFatalFiles, CommonUtils.FILE_MODIFIED_COMPARATOR);
        for (File nonFatalFile : nonFatalFiles) {
            try {
                Fabric.getLogger();
                String.format(Locale.US, "Found Non Fatal for session ID %s in %s ", sessionId, nonFatalFile.getName());
                writeToCosFromFile(cos, nonFatalFile);
            } catch (Exception e) {
                Fabric.getLogger().e("Fabric", "Error writting non-fatal to session.", e);
            }
        }
    }

    private void writeInitialPartsTo(CodedOutputStream cos, String sessionId) throws IOException {
        String[] arr$ = {"SessionUser", "SessionApp", "SessionOS", "SessionDevice"};
        for (int i$ = 0; i$ < 4; i$++) {
            String tag = arr$[i$];
            File[] sessionPartFiles = listFilesMatching(new FileNameContainsFilter(sessionId + tag));
            if (sessionPartFiles.length == 0) {
                Fabric.getLogger().e("Fabric", "Can't find " + tag + " data for session ID " + sessionId, null);
            } else {
                Fabric.getLogger();
                new StringBuilder("Collecting ").append(tag).append(" data for session ID ").append(sessionId);
                writeToCosFromFile(cos, sessionPartFiles[0]);
            }
        }
    }

    private static void writeToCosFromFile(CodedOutputStream cos, File file) throws IOException {
        int numRead;
        if (file.exists()) {
            byte[] bytes = new byte[(int) file.length()];
            FileInputStream fis = null;
            try {
                FileInputStream fis2 = new FileInputStream(file);
                int offset = 0;
                while (offset < bytes.length && (numRead = fis2.read(bytes, offset, bytes.length - offset)) >= 0) {
                    try {
                        offset += numRead;
                    } catch (Throwable th) {
                        th = th;
                        fis = fis2;
                        CommonUtils.closeOrLog(fis, "Failed to close file input stream.");
                        throw th;
                    }
                }
                CommonUtils.closeOrLog(fis2, "Failed to close file input stream.");
                cos.writeRawBytes(bytes);
            } catch (Throwable th2) {
                th = th2;
            }
        } else {
            Fabric.getLogger().e("Fabric", "Tried to include a file that doesn't exist: " + file.getName(), null);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r13v2, types: [java.io.File[]] */
    /* JADX WARN: Type inference failed for: r13v3 */
    /* JADX WARN: Type inference failed for: r13v5, types: [java.io.Closeable] */
    /* JADX WARN: Type inference failed for: r15v23, types: [java.io.File] */
    public void doCloseSessions() throws Exception {
        ClsFileOutputStream clsFileOutputStream;
        ClsFileOutputStream clsFileOutputStream2;
        CodedOutputStream codedOutputStream;
        ClsFileOutputStream clsFileOutputStream3;
        File[] fileArr;
        HashSet hashSet = new HashSet();
        File[] listSessionBeginFiles = listSessionBeginFiles();
        Arrays.sort(listSessionBeginFiles, LARGEST_FILE_NAME_FIRST);
        int min = Math.min(8, listSessionBeginFiles.length);
        for (int i = 0; i < min; i++) {
            hashSet.add(getSessionIdFromSessionFile(listSessionBeginFiles[i]));
        }
        ?? listFilesMatching = listFilesMatching(new AnySessionPartFileFilter((byte) 0));
        for (?? r15 : listFilesMatching) {
            Matcher matcher = SESSION_FILE_PATTERN.matcher(r15.getName());
            matcher.matches();
            if (!hashSet.contains(matcher.group(1))) {
                Fabric.getLogger();
                r15.delete();
            }
        }
        String currentSessionId = getCurrentSessionId();
        if (currentSessionId == null) {
            Fabric.getLogger();
            return;
        }
        try {
            try {
                clsFileOutputStream = new ClsFileOutputStream(this.filesDir, currentSessionId + "SessionUser");
            } catch (Exception e) {
                e = e;
                clsFileOutputStream = null;
            } catch (Throwable th) {
                th = th;
                listFilesMatching = 0;
                CommonUtils.flushOrLog(null, "Failed to flush session user file.");
                CommonUtils.closeOrLog(listFilesMatching, "Failed to close session user file.");
                throw th;
            }
            try {
                CodedOutputStream newInstance = CodedOutputStream.newInstance(clsFileOutputStream);
                Crashlytics crashlytics = this.crashlytics;
                String str = crashlytics.idManager.collectUserIds ? crashlytics.userId : null;
                Crashlytics crashlytics2 = this.crashlytics;
                String str2 = crashlytics2.idManager.collectUserIds ? crashlytics2.userName : null;
                Crashlytics crashlytics3 = this.crashlytics;
                String str3 = crashlytics3.idManager.collectUserIds ? crashlytics3.userEmail : null;
                if (str == null && str2 == null && str3 == null) {
                    CommonUtils.flushOrLog(newInstance, "Failed to flush session user file.");
                    CommonUtils.closeOrLog(clsFileOutputStream, "Failed to close session user file.");
                } else {
                    if (str == null) {
                        str = "";
                    }
                    ByteString copyFromUtf8 = ByteString.copyFromUtf8(str);
                    ByteString stringToByteString = SessionDataWriter.stringToByteString(str2);
                    ByteString stringToByteString2 = SessionDataWriter.stringToByteString(str3);
                    int computeBytesSize = CodedOutputStream.computeBytesSize(1, copyFromUtf8) + 0;
                    if (str2 != null) {
                        computeBytesSize += CodedOutputStream.computeBytesSize(2, stringToByteString);
                    }
                    if (str3 != null) {
                        computeBytesSize += CodedOutputStream.computeBytesSize(3, stringToByteString2);
                    }
                    newInstance.writeTag(6, 2);
                    newInstance.writeRawVarint32(computeBytesSize);
                    newInstance.writeBytes(1, copyFromUtf8);
                    if (str2 != null) {
                        newInstance.writeBytes(2, stringToByteString);
                    }
                    if (str3 != null) {
                        newInstance.writeBytes(3, stringToByteString2);
                    }
                    CommonUtils.flushOrLog(newInstance, "Failed to flush session user file.");
                    CommonUtils.closeOrLog(clsFileOutputStream, "Failed to close session user file.");
                }
                SessionSettingsData settingsData = Crashlytics.getSessionSettingsData();
                if (settingsData == null) {
                    Fabric.getLogger();
                    return;
                }
                int maxLoggedExceptionsCount = settingsData.maxCustomExceptionEvents;
                Fabric.getLogger();
                File[] sessionBeginFiles = listSessionBeginFiles();
                if (sessionBeginFiles == null || sessionBeginFiles.length <= 0) {
                    return;
                }
                for (File sessionBeginFile : sessionBeginFiles) {
                    String sessionIdentifier = getSessionIdFromSessionFile(sessionBeginFile);
                    Fabric.getLogger();
                    Fabric.getLogger();
                    File[] listFilesMatching2 = listFilesMatching(new FileNameContainsFilter(sessionIdentifier + "SessionCrash"));
                    boolean z = listFilesMatching2 != null && listFilesMatching2.length > 0;
                    Fabric.getLogger();
                    String.format(Locale.US, "Session %s has fatal exception: %s", sessionIdentifier, Boolean.valueOf(z));
                    File[] listFilesMatching3 = listFilesMatching(new FileNameContainsFilter(sessionIdentifier + "SessionEvent"));
                    boolean z2 = listFilesMatching3 != null && listFilesMatching3.length > 0;
                    Fabric.getLogger();
                    String.format(Locale.US, "Session %s has non-fatal exceptions: %s", sessionIdentifier, Boolean.valueOf(z2));
                    if (z || z2) {
                        CodedOutputStream codedOutputStream2 = null;
                        try {
                            clsFileOutputStream2 = new ClsFileOutputStream(this.filesDir, sessionIdentifier);
                            try {
                                try {
                                    CodedOutputStream newInstance2 = CodedOutputStream.newInstance(clsFileOutputStream2);
                                    try {
                                        Fabric.getLogger();
                                        writeToCosFromFile(newInstance2, sessionBeginFile);
                                        newInstance2.writeUInt64(4, new Date().getTime() / 1000);
                                        newInstance2.writeBool(5, z);
                                        writeInitialPartsTo(newInstance2, sessionIdentifier);
                                        if (z2) {
                                            if (listFilesMatching3.length > maxLoggedExceptionsCount) {
                                                Fabric.getLogger();
                                                String.format(Locale.US, "Trimming down to %d logged exceptions.", Integer.valueOf(maxLoggedExceptionsCount));
                                                Utils.capFileCount(this.filesDir, new FileNameContainsFilter(sessionIdentifier + "SessionEvent"), maxLoggedExceptionsCount, SMALLEST_FILE_NAME_FIRST);
                                                fileArr = listFilesMatching(new FileNameContainsFilter(sessionIdentifier + "SessionEvent"));
                                            } else {
                                                fileArr = listFilesMatching3;
                                            }
                                            writeNonFatalEventsTo(newInstance2, fileArr, sessionIdentifier);
                                        }
                                        if (z) {
                                            writeToCosFromFile(newInstance2, listFilesMatching2[0]);
                                        }
                                        newInstance2.writeUInt32(11, 1);
                                        newInstance2.writeEnum(12, 3);
                                        CommonUtils.flushOrLog(newInstance2, "Error flushing session file stream");
                                        CommonUtils.closeOrLog(clsFileOutputStream2, "Failed to close CLS file");
                                    } catch (Exception e2) {
                                        e = e2;
                                        codedOutputStream = newInstance2;
                                        clsFileOutputStream3 = clsFileOutputStream2;
                                        try {
                                            Fabric.getLogger().e("Fabric", "Failed to write session file for session ID: " + sessionIdentifier, e);
                                            ExceptionUtils.writeStackTraceIfNotNull(e, clsFileOutputStream3);
                                            CommonUtils.flushOrLog(codedOutputStream, "Error flushing session file stream");
                                            if (clsFileOutputStream3 != null) {
                                                try {
                                                    clsFileOutputStream3.closeInProgressStream();
                                                } catch (IOException e3) {
                                                    Fabric.getLogger().e("Fabric", "Error closing session file stream in the presence of an exception", e3);
                                                }
                                            }
                                            Fabric.getLogger();
                                            deleteSessionPartFilesFor(sessionIdentifier);
                                        } catch (Throwable th2) {
                                            th = th2;
                                            clsFileOutputStream2 = clsFileOutputStream3;
                                            codedOutputStream2 = codedOutputStream;
                                            CommonUtils.flushOrLog(codedOutputStream2, "Error flushing session file stream");
                                            CommonUtils.closeOrLog(clsFileOutputStream2, "Failed to close CLS file");
                                            throw th;
                                        }
                                    }
                                } catch (Throwable th3) {
                                    th = th3;
                                    CommonUtils.flushOrLog(codedOutputStream2, "Error flushing session file stream");
                                    CommonUtils.closeOrLog(clsFileOutputStream2, "Failed to close CLS file");
                                    throw th;
                                }
                            } catch (Exception e4) {
                                e = e4;
                                codedOutputStream = null;
                                clsFileOutputStream3 = clsFileOutputStream2;
                            }
                        } catch (Exception e5) {
                            e = e5;
                            codedOutputStream = null;
                            clsFileOutputStream3 = null;
                        } catch (Throwable th4) {
                            th = th4;
                            clsFileOutputStream2 = null;
                        }
                    } else {
                        Fabric.getLogger();
                    }
                    Fabric.getLogger();
                    deleteSessionPartFilesFor(sessionIdentifier);
                }
            } catch (Exception e6) {
                e = e6;
                ExceptionUtils.writeStackTraceIfNotNull(e, clsFileOutputStream);
                throw e;
            }
        } catch (Throwable th5) {
            th = th5;
            CommonUtils.flushOrLog(null, "Failed to flush session user file.");
            CommonUtils.closeOrLog(listFilesMatching, "Failed to close session user file.");
            throw th;
        }
    }

    private void deleteSessionPartFilesFor(String sessionId) {
        File[] arr$ = listFilesMatching(new SessionPartFileFilter(sessionId));
        for (File file : arr$) {
            file.delete();
        }
    }

    private void sendSessionReports() {
        File[] arr$ = listFilesMatching(SESSION_FILE_FILTER);
        for (final File toSend : arr$) {
            this.executorServiceWrapper.executeAsync(new Runnable() { // from class: com.crashlytics.android.CrashlyticsUncaughtExceptionHandler.14
                @Override // java.lang.Runnable
                public final void run() {
                    if (CommonUtils.canTryConnection(CrashlyticsUncaughtExceptionHandler.this.crashlytics.context)) {
                        Fabric.getLogger();
                        SettingsData settingsData = Settings.LazyHolder.access$100().awaitSettingsData();
                        CreateReportSpiCall call = CrashlyticsUncaughtExceptionHandler.this.crashlytics.getCreateReportSpiCall(settingsData);
                        if (call != null) {
                            new ReportUploader(call).forceUpload(new SessionReport(toSend, CrashlyticsUncaughtExceptionHandler.SEND_AT_CRASHTIME_HEADER));
                        }
                    }
                }
            });
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:12:0x0065  */
    /* JADX WARN: Removed duplicated region for block: B:15:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    static /* synthetic */ void access$200(com.crashlytics.android.CrashlyticsUncaughtExceptionHandler r7, java.util.Date r8, java.lang.Thread r9, java.lang.Throwable r10) throws java.lang.Exception {
        /*
            r1 = 0
            java.io.File r0 = new java.io.File     // Catch: java.lang.Exception -> L79 java.lang.Throwable -> L98
            java.io.File r2 = r7.filesDir     // Catch: java.lang.Exception -> L79 java.lang.Throwable -> L98
            java.lang.String r3 = "crash_marker"
            r0.<init>(r2, r3)     // Catch: java.lang.Exception -> L79 java.lang.Throwable -> L98
            r0.createNewFile()     // Catch: java.lang.Exception -> L79 java.lang.Throwable -> L98
            java.lang.String r0 = r7.getCurrentSessionId()     // Catch: java.lang.Exception -> L79 java.lang.Throwable -> L98
            if (r0 == 0) goto L69
            com.crashlytics.android.Crashlytics.recordFatalExceptionEvent(r0)     // Catch: java.lang.Exception -> L79 java.lang.Throwable -> L98
            com.crashlytics.android.ClsFileOutputStream r6 = new com.crashlytics.android.ClsFileOutputStream     // Catch: java.lang.Exception -> L79 java.lang.Throwable -> L98
            java.io.File r2 = r7.filesDir     // Catch: java.lang.Exception -> L79 java.lang.Throwable -> L98
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch: java.lang.Exception -> L79 java.lang.Throwable -> L98
            r3.<init>()     // Catch: java.lang.Exception -> L79 java.lang.Throwable -> L98
            java.lang.StringBuilder r0 = r3.append(r0)     // Catch: java.lang.Exception -> L79 java.lang.Throwable -> L98
            java.lang.String r3 = "SessionCrash"
            java.lang.StringBuilder r0 = r0.append(r3)     // Catch: java.lang.Exception -> L79 java.lang.Throwable -> L98
            java.lang.String r0 = r0.toString()     // Catch: java.lang.Exception -> L79 java.lang.Throwable -> L98
            r6.<init>(r2, r0)     // Catch: java.lang.Exception -> L79 java.lang.Throwable -> L98
            com.crashlytics.android.CodedOutputStream r1 = com.crashlytics.android.CodedOutputStream.newInstance(r6)     // Catch: java.lang.Throwable -> La7 java.lang.Exception -> La9
            java.lang.String r5 = "crash"
            r0 = r7
            r2 = r8
            r3 = r9
            r4 = r10
            r0.writeSessionEvent$42ff7c1f(r1, r2, r3, r4, r5)     // Catch: java.lang.Throwable -> La7 java.lang.Exception -> La9
            r0 = r6
        L41:
            java.lang.String r2 = "Failed to flush to session begin file."
            io.fabric.sdk.android.services.common.CommonUtils.flushOrLog(r1, r2)
            java.lang.String r1 = "Failed to close fatal exception file output stream."
            io.fabric.sdk.android.services.common.CommonUtils.closeOrLog(r0, r1)
        L4d:
            r7.doCloseSessions()
            r7.doOpenSession()
            java.io.File r0 = r7.filesDir
            java.io.FilenameFilter r1 = com.crashlytics.android.CrashlyticsUncaughtExceptionHandler.SESSION_FILE_FILTER
            r2 = 4
            java.util.Comparator<java.io.File> r3 = com.crashlytics.android.CrashlyticsUncaughtExceptionHandler.SMALLEST_FILE_NAME_FIRST
            com.crashlytics.android.Utils.capFileCount(r0, r1, r2, r3)
            com.crashlytics.android.Crashlytics r0 = r7.crashlytics
            boolean r0 = r0.shouldPromptUserBeforeSendingCrashReports()
            if (r0 != 0) goto L68
            r7.sendSessionReports()
        L68:
            return
        L69:
            io.fabric.sdk.android.Logger r0 = io.fabric.sdk.android.Fabric.getLogger()     // Catch: java.lang.Exception -> L79 java.lang.Throwable -> L98
            java.lang.String r2 = "Fabric"
            java.lang.String r3 = "Tried to write a fatal exception while no session was open."
            r4 = 0
            r0.e(r2, r3, r4)     // Catch: java.lang.Exception -> L79 java.lang.Throwable -> L98
            r0 = r1
            goto L41
        L79:
            r0 = move-exception
            r6 = r1
        L7b:
            io.fabric.sdk.android.Logger r2 = io.fabric.sdk.android.Fabric.getLogger()     // Catch: java.lang.Throwable -> La7
            java.lang.String r3 = "Fabric"
            java.lang.String r4 = "An error occurred in the fatal exception logger"
            r2.e(r3, r4, r0)     // Catch: java.lang.Throwable -> La7
            com.crashlytics.android.ExceptionUtils.writeStackTraceIfNotNull(r0, r6)     // Catch: java.lang.Throwable -> La7
            java.lang.String r0 = "Failed to flush to session begin file."
            io.fabric.sdk.android.services.common.CommonUtils.flushOrLog(r1, r0)
            java.lang.String r0 = "Failed to close fatal exception file output stream."
            io.fabric.sdk.android.services.common.CommonUtils.closeOrLog(r6, r0)
            goto L4d
        L98:
            r0 = move-exception
            r6 = r1
        L9a:
            java.lang.String r2 = "Failed to flush to session begin file."
            io.fabric.sdk.android.services.common.CommonUtils.flushOrLog(r1, r2)
            java.lang.String r1 = "Failed to close fatal exception file output stream."
            io.fabric.sdk.android.services.common.CommonUtils.closeOrLog(r6, r1)
            throw r0
        La7:
            r0 = move-exception
            goto L9a
        La9:
            r0 = move-exception
            goto L7b
        */
        throw new UnsupportedOperationException("Method not decompiled: com.crashlytics.android.CrashlyticsUncaughtExceptionHandler.access$200(com.crashlytics.android.CrashlyticsUncaughtExceptionHandler, java.util.Date, java.lang.Thread, java.lang.Throwable):void");
    }

    static /* synthetic */ void access$800(CrashlyticsUncaughtExceptionHandler x0, SessionEventData x1) throws IOException {
        ClsFileOutputStream clsFileOutputStream;
        Throwable th;
        CodedOutputStream codedOutputStream;
        Exception exc;
        ClsFileOutputStream clsFileOutputStream2;
        CodedOutputStream codedOutputStream2 = null;
        try {
            String currentSessionId = x0.getCurrentSessionId();
            if (currentSessionId != null) {
                clsFileOutputStream2 = new ClsFileOutputStream(x0.filesDir, currentSessionId + "SessionCrash");
                try {
                    codedOutputStream2 = CodedOutputStream.newInstance(clsFileOutputStream2);
                } catch (Exception e) {
                    clsFileOutputStream = clsFileOutputStream2;
                    codedOutputStream = null;
                    exc = e;
                } catch (Throwable th2) {
                    clsFileOutputStream = clsFileOutputStream2;
                    codedOutputStream = null;
                    th = th2;
                }
                try {
                    NativeCrashWriter.writeNativeCrash(x1, codedOutputStream2);
                } catch (Exception e2) {
                    clsFileOutputStream = clsFileOutputStream2;
                    codedOutputStream = codedOutputStream2;
                    exc = e2;
                    try {
                        Fabric.getLogger().e("Fabric", "An error occurred in the native crash logger", exc);
                        ExceptionUtils.writeStackTraceIfNotNull(exc, clsFileOutputStream);
                        CommonUtils.flushOrLog(codedOutputStream, "Failed to flush to session begin file.");
                        CommonUtils.closeOrLog(clsFileOutputStream, "Failed to close fatal exception file output stream.");
                        return;
                    } catch (Throwable th3) {
                        th = th3;
                        CommonUtils.flushOrLog(codedOutputStream, "Failed to flush to session begin file.");
                        CommonUtils.closeOrLog(clsFileOutputStream, "Failed to close fatal exception file output stream.");
                        throw th;
                    }
                } catch (Throwable th4) {
                    clsFileOutputStream = clsFileOutputStream2;
                    codedOutputStream = codedOutputStream2;
                    th = th4;
                    CommonUtils.flushOrLog(codedOutputStream, "Failed to flush to session begin file.");
                    CommonUtils.closeOrLog(clsFileOutputStream, "Failed to close fatal exception file output stream.");
                    throw th;
                }
            } else {
                Fabric.getLogger().e("Fabric", "Tried to write a native crash while no session was open.", null);
                clsFileOutputStream2 = null;
            }
            CommonUtils.flushOrLog(codedOutputStream2, "Failed to flush to session begin file.");
            CommonUtils.closeOrLog(clsFileOutputStream2, "Failed to close fatal exception file output stream.");
        } catch (Exception e3) {
            clsFileOutputStream = null;
            exc = e3;
            codedOutputStream = null;
        } catch (Throwable th5) {
            clsFileOutputStream = null;
            th = th5;
            codedOutputStream = null;
        }
    }
}
