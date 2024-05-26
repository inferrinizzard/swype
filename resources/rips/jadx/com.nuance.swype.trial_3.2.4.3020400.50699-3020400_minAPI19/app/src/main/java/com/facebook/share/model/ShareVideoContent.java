package com.facebook.share.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.facebook.share.model.ShareContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.ShareVideo;

/* loaded from: classes.dex */
public final class ShareVideoContent extends ShareContent<ShareVideoContent, Builder> implements ShareModel {
    public static final Parcelable.Creator<ShareVideoContent> CREATOR = new Parcelable.Creator<ShareVideoContent>() { // from class: com.facebook.share.model.ShareVideoContent.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public final ShareVideoContent createFromParcel(Parcel in) {
            return new ShareVideoContent(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public final ShareVideoContent[] newArray(int size) {
            return new ShareVideoContent[size];
        }
    };
    private final String contentDescription;
    private final String contentTitle;
    private final SharePhoto previewPhoto;
    private final ShareVideo video;

    private ShareVideoContent(Builder builder) {
        super(builder);
        this.contentDescription = builder.contentDescription;
        this.contentTitle = builder.contentTitle;
        this.previewPhoto = builder.previewPhoto;
        this.video = builder.video;
    }

    ShareVideoContent(Parcel in) {
        super(in);
        this.contentDescription = in.readString();
        this.contentTitle = in.readString();
        SharePhoto.Builder previewPhotoBuilder = new SharePhoto.Builder().readFrom(in);
        if (previewPhotoBuilder.getImageUrl() != null || previewPhotoBuilder.getBitmap() != null) {
            this.previewPhoto = previewPhotoBuilder.build();
        } else {
            this.previewPhoto = null;
        }
        this.video = new ShareVideo.Builder().readFrom(in).build();
    }

    public final String getContentDescription() {
        return this.contentDescription;
    }

    public final String getContentTitle() {
        return this.contentTitle;
    }

    public final SharePhoto getPreviewPhoto() {
        return this.previewPhoto;
    }

    public final ShareVideo getVideo() {
        return this.video;
    }

    @Override // com.facebook.share.model.ShareContent, android.os.Parcelable
    public final int describeContents() {
        return 0;
    }

    @Override // com.facebook.share.model.ShareContent, android.os.Parcelable
    public final void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeString(this.contentDescription);
        out.writeString(this.contentTitle);
        out.writeParcelable(this.previewPhoto, 0);
        out.writeParcelable(this.video, 0);
    }

    /* loaded from: classes.dex */
    public static final class Builder extends ShareContent.Builder<ShareVideoContent, Builder> {
        private String contentDescription;
        private String contentTitle;
        private SharePhoto previewPhoto;
        private ShareVideo video;

        public final Builder setContentDescription(String contentDescription) {
            this.contentDescription = contentDescription;
            return this;
        }

        public final Builder setContentTitle(String contentTitle) {
            this.contentTitle = contentTitle;
            return this;
        }

        public final Builder setPreviewPhoto(SharePhoto previewPhoto) {
            this.previewPhoto = previewPhoto == null ? null : new SharePhoto.Builder().readFrom(previewPhoto).build();
            return this;
        }

        public final Builder setVideo(ShareVideo video) {
            if (video != null) {
                this.video = new ShareVideo.Builder().readFrom(video).build();
            }
            return this;
        }

        @Override // com.facebook.share.ShareBuilder
        public final ShareVideoContent build() {
            return new ShareVideoContent(this);
        }

        @Override // com.facebook.share.model.ShareContent.Builder, com.facebook.share.model.ShareModelBuilder
        public final Builder readFrom(ShareVideoContent model) {
            return model == null ? this : ((Builder) super.readFrom((Builder) model)).setContentDescription(model.getContentDescription()).setContentTitle(model.getContentTitle()).setPreviewPhoto(model.getPreviewPhoto()).setVideo(model.getVideo());
        }
    }
}
