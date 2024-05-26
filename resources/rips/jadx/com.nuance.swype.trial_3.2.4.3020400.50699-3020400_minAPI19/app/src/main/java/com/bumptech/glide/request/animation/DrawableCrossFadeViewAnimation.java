package com.bumptech.glide.request.animation;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;

/* loaded from: classes.dex */
public final class DrawableCrossFadeViewAnimation<T extends Drawable> implements GlideAnimation<T> {
    private final GlideAnimation<T> defaultAnimation;
    private final int duration;

    @Override // com.bumptech.glide.request.animation.GlideAnimation
    public final /* bridge */ /* synthetic */ boolean animate(Object obj, GlideAnimation.ViewAdapter viewAdapter) {
        Drawable drawable = (Drawable) obj;
        Drawable currentDrawable = viewAdapter.getCurrentDrawable();
        if (currentDrawable != null) {
            TransitionDrawable transitionDrawable = new TransitionDrawable(new Drawable[]{currentDrawable, drawable});
            transitionDrawable.setCrossFadeEnabled(true);
            transitionDrawable.startTransition(this.duration);
            viewAdapter.setDrawable(transitionDrawable);
            return true;
        }
        this.defaultAnimation.animate(drawable, viewAdapter);
        return false;
    }

    public DrawableCrossFadeViewAnimation(GlideAnimation<T> defaultAnimation, int duration) {
        this.defaultAnimation = defaultAnimation;
        this.duration = duration;
    }
}
