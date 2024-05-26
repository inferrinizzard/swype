package com.nuance.swype.util;

import android.annotation.SuppressLint;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public final class LogManager {
    private static volatile int loggableLevel = 4;
    private static final Log rootLog = new AndroidLog();
    private static Trace trace;

    /* loaded from: classes.dex */
    public interface Log {
        void d(Object obj, Throwable th);

        void d(Object... objArr);

        void e(Object obj);

        void e(Object obj, Throwable th);

        void i(Object obj);

        void v(Object obj);

        void w(Object obj);
    }

    public static Log getLog() {
        return rootLog;
    }

    public static Log getLog(String where) {
        return (where == null || where.length() == 0) ? rootLog : new AndroidLog("[" + where + "] ");
    }

    /* loaded from: classes.dex */
    private static class AndroidLog implements Log {
        private final String prefix;

        AndroidLog() {
            this("");
        }

        AndroidLog(String prefix) {
            if (prefix == null) {
                throw new NullPointerException();
            }
            this.prefix = prefix;
        }

        public boolean isLoggable(int level) {
            return level >= LogManager.loggableLevel;
        }

        @Override // com.nuance.swype.util.LogManager.Log
        public void d(Object message, Throwable t) {
            log(3, message, t);
        }

        @Override // com.nuance.swype.util.LogManager.Log
        public void i(Object message) {
            log(4, message, null);
        }

        @Override // com.nuance.swype.util.LogManager.Log
        public void e(Object message) {
            log(6, message, null);
        }

        @Override // com.nuance.swype.util.LogManager.Log
        public void e(Object message, Throwable t) {
            log(6, message, t);
        }

        @Override // com.nuance.swype.util.LogManager.Log
        public void v(Object message) {
            log(2, message, null);
        }

        @Override // com.nuance.swype.util.LogManager.Log
        public void w(Object message) {
            log(5, message, null);
        }

        @SuppressLint({"LogTagMismatch"})
        private void log(int level, Object message, Throwable t) {
            if (isLoggable(level)) {
                StringBuilder sb = new StringBuilder(this.prefix).append(message);
                if (t != null) {
                    if (isLoggable(3)) {
                        sb.append('\n').append(android.util.Log.getStackTraceString(t));
                    } else {
                        sb.append(' ').append(t);
                    }
                }
                android.util.Log.println(level, "Swype", sb.toString());
            }
        }

        @Override // com.nuance.swype.util.LogManager.Log
        public void d(Object... message) {
            StringBuilder sb = new StringBuilder(this.prefix);
            for (Object obj : message) {
                sb.append(obj);
            }
        }
    }

    public static Long calculateAverage(List<Long> list) {
        Long sum = 0L;
        for (Long time : list) {
            sum = Long.valueOf(sum.longValue() + time.longValue());
        }
        return Long.valueOf(sum.longValue() / list.size());
    }

    public static Trace getTrace() {
        if (trace == null) {
            trace = new Trace((byte) 0);
        }
        return trace;
    }

    /* loaded from: classes.dex */
    public static class Trace extends AndroidLog {
        private final char INDENT_CHAR;
        private final StringBuilder indentation;
        private final Map<String, Long> traceMethodStartTime;

        @Override // com.nuance.swype.util.LogManager.AndroidLog, com.nuance.swype.util.LogManager.Log
        public final /* bridge */ /* synthetic */ void d(Object obj, Throwable th) {
            super.d(obj, th);
        }

        @Override // com.nuance.swype.util.LogManager.AndroidLog, com.nuance.swype.util.LogManager.Log
        public final /* bridge */ /* synthetic */ void d(Object[] objArr) {
            super.d(objArr);
        }

        @Override // com.nuance.swype.util.LogManager.AndroidLog, com.nuance.swype.util.LogManager.Log
        public final /* bridge */ /* synthetic */ void e(Object obj) {
            super.e(obj);
        }

        @Override // com.nuance.swype.util.LogManager.AndroidLog, com.nuance.swype.util.LogManager.Log
        public final /* bridge */ /* synthetic */ void e(Object obj, Throwable th) {
            super.e(obj, th);
        }

        @Override // com.nuance.swype.util.LogManager.AndroidLog, com.nuance.swype.util.LogManager.Log
        public final /* bridge */ /* synthetic */ void i(Object obj) {
            super.i(obj);
        }

        @Override // com.nuance.swype.util.LogManager.AndroidLog
        public final /* bridge */ /* synthetic */ boolean isLoggable(int i) {
            return super.isLoggable(i);
        }

        @Override // com.nuance.swype.util.LogManager.AndroidLog, com.nuance.swype.util.LogManager.Log
        public final /* bridge */ /* synthetic */ void v(Object obj) {
            super.v(obj);
        }

        @Override // com.nuance.swype.util.LogManager.AndroidLog, com.nuance.swype.util.LogManager.Log
        public final /* bridge */ /* synthetic */ void w(Object obj) {
            super.w(obj);
        }

        /* synthetic */ Trace(byte b) {
            this();
        }

        private Trace() {
            super("[TRACE]");
            this.INDENT_CHAR = '#';
            this.indentation = new StringBuilder("#");
            this.traceMethodStartTime = new HashMap();
        }
    }
}
