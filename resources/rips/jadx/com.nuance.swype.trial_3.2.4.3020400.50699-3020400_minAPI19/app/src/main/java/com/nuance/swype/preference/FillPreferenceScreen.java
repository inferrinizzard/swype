package com.nuance.swype.preference;

import android.content.Context;
import android.preference.Preference;
import android.view.View;
import android.view.ViewGroup;
import com.nuance.swype.input.R;

/* loaded from: classes.dex */
public class FillPreferenceScreen extends Preference {
    public FillPreferenceScreen(Context context) {
        super(context);
        setLayoutResource(R.layout.preference_filltext);
        setSelectable(false);
    }

    @Override // android.preference.Preference
    public View getView(View convertView, ViewGroup parent) {
        View v = super.getView(convertView, parent);
        v.setMinimumHeight(parent.getHeight());
        return v;
    }
}
