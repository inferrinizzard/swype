package com.nuance.swypeconnect.ac;

import android.annotation.SuppressLint;
import android.util.SparseArray;
import android.util.SparseIntArray;
import com.facebook.internal.AnalyticsEvents;
import com.nuance.connect.api.ConnectException;
import com.nuance.connect.api.LanguageService;
import com.nuance.connect.common.FeaturesLastUsed;
import com.nuance.connect.store.PersistentDataStore;
import com.nuance.connect.util.ConcurrentCallbackSet;
import com.nuance.connect.util.Logger;
import com.nuance.swypeconnect.ac.ACLanguageDownloadService;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArraySet;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class ACLanguageDownloadServiceBase extends ACService implements ACLanguageDownloadService {
    private static final String DOWNLOADED_LANGUAGES = "AC_Language_Service_Downloaded_Languages_v2";
    private static final String FLAVOR_INSTALLED = "FLAVOR_INSTALLED";
    private static final String LANGUAGE_DL = "LANGUAGE_DL";
    private static final String LANGUAGE_ID = "LANGUAGE_ID";
    private static final String MIGRATING_DOWNLOADED_LANGUAGES = "AC_Language_Service_Downloaded_Languages";
    private static final String PREINSTALLED = "PREINSTALLED";
    private static final String TYPE_INSTALLED = "TYPE_INSTALLED";
    private static final String VERSION = "VERSION";
    private LanguageService languageService;
    public ACManager manager;
    private PersistentDataStore store;
    private static final Logger.Log log = Logger.getLog(Logger.LoggerType.OEM);
    private static final Logger.Log customer = Logger.getLog(Logger.LoggerType.CUSTOMER);
    private final LanguageService.ListCallback languageListCallback = new LanguageService.ListCallback() { // from class: com.nuance.swypeconnect.ac.ACLanguageDownloadServiceBase.1
        @Override // com.nuance.connect.api.LanguageService.ListCallback
        public void languageListUpdate() {
            HashSet hashSet;
            boolean z;
            boolean z2 = false;
            List<LanguageService.LdbInfo> downloadLdbList = ACLanguageDownloadServiceBase.this.languageService.getDownloadLdbList();
            ACLanguageDownloadServiceBase.log.d("languageListUpdate: ", Integer.valueOf(downloadLdbList.size()));
            SparseIntArray sparseIntArray = new SparseIntArray();
            synchronized (ACLanguageDownloadServiceBase.this.supportedLangs) {
                hashSet = new HashSet(ACLanguageDownloadServiceBase.this.supportedLangs.keySet());
            }
            synchronized (ACLanguageDownloadServiceBase.this.languageDatabases) {
                for (LanguageService.LdbInfo ldbInfo : downloadLdbList) {
                    ACLanguageDbInfoImpl aCLanguageDbInfoImpl = (ACLanguageDbInfoImpl) ACLanguageDownloadServiceBase.this.languageDatabases.get(ldbInfo.getXT9LanguageId());
                    if (aCLanguageDbInfoImpl == null) {
                        aCLanguageDbInfoImpl = new ACLanguageDbInfoImpl(ldbInfo, ACLanguageDownloadServiceBase.this);
                        z2 = true;
                    } else if (aCLanguageDbInfoImpl.updateWithInfo(ldbInfo)) {
                        z2 = true;
                    }
                    ACLanguageDownloadServiceBase.this.languageDatabases.put(ldbInfo.getXT9LanguageId(), aCLanguageDbInfoImpl);
                    for (int i : ldbInfo.getXT9LanguageIds()) {
                        if (i != ldbInfo.getXT9LanguageId() && hashSet.contains(Integer.valueOf(i))) {
                            ACLanguageDownloadServiceBase.log.d("adjusting id=", Integer.valueOf(i), " to=", Integer.valueOf(ldbInfo.getXT9LanguageId()));
                            sparseIntArray.put(i, ldbInfo.getXT9LanguageId());
                            ACLanguageDownloadServiceBase.this.languageDatabases.remove(i);
                        }
                    }
                }
                z = z2;
                for (int i2 = 0; i2 < ACLanguageDownloadServiceBase.this.languageDatabases.size(); i2++) {
                    if (ACLanguageDownloadServiceBase.this.languageDatabases.keyAt(i2) < 0) {
                        ACLanguageDownloadServiceBase.this.languageDatabases.removeAt(i2);
                        z = true;
                    }
                }
            }
            if (sparseIntArray.size() > 0) {
                synchronized (ACLanguageDownloadServiceBase.this.supportedLangs) {
                    for (int i3 = 0; i3 < sparseIntArray.size(); i3++) {
                        int keyAt = sparseIntArray.keyAt(i3);
                        int valueAt = sparseIntArray.valueAt(i3);
                        if (ACLanguageDownloadServiceBase.this.supportedLangs.containsKey(Integer.valueOf(keyAt))) {
                            ACLanguageDownloadServiceBase.this.supportedLangs.put(Integer.valueOf(valueAt), ACLanguageDownloadServiceBase.this.supportedLangs.remove(Integer.valueOf(keyAt)));
                        }
                    }
                }
            }
            if (ACLanguageDownloadServiceBase.this.isShutdown || !ACLanguageDownloadServiceBase.this.isLanguageListAvailable()) {
                return;
            }
            if (z) {
                for (ACLanguageDownloadService.ACLanguageListCallback aCLanguageListCallback : (ACLanguageDownloadService.ACLanguageListCallback[]) ACLanguageDownloadServiceBase.this.languageListCallbacks.toArray(new ACLanguageDownloadService.ACLanguageListCallback[0])) {
                    aCLanguageListCallback.onLanguageListUpdate();
                }
            }
            if (ACLanguageDownloadServiceBase.this.bInitialized) {
                return;
            }
            ACLanguageDownloadServiceBase.this.manager.serviceInitialized(ACLanguageDownloadServiceBase.this.getName());
            ACLanguageDownloadServiceBase.this.bInitialized = true;
        }
    };
    private final ConcurrentCallbackSet<ACLanguageDownloadService.ACLanguageListCallback> languageListCallbacks = new ConcurrentCallbackSet<>();
    private boolean bInitialized = false;

    @SuppressLint({"UseSparseArrays"})
    private final HashMap<Integer, HashMap<String, String>> supportedLangs = new HashMap<>();

    @SuppressLint({"UseSparseArrays"})
    private final HashMap<Integer, ACDownloadedLanguage> downloaded = new HashMap<>();
    private final SparseArray<ACLanguageDbInfoImpl> languageDatabases = new SparseArray<>();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class ACDownloadedLanguage {
        final String flavor;
        final String type;
        final int version;
        final int xt9LanguageId;

        ACDownloadedLanguage(int i, int i2, String str, String str2) {
            this.xt9LanguageId = i;
            this.version = i2;
            this.type = str;
            this.flavor = str2;
        }

        static ACDownloadedLanguage fromJson(String str) {
            if (str != null && !"".equals(str)) {
                try {
                    JSONObject jSONObject = new JSONObject(str);
                    return new ACDownloadedLanguage(jSONObject.getInt(ACLanguageDownloadServiceBase.LANGUAGE_ID), jSONObject.getInt(ACLanguageDownloadServiceBase.VERSION), jSONObject.getString(ACLanguageDownloadServiceBase.TYPE_INSTALLED), jSONObject.has(ACLanguageDownloadServiceBase.FLAVOR_INSTALLED) ? jSONObject.getString(ACLanguageDownloadServiceBase.FLAVOR_INSTALLED) : null);
                } catch (JSONException e) {
                    ACLanguageDownloadServiceBase.log.e("Could not create a ACDownloadedLanguage from json: " + str + "; error=" + e.getMessage());
                }
            }
            return null;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public String toJson() {
            try {
                JSONObject jSONObject = new JSONObject();
                jSONObject.put(ACLanguageDownloadServiceBase.LANGUAGE_ID, this.xt9LanguageId);
                jSONObject.put(ACLanguageDownloadServiceBase.VERSION, this.version);
                jSONObject.put(ACLanguageDownloadServiceBase.TYPE_INSTALLED, this.type);
                jSONObject.put(ACLanguageDownloadServiceBase.FLAVOR_INSTALLED, this.flavor);
                return jSONObject.toString();
            } catch (JSONException e) {
                ACLanguageDownloadServiceBase.log.e("Could not convert to JSON: " + e.getMessage());
                return "";
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class ACLanguageDbInfoImpl implements ACLanguageDownloadService.ACLanguageDbInfo {
        private volatile LanguageService.LdbInfo defaultInfo;
        private final Set<LanguageService.LdbInfo> infos = new CopyOnWriteArraySet();
        private final WeakReference<ACLanguageDownloadServiceBase> ref;

        ACLanguageDbInfoImpl(LanguageService.LdbInfo ldbInfo, ACLanguageDownloadServiceBase aCLanguageDownloadServiceBase) {
            this.defaultInfo = ldbInfo;
            if (ldbInfo == null) {
                Logger.getLog(Logger.LoggerType.DEVELOPER).e("ACLanguageDbInfoImpl null");
                this.defaultInfo = new LanguageService.LdbInfo(new int[]{0}, null, null, null, -1);
            }
            this.infos.add(this.defaultInfo);
            this.ref = new WeakReference<>(aCLanguageDownloadServiceBase);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public String getFlavorNameForType(ACLanguageDownloadService.ACLdbType aCLdbType) {
            if (aCLdbType != null && (aCLdbType != ACLanguageDownloadService.ACLdbType.Unspecified || aCLdbType != ACLanguageDownloadService.ACLdbType.ALM)) {
                Iterator<LanguageService.LdbInfo> it = this.infos.iterator();
                while (it.hasNext()) {
                    String flavor = it.next().getFlavor();
                    if (flavor != null && aCLdbType.name().equalsIgnoreCase(flavor)) {
                        return flavor;
                    }
                }
            }
            return null;
        }

        @Override // com.nuance.swypeconnect.ac.ACLanguageDownloadService.ACLanguageDbInfo
        public String getCountryCode() {
            return this.defaultInfo.getCountryCode();
        }

        @Override // com.nuance.swypeconnect.ac.ACLanguageDownloadService.ACLanguageDbInfo
        public String getDisplayName() {
            return this.defaultInfo.getDisplayName();
        }

        @Override // com.nuance.swypeconnect.ac.ACLanguageDownloadService.ACLanguageDbInfo
        public List<ACLanguageDownloadService.ACLdbType> getEnhancedLanguageModels() {
            ArrayList arrayList = new ArrayList();
            ACLanguageDownloadServiceBase aCLanguageDownloadServiceBase = this.ref.get();
            ACLanguageDownloadService.ACLdbType[] types = getTypes();
            if (aCLanguageDownloadServiceBase != null) {
                ACLanguageDownloadService.ACLdbType installedLanguageModel = aCLanguageDownloadServiceBase.getInstalledLanguageModel(this.defaultInfo.getXT9LanguageId());
                for (ACLanguageDownloadService.ACLdbType aCLdbType : types) {
                    if (installedLanguageModel.ordinal() >= aCLdbType.ordinal()) {
                        break;
                    }
                    arrayList.add(aCLdbType);
                }
            }
            if (arrayList.isEmpty()) {
                return null;
            }
            return arrayList;
        }

        @Override // com.nuance.swypeconnect.ac.ACLanguageDownloadService.ACLanguageDbInfo
        public String[] getFlavors() {
            boolean z;
            HashSet hashSet = new HashSet();
            Iterator<LanguageService.LdbInfo> it = this.infos.iterator();
            while (it.hasNext()) {
                String flavor = it.next().getFlavor();
                int length = ACLanguageDownloadService.ACLdbType.values.length;
                while (true) {
                    length--;
                    if (length <= ACLanguageDownloadService.ACLdbType.Unspecified.ordinal()) {
                        z = false;
                        break;
                    }
                    if (ACLanguageDownloadService.ACLdbType.values[length].name().equalsIgnoreCase(flavor)) {
                        z = true;
                        break;
                    }
                }
                if (!z) {
                    hashSet.add(flavor);
                }
            }
            if (hashSet.isEmpty()) {
                return null;
            }
            return (String[]) hashSet.toArray(new String[hashSet.size()]);
        }

        @Override // com.nuance.swypeconnect.ac.ACLanguageDownloadService.ACLanguageDbInfo
        public ACLanguageDownloadService.ACLdbType[] getTypes() {
            LinkedHashSet linkedHashSet = new LinkedHashSet();
            int length = ACLanguageDownloadService.ACLdbType.values.length - 1;
            while (true) {
                int i = length;
                if (i <= ACLanguageDownloadService.ACLdbType.Unspecified.ordinal()) {
                    return (ACLanguageDownloadService.ACLdbType[]) linkedHashSet.toArray(new ACLanguageDownloadService.ACLdbType[linkedHashSet.size()]);
                }
                Iterator<LanguageService.LdbInfo> it = this.infos.iterator();
                while (true) {
                    if (it.hasNext()) {
                        LanguageService.LdbInfo next = it.next();
                        if (next.getFlavor() == null) {
                            linkedHashSet.add(ACLanguageDownloadService.ACLdbType.ALM);
                        } else if (ACLanguageDownloadService.ACLdbType.values[i].name().equalsIgnoreCase(next.getFlavor())) {
                            linkedHashSet.add(ACLanguageDownloadService.ACLdbType.values[i]);
                            break;
                        }
                    }
                }
                length = i - 1;
            }
        }

        public int getVersion() {
            return this.defaultInfo.getVersion();
        }

        @Override // com.nuance.swypeconnect.ac.ACLanguageDownloadService.ACLanguageDbInfo
        public int getXt9LanguageId() {
            return this.defaultInfo.getXT9LanguageId();
        }

        @Override // com.nuance.swypeconnect.ac.ACLanguageDownloadService.ACLanguageDbInfo
        public int[] getXt9LanguageIds() {
            int i = 0;
            TreeSet treeSet = new TreeSet();
            Iterator<LanguageService.LdbInfo> it = this.infos.iterator();
            while (it.hasNext()) {
                for (int i2 : it.next().getXT9LanguageIds()) {
                    treeSet.add(Integer.valueOf(i2));
                }
            }
            int[] iArr = new int[treeSet.size()];
            Iterator it2 = treeSet.iterator();
            while (it2.hasNext()) {
                iArr[i] = ((Integer) it2.next()).intValue();
                i++;
            }
            return iArr;
        }

        @Override // com.nuance.swypeconnect.ac.ACLanguageDownloadService.ACLanguageDbInfo
        public String installedFlavor() {
            ACLanguageDownloadServiceBase aCLanguageDownloadServiceBase = this.ref.get();
            if (aCLanguageDownloadServiceBase != null) {
                return aCLanguageDownloadServiceBase.getInstalledFlavor(this.defaultInfo.getXT9LanguageId());
            }
            return null;
        }

        @Override // com.nuance.swypeconnect.ac.ACLanguageDownloadService.ACLanguageDbInfo
        public ACLanguageDownloadService.ACLdbType installedType() {
            ACLanguageDownloadServiceBase aCLanguageDownloadServiceBase = this.ref.get();
            if (aCLanguageDownloadServiceBase == null || !isInstalled()) {
                return null;
            }
            return aCLanguageDownloadServiceBase.getInstalledLanguageModel(this.defaultInfo.getXT9LanguageId());
        }

        @Override // com.nuance.swypeconnect.ac.ACLanguageDownloadService.ACLanguageDbInfo
        public boolean isDownloaded() {
            ACLanguageDownloadServiceBase aCLanguageDownloadServiceBase = this.ref.get();
            return aCLanguageDownloadServiceBase != null && aCLanguageDownloadServiceBase.isDownloaded(this.defaultInfo.getXT9LanguageId());
        }

        @Override // com.nuance.swypeconnect.ac.ACLanguageDownloadService.ACLanguageDbInfo
        public boolean isInstalled() {
            ACLanguageDownloadServiceBase aCLanguageDownloadServiceBase = this.ref.get();
            return aCLanguageDownloadServiceBase != null && (aCLanguageDownloadServiceBase.isPreinstalled(this.defaultInfo.getXT9LanguageId()) || aCLanguageDownloadServiceBase.isDownloaded(this.defaultInfo.getXT9LanguageId()));
        }

        @Override // com.nuance.swypeconnect.ac.ACLanguageDownloadService.ACLanguageDbInfo
        public boolean isNewerVersionAvailable() {
            ACLanguageDownloadServiceBase aCLanguageDownloadServiceBase = this.ref.get();
            return aCLanguageDownloadServiceBase != null && aCLanguageDownloadServiceBase.isNewerVersionAvailable(this.defaultInfo.getXT9LanguageId(), this.defaultInfo.getVersion());
        }

        @Override // com.nuance.swypeconnect.ac.ACLanguageDownloadService.ACLanguageDbInfo
        public boolean isPreinstalled() {
            ACLanguageDownloadServiceBase aCLanguageDownloadServiceBase = this.ref.get();
            return aCLanguageDownloadServiceBase != null && aCLanguageDownloadServiceBase.isPreinstalled(this.defaultInfo.getXT9LanguageId());
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("ACLanguageDbInfo id=").append(getXt9LanguageId());
            int[] xt9LanguageIds = getXt9LanguageIds();
            sb.append(", ids=[");
            for (int i = 0; i < xt9LanguageIds.length; i++) {
                if (i != 0) {
                    sb.append(",");
                }
                sb.append(xt9LanguageIds[i]);
            }
            sb.append("]");
            sb.append("\n    Display Name: ").append(getDisplayName());
            sb.append("\n    Country Code: ").append(getCountryCode());
            sb.append("\n    All Types: ").append(Arrays.asList(getTypes()));
            sb.append("\n    All Flavors: ").append(Arrays.asList(getFlavors()));
            sb.append("\n    Pre-installed: ").append(isPreinstalled()).append(", isDownloaded: ").append(isDownloaded()).append(", type: ").append(installedType()).append(", flavor=").append(installedFlavor());
            sb.append("\n    Newer version available: ").append(isNewerVersionAvailable());
            sb.append("\n    Version: ").append(this.defaultInfo.getVersion());
            sb.append("\n    Enhanced language models: ").append(getEnhancedLanguageModels());
            return sb.toString();
        }

        boolean updateWithInfo(LanguageService.LdbInfo ldbInfo) {
            boolean z = true;
            if (ldbInfo == null) {
                Logger.getLog(Logger.LoggerType.DEVELOPER).e("updateWithInfo null");
            } else {
                if (this.defaultInfo.getVersion() != ldbInfo.getVersion()) {
                    this.infos.clear();
                    this.defaultInfo = ldbInfo;
                } else {
                    z = false;
                }
                this.infos.add(ldbInfo);
                if (ldbInfo.getFlavor() == null) {
                    this.defaultInfo = ldbInfo;
                }
            }
            return z;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public final class LanguageDownloadCallbackAdapter implements LanguageService.DownloadCallback {
        private ACLanguageDownloadService.ACLanguageDownloadFileCallback callback;
        private String flavor;
        private boolean notifiedStart = false;
        private String type;
        private int version;
        private int xt9LanguageId;

        public LanguageDownloadCallbackAdapter(int i, int i2, String str, String str2, ACLanguageDownloadService.ACLanguageDownloadFileCallback aCLanguageDownloadFileCallback) {
            this.xt9LanguageId = i;
            this.version = i2;
            this.type = str;
            this.flavor = str2;
            this.callback = aCLanguageDownloadFileCallback;
        }

        @Override // com.nuance.connect.api.LanguageService.DownloadCallback
        public final boolean downloadComplete(File file) {
            if (ACLanguageDownloadServiceBase.this.isShutdown) {
                return false;
            }
            if (!this.notifiedStart) {
                downloadStarted();
            }
            ACLanguageDownloadServiceBase.this.manager.getConnect().updateFeatureLastUsed(FeaturesLastUsed.Feature.LANGUAGE, System.currentTimeMillis());
            boolean downloadComplete = this.callback.downloadComplete(file);
            synchronized (ACLanguageDownloadServiceBase.this.downloaded) {
                ACLanguageDownloadServiceBase.customer.d("Language download Complete(" + this.xt9LanguageId + ", v: " + this.version + ", t: " + this.type + ", f: " + this.flavor + "). Status: ", downloadComplete ? "Success" : AnalyticsEvents.PARAMETER_DIALOG_OUTCOME_VALUE_FAILED);
                if (downloadComplete) {
                    ACLanguageDownloadServiceBase.customer.d("Language download Complete saving");
                    ACLanguageDownloadServiceBase.this.downloaded.put(Integer.valueOf(this.xt9LanguageId), new ACDownloadedLanguage(this.xt9LanguageId, this.version, this.type, this.flavor));
                    ACLanguageDownloadServiceBase.this.save();
                }
                ACLanguageDownloadServiceBase.customer.d("Language download Complete: ", ACLanguageDownloadServiceBase.this.downloaded.get(Integer.valueOf(this.xt9LanguageId)));
            }
            return downloadComplete;
        }

        @Override // com.nuance.connect.api.LanguageService.DownloadCallback
        public final void downloadFailed(int i) {
            if (ACLanguageDownloadServiceBase.this.isShutdown) {
                return;
            }
            ACLanguageDownloadServiceBase.customer.d("Language download failed. Reason: ", Integer.valueOf(i));
            this.callback.downloadFailed(i);
        }

        @Override // com.nuance.connect.api.LanguageService.DownloadCallback
        public final void downloadPercentage(int i) {
            if (ACLanguageDownloadServiceBase.this.isShutdown) {
                return;
            }
            if (!this.notifiedStart) {
                downloadStarted();
            }
            this.callback.downloadPercentage(i);
        }

        @Override // com.nuance.connect.api.LanguageService.DownloadCallback
        public final void downloadStarted() {
            if (ACLanguageDownloadServiceBase.this.isShutdown) {
                return;
            }
            ACLanguageDownloadServiceBase.customer.d("Language download started");
            this.notifiedStart = true;
            this.callback.downloadStarted();
        }

        @Override // com.nuance.connect.api.LanguageService.DownloadCallback
        public final void downloadStopped(int i) {
            if (ACLanguageDownloadServiceBase.this.isShutdown) {
                return;
            }
            ACLanguageDownloadServiceBase.customer.d("Language download stopped. Reason: ", Integer.valueOf(i));
            this.callback.downloadStopped(i);
        }

        @Override // com.nuance.connect.api.LanguageService.DownloadCallback
        public final int getVersion() {
            return this.version;
        }
    }

    private void addSupportedLanguage(ACLdbInfo aCLdbInfo) {
        HashMap<String, String> hashMap;
        if (aCLdbInfo != null) {
            int xT9LanguageId = aCLdbInfo.getXT9LanguageId();
            synchronized (this.supportedLangs) {
                hashMap = this.supportedLangs.get(Integer.valueOf(xT9LanguageId));
            }
            if (hashMap != null) {
                hashMap.remove(LANGUAGE_DL);
            }
            if (hashMap == null || !isPreinstalled(xT9LanguageId)) {
                addSupportedLanguage(Integer.valueOf(xT9LanguageId), generateSupportedHashMap(Integer.valueOf(aCLdbInfo.getXT9LanguageId()), true, aCLdbInfo.getVersion(), aCLdbInfo.getType()));
            } else if (Integer.decode(hashMap.get(VERSION)).intValue() < aCLdbInfo.getVersion()) {
                addSupportedLanguage(Integer.valueOf(xT9LanguageId), generateSupportedHashMap(Integer.valueOf(aCLdbInfo.getXT9LanguageId()), true, aCLdbInfo.getVersion(), aCLdbInfo.getType()));
            }
        }
    }

    private void addSupportedLanguage(Integer num, HashMap<String, String> hashMap) {
        int primaryLanguageId = this.languageService.getPrimaryLanguageId(num.intValue());
        synchronized (this.supportedLangs) {
            this.supportedLangs.put(Integer.valueOf(primaryLanguageId), hashMap);
            this.languageService.addSupportedLanguage(primaryLanguageId, isPreinstalled(primaryLanguageId));
        }
    }

    private void addSupportedLanguage(Integer num, ACLdbInfo[] aCLdbInfoArr) {
        boolean containsKey;
        int i;
        synchronized (this.supportedLangs) {
            containsKey = this.supportedLangs.containsKey(num);
        }
        if (containsKey || aCLdbInfoArr == null || aCLdbInfoArr.length <= 0) {
            return;
        }
        int i2 = -1;
        ACLanguageDownloadService.ACLdbType aCLdbType = ACLanguageDownloadService.ACLdbType.Unspecified;
        int length = aCLdbInfoArr.length;
        int i3 = 0;
        ACLanguageDownloadService.ACLdbType aCLdbType2 = aCLdbType;
        while (i3 < length) {
            ACLdbInfo aCLdbInfo = aCLdbInfoArr[i3];
            if (aCLdbInfo == null || aCLdbInfo.getXT9LanguageId() != num.intValue()) {
                i = i2;
            } else {
                ACLanguageDownloadService.ACLdbType type = aCLdbInfo.getType();
                i = i2 >= 0 ? Math.min(i2, aCLdbInfo.getVersion()) : aCLdbInfo.getVersion();
                aCLdbType2 = type;
            }
            i3++;
            i2 = i;
        }
        addSupportedLanguage(num, generateSupportedHashMap(num, true, i2, aCLdbType2));
    }

    private void fillExistingLanguage(int i) {
        boolean z = false;
        synchronized (this.languageDatabases) {
            if (this.languageDatabases.get(this.languageDatabases.indexOfKey(i)) == null) {
                this.languageDatabases.put(i, new ACLanguageDbInfoImpl(new LanguageService.LdbInfo(new int[]{i}, null, null, null, 0), this));
                z = true;
            }
        }
        if (z && isLanguageListAvailable()) {
            this.languageListCallback.languageListUpdate();
        }
    }

    private HashMap<String, String> generateSupportedHashMap(Integer num, boolean z, int i, ACLanguageDownloadService.ACLdbType aCLdbType) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(LANGUAGE_ID, num.toString());
        hashMap.put(PREINSTALLED, Boolean.toString(z));
        hashMap.put(VERSION, Integer.toString(i));
        hashMap.put(TYPE_INSTALLED, aCLdbType == null ? ACLanguageDownloadService.ACLdbType.Unspecified.name() : aCLdbType.name());
        hashMap.put(FLAVOR_INSTALLED, null);
        return hashMap;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String getInstalledFlavor(int i) {
        String str;
        synchronized (this.downloaded) {
            ACDownloadedLanguage aCDownloadedLanguage = this.downloaded.get(Integer.valueOf(i));
            if (aCDownloadedLanguage != null) {
                str = aCDownloadedLanguage.flavor;
            } else {
                synchronized (this.supportedLangs) {
                    str = this.supportedLangs.get(Integer.valueOf(i)) != null ? this.supportedLangs.get(Integer.valueOf(i)).get(FLAVOR_INSTALLED) : null;
                }
            }
        }
        return str;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public ACLanguageDownloadService.ACLdbType getInstalledLanguageModel(int i) {
        ACLanguageDownloadService.ACLdbType aCLdbType;
        synchronized (this.downloaded) {
            ACDownloadedLanguage aCDownloadedLanguage = this.downloaded.get(Integer.valueOf(i));
            if (aCDownloadedLanguage != null) {
                aCLdbType = ACLanguageDownloadService.ACLdbType.fromString(aCDownloadedLanguage.type);
            } else {
                synchronized (this.supportedLangs) {
                    HashMap<String, String> hashMap = this.supportedLangs.get(Integer.valueOf(i));
                    if (hashMap != null) {
                        aCLdbType = ACLanguageDownloadService.ACLdbType.fromString(hashMap.get(TYPE_INSTALLED));
                    } else {
                        log.w("Could not get language model for: ", Integer.valueOf(i));
                        aCLdbType = ACLanguageDownloadService.ACLdbType.Unspecified;
                    }
                }
            }
        }
        return aCLdbType;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isDownloaded(int i) {
        boolean containsKey;
        boolean z;
        synchronized (this.downloaded) {
            if (this.downloaded.get(Integer.valueOf(i)) == null) {
                return false;
            }
            synchronized (this.supportedLangs) {
                containsKey = this.supportedLangs.containsKey(Integer.valueOf(i));
            }
            synchronized (this.downloaded) {
                if (containsKey) {
                    if (this.downloaded.get(Integer.valueOf(i)).version != 0) {
                        z = true;
                    }
                }
                z = false;
            }
            return z;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isNewerVersionAvailable(int i, int i2) {
        HashMap<String, String> hashMap;
        int max;
        synchronized (this.supportedLangs) {
            hashMap = this.supportedLangs.get(Integer.valueOf(i));
        }
        if (hashMap == null) {
            return false;
        }
        synchronized (this.downloaded) {
            int i3 = this.downloaded.get(Integer.valueOf(i)) != null ? this.downloaded.get(Integer.valueOf(i)).version : 0;
            max = (hashMap.get(LANGUAGE_DL) == null || hashMap.get(LANGUAGE_DL).length() <= 0) ? (hashMap.get(VERSION) == null || hashMap.get(VERSION).length() <= 0) ? i3 : Math.max(i3, Integer.decode(hashMap.get(VERSION)).intValue()) : Math.max(i3, Integer.decode(hashMap.get(LANGUAGE_DL)).intValue());
        }
        return (isPreinstalled(i) || isDownloaded(i)) && i2 > max;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isPreinstalled(int i) {
        synchronized (this.supportedLangs) {
            if (!this.supportedLangs.containsKey(Integer.valueOf(i))) {
                return false;
            }
            return this.supportedLangs.get(Integer.valueOf(i)).get(PREINSTALLED).equals(Boolean.TRUE.toString());
        }
    }

    private boolean isSupported(int i) {
        boolean containsKey;
        synchronized (this.supportedLangs) {
            containsKey = this.supportedLangs.containsKey(Integer.valueOf(i));
        }
        return containsKey;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void save() {
        HashMap hashMap = new HashMap();
        synchronized (this.downloaded) {
            for (Map.Entry<Integer, ACDownloadedLanguage> entry : this.downloaded.entrySet()) {
                hashMap.put(entry.getKey(), entry.getValue().toJson());
            }
        }
        this.store.saveObject(DOWNLOADED_LANGUAGES, hashMap);
    }

    @Override // com.nuance.swypeconnect.ac.ACLanguageDownloadService
    public void addExistingLanguage(Integer num, String[] strArr) {
        if (num == null) {
            log.e("addExistingLanguage xt9LanguageId is null");
            return;
        }
        if (strArr != null) {
            ACLdbInfo[] aCLdbInfoArr = new ACLdbInfo[strArr.length];
            for (int i = 0; i < strArr.length; i++) {
                aCLdbInfoArr[i] = ACLdbInfo.load(strArr[i], this.manager.getContext());
            }
            addSupportedLanguage(num, aCLdbInfoArr);
            fillExistingLanguage(num.intValue());
        }
    }

    @Override // com.nuance.swypeconnect.ac.ACLanguageDownloadService
    public void addExistingLanguage(String str) {
        ACLdbInfo load = ACLdbInfo.load(str, this.manager.getContext());
        if (load != null) {
            addSupportedLanguage(load);
            fillExistingLanguage(load.getXT9LanguageId());
        }
    }

    @Override // com.nuance.swypeconnect.ac.ACLanguageDownloadService
    public void addSupportedLanguage(Integer num) {
        if (num == null) {
            log.e("addSupportedLanguage xt9LanguageId is null");
        } else {
            addSupportedLanguage(num, generateSupportedHashMap(num, false, -1, ACLanguageDownloadService.ACLdbType.Unspecified));
        }
    }

    @Override // com.nuance.swypeconnect.ac.ACLanguageDownloadService
    public void addSupportedLanguages(List<Integer> list) {
        if (list == null) {
            return;
        }
        Iterator it = new ArrayList(list).iterator();
        while (it.hasNext()) {
            Integer num = (Integer) it.next();
            if (num == null) {
                log.e("addSupportedLanguage xt9LanguageId is null");
            } else {
                addSupportedLanguage(num, generateSupportedHashMap(num, false, -1, ACLanguageDownloadService.ACLdbType.Unspecified));
            }
        }
        if (isLanguageListAvailable()) {
            this.languageService.notifyCallbacksOfStatus();
        }
    }

    @Override // com.nuance.swypeconnect.ac.ACLanguageDownloadService
    public void cancelDownload(int i) throws ACException {
        int primaryLanguageId = this.languageService.getPrimaryLanguageId(i);
        if (!isSupported(primaryLanguageId)) {
            throw new ACException(111, "The language you requested is not supported.");
        }
        try {
            this.languageService.cancelDownload(primaryLanguageId);
        } catch (ConnectException e) {
            log.e("Exception canceling download: ", e.getMessage());
        }
    }

    @Override // com.nuance.swypeconnect.ac.ACLanguageDownloadService
    public void downloadLanguage(int i, ACLanguageDownloadService.ACLanguageDownloadFileCallback aCLanguageDownloadFileCallback) throws ACException {
        ACLanguageDbInfoImpl aCLanguageDbInfoImpl;
        log.d("downloadLanguage id=", Integer.valueOf(i), " callback=", aCLanguageDownloadFileCallback);
        isAuthorized(0);
        int primaryLanguageId = this.languageService.getPrimaryLanguageId(i);
        if (!isSupported(primaryLanguageId)) {
            throw new ACException(111, "The language you requested is not supported.");
        }
        if (aCLanguageDownloadFileCallback == null) {
            throw new ACException(128, "callback cannot be null");
        }
        synchronized (this.languageDatabases) {
            aCLanguageDbInfoImpl = this.languageDatabases.get(primaryLanguageId);
        }
        if (aCLanguageDbInfoImpl == null) {
            throw new ACException(110, "The language you requested is not available: " + i);
        }
        try {
            customer.d("Language download requested");
            this.languageService.downloadLanguage(primaryLanguageId, new LanguageDownloadCallbackAdapter(primaryLanguageId, aCLanguageDbInfoImpl.getVersion(), ((aCLanguageDbInfoImpl.getTypes() == null || aCLanguageDbInfoImpl.getTypes().length <= 0) ? ACLanguageDownloadService.ACLdbType.Unspecified : aCLanguageDbInfoImpl.getTypes()[0]).name(), null, aCLanguageDownloadFileCallback));
        } catch (ConnectException e) {
            throw new ACException(110, "The language you requested is not available: " + e.getMessage());
        }
    }

    @Override // com.nuance.swypeconnect.ac.ACLanguageDownloadService
    public void downloadLanguage(int i, ACLanguageDownloadService.ACLdbType aCLdbType, ACLanguageDownloadService.ACLanguageDownloadFileCallback aCLanguageDownloadFileCallback) throws ACException {
        ACLanguageDbInfoImpl aCLanguageDbInfoImpl;
        boolean z;
        isAuthorized(0);
        if (aCLdbType == null) {
            throw new ACException(111, "Language type cannot be null.");
        }
        log.d("downloadLanguage id=", Integer.valueOf(i), " type=", aCLdbType, " callback=", aCLanguageDownloadFileCallback);
        int primaryLanguageId = this.languageService.getPrimaryLanguageId(i);
        if (!isSupported(primaryLanguageId)) {
            throw new ACException(111, "The language " + i + " you requested is not supported.");
        }
        if (aCLanguageDownloadFileCallback == null) {
            throw new ACException(128, "callback cannot be null");
        }
        synchronized (this.languageDatabases) {
            aCLanguageDbInfoImpl = this.languageDatabases.get(primaryLanguageId);
        }
        if (aCLanguageDbInfoImpl == null) {
            throw new ACException(110, "The language you requested is not available: " + i);
        }
        ACLanguageDownloadService.ACLdbType[] types = aCLanguageDbInfoImpl.getTypes();
        if (types == null) {
            throw new ACException(110, "the language type " + aCLdbType + " you requested is not available");
        }
        int length = types.length;
        int i2 = 0;
        while (true) {
            if (i2 >= length) {
                z = false;
                break;
            } else {
                if (types[i2] == aCLdbType) {
                    z = true;
                    break;
                }
                i2++;
            }
        }
        if (!z) {
            throw new ACException(110, "the language flavor " + aCLdbType + " you requested is not available");
        }
        try {
            customer.d("Language download requested");
            this.languageService.downloadLanguage(primaryLanguageId, aCLanguageDbInfoImpl.getFlavorNameForType(aCLdbType), new LanguageDownloadCallbackAdapter(primaryLanguageId, aCLanguageDbInfoImpl.getVersion(), aCLdbType.name(), null, aCLanguageDownloadFileCallback));
        } catch (ConnectException e) {
            throw new ACException(110, "The language you requested is not available: " + e.getMessage());
        }
    }

    @Override // com.nuance.swypeconnect.ac.ACLanguageDownloadService
    public void downloadLanguageFlavor(int i, String str, ACLanguageDownloadService.ACLanguageDownloadFileCallback aCLanguageDownloadFileCallback) throws ACException {
        ACLanguageDbInfoImpl aCLanguageDbInfoImpl;
        boolean z;
        isAuthorized(0);
        if (str == null) {
            throw new ACException(111, "Language flavor cannot be null.");
        }
        if (aCLanguageDownloadFileCallback == null) {
            throw new ACException(128, "callback cannot be null");
        }
        log.d("downloadLanguageFlavor id=", Integer.valueOf(i), " flavor=", str, " callback=", aCLanguageDownloadFileCallback);
        int primaryLanguageId = this.languageService.getPrimaryLanguageId(i);
        if (!isSupported(primaryLanguageId)) {
            throw new ACException(111, "The language " + i + " you requested is not supported.");
        }
        synchronized (this.languageDatabases) {
            aCLanguageDbInfoImpl = this.languageDatabases.get(primaryLanguageId);
        }
        if (aCLanguageDbInfoImpl == null) {
            throw new ACException(110, "The language you requested is not available: " + i);
        }
        String[] flavors = aCLanguageDbInfoImpl.getFlavors();
        if (flavors == null) {
            throw new ACException(110, "the language flavor " + str + " you requested is not available");
        }
        int length = flavors.length;
        int i2 = 0;
        while (true) {
            if (i2 >= length) {
                z = false;
                break;
            } else {
                if (str.equals(flavors[i2])) {
                    z = true;
                    break;
                }
                i2++;
            }
        }
        if (!z) {
            throw new ACException(110, "the language flavor " + str + " you requested is not available");
        }
        try {
            customer.d("Language download requested");
            this.languageService.downloadLanguage(primaryLanguageId, str, new LanguageDownloadCallbackAdapter(primaryLanguageId, aCLanguageDbInfoImpl.getVersion(), ((aCLanguageDbInfoImpl.getTypes() == null || aCLanguageDbInfoImpl.getTypes().length <= 0) ? ACLanguageDownloadService.ACLdbType.Unspecified : aCLanguageDbInfoImpl.getTypes()[0]).name(), str, aCLanguageDownloadFileCallback));
        } catch (ConnectException e) {
            throw new ACException(110, "The language you requested is not available: " + e.getMessage());
        }
    }

    @Override // com.nuance.swypeconnect.ac.ACLanguageDownloadService
    public ACLanguageDownloadService.ACLanguageDbInfo getDatabase(int i) throws ACException {
        ACLanguageDbInfoImpl aCLanguageDbInfoImpl;
        int primaryLanguageId = this.languageService.getPrimaryLanguageId(i);
        if (!isSupported(primaryLanguageId)) {
            throw new ACException(111, "The language " + i + " you requested is not supported.");
        }
        synchronized (this.languageDatabases) {
            aCLanguageDbInfoImpl = this.languageDatabases.get(primaryLanguageId);
            if (aCLanguageDbInfoImpl == null) {
                throw new ACException(110, "The language you requested is not available: " + i);
            }
        }
        return aCLanguageDbInfoImpl;
    }

    @Override // com.nuance.swypeconnect.ac.ACLanguageDownloadService
    public List<ACLanguageDownloadService.ACLanguageDbInfo> getDatabaseList() {
        ArrayList arrayList = new ArrayList();
        synchronized (this.languageDatabases) {
            for (int i = 0; i < this.languageDatabases.size(); i++) {
                ACLanguageDbInfoImpl aCLanguageDbInfoImpl = this.languageDatabases.get(this.languageDatabases.keyAt(i));
                if (isSupported(aCLanguageDbInfoImpl.getXt9LanguageId())) {
                    arrayList.add(aCLanguageDbInfoImpl);
                }
            }
        }
        return arrayList;
    }

    @Override // com.nuance.swypeconnect.ac.ACService
    public String getName() {
        return ACManager.LANGUAGE_SERVICE;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Multi-variable type inference failed */
    public void init(LanguageService languageService, PersistentDataStore persistentDataStore, ACManager aCManager) {
        HashMap hashMap;
        this.store = persistentDataStore;
        this.manager = aCManager;
        if (!persistentDataStore.exists(DOWNLOADED_LANGUAGES) && persistentDataStore.exists(MIGRATING_DOWNLOADED_LANGUAGES)) {
            HashMap hashMap2 = (HashMap) persistentDataStore.readObject(MIGRATING_DOWNLOADED_LANGUAGES);
            if (hashMap2 != null) {
                synchronized (this.downloaded) {
                    for (Map.Entry entry : hashMap2.entrySet()) {
                        this.downloaded.put(entry.getKey(), new ACDownloadedLanguage(((Integer) entry.getKey()).intValue(), ((Integer) entry.getValue()).intValue(), ACLanguageDownloadService.ACLdbType.ALM.toString(), null));
                    }
                }
            }
            persistentDataStore.delete(MIGRATING_DOWNLOADED_LANGUAGES);
        } else if (persistentDataStore.exists(DOWNLOADED_LANGUAGES) && (hashMap = (HashMap) persistentDataStore.readObject(DOWNLOADED_LANGUAGES)) != null) {
            synchronized (this.downloaded) {
                for (Map.Entry entry2 : hashMap.entrySet()) {
                    ACDownloadedLanguage fromJson = ACDownloadedLanguage.fromJson((String) entry2.getValue());
                    if (fromJson != null) {
                        this.downloaded.put(entry2.getKey(), fromJson);
                    }
                }
            }
        }
        languageService.registerCallback(this.languageListCallback);
        this.languageService = languageService;
    }

    public void isAuthorized(int i) throws ACException {
        if (requiresDocument(1, i)) {
            log.e("Attempting to download a language before TOS has been accepted.");
            throw new ACException(104, "Please Accept the TOS before requesting " + getName() + ".");
        }
    }

    @Override // com.nuance.swypeconnect.ac.ACLanguageDownloadService
    public boolean isLanguageListAvailable() {
        return this.languageService.isLanguageListAvailable();
    }

    @Override // com.nuance.swypeconnect.ac.ACLanguageDownloadService
    public void languageUninstalled(int i) throws ACException {
        int primaryLanguageId = this.languageService.getPrimaryLanguageId(i);
        boolean z = false;
        synchronized (this.supportedLangs) {
            if (this.supportedLangs.containsKey(Integer.valueOf(primaryLanguageId))) {
                this.supportedLangs.get(Integer.valueOf(primaryLanguageId)).remove(LANGUAGE_DL);
                z = true;
            } else if (this.supportedLangs.containsKey(Integer.valueOf(i))) {
                this.supportedLangs.get(Integer.valueOf(i)).remove(LANGUAGE_DL);
                z = true;
            }
        }
        if (!z) {
            throw new ACException(111, "The language you requested is not supported.");
        }
        synchronized (this.downloaded) {
            this.downloaded.remove(Integer.valueOf(primaryLanguageId));
        }
        save();
        try {
            this.languageService.languageUninstalled(primaryLanguageId);
        } catch (ConnectException e) {
        }
    }

    @Override // com.nuance.swypeconnect.ac.ACLanguageDownloadService
    public void registerCallback(ACLanguageDownloadService.ACLanguageListCallback aCLanguageListCallback) {
        this.languageListCallbacks.add(aCLanguageListCallback);
    }

    @Override // com.nuance.swypeconnect.ac.ACLanguageDownloadService
    public void removeSupportedLanguage(Integer num) {
        HashMap<String, String> remove;
        if (num == null) {
            log.e("removeSupportedLanguage xt9LanguageId is null");
            return;
        }
        int primaryLanguageId = this.languageService.getPrimaryLanguageId(num.intValue());
        synchronized (this.supportedLangs) {
            remove = this.supportedLangs.containsKey(Integer.valueOf(primaryLanguageId)) ? this.supportedLangs.remove(Integer.valueOf(primaryLanguageId)) : this.supportedLangs.remove(num);
        }
        if (remove != null) {
            synchronized (this.downloaded) {
                this.downloaded.remove(Integer.valueOf(primaryLanguageId));
            }
            save();
            try {
                this.languageService.cancelDownload(primaryLanguageId);
            } catch (ConnectException e) {
                log.e("Error removeSupportedLanguage: ", num, " message: ", e.getMessage());
            }
            this.languageService.notifyCallbacksOfStatus();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.swypeconnect.ac.ACService
    public boolean requireInitialization() {
        return true;
    }

    @Override // com.nuance.swypeconnect.ac.ACService
    public boolean requiresDocument(int i) {
        return requiresDocument(i, 1);
    }

    public boolean requiresDocument(int i, int i2) {
        switch (i2) {
            case 0:
                if (i == 1) {
                    return this.manager.getLegalDocuments() == null || !this.manager.getLegalDocuments().userHasAcceptedDocumentByType(1);
                }
                return false;
            case 1:
            default:
                return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.swypeconnect.ac.ACService
    public void shutdown() {
        this.bInitialized = false;
        this.isShutdown = true;
        unregisterCallbacks();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.swypeconnect.ac.ACService
    public void start() {
        this.isShutdown = false;
        this.bInitialized = false;
        this.languageListCallback.languageListUpdate();
    }

    @Override // com.nuance.swypeconnect.ac.ACLanguageDownloadService
    public void unregisterCallback(ACLanguageDownloadService.ACLanguageListCallback aCLanguageListCallback) {
        this.languageListCallbacks.remove(aCLanguageListCallback);
    }

    @Override // com.nuance.swypeconnect.ac.ACLanguageDownloadService
    public void unregisterCallbacks() {
        this.languageListCallbacks.clear();
    }
}
