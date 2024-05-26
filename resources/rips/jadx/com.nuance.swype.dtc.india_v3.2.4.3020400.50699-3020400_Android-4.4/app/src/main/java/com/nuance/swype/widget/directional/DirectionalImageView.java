package com.nuance.swype.widget.directional;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.nuance.swype.widget.directional.DirectionalUtil;

/* loaded from: classes.dex */
public class DirectionalImageView extends ImageView implements DirectionalUtil.OnCreateDrawableStateCallback {
    public DirectionalImageView(Context context) {
        super(context);
    }

    public DirectionalImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DirectionalImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override // android.view.View
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        refreshDrawableState();
    }

    @Override // android.widget.ImageView, android.view.View
    public int[] onCreateDrawableState(int extraSpace) {
        return DirectionalUtil.onCreateDrawableState(this, extraSpace);
    }

    @Override // com.nuance.swype.widget.directional.DirectionalUtil.OnCreateDrawableStateCallback
    public final int[] baseCreateDrawableState(int extraSpace) {
        return super.onCreateDrawableState(extraSpace);
    }
}
