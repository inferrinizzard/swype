package com.bumptech.glide.request.target;

import android.graphics.drawable.Drawable;
import com.bumptech.glide.manager.LifecycleListener;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.animation.GlideAnimation;

/* loaded from: classes.dex */
public interface Target<R> extends LifecycleListener {
    Request getRequest();

    void getSize(SizeReadyCallback sizeReadyCallback);

    void onLoadCleared(Drawable drawable);

    void onLoadFailed$71731cd5(Drawable drawable);

    void onLoadStarted(Drawable drawable);

    void onResourceReady(R r, GlideAnimation<? super R> glideAnimation);

    void setRequest(Request request);
}
