package com.nuance.swypeconnect.ac;

import com.nuance.connect.api.ChinesePredictionService;
import com.nuance.connect.api.ConnectException;
import com.nuance.connect.common.FeaturesLastUsed;
import com.nuance.connect.store.PersistentDataStore;
import com.nuance.connect.util.ConcurrentCallbackSet;
import com.nuance.connect.util.Logger;
import com.nuance.swypeconnect.ac.ACChinesePredictionService;

/* loaded from: classes.dex */
public class ACChinesePredictionServiceBase extends ACService implements ACChinesePredictionService {
    private ACManager manager;
    private ChinesePredictionService predictionService;
    private final ConcurrentCallbackSet<ACChinesePredictionService.ACChinesePredictionCallback> listCallbacks = new ConcurrentCallbackSet<>();
    private Logger.Log log = Logger.getLog(Logger.LoggerType.OEM);
    private ChinesePredictionService.ChinesePredictionCallback predictionCallback = new ChinesePredictionService.ChinesePredictionCallback() { // from class: com.nuance.swypeconnect.ac.ACChinesePredictionServiceBase.1
        @Override // com.nuance.connect.api.ChinesePredictionService.ChinesePredictionCallback
        public void onPredictionCancel(String str) {
            ACChinesePredictionServiceBase.this.log.d("ChinesePredictionCallback.onPredictionCancel() predictionId=", str);
            if (ACChinesePredictionServiceBase.this.isShutdown) {
                return;
            }
            for (ACChinesePredictionService.ACChinesePredictionCallback aCChinesePredictionCallback : (ACChinesePredictionService.ACChinesePredictionCallback[]) ACChinesePredictionServiceBase.this.listCallbacks.toArray(new ACChinesePredictionService.ACChinesePredictionCallback[0])) {
                aCChinesePredictionCallback.onPredictionCancel(str);
            }
        }

        @Override // com.nuance.connect.api.ChinesePredictionService.ChinesePredictionCallback
        public void onPredictionFailure(String str, int i, String str2) {
            ACChinesePredictionServiceBase.this.log.d("ChinesePredictionCallback.onPredictionFailure() predictionId=", str, " code=", Integer.valueOf(i), " description=", str2);
            if (ACChinesePredictionServiceBase.this.isShutdown) {
                return;
            }
            for (ACChinesePredictionService.ACChinesePredictionCallback aCChinesePredictionCallback : (ACChinesePredictionService.ACChinesePredictionCallback[]) ACChinesePredictionServiceBase.this.listCallbacks.toArray(new ACChinesePredictionService.ACChinesePredictionCallback[0])) {
                aCChinesePredictionCallback.onPredictionFailure(str, i, str2);
            }
        }

        @Override // com.nuance.connect.api.ChinesePredictionService.ChinesePredictionCallback
        public void onPredictionResult(ChinesePredictionService.ChinesePredictionResult chinesePredictionResult) {
            ACChinesePredictionServiceBase.this.log.d("ChinesePredictionCallback.onPredictionResult()");
            if (ACChinesePredictionServiceBase.this.isShutdown) {
                return;
            }
            ACChinesePredictionResultImpl aCChinesePredictionResultImpl = new ACChinesePredictionResultImpl(chinesePredictionResult);
            for (ACChinesePredictionService.ACChinesePredictionCallback aCChinesePredictionCallback : (ACChinesePredictionService.ACChinesePredictionCallback[]) ACChinesePredictionServiceBase.this.listCallbacks.toArray(new ACChinesePredictionService.ACChinesePredictionCallback[0])) {
                aCChinesePredictionCallback.onPredictionResult(aCChinesePredictionResultImpl);
            }
        }

        @Override // com.nuance.connect.api.ChinesePredictionService.ChinesePredictionCallback
        public void onPredictionStatus(int i, String str) {
            ACChinesePredictionServiceBase.this.log.d("ChinesePredictionCallback.onPredictionStatus() code=", Integer.valueOf(i), " description=", str);
            if (ACChinesePredictionServiceBase.this.isShutdown) {
                return;
            }
            for (ACChinesePredictionService.ACChinesePredictionCallback aCChinesePredictionCallback : (ACChinesePredictionService.ACChinesePredictionCallback[]) ACChinesePredictionServiceBase.this.listCallbacks.toArray(new ACChinesePredictionService.ACChinesePredictionCallback[0])) {
                aCChinesePredictionCallback.onPredictionStatus(i, str);
            }
        }
    };

    /* loaded from: classes.dex */
    static class ACChinesePredictionImpl implements ACChinesePredictionService.ACChinesePrediction {
        private ChinesePredictionService.ChinesePrediction chinesePrediction;

        ACChinesePredictionImpl(ChinesePredictionService.ChinesePrediction chinesePrediction) {
            this.chinesePrediction = chinesePrediction;
        }

        @Override // com.nuance.swypeconnect.ac.ACChinesePredictionService.ACChinesePrediction, com.nuance.connect.api.ChinesePredictionService.ChinesePrediction
        public int[] getAttributes() {
            return this.chinesePrediction.getAttributes();
        }

        @Override // com.nuance.swypeconnect.ac.ACChinesePredictionService.ACChinesePrediction, com.nuance.connect.api.ChinesePredictionService.ChinesePrediction
        public String getFullSpell() {
            return this.chinesePrediction.getFullSpell();
        }

        @Override // com.nuance.swypeconnect.ac.ACChinesePredictionService.ACChinesePrediction, com.nuance.connect.api.ChinesePredictionService.ChinesePrediction
        public String getPhrase() {
            return this.chinesePrediction.getPhrase();
        }

        @Override // com.nuance.swypeconnect.ac.ACChinesePredictionService.ACChinesePrediction, com.nuance.connect.api.ChinesePredictionService.ChinesePrediction
        public String getPredictionId() {
            return this.chinesePrediction.getPredictionId();
        }

        @Override // com.nuance.swypeconnect.ac.ACChinesePredictionService.ACChinesePrediction, com.nuance.connect.api.ChinesePredictionService.ChinesePrediction
        public String getSpell() {
            return this.chinesePrediction.getSpell();
        }

        public String toString() {
            return "ACChinesePrediction[" + getPredictionId() + ", " + getPhrase() + ", " + getSpell() + "]";
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class ACChinesePredictionRequestImpl implements ACChinesePredictionService.ACChinesePredictionRequest {
        private ChinesePredictionService.ChinesePredictionRequest request;

        ACChinesePredictionRequestImpl(ChinesePredictionService.ChinesePredictionRequest chinesePredictionRequest) {
            this.request = chinesePredictionRequest;
        }

        @Override // com.nuance.swypeconnect.ac.ACChinesePredictionService.ACChinesePredictionRequest, com.nuance.connect.api.ChinesePredictionService.ChinesePredictionRequest
        public int getCharacterSetId() {
            return this.request.getCharacterSetId();
        }

        @Override // com.nuance.swypeconnect.ac.ACChinesePredictionService.ACChinesePredictionRequest, com.nuance.connect.api.ChinesePredictionService.ChinesePredictionRequest
        public int getLanguageId() {
            return this.request.getLanguageId();
        }

        @Override // com.nuance.swypeconnect.ac.ACChinesePredictionService.ACChinesePredictionRequest, com.nuance.connect.api.ChinesePredictionService.ChinesePredictionRequest
        public byte[] getPredictionData() {
            return this.request.getPredictionData();
        }

        @Override // com.nuance.swypeconnect.ac.ACChinesePredictionService.ACChinesePredictionRequest, com.nuance.connect.api.ChinesePredictionService.ChinesePredictionRequest
        public String getPredictionId() {
            return this.request.getPredictionId();
        }
    }

    /* loaded from: classes.dex */
    static class ACChinesePredictionResultImpl implements ACChinesePredictionService.ACChinesePredictionResult {
        ACChinesePredictionService.ACChinesePrediction[] acChinesePredictions;
        private ChinesePredictionService.ChinesePredictionResult chinesePredictionResult;

        ACChinesePredictionResultImpl(ChinesePredictionService.ChinesePredictionResult chinesePredictionResult) {
            this.chinesePredictionResult = chinesePredictionResult;
            this.acChinesePredictions = new ACChinesePredictionService.ACChinesePrediction[chinesePredictionResult.getPredictionCount()];
            int i = 0;
            for (ChinesePredictionService.ChinesePrediction chinesePrediction : chinesePredictionResult.getPredictions()) {
                this.acChinesePredictions[i] = new ACChinesePredictionImpl(chinesePrediction);
                i++;
            }
        }

        @Override // com.nuance.swypeconnect.ac.ACChinesePredictionService.ACChinesePredictionResult, com.nuance.connect.api.ChinesePredictionService.ChinesePredictionResult
        public int getPredictionCount() {
            return this.chinesePredictionResult.getPredictionCount();
        }

        @Override // com.nuance.swypeconnect.ac.ACChinesePredictionService.ACChinesePredictionResult, com.nuance.connect.api.ChinesePredictionService.ChinesePredictionResult
        public String getPredictionId() {
            return this.chinesePredictionResult.getPredictionId();
        }

        @Override // com.nuance.connect.api.ChinesePredictionService.ChinesePredictionResult
        public ACChinesePredictionService.ACChinesePredictionRequest getPredictionRequest() {
            return new ACChinesePredictionRequestImpl(this.chinesePredictionResult.getPredictionRequest());
        }

        @Override // com.nuance.connect.api.ChinesePredictionService.ChinesePredictionResult
        public ACChinesePredictionService.ACChinesePrediction[] getPredictions() {
            return this.acChinesePredictions;
        }

        @Override // com.nuance.swypeconnect.ac.ACChinesePredictionService.ACChinesePredictionResult, com.nuance.connect.api.ChinesePredictionService.ChinesePredictionResult
        public long predictionTime() {
            return this.chinesePredictionResult.predictionTime();
        }
    }

    @Override // com.nuance.swypeconnect.ac.ACChinesePredictionService
    public void cancelPrediction(String str) {
        this.predictionService.cancelPrediction(str);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.swypeconnect.ac.ACService
    public boolean dependenciesMet() {
        String str;
        try {
            str = this.manager.getCoreVersion(3);
        } catch (ACException e) {
            e.printStackTrace();
            str = null;
        }
        if (str != null) {
            return true;
        }
        this.log.e("Attempting to use Chinese Prediction Service without a Chinese core enabled.");
        return false;
    }

    @Override // com.nuance.swypeconnect.ac.ACChinesePredictionService
    public void endSession() {
        this.manager.getConnect().updateFeatureLastUsed(FeaturesLastUsed.Feature.CCPS, System.currentTimeMillis());
        this.predictionService.endSession();
    }

    @Override // com.nuance.swypeconnect.ac.ACChinesePredictionService
    public int getIdleTimeout() {
        return this.predictionService.getIdleTimeout();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.swypeconnect.ac.ACService
    public String getName() {
        return ACManager.CHINESE_CLOUD_PREDICTION;
    }

    @Override // com.nuance.swypeconnect.ac.ACChinesePredictionService
    public int getPredictionResults() {
        return this.predictionService.getPredictionResults();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void init(ChinesePredictionService chinesePredictionService, PersistentDataStore persistentDataStore, ACManager aCManager) {
        this.manager = aCManager;
        chinesePredictionService.registerCallback(this.predictionCallback);
        this.predictionService = chinesePredictionService;
    }

    @Override // com.nuance.swypeconnect.ac.ACChinesePredictionService
    public void logResultSelection(String str) throws ACException {
        if (this.isShutdown) {
            throw new ACException(105);
        }
        try {
            this.predictionService.logResultSelection(str);
        } catch (ConnectException e) {
            if (e.getReasonCode() != 109) {
                throw new ACException(e.getReasonCode(), e.getMessage());
            }
            throw new ACException(128, "Prediction Request on invalid prediction service");
        }
    }

    @Override // com.nuance.swypeconnect.ac.ACChinesePredictionService
    public void logResultSelection(String str, ACChinesePredictionService.ACChinesePrediction aCChinesePrediction) throws ACException {
        if (this.isShutdown) {
            throw new ACException(105);
        }
        try {
            this.predictionService.logResultSelection(str, aCChinesePrediction);
        } catch (ConnectException e) {
            if (e.getReasonCode() != 109) {
                throw new ACException(e.getReasonCode(), e.getMessage());
            }
            throw new ACException(128, "Prediction Request on invalid prediction service");
        }
    }

    @Override // com.nuance.swypeconnect.ac.ACChinesePredictionService
    public void logResultSelection(String str, String str2, String str3) throws ACException {
        if (this.isShutdown) {
            throw new ACException(105);
        }
        try {
            this.predictionService.logResultSelection(str, str2, str3);
        } catch (ConnectException e) {
            if (e.getReasonCode() != 109) {
                throw new ACException(e.getReasonCode(), e.getMessage());
            }
            throw new ACException(128, "Prediction Request on invalid prediction service");
        }
    }

    @Override // com.nuance.swypeconnect.ac.ACChinesePredictionService
    public void logResultSelection(String str, String str2, String str3, String str4, int[] iArr) throws ACException {
        if (this.isShutdown) {
            throw new ACException(105);
        }
        try {
            this.predictionService.logResultSelection(str, str2, str3, str4, iArr);
        } catch (ConnectException e) {
            if (e.getReasonCode() != 109) {
                throw new ACException(e.getReasonCode(), e.getMessage());
            }
            throw new ACException(128, "Prediction Request on invalid prediction service");
        }
    }

    @Override // com.nuance.swypeconnect.ac.ACChinesePredictionService
    public void registerCallback(ACChinesePredictionService.ACChinesePredictionCallback aCChinesePredictionCallback) {
        this.listCallbacks.add(aCChinesePredictionCallback);
    }

    @Override // com.nuance.swypeconnect.ac.ACChinesePredictionService
    public String requestPrediction(byte[] bArr) throws ACException {
        return requestPrediction(bArr, null);
    }

    @Override // com.nuance.swypeconnect.ac.ACChinesePredictionService
    public String requestPrediction(byte[] bArr, String str) throws ACException {
        if (this.isShutdown) {
            throw new ACException(105);
        }
        try {
            return this.predictionService.requestPrediction(bArr, str);
        } catch (ConnectException e) {
            if (e.getReasonCode() == 109) {
                throw new ACException(128, "Prediction Request when session is not started");
            }
            throw new ACException(e.getReasonCode(), e.getMessage());
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.swypeconnect.ac.ACService
    public boolean requiresDocument(int i) {
        return i == 1 || i == 4;
    }

    @Override // com.nuance.swypeconnect.ac.ACChinesePredictionService
    public void setIdleTimeout(int i) throws ACException {
        if (i <= 0 || i > 60) {
            throw new ACException(122, "Idle Timeout cannot be less then zero or more then ACChinesePredictionService.MAX_IDLE_TIMEOUT");
        }
        try {
            this.predictionService.setIdleTimeout(i);
        } catch (ConnectException e) {
            throw new ACException(e.getReasonCode(), e.getMessage());
        }
    }

    @Override // com.nuance.swypeconnect.ac.ACChinesePredictionService
    public void setPredictionResults(int i) throws ACException {
        if (i < 0 || i > 10) {
            throw new ACException(122, "Prediction Results cannot be less then zero or more then ACChinesePredictionService.MAX_PREDICTION_RESULTS");
        }
        try {
            this.predictionService.setPredictionResults(i);
        } catch (ConnectException e) {
            throw new ACException(e.getReasonCode(), e.getMessage());
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.swypeconnect.ac.ACService
    public void shutdown() {
        this.isShutdown = true;
        unregisterCallbacks();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.swypeconnect.ac.ACService
    public void start() {
        this.isShutdown = false;
    }

    @Override // com.nuance.swypeconnect.ac.ACChinesePredictionService
    public void startSession(int i, int i2) throws ACException {
        if (i <= 0 && i2 < 0) {
            throw new ACException(122, "Both languageId and charactersetId must be proper values");
        }
        try {
            this.predictionService.startSession(i, i2);
        } catch (ConnectException e) {
            throw new ACException(e.getReasonCode(), e.getMessage());
        }
    }

    @Override // com.nuance.swypeconnect.ac.ACChinesePredictionService
    public void unregisterCallback(ACChinesePredictionService.ACChinesePredictionCallback aCChinesePredictionCallback) {
        this.listCallbacks.remove(aCChinesePredictionCallback);
    }

    @Override // com.nuance.swypeconnect.ac.ACChinesePredictionService
    public void unregisterCallbacks() {
        this.listCallbacks.clear();
    }
}
