package com.nuance.connect.store;

/* loaded from: classes.dex */
public interface PersistentDataStore {
    boolean clear();

    boolean delete(String str);

    boolean exists(String str);

    Boolean readBoolean(String str, Boolean bool);

    boolean readBoolean(String str, boolean z);

    int readInt(String str, int i);

    long readLong(String str, long j);

    Object readObject(String str);

    String readString(String str, String str2);

    boolean saveBoolean(String str, boolean z);

    boolean saveInt(String str, int i);

    boolean saveLong(String str, long j);

    boolean saveObject(String str, Object obj);

    boolean saveString(String str, String str2);
}
