package com.google.api.client.util;

import com.nuance.swype.input.hardkey.HardKeyboardManager;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/* loaded from: classes.dex */
public final class IOUtils {
    public static long computeLength(StreamingContent content) throws IOException {
        ByteCountingOutputStream countingStream = new ByteCountingOutputStream();
        try {
            content.writeTo(countingStream);
            countingStream.close();
            return countingStream.count;
        } catch (Throwable th) {
            countingStream.close();
            throw th;
        }
    }

    public static void copy(InputStream inputStream, OutputStream outputStream, boolean closeInputStream) throws IOException {
        try {
            com.google.api.client.repackaged.com.google.common.base.Preconditions.checkNotNull(inputStream);
            com.google.api.client.repackaged.com.google.common.base.Preconditions.checkNotNull(outputStream);
            byte[] bArr = new byte[HardKeyboardManager.META_CTRL_ON];
            while (true) {
                int read = inputStream.read(bArr);
                if (read == -1) {
                    break;
                } else {
                    outputStream.write(bArr, 0, read);
                }
            }
        } finally {
            if (closeInputStream) {
                inputStream.close();
            }
        }
    }
}
