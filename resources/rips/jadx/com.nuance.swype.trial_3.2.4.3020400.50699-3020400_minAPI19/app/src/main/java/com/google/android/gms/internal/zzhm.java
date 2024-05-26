package com.google.android.gms.internal;

import android.content.Context;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@zzin
/* loaded from: classes.dex */
public final class zzhm implements zzhk {
    final Context mContext;
    final Set<WebView> zzbwh = Collections.synchronizedSet(new HashSet());

    public zzhm(Context context) {
        this.mContext = context;
    }

    @Override // com.google.android.gms.internal.zzhk
    public final void zza$14e1ec6d(final String str, final String str2) {
        zzkd.zzcv("Fetching assets for the given html");
        zzkh.zzclc.post(new Runnable() { // from class: com.google.android.gms.internal.zzhm.1
            @Override // java.lang.Runnable
            public final void run() {
                final WebView webView = new WebView(zzhm.this.mContext);
                webView.getSettings().setJavaScriptEnabled(true);
                webView.setWebViewClient(new WebViewClient() { // from class: com.google.android.gms.internal.zzhm.1.1
                    @Override // android.webkit.WebViewClient
                    public final void onPageFinished(WebView webView2, String str3) {
                        zzkd.zzcv("Loading assets have finished");
                        zzhm.this.zzbwh.remove(webView);
                    }

                    @Override // android.webkit.WebViewClient
                    public final void onReceivedError(WebView webView2, int i, String str3, String str4) {
                        zzkd.zzcx("Loading assets have failed.");
                        zzhm.this.zzbwh.remove(webView);
                    }
                });
                zzhm.this.zzbwh.add(webView);
                webView.loadDataWithBaseURL(str, str2, "text/html", "UTF-8", null);
                zzkd.zzcv("Fetching assets finished.");
            }
        });
    }
}
