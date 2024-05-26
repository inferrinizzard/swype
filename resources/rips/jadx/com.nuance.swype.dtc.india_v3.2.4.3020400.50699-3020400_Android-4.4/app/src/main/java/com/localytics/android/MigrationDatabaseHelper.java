package com.localytics.android;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorJoiner;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import bolts.MeasurementEvent;
import com.facebook.internal.NativeProtocol;
import com.facebook.internal.ServerProtocol;
import com.localytics.android.Localytics;
import com.nuance.swype.input.License;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
final class MigrationDatabaseHelper extends SQLiteOpenHelper {
    private LocalyticsDao mLocalyticsDao;
    private static final String EVENT_FORMAT = "%s:%s";
    private static final String OPEN_EVENT = String.format(EVENT_FORMAT, BuildConfig.APPLICATION_ID, "open");
    private static final String CLOSE_EVENT = String.format(EVENT_FORMAT, BuildConfig.APPLICATION_ID, "close");
    private static final String OPT_IN_EVENT = String.format(EVENT_FORMAT, BuildConfig.APPLICATION_ID, "opt_in");
    private static final String OPT_OUT_EVENT = String.format(EVENT_FORMAT, BuildConfig.APPLICATION_ID, "opt_out");
    private static final String FLOW_EVENT = String.format(EVENT_FORMAT, BuildConfig.APPLICATION_ID, "flow");
    private static final String EVENTS_SORT_ORDER = String.format("CAST(%s as TEXT)", "_id");
    private static final String[] PROJECTION_UPLOAD_BLOBS = {"events_key_ref"};
    private static final String UPLOAD_BLOBS_EVENTS_SORT_ORDER = String.format("CAST(%s AS TEXT)", "events_key_ref");
    private static final String[] JOINER_ARG_UPLOAD_EVENTS_COLUMNS = {"_id"};
    private static final String SELECTION_UPLOAD_NULL_BLOBS = String.format("%s IS NULL", "processed_in_blob");
    private static final String[] PROJECTION_UPLOAD_EVENTS = {"_id", MeasurementEvent.MEASUREMENT_EVENT_NAME_KEY, "wall_time"};

    /* JADX INFO: Access modifiers changed from: package-private */
    public MigrationDatabaseHelper(String name, int version, LocalyticsDao localyticsDao) {
        super(localyticsDao.getAppContext(), name, (SQLiteDatabase.CursorFactory) null, version);
        this.mLocalyticsDao = localyticsDao;
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public final void onCreate(SQLiteDatabase db) {
        if (db == null) {
            throw new IllegalArgumentException("db cannot be null");
        }
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public final void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 3) {
            db.delete("upload_blob_events", null, null);
            db.delete("event_history", null, null);
            db.delete("upload_blobs", null, null);
            db.delete("attributes", null, null);
            db.delete("events", null, null);
            db.delete("sessions", null, null);
        }
        if (oldVersion < 4) {
            db.execSQL(String.format("ALTER TABLE %s ADD COLUMN %s TEXT;", "sessions", "iu"));
        }
        if (oldVersion < 5) {
            db.execSQL(String.format("ALTER TABLE %s ADD COLUMN %s TEXT;", "sessions", "device_wifi_mac_hash"));
        }
        if (oldVersion < 6) {
            Cursor attributesCursor = null;
            try {
                attributesCursor = db.query("attributes", new String[]{"_id", "attribute_key"}, null, null, null, null, null);
                int idColumnIndex = attributesCursor.getColumnIndexOrThrow("_id");
                int keyColumnIndex = attributesCursor.getColumnIndexOrThrow("attribute_key");
                ContentValues tempValues = new ContentValues();
                String whereClause = String.format("%s = ?", "_id");
                String[] whereArgs = new String[1];
                attributesCursor.moveToPosition(-1);
                while (attributesCursor.moveToNext()) {
                    tempValues.put("attribute_key", String.format(EVENT_FORMAT, this.mLocalyticsDao.getAppContext().getPackageName(), attributesCursor.getString(keyColumnIndex)));
                    whereArgs[0] = Long.toString(attributesCursor.getLong(idColumnIndex));
                    db.update("attributes", tempValues, whereClause, whereArgs);
                    tempValues.clear();
                }
            } finally {
                if (attributesCursor != null) {
                    attributesCursor.close();
                }
            }
        }
        if (oldVersion < 7) {
            db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (%s TEXT, %s INTEGER);", "info", "fb_attribution", "first_run"));
            ContentValues values = new ContentValues();
            values.putNull("fb_attribution");
            values.put("first_run", Boolean.FALSE);
            db.insertOrThrow("info", null, values);
        }
        if (oldVersion < 8) {
            db.execSQL(String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT UNIQUE NOT NULL, %s TEXT NOT NULL);", "identifiers", "_id", "key", "value"));
        }
        if (oldVersion < 9) {
            db.execSQL(String.format("ALTER TABLE %s ADD COLUMN %s INTEGER NOT NULL DEFAULT 0;", "events", "clv_increase"));
        }
        if (oldVersion < 10) {
            db.execSQL(String.format("ALTER TABLE %s ADD COLUMN %s TEXT;", "info", "play_attribution"));
        }
        if (oldVersion < 11) {
            db.execSQL(String.format("ALTER TABLE %s ADD COLUMN %s TEXT;", "info", "registration_id"));
            db.execSQL(String.format("ALTER TABLE %s ADD COLUMN %s TEXT;", "info", "registration_version"));
        }
        if (oldVersion < 12) {
            db.execSQL(String.format("ALTER TABLE %s ADD COLUMN %s TEXT;", "info", "first_android_id"));
            db.execSQL(String.format("ALTER TABLE %s ADD COLUMN %s TEXT;", "info", "first_telephony_id"));
            db.execSQL(String.format("ALTER TABLE %s ADD COLUMN %s TEXT;", "info", "package_name"));
            ContentValues values2 = new ContentValues();
            values2.put("first_android_id", DatapointHelper.getAndroidIdOrNull(this.mLocalyticsDao.getAppContext()));
            values2.put("first_telephony_id", DatapointHelper.getTelephonyDeviceIdOrNull(this.mLocalyticsDao.getAppContext()));
            values2.put("package_name", this.mLocalyticsDao.getAppContext().getPackageName());
            db.update("info", values2, null, null);
            db.execSQL(String.format("ALTER TABLE %s ADD COLUMN %s TEXT;", "sessions", License.DEVICE_ANDROID_ID));
        }
        if (oldVersion < 13) {
            db.execSQL(String.format("ALTER TABLE %s ADD COLUMN %s REAL;", "events", "event_lat"));
            db.execSQL(String.format("ALTER TABLE %s ADD COLUMN %s REAL;", "events", "event_lng"));
        }
        if (oldVersion < 14) {
            db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s INTEGER NOT NULL, %s INTEGER NOT NULL, %s INTEGER, %s INTEGER, %s TEXT NOT NULL, %s TEXT NOT NULL, %s INTEGER NOT NULL, %s INTEGER NOT NULL, %s TEXT NOT NULL, %s INTEGER NOT NULL, %s INTEGER NOT NULL, %s INTEGER, %s INTEGER NOT NULL, %s TEXT, %s TEXT UNIQUE NOT NULL, %s TEXT NOT NULL, %s TEXT NOT NULL)", "amp_rules", "_id", "campaign_id", "expiration", "display_seconds", "display_session", ServerProtocol.FALLBACK_DIALOG_PARAM_VERSION, "phone_location", "phone_size_width", "phone_size_height", "tablet_location", "tablet_size_width", "tablet_size_height", "time_to_display", "internet_required", "ab_test", "rule_name", "location", "devices"));
            db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT NOT NULL, %s INTEGER REFERENCES %s(%s) NOT NULL);", "amp_ruleevent", "_id", MeasurementEvent.MEASUREMENT_EVENT_NAME_KEY, "rule_id_ref", "amp_rules", "_id"));
            db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s INTEGER NOT NULL DEFAULT 0, %s INTEGER NOT NULL);", "amp_displayed", "_id", "displayed", "campaign_id"));
            db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT NOT NULL, %s TEXT NOT NULL, %s INTEGER REFERENCES %s(%s) NOT NULL);", "amp_conditions", "_id", "attribute_name", "operator", "rule_id_ref", "amp_rules", "_id"));
            db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT NOT NULL, %s INTEGER REFERENCES %s(%s) NOT NULL);", "amp_condition_values", "_id", "value", "condition_id_ref", "amp_conditions", "_id"));
        }
        if (oldVersion < 15) {
            db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT UNIQUE NOT NULL, %s TEXT NOT NULL);", "custom_dimensions", "_id", "custom_dimension_key", "custom_dimension_value"));
        }
        if (oldVersion < 16) {
            db.execSQL(String.format("ALTER TABLE %s ADD COLUMN %s TEXT;", "info", "first_advertising_id"));
            db.execSQL(String.format("ALTER TABLE %s ADD COLUMN %s TEXT;", "sessions", "device_advertising_id"));
            db.execSQL(String.format("ALTER TABLE %s ADD COLUMN %s INTEGER;", "info", "push_disabled"));
            db.execSQL(String.format("ALTER TABLE %s ADD COLUMN %s TEXT;", "info", "sender_id"));
        }
        if (oldVersion < 17) {
            db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s INTEGER)", "profile", "_id", "attribute", NativeProtocol.WEB_DIALOG_ACTION));
            db.execSQL(String.format("ALTER TABLE %s ADD COLUMN %s TEXT;", "events", "customer_id"));
            db.execSQL(String.format("ALTER TABLE %s ADD COLUMN %s TEXT;", "events", "user_type"));
            db.execSQL(String.format("ALTER TABLE %s ADD COLUMN %s TEXT;", "events", "ids"));
            db.execSQL(String.format("ALTER TABLE %s ADD COLUMN %s INTEGER", "info", "last_session_open_time"));
            db.execSQL(String.format("ALTER TABLE %s ADD COLUMN %s INTEGER NOT NULL CHECK (%s >= 0) DEFAULT 0", "sessions", "elapsed", "elapsed"));
        }
        if (oldVersion < 18) {
            db.execSQL(String.format("ALTER TABLE %s ADD COLUMN %s TEXT", "profile", "customer_id"));
            Cursor cursor = null;
            ContentValues values3 = new ContentValues();
            try {
                cursor = db.query("profile", null, null, null, null, null, null);
                while (cursor.moveToNext()) {
                    String rowID = String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("_id")));
                    try {
                        JSONObject attributeJSON = new JSONObject(cursor.getString(cursor.getColumnIndexOrThrow("attribute")));
                        values3.put("attribute", attributeJSON.getString("attributes"));
                        values3.put("customer_id", attributeJSON.getString("id"));
                        db.update("profile", values3, String.format("%s = %s", "_id", rowID), null);
                        values3.clear();
                    } catch (Exception e) {
                        db.delete("profile", String.format("%s = %s", "_id", rowID), null);
                    }
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
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

    /* loaded from: classes.dex */
    static final class ApiKeysDbColumns implements BaseColumns {
        static final String API_KEY = "api_key";
        static final String CREATED_TIME = "created_time";
        static final String OPT_OUT = "opt_out";
        static final String TABLE_NAME = "api_keys";
        static final String UUID = "uuid";

        private ApiKeysDbColumns() {
            throw new UnsupportedOperationException("This class is non-instantiable");
        }
    }

    /* loaded from: classes.dex */
    static final class InfoDbColumns implements BaseColumns {
        static final String FB_ATTRIBUTION = "fb_attribution";
        static final String FIRST_ADVERTISING_ID = "first_advertising_id";
        static final String FIRST_ANDROID_ID = "first_android_id";
        static final String FIRST_RUN = "first_run";
        static final String FIRST_TELEPHONY_ID = "first_telephony_id";
        static final String LAST_SESSION_OPEN_TIME = "last_session_open_time";
        static final String PACKAGE_NAME = "package_name";
        static final String PLAY_ATTRIBUTION = "play_attribution";
        static final String PUSH_DISABLED = "push_disabled";
        static final String REGISTRATION_ID = "registration_id";
        static final String REGISTRATION_VERSION = "registration_version";
        static final String SENDER_ID = "sender_id";
        static final String TABLE_NAME = "info";

        private InfoDbColumns() {
            throw new UnsupportedOperationException("This class is non-instantiable");
        }
    }

    /* loaded from: classes.dex */
    static final class IdentifiersDbColumns implements BaseColumns {
        static final String KEY = "key";
        static final String TABLE_NAME = "identifiers";
        static final String VALUE = "value";

        private IdentifiersDbColumns() {
            throw new UnsupportedOperationException("This class is non-instantiable");
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class AttributesDbColumns implements BaseColumns {
        static final String ATTRIBUTE_KEY = "attribute_key";
        static final String ATTRIBUTE_VALUE = "attribute_value";
        static final String EVENTS_KEY_REF = "events_key_ref";
        static final String TABLE_NAME = "attributes";
        static final String ATTRIBUTE_FORMAT = "%s:%s";
        static final String ATTRIBUTE_CUSTOM_DIMENSION_1 = String.format(ATTRIBUTE_FORMAT, BuildConfig.APPLICATION_ID, "custom_dimension_0");
        static final String ATTRIBUTE_CUSTOM_DIMENSION_2 = String.format(ATTRIBUTE_FORMAT, BuildConfig.APPLICATION_ID, "custom_dimension_1");
        static final String ATTRIBUTE_CUSTOM_DIMENSION_3 = String.format(ATTRIBUTE_FORMAT, BuildConfig.APPLICATION_ID, "custom_dimension_2");
        static final String ATTRIBUTE_CUSTOM_DIMENSION_4 = String.format(ATTRIBUTE_FORMAT, BuildConfig.APPLICATION_ID, "custom_dimension_3");
        static final String ATTRIBUTE_CUSTOM_DIMENSION_5 = String.format(ATTRIBUTE_FORMAT, BuildConfig.APPLICATION_ID, "custom_dimension_4");
        static final String ATTRIBUTE_CUSTOM_DIMENSION_6 = String.format(ATTRIBUTE_FORMAT, BuildConfig.APPLICATION_ID, "custom_dimension_5");
        static final String ATTRIBUTE_CUSTOM_DIMENSION_7 = String.format(ATTRIBUTE_FORMAT, BuildConfig.APPLICATION_ID, "custom_dimension_6");
        static final String ATTRIBUTE_CUSTOM_DIMENSION_8 = String.format(ATTRIBUTE_FORMAT, BuildConfig.APPLICATION_ID, "custom_dimension_7");
        static final String ATTRIBUTE_CUSTOM_DIMENSION_9 = String.format(ATTRIBUTE_FORMAT, BuildConfig.APPLICATION_ID, "custom_dimension_8");
        static final String ATTRIBUTE_CUSTOM_DIMENSION_10 = String.format(ATTRIBUTE_FORMAT, BuildConfig.APPLICATION_ID, "custom_dimension_9");

        private AttributesDbColumns() {
            throw new UnsupportedOperationException("This class is non-instantiable");
        }
    }

    /* loaded from: classes.dex */
    static final class CustomDimensionsDbColumns implements BaseColumns {
        static final String CUSTOM_DIMENSION_KEY = "custom_dimension_key";
        static final String CUSTOM_DIMENSION_VALUE = "custom_dimension_value";
        static final String TABLE_NAME = "custom_dimensions";
        static final String CUSTOM_DIMENSION_FORMAT = "%s:%s";
        static final String CUSTOM_DIMENSION_1 = String.format(CUSTOM_DIMENSION_FORMAT, BuildConfig.APPLICATION_ID, "custom_dimension_0");
        static final String CUSTOM_DIMENSION_2 = String.format(CUSTOM_DIMENSION_FORMAT, BuildConfig.APPLICATION_ID, "custom_dimension_1");
        static final String CUSTOM_DIMENSION_3 = String.format(CUSTOM_DIMENSION_FORMAT, BuildConfig.APPLICATION_ID, "custom_dimension_2");
        static final String CUSTOM_DIMENSION_4 = String.format(CUSTOM_DIMENSION_FORMAT, BuildConfig.APPLICATION_ID, "custom_dimension_3");
        static final String CUSTOM_DIMENSION_5 = String.format(CUSTOM_DIMENSION_FORMAT, BuildConfig.APPLICATION_ID, "custom_dimension_4");
        static final String CUSTOM_DIMENSION_6 = String.format(CUSTOM_DIMENSION_FORMAT, BuildConfig.APPLICATION_ID, "custom_dimension_5");
        static final String CUSTOM_DIMENSION_7 = String.format(CUSTOM_DIMENSION_FORMAT, BuildConfig.APPLICATION_ID, "custom_dimension_6");
        static final String CUSTOM_DIMENSION_8 = String.format(CUSTOM_DIMENSION_FORMAT, BuildConfig.APPLICATION_ID, "custom_dimension_7");
        static final String CUSTOM_DIMENSION_9 = String.format(CUSTOM_DIMENSION_FORMAT, BuildConfig.APPLICATION_ID, "custom_dimension_8");
        static final String CUSTOM_DIMENSION_10 = String.format(CUSTOM_DIMENSION_FORMAT, BuildConfig.APPLICATION_ID, "custom_dimension_9");

        private CustomDimensionsDbColumns() {
            throw new UnsupportedOperationException("This class is non-instantiable");
        }
    }

    /* loaded from: classes.dex */
    static final class EventsDbColumns implements BaseColumns {
        static final String CLV_INCREASE = "clv_increase";
        static final String CUST_ID = "customer_id";
        static final String EVENT_NAME = "event_name";
        static final String IDENTIFIERS = "ids";
        static final String LAT_NAME = "event_lat";
        static final String LNG_NAME = "event_lng";
        static final String REAL_TIME = "real_time";
        static final String SESSION_KEY_REF = "session_key_ref";
        static final String TABLE_NAME = "events";
        static final String USER_TYPE = "user_type";
        static final String UUID = "uuid";
        static final String WALL_TIME = "wall_time";

        private EventsDbColumns() {
            throw new UnsupportedOperationException("This class is non-instantiable");
        }
    }

    /* loaded from: classes.dex */
    static final class EventHistoryDbColumns implements BaseColumns {
        static final String NAME = "name";
        static final String PROCESSED_IN_BLOB = "processed_in_blob";
        static final String SESSION_KEY_REF = "session_key_ref";
        static final String TABLE_NAME = "event_history";
        static final String TYPE = "type";
        static final int TYPE_EVENT = 0;
        static final int TYPE_SCREEN = 1;

        private EventHistoryDbColumns() {
            throw new UnsupportedOperationException("This class is non-instantiable");
        }
    }

    /* loaded from: classes.dex */
    static final class SessionsDbColumns implements BaseColumns {
        static final String ANDROID_SDK = "android_sdk";
        static final String ANDROID_VERSION = "android_version";
        static final String API_KEY_REF = "api_key_ref";
        static final String APP_VERSION = "app_version";
        static final String DEVICE_ADVERTISING_ID = "device_advertising_id";
        static final String DEVICE_ANDROID_ID = "device_android_id";
        static final String DEVICE_ANDROID_ID_HASH = "device_android_id_hash";
        static final String DEVICE_COUNTRY = "device_country";
        static final String DEVICE_MANUFACTURER = "device_manufacturer";
        static final String DEVICE_MODEL = "device_model";
        static final String DEVICE_SERIAL_NUMBER_HASH = "device_serial_number_hash";
        static final String DEVICE_TELEPHONY_ID = "device_telephony_id";
        static final String DEVICE_TELEPHONY_ID_HASH = "device_telephony_id_hash";
        static final String DEVICE_WIFI_MAC_HASH = "device_wifi_mac_hash";
        static final String ELAPSED_TIME_SINCE_LAST_SESSION = "elapsed";
        static final String LATITUDE = "latitude";
        static final String LOCALE_COUNTRY = "locale_country";
        static final String LOCALE_LANGUAGE = "locale_language";
        static final String LOCALYTICS_INSTALLATION_ID = "iu";
        static final String LOCALYTICS_LIBRARY_VERSION = "localytics_library_version";
        static final String LONGITUDE = "longitude";
        static final String NETWORK_CARRIER = "network_carrier";
        static final String NETWORK_COUNTRY = "network_country";
        static final String NETWORK_TYPE = "network_type";
        static final String SESSION_START_WALL_TIME = "session_start_wall_time";
        static final String TABLE_NAME = "sessions";
        static final String UUID = "uuid";

        private SessionsDbColumns() {
            throw new UnsupportedOperationException("This class is non-instantiable");
        }
    }

    /* loaded from: classes.dex */
    static final class UploadBlobEventsDbColumns implements BaseColumns {
        static final String EVENTS_KEY_REF = "events_key_ref";
        static final String TABLE_NAME = "upload_blob_events";
        static final String UPLOAD_BLOBS_KEY_REF = "upload_blobs_key_ref";

        private UploadBlobEventsDbColumns() {
            throw new UnsupportedOperationException("This class is non-instantiable");
        }
    }

    /* loaded from: classes.dex */
    static final class UploadBlobsDbColumns implements BaseColumns {
        static final String TABLE_NAME = "upload_blobs";
        static final String UUID = "uuid";

        private UploadBlobsDbColumns() {
            throw new UnsupportedOperationException("This class is non-instantiable");
        }
    }

    /* loaded from: classes.dex */
    static final class AmpRulesDbColumns implements BaseColumns {
        static final String AB_TEST = "ab_test";
        static final String CAMPAIGN_ID = "campaign_id";
        static final String DEVICES = "devices";
        static final String DISPLAY_SECONDS = "display_seconds";
        static final String DISPLAY_SESSION = "display_session";
        static final String EXPIRATION = "expiration";
        static final String INTERNET_REQUIRED = "internet_required";
        static final String LOCATION = "location";
        static final String PHONE_LOCATION = "phone_location";
        static final String PHONE_SIZE_HEIGHT = "phone_size_height";
        static final String PHONE_SIZE_WIDTH = "phone_size_width";
        static final String RULE_NAME = "rule_name";
        static final String TABLET_LOCATION = "tablet_location";
        static final String TABLET_SIZE_HEIGHT = "tablet_size_height";
        static final String TABLET_SIZE_WIDTH = "tablet_size_width";
        static final String TABLE_NAME = "amp_rules";
        static final String TIME_TO_DISPLAY = "time_to_display";
        static final String VERSION = "version";

        private AmpRulesDbColumns() {
            throw new UnsupportedOperationException("This class is non-instantiable");
        }
    }

    /* loaded from: classes.dex */
    static final class AmpDisplayedDbColumns implements BaseColumns {
        static final String CAMPAIGN_ID = "campaign_id";
        static final String DISPLAYED = "displayed";
        static final String TABLE_NAME = "amp_displayed";

        private AmpDisplayedDbColumns() {
            throw new UnsupportedOperationException("This class is non-instantiable");
        }
    }

    /* loaded from: classes.dex */
    static final class AmpRuleEventDbColumns implements BaseColumns {
        static final String EVENT_NAME = "event_name";
        static final String RULE_ID_REF = "rule_id_ref";
        static final String TABLE_NAME = "amp_ruleevent";

        private AmpRuleEventDbColumns() {
            throw new UnsupportedOperationException("This class is non-instantiable");
        }
    }

    /* loaded from: classes.dex */
    static final class AmpConditionsDbColumns implements BaseColumns {
        static final String ATTRIBUTE_NAME = "attribute_name";
        static final String OPERATOR = "operator";
        static final String RULE_ID_REF = "rule_id_ref";
        static final String TABLE_NAME = "amp_conditions";

        private AmpConditionsDbColumns() {
            throw new UnsupportedOperationException("This class is non-instantiable");
        }
    }

    /* loaded from: classes.dex */
    static final class AmpConditionValuesDbColumns implements BaseColumns {
        static final String CONDITION_ID_REF = "condition_id_ref";
        static final String TABLE_NAME = "amp_condition_values";
        static final String VALUE = "value";

        private AmpConditionValuesDbColumns() {
            throw new UnsupportedOperationException("This class is non-instantiable");
        }
    }

    /* loaded from: classes.dex */
    static final class ProfileDbColumns implements BaseColumns {
        static final String ACTION = "action";
        static final String ATTRIBUTE = "attribute";
        static final String CUSTOMER_ID = "customer_id";
        static final String TABLE_NAME = "profile";
        static final String USER_ID = "id";

        private ProfileDbColumns() {
            throw new UnsupportedOperationException("This class is non-instantiable");
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:95:0x02e9 A[DONT_GENERATE] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private static org.json.JSONObject convertEventToJson(android.database.sqlite.SQLiteDatabase r44, android.content.Context r45, long r46, long r48, java.lang.String r50) throws org.json.JSONException {
        /*
            Method dump skipped, instructions count: 2753
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.localytics.android.MigrationDatabaseHelper.convertEventToJson(android.database.sqlite.SQLiteDatabase, android.content.Context, long, long, java.lang.String):org.json.JSONObject");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static List<JSONObject> convertDatabaseToJson(Context context, SQLiteDatabase db, String apiKey) {
        JSONObject blobHeader;
        JSONObject sessionAttributes;
        List<JSONObject> result = new LinkedList<>();
        Cursor cursor = null;
        try {
            cursor = db.query("upload_blobs", null, null, null, null, null, null);
            long creationTime = getApiKeyCreationTime(db, apiKey);
            int idColumn = cursor.getColumnIndexOrThrow("_id");
            int uuidColumn = cursor.getColumnIndexOrThrow("uuid");
            while (cursor.moveToNext()) {
                try {
                    blobHeader = new JSONObject();
                    blobHeader.put("dt", "h");
                    blobHeader.put("pa", creationTime);
                    blobHeader.put("seq", cursor.getLong(idColumn));
                    blobHeader.put("u", cursor.getString(uuidColumn));
                    sessionAttributes = getAttributesFromSession(db, apiKey, getSessionIdForBlobId(db, cursor.getLong(idColumn)));
                } catch (JSONException e) {
                    Localytics.Log.w("Caught exception", e);
                }
                if (sessionAttributes != null) {
                    blobHeader.put("attrs", sessionAttributes);
                    JSONObject identifiers = getIdentifiers(db);
                    if (identifiers != null) {
                        blobHeader.put("ids", identifiers);
                    }
                    result.add(blobHeader);
                    Localytics.Log.w(result.toString());
                    Cursor blobEvents = null;
                    try {
                        blobEvents = db.query("upload_blob_events", new String[]{"_id", "events_key_ref"}, String.format("%s = ?", "upload_blobs_key_ref"), new String[]{Long.toString(cursor.getLong(idColumn))}, null, null, "events_key_ref");
                        int eventIdColumn = blobEvents.getColumnIndexOrThrow("events_key_ref");
                        while (blobEvents.moveToNext() && result.size() < 100) {
                            result.add(convertEventToJson(db, context, blobEvents.getLong(eventIdColumn), cursor.getLong(idColumn), apiKey));
                            db.delete("upload_blob_events", String.format("%s = ?", "_id"), new String[]{Integer.toString(blobEvents.getInt(blobEvents.getColumnIndexOrThrow("_id")))});
                        }
                        if (blobEvents != null) {
                            blobEvents.close();
                        }
                    } catch (Throwable th) {
                        if (blobEvents != null) {
                            blobEvents.close();
                        }
                        throw th;
                        break;
                    }
                } else {
                    break;
                }
            }
            Localytics.Log.v(String.format("JSON result is %s", result.toString()));
            return result;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static long wallTimeForEvent(SQLiteDatabase db, String uuid) {
        Cursor query = null;
        long wallTime = 0;
        try {
            query = db.query("events", new String[]{"wall_time"}, "uuid=?", new String[]{uuid}, null, null, null);
            if (query.moveToFirst()) {
                wallTime = query.getLong(0);
            }
            return wallTime;
        } finally {
            if (query != null) {
                query.close();
            }
        }
    }

    private static JSONObject convertAttributesToJson(SQLiteDatabase db, Context context, long eventId) throws JSONException {
        Cursor cursor = null;
        try {
            cursor = db.query("attributes", null, String.format("%s = ? AND %s != ? AND %s != ? AND %s != ? AND %s != ? AND %s != ? AND %s != ? AND %s != ? AND %s != ? AND %s != ? AND %s != ?", "events_key_ref", "attribute_key", "attribute_key", "attribute_key", "attribute_key", "attribute_key", "attribute_key", "attribute_key", "attribute_key", "attribute_key", "attribute_key"), new String[]{Long.toString(eventId), AttributesDbColumns.ATTRIBUTE_CUSTOM_DIMENSION_1, AttributesDbColumns.ATTRIBUTE_CUSTOM_DIMENSION_2, AttributesDbColumns.ATTRIBUTE_CUSTOM_DIMENSION_3, AttributesDbColumns.ATTRIBUTE_CUSTOM_DIMENSION_4, AttributesDbColumns.ATTRIBUTE_CUSTOM_DIMENSION_5, AttributesDbColumns.ATTRIBUTE_CUSTOM_DIMENSION_6, AttributesDbColumns.ATTRIBUTE_CUSTOM_DIMENSION_7, AttributesDbColumns.ATTRIBUTE_CUSTOM_DIMENSION_8, AttributesDbColumns.ATTRIBUTE_CUSTOM_DIMENSION_9, AttributesDbColumns.ATTRIBUTE_CUSTOM_DIMENSION_10}, null, null, null);
            if (cursor.getCount() != 0) {
                JSONObject attributes = new JSONObject();
                int keyColumn = cursor.getColumnIndexOrThrow("attribute_key");
                int valueColumn = cursor.getColumnIndexOrThrow("attribute_value");
                while (cursor.moveToNext()) {
                    String key = cursor.getString(keyColumn);
                    String value = cursor.getString(valueColumn);
                    attributes.put(key.substring(context.getPackageName().length() + 1, key.length()), value);
                }
            }
            if (cursor != null) {
                cursor.close();
            }
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private static long getApiKeyCreationTime(SQLiteDatabase db, String key) {
        Cursor cursor = null;
        try {
            cursor = db.query("api_keys", null, String.format("%s = ?", "api_key"), new String[]{key}, null, null, null);
            if (cursor.moveToFirst()) {
                return Math.round(cursor.getLong(cursor.getColumnIndexOrThrow("created_time")) / 1000.0d);
            }
            throw new RuntimeException("API key entry couldn't be found");
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private static JSONObject getAttributesFromSession(SQLiteDatabase db, String apiKey, long sessionId) throws JSONException {
        Cursor cursor = null;
        try {
            cursor = db.query("sessions", null, String.format("%s = ?", "_id"), new String[]{Long.toString(sessionId)}, null, null, null);
            if (cursor.moveToFirst()) {
                JSONObject result = new JSONObject();
                result.put("av", cursor.getString(cursor.getColumnIndexOrThrow("app_version")));
                result.put("dac", cursor.getString(cursor.getColumnIndexOrThrow("network_type")));
                String deviceID = cursor.getString(cursor.getColumnIndexOrThrow("device_android_id_hash"));
                if (!"".equals(deviceID)) {
                    result.put("du", deviceID);
                }
                result.put("dc", cursor.getString(cursor.getColumnIndexOrThrow("device_country")));
                result.put("dma", cursor.getString(cursor.getColumnIndexOrThrow("device_manufacturer")));
                result.put("dmo", cursor.getString(cursor.getColumnIndexOrThrow("device_model")));
                result.put("dov", cursor.getString(cursor.getColumnIndexOrThrow("android_version")));
                result.put("dp", "Android");
                result.put("dms", cursor.isNull(cursor.getColumnIndexOrThrow("device_serial_number_hash")) ? JSONObject.NULL : cursor.getString(cursor.getColumnIndexOrThrow("device_serial_number_hash")));
                result.put("dsdk", cursor.getInt(cursor.getColumnIndexOrThrow("android_sdk")));
                result.put("au", apiKey);
                result.put("lv", cursor.getString(cursor.getColumnIndexOrThrow("localytics_library_version")));
                result.put("dt", "a");
                result.put("caid", cursor.isNull(cursor.getColumnIndexOrThrow(License.DEVICE_ANDROID_ID)) ? JSONObject.NULL : cursor.getString(cursor.getColumnIndexOrThrow(License.DEVICE_ANDROID_ID)));
                if (!cursor.isNull(cursor.getColumnIndexOrThrow("device_advertising_id"))) {
                    result.put("gcadid", cursor.getString(cursor.getColumnIndexOrThrow("device_advertising_id")));
                }
                String installationID = cursor.getString(cursor.getColumnIndexOrThrow("iu"));
                if (installationID != null) {
                    result.put("iu", installationID);
                }
                result.put("dlc", cursor.getString(cursor.getColumnIndexOrThrow("locale_country")));
                result.put("dll", cursor.getString(cursor.getColumnIndexOrThrow("locale_language")));
                result.put("nca", cursor.getString(cursor.getColumnIndexOrThrow("network_carrier")));
                result.put("nc", cursor.getString(cursor.getColumnIndexOrThrow("network_country")));
                String fbAttribution = getStringFromAppInfo(db, "fb_attribution");
                if (fbAttribution != null) {
                    result.put("fbat", fbAttribution);
                }
                String playAttribution = getStringFromAppInfo(db, "play_attribution");
                if (playAttribution != null) {
                    result.put("aurl", playAttribution);
                }
                String registrationId = getStringFromAppInfo(db, "registration_id");
                if (registrationId != null) {
                    result.put("push", registrationId);
                }
                String firstAndroidId = getStringFromAppInfo(db, "first_android_id");
                if (firstAndroidId != null) {
                    result.put("aid", firstAndroidId);
                }
                String firstAdvertisingId = getStringFromAppInfo(db, "first_advertising_id");
                if (firstAdvertisingId != null) {
                    result.put("gadid", firstAdvertisingId);
                }
                String packageName = getStringFromAppInfo(db, "package_name");
                if (packageName != null) {
                    result.put("pkg", packageName);
                }
            }
            if (cursor != null) {
                cursor.close();
            }
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private static long getSessionIdForBlobId(SQLiteDatabase db, long blobId) {
        Cursor cursor = null;
        try {
            cursor = db.query("upload_blob_events", new String[]{"events_key_ref"}, String.format("%s = ?", "upload_blobs_key_ref"), new String[]{Long.toString(blobId)}, null, null, null);
            if (cursor.moveToFirst()) {
                long eventId = cursor.getLong(cursor.getColumnIndexOrThrow("events_key_ref"));
                if (cursor != null) {
                    cursor.close();
                }
                Cursor cursor2 = null;
                try {
                    cursor = db.query("events", new String[]{"session_key_ref"}, String.format("%s = ?", "_id"), new String[]{Long.toString(eventId)}, null, null, null);
                    if (cursor.moveToFirst()) {
                        long sessionId = cursor.getLong(cursor.getColumnIndexOrThrow("session_key_ref"));
                    }
                    throw new RuntimeException("No session associated with event");
                } finally {
                    if (cursor2 != null) {
                        cursor2.close();
                    }
                }
            }
            if (cursor != null) {
                cursor.close();
            }
            return -1L;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private static JSONObject getIdentifiers(SQLiteDatabase db) throws JSONException {
        Cursor cursor = null;
        try {
            cursor = db.query("identifiers", null, null, null, null, null, null);
            JSONObject result = null;
            while (cursor.moveToNext()) {
                if (result == null) {
                    result = new JSONObject();
                }
                result.put(cursor.getString(cursor.getColumnIndexOrThrow("key")), cursor.getString(cursor.getColumnIndexOrThrow("value")));
            }
            return result;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private static String getStringFromAppInfo(SQLiteDatabase db, String key) {
        Cursor cursor = null;
        try {
            cursor = db.query("info", null, null, null, null, null, null);
            if (cursor.moveToFirst()) {
                String string = cursor.getString(cursor.getColumnIndexOrThrow(key));
            }
            if (cursor != null) {
                cursor.close();
            }
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private static long getSessionIdForEventId(SQLiteDatabase db, long eventId) {
        Cursor cursor = null;
        try {
            cursor = db.query("events", new String[]{"session_key_ref"}, String.format("%s = ?", "_id"), new String[]{Long.toString(eventId)}, null, null, null);
            if (cursor.moveToFirst()) {
                return cursor.getLong(cursor.getColumnIndexOrThrow("session_key_ref"));
            }
            throw new RuntimeException();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private static String getSessionUuid(SQLiteDatabase db, long sessionId) {
        Cursor cursor = null;
        try {
            cursor = db.query("sessions", new String[]{"uuid"}, String.format("%s = ?", "_id"), new String[]{Long.toString(sessionId)}, null, null, null);
            if (cursor.moveToFirst()) {
                return cursor.getString(cursor.getColumnIndexOrThrow("uuid"));
            }
            throw new RuntimeException();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private static long getSessionStartTime(SQLiteDatabase db, long sessionId) {
        Cursor cursor = null;
        try {
            cursor = db.query("sessions", new String[]{"session_start_wall_time"}, String.format("%s = ?", "_id"), new String[]{Long.toString(sessionId)}, null, null, null);
            if (cursor.moveToFirst()) {
                return cursor.getLong(cursor.getColumnIndexOrThrow("session_start_wall_time"));
            }
            throw new RuntimeException();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private static long getElapsedTimeSinceLastSession(SQLiteDatabase db, long sessionId) {
        Cursor cursor = null;
        try {
            cursor = db.query("sessions", new String[]{"elapsed"}, String.format("%s = ?", "_id"), new String[]{Long.toString(sessionId)}, null, null, null);
            if (cursor.moveToFirst()) {
                return cursor.getLong(cursor.getColumnIndexOrThrow("elapsed"));
            }
            throw new RuntimeException();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private static String getCustomDimensionKey(int dimension) {
        return String.format("%s%s", "c", String.valueOf(dimension - 1));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void preUploadBuildBlobs(SQLiteDatabase db) {
        Set<Long> eventIds = new HashSet<>();
        Cursor eventsCursor = null;
        Cursor blob_eventsCursor = null;
        try {
            eventsCursor = db.query("events", PROJECTION_UPLOAD_EVENTS, null, null, null, null, EVENTS_SORT_ORDER);
            blob_eventsCursor = db.query("upload_blob_events", PROJECTION_UPLOAD_BLOBS, null, null, null, null, UPLOAD_BLOBS_EVENTS_SORT_ORDER);
            int idColumn = eventsCursor.getColumnIndexOrThrow("_id");
            Iterator i$ = new CursorJoiner(eventsCursor, JOINER_ARG_UPLOAD_EVENTS_COLUMNS, blob_eventsCursor, PROJECTION_UPLOAD_BLOBS).iterator();
            while (i$.hasNext()) {
                CursorJoiner.Result joinerResult = i$.next();
                switch (AnonymousClass1.$SwitchMap$android$database$CursorJoiner$Result[joinerResult.ordinal()]) {
                    case 1:
                        eventIds.add(Long.valueOf(eventsCursor.getLong(idColumn)));
                        break;
                }
            }
            if (eventIds.size() > 0) {
                ContentValues values = new ContentValues();
                values.put("uuid", UUID.randomUUID().toString());
                Long blobId = Long.valueOf(db.insert("upload_blobs", null, values));
                values.clear();
                for (Long x : eventIds) {
                    values.put("upload_blobs_key_ref", blobId);
                    values.put("events_key_ref", x);
                    db.insert("upload_blob_events", null, values);
                    values.clear();
                }
                values.put("processed_in_blob", blobId);
                db.update("event_history", values, SELECTION_UPLOAD_NULL_BLOBS, null);
                values.clear();
            }
        } finally {
            if (eventsCursor != null) {
                eventsCursor.close();
            }
            if (blob_eventsCursor != null) {
                blob_eventsCursor.close();
            }
        }
    }

    /* renamed from: com.localytics.android.MigrationDatabaseHelper$1, reason: invalid class name */
    /* loaded from: classes.dex */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$android$database$CursorJoiner$Result = new int[CursorJoiner.Result.values().length];

        static {
            try {
                $SwitchMap$android$database$CursorJoiner$Result[CursorJoiner.Result.LEFT.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$android$database$CursorJoiner$Result[CursorJoiner.Result.BOTH.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$android$database$CursorJoiner$Result[CursorJoiner.Result.RIGHT.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    /* loaded from: classes.dex */
    static final class EventFlow {
        static final String KEY_DATA_TYPE = "dt";
        static final String KEY_EVENT_UUID = "u";
        static final String KEY_FLOW_NEW = "nw";
        static final String KEY_FLOW_OLD = "od";
        static final String KEY_SESSION_START_TIME = "ss";
        static final String VALUE_DATA_TYPE = "f";

        private EventFlow() {
            throw new UnsupportedOperationException("This class is non-instantiable");
        }

        /* loaded from: classes.dex */
        static final class Element {
            static final String TYPE_EVENT = "e";
            static final String TYPE_SCREEN = "s";

            private Element() {
                throw new UnsupportedOperationException("This class is non-instantiable");
            }
        }
    }
}
