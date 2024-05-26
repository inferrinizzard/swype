package com.nuance.connect.sqlite;

/* loaded from: classes.dex */
class ChinesePredictionDataRow {
    final String applicationName;
    final String ccpsVersion;
    final long cloudTime;
    final String countryCode;
    final String predictionId;
    final int resultType;
    final long totalTime;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ChinesePredictionDataRow(String str, String str2, long j, long j2, int i, String str3, String str4) {
        this.predictionId = str;
        this.ccpsVersion = str2;
        this.totalTime = j;
        this.cloudTime = j2;
        this.resultType = i;
        this.applicationName = str3;
        this.countryCode = str4;
    }

    public String toString() {
        return "ChinesePredictionDataRow id=" + this.predictionId + " version=" + this.ccpsVersion + " totalTime=" + this.totalTime + " cloudTime=" + this.cloudTime + " resultType=" + this.resultType + " applicationName=" + this.applicationName + " countryCode=" + this.countryCode;
    }
}
