package com.nuance.swype.widget.directional;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/* loaded from: classes.dex */
public class DirectionalRelativeLayout extends RelativeLayout {
    public DirectionalRelativeLayout(Context context) {
        super(context);
    }

    public DirectionalRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DirectionalRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override // android.widget.RelativeLayout, android.view.ViewGroup
    @TargetApi(17)
    public RelativeLayout.LayoutParams generateLayoutParams(AttributeSet attrs) {
        RelativeLayout.LayoutParams lp = super.generateLayoutParams(attrs);
        int[] rules = lp.getRules();
        if (rules.length > 20 && rules[20] != 0) {
            lp.removeRule(9);
        }
        if (rules.length > 21 && rules[21] != 0) {
            lp.removeRule(11);
        }
        if (rules.length > 18 && rules[18] != 0) {
            lp.removeRule(5);
        }
        if (rules.length > 19 && rules[19] != 0) {
            lp.removeRule(7);
        }
        if (rules.length > 16 && rules[16] != 0) {
            lp.removeRule(0);
        }
        if (rules.length > 17 && rules[17] != 0) {
            lp.removeRule(1);
        }
        return lp;
    }
}
