package com.nuance.swype.input.view;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import com.nuance.swype.input.DragGesture;
import com.nuance.swype.input.R;
import com.nuance.swype.util.LogManager;
import com.nuance.swype.view.ShowHideAnimManager;

/* loaded from: classes.dex */
public class GripPad extends LinearLayout {
    protected static final LogManager.Log log = LogManager.getLog("GripPad");
    private ShowHideAnimManager animManager;
    private View grip;
    private GripButtonListener listener;
    private View minus;
    private View plus;

    /* loaded from: classes.dex */
    public interface GripButtonListener {
        void onClick(View view);

        void onDrag(View view, int i, int i2);

        void onDragBegin(View view);

        void onDragEnd(View view);
    }

    public GripPad(Context context) {
        super(context);
    }

    public GripPad(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setGripIsDrag(boolean isDrag, boolean enable) {
        if (this.grip != null) {
            if (isDrag) {
                this.grip.setOnClickListener(null);
                this.grip.setClickable(false);
                if (enable) {
                    this.grip.setOnTouchListener(new DragGesture(new DragGestureListener()));
                    return;
                } else {
                    this.grip.setOnTouchListener(null);
                    return;
                }
            }
            this.grip.setOnTouchListener(null);
            if (enable) {
                this.grip.setOnClickListener(new View.OnClickListener() { // from class: com.nuance.swype.input.view.GripPad.1
                    @Override // android.view.View.OnClickListener
                    public void onClick(View v) {
                        if (GripPad.this.listener != null) {
                            GripPad.this.listener.onClick(GripPad.this.grip);
                        }
                    }
                });
            } else {
                this.grip.setOnClickListener(null);
                this.grip.setClickable(false);
            }
        }
    }

    @Override // android.view.View
    @SuppressLint({"InlinedApi"})
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.grip = findViewById(R.id.grip);
        this.minus = findViewById(R.id.minus);
        this.plus = findViewById(R.id.plus);
        Animator animShow = AnimatorInflater.loadAnimator(getContext(), android.R.animator.fade_in);
        Animator animHide = AnimatorInflater.loadAnimator(getContext(), android.R.animator.fade_out);
        this.animManager = new ShowHideAnimManager(animShow, animHide);
        this.animManager.listener = ShowHideAnimManager.createDefaultListener$378274fe();
        showSizeButtonsNoAnim(false);
        setGripIsDrag(true, true);
    }

    private void showButton(View button, boolean show) {
        if (show) {
            if (button.getVisibility() != 0) {
                button.setVisibility(0);
                if (this.animManager == null) {
                    return;
                }
                this.animManager.show(button, false);
                return;
            }
            return;
        }
        if (button.getVisibility() != 8) {
            if (this.animManager == null) {
                button.setVisibility(8);
            } else {
                this.animManager.hide(button, false);
            }
        }
    }

    public void showSizeButtons(boolean show) {
        showButton(this.minus, show);
        showButton(this.plus, show);
    }

    public void showSizeButtonsNoAnim(boolean show) {
        this.minus.setVisibility(show ? 0 : 8);
        this.plus.setVisibility(show ? 0 : 8);
    }

    public void setDragListener(GripButtonListener listener) {
        this.listener = listener;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class DragGestureListener implements DragGesture.IDragGestureListener {
        private DragGestureListener() {
        }

        @Override // com.nuance.swype.input.DragGesture.IDragGestureListener
        public void onBeginDrag(View view) {
            if (GripPad.this.listener != null) {
                GripPad.this.listener.onDragBegin(GripPad.this);
            }
        }

        @Override // com.nuance.swype.input.DragGesture.IDragGestureListener
        public void onDrag(View view, int dx, int dy) {
            if (GripPad.this.listener != null) {
                GripPad.this.listener.onDrag(GripPad.this, dx, dy);
            }
        }

        @Override // com.nuance.swype.input.DragGesture.IDragGestureListener
        public void onEndDrag(View view) {
            if (GripPad.this.listener != null) {
                GripPad.this.listener.onDragEnd(GripPad.this);
            }
        }
    }
}
