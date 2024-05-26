package com.nuance.swype.widget;

import android.R;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.ImageButton;

/* loaded from: classes.dex */
public class ImageCompoundButton extends ImageButton implements Checkable {
    private static final int[] CHECKED_STATE_SET = {R.attr.state_checked};
    private boolean checked;
    private OnCheckedChangeListener onCheckedChangeListener;
    private OnClickListener onClickListener;

    /* loaded from: classes.dex */
    public interface OnCheckedChangeListener {
    }

    /* loaded from: classes.dex */
    public interface OnClickListener {
        void onClicked(ImageCompoundButton imageCompoundButton, boolean z);
    }

    public ImageCompoundButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ImageCompoundButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageCompoundButton(Context context) {
        super(context);
    }

    @Override // android.widget.Checkable
    public boolean isChecked() {
        return this.checked;
    }

    @Override // android.widget.Checkable
    public void setChecked(boolean checked) {
        if (this.checked != checked) {
            this.checked = checked;
            refreshDrawableState();
            if (this.onCheckedChangeListener != null) {
                isChecked();
            }
        }
    }

    @Override // android.widget.Checkable
    public void toggle() {
        setChecked(!isChecked());
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        this.onCheckedChangeListener = listener;
    }

    public void setOnClickListener(OnClickListener listener) {
        this.onClickListener = listener;
    }

    @Override // android.widget.ImageView, android.view.View
    public int[] onCreateDrawableState(int extraSpace) {
        int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked()) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }
        return drawableState;
    }

    @Override // android.view.View
    public boolean performClick() {
        toggle();
        if (this.onClickListener != null) {
            playSoundEffect(0);
            this.onClickListener.onClicked(this, isChecked());
        }
        return super.performClick();
    }
}
