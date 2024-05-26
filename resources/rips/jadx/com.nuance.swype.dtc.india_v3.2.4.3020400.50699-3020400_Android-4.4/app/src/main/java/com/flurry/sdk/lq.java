package com.flurry.sdk;

import java.io.File;
import java.io.FileOutputStream;

/* loaded from: classes.dex */
public final class lq {
    private static String a = lq.class.getSimpleName();

    public static boolean a(File file) {
        if (file == null || file.getAbsoluteFile() == null) {
            return false;
        }
        File parentFile = file.getParentFile();
        if (parentFile == null) {
            return true;
        }
        if (parentFile.mkdirs() || parentFile.isDirectory()) {
            return true;
        }
        kf.a(6, a, "Unable to create persistent dir: " + parentFile);
        return false;
    }

    /* JADX WARN: Removed duplicated region for block: B:16:0x005a  */
    /* JADX WARN: Removed duplicated region for block: B:19:? A[RETURN, SYNTHETIC] */
    @java.lang.Deprecated
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static java.lang.String c(java.io.File r7) {
        /*
            r4 = 4
            r0 = 0
            if (r7 == 0) goto La
            boolean r1 = r7.exists()
            if (r1 != 0) goto L13
        La:
            java.lang.String r1 = com.flurry.sdk.lq.a
            java.lang.String r2 = "Persistent file doesn't exist."
            com.flurry.sdk.kf.a(r4, r1, r2)
        L12:
            return r0
        L13:
            java.lang.String r1 = com.flurry.sdk.lq.a
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            java.lang.String r3 = "Loading persistent data: "
            r2.<init>(r3)
            java.lang.String r3 = r7.getAbsolutePath()
            java.lang.StringBuilder r2 = r2.append(r3)
            java.lang.String r2 = r2.toString()
            com.flurry.sdk.kf.a(r4, r1, r2)
            java.io.FileInputStream r2 = new java.io.FileInputStream     // Catch: java.lang.Throwable -> L63 java.lang.Throwable -> L6c
            r2.<init>(r7)     // Catch: java.lang.Throwable -> L63 java.lang.Throwable -> L6c
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L4a java.lang.Throwable -> L6a
            r1.<init>()     // Catch: java.lang.Throwable -> L4a java.lang.Throwable -> L6a
            r3 = 1024(0x400, float:1.435E-42)
            byte[] r3 = new byte[r3]     // Catch: java.lang.Throwable -> L4a java.lang.Throwable -> L6a
        L3a:
            int r4 = r2.read(r3)     // Catch: java.lang.Throwable -> L4a java.lang.Throwable -> L6a
            if (r4 <= 0) goto L5f
            java.lang.String r5 = new java.lang.String     // Catch: java.lang.Throwable -> L4a java.lang.Throwable -> L6a
            r6 = 0
            r5.<init>(r3, r6, r4)     // Catch: java.lang.Throwable -> L4a java.lang.Throwable -> L6a
            r1.append(r5)     // Catch: java.lang.Throwable -> L4a java.lang.Throwable -> L6a
            goto L3a
        L4a:
            r1 = move-exception
        L4b:
            r3 = 6
            java.lang.String r4 = com.flurry.sdk.lq.a     // Catch: java.lang.Throwable -> L6a
            java.lang.String r5 = "Error when loading persistent file"
            com.flurry.sdk.kf.a(r3, r4, r5, r1)     // Catch: java.lang.Throwable -> L6a
            com.flurry.sdk.lr.a(r2)
            r1 = r0
        L58:
            if (r1 == 0) goto L12
            java.lang.String r0 = r1.toString()
            goto L12
        L5f:
            com.flurry.sdk.lr.a(r2)
            goto L58
        L63:
            r1 = move-exception
            r2 = r0
            r0 = r1
        L66:
            com.flurry.sdk.lr.a(r2)
            throw r0
        L6a:
            r0 = move-exception
            goto L66
        L6c:
            r1 = move-exception
            r2 = r0
            goto L4b
        */
        throw new UnsupportedOperationException("Method not decompiled: com.flurry.sdk.lq.c(java.io.File):java.lang.String");
    }

    @Deprecated
    public static void a(File file, String str) {
        FileOutputStream fileOutputStream;
        if (file == null) {
            kf.a(4, a, "No persistent file specified.");
            return;
        }
        if (str == null) {
            kf.a(4, a, "No data specified; deleting persistent file: " + file.getAbsolutePath());
            file.delete();
            return;
        }
        kf.a(4, a, "Writing persistent data: " + file.getAbsolutePath());
        try {
            fileOutputStream = new FileOutputStream(file);
        } catch (Throwable th) {
            th = th;
            fileOutputStream = null;
            lr.a(fileOutputStream);
            throw th;
        }
        try {
            try {
                fileOutputStream.write(str.getBytes());
                lr.a(fileOutputStream);
            } catch (Throwable th2) {
                th = th2;
                lr.a(fileOutputStream);
                throw th;
            }
        } catch (Throwable th3) {
            th = th3;
            kf.a(6, a, "Error writing persistent file", th);
            lr.a(fileOutputStream);
        }
    }
}
