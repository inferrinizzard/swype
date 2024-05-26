package com.google.android.gms.common;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import com.google.android.gms.R;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.zzab;
import com.google.android.gms.common.internal.zzaf;
import com.google.android.gms.common.internal.zzag;
import com.google.android.gms.dynamic.zzg;

/* loaded from: classes.dex */
public final class SignInButton extends FrameLayout implements View.OnClickListener {
    private int mColor;
    private int mSize;
    private View.OnClickListener rA;
    private Scope[] ry;
    private View rz;

    public SignInButton(Context context) {
        this(context, null);
    }

    public SignInButton(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public SignInButton(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.rA = null;
        zzb(context, attributeSet);
        setStyle(this.mSize, this.mColor, this.ry);
    }

    private void zzb(Context context, AttributeSet attributeSet) {
        TypedArray obtainStyledAttributes = context.getTheme().obtainStyledAttributes(attributeSet, R.styleable.SignInButton, 0, 0);
        try {
            this.mSize = obtainStyledAttributes.getInt(R.styleable.SignInButton_buttonSize, 0);
            this.mColor = obtainStyledAttributes.getInt(R.styleable.SignInButton_colorScheme, 2);
            String string = obtainStyledAttributes.getString(R.styleable.SignInButton_scopeUris);
            if (string == null) {
                this.ry = null;
            } else {
                String[] split = string.trim().split("\\s+");
                this.ry = new Scope[split.length];
                for (int i = 0; i < split.length; i++) {
                    this.ry[i] = new Scope(split[i].toString());
                }
            }
        } finally {
            obtainStyledAttributes.recycle();
        }
    }

    @Override // android.view.View.OnClickListener
    public final void onClick(View view) {
        if (this.rA == null || view != this.rz) {
            return;
        }
        this.rA.onClick(this);
    }

    public final void setColorScheme(int i) {
        setStyle(this.mSize, i, this.ry);
    }

    @Override // android.view.View
    public final void setEnabled(boolean z) {
        super.setEnabled(z);
        this.rz.setEnabled(z);
    }

    @Override // android.view.View
    public final void setOnClickListener(View.OnClickListener onClickListener) {
        this.rA = onClickListener;
        if (this.rz != null) {
            this.rz.setOnClickListener(this);
        }
    }

    public final void setScopes(Scope[] scopeArr) {
        setStyle(this.mSize, this.mColor, scopeArr);
    }

    public final void setSize(int i) {
        setStyle(i, this.mColor, this.ry);
    }

    public final void setStyle(int i, int i2) {
        setStyle(i, i2, this.ry);
    }

    public final void setStyle(int i, int i2, Scope[] scopeArr) {
        this.mSize = i;
        this.mColor = i2;
        this.ry = scopeArr;
        Context context = getContext();
        if (this.rz != null) {
            removeView(this.rz);
        }
        try {
            this.rz = zzaf.zzb(context, this.mSize, this.mColor, this.ry);
        } catch (zzg.zza e) {
            Log.w("SignInButton", "Sign in button not found, using placeholder instead");
            int i3 = this.mSize;
            int i4 = this.mColor;
            Scope[] scopeArr2 = this.ry;
            zzag zzagVar = new zzag(context);
            Resources resources = context.getResources();
            boolean zza = zzag.zza(scopeArr2);
            zzagVar.setTypeface(Typeface.DEFAULT_BOLD);
            zzagVar.setTextSize(14.0f);
            float f = resources.getDisplayMetrics().density;
            zzagVar.setMinHeight((int) ((f * 48.0f) + 0.5f));
            zzagVar.setMinWidth((int) ((f * 48.0f) + 0.5f));
            zzagVar.setBackgroundDrawable(resources.getDrawable(zza ? zzag.zzd(i3, zzag.zzg(i4, R.drawable.common_plus_signin_btn_icon_dark, R.drawable.common_plus_signin_btn_icon_light, R.drawable.common_plus_signin_btn_icon_dark), zzag.zzg(i4, R.drawable.common_plus_signin_btn_text_dark, R.drawable.common_plus_signin_btn_text_light, R.drawable.common_plus_signin_btn_text_dark)) : zzag.zzd(i3, zzag.zzg(i4, R.drawable.common_google_signin_btn_icon_dark, R.drawable.common_google_signin_btn_icon_light, R.drawable.common_google_signin_btn_icon_light), zzag.zzg(i4, R.drawable.common_google_signin_btn_text_dark, R.drawable.common_google_signin_btn_text_light, R.drawable.common_google_signin_btn_text_light))));
            zzagVar.setTextColor((ColorStateList) zzab.zzy(resources.getColorStateList(zza ? zzag.zzg(i4, R.color.common_plus_signin_btn_text_dark, R.color.common_plus_signin_btn_text_light, R.color.common_plus_signin_btn_text_dark) : zzag.zzg(i4, R.color.common_google_signin_btn_text_dark, R.color.common_google_signin_btn_text_light, R.color.common_google_signin_btn_text_light))));
            switch (i3) {
                case 0:
                    zzagVar.setText(resources.getString(R.string.common_signin_button_text));
                    break;
                case 1:
                    zzagVar.setText(resources.getString(R.string.common_signin_button_text_long));
                    break;
                case 2:
                    zzagVar.setText((CharSequence) null);
                    break;
                default:
                    throw new IllegalStateException(new StringBuilder(32).append("Unknown button size: ").append(i3).toString());
            }
            zzagVar.setTransformationMethod(null);
            this.rz = zzagVar;
        }
        addView(this.rz);
        this.rz.setEnabled(isEnabled());
        this.rz.setOnClickListener(this);
    }
}
