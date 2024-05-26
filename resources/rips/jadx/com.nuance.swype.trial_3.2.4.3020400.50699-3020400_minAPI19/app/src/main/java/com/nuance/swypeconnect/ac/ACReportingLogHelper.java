package com.nuance.swypeconnect.ac;

import android.annotation.SuppressLint;
import android.util.Patterns;
import android.view.inputmethod.EditorInfo;
import com.nuance.connect.util.Logger;
import com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.xmlResults.XMLResultsHandler;
import com.nuance.swype.input.hardkey.HardKeyboardManager;
import com.nuance.swypeconnect.ac.ACLanguage;
import com.nuance.swypeconnect.ac.ACReportingService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.regex.Pattern;

/* loaded from: classes.dex */
public class ACReportingLogHelper {
    private static final int MAX_CONTEXT_COUNT = 3;
    private static final int MAX_SEARCH_QUERY = 200;
    private static final String NUMBER_REPLACE = "#";
    private static final char SELECTION_LIST_SEPARATOR_FIELD = ' ';
    private static final char SEPARATOR_FIELD = ',';
    private static final char SEPARATOR_ITEMS = ':';
    private ACLanguage language;
    private boolean verifiedSearch;
    private ACReportingService.ACReportingWriter writer;
    private static final Logger.Log developerLog = Logger.getLog(Logger.LoggerType.DEVELOPER);
    private static final Logger.Log oemLog = Logger.getLog(Logger.LoggerType.OEM);
    private static final Pattern NUMBER_REMOVE_PATTERN = Pattern.compile("[0-9]");
    private static final Pattern LITE_REMOVE_PATTERN = Pattern.compile("[\\d\\-\\(\\)\\.]{4,}");
    private EditorInfo currentInfo = null;
    private long lastMark = 0;
    private final ACLanguage.Listener listener = new ACLanguage.Listener() { // from class: com.nuance.swypeconnect.ac.ACReportingLogHelper.1
        @Override // com.nuance.swypeconnect.ac.ACLanguage.Listener
        public void onFinishInput(String str, ACLanguage.InputSessionState inputSessionState) {
            ACReportingLogHelper.this.processBuffer(str, inputSessionState);
            ACReportingLogHelper.this.logTrackRates();
            ACReportingLogHelper.this.lastMark = System.currentTimeMillis();
        }

        @Override // com.nuance.swypeconnect.ac.ACLanguage.Listener
        public void onLanguageChange(int[] iArr, int i) {
            ACReportingLogHelper.this.recordLanguage(iArr, i);
        }

        @Override // com.nuance.swypeconnect.ac.ACLanguage.Listener
        public void onLocale(Locale locale) {
            ACReportingLogHelper.this.recordLocale(locale);
        }

        @Override // com.nuance.swypeconnect.ac.ACLanguage.Listener
        public void onStartInput(EditorInfo editorInfo) {
            ACReportingLogHelper.this.logTrackRates();
            ACReportingLogHelper.this.lastMark = System.currentTimeMillis();
            ACReportingLogHelper.this.verifiedSearch = false;
            ACReportingLogHelper.this.currentInputState = ACReportingService.InputType.UNKNOWN;
            synchronized (ACReportingLogHelper.this) {
                ACReportingLogHelper.this.currentInfo = editorInfo;
            }
        }
    };
    private ACReportingService.InputType currentInputState = ACReportingService.InputType.UNKNOWN;
    private final WPMTracker swypeTracker = new WPMTracker();
    private final WPMTracker tapTracker = new WPMTracker();
    private final WPMTracker spokenTracker = new WPMTracker();
    private final WPMTracker writeTracker = new WPMTracker();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class InputFieldInfo {
        private final EditorInfo mEditorInfo;

        InputFieldInfo(EditorInfo editorInfo) {
            this.mEditorInfo = editorInfo;
        }

        private boolean isLikelySearchField() {
            boolean z = isWebSearchField();
            if (isURLField()) {
                return true;
            }
            return z;
        }

        private boolean isLikelyWebTextEmail() {
            return getInputVariant() == 208;
        }

        private boolean isLikelyWebTextPassword() {
            return getInputVariant() == 224;
        }

        public String getFieldName() {
            return this.mEditorInfo.fieldName != null ? this.mEditorInfo.fieldName : "null";
        }

        public int getImeActionType() {
            return this.mEditorInfo.imeOptions & 1073742079;
        }

        public int getInputClass() {
            return this.mEditorInfo.inputType & 15;
        }

        public int getInputVariant() {
            return this.mEditorInfo.inputType & 4080;
        }

        public boolean isActionGoField() {
            return getImeActionType() == 2;
        }

        public boolean isDateTimeField() {
            return getInputClass() == 4;
        }

        public boolean isEmailAddressField() {
            return (isInputTextClass() && getInputVariant() == 32) || isLikelyWebTextEmail();
        }

        public boolean isEmailSubjectField() {
            return isInputTextClass() && getInputVariant() == 48;
        }

        public boolean isInputTextClass() {
            return getInputClass() == 1 || !isKnownClass();
        }

        public boolean isKnownClass() {
            int inputClass = getInputClass();
            return inputClass == 1 || inputClass == 4 || inputClass == 2 || inputClass == 3;
        }

        public boolean isLongMessageField() {
            return isInputTextClass() && getInputVariant() == 80;
        }

        public boolean isMultilineField() {
            return (this.mEditorInfo.inputType & HardKeyboardManager.META_META_LEFT_ON) != 0;
        }

        public boolean isNameField() {
            return isInputTextClass() && getInputVariant() == 96;
        }

        public boolean isNumberField() {
            return getInputClass() == 2;
        }

        @SuppressLint({"InlinedApi"})
        public boolean isNumericPasswordField() {
            return isNumberField() && getInputVariant() == 16;
        }

        public boolean isPasswordField() {
            return isTextPasswordField() || isNumericPasswordField();
        }

        public boolean isPhoneNumberField() {
            return getInputClass() == 3;
        }

        public boolean isPhoneticField() {
            return isInputTextClass() && getInputVariant() == 192;
        }

        public boolean isPostalAddress() {
            return isInputTextClass() && getInputVariant() == 112;
        }

        public boolean isSearchField() {
            return isLikelySearchField();
        }

        public boolean isShortMessageField() {
            return isInputTextClass() && getInputVariant() == 64;
        }

        public boolean isTextPasswordField() {
            return (isInputTextClass() && (getInputVariant() == 128 || getInputVariant() == 144)) || isLikelyWebTextPassword();
        }

        public boolean isURLField() {
            return isInputTextClass() && getInputVariant() == 16;
        }

        public boolean isWebEditText() {
            return isInputTextClass() && getInputVariant() == 160;
        }

        public boolean isWebSearchField() {
            return getImeActionType() == 3;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class WPMTracker {
        private int sessionDuration;
        private int wordCount;

        public WPMTracker() {
            reset();
        }

        public void addTimeToSession(long j) {
            this.sessionDuration = (int) (this.sessionDuration + (System.currentTimeMillis() - j));
        }

        public int getDuration() {
            return this.sessionDuration;
        }

        public float getWPM() {
            if (hasTrackingInformation()) {
                return (this.wordCount * 60000.0f) / this.sessionDuration;
            }
            return 0.0f;
        }

        public int getWordCount() {
            return this.wordCount;
        }

        public boolean hasTrackingInformation() {
            return this.wordCount > 1;
        }

        public void incrementWordCount(int i) {
            this.wordCount += i;
        }

        public final void reset() {
            this.sessionDuration = 0;
            this.wordCount = 0;
        }
    }

    private String filterText(String str) {
        if (str == null) {
            return null;
        }
        return NUMBER_REMOVE_PATTERN.matcher(str).replaceAll(NUMBER_REPLACE);
    }

    private String filterTextLite(String str) {
        if (str == null) {
            return null;
        }
        return LITE_REMOVE_PATTERN.matcher(str).replaceAll(NUMBER_REPLACE);
    }

    private String filterUrls(String str) {
        return Patterns.WEB_URL.matcher(str).replaceAll("<URL>");
    }

    private synchronized String getDefaultName() {
        return (this.currentInfo == null || this.currentInfo.packageName == null) ? String.valueOf((Object) null) : this.currentInfo.packageName;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void logTrackRates() {
        if (this.swypeTracker.hasTrackingInformation()) {
            trackSwyped(this.swypeTracker.getWordCount(), this.swypeTracker.getDuration());
        }
        this.swypeTracker.reset();
        if (this.tapTracker.hasTrackingInformation()) {
            trackTapped(this.tapTracker.getWordCount(), this.tapTracker.getDuration());
        }
        this.tapTracker.reset();
        if (this.writeTracker.hasTrackingInformation()) {
            trackWritten(this.writeTracker.getWordCount(), this.writeTracker.getDuration());
        }
        this.writeTracker.reset();
        if (this.spokenTracker.hasTrackingInformation()) {
            trackSpoken(this.spokenTracker.getWordCount(), this.spokenTracker.getDuration());
        }
        this.spokenTracker.reset();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void disable() {
        this.language.unregisterListener(this.listener);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void enable() {
        this.swypeTracker.reset();
        this.tapTracker.reset();
        this.spokenTracker.reset();
        this.writeTracker.reset();
        this.currentInputState = ACReportingService.InputType.UNKNOWN;
        this.language.registerListener(this.listener);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void init(ACReportingService.ACReportingWriter aCReportingWriter, ACLanguage aCLanguage) {
        this.writer = aCReportingWriter;
        this.language = aCLanguage;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void mark() {
        switch (this.currentInputState) {
            case TAPPED:
                this.tapTracker.addTimeToSession(this.lastMark);
                break;
            case SWYPED:
                this.swypeTracker.addTimeToSession(this.lastMark);
                break;
            case HANDWRITTEN:
                this.writeTracker.addTimeToSession(this.lastMark);
                break;
            case SPOKEN:
                this.spokenTracker.addTimeToSession(this.lastMark);
                break;
            case UNKNOWN:
                break;
            default:
                oemLog.e("Unknown input state");
                break;
        }
        this.lastMark = System.currentTimeMillis();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void mark(int i) {
        this.currentInputState = ACReportingService.InputType.from(i);
        mark();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void mark(int i, int i2) {
        mark(i);
        switch (this.currentInputState) {
            case TAPPED:
                this.tapTracker.incrementWordCount(i2);
                this.writer.logAggregate(ACReportingService.ACDataPoints.WORD_TAPPED_COUNT.toString(), getDefaultName(), i2, 0.0d, null, null);
                return;
            case SWYPED:
                this.swypeTracker.incrementWordCount(i2);
                this.writer.logAggregate(ACReportingService.ACDataPoints.WORD_SWYPED_COUNT.toString(), getDefaultName(), i2, 0.0d, null, null);
                return;
            case HANDWRITTEN:
                this.writeTracker.incrementWordCount(i2);
                this.writer.logAggregate(ACReportingService.ACDataPoints.WORD_HANDWRITTEN_COUNT.toString(), getDefaultName(), i2, 0.0d, null, null);
                return;
            case SPOKEN:
                this.spokenTracker.incrementWordCount(i2);
                this.writer.logAggregate(ACReportingService.ACDataPoints.WORD_SPOKEN_COUNT.toString(), getDefaultName(), i2, 0.0d, null, null);
                return;
            case UNKNOWN:
                return;
            default:
                oemLog.e("Unknown input state");
                return;
        }
    }

    public void processBuffer(String str) {
        if (this.writer.isReportingEnabled()) {
            processBufferNGram(str);
            processBufferSearch(str, false);
        }
    }

    void processBuffer(String str, ACLanguage.InputSessionState inputSessionState) {
        if (this.writer.isReportingEnabled()) {
            processBufferNGram(str);
            processBufferSearch(str, inputSessionState != null ? inputSessionState.getEnterKeySelected() : false);
        }
    }

    void processBufferNGram(String str) {
        if (!this.writer.isReportingEnabled() || !this.writer.isEntryAllowed(ACReportingService.ACDataPoints.SEVENGRAM_LOGGING)) {
            developerLog.d("processBufferNGram not allowed");
            return;
        }
        if (this.currentInfo == null || str == null || str.isEmpty()) {
            developerLog.v("ignoring NGram: no input");
            return;
        }
        InputFieldInfo inputFieldInfo = new InputFieldInfo(this.currentInfo);
        if (inputFieldInfo.isPasswordField() || inputFieldInfo.isNumberField() || inputFieldInfo.isPhoneNumberField() || inputFieldInfo.isPostalAddress() || inputFieldInfo.isNameField() || inputFieldInfo.isDateTimeField() || inputFieldInfo.isEmailAddressField() || inputFieldInfo.isPhoneticField()) {
            developerLog.v("ignoring NGram: illegal field");
            return;
        }
        if (NUMBER_REMOVE_PATTERN.matcher(str).replaceAll("").isEmpty()) {
            developerLog.v("ignoring NGram: only digits");
            return;
        }
        String filterTextLite = filterTextLite(filterUrls(str));
        ArrayList arrayList = new ArrayList();
        String[] split = filterTextLite.split(XMLResultsHandler.SEP_SPACE);
        if (split.length > 0) {
            StringBuilder sb = new StringBuilder();
            int i = 0;
            while (i < split.length) {
                int length = i + 4 <= split.length ? i + 4 : split.length;
                for (int i2 = i >= 3 ? i - 3 : 0; i2 < length; i2++) {
                    if (i2 == i || i2 == i + 1) {
                        sb.append("*");
                    } else {
                        sb.append("|");
                    }
                    sb.append(split[i2]);
                    if (i2 + 1 == length) {
                        if (i2 == i) {
                            sb.append("*");
                        } else {
                            sb.append("|");
                        }
                    }
                }
                arrayList.add(sb.toString());
                sb.setLength(0);
                i++;
            }
            Collections.shuffle(arrayList);
            sb.setLength(0);
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                sb.append((String) it.next());
                sb.append("::");
            }
            this.writer.logPoint(ACReportingService.ACDataPoints.SEVENGRAM_LOGGING.toString(), getDefaultName(), sb.toString(), null, null, true);
        }
    }

    void processBufferSearch(String str, boolean z) {
        if (this.currentInfo == null || str == null || str.isEmpty()) {
            developerLog.v("Not logging search: buffer empty");
            return;
        }
        if (str.equals(this.currentInfo.label)) {
            developerLog.v("Not logging search: same as label");
            return;
        }
        if (str.equals(this.currentInfo.hintText)) {
            developerLog.v("Not logging search: same as hint text");
            return;
        }
        InputFieldInfo inputFieldInfo = new InputFieldInfo(this.currentInfo);
        if (!inputFieldInfo.isSearchField() && !inputFieldInfo.isActionGoField()) {
            developerLog.v("Not logging search: not a search field");
            return;
        }
        if ((!this.verifiedSearch && inputFieldInfo.isPasswordField()) || inputFieldInfo.isMultilineField() || inputFieldInfo.isNumberField() || inputFieldInfo.isPhoneNumberField() || inputFieldInfo.isPostalAddress() || inputFieldInfo.isNameField() || inputFieldInfo.isShortMessageField() || inputFieldInfo.isEmailSubjectField() || inputFieldInfo.isLongMessageField() || inputFieldInfo.isDateTimeField() || inputFieldInfo.isEmailAddressField() || inputFieldInfo.isPhoneticField()) {
            developerLog.v("Not logging search: not acceptible input field");
            return;
        }
        if (!this.verifiedSearch && str.contains("\n")) {
            developerLog.v("Not logging search: contains newlines");
            return;
        }
        if (str.length() > 200) {
            developerLog.v("Not logging search: too long");
            return;
        }
        if (NUMBER_REMOVE_PATTERN.matcher(str).replaceAll("").isEmpty()) {
            developerLog.v("Not logging search: only numbers");
            return;
        }
        String filterTextLite = filterTextLite(str);
        if (z) {
            this.writer.logPoint(ACReportingService.ACDataPoints.SEARCH_LOGGING.toString(), getDefaultName(), filterTextLite, null, null, true);
        } else {
            this.writer.logPoint(ACReportingService.ACDataPoints.PARTIAL_SEARCH.toString(), getDefaultName(), filterTextLite, null, null, true);
        }
    }

    public void recordAlternativeText(String str) {
        this.writer.logPoint(ACReportingService.ACDataPoints.ALTERNATIVE_TEXT.toString(), getDefaultName(), str, (Date) null, (String) null);
    }

    void recordDeleteWord(String str) {
        this.writer.logPoint(ACReportingService.ACDataPoints.DELETED_WORD.toString(), getDefaultName(), filterText(str), (Date) null, (String) null);
    }

    public void recordInlineText(String str) {
        this.writer.logPoint(ACReportingService.ACDataPoints.INLINE_TEXT.toString(), getDefaultName(), filterText(str), (Date) null, (String) null);
    }

    public void recordKeyCode(int i, boolean z) {
        if (z) {
            this.writer.logPoint(ACReportingService.ACDataPoints.KEYCODE_LONG.toString(), getDefaultName(), String.valueOf(i), (Date) null, (String) null);
        } else {
            this.writer.logPoint(ACReportingService.ACDataPoints.KEYCODE_TAPPED.toString(), getDefaultName(), String.valueOf(i), (Date) null, (String) null);
        }
    }

    public void recordKeyboardLayerChange(String str, String str2) {
        StringBuilder sb = new StringBuilder();
        sb.append("LayerChange_from=").append(String.valueOf(str)).append("_to=").append(String.valueOf(str2));
        this.writer.logPoint(ACReportingService.ACDataPoints.KEYBOARD_PAGE_CHANGE.toString(), getDefaultName(), sb.toString(), null, null, true);
    }

    void recordLanguage(int[] iArr, int i) {
        StringBuilder sb = new StringBuilder();
        if (iArr == null) {
            iArr = new int[0];
        }
        for (int i2 : iArr) {
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(i2);
        }
        this.writer.logPoint(ACReportingService.ACDataPoints.LANGUAGE_CHANGE.toString(), ACLanguage.LanguageChange.from(i).name(), sb.toString(), (Date) null, (String) null);
    }

    void recordLocale(Locale locale) {
        this.writer.logPoint(ACReportingService.ACDataPoints.LOCALE_CHANGE.toString(), getDefaultName(), String.valueOf(locale), (Date) null, (String) null);
    }

    public void recordSelectedWord(String str) {
        this.writer.logPoint(ACReportingService.ACDataPoints.SELECTION_WORD.toString(), getDefaultName(), String.valueOf(str), (Date) null, (String) null);
    }

    public void recordSelectionListContext(String str, String str2) {
        if (!this.writer.isReportingEnabled() || !this.writer.isEntryAllowed(ACReportingService.ACDataPoints.SELECTION_LIST_CONTEXT)) {
            developerLog.d("recordSelectionListContext not allowed");
            return;
        }
        StringBuilder sb = new StringBuilder();
        if (str2 == null) {
            str2 = "";
        }
        for (int i = 0; i < 3; i++) {
            String trim = str2.trim();
            if (trim.isEmpty()) {
                break;
            }
            int length = trim.length() - 1;
            while (length >= 0 && Character.isWhitespace(trim.charAt(length))) {
                length--;
            }
            if (length < 0) {
                break;
            }
            int i2 = length - 1;
            while (i2 >= 0 && !Character.isWhitespace(trim.charAt(i2))) {
                i2--;
            }
            if (sb.length() > 0) {
                sb.insert(0, SELECTION_LIST_SEPARATOR_FIELD);
            }
            sb.insert(0, trim.subSequence(i2 + 1, length + 1));
            str2 = trim.substring(0, i2);
        }
        this.writer.logPoint(ACReportingService.ACDataPoints.SELECTION_LIST_CONTEXT.toString(), getDefaultName(), filterText(((CharSequence) sb) + SEPARATOR_ITEMS + String.valueOf(str)), (Date) null, (String) null);
    }

    public void recordSelectionListOptions(String[] strArr) {
        if (!this.writer.isReportingEnabled() || !this.writer.isEntryAllowed(ACReportingService.ACDataPoints.SELECTION_LIST_OPTIONS)) {
            developerLog.d("recordSelectionListOptions not allowed");
            return;
        }
        StringBuilder sb = new StringBuilder();
        if (strArr != null) {
            for (String str : strArr) {
                String trim = str.trim();
                if (!trim.isEmpty()) {
                    if (sb.length() != 0) {
                        sb.append(SEPARATOR_FIELD);
                    }
                    sb.append(filterText(trim));
                }
            }
        } else {
            sb.append("null");
        }
        this.writer.logPoint(ACReportingService.ACDataPoints.SELECTION_LIST_OPTIONS.toString(), getDefaultName(), sb.toString(), (Date) null, (String) null);
    }

    public void recordSettingChange(String str, String str2, String str3) {
        this.writer.logPoint(ACReportingService.ACDataPoints.SETTINGS_CHANGE.toString(), str, "after:" + String.valueOf(str3), (Date) null, "before:" + String.valueOf(str2));
    }

    public void recordShiftMargin(int i) {
        this.writer.logPoint(ACReportingService.ACDataPoints.SHIFT_MARGIN.toString(), getDefaultName(), Integer.toString(i), (Date) null, (String) null);
    }

    public void recordShiftState(String str) {
        this.writer.logPoint(ACReportingService.ACDataPoints.SHIFT_STATE.toString(), getDefaultName(), String.valueOf(str), (Date) null, (String) null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void recordUdbAdd(String str) {
        this.writer.logPoint(ACReportingService.ACDataPoints.UDB_ADD.toString(), getDefaultName(), filterText(str), null, null, true);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void recordUdbDelete(String str) {
        this.writer.logPoint(ACReportingService.ACDataPoints.UDB_DELETE.toString(), getDefaultName(), filterText(str), null, null, true);
    }

    public void setVerifiedSearch(boolean z) {
        this.verifiedSearch = z;
    }

    public void trackDistanceSwype(long j) {
        this.writer.logAggregate(ACReportingService.ACDataPoints.TOTAL_SWYPE_DISTANCE.toString(), getDefaultName(), Double.valueOf(j).doubleValue(), 0.0d, null, null);
    }

    void trackSpoken(int i, int i2) {
        this.writer.logAggregate(ACReportingService.ACDataPoints.WPM_SPOKEN.toString(), getDefaultName(), Double.valueOf(i).doubleValue(), Double.valueOf(i2).doubleValue(), null, null);
    }

    void trackSwyped(int i, int i2) {
        this.writer.logAggregate(ACReportingService.ACDataPoints.WPM_SWYPED.toString(), getDefaultName(), Double.valueOf(i).doubleValue(), Double.valueOf(i2).doubleValue(), null, null);
    }

    void trackTapped(int i, int i2) {
        this.writer.logAggregate(ACReportingService.ACDataPoints.WPM_TAPPED.toString(), getDefaultName(), Double.valueOf(i).doubleValue(), Double.valueOf(i2).doubleValue(), null, null);
    }

    void trackWritten(int i, int i2) {
        this.writer.logAggregate(ACReportingService.ACDataPoints.WPM_HANDWRITTEN.toString(), getDefaultName(), Double.valueOf(i).doubleValue(), Double.valueOf(i2).doubleValue(), null, null);
    }
}
