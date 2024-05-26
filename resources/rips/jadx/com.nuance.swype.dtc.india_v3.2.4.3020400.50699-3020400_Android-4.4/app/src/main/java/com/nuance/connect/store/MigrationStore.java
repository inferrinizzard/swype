package com.nuance.connect.store;

import com.nuance.connect.util.Logger;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/* loaded from: classes.dex */
public class MigrationStore implements PersistentDataStore {
    private static final Logger.Log log = Logger.getLog(Logger.LoggerType.DEVELOPER, MigrationStore.class.getSimpleName());
    private final PersistentDataStore destination;
    private final Set<String> keys = new CopyOnWriteArraySet();
    private final PersistentDataStore source;

    public MigrationStore(PersistentDataStore persistentDataStore, PersistentDataStore persistentDataStore2) {
        this.source = persistentDataStore;
        this.destination = persistentDataStore2;
    }

    private void migrateKey(String str) {
        if (this.keys.contains(str)) {
            return;
        }
        log.d("migrating key: ", str);
        this.keys.add(str);
        this.source.delete(str);
    }

    @Override // com.nuance.connect.store.PersistentDataStore
    public boolean clear() {
        return this.source.clear() && this.destination.clear();
    }

    @Override // com.nuance.connect.store.PersistentDataStore
    public boolean delete(String str) {
        if (!this.keys.contains(str)) {
            if (!this.source.delete(str)) {
                return false;
            }
            this.keys.add(str);
            log.d("migrating key: ", str);
        }
        return this.destination.delete(str);
    }

    @Override // com.nuance.connect.store.PersistentDataStore
    public boolean exists(String str) {
        return !this.keys.contains(str) ? this.source.exists(str) || this.destination.exists(str) : this.destination.exists(str);
    }

    @Override // com.nuance.connect.store.PersistentDataStore
    public Boolean readBoolean(String str, Boolean bool) {
        if (!this.keys.contains(str)) {
            this.keys.add(str);
            log.d("migrating key: ", str);
            if (this.source.exists(str)) {
                Boolean readBoolean = this.source.readBoolean(str, bool);
                this.destination.saveBoolean(str, readBoolean.booleanValue());
                this.source.delete(str);
                return readBoolean;
            }
        }
        return this.destination.readBoolean(str, bool);
    }

    @Override // com.nuance.connect.store.PersistentDataStore
    public boolean readBoolean(String str, boolean z) {
        if (!this.keys.contains(str)) {
            this.keys.add(str);
            log.d("migrating key: ", str);
            if (this.source.exists(str)) {
                boolean readBoolean = this.source.readBoolean(str, z);
                this.destination.saveBoolean(str, readBoolean);
                this.source.delete(str);
                return readBoolean;
            }
        }
        return this.destination.readBoolean(str, z);
    }

    @Override // com.nuance.connect.store.PersistentDataStore
    public int readInt(String str, int i) {
        if (!this.keys.contains(str)) {
            this.keys.add(str);
            log.d("migrating key: ", str);
            if (this.source.exists(str)) {
                int readInt = this.source.readInt(str, i);
                this.destination.saveInt(str, readInt);
                this.source.delete(str);
                return readInt;
            }
        }
        return this.destination.readInt(str, i);
    }

    @Override // com.nuance.connect.store.PersistentDataStore
    public long readLong(String str, long j) {
        if (!this.keys.contains(str)) {
            this.keys.add(str);
            log.d("migrating key: ", str);
            if (this.source.exists(str)) {
                long readLong = this.source.readLong(str, j);
                this.destination.saveLong(str, readLong);
                this.source.delete(str);
                return readLong;
            }
        }
        return this.destination.readLong(str, j);
    }

    @Override // com.nuance.connect.store.PersistentDataStore
    public Object readObject(String str) {
        if (!this.keys.contains(str)) {
            this.keys.add(str);
            log.d("migrating key: ", str);
            if (this.source.exists(str)) {
                Object readObject = this.source.readObject(str);
                this.destination.saveObject(str, readObject);
                this.source.delete(str);
                return readObject;
            }
        }
        return this.destination.readObject(str);
    }

    @Override // com.nuance.connect.store.PersistentDataStore
    public String readString(String str, String str2) {
        if (!this.keys.contains(str)) {
            this.keys.add(str);
            log.d("migrating key: ", str);
            if (this.source.exists(str)) {
                String readString = this.source.readString(str, str2);
                this.destination.saveString(str, readString);
                this.source.delete(str);
                return readString;
            }
        }
        return this.destination.readString(str, str2);
    }

    @Override // com.nuance.connect.store.PersistentDataStore
    public boolean saveBoolean(String str, boolean z) {
        migrateKey(str);
        return this.destination.saveBoolean(str, z);
    }

    @Override // com.nuance.connect.store.PersistentDataStore
    public boolean saveInt(String str, int i) {
        migrateKey(str);
        return this.destination.saveInt(str, i);
    }

    @Override // com.nuance.connect.store.PersistentDataStore
    public boolean saveLong(String str, long j) {
        migrateKey(str);
        return this.destination.saveLong(str, j);
    }

    @Override // com.nuance.connect.store.PersistentDataStore
    public boolean saveObject(String str, Object obj) {
        migrateKey(str);
        return this.destination.saveObject(str, obj);
    }

    @Override // com.nuance.connect.store.PersistentDataStore
    public boolean saveString(String str, String str2) {
        migrateKey(str);
        return this.destination.saveString(str, str2);
    }
}
