package com.nuance.connect.internal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Pair;
import com.nuance.connect.api.DocumentService;
import com.nuance.connect.comm.MessageAPI;
import com.nuance.connect.common.ConnectFeature;
import com.nuance.connect.common.Strings;
import com.nuance.connect.internal.common.APIHandlers;
import com.nuance.connect.internal.common.Document;
import com.nuance.connect.internal.common.InternalMessages;
import com.nuance.connect.internal.common.ManagerService;
import com.nuance.connect.util.InstallMetadata;
import com.nuance.connect.util.Logger;
import com.nuance.connect.util.StringUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class DocumentServiceInternal extends AbstractService implements DocumentService {
    private static final String ACCEPTED_TIMESTAMP = "TIME_ACCEPTED";
    public static final String DEFAULT_LOCALE = "EN";
    private static final String EULA = "EULA_";
    private static final String PRIVACY = "PRIVACY_";
    private static final String TOS = "TOS_";
    private static final String USAGE = "USAGE_";
    private final AssetsLegalDocuments assetsLegalDocuments;
    private ConnectServiceManagerInternal connectService;
    private static final String METADATA_KEY = ManagerService.DOCUMENTS.getName() + "_METADATA";
    private static final InternalMessages[] MESSAGE_IDS = {InternalMessages.MESSAGE_HOST_DOCUMENT_UPDATED};
    private final Logger.Log log = Logger.getLog(Logger.LoggerType.DEVELOPER, getClass().getSimpleName());
    private final Pattern versionPattern = Pattern.compile("<body.*data-document-version=\"([0-9]+)\"");
    private final HashMap<String, TreeMap<Integer, Document>> documents = new HashMap<>();
    private final HashMap<String, Integer> documentRevisionProvided = new HashMap<>();
    private ConnectHandler handler = new ConnectHandler() { // from class: com.nuance.connect.internal.DocumentServiceInternal.1
        @Override // com.nuance.connect.internal.ConnectHandler
        public String getHandlerName() {
            return APIHandlers.DOCUMENT_HANDLER;
        }

        @Override // com.nuance.connect.internal.ConnectHandler
        public int[] getMessageIDs() {
            int[] iArr = new int[DocumentServiceInternal.MESSAGE_IDS.length];
            for (int i = 0; i < DocumentServiceInternal.MESSAGE_IDS.length; i++) {
                iArr[i] = DocumentServiceInternal.MESSAGE_IDS[i].ordinal();
            }
            return iArr;
        }

        @Override // com.nuance.connect.internal.ConnectHandler
        public void handleMessage(Handler handler, Message message) {
            DocumentServiceInternal.this.handleMessage(handler, message);
        }

        @Override // com.nuance.connect.internal.ConnectHandler
        public void onPostUpgrade() {
        }
    };

    /* JADX INFO: Access modifiers changed from: package-private */
    public DocumentServiceInternal(ConnectServiceManagerInternal connectServiceManagerInternal) {
        this.connectService = connectServiceManagerInternal;
        this.assetsLegalDocuments = new AssetsLegalDocuments(connectServiceManagerInternal.getContext());
        setup(connectServiceManagerInternal.getContext());
    }

    private void acceptDocument(Document.DocumentType documentType, int i) {
        TreeMap<Integer, Document> treeMap = this.documents.get(Document.getPrimaryKey(documentType.getDocTypeId(), documentType.getDocumentClassId()));
        if (treeMap == null || !treeMap.containsKey(Integer.valueOf(i))) {
            this.log.e("Accept document failed. Unknown revision(" + i + ") requested for docType " + documentType.getDocTypeId());
            return;
        }
        Document document = treeMap.get(Integer.valueOf(i));
        document.setAcceptedTimestamp(System.currentTimeMillis());
        switch (documentType) {
            case EULA:
                this.connectService.getUserSettings().acceptEula();
                break;
            case TERMS_OF_SERVICE:
                this.connectService.getUserSettings().userHasAcceptedTOS();
                break;
            case DATA_OPT_IN:
                this.connectService.getUserSettings().acceptUsage();
                break;
        }
        Bundle bundle = new Bundle();
        bundle.putSerializable(Strings.DEFAULT_KEY, document);
        this.connectService.getBinder().sendConnectMessage(InternalMessages.MESSAGE_CLIENT_DOCUMENT_ACCEPTED, bundle);
    }

    @SuppressLint({"UseSparseArrays"})
    private void addDocument(Document document, boolean z) {
        this.log.d("addDocument() type: ", Integer.valueOf(document.getType()), " revision: ", Integer.valueOf(document.getRevision()));
        String primaryKey = Document.getPrimaryKey(document.getType(), document.getDocumentClass());
        if (!this.documents.containsKey(primaryKey)) {
            this.log.d("No TreeMap, adding now.");
            this.documents.put(primaryKey, new TreeMap<>());
        }
        TreeMap<Integer, Document> treeMap = this.documents.get(primaryKey);
        Document value = treeMap.lastEntry() != null ? treeMap.lastEntry().getValue() : null;
        Document document2 = treeMap.get(Integer.valueOf(document.getRevision()));
        boolean z2 = z && value != null && document.compareTo(value) > 0;
        this.log.d("checking to see if document ", Integer.valueOf(document.getType()), " revision ", Integer.valueOf(document.getRevision()), " is already stored.");
        if (document2 != null && document2.getAccepted()) {
            this.log.d("Document ", Integer.valueOf(document.getType()), " at revision ", Integer.valueOf(document.getRevision()), " is already known and accepted, ignoring add.");
            document2.addTranslation(document.getTranslations());
        } else if (document2 != null && !document2.getAccepted() && document.getAccepted()) {
            this.log.d("Document ", Integer.valueOf(document.getType()), " at revision ", Integer.valueOf(document.getRevision()), " is accepted, updating.");
            treeMap.put(Integer.valueOf(document.getRevision()), document);
            document.addTranslation(document2.getTranslations());
        } else if (document2 != null) {
            this.log.d("Document ", Integer.valueOf(document.getType()), " at revision ", Integer.valueOf(document.getRevision()), " updating translation.");
            document2.addTranslation(document.getTranslations());
        } else {
            this.log.d("Document ", Integer.valueOf(document.getType()), " at revision ", Integer.valueOf(document.getRevision()), " not found, adding now.");
            treeMap.put(Integer.valueOf(document.getRevision()), document);
        }
        Map.Entry<Integer, Document> lastEntry = treeMap.lastEntry();
        this.log.d("latest entry: ", Integer.valueOf(lastEntry.getValue().getType()), " rev. ", lastEntry.getKey(), " accepted: ", Boolean.valueOf(lastEntry.getValue().getAccepted()));
        if (lastEntry.getValue().getAccepted()) {
            return;
        }
        Document.DocumentType fromId = Document.DocumentType.fromId(lastEntry.getValue().getType(), lastEntry.getValue().getDocumentClass());
        this.log.d("New version of type: ", fromId.name(), " has not been accepted.  Checking to see if we need to reset.");
        UserSettings userSettings = this.connectService.getUserSettings();
        switch (fromId) {
            case EULA:
                if (userSettings.isEulaAccepted() && !userSettings.isResetEulaOnNextStart() && z2) {
                    this.log.d("Older version of EULA has been accepted, require re-acceptance on restart");
                    this.connectService.notifyConnectionStatus(20, "A new version of the EULA has been made available that must be accepted on the next start.");
                    userSettings.setResetEulaOnNextStart(true);
                    return;
                } else {
                    if (z2) {
                        this.log.d("New version of document ", Integer.valueOf(document.getType()), " at revision ", Integer.valueOf(document.getRevision()), " has been received.  Updating changed flag.");
                        this.connectService.notifyConnectionStatus(20, "A new version of the EULA has been made available that must be accepted on the next start.");
                        userSettings.setEulaChanged(true);
                        return;
                    }
                    return;
                }
            case TERMS_OF_SERVICE:
                if (userSettings.isTOSAccepted() && !userSettings.isResetTOSAcceptedOnNextStart() && z2) {
                    this.log.d("Older version of TOS has been accepted, require re-acceptance on restart");
                    this.connectService.notifyConnectionStatus(18, "A new version of the Terms of Service has been made available that must be accepted on the next start.");
                    userSettings.setResetTOSAcceptedOnNextStart(true);
                    return;
                } else {
                    if (z2) {
                        this.log.d("New version of document ", Integer.valueOf(document.getType()), " at revision ", Integer.valueOf(document.getRevision()), " has been received.  Updating changed flag.");
                        this.connectService.notifyConnectionStatus(18, "A new version of the Terms of Service has been made available that must be accepted on the next start.");
                        userSettings.setTosChanged(true);
                        return;
                    }
                    return;
                }
            case PRIVACY_POLICY:
            default:
                return;
            case DATA_OPT_IN:
                if (userSettings.isDataCollectionAccepted() && !userSettings.isResetUsageAcceptedOnNextStart() && z2) {
                    this.log.d("Older version of USAGE has been accepted, require re-acceptance on restart");
                    this.connectService.notifyConnectionStatus(19, "A new version of the Data Opt-in agreement has been made available that must be accepted on the next start.");
                    userSettings.setResetUsageAcceptedOnNextStart(true);
                    return;
                } else {
                    if (z2) {
                        this.log.d("New version of document ", Integer.valueOf(document.getType()), " at revision ", Integer.valueOf(document.getRevision()), " has been received.  Updating changed flag.");
                        this.connectService.notifyConnectionStatus(19, "A new version of the Data Opt-in agreement has been made available that must be accepted on the next start.");
                        userSettings.setUsageChanged(true);
                        return;
                    }
                    return;
                }
        }
    }

    private Pair<Integer, String> getDefaultDocument(int i, String str) {
        int i2 = 0;
        String text = this.assetsLegalDocuments.getText(i, str);
        if (text == null || text.isEmpty()) {
            return null;
        }
        Matcher matcher = this.versionPattern.matcher(text);
        if (matcher.find()) {
            try {
                i2 = Integer.valueOf(Integer.parseInt(matcher.group(1)));
            } catch (NumberFormatException e) {
                this.log.d("number format exception processing ", matcher.group(1));
                e.printStackTrace();
            }
        } else {
            this.log.d("couldn't find version number in content.");
        }
        return new Pair<>(i2, text);
    }

    private Pair<Integer, String> getDownloadedDocument(Document.DocumentType documentType, String str) {
        TreeMap<Integer, Document> treeMap;
        Document document;
        String documentPath;
        String primaryKey = Document.getPrimaryKey(documentType.getDocTypeId(), documentType.getDocumentClassId());
        Integer valueOf = Integer.valueOf(getLatestDocumentRevision(documentType.getDocTypeId(), documentType.getDocumentClassId()));
        if (valueOf.intValue() > 0 && (treeMap = this.documents.get(primaryKey)) != null && treeMap.containsKey(valueOf) && (document = treeMap.get(valueOf)) != null && (documentPath = document.getDocumentPath(str)) != null) {
            File file = new File(documentPath);
            if (file.exists()) {
                String fileContents = StringUtils.getFileContents(file);
                if (!fileContents.isEmpty()) {
                    return new Pair<>(valueOf, fileContents);
                }
            }
        }
        return null;
    }

    private int getLatestDocumentRevision(int i, int i2) {
        TreeMap<Integer, Document> treeMap = this.documents.get(Document.getPrimaryKey(i, i2));
        if (treeMap != null) {
            ArrayList arrayList = new ArrayList(treeMap.values());
            Collections.sort(arrayList);
            if (treeMap.size() > 0) {
                return ((Document) arrayList.get(arrayList.size() - 1)).getRevision();
            }
        }
        return 0;
    }

    private List<String> getMatchOrder(Locale locale) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(locale.toString().replace("#", ""));
        String str = locale.getLanguage() + Document.ID_SEPARATOR + locale.getCountry();
        if (!locale.toString().equals(str)) {
            arrayList.add(str);
        }
        arrayList.add(locale.getLanguage());
        return arrayList;
    }

    private String getNameDocumentName(int i, String str) {
        String upperCase = str.toUpperCase(Locale.ENGLISH);
        switch (i) {
            case 1:
                return TOS + upperCase;
            case 2:
                return EULA + upperCase;
            case 3:
                return PRIVACY + upperCase;
            case 4:
                return USAGE + upperCase;
            default:
                this.log.e("Unknown Document Type:" + i);
                return "";
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleMessage(Handler handler, Message message) {
        switch (InternalMessages.fromInt(message.what)) {
            case MESSAGE_HOST_DOCUMENT_UPDATED:
                boolean z = message.getData().getBoolean(Strings.BUNDLE_KEY);
                Iterator it = ((ArrayList) message.getData().getSerializable(Strings.DEFAULT_KEY)).iterator();
                while (it.hasNext()) {
                    addDocument((Document) it.next(), z);
                }
                return;
            default:
                return;
        }
    }

    private void setup(Context context) {
        for (Document.DocumentType documentType : Document.DocumentType.values()) {
            Pair<Integer, String> defaultDocument = getDefaultDocument(documentType.getDocTypeId(), DEFAULT_LOCALE);
            if (defaultDocument != null) {
                addDocument(new Document(documentType.getDocTypeId(), documentType.getDocumentClassId(), ((Integer) defaultDocument.first).intValue(), "", DEFAULT_LOCALE), false);
            } else {
                addDocument(new Document(documentType.getDocTypeId(), documentType.getDocumentClassId(), 0, "", DEFAULT_LOCALE), false);
            }
        }
        InstallMetadata installMetadata = new InstallMetadata(this.connectService.getDataManager(), METADATA_KEY);
        for (String str : installMetadata.allPackages()) {
            if (Document.DocumentType.fromId(installMetadata.getIntProp(str, MessageAPI.TYPE), installMetadata.getIntProp(str, MessageAPI.CLASS)) != null) {
                Document document = new Document(installMetadata.getIntProp(str, MessageAPI.TYPE), installMetadata.getIntProp(str, MessageAPI.CLASS), installMetadata.getIntProp(str, MessageAPI.REVISION), installMetadata.getProp(str, Strings.MAP_KEY_FILE_LOCATION), installMetadata.getProp(str, MessageAPI.LANGUAGE));
                if (installMetadata.getLongProp(str, ACCEPTED_TIMESTAMP) != Long.MIN_VALUE) {
                    document.setAcceptedTimestamp(installMetadata.getLongProp(str, ACCEPTED_TIMESTAMP));
                }
                if (installMetadata.isInstalled(str)) {
                    addDocument(document, false);
                }
            }
        }
    }

    @Override // com.nuance.connect.api.DocumentService
    public void acceptDocument(Document.DocumentType documentType) {
        acceptDocument(documentType, this.documentRevisionProvided.get(Document.getPrimaryKey(documentType.getDocTypeId(), documentType.getDocumentClassId())).intValue());
    }

    @Override // com.nuance.connect.api.DocumentService
    public void acceptDocumentIfCurrent(Document.DocumentType documentType, int i) {
        TreeMap<Integer, Document> treeMap = this.documents.get(Document.getPrimaryKey(documentType.getDocTypeId(), documentType.getDocumentClassId()));
        if (treeMap == null || treeMap.lastKey().intValue() == i) {
            acceptDocument(documentType, i);
        } else {
            this.log.e("The document being accepted is out of date.  An updated version of the document must be requested before it can be accepted.");
            throw new RuntimeException("DOCUMENT_OUT_OF_DATE");
        }
    }

    @Override // com.nuance.connect.api.DocumentService
    public Map<Integer, Document> getAllRevisions(Document.DocumentType documentType) {
        return this.documents.get(Document.getPrimaryKey(documentType.getDocTypeId(), documentType.getDocumentClassId()));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.connect.internal.AbstractService
    public ConnectFeature[] getDependencies() {
        return ConnectFeature.DOCUMENTS.values();
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.nuance.connect.api.DocumentService
    public String getDocument(Document.DocumentType documentType, Locale locale) {
        TreeMap treeMap;
        if (locale == null) {
            this.log.d("locale is null; reverting to default");
            locale = Locale.getDefault();
            if (locale == null) {
                this.log.e("Could not get default locale");
                locale = Locale.ENGLISH;
            }
        }
        this.log.d("Document ", documentType.name(), " requested for locale ", locale.toString());
        List<String> matchOrder = getMatchOrder(locale);
        this.log.d("Match order is: ", matchOrder);
        String primaryKey = Document.getPrimaryKey(documentType.getDocTypeId(), documentType.getDocumentClassId());
        for (String str : matchOrder) {
            this.log.d("Checking for most recent cloud version of document in ", str);
            Pair<Integer, String> downloadedDocument = getDownloadedDocument(documentType, str);
            if (downloadedDocument != null) {
                this.log.d("most recent cloud version (", downloadedDocument.first, ") of document found for locale ", str);
                this.documentRevisionProvided.put(primaryKey, downloadedDocument.first);
                return (String) downloadedDocument.second;
            }
        }
        if (!matchOrder.contains(Locale.ENGLISH.getLanguage())) {
            matchOrder.add(Locale.ENGLISH.getLanguage());
        }
        this.log.d("Match order for embedded documents is: ", matchOrder);
        for (String str2 : matchOrder) {
            this.log.d("Checking for most recent built-in version of document in ", str2);
            Pair<Integer, String> defaultDocument = getDefaultDocument(documentType.getDocTypeId(), str2);
            if (defaultDocument != null) {
                this.log.d("most recent built-in version (", defaultDocument.first, ") of document found for locale ", str2);
                this.documentRevisionProvided.put(primaryKey, defaultDocument.first);
                TreeMap<Integer, Document> treeMap2 = this.documents.get(primaryKey);
                if (treeMap2 == null) {
                    TreeMap<Integer, Document> treeMap3 = new TreeMap<>();
                    this.documents.put(primaryKey, treeMap3);
                    treeMap = treeMap3;
                } else {
                    treeMap = treeMap2;
                }
                if (!treeMap.containsKey(defaultDocument.first)) {
                    treeMap.put(defaultDocument.first, new Document(documentType.getDocTypeId(), documentType.getDocumentClassId(), ((Integer) defaultDocument.first).intValue(), "", locale.toString()));
                }
                return (String) defaultDocument.second;
            }
        }
        throw new IllegalArgumentException("Get document failed no English translation found for requested document " + documentType.name());
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.nuance.connect.api.DocumentService
    public String getDocumentRevisionNumber(Document.DocumentType documentType, Locale locale) {
        Pair<Integer, String> defaultDocument;
        String primaryKey = Document.getPrimaryKey(documentType.getDocTypeId(), documentType.getDocumentClassId());
        if (this.documentRevisionProvided.containsKey(primaryKey) && this.documentRevisionProvided.get(primaryKey) != null) {
            return this.documentRevisionProvided.get(primaryKey).toString();
        }
        int latestDocumentRevision = getLatestDocumentRevision(documentType.getDocTypeId(), documentType.getDocumentClassId());
        if (latestDocumentRevision > 0) {
            this.documentRevisionProvided.put(primaryKey, Integer.valueOf(latestDocumentRevision));
            return String.valueOf(latestDocumentRevision);
        }
        Pair<Integer, String> defaultDocument2 = getDefaultDocument(documentType.getDocTypeId(), locale.toString());
        if (defaultDocument2 != null) {
            this.documentRevisionProvided.put(primaryKey, defaultDocument2.first);
            return ((Integer) defaultDocument2.first).toString();
        }
        if (!locale.toString().equals(locale.getLanguage()) && (defaultDocument = getDefaultDocument(documentType.getDocTypeId(), locale.getLanguage())) != null) {
            this.documentRevisionProvided.put(primaryKey, defaultDocument.first);
            return ((Integer) defaultDocument.first).toString();
        }
        Pair<Integer, String> defaultDocument3 = getDefaultDocument(documentType.getDocTypeId(), Locale.ENGLISH.getLanguage());
        if (defaultDocument3 == null) {
            return "0";
        }
        this.documentRevisionProvided.put(primaryKey, defaultDocument3.first);
        return ((Integer) defaultDocument3.first).toString();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.connect.internal.AbstractService
    public ConnectHandler[] getHandlers() {
        return new ConnectHandler[]{this.handler};
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.connect.internal.AbstractService
    public String getServiceName() {
        return ConnectFeature.DOCUMENTS.name();
    }
}
