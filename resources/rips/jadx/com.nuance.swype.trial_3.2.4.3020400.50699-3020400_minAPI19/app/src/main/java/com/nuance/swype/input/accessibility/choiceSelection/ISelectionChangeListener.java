package com.nuance.swype.input.accessibility.choiceSelection;

import com.nuance.swype.input.KeyboardEx;

/* loaded from: classes.dex */
public interface ISelectionChangeListener {
    void cancelSelection();

    void selectionChanged(KeyboardEx.Key key);

    void selectionChanged(String str);

    void selectionChangedToCancel();

    void selectionModeConfirmed();

    void selectionOutOfBounds();
}
