package com.nuance.connect.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseCorruptException;
import android.database.sqlite.SQLiteException;
import android.os.Message;
import com.nuance.connect.comm.MessageAPI;
import com.nuance.connect.sqlite.MasterDatabase;
import com.nuance.connect.util.ConcurrentCallbackSet;
import com.nuance.connect.util.HandlerThread;
import com.nuance.connect.util.Logger;
import com.nuance.connect.util.StringUtils;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public final class ReportingDataSource {
    private static final String AGGREGATE_TABLE = "report_aa";
    private static final String DATAPOINT_TABLE = "report_ab";
    private static final String DB_NAME = "reporting";
    private static final int DB_VERSION = 1;
    private static final String ENCODING = "UTF-8";
    private static final String EXTRA_DELIMITER = ", ";
    private static final String FIELD_AGG_POINT_COUNT = "f";
    private static final String FIELD_AGG_POINT_INTERVAL = "e";
    private static final String FIELD_AGG_POINT_VALUE = "d";
    private static final String FIELD_EXTRA = "g";
    private static final String FIELD_ID = "a";
    private static final String FIELD_NAME = "b";
    private static final String FIELD_START_TIME = "h";
    private static final String FIELD_TIMESTAMP = "j";
    private static final String FIELD_UPDATED_TIME = "i";
    private static final String FIELD_VALUE = "c";
    private static final String NAME_LIST = "NAME_LIST";
    private static final String ROW_ID = "rowid";
    private volatile int aggregateLimit;
    private final MasterDatabase.DatabaseTable aggregateTable;
    private final Context context;
    private volatile int individualLimit;
    private final MasterDatabase.DatabaseTable individualTable;
    private volatile long maxSize;
    private static final Logger.Log log = Logger.getLog(Logger.LoggerType.DEVELOPER, ReportingDataSource.class.getSimpleName());
    private static final MasterDatabase.TableSchema aggregateTableSchema = new MasterDatabase.TableSchema() { // from class: com.nuance.connect.sqlite.ReportingDataSource.1
        @Override // com.nuance.connect.sqlite.MasterDatabase.TableSchema
        public final String getPrimaryKey() {
            return null;
        }

        @Override // com.nuance.connect.sqlite.MasterDatabase.TableSchema
        public final String getTableName() {
            return ReportingDataSource.AGGREGATE_TABLE;
        }

        @Override // com.nuance.connect.sqlite.MasterDatabase.TableSchema
        public final Map<String, MasterDatabase.ElementType> getTableProperties() {
            HashMap hashMap = new HashMap();
            hashMap.put(ReportingDataSource.FIELD_ID, MasterDatabase.ElementType.TEXT);
            hashMap.put(ReportingDataSource.FIELD_NAME, MasterDatabase.ElementType.TEXT);
            hashMap.put(ReportingDataSource.FIELD_AGG_POINT_VALUE, MasterDatabase.ElementType.INTEGER);
            hashMap.put(ReportingDataSource.FIELD_AGG_POINT_INTERVAL, MasterDatabase.ElementType.INTEGER);
            hashMap.put(ReportingDataSource.FIELD_AGG_POINT_COUNT, MasterDatabase.ElementType.INTEGER);
            hashMap.put(ReportingDataSource.FIELD_EXTRA, MasterDatabase.ElementType.TEXT);
            hashMap.put(ReportingDataSource.FIELD_START_TIME, MasterDatabase.ElementType.DATETIME);
            hashMap.put(ReportingDataSource.FIELD_UPDATED_TIME, MasterDatabase.ElementType.INTEGER);
            return hashMap;
        }
    };
    private static final MasterDatabase.TableSchema individualTableSchema = new MasterDatabase.TableSchema() { // from class: com.nuance.connect.sqlite.ReportingDataSource.2
        @Override // com.nuance.connect.sqlite.MasterDatabase.TableSchema
        public final String getPrimaryKey() {
            return null;
        }

        @Override // com.nuance.connect.sqlite.MasterDatabase.TableSchema
        public final String getTableName() {
            return ReportingDataSource.DATAPOINT_TABLE;
        }

        @Override // com.nuance.connect.sqlite.MasterDatabase.TableSchema
        public final Map<String, MasterDatabase.ElementType> getTableProperties() {
            HashMap hashMap = new HashMap();
            hashMap.put(ReportingDataSource.FIELD_ID, MasterDatabase.ElementType.TEXT);
            hashMap.put(ReportingDataSource.FIELD_NAME, MasterDatabase.ElementType.TEXT);
            hashMap.put(ReportingDataSource.FIELD_VALUE, MasterDatabase.ElementType.TEXT);
            hashMap.put(ReportingDataSource.FIELD_EXTRA, MasterDatabase.ElementType.TEXT);
            hashMap.put(ReportingDataSource.FIELD_TIMESTAMP, MasterDatabase.ElementType.INTEGER);
            return hashMap;
        }
    };
    private static final HandlerThread handler = new HandlerThread() { // from class: com.nuance.connect.sqlite.ReportingDataSource.3
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.nuance.connect.util.HandlerThread
        public final Message handleMessage(Message message) {
            return null;
        }
    };
    private final ConcurrentCallbackSet<WeakReference<Callback>> callbacks = new ConcurrentCallbackSet<>();
    private final boolean enforceLimits = false;

    /* loaded from: classes.dex */
    public interface Callback {
        void onAggregate(boolean z, String str, String str2, double d, double d2, String str3, long j);

        void onIndividual(boolean z, String str, String str2, String str3, String str4, long j);
    }

    /* loaded from: classes.dex */
    public enum DataType {
        AGGREGATE,
        INDIVIDUAL
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class ReportingDatabaseSchema implements MasterDatabase.DatabaseSchema {
        private final WeakReference<ReportingDataSource> parentRef;

        ReportingDatabaseSchema(ReportingDataSource reportingDataSource) {
            this.parentRef = new WeakReference<>(reportingDataSource);
        }

        @Override // com.nuance.connect.sqlite.MasterDatabase.DatabaseSchema
        public Set<String> doNotEncrypt() {
            HashSet hashSet = new HashSet();
            hashSet.add(ReportingDataSource.ROW_ID);
            return hashSet;
        }

        @Override // com.nuance.connect.sqlite.MasterDatabase.DatabaseSchema
        public String getName() {
            return ReportingDataSource.DB_NAME;
        }

        @Override // com.nuance.connect.sqlite.MasterDatabase.DatabaseSchema
        public Set<MasterDatabase.TableSchema> getTableSchemas() {
            HashSet hashSet = new HashSet();
            if (this.parentRef.get() != null) {
                hashSet.add(ReportingDataSource.individualTableSchema);
                hashSet.add(ReportingDataSource.aggregateTableSchema);
            }
            return hashSet;
        }

        @Override // com.nuance.connect.sqlite.MasterDatabase.DatabaseSchema
        public void onMigration() {
            ReportingDataSource reportingDataSource = this.parentRef.get();
            if (reportingDataSource != null) {
                reportingDataSource.context.deleteDatabase(ReportingDataSource.DB_NAME);
            }
        }

        @Override // com.nuance.connect.sqlite.MasterDatabase.DatabaseSchema
        public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        }
    }

    public ReportingDataSource(Context context) {
        this.context = context;
        init();
        this.aggregateTable = MasterDatabase.from(context).getTableDatabase(aggregateTableSchema);
        this.individualTable = MasterDatabase.from(context).getTableDatabase(individualTableSchema);
    }

    public ReportingDataSource(Context context, int i, int i2, long j) {
        this.context = context.getApplicationContext();
        this.individualLimit = i;
        this.aggregateLimit = i2;
        this.maxSize = j;
        init();
        this.aggregateTable = MasterDatabase.from(this.context).getTableDatabase(aggregateTableSchema);
        this.individualTable = MasterDatabase.from(this.context).getTableDatabase(individualTableSchema);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void _clearAggregate(long j) {
        try {
            this.aggregateTable.delete(this.aggregateTable.getColumnName(FIELD_UPDATED_TIME) + " <= " + String.valueOf(j), null);
        } catch (SQLiteException e) {
            log.d("Issue clearing data points.");
        } catch (Exception e2) {
            log.d("Issue clearing aggregate data points.");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void _clearIndividual(long j) {
        try {
            this.individualTable.delete(this.individualTable.getColumnName(FIELD_TIMESTAMP) + " <= " + String.valueOf(j), null);
        } catch (SQLiteException e) {
            log.d("Issue clearing individual data points.");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:36:0x0196  */
    /* JADX WARN: Removed duplicated region for block: B:44:0x01bd A[LOOP:0: B:42:0x01b7->B:44:0x01bd, LOOP_END] */
    /* JADX WARN: Removed duplicated region for block: B:49:0x0189 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void _createAggregatePoint(java.lang.String r18, java.lang.String r19, double r20, double r22, java.lang.String r24, long r25) {
        /*
            Method dump skipped, instructions count: 696
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.nuance.connect.sqlite.ReportingDataSource._createAggregatePoint(java.lang.String, java.lang.String, double, double, java.lang.String, long):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void _createIndividualPoint(String str, String str2, String str3, String str4, long j) {
        boolean z;
        boolean z2;
        log.d("_createIndividualPoint (", str, EXTRA_DELIMITER, str2, ",", str3, ",", str4, ",", Long.valueOf(j), ")");
        if (str == null || str.length() == 0) {
            log.e("Creating Individual Point without a key");
        }
        if (str2 == null || str2.length() == 0) {
            log.e("Creating Individual Point without a name: " + str);
        }
        if (str3 == null || str3.length() == 0) {
            log.e("Creating Individual Point without a value: " + str);
        }
        try {
            try {
                this.individualTable.beginTransaction();
                ContentValues contentValues = new ContentValues();
                contentValues.put(FIELD_ID, str);
                contentValues.put(FIELD_NAME, str2);
                contentValues.put(FIELD_VALUE, str3);
                contentValues.put(FIELD_EXTRA, str4);
                contentValues.put(FIELD_TIMESTAMP, Long.valueOf(j));
                if (this.individualTable.insert(null, contentValues) > 0) {
                    this.individualTable.setTransactionSuccessful();
                    z2 = true;
                } else {
                    log.e("Error writing individual data point, unable to save data change.");
                    z2 = false;
                }
                z = z2;
            } catch (SQLiteDatabaseCorruptException e) {
                log.e("Error writing individual data point: ", e.getMessage());
                this.individualTable.deleteAll();
                z = false;
            } catch (SQLException e2) {
                log.e("Error writing individual data point: ", e2.getMessage());
                z = false;
            } catch (IllegalStateException e3) {
                log.e("Error writing individual data point: ", e3.getMessage());
                z = false;
            } catch (Exception e4) {
                log.e("Error writing individual data point: ", e4.getMessage());
                z = false;
            }
            if (z && this.enforceLimits && numIndividual() > this.individualLimit) {
                limitIndividual(this.individualLimit);
            }
            Iterator<Callback> it = getCallbacks().iterator();
            while (it.hasNext()) {
                it.next().onIndividual(z, str, str2, str3, str4, j);
            }
        } finally {
            this.individualTable.endTransaction();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void _enforceLimits(DataType dataType) {
        switch (dataType) {
            case AGGREGATE:
                if (numAggregate() >= this.aggregateLimit) {
                    limitAggregate(this.aggregateLimit);
                    return;
                }
                return;
            case INDIVIDUAL:
                if (numIndividual() >= this.individualLimit) {
                    limitIndividual(this.individualLimit);
                    return;
                }
                return;
            default:
                return;
        }
    }

    private void enforceLimits(final DataType dataType) {
        handler.process(new Runnable() { // from class: com.nuance.connect.sqlite.ReportingDataSource.8
            @Override // java.lang.Runnable
            public void run() {
                ReportingDataSource.this._enforceLimits(dataType);
            }
        });
    }

    private JSONObject generateAggregatePoint(ContentValues contentValues, JSONObject jSONObject) {
        boolean z;
        JSONObject jSONObject2 = new JSONObject();
        JSONObject jSONObject3 = new JSONObject();
        String asString = contentValues.getAsString(FIELD_NAME);
        try {
            jSONObject2 = jSONObject.getJSONObject(asString);
            z = true;
        } catch (JSONException e) {
            z = false;
        }
        if (!z) {
            try {
                jSONObject.accumulate(NAME_LIST, asString);
                jSONObject2.put(MessageAPI.AGGREGATE_POINTS, new JSONArray());
            } catch (JSONException e2) {
                log.e("Error generating point.");
            }
        }
        jSONObject3.put(MessageAPI.ID, contentValues.getAsString(FIELD_ID));
        jSONObject3.put(MessageAPI.EXTRA, contentValues.getAsString(FIELD_EXTRA));
        jSONObject3.put(MessageAPI.START, contentValues.getAsString(FIELD_START_TIME));
        jSONObject3.put(MessageAPI.END, contentValues.getAsLong(FIELD_UPDATED_TIME));
        jSONObject3.put(MessageAPI.TOTAL, StringUtils.safeStringToInt(contentValues.getAsString(FIELD_AGG_POINT_COUNT)));
        jSONObject3.put(MessageAPI.VALUE, StringUtils.safeStringToDouble(contentValues.getAsString(FIELD_AGG_POINT_VALUE)));
        jSONObject3.put(MessageAPI.INTERVAL, StringUtils.safeStringToDouble(contentValues.getAsString(FIELD_AGG_POINT_INTERVAL)));
        jSONObject2.put(MessageAPI.NAME, asString);
        jSONObject2.put(MessageAPI.TYPE, "SUM");
        jSONObject2.accumulate(MessageAPI.AGGREGATE_POINTS, jSONObject3);
        jSONObject.put(asString, jSONObject2);
        return jSONObject;
    }

    private JSONObject generateIndividualPoint(ContentValues contentValues) {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put(MessageAPI.ID, contentValues.getAsString(FIELD_ID));
            jSONObject.put(MessageAPI.NAME, contentValues.getAsString(FIELD_NAME));
            jSONObject.put(MessageAPI.VALUE, contentValues.getAsString(FIELD_VALUE));
            jSONObject.put(MessageAPI.EXTRA, contentValues.getAsString(FIELD_EXTRA));
            jSONObject.put("1", contentValues.get(FIELD_TIMESTAMP));
        } catch (JSONException e) {
            log.e("Error generating individual point.");
        }
        if (jSONObject.get(MessageAPI.ID) == null || "".equals(jSONObject.get(MessageAPI.ID)) || jSONObject.get(MessageAPI.NAME) == null || "".equals(jSONObject.get(MessageAPI.NAME)) || jSONObject.get(MessageAPI.VALUE) == null) {
            return null;
        }
        if ("".equals(jSONObject.get(MessageAPI.VALUE))) {
            return null;
        }
        return jSONObject;
    }

    private Set<Callback> getCallbacks() {
        HashSet hashSet = new HashSet();
        Iterator<WeakReference<Callback>> it = this.callbacks.iterator();
        while (it.hasNext()) {
            WeakReference<Callback> next = it.next();
            Callback callback = next.get();
            if (callback == null) {
                this.callbacks.remove(next);
            } else {
                hashSet.add(callback);
            }
        }
        return hashSet;
    }

    private void init() {
        MasterDatabase.from(this.context).connectDatabase(new ReportingDatabaseSchema(this));
        synchronized (ReportingDataSource.class) {
            if (!handler.isAlive()) {
                handler.start();
            }
        }
    }

    private void limitAggregate(int i) {
        Cursor cursor;
        long j;
        try {
            try {
                try {
                    cursor = this.aggregateTable.query(false, new String[]{FIELD_UPDATED_TIME}, null, null, null, null, this.aggregateTable.getColumnName(FIELD_UPDATED_TIME) + " DESC", String.valueOf(i));
                } catch (Throwable th) {
                    th = th;
                    cursor = null;
                }
                try {
                    if (cursor.getCount() > 0) {
                        cursor.moveToLast();
                        j = Long.parseLong(cursor.getString(0));
                    } else {
                        j = 0;
                    }
                    if (cursor != null) {
                        try {
                            cursor.close();
                        } catch (SQLException e) {
                        }
                    }
                    if (j > 0) {
                        this.aggregateTable.delete(this.aggregateTable.getColumnName(FIELD_UPDATED_TIME) + " < " + String.valueOf(j), null);
                    }
                } catch (Throwable th2) {
                    th = th2;
                    if (cursor != null) {
                        try {
                            cursor.close();
                        } catch (SQLException e2) {
                        }
                    }
                    throw th;
                }
            } catch (SQLException e3) {
                log.e("Error limiting aggregate data points: ", e3.getMessage());
            }
        } catch (SQLiteDatabaseCorruptException e4) {
            log.e("Error limiting aggregate data points: ", e4.getMessage());
        } catch (IllegalStateException e5) {
            log.e("Error limiting aggregate data points: ", e5.getMessage());
        } catch (Exception e6) {
            log.e("Error limiting aggregate data points: ", e6.getMessage());
        }
    }

    private void limitIndividual(int i) {
        Cursor cursor;
        long j;
        try {
            try {
                try {
                    cursor = this.individualTable.query(false, new String[]{FIELD_TIMESTAMP}, null, null, null, null, this.individualTable.getColumnName(FIELD_TIMESTAMP) + " DESC", String.valueOf(i));
                } catch (Throwable th) {
                    th = th;
                    cursor = null;
                }
                try {
                    if (cursor.getCount() > 0) {
                        cursor.moveToLast();
                        j = Long.parseLong(cursor.getString(0));
                    } else {
                        j = 0;
                    }
                    if (cursor != null) {
                        try {
                            cursor.close();
                        } catch (SQLException e) {
                        }
                    }
                    if (j > 0) {
                        this.individualTable.delete(this.individualTable.getColumnName(FIELD_TIMESTAMP) + " < " + String.valueOf(j), null);
                    }
                } catch (Throwable th2) {
                    th = th2;
                    if (cursor != null) {
                        try {
                            cursor.close();
                        } catch (SQLException e2) {
                        }
                    }
                    throw th;
                }
            } catch (SQLException e3) {
                log.e("Error limiting individual data points: ", e3.getMessage());
            }
        } catch (SQLiteDatabaseCorruptException e4) {
            log.e("Error limiting individual data points: ", e4.getMessage());
        } catch (IllegalStateException e5) {
            log.e("Error limiting individual data points: ", e5.getMessage());
        } catch (Exception e6) {
            log.e("Error limiting individual data points: ", e6.getMessage());
        }
    }

    private String makePlaceholders(int i) {
        StringBuilder sb = new StringBuilder();
        int i2 = i - 1;
        sb.append("?");
        for (int i3 = 0; i3 < i2; i3++) {
            sb.append(", ?");
        }
        return sb.toString();
    }

    private int numAggregate() {
        Cursor cursor;
        int i;
        try {
            cursor = this.aggregateTable.query(false, null, null, null, null, null, null, null);
            try {
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    i = cursor.getInt(0);
                } else {
                    i = 0;
                }
                if (cursor != null) {
                    try {
                        cursor.close();
                    } catch (SQLException e) {
                        log.d("Problem getting data count.");
                        return i;
                    } catch (Exception e2) {
                        log.d("Problem getting data count.");
                        return i;
                    }
                }
                return i;
            } catch (Throwable th) {
                th = th;
                if (cursor != null) {
                    try {
                        cursor.close();
                    } catch (SQLException e3) {
                        i = 0;
                        log.d("Problem getting data count.");
                        return i;
                    } catch (Exception e4) {
                        i = 0;
                        log.d("Problem getting data count.");
                        return i;
                    }
                }
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
            cursor = null;
        }
    }

    private int numIndividual() {
        Cursor cursor;
        int i;
        try {
            cursor = this.individualTable.query(false, null, null, null, null, null, null, null);
            try {
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    i = cursor.getInt(0);
                } else {
                    i = 0;
                }
                if (cursor != null) {
                    try {
                        cursor.close();
                    } catch (SQLException e) {
                        log.d("Problem getting individual data count.");
                        return i;
                    } catch (Exception e2) {
                        log.d("Problem getting individual data count.");
                        return i;
                    }
                }
                return i;
            } catch (Throwable th) {
                th = th;
                if (cursor != null) {
                    try {
                        cursor.close();
                    } catch (SQLException e3) {
                        i = 0;
                        log.d("Problem getting individual data count.");
                        return i;
                    } catch (Exception e4) {
                        i = 0;
                        log.d("Problem getting individual data count.");
                        return i;
                    }
                }
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
            cursor = null;
        }
    }

    public final void clearAggregate(final long j) {
        handler.process(new Runnable() { // from class: com.nuance.connect.sqlite.ReportingDataSource.6
            @Override // java.lang.Runnable
            public void run() {
                ReportingDataSource.this._clearAggregate(j);
            }
        });
    }

    public final void clearIndividual(final long j) {
        handler.process(new Runnable() { // from class: com.nuance.connect.sqlite.ReportingDataSource.7
            @Override // java.lang.Runnable
            public void run() {
                ReportingDataSource.this._clearIndividual(j);
            }
        });
    }

    public final void createAggregatePoint(final String str, final String str2, final double d, final double d2, final String str3, final long j) {
        handler.process(new Runnable() { // from class: com.nuance.connect.sqlite.ReportingDataSource.5
            @Override // java.lang.Runnable
            public void run() {
                ReportingDataSource.this._createAggregatePoint(str, str2, d, d2, str3, j);
            }
        });
    }

    public final void createIndividualPoint(final String str, final String str2, final String str3, final String str4, final long j) {
        handler.process(new Runnable() { // from class: com.nuance.connect.sqlite.ReportingDataSource.4
            @Override // java.lang.Runnable
            public void run() {
                ReportingDataSource.this._createIndividualPoint(str, str2, str3, str4, j);
            }
        });
    }

    public final JSONArray getAggregatePoints(String[] strArr, long j) {
        Cursor cursor = null;
        JSONArray jSONArray = new JSONArray();
        StringBuilder sb = new StringBuilder();
        String str = this.aggregateTable.getColumnName(FIELD_UPDATED_TIME) + " DESC";
        Cursor cursor2 = null;
        try {
        } catch (SQLException e) {
            log.d("Issue mapping values when generating aggregate value.");
        } catch (JSONException e2) {
            log.d("Issue mapping values when generating aggregate value.");
        } catch (Exception e3) {
            log.d("Issue mapping values when generating aggregate value.");
        }
        try {
            sb.append(this.aggregateTable.getColumnName(FIELD_UPDATED_TIME));
            sb.append(" <= ");
            sb.append(j);
            if (strArr != null) {
                if (strArr.length == 0) {
                    if (0 != 0) {
                        cursor2.close();
                    }
                    return jSONArray;
                }
                sb.append(" AND ");
                sb.append(this.aggregateTable.getColumnName(FIELD_ID));
                sb.append(" in (");
                sb.append(makePlaceholders(strArr.length));
                sb.append(")");
            }
            Cursor query = this.aggregateTable.query(false, null, sb.toString(), strArr, null, null, str, null);
            if (query != null) {
                try {
                    if (query.getCount() > 0) {
                        query.moveToFirst();
                        JSONObject jSONObject = new JSONObject();
                        jSONObject.put(NAME_LIST, new JSONArray());
                        JSONObject jSONObject2 = jSONObject;
                        for (int i = 0; i < query.getCount(); i++) {
                            ContentValues contentValues = new ContentValues();
                            DatabaseUtils.cursorRowToContentValues(query, contentValues);
                            jSONObject2 = generateAggregatePoint(contentValues, jSONObject2);
                            query.moveToNext();
                        }
                        JSONArray jSONArray2 = jSONObject2.getJSONArray(NAME_LIST);
                        for (int i2 = 0; i2 < jSONArray2.length(); i2++) {
                            jSONArray.put(jSONObject2.get(jSONArray2.getString(i2)));
                        }
                        if (query != null) {
                            query.close();
                        }
                        return jSONArray;
                    }
                } catch (Throwable th) {
                    th = th;
                    cursor = query;
                    if (cursor != null) {
                        cursor.close();
                    }
                    throw th;
                }
            }
            log.d("no aggregate points to send");
            if (query != null) {
                query.close();
            }
            return null;
        } catch (Throwable th2) {
            th = th2;
        }
    }

    /* JADX WARN: Finally extract failed */
    /* JADX WARN: Removed duplicated region for block: B:50:0x01cf A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:51:0x021f  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final android.util.Pair<java.lang.Integer, java.lang.String> getIndividualPointsFile(java.lang.String[] r17, long r18, java.lang.String r20, java.lang.String r21) {
        /*
            Method dump skipped, instructions count: 686
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.nuance.connect.sqlite.ReportingDataSource.getIndividualPointsFile(java.lang.String[], long, java.lang.String, java.lang.String):android.util.Pair");
    }

    public final long getLastAggregatePoint() {
        Cursor cursor;
        Exception e;
        long j;
        IllegalStateException e2;
        SQLException e3;
        SQLiteDatabaseCorruptException e4;
        Cursor query;
        try {
            query = this.aggregateTable.query(false, new String[]{FIELD_UPDATED_TIME}, null, null, null, null, this.aggregateTable.getColumnName(FIELD_UPDATED_TIME) + " DESC", "1");
        } catch (Throwable th) {
            th = th;
            cursor = null;
        }
        try {
            if (query.getCount() > 0) {
                query.moveToFirst();
                j = Long.parseLong(query.getString(0));
            } else {
                j = -1;
            }
            if (query != null) {
                try {
                    query.close();
                } catch (SQLiteDatabaseCorruptException e5) {
                    e4 = e5;
                    log.e("Error getting last aggregate point: ", e4.getMessage());
                    return j;
                } catch (SQLException e6) {
                    e3 = e6;
                    log.e("Error getting last aggregate point: ", e3.getMessage());
                    return j;
                } catch (IllegalStateException e7) {
                    e2 = e7;
                    log.e("Error getting last aggregate point: ", e2.getMessage());
                    return j;
                } catch (Exception e8) {
                    e = e8;
                    log.e("Error getting last aggregate point: ", e.getMessage());
                    return j;
                }
            }
            return j;
        } catch (Throwable th2) {
            th = th2;
            cursor = query;
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (SQLiteDatabaseCorruptException e9) {
                    e4 = e9;
                    j = -1;
                    log.e("Error getting last aggregate point: ", e4.getMessage());
                    return j;
                } catch (SQLException e10) {
                    e3 = e10;
                    j = -1;
                    log.e("Error getting last aggregate point: ", e3.getMessage());
                    return j;
                } catch (IllegalStateException e11) {
                    e2 = e11;
                    j = -1;
                    log.e("Error getting last aggregate point: ", e2.getMessage());
                    return j;
                } catch (Exception e12) {
                    e = e12;
                    j = -1;
                    log.e("Error getting last aggregate point: ", e.getMessage());
                    return j;
                }
            }
            throw th;
        }
    }

    public final long getLastIndividualPoint() {
        Cursor cursor;
        Exception e;
        long j;
        IllegalStateException e2;
        SQLException e3;
        SQLiteDatabaseCorruptException e4;
        Cursor query;
        try {
            query = this.individualTable.query(false, new String[]{FIELD_TIMESTAMP}, null, null, null, null, this.individualTable.getColumnName(FIELD_TIMESTAMP) + " DESC", "1");
        } catch (Throwable th) {
            th = th;
            cursor = null;
        }
        try {
            if (query.getCount() > 0) {
                query.moveToFirst();
                j = Long.parseLong(query.getString(0));
            } else {
                j = -1;
            }
            if (query != null) {
                try {
                    query.close();
                } catch (SQLiteDatabaseCorruptException e5) {
                    e4 = e5;
                    log.e("Error getting last individual point: ", e4.getMessage());
                    return j;
                } catch (SQLException e6) {
                    e3 = e6;
                    log.e("Error getting last individual point: ", e3.getMessage());
                    return j;
                } catch (IllegalStateException e7) {
                    e2 = e7;
                    log.e("Error getting last individual point: ", e2.getMessage());
                    return j;
                } catch (Exception e8) {
                    e = e8;
                    log.e("Error getting last individual point: ", e.getMessage());
                    return j;
                }
            }
            return j;
        } catch (Throwable th2) {
            th = th2;
            cursor = query;
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (SQLiteDatabaseCorruptException e9) {
                    e4 = e9;
                    j = -1;
                    log.e("Error getting last individual point: ", e4.getMessage());
                    return j;
                } catch (SQLException e10) {
                    e3 = e10;
                    j = -1;
                    log.e("Error getting last individual point: ", e3.getMessage());
                    return j;
                } catch (IllegalStateException e11) {
                    e2 = e11;
                    j = -1;
                    log.e("Error getting last individual point: ", e2.getMessage());
                    return j;
                } catch (Exception e12) {
                    e = e12;
                    j = -1;
                    log.e("Error getting last individual point: ", e.getMessage());
                    return j;
                }
            }
            throw th;
        }
    }

    public final int getMaxAggregateEntries() {
        return this.aggregateLimit;
    }

    public final int getMaxIndividualEntries() {
        return this.individualLimit;
    }

    public final boolean hasAggregate() {
        return getLastAggregatePoint() >= 0;
    }

    public final boolean hasIndividual() {
        return getLastIndividualPoint() >= 0;
    }

    public final void registerCallback(Callback callback) {
        if (callback != null) {
            this.callbacks.add(new WeakReference<>(callback));
        }
    }

    public final void setMaxAggregateEntries(int i) {
        this.aggregateLimit = i;
        if (this.enforceLimits) {
            enforceLimits(DataType.AGGREGATE);
        }
    }

    public final void setMaxIndividualEntries(int i) {
        this.individualLimit = i;
        if (this.enforceLimits) {
            enforceLimits(DataType.INDIVIDUAL);
        }
    }

    public final void unregisterCallback(Callback callback) {
        Iterator<WeakReference<Callback>> it = this.callbacks.iterator();
        while (it.hasNext()) {
            WeakReference<Callback> next = it.next();
            Callback callback2 = next.get();
            if (callback2 == null) {
                this.callbacks.remove(next);
            } else if (callback2.equals(callback)) {
                this.callbacks.remove(next);
            }
        }
    }

    public final void unregisterCallbacks() {
        this.callbacks.clear();
    }
}
