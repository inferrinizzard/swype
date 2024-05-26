package com.bumptech.glide.load.data;

import android.content.res.AssetManager;
import android.util.Log;
import com.bumptech.glide.Priority;
import java.io.IOException;

/* loaded from: classes.dex */
public abstract class AssetPathFetcher<T> implements DataFetcher<T> {
    private final AssetManager assetManager;
    private final String assetPath;
    private T data;

    protected abstract void close(T t) throws IOException;

    protected abstract T loadResource(AssetManager assetManager, String str) throws IOException;

    public AssetPathFetcher(AssetManager assetManager, String assetPath) {
        this.assetManager = assetManager;
        this.assetPath = assetPath;
    }

    @Override // com.bumptech.glide.load.data.DataFetcher
    public final T loadData(Priority priority) throws Exception {
        this.data = loadResource(this.assetManager, this.assetPath);
        return this.data;
    }

    @Override // com.bumptech.glide.load.data.DataFetcher
    public final void cleanup() {
        if (this.data != null) {
            try {
                close(this.data);
            } catch (IOException e) {
                if (Log.isLoggable("AssetUriFetcher", 2)) {
                    Log.v("AssetUriFetcher", "Failed to close data", e);
                }
            }
        }
    }

    @Override // com.bumptech.glide.load.data.DataFetcher
    public final String getId() {
        return this.assetPath;
    }

    @Override // com.bumptech.glide.load.data.DataFetcher
    public final void cancel() {
    }
}
