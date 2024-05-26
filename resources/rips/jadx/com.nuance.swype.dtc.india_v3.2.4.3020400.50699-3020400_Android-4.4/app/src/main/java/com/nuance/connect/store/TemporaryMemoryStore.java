package com.nuance.connect.store;

import com.nuance.connect.util.Data;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public class TemporaryMemoryStore implements PersistentDataStore {
    private final Map<String, String> store = new HashMap();

    @Override // com.nuance.connect.store.PersistentDataStore
    public boolean clear() {
        this.store.clear();
        return true;
    }

    @Override // com.nuance.connect.store.PersistentDataStore
    public boolean delete(String str) {
        this.store.remove(str);
        return true;
    }

    @Override // com.nuance.connect.store.PersistentDataStore
    public boolean exists(String str) {
        return this.store.containsKey(str);
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
        String str3 = this.store.get(str);
        return str3 == null ? str2 : str3;
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
    public boolean saveString(String str, String str2) {
        this.store.put(str, str2);
        return true;
    }
}
