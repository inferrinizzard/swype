package com.bumptech.glide.load.model;

import android.net.Uri;
import com.bumptech.glide.load.data.DataFetcher;
import java.io.File;

/* loaded from: classes.dex */
public class FileLoader<T> implements ModelLoader<File, T> {
    private final ModelLoader<Uri, T> uriLoader;

    @Override // com.bumptech.glide.load.model.ModelLoader
    public final /* bridge */ /* synthetic */ DataFetcher getResourceFetcher(File file, int x1, int x2) {
        return this.uriLoader.getResourceFetcher(Uri.fromFile(file), x1, x2);
    }

    public FileLoader(ModelLoader<Uri, T> uriLoader) {
        this.uriLoader = uriLoader;
    }
}
