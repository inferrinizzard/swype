package com.google.android.gms.common.util;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/* loaded from: classes.dex */
public final class zzo {
    public static long zza$40f06453$1ade7318(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] bArr = new byte[1024];
        long j = 0;
        while (true) {
            try {
                int read = inputStream.read(bArr, 0, 1024);
                if (read == -1) {
                    return j;
                }
                j += read;
                outputStream.write(bArr, 0, read);
            } finally {
                zzb(inputStream);
                zzb(outputStream);
            }
        }
    }

    public static void zzb(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
            }
        }
    }
}
