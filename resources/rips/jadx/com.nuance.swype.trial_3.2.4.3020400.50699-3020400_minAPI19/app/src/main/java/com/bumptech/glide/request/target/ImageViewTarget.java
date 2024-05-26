package com.bumptech.glide.request.target;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import com.bumptech.glide.request.animation.GlideAnimation;

/* loaded from: classes.dex */
public abstract class ImageViewTarget<Z> extends ViewTarget<ImageView, Z> implements GlideAnimation.ViewAdapter {
    protected abstract void setResource(Z z);

    public ImageViewTarget(ImageView view) {
        super(view);
    }

    @Override // com.bumptech.glide.request.animation.GlideAnimation.ViewAdapter
    public final Drawable getCurrentDrawable() {
        return ((ImageView) this.view).getDrawable();
    }

    @Override // com.bumptech.glide.request.animation.GlideAnimation.ViewAdapter
    public final void setDrawable(Drawable drawable) {
        ((ImageView) this.view).setImageDrawable(drawable);
    }

    @Override // com.bumptech.glide.request.target.BaseTarget, com.bumptech.glide.request.target.Target
    public final void onLoadStarted(Drawable placeholder) {
        ((ImageView) this.view).setImageDrawable(placeholder);
    }

    @Override // com.bumptech.glide.request.target.BaseTarget, com.bumptech.glide.request.target.Target
    public final void onLoadFailed$71731cd5(Drawable errorDrawable) {
        ((ImageView) this.view).setImageDrawable(errorDrawable);
    }

    @Override // com.bumptech.glide.request.target.BaseTarget, com.bumptech.glide.request.target.Target
    public final void onLoadCleared(Drawable placeholder) {
        ((ImageView) this.view).setImageDrawable(placeholder);
    }

    @Override // com.bumptech.glide.request.target.Target
    public void onResourceReady(Z resource, GlideAnimation<? super Z> glideAnimation) {
        if (glideAnimation == null || !glideAnimation.animate(resource, this)) {
            setResource(resource);
        }
    }
}
