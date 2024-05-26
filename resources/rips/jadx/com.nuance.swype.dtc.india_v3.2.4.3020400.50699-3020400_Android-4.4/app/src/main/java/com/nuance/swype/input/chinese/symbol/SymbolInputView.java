package com.nuance.swype.input.chinese.symbol;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import com.nuance.swype.input.view.DragHelper;
import com.nuance.swype.util.LogManager;

/* loaded from: classes.dex */
public class SymbolInputView extends LinearLayout implements DragHelper.DragVisualizer {
    private static final LogManager.Log log = LogManager.getLog("SymbolInputView");
    private DragHelper dragHelper;

    public SymbolInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    @Override // com.nuance.swype.input.view.DragHelper.DragVisualizer
    public void setDragHelper(DragHelper dragHelper) {
        this.dragHelper = dragHelper;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        if (this.dragHelper != null && this.dragHelper.shouldUseDragPaint(this)) {
            canvas.saveLayer(null, this.dragHelper.getDragPaint(), 16);
            super.dispatchDraw(canvas);
            canvas.restore();
            return;
        }
        super.dispatchDraw(canvas);
    }

    @Override // android.widget.LinearLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        log.d("onLayout(): (" + left + "," + top + "); " + (right - left) + "x" + (bottom - top));
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override // android.widget.LinearLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
