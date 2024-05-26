package com.nuance.swypeconnect.ac;

import java.util.Locale;

/* loaded from: classes.dex */
public interface ACLegalDocuments {
    public static final int TYPE_DATA_OPT_IN = 4;
    public static final int TYPE_TERMS_OF_SERVICE = 1;

    /* loaded from: classes.dex */
    public interface ACLegalDocument {
        String getLastAcceptedVersion();

        String getLatestVersion();

        String getTranslation() throws ACException;

        int getType();

        boolean isAccepted();

        boolean isChanged();

        boolean wasAccepted();
    }

    void acceptDocument(ACLegalDocument aCLegalDocument) throws ACException;

    ACLegalDocument getDocument(int i) throws ACException;

    ACLegalDocument getDocument(int i, Locale locale) throws ACException;

    @Deprecated
    String getDocumentByType(int i);

    @Deprecated
    String getVersionByType(int i);

    void resetChangedFlag(ACLegalDocument aCLegalDocument);

    @Deprecated
    void userAcceptDocumentByType(int i) throws ACException;

    @Deprecated
    boolean userHasAcceptedDocumentByType(int i);
}
