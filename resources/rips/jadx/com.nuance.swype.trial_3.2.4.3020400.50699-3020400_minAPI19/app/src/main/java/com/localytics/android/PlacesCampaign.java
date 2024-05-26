package com.localytics.android;

import android.os.Parcel;
import android.os.Parcelable;
import com.localytics.android.Region;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public final class PlacesCampaign extends Campaign {
    public static final Parcelable.Creator<PlacesCampaign> CREATOR = new Parcelable.Creator<PlacesCampaign>() { // from class: com.localytics.android.PlacesCampaign.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public final PlacesCampaign createFromParcel(Parcel source) {
            return new PlacesCampaign(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public final PlacesCampaign[] newArray(int size) {
            return new PlacesCampaign[size];
        }
    };
    private boolean mControlGroup;
    private long mCreativeId;
    private String mCreativeType;
    private String mMessage;
    private Region mRegion;
    private String mSoundFilename;
    private Region.Event mTriggerEvent;

    PlacesCampaign(Builder builder) {
        this.mCampaignId = builder.mCampaignId;
        this.mCreativeId = builder.mCreativeId;
        this.mCreativeType = builder.mCreativeType;
        this.mMessage = builder.mMessage;
        this.mSoundFilename = builder.mSoundFilename;
        this.mRuleName = builder.mRuleName;
        this.mAbTest = builder.mAbTest;
        this.mRegion = builder.mRegion;
        this.mTriggerEvent = builder.mTriggerEvent;
        this.mControlGroup = builder.mControlGroup;
        this.mVersion = builder.mVersion;
        this.mSchemaVersion = builder.mSchemaVersion;
        this.mAttributes = builder.mAttributes;
    }

    public final long getCreativeId() {
        return this.mCreativeId;
    }

    public final String getCreativeType() {
        return this.mCreativeType;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void setCreativeType(String creativeType) {
        this.mCreativeType = creativeType;
    }

    public final String getMessage() {
        return this.mMessage;
    }

    public final String getSoundFilename() {
        return this.mSoundFilename;
    }

    public final Region getRegion() {
        return this.mRegion;
    }

    public final Region.Event getTriggerEvent() {
        return this.mTriggerEvent;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean isControlGroup() {
        return this.mControlGroup;
    }

    /* loaded from: classes.dex */
    static class Builder {
        String mAbTest = null;
        Map<String, String> mAttributes = new HashMap();
        long mCampaignId;
        boolean mControlGroup;
        long mCreativeId;
        String mCreativeType;
        String mMessage;
        Region mRegion;
        String mRuleName;
        long mSchemaVersion;
        String mSoundFilename;
        Region.Event mTriggerEvent;
        long mVersion;

        /* JADX INFO: Access modifiers changed from: package-private */
        public Builder setCampaignId(long campaignId) {
            this.mCampaignId = campaignId;
            return this;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public Builder setRuleName(String ruleName) {
            this.mRuleName = ruleName;
            return this;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public Builder setCreativeId(long creativeId) {
            this.mCreativeId = creativeId;
            return this;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public Builder setCreativeType(String creativeType) {
            this.mCreativeType = creativeType;
            return this;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public Builder setMessage(String message) {
            this.mMessage = message;
            return this;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public Builder setSoundFilename(String soundFilename) {
            this.mSoundFilename = soundFilename;
            return this;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public Builder setRegion(Region region) {
            this.mRegion = region;
            return this;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public Builder setTriggerEvent(Region.Event event) {
            this.mTriggerEvent = event;
            return this;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public Builder setControlGroup(boolean controlGroup) {
            this.mControlGroup = controlGroup;
            return this;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public Builder setAbTest(String abTest) {
            this.mAbTest = abTest;
            return this;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public Builder setVersion(long version) {
            this.mVersion = version;
            return this;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public Builder setSchemaVersion(long schemaVersion) {
            this.mSchemaVersion = schemaVersion;
            return this;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public Builder setAttributes(Map<String, String> attributes) {
            if (attributes != null && attributes.size() > 0) {
                this.mAttributes.putAll(attributes);
            }
            return this;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public PlacesCampaign build() {
            return new PlacesCampaign(this);
        }
    }

    @Override // android.os.Parcelable
    public final int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mCampaignId);
        dest.writeLong(this.mCreativeId);
        dest.writeString(this.mCreativeType);
        dest.writeString(this.mMessage);
        dest.writeString(this.mSoundFilename);
        dest.writeString(this.mAbTest);
        dest.writeLong(this.mVersion);
        dest.writeParcelable(this.mRegion, flags);
        dest.writeSerializable(this.mTriggerEvent);
        dest.writeInt(this.mControlGroup ? 1 : 0);
        dest.writeLong(this.mSchemaVersion);
        dest.writeMap(this.mAttributes);
    }

    private PlacesCampaign(Parcel in) {
        this.mCampaignId = in.readLong();
        this.mCreativeId = in.readLong();
        this.mCreativeType = in.readString();
        this.mMessage = in.readString();
        this.mSoundFilename = in.readString();
        this.mAbTest = in.readString();
        this.mVersion = in.readLong();
        this.mRegion = (Region) in.readParcelable(Region.class.getClassLoader());
        this.mTriggerEvent = (Region.Event) in.readSerializable();
        this.mControlGroup = in.readInt() != 0;
        this.mSchemaVersion = in.readInt();
        this.mAttributes = in.readHashMap(null);
    }
}
