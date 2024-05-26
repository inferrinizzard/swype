package com.nuance.connect.internal;

import com.nuance.connect.internal.GenericProperty;
import com.nuance.connect.internal.Property;
import com.nuance.connect.store.PersistentDataStore;
import com.nuance.connect.system.NetworkState;
import com.nuance.connect.util.StringUtils;
import java.util.Set;

/* loaded from: classes.dex */
public class UserSettings {
    public static final String BACKGROUND_DATA_STATE = "BACKGROUND_DATA_STATE";
    public static final String CATALOG_ENABLED = "CATALOG_ENABLED";
    public static final String CATALOG_LAST_SENT_STATUS = "CATALOG_LAST_SENT_STATUS";
    public static final String CATALOG_PURCHASED_SKUS = "CATALOG_PURCHASED_SKUS";
    public static final String EULA_ACCEPTED = "CONFIGURATION_EULA_ACCEPTED";
    public static final String EULA_CHANGED = "CONFIGURATION_EULA_CHANGED";
    public static final String EULA_RESET = "CONFIGURATION_EULA_RESET";
    public static final String FOREGROUND_DATA_STATE = "FOREGROUND_DATA_STATE";
    public static final String LIVING_LANGUAGE_MAX_EVENTS = "LIVING_LANGUAGE_MAX_EVENTS";
    public static final String LOCATION_COUNTRY = "LOCATION_COUNTRY";
    public static final String LOCATION_COUNTRY_TIMESTAMP = "LOCATION_COUNTRY_TIMESTAMP";
    public static final String TOS_ACCEPTED = "CONFIGURATION_TOS_ACCEPTED";
    public static final String TOS_CHANGED = "CONFIGURATION_TOS_CHANGED";
    public static final String TOS_RESET = "CONFIGURATION_TOS_RESET";
    public static final String TOS_SHOWN = "CONFIGURATION_TOS_SHOWN";
    public static final String USAGE_CHANGED = "CONFIGURATION_USAGE_CHANGED";
    public static final String USAGE_RESET = "CONFIGURATION_USAGE_RESET";
    public static final String USER_ALLOW_DATA_COLLECTION = "USER_ALLOW_USAGE_COLLECTION";
    public static final String USER_ALLOW_STATISTICS_COLLECTION = "USER_ALLOW_STATISTICS_COLLECTION";
    public static final String USER_DLM_SYNC_ACCOUNT_ENABLED = "USER_DLM_SYNC_ACCOUNT_ENABLED";
    public static final String USER_DLM_SYNC_ENABLED = "USER_DLM_SYNC_ENABLED";
    public static final String USER_LIVING_LANGUAGE_ENABLED = "USER_LIVING_LANGUAGE_ENABLED";
    private Property<String> backgroundNetworkState;
    private Property<Boolean> catalogEnabled;
    private Property<Long> catalogLastSent;
    private Property<String> catalogPurchasedSKUs;
    private Property<Boolean> dataCollection;
    private Property<Boolean> dlmSyncAccountEnabled;
    private Property<Boolean> dlmSyncEnabled;
    private Property<Boolean> eulaAccepted;
    private Property<Boolean> eulaChanged;
    private Property<String> foregroundNetworkState;
    private Property<Boolean> livingLanguageEnabled;
    private Property<Integer> livingLanguageMaxEvents;
    private Property<String> locationCountry;
    private Property<Long> locationCountryTimestamp;
    private final PropertyStore propertyStore = new PropertyStore();
    private Property<Boolean> resetEulaAccepted;
    private Property<Boolean> resetTOSAccepted;
    private Property<Boolean> resetUsageAccepted;
    private PersistentDataStore store;
    private Property<Boolean> tosAccepted;
    private Property<Boolean> tosChanged;
    private Property<Boolean> tosShown;
    private Property<Boolean> usageChanged;

    public UserSettings(PersistentDataStore persistentDataStore, boolean z, boolean z2, String str, String str2, int i) {
        this.store = persistentDataStore;
        loadStoredSettings(z, z2, str, str2, i);
    }

    private void loadStoredSettings(boolean z, boolean z2, String str, String str2, int i) {
        this.tosAccepted = new GenericProperty.BooleanProperty(TOS_ACCEPTED, false, this.store, 1, 0, null);
        this.propertyStore.setProperty(this.tosAccepted);
        this.resetTOSAccepted = new GenericProperty.BooleanProperty(TOS_RESET, false, this.store, 1, 0, null);
        this.propertyStore.setProperty(this.resetTOSAccepted);
        this.tosChanged = new GenericProperty.BooleanProperty(TOS_CHANGED, false, this.store, 1, 0, null);
        this.propertyStore.setProperty(this.tosChanged);
        this.tosShown = new GenericProperty.BooleanProperty(TOS_SHOWN, false, this.store, 1, 0, null);
        this.propertyStore.setProperty(this.tosShown);
        this.dataCollection = new GenericProperty.BooleanProperty(USER_ALLOW_DATA_COLLECTION, false, this.store, 1, 0, null);
        this.propertyStore.setProperty(this.dataCollection);
        this.eulaChanged = new GenericProperty.BooleanProperty(EULA_CHANGED, false, this.store, 1, 0, null);
        this.propertyStore.setProperty(this.eulaChanged);
        this.eulaAccepted = new GenericProperty.BooleanProperty(EULA_ACCEPTED, false, this.store, 1, 0, null);
        this.propertyStore.setProperty(this.eulaAccepted);
        this.resetEulaAccepted = new GenericProperty.BooleanProperty(EULA_RESET, false, this.store, 1, 0, null);
        this.propertyStore.setProperty(this.resetEulaAccepted);
        this.resetUsageAccepted = new GenericProperty.BooleanProperty(USAGE_RESET, false, this.store, 1, 0, null);
        this.propertyStore.setProperty(this.resetUsageAccepted);
        this.usageChanged = new GenericProperty.BooleanProperty(USAGE_CHANGED, false, this.store, 1, 0, null);
        this.propertyStore.setProperty(this.usageChanged);
        this.dlmSyncEnabled = new GenericProperty.BooleanProperty(USER_DLM_SYNC_ENABLED, Boolean.valueOf(z), this.store, 1, 0, null);
        this.propertyStore.setProperty(this.dlmSyncEnabled);
        this.dlmSyncAccountEnabled = new GenericProperty.BooleanProperty(USER_DLM_SYNC_ACCOUNT_ENABLED, false, this.store, 1, 0, null);
        this.propertyStore.setProperty(this.dlmSyncAccountEnabled);
        this.livingLanguageEnabled = new GenericProperty.BooleanProperty(USER_LIVING_LANGUAGE_ENABLED, Boolean.valueOf(z2), this.store, 1, 0, null);
        this.propertyStore.setProperty(this.livingLanguageEnabled);
        this.foregroundNetworkState = new GenericProperty.StringProperty(FOREGROUND_DATA_STATE, str, this.store, 1, 0, null);
        this.propertyStore.setProperty(this.foregroundNetworkState);
        this.backgroundNetworkState = new GenericProperty.StringProperty(BACKGROUND_DATA_STATE, str2, this.store, 1, 0, null);
        this.propertyStore.setProperty(this.backgroundNetworkState);
        this.livingLanguageMaxEvents = new GenericProperty.IntegerProperty(LIVING_LANGUAGE_MAX_EVENTS, Integer.valueOf(i), this.store, 1, 0, null);
        this.propertyStore.setProperty(this.livingLanguageMaxEvents);
        this.catalogEnabled = new GenericProperty.BooleanProperty(CATALOG_ENABLED, false, this.store, 1, 0, null);
        this.propertyStore.setProperty(this.catalogEnabled);
        this.catalogLastSent = new GenericProperty.LongProperty(CATALOG_LAST_SENT_STATUS, Long.MIN_VALUE, this.store, 1, 0, null);
        this.propertyStore.setProperty(this.catalogLastSent);
        this.catalogLastSent = new GenericProperty.LongProperty(CATALOG_LAST_SENT_STATUS, Long.MIN_VALUE, this.store, 1, 0, null);
        this.propertyStore.setProperty(this.catalogLastSent);
        this.locationCountry = new GenericProperty.StringProperty("LOCATION_COUNTRY", null, this.store, 1, 1, null);
        this.propertyStore.setProperty(this.locationCountry);
        this.locationCountryTimestamp = new GenericProperty.LongProperty(LOCATION_COUNTRY_TIMESTAMP, Long.MIN_VALUE, this.store, 1, 1, null);
        this.propertyStore.setProperty(this.locationCountryTimestamp);
        this.catalogPurchasedSKUs = new GenericProperty.StringProperty(CATALOG_PURCHASED_SKUS, "", this.store, 1, 1, null);
        this.propertyStore.setProperty(this.catalogPurchasedSKUs);
    }

    public void acceptEula() {
        this.eulaAccepted.setValue(true, Property.Source.USER);
        setEulaChanged(false);
        setResetEulaOnNextStart(false);
    }

    public void acceptUsage() {
        setDataCollectionAccepted(true);
        setUsageChanged(false);
        setResetUsageAcceptedOnNextStart(false);
    }

    public NetworkState.NetworkConfiguration getBackgroundNetworkState() {
        return NetworkState.NetworkConfiguration.fromString(this.backgroundNetworkState.getValue());
    }

    public long getCatalogLastSent() {
        return this.catalogLastSent.getValue().longValue();
    }

    public Set<String> getCatalogPurchasedSKUList() {
        return StringUtils.stringToSet(this.catalogPurchasedSKUs.getValue(), ",");
    }

    public NetworkState.NetworkConfiguration getForegroundNetworkState() {
        return NetworkState.NetworkConfiguration.fromString(this.foregroundNetworkState.getValue());
    }

    public String getLocationCountry() {
        return this.locationCountry.getValue();
    }

    public long getLocationCountryTimestamp() {
        return this.locationCountryTimestamp.getValue().longValue();
    }

    public int getMaxNumberOfEvents() {
        return this.livingLanguageMaxEvents.getValue().intValue();
    }

    public boolean isCatalogEnabled() {
        return this.catalogEnabled.getValue().booleanValue();
    }

    public boolean isDataCollectionAccepted() {
        return this.dataCollection.getValue().booleanValue();
    }

    public boolean isDlmSyncAccountEnabled() {
        return this.dlmSyncAccountEnabled.getValue().booleanValue();
    }

    public boolean isDlmSyncEnabled() {
        return this.dlmSyncEnabled.getValue().booleanValue();
    }

    public boolean isEulaAccepted() {
        return this.eulaAccepted.getValue().booleanValue();
    }

    public boolean isEulaChanged() {
        return this.eulaChanged.getValue().booleanValue();
    }

    public boolean isLivingLanguageEnabled() {
        return this.livingLanguageEnabled.getValue().booleanValue();
    }

    public boolean isResetEulaOnNextStart() {
        return this.resetEulaAccepted.getValue().booleanValue();
    }

    public boolean isResetTOSAcceptedOnNextStart() {
        return this.resetTOSAccepted.getValue().booleanValue();
    }

    public boolean isResetUsageAcceptedOnNextStart() {
        return this.resetUsageAccepted.getValue().booleanValue();
    }

    public boolean isTOSAccepted() {
        return this.tosAccepted.getValue().booleanValue();
    }

    public boolean isTosChanged() {
        return this.tosChanged.getValue().booleanValue();
    }

    public boolean isUsageChanged() {
        return this.usageChanged.getValue().booleanValue();
    }

    public void registerUserSettingsListener(String str, Property.ValueListener<?> valueListener) {
        this.propertyStore.addListener(str, valueListener);
    }

    public void resetEula() {
        this.eulaAccepted.setValue(false, Property.Source.OEM_RUNTIME);
        setResetEulaOnNextStart(false);
    }

    public void resetTOS() {
        this.tosAccepted.setValue(false, Property.Source.OEM_RUNTIME);
        setResetTOSAcceptedOnNextStart(false);
    }

    public void setBackgroundNetworkState(NetworkState.NetworkConfiguration networkConfiguration) {
        this.backgroundNetworkState.setValue(networkConfiguration.toString(), Property.Source.OEM_RUNTIME);
    }

    public void setCatalogEnabled(boolean z) {
        this.catalogEnabled.setValue(Boolean.valueOf(z), Property.Source.USER);
    }

    public void setCatalogLastSent(long j) {
        this.catalogLastSent.setValue(Long.valueOf(j), Property.Source.OEM_RUNTIME);
    }

    public void setCatalogPurchasedSKUList(Set<String> set) {
        this.catalogPurchasedSKUs.set(StringUtils.listToString(set, ","), Property.Source.USER);
    }

    public void setDataCollectionAccepted(boolean z) {
        this.dataCollection.setValue(Boolean.valueOf(z), Property.Source.USER);
    }

    public void setDlmSyncAccountEnabled(boolean z) {
        this.dlmSyncAccountEnabled.setValue(Boolean.valueOf(z), Property.Source.USER);
    }

    public void setDlmSyncEnabled(boolean z) {
        this.dlmSyncEnabled.setValue(Boolean.valueOf(z), Property.Source.USER);
    }

    public void setEulaChanged(boolean z) {
        this.eulaChanged.setValue(Boolean.valueOf(z), Property.Source.DEFAULT);
    }

    public void setForegroundNetworkState(NetworkState.NetworkConfiguration networkConfiguration) {
        this.foregroundNetworkState.setValue(networkConfiguration.toString(), Property.Source.OEM_RUNTIME);
    }

    public void setLivingLanguageEnabled(boolean z) {
        this.livingLanguageEnabled.setValue(Boolean.valueOf(z), Property.Source.USER);
    }

    public void setLocationCountry(String str) {
        this.locationCountry.set(str, Property.Source.SERVER);
    }

    public void setLocationCountryTimestamp(long j) {
        this.locationCountryTimestamp.set(Long.valueOf(j), Property.Source.SERVER);
    }

    public void setMaxNumberOfEvents(int i) {
        this.livingLanguageMaxEvents.setValue(Integer.valueOf(i), Property.Source.OEM_RUNTIME);
    }

    public void setResetEulaOnNextStart(boolean z) {
        this.resetEulaAccepted.setValue(Boolean.valueOf(z), Property.Source.DEFAULT);
        if (z) {
            setEulaChanged(true);
        }
    }

    public void setResetTOSAcceptedOnNextStart(boolean z) {
        this.resetTOSAccepted.setValue(Boolean.valueOf(z), Property.Source.DEFAULT);
        if (z) {
            setTosChanged(true);
        }
    }

    public void setResetUsageAcceptedOnNextStart(boolean z) {
        this.resetUsageAccepted.setValue(Boolean.valueOf(z), Property.Source.DEFAULT);
        if (z) {
            setUsageChanged(true);
        }
    }

    public void setTosChanged(boolean z) {
        this.tosChanged.setValue(Boolean.valueOf(z), Property.Source.DEFAULT);
    }

    public void setUsageChanged(boolean z) {
        this.usageChanged.setValue(Boolean.valueOf(z), Property.Source.DEFAULT);
    }

    public void userHasAcceptedTOS() {
        this.tosAccepted.setValue(true, Property.Source.USER);
        setResetTOSAcceptedOnNextStart(false);
        setTosChanged(false);
    }
}
