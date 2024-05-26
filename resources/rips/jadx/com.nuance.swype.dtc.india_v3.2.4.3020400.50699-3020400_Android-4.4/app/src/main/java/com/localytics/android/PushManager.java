package com.localytics.android;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;
import com.localytics.android.Localytics;
import com.nuance.connect.comm.MessageAPI;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class PushManager extends BasePushManager {
    private static final String PUSH_API_URL_TEMPLATE = "https://pushapi.localytics.com/push_test?platform=android&type=prod&campaign=%s&creative=%s&token=%s&install_id=%s&client_ts=%s";
    private static final String PUSH_OPENED_EVENT = "Localytics Push Opened";
    private static final String PUSH_RECEIVED_EVENT = "Localytics Push Received";

    /* JADX INFO: Access modifiers changed from: package-private */
    public PushManager(LocalyticsDao localyticsDao, MarketingHandler marketingHandler) {
        super(localyticsDao, marketingHandler);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean _handlePushReceived(Bundle data) {
        if (this.mLocalyticsDao.areNotificationsDisabled()) {
            Localytics.Log.w("Got push notification while push is disabled.");
        } else {
            boolean allowedByListener = true;
            PushCampaign campaign = _convertToPushCampaign(data);
            if (campaign != null) {
                synchronized (this.mMarketingHandler) {
                    MessagingListener devListener = this.mMarketingHandler.mListeners.getDevListener();
                    if (devListener != null) {
                        allowedByListener = devListener.localyticsShouldShowPushNotification(campaign);
                    }
                }
                if (allowedByListener) {
                    _tagPushReceivedEvent(campaign);
                    if (_hasMessage(campaign.getMessage()) && !campaign.isControlGroup()) {
                        _showPushNotification(campaign, data);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean _tagPushReceivedEvent(Bundle data) {
        PushCampaign campaign = _convertToPushCampaign(data);
        return campaign != null && _tagPushReceivedEvent(campaign);
    }

    boolean _tagPushReceivedEvent(PushCampaign campaign) {
        long campaignId = campaign.getCampaignId();
        long creativeId = campaign.getCreativeId();
        String message = campaign.getMessage();
        String creativeType = campaign.getCreativeType();
        long serverSchemaVersion = campaign.getSchemaVersion();
        int killSwitch = campaign.getKillSwitch();
        int testMode = campaign.getTestMode();
        return _tagPushReceived(PUSH_RECEIVED_EVENT, message, campaignId, String.valueOf(creativeId), String.valueOf(serverSchemaVersion), creativeType, killSwitch, testMode, null);
    }

    void _showPushNotification(PushCampaign campaign, Bundle data) {
        String message = campaign.getMessage();
        long campaignId = campaign.getCampaignId();
        _showPushNotification(message, campaign.getSoundFilename(), campaignId, campaign, data);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void handlePushNotificationOpened(Intent intent) {
        String llString;
        if (intent != null && intent.getExtras() != null && (llString = intent.getExtras().getString("ll")) != null) {
            try {
                JSONObject llObject = new JSONObject(llString);
                String campaignId = llObject.getString("ca");
                String creativeId = llObject.getString("cr");
                String serverSchemaVersion = llObject.optString("v", "1");
                int testMode = llObject.optInt("test_mode", 0);
                String creativeType = llObject.optString("t", null);
                if (TextUtils.isEmpty(creativeType)) {
                    creativeType = "Alert";
                }
                if (campaignId != null && creativeId != null && testMode == 0) {
                    HashMap<String, String> attributes = new HashMap<>();
                    attributes.put("Campaign ID", campaignId);
                    attributes.put("Creative ID", creativeId);
                    attributes.put("Creative Type", creativeType);
                    attributes.put("Action", "Click");
                    attributes.put("Schema Version - Client", MessageAPI.DEVICE_ID);
                    attributes.put("Schema Version - Server", serverSchemaVersion);
                    this.mLocalyticsDao.tagEvent(PUSH_OPENED_EVENT, attributes);
                }
                intent.removeExtra("ll");
            } catch (JSONException e) {
                Localytics.Log.w("Failed to get campaign id or creative id from payload");
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Type inference failed for: r0v7, types: [com.localytics.android.PushManager$1] */
    public void handlePushTestMode(String[] components) {
        try {
            final String campaign = components[2];
            final String creative = components[3];
            final Future<String> customerIdFuture = this.mLocalyticsDao.getCustomerIdFuture();
            final Context appContext = this.mLocalyticsDao.getAppContext();
            if (!TextUtils.isEmpty(campaign) && !TextUtils.isEmpty(creative)) {
                new AsyncTask<Void, Void, String>() { // from class: com.localytics.android.PushManager.1
                    /* JADX INFO: Access modifiers changed from: protected */
                    @Override // android.os.AsyncTask
                    public String doInBackground(Void... params) {
                        try {
                            return PushManager.this.mLocalyticsDao.getPushRegistrationId();
                        } catch (Exception e) {
                            Localytics.Log.e("Exception while handling test mode", e);
                            return null;
                        }
                    }

                    /* JADX INFO: Access modifiers changed from: protected */
                    /* JADX WARN: Type inference failed for: r0v0, types: [com.localytics.android.PushManager$1$1] */
                    @Override // android.os.AsyncTask
                    public void onPostExecute(final String pushRegID) {
                        new AsyncTask<Void, Void, String>() { // from class: com.localytics.android.PushManager.1.1
                            @Override // android.os.AsyncTask
                            protected void onPreExecute() {
                                try {
                                    if (!TextUtils.isEmpty(pushRegID)) {
                                        Toast.makeText(appContext, "Push Test Activated\nYou should receive a notification soon.", 1).show();
                                    } else {
                                        Toast.makeText(appContext, "App isn't registered with GCM to receive push notifications. Please make sure that Localytics.registerPush(<PROJECT_ID>) has been called.", 1).show();
                                    }
                                } catch (Exception e) {
                                    Localytics.Log.e("Exception while handling test mode", e);
                                }
                            }

                            /* JADX INFO: Access modifiers changed from: protected */
                            @Override // android.os.AsyncTask
                            public String doInBackground(Void... params) {
                                try {
                                    if (!TextUtils.isEmpty(pushRegID)) {
                                        String installID = PushManager.this.mLocalyticsDao.getInstallationId();
                                        String url = String.format(PushManager.PUSH_API_URL_TEMPLATE, campaign, creative, pushRegID, installID, Long.toString(Math.round(PushManager.this.mLocalyticsDao.getCurrentTimeMillis() / 1000.0d)));
                                        HttpURLConnection connection = null;
                                        try {
                                            try {
                                                try {
                                                    try {
                                                        Proxy proxy = PushManager.this.mLocalyticsDao.getProxy();
                                                        connection = (HttpURLConnection) BaseUploadThread.createURLConnection(new URL(url), proxy);
                                                        connection.setConnectTimeout(5000);
                                                        connection.setReadTimeout(5000);
                                                        connection.setDoOutput(false);
                                                        connection.setRequestProperty("x-install-id", installID);
                                                        connection.setRequestProperty("x-app-id", DatapointHelper.getAppVersion(PushManager.this.mLocalyticsDao.getAppContext()));
                                                        connection.setRequestProperty("x-client-version", Constants.LOCALYTICS_CLIENT_LIBRARY_VERSION);
                                                        connection.setRequestProperty("x-app-version", DatapointHelper.getAppVersion(PushManager.this.mLocalyticsDao.getAppContext()));
                                                        connection.setRequestProperty("x-customer-id", (String) customerIdFuture.get());
                                                        connection.getInputStream();
                                                        if (connection != null) {
                                                            connection.disconnect();
                                                        }
                                                    } catch (InterruptedException e) {
                                                        Localytics.Log.e("Exception while handling test mode", e);
                                                        if (connection != null) {
                                                            connection.disconnect();
                                                        }
                                                    }
                                                } catch (IOException e2) {
                                                    StringBuilder errorMessageBuilder = new StringBuilder("Unfortunately, something went wrong. Push test activation was unsuccessful.");
                                                    if (Localytics.isLoggingEnabled() && (e2 instanceof FileNotFoundException)) {
                                                        errorMessageBuilder.append("\n\nCause:\nPush registration token has not yet been processed. Please wait a few minutes and try again.");
                                                        Localytics.Log.e("Activating push test has failed", e2);
                                                    }
                                                    String sb = errorMessageBuilder.toString();
                                                    if (connection == null) {
                                                        return sb;
                                                    }
                                                    connection.disconnect();
                                                    return sb;
                                                }
                                            } catch (ExecutionException e3) {
                                                Localytics.Log.e("Exception while handling test mode", e3);
                                                if (connection != null) {
                                                    connection.disconnect();
                                                }
                                            }
                                        } catch (Throwable th) {
                                            if (connection != null) {
                                                connection.disconnect();
                                            }
                                            throw th;
                                        }
                                    }
                                } catch (Exception e4) {
                                    Localytics.Log.e("Exception while handling test mode", e4);
                                }
                                return null;
                            }

                            /* JADX INFO: Access modifiers changed from: protected */
                            @Override // android.os.AsyncTask
                            public void onPostExecute(String result) {
                                if (result != null) {
                                    try {
                                        Toast.makeText(appContext, result, 1).show();
                                    } catch (Exception e) {
                                        Localytics.Log.e("Exception while handling test mode", e);
                                    }
                                }
                            }
                        }.execute(new Void[0]);
                    }
                }.execute(new Void[0]);
            }
        } catch (Exception e) {
            Localytics.Log.e("Exception while handling test mode", e);
        }
    }

    PushCampaign _convertToPushCampaign(Bundle data) {
        try {
            String message = data.getString("message");
            String soundURI = data.getString("ll_sound_filename");
            return new PushCampaign(message, soundURI, data);
        } catch (JSONException e) {
            Localytics.Log.w("Failed to parse push campaign from payload, ignoring message");
            return null;
        }
    }
}
