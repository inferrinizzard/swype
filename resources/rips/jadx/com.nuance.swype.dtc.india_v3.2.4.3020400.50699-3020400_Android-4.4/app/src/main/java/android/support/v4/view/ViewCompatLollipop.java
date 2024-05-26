package android.support.v4.view;

import android.graphics.Rect;

/* loaded from: classes.dex */
final class ViewCompatLollipop {
    private static ThreadLocal<Rect> sThreadLocalRect;

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Rect getEmptyTempRect() {
        if (sThreadLocalRect == null) {
            sThreadLocalRect = new ThreadLocal<>();
        }
        Rect rect = sThreadLocalRect.get();
        if (rect == null) {
            rect = new Rect();
            sThreadLocalRect.set(rect);
        }
        rect.setEmpty();
        return rect;
    }
}
