package com.nuance.swype.input.accessibility.choiceSelection;

import android.graphics.Point;
import com.nuance.swype.input.KeyboardEx;
import com.nuance.swype.input.accessibility.choiceSelection.ScrubGestureDetector;
import com.nuance.swype.input.accessibility.statemachine.LongPressState;
import com.nuance.swype.input.accessibility.statemachine.WordSelectionState;
import java.util.List;

/* loaded from: classes.dex */
public class ScrubGestureStrategy implements ISelectionStrategy {
    private ISelectionChangeListener changeListener;
    private List<?> choices;
    private boolean isCancelAdded;
    int lastCircleCount;
    private int numberOfChoices;
    ScrubGestureDetector sgd = new ScrubGestureDetector();

    @Override // com.nuance.swype.input.accessibility.choiceSelection.ISelectionStrategy
    public void setStartPoint(Point point, int startIndexOffset) {
        if (this.isCancelAdded) {
            this.sgd.reset(point, 0, this.numberOfChoices - 1, startIndexOffset + 2);
            this.lastCircleCount = startIndexOffset + 2;
        } else {
            this.sgd.reset(point, 0, this.numberOfChoices - 1, startIndexOffset);
            this.lastCircleCount = startIndexOffset;
        }
    }

    @Override // com.nuance.swype.input.accessibility.choiceSelection.ISelectionStrategy
    public void handleMove(Point point) {
        if (this.sgd.shouldLockInScrubMode()) {
            notifyListenerForConfirmation(true);
        }
        int circleCount = this.sgd.trackSignChange(point);
        if (this.changeListener != null && ((this.choices == null || this.numberOfChoices == 0) && this.sgd.hasNewCircleComplete())) {
            this.changeListener.selectionOutOfBounds();
        }
        if (circleCount != this.lastCircleCount || this.sgd.hasNewCircleComplete()) {
            this.lastCircleCount = circleCount;
            notifyChange(circleCount, this.sgd.getDirection());
        }
    }

    private void notifyListenerForConfirmation(boolean shouldLockInScrubMode) {
        if (this.changeListener != null && shouldLockInScrubMode) {
            this.changeListener.selectionModeConfirmed();
        }
    }

    private void notifyChange(int circleNumber, ScrubGestureDetector.Direction direction) {
        if (this.changeListener != null) {
            int slot = computeCurrentSlot(circleNumber, direction);
            boolean bContinue = true;
            while (bContinue) {
                bContinue = false;
                if (this.choices == null || this.choices.size() == 0 || (this.isCancelAdded && (slot == this.numberOfChoices - 1 || slot == 0))) {
                    this.changeListener.selectionOutOfBounds();
                    this.sgd.resetCircleCount(slot);
                } else if (this.isCancelAdded && (slot == this.numberOfChoices - 2 || slot == 1)) {
                    this.changeListener.selectionChangedToCancel();
                } else if (this.isCancelAdded && slot >= 2 && slot < this.numberOfChoices - 2) {
                    if (this.changeListener instanceof WordSelectionState) {
                        this.changeListener.selectionChanged(this.choices.get(slot - 2).toString());
                    } else if (this.changeListener instanceof LongPressState) {
                        if (((KeyboardEx.Key) this.choices.get(slot - 2)).visible) {
                            this.changeListener.selectionChanged((KeyboardEx.Key) this.choices.get(slot - 2));
                        } else {
                            slot = direction == ScrubGestureDetector.Direction.CLOCKWISE ? slot + 1 : slot - 1;
                            this.sgd.resetCircleCount(slot);
                            bContinue = true;
                        }
                    }
                }
            }
        }
    }

    private int computeCurrentSlot(int circleNumber, ScrubGestureDetector.Direction direction) {
        return circleNumber;
    }

    @Override // com.nuance.swype.input.accessibility.choiceSelection.ISelectionStrategy
    public void setChoices(List<?> choices) {
        this.isCancelAdded = false;
        this.choices = choices;
        if (choices != null) {
            this.numberOfChoices = choices.size();
        } else {
            this.numberOfChoices = 0;
        }
    }

    @Override // com.nuance.swype.input.accessibility.choiceSelection.ISelectionStrategy
    public void setSelectionChangeListener(ISelectionChangeListener listener) {
        this.changeListener = listener;
    }

    @Override // com.nuance.swype.input.accessibility.choiceSelection.ISelectionStrategy
    public void addCancel() {
        if (!this.isCancelAdded) {
            this.isCancelAdded = true;
            this.numberOfChoices += 4;
        }
    }
}
