package com.bumptech.glide.load.resource;

import com.bumptech.glide.load.ResourceEncoder;
import java.io.OutputStream;

/* loaded from: classes.dex */
public final class NullResourceEncoder<T> implements ResourceEncoder<T> {
    private static final NullResourceEncoder<?> NULL_ENCODER = new NullResourceEncoder<>();

    @Override // com.bumptech.glide.load.Encoder
    public final /* bridge */ /* synthetic */ boolean encode(Object x0, OutputStream x1) {
        return false;
    }

    public static <T> NullResourceEncoder<T> get() {
        return (NullResourceEncoder<T>) NULL_ENCODER;
    }

    @Override // com.bumptech.glide.load.Encoder
    public final String getId() {
        return "";
    }
}
