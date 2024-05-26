package com.nuance.swype.widget.directional;

import android.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.TextView;
import com.nuance.swype.widget.directional.DirectionalUtil;

/* loaded from: classes.dex */
public class DirectionalTextView extends TextView implements DirectionalUtil.OnCreateDrawableStateCallback {
    private Drawable drawableEnd;
    private Drawable drawableStart;
    private int[] drawable_attr;
    private boolean showingRtl;
    private static int drawable_start = R.attr.drawableStart;
    private static int drawable_end = R.attr.drawableEnd;

    public DirectionalTextView(Context context) {
        super(context);
        this.drawable_attr = new int[]{drawable_start, drawable_end};
    }

    public DirectionalTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.drawable_attr = new int[]{drawable_start, drawable_end};
        directionalInit(context, attrs, R.attr.textViewStyle);
    }

    public DirectionalTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.drawable_attr = new int[]{drawable_start, drawable_end};
        directionalInit(context, attrs, defStyle);
    }

    @Override // android.widget.TextView, android.view.View
    public int[] onCreateDrawableState(int extraSpace) {
        return DirectionalUtil.onCreateDrawableState(this, extraSpace, this.showingRtl);
    }

    @Override // android.widget.TextView, android.view.View
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        boolean isCurrentlyRtl = DirectionalUtil.isCurrentlyRtl();
        if (isCurrentlyRtl == this.showingRtl) {
            return;
        }
        this.showingRtl = isCurrentlyRtl;
        refreshDrawableState();
    }

    private void directionalInit(Context context, AttributeSet attrs, int defStyle) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, this.drawable_attr, defStyle, 0);
        this.drawableStart = a.getDrawable(0);
        if (this.drawableStart != null) {
            this.drawableStart.setBounds(0, 0, this.drawableStart.getIntrinsicWidth(), this.drawableStart.getIntrinsicHeight());
        }
        this.drawableEnd = a.getDrawable(1);
        if (this.drawableEnd != null) {
            this.drawableEnd.setBounds(0, 0, this.drawableEnd.getIntrinsicWidth(), this.drawableEnd.getIntrinsicHeight());
        }
        a.recycle();
    }

    @Override // com.nuance.swype.widget.directional.DirectionalUtil.OnCreateDrawableStateCallback
    public final int[] baseCreateDrawableState(int extraSpace) {
        return super.onCreateDrawableState(extraSpace);
    }
}
