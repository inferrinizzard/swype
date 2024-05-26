package com.localytics.android;

import android.support.v4.app.NotificationCompat;

/* loaded from: classes.dex */
public interface MessagingListener {
    void localyticsDidDismissInAppMessage();

    void localyticsDidDisplayInAppMessage();

    boolean localyticsShouldShowPlacesPushNotification(PlacesCampaign placesCampaign);

    boolean localyticsShouldShowPushNotification(PushCampaign pushCampaign);

    void localyticsWillDismissInAppMessage();

    void localyticsWillDisplayInAppMessage();

    NotificationCompat.Builder localyticsWillShowPlacesPushNotification(NotificationCompat.Builder builder, PlacesCampaign placesCampaign);

    NotificationCompat.Builder localyticsWillShowPushNotification(NotificationCompat.Builder builder, PushCampaign pushCampaign);
}
