package com.nuance.connect.store;

import android.content.Context;
import com.nuance.connect.util.EncryptUtils;

/* loaded from: classes.dex */
public class DataStoreFactory {
    public static final String CONNECT_PREFERENCES = "ConnectPrefs";
    public static final String DATA_STORE_TYPE_DATABASE = "com.nuance.swype.connect.store.DatabaseDataStore";
    public static final String DATA_STORE_TYPE_DEFAULT = "com.nuance.swype.connect.store.FilePreference";
    public static final String DATA_STORE_TYPE_FILE = "com.nuance.swype.connect.store.FilePreference";
    public static final String DATA_STORE_TYPE_MIGRATION = "com.nuance.swype.connect.store.MigrationStore";
    public static final String DATA_STORE_TYPE_SHARED_PREFS = "com.nuance.swype.connect.store.SharedPrefStore";
    public static final String DATA_STORE_TYPE_TEMP = "com.nuance.swype.connect.store.TemporaryMemoryStore";
    private static volatile PersistentDataStore defaultStore = null;

    public static PersistentDataStore getDataStore(Context context, String str, String str2) {
        if ("com.nuance.swype.connect.store.FilePreference".equals(str)) {
            return new FileStore(context.getFilesDir().getAbsolutePath(), EncryptUtils.defaultSecretKey(context), str2);
        }
        if (DATA_STORE_TYPE_TEMP.equals(str)) {
            return new TemporaryMemoryStore();
        }
        if (DATA_STORE_TYPE_SHARED_PREFS.equals(str)) {
            return new SharedPrefStore(context, CONNECT_PREFERENCES, str2);
        }
        if (DATA_STORE_TYPE_DATABASE.equals(str)) {
            return new DatabaseDataStore(context);
        }
        if (DATA_STORE_TYPE_MIGRATION.equals(str)) {
            return new MigrationStore(getDataStore(context, "com.nuance.swype.connect.store.FilePreference", str2), getDataStore(context, DATA_STORE_TYPE_DATABASE, str2));
        }
        if (!"com.nuance.swype.connect.store.FilePreference".equals(str)) {
            return new DatabaseDataStore(context);
        }
        synchronized (DataStoreFactory.class) {
            if (defaultStore != null) {
                return defaultStore;
            }
            DatabaseDataStore databaseDataStore = new DatabaseDataStore(context);
            if (str2 != null) {
                PersistentDataStore dataStore = getDataStore(context, "com.nuance.swype.connect.store.FilePreference", str2);
                if (databaseDataStore.isEmpty()) {
                    defaultStore = new MigrationStore(dataStore, databaseDataStore);
                } else {
                    defaultStore = databaseDataStore;
                    dataStore.clear();
                }
            } else {
                defaultStore = databaseDataStore;
            }
            return defaultStore;
        }
    }
}
