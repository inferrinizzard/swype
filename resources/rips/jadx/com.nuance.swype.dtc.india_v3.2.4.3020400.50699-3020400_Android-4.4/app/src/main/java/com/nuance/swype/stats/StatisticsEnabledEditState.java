package com.nuance.swype.stats;

import android.graphics.Point;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import com.nuance.swype.input.EditState;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.KeyboardEx;
import com.nuance.swype.input.KeyboardViewEx;
import com.nuance.swype.stats.StatisticsManager;
import com.nuance.swype.stats.UsageManager;
import com.nuance.swype.util.LogManager;
import com.nuance.swypeconnect.ac.ACReportingService;
import java.util.List;

/* loaded from: classes.dex */
public class StatisticsEnabledEditState extends EditState {
    private static final LogManager.Log log = LogManager.getLog("Stats");
    private int currentToken;
    private boolean enterKeySelected;
    private final IMEApplication ime;
    private int inputType;
    private KeyboardActionStatsDecorator keyboardActionDecorator;
    private UsageManager.KeyboardUsageScribe keyboardUsageScribe;
    private int previousBufferLength;
    DefaultSelectionType selectionType;
    private StatisticsManager.SessionStatsScribe sessionScribe;

    /* loaded from: classes.dex */
    public enum DefaultSelectionType {
        CURSOR_REPOSITION { // from class: com.nuance.swype.stats.StatisticsEnabledEditState.DefaultSelectionType.1
            @Override // java.lang.Enum
            public final String toString() {
                return "Cursor reposition: ";
            }
        },
        GENERIC { // from class: com.nuance.swype.stats.StatisticsEnabledEditState.DefaultSelectionType.2
            @Override // java.lang.Enum
            public final String toString() {
                return "Generic: ";
            }
        },
        MULTITAP_TOGGLE { // from class: com.nuance.swype.stats.StatisticsEnabledEditState.DefaultSelectionType.3
            @Override // java.lang.Enum
            public final String toString() {
                return "Multitap toggle: ";
            }
        },
        MULTITAP_TIMEOUT { // from class: com.nuance.swype.stats.StatisticsEnabledEditState.DefaultSelectionType.4
            @Override // java.lang.Enum
            public final String toString() {
                return "Multitap timeout: ";
            }
        },
        SELECTION_WCL { // from class: com.nuance.swype.stats.StatisticsEnabledEditState.DefaultSelectionType.5
            @Override // java.lang.Enum
            public final String toString() {
                return "WCL selection: ";
            }
        },
        SELECTION_CHANGED { // from class: com.nuance.swype.stats.StatisticsEnabledEditState.DefaultSelectionType.6
            @Override // java.lang.Enum
            public final String toString() {
                return "Selection changed: ";
            }
        },
        SWYPE_NEXT_WORD { // from class: com.nuance.swype.stats.StatisticsEnabledEditState.DefaultSelectionType.7
            @Override // java.lang.Enum
            public final String toString() {
                return "Swype next word: ";
            }
        },
        TAPPED_SPACE { // from class: com.nuance.swype.stats.StatisticsEnabledEditState.DefaultSelectionType.8
            @Override // java.lang.Enum
            public final String toString() {
                return "Tapped space: ";
            }
        },
        TAPPED_PUNCTUATION { // from class: com.nuance.swype.stats.StatisticsEnabledEditState.DefaultSelectionType.9
            @Override // java.lang.Enum
            public final String toString() {
                return "Tapped punctuation: ";
            }
        },
        TAPPED_PREFIX { // from class: com.nuance.swype.stats.StatisticsEnabledEditState.DefaultSelectionType.10
            @Override // java.lang.Enum
            public final String toString() {
                return "Tapped prefix: ";
            }
        },
        UNSPECIFIED { // from class: com.nuance.swype.stats.StatisticsEnabledEditState.DefaultSelectionType.11
            @Override // java.lang.Enum
            public final String toString() {
                return "Unspecified: ";
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class KeyboardActionStatsDecorator implements KeyboardViewEx.OnKeyboardActionListener {
        private KeyboardViewEx.OnKeyboardActionListener base;

        public KeyboardActionStatsDecorator(KeyboardViewEx.OnKeyboardActionListener listener) {
            this.base = listener;
        }

        @Override // com.nuance.swype.input.KeyboardViewEx.OnKeyboardActionListener
        public void onPress(int primaryCode) {
            this.base.onPress(primaryCode);
        }

        @Override // com.nuance.swype.input.KeyboardViewEx.OnKeyboardActionListener
        public void onRelease(int primaryCode) {
            this.base.onRelease(primaryCode);
        }

        @Override // com.nuance.swype.input.KeyboardViewEx.OnKeyboardActionListener
        public void onKey(Point point, int primaryCode, int[] keyCodes, KeyboardEx.Key key, long eventTime) {
            this.base.onKey(point, primaryCode, keyCodes, key, eventTime);
            StatisticsEnabledEditState.this.inputType = ACReportingService.INPUT_TYPE_TAPPED;
            if (StatisticsEnabledEditState.this.sessionScribe != null) {
                StatisticsEnabledEditState.this.sessionScribe.mark(StatisticsEnabledEditState.this.inputType);
            }
            if (StatisticsEnabledEditState.this.keyboardUsageScribe != null) {
                StatisticsEnabledEditState.this.keyboardUsageScribe.recordKeycodeTapped(primaryCode);
            }
        }

        @Override // com.nuance.swype.input.KeyboardViewEx.OnKeyboardActionListener
        public void onText(CharSequence text, long eventTime) {
            this.base.onText(text, eventTime);
        }

        @Override // com.nuance.swype.input.KeyboardViewEx.OnKeyboardActionListener
        public void swipeLeft() {
            this.base.swipeLeft();
        }

        @Override // com.nuance.swype.input.KeyboardViewEx.OnKeyboardActionListener
        public void swipeRight() {
            this.base.swipeRight();
        }

        @Override // com.nuance.swype.input.KeyboardViewEx.OnKeyboardActionListener
        public void swipeDown() {
            this.base.swipeDown();
        }

        @Override // com.nuance.swype.input.KeyboardViewEx.OnKeyboardActionListener
        public void swipeUp() {
            this.base.swipeUp();
        }

        @Override // com.nuance.swype.input.KeyboardViewEx.OnKeyboardActionListener
        public void onMultitapTimeout() {
            this.base.onMultitapTimeout();
        }

        @Override // com.nuance.swype.input.KeyboardViewEx.OnKeyboardActionListener
        public void onTrace(KeyboardViewEx.TracePoints trace) {
            this.base.onTrace(trace);
            StatisticsEnabledEditState.this.inputType = ACReportingService.INPUT_TYPE_SWYPED;
            if (StatisticsEnabledEditState.this.sessionScribe != null) {
                StatisticsEnabledEditState.this.sessionScribe.mark(StatisticsEnabledEditState.this.inputType);
            }
        }

        @Override // com.nuance.swype.input.KeyboardViewEx.OnKeyboardActionListener
        public void onWrite(List<Point> write) {
            this.base.onWrite(write);
            StatisticsEnabledEditState.this.inputType = ACReportingService.INPUT_TYPE_HANDWRITTEN;
            if (StatisticsEnabledEditState.this.sessionScribe != null) {
                StatisticsEnabledEditState.this.sessionScribe.mark(StatisticsEnabledEditState.this.inputType);
            }
        }

        @Override // com.nuance.swype.input.KeyboardViewEx.OnKeyboardActionListener
        public void onDoublePress(int primaryCode) {
        }

        @Override // com.nuance.swype.input.KeyboardViewEx.OnKeyboardActionListener
        public void onHardwareCharKey(int primaryCode, int[] keyCodes) {
        }

        @Override // com.nuance.swype.input.KeyboardViewEx.OnKeyboardActionListener
        public void onUpdateSpeechText(CharSequence text, boolean isStreaming, boolean isFinal) {
            StatisticsEnabledEditState.log.v("onUpdateSpeechText");
            this.base.onUpdateSpeechText(text, isStreaming, isFinal);
            StatisticsEnabledEditState.this.inputType = ACReportingService.INPUT_TYPE_SPOKEN;
            if (StatisticsEnabledEditState.this.keyboardUsageScribe != null) {
                int wordCount = text.toString().trim().split("\\s").length;
                if (StatisticsEnabledEditState.this.sessionScribe != null) {
                    StatisticsEnabledEditState.this.sessionScribe.mark(StatisticsEnabledEditState.this.inputType, wordCount);
                }
                if (!isStreaming || isFinal) {
                    StatisticsEnabledEditState.this.keyboardUsageScribe.recordCommittedSentence(text.toString());
                }
            }
        }
    }

    public StatisticsEnabledEditState(IMEApplication imeApp) {
        super(imeApp);
        this.inputType = 0;
        this.selectionType = DefaultSelectionType.UNSPECIFIED;
        this.ime = imeApp;
        UsageManager usageManager = UsageManager.from(this.ime);
        if (usageManager != null) {
            this.keyboardUsageScribe = usageManager.getKeyboardUsageScribe();
        }
        this.sessionScribe = StatisticsManager.getSessionStatsScribe(this.ime);
    }

    @Override // com.nuance.swype.input.EditState
    public void selectWord(CharSequence wordSelected, CharSequence defaultWord) {
        super.selectWord(wordSelected, defaultWord);
        if (wordSelected != null && this.keyboardUsageScribe != null) {
            if (this.sessionScribe != null) {
                this.sessionScribe.mark(this.inputType, 1);
            }
            if (defaultWord != null && !defaultWord.equals(wordSelected)) {
                new StringBuilder("Default word not selected: ").append((Object) wordSelected);
            } else {
                new StringBuilder().append(this.selectionType.toString()).append((Object) wordSelected);
                this.selectionType = DefaultSelectionType.UNSPECIFIED;
            }
        }
    }

    public void setDefaultWordType(DefaultSelectionType type) {
        if (type == null) {
            type = DefaultSelectionType.UNSPECIFIED;
        }
        this.selectionType = type;
    }

    @Override // com.nuance.swype.input.EditState
    public void startSession() {
        super.startSession();
        this.inputType = 0;
        this.enterKeySelected = false;
        this.currentToken = -1;
    }

    @Override // com.nuance.swype.input.EditState
    public void endSession() {
        super.endSession();
        String inputContext = getInputContents();
        this.previousBufferLength = inputContext == null ? 0 : inputContext.length();
    }

    @Override // com.nuance.swype.input.EditState
    public void recapture() {
        super.recapture();
    }

    @Override // com.nuance.swype.input.EditState
    public void cursorChanged(CharSequence charBeforeCusor) {
        super.cursorChanged(charBeforeCusor);
    }

    private String getInputContents() {
        ExtractedText eText;
        return (this.ime == null || this.ime.getIME() == null || this.ime.getIME().getCurrentInputConnection() == null || (eText = this.ime.getIME().getCurrentInputConnection().getExtractedText(new ExtractedTextRequest(), 0)) == null || eText.text == null || this.keyboardUsageScribe == null) ? "" : String.valueOf(eText.text);
    }

    public String getHideWindowInputContents() {
        String inputContext = getInputContents();
        if (verifyWorthProcessing(inputContext)) {
            return inputContext;
        }
        return null;
    }

    public KeyboardViewEx.OnKeyboardActionListener getOnKeyboardActionListener() {
        return this.keyboardActionDecorator;
    }

    public KeyboardViewEx.OnKeyboardActionListener watchOnKeyboardActionListener(KeyboardViewEx.OnKeyboardActionListener listener) {
        this.keyboardActionDecorator = new KeyboardActionStatsDecorator(listener);
        return this.keyboardActionDecorator;
    }

    private boolean verifyWorthProcessing(String inputContext) {
        if (inputContext == null || inputContext.length() == 0) {
            return false;
        }
        if (this.previousBufferLength == 0 || this.previousBufferLength != inputContext.length()) {
            return true;
        }
        return false;
    }

    @Override // com.nuance.swype.input.EditState
    public void enterSent() {
        this.enterKeySelected = true;
    }

    public boolean isEnterSent() {
        return this.enterKeySelected;
    }

    @Override // com.nuance.swype.input.EditState
    public void runSearch(String searchString) {
    }

    public void reportWritingResults(String string) {
        if (this.sessionScribe != null) {
            this.sessionScribe.mark(ACReportingService.INPUT_TYPE_HANDWRITTEN, 1);
        }
    }
}
