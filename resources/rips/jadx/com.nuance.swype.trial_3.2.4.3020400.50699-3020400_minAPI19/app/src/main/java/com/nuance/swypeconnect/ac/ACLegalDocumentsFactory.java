package com.nuance.swypeconnect.ac;

import com.nuance.connect.api.ConnectServiceManager;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class ACLegalDocumentsFactory {
    ACLegalDocumentsFactory() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ACLegalDocuments getACLegalDocuments(String str, ConnectServiceManager connectServiceManager) {
        ACLegalDocumentsBase aCLegalDocumentsBase;
        ACLegalDocumentsBase aCLegalDocumentsBase2 = new ACLegalDocumentsBase();
        try {
            aCLegalDocumentsBase = (ACLegalDocumentsBase) Class.forName(str).newInstance();
        } catch (ClassNotFoundException e) {
            aCLegalDocumentsBase = aCLegalDocumentsBase2;
        } catch (IllegalAccessException e2) {
            aCLegalDocumentsBase = aCLegalDocumentsBase2;
        } catch (InstantiationException e3) {
            aCLegalDocumentsBase = aCLegalDocumentsBase2;
        }
        aCLegalDocumentsBase.init(connectServiceManager);
        return aCLegalDocumentsBase;
    }
}
