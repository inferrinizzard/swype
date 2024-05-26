package com.facebook.login;

/* loaded from: classes.dex */
public enum LoginBehavior {
    NATIVE_WITH_FALLBACK(true, true, true, false, true, true),
    NATIVE_ONLY(true, true, false, false, false, true),
    KATANA_ONLY(false, true, false, false, false, false),
    WEB_ONLY(false, false, true, false, true, false),
    WEB_VIEW_ONLY(false, false, true, false, false, false),
    DEVICE_AUTH(false, false, false, true, false, false);

    private final boolean allowsCustomTabAuth;
    private final boolean allowsDeviceAuth;
    private final boolean allowsFacebookLiteAuth;
    private final boolean allowsGetTokenAuth;
    private final boolean allowsKatanaAuth;
    private final boolean allowsWebViewAuth;

    LoginBehavior(boolean allowsGetTokenAuth, boolean allowsKatanaAuth, boolean allowsWebViewAuth, boolean allowsDeviceAuth, boolean allowsCustomTabAuth, boolean allowsFacebookLiteAuth) {
        this.allowsGetTokenAuth = allowsGetTokenAuth;
        this.allowsKatanaAuth = allowsKatanaAuth;
        this.allowsWebViewAuth = allowsWebViewAuth;
        this.allowsDeviceAuth = allowsDeviceAuth;
        this.allowsCustomTabAuth = allowsCustomTabAuth;
        this.allowsFacebookLiteAuth = allowsFacebookLiteAuth;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean allowsGetTokenAuth() {
        return this.allowsGetTokenAuth;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean allowsKatanaAuth() {
        return this.allowsKatanaAuth;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean allowsWebViewAuth() {
        return this.allowsWebViewAuth;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean allowsDeviceAuth() {
        return this.allowsDeviceAuth;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean allowsCustomTabAuth() {
        return this.allowsCustomTabAuth;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean allowsFacebookLiteAuth() {
        return this.allowsFacebookLiteAuth;
    }
}
