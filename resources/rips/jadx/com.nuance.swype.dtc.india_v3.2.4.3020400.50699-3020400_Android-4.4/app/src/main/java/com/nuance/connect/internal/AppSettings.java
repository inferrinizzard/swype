package com.nuance.connect.internal;

import com.nuance.connect.internal.GenericProperty;
import com.nuance.connect.internal.Property;
import com.nuance.connect.store.PersistentDataStore;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/* loaded from: classes.dex */
public final class AppSettings {
    private static final String SETTING_PREFIX = "CUSTOMER_";
    private Property<Integer> connectionConcurrentLimit;
    private Property<String> currentVersion;
    private Property<String> customPropertyString;
    private Property<String> customerString;
    private Property<String> deviceId;
    private Property<Integer> downloadIdleTimeout;
    private Property<String> initialVersion;
    private Property<Long> initializationTimestamp;
    private Property<String> lastSentLocation;
    private Property<String> minimumSSLProtocol;
    private Property<String> mostRecentLocation;
    private Property<Integer> oemLogLevel;
    private Property<Integer> refreshInterval;
    private Property<Boolean> reportingAllowed;
    private ConnectServiceManagerInternal service;
    private PersistentDataStore store;
    private Property<String> swyperId;
    private PropertyStore propertyStore = new PropertyStore();
    private Map<String, Property> customProperties = new HashMap();

    /* loaded from: classes.dex */
    public enum Key {
        CONFIGURATION_CONNECTION_CONCURRENT_LIMIT,
        DOWNLOAD_IDLE_TIMEOUT,
        CUSTOMER_STRING,
        OEM_LOG_LEVEL,
        CONFIGURATION_REFRESH_INTERVAL,
        API_DEVICE_ID,
        API_SWYPER_ID,
        INITIAL_VERSION_NUMBER,
        CURRENT_VERSION_NUMBER,
        INITIALIZATION_TIMESTAMP,
        LAST_SENT_LOCATION,
        MOST_RECENT_LOCATION,
        REPORTING_ALLOWED,
        CONFIGURATION_PROPERTIES,
        CUSTOM_PROPERTY,
        MINIMUM_SSL_PROTOCOL
    }

    public AppSettings(PersistentDataStore persistentDataStore, ConnectServiceManagerInternal connectServiceManagerInternal) {
        this.store = persistentDataStore;
        this.service = connectServiceManagerInternal;
        loadStoredSettings();
    }

    private void loadCustomProperties() {
        for (String str : Arrays.asList(this.customPropertyString.getValue().split("\\s*,\\s*"))) {
            if (str != null && str.length() > 0) {
                setConfigurationProperty(str, "", false);
            }
        }
    }

    private void loadStoredSettings() {
        this.refreshInterval = new GenericProperty.IntegerProperty(Key.CONFIGURATION_REFRESH_INTERVAL.name(), -1, this.store, 5, 0, null);
        this.propertyStore.setProperty(this.refreshInterval);
        this.connectionConcurrentLimit = new GenericProperty.IntegerProperty(Key.CONFIGURATION_CONNECTION_CONCURRENT_LIMIT.name(), 3, this.store, 1, 0, null);
        this.propertyStore.setProperty(this.connectionConcurrentLimit);
        this.downloadIdleTimeout = new GenericProperty.IntegerProperty(Key.DOWNLOAD_IDLE_TIMEOUT.name(), 120, this.store, 1, 0, null);
        this.propertyStore.setProperty(this.downloadIdleTimeout);
        this.customerString = new GenericProperty.StringProperty(Key.CUSTOMER_STRING.name(), "", this.store, 1, 0, null);
        this.propertyStore.setProperty(this.customerString);
        this.oemLogLevel = new GenericProperty.IntegerProperty(Key.OEM_LOG_LEVEL.name(), 6, this.store, 3, 0, null);
        this.propertyStore.setProperty(this.oemLogLevel);
        this.deviceId = new GenericProperty.StringProperty(Key.API_DEVICE_ID.name(), "", this.store, 1, 0, null);
        this.propertyStore.setProperty(this.deviceId);
        this.swyperId = new GenericProperty.StringProperty(Key.API_SWYPER_ID.name(), "", this.store, 1, 0, null);
        this.propertyStore.setProperty(this.swyperId);
        this.initialVersion = new GenericProperty.StringProperty(Key.INITIAL_VERSION_NUMBER.name(), null, this.store, 1, 0, null);
        this.propertyStore.setProperty(this.initialVersion);
        this.currentVersion = new GenericProperty.StringProperty(Key.CURRENT_VERSION_NUMBER.name(), null, this.store, 1, 0, null);
        this.propertyStore.setProperty(this.currentVersion);
        this.initializationTimestamp = new GenericProperty.LongProperty(Key.INITIALIZATION_TIMESTAMP.name(), Long.MIN_VALUE, this.store, 1, 0, null);
        this.propertyStore.setProperty(this.initializationTimestamp);
        this.lastSentLocation = new GenericProperty.StringProperty(Key.LAST_SENT_LOCATION.name(), null, this.store, 1, 0, null);
        this.propertyStore.setProperty(this.lastSentLocation);
        this.mostRecentLocation = new GenericProperty.StringProperty(Key.MOST_RECENT_LOCATION.name(), null, this.store, 1, 0, null);
        this.propertyStore.setProperty(this.mostRecentLocation);
        this.reportingAllowed = new GenericProperty.BooleanProperty(Key.REPORTING_ALLOWED.name(), Boolean.FALSE, this.store, 1, 0, null);
        this.propertyStore.setProperty(this.reportingAllowed);
        this.customPropertyString = new GenericProperty.StringProperty(Key.CUSTOM_PROPERTY.name(), "", this.store, 1, 0, null);
        this.propertyStore.setProperty(this.customPropertyString);
        this.minimumSSLProtocol = new GenericProperty.StringProperty(Key.MINIMUM_SSL_PROTOCOL.name(), "", null, 1, 0, null);
        this.propertyStore.setProperty(this.minimumSSLProtocol);
        loadCustomProperties();
    }

    private void writeCustomProperties() {
        StringBuilder sb = new StringBuilder();
        Iterator<Map.Entry<String, Property>> it = this.customProperties.entrySet().iterator();
        while (it.hasNext()) {
            sb.append(it.next().getKey());
            sb.append(",");
        }
        this.customPropertyString.setValue(sb.toString(), Property.Source.BUILD);
    }

    public final void deleteConfigurationProperty(String str) throws Exception {
        if (!str.startsWith(SETTING_PREFIX)) {
            str = SETTING_PREFIX + str;
        }
        if (!this.customProperties.containsKey(str)) {
            throw new Exception("Unknown Configuration");
        }
        this.customProperties.remove(str);
        writeCustomProperties();
    }

    public final String getConfigurationProperty(String str) throws Exception {
        if (!str.startsWith(SETTING_PREFIX)) {
            str = SETTING_PREFIX + str;
        }
        if (this.customProperties.containsKey(str)) {
            return (String) this.customProperties.get(str).getValue();
        }
        throw new Exception("Unknown Configuration");
    }

    public final int getConnectionConcurrentLimit() {
        return this.connectionConcurrentLimit.getValue().intValue();
    }

    public final String getCurrentVersion() {
        return this.currentVersion.getValue();
    }

    public final String getCustomerString() {
        return this.customerString.getValue();
    }

    public final String getDeviceId() {
        return this.deviceId.getValue();
    }

    public final int getDownloadIdleTimeout() {
        return this.downloadIdleTimeout.getValue().intValue();
    }

    public final String getInitialVersion() {
        return this.initialVersion.getValue();
    }

    public final Long getInitializationTimestamp() {
        return this.initializationTimestamp.getValue();
    }

    public final String getLastSentLocation() {
        return this.lastSentLocation.getValue();
    }

    public final int getLogLevel() {
        return this.oemLogLevel.getValue().intValue();
    }

    public final String getMinimumSSLProtocol() {
        return this.minimumSSLProtocol.getValue();
    }

    public final String getMostRecentLocation() {
        return this.mostRecentLocation.getValue();
    }

    public final int getRefreshInterval() {
        return this.refreshInterval.getValue().intValue();
    }

    public final String getSwyperId() {
        return this.swyperId.getValue();
    }

    public final boolean isReportingAllowed() {
        return this.reportingAllowed.getValue().booleanValue();
    }

    public final void registerSettingsListener(Key key, Property.ValueListener<?> valueListener) {
        this.propertyStore.addListener(key.name(), valueListener);
    }

    public final void registerSettingsListener(String str, Property.ValueListener<?> valueListener) {
        this.propertyStore.addListener(str, valueListener);
    }

    public final void sendCustomProperties() {
        for (Map.Entry<String, Property> entry : this.customProperties.entrySet()) {
            this.service.sendCustomProperty(entry.getKey(), (String) this.customProperties.get(entry.getKey()).getValue(), false);
        }
    }

    public final void setConfigurationProperty(String str, String str2, boolean z) {
        String str3 = !str.startsWith(SETTING_PREFIX) ? SETTING_PREFIX + str : str;
        if (this.customProperties.containsKey(str3)) {
            Property property = this.customProperties.get(str3);
            property.setValue(str2, Property.Source.BUILD);
            this.customProperties.put(str3, property);
            this.service.sendCustomProperty(property.getKey(), (String) property.getValue(), z);
        } else {
            GenericProperty.StringProperty stringProperty = new GenericProperty.StringProperty(str3, str2, this.store, 1, 0, null);
            this.propertyStore.setProperty(stringProperty);
            this.customProperties.put(str3, stringProperty);
            this.service.sendCustomProperty(stringProperty.getKey(), stringProperty.getValue(), z);
        }
        writeCustomProperties();
    }

    public final void setConnectionConcurrentLimit(int i) {
        this.connectionConcurrentLimit.setValue(Integer.valueOf(i), Property.Source.OEM_RUNTIME);
    }

    public final void setCurrentVersion(String str) {
        this.currentVersion.set(str, Property.Source.BUILD);
    }

    public final void setCustomerString(String str) {
        this.customerString.setValue(str, Property.Source.OEM_RUNTIME);
    }

    public final void setDeviceId(String str) {
        this.deviceId.setValue(str, Property.Source.USER);
    }

    public final void setDownloadIdleTimeout(int i) {
        this.downloadIdleTimeout.setValue(Integer.valueOf(i), Property.Source.OEM_RUNTIME);
    }

    public final void setInitialVersion(String str) {
        this.initialVersion.set(str, Property.Source.BUILD);
    }

    public final void setInitializationTimestamp(Long l) {
        this.initializationTimestamp.set(l, Property.Source.DEFAULT);
    }

    public final void setLastSentLocation(String str) {
        this.lastSentLocation.set(str, Property.Source.DEFAULT);
    }

    public final void setLogLevel(int i) {
        this.oemLogLevel.setValue(Integer.valueOf(i), Property.Source.OEM_RUNTIME);
    }

    public final void setMinimumSSLProtocol(String str) {
        this.minimumSSLProtocol.set(str, Property.Source.OEM_RUNTIME);
    }

    public final void setMostRecentLocation(String str) {
        this.mostRecentLocation.set(str, Property.Source.DEFAULT);
    }

    public final void setRefreshInterval(int i) {
        this.refreshInterval.setValue(Integer.valueOf(i), Property.Source.OEM_RUNTIME);
    }

    public final void setReportingAllowed(boolean z) {
        this.reportingAllowed.setValue(Boolean.valueOf(z), Property.Source.OEM_RUNTIME);
    }

    public final void setSwyperId(String str) {
        this.swyperId.setValue(str, Property.Source.USER);
    }
}
