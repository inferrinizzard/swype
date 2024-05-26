package com.bumptech.glide.load.data;

import android.content.res.AssetManager;
import java.io.IOException;
import java.io.InputStream;

/* loaded from: classes.dex */
public final class StreamAssetPathFetcher extends AssetPathFetcher<InputStream> {
    @Override // com.bumptech.glide.load.data.AssetPathFetcher
    protected final /* bridge */ /* synthetic */ void close(InputStream inputStream) throws IOException {
        inputStream.close();
    }

    public StreamAssetPathFetcher(AssetManager assetManager, String assetPath) {
        super(assetManager, assetPath);
    }

    @Override // com.bumptech.glide.load.data.AssetPathFetcher
    protected final /* bridge */ /* synthetic */ InputStream loadResource(AssetManager x0, String x1) throws IOException {
        return x0.open(x1);
    }
}
