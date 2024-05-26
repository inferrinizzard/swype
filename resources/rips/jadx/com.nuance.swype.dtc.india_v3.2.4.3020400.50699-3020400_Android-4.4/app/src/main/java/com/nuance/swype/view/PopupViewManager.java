package com.nuance.swype.view;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Context;
import android.content.res.TypedArray;
import android.view.View;
import com.nuance.swype.input.R;
import com.nuance.swype.util.LogManager;
import com.nuance.swype.view.ShowHideAnimManager;
import java.util.HashSet;
import java.util.Set;

/* loaded from: classes.dex */
public final class PopupViewManager implements ShowHideAnimManager.Listener {
    protected static final LogManager.Log log = LogManager.getLog("PopupViewManager");
    private final Set<View> activeViews = new HashSet();
    private ShowHideAnimManager animManager;
    private final OverlayView overlayView;

    public PopupViewManager(Context context, OverlayView overlay) {
        this.overlayView = overlay;
        TypedArray props = context.obtainStyledAttributes(R.style.PopupAnimationStyle, R.styleable.PopupAnimation);
        if (props == null) {
            return;
        }
        this.animManager = new ShowHideAnimManager(loadAnim(context, props, R.styleable.PopupAnimation_popupShowAnimation), loadAnim(context, props, R.styleable.PopupAnimation_popupHideAnimation));
        this.animManager.listener = this;
        props.recycle();
    }

    private static Animator loadAnim(Context context, TypedArray attr, int idx) {
        int id = attr.getResourceId(idx, 0);
        if (id <= 0) {
            return null;
        }
        Animator out = AnimatorInflater.loadAnimator(context, id);
        return out;
    }

    @Override // com.nuance.swype.view.ShowHideAnimManager.Listener
    public final void onHideComplete(View view) {
        this.overlayView.removeView(view);
        this.activeViews.remove(view);
    }
}
