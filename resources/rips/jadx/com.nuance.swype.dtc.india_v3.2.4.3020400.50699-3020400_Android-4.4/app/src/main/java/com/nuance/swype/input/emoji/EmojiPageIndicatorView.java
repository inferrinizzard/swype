package com.nuance.swype.input.emoji;

import android.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import com.nuance.swype.util.DrawDebug;
import com.nuance.swype.util.GeomUtil;
import com.nuance.swype.util.ViewUtil;

/* loaded from: classes.dex */
public class EmojiPageIndicatorView extends View {
    private static final float DEFAULT_HEIGHT = 10.0f;
    private static final float DEFAULT_SPACING = 10.0f;
    private static final float DEFAULT_WIDTH = 10.0f;
    int[] activeState;
    private Drawable dotDrawable;
    private float dotHeight;
    private float dotWidth;
    private DrawDebug drawDebug;
    private float horSpacing;
    int[] inactiveState;
    private int pageCount;
    private int pageIndex;

    public EmojiPageIndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.activeState = new int[]{R.attr.state_active};
        this.inactiveState = new int[0];
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, com.nuance.swype.input.R.styleable.EmojiPageIndicatorStyle, 0, 0);
        int emojiPageIndicatorResId = a.getResourceId(com.nuance.swype.input.R.styleable.EmojiPageIndicatorStyle_pageIndicatorSrc, 0);
        this.dotWidth = a.getDimension(com.nuance.swype.input.R.styleable.EmojiPageIndicatorStyle_pageIndicatorWidth, 10.0f);
        this.dotHeight = a.getDimension(com.nuance.swype.input.R.styleable.EmojiPageIndicatorStyle_pageIndicatorHeight, 10.0f);
        this.horSpacing = a.getDimension(com.nuance.swype.input.R.styleable.EmojiPageIndicatorStyle_pageIndicatorSpacing, 10.0f);
        a.recycle();
        this.dotDrawable = context.getResources().getDrawable(emojiPageIndicatorResId);
        this.pageCount = 1;
        this.pageIndex = 0;
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        int verPad = getPaddingTop() + getPaddingBottom();
        int height = getHeight() - verPad;
        int yPos = getPaddingTop() + GeomUtil.getOffsetY(height, (int) this.dotHeight, 17);
        int horPad = getPaddingLeft() + getPaddingRight();
        int width = getWidth() - horPad;
        int contentWidth = getContentWidth();
        int xPos = getPaddingLeft() + GeomUtil.getOffsetX(width, contentWidth, 17);
        float scale = contentWidth > 0 ? width / contentWidth : 1.0f;
        this.dotDrawable.setBounds(0, 0, (int) this.dotWidth, (int) this.dotHeight);
        canvas.save(1);
        if (scale < 1.0f) {
            canvas.scale(scale, scale, getWidth() / 2, getHeight() / 2);
        }
        canvas.translate(xPos, yPos);
        for (int idx = 0; idx < this.pageCount; idx++) {
            if (idx == this.pageIndex) {
                this.dotDrawable.setState(this.activeState);
            } else {
                this.dotDrawable.setState(this.inactiveState);
            }
            this.dotDrawable.draw(canvas);
            canvas.translate(this.dotWidth + this.horSpacing, 0.0f);
        }
        canvas.restore();
        if (this.drawDebug != null) {
            this.drawDebug.drawPaddingOutline(this, canvas);
        }
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int minWidth = getContentWidth() + getPaddingLeft() + getPaddingRight();
        setMeasuredDimension(ViewUtil.getDefaultSizePreferMin(minWidth, widthMeasureSpec), getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec));
    }

    private int getContentWidth() {
        return (int) Math.ceil((this.pageCount * this.dotWidth) + ((this.pageCount - 1) * this.horSpacing));
    }

    public void setNumberOfPages(int count) {
        this.pageCount = count;
        requestLayout();
        invalidate();
    }

    public void setActivePage(int pageNumber) {
        this.pageIndex = pageNumber;
        invalidate();
    }
}
