package com.nuance.swypeconnect.ac;

import com.nuance.connect.api.ChinesePredictionService;
import com.nuance.connect.store.PersistentDataStore;

/* loaded from: classes.dex */
public class ACChinesePredictionServiceFactory {
    /* JADX INFO: Access modifiers changed from: package-private */
    public static ACChinesePredictionService getACChinesePredictionService(String str, ChinesePredictionService chinesePredictionService, PersistentDataStore persistentDataStore, ACManager aCManager) {
        ACChinesePredictionServiceBase aCChinesePredictionServiceBase;
        ACChinesePredictionServiceBase aCChinesePredictionServiceBase2 = new ACChinesePredictionServiceBase();
        try {
            aCChinesePredictionServiceBase = (ACChinesePredictionServiceBase) Class.forName(str).newInstance();
        } catch (ClassNotFoundException e) {
            aCChinesePredictionServiceBase = aCChinesePredictionServiceBase2;
        } catch (IllegalAccessException e2) {
            aCChinesePredictionServiceBase = aCChinesePredictionServiceBase2;
        } catch (InstantiationException e3) {
            aCChinesePredictionServiceBase = aCChinesePredictionServiceBase2;
        }
        aCChinesePredictionServiceBase.init(chinesePredictionService, persistentDataStore, aCManager);
        return aCChinesePredictionServiceBase;
    }
}
