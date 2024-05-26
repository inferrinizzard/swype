package android.support.v4.view;

import android.view.View;

/* loaded from: classes.dex */
final class ViewCompatHC {
    /* JADX INFO: Access modifiers changed from: package-private */
    public static void offsetTopAndBottom(View view, int offset) {
        view.offsetTopAndBottom(offset);
        tickleInvalidationFlag(view);
        Object parent = view.getParent();
        if (parent instanceof View) {
            tickleInvalidationFlag((View) parent);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void offsetLeftAndRight(View view, int offset) {
        view.offsetLeftAndRight(offset);
        tickleInvalidationFlag(view);
        Object parent = view.getParent();
        if (parent instanceof View) {
            tickleInvalidationFlag((View) parent);
        }
    }

    private static void tickleInvalidationFlag(View view) {
        float y = view.getTranslationY();
        view.setTranslationY(1.0f + y);
        view.setTranslationY(y);
    }
}
