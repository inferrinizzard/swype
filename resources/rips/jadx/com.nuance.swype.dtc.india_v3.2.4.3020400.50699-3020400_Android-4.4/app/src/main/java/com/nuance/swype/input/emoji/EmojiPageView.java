package com.nuance.swype.input.emoji;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.R;
import com.nuance.swype.input.UserPreferences;
import com.nuance.swype.input.emoji.Emoji;
import com.nuance.swype.input.emoji.EmojiSkinAdapter;
import com.nuance.swype.input.emoji.finger.FingerInfo;
import com.nuance.swype.input.emoji.finger.FingerState;
import com.nuance.swype.input.emoji.finger.FingerStateListener;
import com.nuance.swype.input.emoji.finger.FingerStateManager;
import com.nuance.swype.input.emoji.finger.Fingerable;
import com.nuance.swype.input.emoji.util.PopupManager;
import com.nuance.swype.util.DrawDebug;
import com.nuance.swype.util.LogManager;
import com.nuance.swype.util.ViewUtil;
import com.nuance.swype.view.OverlayView;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public class EmojiPageView extends ViewGroup implements EmojiSkinAdapter.OnItemClickListener {
    protected static final LogManager.Log log = LogManager.getLog("EmojiPageView");
    private IMEApplication app;
    private EmojiCategory cat;
    private EmojiGridCell cell;
    private List<EmojiGridCell> cells;
    private DrawDebug drawDebug;
    private EmojiCacheManager<String, Integer> emojiCacheManager;
    private List<Emoji> emojis;
    private FingerStateManager fingerStateManager;
    private Rect invalid;
    private boolean isEmojiPressed;
    private ColorStateList keyColor;
    private Listener listener;
    Drawable mSelectionHighlight;
    private EmojiViewPager pager;
    private EmojiGridParams params;
    private final int[] points;
    private PopupEmojiSkinList popupEmojiSkinList;
    private PopupManager popupManager;
    private List<Emoji> skinToneList;
    private UserPreferences userPrefs;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public enum EmojiFeedback {
        FEEDBACK_PRESS,
        FEEDBACK_POPUP
    }

    /* loaded from: classes.dex */
    public interface Listener {
        void onSelect(Emoji emoji);

        void playFeedback(String str, EmojiFeedback emojiFeedback);
    }

    public DrawDebug getDrawDebug() {
        return this.drawDebug;
    }

    /* loaded from: classes.dex */
    private class KeyMapper implements Fingerable.FingerableMapper {
        public KeyMapper() {
        }

        @Override // com.nuance.swype.input.emoji.finger.Fingerable.FingerableMapper
        public Fingerable map(float x, float y) {
            return EmojiPageView.this.getCell(x, y);
        }
    }

    public EmojiPageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.points = new int[4];
        this.invalid = new Rect();
        this.keyColor = IMEApplication.from(context).getThemedColorStateList(R.attr.emojiColor);
        setWillNotDraw(false);
    }

    public PopupManager getPopupManager() {
        return this.popupManager;
    }

    public void onPageScrollStateChanged(boolean isStart) {
        log.d("onPageScrollStateChanged(): is start: ", Boolean.valueOf(isStart));
        if (isStart) {
            this.popupManager.hideAll();
        }
    }

    public void onPageScrolled(int pos, float positionOffset, int positionOffsetPixels) {
    }

    public void init(OverlayView overlayView, EmojiCategory cat, EmojiGridParams params, FingerStateListener fingerListener, List<Emoji> items, int pageNumber, EmojiViewPager pager) {
        this.params = params;
        this.pager = pager;
        this.cat = cat;
        this.popupManager = new PopupManager(overlayView, this);
        this.emojiCacheManager = EmojiCacheManager.from(getContext());
        int holdTimeout = UserPreferences.from(getContext()).getLongPressDelay();
        FingerState.Params defaults = FingerState.Params.createDefault(getContext());
        FingerState.Params fingerParams = new FingerState.Params.Builder(defaults).setTrackVelocity(false).setHoldTimeout(holdTimeout).build();
        this.fingerStateManager = new FingerStateManager(getContext(), fingerParams, fingerListener, new KeyMapper());
        setItems(items, cat, pageNumber);
        initSkinPopupView();
        this.userPrefs = UserPreferences.from(getContext());
        this.app = IMEApplication.from(getContext());
        this.mSelectionHighlight = this.app.getThemedDrawable(R.attr.emojiSelectorBackgroundPressed);
    }

    public void showSkinTonePopup(EmojiGridCell cell) {
        log.d("showEmojiPopupWindow(): called  >>>>>>>>>>");
        if (this.popupEmojiSkinList == null || !this.popupEmojiSkinList.isShowing()) {
            dismissEmojiPopup();
            setSkinToneAdapter(cell);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean touchMoveHandle(Fingerable object, FingerInfo pt) {
        log.d("touchMoveHandleBySlideSelectPopup()", "called >>>>>: " + object);
        if (this.popupEmojiSkinList == null) {
            return false;
        }
        this.popupEmojiSkinList.touchMoveHandleBySkinPopup((int) pt.getPosX(), (int) pt.getPosY(), (int) pt.getOffsetX(), (int) pt.getOffsetY());
        return true;
    }

    private void initSkinPopupView() {
        IMEApplication app = IMEApplication.from(getContext());
        LayoutInflater inflater = app.getThemedLayoutInflater(LayoutInflater.from(app));
        app.getThemeLoader().setLayoutInflaterFactory(inflater);
        this.popupEmojiSkinList = new PopupEmojiSkinList(getContext(), inflater, this.pager);
    }

    private void setSkinToneAdapter(EmojiGridCell cell) {
        Emoji emoji;
        if (cell.getCellIndex() <= this.emojis.size() && (emoji = cell.getEmoji()) != null) {
            Emoji.SkinToneEnum skinToneEnum = emoji.getEmojiSkinType();
            int defaultSkinTone = 0;
            if (skinToneEnum != null) {
                defaultSkinTone = skinToneEnum.getSkinValue();
            }
            if (emoji.isSkinTone()) {
                this.skinToneList = emoji.getParentEmoji().getEmojiSkinToneList();
            } else {
                this.skinToneList = this.emojis.get(cell.getCellIndex()).getEmojiSkinToneList();
            }
            EmojiInfo emojiInfo = new EmojiInfo();
            emojiInfo.xPos = cell.getLeft();
            emojiInfo.yPos = cell.getTop();
            emojiInfo.width = cell.getWidth();
            emojiInfo.height = cell.getHeight();
            this.popupEmojiSkinList.setSkinToneAdapter(this.skinToneList, this, emojiInfo, defaultSkinTone);
        }
    }

    public void setCurrentSkinTone(EmojiGridCell cell) {
        if (this.popupEmojiSkinList != null && this.popupEmojiSkinList.isShowing()) {
            this.popupEmojiSkinList.getDefaultSkinTonePopupView();
            return;
        }
        Emoji emoji = cell.getEmoji();
        dismissEmojiPopup();
        setDefaultEmoji(emoji);
    }

    public void dismissEmojiPopup() {
        if (this.popupEmojiSkinList != null && this.popupEmojiSkinList.isShowing()) {
            this.popupEmojiSkinList.dismiss();
        }
    }

    public void setDefaultEmoji(Emoji emoji) {
        if (this.listener != null) {
            this.listener.onSelect(emoji);
            invalidate();
        }
    }

    public void setTouchable(boolean enableTouch) {
        if (!enableTouch) {
            this.fingerStateManager.cancelAll();
        }
        setOnTouchListener(enableTouch ? this.fingerStateManager : null);
    }

    public void setItems(List<Emoji> items, EmojiCategory cat, int pageNumber) {
        int firstIndex = pageNumber * this.params.cellCount;
        if (firstIndex > items.size()) {
            throw new IndexOutOfBoundsException("Bad page number: " + pageNumber);
        }
        int endIndex = Math.min(this.params.cellCount + firstIndex, items.size());
        this.emojis = items.subList(firstIndex, endIndex);
        this.cells = this.params.createCells(getContext(), this, cat, this.emojis);
        log.d("EmojiPageView()", " setItems : called :: emojis.size() ", this.emojis.size() + " , cells count:: " + this.cells.size() + " , pageNumber : " + pageNumber);
        invalidate();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void notifyFeedback(EmojiGridCell cell, EmojiFeedback feedback) {
        if (this.listener != null) {
            this.listener.playFeedback(cell.getText(), feedback);
        }
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        log.d("EmojiPageView(): onDraw :: called  : >>>>>> ");
        canvas.save(1);
        canvas.translate(getPaddingLeft(), getPaddingTop());
        if (isEmojiPressed()) {
            drawEmojiBackground(canvas, this.cell.getLeft(), this.cell.getTop(), this.cell.getRight(), this.cell.getBottom());
        }
        drawCells(canvas);
        canvas.restore();
        if (this.drawDebug != null) {
            this.drawDebug.drawBorderOutline$1be95c50(canvas, getWidth(), getHeight());
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void invalidate(EmojiGridCell cell) {
        log.d("EmojiPageView(): invalidate :: called  : >>>>>> ");
        this.cell = cell;
        invalidate();
    }

    public EmojiGridParams getParams() {
        return this.params;
    }

    private int getEmojiIndex(float x, float y) {
        int index = this.params.getCellIndex(x, y);
        if (index < 0 || index >= this.emojis.size()) {
            return -1;
        }
        return index;
    }

    public String getEmojiText(float x, float y) {
        int idx = getEmojiIndex(x, y);
        if (idx != -1) {
            return this.emojis.get(idx).getEmojiDisplayCode();
        }
        return null;
    }

    protected EmojiGridCell getCell(float x, float y) {
        int idx = getEmojiIndex(x, y);
        if (idx != -1) {
            return this.cells.get(idx);
        }
        return null;
    }

    protected boolean intersects(Rect area, EmojiGridCell cell) {
        cell.getTransformedBounds(this.points);
        if (this.points[0] >= area.right || this.points[2] <= area.left) {
            return false;
        }
        return this.points[1] < area.bottom && this.points[3] > area.top;
    }

    private void drawCells(Canvas canvas) {
        if (canvas.getClipBounds(this.invalid)) {
            this.params.paint.setColor(this.keyColor.getDefaultColor());
            Iterator<EmojiGridCell> it = this.cells.iterator();
            while (it.hasNext()) {
                it.next().draw(canvas, this.drawDebug);
            }
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int minWidth = getSuggestedMinimumWidth();
        int minHeight = getSuggestedMinimumHeight();
        setMeasuredDimension(ViewUtil.getDefaultSizePreferMin(minWidth, widthMeasureSpec), ViewUtil.getDefaultSizePreferMin(minHeight, heightMeasureSpec));
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        dismissEmojiPopup();
        this.popupManager.hideAll();
    }

    @Override // com.nuance.swype.input.emoji.EmojiSkinAdapter.OnItemClickListener
    public void onItemClick(View view, int position, boolean isLongClick) {
        setSelectedSkinTone(position);
    }

    public void setSelectedSkinTone(int position) {
        dismissEmojiPopup();
        Emoji emoji = this.skinToneList.get(position);
        if (emoji != null) {
            setDefaultEmoji(emoji);
            if (!this.cat.isRecentCategory()) {
                String emojiCode = emoji.getEmojiCode();
                if (emoji.getEmojiSkinType() != null) {
                    int skinToneValue = emoji.getEmojiSkinType().getSkinValue();
                    this.emojiCacheManager.addObjectToCache(emojiCode, Integer.valueOf(skinToneValue));
                    this.userPrefs.setCachedEmojiSkinTone(emojiCode, skinToneValue);
                }
                this.cells = this.params.createCells(getContext(), this, this.cat, this.emojis);
                postInvalidate();
            }
        }
    }

    public void drawEmojiSelector(EmojiGridCell cell) {
        if (!isEmojiPressed()) {
            setEmojiPressed(true);
            invalidate(cell);
        }
    }

    void drawEmojiBackground(Canvas canvas, int left, int top, int right, int bottom) {
        if (right - left > 0 && bottom - top > 0) {
            log.d("drawSkinToneSelector()", " draw called ::  left:: " + left + ", top:: " + top + "right :: " + right + " , bottom :: " + bottom);
            canvas.save();
            this.mSelectionHighlight.setBounds(left, top, right, bottom);
            this.mSelectionHighlight.draw(canvas);
            canvas.restore();
        }
    }

    public boolean isEmojiPressed() {
        return this.isEmojiPressed;
    }

    public void setEmojiPressed(boolean emojiPressed) {
        this.isEmojiPressed = emojiPressed;
    }
}
