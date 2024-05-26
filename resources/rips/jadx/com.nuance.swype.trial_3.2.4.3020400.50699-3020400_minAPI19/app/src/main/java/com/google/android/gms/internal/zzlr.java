package com.google.android.gms.internal;

import android.text.TextUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import java.net.URI;
import java.net.URISyntaxException;

@zzin
/* loaded from: classes.dex */
public final class zzlr extends WebViewClient {
    private final zzlh zzbgf;
    private final zzhz zzbyg;
    private final String zzcqm;
    private boolean zzcqn = false;

    public zzlr(zzhz zzhzVar, zzlh zzlhVar, String str) {
        this.zzcqm = zzdf(str);
        this.zzbgf = zzlhVar;
        this.zzbyg = zzhzVar;
    }

    private boolean zzde(String str) {
        boolean z = false;
        String zzdf = zzdf(str);
        if (!TextUtils.isEmpty(zzdf)) {
            try {
                URI uri = new URI(zzdf);
                if ("passback".equals(uri.getScheme())) {
                    zzkd.zzcv("Passback received");
                    this.zzbyg.zzqa();
                    z = true;
                } else if (!TextUtils.isEmpty(this.zzcqm)) {
                    URI uri2 = new URI(this.zzcqm);
                    String host = uri2.getHost();
                    String host2 = uri.getHost();
                    String path = uri2.getPath();
                    String path2 = uri.getPath();
                    if (com.google.android.gms.common.internal.zzaa.equal(host, host2) && com.google.android.gms.common.internal.zzaa.equal(path, path2)) {
                        zzkd.zzcv("Passback received");
                        this.zzbyg.zzqa();
                        z = true;
                    }
                }
            } catch (URISyntaxException e) {
                zzkd.e(e.getMessage());
            }
        }
        return z;
    }

    private static String zzdf(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        try {
            return str.endsWith("/") ? str.substring(0, str.length() - 1) : str;
        } catch (IndexOutOfBoundsException e) {
            zzkd.e(e.getMessage());
            return str;
        }
    }

    @Override // android.webkit.WebViewClient
    public final void onLoadResource(WebView webView, String str) {
        String valueOf = String.valueOf(str);
        zzkd.zzcv(valueOf.length() != 0 ? "JavascriptAdWebViewClient::onLoadResource: ".concat(valueOf) : new String("JavascriptAdWebViewClient::onLoadResource: "));
        if (zzde(str)) {
            return;
        }
        this.zzbgf.zzuj().onLoadResource(this.zzbgf.getWebView(), str);
    }

    @Override // android.webkit.WebViewClient
    public final boolean shouldOverrideUrlLoading(WebView webView, String str) {
        String valueOf = String.valueOf(str);
        zzkd.zzcv(valueOf.length() != 0 ? "JavascriptAdWebViewClient::shouldOverrideUrlLoading: ".concat(valueOf) : new String("JavascriptAdWebViewClient::shouldOverrideUrlLoading: "));
        if (!zzde(str)) {
            return this.zzbgf.zzuj().shouldOverrideUrlLoading(this.zzbgf.getWebView(), str);
        }
        zzkd.zzcv("shouldOverrideUrlLoading: received passback url");
        return true;
    }

    @Override // android.webkit.WebViewClient
    public final void onPageFinished(WebView webView, String str) {
        String valueOf = String.valueOf(str);
        zzkd.zzcv(valueOf.length() != 0 ? "JavascriptAdWebViewClient::onPageFinished: ".concat(valueOf) : new String("JavascriptAdWebViewClient::onPageFinished: "));
        if (this.zzcqn) {
            return;
        }
        zzhz zzhzVar = this.zzbyg;
        zzhzVar.zzbxx.postDelayed(zzhzVar, zzhzVar.zzbxy);
        this.zzcqn = true;
    }
}
