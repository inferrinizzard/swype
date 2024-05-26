package com.localytics.android;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class InAppCampaign extends WebViewCampaign {
    public static final Parcelable.Creator<InAppCampaign> CREATOR = new Parcelable.Creator<InAppCampaign>() { // from class: com.localytics.android.InAppCampaign.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public final InAppCampaign createFromParcel(Parcel source) {
            return new InAppCampaign(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public final InAppCampaign[] newArray(int size) {
            return new InAppCampaign[size];
        }
    };
    private String mDisplayLocation;
    private Map<String, String> mEventAttributes;

    private InAppCampaign() {
        throw new UnsupportedOperationException("Use InAppCampaign.Builder to create instances");
    }

    InAppCampaign(Builder builder) {
        this.mCampaignId = builder.mCampaignId;
        this.mVersion = builder.mVersion;
        this.mSchemaVersion = builder.mSchemaVersion;
        this.mAbTest = builder.mAbTest;
        this.mRuleName = builder.mRuleName;
        this.mDisplayLocation = builder.mDisplayLocation;
        this.mEventAttributes = builder.mEventAttributes;
        this.mAttributes = builder.mAttributes;
        this.mWebViewAttributes = builder.mWebViewAttributes;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final String getDisplayLocation() {
        return this.mDisplayLocation;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Map<String, String> getEventAttributes() {
        return this.mEventAttributes;
    }

    /* loaded from: classes.dex */
    static class Builder {
        long mCampaignId = 0;
        long mVersion = 0;
        long mSchemaVersion = 0;
        String mAbTest = null;
        String mRuleName = null;
        String mDisplayLocation = null;
        Map<String, String> mEventAttributes = new TreeMap();
        Map<String, String> mAttributes = new HashMap();
        Map<String, String> mWebViewAttributes = new HashMap();

        /* JADX INFO: Access modifiers changed from: package-private */
        public Builder setCampaignId(long campaignId) {
            this.mCampaignId = campaignId;
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
        public Builder setRuleName(String ruleName) {
            this.mRuleName = ruleName;
            return this;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public Builder setDisplayLocation(String location) {
            this.mDisplayLocation = location;
            return this;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public Builder setAbTest(String abTest) {
            this.mAbTest = abTest;
            return this;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public Builder setEventAttributes(Map<String, String> attributes) {
            if (attributes != null && attributes.size() > 0) {
                this.mEventAttributes.putAll(attributes);
            }
            return this;
        }

        Builder setAttributes(Map<String, String> attributes) {
            if (attributes != null && attributes.size() > 0) {
                this.mAttributes.putAll(attributes);
            }
            return this;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public Builder setWebViewAttributes(Map<String, String> webViewAttributes) {
            if (webViewAttributes != null && webViewAttributes.size() > 0) {
                this.mWebViewAttributes.putAll(webViewAttributes);
            }
            return this;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public InAppCampaign build() {
            return new InAppCampaign(this);
        }
    }

    @Override // android.os.Parcelable
    public final int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mDisplayLocation);
        dest.writeString(this.mAbTest);
        dest.writeString(this.mRuleName);
        dest.writeMap(this.mEventAttributes);
        dest.writeLong(this.mCampaignId);
        dest.writeLong(this.mVersion);
        dest.writeLong(this.mSchemaVersion);
        dest.writeMap(this.mAttributes);
        dest.writeMap(this.mWebViewAttributes);
    }

    private InAppCampaign(Parcel in) {
        this.mDisplayLocation = in.readString();
        this.mAbTest = in.readString();
        this.mRuleName = in.readString();
        this.mEventAttributes = in.readHashMap(null);
        this.mCampaignId = in.readInt();
        this.mVersion = in.readInt();
        this.mSchemaVersion = in.readInt();
        this.mAttributes = in.readHashMap(null);
        this.mWebViewAttributes = in.readHashMap(null);
    }
}
