package com.nuance.swype.input.accessibility.statemachine;

import android.view.accessibility.AccessibilityEvent;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.KeyboardEx;
import com.nuance.swype.input.KeyboardViewEx;
import com.nuance.swype.input.R;
import com.nuance.swype.input.accessibility.AccessibilityLabel;
import com.nuance.swype.input.accessibility.AccessibilityNotification;
import com.nuance.swype.input.accessibility.SoundResources;
import com.nuance.swype.input.accessibility.choiceSelection.ISelectionChangeListener;
import com.nuance.swype.input.accessibility.choiceSelection.ISelectionStrategy;
import com.nuance.swype.input.accessibility.choiceSelection.ScrubGestureStrategy;
import com.nuance.swype.util.LogManager;
import java.util.List;

/* loaded from: classes.dex */
public class LongPressState extends KeyboardAccessibilityState implements ISelectionChangeListener {
    private static LongPressState instance;
    protected static final LogManager.Log log = LogManager.getLog("LongPressState");
    private KeyboardEx.Key currentChoiceKey;
    private boolean firstTime;
    private boolean isCurrentChoiceCancel;
    private String choiceStr = null;
    private String cancelStr = null;
    private ISelectionStrategy selectionStrategy = new ScrubGestureStrategy();

    public static KeyboardAccessibilityState getInstance() {
        if (instance == null) {
            instance = new LongPressState();
        }
        return instance;
    }

    LongPressState() {
    }

    @Override // com.nuance.swype.input.accessibility.statemachine.KeyboardAccessibilityState
    public void handleActionDown(KeyboardViewEx.Pointer pointer, KeyboardEx.Key key) {
    }

    private List<KeyboardEx.Key> getPopupChoices(KeyboardViewEx keyboardViewEx) {
        return keyboardViewEx.getKeyListInSlideSelectPopup();
    }

    @Override // com.nuance.swype.input.accessibility.statemachine.KeyboardAccessibilityState
    public void handleActionMove(KeyboardViewEx.Pointer pointer, KeyboardEx.Key key) {
        this.selectionStrategy.handleMove(pointer.getCurrentLocation());
        if (!this.isCurrentChoiceCancel) {
            this.currentKeyboardView.setCurrentKeyInSlideSelectPopupManager(this.currentChoiceKey);
        } else {
            this.currentKeyboardView.setCurrentKeyInSlideSelectPopupManager(null);
        }
    }

    @Override // com.nuance.swype.input.accessibility.statemachine.KeyboardAccessibilityState
    public void handleActionUp(KeyboardViewEx.Pointer pointer, KeyboardEx.Key key) {
        if (this.currentKeyboardView != null) {
            this.currentKeyboardView.closePopup();
            this.currentKeyboardView.dismissSingleAltCharPopup();
        }
        changeState(ExplorationState.getInstance());
    }

    @Override // com.nuance.swype.input.accessibility.statemachine.KeyboardAccessibilityState
    public void handleActionCancel(KeyboardViewEx.Pointer pointer) {
    }

    @Override // com.nuance.swype.input.accessibility.statemachine.KeyboardAccessibilityState
    public void populateEventData(AccessibilityEvent event) {
        CharSequence label = null;
        if (this.currentChoiceKey != null) {
            AccessibilityLabel accessibilityLabel = IMEApplication.from(this.currentKeyboardView.getContext()).getAppPreferences().getAccessibilityInfo().getAccessibilityLabel();
            if (accessibilityLabel != null) {
                KeyboardEx.KeyboardLayerType layer = getKeyboardLayer();
                label = accessibilityLabel.getAccessibilityLabel(this.currentChoiceKey, this.currentKeyboardView.getShiftState(), layer, true);
            }
        } else if (this.isCurrentChoiceCancel) {
            label = this.cancelStr;
        }
        if (this.firstTime) {
            label = String.format(this.choiceStr, label);
            this.firstTime = false;
        }
        setAccessibilityEventText(event, label);
    }

    @Override // com.nuance.swype.input.accessibility.choiceSelection.ISelectionChangeListener
    public void selectionChanged(String selection) {
    }

    @Override // com.nuance.swype.input.accessibility.choiceSelection.ISelectionChangeListener
    public void selectionChanged(KeyboardEx.Key selection) {
        this.isCurrentChoiceCancel = false;
        log.d("selectionChanged Key:", selection.codes, " label:", selection.label);
        if (this.currentChoiceKey != selection) {
            this.currentChoiceKey = selection;
            if (shouldSpeakForPassword()) {
                AccessibilityNotification.getInstance().speak(this.currentKeyboardView.getContext());
            }
        }
    }

    @Override // com.nuance.swype.input.accessibility.choiceSelection.ISelectionChangeListener
    public void selectionChangedToCancel() {
        this.isCurrentChoiceCancel = true;
        this.currentChoiceKey = null;
        if (shouldSpeakForPassword()) {
            AccessibilityNotification.getInstance().speak(this.currentKeyboardView.getContext());
        }
    }

    @Override // com.nuance.swype.input.accessibility.choiceSelection.ISelectionChangeListener
    public void selectionOutOfBounds() {
        SoundResources instance2 = SoundResources.getInstance();
        if (instance2 != null) {
            instance2.play(3);
        }
    }

    @Override // com.nuance.swype.input.accessibility.choiceSelection.ISelectionChangeListener
    public void cancelSelection() {
        this.currentKeyboardView.closePopup();
    }

    @Override // com.nuance.swype.input.accessibility.statemachine.KeyboardAccessibilityState
    public void playSoundPlayedOnKeyboardExit() {
        cancelSelection();
        super.playSoundPlayedOnKeyboardExit();
    }

    @Override // com.nuance.swype.input.accessibility.statemachine.KeyboardAccessibilityState
    public void onEnter() {
        KeyboardViewEx currentKeyboardView = getCurrentView();
        if (currentKeyboardView != null) {
            this.choiceStr = currentKeyboardView.getContext().getResources().getString(R.string.accessibility_note_Choice);
            this.cancelStr = currentKeyboardView.getContext().getResources().getString(R.string.accessibility_note_Cancel);
        }
        initDiacriticList();
    }

    private void initDiacriticList() {
        this.selectionStrategy.setChoices(null);
        this.mCurrentAccessibilityKey = getKeyboardModel().getCurrentKey();
        if (this.mCurrentAccessibilityKey != null) {
            List<?> choices = getPopupChoices(this.currentKeyboardView);
            this.selectionStrategy.setChoices(choices);
            this.selectionStrategy.addCancel();
            this.selectionStrategy.setSelectionChangeListener(this);
            this.currentChoiceKey = this.currentKeyboardView.getDefaultKeyInSlideSelectPopup();
            int startIndexOffset = 0;
            if (choices != null && choices.size() > 0 && this.currentChoiceKey != choices.get(0)) {
                startIndexOffset = -1;
            }
            this.selectionStrategy.setStartPoint(getKeyboardModel().getPointer().getCurrentLocation(), startIndexOffset);
            this.currentKeyboardView.redrawKeyboard();
            this.firstTime = true;
            if (shouldSpeakForPassword()) {
                AccessibilityNotification.getInstance().speak(this.currentKeyboardView.getContext());
            }
        }
    }

    @Override // com.nuance.swype.input.accessibility.statemachine.KeyboardAccessibilityState
    public void onExit() {
    }

    @Override // com.nuance.swype.input.accessibility.choiceSelection.ISelectionChangeListener
    public void selectionModeConfirmed() {
    }
}
