package com.nuance.swype.input;

import com.nuance.input.swypecorelib.Candidates;
import com.nuance.input.swypecorelib.RecaptureInfo;
import com.nuance.input.swypecorelib.WordCandidate;
import com.nuance.input.swypecorelib.XT9CoreInput;
import com.nuance.swype.input.appspecific.AppSpecificInputConnection;
import com.nuance.swype.stats.StatisticsEnabledEditState;
import com.nuance.swype.util.LogManager;
import com.nuance.swype.util.WordDecorator;

/* loaded from: classes.dex */
public class BackspaceRevertHandler {
    private static final boolean ENABLE_LOG = false;
    private static final LogManager.Log log = LogManager.getLog("BackspaceRevertHandler");
    private Candidates candidates;
    private final AlphaInputView inputView;
    private final boolean resetOnBackToWordBegin = false;
    private final RevertState stateDefault = new RevertState() { // from class: com.nuance.swype.input.BackspaceRevertHandler.1
        @Override // com.nuance.swype.input.BackspaceRevertHandler.RevertState, com.nuance.swype.input.BackspaceRevertHandler.State
        public String getName() {
            return "default";
        }

        @Override // com.nuance.swype.input.BackspaceRevertHandler.RevertState
        public boolean onImplicitSelect(Candidates candidates, WordCandidate candidate, StatisticsEnabledEditState.DefaultSelectionType type) {
            if (candidates == null) {
                dlog("onImplicitSelect(): null candidates");
                BackspaceRevertHandler.this.changeState(BackspaceRevertHandler.this.stateDefault);
            } else {
                dlog("onImplicitSelect(): cand: " + candidates.source());
                boolean isAutoCorrected = !candidates.getDefaultCandidateString().equals(candidates.getExactCandidateString());
                dlog("onImplicitSelect(): auto corrected: " + isAutoCorrected + "; exact word: " + ((Object) candidates.getExactCandidateString()));
                dlog("onImplicitSelect(): type: " + type);
                if (Candidates.match(candidates, Candidates.Source.TAP) && BackspaceRevertHandler.this.lastTapAutoSelectedWord(type) && isAutoCorrected) {
                    BackspaceRevertHandler.this.candidates = candidates;
                    BackspaceRevertHandler.this.changeState(BackspaceRevertHandler.this.stateRevertable);
                }
            }
            return false;
        }

        @Override // com.nuance.swype.input.BackspaceRevertHandler.State
        public void onEnter(State last) {
            BackspaceRevertHandler.this.candidates = null;
        }
    };
    private final RevertState stateRevertable = new RevertState() { // from class: com.nuance.swype.input.BackspaceRevertHandler.2
        @Override // com.nuance.swype.input.BackspaceRevertHandler.RevertState, com.nuance.swype.input.BackspaceRevertHandler.State
        public String getName() {
            return "revertable";
        }

        @Override // com.nuance.swype.input.BackspaceRevertHandler.RevertState
        public boolean onSendBackspace(int count) {
            AppSpecificInputConnection ic;
            boolean reverted = false;
            boolean isAutoCorrected = !BackspaceRevertHandler.this.candidates.getDefaultCandidateString().equals(BackspaceRevertHandler.this.candidates.getExactCandidateString());
            dlog("onSendBackspace(): was auto corrected: " + isAutoCorrected + "; bs count: " + count);
            if (isAutoCorrected && 1 == count && (ic = BackspaceRevertHandler.this.inputView.getCurrentInputConnection()) != null) {
                ic.beginBatchEdit();
                String word = BackspaceRevertHandler.this.candidates.getDefaultCandidateString().toString();
                BackspaceRevertHandler.this.inputView.setInlineWord(word);
                reverted = ic.setComposingRegionBeforeCursor(word, 1, true) != -1;
                dlog("onSendBackspace(): set composing region success: " + reverted);
                if (reverted) {
                    WordDecorator wd = BackspaceRevertHandler.this.inputView.getWordDecorator();
                    if (wd == null) {
                        ic.commitText(BackspaceRevertHandler.this.candidates.getExactCandidateString(), 1);
                    } else {
                        ic.commitText(wd.decorateUnrecognizedWord(BackspaceRevertHandler.this.candidates.getExactCandidateString()), 1);
                    }
                    BackspaceRevertHandler.this.inputView.mIme.onExtractedTextClicked();
                }
                BackspaceRevertHandler.this.inputView.setInlineWord("");
                ic.endBatchEdit();
            }
            BackspaceRevertHandler.this.changeState(reverted ? BackspaceRevertHandler.this.stateReverted : BackspaceRevertHandler.this.stateDefault);
            return reverted;
        }

        @Override // com.nuance.swype.input.BackspaceRevertHandler.RevertState
        public void onSetSuggestions(Candidates candidates) {
            if (candidates != null) {
                dlog("onSetSuggestions(): " + BackspaceRevertHandler.desc(candidates));
                if (!Candidates.match(candidates, Candidates.Source.NEXT_WORD_PREDICTION, Candidates.Source.UDB_EDIT)) {
                    BackspaceRevertHandler.this.changeState(BackspaceRevertHandler.this.stateDefault);
                    return;
                }
                return;
            }
            dlog("onSetSuggestions(): null candidates");
            BackspaceRevertHandler.this.changeState(BackspaceRevertHandler.this.stateDefault);
        }
    };
    private final RevertState stateReverted = new RevertState() { // from class: com.nuance.swype.input.BackspaceRevertHandler.3
        @Override // com.nuance.swype.input.BackspaceRevertHandler.RevertState, com.nuance.swype.input.BackspaceRevertHandler.State
        public String getName() {
            return "reverted";
        }

        @Override // com.nuance.swype.input.BackspaceRevertHandler.RevertState
        public boolean onImplicitSelect(Candidates candidates, WordCandidate candidate, StatisticsEnabledEditState.DefaultSelectionType type) {
            dlog("onImplicitSelect(): cand: " + candidates.source());
            return false;
        }

        @Override // com.nuance.swype.input.BackspaceRevertHandler.RevertState
        public Candidates onWordRecapture(String word) {
            dlog("onWordRecapture(): (reverting) last candidates default word: " + ((Object) BackspaceRevertHandler.this.candidates.getDefaultCandidateString()));
            Candidates out = BackspaceRevertHandler.this.recaptureAcWordCandidates();
            BackspaceRevertHandler.this.changeState(BackspaceRevertHandler.this.stateRecapSuggestionsPending);
            return out;
        }

        @Override // com.nuance.swype.input.BackspaceRevertHandler.RevertState
        public void onSetSuggestions(Candidates candidates) {
            if (candidates != null) {
                dlog("onSetSuggestions(): " + BackspaceRevertHandler.desc(candidates));
                dlog("onSetSuggestions(): expected RECAPTURE_BY_TEXT_SELECTION before this!");
                if (Candidates.match(candidates, Candidates.Source.RECAPTURE)) {
                    BackspaceRevertHandler.this.changeState(BackspaceRevertHandler.this.stateOverride);
                    BackspaceRevertHandler.this.doAutoCorrectOverride(candidates);
                    return;
                }
                return;
            }
            dlog("onSetSuggestions(): null candidates");
            BackspaceRevertHandler.this.changeState(BackspaceRevertHandler.this.stateDefault);
        }

        @Override // com.nuance.swype.input.BackspaceRevertHandler.RevertState
        public void onPreSpace(Candidates candidates) {
            dlog("onPreSpace()");
            if (candidates == null) {
                dlog("onPreSpace(): candidates null");
                BackspaceRevertHandler.this.changeState(BackspaceRevertHandler.this.stateDefault);
            } else {
                dlog("onPreSpace(): cand: " + candidates.source());
                BackspaceRevertHandler.this.onPreSpaceOverrideOrReverted(candidates);
            }
        }
    };
    private final RevertState stateRecapSuggestionsPending = new RevertState() { // from class: com.nuance.swype.input.BackspaceRevertHandler.4
        @Override // com.nuance.swype.input.BackspaceRevertHandler.RevertState, com.nuance.swype.input.BackspaceRevertHandler.State
        public String getName() {
            return "recap-suggetions-pending";
        }

        @Override // com.nuance.swype.input.BackspaceRevertHandler.RevertState
        public boolean onImplicitSelect(Candidates candidates, WordCandidate candidate, StatisticsEnabledEditState.DefaultSelectionType type) {
            dlog("onImplicitSelect(): cand: " + candidates.source());
            return false;
        }

        @Override // com.nuance.swype.input.BackspaceRevertHandler.RevertState
        public void onSetSuggestions(Candidates candidates) {
            if (candidates != null) {
                dlog("onSetSuggestions(): " + BackspaceRevertHandler.desc(candidates));
                if (Candidates.match(candidates, Candidates.Source.RECAPTURE_BY_TEXT_SELECTION)) {
                    BackspaceRevertHandler.this.changeState(BackspaceRevertHandler.this.stateOverride);
                    return;
                } else {
                    BackspaceRevertHandler.this.changeState(BackspaceRevertHandler.this.stateDefault);
                    return;
                }
            }
            dlog("onSetSuggestions(): null candidates");
            BackspaceRevertHandler.this.changeState(BackspaceRevertHandler.this.stateDefault);
        }
    };
    private final RevertState stateOverride = new RevertState() { // from class: com.nuance.swype.input.BackspaceRevertHandler.5
        @Override // com.nuance.swype.input.BackspaceRevertHandler.RevertState, com.nuance.swype.input.BackspaceRevertHandler.State
        public String getName() {
            return "override";
        }

        @Override // com.nuance.swype.input.BackspaceRevertHandler.RevertState
        public void onBackToWordBegin() {
            dlog("onBackToWordBegin()");
        }

        @Override // com.nuance.swype.input.BackspaceRevertHandler.RevertState
        public boolean onImplicitSelect(Candidates candidates, WordCandidate candidate, StatisticsEnabledEditState.DefaultSelectionType type) {
            if (candidates == null) {
                dlog("onImplicitSelect(): null candidates");
                BackspaceRevertHandler.this.changeState(BackspaceRevertHandler.this.stateDefault);
                return false;
            }
            dlog("onImplicitSelect(): cand: " + candidates.source());
            if (!Candidates.match(candidates, Candidates.Source.TAP) || !BackspaceRevertHandler.this.lastTapAutoSelectedWord(type)) {
                BackspaceRevertHandler.log.d("onImplicitSelect(): wrong state for candidates");
                BackspaceRevertHandler.this.changeState(BackspaceRevertHandler.this.stateDefault);
                return false;
            }
            BackspaceRevertHandler.log.d("onImplicitSelect(): implicit select in override state (treat as explicit)");
            BackspaceRevertHandler.this.candidates = candidates;
            BackspaceRevertHandler.this.changeState(BackspaceRevertHandler.this.stateRevertable);
            return true;
        }

        @Override // com.nuance.swype.input.BackspaceRevertHandler.RevertState
        public Candidates onWordRecapture(String word) {
            dlog("onWordRecapture(): word: " + word);
            if (word.equals(BackspaceRevertHandler.this.candidates.getExactCandidateString())) {
                return BackspaceRevertHandler.this.recaptureAcWordCandidates();
            }
            return null;
        }

        @Override // com.nuance.swype.input.BackspaceRevertHandler.RevertState
        public void onSetSuggestions(Candidates candidates) {
            if (candidates != null) {
                dlog("onSetSuggestions(): " + BackspaceRevertHandler.desc(candidates));
                BackspaceRevertHandler.this.doAutoCorrectOverride(candidates);
            } else {
                dlog("onSetSuggestions(): null candidates");
                BackspaceRevertHandler.this.changeState(BackspaceRevertHandler.this.stateDefault);
            }
        }

        @Override // com.nuance.swype.input.BackspaceRevertHandler.RevertState
        public void onPreSpace(Candidates candidates) {
            dlog("onPreSpace()");
            if (candidates != null) {
                dlog("onPreSpace(): " + BackspaceRevertHandler.desc(candidates));
                BackspaceRevertHandler.this.onPreSpaceOverrideOrReverted(candidates);
            } else {
                dlog("onPreSpace(): candidates null");
                BackspaceRevertHandler.this.changeState(BackspaceRevertHandler.this.stateDefault);
            }
        }
    };
    private RevertState state = this.stateDefault;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public abstract class State {
        State() {
        }

        public void onEnter(State last) {
            dlog("onEnter()");
        }

        public void onExit(State last) {
        }

        void dlog(Object msg) {
        }

        public String getName() {
            return getClass().getSimpleName();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public abstract class RevertState extends State {
        protected RevertState() {
            super();
        }

        @Override // com.nuance.swype.input.BackspaceRevertHandler.State
        public String getName() {
            return "base-state";
        }

        public boolean onImplicitSelect(Candidates candidates, WordCandidate candidate, StatisticsEnabledEditState.DefaultSelectionType type) {
            if (candidates == null) {
                dlog("onImplicitSelect(): null candidates");
                BackspaceRevertHandler.this.changeState(BackspaceRevertHandler.this.stateDefault);
            } else {
                dlog("onImplicitSelect(): cand: " + candidates.source());
                BackspaceRevertHandler.this.changeState(BackspaceRevertHandler.this.stateDefault);
            }
            return false;
        }

        public Candidates onWordRecapture(String word) {
            dlog("onWordRecapture(): word: " + word);
            return null;
        }

        public boolean onSendBackspace(int count) {
            dlog("onSendBackspace(): count: " + count);
            BackspaceRevertHandler.this.changeState(BackspaceRevertHandler.this.stateDefault);
            return false;
        }

        public void onSetSuggestions(Candidates candidates) {
            if (candidates != null) {
                dlog("onSetSuggestions(): " + BackspaceRevertHandler.desc(candidates));
            } else {
                dlog("onSetSuggestions(): bad candidates");
                BackspaceRevertHandler.this.changeState(BackspaceRevertHandler.this.stateDefault);
            }
        }

        public void onPreSpace(Candidates candidates) {
            dlog("onPreSpace()");
            if (candidates == null) {
                dlog("onPreSpace(): candidates null");
                BackspaceRevertHandler.this.changeState(BackspaceRevertHandler.this.stateDefault);
            } else {
                dlog("onPreSpace(): cand: " + candidates.source());
            }
        }

        public void onBackToWordBegin() {
            dlog("onBackToWordBegin()");
        }
    }

    public BackspaceRevertHandler(AlphaInputView inputView) {
        this.inputView = inputView;
    }

    public void reset() {
        dlog("reset()");
        changeState(this.stateDefault);
    }

    public boolean onImplicitSelect(Candidates candidates, WordCandidate candidate, StatisticsEnabledEditState.DefaultSelectionType type) {
        return this.state.onImplicitSelect(candidates, candidate, type);
    }

    public Candidates onWordRecapture(String word) {
        return this.state.onWordRecapture(word);
    }

    public boolean onSendBackspace(int count) {
        return this.state.onSendBackspace(count);
    }

    public void onSetSuggestions(Candidates candidates) {
        this.state.onSetSuggestions(candidates);
    }

    public void onPreSpace(Candidates candidates) {
        this.state.onPreSpace(candidates);
    }

    public void onBackToWordBegin() {
        this.state.onBackToWordBegin();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onPreSpaceOverrideOrReverted(Candidates candidates) {
        if (Candidates.match(candidates, Candidates.Source.TAP, Candidates.Source.RECAPTURE_BY_TEXT_SELECTION, Candidates.Source.RECAPTURE)) {
            this.candidates = candidates;
            changeState(this.stateRevertable);
        } else {
            log.d("onPreSpaceOverrideOrReverted(): wrong state for candidates");
            changeState(this.stateDefault);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doAutoCorrectOverride(Candidates candidates) {
        if (Candidates.match(candidates, Candidates.Source.TAP, Candidates.Source.RECAPTURE)) {
            candidates.setDefaultIndex(candidates.getExactCandidateIndex());
        } else if (Candidates.match(candidates, Candidates.Source.RECAPTURE_BY_TEXT_SELECTION)) {
            if (candidates.getExactCandidateString().equals(this.candidates.getExactCandidateString()) ? false : true) {
                dlog("external recapture");
                changeState(this.stateDefault);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String desc(Candidates cand) {
        return "source: " + cand.source() + "; si: " + cand.getSmartSelectionIndex() + "; exact: " + cand.getExactCandidateIndex() + "; default: " + cand.getDefaultCandidateIndex();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Candidates recaptureAcWordCandidates() {
        RecaptureInfo recaptureInfo = recapture(this.candidates.getDefaultCandidateString().toString());
        if (recaptureInfo.totalWord <= 0) {
            return null;
        }
        Candidates out = this.inputView.getXT9CoreInput().getRecaptureCandidates(Candidates.Source.RECAPTURE_BY_TEXT_SELECTION, recaptureInfo);
        dlog("recaptureAcWordCandidates(): " + desc(out));
        out.setDefaultIndex(this.candidates.getExactCandidateIndex());
        return out;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean lastTapAutoSelectedWord(StatisticsEnabledEditState.DefaultSelectionType type) {
        return type.equals(StatisticsEnabledEditState.DefaultSelectionType.TAPPED_SPACE) || type.equals(StatisticsEnabledEditState.DefaultSelectionType.TAPPED_PUNCTUATION);
    }

    private RecaptureInfo recapture(String phrase) {
        XT9CoreInput input = this.inputView.getXT9CoreInput();
        input.clearAllKeys();
        return input.recaptureWord(phrase.toCharArray(), true);
    }

    private static String getName(State state) {
        return state != null ? state.getName() : "*";
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void changeState(RevertState next) {
        dlog("changeState(): " + getName(this.state) + "->" + getName(next));
        if (this.state != null) {
            this.state.onExit(next);
        }
        State old = this.state;
        this.state = next;
        if (this.state != null) {
            this.state.onEnter(old);
        }
    }

    private void dlog(Object msg) {
    }

    public boolean isOverrideActive() {
        return this.state.equals(this.stateOverride);
    }

    public boolean isRevertable() {
        return this.state.equals(this.stateRevertable);
    }
}
