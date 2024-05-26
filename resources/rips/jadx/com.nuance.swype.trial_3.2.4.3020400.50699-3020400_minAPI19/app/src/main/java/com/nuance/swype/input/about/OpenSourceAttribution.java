package com.nuance.swype.input.about;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import com.nuance.swype.input.R;
import com.nuance.swype.util.LocalizationUtils;

/* loaded from: classes.dex */
public class OpenSourceAttribution extends Activity {
    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WebView webView = new WebView(this);
        webView.setBackgroundColor(0);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setScrollBarStyle(0);
        String url = getResources().getString(R.string.open_source_attribution_file);
        webView.loadUrl(LocalizationUtils.ASSETS_URL_PREFIX_HELPS + url);
        setContentView(webView);
    }
}
