package com.nuance.swype.input.udb.sync;

import android.content.ContentUris;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.R;
import com.nuance.swype.input.udb.NewWordsBucketFactory;
import com.nuance.swype.util.LogManager;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/* loaded from: classes.dex */
public class SyncDataProviderManager {
    private static final int MAX_WORD_LENGTH = 32;
    private static final int MIN_WORD_LENGTH = 1;
    private static final long SYNC_DELAY_IN_SECONDS = 30;
    private Context appContext;
    private ScheduledThreadPoolExecutor mExecutor = new ScheduledThreadPoolExecutor(1);
    private List<ContentObserver> observers = new ArrayList();
    private Set<SyncDataProvider> scheduled = new HashSet();
    static final LogManager.Log log = LogManager.getLog("SyncDataProviderManager");
    private static final String[] PROJECTION_QUERY = {"word", "shortcut", "frequency", "locale", "appid"};
    private static long timeOfLastUpdate = 0;

    public void addProvider(Context context, SyncDataProvider provider) {
        addProviderInternal(context, provider);
    }

    private void addProviderInternal(Context context, SyncDataProvider provider) {
        ProviderObserver observer = new ProviderObserver(null, provider);
        this.observers.add(observer);
        context.getContentResolver().registerContentObserver(provider.getContentUri(), true, observer);
        scheduleSync(provider, SYNC_DELAY_IN_SECONDS, TimeUnit.SECONDS);
    }

    public void initialize(Context context) {
        this.appContext = context;
    }

    public void deinitialize(Context context) {
        if (this.mExecutor != null) {
            this.mExecutor.shutdownNow();
        }
        this.scheduled.clear();
        for (ContentObserver observer : this.observers) {
            context.getContentResolver().unregisterContentObserver(observer);
        }
        this.observers.clear();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void scheduleSync(SyncDataProvider provider, long delay, TimeUnit timeUnit) {
        if (!this.scheduled.contains(provider)) {
            log.d("scheduleSync(): scheduling: " + provider.getClass().getSimpleName());
            this.scheduled.add(provider);
            this.mExecutor.schedule(new ProviderRunnable(provider), delay, timeUnit);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void syncAndroidDictionary(SyncDataProvider provider, Uri uri) {
        Cursor cursor;
        NewWordsBucketFactory.NewWordsBucket bucket = provider.getNewWordsBucket();
        long changedRowId = ContentUris.parseId(uri);
        if (-1 != changedRowId && (cursor = this.appContext.getContentResolver().query(uri, PROJECTION_QUERY, null, null, null)) != null) {
            try {
                if (cursor.moveToFirst()) {
                    int indexWord = cursor.getColumnIndex("word");
                    while (!cursor.isAfterLast()) {
                        String newWord = cursor.getString(indexWord);
                        int wordLen = newWord.length();
                        if (wordLen >= 32 || wordLen <= 1) {
                            cursor.close();
                            return;
                        } else {
                            bucket.add(newWord);
                            cursor.moveToNext();
                        }
                    }
                }
                IMEApplication.from(this.appContext).notifyNewWordsForScanning(bucket);
            } finally {
                cursor.close();
            }
        }
    }

    protected void syncCursor(Cursor cursor, NewWordsBucketFactory.NewWordsBucket bucket) {
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(0);
                if (name != null) {
                    int len = name.length();
                    int i = 0;
                    while (i < len) {
                        if (Character.isLetter(name.charAt(i))) {
                            int j = i + 1;
                            while (j < len) {
                                char c = name.charAt(j);
                                if (c != '-' && c != '\'' && !Character.isLetter(c)) {
                                    break;
                                } else {
                                    j++;
                                }
                            }
                            String word = name.substring(i, j);
                            i = j - 1;
                            int wordLen = word.length();
                            if (wordLen < 32 && wordLen > 1) {
                                bucket.add(word);
                            }
                        }
                        i++;
                    }
                }
            }
            cursor.close();
            IMEApplication.from(this.appContext).notifyNewWordsForScanning(bucket);
        }
    }

    /* loaded from: classes.dex */
    public class ProviderRunnable implements Runnable {
        SyncDataProvider provider;

        public ProviderRunnable(SyncDataProvider provider) {
            this.provider = provider;
        }

        @Override // java.lang.Runnable
        public void run() {
            SyncDataProviderManager.log.d("run(): sync" + this.provider.getClass().getSimpleName());
            SyncDataProviderManager.this.syncCursor(this.provider.getCursor(), this.provider.getNewWordsBucket());
            SyncDataProviderManager.this.scheduled.remove(this.provider);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class ProviderObserver extends ContentObserver {
        boolean enableAndroidDictionarySync;
        SyncDataProvider provider;

        public ProviderObserver(Handler handler, SyncDataProvider provider) {
            super(handler);
            this.provider = provider;
            this.enableAndroidDictionarySync = SyncDataProviderManager.this.appContext.getResources().getBoolean(R.bool.mutual_synchronize_android_dictionary);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange, Uri uri) {
            if (!this.enableAndroidDictionarySync || !(this.provider instanceof AndroidUserDictionarySyncDataProvider)) {
                SyncDataProviderManager.this.scheduleSync(this.provider, SyncDataProviderManager.SYNC_DELAY_IN_SECONDS, TimeUnit.SECONDS);
            } else if (System.currentTimeMillis() - SyncDataProviderManager.timeOfLastUpdate > 1000 && !IMEApplication.from(SyncDataProviderManager.this.appContext).getIME().isImeInUse()) {
                SyncDataProviderManager.this.syncAndroidDictionary(this.provider, uri);
                long unused = SyncDataProviderManager.timeOfLastUpdate = System.currentTimeMillis();
            }
        }
    }
}
