package com.nuance.swype.preference;

import android.R;
import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.nuance.swype.input.InputMethods;

/* loaded from: classes.dex */
public class LanguagePreference extends Preference implements View.OnClickListener {
    private View currentView;
    private boolean hideKeyboard;
    private boolean isCurrent;
    private boolean keyIsDown;
    private ViewGroup keyboardIcon;
    InputMethods.Language language;
    private LanguagePreferenceListener listener;
    private TextView summaryView;
    public float titleTextSize;

    /* loaded from: classes.dex */
    public interface LanguagePreferenceListener {
        void onKeyboardClicked(InputMethods.Language language);

        void onLanguageClicked(InputMethods.Language language);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public LanguagePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.titleTextSize = Float.NaN;
        this.keyIsDown = false;
        this.currentView = null;
    }

    public LanguagePreference(Context context) {
        super(context);
        this.titleTextSize = Float.NaN;
        this.keyIsDown = false;
        this.currentView = null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.preference.Preference
    public View onCreateView(ViewGroup parent) {
        setLayoutResource(getLayoutResource());
        View view = super.onCreateView(parent);
        if (!Float.isNaN(this.titleTextSize)) {
            ((TextView) view.findViewById(R.id.title)).setTextSize(0, this.titleTextSize);
        }
        view.findViewById(com.nuance.swype.input.R.id.language_item).setOnKeyListener(new View.OnKeyListener() { // from class: com.nuance.swype.preference.LanguagePreference.1
            @Override // android.view.View.OnKeyListener
            public final boolean onKey(View v, int keyCode, KeyEvent event) {
                return LanguagePreference.this.onKeyForFocusChanging(v, keyCode, event);
            }
        });
        view.findViewById(R.id.widget_frame).setOnKeyListener(new View.OnKeyListener() { // from class: com.nuance.swype.preference.LanguagePreference.2
            @Override // android.view.View.OnKeyListener
            public final boolean onKey(View v, int keyCode, KeyEvent event) {
                return LanguagePreference.this.onKeyForFocusChanging(v, keyCode, event);
            }
        });
        this.currentView = view;
        return view;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.preference.Preference
    public void onBindView(View view) {
        super.onBindView(view);
        this.summaryView = (TextView) view.findViewById(R.id.summary);
        this.summaryView.setText(getSummaryResId());
        setLanguage(this.language);
        view.findViewById(com.nuance.swype.input.R.id.language_item).setOnClickListener(this);
        this.keyboardIcon = (ViewGroup) view.findViewById(R.id.widget_frame);
        this.keyboardIcon.setOnClickListener(this);
        setCurrent(this.isCurrent);
    }

    public void setLanguagePreferenceListener(LanguagePreferenceListener listener) {
        this.listener = listener;
    }

    public void setLanguage(InputMethods.Language language) {
        this.language = language;
        if (language != null) {
            setTitle(getLanguageDisplayName(language));
        }
    }

    String getLanguageDisplayName(InputMethods.Language language) {
        return language.getDisplayName();
    }

    public final void setCurrent(boolean isCurrent) {
        this.isCurrent = isCurrent;
        setEnabled(!isCurrent);
        if (this.keyboardIcon != null) {
            this.keyboardIcon.setVisibility((this.hideKeyboard || isCurrent) ? 8 : 0);
        }
        if (this.summaryView != null) {
            this.summaryView.setVisibility(showSummary() ? 0 : 8);
        }
        if (isCurrent && this.currentView != null) {
            this.currentView.findViewById(com.nuance.swype.input.R.id.language_item).setFocusable(false);
            this.currentView.findViewById(R.id.widget_frame).setFocusable(false);
        }
    }

    public final void setKeyboardVisible(boolean visible) {
        this.hideKeyboard = !visible;
        if (this.keyboardIcon != null) {
            this.keyboardIcon.setVisibility(visible ? 0 : 8);
        }
    }

    @Override // android.preference.Preference
    public int getLayoutResource() {
        return com.nuance.swype.input.R.layout.language_preference;
    }

    int getSummaryResId() {
        return com.nuance.swype.input.R.string.pref_current_language_summary;
    }

    boolean showSummary() {
        return this.isCurrent;
    }

    public void onClick(View view) {
        view.setSelected(true);
        int viewId = view.getId();
        if (viewId == 16908310 || com.nuance.swype.input.R.id.language_item == viewId) {
            if (this.listener != null) {
                this.listener.onLanguageClicked(this.language);
            }
        } else if (viewId == 16908312 && this.listener != null) {
            this.listener.onKeyboardClicked(this.language);
        }
    }

    public static float getDefaultTitleTextSize(Context ctx) {
        Preference pref = new Preference(ctx);
        TextView tv = (TextView) ((LayoutInflater) ctx.getSystemService("layout_inflater")).inflate(pref.getLayoutResource(), (ViewGroup) null, false).findViewById(R.id.title);
        if (tv == null) {
            return Float.NaN;
        }
        float defTitleTextSize = tv.getTextSize();
        return defTitleTextSize;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean onKeyForFocusChanging(View v, int keyCode, KeyEvent event) {
        if (keyCode != 20 && keyCode != 19 && keyCode != 21 && keyCode != 22) {
            return false;
        }
        if (event.getAction() == 0) {
            this.keyIsDown = true;
            return true;
        }
        if (!this.keyIsDown) {
            return true;
        }
        this.keyIsDown = false;
        View nextView = null;
        if (keyCode == 19) {
            nextView = v.focusSearch(33);
        } else if (keyCode == 21) {
            nextView = v.focusSearch(17);
        } else if (keyCode == 22) {
            nextView = v.focusSearch(66);
        } else if (keyCode == 20) {
            nextView = v.focusSearch(130);
        }
        if (nextView != null) {
            nextView.requestFocus();
        }
        return true;
    }
}
