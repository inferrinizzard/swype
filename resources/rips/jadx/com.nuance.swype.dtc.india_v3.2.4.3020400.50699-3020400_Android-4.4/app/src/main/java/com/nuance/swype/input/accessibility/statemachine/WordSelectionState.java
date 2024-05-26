package com.nuance.swype.input.accessibility.statemachine;

import android.graphics.Point;
import android.view.accessibility.AccessibilityEvent;
import com.nuance.input.swypecorelib.Candidates;
import com.nuance.input.swypecorelib.WordCandidate;
import com.nuance.swype.input.CandidatesListView;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.KeyboardEx;
import com.nuance.swype.input.KeyboardViewEx;
import com.nuance.swype.input.R;
import com.nuance.swype.input.accessibility.AccessibilityInfo;
import com.nuance.swype.input.accessibility.AccessibilityNotification;
import com.nuance.swype.input.accessibility.SoundResources;
import com.nuance.swype.input.accessibility.choiceSelection.ISelectionChangeListener;
import com.nuance.swype.input.accessibility.choiceSelection.ScrubGestureStrategy;
import com.nuance.swype.util.LogManager;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class WordSelectionState extends KeyboardAccessibilityState implements CandidatesListView.CandidateListener, ISelectionChangeListener {
    private static WordSelectionState instance;
    protected static final LogManager.Log log = LogManager.getLog("WordSelectionState");
    private AccessibilityInfo accessibilityInfo;
    private ACTION_TYPE actionType = ACTION_TYPE.DEFAULT_SELECTION;
    private String cancelStr;
    private String currentSelection;
    private String defaultWord;
    private boolean defaultWordSpokenOnce;
    private boolean handlingActionUp;
    private boolean hasScrubbingStarted;
    private boolean isListInitialize;
    private boolean isStartPointInitialize;
    private long mLastEventTime;
    private Point mLastPoint;
    private int minimumMoveTime;
    ScrubGestureStrategy scrubGesture;
    private double thresholdSpeed;
    ArrayList<String> wordChoiceListCache;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public enum ACTION_TYPE {
        CANCEL,
        DEFAULT_SELECTION,
        SCRUB_GESTURE
    }

    public static WordSelectionState getInstance() {
        if (instance == null) {
            instance = new WordSelectionState();
        }
        return instance;
    }

    protected WordSelectionState() {
    }

    public void initializeList(ArrayList<?> choices) {
        this.scrubGesture = new ScrubGestureStrategy();
        this.scrubGesture.setChoices(choices);
        this.scrubGesture.setSelectionChangeListener(this);
        if (choices != null && choices.size() > 0) {
            this.scrubGesture.addCancel();
            this.defaultWord = (String) choices.get(0);
            setCurrentSelection(this.defaultWord);
            this.isListInitialize = true;
        }
    }

    private void setCurrentSelection(String word) {
        this.currentSelection = word;
    }

    @Override // com.nuance.swype.input.accessibility.statemachine.KeyboardAccessibilityState
    public void handleActionDown(KeyboardViewEx.Pointer pointer, KeyboardEx.Key key) {
    }

    @Override // com.nuance.swype.input.accessibility.statemachine.KeyboardAccessibilityState
    public void handleActionMove(KeyboardViewEx.Pointer pointer, KeyboardEx.Key key) {
        Point currentPoint = pointer.getCurrentLocation();
        checkStartPointInitialization(currentPoint);
        long time = pointer.getCurrentTime();
        this.isTooFast = isTooFast(currentPoint, time);
        if (!this.isTooFast) {
            this.mLastPoint.x = currentPoint.x;
            this.mLastPoint.y = currentPoint.y;
            this.mLastEventTime = time;
        }
        if (!this.hasScrubbingStarted && pointer.getCurrentLocation().y > 0) {
            changeState(ExplorationState.getInstance());
        } else {
            this.scrubGesture.handleMove(pointer.getCurrentLocation());
        }
    }

    private void speakDefaultWordOnEnter() {
        if (!this.defaultWordSpokenOnce && this.defaultWord != null && this.isListInitialize) {
            selectionChanged(this.defaultWord);
            this.defaultWordSpokenOnce = true;
            this.hasScrubbingStarted = false;
        }
    }

    private void checkStartPointInitialization(Point point) {
        if (!this.isStartPointInitialize && this.scrubGesture != null) {
            this.scrubGesture.setStartPoint(point, 0);
            this.isStartPointInitialize = true;
        }
    }

    @Override // com.nuance.swype.input.accessibility.statemachine.KeyboardAccessibilityState
    public void handleActionUp(KeyboardViewEx.Pointer pointer, KeyboardEx.Key key) {
        this.handlingActionUp = true;
        switch (this.actionType) {
            case CANCEL:
                break;
            default:
                if (!this.currentSelection.equals("")) {
                    insertWordToBuffer(this.currentSelection);
                    break;
                }
                break;
        }
        changeState(ExplorationState.getInstance());
    }

    private void replaceActiveTextWith(String word) {
        CandidatesListView candidateListView = getCandidateListView();
        if (candidateListView != null) {
            candidateListView.selectCandidateTriggedByExternalSource(word);
        }
    }

    private CandidatesListView getCandidateListView() {
        KeyboardViewEx currentView = getCurrentView();
        if (currentView == null) {
            return null;
        }
        CandidatesListView candidateListView = IMEApplication.from(currentView.getContext()).getIME().getCurrentInputView().getCandidatesListView();
        return candidateListView;
    }

    private void insertWordToBuffer(String word) {
        if (getCurrentView() != null && !word.equals("")) {
            replaceActiveTextWith(word);
        }
    }

    @Override // com.nuance.swype.input.accessibility.statemachine.KeyboardAccessibilityState
    public void handleActionCancel(KeyboardViewEx.Pointer pointer) {
    }

    @Override // com.nuance.swype.input.accessibility.statemachine.KeyboardAccessibilityState
    public void populateEventData(AccessibilityEvent event) {
        if (!this.currentSelection.equals("") && !this.handlingActionUp) {
            interruptTalkbackIfRequired(this.accessibilityInfo);
            setAccessibilityEventText(event, this.currentSelection);
        }
    }

    @Override // com.nuance.swype.input.accessibility.choiceSelection.ISelectionChangeListener
    public void selectionChanged(String selection) {
        this.actionType = ACTION_TYPE.SCRUB_GESTURE;
        boolean spokenOnce = this.currentSelection.equals(selection);
        setCurrentSelection(selection);
        if (!this.currentSelection.equals("")) {
            getCandidateListView().highlightCandidate(selection);
            log.d("WordSelectionState selectionChanged defaultWord:", this.defaultWord, " spokenOnce:", Boolean.valueOf(spokenOnce));
            if (!spokenOnce) {
                speakSelectedChoice();
            }
        }
    }

    @Override // com.nuance.swype.input.accessibility.choiceSelection.ISelectionChangeListener
    public void selectionChanged(KeyboardEx.Key selection) {
    }

    @Override // com.nuance.swype.input.accessibility.choiceSelection.ISelectionChangeListener
    public void cancelSelection() {
    }

    @Override // com.nuance.swype.input.accessibility.choiceSelection.ISelectionChangeListener
    public void selectionChangedToCancel() {
        this.actionType = ACTION_TYPE.CANCEL;
        setCurrentSelection(this.cancelStr);
        getCandidateListView().hideCandidateHighlight();
        log.d("WordSelectionState selectionChangedToCancel defaultWord:", this.defaultWord);
        speakSelectedChoice();
    }

    private void speakSelectedChoice() {
        log.d("WordSelectionState speakSelectedChoice defaultWord:", this.defaultWord);
        KeyboardViewEx currentView = getCurrentView();
        if (currentView != null) {
            AccessibilityNotification.getInstance().speak(currentView.getContext());
        }
    }

    @Override // com.nuance.swype.input.accessibility.choiceSelection.ISelectionChangeListener
    public void selectionModeConfirmed() {
        this.hasScrubbingStarted = true;
    }

    @Override // com.nuance.swype.input.accessibility.statemachine.KeyboardAccessibilityState
    public void onEnter() {
        this.accessibilityInfo = IMEApplication.from(getCurrentView().getContext()).getAppPreferences().getAccessibilityInfo();
        double thresholdSpeedFactor = getCurrentView().getResources().getInteger(R.integer.accessibility_explore_speed_threshold_factor);
        this.thresholdSpeed = getCurrentView().getResources().getInteger(R.integer.accessibility_explore_speed_threshold) / thresholdSpeedFactor;
        this.minimumMoveTime = getCurrentView().getResources().getInteger(R.integer.accessibility_explore_minimum_move_time);
        this.hover_exit_adjustment_width_offset_low = getCurrentView().getContext().getResources().getDimensionPixelSize(R.dimen.hover_exit_adjustment_width_offset_word_selection_state_low);
        this.hover_exit_adjustment_width_offset_high = getCurrentView().getContext().getResources().getDimensionPixelSize(R.dimen.hover_exit_adjustment_width_offset_word_selection_state_high);
        this.hover_exit_adjustment_width_offset = this.hover_exit_adjustment_width_offset_low;
        this.cancelStr = this.currentKeyboardView.getContext().getResources().getString(R.string.accessibility_note_Cancel);
        log.d("WordSelectionState onEnter defaultWord:", this.defaultWord);
        this.hasScrubbingStarted = false;
        this.defaultWordSpokenOnce = false;
        this.isStartPointInitialize = false;
        setCurrentSelection("");
        this.handlingActionUp = false;
        speakDefaultWordOnEnter();
        if (this.wordChoiceListCache != null) {
            initializeList(this.wordChoiceListCache);
        }
    }

    @Override // com.nuance.swype.input.accessibility.statemachine.KeyboardAccessibilityState
    public void onExit() {
        this.scrubGesture = null;
        this.hasScrubbingStarted = false;
        CandidatesListView candidateListView = getCandidateListView();
        if (candidateListView != null) {
            candidateListView.hideCandidateHighlight();
        }
        setCurrentSelection("");
    }

    @Override // com.nuance.swype.input.CandidatesListView.CandidateListener
    public boolean onSelectCandidate(WordCandidate candidate, Candidates candidates) {
        return false;
    }

    @Override // com.nuance.swype.input.CandidatesListView.CandidateListener
    public boolean onPressHoldCandidate(WordCandidate candidate, Candidates candidates) {
        return false;
    }

    @Override // com.nuance.swype.input.CandidatesListView.CandidateListener
    public boolean onPressReleaseCandidate(WordCandidate candidate, Candidates candidates) {
        return false;
    }

    @Override // com.nuance.swype.input.CandidatesListView.CandidateListener
    public void onPressMoveCandidate(float xPos, float yPos, float xOffset) {
    }

    @Override // com.nuance.swype.input.CandidatesListView.CandidateListener
    public void onCandidatesUpdated(Candidates candidates) {
        this.isListInitialize = false;
        this.wordChoiceListCache = candidatesToList(candidates);
        initializeList(this.wordChoiceListCache);
    }

    public ArrayList<String> candidatesToList(Candidates candidates) {
        ArrayList<String> wordChoiceList = new ArrayList<>();
        if (candidates != null) {
            for (WordCandidate candidate : candidates.getCandidates()) {
                wordChoiceList.add(candidate.toString());
            }
        }
        return wordChoiceList;
    }

    @Override // com.nuance.swype.input.accessibility.choiceSelection.ISelectionChangeListener
    public void selectionOutOfBounds() {
        this.actionType = ACTION_TYPE.CANCEL;
        SoundResources instance2 = SoundResources.getInstance();
        if (instance2 != null) {
            instance2.play(3);
        }
    }

    private boolean isTooFast(Point point, long currentTime) {
        if (point != null) {
            if (this.mLastPoint == null) {
                this.mLastPoint = new Point();
                this.mLastPoint.x = point.x;
                this.mLastPoint.y = point.y;
            } else {
                double distance = Math.abs(point.x - this.mLastPoint.x);
                double time = currentTime - this.mLastEventTime;
                fast = distance / time > this.thresholdSpeed / 2.0d;
                this.minimumMoveTime = 25;
                if (time < this.minimumMoveTime) {
                    fast = true;
                }
                if (fast) {
                    this.hover_exit_adjustment_width_offset = this.hover_exit_adjustment_width_offset_high;
                } else {
                    this.hover_exit_adjustment_width_offset = this.hover_exit_adjustment_width_offset_low;
                }
            }
        }
        return fast;
    }
}
