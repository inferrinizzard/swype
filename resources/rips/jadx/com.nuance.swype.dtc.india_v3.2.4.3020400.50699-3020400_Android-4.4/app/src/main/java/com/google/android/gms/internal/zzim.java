package com.google.android.gms.internal;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import com.google.android.gms.ads.internal.util.client.VersionInfoParcel;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread;
import java.util.ArrayList;
import java.util.LinkedList;

@zzin
/* loaded from: classes.dex */
public final class zzim implements Thread.UncaughtExceptionHandler {
    private Context mContext;
    private VersionInfoParcel zzamw;
    private Thread.UncaughtExceptionHandler zzcac;
    private Thread.UncaughtExceptionHandler zzcad;

    public zzim(Context context, VersionInfoParcel versionInfoParcel, Thread.UncaughtExceptionHandler uncaughtExceptionHandler, Thread.UncaughtExceptionHandler uncaughtExceptionHandler2) {
        this.zzcac = uncaughtExceptionHandler;
        this.zzcad = uncaughtExceptionHandler2;
        this.mContext = context;
        this.zzamw = versionInfoParcel;
    }

    public static zzim zza(Context context, Thread thread, VersionInfoParcel versionInfoParcel) {
        if (context == null || thread == null || versionInfoParcel == null) {
            return null;
        }
        if (!zzu$faab209()) {
            return null;
        }
        Thread.UncaughtExceptionHandler uncaughtExceptionHandler = thread.getUncaughtExceptionHandler();
        zzim zzimVar = new zzim(context, versionInfoParcel, uncaughtExceptionHandler, Thread.getDefaultUncaughtExceptionHandler());
        if (uncaughtExceptionHandler != null && (uncaughtExceptionHandler instanceof zzim)) {
            return (zzim) uncaughtExceptionHandler;
        }
        try {
            thread.setUncaughtExceptionHandler(zzimVar);
            return zzimVar;
        } catch (SecurityException e) {
            zzkd.zzc("Fail to set UncaughtExceptionHandler.", e);
            return null;
        }
    }

    private static boolean zzu$faab209() {
        return ((Boolean) com.google.android.gms.ads.internal.zzu.zzfz().zzd(zzdc.zzayd)).booleanValue();
    }

    /* JADX WARN: Removed duplicated region for block: B:23:0x0041  */
    /* JADX WARN: Removed duplicated region for block: B:31:0x0058  */
    /* JADX WARN: Removed duplicated region for block: B:33:0x005e  */
    @Override // java.lang.Thread.UncaughtExceptionHandler
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final void uncaughtException(java.lang.Thread r11, java.lang.Throwable r12) {
        /*
            r10 = this;
            r1 = 1
            r3 = 0
            if (r12 == 0) goto L4f
            r2 = r3
            r0 = r3
            r5 = r12
        L7:
            if (r5 == 0) goto L3a
            java.lang.StackTraceElement[] r6 = r5.getStackTrace()
            int r7 = r6.length
            r4 = r3
        Lf:
            if (r4 >= r7) goto L34
            r8 = r6[r4]
            java.lang.String r9 = r8.getClassName()
            boolean r9 = zzcc(r9)
            if (r9 == 0) goto L1e
            r0 = r1
        L1e:
            java.lang.Class r9 = r10.getClass()
            java.lang.String r9 = r9.getName()
            java.lang.String r8 = r8.getClassName()
            boolean r8 = r9.equals(r8)
            if (r8 == 0) goto L31
            r2 = r1
        L31:
            int r4 = r4 + 1
            goto Lf
        L34:
            java.lang.Throwable r4 = r5.getCause()
            r5 = r4
            goto L7
        L3a:
            if (r0 == 0) goto L4f
            if (r2 != 0) goto L4f
            r0 = r1
        L3f:
            if (r0 == 0) goto L54
            android.os.Looper r0 = android.os.Looper.getMainLooper()
            java.lang.Thread r0 = r0.getThread()
            if (r0 == r11) goto L51
            r10.zza(r12, r1)
        L4e:
            return
        L4f:
            r0 = r3
            goto L3f
        L51:
            r10.zza(r12, r3)
        L54:
            java.lang.Thread$UncaughtExceptionHandler r0 = r10.zzcac
            if (r0 == 0) goto L5e
            java.lang.Thread$UncaughtExceptionHandler r0 = r10.zzcac
            r0.uncaughtException(r11, r12)
            goto L4e
        L5e:
            java.lang.Thread$UncaughtExceptionHandler r0 = r10.zzcad
            if (r0 == 0) goto L4e
            java.lang.Thread$UncaughtExceptionHandler r0 = r10.zzcad
            r0.uncaughtException(r11, r12)
            goto L4e
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.zzim.uncaughtException(java.lang.Thread, java.lang.Throwable):void");
    }

    private static boolean zzcc(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        if (str.startsWith((String) com.google.android.gms.ads.internal.zzu.zzfz().zzd(zzdc.zzayf))) {
            return true;
        }
        try {
            return Class.forName(str).isAnnotationPresent(zzin.class);
        } catch (Exception e) {
            String valueOf = String.valueOf(str);
            zzkd.zza(valueOf.length() != 0 ? "Fail to check class type for class ".concat(valueOf) : new String("Fail to check class type for class "), e);
            return false;
        }
    }

    public final void zza(Throwable th, boolean z) {
        Throwable th2;
        Throwable th3;
        if (zzu$faab209()) {
            if (((Boolean) com.google.android.gms.ads.internal.zzu.zzfz().zzd(zzdc.zzaye)).booleanValue()) {
                th2 = th;
            } else {
                LinkedList linkedList = new LinkedList();
                for (Throwable th4 = th; th4 != null; th4 = th4.getCause()) {
                    linkedList.push(th4);
                }
                th2 = null;
                while (!linkedList.isEmpty()) {
                    Throwable th5 = (Throwable) linkedList.pop();
                    StackTraceElement[] stackTrace = th5.getStackTrace();
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(new StackTraceElement(th5.getClass().getName(), "<filtered>", "<filtered>", 1));
                    boolean z2 = false;
                    for (StackTraceElement stackTraceElement : stackTrace) {
                        if (zzcc(stackTraceElement.getClassName())) {
                            z2 = true;
                            arrayList.add(stackTraceElement);
                        } else {
                            String className = stackTraceElement.getClassName();
                            if (!TextUtils.isEmpty(className) && (className.startsWith("android.") || className.startsWith("java."))) {
                                arrayList.add(stackTraceElement);
                            } else {
                                arrayList.add(new StackTraceElement("<filtered>", "<filtered>", "<filtered>", 1));
                            }
                        }
                    }
                    if (z2) {
                        th3 = th2 == null ? new Throwable(th5.getMessage()) : new Throwable(th5.getMessage(), th2);
                        th3.setStackTrace((StackTraceElement[]) arrayList.toArray(new StackTraceElement[0]));
                    } else {
                        th3 = th2;
                    }
                    th2 = th3;
                }
            }
            if (th2 != null) {
                Class<?> cls = th.getClass();
                ArrayList arrayList2 = new ArrayList();
                StringWriter stringWriter = new StringWriter();
                th2.printStackTrace(new PrintWriter(stringWriter));
                Uri.Builder appendQueryParameter = new Uri.Builder().scheme("https").path("//pagead2.googlesyndication.com/pagead/gen_204").appendQueryParameter("id", "gmob-apps-report-exception").appendQueryParameter("os", Build.VERSION.RELEASE).appendQueryParameter("api", String.valueOf(Build.VERSION.SDK_INT));
                com.google.android.gms.ads.internal.zzu.zzfq();
                arrayList2.add(appendQueryParameter.appendQueryParameter("device", zzkh.zztg()).appendQueryParameter("js", this.zzamw.zzcs).appendQueryParameter("appid", this.mContext.getApplicationContext().getPackageName()).appendQueryParameter("exceptiontype", cls.getName()).appendQueryParameter("stacktrace", stringWriter.toString()).appendQueryParameter("eids", TextUtils.join(",", zzdc.zzjx())).appendQueryParameter("trapped", String.valueOf(z)).toString());
                com.google.android.gms.ads.internal.zzu.zzfq();
                zzkh.zza(arrayList2, com.google.android.gms.ads.internal.zzu.zzft().zzso());
            }
        }
    }
}
