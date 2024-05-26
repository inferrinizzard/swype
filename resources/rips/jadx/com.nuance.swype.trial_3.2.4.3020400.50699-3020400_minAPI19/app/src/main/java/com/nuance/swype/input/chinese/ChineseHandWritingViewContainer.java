package com.nuance.swype.input.chinese;

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
import java.util.List;

/* loaded from: classes.dex */
public class ChineseHandWritingViewContainer extends AbstractHandWritingContainer {
    public static final int NO_GIRD_VIEW = 2;
    private VerCandidatesListContainer mCharacterListContainer;
    private int mDefaultHandwritingAreaFrameHeight;
    private FrameLayout mHandwritingAreaFrame;
    private FrameLayout mKeyboardAreaFrame;
    private ChineseHandWritingInputView mKeyboardView;
    private ChineseHandWritingView mWritingPadView;

    public ChineseHandWritingViewContainer(Context context) {
        this(context, null);
    }

    public ChineseHandWritingViewContainer(Context context, AttributeSet attrs) {
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
            this.mHandwritingAreaFrame = (FrameLayout) findViewById(R.id.chinese_handwriting_area);
            this.mDefaultHandwritingAreaFrameHeight = this.mHandwritingAreaFrame.getLayoutParams().height;
            updateHandwritingPadSize();
            this.mKeyboardAreaFrame = (FrameLayout) findViewById(R.id.chinese_handwriting_key_area);
            LayoutInflater inflater = LayoutInflater.from(getContext());
            IMEApplication app = IMEApplication.from(getContext());
            LayoutInflater inflater2 = app.getThemedLayoutInflater(inflater);
            this.mKeyboardView = (ChineseHandWritingInputView) inflater2.inflate(R.layout.chinese_handwriting_input, (ViewGroup) null);
            this.mKeyboardView.setHWContainer(this);
            app.getThemeLoader().setLayoutInflaterFactory(inflater2);
            this.mWritingPadView = (ChineseHandWritingView) inflater2.inflate(R.layout.chinese_writing_view, (ViewGroup) null);
            app.getThemeLoader().applyTheme(this.mWritingPadView);
            LayoutInflater inflater3 = app.getThemedLayoutInflater(inflater2);
            app.getThemeLoader().setLayoutInflaterFactory(inflater3);
            this.mCharacterListContainer = (VerCandidatesListContainer) inflater3.inflate(R.layout.chinese_ver_hw_candidates_list_container, (ViewGroup) null);
            app.getThemeLoader().applyTheme(this.mCharacterListContainer);
            this.mCharacterListContainer.setAttrs(this.mKeyboardView, R.style.CHNHWCharacterListView);
            this.mCharacterListContainer.initViews();
            this.mHandwritingAreaFrame.addView(this.mWritingPadView, new FrameLayout.LayoutParams(-1, -1));
            this.mKeyboardAreaFrame.addView(this.mKeyboardView, new FrameLayout.LayoutParams(-1, -1));
            this.mWritingPadView.setOnWritingActionListener(this.mKeyboardView);
            this.mKeyboardView.setHandWritingView(this.mWritingPadView);
            this.mWritingPadView.setMultitouchListener(this.mKeyboardView);
        }
    }

    public void updateHandwritingPadSize() {
        if (this.mHandwritingAreaFrame != null) {
            updateHandwritingAreaSize(this.mHandwritingAreaFrame, this.mDefaultHandwritingAreaFrameHeight);
        }
    }

    public void setCandidates(List<CharSequence> aPrefixList) {
        if (this.mCharacterListContainer != null) {
            this.mCharacterListContainer.setCandidates(aPrefixList);
        }
    }

    @SuppressLint({"PrivateResource"})
    public void showHWFrameAndCharacterList() {
        this.mHandwritingAreaFrame.setVisibility(0);
    }

    public void showHWFrameList() {
        this.mHandwritingAreaFrame.setVisibility(0);
    }

    public void hideHWFrameAndCharacterList() {
        this.mHandwritingAreaFrame.setVisibility(8);
    }

    public void hideCharacterList() {
        this.mCharacterListContainer.setVisibility(8);
    }

    public void showCharacterList() {
        this.mCharacterListContainer.setVisibility(0);
    }

    public void hideCandidatesGridView(View aPopupWindow) {
        if (aPopupWindow != null) {
            removeView(aPopupWindow);
            this.mHandwritingAreaFrame.setVisibility(0);
            this.mKeyboardAreaFrame.setVisibility(0);
            this.mKeyboardView.setContextWindowShowing(false);
        }
    }

    public void showCandidatesGridView(View aPopupWindow) {
        if (aPopupWindow != null) {
            this.mHandwritingAreaFrame.setVisibility(8);
            this.mKeyboardAreaFrame.setVisibility(8);
            addView(aPopupWindow);
            this.mKeyboardView.setContextWindowShowing(true);
        }
    }

    public void clearCharacterList() {
        if (this.mCharacterListContainer != null) {
            this.mCharacterListContainer.clear();
            this.mCharacterListContainer.requestLayout();
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
            int h2 = this.mKeyboardView.getMeasuredHeight();
            if (this.mHandwritingAreaFrame.isShown() || getChildCount() == 2) {
                h = this.mKeyboardView.getMeasuredHeight();
            } else {
                h = h2 + this.mKeyboardView.getCandidateHeight();
            }
        }
        if (this.mWritingPadView != null && this.mHandwritingAreaFrame.getVisibility() == 0 && getChildCount() == 2) {
            return h + this.mWritingPadView.getMeasuredHeight() + this.mHandwritingAreaFrame.getPaddingBottom() + this.mHandwritingAreaFrame.getPaddingTop();
        }
        if (this.mWritingPadView != null && getChildCount() != 2) {
            return h + this.mWritingPadView.getMeasuredHeight();
        }
        return h;
    }

    public int getCharacterListWidth() {
        if (this.mCharacterListContainer != null) {
            return this.mCharacterListContainer.getWidth();
        }
        return 0;
    }

    public VerCandidatesListContainer getVerContainer() {
        return this.mCharacterListContainer;
    }

    @Override // com.nuance.swype.input.AbstractHandWritingContainer, com.nuance.swype.input.view.InputLayout.DragListener
    public void onBeginDrag() {
        InputLayout.setBackAlpha(this.mKeyboardView, 127);
        InputLayout.setBackAlpha(this.mCharacterListContainer, 127);
        InputLayout.setBackAlpha(this.mWritingPadView, 127);
        super.onBeginDrag();
    }

    @Override // com.nuance.swype.input.AbstractHandWritingContainer, com.nuance.swype.input.view.InputLayout.DragListener
    public void onDrag(int dx, int dy) {
        super.onDrag(dx, dy);
    }

    @Override // com.nuance.swype.input.AbstractHandWritingContainer, com.nuance.swype.input.view.InputLayout.DragListener
    public void onEndDrag() {
        InputLayout.setBackAlpha(this.mKeyboardView, 255);
        InputLayout.setBackAlpha(this.mCharacterListContainer, 255);
        InputLayout.setBackAlpha(this.mWritingPadView, 255);
        super.onEndDrag();
    }
}
