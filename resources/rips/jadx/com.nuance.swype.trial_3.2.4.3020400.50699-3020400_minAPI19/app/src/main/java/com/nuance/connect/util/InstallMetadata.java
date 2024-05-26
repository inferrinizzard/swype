package com.nuance.connect.util;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.nuance.connect.common.Integers;
import com.nuance.connect.common.Strings;
import com.nuance.connect.store.PersistentDataStore;
import com.nuance.connect.util.Logger;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;

/* loaded from: classes.dex */
public class InstallMetadata {
    private static final Pattern ARRAY_STRING_PATTERN = Pattern.compile("[\\[\\] ]");
    private static final int DATA_EXPIRED = 100;
    private static final int DATA_EXPIRED_TIMEOUT = 30000;
    public static final int INSTALL_AVAILABLE = 0;
    public static final int INSTALL_CANCELED = 8;
    public static final int INSTALL_DOWNLOAD = 3;
    public static final int INSTALL_DOWNLOAD_COMPLETE = 4;
    public static final int INSTALL_DOWNLOAD_VERIFIED = 5;
    public static final int INSTALL_FAILED = 9;
    public static final int INSTALL_INSTALLED = 7;
    public static final int INSTALL_PENDING = 1;

    @Deprecated
    public static final int INSTALL_PENDING_LICENSING = 6;
    public static final int INSTALL_REMOVED = 10;
    public static final int INSTALL_REQUEST = 2;
    public static final int INSTALL_UNKNOWN = 11;
    private final WeakReference<MetaDataClient> context;
    private volatile String dataStoreKey;
    private volatile boolean flagTransaction;
    private volatile boolean ignoreTimer;
    private volatile HashMap<String, HashMap<String, String>> installMetadata;
    private volatile boolean metadataChanged;
    private final int[] rwLock = new int[0];
    private final ReentrantLock transactionLock = new ReentrantLock();
    private final AtomicLong lastUsed = new AtomicLong(0);
    private final Logger.Log log = Logger.getLog(Logger.LoggerType.DEVELOPER, getClass().getSimpleName());
    private final Handler.Callback callback = new Handler.Callback() { // from class: com.nuance.connect.util.InstallMetadata.1
        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message message) {
            synchronized (InstallMetadata.this.rwLock) {
                switch (message.what) {
                    case 100:
                        if (InstallMetadata.this.lastUsed.get() < System.currentTimeMillis() - 30000) {
                            InstallMetadata.this.flush();
                            break;
                        }
                        break;
                }
            }
            return false;
        }
    };
    private final Handler queue = new Handler(Looper.getMainLooper(), new WeakReferenceHandlerCallback(this.callback));

    /* loaded from: classes.dex */
    public interface MetaDataClient {
        PersistentDataStore getDataStore();
    }

    /* loaded from: classes.dex */
    private static class WeakReferenceHandlerCallback implements Handler.Callback {
        private final WeakReference<Handler.Callback> callbackRef;

        public WeakReferenceHandlerCallback(Handler.Callback callback) {
            this.callbackRef = new WeakReference<>(callback);
        }

        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message message) {
            Handler.Callback callback = this.callbackRef.get();
            if (callback != null) {
                return callback.handleMessage(message);
            }
            return true;
        }
    }

    public InstallMetadata(MetaDataClient metaDataClient, String str) {
        this.context = new WeakReference<>(metaDataClient);
        this.dataStoreKey = str;
    }

    private void checkLoaded() {
        synchronized (this.rwLock) {
            resetExpirationTimer();
            if (this.installMetadata == null) {
                loadMetadata();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void flush() {
        saveMetadata();
        this.lastUsed.set(0L);
        synchronized (this.rwLock) {
            this.installMetadata = null;
        }
    }

    private String getPackageList(List<Integer> list, String str) {
        StringBuilder sb = new StringBuilder();
        if (str == null) {
            str = ",";
        }
        Iterator<String> it = listFromSteps(list).iterator();
        String str2 = "";
        while (it.hasNext()) {
            sb.append(str2).append(it.next());
            str2 = str;
        }
        return sb.toString();
    }

    private void loadMetadata() {
        this.lastUsed.set(System.currentTimeMillis());
        synchronized (this.rwLock) {
            MetaDataClient metaDataClient = this.context.get();
            if (metaDataClient != null) {
                this.installMetadata = loadPersistentData(metaDataClient);
            } else {
                this.installMetadata = new HashMap<>();
            }
        }
    }

    private HashMap<String, HashMap<String, String>> loadPersistentData(MetaDataClient metaDataClient) {
        Object unserializeObject = Data.unserializeObject(metaDataClient.getDataStore().readString(this.dataStoreKey, ""));
        return unserializeObject != null ? (HashMap) unserializeObject : new HashMap<>();
    }

    private void pauseExpirationTimer() {
        this.queue.removeMessages(100);
        this.ignoreTimer = true;
    }

    private void propWrite(String str, String str2, String str3, boolean z) {
        checkLoaded();
        synchronized (this.rwLock) {
            if (this.installMetadata.containsKey(str)) {
                HashMap<String, String> props = getProps(str);
                props.put(str2, str3);
                this.installMetadata.put(str, props);
            } else {
                this.log.v("writing property to non-existent package: ", str);
            }
        }
        this.metadataChanged = true;
        if (z) {
            saveMetadata();
        }
    }

    private void resetExpirationTimer() {
        this.lastUsed.set(System.currentTimeMillis());
        if (this.ignoreTimer) {
            return;
        }
        this.queue.removeMessages(100);
        this.queue.sendMessageDelayed(this.queue.obtainMessage(100), 30000L);
    }

    private boolean testPackageInstallsInRange(int i, int i2) {
        int i3;
        checkLoaded();
        if (i <= i2) {
            i2 = i;
            i = i2;
        }
        Iterator<Map.Entry<String, HashMap<String, String>>> it = this.installMetadata.entrySet().iterator();
        boolean z = false;
        while (it.hasNext()) {
            try {
                i3 = Integer.parseInt(it.next().getValue().get(Strings.MAP_KEY_STEP));
            } catch (NumberFormatException e) {
                i3 = 0;
            }
            if (i3 >= i2 && i3 <= i) {
                z = true;
            }
            z = z;
        }
        return z;
    }

    public boolean addPackage(String str) {
        checkLoaded();
        HashMap<String, String> hashMap = new HashMap<>();
        if (this.installMetadata.containsKey(str)) {
            this.log.d("package already known: (", str, ")");
            return false;
        }
        synchronized (this.rwLock) {
            hashMap.put("STARTED", Long.toString(System.currentTimeMillis()));
            hashMap.put(Strings.MAP_KEY_STEP, Integer.toString(0));
            this.installMetadata.put(str, hashMap);
        }
        this.metadataChanged = true;
        saveMetadata();
        return true;
    }

    public Set<String> allPackages() {
        HashSet hashSet;
        synchronized (this.rwLock) {
            checkLoaded();
            hashSet = new HashSet(this.installMetadata.keySet());
        }
        return hashSet;
    }

    public void beginTransaction() {
        this.flagTransaction = true;
        this.transactionLock.lock();
        checkLoaded();
        pauseExpirationTimer();
    }

    public void clear() {
        synchronized (this.rwLock) {
            this.installMetadata = new HashMap<>();
        }
        this.metadataChanged = true;
        saveMetadata();
    }

    public void commitTransaction() {
        this.flagTransaction = false;
        saveMetadata();
        this.transactionLock.unlock();
        this.ignoreTimer = false;
        resetExpirationTimer();
    }

    public boolean deletePackage(String str) {
        checkLoaded();
        if (!this.installMetadata.containsKey(str)) {
            return false;
        }
        synchronized (this.rwLock) {
            this.installMetadata.remove(str);
        }
        saveMetadata();
        return true;
    }

    public HashMap<String, HashMap<String, String>> getAllMetadata() {
        checkLoaded();
        HashMap<String, HashMap<String, String>> hashMap = new HashMap<>();
        synchronized (this.rwLock) {
            for (Map.Entry<String, HashMap<String, String>> entry : this.installMetadata.entrySet()) {
                hashMap.put(entry.getKey(), (HashMap) entry.getValue().clone());
            }
        }
        return hashMap;
    }

    public boolean getBoolProp(String str, String str2) {
        return Boolean.parseBoolean(getProp(str, str2));
    }

    public String getInstalledPackageList() {
        return getPackageList(Arrays.asList(7), ",");
    }

    public int[] getIntArrayProp(String str, String str2) {
        try {
            String prop = getProp(str, str2);
            if (prop == null || prop.isEmpty()) {
                return new int[0];
            }
            String[] split = prop.split(",");
            int[] iArr = new int[split.length];
            for (int i = 0; i < split.length; i++) {
                iArr[i] = Integer.parseInt(split[i]);
            }
            return iArr;
        } catch (NumberFormatException e) {
            this.log.d(e.getMessage());
            return new int[0];
        }
    }

    public int getIntProp(String str, String str2) {
        try {
            return Integer.parseInt(getProp(str, str2));
        } catch (NumberFormatException e) {
            return Integers.STATUS_SUCCESS;
        }
    }

    public Integer[] getIntegerArrayProp(String str, String str2) {
        try {
            String prop = getProp(str, str2);
            if (prop == null || prop.isEmpty()) {
                return new Integer[0];
            }
            String[] split = prop.split(",");
            Integer[] numArr = new Integer[split.length];
            for (int i = 0; i < split.length; i++) {
                numArr[i] = Integer.valueOf(Integer.parseInt(split[i]));
            }
            return numArr;
        } catch (NumberFormatException e) {
            this.log.d(e.getMessage());
            return new Integer[0];
        }
    }

    public long getLongProp(String str, String str2) {
        try {
            return Long.parseLong(getProp(str, str2));
        } catch (NumberFormatException e) {
            return Long.MIN_VALUE;
        }
    }

    public String getProp(String str, String str2) {
        String str3;
        synchronized (this.rwLock) {
            checkLoaded();
            str3 = this.installMetadata.containsKey(str) ? this.installMetadata.get(str).get(str2) : null;
        }
        return str3;
    }

    public HashMap<String, String> getProps(String str) {
        checkLoaded();
        return this.installMetadata.containsKey(str) ? (HashMap) this.installMetadata.get(str).clone() : new HashMap<>();
    }

    public int getStep(String str) {
        return getIntProp(str, Strings.MAP_KEY_STEP);
    }

    public boolean hasMoreInstalls() {
        boolean testPackageInstallsInRange = testPackageInstallsInRange(1, 5);
        this.log.d("hasMoreInstalls() : ", Boolean.valueOf(testPackageInstallsInRange));
        return testPackageInstallsInRange;
    }

    public boolean hasMoreRemoves() {
        boolean testPackageInstallsInRange = testPackageInstallsInRange(10, 10);
        this.log.d("hasMoreRemoves() : ", Boolean.valueOf(testPackageInstallsInRange));
        return testPackageInstallsInRange;
    }

    public boolean hasPackage(String str) {
        checkLoaded();
        return this.installMetadata.containsKey(str);
    }

    public boolean hasPackages() {
        checkLoaded();
        return !this.installMetadata.isEmpty();
    }

    public boolean isAvailable(String str) {
        return getStep(str) == 0;
    }

    public boolean isDownloading(String str) {
        int step = getStep(str);
        return step > 1 && step <= 5;
    }

    public boolean isInstalled(String str) {
        return getStep(str) == 7;
    }

    public boolean isInstalling() {
        boolean testPackageInstallsInRange = testPackageInstallsInRange(2, 5);
        this.log.d("isInstalling() : ", Boolean.valueOf(testPackageInstallsInRange));
        return testPackageInstallsInRange;
    }

    public boolean isInstalling(String str) {
        int step = getStep(str);
        return step > 0 && step < 7;
    }

    public List<String> listFromIntArrayPropContains(String str, int i) {
        checkLoaded();
        ArrayList arrayList = new ArrayList();
        synchronized (this.rwLock) {
            for (String str2 : this.installMetadata.keySet()) {
                try {
                    Integer[] integerArrayProp = getIntegerArrayProp(str2, str);
                    int length = integerArrayProp.length;
                    int i2 = 0;
                    while (true) {
                        if (i2 >= length) {
                            break;
                        }
                        if (i == integerArrayProp[i2].intValue()) {
                            arrayList.add(str2);
                            break;
                        }
                        i2++;
                    }
                } catch (NumberFormatException e) {
                } catch (Exception e2) {
                }
            }
        }
        return arrayList;
    }

    public List<String> listFromPropEquals(String str, int i) {
        checkLoaded();
        ArrayList arrayList = new ArrayList();
        synchronized (this.rwLock) {
            for (Map.Entry<String, HashMap<String, String>> entry : this.installMetadata.entrySet()) {
                try {
                    if (Integer.parseInt(entry.getValue().get(str)) == i) {
                        arrayList.add(entry.getKey());
                    }
                } catch (NumberFormatException e) {
                }
            }
        }
        return arrayList;
    }

    public List<String> listFromPropEquals(String str, String str2) {
        checkLoaded();
        ArrayList arrayList = new ArrayList();
        synchronized (this.rwLock) {
            for (Map.Entry<String, HashMap<String, String>> entry : this.installMetadata.entrySet()) {
                try {
                    String str3 = entry.getValue().get(str);
                    if (str3 != null && str3.equals(str2)) {
                        arrayList.add(entry.getKey());
                    }
                } catch (NumberFormatException e) {
                }
            }
        }
        return arrayList;
    }

    public List<String> listFromSteps(List<Integer> list) {
        checkLoaded();
        ArrayList arrayList = new ArrayList();
        synchronized (this.rwLock) {
            for (Map.Entry<String, HashMap<String, String>> entry : this.installMetadata.entrySet()) {
                if (list.contains(Integer.valueOf(Integer.parseInt(entry.getValue().get(Strings.MAP_KEY_STEP))))) {
                    arrayList.add(entry.getKey());
                }
            }
        }
        return arrayList;
    }

    public void propsWrite(String str, HashMap<String, String> hashMap, boolean z) {
        checkLoaded();
        synchronized (this.rwLock) {
            if (this.installMetadata.containsKey(str)) {
                this.installMetadata.put(str, hashMap);
            }
        }
        this.metadataChanged = true;
        if (z) {
            saveMetadata();
        }
    }

    public void removeProp(String str, String str2) {
        checkLoaded();
        synchronized (this.rwLock) {
            if (this.installMetadata.containsKey(str)) {
                HashMap<String, String> props = getProps(str);
                props.remove(str2);
                this.installMetadata.put(str, props);
            } else {
                this.log.v("removing property to non-existent package: ", str);
            }
        }
        this.metadataChanged = true;
        saveMetadata();
    }

    public void saveMetadata() {
        MetaDataClient metaDataClient = this.context.get();
        if (metaDataClient == null || this.flagTransaction || !this.metadataChanged) {
            return;
        }
        synchronized (this.rwLock) {
            metaDataClient.getDataStore().saveString(this.dataStoreKey, Data.serializeObject(this.installMetadata));
            this.metadataChanged = false;
        }
    }

    public void setProp(String str, String str2, int i) {
        setProp(str, str2, Integer.toString(i));
    }

    public void setProp(String str, String str2, long j) {
        setProp(str, str2, Long.toString(j));
    }

    public void setProp(String str, String str2, String str3) {
        propWrite(str, str2, str3, true);
    }

    public void setProp(String str, String str2, boolean z) {
        setProp(str, str2, Boolean.toString(z));
    }

    public void setProp(String str, String str2, int[] iArr) {
        setProp(str, str2, ARRAY_STRING_PATTERN.matcher(Arrays.toString(iArr)).replaceAll(""));
    }

    public void setProps(String str, HashMap<String, String> hashMap) {
        propsWrite(str, hashMap, true);
    }

    public void setStep(String str, int i) {
        setProp(str, Strings.MAP_KEY_STEP, i);
    }

    public void setUnsavedProp(String str, String str2, int i) {
        setUnsavedProp(str, str2, Integer.toString(i));
    }

    public void setUnsavedProp(String str, String str2, long j) {
        setUnsavedProp(str, str2, Long.toString(j));
    }

    public void setUnsavedProp(String str, String str2, String str3) {
        propWrite(str, str2, str3, false);
    }

    public void setUnsavedProp(String str, String str2, boolean z) {
        setUnsavedProp(str, str2, Boolean.toString(z));
    }

    public void setUnsavedProp(String str, String str2, int[] iArr) {
        setUnsavedProp(str, str2, ARRAY_STRING_PATTERN.matcher(Arrays.toString(iArr)).replaceAll(""));
    }

    public void setUnsavedProps(String str, HashMap<String, String> hashMap) {
        propsWrite(str, hashMap, false);
    }

    public void setUnsavedStep(String str, int i) {
        setUnsavedProp(str, Strings.MAP_KEY_STEP, i);
    }

    public void uninstallPackage(String str) {
        HashMap<String, String> props = getProps(str);
        props.put(Strings.MAP_KEY_STEP, Integer.toString(0));
        props.put(Strings.MAP_KEY_FILE_LOCATION, null);
        props.put(Strings.MAP_KEY_TRANSACTION_ID, null);
        setProps(str, props);
    }
}
