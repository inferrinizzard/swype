package com.localytics.android;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public final class InboxCampaign extends WebViewCampaign {
    public static final Parcelable.Creator<InboxCampaign> CREATOR = new Parcelable.Creator<InboxCampaign>() { // from class: com.localytics.android.InboxCampaign.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public final InboxCampaign createFromParcel(Parcel source) {
            return new InboxCampaign(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public final InboxCampaign[] newArray(int size) {
            return new InboxCampaign[size];
        }
    };
    private Uri mCreativeUri;
    private long mInboxId;
    private Uri mLocalCreativeUri;
    private Uri mLocalThumbnailUri;
    private boolean mRead;
    private long mReceivedDate;
    private long mSortOrder;
    private String mSummary;
    private Uri mThumbnailUri;
    private String mTitle;

    private InboxCampaign() {
        throw new UnsupportedOperationException("Use InboxCampaign.Builder to create instances");
    }

    InboxCampaign(Builder builder) {
        this.mCampaignId = builder.mCampaignId;
        this.mInboxId = builder.mInboxId;
        this.mRead = builder.mRead;
        this.mRuleName = builder.mRuleName;
        this.mTitle = builder.mTitle;
        this.mSummary = builder.mSummary;
        this.mThumbnailUri = builder.mThumbnailUri;
        this.mCreativeUri = builder.mCreativeUri;
        this.mLocalThumbnailUri = builder.mLocalThumbnailUri;
        this.mLocalCreativeUri = builder.mLocalCreativeUri;
        this.mAbTest = builder.mAbTest;
        this.mSortOrder = builder.mSortOrder;
        this.mVersion = builder.mVersion;
        this.mReceivedDate = builder.mReceivedDate;
        this.mSchemaVersion = builder.mSchemaVersion;
        this.mAttributes = builder.mAttributes;
        this.mWebViewAttributes = builder.mWebViewAttributes;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final long getInboxId() {
        return this.mInboxId;
    }

    public final boolean isRead() {
        return this.mRead;
    }

    public final void setRead(boolean read) {
        if (read != this.mRead) {
            Localytics.setInboxCampaignRead(this.mCampaignId, read);
        }
        this.mRead = read;
    }

    public final String getTitle() {
        return this.mTitle;
    }

    public final long getSortOrder() {
        return this.mSortOrder;
    }

    public final Date getReceivedDate() {
        return new Date(this.mReceivedDate);
    }

    public final String getSummary() {
        return this.mSummary;
    }

    public final boolean hasThumbnail() {
        return this.mThumbnailUri != null;
    }

    public final Uri getThumbnailUri() {
        return this.mThumbnailUri;
    }

    public final boolean hasCreative() {
        return this.mLocalCreativeUri != null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final Uri getCreativeUri() {
        return this.mCreativeUri;
    }

    protected final Uri getLocalCreativeUri() {
        return this.mLocalCreativeUri;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final Uri getLocalThumbnailUri() {
        return this.mLocalThumbnailUri;
    }

    /* loaded from: classes.dex */
    static class Builder {
        long mCampaignId = 0;
        long mInboxId = 0;
        boolean mRead = false;
        String mRuleName = null;
        String mTitle = null;
        String mSummary = null;
        Uri mThumbnailUri = null;
        Uri mCreativeUri = null;
        Uri mLocalThumbnailUri = null;
        Uri mLocalCreativeUri = null;
        String mAbTest = null;
        long mVersion = 0;
        long mReceivedDate = 0;
        long mSortOrder = 0;
        long mSchemaVersion = 0;
        Map<String, String> mAttributes = new HashMap();
        Map<String, String> mWebViewAttributes = new HashMap();

        /* JADX INFO: Access modifiers changed from: package-private */
        public Builder setCampaignId(long campaignId) {
            this.mCampaignId = campaignId;
            return this;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public Builder setInboxId(long inboxId) {
            this.mInboxId = inboxId;
            return this;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public Builder setRead(boolean read) {
            this.mRead = read;
            return this;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public Builder setRuleName(String ruleName) {
            this.mRuleName = ruleName;
            return this;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public Builder setTitle(String title) {
            this.mTitle = title;
            return this;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public Builder setSummary(String summary) {
            this.mSummary = summary;
            return this;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public Builder setThumbnailUri(Uri uri) {
            this.mThumbnailUri = uri;
            return this;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public Builder setCreativeUri(Uri uri) {
            this.mCreativeUri = uri;
            return this;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public Builder setLocalThumbnailUri(Uri uri) {
            this.mLocalThumbnailUri = uri;
            return this;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public Builder setLocalCreativeUri(Uri uri) {
            this.mLocalCreativeUri = uri;
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
        public Builder setReceivedDate(long receivedDate) {
            this.mReceivedDate = receivedDate;
            return this;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public Builder setSortOrder(long sortOrder) {
            this.mSortOrder = sortOrder;
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
        public Builder setWebViewAttributes(Map<String, String> webViewAttributes) {
            if (webViewAttributes != null && webViewAttributes.size() > 0) {
                this.mWebViewAttributes.putAll(webViewAttributes);
            }
            return this;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public InboxCampaign build() {
            return new InboxCampaign(this);
        }
    }

    @Override // android.os.Parcelable
    public final int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mInboxId);
        dest.writeInt(this.mRead ? 1 : 0);
        dest.writeString(this.mTitle);
        dest.writeString(this.mSummary);
        dest.writeValue(this.mThumbnailUri);
        dest.writeValue(this.mLocalThumbnailUri);
        dest.writeValue(this.mCreativeUri);
        dest.writeValue(this.mLocalCreativeUri);
        dest.writeLong(this.mCampaignId);
        dest.writeString(this.mAbTest);
        dest.writeLong(this.mVersion);
        dest.writeLong(this.mReceivedDate);
        dest.writeLong(this.mSortOrder);
        dest.writeLong(this.mSchemaVersion);
        dest.writeString(this.mRuleName);
        dest.writeMap(this.mAttributes);
        dest.writeMap(this.mWebViewAttributes);
    }

    private InboxCampaign(Parcel in) {
        this.mInboxId = in.readLong();
        this.mRead = in.readInt() == 1;
        this.mTitle = in.readString();
        this.mSummary = in.readString();
        this.mThumbnailUri = (Uri) in.readValue(null);
        this.mLocalThumbnailUri = (Uri) in.readValue(null);
        this.mCreativeUri = (Uri) in.readValue(null);
        this.mLocalCreativeUri = (Uri) in.readValue(null);
        this.mCampaignId = in.readLong();
        this.mAbTest = in.readString();
        this.mVersion = in.readLong();
        this.mReceivedDate = in.readLong();
        this.mSortOrder = in.readLong();
        this.mSchemaVersion = in.readLong();
        this.mRuleName = in.readString();
        this.mAttributes = in.readHashMap(null);
        this.mWebViewAttributes = in.readHashMap(null);
    }
}
