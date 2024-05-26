package com.nuance.swype.input.emoji.util;

import android.graphics.Rect;
import android.view.View;
import com.nuance.swype.util.CoordUtils;
import com.nuance.swype.util.GeomUtil;
import com.nuance.swype.view.OverlayView;
import java.util.HashSet;
import java.util.Iterator;

/* loaded from: classes.dex */
public class PopupManager {
    private View host;
    private OverlayView overlayView;
    private final HashSet<PopupView> popups = new HashSet<>();

    public PopupManager(OverlayView overlayView, View host) {
        this.overlayView = overlayView;
        this.host = host;
    }

    public void confineContentToOverlay(int[] pos, int width, int height, int padding) {
        mapContentToOverlay(pos);
        int x = pos[0];
        int y = pos[1];
        Rect rc = new Rect(x, y, x + width, y + height);
        GeomUtil.confine(rc, this.overlayView.getDimsRect(), padding);
        pos[0] = rc.left;
        pos[1] = rc.top;
        mapOverlayToContent(pos);
    }

    protected int[] mapViewToOverlay(int x, int y) {
        int[] out = CoordUtils.newInstance(x, y);
        mapViewToOverlay(out);
        return out;
    }

    public int[] mapContentToOverlay(int x, int y) {
        int[] out = CoordUtils.newInstance(x, y);
        out[0] = out[0] + this.host.getPaddingLeft();
        out[1] = out[1] + this.host.getPaddingTop();
        mapViewToOverlay(out);
        return out;
    }

    protected void mapContentToOverlay(int[] pos) {
        pos[0] = pos[0] + this.host.getPaddingLeft();
        pos[1] = pos[1] + this.host.getPaddingTop();
        mapViewToOverlay(pos);
    }

    protected void mapViewToOverlay(int[] pos) {
        int[] offset = this.overlayView.getOverlayPos(this.host);
        pos[0] = pos[0] + offset[0];
        pos[1] = pos[1] + offset[1];
    }

    protected void mapOverlayToView(int[] pos) {
        int[] offset = this.overlayView.getOverlayPos(this.host);
        pos[0] = pos[0] - offset[0];
        pos[1] = pos[1] - offset[1];
    }

    protected void mapOverlayToContent(int[] pos) {
        mapOverlayToView(pos);
        pos[0] = pos[0] - this.host.getPaddingLeft();
        pos[1] = pos[1] - this.host.getPaddingTop();
    }

    public int[] getOffsetFromAnchorPos(PopupView popupView, int left, int right) {
        int[] pos = mapContentToOverlay(left, right);
        popupView.mapRelativeToAnchorPos(pos);
        return pos;
    }

    public void hideAll() {
        Iterator<PopupView> iter = this.popups.iterator();
        while (iter.hasNext()) {
            PopupView popupView = iter.next();
            iter.remove();
            this.overlayView.removeView(popupView);
            popupView.onRemoved();
        }
    }

    public boolean addPopup(PopupView popupView) {
        if (!track(popupView)) {
            return false;
        }
        this.overlayView.addView(popupView);
        popupView.onAdded();
        return true;
    }

    public boolean removePopup(PopupView popupView) {
        if (!untrack(popupView)) {
            return false;
        }
        this.overlayView.removeView(popupView);
        popupView.onRemoved();
        return true;
    }

    private boolean track(PopupView popupView) {
        if (this.popups.contains(popupView)) {
            return false;
        }
        this.popups.add(popupView);
        return true;
    }

    private boolean untrack(PopupView popupView) {
        if (!this.popups.contains(popupView)) {
            return false;
        }
        this.popups.remove(popupView);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onDetachedFromWindow(PopupView popupView) {
        if (untrack(popupView)) {
            popupView.onRemoved();
        }
    }
}
