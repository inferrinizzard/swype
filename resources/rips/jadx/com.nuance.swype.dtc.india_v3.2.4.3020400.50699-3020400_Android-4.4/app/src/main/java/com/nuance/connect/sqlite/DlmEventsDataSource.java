package com.nuance.connect.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import com.nuance.connect.comm.MessageAPI;
import com.nuance.connect.sqlite.MasterDatabase;
import com.nuance.connect.util.Logger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public final class DlmEventsDataSource {
    private static final String APPNAME_TABLE = "dlm_ab";
    private static final String DB_NAME = "dlmevents";
    private static final int DB_VERSION = 1;
    private static final String EVENTS_TABLE = "dlm_aa";
    private static final String FIELD_APPID = "c";
    private static final String FIELD_APPNAME = "e";
    private static final String FIELD_CATEGORY = "a";
    private static final String FIELD_EVENT = "d";
    private static final String FIELD_INPUTTYPE = "f";
    private static final String FIELD_LOCALE = "k";
    private static final String FIELD_LOCALEID = "j";
    private static final String FIELD_LOCATION = "h";
    private static final String FIELD_LOCATIONID = "g";
    private static final String FIELD_TIMESTAMP = "b";
    private static final String LOCALE_TABLE = "dlm_ae";
    private static final String LOCATION_TABLE = "dlm_ad";
    private static final String NEWWORDS_TABLE = "dlm_ac";
    private static final String ROW_ID = "rowid";
    private static DlmEventsDatabaseSchema schemaInstance;
    private final MasterDatabase.DatabaseTable appNamesTable;
    private final Context context;
    private final MasterDatabase database;
    private final MasterDatabase.DatabaseTable eventsTable;
    private final DatabaseHandlerThread handler;
    private final MasterDatabase.DatabaseTable localeTable;
    private final MasterDatabase.DatabaseTable locationTable;
    private Logger.Log log = Logger.getLog(Logger.LoggerType.DEVELOPER, getClass().getSimpleName());
    private final MasterDatabase.DatabaseTable newWordsTable;
    private static final MasterDatabase.TableSchema eventsTableSchema = new MasterDatabase.TableSchema() { // from class: com.nuance.connect.sqlite.DlmEventsDataSource.1
        @Override // com.nuance.connect.sqlite.MasterDatabase.TableSchema
        public final String getPrimaryKey() {
            return null;
        }

        @Override // com.nuance.connect.sqlite.MasterDatabase.TableSchema
        public final String getTableName() {
            return DlmEventsDataSource.EVENTS_TABLE;
        }

        @Override // com.nuance.connect.sqlite.MasterDatabase.TableSchema
        public final Map<String, MasterDatabase.ElementType> getTableProperties() {
            HashMap hashMap = new HashMap();
            hashMap.put(DlmEventsDataSource.FIELD_EVENT, MasterDatabase.ElementType.BLOB);
            hashMap.put(DlmEventsDataSource.FIELD_TIMESTAMP, MasterDatabase.ElementType.DATETIME);
            hashMap.put(DlmEventsDataSource.FIELD_APPID, MasterDatabase.ElementType.INTEGER);
            hashMap.put(DlmEventsDataSource.FIELD_CATEGORY, MasterDatabase.ElementType.INTEGER);
            hashMap.put(DlmEventsDataSource.FIELD_INPUTTYPE, MasterDatabase.ElementType.INTEGER);
            hashMap.put(DlmEventsDataSource.FIELD_LOCALEID, MasterDatabase.ElementType.INTEGER);
            hashMap.put(DlmEventsDataSource.FIELD_LOCATIONID, MasterDatabase.ElementType.INTEGER);
            return hashMap;
        }
    };
    private static final MasterDatabase.TableSchema newWordsTableSchema = new MasterDatabase.TableSchema() { // from class: com.nuance.connect.sqlite.DlmEventsDataSource.2
        @Override // com.nuance.connect.sqlite.MasterDatabase.TableSchema
        public final String getPrimaryKey() {
            return null;
        }

        @Override // com.nuance.connect.sqlite.MasterDatabase.TableSchema
        public final String getTableName() {
            return DlmEventsDataSource.NEWWORDS_TABLE;
        }

        @Override // com.nuance.connect.sqlite.MasterDatabase.TableSchema
        public final Map<String, MasterDatabase.ElementType> getTableProperties() {
            HashMap hashMap = new HashMap();
            hashMap.put(DlmEventsDataSource.FIELD_EVENT, MasterDatabase.ElementType.BLOB);
            hashMap.put(DlmEventsDataSource.FIELD_TIMESTAMP, MasterDatabase.ElementType.DATETIME);
            hashMap.put(DlmEventsDataSource.FIELD_CATEGORY, MasterDatabase.ElementType.INTEGER);
            return hashMap;
        }
    };
    private static final MasterDatabase.TableSchema appNamesTableSchema = new MasterDatabase.TableSchema() { // from class: com.nuance.connect.sqlite.DlmEventsDataSource.3
        @Override // com.nuance.connect.sqlite.MasterDatabase.TableSchema
        public final String getPrimaryKey() {
            return DlmEventsDataSource.FIELD_APPID;
        }

        @Override // com.nuance.connect.sqlite.MasterDatabase.TableSchema
        public final String getTableName() {
            return DlmEventsDataSource.APPNAME_TABLE;
        }

        @Override // com.nuance.connect.sqlite.MasterDatabase.TableSchema
        public final Map<String, MasterDatabase.ElementType> getTableProperties() {
            HashMap hashMap = new HashMap();
            hashMap.put(DlmEventsDataSource.FIELD_APPID, MasterDatabase.ElementType.INTEGER);
            hashMap.put(DlmEventsDataSource.FIELD_APPNAME, MasterDatabase.ElementType.TEXT);
            return hashMap;
        }
    };
    private static final MasterDatabase.TableSchema locationTableSchema = new MasterDatabase.TableSchema() { // from class: com.nuance.connect.sqlite.DlmEventsDataSource.4
        @Override // com.nuance.connect.sqlite.MasterDatabase.TableSchema
        public final String getPrimaryKey() {
            return DlmEventsDataSource.FIELD_LOCATIONID;
        }

        @Override // com.nuance.connect.sqlite.MasterDatabase.TableSchema
        public final String getTableName() {
            return DlmEventsDataSource.LOCATION_TABLE;
        }

        @Override // com.nuance.connect.sqlite.MasterDatabase.TableSchema
        public final Map<String, MasterDatabase.ElementType> getTableProperties() {
            HashMap hashMap = new HashMap();
            hashMap.put(DlmEventsDataSource.FIELD_LOCATIONID, MasterDatabase.ElementType.INTEGER);
            hashMap.put(DlmEventsDataSource.FIELD_LOCATION, MasterDatabase.ElementType.TEXT);
            return hashMap;
        }
    };
    private static final MasterDatabase.TableSchema localeTableSchema = new MasterDatabase.TableSchema() { // from class: com.nuance.connect.sqlite.DlmEventsDataSource.5
        @Override // com.nuance.connect.sqlite.MasterDatabase.TableSchema
        public final String getPrimaryKey() {
            return DlmEventsDataSource.FIELD_LOCALEID;
        }

        @Override // com.nuance.connect.sqlite.MasterDatabase.TableSchema
        public final String getTableName() {
            return DlmEventsDataSource.LOCALE_TABLE;
        }

        @Override // com.nuance.connect.sqlite.MasterDatabase.TableSchema
        public final Map<String, MasterDatabase.ElementType> getTableProperties() {
            HashMap hashMap = new HashMap();
            hashMap.put(DlmEventsDataSource.FIELD_LOCALEID, MasterDatabase.ElementType.INTEGER);
            hashMap.put(DlmEventsDataSource.FIELD_LOCALE, MasterDatabase.ElementType.TEXT);
            return hashMap;
        }
    };

    /* loaded from: classes.dex */
    private static class DlmEventsDatabaseSchema implements MasterDatabase.DatabaseSchema {
        private final Context context;

        private DlmEventsDatabaseSchema(Context context) {
            this.context = context.getApplicationContext();
        }

        public static synchronized DlmEventsDatabaseSchema from(Context context) {
            DlmEventsDatabaseSchema dlmEventsDatabaseSchema;
            synchronized (DlmEventsDatabaseSchema.class) {
                if (DlmEventsDataSource.schemaInstance == null) {
                    DlmEventsDatabaseSchema unused = DlmEventsDataSource.schemaInstance = new DlmEventsDatabaseSchema(context);
                }
                dlmEventsDatabaseSchema = DlmEventsDataSource.schemaInstance;
            }
            return dlmEventsDatabaseSchema;
        }

        @Override // com.nuance.connect.sqlite.MasterDatabase.DatabaseSchema
        public Set<String> doNotEncrypt() {
            HashSet hashSet = new HashSet();
            hashSet.add(DlmEventsDataSource.ROW_ID);
            return hashSet;
        }

        @Override // com.nuance.connect.sqlite.MasterDatabase.DatabaseSchema
        public String getName() {
            return DlmEventsDataSource.DB_NAME;
        }

        @Override // com.nuance.connect.sqlite.MasterDatabase.DatabaseSchema
        public Set<MasterDatabase.TableSchema> getTableSchemas() {
            HashSet hashSet = new HashSet();
            hashSet.add(DlmEventsDataSource.eventsTableSchema);
            hashSet.add(DlmEventsDataSource.newWordsTableSchema);
            hashSet.add(DlmEventsDataSource.appNamesTableSchema);
            hashSet.add(DlmEventsDataSource.locationTableSchema);
            hashSet.add(DlmEventsDataSource.localeTableSchema);
            return hashSet;
        }

        @Override // com.nuance.connect.sqlite.MasterDatabase.DatabaseSchema
        public void onMigration() {
            this.context.deleteDatabase(DlmEventsDataSource.DB_NAME);
        }

        @Override // com.nuance.connect.sqlite.MasterDatabase.DatabaseSchema
        public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        }
    }

    public DlmEventsDataSource(Context context) {
        this.context = context.getApplicationContext();
        this.database = MasterDatabase.from(this.context);
        this.database.connectDatabase(DlmEventsDatabaseSchema.from(context));
        this.eventsTable = this.database.getTableDatabase(eventsTableSchema);
        this.newWordsTable = this.database.getTableDatabase(newWordsTableSchema);
        this.appNamesTable = this.database.getTableDatabase(appNamesTableSchema);
        this.locationTable = this.database.getTableDatabase(locationTableSchema);
        this.localeTable = this.database.getTableDatabase(localeTableSchema);
        this.handler = new DatabaseHandlerThread(this);
        this.handler.start();
    }

    private boolean clearUnusedLUEntries(MasterDatabase.DatabaseTable databaseTable, String str) {
        boolean z = true;
        try {
            try {
                try {
                    databaseTable.beginTransaction();
                    databaseTable.delete("? NOT IN (SELECT ? FROM " + this.eventsTable.getName() + ")", new String[]{str, str});
                    databaseTable.setTransactionSuccessful();
                } catch (SQLException e) {
                    this.log.e("clearUnusedLUEntries(", databaseTable.getName(), ") failed message: ", e.getMessage());
                    databaseTable.endTransaction();
                    z = false;
                }
            } catch (SQLiteException e2) {
                this.log.e("clearUnusedLUEntries(", databaseTable.getName(), ") failed message: ", e2.getMessage());
                databaseTable.endTransaction();
                z = false;
            }
            return z;
        } finally {
            databaseTable.endTransaction();
        }
    }

    private long getLUIndex(String str, MasterDatabase.DatabaseTable databaseTable, String str2, String str3) {
        Cursor cursor;
        Exception exc;
        long j;
        SQLException sQLException;
        long j2;
        Cursor query;
        long insert;
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(databaseTable.getColumnName(str3)).append(" = ?");
            query = databaseTable.query(false, new String[]{str2, str3}, sb.toString(), new String[]{str}, null, null, null, null);
        } catch (Throwable th) {
            th = th;
            cursor = null;
        }
        try {
            query.moveToFirst();
            if (query.getCount() > 0) {
                insert = query.getInt(query.getColumnIndexOrThrow(str2));
            } else {
                ContentValues contentValues = new ContentValues();
                contentValues.put(str3, str);
                insert = databaseTable.insert(null, contentValues);
            }
            if (query == null) {
                return insert;
            }
            try {
                if (query.isClosed()) {
                    return insert;
                }
                query.close();
                return insert;
            } catch (SQLException e) {
                sQLException = e;
                j2 = insert;
                this.log.e("Error in [", databaseTable.getName(), "] getIndex(", str, "): ", sQLException.getMessage());
                return j2;
            } catch (Exception e2) {
                exc = e2;
                j = insert;
                this.log.e("Error in [", databaseTable.getName(), "] getIndex(", str, "): ", exc.getMessage());
                return j;
            }
        } catch (Throwable th2) {
            th = th2;
            cursor = query;
            if (cursor != null) {
                try {
                    if (!cursor.isClosed()) {
                        cursor.close();
                    }
                } catch (SQLException e3) {
                    sQLException = e3;
                    j2 = -1;
                    this.log.e("Error in [", databaseTable.getName(), "] getIndex(", str, "): ", sQLException.getMessage());
                    return j2;
                } catch (Exception e4) {
                    exc = e4;
                    j = -1;
                    this.log.e("Error in [", databaseTable.getName(), "] getIndex(", str, "): ", exc.getMessage());
                    return j;
                }
            }
            throw th;
        }
    }

    private long getLastRowIdForTable(MasterDatabase.DatabaseTable databaseTable) {
        Cursor cursor;
        try {
        } catch (SQLiteException e) {
            this.log.e("getLastRowId failed; message: " + e.getMessage());
        } catch (Exception e2) {
            this.log.e("getLastRowId failed; message: " + e2.getMessage());
        }
        try {
            Cursor query = databaseTable.query(false, new String[]{ROW_ID}, null, null, null, null, "rowid DESC", "1");
            try {
                if (query.getCount() <= 0 || !query.moveToFirst()) {
                    if (query != null) {
                        query.close();
                    }
                    return -1L;
                }
                long j = query.getLong(0);
                if (query == null) {
                    return j;
                }
                query.close();
                return j;
            } catch (Throwable th) {
                th = th;
                cursor = query;
                if (cursor != null) {
                    cursor.close();
                }
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
            cursor = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void _clearEvents(int i, long j) {
        MasterDatabase.DatabaseTable databaseTable;
        String[] strArr;
        StringBuilder sb;
        Cursor cursor;
        Cursor query;
        try {
            try {
                this.eventsTable.beginTransaction();
                strArr = new String[]{FIELD_EVENT, FIELD_TIMESTAMP, FIELD_APPID, FIELD_CATEGORY};
                sb = new StringBuilder();
                sb.append(this.eventsTable.getColumnName(FIELD_CATEGORY)).append(" = ").append(i);
                try {
                    query = this.eventsTable.query(false, strArr, sb.toString(), null, null, null, null, null);
                } catch (Throwable th) {
                    th = th;
                    cursor = null;
                }
            } catch (SQLiteException e) {
                this.log.e("clearEvents failed rowid=", Long.valueOf(j), " message: ", e.getMessage());
                databaseTable = this.eventsTable;
            } catch (Exception e2) {
                this.log.e("clearEvents failed rowid=", Long.valueOf(j), " message: ", e2.getMessage());
                databaseTable = this.eventsTable;
            }
            try {
                long count = query.getCount();
                if (query != null) {
                    query.close();
                }
                this.eventsTable.delete("rowid <= " + j, null);
                try {
                    query = this.eventsTable.query(false, strArr, sb.toString(), null, null, null, null, null);
                    this.log.d("clearEvents removed: ", Long.valueOf(count - query.getCount()));
                    this.eventsTable.setTransactionSuccessful();
                    databaseTable = this.eventsTable;
                    databaseTable.endTransaction();
                    clearUnusedLUEntries(this.appNamesTable, FIELD_APPID);
                    clearUnusedLUEntries(this.localeTable, FIELD_LOCALE);
                    clearUnusedLUEntries(this.locationTable, FIELD_LOCATION);
                } finally {
                    if (query != null) {
                        query.close();
                    }
                }
            } catch (Throwable th2) {
                th = th2;
                cursor = query;
                if (cursor != null) {
                    cursor.close();
                }
                throw th;
            }
        } catch (Throwable th3) {
            this.eventsTable.endTransaction();
            throw th3;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean _insertEvent(String str, int i, long j, String str2, String str3, String str4, int i2) {
        MasterDatabase.DatabaseTable databaseTable;
        if (this.eventsTable.isFull()) {
            return false;
        }
        boolean z = false;
        try {
            try {
                this.eventsTable.beginTransaction();
                long j2 = -1;
                long j3 = -1;
                long j4 = -1;
                if (str2 != null && str2.length() > 0) {
                    j4 = getLUIndex(str2, this.appNamesTable, FIELD_APPID, FIELD_APPNAME);
                }
                if (str3 != null && str3.length() > 0) {
                    j2 = getLUIndex(str3, this.locationTable, FIELD_LOCATIONID, FIELD_LOCATION);
                }
                if (str4 != null && str4.length() > 0) {
                    j3 = getLUIndex(str4, this.localeTable, FIELD_LOCALEID, FIELD_LOCALE);
                }
                ContentValues contentValues = new ContentValues();
                contentValues.put(FIELD_CATEGORY, Integer.valueOf(i));
                contentValues.put(FIELD_EVENT, str);
                contentValues.put(FIELD_TIMESTAMP, Long.valueOf(j));
                contentValues.put(FIELD_APPID, Long.valueOf(j4));
                contentValues.put(FIELD_LOCATIONID, Long.valueOf(j2));
                contentValues.put(FIELD_LOCALEID, Long.valueOf(j3));
                contentValues.put(FIELD_INPUTTYPE, Integer.valueOf(i2));
                if (this.eventsTable.insert(null, contentValues) < 0) {
                    this.log.e("insertEvent failed to insert event: ", str);
                } else {
                    this.eventsTable.setTransactionSuccessful();
                    z = true;
                }
                databaseTable = this.eventsTable;
            } catch (SQLException e) {
                this.log.e("insertEvent failed to insert event: (", str, ") SQL message", e.getMessage());
                z = false;
                databaseTable = this.eventsTable;
            }
            databaseTable.endTransaction();
            return z;
        } catch (Throwable th) {
            this.eventsTable.endTransaction();
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean _insertHighPriorityEvent(String str, int i, long j) {
        boolean z = true;
        try {
            if (this.newWordsTable.isFull()) {
                return false;
            }
            try {
                this.newWordsTable.beginTransaction();
                ContentValues contentValues = new ContentValues();
                contentValues.put(FIELD_CATEGORY, Integer.valueOf(i));
                contentValues.put(FIELD_TIMESTAMP, Long.valueOf(j));
                contentValues.put(FIELD_EVENT, str);
                if (this.newWordsTable.insert(null, contentValues) < 0) {
                    this.log.e("insertHighPriorityEvents failed to insert event: ", str);
                } else {
                    this.newWordsTable.setTransactionSuccessful();
                }
            } catch (SQLException e) {
                this.log.e("insertHighPriorityEvent failed to insert event: (", str, ") SQL message", e.getMessage());
                this.newWordsTable.endTransaction();
                z = false;
            }
            return z;
        } finally {
            this.newWordsTable.endTransaction();
        }
    }

    public final void clearEvents(int i, long j) {
        this.handler.process(1, i, 0, Long.valueOf(j));
    }

    public final void clearHighPriorityEvents(int i, long j) {
        MasterDatabase.DatabaseTable databaseTable;
        String[] strArr;
        Cursor cursor = null;
        try {
            try {
                this.newWordsTable.beginTransaction();
                strArr = new String[]{FIELD_CATEGORY, FIELD_EVENT, FIELD_TIMESTAMP};
            } catch (SQLiteException e) {
                this.log.e("clearHighPriorityEvents failed cat=", Integer.valueOf(i), " rowid=", Long.valueOf(j), " message: ", e.getMessage());
                databaseTable = this.newWordsTable;
            } catch (Exception e2) {
                this.log.e("clearHighPriorityEvents failed cat=", Integer.valueOf(i), " rowid=", Long.valueOf(j), " message: ", e2.getMessage());
                databaseTable = this.newWordsTable;
            }
            try {
                Cursor query = this.newWordsTable.query(false, strArr, null, null, null, null, null, null);
                if (query != null) {
                    query.close();
                }
                this.newWordsTable.delete("rowid <= " + j, null);
                try {
                    query = this.newWordsTable.query(false, strArr, null, null, null, null, null, null);
                    this.newWordsTable.setTransactionSuccessful();
                    databaseTable = this.newWordsTable;
                    databaseTable.endTransaction();
                } finally {
                    if (query != null) {
                        query.close();
                    }
                }
            } catch (Throwable th) {
                if (0 != 0) {
                    cursor.close();
                }
                throw th;
            }
        } catch (Throwable th2) {
            this.newWordsTable.endTransaction();
            throw th2;
        }
    }

    public final int[] getEventCategories() {
        int[] iArr;
        int[] iArr2;
        Exception e;
        SQLiteException e2;
        Cursor cursor = null;
        try {
            new StringBuffer("SELECT DISTINCT ").append(this.eventsTable.getColumnName(FIELD_CATEGORY)).append(" FROM ").append(this.eventsTable.getName());
            Cursor query = this.eventsTable.query(true, new String[]{FIELD_CATEGORY}, null, null, null, null, null, null);
            try {
                if (query.getCount() <= 0) {
                    if (query != null) {
                        try {
                            if (!query.isClosed()) {
                                query.close();
                            }
                        } catch (SQLiteException e3) {
                            e2 = e3;
                            iArr2 = null;
                            this.log.e("getEventCategories failed: ", e2.getMessage());
                            return iArr2;
                        } catch (Exception e4) {
                            e = e4;
                            iArr2 = null;
                            this.log.e("getEventCategories failed: ", e.getMessage());
                            return iArr2;
                        }
                    }
                    return null;
                }
                iArr2 = new int[query.getCount()];
                try {
                    query.moveToFirst();
                    for (int i = 0; i < query.getCount(); i++) {
                        iArr2[i] = query.getInt(query.getColumnIndex(FIELD_CATEGORY));
                        query.moveToNext();
                    }
                    if (query == null) {
                        return iArr2;
                    }
                    try {
                        if (query.isClosed()) {
                            return iArr2;
                        }
                        query.close();
                        return iArr2;
                    } catch (SQLiteException e5) {
                        e2 = e5;
                        this.log.e("getEventCategories failed: ", e2.getMessage());
                        return iArr2;
                    } catch (Exception e6) {
                        e = e6;
                        this.log.e("getEventCategories failed: ", e.getMessage());
                        return iArr2;
                    }
                } catch (Throwable th) {
                    cursor = query;
                    iArr = iArr2;
                    th = th;
                    if (cursor != null) {
                        try {
                            if (!cursor.isClosed()) {
                                cursor.close();
                            }
                        } catch (SQLiteException e7) {
                            iArr2 = iArr;
                            e2 = e7;
                            this.log.e("getEventCategories failed: ", e2.getMessage());
                            return iArr2;
                        } catch (Exception e8) {
                            iArr2 = iArr;
                            e = e8;
                            this.log.e("getEventCategories failed: ", e.getMessage());
                            return iArr2;
                        }
                    }
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
                iArr = null;
                cursor = query;
            }
        } catch (Throwable th3) {
            th = th3;
            iArr = null;
        }
    }

    public final JSONArray getEvents(int i, long j) {
        Cursor cursor = null;
        JSONArray jSONArray = new JSONArray();
        try {
            String[] strArr = {String.valueOf(i), String.valueOf(j)};
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("SELECT * FROM ").append(this.eventsTable.getName()).append(" LEFT OUTER JOIN ").append(this.appNamesTable.getName()).append(" ON ").append(this.eventsTable.getName()).append(".").append(this.eventsTable.getColumnName(FIELD_APPID)).append(" = ").append(this.appNamesTable.getName()).append(".").append(this.appNamesTable.getColumnName(FIELD_APPID)).append(" LEFT OUTER JOIN ").append(this.locationTable.getName()).append(" ON ").append(this.eventsTable.getName()).append(".").append(this.eventsTable.getColumnName(FIELD_LOCATIONID)).append(" = ").append(this.locationTable.getName()).append(".").append(this.locationTable.getColumnName(FIELD_LOCATIONID)).append(" LEFT OUTER JOIN ").append(this.localeTable.getName()).append(" ON ").append(this.eventsTable.getName()).append(".").append(this.eventsTable.getColumnName(FIELD_LOCALEID)).append(" = ").append(this.localeTable.getName()).append(".").append(this.localeTable.getColumnName(FIELD_LOCALEID)).append(" WHERE ").append(this.database.getActualColumnName(FIELD_CATEGORY)).append(" = ? AND ").append(this.eventsTable.getName()).append(".rowid <= ?");
            try {
                cursor = MasterDatabase.from(this.context).rawQuery(stringBuffer.toString(), strArr, null);
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    do {
                        ContentValues contentValues = new ContentValues();
                        DatabaseUtils.cursorRowToContentValues(cursor, contentValues);
                        JSONObject jSONObject = new JSONObject();
                        jSONObject.put("1", contentValues.getAsLong(FIELD_TIMESTAMP));
                        jSONObject.put(MessageAPI.RECORD, contentValues.getAsString(FIELD_EVENT));
                        jSONObject.put(MessageAPI.APPLICATION, contentValues.getAsString(FIELD_APPNAME));
                        jSONObject.put(MessageAPI.LOCALE, contentValues.getAsString(FIELD_LOCALE));
                        jSONObject.put(MessageAPI.INPUT_TYPE, contentValues.getAsInteger(FIELD_INPUTTYPE));
                        jSONObject.put(MessageAPI.LOCATION, contentValues.getAsString(FIELD_LOCATION));
                        jSONArray.put(jSONObject);
                    } while (cursor.moveToNext());
                    if (cursor != null) {
                        cursor.close();
                    }
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        } catch (SQLiteException e) {
            this.log.e("getEvents failed: ", e.getMessage());
        } catch (JSONException e2) {
            this.log.e("getEvents failed: ", e2.getMessage());
        } catch (Exception e3) {
            this.log.e("getEvents failed: ", e3.getMessage());
        }
        return jSONArray;
    }

    public final JSONArray getHighPriorityEvents(int i) {
        String[] strArr;
        StringBuffer stringBuffer;
        Cursor cursor;
        JSONArray jSONArray = new JSONArray();
        try {
            strArr = new String[]{FIELD_EVENT, FIELD_CATEGORY};
            stringBuffer = new StringBuffer();
            stringBuffer.append(this.newWordsTable.getColumnName(FIELD_CATEGORY)).append(" = ").append(i);
        } catch (SQLiteException e) {
            this.log.e("getHighPriorityEvents failed: ", e.getMessage());
        } catch (Exception e2) {
            this.log.e("getHighPriorityEvents failed: ", e2.getMessage());
        }
        try {
            cursor = this.newWordsTable.query(false, strArr, stringBuffer.toString(), null, null, null, null, null);
            try {
                if (cursor.getCount() <= 0) {
                    if (cursor != null) {
                        cursor.close();
                    }
                    return jSONArray;
                }
                cursor.moveToFirst();
                do {
                    ContentValues contentValues = new ContentValues();
                    DatabaseUtils.cursorRowToContentValues(cursor, contentValues);
                    jSONArray.put(contentValues.getAsString(FIELD_EVENT));
                } while (cursor.moveToNext());
                if (cursor != null) {
                    cursor.close();
                }
                return jSONArray;
            } catch (Throwable th) {
                th = th;
                if (cursor != null) {
                    cursor.close();
                }
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
            cursor = null;
        }
    }

    public final long getLastHighPriorityRowId() {
        return getLastRowIdForTable(this.newWordsTable);
    }

    public final long getLastRowId() {
        return getLastRowIdForTable(this.eventsTable);
    }

    public final boolean insertEvent(String str, int i, long j, String str2, String str3, String str4, int i2) {
        this.handler.addEvent(new DlmEvent(str, i, j, str2, str3, str4, i2, false));
        return true;
    }

    public final boolean insertHighPriorityEvent(String str, int i, long j) {
        this.handler.addEvent(new DlmEvent(str, i, j, null, null, null, 0, true));
        return true;
    }

    public final boolean isFull() {
        return false;
    }

    public final void reset() {
        this.log.v("DlmEventsDataStore.reset()");
        this.eventsTable.deleteAll();
        this.newWordsTable.deleteAll();
        this.appNamesTable.deleteAll();
        this.locationTable.deleteAll();
        this.localeTable.deleteAll();
    }
}
