package com.bumptech.glide.load.model;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.util.Log;
import com.bumptech.glide.load.data.DataFetcher;

/* loaded from: classes.dex */
public class ResourceLoader<T> implements ModelLoader<Integer, T> {
    private final Resources resources;
    private final ModelLoader<Uri, T> uriLoader;

    public ResourceLoader(Context context, ModelLoader<Uri, T> uriLoader) {
        this(context.getResources(), uriLoader);
    }

    private ResourceLoader(Resources resources, ModelLoader<Uri, T> uriLoader) {
        this.resources = resources;
        this.uriLoader = uriLoader;
    }

    /* JADX INFO: Access modifiers changed from: private */
    @Override // com.bumptech.glide.load.model.ModelLoader
    public DataFetcher<T> getResourceFetcher(Integer model, int width, int height) {
        Uri uri = null;
        try {
            uri = Uri.parse("android.resource://" + this.resources.getResourcePackageName(model.intValue()) + '/' + this.resources.getResourceTypeName(model.intValue()) + '/' + this.resources.getResourceEntryName(model.intValue()));
        } catch (Resources.NotFoundException e) {
            if (Log.isLoggable("ResourceLoader", 5)) {
                Log.w("ResourceLoader", "Received invalid resource id: " + model, e);
            }
        }
        if (uri != null) {
            return this.uriLoader.getResourceFetcher(uri, width, height);
        }
        return null;
    }
}
