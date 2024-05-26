package com.flurry.sdk;

import android.text.TextUtils;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public class iy {
    private static final String b = iy.class.getSimpleName();
    byte[] a;

    /* synthetic */ iy(byte b2) {
        this();
    }

    /* loaded from: classes.dex */
    public static class a implements kz<iy> {
        @Override // com.flurry.sdk.kz
        public final /* synthetic */ void a(OutputStream outputStream, iy iyVar) throws IOException {
            iy iyVar2 = iyVar;
            if (outputStream == null || iyVar2 == null) {
                return;
            }
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream) { // from class: com.flurry.sdk.iy.a.1
                @Override // java.io.FilterOutputStream, java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
                public final void close() {
                }
            };
            int i = 0;
            if (iyVar2.a != null) {
                i = iyVar2.a.length;
            }
            dataOutputStream.writeShort(i);
            if (i > 0) {
                dataOutputStream.write(iyVar2.a);
            }
            dataOutputStream.flush();
        }

        @Override // com.flurry.sdk.kz
        public final /* synthetic */ iy a(InputStream inputStream) throws IOException {
            if (inputStream == null) {
                return null;
            }
            DataInputStream dataInputStream = new DataInputStream(inputStream) { // from class: com.flurry.sdk.iy.a.2
                @Override // java.io.FilterInputStream, java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
                public final void close() {
                }
            };
            iy iyVar = new iy((byte) 0);
            int readUnsignedShort = dataInputStream.readUnsignedShort();
            if (readUnsignedShort <= 0) {
                return iyVar;
            }
            byte[] bArr = new byte[readUnsignedShort];
            dataInputStream.readFully(bArr);
            iyVar.a = bArr;
            return iyVar;
        }
    }

    private iy() {
    }

    public iy(byte[] bArr) {
        this.a = bArr;
    }

    public iy(iz izVar) throws IOException {
        DataOutputStream dataOutputStream;
        int i;
        DataOutputStream dataOutputStream2 = null;
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            dataOutputStream = new DataOutputStream(byteArrayOutputStream);
            try {
                dataOutputStream.writeShort(7);
                dataOutputStream.writeUTF(izVar.a);
                dataOutputStream.writeLong(izVar.b);
                dataOutputStream.writeLong(izVar.c);
                dataOutputStream.writeLong(izVar.d);
                dataOutputStream.writeBoolean(true);
                dataOutputStream.writeByte(-1);
                if (TextUtils.isEmpty(izVar.f)) {
                    dataOutputStream.writeBoolean(false);
                } else {
                    dataOutputStream.writeBoolean(true);
                    dataOutputStream.writeUTF(izVar.f);
                }
                if (TextUtils.isEmpty(izVar.g)) {
                    dataOutputStream.writeBoolean(false);
                } else {
                    dataOutputStream.writeBoolean(true);
                    dataOutputStream.writeUTF(izVar.g);
                }
                Map<String, String> map = izVar.h;
                if (map == null) {
                    dataOutputStream.writeShort(0);
                } else {
                    dataOutputStream.writeShort(map.size());
                    for (Map.Entry<String, String> entry : map.entrySet()) {
                        dataOutputStream.writeUTF(entry.getKey());
                        dataOutputStream.writeUTF(entry.getValue());
                    }
                }
                Map<String, String> map2 = izVar.e;
                if (map2 == null) {
                    dataOutputStream.writeShort(0);
                } else {
                    dataOutputStream.writeShort(map2.size());
                    for (Map.Entry<String, String> entry2 : map2.entrySet()) {
                        dataOutputStream.writeUTF(entry2.getKey());
                        dataOutputStream.writeUTF(entry2.getValue());
                        dataOutputStream.writeByte(0);
                    }
                }
                dataOutputStream.writeUTF(izVar.i);
                dataOutputStream.writeUTF(izVar.j);
                dataOutputStream.writeByte(izVar.k);
                dataOutputStream.writeByte(izVar.l);
                dataOutputStream.writeUTF(izVar.m);
                if (izVar.n == null) {
                    dataOutputStream.writeBoolean(false);
                } else {
                    dataOutputStream.writeBoolean(true);
                    dataOutputStream.writeDouble(lr.a(izVar.n.getLatitude()));
                    dataOutputStream.writeDouble(lr.a(izVar.n.getLongitude()));
                    dataOutputStream.writeFloat(izVar.n.getAccuracy());
                }
                dataOutputStream.writeInt(izVar.o);
                dataOutputStream.writeByte(-1);
                dataOutputStream.writeByte(-1);
                dataOutputStream.writeByte(izVar.p);
                if (izVar.q == null) {
                    dataOutputStream.writeBoolean(false);
                } else {
                    dataOutputStream.writeBoolean(true);
                    dataOutputStream.writeLong(izVar.q.longValue());
                }
                Map<String, iu> map3 = izVar.r;
                if (map3 == null) {
                    dataOutputStream.writeShort(0);
                } else {
                    dataOutputStream.writeShort(map3.size());
                    for (Map.Entry<String, iu> entry3 : map3.entrySet()) {
                        dataOutputStream.writeUTF(entry3.getKey());
                        dataOutputStream.writeInt(entry3.getValue().a);
                    }
                }
                List<iv> list = izVar.s;
                if (list == null) {
                    dataOutputStream.writeShort(0);
                } else {
                    dataOutputStream.writeShort(list.size());
                    Iterator<iv> it = list.iterator();
                    while (it.hasNext()) {
                        dataOutputStream.write(it.next().b());
                    }
                }
                dataOutputStream.writeBoolean(izVar.t);
                List<it> list2 = izVar.v;
                if (list2 != null) {
                    int i2 = 0;
                    int i3 = 0;
                    int i4 = 0;
                    while (true) {
                        if (i2 >= list2.size()) {
                            i = i3;
                            break;
                        }
                        i4 += list2.get(i2).a().length;
                        if (i4 > 160000) {
                            kf.a(5, b, "Error Log size exceeded. No more event details logged.");
                            i = i3;
                            break;
                        } else {
                            i3++;
                            i2++;
                        }
                    }
                } else {
                    i = 0;
                }
                dataOutputStream.writeInt(izVar.u);
                dataOutputStream.writeShort(i);
                for (int i5 = 0; i5 < i; i5++) {
                    dataOutputStream.write(list2.get(i5).a());
                }
                dataOutputStream.writeInt(-1);
                dataOutputStream.writeShort(0);
                dataOutputStream.writeShort(0);
                dataOutputStream.writeShort(0);
                this.a = byteArrayOutputStream.toByteArray();
                lr.a(dataOutputStream);
            } catch (IOException e) {
                e = e;
                dataOutputStream2 = dataOutputStream;
                try {
                    kf.a(6, b, "", e);
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
        } catch (IOException e2) {
            e = e2;
        } catch (Throwable th3) {
            th = th3;
            dataOutputStream = null;
        }
    }
}
