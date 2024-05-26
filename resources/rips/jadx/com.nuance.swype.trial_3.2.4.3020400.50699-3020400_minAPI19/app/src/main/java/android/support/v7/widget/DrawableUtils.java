package android.support.v7.widget;

import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ScaleDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.support.v4.graphics.drawable.DrawableWrapper;

/* loaded from: classes.dex */
public final class DrawableUtils {
    public static final Rect INSETS_NONE = new Rect();
    private static Class<?> sInsetsClazz;

    static {
        if (Build.VERSION.SDK_INT >= 18) {
            try {
                sInsetsClazz = Class.forName("android.graphics.Insets");
            } catch (ClassNotFoundException e) {
            }
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code restructure failed: missing block: B:13:0x003d, code lost:            switch(r3) {            case 0: goto L24;            case 1: goto L30;            case 2: goto L31;            case 3: goto L32;            default: goto L37;        };     */
    /* JADX WARN: Code restructure failed: missing block: B:14:0x0070, code lost:            r2.left = r0.getInt(r1);     */
    /* JADX WARN: Code restructure failed: missing block: B:16:0x0040, code lost:            r5 = r5 + 1;     */
    /* JADX WARN: Code restructure failed: missing block: B:19:0x0084, code lost:            r2.top = r0.getInt(r1);     */
    /* JADX WARN: Code restructure failed: missing block: B:21:0x008b, code lost:            r2.right = r0.getInt(r1);     */
    /* JADX WARN: Code restructure failed: missing block: B:23:0x0092, code lost:            r2.bottom = r0.getInt(r1);     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static android.graphics.Rect getOpticalBounds(android.graphics.drawable.Drawable r10) {
        /*
            r4 = 0
            java.lang.Class<?> r3 = android.support.v7.widget.DrawableUtils.sInsetsClazz
            if (r3 == 0) goto L81
            android.graphics.drawable.Drawable r10 = android.support.v4.graphics.drawable.DrawableCompat.unwrap(r10)     // Catch: java.lang.Exception -> L77
            java.lang.Class r3 = r10.getClass()     // Catch: java.lang.Exception -> L77
            java.lang.String r5 = "getOpticalInsets"
            r6 = 0
            java.lang.Class[] r6 = new java.lang.Class[r6]     // Catch: java.lang.Exception -> L77
            java.lang.reflect.Method r3 = r3.getMethod(r5, r6)     // Catch: java.lang.Exception -> L77
            r5 = 0
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch: java.lang.Exception -> L77
            java.lang.Object r1 = r3.invoke(r10, r5)     // Catch: java.lang.Exception -> L77
            if (r1 == 0) goto L81
            android.graphics.Rect r2 = new android.graphics.Rect     // Catch: java.lang.Exception -> L77
            r2.<init>()     // Catch: java.lang.Exception -> L77
            java.lang.Class<?> r3 = android.support.v7.widget.DrawableUtils.sInsetsClazz     // Catch: java.lang.Exception -> L77
            java.lang.reflect.Field[] r6 = r3.getFields()     // Catch: java.lang.Exception -> L77
            int r7 = r6.length     // Catch: java.lang.Exception -> L77
            r5 = r4
        L2d:
            if (r5 >= r7) goto L83
            r0 = r6[r5]     // Catch: java.lang.Exception -> L77
            java.lang.String r8 = r0.getName()     // Catch: java.lang.Exception -> L77
            r3 = -1
            int r9 = r8.hashCode()     // Catch: java.lang.Exception -> L77
            switch(r9) {
                case -1383228885: goto L65;
                case 115029: goto L4f;
                case 3317767: goto L44;
                case 108511772: goto L5a;
                default: goto L3d;
            }     // Catch: java.lang.Exception -> L77
        L3d:
            switch(r3) {
                case 0: goto L70;
                case 1: goto L84;
                case 2: goto L8b;
                case 3: goto L92;
                default: goto L40;
            }     // Catch: java.lang.Exception -> L77
        L40:
            int r3 = r5 + 1
            r5 = r3
            goto L2d
        L44:
            java.lang.String r9 = "left"
            boolean r8 = r8.equals(r9)     // Catch: java.lang.Exception -> L77
            if (r8 == 0) goto L3d
            r3 = r4
            goto L3d
        L4f:
            java.lang.String r9 = "top"
            boolean r8 = r8.equals(r9)     // Catch: java.lang.Exception -> L77
            if (r8 == 0) goto L3d
            r3 = 1
            goto L3d
        L5a:
            java.lang.String r9 = "right"
            boolean r8 = r8.equals(r9)     // Catch: java.lang.Exception -> L77
            if (r8 == 0) goto L3d
            r3 = 2
            goto L3d
        L65:
            java.lang.String r9 = "bottom"
            boolean r8 = r8.equals(r9)     // Catch: java.lang.Exception -> L77
            if (r8 == 0) goto L3d
            r3 = 3
            goto L3d
        L70:
            int r3 = r0.getInt(r1)     // Catch: java.lang.Exception -> L77
            r2.left = r3     // Catch: java.lang.Exception -> L77
            goto L40
        L77:
            r3 = move-exception
            java.lang.String r3 = "DrawableUtils"
            java.lang.String r4 = "Couldn't obtain the optical insets. Ignoring."
            android.util.Log.e(r3, r4)
        L81:
            android.graphics.Rect r2 = android.support.v7.widget.DrawableUtils.INSETS_NONE
        L83:
            return r2
        L84:
            int r3 = r0.getInt(r1)     // Catch: java.lang.Exception -> L77
            r2.top = r3     // Catch: java.lang.Exception -> L77
            goto L40
        L8b:
            int r3 = r0.getInt(r1)     // Catch: java.lang.Exception -> L77
            r2.right = r3     // Catch: java.lang.Exception -> L77
            goto L40
        L92:
            int r3 = r0.getInt(r1)     // Catch: java.lang.Exception -> L77
            r2.bottom = r3     // Catch: java.lang.Exception -> L77
            goto L40
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v7.widget.DrawableUtils.getOpticalBounds(android.graphics.drawable.Drawable):android.graphics.Rect");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void fixDrawable(Drawable drawable) {
        if (Build.VERSION.SDK_INT != 21 || !"android.graphics.drawable.VectorDrawable".equals(drawable.getClass().getName())) {
            return;
        }
        int[] state = drawable.getState();
        if (state == null || state.length == 0) {
            drawable.setState(ThemeUtils.CHECKED_STATE_SET);
        } else {
            drawable.setState(ThemeUtils.EMPTY_STATE_SET);
        }
        drawable.setState(state);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static boolean canSafelyMutateDrawable(Drawable drawable) {
        while (true) {
            if (Build.VERSION.SDK_INT < 8 && (drawable instanceof StateListDrawable)) {
                return false;
            }
            if (Build.VERSION.SDK_INT < 15 && (drawable instanceof InsetDrawable)) {
                return false;
            }
            if (Build.VERSION.SDK_INT < 15 && (drawable instanceof GradientDrawable)) {
                return false;
            }
            if (Build.VERSION.SDK_INT < 17 && (drawable instanceof LayerDrawable)) {
                return false;
            }
            if (drawable instanceof DrawableContainer) {
                Drawable.ConstantState state = drawable.getConstantState();
                if (state instanceof DrawableContainer.DrawableContainerState) {
                    for (Drawable drawable2 : ((DrawableContainer.DrawableContainerState) state).getChildren()) {
                        if (!canSafelyMutateDrawable(drawable2)) {
                            return false;
                        }
                    }
                }
            } else if (drawable instanceof DrawableWrapper) {
                drawable = ((DrawableWrapper) drawable).getWrappedDrawable();
            } else if (!(drawable instanceof android.support.v7.graphics.drawable.DrawableWrapper)) {
                if (!(drawable instanceof ScaleDrawable)) {
                    break;
                }
                drawable = ((ScaleDrawable) drawable).getDrawable();
            } else {
                drawable = ((android.support.v7.graphics.drawable.DrawableWrapper) drawable).mDrawable;
            }
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static PorterDuff.Mode parseTintMode(int value, PorterDuff.Mode defaultMode) {
        switch (value) {
            case 3:
                return PorterDuff.Mode.SRC_OVER;
            case 4:
            case 6:
            case 7:
            case 8:
            case 10:
            case 11:
            case 12:
            case 13:
            default:
                return defaultMode;
            case 5:
                return PorterDuff.Mode.SRC_IN;
            case 9:
                return PorterDuff.Mode.SRC_ATOP;
            case 14:
                return PorterDuff.Mode.MULTIPLY;
            case 15:
                return PorterDuff.Mode.SCREEN;
            case 16:
                if (Build.VERSION.SDK_INT >= 11) {
                    return PorterDuff.Mode.valueOf("ADD");
                }
                return defaultMode;
        }
    }
}
