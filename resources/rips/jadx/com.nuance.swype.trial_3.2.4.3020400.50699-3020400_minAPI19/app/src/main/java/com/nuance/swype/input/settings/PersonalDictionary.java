package com.nuance.swype.input.settings;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.InputMethods;
import com.nuance.swype.input.R;
import com.nuance.swype.input.UserPreferences;
import com.nuance.swype.input.udb.UserDictionaryIterator;
import com.nuance.swype.usagedata.UsageData;
import com.nuance.swype.widget.ImageCompoundButton;
import com.nuance.swypeconnect.ac.ACAccount;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/* loaded from: classes.dex */
public final class PersonalDictionary {
    private static final String SELECTED_WORDS_STATE_KEY = "selected_words";
    private final Context context;
    private ImageButton deleteButton;
    private ImageCompoundButton selectAllButton;
    private boolean settingsChanged;
    private UserDictionaryIterator userDictionaryIterator;
    private final BaseAdapter listAdapter = new UdbListAdapter();
    private final List<String> udbWords = new ArrayList();
    private final Set<String> selectedWords = new HashSet();
    private final DialogInterface.OnClickListener okClicked = new DialogInterface.OnClickListener() { // from class: com.nuance.swype.input.settings.PersonalDictionary.2
        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialog, int which) {
            PersonalDictionary.this.deleteSelectedWords();
            PersonalDictionary.this.settingsChanged = true;
        }
    };

    public PersonalDictionary(Context context, Bundle saveInstanceState) {
        String[] selectedWordsArray;
        this.context = context;
        updateUserDictionary();
        if (saveInstanceState != null && (selectedWordsArray = saveInstanceState.getStringArray(SELECTED_WORDS_STATE_KEY)) != null) {
            this.selectedWords.addAll(Arrays.asList(selectedWordsArray));
        }
    }

    private void updateUserDictionary() {
        this.userDictionaryIterator = IMEApplication.from(this.context).createUserDictionaryIterator(InputMethods.from(this.context).getCurrentInputLanguage());
    }

    public final View onCreateView(LayoutInflater inflater, ViewGroup container, View.OnClickListener confirmDeletions) {
        View view = inflater.inflate(R.layout.personal_dictionary, container, false);
        this.deleteButton = (ImageButton) view.findViewById(R.id.btn_delete);
        this.deleteButton.setOnClickListener(confirmDeletions);
        this.selectAllButton = (ImageCompoundButton) view.findViewById(R.id.btn_select_all);
        this.selectAllButton.setOnClickListener(new ImageCompoundButton.OnClickListener() { // from class: com.nuance.swype.input.settings.PersonalDictionary.1
            @Override // com.nuance.swype.widget.ImageCompoundButton.OnClickListener
            public void onClicked(ImageCompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    PersonalDictionary.this.selectAllWords();
                } else {
                    PersonalDictionary.this.unselectAllWords();
                }
            }
        });
        return view;
    }

    public final ListAdapter getListAdapter() {
        return this.listAdapter;
    }

    public final void onListItemClick(View v) {
        CheckedTextView checkedTextView = (CheckedTextView) v.getTag();
        checkedTextView.setChecked(!checkedTextView.isChecked());
        String word = checkedTextView.getText().toString();
        if (checkedTextView.isChecked()) {
            selectUdbWord(word);
        } else {
            unselectUdbWord(word);
        }
    }

    public final void onSaveInstanceState(Bundle outState) {
        outState.putStringArray(SELECTED_WORDS_STATE_KEY, (String[]) this.selectedWords.toArray(new String[this.selectedWords.size()]));
    }

    public final void onResume() {
        updateUserDictionary();
        updateUdbWords();
        updateButtonStates();
    }

    private void updateUdbWords() {
        this.udbWords.clear();
        Set<String> dedupWords = new HashSet<>();
        StringBuilder word = new StringBuilder();
        while (this.userDictionaryIterator.getNext(word)) {
            dedupWords.add(word.toString());
        }
        this.udbWords.addAll(dedupWords);
        Collections.sort(this.udbWords, String.CASE_INSENSITIVE_ORDER);
        this.selectedWords.retainAll(this.udbWords);
        notifyDataChange();
    }

    private void updateButtonStates() {
        updateSelectAllButton();
        updateDeleteButton();
    }

    private void updateDeleteButton() {
        this.deleteButton.setEnabled(!this.selectedWords.isEmpty());
    }

    private void updateSelectAllButton() {
        this.selectAllButton.setEnabled(!this.udbWords.isEmpty());
        if (this.selectedWords.size() == this.udbWords.size()) {
            this.selectAllButton.setChecked(true);
        } else {
            this.selectAllButton.setChecked(false);
        }
    }

    private void selectUdbWord(String word) {
        this.selectedWords.add(word);
        updateButtonStates();
    }

    private void unselectUdbWord(String word) {
        this.selectedWords.remove(word);
        updateButtonStates();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void selectAllWords() {
        for (String word : this.udbWords) {
            this.selectedWords.add(word);
        }
        notifyDataChange();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void unselectAllWords() {
        this.selectedWords.clear();
        notifyDataChange();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void deleteSelectedWords() {
        this.userDictionaryIterator.delete(this.selectedWords);
        updateUdbWords();
    }

    private void notifyDataChange() {
        this.listAdapter.notifyDataSetChanged();
        updateButtonStates();
    }

    public final Dialog createDialog() {
        int numWords = this.selectedWords.size();
        String msg = this.context.getResources().getQuantityString(R.plurals.dialog_msg_delete_words, numWords, Integer.valueOf(numWords));
        TypedValue typedValue = new TypedValue();
        Boolean hasThemedIcon = Boolean.valueOf(this.context.getTheme().resolveAttribute(android.R.attr.alertDialogIcon, typedValue, true));
        return new AlertDialog.Builder(this.context).setTitle(R.string.udb_delete).setIcon(hasThemedIcon.booleanValue() ? typedValue.resourceId : android.R.drawable.ic_dialog_alert).setMessage(msg).setNegativeButton(R.string.cancel_button, (DialogInterface.OnClickListener) null).setPositiveButton(R.string.ok_button, this.okClicked).create();
    }

    /* loaded from: classes.dex */
    private class UdbListAdapter extends BaseAdapter {
        private UdbListAdapter() {
        }

        @Override // android.widget.Adapter
        public int getCount() {
            return PersonalDictionary.this.udbWords.size();
        }

        @Override // android.widget.Adapter
        public Object getItem(int position) {
            return PersonalDictionary.this.udbWords.get(position);
        }

        @Override // android.widget.Adapter
        public long getItemId(int position) {
            return position;
        }

        @Override // android.widget.Adapter
        @SuppressLint({"InflateParams"})
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(PersonalDictionary.this.context).inflate(R.layout.list_item_checkbox, (ViewGroup) null);
                convertView.setTag((CheckedTextView) convertView.findViewById(R.id.checked_view));
            }
            CheckedTextView checkedTextView = (CheckedTextView) convertView.getTag();
            String word = (String) getItem(position);
            checkedTextView.setText(word);
            checkedTextView.setChecked(PersonalDictionary.this.selectedWords.contains(word));
            return convertView;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void onStop() {
        if (this.settingsChanged) {
            recordMyWordsSettings();
        }
    }

    private void recordMyWordsSettings() {
        UsageData.BackupAndSyncStatus status;
        IMEApplication app = IMEApplication.from(this.context);
        UserPreferences userPrefs = app.getUserPreferences();
        ACAccount account = app.getConnect().getAccounts().getActiveAccount();
        if (account == null) {
            status = UsageData.BackupAndSyncStatus.UNREGISTERED;
        } else if (!account.isLinked()) {
            status = UsageData.BackupAndSyncStatus.REGISTERED;
        } else if (app.getConnect().getSync().isEnabled()) {
            status = UsageData.BackupAndSyncStatus.ON;
        } else {
            status = UsageData.BackupAndSyncStatus.OFF;
        }
        boolean livingLanguageEnabled = app.getConnect().getLivingLanguage(null).isEnabled();
        boolean askBeforeAdd = userPrefs.getAskBeforeAdd();
        UsageData.recordMyWordsSettings(status, livingLanguageEnabled, askBeforeAdd);
    }
}
