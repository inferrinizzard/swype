package com.bumptech.glide.load.model.stream;

import android.content.Context;
import com.bumptech.glide.load.data.ByteArrayFetcher;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.GenericLoaderFactory;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import java.io.InputStream;

/* loaded from: classes.dex */
public final class StreamByteArrayLoader implements StreamModelLoader<byte[]> {
    private final String id;

    @Override // com.bumptech.glide.load.model.ModelLoader
    public final /* bridge */ /* synthetic */ DataFetcher getResourceFetcher(Object x0, int x1, int x2) {
        return new ByteArrayFetcher((byte[]) x0, this.id);
    }

    public StreamByteArrayLoader() {
        this("");
    }

    @Deprecated
    private StreamByteArrayLoader(String id) {
        this.id = id;
    }

    /* loaded from: classes.dex */
    public static class Factory implements ModelLoaderFactory<byte[], InputStream> {
        @Override // com.bumptech.glide.load.model.ModelLoaderFactory
        public final ModelLoader<byte[], InputStream> build(Context context, GenericLoaderFactory factories) {
            return new StreamByteArrayLoader();
        }
    }
}
