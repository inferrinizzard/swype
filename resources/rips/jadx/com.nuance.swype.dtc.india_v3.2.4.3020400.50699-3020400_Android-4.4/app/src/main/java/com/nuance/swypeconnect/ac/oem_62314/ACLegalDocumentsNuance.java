package com.nuance.swypeconnect.ac.oem_62314;

import com.nuance.swypeconnect.ac.ACLegalDocumentsBase;

/* loaded from: classes.dex */
public final class ACLegalDocumentsNuance extends ACLegalDocumentsBase {
    public static final int TYPE_EULA = 2;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swypeconnect.ac.ACLegalDocumentsBase
    public final boolean documentTypeAllowed(int i) {
        switch (i) {
            case 1:
            case 2:
            case 4:
                return true;
            case 3:
            default:
                throw new IllegalArgumentException("Invalid document type requested.");
        }
    }

    @Override // com.nuance.swypeconnect.ac.ACLegalDocumentsBase, com.nuance.swypeconnect.ac.ACLegalDocuments
    public final boolean userHasAcceptedDocumentByType(int i) {
        documentTypeAllowed(i);
        switch (i) {
            case 1:
                return this.settings.isTOSAccepted();
            case 2:
                return this.settings.isEulaAccepted();
            case 3:
            default:
                return false;
            case 4:
                return this.settings.isDataCollectionAccepted();
        }
    }
}
