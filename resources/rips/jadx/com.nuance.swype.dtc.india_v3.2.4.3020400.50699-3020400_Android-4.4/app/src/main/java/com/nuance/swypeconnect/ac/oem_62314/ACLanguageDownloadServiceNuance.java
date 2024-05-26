package com.nuance.swypeconnect.ac.oem_62314;

import com.nuance.connect.util.Logger;
import com.nuance.swypeconnect.ac.ACException;
import com.nuance.swypeconnect.ac.ACLanguageDownloadServiceBase;

/* loaded from: classes.dex */
public class ACLanguageDownloadServiceNuance extends ACLanguageDownloadServiceBase {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swypeconnect.ac.ACLanguageDownloadServiceBase
    public void isAuthorized(int i) throws ACException {
        if (requiresDocument(1, i)) {
            Logger.getLog(Logger.LoggerType.CUSTOMER).e("Attempting to download a language before TOS has been accepted.");
            throw new ACException(104, "Please Accept the TOS before requesting " + super.getName() + ".");
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swypeconnect.ac.ACLanguageDownloadServiceBase, com.nuance.swypeconnect.ac.ACService
    public boolean requiresDocument(int i) {
        return requiresDocument(i, 1);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swypeconnect.ac.ACLanguageDownloadServiceBase
    public boolean requiresDocument(int i, int i2) {
        switch (i2) {
            case 0:
                if (i == 1 || i == 2) {
                    return this.manager.getLegalDocuments() == null || !(this.manager.getLegalDocuments().userHasAcceptedDocumentByType(1) || this.manager.getLegalDocuments().userHasAcceptedDocumentByType(2));
                }
                return false;
            case 1:
            default:
                return false;
        }
    }
}
