package com.nuance.swype.preference;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Checkable;
import android.widget.RadioButton;
import android.widget.TextView;
import com.nuance.swype.input.AppPreferences;
import com.nuance.swype.input.InputMethods;
import com.nuance.swype.input.R;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public final class KeyboardListAdapter extends BaseAdapter {
    private final Context context;
    public final InputMethods.InputMode curInputMode;
    private final List<InputMethods.InputMode> inputModes;
    private boolean isJapaneseLanguage;
    private boolean isKoreanLanguage;
    private final List<InputMethods.Layout> mKeyboardLayouts;
    private List<String> mJapaneseLayoutEntries = new ArrayList();
    private List<String> japaneseLayoutEntryValues = new ArrayList();

    public KeyboardListAdapter(Context context, InputMethods.Language language) {
        this.isKoreanLanguage = false;
        this.isJapaneseLanguage = false;
        this.context = context;
        this.curInputMode = language.getCurrentInputModeNoHandwriting();
        this.mKeyboardLayouts = this.curInputMode.getKeyboardLayouts();
        if (language.isKoreanLanguage()) {
            this.isKoreanLanguage = true;
        } else if (language.isJapaneseLanguage()) {
            this.isJapaneseLanguage = true;
            String[] mJapaneseLayouts = context.getResources().getStringArray(R.array.entries_japanese_keyboard_portrait_options);
            this.mJapaneseLayoutEntries.addAll(Arrays.asList(mJapaneseLayouts));
            this.japaneseLayoutEntryValues.addAll(Arrays.asList(context.getResources().getStringArray(R.array.entryValues_japanese_keyboard_portrait_options)));
        }
        this.inputModes = language.getDisplayModes();
        Iterator<InputMethods.InputMode> it = this.inputModes.iterator();
        while (it.hasNext() && !it.next().equals(this.curInputMode)) {
        }
    }

    @Override // android.widget.Adapter
    public final int getCount() {
        if (this.isKoreanLanguage) {
            return this.mKeyboardLayouts.size();
        }
        if (this.isJapaneseLanguage) {
            return this.mJapaneseLayoutEntries.size();
        }
        return this.inputModes.size();
    }

    @Override // android.widget.BaseAdapter, android.widget.ListAdapter
    public final boolean areAllItemsEnabled() {
        return true;
    }

    @Override // android.widget.BaseAdapter, android.widget.ListAdapter
    public final boolean isEnabled(int position) {
        return true;
    }

    @Override // android.widget.Adapter
    public final Object getItem(int position) {
        if (this.isKoreanLanguage) {
            return this.mKeyboardLayouts.get(position);
        }
        if (this.isJapaneseLanguage) {
            return this.mJapaneseLayoutEntries.get(position);
        }
        return this.inputModes.get(position);
    }

    @Override // android.widget.Adapter
    public final long getItemId(int position) {
        return position;
    }

    @Override // android.widget.Adapter
    public final View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(this.context).inflate(R.layout.language_item, parent, false);
            TextView label = (TextView) convertView.findViewById(android.R.id.text1);
            RadioButton button = (RadioButton) convertView.findViewById(android.R.id.button1);
            convertView.setTag(new Holder(label, button));
        }
        if (this.isKoreanLanguage) {
            InputMethods.Layout layout = this.mKeyboardLayouts.get(position);
            Holder holder = (Holder) convertView.getTag();
            holder.label.setText(layout.getDisplayLayoutName());
            holder.checkable.setChecked(this.mKeyboardLayouts.get(position).equals(this.curInputMode.getCurrentLayout()));
        } else if (this.isJapaneseLanguage) {
            String layout2 = this.mJapaneseLayoutEntries.get(position);
            Holder holder2 = (Holder) convertView.getTag();
            holder2.label.setText(layout2);
            String currentLayoutValue = AppPreferences.from(this.context.getApplicationContext()).getString(this.curInputMode.getPortaitLayoutOptionsPrefKey(), "0");
            if (this.mJapaneseLayoutEntries.get(position).equals(this.mJapaneseLayoutEntries.get(this.japaneseLayoutEntryValues.indexOf(currentLayoutValue)))) {
                holder2.checkable.setChecked(true);
            } else {
                holder2.checkable.setChecked(false);
            }
        } else {
            InputMethods.InputMode inputMode = this.inputModes.get(position);
            Holder holder3 = (Holder) convertView.getTag();
            holder3.label.setText(inputMode.getDisplayInputMode());
            holder3.checkable.setChecked(inputMode.equals(this.curInputMode));
        }
        return convertView;
    }

    /* loaded from: classes.dex */
    private static class Holder {
        public final Checkable checkable;
        public final TextView label;

        public Holder(TextView label, Checkable button) {
            this.label = label;
            this.checkable = button;
        }
    }
}
