package com.nuance.input.swypecorelib.usagedata;

import android.graphics.Point;
import java.util.List;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class SessionData {
    private final JSONObject jsonSessionData;
    private final int languageID;
    private final int selectedCandidateCount;
    private final List<Point> tracePoints;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SessionData(JSONObject jsonSessionData, List<Point> tracePoints, int selectedCandidateCount, int languageID) {
        this.jsonSessionData = jsonSessionData;
        this.tracePoints = tracePoints;
        this.selectedCandidateCount = selectedCandidateCount;
        this.languageID = languageID;
    }

    public JSONObject getData() {
        return this.jsonSessionData;
    }

    public int getSelectedCandidateCount() {
        return this.selectedCandidateCount;
    }

    public int getLanguageID() {
        return this.languageID;
    }

    public List<Point> getTracePoints() {
        return this.tracePoints;
    }
}
