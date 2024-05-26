package com.nuance.swype.preference;

import android.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/* loaded from: classes.dex */
public class NoMarginClassActionPreference extends ClassActionPreference {
    public float titleTextSize;

    public NoMarginClassActionPreference(Context context) {
        super(context);
        this.titleTextSize = Float.NaN;
    }

    public NoMarginClassActionPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.titleTextSize = Float.NaN;
    }

    public NoMarginClassActionPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.titleTextSize = Float.NaN;
    }

    @Override // android.preference.Preference
    protected View onCreateView(ViewGroup parent) {
        setLayoutResource(getLayoutResource());
        View view = super.onCreateView(parent);
        if (!Float.isNaN(this.titleTextSize)) {
            ((TextView) view.findViewById(R.id.title)).setTextSize(0, this.titleTextSize);
        }
        return view;
    }

    @Override // android.preference.Preference
    public int getLayoutResource() {
        return com.nuance.swype.input.R.layout.preference_no_margin;
    }
}
