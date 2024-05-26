package com.nuance.nmdp.speechkit;

/* loaded from: classes.dex */
final class SpeechErrorImpl implements SpeechError {
    private final int _errorCode;
    private final String _errorDetail;
    private final String _suggestion;

    public SpeechErrorImpl(int errorCode, String errorDetail, String suggestion) {
        this._errorCode = errorCode;
        this._suggestion = suggestion;
        this._errorDetail = errorDetail == null ? getDefaultErrorText(errorCode) : errorDetail;
    }

    @Override // com.nuance.nmdp.speechkit.SpeechError
    public final int getErrorCode() {
        return this._errorCode;
    }

    @Override // com.nuance.nmdp.speechkit.SpeechError
    public final String getErrorDetail() {
        return this._errorDetail;
    }

    @Override // com.nuance.nmdp.speechkit.SpeechError
    public final String getSuggestion() {
        return this._suggestion;
    }

    private static String getDefaultErrorText(int errorCode) {
        switch (errorCode) {
            case 1:
                return "Failed to connect to speech server.";
            case 2:
                return "Please retry your query.";
            case 3:
            case 4:
            default:
                return "An error occurred.";
            case 5:
                return "Query cancelled.";
        }
    }
}
