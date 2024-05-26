package com.nuance.swype.input;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.nuance.android.compat.ActivityManagerCompat;
import com.nuance.android.util.LruCache;
import com.nuance.android.util.WeakReferenceHandler;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class FunctionBarListView extends View {
    protected static final String ADDONDICTIONARIES = "addondictionaries";
    public static final int ADDONDICTIONARIES_ITEM = 112;
    protected static final String CHINESESETTINGS = "chinesesettings";
    public static final int CHINESESETTINGS_ITEM = 113;
    public static final char COMPONENT_MARKER = 40959;
    protected static final String CYCLINGKEYBOARD = "cyclingkeyboard";
    public static final int DISMISSKEYBOARD_ITEM = 115;
    protected static final String EDITKEYBOARD = "editkeyboard";
    public static final int EDITKEYBOARD_ITEM = 110;
    public static final int EMOJI_ITEM = 118;
    protected static final String FUNCTIONBAREMOJI = "function_bar_emoji";
    protected static final String FUNCTIONBARSETTING = "functionbarsetting";
    protected static final String HANDWRITINGMODE = "handwritingmode";
    protected static final String HIDE_KEYBOARD = "hide_keyboard";
    protected static final String INPUTMODE = "inputmode";
    public static final int INPUTMODE_ITEM = 104;
    protected static final String LANGUAGE_OPTION = "language_option";
    public static final int LANGUAGE_OPTION_ITME = 103;
    protected static final int MAX_SUGGESTIONS = Math.max(10, 10);
    public static final int MODE_CHANGE_ITEM = 102;
    protected static final int MSG_REMOVE_PREVIEW = 1;
    protected static final int MSG_REMOVE_THROUGH_PREVIEW = 2;
    protected static final String NUMBERKEYBOARD = "numberkeyboard";
    public static final int NUMBERKEYBOARD_ITEM = 109;
    protected static final String ONKEYBOARD = "onkeyboard";
    protected static final int OUT_OF_BOUNDS = -1;
    public static final int QUICKTOGGLE_ITEM_HW_OFF = 117;
    public static final int QUICKTOGGLE_ITEM_HW_ON = 114;
    protected static final int SCROLL_PIXELS = 12;
    protected static final String SETTINGS = "settings";
    public static final int SETTING_ITEM = 101;
    public static final int SWITCHIME_ITEM = 116;
    protected static final String TAG = "WordListView";
    protected static final String THEMES = "themes";
    public static final int THEMES_ITEM = 111;
    private final Handler.Callback handlerCallback;
    protected float mA;
    private boolean mAbortKey;
    protected Rect mBgPadding;
    protected int mColorComponent;
    protected int mColorNormal;
    protected int mColorOther;
    protected int mColorPressed;
    protected int mColorRecommended;
    private Context mContext;
    protected int mCurrentWordIndex;
    protected int mDescent;
    private int mDismissKeyboardItemWidth;
    protected boolean mDragSelected;
    protected boolean mEnableHighlight;
    private LruCache<Integer, Bitmap> mFunctionBitmaps;
    private List<Integer> mFunctionItems;
    protected GestureDetector mGestureDetector;
    Handler mHandler;
    protected Typeface mItalic;
    private int mLeftPadding;
    protected int mMinCandidateWidth;
    protected int mMinPadWidth;
    private int mMinikeyboardOffsetX;
    protected boolean mMouseDown;
    protected OnFunctionBarListener mOnFunctionBarListener;
    protected Paint mPaint;
    protected int mPopupPreviewX;
    protected int mPopupPreviewY;
    protected PopupWindow mPreviewPopup;
    protected TextView mPreviewText;
    protected boolean mScrolled;
    protected int mSelectedIndex;
    protected CharSequence mSelectedString;
    protected Drawable mSelectionHighlight;
    protected boolean mShowingNextCandidatesPrediction;
    RollAverage mSwipeSpeed;
    protected int mTargetScrollX;
    protected int mTextSize;
    public int mTotalWidth;
    protected int mTouchIndex;
    protected int mTouchX;
    protected float mV;
    protected int previewTextColor;

    /* loaded from: classes.dex */
    public interface OnFunctionBarListener {
        void selectFunctionBarFunction(int i);
    }

    /* loaded from: classes.dex */
    protected static class RollAverage {
        private boolean mFlushed;
        private final float mKeepRatio;
        private float mVal;

        public RollAverage(float keepRatio) {
            this.mKeepRatio = keepRatio;
            flush();
        }

        public void add(float val) {
            if (isFlushed()) {
                this.mVal = val;
                this.mFlushed = false;
            } else {
                this.mVal = (this.mKeepRatio * this.mVal) + ((1.0f - this.mKeepRatio) * val);
            }
        }

        public float get() {
            if (isFlushed()) {
                return 0.0f;
            }
            return this.mVal;
        }

        public final void flush() {
            this.mFlushed = true;
            this.mVal = 0.0f;
        }

        public boolean isFlushed() {
            return this.mFlushed;
        }
    }

    private float bound(float val, float min, float max) {
        if (val < min) {
            return min;
        }
        return val > max ? max : val;
    }

    @SuppressLint({"InflateParams"})
    public FunctionBarListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mTouchX = -1;
        this.mFunctionItems = new ArrayList();
        this.mV = 0.0f;
        this.mA = 0.0f;
        this.mMouseDown = false;
        this.mDragSelected = false;
        this.mTouchIndex = -1;
        this.mFunctionBitmaps = new LruCache<Integer, Bitmap>(10) { // from class: com.nuance.swype.input.FunctionBarListView.1
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.nuance.android.util.LruCache
            public void entryRemoved(boolean evicted, Integer key, Bitmap oldValue, Bitmap newValue) {
                if (oldValue != null) {
                    oldValue.recycle();
                }
            }
        };
        this.handlerCallback = new Handler.Callback() { // from class: com.nuance.swype.input.FunctionBarListView.2
            @Override // android.os.Handler.Callback
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        FunctionBarListView.this.mPreviewText.setVisibility(8);
                        FunctionBarListView.this.mPreviewText.setVisibility(4);
                        FunctionBarListView.this.mPreviewPopup.dismiss();
                        return true;
                    case 2:
                        FunctionBarListView.this.mPreviewText.setVisibility(8);
                        FunctionBarListView.this.mPreviewPopup.dismiss();
                        if (FunctionBarListView.this.mTouchX != -1) {
                            FunctionBarListView.this.removeHighlight();
                            return true;
                        }
                        return true;
                    default:
                        return true;
                }
            }
        };
        this.mHandler = WeakReferenceHandler.create(this.handlerCallback);
        this.mSwipeSpeed = new RollAverage(0.3f);
        this.mContext = context;
        IMEApplication app = IMEApplication.from(context);
        LayoutInflater inflater = app.getThemedLayoutInflater(LayoutInflater.from(context));
        app.getThemeLoader().setLayoutInflaterFactory(inflater);
        this.mPreviewPopup = new PopupWindow(context);
        this.mPreviewText = (TextView) inflater.inflate(R.layout.candidate_preview, (ViewGroup) null);
        app.getThemeLoader().applyTheme(this.mPreviewText);
        this.mPreviewPopup.setWindowLayoutMode(-2, -2);
        this.mPreviewPopup.setContentView(this.mPreviewText);
        this.mPreviewPopup.setBackgroundDrawable(null);
        readStyles(context);
        this.previewTextColor = app.getThemedColor(R.attr.popupKeyTextColor);
        this.mSelectionHighlight = app.getThemedDrawable(R.attr.listSelectorBackgroundPressed);
        this.mEnableHighlight = true;
        this.mPaint = new Paint();
        this.mPaint.setColor(this.mColorNormal);
        this.mPaint.setAntiAlias(true);
        this.mPaint.setTextSize(this.mTextSize);
        this.mPaint.setStrokeWidth(0.0f);
        this.mDescent = (int) this.mPaint.descent();
        this.mItalic = Typeface.create(Typeface.SERIF, 2);
        this.mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() { // from class: com.nuance.swype.input.FunctionBarListView.3
            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (FunctionBarListView.this.mFunctionBitmaps == null || FunctionBarListView.this.mFunctionBitmaps.size() == 0) {
                    return false;
                }
                FunctionBarListView.this.mA = 0.0f;
                FunctionBarListView.this.mV = 0.0f;
                FunctionBarListView.this.invalidate();
                return true;
            }
        });
        this.mGestureDetector.setIsLongpressEnabled(false);
        setHorizontalFadingEdgeEnabled(true);
        setWillNotDraw(false);
        setHorizontalScrollBarEnabled(false);
        setVerticalScrollBarEnabled(false);
        clear();
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        this.mMinPadWidth = 0;
        this.mMinCandidateWidth = (int) (40.0f * dm.density);
    }

    public void setOnFunctionBarListener(OnFunctionBarListener listener) {
        this.mOnFunctionBarListener = listener;
    }

    @Override // android.view.View
    public int computeHorizontalScrollRange() {
        return this.mTotalWidth;
    }

    public void touchWord(int i) {
        this.mSelectedIndex = i;
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        int measuredWidth = getFunctionBarWidth();
        this.mTotalWidth = 0;
        if (this.mBgPadding == null) {
            this.mBgPadding = new Rect(0, 0, 0, 0);
            if (getBackground() != null) {
                getBackground().getPadding(this.mBgPadding);
            }
        }
        int touchX = this.mTouchX;
        int width = getWidth() - this.mDismissKeyboardItemWidth;
        if (this.mFunctionBitmaps != null && this.mFunctionBitmaps.size() != 0) {
            int height = getHeight();
            int x = 0;
            int count = this.mFunctionBitmaps.size();
            Rect bgPadding = this.mBgPadding;
            Paint paint = this.mPaint;
            int scrollX = getScrollX();
            boolean scrolled = this.mScrolled;
            int y = (height - this.mFunctionBitmaps.get(Integer.valueOf(getFunctionItem(0))).getHeight()) / 2;
            paint.setTypeface(Typeface.DEFAULT);
            if (this.mFunctionBitmaps.size() > 1) {
                this.mLeftPadding = (width - getFunctionBarWidth()) / ((this.mFunctionBitmaps.size() - 1) * 2);
            }
            for (int i = 0; i < count; i++) {
                if (i == 0 && canvas != null && measuredWidth < getWidth()) {
                    canvas.save();
                    canvas.translate(x, 0.0f);
                    canvas.restore();
                }
                paint.setUnderlineText(false);
                paint.setColor(this.mColorOther);
                int bitmapWidth = this.mFunctionBitmaps.get(Integer.valueOf(getFunctionItem(i))).getWidth() + (this.mLeftPadding * 2) + (this.mMinPadWidth * 2);
                if (i == count - 1) {
                    bitmapWidth = this.mDismissKeyboardItemWidth * 2;
                }
                int cellWidth = bitmapWidth + (this.mMinPadWidth * 2);
                if (i == count - 1 && this.mFunctionBitmaps.size() == 1) {
                    x += getWidth() - (this.mDismissKeyboardItemWidth * 2);
                }
                if (touchX + scrollX >= x && touchX + scrollX < x + cellWidth && !scrolled && this.mTouchX != -1) {
                    if (canvas != null) {
                        canvas.save();
                        canvas.translate(x, 0.0f);
                        this.mSelectionHighlight.setBounds(0, bgPadding.top, cellWidth, height);
                        this.mSelectionHighlight.draw(canvas);
                        canvas.restore();
                        this.mPaint.setColor(this.mColorPressed);
                        if (i != count - 1) {
                            showPreview(x + this.mMinikeyboardOffsetX, getPopupContent(getFunctionItem(i)), this.mFunctionBitmaps.get(Integer.valueOf(getFunctionItem(i))).getWidth());
                        }
                        this.mPaint.setTypeface(Typeface.DEFAULT_BOLD);
                    }
                    this.mTouchIndex = i;
                }
                if (canvas != null) {
                    if (i != count - 1) {
                        canvas.drawBitmap(this.mFunctionBitmaps.get(Integer.valueOf(getFunctionItem(i))), this.mLeftPadding + x, y, paint);
                    } else {
                        canvas.drawBitmap(this.mFunctionBitmaps.get(Integer.valueOf(getFunctionItem(i))), getWidth() - ((this.mDismissKeyboardItemWidth * 3) / 2), y, paint);
                    }
                    paint.setColor(this.mColorOther);
                    if (i < count - 1 || measuredWidth < getWidth()) {
                        canvas.save();
                        canvas.translate(x + cellWidth, 0.0f);
                        canvas.restore();
                    }
                }
                paint.setTypeface(Typeface.DEFAULT);
                x += bitmapWidth;
            }
            this.mTotalWidth = x;
            if (this.mTargetScrollX != getScrollX()) {
                scrollToTarget();
            }
            if (!this.mMouseDown) {
                calcAccel();
                if (0.0f != this.mA || 0.0f != this.mV) {
                    slide();
                }
            }
        }
    }

    protected void setVelocity(float v) {
        this.mV = 0.7f * bound(v, -30.0f, 30.0f);
    }

    protected void calcAccel() {
        float vUnitVect = 0.0f == this.mV ? 0.0f : this.mV / Math.abs(this.mV);
        float x = getScrollX();
        int maxScroll = Math.max(0, this.mTotalWidth - getWidth());
        if (x >= 0.0f && x <= maxScroll) {
            if (0.0f != this.mV) {
                this.mA = (-0.6f) * vUnitVect;
                return;
            } else {
                this.mA = 0.0f;
                return;
            }
        }
        float oobDist = x < 0.0f ? x : x - maxScroll;
        if (vUnitVect * oobDist > 0.0f) {
            this.mA = (-0.005f) * vUnitVect * oobDist * oobDist;
            this.mA = bound(this.mA, -10.0f, 10.0f);
            return;
        }
        this.mA = 0.0f;
        this.mV = 0.0f;
        if (oobDist < 0.0f) {
            this.mTargetScrollX = 0;
        } else {
            this.mTargetScrollX = maxScroll;
        }
        invalidate();
    }

    protected float pull(float dist) {
        float oobDist;
        float x = getScrollX();
        int width = getWidth();
        int maxScroll = Math.max(0, this.mTotalWidth - width);
        float maxPullDist = width / 4.0f;
        if (x < 0.0f) {
            oobDist = x;
        } else if (x > maxScroll) {
            oobDist = x - maxScroll;
        } else {
            oobDist = 0.0f;
        }
        float pinnedOobDist = Math.min(Math.abs(oobDist), maxPullDist);
        float ret = ((float) Math.pow(1.0f - ((pinnedOobDist * pinnedOobDist) / (maxPullDist * maxPullDist)), 0.8d)) * dist;
        if (dist * oobDist < 0.0f && Math.abs(ret) < 1.0f) {
            return dist / Math.abs(dist);
        }
        return ret;
    }

    private void slide() {
        float oldX = getScrollX();
        int maxScroll = Math.max(0, this.mTotalWidth - getWidth());
        float oldV = this.mV;
        scrollToX(getScrollX() + ((int) pull(this.mV * 1.0f)));
        this.mV += this.mA * 1.0f;
        if (Math.abs(this.mV) < 1.0f) {
            this.mV = 0.0f;
        }
        if ((this.mV * oldV <= 0.0f && getScrollX() >= 0 && getScrollX() <= maxScroll) || ((oldX < 0.0f && getScrollX() >= 0) || (oldX > maxScroll && getScrollX() <= maxScroll))) {
            this.mV = 0.0f;
            this.mA = 0.0f;
            if (oldX < 0.0f && getScrollX() >= 0) {
                scrollToX(0);
            } else if (oldX > maxScroll && getScrollX() <= maxScroll) {
                scrollToX(getScrollX() + maxScroll);
            }
        }
        this.mTargetScrollX = getScrollX();
        requestLayout();
        invalidate();
    }

    private void scrollToTarget() {
        if (this.mTargetScrollX > getScrollX()) {
            double dist = this.mTargetScrollX - getScrollX();
            scrollToX(getScrollX() + ((int) ((Math.pow(Math.min(dist, 100.0d) / 100.0d, 1.5d) * 12.0d) + 1.0d)));
            if (getScrollX() >= this.mTargetScrollX) {
                scrollToX(this.mTargetScrollX);
            }
        } else {
            double dist2 = getScrollX() - this.mTargetScrollX;
            scrollToX(getScrollX() - ((int) ((Math.pow(Math.min(dist2, 100.0d) / 100.0d, 1.5d) * 12.0d) + 1.0d)));
            if (getScrollX() <= this.mTargetScrollX) {
                scrollToX(this.mTargetScrollX);
            }
        }
        requestLayout();
        invalidate();
    }

    protected void updateScrollPosition(int targetX) {
        if (targetX != getScrollX()) {
            this.mTargetScrollX = targetX;
            invalidate();
            this.mScrolled = true;
        }
    }

    public void clear() {
        this.mTouchX = -1;
        this.mSelectedString = null;
        this.mSelectedIndex = -1;
        this.mTotalWidth = 0;
        invalidate();
        if (this.mPreviewPopup.isShowing()) {
            this.mPreviewPopup.dismiss();
        }
        scrollToX(0);
        this.mTargetScrollX = 0;
        this.mA = 0.0f;
        this.mV = 0.0f;
    }

    public void selectActiveWord() {
        trySelect();
    }

    protected void showPreview(int popupX, String previewText, int bitmapWidth) {
        if (!ActivityManagerCompat.isUserAMonkey()) {
            if (!this.mPreviewPopup.isShowing() || !this.mPreviewText.getText().equals(previewText)) {
                this.mPreviewText.setText(previewText);
                this.mPreviewText.setTextColor(this.previewTextColor);
                this.mPreviewText.measure(View.MeasureSpec.makeMeasureSpec(0, 0), View.MeasureSpec.makeMeasureSpec(0, 0));
                int popupWidth = ((int) (this.mPaint.measureText(previewText, 0, previewText.length()) + (this.mMinPadWidth * 2))) + this.mPreviewText.getPaddingLeft() + this.mPreviewText.getPaddingRight();
                int popupHeight = this.mPreviewText.getMeasuredHeight() + 10;
                this.mPopupPreviewX = popupX;
                this.mPopupPreviewY = -popupHeight;
                this.mHandler.removeMessages(1);
                int[] offsetInWindow = new int[2];
                getLocationInWindow(offsetInWindow);
                this.mPreviewText.setVisibility(0);
                this.mPreviewPopup.dismiss();
                this.mPreviewPopup.setWidth(popupWidth);
                this.mPreviewPopup.setHeight(popupHeight);
                this.mPreviewPopup.showAtLocation(this, 0, this.mPopupPreviewX, this.mPopupPreviewY + offsetInWindow[1]);
            }
        }
    }

    protected void trySelect() {
        if (this.mPreviewPopup != null && this.mPreviewPopup.isShowing()) {
            this.mPreviewPopup.dismiss();
        }
        this.mOnFunctionBarListener.selectFunctionBarFunction(getFunctionItem(this.mSelectedIndex));
        touchWord(this.mSelectedIndex);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // android.view.View
    @SuppressLint({"ClickableViewAccessibility"})
    public boolean onTouchEvent(MotionEvent me) {
        int action = me.getAction();
        int x = (int) me.getX();
        int y = (int) me.getY();
        this.mTouchX = x;
        this.mMinikeyboardOffsetX = ((int) me.getRawX()) - x;
        if (!this.mGestureDetector.onTouchEvent(me)) {
            switch (action) {
                case 0:
                    this.mMouseDown = true;
                    this.mDragSelected = false;
                    this.mAbortKey = false;
                    this.mSwipeSpeed.flush();
                    this.mA = 0.0f;
                    this.mV = 0.0f;
                    this.mScrolled = false;
                    this.mTouchIndex = -1;
                    invalidate();
                    break;
                case 1:
                    this.mMouseDown = false;
                    this.mDragSelected = false;
                    if (!this.mSwipeSpeed.isFlushed()) {
                        setVelocity(this.mSwipeSpeed.get());
                    }
                    if (this.mTouchIndex != -1 && this.mFunctionBitmaps.size() > 0 && this.mTouchIndex < this.mFunctionBitmaps.size() && !this.mAbortKey) {
                        touchWord(this.mTouchIndex);
                        trySelect();
                    }
                    removeHighlight();
                    hidePreview();
                    break;
                case 2:
                    if (y <= 0 && !this.mDragSelected) {
                        this.mDragSelected = true;
                        break;
                    }
                    break;
            }
        }
        return true;
    }

    @SuppressLint({"WrongCall"})
    public void takeSuggestionAt(float x) {
        this.mTouchX = (int) x;
        onDraw(null);
        trySelect();
        invalidate();
        this.mHandler.removeMessages(2);
        this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(2), 200L);
    }

    private void hidePreview() {
        this.mCurrentWordIndex = -1;
        if (this.mPreviewPopup.isShowing()) {
            this.mHandler.removeMessages(1);
            this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(1), 60L);
        }
    }

    protected void removeHighlight() {
        this.mTouchX = -1;
        invalidate();
    }

    @Override // android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.mPreviewPopup.isShowing()) {
            this.mHandler.removeMessages(1);
            this.mPreviewText.setVisibility(8);
            this.mPreviewText.setVisibility(4);
        }
    }

    protected void scrollToX(int newScrollX) {
        scrollTo(newScrollX, getScrollY());
    }

    private void readStyles(Context context) {
        TypedArray a = context.obtainStyledAttributes(R.style.WordListView, R.styleable.WordListView);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.WordListView_candidateNormal) {
                this.mColorNormal = a.getColor(attr, -65536);
            } else if (attr == R.styleable.WordListView_candidateRecommended) {
                this.mColorRecommended = a.getColor(attr, -65536);
            } else if (attr == R.styleable.WordListView_candidateOther) {
                this.mColorOther = a.getColor(attr, -65536);
            } else if (attr == R.styleable.WordListView_candidateComponent) {
                this.mColorComponent = a.getColor(attr, -65536);
            } else if (attr == R.styleable.WordListView_textSizeCJK) {
                this.mTextSize = a.getDimensionPixelSize(attr, 20);
            } else if (attr == R.styleable.WordListView_candidatePressed) {
                this.mColorPressed = a.getColor(attr, -65536);
            }
        }
        a.recycle();
    }

    @Override // android.view.View
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    private int getFunctionBarWidth() {
        int width = 0;
        for (int i = 0; i < this.mFunctionBitmaps.size(); i++) {
            Bitmap bitmap = this.mFunctionBitmaps.get(Integer.valueOf(getFunctionItem(i)));
            width += bitmap.getWidth();
        }
        return width;
    }

    public void addToolBarItem(int item) {
        int bitmapID = 0;
        switch (item) {
            case 101:
                bitmapID = R.drawable.functionbar_swypesettings;
                break;
            case 103:
                bitmapID = R.drawable.functionbar_languages;
                break;
            case 104:
                bitmapID = R.drawable.functionbar_inputmodes;
                break;
            case 109:
                bitmapID = R.drawable.functionbar_number;
                break;
            case 110:
                bitmapID = R.drawable.functionbar_edit;
                break;
            case 111:
                bitmapID = R.drawable.functionbar_themes;
                break;
            case 112:
                bitmapID = R.drawable.functionbar_addondictionaries;
                break;
            case 113:
                bitmapID = R.drawable.functionbar_chinesesettings;
                break;
            case 114:
                bitmapID = R.drawable.functionbar_handwritingoff;
                break;
            case 115:
                bitmapID = R.drawable.functionbar_dismiss;
                break;
            case 117:
                bitmapID = R.drawable.functionbar_handwriting;
                break;
            case 118:
                bitmapID = R.drawable.functionbar_emoji;
                break;
        }
        if (bitmapID != 0) {
            Bitmap bp = this.mFunctionBitmaps.get(Integer.valueOf(bitmapID));
            if (bp == null) {
                bp = BitmapFactory.decodeResource(getContext().getResources(), bitmapID);
                this.mFunctionBitmaps.put(Integer.valueOf(item), bp);
            }
            this.mFunctionItems.add(Integer.valueOf(item));
            if (bitmapID == R.drawable.functionbar_dismiss) {
                this.mDismissKeyboardItemWidth = bp.getWidth();
            }
        }
    }

    public int getFunctionItem(int index) {
        return this.mFunctionItems.get(index).intValue();
    }

    public void recycleBitmap() {
        if (this.mFunctionBitmaps != null) {
            this.mFunctionBitmaps.evictAll();
        }
        this.mFunctionItems.clear();
    }

    @SuppressLint({"WrongCall"})
    public void showFunctionBar() {
        onDraw(null);
    }

    private String getPopupContent(int index) {
        switch (index) {
            case 101:
                return this.mContext.getString(R.string.swype);
            case 102:
            case 105:
            case 106:
            case 107:
            case 108:
            default:
                return "";
            case 103:
                return this.mContext.getString(R.string.language_category_title);
            case 104:
                return this.mContext.getString(R.string.input_method);
            case 109:
                return this.mContext.getString(R.string.number_keyboard);
            case 110:
                return this.mContext.getString(R.string.edit_keyboard);
            case 111:
                return this.mContext.getString(R.string.pref_menu_themes);
            case 112:
                return this.mContext.getString(R.string.addon_dictionaries);
            case 113:
                return this.mContext.getString(R.string.settings_swype_chinese);
            case 114:
                return this.mContext.getString(R.string.function_bar_quick_toggle);
            case 115:
                return this.mContext.getString(R.string.dismiss_keyboard);
            case 116:
                return this.mContext.getString(R.string.switch_ime);
            case 117:
                return this.mContext.getString(R.string.function_bar_quick_toggle);
            case 118:
                return this.mContext.getString(R.string.function_bar_emoji);
        }
    }

    public boolean isFunctionBarShowing() {
        return this.mFunctionBitmaps.size() > 0;
    }

    public boolean isFunctionBarDisabledOrZeroItem() {
        return !UserPreferences.from(getContext()).getShowFunctionBar() || this.mFunctionBitmaps.size() == 0;
    }
}
