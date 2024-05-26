package com.bumptech.glide.load.resource.drawable;

import android.graphics.drawable.Drawable;
import com.bumptech.glide.load.engine.Resource;

/* loaded from: classes.dex */
public abstract class DrawableResource<T extends Drawable> implements Resource<T> {
    public final T drawable;

    public DrawableResource(T drawable) {
        if (drawable == null) {
            throw new NullPointerException("Drawable must not be null!");
        }
        this.drawable = drawable;
    }

    @Override // com.bumptech.glide.load.engine.Resource
    public final /* bridge */ /* synthetic */ Object get() {
        return this.drawable.getConstantState().newDrawable();
    }
}
