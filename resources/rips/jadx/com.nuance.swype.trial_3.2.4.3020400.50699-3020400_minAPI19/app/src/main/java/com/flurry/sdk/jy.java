package com.flurry.sdk;

import java.io.File;

/* loaded from: classes.dex */
public class jy<T> {
    private static final String a = jy.class.getSimpleName();
    private final File b;
    private final kz<T> c;

    public jy(File file, String str, int i, lc<T> lcVar) {
        this.b = file;
        this.c = new kx(new lb(str, i, lcVar));
    }

    /* JADX WARN: Removed duplicated region for block: B:15:0x003f  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final T a() {
        /*
            r8 = this;
            r7 = 3
            r0 = 0
            java.io.File r1 = r8.b
            if (r1 != 0) goto L7
        L6:
            return r0
        L7:
            java.io.File r1 = r8.b
            boolean r1 = r1.exists()
            if (r1 != 0) goto L2c
            r1 = 5
            java.lang.String r2 = com.flurry.sdk.jy.a
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            java.lang.String r4 = "No data to read for file:"
            r3.<init>(r4)
            java.io.File r4 = r8.b
            java.lang.String r4 = r4.getName()
            java.lang.StringBuilder r3 = r3.append(r4)
            java.lang.String r3 = r3.toString()
            com.flurry.sdk.kf.a(r1, r2, r3)
            goto L6
        L2c:
            r1 = 0
            java.io.FileInputStream r2 = new java.io.FileInputStream     // Catch: java.lang.Exception -> L60 java.lang.Throwable -> L83
            java.io.File r3 = r8.b     // Catch: java.lang.Exception -> L60 java.lang.Throwable -> L83
            r2.<init>(r3)     // Catch: java.lang.Exception -> L60 java.lang.Throwable -> L83
            com.flurry.sdk.kz<T> r3 = r8.c     // Catch: java.lang.Throwable -> L8a java.lang.Exception -> L8c
            java.lang.Object r0 = r3.a(r2)     // Catch: java.lang.Throwable -> L8a java.lang.Exception -> L8c
            com.flurry.sdk.lr.a(r2)
        L3d:
            if (r1 == 0) goto L6
            java.lang.String r1 = com.flurry.sdk.jy.a
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            java.lang.String r3 = "Deleting data file:"
            r2.<init>(r3)
            java.io.File r3 = r8.b
            java.lang.String r3 = r3.getName()
            java.lang.StringBuilder r2 = r2.append(r3)
            java.lang.String r2 = r2.toString()
            com.flurry.sdk.kf.a(r7, r1, r2)
            java.io.File r1 = r8.b
            r1.delete()
            goto L6
        L60:
            r1 = move-exception
            r2 = r0
        L62:
            r3 = 3
            java.lang.String r4 = com.flurry.sdk.jy.a     // Catch: java.lang.Throwable -> L8a
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L8a
            java.lang.String r6 = "Error reading data file:"
            r5.<init>(r6)     // Catch: java.lang.Throwable -> L8a
            java.io.File r6 = r8.b     // Catch: java.lang.Throwable -> L8a
            java.lang.String r6 = r6.getName()     // Catch: java.lang.Throwable -> L8a
            java.lang.StringBuilder r5 = r5.append(r6)     // Catch: java.lang.Throwable -> L8a
            java.lang.String r5 = r5.toString()     // Catch: java.lang.Throwable -> L8a
            com.flurry.sdk.kf.a(r3, r4, r5, r1)     // Catch: java.lang.Throwable -> L8a
            r1 = 1
            com.flurry.sdk.lr.a(r2)
            goto L3d
        L83:
            r1 = move-exception
            r2 = r0
            r0 = r1
        L86:
            com.flurry.sdk.lr.a(r2)
            throw r0
        L8a:
            r0 = move-exception
            goto L86
        L8c:
            r1 = move-exception
            goto L62
        */
        throw new UnsupportedOperationException("Method not decompiled: com.flurry.sdk.jy.a():java.lang.Object");
    }

    /* JADX WARN: Removed duplicated region for block: B:6:0x0024  */
    /* JADX WARN: Removed duplicated region for block: B:9:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final void a(T r9) {
        /*
            r8 = this;
            r3 = 1
            r7 = 3
            r0 = 0
            r1 = 0
            if (r9 != 0) goto L45
            java.lang.String r0 = com.flurry.sdk.jy.a
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            java.lang.String r2 = "No data to write for file:"
            r1.<init>(r2)
            java.io.File r2 = r8.b
            java.lang.String r2 = r2.getName()
            java.lang.StringBuilder r1 = r1.append(r2)
            java.lang.String r1 = r1.toString()
            com.flurry.sdk.kf.a(r7, r0, r1)
            r0 = r3
        L22:
            if (r0 == 0) goto L44
            java.lang.String r0 = com.flurry.sdk.jy.a
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            java.lang.String r2 = "Deleting data file:"
            r1.<init>(r2)
            java.io.File r2 = r8.b
            java.lang.String r2 = r2.getName()
            java.lang.StringBuilder r1 = r1.append(r2)
            java.lang.String r1 = r1.toString()
            com.flurry.sdk.kf.a(r7, r0, r1)
            java.io.File r0 = r8.b
            r0.delete()
        L44:
            return
        L45:
            java.io.File r2 = r8.b     // Catch: java.lang.Exception -> L56 java.lang.Throwable -> L88
            boolean r2 = com.flurry.sdk.lq.a(r2)     // Catch: java.lang.Exception -> L56 java.lang.Throwable -> L88
            if (r2 != 0) goto L78
            java.io.IOException r0 = new java.io.IOException     // Catch: java.lang.Exception -> L56 java.lang.Throwable -> L88
            java.lang.String r2 = "Cannot create parent directory!"
            r0.<init>(r2)     // Catch: java.lang.Exception -> L56 java.lang.Throwable -> L88
            throw r0     // Catch: java.lang.Exception -> L56 java.lang.Throwable -> L88
        L56:
            r0 = move-exception
        L57:
            r2 = 3
            java.lang.String r4 = com.flurry.sdk.jy.a     // Catch: java.lang.Throwable -> L88
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L88
            java.lang.String r6 = "Error writing data file:"
            r5.<init>(r6)     // Catch: java.lang.Throwable -> L88
            java.io.File r6 = r8.b     // Catch: java.lang.Throwable -> L88
            java.lang.String r6 = r6.getName()     // Catch: java.lang.Throwable -> L88
            java.lang.StringBuilder r5 = r5.append(r6)     // Catch: java.lang.Throwable -> L88
            java.lang.String r5 = r5.toString()     // Catch: java.lang.Throwable -> L88
            com.flurry.sdk.kf.a(r2, r4, r5, r0)     // Catch: java.lang.Throwable -> L88
            com.flurry.sdk.lr.a(r1)
            r0 = r3
            goto L22
        L78:
            java.io.FileOutputStream r2 = new java.io.FileOutputStream     // Catch: java.lang.Exception -> L56 java.lang.Throwable -> L88
            java.io.File r4 = r8.b     // Catch: java.lang.Exception -> L56 java.lang.Throwable -> L88
            r2.<init>(r4)     // Catch: java.lang.Exception -> L56 java.lang.Throwable -> L88
            com.flurry.sdk.kz<T> r1 = r8.c     // Catch: java.lang.Throwable -> L8d java.lang.Exception -> L90
            r1.a(r2, r9)     // Catch: java.lang.Throwable -> L8d java.lang.Exception -> L90
            com.flurry.sdk.lr.a(r2)
            goto L22
        L88:
            r0 = move-exception
        L89:
            com.flurry.sdk.lr.a(r1)
            throw r0
        L8d:
            r0 = move-exception
            r1 = r2
            goto L89
        L90:
            r0 = move-exception
            r1 = r2
            goto L57
        */
        throw new UnsupportedOperationException("Method not decompiled: com.flurry.sdk.jy.a(java.lang.Object):void");
    }

    public final boolean b() {
        if (this.b == null) {
            return false;
        }
        return this.b.delete();
    }
}
