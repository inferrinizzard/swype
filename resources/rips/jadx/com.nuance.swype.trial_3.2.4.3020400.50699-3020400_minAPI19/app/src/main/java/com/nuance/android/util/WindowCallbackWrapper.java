package com.nuance.android.util;

import android.annotation.TargetApi;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SearchEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;

/* loaded from: classes.dex */
public class WindowCallbackWrapper implements Window.Callback {
    private Window.Callback target;

    public boolean wraps(Window.Callback callback) {
        for (Window.Callback current = this.target; current != null; current = ((WindowCallbackWrapper) current).target) {
            if (current == callback) {
                return true;
            }
            if (!(current instanceof WindowCallbackWrapper)) {
                return false;
            }
        }
        return false;
    }

    public Window.Callback setTarget(Window.Callback target) {
        this.target = target;
        return this;
    }

    @Override // android.view.Window.Callback
    @TargetApi(12)
    public boolean dispatchGenericMotionEvent(MotionEvent arg0) {
        return this.target.dispatchGenericMotionEvent(arg0);
    }

    @Override // android.view.Window.Callback
    public boolean dispatchKeyEvent(KeyEvent event) {
        return this.target.dispatchKeyEvent(event);
    }

    @Override // android.view.Window.Callback
    @TargetApi(11)
    public boolean dispatchKeyShortcutEvent(KeyEvent event) {
        return this.target.dispatchKeyShortcutEvent(event);
    }

    @Override // android.view.Window.Callback
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        return this.target.dispatchPopulateAccessibilityEvent(event);
    }

    @Override // android.view.Window.Callback
    public boolean dispatchTouchEvent(MotionEvent event) {
        return this.target.dispatchTouchEvent(event);
    }

    @Override // android.view.Window.Callback
    public boolean dispatchTrackballEvent(MotionEvent event) {
        return this.target.dispatchTrackballEvent(event);
    }

    @Override // android.view.Window.Callback
    @TargetApi(11)
    public void onActionModeFinished(ActionMode mode) {
        this.target.onActionModeFinished(mode);
    }

    @Override // android.view.Window.Callback
    @TargetApi(11)
    public void onActionModeStarted(ActionMode mode) {
        this.target.onActionModeStarted(mode);
    }

    @Override // android.view.Window.Callback
    public void onAttachedToWindow() {
        this.target.onContentChanged();
    }

    @Override // android.view.Window.Callback
    public void onContentChanged() {
        this.target.onContentChanged();
    }

    @Override // android.view.Window.Callback
    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        return this.target.onCreatePanelMenu(featureId, menu);
    }

    @Override // android.view.Window.Callback
    public View onCreatePanelView(int featureId) {
        return this.target.onCreatePanelView(featureId);
    }

    @Override // android.view.Window.Callback
    public void onDetachedFromWindow() {
        this.target.onDetachedFromWindow();
    }

    @Override // android.view.Window.Callback
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        return this.target.onMenuItemSelected(featureId, item);
    }

    @Override // android.view.Window.Callback
    public boolean onMenuOpened(int featureId, Menu menu) {
        return this.target.onMenuOpened(featureId, menu);
    }

    @Override // android.view.Window.Callback
    public void onPanelClosed(int featureId, Menu menu) {
        this.target.onPanelClosed(featureId, menu);
    }

    @Override // android.view.Window.Callback
    public boolean onPreparePanel(int featureId, View view, Menu menu) {
        return this.target.onPreparePanel(featureId, view, menu);
    }

    @Override // android.view.Window.Callback
    public boolean onSearchRequested() {
        return this.target.onSearchRequested();
    }

    @Override // android.view.Window.Callback
    public void onWindowAttributesChanged(WindowManager.LayoutParams attrs) {
        try {
            this.target.onWindowAttributesChanged(attrs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override // android.view.Window.Callback
    public void onWindowFocusChanged(boolean hasFocus) {
        this.target.onWindowFocusChanged(hasFocus);
    }

    @Override // android.view.Window.Callback
    @TargetApi(11)
    public ActionMode onWindowStartingActionMode(ActionMode.Callback callback) {
        return this.target.onWindowStartingActionMode(callback);
    }

    @Override // android.view.Window.Callback
    public boolean onSearchRequested(SearchEvent searchEvent) {
        return false;
    }

    @Override // android.view.Window.Callback
    public ActionMode onWindowStartingActionMode(ActionMode.Callback callback, int type) {
        return null;
    }
}
