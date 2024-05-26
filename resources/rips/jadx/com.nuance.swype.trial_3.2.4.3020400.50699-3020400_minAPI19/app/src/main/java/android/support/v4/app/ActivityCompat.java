package android.support.v4.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat21;
import android.support.v4.app.ActivityCompatApi23;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;

/* loaded from: classes.dex */
public final class ActivityCompat extends ContextCompat {

    /* loaded from: classes.dex */
    public interface OnRequestPermissionsResultCallback {
        void onRequestPermissionsResult(int i, String[] strArr, int[] iArr);
    }

    public static void startActivity(Activity activity, Intent intent, Bundle options) {
        if (Build.VERSION.SDK_INT < 16) {
            activity.startActivity(intent);
        } else {
            activity.startActivity(intent, options);
        }
    }

    public static void startActivityForResult(Activity activity, Intent intent, int requestCode, Bundle options) {
        if (Build.VERSION.SDK_INT < 16) {
            activity.startActivityForResult(intent, requestCode);
        } else {
            activity.startActivityForResult(intent, requestCode, options);
        }
    }

    public static void startIntentSenderForResult(Activity activity, IntentSender intent, int requestCode, Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags, Bundle options) throws IntentSender.SendIntentException {
        if (Build.VERSION.SDK_INT < 16) {
            if (Build.VERSION.SDK_INT < 5) {
                return;
            }
            activity.startIntentSenderForResult(intent, requestCode, fillInIntent, flagsMask, flagsValues, extraFlags);
            return;
        }
        activity.startIntentSenderForResult(intent, requestCode, fillInIntent, flagsMask, flagsValues, extraFlags, options);
    }

    public static void finishAfterTransition(Activity activity) {
        if (Build.VERSION.SDK_INT < 21) {
            activity.finish();
        } else {
            activity.finishAfterTransition();
        }
    }

    public static void setEnterSharedElementCallback(Activity activity, SharedElementCallback callback) {
        if (Build.VERSION.SDK_INT < 21) {
            return;
        }
        activity.setEnterSharedElementCallback(ActivityCompat21.createCallback(createCallback(callback)));
    }

    public static void setExitSharedElementCallback(Activity activity, SharedElementCallback callback) {
        if (Build.VERSION.SDK_INT < 21) {
            return;
        }
        activity.setExitSharedElementCallback(ActivityCompat21.createCallback(createCallback(callback)));
    }

    public static void postponeEnterTransition(Activity activity) {
        if (Build.VERSION.SDK_INT < 21) {
            return;
        }
        activity.postponeEnterTransition();
    }

    public static void startPostponedEnterTransition(Activity activity) {
        if (Build.VERSION.SDK_INT < 21) {
            return;
        }
        activity.startPostponedEnterTransition();
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static void requestPermissions(final Activity activity, final String[] permissions, final int requestCode) {
        if (Build.VERSION.SDK_INT < 23) {
            if (activity instanceof OnRequestPermissionsResultCallback) {
                new Handler(Looper.getMainLooper()).post(new Runnable() { // from class: android.support.v4.app.ActivityCompat.1
                    @Override // java.lang.Runnable
                    public final void run() {
                        int[] grantResults = new int[permissions.length];
                        PackageManager packageManager = activity.getPackageManager();
                        String packageName = activity.getPackageName();
                        int permissionCount = permissions.length;
                        for (int i = 0; i < permissionCount; i++) {
                            grantResults[i] = packageManager.checkPermission(permissions[i], packageName);
                        }
                        ((OnRequestPermissionsResultCallback) activity).onRequestPermissionsResult(requestCode, permissions, grantResults);
                    }
                });
            }
        } else {
            if (activity instanceof ActivityCompatApi23.RequestPermissionsRequestCodeValidator) {
                ((ActivityCompatApi23.RequestPermissionsRequestCodeValidator) activity).validateRequestPermissionsRequestCode(requestCode);
            }
            activity.requestPermissions(permissions, requestCode);
        }
    }

    public static boolean shouldShowRequestPermissionRationale(Activity activity, String permission) {
        if (Build.VERSION.SDK_INT >= 23) {
            return activity.shouldShowRequestPermissionRationale(permission);
        }
        return false;
    }

    private static ActivityCompat21.SharedElementCallback21 createCallback(SharedElementCallback callback) {
        if (callback == null) {
            return null;
        }
        ActivityCompat21.SharedElementCallback21 newCallback = new SharedElementCallback21Impl(callback);
        return newCallback;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class SharedElementCallback21Impl extends ActivityCompat21.SharedElementCallback21 {
        private SharedElementCallback mCallback;

        public SharedElementCallback21Impl(SharedElementCallback callback) {
            this.mCallback = callback;
        }

        @Override // android.support.v4.app.ActivityCompat21.SharedElementCallback21
        public final Parcelable onCaptureSharedElementSnapshot(View sharedElement, Matrix viewToGlobalMatrix, RectF screenBounds) {
            Bitmap createDrawableBitmap;
            SharedElementCallback sharedElementCallback = this.mCallback;
            if (sharedElement instanceof ImageView) {
                ImageView imageView = (ImageView) sharedElement;
                Drawable drawable = imageView.getDrawable();
                Drawable background = imageView.getBackground();
                if (drawable != null && background == null && (createDrawableBitmap = SharedElementCallback.createDrawableBitmap(drawable)) != null) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("sharedElement:snapshot:bitmap", createDrawableBitmap);
                    bundle.putString("sharedElement:snapshot:imageScaleType", imageView.getScaleType().toString());
                    if (imageView.getScaleType() == ImageView.ScaleType.MATRIX) {
                        float[] fArr = new float[9];
                        imageView.getImageMatrix().getValues(fArr);
                        bundle.putFloatArray("sharedElement:snapshot:imageMatrix", fArr);
                    }
                    return bundle;
                }
            }
            int round = Math.round(screenBounds.width());
            int round2 = Math.round(screenBounds.height());
            if (round <= 0 || round2 <= 0) {
                return null;
            }
            float min = Math.min(1.0f, SharedElementCallback.MAX_IMAGE_SIZE / (round * round2));
            int i = (int) (round * min);
            int i2 = (int) (round2 * min);
            if (sharedElementCallback.mTempMatrix == null) {
                sharedElementCallback.mTempMatrix = new Matrix();
            }
            sharedElementCallback.mTempMatrix.set(viewToGlobalMatrix);
            sharedElementCallback.mTempMatrix.postTranslate(-screenBounds.left, -screenBounds.top);
            sharedElementCallback.mTempMatrix.postScale(min, min);
            Bitmap createBitmap = Bitmap.createBitmap(i, i2, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(createBitmap);
            canvas.concat(sharedElementCallback.mTempMatrix);
            sharedElement.draw(canvas);
            return createBitmap;
        }

        @Override // android.support.v4.app.ActivityCompat21.SharedElementCallback21
        public final View onCreateSnapshotView(Context context, Parcelable snapshot) {
            return SharedElementCallback.onCreateSnapshotView(context, snapshot);
        }
    }
}
