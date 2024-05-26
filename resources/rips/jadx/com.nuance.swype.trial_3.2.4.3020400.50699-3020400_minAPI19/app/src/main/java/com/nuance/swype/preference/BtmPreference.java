package com.nuance.swype.preference;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import com.nuance.swype.input.R;

/* loaded from: classes.dex */
public class BtmPreference extends Preference {
    public BtmPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public BtmPreference(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    @Override // android.preference.Preference
    protected View onCreateView(ViewGroup parent) {
        setLayoutResource(R.layout.btm_attribution);
        return super.onCreateView(parent);
    }
}
