package com.bumptech.glide.load.resource.bitmap;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;

/* loaded from: classes.dex */
public final class FitCenter extends BitmapTransformation {
    public FitCenter(BitmapPool bitmapPool) {
        super(bitmapPool);
    }

    @Override // com.bumptech.glide.load.Transformation
    public final String getId() {
        return "FitCenter.com.bumptech.glide.load.resource.bitmap";
    }

    @Override // com.bumptech.glide.load.resource.bitmap.BitmapTransformation
    protected final Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        if (toTransform.getWidth() == outWidth && toTransform.getHeight() == outHeight) {
            if (Log.isLoggable("TransformationUtils", 2)) {
                Log.v("TransformationUtils", "requested target size matches input, returning input");
                return toTransform;
            }
            return toTransform;
        }
        float min = Math.min(outWidth / toTransform.getWidth(), outHeight / toTransform.getHeight());
        int width = (int) (toTransform.getWidth() * min);
        int height = (int) (toTransform.getHeight() * min);
        if (toTransform.getWidth() == width && toTransform.getHeight() == height) {
            if (Log.isLoggable("TransformationUtils", 2)) {
                Log.v("TransformationUtils", "adjusted target size matches input, returning input");
                return toTransform;
            }
            return toTransform;
        }
        Bitmap.Config safeConfig = TransformationUtils.getSafeConfig(toTransform);
        Bitmap toTransform2 = pool.get(width, height, safeConfig);
        if (toTransform2 == null) {
            toTransform2 = Bitmap.createBitmap(width, height, safeConfig);
        }
        TransformationUtils.setAlpha(toTransform, toTransform2);
        if (Log.isLoggable("TransformationUtils", 2)) {
            Log.v("TransformationUtils", "request: " + outWidth + "x" + outHeight);
            Log.v("TransformationUtils", "toFit:   " + toTransform.getWidth() + "x" + toTransform.getHeight());
            Log.v("TransformationUtils", "toReuse: " + toTransform2.getWidth() + "x" + toTransform2.getHeight());
            Log.v("TransformationUtils", "minPct:   " + min);
        }
        Canvas canvas = new Canvas(toTransform2);
        Matrix matrix = new Matrix();
        matrix.setScale(min, min);
        canvas.drawBitmap(toTransform, matrix, new Paint(6));
        return toTransform2;
    }
}
