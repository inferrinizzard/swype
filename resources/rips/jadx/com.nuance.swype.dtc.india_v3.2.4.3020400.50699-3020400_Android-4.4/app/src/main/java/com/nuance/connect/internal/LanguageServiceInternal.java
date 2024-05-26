package com.nuance.connect.internal;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Pair;
import com.nuance.connect.api.ConnectException;
import com.nuance.connect.api.LanguageService;
import com.nuance.connect.comm.MessageAPI;
import com.nuance.connect.common.ConnectFeature;
import com.nuance.connect.common.Integers;
import com.nuance.connect.common.Strings;
import com.nuance.connect.internal.common.APIHandlers;
import com.nuance.connect.internal.common.InternalMessages;
import com.nuance.connect.util.ConcurrentCallbackSet;
import com.nuance.connect.util.Logger;
import com.nuance.connect.util.StringUtils;
import com.nuance.connect.util.WeakReferenceHandler;
import com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.xmlResults.XMLResultsHandler;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/* loaded from: classes.dex */
public class LanguageServiceInternal extends AbstractService implements LanguageService {
    private static final InternalMessages[] MESSAGE_IDS = {InternalMessages.MESSAGE_HOST_SET_LANGUAGES_STATUS, InternalMessages.MESSAGE_HOST_LANGUAGE_INSTALL_READY, InternalMessages.MESSAGE_HOST_LANGUAGE_DOWNLOAD_PROGRESS, InternalMessages.MESSAGE_HOST_LANGUAGE_UNINSTALL, InternalMessages.MESSAGE_HOST_LANGUAGE_DOWNLOAD_FAILED, InternalMessages.MESSAGE_HOST_LANGUAGE_DOWNLOAD_CANCEL_ACK};
    private static final int SEND_PREINSTALL_LIST = 0;
    private final ConnectServiceManagerInternal connectService;
    private boolean languageListNotified;
    private boolean shouldSend;
    private final Logger.Log log = Logger.getLog(Logger.LoggerType.DEVELOPER, getClass().getSimpleName());
    private final Logger.Log oemLog = Logger.getLog(Logger.LoggerType.OEM);
    private final HashMap<String, HashMap<String, String>> languageMetadata = new HashMap<>();
    private final HashMap<Integer, String> languageIdMap = new HashMap<>();
    private final Set<String> canceledDownloads = new HashSet();
    private final Map<String, LanguageService.DownloadCallback> redownload = new HashMap();
    private final ConcurrentCallbackSet<LanguageService.ListCallback> languageCallbacks = new ConcurrentCallbackSet<>();
    private final Map<Integer, Pair<String, LanguageService.DownloadCallback>> downloadingCallbacks = new HashMap();
    private final TreeSet<String> existingLanguages = new TreeSet<>();
    private final TreeSet<String> supportedLanguages = new TreeSet<>();
    private final Handler.Callback handlerCallback = new Handler.Callback() { // from class: com.nuance.connect.internal.LanguageServiceInternal.1
        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message message) {
            if (message.what != 0) {
                return false;
            }
            String implode = StringUtils.implode(LanguageServiceInternal.this.existingLanguages, ",");
            String implode2 = StringUtils.implode(LanguageServiceInternal.this.supportedLanguages, ",");
            Bundle bundle = new Bundle();
            bundle.putString(Strings.DEFAULT_KEY, implode);
            bundle.putString(Strings.PROP_LANGUAGE, implode2);
            LanguageServiceInternal.this.connectService.getBinder().sendConnectMessage(InternalMessages.MESSAGE_CLIENT_INSTALLED_LANGUAGES, bundle);
            return true;
        }
    };
    private final Handler weakReferenceHandler = WeakReferenceHandler.create(this.handlerCallback);
    private final ConnectHandler handler = new ConnectHandler() { // from class: com.nuance.connect.internal.LanguageServiceInternal.2
        @Override // com.nuance.connect.internal.ConnectHandler
        public String getHandlerName() {
            return APIHandlers.LANGUAGE_DL_HANDLER;
        }

        @Override // com.nuance.connect.internal.ConnectHandler
        public int[] getMessageIDs() {
            int[] iArr = new int[LanguageServiceInternal.MESSAGE_IDS.length];
            for (int i = 0; i < LanguageServiceInternal.MESSAGE_IDS.length; i++) {
                iArr[i] = LanguageServiceInternal.MESSAGE_IDS[i].ordinal();
            }
            return iArr;
        }

        @Override // com.nuance.connect.internal.ConnectHandler
        public void handleMessage(Handler handler, Message message) {
            switch (AnonymousClass3.$SwitchMap$com$nuance$connect$internal$common$InternalMessages[InternalMessages.fromInt(message.what).ordinal()]) {
                case 1:
                    LanguageServiceInternal.this.log.d("MESSAGE_HOST_SET_LANGUAGES_STATUS");
                    LanguageServiceInternal.this.updateLanguagesData((HashMap) message.getData().getSerializable(Strings.DEFAULT_KEY));
                    return;
                case 2:
                    LanguageServiceInternal.this.log.d("MESSAGE_HOST_LANGUAGE_INSTALL_READY");
                    try {
                        Bundle data = message.getData();
                        String string = data.getString(Strings.DEFAULT_KEY);
                        String string2 = data.getString(Strings.MESSAGE_BUNDLE_FILEPATH);
                        int findLanguageIdFromFlavorByName = LanguageServiceInternal.this.findLanguageIdFromFlavorByName(string);
                        if (LanguageServiceInternal.this.canceledDownloads.contains(string) || !LanguageServiceInternal.this.downloadingCallbacks.containsKey(Integer.valueOf(findLanguageIdFromFlavorByName))) {
                            return;
                        }
                        LanguageService.DownloadCallback downloadCallback = (LanguageService.DownloadCallback) ((Pair) LanguageServiceInternal.this.downloadingCallbacks.get(Integer.valueOf(findLanguageIdFromFlavorByName))).second;
                        LanguageServiceInternal.this.downloadingCallbacks.remove(Integer.valueOf(findLanguageIdFromFlavorByName));
                        if (string2 == null || !downloadCallback.downloadComplete(new File(string2))) {
                            throw new Exception("(Internal) Not successful");
                        }
                        LanguageServiceInternal.this.markLanguageInstalled(string, downloadCallback.getVersion());
                        LanguageServiceInternal.this.connectService.getBinder().sendConnectMessage(InternalMessages.MESSAGE_CLIENT_LANGUAGE_INSTALLED, string, Integers.STATUS_SUCCESS, downloadCallback.getVersion());
                        LanguageServiceInternal.this.notifyCallbacksOfStatus();
                        return;
                    } catch (Exception e) {
                        LanguageServiceInternal.this.log.e("Exception Installing: lang=", null, " message=", e.getMessage());
                        LanguageServiceInternal.this.connectService.getBinder().sendConnectMessage(InternalMessages.MESSAGE_CLIENT_LANGUAGE_INSTALLED, null, 0, Integers.STATUS_SUCCESS);
                        if (LanguageServiceInternal.this.downloadingCallbacks.containsKey(-1)) {
                            LanguageService.DownloadCallback downloadCallback2 = (LanguageService.DownloadCallback) ((Pair) LanguageServiceInternal.this.downloadingCallbacks.get(-1)).second;
                            LanguageServiceInternal.this.downloadingCallbacks.remove(-1);
                            downloadCallback2.downloadFailed(2);
                            return;
                        }
                        return;
                    }
                case 3:
                    Bundle data2 = message.getData();
                    String string3 = data2.getString(Strings.PROP_LANGUAGE);
                    int findLanguageIdFromFlavorByName2 = LanguageServiceInternal.this.findLanguageIdFromFlavorByName(string3);
                    String string4 = data2.getString(Strings.PROP_DOWNLOAD_PERCENT);
                    int intValue = string4 != null ? Integer.decode(string4).intValue() : 0;
                    if (LanguageServiceInternal.this.canceledDownloads.contains(string3) || !LanguageServiceInternal.this.downloadingCallbacks.containsKey(Integer.valueOf(findLanguageIdFromFlavorByName2))) {
                        return;
                    }
                    ((LanguageService.DownloadCallback) ((Pair) LanguageServiceInternal.this.downloadingCallbacks.get(Integer.valueOf(findLanguageIdFromFlavorByName2))).second).downloadPercentage(intValue);
                    return;
                case 4:
                    LanguageServiceInternal.this.removeLanguage(message.getData().getString(Strings.DEFAULT_KEY));
                    return;
                case 5:
                    Bundle data3 = message.getData();
                    String string5 = data3.getString(Strings.PROP_LANGUAGE);
                    int findLanguageIdFromFlavorByName3 = LanguageServiceInternal.this.findLanguageIdFromFlavorByName(string5);
                    LanguageServiceInternal.this.log.d("MESSAGE_HOST_LANGUAGE_DOWNLOAD_FAILED: ", string5);
                    if (LanguageServiceInternal.this.downloadingCallbacks.containsKey(Integer.valueOf(findLanguageIdFromFlavorByName3))) {
                        int i = data3.getInt(Strings.DEFAULT_KEY);
                        LanguageService.DownloadCallback downloadCallback3 = (LanguageService.DownloadCallback) ((Pair) LanguageServiceInternal.this.downloadingCallbacks.get(Integer.valueOf(findLanguageIdFromFlavorByName3))).second;
                        LanguageServiceInternal.this.downloadingCallbacks.remove(Integer.valueOf(findLanguageIdFromFlavorByName3));
                        downloadCallback3.downloadFailed(i);
                        return;
                    }
                    return;
                case 6:
                    String string6 = message.getData().getString(Strings.DEFAULT_KEY);
                    int findLanguageIdFromFlavorByName4 = LanguageServiceInternal.this.findLanguageIdFromFlavorByName(string6);
                    LanguageServiceInternal.this.canceledDownloads.remove(string6);
                    LanguageServiceInternal.this.log.d("MESSAGE_HOST_LANGUAGE_DOWNLOAD_CANCEL_ACK: ", string6);
                    if (LanguageServiceInternal.this.downloadingCallbacks.containsKey(Integer.valueOf(findLanguageIdFromFlavorByName4))) {
                        LanguageService.DownloadCallback downloadCallback4 = (LanguageService.DownloadCallback) ((Pair) LanguageServiceInternal.this.downloadingCallbacks.get(Integer.valueOf(findLanguageIdFromFlavorByName4))).second;
                        LanguageServiceInternal.this.downloadingCallbacks.remove(Integer.valueOf(findLanguageIdFromFlavorByName4));
                        downloadCallback4.downloadStopped(3);
                    }
                    if (LanguageServiceInternal.this.redownload.containsKey(string6)) {
                        LanguageServiceInternal.this.log.e("redownload was requested for canceled language, redownloading: ", string6);
                        try {
                            LanguageServiceInternal.this.downloadLanguage(findLanguageIdFromFlavorByName4, (LanguageService.DownloadCallback) LanguageServiceInternal.this.redownload.get(string6));
                            LanguageServiceInternal.this.redownload.remove(string6);
                            return;
                        } catch (ConnectException e2) {
                            LanguageServiceInternal.this.log.e("Error Attempting to Redownload with message: ", e2.getMessage());
                            return;
                        }
                    }
                    return;
                default:
                    return;
            }
        }

        @Override // com.nuance.connect.internal.ConnectHandler
        public void onPostUpgrade() {
        }
    };

    /* renamed from: com.nuance.connect.internal.LanguageServiceInternal$3, reason: invalid class name */
    /* loaded from: classes.dex */
    static /* synthetic */ class AnonymousClass3 {
        static final /* synthetic */ int[] $SwitchMap$com$nuance$connect$internal$common$InternalMessages = new int[InternalMessages.values().length];

        static {
            try {
                $SwitchMap$com$nuance$connect$internal$common$InternalMessages[InternalMessages.MESSAGE_HOST_SET_LANGUAGES_STATUS.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$nuance$connect$internal$common$InternalMessages[InternalMessages.MESSAGE_HOST_LANGUAGE_INSTALL_READY.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$nuance$connect$internal$common$InternalMessages[InternalMessages.MESSAGE_HOST_LANGUAGE_DOWNLOAD_PROGRESS.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$nuance$connect$internal$common$InternalMessages[InternalMessages.MESSAGE_HOST_LANGUAGE_UNINSTALL.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$nuance$connect$internal$common$InternalMessages[InternalMessages.MESSAGE_HOST_LANGUAGE_DOWNLOAD_FAILED.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$com$nuance$connect$internal$common$InternalMessages[InternalMessages.MESSAGE_HOST_LANGUAGE_DOWNLOAD_CANCEL_ACK.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public LanguageServiceInternal(ConnectServiceManagerInternal connectServiceManagerInternal) {
        this.connectService = connectServiceManagerInternal;
    }

    private void cancelDownload(String str) {
        if (str == null) {
            this.oemLog.e("cancel download, invalid language: null");
            return;
        }
        if (this.redownload.containsKey(str) && this.canceledDownloads.contains(str)) {
            this.redownload.remove(str);
            return;
        }
        this.canceledDownloads.add(str);
        this.connectService.getBinder().sendConnectMessage(InternalMessages.MESSAGE_CLIENT_LANGUAGE_DOWNLOAD_CANCEL, str, 0, 0);
        synchronized (this.languageMetadata) {
            if (this.languageMetadata.containsKey(str)) {
                this.languageMetadata.get(str).put(Strings.MAP_KEY_STEP, "8");
            }
        }
    }

    private String findLanguageById(int i, String str) {
        String findLanguageById = findLanguageById(i);
        if (findLanguageById == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(findLanguageById);
        if (str != null) {
            sb.append(XMLResultsHandler.SEP_HYPHEN);
            sb.append(str);
        }
        return sb.toString();
    }

    private String findLanguageFlavorFromName(String str) {
        return str.split(XMLResultsHandler.SEP_HYPHEN)[1];
    }

    private int findLanguageIdByName(String str) {
        int i;
        int[] languageIds;
        synchronized (this.languageMetadata) {
            HashMap<String, String> hashMap = this.languageMetadata.get(str);
            i = (hashMap == null || (languageIds = getLanguageIds(hashMap.get(MessageAPI.LANGUAGE_IDS))) == null || languageIds.length <= 0) ? -1 : languageIds[0];
        }
        return i;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int findLanguageIdFromFlavorByName(String str) {
        if (str == null) {
            str = "";
        }
        return findLanguageIdByName(str.split(XMLResultsHandler.SEP_HYPHEN)[0]);
    }

    private int[] getLanguageIds(String str) {
        if (str != null) {
            try {
                if (!str.isEmpty()) {
                    String[] split = str.split(",");
                    int[] iArr = new int[split.length];
                    for (int i = 0; i < split.length; i++) {
                        iArr[i] = Integer.parseInt(split[i]);
                    }
                    return iArr;
                }
            } catch (NumberFormatException e) {
                this.log.d(e.getMessage());
            }
        }
        return null;
    }

    private boolean hasFlavors(String str) {
        return str.contains(XMLResultsHandler.SEP_HYPHEN);
    }

    private void installLanguage(String str) {
        synchronized (this.languageMetadata) {
            if (this.languageMetadata.containsKey(str)) {
                this.connectService.getBinder().sendConnectMessage(InternalMessages.MESSAGE_CLIENT_LANGUAGE_INSTALL, str, 0, 0);
            } else {
                this.oemLog.e("Attempt to install a language that isn't available");
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void markLanguageInstalled(String str, int i) {
        synchronized (this.languageMetadata) {
            if (this.languageMetadata.containsKey(str)) {
                this.languageMetadata.get(str).put(Strings.MAP_KEY_STEP, MessageAPI.MESSAGE);
                this.languageMetadata.get(str).put(Strings.PROP_INSTALLEDVERSION, String.valueOf(i));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeLanguage(String str) {
        synchronized (this.languageMetadata) {
            if (this.languageMetadata.containsKey(str)) {
                this.languageMetadata.get(str).put(Strings.MAP_KEY_STEP, "0");
                this.connectService.getBinder().sendConnectMessage(InternalMessages.MESSAGE_CLIENT_LANGUAGE_UNINSTALL, str, 0, 0);
            } else {
                this.oemLog.e("Attempt to uninstall a language that isn't available");
            }
        }
        notifyCallbacksOfStatus();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateLanguagesData(Map<String, HashMap<String, String>> map) {
        boolean z;
        if (map == null || map.isEmpty()) {
            this.log.e("updateLanguagesData contains no metadata");
            return;
        }
        HashMap hashMap = new HashMap();
        synchronized (this.languageMetadata) {
            z = false;
            for (Map.Entry<String, HashMap<String, String>> entry : map.entrySet()) {
                String key = entry.getKey();
                HashMap<String, String> value = entry.getValue();
                if (this.canceledDownloads.contains(key)) {
                    value.put(Strings.MAP_KEY_STEP, "8");
                }
                HashMap<String, String> hashMap2 = this.languageMetadata.get(key);
                boolean z2 = (hashMap2 == null || value.get(Strings.PROP_VERSION) == null || hashMap2.get(Strings.PROP_VERSION) == null || value.get(Strings.PROP_VERSION).equals(hashMap2.get(Strings.PROP_VERSION))) ? hashMap2 == null ? true : z : true;
                int[] languageIds = getLanguageIds(entry.getValue().get(MessageAPI.LANGUAGE_IDS));
                if (languageIds != null) {
                    for (int i : languageIds) {
                        hashMap.put(Integer.valueOf(i), key.split(XMLResultsHandler.SEP_HYPHEN)[0]);
                    }
                }
                this.languageMetadata.put(entry.getKey(), entry.getValue());
                z = z2;
            }
        }
        synchronized (this.languageIdMap) {
            this.languageIdMap.clear();
            this.languageIdMap.putAll(hashMap);
        }
        if (!this.languageListNotified || z) {
            this.languageListNotified = true;
            notifyCallbacksOfStatus();
        }
    }

    @Override // com.nuance.connect.api.LanguageService
    public void addSupportedLanguage(int i, boolean z) {
        boolean z2 = true;
        String findLanguageById = findLanguageById(i);
        if (findLanguageById == null) {
            return;
        }
        if (z) {
            if (!this.shouldSend && this.existingLanguages.contains(findLanguageById)) {
                z2 = false;
            }
            this.shouldSend = z2;
            this.existingLanguages.add(findLanguageById);
        } else {
            if (!this.shouldSend && this.supportedLanguages.contains(findLanguageById)) {
                z2 = false;
            }
            this.shouldSend = z2;
            this.supportedLanguages.add(findLanguageById);
        }
        if (this.shouldSend) {
            this.weakReferenceHandler.removeMessages(0);
            this.weakReferenceHandler.sendEmptyMessageDelayed(0, 1000L);
        }
    }

    @Override // com.nuance.connect.api.LanguageService
    public void cancelDownload(int i) throws ConnectException {
        Pair<String, LanguageService.DownloadCallback> pair = this.downloadingCallbacks.get(Integer.valueOf(i));
        if (pair != null) {
            cancelDownload((String) pair.first);
        } else {
            this.oemLog.w("Attempting to cancel a download on a language that is currently not being processed. Language: ", Integer.valueOf(i));
        }
    }

    @Override // com.nuance.connect.api.LanguageService
    public void cancelDownload(int i, String str) throws ConnectException {
        throw new ConnectException(0, "Method is currently unimplemented, implement prior to release.");
    }

    @Override // com.nuance.connect.api.LanguageService
    public void downloadLanguage(int i, LanguageService.DownloadCallback downloadCallback) throws ConnectException {
        String findLanguageById = findLanguageById(i);
        this.oemLog.d("downloadLanguage id=", Integer.valueOf(i), " lang=", findLanguageById, " callback=", downloadCallback);
        synchronized (this.languageMetadata) {
            if (!this.languageMetadata.containsKey(findLanguageById)) {
                throw new ConnectException(110);
            }
        }
        if (this.canceledDownloads.contains(findLanguageById)) {
            this.log.d("Language currently being canceled, putting into queue once verified.");
            this.redownload.put(findLanguageById, downloadCallback);
        } else {
            this.downloadingCallbacks.put(Integer.valueOf(i), new Pair<>(findLanguageById, downloadCallback));
            installLanguage(findLanguageById);
        }
    }

    @Override // com.nuance.connect.api.LanguageService
    public void downloadLanguage(int i, String str, LanguageService.DownloadCallback downloadCallback) throws ConnectException {
        String findLanguageById = findLanguageById(i, str);
        this.oemLog.d("downloadLanguage id=", Integer.valueOf(i), " flavor=", str, " lang=", findLanguageById, " callback=", downloadCallback);
        synchronized (this.languageMetadata) {
            if (!this.languageMetadata.containsKey(findLanguageById)) {
                throw new ConnectException(110);
            }
        }
        if (this.canceledDownloads.contains(findLanguageById)) {
            this.log.d("Language currently being canceled, putting into queue once verified.");
            this.redownload.put(findLanguageById, downloadCallback);
        } else {
            this.downloadingCallbacks.put(Integer.valueOf(i), new Pair<>(findLanguageById, downloadCallback));
            installLanguage(findLanguageById);
        }
    }

    public String findLanguageById(int i) {
        String str;
        synchronized (this.languageIdMap) {
            str = this.languageIdMap.get(Integer.valueOf(i));
        }
        return str;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.connect.internal.AbstractService
    public ConnectFeature[] getDependencies() {
        return ConnectFeature.LANGUAGE.values();
    }

    @Override // com.nuance.connect.api.LanguageService
    public List<LanguageService.LdbInfo> getDownloadLdbList() {
        int findLanguageIdByName;
        ArrayList arrayList = new ArrayList();
        synchronized (this.languageMetadata) {
            for (Map.Entry<String, HashMap<String, String>> entry : this.languageMetadata.entrySet()) {
                String str = null;
                if (hasFlavors(entry.getKey())) {
                    int findLanguageIdFromFlavorByName = findLanguageIdFromFlavorByName(entry.getKey());
                    str = findLanguageFlavorFromName(entry.getKey());
                    findLanguageIdByName = findLanguageIdFromFlavorByName;
                } else {
                    findLanguageIdByName = findLanguageIdByName(entry.getKey());
                }
                if (findLanguageIdByName > 0) {
                    int i = 0;
                    HashMap<String, String> value = entry.getValue();
                    try {
                        i = Integer.decode(value.get(Strings.PROP_VERSION)).intValue();
                    } catch (NumberFormatException e) {
                    }
                    arrayList.add(new LanguageService.LdbInfo(getLanguageIds(value.get(MessageAPI.LANGUAGE_IDS)), str, value.get(MessageAPI.NAME_TRANSLATED), value.get(MessageAPI.COUNTRY), i));
                }
            }
        }
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.connect.internal.AbstractService
    public ConnectHandler[] getHandlers() {
        return new ConnectHandler[]{this.handler};
    }

    @Override // com.nuance.connect.api.LanguageService
    public int getPrimaryLanguageId(int i) {
        String findLanguageById = findLanguageById(i);
        return findLanguageById == null ? i : findLanguageIdByName(findLanguageById);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.connect.internal.AbstractService
    public String getServiceName() {
        return ConnectFeature.LANGUAGE.name();
    }

    @Override // com.nuance.connect.api.LanguageService
    public boolean isLanguageListAvailable() {
        return this.languageListNotified;
    }

    @Override // com.nuance.connect.api.LanguageService
    public void languageUninstalled(int i) throws ConnectException {
        String findLanguageById = findLanguageById(i);
        LanguageService.DownloadCallback downloadCallback = null;
        if (this.downloadingCallbacks.containsKey(Integer.valueOf(i))) {
            downloadCallback = (LanguageService.DownloadCallback) this.downloadingCallbacks.get(Integer.valueOf(i)).second;
            findLanguageById = (String) this.downloadingCallbacks.get(Integer.valueOf(i)).first;
            this.downloadingCallbacks.remove(Integer.valueOf(i));
        }
        removeLanguage(findLanguageById);
        if (downloadCallback != null) {
            downloadCallback.downloadStopped(3);
        }
    }

    @Override // com.nuance.connect.api.LanguageService
    public void languageUninstalled(int i, String str) throws ConnectException {
        throw new ConnectException(0, "Method is currently unimplemented, implement prior to release.");
    }

    @Override // com.nuance.connect.api.LanguageService
    public void notifyCallbacksOfStatus() {
        for (LanguageService.ListCallback listCallback : (LanguageService.ListCallback[]) this.languageCallbacks.toArray(new LanguageService.ListCallback[0])) {
            listCallback.languageListUpdate();
        }
    }

    @Override // com.nuance.connect.api.LanguageService
    public void registerCallback(LanguageService.ListCallback listCallback) {
        this.languageCallbacks.add(listCallback);
    }

    @Override // com.nuance.connect.api.LanguageService
    public void unregisterCallback(LanguageService.ListCallback listCallback) {
        this.languageCallbacks.remove(listCallback);
    }

    @Override // com.nuance.connect.api.LanguageService
    public void unregisterCallbacks() {
        this.languageCallbacks.clear();
    }
}
