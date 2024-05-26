package com.bumptech.glide.request.animation;

import android.view.View;
import android.view.animation.Animation;
import com.bumptech.glide.request.animation.GlideAnimation;

/* loaded from: classes.dex */
public final class ViewAnimation<R> implements GlideAnimation<R> {
    private final AnimationFactory animationFactory;

    /* loaded from: classes.dex */
    interface AnimationFactory {
        Animation build();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ViewAnimation(AnimationFactory animationFactory) {
        this.animationFactory = animationFactory;
    }

    @Override // com.bumptech.glide.request.animation.GlideAnimation
    public final boolean animate(R current, GlideAnimation.ViewAdapter adapter) {
        View view = adapter.getView();
        if (view != null) {
            view.clearAnimation();
            Animation animation = this.animationFactory.build();
            view.startAnimation(animation);
            return false;
        }
        return false;
    }
}
