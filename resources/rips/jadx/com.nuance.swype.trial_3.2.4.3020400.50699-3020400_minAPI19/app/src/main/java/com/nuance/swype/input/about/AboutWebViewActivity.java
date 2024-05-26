package com.nuance.swype.input.about;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import com.nuance.swype.input.R;

/* loaded from: classes.dex */
public class AboutWebViewActivity extends Activity {
    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WebView webView = new WebView(this);
        webView.setBackgroundColor(0);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setScrollBarStyle(0);
        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }
        CharSequence url = intent.getCharSequenceExtra(AboutBuilders.URL_DATA);
        if (url == null || url.length() == 0) {
            finish();
            return;
        }
        webView.loadUrl(url.toString());
        setContentView(webView);
        String urlString = url.toString();
        if (urlString.equals(getResources().getString(R.string.url_dragon_website)) || urlString.equals(getResources().getString(R.string.url_dragon_twitter)) || urlString.equals(getResources().getString(R.string.url_dragon_facebook)) || urlString.equals(getResources().getString(R.string.url_dragon_youtube))) {
            setTitle(R.string.dragon);
        }
    }
}
