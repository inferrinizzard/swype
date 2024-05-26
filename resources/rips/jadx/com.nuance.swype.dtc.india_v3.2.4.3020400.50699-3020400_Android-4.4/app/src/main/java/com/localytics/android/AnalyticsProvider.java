package com.localytics.android;

import android.content.ContentValues;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.text.TextUtils;
import com.localytics.android.BaseProvider;
import com.localytics.android.Localytics;
import java.io.File;
import org.json.JSONArray;
import org.json.JSONException;

/* loaded from: classes.dex */
class AnalyticsProvider extends BaseProvider {
    static final int DATABASE_VERSION = 3;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AnalyticsProvider(String dbName, LocalyticsDao localyticsDao) {
        super(localyticsDao, dbName);
        this.mDb = new AnalyticsDatabaseHelper(this.mDbPath, 3, localyticsDao).getWritableDatabase();
    }

    AnalyticsProvider(LocalyticsDao localyticsDao) {
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
    static final class CustomDimensionsV3Columns implements BaseColumns {
        static final String CUSTOM_DIMENSION_KEY = "custom_dimension_key";
        static final String CUSTOM_DIMENSION_VALUE = "custom_dimension_value";
        static final String TABLE_NAME = "custom_dimensions";

        private CustomDimensionsV3Columns() {
            throw new UnsupportedOperationException("This class is non-instantiable");
        }
    }

    /* loaded from: classes.dex */
    static final class EventsV3Columns implements BaseColumns {
        static final String BLOB = "blob";
        static final String TABLE_NAME = "events";
        static final String UPLOAD_FORMAT = "upload_format";

        private EventsV3Columns() {
            throw new UnsupportedOperationException("This class is non-instantiable");
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: classes.dex */
        public enum UploadFormat {
            V2(2),
            V3(3);

            private final int value;

            UploadFormat(int val) {
                this.value = val;
            }

            public final int getValue() {
                return this.value;
            }
        }
    }

    /* loaded from: classes.dex */
    static final class IdentifiersV3Columns implements BaseColumns {
        static final String KEY = "key";
        static final String TABLE_NAME = "identifiers";
        static final String VALUE = "value";

        IdentifiersV3Columns() {
            throw new UnsupportedOperationException("This class is non-instantiable");
        }
    }

    /* loaded from: classes.dex */
    static final class InfoV3Columns implements BaseColumns {
        static final String API_KEY = "api_key";
        static final String APP_VERSION = "app_version";
        static final String CREATED_TIME = "created_time";
        static final String CURRENT_SESSION_UUID = "current_session_uuid";
        static final String CUSTOMER_ID = "customer_id";
        static final String FB_ATTRIBUTION = "fb_attribution";
        static final String FIRST_ANDROID_ID = "first_android_id";
        static final String FIRST_OPEN_EVENT_BLOB = "first_open_event_blob";
        static final String LAST_SESSION_CLOSE_TIME = "last_session_close_time";
        static final String LAST_SESSION_OPEN_TIME = "last_session_open_time";
        static final String LEGACY_ADVERTISING_ID = "first_advertising_id";
        static final String NEXT_HEADER_NUMBER = "next_header_number";
        static final String NEXT_SESSION_NUMBER = "next_session_number";
        static final String NOTIFICATIONS_DISABLED = "push_disabled";
        static final String OPT_OUT = "opt_out";
        static final String PACKAGE_NAME = "package_name";
        static final String PLAY_ATTRIBUTION = "play_attribution";
        static final String QUEUED_CLOSE_SESSION_BLOB = "queued_close_session_blob";
        static final String QUEUED_CLOSE_SESSION_BLOB_UPLOAD_FORMAT = "queued_close_session_blob_upload_format";
        static final String REGISTRATION_ID = "registration_id";
        static final String REGISTRATION_VERSION = "registration_version";
        static final String SENDER_ID = "sender_id";
        static final String TABLE_NAME = "info";
        static final String USER_TYPE = "user_type";
        static final String UUID = "uuid";

        private InfoV3Columns() {
            throw new UnsupportedOperationException("This class is non-instantiable");
        }
    }

    /* loaded from: classes.dex */
    static final class ScreensColumns implements BaseColumns {
        static final String NAME = "name";
        static final String TABLE_NAME = "screens";

        ScreensColumns() {
        }
    }

    /* loaded from: classes.dex */
    static class AnalyticsDatabaseHelper extends BaseProvider.LocalyticsDatabaseHelper {
        private JSONArray mScreensFromV2;

        AnalyticsDatabaseHelper(String name, int version, LocalyticsDao localyticsDao) {
            super(name, version, localyticsDao);
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onCreate(SQLiteDatabase db) {
            if (db == null) {
                throw new IllegalArgumentException("db cannot be null");
            }
            db.execSQL("PRAGMA auto_vacuum = INCREMENTAL;");
            onUpgrade(db, 0, 3);
        }

        /* JADX WARN: Removed duplicated region for block: B:109:0x0629 A[Catch: all -> 0x0261, TRY_ENTER, TryCatch #0 {, blocks: (B:7:0x0176, B:18:0x01f2, B:32:0x0270, B:41:0x02dc, B:42:0x02e1, B:44:0x02eb, B:45:0x02f2, B:47:0x02fe, B:48:0x0309, B:50:0x030f, B:52:0x0315, B:85:0x0329, B:58:0x033e, B:62:0x0390, B:64:0x03a9, B:66:0x03c4, B:67:0x0404, B:69:0x03e1, B:71:0x03ec, B:72:0x03f5, B:76:0x041f, B:78:0x0431, B:79:0x0443, B:109:0x0629, B:110:0x062c, B:111:0x062f, B:116:0x064b, B:117:0x064e, B:121:0x02d6, B:122:0x02d9, B:126:0x025d, B:127:0x0260, B:130:0x026a, B:131:0x026d, B:35:0x0275, B:36:0x0284, B:38:0x028a, B:10:0x0190, B:12:0x01a5, B:95:0x0471, B:97:0x0486, B:99:0x0512, B:100:0x0527, B:102:0x05f7, B:103:0x0619, B:106:0x0651, B:107:0x0631, B:21:0x01f7, B:22:0x0206, B:24:0x020c, B:28:0x0237), top: B:6:0x0176, inners: #1, #3, #4, #6 }] */
        /* JADX WARN: Removed duplicated region for block: B:97:0x0486 A[Catch: all -> 0x0648, TryCatch #4 {all -> 0x0648, blocks: (B:95:0x0471, B:97:0x0486, B:99:0x0512, B:100:0x0527, B:102:0x05f7, B:103:0x0619, B:106:0x0651, B:107:0x0631), top: B:94:0x0471, outer: #0 }] */
        @Override // com.localytics.android.BaseProvider.LocalyticsDatabaseHelper
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        protected void migrateV2ToV3(android.database.sqlite.SQLiteDatabase r50) {
            /*
                Method dump skipped, instructions count: 1665
                To view this dump change 'Code comments level' option to 'DEBUG'
            */
            throw new UnsupportedOperationException("Method not decompiled: com.localytics.android.AnalyticsProvider.AnalyticsDatabaseHelper.migrateV2ToV3(android.database.sqlite.SQLiteDatabase):void");
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (oldVersion <= 0) {
                migrateV2ToV3(db);
            }
            if (oldVersion < 2) {
                addFirstOpenEventToInfoTable(db);
            }
            if (oldVersion < 3) {
                addScreens(db);
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

        protected void addFirstOpenEventToInfoTable(SQLiteDatabase db) {
            db.execSQL(String.format("ALTER TABLE %s ADD COLUMN %s TEXT;", "info", "first_open_event_blob"));
        }

        protected void addScreens(SQLiteDatabase db) {
            db.execSQL(String.format("CREATE TABLE %s (%s TEXT NOT NULL)", "screens", "name"));
            if (this.mScreensFromV2 != null && this.mScreensFromV2.length() > 0) {
                for (int i = 0; i < this.mScreensFromV2.length(); i++) {
                    try {
                        String screen = this.mScreensFromV2.getString(i);
                        if (!TextUtils.isEmpty(screen)) {
                            ContentValues screenValues = new ContentValues();
                            screenValues.put("name", screen);
                            db.insert("screens", null, screenValues);
                        }
                    } catch (JSONException e) {
                        Localytics.Log.e("Bad data in v2 db. Non-string type in screen flow");
                        return;
                    }
                }
            }
        }
    }
}
