package com.nuance.swype.input.udb.sync;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.UserDictionary;
import com.nuance.swype.input.udb.NewWordsBucketFactory;

/* loaded from: classes.dex */
public class AndroidUserDictionarySyncDataProvider extends SyncDataProvider {
    public AndroidUserDictionarySyncDataProvider(Context context, NewWordsBucketFactory.NewWordsBucket bucket) {
        super(context, bucket);
    }

    @Override // com.nuance.swype.input.udb.sync.SyncDataProvider
    public Uri getContentUri() {
        return UserDictionary.Words.CONTENT_URI;
    }

    @Override // com.nuance.swype.input.udb.sync.SyncDataProvider
    public Cursor getCursor() {
        return this.context.getContentResolver().query(UserDictionary.Words.CONTENT_URI, new String[]{"word"}, null, null, null);
    }
}
