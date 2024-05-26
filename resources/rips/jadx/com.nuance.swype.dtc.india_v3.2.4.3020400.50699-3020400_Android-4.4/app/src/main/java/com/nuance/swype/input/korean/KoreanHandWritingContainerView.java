package com.nuance.swype.input.korean;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import com.nuance.android.compat.ActivityManagerCompat;
import com.nuance.android.compat.ViewCompat;
import com.nuance.swype.input.AbstractHandWritingContainer;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.InputView;
import com.nuance.swype.input.R;
import com.nuance.swype.input.view.InputLayout;

/* loaded from: classes.dex */
public class KoreanHandWritingContainerView extends AbstractHandWritingContainer {
    private int mDefaultHandwritingHeight;
    private PopupWindow mFullScreenHandWritingPopup;
    private KoreanHandWritingView mFullScreenHandwritingView;
    private FrameLayout mHandwritingAreaFrame;
    private FrameLayout mKeyboardAreaFrame;
    private KoreanHandWritingInputView mKeyboardView;
    private KoreanHandWritingView mWritingPadView;

    public KoreanHandWritingContainerView(Context context) {
        this(context, null);
    }

    public KoreanHandWritingContainerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override // com.nuance.swype.input.AbstractHandWritingContainer
    public InputView getInputView() {
        if (this.mKeyboardView == null) {
            initViews();
        }
        return this.mKeyboardView;
    }

    @Override // com.nuance.swype.input.AbstractHandWritingContainer
    @SuppressLint({"InflateParams", "PrivateResource"})
    public void initViews() {
        if (this.mHandwritingAreaFrame == null) {
            this.mHandwritingAreaFrame = (FrameLayout) findViewById(R.id.korean_handwriting_area);
            this.mKeyboardAreaFrame = (FrameLayout) findViewById(R.id.korean_handwriting_key_area);
            this.mDefaultHandwritingHeight = this.mHandwritingAreaFrame.getLayoutParams().height;
            updateHandwritingPadSize();
            LayoutInflater inflater = IMEApplication.from(getContext()).getThemedLayoutInflater(LayoutInflater.from(getContext()));
            this.mKeyboardView = (KoreanHandWritingInputView) inflater.inflate(R.layout.korean_handwriting_input, (ViewGroup) null);
            this.mWritingPadView = (KoreanHandWritingView) inflater.inflate(R.layout.korean_writing_view, (ViewGroup) null);
            this.mFullScreenHandwritingView = (KoreanHandWritingView) inflater.inflate(R.layout.korean_full_screen_writing_view, (ViewGroup) null);
            this.mHandwritingAreaFrame.addView(this.mWritingPadView, new FrameLayout.LayoutParams(-1, -1));
            this.mKeyboardAreaFrame.addView(this.mKeyboardView, new FrameLayout.LayoutParams(-1, -1));
            this.mWritingPadView.setOnWritingActionListener(this.mKeyboardView);
            this.mFullScreenHandwritingView.setOnWritingActionListener(this.mKeyboardView);
            this.mKeyboardView.setHandWritingView(this.mWritingPadView);
            this.mFullScreenHandwritingView.setSelectionAreaListener(this.mKeyboardView);
            this.mKeyboardView.setContainerView(this);
            this.mWritingPadView.setMultitouchListener(this.mKeyboardView);
            this.mFullScreenHandwritingView.setMultitouchListener(this.mKeyboardView);
        }
    }

    public void updateHandwritingPadSize() {
        if (this.mHandwritingAreaFrame != null) {
            updateHandwritingAreaSize(this.mHandwritingAreaFrame, this.mDefaultHandwritingHeight);
        }
    }

    public void hideHWFrameAndCharacterList() {
        this.mHandwritingAreaFrame.setVisibility(8);
    }

    @SuppressLint({"PrivateResource"})
    public void setFullScreenHandWritingFrame() {
        this.mKeyboardView.updateKeyboardDockMode();
        this.mKeyboardView.setHandWritingView(this.mFullScreenHandwritingView);
        this.mHandwritingAreaFrame.setVisibility(8);
        if (this.mFullScreenHandWritingPopup == null) {
            this.mFullScreenHandWritingPopup = new PopupWindow(this.mFullScreenHandwritingView);
        }
        ViewCompat.setBackground(this.mKeyboardAreaFrame, IMEApplication.from(getContext()).getThemedDrawable(R.attr.keyboardBackgroundHwr));
    }

    @SuppressLint({"PrivateResource"})
    public void setNormalHandScreenWritingFrame() {
        this.mKeyboardView.updateKeyboardDockMode();
        this.mKeyboardView.setHandWritingView(this.mWritingPadView);
        hideFullScreenHandWritingFrame();
        this.mHandwritingAreaFrame.setVisibility(0);
        setBackground(IMEApplication.from(getContext()).getThemedDrawable(R.attr.keyboardBackgroundHwrContainer));
    }

    public void hideFullScreenHandWritingFrame() {
        if (this.mFullScreenHandWritingPopup != null && this.mFullScreenHandWritingPopup.isShowing()) {
            this.mFullScreenHandWritingPopup.dismiss();
            this.mFullScreenHandWritingPopup = null;
        }
    }

    public void showFullScreenHandWritingFrame(int x, int y, int w, int h) {
        if (this.mFullScreenHandWritingPopup != null && !this.mFullScreenHandWritingPopup.isShowing() && !ActivityManagerCompat.isUserAMonkey()) {
            log.d("showFullScreenHandWritingFrame...x: ", Integer.valueOf(x), "..y: ", Integer.valueOf(y), "..w: ", Integer.valueOf(w), "..h: ", Integer.valueOf(h));
            this.mFullScreenHandwritingView.measure(w, h);
            this.mFullScreenHandWritingPopup.update(0, 0, w, h);
            this.mFullScreenHandWritingPopup.showAtLocation(this.mKeyboardView, 17, x, y);
        }
    }

    @Override // com.nuance.swype.input.AbstractHandWritingContainer, android.widget.LinearLayout, android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.mWritingPadView.measure(this.mKeyboardView.getMeasuredWidth(), this.mWritingPadView.getMeasuredHeight());
    }

    @Override // com.nuance.swype.input.AbstractHandWritingContainer
    public int getExactWidth(int widthMeasureSpec) {
        int w = View.MeasureSpec.getSize(widthMeasureSpec);
        if (this.mKeyboardView != null) {
            int w2 = this.mKeyboardView.getMeasuredWidth();
            return w2;
        }
        return w;
    }

    @Override // com.nuance.swype.input.AbstractHandWritingContainer
    public int getExactHeight(int heightMeasureSpec) {
        int h = View.MeasureSpec.getSize(heightMeasureSpec);
        if (this.mKeyboardView != null) {
            h = this.mKeyboardView.getMeasuredHeight();
        }
        if (this.mHandwritingAreaFrame.getVisibility() != 8) {
            return this.mWritingPadView.getMeasuredHeight() + h + this.mHandwritingAreaFrame.getPaddingBottom() + this.mHandwritingAreaFrame.getPaddingTop();
        }
        return h;
    }

    @Override // com.nuance.swype.input.AbstractHandWritingContainer, com.nuance.swype.input.view.InputLayout.DragListener
    public void onBeginDrag() {
        InputLayout.setBackAlpha(this.mKeyboardView, 127);
        InputLayout.setBackAlpha(this.mWritingPadView, 127);
        super.onBeginDrag();
    }

    @Override // com.nuance.swype.input.AbstractHandWritingContainer, com.nuance.swype.input.view.InputLayout.DragListener
    public void onEndDrag() {
        InputLayout.setBackAlpha(this.mKeyboardView, 255);
        InputLayout.setBackAlpha(this.mWritingPadView, 255);
        super.onEndDrag();
    }
}
