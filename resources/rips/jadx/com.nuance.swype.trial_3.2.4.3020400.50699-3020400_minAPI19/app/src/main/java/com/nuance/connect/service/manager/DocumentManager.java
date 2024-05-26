package com.nuance.connect.service.manager;

import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import com.nuance.connect.comm.Command;
import com.nuance.connect.comm.MessageAPI;
import com.nuance.connect.comm.Response;
import com.nuance.connect.common.Strings;
import com.nuance.connect.internal.common.Document;
import com.nuance.connect.internal.common.InternalMessages;
import com.nuance.connect.internal.common.ManagerService;
import com.nuance.connect.service.ConnectClient;
import com.nuance.connect.service.configuration.ConnectConfiguration;
import com.nuance.connect.service.manager.AbstractCommandManager;
import com.nuance.connect.service.manager.interfaces.LanguageListener;
import com.nuance.connect.util.FileUtils;
import com.nuance.connect.util.InstallMetadata;
import com.nuance.connect.util.Logger;
import com.nuance.connect.util.StringUtils;
import com.nuance.connect.util.TimeConversion;
import com.nuance.connect.util.VersionUtils;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class DocumentManager extends AbstractCommandManager {
    private static final String ACCEPTED_TIMESTAMP = "TIME_ACCEPTED";
    public static final String COMMAND_FAMILY;
    public static final String COMMAND_GET_DOCUMENT = "documentGet";
    public static final int COMMAND_VERSION = 9;
    private static final String DOCS_DIR = "docs";
    private static final String DOC_ID = "docId";
    private static final String EXTENSION = ".html";
    private static final String LAST_UPDATED_KEY;
    public static final String MANAGER_NAME;
    private static final InternalMessages[] MESSAGES_HANDLED;
    private static final String METADATA_KEY;
    private InstallMetadata docMetadata;
    private long lastUpdated;
    private final LanguageListener localeUpdateListener;
    private Logger.Log log;

    static {
        String name = ManagerService.DOCUMENTS.getName();
        COMMAND_FAMILY = name;
        MANAGER_NAME = name;
        LAST_UPDATED_KEY = COMMAND_FAMILY + "_LAST_UPDATED";
        METADATA_KEY = COMMAND_FAMILY + "_METADATA";
        MESSAGES_HANDLED = new InternalMessages[]{InternalMessages.MESSAGE_CLIENT_DOCUMENT_ACCEPTED, InternalMessages.MESSAGE_COMMAND_UPDATE_DOCS};
    }

    public DocumentManager(ConnectClient connectClient) {
        super(connectClient);
        this.log = Logger.getLog(Logger.LoggerType.DEVELOPER, getClass().getSimpleName());
        this.localeUpdateListener = new LanguageListener() { // from class: com.nuance.connect.service.manager.DocumentManager.1
            @Override // com.nuance.connect.service.manager.interfaces.LanguageListener
            public void onLanguageUpdate(int[] iArr) {
                DocumentManager.this.log.d("onLanguageUpdate");
            }

            @Override // com.nuance.connect.service.manager.interfaces.LanguageListener
            public void onLocaleUpdate(Locale locale) {
                DocumentManager.this.log.d("onLocaleUpdate(", locale, ")");
                if (AbstractCommandManager.ManagerState.STARTED.equals(DocumentManager.this.getManagerStartState())) {
                    DocumentManager.this.reprocessList();
                }
            }
        };
        this.version = 9;
        this.commandFamily = COMMAND_FAMILY;
        setMessagesHandled(MESSAGES_HANDLED);
        this.validCommands.addCommand("list", COMMAND_RESPONSE_SUCCESS);
        this.validCommands.addCommand(COMMAND_GET_DOCUMENT, COMMAND_RESPONSE_SUCCESS);
        this.docMetadata = new InstallMetadata(this.client.getDataManager(), METADATA_KEY);
    }

    private void acceptDocumentTerms(int i, int i2, int i3) {
        String buildDocId = buildDocId(i, i2, i3);
        try {
            this.docMetadata.beginTransaction();
            if (!this.docMetadata.hasPackage(buildDocId)) {
                this.docMetadata.addPackage(buildDocId);
                this.docMetadata.setProp(buildDocId, MessageAPI.TYPE, i);
                this.docMetadata.setProp(buildDocId, MessageAPI.REVISION, i3);
                this.docMetadata.setProp(buildDocId, MessageAPI.CLASS, i2);
                this.docMetadata.setProp(buildDocId, MessageAPI.URL, "");
                this.docMetadata.setProp(buildDocId, Strings.MAP_KEY_FILE_LOCATION, "");
                this.docMetadata.setStep(buildDocId, 7);
            }
            this.docMetadata.setProp(buildDocId, ACCEPTED_TIMESTAMP, System.currentTimeMillis());
            this.client.setProperty(ConnectConfiguration.ConfigProperty.DOCUMENT_REVISIONS, getAcceptedDocumentsJSON());
            if (i == Document.DocumentType.TERMS_OF_SERVICE.getDocTypeId()) {
                this.client.setProperty(ConnectConfiguration.ConfigProperty.TOS_ACCEPTED, Boolean.TRUE);
            }
            this.client.postMessageDelayed(InternalMessages.MESSAGE_COMMAND_DEVICE_UPDATE, TimeConversion.MILLIS_IN_MINUTE);
        } finally {
            this.docMetadata.commitTransaction();
        }
    }

    private String buildDocId(int i, int i2, int i3) {
        return String.valueOf(i) + Document.ID_SEPARATOR + String.valueOf(i2) + Document.ID_SEPARATOR + String.valueOf(i3);
    }

    private String getAcceptedDocumentsJSON() {
        HashMap hashMap = new HashMap();
        for (String str : this.docMetadata.allPackages()) {
            int intProp = this.docMetadata.getIntProp(str, MessageAPI.REVISION);
            boolean z = this.docMetadata.getLongProp(str, ACCEPTED_TIMESTAMP) != Long.MIN_VALUE;
            Document.DocumentType fromId = Document.DocumentType.fromId(this.docMetadata.getIntProp(str, MessageAPI.TYPE), this.docMetadata.getIntProp(str, MessageAPI.CLASS));
            Integer num = (Integer) hashMap.get(fromId);
            if (z && fromId != null && (num == null || num.intValue() < intProp)) {
                hashMap.put(fromId, Integer.valueOf(intProp));
            }
        }
        JSONArray jSONArray = new JSONArray();
        if (hashMap.size() > 0) {
            for (Map.Entry entry : hashMap.entrySet()) {
                JSONObject jSONObject = new JSONObject();
                try {
                    jSONObject.put(MessageAPI.TYPE, ((Document.DocumentType) entry.getKey()).getDocTypeId());
                    jSONObject.put(MessageAPI.CLASS, ((Document.DocumentType) entry.getKey()).getDocumentClassId());
                    jSONObject.put(MessageAPI.REVISION, entry.getValue());
                    jSONArray.put(jSONObject);
                } catch (JSONException e) {
                }
            }
        }
        return jSONArray.toString();
    }

    private void getDocument(String str) {
        if (this.docMetadata.getProp(str, MessageAPI.URL) == null) {
            this.log.d("Document URL does not exist.  Ignoring: ", str);
            return;
        }
        Command createCommand = createCommand(COMMAND_GET_DOCUMENT, Command.REQUEST_TYPE.CRITICAL);
        createCommand.thirdPartyURL = this.docMetadata.getProp(str, MessageAPI.URL);
        createCommand.method = "GET";
        createCommand.hasBody = false;
        createCommand.handleIOExceptionInConnector = false;
        createCommand.notifyDownloadStatus = false;
        createCommand.allowDuplicateOfCommand = true;
        createCommand.parameters.put(DOC_ID, str);
        sendCommand(createCommand);
    }

    private void getList() {
        this.log.d("getList()");
        Command createCommand = createCommand("list", Command.REQUEST_TYPE.CRITICAL);
        createCommand.requireSession = true;
        createCommand.parameters.put(MessageAPI.LANGUAGE, normalizeLocaleForServerRequest(this.client.getCurrentLocale()));
        sendCommand(createCommand);
    }

    private String moveDoc(String str, String str2, String str3) {
        File file = new File(this.client.getFilesDir(), DOCS_DIR);
        if (!file.exists() && !file.mkdir()) {
            return null;
        }
        File file2 = new File(file, str2 + str3);
        if (str == null || str.length() <= 0 || !FileUtils.copyFile(str, file2.getAbsolutePath())) {
            return null;
        }
        if (!new File(str).delete()) {
            this.log.d("unable to delete temporary document file " + str);
        }
        return file2.getAbsolutePath();
    }

    private String normalizeLocaleForServerRequest(Locale locale) {
        String locale2 = locale.toString();
        return locale2.contains("#") ? locale2.substring(0, locale2.indexOf("_#")) : locale2;
    }

    private void processGetDocumentResponse(Response response) {
        int lastIndexOf;
        if (1 == response.status) {
            try {
                String str = (String) response.initialCommand.parameters.get(DOC_ID);
                String str2 = response.initialCommand.thirdPartyURL;
                String str3 = EXTENSION;
                if (!TextUtils.isEmpty(str2)) {
                    try {
                        String file = new URL(str2).getFile();
                        if (!TextUtils.isEmpty(file) && (lastIndexOf = file.lastIndexOf(45)) != -1) {
                            str3 = file.substring(lastIndexOf + 1);
                        }
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
                this.docMetadata.beginTransaction();
                String moveDoc = moveDoc(response.fileLocation, str, str3.toLowerCase());
                if (moveDoc != null) {
                    this.docMetadata.setProp(str, Strings.MAP_KEY_FILE_LOCATION, moveDoc);
                    this.docMetadata.setStep(str, 7);
                    sendDocumentToHost(new String[]{str}, true);
                }
            } finally {
                this.docMetadata.commitTransaction();
            }
        }
    }

    private void processListResponse(Response response) {
        this.log.d("processListResponse()");
        if (1 != response.status) {
            this.log.e("processListResponse(): failed with status: ", Integer.valueOf(response.status));
            return;
        }
        this.log.d("processListResponse(): parameters: ", response.parameters);
        if (response.parameters.containsKey(MessageAPI.DOCUMENT_LIST)) {
            JSONArray jSONArray = (JSONArray) response.parameters.get(MessageAPI.DOCUMENT_LIST);
            String str = (String) response.initialCommand.parameters.get(MessageAPI.LANGUAGE);
            this.log.d("required documents: ", this.client.getServiceInitializationConfig().getRequiredLegalDocuments());
            List<String> stringToList = StringUtils.stringToList(this.client.getServiceInitializationConfig().getRequiredLegalDocuments(), ",");
            if (jSONArray != null) {
                try {
                    this.docMetadata.beginTransaction();
                    HashSet hashSet = new HashSet();
                    for (int i = 0; i < jSONArray.length(); i++) {
                        try {
                            JSONObject jSONObject = jSONArray.getJSONObject(i);
                            int i2 = jSONObject.getInt(MessageAPI.TYPE);
                            int i3 = jSONObject.getInt(MessageAPI.CLASS);
                            String string = jSONObject.getString(MessageAPI.URL);
                            int i4 = jSONObject.getInt(MessageAPI.REVISION);
                            String buildDocId = buildDocId(i2, i3, i4);
                            hashSet.add(buildDocId);
                            if (stringToList.contains(String.valueOf(i2))) {
                                if (!this.docMetadata.hasPackage(buildDocId)) {
                                    this.log.d("Server provided new document revision type[" + i2 + "] class[" + i3 + "] revision [" + i4 + "]");
                                    this.docMetadata.addPackage(buildDocId);
                                    this.docMetadata.setProp(buildDocId, MessageAPI.TYPE, i2);
                                    this.docMetadata.setProp(buildDocId, MessageAPI.CLASS, i3);
                                    this.docMetadata.setProp(buildDocId, MessageAPI.REVISION, i4);
                                    this.docMetadata.setProp(buildDocId, MessageAPI.URL, string);
                                    this.docMetadata.setProp(buildDocId, Strings.MAP_KEY_FILE_LOCATION, "");
                                    this.docMetadata.setProp(buildDocId, ACCEPTED_TIMESTAMP, Long.MIN_VALUE);
                                    this.docMetadata.setStep(buildDocId, 1);
                                    this.docMetadata.setProp(buildDocId, MessageAPI.LANGUAGE, str);
                                    getDocument(buildDocId);
                                } else if (str != null && !str.equals(this.docMetadata.getProp(buildDocId, MessageAPI.LANGUAGE))) {
                                    this.log.d("Updating for current language; document revision type[" + i2 + "] class[" + i3 + "] revision [" + i4 + "]");
                                    this.docMetadata.setProp(buildDocId, Strings.MAP_KEY_FILE_LOCATION, "");
                                    this.docMetadata.setProp(buildDocId, MessageAPI.LANGUAGE, str);
                                    this.docMetadata.setProp(buildDocId, MessageAPI.URL, string);
                                    this.docMetadata.setStep(buildDocId, 1);
                                    getDocument(buildDocId);
                                } else if (this.docMetadata.isInstalling(buildDocId)) {
                                    getDocument(buildDocId);
                                }
                            } else if (this.docMetadata.isInstalling(buildDocId)) {
                                getDocument(buildDocId);
                            } else {
                                this.log.d("document type ", Integer.valueOf(i2), " is not required for this build, ignoring download");
                            }
                        } catch (JSONException e) {
                            this.log.d("Error processing json object " + e.getMessage());
                        }
                    }
                    for (String str2 : this.docMetadata.allPackages()) {
                        if (!hashSet.contains(str2)) {
                            this.log.d("Found stale document id:", str2);
                            if (this.docMetadata.getLongProp(str2, ACCEPTED_TIMESTAMP) == Long.MIN_VALUE) {
                                this.docMetadata.deletePackage(str2);
                            } else if (this.docMetadata.isInstalling(str2)) {
                                this.docMetadata.setStep(str2, 8);
                            } else {
                                this.log.d("   Document is accepted.  Keeping the document");
                            }
                        }
                    }
                } finally {
                    this.docMetadata.commitTransaction();
                }
            }
        }
        this.lastUpdated = System.currentTimeMillis();
        this.client.getDataStore().saveLong(LAST_UPDATED_KEY, this.lastUpdated);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void reprocessList() {
        this.log.d("reprocessList()");
        HashMap hashMap = new HashMap();
        for (String str : this.docMetadata.allPackages()) {
            int intProp = this.docMetadata.getIntProp(str, MessageAPI.REVISION);
            Document.DocumentType fromId = Document.DocumentType.fromId(this.docMetadata.getIntProp(str, MessageAPI.TYPE), this.docMetadata.getIntProp(str, MessageAPI.CLASS));
            Integer num = (Integer) hashMap.get(fromId);
            if (fromId != null && (num == null || num.intValue() < intProp)) {
                hashMap.put(fromId, Integer.valueOf(intProp));
            }
        }
        if (hashMap.size() > 0) {
            String normalizeLocaleForServerRequest = normalizeLocaleForServerRequest(this.client.getCurrentLocale());
            HashSet hashSet = new HashSet();
            Iterator it = hashMap.entrySet().iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                Map.Entry entry = (Map.Entry) it.next();
                String buildDocId = buildDocId(((Document.DocumentType) entry.getKey()).getDocTypeId(), ((Document.DocumentType) entry.getKey()).getDocumentClassId(), ((Integer) entry.getValue()).intValue());
                if (this.docMetadata.getLongProp(buildDocId, ACCEPTED_TIMESTAMP) == Long.MIN_VALUE && !this.docMetadata.getProp(buildDocId, MessageAPI.LANGUAGE).equals(normalizeLocaleForServerRequest)) {
                    this.log.d("locale different than installed document's locale.  get list in new language");
                    hashSet.clear();
                    getList();
                    break;
                } else if (this.docMetadata.isInstalling(buildDocId)) {
                    this.log.d("document found that is installing, getting document");
                    hashSet.add(buildDocId);
                }
            }
            Iterator it2 = hashSet.iterator();
            while (it2.hasNext()) {
                getDocument((String) it2.next());
            }
        }
    }

    private void sendDocumentToHost(String[] strArr, boolean z) {
        ArrayList arrayList = new ArrayList();
        for (String str : strArr) {
            Document document = new Document(this.docMetadata.getIntProp(str, MessageAPI.TYPE), this.docMetadata.getIntProp(str, MessageAPI.CLASS), this.docMetadata.getIntProp(str, MessageAPI.REVISION), this.docMetadata.getProp(str, Strings.MAP_KEY_FILE_LOCATION), this.docMetadata.getProp(str, MessageAPI.LANGUAGE));
            if (this.docMetadata.getLongProp(str, ACCEPTED_TIMESTAMP) != Long.MIN_VALUE) {
                document.setAcceptedTimestamp(this.docMetadata.getLongProp(str, ACCEPTED_TIMESTAMP));
            }
            if (this.docMetadata.isInstalled(str)) {
                arrayList.add(document);
            }
        }
        Bundle bundle = new Bundle();
        bundle.putSerializable(Strings.DEFAULT_KEY, arrayList);
        bundle.putBoolean(Strings.BUNDLE_KEY, z);
        this.client.sendMessageToHost(InternalMessages.MESSAGE_HOST_DOCUMENT_UPDATED, bundle);
    }

    private void sendListToHost() {
        HashMap hashMap = new HashMap();
        for (String str : this.docMetadata.allPackages()) {
            int intProp = this.docMetadata.getIntProp(str, MessageAPI.REVISION);
            Document.DocumentType fromId = Document.DocumentType.fromId(this.docMetadata.getIntProp(str, MessageAPI.TYPE), this.docMetadata.getIntProp(str, MessageAPI.CLASS));
            if (fromId != null) {
                hashMap.put(fromId, Integer.valueOf(intProp));
            }
        }
        if (hashMap.size() > 0) {
            String[] strArr = new String[hashMap.size()];
            int i = 0;
            for (Map.Entry entry : hashMap.entrySet()) {
                strArr[i] = buildDocId(((Document.DocumentType) entry.getKey()).getDocTypeId(), ((Document.DocumentType) entry.getKey()).getDocumentClassId(), ((Integer) entry.getValue()).intValue());
                i++;
            }
            sendDocumentToHost(strArr, false);
        }
    }

    @Override // com.nuance.connect.service.manager.AbstractCommandManager, com.nuance.connect.service.manager.interfaces.CommandManager
    public void alarmNotification(String str, Bundle bundle) {
    }

    @Override // com.nuance.connect.service.manager.interfaces.Manager
    public String[] getDependencies() {
        ArrayList arrayList = new ArrayList();
        for (ManagerService managerService : ManagerService.DOCUMENTS.values()) {
            arrayList.add(managerService.getName());
        }
        return (String[]) arrayList.toArray(new String[0]);
    }

    @Override // com.nuance.connect.service.manager.AbstractCommandManager, com.nuance.connect.service.manager.interfaces.Manager
    public void init() {
        this.lastUpdated = this.client.getDataStore().readLong(LAST_UPDATED_KEY, Long.MIN_VALUE);
        this.client.addLanguageListener(this.localeUpdateListener);
    }

    @Override // com.nuance.connect.service.manager.AbstractCommandManager, com.nuance.connect.comm.ResponseCallback
    public void onFileResponse(Response response) {
        if (this.validCommands.isResponseFor(COMMAND_GET_DOCUMENT, response)) {
            processGetDocumentResponse(response);
        }
    }

    @Override // com.nuance.connect.service.manager.AbstractCommandManager, com.nuance.connect.service.manager.interfaces.MessageProcessor
    public boolean onHandleMessage(Message message) {
        switch (InternalMessages.fromInt(message.what)) {
            case MESSAGE_CLIENT_DOCUMENT_ACCEPTED:
                this.log.d("MESSAGE_CLIENT_DOCUMENT_ACCEPTED");
                Document document = (Document) message.getData().getSerializable(Strings.DEFAULT_KEY);
                acceptDocumentTerms(document.getType(), document.getDocumentClass(), document.getRevision());
                return true;
            case MESSAGE_COMMAND_UPDATE_DOCS:
                this.log.d("MESSAGE_COMMAND_UPDATE_DOCS");
                getList();
                return true;
            default:
                return false;
        }
    }

    @Override // com.nuance.connect.service.manager.AbstractCommandManager, com.nuance.connect.comm.ResponseCallback
    public void onResponse(Response response) {
        if (this.validCommands.isResponseFor("list", response)) {
            processListResponse(response);
        }
    }

    @Override // com.nuance.connect.service.manager.AbstractCommandManager, com.nuance.connect.service.manager.interfaces.Manager
    public void onUpgrade(VersionUtils.Version version, VersionUtils.Version version2, boolean z) {
        if (!VersionUtils.isDataCleanupRequiredOnUpgrade(version, version2) || this.docMetadata.hasPackages() || this.lastUpdated == Long.MIN_VALUE) {
            return;
        }
        this.lastUpdated = Long.MIN_VALUE;
        removePreference(LAST_UPDATED_KEY);
        getList();
    }

    @Override // com.nuance.connect.service.manager.AbstractCommandManager, com.nuance.connect.service.manager.interfaces.Manager
    public void rebind() {
        sendListToHost();
        reprocessList();
    }

    @Override // com.nuance.connect.service.manager.AbstractCommandManager, com.nuance.connect.service.manager.interfaces.Manager
    public void start() {
        this.managerStartState = AbstractCommandManager.ManagerState.STARTING;
        if (this.lastUpdated == Long.MIN_VALUE) {
            this.log.d("lastUpdated not set, need to get list.");
            getList();
        } else {
            sendListToHost();
            reprocessList();
        }
        managerStartComplete();
    }
}
