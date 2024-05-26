package com.localytics.android;

import android.content.Context;
import java.net.Proxy;
import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.Future;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public interface LocalyticsDao {
    boolean areNotificationsDisabled();

    int getAndroidVersionInt();

    Context getAppContext();

    String getAppKey();

    Map<Integer, String> getCachedCustomDimensions();

    Map<String, String> getCachedIdentifiers();

    Calendar getCalendar();

    long getCurrentTimeMillis();

    String getCustomDimension(int i);

    Future<String> getCustomerIdFuture();

    String getInstallationId();

    Proxy getProxy();

    String getPushRegistrationId();

    String getTimeStringForSQLite();

    boolean isAppInForeground();

    boolean isAutoIntegrate();

    boolean isUsingNewCreativeLocation();

    void setCustomDimension(int i, String str);

    void setTestModeEnabled(boolean z);

    void stoppedMonitoringAllGeofences();

    void tagEvent(String str);

    void tagEvent(String str, Map<String, String> map);

    void tagEvent(String str, Map<String, String> map, long j);

    void upload();

    void useNewCreativeLocation(boolean z);
}
