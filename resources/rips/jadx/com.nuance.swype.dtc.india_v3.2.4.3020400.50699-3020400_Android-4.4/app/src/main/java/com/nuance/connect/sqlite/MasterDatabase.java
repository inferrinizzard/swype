package com.nuance.connect.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.nuance.connect.internal.common.Document;
import com.nuance.connect.util.EncryptUtils;
import com.nuance.connect.util.Logger;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/* loaded from: classes.dex */
public class MasterDatabase {
    private static Codec defaultCodec;
    private static MasterDatabase instance;
    private final SQLDataSource dataSource;
    private final DatabaseOpenHelper helper;
    private static final Logger.Log devLog = Logger.getLog(Logger.LoggerType.DEVELOPER, MasterDatabase.class.getSimpleName());
    private static final Logger.Log oemLog = Logger.getLog(Logger.LoggerType.OEM, MasterDatabase.class.getSimpleName());
    private final Map<String, DatabaseTable> tables = new HashMap();
    private final Map<String, DatabaseSchema> databases = new HashMap();

    /* loaded from: classes.dex */
    public interface Codec {
        String decryptString(String str);

        String encryptString(String str);

        String getActualColumnName(String str);

        String getLogicalColumnName(String str);
    }

    /* loaded from: classes.dex */
    private static class DatabaseOpenHelper extends SQLiteOpenHelper {
        private static final String DB_NAME = "ac_connect";
        private static final int DB_VERSION = 4;
        private static DatabaseOpenHelper instance;
        private final Context context;

        private DatabaseOpenHelper(Context context) {
            super(context, DB_NAME, (SQLiteDatabase.CursorFactory) null, 4);
            this.context = context;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static synchronized DatabaseOpenHelper getInstance(Context context) {
            DatabaseOpenHelper databaseOpenHelper;
            synchronized (DatabaseOpenHelper.class) {
                if (instance == null) {
                    instance = new DatabaseOpenHelper(context.getApplicationContext());
                }
                databaseOpenHelper = instance;
            }
            return databaseOpenHelper;
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onCreate(SQLiteDatabase sQLiteDatabase) {
        }

        /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:24:0x007b -> B:15:0x0023). Please report as a decompilation issue!!! */
        /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:25:0x007d -> B:15:0x0023). Please report as a decompilation issue!!! */
        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
            Cursor cursor = null;
            try {
                if (i < 4) {
                    try {
                        cursor = sQLiteDatabase.rawQuery("SELECT * FROM sqlite_master WHERE type='table'", null);
                    } catch (Exception e) {
                        MasterDatabase.devLog.e("Error: " + e.getMessage());
                        if (cursor != null) {
                            cursor.close();
                        }
                    }
                    if (cursor == null || !cursor.moveToFirst()) {
                        MasterDatabase.devLog.d("No tables to upgrade");
                        if (cursor != null) {
                            cursor.close();
                        }
                    }
                    do {
                        String string = cursor.getString(cursor.getColumnIndex("name"));
                        MasterDatabase.devLog.d("Upgrading table: ", string);
                        sQLiteDatabase.execSQL("DROP TABLE IF EXISTS '" + string + "'");
                    } while (cursor.moveToNext());
                    if (cursor != null) {
                        cursor.close();
                    }
                }
            } catch (Throwable th) {
                if (cursor != null) {
                    cursor.close();
                }
                throw th;
            }
        }
    }

    /* loaded from: classes.dex */
    public interface DatabaseSchema {
        Set<String> doNotEncrypt();

        String getName();

        Set<TableSchema> getTableSchemas();

        void onMigration();

        void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2);
    }

    /* loaded from: classes.dex */
    public interface DatabaseTable {
        void beginTransaction();

        int delete(String str, String[] strArr);

        boolean deleteAll();

        void endTransaction();

        Codec getCodec();

        String getColumnName(String str);

        String getName();

        long insert(String str, ContentValues contentValues);

        long insertWithOnConflict(String str, ContentValues contentValues, int i);

        boolean isFull();

        Cursor query(boolean z, String[] strArr, String str, String[] strArr2, String str2, String str3, String str4, String str5);

        void setTransactionSuccessful();

        int update(ContentValues contentValues, String str, String[] strArr);
    }

    /* loaded from: classes.dex */
    private class DatabaseTableImpl implements Codec, DatabaseTable {
        private final Set<String> doNotEncrypt;
        private final String tableName;

        private DatabaseTableImpl(String str, Set<String> set) {
            this.tableName = str;
            this.doNotEncrypt = set == null ? new HashSet() : new HashSet(set);
        }

        private ContentValues encryptContentValues(ContentValues contentValues) {
            ContentValues contentValues2 = new ContentValues();
            for (Map.Entry<String, Object> entry : contentValues.valueSet()) {
                Object value = entry.getValue();
                String actualColumnName = getActualColumnName(entry.getKey());
                if (value instanceof String) {
                    contentValues2.put(actualColumnName, encryptString((String) value));
                } else if (value instanceof Byte) {
                    contentValues2.put(actualColumnName, (Byte) value);
                } else if (value instanceof Short) {
                    contentValues2.put(actualColumnName, (Short) value);
                } else if (value instanceof Integer) {
                    contentValues2.put(actualColumnName, (Integer) value);
                } else if (value instanceof Long) {
                    contentValues2.put(actualColumnName, (Long) value);
                } else if (value instanceof Float) {
                    contentValues2.put(actualColumnName, (Float) value);
                } else if (value instanceof Double) {
                    contentValues2.put(actualColumnName, (Double) value);
                } else if (value instanceof Boolean) {
                    contentValues2.put(actualColumnName, (Boolean) value);
                } else if (value instanceof byte[]) {
                    contentValues2.put(actualColumnName, (byte[]) value);
                } else if (value == null) {
                    contentValues2.putNull(actualColumnName);
                } else {
                    MasterDatabase.devLog.e("Unknown type for object: ", value);
                }
            }
            return contentValues2;
        }

        private String[] encryptStringArray(String[] strArr) {
            if (strArr == null) {
                return strArr;
            }
            String[] strArr2 = new String[strArr.length];
            for (int i = 0; i < strArr.length; i++) {
                strArr2[i] = encryptString(strArr[i]);
            }
            return strArr2;
        }

        private String[] getActualColumnNamesArray(String[] strArr) {
            if (strArr == null) {
                return strArr;
            }
            String[] strArr2 = new String[strArr.length];
            for (int i = 0; i < strArr.length; i++) {
                strArr2[i] = getActualColumnName(strArr[i]);
            }
            return strArr2;
        }

        @Override // com.nuance.connect.sqlite.MasterDatabase.DatabaseTable
        public void beginTransaction() {
            MasterDatabase.this.dataSource.beginTransaction();
        }

        @Override // com.nuance.connect.sqlite.MasterDatabase.Codec
        public String decryptString(String str) {
            return this.doNotEncrypt.contains(str) ? str : MasterDatabase.defaultCodec.decryptString(str);
        }

        @Override // com.nuance.connect.sqlite.MasterDatabase.DatabaseTable
        public int delete(String str, String[] strArr) {
            int i;
            Throwable th;
            int i2 = 0;
            try {
                try {
                    i = MasterDatabase.this.dataSource.incrementOpenRefCount();
                } catch (IllegalStateException e) {
                    i = 0;
                } catch (Throwable th2) {
                    i = 0;
                    th = th2;
                    MasterDatabase.this.dataSource.decrementOpenRefCount(i);
                    throw th;
                }
                try {
                    i2 = MasterDatabase.this.dataSource.getDatabase().delete(getName(), str, encryptStringArray(strArr));
                    MasterDatabase.this.dataSource.decrementOpenRefCount(i);
                } catch (IllegalStateException e2) {
                    MasterDatabase.devLog.e("delete could not open database: ", getName());
                    MasterDatabase.this.dataSource.decrementOpenRefCount(i);
                    return i2;
                }
                return i2;
            } catch (Throwable th3) {
                th = th3;
                MasterDatabase.this.dataSource.decrementOpenRefCount(i);
                throw th;
            }
        }

        @Override // com.nuance.connect.sqlite.MasterDatabase.DatabaseTable
        public boolean deleteAll() {
            return delete("1", null) >= 0;
        }

        @Override // com.nuance.connect.sqlite.MasterDatabase.Codec
        public String encryptString(String str) {
            return this.doNotEncrypt.contains(str) ? str : MasterDatabase.defaultCodec.encryptString(str);
        }

        @Override // com.nuance.connect.sqlite.MasterDatabase.DatabaseTable
        public void endTransaction() {
            MasterDatabase.this.dataSource.endTransaction();
        }

        @Override // com.nuance.connect.sqlite.MasterDatabase.Codec
        public String getActualColumnName(String str) {
            return this.doNotEncrypt.contains(str) ? str : MasterDatabase.this.getActualColumnName(str);
        }

        @Override // com.nuance.connect.sqlite.MasterDatabase.DatabaseTable
        public Codec getCodec() {
            return this;
        }

        @Override // com.nuance.connect.sqlite.MasterDatabase.DatabaseTable
        public String getColumnName(String str) {
            return getActualColumnName(str);
        }

        @Override // com.nuance.connect.sqlite.MasterDatabase.Codec
        public String getLogicalColumnName(String str) {
            return this.doNotEncrypt.contains(str) ? str : MasterDatabase.this.getLogicalColumnName(str);
        }

        @Override // com.nuance.connect.sqlite.MasterDatabase.DatabaseTable
        public String getName() {
            return this.tableName;
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r0v0 */
        /* JADX WARN: Type inference failed for: r0v1 */
        /* JADX WARN: Type inference failed for: r0v16 */
        /* JADX WARN: Type inference failed for: r0v5 */
        /* JADX WARN: Type inference failed for: r0v6 */
        @Override // com.nuance.connect.sqlite.MasterDatabase.DatabaseTable
        public long insert(String str, ContentValues contentValues) {
            int i;
            Throwable th;
            ?? r0 = 0;
            int i2 = 0;
            try {
                try {
                    i = MasterDatabase.this.dataSource.incrementOpenRefCount();
                    try {
                        long insert = MasterDatabase.this.dataSource.getDatabase().insert(getName(), str, encryptContentValues(contentValues));
                        MasterDatabase.this.dataSource.decrementOpenRefCount(i);
                        r0 = insert;
                    } catch (IllegalStateException e) {
                        i2 = i;
                        MasterDatabase.devLog.e("insert could not open database: ", getName());
                        MasterDatabase.this.dataSource.decrementOpenRefCount(i2);
                        r0 = -1;
                        return r0 == true ? 1L : 0L;
                    } catch (Throwable th2) {
                        th = th2;
                        MasterDatabase.this.dataSource.decrementOpenRefCount(i);
                        throw th;
                    }
                } catch (IllegalStateException e2) {
                }
                return r0 == true ? 1L : 0L;
            } catch (Throwable th3) {
                i = r0;
                th = th3;
            }
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r0v0 */
        /* JADX WARN: Type inference failed for: r0v1 */
        /* JADX WARN: Type inference failed for: r0v16 */
        /* JADX WARN: Type inference failed for: r0v5 */
        /* JADX WARN: Type inference failed for: r0v6 */
        @Override // com.nuance.connect.sqlite.MasterDatabase.DatabaseTable
        public long insertWithOnConflict(String str, ContentValues contentValues, int i) {
            int i2;
            Throwable th;
            ?? r0 = 0;
            int i3 = 0;
            try {
                try {
                    i2 = MasterDatabase.this.dataSource.incrementOpenRefCount();
                    try {
                        long insertWithOnConflict = MasterDatabase.this.dataSource.getDatabase().insertWithOnConflict(getName(), str, encryptContentValues(contentValues), i);
                        MasterDatabase.this.dataSource.decrementOpenRefCount(i2);
                        r0 = insertWithOnConflict;
                    } catch (IllegalStateException e) {
                        i3 = i2;
                        MasterDatabase.devLog.e("insertWithOnConflict could not open database: ", getName());
                        MasterDatabase.this.dataSource.decrementOpenRefCount(i3);
                        r0 = -1;
                        return r0 == true ? 1L : 0L;
                    } catch (Throwable th2) {
                        th = th2;
                        MasterDatabase.this.dataSource.decrementOpenRefCount(i2);
                        throw th;
                    }
                } catch (IllegalStateException e2) {
                }
                return r0 == true ? 1L : 0L;
            } catch (Throwable th3) {
                i2 = r0;
                th = th3;
            }
        }

        @Override // com.nuance.connect.sqlite.MasterDatabase.DatabaseTable
        public boolean isFull() {
            return false;
        }

        @Override // com.nuance.connect.sqlite.MasterDatabase.DatabaseTable
        public Cursor query(boolean z, String[] strArr, String str, String[] strArr2, String str2, String str3, String str4, String str5) {
            try {
                return new DecryptionCursor(MasterDatabase.this.dataSource, MasterDatabase.this.dataSource.getDatabase().query(z, getName(), getActualColumnNamesArray(strArr), str, encryptStringArray(strArr2), str2, str3, str4, str5), this);
            } catch (IllegalStateException e) {
                MasterDatabase.devLog.e("query could not open database: ", getName());
                return null;
            }
        }

        @Override // com.nuance.connect.sqlite.MasterDatabase.DatabaseTable
        public void setTransactionSuccessful() {
            MasterDatabase.this.dataSource.setTransactionSuccessful();
        }

        @Override // com.nuance.connect.sqlite.MasterDatabase.DatabaseTable
        public int update(ContentValues contentValues, String str, String[] strArr) {
            int i;
            Throwable th;
            int i2 = 0;
            try {
                try {
                    i = MasterDatabase.this.dataSource.incrementOpenRefCount();
                } catch (IllegalStateException e) {
                    i = 0;
                } catch (Throwable th2) {
                    i = 0;
                    th = th2;
                    MasterDatabase.this.dataSource.decrementOpenRefCount(i);
                    throw th;
                }
                try {
                    i2 = MasterDatabase.this.dataSource.getDatabase().update(getName(), encryptContentValues(contentValues), str, encryptStringArray(strArr));
                    MasterDatabase.this.dataSource.decrementOpenRefCount(i);
                } catch (IllegalStateException e2) {
                    MasterDatabase.devLog.e("update could not open database: ", getName());
                    MasterDatabase.this.dataSource.decrementOpenRefCount(i);
                    return i2;
                }
                return i2;
            } catch (Throwable th3) {
                th = th3;
                MasterDatabase.this.dataSource.decrementOpenRefCount(i);
                throw th;
            }
        }
    }

    /* loaded from: classes.dex */
    private static class DecryptionCursor extends RefCountingCursor {
        final Codec codec;

        public DecryptionCursor(SQLDataSource sQLDataSource, Cursor cursor, Codec codec) {
            super(sQLDataSource, cursor);
            this.codec = codec;
        }

        @Override // com.nuance.connect.sqlite.MasterDatabase.RefCountingCursor, android.database.CursorWrapper, android.database.Cursor, java.io.Closeable, java.lang.AutoCloseable
        public void close() {
            super.close();
        }

        @Override // android.database.CursorWrapper, android.database.Cursor
        public int getColumnIndex(String str) {
            return super.getColumnIndex(this.codec.getActualColumnName(str));
        }

        @Override // android.database.CursorWrapper, android.database.Cursor
        public int getColumnIndexOrThrow(String str) throws IllegalArgumentException {
            return super.getColumnIndexOrThrow(this.codec.getActualColumnName(str));
        }

        @Override // android.database.CursorWrapper, android.database.Cursor
        public String getColumnName(int i) {
            return this.codec.getLogicalColumnName(super.getColumnName(i));
        }

        @Override // android.database.CursorWrapper, android.database.Cursor
        public String[] getColumnNames() {
            String[] columnNames = super.getColumnNames();
            if (columnNames == null) {
                return null;
            }
            String[] strArr = new String[columnNames.length];
            for (int i = 0; i < columnNames.length; i++) {
                strArr[i] = this.codec.getLogicalColumnName(columnNames[i]);
            }
            return strArr;
        }

        @Override // android.database.CursorWrapper, android.database.Cursor
        public String getString(int i) {
            return super.getType(i) == 3 ? this.codec.decryptString(super.getString(i)) : super.getString(i);
        }
    }

    /* loaded from: classes.dex */
    public enum ElementType {
        BLOB,
        INTEGER,
        STRING,
        TEXT,
        DATETIME
    }

    /* loaded from: classes.dex */
    private static class RefCountingCursor extends CursorWrapper {
        protected final WeakReference<SQLDataSource> dataSourceRef;
        protected final int token;

        public RefCountingCursor(SQLDataSource sQLDataSource, Cursor cursor) {
            super(cursor);
            this.dataSourceRef = new WeakReference<>(sQLDataSource);
            this.token = sQLDataSource.incrementOpenRefCount();
        }

        @Override // android.database.CursorWrapper, android.database.Cursor, java.io.Closeable, java.lang.AutoCloseable
        public void close() {
            super.close();
            SQLDataSource sQLDataSource = this.dataSourceRef.get();
            if (sQLDataSource != null) {
                sQLDataSource.decrementOpenRefCount(this.token);
            }
        }
    }

    /* loaded from: classes.dex */
    public interface TableSchema {
        String getPrimaryKey();

        String getTableName();

        Map<String, ElementType> getTableProperties();
    }

    private MasterDatabase(Context context) {
        Context applicationContext = context.getApplicationContext();
        this.helper = DatabaseOpenHelper.getInstance(applicationContext);
        this.dataSource = new SQLDataSource(applicationContext) { // from class: com.nuance.connect.sqlite.MasterDatabase.1
            @Override // com.nuance.connect.sqlite.SQLDataSource
            public SQLiteDatabase open() throws SQLException {
                return MasterDatabase.this.helper.getWritableDatabase();
            }
        };
    }

    private void deleteAllTables() {
        Logger.configure(true, 1, Logger.OutputMode.ANDROID_LOG, null);
        devLog.d("deleteAllTables()");
        ArrayList<String> arrayList = new ArrayList();
        Cursor rawQuery = this.dataSource.getDatabase().rawQuery("SELECT * FROM sqlite_master WHERE type='table';", null);
        try {
            rawQuery.moveToFirst();
            while (!rawQuery.isAfterLast()) {
                String string = rawQuery.getString(1);
                if (!string.equals("android_metadata") && !string.equals("sqlite_sequence")) {
                    arrayList.add(string);
                }
                rawQuery.moveToNext();
            }
            rawQuery.close();
            for (String str : arrayList) {
                devLog.d("delete from table: ", str);
                try {
                    this.dataSource.getDatabase().execSQL("DELETE from " + str);
                } catch (SQLException e) {
                    devLog.d("unable to clear: " + str);
                    e.printStackTrace();
                }
            }
        } catch (Throwable th) {
            rawQuery.close();
            throw th;
        }
    }

    public static synchronized MasterDatabase from(final Context context) {
        MasterDatabase masterDatabase;
        synchronized (MasterDatabase.class) {
            if (instance == null) {
                defaultCodec = new Codec() { // from class: com.nuance.connect.sqlite.MasterDatabase.2
                    @Override // com.nuance.connect.sqlite.MasterDatabase.Codec
                    public final String decryptString(String str) {
                        return EncryptUtils.databaseDecryptString(context, str);
                    }

                    @Override // com.nuance.connect.sqlite.MasterDatabase.Codec
                    public final String encryptString(String str) {
                        return EncryptUtils.databaseEncryptString(context, str);
                    }

                    @Override // com.nuance.connect.sqlite.MasterDatabase.Codec
                    public final String getActualColumnName(String str) {
                        return MasterDatabase.instance.getActualColumnName(str);
                    }

                    @Override // com.nuance.connect.sqlite.MasterDatabase.Codec
                    public final String getLogicalColumnName(String str) {
                        return MasterDatabase.instance.getLogicalColumnName(str);
                    }
                };
                instance = new MasterDatabase(context.getApplicationContext());
            }
            masterDatabase = instance;
        }
        return masterDatabase;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String getLogicalColumnName(String str) {
        if (str != null && str.startsWith("A")) {
            return str.substring(1);
        }
        devLog.w("invalid column name: " + str);
        return "";
    }

    private String obfuscateTableName(DatabaseSchema databaseSchema, TableSchema tableSchema) {
        return databaseSchema.getName() + Document.ID_SEPARATOR + tableSchema.getTableName();
    }

    /* JADX WARN: Removed duplicated region for block: B:36:0x0192 A[DONT_GENERATE] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void connectDatabase(com.nuance.connect.sqlite.MasterDatabase.DatabaseSchema r15) {
        /*
            Method dump skipped, instructions count: 597
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.nuance.connect.sqlite.MasterDatabase.connectDatabase(com.nuance.connect.sqlite.MasterDatabase$DatabaseSchema):void");
    }

    public String getActualColumnName(String str) {
        return "A" + str;
    }

    public String getDatabaseString(String str) {
        return defaultCodec.encryptString(str);
    }

    public DatabaseTable getTableDatabase(TableSchema tableSchema) {
        if (tableSchema != null) {
            return this.tables.get(obfuscateTableName(this.databases.get(tableSchema.getTableName()), tableSchema));
        }
        return null;
    }

    public DatabaseTable getTableDatabase(String str) {
        return this.tables.get(str);
    }

    public Cursor rawQuery(String str, String[] strArr, Codec codec) {
        if (codec == null) {
            codec = defaultCodec;
        }
        try {
            return new DecryptionCursor(this.dataSource, this.dataSource.getDatabase().rawQuery(str, strArr), codec);
        } catch (IllegalStateException e) {
            devLog.e("rawQuery could not open database");
            return null;
        }
    }
}
