package com.flurry.sdk;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public final class ky<T> implements kz<List<T>> {
    kz<T> a;

    public ky(kz<T> kzVar) {
        this.a = kzVar;
    }

    @Override // com.flurry.sdk.kz
    public final void a(OutputStream outputStream, List<T> list) throws IOException {
        if (outputStream != null) {
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream) { // from class: com.flurry.sdk.ky.1
                @Override // java.io.FilterOutputStream, java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
                public final void close() {
                }
            };
            int size = list != null ? list.size() : 0;
            dataOutputStream.writeInt(size);
            for (int i = 0; i < size; i++) {
                this.a.a(outputStream, list.get(i));
            }
            dataOutputStream.flush();
        }
    }

    @Override // com.flurry.sdk.kz
    /* renamed from: b, reason: merged with bridge method [inline-methods] */
    public final List<T> a(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            return null;
        }
        int readInt = new DataInputStream(inputStream) { // from class: com.flurry.sdk.ky.2
            @Override // java.io.FilterInputStream, java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
            public final void close() {
            }
        }.readInt();
        ArrayList arrayList = new ArrayList(readInt);
        for (int i = 0; i < readInt; i++) {
            T a = this.a.a(inputStream);
            if (a == null) {
                throw new IOException("Missing record.");
            }
            arrayList.add(a);
        }
        return arrayList;
    }
}
