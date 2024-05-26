package com.nuance.swype.input;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import com.nuance.swype.input.view.InputLayout;

/* loaded from: classes.dex */
public class AlphaHandWritingContainerView extends AbstractHandWritingContainer {
    private int mDefaultHandwritingPadHeight;
    protected AlphaHandWritingView mHandwritingPadView;
    private AlphaHandWritingInputView mKeyboardInputView;

    public AlphaHandWritingContainerView(Context context) {
        this(context, null);
    }

    public AlphaHandWritingContainerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override // com.nuance.swype.input.AbstractHandWritingContainer
    public void initViews() {
        this.mKeyboardInputView = (AlphaHandWritingInputView) findViewById(R.id.keyboardview);
        this.mHandwritingPadView = (AlphaHandWritingView) findViewById(R.id.alpha_handwriting_area);
        this.mDefaultHandwritingPadHeight = this.mHandwritingPadView.getLayoutParams().height;
        updateHandwritingPadSize();
        this.mKeyboardInputView.setContainerView(this);
        this.mHandwritingPadView.setOnWritingActionListener(this.mKeyboardInputView);
        this.mKeyboardInputView.setHandWritingView(this.mHandwritingPadView);
    }

    public void updateHandwritingPadSize() {
        if (this.mHandwritingPadView != null) {
            updateHandwritingAreaSize(this.mHandwritingPadView, this.mDefaultHandwritingPadHeight);
        }
    }

    @Override // com.nuance.swype.input.AbstractHandWritingContainer
    public InputView getInputView() {
        if (this.mKeyboardInputView == null) {
            initViews();
        }
        return this.mKeyboardInputView;
    }

    @Override // com.nuance.swype.input.AbstractHandWritingContainer, android.widget.LinearLayout, android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.mHandwritingPadView.measure(this.mKeyboardInputView.getMeasuredWidth(), this.mHandwritingPadView.getMeasuredHeight());
    }

    @Override // com.nuance.swype.input.AbstractHandWritingContainer
    public int getExactWidth(int widthMeasureSpec) {
        int w = View.MeasureSpec.getSize(widthMeasureSpec);
        if (this.mKeyboardInputView != null) {
            int w2 = this.mKeyboardInputView.getMeasuredWidth();
            return w2;
        }
        return w;
    }

    @Override // com.nuance.swype.input.AbstractHandWritingContainer
    public int getExactHeight(int heightMeasureSpec) {
        int h = View.MeasureSpec.getSize(heightMeasureSpec);
        if (this.mKeyboardInputView != null) {
            h = this.mKeyboardInputView.getMeasuredHeight();
        }
        if (this.mHandwritingPadView != null) {
            return h + this.mHandwritingPadView.getMeasuredHeight();
        }
        return h;
    }

    @Override // com.nuance.swype.input.AbstractHandWritingContainer, com.nuance.swype.input.view.InputLayout.DragListener
    public void onBeginDrag() {
        InputLayout.setBackAlpha(this.mKeyboardInputView, 127);
        InputLayout.setBackAlpha(this.mHandwritingPadView, 127);
        super.onBeginDrag();
    }

    @Override // com.nuance.swype.input.AbstractHandWritingContainer, com.nuance.swype.input.view.InputLayout.DragListener
    public void onEndDrag() {
        InputLayout.setBackAlpha(this.mKeyboardInputView, 255);
        InputLayout.setBackAlpha(this.mHandwritingPadView, 255);
        super.onEndDrag();
    }
}
