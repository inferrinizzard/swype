package com.nuance.swype.input.chinese;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.R;
import com.nuance.swype.input.chinese.CJKCandidatesListView;
import java.util.Arrays;

/* loaded from: classes.dex */
public class VerCandidatesListView extends CJKCandidatesListView {
    protected int CANDIDATES_NUM_PER_COLUMN;
    private boolean isSpellPrefix;
    protected VerCandidatesListContainer mContainer;
    private boolean mHighlightPrefix;
    private int mKeyboradHeight;
    protected int[] mWordHeight;
    protected int[] mWordY;
    private Rect rectBounds;

    public VerCandidatesListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.CANDIDATES_NUM_PER_COLUMN = 4;
        this.mWordHeight = new int[MAX_SUGGESTIONS];
        this.mWordY = new int[MAX_SUGGESTIONS];
        this.mHighlightPrefix = true;
        this.rectBounds = new Rect();
    }

    @Override // com.nuance.swype.input.chinese.CJKCandidatesListView
    public void init() {
        IMEApplication app = IMEApplication.from(this.mContext);
        this.mDivider = app.getThemedDrawable(R.attr.keyboardSuggestVerStripDivider);
        this.mSelectionHighlight = app.getThemedDrawable(R.attr.listSelectorBackgroundPressed);
        this.CANDIDATES_NUM_PER_COLUMN = 4;
        this.mEnableHighlight = true;
        this.mPaint = new Paint();
        this.mPaint.setColor(this.mColorNormal);
        this.mPaint.setAntiAlias(true);
        this.mPaint.setTextSize(this.mTextSizeInit);
        this.mPaint.setStrokeWidth(0.0f);
        this.mDescent = (int) this.mPaint.descent();
        this.mGetMoreWordsHandler = null;
        this.mGestureDetector = new GestureDetector(this.mContext, new GestureDetector.SimpleOnGestureListener() { // from class: com.nuance.swype.input.chinese.VerCandidatesListView.1
            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                VerCandidatesListView.this.mScrolled = true;
                VerCandidatesListView.this.mA = 0.0f;
                VerCandidatesListView.this.mV = 0.0f;
                VerCandidatesListView.this.scrollToY(VerCandidatesListView.this.getScrollY() + ((int) VerCandidatesListView.this.pull(distanceY)));
                VerCandidatesListView.this.mTargetScrollPos = VerCandidatesListView.this.getScrollY();
                VerCandidatesListView.this.mSwipeSpeed.add(distanceY);
                if (VerCandidatesListView.this.needMoreWords()) {
                    VerCandidatesListView.this.mGetMoreWordsHandler.requestMoreWords();
                }
                VerCandidatesListView.this.invalidate();
                return true;
            }
        });
        this.mGestureDetector.setIsLongpressEnabled(false);
        setHorizontalFadingEdgeEnabled(false);
        setVerticalFadingEdgeEnabled(true);
        setWillNotDraw(false);
        setHorizontalScrollBarEnabled(false);
        setVerticalScrollBarEnabled(false);
        clear();
    }

    public void setOnWordSelectActionVerListener(CJKCandidatesListView.OnWordSelectActionListener listener) {
        this.mOnWordSelectActionListener = listener;
    }

    public void setContainer(VerCandidatesListContainer container) {
        this.mContainer = container;
    }

    @Override // android.view.View
    public int computeVerticalScrollRange() {
        return this.mTotalLength;
    }

    public void setSpellPrefix(boolean prefix) {
        this.isSpellPrefix = prefix;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.chinese.CJKCandidatesListView, android.view.View
    public void onDraw(Canvas canvas) {
        int wordHeight;
        this.mTotalLength = 0;
        Paint paint = this.mPaint;
        if (this.isSpellPrefix) {
            this.mPaint.setTextSize((this.mTextSizeInit * 75) / 100.0f);
        } else {
            this.mPaint.setTextSize(this.mTextSizeInit);
        }
        if (this.mSuggestions != null && this.mContainer != null) {
            int height = this.mKeyboradHeight;
            if (height == 0) {
                height = this.mContainer.getHeight();
            }
            int width = getWidth();
            if (height != 0 && width != 0) {
                int size = this.mSuggestions.size();
                this.mDivider.setBounds(0, 0, getWidth(), this.mDivider.getIntrinsicHeight());
                if (size != 0) {
                    int offset = height / this.CANDIDATES_NUM_PER_COLUMN;
                    int textH = ((int) paint.ascent()) + ((int) paint.descent());
                    int y = getTop() + offset + textH;
                    int count = this.mSuggestions.size();
                    int touchY = this.mTouchY;
                    int scrollY = getScrollY();
                    boolean scrolled = this.mScrolled;
                    for (int i = 0; i < count; i++) {
                        CharSequence suggestion = this.mSuggestions.get(i);
                        if (suggestion == null || suggestion.length() != 0) {
                            paint.setUnderlineText(false);
                            if (suggestion != null && suggestion.charAt(0) == 40959) {
                                if (suggestion.charAt(1) != '~') {
                                    paint.setUnderlineText(true);
                                    paint.setColor(this.mColorComponent);
                                } else {
                                    continue;
                                }
                            } else if (this.mSelectedIndex == i && this.mEnableHighlight && this.mHighlightPrefix) {
                                setTypeFace(paint);
                                paint.setColor(this.mColorRecommended);
                            } else if (i == 0 && this.mEnableHighlight) {
                                paint.setColor(this.mColorNormal);
                            } else {
                                paint.setColor(this.mColorOther);
                            }
                            if (suggestion == null) {
                                break;
                            }
                            if (this.mWordHeight[i] != 0) {
                                wordHeight = this.mWordHeight[i];
                            } else {
                                wordHeight = offset;
                                this.mWordHeight[i] = wordHeight;
                            }
                            this.mWordY[i] = (wordHeight / 3) + y;
                            if (touchY + scrollY >= y - ((wordHeight * 2) / 3) && touchY + scrollY < this.mWordY[i] && !scrolled && this.mTouchY != -1) {
                                if (canvas != null) {
                                    canvas.translate(0.0f, y);
                                    this.mSelectionHighlight.setBounds(0, (wordHeight * 3) / (-5), width, wordHeight / 3);
                                    this.mSelectionHighlight.draw(canvas);
                                    canvas.translate(0.0f, -y);
                                    this.mPaint.setColor(this.mColorPressed);
                                    setTypeFace(paint);
                                }
                                this.mTouchIndex = i;
                            }
                            if (canvas != null) {
                                if (suggestion instanceof SpannableStringBuilder) {
                                    for (ForegroundColorSpan span : (ForegroundColorSpan[]) ((SpannableStringBuilder) suggestion).getSpans(0, suggestion.length(), ForegroundColorSpan.class)) {
                                        paint.setColor(span.getForegroundColor());
                                    }
                                }
                                paint.getTextBounds(suggestion.toString(), 0, suggestion.length(), this.rectBounds);
                                float x = ((width - this.rectBounds.width()) / 2.0f) - this.rectBounds.left;
                                canvas.drawText(suggestion, 0, suggestion.length(), x, y, paint);
                                y += wordHeight;
                                canvas.translate(0.0f, this.mWordY[i]);
                                this.mDivider.draw(canvas);
                                canvas.translate(0.0f, -this.mWordY[i]);
                                paint.setColor(this.mColorOther);
                            }
                            setTypeFace(paint);
                        }
                    }
                    this.mTotalLength = getTop() + y;
                    if (this.mTargetScrollPos != getScrollY()) {
                        scrollToTarget();
                    }
                    if (!this.mMouseDown) {
                        calcAccel();
                        if (0.0f != this.mA || 0.0f != this.mV) {
                            slide();
                        }
                    }
                    this.mContainer.requestLayout();
                }
            }
        }
    }

    @Override // com.nuance.swype.input.chinese.CJKCandidatesListView
    protected void calcAccel() {
        float vUnitVect = 0.0f == this.mV ? 0.0f : this.mV / Math.abs(this.mV);
        float y = getScrollY();
        int maxScroll = Math.max(0, this.mTotalLength - getHeight());
        if (y >= 0.0f && y <= maxScroll) {
            if (0.0f != this.mV) {
                this.mA = (-0.6f) * vUnitVect;
                return;
            } else {
                this.mA = 0.0f;
                return;
            }
        }
        float oobDist = y < 0.0f ? y : y - maxScroll;
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

    @Override // com.nuance.swype.input.chinese.CJKCandidatesListView
    protected float pull(float dist) {
        float oobDist;
        float y = getScrollY();
        int height = getHeight();
        int maxScroll = Math.max(0, this.mTotalLength - height);
        float maxPullDist = height / 4.0f;
        if (y < 0.0f) {
            oobDist = y;
        } else if (y > maxScroll) {
            oobDist = y - maxScroll;
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
        float oldY = getScrollY();
        int maxScroll = Math.max(0, this.mTotalLength - getHeight());
        float oldV = this.mV;
        scrollToY(getScrollY() + ((int) pull(this.mV * 1.0f)));
        this.mV += this.mA * 1.0f;
        if (Math.abs(this.mV) < 1.0f) {
            this.mV = 0.0f;
        }
        if ((this.mV * oldV <= 0.0f && getScrollY() >= 0 && getScrollY() <= maxScroll) || ((oldY < 0.0f && getScrollY() >= 0) || (oldY > maxScroll && getScrollY() <= maxScroll))) {
            this.mV = 0.0f;
            this.mA = 0.0f;
            if (oldY < 0.0f && getScrollY() >= 0) {
                scrollToY(0);
            } else if (oldY > maxScroll && getScrollY() <= maxScroll) {
                scrollToY(getScrollY() + maxScroll);
            }
        }
        this.mTargetScrollPos = getScrollY();
        requestLayout();
        if (needMoreWords()) {
            this.mGetMoreWordsHandler.requestMoreWords();
        }
        invalidate();
    }

    private void scrollToTarget() {
        if (this.mTargetScrollPos > getScrollY()) {
            double dist = this.mTargetScrollPos - getScrollY();
            scrollToY(getScrollY() + ((int) ((Math.pow(Math.min(dist, 100.0d) / 100.0d, 1.5d) * 12.0d) + 1.0d)));
            if (getScrollY() >= this.mTargetScrollPos) {
                scrollToY(this.mTargetScrollPos);
                requestLayout();
            }
        } else {
            double dist2 = getScrollY() - this.mTargetScrollPos;
            scrollToY(getScrollY() - ((int) ((Math.pow(Math.min(dist2, 100.0d) / 100.0d, 1.5d) * 12.0d) + 1.0d)));
            if (getScrollY() <= this.mTargetScrollPos) {
                scrollToY(this.mTargetScrollPos);
                requestLayout();
            }
        }
        invalidate();
    }

    @Override // com.nuance.swype.input.chinese.CJKCandidatesListView
    public void scrollPrev() {
        if (!this.mOnWordSelectActionListener.isScrollable(this)) {
            this.mOnWordSelectActionListener.prevBtnPressed(this);
            return;
        }
        int i = 0;
        int count = this.mSuggestions.size();
        int firstItem = 0;
        while (true) {
            if (i < count) {
                if (this.mWordY[i] < getScrollY() && this.mWordY[i] + this.mWordHeight[i] >= getScrollY() - 1) {
                    firstItem = i;
                    break;
                }
                i++;
            } else {
                break;
            }
        }
        int leftEdge = (this.mWordY[firstItem] + this.mWordHeight[firstItem]) - getHeight();
        if (leftEdge < 0) {
            leftEdge = 0;
        }
        if (leftEdge >= getScrollY()) {
            leftEdge = Math.max(0, getScrollY() - getHeight());
        }
        updateScrollPosition(leftEdge);
    }

    @Override // com.nuance.swype.input.chinese.CJKCandidatesListView
    public void scrollNext() {
        if (!this.mOnWordSelectActionListener.isScrollable(this)) {
            this.mOnWordSelectActionListener.nextBtnPressed(this);
            return;
        }
        int i = 0;
        int targetY = getScrollY();
        int count = this.mSuggestions.size();
        int rightEdge = getScrollY() + getHeight();
        while (true) {
            if (i < count) {
                if (this.mWordY[i] <= rightEdge && this.mWordY[i] + this.mWordHeight[i] >= rightEdge) {
                    targetY = Math.min(this.mWordY[i], this.mTotalLength - getHeight());
                    break;
                }
                i++;
            } else {
                break;
            }
        }
        if (targetY <= getScrollY()) {
            targetY = rightEdge;
        }
        updateScrollPosition(targetY);
    }

    @Override // com.nuance.swype.input.chinese.CJKCandidatesListView
    public void updateScrollPosition(int targetY) {
        if (targetY != getScrollY()) {
            this.mTargetScrollPos = targetY;
            if (needMoreWords()) {
                this.mGetMoreWordsHandler.requestMoreWords();
            }
            invalidate();
            this.mScrolled = true;
        }
    }

    @Override // com.nuance.swype.input.chinese.CJKCandidatesListView
    public void clear() {
        super.clear();
        Arrays.fill(this.mWordHeight, 0);
        Arrays.fill(this.mWordY, 0);
        scrollToY(0);
    }

    @Override // com.nuance.swype.input.chinese.CJKCandidatesListView
    public void selectActiveWord() {
        if (this.mSelectedString == null && this.mSelectedIndex >= 0) {
            this.mOnWordSelectActionListener.selectWord(this.mSelectedIndex, this.mSuggestions.get(this.mSelectedIndex), this);
        } else {
            trySelect();
        }
    }

    @Override // com.nuance.swype.input.chinese.CJKCandidatesListView
    protected void trySelect() {
        if (this.mSelectedString != null) {
            this.mOnWordSelectActionListener.selectWord(this.mSelectedIndex, this.mSelectedString, this);
        }
        touchWord(this.mSelectedIndex, this.mSelectedString);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // com.nuance.swype.input.chinese.CJKCandidatesListView, android.view.View
    @SuppressLint({"ClickableViewAccessibility"})
    public boolean onTouchEvent(MotionEvent me) {
        if (!this.mGestureDetector.onTouchEvent(me)) {
            int action = me.getAction();
            int x = (int) me.getX();
            this.mTouchY = (int) me.getY();
            this.mTouchX = x;
            switch (action) {
                case 0:
                    this.mMouseDown = true;
                    this.mDragSelected = false;
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
                    if (!this.mScrolled && this.mTouchIndex != -1 && this.mSuggestions.size() > 0 && this.mTouchIndex < this.mSuggestions.size()) {
                        touchWord(this.mTouchIndex, this.mSuggestions.get(this.mTouchIndex));
                        trySelect();
                    }
                    removeHighlight();
                    break;
                case 2:
                    if (x <= 0 && !this.mDragSelected) {
                        this.mDragSelected = true;
                        break;
                    }
                    break;
            }
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean needMoreWords() {
        return this.mGetMoreWordsHandler != null && this.mTargetScrollPos + (getHeight() * 2) >= this.mTotalLength;
    }

    protected void scrollToY(int newScrollY) {
        scrollTo(getScrollX(), newScrollY);
    }

    @Override // com.nuance.swype.input.chinese.CJKCandidatesListView
    public void updateCandidatesSize() {
        if (this.mPaint != null) {
            getCandidateSize();
            this.mPaint.setTextSize(this.mTextSizeInit);
        }
    }

    public void setKeyboardHeight(int height) {
        this.mKeyboradHeight = height;
    }

    public void enablePrefixHighlight() {
        this.mHighlightPrefix = true;
    }

    public void disablePrefixHighlight() {
        this.mHighlightPrefix = false;
    }

    private void setTypeFace(Paint paint) {
        if (IMEApplication.from(getContext()).getUserPreferences().getKeyboardFontBold()) {
            paint.setTypeface(Typeface.DEFAULT_BOLD);
        } else {
            paint.setTypeface(Typeface.DEFAULT);
        }
    }
}
