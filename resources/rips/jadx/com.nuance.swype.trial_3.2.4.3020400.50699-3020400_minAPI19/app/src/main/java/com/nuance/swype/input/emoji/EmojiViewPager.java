package com.nuance.swype.input.emoji;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/* loaded from: classes.dex */
public class EmojiViewPager extends ViewPager {
    private boolean allowScroll;
    private Handler handler;
    private PagerSizeChangeListener listener;

    /* loaded from: classes.dex */
    public interface PagerSizeChangeListener {
        void onPagerSizeChanged();
    }

    public EmojiViewPager(Context context) {
        super(context);
        this.allowScroll = true;
        this.handler = new Handler();
    }

    public EmojiViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.allowScroll = true;
        this.handler = new Handler();
    }

    public void setAllowScroll(boolean allowScroll) {
        this.allowScroll = allowScroll;
    }

    public void setPagerSizeChangeListener(PagerSizeChangeListener listener) {
        this.listener = listener;
    }

    @Override // android.support.v4.view.ViewPager, android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return this.allowScroll && super.onInterceptTouchEvent(event);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v4.view.ViewPager, android.view.View
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (this.listener != null) {
            this.handler.post(new Runnable() { // from class: com.nuance.swype.input.emoji.EmojiViewPager.1
                @Override // java.lang.Runnable
                public void run() {
                    EmojiViewPager.this.listener.onPagerSizeChanged();
                }
            });
        }
    }
}
