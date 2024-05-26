package com.nuance.connect.api;

import com.nuance.connect.internal.common.Document;
import java.util.Locale;
import java.util.Map;

/* loaded from: classes.dex */
public interface DocumentService {
    void acceptDocument(Document.DocumentType documentType);

    void acceptDocumentIfCurrent(Document.DocumentType documentType, int i);

    Map<Integer, Document> getAllRevisions(Document.DocumentType documentType);

    String getDocument(Document.DocumentType documentType, Locale locale);

    String getDocumentRevisionNumber(Document.DocumentType documentType, Locale locale);
}
