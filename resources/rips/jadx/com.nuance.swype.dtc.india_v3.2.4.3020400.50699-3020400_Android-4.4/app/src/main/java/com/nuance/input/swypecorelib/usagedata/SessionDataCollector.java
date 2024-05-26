package com.nuance.input.swypecorelib.usagedata;

import com.facebook.internal.ServerProtocol;
import com.localytics.android.BuildConfig;
import com.nuance.input.swypecorelib.Candidates;
import com.nuance.input.swypecorelib.WordCandidate;
import com.nuance.swype.input.UserPreferences;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class SessionDataCollector extends SessionDataCollectorAbstract {
    private static final String TAG = "SessionDataCollector";
    private boolean autoCorrectionEnabled;
    private final String coreVersion;
    private int kdbID;
    private int keyboardHeight;
    private int keyboardWidth;
    private int languageID;
    private String ldbVersion;
    private int recapturedEditCount;
    private long sessionEnd;
    private long sessionStart;
    private boolean wasRecaptured;
    private List<SessionDataSelectedCandidate> sessionDataSelectedCandidates = new ArrayList();
    private SessionDataTracePoints tracePoints = new SessionDataTracePoints();
    private String dataPointVersion = BuildConfig.VERSION_NAME;

    public SessionDataCollector(String coreVersion) {
        this.coreVersion = coreVersion;
    }

    public SessionData getDetailSessionData() {
        JSONObject jsonObject = getBaseData();
        try {
            jsonObject.put("words", getDetailWords());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new SessionData(jsonObject, this.tracePoints.getPoints(), this.sessionDataSelectedCandidates.size(), this.languageID);
    }

    @Override // com.nuance.input.swypecorelib.usagedata.SessionDataCollectorAbstract
    public SessionData getSessionData() {
        JSONObject jsonObject = getBaseData();
        try {
            jsonObject.put("Wordcount", this.sessionDataSelectedCandidates.size());
            jsonObject.put("Words", getCompactFormatCandidate(this.sessionDataSelectedCandidates));
            jsonObject.put(ServerProtocol.FALLBACK_DIALOG_PARAM_VERSION, getDataPointVersion());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new SessionData(jsonObject, this.tracePoints.getPoints(), this.sessionDataSelectedCandidates.size(), this.languageID);
    }

    @Override // com.nuance.input.swypecorelib.usagedata.SessionDataChangedListener
    public void onStartInputSession() {
        long currentTimeMillis = System.currentTimeMillis();
        this.sessionEnd = currentTimeMillis;
        this.sessionStart = currentTimeMillis;
        this.sessionDataSelectedCandidates = new ArrayList();
        this.tracePoints = new SessionDataTracePoints();
        this.wasRecaptured = false;
        this.recapturedEditCount = 0;
    }

    @Override // com.nuance.input.swypecorelib.usagedata.SessionDataChangedListener
    public void onFinishInputSession() {
        this.sessionEnd = System.currentTimeMillis();
    }

    @Override // com.nuance.input.swypecorelib.usagedata.SessionDataChangedListener
    public void onChangeLanguage(int languageID, String ldbVersion) {
        this.languageID = languageID;
        this.ldbVersion = ldbVersion;
    }

    @Override // com.nuance.input.swypecorelib.usagedata.SessionDataChangedListener
    public void onChangeKeyboard(int kdbID, int width, int height) {
        this.keyboardWidth = width;
        this.keyboardHeight = height;
        this.kdbID = kdbID;
    }

    @Override // com.nuance.input.swypecorelib.usagedata.SessionDataChangedListener
    public void onRecapture(char[] word) {
        this.wasRecaptured = true;
    }

    @Override // com.nuance.input.swypecorelib.usagedata.SessionDataChangedListener
    public void onRecaptureEdit(char[] word) {
        this.recapturedEditCount++;
    }

    @Override // com.nuance.input.swypecorelib.usagedata.SessionDataChangedListener
    public void onCandidateSelected(WordCandidate candidate, Candidates candidates, boolean autoAccepted) {
        addSelectedCandidate(candidate, candidates, autoAccepted);
    }

    @Override // com.nuance.input.swypecorelib.usagedata.SessionDataChangedListener
    public void onAutoCorrectionEnabled(boolean enabled) {
        this.autoCorrectionEnabled = enabled;
    }

    @Override // com.nuance.input.swypecorelib.usagedata.SessionDataChangedListener
    public void onTraced(float[] xCoords, float[] yCoords) {
        this.tracePoints.add(xCoords, yCoords);
    }

    private void addSelectedCandidate(WordCandidate selectedCandidate, Candidates candidates, boolean autoAccepted) {
        SessionDataSelectedCandidate candidate = new SessionDataSelectedCandidate(selectedCandidate);
        candidate.autoAccepted = autoAccepted;
        candidate.defaultCandidate = candidates.getDefaultCandidate();
        candidate.exactCandidate = candidates.getExactCandidate();
        candidate.candidatesSource = candidates.source();
        this.sessionDataSelectedCandidates.add(candidate);
    }

    private JSONObject getBaseData() {
        JSONObject jsonObject = new JSONObject();
        try {
            if (this.sessionStart == this.sessionEnd) {
                this.sessionEnd = System.currentTimeMillis();
            }
            jsonObject.put("CoreVersion", this.coreVersion);
            jsonObject.put("LanguageID", this.languageID);
            jsonObject.put("StartSession", this.sessionStart);
            jsonObject.put("EndSession", this.sessionEnd);
            jsonObject.put("Duration", this.sessionEnd - this.sessionStart);
            jsonObject.put("LDBVersion", this.ldbVersion);
            jsonObject.put("DLMMaturity", 15);
            jsonObject.put("Edited", this.recapturedEditCount > 0 ? "yes" : "no");
            jsonObject.put("auto_correction", this.autoCorrectionEnabled ? "on" : "off");
            jsonObject.put("SelListSetting", this.autoCorrectionEnabled ? "high" : UserPreferences.TRACE_FILTER_LOW);
            jsonObject.put("KeyboardHeight", this.keyboardHeight);
            jsonObject.put("KeyboardWidth", this.keyboardWidth);
            jsonObject.put("Recaptured", this.wasRecaptured);
            jsonObject.put("kdbID", this.kdbID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private JSONArray getDetailWords() throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for (SessionDataSelectedCandidate selectedCandidate : this.sessionDataSelectedCandidates) {
            jsonArray.put(formatDetailCandidate(selectedCandidate));
        }
        return jsonArray;
    }

    private JSONObject formatDetailCandidate(SessionDataSelectedCandidate candidate) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        WordCandidate selectedCandidate = candidate.getSelectedCandidate();
        WordCandidate exactCandidate = candidate.getExactCandidate();
        jsonObject.put("InputType", (int) getWordSourceToType(candidate.getCandidatesSource()));
        jsonObject.put("AcceptType", candidate.isAutoAccepted() ? 65 : 83);
        jsonObject.put("ResultantWordType", resultantWordType(candidate.getSelectedCandidate(), candidate.getCandidatesSource()));
        jsonObject.put("SelectionListIndex", selectedCandidate.id());
        jsonObject.put("SelectionDefault", candidate.getDefaultCandidate().id());
        jsonObject.put("CharCountTapped", exactCandidate.length());
        jsonObject.put("CharCountResultant", selectedCandidate.length());
        jsonObject.put("EditDistance", minEditDistance(exactCandidate.toString(), selectedCandidate.toString()));
        jsonObject.put("word", candidate.getSelectedCandidate().toString());
        return jsonObject;
    }

    private String getCompactFormatCandidate(List<SessionDataSelectedCandidate> candidates) {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < candidates.size(); i++) {
            SessionDataSelectedCandidate selectedCandidate = candidates.get(i);
            buffer.append(formatCompactCandidate(selectedCandidate));
            if (i + 1 < candidates.size()) {
                buffer.append('|');
            }
        }
        return buffer.toString();
    }

    private String formatCompactCandidate(SessionDataSelectedCandidate candidate) {
        StringBuilder buffer = new StringBuilder();
        WordCandidate selectedCandidate = candidate.getSelectedCandidate();
        char wordSourceInputType = getWordSourceToType(candidate.getCandidatesSource());
        buffer.append(wordSourceInputType);
        buffer.append(candidate.isAutoAccepted() ? 'A' : 'S');
        String resultantWordType = resultantWordType(candidate.getSelectedCandidate(), candidate.getCandidatesSource());
        buffer.append(resultantWordType);
        buffer.append(String.format(Locale.ENGLISH, "%02d", Integer.valueOf(selectedCandidate.id())));
        int defaultSelection = getSelectionDefault(candidate.getCandidatesSource(), candidate);
        buffer.append(String.format(Locale.ENGLISH, "%02d", Integer.valueOf(defaultSelection)));
        int charCountTapped = getCharCountTapped(candidate.getCandidatesSource(), candidate.getExactCandidate().toString());
        buffer.append(String.format(Locale.ENGLISH, "%02d", Integer.valueOf(charCountTapped)));
        int charCountResultant = getCharCountResultant(selectedCandidate);
        buffer.append(String.format(Locale.ENGLISH, "%02d", Integer.valueOf(charCountResultant)));
        int editDistance = getEditDistance(resultantWordType, charCountTapped, charCountResultant, candidate.getExactCandidate().toString(), selectedCandidate.toString());
        buffer.append(String.format(Locale.ENGLISH, "%02d", Integer.valueOf(editDistance)));
        buffer.append('*');
        buffer.append(candidate.getSelectedCandidate().toString());
        return buffer.toString();
    }

    public int getSelectionDefault(Candidates.Source source, SessionDataSelectedCandidate candidate) {
        return (candidate == null || !candidate.getSelectedCandidate().isDefault() || source == Candidates.Source.NEXT_WORD_PREDICTION) ? 0 : 1;
    }

    public char getWordSourceToType(Candidates.Source source) {
        switch (source) {
            case TAP:
                return 'T';
            case TRACE:
                return 'S';
            case RECAPTURE:
            case RECAPTURE_BY_TEXT_SELECTION:
                return 'R';
            case NEXT_WORD_PREDICTION:
                return 'P';
            default:
                return 'X';
        }
    }

    public String resultantWordType(WordCandidate candidate, Candidates.Source source) {
        if (source == Candidates.Source.TRACE) {
            return "Sw";
        }
        if (source == Candidates.Source.NEXT_WORD_PREDICTION) {
            return "Nw";
        }
        if (candidate.isExact()) {
            return "Ex";
        }
        if (candidate.contextKillLength() > 0) {
            return "Mw";
        }
        if (candidate.isSpellCorrected() || (candidate.isDefault() && !candidate.isExact())) {
            return "Cr";
        }
        if (source == Candidates.Source.RECAPTURE_BY_TEXT_SELECTION || candidate.isCompletion() || !candidate.isExact()) {
            return "Cp";
        }
        return "XX";
    }

    public int getCharCountTapped(Candidates.Source source, String candidate) {
        if (candidate == null || candidate.length() <= 0 || source == Candidates.Source.TRACE || source == Candidates.Source.NEXT_WORD_PREDICTION) {
            return 0;
        }
        return candidate.length();
    }

    public int getCharCountResultant(WordCandidate candidate) {
        if (candidate == null || candidate.length() <= 0) {
            return 0;
        }
        return candidate.length();
    }

    public int getEditDistance(String resultantWordType, int charCountTapped, int charCountResultant, String exactCandidate, String selectedCandidate) {
        if (resultantWordType.equals("Sw") || resultantWordType.equals("Nw")) {
            return 0;
        }
        if (resultantWordType.equals("Cp")) {
            return Math.abs(charCountTapped - charCountResultant);
        }
        return minEditDistance(exactCandidate, selectedCandidate);
    }

    public String getDataPointVersion() {
        return this.dataPointVersion;
    }

    private int minEditDistance(String word1, String word2) {
        char[] chars1 = word1.toCharArray();
        char[] chars2 = word2.toCharArray();
        int m = chars1.length;
        int n = chars2.length;
        int[][] editDistance = (int[][]) Array.newInstance((Class<?>) Integer.TYPE, m + 1, n + 1);
        for (int i = 0; i <= m; i++) {
            for (int j = 0; j <= n; j++) {
                if (i == 0) {
                    editDistance[i][j] = j;
                } else if (j == 0) {
                    editDistance[i][j] = i;
                } else if (chars1[i - 1] == chars2[j - 1]) {
                    editDistance[i][j] = editDistance[i - 1][j - 1];
                } else {
                    editDistance[i][j] = Math.min(Math.min(editDistance[i - 1][j], editDistance[i][j - 1]), editDistance[i - 1][j - 1]) + 1;
                }
            }
        }
        return editDistance[m][n];
    }
}
