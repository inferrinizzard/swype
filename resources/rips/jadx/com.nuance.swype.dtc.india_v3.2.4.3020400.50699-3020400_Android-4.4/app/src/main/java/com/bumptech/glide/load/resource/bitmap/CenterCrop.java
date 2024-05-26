package com.bumptech.glide.load.resource.bitmap;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;

/* loaded from: classes.dex */
public final class CenterCrop extends BitmapTransformation {
    public CenterCrop(BitmapPool bitmapPool) {
        super(bitmapPool);
    }

    @Override // com.bumptech.glide.load.resource.bitmap.BitmapTransformation
    protected final Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        float width;
        float height;
        Bitmap transformed;
        float f = 0.0f;
        Bitmap toReuse = pool.get(outWidth, outHeight, toTransform.getConfig() != null ? toTransform.getConfig() : Bitmap.Config.ARGB_8888);
        if (toTransform == null) {
            transformed = null;
        } else if (toTransform.getWidth() == outWidth && toTransform.getHeight() == outHeight) {
            transformed = toTransform;
        } else {
            Matrix matrix = new Matrix();
            if (toTransform.getWidth() * outHeight > toTransform.getHeight() * outWidth) {
                width = outHeight / toTransform.getHeight();
                f = (outWidth - (toTransform.getWidth() * width)) * 0.5f;
                height = 0.0f;
            } else {
                width = outWidth / toTransform.getWidth();
                height = (outHeight - (toTransform.getHeight() * width)) * 0.5f;
            }
            matrix.setScale(width, width);
            matrix.postTranslate((int) (f + 0.5f), (int) (height + 0.5f));
            Bitmap createBitmap = toReuse != null ? toReuse : Bitmap.createBitmap(outWidth, outHeight, TransformationUtils.getSafeConfig(toTransform));
            TransformationUtils.setAlpha(toTransform, createBitmap);
            new Canvas(createBitmap).drawBitmap(toTransform, matrix, new Paint(6));
            transformed = createBitmap;
        }
        if (toReuse != null && toReuse != transformed && !pool.put(toReuse)) {
            toReuse.recycle();
        }
        return transformed;
    }

    @Override // com.bumptech.glide.load.Transformation
    public final String getId() {
        return "CenterCrop.com.bumptech.glide.load.resource.bitmap";
    }
}
