package android.support.v7.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.widget.PopupWindowCompat;
import android.support.v7.appcompat.R;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.PopupWindow;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

/* loaded from: classes.dex */
class AppCompatPopupWindow extends PopupWindow {
    private static final boolean COMPAT_OVERLAP_ANCHOR;
    private boolean mOverlapAnchor;

    static {
        COMPAT_OVERLAP_ANCHOR = Build.VERSION.SDK_INT < 21;
    }

    public AppCompatPopupWindow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(11)
    public AppCompatPopupWindow(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TintTypedArray a = TintTypedArray.obtainStyledAttributes(context, attrs, R.styleable.PopupWindow, defStyleAttr, defStyleRes);
        if (a.hasValue(R.styleable.PopupWindow_overlapAnchor)) {
            boolean z = a.getBoolean(R.styleable.PopupWindow_overlapAnchor, false);
            if (COMPAT_OVERLAP_ANCHOR) {
                this.mOverlapAnchor = z;
            } else {
                PopupWindowCompat.setOverlapAnchor(this, z);
            }
        }
        setBackgroundDrawable(a.getDrawable(R.styleable.PopupWindow_android_popupBackground));
        int sdk = Build.VERSION.SDK_INT;
        if (defStyleRes != 0 && sdk < 11 && sdk >= 9 && a.hasValue(R.styleable.PopupWindow_android_popupAnimationStyle)) {
            setAnimationStyle(a.getResourceId(R.styleable.PopupWindow_android_popupAnimationStyle, -1));
        }
        a.mWrapped.recycle();
        if (Build.VERSION.SDK_INT < 14) {
            try {
                final Field declaredField = PopupWindow.class.getDeclaredField("mAnchor");
                declaredField.setAccessible(true);
                Field declaredField2 = PopupWindow.class.getDeclaredField("mOnScrollChangedListener");
                declaredField2.setAccessible(true);
                final ViewTreeObserver.OnScrollChangedListener onScrollChangedListener = (ViewTreeObserver.OnScrollChangedListener) declaredField2.get(this);
                declaredField2.set(this, new ViewTreeObserver.OnScrollChangedListener() { // from class: android.support.v7.widget.AppCompatPopupWindow.1
                    @Override // android.view.ViewTreeObserver.OnScrollChangedListener
                    public final void onScrollChanged() {
                        try {
                            WeakReference<View> mAnchor = (WeakReference) declaredField.get(this);
                            if (mAnchor != null && mAnchor.get() != null) {
                                onScrollChangedListener.onScrollChanged();
                            }
                        } catch (IllegalAccessException e) {
                        }
                    }
                });
            } catch (Exception e) {
            }
        }
    }

    @Override // android.widget.PopupWindow
    public void showAsDropDown(View anchor, int xoff, int yoff) {
        if (COMPAT_OVERLAP_ANCHOR && this.mOverlapAnchor) {
            yoff -= anchor.getHeight();
        }
        super.showAsDropDown(anchor, xoff, yoff);
    }

    @Override // android.widget.PopupWindow
    @TargetApi(19)
    public void showAsDropDown(View anchor, int xoff, int yoff, int gravity) {
        if (COMPAT_OVERLAP_ANCHOR && this.mOverlapAnchor) {
            yoff -= anchor.getHeight();
        }
        super.showAsDropDown(anchor, xoff, yoff, gravity);
    }

    @Override // android.widget.PopupWindow
    public void update(View anchor, int xoff, int yoff, int width, int height) {
        if (COMPAT_OVERLAP_ANCHOR && this.mOverlapAnchor) {
            yoff -= anchor.getHeight();
        }
        super.update(anchor, xoff, yoff, width, height);
    }
}
