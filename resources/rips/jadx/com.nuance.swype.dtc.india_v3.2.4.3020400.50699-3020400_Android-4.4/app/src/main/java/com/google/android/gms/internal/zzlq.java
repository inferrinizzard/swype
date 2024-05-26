package com.google.android.gms.internal;

import android.annotation.TargetApi;
import android.view.View;
import android.webkit.WebChromeClient;

@TargetApi(14)
@zzin
/* loaded from: classes.dex */
public final class zzlq extends zzlo {
    public zzlq(zzlh zzlhVar) {
        super(zzlhVar);
    }

    @Override // android.webkit.WebChromeClient
    public final void onShowCustomView(View view, int i, WebChromeClient.CustomViewCallback customViewCallback) {
        zza(view, i, customViewCallback);
    }
}
