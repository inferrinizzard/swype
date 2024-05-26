package com.localytics.android;

import java.util.Map;

/* loaded from: classes.dex */
abstract class WebViewCampaign extends Campaign {
    protected Map<String, String> mWebViewAttributes;

    /* JADX INFO: Access modifiers changed from: protected */
    public Map<String, String> getWebViewAttributes() {
        return this.mWebViewAttributes;
    }
}
