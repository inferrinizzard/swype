package com.bumptech.glide.load.model;

import android.os.ParcelFileDescriptor;
import java.io.InputStream;

/* loaded from: classes.dex */
public class ImageVideoWrapper {
    public final ParcelFileDescriptor fileDescriptor;
    public final InputStream streamData;

    public ImageVideoWrapper(InputStream streamData, ParcelFileDescriptor fileDescriptor) {
        this.streamData = streamData;
        this.fileDescriptor = fileDescriptor;
    }
}
