package com.nuance.swypeconnect.ac;

import android.util.SparseBooleanArray;
import com.nuance.connect.api.ConfigService;
import com.nuance.connect.api.ConnectServiceManager;
import com.nuance.connect.api.DocumentService;
import com.nuance.connect.common.ConnectFeature;
import com.nuance.connect.internal.UserSettings;
import com.nuance.connect.internal.common.Document;
import com.nuance.connect.util.Logger;
import com.nuance.connect.util.TimeConversion;
import com.nuance.swypeconnect.ac.ACLegalDocuments;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/* loaded from: classes.dex */
public class ACLegalDocumentsBase implements ACLegalDocuments {
    private ConfigService configService;
    private Logger.Log customerLog = Logger.getLog(Logger.LoggerType.CUSTOMER);
    private SparseBooleanArray documentsShown = new SparseBooleanArray(4);
    protected DocumentService service;
    public UserSettings settings;

    /* loaded from: classes.dex */
    protected static class ACLegalDocumentImpl implements ACLegalDocuments.ACLegalDocument {
        private final boolean accepted;
        private final TreeMap<Integer, Document> allRevisions;
        private final boolean changed;
        private final Locale locale;
        private final WeakReference<ACLegalDocuments> parentRef;
        private final int type;

        private ACLegalDocumentImpl(int i, Map<Integer, Document> map, boolean z, ACLegalDocuments aCLegalDocuments, boolean z2) {
            this(i, map, z, aCLegalDocuments, z2, (Locale) null);
        }

        private ACLegalDocumentImpl(int i, Map<Integer, Document> map, boolean z, ACLegalDocuments aCLegalDocuments, boolean z2, Locale locale) {
            this.allRevisions = new TreeMap<>(Collections.reverseOrder());
            this.type = i;
            this.allRevisions.putAll(map);
            this.changed = z;
            this.parentRef = new WeakReference<>(aCLegalDocuments);
            this.accepted = z2;
            this.locale = locale;
        }

        @Override // com.nuance.swypeconnect.ac.ACLegalDocuments.ACLegalDocument
        public String getLastAcceptedVersion() {
            for (Map.Entry<Integer, Document> entry : this.allRevisions.entrySet()) {
                if (entry.getValue().getAccepted()) {
                    return entry.getKey().toString();
                }
            }
            return "-1";
        }

        @Override // com.nuance.swypeconnect.ac.ACLegalDocuments.ACLegalDocument
        public String getLatestVersion() {
            return String.valueOf(this.allRevisions.firstKey());
        }

        @Override // com.nuance.swypeconnect.ac.ACLegalDocuments.ACLegalDocument
        public String getTranslation() throws ACException {
            ACLegalDocumentsBase aCLegalDocumentsBase = (ACLegalDocumentsBase) this.parentRef.get();
            if (aCLegalDocumentsBase != null) {
                return this.locale != null ? aCLegalDocumentsBase.getDocumentByType(getType(), this.locale) : aCLegalDocumentsBase.getDocumentByType(getType());
            }
            throw new ACException(109, "This document is out of date.  Call getDocument() to get a new version.");
        }

        @Override // com.nuance.swypeconnect.ac.ACLegalDocuments.ACLegalDocument
        public int getType() {
            return this.type;
        }

        @Override // com.nuance.swypeconnect.ac.ACLegalDocuments.ACLegalDocument
        public boolean isAccepted() {
            return this.accepted;
        }

        @Override // com.nuance.swypeconnect.ac.ACLegalDocuments.ACLegalDocument
        public boolean isChanged() {
            return this.changed;
        }

        @Override // com.nuance.swypeconnect.ac.ACLegalDocuments.ACLegalDocument
        public boolean wasAccepted() {
            return isAccepted() || !"-1".equals(getLastAcceptedVersion());
        }
    }

    private boolean documentTypeAllowed(ACLegalDocuments.ACLegalDocument aCLegalDocument) {
        if (aCLegalDocument == null) {
            throw new IllegalArgumentException("Invalid document type requested.");
        }
        return documentTypeAllowed(aCLegalDocument.getType());
    }

    private String getVersionByType(Document.DocumentType documentType) {
        return this.service.getDocumentRevisionNumber(documentType, this.configService.getActiveLocale());
    }

    private boolean hasDocumentChanged(int i) {
        switch (i) {
            case 1:
                return this.settings.isTosChanged();
            case 2:
                return this.settings.isEulaChanged();
            case 3:
            default:
                return false;
            case 4:
                return this.settings.isUsageChanged();
        }
    }

    private boolean isDocumentShown(int i) {
        return this.documentsShown.indexOfKey(i) >= 0;
    }

    private void setDocumentShown(int i) {
        this.documentsShown.put(i, true);
    }

    @Override // com.nuance.swypeconnect.ac.ACLegalDocuments
    public void acceptDocument(ACLegalDocuments.ACLegalDocument aCLegalDocument) throws ACException {
        documentTypeAllowed(aCLegalDocument);
        Document.DocumentType fromId = Document.DocumentType.fromId(aCLegalDocument.getType(), 1);
        try {
            this.service.acceptDocumentIfCurrent(fromId, Integer.parseInt(aCLegalDocument.getLatestVersion()));
            this.customerLog.i("Document ", fromId.name(), " version ", getVersionByType(fromId), " accepted on ", TimeConversion.prettyDateFormat(System.currentTimeMillis()));
        } catch (Exception e) {
            throw new ACException(136, "The document being accepted is out of date.  An updated version of the document must be requested before it can be accepted.");
        }
    }

    public boolean documentTypeAllowed(int i) {
        if (i == 1 || i == 4) {
            return true;
        }
        throw new IllegalArgumentException("Invalid document type requested.");
    }

    @Override // com.nuance.swypeconnect.ac.ACLegalDocuments
    public ACLegalDocuments.ACLegalDocument getDocument(int i) throws ACException {
        documentTypeAllowed(i);
        return new ACLegalDocumentImpl(i, this.service.getAllRevisions(Document.DocumentType.fromId(i, 1)), hasDocumentChanged(i), this, userHasAcceptedDocumentByType(i));
    }

    @Override // com.nuance.swypeconnect.ac.ACLegalDocuments
    public ACLegalDocuments.ACLegalDocument getDocument(int i, Locale locale) throws ACException {
        documentTypeAllowed(i);
        return new ACLegalDocumentImpl(i, this.service.getAllRevisions(Document.DocumentType.fromId(i, 1)), hasDocumentChanged(i), this, userHasAcceptedDocumentByType(i), locale);
    }

    @Override // com.nuance.swypeconnect.ac.ACLegalDocuments
    public String getDocumentByType(int i) {
        return getDocumentByType(i, this.configService.getActiveLocale());
    }

    String getDocumentByType(int i, Locale locale) {
        documentTypeAllowed(i);
        setDocumentShown(i);
        return this.service.getDocument(Document.DocumentType.fromId(i, 1), locale);
    }

    @Override // com.nuance.swypeconnect.ac.ACLegalDocuments
    public String getVersionByType(int i) {
        documentTypeAllowed(i);
        return getVersionByType(Document.DocumentType.fromId(i, 1));
    }

    public void init(ConnectServiceManager connectServiceManager) {
        this.settings = connectServiceManager.getUserSettings();
        this.service = (DocumentService) connectServiceManager.getFeatureService(ConnectFeature.DOCUMENTS);
        this.configService = (ConfigService) connectServiceManager.getFeatureService(ConnectFeature.CONFIG);
        if (this.settings.isResetEulaOnNextStart()) {
            this.settings.resetEula();
        }
        if (this.settings.isResetTOSAcceptedOnNextStart()) {
            this.settings.resetTOS();
        }
        if (this.settings.isResetUsageAcceptedOnNextStart()) {
            this.settings.setDataCollectionAccepted(false);
        }
    }

    @Override // com.nuance.swypeconnect.ac.ACLegalDocuments
    public void resetChangedFlag(ACLegalDocuments.ACLegalDocument aCLegalDocument) {
        documentTypeAllowed(aCLegalDocument);
        this.customerLog.i("Document ", Document.DocumentType.fromId(aCLegalDocument.getType(), 1).name(), " changed flag reset on ", TimeConversion.prettyDateFormat(System.currentTimeMillis()));
        switch (aCLegalDocument.getType()) {
            case 1:
                this.settings.setTosChanged(false);
                return;
            case 2:
                this.settings.setEulaChanged(false);
                return;
            case 3:
            default:
                return;
            case 4:
                this.settings.setUsageChanged(false);
                return;
        }
    }

    @Override // com.nuance.swypeconnect.ac.ACLegalDocuments
    public void userAcceptDocumentByType(int i) throws ACException {
        documentTypeAllowed(i);
        if (!isDocumentShown(i)) {
            throw new ACException(121, "You must call getDocumentByType before calling userAcceptDocumentByType()");
        }
        Document.DocumentType fromId = Document.DocumentType.fromId(i, 1);
        this.customerLog.i("Document ", fromId.name(), " version ", getVersionByType(fromId), " accepted on ", TimeConversion.prettyDateFormat(System.currentTimeMillis()));
        this.service.acceptDocument(fromId);
    }

    @Override // com.nuance.swypeconnect.ac.ACLegalDocuments
    public boolean userHasAcceptedDocumentByType(int i) {
        documentTypeAllowed(i);
        switch (i) {
            case 1:
                return this.settings.isTOSAccepted();
            case 2:
            case 3:
            default:
                return false;
            case 4:
                return this.settings.isDataCollectionAccepted();
        }
    }
}
