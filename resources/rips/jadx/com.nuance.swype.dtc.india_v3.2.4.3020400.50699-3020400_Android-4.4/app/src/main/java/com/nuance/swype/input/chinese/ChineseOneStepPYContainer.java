package com.nuance.swype.input.chinese;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.InputView;
import com.nuance.swype.input.R;
import com.nuance.swype.input.view.InputLayout;
import java.lang.ref.WeakReference;
import java.util.List;

/* loaded from: classes.dex */
public class ChineseOneStepPYContainer extends FrameLayout implements InputLayout.DragListener {
    private int lastKeyboardWidth;
    protected WeakReference<ChineseInputView> mKeyboardViewWeakRef;
    protected VerCandidatesListContainer mPrefixContainer;
    private boolean mPrefixVisible;

    public InputView getInputView() {
        if (this.mKeyboardViewWeakRef == null) {
            initViews();
        }
        return this.mKeyboardViewWeakRef.get();
    }

    public ChineseInputView getKeyboardView() {
        if (this.mKeyboardViewWeakRef != null) {
            return this.mKeyboardViewWeakRef.get();
        }
        return null;
    }

    public ChineseOneStepPYContainer(Context context) {
        this(context, null);
    }

    public ChineseOneStepPYContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mPrefixContainer = null;
        this.mPrefixVisible = false;
    }

    @SuppressLint({"InflateParams"})
    public void initViews() {
        if (this.mKeyboardViewWeakRef == null || this.mKeyboardViewWeakRef.get() == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            ChineseInputView keyboardView = (ChineseInputView) IMEApplication.from(getContext()).getThemedLayoutInflater(inflater).inflate(R.layout.chinese_input, (ViewGroup) null);
            this.mKeyboardViewWeakRef = new WeakReference<>(keyboardView);
            addView(keyboardView);
            keyboardView.setContainerView(this);
        }
        if (this.mPrefixContainer == null) {
            LayoutInflater inflater2 = LayoutInflater.from(getContext());
            IMEApplication app = IMEApplication.from(getContext());
            LayoutInflater inflater3 = app.getThemedLayoutInflater(inflater2);
            app.getThemeLoader().setLayoutInflaterFactory(inflater3);
            this.mPrefixContainer = (VerCandidatesListContainer) inflater3.inflate(R.layout.chinese_ver_candidates_list_container, (ViewGroup) null);
            app.getThemeLoader().applyTheme(this.mPrefixContainer);
            this.mPrefixContainer.setAttrs(this.mKeyboardViewWeakRef.get(), R.style.CHNPYPrefixListView);
            this.mPrefixContainer.initViews();
        }
    }

    public void setSpellPrefix(boolean prefix) {
        if (this.mPrefixContainer != null) {
            this.mPrefixContainer.setSpellPrefix(prefix);
        }
    }

    public void hideSymbolList() {
        if (getChildCount() > 1) {
            removeView(this.mPrefixContainer);
        }
    }

    @SuppressLint({"InflateParams", "NewApi"})
    public void showPrefixList(List<CharSequence> aPrefixList, int activePrefixIndex, boolean highlightPrefix) {
        int containerWidth;
        if (aPrefixList != null && !aPrefixList.isEmpty() && getKeyboardView() != null) {
            IMEApplication app = IMEApplication.from(getContext());
            if (this.mPrefixContainer == null) {
                LayoutInflater inflater = IMEApplication.from(getContext()).getThemedLayoutInflater(LayoutInflater.from(getContext()));
                app.getThemeLoader().setLayoutInflaterFactory(inflater);
                this.mPrefixContainer = (VerCandidatesListContainer) inflater.inflate(R.layout.chinese_ver_candidates_list_container, (ViewGroup) null);
                app.getThemeLoader().applyTheme(this.mPrefixContainer);
                this.mPrefixContainer.setAttrs(getKeyboardView(), R.style.CHNPYPrefixListView);
                this.mPrefixContainer.initViews();
            }
            int prefixContainerHeight = (getKeyboardView().getKeyboard().getHeight() * 3) / 4;
            this.mPrefixContainer.setKeyboardHeight(prefixContainerHeight);
            this.mPrefixContainer.setCandidates(aPrefixList, activePrefixIndex, highlightPrefix);
            int currentKeyboardWidth = getKeyboardView().getKeyboard().getMinWidth();
            if (getKeyboardView().isTabletDevice()) {
                containerWidth = currentKeyboardWidth / 5;
            } else {
                containerWidth = (currentKeyboardWidth * 14) / 100;
            }
            if (getChildCount() < 2) {
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(containerWidth, prefixContainerHeight, 3);
                addView(this.mPrefixContainer, params);
            } else if (getKeyboardView().isTabletDevice() && this.lastKeyboardWidth != currentKeyboardWidth) {
                ViewGroup.LayoutParams lp = this.mPrefixContainer.getLayoutParams();
                if (lp != null) {
                    lp.height = prefixContainerHeight;
                    lp.width = containerWidth;
                }
                updateViewLayout(this.mPrefixContainer, lp);
            } else {
                ViewGroup.LayoutParams lp2 = this.mPrefixContainer.getLayoutParams();
                if (lp2 != null && (lp2.height != prefixContainerHeight || lp2.width != containerWidth)) {
                    lp2.height = prefixContainerHeight;
                    lp2.width = containerWidth;
                    updateViewLayout(this.mPrefixContainer, lp2);
                }
            }
            this.lastKeyboardWidth = currentKeyboardWidth;
        }
    }

    public void hideContextWindow(View view) {
        if (view != null) {
            removeView(view);
            if (this.mPrefixVisible && this.mPrefixContainer != null) {
                this.mPrefixContainer.setVisibility(0);
            }
            getKeyboardView().setVisibility(0);
            requestLayout();
            getKeyboardView().setContextWindowShowing(false);
        }
    }

    public void showContextWindow(View view) {
        if (view != null) {
            if (this.mPrefixContainer != null) {
                if (this.mPrefixContainer.getVisibility() == 0) {
                    this.mPrefixContainer.setVisibility(8);
                    this.mPrefixVisible = true;
                } else {
                    this.mPrefixVisible = false;
                }
            }
            getKeyboardView().setVisibility(8);
            addView(view);
            bringChildToFront(view);
            requestLayout();
            getKeyboardView().setContextWindowShowing(true);
        }
    }

    public VerCandidatesListContainer getVerContainer() {
        return this.mPrefixContainer;
    }

    @Override // com.nuance.swype.input.view.InputLayout.DragListener
    public void onBeginDrag() {
        getInputView().onBeginDrag();
    }

    @Override // com.nuance.swype.input.view.InputLayout.DragListener
    public void onDrag(int dx, int dy) {
        InputLayout.setBackAlpha(getKeyboardView(), 127);
        getInputView().onDrag(dx, dy);
    }

    @Override // com.nuance.swype.input.view.InputLayout.DragListener
    public void onEndDrag() {
        getInputView().onEndDrag();
    }

    @Override // com.nuance.swype.input.view.InputLayout.DragListener
    public void onSnapToEdge(int dx, int dy) {
        InputLayout.setBackAlpha(getKeyboardView(), 255);
        getInputView().onSnapToEdge(dx, dy);
    }
}
