package com.flurry.sdk;

import com.flurry.sdk.ij;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public class ii extends kp {
    private static final String t = ii.class.getName();
    public ArrayList<ij> a;
    final long b;
    final int c;
    final int d;
    final ip e;
    final Map<String, String> f;
    long g;
    int h;
    int i;
    String j;
    String k;
    boolean l;
    public im m;
    private final int u = 1000;
    private final int v = 30000;

    public ii(String str, long j, String str2, long j2, int i, int i2, ip ipVar, Map<String, String> map, int i3, int i4, String str3) {
        this.q = str2;
        this.r = str2;
        this.n = j2;
        a_();
        this.k = str;
        this.b = j;
        this.s = i;
        this.c = i;
        this.d = i2;
        this.e = ipVar;
        this.f = map;
        this.h = i3;
        this.i = i4;
        this.j = str3;
        this.g = 30000L;
        this.a = new ArrayList<>();
    }

    @Override // com.flurry.sdk.kp
    public final void a_() {
        super.a_();
        if (this.p != 1) {
            this.g *= 3;
        }
    }

    public final synchronized void c() {
        this.m.c();
    }

    public final void d() {
        Iterator<ij> it = this.a.iterator();
        while (it.hasNext()) {
            it.next().l = this;
        }
    }

    /* loaded from: classes.dex */
    public static class a implements kz<ii> {
        ky<ij> a = new ky<>(new ij.a());

        @Override // com.flurry.sdk.kz
        public final /* synthetic */ void a(OutputStream outputStream, ii iiVar) throws IOException {
            ii iiVar2 = iiVar;
            if (outputStream == null || iiVar2 == null) {
                return;
            }
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream) { // from class: com.flurry.sdk.ii.a.1
                @Override // java.io.FilterOutputStream, java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
                public final void close() {
                }
            };
            if (iiVar2.k != null) {
                dataOutputStream.writeUTF(iiVar2.k);
            } else {
                dataOutputStream.writeUTF("");
            }
            if (iiVar2.r != null) {
                dataOutputStream.writeUTF(iiVar2.r);
            } else {
                dataOutputStream.writeUTF("");
            }
            dataOutputStream.writeLong(iiVar2.n);
            dataOutputStream.writeInt(iiVar2.p);
            dataOutputStream.writeLong(iiVar2.b);
            dataOutputStream.writeInt(iiVar2.c);
            dataOutputStream.writeInt(iiVar2.d);
            dataOutputStream.writeInt(iiVar2.e.e);
            Map map = iiVar2.f;
            if (map != null) {
                dataOutputStream.writeInt(iiVar2.f.size());
                for (String str : iiVar2.f.keySet()) {
                    dataOutputStream.writeUTF(str);
                    dataOutputStream.writeUTF((String) map.get(str));
                }
            } else {
                dataOutputStream.writeInt(0);
            }
            dataOutputStream.writeLong(iiVar2.g);
            dataOutputStream.writeInt(iiVar2.h);
            dataOutputStream.writeInt(iiVar2.i);
            if (iiVar2.j != null) {
                dataOutputStream.writeUTF(iiVar2.j);
            } else {
                dataOutputStream.writeUTF("");
            }
            dataOutputStream.writeBoolean(iiVar2.l);
            dataOutputStream.flush();
            this.a.a(outputStream, (List<ij>) iiVar2.a);
        }

        @Override // com.flurry.sdk.kz
        public final /* synthetic */ ii a(InputStream inputStream) throws IOException {
            if (inputStream == null) {
                return null;
            }
            DataInputStream dataInputStream = new DataInputStream(inputStream) { // from class: com.flurry.sdk.ii.a.2
                @Override // java.io.FilterInputStream, java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
                public final void close() {
                }
            };
            String readUTF = dataInputStream.readUTF();
            if (readUTF.equals("")) {
                readUTF = null;
            }
            String readUTF2 = dataInputStream.readUTF();
            long readLong = dataInputStream.readLong();
            int readInt = dataInputStream.readInt();
            long readLong2 = dataInputStream.readLong();
            int readInt2 = dataInputStream.readInt();
            int readInt3 = dataInputStream.readInt();
            ip a = ip.a(dataInputStream.readInt());
            HashMap hashMap = null;
            int readInt4 = dataInputStream.readInt();
            if (readInt4 != 0) {
                hashMap = new HashMap();
                for (int i = 0; i < readInt4; i++) {
                    hashMap.put(dataInputStream.readUTF(), dataInputStream.readUTF());
                }
            }
            long readLong3 = dataInputStream.readLong();
            int readInt5 = dataInputStream.readInt();
            int readInt6 = dataInputStream.readInt();
            String readUTF3 = dataInputStream.readUTF();
            if (readUTF3.equals("")) {
                readUTF3 = null;
            }
            boolean readBoolean = dataInputStream.readBoolean();
            ii iiVar = new ii(readUTF, readLong2, readUTF2, readLong, readInt2, readInt3, a, hashMap, readInt5, readInt6, readUTF3);
            iiVar.g = readLong3;
            iiVar.l = readBoolean;
            iiVar.p = readInt;
            iiVar.a = (ArrayList) this.a.a(inputStream);
            iiVar.d();
            return iiVar;
        }
    }
}
