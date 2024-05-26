package com.nuance.swype.preference;

import android.R;
import android.annotation.TargetApi;
import android.content.Context;
import android.preference.CheckBoxPreference;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

@TargetApi(14)
/* loaded from: classes.dex */
public class CustomCheckBoxPreference extends CheckBoxPreference {
    public CustomCheckBoxPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public CustomCheckBoxPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomCheckBoxPreference(Context context) {
        super(context);
    }

    @Override // android.preference.Preference
    protected View onCreateView(ViewGroup parent) {
        View view = super.onCreateView(parent);
        TextView summary = (TextView) view.findViewById(R.id.summary);
        if (summary != null) {
            summary.setEllipsize(TextUtils.TruncateAt.END);
            summary.setMaxLines(8);
        }
        return view;
    }
}
