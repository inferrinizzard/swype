package com.nuance.swype.input.chinese;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
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
import android.view.MotionEvent;
import android.view.View;
import com.nuance.android.compat.UserManagerCompat;
import com.nuance.android.util.WeakReferenceHandler;
import com.nuance.input.swypecorelib.Candidates;
import com.nuance.input.swypecorelib.WordCandidate;
import com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.xmlResults.XMLResultsHandler;
import com.nuance.swype.input.AlphaInputView;
import com.nuance.swype.input.DrawableCandidate;
import com.nuance.swype.input.IME;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.R;
import com.nuance.swype.input.UserPreferences;
import com.nuance.swype.input.emoji.Emoji;
import com.nuance.swype.input.emoji.EmojiCacheManager;
import com.nuance.swype.input.emoji.EmojiInfo;
import com.nuance.swype.input.emoji.EmojiLoader;
import com.nuance.swype.input.settings.ThemesPrefs;
import com.nuance.swype.plugin.ThemeLoader;
import com.nuance.swype.plugin.TypedArrayWrapper;
import com.nuance.swype.util.LogManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/* loaded from: classes.dex */
public class CJKCandidatesListView extends View {
    public static final char COMPONENT_MARKER = 40959;
    private static final int MIN_LONGPRESS_TIMEOUT = 750;
    private static final int MSG_LONGPRESS = 3;
    protected static final int MSG_REMOVE_THROUGH_PREVIEW = 2;
    protected static final int OUT_OF_BOUNDS = -1;
    protected static final int SCROLL_PIXELS = 12;
    private boolean alignLeft;
    private boolean allowFullScroll;
    private boolean altCharacterConverted;
    private EmojiCacheManager<String, Integer> emojiCacheManager;
    public List<EmojiInfo> emojiInfoList;
    private final Handler.Callback handlerCallback;
    protected float mA;
    private boolean mAbortKey;
    protected int mAlphaTextSize;
    private boolean mAssociationWord;
    protected Rect mBgPadding;
    protected int mColorComponent;
    protected int mColorNormal;
    protected int mColorOther;
    protected int mColorPressed;
    protected int mColorRecommended;
    protected Context mContext;
    protected Drawable mDefaultWordHighlight;
    protected int mDescent;
    protected Drawable mDivider;
    protected boolean mDragSelected;
    public Map<String, String> mEmojis;
    protected boolean mEnableHighlight;
    private boolean mExactKeyboardShowable;
    protected GestureDetector mGestureDetector;
    protected GetMoreWordsHandler mGetMoreWordsHandler;
    Handler mHandler;
    protected Typeface mItalic;
    private boolean mLeftArrowShowable;
    private int mLeftArrowWidth;
    private int mLongPressTimeout;
    protected int mMinPadWidth;
    protected boolean mMouseDown;
    private Candidates mNotMatchTooltip;
    protected OnWordSelectActionListener mOnWCLSelectActionListener;
    protected OnWordSelectActionListener mOnWordSelectActionListener;
    protected int[] mPadWidth;
    protected Paint mPaint;
    protected boolean mScroll;
    protected boolean mScrolled;
    protected int mSelectedIndex;
    protected CharSequence mSelectedString;
    protected Drawable mSelectionHighlight;
    private boolean mShowTooltip;
    public List<CharSequence> mSuggestions;
    RollAverage mSwipeSpeed;
    private Candidates mSwypeTooltip;
    protected int mTargetScrollPos;
    protected int mTextSize;
    protected int mTextSizeInit;
    private Tooltip mTooltip;
    private int[] mTooltipWordWidth;
    public int mTotalLength;
    protected int mTouchIndex;
    protected int mTouchX;
    protected int mTouchY;
    protected float mV;
    private Candidates mWCLTooltip;
    private List<AtomicInteger> mWordSourceList;
    private int mWordSpace;
    protected int[] mWordWidth;
    private int[] mWordWidthWithoutPadding;
    public int[] mWordX;
    private float xDown;
    protected static final int MAX_SUGGESTIONS = Math.max(256, 10);
    protected static final LogManager.Log log = LogManager.getLog("CJKCandidatesListView");

    /* loaded from: classes.dex */
    public interface GetMoreWordsHandler {
        void requestMoreWords();
    }

    /* loaded from: classes.dex */
    public interface OnWordSelectActionListener {
        boolean isScrollable(CJKCandidatesListView cJKCandidatesListView);

        void nextBtnPressed(CJKCandidatesListView cJKCandidatesListView);

        void onCandidateLongPressed(int i, String str, int i2, CJKCandidatesListView cJKCandidatesListView);

        void onPressMoveCandidate(float f, float f2, float f3);

        boolean onPressReleaseCandidate(int i, String str, int i2);

        void onScrollContentChanged();

        void prevBtnPressed(CJKCandidatesListView cJKCandidatesListView);

        void selectWord(int i, CharSequence charSequence, View view);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public enum Tooltip {
        SWYPE_TOOLTIP,
        NOTMATCH_TOOLTIP,
        WCL_TOOLTIP
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

    /* JADX INFO: Access modifiers changed from: protected */
    public float bound(float val, float min, float max) {
        if (val < min) {
            return min;
        }
        return val > max ? max : val;
    }

    public List<CharSequence> suggestions() {
        return this.mSuggestions;
    }

    public Map<String, String> getEmojis() {
        return this.mEmojis;
    }

    public List<EmojiInfo> getEmojiInfoList() {
        return this.emojiInfoList;
    }

    public CJKCandidatesListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mSuggestions = new ArrayList();
        this.emojiInfoList = new ArrayList();
        this.mEmojis = new HashMap();
        this.mWordSourceList = new ArrayList();
        this.mTouchX = -1;
        this.mTouchY = -1;
        this.mLongPressTimeout = MIN_LONGPRESS_TIMEOUT;
        this.mWordWidth = new int[MAX_SUGGESTIONS + 1];
        this.mPadWidth = new int[MAX_SUGGESTIONS + 1];
        this.mWordX = new int[MAX_SUGGESTIONS + 1];
        this.mScroll = true;
        this.mV = 0.0f;
        this.mA = 0.0f;
        this.mMouseDown = false;
        this.mDragSelected = false;
        this.mTouchIndex = -1;
        this.mAssociationWord = false;
        this.mLeftArrowShowable = false;
        this.mLeftArrowWidth = 0;
        this.mTooltipWordWidth = null;
        this.mWordWidthWithoutPadding = null;
        this.mExactKeyboardShowable = false;
        this.mShowTooltip = false;
        this.mTooltip = null;
        this.mSwypeTooltip = null;
        this.mNotMatchTooltip = null;
        this.mWCLTooltip = null;
        this.handlerCallback = new Handler.Callback() { // from class: com.nuance.swype.input.chinese.CJKCandidatesListView.1
            /* JADX WARN: Failed to find 'out' block for switch in B:2:0x0004. Please report as an issue. */
            @Override // android.os.Handler.Callback
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case 3:
                        if (CJKCandidatesListView.this.mTouchIndex != -1) {
                            if (CJKCandidatesListView.this.mWordSourceList != null && !CJKCandidatesListView.this.mWordSourceList.isEmpty()) {
                                if (CJKCandidatesListView.this.mTouchIndex >= CJKCandidatesListView.this.mWordSourceList.size()) {
                                    return false;
                                }
                                int wdSource = ((AtomicInteger) CJKCandidatesListView.this.mWordSourceList.get(CJKCandidatesListView.this.mTouchIndex)).get();
                                CJKCandidatesListView.log.d("onCandidateLongPressed()", " called 11::  wdSource  :: " + wdSource + " , word :: " + CJKCandidatesListView.this.mSuggestions.get(CJKCandidatesListView.this.mTouchIndex).toString());
                                if (wdSource == 5 || wdSource == 14 || wdSource == 6 || wdSource == 9 || wdSource == 4 || wdSource == 7) {
                                    CJKCandidatesListView.this.abortKey();
                                    CJKCandidatesListView.this.mOnWordSelectActionListener.onCandidateLongPressed(CJKCandidatesListView.this.mTouchIndex, CJKCandidatesListView.this.mSuggestions.get(CJKCandidatesListView.this.mTouchIndex).toString(), wdSource, CJKCandidatesListView.this);
                                }
                            } else {
                                if (CJKCandidatesListView.this.mTouchIndex >= CJKCandidatesListView.this.mSuggestions.size()) {
                                    return false;
                                }
                                CJKCandidatesListView.this.abortKey();
                                CJKCandidatesListView.this.mOnWordSelectActionListener.onCandidateLongPressed(CJKCandidatesListView.this.mTouchIndex, CJKCandidatesListView.this.mSuggestions.get(CJKCandidatesListView.this.mTouchIndex).toString(), 0, CJKCandidatesListView.this);
                            }
                        }
                        break;
                    default:
                        return true;
                }
            }
        };
        this.mHandler = WeakReferenceHandler.create(this.handlerCallback);
        this.mSwipeSpeed = new RollAverage(0.3f);
        this.mContext = context;
    }

    public void init() {
        IMEApplication app = IMEApplication.from(this.mContext);
        this.emojiCacheManager = EmojiCacheManager.from(this.mContext);
        this.mSelectionHighlight = app.getThemedDrawable(R.attr.listSelectorBackgroundPressed);
        this.mDefaultWordHighlight = app.getThemedDrawable(R.attr.wclDefaultWordBackground);
        this.mDivider = app.getThemedDrawable(R.attr.keyboardSuggestStripDivider);
        this.mEnableHighlight = true;
        this.mPaint = new Paint();
        this.mPaint.setColor(this.mColorNormal);
        this.mPaint.setAntiAlias(true);
        this.mPaint.setTextSize(this.mTextSize);
        this.mPaint.setStrokeWidth(0.0f);
        this.mDescent = (int) this.mPaint.descent();
        this.mItalic = Typeface.create(Typeface.SERIF, 2);
        this.mGetMoreWordsHandler = null;
        this.mGestureDetector = new GestureDetector(this.mContext, new GestureDetector.SimpleOnGestureListener() { // from class: com.nuance.swype.input.chinese.CJKCandidatesListView.2
            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (CJKCandidatesListView.this.mSuggestions == null || CJKCandidatesListView.this.mSuggestions.isEmpty() || CJKCandidatesListView.this.getMaxScroll() <= 0 || !CJKCandidatesListView.this.mScroll) {
                    return false;
                }
                CJKCandidatesListView.this.mScrolled = true;
                CJKCandidatesListView.this.mA = 0.0f;
                CJKCandidatesListView.this.mV = 0.0f;
                CJKCandidatesListView.this.scrollToX(CJKCandidatesListView.this.getScrollX() + ((int) CJKCandidatesListView.this.pull(distanceX)));
                CJKCandidatesListView.this.mTargetScrollPos = CJKCandidatesListView.this.getScrollX();
                CJKCandidatesListView.this.mSwipeSpeed.add(distanceX);
                CJKCandidatesListView.this.mHandler.removeMessages(3);
                if (CJKCandidatesListView.this.needMoreWords()) {
                    CJKCandidatesListView.this.mGetMoreWordsHandler.requestMoreWords();
                }
                CJKCandidatesListView.this.invalidate();
                return true;
            }
        });
        this.mGestureDetector.setIsLongpressEnabled(false);
        setHorizontalFadingEdgeEnabled(true);
        setWillNotDraw(false);
        setHorizontalScrollBarEnabled(false);
        setVerticalScrollBarEnabled(false);
        clear();
        DisplayMetrics dm = this.mContext.getResources().getDisplayMetrics();
        this.mMinPadWidth = (int) (this.mWordSpace * dm.density);
    }

    public void setOnWordSelectActionListener(OnWordSelectActionListener listener) {
        this.mOnWordSelectActionListener = listener;
    }

    public void setOnWCLMessageSelectActionListener(OnWordSelectActionListener listener) {
        this.mOnWCLSelectActionListener = listener;
    }

    @Override // android.view.View
    public int computeHorizontalScrollRange() {
        return this.mTotalLength;
    }

    public void touchWord(int i, CharSequence suggestion) {
        if (this.mSelectedIndex != i && (this.mOnWordSelectActionListener instanceof ChineseHandWritingInputView)) {
            ((ChineseHandWritingInputView) this.mOnWordSelectActionListener).setActiveCandidate(i);
        } else if (this.mSelectedIndex != i && (this.mOnWordSelectActionListener instanceof ChineseFSHandWritingInputView)) {
            ((ChineseFSHandWritingInputView) this.mOnWordSelectActionListener).setActiveCandidate(i);
        }
        this.mSelectedIndex = i;
        this.mSelectedString = suggestion;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onDraw(Canvas canvas) {
        log.d("onDraw(): canvas: " + canvas);
        IME mIme = IMEApplication.from(getContext()).getIME();
        Configuration config = getResources().getConfiguration();
        if (!mIme.isShowCandidatesViewAllowed() && config.orientation == 2) {
            mIme.setCandidatesViewShown(false);
            return;
        }
        int measuredWidth = this.mTotalLength;
        this.mTotalLength = 0;
        if (this.mBgPadding == null) {
            this.mBgPadding = new Rect(0, 0, 0, 0);
            if (getBackground() != null) {
                getBackground().getPadding(this.mBgPadding);
            }
        }
        if (this.mShowTooltip && measuredWidth == 0) {
            computeDisplayingCandidateDrawingRegion(this.mTooltip);
        }
        int touchX = this.mTouchX;
        int width = getWidth();
        if (!this.alignLeft && measuredWidth < width && canvas != null) {
            int offset = (width - measuredWidth) / 2;
            canvas.translate(offset, 0.0f);
            touchX -= offset;
        }
        if (this.mSuggestions == null || this.mSuggestions.isEmpty()) {
            if (this.mShowTooltip) {
                drawToolTip(canvas);
                return;
            }
            return;
        }
        int height = getHeight();
        if (this.mLeftArrowShowable) {
            this.mDivider.setBounds(this.mLeftArrowWidth, this.mBgPadding.top, this.mDivider.getIntrinsicWidth() + this.mLeftArrowWidth, getHeight() - this.mBgPadding.bottom);
        } else {
            this.mDivider.setBounds(0, this.mBgPadding.top, this.mDivider.getIntrinsicWidth(), getHeight() - this.mBgPadding.bottom);
        }
        int x = 0;
        int count = this.mSuggestions.size();
        Rect bgPadding = this.mBgPadding;
        Paint paint = this.mPaint;
        int scrollX = getScrollX();
        boolean scrolled = this.mScrolled;
        int y = ((int) ((height + this.mPaint.getTextSize()) - this.mDescent)) / 2;
        paint.setTypeface(Typeface.DEFAULT);
        for (int i = 0; i < count; i++) {
            if (i == 0 && canvas != null && measuredWidth < getWidth()) {
                canvas.save();
                canvas.translate(x, 0.0f);
                this.mDivider.draw(canvas);
                canvas.restore();
            }
            CharSequence suggestion = this.mSuggestions.get(i);
            if (suggestion != null && suggestion.length() != 0) {
                if (EmojiLoader.isEmoji(suggestion.toString())) {
                    suggestion = getEmojiText(suggestion.toString());
                }
                int startIndex = 0;
                paint.setUnderlineText(false);
                if (suggestion.charAt(0) == 40959) {
                    if (suggestion.charAt(1) == '~') {
                        if (this.mSelectedIndex == i) {
                            this.mSelectedIndex++;
                        }
                    } else {
                        paint.setUnderlineText(true);
                        startIndex = 1;
                        paint.setColor(this.mColorComponent);
                    }
                } else if (this.mSelectedIndex == i && this.mEnableHighlight) {
                    paint.setTypeface(Typeface.DEFAULT);
                    paint.setColor(this.mColorRecommended);
                } else if (i == 0 && this.mEnableHighlight && (this.mOnWordSelectActionListener instanceof AlphaInputView)) {
                    paint.setColor(this.mColorNormal);
                    paint.setTypeface(this.mItalic);
                } else if (this.mAssociationWord) {
                    paint.setColor(this.mColorComponent);
                } else {
                    paint.setColor(this.mColorOther);
                }
                int wordWidth = (int) paint.measureText(suggestion, startIndex, suggestion.length());
                int padWidth = this.mMinPadWidth;
                this.mWordWidth[i] = wordWidth;
                this.mPadWidth[i] = padWidth;
                int cellWidth = wordWidth + (padWidth * 2);
                this.mWordX[i] = x;
                int arrowOffset = this.mLeftArrowShowable ? this.mLeftArrowWidth : 0;
                if (touchX + scrollX >= x + arrowOffset && touchX + scrollX < x + cellWidth + arrowOffset && !scrolled && this.mTouchX != -1) {
                    if (canvas != null) {
                        canvas.save();
                        canvas.translate(x, 0.0f);
                        this.mSelectionHighlight.setBounds(this.mDivider.getBounds().width() + arrowOffset, bgPadding.top, cellWidth + arrowOffset, height);
                        this.mSelectionHighlight.draw(canvas);
                        canvas.restore();
                        this.mPaint.setColor(this.mColorPressed);
                        this.mPaint.setTypeface(Typeface.DEFAULT);
                    }
                    this.mTouchIndex = i;
                }
                if (this.mSelectedIndex == i && this.mEnableHighlight && canvas != null && this.mDefaultWordHighlight != null) {
                    canvas.save();
                    canvas.translate(x, 0.0f);
                    this.mDefaultWordHighlight.setBounds(this.mDivider.getBounds().width() + arrowOffset, bgPadding.top, cellWidth + arrowOffset, height);
                    this.mDefaultWordHighlight.draw(canvas);
                    canvas.restore();
                }
                if (canvas != null) {
                    log.d("drawCandidate()", " draw called ::  x:: " + x + ", y:: " + y + " , offset :: 16844052");
                    log.d("drawCandidate()", " draw called :: wordWidth:: " + wordWidth + ", height:: " + height + ", cellWidth :: " + cellWidth + " , offset :: 16844052");
                    EmojiInfo emojiInfo = new EmojiInfo();
                    emojiInfo.xPos = x;
                    emojiInfo.yPos = y;
                    emojiInfo.width = cellWidth;
                    emojiInfo.height = height;
                    this.emojiInfoList.add(emojiInfo);
                    canvas.drawText(suggestion, startIndex, suggestion.length(), x + padWidth + arrowOffset, y, paint);
                    paint.setColor(this.mColorOther);
                    if (i < count - 1 || measuredWidth < getWidth()) {
                        canvas.save();
                        canvas.translate(x + cellWidth, 0.0f);
                        this.mDivider.draw(canvas);
                        canvas.restore();
                    }
                }
                paint.setTypeface(Typeface.DEFAULT);
                x += (padWidth * 2) + wordWidth;
            }
        }
        this.mTotalLength = x;
        if (!this.mExactKeyboardShowable && getScrollX() < 0 && this.mLeftArrowShowable) {
            this.mOnWordSelectActionListener.prevBtnPressed(this);
            this.mExactKeyboardShowable = true;
        }
        if (this.mTargetScrollPos != getScrollX()) {
            scrollToTarget();
        }
        if (!this.mMouseDown) {
            calcAccel();
            if (0.0f != this.mA || 0.0f != this.mV) {
                slide();
            }
        }
    }

    @SuppressLint({"WrongCall"})
    public void drawCandidates(int defaultIndex) {
        this.mSelectedIndex = defaultIndex;
        onDraw(null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setVelocity(float v) {
        this.mV = 0.7f * bound(v, -30.0f, 30.0f);
    }

    protected void calcAccel() {
        float vUnitVect = 0.0f == this.mV ? 0.0f : this.mV / Math.abs(this.mV);
        float x = getScrollX();
        int maxScroll = Math.max(0, getMaxScroll());
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
            this.mTargetScrollPos = 0;
        } else {
            this.mTargetScrollPos = maxScroll;
        }
        invalidate();
    }

    public boolean isScrolling() {
        return (this.mA == 0.0f || this.mV == 0.0f) ? false : true;
    }

    protected float pull(float dist) {
        float oobDist;
        float x = getScrollX();
        int width = getWidth();
        int maxScroll = Math.max(0, getMaxScroll());
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
        int maxScroll = Math.max(0, getMaxScroll());
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
        this.mTargetScrollPos = getScrollX();
        this.mOnWordSelectActionListener.onScrollContentChanged();
        if (needMoreWords()) {
            this.mGetMoreWordsHandler.requestMoreWords();
        }
        invalidate();
    }

    private void scrollToTarget() {
        if (this.mTargetScrollPos > getScrollX()) {
            double dist = this.mTargetScrollPos - getScrollX();
            scrollToX(getScrollX() + ((int) ((Math.pow(Math.min(dist, 100.0d) / 100.0d, 1.5d) * 12.0d) + 1.0d)));
            if (getScrollX() >= this.mTargetScrollPos) {
                scrollToX(this.mTargetScrollPos);
            }
        } else {
            double dist2 = getScrollX() - this.mTargetScrollPos;
            scrollToX(getScrollX() - ((int) ((Math.pow(Math.min(dist2, 100.0d) / 100.0d, 1.5d) * 12.0d) + 1.0d)));
            if (getScrollX() <= this.mTargetScrollPos) {
                scrollToX(this.mTargetScrollPos);
            }
        }
        this.mOnWordSelectActionListener.onScrollContentChanged();
        invalidate();
    }

    public void setMoreSuggestions(List<CharSequence> wordList, List<AtomicInteger> wordSourceList) {
        if (wordList != null) {
            this.mShowTooltip = false;
            this.mSuggestions.clear();
            for (int i = 0; i < wordList.size(); i++) {
                this.mSuggestions.add(wordList.get(i));
            }
            this.mWordSourceList.clear();
            if (wordSourceList != null && wordSourceList.size() > 0) {
                for (int i2 = 0; i2 < wordSourceList.size(); i2++) {
                    this.mWordSourceList.add(wordSourceList.get(i2));
                }
            }
        }
    }

    public void setSuggestions(List<CharSequence> wordList, int defaultWordIndex) {
        setSuggestions(wordList, defaultWordIndex, null);
    }

    @SuppressLint({"WrongCall"})
    public void setSuggestions(List<CharSequence> wordList, int defaultWordIndex, List<AtomicInteger> wordSourceList) {
        log.d("setSuggestions()");
        log.d("Swype", "CJK_Suggestions" + wordList);
        clear();
        if (wordList != null && wordList.size() > 0) {
            if (defaultWordIndex >= wordList.size()) {
                defaultWordIndex = 0;
            }
            this.mSelectedIndex = defaultWordIndex;
            setMoreSuggestions(wordList, wordSourceList);
            if (defaultWordIndex != -1) {
                touchWord(defaultWordIndex, this.mSuggestions.get(defaultWordIndex));
            }
        }
        onDraw(null);
        this.mOnWordSelectActionListener.onScrollContentChanged();
    }

    private boolean isHardKeyboardActive() {
        return IMEApplication.from(getContext()).getIME().isHardKeyboardActive();
    }

    public void scrollPrev() {
        if (isHardKeyboardActive()) {
            int i = 0;
            int count = this.mSuggestions.size();
            int firstItem = 0;
            int leftEdge = getScrollX();
            int iParentWidth = getContext().getResources().getDisplayMetrics().widthPixels;
            if (getParent() instanceof View) {
                iParentWidth = ((View) getParent()).getWidth();
            }
            int rightArrowSize = iParentWidth - getRight();
            if (this.mLeftArrowShowable) {
                leftEdge = (leftEdge - rightArrowSize) - this.mMinPadWidth;
            }
            while (true) {
                if (i >= count) {
                    break;
                }
                if (this.mWordX[i] >= leftEdge) {
                    if (i > 0) {
                        firstItem = i - 1;
                    } else {
                        firstItem = 0;
                    }
                } else {
                    if (this.mWordX[i] + this.mWordWidth[i] >= leftEdge - 1) {
                        firstItem = i;
                        break;
                    }
                    i++;
                }
            }
            int leftEdge2 = ((this.mWordX[firstItem] + this.mWordWidth[firstItem]) - getWidth()) + rightArrowSize;
            if (leftEdge2 < 0) {
                leftEdge2 = 0;
            }
            if (leftEdge2 >= getScrollX()) {
                leftEdge2 = Math.max(0, getScrollX() - getWidth());
            }
            updateScrollPosition(leftEdge2);
            return;
        }
        if (!this.mOnWordSelectActionListener.isScrollable(this)) {
            this.mOnWordSelectActionListener.prevBtnPressed(this);
            return;
        }
        int i2 = 0;
        int count2 = this.mSuggestions.size();
        int firstItem2 = 0;
        while (true) {
            if (i2 < count2) {
                if (this.mWordX[i2] < getScrollX() && this.mWordX[i2] + this.mWordWidth[i2] >= getScrollX() - 1) {
                    firstItem2 = i2;
                    break;
                }
                i2++;
            } else {
                break;
            }
        }
        int leftEdge3 = (this.mWordX[firstItem2] + this.mWordWidth[firstItem2]) - getWidth();
        if (leftEdge3 < 0) {
            leftEdge3 = 0;
        }
        if (leftEdge3 >= getScrollX()) {
            leftEdge3 = Math.max(0, getScrollX() - getWidth());
        }
        updateScrollPosition(leftEdge3);
    }

    public void scrollNext() {
        if (!this.mOnWordSelectActionListener.isScrollable(this)) {
            this.mOnWordSelectActionListener.nextBtnPressed(this);
            return;
        }
        if (isHardKeyboardActive()) {
            int targetX = getScrollX();
            int count = this.mSuggestions.size();
            int iParentWidth = getContext().getResources().getDisplayMetrics().widthPixels;
            if (getParent() instanceof View) {
                iParentWidth = ((View) getParent()).getWidth();
            }
            int rightArrowSize = iParentWidth - getRight();
            int rightEdge = (getScrollX() + getWidth()) - rightArrowSize;
            for (int i = 0; i < count; i++) {
                if (this.mWordX[i] > rightEdge || this.mWordX[i] + this.mWordWidth[i] >= rightEdge) {
                    targetX = Math.min(this.mWordX[i] + this.mLeftArrowWidth, this.mTotalLength - getWidth());
                    break;
                }
            }
            if (targetX <= getScrollX()) {
                targetX = rightEdge;
            }
            if (getLeft() < rightArrowSize) {
                targetX -= rightArrowSize;
            }
            updateScrollPosition(targetX);
            return;
        }
        int i2 = 0;
        int targetX2 = getScrollX();
        int count2 = this.mSuggestions.size();
        int rightEdge2 = getScrollX() + getWidth();
        while (true) {
            if (i2 >= count2) {
                break;
            }
            if (this.mWordX[i2] <= rightEdge2 && this.mWordX[i2] + this.mWordWidth[i2] >= rightEdge2) {
                targetX2 = Math.min(this.mWordX[i2], this.mTotalLength - getWidth());
                break;
            }
            i2++;
        }
        if (targetX2 <= getScrollX()) {
            targetX2 = rightEdge2;
        }
        updateScrollPosition(targetX2);
    }

    public void scrollLeftToHighlight(int index) {
        if (!this.mOnWordSelectActionListener.isScrollable(this)) {
            this.mOnWordSelectActionListener.prevBtnPressed(this);
            return;
        }
        int leftEdge = (this.mWordX[index] + this.mWordWidth[index]) - getWidth();
        if (leftEdge < 0) {
            leftEdge = 0;
        }
        if (leftEdge >= getScrollX()) {
            leftEdge = Math.max(0, getScrollX() - getWidth());
        }
        updateScrollPosition(leftEdge);
    }

    public void scrollRightToHighLight(int index) {
        if (!this.mOnWordSelectActionListener.isScrollable(this)) {
            this.mOnWordSelectActionListener.nextBtnPressed(this);
            return;
        }
        int targetX = getScrollX();
        int count = this.mSuggestions.size();
        int rightEdge = getScrollX() + getWidth();
        if (index >= 0 && index < count) {
            targetX = Math.min(this.mWordX[index], this.mTotalLength - getWidth());
        }
        if (targetX <= getScrollX()) {
            targetX = rightEdge;
        }
        updateScrollPosition(targetX);
    }

    public void updateScrollPosition(int targetX) {
        if (targetX != getScrollX()) {
            this.mTargetScrollPos = targetX;
            if (needMoreWords()) {
                this.mGetMoreWordsHandler.requestMoreWords();
            }
            invalidate();
            this.mScrolled = true;
        }
    }

    public void clear() {
        this.mShowTooltip = false;
        this.mSuggestions.clear();
        this.mWordSourceList.clear();
        this.mHandler.removeMessages(3);
        this.mTouchY = -1;
        this.mTouchX = -1;
        this.mSelectedString = null;
        this.mSelectedIndex = -1;
        this.mTotalLength = 0;
        invalidate();
        Arrays.fill(this.mWordWidth, 0);
        Arrays.fill(this.mWordX, 0);
        scrollToX(0);
        this.mTargetScrollPos = 0;
        this.mA = 0.0f;
        this.mV = 0.0f;
        if (this.mOnWordSelectActionListener != null) {
            this.mOnWordSelectActionListener.onScrollContentChanged();
        }
    }

    public void selectActiveWord() {
        if (this.mSelectedString == null && this.mSelectedIndex >= 0) {
            this.mOnWordSelectActionListener.selectWord(this.mSelectedIndex, this.mSuggestions.get(this.mSelectedIndex), this);
        } else {
            trySelect();
        }
    }

    protected void trySelect() {
        if (this.mSelectedString != null) {
            this.mOnWordSelectActionListener.selectWord(this.mSelectedIndex, this.mSelectedString, this);
        }
        touchWord(this.mSelectedIndex, null);
    }

    protected void trySelectWCLMsg() {
        this.mOnWCLSelectActionListener.selectWord(this.mSelectedIndex, this.mSelectedString, this);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // android.view.View
    @SuppressLint({"ClickableViewAccessibility"})
    public boolean onTouchEvent(MotionEvent me) {
        if (!this.mGestureDetector.onTouchEvent(me)) {
            int action = me.getAction();
            int x = (int) me.getX();
            int y = (int) me.getY();
            this.mTouchY = y;
            this.mTouchX = x;
            switch (action) {
                case 0:
                    this.xDown = this.mTouchX;
                    this.mMouseDown = true;
                    this.mDragSelected = false;
                    this.mAbortKey = false;
                    this.mSwipeSpeed.flush();
                    this.mA = 0.0f;
                    this.mV = 0.0f;
                    this.mScrolled = false;
                    this.mTouchIndex = -1;
                    invalidate();
                    if (!this.mHandler.hasMessages(3)) {
                        this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(3), this.mLongPressTimeout);
                        break;
                    }
                    break;
                case 1:
                    this.mHandler.removeMessages(3);
                    this.mMouseDown = false;
                    this.mDragSelected = false;
                    if (!this.mSwipeSpeed.isFlushed()) {
                        setVelocity(this.mSwipeSpeed.get());
                    }
                    if (!this.mScrolled && this.mTouchIndex != -1 && this.mSuggestions.size() > 0 && this.mTouchIndex < this.mSuggestions.size() && !this.mAbortKey) {
                        touchWord(this.mTouchIndex, this.mSuggestions.get(this.mTouchIndex));
                        trySelect();
                    }
                    removeHighlight();
                    if (this.mShowTooltip && Tooltip.WCL_TOOLTIP.equals(this.mTooltip)) {
                        trySelectWCLMsg();
                    }
                    if (!this.mScrolled && this.mTouchIndex != -1 && this.mSuggestions.size() > 0 && this.mTouchIndex < this.mSuggestions.size()) {
                        setFingerUpListener();
                        break;
                    }
                    break;
                case 2:
                    if (y <= 0 && !this.mDragSelected) {
                        this.mDragSelected = true;
                    }
                    onMoveHandler(me);
                    break;
            }
        }
        return true;
    }

    private void setFingerUpListener() {
        if (this.mAbortKey && this.mOnWordSelectActionListener != null && this.mTouchIndex != -1) {
            int wdSource = 0;
            if (this.mWordSourceList != null && this.mWordSourceList.size() > 0) {
                if (this.mTouchIndex < this.mWordSourceList.size()) {
                    wdSource = this.mWordSourceList.get(this.mTouchIndex).get();
                } else {
                    return;
                }
            }
            this.mOnWordSelectActionListener.onPressReleaseCandidate(this.mTouchIndex, this.mSuggestions.get(this.mTouchIndex).toString(), wdSource);
            touchWord(this.mSelectedIndex, null);
        }
    }

    public void setAlignLeft(boolean alignLeft) {
        this.alignLeft = alignLeft;
    }

    public void setFullScroll(boolean allow) {
        this.allowFullScroll = allow;
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

    /* JADX INFO: Access modifiers changed from: protected */
    public void removeHighlight() {
        this.mTouchY = -1;
        this.mTouchX = -1;
        invalidate();
    }

    protected void longPressFirstWord() {
    }

    public void setGetMoreWordsHandler(GetMoreWordsHandler getMoreWordsHandler) {
        this.mGetMoreWordsHandler = getMoreWordsHandler;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean needMoreWords() {
        return this.mGetMoreWordsHandler != null && this.mTargetScrollPos + (getWidth() * 2) >= this.mTotalLength;
    }

    @Override // android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    protected void scrollToX(int newScrollX) {
        scrollTo(newScrollX, getScrollY());
    }

    public void readStyles(int aResid) {
        TypedArrayWrapper a = null;
        if (aResid == R.style.WordListView) {
            a = ThemeLoader.getInstance().obtainStyledAttributes$6d3b0587(this.mContext, null, R.styleable.WordListView, 0, R.style.WordListView, R.xml.defaults, "WordListView");
        } else if (aResid == R.style.CHNPYPrefixListView) {
            a = ThemeLoader.getInstance().obtainStyledAttributes$6d3b0587(this.mContext, null, R.styleable.WordListView, 0, aResid, R.xml.defaults, "CHNPYPrefixListView");
        } else if (aResid == R.style.CHNHWCharacterListView) {
            a = ThemeLoader.getInstance().obtainStyledAttributes$6d3b0587(this.mContext, null, R.styleable.WordListView, 0, aResid, R.xml.defaults, "CHNHWCharacterListView");
        }
        if (a == null) {
            return;
        }
        int n = a.delegateTypedArray.getIndexCount();
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
                this.mTextSizeInit = a.getDimensionPixelSize(attr, 20);
            } else if (attr == R.styleable.WordListView_textSize) {
                this.mAlphaTextSize = a.getDimensionPixelSize(attr, 20);
            } else if (attr == R.styleable.WordListView_wordSpace) {
                this.mWordSpace = a.getDimensionPixelSize(attr, 10);
            } else if (attr == R.styleable.WordListView_candidatePressed) {
                this.mColorPressed = a.getColor(attr, -65536);
            }
        }
        a.recycle();
    }

    public void enableHighlight() {
        this.mEnableHighlight = true;
    }

    public void disableHighlight() {
        this.mEnableHighlight = false;
    }

    public int wordCount() {
        if (this.mSuggestions != null) {
            return this.mSuggestions.size();
        }
        return 0;
    }

    public void setLeftArrowStatus(boolean leftArrow) {
        this.mLeftArrowShowable = leftArrow;
    }

    public boolean isCandidateListEmpty() {
        return this.mSuggestions == null || this.mSuggestions.isEmpty();
    }

    public void abortKey() {
        this.mAbortKey = true;
    }

    public int getLeftWidth() {
        return getLeft();
    }

    public int getRightWidth() {
        return getRight();
    }

    public boolean getLeftArrowStatus() {
        return this.mLeftArrowShowable;
    }

    public int getFirstCandidateIndexInNextOrPreviousPage(boolean isNextPage) {
        int i = 0;
        int count = this.mSuggestions.size();
        if (isHardKeyboardActive()) {
            if (isNextPage) {
                int rightEdge = getScrollX() + getWidth();
                while (i < count) {
                    if (this.mWordX[i] >= rightEdge || this.mWordX[i] + this.mWordWidth[i] >= rightEdge) {
                        int firstItem = i;
                        return firstItem;
                    }
                    i++;
                }
                return -1;
            }
            int leftEdge = getScrollX();
            if (this.mLeftArrowShowable) {
                leftEdge = (leftEdge - this.mLeftArrowWidth) - this.mMinPadWidth;
            }
            int i2 = count - 1;
            while (i2 > 0 && this.mWordX[i2] >= leftEdge) {
                i2--;
            }
            int firstItem2 = i2;
            return firstItem2;
        }
        if (isNextPage) {
            int rightEdge2 = getScrollX() + getWidth();
            while (i < count) {
                if (this.mWordX[i] <= rightEdge2 && this.mWordX[i] + this.mWordWidth[i] >= rightEdge2) {
                    int firstItem3 = i;
                    return firstItem3;
                }
                i++;
            }
            return -1;
        }
        while (i < count) {
            if (this.mWordX[i] < getScrollX() && this.mWordX[i] + this.mWordWidth[i] >= getScrollX() - 1) {
                int firstItem4 = i;
                return firstItem4;
            }
            i++;
        }
        return -1;
    }

    public boolean isKeyOutofRightBound(int i) {
        int rightEdge = getScrollX() + getWidth();
        if (this.mWordX[i] >= rightEdge) {
            return true;
        }
        return this.mWordX[i] <= rightEdge && this.mWordX[i] + this.mWordWidth[i] >= rightEdge;
    }

    public boolean isKeyOutofLeftBound(int i) {
        if (this.mWordX[i] < getScrollX()) {
            return true;
        }
        return this.mWordX[i] < getScrollX() && this.mWordX[i] + this.mWordWidth[i] >= getScrollX() + (-1);
    }

    public void showMessageOnWCL(Candidates candidates) {
        if (this.mSuggestions == null || this.mSuggestions.isEmpty()) {
            this.mWCLTooltip = candidates;
            this.mShowTooltip = true;
            computeDisplayingCandidateDrawingRegion(Tooltip.WCL_TOOLTIP);
            this.mTooltip = Tooltip.WCL_TOOLTIP;
            requestLayout();
            invalidate();
        }
    }

    public void showSwypeTooltip() {
        if ((this.mSuggestions == null || this.mSuggestions.isEmpty()) && UserManagerCompat.isUserUnlocked(this.mContext)) {
            this.mShowTooltip = true;
            computeDisplayingCandidateDrawingRegion(Tooltip.SWYPE_TOOLTIP);
            this.mTooltip = Tooltip.SWYPE_TOOLTIP;
            requestLayout();
            invalidate();
        }
    }

    public void showNotMatchTootip() {
        if (this.mSuggestions == null || this.mSuggestions.isEmpty()) {
            this.mShowTooltip = true;
            computeDisplayingCandidateDrawingRegion(Tooltip.NOTMATCH_TOOLTIP);
            this.mTooltip = Tooltip.NOTMATCH_TOOLTIP;
            requestLayout();
            invalidate();
        }
    }

    public void hideTooltip() {
        if (this.mShowTooltip) {
            this.mShowTooltip = false;
            invalidate();
        }
    }

    public boolean isShowingSwypeTooltip() {
        return this.mShowTooltip;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getMaxScroll() {
        if (this.mSuggestions == null) {
            return 0;
        }
        int maxScroll = 0;
        if (!this.allowFullScroll) {
            maxScroll = this.mTotalLength - getWidth();
        } else if (this.mWordWidth.length > 0) {
            maxScroll = (this.mTotalLength - this.mWordWidth[this.mWordWidth.length - 1]) - getLeftWidth();
        }
        return Math.max(0, maxScroll);
    }

    private Candidates getTooltip(Tooltip tooltip) {
        switch (tooltip) {
            case NOTMATCH_TOOLTIP:
                if (this.mNotMatchTooltip == null) {
                    this.mNotMatchTooltip = new Candidates(IMEApplication.from(getContext()).getToolTips().createNoMatchTip());
                }
                return this.mNotMatchTooltip;
            case SWYPE_TOOLTIP:
                if (this.mSwypeTooltip == null) {
                    this.mSwypeTooltip = new Candidates(IMEApplication.from(getContext()).getToolTips().createSwypeKeyTip());
                }
                return this.mSwypeTooltip;
            case WCL_TOOLTIP:
                return this.mWCLTooltip;
            default:
                return null;
        }
    }

    private void computeDisplayingCandidateDrawingRegion(Tooltip t) {
        int width;
        Candidates tooltip = getTooltip(t);
        boolean needRefresh = this.mTooltip == null || this.mTooltip != t;
        this.mTotalLength = 0;
        this.mPaint.setTextSize(this.mAlphaTextSize);
        this.mPaint.setTypeface(Typeface.DEFAULT_BOLD);
        if (needRefresh || this.mWordWidthWithoutPadding == null || this.mTooltipWordWidth == null) {
            this.mWordWidthWithoutPadding = new int[tooltip.count()];
            this.mTooltipWordWidth = new int[tooltip.count()];
            int padding = (int) this.mPaint.measureText(XMLResultsHandler.SEP_SPACE, 0, 1);
            int count = tooltip.count();
            for (int index = 0; index < count; index++) {
                WordCandidate candidate = tooltip.get(index);
                if (candidate.source() == WordCandidate.Source.WORD_SOURCE_DRAWABLE) {
                    Drawable icon = ((DrawableCandidate) candidate).icon;
                    icon.setBounds(0, 0, icon.getIntrinsicWidth(), getHeight());
                    width = icon.getIntrinsicWidth();
                    this.mWordWidthWithoutPadding[index] = width;
                } else {
                    this.mPaint.setTypeface(Typeface.DEFAULT);
                    String word = candidate.toString();
                    this.mWordWidthWithoutPadding[index] = (int) this.mPaint.measureText(word);
                    width = this.mWordWidthWithoutPadding[index] + padding;
                }
                this.mTooltipWordWidth[index] = width;
                this.mTotalLength += width;
            }
        } else {
            for (int w : this.mTooltipWordWidth) {
                this.mTotalLength += w;
            }
        }
        this.mPaint.setTextSize(this.mTextSize);
        this.mPaint.setTypeface(Typeface.DEFAULT);
    }

    private void drawToolTip(Canvas canvas) {
        Candidates tooltip;
        if (canvas != null && this.mTooltip != null && (tooltip = getTooltip(this.mTooltip)) != null) {
            this.mPaint.setTextSize(this.mAlphaTextSize);
            this.mPaint.setTypeface(Typeface.DEFAULT_BOLD);
            int count = tooltip.count();
            int x = 0;
            int y = ((int) ((getHeight() + this.mPaint.getTextSize()) - this.mDescent)) / 2;
            Paint paint = this.mPaint;
            this.mPaint.setColor(this.mColorRecommended);
            this.mTotalLength = 0;
            for (int i = 0; i < count; i++) {
                WordCandidate candidate = tooltip.get(i);
                WordCandidate.Source source = candidate.source();
                float textWidth = this.mWordWidthWithoutPadding[i];
                int wordWidth = this.mTooltipWordWidth[i];
                if (source != WordCandidate.Source.WORD_SOURCE_DRAWABLE) {
                    CharSequence suggestion = candidate.toString();
                    canvas.drawText(suggestion, 0, suggestion.length(), ((wordWidth - textWidth) / 2.0f) + x, y, paint);
                } else {
                    Drawable icon = ((DrawableCandidate) candidate).icon;
                    int availableHeight = (getHeight() - this.mBgPadding.top) - this.mBgPadding.bottom;
                    int iconTop = (availableHeight - icon.getIntrinsicHeight()) / 2;
                    double iconScale = 1.0d;
                    if (iconTop < 0) {
                        iconTop = this.mBgPadding.top;
                        iconScale = availableHeight / icon.getIntrinsicWidth();
                    }
                    icon.setBounds(0, iconTop, (int) (icon.getIntrinsicWidth() * iconScale), (int) (iconTop + (icon.getIntrinsicHeight() * iconScale)));
                    canvas.save();
                    canvas.translate(((wordWidth - ((int) textWidth)) / 2) + x, 0.0f);
                    icon.draw(canvas);
                    canvas.restore();
                }
                x += wordWidth;
                this.mTotalLength += wordWidth;
            }
            this.mPaint.setTextSize(this.mTextSize);
            this.mPaint.setTypeface(Typeface.DEFAULT);
        }
    }

    public int getSelectCandidateColorPressed() {
        return this.mColorPressed;
    }

    @Override // android.view.View
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    public void setAltCharacterConverted(boolean converted) {
        this.altCharacterConverted = converted;
    }

    public boolean getAltCharacterConverted() {
        return this.altCharacterConverted;
    }

    public void setExactKeyboardShowable(boolean showable) {
        this.mExactKeyboardShowable = showable;
    }

    public boolean isExactKeyboardShowable() {
        return this.mExactKeyboardShowable;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void getCandidateSize() {
        UserPreferences sp = IMEApplication.from(getContext()).getUserPreferences();
        this.mTextSize = (int) (this.mTextSizeInit * ThemesPrefs.getCandidatesSize(sp, "Candidates_Size", 1.0f));
    }

    public void updateCandidatesSize() {
        if (this.mPaint != null) {
            getCandidateSize();
            this.mPaint.setTextSize(this.mTextSize);
        }
    }

    public void scrollHead() {
        int leftEdge = (this.mWordX[0] + this.mWordWidth[0]) - getWidth();
        if (leftEdge < 0) {
            leftEdge = 0;
        }
        if (leftEdge >= getScrollX()) {
            leftEdge = Math.max(0, getScrollX() - getWidth());
        }
        updateScrollPosition(leftEdge);
    }

    public void scrollEnd() {
        updateScrollPosition(this.mTotalLength);
    }

    public int getLeftDistance() {
        return this.mLeftArrowWidth;
    }

    public int getCenterCandidateIndex() {
        int i = 0;
        int item = -1;
        int count = this.mSuggestions.size();
        if (count <= 0) {
            return -1;
        }
        int rightEdge = getScrollX() + getWidth();
        while (true) {
            if (i < count) {
                if (this.mWordX[i] <= rightEdge && this.mWordX[i] + this.mWordWidth[i] >= rightEdge + 1) {
                    item = i;
                    break;
                }
                i++;
            } else {
                break;
            }
        }
        if (item == -1) {
            item = count;
        }
        return item / 2;
    }

    private String getEmojiText(String emoji_unicode) {
        String emojiCode;
        if (emoji_unicode == null) {
            return null;
        }
        Emoji emoji = EmojiLoader.getEmoji(emoji_unicode);
        if (emoji == null || (emojiCode = getDefaultSkinToneCode(emoji)) == null) {
            return emoji_unicode;
        }
        this.mEmojis.put(emoji_unicode, emojiCode);
        return emojiCode;
    }

    private String getDefaultSkinToneCode(Emoji emoji) {
        Emoji defaultSkinToneEmoji = EmojiLoader.getDefaultSkinToneCode(getContext(), emoji, this.emojiCacheManager);
        if (defaultSkinToneEmoji == null) {
            return null;
        }
        return defaultSkinToneEmoji.getEmojiDisplayCode();
    }

    void onMoveHandler(MotionEvent ev) {
        int pointerCount = ev.getPointerCount();
        int historySize = ev.getHistorySize();
        for (int idxHistory = 0; idxHistory < historySize; idxHistory++) {
            for (int idxPointer = 0; idxPointer < pointerCount; idxPointer++) {
                float xPos = ev.getHistoricalX(idxPointer, idxHistory);
                float yPos = ev.getHistoricalY(idxPointer, idxHistory);
                float xOffset = getOffsetX(xPos);
                raiseOnPressMoveCandidate(xPos, yPos, xOffset);
            }
        }
        for (int pointerIndex = 0; pointerIndex < pointerCount; pointerIndex++) {
            float xPos2 = ev.getX(pointerIndex);
            float yPos2 = ev.getY(pointerIndex);
            float xOffset2 = getOffsetX(xPos2);
            raiseOnPressMoveCandidate(xPos2, yPos2, xOffset2);
        }
    }

    private void raiseOnPressMoveCandidate(float xPos, float yPos, float xOffset) {
        if (this.mOnWordSelectActionListener != null && this.mTouchIndex != -1) {
            this.mOnWordSelectActionListener.onPressMoveCandidate(xPos, yPos, xOffset);
        }
    }

    public float getOffsetX(float xPos) {
        return xPos - this.xDown;
    }

    public void setHorizontalScroll(boolean mScroll) {
        this.mScroll = mScroll;
    }

    public int getTargetScrollX() {
        return this.mTargetScrollPos;
    }
}
