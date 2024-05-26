package com.localytics.android;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.view.ViewGroup;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class MarketingWebView extends WebView {
    /* JADX INFO: Access modifiers changed from: package-private */
    @SuppressLint({"SetJavaScriptEnabled"})
    @TargetApi(16)
    public MarketingWebView(Context context, ViewGroup.MarginLayoutParams layoutParams) {
        super(context, null);
        setLayoutParams(layoutParams);
        setBackgroundColor(0);
        setInitialScale(1);
        WebSettings settings = getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setUseWideViewPort(true);
    }

    /* loaded from: classes.dex */
    static class MarketingWebViewClient extends WebViewClient {
        private MarketingWebViewManager mWebViewManager;

        /* JADX INFO: Access modifiers changed from: package-private */
        public MarketingWebViewClient(MarketingWebViewManager manager) {
            this.mWebViewManager = manager;
        }

        @Override // android.webkit.WebViewClient
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return this.mWebViewManager.handleShouldOverrideUrlLoading(url);
        }

        @Override // android.webkit.WebViewClient
        @TargetApi(11)
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            return this.mWebViewManager.shouldInterceptRequest(view, url) ? new WebResourceResponse("text/plain", "UTF-8", null) : super.shouldInterceptRequest(view, url);
        }
    }
}
