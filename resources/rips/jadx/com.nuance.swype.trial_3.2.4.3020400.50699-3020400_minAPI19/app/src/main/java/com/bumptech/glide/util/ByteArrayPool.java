package com.bumptech.glide.util;

import android.util.Log;
import java.util.Queue;

/* loaded from: classes.dex */
public final class ByteArrayPool {
    private static final ByteArrayPool BYTE_ARRAY_POOL = new ByteArrayPool();
    private final Queue<byte[]> tempQueue = Util.createQueue(0);

    public static ByteArrayPool get() {
        return BYTE_ARRAY_POOL;
    }

    private ByteArrayPool() {
    }

    public final byte[] getBytes() {
        byte[] result;
        synchronized (this.tempQueue) {
            result = this.tempQueue.poll();
        }
        if (result == null) {
            byte[] result2 = new byte[65536];
            Log.isLoggable("ByteArrayPool", 3);
            return result2;
        }
        return result;
    }

    public final boolean releaseBytes(byte[] bytes) {
        if (bytes.length != 65536) {
            return false;
        }
        boolean accepted = false;
        synchronized (this.tempQueue) {
            if (this.tempQueue.size() < 32) {
                accepted = true;
                this.tempQueue.offer(bytes);
            }
        }
        return accepted;
    }
}
