package com.nuance.swype.input;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputConnection;
import com.nuance.android.compat.ViewCompat;
import com.nuance.android.util.WeakReferenceHandler;
import com.nuance.input.swypecorelib.Candidates;
import com.nuance.input.swypecorelib.WordCandidate;
import com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.xmlResults.XMLResultsHandler;
import com.nuance.swype.input.emoji.Emoji;
import com.nuance.swype.input.emoji.EmojiCacheManager;
import com.nuance.swype.input.emoji.EmojiLoader;
import com.nuance.swype.input.settings.ThemesPrefs;
import com.nuance.swype.plugin.ThemeLoader;
import com.nuance.swype.plugin.TypedArrayWrapper;
import com.nuance.swype.util.LogManager;
import com.nuance.swype.widget.directional.DirectionalUtil;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public abstract class CandidatesListView extends View {
    private static final int LONG_PRESS_DELAY = 750;
    protected static final int MAX_SUGGESTIONS = 10;
    private static final int MSG_LONG_PRESS = 1;
    protected static final int OUT_OF_BOUNDS = -1;
    protected static final int SCROLL_PIXELS = 12;
    private String activeCandidate;
    private boolean alignLeft;
    private boolean allowFullScroll;
    private final Mapper defaultMapper;
    private EmojiCacheManager<String, Integer> emojiCacheManager;
    private final Handler.Callback handlerCallback;
    private boolean highlightActiveCandidate;
    private boolean highlightAddtoDictionaryTip;
    private boolean isHardwareKeyboardUsed;
    private boolean isRtl;
    private WeakReference<CandidateListener> listener;
    protected float mA;
    protected Rect mBgPadding;
    protected final List<WeakReference<CandidateListener>> mCandidateListeners;
    protected int mColorNormal;
    protected int mColorOther;
    protected int mColorPressed;
    protected int mColorRecommended;
    protected int mColorUDBTip;
    protected final Drawable mDefaultWordHighlight;
    protected final int mDescent;
    private boolean mDisableCandidateDisplay;
    protected final Drawable mDivider;
    protected boolean mDragSelected;
    protected final GestureDetector mGestureDetector;
    private int mGravity;
    private final Handler mHandler;
    private final WeakReference<IME> mIme;
    protected final Typeface mItalic;
    private boolean mLongPressHandled;
    protected final int mMinCandidateWidth;
    protected boolean mMouseDown;
    protected List<Integer> mPadWidth;
    public final Paint mPaint;
    protected boolean mScroll;
    protected boolean mScrolled;
    protected WordCandidate mSelectedCandidate;
    protected int mSelectedIndex;
    protected final Drawable mSelectionHighlight;
    protected Candidates mSuggestions;
    RollAverage mSwipeSpeed;
    protected final Drawable mTappedWordHighlight;
    protected int mTargetScrollX;
    protected int mTextPadding;
    public int mTextSize;
    private int mTextSizeInit;
    protected int mTotalWidth;
    protected int mTouchIndex;
    protected int mTouchX;
    protected float mV;
    protected List<Integer> mWordWidth;
    protected List<Integer> mWordWidthWithoutPadding;
    protected List<Integer> mWordX;
    private final Mapper reverseMapper;
    private Typeface typeface;
    private Typeface typefaceBold;
    private float xDown;
    private float yDown;
    protected static final String TAG = "CandidatesListView";
    protected static final LogManager.Log log = LogManager.getLog(TAG);
    private static final int[] CAND_STATE_DEFAULT_NORMAL = new int[0];
    private static final int[] CAND_STATE_DEFAULT_PRESSED = {android.R.attr.state_pressed};

    /* loaded from: classes.dex */
    public interface CandidateListener {
        void onCandidatesUpdated(Candidates candidates);

        boolean onPressHoldCandidate(WordCandidate wordCandidate, Candidates candidates);

        void onPressMoveCandidate(float f, float f2, float f3);

        boolean onPressReleaseCandidate(WordCandidate wordCandidate, Candidates candidates);

        boolean onSelectCandidate(WordCandidate wordCandidate, Candidates candidates);
    }

    /* loaded from: classes.dex */
    public enum Format {
        DEFAULT,
        FIT_AS_MUCH,
        NONE
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public interface Mapper {
        int map(int i);

        int map(int i, int i2);
    }

    abstract int getMinCandidateWidth(Context context);

    abstract void setColor(Paint paint, int i);

    abstract void setTypeface(Paint paint, Typeface typeface);

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

    /* JADX INFO: Access modifiers changed from: protected */
    public CandidatesListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mTouchX = -1;
        this.mWordWidth = new ArrayList();
        this.mPadWidth = new ArrayList();
        this.mWordX = new ArrayList();
        this.mWordWidthWithoutPadding = new ArrayList();
        this.mScroll = true;
        this.mTouchIndex = -1;
        this.mDisableCandidateDisplay = false;
        this.mGravity = 17;
        this.handlerCallback = new Handler.Callback() { // from class: com.nuance.swype.input.CandidatesListView.1
            @Override // android.os.Handler.Callback
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        CandidatesListView.this.handleLongPress();
                        return true;
                    default:
                        return false;
                }
            }
        };
        this.isRtl = false;
        this.mSwipeSpeed = new RollAverage(0.3f);
        this.defaultMapper = new Mapper() { // from class: com.nuance.swype.input.CandidatesListView.3
            @Override // com.nuance.swype.input.CandidatesListView.Mapper
            public int map(int x) {
                return x;
            }

            @Override // com.nuance.swype.input.CandidatesListView.Mapper
            public int map(int x, int itemWidth) {
                return x;
            }
        };
        this.reverseMapper = new Mapper() { // from class: com.nuance.swype.input.CandidatesListView.4
            @Override // com.nuance.swype.input.CandidatesListView.Mapper
            public int map(int x) {
                return CandidatesListView.this.getWidth() - x;
            }

            @Override // com.nuance.swype.input.CandidatesListView.Mapper
            public int map(int x, int itemWidth) {
                return (CandidatesListView.this.getWidth() - x) - itemWidth;
            }
        };
        this.mHandler = WeakReferenceHandler.create(this.handlerCallback);
        readStyles(context);
        IMEApplication app = IMEApplication.from(context);
        this.mIme = new WeakReference<>(app.getIME());
        this.emojiCacheManager = EmojiCacheManager.from(context);
        this.mSelectionHighlight = app.getThemedDrawable(R.attr.listSelectorBackgroundPressed);
        this.mDefaultWordHighlight = app.getThemedDrawable(R.attr.wclDefaultWordBackground);
        ShapeDrawable shapeDrawable = new ShapeDrawable();
        setColor(shapeDrawable.getPaint(), app.getThemedColor(R.attr.listSelectorBackgroundTappedColor));
        this.mTappedWordHighlight = shapeDrawable;
        this.mDivider = app.getThemedDrawable(R.attr.keyboardSuggestStripDivider);
        this.mPaint = new Paint();
        setColor(this.mPaint, this.mColorNormal);
        this.mPaint.setAntiAlias(true);
        this.mPaint.setTextSize(this.mTextSize);
        this.mPaint.setStrokeWidth(0.0f);
        this.mDescent = (int) this.mPaint.descent();
        this.mItalic = Typeface.create(Typeface.SERIF, 2);
        setTypeface(this.mPaint, Typeface.DEFAULT);
        this.typeface = Typeface.DEFAULT;
        String typefaceNormal = context.getString(R.string.custom_wcl_font_normal);
        if (!TextUtils.isEmpty(typefaceNormal)) {
            this.typeface = Typeface.create(typefaceNormal, 0);
        }
        this.typefaceBold = getResources().getBoolean(R.bool.font_typeface_bold) ? Typeface.DEFAULT_BOLD : Typeface.DEFAULT;
        String typefaceBoldName = context.getString(R.string.custom_wcl_font_bold);
        if (!TextUtils.isEmpty(typefaceBoldName)) {
            this.typefaceBold = Typeface.create(typefaceBoldName, 0);
        }
        setTypeface(this.mPaint, this.typeface);
        this.mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() { // from class: com.nuance.swype.input.CandidatesListView.2
            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if ((CandidatesListView.this.getMaxScroll() <= 0 && CandidatesListView.this.getMinScroll() >= 0) || !CandidatesListView.this.mScroll) {
                    return false;
                }
                CandidatesListView.this.mScrolled = true;
                CandidatesListView.this.mA = 0.0f;
                CandidatesListView.this.mV = 0.0f;
                CandidatesListView.this.scrollToX(CandidatesListView.this.getScrollX() + ((int) CandidatesListView.this.pull(distanceX)));
                CandidatesListView.this.mTargetScrollX = CandidatesListView.this.getScrollX();
                CandidatesListView.this.mSwipeSpeed.add(distanceX);
                CandidatesListView.this.mHandler.removeMessages(1);
                CandidatesListView.this.invalidate();
                return true;
            }
        });
        this.mGestureDetector.setIsLongpressEnabled(false);
        setHorizontalFadingEdgeEnabled(true);
        setWillNotDraw(false);
        setHorizontalScrollBarEnabled(false);
        setVerticalScrollBarEnabled(false);
        this.mCandidateListeners = new ArrayList();
        clear();
        this.mMinCandidateWidth = getMinCandidateWidth(context);
    }

    public void setFullScroll(boolean allow) {
        this.allowFullScroll = allow;
    }

    public void setAlignLeft(boolean alignLeft) {
        this.alignLeft = alignLeft;
        if (ViewCompat.isHardwareAccelerated(this)) {
            invalidate();
        }
    }

    public void addOnWordSelectActionListener(CandidateListener newlistener) {
        if (newlistener != null) {
            Iterator<WeakReference<CandidateListener>> it = this.mCandidateListeners.iterator();
            while (it.hasNext()) {
                CandidateListener listener = it.next().get();
                if (listener != null && listener == newlistener) {
                    return;
                }
            }
            this.mCandidateListeners.add(new WeakReference<>(newlistener));
        }
    }

    public void clearOnWordSelectActionListeners() {
        this.mCandidateListeners.clear();
    }

    @Override // android.view.View
    public int computeHorizontalScrollRange() {
        return this.mTotalWidth;
    }

    public void touchWord(int i, WordCandidate candidate) {
        this.mSelectedIndex = i;
        this.mSelectedCandidate = candidate;
    }

    private Mapper getMapper() {
        return getRTLDrawingSetting() ? this.reverseMapper : this.defaultMapper;
    }

    @Override // android.view.View
    @SuppressLint({"RtlHardcoded"})
    protected void onDraw(Canvas canvas) {
        if (this.mSuggestions != null && !this.mDisableCandidateDisplay) {
            if (this.mBgPadding == null) {
                this.mBgPadding = new Rect(0, 0, 0, 0);
                if (getBackground() != null) {
                    getBackground().getPadding(this.mBgPadding);
                }
            }
            int divWidth = this.mDivider.getIntrinsicWidth();
            Drawable drawable = this.mDivider;
            int i = this.mBgPadding.top;
            if (divWidth <= 0) {
                divWidth = 0;
            }
            drawable.setBounds(0, i, divWidth, getHeight() - this.mBgPadding.bottom);
            int viewWidth = getWidth();
            Mapper mapper = getMapper();
            int itemCount = this.mSuggestions.count();
            Rect bgPadding = this.mBgPadding;
            int touchX = this.mTouchX;
            boolean scrolled = this.mScrolled;
            int height = getHeight();
            int y = ((int) ((height + this.mPaint.getTextSize()) - this.mDescent)) / 2;
            int contentWidth = this.mTotalWidth;
            boolean forceCenter = this.mSuggestions.source() == Candidates.Source.TOOL_TIP || this.mSuggestions.source() == Candidates.Source.UDB_EDIT;
            int x = 0;
            if (!this.alignLeft && contentWidth < viewWidth) {
                x = (forceCenter || !(this.mGravity == 3 || this.mGravity == 8388611)) ? (viewWidth - contentWidth) / 2 : 0;
            }
            if (this.mSuggestions.hasAttribute(1) && viewWidth <= contentWidth) {
                x += viewWidth / 2;
                for (int i2 = 0; i2 < itemCount; i2++) {
                    x -= this.mWordWidth.get(i2).intValue() / 2;
                }
            }
            if (this.mSuggestions.hasAttribute(2) && this.mMouseDown) {
                int xOffset = x;
                if (viewWidth <= contentWidth) {
                    xOffset = (viewWidth - contentWidth) / 2;
                }
                draw(canvas, mapper, this.mSelectionHighlight, xOffset, 0, bgPadding.top, contentWidth, getHeight() - bgPadding.bottom);
                this.mTouchIndex = 0;
            }
            boolean isShortWordList = (this.mSuggestions.get(0).source() == WordCandidate.Source.WORD_SOURCE_DRAWABLE || this.mSuggestions.source() == Candidates.Source.TOOL_TIP || this.mSuggestions.hasAttribute(2) || this.mTotalWidth >= viewWidth) ? false : true;
            if (isShortWordList) {
                if (this.isRtl) {
                    canvas.save();
                    canvas.translate(x, 0.0f);
                    this.mDivider.draw(canvas);
                    canvas.restore();
                } else {
                    this.mDivider.draw(canvas);
                }
            }
            int lastIndex = itemCount - 1;
            for (int i3 = 0; i3 < itemCount; i3++) {
                int wordWidth = drawCandidate(canvas, mapper, i3, lastIndex, x, y, height, touchX);
                x += wordWidth;
            }
            if (isShortWordList) {
                draw(canvas, mapper, this.mDivider, x);
            }
            if (!scrolled && this.mSuggestions.source() == Candidates.Source.NEXT_WORD_PREDICTION && this.mTotalWidth > viewWidth) {
                this.mTargetScrollX = this.mWordX.get(this.mSelectedIndex).intValue() - ((viewWidth - this.mWordWidth.get(this.mSelectedIndex).intValue()) / 2);
            }
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

    void drawCandBackground(Canvas canvas, Mapper mapper, WordCandidate cand, int idx, int offset, boolean isPressed, Rect bounds) {
        if (idx == 0 && this.mSuggestions.getDefaultCandidateIndex() != this.mSuggestions.getExactCandidateIndex() && this.mSuggestions.getExactCandidateIndex() == 0) {
            draw(canvas, mapper, this.mTappedWordHighlight, offset, bounds);
        } else if (this.mDefaultWordHighlight != null && this.mSuggestions.getSmartSelectionIndex() == idx) {
            int[] drawableState = isPressed ? CAND_STATE_DEFAULT_PRESSED : CAND_STATE_DEFAULT_NORMAL;
            this.mDefaultWordHighlight.setState(drawableState);
            draw(canvas, mapper, this.mDefaultWordHighlight, offset, bounds);
        }
    }

    private void drawCandidateText(Canvas canvas, Mapper mapper, Paint paint, WordCandidate candidate, int wordWidth, int textWidth, int x, int y, boolean isPressed) {
        CharSequence suggestion = candidate.toString();
        int contextLen = candidate.contextPredictLength();
        int wordStart = mapper.map(x + ((wordWidth - textWidth) / 2), textWidth);
        Paint contextPaint = new Paint(paint);
        if (isPressed) {
            setColor(contextPaint, this.mColorPressed);
        } else {
            setColor(contextPaint, this.mColorRecommended);
        }
        setTypeface(contextPaint, this.mItalic);
        if (contextLen != 0) {
            canvas.drawText(suggestion, 0, contextLen, wordStart, y, contextPaint);
            wordStart = (int) (wordStart + contextPaint.measureText(candidate.contextPredict()));
        }
        int highlightLen = contextLen != 0 ? 0 : Math.min(candidate.contextKillLength(), suggestion.length() - 1);
        if (highlightLen != 0) {
            log.d("suggestions-", suggestion, "contextLen-", Integer.valueOf(contextLen), " highlightLen-", Integer.valueOf(highlightLen));
            canvas.drawText(suggestion, contextLen, highlightLen, wordStart, y, contextPaint);
            wordStart = (int) (wordStart + contextPaint.measureText(candidate.word().substring(0, highlightLen)));
        }
        canvas.drawText(suggestion, contextLen + highlightLen, suggestion.length(), wordStart, y, paint);
    }

    void draw(Canvas canvas, Mapper mapper, Drawable drawable, int offset, int left, int top, int right, int bottom) {
        if (right - left > 0 && bottom - top > 0) {
            canvas.save();
            canvas.translate(mapper.map(offset, right - left), 0.0f);
            drawable.setBounds(left, top, right, bottom);
            drawable.draw(canvas);
            canvas.restore();
        }
    }

    void draw(Canvas canvas, Mapper mapper, Drawable drawable, int offset, Rect bounds) {
        if (bounds != null) {
            draw(canvas, mapper, drawable, offset, bounds.left, bounds.top, bounds.right, bounds.bottom);
        }
    }

    void draw(Canvas canvas, Mapper mapper, Drawable drawable, int offset) {
        draw(canvas, mapper, drawable, offset, drawable.getBounds());
    }

    private boolean isList() {
        return !isTypeOneOf(Candidates.Source.TOOL_TIP, Candidates.Source.UDB_EDIT);
    }

    protected boolean isCandidatesList() {
        return isList() && !isTypeOneOf(Candidates.Source.COMPLETIONS, Candidates.Source.NEXT_WORD_PREDICTION);
    }

    protected boolean isTypeOneOf(Candidates.Source... types) {
        for (Candidates.Source type : types) {
            if (this.mSuggestions.source() == type) {
                return true;
            }
        }
        return false;
    }

    private int drawCandidate(Canvas canvas, Mapper mapper, int idxCandidate, int lastIndex, int x, int y, int height, int touchX) {
        Emoji emoji;
        Paint paint = this.mPaint;
        int scrollX = getScrollX();
        boolean scrolled = this.mScrolled;
        Rect bgPadding = this.mBgPadding;
        WordCandidate candidate = this.mSuggestions.get(idxCandidate);
        WordCandidate.Source source = candidate.source();
        if (this.mMouseDown && this.mSuggestions.hasAttribute(2)) {
            setColor(this.mPaint, this.mColorPressed);
            setTypeface(this.mPaint, this.typefaceBold);
        } else if (source != WordCandidate.Source.WORD_SOURCE_DRAWABLE) {
            setWordCandidateFont(this.mSuggestions, idxCandidate);
            setWordCandidateColor(this.mSuggestions, idxCandidate);
        }
        float textWidth = this.mWordWidthWithoutPadding.get(idxCandidate).intValue();
        int wordWidth = this.mWordWidth.get(idxCandidate).intValue();
        int xFlipped = mapper.map(x, wordWidth);
        boolean isPressedCand = touchX != -1 && touchX + scrollX >= xFlipped && touchX + scrollX < xFlipped + wordWidth && !scrolled;
        int divWidth = this.mDivider.getBounds().width();
        if (isList()) {
            if ((isPressedCand && !scrolled) || (this.highlightActiveCandidate && isActiveCandidate(candidate))) {
                setColor(this.mPaint, this.mColorPressed);
                setTypeface(this.mPaint, this.typefaceBold);
                if (source != WordCandidate.Source.WORD_SOURCE_DRAWABLE) {
                    if (this.mSuggestions.source() == Candidates.Source.TOOL_TIP) {
                        wordWidth = ((int) this.mPaint.measureText(candidate.toString())) + ((int) this.mPaint.measureText(XMLResultsHandler.SEP_SPACE, 0, 1));
                    } else {
                        wordWidth = getWordDisplaySize(candidate.toString());
                    }
                }
                draw(canvas, mapper, this.mSelectionHighlight, x + ((wordWidth - wordWidth) / 2), divWidth, bgPadding.top, wordWidth, bgPadding.top + height);
                this.mTouchIndex = idxCandidate;
            } else if (source != WordCandidate.Source.WORD_SOURCE_DRAWABLE && isCandidatesList()) {
                Rect bounds = new Rect(divWidth, bgPadding.top, wordWidth, bgPadding.top + height);
                drawCandBackground(canvas, mapper, candidate, idxCandidate, x, isPressedCand, bounds);
            }
        }
        if (this.mSuggestions.count() == 2 && getHighlightAddtoDictionaryTip()) {
            if (source == WordCandidate.Source.WORD_SOURCE_DRAWABLE) {
                draw(canvas, mapper, this.mSelectionHighlight, x, 0, bgPadding.top, wordWidth + this.mWordWidth.get(1).intValue(), bgPadding.top + height);
            } else if (source != WordCandidate.Source.WORD_SOURCE_DRAWABLE && this.mSuggestions.get(0).source() == WordCandidate.Source.WORD_SOURCE_DRAWABLE) {
                setColor(this.mPaint, this.mColorPressed);
                setTypeface(this.mPaint, this.typefaceBold);
                this.isHardwareKeyboardUsed = false;
                this.highlightAddtoDictionaryTip = false;
            }
        }
        if (source != WordCandidate.Source.WORD_SOURCE_DRAWABLE) {
            String emoji_unicode = candidate.toString();
            if (EmojiLoader.isEmoji(emoji_unicode) && (emoji = EmojiLoader.getEmoji(emoji_unicode)) != null) {
                String emojiCode = getDefaultSkinToneCode(emoji);
                if (emojiCode != null) {
                    candidate.setWord(emojiCode);
                }
                candidate.setLeft(x);
                candidate.setTop(y);
                candidate.setWidth((int) textWidth);
                candidate.setHeight(height);
                candidate.setEmojiUnicode(emoji_unicode);
            }
            drawCandidateText(canvas, mapper, paint, candidate, wordWidth, (int) textWidth, x, y, isPressedCand);
            if (isList() && idxCandidate < lastIndex) {
                draw(canvas, mapper, this.mDivider, x + wordWidth, null);
            }
        } else {
            Drawable icon = ((DrawableCandidate) candidate).icon;
            drawIcon(canvas, mapper, icon, x, idxCandidate, wordWidth - ((int) textWidth));
        }
        return wordWidth;
    }

    private int getIconLeftOffset(int idxCandidate, int padding) {
        if (idxCandidate != 0) {
            if (this.mSuggestions.count() - 1 == idxCandidate) {
                return 0;
            }
            return padding / 2;
        }
        return padding;
    }

    private void drawIcon(Canvas canvas, Mapper mapper, Drawable icon, int x, int idxCandidate, int wordPadding) {
        int availableHeight = (getHeight() - this.mBgPadding.top) - this.mBgPadding.bottom;
        int iconTop = (availableHeight - icon.getIntrinsicHeight()) / 2;
        double iconScale = 1.0d;
        if (iconTop < 0) {
            iconTop = this.mBgPadding.top;
            iconScale = availableHeight / icon.getIntrinsicWidth();
        }
        int left = getIconLeftOffset(idxCandidate, wordPadding);
        icon.setBounds(0, iconTop, (int) (icon.getIntrinsicWidth() * iconScale), (int) (iconTop + (icon.getIntrinsicHeight() * iconScale)));
        draw(canvas, mapper, icon, x + left);
    }

    private boolean getRTLDrawingSetting() {
        return (this.mSuggestions == null || !(this.mSuggestions.hasAttribute(1) || this.mSuggestions.hasAttribute(2) || this.mSuggestions.source() == Candidates.Source.TOOL_TIP)) ? this.isRtl : DirectionalUtil.isCurrentlyRtl();
    }

    private void computeDisplayingCandidateDrawingRegion() {
        int width;
        if (this.mSuggestions != null && !this.mSuggestions.getCandidates().isEmpty()) {
            while (this.mWordWidth.size() < this.mSuggestions.count()) {
                this.mWordWidth.add(0);
                this.mPadWidth.add(0);
                this.mWordX.add(0);
                this.mWordWidthWithoutPadding.add(0);
            }
            int x = 0;
            int padding = (int) this.mPaint.measureText(XMLResultsHandler.SEP_SPACE, 0, 1);
            int count = this.mSuggestions.count();
            for (int index = 0; index < count; index++) {
                WordCandidate candidate = this.mSuggestions.get(index);
                if (candidate.source() == WordCandidate.Source.WORD_SOURCE_DRAWABLE) {
                    Drawable icon = ((DrawableCandidate) candidate).icon;
                    icon.setBounds(0, 0, icon.getIntrinsicWidth(), getHeight());
                    width = icon.getIntrinsicWidth();
                    this.mWordWidthWithoutPadding.set(index, Integer.valueOf(width));
                    if (index == 0 || index == count - 1) {
                        width += this.mTextPadding;
                    }
                } else {
                    setWordCandidateFont(this.mSuggestions, index);
                    String word = candidate.toString();
                    this.mWordWidthWithoutPadding.set(index, Integer.valueOf((int) this.mPaint.measureText(word)));
                    if (this.mSuggestions.source() == Candidates.Source.TOOL_TIP) {
                        width = this.mWordWidthWithoutPadding.get(index).intValue() + padding;
                    } else {
                        width = getWordDisplaySize(word);
                    }
                }
                this.mWordWidth.set(index, Integer.valueOf(width));
                this.mWordX.set(index, Integer.valueOf(x));
                this.mTotalWidth += width;
                x += width;
            }
        }
    }

    private void setWordCandidateColor(Candidates candidates, int idx) {
        if (this.isHardwareKeyboardUsed && candidates.getSmartSelectionIndex() == idx) {
            setColor(this.mPaint, this.mColorRecommended);
            this.isHardwareKeyboardUsed = false;
        } else if (!isList()) {
            setColor(this.mPaint, this.mColorUDBTip);
        } else if (candidates.source() != Candidates.Source.NEXT_WORD_PREDICTION && candidates.getSmartSelectionIndex() == idx) {
            setColor(this.mPaint, this.mColorRecommended);
        } else {
            setColor(this.mPaint, this.mColorOther);
        }
    }

    private void setWordCandidateFont(Candidates candidates, int wordCandidateIndex) {
        if (candidates.source() != Candidates.Source.NEXT_WORD_PREDICTION && candidates.getSmartSelectionIndex() == wordCandidateIndex) {
            setTypeface(this.mPaint, this.typefaceBold);
        } else if (candidates.source() == Candidates.Source.TOOL_TIP || candidates.source() == Candidates.Source.UDB_EDIT) {
            setTypeface(this.mPaint, this.typefaceBold);
        } else {
            setTypeface(this.mPaint, this.typeface);
        }
    }

    private int getWordDisplaySize(String word) {
        float minWidth = this.mMinCandidateWidth + (this.mTextPadding * 2);
        return (int) Math.max(this.mPaint.measureText(word, 0, word.length()) + (this.mTextPadding * 2), minWidth);
    }

    protected void setVelocity(float v) {
        this.mV = 0.7f * bound(v, -30.0f, 30.0f);
    }

    protected void calcAccel() {
        float vUnitVect = 0.0f == this.mV ? 0.0f : this.mV / Math.abs(this.mV);
        float x = getScrollX();
        int maxScroll = getMaxScroll();
        int minScroll = getMinScroll();
        if (x >= minScroll && x <= maxScroll) {
            if (0.0f != this.mV) {
                this.mA = (-0.6f) * vUnitVect;
                return;
            } else {
                this.mA = 0.0f;
                return;
            }
        }
        float oobDist = x < ((float) minScroll) ? x : x - maxScroll;
        if (vUnitVect * oobDist > 0.0f) {
            this.mA = (-0.005f) * vUnitVect * oobDist * oobDist;
            this.mA = bound(this.mA, -10.0f, 10.0f);
            return;
        }
        this.mA = 0.0f;
        this.mV = 0.0f;
        if (oobDist < minScroll) {
            this.mTargetScrollX = minScroll;
        } else {
            this.mTargetScrollX = maxScroll;
        }
        invalidate();
    }

    protected float pull(float dist) {
        float oobDist;
        float x = getScrollX();
        int width = getWidth();
        int maxScroll = getMaxScroll();
        int minScroll = getMinScroll();
        float maxPullDist = width / 4.0f;
        if (x < minScroll) {
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
        int maxScroll = getMaxScroll();
        int minScroll = getMinScroll();
        float oldV = this.mV;
        scrollToX(getScrollX() + ((int) pull(this.mV * 1.0f)));
        this.mV += this.mA * 1.0f;
        if (Math.abs(this.mV) < 1.0f) {
            this.mV = 0.0f;
        }
        if ((this.mV * oldV <= 0.0f && getScrollX() >= minScroll && getScrollX() <= maxScroll) || ((oldX < minScroll && getScrollX() >= minScroll) || (oldX > maxScroll && getScrollX() <= maxScroll))) {
            this.mV = 0.0f;
            this.mA = 0.0f;
            if (oldX < minScroll && getScrollX() >= minScroll) {
                scrollToX(minScroll);
            } else if (oldX > maxScroll && getScrollX() <= maxScroll) {
                scrollToX(getScrollX() + maxScroll);
            }
        }
        this.mTargetScrollX = getScrollX();
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
        invalidate();
    }

    protected void updateScrollPosition(int targetX) {
        if (targetX != getScrollX()) {
            this.mTargetScrollX = targetX;
            invalidate();
            this.mScrolled = true;
        }
    }

    public final void clear() {
        this.mSuggestions = null;
        this.mTouchX = -1;
        this.mSelectedCandidate = null;
        this.mSelectedIndex = -1;
        this.mTotalWidth = 0;
        this.mTargetScrollX = 0;
        invalidate();
        scrollToX(0);
        this.mA = 0.0f;
        this.mV = 0.0f;
        raiseOnCandidatesUpdated();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void trySelect() {
        if (this.mSuggestions != null && this.mSuggestions.source() != Candidates.Source.TOOL_TIP) {
            boolean selectionHandled = false;
            if (this.mSelectedCandidate != null) {
                selectionHandled = raiseOnSelectCandidate(this.mSelectedCandidate);
            }
            if (!selectionHandled) {
                touchWord(this.mSelectedIndex, null);
            }
        }
    }

    private void handleReleasePress() {
        WordCandidate candidate = getCurrentSelectedCandidate();
        raiseOnPressReleaseCandidate(candidate);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleLongPress() {
        this.mLongPressHandled = true;
        if (this.mTouchIndex != -1 && this.mSuggestions != null && this.mSuggestions.source() != Candidates.Source.TOOL_TIP && this.mSuggestions.count() > this.mTouchIndex) {
            touchWord(this.mTouchIndex, this.mSuggestions.get(this.mTouchIndex));
            WordCandidate candidate = getCurrentSelectedCandidate();
            if (candidate != null) {
                raiseOnPressHoldCandidate(candidate);
            }
        }
    }

    private boolean raiseOnSelectCandidate(WordCandidate candidate) {
        if (this.mIme.get() == null) {
            return false;
        }
        InputConnection ic = this.mIme.get() == null ? null : this.mIme.get().getCurrentInputConnection();
        if (ic == null) {
            return false;
        }
        ic.beginBatchEdit();
        CandidateListener localListener = getCandidateListener();
        Candidates suggestions = this.mSuggestions;
        if (localListener != null && localListener.onSelectCandidate(candidate, suggestions)) {
            ic.endBatchEdit();
            return true;
        }
        Iterator<WeakReference<CandidateListener>> it = this.mCandidateListeners.iterator();
        while (it.hasNext()) {
            CandidateListener listener = it.next().get();
            if (listener != null && listener.onSelectCandidate(candidate, suggestions)) {
                ic.endBatchEdit();
                return true;
            }
        }
        ic.endBatchEdit();
        return false;
    }

    private boolean raiseOnPressHoldCandidate(WordCandidate candidate) {
        CandidateListener localListener = getCandidateListener();
        if (localListener != null && localListener.onPressHoldCandidate(candidate, this.mSuggestions)) {
            return true;
        }
        Iterator<WeakReference<CandidateListener>> it = this.mCandidateListeners.iterator();
        while (it.hasNext()) {
            CandidateListener listener = it.next().get();
            if (listener != null && listener.onPressHoldCandidate(candidate, this.mSuggestions)) {
                return true;
            }
        }
        return false;
    }

    private boolean raiseOnPressReleaseCandidate(WordCandidate candidate) {
        CandidateListener localListener = getCandidateListener();
        if (localListener != null && localListener.onPressReleaseCandidate(candidate, this.mSuggestions)) {
            return true;
        }
        Iterator<WeakReference<CandidateListener>> it = this.mCandidateListeners.iterator();
        while (it.hasNext()) {
            CandidateListener listener = it.next().get();
            if (listener != null && listener.onPressReleaseCandidate(candidate, this.mSuggestions)) {
                return true;
            }
        }
        return false;
    }

    private void raiseOnPressMoveCandidate(float xPos, float yPos, float xOffset) {
        CandidateListener localListener = getCandidateListener();
        if (localListener != null) {
            localListener.onPressMoveCandidate(xPos, yPos, xOffset);
        }
        Iterator<WeakReference<CandidateListener>> it = this.mCandidateListeners.iterator();
        while (it.hasNext()) {
            CandidateListener listener = it.next().get();
            if (listener != null) {
                listener.onPressMoveCandidate(xPos, yPos, xOffset);
            }
        }
    }

    private void raiseOnCandidatesUpdated() {
        CandidateListener localListener;
        if (this.mSuggestions != null && (localListener = getCandidateListener()) != null) {
            localListener.onCandidatesUpdated(this.mSuggestions);
        }
        if (this.mCandidateListeners != null) {
            Iterator<WeakReference<CandidateListener>> it = this.mCandidateListeners.iterator();
            while (it.hasNext()) {
                CandidateListener listener = it.next().get();
                if (listener != null) {
                    listener.onCandidatesUpdated(this.mSuggestions);
                }
            }
        }
    }

    public void setCandidateListener(CandidateListener listener) {
        this.listener = new WeakReference<>(listener);
    }

    public CandidateListener getCandidateListener() {
        if (this.listener != null) {
            return this.listener.get();
        }
        return null;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // android.view.View
    @SuppressLint({"ClickableViewAccessibility"})
    public boolean onTouchEvent(MotionEvent me) {
        if ((this.mIme.get() == null || !this.mIme.get().isAccessibilitySupportEnabled()) && !this.mGestureDetector.onTouchEvent(me)) {
            int action = me.getAction();
            int x = (int) me.getX();
            int y = (int) me.getY();
            this.mTouchX = x;
            switch (action) {
                case 0:
                    this.xDown = this.mTouchX;
                    this.yDown = y;
                    log.d("onMove(): called  action down >>>>>>>>>>1 :: xDown==" + this.xDown);
                    this.mLongPressHandled = false;
                    this.mMouseDown = true;
                    this.mDragSelected = false;
                    this.mSwipeSpeed.flush();
                    this.mA = 0.0f;
                    this.mV = 0.0f;
                    this.mScrolled = false;
                    this.mTouchIndex = -1;
                    invalidate();
                    this.mHandler.removeMessages(1);
                    this.mHandler.sendEmptyMessageDelayed(1, 750L);
                    break;
                case 1:
                    this.mHandler.removeMessages(1);
                    this.mMouseDown = false;
                    this.mDragSelected = false;
                    if (!this.mSwipeSpeed.isFlushed()) {
                        setVelocity(this.mSwipeSpeed.get());
                    }
                    if (!this.mLongPressHandled && !this.mScrolled && this.mTouchIndex != -1 && this.mSuggestions != null && this.mSuggestions.source() != Candidates.Source.TOOL_TIP && this.mSuggestions.count() > 0 && this.mTouchIndex < this.mSuggestions.count()) {
                        if (!this.mSuggestions.hasAttribute(2)) {
                            touchWord(this.mTouchIndex, this.mSuggestions.get(this.mTouchIndex));
                        }
                        trySelect();
                    }
                    removeHighlight();
                    log.d("onTouch()", "called >>>>>: mLongPressHandled " + this.mLongPressHandled);
                    if (this.mLongPressHandled) {
                        handleReleasePress();
                    }
                    this.mLongPressHandled = false;
                    break;
                case 2:
                    if (y <= 0 && !this.mDragSelected) {
                        this.mDragSelected = true;
                    }
                    onMove(me);
                    break;
            }
        }
        return true;
    }

    protected void removeHighlight() {
        this.mTouchX = -1;
        invalidate();
    }

    protected void longPressFirstWord() {
    }

    @Override // android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    protected void scrollToX(int newScrollX) {
        scrollTo(newScrollX, getScrollY());
    }

    private void readStyles(Context context) {
        TypedArrayWrapper a = ThemeLoader.getInstance().obtainStyledAttributes$6d3b0587(context, null, R.styleable.WordListView, 0, R.style.WordListView, R.xml.defaults, "WordListView");
        int n = a.delegateTypedArray.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.WordListView_candidateNormal) {
                this.mColorNormal = a.getColor(attr, -65536);
            } else if (attr == R.styleable.WordListView_candidateRecommended) {
                this.mColorRecommended = a.getColor(attr, -65536);
            } else if (attr == R.styleable.WordListView_candidateOther) {
                this.mColorOther = a.getColor(attr, -65536);
            } else if (attr == R.styleable.WordListView_candidatePressed) {
                this.mColorPressed = a.getColor(attr, -65536);
            } else if (attr == R.styleable.WordListView_textSize) {
                this.mTextSizeInit = a.getDimensionPixelSize(attr, 20);
            } else if (attr == R.styleable.WordListView_textPadding) {
                this.mTextPadding = a.getDimensionPixelSize(attr, 10);
            } else if (attr == R.styleable.WordListView_udbAndTipColor) {
                if (a.hasValue(attr)) {
                    this.mColorUDBTip = a.getColor(attr, -65536);
                } else {
                    this.mColorUDBTip = a.getColor(R.styleable.WordListView_candidateRecommended, -65536);
                }
            } else if (attr == R.styleable.WordListView_android_gravity) {
                if (a.hasValue(attr)) {
                    this.mGravity = a.getInt(R.styleable.WordListView_android_gravity, this.mGravity);
                } else {
                    this.mGravity = 17;
                }
            }
        }
        a.recycle();
    }

    public int wordCount() {
        if (this.mSuggestions != null) {
            return this.mSuggestions.count();
        }
        return 0;
    }

    public int getSelectedIndex() {
        return this.mSelectedIndex;
    }

    public WordCandidate getCurrentSelectedCandidate() {
        return this.mSelectedCandidate;
    }

    public Candidates setSuggestions(Candidates candidates, Format format) {
        clear();
        if (candidates != null && candidates.count() > 0) {
            int[] iArr = AnonymousClass5.$SwitchMap$com$nuance$swype$input$CandidatesListView$Format;
            format.ordinal();
            setDefaultSuggestions(candidates);
            this.mSelectedIndex = this.mSuggestions.getDefaultCandidateIndex();
            computeDisplayingCandidateDrawingRegion();
            touchWord(this.mSelectedIndex, this.mSuggestions.get(this.mSelectedIndex));
            scrollToX(this.mTargetScrollX);
            requestLayout();
            invalidate();
        }
        log.d("Suggestions:", getWordListAsString());
        return this.mSuggestions;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.nuance.swype.input.CandidatesListView$5, reason: invalid class name */
    /* loaded from: classes.dex */
    public static /* synthetic */ class AnonymousClass5 {
        static final /* synthetic */ int[] $SwitchMap$com$nuance$swype$input$CandidatesListView$Format = new int[Format.values().length];

        static {
            try {
                $SwitchMap$com$nuance$swype$input$CandidatesListView$Format[Format.FIT_AS_MUCH.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$nuance$swype$input$CandidatesListView$Format[Format.DEFAULT.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    private void setDefaultSuggestions(Candidates candidates) {
        this.mSuggestions = new Candidates(candidates);
        raiseOnCandidatesUpdated();
    }

    public boolean isEditingUDBWords() {
        return this.mSuggestions != null && this.mSuggestions.isUDBEditing();
    }

    @Override // android.view.View
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w != oldw) {
            resetScroll();
        }
    }

    private void resetScroll() {
        scrollToX(0);
        this.mTargetScrollX = 0;
        invalidate();
    }

    private int getScrollRange() {
        if (this.mSuggestions == null) {
            return 0;
        }
        int range = 0;
        if (!this.allowFullScroll || this.mSuggestions.hasAttribute(1)) {
            if (this.mSuggestions.hasAttribute(1)) {
                range = (this.mTotalWidth - getWidth()) / 2;
            } else {
                range = this.mTotalWidth - getWidth();
            }
        } else if (this.mWordWidth.size() > 0) {
            range = this.mTotalWidth - this.mWordWidth.get(this.mWordWidth.size() - 1).intValue();
        }
        return Math.max(0, range);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getMaxScroll() {
        if (getRTLDrawingSetting()) {
            return 0;
        }
        return getScrollRange();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getMinScroll() {
        if (getRTLDrawingSetting()) {
            return -getScrollRange();
        }
        return 0;
    }

    public boolean selectCandidateTriggedByExternalSource(String candidate) {
        this.highlightActiveCandidate = false;
        for (WordCandidate wordCandidate : this.mSuggestions.getCandidates()) {
            if (candidate.equals(wordCandidate.toString())) {
                raiseOnSelectCandidate(wordCandidate);
                return true;
            }
        }
        return false;
    }

    private boolean isActiveCandidate(WordCandidate candidate) {
        return candidate.toString().equals(this.activeCandidate);
    }

    public void highlightCandidate(String candidate) {
        this.highlightActiveCandidate = true;
        this.activeCandidate = candidate;
        scrollToHighlight();
    }

    private void scrollToHighlight() {
        this.mSelectedIndex = getActiveCandidateIndex();
        if (this.mSelectedIndex >= 0 && this.mSelectedIndex <= this.mSuggestions.count() - 1) {
            this.mSelectedCandidate = this.mSuggestions.get(this.mSelectedIndex);
            int maxScroll = Math.max(0, this.mTotalWidth - getWidth());
            if (this.mSelectedIndex == 0 && getScrollX() != 0) {
                this.mTargetScrollX = 0;
                this.mScrolled = true;
            } else if (this.mSelectedIndex == this.mSuggestions.count() - 1 && getScrollX() != maxScroll) {
                this.mTargetScrollX = maxScroll;
                this.mScrolled = true;
            } else if (this.mWordX.get(this.mSelectedIndex).intValue() < getScrollX()) {
                this.mTargetScrollX = this.mWordX.get(this.mSelectedIndex).intValue();
                this.mScrolled = true;
            } else {
                if (this.mWordWidth.get(this.mSelectedIndex).intValue() + this.mWordX.get(this.mSelectedIndex).intValue() > getScrollX() + getWidth()) {
                    this.mTargetScrollX = (this.mWordWidth.get(this.mSelectedIndex).intValue() + this.mWordX.get(this.mSelectedIndex).intValue()) - getWidth();
                    this.mScrolled = true;
                }
            }
            invalidate();
        }
    }

    private int getActiveCandidateIndex() {
        if (this.activeCandidate != null) {
            for (WordCandidate wordCandidate : this.mSuggestions.getCandidates()) {
                if (this.activeCandidate.equals(wordCandidate.toString())) {
                    return wordCandidate.id();
                }
            }
        }
        return -1;
    }

    public void hideCandidateHighlight() {
        this.highlightActiveCandidate = false;
        invalidate();
    }

    public void setCandidatesDisplay(boolean value) {
        this.mDisableCandidateDisplay = !value;
    }

    public void setHorizontalLayout(int x, int width) {
        if (width > 0) {
            ViewGroup.LayoutParams lp = getLayoutParams();
            if (lp instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams marginLp = (ViewGroup.MarginLayoutParams) lp;
                marginLp.width = width;
                marginLp.setMargins(x, marginLp.topMargin, marginLp.rightMargin, marginLp.bottomMargin);
            }
        }
    }

    public void setCandidateSize(float newTempTextSize) {
        this.mTextSize = (int) (this.mTextSizeInit * newTempTextSize);
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

    public boolean isKeyOutofRightBound(int i) {
        int rightEdge = getScrollX() + getWidth();
        if (this.mWordX.get(i).intValue() >= rightEdge) {
            return true;
        }
        if (this.mWordX.get(i).intValue() <= rightEdge) {
            if (this.mWordWidth.get(i).intValue() + this.mWordX.get(i).intValue() >= rightEdge) {
                return true;
            }
        }
        return false;
    }

    public boolean isKeyOutofLeftBound(int i) {
        if (this.mWordX.get(i).intValue() < getScrollX()) {
            return true;
        }
        if (this.mWordX.get(i).intValue() < getScrollX()) {
            if (this.mWordWidth.get(i).intValue() + this.mWordX.get(i).intValue() >= getScrollX() - 1) {
                return true;
            }
        }
        return false;
    }

    public int getFirstCandidateIndexInNextPage() {
        int i = 0;
        int firstItem = -1;
        int count = this.mSuggestions.count();
        if (count <= 0) {
            return -1;
        }
        int rightEdge = getScrollX() + getWidth();
        while (true) {
            if (i >= count) {
                break;
            }
            if (this.mWordX.get(i).intValue() <= rightEdge) {
                if (this.mWordWidth.get(i).intValue() + this.mWordX.get(i).intValue() >= rightEdge + 1) {
                    firstItem = i;
                    break;
                }
            }
            i++;
        }
        return firstItem;
    }

    public int getCenterCandidateIndex() {
        int i = 0;
        int item = -1;
        int count = this.mSuggestions.count();
        if (count <= 0) {
            return -1;
        }
        int rightEdge = getScrollX() + getWidth();
        while (true) {
            if (i >= count) {
                break;
            }
            if (this.mWordX.get(i).intValue() <= rightEdge) {
                if (this.mWordWidth.get(i).intValue() + this.mWordX.get(i).intValue() >= rightEdge + 1) {
                    item = i;
                    break;
                }
            }
            i++;
        }
        if (item == -1) {
            item = count;
        }
        return item / 2;
    }

    public int getFirstCandidateIndexInPreviousPage() {
        int i = 0;
        int firstItem = -1;
        int count = this.mSuggestions.count();
        if (count <= 0) {
            return -1;
        }
        while (true) {
            if (i >= count) {
                break;
            }
            if (this.mWordX.get(i).intValue() < getScrollX()) {
                if (this.mWordWidth.get(i).intValue() + this.mWordX.get(i).intValue() >= getScrollX() - 1) {
                    firstItem = i;
                    break;
                }
            }
            i++;
        }
        return firstItem;
    }

    public void scrollHead() {
        int leftEdge = (this.mWordWidth.get(0).intValue() + this.mWordX.get(0).intValue()) - getWidth();
        if (leftEdge < 0) {
            leftEdge = 0;
        }
        if (leftEdge >= getScrollX()) {
            leftEdge = Math.max(0, getScrollX() - getWidth());
        }
        updateScrollPosition(leftEdge);
    }

    public void scrollEnd() {
        updateScrollPosition(this.mTotalWidth);
    }

    public void scrollPrev() {
        int i = 0;
        int count = this.mSuggestions.count();
        int firstItem = 0;
        while (true) {
            if (i >= count) {
                break;
            }
            if (this.mWordX.get(i).intValue() < getScrollX()) {
                if (this.mWordWidth.get(i).intValue() + this.mWordX.get(i).intValue() >= getScrollX() - 1) {
                    firstItem = i;
                    break;
                }
            }
            i++;
        }
        int leftEdge = (this.mWordWidth.get(firstItem).intValue() + this.mWordX.get(firstItem).intValue()) - getWidth();
        if (leftEdge < 0) {
            leftEdge = 0;
        }
        if (leftEdge >= getScrollX()) {
            leftEdge = Math.max(0, getScrollX() - getWidth());
        }
        updateScrollPosition(leftEdge);
    }

    public void scrollNext() {
        int i = 0;
        int targetX = getScrollX();
        int count = this.mSuggestions.count();
        int rightEdge = getScrollX() + getWidth();
        while (true) {
            if (i >= count) {
                break;
            }
            if (this.mWordX.get(i).intValue() <= rightEdge) {
                if (this.mWordWidth.get(i).intValue() + this.mWordX.get(i).intValue() >= rightEdge) {
                    targetX = Math.min(this.mWordX.get(i).intValue(), this.mTotalWidth - getWidth());
                    break;
                }
            }
            i++;
        }
        if (targetX <= getScrollX()) {
            targetX = rightEdge;
        }
        int iParentWidth = IMEApplication.from(getContext()).getDisplay().widthPixels;
        if (getParent() instanceof View) {
            iParentWidth = ((View) getParent()).getWidth();
        }
        int rightArrowSize = iParentWidth - getRight();
        if (getLeft() < rightArrowSize) {
            targetX -= rightArrowSize;
        }
        updateScrollPosition(targetX);
    }

    public void setHardwareKeyboardUsed(boolean used) {
        this.isHardwareKeyboardUsed = used;
    }

    public boolean getHardwareKeyboardUsed() {
        return this.isHardwareKeyboardUsed;
    }

    public void setHighlightAddtoDictionaryTip(boolean highlight) {
        this.highlightAddtoDictionaryTip = highlight;
        invalidate();
    }

    public boolean getHighlightAddtoDictionaryTip() {
        return this.highlightAddtoDictionaryTip;
    }

    public void updateRtlStatus(String newLangStr) {
        this.isRtl = DirectionalUtil.isLanguageRtl(newLangStr);
    }

    @Override // android.view.View
    protected float getLeftFadingEdgeStrength() {
        if (this.isRtl) {
            return (this.mSuggestions == null || this.mSuggestions.count() <= 0 || !isKeyOutofRightBound(this.mSuggestions.count() + (-1))) ? 0.0f : 1.0f;
        }
        return super.getLeftFadingEdgeStrength();
    }

    public String getWordListAsString() {
        ArrayList<String> hwclContent = new ArrayList<>();
        if (this.mSuggestions != null) {
            if (this.mSuggestions.getDefaultCandidate() != null) {
                String defaultWord = this.mSuggestions.getDefaultCandidate().toString();
                hwclContent.add(0, defaultWord);
            } else {
                hwclContent.add(0, "");
            }
            int offset = 0;
            if (this.mTotalWidth < getWidth()) {
                offset = (getWidth() - this.mTotalWidth) / 2;
            }
            int numberOfWords = this.mSuggestions.getCandidates().size();
            for (int i = 0; i < numberOfWords; i++) {
                WordCandidate candidateWord = this.mSuggestions.get(i);
                int xPos = (this.mWordWidth.get(i).intValue() / 2) + this.mWordX.get(i).intValue() + offset;
                hwclContent.add(candidateWord.toString());
                hwclContent.add(Integer.toString(xPos));
            }
        }
        StringBuilder retValue = new StringBuilder();
        Iterator<String> it = hwclContent.iterator();
        while (it.hasNext()) {
            String word = it.next();
            if (retValue.length() > 0) {
                retValue.append(",");
            }
            retValue.append(word);
        }
        return retValue.toString();
    }

    private String getDefaultSkinToneCode(Emoji emoji) {
        return EmojiLoader.getDefaultSkinToneCode(getContext(), emoji, this.emojiCacheManager).getEmojiDisplayCode();
    }

    private Emoji getEmoji(String emoji_unicode) {
        return EmojiLoader.getEmoji(emoji_unicode);
    }

    public void setHorizontalScroll(boolean mScroll) {
        this.mScroll = mScroll;
    }

    public int getTargetScrollX() {
        return this.mTargetScrollX;
    }

    public float getOffsetX(float xPos) {
        return xPos - this.xDown;
    }

    private void onMove(MotionEvent ev) {
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
}
