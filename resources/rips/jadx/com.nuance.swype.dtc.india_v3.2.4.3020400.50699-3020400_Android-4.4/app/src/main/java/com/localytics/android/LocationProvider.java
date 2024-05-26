package com.localytics.android;

import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import com.localytics.android.BaseProvider;
import com.localytics.android.Localytics;
import java.io.File;

/* loaded from: classes.dex */
class LocationProvider extends BaseProvider {
    static final int DATABASE_VERSION = 1;

    /* JADX INFO: Access modifiers changed from: package-private */
    public LocationProvider(String dbName, LocalyticsDao localyticsDao) {
        super(localyticsDao, dbName);
        this.mDb = new LocationDatabaseHelper(this.mDbPath, 1, localyticsDao).getWritableDatabase();
    }

    LocationProvider(LocalyticsDao localyticsDao) {
        super(localyticsDao);
    }

    @Override // com.localytics.android.BaseProvider
    long maxSiloDbSize() {
        return 10 * Constants.BYTES_IN_A_MEGABYTE;
    }

    @Override // com.localytics.android.BaseProvider
    boolean canAddToDB() {
        return new File(this.mDb.getPath()).length() < maxSiloDbSize();
    }

    /* loaded from: classes.dex */
    static final class GeofencesV3Columns implements BaseColumns {
        static final String ENTERED_TIME = "entered_time";
        static final String ENTER_ANALYTICS_ENABLED = "enter_analytics_enabled";
        static final String EXITED_TIME = "exited_time";
        static final String EXIT_ANALYTICS_ENABLED = "exit_analytics_enabled";
        static final String IDENTIFIER = "identifier";
        static final String IS_MONITORED = "is_monitored";
        static final String LATITUDE = "latitude";
        static final String LONGITUDE = "longitude";
        static final String NAME = "name";
        static final String PLACE_ID = "place_id";
        static final String RADIUS = "radius";
        static final String SCHEMA_VERSION = "schema_version";
        static final String TABLE_NAME = "geofences";

        private GeofencesV3Columns() {
            throw new UnsupportedOperationException("This class is non-instantiable");
        }
    }

    /* loaded from: classes.dex */
    static final class GeofencesAttributesV3Columns implements BaseColumns {
        static final String KEY = "key";
        static final String PLACE_ID = "place_id";
        static final String TABLE_NAME = "geofences_attributes";
        static final String VALUE = "value";

        private GeofencesAttributesV3Columns() {
            throw new UnsupportedOperationException("This class is non-instantiable");
        }
    }

    /* loaded from: classes.dex */
    static final class InfoV3Columns implements BaseColumns {
        static final String LOCATION_MONITORING_ENABLED = "location_monitoring_enabled";
        static final String PLACES_DATA_LAST_MODIFIED = "places_data_last_modified";
        static final String TABLE_NAME = "info";

        private InfoV3Columns() {
            throw new UnsupportedOperationException("This class is non-instantiable");
        }
    }

    /* loaded from: classes.dex */
    static class LocationDatabaseHelper extends BaseProvider.LocalyticsDatabaseHelper {
        LocationDatabaseHelper(String name, int version, LocalyticsDao localyticsDao) {
            super(name, version, localyticsDao);
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onCreate(SQLiteDatabase db) {
            if (db == null) {
                throw new IllegalArgumentException("db cannot be null");
            }
            db.execSQL("PRAGMA auto_vacuum = INCREMENTAL;");
            onUpgrade(db, 0, 1);
        }

        @Override // com.localytics.android.BaseProvider.LocalyticsDatabaseHelper
        protected void migrateV2ToV3(SQLiteDatabase db) {
            db.execSQL(String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY,%s TEXT NOT NULL UNIQUE,%s TEXT,%s INTEGER NOT NULL,%s INTEGER NOT NULL,%s INTEGER NOT NULL,%s INTEGER NOT NULL DEFAULT 0,%s INTEGER NOT NULL DEFAULT 0,%s INTEGER NOT NULL DEFAULT 0,%s INTEGER NOT NULL DEFAULT 0,%s INTEGER NOT NULL DEFAULT 1,%s INTEGER NOT NULL DEFAULT 0);", "geofences", "place_id", "identifier", "name", "latitude", "longitude", "radius", "enter_analytics_enabled", "exit_analytics_enabled", "entered_time", "exited_time", "schema_version", "is_monitored"));
            db.execSQL(String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT,%s INTEGER REFERENCES %s(%s) ON DELETE CASCADE,%s TEXT NOT NULL,%s TEXT NOT NULL);", "geofences_attributes", "_id", "place_id", "geofences", "place_id", "key", "value"));
            db.execSQL(String.format("CREATE TABLE %s (%s INTEGER,%s INTEGER);", "info", "places_data_last_modified", "location_monitoring_enabled"));
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
