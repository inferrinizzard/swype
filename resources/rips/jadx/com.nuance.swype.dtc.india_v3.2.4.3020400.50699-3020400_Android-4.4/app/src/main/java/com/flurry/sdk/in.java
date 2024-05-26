package com.flurry.sdk;

import android.os.Build;
import com.flurry.sdk.io;
import com.flurry.sdk.kl;
import com.flurry.sdk.kn;
import com.nuance.swypeconnect.ac.ACReportingService;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.CRC32;

/* loaded from: classes.dex */
public class in {
    public static final String b = in.class.getName();
    private static in c = null;
    public String a;
    private jy<List<io>> d;
    private List<io> e;
    private boolean f;

    private in() {
    }

    public static synchronized in a() {
        in inVar;
        synchronized (in.class) {
            if (c == null) {
                in inVar2 = new in();
                c = inVar2;
                inVar2.d = new jy<>(jr.a().a.getFileStreamPath(".yflurrypulselogging." + Long.toString(lr.i(jr.a().d), 16)), ".yflurrypulselogging.", 1, new lc<List<io>>() { // from class: com.flurry.sdk.in.1
                    @Override // com.flurry.sdk.lc
                    public final kz<List<io>> a$1f969724() {
                        return new ky(new io.a());
                    }
                });
                inVar2.f = ((Boolean) li.a().a("UseHttps")).booleanValue();
                kf.a(4, b, "initSettings, UseHttps = " + inVar2.f);
                inVar2.e = inVar2.d.a();
                if (inVar2.e == null) {
                    inVar2.e = new ArrayList();
                }
            }
            inVar = c;
        }
        return inVar;
    }

    public final synchronized void a(im imVar) {
        try {
            this.e.add(new io(imVar.d()));
            kf.a(4, b, "Saving persistent Pulse logging data.");
            this.d.a(this.e);
        } catch (IOException e) {
            kf.a(6, b, "Error when generating pulse log report in addReport part");
        }
    }

    private byte[] d() throws IOException {
        DataOutputStream dataOutputStream;
        byte[] byteArray;
        DataOutputStream dataOutputStream2 = null;
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            dataOutputStream = new DataOutputStream(byteArrayOutputStream);
            try {
                if (this.e == null || this.e.isEmpty()) {
                    byteArray = byteArrayOutputStream.toByteArray();
                    lr.a(dataOutputStream);
                } else {
                    dataOutputStream.writeShort(1);
                    dataOutputStream.writeShort(1);
                    dataOutputStream.writeLong(System.currentTimeMillis());
                    dataOutputStream.writeUTF(jr.a().d);
                    dataOutputStream.writeUTF(jn.a().i());
                    dataOutputStream.writeShort(js.a());
                    dataOutputStream.writeShort(3);
                    jn.a();
                    dataOutputStream.writeUTF(jn.d());
                    dataOutputStream.writeBoolean(je.a().d());
                    ArrayList<ht> arrayList = new ArrayList();
                    for (Map.Entry entry : Collections.unmodifiableMap(je.a().a).entrySet()) {
                        ht htVar = new ht();
                        htVar.a = ((jm) entry.getKey()).c;
                        if (((jm) entry.getKey()).d) {
                            htVar.b = new String((byte[]) entry.getValue());
                        } else {
                            htVar.b = lr.b((byte[]) entry.getValue());
                        }
                        arrayList.add(htVar);
                    }
                    dataOutputStream.writeShort(arrayList.size());
                    for (ht htVar2 : arrayList) {
                        dataOutputStream.writeShort(htVar2.a);
                        byte[] bytes = htVar2.b.getBytes();
                        dataOutputStream.writeShort(bytes.length);
                        dataOutputStream.write(bytes);
                    }
                    dataOutputStream.writeShort(6);
                    dataOutputStream.writeShort(ig.MODEL.h);
                    dataOutputStream.writeUTF(Build.MODEL);
                    dataOutputStream.writeShort(ig.BRAND.h);
                    dataOutputStream.writeUTF(Build.BOARD);
                    dataOutputStream.writeShort(ig.ID.h);
                    dataOutputStream.writeUTF(Build.ID);
                    dataOutputStream.writeShort(ig.DEVICE.h);
                    dataOutputStream.writeUTF(Build.DEVICE);
                    dataOutputStream.writeShort(ig.PRODUCT.h);
                    dataOutputStream.writeUTF(Build.PRODUCT);
                    dataOutputStream.writeShort(ig.VERSION_RELEASE.h);
                    dataOutputStream.writeUTF(Build.VERSION.RELEASE);
                    dataOutputStream.writeShort(this.e.size());
                    Iterator<io> it = this.e.iterator();
                    while (it.hasNext()) {
                        dataOutputStream.write(it.next().a);
                    }
                    byte[] byteArray2 = byteArrayOutputStream.toByteArray();
                    CRC32 crc32 = new CRC32();
                    crc32.update(byteArray2);
                    dataOutputStream.writeInt((int) crc32.getValue());
                    byteArray = byteArrayOutputStream.toByteArray();
                    lr.a(dataOutputStream);
                }
                return byteArray;
            } catch (IOException e) {
                e = e;
                dataOutputStream2 = dataOutputStream;
                try {
                    kf.a(6, b, "Error when generating report", e);
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

    /* JADX WARN: Multi-variable type inference failed */
    private synchronized void a(byte[] bArr) {
        String str;
        if (!jk.a().b) {
            kf.a(5, b, "Reports were not sent! No Internet connection!");
        } else if (bArr == 0 || bArr.length == 0) {
            kf.a(3, b, "No report need be sent");
        } else {
            if (this.a != null) {
                str = this.a;
            } else {
                str = "https://data.flurry.com/pcr.do";
            }
            kf.a(4, b, "PulseLoggingManager: start upload data " + Arrays.toString(bArr) + " to " + str);
            kl klVar = new kl();
            klVar.f = str;
            klVar.w = ACReportingService.MAXIMUM_DATABASE_ENTRIES;
            klVar.g = kn.a.kPost;
            klVar.j = true;
            klVar.a("Content-Type", "application/octet-stream");
            klVar.c = new kv();
            klVar.b = bArr;
            klVar.a = new kl.a<byte[], Void>() { // from class: com.flurry.sdk.in.2
                @Override // com.flurry.sdk.kl.a
                public final /* synthetic */ void a(kl<byte[], Void> klVar2, Void r7) {
                    int i = klVar2.p;
                    if (i <= 0) {
                        kf.e(in.b, "Server Error: " + i);
                        return;
                    }
                    if (i < 200 || i >= 300) {
                        kf.a(3, in.b, "Pulse logging report sent unsuccessfully, HTTP response:" + i);
                        return;
                    }
                    kf.a(3, in.b, "Pulse logging report sent successfully HTTP response:" + i);
                    in.this.e.clear();
                    in.this.d.a(in.this.e);
                }
            };
            jp.a().a((Object) this, (in) klVar);
        }
    }

    public final synchronized void b() {
        try {
            a(d());
        } catch (IOException e) {
            kf.a(6, b, "Report not send due to exception in generate data");
        }
    }
}
