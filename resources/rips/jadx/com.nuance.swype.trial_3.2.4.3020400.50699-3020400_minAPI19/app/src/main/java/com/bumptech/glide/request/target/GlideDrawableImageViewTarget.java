package com.bumptech.glide.request.target;

import android.widget.ImageView;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;

/* loaded from: classes.dex */
public final class GlideDrawableImageViewTarget extends ImageViewTarget<GlideDrawable> {
    private int maxLoopCount;
    private GlideDrawable resource;

    @Override // com.bumptech.glide.request.target.ImageViewTarget, com.bumptech.glide.request.target.Target
    public final /* bridge */ /* synthetic */ void onResourceReady(Object x0, GlideAnimation x1) {
        GlideDrawable glideDrawable = (GlideDrawable) x0;
        if (!glideDrawable.isAnimated()) {
            float intrinsicWidth = glideDrawable.getIntrinsicWidth() / glideDrawable.getIntrinsicHeight();
            if (Math.abs((((ImageView) this.view).getWidth() / ((ImageView) this.view).getHeight()) - 1.0f) <= 0.05f && Math.abs(intrinsicWidth - 1.0f) <= 0.05f) {
                glideDrawable = new SquaringDrawable(glideDrawable, ((ImageView) this.view).getWidth());
            }
        }
        super.onResourceReady(glideDrawable, x1);
        this.resource = glideDrawable;
        glideDrawable.setLoopCount(this.maxLoopCount);
        glideDrawable.start();
    }

    @Override // com.bumptech.glide.request.target.ImageViewTarget
    protected final /* bridge */ /* synthetic */ void setResource(GlideDrawable glideDrawable) {
        ((ImageView) this.view).setImageDrawable(glideDrawable);
    }

    public GlideDrawableImageViewTarget(ImageView view) {
        this(view, (byte) 0);
    }

    private GlideDrawableImageViewTarget(ImageView view, byte b) {
        super(view);
        this.maxLoopCount = -1;
    }

    @Override // com.bumptech.glide.request.target.BaseTarget, com.bumptech.glide.manager.LifecycleListener
    public final void onStart() {
        if (this.resource != null) {
            this.resource.start();
        }
    }

    @Override // com.bumptech.glide.request.target.BaseTarget, com.bumptech.glide.manager.LifecycleListener
    public final void onStop() {
        if (this.resource != null) {
            this.resource.stop();
        }
    }
}
