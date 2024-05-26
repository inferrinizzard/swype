package com.flurry.sdk;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/* loaded from: classes.dex */
public final class it {
    public String a;
    private int b;
    private long c;
    private String d;
    private String e;
    private Throwable f;

    public it(int i, long j, String str, String str2, String str3, Throwable th) {
        this.b = i;
        this.c = j;
        this.a = str;
        this.d = str2;
        this.e = str3;
        this.f = th;
    }

    public final byte[] a() {
        DataOutputStream dataOutputStream;
        Throwable th;
        byte[] bArr;
        ByteArrayOutputStream byteArrayOutputStream;
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        } catch (IOException e) {
            dataOutputStream = null;
        } catch (Throwable th2) {
            dataOutputStream = null;
            th = th2;
            lr.a(dataOutputStream);
            throw th;
        }
        try {
            try {
                dataOutputStream.writeShort(this.b);
                dataOutputStream.writeLong(this.c);
                dataOutputStream.writeUTF(this.a);
                dataOutputStream.writeUTF(this.d);
                dataOutputStream.writeUTF(this.e);
                if (this.f != null) {
                    if ("uncaught".equals(this.a)) {
                        dataOutputStream.writeByte(3);
                    } else {
                        dataOutputStream.writeByte(2);
                    }
                    dataOutputStream.writeByte(2);
                    StringBuilder sb = new StringBuilder("");
                    String property = System.getProperty("line.separator");
                    for (StackTraceElement stackTraceElement : this.f.getStackTrace()) {
                        sb.append(stackTraceElement);
                        sb.append(property);
                    }
                    if (this.f.getCause() != null) {
                        sb.append(property);
                        sb.append("Caused by: ");
                        for (StackTraceElement stackTraceElement2 : this.f.getCause().getStackTrace()) {
                            sb.append(stackTraceElement2);
                            sb.append(property);
                        }
                    }
                    byte[] bytes = sb.toString().getBytes();
                    dataOutputStream.writeInt(bytes.length);
                    dataOutputStream.write(bytes);
                } else {
                    dataOutputStream.writeByte(1);
                    dataOutputStream.writeByte(0);
                }
                dataOutputStream.flush();
                bArr = byteArrayOutputStream.toByteArray();
                lr.a(dataOutputStream);
            } catch (Throwable th3) {
                th = th3;
                lr.a(dataOutputStream);
                throw th;
            }
        } catch (IOException e2) {
            bArr = new byte[0];
            lr.a(dataOutputStream);
            return bArr;
        }
        return bArr;
    }
}
