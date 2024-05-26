package com.bumptech.glide.request.target;

import android.graphics.Bitmap;
import android.widget.ImageView;

/* loaded from: classes.dex */
public final class BitmapImageViewTarget extends ImageViewTarget<Bitmap> {
    @Override // com.bumptech.glide.request.target.ImageViewTarget
    protected final /* bridge */ /* synthetic */ void setResource(Bitmap bitmap) {
        ((ImageView) this.view).setImageBitmap(bitmap);
    }

    public BitmapImageViewTarget(ImageView view) {
        super(view);
    }
}
