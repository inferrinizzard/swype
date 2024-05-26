package com.localytics.android;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import com.localytics.android.BaseProvider;
import com.localytics.android.Localytics;
import com.localytics.android.ProfileHandler;
import java.io.File;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
class ProfileProvider extends BaseProvider {
    static final int DATABASE_VERSION = 1;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ProfileProvider(String dbName, LocalyticsDao localyticsDao) {
        super(localyticsDao, dbName);
        this.mDb = new ProfileDatabaseHelper(this.mDbPath, 1, localyticsDao).getWritableDatabase();
    }

    ProfileProvider(LocalyticsDao localyticsDao) {
        super(localyticsDao);
    }

    @Override // com.localytics.android.BaseProvider
    long maxSiloDbSize() {
        return 1 * Constants.BYTES_IN_A_MEGABYTE;
    }

    @Override // com.localytics.android.BaseProvider
    boolean canAddToDB() {
        return new File(this.mDb.getPath()).length() < maxSiloDbSize();
    }

    /* loaded from: classes.dex */
    static final class ProfileV3Columns implements BaseColumns {
        static final String CHANGE = "change";
        static final String CUSTOMER_ID = "customer_id";
        static final String DATABASE = "scope";
        static final String TABLE_NAME = "changes";

        private ProfileV3Columns() {
            throw new UnsupportedOperationException("This class is non-instantiable");
        }
    }

    /* loaded from: classes.dex */
    static final class ProfileDatabaseHelper extends BaseProvider.LocalyticsDatabaseHelper {
        ProfileDatabaseHelper(String name, int version, LocalyticsDao localyticsDao) {
            super(name, version, localyticsDao);
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public final void onCreate(SQLiteDatabase db) {
            if (db == null) {
                throw new IllegalArgumentException("db cannot be null");
            }
            db.execSQL("PRAGMA auto_vacuum = INCREMENTAL;");
            onUpgrade(db, 0, 1);
        }

        @Override // com.localytics.android.BaseProvider.LocalyticsDatabaseHelper
        protected final void migrateV2ToV3(SQLiteDatabase db) {
            db.execSQL(String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT NOT NULL, %s TEXT NOT NULL, %s TEXT NOT NULL)", "changes", "_id", "scope", "change", "customer_id"));
            if (oldDB != null) {
                synchronized (oldDB) {
                    Cursor cursor = null;
                    try {
                        ContentValues values = new ContentValues();
                        cursor = oldDB.query("profile", null, null, null, null, null, "_id ASC");
                        while (cursor.moveToNext()) {
                            try {
                                JSONObject oldJSON = new JSONObject(cursor.getString(cursor.getColumnIndexOrThrow("attribute")));
                                JSONObject newJSON = new JSONObject();
                                newJSON.put("op", ProfileHandler.ProfileOperation.ASSIGN.getOperationString());
                                String key = oldJSON.keys().next();
                                newJSON.put("attr", key);
                                newJSON.put("value", oldJSON.get(key));
                                values.put("scope", Localytics.ProfileScope.APPLICATION.getScope());
                                values.put("change", newJSON.toString());
                                values.put("customer_id", cursor.getString(cursor.getColumnIndexOrThrow("customer_id")));
                                db.insert("changes", null, values);
                                values.clear();
                            } catch (JSONException e) {
                                Localytics.Log.w("Caught JSON exception", e);
                            }
                        }
                        cleanUpOldDB();
                        if (cursor != null) {
                            cursor.close();
                        }
                    } catch (Throwable th) {
                        if (cursor != null) {
                            cursor.close();
                        }
                        throw th;
                    }
                }
            }
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public final void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (oldVersion <= 0) {
                migrateV2ToV3(db);
            }
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public final void onOpen(SQLiteDatabase db) {
            super.onOpen(db);
            Localytics.Log.v(String.format("SQLite library version is: %s", DatabaseUtils.stringForQuery(db, "select sqlite_version()", null)));
            if (!db.isReadOnly()) {
                db.execSQL("PRAGMA foreign_keys = ON;");
            }
        }
    }
}
