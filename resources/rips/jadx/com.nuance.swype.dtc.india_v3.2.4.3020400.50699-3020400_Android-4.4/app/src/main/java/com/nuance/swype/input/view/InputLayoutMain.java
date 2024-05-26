package com.nuance.swype.input.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import com.nuance.swype.input.R;
import com.nuance.swype.input.view.InputLayout;
import com.nuance.swype.util.LogManager;

/* loaded from: classes.dex */
public class InputLayoutMain extends FrameLayout implements InputLayout.DragListener {
    protected static final LogManager.Log log = LogManager.getLog("InputLayoutMain");
    private View contentArea;
    private View coverFrame;
    private boolean isDragging;

    public InputLayoutMain(Context context) {
        super(context);
    }

    public InputLayoutMain(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override // com.nuance.swype.input.view.InputLayout.DragListener
    public void onBeginDrag() {
        this.isDragging = true;
    }

    @Override // com.nuance.swype.input.view.InputLayout.DragListener
    public void onDrag(int dx, int dy) {
    }

    @Override // com.nuance.swype.input.view.InputLayout.DragListener
    public void onEndDrag() {
        this.isDragging = false;
    }

    @Override // com.nuance.swype.input.view.InputLayout.DragListener
    public void onSnapToEdge(int dx, int dy) {
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return this.isDragging;
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.contentArea = findViewById(R.id.content_area);
        this.coverFrame = (FrameLayout) findViewById(R.id.coverview_container);
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChild(this.contentArea, widthMeasureSpec, heightMeasureSpec);
        FrameLayout.LayoutParams coverFrameParams = (FrameLayout.LayoutParams) this.coverFrame.getLayoutParams();
        coverFrameParams.height = this.contentArea.getMeasuredHeight();
        coverFrameParams.width = this.contentArea.getMeasuredWidth();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
