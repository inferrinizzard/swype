package com.bumptech.glide.load.model;

import android.content.Context;
import android.net.Uri;
import com.bumptech.glide.load.data.DataFetcher;

/* loaded from: classes.dex */
public abstract class UriLoader<T> implements ModelLoader<Uri, T> {
    private final Context context;
    private final ModelLoader<GlideUrl, T> urlLoader;

    public abstract DataFetcher<T> getAssetPathFetcher(Context context, String str);

    public abstract DataFetcher<T> getLocalUriFetcher(Context context, Uri uri);

    @Override // com.bumptech.glide.load.model.ModelLoader
    public final /* bridge */ /* synthetic */ DataFetcher getResourceFetcher(Uri uri, int x1, int x2) {
        Uri uri2 = uri;
        String scheme = uri2.getScheme();
        if ("file".equals(scheme) || "content".equals(scheme) || "android.resource".equals(scheme)) {
            if (AssetUriParser.isAssetUri(uri2)) {
                return getAssetPathFetcher(this.context, AssetUriParser.toAssetPath(uri2));
            }
            return getLocalUriFetcher(this.context, uri2);
        }
        if (this.urlLoader == null) {
            return null;
        }
        if (!"http".equals(scheme) && !"https".equals(scheme)) {
            return null;
        }
        return this.urlLoader.getResourceFetcher(new GlideUrl(uri2.toString()), x1, x2);
    }

    public UriLoader(Context context, ModelLoader<GlideUrl, T> urlLoader) {
        this.context = context;
        this.urlLoader = urlLoader;
    }
}
