package com.facebook.share.model;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public abstract class ShareMedia implements ShareModel {
    private final Bundle params;

    /* loaded from: classes.dex */
    public enum Type {
        PHOTO,
        VIDEO
    }

    public abstract Type getMediaType();

    /* JADX INFO: Access modifiers changed from: protected */
    public ShareMedia(Builder builder) {
        this.params = new Bundle(builder.params);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ShareMedia(Parcel in) {
        this.params = in.readBundle();
    }

    @Deprecated
    public Bundle getParameters() {
        return new Bundle(this.params);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeBundle(this.params);
    }

    /* loaded from: classes.dex */
    public static abstract class Builder<M extends ShareMedia, B extends Builder> implements ShareModelBuilder<M, B> {
        private Bundle params = new Bundle();

        @Deprecated
        public B setParameter(String key, String value) {
            this.params.putString(key, value);
            return this;
        }

        @Deprecated
        public B setParameters(Bundle parameters) {
            this.params.putAll(parameters);
            return this;
        }

        @Override // com.facebook.share.model.ShareModelBuilder
        public B readFrom(M model) {
            return model == null ? this : setParameters(model.getParameters());
        }

        static void writeListTo(Parcel out, int parcelFlags, List<ShareMedia> media) {
            out.writeParcelableArray((ShareMedia[]) media.toArray(), parcelFlags);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static List<ShareMedia> readListFrom(Parcel in) {
            Parcelable[] parcelables = in.readParcelableArray(ShareMedia.class.getClassLoader());
            List<ShareMedia> shareMedia = new ArrayList<>(parcelables.length);
            for (Parcelable parcelable : parcelables) {
                shareMedia.add((ShareMedia) parcelable);
            }
            return shareMedia;
        }
    }
}
