package com.nuance.connect.store;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import com.nuance.connect.util.Data;
import com.nuance.connect.util.EncryptUtils;
import java.io.Serializable;
import java.util.Iterator;

/* loaded from: classes.dex */
public class SharedPrefStore implements PersistentDataStore {
    private final String prefPath;
    private final SharedPreferences prefs;
    private final String secretKey;

    @SuppressLint({"InlinedApi"})
    public SharedPrefStore(Context context, String str, String str2) {
        this.prefPath = str;
        this.prefs = context.getSharedPreferences(this.prefPath, 4);
        this.secretKey = str2;
    }

    @Override // com.nuance.connect.store.PersistentDataStore
    public boolean clear() {
        Iterator<String> it = this.prefs.getAll().keySet().iterator();
        while (it.hasNext()) {
            delete(it.next());
        }
        return true;
    }

    @Override // com.nuance.connect.store.PersistentDataStore
    @SuppressLint({"CommitPrefEdits"})
    public boolean delete(String str) {
        SharedPreferences.Editor edit = this.prefs.edit();
        edit.remove(str);
        edit.commit();
        return false;
    }

    @Override // com.nuance.connect.store.PersistentDataStore
    public boolean exists(String str) {
        return this.prefs.contains(str);
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
        String decryptString = EncryptUtils.decryptString(this.prefs.getString(str, null), this.secretKey);
        return decryptString == null ? str2 : decryptString;
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
    @SuppressLint({"CommitPrefEdits"})
    public boolean saveString(String str, String str2) {
        SharedPreferences.Editor edit = this.prefs.edit();
        edit.putString(str, EncryptUtils.encryptString(str2, this.secretKey));
        edit.commit();
        return true;
    }
}
