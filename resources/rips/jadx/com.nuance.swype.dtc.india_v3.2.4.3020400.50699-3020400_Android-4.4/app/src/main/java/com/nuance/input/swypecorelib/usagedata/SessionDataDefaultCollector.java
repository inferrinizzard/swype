package com.nuance.input.swypecorelib.usagedata;

import com.nuance.input.swypecorelib.Candidates;
import com.nuance.input.swypecorelib.WordCandidate;

/* loaded from: classes.dex */
public class SessionDataDefaultCollector extends SessionDataCollectorAbstract {
    @Override // com.nuance.input.swypecorelib.usagedata.SessionDataCollectorAbstract
    public SessionData getSessionData() {
        return new SessionData(null, null, 0, 0);
    }

    @Override // com.nuance.input.swypecorelib.usagedata.SessionDataChangedListener
    public void onStartInputSession() {
    }

    @Override // com.nuance.input.swypecorelib.usagedata.SessionDataChangedListener
    public void onFinishInputSession() {
    }

    @Override // com.nuance.input.swypecorelib.usagedata.SessionDataChangedListener
    public void onChangeLanguage(int languageID, String ldbVersion) {
    }

    @Override // com.nuance.input.swypecorelib.usagedata.SessionDataChangedListener
    public void onChangeKeyboard(int kdbID, int width, int height) {
    }

    @Override // com.nuance.input.swypecorelib.usagedata.SessionDataChangedListener
    public void onRecapture(char[] word) {
    }

    @Override // com.nuance.input.swypecorelib.usagedata.SessionDataChangedListener
    public void onRecaptureEdit(char[] word) {
    }

    @Override // com.nuance.input.swypecorelib.usagedata.SessionDataChangedListener
    public void onCandidateSelected(WordCandidate candidate, Candidates candidates, boolean autoAccepted) {
    }

    @Override // com.nuance.input.swypecorelib.usagedata.SessionDataChangedListener
    public void onAutoCorrectionEnabled(boolean endable) {
    }

    @Override // com.nuance.input.swypecorelib.usagedata.SessionDataChangedListener
    public void onTraced(float[] xCoords, float[] yCoords) {
    }
}
