package com.localytics.android;

import android.os.Handler;
import android.os.Looper;
import android.util.SparseArray;
import android.webkit.JavascriptInterface;
import com.localytics.android.Localytics;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class JavaScriptClient {
    private final SparseArray<MarketingCallable> mCallbacks;

    /* JADX INFO: Access modifiers changed from: package-private */
    public JavaScriptClient(SparseArray<MarketingCallable> callbacks) {
        this.mCallbacks = callbacks;
    }

    final SparseArray<MarketingCallable> getCallbacks() {
        return this.mCallbacks;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final String getJavaScriptBridge() {
        String identifers = getIdentifiers();
        return String.format("javascript:(function() {  var localyticsScript = document.createElement('script');  localyticsScript.type = 'text/javascript';  localyticsScript.text = '  localytics.identifers = %s;  localytics.identifiers = %s;  localytics.customDimensions = %s;  localytics.attributes = %s;  localytics.libraryVersion = \"%s\";  localytics.tagEvent = function(event, attributes, customerValueIncrease) {     localytics.nativeTagEvent(event, JSON.stringify(attributes), JSON.stringify(customerValueIncrease));  };  localytics.setCustomDimension = function(number, value) {     if (number != null && value != null)        localytics.nativeSetCustomDimension(number, value);  };  window.open = function(url) {     if (url != null)        localytics.navigate(url);  };  localytics.close = function() {     localytics.nativeClose();  };';  document.getElementsByTagName('body')[0].appendChild(localyticsScript);})()", identifers, identifers, getCustomDimensions(), getAttributes(), Localytics.getLibraryVersion());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final String getViewportAdjuster(float viewportWidth, float viewportHeight) {
        return String.format("javascript:(function() {  var viewportNode = document.createElement('meta');  viewportNode.name    = 'viewport';  viewportNode.content = 'width=%f, height=%f, user-scalable=no, minimum-scale=.25, maximum-scale=1';  viewportNode.id      = 'metatag';  document.getElementsByTagName('head')[0].appendChild(viewportNode);})()", Float.valueOf(viewportWidth), Float.valueOf(viewportHeight));
    }

    final String getIdentifiers() {
        return (String) invoke(4, null);
    }

    final String getCustomDimensions() {
        return (String) invoke(5, null);
    }

    final String getAttributes() {
        return (String) invoke(6, null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Object invoke(int methodId, Object[] params) {
        MarketingCallable callable;
        if (this.mCallbacks == null || (callable = this.mCallbacks.get(methodId)) == null) {
            return null;
        }
        Object result = callable.call(params);
        return result;
    }

    @JavascriptInterface
    public final void nativeTagEvent(String event, String attributes, String customerValueIncrease) {
        Localytics.Log.w("[JavaScriptClient]: nativeTagEvent is being called");
        invoke(2, new Object[]{event, attributes, customerValueIncrease});
    }

    @JavascriptInterface
    public final void nativeSetCustomDimension(long dimension, String value) {
        Localytics.Log.w("[JavaScriptClient]: nativeSetCustomDimension is being called");
        invoke(7, new Object[]{Integer.valueOf((int) dimension), value});
    }

    @JavascriptInterface
    public final void navigate(String url) {
        invoke(1, new String[]{url});
    }

    @JavascriptInterface
    public final void nativeClose() {
        Localytics.Log.w("[JavaScriptClient]: nativeClose is being called");
        new Handler(Looper.getMainLooper()).post(new Runnable() { // from class: com.localytics.android.JavaScriptClient.1
            @Override // java.lang.Runnable
            public void run() {
                JavaScriptClient.this.invoke(3, null);
            }
        });
    }
}
