package com.google.android.gms.common.internal;

import android.R;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
import com.google.android.gms.common.api.Scope;

/* loaded from: classes.dex */
public final class zzag extends Button {
    public zzag(Context context) {
        this(context, null);
    }

    public zzag(Context context, AttributeSet attributeSet) {
        super(context, attributeSet, R.attr.buttonStyle);
    }

    public static int zzd(int i, int i2, int i3) {
        switch (i) {
            case 0:
            case 1:
                return i3;
            case 2:
                return i2;
            default:
                throw new IllegalStateException(new StringBuilder(32).append("Unknown button size: ").append(i).toString());
        }
    }

    public static int zzg(int i, int i2, int i3, int i4) {
        switch (i) {
            case 0:
                return i2;
            case 1:
                return i3;
            case 2:
                return i4;
            default:
                throw new IllegalStateException(new StringBuilder(33).append("Unknown color scheme: ").append(i).toString());
        }
    }

    public static boolean zza(Scope[] scopeArr) {
        if (scopeArr == null) {
            return false;
        }
        for (Scope scope : scopeArr) {
            String str = scope.sp;
            int i = ((!str.contains("/plus.") || str.equals("https://www.googleapis.com/auth/plus.me")) && !str.equals("https://www.googleapis.com/auth/games")) ? i + 1 : 0;
            return true;
        }
        return false;
    }
}
