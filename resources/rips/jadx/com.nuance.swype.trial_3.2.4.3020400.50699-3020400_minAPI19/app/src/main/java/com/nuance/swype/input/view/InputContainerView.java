package com.nuance.swype.input.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.nuance.android.compat.ViewCompat;
import com.nuance.connect.common.Integers;
import com.nuance.swype.input.AppPreferences;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.KeyboardEx;
import com.nuance.swype.input.R;
import com.nuance.swype.input.view.DragFrame;
import com.nuance.swype.util.AdsUtil;
import com.nuance.swype.util.CoordUtils;
import com.nuance.swype.util.FrameworkUtil;
import com.nuance.swype.util.GeomUtil;
import com.nuance.swype.util.LogManager;
import com.nuance.swype.util.ViewUtil;
import com.nuance.swype.view.BasicViewLayout;
import com.nuance.swype.view.OverlayView;
import com.nuance.swype.view.PopupViewManager;
import java.util.HashSet;
import java.util.Set;

/* loaded from: classes.dex */
public class InputContainerView extends BasicViewLayout {
    private static final String PREFKEY_PREFIX_BOTTOM = "keyboard.bt.offset";
    private static final String PREFKEY_PREFIX_X = "keyboard.x.offset";
    private static final String PREFKEY_PREFIX_Y = "keyboard.y.offset";
    private static final LogManager.Log log = LogManager.getLog("InputContainerView");
    private IMEApplication app;
    private boolean isFullScreenMode;
    private Item item;
    private KeyboardEx.KeyboardDockMode keyboardDockMode;
    private boolean miniDockFullAppAreaMode;
    private OverlayView overlayView;
    private PopupViewManager popupViewManager;
    private boolean useWings;
    private final Set<View> widgetViews;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public enum PersistOp {
        SAVE,
        RESTORE,
        NOTHING
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String getPrefKeyX(int orientation, KeyboardEx.KeyboardDockMode dockMode) {
        return PREFKEY_PREFIX_X + orientation + dockMode.ordinal();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String getPrefKeyY(int orientation, KeyboardEx.KeyboardDockMode dockMode) {
        return PREFKEY_PREFIX_Y + orientation + dockMode.ordinal();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String getPrefKeyBottom(int orientation, KeyboardEx.KeyboardDockMode dockMode) {
        return PREFKEY_PREFIX_BOTTOM + orientation + dockMode.ordinal();
    }

    public InputContainerView(Context context) {
        this(context, null);
    }

    public InputContainerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.isFullScreenMode = false;
        this.useWings = false;
        this.widgetViews = new HashSet();
        this.keyboardDockMode = KeyboardEx.KeyboardDockMode.MOVABLE_MINI;
        this.miniDockFullAppAreaMode = false;
        this.app = IMEApplication.from(getContext());
    }

    public void init(View inputView, View candidatesView, boolean supportsFullMiniMovement) {
        boolean shouldUseWings = !supportsFullMiniMovement;
        if (shouldUseWings != this.useWings) {
            this.useWings = shouldUseWings;
            removeItem();
        } else if (this.item != null) {
            String currentTheme = this.app.getCurrentTheme().getSku();
            if (!this.item.getTheme().equals(currentTheme)) {
                removeItem();
            }
        }
        if (this.item == null) {
            this.item = new Item();
        }
        this.item.setInputView(inputView);
        this.item.setCandidatesView(candidatesView);
        this.item.setCover(null, false);
        this.item.dragLock(false);
        ViewUtil.resetView(this);
    }

    public void refresh(boolean supportsFullMiniMovement) {
        if (this.item == null) {
            throw new IllegalStateException("Bad state");
        }
        init(this.item.getInputView(), this.item.getCandidatesView(), supportsFullMiniMovement);
    }

    public void removeItem() {
        if (this.item != null) {
            this.item.setInputView(null);
            this.item.setCandidatesView(null);
            this.item.removeDragFrame();
        }
        this.item = null;
    }

    @Deprecated
    public int getRootViewHeight() {
        return this.item.getInputLayoutHeight();
    }

    public Rect getInputWindowRect() {
        return this.item.getInputWindowRect();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class Item implements DragFrame.DragFrameListener {
        private DragFrame dragFrame;
        private InputLayout inputLayout;
        private Rect inputViewRectLastLayout;
        private boolean isDragFrameVisible;
        private FrameLayout miniDockFrame;
        private View rootView;
        private int snapThreshold;
        private String themeId;
        private int dockGravity = 81;
        private boolean isDocked = true;
        private boolean allowDragX = true;
        private boolean allowDragY = true;
        private boolean dragLock = false;
        private boolean isSnapPending = false;
        private PersistOp pendingPersistOp = PersistOp.NOTHING;
        private int[] posTemp = new int[2];
        private int dxDrag = 0;
        private int dyDrag = 0;
        private final Rect dragStart = new Rect();
        private final int[] dragPos = new int[2];

        public Item() {
            this.isDragFrameVisible = false;
            this.snapThreshold = InputContainerView.this.getContext().getResources().getDimensionPixelSize(R.dimen.movable_keyboard_docking_threshold);
            this.inputLayout = InputLayout.create(InputContainerView.this.getContext(), InputContainerView.this.useWings);
            if (this.inputLayout.hasInternalDragFrame()) {
                this.inputLayout.showDragFrame(false);
                this.inputLayout.setDragListener(this);
            } else {
                this.dragFrame = (DragFrame) inflate(R.layout.input_drag_frame);
                this.dragFrame.setDragListener(this);
            }
            InputContainerView.this.addView(this.inputLayout);
            this.rootView = this.inputLayout;
            this.isDragFrameVisible = false;
            this.themeId = InputContainerView.this.app.getCurrentThemeId();
        }

        public String getTheme() {
            return this.themeId;
        }

        private View inflate(int layoutId) {
            LayoutInflater inflater = LayoutInflater.from(InputContainerView.this.getContext());
            return InputContainerView.this.app.getThemedLayoutInflater(inflater).inflate(layoutId, (ViewGroup) null);
        }

        protected View getRootView() {
            return this.rootView;
        }

        protected InputLayout getInputLayout() {
            return this.inputLayout;
        }

        public Rect getInputWindowRect() {
            return GeomUtil.getRect(this.inputLayout);
        }

        protected void removeMiniDockFrame() {
            if (this.miniDockFrame != null) {
                this.miniDockFrame.removeView(this.inputLayout);
                removeView(this.miniDockFrame);
                this.miniDockFrame = null;
                this.rootView = this.inputLayout;
                this.inputLayout.setLayoutParams(BasicViewLayout.newLayoutParams(0, 0));
                InputContainerView.this.addView(this.inputLayout);
                InputContainerView.this.requestLayout();
            }
        }

        private Drawable getMiniDockFrameBackgroundDrawable(KeyboardEx.KeyboardDockMode keyboardDockMode) {
            switch (keyboardDockMode) {
                case DOCK_LEFT:
                    return InputContainerView.this.app.getThemedDrawable(R.attr.keyboardBackgroundWingRight);
                case DOCK_RIGHT:
                    return InputContainerView.this.app.getThemedDrawable(R.attr.keyboardBackgroundWingLeft);
                default:
                    return InputContainerView.this.app.getThemedDrawable(R.attr.keyboardBackground);
            }
        }

        protected void updateMiniDockFrame() {
            int gravity;
            if (InputContainerView.this.miniDockFullAppAreaMode) {
                removeMiniDockFrame();
                return;
            }
            switch (InputContainerView.this.keyboardDockMode) {
                case DOCK_LEFT:
                    gravity = 8388611;
                    break;
                case DOCK_RIGHT:
                    gravity = 8388613;
                    break;
                default:
                    removeMiniDockFrame();
                    return;
            }
            if (this.miniDockFrame == null) {
                removeView(this.inputLayout);
                this.miniDockFrame = new FrameLayout(InputContainerView.this.getContext());
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(-2, -2);
                this.inputLayout.setLayoutParams(params);
                this.miniDockFrame.addView(this.inputLayout);
                this.miniDockFrame.setLayoutParams(BasicViewLayout.newLayoutParams(0, 0, -1, -2));
                this.rootView = this.miniDockFrame;
                InputContainerView.this.addView(this.miniDockFrame);
            }
            ((FrameLayout.LayoutParams) this.inputLayout.getLayoutParams()).gravity = gravity;
            this.miniDockFrame.setBackgroundDrawable(getMiniDockFrameBackgroundDrawable(InputContainerView.this.keyboardDockMode));
            InputContainerView.this.requestLayout();
        }

        protected void addOuterDragFrame() {
            if (!this.isDragFrameVisible) {
                int x = this.inputLayout.getLeft();
                int y = this.inputLayout.getTop();
                removeView(this.inputLayout);
                this.dragFrame.setContentView(this.inputLayout);
                InputContainerView.this.addView(this.dragFrame, BasicViewLayout.newLayoutParams(x, y));
                this.rootView = this.dragFrame;
                InputContainerView.this.requestLayout();
                this.isDragFrameVisible = true;
            }
        }

        protected void removeOuterDragFrame() {
            if (this.isDragFrameVisible) {
                int x = this.dragFrame.getLeft();
                int y = this.dragFrame.getTop();
                removeView(this.dragFrame);
                this.dragFrame.setContentView(null);
                InputContainerView.this.addView(this.inputLayout, BasicViewLayout.newLayoutParams(x, y));
                this.rootView = this.inputLayout;
                InputContainerView.this.requestLayout();
                this.isDragFrameVisible = false;
            }
        }

        public void removeDragFrame() {
            if (this.dragFrame != null) {
                removeView(this.dragFrame);
            } else if (this.inputLayout != null) {
                removeView(this.inputLayout);
            }
        }

        protected void showDragFrameOuter(boolean show) {
            if (show) {
                addOuterDragFrame();
            } else {
                removeOuterDragFrame();
            }
        }

        protected void showDragFrame(boolean show) {
            if (this.inputLayout.hasInternalDragFrame()) {
                this.inputLayout.showDragFrame(show);
            } else {
                showDragFrameOuter(show);
            }
            this.isDragFrameVisible = show;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setDragGripIsDrag(boolean isDrag, boolean enable) {
            if (this.dragFrame != null && (this.dragFrame instanceof DragFrameGripPad)) {
                ((DragFrameGripPad) this.dragFrame).setGripIsDrag(isDrag, enable);
            }
        }

        public Rect getVisibleWindowRect() {
            this.rootView.getLocationInWindow(this.posTemp);
            Rect rect = new Rect(this.posTemp[0], this.posTemp[1], this.posTemp[0] + this.rootView.getWidth(), this.posTemp[1] + this.rootView.getHeight());
            rect.top = this.inputLayout.getVisibleTopInWindow();
            return rect;
        }

        public void getVisiblePosInWindow(int[] out, int gravity) {
            this.inputLayout.getVisiblePosInWindow(out);
            int i = out[0];
            InputLayout inputLayout = this.inputLayout;
            switch (gravity & 7) {
                case 1:
                    i += inputLayout.getWidth() / 2;
                    break;
                case 5:
                    i += inputLayout.getWidth();
                    break;
            }
            out[0] = i;
            int i2 = out[1];
            InputLayout inputLayout2 = this.inputLayout;
            switch (gravity & 112) {
                case 16:
                    i2 += inputLayout2.getHeight() / 2;
                    break;
                case 80:
                    i2 += inputLayout2.getBottom();
                    break;
            }
            out[1] = i2;
        }

        protected Rect getMeasuredRootFrameDims(boolean measure) {
            if (measure) {
                Rect displayRect = InputContainerView.this.app.getDisplayRectSize(new Rect());
                this.rootView.measure(View.MeasureSpec.makeMeasureSpec(displayRect.width(), Integers.STATUS_SUCCESS), View.MeasureSpec.makeMeasureSpec(displayRect.height(), Integers.STATUS_SUCCESS));
            }
            return InputContainerView.getMeasuredRect(this.rootView);
        }

        private boolean handleDecorationAdjustment() {
            InputContainerView.this.layoutChild(this.rootView);
            boolean inputViewHeightChanged = false;
            Rect inputViewRectNow = this.inputLayout.getInputAreaRectRelativeTo(this.rootView);
            if (inputViewRectNow != null && this.inputViewRectLastLayout != null) {
                if (inputViewRectNow.height() == this.inputViewRectLastLayout.height() && inputViewRectNow.width() == this.inputViewRectLastLayout.width()) {
                    Rect rect = this.inputViewRectLastLayout;
                    int[] diff = {inputViewRectNow.left - rect.left, inputViewRectNow.top - rect.top};
                    InputContainerView.moveLayoutPos(this.rootView, -diff[0], -diff[1]);
                }
                inputViewHeightChanged = inputViewRectNow.height() != this.inputViewRectLastLayout.height();
            }
            this.inputViewRectLastLayout = inputViewRectNow;
            return inputViewHeightChanged;
        }

        public void handleLayout(boolean changed, int left, int top, int right, int bottom) {
            InputContainerView.log.d("handleLayout(): (" + left + "," + top + "); " + (right - left) + "x" + (bottom - top));
            if ((handleDecorationAdjustment() || changed) && (KeyboardEx.KeyboardDockMode.MOVABLE_MINI.equals(InputContainerView.this.keyboardDockMode) || (!InputContainerView.this.useWings && (KeyboardEx.KeyboardDockMode.DOCK_LEFT.equals(InputContainerView.this.keyboardDockMode) || KeyboardEx.KeyboardDockMode.DOCK_RIGHT.equals(InputContainerView.this.keyboardDockMode))))) {
                InputContainerView.this.restorePos();
            }
            Rect insets = this.inputLayout.getVisibleAreaInsets();
            if (this.isSnapPending) {
                setLayoutSnapToEdge(this.rootView, insets, this.snapThreshold);
                this.isSnapPending = false;
            }
            if (this.isDocked) {
                InputContainerView.this.setLayoutDocked(this.rootView, this.dockGravity);
            }
            if (PersistOp.RESTORE.equals(this.pendingPersistOp)) {
                restoreFrameLayoutPos();
                this.pendingPersistOp = PersistOp.NOTHING;
            }
            InputContainerView.this.setLayoutConfined(this.rootView, insets, 0);
            if (PersistOp.SAVE.equals(this.pendingPersistOp)) {
                saveFrameLayoutPos();
                this.pendingPersistOp = PersistOp.NOTHING;
            }
        }

        public void setDockGravity(int dockGravity) {
            if (this.dockGravity != dockGravity) {
                this.dockGravity = dockGravity;
                InputContainerView.this.requestLayout();
            }
        }

        public void setAllowedMovement(boolean allowDragX, boolean allowDragY) {
            this.allowDragX = allowDragX;
            this.allowDragY = allowDragY;
        }

        public void dragLock(boolean lock) {
            this.dragLock = lock;
            if ((InputContainerView.this.keyboardDockMode == KeyboardEx.KeyboardDockMode.MOVABLE_MINI || InputContainerView.this.keyboardDockMode == KeyboardEx.KeyboardDockMode.DOCK_LEFT || InputContainerView.this.keyboardDockMode == KeyboardEx.KeyboardDockMode.DOCK_RIGHT) && this.inputLayout.hasInternalDragFrame()) {
                this.inputLayout.setDragGripIsDrag(true, !lock);
            }
        }

        public void setDocked(boolean dock) {
            if (this.isDocked != dock) {
                this.isDocked = dock;
                InputContainerView.this.requestLayout();
            }
        }

        private void saveToPrefs(int orientation, KeyboardEx.KeyboardDockMode mode, int x, int y, int bottom) {
            AppPreferences.from(InputContainerView.this.getContext()).setInt(InputContainerView.getPrefKeyX(orientation, InputContainerView.this.keyboardDockMode), x);
            AppPreferences.from(InputContainerView.this.getContext()).setInt(InputContainerView.getPrefKeyY(orientation, InputContainerView.this.keyboardDockMode), y);
            AppPreferences.from(InputContainerView.this.getContext()).setInt(InputContainerView.getPrefKeyBottom(orientation, InputContainerView.this.keyboardDockMode), bottom);
        }

        private int getPrefX(int orientation, KeyboardEx.KeyboardDockMode dockMode) {
            return InputContainerView.this.app.getAppPreferences().getInt(InputContainerView.getPrefKeyX(orientation, dockMode), -1);
        }

        private int getPrefY(int orientation, KeyboardEx.KeyboardDockMode dockMode) {
            return InputContainerView.this.app.getAppPreferences().getInt(InputContainerView.getPrefKeyY(orientation, dockMode), -1);
        }

        private int getPrefBottom(int orientation, KeyboardEx.KeyboardDockMode dockMode) {
            return InputContainerView.this.app.getAppPreferences().getInt(InputContainerView.getPrefKeyBottom(orientation, dockMode), -1);
        }

        private void saveFrameLayoutPos() {
            if (this.rootView.isLayoutRequested()) {
                InputContainerView.log.d("saveFrameLayoutPos(): waiting for layout");
                this.pendingPersistOp = PersistOp.SAVE;
                return;
            }
            int orientation = InputContainerView.this.getContext().getResources().getConfiguration().orientation;
            BasicViewLayout.LayoutParams params = (BasicViewLayout.LayoutParams) this.rootView.getLayoutParams();
            int x = params.x;
            int y = params.y;
            int bottom = y + this.rootView.getMeasuredHeight();
            InputContainerView.log.d("isLayoutRequested(): or: " + orientation + "; x: " + x + "; y: " + y, " bottom: ", Integer.valueOf(bottom));
            saveToPrefs(orientation, InputContainerView.this.keyboardDockMode, x, y, bottom);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void restoreFrameLayoutPos() {
            int bottom;
            if (this.rootView.isLayoutRequested()) {
                this.pendingPersistOp = PersistOp.RESTORE;
                return;
            }
            int orientation = InputContainerView.this.getContext().getResources().getConfiguration().orientation;
            int x = getPrefX(orientation, InputContainerView.this.keyboardDockMode);
            int y = getPrefY(orientation, InputContainerView.this.keyboardDockMode);
            if (this.allowDragY) {
                bottom = getPrefBottom(orientation, InputContainerView.this.keyboardDockMode);
            } else {
                bottom = InputContainerView.this.getHeight();
            }
            InputContainerView.log.d("restoreFramePos(): or: " + orientation + "; x: " + x + "; y: " + y, " bottom:", Integer.valueOf(bottom));
            if (x != -1 && y != -1) {
                if (!this.allowDragX) {
                    x = ((BasicViewLayout.LayoutParams) this.inputLayout.getLayoutParams()).x;
                }
                InputContainerView.this.setLayoutPosConfineBot(this.rootView, this.inputLayout.getVisibleAreaInsets(), x, bottom);
                this.isDocked = false;
            }
        }

        private void restoreFrameLayoutPosMiniKeyboard(int height) {
            if (KeyboardEx.KeyboardDockMode.MOVABLE_MINI.equals(InputContainerView.this.keyboardDockMode)) {
                int orientation = InputContainerView.this.getContext().getResources().getConfiguration().orientation;
                Rect parent = InputContainerView.this.getDimsRect();
                int x = getPrefX(orientation, InputContainerView.this.keyboardDockMode);
                int y = parent.height() - height;
                InputContainerView.log.d("restoreFrameLayoutPosMiniKeyboard(): ", " x: ", Integer.valueOf(x), "; y: ", Integer.valueOf(y));
                if (y > 0) {
                    BasicViewLayout.setPos(this.rootView, x, y);
                }
            }
        }

        public void moveFrameBy(int dx, int dy) {
            this.isDocked = false;
            InputContainerView.this.moveConfine(this.rootView, this.inputLayout.getVisibleAreaInsets(), dx, dy);
        }

        private int[] confineDragMovement(int dx, int dy) {
            this.dragPos[0] = this.dragStart.left;
            this.dragPos[1] = this.dragStart.top;
            InputContainerView.this.adjustConfine(this.dragPos, this.dragStart.width(), this.dragStart.height(), this.inputLayout.getVisibleAreaInsets(), dx, dy);
            int[] iArr = this.dragPos;
            iArr[0] = iArr[0] - this.dragStart.left;
            int[] iArr2 = this.dragPos;
            iArr2[1] = iArr2[1] - this.dragStart.top;
            return this.dragPos;
        }

        @Override // com.nuance.swype.input.view.DragFrame.DragFrameListener
        public void onDragBegin(View contentView) {
            if (!this.dragLock) {
                View view = this.rootView;
                this.dragStart.set(view.getLeft(), view.getTop(), view.getLeft() + view.getWidth(), view.getHeight() + view.getTop());
                this.dxDrag = 0;
                this.dyDrag = 0;
                this.isDocked = false;
                this.inputLayout.onBeginDrag();
            }
        }

        @Override // com.nuance.swype.input.view.DragFrame.DragFrameListener
        public void onDrag(View contentView, int dx, int dy) {
            if (!this.dragLock) {
                this.dxDrag = (this.allowDragX ? dx : 0) + this.dxDrag;
                this.dyDrag = (this.allowDragY ? dy : 0) + this.dyDrag;
                if (ViewCompat.supports2dTranslation()) {
                    int[] delta = confineDragMovement(this.dxDrag, this.dyDrag);
                    this.dxDrag = delta[0];
                    this.dyDrag = delta[1];
                    ViewCompat.setTranslation(this.rootView, this.dxDrag, this.dyDrag);
                } else {
                    moveFrameBy(dx, dy);
                }
                InputLayout inputLayout = this.inputLayout;
                if (!this.allowDragX) {
                    dx = 0;
                }
                if (!this.allowDragY) {
                    dy = 0;
                }
                inputLayout.onDrag(dx, dy);
            }
        }

        @Override // com.nuance.swype.input.view.DragFrame.DragFrameListener
        public void onDragEnd(View contentView) {
            if (!this.dragLock) {
                if (ViewCompat.supports2dTranslation()) {
                    ViewCompat.setTranslation(this.rootView, 0.0f, 0.0f);
                    moveFrameBy(this.dxDrag, this.dyDrag);
                }
                this.dxDrag = 0;
                this.dyDrag = 0;
                this.inputLayout.onEndDrag();
                snap();
                saveFrameLayoutPos();
            }
        }

        protected void snap() {
            if (!this.inputLayout.isLayoutRequested()) {
                Rect insets = this.inputLayout.getVisibleAreaInsets();
                snapToEdge(this.rootView, insets, this.snapThreshold);
            } else {
                this.isSnapPending = true;
            }
        }

        public void showCandidates(boolean shown) {
            this.inputLayout.showCandidates(shown);
        }

        public void showShadow(boolean show) {
            this.inputLayout.showShadow(show);
        }

        public void showInput(boolean shown) {
            this.inputLayout.showInput(shown);
        }

        public boolean isCoverShowing() {
            return this.inputLayout.getCoverView() != null;
        }

        public void setCover(View coverView, boolean enableAnim) {
            ViewUtil.resetView(coverView);
            this.inputLayout.setCoverView(coverView, enableAnim);
        }

        public void setCover(View coverView, boolean enableAnim, int width, int height) {
            ViewUtil.resetView(coverView);
            this.inputLayout.setCoverView(coverView, enableAnim, width, height);
        }

        @Deprecated
        protected int getInputLayoutHeight() {
            return this.inputLayout.getHeight();
        }

        public void setCandidatesView(View candidatesView) {
            ViewUtil.resetView(candidatesView);
            this.inputLayout.setCandidatesView(candidatesView);
        }

        public View getCandidatesView() {
            if (this.inputLayout != null) {
                return this.inputLayout.getCandidatesView();
            }
            return null;
        }

        public void setInputView(View inputView) {
            ViewUtil.resetView(inputView);
            this.inputLayout.setInputView(inputView);
        }

        public View getInputView() {
            if (this.inputLayout != null) {
                return this.inputLayout.getInputView();
            }
            return null;
        }

        public void removeView(View view) {
            FrameworkUtil.disownParent(view);
        }

        protected void setLayoutSnapToEdge(View view, Rect insets, int threshold) {
            Rect frame = InputContainerView.getMeasuredRect(view);
            if (insets != null) {
                GeomUtil.shrink(frame, insets);
            }
            Rect container = InputContainerView.this.getDimsRect();
            int dx = GeomUtil.getSnapDistHor(frame, container, threshold);
            int dy = GeomUtil.getSnapDistVer(frame, container, threshold);
            if (dx != 0 || dy != 0) {
                InputContainerView.moveLayoutPos(this.rootView, dx, dy);
                if (!this.inputLayout.isLayoutRequested()) {
                    this.inputLayout.onSnapToEdge(dx, dy);
                }
            }
        }

        protected void snapToEdge(View view, Rect insets, int threshold) {
            Rect frame = GeomUtil.getRect(view);
            if (insets != null) {
                GeomUtil.shrink(frame, insets);
            }
            Rect container = InputContainerView.this.getDimsRect();
            int dx = GeomUtil.getSnapDistHor(frame, container, threshold);
            int dy = GeomUtil.getSnapDistVer(frame, container, threshold);
            if (dx != 0 || dy != 0) {
                moveFrameBy(dx, dy);
                if (!this.inputLayout.isLayoutRequested()) {
                    this.inputLayout.onSnapToEdge(dx, dy);
                }
            }
        }

        @Override // com.nuance.swype.input.view.DragFrame.DragFrameListener
        public void onClick(View contentView) {
            InputContainerView.log.d("onClick");
        }
    }

    public void setFullScreenMode(boolean isFullScreenMode) {
        this.isFullScreenMode = isFullScreenMode;
        if (this.item != null) {
            this.item.getInputLayout().showShadow(!isFullScreenMode);
        }
    }

    public Rect getVisibleWindowRect() {
        return this.item.getVisibleWindowRect();
    }

    public int getInputLayoutHeight() {
        return this.item.getInputLayout().getHeight();
    }

    public void getVisiblePosInWindow(int[] out, int gravity) {
        if (this.item != null) {
            this.item.getVisiblePosInWindow(out, gravity);
        }
    }

    @Override // android.view.View
    protected int getSuggestedMinimumHeight() {
        int i;
        if (this.item != null && this.isFullScreenMode) {
            Rect dims = this.item.getMeasuredRootFrameDims(false);
            log.d("measureRootFrame(): " + dims.toShortString());
            return dims.height();
        }
        Context context = getContext();
        int displayHeight = IMEApplication.from(context).getDisplayHeight();
        Resources resources = context.getResources();
        int identifier = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (identifier > 0) {
            i = resources.getDimensionPixelSize(identifier);
        } else {
            Rect rect = new Rect();
            getWindowVisibleDisplayFrame(rect);
            i = rect.top;
        }
        return displayHeight - i;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.view.BasicViewLayout, android.view.ViewGroup, android.view.View
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (this.item != null) {
            this.item.handleLayout(changed, left, top, right, bottom);
        }
        super.onLayout(changed, left, top, right, bottom);
    }

    public void setDockGravity(int dockGravity) {
        if (this.item != null) {
            this.item.setDockGravity(dockGravity);
        }
    }

    public boolean isCoverShowing() {
        return this.item.isCoverShowing();
    }

    public void setCover(View view, boolean enableAnim) {
        this.item.setCover(view, enableAnim);
    }

    public void setCover(View view, boolean enableAnim, int width, int height) {
        this.item.setCover(view, enableAnim, width, height);
    }

    public void setDocked(boolean dock) {
        if (this.item != null) {
            this.item.setDocked(dock);
        }
    }

    public void showDragFrame(boolean show) {
        if (this.item != null) {
            this.item.showDragFrame(show);
        }
    }

    private void setDragGripIsDrag(boolean isDrag, boolean enable) {
        if (this.item != null) {
            this.item.setDragGripIsDrag(isDrag, enable);
        }
    }

    public void setAllowedMovement(boolean allowDragX, boolean allowDragY) {
        if (this.item != null) {
            this.item.setAllowedMovement(allowDragX, allowDragY);
        }
    }

    public boolean isFullAppAreaMode() {
        return KeyboardEx.KeyboardDockMode.MOVABLE_MINI.equals(this.keyboardDockMode) || this.item.isDragFrameVisible;
    }

    private void initModeHelper(int gravity) {
        setDocked(true);
        setDockGravity(gravity);
    }

    private void initModeDraggable(int gravity) {
        initModeHelper(gravity);
        showDragFrame(true);
        setDragGripIsDrag(true, true);
        restorePos();
    }

    private void initModeFixed(int gravity, boolean showGripPad) {
        log.d("initModeFixed(): use wings: ", Boolean.valueOf(this.useWings), "; show grip: ", Boolean.valueOf(showGripPad));
        initModeHelper(gravity);
        if (this.useWings) {
            showDragFrame(false);
            return;
        }
        showDragFrame(showGripPad);
        setDragGripIsDrag(false, true);
        restorePos();
    }

    public void setMode(KeyboardEx.KeyboardDockMode mode) {
        this.keyboardDockMode = mode;
        if (AdsUtil.sAdsSupported) {
            this.app.getAdSessionTracker().setKeyboardMode(this.keyboardDockMode);
            if (!this.app.getBillboardManager().canShowBillboard()) {
                this.app.getBillboardManager().hide();
            }
        }
        if (KeyboardEx.KeyboardDockMode.MOVABLE_MINI.equals(mode)) {
            initModeDraggable(81);
        } else if (KeyboardEx.KeyboardDockMode.DOCK_LEFT.equals(mode)) {
            if (this.miniDockFullAppAreaMode) {
                initModeFixed(8388691, false);
            } else {
                initModeFixed(81, false);
            }
        } else if (KeyboardEx.KeyboardDockMode.DOCK_RIGHT.equals(mode)) {
            if (this.miniDockFullAppAreaMode) {
                initModeFixed(8388693, false);
            } else {
                initModeFixed(81, false);
            }
        } else {
            initModeFixed(81, false);
        }
        this.item.updateMiniDockFrame();
        if (this.isFullScreenMode) {
            setBackgroundColor(-16777216);
        } else {
            ViewCompat.setBackground(this, null);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void restorePos() {
        if (this.item != null) {
            this.item.restoreFrameLayoutPos();
        }
    }

    public void showCandidates(boolean shown) {
        if (this.item != null) {
            this.item.showCandidates(shown);
        }
    }

    public void showInputArea(boolean shown) {
        if (this.item != null) {
            this.item.showInput(shown);
        }
    }

    public void dragLock(boolean lock) {
        if (this.item != null) {
            this.item.dragLock(lock);
        }
    }

    public Region getWidgetViewTouchableRegion() {
        if (this.widgetViews.size() > 0) {
            Region region = new Region();
            for (View widget : this.widgetViews) {
                if (widget != null && widget.isShown()) {
                    Rect rec = getOverlayRect(widget);
                    region.op(rec, region, Region.Op.UNION);
                }
            }
            if (!region.isEmpty()) {
                return region;
            }
        }
        return null;
    }

    public Rect getOverlayRect(View view) {
        Rect rc = GeomUtil.getRect(view);
        int[] widgetViewPos = CoordUtils.getWindowPos(view);
        int[] containerViewPos = CoordUtils.getWindowPos(this);
        CoordUtils.subtract(widgetViewPos, containerViewPos, widgetViewPos);
        GeomUtil.moveRectTo(rc, widgetViewPos);
        return rc;
    }

    public void addWidgetView(View view) {
        FrameworkUtil.disownParent(view);
        removeView(this.item.getRootView());
        addView(view);
        addView(this.item.getRootView());
        this.widgetViews.add(view);
    }

    public boolean isActiveWidgetView(View view) {
        return view != null && this.widgetViews.contains(view);
    }

    public void removeActiveWidegtView(View view) {
        this.widgetViews.remove(view);
    }

    public void moveWidgetView(View v, Rect rc) {
        OverlayView.setGeometry(v, rc);
    }

    public void showWidgetView(View v) {
        if (v != null) {
            this.widgetViews.add(v);
            v.setVisibility(0);
        }
    }

    public void hideWidgetView(View v) {
        if (v != null) {
            v.setVisibility(8);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.view.BasicViewLayout, android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.overlayView != null) {
            this.overlayView.attach(this);
        }
    }

    public final OverlayView getOverlayViewCreate() {
        if (this.overlayView == null) {
            this.overlayView = new OverlayView(getContext(), null);
            if (getWindowToken() != null) {
                this.overlayView.attach(this);
            }
        }
        return this.overlayView;
    }

    public void detachOverlayView() {
        if (this.overlayView != null) {
            this.overlayView.detach();
            this.overlayView = null;
            this.popupViewManager = null;
        }
    }

    public final PopupViewManager getPopupViewManagerCreate() {
        if (this.popupViewManager == null) {
            this.popupViewManager = new PopupViewManager(getContext(), getOverlayViewCreate());
        }
        return this.popupViewManager;
    }

    public final PopupViewManager getPopupViewManager() {
        return this.popupViewManager;
    }

    public void invalidateItem() {
        if (this.item != null && this.item.getRootView() != null) {
            this.item.getRootView().invalidate();
        }
    }
}
