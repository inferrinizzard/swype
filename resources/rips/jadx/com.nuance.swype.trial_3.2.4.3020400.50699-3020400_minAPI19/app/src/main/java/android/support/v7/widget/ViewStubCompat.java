package android.support.v7.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.support.v7.appcompat.R;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import java.lang.ref.WeakReference;

/* loaded from: classes.dex */
public final class ViewStubCompat extends View {
    private OnInflateListener mInflateListener;
    private int mInflatedId;
    private WeakReference<View> mInflatedViewRef;
    private LayoutInflater mInflater;
    private int mLayoutResource;

    /* loaded from: classes.dex */
    public interface OnInflateListener {
    }

    public ViewStubCompat(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ViewStubCompat(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mLayoutResource = 0;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ViewStubCompat, defStyle, 0);
        this.mInflatedId = a.getResourceId(R.styleable.ViewStubCompat_android_inflatedId, -1);
        this.mLayoutResource = a.getResourceId(R.styleable.ViewStubCompat_android_layout, 0);
        setId(a.getResourceId(R.styleable.ViewStubCompat_android_id, -1));
        a.recycle();
        setVisibility(8);
        setWillNotDraw(true);
    }

    public final int getInflatedId() {
        return this.mInflatedId;
    }

    public final void setInflatedId(int inflatedId) {
        this.mInflatedId = inflatedId;
    }

    public final int getLayoutResource() {
        return this.mLayoutResource;
    }

    public final void setLayoutResource(int layoutResource) {
        this.mLayoutResource = layoutResource;
    }

    public final void setLayoutInflater(LayoutInflater inflater) {
        this.mInflater = inflater;
    }

    public final LayoutInflater getLayoutInflater() {
        return this.mInflater;
    }

    @Override // android.view.View
    protected final void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(0, 0);
    }

    @Override // android.view.View
    public final void draw(Canvas canvas) {
    }

    @Override // android.view.View
    protected final void dispatchDraw(Canvas canvas) {
    }

    @Override // android.view.View
    public final void setVisibility(int visibility) {
        LayoutInflater from;
        if (this.mInflatedViewRef != null) {
            View view = this.mInflatedViewRef.get();
            if (view != null) {
                view.setVisibility(visibility);
                return;
            }
            throw new IllegalStateException("setVisibility called on un-referenced view");
        }
        super.setVisibility(visibility);
        if (visibility == 0 || visibility == 4) {
            ViewParent parent = getParent();
            if (parent != null && (parent instanceof ViewGroup)) {
                if (this.mLayoutResource != 0) {
                    ViewGroup viewGroup = (ViewGroup) parent;
                    if (this.mInflater != null) {
                        from = this.mInflater;
                    } else {
                        from = LayoutInflater.from(getContext());
                    }
                    View inflate = from.inflate(this.mLayoutResource, viewGroup, false);
                    if (this.mInflatedId != -1) {
                        inflate.setId(this.mInflatedId);
                    }
                    int indexOfChild = viewGroup.indexOfChild(this);
                    viewGroup.removeViewInLayout(this);
                    ViewGroup.LayoutParams layoutParams = getLayoutParams();
                    if (layoutParams != null) {
                        viewGroup.addView(inflate, indexOfChild, layoutParams);
                    } else {
                        viewGroup.addView(inflate, indexOfChild);
                    }
                    this.mInflatedViewRef = new WeakReference<>(inflate);
                    return;
                }
                throw new IllegalArgumentException("ViewStub must have a valid layoutResource");
            }
            throw new IllegalStateException("ViewStub must have a non-null ViewGroup viewParent");
        }
    }

    public final void setOnInflateListener(OnInflateListener inflateListener) {
        this.mInflateListener = inflateListener;
    }
}
