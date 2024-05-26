package com.nuance.input.swypecorelib;

import android.graphics.Point;
import android.text.SpannableStringBuilder;
import android.util.Log;
import com.nuance.input.swypecorelib.Candidates;
import com.nuance.input.swypecorelib.Shift;
import com.nuance.input.swypecorelib.XT9KeyboardDatabase;
import com.nuance.input.swypecorelib.usagedata.SessionData;
import com.nuance.input.swypecorelib.usagedata.SessionDataCollectorAbstract;
import java.io.FileDescriptor;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public abstract class XT9CoreInput {
    public static final int ALPHA_CORE = 1;
    public static final int AML_WORD_COMPLETION_ORDER = 106;
    public static final int AUTO_CORRECTION = 99;
    public static final int AUTO_SPACE = 104;
    public static final int CHINESE_CORE = 2;
    public static final int CHINESE_HONGKONG_LANGUAGEID = 226;
    public static final int CHINESE_TRAD_LANGUAGEID = 224;
    private static final int CHUNJIIN_INPUT_MODE = 5;
    private static final int CONVERSION_MODE = 3;
    private static final int DEFAULT_INPUT_MODE = 0;
    public static final int DEFAULT_WORD_COMPLETION_POINT = 1;
    public static final int DLM_SCAN_ACTION_COUNT = 1;
    public static final int DLM_USER_EXPLICIT_ACTION_COUNT = 1;
    public static final int DLM_USER_IMPLICIT_ACTION_COUNT = 1;
    public static final int JAPANESE_CORE = 3;
    public static final int KOREAN_CORE = 4;
    public static final int LANG_MODEL = 102;
    private static final int MAXEMOJILIST = 100;
    public static final int MAXWORDLEN = 64;
    public static final int MAXWORDLIST = 10;
    public static final int MAX_CONTEXT_BUFFER_BEFORE_CURSOR = 128;
    private static final int NARAGATGUL_INPUT_MODE = 6;
    public static final int RECAPTUREMAXLIST = 5;
    private static final int TELEX_INPUT_MODE = 2;
    private static final int TRANSLITERATION_MODE = 4;
    public static final int UNSPECIFIED_CORE = 0;
    private static final int VEGA_INPUT_MODE = 7;
    private static final int VNI_INPUT_MODE = 1;
    public static final int WORD_COMPLETION_POINT = 101;
    public static final int WORD_COMPLETION_POINT_AFTER_FIVE_KEYS = 5;
    public static final int WORD_COMPLETION_POINT_AFTER_FOUR_KEYS = 4;
    public static final int WORD_COMPLETION_POINT_AFTER_ONE_KEY = 1;
    public static final int WORD_COMPLETION_POINT_AFTER_SIX_KEYS = 6;
    public static final int WORD_COMPLETION_POINT_AFTER_THREE_KEYS = 3;
    public static final int WORD_COMPLETION_POINT_AFTER_TWO_KEYS = 2;
    public static final int WORD_COMPLETION_POINT_OFF = 0;
    private static EmojiFilter emojiFilter;
    protected DlmEventHandler dlmEvenHandler;
    protected long inputContext;
    protected InputContextRequest inputContextRequestListener;
    private KeyboardLoadCallback keyboardLoadCallback;
    protected int mLanguageID;
    protected final SessionDataCollectorAbstract sessionDataCollector;
    private TouchRequestCallback touchRequestCallback;
    private boolean isGestureLoaded = false;
    protected XT9Status et9status = XT9Status.ET9STATUS_NO_INIT;
    private ICandidateFactory candidateFactory = new DefaultCandidateFactory();

    /* loaded from: classes.dex */
    public interface DlmEventHandler {
        void onBeginDlmBackup(int i);

        void onDlmEvent(byte[] bArr, boolean z, long j, int i);

        void onDlmInitializeStatus(XT9Status xT9Status, int i);

        void onEndDlmBackup();
    }

    /* loaded from: classes.dex */
    public interface ICandidateFactory {
        Candidates createCandidates(List<WordCandidate> list, Candidates.Source source);
    }

    /* loaded from: classes.dex */
    public interface KeyboardLoadCallback {
        XT9KeyboardDatabase loadKeyboardDatabase(int i, int i2);
    }

    private static native void common_enableTrace(long j, boolean z);

    private static native boolean common_gdb_load(long j, short s, short[] sArr, byte[] bArr, short[] sArr2, char[] cArr);

    private static native String common_getCoreVersion();

    private static native String common_getCurrentLDBVersion(long j);

    private static native int common_getDefaultInputMode(long j, int i);

    private static native int common_getKeyIndexByTap(long j, int i, int i2);

    private static native String common_getKeyboardPageXML(long j);

    private static native String common_getSentenceTermCharacters(long j, int i);

    private static native List<WordCandidate> common_getSuggestions(long j, int i, int[] iArr);

    private static native List<WordCandidate> common_getSuggestionsEmoji(long j, int i);

    private static native int common_get_fd(FileDescriptor fileDescriptor);

    private static native int common_isAutoSpaceBeforeTrace(long j, int[] iArr, int[] iArr2);

    private static native boolean common_isLikelyEmoji(long j, char[] cArr, int i);

    private static native boolean common_isTraceEnabled(long j);

    private static native void common_multiTapTimeOut(long j);

    private static native boolean common_onPostInstallLanguage(long j, int i, boolean z);

    private static native boolean common_onPreInstallLanguage(long j, int i, boolean z);

    private static native boolean common_onUpdateLanguage(long j, int i, boolean z);

    private static native boolean common_processKey(long j, int i, int i2, long j2);

    private static native boolean common_processStoredTouch(long j, int i, char[] cArr);

    private static native boolean common_processTap(long j, int i, int i2, int i3, long j2);

    private static native boolean common_processTrace(long j, int[] iArr, int[] iArr2, int[] iArr3, int i);

    private static native boolean common_recaptureWord(long j, char[] cArr, int i, int[] iArr);

    private static native boolean common_reconstructWord(long j, char[] cArr);

    private static native void common_register_InputContext_callback(long j, XT9CoreInput xT9CoreInput);

    private static native void common_register_TouchRequest_callback(long j, XT9CoreInput xT9CoreInput);

    private static native void common_register_kdb_callback(long j, XT9CoreInput xT9CoreInput);

    private static native void common_setEmojiFilter(long j);

    private static native void common_setExternalDatabasePath(String[] strArr);

    private static native boolean common_setKeyboardDatabase(long j, int i, int i2, boolean z);

    private static native void common_setLDBEmoji(long j, boolean z);

    private static native int common_setLanguage(long j, int i, int i2, int i3);

    private static native void common_setMultiTapInputMode(long j, boolean z);

    private static native void common_setRunningState(long j, int i);

    private static native boolean common_touchCancel(long j, int i);

    private static native boolean common_touchEnd(long j, int i, float[] fArr, float[] fArr2, int[] iArr);

    private static native boolean common_touchMove(long j, int i, float[] fArr, float[] fArr2, int[] iArr);

    private static native boolean common_touchStart(long j, int i, float[] fArr, float[] fArr2, int[] iArr);

    private static native void common_touchTimeOut(long j, int i);

    private static native void common_unregister_InputContext_callback(long j);

    private static native void common_unregister_kdb_callback(long j);

    private static native void common_wordSelected(long j, int i, boolean z);

    protected abstract long create_native_context(String str);

    protected abstract void destroy_native_context(long j);

    protected abstract boolean exportDlmAsEvents();

    public abstract void finishSession();

    public abstract int getInputCoreCategory();

    protected abstract XT9Status initialize_native_core(long j);

    public abstract void persistUserDatabase();

    public abstract void startSession();

    /* loaded from: classes.dex */
    public enum XT9InputMode {
        DEFAULT(0),
        VNI(1),
        TELEX(2),
        TRANSLITERATION(4),
        CHUNJIIN(5),
        NARAGATGUL(6),
        VEGA(7),
        CONVERSION(3);

        private final int value;

        public final int value() {
            return this.value;
        }

        XT9InputMode(int value) {
            this.value = value;
        }

        static XT9InputMode valueOf(int inputMode) {
            if (inputMode == 1) {
                return VNI;
            }
            if (inputMode == 2) {
                return TELEX;
            }
            if (inputMode == 5) {
                return CHUNJIIN;
            }
            if (inputMode == 6) {
                return NARAGATGUL;
            }
            if (inputMode == 7) {
                return VEGA;
            }
            if (inputMode == 4) {
                return TRANSLITERATION;
            }
            if (inputMode == 3) {
                return CONVERSION;
            }
            return DEFAULT;
        }
    }

    /* loaded from: classes.dex */
    public static class Gesture {
        private static final byte DEFAULT_FREQUENCY = -1;
        public byte frequency = DEFAULT_FREQUENCY;
        public char[] path;
        public short returnValue;

        public Gesture(short returnValue, char[] gesture) {
            this.returnValue = returnValue;
            this.path = gesture;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public XT9CoreInput(SessionDataCollectorAbstract sessionDataCollector) {
        this.sessionDataCollector = sessionDataCollector;
    }

    public SessionData getSessionData() {
        return this.sessionDataCollector.getSessionData();
    }

    public boolean hasInputContext() {
        return this.inputContext != 0;
    }

    public XT9Status createSession(DlmEventHandler dlmEvenHandler, String databaseConfigFile, EmojiFilter filter, String[] externalDatabasePath) {
        XT9Status xT9Status;
        synchronized (this) {
            this.dlmEvenHandler = dlmEvenHandler;
            if (this.inputContext == 0) {
                this.inputContext = create_native_context(databaseConfigFile);
                this.et9status = initialize_native_core(this.inputContext);
                if (dlmEvenHandler != null) {
                    dlmEvenHandler.onDlmInitializeStatus(this.et9status, getInputCoreCategory());
                }
                emojiFilter = filter;
                common_setEmojiFilter(this.inputContext);
            }
            common_setExternalDatabasePath(externalDatabasePath);
            common_register_kdb_callback(this.inputContext, this);
            common_register_TouchRequest_callback(this.inputContext, this);
            xT9Status = this.et9status;
        }
        return xT9Status;
    }

    public void destroySession() {
        synchronized (this) {
            common_unregister_kdb_callback(this.inputContext);
            common_unregister_InputContext_callback(this.inputContext);
            destroy_native_context(this.inputContext);
            this.inputContext = 0L;
            this.dlmEvenHandler = null;
        }
    }

    public XT9InputMode getDefaultInputMode(int languageID) {
        return XT9InputMode.valueOf(common_getDefaultInputMode(this.inputContext, languageID));
    }

    public XT9Status setLanguage(int languageID) {
        this.mLanguageID = languageID;
        return setLanguage(languageID, 0);
    }

    public XT9Status setLanguage(int firstLanguageID, int secondLanguageID) {
        this.mLanguageID = firstLanguageID;
        return setLanguage(firstLanguageID, secondLanguageID, XT9InputMode.valueOf(common_getDefaultInputMode(this.inputContext, firstLanguageID)));
    }

    public XT9Status setLanguage(int firstLanguageID, int secondLanguageID, XT9InputMode inputMode) {
        this.mLanguageID = firstLanguageID;
        XT9Status status = XT9Status.from(common_setLanguage(this.inputContext, firstLanguageID, secondLanguageID, inputMode.value()));
        if (status == XT9Status.ET9STATUS_NONE) {
            this.sessionDataCollector.onChangeLanguage(firstLanguageID, getCurrentLDBVersion());
        }
        return status;
    }

    public boolean onPreInstallLanguage(int languageID, boolean isCurrentLanguage) {
        return common_onPreInstallLanguage(this.inputContext, languageID, isCurrentLanguage);
    }

    public boolean onPostInstallLanguage(int languageID, boolean isCurrentLanguage) {
        return common_onPostInstallLanguage(this.inputContext, languageID, isCurrentLanguage);
    }

    public boolean onUpdateLanguage(int languageID, boolean isCurrentLanguage) {
        return common_onUpdateLanguage(this.inputContext, languageID, isCurrentLanguage);
    }

    public boolean dlmSwapLanguage(int from, int to) {
        return false;
    }

    public boolean noteWordDone(String buffer, int cursorPosition) {
        return false;
    }

    public boolean setKeyboardDatabase(int kbdId, int pageNum, boolean forceReload) {
        return common_setKeyboardDatabase(this.inputContext, kbdId, pageNum, forceReload);
    }

    public void setKeyboardLoadCallback(KeyboardLoadCallback loadCallback) {
        this.keyboardLoadCallback = loadCallback;
        common_register_kdb_callback(this.inputContext, this);
    }

    static boolean canShowEmoji(String emoji) {
        if (emojiFilter != null) {
            boolean canShow = emojiFilter.canShow(emoji);
            return canShow;
        }
        Log.e("XT9CoreInput", "canShowEmoji: emoji filter not set!");
        return false;
    }

    char[] getKeyboardDatabaseCallback(int kbdId, int pageNum) {
        XT9KeyboardDatabase keyboard = this.keyboardLoadCallback.loadKeyboardDatabase(kbdId, pageNum);
        if (keyboard == null) {
            return null;
        }
        int packedArraySize = 4;
        for (XT9KeyboardDatabase.Key key : keyboard.keys) {
            int multitapCharCount = key.multitapChars == null ? 0 : key.multitapChars.length;
            packedArraySize += key.codes.length + 8 + key.shiftCodes.length + multitapCharCount;
        }
        char[] keyboardPacked = new char[packedArraySize];
        int i = 0 + 1;
        keyboardPacked[0] = (char) keyboard.width;
        keyboardPacked[1] = (char) keyboard.height;
        keyboardPacked[2] = (char) keyboard.pages;
        int i2 = i + 1 + 1 + 1;
        keyboardPacked[3] = (char) keyboard.keys.size();
        for (XT9KeyboardDatabase.Key key2 : keyboard.keys) {
            int i3 = i2 + 1;
            keyboardPacked[i2] = (char) key2.type;
            int i4 = i3 + 1;
            keyboardPacked[i3] = (char) key2.y;
            int i5 = i4 + 1;
            keyboardPacked[i4] = (char) key2.x;
            int i6 = i5 + 1;
            keyboardPacked[i5] = (char) ((key2.y + key2.height) - 1);
            int i7 = i6 + 1;
            keyboardPacked[i6] = (char) ((key2.x + key2.width) - 1);
            int i8 = i7 + 1;
            keyboardPacked[i7] = (char) key2.codes.length;
            System.arraycopy(key2.codes, 0, keyboardPacked, i8, key2.codes.length);
            int i9 = i8 + key2.codes.length;
            int i10 = i9 + 1;
            keyboardPacked[i9] = (char) key2.shiftCodes.length;
            System.arraycopy(key2.shiftCodes, 0, keyboardPacked, i10, key2.shiftCodes.length);
            int i11 = i10 + key2.shiftCodes.length;
            int multitapCount = key2.multitapChars == null ? 0 : key2.multitapChars.length;
            int i12 = i11 + 1;
            keyboardPacked[i11] = (char) multitapCount;
            if (multitapCount > 0) {
                System.arraycopy(key2.multitapChars, 0, keyboardPacked, i12, multitapCount);
                i2 = i12 + multitapCount;
            } else {
                i2 = i12;
            }
        }
        this.sessionDataCollector.onChangeKeyboard(kbdId, keyboard.width, keyboard.height);
        return keyboardPacked;
    }

    public boolean touchStart(int pointerID, float[] xCoords, float[] yCoords, int[] timersMS) {
        return common_touchStart(this.inputContext, pointerID, xCoords, yCoords, timersMS);
    }

    public boolean touchEnd(int pointerID, float[] xCoords, float[] yCoords, int[] timersMS) {
        return common_touchEnd(this.inputContext, pointerID, xCoords, yCoords, timersMS);
    }

    public boolean touchMove(int pointerID, float[] xCoords, float[] yCoords, int[] timersMS) {
        if (!common_touchMove(this.inputContext, pointerID, xCoords, yCoords, timersMS)) {
            return false;
        }
        this.sessionDataCollector.onTraced(xCoords, yCoords);
        return true;
    }

    public boolean touchCancel(int pointerID) {
        return common_touchCancel(this.inputContext, pointerID);
    }

    public boolean processStoredTouch(int currentIndex, char[] functionKey) {
        return common_processStoredTouch(this.inputContext, currentIndex, functionKey);
    }

    public boolean processKey(Point point, int key, Shift.ShiftState shiftState, long eventTime) {
        return point == null ? processKey(key, shiftState, eventTime) : processTap(point, shiftState, eventTime);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean processTap(Point point, Shift.ShiftState shiftState, long eventTime) {
        return common_processTap(this.inputContext, point.x, point.y, shiftState.getIndex(), eventTime);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean processKey(int key, Shift.ShiftState shiftState, long eventTime) {
        return common_processKey(this.inputContext, key, shiftState.getIndex(), eventTime);
    }

    public boolean processTrace(List<Point> trace, List<Long> times, Shift.ShiftState shiftState) {
        if (trace.size() != times.size()) {
            throw new IllegalArgumentException("trace points size " + trace.size() + " != points event time size " + times.size());
        }
        int size = trace.size();
        int[] nativeTraceX = new int[size];
        int[] nativeTraceY = new int[size];
        int[] nativeEventTimes = new int[size];
        for (int i = 0; i < size; i++) {
            Point pt = trace.get(i);
            nativeTraceX[i] = pt.x;
            nativeTraceY[i] = pt.y;
            nativeEventTimes[i] = times.get(i).intValue();
        }
        return common_processTrace(this.inputContext, nativeTraceX, nativeTraceY, nativeEventTimes, shiftState.getIndex());
    }

    public boolean clearKey() {
        return false;
    }

    public boolean clearCharacter() {
        return clearKey();
    }

    public boolean clearAllKeys() {
        return false;
    }

    public boolean clearKeyByIndex(int index, int count) {
        return false;
    }

    public int getKeyCount() {
        return 0;
    }

    public boolean hasActiveInput() {
        return false;
    }

    public List<CharSequence> getWords(SpannableStringBuilder defaultWord, int[] defaultWordIndex, int countToGet) {
        return null;
    }

    public void setAttribute(int id, boolean value) {
    }

    public void setAttribute(int id, int value) {
    }

    public void setShiftState(Shift.ShiftState shift) {
    }

    public Shift.ShiftState getShiftState() {
        return Shift.ShiftState.OFF;
    }

    public boolean addExplicit(char[] charBuffer, int len, Shift.ShiftState shiftState) {
        return false;
    }

    public void setContext(char[] wordContext) {
    }

    public RecaptureInfo recaptureWord(char[] word, boolean isSelection) {
        int[] recaptureInfo = RecaptureInfo.allocateRecaptureInfoFieldInfoArray();
        if (common_recaptureWord(this.inputContext, word, isSelection ? 1 : 0, recaptureInfo)) {
            return new RecaptureInfo(recaptureInfo, word);
        }
        return RecaptureInfo.EMPTY_RECAPTURE_INFO;
    }

    public boolean reconstructWord(char[] word) {
        return common_reconstructWord(this.inputContext, word);
    }

    public RecaptureInfo undoAccept(char[] word, int cursorPosition) {
        return RecaptureInfo.EMPTY_RECAPTURE_INFO;
    }

    public boolean loadGestures(List<Gesture> gestures) {
        if (this.inputContext == 0) {
            return false;
        }
        int count = gestures.size();
        short[] gestureLengths = new short[count];
        byte[] frequencies = new byte[count];
        short[] returnValues = new short[count];
        int combinedGestureLength = 0;
        for (int i = 0; i < count; i++) {
            Gesture gesture = gestures.get(i);
            combinedGestureLength += gesture.path.length;
            gestureLengths[i] = (short) gesture.path.length;
            frequencies[i] = gesture.frequency;
            returnValues[i] = gesture.returnValue;
        }
        char[] combinedGestures = new char[combinedGestureLength];
        int combinedPtr = 0;
        for (int i2 = 0; i2 < count; i2++) {
            Gesture gesture2 = gestures.get(i2);
            System.arraycopy(gesture2.path, 0, combinedGestures, combinedPtr, gesture2.path.length);
            combinedPtr += gesture2.path.length;
        }
        this.isGestureLoaded = common_gdb_load(this.inputContext, (short) count, gestureLengths, frequencies, returnValues, combinedGestures);
        return this.isGestureLoaded;
    }

    protected final Candidates createCandidates(List<WordCandidate> wordCandidates, Candidates.Source source) {
        Candidates out;
        return (this.candidateFactory == null || (out = this.candidateFactory.createCandidates(wordCandidates, source)) == null) ? new Candidates(wordCandidates, source) : out;
    }

    private Candidates getCandidates(Candidates.Source source, int max, RecaptureInfo recaptureInfo) {
        List<WordCandidate> wordCandidates = common_getSuggestions(this.inputContext, max, recaptureInfo.recapturedFieldInfo);
        return createCandidates(wordCandidates, source);
    }

    private Candidates getCandidatesEmoji(Candidates.Source source, int max) {
        List<WordCandidate> emojiCandidates = common_getSuggestionsEmoji(this.inputContext, max);
        return createCandidates(emojiCandidates, source);
    }

    public Candidates getCandidates(Candidates.Source source, int max) {
        return getCandidates(source, max, RecaptureInfo.EMPTY_RECAPTURE_INFO);
    }

    public Candidates getCandidates(Candidates.Source source) {
        return getCandidates(source, 10, RecaptureInfo.EMPTY_RECAPTURE_INFO);
    }

    public Candidates getCandidates() {
        return getCandidates(Candidates.Source.TRACE, 10, RecaptureInfo.EMPTY_RECAPTURE_INFO);
    }

    public Candidates getCandidatesEmoji() {
        return getCandidatesEmoji(Candidates.Source.EMOJEENIE, 100);
    }

    public Candidates getRecaptureCandidates(Candidates.Source source, RecaptureInfo recaptureInfo) {
        return getCandidates(source, 5, recaptureInfo);
    }

    public String getCurrentLDBVersion() {
        return common_getCurrentLDBVersion(this.inputContext);
    }

    public String getCoreVersion() {
        return common_getCoreVersion();
    }

    public boolean isLikelyEmoji(String text) {
        return common_isLikelyEmoji(this.inputContext, text.toCharArray(), text.length());
    }

    /* loaded from: classes.dex */
    public static class DefaultCandidateFactory implements ICandidateFactory {
        private Candidates formatNextPredictionCandidates(Candidates wordCandidates) {
            if (wordCandidates.count() < 3) {
                wordCandidates.setDefaultIndex(0);
                wordCandidates.setExactIndex(0);
                return wordCandidates;
            }
            List<WordCandidate> candidates = new ArrayList<>(3);
            candidates.add(0, wordCandidates.get(1));
            candidates.add(1, wordCandidates.get(0));
            candidates.add(2, wordCandidates.get(2));
            Candidates newCandidates = new Candidates(candidates, wordCandidates.source(), 1, 0);
            return newCandidates;
        }

        @Override // com.nuance.input.swypecorelib.XT9CoreInput.ICandidateFactory
        public Candidates createCandidates(List<WordCandidate> wordCandidates, Candidates.Source source) {
            if (source != Candidates.Source.NEXT_WORD_PREDICTION) {
                return new Candidates(wordCandidates, source);
            }
            Candidates candidates = new Candidates(wordCandidates, source);
            return formatNextPredictionCandidates(candidates);
        }
    }

    public void setCandidateFactory(ICandidateFactory fact) {
        this.candidateFactory = fact;
    }

    public static void ensureSecondIsDefault(List<WordCandidate> wordCandidates) {
        if (wordCandidates.size() > 1) {
            WordCandidate second = wordCandidates.get(1);
            if (!second.isDefault()) {
                WordCandidate first = wordCandidates.get(0);
                WordCandidate first2 = new WordCandidate(first.toString(), second.completionLength(), first.attribute() & (-65), first.id());
                WordCandidate second2 = new WordCandidate(second.toString(), second.completionLength(), second.attribute() | 64, second.id());
                wordCandidates.set(0, first2);
                wordCandidates.set(1, second2);
            }
        }
    }

    public void wordSelected(int wordIndex, boolean userExplictPick) {
        common_wordSelected(this.inputContext, wordIndex, userExplictPick);
    }

    public void setWordQuarantineLevel(int userImplicitAction, int userExplicitAction, int scanUsageAction) {
    }

    public boolean removeSpaceBeforeWord(int wordIndex) {
        return false;
    }

    public void exportDlm() {
        if (this.dlmEvenHandler != null) {
            this.dlmEvenHandler.onBeginDlmBackup(getInputCoreCategory());
            exportDlmAsEvents();
            this.dlmEvenHandler.onEndDlmBackup();
        }
    }

    public static int getFdFromFileDescriptor(FileDescriptor fileDescriptor) {
        return common_get_fd(fileDescriptor);
    }

    public String getKeyboardPageXML() {
        return common_getKeyboardPageXML(this.inputContext);
    }

    public void setRunningState(int runningState) {
        common_setRunningState(this.inputContext, runningState);
    }

    public String getSentenceTermCharacters(int langId) {
        return common_getSentenceTermCharacters(this.inputContext, langId);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onDlmEvent(byte[] event, int len, boolean highPriority) throws Exception {
        if (len != event.length) {
            throw new Exception("Event size not equal!");
        }
        onDlmEvent(event, highPriority);
    }

    private void onDlmEvent(byte[] event, boolean highPriority) {
        if (event != null && this.dlmEvenHandler != null) {
            this.dlmEvenHandler.onDlmEvent(event, highPriority, System.currentTimeMillis(), getInputCoreCategory());
        }
    }

    public void setLDBEmoji(boolean enable) {
        common_setLDBEmoji(this.inputContext, enable);
    }

    public void multiTapTimeOut() {
        common_multiTapTimeOut(this.inputContext);
    }

    public void touchTimeOut(int timeout) {
        common_touchTimeOut(this.inputContext, timeout);
    }

    public boolean isNullLdb() {
        return false;
    }

    public boolean isLanguageHaveEmojiPrediction() {
        return false;
    }

    public void enableTrace(boolean enabled) {
        common_enableTrace(this.inputContext, enabled);
    }

    public boolean isTraceEnabled() {
        return common_isTraceEnabled(this.inputContext);
    }

    public boolean isGestureLoaded() {
        return this.isGestureLoaded;
    }

    public void setMultiTapInputMode(boolean isMultiTap) {
        common_setMultiTapInputMode(this.inputContext, isMultiTap);
    }

    public void setInputContextRequestListener(InputContextRequest listener) {
        this.inputContextRequestListener = listener;
        common_register_InputContext_callback(this.inputContext, this);
    }

    public void setTouchRequestCallback(TouchRequestCallback listener) {
        this.touchRequestCallback = listener;
        common_register_TouchRequest_callback(this.inputContext, this);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static void setExternalDatabasePath(String[] externalDatabasePath) {
        common_setExternalDatabasePath(externalDatabasePath);
    }

    void touchStarted(boolean mainTouch, int pointerId, int keyType, int keyIndex, char keyCode, boolean canBeTraced) {
        if (this.touchRequestCallback != null) {
            this.touchRequestCallback.touchStarted(mainTouch, pointerId, KeyType.from(keyType), keyIndex, keyCode, canBeTraced);
        }
    }

    void touchUpdated(boolean mainTouch, int pointerId, int keyType, int keyIndex, char keyCode, boolean canBeTraced) {
        if (this.touchRequestCallback != null) {
            this.touchRequestCallback.touchUpdated(mainTouch, pointerId, KeyType.from(keyType), keyIndex, keyCode, canBeTraced);
        }
    }

    void touchEnded(boolean mainTouch, int pointerId, int keyType, int keyIndex, char keyCode, boolean canBeTraced) {
        if (this.touchRequestCallback != null) {
            this.touchRequestCallback.touchEnded(mainTouch, pointerId, KeyType.from(keyType), keyIndex, keyCode, canBeTraced);
        }
    }

    void touchCanceled(boolean mainTouch, int pointerID) {
        if (this.touchRequestCallback != null) {
            this.touchRequestCallback.touchCanceled(mainTouch, pointerID);
        }
    }

    void setMultiTapTimerTimeOutRequest(int timerID) {
        if (this.touchRequestCallback != null) {
            this.touchRequestCallback.setMultiTapTimerTimeOutRequest(timerID);
        }
    }

    void setTouchTimerTimeOutRequest(int timeOut) {
        if (this.touchRequestCallback != null) {
            this.touchRequestCallback.setTouchTimerTimeOutRequest(timeOut);
        }
    }

    void keyboardLoaded(int kdbNum, int pageNum) {
        if (this.touchRequestCallback != null) {
            this.touchRequestCallback.keyboardLoaded(kdbNum, pageNum);
        }
    }

    char[] contextBufferCallback(int maxBufferLen) {
        if (this.inputContextRequestListener != null) {
            return this.inputContextRequestListener.getContextBuffer(maxBufferLen);
        }
        return null;
    }

    char[] autoCapTextBufferCallback(int maxBufferLen) {
        if (this.inputContextRequestListener != null) {
            return this.inputContextRequestListener.getAutoCapitalizationTextBuffer(maxBufferLen);
        }
        return null;
    }

    boolean autoAcceptCallback(boolean addSeparator) {
        return this.inputContextRequestListener != null && this.inputContextRequestListener.autoAccept(addSeparator);
    }
}
