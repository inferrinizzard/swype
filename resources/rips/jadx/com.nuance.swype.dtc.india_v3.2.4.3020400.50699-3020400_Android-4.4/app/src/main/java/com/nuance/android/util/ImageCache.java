package com.nuance.android.util;

import android.content.Context;
import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.ApplicationVersionSignature;
import com.nuance.swype.util.LogManager;
import com.nuance.swype.util.drawable.ImageViewWrapper;

/* loaded from: classes.dex */
public class ImageCache {
    private final RequestManager mRequestManager;
    private static volatile ImageCache singleton = null;
    private static final LogManager.Log log = LogManager.getLog("ImageCache");

    public static ImageCache with(Context context) {
        if (singleton == null) {
            synchronized (ImageCache.class) {
                if (singleton == null) {
                    singleton = new ImageCache(context);
                }
            }
        }
        return singleton;
    }

    private ImageCache(Context context) {
        this.mRequestManager = Glide.with(context);
    }

    public final void loadImage(String imagePath, int placeHolderResId, ImageViewWrapper target) {
        ((DrawableTypeRequest) this.mRequestManager.loadGeneric(String.class).load((DrawableTypeRequest) imagePath)).diskCacheStrategy(DiskCacheStrategy.RESULT).placeholder(placeHolderResId).dontAnimate().override(target.getWrapperImageWidth(), target.getWrapperImageHeight()).into(target);
        log.d("loadImage  imageWidth=" + target.getWrapperImageWidth() + " imageHeight=" + target.getWrapperImageHeight());
    }

    public final void loadImage(int resourceId, int placeHolderResId, ImageViewWrapper target) {
        RequestManager requestManager = this.mRequestManager;
        ((DrawableTypeRequest) ((DrawableTypeRequest) requestManager.loadGeneric(Integer.class).signature(ApplicationVersionSignature.obtain(requestManager.context))).load((DrawableTypeRequest) Integer.valueOf(resourceId))).diskCacheStrategy(DiskCacheStrategy.RESULT).placeholder(placeHolderResId).dontAnimate().into(target);
    }
}
