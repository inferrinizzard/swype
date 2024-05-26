package com.nuance.swype.widget;

import android.R;
import android.annotation.TargetApi;
import android.content.Context;
import android.preference.SwitchPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

@TargetApi(14)
/* loaded from: classes.dex */
public class MultilineSwitchPreference extends SwitchPreference {
    public MultilineSwitchPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MultilineSwitchPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MultilineSwitchPreference(Context context) {
        super(context);
    }

    @Override // android.preference.SwitchPreference, android.preference.Preference
    public void onBindView(View view) {
        super.onBindView(view);
        TextView titleView = (TextView) view.findViewById(R.id.title);
        if (titleView != null) {
            titleView.setSingleLine(false);
        }
    }
}
