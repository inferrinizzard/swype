package com.bumptech.glide.load.data;

import android.content.res.AssetManager;
import android.os.ParcelFileDescriptor;
import java.io.IOException;

/* loaded from: classes.dex */
public final class FileDescriptorAssetPathFetcher extends AssetPathFetcher<ParcelFileDescriptor> {
    @Override // com.bumptech.glide.load.data.AssetPathFetcher
    protected final /* bridge */ /* synthetic */ void close(ParcelFileDescriptor parcelFileDescriptor) throws IOException {
        parcelFileDescriptor.close();
    }

    public FileDescriptorAssetPathFetcher(AssetManager assetManager, String assetPath) {
        super(assetManager, assetPath);
    }

    @Override // com.bumptech.glide.load.data.AssetPathFetcher
    protected final /* bridge */ /* synthetic */ ParcelFileDescriptor loadResource(AssetManager x0, String x1) throws IOException {
        return x0.openFd(x1).getParcelFileDescriptor();
    }
}
