package com.localytics.android;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import com.localytics.android.Localytics;
import com.nuance.connect.comm.MessageAPI;
import java.util.HashMap;
import java.util.Map;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class BasePushManager extends BaseMarketingManager {
    protected static final String ACTION_ATTRIBUTE = "Action";
    protected static final String APP_CONTEXT_ATTRIBUTE = "App Context";
    protected static final String CAMPAIGN_ID_ATTRIBUTE = "Campaign ID";
    protected static final String CREATIVE_DISPLAYED_ATTRIBUTE = "Creative Displayed";
    protected static final String CREATIVE_ID_ATTRIBUTE = "Creative ID";
    protected static final String CREATIVE_TYPE_ATTRIBUTE = "Creative Type";
    protected static final String PUSH_NOTIFICATIONS_ENABLED_ATTRIBUTE = "Push Notifications Enabled";
    protected final MarketingHandler mMarketingHandler;

    public BasePushManager(LocalyticsDao localyticsDao, MarketingHandler marketingHandler) {
        super(localyticsDao);
        this.mMarketingHandler = marketingHandler;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String _creativeTypeForMessage(String creativeType, String message) {
        if (TextUtils.isEmpty(creativeType)) {
            if (!TextUtils.isEmpty(message)) {
                return "Alert";
            }
            return "Silent";
        }
        return creativeType;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean _hasMessage(String message) {
        return !TextUtils.isEmpty(message);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean _tagPushReceived(String eventName, String message, long campaignId, String creativeId, String serverSchemaVersion, String creativeType, int killSwitch, int testMode, Map<String, String> extraAttributes) {
        String creativeDisplayed;
        boolean notificationsDisabled = this.mLocalyticsDao.areNotificationsDisabled();
        boolean hasMessage = !TextUtils.isEmpty(message);
        String creativeType2 = _creativeTypeForMessage(creativeType, message);
        String appContext = "Not Available";
        if (this.mLocalyticsDao.isAutoIntegrate()) {
            if (this.mLocalyticsDao.isAppInForeground()) {
                appContext = "Foreground";
            } else {
                appContext = "Background";
            }
        }
        if (hasMessage) {
            creativeDisplayed = notificationsDisabled ? "No" : "Yes";
        } else {
            creativeDisplayed = "Not Applicable";
        }
        HashMap<String, String> attributes = new HashMap<>();
        attributes.put(CAMPAIGN_ID_ATTRIBUTE, String.valueOf(campaignId));
        attributes.put(CREATIVE_ID_ATTRIBUTE, creativeId);
        attributes.put(CREATIVE_TYPE_ATTRIBUTE, creativeType2);
        attributes.put(CREATIVE_DISPLAYED_ATTRIBUTE, creativeDisplayed);
        attributes.put(PUSH_NOTIFICATIONS_ENABLED_ATTRIBUTE, notificationsDisabled ? "No" : "Yes");
        attributes.put(APP_CONTEXT_ATTRIBUTE, appContext);
        attributes.put("Schema Version - Client", MessageAPI.DEVICE_ID);
        attributes.put("Schema Version - Server", serverSchemaVersion);
        if (extraAttributes != null) {
            attributes.putAll(extraAttributes);
        }
        if (killSwitch == 0 && testMode == 0) {
            this.mLocalyticsDao.tagEvent(eventName, attributes);
            this.mLocalyticsDao.upload();
            return true;
        }
        return false;
    }

    private Uri _getSoundUri(String soundFilename) {
        if (TextUtils.isEmpty(soundFilename)) {
            return null;
        }
        if (soundFilename.contains(".")) {
            soundFilename = soundFilename.substring(0, soundFilename.indexOf("."));
        }
        return new Uri.Builder().scheme("android.resource").authority(this.mLocalyticsDao.getAppContext().getPackageName()).appendPath("raw").appendPath(soundFilename).build();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void _showPushNotification(String message, String soundFilename, long campaignId, Campaign campaign, Bundle extras) {
        Context appContext = this.mLocalyticsDao.getAppContext();
        CharSequence appName = "";
        int appIcon = DatapointHelper.getLocalyticsNotificationIcon(appContext);
        try {
            ApplicationInfo applicationInfo = appContext.getPackageManager().getApplicationInfo(appContext.getPackageName(), 0);
            appName = appContext.getPackageManager().getApplicationLabel(applicationInfo);
        } catch (PackageManager.NameNotFoundException e) {
            Localytics.Log.w("Failed to get application name");
        }
        Intent trackingIntent = new Intent(appContext, (Class<?>) PushTrackingActivity.class);
        trackingIntent.putExtras(extras);
        PendingIntent contentIntent = PendingIntent.getActivity(appContext, (int) campaignId, trackingIntent, 134217728);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(appContext).setSmallIcon(appIcon).setContentTitle(appName).setContentIntent(contentIntent).setAutoCancel$7abcb88d();
        Uri soundUri = _getSoundUri(soundFilename);
        if (soundUri != null) {
            builder.setSound(soundUri);
            builder.setDefaults(6);
        } else {
            builder.setDefaults(-1);
        }
        String publicMessage = extras.getString("ll_public_message");
        if (!TextUtils.isEmpty(publicMessage)) {
            builder.setPublicVersion(builder.setContentText(publicMessage).setStyle(new NotificationCompat.BigTextStyle().bigText(publicMessage)).build());
        }
        builder.setContentText(message).setStyle(new NotificationCompat.BigTextStyle().bigText(message));
        MessagingListener devListener = this.mMarketingHandler.mListeners.getDevListener();
        if (devListener != null) {
            if (campaign instanceof PlacesCampaign) {
                builder = devListener.localyticsWillShowPlacesPushNotification(builder, (PlacesCampaign) campaign);
            } else if (campaign instanceof PushCampaign) {
                builder = devListener.localyticsWillShowPushNotification(builder, (PushCampaign) campaign);
            }
        }
        NotificationManager notificationManager = (NotificationManager) appContext.getSystemService("notification");
        Notification notification = builder.build();
        if (devListener != null) {
            logNotification(notification, contentIntent);
        }
        notificationManager.notify((int) campaignId, notification);
    }

    private void logNotification(Notification notification, PendingIntent contentIntent) {
        Object[] objArr = new Object[1];
        objArr[0] = contentIntent.equals(notification.contentIntent) ? "the same" : "a different";
        Localytics.Log.v(String.format("The notification returned by the user contains %s content intent", objArr));
        Localytics.Log.v(notification.toString());
    }
}
