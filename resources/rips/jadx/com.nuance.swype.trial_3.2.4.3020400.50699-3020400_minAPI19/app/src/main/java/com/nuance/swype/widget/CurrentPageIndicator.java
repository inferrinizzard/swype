package com.nuance.swype.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.nuance.swype.input.R;

/* loaded from: classes.dex */
public class CurrentPageIndicator extends LinearLayout {
    Context context;
    private int currentPage;
    private Drawable currentPageDrawable;
    private int drawablePadding;
    private int mode;
    private Drawable otherPageDrawable;
    private int totalPages;

    public CurrentPageIndicator(Context context) {
        super(context);
        this.mode = 0;
    }

    public CurrentPageIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mode = 0;
        this.context = context;
        loadAttributes(attrs, context, R.attr.currentPageIndicatorStyle);
        makeView();
    }

    @SuppressLint({"NewApi"})
    public CurrentPageIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mode = 0;
        this.context = context;
        loadAttributes(attrs, context, defStyle);
        makeView();
    }

    private void makeView() {
        if (this.mode == 1) {
            int i = 1;
            while (i <= this.totalPages) {
                ImageView view = new ImageView(this.context);
                LinearLayout.LayoutParams params = generateDefaultLayoutParams();
                params.leftMargin = this.drawablePadding;
                params.rightMargin = this.drawablePadding;
                view.setImageDrawable(i == this.currentPage ? this.currentPageDrawable : this.otherPageDrawable);
                view.setLayoutParams(params);
                addView(view);
                i++;
            }
            return;
        }
        TextView t = new TextView(this.context);
        t.setText(String.format("(%1$s of %2$s)", String.valueOf(this.currentPage), String.valueOf(this.totalPages)));
        addView(t);
    }

    private void loadAttributes(AttributeSet attrs, Context context, int defStyle) {
        Resources.Theme theme = context.getTheme();
        TypedArray a = theme.obtainStyledAttributes(attrs, R.styleable.CurrentPageIndicatorAppearance, defStyle, 0);
        TypedArray appearance = null;
        int ia = a.getResourceId(R.styleable.CurrentPageIndicatorAppearance_indicatorAppearance, -1);
        if (ia != -1) {
            appearance = theme.obtainStyledAttributes(ia, R.styleable.IndicatorAppearance);
        }
        if (appearance != null) {
            int n = appearance.getIndexCount();
            for (int i = 0; i < n; i++) {
                int index = appearance.getIndex(i);
                if (index != R.styleable.IndicatorAppearance_android_gravity && index != R.styleable.IndicatorAppearance_android_layout_margin) {
                    if (index == R.styleable.IndicatorAppearance_currentPageDrawable) {
                        this.currentPageDrawable = appearance.getDrawable(R.styleable.IndicatorAppearance_currentPageDrawable);
                    } else if (index == R.styleable.IndicatorAppearance_otherPageDrawable) {
                        this.otherPageDrawable = appearance.getDrawable(R.styleable.IndicatorAppearance_otherPageDrawable);
                    } else {
                        int i2 = R.styleable.IndicatorAppearance_drawablePadding;
                    }
                }
            }
            appearance.recycle();
        }
        a.recycle();
        TypedArray a2 = theme.obtainStyledAttributes(attrs, R.styleable.CurrentPageIndicator, defStyle, 0);
        int n2 = a2.getIndexCount();
        for (int i3 = 0; i3 < n2; i3++) {
            int attr = a2.getIndex(i3);
            if (attr == R.styleable.CurrentPageIndicator_currentPage) {
                this.currentPage = a2.getInt(attr, 0);
            } else if (attr == R.styleable.CurrentPageIndicator_totalPages) {
                this.totalPages = a2.getInt(attr, 0);
            } else if (attr == R.styleable.CurrentPageIndicator_currentPageDrawable) {
                this.currentPageDrawable = a2.getDrawable(attr);
            } else if (attr == R.styleable.CurrentPageIndicator_otherPageDrawable) {
                this.otherPageDrawable = a2.getDrawable(attr);
            } else if (attr == R.styleable.CurrentPageIndicator_drawablePadding) {
                this.drawablePadding = a2.getDimensionPixelSize(attr, 0);
            }
        }
        if (this.currentPageDrawable != null && this.otherPageDrawable != null) {
            this.mode = 1;
        }
        a2.recycle();
        if (this.currentPage <= 0 || this.totalPages <= 0 || this.currentPage > this.totalPages) {
            throw new IllegalArgumentException();
        }
    }
}
