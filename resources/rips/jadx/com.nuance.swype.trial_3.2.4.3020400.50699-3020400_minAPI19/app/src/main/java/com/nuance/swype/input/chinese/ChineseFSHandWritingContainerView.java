package com.nuance.swype.input.chinese;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import com.nuance.swype.input.AbstractHandWritingContainer;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.InputView;
import com.nuance.swype.input.R;
import com.nuance.swype.input.view.InputLayout;

/* loaded from: classes.dex */
public class ChineseFSHandWritingContainerView extends AbstractHandWritingContainer {
    private static final int GRID_NUM = 5;
    private static final int WITHOUT_GRID_VIEW = 1;
    private static final int WITH_GRID_VIEW = 2;
    protected View candidatesPopup;
    private Context mContext;
    protected PopupWindow mFullScreenHandWritingPopup;
    protected ChineseHandWritingView mHandwritingView;
    protected FrameLayout mKeyboardAreadFrame;
    protected ChineseFSHandWritingInputView mKeyboardInputView;

    public ChineseFSHandWritingContainerView(Context context) {
        this(context, null);
    }

    public ChineseFSHandWritingContainerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.candidatesPopup = null;
        this.mContext = context;
    }

    @Override // com.nuance.swype.input.AbstractHandWritingContainer
    public InputView getInputView() {
        if (this.mKeyboardInputView == null) {
            initViews();
        }
        return this.mKeyboardInputView;
    }

    @Override // com.nuance.swype.input.AbstractHandWritingContainer
    @SuppressLint({"InflateParams", "PrivateResource"})
    public void initViews() {
        this.mKeyboardAreadFrame = (FrameLayout) findViewById(R.id.chinese_handwriting_key_area);
        LayoutInflater inflater = IMEApplication.from(getContext()).getThemedLayoutInflater(LayoutInflater.from(getContext()));
        this.mHandwritingView = (ChineseHandWritingView) inflater.inflate(R.layout.chinese_full_screen_writing_view, (ViewGroup) null);
        this.mKeyboardInputView = (ChineseFSHandWritingInputView) inflater.inflate(R.layout.chinesefs_handwriting_input, (ViewGroup) null);
        this.mKeyboardAreadFrame.addView(this.mKeyboardInputView, new FrameLayout.LayoutParams(-1, -2));
        this.mKeyboardInputView.setContainerView(this);
        this.mHandwritingView.setOnWritingActionListener(this.mKeyboardInputView);
        this.mHandwritingView.setSelectionAreaListener(this.mKeyboardInputView);
        this.mHandwritingView.setMultitouchListener(this.mKeyboardInputView);
    }

    public void setFullScreenHandWritingFrame() {
        this.mKeyboardInputView.setHandWritingView(this.mHandwritingView);
        if (this.mFullScreenHandWritingPopup == null) {
            this.mFullScreenHandWritingPopup = new PopupWindow(this.mHandwritingView);
        }
    }

    public void hideFullScreenHandWritingFrame(boolean aFlag) {
        if (this.mFullScreenHandWritingPopup != null) {
            this.mFullScreenHandWritingPopup.dismiss();
            if (aFlag) {
                this.mFullScreenHandWritingPopup = null;
            }
        }
    }

    public void showFullScreenHandWritingFrame(int x, int y, int w, int h) {
        log.d("showFullScreenHandWritingFrame...x: ", Integer.valueOf(x), "..y: ", Integer.valueOf(y), "..w: ", Integer.valueOf(w), "..h: ", Integer.valueOf(h));
        if (this.mFullScreenHandWritingPopup != null && !this.mFullScreenHandWritingPopup.isShowing() && this.mKeyboardInputView.getWindowToken() != null) {
            this.mHandwritingView.measure(w, h);
            this.mFullScreenHandWritingPopup.update(x, y, w, h);
            this.mFullScreenHandWritingPopup.showAtLocation(this.mKeyboardInputView, 17, x, y);
        }
    }

    public void hidePopupCandidatesView() {
        if (this.mKeyboardAreadFrame.getChildCount() == 2) {
            this.mKeyboardAreadFrame.removeView(this.candidatesPopup);
            this.mKeyboardAreadFrame.setMinimumHeight(this.mKeyboardInputView.getMeasuredHeight());
            this.mKeyboardAreadFrame.setMinimumWidth(this.mKeyboardInputView.getMeasuredWidth());
            this.mKeyboardInputView.setVisibility(0);
            this.mKeyboardInputView.setContextWindowShowing(false);
        }
    }

    public void showPopupCandidatesView(View candidatesView) {
        if (candidatesView != null && this.mKeyboardAreadFrame.getChildCount() == 1) {
            this.candidatesPopup = candidatesView;
            this.mKeyboardInputView.setVisibility(8);
            int envWidth = IMEApplication.from(this.mContext).getDisplayWidth();
            int height = this.mKeyboardInputView.getHeight() * 5;
            this.mKeyboardAreadFrame.setMinimumHeight(height);
            this.mKeyboardAreadFrame.setMinimumWidth(envWidth);
            this.mKeyboardAreadFrame.addView(this.candidatesPopup);
            this.mKeyboardInputView.setContextWindowShowing(true);
        }
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
            if (this.mKeyboardAreadFrame.getChildCount() == 2) {
                int h2 = this.mKeyboardInputView.getHeight() * 5;
                return h2;
            }
            int h3 = this.mKeyboardInputView.getMeasuredHeight();
            return h3;
        }
        return h;
    }

    @Override // com.nuance.swype.input.AbstractHandWritingContainer, com.nuance.swype.input.view.InputLayout.DragListener
    public void onBeginDrag() {
        InputLayout.setBackAlpha(this.mKeyboardInputView, 127);
        InputLayout.setBackAlpha(this.mHandwritingView, 127);
        super.onBeginDrag();
    }

    @Override // com.nuance.swype.input.AbstractHandWritingContainer, com.nuance.swype.input.view.InputLayout.DragListener
    public void onDrag(int dx, int dy) {
        super.onDrag(dx, dy);
    }

    @Override // com.nuance.swype.input.AbstractHandWritingContainer, com.nuance.swype.input.view.InputLayout.DragListener
    public void onEndDrag() {
        InputLayout.setBackAlpha(this.mKeyboardInputView, 255);
        InputLayout.setBackAlpha(this.mHandwritingView, 255);
        super.onEndDrag();
    }
}
