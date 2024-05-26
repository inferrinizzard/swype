package com.nuance.swypeconnect.ac;

import com.nuance.connect.api.LanguageService;
import com.nuance.connect.store.PersistentDataStore;

/* loaded from: classes.dex */
public class ACLanguageDownloadServiceFactory {
    /* JADX INFO: Access modifiers changed from: package-private */
    public static ACLanguageDownloadService getACLanguageDownloadService(String str, LanguageService languageService, PersistentDataStore persistentDataStore, ACManager aCManager) {
        ACLanguageDownloadServiceBase aCLanguageDownloadServiceBase;
        ACLanguageDownloadServiceBase aCLanguageDownloadServiceBase2 = new ACLanguageDownloadServiceBase();
        try {
            aCLanguageDownloadServiceBase = (ACLanguageDownloadServiceBase) Class.forName(str).newInstance();
        } catch (ClassNotFoundException e) {
            aCLanguageDownloadServiceBase = aCLanguageDownloadServiceBase2;
        } catch (IllegalAccessException e2) {
            aCLanguageDownloadServiceBase = aCLanguageDownloadServiceBase2;
        } catch (InstantiationException e3) {
            aCLanguageDownloadServiceBase = aCLanguageDownloadServiceBase2;
        }
        aCLanguageDownloadServiceBase.init(languageService, persistentDataStore, aCManager);
        return aCLanguageDownloadServiceBase;
    }
}
