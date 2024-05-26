package com.nuance.connect.store;

import com.nuance.connect.util.Data;
import com.nuance.connect.util.EncryptUtils;
import com.nuance.connect.util.Logger;
import com.nuance.connect.util.StringUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/* loaded from: classes.dex */
public class FileStore implements PersistentDataStore {
    private static final String PREFS_DIR = "prefs";
    private static final String PREF_FILE_EXTENSION = ".dat";
    private final String appFilesFolder;
    private final String emptyEncrypt;
    private final String legacySecretKey;
    private final String nullEncrypt;
    private final String secretKey;
    Logger.Log oemLog = Logger.getLog(Logger.LoggerType.OEM, getClass().getSimpleName());
    private final Map<String, String> obfuscatedFileNames = new ConcurrentHashMap();
    private final Map<String, Object> fileLocks = new HashMap();

    public FileStore(String str, String str2, String str3) {
        this.appFilesFolder = str;
        this.secretKey = str2;
        this.legacySecretKey = str3;
        this.nullEncrypt = EncryptUtils.encryptString(null, str2);
        this.emptyEncrypt = EncryptUtils.encryptString("", str2);
    }

    private String getObfuscatedFileName(String str) {
        if (this.obfuscatedFileNames.containsKey(str)) {
            return this.obfuscatedFileNames.get(str);
        }
        String str2 = EncryptUtils.md5(str.getBytes(Charset.forName("UTF-8"))) + PREF_FILE_EXTENSION;
        this.obfuscatedFileNames.put(str, str2);
        return str2;
    }

    private File getPrefFile(String str) {
        return new File(getPrefsDir(), getObfuscatedFileName(str));
    }

    private File getPrefsDir() {
        if (this.appFilesFolder == null) {
            return null;
        }
        File file = new File(this.appFilesFolder, PREFS_DIR);
        if (file.exists() || file.mkdirs()) {
            return file;
        }
        this.oemLog.e("Unable to create persistent storage directory when it was missing");
        return null;
    }

    @Override // com.nuance.connect.store.PersistentDataStore
    public boolean clear() {
        String[] list;
        File file = new File(this.appFilesFolder, PREFS_DIR);
        if (file.isDirectory() && (list = file.list()) != null) {
            for (String str : list) {
                if (!new File(file, str).delete()) {
                    this.oemLog.v("failed to clear preferences files");
                }
            }
        }
        this.obfuscatedFileNames.clear();
        return true;
    }

    @Override // com.nuance.connect.store.PersistentDataStore
    public boolean delete(String str) {
        File prefFile = getPrefFile(str);
        if (prefFile.exists()) {
            return prefFile.delete();
        }
        return true;
    }

    @Override // com.nuance.connect.store.PersistentDataStore
    public boolean exists(String str) {
        return getPrefFile(str).exists();
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
        Object obj;
        String fileContents;
        String str3;
        File prefFile = getPrefFile(str);
        if (!prefFile.exists()) {
            return str2;
        }
        synchronized (this.fileLocks) {
            if (!this.fileLocks.containsKey(str)) {
                this.fileLocks.put(str, new int[0]);
            }
            obj = this.fileLocks.get(str);
        }
        synchronized (obj) {
            fileContents = StringUtils.getFileContents(prefFile);
        }
        if (fileContents == null || "".equals(fileContents)) {
            str3 = fileContents;
        } else {
            str3 = EncryptUtils.decryptString(fileContents, this.secretKey);
            if (this.legacySecretKey != null && ((str3 == null || "".equals(str3)) && !fileContents.equals(this.nullEncrypt) && !fileContents.equals(this.emptyEncrypt))) {
                str3 = EncryptUtils.legacyDecryptString(fileContents, this.legacySecretKey);
                String legacyDecryptString = EncryptUtils.legacyDecryptString(fileContents, this.secretKey);
                if (legacyDecryptString != null && !legacyDecryptString.isEmpty() && (str3 == null || str3.length() > legacyDecryptString.length())) {
                    str3 = legacyDecryptString;
                }
                if (str3 != null && !"".equals(str3)) {
                    this.oemLog.v("Switching encryption key: " + str);
                    saveString(str, str3);
                }
            }
        }
        return (str3 == null || "".equals(str3)) ? str2 : str3;
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
        Object obj;
        FileOutputStream fileOutputStream;
        FileLock fileLock = null;
        boolean z = false;
        if (str2 != null) {
            String encryptString = EncryptUtils.encryptString(str2, this.secretKey);
            File prefFile = getPrefFile(str);
            if (!prefFile.exists()) {
                try {
                    prefFile.createNewFile();
                } catch (IOException e) {
                }
            }
            synchronized (this.fileLocks) {
                if (!this.fileLocks.containsKey(str)) {
                    this.fileLocks.put(str, new int[0]);
                }
                obj = this.fileLocks.get(str);
            }
            try {
                byte[] bytes = encryptString.getBytes(Charset.forName("UTF-8"));
                try {
                    synchronized (obj) {
                        try {
                            fileOutputStream = new FileOutputStream(prefFile);
                        } catch (Throwable th) {
                            th = th;
                            fileOutputStream = null;
                        }
                        try {
                            fileLock = fileOutputStream.getChannel().lock();
                            fileOutputStream.write(bytes);
                            z = true;
                            if (fileLock != null) {
                                try {
                                    fileLock.release();
                                } catch (Exception e2) {
                                }
                            }
                            if (fileOutputStream != null) {
                                fileOutputStream.close();
                            }
                        } catch (Throwable th2) {
                            th = th2;
                            if (fileLock != null) {
                                try {
                                    fileLock.release();
                                } catch (Exception e3) {
                                }
                            }
                            if (fileOutputStream == null) {
                                throw th;
                            }
                            fileOutputStream.close();
                            throw th;
                        }
                    }
                } catch (Throwable th3) {
                    Throwable th4 = th3;
                    while (true) {
                        try {
                            try {
                                break;
                            } catch (FileNotFoundException e4) {
                                z = false;
                                e = e4;
                                this.oemLog.e((Object) "Exception reading file", (Throwable) e);
                                return z;
                            } catch (IOException e5) {
                                z = false;
                                e = e5;
                                this.oemLog.e((Object) "Exception reading file", (Throwable) e);
                                return z;
                            } catch (OverlappingFileLockException e6) {
                                z = false;
                                e = e6;
                                this.oemLog.e((Object) "Exception reading file", (Throwable) e);
                                return z;
                            }
                        } catch (Throwable th5) {
                            th4 = th5;
                        }
                    }
                    throw th4;
                }
            } catch (FileNotFoundException e7) {
                e = e7;
            } catch (IOException e8) {
                e = e8;
            } catch (OverlappingFileLockException e9) {
                e = e9;
            }
        }
        return z;
    }
}
