package com.localytics.android;

import android.support.v4.app.NotificationCompat;

/* loaded from: classes.dex */
public class MessagingListenerAdapter implements MessagingListener {
    @Override // com.localytics.android.MessagingListener
    public void localyticsWillDisplayInAppMessage() {
    }

    @Override // com.localytics.android.MessagingListener
    public void localyticsDidDisplayInAppMessage() {
    }

    @Override // com.localytics.android.MessagingListener
    public void localyticsWillDismissInAppMessage() {
    }

    @Override // com.localytics.android.MessagingListener
    public void localyticsDidDismissInAppMessage() {
    }

    @Override // com.localytics.android.MessagingListener
    public boolean localyticsShouldShowPushNotification(PushCampaign campaign) {
        return true;
    }

    @Override // com.localytics.android.MessagingListener
    public boolean localyticsShouldShowPlacesPushNotification(PlacesCampaign campaign) {
        return true;
    }

    @Override // com.localytics.android.MessagingListener
    public NotificationCompat.Builder localyticsWillShowPlacesPushNotification(NotificationCompat.Builder builder, PlacesCampaign campaign) {
        return builder;
    }

    @Override // com.localytics.android.MessagingListener
    public NotificationCompat.Builder localyticsWillShowPushNotification(NotificationCompat.Builder builder, PushCampaign campaign) {
        return builder;
    }
}
