package com.bumptech.glide.load.model;

import android.os.ParcelFileDescriptor;
import com.bumptech.glide.load.Encoder;
import java.io.InputStream;
import java.io.OutputStream;

/* loaded from: classes.dex */
public final class ImageVideoWrapperEncoder implements Encoder<ImageVideoWrapper> {
    private final Encoder<ParcelFileDescriptor> fileDescriptorEncoder;
    private String id;
    private final Encoder<InputStream> streamEncoder;

    @Override // com.bumptech.glide.load.Encoder
    public final /* bridge */ /* synthetic */ boolean encode(ImageVideoWrapper imageVideoWrapper, OutputStream x1) {
        ImageVideoWrapper imageVideoWrapper2 = imageVideoWrapper;
        if (imageVideoWrapper2.streamData != null) {
            return this.streamEncoder.encode(imageVideoWrapper2.streamData, x1);
        }
        return this.fileDescriptorEncoder.encode(imageVideoWrapper2.fileDescriptor, x1);
    }

    public ImageVideoWrapperEncoder(Encoder<InputStream> streamEncoder, Encoder<ParcelFileDescriptor> fileDescriptorEncoder) {
        this.streamEncoder = streamEncoder;
        this.fileDescriptorEncoder = fileDescriptorEncoder;
    }

    @Override // com.bumptech.glide.load.Encoder
    public final String getId() {
        if (this.id == null) {
            this.id = this.streamEncoder.getId() + this.fileDescriptorEncoder.getId();
        }
        return this.id;
    }
}
