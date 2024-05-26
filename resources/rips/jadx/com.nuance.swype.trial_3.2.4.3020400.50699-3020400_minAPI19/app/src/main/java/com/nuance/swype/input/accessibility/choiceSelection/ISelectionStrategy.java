package com.nuance.swype.input.accessibility.choiceSelection;

import android.graphics.Point;
import java.util.List;

/* loaded from: classes.dex */
public interface ISelectionStrategy {
    void addCancel();

    void handleMove(Point point);

    void setChoices(List<?> list);

    void setSelectionChangeListener(ISelectionChangeListener iSelectionChangeListener);

    void setStartPoint(Point point, int i);
}
