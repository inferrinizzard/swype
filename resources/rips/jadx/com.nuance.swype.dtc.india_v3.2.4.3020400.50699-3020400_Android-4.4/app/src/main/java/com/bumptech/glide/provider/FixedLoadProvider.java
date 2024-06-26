package com.bumptech.glide.provider;

import com.bumptech.glide.load.Encoder;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.ResourceEncoder;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.resource.transcode.ResourceTranscoder;
import java.io.File;

/* loaded from: classes.dex */
public final class FixedLoadProvider<A, T, Z, R> implements LoadProvider<A, T, Z, R> {
    private final DataLoadProvider<T, Z> dataLoadProvider;
    private final ModelLoader<A, T> modelLoader;
    private final ResourceTranscoder<Z, R> transcoder;

    public FixedLoadProvider(ModelLoader<A, T> modelLoader, ResourceTranscoder<Z, R> transcoder, DataLoadProvider<T, Z> dataLoadProvider) {
        if (modelLoader == null) {
            throw new NullPointerException("ModelLoader must not be null");
        }
        this.modelLoader = modelLoader;
        if (transcoder == null) {
            throw new NullPointerException("Transcoder must not be null");
        }
        this.transcoder = transcoder;
        if (dataLoadProvider == null) {
            throw new NullPointerException("DataLoadProvider must not be null");
        }
        this.dataLoadProvider = dataLoadProvider;
    }

    @Override // com.bumptech.glide.provider.LoadProvider
    public final ModelLoader<A, T> getModelLoader() {
        return this.modelLoader;
    }

    @Override // com.bumptech.glide.provider.LoadProvider
    public final ResourceTranscoder<Z, R> getTranscoder() {
        return this.transcoder;
    }

    @Override // com.bumptech.glide.provider.DataLoadProvider
    public final ResourceDecoder<File, Z> getCacheDecoder() {
        return this.dataLoadProvider.getCacheDecoder();
    }

    @Override // com.bumptech.glide.provider.DataLoadProvider
    public final ResourceDecoder<T, Z> getSourceDecoder() {
        return this.dataLoadProvider.getSourceDecoder();
    }

    @Override // com.bumptech.glide.provider.DataLoadProvider
    public final Encoder<T> getSourceEncoder() {
        return this.dataLoadProvider.getSourceEncoder();
    }

    @Override // com.bumptech.glide.provider.DataLoadProvider
    public final ResourceEncoder<Z> getEncoder() {
        return this.dataLoadProvider.getEncoder();
    }
}
