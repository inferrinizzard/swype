package com.flurry.sdk;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public final class iv {
    public String a;
    public boolean b;
    public boolean c;
    public long d;
    private final Map<String, String> e = new HashMap();
    private int f;
    long g;

    public iv(int i, String str, Map<String, String> map, long j) {
        this.f = i;
        this.a = str;
        if (map != null) {
            this.e.putAll(map);
        }
        this.g = j;
        this.b = false;
        if (this.b) {
            this.c = false;
        } else {
            this.c = true;
        }
    }

    public final synchronized byte[] b() {
        DataOutputStream dataOutputStream;
        Throwable th;
        DataOutputStream dataOutputStream2;
        byte[] bArr;
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            dataOutputStream = new DataOutputStream(byteArrayOutputStream);
            try {
                dataOutputStream.writeShort(this.f);
                dataOutputStream.writeUTF(this.a);
                dataOutputStream.writeShort(this.e.size());
                for (Map.Entry<String, String> entry : this.e.entrySet()) {
                    dataOutputStream.writeUTF(lr.b(entry.getKey()));
                    dataOutputStream.writeUTF(lr.b(entry.getValue()));
                }
                dataOutputStream.writeLong(this.g);
                dataOutputStream.writeLong(this.d);
                dataOutputStream.flush();
                bArr = byteArrayOutputStream.toByteArray();
                lr.a(dataOutputStream);
            } catch (IOException e) {
                dataOutputStream2 = dataOutputStream;
                try {
                    bArr = new byte[0];
                    lr.a(dataOutputStream2);
                    return bArr;
                } catch (Throwable th2) {
                    th = th2;
                    dataOutputStream = dataOutputStream2;
                    lr.a(dataOutputStream);
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                lr.a(dataOutputStream);
                throw th;
            }
        } catch (IOException e2) {
            dataOutputStream2 = null;
        } catch (Throwable th4) {
            dataOutputStream = null;
            th = th4;
        }
        return bArr;
    }
}
