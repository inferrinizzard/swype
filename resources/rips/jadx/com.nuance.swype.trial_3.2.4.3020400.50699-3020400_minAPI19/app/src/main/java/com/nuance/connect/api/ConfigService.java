package com.nuance.connect.api;

import com.nuance.connect.system.NetworkState;
import java.util.Locale;

/* loaded from: classes.dex */
public interface ConfigService {

    /* loaded from: classes.dex */
    public interface ActiveNetworkState {
        boolean getCellularState();

        boolean getRoamingState();

        boolean getWifiState();

        void setNetworkState(boolean z, boolean z2, boolean z3);
    }

    void deleteConfigurationProperty(String str) throws Exception;

    int[] getActiveLanguages();

    Locale getActiveLocale();

    NetworkState.NetworkConfiguration getBackgroundNetworkState();

    String getConfigurationProperty(String str) throws Exception;

    int getConnectionConcurrentLimit();

    String getCurrentVersion();

    String getCustomExternalConfiguration(String str) throws Exception;

    String[] getCustomExternalConfigurationKeys();

    String getCustomerString();

    String getDeviceId();

    int getDownloadIdleTimeout();

    NetworkState.NetworkConfiguration getForegroundNetworkState();

    String getInitialVersion();

    Long getInitializationTimestamp();

    int getMinimumRefreshInterval();

    int getRefreshInterval();

    String getSwyperId();

    boolean getUsageDataState();

    void setActiveLanguages(int[] iArr);

    void setBackgroundNetworkState(NetworkState.NetworkConfiguration networkConfiguration);

    void setConfigurationProperty(String str, String str2, boolean z);

    void setConnectionConcurrentLimit(int i);

    void setCurrentVersion(String str);

    void setCustomerString(String str);

    void setDownloadIdleTimeout(int i);

    void setForegroundNetworkState(NetworkState.NetworkConfiguration networkConfiguration);

    void setInitialVersion(String str);

    void setInitializationTimestamp(Long l);

    void setLogLevel(int i);

    void setMinimumSSLProtocol(String str);

    void setRefreshInterval(int i);

    void setUsageDataState(boolean z);
}
