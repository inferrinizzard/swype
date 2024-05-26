package com.nuance.swype.input.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import com.nuance.swype.input.R;
import com.nuance.swype.input.view.GripPad;

/* loaded from: classes.dex */
public class DragFrameGripPad extends DragFrame implements GripPad.GripButtonListener {
    private boolean dragging;
    private GripPad gripPad;

    public DragFrameGripPad(Context context) {
        super(context);
        this.dragging = false;
    }

    public DragFrameGripPad(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.dragging = false;
    }

    public void setGripIsDrag(boolean isDrag, boolean enable) {
        this.gripPad.setGripIsDrag(isDrag, enable);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.view.DragFrame, android.view.View
    public void onFinishInflate() {
        super.onFinishInflate();
        this.gripPad = (GripPad) findViewById(R.id.grip_pad);
        this.gripPad.setDragListener(this);
    }

    @Override // com.nuance.swype.input.view.DragFrame
    public void showDecoration(boolean show) {
        this.gripPad.setVisibility(show ? 0 : 8);
    }

    @Override // com.nuance.swype.input.view.GripPad.GripButtonListener
    public void onDragBegin(View grip) {
        if (!this.dragging) {
            this.dragging = true;
            notifyDragBegin();
        }
    }

    @Override // com.nuance.swype.input.view.GripPad.GripButtonListener
    public void onDrag(View grip, int dx, int dy) {
        if (this.dragging) {
            notifyDrag(dx, dy);
        }
    }

    @Override // com.nuance.swype.input.view.GripPad.GripButtonListener
    public void onDragEnd(View grip) {
        if (this.dragging) {
            notifyDragEnd();
        }
        this.dragging = false;
    }

    @Override // com.nuance.swype.input.view.GripPad.GripButtonListener
    public void onClick(View grip) {
        notifyClick();
    }

    @Override // com.nuance.swype.input.view.DragFrame
    public int getGripHeight() {
        return 0;
    }
}
