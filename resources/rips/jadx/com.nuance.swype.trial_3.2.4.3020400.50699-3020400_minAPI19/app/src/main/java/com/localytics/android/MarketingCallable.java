package com.localytics.android;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public abstract class MarketingCallable {
    static final int ON_IN_APP_TEST_CLOSE_CAMPAIGN_LIST = 9;
    static final int ON_IN_APP_TEST_COPY_INSTALL_ID = 13;
    static final int ON_IN_APP_TEST_COPY_PUSH_TOKEN = 12;
    static final int ON_IN_APP_TEST_POPUP_CAMPAIGN_LIST = 8;
    static final int ON_IN_APP_TEST_REFRESH_CAMPAIGN_LIST = 11;
    static final int ON_IN_APP_TEST_SHOW_CAMPAIGN = 10;
    static final int ON_MARKETING_JS_CLOSE_WINDOW = 3;
    static final int ON_MARKETING_JS_GET_ATTRIBUTES = 6;
    static final int ON_MARKETING_JS_GET_CUSTOM_DIMENSIONS = 5;
    static final int ON_MARKETING_JS_GET_IDENTIFIERS = 4;
    static final int ON_MARKETING_JS_NAVIGATE = 1;
    static final int ON_MARKETING_JS_SET_CUSTOM_DIMENSIONS = 7;
    static final int ON_MARKETING_JS_TAG_EVENT = 2;

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract Object call(Object[] objArr);
}
