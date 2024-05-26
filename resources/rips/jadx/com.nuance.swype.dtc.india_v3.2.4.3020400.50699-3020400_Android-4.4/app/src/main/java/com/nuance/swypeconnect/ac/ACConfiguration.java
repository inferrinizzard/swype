package com.nuance.swypeconnect.ac;

import com.nuance.connect.api.ConfigService;
import com.nuance.connect.api.ConnectServiceManager;
import com.nuance.connect.common.ConnectFeature;
import com.nuance.connect.store.PersistentDataStore;
import com.nuance.connect.system.NetworkState;
import com.nuance.connect.util.Logger;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import javax.net.ssl.SSLContext;

/* loaded from: classes.dex */
public final class ACConfiguration {
    public static final int MAX_CONNECTIONS_LIMIT = 10;
    private static final Logger.Log log = Logger.getLog(Logger.LoggerType.OEM);
    private final ConfigService config;
    private final ACManager manager;

    /* loaded from: classes.dex */
    public enum ACSSLProtocol {
        SSLv3("SSLv3"),
        TLSv1("TLSv1"),
        TLSv1_1("TLSv1.1"),
        TLSv1_2("TLSv1.2");

        private final String protocol;

        ACSSLProtocol(String str) {
            this.protocol = str;
        }
    }

    /* loaded from: classes.dex */
    private static class ConfigServiceWrapper implements ConfigService {
        private ConfigService config;
        private ConnectServiceManager connect;

        private ConfigServiceWrapper(ACManager aCManager) {
            this.connect = aCManager.getConnect();
            this.config = (ConfigService) aCManager.getConnect().getFeatureService(ConnectFeature.CONFIG);
        }

        @Override // com.nuance.connect.api.ConfigService
        public void deleteConfigurationProperty(String str) throws Exception {
            this.config.deleteConfigurationProperty(str);
        }

        @Override // com.nuance.connect.api.ConfigService
        public int[] getActiveLanguages() {
            if (this.connect.isLicensed()) {
                return this.config.getActiveLanguages();
            }
            return null;
        }

        @Override // com.nuance.connect.api.ConfigService
        public Locale getActiveLocale() {
            if (this.connect.isLicensed()) {
                return this.config.getActiveLocale();
            }
            return null;
        }

        @Override // com.nuance.connect.api.ConfigService
        public NetworkState.NetworkConfiguration getBackgroundNetworkState() {
            if (this.connect.isLicensed()) {
                return this.config.getBackgroundNetworkState();
            }
            return null;
        }

        @Override // com.nuance.connect.api.ConfigService
        public String getConfigurationProperty(String str) throws Exception {
            return this.config.getConfigurationProperty(str);
        }

        @Override // com.nuance.connect.api.ConfigService
        public int getConnectionConcurrentLimit() {
            if (this.connect.isLicensed()) {
                return this.config.getConnectionConcurrentLimit();
            }
            return 0;
        }

        @Override // com.nuance.connect.api.ConfigService
        public String getCurrentVersion() {
            if (this.connect.isLicensed()) {
                return this.config.getCurrentVersion();
            }
            return null;
        }

        @Override // com.nuance.connect.api.ConfigService
        public String getCustomExternalConfiguration(String str) throws Exception {
            return this.config.getCustomExternalConfiguration(str);
        }

        @Override // com.nuance.connect.api.ConfigService
        public String[] getCustomExternalConfigurationKeys() {
            return this.config.getCustomExternalConfigurationKeys();
        }

        @Override // com.nuance.connect.api.ConfigService
        public String getCustomerString() {
            if (this.connect.isLicensed()) {
                return this.config.getCustomerString();
            }
            return null;
        }

        @Override // com.nuance.connect.api.ConfigService
        public String getDeviceId() {
            if (this.connect.isLicensed()) {
                return this.config.getDeviceId();
            }
            return null;
        }

        @Override // com.nuance.connect.api.ConfigService
        public int getDownloadIdleTimeout() {
            if (this.connect.isLicensed()) {
                return this.config.getDownloadIdleTimeout();
            }
            return 0;
        }

        @Override // com.nuance.connect.api.ConfigService
        public NetworkState.NetworkConfiguration getForegroundNetworkState() {
            if (this.connect.isLicensed()) {
                return this.config.getForegroundNetworkState();
            }
            return null;
        }

        @Override // com.nuance.connect.api.ConfigService
        public String getInitialVersion() {
            if (this.connect.isLicensed()) {
                return this.config.getInitialVersion();
            }
            return null;
        }

        @Override // com.nuance.connect.api.ConfigService
        public Long getInitializationTimestamp() {
            if (this.connect.isLicensed()) {
                return this.config.getInitializationTimestamp();
            }
            return null;
        }

        @Override // com.nuance.connect.api.ConfigService
        public int getMinimumRefreshInterval() {
            if (this.connect.isLicensed()) {
                return this.config.getMinimumRefreshInterval();
            }
            return 0;
        }

        @Override // com.nuance.connect.api.ConfigService
        public int getRefreshInterval() {
            if (this.connect.isLicensed()) {
                return this.config.getRefreshInterval();
            }
            return 0;
        }

        @Override // com.nuance.connect.api.ConfigService
        public String getSwyperId() {
            if (this.connect.isLicensed()) {
                return this.config.getSwyperId();
            }
            return null;
        }

        @Override // com.nuance.connect.api.ConfigService
        public boolean getUsageDataState() {
            if (this.connect.isLicensed()) {
                return this.config.getUsageDataState();
            }
            return false;
        }

        @Override // com.nuance.connect.api.ConfigService
        public void setActiveLanguages(int[] iArr) {
            if (this.connect.isLicensed()) {
                this.config.setActiveLanguages(iArr);
            }
        }

        @Override // com.nuance.connect.api.ConfigService
        public void setBackgroundNetworkState(NetworkState.NetworkConfiguration networkConfiguration) {
            if (this.connect.isLicensed()) {
                this.config.setBackgroundNetworkState(networkConfiguration);
            }
        }

        @Override // com.nuance.connect.api.ConfigService
        public void setConfigurationProperty(String str, String str2, boolean z) {
            this.config.setConfigurationProperty(str, str2, z);
        }

        @Override // com.nuance.connect.api.ConfigService
        public void setConnectionConcurrentLimit(int i) {
            if (this.connect.isLicensed()) {
                this.config.setConnectionConcurrentLimit(i);
            }
        }

        @Override // com.nuance.connect.api.ConfigService
        public void setCurrentVersion(String str) {
            if (this.connect.isLicensed()) {
                this.config.setCurrentVersion(str);
            }
        }

        @Override // com.nuance.connect.api.ConfigService
        public void setCustomerString(String str) {
            if (this.connect.isLicensed()) {
                this.config.setCustomerString(str);
            }
        }

        @Override // com.nuance.connect.api.ConfigService
        public void setDownloadIdleTimeout(int i) {
            if (this.connect.isLicensed()) {
                this.config.setDownloadIdleTimeout(i);
            }
        }

        @Override // com.nuance.connect.api.ConfigService
        public void setForegroundNetworkState(NetworkState.NetworkConfiguration networkConfiguration) {
            if (this.connect.isLicensed()) {
                this.config.setForegroundNetworkState(networkConfiguration);
            }
        }

        @Override // com.nuance.connect.api.ConfigService
        public void setInitialVersion(String str) {
            if (this.connect.isLicensed()) {
                this.config.setInitialVersion(str);
            }
        }

        @Override // com.nuance.connect.api.ConfigService
        public void setInitializationTimestamp(Long l) {
            if (this.connect.isLicensed()) {
                this.config.setInitializationTimestamp(l);
            }
        }

        @Override // com.nuance.connect.api.ConfigService
        public void setLogLevel(int i) {
            if (this.connect.isLicensed()) {
                this.config.setLogLevel(i);
            }
        }

        @Override // com.nuance.connect.api.ConfigService
        public void setMinimumSSLProtocol(String str) {
            if (this.connect.isLicensed()) {
                this.config.setMinimumSSLProtocol(str);
            }
        }

        @Override // com.nuance.connect.api.ConfigService
        public void setRefreshInterval(int i) {
            if (this.connect.isLicensed()) {
                this.config.setRefreshInterval(i);
            }
        }

        @Override // com.nuance.connect.api.ConfigService
        public void setUsageDataState(boolean z) {
            if (this.connect.isLicensed()) {
                this.config.setUsageDataState(z);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ACConfiguration(ACManager aCManager, PersistentDataStore persistentDataStore) {
        this.config = new ConfigServiceWrapper(aCManager);
        this.manager = aCManager;
        try {
            setMinimumSSLProtocol(null);
        } catch (ACException e) {
            log.e(e.getMessage());
        }
    }

    public final void backgroundData(boolean z, boolean z2, boolean z3) throws ACException {
        if (!z2 && z3) {
            throw new ACException(109, "Roaming cannot be enabled when cellular is disabled");
        }
        this.config.setBackgroundNetworkState(new NetworkState.NetworkConfiguration(z, z2, z3));
    }

    protected final boolean customConfigurationNameCheck(String str) {
        return (str == null || str.length() == 0 || str.length() > 50 || str.contains(",") || str.contains(":")) ? false : true;
    }

    public final void deleteConfigurationProperty(String str) throws ACException {
        try {
            this.config.deleteConfigurationProperty(str);
        } catch (Exception e) {
            throw new ACException(133);
        }
    }

    public final void enableEnhancedPassiveLocation() {
    }

    public final void foregroundData(boolean z, boolean z2, boolean z3) throws ACException {
        if (!z2 && z3) {
            throw new ACException(109, "Roaming cannot be enabled when cellular is disabled");
        }
        this.config.setForegroundNetworkState(new NetworkState.NetworkConfiguration(z, z2, z3));
    }

    public final int getConcurrentConnectionLimit() {
        return this.config.getConnectionConcurrentLimit();
    }

    public final boolean getConfigurationPropertyBool(String str) throws ACException {
        try {
            return Boolean.valueOf(this.config.getConfigurationProperty(str)).booleanValue();
        } catch (Exception e) {
            throw new ACException(133);
        }
    }

    public final int getConfigurationPropertyInt(String str) throws ACException {
        try {
            return Integer.parseInt(this.config.getConfigurationProperty(str));
        } catch (Exception e) {
            throw new ACException(133);
        }
    }

    public final String getConfigurationPropertyString(String str) throws ACException {
        try {
            return this.config.getConfigurationProperty(str);
        } catch (Exception e) {
            throw new ACException(133);
        }
    }

    public final String getCustomCloudConfiguration(String str) throws ACException {
        try {
            return this.config.getCustomExternalConfiguration(str);
        } catch (Exception e) {
            throw new ACException(133, "Cannot find a value for " + str);
        }
    }

    public final String[] getCustomCloudConfigurationKeys() {
        return this.config.getCustomExternalConfigurationKeys();
    }

    public final String getCustomerString() {
        return this.config.getCustomerString();
    }

    public final int getDownloadIdleTimeout() {
        return this.config.getDownloadIdleTimeout();
    }

    public final int getRefreshInterval() {
        return this.config.getRefreshInterval();
    }

    public final void setConcurrentConnectionLimit(int i) throws ACException {
        if (i > 10 || i <= 0) {
            throw new ACException(113, "The max connection limit is 10. You may not set it below 0, or above the limit.");
        }
        this.config.setConnectionConcurrentLimit(i);
    }

    public final void setConfigurationProperty(String str, int i, boolean z) throws ACException {
        if (!customConfigurationNameCheck(str)) {
            throw new ACException(134);
        }
        this.config.setConfigurationProperty(str, Integer.toString(i), z);
    }

    public final void setConfigurationProperty(String str, String str2, boolean z) throws ACException {
        if (!customConfigurationNameCheck(str)) {
            throw new ACException(134);
        }
        this.config.setConfigurationProperty(str, str2, z);
    }

    public final void setConfigurationProperty(String str, boolean z, boolean z2) throws ACException {
        if (!customConfigurationNameCheck(str)) {
            throw new ACException(134);
        }
        this.config.setConfigurationProperty(str, Boolean.toString(z), z2);
    }

    public final void setCustomerString(String str) {
        this.config.setCustomerString(str);
    }

    public final void setDownloadIdleTimeout(int i) throws ACException {
        if (i < 10 && i != -1) {
            throw new ACException(112, "The download idle timeout must be either -1 or greater than 10.");
        }
        this.config.setDownloadIdleTimeout(i);
    }

    public final void setLocationServiceAllowed(boolean z) throws ACException {
        this.manager.setLocationServiceAllowed(z);
    }

    public final void setLogLevel(int i) {
        this.config.setLogLevel(i);
    }

    public final void setMinimumSSLProtocol(ACSSLProtocol aCSSLProtocol) throws ACException {
        if (this.manager.isConnectStarted()) {
            throw new ACException(109, "Cannot set the protocol after ACManager.start() is called");
        }
        if (aCSSLProtocol == null) {
            try {
                aCSSLProtocol = ACSSLProtocol.TLSv1_1;
            } catch (NoSuchAlgorithmException e) {
                log.e("Failed to initialize SSL protocol " + aCSSLProtocol);
                throw new ACException(109, "Failed to use SSL protocol: " + e.getMessage());
            }
        }
        SSLContext.getInstance(aCSSLProtocol.protocol);
        this.config.setMinimumSSLProtocol(aCSSLProtocol.protocol);
    }

    public final void setRefreshInterval(int i) throws ACException {
        if (i != 0 && i < this.config.getMinimumRefreshInterval()) {
            throw new ACException(113, "Refresh Interval cannot be less then " + this.config.getMinimumRefreshInterval());
        }
        this.config.setRefreshInterval(i);
    }
}
