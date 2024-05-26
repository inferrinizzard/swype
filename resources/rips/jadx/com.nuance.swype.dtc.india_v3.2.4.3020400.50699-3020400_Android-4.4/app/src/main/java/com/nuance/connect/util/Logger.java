package com.nuance.connect.util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/* loaded from: classes.dex */
public class Logger {
    public static final int ALL = 1;
    public static final int ASSERT = 7;
    public static final int DEBUG = 3;
    public static final int ERROR = 6;
    public static final int INFO = 4;
    protected static final String TAG = "ConnectSDK";
    public static final int VERBOSE = 2;
    public static final int WARN = 5;
    private static File outputFile;
    private static Trace trace;
    protected static volatile int logLevel = 6;
    private static boolean developerLogEnabled = false;
    private static OutputMode outputMode = OutputMode.ANDROID_LOG;
    private static final Log developerLog = new DeveloperLog(LoggerType.DEVELOPER);
    private static final Log oemLog = new DeveloperLog(LoggerType.OEM);
    private static final Log customerLog = new DeveloperLog(LoggerType.CUSTOMER, 2);
    private static final int[] traceCreateLock = new int[0];

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class DeveloperLog implements Log {
        private static final int CUSTOM_LOG_LEVEL_DEFAULT = -1;
        private int customLogLevel;
        private final LoggerType loggerType;
        private final String prefix;
        private boolean threadIdLoggingEnabled;

        protected DeveloperLog(LoggerType loggerType) {
            this(loggerType, "");
        }

        protected DeveloperLog(LoggerType loggerType, int i) {
            this.customLogLevel = -1;
            this.prefix = "";
            this.loggerType = loggerType;
            this.customLogLevel = i;
        }

        protected DeveloperLog(LoggerType loggerType, String str) {
            this.customLogLevel = -1;
            if (str == null) {
                throw new NullPointerException();
            }
            this.prefix = str;
            this.loggerType = loggerType;
        }

        protected DeveloperLog(LoggerType loggerType, String str, int i) {
            this.customLogLevel = -1;
            if (str == null) {
                throw new NullPointerException();
            }
            this.prefix = str;
            this.loggerType = loggerType;
            this.customLogLevel = i;
        }

        protected DeveloperLog(LoggerType loggerType, String str, boolean z) {
            this(loggerType, str);
            this.threadIdLoggingEnabled = z;
        }

        @Override // com.nuance.connect.util.Logger.Log
        public void d(Object obj) {
            log(3, obj, null);
        }

        @Override // com.nuance.connect.util.Logger.Log
        public void d(Object obj, Object obj2) {
            logConcat(3, obj, obj2);
        }

        @Override // com.nuance.connect.util.Logger.Log
        public void d(Object obj, Object obj2, Object obj3) {
            logConcat(3, obj, obj2, obj3);
        }

        @Override // com.nuance.connect.util.Logger.Log
        public void d(Object obj, Object obj2, Object obj3, Object obj4) {
            logConcat(3, obj, obj2, obj3, obj4);
        }

        @Override // com.nuance.connect.util.Logger.Log
        public void d(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
            logConcat(3, obj, obj2, obj3, obj4, obj5);
        }

        @Override // com.nuance.connect.util.Logger.Log
        public void d(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6) {
            logConcat(3, obj, obj2, obj3, obj4, obj5, obj6);
        }

        @Override // com.nuance.connect.util.Logger.Log
        public void d(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7) {
            logConcat(3, obj, obj2, obj3, obj4, obj5, obj6, obj7);
        }

        @Override // com.nuance.connect.util.Logger.Log
        public void d(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8) {
            logConcat(3, obj, obj2, obj3, obj4, obj5, obj6, obj7, obj8);
        }

        @Override // com.nuance.connect.util.Logger.Log
        public void d(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9) {
            logConcat(3, obj, obj2, obj3, obj4, obj5, obj6, obj7, obj8, obj9);
        }

        @Override // com.nuance.connect.util.Logger.Log
        public void d(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9, Object obj10) {
            logConcat(3, obj, obj2, obj3, obj4, obj5, obj6, obj7, obj8, obj9, obj10);
        }

        @Override // com.nuance.connect.util.Logger.Log
        public void d(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9, Object obj10, Object obj11) {
            logConcat(3, obj, obj2, obj3, obj4, obj5, obj6, obj7, obj8, obj9, obj10, obj11);
        }

        @Override // com.nuance.connect.util.Logger.Log
        public void d(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9, Object obj10, Object obj11, Object obj12) {
            logConcat(3, obj, obj2, obj3, obj4, obj5, obj6, obj7, obj8, obj9, obj10, obj11, obj12);
        }

        @Override // com.nuance.connect.util.Logger.Log
        public void d(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9, Object obj10, Object obj11, Object obj12, Object obj13) {
            logConcat(3, obj, obj2, obj3, obj4, obj5, obj6, obj7, obj8, obj9, obj10, obj11, obj12, obj13);
        }

        @Override // com.nuance.connect.util.Logger.Log
        public void d(Object obj, Throwable th) {
            log(3, obj, th);
        }

        @Override // com.nuance.connect.util.Logger.Log
        public void e(Object obj) {
            log(6, obj, null);
        }

        @Override // com.nuance.connect.util.Logger.Log
        public void e(Object obj, Object obj2) {
            logConcat(6, obj, obj2);
        }

        @Override // com.nuance.connect.util.Logger.Log
        public void e(Object obj, Object obj2, Object obj3) {
            logConcat(6, obj, obj2, obj3);
        }

        @Override // com.nuance.connect.util.Logger.Log
        public void e(Object obj, Object obj2, Object obj3, Object obj4) {
            logConcat(6, obj, obj2, obj3, obj4);
        }

        @Override // com.nuance.connect.util.Logger.Log
        public void e(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
            logConcat(6, obj, obj2, obj3, obj4, obj5);
        }

        @Override // com.nuance.connect.util.Logger.Log
        public void e(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6) {
            logConcat(6, obj, obj2, obj3, obj4, obj5, obj6);
        }

        @Override // com.nuance.connect.util.Logger.Log
        public void e(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7) {
            logConcat(6, obj, obj2, obj3, obj4, obj5, obj6, obj7);
        }

        @Override // com.nuance.connect.util.Logger.Log
        public void e(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8) {
            logConcat(6, obj, obj2, obj3, obj4, obj5, obj6, obj7, obj8);
        }

        @Override // com.nuance.connect.util.Logger.Log
        public void e(Object obj, Throwable th) {
            log(6, obj, th);
        }

        @Override // com.nuance.connect.util.Logger.Log
        public void i(Object obj) {
            log(4, obj, null);
        }

        @Override // com.nuance.connect.util.Logger.Log
        public void i(Object obj, Object obj2) {
            logConcat(4, obj, obj2);
        }

        @Override // com.nuance.connect.util.Logger.Log
        public void i(Object obj, Object obj2, Object obj3) {
            logConcat(4, obj, obj2, obj3);
        }

        @Override // com.nuance.connect.util.Logger.Log
        public void i(Object obj, Object obj2, Object obj3, Object obj4) {
            logConcat(4, obj, obj2, obj3, obj4);
        }

        @Override // com.nuance.connect.util.Logger.Log
        public void i(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
            logConcat(4, obj, obj2, obj3, obj4, obj5);
        }

        @Override // com.nuance.connect.util.Logger.Log
        public void i(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6) {
            logConcat(4, obj, obj2, obj3, obj4, obj5, obj6);
        }

        @Override // com.nuance.connect.util.Logger.Log
        public void i(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7) {
            logConcat(4, obj, obj2, obj3, obj4, obj5, obj6, obj7);
        }

        @Override // com.nuance.connect.util.Logger.Log
        public void i(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8) {
            logConcat(4, obj, obj2, obj3, obj4, obj5, obj6, obj7, obj8);
        }

        @Override // com.nuance.connect.util.Logger.Log
        public void i(Object obj, Throwable th) {
            log(4, obj, th);
        }

        @Override // com.nuance.connect.util.Logger.Log
        public boolean isLoggable(int i) {
            boolean z = i >= (this.customLogLevel == -1 ? Logger.logLevel : this.customLogLevel);
            return this.loggerType == LoggerType.DEVELOPER ? z && Logger.developerLogEnabled : z;
        }

        protected void log(int i, Object obj, Throwable th) {
            if (isLoggable(i)) {
                StringBuilder sb = new StringBuilder(this.prefix);
                if (this.threadIdLoggingEnabled) {
                    sb.append("[").append(Thread.currentThread().getId()).append("] ");
                }
                sb.append(obj);
                if (th != null) {
                    if (isLoggable(3)) {
                        sb.append('\n').append(android.util.Log.getStackTraceString(th));
                    } else {
                        sb.append(' ').append(th);
                    }
                }
                if (Logger.outputMode == OutputMode.ANDROID_LOG || Logger.outputMode == OutputMode.LOG_AND_FILE) {
                    android.util.Log.println(i, Logger.TAG, sb.toString());
                }
                if ((Logger.outputMode == OutputMode.FILE || Logger.outputMode == OutputMode.LOG_AND_FILE) && Logger.outputFile != null) {
                    Logger.outputFile.exists();
                }
            }
        }

        protected void logConcat(int i, Object... objArr) {
            if (isLoggable(i)) {
                StringBuilder sb = new StringBuilder();
                for (Object obj : objArr) {
                    sb.append(obj);
                }
                log(i, sb.toString(), null);
            }
        }

        public void setCustomLogLevel(int i) {
            this.customLogLevel = i;
        }

        @Override // com.nuance.connect.util.Logger.Log
        public void v(Object obj) {
            log(2, obj, null);
        }

        @Override // com.nuance.connect.util.Logger.Log
        public void v(Object obj, Object obj2) {
            logConcat(2, obj, obj2);
        }

        @Override // com.nuance.connect.util.Logger.Log
        public void v(Object obj, Object obj2, Object obj3) {
            logConcat(2, obj, obj2, obj3);
        }

        @Override // com.nuance.connect.util.Logger.Log
        public void v(Object obj, Object obj2, Object obj3, Object obj4) {
            logConcat(2, obj, obj2, obj3, obj4);
        }

        @Override // com.nuance.connect.util.Logger.Log
        public void v(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
            logConcat(2, obj, obj2, obj3, obj4, obj5);
        }

        @Override // com.nuance.connect.util.Logger.Log
        public void v(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6) {
            logConcat(2, obj, obj2, obj3, obj4, obj5, obj6);
        }

        @Override // com.nuance.connect.util.Logger.Log
        public void v(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7) {
            logConcat(2, obj, obj2, obj3, obj4, obj5, obj6, obj7);
        }

        @Override // com.nuance.connect.util.Logger.Log
        public void v(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8) {
            logConcat(2, obj, obj2, obj3, obj4, obj5, obj6, obj7, obj8);
        }

        @Override // com.nuance.connect.util.Logger.Log
        public void v(Object obj, Throwable th) {
            log(2, obj, th);
        }

        @Override // com.nuance.connect.util.Logger.Log
        public void w(Object obj) {
            log(5, obj, null);
        }

        @Override // com.nuance.connect.util.Logger.Log
        public void w(Object obj, Object obj2) {
            logConcat(5, obj, obj2);
        }

        @Override // com.nuance.connect.util.Logger.Log
        public void w(Object obj, Object obj2, Object obj3) {
            logConcat(5, obj, obj2, obj3);
        }

        @Override // com.nuance.connect.util.Logger.Log
        public void w(Object obj, Object obj2, Object obj3, Object obj4) {
            logConcat(5, obj, obj2, obj3, obj4);
        }

        @Override // com.nuance.connect.util.Logger.Log
        public void w(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
            logConcat(5, obj, obj2, obj3, obj4, obj5);
        }

        @Override // com.nuance.connect.util.Logger.Log
        public void w(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6) {
            logConcat(5, obj, obj2, obj3, obj4, obj5, obj6);
        }

        @Override // com.nuance.connect.util.Logger.Log
        public void w(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7) {
            logConcat(5, obj, obj2, obj3, obj4, obj5, obj6, obj7);
        }

        @Override // com.nuance.connect.util.Logger.Log
        public void w(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8) {
            logConcat(5, obj, obj2, obj3, obj4, obj5, obj6, obj7, obj8);
        }

        @Override // com.nuance.connect.util.Logger.Log
        public void w(Object obj, Throwable th) {
            log(5, obj, th);
        }
    }

    /* loaded from: classes.dex */
    public interface Log {
        void d(Object obj);

        void d(Object obj, Object obj2);

        void d(Object obj, Object obj2, Object obj3);

        void d(Object obj, Object obj2, Object obj3, Object obj4);

        void d(Object obj, Object obj2, Object obj3, Object obj4, Object obj5);

        void d(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6);

        void d(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7);

        void d(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8);

        void d(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9);

        void d(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9, Object obj10);

        void d(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9, Object obj10, Object obj11);

        void d(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9, Object obj10, Object obj11, Object obj12);

        void d(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9, Object obj10, Object obj11, Object obj12, Object obj13);

        void d(Object obj, Throwable th);

        void e(Object obj);

        void e(Object obj, Object obj2);

        void e(Object obj, Object obj2, Object obj3);

        void e(Object obj, Object obj2, Object obj3, Object obj4);

        void e(Object obj, Object obj2, Object obj3, Object obj4, Object obj5);

        void e(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6);

        void e(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7);

        void e(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8);

        void e(Object obj, Throwable th);

        void i(Object obj);

        void i(Object obj, Object obj2);

        void i(Object obj, Object obj2, Object obj3);

        void i(Object obj, Object obj2, Object obj3, Object obj4);

        void i(Object obj, Object obj2, Object obj3, Object obj4, Object obj5);

        void i(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6);

        void i(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7);

        void i(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8);

        void i(Object obj, Throwable th);

        boolean isLoggable(int i);

        void v(Object obj);

        void v(Object obj, Object obj2);

        void v(Object obj, Object obj2, Object obj3);

        void v(Object obj, Object obj2, Object obj3, Object obj4);

        void v(Object obj, Object obj2, Object obj3, Object obj4, Object obj5);

        void v(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6);

        void v(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7);

        void v(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8);

        void v(Object obj, Throwable th);

        void w(Object obj);

        void w(Object obj, Object obj2);

        void w(Object obj, Object obj2, Object obj3);

        void w(Object obj, Object obj2, Object obj3, Object obj4);

        void w(Object obj, Object obj2, Object obj3, Object obj4, Object obj5);

        void w(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6);

        void w(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7);

        void w(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8);

        void w(Object obj, Throwable th);
    }

    /* loaded from: classes.dex */
    public enum LoggerType {
        DEVELOPER,
        OEM,
        CUSTOMER
    }

    /* loaded from: classes.dex */
    public enum OutputMode {
        ANDROID_LOG,
        FILE,
        LOG_AND_FILE
    }

    /* loaded from: classes.dex */
    public static class Trace extends DeveloperLog {
        private static final char INDENT_CHAR = '#';
        private final StringBuilder indentation;
        private Map<String, Long> traceMethodStartTime;

        private Trace() {
            super(LoggerType.DEVELOPER, "[TRACE]");
            this.indentation = new StringBuilder("#");
            this.traceMethodStartTime = new HashMap();
        }

        private StringBuilder curIndent() {
            StringBuilder sb;
            synchronized (this.indentation) {
                sb = this.indentation;
            }
            return sb;
        }

        private StringBuilder decIndent() {
            StringBuilder sb;
            synchronized (this.indentation) {
                if (this.indentation.length() > 0) {
                    this.indentation.deleteCharAt(this.indentation.length() - 1);
                }
                sb = this.indentation;
            }
            return sb;
        }

        private StringBuilder incIndent() {
            StringBuilder sb;
            synchronized (this.indentation) {
                this.indentation.append(INDENT_CHAR);
                sb = this.indentation;
            }
            return sb;
        }

        @Override // com.nuance.connect.util.Logger.DeveloperLog, com.nuance.connect.util.Logger.Log
        public /* bridge */ /* synthetic */ void d(Object obj) {
            super.d(obj);
        }

        @Override // com.nuance.connect.util.Logger.DeveloperLog, com.nuance.connect.util.Logger.Log
        public /* bridge */ /* synthetic */ void d(Object obj, Object obj2) {
            super.d(obj, obj2);
        }

        @Override // com.nuance.connect.util.Logger.DeveloperLog, com.nuance.connect.util.Logger.Log
        public /* bridge */ /* synthetic */ void d(Object obj, Object obj2, Object obj3) {
            super.d(obj, obj2, obj3);
        }

        @Override // com.nuance.connect.util.Logger.DeveloperLog, com.nuance.connect.util.Logger.Log
        public /* bridge */ /* synthetic */ void d(Object obj, Object obj2, Object obj3, Object obj4) {
            super.d(obj, obj2, obj3, obj4);
        }

        @Override // com.nuance.connect.util.Logger.DeveloperLog, com.nuance.connect.util.Logger.Log
        public /* bridge */ /* synthetic */ void d(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
            super.d(obj, obj2, obj3, obj4, obj5);
        }

        @Override // com.nuance.connect.util.Logger.DeveloperLog, com.nuance.connect.util.Logger.Log
        public /* bridge */ /* synthetic */ void d(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6) {
            super.d(obj, obj2, obj3, obj4, obj5, obj6);
        }

        @Override // com.nuance.connect.util.Logger.DeveloperLog, com.nuance.connect.util.Logger.Log
        public /* bridge */ /* synthetic */ void d(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7) {
            super.d(obj, obj2, obj3, obj4, obj5, obj6, obj7);
        }

        @Override // com.nuance.connect.util.Logger.DeveloperLog, com.nuance.connect.util.Logger.Log
        public /* bridge */ /* synthetic */ void d(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8) {
            super.d(obj, obj2, obj3, obj4, obj5, obj6, obj7, obj8);
        }

        @Override // com.nuance.connect.util.Logger.DeveloperLog, com.nuance.connect.util.Logger.Log
        public /* bridge */ /* synthetic */ void d(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9) {
            super.d(obj, obj2, obj3, obj4, obj5, obj6, obj7, obj8, obj9);
        }

        @Override // com.nuance.connect.util.Logger.DeveloperLog, com.nuance.connect.util.Logger.Log
        public /* bridge */ /* synthetic */ void d(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9, Object obj10) {
            super.d(obj, obj2, obj3, obj4, obj5, obj6, obj7, obj8, obj9, obj10);
        }

        @Override // com.nuance.connect.util.Logger.DeveloperLog, com.nuance.connect.util.Logger.Log
        public /* bridge */ /* synthetic */ void d(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9, Object obj10, Object obj11) {
            super.d(obj, obj2, obj3, obj4, obj5, obj6, obj7, obj8, obj9, obj10, obj11);
        }

        @Override // com.nuance.connect.util.Logger.DeveloperLog, com.nuance.connect.util.Logger.Log
        public /* bridge */ /* synthetic */ void d(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9, Object obj10, Object obj11, Object obj12) {
            super.d(obj, obj2, obj3, obj4, obj5, obj6, obj7, obj8, obj9, obj10, obj11, obj12);
        }

        @Override // com.nuance.connect.util.Logger.DeveloperLog, com.nuance.connect.util.Logger.Log
        public /* bridge */ /* synthetic */ void d(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9, Object obj10, Object obj11, Object obj12, Object obj13) {
            super.d(obj, obj2, obj3, obj4, obj5, obj6, obj7, obj8, obj9, obj10, obj11, obj12, obj13);
        }

        @Override // com.nuance.connect.util.Logger.DeveloperLog, com.nuance.connect.util.Logger.Log
        public /* bridge */ /* synthetic */ void d(Object obj, Throwable th) {
            super.d(obj, th);
        }

        @Override // com.nuance.connect.util.Logger.DeveloperLog, com.nuance.connect.util.Logger.Log
        public /* bridge */ /* synthetic */ void e(Object obj) {
            super.e(obj);
        }

        @Override // com.nuance.connect.util.Logger.DeveloperLog, com.nuance.connect.util.Logger.Log
        public /* bridge */ /* synthetic */ void e(Object obj, Object obj2) {
            super.e(obj, obj2);
        }

        @Override // com.nuance.connect.util.Logger.DeveloperLog, com.nuance.connect.util.Logger.Log
        public /* bridge */ /* synthetic */ void e(Object obj, Object obj2, Object obj3) {
            super.e(obj, obj2, obj3);
        }

        @Override // com.nuance.connect.util.Logger.DeveloperLog, com.nuance.connect.util.Logger.Log
        public /* bridge */ /* synthetic */ void e(Object obj, Object obj2, Object obj3, Object obj4) {
            super.e(obj, obj2, obj3, obj4);
        }

        @Override // com.nuance.connect.util.Logger.DeveloperLog, com.nuance.connect.util.Logger.Log
        public /* bridge */ /* synthetic */ void e(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
            super.e(obj, obj2, obj3, obj4, obj5);
        }

        @Override // com.nuance.connect.util.Logger.DeveloperLog, com.nuance.connect.util.Logger.Log
        public /* bridge */ /* synthetic */ void e(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6) {
            super.e(obj, obj2, obj3, obj4, obj5, obj6);
        }

        @Override // com.nuance.connect.util.Logger.DeveloperLog, com.nuance.connect.util.Logger.Log
        public /* bridge */ /* synthetic */ void e(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7) {
            super.e(obj, obj2, obj3, obj4, obj5, obj6, obj7);
        }

        @Override // com.nuance.connect.util.Logger.DeveloperLog, com.nuance.connect.util.Logger.Log
        public /* bridge */ /* synthetic */ void e(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8) {
            super.e(obj, obj2, obj3, obj4, obj5, obj6, obj7, obj8);
        }

        @Override // com.nuance.connect.util.Logger.DeveloperLog, com.nuance.connect.util.Logger.Log
        public /* bridge */ /* synthetic */ void e(Object obj, Throwable th) {
            super.e(obj, th);
        }

        public void enter(Object obj) {
            d(incIndent(), obj);
        }

        public void enter(Object obj, Object obj2) {
            d(incIndent(), obj, obj2);
        }

        public void enter(Object obj, Object obj2, Object obj3) {
            d(incIndent(), obj, obj2, obj3);
        }

        public void enter(Object obj, Object obj2, Object obj3, Object obj4) {
            d(incIndent(), obj, obj2, obj3, obj4);
        }

        public void enter(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
            d(incIndent(), obj, obj2, obj3, obj4, obj5);
        }

        public void enter(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6) {
            d(incIndent(), obj, obj2, obj3, obj4, obj5, obj6);
        }

        public void enter(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7) {
            d(incIndent(), obj, obj2, obj3, obj4, obj5, obj6, obj7);
        }

        public void enterMethod(String str) {
            d(incIndent(), str, "...");
            this.traceMethodStartTime.put(str + Thread.currentThread(), Long.valueOf(nanoTime()));
        }

        public void exit(Object obj) {
            d(curIndent(), obj);
            decIndent();
        }

        public void exit(Object obj, Object obj2) {
            d(curIndent(), obj, obj2);
            decIndent();
        }

        public void exit(Object obj, Object obj2, Object obj3) {
            d(curIndent(), obj, obj2, obj3);
            decIndent();
        }

        public void exit(Object obj, Object obj2, Object obj3, Object obj4) {
            d(curIndent(), obj, obj2, obj3, obj4);
            decIndent();
        }

        public void exit(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
            d(curIndent(), obj, obj2, obj3, obj4, obj5);
            decIndent();
        }

        public void exit(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6) {
            d(curIndent(), obj, obj2, obj3, obj4, obj5, obj6);
            decIndent();
        }

        public void exit(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7) {
            d(curIndent(), obj, obj2, obj3, obj4, obj5, obj6, obj7);
            decIndent();
        }

        public void exitMethod(String str) {
            d(curIndent(), str, "...took ", Long.valueOf(nanoTimeToMillis(nanoTime() - (this.traceMethodStartTime.containsKey(new StringBuilder().append(str).append(Thread.currentThread()).toString()) ? this.traceMethodStartTime.get(str + Thread.currentThread()).longValue() : 0L))), "ms");
            decIndent();
        }

        @Override // com.nuance.connect.util.Logger.DeveloperLog, com.nuance.connect.util.Logger.Log
        public /* bridge */ /* synthetic */ void i(Object obj) {
            super.i(obj);
        }

        @Override // com.nuance.connect.util.Logger.DeveloperLog, com.nuance.connect.util.Logger.Log
        public /* bridge */ /* synthetic */ void i(Object obj, Object obj2) {
            super.i(obj, obj2);
        }

        @Override // com.nuance.connect.util.Logger.DeveloperLog, com.nuance.connect.util.Logger.Log
        public /* bridge */ /* synthetic */ void i(Object obj, Object obj2, Object obj3) {
            super.i(obj, obj2, obj3);
        }

        @Override // com.nuance.connect.util.Logger.DeveloperLog, com.nuance.connect.util.Logger.Log
        public /* bridge */ /* synthetic */ void i(Object obj, Object obj2, Object obj3, Object obj4) {
            super.i(obj, obj2, obj3, obj4);
        }

        @Override // com.nuance.connect.util.Logger.DeveloperLog, com.nuance.connect.util.Logger.Log
        public /* bridge */ /* synthetic */ void i(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
            super.i(obj, obj2, obj3, obj4, obj5);
        }

        @Override // com.nuance.connect.util.Logger.DeveloperLog, com.nuance.connect.util.Logger.Log
        public /* bridge */ /* synthetic */ void i(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6) {
            super.i(obj, obj2, obj3, obj4, obj5, obj6);
        }

        @Override // com.nuance.connect.util.Logger.DeveloperLog, com.nuance.connect.util.Logger.Log
        public /* bridge */ /* synthetic */ void i(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7) {
            super.i(obj, obj2, obj3, obj4, obj5, obj6, obj7);
        }

        @Override // com.nuance.connect.util.Logger.DeveloperLog, com.nuance.connect.util.Logger.Log
        public /* bridge */ /* synthetic */ void i(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8) {
            super.i(obj, obj2, obj3, obj4, obj5, obj6, obj7, obj8);
        }

        @Override // com.nuance.connect.util.Logger.DeveloperLog, com.nuance.connect.util.Logger.Log
        public /* bridge */ /* synthetic */ void i(Object obj, Throwable th) {
            super.i(obj, th);
        }

        @Override // com.nuance.connect.util.Logger.DeveloperLog, com.nuance.connect.util.Logger.Log
        public /* bridge */ /* synthetic */ boolean isLoggable(int i) {
            return super.isLoggable(i);
        }

        public long nanoTime() {
            return System.nanoTime();
        }

        public long nanoTimeToMillis(long j) {
            return TimeUnit.MILLISECONDS.convert(j, TimeUnit.NANOSECONDS);
        }

        @Override // com.nuance.connect.util.Logger.DeveloperLog
        public /* bridge */ /* synthetic */ void setCustomLogLevel(int i) {
            super.setCustomLogLevel(i);
        }

        @Override // com.nuance.connect.util.Logger.DeveloperLog, com.nuance.connect.util.Logger.Log
        public /* bridge */ /* synthetic */ void v(Object obj) {
            super.v(obj);
        }

        @Override // com.nuance.connect.util.Logger.DeveloperLog, com.nuance.connect.util.Logger.Log
        public /* bridge */ /* synthetic */ void v(Object obj, Object obj2) {
            super.v(obj, obj2);
        }

        @Override // com.nuance.connect.util.Logger.DeveloperLog, com.nuance.connect.util.Logger.Log
        public /* bridge */ /* synthetic */ void v(Object obj, Object obj2, Object obj3) {
            super.v(obj, obj2, obj3);
        }

        @Override // com.nuance.connect.util.Logger.DeveloperLog, com.nuance.connect.util.Logger.Log
        public /* bridge */ /* synthetic */ void v(Object obj, Object obj2, Object obj3, Object obj4) {
            super.v(obj, obj2, obj3, obj4);
        }

        @Override // com.nuance.connect.util.Logger.DeveloperLog, com.nuance.connect.util.Logger.Log
        public /* bridge */ /* synthetic */ void v(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
            super.v(obj, obj2, obj3, obj4, obj5);
        }

        @Override // com.nuance.connect.util.Logger.DeveloperLog, com.nuance.connect.util.Logger.Log
        public /* bridge */ /* synthetic */ void v(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6) {
            super.v(obj, obj2, obj3, obj4, obj5, obj6);
        }

        @Override // com.nuance.connect.util.Logger.DeveloperLog, com.nuance.connect.util.Logger.Log
        public /* bridge */ /* synthetic */ void v(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7) {
            super.v(obj, obj2, obj3, obj4, obj5, obj6, obj7);
        }

        @Override // com.nuance.connect.util.Logger.DeveloperLog, com.nuance.connect.util.Logger.Log
        public /* bridge */ /* synthetic */ void v(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8) {
            super.v(obj, obj2, obj3, obj4, obj5, obj6, obj7, obj8);
        }

        @Override // com.nuance.connect.util.Logger.DeveloperLog, com.nuance.connect.util.Logger.Log
        public /* bridge */ /* synthetic */ void v(Object obj, Throwable th) {
            super.v(obj, th);
        }

        @Override // com.nuance.connect.util.Logger.DeveloperLog, com.nuance.connect.util.Logger.Log
        public /* bridge */ /* synthetic */ void w(Object obj) {
            super.w(obj);
        }

        @Override // com.nuance.connect.util.Logger.DeveloperLog, com.nuance.connect.util.Logger.Log
        public /* bridge */ /* synthetic */ void w(Object obj, Object obj2) {
            super.w(obj, obj2);
        }

        @Override // com.nuance.connect.util.Logger.DeveloperLog, com.nuance.connect.util.Logger.Log
        public /* bridge */ /* synthetic */ void w(Object obj, Object obj2, Object obj3) {
            super.w(obj, obj2, obj3);
        }

        @Override // com.nuance.connect.util.Logger.DeveloperLog, com.nuance.connect.util.Logger.Log
        public /* bridge */ /* synthetic */ void w(Object obj, Object obj2, Object obj3, Object obj4) {
            super.w(obj, obj2, obj3, obj4);
        }

        @Override // com.nuance.connect.util.Logger.DeveloperLog, com.nuance.connect.util.Logger.Log
        public /* bridge */ /* synthetic */ void w(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
            super.w(obj, obj2, obj3, obj4, obj5);
        }

        @Override // com.nuance.connect.util.Logger.DeveloperLog, com.nuance.connect.util.Logger.Log
        public /* bridge */ /* synthetic */ void w(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6) {
            super.w(obj, obj2, obj3, obj4, obj5, obj6);
        }

        @Override // com.nuance.connect.util.Logger.DeveloperLog, com.nuance.connect.util.Logger.Log
        public /* bridge */ /* synthetic */ void w(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7) {
            super.w(obj, obj2, obj3, obj4, obj5, obj6, obj7);
        }

        @Override // com.nuance.connect.util.Logger.DeveloperLog, com.nuance.connect.util.Logger.Log
        public /* bridge */ /* synthetic */ void w(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8) {
            super.w(obj, obj2, obj3, obj4, obj5, obj6, obj7, obj8);
        }

        @Override // com.nuance.connect.util.Logger.DeveloperLog, com.nuance.connect.util.Logger.Log
        public /* bridge */ /* synthetic */ void w(Object obj, Throwable th) {
            super.w(obj, th);
        }
    }

    private Logger() {
    }

    public static void configure(boolean z, int i, OutputMode outputMode2, File file) {
        outputMode = outputMode2;
        if (i <= 0 || i > 7) {
            throw new IllegalArgumentException("Unknown log level " + i);
        }
        if (outputMode != OutputMode.ANDROID_LOG && file == null) {
            throw new IllegalArgumentException("Log file required for selected output");
        }
        outputFile = file;
        if (logLevel != i) {
            logLevel = i;
            android.util.Log.v(TAG, "Changing log level to : " + i);
        }
        if (z != developerLogEnabled) {
            developerLogEnabled = z;
            getLog(LoggerType.OEM).v("Developer log enabled: " + z);
        }
    }

    public static Log getLog(LoggerType loggerType) {
        switch (loggerType) {
            case OEM:
                return oemLog;
            case CUSTOMER:
                return customerLog;
            default:
                return developerLog;
        }
    }

    public static Log getLog(LoggerType loggerType, String str) {
        return (str == null || str.length() == 0) ? getLog(loggerType) : new DeveloperLog(loggerType, "[" + str + "] ");
    }

    public static Log getThreadLog(LoggerType loggerType, String str) {
        return (str == null || str.length() == 0) ? getLog(loggerType) : new DeveloperLog(loggerType, "[" + str + "] ", true);
    }

    public static Trace getTrace() {
        synchronized (traceCreateLock) {
            if (trace == null) {
                trace = new Trace();
            }
        }
        return trace;
    }
}
