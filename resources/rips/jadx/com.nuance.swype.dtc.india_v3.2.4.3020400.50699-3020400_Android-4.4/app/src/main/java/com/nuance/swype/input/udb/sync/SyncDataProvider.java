package com.nuance.swype.input.udb.sync;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import com.nuance.swype.input.udb.NewWordsBucketFactory;

/* loaded from: classes.dex */
public abstract class SyncDataProvider {
    protected final NewWordsBucketFactory.NewWordsBucket bucket;
    protected final Context context;

    public abstract Uri getContentUri();

    public abstract Cursor getCursor();

    public SyncDataProvider(Context context, NewWordsBucketFactory.NewWordsBucket bucket) {
        this.bucket = bucket;
        this.context = context;
    }

    public NewWordsBucketFactory.NewWordsBucket getNewWordsBucket() {
        return this.bucket;
    }
}
