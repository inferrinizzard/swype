package com.facebook.share.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.facebook.share.model.ShareContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.ShareVideo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/* loaded from: classes.dex */
public final class ShareMediaContent extends ShareContent<ShareMediaContent, Builder> {
    public static final Parcelable.Creator<ShareMediaContent> CREATOR = new Parcelable.Creator<ShareMediaContent>() { // from class: com.facebook.share.model.ShareMediaContent.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public final ShareMediaContent createFromParcel(Parcel in) {
            return new ShareMediaContent(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public final ShareMediaContent[] newArray(int size) {
            return new ShareMediaContent[size];
        }
    };
    private final List<ShareMedia> media;

    private ShareMediaContent(Builder builder) {
        super(builder);
        this.media = Collections.unmodifiableList(builder.media);
    }

    ShareMediaContent(Parcel in) {
        super(in);
        ShareMedia[] shareMedia = (ShareMedia[]) in.readParcelableArray(ShareMedia.class.getClassLoader());
        this.media = Arrays.asList(shareMedia);
    }

    public final List<ShareMedia> getMedia() {
        return this.media;
    }

    @Override // com.facebook.share.model.ShareContent, android.os.Parcelable
    public final int describeContents() {
        return 0;
    }

    @Override // com.facebook.share.model.ShareContent, android.os.Parcelable
    public final void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeParcelableArray((ShareMedia[]) this.media.toArray(), flags);
    }

    /* loaded from: classes.dex */
    public static class Builder extends ShareContent.Builder<ShareMediaContent, Builder> {
        private final List<ShareMedia> media = new ArrayList();

        public Builder addMedium(ShareMedia medium) {
            ShareMedia mediumToAdd;
            if (medium != null) {
                if (medium instanceof SharePhoto) {
                    mediumToAdd = new SharePhoto.Builder().readFrom((SharePhoto) medium).build();
                } else if (medium instanceof ShareVideo) {
                    mediumToAdd = new ShareVideo.Builder().readFrom((ShareVideo) medium).build();
                } else {
                    throw new IllegalArgumentException("medium must be either a SharePhoto or ShareVideo");
                }
                this.media.add(mediumToAdd);
            }
            return this;
        }

        public Builder addMedia(List<ShareMedia> media) {
            if (media != null) {
                for (ShareMedia medium : media) {
                    addMedium(medium);
                }
            }
            return this;
        }

        @Override // com.facebook.share.ShareBuilder
        public ShareMediaContent build() {
            return new ShareMediaContent(this);
        }

        @Override // com.facebook.share.model.ShareContent.Builder, com.facebook.share.model.ShareModelBuilder
        public Builder readFrom(ShareMediaContent model) {
            return model == null ? this : ((Builder) super.readFrom((Builder) model)).addMedia(model.getMedia());
        }

        public Builder setMedia(List<ShareMedia> media) {
            this.media.clear();
            addMedia(media);
            return this;
        }
    }
}
