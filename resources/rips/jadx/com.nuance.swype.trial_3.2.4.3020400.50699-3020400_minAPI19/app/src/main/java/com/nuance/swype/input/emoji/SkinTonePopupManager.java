package com.nuance.swype.input.emoji;

import android.content.Context;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.nuance.swype.input.KeyboardEx;
import com.nuance.swype.input.UserPreferences;
import com.nuance.swype.util.LogManager;

/* loaded from: classes.dex */
public class SkinTonePopupManager {
    private static final int FINGER_MOVEMENT_VELOCITY = 30;
    protected static final LogManager.Log log = LogManager.getLog("SkinTonePopupManager");
    private Context context;
    private View parentView;
    private View popupContainerView;
    private RecyclerView popupRecyclerView;
    private int skinTonePosition;
    private EmojiInfo triggerEmoji;
    private Point popupPos = new Point();
    private View defaultSkinTone = null;
    private int[] outLocation = new int[2];

    public SkinTonePopupManager(View parentView, Context context) {
        this.parentView = parentView;
        this.context = context;
    }

    public static int calcGravity(View parentView, EmojiInfo popupEmoji) {
        int xMiddle = parentView.getWidth() / 2;
        int xKey = parentView.getPaddingLeft() + popupEmoji.getLeft() + (popupEmoji.getWidth() / 2);
        int gravity = (xKey > xMiddle ? 8388613 : 8388611) | 80;
        log.d("calcGravity()", ": called  >>>>>>>>>> xMiddle:" + xMiddle + " , gravity: " + gravity + " , xKey:" + xKey);
        return gravity;
    }

    public Point preparePopup(View popupContainerView, RecyclerView popupRecyclerView, EmojiInfo triggerEmoji) {
        this.popupContainerView = popupContainerView;
        this.popupRecyclerView = popupRecyclerView;
        this.triggerEmoji = triggerEmoji;
        this.popupPos = calcPopupKeyboardPos();
        return toWindowPos(this.popupPos);
    }

    protected Point calcPopupKeyboardPos() {
        int xPopup;
        int hGrav = calcGravity(this.parentView, this.triggerEmoji) & 7;
        int xKey = this.triggerEmoji.getLeft();
        int yKey = this.triggerEmoji.getTop() + this.parentView.getPaddingTop();
        log.d("calcPopupKeyboardPos()", ": called  >>>>>> xKey:" + xKey + " ,yKey:: " + yKey);
        if (8388613 == hGrav || 5 == hGrav) {
            xPopup = ((this.triggerEmoji.getWidth() + xKey) - this.popupContainerView.getMeasuredWidth()) + this.popupContainerView.getPaddingRight();
        } else {
            xPopup = xKey - this.popupContainerView.getPaddingLeft();
        }
        if (isNotBoundWithKBLayout(xPopup) && !isLandscapeRightDockMode()) {
            xPopup = getXCoordinate();
        }
        int yPopup = (yKey - this.popupContainerView.getMeasuredHeight()) + this.popupContainerView.getPaddingBottom();
        return new Point(xPopup, yPopup);
    }

    private boolean isNotBoundWithKBLayout(int xPopup) {
        int tWidth = xPopup + this.popupContainerView.getMeasuredWidth();
        return xPopup < 0 || tWidth > this.parentView.getWidth();
    }

    private int getXCoordinate() {
        this.parentView.getLocationInWindow(this.outLocation);
        return isRightDockMode() ? ((this.outLocation[0] + this.parentView.getPaddingLeft()) - this.triggerEmoji.getWidth()) - this.triggerEmoji.getHeight() : this.outLocation[0] + this.parentView.getPaddingLeft();
    }

    protected Point toWindowPos(Point pt) {
        int[] winOffset = new int[2];
        this.parentView.getLocationInWindow(winOffset);
        int x = pt.x + winOffset[0];
        int y = pt.y + winOffset[1];
        if (isNotBoundWithKBLayout(x) && isLandscapeRightDockMode()) {
            x = winOffset[0] + this.triggerEmoji.getWidth();
        }
        return new Point(x, y);
    }

    public void onMove(int x, int y, int xOffset, int yOffset) {
        log.d("onMove()", "called >>>>>: x :: " + x + " , y:: " + y + " , xOffset" + xOffset + " , yOffset::" + yOffset);
        if (xOffset < 0) {
            xOffset = -xOffset;
        }
        if (xOffset >= 30) {
            int xPopup = x - this.popupPos.x;
            int yPopup = y - this.popupPos.y;
            if (this.popupRecyclerView.getHeight() == 0 && this.popupRecyclerView.getWidth() == 0) {
                log.d("onMove: Popup keyboard not yet drawn. Ignoring move event.");
            } else {
                this.defaultSkinTone = findNearestSkinTone(xPopup, yPopup);
            }
        }
    }

    public void onMove(int x, int y, int xOffset) {
        if (xOffset < 0) {
            xOffset = -xOffset;
        }
        if (xOffset >= 30) {
            int xPopup = x - this.popupPos.x;
            int yPopup = y - this.popupPos.y;
            if (this.popupRecyclerView.getHeight() == 0 && this.popupRecyclerView.getWidth() == 0) {
                log.d("onMove: Popup keyboard not yet drawn. Ignoring move event.");
            } else {
                this.defaultSkinTone = findNearestSkinTone(xPopup, yPopup);
            }
        }
    }

    public View getDefaultSkinTone() {
        return this.defaultSkinTone;
    }

    public void setSkinTonePosition(int position) {
        this.skinTonePosition = position;
    }

    public int getSkinTonePosition() {
        return this.skinTonePosition;
    }

    protected View findNearestSkinTone(int xKeyboard, int yKeyboard) {
        View closestView = this.defaultSkinTone;
        int minDistance = Integer.MAX_VALUE;
        int childCount = this.popupRecyclerView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = this.popupRecyclerView.getChildAt(i);
            int distance = distanceToView(xKeyboard, child);
            if (distance < minDistance) {
                closestView = child;
                minDistance = distance;
                setSkinTonePosition(i);
            }
            child.setPressed(false);
        }
        closestView.setPressed(true);
        return closestView;
    }

    private int distanceToView(int xKeyboard, View view) {
        view.getLocationInWindow(this.outLocation);
        int distance = xKeyboard - (this.outLocation[0] + (view.getWidth() / 2));
        if (distance < 0) {
            return -distance;
        }
        return distance;
    }

    private boolean isRightDockMode() {
        return UserPreferences.from(this.context).getKeyboardDockingMode() == KeyboardEx.KeyboardDockMode.DOCK_RIGHT;
    }

    private boolean isLandscapeRightDockMode() {
        boolean landscape = this.context.getResources().getConfiguration().orientation == 2;
        return isRightDockMode() && landscape;
    }
}
