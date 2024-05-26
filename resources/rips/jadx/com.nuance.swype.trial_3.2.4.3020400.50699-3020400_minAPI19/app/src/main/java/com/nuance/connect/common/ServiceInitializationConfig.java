package com.nuance.connect.common;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.Arrays;

/* loaded from: classes.dex */
public class ServiceInitializationConfig implements Parcelable {
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() { // from class: com.nuance.connect.common.ServiceInitializationConfig.1
        @Override // android.os.Parcelable.Creator
        public final Object createFromParcel(Parcel parcel) {
            return new ServiceInitializationConfig(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public final Object[] newArray(int i) {
            return new ServiceInitializationConfig[i];
        }
    };
    private final boolean anonymousBuild;
    private final String apiServerUrl;
    private final String applicationId;
    private final String backgroundData;
    private final String buildPropertiesFilter;
    private final String buildPropertiesPreTosFilter;
    private final boolean collectUserProperties;
    private final int connectionLimit;
    private final String coreVersionAlpha;
    private final String coreVersionChinese;
    private final String coreVersionJapanese;
    private final String coreVersionKorean;
    private final String customerString;
    private final int defaultDelay;
    private final boolean developerLogEnabled;
    private final String foreGroundData;
    private final String legacySecretKey;
    private final String locationServerUrl;
    private final boolean locationServiceEnabled;
    private final int locationServiceLookupInterval;
    private final int logLevel;
    private final String minimumSSLProtocol;
    private final int networkTimeout;
    private final String oemId;
    private final boolean platformUpdateEnabled;
    private final boolean reportingAllowed;
    private final String requiredLegalDocuments;
    private final String version;

    /* loaded from: classes.dex */
    public static class ServiceInitializationConfigBuilder {
        private boolean anonymousBuild;
        private String apiServerUrl;
        private String applicationId;
        private String backgroundData;
        private String buildPropertiesFilter;
        private String buildPropertiesPreTosFilter;
        private boolean collectUserProperties;
        private int connectionLimit;
        private String coreVersionAlpha;
        private String coreVersionChinese;
        private String coreVersionJapanese;
        private String coreVersionKorean;
        private String customerString;
        private int defaultDelay;
        private boolean developerLogEnabled;
        private String foreGroundData;
        private String legacySecretKey;
        private String locationServerUrl;
        private boolean locationServiceEnabled;
        private int locationServiceLookupInterval;
        private int logLevel;
        private String minimumSSLProtocol;
        private int networkTimeout;
        private String oemId;
        private boolean platformUpdateEnabled;
        private boolean reportingAllowed;
        private String requiredLegalDocuments;
        private String version;

        public ServiceInitializationConfig build() {
            return new ServiceInitializationConfig(this);
        }

        public ServiceInitializationConfigBuilder setAnonymousBuild(boolean z) {
            this.anonymousBuild = z;
            return this;
        }

        public ServiceInitializationConfigBuilder setApiServerUrl(String str) {
            this.apiServerUrl = str;
            return this;
        }

        public ServiceInitializationConfigBuilder setApplicationId(String str) {
            this.applicationId = str;
            return this;
        }

        public ServiceInitializationConfigBuilder setBackgroundData(String str) {
            this.backgroundData = str;
            return this;
        }

        public ServiceInitializationConfigBuilder setBuildPropertiesFilter(String str) {
            this.buildPropertiesFilter = str;
            return this;
        }

        public ServiceInitializationConfigBuilder setBuildPropertiesPreTosFilter(String str) {
            this.buildPropertiesPreTosFilter = str;
            return this;
        }

        public ServiceInitializationConfigBuilder setCollectUserProperties(boolean z) {
            this.collectUserProperties = z;
            return this;
        }

        public ServiceInitializationConfigBuilder setConnectionLimit(int i) {
            this.connectionLimit = i;
            return this;
        }

        public ServiceInitializationConfigBuilder setCoreVersionAlpha(String str) {
            this.coreVersionAlpha = str;
            return this;
        }

        public ServiceInitializationConfigBuilder setCoreVersionChinese(String str) {
            this.coreVersionChinese = str;
            return this;
        }

        public ServiceInitializationConfigBuilder setCoreVersionJapanese(String str) {
            this.coreVersionJapanese = str;
            return this;
        }

        public ServiceInitializationConfigBuilder setCoreVersionKorean(String str) {
            this.coreVersionKorean = str;
            return this;
        }

        public ServiceInitializationConfigBuilder setCustomerString(String str) {
            this.customerString = str;
            return this;
        }

        public ServiceInitializationConfigBuilder setDefaultDelay(int i) {
            this.defaultDelay = i;
            return this;
        }

        public ServiceInitializationConfigBuilder setDeveloperLogEnabled(boolean z) {
            this.developerLogEnabled = z;
            return this;
        }

        public ServiceInitializationConfigBuilder setForeGroundData(String str) {
            this.foreGroundData = str;
            return this;
        }

        public ServiceInitializationConfigBuilder setLegacySecretKey(String str) {
            this.legacySecretKey = str;
            return this;
        }

        public ServiceInitializationConfigBuilder setLocationServerUrl(String str) {
            this.locationServerUrl = str;
            return this;
        }

        public ServiceInitializationConfigBuilder setLocationServiceEnabled(boolean z) {
            this.locationServiceEnabled = z;
            return this;
        }

        public ServiceInitializationConfigBuilder setLocationServiceLookupInterval(int i) {
            this.locationServiceLookupInterval = i;
            return this;
        }

        public ServiceInitializationConfigBuilder setLogLevel(int i) {
            this.logLevel = i;
            return this;
        }

        public ServiceInitializationConfigBuilder setMinimumSSLProtocol(String str) {
            this.minimumSSLProtocol = str;
            return this;
        }

        public ServiceInitializationConfigBuilder setNetworkTimeout(int i) {
            this.networkTimeout = i;
            return this;
        }

        public ServiceInitializationConfigBuilder setOemId(String str) {
            this.oemId = str;
            return this;
        }

        public ServiceInitializationConfigBuilder setPlatformUpdateEnabled(boolean z) {
            this.platformUpdateEnabled = z;
            return this;
        }

        public ServiceInitializationConfigBuilder setReportingAllowed(boolean z) {
            this.reportingAllowed = z;
            return this;
        }

        public ServiceInitializationConfigBuilder setRequiredLegalDocuments(String str) {
            this.requiredLegalDocuments = str;
            return this;
        }

        public ServiceInitializationConfigBuilder setVersion(String str) {
            this.version = str;
            return this;
        }
    }

    private ServiceInitializationConfig(Parcel parcel) {
        this.oemId = parcel.readString();
        this.version = parcel.readString();
        this.apiServerUrl = parcel.readString();
        this.locationServerUrl = parcel.readString();
        this.locationServiceLookupInterval = parcel.readInt();
        this.locationServiceEnabled = Boolean.parseBoolean(parcel.readString());
        this.logLevel = parcel.readInt();
        this.developerLogEnabled = Boolean.parseBoolean(parcel.readString());
        this.foreGroundData = parcel.readString();
        this.backgroundData = parcel.readString();
        this.applicationId = parcel.readString();
        this.customerString = parcel.readString();
        this.anonymousBuild = Boolean.parseBoolean(parcel.readString());
        this.reportingAllowed = Boolean.parseBoolean(parcel.readString());
        this.buildPropertiesFilter = parcel.readString();
        this.buildPropertiesPreTosFilter = parcel.readString();
        this.coreVersionAlpha = parcel.readString();
        this.coreVersionChinese = parcel.readString();
        this.coreVersionKorean = parcel.readString();
        this.coreVersionJapanese = parcel.readString();
        this.connectionLimit = parcel.readInt();
        this.networkTimeout = parcel.readInt();
        this.collectUserProperties = Boolean.parseBoolean(parcel.readString());
        this.platformUpdateEnabled = Boolean.parseBoolean(parcel.readString());
        this.defaultDelay = parcel.readInt();
        this.requiredLegalDocuments = parcel.readString();
        this.minimumSSLProtocol = parcel.readString();
        this.legacySecretKey = parcel.readString();
    }

    private ServiceInitializationConfig(ServiceInitializationConfigBuilder serviceInitializationConfigBuilder) {
        this.oemId = serviceInitializationConfigBuilder.oemId;
        this.version = serviceInitializationConfigBuilder.version;
        this.apiServerUrl = serviceInitializationConfigBuilder.apiServerUrl;
        this.locationServerUrl = serviceInitializationConfigBuilder.locationServerUrl;
        this.locationServiceLookupInterval = serviceInitializationConfigBuilder.locationServiceLookupInterval;
        this.locationServiceEnabled = serviceInitializationConfigBuilder.locationServiceEnabled;
        this.logLevel = serviceInitializationConfigBuilder.logLevel > 0 ? serviceInitializationConfigBuilder.logLevel : 6;
        this.developerLogEnabled = serviceInitializationConfigBuilder.developerLogEnabled;
        this.applicationId = serviceInitializationConfigBuilder.applicationId;
        this.customerString = serviceInitializationConfigBuilder.customerString;
        this.anonymousBuild = serviceInitializationConfigBuilder.anonymousBuild;
        this.reportingAllowed = serviceInitializationConfigBuilder.reportingAllowed;
        this.buildPropertiesFilter = serviceInitializationConfigBuilder.buildPropertiesFilter != null ? serviceInitializationConfigBuilder.buildPropertiesFilter : "";
        this.buildPropertiesPreTosFilter = serviceInitializationConfigBuilder.buildPropertiesPreTosFilter != null ? serviceInitializationConfigBuilder.buildPropertiesPreTosFilter : "";
        this.foreGroundData = serviceInitializationConfigBuilder.foreGroundData;
        this.backgroundData = serviceInitializationConfigBuilder.backgroundData;
        this.coreVersionAlpha = serviceInitializationConfigBuilder.coreVersionAlpha;
        this.coreVersionChinese = serviceInitializationConfigBuilder.coreVersionChinese;
        this.coreVersionKorean = serviceInitializationConfigBuilder.coreVersionKorean;
        this.coreVersionJapanese = serviceInitializationConfigBuilder.coreVersionJapanese;
        this.connectionLimit = serviceInitializationConfigBuilder.connectionLimit;
        this.networkTimeout = serviceInitializationConfigBuilder.networkTimeout;
        this.collectUserProperties = serviceInitializationConfigBuilder.collectUserProperties;
        this.platformUpdateEnabled = serviceInitializationConfigBuilder.platformUpdateEnabled;
        this.defaultDelay = serviceInitializationConfigBuilder.defaultDelay;
        this.requiredLegalDocuments = serviceInitializationConfigBuilder.requiredLegalDocuments;
        this.minimumSSLProtocol = serviceInitializationConfigBuilder.minimumSSLProtocol;
        this.legacySecretKey = serviceInitializationConfigBuilder.legacySecretKey;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !obj.getClass().equals(getClass())) {
            return false;
        }
        return obj.hashCode() == hashCode();
    }

    public String getApiServerUrl() {
        return this.apiServerUrl;
    }

    public String getApplicationId() {
        return this.applicationId;
    }

    public String getBackgroundData() {
        return this.backgroundData;
    }

    public String getBuildPropertiesFilter() {
        return this.buildPropertiesFilter;
    }

    public String getBuildPropertiesPreTosFilter() {
        return this.buildPropertiesPreTosFilter;
    }

    public int getConnectionLimit() {
        return this.connectionLimit;
    }

    public String getCoreVersionAlpha() {
        return this.coreVersionAlpha;
    }

    public String getCoreVersionChinese() {
        return this.coreVersionChinese;
    }

    public String getCoreVersionJapanese() {
        return this.coreVersionJapanese;
    }

    public String getCoreVersionKorean() {
        return this.coreVersionKorean;
    }

    public String getCustomerString() {
        return this.customerString;
    }

    public int getDefaultDelay() {
        return this.defaultDelay;
    }

    public String getForeGroundData() {
        return this.foreGroundData;
    }

    public String getLegacySecretKey() {
        return this.legacySecretKey;
    }

    public String getLocationServerUrl() {
        return this.locationServerUrl;
    }

    public int getLocationServiceLookupInterval() {
        return this.locationServiceLookupInterval;
    }

    public int getLogLevel() {
        return this.logLevel;
    }

    public String getMinimumSSLProtocol() {
        return this.minimumSSLProtocol;
    }

    public int getNetworkTimeout() {
        return this.networkTimeout;
    }

    public String getOemId() {
        return this.oemId;
    }

    public String getRequiredLegalDocuments() {
        return this.requiredLegalDocuments;
    }

    public String getVersion() {
        return this.version;
    }

    public int hashCode() {
        return Arrays.hashCode(new Object[]{this.oemId, this.version, this.apiServerUrl, this.locationServerUrl, Integer.valueOf(this.locationServiceLookupInterval), Boolean.valueOf(this.locationServiceEnabled), Integer.valueOf(this.logLevel), Boolean.valueOf(this.developerLogEnabled), this.foreGroundData, this.backgroundData, this.applicationId, this.customerString, Boolean.valueOf(this.anonymousBuild), Boolean.valueOf(this.reportingAllowed), this.buildPropertiesFilter, this.buildPropertiesPreTosFilter, this.coreVersionAlpha, this.coreVersionChinese, this.coreVersionKorean, this.coreVersionJapanese, Integer.valueOf(this.connectionLimit), Integer.valueOf(this.networkTimeout), Boolean.valueOf(this.collectUserProperties), Boolean.valueOf(this.platformUpdateEnabled), Integer.valueOf(this.defaultDelay), this.requiredLegalDocuments, this.minimumSSLProtocol, this.legacySecretKey});
    }

    public boolean isAnonymousBuild() {
        return this.anonymousBuild;
    }

    public boolean isCollectUserProperties() {
        return this.collectUserProperties;
    }

    public boolean isDeveloperLogEnabled() {
        return this.developerLogEnabled;
    }

    public boolean isLocationServiceEnabled() {
        return this.locationServiceEnabled;
    }

    public boolean isPlatformUpdateEnabled() {
        return this.platformUpdateEnabled;
    }

    public boolean isReportingAllowed() {
        return this.reportingAllowed;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ServiceInitializationConfig:  \n");
        sb.append("oemId: ").append(this.oemId).append("\n");
        sb.append("version: ").append(this.version).append("\n");
        sb.append("apiServerUrl: ").append(this.apiServerUrl).append("\n");
        sb.append("locationServerUrl: ").append(this.locationServerUrl).append("\n");
        sb.append("locationServiceLookupInterval: ").append(this.locationServiceLookupInterval).append("\n");
        sb.append("locationServiceEnabled: ").append(this.locationServiceEnabled).append("\n");
        sb.append("logLevel: ").append(this.logLevel).append("\n");
        sb.append("developerLogEnabled: ").append(this.developerLogEnabled).append("\n");
        sb.append("foreGroundData: ").append(this.foreGroundData).append("\n");
        sb.append("backgroundData: ").append(this.backgroundData).append("\n");
        sb.append("applicationId: ").append(this.applicationId).append("\n");
        sb.append("customerString: ").append(this.customerString).append("\n");
        sb.append("anonymousBuild: ").append(this.anonymousBuild).append("\n");
        sb.append("reportingAllowed: ").append(this.reportingAllowed).append("\n");
        sb.append("buildPropertiesFilter: ").append(this.buildPropertiesFilter).append("\n");
        sb.append("buildPropertiesPreTosFilter: ").append(this.buildPropertiesPreTosFilter).append("\n");
        sb.append("coreVersionAlpha: ").append(this.coreVersionAlpha).append("\n");
        sb.append("coreVersionChinese: ").append(this.coreVersionChinese).append("\n");
        sb.append("coreVersionKorean: ").append(this.coreVersionKorean).append("\n");
        sb.append("coreVersionJapanese: ").append(this.coreVersionJapanese).append("\n");
        sb.append("connectionLimit: ").append(this.connectionLimit).append("\n");
        sb.append("networkTimeout: ").append(this.networkTimeout).append("\n");
        sb.append("collectUserProperties: ").append(this.collectUserProperties).append("\n");
        sb.append("platformUpdateEnabled: ").append(this.platformUpdateEnabled).append("\n");
        sb.append("defaultDelay: ").append(this.defaultDelay).append("\n");
        sb.append("requiredLegalDocuments: ").append(this.requiredLegalDocuments).append("\n");
        sb.append("minimumSSLProtocol: ").append(this.minimumSSLProtocol).append("\n");
        return sb.toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.oemId);
        parcel.writeString(this.version);
        parcel.writeString(this.apiServerUrl);
        parcel.writeString(this.locationServerUrl);
        parcel.writeInt(this.locationServiceLookupInterval);
        parcel.writeString(String.valueOf(this.locationServiceEnabled));
        parcel.writeInt(this.logLevel);
        parcel.writeString(String.valueOf(this.developerLogEnabled));
        parcel.writeString(this.foreGroundData);
        parcel.writeString(this.backgroundData);
        parcel.writeString(this.applicationId);
        parcel.writeString(this.customerString);
        parcel.writeString(String.valueOf(this.anonymousBuild));
        parcel.writeString(String.valueOf(this.reportingAllowed));
        parcel.writeString(this.buildPropertiesFilter);
        parcel.writeString(this.buildPropertiesPreTosFilter);
        parcel.writeString(this.coreVersionAlpha);
        parcel.writeString(this.coreVersionChinese);
        parcel.writeString(this.coreVersionKorean);
        parcel.writeString(this.coreVersionJapanese);
        parcel.writeInt(this.connectionLimit);
        parcel.writeInt(this.networkTimeout);
        parcel.writeString(String.valueOf(this.collectUserProperties));
        parcel.writeString(String.valueOf(this.platformUpdateEnabled));
        parcel.writeInt(this.defaultDelay);
        parcel.writeString(this.requiredLegalDocuments);
        parcel.writeString(this.minimumSSLProtocol);
        parcel.writeString(this.legacySecretKey);
    }
}
