package com.localytics.android;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import com.localytics.android.DatapointHelper;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
class AnalyticsHeader {
    private String mCustomerID;
    final JSONObject mHeaderBlob = new JSONObject();
    private JSONObject mIdentifiers;
    private long mLastSessionStart;
    private String mOpenSessionUUID;
    private int mSessionSequenceNumber;
    private String mUserType;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AnalyticsHeader(AnalyticsHandler handler, BaseProvider provider, Context appContext, String installationId, String apiKey) throws JSONException {
        setupHeader(handler, provider, appContext, installationId, apiKey);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getSessionSequenceNumber() {
        return this.mSessionSequenceNumber;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getOpenSessionUUID() {
        return this.mOpenSessionUUID;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getCustomerID() {
        return this.mCustomerID;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getUserType() {
        return this.mUserType;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public long getLastSessionStartTime() {
        return this.mLastSessionStart;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public JSONObject getHeaderBlob() {
        return this.mHeaderBlob;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public JSONObject getIdentifiersObject() {
        return this.mIdentifiers;
    }

    private void setupHeader(AnalyticsHandler handler, BaseProvider provider, Context appContext, String installationId, String apiKey) throws JSONException {
        int headerSeqNumber = 0;
        this.mSessionSequenceNumber = 0;
        this.mOpenSessionUUID = "";
        this.mCustomerID = "";
        this.mUserType = "";
        this.mLastSessionStart = 0L;
        this.mHeaderBlob.put("dt", "h");
        this.mHeaderBlob.put("u", UUID.randomUUID().toString());
        JSONObject headerAttributes = new JSONObject();
        Cursor cursor = null;
        try {
            cursor = provider.query("info", null, null, null, null);
            if (cursor.moveToFirst()) {
                this.mHeaderBlob.put("pa", Math.round(cursor.getLong(cursor.getColumnIndexOrThrow("created_time")) / 1000.0d));
                this.mSessionSequenceNumber = cursor.getInt(cursor.getColumnIndexOrThrow("next_session_number"));
                this.mCustomerID = cursor.getString(cursor.getColumnIndexOrThrow("customer_id"));
                this.mUserType = cursor.getString(cursor.getColumnIndexOrThrow("user_type"));
                this.mOpenSessionUUID = cursor.getString(cursor.getColumnIndexOrThrow("current_session_uuid"));
                headerSeqNumber = cursor.getInt(cursor.getColumnIndexOrThrow("next_header_number"));
                this.mHeaderBlob.put("seq", headerSeqNumber);
                this.mLastSessionStart = cursor.getLong(cursor.getColumnIndexOrThrow("last_session_open_time"));
                String packageName = cursor.getString(cursor.getColumnIndexOrThrow("package_name"));
                String playAttribution = cursor.getString(cursor.getColumnIndexOrThrow("play_attribution"));
                String firstAndroidId = cursor.getString(cursor.getColumnIndexOrThrow("first_android_id"));
                String registrationId = cursor.getString(cursor.getColumnIndexOrThrow("registration_id"));
                String fbAttribution = cursor.getString(cursor.getColumnIndexOrThrow("fb_attribution"));
                boolean notificationsEnabled = cursor.getInt(cursor.getColumnIndexOrThrow("push_disabled")) == 0;
                headerAttributes = _buildHeaderAttributes(handler, appContext, packageName, playAttribution, installationId, apiKey, fbAttribution, registrationId, firstAndroidId, notificationsEnabled);
            }
            updateNextHeaderNumber(provider, headerSeqNumber);
            this.mHeaderBlob.put("attrs", headerAttributes);
            this.mIdentifiers = _getIdentifiersObject(provider);
            if (this.mIdentifiers.length() > 0) {
                this.mHeaderBlob.put("ids", this.mIdentifiers);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private JSONObject _buildHeaderAttributes(AnalyticsHandler handler, Context appContext, String packageName, String playAttribution, String installationId, String apiKey, String fbAttribution, String registrationId, String firstAndroidId, boolean notificationsEnabled) throws JSONException {
        JSONObject headerAttributes = new JSONObject();
        headerAttributes.put("dt", "a");
        headerAttributes.put("au", apiKey);
        String deviceID = DatapointHelper.getAndroidIdHashOrNull(appContext);
        if (deviceID != null) {
            headerAttributes.put("du", deviceID);
        }
        TelephonyManager telephonyManager = (TelephonyManager) appContext.getSystemService("phone");
        headerAttributes.put("lv", Constants.LOCALYTICS_CLIENT_LIBRARY_VERSION);
        headerAttributes.put("av", DatapointHelper.getAppVersion(appContext));
        headerAttributes.put("dp", "Android");
        headerAttributes.put("dll", Locale.getDefault().getLanguage());
        headerAttributes.put("dlc", Locale.getDefault().getCountry());
        headerAttributes.put("nc", telephonyManager.getNetworkCountryIso());
        headerAttributes.put("dc", telephonyManager.getSimCountryIso());
        headerAttributes.put("dma", DatapointHelper.getManufacturer());
        headerAttributes.put("dmo", Build.MODEL);
        headerAttributes.put("dov", Build.VERSION.RELEASE);
        headerAttributes.put("nca", telephonyManager.getNetworkOperatorName());
        headerAttributes.put("dac", DatapointHelper.getNetworkType(telephonyManager, appContext));
        headerAttributes.put("iu", installationId);
        headerAttributes.put("push", registrationId == null ? "" : registrationId);
        headerAttributes.put("ne", notificationsEnabled && !TextUtils.isEmpty(registrationId));
        if (fbAttribution != null) {
            headerAttributes.put("fbat", fbAttribution);
        }
        if (firstAndroidId != null) {
            headerAttributes.put("aid", firstAndroidId);
        }
        Object androidIdOrNull = DatapointHelper.getAndroidIdOrNull(appContext);
        if (androidIdOrNull == null) {
            androidIdOrNull = JSONObject.NULL;
        }
        headerAttributes.put("caid", androidIdOrNull);
        DatapointHelper.AdvertisingInfo advertisingInfo = DatapointHelper.getAdvertisingInfo(appContext);
        if (advertisingInfo != null && advertisingInfo.id != null) {
            headerAttributes.put("gadid", advertisingInfo.id);
            headerAttributes.put("gcadid", advertisingInfo.id);
        }
        if (packageName != null) {
            headerAttributes.put("pkg", packageName);
        }
        if (playAttribution != null) {
            headerAttributes.put("aurl", playAttribution);
        }
        headerAttributes.put("lad", advertisingInfo != null && advertisingInfo.limitAdTracking);
        TimeZone tz = TimeZone.getDefault();
        Date time = Calendar.getInstance(tz).getTime();
        int timezone = tz.inDaylightTime(time) ? tz.getRawOffset() + tz.getDSTSavings() : tz.getRawOffset();
        headerAttributes.put("tz", timezone / 1000);
        Object serialNumberHashOrNull = DatapointHelper.getSerialNumberHashOrNull();
        if (serialNumberHashOrNull == null) {
            serialNumberHashOrNull = JSONObject.NULL;
        }
        headerAttributes.put("dms", serialNumberHashOrNull);
        headerAttributes.put("dsdk", Integer.valueOf(Constants.CURRENT_API_LEVEL));
        headerAttributes.put("lpg", DatapointHelper.isLocationPermissionGranted(appContext));
        handler._updateHeaderForTestModeAttribution(playAttribution, headerAttributes, advertisingInfo, false);
        return headerAttributes;
    }

    private JSONObject _getIdentifiersObject(BaseProvider provider) {
        Cursor cursor = null;
        JSONObject result = new JSONObject();
        try {
            cursor = provider.query("identifiers", null, null, null, null);
            while (cursor.moveToNext()) {
                result.put(cursor.getString(cursor.getColumnIndexOrThrow("key")), cursor.getString(cursor.getColumnIndexOrThrow("value")));
            }
            if (cursor != null) {
                cursor.close();
            }
        } catch (JSONException e) {
            if (cursor != null) {
                cursor.close();
            }
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }

    private void updateNextHeaderNumber(BaseProvider provider, int headerSeqNumber) {
        ContentValues infoValues = new ContentValues();
        infoValues.put("next_header_number", Integer.valueOf(headerSeqNumber + 1));
        provider.update("info", infoValues, null, null);
    }
}
