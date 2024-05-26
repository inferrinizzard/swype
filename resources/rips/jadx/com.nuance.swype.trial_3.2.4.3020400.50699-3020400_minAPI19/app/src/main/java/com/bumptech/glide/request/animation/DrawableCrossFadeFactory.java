package com.bumptech.glide.request.animation;

import android.graphics.drawable.Drawable;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import com.bumptech.glide.request.animation.ViewAnimation;

/* loaded from: classes.dex */
public final class DrawableCrossFadeFactory<T extends Drawable> implements GlideAnimationFactory<T> {
    private final ViewAnimationFactory<T> animationFactory;
    private final int duration;
    private DrawableCrossFadeViewAnimation<T> firstResourceAnimation;
    private DrawableCrossFadeViewAnimation<T> secondResourceAnimation;

    public DrawableCrossFadeFactory() {
        this((byte) 0);
    }

    private DrawableCrossFadeFactory(byte b) {
        this(new ViewAnimationFactory(new DefaultAnimationFactory()));
    }

    private DrawableCrossFadeFactory(ViewAnimationFactory<T> animationFactory) {
        this.animationFactory = animationFactory;
        this.duration = 300;
    }

    @Override // com.bumptech.glide.request.animation.GlideAnimationFactory
    public final GlideAnimation<T> build(boolean isFromMemoryCache, boolean isFirstResource) {
        if (isFromMemoryCache) {
            return NoAnimation.get();
        }
        if (isFirstResource) {
            if (this.firstResourceAnimation == null) {
                this.firstResourceAnimation = new DrawableCrossFadeViewAnimation<>(this.animationFactory.build(false, true), this.duration);
            }
            return this.firstResourceAnimation;
        }
        if (this.secondResourceAnimation == null) {
            this.secondResourceAnimation = new DrawableCrossFadeViewAnimation<>(this.animationFactory.build(false, false), this.duration);
        }
        return this.secondResourceAnimation;
    }

    /* loaded from: classes.dex */
    private static class DefaultAnimationFactory implements ViewAnimation.AnimationFactory {
        private final int duration = 300;

        DefaultAnimationFactory() {
        }

        @Override // com.bumptech.glide.request.animation.ViewAnimation.AnimationFactory
        public final Animation build() {
            AlphaAnimation animation = new AlphaAnimation(0.0f, 1.0f);
            animation.setDuration(this.duration);
            return animation;
        }
    }
}
