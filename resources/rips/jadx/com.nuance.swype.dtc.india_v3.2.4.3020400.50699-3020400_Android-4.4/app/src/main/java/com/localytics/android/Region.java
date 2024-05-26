package com.localytics.android;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public abstract class Region implements Parcelable {
    protected Map<String, String> mAttributes;
    protected boolean mEnterAnalyticsEnabled;
    protected boolean mExitAnalyticsEnabled;
    protected double mLatitude;
    protected double mLongitude;
    protected String mName;
    protected long mPlaceId;
    protected int mSchemaVersion;
    protected String mType;
    protected String mUniqueId;

    public boolean equals(Object o) {
        if (!(o instanceof Region)) {
            return false;
        }
        Region otherRegion = (Region) o;
        if (this.mUniqueId == null) {
            return otherRegion.mUniqueId == null;
        }
        return this.mUniqueId.equals(otherRegion.mUniqueId);
    }

    public int hashCode() {
        return this.mUniqueId == null ? super.hashCode() : this.mUniqueId.hashCode();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public long getPlaceId() {
        return this.mPlaceId;
    }

    public String getUniqueId() {
        return this.mUniqueId;
    }

    public double getLatitude() {
        return this.mLatitude;
    }

    protected void setLatitude(double latitude) {
        this.mLatitude = latitude;
    }

    public double getLongitude() {
        return this.mLongitude;
    }

    protected void setLongitude(double longitude) {
        this.mLongitude = longitude;
    }

    public String getName() {
        return this.mName;
    }

    protected void setName(String name) {
        this.mName = name;
    }

    public String getType() {
        return this.mType;
    }

    public Map<String, String> getAttributes() {
        return this.mAttributes;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isEnterAnalyticsEnabled() {
        return this.mEnterAnalyticsEnabled;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isExitAnalyticsEnabled() {
        return this.mExitAnalyticsEnabled;
    }

    protected void setEnterAnalyticsEnabled(boolean enterAnalyticsEnabled) {
        this.mEnterAnalyticsEnabled = enterAnalyticsEnabled;
    }

    protected void setExitAnalyticsEnabled(boolean exitAnalyticsEnabled) {
        this.mExitAnalyticsEnabled = exitAnalyticsEnabled;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int getSchemaVersion() {
        return this.mSchemaVersion;
    }

    protected void setSchemaVersion(int schemaVersion) {
        this.mSchemaVersion = schemaVersion;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Map<String, String> readStringMap(Parcel in) {
        int size = in.readInt();
        Map<String, String> map = new HashMap<>(size);
        for (int i = 0; i < size; i++) {
            map.put(in.readString(), in.readString());
        }
        return map;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void writeStringMap(Parcel dest, Map<String, String> map) {
        dest.writeInt(map.size());
        for (Map.Entry<String, String> entry : map.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeString(entry.getValue());
        }
    }

    /* loaded from: classes.dex */
    public enum Event {
        ENTER("enter"),
        EXIT("exit");

        String name;

        Event(String name) {
            this.name = name;
        }

        @Override // java.lang.Enum
        public final String toString() {
            return this.name;
        }
    }
}
