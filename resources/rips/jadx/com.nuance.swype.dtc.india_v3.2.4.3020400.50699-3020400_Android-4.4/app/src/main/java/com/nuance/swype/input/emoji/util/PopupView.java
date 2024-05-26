package com.nuance.swype.input.emoji.util;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import com.nuance.android.compat.ViewCompat;
import com.nuance.swype.util.CoordUtils;
import com.nuance.swype.view.OverlayView;

/* loaded from: classes.dex */
public abstract class PopupView extends View {
    private final int[] anchorPos;
    private PopupManager popupManager;

    /* loaded from: classes.dex */
    public enum Mode {
        MODE_ENABLE_HW_LAYER,
        MODE_DISABLE_HW_ACCEL,
        MODE_DEFAULT
    }

    public PopupView(Context context, PopupManager popupManager, Mode mode) {
        super(context);
        this.anchorPos = new int[2];
        this.popupManager = popupManager;
        switch (mode) {
            case MODE_DISABLE_HW_ACCEL:
                ViewCompat.disableHardwareAccel(this);
                return;
            case MODE_ENABLE_HW_LAYER:
                ViewCompat.enableHardwareLayer(this);
                return;
            default:
                return;
        }
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    private void setGeometry(int[] pos, int width, int height) {
        OverlayView.setGeometry(this, pos[0], pos[1], width, height);
    }

    public void setAnchor(int left, int top) {
        int[] pos = this.popupManager.mapContentToOverlay(left, top);
        int[] iArr = this.anchorPos;
        iArr[0] = pos[0];
        iArr[1] = pos[1];
    }

    public boolean show(int left, int top, int width, int height) {
        int[] pos = this.popupManager.mapContentToOverlay(left, top);
        pos[0] = pos[0] - getPaddingLeft();
        pos[1] = pos[1] - getPaddingTop();
        int width2 = width + getPaddingLeft() + getPaddingRight();
        int height2 = height + getPaddingTop() + getPaddingBottom();
        boolean added = this.popupManager.addPopup(this);
        setGeometry(pos, width2, height2);
        return added;
    }

    public void hide() {
        this.popupManager.removePopup(this);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onAdded() {
    }

    public void onRemoved() {
    }

    @Override // android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.popupManager != null) {
            this.popupManager.onDetachedFromWindow(this);
        }
    }

    public void mapRelativeToAnchorPos(int[] pos) {
        CoordUtils.subtract(pos, this.anchorPos, pos);
    }
}
