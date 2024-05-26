package com.flurry.sdk;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/* loaded from: classes.dex */
public class ij {
    private static final String m = ij.class.getName();
    public int a;
    public long b;
    public long c;
    public boolean d;
    public String g;
    public int h;
    public long i;
    public boolean j;
    public ii l;
    public long k = 0;
    public int e = 0;
    public ik f = ik.PENDING_COMPLETION;

    public ij(ii iiVar, long j, long j2, int i) {
        this.l = iiVar;
        this.b = j;
        this.c = j2;
        this.a = i;
    }

    public final void a() {
        this.l.a.add(this);
        if (!this.d) {
            return;
        }
        this.l.l = true;
    }

    /* loaded from: classes.dex */
    public static class a implements kz<ij> {
        @Override // com.flurry.sdk.kz
        public final /* synthetic */ void a(OutputStream outputStream, ij ijVar) throws IOException {
            ij ijVar2 = ijVar;
            if (outputStream == null || ijVar2 == null) {
                return;
            }
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream) { // from class: com.flurry.sdk.ij.a.1
                @Override // java.io.FilterOutputStream, java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
                public final void close() {
                }
            };
            dataOutputStream.writeInt(ijVar2.a);
            dataOutputStream.writeLong(ijVar2.b);
            dataOutputStream.writeLong(ijVar2.c);
            dataOutputStream.writeBoolean(ijVar2.d);
            dataOutputStream.writeInt(ijVar2.e);
            dataOutputStream.writeInt(ijVar2.f.e);
            if (ijVar2.g != null) {
                dataOutputStream.writeUTF(ijVar2.g);
            } else {
                dataOutputStream.writeUTF("");
            }
            dataOutputStream.writeInt(ijVar2.h);
            dataOutputStream.writeLong(ijVar2.i);
            dataOutputStream.writeBoolean(ijVar2.j);
            dataOutputStream.writeLong(ijVar2.k);
            dataOutputStream.flush();
        }

        @Override // com.flurry.sdk.kz
        public final /* synthetic */ ij a(InputStream inputStream) throws IOException {
            if (inputStream == null) {
                return null;
            }
            DataInputStream dataInputStream = new DataInputStream(inputStream) { // from class: com.flurry.sdk.ij.a.2
                @Override // java.io.FilterInputStream, java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
                public final void close() {
                }
            };
            int readInt = dataInputStream.readInt();
            long readLong = dataInputStream.readLong();
            long readLong2 = dataInputStream.readLong();
            boolean readBoolean = dataInputStream.readBoolean();
            int readInt2 = dataInputStream.readInt();
            ik a = ik.a(dataInputStream.readInt());
            String readUTF = dataInputStream.readUTF();
            int readInt3 = dataInputStream.readInt();
            long readLong3 = dataInputStream.readLong();
            boolean readBoolean2 = dataInputStream.readBoolean();
            long readLong4 = dataInputStream.readLong();
            ij ijVar = new ij(null, readLong, readLong2, readInt);
            ijVar.d = readBoolean;
            ijVar.e = readInt2;
            ijVar.f = a;
            ijVar.g = readUTF;
            ijVar.h = readInt3;
            ijVar.i = readLong3;
            ijVar.j = readBoolean2;
            ijVar.k = readLong4;
            return ijVar;
        }
    }
}
