package com.localytics.android;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import com.localytics.android.Localytics;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
abstract class BaseProvider {
    static final String DATABASE_FILE = "com.localytics.android.%s.%s.sqlite";
    private static final String OLD_DATABASE_FILE = "com.localytics.android.%s.sqlite";
    private static final Map<String, String> sCountProjectionMap = Collections.unmodifiableMap(getCountProjectionMap());
    boolean mDatabaseJustMoved;
    SQLiteDatabase mDb;
    String mDbPath;
    LocalyticsDao mLocalyticsDao;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public interface InClauseBuilder<T> {
        Object getValue(T t);
    }

    abstract boolean canAddToDB();

    abstract long maxSiloDbSize();

    /* JADX INFO: Access modifiers changed from: package-private */
    public BaseProvider(LocalyticsDao localyticsDao) {
        this.mLocalyticsDao = localyticsDao;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public BaseProvider(LocalyticsDao localyticsDao, String dbName) {
        this.mLocalyticsDao = localyticsDao;
        boolean useNewLocation = moveDatabaseIfNecessary(this.mLocalyticsDao.getAndroidVersionInt(), String.format(DATABASE_FILE, DatapointHelper.getSha256_buggy(this.mLocalyticsDao.getAppKey()), dbName));
        this.mDbPath = makeDatabasePath(dbName, useNewLocation);
        Localytics.Log.v(String.format("Database path for %s is %s", dbName, this.mDbPath));
    }

    boolean moveDatabaseIfNecessary(int sdkInt, String dbName) {
        if (sdkInt < 21) {
            return false;
        }
        File newDatabaseDir = newDatabaseDir(this.mLocalyticsDao.getAppContext());
        File dbInNewLocation = new File(newDatabaseDir, dbName);
        File dbInOldLocation = this.mLocalyticsDao.getAppContext().getDatabasePath(dbName);
        if (dbInOldLocation.exists()) {
            try {
                Utils.deleteFile(dbInNewLocation);
                Utils.copyFile(dbInOldLocation, dbInNewLocation);
                Utils.deleteFile(dbInOldLocation);
                Utils.deleteFile(new File(dbInOldLocation.getAbsolutePath() + "-journal"));
                this.mDatabaseJustMoved = true;
                Localytics.Log.v("Moved database from " + dbInOldLocation + " to " + dbInNewLocation);
                return true;
            } catch (Exception e) {
                Localytics.Log.e("Exception while copying database to new location", e);
                return false;
            }
        }
        Localytics.Log.v("No need to move database.");
        return true;
    }

    String makeDatabasePath(String dbName, boolean useNewLocation) {
        String databaseName = String.format(DATABASE_FILE, DatapointHelper.getSha256_buggy(this.mLocalyticsDao.getAppKey()), dbName);
        if (!useNewLocation) {
            return this.mLocalyticsDao.getAppContext().getDatabasePath(databaseName).getAbsolutePath();
        }
        File newDatabaseDir = newDatabaseDir(this.mLocalyticsDao.getAppContext());
        return new File(newDatabaseDir, databaseName).getAbsolutePath();
    }

    private static HashMap<String, String> getCountProjectionMap() {
        HashMap<String, String> temp = new HashMap<>();
        temp.put("_count", "COUNT(*)");
        return temp;
    }

    @TargetApi(21)
    private static File newDatabaseDir(Context appContext) {
        File dir = new File(appContext.getNoBackupFilesDir(), ".localytics");
        dir.mkdir();
        return dir;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void deleteOldFiles(Context appContext) {
        deleteDirectory(new File(appContext.getFilesDir(), "localytics"));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean deleteDirectory(File directory) {
        if (directory.exists() && directory.isDirectory()) {
            String[] arr$ = directory.list();
            for (String child : arr$) {
                if (!deleteDirectory(new File(directory, child))) {
                    return false;
                }
            }
        }
        return directory.delete();
    }

    private int getNumberOfRows(String tableName) {
        return query(tableName, null, null, null, null).getCount();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Cursor query(String tableName, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Localytics.Log.v(String.format("Query table: %s, projection: %s, selection: %s, selectionArgs: %s", tableName, Arrays.toString(projection), selection, Arrays.toString(selectionArgs)));
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(tableName);
        if (projection != null && 1 == projection.length && "_count".equals(projection[0])) {
            qb.setProjectionMap(sCountProjectionMap);
        }
        Cursor result = qb.query(this.mDb, projection, selection, selectionArgs, null, null, sortOrder);
        Localytics.Log.v("Query result is: " + DatabaseUtils.dumpCursorToString(result));
        return result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public long insert(String tableName, ContentValues values) {
        Localytics.Log.v(String.format("Insert table: %s, values: %s", tableName, values.toString()));
        if (!canAddToDB()) {
            Localytics.Log.v("Database is full; data not inserted");
            return -1L;
        }
        long result = this.mDb.insertOrThrow(tableName, null, values);
        Localytics.Log.v(String.format("Inserted row with new id %d", Long.valueOf(result)));
        return result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public long replace(String tableName, ContentValues values) {
        Localytics.Log.v(String.format("Replace table: %s, values: %s", tableName, values.toString()));
        if (!canAddToDB()) {
            Localytics.Log.v(String.format("Database is full; data not replaced", new Object[0]));
            return -1L;
        }
        long result = this.mDb.replace(tableName, null, values);
        Localytics.Log.v(String.format("Replaced row with id %d", Long.valueOf(result)));
        return result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void vacuumIfNecessary() {
        if (new File(this.mDb.getPath()).length() >= maxSiloDbSize() * 0.8d) {
            runBatchTransaction(new Runnable() { // from class: com.localytics.android.BaseProvider.1
                @Override // java.lang.Runnable
                public void run() {
                    Cursor cursor = null;
                    try {
                        try {
                            cursor = BaseProvider.this.mDb.rawQuery("PRAGMA incremental_vacuum(0);", null);
                            do {
                            } while (cursor.moveToNext());
                            if (cursor != null) {
                                cursor.close();
                            }
                        } catch (Exception e) {
                            Localytics.Log.w("Auto-vacuum error", e);
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
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int remove(String tableName, String selection, String[] selectionArgs) {
        int count;
        Localytics.Log.v(String.format("Delete table: %s, selection: %s, selectionArgs: %s", tableName, selection, Arrays.toString(selectionArgs)));
        if (selection == null) {
            count = this.mDb.delete(tableName, "1", null);
        } else {
            count = this.mDb.delete(tableName, selection, selectionArgs);
        }
        Localytics.Log.v(String.format("Deleted %d rows", Integer.valueOf(count)));
        return count;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int update(String tableName, ContentValues values, String selection, String[] selectionArgs) {
        Localytics.Log.v(String.format("Update table: %s, values: %s, selection: %s, selectionArgs: %s", tableName, values.toString(), selection, Arrays.toString(selectionArgs)));
        return this.mDb.update(tableName, values, selection, selectionArgs);
    }

    public void runBatchTransaction(Runnable runnable) {
        this.mDb.beginTransaction();
        try {
            runnable.run();
            this.mDb.setTransactionSuccessful();
        } finally {
            this.mDb.endTransaction();
        }
    }

    void close() {
        this.mDb.close();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static <T> String buildSqlInClause(List<T> objects, InClauseBuilder<T> builder) {
        return buildSqlInClauseImpl(objects, objects.size(), builder);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String buildSqlInClause(JSONArray array, InClauseBuilder<JSONObject> builder) {
        return buildSqlInClauseImpl(array, array.length(), builder);
    }

    private static <T> String buildSqlInClauseImpl(Object listOrArray, int size, InClauseBuilder<T> inClauseBuilder) {
        StringBuilder sb = new StringBuilder("(");
        for (int i = 0; i < size; i++) {
            if (listOrArray instanceof List) {
                List<T> list = (List) listOrArray;
                sb.append(inClauseBuilder.getValue(list.get(i)));
            } else if (listOrArray instanceof JSONArray) {
                JSONArray array = (JSONArray) listOrArray;
                try {
                    sb.append(inClauseBuilder.getValue(array.getJSONObject(i)));
                } catch (JSONException e) {
                }
            }
            if (i != size - 1) {
                sb.append(",");
            }
        }
        sb.append(")");
        return sb.toString();
    }

    /* loaded from: classes.dex */
    static abstract class LocalyticsDatabaseHelper extends SQLiteOpenHelper {
        static final String SQLITE_BOOLEAN_FALSE = "0";
        static final String SQLITE_BOOLEAN_TRUE = "1";
        private static int completedMigrations;
        static SQLiteDatabase oldDB;
        private static File oldDBFile = null;
        LocalyticsDao mLocalyticsDao;

        protected abstract void migrateV2ToV3(SQLiteDatabase sQLiteDatabase);

        /* JADX INFO: Access modifiers changed from: package-private */
        public LocalyticsDatabaseHelper(String name, int version, LocalyticsDao localyticsDao) {
            super(localyticsDao.getAppContext(), name, (SQLiteDatabase.CursorFactory) null, version);
            this.mLocalyticsDao = localyticsDao;
            synchronized (LocalyticsDatabaseHelper.class) {
                if (oldDBFile == null) {
                    String dbName = String.format(BaseProvider.OLD_DATABASE_FILE, DatapointHelper.getSha256_buggy(localyticsDao.getAppKey()));
                    String path = localyticsDao.getAppContext().getDatabasePath(dbName).getPath();
                    File file = new File(path);
                    oldDBFile = file;
                    if (file.exists()) {
                        completedMigrations = 0;
                        MigrationDatabaseHelper mDB = new MigrationDatabaseHelper(dbName, 18, localyticsDao);
                        try {
                            oldDB = mDB.getWritableDatabase();
                        } catch (SQLiteException e) {
                            Localytics.Log.w("Error opening old database; old data will not be retained.");
                        }
                    }
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static void cleanUpOldDB() {
            int i = completedMigrations + 1;
            completedMigrations = i;
            if (i == 3) {
                oldDB.close();
                oldDBFile.delete();
            }
        }

        static void resetStaticVars() {
            oldDBFile = null;
            oldDB = null;
            completedMigrations = 0;
        }
    }
}
