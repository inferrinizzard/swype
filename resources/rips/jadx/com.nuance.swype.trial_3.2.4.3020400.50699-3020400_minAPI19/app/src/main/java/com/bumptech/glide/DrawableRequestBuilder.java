package com.bumptech.glide;

import android.content.Context;
import android.widget.ImageView;
import com.bumptech.glide.load.Encoder;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.ImageVideoWrapper;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.load.resource.gifbitmap.GifBitmapWrapper;
import com.bumptech.glide.manager.Lifecycle;
import com.bumptech.glide.manager.RequestTracker;
import com.bumptech.glide.provider.LoadProvider;
import com.bumptech.glide.request.animation.DrawableCrossFadeFactory;
import com.bumptech.glide.request.animation.GlideAnimationFactory;
import com.bumptech.glide.request.target.Target;

/* loaded from: classes.dex */
public class DrawableRequestBuilder<ModelType> extends GenericRequestBuilder<ModelType, ImageVideoWrapper, GifBitmapWrapper, GlideDrawable> {
    /* JADX INFO: Access modifiers changed from: package-private */
    public DrawableRequestBuilder(Context context, Class<ModelType> modelClass, LoadProvider<ModelType, ImageVideoWrapper, GifBitmapWrapper, GlideDrawable> loadProvider, Glide glide, RequestTracker requestTracker, Lifecycle lifecycle) {
        super(context, modelClass, loadProvider, GlideDrawable.class, glide, requestTracker, lifecycle);
        super.animate(new DrawableCrossFadeFactory());
    }

    public final DrawableRequestBuilder<ModelType> thumbnail(DrawableRequestBuilder<?> thumbnailRequest) {
        super.thumbnail((GenericRequestBuilder) thumbnailRequest);
        return this;
    }

    public final DrawableRequestBuilder<ModelType> centerCrop() {
        super.transform(this.glide.drawableCenterCrop);
        return this;
    }

    @Override // com.bumptech.glide.GenericRequestBuilder
    public final DrawableRequestBuilder<ModelType> dontAnimate() {
        super.dontAnimate();
        return this;
    }

    @Override // com.bumptech.glide.GenericRequestBuilder
    public final DrawableRequestBuilder<ModelType> placeholder(int resourceId) {
        super.placeholder(resourceId);
        return this;
    }

    @Override // com.bumptech.glide.GenericRequestBuilder
    public final DrawableRequestBuilder<ModelType> diskCacheStrategy(DiskCacheStrategy strategy) {
        super.diskCacheStrategy(strategy);
        return this;
    }

    @Override // com.bumptech.glide.GenericRequestBuilder
    public final DrawableRequestBuilder<ModelType> override(int width, int height) {
        super.override(width, height);
        return this;
    }

    @Override // com.bumptech.glide.GenericRequestBuilder
    public final DrawableRequestBuilder<ModelType> signature(Key signature) {
        super.signature(signature);
        return this;
    }

    @Override // com.bumptech.glide.GenericRequestBuilder
    public final DrawableRequestBuilder<ModelType> load(ModelType model) {
        super.load((DrawableRequestBuilder<ModelType>) model);
        return this;
    }

    @Override // com.bumptech.glide.GenericRequestBuilder
    public final Target<GlideDrawable> into(ImageView view) {
        return super.into(view);
    }

    @Override // com.bumptech.glide.GenericRequestBuilder
    final void applyCenterCrop() {
        centerCrop();
    }

    @Override // com.bumptech.glide.GenericRequestBuilder
    final void applyFitCenter() {
        super.transform(this.glide.drawableFitCenter);
    }

    @Override // com.bumptech.glide.GenericRequestBuilder
    /* renamed from: clone */
    public final /* bridge */ /* synthetic */ GenericRequestBuilder mo28clone() {
        return (DrawableRequestBuilder) super.mo28clone();
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.bumptech.glide.GenericRequestBuilder
    public final /* bridge */ /* synthetic */ GenericRequestBuilder load(Object obj) {
        super.load((DrawableRequestBuilder<ModelType>) obj);
        return this;
    }

    @Override // com.bumptech.glide.GenericRequestBuilder
    public final /* bridge */ /* synthetic */ GenericRequestBuilder signature(Key x0) {
        super.signature(x0);
        return this;
    }

    @Override // com.bumptech.glide.GenericRequestBuilder
    public final /* bridge */ /* synthetic */ GenericRequestBuilder skipMemoryCache(boolean x0) {
        super.skipMemoryCache(x0);
        return this;
    }

    @Override // com.bumptech.glide.GenericRequestBuilder
    public final /* bridge */ /* synthetic */ GenericRequestBuilder placeholder(int x0) {
        super.placeholder(x0);
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.bumptech.glide.GenericRequestBuilder
    public final /* bridge */ /* synthetic */ GenericRequestBuilder animate(GlideAnimationFactory<GlideDrawable> glideAnimationFactory) {
        super.animate(glideAnimationFactory);
        return this;
    }

    @Override // com.bumptech.glide.GenericRequestBuilder
    public final /* bridge */ /* synthetic */ GenericRequestBuilder dontAnimate() {
        super.dontAnimate();
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.bumptech.glide.GenericRequestBuilder
    public final /* bridge */ /* synthetic */ GenericRequestBuilder transform(Transformation<GifBitmapWrapper>[] transformationArr) {
        super.transform(transformationArr);
        return this;
    }

    @Override // com.bumptech.glide.GenericRequestBuilder
    public final /* bridge */ /* synthetic */ GenericRequestBuilder diskCacheStrategy(DiskCacheStrategy x0) {
        super.diskCacheStrategy(x0);
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.bumptech.glide.GenericRequestBuilder
    public final /* bridge */ /* synthetic */ GenericRequestBuilder sourceEncoder(Encoder<ImageVideoWrapper> encoder) {
        super.sourceEncoder(encoder);
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.bumptech.glide.GenericRequestBuilder
    public final /* bridge */ /* synthetic */ GenericRequestBuilder decoder(ResourceDecoder<ImageVideoWrapper, GifBitmapWrapper> resourceDecoder) {
        super.decoder(resourceDecoder);
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.bumptech.glide.GenericRequestBuilder
    public final /* bridge */ /* synthetic */ GenericRequestBuilder thumbnail(GenericRequestBuilder<?, ?, ?, GlideDrawable> genericRequestBuilder) {
        super.thumbnail(genericRequestBuilder);
        return this;
    }

    @Override // com.bumptech.glide.GenericRequestBuilder
    /* renamed from: clone, reason: collision with other method in class */
    public /* bridge */ /* synthetic */ Object mo28clone() throws CloneNotSupportedException {
        return (DrawableRequestBuilder) super.mo28clone();
    }
}
