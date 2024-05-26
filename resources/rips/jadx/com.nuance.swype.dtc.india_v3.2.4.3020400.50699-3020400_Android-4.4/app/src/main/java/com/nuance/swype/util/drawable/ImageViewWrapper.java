package com.nuance.swype.util.drawable;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.nuance.swype.util.LogManager;

/* loaded from: classes.dex */
public class ImageViewWrapper extends ImageView {
    private static final LogManager.Log log = LogManager.getLog("ImageViewWrapper");
    private int wrapperHeight;
    private int wrapperWidth;

    public ImageViewWrapper(Context context) {
        super(context);
    }

    public ImageViewWrapper(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageViewWrapper(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onDetachedFromWindow() {
        log.d("onDetachedFromWindow");
        setImageDrawable(null);
        Glide.clear(this);
        super.onDetachedFromWindow();
    }

    @Override // android.widget.ImageView
    public void setImageDrawable(Drawable drawable) {
        Drawable previousDrawable = getDrawable();
        if (drawable == null || !drawable.equals(previousDrawable)) {
            super.setImageDrawable(drawable);
            changeDrawableState(drawable, true);
            changeDrawableState(previousDrawable, false);
        }
    }

    private void changeDrawableState(Drawable drawable, boolean isDisplayed) {
        if (drawable instanceof LayerDrawable) {
            LayerDrawable layerDrawable = (LayerDrawable) drawable;
            int z = layerDrawable.getNumberOfLayers();
            for (int i = 0; i < z; i++) {
                changeDrawableState(layerDrawable.getDrawable(i), isDisplayed);
            }
        }
    }

    public void setWrapperImageWidth(int width) {
        this.wrapperWidth = width;
    }

    public void setWrapperImageHeight(int height) {
        this.wrapperHeight = height;
    }

    public int getWrapperImageWidth() {
        return this.wrapperWidth;
    }

    public int getWrapperImageHeight() {
        return this.wrapperHeight;
    }
}
