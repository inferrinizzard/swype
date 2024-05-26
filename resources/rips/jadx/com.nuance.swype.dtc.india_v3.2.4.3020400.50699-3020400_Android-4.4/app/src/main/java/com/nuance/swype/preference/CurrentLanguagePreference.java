package com.nuance.swype.preference;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.nuance.swype.input.BilingualLanguage;
import com.nuance.swype.input.InputMethods;
import com.nuance.swype.input.R;
import com.nuance.swype.preference.LanguagePreference;

/* loaded from: classes.dex */
public class CurrentLanguagePreference extends LanguagePreference {
    private TextView additionalLanguageName;
    private CurrentLanguagePreferenceListener listener;
    private boolean showAdditionalLanguage;

    /* loaded from: classes.dex */
    public interface CurrentLanguagePreferenceListener extends LanguagePreference.LanguagePreferenceListener {
        void onAdditionalLanguageClicked(InputMethods.Language language);
    }

    public CurrentLanguagePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CurrentLanguagePreference(Context context) {
        super(context);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.preference.LanguagePreference, android.preference.Preference
    public View onCreateView(ViewGroup parent) {
        View view = super.onCreateView(parent);
        setLanguage(this.language);
        setAdditionalLanguageVisible(this.showAdditionalLanguage);
        View itemView = view.findViewById(R.id.language_preference_additional_name);
        int startPadding = (int) (itemView.getPaddingStart() + (getContext().getResources().getDisplayMetrics().density * 16.0f));
        itemView.setPaddingRelative(startPadding, itemView.getPaddingTop(), itemView.getPaddingEnd(), itemView.getPaddingBottom());
        itemView.setOnKeyListener(new View.OnKeyListener() { // from class: com.nuance.swype.preference.CurrentLanguagePreference.1
            @Override // android.view.View.OnKeyListener
            public final boolean onKey(View v, int keyCode, KeyEvent event) {
                return CurrentLanguagePreference.this.onKeyForFocusChanging(v, keyCode, event);
            }
        });
        view.findViewById(R.id.language_item).setOnKeyListener(new View.OnKeyListener() { // from class: com.nuance.swype.preference.CurrentLanguagePreference.2
            @Override // android.view.View.OnKeyListener
            public final boolean onKey(View v, int keyCode, KeyEvent event) {
                return CurrentLanguagePreference.this.onKeyForFocusChanging(v, keyCode, event);
            }
        });
        view.findViewById(android.R.id.widget_frame).setOnKeyListener(new View.OnKeyListener() { // from class: com.nuance.swype.preference.CurrentLanguagePreference.3
            @Override // android.view.View.OnKeyListener
            public final boolean onKey(View v, int keyCode, KeyEvent event) {
                return CurrentLanguagePreference.this.onKeyForFocusChanging(v, keyCode, event);
            }
        });
        return view;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.preference.LanguagePreference, android.preference.Preference
    public void onBindView(View view) {
        super.onBindView(view);
        this.additionalLanguageName = (TextView) view.findViewById(R.id.language_preference_additional_name);
        this.additionalLanguageName.setOnClickListener(this);
        if (!Float.isNaN(this.titleTextSize)) {
            this.additionalLanguageName.setTextSize(0, this.titleTextSize);
        }
        setLanguage(this.language);
        setAdditionalLanguageVisible(this.showAdditionalLanguage);
    }

    @Override // com.nuance.swype.preference.LanguagePreference, android.view.View.OnClickListener
    public void onClick(View view) {
        super.onClick(view);
        if (view.getId() == R.id.language_preference_additional_name && this.listener != null) {
            this.listener.onAdditionalLanguageClicked(this.language);
        }
    }

    @Override // com.nuance.swype.preference.LanguagePreference
    public final void setLanguagePreferenceListener(LanguagePreference.LanguagePreferenceListener listener) {
        super.setLanguagePreferenceListener(listener);
        if (listener instanceof CurrentLanguagePreferenceListener) {
            this.listener = (CurrentLanguagePreferenceListener) listener;
        }
    }

    @Override // com.nuance.swype.preference.LanguagePreference, android.preference.Preference
    public int getLayoutResource() {
        return R.layout.current_language_preference;
    }

    @Override // com.nuance.swype.preference.LanguagePreference
    protected final int getSummaryResId() {
        return R.string.pref_change_language_summary;
    }

    @Override // com.nuance.swype.preference.LanguagePreference
    protected final boolean showSummary() {
        return true;
    }

    @Override // com.nuance.swype.preference.LanguagePreference
    public final void setLanguage(InputMethods.Language language) {
        String additionalLang;
        super.setLanguage(language);
        if (language instanceof BilingualLanguage) {
            BilingualLanguage biLang = (BilingualLanguage) language;
            additionalLang = "+ " + biLang.getSecondLanguage().getDisplayName();
        } else {
            additionalLang = "+ " + ((Object) getContext().getText(R.string.pref_bilingual_type_in_two_lang));
        }
        if (this.additionalLanguageName != null) {
            this.additionalLanguageName.setText(additionalLang);
        }
    }

    public final void setAdditionalLanguageVisible(boolean visible) {
        this.showAdditionalLanguage = visible;
        if (this.additionalLanguageName != null) {
            this.additionalLanguageName.setVisibility(visible ? 0 : 8);
        }
    }

    @Override // com.nuance.swype.preference.LanguagePreference
    public final String getLanguageDisplayName(InputMethods.Language language) {
        if (language instanceof BilingualLanguage) {
            String firstLangName = ((BilingualLanguage) language).getFirstLanguage().getDisplayName();
            return firstLangName;
        }
        String firstLangName2 = language.getDisplayName();
        return firstLangName2;
    }
}
