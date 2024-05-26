package com.localytics.android;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import com.facebook.internal.AnalyticsEvents;
import com.facebook.internal.ServerProtocol;
import com.google.android.gms.iid.InstanceID;
import com.localytics.android.AnalyticsProvider;
import com.localytics.android.DatapointHelper;
import com.localytics.android.Localytics;
import com.localytics.android.Region;
import java.io.IOException;
import java.net.URLDecoder;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class AnalyticsHandler extends BaseHandler implements LocationListener {
    private static final long BASE_PUSH_REGISTRATION_BACKOFF = 5000;
    private static final int MAX_PUSH_REGISTRATION_RETRIES = 3;
    private static final int MESSAGE_CLOSE = 102;
    private static final int MESSAGE_DISABLE_NOTIFICATIONS = 110;
    private static final int MESSAGE_OPEN = 101;
    private static final int MESSAGE_OPT_OUT = 108;
    private static final int MESSAGE_REGISTER_PUSH = 109;
    private static final int MESSAGE_RETRIEVE_TOKEN_FROM_INSTANCEID = 113;
    private static final int MESSAGE_SET_CUSTOM_DIMENSION = 107;
    private static final int MESSAGE_SET_IDENTIFIER = 105;
    private static final int MESSAGE_SET_LOCATION = 106;
    private static final int MESSAGE_SET_PUSH_REGID = 111;
    private static final int MESSAGE_SET_REFERRERID = 112;
    private static final int MESSAGE_TAG_EVENT = 103;
    private static final int MESSAGE_TAG_SCREEN = 104;
    private static final String PARAM_LOCALYTICS_REFERRER_TEST_MODE = "localytics_test_mode";
    private boolean mAppWasUpgraded;
    private Map<Integer, String> mCachedCustomDimensions;
    private Map<String, String> mCachedIdentifiers;
    boolean mFirstSessionEver;
    private String mInstallId;
    boolean mIsSessionOpen;
    private String mLastScreenTag;
    protected final ListenersSet<AnalyticsListener> mListeners;
    boolean mReferrerTestModeEnabled;
    private HashMap<String, String> mSanitizingDictionary;
    boolean mSentReferrerTestMode;
    private static final String[] PROJECTION_SET_CUSTOM_DIMENSION = {"custom_dimension_value"};
    private static final String SELECTION_SET_CUSTOM_DIMENSION = String.format("%s = ?", "custom_dimension_key");
    private static final String[] PROJECTION_SET_IDENTIFIER = {"key", "value"};
    private static final String SELECTION_SET_IDENTIFIER = String.format("%s = ?", "key");
    private static Location sLastLocation = null;
    private static int sNumPushRegistrationRetries = 0;

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean areNotificationsDisabled() {
        return getBool(new Callable<Boolean>() { // from class: com.localytics.android.AnalyticsHandler.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // java.util.concurrent.Callable
            public Boolean call() {
                return Boolean.valueOf(AnalyticsHandler.this._areNotificationsDisabled());
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AnalyticsHandler(LocalyticsDao localyticsDao, Looper looper) {
        super(localyticsDao, looper);
        this.mAppWasUpgraded = false;
        this.mFirstSessionEver = false;
        this.mReferrerTestModeEnabled = false;
        this.mSentReferrerTestMode = false;
        this.mIsSessionOpen = false;
        this.siloName = "Analytics";
        this.mListeners = new ListenersSet<>(AnalyticsListener.class);
        queueMessage(obtainMessage(1));
        this.mSanitizingDictionary = new HashMap<>();
        this.mSanitizingDictionary.put("facebook", "Facebook");
        this.mSanitizingDictionary.put("twitter", "Twitter");
        this.mSanitizingDictionary.put(AnalyticsEvents.PARAMETER_SHARE_DIALOG_SHOW_NATIVE, "Native");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setDeveloperListener(AnalyticsListener listener) {
        this.mListeners.setDevListener(listener);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addListener(AnalyticsListener listener) {
        this.mListeners.add(listener);
    }

    Map<String, String> _getIdentifiers() {
        Cursor cursor = null;
        Map<String, String> customerIDs = new HashMap<>();
        try {
            cursor = this.mProvider.query("identifiers", null, null, null, null);
            while (cursor.moveToNext()) {
                String key = cursor.getString(cursor.getColumnIndexOrThrow("key"));
                String value = cursor.getString(cursor.getColumnIndexOrThrow("value"));
                customerIDs.put(key, value);
            }
            if (cursor != null) {
                cursor.close();
            }
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.close();
            }
        }
        return customerIDs;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void tagEvent(String eventName, Map<String, String> attributes, long customerValueIncrease) {
        if (TextUtils.isEmpty(eventName)) {
            throw new IllegalArgumentException("event cannot be null or empty");
        }
        if (attributes != null) {
            if (attributes.isEmpty()) {
                Localytics.Log.e("attributes is empty.  Did the caller make an error?");
            }
            if (attributes.size() > 50) {
                Localytics.Log.e(String.format("attributes size is %d, exceeding the maximum size of %d.  Did the caller make an error?", Integer.valueOf(attributes.size()), 50));
            }
            for (Map.Entry<String, String> entry : attributes.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (TextUtils.isEmpty(key)) {
                    Localytics.Log.e("attributes cannot contain null or empty keys");
                }
                if (TextUtils.isEmpty(value)) {
                    Localytics.Log.e("attributes cannot contain null or empty values");
                }
            }
        }
        queueMessage(obtainMessage(103, new Object[]{eventName, attributes, Long.valueOf(customerValueIncrease)}));
    }

    public void tagPurchased(String itemName, String itemId, String itemType, Long itemPrice, Map<String, String> attributes) {
        Map<String, String> attributesCopy = new HashMap<>();
        if (attributes != null) {
            attributesCopy.putAll(attributes);
        }
        if (itemName != null) {
            attributesCopy.put("Item Name", itemName);
        }
        if (itemId != null) {
            attributesCopy.put("Item ID", itemId);
        }
        if (itemType != null) {
            attributesCopy.put("Item Type", itemType);
        }
        if (itemPrice != null) {
            attributesCopy.put("Item Price", itemPrice.toString());
        }
        long clv = itemPrice != null ? itemPrice.longValue() : 0L;
        if (Constants.IGNORE_STANDARD_EVENT_CLV.booleanValue()) {
            clv = 0;
        }
        tagEvent("Localytics Purchased", attributesCopy, clv);
    }

    public void tagAddedToCart(String itemName, String itemId, String itemType, Long itemPrice, Map<String, String> attributes) {
        Map<String, String> attributesCopy = new HashMap<>();
        if (attributes != null) {
            attributesCopy.putAll(attributes);
        }
        if (itemName != null) {
            attributesCopy.put("Item Name", itemName);
        }
        if (itemId != null) {
            attributesCopy.put("Item ID", itemId);
        }
        if (itemType != null) {
            attributesCopy.put("Item Type", itemType);
        }
        if (itemPrice != null) {
            attributesCopy.put("Item Price", itemPrice.toString());
        }
        tagEvent("Localytics Added To Cart", attributesCopy, 0L);
    }

    public void tagStartedCheckout(Long totalPrice, Long itemCount, Map<String, String> attributes) {
        Map<String, String> attributesCopy = new HashMap<>();
        if (attributes != null) {
            attributesCopy.putAll(attributes);
        }
        if (totalPrice != null) {
            attributesCopy.put("Total Price", totalPrice.toString());
        }
        if (itemCount != null) {
            attributesCopy.put("Item Count", itemCount.toString());
        }
        tagEvent("Localytics Started Checkout", attributesCopy, 0L);
    }

    public void tagCompletedCheckout(Long totalPrice, Long itemCount, Map<String, String> attributes) {
        Map<String, String> attributesCopy = new HashMap<>();
        if (attributes != null) {
            attributesCopy.putAll(attributes);
        }
        if (totalPrice != null) {
            attributesCopy.put("Total Price", totalPrice.toString());
        }
        if (itemCount != null) {
            attributesCopy.put("Item Count", itemCount.toString());
        }
        long clv = totalPrice != null ? totalPrice.longValue() : 0L;
        if (Constants.IGNORE_STANDARD_EVENT_CLV.booleanValue()) {
            clv = 0;
        }
        tagEvent("Localytics Completed Checkout", attributesCopy, clv);
    }

    public void tagContentViewed(String contentName, String contentId, String contentType, Map<String, String> attributes) {
        Map<String, String> attributesCopy = new HashMap<>();
        if (attributes != null) {
            attributesCopy.putAll(attributes);
        }
        if (contentName != null) {
            attributesCopy.put("Content Name", contentName);
        }
        if (contentId != null) {
            attributesCopy.put("Content ID", contentId);
        }
        if (contentType != null) {
            attributesCopy.put("Content Type", contentType);
        }
        tagEvent("Localytics Content Viewed", attributesCopy, 0L);
    }

    public void tagSearched(String queryText, String contentType, Long resultCount, Map<String, String> attributes) {
        Map<String, String> attributesCopy = new HashMap<>();
        if (attributes != null) {
            attributesCopy.putAll(attributes);
        }
        if (queryText != null) {
            attributesCopy.put("Search Query", queryText);
        }
        if (contentType != null) {
            attributesCopy.put("Content Type", contentType);
        }
        if (resultCount != null) {
            attributesCopy.put("Search Result Count", resultCount.toString());
        }
        tagEvent("Localytics Searched", attributesCopy, 0L);
    }

    public void tagShared(String contentName, String contentId, String contentType, String methodName, Map<String, String> attributes) {
        Map<String, String> attributesCopy = new HashMap<>();
        if (attributes != null) {
            attributesCopy.putAll(attributes);
        }
        if (contentName != null) {
            attributesCopy.put("Content Name", contentName);
        }
        if (contentId != null) {
            attributesCopy.put("Content ID", contentId);
        }
        if (contentType != null) {
            attributesCopy.put("Content Type", contentType);
        }
        if (methodName != null) {
            attributesCopy.put("Method Name", methodName);
        }
        tagEvent("Localytics Shared", attributesCopy, 0L);
    }

    public void tagContentRated(String contentName, String contentId, String contentType, Long rating, Map<String, String> attributes) {
        Map<String, String> attributesCopy = new HashMap<>();
        if (attributes != null) {
            attributesCopy.putAll(attributes);
        }
        if (contentName != null) {
            attributesCopy.put("Content Name", contentName);
        }
        if (contentId != null) {
            attributesCopy.put("Content ID", contentId);
        }
        if (contentType != null) {
            attributesCopy.put("Content Type", contentType);
        }
        if (rating != null) {
            attributesCopy.put("Content Rating", rating.toString());
        }
        tagEvent("Localytics Content Rated", attributesCopy, 0L);
    }

    public void tagRegistered(String methodName, Map<String, String> attributes) {
        Map<String, String> attributesCopy = new HashMap<>();
        if (attributes != null) {
            attributesCopy.putAll(attributes);
        }
        if (methodName != null) {
            String sanitizedMethodString = sanitizeMethodString(methodName);
            attributesCopy.put("Method Name", sanitizedMethodString);
        }
        tagEvent("Localytics Registered", attributesCopy, 0L);
    }

    public void tagLoggedIn(String methodName, Map<String, String> attributes) {
        Map<String, String> attributesCopy = new HashMap<>();
        if (attributes != null) {
            attributesCopy.putAll(attributes);
        }
        if (methodName != null) {
            String sanitizedMethodString = sanitizeMethodString(methodName);
            attributesCopy.put("Method Name", sanitizedMethodString);
        }
        tagEvent("Localytics Logged In", attributesCopy, 0L);
    }

    public void tagLoggedOut(Map<String, String> attributes) {
        tagEvent("Localytics Logged Out", attributes, 0L);
    }

    public void tagInvited(String methodName, Map<String, String> attributes) {
        Map<String, String> attributesCopy = new HashMap<>();
        if (attributes != null) {
            attributesCopy.putAll(attributes);
        }
        if (methodName != null) {
            String sanitizedMethodString = sanitizeMethodString(methodName);
            attributesCopy.put("Method Name", sanitizedMethodString);
        }
        tagEvent("Localytics Invited", attributesCopy, 0L);
    }

    private String sanitizeMethodString(String method) {
        String normalizedMethodString = method.toLowerCase();
        String sanitizedMethodString = this.mSanitizingDictionary.get(normalizedMethodString);
        return sanitizedMethodString != null ? sanitizedMethodString : method;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.localytics.android.BaseHandler
    public void handleMessageExtended(Message msg) throws Exception {
        switch (msg.what) {
            case 101:
                Localytics.Log.d("Analytics handler received MESSAGE_OPEN");
                _runBatchTransactionOnProvider(new Runnable() { // from class: com.localytics.android.AnalyticsHandler.2
                    @Override // java.lang.Runnable
                    public void run() {
                        if (AnalyticsHandler.this._isOptedOut()) {
                            Localytics.Log.d("Data collection is opted out");
                        } else {
                            AnalyticsHandler.this._open();
                        }
                    }
                });
                return;
            case 102:
                Localytics.Log.d("Analytics handler received MESSAGE_CLOSE");
                _runBatchTransactionOnProvider(new Runnable() { // from class: com.localytics.android.AnalyticsHandler.3
                    @Override // java.lang.Runnable
                    public void run() {
                        if (AnalyticsHandler.this._isOptedOut()) {
                            Localytics.Log.d("Data collection is opted out");
                        } else {
                            AnalyticsHandler.this._close();
                        }
                    }
                });
                return;
            case 103:
                Localytics.Log.d("Analytics handler received MESSAGE_TAG_EVENT");
                Object[] params = (Object[]) msg.obj;
                final String event = (String) params[0];
                final Map<String, String> attributes = (Map) params[1];
                final Long clv = (Long) params[2];
                _runBatchTransactionOnProvider(new Runnable() { // from class: com.localytics.android.AnalyticsHandler.4
                    @Override // java.lang.Runnable
                    public void run() {
                        if (!AnalyticsHandler.this._isOptedOut()) {
                            AnalyticsHandler.this._tagEvent(event, attributes, clv);
                        } else {
                            Localytics.Log.d("Data collection is opted out");
                        }
                    }
                });
                return;
            case 104:
                Localytics.Log.d("Analytics handler received MESSAGE_TAG_SCREEN");
                final String screen = (String) msg.obj;
                _runBatchTransactionOnProvider(new Runnable() { // from class: com.localytics.android.AnalyticsHandler.5
                    @Override // java.lang.Runnable
                    public void run() {
                        if (!AnalyticsHandler.this._isOptedOut()) {
                            AnalyticsHandler.this._tagScreen(screen);
                        } else {
                            Localytics.Log.d("Data collection is opted out");
                        }
                    }
                });
                return;
            case 105:
                Localytics.Log.d("Analytics handler received MESSAGE_SET_IDENTIFIER");
                Object[] params2 = (Object[]) msg.obj;
                final String key = (String) params2[0];
                final String value = (String) params2[1];
                _runBatchTransactionOnProvider(new Runnable() { // from class: com.localytics.android.AnalyticsHandler.7
                    @Override // java.lang.Runnable
                    public void run() {
                        AnalyticsHandler.this._setIdentifier(key, value);
                    }
                });
                return;
            case 106:
                Localytics.Log.d("Analytics handler received MESSAGE_SET_LOCATION");
                sLastLocation = (Location) msg.obj;
                return;
            case 107:
                Localytics.Log.d("Analytics handler received MESSAGE_SET_CUSTOM_DIMENSION");
                Object[] params3 = (Object[]) msg.obj;
                final int dimension = ((Integer) params3[0]).intValue();
                final String value2 = (String) params3[1];
                _runBatchTransactionOnProvider(new Runnable() { // from class: com.localytics.android.AnalyticsHandler.6
                    @Override // java.lang.Runnable
                    public void run() {
                        AnalyticsHandler.this._setCustomDimension(dimension, value2);
                    }
                });
                return;
            case 108:
                Localytics.Log.v("Analytics handler received MESSAGE_OPT_OUT");
                final boolean isOptingOut = msg.arg1 != 0;
                _runBatchTransactionOnProvider(new Runnable() { // from class: com.localytics.android.AnalyticsHandler.10
                    @Override // java.lang.Runnable
                    public void run() {
                        AnalyticsHandler.this._setOptedOut(isOptingOut);
                    }
                });
                return;
            case 109:
                Localytics.Log.d("Analytics handler received MESSAGE_REGISTER_PUSH");
                final String newSenderId = (String) msg.obj;
                _runBatchTransactionOnProvider(new Runnable() { // from class: com.localytics.android.AnalyticsHandler.11
                    @Override // java.lang.Runnable
                    public void run() {
                        AnalyticsHandler.this._registerPush(newSenderId);
                    }
                });
                return;
            case 110:
                Localytics.Log.d("Analytics handler received MESSAGE_DISABLE_NOTIFICATIONS");
                final int disabled = msg.arg1;
                _runBatchTransactionOnProvider(new Runnable() { // from class: com.localytics.android.AnalyticsHandler.9
                    @Override // java.lang.Runnable
                    public void run() {
                        AnalyticsHandler.this._setNotificationsDisabled(disabled);
                    }
                });
                return;
            case 111:
                Localytics.Log.d("Analytics handler received MESSAGE_SET_PUSH_REGID");
                final String pushRegId = (String) msg.obj;
                _runBatchTransactionOnProvider(new Runnable() { // from class: com.localytics.android.AnalyticsHandler.8
                    @Override // java.lang.Runnable
                    public void run() {
                        AnalyticsHandler.this._setPushID(pushRegId);
                    }
                });
                return;
            case 112:
                Localytics.Log.d("Analytics handler received MESSAGE_SET_REFERRERID");
                final String referrerId = (String) msg.obj;
                _runBatchTransactionOnProvider(new Runnable() { // from class: com.localytics.android.AnalyticsHandler.12
                    @Override // java.lang.Runnable
                    public void run() {
                        AnalyticsHandler.this._setReferrerId(referrerId);
                    }
                });
                return;
            case 113:
                Localytics.Log.d("Analytics handler received MESSAGE_RETRIEVE_TOKEN_FROM_INSTANCEID");
                _runBatchTransactionOnProvider(new Runnable() { // from class: com.localytics.android.AnalyticsHandler.13
                    @Override // java.lang.Runnable
                    public void run() {
                        AnalyticsHandler.this._retrieveTokenFromInstanceId();
                    }
                });
                return;
            default:
                super.handleMessageExtended(msg);
                return;
        }
    }

    @Override // com.localytics.android.BaseHandler
    protected void _deleteUploadedData(int maxRowToDelete) {
        this.mProvider.remove("events", "_id <= " + maxRowToDelete, null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void _setNotificationsDisabled(int disabled) {
        ContentValues values = new ContentValues();
        values.put("push_disabled", Integer.valueOf(disabled));
        this.mProvider.update("info", values, null, null);
    }

    @Override // com.localytics.android.BaseHandler
    protected void _onUploadCompleted(boolean successful, String responseBody) {
        this.mProvider.vacuumIfNecessary();
    }

    @Override // com.localytics.android.BaseHandler
    protected int _getMaxRowToUpload() {
        int numberOfAttributes = 0;
        Cursor cursor = null;
        try {
            cursor = this.mProvider.query("events", new String[]{"_id"}, null, null, "_id ASC");
            if (cursor.moveToLast()) {
                numberOfAttributes = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
            }
            if (cursor != null) {
                cursor.close();
            }
            return numberOfAttributes;
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.close();
            }
            return 0;
        }
    }

    @Override // com.localytics.android.BaseHandler
    protected TreeMap<Integer, Object> _getDataToUpload() {
        TreeMap<Integer, Object> eventsMap = new TreeMap<>();
        Cursor cursor = null;
        try {
            cursor = this.mProvider.query("events", null, null, null, "_id ASC");
            while (cursor.moveToNext() && eventsMap.size() < 100) {
                int eventID = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
                String eventBlob = cursor.getString(cursor.getColumnIndexOrThrow("blob"));
                eventsMap.put(Integer.valueOf(eventID), eventBlob);
            }
            if (cursor != null) {
                cursor.close();
            }
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.close();
            }
        }
        return eventsMap;
    }

    @Override // com.localytics.android.BaseHandler
    protected BaseUploadThread _getUploadThread(TreeMap<Integer, Object> data, String customerId) {
        return new AnalyticsUploadHandler(this, data, customerId, this.mLocalyticsDao);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void _registerPush(String newSenderID) {
        Cursor cursor = null;
        String senderId = null;
        try {
            cursor = this.mProvider.query("info", new String[]{"sender_id", "registration_version", "registration_id"}, null, null, null);
            if (cursor.moveToFirst()) {
                senderId = cursor.getString(cursor.getColumnIndexOrThrow("sender_id"));
            }
            if (!newSenderID.equals(senderId)) {
                _setPushID(newSenderID, null);
            }
            _retrieveTokenFromInstanceId(newSenderID);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private String _getPushRegistrationId() {
        String pushRegId = null;
        Cursor cursor = null;
        try {
            cursor = this.mProvider.query("info", new String[]{"registration_id"}, null, null, null);
            if (cursor.moveToFirst()) {
                pushRegId = cursor.getString(cursor.getColumnIndexOrThrow("registration_id"));
            }
            return pushRegId == null ? "" : pushRegId;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean _areNotificationsDisabled() {
        boolean disabled = false;
        Cursor cursor = null;
        try {
            cursor = this.mProvider.query("info", new String[]{"push_disabled"}, null, null, null);
            while (cursor.moveToNext()) {
                disabled = cursor.getInt(cursor.getColumnIndexOrThrow("push_disabled")) == 1;
            }
            return disabled;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getCustomDimension(final int dimension) {
        return getString(new Callable<String>() { // from class: com.localytics.android.AnalyticsHandler.14
            @Override // java.util.concurrent.Callable
            public String call() {
                return AnalyticsHandler.this._getCustomDimension(dimension);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String _getCustomDimension(int dimension) {
        String value = null;
        if (dimension >= 0 && dimension < 20) {
            value = null;
            String key = getCustomDimensionAttributeKey(dimension);
            Cursor cursor = null;
            try {
                cursor = this.mProvider.query("custom_dimensions", PROJECTION_SET_CUSTOM_DIMENSION, SELECTION_SET_CUSTOM_DIMENSION, new String[]{key}, null);
                if (cursor.moveToFirst()) {
                    value = cursor.getString(cursor.getColumnIndexOrThrow("custom_dimension_value"));
                }
                if (cursor != null) {
                    cursor.close();
                }
            } catch (Throwable th) {
                if (cursor != null) {
                    cursor.close();
                }
                throw th;
            }
        }
        return value;
    }

    private String getCustomDimensionAttributeKey(int index) {
        if (index >= 0 && index < 20) {
            return String.format("%s%s", "custom_dimension_", String.valueOf(index));
        }
        Localytics.Log.e("Custom dimension index cannot exceed 19");
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public FutureTask<String> getCustomerIdFuture() {
        return getFuture(new Callable<String>() { // from class: com.localytics.android.AnalyticsHandler.15
            @Override // java.util.concurrent.Callable
            public String call() {
                return AnalyticsHandler.this._getCustomerId();
            }
        });
    }

    protected String _getCustomerId() {
        Cursor cursor = null;
        String customerId = this.mInstallId;
        try {
            cursor = this.mProvider.query("info", new String[]{"customer_id"}, null, null, null);
            if (cursor.moveToFirst()) {
                customerId = cursor.getString(cursor.getColumnIndexOrThrow("customer_id"));
            }
            return customerId;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getInstallationId() {
        return getString(new Callable<String>() { // from class: com.localytics.android.AnalyticsHandler.16
            @Override // java.util.concurrent.Callable
            public String call() throws Exception {
                return AnalyticsHandler.this.mInstallId;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getPushRegistrationID() {
        return getString(new Callable<String>() { // from class: com.localytics.android.AnalyticsHandler.17
            @Override // java.util.concurrent.Callable
            public String call() {
                Cursor cursor = null;
                try {
                    cursor = AnalyticsHandler.this.mProvider.query("info", new String[]{"registration_id"}, null, null, null);
                    if (cursor.moveToFirst()) {
                        String string = cursor.getString(cursor.getColumnIndexOrThrow("registration_id"));
                    }
                    if (cursor != null) {
                        cursor.close();
                    }
                    return null;
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setPushRegistrationId(String registrationId) {
        queueMessage(obtainMessage(111, registrationId));
    }

    String _getPushSenderId() {
        Cursor cursor = null;
        try {
            cursor = this.mProvider.query("info", new String[]{"sender_id"}, null, null, null);
            if (cursor.moveToFirst()) {
                String string = cursor.getString(cursor.getColumnIndexOrThrow("sender_id"));
            }
            if (cursor != null) {
                cursor.close();
            }
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void _tagEvent(String event, Map<String, String> attributes, Long clv) {
        try {
            AnalyticsHeader header = new AnalyticsHeader(this, this.mProvider, this.mLocalyticsDao.getAppContext(), this.mLocalyticsDao.getInstallationId(), this.mLocalyticsDao.getAppKey());
            String eventUUID = UUID.randomUUID().toString();
            long currentTime = this.mLocalyticsDao.getCurrentTimeMillis();
            Context appContext = this.mLocalyticsDao.getAppContext();
            ContentValues eventValues = new ContentValues();
            JSONObject eventBlob = new JSONObject();
            eventBlob.put("dt", "e");
            eventBlob.put("ct", Math.round(currentTime / 1000.0d));
            eventBlob.put("u", eventUUID);
            eventBlob.put("su", this.mIsSessionOpen ? header.getOpenSessionUUID() : "");
            if (event.startsWith(appContext.getPackageName())) {
                eventBlob.put("n", event.substring(appContext.getPackageName().length() + 1, event.length()));
            } else {
                eventBlob.put("n", event);
            }
            if (clv.longValue() != 0) {
                eventBlob.put("v", clv);
            }
            _addLocationIDsAndCustomDimensions(eventBlob, header.getIdentifiersObject(), header.getCustomerID(), header.getUserType());
            if (attributes != null) {
                eventBlob.put("attrs", new JSONObject(attributes));
            }
            eventValues.put("blob", String.format("%s\n%s", header.getHeaderBlob().toString(), eventBlob.toString()));
            eventValues.put("upload_format", Integer.valueOf(AnalyticsProvider.EventsV3Columns.UploadFormat.V2.getValue()));
            if (eventValues.size() > 0) {
                this.mProvider.insert("events", eventValues);
            }
            this.mListeners.getProxy().localyticsDidTagEvent(event, attributes, clv == null ? 0L : clv.longValue());
        } catch (Exception e) {
        }
    }

    private void _tagOpenEvent() {
        try {
            AnalyticsHeader header = new AnalyticsHeader(this, this.mProvider, this.mLocalyticsDao.getAppContext(), this.mLocalyticsDao.getInstallationId(), this.mLocalyticsDao.getAppKey());
            ContentValues infoValues = new ContentValues();
            String eventUUID = UUID.randomUUID().toString();
            long currentTime = this.mLocalyticsDao.getCurrentTimeMillis();
            JSONObject eventBlob = new JSONObject();
            ContentValues eventValues = new ContentValues();
            eventBlob.put("dt", "s");
            eventBlob.put("ct", Math.round(currentTime / 1000.0d));
            eventBlob.put("u", eventUUID);
            long elapsedTime = 0;
            long lastSessionStartTime = header.getLastSessionStartTime();
            if (lastSessionStartTime > 0) {
                elapsedTime = currentTime - lastSessionStartTime;
            }
            eventBlob.put("sl", Math.round(elapsedTime / 1000.0d));
            eventBlob.put("nth", header.getSessionSequenceNumber());
            _addLocationIDsAndCustomDimensions(eventBlob, header.getIdentifiersObject(), header.getCustomerID(), header.getUserType());
            String headerEventBlob = String.format("%s\n%s", header.getHeaderBlob().toString(), eventBlob.toString());
            eventValues.put("blob", headerEventBlob);
            eventValues.put("upload_format", Integer.valueOf(AnalyticsProvider.EventsV3Columns.UploadFormat.V2.getValue()));
            infoValues.put("last_session_open_time", Long.valueOf(currentTime));
            infoValues.put("next_session_number", Integer.valueOf(header.getSessionSequenceNumber() + 1));
            infoValues.put("current_session_uuid", eventUUID);
            if (this.mFirstSessionEver) {
                infoValues.put("first_open_event_blob", headerEventBlob);
            }
            this.mProvider.update("info", infoValues, null, null);
            if (eventValues.size() > 0) {
                this.mProvider.insert("events", eventValues);
            }
        } catch (Exception e) {
            Localytics.Log.e("Failed to save session open event", e);
        }
    }

    private void _tagQueuedCloseEvent(boolean isRecoveredClose) {
        try {
            AnalyticsHeader header = new AnalyticsHeader(this, this.mProvider, this.mLocalyticsDao.getAppContext(), this.mLocalyticsDao.getInstallationId(), this.mLocalyticsDao.getAppKey());
            ContentValues infoValues = new ContentValues();
            long currentTime = this.mLocalyticsDao.getCurrentTimeMillis();
            if (!isRecoveredClose) {
                infoValues.put("last_session_close_time", Long.valueOf(currentTime));
            }
            String closeBlob = _createCloseBlob(header, isRecoveredClose, currentTime);
            infoValues.put("queued_close_session_blob", closeBlob);
            infoValues.put("queued_close_session_blob_upload_format", Integer.valueOf(AnalyticsProvider.EventsV3Columns.UploadFormat.V2.getValue()));
            this.mProvider.update("info", infoValues, null, null);
        } catch (Exception e) {
            Localytics.Log.e("Failed to save queued session close event", e);
        }
    }

    private void _tagOptEvent(boolean isOptingOut) {
        try {
            AnalyticsHeader header = new AnalyticsHeader(this, this.mProvider, this.mLocalyticsDao.getAppContext(), this.mLocalyticsDao.getInstallationId(), this.mLocalyticsDao.getAppKey());
            String eventUUID = UUID.randomUUID().toString();
            long currentTime = this.mLocalyticsDao.getCurrentTimeMillis();
            JSONObject eventBlob = new JSONObject();
            ContentValues eventValues = new ContentValues();
            eventBlob.put("dt", "o");
            eventBlob.put("u", eventUUID);
            eventBlob.put("out", isOptingOut);
            eventBlob.put("ct", Math.round(currentTime / 1000.0d));
            eventValues.put("blob", String.format("%s\n%s", header.getHeaderBlob().toString(), eventBlob.toString()));
            eventValues.put("upload_format", Integer.valueOf(AnalyticsProvider.EventsV3Columns.UploadFormat.V2.getValue()));
            if (eventValues.size() > 0) {
                this.mProvider.insert("events", eventValues);
            }
        } catch (Exception e) {
            Localytics.Log.e("Failed to save opt in/out event", e);
        }
    }

    private String _createCloseBlob(AnalyticsHeader header, boolean isRecoveredClose, long currentTime) throws JSONException {
        Object eventUUID = UUID.randomUUID().toString();
        JSONObject eventBlob = new JSONObject();
        eventBlob.put("dt", "c");
        eventBlob.put("u", eventUUID);
        eventBlob.put("su", header.getOpenSessionUUID());
        long lastSessionStartTime = header.getLastSessionStartTime();
        eventBlob.put("ss", Math.round(lastSessionStartTime / 1000.0d));
        eventBlob.put("ct", Math.round(currentTime / 1000.0d));
        eventBlob.put("ctl", Math.round((currentTime - lastSessionStartTime) / 1000.0d));
        JSONArray screens = new JSONArray((Collection) _getScreens());
        if (screens.length() > 0) {
            eventBlob.put("fl", screens);
        }
        eventBlob.put("isl", isRecoveredClose);
        _addLocationIDsAndCustomDimensions(eventBlob, header.getIdentifiersObject(), header.getCustomerID(), header.getUserType());
        return String.format("%s\n%s", header.getHeaderBlob().toString(), eventBlob.toString());
    }

    List<String> _getScreens() {
        List<String> screens = new LinkedList<>();
        Cursor query = this.mProvider.query("screens", null, null, null, null);
        while (query.moveToNext()) {
            screens.add(query.getString(query.getColumnIndexOrThrow("name")));
        }
        query.close();
        return screens;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void _updateHeaderForTestModeAttribution(String playAttribution, JSONObject headerAttributes, DatapointHelper.AdvertisingInfo advertisingInfo, boolean ignoreFirstSession) {
        if (!this.mSentReferrerTestMode) {
            if (!TextUtils.isEmpty(playAttribution)) {
                String[] arr$ = URLDecoder.decode(playAttribution).split("[&]");
                for (String str : arr$) {
                    String[] keyValue = str.split("[=]");
                    if (keyValue.length > 1) {
                        String key = keyValue[0].toLowerCase();
                        String value = keyValue[1].toLowerCase();
                        this.mReferrerTestModeEnabled = key.equals(PARAM_LOCALYTICS_REFERRER_TEST_MODE) && (value.equals("1") || value.equals(ServerProtocol.DIALOG_RETURN_SCOPES_TRUE));
                    }
                }
            }
            if ((ignoreFirstSession || this.mFirstSessionEver) && this.mReferrerTestModeEnabled) {
                try {
                    Localytics.Log.i("[REFERRAL] using fake id for attribution test mode");
                    String fakeDeviceID = Long.toHexString(new SecureRandom().nextLong());
                    headerAttributes.put("aid", fakeDeviceID);
                    headerAttributes.put("du", DatapointHelper.getSha256_buggy(fakeDeviceID));
                    headerAttributes.put("caid", fakeDeviceID);
                    if (advertisingInfo != null) {
                        String fakeAdvertisingId = UUID.randomUUID().toString();
                        headerAttributes.put("gadid", fakeAdvertisingId);
                        headerAttributes.put("gcadid", fakeAdvertisingId);
                    }
                    this.mSentReferrerTestMode = true;
                } catch (JSONException e) {
                    Localytics.Log.e("Exception adding values to object", e);
                }
            }
        }
    }

    private void _addLocationIDsAndCustomDimensions(JSONObject eventBlob, JSONObject identifiers, String customerID, String userType) {
        try {
            if (sLastLocation != null) {
                double lat = sLastLocation.getLatitude();
                double lng = sLastLocation.getLongitude();
                if (lat != 0.0d && lng != 0.0d) {
                    eventBlob.put("lat", lat);
                    eventBlob.put("lng", lng);
                }
            }
            eventBlob.put("cid", customerID);
            eventBlob.put("utp", userType);
            if (identifiers.length() > 0) {
                eventBlob.put("ids", identifiers);
            }
            Cursor cursor = null;
            try {
                cursor = this.mProvider.query("custom_dimensions", null, null, null, null);
                while (cursor.moveToNext()) {
                    String key = cursor.getString(cursor.getColumnIndexOrThrow("custom_dimension_key"));
                    String value = cursor.getString(cursor.getColumnIndexOrThrow("custom_dimension_value"));
                    String newKey = key.replace("custom_dimension_", "c");
                    eventBlob.put(newKey, value);
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        } catch (JSONException e) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void _tagScreen(String screen) {
        if (this.mIsSessionOpen) {
            if (!screen.equals(this.mLastScreenTag)) {
                this.mLastScreenTag = screen;
                ContentValues screenValues = new ContentValues();
                screenValues.put("name", screen);
                this.mProvider.insert("screens", screenValues);
                return;
            }
            return;
        }
        Localytics.Log.w("Screen not tagged because a session is not open");
    }

    private void _clearScreens() {
        this.mProvider.remove("screens", null, null);
        this.mLastScreenTag = null;
    }

    private String _retrieveLastScreenTag() {
        String lastScreen = null;
        Cursor query = this.mProvider.mDb.rawQuery(String.format("SELECT MAX(rowid), %s FROM %s", "name", "screens"), null);
        if (query.moveToFirst()) {
            lastScreen = query.getString(query.getColumnIndexOrThrow("name"));
        }
        query.close();
        return lastScreen;
    }

    protected void _open() {
        if (this.mIsSessionOpen) {
            Localytics.Log.w("Session was already open");
            return;
        }
        Cursor cursor = null;
        try {
            cursor = this.mProvider.query("info", new String[]{"last_session_close_time"}, null, null, null);
            if (cursor.moveToFirst()) {
                long sessionCloseTime = cursor.getLong(cursor.getColumnIndexOrThrow("last_session_close_time"));
                boolean newSession = this.mLocalyticsDao.getCurrentTimeMillis() - sessionCloseTime > Constants.SESSION_EXPIRATION;
                this.mListeners.getProxy().localyticsSessionWillOpen(this.mFirstSessionEver, this.mAppWasUpgraded, !newSession);
                Localytics.Log.v(newSession ? "Opening new session" : "Opening old closed session and reconnecting");
                _dequeQueuedCloseSessionTag(newSession);
                this.mIsSessionOpen = true;
                if (newSession) {
                    _tagOpenEvent();
                    BaseProvider.deleteOldFiles(this.mLocalyticsDao.getAppContext());
                }
                this.mListeners.getProxy().localyticsSessionDidOpen(this.mFirstSessionEver, this.mAppWasUpgraded, !newSession);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void _dequeQueuedCloseSessionTag(boolean saveAsEvent) {
        ContentValues values = new ContentValues();
        if (saveAsEvent) {
            Cursor cursor = null;
            String queuedCloseSessionTag = null;
            int queuedCloseSessionTagUploadFormat = 0;
            try {
                cursor = this.mProvider.query("info", new String[]{"queued_close_session_blob", "queued_close_session_blob_upload_format"}, null, null, null);
                if (cursor.moveToFirst()) {
                    queuedCloseSessionTag = cursor.getString(cursor.getColumnIndexOrThrow("queued_close_session_blob"));
                    queuedCloseSessionTagUploadFormat = cursor.getInt(cursor.getColumnIndexOrThrow("queued_close_session_blob_upload_format"));
                }
                if (TextUtils.isEmpty(queuedCloseSessionTag) && !this.mFirstSessionEver) {
                    try {
                        AnalyticsHeader header = new AnalyticsHeader(this, this.mProvider, this.mLocalyticsDao.getAppContext(), this.mLocalyticsDao.getInstallationId(), this.mLocalyticsDao.getAppKey());
                        queuedCloseSessionTag = _createCloseBlob(header, true, this.mLocalyticsDao.getCurrentTimeMillis());
                        queuedCloseSessionTagUploadFormat = AnalyticsProvider.EventsV3Columns.UploadFormat.V2.getValue();
                    } catch (Exception e) {
                    }
                }
                if (!TextUtils.isEmpty(queuedCloseSessionTag)) {
                    values.put("blob", queuedCloseSessionTag);
                    values.put("upload_format", Integer.valueOf(queuedCloseSessionTagUploadFormat));
                    this.mProvider.insert("events", values);
                    values.clear();
                    this.mAppWasUpgraded = false;
                    this.mFirstSessionEver = false;
                    _clearScreens();
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        values.putNull("queued_close_session_blob");
        values.putNull("queued_close_session_blob_upload_format");
        values.put("last_session_close_time", (Integer) 0);
        this.mProvider.update("info", values, null, null);
    }

    protected void _close() {
        if (!this.mIsSessionOpen) {
            Localytics.Log.w("Session was not open, so close is not possible.");
            return;
        }
        this.mListeners.getProxy().localyticsSessionWillClose();
        ContentValues values = new ContentValues();
        values.put("last_session_close_time", Long.valueOf(this.mLocalyticsDao.getCurrentTimeMillis()));
        this.mProvider.update("info", values, null, null);
        _tagQueuedCloseEvent(false);
        this.mIsSessionOpen = false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void _setCustomDimension(int dimension, String value) {
        String key = getCustomDimensionAttributeKey(dimension);
        synchronized (this) {
            if (!TextUtils.isEmpty(value)) {
                ContentValues values = new ContentValues();
                values.put("custom_dimension_key", key);
                values.put("custom_dimension_value", value);
                if (this.mProvider.update("custom_dimensions", values, SELECTION_SET_CUSTOM_DIMENSION, new String[]{key}) != 0) {
                    this.mCachedCustomDimensions.put(Integer.valueOf(dimension), value);
                } else if (this.mProvider.insert("custom_dimensions", values) != -1) {
                    this.mCachedCustomDimensions.put(Integer.valueOf(dimension), value);
                }
            } else if (this.mProvider.remove("custom_dimensions", String.format("%s = ?", "custom_dimension_key"), new String[]{key}) != 0) {
                this.mCachedCustomDimensions.remove(Integer.valueOf(dimension));
            }
        }
    }

    protected void _setIdentifier(String key, String value) {
        if (value != null) {
            value = value.trim();
        }
        if (key.equals("customer_id")) {
            String currentCustomerId = _getCustomerId();
            if (!TextUtils.equals(value, currentCustomerId)) {
                _setIdentifier("first_name", null);
                _setIdentifier("last_name", null);
                _setIdentifier("full_name", null);
                _setIdentifier("email", null);
                ContentValues values = new ContentValues();
                if (TextUtils.isEmpty(value)) {
                    values.put("customer_id", this.mInstallId);
                    values.put("user_type", "anonymous");
                } else {
                    values.put("customer_id", value);
                    values.put("user_type", "known");
                }
                this.mProvider.update("info", values, null, null);
            } else {
                return;
            }
        }
        synchronized (this) {
            if (!TextUtils.isEmpty(value)) {
                ContentValues values2 = new ContentValues();
                values2.put("key", key);
                values2.put("value", value);
                if (this.mProvider.update("identifiers", values2, SELECTION_SET_IDENTIFIER, new String[]{key}) != 0) {
                    this.mCachedIdentifiers.put(key, value);
                } else if (this.mProvider.insert("identifiers", values2) != -1) {
                    this.mCachedIdentifiers.put(key, value);
                }
            } else if (this.mProvider.remove("identifiers", String.format("%s = ?", "key"), new String[]{key}) != 0) {
                this.mCachedIdentifiers.remove(key);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void openSession() {
        queueMessage(obtainMessage(101));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setCustomDimension(int dimension, String value) {
        if (dimension < 0 || dimension >= 20) {
            throw new IllegalArgumentException("Only valid dimensions are 0 - 19");
        }
        queueMessage(obtainMessage(107, new Object[]{Integer.valueOf(dimension), value}));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setIdentifier(String key, String value) {
        if (TextUtils.isEmpty(key)) {
            throw new IllegalArgumentException("key cannot be null or empty");
        }
        queueMessage(obtainMessage(105, new Object[]{key, value}));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getIdentifier(final String key) {
        return getString(new Callable<String>() { // from class: com.localytics.android.AnalyticsHandler.18
            @Override // java.util.concurrent.Callable
            public String call() {
                Cursor cursor = null;
                String currentValue = null;
                try {
                    cursor = AnalyticsHandler.this.mProvider.query("identifiers", AnalyticsHandler.PROJECTION_SET_IDENTIFIER, AnalyticsHandler.SELECTION_SET_IDENTIFIER, new String[]{key}, null);
                    if (cursor.moveToFirst()) {
                        currentValue = cursor.getString(cursor.getColumnIndexOrThrow("value"));
                    }
                    return currentValue;
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void closeSession() {
        queueMessage(obtainMessage(102));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Map<String, String> getCachedIdentifiers() {
        Map<String, String> identifiers = new HashMap<>();
        synchronized (this) {
            if (this.mCachedIdentifiers != null) {
                identifiers.putAll(this.mCachedIdentifiers);
            }
        }
        return identifiers;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Map<Integer, String> getCachedCustomDimensions() {
        Map<Integer, String> customDimensions = new HashMap<>();
        synchronized (this) {
            if (this.mCachedCustomDimensions != null) {
                customDimensions.putAll(this.mCachedCustomDimensions);
            }
        }
        return customDimensions;
    }

    protected void _initCachedIdentifiers() {
        synchronized (this) {
            if (this.mCachedIdentifiers == null) {
                this.mCachedIdentifiers = new HashMap();
            }
            this.mCachedIdentifiers.putAll(_getIdentifiers());
        }
    }

    protected void _initCachedCustomDimensions() {
        synchronized (this) {
            if (this.mCachedCustomDimensions == null) {
                this.mCachedCustomDimensions = new HashMap();
            }
            for (int i = 0; i < 20; i++) {
                this.mCachedCustomDimensions.put(Integer.valueOf(i), _getCustomDimension(i));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void tagScreen(String screen) {
        if (TextUtils.isEmpty(screen)) {
            throw new IllegalArgumentException("event cannot be null or empty");
        }
        queueMessage(obtainMessage(104, screen));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setLocation(Location location) {
        queueMessage(obtainMessage(106, new Location(location)));
    }

    protected boolean _isOptedOut() {
        Cursor cursor = null;
        try {
            cursor = this.mProvider.query("info", new String[]{"opt_out"}, null, null, null);
            if (cursor.moveToFirst()) {
                boolean z = cursor.getInt(cursor.getColumnIndexOrThrow("opt_out")) != 0;
            }
            if (cursor != null) {
                cursor.close();
            }
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void _setOptedOut(boolean isOptingOut) {
        if (_isOptedOut() != isOptingOut) {
            _tagOptEvent(isOptingOut);
            if (this.mIsSessionOpen && isOptingOut) {
                _close();
            }
            ContentValues values = new ContentValues();
            values.put("opt_out", Boolean.valueOf(isOptingOut));
            this.mProvider.update("info", values, null, null);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setOptedOut(boolean isOptingOut) {
        Localytics.Log.v(String.format("Requested opt-out state is %b", Boolean.valueOf(isOptingOut)));
        queueMessage(obtainMessage(108, isOptingOut ? 1 : 0, 0));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isOptedOut() {
        return getBool(new Callable<Boolean>() { // from class: com.localytics.android.AnalyticsHandler.19
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // java.util.concurrent.Callable
            public Boolean call() {
                return Boolean.valueOf(AnalyticsHandler.this._isOptedOut());
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setNotificationsDisabled(boolean disabled) {
        queueMessage(obtainMessage(110, disabled ? 1 : 0, 0));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void _setPushID(String pushRegId) {
        String oldRegistrationId = _getPushRegistrationId();
        String newRegistrationId = pushRegId == null ? "" : pushRegId;
        ContentValues values = new ContentValues();
        values.put("registration_id", newRegistrationId);
        values.put("registration_version", DatapointHelper.getAppVersion(this.mLocalyticsDao.getAppContext()));
        this.mProvider.update("info", values, null, null);
        if (!newRegistrationId.equals(oldRegistrationId)) {
            _tagPushRegisteredEvent();
        }
    }

    private void _setPushID(String senderId, String pushRegId) {
        String oldRegistrationId = _getPushRegistrationId();
        String newRegistrationId = pushRegId == null ? "" : pushRegId;
        ContentValues values = new ContentValues();
        values.put("sender_id", senderId);
        values.put("registration_id", newRegistrationId);
        this.mProvider.update("info", values, null, null);
        if (!newRegistrationId.equals(oldRegistrationId)) {
            _tagPushRegisteredEvent();
        }
    }

    private void _tagPushRegisteredEvent() {
        this.mLocalyticsDao.tagEvent("Localytics Push Registered");
        this.mLocalyticsDao.upload();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void registerPush(String senderId, long delay) {
        queueMessageDelayed(obtainMessage(109, senderId), delay);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void retrieveTokenFromInstanceId() {
        queueMessage(obtainMessage(113));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void _retrieveTokenFromInstanceId() {
        _retrieveTokenFromInstanceId(_getPushSenderId());
    }

    private void _retrieveTokenFromInstanceId(String senderId) {
        if (ContextCompat.checkSelfPermission(this.mLocalyticsDao.getAppContext(), "com.google.android.c2dm.permission.RECEIVE") == 0) {
            try {
                if (TextUtils.isEmpty(senderId)) {
                    Localytics.Log.w("GCM registration failed, got empty sender id");
                } else {
                    String token = InstanceID.getInstance(this.mLocalyticsDao.getAppContext()).getToken(senderId, "GCM");
                    if (!TextUtils.isEmpty(token)) {
                        Localytics.Log.v(String.format("GCM registered, new id: %s", token));
                        _setPushID(token);
                    } else {
                        Localytics.Log.w("GCM registration failed, got empty token");
                    }
                }
                return;
            } catch (IOException e) {
                Localytics.Log.e("Exception while registering GCM", e);
                String error = e.getMessage();
                if ("SERVICE_NOT_AVAILABLE".equals(error) || "TIMEOUT".equals(error)) {
                    int i = sNumPushRegistrationRetries;
                    sNumPushRegistrationRetries = i + 1;
                    if (i < 3) {
                        long delay = ((long) Math.pow(2.0d, sNumPushRegistrationRetries - 1)) * BASE_PUSH_REGISTRATION_BACKOFF;
                        Localytics.Log.w("GCM registration ERROR_SERVICE_NOT_AVAILABLE. Retrying in " + delay + " milliseconds.");
                        registerPush(senderId, delay);
                        return;
                    }
                    sNumPushRegistrationRetries = 0;
                    return;
                }
                return;
            }
        }
        Localytics.Log.w("'com.google.android.c2dm.permission.RECEIVE' missing from AndroidManifest.xml");
    }

    @Override // com.localytics.android.BaseHandler
    protected void _init() {
        if (this.mProvider == null) {
            this.mProvider = new AnalyticsProvider(this.siloName.toLowerCase(), this.mLocalyticsDao);
        }
        _initApiKey();
        _initCachedIdentifiers();
        _initCachedCustomDimensions();
    }

    protected void _initApiKey() {
        Cursor cursor = null;
        try {
            String appVersion = DatapointHelper.getAppVersion(this.mLocalyticsDao.getAppContext());
            cursor = this.mProvider.query("info", new String[]{"app_version", "uuid", "next_session_number", "customer_id"}, null, null, null);
            if (cursor.moveToFirst()) {
                Localytics.Log.v(String.format("Loading details for API key %s", this.mLocalyticsDao.getAppKey()));
                ContentValues values = new ContentValues();
                if (!cursor.getString(cursor.getColumnIndexOrThrow("app_version")).equals(appVersion)) {
                    values.put("app_version", appVersion);
                    this.mAppWasUpgraded = true;
                }
                if (values.size() != 0) {
                    this.mProvider.update("info", values, null, null);
                }
                this.mFirstSessionEver = cursor.getInt(cursor.getColumnIndexOrThrow("next_session_number")) == 1;
                this.mInstallId = cursor.getString(cursor.getColumnIndexOrThrow("uuid"));
            } else {
                Localytics.Log.v(String.format("Performing first-time initialization for new API key %s", this.mLocalyticsDao.getAppKey()));
                String installationID = UUID.randomUUID().toString();
                this.mInstallId = installationID;
                ContentValues values2 = new ContentValues();
                values2.put("api_key", this.mLocalyticsDao.getAppKey());
                values2.put("uuid", installationID);
                values2.put("created_time", Long.valueOf(this.mLocalyticsDao.getCurrentTimeMillis()));
                values2.put("opt_out", Boolean.FALSE);
                values2.put("push_disabled", Boolean.FALSE);
                values2.put("customer_id", installationID);
                values2.put("user_type", "anonymous");
                values2.put("fb_attribution", DatapointHelper.getFBAttribution(this.mLocalyticsDao.getAppContext()));
                values2.put("first_android_id", DatapointHelper.getAndroidIdOrNull(this.mLocalyticsDao.getAppContext()));
                values2.put("package_name", this.mLocalyticsDao.getAppContext().getPackageName());
                values2.put("app_version", appVersion);
                values2.put("next_session_number", (Integer) 1);
                values2.put("next_header_number", (Integer) 1);
                values2.put("last_session_open_time", (Integer) 0);
                values2.put("last_session_close_time", (Integer) 0);
                this.mProvider.insert("info", values2);
                this.mFirstSessionEver = true;
            }
            this.mLastScreenTag = _retrieveLastScreenTag();
            this.mProvider.vacuumIfNecessary();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setReferrerId(String referrerId) {
        queueMessage(obtainMessage(112, referrerId));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void _setReferrerId(String referrerId) {
        if (!TextUtils.isEmpty(referrerId)) {
            Cursor cursor = null;
            try {
                cursor = this.mProvider.query("info", new String[]{"play_attribution"}, null, null, null);
                if (cursor.moveToFirst() && TextUtils.isEmpty(cursor.getString(cursor.getColumnIndexOrThrow("play_attribution")))) {
                    ContentValues values = new ContentValues();
                    values.put("play_attribution", referrerId);
                    this.mProvider.update("info", values, null, null);
                    Localytics.Log.i("[REFERRAL] _setReferrerId: " + referrerId);
                    _reuploadFirstSession(referrerId);
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
    }

    private void _reuploadFirstSession(String referrerId) {
        String updatedEventBlob = _replaceAttributionInFirstSession(referrerId);
        if (!TextUtils.isEmpty(updatedEventBlob)) {
            new ReferralUploader(this, _getCustomerId(), updatedEventBlob, this.mLocalyticsDao).start();
        }
    }

    String _replaceAttributionInFirstSession(String referrerId) {
        Cursor cursor = null;
        try {
            cursor = this.mProvider.query("info", new String[]{"first_open_event_blob"}, null, null, null);
            if (cursor.moveToFirst()) {
                String firstEventBlob = cursor.getString(cursor.getColumnIndexOrThrow("first_open_event_blob"));
                if (!TextUtils.isEmpty(firstEventBlob)) {
                    String[] blobs = firstEventBlob.split("[\n]");
                    try {
                        JSONObject headerBlob = new JSONObject(blobs[0]);
                        JSONObject headerAttributes = (JSONObject) headerBlob.get("attrs");
                        DatapointHelper.AdvertisingInfo advertisingInfo = DatapointHelper.getAdvertisingInfo(this.mLocalyticsDao.getAppContext());
                        _updateHeaderForTestModeAttribution(referrerId, headerAttributes, advertisingInfo, true);
                        headerAttributes.put("aurl", referrerId);
                        String format = String.format("%s\n%s", headerBlob.toString(), blobs[1]);
                    } catch (JSONException e) {
                        Localytics.Log.e("JSONException", e);
                    }
                }
            }
            if (cursor != null) {
                cursor.close();
            }
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Override // com.localytics.android.LocationListener
    public void localyticsDidUpdateLocation(Location location) {
        setLocation(location);
    }

    @Override // com.localytics.android.LocationListener
    public void localyticsDidTriggerRegions(List<Region> regions, Region.Event event) {
    }

    @Override // com.localytics.android.LocationListener
    public void localyticsDidUpdateMonitoredGeofences(List<CircularRegion> added, List<CircularRegion> removed) {
    }
}
