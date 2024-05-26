package com.bumptech.glide.load.data;

import android.content.ContentResolver;
import android.content.Context;
import android.content.UriMatcher;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/* loaded from: classes.dex */
public final class StreamLocalUriFetcher extends LocalUriFetcher<InputStream> {
    private static final UriMatcher URI_MATCHER;

    @Override // com.bumptech.glide.load.data.LocalUriFetcher
    protected final /* bridge */ /* synthetic */ void close(InputStream inputStream) throws IOException {
        inputStream.close();
    }

    static {
        UriMatcher uriMatcher = new UriMatcher(-1);
        URI_MATCHER = uriMatcher;
        uriMatcher.addURI("com.android.contacts", "contacts/lookup/*/#", 1);
        URI_MATCHER.addURI("com.android.contacts", "contacts/lookup/*", 1);
        URI_MATCHER.addURI("com.android.contacts", "contacts/#/photo", 2);
        URI_MATCHER.addURI("com.android.contacts", "contacts/#", 3);
        URI_MATCHER.addURI("com.android.contacts", "contacts/#/display_photo", 4);
    }

    public StreamLocalUriFetcher(Context context, Uri uri) {
        super(context, uri);
    }

    @Override // com.bumptech.glide.load.data.LocalUriFetcher
    protected final /* bridge */ /* synthetic */ InputStream loadResource(Uri x0, ContentResolver x1) throws FileNotFoundException {
        int match = URI_MATCHER.match(x0);
        switch (match) {
            case 1:
            case 3:
                if (match == 1 && (x0 = ContactsContract.Contacts.lookupContact(x1, x0)) == null) {
                    throw new FileNotFoundException("Contact cannot be found");
                }
                if (Build.VERSION.SDK_INT < 14) {
                    return ContactsContract.Contacts.openContactPhotoInputStream(x1, x0);
                }
                return ContactsContract.Contacts.openContactPhotoInputStream(x1, x0, true);
            case 2:
            default:
                return x1.openInputStream(x0);
        }
    }
}
