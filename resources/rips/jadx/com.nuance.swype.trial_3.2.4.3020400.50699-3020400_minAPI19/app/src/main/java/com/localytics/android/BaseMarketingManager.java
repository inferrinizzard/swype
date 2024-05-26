package com.localytics.android;

/* loaded from: classes.dex */
abstract class BaseMarketingManager {
    protected LocalyticsDao mLocalyticsDao;
    protected BaseProvider mProvider;

    public BaseMarketingManager(LocalyticsDao localyticsDao) {
        this.mLocalyticsDao = localyticsDao;
    }

    public void setProvider(BaseProvider provider) {
        this.mProvider = provider;
    }
}
