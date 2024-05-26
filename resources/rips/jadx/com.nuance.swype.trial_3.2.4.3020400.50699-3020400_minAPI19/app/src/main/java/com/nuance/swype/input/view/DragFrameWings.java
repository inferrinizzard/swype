package com.nuance.swype.input.view;

import android.content.Context;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import com.nuance.swype.input.DragGesture;
import com.nuance.swype.input.R;
import com.nuance.swype.util.LogManager;

/* loaded from: classes.dex */
public class DragFrameWings extends DragFrame {
    private static final LogManager.Log log = LogManager.getLog("DragFrameWings");
    private View activeGrip;
    private ImageView leftGrip;
    private ImageView rightGrip;

    public DragFrameWings(Context context) {
        super(context);
    }

    public DragFrameWings(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.view.DragFrame, android.view.View
    public void onFinishInflate() {
        super.onFinishInflate();
        DragGestureListener dragGestureListener = new DragGestureListener();
        this.leftGrip = (ImageView) findViewById(R.id.left_wing);
        fixBackgroundRepeat(this.leftGrip);
        this.leftGrip.setOnTouchListener(new DragGesture(dragGestureListener));
        this.rightGrip = (ImageView) findViewById(R.id.right_wing);
        fixBackgroundRepeat(this.rightGrip);
        this.rightGrip.setOnTouchListener(new DragGesture(dragGestureListener));
    }

    @Override // com.nuance.swype.input.view.DragFrame
    public void showDecoration(boolean show) {
        this.leftGrip.setVisibility(show ? 0 : 8);
        this.rightGrip.setVisibility(show ? 0 : 8);
    }

    private static void fixBackgroundRepeat(View view) {
        Drawable bg = view.getBackground();
        if (bg != null && (bg instanceof BitmapDrawable)) {
            BitmapDrawable bmp = (BitmapDrawable) bg;
            bmp.mutate();
            bmp.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        }
    }

    /* loaded from: classes.dex */
    private class DragGestureListener implements DragGesture.IDragGestureListener {
        private DragGestureListener() {
        }

        @Override // com.nuance.swype.input.DragGesture.IDragGestureListener
        public void onBeginDrag(View view) {
            if (DragFrameWings.this.activeGrip == view) {
                DragFrameWings.log.d("onBeginDrag(): unexpected (drag is active)");
                onEndDrag(view);
            } else if (DragFrameWings.this.activeGrip == null) {
                DragFrameWings.this.activeGrip = view;
                DragFrameWings.this.notifyDragBegin();
            } else {
                DragFrameWings.log.d("onBeginDrag(): ignore event (already dragging)");
            }
        }

        @Override // com.nuance.swype.input.DragGesture.IDragGestureListener
        public void onDrag(View view, int dx, int dy) {
            if (DragFrameWings.this.activeGrip == view) {
                DragFrameWings.this.notifyDrag(dx, dy);
            }
        }

        @Override // com.nuance.swype.input.DragGesture.IDragGestureListener
        public void onEndDrag(View view) {
            if (DragFrameWings.this.activeGrip == view) {
                DragFrameWings.this.notifyDragEnd();
                DragFrameWings.this.activeGrip = null;
            }
        }
    }
}
