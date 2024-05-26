package com.bumptech.glide.load.model;

import android.util.Log;
import com.bumptech.glide.load.Encoder;
import com.bumptech.glide.util.ByteArrayPool;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/* loaded from: classes.dex */
public final class StreamEncoder implements Encoder<InputStream> {
    @Override // com.bumptech.glide.load.Encoder
    public final /* bridge */ /* synthetic */ boolean encode(InputStream inputStream, OutputStream x1) {
        return encode2(inputStream, x1);
    }

    /* renamed from: encode, reason: avoid collision after fix types in other method */
    private static boolean encode2(InputStream data, OutputStream os) {
        byte[] buffer = ByteArrayPool.get().getBytes();
        while (true) {
            try {
                try {
                    int read = data.read(buffer);
                    if (read == -1) {
                        ByteArrayPool.get().releaseBytes(buffer);
                        return true;
                    }
                    os.write(buffer, 0, read);
                } catch (IOException e) {
                    Log.isLoggable("StreamEncoder", 3);
                    ByteArrayPool.get().releaseBytes(buffer);
                    return false;
                }
            } catch (Throwable th) {
                ByteArrayPool.get().releaseBytes(buffer);
                throw th;
            }
        }
    }

    @Override // com.bumptech.glide.load.Encoder
    public final String getId() {
        return "";
    }
}
