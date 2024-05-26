package com.nuance.swypeconnect.ac;

import com.nuance.connect.api.ChinesePredictionService;

/* loaded from: classes.dex */
public interface ACChinesePredictionService {
    public static final int MAX_IDLE_TIMEOUT = 60;
    public static final int MAX_PREDICTION_RESULTS = 10;
    public static final int REASON_FAILED_HTTP = 0;
    public static final int REASON_NETWORK_TIMEOUT = 4;
    public static final int REASON_USER_CANCELED = 3;

    /* loaded from: classes.dex */
    public interface ACChinesePrediction extends ChinesePredictionService.ChinesePrediction {
        @Override // com.nuance.connect.api.ChinesePredictionService.ChinesePrediction
        int[] getAttributes();

        @Override // com.nuance.connect.api.ChinesePredictionService.ChinesePrediction
        String getFullSpell();

        @Override // com.nuance.connect.api.ChinesePredictionService.ChinesePrediction
        String getPhrase();

        @Override // com.nuance.connect.api.ChinesePredictionService.ChinesePrediction
        String getPredictionId();

        @Override // com.nuance.connect.api.ChinesePredictionService.ChinesePrediction
        String getSpell();
    }

    /* loaded from: classes.dex */
    public interface ACChinesePredictionCallback {
        void onPredictionCancel(String str);

        void onPredictionFailure(String str, int i, String str2);

        void onPredictionResult(ACChinesePredictionResult aCChinesePredictionResult);

        void onPredictionStatus(int i, String str);
    }

    /* loaded from: classes.dex */
    public interface ACChinesePredictionRequest extends ChinesePredictionService.ChinesePredictionRequest {
        @Override // com.nuance.connect.api.ChinesePredictionService.ChinesePredictionRequest
        int getCharacterSetId();

        @Override // com.nuance.connect.api.ChinesePredictionService.ChinesePredictionRequest
        int getLanguageId();

        @Override // com.nuance.connect.api.ChinesePredictionService.ChinesePredictionRequest
        byte[] getPredictionData();

        @Override // com.nuance.connect.api.ChinesePredictionService.ChinesePredictionRequest
        String getPredictionId();
    }

    /* loaded from: classes.dex */
    public interface ACChinesePredictionResult extends ChinesePredictionService.ChinesePredictionResult {
        @Override // com.nuance.connect.api.ChinesePredictionService.ChinesePredictionResult
        int getPredictionCount();

        @Override // com.nuance.connect.api.ChinesePredictionService.ChinesePredictionResult
        String getPredictionId();

        @Override // com.nuance.connect.api.ChinesePredictionService.ChinesePredictionResult
        ACChinesePredictionRequest getPredictionRequest();

        @Override // com.nuance.connect.api.ChinesePredictionService.ChinesePredictionResult
        ACChinesePrediction[] getPredictions();

        @Override // com.nuance.connect.api.ChinesePredictionService.ChinesePredictionResult
        long predictionTime();
    }

    void cancelPrediction(String str);

    void endSession();

    int getIdleTimeout();

    int getPredictionResults();

    void logResultSelection(String str) throws ACException;

    void logResultSelection(String str, ACChinesePrediction aCChinesePrediction) throws ACException;

    void logResultSelection(String str, String str2, String str3) throws ACException;

    void logResultSelection(String str, String str2, String str3, String str4, int[] iArr) throws ACException;

    void registerCallback(ACChinesePredictionCallback aCChinesePredictionCallback);

    String requestPrediction(byte[] bArr) throws ACException;

    String requestPrediction(byte[] bArr, String str) throws ACException;

    void setIdleTimeout(int i) throws ACException;

    void setPredictionResults(int i) throws ACException;

    void startSession(int i, int i2) throws ACException;

    void unregisterCallback(ACChinesePredictionCallback aCChinesePredictionCallback);

    void unregisterCallbacks();
}
