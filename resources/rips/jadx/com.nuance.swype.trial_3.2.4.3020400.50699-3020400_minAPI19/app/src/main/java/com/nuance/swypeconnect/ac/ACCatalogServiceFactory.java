package com.nuance.swypeconnect.ac;

import com.nuance.connect.api.CatalogService;
import com.nuance.connect.store.PersistentDataStore;
import com.nuance.connect.util.Logger;

/* loaded from: classes.dex */
public class ACCatalogServiceFactory {
    /* JADX INFO: Access modifiers changed from: package-private */
    public static ACCatalogService getACCatalogService(String str, CatalogService catalogService, PersistentDataStore persistentDataStore, ACManager aCManager) {
        ACCatalogServiceBase aCCatalogServiceBase;
        Logger.Log log = Logger.getLog(Logger.LoggerType.OEM);
        log.d("ACCatalogServiceFactory.getACCatalogService(", str, ")");
        log.d("ACCatalogServiceFactory.getACCatalogService()", Boolean.valueOf(catalogService.isCatalogEnabled()), ")");
        ACCatalogServiceBase aCCatalogServiceBase2 = new ACCatalogServiceBase();
        try {
            aCCatalogServiceBase = (ACCatalogServiceBase) Class.forName(str).newInstance();
        } catch (ClassNotFoundException e) {
            aCCatalogServiceBase = aCCatalogServiceBase2;
        } catch (IllegalAccessException e2) {
            aCCatalogServiceBase = aCCatalogServiceBase2;
        } catch (InstantiationException e3) {
            aCCatalogServiceBase = aCCatalogServiceBase2;
        }
        log.d("ACCatalogServiceFactory calling init()");
        aCCatalogServiceBase.init(catalogService, persistentDataStore, aCManager);
        return aCCatalogServiceBase;
    }
}
