package com.nuance.swype.input.chinese;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Checkable;
import android.widget.RadioButton;
import android.widget.TextView;
import com.nuance.swype.input.InputMethods;
import com.nuance.swype.input.R;
import com.nuance.swype.input.UserPreferences;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class InputModeListAdapter extends BaseAdapter {
    private final Context context;
    private final InputMethods.InputMode curInputMode;
    private final List<InputMethods.InputMode> inputModes = new ArrayList();

    public InputModeListAdapter(Context context, InputMethods.Language language) {
        this.context = context;
        this.curInputMode = language.getCurrentInputMode();
        for (InputMethods.InputMode inputMode : language.getAllInputModes()) {
            if (!inputMode.isEnabled()) {
                inputMode.setEnabled(true);
            }
            if (!inputMode.isMultitapMode() && (!inputMode.isHandwriting() || UserPreferences.from(context).isHandwritingEnabled())) {
                this.inputModes.add(inputMode);
            }
        }
    }

    @Override // android.widget.Adapter
    public int getCount() {
        return this.inputModes.size();
    }

    @Override // android.widget.BaseAdapter, android.widget.ListAdapter
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override // android.widget.BaseAdapter, android.widget.ListAdapter
    public boolean isEnabled(int position) {
        return true;
    }

    @Override // android.widget.Adapter
    public Object getItem(int position) {
        return this.inputModes.get(position);
    }

    @Override // android.widget.Adapter
    public long getItemId(int position) {
        return position;
    }

    @Override // android.widget.Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(this.context).inflate(R.layout.language_item, parent, false);
            TextView label = (TextView) convertView.findViewById(android.R.id.text1);
            RadioButton button = (RadioButton) convertView.findViewById(android.R.id.button1);
            convertView.setTag(new Holder(label, button));
        }
        InputMethods.InputMode inputMode = this.inputModes.get(position);
        Holder holder = (Holder) convertView.getTag();
        holder.label.setText(inputMode.getDisplayInputMode());
        holder.checkable.setChecked(this.curInputMode.mInputMode.equals(inputMode.mInputMode));
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
