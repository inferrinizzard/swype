package com.nuance.android.util;

import android.content.Context;
import android.content.res.Resources;
import com.bumptech.glide.load.model.GenericLoaderFactory;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.ModelCache;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.stream.BaseGlideUrlLoader;
import com.nuance.swype.input.R;
import com.nuance.swype.input.ThemeManager;
import java.io.InputStream;

/* loaded from: classes.dex */
public final class ThemeModelLoader extends BaseGlideUrlLoader<ThemeManager.SwypeTheme> {
    private final Context mContext;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.bumptech.glide.load.model.stream.BaseGlideUrlLoader
    public final /* bridge */ /* synthetic */ String getUrl$7e16248f(ThemeManager.SwypeTheme swypeTheme) {
        ThemeManager.SwypeTheme swypeTheme2 = swypeTheme;
        if (swypeTheme2.isConnectTheme()) {
            return ((ThemeManager.ConnectDownloadableThemeWrapper) swypeTheme2).getThumbnailUrl();
        }
        Resources resources = this.mContext.getResources();
        return "android.resource://" + resources.getResourcePackageName(swypeTheme2.getPreviewResId()) + '/' + resources.getResourceTypeName(swypeTheme2.getPreviewResId()) + '/' + resources.getResourceEntryName(swypeTheme2.getPreviewResId());
    }

    /* loaded from: classes.dex */
    public static class Factory implements ModelLoaderFactory<ThemeManager.SwypeTheme, InputStream> {
        private final ModelCache<ThemeManager.SwypeTheme, GlideUrl> modelCache = new ModelCache<>(R.styleable.ThemeTemplate_symKeyboardHwr123);

        @Override // com.bumptech.glide.load.model.ModelLoaderFactory
        public final ModelLoader<ThemeManager.SwypeTheme, InputStream> build(Context context, GenericLoaderFactory factories) {
            return new ThemeModelLoader(context, this.modelCache);
        }
    }

    public ThemeModelLoader(Context context, ModelCache<ThemeManager.SwypeTheme, GlideUrl> modelCache) {
        super(context, modelCache);
        this.mContext = context;
    }
}
