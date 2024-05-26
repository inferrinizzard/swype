package com.nuance.swype.preference;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import com.nuance.swype.input.R;

/* loaded from: classes.dex */
public class StarTrekPreference extends Preference {
    public StarTrekPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public StarTrekPreference(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    @Override // android.preference.Preference
    protected View onCreateView(ViewGroup parent) {
        setLayoutResource(R.layout.startrek);
        return super.onCreateView(parent);
    }
}
