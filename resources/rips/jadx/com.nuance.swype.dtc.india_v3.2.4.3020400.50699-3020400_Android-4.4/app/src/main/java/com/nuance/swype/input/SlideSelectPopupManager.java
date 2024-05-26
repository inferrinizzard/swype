package com.nuance.swype.input;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Point;
import android.text.TextUtils;
import android.view.View;
import com.nuance.swype.input.KeyboardEx;
import com.nuance.swype.input.KeyboardViewEx;
import com.nuance.swype.util.LogManager;
import java.util.List;
import java.util.ListIterator;

/* loaded from: classes.dex */
public class SlideSelectPopupManager {
    protected static final LogManager.Log log = LogManager.getLog("SlideSelectPopupManager");
    private int cancelDist;
    private int cancelDistSquared;
    private KeyboardEx.Key currentKey;
    private KeyboardViewEx parentKeyboardView;
    private View popupContainerView;
    private KeyboardViewEx popupKeyboardView;
    private int slideSelectPopupAdjustY;
    private KeyboardEx.Key triggerKey;
    private int xDown;
    private int yDown;
    private Point popupPos = new Point();
    private boolean enableRelativeMovement = false;
    private float movementScaleFactor = 1.0f;

    public SlideSelectPopupManager(KeyboardViewEx parentKeyboardView) {
        this.parentKeyboardView = parentKeyboardView;
        Resources res = parentKeyboardView.getContext().getResources();
        int rowHeight = parentKeyboardView.getMaxRowHeight();
        float adjustYFactor = res.getFraction(R.fraction.slide_select_popup_adjust_y_factor, 1, 1);
        this.slideSelectPopupAdjustY = (int) (rowHeight * adjustYFactor);
        float cancelDistFactor = res.getFraction(R.fraction.slide_select_popup_cancel_dist_factor, 1, 1);
        this.cancelDist = (int) (rowHeight * cancelDistFactor);
        this.cancelDist += this.slideSelectPopupAdjustY;
        this.cancelDistSquared = this.cancelDist * this.cancelDist;
    }

    protected void setDownKey(KeyboardEx.Key key) {
        if (key == null) {
            log.w("setDownKey(): invalid key (setting first as current)");
            key = this.popupKeyboardView.getKeyboard().getKeys().get(0);
        }
        this.xDown = key.x + (key.width / 2);
        this.yDown = key.y + (key.height / 2);
    }

    private int constrainX(int x) {
        int last = ((this.popupKeyboardView.getWidth() - this.popupKeyboardView.getPaddingLeft()) - this.popupKeyboardView.getPaddingRight()) - 1;
        return Math.max(0, Math.min(x, last));
    }

    private int constrainY(int x) {
        int last = ((this.popupKeyboardView.getHeight() - this.popupKeyboardView.getPaddingTop()) - this.popupKeyboardView.getPaddingBottom()) - 1;
        return Math.max(0, Math.min(x, last));
    }

    public void onMove(int x, int y, int xOffset, int yOffset) {
        int xPopup;
        int yPopup;
        if (this.enableRelativeMovement) {
            xPopup = moveOffsetToPopupX(xOffset);
            yPopup = moveOffsetToPopupY(yOffset);
        } else {
            xPopup = x - this.popupPos.x;
            yPopup = y - this.popupPos.y;
        }
        if (this.popupKeyboardView.getHeight() == 0 && this.popupKeyboardView.getWidth() == 0) {
            log.d("onMove: Popup keyboard not yet drawn. Ignoring move event.");
            return;
        }
        KeyboardEx.Key key = findNearestKey(xPopup, yPopup);
        if (isPointInRange(xPopup, yPopup, key)) {
            setCurrentKey(key);
        } else {
            setCurrentKey(null);
        }
    }

    private boolean isPointInRange(int xKeyboard, int yKeyboard) {
        int right = this.popupKeyboardView.getWidth() - this.popupKeyboardView.getPaddingRight();
        int bot = this.popupKeyboardView.getHeight() - this.popupKeyboardView.getPaddingBottom();
        return xKeyboard >= (-this.cancelDist) && xKeyboard <= this.cancelDist + right && yKeyboard >= (-this.cancelDist) && yKeyboard <= this.cancelDist + bot;
    }

    private boolean isPointInRange(int xKeyboard, int yKeyboard, KeyboardEx.Key key) {
        return key.squaredDistanceToEdge(xKeyboard, yKeyboard) < this.cancelDistSquared;
    }

    protected boolean handleSpecialCaseKeyCode(KeyboardEx.Key key) {
        if (this.parentKeyboardView.isExploreByTouchOn()) {
            return false;
        }
        if (-200 == this.currentKey.codes[0]) {
            this.parentKeyboardView.showAltStaticSelectPopup(this.triggerKey);
            return true;
        }
        if (key.popupResId == 0) {
            return false;
        }
        KeyboardEx.Key movedKey = new KeyboardEx.Key(key, this.triggerKey.x, this.triggerKey.y, this.triggerKey.gap, this.triggerKey.width, this.triggerKey.height);
        this.parentKeyboardView.showStaticSelectPopup(movedKey);
        return true;
    }

    protected boolean sendKeyCode(KeyboardViewEx.OnKeyboardActionListener listener, KeyboardEx.Key key) {
        if (this.currentKey.codes[0] == 4063) {
            return false;
        }
        listener.onKey(null, this.currentKey.codes[0], this.currentKey.codes, key, 0L);
        return true;
    }

    protected boolean sendKeyText(KeyboardViewEx.OnKeyboardActionListener listener, KeyboardEx.Key key) {
        CharSequence text = key.text;
        if (TextUtils.isEmpty(text)) {
            text = key.label;
        }
        if (TextUtils.isEmpty(text)) {
            return false;
        }
        if (text != null && text.length() == 1 && key.codes[0] != 4063) {
            listener.onKey(null, text.charAt(0), key.codes, key, 0L);
        } else {
            listener.onText(key.text, 0L);
            listener.onRelease(-1);
        }
        return true;
    }

    public void onCancel() {
        log.d("onCancel()");
        setCurrentKey(null);
    }

    public void onUp() {
        KeyboardViewEx.OnKeyboardActionListener listener;
        log.d("onUp()");
        if (this.currentKey != null && (listener = this.popupKeyboardView.getOnKeyboardActionListener()) != null && !handleSpecialCaseKeyCode(this.currentKey) && !sendKeyCode(listener, this.currentKey)) {
            sendKeyText(listener, this.currentKey);
        }
        setCurrentKey(null);
    }

    public KeyboardEx.Key getCurrentKey() {
        return this.currentKey;
    }

    public KeyboardEx.Key getTriggerKey() {
        return this.triggerKey;
    }

    public List<KeyboardEx.Key> getKeyList() {
        return this.popupKeyboardView.getKeyboard().getKeys();
    }

    public KeyboardEx.Key getDefaultKey() {
        return this.popupKeyboardView.getKeyboard().getDefaultKey();
    }

    protected static void toggleKey(KeyboardViewEx keyboard, KeyboardEx.Key key, boolean press) {
        if (press) {
            if (!key.isPressed()) {
                key.onPressed();
                keyboard.invalidateKey(key);
                return;
            }
            return;
        }
        if (key.isPressed()) {
            key.onPressed();
            keyboard.invalidateKey(key);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setCurrentKey(KeyboardEx.Key key) {
        if (key != null && !isActive(key)) {
            log.d("setCurrentKey(): inactive (ignoring): ", key);
            log.d("setCurrentKey(): vis: ", Boolean.valueOf(key.visible), "; dimmed: ", Boolean.valueOf(key.dimmed));
        } else if (key != this.currentKey) {
            if (this.currentKey != null) {
                toggleKey(this.popupKeyboardView, this.currentKey, false);
            }
            this.currentKey = key;
            if (this.currentKey != null) {
                toggleKey(this.popupKeyboardView, this.currentKey, true);
            }
        }
    }

    protected int moveOffsetToPopupX(int xOffset) {
        return this.xDown + ((int) (xOffset * this.movementScaleFactor));
    }

    protected int moveOffsetToPopupY(int yOffset) {
        return this.yDown + ((int) (yOffset * this.movementScaleFactor));
    }

    protected KeyboardEx.Key findNearestKey(int xKeyboard, int yKeyboard) {
        int distSquared;
        int xKeyboard2 = constrainX(xKeyboard);
        int yKeyboard2 = constrainY(yKeyboard);
        if (this.currentKey != null && this.currentKey.contains(xKeyboard2, yKeyboard2)) {
            return this.currentKey;
        }
        KeyboardEx.Key nearestKey = null;
        int nearestDistSquared = Integer.MAX_VALUE;
        for (KeyboardEx.Key key : this.popupKeyboardView.getKeyboard().getKeys()) {
            if (isActive(key) && (distSquared = key.squaredDistanceToEdge(xKeyboard2, yKeyboard2)) < nearestDistSquared) {
                nearestKey = key;
                nearestDistSquared = distSquared;
            }
        }
        return nearestKey;
    }

    public static int calcGravity(View parentView, KeyboardEx.Key popupKey) {
        int xMiddle = parentView.getWidth() / 2;
        return ((parentView.getPaddingLeft() + popupKey.x) + (popupKey.width / 2) > xMiddle ? 8388613 : 8388611) | 80;
    }

    public Point preparePopup(View popupContainerView, KeyboardViewEx popupKeyboard, KeyboardEx.Key popupKey, Point downPos) {
        this.popupContainerView = popupContainerView;
        this.popupKeyboardView = popupKeyboard;
        this.triggerKey = popupKey;
        KeyboardEx.Key key = popupKeyboard.getKeyboard().getDefaultKey();
        setDownKey(key);
        this.popupPos = calcPopupKeyboardPos();
        setCurrentKey(key);
        return toWindowPos(this.popupPos);
    }

    public Point preparePopupForKeypad(View popupContainerView, KeyboardViewEx popupKeyboard, KeyboardEx.Key popupKey, Point downPos) {
        return preparePopup(popupContainerView, popupKeyboard, popupKey, downPos);
    }

    public KeyboardEx createPopupCharsKeyboard(int popupKeyboardId, CharSequence popupChars, KeyboardEx.Key extraKey, int idxDefault, KeyboardEx.Key popupKey, int altCharsPopupMaxCol, int horPadding) {
        int gravity = calcGravity(this.parentKeyboardView, popupKey);
        if (-1 == idxDefault && extraKey == null) {
            log.w("createPopupCharsKeyboard(): selecting first char as default");
            idxDefault = 0;
        }
        return new KeyboardEx(this.parentKeyboardView.getContext(), popupKeyboardId, popupChars, extraKey, idxDefault, gravity, altCharsPopupMaxCol, horPadding, true);
    }

    private static KeyboardEx.Key findFirstActiveFromLeft(List<KeyboardEx.Key> keys) {
        for (KeyboardEx.Key key : keys) {
            if (isActive(key)) {
                return key;
            }
        }
        return null;
    }

    private static KeyboardEx.Key findFirstActiveFromRight(List<KeyboardEx.Key> keys) {
        ListIterator<KeyboardEx.Key> iter = keys.listIterator(keys.size());
        while (iter.hasPrevious()) {
            KeyboardEx.Key key = iter.previous();
            if (isActive(key)) {
                return key;
            }
        }
        return null;
    }

    private static boolean isActive(KeyboardEx.Key key) {
        return key.visible && !key.dimmed;
    }

    @SuppressLint({"RtlHardcoded"})
    public KeyboardEx createStaticKeyboard(int popupKeyboardId, int idxDefault, KeyboardEx.Key popupKey) {
        int gravity = calcGravity(this.parentKeyboardView, popupKey);
        KeyboardEx keyboard = new KeyboardEx(this.parentKeyboardView.getContext(), popupKeyboardId, false, true, true);
        if (keyboard.getDefaultKey() == null) {
            int hGrav = gravity & 7;
            int rowCount = keyboard.mKeyboardLayout.size();
            if (rowCount > 0) {
                List<KeyboardEx.Key> keys = keyboard.mKeyboardLayout.get(rowCount - 1).mKeys;
                for (KeyboardEx.Key key : keys) {
                    if (popupKey.codes[0] == 10 && key.codes[0] == 10) {
                        if (popupKey.label != null) {
                            key.label = popupKey.label;
                            key.labelUpperCase = popupKey.labelUpperCase;
                            key.icon = null;
                            key.iconPreview = null;
                        } else {
                            key.icon = popupKey.icon;
                            key.iconPreview = popupKey.iconPreview;
                            key.label = null;
                        }
                    }
                    key.isMiniKeyboardKey = true;
                }
                if (keys.size() > 0) {
                    keyboard.setDefaultKey((8388613 == hGrav || 5 == hGrav) ? findFirstActiveFromRight(keys) : findFirstActiveFromLeft(keys));
                }
            }
        }
        return keyboard;
    }

    @SuppressLint({"RtlHardcoded"})
    protected Point calcPopupKeyboardPos() {
        int xPopup;
        int hGrav = calcGravity(this.parentKeyboardView, this.triggerKey) & 7;
        int xKey = this.triggerKey.x + this.parentKeyboardView.getPaddingLeft();
        int yKey = this.triggerKey.y + this.parentKeyboardView.getPaddingTop();
        if (8388613 == hGrav || 5 == hGrav) {
            xPopup = ((this.triggerKey.width + xKey) - this.popupContainerView.getMeasuredWidth()) + this.popupContainerView.getPaddingRight();
        } else {
            xPopup = xKey - this.popupContainerView.getPaddingLeft();
        }
        int yPopup = ((yKey - this.popupContainerView.getMeasuredHeight()) - this.slideSelectPopupAdjustY) + this.popupContainerView.getPaddingBottom();
        return new Point(xPopup, yPopup);
    }

    protected Point toWindowPos(Point pt) {
        int[] winOffset = new int[2];
        this.parentKeyboardView.getLocationInWindow(winOffset);
        int x = pt.x + winOffset[0];
        int y = pt.y + winOffset[1];
        int margin = 0;
        if (this.triggerKey != null) {
            margin = -(this.triggerKey.height / 2);
        }
        if (y < margin || this.parentKeyboardView.isFullScreenHandWritingView()) {
            this.enableRelativeMovement = true;
        } else {
            this.enableRelativeMovement = false;
        }
        return new Point(x, y);
    }
}
