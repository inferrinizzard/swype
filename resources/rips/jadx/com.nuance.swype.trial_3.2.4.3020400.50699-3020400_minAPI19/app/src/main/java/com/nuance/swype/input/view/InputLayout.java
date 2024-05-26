package com.nuance.swype.input.view;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import com.nuance.android.compat.ViewCompat;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.R;
import com.nuance.swype.input.view.DragFrame;
import com.nuance.swype.input.view.DragHelper;
import com.nuance.swype.util.LogManager;
import com.nuance.swype.view.ShowHideAnimManager;

/* loaded from: classes.dex */
public class InputLayout extends LinearLayout {
    public static final int dragStateAlpha = 127;
    protected static final LogManager.Log log = LogManager.getLog("InputLayout");
    public static final int normalAlpha = 255;
    private FrameLayout candidatesParentFrame;
    private View candidatesView;
    private View contentArea;
    private FrameLayout coverParentFrame;
    private View coverView;
    private DragFrame dragFrame;
    private DragHelper dragHelper;
    final boolean enableShadow;
    private View inputView;
    private FrameLayout inputViewParentFrame;
    private boolean isTemporaryLayerSet;
    private boolean isUsingDragPaintOnInputLayout;
    private InputLayoutMain mainArea;
    private int[] posTemp;
    private View topShadow;

    /* loaded from: classes.dex */
    public interface DragListener {
        void onBeginDrag();

        void onDrag(int i, int i2);

        void onEndDrag();

        void onSnapToEdge(int i, int i2);
    }

    public static InputLayout create(Context context, boolean useWings) {
        LayoutInflater inflater = LayoutInflater.from(context);
        IMEApplication app = IMEApplication.from(context);
        LayoutInflater inflater2 = app.getThemedLayoutInflater(inflater);
        app.getThemeLoader().setLayoutInflaterFactory(inflater2);
        InputLayout input = (InputLayout) inflater2.inflate(useWings ? R.layout.input_layout_wings : R.layout.input_layout, (ViewGroup) null);
        app.getThemeLoader().applyTheme(input);
        return input;
    }

    public InputLayout(Context context) {
        this(context, null);
    }

    public InputLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.dragHelper = new DragHelper();
        this.isUsingDragPaintOnInputLayout = ViewCompat.supportsSetLayerPaint();
        this.posTemp = new int[2];
        this.enableShadow = getResources().getBoolean(R.bool.show_top_shadow);
    }

    public boolean hasInternalDragFrame() {
        return this.dragFrame != null;
    }

    @Override // android.widget.LinearLayout
    public void setShowDividers(int showDividers) {
        super.setShowDividers(showDividers);
    }

    public void showDragFrame(boolean show) {
        if (this.dragFrame == null) {
            throw new UnsupportedOperationException("Layout has no internal drag frame");
        }
        this.dragFrame.showDecoration(show);
    }

    public void setDragGripIsDrag(boolean isDrag, boolean enable) {
        if (this.dragFrame != null && (this.dragFrame instanceof DragFrameGripPad)) {
            ((DragFrameGripPad) this.dragFrame).setGripIsDrag(isDrag, enable);
        }
    }

    public void setDragListener(DragFrame.DragFrameListener dragListener) {
        if (this.dragFrame == null) {
            throw new UnsupportedOperationException("Layout has no internal drag frame");
        }
        this.dragFrame.setDragListener(dragListener);
    }

    @TargetApi(11)
    private static Animator loadAnim(Context context, TypedArray attr, int idx) {
        int id = attr.getResourceId(idx, 0);
        if (id <= 0) {
            return null;
        }
        Animator out = AnimatorInflater.loadAnimator(context, id);
        return out;
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.candidatesParentFrame = (FrameLayout) findViewById(R.id.candidateview_container);
        this.inputViewParentFrame = (FrameLayout) findViewById(R.id.inputview_container);
        this.topShadow = findViewById(R.id.top_shadow_view);
        this.contentArea = findViewById(R.id.content_area);
        this.mainArea = (InputLayoutMain) findViewById(R.id.main_area);
        this.coverParentFrame = (FrameLayout) findViewById(R.id.coverview_container);
        this.dragFrame = (DragFrame) findViewById(R.id.drag_frame);
        showShadow(this.enableShadow);
    }

    private static void showView(ViewGroup frame, View view, int width, int height, ShowHideAnimManager animManager, boolean enableAnim) {
        if (view != null) {
            if (view.getParent() != frame) {
                frame.removeAllViews();
                frame.addView(view, width, height);
            }
            showFrame(frame, animManager, enableAnim);
            return;
        }
        hideFrame(frame, animManager, enableAnim);
    }

    private static void showFrame(ViewGroup frame, ShowHideAnimManager animManager, boolean enableAnim) {
        frame.setVisibility(0);
        if (animManager != null) {
            animManager.show(frame, enableAnim ? false : true);
        }
    }

    private static void hideFrame(ViewGroup frame, ShowHideAnimManager animManager, boolean enableAnim) {
        frame.removeAllViews();
        if (animManager != null) {
            animManager.hide(frame, !enableAnim);
        } else {
            frame.setVisibility(8);
        }
    }

    private void notifyDragVisualizer(boolean set, Object obj) {
        if (!this.isUsingDragPaintOnInputLayout && (obj instanceof DragHelper.DragVisualizer)) {
            ((DragHelper.DragVisualizer) obj).setDragHelper(set ? this.dragHelper : null);
        }
    }

    private void notifyBeginDrag(Object... objs) {
        for (Object obj : objs) {
            if (obj instanceof DragListener) {
                ((DragListener) obj).onBeginDrag();
            }
            notifyDragVisualizer(true, obj);
        }
    }

    private void notifyDrag(int dx, int dy, Object... objs) {
        for (Object obj : objs) {
            if (obj instanceof DragListener) {
                ((DragListener) obj).onDrag(dx, dy);
            }
        }
    }

    private void notifyEndDrag(Object... objs) {
        for (Object obj : objs) {
            if (obj instanceof DragListener) {
                ((DragListener) obj).onEndDrag();
            }
            notifyDragVisualizer(false, obj);
        }
    }

    private void notifySnapToEdge(int dx, int dy, Object... objs) {
        for (Object obj : objs) {
            if (obj instanceof DragListener) {
                ((DragListener) obj).onSnapToEdge(dx, dy);
            }
        }
    }

    public void setDragVisualState(float alphaScale, View... views) {
        for (View view : views) {
            this.dragHelper.setDragVisualState(view, alphaScale);
        }
    }

    public void clearDragVisualState(View... views) {
        for (View view : views) {
            this.dragHelper.clearDragVisualState(view);
        }
    }

    public void setDragVisualStateRecursive(float alphaScale, View view) {
        setDragVisualState(alphaScale, view);
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            int count = group.getChildCount();
            for (int idx = 0; idx < count; idx++) {
                setDragVisualStateRecursive(alphaScale, group.getChildAt(idx));
            }
        }
    }

    public void clearDragVisualStateRecursive(View view) {
        clearDragVisualState(view);
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            int count = group.getChildCount();
            for (int idx = 0; idx < count; idx++) {
                clearDragVisualStateRecursive(group.getChildAt(idx));
            }
        }
    }

    public void onBeginDrag() {
        this.dragHelper.onBeginDragVisualState();
        boolean isBackedByLayer = ViewCompat.isBackedByLayer(this);
        this.isTemporaryLayerSet = this.isUsingDragPaintOnInputLayout && !isBackedByLayer;
        if (this.isTemporaryLayerSet) {
            log.d("onBeginDrag(): enabling hw layer on layout");
            ViewCompat.enableHardwareLayer(this);
            if (!ViewCompat.isBackedByLayer(this)) {
                log.d("onBeginDrag(): no layer!");
                this.isUsingDragPaintOnInputLayout = false;
                this.isTemporaryLayerSet = false;
            }
        }
        if (this.isUsingDragPaintOnInputLayout) {
            setDragVisualState(0.5f, this);
        } else if (this.coverView != null) {
            this.contentArea.setVisibility(4);
            setDragVisualState(0.5f, this, this.coverView);
        } else {
            setDragVisualStateRecursive(0.5f, this);
        }
        notifyBeginDrag(this.mainArea, this.inputView, this.coverView);
    }

    public void onDrag(int dx, int dy) {
        notifyDrag(dx, dy, this.mainArea, this.inputView, this.coverView);
    }

    public void onEndDrag() {
        if (this.isTemporaryLayerSet) {
            log.d("onEndDrag(): removing hw layer on layout");
            ViewCompat.removeLayer(this);
            this.isTemporaryLayerSet = false;
        }
        if (!this.isUsingDragPaintOnInputLayout) {
            clearDragVisualStateRecursive(this);
        }
        this.contentArea.setVisibility(0);
        this.dragHelper.onFinishDragVisualState();
        notifyEndDrag(this.mainArea, this.inputView, this.coverView);
    }

    public void onSnapToEdge(int dx, int dy) {
        notifySnapToEdge(dx, dy, this.inputView, this.coverView);
    }

    public void showInput(boolean show) {
        showView(this.inputViewParentFrame, show ? this.inputView : null, -1, -2, null, false);
    }

    public void showCandidates(boolean show) {
        showView(this.candidatesParentFrame, show ? this.candidatesView : null, -1, -2, null, false);
    }

    public void showShadow(boolean show) {
        this.topShadow.setVisibility((show && this.enableShadow) ? 0 : 8);
    }

    private static int getLayoutHeight(View view) {
        if (view.getVisibility() != 8) {
            return view.getMeasuredHeight();
        }
        return 0;
    }

    public int getVisibleTopInWindow() {
        getVisiblePosInWindow(this.posTemp);
        return this.posTemp[1];
    }

    public void getVisiblePosInWindow(int[] pos) {
        this.contentArea.getLocationInWindow(pos);
    }

    public void setInputView(View view) {
        this.inputView = view;
        showInput(true);
    }

    public void setCandidatesView(View view) {
        this.candidatesView = view;
    }

    public void setCoverView(View view, boolean enableAnim) {
        setCoverView(view, enableAnim, -1, -1);
    }

    public void setCoverView(View view, boolean enableAnim, int width, int height) {
        this.coverView = view;
        showView(this.coverParentFrame, this.coverView, width, height, null, enableAnim);
    }

    public View getCandidatesView() {
        return this.candidatesView;
    }

    public View getCoverView() {
        return this.coverView;
    }

    public View getInputView() {
        return this.inputView;
    }

    public Rect getVisibleAreaInsets() {
        return new Rect(0, this.topShadow != null ? getLayoutHeight(this.topShadow) : 0, 0, 0);
    }

    private boolean isInputViewInLayout() {
        return (this.inputView == null || this.inputView.getVisibility() == 8) ? false : true;
    }

    /* JADX WARN: Code restructure failed: missing block: B:12:0x002e, code lost:            return null;     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public android.graphics.Rect getInputAreaRectRelativeTo(android.view.View r6) {
        /*
            r5 = this;
            r1 = 0
            boolean r0 = r5.isInputViewInLayout()
            if (r0 == 0) goto L2f
            android.view.View r0 = r5.inputView
            android.graphics.Rect r2 = com.nuance.swype.util.GeomUtil.getRect(r0)
            android.view.ViewParent r0 = r0.getParent()
        L11:
            if (r0 == r6) goto L2b
            if (r0 == 0) goto L2b
            boolean r3 = r0 instanceof android.view.View
            if (r3 == 0) goto L2b
            android.view.View r0 = (android.view.View) r0
            int r3 = r0.getLeft()
            int r4 = r0.getTop()
            com.nuance.swype.util.GeomUtil.moveRectBy(r2, r3, r4)
            android.view.ViewParent r0 = r0.getParent()
            goto L11
        L2b:
            if (r0 == r6) goto L31
            r0 = r1
        L2e:
            return r0
        L2f:
            r0 = r1
            goto L2e
        L31:
            r0 = r2
            goto L2e
        */
        throw new UnsupportedOperationException("Method not decompiled: com.nuance.swype.input.view.InputLayout.getInputAreaRectRelativeTo(android.view.View):android.graphics.Rect");
    }

    public static void setBackAlpha(View view, int val) {
        Drawable bg;
        if (view != null && (bg = view.getBackground()) != null) {
            bg.setAlpha(val);
        }
    }
}
