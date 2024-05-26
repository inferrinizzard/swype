package com.nuance.android.util;

import android.content.Context;
import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.MemoryCategory;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.module.GlideModule;
import com.nuance.android.util.ThemeModelLoader;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.ThemeManager;
import com.nuance.swype.util.LogManager;
import java.io.InputStream;

/* loaded from: classes.dex */
public class GlideConfiguration implements GlideModule {
    private static final LogManager.Log log = LogManager.getLog("GlideConfiguration");

    @Override // com.bumptech.glide.module.GlideModule
    public final void applyOptions(Context context, GlideBuilder builder) {
        if (IMEApplication.from(context.getApplicationContext()).isSmallImageCacheDevice()) {
            builder.decodeFormat = DecodeFormat.PREFER_RGB_565;
        } else {
            builder.decodeFormat = DecodeFormat.PREFER_ARGB_8888;
        }
    }

    @Override // com.bumptech.glide.module.GlideModule
    public final void registerComponents(Context context, Glide glide) {
        if (IMEApplication.from(context.getApplicationContext()).isSmallImageCacheDevice()) {
            glide.setMemoryCategory(MemoryCategory.LOW);
        } else {
            glide.setMemoryCategory(MemoryCategory.NORMAL);
        }
        glide.register(ThemeManager.SwypeTheme.class, InputStream.class, new ThemeModelLoader.Factory());
    }
}
