package com.nuance.swype.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;
import com.nuance.swype.util.LogManager;

/* loaded from: classes.dex */
public class CustomHorizontalScrollView extends HorizontalScrollView {
    private static final LogManager.Log log = LogManager.getLog("CustomHorizontalScrollView");
    private ScrollListener scrollListener;

    /* loaded from: classes.dex */
    public interface ScrollListener {
        void onScrollChanged(int i);
    }

    public CustomHorizontalScrollView(Context context) {
        super(context, null);
    }

    public CustomHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomHorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public CustomHorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setScrollListener(ScrollListener listener) {
        this.scrollListener = listener;
    }

    @Override // android.view.View
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        log.d("Scrolling", "X from [", Integer.valueOf(oldl), "] to [", Integer.valueOf(l), "]");
        if (this.scrollListener != null) {
            this.scrollListener.onScrollChanged(l);
        }
        super.onScrollChanged(l, t, oldl, oldt);
    }
}
