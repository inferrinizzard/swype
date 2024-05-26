package com.nuance.swype.stats.fulllogging;

import android.content.Context;
import android.graphics.Point;
import android.text.TextUtils;
import com.nuance.input.swypecorelib.Candidates;
import com.nuance.input.swypecorelib.Shift;
import com.nuance.input.swypecorelib.WordCandidate;
import com.nuance.swype.input.KeyboardEx;
import com.nuance.swype.stats.AbstractScribe;
import com.nuance.swype.stats.UsageManager;
import com.nuance.swype.util.CharacterUtilities;
import com.nuance.swypeconnect.ac.ACReportingService;
import com.nuance.swypeconnect.ac.oem_62314.ACReportingLogHelperNuance;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* loaded from: classes.dex */
public class FullUsageScribe extends AbstractScribe implements UsageManager.KeyboardUsageScribe {
    private static final char LOGTAG_CANDIDATES_TYPE_COMPLETIONS = 'C';
    private static final char LOGTAG_CANDIDATES_TYPE_HWR = 'H';
    private static final char LOGTAG_CANDIDATES_TYPE_NEXT_WORD_PREDICTION = 'N';
    private static final char LOGTAG_CANDIDATES_TYPE_RECAPTURE = 'R';
    private static final char LOGTAG_CANDIDATES_TYPE_RECAPTURE_BY_TEXT_SELECTION = 'S';
    private static final char LOGTAG_CANDIDATES_TYPE_SPEECH_ALTERNATES = 'A';
    private static final char LOGTAG_CANDIDATES_TYPE_TAP = 'P';
    private static final char LOGTAG_CANDIDATES_TYPE_TRACE = 'T';
    private static final char LOGTAG_SEPARATOR_CONTEXT_WORD_COUNT = ' ';
    private static final char LOGTAG_SEPARATOR_FIELD = ',';
    private static final char LOGTAG_TYPE_UNKNOWN = 'U';
    private static final int MAX_SUGGESTIONS_COUNT = 5;
    private static final String REPLACEMENT_DIGIT = "#";
    private static final String REPLACEMENT_SEPARATOR_ITEMS = "*:*";
    private static final int SELECTION_LIST_OPTIONS_MAXIMUM = 20;
    private boolean longPressDetected;
    private static final char SEPARATOR_ITEMS = ':';
    private static final String SEPARATOR_ITEMS_STRING = Character.toString(SEPARATOR_ITEMS);
    private static final Matcher DIGIT_REGEX = Pattern.compile("[0-9]").matcher("");

    public FullUsageScribe() {
        this.longPressDetected = false;
    }

    public FullUsageScribe(Context ctx) {
        super(ctx);
        this.longPressDetected = false;
    }

    @Override // com.nuance.swype.stats.UsageManager.KeyboardUsageScribe
    public void recordKeycodeTapped(int keycode) {
        if (this.longPressDetected) {
            sendStat(ACReportingService.ACDataPoints.KEYCODE_LONG, Integer.toString(keycode));
            this.longPressDetected = false;
        }
    }

    @Override // com.nuance.swype.stats.UsageManager.KeyboardUsageScribe
    public void recordTracePath(List<Point> trace, List<Long> eventTime) {
        if (allowedProcess(ACReportingService.ACDataPoints.TRACE_PATH.toString())) {
            int pointsCount = trace.size();
            int timeCount = eventTime.size();
            if (pointsCount > 0 && timeCount == pointsCount) {
                StringBuilder sb = new StringBuilder();
                sb.append(Integer.toHexString(pointsCount));
                for (int i = 0; i < pointsCount; i++) {
                    Point point = trace.get(i);
                    long time = eventTime.get(i).longValue();
                    sb.append(LOGTAG_SEPARATOR_FIELD);
                    sb.append(Integer.toHexString(point.x));
                    sb.append(",");
                    sb.append(Integer.toHexString(point.y));
                    sb.append(",");
                    sb.append(Long.toString(time));
                }
                sendStat(ACReportingService.ACDataPoints.TRACE_PATH, sb.toString());
            }
        }
    }

    @Override // com.nuance.swype.stats.UsageManager.KeyboardUsageScribe
    public void recordDeletedWord(String word) {
        log.v("recordDeletedWord word=" + word);
        sendStat(ACReportingService.ACDataPoints.DELETED_WORD, word);
    }

    @Override // com.nuance.swype.stats.UsageManager.KeyboardUsageScribe
    public void recordKeycodeLongpress(int keycode) {
        log.v("recordKeycodeLongpress keycode=" + keycode);
        this.longPressDetected = keycode >= 0;
    }

    @Override // com.nuance.swype.stats.UsageManager.KeyboardUsageScribe
    public void recordCompletedText(String text) {
        log.v("recordCompletedText text=" + text);
        sendStat(ACReportingService.ACDataPoints.COMPLETED_TEXT, text);
    }

    @Override // com.nuance.swype.stats.UsageManager.KeyboardUsageScribe
    public void recordRecapture(String context, int offset) {
        sendStat(ACReportingService.ACDataPoints.RECAPTURE, Integer.toString(offset));
    }

    @Override // com.nuance.swype.stats.UsageManager.KeyboardUsageScribe
    public void recordKeyboardPageXML(String xml) {
        sendStat(ACReportingService.ACDataPoints.KEYBOARD_XML, xml);
    }

    @Override // com.nuance.swype.stats.UsageManager.KeyboardUsageScribe
    public void recordSelectionListOptions(Candidates candidates) {
        if (allowedProcess(ACReportingService.ACDataPoints.SELECTION_LIST_OPTIONS.toString()) && candidates != null) {
            Candidates suggestions = new Candidates(candidates);
            StringBuilder sb = new StringBuilder();
            sb.append(Integer.toHexString(suggestions.getDefaultCandidateIndex()));
            sb.append(LOGTAG_SEPARATOR_FIELD);
            sb.append(Integer.toHexString(suggestions.getExactCandidateIndex()));
            sb.append(LOGTAG_SEPARATOR_FIELD);
            sb.append(getCandidatesListType(candidates));
            for (int i = 0; i < suggestions.count() && i < 5; i++) {
                String encodedWordCandidate = getEncodedWordCandidateString(suggestions.get(i));
                if (encodedWordCandidate == null) {
                    log.e("Failed to get encoded string for candidate (" + i + ")");
                } else {
                    sb.append(LOGTAG_SEPARATOR_FIELD);
                    sb.append(encodedWordCandidate);
                }
            }
            sendStat(ACReportingService.ACDataPoints.SELECTION_LIST_OPTIONS, sb.toString());
        }
    }

    @Override // com.nuance.swype.stats.UsageManager.KeyboardUsageScribe
    public void recordSelectionListOptionString(List<CharSequence> selection) {
        if (allowedProcess(ACReportingService.ACDataPoints.SELECTION_LIST_OPTIONS.toString()) && selection != null) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < selection.size() && i < 20; i++) {
                sb.append(selection.get(i));
                sb.append(LOGTAG_SEPARATOR_FIELD);
            }
            sendStat(ACReportingService.ACDataPoints.SELECTION_LIST_OPTIONS, sb.toString());
        }
    }

    @Override // com.nuance.swype.stats.UsageManager.KeyboardUsageScribe
    public void recordSelectedWord(String word) {
        sendStat(ACReportingService.ACDataPoints.SELECTION_WORD, word);
    }

    @Override // com.nuance.swype.stats.UsageManager.KeyboardUsageScribe
    public void recordShiftState(Shift.ShiftState state) {
        sendStat(ACReportingService.ACDataPoints.SHIFT_STATE, state.toString());
    }

    @Override // com.nuance.swype.stats.UsageManager.KeyboardUsageScribe
    public void recordSelectionListContext(WordCandidate candidate, String textBuffer) {
        if (allowedProcess(ACReportingService.ACDataPoints.SELECTION_LIST_CONTEXT.toString()) && !TextUtils.isEmpty(textBuffer)) {
            StringBuilder contextBuffer = new StringBuilder();
            for (int i = 0; i < 3; i++) {
                String textBuffer2 = textBuffer.trim();
                if (TextUtils.isEmpty(textBuffer2)) {
                    break;
                }
                StringBuilder aWord = new StringBuilder();
                extractWordBeforeCursor(textBuffer2, aWord);
                if (aWord.length() <= 0) {
                    break;
                }
                if (contextBuffer.length() > 0) {
                    contextBuffer.insert(0, LOGTAG_SEPARATOR_CONTEXT_WORD_COUNT);
                }
                contextBuffer.insert(0, (CharSequence) aWord);
                textBuffer = textBuffer2.substring(0, textBuffer2.length() - aWord.length());
            }
            String encodedWordCandidate = getEncodedWordCandidateString(candidate);
            if (encodedWordCandidate != null) {
                StringBuilder sb = new StringBuilder();
                if (!TextUtils.isEmpty(contextBuffer)) {
                    sb.append(escapeString(maskDigitInString(contextBuffer.toString())));
                }
                sb.append(SEPARATOR_ITEMS);
                sb.append(encodedWordCandidate);
                sendStat(ACReportingService.ACDataPoints.SELECTION_LIST_CONTEXT, sb.toString());
            }
        }
    }

    @Override // com.nuance.swype.stats.UsageManager.KeyboardUsageScribe
    public void recordKeyboardLayerChange(KeyboardEx.KeyboardLayerType from, KeyboardEx.KeyboardLayerType to) {
        ACReportingLogHelperNuance helper = getHelper();
        if (helper != null) {
            helper.recordKeyboardLayerChange(String.valueOf(from), String.valueOf(to));
        }
    }

    @Override // com.nuance.swype.stats.UsageManager.KeyboardUsageScribe
    public void recordAlternativeText(String text) {
        ACReportingLogHelperNuance helper = getHelper();
        if (helper != null && text != null && text.length() > 0) {
            helper.recordAlternativeText(text);
        }
    }

    @Override // com.nuance.swype.stats.UsageManager.KeyboardUsageScribe
    public void recordInlineText(String text) {
        ACReportingLogHelperNuance helper = getHelper();
        if (helper != null && text != null && text.length() > 0) {
            helper.recordInlineText(text);
        }
    }

    @Override // com.nuance.swype.stats.UsageManager.KeyboardUsageScribe
    public void recordShiftMargin(int margin) {
        ACReportingLogHelperNuance helper = getHelper();
        if (helper != null) {
            helper.recordShiftMargin(margin);
        }
    }

    @Override // com.nuance.swype.stats.UsageManager.KeyboardUsageScribe
    public void recordTextBuffer(String text) {
        if (allowedProcess(ACReportingService.ACDataPoints.TEXT_BUFFER.toString())) {
            log.v("recordTextBuffer text=" + text);
            sendStat(ACReportingService.ACDataPoints.TEXT_BUFFER, text);
        }
    }

    private void extractWordBeforeCursor(CharSequence seqBeforCusor, StringBuilder word) {
        word.setLength(0);
        if (seqBeforCusor != null && seqBeforCusor.length() > 0) {
            int end = seqBeforCusor.length() - 1;
            while (end >= 0 && CharacterUtilities.isWhiteSpace(seqBeforCusor.charAt(end))) {
                end--;
            }
            if (end >= 0) {
                boolean valideSeq = true;
                int start = end - 1;
                while (true) {
                    if (start < 0 || CharacterUtilities.isWhiteSpace(seqBeforCusor.charAt(start))) {
                        break;
                    }
                    if (!CharacterUtilities.isValidChineseChar(seqBeforCusor.charAt(start))) {
                        start--;
                    } else {
                        valideSeq = false;
                        break;
                    }
                }
                if (valideSeq) {
                    word.append(seqBeforCusor.subSequence(start + 1, end + 1));
                }
            }
        }
    }

    private String escapeString(String string) {
        return string.replace(SEPARATOR_ITEMS_STRING, REPLACEMENT_SEPARATOR_ITEMS);
    }

    private String maskDigitInString(String string) {
        return DIGIT_REGEX.reset(string).replaceAll(REPLACEMENT_DIGIT);
    }

    private String getEncodedWordCandidateString(WordCandidate candidate) {
        if (candidate == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        String sourceHexString = Integer.toHexString(candidate.source().value());
        int sourceHexStringLength = sourceHexString.length();
        sb.append(candidate.isSpellCorrected() ? '1' : '0');
        sb.append(candidate.isTerminal() ? '1' : '0');
        sb.append(candidate.isCompletion() ? '1' : '0');
        if (sourceHexStringLength > 1) {
            sb.append(SEPARATOR_ITEMS);
        }
        sb.append(sourceHexString);
        if (sourceHexStringLength > 1) {
            sb.append(SEPARATOR_ITEMS);
        }
        sb.append(escapeString(maskDigitInString(candidate.toString())));
        return sb.toString();
    }

    private char getCandidatesListType(Candidates candidates) {
        if (candidates == null) {
            return LOGTAG_TYPE_UNKNOWN;
        }
        switch (candidates.source()) {
            case TRACE:
                return LOGTAG_CANDIDATES_TYPE_TRACE;
            case TAP:
                return LOGTAG_CANDIDATES_TYPE_TAP;
            case RECAPTURE:
                return LOGTAG_CANDIDATES_TYPE_RECAPTURE;
            case RECAPTURE_BY_TEXT_SELECTION:
                return LOGTAG_CANDIDATES_TYPE_RECAPTURE_BY_TEXT_SELECTION;
            case NEXT_WORD_PREDICTION:
                return LOGTAG_CANDIDATES_TYPE_NEXT_WORD_PREDICTION;
            case HWR:
                return LOGTAG_CANDIDATES_TYPE_HWR;
            case SPEECH_ALTERNATES:
                return LOGTAG_CANDIDATES_TYPE_SPEECH_ALTERNATES;
            case COMPLETIONS:
                return LOGTAG_CANDIDATES_TYPE_COMPLETIONS;
            default:
                return LOGTAG_TYPE_UNKNOWN;
        }
    }

    @Override // com.nuance.swype.stats.UsageManager.KeyboardUsageScribe
    public void recordActiveWord(String candidate, String spelling, String prefix) {
        if (allowedProcess(ACReportingService.ACDataPoints.ACTIVE_WORD.toString())) {
            String activeWord = "";
            if (candidate != null) {
                activeWord = "candidate:" + candidate;
            }
            if (spelling != null) {
                activeWord = activeWord + "|spell:" + spelling;
            }
            if (prefix != null) {
                activeWord = activeWord + "|prefix:" + prefix;
            }
            sendStat(ACReportingService.ACDataPoints.ACTIVE_WORD, activeWord);
        }
    }

    @Override // com.nuance.swype.stats.UsageManager.KeyboardUsageScribe
    public void recordCommittedSentence(String sentence) {
        if (allowedProcess(ACReportingService.ACDataPoints.COMMITTED_SENTENCE.toString())) {
            sendStatFiltered(ACReportingService.ACDataPoints.COMMITTED_SENTENCE, sentence);
        }
    }

    @Override // com.nuance.swype.stats.UsageManager.KeyboardUsageScribe
    public void recordSetContext(String context) {
        if (allowedProcess(ACReportingService.ACDataPoints.SET_CONTEXT.toString())) {
            sendStatFiltered(ACReportingService.ACDataPoints.SET_CONTEXT, context);
        }
    }

    @Override // com.nuance.swype.stats.UsageManager.KeyboardUsageScribe
    public void recordGestureType(String type) {
        if (allowedProcess(ACReportingService.ACDataPoints.GESTURE_TYPE.toString())) {
            log.v("recordGestureType type=" + type);
            sendStat(ACReportingService.ACDataPoints.GESTURE_TYPE, type);
        }
    }

    @Override // com.nuance.swype.stats.UsageManager.KeyboardUsageScribe
    public void recordUsedTimeSelectionListDisplay(String name, String time) {
        if (allowedProcess(ACReportingService.ACDataPoints.SELECTION_LIST_DISPLAY.toString())) {
            sendStat(ACReportingService.ACDataPoints.SELECTION_LIST_DISPLAY, name, time);
        }
    }

    @Override // com.nuance.swype.stats.UsageManager.KeyboardUsageScribe
    public void recordInitialLocaleSetting(String localeSetting) {
        if (allowedProcess(ACReportingService.ACDataPoints.INITIAL_LOCALE_SETTING.toString())) {
            sendStat(ACReportingService.ACDataPoints.INITIAL_LOCALE_SETTING, localeSetting);
        }
    }

    @Override // com.nuance.swype.stats.UsageManager.KeyboardUsageScribe
    public void recordAppInstallRemove(String appId, String Event) {
        log.v("recordAppInstallRemove:" + appId + Event);
    }

    @Override // com.nuance.swype.stats.UsageManager.KeyboardUsageScribe
    public String filterInputBuffer(String buffer) {
        return buffer;
    }

    @Override // com.nuance.swype.stats.UsageManager.KeyboardUsageScribe
    public void recordWordWCLDataPoint(String json) {
        if (allowedProcess(ACReportingService.ACDataPoints.WCL_USE_TRACKING)) {
            log.d("recordWordWCLDataPoint: ", this);
            sendStat(ACReportingService.ACDataPoints.WCL_USE_TRACKING, json);
        }
    }
}
