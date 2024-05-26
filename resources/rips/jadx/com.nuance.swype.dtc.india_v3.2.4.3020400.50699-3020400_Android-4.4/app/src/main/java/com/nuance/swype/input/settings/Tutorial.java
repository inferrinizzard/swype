package com.nuance.swype.input.settings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.R;

/* loaded from: classes.dex */
public class Tutorial {
    public static final String EXTRA_HELP_URL = "help_url";
    private String loadedUrl;
    private final View webView;

    @SuppressLint({"SetJavaScriptEnabled"})
    public Tutorial(Context context) {
        this.webView = LayoutInflater.from(context).inflate(R.layout.tutorial_webview, (ViewGroup) null, false);
        ((WebView) this.webView.findViewById(R.id.webview)).getSettings().setJavaScriptEnabled(true);
        if (IMEApplication.from(context).isScreenLayoutTablet()) {
            if (this.webView.getPaddingLeft() <= 16 || this.webView.getPaddingStart() <= 16) {
                int padding = (int) (context.getResources().getDisplayMetrics().density * 32.0f);
                this.webView.setPaddingRelative(padding, this.webView.getTop(), padding, this.webView.getBottom());
            }
        }
    }

    public View getView() {
        return this.webView;
    }

    public void load(String url) {
        ((WebView) this.webView.findViewById(R.id.webview)).loadUrl(url);
        this.loadedUrl = url;
    }

    public String getUrl() {
        return ((WebView) this.webView.findViewById(R.id.webview)).getUrl();
    }

    public String getLoadedUrl() {
        return this.loadedUrl;
    }
}
