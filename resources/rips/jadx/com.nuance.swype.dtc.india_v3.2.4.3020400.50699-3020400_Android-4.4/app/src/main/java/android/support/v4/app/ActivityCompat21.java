package android.support.v4.app;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Parcelable;
import android.view.View;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
final class ActivityCompat21 {

    /* loaded from: classes.dex */
    public static abstract class SharedElementCallback21 {
        public abstract Parcelable onCaptureSharedElementSnapshot(View view, Matrix matrix, RectF rectF);

        public abstract View onCreateSnapshotView(Context context, Parcelable parcelable);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static android.app.SharedElementCallback createCallback(SharedElementCallback21 callback) {
        if (callback == null) {
            return null;
        }
        android.app.SharedElementCallback newListener = new SharedElementCallbackImpl(callback);
        return newListener;
    }

    /* loaded from: classes.dex */
    private static class SharedElementCallbackImpl extends android.app.SharedElementCallback {
        private SharedElementCallback21 mCallback;

        public SharedElementCallbackImpl(SharedElementCallback21 callback) {
            this.mCallback = callback;
        }

        @Override // android.app.SharedElementCallback
        public final void onSharedElementStart(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots) {
        }

        @Override // android.app.SharedElementCallback
        public final void onSharedElementEnd(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots) {
        }

        @Override // android.app.SharedElementCallback
        public final void onRejectSharedElements(List<View> rejectedSharedElements) {
        }

        @Override // android.app.SharedElementCallback
        public final void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
        }

        @Override // android.app.SharedElementCallback
        public final Parcelable onCaptureSharedElementSnapshot(View sharedElement, Matrix viewToGlobalMatrix, RectF screenBounds) {
            return this.mCallback.onCaptureSharedElementSnapshot(sharedElement, viewToGlobalMatrix, screenBounds);
        }

        @Override // android.app.SharedElementCallback
        public final View onCreateSnapshotView(Context context, Parcelable snapshot) {
            return this.mCallback.onCreateSnapshotView(context, snapshot);
        }
    }
}
