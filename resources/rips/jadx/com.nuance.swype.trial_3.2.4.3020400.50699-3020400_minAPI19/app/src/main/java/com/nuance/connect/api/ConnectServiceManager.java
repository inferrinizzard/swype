package com.nuance.connect.api;

import android.os.Bundle;
import com.nuance.connect.common.ConnectFeature;
import com.nuance.connect.common.FeaturesLastUsed;
import com.nuance.connect.host.service.HostInterface;
import com.nuance.connect.internal.UserSettings;
import com.nuance.connect.store.PersistentDataStore;
import com.nuance.connect.util.ActionDelegateCallback;
import com.nuance.connect.util.ActionFilter;
import com.nuance.connect.util.ConnectAction;
import java.util.List;
import java.util.Locale;

/* loaded from: classes.dex */
public interface ConnectServiceManager {
    Bundle dispatchAction(ConnectAction connectAction);

    Bundle dispatchAction(String str, String str2);

    PersistentDataStore getDataStore();

    Object getFeatureService(ConnectFeature connectFeature) throws IllegalArgumentException;

    UserSettings getUserSettings();

    boolean isLicensed();

    void notifyConnectionStatus(int i, String str);

    void onUpgrade(String str, String str2);

    void refresh(boolean z);

    void registerActionCallback(ActionDelegateCallback actionDelegateCallback, ActionFilter actionFilter);

    void registerActionCallback(ActionDelegateCallback actionDelegateCallback, List<ActionFilter> list);

    void registerConnectionCallback(ConnectionCallback connectionCallback);

    void registerLocaleCallback(LocaleCallback localeCallback);

    void retryConnection();

    void sendConnectionStatus();

    void setActiveLocale(Locale locale);

    void setHostInterface(HostInterface hostInterface);

    void start();

    void stop();

    void unregisterActionCallback(ActionDelegateCallback actionDelegateCallback);

    void unregisterConnectionCallback(ConnectionCallback connectionCallback);

    void unregisterLocaleCallback(LocaleCallback localeCallback);

    void updateFeatureLastUsed(FeaturesLastUsed.Feature feature, long j);
}
