package com.localytics.android;

import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import com.localytics.android.BaseProvider;
import com.localytics.android.Localytics;

/* loaded from: classes.dex */
class ManifestProvider extends BaseProvider {
    static final int DATABASE_VERSION = 1;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ManifestProvider(String dbName, LocalyticsDao localyticsDao) {
        super(localyticsDao, dbName);
        this.mDb = new ManifestDatabaseHelper(this.mDbPath, 1, localyticsDao).getWritableDatabase();
    }

    ManifestProvider(LocalyticsDao localyticsDao) {
        super(localyticsDao);
    }

    @Override // com.localytics.android.BaseProvider
    long maxSiloDbSize() {
        return 1 * Constants.BYTES_IN_A_MEGABYTE;
    }

    @Override // com.localytics.android.BaseProvider
    boolean canAddToDB() {
        return true;
    }

    /* loaded from: classes.dex */
    static final class InfoV3Columns implements BaseColumns {
        static final String LAST_CAMPAIGN_DOWNLOAD = "last_campaign_download";
        static final String TABLE_NAME = "info";

        private InfoV3Columns() {
            throw new UnsupportedOperationException("This class is non-instantiable");
        }
    }

    /* loaded from: classes.dex */
    static class ManifestDatabaseHelper extends BaseProvider.LocalyticsDatabaseHelper {
        ManifestDatabaseHelper(String name, int version, LocalyticsDao localyticsDao) {
            super(name, version, localyticsDao);
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onCreate(SQLiteDatabase db) {
            if (db == null) {
                throw new IllegalArgumentException("db cannot be null");
            }
            onUpgrade(db, 0, 1);
        }

        @Override // com.localytics.android.BaseProvider.LocalyticsDatabaseHelper
        protected void migrateV2ToV3(SQLiteDatabase db) {
            db.execSQL(String.format("CREATE TABLE %s (%s INTEGER DEFAULT 0);", "info", "last_campaign_download"));
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (oldVersion <= 0) {
                migrateV2ToV3(db);
            }
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onOpen(SQLiteDatabase db) {
            super.onOpen(db);
            Localytics.Log.v(String.format("SQLite library version is: %s", DatabaseUtils.stringForQuery(db, "select sqlite_version()", null)));
            if (!db.isReadOnly()) {
                db.execSQL("PRAGMA foreign_keys = ON;");
            }
        }
    }
}
