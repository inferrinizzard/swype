package com.bumptech.glide.load.resource.bitmap;

import android.os.ParcelFileDescriptor;

/* loaded from: classes.dex */
public final class VideoBitmapDecoder implements BitmapDecoder<ParcelFileDescriptor> {
    private static final MediaMetadataRetrieverFactory DEFAULT_FACTORY = new MediaMetadataRetrieverFactory();
    private MediaMetadataRetrieverFactory factory;
    int frame;

    public VideoBitmapDecoder() {
        this(DEFAULT_FACTORY);
    }

    private VideoBitmapDecoder(MediaMetadataRetrieverFactory factory) {
        this.factory = factory;
        this.frame = -1;
    }

    @Override // com.bumptech.glide.load.resource.bitmap.BitmapDecoder
    public final String getId() {
        return "VideoBitmapDecoder.com.bumptech.glide.load.resource.bitmap";
    }

    /* loaded from: classes.dex */
    static class MediaMetadataRetrieverFactory {
        MediaMetadataRetrieverFactory() {
        }
    }
}
