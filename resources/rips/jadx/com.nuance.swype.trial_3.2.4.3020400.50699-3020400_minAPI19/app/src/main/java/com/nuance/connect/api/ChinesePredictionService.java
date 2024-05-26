package com.nuance.connect.api;

/* loaded from: classes.dex */
public interface ChinesePredictionService {
    public static final int MAX_IDLE_TIMEOUT = 60;
    public static final int MAX_PREDICTION_RESULTS = 10;

    /* loaded from: classes.dex */
    public interface ChinesePrediction {
        int[] getAttributes();

        String getFullSpell();

        String getPhrase();

        String getPredictionId();

        String getSpell();
    }

    /* loaded from: classes.dex */
    public interface ChinesePredictionCallback {
        void onPredictionCancel(String str);

        void onPredictionFailure(String str, int i, String str2);

        void onPredictionResult(ChinesePredictionResult chinesePredictionResult);

        void onPredictionStatus(int i, String str);
    }

    /* loaded from: classes.dex */
    public interface ChinesePredictionRequest {
        int getCharacterSetId();

        int getLanguageId();

        byte[] getPredictionData();

        String getPredictionId();
    }

    /* loaded from: classes.dex */
    public interface ChinesePredictionResult {
        int getPredictionCount();

        String getPredictionId();

        ChinesePredictionRequest getPredictionRequest();

        ChinesePrediction[] getPredictions();

        long predictionTime();
    }

    void cancelPrediction(String str);

    void endSession();

    int getIdleTimeout();

    int getPredictionResults();

    boolean isSessionActive();

    void logResultSelection(String str) throws ConnectException;

    void logResultSelection(String str, ChinesePrediction chinesePrediction) throws ConnectException;

    void logResultSelection(String str, String str2, String str3) throws ConnectException;

    void logResultSelection(String str, String str2, String str3, String str4, int[] iArr) throws ConnectException;

    void registerCallback(ChinesePredictionCallback chinesePredictionCallback);

    String requestPrediction(byte[] bArr) throws ConnectException;

    String requestPrediction(byte[] bArr, String str) throws ConnectException;

    void setIdleTimeout(int i) throws ConnectException;

    void setPredictionResults(int i) throws ConnectException;

    void startSession(int i, int i2) throws ConnectException;

    void unregisterCallback(ChinesePredictionCallback chinesePredictionCallback);

    void unregisterCallbacks();
}
