package com.nuance.swype.input;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import com.nuance.swype.input.view.InputLayout;
import com.nuance.swype.util.LogManager;

/* loaded from: classes.dex */
public abstract class AbstractHandWritingContainer extends LinearLayout implements InputLayout.DragListener {
    public static final LogManager.Log log = LogManager.getLog("AbstractHandWritingContainer");

    public abstract int getExactHeight(int i);

    public abstract int getExactWidth(int i);

    public abstract InputView getInputView();

    public abstract void initViews();

    public AbstractHandWritingContainer(Context context) {
        this(context, null);
    }

    public AbstractHandWritingContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override // android.widget.LinearLayout, android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getExactWidth(widthMeasureSpec), getExactHeight(heightMeasureSpec));
    }

    public void updateHandwritingAreaSize(View view, int defaultHandwritingHeight) {
        float keyboardScale;
        UserPreferences userPrefs = IMEApplication.from(getContext()).getUserPreferences();
        if (getResources().getConfiguration().orientation == 1) {
            keyboardScale = userPrefs.getKeyboardScalePortrait();
        } else {
            keyboardScale = userPrefs.getKeyboardScaleLandscape();
        }
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) view.getLayoutParams();
        if (defaultHandwritingHeight <= 0) {
            defaultHandwritingHeight = view.getLayoutParams().height;
        }
        lp.height = (int) (defaultHandwritingHeight * keyboardScale);
        log.d("updateHandwritingAreaSize(): using: " + lp.height);
        view.invalidate();
    }

    @Override // com.nuance.swype.input.view.InputLayout.DragListener
    public void onBeginDrag() {
        getInputView().onBeginDrag();
    }

    @Override // com.nuance.swype.input.view.InputLayout.DragListener
    public void onDrag(int dx, int dy) {
        getInputView().onDrag(dx, dy);
    }

    @Override // com.nuance.swype.input.view.InputLayout.DragListener
    public void onEndDrag() {
        getInputView().onEndDrag();
    }

    @Override // com.nuance.swype.input.view.InputLayout.DragListener
    public void onSnapToEdge(int dx, int dy) {
        getInputView().onSnapToEdge(dx, dy);
    }
}
