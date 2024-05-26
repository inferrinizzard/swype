package android.support.v7.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.v4.content.res.ConfigurationHelper;
import android.support.v7.appcompat.R;

/* loaded from: classes.dex */
public final class ActionBarPolicy {
    public Context mContext;

    public static ActionBarPolicy get(Context context) {
        return new ActionBarPolicy(context);
    }

    private ActionBarPolicy(Context context) {
        this.mContext = context;
    }

    public final int getMaxActionButtons() {
        Resources res = this.mContext.getResources();
        int widthDp = ConfigurationHelper.getScreenWidthDp(res);
        int heightDp = ConfigurationHelper.getScreenHeightDp(res);
        if (ConfigurationHelper.getSmallestScreenWidthDp(res) > 600 || widthDp > 600 || ((widthDp > 960 && heightDp > 720) || (widthDp > 720 && heightDp > 960))) {
            return 5;
        }
        if (widthDp >= 500 || ((widthDp > 640 && heightDp > 480) || (widthDp > 480 && heightDp > 640))) {
            return 4;
        }
        if (widthDp >= 360) {
            return 3;
        }
        return 2;
    }

    public final int getTabContainerHeight() {
        TypedArray a = this.mContext.obtainStyledAttributes(null, R.styleable.ActionBar, R.attr.actionBarStyle, 0);
        int height = a.getLayoutDimension(R.styleable.ActionBar_height, 0);
        Resources r = this.mContext.getResources();
        if (!this.mContext.getResources().getBoolean(R.bool.abc_action_bar_embed_tabs)) {
            height = Math.min(height, r.getDimensionPixelSize(R.dimen.abc_action_bar_stacked_max_height));
        }
        a.recycle();
        return height;
    }

    public final int getStackedTabMaxWidth() {
        return this.mContext.getResources().getDimensionPixelSize(R.dimen.abc_action_bar_stacked_tab_max_width);
    }
}
