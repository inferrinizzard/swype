package android.support.v7.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v7.appcompat.R;
import android.util.AttributeSet;
import android.widget.SeekBar;

/* loaded from: classes.dex */
public class AppCompatSeekBar extends SeekBar {
    private AppCompatSeekBarHelper mAppCompatSeekBarHelper;
    private AppCompatDrawableManager mDrawableManager;

    public AppCompatSeekBar(Context context) {
        this(context, null);
    }

    public AppCompatSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.seekBarStyle);
    }

    public AppCompatSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mDrawableManager = AppCompatDrawableManager.get();
        this.mAppCompatSeekBarHelper = new AppCompatSeekBarHelper(this, this.mDrawableManager);
        this.mAppCompatSeekBarHelper.loadFromAttributes(attrs, defStyleAttr);
    }

    @Override // android.widget.AbsSeekBar, android.widget.ProgressBar, android.view.View
    protected void onDraw(Canvas canvas) {
        int max;
        super.onDraw(canvas);
        AppCompatSeekBarHelper appCompatSeekBarHelper = this.mAppCompatSeekBarHelper;
        if (appCompatSeekBarHelper.mTickMark == null || (max = appCompatSeekBarHelper.mView.getMax()) <= 1) {
            return;
        }
        int intrinsicWidth = appCompatSeekBarHelper.mTickMark.getIntrinsicWidth();
        int intrinsicHeight = appCompatSeekBarHelper.mTickMark.getIntrinsicHeight();
        int i = intrinsicWidth >= 0 ? intrinsicWidth / 2 : 1;
        int i2 = intrinsicHeight >= 0 ? intrinsicHeight / 2 : 1;
        appCompatSeekBarHelper.mTickMark.setBounds(-i, -i2, i, i2);
        float width = ((appCompatSeekBarHelper.mView.getWidth() - appCompatSeekBarHelper.mView.getPaddingLeft()) - appCompatSeekBarHelper.mView.getPaddingRight()) / max;
        int save = canvas.save();
        canvas.translate(appCompatSeekBarHelper.mView.getPaddingLeft(), appCompatSeekBarHelper.mView.getHeight() / 2);
        for (int i3 = 0; i3 <= max; i3++) {
            appCompatSeekBarHelper.mTickMark.draw(canvas);
            canvas.translate(width, 0.0f);
        }
        canvas.restoreToCount(save);
    }

    @Override // android.widget.AbsSeekBar, android.widget.ProgressBar, android.view.View
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        AppCompatSeekBarHelper appCompatSeekBarHelper = this.mAppCompatSeekBarHelper;
        Drawable drawable = appCompatSeekBarHelper.mTickMark;
        if (drawable == null || !drawable.isStateful() || !drawable.setState(appCompatSeekBarHelper.mView.getDrawableState())) {
            return;
        }
        appCompatSeekBarHelper.mView.invalidateDrawable(drawable);
    }

    @Override // android.widget.AbsSeekBar, android.widget.ProgressBar, android.view.View
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        AppCompatSeekBarHelper appCompatSeekBarHelper = this.mAppCompatSeekBarHelper;
        if (appCompatSeekBarHelper.mTickMark == null) {
            return;
        }
        appCompatSeekBarHelper.mTickMark.jumpToCurrentState();
    }
}
