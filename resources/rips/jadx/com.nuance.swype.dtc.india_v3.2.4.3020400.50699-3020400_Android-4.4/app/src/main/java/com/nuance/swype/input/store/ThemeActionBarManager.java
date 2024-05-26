package com.nuance.swype.input.store;

import android.app.ActionBar;
import android.support.v4.app.FragmentActivity;
import java.lang.reflect.Field;

/* loaded from: classes.dex */
public class ThemeActionBarManager {
    private ActionBar mActionBar;
    private FragmentActivity store;

    public ThemeActionBarManager(FragmentActivity activity) {
        this.store = activity;
        this.mActionBar = this.store.getActionBar();
    }

    public void hideActionBar() {
        disableShowHideAnimation();
        this.mActionBar.hide();
    }

    public void showActionBar() {
        disableShowHideAnimation();
        this.mActionBar.show();
    }

    public void disableShowHideAnimation() {
        try {
            this.mActionBar.getClass().getDeclaredMethod("setShowHideAnimationEnabled", Boolean.TYPE).invoke(this.mActionBar, false);
        } catch (Exception e) {
            try {
                Field mActionBarField = this.mActionBar.getClass().getSuperclass().getDeclaredField("mActionBar");
                mActionBarField.setAccessible(true);
                Object icsActionBar = mActionBarField.get(this.mActionBar);
                Field mShowHideAnimationEnabledField = icsActionBar.getClass().getDeclaredField("mShowHideAnimationEnabled");
                mShowHideAnimationEnabledField.setAccessible(true);
                mShowHideAnimationEnabledField.set(icsActionBar, false);
                Field mCurrentShowAnimField = icsActionBar.getClass().getDeclaredField("mCurrentShowAnim");
                mCurrentShowAnimField.setAccessible(true);
                mCurrentShowAnimField.set(icsActionBar, null);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    public void restoreActionBar() {
        disableShowHideAnimation();
        this.mActionBar.show();
    }

    public void release() {
        this.store = null;
    }

    public boolean isActionBarShowing() {
        return this.mActionBar != null && this.mActionBar.isShowing();
    }
}
