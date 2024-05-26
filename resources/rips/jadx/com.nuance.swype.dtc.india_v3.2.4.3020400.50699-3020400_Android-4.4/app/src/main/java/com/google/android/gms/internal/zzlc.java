package com.google.android.gms.internal;

import android.view.View;
import android.view.ViewTreeObserver;

@zzin
/* loaded from: classes.dex */
public final class zzlc {
    public static void zza(View view, ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener) {
        new zzld(view, onGlobalLayoutListener).zzua();
    }

    public static void zza(View view, ViewTreeObserver.OnScrollChangedListener onScrollChangedListener) {
        new zzle(view, onScrollChangedListener).zzua();
    }
}
