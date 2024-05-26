package com.nuance.swype.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import com.nuance.android.compat.ViewCompat;
import com.nuance.swype.input.R;
import com.nuance.swype.util.GeomUtil;
import com.nuance.swype.util.LogManager;
import com.nuance.swype.util.ViewUtil;

/* loaded from: classes.dex */
public class BasicViewLayout extends ViewGroup {
    private static final LogManager.Log log = LogManager.getLog("BasicViewLayout");
    private Drawable borderDrawable;
    private boolean isDebugMode;
    private Rect tempRect;

    public BasicViewLayout(Context context) {
        super(context);
        this.isDebugMode = false;
        this.tempRect = new Rect();
    }

    protected final void enableHardwareLayer() {
        Paint layerPaint = new Paint();
        layerPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
        ViewCompat.enableHardwareLayer(this, layerPaint);
    }

    public BasicViewLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.isDebugMode = false;
        this.tempRect = new Rect();
    }

    protected void enableDebugMode() {
        enableDebugMode(-256, 10);
    }

    protected void enableDebugMode(int color, int thickness) {
        this.isDebugMode = true;
        ShapeDrawable shapeDrawable = new ShapeDrawable(new RectShape());
        Paint paint = shapeDrawable.getPaint();
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(thickness);
        paint.setAlpha(R.styleable.ThemeTemplate_keyboardBackgroundHwrContainer);
        this.borderDrawable = shapeDrawable;
    }

    public boolean isDebugMode() {
        return this.isDebugMode;
    }

    protected void drawDebug(Canvas canvas) {
        if (isDebugMode()) {
            this.borderDrawable.setBounds(getLeft(), getTop(), getLeft() + getWidth(), getTop() + getHeight());
            this.borderDrawable.draw(canvas);
        }
    }

    protected void invalidateDebugRect() {
        if (isDebugMode()) {
            invalidate();
        }
    }

    protected boolean isDrawNeeded() {
        return isDebugMode();
    }

    protected final void enableOrDisableOnDraw() {
        boolean enableDraw = isDrawNeeded();
        setWillNotDraw(!enableDraw);
    }

    @Override // android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        enableOrDisableOnDraw();
    }

    public void layoutChild(View view) {
        LayoutParams params = (LayoutParams) view.getLayoutParams();
        int cx = view.getMeasuredWidth();
        int cy = view.getMeasuredHeight();
        view.layout(params.x, params.y, params.x + cx, params.y + cy);
    }

    @Override // android.view.ViewGroup, android.view.View
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int count = getChildCount();
        for (int idx = 0; idx < count; idx++) {
            View child = getChildAt(idx);
            if (child.getVisibility() != 8) {
                layoutChild(child);
            }
        }
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        int minWidth = getSuggestedMinimumWidth();
        int minHeight = getSuggestedMinimumHeight();
        setMeasuredDimension(ViewUtil.getDefaultSizePreferMin(minWidth, widthMeasureSpec), ViewUtil.getDefaultSizePreferMin(minHeight, heightMeasureSpec));
    }

    @Override // android.view.ViewGroup
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override // android.view.ViewGroup
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return newLayoutParams(0, 0);
    }

    @Override // android.view.ViewGroup
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override // android.view.View
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawDebug(canvas);
    }

    public static LayoutParams newLayoutParams(int x, int y) {
        return new LayoutParams(x, y, -2, -2);
    }

    public static LayoutParams newLayoutParams(int x, int y, int width, int height) {
        return new LayoutParams(x, y, width, height);
    }

    public static LayoutParams newLayoutParams(Rect rc) {
        return newLayoutParams(rc.left, rc.top, rc.width(), rc.height());
    }

    protected static void setLayoutPos(View view, int x, int y) {
        LayoutParams params = (LayoutParams) view.getLayoutParams();
        params.x = x;
        params.y = y;
    }

    public static void setPos(View view, int x, int y) {
        setLayoutPos(view, x, y);
        view.requestLayout();
    }

    public static void moveLayoutPos(View view, int dx, int dy) {
        LayoutParams params = (LayoutParams) view.getLayoutParams();
        params.x += dx;
        params.y += dy;
    }

    private void confineHelper(Rect rc, Rect insets) {
        Rect area = getDimsRect();
        if (insets != null) {
            GeomUtil.expand(area, insets);
        }
        GeomUtil.confine(rc, area);
    }

    protected Rect adjustConfine(Rect rc, Rect insets, int dx, int dy) {
        GeomUtil.moveRectBy(rc, dx, dy);
        confineHelper(rc, insets);
        return rc;
    }

    public void adjustConfine(int[] pos, int width, int height, Rect insets, int dx, int dy) {
        this.tempRect.left = pos[0] + dx;
        this.tempRect.top = pos[1] + dy;
        this.tempRect.right = this.tempRect.left + width;
        this.tempRect.bottom = this.tempRect.top + height;
        confineHelper(this.tempRect, insets);
        pos[0] = this.tempRect.left;
        pos[1] = this.tempRect.top;
    }

    protected Rect getConfined(View view, Rect insets, int dx, int dy) {
        return adjustConfine(GeomUtil.getRect(view), insets, dx, dy);
    }

    public void moveConfine(View view, Rect insets, int dx, int dy) {
        Rect rc = getConfined(view, insets, dx, dy);
        setPos(view, rc.left, rc.top);
    }

    protected void setLayoutPosConfine(View view, Rect insets, int x, int y) {
        Rect rc = GeomUtil.getRect(view);
        GeomUtil.moveRectTo(rc, x, y);
        confineHelper(rc, insets);
        setLayoutPos(view, rc.left, rc.top);
    }

    public void setLayoutPosConfineBot(View view, Rect insets, int x, int yBot) {
        Rect rc = GeomUtil.getRect(view);
        int y = yBot - rc.height();
        GeomUtil.moveRectTo(rc, x, y);
        confineHelper(rc, insets);
        setLayoutPos(view, rc.left, rc.top);
    }

    public static void setGeometry(View view, int x, int y, int width, int height) {
        LayoutParams params = (LayoutParams) view.getLayoutParams();
        if (params == null) {
            throw new IllegalStateException("View layout params must be set");
        }
        params.x = x;
        params.y = y;
        params.width = width;
        params.height = height;
        view.requestLayout();
    }

    public static void setGeometry(View view, int x, int y) {
        LayoutParams params = (LayoutParams) view.getLayoutParams();
        if (params == null) {
            throw new IllegalStateException("View layout params must be set");
        }
        params.x = x;
        params.y = y;
        view.requestLayout();
    }

    public static void setGeometry(View view, Rect rc) {
        setGeometry(view, rc.left, rc.top, rc.width(), rc.height());
    }

    public Rect getDimsRect() {
        return new Rect(0, 0, getWidth(), getHeight());
    }

    public static Rect getMeasuredRect(View view) {
        LayoutParams params = (LayoutParams) view.getLayoutParams();
        int cx = view.getMeasuredWidth();
        int cy = view.getMeasuredHeight();
        return new Rect(params.x, params.y, params.x + cx, params.y + cy);
    }

    public void setLayoutConfined(View view, Rect insets, int confinePadding) {
        Rect child = getMeasuredRect(view);
        Rect parent = getDimsRect();
        if (insets != null) {
            GeomUtil.expand(parent, insets);
        }
        if (GeomUtil.confine(child, parent, confinePadding)) {
            log.d("setLayoutConfined(): confining: " + child.toShortString() + " within " + parent.toShortString());
            setLayoutPos(view, child.left, child.top);
        }
    }

    public void setLayoutDocked(View view, int dockGravity) {
        int cx = view.getMeasuredWidth();
        int cy = view.getMeasuredHeight();
        int x = getGravityBasedX(cx, getMeasuredWidth(), dockGravity);
        int y = getGravityBasedY(cy, getMeasuredHeight(), dockGravity);
        setLayoutPos(view, x, y);
    }

    public static int getGravityBasedX(int cx, int width, int grav) {
        switch (grav & 7) {
            case 8388611:
                return 0;
            case 8388612:
            default:
                int x = (width - cx) / 2;
                return x;
            case 8388613:
                int x2 = width - cx;
                return x2;
        }
    }

    public static int getGravityBasedY(int cy, int height, int grav) {
        switch (grav & 112) {
            case 48:
                return 0;
            case 80:
                int y = height - cy;
                return y;
            default:
                int y2 = (height - cy) / 2;
                return y2;
        }
    }

    /* loaded from: classes.dex */
    public static class LayoutParams extends ViewGroup.LayoutParams {
        public int x;
        public int y;

        public LayoutParams(int x, int y, int width, int height) {
            super(width, height);
            this.x = x;
            this.y = y;
        }

        public LayoutParams(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public String toString() {
            return "OverlayView.LayoutParams={width=" + sizeToString(this.width) + ", height=" + sizeToString(this.height) + " x=" + this.x + " y=" + this.y + "}";
        }

        private static String sizeToString(int size) {
            if (size == -2) {
                return "wrap-content";
            }
            if (size == -1) {
                return "match-parent";
            }
            return String.valueOf(size);
        }
    }
}
