package com.nuance.swype.preference;

import android.content.Context;
import android.preference.ListPreference;
import com.nuance.swype.input.InputMethods;
import java.util.List;

/* loaded from: classes.dex */
public class KeyboardLayoutPreference extends ListPreference {
    private final InputMethods.InputMode mInputMode;
    private final List<InputMethods.Layout> mKeyboardLayouts;
    private Integer mOrientation;

    public KeyboardLayoutPreference(Context context, InputMethods.InputMode inputMode, Integer orientation) {
        super(context);
        this.mOrientation = null;
        this.mInputMode = inputMode;
        this.mKeyboardLayouts = inputMode.getKeyboardLayouts();
        this.mOrientation = orientation;
        CharSequence[] entries = new CharSequence[this.mKeyboardLayouts.size()];
        CharSequence[] values = new CharSequence[this.mKeyboardLayouts.size()];
        for (int i = 0; i < this.mKeyboardLayouts.size(); i++) {
            entries[i] = this.mKeyboardLayouts.get(i).getDisplayLayoutName();
            values[i] = String.valueOf(i);
        }
        setEntries(entries);
        setEntryValues(values);
        setCurrentLayout();
    }

    @Override // android.preference.Preference
    protected boolean persistString(String value) {
        int index = Integer.parseInt(value);
        if (index >= this.mKeyboardLayouts.size()) {
            return false;
        }
        InputMethods.Layout layout = this.mKeyboardLayouts.get(index);
        if (this.mOrientation == null) {
            layout.saveAsCurrent();
        } else {
            layout.saveAsCurrent(this.mOrientation.intValue());
        }
        setSummary(layout.getDisplayLayoutName());
        return true;
    }

    private void setCurrentLayout() {
        InputMethods.Layout curLayout = this.mOrientation == null ? this.mInputMode.getCurrentLayout() : this.mInputMode.getCurrentLayout(this.mOrientation.intValue());
        for (int i = 0; i < this.mKeyboardLayouts.size(); i++) {
            if (curLayout == this.mKeyboardLayouts.get(i)) {
                setValueIndex(i);
                setSummary(curLayout.getDisplayLayoutName());
                return;
            }
        }
    }
}
