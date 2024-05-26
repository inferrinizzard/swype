package android.support.v7.widget;

import android.R;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.TintableBackgroundView;
import android.support.v4.view.ViewCompat;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.view.menu.ShowableListMenu;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.ThemedSpinnerAdapter;

/* loaded from: classes.dex */
public class AppCompatSpinner extends Spinner implements TintableBackgroundView {
    private static final int[] ATTRS_ANDROID_SPINNERMODE;
    private static final boolean IS_AT_LEAST_JB;
    private static final boolean IS_AT_LEAST_M;
    private AppCompatBackgroundHelper mBackgroundTintHelper;
    private AppCompatDrawableManager mDrawableManager;
    private int mDropDownWidth;
    private ForwardingListener mForwardingListener;
    private DropdownPopup mPopup;
    private Context mPopupContext;
    private boolean mPopupSet;
    private SpinnerAdapter mTempAdapter;
    private final Rect mTempRect;

    static {
        IS_AT_LEAST_M = Build.VERSION.SDK_INT >= 23;
        IS_AT_LEAST_JB = Build.VERSION.SDK_INT >= 16;
        ATTRS_ANDROID_SPINNERMODE = new int[]{R.attr.spinnerMode};
    }

    public AppCompatSpinner(Context context) {
        this(context, null);
    }

    public AppCompatSpinner(Context context, AttributeSet attrs) {
        this(context, attrs, android.support.v7.appcompat.R.attr.spinnerStyle);
    }

    public AppCompatSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, (byte) 0);
    }

    private AppCompatSpinner(Context context, AttributeSet attrs, int defStyleAttr, byte b) {
        this(context, attrs, defStyleAttr, -1);
    }

    private AppCompatSpinner(Context context, AttributeSet attrs, int defStyleAttr, int mode) {
        super(context, attrs, defStyleAttr);
        Context context2;
        AppCompatSpinner appCompatSpinner;
        this.mTempRect = new Rect();
        TintTypedArray a = TintTypedArray.obtainStyledAttributes(context, attrs, android.support.v7.appcompat.R.styleable.Spinner, defStyleAttr, 0);
        this.mDrawableManager = AppCompatDrawableManager.get();
        this.mBackgroundTintHelper = new AppCompatBackgroundHelper(this, this.mDrawableManager);
        int popupThemeResId = a.getResourceId(android.support.v7.appcompat.R.styleable.Spinner_popupTheme, 0);
        if (popupThemeResId != 0) {
            context2 = new ContextThemeWrapper(context, popupThemeResId);
            appCompatSpinner = this;
        } else if (IS_AT_LEAST_M) {
            context2 = null;
            appCompatSpinner = this;
        } else {
            context2 = context;
            appCompatSpinner = this;
        }
        appCompatSpinner.mPopupContext = context2;
        if (this.mPopupContext != null) {
            if (Build.VERSION.SDK_INT >= 11) {
                TypedArray aa = null;
                try {
                    try {
                        aa = context.obtainStyledAttributes(attrs, ATTRS_ANDROID_SPINNERMODE, defStyleAttr, 0);
                        mode = aa.hasValue(0) ? aa.getInt(0, 0) : mode;
                    } catch (Exception e) {
                        Log.i("AppCompatSpinner", "Could not read android:spinnerMode", e);
                        if (aa != null) {
                            aa.recycle();
                        }
                    }
                } finally {
                    if (aa != null) {
                        aa.recycle();
                    }
                }
            } else {
                mode = 1;
            }
            if (mode == 1) {
                final DropdownPopup popup = new DropdownPopup(this.mPopupContext, attrs, defStyleAttr);
                TintTypedArray pa = TintTypedArray.obtainStyledAttributes(this.mPopupContext, attrs, android.support.v7.appcompat.R.styleable.Spinner, defStyleAttr, 0);
                this.mDropDownWidth = pa.getLayoutDimension(android.support.v7.appcompat.R.styleable.Spinner_android_dropDownWidth, -2);
                popup.setBackgroundDrawable(pa.getDrawable(android.support.v7.appcompat.R.styleable.Spinner_android_popupBackground));
                popup.mHintText = a.mWrapped.getString(android.support.v7.appcompat.R.styleable.Spinner_android_prompt);
                pa.mWrapped.recycle();
                this.mPopup = popup;
                this.mForwardingListener = new ForwardingListener(this) { // from class: android.support.v7.widget.AppCompatSpinner.1
                    @Override // android.support.v7.widget.ForwardingListener
                    public final ShowableListMenu getPopup() {
                        return popup;
                    }

                    @Override // android.support.v7.widget.ForwardingListener
                    public final boolean onForwardingStarted() {
                        if (!AppCompatSpinner.this.mPopup.mPopup.isShowing()) {
                            AppCompatSpinner.this.mPopup.show();
                            return true;
                        }
                        return true;
                    }
                };
            }
        }
        CharSequence[] entries = a.mWrapped.getTextArray(android.support.v7.appcompat.R.styleable.Spinner_android_entries);
        if (entries != null) {
            ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(context, R.layout.simple_spinner_item, entries);
            adapter.setDropDownViewResource(android.support.v7.appcompat.R.layout.support_simple_spinner_dropdown_item);
            setAdapter((SpinnerAdapter) adapter);
        }
        a.mWrapped.recycle();
        this.mPopupSet = true;
        if (this.mTempAdapter != null) {
            setAdapter(this.mTempAdapter);
            this.mTempAdapter = null;
        }
        this.mBackgroundTintHelper.loadFromAttributes(attrs, defStyleAttr);
    }

    @Override // android.widget.Spinner
    public Context getPopupContext() {
        if (this.mPopup != null) {
            return this.mPopupContext;
        }
        if (IS_AT_LEAST_M) {
            return super.getPopupContext();
        }
        return null;
    }

    @Override // android.widget.Spinner
    public void setPopupBackgroundDrawable(Drawable background) {
        if (this.mPopup != null) {
            this.mPopup.setBackgroundDrawable(background);
        } else if (IS_AT_LEAST_JB) {
            super.setPopupBackgroundDrawable(background);
        }
    }

    @Override // android.widget.Spinner
    public void setPopupBackgroundResource(int resId) {
        setPopupBackgroundDrawable(ContextCompat.getDrawable(getPopupContext(), resId));
    }

    @Override // android.widget.Spinner
    public Drawable getPopupBackground() {
        if (this.mPopup == null) {
            if (IS_AT_LEAST_JB) {
                return super.getPopupBackground();
            }
            return null;
        }
        return this.mPopup.mPopup.getBackground();
    }

    @Override // android.widget.Spinner
    public void setDropDownVerticalOffset(int pixels) {
        if (this.mPopup != null) {
            this.mPopup.setVerticalOffset(pixels);
        } else if (IS_AT_LEAST_JB) {
            super.setDropDownVerticalOffset(pixels);
        }
    }

    @Override // android.widget.Spinner
    public int getDropDownVerticalOffset() {
        if (this.mPopup != null) {
            return this.mPopup.getVerticalOffset();
        }
        if (IS_AT_LEAST_JB) {
            return super.getDropDownVerticalOffset();
        }
        return 0;
    }

    @Override // android.widget.Spinner
    public void setDropDownHorizontalOffset(int pixels) {
        if (this.mPopup == null) {
            if (IS_AT_LEAST_JB) {
                super.setDropDownHorizontalOffset(pixels);
                return;
            }
            return;
        }
        this.mPopup.mDropDownHorizontalOffset = pixels;
    }

    @Override // android.widget.Spinner
    public int getDropDownHorizontalOffset() {
        if (this.mPopup == null) {
            if (IS_AT_LEAST_JB) {
                return super.getDropDownHorizontalOffset();
            }
            return 0;
        }
        return this.mPopup.mDropDownHorizontalOffset;
    }

    @Override // android.widget.Spinner
    public void setDropDownWidth(int pixels) {
        if (this.mPopup != null) {
            this.mDropDownWidth = pixels;
        } else if (IS_AT_LEAST_JB) {
            super.setDropDownWidth(pixels);
        }
    }

    @Override // android.widget.Spinner
    public int getDropDownWidth() {
        if (this.mPopup != null) {
            return this.mDropDownWidth;
        }
        if (IS_AT_LEAST_JB) {
            return super.getDropDownWidth();
        }
        return 0;
    }

    @Override // android.widget.AdapterView
    public void setAdapter(SpinnerAdapter adapter) {
        if (!this.mPopupSet) {
            this.mTempAdapter = adapter;
            return;
        }
        super.setAdapter(adapter);
        if (this.mPopup != null) {
            Context popupContext = this.mPopupContext == null ? getContext() : this.mPopupContext;
            this.mPopup.setAdapter(new DropDownAdapter(adapter, popupContext.getTheme()));
        }
    }

    @Override // android.widget.Spinner, android.widget.AdapterView, android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.mPopup != null && this.mPopup.mPopup.isShowing()) {
            this.mPopup.dismiss();
        }
    }

    @Override // android.widget.Spinner, android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        if (this.mForwardingListener == null || !this.mForwardingListener.onTouch(this, event)) {
            return super.onTouchEvent(event);
        }
        return true;
    }

    @Override // android.widget.Spinner, android.widget.AbsSpinner, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (this.mPopup != null && View.MeasureSpec.getMode(widthMeasureSpec) == Integer.MIN_VALUE) {
            int measuredWidth = getMeasuredWidth();
            setMeasuredDimension(Math.min(Math.max(measuredWidth, compatMeasureContentWidth(getAdapter(), getBackground())), View.MeasureSpec.getSize(widthMeasureSpec)), getMeasuredHeight());
        }
    }

    @Override // android.widget.Spinner, android.view.View
    public boolean performClick() {
        if (this.mPopup == null) {
            return super.performClick();
        }
        if (!this.mPopup.mPopup.isShowing()) {
            this.mPopup.show();
        }
        return true;
    }

    @Override // android.widget.Spinner
    public void setPrompt(CharSequence prompt) {
        if (this.mPopup == null) {
            super.setPrompt(prompt);
        } else {
            this.mPopup.mHintText = prompt;
        }
    }

    @Override // android.widget.Spinner
    public CharSequence getPrompt() {
        return this.mPopup != null ? this.mPopup.mHintText : super.getPrompt();
    }

    @Override // android.view.View
    public void setBackgroundResource(int resId) {
        super.setBackgroundResource(resId);
        if (this.mBackgroundTintHelper != null) {
            this.mBackgroundTintHelper.onSetBackgroundResource(resId);
        }
    }

    @Override // android.view.View
    public void setBackgroundDrawable(Drawable background) {
        super.setBackgroundDrawable(background);
        if (this.mBackgroundTintHelper == null) {
            return;
        }
        this.mBackgroundTintHelper.setInternalBackgroundTint(null);
    }

    @Override // android.support.v4.view.TintableBackgroundView
    public void setSupportBackgroundTintList(ColorStateList tint) {
        if (this.mBackgroundTintHelper != null) {
            this.mBackgroundTintHelper.setSupportBackgroundTintList(tint);
        }
    }

    @Override // android.support.v4.view.TintableBackgroundView
    public ColorStateList getSupportBackgroundTintList() {
        if (this.mBackgroundTintHelper != null) {
            return this.mBackgroundTintHelper.getSupportBackgroundTintList();
        }
        return null;
    }

    @Override // android.support.v4.view.TintableBackgroundView
    public void setSupportBackgroundTintMode(PorterDuff.Mode tintMode) {
        if (this.mBackgroundTintHelper != null) {
            this.mBackgroundTintHelper.setSupportBackgroundTintMode(tintMode);
        }
    }

    @Override // android.support.v4.view.TintableBackgroundView
    public PorterDuff.Mode getSupportBackgroundTintMode() {
        if (this.mBackgroundTintHelper != null) {
            return this.mBackgroundTintHelper.getSupportBackgroundTintMode();
        }
        return null;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (this.mBackgroundTintHelper != null) {
            this.mBackgroundTintHelper.applySupportBackgroundTint();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int compatMeasureContentWidth(SpinnerAdapter adapter, Drawable background) {
        if (adapter == null) {
            return 0;
        }
        int width = 0;
        View itemView = null;
        int itemType = 0;
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), 0);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), 0);
        int start = Math.max(0, getSelectedItemPosition());
        int end = Math.min(adapter.getCount(), start + 15);
        int count = end - start;
        for (int i = Math.max(0, start - (15 - count)); i < end; i++) {
            int positionType = adapter.getItemViewType(i);
            if (positionType != itemType) {
                itemType = positionType;
                itemView = null;
            }
            itemView = adapter.getView(i, itemView, this);
            if (itemView.getLayoutParams() == null) {
                itemView.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
            }
            itemView.measure(widthMeasureSpec, heightMeasureSpec);
            width = Math.max(width, itemView.getMeasuredWidth());
        }
        if (background != null) {
            background.getPadding(this.mTempRect);
            return width + this.mTempRect.left + this.mTempRect.right;
        }
        return width;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class DropDownAdapter implements ListAdapter, SpinnerAdapter {
        private SpinnerAdapter mAdapter;
        private ListAdapter mListAdapter;

        public DropDownAdapter(SpinnerAdapter adapter, Resources.Theme dropDownTheme) {
            this.mAdapter = adapter;
            if (adapter instanceof ListAdapter) {
                this.mListAdapter = (ListAdapter) adapter;
            }
            if (dropDownTheme != null && AppCompatSpinner.IS_AT_LEAST_M && (adapter instanceof ThemedSpinnerAdapter)) {
                ThemedSpinnerAdapter themedAdapter = (ThemedSpinnerAdapter) adapter;
                if (themedAdapter.getDropDownViewTheme() != dropDownTheme) {
                    themedAdapter.setDropDownViewTheme(dropDownTheme);
                }
            }
        }

        @Override // android.widget.Adapter
        public final int getCount() {
            if (this.mAdapter == null) {
                return 0;
            }
            return this.mAdapter.getCount();
        }

        @Override // android.widget.Adapter
        public final Object getItem(int position) {
            if (this.mAdapter == null) {
                return null;
            }
            return this.mAdapter.getItem(position);
        }

        @Override // android.widget.Adapter
        public final long getItemId(int position) {
            if (this.mAdapter == null) {
                return -1L;
            }
            return this.mAdapter.getItemId(position);
        }

        @Override // android.widget.Adapter
        public final View getView(int position, View convertView, ViewGroup parent) {
            return getDropDownView(position, convertView, parent);
        }

        @Override // android.widget.SpinnerAdapter
        public final View getDropDownView(int position, View convertView, ViewGroup parent) {
            if (this.mAdapter == null) {
                return null;
            }
            return this.mAdapter.getDropDownView(position, convertView, parent);
        }

        @Override // android.widget.Adapter
        public final boolean hasStableIds() {
            return this.mAdapter != null && this.mAdapter.hasStableIds();
        }

        @Override // android.widget.Adapter
        public final void registerDataSetObserver(DataSetObserver observer) {
            if (this.mAdapter != null) {
                this.mAdapter.registerDataSetObserver(observer);
            }
        }

        @Override // android.widget.Adapter
        public final void unregisterDataSetObserver(DataSetObserver observer) {
            if (this.mAdapter != null) {
                this.mAdapter.unregisterDataSetObserver(observer);
            }
        }

        @Override // android.widget.ListAdapter
        public final boolean areAllItemsEnabled() {
            ListAdapter adapter = this.mListAdapter;
            if (adapter != null) {
                return adapter.areAllItemsEnabled();
            }
            return true;
        }

        @Override // android.widget.ListAdapter
        public final boolean isEnabled(int position) {
            ListAdapter adapter = this.mListAdapter;
            if (adapter != null) {
                return adapter.isEnabled(position);
            }
            return true;
        }

        @Override // android.widget.Adapter
        public final int getItemViewType(int position) {
            return 0;
        }

        @Override // android.widget.Adapter
        public final int getViewTypeCount() {
            return 1;
        }

        @Override // android.widget.Adapter
        public final boolean isEmpty() {
            return getCount() == 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class DropdownPopup extends ListPopupWindow {
        private ListAdapter mAdapter;
        CharSequence mHintText;
        private final Rect mVisibleRect;

        public DropdownPopup(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            this.mVisibleRect = new Rect();
            this.mDropDownAnchorView = AppCompatSpinner.this;
            setModal$1385ff();
            this.mPromptPosition = 0;
            this.mItemClickListener = new AdapterView.OnItemClickListener() { // from class: android.support.v7.widget.AppCompatSpinner.DropdownPopup.1
                @Override // android.widget.AdapterView.OnItemClickListener
                public final void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    AppCompatSpinner.this.setSelection(position);
                    if (AppCompatSpinner.this.getOnItemClickListener() != null) {
                        AppCompatSpinner.this.performItemClick(v, position, DropdownPopup.this.mAdapter.getItemId(position));
                    }
                    DropdownPopup.this.dismiss();
                }
            };
        }

        @Override // android.support.v7.widget.ListPopupWindow
        public final void setAdapter(ListAdapter adapter) {
            super.setAdapter(adapter);
            this.mAdapter = adapter;
        }

        final void computeContentWidth() {
            int hOffset;
            Drawable background = this.mPopup.getBackground();
            int hOffset2 = 0;
            if (background != null) {
                background.getPadding(AppCompatSpinner.this.mTempRect);
                hOffset2 = ViewUtils.isLayoutRtl(AppCompatSpinner.this) ? AppCompatSpinner.this.mTempRect.right : -AppCompatSpinner.this.mTempRect.left;
            } else {
                Rect rect = AppCompatSpinner.this.mTempRect;
                AppCompatSpinner.this.mTempRect.right = 0;
                rect.left = 0;
            }
            int spinnerPaddingLeft = AppCompatSpinner.this.getPaddingLeft();
            int spinnerPaddingRight = AppCompatSpinner.this.getPaddingRight();
            int spinnerWidth = AppCompatSpinner.this.getWidth();
            if (AppCompatSpinner.this.mDropDownWidth == -2) {
                int contentWidth = AppCompatSpinner.this.compatMeasureContentWidth((SpinnerAdapter) this.mAdapter, this.mPopup.getBackground());
                int contentWidthLimit = (AppCompatSpinner.this.getContext().getResources().getDisplayMetrics().widthPixels - AppCompatSpinner.this.mTempRect.left) - AppCompatSpinner.this.mTempRect.right;
                if (contentWidth > contentWidthLimit) {
                    contentWidth = contentWidthLimit;
                }
                setContentWidth(Math.max(contentWidth, (spinnerWidth - spinnerPaddingLeft) - spinnerPaddingRight));
            } else if (AppCompatSpinner.this.mDropDownWidth != -1) {
                setContentWidth(AppCompatSpinner.this.mDropDownWidth);
            } else {
                setContentWidth((spinnerWidth - spinnerPaddingLeft) - spinnerPaddingRight);
            }
            if (ViewUtils.isLayoutRtl(AppCompatSpinner.this)) {
                hOffset = hOffset2 + ((spinnerWidth - spinnerPaddingRight) - this.mDropDownWidth);
            } else {
                hOffset = hOffset2 + spinnerPaddingLeft;
            }
            this.mDropDownHorizontalOffset = hOffset;
        }

        @Override // android.support.v7.widget.ListPopupWindow, android.support.v7.view.menu.ShowableListMenu
        public final void show() {
            ViewTreeObserver vto;
            boolean wasShowing = this.mPopup.isShowing();
            computeContentWidth();
            setInputMethodMode$13462e();
            super.show();
            this.mDropDownList.setChoiceMode(1);
            int selectedItemPosition = AppCompatSpinner.this.getSelectedItemPosition();
            DropDownListView dropDownListView = this.mDropDownList;
            if (this.mPopup.isShowing() && dropDownListView != null) {
                dropDownListView.setListSelectionHidden(false);
                dropDownListView.setSelection(selectedItemPosition);
                if (Build.VERSION.SDK_INT >= 11 && dropDownListView.getChoiceMode() != 0) {
                    dropDownListView.setItemChecked(selectedItemPosition, true);
                }
            }
            if (!wasShowing && (vto = AppCompatSpinner.this.getViewTreeObserver()) != null) {
                final ViewTreeObserver.OnGlobalLayoutListener layoutListener = new ViewTreeObserver.OnGlobalLayoutListener() { // from class: android.support.v7.widget.AppCompatSpinner.DropdownPopup.2
                    @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
                    public final void onGlobalLayout() {
                        if (!DropdownPopup.access$600(DropdownPopup.this, AppCompatSpinner.this)) {
                            DropdownPopup.this.dismiss();
                        } else {
                            DropdownPopup.this.computeContentWidth();
                            DropdownPopup.super.show();
                        }
                    }
                };
                vto.addOnGlobalLayoutListener(layoutListener);
                setOnDismissListener(new PopupWindow.OnDismissListener() { // from class: android.support.v7.widget.AppCompatSpinner.DropdownPopup.3
                    @Override // android.widget.PopupWindow.OnDismissListener
                    public final void onDismiss() {
                        ViewTreeObserver vto2 = AppCompatSpinner.this.getViewTreeObserver();
                        if (vto2 != null) {
                            vto2.removeGlobalOnLayoutListener(layoutListener);
                        }
                    }
                });
            }
        }

        static /* synthetic */ boolean access$600(DropdownPopup x0, View x1) {
            return ViewCompat.isAttachedToWindow(x1) && x1.getGlobalVisibleRect(x0.mVisibleRect);
        }
    }
}
