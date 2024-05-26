package com.nuance.swypeconnect.ac;

import android.view.inputmethod.EditorInfo;
import com.nuance.connect.api.ConfigService;
import com.nuance.connect.api.LocaleCallback;
import com.nuance.connect.common.ConnectFeature;
import com.nuance.connect.util.ConcurrentCallbackSet;
import java.util.Arrays;
import java.util.Locale;

/* loaded from: classes.dex */
public final class ACLanguage {
    public static final int SELECTED_FROM_GESTURE = 5;
    public static final int SELECTED_FROM_IMPLICIT = 6;
    public static final int SELECTED_FROM_KEYBOARD = 2;
    public static final int SELECTED_FROM_LOCALE = 3;
    public static final int SELECTED_FROM_QUICK_TOGGLE = 4;
    public static final int SELECTED_FROM_SETTINGS = 1;
    public static final int SELECTED_FROM_UNSPECIFIED = 0;
    private final ConfigService config;
    private final ConcurrentCallbackSet<Listener> listeners = new ConcurrentCallbackSet<>();
    private final LocaleCallback localeCallback = new LocaleCallback() { // from class: com.nuance.swypeconnect.ac.ACLanguage.1
        @Override // com.nuance.connect.api.LocaleCallback
        public void onLocaleChange(Locale locale) {
            for (Listener listener : ACLanguage.this.getListeners()) {
                listener.onLocale(locale);
            }
        }
    };
    private final ACManager manager;

    /* loaded from: classes.dex */
    public interface InputSessionState {
        boolean getEnterKeySelected();
    }

    /* loaded from: classes.dex */
    enum LanguageChange {
        UNSPECIFIED(0),
        SETTINGS(1),
        KEYBOARD(2),
        LOCALE(3),
        QUICK_TOGGLE(4),
        LANGUAGE_CHANGE_GESTURE(5),
        IMPLICIT_BASED_ON_FIELD(6);

        private final int id;

        LanguageChange(int i) {
            this.id = i;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static LanguageChange from(int i) {
            for (LanguageChange languageChange : values()) {
                if (languageChange.id == i) {
                    return languageChange;
                }
            }
            return UNSPECIFIED;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public interface Listener {
        void onFinishInput(String str, InputSessionState inputSessionState);

        void onLanguageChange(int[] iArr, int i);

        void onLocale(Locale locale);

        void onStartInput(EditorInfo editorInfo);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ACLanguage(ACManager aCManager) {
        this.config = (ConfigService) aCManager.getConnect().getFeatureService(ConnectFeature.CONFIG);
        this.manager = aCManager;
        this.manager.getConnect().registerLocaleCallback(this.localeCallback);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Listener[] getListeners() {
        return (Listener[]) this.listeners.toArray(new Listener[0]);
    }

    public final int[] getActiveLanguages() {
        return this.config.getActiveLanguages();
    }

    public final void onFinishInput() {
        onFinishInput(null);
    }

    public final void onFinishInput(String str) {
        onFinishInput(str, null);
    }

    public final void onFinishInput(String str, InputSessionState inputSessionState) {
        if (this.manager.getDLMConnector() == null) {
            return;
        }
        try {
            this.manager.getDLMConnector().getConnector().resume();
        } catch (Exception e) {
        }
        for (Listener listener : getListeners()) {
            listener.onFinishInput(str, inputSessionState);
        }
    }

    public final void onStartInput(EditorInfo editorInfo, boolean z) {
        if (this.manager.getDLMConnector() == null) {
            return;
        }
        try {
            this.manager.getDLMConnector().getConnector().yield();
        } catch (Exception e) {
        }
        for (Listener listener : getListeners()) {
            listener.onStartInput(editorInfo);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void registerListener(Listener listener) {
        this.listeners.add(listener);
    }

    public final void setActiveLanguages(int[] iArr) {
        setActiveLanguages(iArr, 0);
    }

    public final void setActiveLanguages(int[] iArr, int i) {
        int[] activeLanguages = this.config.getActiveLanguages();
        boolean equals = (iArr == null || activeLanguages == null) ? iArr == null && activeLanguages == null : Arrays.equals(iArr, activeLanguages);
        this.config.setActiveLanguages(iArr);
        if (equals) {
            return;
        }
        for (Listener listener : getListeners()) {
            listener.onLanguageChange(iArr, i);
        }
    }

    @Deprecated
    public final void setActiveLocale(Locale locale) {
        this.manager.getConnect().setActiveLocale(locale);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void unregisterListener(Listener listener) {
        this.listeners.remove(listener);
    }

    final void unregisterListeners() {
        this.listeners.clear();
    }
}
