package com.flurry.sdk;

import com.flurry.sdk.jb;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;

/* loaded from: classes.dex */
public final class hn {
    private static final String a = hn.class.getSimpleName();

    public static jb a(File file) {
        Closeable closeable;
        FileInputStream fileInputStream;
        DataInputStream dataInputStream;
        jb jbVar;
        if (file == null || !file.exists()) {
            return null;
        }
        jb.a aVar = new jb.a();
        try {
            try {
                fileInputStream = new FileInputStream(file);
            } catch (Exception e) {
                e = e;
                dataInputStream = null;
                fileInputStream = null;
            } catch (Throwable th) {
                th = th;
                closeable = null;
                fileInputStream = null;
            }
            try {
                dataInputStream = new DataInputStream(fileInputStream);
            } catch (Exception e2) {
                e = e2;
                dataInputStream = null;
            } catch (Throwable th2) {
                th = th2;
                closeable = null;
                lr.a(closeable);
                lr.a((Closeable) fileInputStream);
                throw th;
            }
            try {
            } catch (Exception e3) {
                e = e3;
                kf.a(3, a, "Error loading legacy agent data.", e);
                lr.a((Closeable) dataInputStream);
                lr.a((Closeable) fileInputStream);
                jbVar = null;
                return jbVar;
            }
            if (dataInputStream.readUnsignedShort() != 46586) {
                kf.a(3, a, "Unexpected file type");
                lr.a((Closeable) dataInputStream);
                lr.a((Closeable) fileInputStream);
                return null;
            }
            int readUnsignedShort = dataInputStream.readUnsignedShort();
            if (readUnsignedShort != 2) {
                kf.a(6, a, "Unknown agent file version: " + readUnsignedShort);
                lr.a((Closeable) dataInputStream);
                lr.a((Closeable) fileInputStream);
                return null;
            }
            jbVar = (jb) aVar.a(dataInputStream);
            lr.a((Closeable) dataInputStream);
            lr.a((Closeable) fileInputStream);
            return jbVar;
        } catch (Throwable th3) {
            th = th3;
        }
    }
}
