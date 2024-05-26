package com.localytics.android;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.SparseArray;
import android.webkit.WebView;
import com.facebook.internal.ServerProtocol;
import com.localytics.android.Localytics;
import com.nuance.connect.comm.MessageAPI;
import com.nuance.swype.input.hardkey.HardKeyboardManager;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class MarketingWebViewManager {
    static final int MESSAGE_DISMISS = 1;
    static final int MESSAGE_LOAD_URL = 2;
    WebViewCampaign mCampaign;
    Context mContext;
    final AtomicBoolean mIsMarketingActionTagged = new AtomicBoolean(false);
    LocalyticsDao mLocalyticsDao;
    Handler mMessageHandler;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public enum ProtocolHandleAction {
        PROTOCOL_UNMATCHED,
        OPENING_INTERNAL,
        OPENING_EXTERNAL,
        DO_NOT_OPEN
    }

    public MarketingWebViewManager(LocalyticsDao localyticsDao) {
        this.mLocalyticsDao = localyticsDao;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setCampaign(WebViewCampaign campaign) {
        this.mCampaign = campaign;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setContext(Context context) {
        this.mContext = context;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setMessageHandler(Handler handler) {
        this.mMessageHandler = handler;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean handleShouldOverrideUrlLoading(String url) {
        return handleUrl(url) != ProtocolHandleAction.OPENING_INTERNAL;
    }

    public boolean shouldInterceptRequest(WebView view, String url) {
        try {
            ProtocolHandleAction actionForRequest = handleFileProtocolRequest(new URL(url));
            switch (actionForRequest) {
                case OPENING_INTERNAL:
                case PROTOCOL_UNMATCHED:
                    return false;
                default:
                    return true;
            }
        } catch (MalformedURLException e) {
            return true;
        }
    }

    ProtocolHandleAction handleUrl(String urlString) {
        URL url;
        ProtocolHandleAction result;
        Localytics.Log.w(String.format("[Marketing Nav Handler]: Evaluating marketing URL:\n\tURL:%s", urlString));
        URI uri = null;
        ProtocolHandleAction result2 = ProtocolHandleAction.PROTOCOL_UNMATCHED;
        try {
            try {
                URI uri2 = new URI(urlString);
                try {
                    if (TextUtils.isEmpty(uri2.getScheme())) {
                        String basePath = this.mCampaign.getWebViewAttributes().get("base_path");
                        if (!TextUtils.isEmpty(basePath)) {
                            urlString = "file://" + basePath + '/' + urlString;
                        }
                        uri = new URI(urlString);
                    } else {
                        uri = uri2;
                    }
                    tagMarketingActionForURL(uri);
                    url = new URL(urlString);
                    result = handleFileProtocolRequest(url);
                } catch (Exception e) {
                    uri = uri2;
                    result2 = handleCustomProtocolRequest(urlString);
                    if (result2 != ProtocolHandleAction.PROTOCOL_UNMATCHED) {
                        if (result2 == ProtocolHandleAction.OPENING_INTERNAL && uri != null && uri.getScheme().equals("file")) {
                            this.mMessageHandler.obtainMessage(2, uri.toString()).sendToTarget();
                        }
                        return result2;
                    }
                    if (result2 == ProtocolHandleAction.OPENING_INTERNAL && uri != null && uri.getScheme().equals("file")) {
                        this.mMessageHandler.obtainMessage(2, uri.toString()).sendToTarget();
                    }
                    return result2;
                } catch (Throwable th) {
                    th = th;
                    uri = uri2;
                    if (result2 == ProtocolHandleAction.OPENING_INTERNAL && uri != null && uri.getScheme().equals("file")) {
                        this.mMessageHandler.obtainMessage(2, uri.toString()).sendToTarget();
                    }
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (Exception e2) {
        }
        if (result != ProtocolHandleAction.PROTOCOL_UNMATCHED) {
            if (result == ProtocolHandleAction.OPENING_INTERNAL && uri.getScheme().equals("file")) {
                this.mMessageHandler.obtainMessage(2, uri.toString()).sendToTarget();
            }
            return result;
        }
        ProtocolHandleAction result3 = handleHttpProtocolRequest(url);
        if (result3 != ProtocolHandleAction.PROTOCOL_UNMATCHED) {
            if (result3 == ProtocolHandleAction.OPENING_INTERNAL && uri.getScheme().equals("file")) {
                this.mMessageHandler.obtainMessage(2, uri.toString()).sendToTarget();
            }
            return result3;
        }
        result2 = handleCustomProtocolRequest(url);
        if (result2 != ProtocolHandleAction.PROTOCOL_UNMATCHED) {
            if (result2 == ProtocolHandleAction.OPENING_INTERNAL && uri.getScheme().equals("file")) {
                this.mMessageHandler.obtainMessage(2, uri.toString()).sendToTarget();
            }
            return result2;
        }
        Localytics.Log.w(String.format("[Marketing Nav Handler]: Protocol handler scheme not recognized. Attempting to load the URL... [Scheme: %s]", url.getProtocol()));
        if (result2 == ProtocolHandleAction.OPENING_INTERNAL && uri.getScheme().equals("file")) {
            this.mMessageHandler.obtainMessage(2, uri.toString()).sendToTarget();
        }
        return result2;
    }

    private void tagMarketingActionForURL(URI uri) {
        String marketingActionValue = getValueByQueryKey("ampAction", uri);
        if (!TextUtils.isEmpty(marketingActionValue)) {
            Localytics.Log.w(String.format("Attempting to tag event with custom marketing action. [Action: %s]", marketingActionValue));
            tagMarketingActionEventWithAction(marketingActionValue);
            return;
        }
        String protocol = uri.getScheme();
        if (!TextUtils.isEmpty(protocol) && !protocol.equals("file") && !protocol.equals("http") && !protocol.equals("https")) {
            tagMarketingActionEventWithAction("click");
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void tagMarketingActionEventWithAction(String action) {
        if (!Constants.isTestModeEnabled() && this.mCampaign != null) {
            if (this.mIsMarketingActionTagged.getAndSet(true)) {
                if (this.mCampaign instanceof InAppCampaign) {
                    Localytics.Log.w(String.format("The in-app action for this message has already been set. Ignoring in-app Action: [%s]", action));
                    return;
                } else {
                    if (this.mCampaign instanceof InboxCampaign) {
                        Localytics.Log.w(String.format("The inbox action for this message has already been set. Ignoring inbox Action: [%s]", action));
                        return;
                    }
                    return;
                }
            }
            TreeMap<String, String> attributes = new TreeMap<>();
            attributes.put("Schema Version - Client", MessageAPI.DEVICE_ID);
            if (this.mCampaign.getSchemaVersion() != 0) {
                attributes.put("Schema Version - Server", Long.toString(this.mCampaign.getSchemaVersion()));
            }
            if (this.mCampaign instanceof InAppCampaign) {
                attributes.put("ampAction", action);
                attributes.put("type", "In-App");
                attributes.put("ampCampaignId", Long.toString(this.mCampaign.getCampaignId()));
                attributes.put("ampCampaign", this.mCampaign.getRuleName());
                if (!TextUtils.isEmpty(this.mCampaign.getAbTest())) {
                    attributes.put("ampAB", this.mCampaign.getAbTest());
                }
                this.mLocalyticsDao.tagEvent("ampView", attributes);
            } else if (this.mCampaign instanceof InboxCampaign) {
                attributes.put("Action", action);
                attributes.put("Type", "Inbox");
                attributes.put("Campaign ID", Long.toString(this.mCampaign.getCampaignId()));
                if (!TextUtils.isEmpty(this.mCampaign.getAbTest())) {
                    attributes.put("Creative ID", this.mCampaign.getAbTest());
                }
                this.mLocalyticsDao.tagEvent("Localytics Inbox Message Viewed", attributes);
            }
            if (Localytics.isLoggingEnabled()) {
                StringBuilder builder = new StringBuilder();
                for (Map.Entry<String, String> entry : attributes.entrySet()) {
                    builder.append(" Key = ").append(entry.getKey()).append(", Value = ").append(entry.getValue());
                }
                Localytics.Log.v(String.format("Marketing event tagged successfully.\n   Attributes Dictionary = \n%s", builder.toString()));
            }
        }
    }

    private ProtocolHandleAction handleFileProtocolRequest(URL url) {
        if (!url.getProtocol().equals("file")) {
            return ProtocolHandleAction.PROTOCOL_UNMATCHED;
        }
        if ((TextUtils.isEmpty(url.getHost()) || url.getHost().equals("localhost")) && isPathWithinCreativeDir(url.getPath())) {
            return ProtocolHandleAction.OPENING_INTERNAL;
        }
        Localytics.Log.w("[Marketing Nav Handler]: Displaying content from your local creatives.");
        return ProtocolHandleAction.DO_NOT_OPEN;
    }

    private boolean isPathWithinCreativeDir(String urlString) {
        File file;
        File allowableFileDir;
        if (!TextUtils.isEmpty(urlString)) {
            File file2 = new File(urlString);
            try {
                file = file2.getCanonicalFile();
            } catch (IOException e) {
                file = file2.getAbsoluteFile();
            }
            File dirFile = new File(this.mCampaign.getWebViewAttributes().get("base_path"));
            try {
                allowableFileDir = dirFile.getCanonicalFile();
            } catch (IOException e2) {
                allowableFileDir = dirFile.getAbsoluteFile();
            }
            while (file != null && file.exists()) {
                if (file.equals(allowableFileDir)) {
                    return true;
                }
                file = file.getParentFile();
            }
        }
        return false;
    }

    private ProtocolHandleAction handleHttpProtocolRequest(URL url) {
        String protocol = url.getProtocol();
        if (!protocol.equals("http") && !protocol.equals("https")) {
            return ProtocolHandleAction.PROTOCOL_UNMATCHED;
        }
        Localytics.Log.w("[Marketing Nav Handler]: Handling a request for an external HTTP address.");
        String openExternalValue = getValueByQueryKey("ampExternalOpen", url);
        if (!TextUtils.isEmpty(openExternalValue) && openExternalValue.toLowerCase(Locale.US).equals(ServerProtocol.DIALOG_RETURN_SCOPES_TRUE)) {
            Localytics.Log.w(String.format("[Marketing Nav Handler]: Query string hook [%s] set to true. Opening the URL in chrome", "ampExternalOpen"));
            Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(url.toString()));
            if (this.mLocalyticsDao.getAppContext().getPackageManager().queryIntentActivities(intent, 0).size() > 0) {
                if (this.mContext != null) {
                    if (this.mCampaign instanceof InAppCampaign) {
                        this.mMessageHandler.obtainMessage(1).sendToTarget();
                    }
                    this.mContext.startActivity(intent);
                }
                return ProtocolHandleAction.OPENING_EXTERNAL;
            }
        }
        Localytics.Log.w("[Marketing Nav Handler]: Loading HTTP request inside the current marketing view");
        return ProtocolHandleAction.OPENING_INTERNAL;
    }

    private ProtocolHandleAction handleCustomProtocolRequest(URL url) {
        return handleCustomProtocolRequest(url.toString());
    }

    private ProtocolHandleAction handleCustomProtocolRequest(String urlString) {
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(urlString));
        if (this.mLocalyticsDao.getAppContext().getPackageManager().queryIntentActivities(intent, 0).size() > 0) {
            Localytics.Log.w("[Marketing Nav Handler]: An app on this device is registered to handle this protocol scheme. Opening...");
            intent.setFlags(HardKeyboardManager.META_META_LEFT_ON);
            if (this.mContext != null) {
                if (this.mCampaign instanceof InAppCampaign) {
                    this.mMessageHandler.obtainMessage(1).sendToTarget();
                }
                this.mContext.startActivity(intent);
            }
            return ProtocolHandleAction.OPENING_EXTERNAL;
        }
        Localytics.Log.w(String.format("[Marketing Nav Handler]: Invalid url %s", urlString));
        this.mMessageHandler.obtainMessage(1).sendToTarget();
        return ProtocolHandleAction.PROTOCOL_UNMATCHED;
    }

    private String getValueByQueryKey(String queryKey, URI url) {
        String query = url.getQuery();
        if (TextUtils.isEmpty(queryKey) || TextUtils.isEmpty(query)) {
            return null;
        }
        String[] arr$ = url.getQuery().split("[&]");
        for (String str : arr$) {
            String[] components = str.split("[=]");
            if (components[0].compareTo(queryKey) == 0 && 2 == components.length) {
                try {
                    return URLDecoder.decode(components[1], "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    return null;
                }
            }
        }
        return null;
    }

    private String getValueByQueryKey(String queryKey, URL url) {
        try {
            return getValueByQueryKey(queryKey, url.toURI());
        } catch (URISyntaxException e) {
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public JavaScriptClient getJavaScriptClient(Map attributes) {
        SparseArray<MarketingCallable> callbacks = getJavaScriptCallbacks(attributes);
        return new JavaScriptClient(callbacks);
    }

    private SparseArray<MarketingCallable> getJavaScriptCallbacks(final Map<String, String> eventAttributes) {
        final List JS_STRINGS_THAT_MEAN_NULL = Arrays.asList("undefined", "null", "nil", "\"\"", "''");
        SparseArray callbacks = new SparseArray();
        callbacks.put(2, new MarketingCallable() { // from class: com.localytics.android.MarketingWebViewManager.1
            /* JADX INFO: Access modifiers changed from: package-private */
            @Override // com.localytics.android.MarketingCallable
            public Object call(Object[] params) {
                try {
                    String event = (String) params[0];
                    String attributes = (String) params[1];
                    String thirdParam = (String) params[2];
                    long customerValueIncrease = 0;
                    Map<String, String> nativeAttributes = new HashMap<>();
                    if (TextUtils.isEmpty(event)) {
                        Localytics.Log.e("event cannot be null or empty");
                    }
                    if (!TextUtils.isEmpty(attributes) && !JS_STRINGS_THAT_MEAN_NULL.contains(attributes)) {
                        for (Map.Entry<String, Object> entry : JsonHelper.toMap(new JSONObject(attributes)).entrySet()) {
                            nativeAttributes.put(entry.getKey(), String.valueOf(entry.getValue()));
                        }
                    }
                    try {
                        customerValueIncrease = Long.valueOf(thirdParam).longValue();
                    } catch (NumberFormatException e) {
                        try {
                            if (!TextUtils.isEmpty(thirdParam) && !JS_STRINGS_THAT_MEAN_NULL.contains(thirdParam)) {
                                for (Map.Entry<String, Object> entry2 : JsonHelper.toMap(new JSONObject(thirdParam)).entrySet()) {
                                    nativeAttributes.put(entry2.getKey(), String.valueOf(entry2.getValue()));
                                }
                            }
                        } catch (JSONException e2) {
                        }
                    }
                    if (attributes != null) {
                        if (nativeAttributes.isEmpty()) {
                            Localytics.Log.w("attributes is empty.  Did the caller make an error?");
                        }
                        if (nativeAttributes.size() > 50) {
                            Localytics.Log.w(String.format("attributes size is %d, exceeding the maximum size of %d.  Did the caller make an error?", Integer.valueOf(nativeAttributes.size()), 50));
                        }
                        for (Map.Entry<String, String> entry3 : nativeAttributes.entrySet()) {
                            String key = entry3.getKey();
                            String value = String.valueOf(entry3.getValue());
                            if (TextUtils.isEmpty(key)) {
                                Localytics.Log.e("attributes cannot contain null or empty keys");
                            }
                            if (TextUtils.isEmpty(value)) {
                                Localytics.Log.e("attributes cannot contain null or empty values");
                            }
                        }
                    }
                    MarketingWebViewManager.this.mLocalyticsDao.tagEvent(event, nativeAttributes, customerValueIncrease);
                    return null;
                } catch (Exception e3) {
                    Localytics.Log.e("MarketingCallable ON_MARKETING_JS_TAG_EVENT exception", e3);
                    return null;
                }
            }
        });
        callbacks.put(4, new MarketingCallable() { // from class: com.localytics.android.MarketingWebViewManager.2
            /* JADX INFO: Access modifiers changed from: package-private */
            @Override // com.localytics.android.MarketingCallable
            public Object call(Object[] params) {
                try {
                    Map<String, String> customerIDs = MarketingWebViewManager.this.mLocalyticsDao.getCachedIdentifiers();
                    if (!customerIDs.isEmpty()) {
                        JSONObject jsonIdentifiers = new JSONObject();
                        for (Map.Entry<String, String> pair : customerIDs.entrySet()) {
                            jsonIdentifiers.put(pair.getKey(), pair.getValue());
                        }
                        return jsonIdentifiers.toString();
                    }
                } catch (Exception e) {
                    Localytics.Log.e("MarketingCallable ON_MARKETING_JS_GET_IDENTIFIERS exception", e);
                }
                return null;
            }
        });
        callbacks.put(5, new MarketingCallable() { // from class: com.localytics.android.MarketingWebViewManager.3
            /* JADX INFO: Access modifiers changed from: package-private */
            @Override // com.localytics.android.MarketingCallable
            public Object call(Object[] params) {
                JSONObject jsonCustomDimensions = new JSONObject();
                try {
                    Map<Integer, String> customDimensions = MarketingWebViewManager.this.mLocalyticsDao.getCachedCustomDimensions();
                    for (int i = 0; i < 20; i++) {
                        try {
                            jsonCustomDimensions.put("c" + i, customDimensions.get(Integer.valueOf(i)));
                        } catch (JSONException e) {
                            Localytics.Log.w("[JavaScriptClient]: Failed to get custom dimension");
                        }
                    }
                } catch (Exception e2) {
                    Localytics.Log.e("MarketingCallable ON_MARKETING_JS_GET_CUSTOM_DIMENSIONS exception", e2);
                }
                return jsonCustomDimensions.toString();
            }
        });
        callbacks.put(6, new MarketingCallable() { // from class: com.localytics.android.MarketingWebViewManager.4
            /* JADX INFO: Access modifiers changed from: package-private */
            @Override // com.localytics.android.MarketingCallable
            public Object call(Object[] params) {
                try {
                    if (eventAttributes == null || eventAttributes.size() == 0) {
                        return null;
                    }
                    JSONObject jsonAttributes = new JSONObject();
                    for (Map.Entry<String, String> entry : eventAttributes.entrySet()) {
                        String key = entry.getKey();
                        String value = entry.getValue();
                        jsonAttributes.put(key, value);
                    }
                    return jsonAttributes.toString();
                } catch (Exception e) {
                    Localytics.Log.e("MarketingCallable ON_MARKETING_JS_GET_ATTRIBUTES exception", e);
                    return null;
                }
            }
        });
        callbacks.put(7, new MarketingCallable() { // from class: com.localytics.android.MarketingWebViewManager.5
            /* JADX INFO: Access modifiers changed from: package-private */
            @Override // com.localytics.android.MarketingCallable
            public Object call(Object[] params) {
                try {
                    int dimension = ((Integer) params[0]).intValue();
                    String value = (String) params[1];
                    Localytics.setCustomDimension(dimension, value);
                    return null;
                } catch (Exception e) {
                    Localytics.Log.e("MarketingCallable ON_MARKETING_JS_SET_CUSTOM_DIMENSIONS exception", e);
                    return null;
                }
            }
        });
        callbacks.put(1, new MarketingCallable() { // from class: com.localytics.android.MarketingWebViewManager.6
            /* JADX INFO: Access modifiers changed from: package-private */
            @Override // com.localytics.android.MarketingCallable
            public Object call(Object[] params) {
                try {
                    MarketingWebViewManager.this.handleUrl((String) params[0]);
                    return null;
                } catch (Exception e) {
                    Localytics.Log.e("MarketingCallable ON_MARKETING_JS_NAVIGATE exception", e);
                    return null;
                }
            }
        });
        callbacks.put(3, new MarketingCallable() { // from class: com.localytics.android.MarketingWebViewManager.7
            /* JADX INFO: Access modifiers changed from: package-private */
            @Override // com.localytics.android.MarketingCallable
            public Object call(Object[] params) {
                try {
                    Message.obtain(MarketingWebViewManager.this.mMessageHandler, 1).sendToTarget();
                    return null;
                } catch (Exception e) {
                    Localytics.Log.e("MarketingCallable ON_MARKETING_JS_CLOSE_WINDOW exception", e);
                    return null;
                }
            }
        });
        return callbacks;
    }
}
