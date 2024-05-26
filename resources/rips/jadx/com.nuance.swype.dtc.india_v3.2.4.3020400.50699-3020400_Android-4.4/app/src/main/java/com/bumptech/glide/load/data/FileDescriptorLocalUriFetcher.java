package com.bumptech.glide.load.data;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;

/* loaded from: classes.dex */
public final class FileDescriptorLocalUriFetcher extends LocalUriFetcher<ParcelFileDescriptor> {
    @Override // com.bumptech.glide.load.data.LocalUriFetcher
    protected final /* bridge */ /* synthetic */ void close(ParcelFileDescriptor parcelFileDescriptor) throws IOException {
        parcelFileDescriptor.close();
    }

    public FileDescriptorLocalUriFetcher(Context context, Uri uri) {
        super(context, uri);
    }

    @Override // com.bumptech.glide.load.data.LocalUriFetcher
    protected final /* bridge */ /* synthetic */ ParcelFileDescriptor loadResource(Uri x0, ContentResolver x1) throws FileNotFoundException {
        return x1.openAssetFileDescriptor(x0, "r").getParcelFileDescriptor();
    }
}
