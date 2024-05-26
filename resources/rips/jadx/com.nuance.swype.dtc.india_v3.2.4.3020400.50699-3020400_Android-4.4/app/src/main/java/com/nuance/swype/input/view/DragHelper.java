package com.nuance.swype.input.view;

import android.annotation.TargetApi;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.View;
import com.nuance.android.compat.DrawableCompat;
import com.nuance.android.compat.ViewCompat;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public class DragHelper {
    private static final int ALPHA_OPAQUE = 255;
    private static final float DRAG_PAINT_ALPHA = 0.5f;
    private Paint dragPaint;
    private boolean isDragging = false;
    private Map<View, Integer> savedAlpha = new HashMap();

    /* loaded from: classes.dex */
    public interface DragVisualizer {
        void setDragHelper(DragHelper dragHelper);
    }

    protected ColorFilter createColorFilter() {
        return createSaturationColorFilter(0.75f, DRAG_PAINT_ALPHA);
    }

    public Paint getDragPaint() {
        if (this.dragPaint == null) {
            this.dragPaint = new Paint();
            this.dragPaint.setColorFilter(createColorFilter());
        }
        return this.dragPaint;
    }

    public boolean shouldUseDragPaint(View view) {
        return !shouldUseLayerPaint(view);
    }

    private boolean shouldUseLayerPaint(View view) {
        return canUseLayerPaint(view);
    }

    public static int scaleBackAlpha(View view, float alpha) {
        Drawable bg;
        if (view == null || (bg = view.getBackground()) == null) {
            return -1;
        }
        int old = DrawableCompat.getAlpha(bg, 255);
        int newAlpha = (int) (old * alpha);
        bg.setAlpha(newAlpha);
        return old;
    }

    public static int setBackAlpha(View view, int alpha) {
        Drawable bg;
        if (view == null || (bg = view.getBackground()) == null) {
            return -1;
        }
        int old = DrawableCompat.getAlpha(bg, 255);
        bg.setAlpha(alpha);
        return old;
    }

    @TargetApi(11)
    public static boolean canUseLayerPaint(View view) {
        return ViewCompat.supportsSetLayerPaint() && ViewCompat.isBackedByLayer(view);
    }

    public void onBeginDragVisualState() {
        this.isDragging = true;
    }

    public void onFinishDragVisualState() {
        this.savedAlpha.clear();
        this.isDragging = false;
    }

    public boolean isDragging() {
        return this.isDragging;
    }

    private void scaleAndMapBackAlpha(View view, float alphaScale) {
        if (alphaScale != 1.0f) {
            int oldAlpha = scaleBackAlpha(view, alphaScale);
            if (this.savedAlpha.containsKey(view)) {
                throw new AssertionError("Should not contain view");
            }
            this.savedAlpha.put(view, Integer.valueOf(oldAlpha));
        }
    }

    public void setDragVisualState(View view, float alphaScale) {
        if (view != null) {
            if (shouldUseLayerPaint(view)) {
                ViewCompat.setLayerPaint(view, getDragPaint());
            } else {
                scaleAndMapBackAlpha(view, alphaScale);
            }
        }
    }

    public void clearDragVisualState(View view) {
        if (view != null) {
            Integer backgroundAlpha = this.savedAlpha.remove(view);
            if (backgroundAlpha != null && backgroundAlpha.intValue() != -1) {
                setBackAlpha(view, backgroundAlpha.intValue());
            }
            if (shouldUseLayerPaint(view)) {
                ViewCompat.setLayerPaint(view, null);
            }
        }
    }

    private static float[] createAlphaMatrix(float alpha) {
        return new float[]{1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, alpha, 0.0f};
    }

    private static float[] createGrayScaleMatrix(float alpha) {
        return new float[]{0.3f, 0.59f, 0.11f, 0.0f, 0.0f, 0.3f, 0.59f, 0.11f, 0.0f, 0.0f, 0.3f, 0.59f, 0.11f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, alpha, 0.0f};
    }

    private static ColorFilter createSaturationColorFilter(float scale, float alpha) {
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(scale);
        if (alpha < 1.0f) {
            cm.postConcat(new ColorMatrix(createAlphaMatrix(alpha)));
        }
        return new ColorMatrixColorFilter(cm);
    }

    private static ColorFilter createGrayScaleColorFilter(float alpha) {
        return new ColorMatrixColorFilter(new ColorMatrix(createGrayScaleMatrix(alpha)));
    }
}
