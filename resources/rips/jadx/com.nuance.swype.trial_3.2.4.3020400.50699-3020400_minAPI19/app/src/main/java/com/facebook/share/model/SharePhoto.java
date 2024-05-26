package com.facebook.share.model;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import com.facebook.share.model.ShareMedia;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public final class SharePhoto extends ShareMedia {
    public static final Parcelable.Creator<SharePhoto> CREATOR = new Parcelable.Creator<SharePhoto>() { // from class: com.facebook.share.model.SharePhoto.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public final SharePhoto createFromParcel(Parcel source) {
            return new SharePhoto(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public final SharePhoto[] newArray(int size) {
            return new SharePhoto[size];
        }
    };
    private final Bitmap bitmap;
    private final String caption;
    private final Uri imageUrl;
    private final boolean userGenerated;

    private SharePhoto(Builder builder) {
        super(builder);
        this.bitmap = builder.bitmap;
        this.imageUrl = builder.imageUrl;
        this.userGenerated = builder.userGenerated;
        this.caption = builder.caption;
    }

    SharePhoto(Parcel in) {
        super(in);
        this.bitmap = (Bitmap) in.readParcelable(Bitmap.class.getClassLoader());
        this.imageUrl = (Uri) in.readParcelable(Uri.class.getClassLoader());
        this.userGenerated = in.readByte() != 0;
        this.caption = in.readString();
    }

    public final Bitmap getBitmap() {
        return this.bitmap;
    }

    public final Uri getImageUrl() {
        return this.imageUrl;
    }

    public final boolean getUserGenerated() {
        return this.userGenerated;
    }

    public final String getCaption() {
        return this.caption;
    }

    @Override // com.facebook.share.model.ShareMedia, android.os.Parcelable
    public final int describeContents() {
        return 0;
    }

    @Override // com.facebook.share.model.ShareMedia, android.os.Parcelable
    public final void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeParcelable(this.bitmap, 0);
        out.writeParcelable(this.imageUrl, 0);
        out.writeByte((byte) (this.userGenerated ? 1 : 0));
        out.writeString(this.caption);
    }

    @Override // com.facebook.share.model.ShareMedia
    public final ShareMedia.Type getMediaType() {
        return ShareMedia.Type.PHOTO;
    }

    /* loaded from: classes.dex */
    public static final class Builder extends ShareMedia.Builder<SharePhoto, Builder> {
        private Bitmap bitmap;
        private String caption;
        private Uri imageUrl;
        private boolean userGenerated;

        public final Builder setBitmap(Bitmap bitmap) {
            this.bitmap = bitmap;
            return this;
        }

        public final Builder setImageUrl(Uri imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public final Builder setUserGenerated(boolean userGenerated) {
            this.userGenerated = userGenerated;
            return this;
        }

        public final Builder setCaption(String caption) {
            this.caption = caption;
            return this;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public final Uri getImageUrl() {
            return this.imageUrl;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public final Bitmap getBitmap() {
            return this.bitmap;
        }

        @Override // com.facebook.share.ShareBuilder
        public final SharePhoto build() {
            return new SharePhoto(this);
        }

        @Override // com.facebook.share.model.ShareMedia.Builder, com.facebook.share.model.ShareModelBuilder
        public final Builder readFrom(SharePhoto model) {
            return model == null ? this : ((Builder) super.readFrom((Builder) model)).setBitmap(model.getBitmap()).setImageUrl(model.getImageUrl()).setUserGenerated(model.getUserGenerated()).setCaption(model.getCaption());
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public final Builder readFrom(Parcel parcel) {
            return readFrom((SharePhoto) parcel.readParcelable(SharePhoto.class.getClassLoader()));
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static void writePhotoListTo(Parcel out, int parcelFlags, List<SharePhoto> photos) {
            ShareMedia[] array = new ShareMedia[photos.size()];
            for (int i = 0; i < photos.size(); i++) {
                array[i] = photos.get(i);
            }
            out.writeParcelableArray(array, parcelFlags);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static List<SharePhoto> readPhotoListFrom(Parcel in) {
            List<ShareMedia> media = readListFrom(in);
            List<SharePhoto> photos = new ArrayList<>();
            for (ShareMedia medium : media) {
                if (medium instanceof SharePhoto) {
                    photos.add((SharePhoto) medium);
                }
            }
            return photos;
        }
    }
}
