package android.support.v4.view;

import android.content.Context;
import android.util.Log;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;

/* loaded from: classes.dex */
public abstract class ActionProvider {
    private final Context mContext;
    public SubUiVisibilityListener mSubUiVisibilityListener;
    public VisibilityListener mVisibilityListener;

    /* loaded from: classes.dex */
    public interface SubUiVisibilityListener {
        void onSubUiVisibilityChanged(boolean z);
    }

    /* loaded from: classes.dex */
    public interface VisibilityListener {
        void onActionProviderVisibilityChanged$1385ff();
    }

    public abstract View onCreateActionView();

    public ActionProvider(Context context) {
        this.mContext = context;
    }

    public View onCreateActionView(MenuItem forItem) {
        return onCreateActionView();
    }

    public boolean overridesItemVisibility() {
        return false;
    }

    public boolean isVisible() {
        return true;
    }

    public boolean onPerformDefaultAction() {
        return false;
    }

    public boolean hasSubMenu() {
        return false;
    }

    public void onPrepareSubMenu(SubMenu subMenu) {
    }

    public final void subUiVisibilityChanged(boolean isVisible) {
        if (this.mSubUiVisibilityListener != null) {
            this.mSubUiVisibilityListener.onSubUiVisibilityChanged(isVisible);
        }
    }

    public void setVisibilityListener(VisibilityListener listener) {
        if (this.mVisibilityListener != null) {
            Log.w("ActionProvider(support)", "setVisibilityListener: Setting a new ActionProvider.VisibilityListener when one is already set. Are you reusing this " + getClass().getSimpleName() + " instance while it is still in use somewhere else?");
        }
        this.mVisibilityListener = listener;
    }
}
