package com.nuance.swype.input.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import com.nuance.swype.input.R;

/* loaded from: classes.dex */
public abstract class DragFrame extends LinearLayout {
    private FrameLayout contentFrame;
    private View contentView;
    private DragFrameListener dragListener;
    private GripPad gripPad;

    /* loaded from: classes.dex */
    public interface DragFrameListener {
        void onClick(View view);

        void onDrag(View view, int i, int i2);

        void onDragBegin(View view);

        void onDragEnd(View view);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void notifyClick() {
        if (this.dragListener != null) {
            this.dragListener.onClick(this.contentView);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void notifyDragBegin() {
        if (this.dragListener != null) {
            this.dragListener.onDragBegin(this.contentView);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void notifyDrag(int dx, int dy) {
        if (this.dragListener != null) {
            this.dragListener.onDrag(this.contentView, dx, dy);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void notifyDragEnd() {
        if (this.dragListener != null) {
            this.dragListener.onDragEnd(this.contentView);
        }
    }

    public DragFrame(Context context) {
        super(context);
    }

    public DragFrame(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setDragListener(DragFrameListener dragListener) {
        this.dragListener = dragListener;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onFinishInflate() {
        super.onFinishInflate();
        this.contentFrame = (FrameLayout) findViewById(R.id.content_frame);
        this.contentView = findViewById(R.id.content_area);
        this.gripPad = (GripPad) findViewById(R.id.grip_pad);
    }

    private FrameLayout.LayoutParams newFrameLayoutParams() {
        return new FrameLayout.LayoutParams(-2, -2);
    }

    public void setContentView(InputLayout view) {
        if (this.contentFrame == null) {
            throw new UnsupportedOperationException("Layout has no frame");
        }
        this.contentView = view;
        this.contentFrame.removeAllViews();
        if (view != null) {
            this.contentFrame.addView(view, newFrameLayoutParams());
            this.contentFrame.setVisibility(0);
        } else {
            this.contentFrame.setVisibility(8);
        }
    }

    public void showDecoration(boolean show) {
        throw new UnsupportedOperationException("not implemented");
    }

    public int getGripHeight() {
        if (this.gripPad == null || !this.gripPad.isShown()) {
            return 0;
        }
        return this.gripPad.getHeight();
    }
}
