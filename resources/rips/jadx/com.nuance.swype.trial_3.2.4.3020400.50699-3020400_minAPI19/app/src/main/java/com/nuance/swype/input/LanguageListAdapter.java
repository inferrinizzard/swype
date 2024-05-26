package com.nuance.swype.input;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Checkable;
import android.widget.RadioButton;
import android.widget.TextView;
import com.nuance.swype.input.InputMethods;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public class LanguageListAdapter extends BaseAdapter {
    private int checkItem;
    private final InputMethods.Language currentLanguage;
    private final List<InputMethods.Language> languages;
    private final Context mContext;
    private final boolean showNoneOption;

    public LanguageListAdapter(Context context, List<InputMethods.Language> languages, InputMethods.Language currentLanguage) {
        this(context, languages, currentLanguage, false);
    }

    public LanguageListAdapter(Context context, List<InputMethods.Language> languages, InputMethods.Language currentLanguage, boolean showNoneOption) {
        this.mContext = context;
        this.languages = languages;
        this.currentLanguage = currentLanguage;
        this.showNoneOption = showNoneOption;
        init();
    }

    private void init() {
        Iterator<InputMethods.Language> it = this.languages.iterator();
        while (it.hasNext() && !it.next().equals(this.currentLanguage)) {
            this.checkItem++;
        }
        if (this.showNoneOption) {
            if (this.checkItem < this.languages.size()) {
                this.checkItem++;
            } else {
                this.checkItem = 0;
            }
        }
    }

    @Override // android.widget.Adapter
    public int getCount() {
        int count = this.languages.size();
        if (this.showNoneOption) {
            return count + 1;
        }
        return count;
    }

    @Override // android.widget.BaseAdapter, android.widget.ListAdapter
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override // android.widget.BaseAdapter, android.widget.ListAdapter
    public boolean isEnabled(int position) {
        return true;
    }

    @Override // android.widget.Adapter
    public Object getItem(int position) {
        if (this.showNoneOption) {
            if (position == 0) {
                return null;
            }
            position--;
        }
        return this.languages.get(position);
    }

    @Override // android.widget.Adapter
    public long getItemId(int position) {
        return position;
    }

    public void addLanguage(InputMethods.Language lang) {
        if (lang != null) {
            this.languages.add(lang);
            notifyDataSetChanged();
        }
    }

    public int getCheckItem() {
        return this.checkItem;
    }

    @Override // android.widget.Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(this.mContext).inflate(R.layout.language_item, parent, false);
            TextView label = (TextView) convertView.findViewById(android.R.id.text1);
            RadioButton button = (RadioButton) convertView.findViewById(android.R.id.button1);
            convertView.setTag(new Holder(label, button));
        }
        String label2 = null;
        boolean checked = false;
        if (this.showNoneOption) {
            if (position == 0) {
                label2 = this.mContext.getResources().getString(R.string.label_none);
                checked = this.currentLanguage == null;
            } else {
                position--;
            }
        }
        if (label2 == null) {
            InputMethods.Language language = this.languages.get(position);
            label2 = language.getDisplayName();
            checked = language.equals(this.currentLanguage);
        }
        Holder holder = (Holder) convertView.getTag();
        holder.label.setText(label2);
        holder.checkable.setChecked(checked);
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
