package com.flurry.sdk;

import com.flurry.sdk.ii;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/* loaded from: classes.dex */
public final class im {
    private static final String e = in.class.getName();
    public long a;
    int b;
    public String c;
    Map<Long, ii> d;
    private long f = System.currentTimeMillis();
    private long g;
    private iq h;
    private boolean i;
    private int j;
    private AtomicInteger k;

    public im(String str, boolean z, long j, long j2, iq iqVar, Map<Long, ii> map) {
        this.c = str;
        this.i = z;
        this.a = j;
        this.g = j2;
        this.h = iqVar;
        this.d = map;
        if (map != null) {
            Iterator<Long> it = map.keySet().iterator();
            while (it.hasNext()) {
                map.get(it.next()).m = this;
            }
            this.j = map.size();
        } else {
            this.j = 0;
        }
        this.k = new AtomicInteger(0);
    }

    public final List<ii> a() {
        return this.d != null ? new ArrayList(this.d.values()) : Collections.emptyList();
    }

    public final synchronized boolean b() {
        return this.k.intValue() >= this.j;
    }

    public final synchronized void c() {
        this.k.incrementAndGet();
    }

    /* loaded from: classes.dex */
    public static class a implements kz<im> {
        ky<ii> a = new ky<>(new ii.a());

        @Override // com.flurry.sdk.kz
        public final /* synthetic */ void a(OutputStream outputStream, im imVar) throws IOException {
            im imVar2 = imVar;
            if (outputStream == null || imVar2 == null) {
                return;
            }
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream) { // from class: com.flurry.sdk.im.a.1
                @Override // java.io.FilterOutputStream, java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
                public final void close() {
                }
            };
            dataOutputStream.writeLong(imVar2.a);
            dataOutputStream.writeLong(imVar2.f);
            dataOutputStream.writeLong(imVar2.g);
            dataOutputStream.writeInt(imVar2.h.e);
            dataOutputStream.writeBoolean(imVar2.i);
            dataOutputStream.writeInt(imVar2.b);
            if (imVar2.c != null) {
                dataOutputStream.writeUTF(imVar2.c);
            } else {
                dataOutputStream.writeUTF("");
            }
            dataOutputStream.writeInt(imVar2.j);
            dataOutputStream.writeInt(imVar2.k.intValue());
            dataOutputStream.flush();
            this.a.a(outputStream, imVar2.a());
        }

        @Override // com.flurry.sdk.kz
        public final /* synthetic */ im a(InputStream inputStream) throws IOException {
            if (inputStream == null) {
                return null;
            }
            DataInputStream dataInputStream = new DataInputStream(inputStream) { // from class: com.flurry.sdk.im.a.2
                @Override // java.io.FilterInputStream, java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
                public final void close() {
                }
            };
            long readLong = dataInputStream.readLong();
            long readLong2 = dataInputStream.readLong();
            long readLong3 = dataInputStream.readLong();
            iq a = iq.a(dataInputStream.readInt());
            boolean readBoolean = dataInputStream.readBoolean();
            int readInt = dataInputStream.readInt();
            String readUTF = dataInputStream.readUTF();
            int readInt2 = dataInputStream.readInt();
            int readInt3 = dataInputStream.readInt();
            im imVar = new im(readUTF, readBoolean, readLong, readLong3, a, null);
            imVar.f = readLong2;
            imVar.b = readInt;
            imVar.j = readInt2;
            imVar.k = new AtomicInteger(readInt3);
            List<ii> a2 = this.a.a(inputStream);
            if (a2 != null) {
                imVar.d = new HashMap();
                for (ii iiVar : a2) {
                    iiVar.m = imVar;
                    imVar.d.put(Long.valueOf(iiVar.b), iiVar);
                }
            }
            return imVar;
        }
    }

    public final byte[] d() throws IOException {
        DataOutputStream dataOutputStream;
        DataOutputStream dataOutputStream2 = null;
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            dataOutputStream = new DataOutputStream(byteArrayOutputStream);
            try {
                dataOutputStream.writeShort(this.h.e);
                dataOutputStream.writeLong(this.a);
                dataOutputStream.writeLong(this.g);
                dataOutputStream.writeBoolean(this.i);
                if (this.i) {
                    dataOutputStream.writeShort(this.b);
                    dataOutputStream.writeUTF(this.c);
                }
                dataOutputStream.writeShort(this.d.size());
                if (this.d != null) {
                    for (Map.Entry<Long, ii> entry : this.d.entrySet()) {
                        ii value = entry.getValue();
                        dataOutputStream.writeLong(entry.getKey().longValue());
                        dataOutputStream.writeUTF(value.r);
                        dataOutputStream.writeShort(value.a.size());
                        Iterator<ij> it = value.a.iterator();
                        while (it.hasNext()) {
                            ij next = it.next();
                            dataOutputStream.writeShort(next.a);
                            dataOutputStream.writeLong(next.b);
                            dataOutputStream.writeLong(next.c);
                            dataOutputStream.writeBoolean(next.d);
                            dataOutputStream.writeShort(next.e);
                            dataOutputStream.writeShort(next.f.e);
                            if ((next.e < 200 || next.e >= 400) && next.g != null) {
                                byte[] bytes = next.g.getBytes();
                                dataOutputStream.writeShort(bytes.length);
                                dataOutputStream.write(bytes);
                            }
                            dataOutputStream.writeShort(next.h);
                            dataOutputStream.writeInt((int) next.k);
                        }
                    }
                }
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                lr.a(dataOutputStream);
                return byteArray;
            } catch (IOException e2) {
                e = e2;
                dataOutputStream2 = dataOutputStream;
                try {
                    kf.a(6, e, "Error when generating report", e);
                    throw e;
                } catch (Throwable th) {
                    th = th;
                    dataOutputStream = dataOutputStream2;
                    lr.a(dataOutputStream);
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
                lr.a(dataOutputStream);
                throw th;
            }
        } catch (IOException e3) {
            e = e3;
        } catch (Throwable th3) {
            th = th3;
            dataOutputStream = null;
        }
    }
}
