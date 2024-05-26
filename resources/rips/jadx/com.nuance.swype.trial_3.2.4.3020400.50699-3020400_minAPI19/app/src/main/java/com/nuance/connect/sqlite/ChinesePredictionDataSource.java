package com.nuance.connect.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.SystemClock;
import com.nuance.connect.proto.Prediction;
import com.nuance.connect.sqlite.MasterDatabase;
import com.nuance.connect.util.Logger;
import com.nuance.connect.util.StringUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/* loaded from: classes.dex */
public class ChinesePredictionDataSource {
    private static final String DB_NAME = "chineseprediction";
    private static final int DB_VERSION = 2;
    private static final String FIELD_RESULT_APPLICATION_NAME = "k";
    private static final String FIELD_RESULT_ATTRIBUTE = "f";
    private static final String FIELD_RESULT_CCPS_VERSION = "i";
    private static final String FIELD_RESULT_CLOUD_TIME = "g";
    private static final String FIELD_RESULT_COUNTRY = "l";
    private static final String FIELD_RESULT_CREATED_STAMP = "j";
    private static final String FIELD_RESULT_FULLSPELL = "e";
    private static final String FIELD_RESULT_PHRASE = "c";
    private static final String FIELD_RESULT_PREDICTION_ID = "a";
    private static final String FIELD_RESULT_SPELL = "d";
    private static final String FIELD_RESULT_TOTAL_TIME = "h";
    private static final String FIELD_RESULT_TYPE = "b";
    private static final int MAX_DATABASE_SIZE = 1048576;
    private static final long MAX_WAIT_TRANSMIT_TIME = 600000;
    private static final String PREDICTION_TABLE = "chineseprediction_aa";
    public static final int RESULT_TYPE_CANCELED = 2;
    public static final int RESULT_TYPE_COMPLETED = 3;
    public static final int RESULT_TYPE_FAILED = 1;
    public static final int RESULT_TYPE_PENDING = 0;
    private static final String ROWID = "rowid";
    public static final String UNKNOWN = "UNKNOWN";
    private static final MasterDatabase.TableSchema predictionTableSchema = new MasterDatabase.TableSchema() { // from class: com.nuance.connect.sqlite.ChinesePredictionDataSource.1
        @Override // com.nuance.connect.sqlite.MasterDatabase.TableSchema
        public final String getPrimaryKey() {
            return null;
        }

        @Override // com.nuance.connect.sqlite.MasterDatabase.TableSchema
        public final String getTableName() {
            return ChinesePredictionDataSource.PREDICTION_TABLE;
        }

        @Override // com.nuance.connect.sqlite.MasterDatabase.TableSchema
        public final Map<String, MasterDatabase.ElementType> getTableProperties() {
            HashMap hashMap = new HashMap();
            hashMap.put(ChinesePredictionDataSource.FIELD_RESULT_PREDICTION_ID, MasterDatabase.ElementType.TEXT);
            hashMap.put(ChinesePredictionDataSource.FIELD_RESULT_TYPE, MasterDatabase.ElementType.INTEGER);
            hashMap.put(ChinesePredictionDataSource.FIELD_RESULT_PHRASE, MasterDatabase.ElementType.TEXT);
            hashMap.put(ChinesePredictionDataSource.FIELD_RESULT_SPELL, MasterDatabase.ElementType.TEXT);
            hashMap.put(ChinesePredictionDataSource.FIELD_RESULT_FULLSPELL, MasterDatabase.ElementType.TEXT);
            hashMap.put(ChinesePredictionDataSource.FIELD_RESULT_ATTRIBUTE, MasterDatabase.ElementType.TEXT);
            hashMap.put(ChinesePredictionDataSource.FIELD_RESULT_CLOUD_TIME, MasterDatabase.ElementType.INTEGER);
            hashMap.put(ChinesePredictionDataSource.FIELD_RESULT_TOTAL_TIME, MasterDatabase.ElementType.INTEGER);
            hashMap.put(ChinesePredictionDataSource.FIELD_RESULT_CREATED_STAMP, MasterDatabase.ElementType.INTEGER);
            hashMap.put(ChinesePredictionDataSource.FIELD_RESULT_CCPS_VERSION, MasterDatabase.ElementType.STRING);
            hashMap.put(ChinesePredictionDataSource.FIELD_RESULT_APPLICATION_NAME, MasterDatabase.ElementType.STRING);
            hashMap.put(ChinesePredictionDataSource.FIELD_RESULT_COUNTRY, MasterDatabase.ElementType.STRING);
            return hashMap;
        }
    };
    private static ChinesePredictionDatabaseSchema schemaInstance;
    private final Context context;
    private final PredictionDatabaseHandlerThread handler;
    private Logger.Log log = Logger.getLog(Logger.LoggerType.DEVELOPER, getClass().getSimpleName());
    private final MasterDatabase.DatabaseTable predictionTable;

    /* loaded from: classes.dex */
    private static class ChinesePredictionDatabaseSchema implements MasterDatabase.DatabaseSchema {
        private final Context context;

        private ChinesePredictionDatabaseSchema(Context context) {
            this.context = context.getApplicationContext();
        }

        public static synchronized ChinesePredictionDatabaseSchema from(Context context) {
            ChinesePredictionDatabaseSchema chinesePredictionDatabaseSchema;
            synchronized (ChinesePredictionDatabaseSchema.class) {
                if (ChinesePredictionDataSource.schemaInstance == null) {
                    ChinesePredictionDatabaseSchema unused = ChinesePredictionDataSource.schemaInstance = new ChinesePredictionDatabaseSchema(context);
                }
                chinesePredictionDatabaseSchema = ChinesePredictionDataSource.schemaInstance;
            }
            return chinesePredictionDatabaseSchema;
        }

        @Override // com.nuance.connect.sqlite.MasterDatabase.DatabaseSchema
        public Set<String> doNotEncrypt() {
            HashSet hashSet = new HashSet();
            hashSet.add(ChinesePredictionDataSource.ROWID);
            return hashSet;
        }

        @Override // com.nuance.connect.sqlite.MasterDatabase.DatabaseSchema
        public String getName() {
            return ChinesePredictionDataSource.DB_NAME;
        }

        @Override // com.nuance.connect.sqlite.MasterDatabase.DatabaseSchema
        public Set<MasterDatabase.TableSchema> getTableSchemas() {
            HashSet hashSet = new HashSet();
            hashSet.add(ChinesePredictionDataSource.predictionTableSchema);
            return hashSet;
        }

        @Override // com.nuance.connect.sqlite.MasterDatabase.DatabaseSchema
        public void onMigration() {
            this.context.deleteDatabase(ChinesePredictionDataSource.DB_NAME);
        }

        @Override // com.nuance.connect.sqlite.MasterDatabase.DatabaseSchema
        public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        }
    }

    /* loaded from: classes.dex */
    public static class ChinesePredictionResultDataReturn {
        public final int lastRowId;
        public final Prediction.LoggingRequestV1 loggingRequest;

        ChinesePredictionResultDataReturn(int i, Prediction.LoggingRequestV1 loggingRequestV1) {
            this.lastRowId = i;
            this.loggingRequest = loggingRequestV1;
        }
    }

    public ChinesePredictionDataSource(Context context) {
        this.context = context.getApplicationContext();
        MasterDatabase from = MasterDatabase.from(this.context);
        from.connectDatabase(ChinesePredictionDatabaseSchema.from(context));
        this.predictionTable = from.getTableDatabase(predictionTableSchema);
        this.handler = new PredictionDatabaseHandlerThread(this);
        this.handler.start();
    }

    private ArrayList<Integer> getIntegerArray(String str) {
        ArrayList<Integer> arrayList = new ArrayList<>();
        if (str != null) {
            for (int i = 0; i < str.length(); i++) {
                arrayList.add(i, Integer.valueOf(str.charAt(i)));
            }
        }
        return arrayList;
    }

    private ArrayList<Integer> parseStringToIntegerArray(String str) {
        ArrayList<Integer> arrayList = new ArrayList<>();
        if (str != null) {
            String[] split = str.split(",");
            for (int i = 0; i < split.length; i++) {
                try {
                    arrayList.add(i, Integer.valueOf(Integer.parseInt(split[i])));
                } catch (NumberFormatException e) {
                    this.log.d("NumberFormatException in parseStringToIntegerArray() string: ", str);
                }
            }
        }
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void deletePredictions(int i) {
        this.log.d("deletePredictions beforeRowId=", Integer.valueOf(i));
        try {
            StringBuilder sb = new StringBuilder();
            String[] strArr = {String.valueOf(i)};
            if (i > 0) {
                sb.append(ROWID);
                sb.append(" < ?");
            } else {
                sb.append(1);
                strArr = null;
            }
            this.log.d("deletePredictions rows=", Integer.valueOf(this.predictionTable.delete(sb.toString(), strArr)));
        } catch (SQLiteException e) {
            this.log.e("deletePredictions failed: ", e.getMessage());
        }
    }

    public void deletePredictionsFrom(int i) {
        this.handler.process(1, i, 0, 0);
    }

    public int getPendingLogSize() {
        Cursor cursor;
        SQLiteException e;
        int i;
        this.log.d("getPendingLogSize");
        try {
            cursor = this.predictionTable.query(false, null, null, null, null, null, null, null);
            if (cursor != null) {
                try {
                    i = cursor.getCount();
                } catch (Throwable th) {
                    th = th;
                    if (cursor != null) {
                        try {
                            cursor.close();
                        } catch (SQLiteException e2) {
                            e = e2;
                            i = 0;
                            this.log.e("getPendingLogSize failed: ", e.getMessage());
                            return i;
                        }
                    }
                    throw th;
                }
            } else {
                i = 0;
            }
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (SQLiteException e3) {
                    e = e3;
                    this.log.e("getPendingLogSize failed: ", e.getMessage());
                    return i;
                }
            }
            return i;
        } catch (Throwable th2) {
            th = th2;
            cursor = null;
        }
    }

    /* JADX WARN: Not initialized variable reg: 5, insn: 0x021d: MOVE (r14 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r5 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]), block:B:60:0x021d */
    /* JADX WARN: Removed duplicated region for block: B:36:0x01fb  */
    /* JADX WARN: Removed duplicated region for block: B:38:0x022c  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public com.nuance.connect.sqlite.ChinesePredictionDataSource.ChinesePredictionResultDataReturn getPredictions(java.lang.String r16) {
        /*
            Method dump skipped, instructions count: 575
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.nuance.connect.sqlite.ChinesePredictionDataSource.getPredictions(java.lang.String):com.nuance.connect.sqlite.ChinesePredictionDataSource$ChinesePredictionResultDataReturn");
    }

    public boolean insertPrediction(String str, String str2, int i, long j, long j2, String str3, String str4) {
        this.handler.addPrediction(new ChinesePredictionDataRow(str, str2, j, j2, i, str3, str4));
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean insertPredictionObject(ChinesePredictionDataRow chinesePredictionDataRow) {
        MasterDatabase.DatabaseTable databaseTable;
        boolean z = false;
        this.log.d("insertPredictionObject row=", chinesePredictionDataRow.toString());
        try {
            if (!this.predictionTable.isFull()) {
                try {
                    this.predictionTable.beginTransaction();
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(FIELD_RESULT_PREDICTION_ID, chinesePredictionDataRow.predictionId);
                    contentValues.put(FIELD_RESULT_CCPS_VERSION, chinesePredictionDataRow.ccpsVersion);
                    contentValues.put(FIELD_RESULT_TYPE, Integer.valueOf(chinesePredictionDataRow.resultType));
                    contentValues.put(FIELD_RESULT_CLOUD_TIME, Long.valueOf(chinesePredictionDataRow.cloudTime));
                    contentValues.put(FIELD_RESULT_TOTAL_TIME, Long.valueOf(chinesePredictionDataRow.totalTime));
                    contentValues.put(FIELD_RESULT_CREATED_STAMP, Long.valueOf(SystemClock.uptimeMillis()));
                    contentValues.put(FIELD_RESULT_APPLICATION_NAME, chinesePredictionDataRow.applicationName);
                    contentValues.put(FIELD_RESULT_COUNTRY, chinesePredictionDataRow.countryCode);
                    if (this.predictionTable.insert(null, contentValues) < 0) {
                        this.log.e("insertPredictionObject failed to insert event: ", chinesePredictionDataRow.toString());
                    } else {
                        this.predictionTable.setTransactionSuccessful();
                        z = true;
                    }
                    databaseTable = this.predictionTable;
                } catch (SQLException e) {
                    this.log.e("insertPredictionObject failed to insert prediction: (", chinesePredictionDataRow.toString(), ") SQL message", e.getMessage());
                    databaseTable = this.predictionTable;
                }
                databaseTable.endTransaction();
            }
            return z;
        } catch (Throwable th) {
            this.predictionTable.endTransaction();
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean insertPredictionResultObject(ChinesePredictionDataResultRow chinesePredictionDataResultRow) {
        MasterDatabase.DatabaseTable databaseTable;
        boolean z = false;
        this.log.d("insertPredictionResultObject row=", chinesePredictionDataResultRow.toString());
        try {
            if (!this.predictionTable.isFull()) {
                try {
                    this.predictionTable.beginTransaction();
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(FIELD_RESULT_TYPE, Integer.valueOf(chinesePredictionDataResultRow.resultType));
                    contentValues.put(FIELD_RESULT_PHRASE, chinesePredictionDataResultRow.phrase);
                    contentValues.put(FIELD_RESULT_SPELL, chinesePredictionDataResultRow.spell);
                    contentValues.put(FIELD_RESULT_FULLSPELL, chinesePredictionDataResultRow.fullSpell);
                    String str = "";
                    if (chinesePredictionDataResultRow.attribute != null && chinesePredictionDataResultRow.attribute.length > 0) {
                        str = StringUtils.implode(chinesePredictionDataResultRow.attribute, ",");
                    }
                    contentValues.put(FIELD_RESULT_ATTRIBUTE, str);
                    int update = this.predictionTable.update(contentValues, "a = ?", new String[]{chinesePredictionDataResultRow.predictionId});
                    if (update == 0) {
                        this.log.e("insertPredictionResultObject failed to create result for prediction: ", chinesePredictionDataResultRow.predictionId);
                    } else if (update > 1) {
                        this.log.e("insertPredictionResultObject failed to properly create result for prediction: ", chinesePredictionDataResultRow.predictionId);
                    } else {
                        this.predictionTable.setTransactionSuccessful();
                        z = true;
                    }
                    databaseTable = this.predictionTable;
                } catch (SQLException e) {
                    this.log.e("insertPredictionResultObject failed to create result for prediction: ", chinesePredictionDataResultRow.predictionId, " SQL message", e.getMessage());
                    databaseTable = this.predictionTable;
                }
                databaseTable.endTransaction();
            }
            return z;
        } catch (Throwable th) {
            this.predictionTable.endTransaction();
            throw th;
        }
    }

    public boolean isFull() {
        return this.predictionTable.isFull();
    }

    public boolean logPredictionResult(String str, int i, String str2, String str3, String str4, int[] iArr) {
        this.handler.addPrediction(new ChinesePredictionDataResultRow(str, i, str2, str3, str4, iArr));
        return true;
    }

    public void reset() {
        this.log.v("ChinesePredictionDataSource.reset()");
        this.predictionTable.deleteAll();
    }
}
