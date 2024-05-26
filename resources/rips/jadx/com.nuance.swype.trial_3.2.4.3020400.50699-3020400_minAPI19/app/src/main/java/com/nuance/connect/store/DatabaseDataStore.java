package com.nuance.connect.store;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Looper;
import android.os.Message;
import com.nuance.connect.sqlite.MasterDatabase;
import com.nuance.connect.util.Data;
import com.nuance.connect.util.HandlerThread;
import com.nuance.connect.util.Logger;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/* loaded from: classes.dex */
public class DatabaseDataStore implements PersistentDataStore {
    private static final String DB_NAME = "DATA_STORE";
    private static final String ENTRY_TABLE = "ENTRY_TABLE";
    private static final String ENTRY_TABLE_KEY = "ENTRY_TABLE_KEY";
    private static final String ENTRY_TABLE_VALUE = "ENTRY_TABLE_VALUE";
    private final Context context;
    private final MasterDatabase.DatabaseTable entryTable;
    private final HandlerThread handlerThread;
    private static final Logger.Log log = Logger.getLog(Logger.LoggerType.DEVELOPER, DatabaseDataStore.class.getSimpleName());
    private static final MasterDatabase.TableSchema entriesTableSchema = new MasterDatabase.TableSchema() { // from class: com.nuance.connect.store.DatabaseDataStore.1
        @Override // com.nuance.connect.sqlite.MasterDatabase.TableSchema
        public final String getPrimaryKey() {
            return DatabaseDataStore.ENTRY_TABLE_KEY;
        }

        @Override // com.nuance.connect.sqlite.MasterDatabase.TableSchema
        public final String getTableName() {
            return DatabaseDataStore.ENTRY_TABLE;
        }

        @Override // com.nuance.connect.sqlite.MasterDatabase.TableSchema
        public final Map<String, MasterDatabase.ElementType> getTableProperties() {
            HashMap hashMap = new HashMap();
            hashMap.put(DatabaseDataStore.ENTRY_TABLE_KEY, MasterDatabase.ElementType.STRING);
            hashMap.put(DatabaseDataStore.ENTRY_TABLE_VALUE, MasterDatabase.ElementType.STRING);
            return hashMap;
        }
    };

    /* loaded from: classes.dex */
    private static class StoreDatabaseSchema implements MasterDatabase.DatabaseSchema {
        private final WeakReference<DatabaseDataStore> parentRef;

        StoreDatabaseSchema(DatabaseDataStore databaseDataStore) {
            this.parentRef = new WeakReference<>(databaseDataStore);
        }

        @Override // com.nuance.connect.sqlite.MasterDatabase.DatabaseSchema
        public Set<String> doNotEncrypt() {
            return null;
        }

        @Override // com.nuance.connect.sqlite.MasterDatabase.DatabaseSchema
        public String getName() {
            return DatabaseDataStore.DB_NAME;
        }

        @Override // com.nuance.connect.sqlite.MasterDatabase.DatabaseSchema
        public Set<MasterDatabase.TableSchema> getTableSchemas() {
            HashSet hashSet = new HashSet();
            if (this.parentRef.get() != null) {
                hashSet.add(DatabaseDataStore.entriesTableSchema);
            }
            return hashSet;
        }

        @Override // com.nuance.connect.sqlite.MasterDatabase.DatabaseSchema
        public void onMigration() {
        }

        @Override // com.nuance.connect.sqlite.MasterDatabase.DatabaseSchema
        public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        }
    }

    public DatabaseDataStore(Context context) {
        this.context = context;
        MasterDatabase from = MasterDatabase.from(context);
        from.connectDatabase(new StoreDatabaseSchema(this));
        this.entryTable = from.getTableDatabase(entriesTableSchema);
        this.handlerThread = new HandlerThread() { // from class: com.nuance.connect.store.DatabaseDataStore.2
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.nuance.connect.util.HandlerThread
            public Message handleMessage(Message message) {
                return null;
            }
        };
        this.handlerThread.start();
    }

    private String read(String str) {
        Cursor cursor;
        Exception e;
        SQLException e2;
        String str2 = null;
        String[] strArr = {str};
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(this.entryTable.getColumnName(ENTRY_TABLE_KEY)).append(" = ?");
            Cursor query = this.entryTable.query(false, new String[]{ENTRY_TABLE_VALUE}, sb.toString(), strArr, null, null, null, null);
            try {
                query.moveToFirst();
                String str3 = (query.getCount() <= 0 || query.getColumnCount() <= 0) ? null : query.getString(0);
                try {
                    if (query.getCount() > 1) {
                        log.e("read() Unexpected error in DatabaseDataStore; count=" + query.getCount(), "; key=", str);
                    }
                    if (query != null) {
                        try {
                            query.close();
                        } catch (SQLException e3) {
                            e2 = e3;
                            log.e("read() Error in [SQLiteDataStore] key(", str, "): " + e2.getMessage());
                            return str3;
                        } catch (Exception e4) {
                            e = e4;
                            log.e("read() Error in [SQLiteDataStore] key(", str, "): " + e.getMessage());
                            return str3;
                        }
                    }
                    return str3;
                } catch (Throwable th) {
                    str2 = str3;
                    th = th;
                    cursor = query;
                    if (cursor != null) {
                        try {
                            cursor.close();
                        } catch (SQLException e5) {
                            e2 = e5;
                            str3 = str2;
                            log.e("read() Error in [SQLiteDataStore] key(", str, "): " + e2.getMessage());
                            return str3;
                        } catch (Exception e6) {
                            e = e6;
                            str3 = str2;
                            log.e("read() Error in [SQLiteDataStore] key(", str, "): " + e.getMessage());
                            return str3;
                        }
                    }
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
                cursor = query;
            }
        } catch (Throwable th3) {
            th = th3;
            cursor = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean save(String str, String str2) {
        boolean z;
        try {
            try {
                this.entryTable.beginTransaction();
                ContentValues contentValues = new ContentValues();
                contentValues.put(ENTRY_TABLE_KEY, str);
                contentValues.put(ENTRY_TABLE_VALUE, str2);
                if (this.entryTable.insertWithOnConflict(null, contentValues, 5) < 0) {
                    log.e("save failed to insert key: ", str, "; value: ", str2);
                    z = false;
                } else {
                    this.entryTable.setTransactionSuccessful();
                    z = true;
                }
                return z;
            } finally {
                this.entryTable.endTransaction();
            }
        } catch (SQLException e) {
            log.e("save failed to insert key: (", str, ") value (", str2, ") SQL message" + e.getMessage());
            return false;
        }
    }

    @Override // com.nuance.connect.store.PersistentDataStore
    public boolean clear() {
        return this.entryTable.deleteAll();
    }

    @Override // com.nuance.connect.store.PersistentDataStore
    public boolean delete(String str) {
        boolean z = true;
        try {
            try {
                this.entryTable.beginTransaction();
                StringBuilder sb = new StringBuilder();
                sb.append(this.entryTable.getColumnName(ENTRY_TABLE_KEY)).append(" = ?");
                this.entryTable.delete(sb.toString(), new String[]{str});
                this.entryTable.setTransactionSuccessful();
            } catch (SQLiteException e) {
                log.e("delete(", ENTRY_TABLE_KEY, ") failed message: ", e.getMessage());
                this.entryTable.endTransaction();
                z = false;
            } catch (SQLException e2) {
                log.e("delete(", ENTRY_TABLE_KEY, ") failed message: ", e2.getMessage());
                this.entryTable.endTransaction();
                z = false;
            }
            return z;
        } finally {
            this.entryTable.endTransaction();
        }
    }

    @Override // com.nuance.connect.store.PersistentDataStore
    public boolean exists(String str) {
        Cursor cursor;
        Exception e;
        boolean z;
        SQLException e2;
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(this.entryTable.getColumnName(ENTRY_TABLE_KEY)).append(" = ?");
            cursor = this.entryTable.query(false, null, sb.toString(), new String[]{str}, null, null, null, null);
            try {
                cursor.moveToFirst();
                z = cursor.getCount() > 0;
                if (cursor != null) {
                    try {
                        cursor.close();
                    } catch (SQLException e3) {
                        e2 = e3;
                        log.e("exists() Error in [SQLiteDataStore] key(", str, "): " + e2.getMessage());
                        return z;
                    } catch (Exception e4) {
                        e = e4;
                        log.e("exists() Error in [SQLiteDataStore] key(", str, "): " + e.getMessage());
                        return z;
                    }
                }
                return z;
            } catch (Throwable th) {
                th = th;
                if (cursor != null) {
                    try {
                        cursor.close();
                    } catch (SQLException e5) {
                        e2 = e5;
                        z = false;
                        log.e("exists() Error in [SQLiteDataStore] key(", str, "): " + e2.getMessage());
                        return z;
                    } catch (Exception e6) {
                        e = e6;
                        z = false;
                        log.e("exists() Error in [SQLiteDataStore] key(", str, "): " + e.getMessage());
                        return z;
                    }
                }
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
            cursor = null;
        }
    }

    public boolean isEmpty() {
        Cursor query = this.entryTable.query(false, null, null, null, null, null, null, null);
        return query == null || !query.moveToFirst();
    }

    @Override // com.nuance.connect.store.PersistentDataStore
    public Boolean readBoolean(String str, Boolean bool) {
        String readString = readString(str, null);
        return readString == null ? bool : Boolean.valueOf(readString);
    }

    @Override // com.nuance.connect.store.PersistentDataStore
    public boolean readBoolean(String str, boolean z) {
        String readString = readString(str, null);
        return readString == null ? z : Boolean.valueOf(readString).booleanValue();
    }

    @Override // com.nuance.connect.store.PersistentDataStore
    public int readInt(String str, int i) {
        try {
            return Integer.parseInt(readString(str, null));
        } catch (Exception e) {
            return i;
        }
    }

    @Override // com.nuance.connect.store.PersistentDataStore
    public long readLong(String str, long j) {
        try {
            return Long.parseLong(readString(str, null));
        } catch (Exception e) {
            return j;
        }
    }

    @Override // com.nuance.connect.store.PersistentDataStore
    public Object readObject(String str) {
        return Data.unserializeObject(readString(str, null));
    }

    @Override // com.nuance.connect.store.PersistentDataStore
    public String readString(String str, String str2) {
        String read = read(str);
        return (read == null || "".equals(read)) ? str2 : read;
    }

    public void reset() {
        log.v("DatabaseDataStore.reset()");
        this.entryTable.deleteAll();
    }

    @Override // com.nuance.connect.store.PersistentDataStore
    public boolean saveBoolean(String str, boolean z) {
        return saveString(str, Boolean.toString(z));
    }

    @Override // com.nuance.connect.store.PersistentDataStore
    public boolean saveInt(String str, int i) {
        return saveString(str, Integer.toString(i));
    }

    @Override // com.nuance.connect.store.PersistentDataStore
    public boolean saveLong(String str, long j) {
        return saveString(str, Long.toString(j));
    }

    @Override // com.nuance.connect.store.PersistentDataStore
    public boolean saveObject(String str, Object obj) {
        if (obj == null || (obj instanceof Serializable)) {
            return saveString(str, Data.serializeObject(obj));
        }
        throw new IllegalArgumentException("Attempting to save invalid object. The object you are saving does not implement Serializable");
    }

    @Override // com.nuance.connect.store.PersistentDataStore
    public boolean saveString(final String str, final String str2) {
        if (str2 == null) {
            return delete(str);
        }
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                this.handlerThread.process(new Runnable() { // from class: com.nuance.connect.store.DatabaseDataStore.3
                    @Override // java.lang.Runnable
                    public void run() {
                        DatabaseDataStore.this.save(str, str2);
                    }
                });
            } else {
                save(str, str2);
            }
            return true;
        } catch (Exception e) {
            log.e("saveString() Database [SQLiteDataStore] Exception; key=", str, " exception=", e.getMessage());
            return false;
        }
    }
}
