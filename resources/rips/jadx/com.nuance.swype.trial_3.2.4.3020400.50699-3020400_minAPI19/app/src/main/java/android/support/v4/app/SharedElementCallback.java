package android.support.v4.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.ImageView;

/* loaded from: classes.dex */
public abstract class SharedElementCallback {
    static int MAX_IMAGE_SIZE = 1048576;
    Matrix mTempMatrix;

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Bitmap createDrawableBitmap(Drawable drawable) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        if (width <= 0 || height <= 0) {
            return null;
        }
        float scale = Math.min(1.0f, MAX_IMAGE_SIZE / (width * height));
        if ((drawable instanceof BitmapDrawable) && scale == 1.0f) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        int bitmapWidth = (int) (width * scale);
        int bitmapHeight = (int) (height * scale);
        Bitmap bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Rect existingBounds = drawable.getBounds();
        int left = existingBounds.left;
        int top = existingBounds.top;
        int right = existingBounds.right;
        int bottom = existingBounds.bottom;
        drawable.setBounds(0, 0, bitmapWidth, bitmapHeight);
        drawable.draw(canvas);
        drawable.setBounds(left, top, right, bottom);
        return bitmap;
    }

    public static View onCreateSnapshotView(Context context, Parcelable snapshot) {
        ImageView view = null;
        if (snapshot instanceof Bundle) {
            Bundle bundle = (Bundle) snapshot;
            Bitmap bitmap = (Bitmap) bundle.getParcelable("sharedElement:snapshot:bitmap");
            if (bitmap == null) {
                return null;
            }
            ImageView imageView = new ImageView(context);
            view = imageView;
            imageView.setImageBitmap(bitmap);
            imageView.setScaleType(ImageView.ScaleType.valueOf(bundle.getString("sharedElement:snapshot:imageScaleType")));
            if (imageView.getScaleType() == ImageView.ScaleType.MATRIX) {
                float[] values = bundle.getFloatArray("sharedElement:snapshot:imageMatrix");
                Matrix matrix = new Matrix();
                matrix.setValues(values);
                imageView.setImageMatrix(matrix);
            }
        } else if (snapshot instanceof Bitmap) {
            view = new ImageView(context);
            view.setImageBitmap((Bitmap) snapshot);
        }
        return view;
    }
}
