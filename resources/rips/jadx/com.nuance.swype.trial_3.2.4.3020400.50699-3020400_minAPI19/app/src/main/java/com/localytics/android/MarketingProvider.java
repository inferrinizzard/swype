package com.localytics.android;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import bolts.MeasurementEvent;
import com.facebook.internal.ServerProtocol;
import com.localytics.android.BaseProvider;
import com.localytics.android.Localytics;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
class MarketingProvider extends BaseProvider {
    static final int DATABASE_VERSION = 5;

    /* JADX INFO: Access modifiers changed from: package-private */
    public MarketingProvider(String dbName, LocalyticsDao localyticsDao) {
        super(localyticsDao, dbName);
        determineCreativeLocation(this.mDbPath);
        moveCreativesIfNecessary(localyticsDao.getAndroidVersionInt());
        this.mDb = new MarketingDatabaseHelper(this.mDbPath, 5, localyticsDao).getWritableDatabase();
    }

    MarketingProvider(LocalyticsDao localyticsDao) {
        super(localyticsDao);
    }

    @Override // com.localytics.android.BaseProvider
    long maxSiloDbSize() {
        return 10 * Constants.BYTES_IN_A_MEGABYTE;
    }

    @Override // com.localytics.android.BaseProvider
    boolean canAddToDB() {
        return true;
    }

    @TargetApi(21)
    void moveCreativesIfNecessary(int sdkInt) {
        if (sdkInt >= 21) {
            File oldCreativeRootDir = new File(CreativeManager.getOldZipFileDirPath(this.mLocalyticsDao));
            File newCreativeRootDir = new File(CreativeManager.getZipFileDirPath(this.mLocalyticsDao));
            try {
                try {
                    if (this.mDatabaseJustMoved) {
                        Utils.copyDirectory(oldCreativeRootDir, newCreativeRootDir);
                        Localytics.Log.v("Moved creatives from " + oldCreativeRootDir + " to newCreativeRootDir");
                    }
                    if (this.mDatabaseJustMoved) {
                        Utils.deleteFile(oldCreativeRootDir);
                        Localytics.Log.v("Removed old creatives folder.");
                        return;
                    }
                    return;
                } catch (Exception e) {
                    if (oldCreativeRootDir.isDirectory()) {
                        Utils.deleteFile(newCreativeRootDir);
                        Localytics.Log.e("Failed to copy creative file", e);
                    } else {
                        Localytics.Log.v("Old creative directory doesn't exist", e);
                    }
                    if (this.mDatabaseJustMoved) {
                        Utils.deleteFile(oldCreativeRootDir);
                        Localytics.Log.v("Removed old creatives folder.");
                        return;
                    }
                    return;
                }
            } catch (Throwable th) {
                if (this.mDatabaseJustMoved) {
                    Utils.deleteFile(oldCreativeRootDir);
                    Localytics.Log.v("Removed old creatives folder.");
                }
                throw th;
            }
        }
        Localytics.Log.v("No need to move creatives.");
    }

    @TargetApi(21)
    void determineCreativeLocation(String dbPath) {
        Context context = this.mLocalyticsDao.getAppContext();
        if (this.mLocalyticsDao.getAndroidVersionInt() >= 21) {
            boolean useNewLocation = dbPath.startsWith(context.getNoBackupFilesDir().getAbsolutePath());
            this.mLocalyticsDao.useNewCreativeLocation(useNewLocation);
            Localytics.Log.v("Use new creatives location after Lollipop.");
        } else {
            this.mLocalyticsDao.useNewCreativeLocation(false);
            Localytics.Log.v("Use old creatives location.");
        }
    }

    /* loaded from: classes.dex */
    static final class MarketingRulesV3Columns implements BaseColumns {
        static final String AB_TEST = "ab_test";
        static final String CAMPAIGN_ID = "campaign_id";
        static final String CONTROL_GROUP = "control_group";
        static final String DEVICES = "devices";
        static final String DISPLAY_SECONDS = "display_seconds";
        static final String DISPLAY_SESSION = "display_session";
        static final String EXPIRATION = "expiration";
        static final String INTERNET_REQUIRED = "internet_required";
        static final String LOCATION = "location";
        static final String PHONE_LOCATION = "phone_location";
        static final String PHONE_SIZE_HEIGHT = "phone_size_height";
        static final String PHONE_SIZE_WIDTH = "phone_size_width";
        static final String RULE_NAME = "rule_name_non_unique";
        static final String RULE_NAME_UNIQUE = "rule_name";
        static final String SCHEMA_VERSION = "schema_version";
        static final String TABLET_LOCATION = "tablet_location";
        static final String TABLET_SIZE_HEIGHT = "tablet_size_height";
        static final String TABLET_SIZE_WIDTH = "tablet_size_width";
        static final String TABLE_NAME = "marketing_rules";
        static final String TIME_TO_DISPLAY = "time_to_display";
        static final String VERSION = "version";

        private MarketingRulesV3Columns() {
            throw new UnsupportedOperationException("This class is non-instantiable");
        }
    }

    /* loaded from: classes.dex */
    static final class MarketingDisplayedV3Columns implements BaseColumns {
        static final String CAMPAIGN_ID = "campaign_id";
        static final String TABLE_NAME = "marketing_displayed";

        private MarketingDisplayedV3Columns() {
            throw new UnsupportedOperationException("This class is non-instantiable");
        }
    }

    /* loaded from: classes.dex */
    static final class MarketingRuleEventV3Columns implements BaseColumns {
        static final String EVENT_NAME = "event_name";
        static final String RULE_ID_REF = "rule_id_ref";
        static final String TABLE_NAME = "marketing_ruleevent";

        private MarketingRuleEventV3Columns() {
            throw new UnsupportedOperationException("This class is non-instantiable");
        }
    }

    /* loaded from: classes.dex */
    static final class MarketingConditionsV3Columns implements BaseColumns {
        static final String ATTRIBUTE_NAME = "attribute_name";
        static final String OPERATOR = "operator";
        static final String RULE_ID_REF = "rule_id_ref";
        static final String TABLE_NAME = "marketing_conditions";

        private MarketingConditionsV3Columns() {
            throw new UnsupportedOperationException("This class is non-instantiable");
        }
    }

    /* loaded from: classes.dex */
    static final class MarketingConditionValuesV3Columns implements BaseColumns {
        static final String CONDITION_ID_REF = "condition_id_ref";
        static final String TABLE_NAME = "marketing_condition_values";
        static final String VALUE = "value";

        private MarketingConditionValuesV3Columns() {
            throw new UnsupportedOperationException("This class is non-instantiable");
        }
    }

    /* loaded from: classes.dex */
    static final class FrequencyCappingV3Columns implements BaseColumns {
        static final String CAMPAIGN_ID = "campaign_id";
        static final String IGNORE_GLOBAL = "ignore_global";
        static final String MAX_DISPLAY_COUNT = "max_display_count";
        static final String TABLE_NAME = "frequency_capping_rules";

        private FrequencyCappingV3Columns() {
            throw new UnsupportedOperationException("This class is non-instantiable");
        }
    }

    /* loaded from: classes.dex */
    static final class FrequencyCappingDisplayFrequencyV3Columns implements BaseColumns {
        static final String DISPLAY_FREQUENCY_COUNT = "count";
        static final String DISPLAY_FREQUENCY_DAYS = "days";
        static final String FREQUENCY_ID = "frequency_id";
        static final String TABLE_NAME = "frequency_capping_display_frequencies";

        private FrequencyCappingDisplayFrequencyV3Columns() {
            throw new UnsupportedOperationException("This class is non-instantiable");
        }
    }

    /* loaded from: classes.dex */
    static final class FrequencyCappingBlackoutDateV3Columns implements BaseColumns {
        static final String END_DATE = "end";
        static final String FREQUENCY_ID = "frequency_id";
        static final String RULE_GROUP_ID = "rule_group_id";
        static final String START_DATE = "start";
        static final String TABLE_NAME = "frequency_capping_blackout_dates";

        private FrequencyCappingBlackoutDateV3Columns() {
            throw new UnsupportedOperationException("This class is non-instantiable");
        }
    }

    /* loaded from: classes.dex */
    static final class FrequencyCappingBlackoutWeekdayV3Columns implements BaseColumns {
        static final String DAY = "day";
        static final String FREQUENCY_ID = "frequency_id";
        static final String RULE_GROUP_ID = "rule_group_id";
        static final String TABLE_NAME = "frequency_capping_blackout_weekdays";

        private FrequencyCappingBlackoutWeekdayV3Columns() {
            throw new UnsupportedOperationException("This class is non-instantiable");
        }
    }

    /* loaded from: classes.dex */
    static final class FrequencyCappingBlackoutTimeV3Columns implements BaseColumns {
        static final String END_TIME = "end";
        static final String FREQUENCY_ID = "frequency_id";
        static final String RULE_GROUP_ID = "rule_group_id";
        static final String START_TIME = "start";
        static final String TABLE_NAME = "frequency_capping_blackout_times";

        private FrequencyCappingBlackoutTimeV3Columns() {
            throw new UnsupportedOperationException("This class is non-instantiable");
        }
    }

    /* loaded from: classes.dex */
    static final class CamapignsDisplayedV3Columns implements BaseColumns {
        static final String CAMPAIGN_ID = "campaign_id";
        static final String DATE = "date";
        static final String IGNORE_GLOBAL = "ignore_global";
        static final String TABLE_NAME = "campaigns_displayed";

        private CamapignsDisplayedV3Columns() {
            throw new UnsupportedOperationException("This class is non-instantiable");
        }
    }

    /* loaded from: classes.dex */
    static final class InboxCampaignV3Columns implements BaseColumns {
        static final String AB_TEST = "ab_test";
        static final String CAMPAIGN_ID = "campaign_id";
        static final String CREATIVE_LOCATION = "creative_location";
        static final String EXPIRATION = "expiration";
        static final String LISTING_SUMMARY = "listing_summary";
        static final String LISTING_TITLE = "listing_title";
        static final String READ = "read";
        static final String RECEIVED_DATE = "received_date";
        static final String RULE_NAME = "rule_name";
        static final String SCHEMA_VERSION = "schema_version";
        static final String SORT_ORDER = "sort_order";
        static final String TABLE_NAME = "inbox_campaigns";
        static final String THUMBNAIL_LOCATION = "thumbnail_location";
        static final String VERSION = "version";

        private InboxCampaignV3Columns() {
            throw new UnsupportedOperationException("This class is non-instantiable");
        }
    }

    /* loaded from: classes.dex */
    static final class InboxCampaignAttributesV3Columns implements BaseColumns {
        static final String INBOX_ID_REF = "inbox_id_ref";
        static final String KEY = "key";
        static final String TABLE_NAME = "inbox_campaign_attributes";
        static final String VALUE = "value";

        private InboxCampaignAttributesV3Columns() {
            throw new UnsupportedOperationException("This class is non-instantiable");
        }
    }

    /* loaded from: classes.dex */
    static final class PlacesCampaignV3Columns implements BaseColumns {
        static final String AB_TEST = "ab_test";
        static final String CAMPAIGN_ID = "campaign_id";
        static final String CONTROL_GROUP = "control_group";
        static final String CREATIVE_ID = "creative_id";
        static final String CREATIVE_TYPE = "creative_type";
        static final String EXPIRATION = "expiration";
        static final String MESSAGE = "message";
        static final String RULE_NAME = "rule_name";
        static final String SCHEMA_VERSION = "schema_version";
        static final String SOUND_FILENAME = "sound_filename";
        static final String TABLE_NAME = "places_campaigns";
        static final String VERSION = "version";

        private PlacesCampaignV3Columns() {
            throw new UnsupportedOperationException("This class is non-instantiable");
        }
    }

    /* loaded from: classes.dex */
    static final class PlacesCampaignAttributesV3Columns implements BaseColumns {
        static final String CAMPAIGN_ID = "campaign_id";
        static final String KEY = "key";
        static final String TABLE_NAME = "places_campaign_attributes";
        static final String VALUE = "value";

        private PlacesCampaignAttributesV3Columns() {
            throw new UnsupportedOperationException("This class is non-instantiable");
        }
    }

    /* loaded from: classes.dex */
    static final class PlacesCampaignsGeofenceTriggerV3Columns implements BaseColumns {
        static final String CAMPAIGN_ID = "campaign_id";
        static final String PLACE_ID = "place_id";
        static final String TABLE_NAME = "places_campaigns_geofence_triggers";

        private PlacesCampaignsGeofenceTriggerV3Columns() {
            throw new UnsupportedOperationException("This class is non-instantiable");
        }
    }

    /* loaded from: classes.dex */
    static final class PlacesCampaignsEventV3Columns implements BaseColumns {
        static final String CAMPAIGN_ID = "campaign_id";
        static final String EVENT = "event";
        static final String TABLE_NAME = "places_campaigns_events";

        private PlacesCampaignsEventV3Columns() {
            throw new UnsupportedOperationException("This class is non-instantiable");
        }
    }

    /* loaded from: classes.dex */
    static final class PlacesCampaignsDisplayedV3Columns implements BaseColumns {
        static final String CAMPAIGN_ID = "campaign_id";
        static final String TABLE_NAME = "places_campaigns_displayed";

        private PlacesCampaignsDisplayedV3Columns() {
            throw new UnsupportedOperationException("This class is non-instantiable");
        }
    }

    /* loaded from: classes.dex */
    static class MarketingDatabaseHelper extends BaseProvider.LocalyticsDatabaseHelper {
        MarketingDatabaseHelper(String name, int version, LocalyticsDao localyticsDao) {
            super(name, version, localyticsDao);
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onCreate(SQLiteDatabase db) {
            if (db == null) {
                throw new IllegalArgumentException("db cannot be null");
            }
            onUpgrade(db, 0, 5);
        }

        @Override // com.localytics.android.BaseProvider.LocalyticsDatabaseHelper
        protected void migrateV2ToV3(SQLiteDatabase db) {
            db.execSQL(String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s INTEGER NOT NULL, %s INTEGER NOT NULL, %s INTEGER, %s INTEGER, %s TEXT NOT NULL, %s TEXT NOT NULL, %s INTEGER NOT NULL, %s INTEGER NOT NULL, %s TEXT NOT NULL, %s INTEGER NOT NULL, %s INTEGER NOT NULL, %s INTEGER, %s INTEGER NOT NULL, %s TEXT, %s TEXT UNIQUE NOT NULL, %s TEXT NOT NULL, %s TEXT NOT NULL)", "marketing_rules", "_id", "campaign_id", "expiration", "display_seconds", "display_session", ServerProtocol.FALLBACK_DIALOG_PARAM_VERSION, "phone_location", "phone_size_width", "phone_size_height", "tablet_location", "tablet_size_width", "tablet_size_height", "time_to_display", "internet_required", "ab_test", "rule_name", "location", "devices"));
            db.execSQL(String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT NOT NULL, %s INTEGER REFERENCES %s(%s) NOT NULL);", "marketing_ruleevent", "_id", MeasurementEvent.MEASUREMENT_EVENT_NAME_KEY, "rule_id_ref", "marketing_rules", "_id"));
            db.execSQL(String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY);", "marketing_displayed", "campaign_id"));
            db.execSQL(String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT NOT NULL, %s TEXT NOT NULL, %s INTEGER REFERENCES %s(%s) NOT NULL);", "marketing_conditions", "_id", "attribute_name", "operator", "rule_id_ref", "marketing_rules", "_id"));
            db.execSQL(String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT NOT NULL, %s INTEGER REFERENCES %s(%s) NOT NULL);", "marketing_condition_values", "_id", "value", "condition_id_ref", "marketing_conditions", "_id"));
            if (oldDB != null) {
                synchronized (oldDB) {
                    Cursor cursor = null;
                    ContentValues values = new ContentValues();
                    try {
                        cursor = oldDB.query("amp_rules", null, null, null, null, null, "_id ASC");
                        while (cursor.moveToNext()) {
                            values.put("_id", Integer.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("_id"))));
                            values.put("campaign_id", Integer.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("campaign_id"))));
                            values.put("expiration", Integer.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("expiration"))));
                            values.put("display_seconds", Integer.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("display_seconds"))));
                            values.put("display_session", Integer.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("display_session"))));
                            values.put(ServerProtocol.FALLBACK_DIALOG_PARAM_VERSION, cursor.getString(cursor.getColumnIndexOrThrow(ServerProtocol.FALLBACK_DIALOG_PARAM_VERSION)));
                            values.put("phone_location", cursor.getString(cursor.getColumnIndexOrThrow("phone_location")));
                            values.put("phone_size_width", Integer.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("phone_size_width"))));
                            values.put("phone_size_height", Integer.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("phone_size_height"))));
                            values.put("tablet_location", cursor.getString(cursor.getColumnIndexOrThrow("tablet_location")));
                            values.put("tablet_size_width", Integer.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("tablet_size_width"))));
                            values.put("tablet_size_height", Integer.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("tablet_size_height"))));
                            values.put("time_to_display", Integer.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("time_to_display"))));
                            values.put("internet_required", Integer.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("internet_required"))));
                            values.put("ab_test", cursor.getString(cursor.getColumnIndexOrThrow("ab_test")));
                            values.put("rule_name", cursor.getString(cursor.getColumnIndexOrThrow("rule_name")));
                            values.put("location", cursor.getString(cursor.getColumnIndexOrThrow("location")));
                            values.put("devices", cursor.getString(cursor.getColumnIndexOrThrow("devices")));
                            db.insert("marketing_rules", null, values);
                            values.clear();
                        }
                        if (cursor != null) {
                            cursor.close();
                            cursor = null;
                        }
                        try {
                            cursor = oldDB.query("amp_ruleevent", null, null, null, null, null, "_id ASC");
                            while (cursor.moveToNext()) {
                                values.put("_id", Integer.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("_id"))));
                                values.put(MeasurementEvent.MEASUREMENT_EVENT_NAME_KEY, cursor.getString(cursor.getColumnIndexOrThrow(MeasurementEvent.MEASUREMENT_EVENT_NAME_KEY)));
                                values.put("rule_id_ref", Integer.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("rule_id_ref"))));
                                db.insert("marketing_ruleevent", null, values);
                                values.clear();
                            }
                            if (cursor != null) {
                                cursor.close();
                                cursor = null;
                            }
                            try {
                                cursor = oldDB.query("amp_displayed", null, null, null, null, null, "_id ASC");
                                while (cursor.moveToNext()) {
                                    if (cursor.getInt(cursor.getColumnIndexOrThrow("displayed")) == 1) {
                                        values.put("campaign_id", Integer.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("campaign_id"))));
                                        db.insert("marketing_displayed", null, values);
                                        values.clear();
                                    }
                                }
                                if (cursor != null) {
                                    cursor.close();
                                    cursor = null;
                                }
                                try {
                                    cursor = oldDB.query("amp_conditions", null, null, null, null, null, "_id ASC");
                                    while (cursor.moveToNext()) {
                                        values.put("_id", Integer.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("_id"))));
                                        values.put("attribute_name", cursor.getString(cursor.getColumnIndexOrThrow("attribute_name")));
                                        values.put("operator", cursor.getString(cursor.getColumnIndexOrThrow("operator")));
                                        values.put("rule_id_ref", Integer.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("rule_id_ref"))));
                                        db.insert("marketing_conditions", null, values);
                                        values.clear();
                                    }
                                    if (cursor != null) {
                                        cursor.close();
                                        cursor = null;
                                    }
                                    try {
                                        cursor = oldDB.query("amp_condition_values", null, null, null, null, null, "_id ASC");
                                        while (cursor.moveToNext()) {
                                            values.put("_id", Integer.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("_id"))));
                                            values.put("value", cursor.getString(cursor.getColumnIndexOrThrow("value")));
                                            values.put("condition_id_ref", Integer.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("condition_id_ref"))));
                                            db.insert("marketing_condition_values", null, values);
                                            values.clear();
                                        }
                                        if (cursor != null) {
                                            cursor.close();
                                        }
                                        cleanUpOldDB();
                                    } finally {
                                    }
                                } finally {
                                }
                            } finally {
                                if (cursor != null) {
                                    cursor.close();
                                }
                            }
                        } finally {
                            if (cursor != null) {
                                cursor.close();
                            }
                        }
                    } finally {
                        if (cursor != null) {
                            cursor.close();
                        }
                    }
                }
            }
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (oldVersion <= 0) {
                migrateV2ToV3(db);
            }
            if (oldVersion < 2) {
                addNonUniqueRuleName(db);
            }
            if (oldVersion < 3) {
                setUpFrequencyCappingTables(db);
                addControlGroup(db);
                addSchemaVersion(db);
            }
            if (oldVersion < 4) {
                addInboxCampaignTables(db);
            }
            if (oldVersion < 5) {
                addPlacesCampaignTables(db);
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

        protected void addNonUniqueRuleName(SQLiteDatabase db) {
            db.execSQL(String.format("ALTER TABLE %s ADD COLUMN %s TEXT;", "marketing_rules", "rule_name_non_unique"));
            db.execSQL(String.format("UPDATE %s SET %s = %s;", "marketing_rules", "rule_name_non_unique", "rule_name"));
        }

        protected void addControlGroup(SQLiteDatabase db) {
            db.execSQL(String.format("ALTER TABLE %s ADD COLUMN %s INTEGER DEFAULT 0;", "marketing_rules", "control_group"));
        }

        protected void addSchemaVersion(SQLiteDatabase db) {
            db.execSQL(String.format("ALTER TABLE %s ADD COLUMN %s INTEGER DEFAULT 1;", "marketing_rules", "schema_version"));
        }

        protected void setUpFrequencyCappingTables(SQLiteDatabase db) {
            db.execSQL(String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s INTEGER UNIQUE, %s INTEGER, %s INTEGER);", "frequency_capping_rules", "_id", "campaign_id", "max_display_count", "ignore_global"));
            db.execSQL(String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s INTEGER NOT NULL, %s INTEGER NOT NULL, %s INTEGER NOT NULL, FOREIGN KEY(%s) REFERENCES %s(%s) ON DELETE CASCADE);", "frequency_capping_display_frequencies", "_id", "frequency_id", "count", "days", "frequency_id", "frequency_capping_rules", "_id"));
            db.execSQL(String.format("CREATE TABLE %s (%s INTEGER NOT NULL, %s INTEGER NOT NULL, %s INTEGER NOT NULL, %s INTEGER NOT NULL, FOREIGN KEY(%s) REFERENCES %s(%s) ON DELETE CASCADE);", "frequency_capping_blackout_dates", "frequency_id", "rule_group_id", "start", "end", "frequency_id", "frequency_capping_rules", "_id"));
            db.execSQL(String.format("CREATE TABLE %s (%s INTEGER NOT NULL, %s INTEGER NOT NULL, %s INTEGER NOT NULL, FOREIGN KEY(%s) REFERENCES %s(%s) ON DELETE CASCADE);", "frequency_capping_blackout_weekdays", "frequency_id", "rule_group_id", "day", "frequency_id", "frequency_capping_rules", "_id"));
            db.execSQL(String.format("CREATE TABLE %s (%s INTEGER NOT NULL, %s INTEGER NOT NULL, %s INTEGER NOT NULL, %s INTEGER NOT NULL, FOREIGN KEY(%s) REFERENCES %s(%s) ON DELETE CASCADE);", "frequency_capping_blackout_times", "frequency_id", "rule_group_id", "start", "end", "frequency_id", "frequency_capping_rules", "_id"));
            List<Integer> displayedCampaigns = new ArrayList<>();
            Cursor cursor = null;
            try {
                cursor = db.query("marketing_displayed", new String[]{"campaign_id"}, null, null, null, null, null);
                while (cursor.moveToNext()) {
                    displayedCampaigns.add(Integer.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("campaign_id"))));
                }
                db.execSQL(String.format("CREATE TABLE %s (%s INTEGER NOT NULL, %s INTEGER NOT NULL, %s INTEGER NOT NULL);", "campaigns_displayed", "campaign_id", "date", "ignore_global"));
                for (Integer campaignId : displayedCampaigns) {
                    db.execSQL(String.format("INSERT INTO %s VALUES (?, datetime(0,'unixepoch'), ?);", "campaigns_displayed"), new Integer[]{campaignId, 1});
                }
                db.execSQL(String.format("DROP TABLE %s", "marketing_displayed"));
                db.execSQL(String.format("DELETE FROM %s", "marketing_rules"));
                BaseProvider.deleteDirectory(new File(CreativeManager.getZipFileDirPath(this.mLocalyticsDao)));
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }

        protected void addInboxCampaignTables(SQLiteDatabase db) {
            db.execSQL(String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s INTEGER UNIQUE NOT NULL, %s INTEGER NOT NULL, %s INTEGER NOT NULL, %s INTEGER, %s TEXT, %s TEXT NOT NULL, %s TEXT NOT NULL, %s TEXT, %s INTEGER NOT NULL, %s INTEGER NOT NULL DEFAULT 0, %s INTEGER NOT NULL DEFAULT 1, %s TEXT, %s TEXT);", "inbox_campaigns", "_id", "campaign_id", "expiration", ServerProtocol.FALLBACK_DIALOG_PARAM_VERSION, "received_date", "ab_test", "rule_name", "listing_title", "listing_summary", "sort_order", "read", "schema_version", "thumbnail_location", "creative_location"));
            db.execSQL(String.format("CREATE TABLE %s (%s TEXT NOT NULL, %s TEXT NOT NULL,%s INTEGER REFERENCES %s(%s) ON DELETE CASCADE);", "inbox_campaign_attributes", "key", "value", "inbox_id_ref", "inbox_campaigns", "_id"));
        }

        protected void addPlacesCampaignTables(SQLiteDatabase db) {
            db.execSQL(String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY,%s INTEGER NOT NULL,%s TEXT,%s INTEGER, %s INTEGER NOT NULL, %s TEXT, %s TEXT NOT NULL, %s INTEGER DEFAULT 0,%s INTEGER DEFAULT 1,%s TEXT,%s TEXT);", "places_campaigns", "campaign_id", "creative_id", "creative_type", "expiration", ServerProtocol.FALLBACK_DIALOG_PARAM_VERSION, "ab_test", "rule_name", "control_group", "schema_version", "message", "sound_filename"));
            db.execSQL(String.format("CREATE TABLE %s (%s TEXT NOT NULL, %s TEXT NOT NULL,%s INTEGER REFERENCES %s(%s) ON DELETE CASCADE);", "places_campaign_attributes", "key", "value", "campaign_id", "places_campaigns", "campaign_id"));
            db.execSQL(String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s INTEGER NOT NULL, %s INTEGER REFERENCES %s(%s) ON DELETE CASCADE);", "places_campaigns_geofence_triggers", "_id", "place_id", "campaign_id", "places_campaigns", "campaign_id"));
            db.execSQL(String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT NOT NULL, %s INTEGER REFERENCES %s(%s) ON DELETE CASCADE);", "places_campaigns_events", "_id", "event", "campaign_id", "places_campaigns", "campaign_id"));
            db.execSQL(String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY);", "places_campaigns_displayed", "campaign_id"));
        }
    }
}
