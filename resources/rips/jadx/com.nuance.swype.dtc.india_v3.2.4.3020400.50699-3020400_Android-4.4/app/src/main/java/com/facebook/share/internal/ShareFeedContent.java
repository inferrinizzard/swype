package com.facebook.share.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.facebook.share.model.ShareContent;

/* loaded from: classes.dex */
public class ShareFeedContent extends ShareContent<ShareFeedContent, Builder> {
    public static final Parcelable.Creator<ShareFeedContent> CREATOR = new Parcelable.Creator<ShareFeedContent>() { // from class: com.facebook.share.internal.ShareFeedContent.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public final ShareFeedContent createFromParcel(Parcel in) {
            return new ShareFeedContent(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public final ShareFeedContent[] newArray(int size) {
            return new ShareFeedContent[size];
        }
    };
    private final String link;
    private final String linkCaption;
    private final String linkDescription;
    private final String linkName;
    private final String mediaSource;
    private final String picture;
    private final String toId;

    private ShareFeedContent(Builder builder) {
        super(builder);
        this.toId = builder.toId;
        this.link = builder.link;
        this.linkName = builder.linkName;
        this.linkCaption = builder.linkCaption;
        this.linkDescription = builder.linkDescription;
        this.picture = builder.picture;
        this.mediaSource = builder.mediaSource;
    }

    ShareFeedContent(Parcel in) {
        super(in);
        this.toId = in.readString();
        this.link = in.readString();
        this.linkName = in.readString();
        this.linkCaption = in.readString();
        this.linkDescription = in.readString();
        this.picture = in.readString();
        this.mediaSource = in.readString();
    }

    public String getToId() {
        return this.toId;
    }

    public String getLink() {
        return this.link;
    }

    public String getLinkName() {
        return this.linkName;
    }

    public String getLinkCaption() {
        return this.linkCaption;
    }

    public String getLinkDescription() {
        return this.linkDescription;
    }

    public String getPicture() {
        return this.picture;
    }

    public String getMediaSource() {
        return this.mediaSource;
    }

    @Override // com.facebook.share.model.ShareContent, android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // com.facebook.share.model.ShareContent, android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeString(this.toId);
        out.writeString(this.link);
        out.writeString(this.linkName);
        out.writeString(this.linkCaption);
        out.writeString(this.linkDescription);
        out.writeString(this.picture);
        out.writeString(this.mediaSource);
    }

    /* loaded from: classes.dex */
    public static final class Builder extends ShareContent.Builder<ShareFeedContent, Builder> {
        private String link;
        private String linkCaption;
        private String linkDescription;
        private String linkName;
        private String mediaSource;
        private String picture;
        private String toId;

        public final Builder setToId(String toId) {
            this.toId = toId;
            return this;
        }

        public final Builder setLink(String link) {
            this.link = link;
            return this;
        }

        public final Builder setLinkName(String linkName) {
            this.linkName = linkName;
            return this;
        }

        public final Builder setLinkCaption(String linkCaption) {
            this.linkCaption = linkCaption;
            return this;
        }

        public final Builder setLinkDescription(String linkDescription) {
            this.linkDescription = linkDescription;
            return this;
        }

        public final Builder setPicture(String picture) {
            this.picture = picture;
            return this;
        }

        public final Builder setMediaSource(String mediaSource) {
            this.mediaSource = mediaSource;
            return this;
        }

        @Override // com.facebook.share.ShareBuilder
        public final ShareFeedContent build() {
            return new ShareFeedContent(this);
        }

        @Override // com.facebook.share.model.ShareContent.Builder, com.facebook.share.model.ShareModelBuilder
        public final Builder readFrom(ShareFeedContent model) {
            return model == null ? this : ((Builder) super.readFrom((Builder) model)).setToId(model.getToId()).setLink(model.getLink()).setLinkName(model.getLinkName()).setLinkCaption(model.getLinkCaption()).setLinkDescription(model.getLinkDescription()).setPicture(model.getPicture()).setMediaSource(model.getMediaSource());
        }
    }
}
