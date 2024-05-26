package com.nuance.swype.preference;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.nuance.android.compat.PreferenceCompat;
import com.nuance.swype.input.R;

/* loaded from: classes.dex */
public class IconPreferenceScreen extends ClassActionPreference {
    private final Context ctx;
    private Drawable icon;
    private View view;

    @TargetApi(11)
    public static Preference createPreferenceWithIcon(Context context, Drawable icon) {
        Preference pref = new Preference(context);
        PreferenceCompat.setIcon(pref, icon);
        return pref;
    }

    public IconPreferenceScreen(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IconPreferenceScreen(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.ctx = context;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.IconPreferenceScreen, defStyle, 0);
        this.icon = a.getDrawable(R.styleable.IconPreferenceScreen_icon);
        a.recycle();
    }

    @Override // android.preference.Preference
    protected View onCreateView(ViewGroup parent) {
        ViewGroup v = (ViewGroup) super.onCreateView(parent);
        if (v.findViewById(android.R.id.icon) == null && this.icon != null) {
            ImageView image = new ImageView(this.ctx);
            image.setImageDrawable(this.icon);
            v.addView(image, 0, new ViewGroup.LayoutParams(-2, -2));
            this.view = v;
        }
        return v;
    }

    @Override // com.nuance.swype.preference.ClassActionPreference, com.nuance.swype.preference.ViewClickPreference, android.preference.Preference
    public void onBindView(View view) {
        super.onBindView(view);
        ImageView imageView = (ImageView) view.findViewById(android.R.id.icon);
        if (imageView != null && this.icon != null) {
            imageView.setImageDrawable(this.icon);
        }
    }

    @Override // android.preference.Preference
    public void setIcon(Drawable d) {
        ImageView imageView;
        this.icon = d;
        if (this.view != null && (imageView = (ImageView) this.view.findViewById(android.R.id.icon)) != null && this.icon != null) {
            imageView.setImageDrawable(this.icon);
        }
    }
}
