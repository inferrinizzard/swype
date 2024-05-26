package com.localytics.android;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public class CircularRegion extends Region {
    public static final Parcelable.Creator<CircularRegion> CREATOR = new Parcelable.Creator<CircularRegion>() { // from class: com.localytics.android.CircularRegion.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public final CircularRegion createFromParcel(Parcel source) {
            return new CircularRegion(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public final CircularRegion[] newArray(int size) {
            return new CircularRegion[size];
        }
    };
    private int mRadius;

    public int getRadius() {
        return this.mRadius;
    }

    protected void setRadius(int radius) {
        this.mRadius = radius;
    }

    private CircularRegion(Builder builder) {
        this.mPlaceId = builder.mPlaceId;
        this.mUniqueId = builder.mUniqueId;
        this.mLatitude = builder.mLatitude;
        this.mLongitude = builder.mLongitude;
        this.mRadius = builder.mRadius;
        this.mName = builder.mName;
        this.mType = "geofence";
        this.mAttributes = builder.mAttributes;
        this.mEnterAnalyticsEnabled = builder.mEnterAnalyticsEnabled;
        this.mExitAnalyticsEnabled = builder.mExitAnalyticsEnabled;
        this.mSchemaVersion = builder.mSchemaVersion;
    }

    public String toString() {
        return "[CircularRegion] id=" + this.mUniqueId + " | lat=" + this.mLatitude + " | lng=" + this.mLongitude + " | rad=" + this.mRadius + " | name=" + this.mName + " | attrs=" + this.mAttributes;
    }

    /* loaded from: classes.dex */
    public static class Builder {
        private boolean mEnterAnalyticsEnabled;
        private boolean mExitAnalyticsEnabled;
        private int mSchemaVersion;
        private long mPlaceId = 0;
        private String mUniqueId = null;
        private double mLatitude = 360.0d;
        private double mLongitude = 360.0d;
        private String mName = null;
        private int mRadius = 0;
        private Map<String, String> mAttributes = new HashMap();

        /* JADX INFO: Access modifiers changed from: protected */
        public Builder setPlaceId(long placeId) {
            this.mPlaceId = placeId;
            return this;
        }

        public Builder setUniqueId(String uniqueId) {
            this.mUniqueId = uniqueId;
            return this;
        }

        public Builder setLatitude(double latitude) {
            this.mLatitude = latitude;
            return this;
        }

        public Builder setLongitude(double longitude) {
            this.mLongitude = longitude;
            return this;
        }

        public Builder setName(String name) {
            this.mName = name;
            return this;
        }

        public Builder setAttributes(Map<String, String> attributes) {
            if (attributes != null && attributes.size() > 0) {
                this.mAttributes.putAll(attributes);
            }
            return this;
        }

        public Builder setRadius(int radius) {
            this.mRadius = radius;
            return this;
        }

        public Builder setEnterAnalyticsEnabled(boolean enterAnalyticsEnabled) {
            this.mEnterAnalyticsEnabled = enterAnalyticsEnabled;
            return this;
        }

        public Builder setExitAnalyticsEnabled(boolean exitAnalyticsEnabled) {
            this.mExitAnalyticsEnabled = exitAnalyticsEnabled;
            return this;
        }

        public Builder setSchemaVersion(int schemaVersion) {
            this.mSchemaVersion = schemaVersion;
            return this;
        }

        public CircularRegion build() {
            return new CircularRegion(this);
        }
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mPlaceId);
        dest.writeString(this.mUniqueId);
        dest.writeInt(this.mRadius);
        dest.writeDouble(this.mLatitude);
        dest.writeDouble(this.mLongitude);
        dest.writeString(this.mName);
        dest.writeString(this.mType);
        dest.writeByte(this.mEnterAnalyticsEnabled ? (byte) 1 : (byte) 0);
        dest.writeByte(this.mExitAnalyticsEnabled ? (byte) 1 : (byte) 0);
        dest.writeInt(this.mSchemaVersion);
        writeStringMap(dest, this.mAttributes);
    }

    private CircularRegion(Parcel in) {
        this.mPlaceId = in.readLong();
        this.mUniqueId = in.readString();
        this.mRadius = in.readInt();
        this.mLatitude = in.readDouble();
        this.mLongitude = in.readDouble();
        this.mName = in.readString();
        this.mType = in.readString();
        this.mEnterAnalyticsEnabled = in.readByte() != 0;
        this.mExitAnalyticsEnabled = in.readByte() != 0;
        this.mSchemaVersion = in.readInt();
        this.mAttributes = readStringMap(in);
    }
}
