package com.nuance.swype.input.settings;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.SeekBar;
import android.widget.TextView;
import com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.xmlResults.XMLResultsHandler;
import com.nuance.swype.input.AppPreferences;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.InputMethods;
import com.nuance.swype.input.R;
import com.nuance.swype.input.ThemeManager;
import com.nuance.swype.input.UserPreferences;
import com.nuance.swype.preference.KeyboardLayoutPreference;
import com.nuance.swype.stats.StatisticsManager;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/* loaded from: classes.dex */
public abstract class InputPrefs implements Preference.OnPreferenceChangeListener {
    public static final int AUTO_ACCEPT_COLOR_INDEX = 24;
    protected static final int DIALOG_PEN_COLOR = 13;
    protected static final int DIALOG_RECOGNITION_SPEED = 11;
    protected static final int DIALOG_STROKE_WIDTH = 12;
    private static final String INPUT_MODE_DISPLAY_KEY = "input_mode_display";
    private static final String INPUT_MODE_KEY = "input_mode";
    private static final String LANGUAGE_ID_KEY = "language_id";
    private static final float QVGA_DEVICE = 0.8f;
    private static final int RECOGNITION_SPEED_STEPSIZE = 100;
    private final Context context;
    private String displayInputModeName;
    private final String inputModeName;
    private final String languageId;
    private int mColorIndex;
    private int mValue;
    private final Preference.OnPreferenceChangeListener saveInputAreaPref = new Preference.OnPreferenceChangeListener() { // from class: com.nuance.swype.input.settings.InputPrefs.5
        @Override // android.preference.Preference.OnPreferenceChangeListener
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            if (!(preference instanceof ListPreference)) {
                return false;
            }
            ListPreference list = (ListPreference) preference;
            AppPreferences.from(InputPrefs.this.context).setHandwritingInputArea(Integer.parseInt(newValue.toString()));
            StatisticsManager.SessionStatsScribe sessionScribe = StatisticsManager.getSessionStatsScribe(InputPrefs.this.context);
            if (sessionScribe != null) {
                sessionScribe.recordSettingsChange(list.getKey(), newValue.toString(), list.getValue());
            }
            return true;
        }
    };
    private final PreferenceScreen screen;
    public static final int INPUT_PREFS_XML = R.xml.keyboard_input_screen_prefs;
    private static final int[] COLORS = {Color.rgb(R.styleable.ThemeTemplate_keyboardSuggestStripVer, R.styleable.ThemeTemplate_keyboardSuggestStripVer, R.styleable.ThemeTemplate_keyboardSuggestStripVer), Color.rgb(R.styleable.ThemeTemplate_unrecognizedWordUnderlineThickness, R.styleable.ThemeTemplate_unrecognizedWordUnderlineThickness, R.styleable.ThemeTemplate_unrecognizedWordUnderlineThickness), Color.rgb(130, 130, 130), Color.rgb(105, 105, 105), Color.rgb(28, 28, 28), Color.rgb(R.styleable.ThemeTemplate_popupPreviewKeyOffset, 255, R.styleable.ThemeTemplate_popupPreviewKeyOffset), Color.rgb(255, R.styleable.ThemeTemplate_btnKeyboardActionKeyNormalTop, 0), Color.rgb(46, R.styleable.ThemeTemplate_hwclTranslitHiQwerty, 87), Color.rgb(0, R.styleable.ThemeTemplate_hwclTranslitHiQwerty, 0), Color.rgb(0, 100, 0), Color.rgb(255, R.styleable.ThemeTemplate_chinesePeriod, R.styleable.ThemeTemplate_keyCharacterSizeSmall), Color.rgb(250, 250, R.styleable.ThemeTemplate_btnKeyboardNumberPressed), Color.rgb(R.styleable.ThemeTemplate_keyboardWingBackground, R.styleable.ThemeTemplate_keyboardSuggestStripVer, R.styleable.ThemeTemplate_btnKeyboardActionKeyPressedMid), Color.rgb(R.styleable.ThemeTemplate_keyboardWingBackground, R.styleable.ThemeTemplate_keyboardBackgroundHwrView, 130), Color.rgb(255, 255, 0), Color.rgb(79, R.styleable.ThemeTemplate_popupBorderPaddingLeft, 205), Color.rgb(0, R.styleable.ThemeTemplate_btnKeyboardKeyNormalMid, 255), Color.rgb(67, 110, R.styleable.ThemeTemplate_keyboardWingBackground), Color.rgb(0, 0, 205), Color.rgb(0, 0, R.styleable.ThemeTemplate_hwclTranslitHiQwerty), Color.rgb(255, 130, R.styleable.ThemeTemplate_btnKeyboardActionKeyPressedTop), Color.rgb(255, 0, 255), Color.rgb(R.styleable.ThemeTemplate_keyboardWingBackground, 0, 0), Color.rgb(205, 0, 0), Color.rgb(R.styleable.ThemeTemplate_hwclTranslitHiQwerty, 0, 0), Color.rgb(255, 127, 0), Color.rgb(R.styleable.ThemeTemplate_keyboardWingBackground, 0, 0), Color.rgb(205, 0, 0), Color.rgb(R.styleable.ThemeTemplate_hwclTranslitHiQwerty, 0, 0), Color.rgb(255, 127, 0), Color.rgb(28, 28, 28)};
    private static final Integer[] mThumbIds = {Integer.valueOf(R.drawable.color_picker_00), Integer.valueOf(R.drawable.color_picker_01), Integer.valueOf(R.drawable.color_picker_02), Integer.valueOf(R.drawable.color_picker_03), Integer.valueOf(R.drawable.color_picker_04), Integer.valueOf(R.drawable.color_picker_10), Integer.valueOf(R.drawable.color_picker_11), Integer.valueOf(R.drawable.color_picker_12), Integer.valueOf(R.drawable.color_picker_13), Integer.valueOf(R.drawable.color_picker_14), Integer.valueOf(R.drawable.color_picker_20), Integer.valueOf(R.drawable.color_picker_21), Integer.valueOf(R.drawable.color_picker_22), Integer.valueOf(R.drawable.color_picker_23), Integer.valueOf(R.drawable.color_picker_24), Integer.valueOf(R.drawable.color_picker_30), Integer.valueOf(R.drawable.color_picker_31), Integer.valueOf(R.drawable.color_picker_32), Integer.valueOf(R.drawable.color_picker_33), Integer.valueOf(R.drawable.color_picker_34), Integer.valueOf(R.drawable.color_picker_40), Integer.valueOf(R.drawable.color_picker_41), Integer.valueOf(R.drawable.color_picker_42), Integer.valueOf(R.drawable.color_picker_43), Integer.valueOf(R.drawable.color_picker_44)};

    abstract void doShowDialog(int i, Bundle bundle);

    public static Bundle createArgs(InputMethods.InputMode inputMode) {
        Bundle args = new Bundle();
        args.putString(LANGUAGE_ID_KEY, inputMode.mParent.getLanguageId());
        args.putString(INPUT_MODE_KEY, inputMode.mInputMode);
        args.putString(INPUT_MODE_DISPLAY_KEY, inputMode.getDisplayInputMode());
        return args;
    }

    public InputPrefs(PreferenceScreen screen, Bundle args) {
        this.context = screen.getContext();
        this.screen = screen;
        this.languageId = args.getString(LANGUAGE_ID_KEY);
        this.inputModeName = args.getString(INPUT_MODE_KEY);
        this.displayInputModeName = args.getString(INPUT_MODE_DISPLAY_KEY);
        if (TextUtils.isEmpty(this.languageId) || this.inputModeName == null) {
            throw new IllegalArgumentException(args.keySet().toString());
        }
    }

    public String getInputModeName() {
        if (this.inputModeName.equals(InputMethods.CHINESE_INPUT_MODE_HANDWRITING_FULL_SCREEN)) {
            this.displayInputModeName = this.context.getText(R.string.handwriting).toString();
        } else if (this.inputModeName.equals(InputMethods.CHINESE_INPUT_MODE_PINYIN_QWERTY)) {
            this.displayInputModeName = this.context.getText(R.string.pinyin).toString();
        }
        return this.displayInputModeName;
    }

    public void onResume() {
        InputMethods.InputMode handwritingInputMode;
        InputMethods.Language inputLanguage = InputMethods.from(this.context).findInputLanguage(this.languageId);
        if (inputLanguage == null) {
            throw new IllegalStateException(String.format("Can't find #%s in available input languages", this.languageId));
        }
        InputMethods.InputMode inputMode = inputLanguage.getInputMode(this.inputModeName);
        if (inputMode == null) {
            throw new IllegalStateException(String.format("Can't find %s in available input modes", this.inputModeName));
        }
        this.screen.setTitle(inputLanguage.getDisplayName());
        if (inputLanguage.isChineseLanguage()) {
            this.screen.removeAll();
            if (inputMode.isHandwriting()) {
                InputMethods.InputMode handwritingInputMode2 = inputLanguage.getHandwritingMode();
                if (handwritingInputMode2 != null) {
                    createChineseHandwritingScreen(this.screen, handwritingInputMode2);
                    return;
                }
                return;
            }
            List<InputMethods.Layout> keyboardLayouts = inputMode.getKeyboardLayouts();
            if (keyboardLayouts != null && !keyboardLayouts.isEmpty() && !inputMode.mInputMode.equals(InputMethods.CHINESE_INPUT_MODE_DOUBLEPINYIN)) {
                createKeyboardLayoutsScreen(this.screen, inputMode);
                return;
            }
            return;
        }
        if (inputLanguage.isJapaneseLanguage()) {
            this.screen.removeAll();
            if (inputMode.isHandwriting() && (handwritingInputMode = inputLanguage.getHandwritingMode()) != null) {
                createJapaneseHandwritingScreen(this.screen, handwritingInputMode);
                return;
            }
            return;
        }
        this.screen.removeAll();
        if (inputMode.isHandwriting()) {
            InputMethods.InputMode handwritingInputMode3 = inputLanguage.getHandwritingMode();
            if (handwritingInputMode3 != null) {
                createAlphaHandwritingScreen(this.screen, handwritingInputMode3);
                return;
            }
            return;
        }
        List<InputMethods.Layout> keyboardLayouts2 = inputMode.getKeyboardLayouts();
        if (keyboardLayouts2 != null && !keyboardLayouts2.isEmpty()) {
            createKeyboardLayoutsScreen(this.screen, inputMode);
        }
    }

    private PreferenceScreen createKeyboardLayoutsScreen(PreferenceScreen preferenceScreen, InputMethods.InputMode inputMode) {
        if (!inputMode.mParent.isChineseLanguage()) {
            PreferenceCategory category = new PreferenceCategory(this.context);
            category.setTitle(this.context.getText(R.string.layouts_title));
            preferenceScreen.addPreference(category);
            KeyboardLayoutPreference layoutPref = new KeyboardLayoutPreference(this.context, inputMode, 1);
            CharSequence title = this.context.getText(R.string.layout_title);
            layoutPref.setTitle(title);
            layoutPref.setDialogIcon(R.drawable.swype_logo);
            layoutPref.setDialogTitle(title);
            category.addPreference(layoutPref);
        }
        if (inputMode.mParent.isChineseSimplified() && InputMethods.isChineseInputModePinyin(inputMode.mInputMode)) {
            PreferenceCategory mohuCategory = new PreferenceCategory(this.context);
            mohuCategory.setTitle(this.context.getText(R.string.chs_fuzzy_pinyin_setting));
            preferenceScreen.addPreference(mohuCategory);
            createChineseSimplifiedMohuPiny(mohuCategory);
        }
        return preferenceScreen;
    }

    private void createKoreanConsonantInput(PreferenceScreen preferenceScreen) {
        PreferenceCategory category = new PreferenceCategory(this.context);
        category.setTitle(this.context.getText(R.string.pref_header_advanced));
        preferenceScreen.addPreference(category);
        CheckBoxPreference consonantInputCheckBox = new CheckBoxPreference(this.context);
        consonantInputCheckBox.setKey(UserPreferences.PREF_ENABLE_KOREAN_CONSONANT_INPUT);
        consonantInputCheckBox.setTitle(R.string.enable_korean_consonant_input);
        consonantInputCheckBox.setChecked(UserPreferences.from(this.context).getEnableKoreanConsonantInput());
        category.addPreference(consonantInputCheckBox);
    }

    private void createChineseSimplifiedMohuPiny(PreferenceCategory mohuCategory) {
        CheckBoxPreference checkBox = new CheckBoxPreference(this.context);
        checkBox.setKey(UserPreferences.CHINESE_FUZZY_Z_ZH_C_CH_S_SH);
        checkBox.setTitle(R.string.fuzzy_pinyin_z_zh_c_ch_s_sh);
        mohuCategory.addPreference(checkBox);
        CheckBoxPreference checkBox2 = new CheckBoxPreference(this.context);
        checkBox2.setKey(UserPreferences.CHINESE_FUZZY_N_NG);
        checkBox2.setTitle(R.string.fuzzy_pinyin_n_ng);
        checkBox2.setSummary(R.string.fuzzy_pinyin_summary_n_ng);
        mohuCategory.addPreference(checkBox2);
        CheckBoxPreference checkBox3 = new CheckBoxPreference(this.context);
        checkBox3.setKey(UserPreferences.CHINESE_FUZZY_N_L);
        checkBox3.setTitle(R.string.fuzzy_pinyin_n_l);
        mohuCategory.addPreference(checkBox3);
        CheckBoxPreference checkBox4 = new CheckBoxPreference(this.context);
        checkBox4.setKey(UserPreferences.CHINESE_FUZZY_R_L);
        checkBox4.setTitle(R.string.fuzzy_pinyin_r_l);
        mohuCategory.addPreference(checkBox4);
        CheckBoxPreference checkBox5 = new CheckBoxPreference(this.context);
        checkBox5.setKey(UserPreferences.CHINESE_FUZZY_F_H);
        checkBox5.setTitle(R.string.fuzzy_pinyin_f_h);
        mohuCategory.addPreference(checkBox5);
    }

    private PreferenceScreen createAutoRecognitionLayoutsScreen(PreferenceScreen parent) {
        PreferenceScreen autoRecognitionScreen = parent.getPreferenceManager().createPreferenceScreen(this.context);
        autoRecognitionScreen.setTitle(R.string.handwriting_recognize_speed_title);
        autoRecognitionScreen.setSummary(R.string.handwriting_recognize_speed_summary);
        autoRecognitionScreen.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.nuance.swype.input.settings.InputPrefs.1
            @Override // android.preference.Preference.OnPreferenceClickListener
            public boolean onPreferenceClick(Preference preference) {
                Bundle args = new Bundle();
                InputPrefs.this.doShowDialog(11, args);
                return true;
            }
        });
        return autoRecognitionScreen;
    }

    private PreferenceScreen createPenSizeSettingScreen(PreferenceScreen parent) {
        PreferenceScreen penSizeScreen = parent.getPreferenceManager().createPreferenceScreen(this.context);
        penSizeScreen.setTitle(R.string.handwriting_pen_size_dialog_title);
        penSizeScreen.setSummary(R.string.handwriting_pen_size_dialog_summary);
        penSizeScreen.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.nuance.swype.input.settings.InputPrefs.2
            @Override // android.preference.Preference.OnPreferenceClickListener
            public boolean onPreferenceClick(Preference preference) {
                Bundle args = new Bundle();
                InputPrefs.this.doShowDialog(12, args);
                return true;
            }
        });
        return penSizeScreen;
    }

    private PreferenceScreen createColorLayoutsScreen(PreferenceScreen parent) {
        PreferenceScreen widthScreen = parent.getPreferenceManager().createPreferenceScreen(this.context);
        widthScreen.setTitle(R.string.handwriting_recognize_color_title);
        widthScreen.setSummary(R.string.handwriting_recognize_color_summary);
        widthScreen.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.nuance.swype.input.settings.InputPrefs.3
            @Override // android.preference.Preference.OnPreferenceClickListener
            public boolean onPreferenceClick(Preference preference) {
                Bundle args = new Bundle();
                InputPrefs.this.doShowDialog(13, args);
                return true;
            }
        });
        return widthScreen;
    }

    private PreferenceScreen createAlphaHandwritingScreen(PreferenceScreen preferenceScreen, InputMethods.InputMode handwritingInputMode) {
        PreferenceCategory categoryPreference = new PreferenceCategory(this.context);
        categoryPreference.setTitle(R.string.handwriting_input_description);
        preferenceScreen.addPreference(categoryPreference);
        CheckBoxPreference punctuationCheckbox = new CheckBoxPreference(this.context);
        punctuationCheckbox.setKey(handwritingInputMode.getMixLetterAndPunctuationEnabledPrefKey());
        punctuationCheckbox.setChecked(handwritingInputMode.isMixLetterAndPunctuationEnabled());
        punctuationCheckbox.setTitle(this.context.getText(R.string.punctuation));
        punctuationCheckbox.setSummary(this.context.getText(R.string.handwriting_input_mix_punctuation_description));
        categoryPreference.addPreference(punctuationCheckbox);
        CheckBoxPreference symbolCheckbox = new CheckBoxPreference(this.context);
        symbolCheckbox.setKey(handwritingInputMode.getLetterAndSymbolEnabledPrefKey());
        symbolCheckbox.setChecked(handwritingInputMode.isMixLetterAndSymbolEnabled());
        symbolCheckbox.setTitle(this.context.getText(R.string.symbols));
        symbolCheckbox.setSummary(this.context.getText(R.string.handwriting_input_mix_symbols_description));
        categoryPreference.addPreference(symbolCheckbox);
        CheckBoxPreference digitCheckbox = new CheckBoxPreference(this.context);
        digitCheckbox.setKey(handwritingInputMode.getMixLetterAndDigitEnglishEnabledPrefKey());
        digitCheckbox.setChecked(handwritingInputMode.isMixLetterAndDigitEnglishEnabled());
        digitCheckbox.setTitle(this.context.getText(R.string.digits));
        digitCheckbox.setSummary(this.context.getText(R.string.handwriting_input_mix_digits_description));
        categoryPreference.addPreference(digitCheckbox);
        CheckBoxPreference autospaceCheckbox = new CheckBoxPreference(this.context);
        autospaceCheckbox.setKey(handwritingInputMode.getAutoSpacePrefKey());
        autospaceCheckbox.setTitle(this.context.getText(R.string.pref_auto_space_title));
        autospaceCheckbox.setSummary(this.context.getText(R.string.pref_auto_space_summary));
        autospaceCheckbox.setChecked(handwritingInputMode.isAutoSpaceEnabled());
        categoryPreference.addPreference(autospaceCheckbox);
        preferenceScreen.addPreference(createAutoRecognitionLayoutsScreen(preferenceScreen));
        preferenceScreen.addPreference(createPenSizeSettingScreen(preferenceScreen));
        preferenceScreen.addPreference(createColorLayoutsScreen(preferenceScreen));
        return preferenceScreen;
    }

    private PreferenceScreen createChineseHandwritingScreen(PreferenceScreen handwritingScreen, InputMethods.InputMode handwritingInputMode) {
        handwritingScreen.addPreference(createAutoRecognitionLayoutsScreen(handwritingScreen));
        handwritingScreen.addPreference(createPenSizeSettingScreen(handwritingScreen));
        handwritingScreen.addPreference(createColorLayoutsScreen(handwritingScreen));
        CheckBoxPreference integratedCheckbox = new CheckBoxPreference(this.context);
        integratedCheckbox.setKey(handwritingInputMode.getMixLetterAndIntegratedEnabledPrefKey());
        integratedCheckbox.setChecked(handwritingInputMode.isMixLetterAndIntegratedEnabled());
        integratedCheckbox.setTitle(this.context.getText(R.string.integrated));
        integratedCheckbox.setSummary(this.context.getText(R.string.handwriting_input_mix_integrated_description));
        handwritingScreen.addPreference(integratedCheckbox);
        final CheckBoxPreference abcCheckbox = new CheckBoxPreference(this.context);
        abcCheckbox.setKey(handwritingInputMode.getMixLetterAndABCEnabledPrefKey());
        abcCheckbox.setChecked(handwritingInputMode.isMixLetterAndABCEnabled());
        abcCheckbox.setTitle(this.context.getText(R.string.ABC));
        abcCheckbox.setSummary(this.context.getText(R.string.handwriting_input_mix_chinese_abc_description));
        if (!handwritingInputMode.isMixLetterAndIntegratedEnabled()) {
            abcCheckbox.setEnabled(false);
        }
        handwritingScreen.addPreference(abcCheckbox);
        final CheckBoxPreference punctuationCheckbox = new CheckBoxPreference(this.context);
        punctuationCheckbox.setKey(handwritingInputMode.getMixLetterAndPunctuationEnabledPrefKey());
        punctuationCheckbox.setChecked(handwritingInputMode.isMixLetterAndPunctuationEnabled());
        punctuationCheckbox.setTitle(this.context.getText(R.string.punctuation));
        punctuationCheckbox.setSummary(this.context.getText(R.string.handwriting_input_mix_chinese_punctuation_description));
        if (!handwritingInputMode.isMixLetterAndIntegratedEnabled()) {
            punctuationCheckbox.setEnabled(false);
        }
        handwritingScreen.addPreference(punctuationCheckbox);
        final CheckBoxPreference symbolCheckbox = new CheckBoxPreference(this.context);
        symbolCheckbox.setKey(handwritingInputMode.getLetterAndSymbolEnabledPrefKey());
        symbolCheckbox.setChecked(handwritingInputMode.isMixLetterAndSymbolEnabled());
        symbolCheckbox.setTitle(this.context.getText(R.string.symbols));
        symbolCheckbox.setSummary(this.context.getText(R.string.handwriting_input_mix_chinese_symbols_description));
        if (!handwritingInputMode.isMixLetterAndIntegratedEnabled()) {
            symbolCheckbox.setEnabled(false);
        }
        handwritingScreen.addPreference(symbolCheckbox);
        final CheckBoxPreference digitCheckbox = new CheckBoxPreference(this.context);
        digitCheckbox.setKey(handwritingInputMode.getMixLetterAndDigitChineseEnabledPrefKey());
        digitCheckbox.setChecked(handwritingInputMode.isMixLetterAndDigitChineseEnabled());
        digitCheckbox.setTitle(this.context.getText(R.string.digits));
        digitCheckbox.setSummary(this.context.getText(R.string.handwriting_input_mix_chinese_digits_description));
        digitCheckbox.setChecked(true);
        if (!handwritingInputMode.isMixLetterAndIntegratedEnabled()) {
            digitCheckbox.setEnabled(false);
        }
        handwritingScreen.addPreference(digitCheckbox);
        integratedCheckbox.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.nuance.swype.input.settings.InputPrefs.4
            @Override // android.preference.Preference.OnPreferenceClickListener
            public boolean onPreferenceClick(Preference pref) {
                boolean checked = ((CheckBoxPreference) pref).isChecked();
                abcCheckbox.setEnabled(checked);
                punctuationCheckbox.setEnabled(checked);
                symbolCheckbox.setEnabled(checked);
                digitCheckbox.setEnabled(checked);
                return true;
            }
        });
        return handwritingScreen;
    }

    private PreferenceScreen createJapaneseHandwritingScreen(PreferenceScreen preferenceScreen, InputMethods.InputMode handwritingInputMode) {
        PreferenceCategory categoryPreference = new PreferenceCategory(this.context);
        categoryPreference.setTitle(R.string.pref_header_advanced);
        preferenceScreen.addPreference(categoryPreference);
        CheckBoxPreference abcCheckbox = new CheckBoxPreference(this.context);
        abcCheckbox.setKey(handwritingInputMode.getMixLetterAndABCEnabledPrefKey());
        abcCheckbox.setChecked(handwritingInputMode.isMixLetterAndABCEnabled());
        abcCheckbox.setTitle(this.context.getText(R.string.ABC));
        abcCheckbox.setSummary(this.context.getText(R.string.handwriting_input_mix_japanese_abc_description));
        preferenceScreen.addPreference(abcCheckbox);
        CheckBoxPreference punctuationCheckbox = new CheckBoxPreference(this.context);
        punctuationCheckbox.setKey(handwritingInputMode.getMixLetterAndPunctuationEnabledPrefKey());
        punctuationCheckbox.setChecked(handwritingInputMode.isMixLetterAndPunctuationEnabled());
        punctuationCheckbox.setTitle(this.context.getText(R.string.punctuation));
        punctuationCheckbox.setSummary(this.context.getText(R.string.handwriting_input_mix_punctuation_description));
        categoryPreference.addPreference(punctuationCheckbox);
        CheckBoxPreference symbolCheckbox = new CheckBoxPreference(this.context);
        symbolCheckbox.setKey(handwritingInputMode.getLetterAndSymbolEnabledPrefKey());
        symbolCheckbox.setChecked(handwritingInputMode.isMixLetterAndSymbolEnabled());
        symbolCheckbox.setTitle(this.context.getText(R.string.symbols));
        symbolCheckbox.setSummary(this.context.getText(R.string.handwriting_input_mix_symbols_description));
        categoryPreference.addPreference(symbolCheckbox);
        CheckBoxPreference digitCheckbox = new CheckBoxPreference(this.context);
        digitCheckbox.setKey(handwritingInputMode.getMixLetterAndDigitJapaneseEnabledPrefKey());
        digitCheckbox.setChecked(handwritingInputMode.isMixLetterAndDigitJapaneseEnabled());
        digitCheckbox.setTitle(this.context.getText(R.string.digits));
        digitCheckbox.setSummary(this.context.getText(R.string.handwriting_input_mix_digits_description));
        categoryPreference.addPreference(digitCheckbox);
        preferenceScreen.addPreference(createAutoRecognitionLayoutsScreen(preferenceScreen));
        return preferenceScreen;
    }

    public Dialog createRecognitionSpeedDialog() {
        DisplayMetrics display = IMEApplication.from(this.context).getDisplay();
        ViewGroup view = (ViewGroup) ((LayoutInflater) this.context.getSystemService("layout_inflater")).inflate(R.layout.recognition_speed_dialog, (ViewGroup) null);
        Resources res = this.context.getResources();
        final String unit = XMLResultsHandler.SEP_SPACE + ((Object) res.getText(R.string.handwriting_recognize_speed_accept_dialog_unit));
        final int min = res.getInteger(R.integer.handwriting_auto_recognize_min_ms);
        int max = res.getInteger(R.integer.handwriting_auto_recognize_max_ms) - min;
        final int defaultValue = res.getInteger(R.integer.handwriting_auto_recognize_default_ms);
        UserPreferences sp = IMEApplication.from(this.context).getUserPreferences();
        this.mValue = getAutoDelay(sp, UserPreferences.HWR_AUTO_ACCEPT_RECOGNIZE, defaultValue);
        final TextView valueLongPress = (TextView) view.findViewById(R.id.value_recognition_speed);
        valueLongPress.setText(formatNumber(this.mValue / 1000.0d) + unit);
        SeekBar longPressSeekBar = (SeekBar) view.findViewById(R.id.speed_seekbar);
        if (display.density < 0.8f) {
            longPressSeekBar.setPadding((int) this.context.getResources().getDimension(R.dimen.seek_bar_padding), 0, (int) this.context.getResources().getDimension(R.dimen.seek_bar_padding), 0);
        }
        longPressSeekBar.setMax(max / 100);
        longPressSeekBar.setProgress((this.mValue - min) / 100);
        longPressSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.nuance.swype.input.settings.InputPrefs.6
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                InputPrefs.this.mValue = (progress * 100) + min;
                valueLongPress.setText(InputPrefs.this.formatNumber(InputPrefs.this.mValue / 1000.0d) + unit);
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(this.context).setTitle(R.string.handwriting_recognize_speed_title).setIcon(R.drawable.swype_logo).setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() { // from class: com.nuance.swype.input.settings.InputPrefs.7
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                InputPrefs.this.saveAutoDelay(UserPreferences.HWR_AUTO_ACCEPT_RECOGNIZE, InputPrefs.this.mValue);
                StatisticsManager.SessionStatsScribe sessionScribe = StatisticsManager.getSessionStatsScribe(InputPrefs.this.context);
                if (sessionScribe != null) {
                    sessionScribe.recordSettingsChange(UserPreferences.HWR_AUTO_ACCEPT_RECOGNIZE, Integer.valueOf(InputPrefs.this.mValue), Integer.valueOf(defaultValue));
                }
            }
        }).setNegativeButton(R.string.cancel_button, (DialogInterface.OnClickListener) null);
        builder.setView(view);
        return builder.create();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String formatNumber(double number) {
        return NumberFormat.getInstance(new Locale(this.context.getResources().getConfiguration().locale.getLanguage())).format(number);
    }

    public Dialog createStrokeWidthDialog() {
        DisplayMetrics display = IMEApplication.from(this.context).getDisplay();
        ViewGroup view = (ViewGroup) ((LayoutInflater) this.context.getSystemService("layout_inflater")).inflate(R.layout.stroke_width_dialog, (ViewGroup) null);
        Resources res = this.context.getResources();
        final String unit = XMLResultsHandler.SEP_SPACE + ((Object) res.getText(R.string.handwriting_pen_size_dialog_unit));
        final int min = res.getInteger(R.integer.handwriting_pen_size_min_pixels);
        int max = res.getInteger(R.integer.handwriting_pen_size_max_pixels) - min;
        DisplayMetrics dm = res.getDisplayMetrics();
        final int defaultValue = (int) ((IMEApplication.from(this.context).isScreenLayoutTablet() ? 7 : 4) * dm.density);
        UserPreferences sp = IMEApplication.from(this.context).getUserPreferences();
        this.mValue = getPenSize(sp, UserPreferences.HWR_PEN_SIZE, defaultValue);
        final TextView valueLongPress = (TextView) view.findViewById(R.id.value_stroke_width);
        valueLongPress.setText(formatNumber(this.mValue) + unit);
        SeekBar longPressSeekBar = (SeekBar) view.findViewById(R.id.stroke_width_seekbar);
        if (display.density < 0.8f) {
            longPressSeekBar.setPadding((int) this.context.getResources().getDimension(R.dimen.seek_bar_padding), 0, (int) this.context.getResources().getDimension(R.dimen.seek_bar_padding), 0);
        }
        longPressSeekBar.setMax(max);
        longPressSeekBar.setProgress(this.mValue - min);
        longPressSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.nuance.swype.input.settings.InputPrefs.8
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                InputPrefs.this.mValue = min + progress;
                valueLongPress.setText(InputPrefs.this.formatNumber(InputPrefs.this.mValue) + unit);
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(this.context).setTitle(R.string.handwriting_pen_size_dialog_title).setIcon(R.drawable.swype_logo).setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() { // from class: com.nuance.swype.input.settings.InputPrefs.9
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                InputPrefs.this.savePenSize(UserPreferences.HWR_PEN_SIZE, InputPrefs.this.mValue);
                StatisticsManager.SessionStatsScribe sessionScribe = StatisticsManager.getSessionStatsScribe(InputPrefs.this.context);
                if (sessionScribe != null) {
                    sessionScribe.recordSettingsChange(UserPreferences.HWR_PEN_SIZE, Integer.valueOf(InputPrefs.this.mValue), Integer.valueOf(defaultValue));
                }
            }
        }).setNegativeButton(R.string.cancel_button, (DialogInterface.OnClickListener) null);
        builder.setView(view);
        return builder.create();
    }

    public Dialog createColorPickerDialog() {
        ViewGroup view = (ViewGroup) ((LayoutInflater) this.context.getSystemService("layout_inflater")).inflate(R.layout.pen_color_dialog, (ViewGroup) null);
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.color_picker_llayout);
        layout.setVerticalScrollBarEnabled(true);
        layout.setHorizontalScrollBarEnabled(true);
        GridView gridview = (GridView) view.findViewById(R.id.gridview);
        final ImageView selected = (ImageView) view.findViewById(R.id.color_picker_selected);
        if (!UserPreferences.from(this.context).contains(UserPreferences.HWR_AUTO_ACCEPT_COLOR)) {
            setThemeColorToDefaultColor();
        }
        this.mColorIndex = getPenColorIndex(UserPreferences.from(this.context), UserPreferences.HWR_AUTO_ACCEPT_COLOR, 24);
        GradientDrawable shape = (GradientDrawable) this.context.getResources().getDrawable(mThumbIds[this.mColorIndex].intValue());
        shape.setColor(COLORS[this.mColorIndex]);
        selected.setImageDrawable(shape);
        ImageAdapter imAdapter = new ImageAdapter(this.context);
        gridview.setAdapter((ListAdapter) imAdapter);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.nuance.swype.input.settings.InputPrefs.10
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                InputPrefs.this.mColorIndex = arg2;
                GradientDrawable shape2 = (GradientDrawable) InputPrefs.this.context.getResources().getDrawable(InputPrefs.mThumbIds[InputPrefs.this.mColorIndex].intValue());
                shape2.setColor(InputPrefs.COLORS[InputPrefs.this.mColorIndex]);
                selected.setImageDrawable(shape2);
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(this.context).setTitle(R.string.handwriting_recognize_color_title).setIcon(R.drawable.swype_logo).setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() { // from class: com.nuance.swype.input.settings.InputPrefs.11
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                InputPrefs.this.savePenColorIndex(UserPreferences.HWR_AUTO_ACCEPT_COLOR, InputPrefs.this.mColorIndex);
            }
        }).setNegativeButton(R.string.cancel_button, (DialogInterface.OnClickListener) null);
        builder.setView(view);
        return builder.create();
    }

    public static int getAutoDelay(UserPreferences sp, String key, int defValue) {
        try {
            int delay = sp.getInt(key, defValue);
            return delay;
        } catch (ClassCastException e) {
            sp.remove(key);
            sp.setInt(key, defValue);
            return defValue;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void saveAutoDelay(String key, int delay) {
        UserPreferences.from(this.context).setInt(key, delay);
    }

    public static int getPenSize(UserPreferences sp, String key, int defValue) {
        try {
            int size = sp.getInt(key, defValue);
            return size;
        } catch (ClassCastException e) {
            sp.remove(key);
            sp.setInt(key, defValue);
            return defValue;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void savePenSize(String key, int delay) {
        UserPreferences.from(this.context).setInt(key, delay);
    }

    /* loaded from: classes.dex */
    public class ImageAdapter extends BaseAdapter {
        private Context mContext;

        public ImageAdapter(Context c) {
            this.mContext = c;
        }

        @Override // android.widget.Adapter
        public int getCount() {
            return InputPrefs.mThumbIds.length;
        }

        @Override // android.widget.Adapter
        public Object getItem(int position) {
            return (position < 0 || position >= 25) ? InputPrefs.mThumbIds[0] : InputPrefs.mThumbIds[position];
        }

        @Override // android.widget.Adapter
        public long getItemId(int position) {
            return 0L;
        }

        @Override // android.widget.Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                imageView = new ImageView(this.mContext);
                imageView.setLayoutParams(new AbsListView.LayoutParams(this.mContext.getResources().getDimensionPixelSize(R.dimen.color_picker_grid_cell_size_length), this.mContext.getResources().getDimensionPixelSize(R.dimen.color_picker_grid_cell_size_length)));
                imageView.setScaleType(ImageView.ScaleType.CENTER);
            } else if (convertView instanceof ImageView) {
                imageView = (ImageView) convertView;
            } else {
                return null;
            }
            GradientDrawable shape = (GradientDrawable) InputPrefs.this.context.getResources().getDrawable(InputPrefs.mThumbIds[position].intValue());
            shape.setColor(InputPrefs.COLORS[position]);
            imageView.setImageDrawable(shape);
            return imageView;
        }
    }

    private int getPenColorIndex(UserPreferences sp, String key, int defValue) {
        int index = defValue;
        try {
            int index2 = sp.getInt(key, defValue);
            if (index2 > 24) {
                index = defValue;
                sp.remove(key);
                sp.setInt(key, defValue);
                return index;
            }
            return index2;
        } catch (ClassCastException e) {
            sp.remove(key);
            sp.setInt(key, defValue);
            return index;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void savePenColorIndex(String key, int index) {
        UserPreferences.from(this.context).setInt(key, index);
    }

    public static int getPenColor(UserPreferences sp, String key, int defValue, Context context) {
        if (!sp.contains(UserPreferences.HWR_AUTO_ACCEPT_COLOR)) {
            return IMEApplication.from(context).getThemedColor(R.attr.traceColor);
        }
        int index = defValue;
        try {
            index = sp.getInt(key, defValue);
        } catch (ClassCastException e) {
            sp.remove(key);
            sp.setInt(key, defValue);
        }
        return COLORS[index];
    }

    public static void setHWRThemePenColor(UserPreferences sp, String key, int themeColor, Context context) {
        if (ThemeManager.isDownloadableThemesEnabled() && themeColor == -16777216) {
            themeColor = IMEApplication.from(context).getThemedColor(R.attr.traceColorHwrForMLS);
        }
        int index = 0;
        while (true) {
            if (index >= mThumbIds.length) {
                break;
            }
            if (COLORS[index] != themeColor) {
                index++;
            } else {
                COLORS[index] = COLORS[mThumbIds.length - 1];
                break;
            }
        }
        COLORS[mThumbIds.length - 1] = themeColor;
        sp.remove(key);
    }

    private void setThemeColorToDefaultColor() {
        IMEApplication app = IMEApplication.from(this.context);
        int themeColor = 0;
        if (app != null) {
            themeColor = app.getThemedColor(R.attr.traceColor);
        }
        int index = 0;
        while (true) {
            if (index >= mThumbIds.length) {
                break;
            }
            if (COLORS[index] != themeColor) {
                index++;
            } else {
                COLORS[index] = COLORS[mThumbIds.length - 1];
                break;
            }
        }
        COLORS[mThumbIds.length - 1] = themeColor;
    }
}
