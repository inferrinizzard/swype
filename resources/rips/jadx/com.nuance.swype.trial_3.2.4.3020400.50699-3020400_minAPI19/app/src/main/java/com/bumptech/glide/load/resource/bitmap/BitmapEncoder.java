package com.bumptech.glide.load.resource.bitmap;

import android.graphics.Bitmap;
import android.util.Log;
import com.bumptech.glide.load.ResourceEncoder;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.util.LogTime;
import com.bumptech.glide.util.Util;
import java.io.OutputStream;

/* loaded from: classes.dex */
public final class BitmapEncoder implements ResourceEncoder<Bitmap> {
    private Bitmap.CompressFormat compressFormat;
    private int quality;

    @Override // com.bumptech.glide.load.Encoder
    public final /* bridge */ /* synthetic */ boolean encode(Object x0, OutputStream x1) {
        Bitmap.CompressFormat compressFormat;
        Bitmap bitmap = (Bitmap) ((Resource) x0).get();
        long logTime = LogTime.getLogTime();
        if (this.compressFormat != null) {
            compressFormat = this.compressFormat;
        } else if (bitmap.hasAlpha()) {
            compressFormat = Bitmap.CompressFormat.PNG;
        } else {
            compressFormat = Bitmap.CompressFormat.JPEG;
        }
        bitmap.compress(compressFormat, this.quality, x1);
        if (Log.isLoggable("BitmapEncoder", 2)) {
            Log.v("BitmapEncoder", "Compressed with type: " + compressFormat + " of size " + Util.getBitmapByteSize(bitmap) + " in " + LogTime.getElapsedMillis(logTime));
            return true;
        }
        return true;
    }

    public BitmapEncoder() {
        this((byte) 0);
    }

    private BitmapEncoder(byte b) {
        this.compressFormat = null;
        this.quality = 90;
    }

    @Override // com.bumptech.glide.load.Encoder
    public final String getId() {
        return "BitmapEncoder.com.bumptech.glide.load.resource.bitmap";
    }
}
