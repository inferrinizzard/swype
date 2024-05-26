package com.flurry.sdk;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/* loaded from: classes.dex */
public final class kx<ObjectType> extends kw<ObjectType> {
    public kx(kz<ObjectType> kzVar) {
        super(kzVar);
    }

    @Override // com.flurry.sdk.kw, com.flurry.sdk.kz
    public final void a(OutputStream outputStream, ObjectType objecttype) throws IOException {
        GZIPOutputStream gZIPOutputStream;
        if (outputStream != null) {
            try {
                gZIPOutputStream = new GZIPOutputStream(outputStream);
            } catch (Throwable th) {
                th = th;
                gZIPOutputStream = null;
            }
            try {
                super.a(gZIPOutputStream, objecttype);
                lr.a(gZIPOutputStream);
            } catch (Throwable th2) {
                th = th2;
                lr.a(gZIPOutputStream);
                throw th;
            }
        }
    }

    @Override // com.flurry.sdk.kw, com.flurry.sdk.kz
    public final ObjectType a(InputStream inputStream) throws IOException {
        GZIPInputStream gZIPInputStream;
        Throwable th;
        ObjectType objecttype = null;
        if (inputStream != null) {
            try {
                gZIPInputStream = new GZIPInputStream(inputStream);
                try {
                    objecttype = (ObjectType) super.a(gZIPInputStream);
                    lr.a((Closeable) gZIPInputStream);
                } catch (Throwable th2) {
                    th = th2;
                    lr.a((Closeable) gZIPInputStream);
                    throw th;
                }
            } catch (Throwable th3) {
                gZIPInputStream = null;
                th = th3;
            }
        }
        return objecttype;
    }
}
