package android.support.v7.widget;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.appcompat.R;
import android.util.AttributeSet;
import android.widget.ImageView;

/* loaded from: classes.dex */
public final class AppCompatImageHelper {
    private final AppCompatDrawableManager mDrawableManager;
    private final ImageView mView;

    public AppCompatImageHelper(ImageView view, AppCompatDrawableManager drawableManager) {
        this.mView = view;
        this.mDrawableManager = drawableManager;
    }

    public final void loadFromAttributes(AttributeSet attrs, int defStyleAttr) {
        int id;
        TintTypedArray a = null;
        try {
            Drawable drawable = this.mView.getDrawable();
            if (drawable == null && (id = (a = TintTypedArray.obtainStyledAttributes(this.mView.getContext(), attrs, R.styleable.AppCompatImageView, defStyleAttr, 0)).getResourceId(R.styleable.AppCompatImageView_srcCompat, -1)) != -1 && (drawable = this.mDrawableManager.getDrawable(this.mView.getContext(), id, false)) != null) {
                this.mView.setImageDrawable(drawable);
            }
            if (drawable != null) {
                DrawableUtils.fixDrawable(drawable);
            }
        } finally {
            if (a != null) {
                a.mWrapped.recycle();
            }
        }
    }

    public final void setImageResource(int resId) {
        Drawable d;
        if (resId != 0) {
            if (this.mDrawableManager == null) {
                d = ContextCompat.getDrawable(this.mView.getContext(), resId);
            } else {
                d = this.mDrawableManager.getDrawable(this.mView.getContext(), resId, false);
            }
            if (d != null) {
                DrawableUtils.fixDrawable(d);
            }
            this.mView.setImageDrawable(d);
            return;
        }
        this.mView.setImageDrawable(null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean hasOverlappingRendering() {
        Drawable background = this.mView.getBackground();
        return Build.VERSION.SDK_INT < 21 || !(background instanceof RippleDrawable);
    }
}
