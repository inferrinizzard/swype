package com.bumptech.glide.load.resource.bitmap;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.ParcelFileDescriptor;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import java.io.IOException;

/* loaded from: classes.dex */
public final class FileDescriptorBitmapDecoder implements ResourceDecoder<ParcelFileDescriptor, Bitmap> {
    private final VideoBitmapDecoder bitmapDecoder;
    private final BitmapPool bitmapPool;
    private DecodeFormat decodeFormat;

    @Override // com.bumptech.glide.load.ResourceDecoder
    public final /* bridge */ /* synthetic */ Resource<Bitmap> decode(ParcelFileDescriptor parcelFileDescriptor, int x1, int x2) throws IOException {
        Bitmap frameAtTime;
        ParcelFileDescriptor parcelFileDescriptor2 = parcelFileDescriptor;
        VideoBitmapDecoder videoBitmapDecoder = this.bitmapDecoder;
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(parcelFileDescriptor2.getFileDescriptor());
        if (videoBitmapDecoder.frame >= 0) {
            frameAtTime = mediaMetadataRetriever.getFrameAtTime(videoBitmapDecoder.frame);
        } else {
            frameAtTime = mediaMetadataRetriever.getFrameAtTime();
        }
        mediaMetadataRetriever.release();
        parcelFileDescriptor2.close();
        return BitmapResource.obtain(frameAtTime, this.bitmapPool);
    }

    public FileDescriptorBitmapDecoder(BitmapPool bitmapPool, DecodeFormat decodeFormat) {
        this(new VideoBitmapDecoder(), bitmapPool, decodeFormat);
    }

    private FileDescriptorBitmapDecoder(VideoBitmapDecoder bitmapDecoder, BitmapPool bitmapPool, DecodeFormat decodeFormat) {
        this.bitmapDecoder = bitmapDecoder;
        this.bitmapPool = bitmapPool;
        this.decodeFormat = decodeFormat;
    }

    @Override // com.bumptech.glide.load.ResourceDecoder
    public final String getId() {
        return "FileDescriptorBitmapDecoder.com.bumptech.glide.load.data.bitmap";
    }
}
