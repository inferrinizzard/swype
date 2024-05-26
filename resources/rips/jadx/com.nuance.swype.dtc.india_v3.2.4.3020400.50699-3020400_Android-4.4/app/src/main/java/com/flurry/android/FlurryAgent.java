package com.flurry.android;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import com.flurry.sdk.hk;
import com.flurry.sdk.ja;
import com.flurry.sdk.jd;
import com.flurry.sdk.jq;
import com.flurry.sdk.jr;
import com.flurry.sdk.ka;
import com.flurry.sdk.kb;
import com.flurry.sdk.kf;
import com.flurry.sdk.le;
import com.flurry.sdk.lf;
import com.flurry.sdk.li;
import com.flurry.sdk.mb;
import com.nuance.swype.input.IME;
import java.util.Map;

/* loaded from: classes.dex */
public final class FlurryAgent {
    private static final String a = FlurryAgent.class.getSimpleName();
    private static final ka<le> b = new ka<le>() { // from class: com.flurry.android.FlurryAgent.1
        AnonymousClass1() {
        }

        @Override // com.flurry.sdk.ka
        public final /* synthetic */ void a(le leVar) {
            jr.a().a(new Runnable() { // from class: com.flurry.android.FlurryAgent.1.1
                final /* synthetic */ le a;

                RunnableC00051(le leVar2) {
                    r2 = leVar2;
                }

                @Override // java.lang.Runnable
                public final void run() {
                    switch (AnonymousClass2.a[r2.c - 1]) {
                        case 1:
                            if (FlurryAgent.c != null) {
                                FlurryAgent.c.onSessionStarted();
                                return;
                            }
                            return;
                        default:
                            return;
                    }
                }
            });
        }

        /* renamed from: com.flurry.android.FlurryAgent$1$1 */
        /* loaded from: classes.dex */
        final class RunnableC00051 implements Runnable {
            final /* synthetic */ le a;

            RunnableC00051(le leVar2) {
                r2 = leVar2;
            }

            @Override // java.lang.Runnable
            public final void run() {
                switch (AnonymousClass2.a[r2.c - 1]) {
                    case 1:
                        if (FlurryAgent.c != null) {
                            FlurryAgent.c.onSessionStarted();
                            return;
                        }
                        return;
                    default:
                        return;
                }
            }
        }
    };
    private static FlurryAgentListener c = null;
    private static boolean d = false;
    private static int e = 5;
    private static long f = IME.RETRY_DELAY_IN_MILLIS;
    private static boolean g = true;
    private static boolean h = false;
    private static String i = null;

    /* loaded from: classes.dex */
    public static class Builder {
        public static FlurryAgentListener a = null;
        public boolean b = false;
        public int c = 5;
        public long d = IME.RETRY_DELAY_IN_MILLIS;
        public boolean e = true;
        public boolean f = false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.flurry.android.FlurryAgent$1 */
    /* loaded from: classes.dex */
    public final class AnonymousClass1 implements ka<le> {
        AnonymousClass1() {
        }

        @Override // com.flurry.sdk.ka
        public final /* synthetic */ void a(le leVar2) {
            jr.a().a(new Runnable() { // from class: com.flurry.android.FlurryAgent.1.1
                final /* synthetic */ le a;

                RunnableC00051(le leVar22) {
                    r2 = leVar22;
                }

                @Override // java.lang.Runnable
                public final void run() {
                    switch (AnonymousClass2.a[r2.c - 1]) {
                        case 1:
                            if (FlurryAgent.c != null) {
                                FlurryAgent.c.onSessionStarted();
                                return;
                            }
                            return;
                        default:
                            return;
                    }
                }
            });
        }

        /* renamed from: com.flurry.android.FlurryAgent$1$1 */
        /* loaded from: classes.dex */
        final class RunnableC00051 implements Runnable {
            final /* synthetic */ le a;

            RunnableC00051(le leVar22) {
                r2 = leVar22;
            }

            @Override // java.lang.Runnable
            public final void run() {
                switch (AnonymousClass2.a[r2.c - 1]) {
                    case 1:
                        if (FlurryAgent.c != null) {
                            FlurryAgent.c.onSessionStarted();
                            return;
                        }
                        return;
                    default:
                        return;
                }
            }
        }
    }

    /* renamed from: com.flurry.android.FlurryAgent$2 */
    /* loaded from: classes.dex */
    static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] a = new int[le.a.a().length];

        static {
            try {
                a[le.a.b - 1] = 1;
            } catch (NoSuchFieldError e) {
            }
        }
    }

    private FlurryAgent() {
    }

    @Deprecated
    private static synchronized void init(Context context, String str) {
        synchronized (FlurryAgent.class) {
            if (Build.VERSION.SDK_INT < 10) {
                kf.b(a, "Device SDK Version older than 10");
            } else {
                if (context == null) {
                    throw new NullPointerException("Null context");
                }
                if (TextUtils.isEmpty(str)) {
                    throw new IllegalArgumentException("API key not specified");
                }
                if (jr.a() != null) {
                    kf.e(a, "Flurry is already initialized");
                }
                try {
                    mb.a();
                    jr.a(context, str);
                } catch (Throwable th) {
                    kf.a(a, "", th);
                }
                kf.e(a, "'init' method is deprecated.");
            }
        }
    }

    public static void onStartSession(Context context) {
        if (Build.VERSION.SDK_INT < 10) {
            kf.b(a, "Device SDK Version older than 10");
            return;
        }
        if (context == null) {
            throw new NullPointerException("Null context");
        }
        if (jr.a() == null) {
            throw new IllegalStateException("Flurry SDK must be initialized before starting a session");
        }
        try {
            lf.a().b(context);
        } catch (Throwable th) {
            kf.a(a, "", th);
        }
    }

    public static void onEndSession(Context context) {
        if (Build.VERSION.SDK_INT < 10) {
            kf.b(a, "Device SDK Version older than 10");
            return;
        }
        if (context == null) {
            throw new NullPointerException("Null context");
        }
        if (jr.a() == null) {
            throw new IllegalStateException("Flurry SDK must be initialized before ending a session");
        }
        try {
            lf.a().c(context);
        } catch (Throwable th) {
            kf.a(a, "", th);
        }
    }

    public static FlurryEventRecordStatus logEvent(String str) {
        FlurryEventRecordStatus flurryEventRecordStatus;
        FlurryEventRecordStatus flurryEventRecordStatus2 = FlurryEventRecordStatus.kFlurryEventFailed;
        if (Build.VERSION.SDK_INT < 10) {
            kf.b(a, "Device SDK Version older than 10");
            return flurryEventRecordStatus2;
        }
        if (str == null) {
            kf.b(a, "String eventId passed to logEvent was null.");
            return flurryEventRecordStatus2;
        }
        try {
            hk.a();
            ja c2 = hk.c();
            flurryEventRecordStatus = FlurryEventRecordStatus.kFlurryEventFailed;
            if (c2 != null) {
                flurryEventRecordStatus = c2.a$7a1fda5(str, null);
            }
        } catch (Throwable th) {
            kf.a(a, "Failed to log event: " + str, th);
            flurryEventRecordStatus = flurryEventRecordStatus2;
        }
        return flurryEventRecordStatus;
    }

    public static FlurryEventRecordStatus logEvent(String str, Map<String, String> map) {
        FlurryEventRecordStatus flurryEventRecordStatus;
        FlurryEventRecordStatus flurryEventRecordStatus2 = FlurryEventRecordStatus.kFlurryEventFailed;
        if (Build.VERSION.SDK_INT < 10) {
            kf.b(a, "Device SDK Version older than 10");
            return flurryEventRecordStatus2;
        }
        if (str == null) {
            kf.b(a, "String eventId passed to logEvent was null.");
            return flurryEventRecordStatus2;
        }
        if (map == null) {
            kf.b(a, "String parameters passed to logEvent was null.");
            return flurryEventRecordStatus2;
        }
        try {
            hk.a();
            ja c2 = hk.c();
            flurryEventRecordStatus = FlurryEventRecordStatus.kFlurryEventFailed;
            if (c2 != null) {
                flurryEventRecordStatus = c2.a$7a1fda5(str, map);
            }
        } catch (Throwable th) {
            kf.a(a, "Failed to log event: " + str, th);
            flurryEventRecordStatus = flurryEventRecordStatus2;
        }
        return flurryEventRecordStatus;
    }

    public static void addSessionProperty(String str, String str2) {
        if (Build.VERSION.SDK_INT < 10) {
            kf.b(a, "Device SDK Version older than 10");
            return;
        }
        if (TextUtils.isEmpty(str) || TextUtils.isEmpty(str2)) {
            kf.b(a, "String name or value passed to addSessionProperty was null or empty.");
            return;
        }
        if (jr.a() == null) {
            throw new IllegalStateException("Flurry SDK must be initialized before starting a session");
        }
        jd.a();
        jq i2 = jd.i();
        if (i2 == null) {
            return;
        }
        i2.a(str, str2);
    }

    public static /* synthetic */ void a(FlurryAgentListener flurryAgentListener, boolean z, int i2, long j, boolean z2, boolean z3, Context context, String str) {
        c = flurryAgentListener;
        if (Build.VERSION.SDK_INT < 10) {
            kf.b(a, "Device SDK Version older than 10");
        } else if (flurryAgentListener == null) {
            kf.b(a, "Listener cannot be null");
            kb.a().b("com.flurry.android.sdk.FlurrySessionEvent", b);
        } else {
            c = flurryAgentListener;
            kb.a().a("com.flurry.android.sdk.FlurrySessionEvent", b);
            kf.e(a, "'setFlurryAgentListener' method is deprecated.");
        }
        d = z;
        if (Build.VERSION.SDK_INT < 10) {
            kf.b(a, "Device SDK Version older than 10");
        } else {
            if (z) {
                kf.b();
            } else {
                kf.a();
            }
            kf.e(a, "'setLogEnabled' method is deprecated.");
        }
        e = i2;
        if (Build.VERSION.SDK_INT < 10) {
            kf.b(a, "Device SDK Version older than 10");
        } else {
            kf.a(i2);
            kf.e(a, "'setLogLevel' method is deprecated.");
        }
        f = j;
        if (Build.VERSION.SDK_INT < 10) {
            kf.b(a, "Device SDK Version older than 10");
        } else if (j < 5000) {
            kf.b(a, "Invalid time set for session resumption: " + j);
        } else {
            li.a().a("ContinueSessionMillis", Long.valueOf(j));
            kf.e(a, "'setContinueSessionMillis' method is deprecated.");
        }
        g = z2;
        if (Build.VERSION.SDK_INT < 10) {
            kf.b(a, "Device SDK Version older than 10");
        } else {
            li.a().a("CaptureUncaughtExceptions", Boolean.valueOf(z2));
            kf.e(a, "'setCaptureUncaughtExceptions' method is deprecated.");
        }
        h = z3;
        if (Build.VERSION.SDK_INT < 10) {
            kf.b(a, "Device SDK Version older than 10");
        } else {
            li.a().a("ProtonEnabled", Boolean.valueOf(z3));
            if (!z3) {
                li.a().a("analyticsEnabled", (Object) true);
            }
            kf.e(a, "'setPulseEnabled' method is deprecated.");
        }
        i = str;
        init(context, i);
    }
}
