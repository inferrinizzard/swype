package com.nuance.swype.input.japanese;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.nuance.swype.input.AbstractHandWritingContainer;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.InputView;
import com.nuance.swype.input.R;
import com.nuance.swype.input.view.InputLayout;

/* loaded from: classes.dex */
public class JapaneseHandWritingContainerView extends AbstractHandWritingContainer {
    private int candidateListViewHeight;
    private int mDefaultHandwritingPadHeight;
    protected FrameLayout mHandwritingAreaFrame;
    protected JapaneseHandWritingView mHandwritingPadView;
    protected FrameLayout mKeyboardAreadFrame;
    protected JapaneseHandWritingInputView mKeyboardInputView;

    public JapaneseHandWritingContainerView(Context context) {
        this(context, null);
    }

    public JapaneseHandWritingContainerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.candidateListViewHeight = 0;
    }

    @Override // com.nuance.swype.input.AbstractHandWritingContainer
    @SuppressLint({"InflateParams", "PrivateResource"})
    public void initViews() {
        if (this.mHandwritingAreaFrame == null) {
            IMEApplication app = IMEApplication.from(getContext());
            LayoutInflater inflater = app.getThemedLayoutInflater(LayoutInflater.from(getContext()));
            app.getThemeLoader().setLayoutInflaterFactory(inflater);
            this.mHandwritingAreaFrame = (FrameLayout) findViewById(R.id.japanese_handwriting_area);
            this.mKeyboardAreadFrame = (FrameLayout) findViewById(R.id.japanese_handwriting_key_area);
            this.mHandwritingPadView = (JapaneseHandWritingView) inflater.inflate(R.layout.japanese_writing_view, (ViewGroup) null);
            app.getThemeLoader().applyTheme(this.mHandwritingPadView);
            this.mDefaultHandwritingPadHeight = this.mHandwritingAreaFrame.getLayoutParams().height;
            updateHandwritingPadSize();
            this.mKeyboardInputView = (JapaneseHandWritingInputView) inflater.inflate(R.layout.japanese_handwriting_input, (ViewGroup) null);
            this.mHandwritingAreaFrame.addView(this.mHandwritingPadView, new FrameLayout.LayoutParams(-1, -2));
            this.mKeyboardAreadFrame.addView(this.mKeyboardInputView, new FrameLayout.LayoutParams(-1, -2));
            this.mKeyboardInputView.setContainerView(this);
            this.mHandwritingPadView.setOnWritingActionListener(this.mKeyboardInputView);
            this.mKeyboardInputView.setHandWritingView(this.mHandwritingPadView);
            this.mHandwritingPadView.setMultitouchListener(this.mKeyboardInputView);
        }
    }

    public void updateHandwritingPadSize() {
        if (this.mHandwritingAreaFrame != null) {
            updateHandwritingAreaSize(this.mHandwritingAreaFrame, this.mDefaultHandwritingPadHeight);
        }
    }

    public void hideContextWindow(View aContextWindow) {
        if (aContextWindow != null) {
            removeView(aContextWindow);
            this.mKeyboardInputView.setVisibility(0);
            this.mHandwritingAreaFrame.setVisibility(0);
            this.candidateListViewHeight = 0;
            requestLayout();
            this.mKeyboardInputView.setContextWindowShowing(false);
        }
    }

    public void showContextWindow(View aContextWindow, int candidateListHeight) {
        if (aContextWindow != null) {
            this.mKeyboardInputView.setVisibility(8);
            this.mHandwritingAreaFrame.setVisibility(8);
            addView(aContextWindow);
            aContextWindow.setVisibility(0);
            this.candidateListViewHeight = candidateListHeight;
            bringChildToFront(aContextWindow);
            requestLayout();
            this.mKeyboardInputView.setContextWindowShowing(true);
        }
    }

    public void setNormalHandScreenWritingFrame() {
        this.mKeyboardInputView.setHandWritingView(this.mHandwritingPadView);
        this.mHandwritingAreaFrame.setVisibility(0);
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
            h += this.mHandwritingPadView.getMeasuredHeight();
        }
        if (this.candidateListViewHeight != 0) {
            return h + this.candidateListViewHeight;
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
    public void onDrag(int dx, int dy) {
        super.onDrag(dx, dy);
    }

    @Override // com.nuance.swype.input.AbstractHandWritingContainer, com.nuance.swype.input.view.InputLayout.DragListener
    public void onEndDrag() {
        InputLayout.setBackAlpha(this.mKeyboardInputView, 255);
        InputLayout.setBackAlpha(this.mHandwritingPadView, 255);
        super.onEndDrag();
    }
}
