package com.nuance.swype.input.japanese;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.nuance.swype.input.InputView;
import com.nuance.swype.input.R;
import com.nuance.swype.input.view.InputLayout;

/* loaded from: classes.dex */
public class JapaneseWordPageContainer extends FrameLayout implements InputLayout.DragListener {
    protected LayoutInflater mInflater;
    protected JapaneseInputView mKeyboardView;

    public InputView getInputView() {
        if (this.mKeyboardView == null) {
            initViews();
        }
        return this.mKeyboardView;
    }

    public JapaneseWordPageContainer(Context context) {
        this(context, null);
    }

    public JapaneseWordPageContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mKeyboardView = null;
    }

    @SuppressLint({"InflateParams", "PrivateResource"})
    public void initViews() {
        if (this.mKeyboardView == null) {
            this.mInflater = (LayoutInflater) getContext().getSystemService("layout_inflater");
            this.mKeyboardView = (JapaneseInputView) this.mInflater.inflate(R.layout.japanese_input, (ViewGroup) null);
            addView(this.mKeyboardView);
            this.mKeyboardView.setContainerView(this);
        }
    }

    public void hideContextWindow(View aContextWindow) {
        if (aContextWindow != null) {
            removeView(aContextWindow);
            this.mKeyboardView.setVisibility(0);
            requestLayout();
            this.mKeyboardView.setContextWindowShowing(false);
        }
    }

    public void showContextWindow(View aContextWindow) {
        if (aContextWindow != null) {
            this.mKeyboardView.setVisibility(8);
            addView(aContextWindow);
            aContextWindow.setVisibility(0);
            bringChildToFront(aContextWindow);
            requestLayout();
            this.mKeyboardView.setContextWindowShowing(true);
        }
    }

    @Override // com.nuance.swype.input.view.InputLayout.DragListener
    public void onBeginDrag() {
        InputLayout.setBackAlpha(this.mKeyboardView, 127);
        getInputView().onBeginDrag();
    }

    @Override // com.nuance.swype.input.view.InputLayout.DragListener
    public void onDrag(int dx, int dy) {
        getInputView().onDrag(dx, dy);
    }

    @Override // com.nuance.swype.input.view.InputLayout.DragListener
    public void onEndDrag() {
        InputLayout.setBackAlpha(this.mKeyboardView, 255);
        getInputView().onEndDrag();
    }

    @Override // com.nuance.swype.input.view.InputLayout.DragListener
    public void onSnapToEdge(int dx, int dy) {
        getInputView().onSnapToEdge(dx, dy);
    }
}
