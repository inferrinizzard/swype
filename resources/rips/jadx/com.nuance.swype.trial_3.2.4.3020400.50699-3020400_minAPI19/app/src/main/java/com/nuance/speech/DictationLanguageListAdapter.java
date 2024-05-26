package com.nuance.speech;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Checkable;
import android.widget.RadioButton;
import android.widget.TextView;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.R;
import com.nuance.swype.input.UserPreferences;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/* loaded from: classes.dex */
public class DictationLanguageListAdapter extends BaseAdapter {
    private static final Comparator<DisplayInfoItem> displayLanguageCompare = new Comparator<DisplayInfoItem>() { // from class: com.nuance.speech.DictationLanguageListAdapter.1
        private final Collator c = Collator.getInstance();

        @Override // java.util.Comparator
        public final int compare(DisplayInfoItem lhs, DisplayInfoItem rhs) {
            return this.c.compare(lhs.displayName, rhs.displayName);
        }
    };
    private int checkItem;
    final Context context;
    final String currentLanguageName;
    final List<DisplayInfoItem> displayItems;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class DisplayInfoItem {
        final String displayName;
        final String langName;

        DisplayInfoItem(String langName, String displayName) {
            this.langName = langName;
            this.displayName = displayName;
        }
    }

    /* loaded from: classes.dex */
    public static class Builder {
        private final Context context;
        private final List<DisplayInfoItem> displayItems = new ArrayList();

        public Builder(Context context) {
            this.context = context;
        }

        public void add(String langName, String displayName) {
            this.displayItems.add(new DisplayInfoItem(langName, displayName));
        }

        public DictationLanguageListAdapter build() {
            return new DictationLanguageListAdapter(this.context, this.displayItems);
        }
    }

    private DictationLanguageListAdapter(Context context, List<DisplayInfoItem> items) {
        this.context = context;
        this.displayItems = items;
        this.currentLanguageName = IMEApplication.from(context).getUserPreferences().getCurrentDictationLanguageName();
        Collections.sort(this.displayItems, displayLanguageCompare);
        this.displayItems.add(0, new DisplayInfoItem(UserPreferences.DEFAULT_AUTOMATIC_DICTATION_LANGUAGE, context.getResources().getString(R.string.automatic)));
    }

    @Override // android.widget.Adapter
    public int getCount() {
        return this.displayItems.size();
    }

    @Override // android.widget.Adapter
    public Object getItem(int position) {
        return this.displayItems.get(position).langName;
    }

    @Override // android.widget.Adapter
    public long getItemId(int position) {
        return position;
    }

    public int getCheckItem() {
        return this.checkItem;
    }

    @Override // android.widget.Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(this.context).inflate(R.layout.language_item, parent, false);
            TextView label = (TextView) convertView.findViewById(android.R.id.text1);
            RadioButton button = (RadioButton) convertView.findViewById(android.R.id.button1);
            convertView.setTag(new Holder(label, button));
        }
        Holder holder = (Holder) convertView.getTag();
        holder.label.setText(this.displayItems.get(position).displayName);
        boolean isChecked = this.currentLanguageName.equals(this.displayItems.get(position).langName);
        if (isChecked) {
            this.checkItem = position;
        }
        holder.checkable.setChecked(isChecked);
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
