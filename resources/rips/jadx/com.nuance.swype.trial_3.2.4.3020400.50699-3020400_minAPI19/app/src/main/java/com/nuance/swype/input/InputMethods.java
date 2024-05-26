package com.nuance.swype.input;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.text.TextUtils;
import android.util.Xml;
import android.view.inputmethod.InputMethodSubtype;
import com.nuance.android.compat.SharedPreferencesEditorCompat;
import com.nuance.connect.internal.common.Document;
import com.nuance.input.swypecorelib.SwypeCoreLibrary;
import com.nuance.input.swypecorelib.T9Write;
import com.nuance.input.swypecorelib.XT9CoreInput;
import com.nuance.input.swypecorelib.XT9Status;
import com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.xmlResults.XMLResultsHandler;
import com.nuance.swype.connect.Connect;
import com.nuance.swype.input.BuildInfo;
import com.nuance.swype.input.DatabaseConfig;
import com.nuance.swype.input.KeyboardEx;
import com.nuance.swype.util.AdsUtil;
import com.nuance.swype.util.LocalizationUtils;
import com.nuance.swype.util.LogManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.xmlpull.v1.XmlPullParserException;

@SuppressLint({"DefaultLocale"})
/* loaded from: classes.dex */
public class InputMethods {
    public static final String CHINESE_INPUT_MODE_CANGJIE_NINE_KEYS = "cangjie_nine_keys";
    public static final String CHINESE_INPUT_MODE_CANGJIE_QWERTY = "cangjie_qwerty";
    public static final String CHINESE_INPUT_MODE_CANGJIE_TYPE = "cangjie";
    public static final String CHINESE_INPUT_MODE_DOUBLEPINYIN = "doublepinyin";
    public static final String CHINESE_INPUT_MODE_HANDWRITING_FULL_SCREEN = "handwriting_full_screen";
    public static final String CHINESE_INPUT_MODE_HANDWRITING_HALF_SCREEN = "handwriting_half_screen";
    public static final String CHINESE_INPUT_MODE_PINYIN_NINE_KEYS = "pinyin_nine_keys";
    public static final String CHINESE_INPUT_MODE_PINYIN_QWERTY = "pinyin_qwerty";
    public static final String CHINESE_INPUT_MODE_PINYIN_TYPE = "pinyin";
    public static final String CHINESE_INPUT_MODE_QUICK_CANGJIE_NINE_KEYS = "quick_cangjie_nine_keys";
    public static final String CHINESE_INPUT_MODE_QUICK_CANGJIE_QWERTY = "quick_cangjie_qwerty";
    public static final String CHINESE_INPUT_MODE_QUICK_CANGJIE_TYPE = "quick_cangjie";
    public static final String CHINESE_INPUT_MODE_STROKE = "stroke";
    public static final String CHINESE_INPUT_MODE_ZHUYIN_NINE_KEYS = "zhuyin_nine_keys";
    public static final String CHINESE_INPUT_MODE_ZHUYIN_QWERTY = "zhuyin_qwerty";
    public static final String CHINESE_INPUT_MODE_ZHUYIN_TYPE = "zhuyin";
    private static final boolean DEFAULT_LANGUAGE_ENABLED = true;
    private static final String GLOBAL_LANG_ID = "GLOBAL";
    public static final String HANDWRITING_INPUT_MODE = "handwriting";
    public static final String KEYBOARD_TRANSLITERATION = "hindiTransliteration";
    public static final int KEYPAD_KEYBOARD_LAYOUT = 1536;
    public static final int KEYPAD_NARATGUL_KEYBOARD_LAYOUT = 1824;
    public static final int KEYPAD_PLUS_KEYBOARD_LAYOUT = 1792;
    public static final int KEYPAD_SKY_KEYBOARD_LAYOUT = 1808;
    private static final int MAX_RECENT_LANGUAGES = 4;
    public static final int MCC_CODE_RUSSIA = 250;
    public static final String MULTITAP_INPUT_MODE = "multitap";
    public static final int QWERTY_KEYBOARD_BILINGUAL_LAYOUT = 3488;
    public static final int QWERTY_KEYBOARD_LAYOUT = 2304;
    public static final int REDUCED_QWERTY_KEYBOARD_LAYOUT = 2560;
    public static final String SAVED_LOCALE_KEY = "savedLocale";
    public static final String SETTING_CURRENT_LANGUAGE = "current_language";
    private static final String SETTING_RECENT_LANGUAGE_DELIMETER = ",";
    private static final String SETTING_TOGGLE_RESTORE_LANGUAGE = "toggle_restore_lang";
    private static final String SETTING_TOGGLE_TEMPORARY = "toggle_temporary";
    private static final String TAG_INPUT_LANGUAGE = "Language";
    private static final String TAG_INPUT_LAYOUT = "Layout";
    private static final String TAG_INPUT_MODE = "InputMode";
    public static final String VIETNAMESE_INPUT_MODE_NATIONAL = "national";
    public static final String VIETNAMESE_INPUT_MODE_TELEX = "telex";
    private static boolean isLocaleChanged;
    private static String mCurrentLanguageId;
    private static String[] recentLanguageCache;
    private Map<String, Language> mAllLanguages;
    protected final Context mContext;
    private Language mCurrentLanguage;
    private String mDefaultLanguageId;
    private final boolean mSpeechBuildEnabled;
    private final boolean mT9TraceBuildEnabled;
    private final boolean mT9WriteAlphaBuildEnabled;
    private final boolean mT9WriteBuildChineseEnabled;
    private final boolean mXT9ChineseInputBuildEnabled;
    private final boolean showNativeNameForDisplay;
    private static final LogManager.Log log = LogManager.getLog("InputMethods");
    protected static final LogManager.Trace trace = LogManager.getTrace();
    private static final String SETTING_HK = "Chinese_HK.enabled";
    private static final String SETTING_KOREAN = "Korean.enabled";
    private static final String SETTING_SIMPLIFY = "Chinese_CN.enabled";
    private static final String SETTING_TW = "Chinese_TW.enabled";
    private static final String SETTING_JA = "Japanese.enabled";
    public static final Set<String> CJK_SETTINGS = Collections.unmodifiableSet(new HashSet(Arrays.asList(SETTING_HK, SETTING_KOREAN, SETTING_SIMPLIFY, SETTING_TW, SETTING_JA)));
    private final List<Language> mInputLanguages = new ArrayList();
    private final List<Language> mAlphaInputLanguageCycling = new ArrayList();
    private final List<Language> mChineseInputLanguageCycling = new ArrayList();
    private Language mFastSwitchedOffLanguage = null;
    private ArrayList<Language> checkedList = new ArrayList<>();

    public static boolean isLocaleChanged() {
        return isLocaleChanged;
    }

    public static void setIsLocaleChanged(boolean isLocaleChanged2) {
        isLocaleChanged = isLocaleChanged2;
    }

    public static InputMethods from(Context context) {
        return IMEApplication.from(context).getInputMethods();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public InputMethods(Context context) {
        this.mContext = context;
        LanguageList langList = new LanguageList(this.mContext);
        SwypeCoreLibrary corelib = IMEApplication.from(context).getSwypeCoreLibMgr().getSwypeCoreLibInstance();
        this.mT9WriteAlphaBuildEnabled = corelib.isWriteAlphaBuildEnabled();
        this.mT9WriteBuildChineseEnabled = corelib.isWriteChineseBuildEnabled();
        this.mT9TraceBuildEnabled = corelib.isTraceBuildEnabled();
        this.mXT9ChineseInputBuildEnabled = corelib.isChineseInputBuildEnabled();
        this.mSpeechBuildEnabled = true;
        this.showNativeNameForDisplay = context.getResources().getBoolean(R.bool.display_native_download_language_name);
        this.mAllLanguages = loadInputMethodConfigurations();
        Language l = this.mAllLanguages.get(langList.getDefaultLanguage());
        if (l != null) {
            this.mDefaultLanguageId = l.mLanguageId;
        }
        SharedPreferences sp = AppPreferences.from(this.mContext).getPreferences();
        List<String> inputLangList = langList.getLanguageList();
        for (String lang : inputLangList) {
            Language langObj = this.mAllLanguages.get(lang);
            if (langObj != null) {
                if (DatabaseConfig.isPossibleDeprecatedLanguage(this.mContext, langObj.mEnglishName) && getSavedInputMode(sp, Integer.toHexString(langObj.mCoreLanguageId), "").equals("")) {
                    if (inputLangList.contains(lang + "_Std") && DatabaseConfig.isUsingDeprecatedLanguageLDB(this.mContext, langObj.mEnglishName)) {
                        langObj.mDefaultInputMode = langObj.defaultGlobalInputMode;
                    }
                    InputMode im = langObj.getCurrentInputMode();
                    saveInputMode(sp, langObj.mLanguageId, im);
                }
                addLanguage(langObj);
            }
        }
        initRecentLanguageCache(context, langList.getDefaultLanguage());
        List<Language> chineseInputLanguages = getChineseInputLanguages();
        if (chineseInputLanguages != null) {
            Iterator<Language> it = chineseInputLanguages.iterator();
            while (it.hasNext()) {
                Iterator<InputMode> it2 = it.next().getAllInputModes().iterator();
                while (it2.hasNext()) {
                    it2.next().setEnabled(true);
                }
            }
        }
        detectLocaleChange();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void detectLocaleChange() {
        AppPreferences appPrefs = AppPreferences.from(this.mContext);
        String previousSavedLocale = appPrefs.getString(SAVED_LOCALE_KEY, null);
        Locale currentLocale = this.mContext.getResources().getConfiguration().locale;
        if (!currentLocale.toString().equals(previousSavedLocale)) {
            setIsLocaleChanged(true);
            appPrefs.setString(SAVED_LOCALE_KEY, currentLocale.toString());
            Language defaultLanguage = findLanguageBestFitsCurrentLocale(currentLocale, this.mInputLanguages);
            if (defaultLanguage != null) {
                this.mDefaultLanguageId = defaultLanguage.mLanguageId;
            }
            Language defaultLanguage2 = setDefaultLanguage(this.mDefaultLanguageId);
            if (defaultLanguage2 != null) {
                defaultLanguage2.setInputMode(defaultLanguage2.mDefaultInputMode);
            } else {
                log.e(String.format("Cannot find default language Id %s locale = %s", this.mDefaultLanguageId, currentLocale));
            }
            resetToggleState();
            IMEApplication.from(this.mContext).getAppPreferences().resetAccessibilityInfo(this.mContext);
            Connect.from(this.mContext).setCurrentLanguage(defaultLanguage2, 3);
        }
    }

    private Map<String, Language> loadInputMethodConfigurations() {
        Map<String, Language> languageConfigs = new HashMap<>();
        Resources res = this.mContext.getResources();
        XmlResourceParser parser = res.getXml(R.xml.inputmethodsconfig);
        Map<String, List<DatabaseConfig.LanguageDB>> langToDbMap = DatabaseConfig.getLanguageDBList(this.mContext, null);
        List<InputMode> globalInputModes = new ArrayList<>();
        Language language = null;
        InputMode inputMode = null;
        Layout layout = null;
        while (true) {
            try {
                int event = parser.next();
                if (event == 1) {
                    break;
                }
                if (event == 2) {
                    String tag = parser.getName();
                    if (TAG_INPUT_LANGUAGE.equals(tag)) {
                        language = new Language(res, this, parser, langToDbMap);
                    } else if (TAG_INPUT_MODE.equals(tag)) {
                        inputMode = language != null ? createInputMode(res, language, parser) : new InputMode(res, null, parser);
                    } else if (TAG_INPUT_LAYOUT.equals(tag)) {
                        layout = new Layout(res, inputMode, parser);
                    }
                } else if (event == 3) {
                    String tag2 = parser.getName();
                    if (TAG_INPUT_LANGUAGE.equals(tag2)) {
                        if (!language.isChineseLanguage() || this.mXT9ChineseInputBuildEnabled) {
                            languageConfigs.put(language.mEnglishName, language);
                        }
                        language = null;
                    } else if (TAG_INPUT_MODE.equals(tag2)) {
                        if (language == null) {
                            inputMode.isGlobal = true;
                            globalInputModes.add(inputMode);
                        } else if (inputMode != null && inputMode.mInputMode != null && (!inputMode.mInputMode.equals(HANDWRITING_INPUT_MODE) || !isChineseInputModeHandwriting(inputMode.mInputMode) || this.mT9WriteAlphaBuildEnabled || this.mT9WriteBuildChineseEnabled)) {
                            language.addInputMode(inputMode);
                        }
                    } else if (TAG_INPUT_LAYOUT.equals(tag2)) {
                        inputMode.addLayout(layout);
                    }
                }
            } catch (IOException e) {
                if (parser != null) {
                    parser.close();
                }
            } catch (XmlPullParserException e2) {
                if (parser != null) {
                    parser.close();
                }
            } catch (Throwable th) {
                if (parser != null) {
                    parser.close();
                }
                throw th;
            }
        }
        if (parser != null) {
            parser.close();
        }
        for (Language lang : languageConfigs.values()) {
            for (InputMode mode : globalInputModes) {
                if (mode.encoding != null && mode.isCompatibleLanguage(lang)) {
                    lang.addInputMode(new InputMode(mode, lang));
                }
            }
        }
        return languageConfigs;
    }

    private InputMode createInputMode(Resources res, Language parent, XmlResourceParser parser) {
        TypedArray a = res.obtainAttributes(Xml.asAttributeSet(parser), R.styleable.XT9);
        String inputMode = a.getString(R.styleable.XT9_inputMode);
        a.recycle();
        if (inputMode == null && parent.mDefaultInputMode != null) {
            inputMode = parent.mDefaultInputMode;
        }
        if (inputMode != null) {
            if (parent.isChineseLanguage()) {
                if (isChineseInputModeHandwriting(inputMode)) {
                    return new ChineseHandwritingInputMode(res, parent, parser);
                }
                return new ChineseInputMode(res, parent, parser);
            }
            if (parent.isKoreanLanguage()) {
                if (inputMode.equals(HANDWRITING_INPUT_MODE)) {
                    return new KoreanHandwritingInputMode(res, parent, parser);
                }
                return new KoreanAmbigInputMode(res, parent, parser);
            }
            if (parent.isJapaneseLanguage()) {
                if (inputMode.equals(HANDWRITING_INPUT_MODE)) {
                    return new JapaneseHandwritingInputMode(res, parent, parser);
                }
                return new JapaneseInputMode(res, parent, parser);
            }
            return new InputMode(res, parent, parser);
        }
        return null;
    }

    void addLanguage(Language language) {
        if (!this.mInputLanguages.contains(language)) {
            this.mInputLanguages.add(language);
        } else {
            log.w(String.format("Duplicate language (%s) - IGNORED", language.mLanguageId));
        }
    }

    public List<Language> getInputLanguages() {
        return this.mInputLanguages;
    }

    public List<Language> getRecentLanguages() {
        ArrayList<Language> recentLanguageList = new ArrayList<>();
        for (String languageId : getRecentLanguages(this.mContext, this.mDefaultLanguageId)) {
            Language language = findInputLanguage(languageId);
            if (language != null) {
                recentLanguageList.add(language);
            }
        }
        return recentLanguageList;
    }

    public void setCheckedLanguages(ArrayList<Language> list) {
        this.checkedList = list;
    }

    public ArrayList<Language> getCheckedLanguages() {
        return this.checkedList;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void refreshRecentLanguages() {
        List<String> languageIds = new ArrayList<>();
        for (String languageId : getRecentLanguages(this.mContext, this.mDefaultLanguageId)) {
            Language language = findInputLanguage(languageId, true);
            if (language != null) {
                String foundId = language.getLanguageId();
                if (!languageIds.contains(foundId)) {
                    if (!foundId.equals(languageId)) {
                        language.setInputMode(getSavedInputModeNoHandwriting(AppPreferences.from(this.mContext).getPreferences(), languageId, language.getCurrentInputMode().mInputMode));
                    }
                    languageIds.add(foundId);
                }
            }
        }
        setRecentLanguages(this.mContext, languageIds);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void forceAddRecentLanguage(String languageId) {
        String[] recentLanguageList = getRecentLanguages(this.mContext, languageId);
        boolean inList = false;
        int length = recentLanguageList.length;
        int i = 0;
        while (true) {
            if (i >= length) {
                break;
            }
            if (!recentLanguageList[i].equals(languageId)) {
                i++;
            } else {
                inList = true;
                break;
            }
        }
        if (!inList) {
            saveLanguageAt(this.mContext, languageId, 1);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Language getLastLanguage() {
        List<Language> recentLanguages = getRecentLanguages();
        if (recentLanguages.size() > 1) {
            return recentLanguages.get(1);
        }
        return null;
    }

    public List<Language> getCompatibleInputLanguages(Language language) {
        List<Language> compatible = new ArrayList<>();
        for (Language checkLang : this.mInputLanguages) {
            if (language != null && language.isCompatibleLanguage(checkLang) && !language.equals(checkLang) && checkLang.isCoreLanguage()) {
                compatible.add(checkLang);
            }
        }
        return compatible;
    }

    public Map<String, Language> getAllLanguages() {
        return this.mAllLanguages;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getLanguageIdIntByLanguageName(String langName) {
        Language lang = this.mAllLanguages.get(langName);
        if (lang != null) {
            return lang.getCoreLanguageId();
        }
        return 0;
    }

    public Language getLanguageById(int languageId) {
        String languageName = Integer.toHexString(languageId);
        for (Language language : this.mInputLanguages) {
            if (languageName.equals(language.getLanguageId())) {
                return language;
            }
        }
        return null;
    }

    public List<Language> getInputLanguagesCopy() {
        return new ArrayList(this.mInputLanguages);
    }

    private List<Language> getChineseInputLanguages() {
        List<Language> chineseInputLanguages = new ArrayList<>();
        for (Language language : this.mInputLanguages) {
            if (language.isChineseLanguage()) {
                chineseInputLanguages.add(language);
            }
        }
        return chineseInputLanguages;
    }

    public boolean isCJKOnDevice() {
        for (Language m : this.mInputLanguages) {
            if (m.isChineseLanguage() || m.isKoreanLanguage() || m.isJapaneseLanguage()) {
                return true;
            }
        }
        return false;
    }

    public static boolean isChineseInputModePinyin(String inputMode) {
        return CHINESE_INPUT_MODE_PINYIN_NINE_KEYS.compareTo(inputMode) == 0 || CHINESE_INPUT_MODE_PINYIN_QWERTY.compareTo(inputMode) == 0;
    }

    public static boolean isChineseNineKeyPinyin(String inputMode) {
        return CHINESE_INPUT_MODE_PINYIN_NINE_KEYS.compareTo(inputMode) == 0;
    }

    public static boolean isChineseDoublePinyin(String inputMode) {
        return CHINESE_INPUT_MODE_DOUBLEPINYIN.compareTo(inputMode) == 0;
    }

    public static boolean isChineseInputModeZhuyin(String inputMode) {
        return CHINESE_INPUT_MODE_ZHUYIN_NINE_KEYS.compareTo(inputMode) == 0 || CHINESE_INPUT_MODE_ZHUYIN_QWERTY.compareTo(inputMode) == 0;
    }

    public static boolean isChineseInputModeHandwriting(String inputMode) {
        return CHINESE_INPUT_MODE_HANDWRITING_FULL_SCREEN.compareTo(inputMode) == 0 || CHINESE_INPUT_MODE_HANDWRITING_HALF_SCREEN.compareTo(inputMode) == 0;
    }

    public static boolean isChineseInputModeCangjie(String inputMode) {
        return CHINESE_INPUT_MODE_CANGJIE_NINE_KEYS.compareTo(inputMode) == 0 || CHINESE_INPUT_MODE_CANGJIE_QWERTY.compareTo(inputMode) == 0;
    }

    public static boolean isChineseInputModeQuickCangjie(String inputMode) {
        return CHINESE_INPUT_MODE_QUICK_CANGJIE_NINE_KEYS.compareTo(inputMode) == 0 || CHINESE_INPUT_MODE_QUICK_CANGJIE_QWERTY.compareTo(inputMode) == 0;
    }

    public static String getChineseInputModeType(String inputMode) {
        if (isChineseInputModePinyin(inputMode)) {
            return CHINESE_INPUT_MODE_PINYIN_TYPE;
        }
        if (isChineseInputModeZhuyin(inputMode)) {
            return CHINESE_INPUT_MODE_ZHUYIN_TYPE;
        }
        if (isChineseInputModeCangjie(inputMode)) {
            return CHINESE_INPUT_MODE_CANGJIE_TYPE;
        }
        if (isChineseInputModeQuickCangjie(inputMode)) {
            return CHINESE_INPUT_MODE_QUICK_CANGJIE_TYPE;
        }
        if (isChineseInputModeHandwriting(inputMode)) {
            return HANDWRITING_INPUT_MODE;
        }
        return inputMode;
    }

    public void setChineseCyclingKeyboardInputMode(int layoutId, String modeName, Language lang) {
        String mode = getChineseInputModeType(modeName);
        lang.getInputMode(mode + getChineseModeNameSuffix(layoutId)).saveAsCurrent();
    }

    private String getChineseModeNameSuffix(int layoutId) {
        if (layoutId == 2304) {
            return "_qwerty";
        }
        if (layoutId != 1536) {
            return "";
        }
        return "_nine_keys";
    }

    public void syncWithCurrentUserConfiguration() {
        this.mAlphaInputLanguageCycling.clear();
        this.mChineseInputLanguageCycling.clear();
        List<Language> inputLanguages = getInputLanguages();
        Language currentLanguage = getCurrentInputLanguage();
        if (currentLanguage != null && !currentLanguage.isEnabled()) {
            boolean found = false;
            int i = (inputLanguages.indexOf(currentLanguage) + 1) % inputLanguages.size();
            while (true) {
                if (i >= inputLanguages.size()) {
                    break;
                }
                Language inputLanguage = inputLanguages.get(i);
                if (!inputLanguage.isEnabled()) {
                    i++;
                } else {
                    inputLanguage.saveAsCurrent();
                    inputLanguage.setEnabled(true);
                    found = true;
                    break;
                }
            }
            if (!found) {
                Language defaultLanguage = findInputLanguage(this.mDefaultLanguageId);
                defaultLanguage.saveAsCurrent();
                defaultLanguage.setEnabled(true);
            }
        }
        for (Language inputLanguage2 : inputLanguages) {
            if (inputLanguage2.isChineseLanguage() && inputLanguage2.isEnabled()) {
                this.mChineseInputLanguageCycling.add(inputLanguage2);
            } else if (inputLanguage2.isEnabled()) {
                this.mAlphaInputLanguageCycling.add(inputLanguage2);
            }
        }
    }

    public int countEnabledLanguageMode() {
        return this.mAlphaInputLanguageCycling.size() + this.mChineseInputLanguageCycling.size();
    }

    public Language getCurrentInputLanguage() {
        if (this.mCurrentLanguage != null && this.mCurrentLanguage.mLanguageId.equals(mCurrentLanguageId)) {
            return this.mCurrentLanguage;
        }
        String currentLanguageId = getSavedLanguage(this.mContext, this.mDefaultLanguageId);
        Language language = findInputLanguage(currentLanguageId);
        if (language == null && (language = setDefaultLanguage(this.mDefaultLanguageId)) == null) {
            log.e("Cannot find the current Language with languageId = " + currentLanguageId);
            return null;
        }
        this.mCurrentLanguage = language;
        mCurrentLanguageId = language.mLanguageId;
        return this.mCurrentLanguage;
    }

    private void setFastSwitchedOffLanguage(Language lang) {
        String str;
        this.mFastSwitchedOffLanguage = lang;
        AppPreferences from = AppPreferences.from(this.mContext);
        if (lang == null) {
            str = "";
        } else {
            str = lang.mLanguageId;
        }
        from.setString(SETTING_TOGGLE_RESTORE_LANGUAGE, str);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Language getFastSwitchedOffLanguage() {
        if (this.mFastSwitchedOffLanguage == null) {
            String switchableLanguageId = AppPreferences.from(this.mContext).getUpgradedString(SETTING_TOGGLE_RESTORE_LANGUAGE, "", "%x");
            this.mFastSwitchedOffLanguage = findInputLanguage(switchableLanguageId);
        }
        return this.mFastSwitchedOffLanguage;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isToggleTemporary() {
        return AppPreferences.from(this.mContext).getBoolean(SETTING_TOGGLE_TEMPORARY, false);
    }

    private void setToggleTemporary(boolean isTemporary) {
        AppPreferences.from(this.mContext).setBoolean(SETTING_TOGGLE_TEMPORARY, isTemporary);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getToggleLanguageName() {
        return getNativeLanguageNameForCJK(getFastSwitchedOffLanguage());
    }

    private void setToggleState(Language restoreLang, boolean isTemp) {
        setFastSwitchedOffLanguage(restoreLang);
        setToggleTemporary(isTemp);
    }

    private void resetToggleState() {
        setToggleState(null, false);
    }

    public void setCurrentLanguage(String langId, String langIdFrom, boolean isTemp) {
        setToggleState(findInputLanguage(langIdFrom), isTemp);
        saveLanguage(this.mContext, langId);
    }

    public void setCurrentLanguage(String languageId) {
        Language lang = getCurrentInputLanguage();
        if (lang == null || !languageId.equals(lang.mLanguageId)) {
            resetToggleState();
        }
        saveLanguage(this.mContext, languageId);
    }

    public void setCurrentLanguageById(int languageId) {
        setCurrentLanguage(Integer.toHexString(languageId));
    }

    private Language setDefaultLanguage(String defaultLangID) {
        Language language = findInputLanguage(defaultLangID);
        if (language != null) {
            language.saveAsCurrent();
            language.setEnabled(true);
        }
        return language;
    }

    public Language findInputLanguage(String languageID) {
        return findInputLanguage(languageID, false);
    }

    public Language findLanguageFromName(String languageName) {
        return this.mAllLanguages.get(languageName);
    }

    private Language findInputLanguage(String languageID, boolean returnSingleOnBilingualFail) {
        for (Language m : this.mInputLanguages) {
            if (m.equals(languageID)) {
                return m;
            }
        }
        String[] languageIds = BilingualLanguage.getLanguageIds(languageID);
        if (languageIds.length > 1) {
            Language firstLanguage = findInputLanguage(languageIds[0]);
            Language secondLanguage = findInputLanguage(languageIds[1]);
            if (firstLanguage != null) {
                if (secondLanguage != null) {
                    Language language = new BilingualLanguage(firstLanguage, secondLanguage);
                    addLanguage(language);
                    return language;
                }
                if (returnSingleOnBilingualFail) {
                    return firstLanguage;
                }
            } else if (returnSingleOnBilingualFail) {
                return secondLanguage;
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Language findCoreInputLanguage(int coreLanguageID) {
        for (Language m : this.mInputLanguages) {
            if (m.mCoreLanguageId == coreLanguageID && m.isCoreLanguage()) {
                return m;
            }
        }
        return null;
    }

    public boolean isInputLanguageCurrent(String languageID) {
        return languageID != null && languageID.equalsIgnoreCase(getSavedLanguage(this.mContext, this.mDefaultLanguageId));
    }

    public Language findLanguageBestFitsCurrentLocale(Locale currentLocale, List<Language> inputLanguages) {
        String currentCountryCode = matchLocaleToSpecificLanguage(currentLocale, currentLocale.getCountry().toLowerCase());
        String currentLangCode = currentLocale.getLanguage().toLowerCase();
        Language bestMatch = null;
        Language matchLangCode = null;
        Language firstOneMatchLangNotCountry = null;
        Iterator<Language> it = inputLanguages.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            Language m = it.next();
            Locale locale = m.locale;
            String langCode = locale.getLanguage().toLowerCase();
            if (currentLangCode.equals(langCode)) {
                String countryCode = locale.getCountry().toLowerCase();
                if (currentCountryCode.equals(countryCode)) {
                    bestMatch = m;
                    break;
                }
                if (matchLangCode == null && countryCode.equals("")) {
                    matchLangCode = m;
                }
                if (firstOneMatchLangNotCountry == null && !currentCountryCode.equals(countryCode)) {
                    firstOneMatchLangNotCountry = m;
                }
            }
        }
        if (bestMatch == null) {
            if (matchLangCode != null) {
                Language bestMatch2 = matchLangCode;
                return bestMatch2;
            }
            Language bestMatch3 = firstOneMatchLangNotCountry;
            return bestMatch3;
        }
        return bestMatch;
    }

    private static String matchLocaleToSpecificLanguage(Locale currentLocale, String currentCountryCode) {
        if (currentLocale.getLanguage().toLowerCase().equalsIgnoreCase("zh") && currentCountryCode.equalsIgnoreCase("MO")) {
            return "hk";
        }
        return currentCountryCode;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Language getDefaultAlphabeticInputLanguage() {
        for (Language language : this.mInputLanguages) {
            if (language.mCoreLanguageId == 265) {
                return language;
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getNativeLanguageNameForCJK(Language language) {
        if (this.mContext == null || language == null) {
            return null;
        }
        Resources res = this.mContext.getResources();
        if (language.isChineseLanguage()) {
            return res.getText(R.string.label_chn_key).toString();
        }
        if (language.isKoreanLanguage()) {
            return res.getText(R.string.label_korean_key).toString();
        }
        if (language.isJapaneseLanguage()) {
            return res.getText(R.string.label_jp_abc).toString();
        }
        return null;
    }

    public void addTextCategory(T9Write t9Write, Language lang) {
        if (t9Write != null && lang != null) {
            t9Write.clearCategory();
            t9Write.addTextCategory();
            InputMode handWritingInputMode = lang.getCurrentInputMode();
            if (handWritingInputMode.isMixLetterAndIntegratedEnabled()) {
                if ((lang.isChineseLanguage() && handWritingInputMode.isMixLetterAndDigitChineseEnabled()) || ((lang.isKoreanLanguage() && handWritingInputMode.isMixLetterAndDigitEnglishEnabled()) || (lang.isJapaneseLanguage() && handWritingInputMode.isMixLetterAndDigitJapaneseEnabled()))) {
                    t9Write.addNumberCategory();
                }
                if (handWritingInputMode.isMixLetterAndPunctuationEnabled()) {
                    t9Write.addPunctuationCategory();
                }
                if (handWritingInputMode.isMixLetterAndSymbolEnabled()) {
                    t9Write.addSymbolCategory();
                }
                if (handWritingInputMode.isMixLetterAndABCEnabled()) {
                    t9Write.addLatinLetterCategory();
                }
            }
        }
    }

    /* loaded from: classes.dex */
    public static class Language implements Comparable<Language> {
        public static final String AUTO_CORRECT = "auto_correction";
        static final int BURMESE_LANGUAGEID = 366;
        static final int BURMESE_ZAWGYI_LANGUAGEID = 452;
        public static final int CHINESE_HONGKONG_LANGUAGEID = 226;
        private static final String CHINESE_HONGKONG_LANGUAGE_ABBR = "HK";
        public static final int CHINESE_SIMP_LANGUAGEID = 225;
        private static final String CHINESE_SIMP_LANGUAGE_ABBR = "CN";
        static final int CHINESE_SINGAPORE_LANGUAGEID = 227;
        public static final int CHINESE_TRAD_LANGUAGEID = 224;
        private static final String CHINESE_TRAD_LANGUAGE_ABBR = "TW";
        public static final String CORRECTION_LEVEL = "correction_level";
        public static final int ENGLISH_LANGUAGEID = 265;
        static final int GERMAN_LANGUAGEID = 263;
        static final int HEBREW_LANGUAGEID = 269;
        static final int HINGLISH_LANGUAGEID = 464;
        static final int JAPANESE_KANA_LANGUAGEID = 1297;
        static final int JAPANESE_ROMAJI_LANGUAGEID = 273;
        static final int KHMER_LANGUAGEID = 300;
        public static final int KOREAN_CJI_LANGUAGEID = 1810;
        static final int KOREAN_LANGUAGEID = 274;
        static final int LANG_ENCODING = 7;
        static final int LANG_INDEX_ANDROID_LANG_ID = 2;
        static final int LANG_INDEX_ENGLISH_NAME = 8;
        static final int LANG_INDEX_ISO_2LETTER_CODE = 0;
        static final int LANG_INDEX_ISO_FULL_CODE = 1;
        static final int LANG_INDEX_NATIVE_NAME = 9;
        static final int LANG_INDEX_XT9_LANG_ID = 3;
        static final int LANG_SUPPORT_AUTO_SPACE = 5;
        static final int LANG_SUPPORT_CAPS = 4;
        static final int LANG_TERM_PUNC = 6;
        static final int LAO_LANGUAGEID = 299;
        public static final String NEXT_WORD_PREDICTION = "next_word_prediction";
        private static final String SPEECH = "nmsp_speech";
        static final int THAI_LANGUAGEID = 286;
        static final int TIBETAN_LANGUAGEID = 332;
        private static final String TRACE = "trace";
        public static final String TRACE_AUTO_ACCEPT = "trace_auto_accept";
        static final int VIETNAMESE_LANGUAGEID = 298;
        private static final String WORD_COMPLETION = "word_completion";
        public final int currencyType;
        public String defaultGlobalInputMode;
        private final List<String> dictionaryList;
        private final List<InputMode> displayModes;
        public final Locale locale;
        public final int mCoreLanguageId;
        private InputMode mCurrentInputMode;
        private boolean mDefaultEnabled;
        public String mDefaultInputMode;
        private final String mEnabledPrefKey;
        private final boolean mEnabledTrace;
        final String mEncoding;
        public final String mEnglishName;
        private final List<InputMode> mInputModes;
        private final boolean mIsTraceFeatureSupport;
        final String mLangRegionCode;
        public final String mLanguageAbbr;
        private final String mLanguageId;
        public final String mNativeLanguageName;
        private InputMethods mParent;
        final boolean mSupportsAutoSpace;
        final boolean mSupportsCaps;
        private boolean mSupportsHwr;
        private CharSequence mTermPunc;
        private InputMethodSubtype subtype;

        /* JADX INFO: Access modifiers changed from: package-private */
        public boolean isGermanLanguage() {
            return this.mCoreLanguageId == 263;
        }

        static boolean isChineseSimplified(int languageId) {
            return languageId == 225 || languageId == 227;
        }

        static boolean isChineseTraditional(int languageId) {
            return languageId == 224 || languageId == 226;
        }

        public static boolean isChineseLanguage(int languageId) {
            return isChineseSimplified(languageId) || isChineseTraditional(languageId);
        }

        public static boolean isJapaneseLanguage(int languageId) {
            return languageId == 273 || languageId == JAPANESE_KANA_LANGUAGEID;
        }

        public static boolean isKoreanLanguage(int languageId) {
            return languageId == 274;
        }

        static boolean isEnglishLanguage(int languageId) {
            return languageId == 265;
        }

        static boolean isThaiLanguage(int languageId) {
            return languageId == 286;
        }

        static boolean isBurmeseLanguage(int languageId) {
            return languageId == 366 || languageId == BURMESE_ZAWGYI_LANGUAGEID;
        }

        static boolean isKhmerLanguage(int languageId) {
            return languageId == 300;
        }

        static boolean isHebrewLanguage(int langaugeId) {
            return langaugeId == 269;
        }

        static boolean isLaoLanguage(int languageId) {
            return languageId == 299;
        }

        static boolean isTibetanLanguage(int languageId) {
            return languageId == 332;
        }

        static boolean isVietnameseLanguage(int languageId) {
            return languageId == 298;
        }

        public static boolean isCJK(int languageId) {
            return isChineseLanguage(languageId) || isKoreanLanguage(languageId) || isJapaneseLanguage(languageId);
        }

        static boolean isHindiLanguage(int languageId) {
            return languageId == 313;
        }

        static boolean isHinglishLanguage(int languageId) {
            return languageId == HINGLISH_LANGUAGEID;
        }

        public boolean isChineseLanguage() {
            return isChineseLanguage(this.mCoreLanguageId);
        }

        public boolean isChineseSimplified() {
            return isChineseSimplified(this.mCoreLanguageId);
        }

        public boolean isChineseTraditional() {
            return isChineseTraditional(this.mCoreLanguageId);
        }

        public boolean isJapaneseLanguage() {
            return isJapaneseLanguage(this.mCoreLanguageId);
        }

        public boolean isKoreanLanguage() {
            return isKoreanLanguage(this.mCoreLanguageId);
        }

        public final boolean isCJK() {
            return isChineseLanguage() || isJapaneseLanguage() || isKoreanLanguage();
        }

        public final boolean isJK() {
            return isJapaneseLanguage() || isKoreanLanguage();
        }

        boolean isCK() {
            return isChineseLanguage() || isKoreanLanguage();
        }

        boolean isEnglishLanguage() {
            return isEnglishLanguage(this.mCoreLanguageId);
        }

        boolean isBurmeseLanguage() {
            return isBurmeseLanguage(this.mCoreLanguageId);
        }

        boolean isKhmerLanguage() {
            return isKhmerLanguage(this.mCoreLanguageId);
        }

        boolean isLaoLanguage() {
            return isLaoLanguage(this.mCoreLanguageId);
        }

        boolean isTibetanLanguage() {
            return isTibetanLanguage(this.mCoreLanguageId);
        }

        boolean isThaiLanguage() {
            return isThaiLanguage(this.mCoreLanguageId);
        }

        public boolean isNonSpacedLanguage() {
            return isBurmeseLanguage() || isKhmerLanguage() || isLaoLanguage() || isThaiLanguage() || isTibetanLanguage();
        }

        public boolean isHindiLanguage() {
            return isHindiLanguage(this.mCoreLanguageId);
        }

        public boolean isHinglishLanguage() {
            return isHinglishLanguage(this.mCoreLanguageId);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public boolean isVietnameseLanguage() {
            return isVietnameseLanguage(this.mCoreLanguageId);
        }

        public Language(Resources res, InputMethods parent, XmlResourceParser parser, Map<String, List<DatabaseConfig.LanguageDB>> langToDbMap) {
            this.mInputModes = new ArrayList();
            this.displayModes = new ArrayList();
            this.dictionaryList = new ArrayList();
            this.mParent = parent;
            TypedArray a = res.obtainAttributes(Xml.asAttributeSet(parser), R.styleable.XT9);
            this.mDefaultInputMode = a.getString(R.styleable.XT9_defaultInputMode);
            this.defaultGlobalInputMode = a.getString(R.styleable.XT9_defaultGlobalInputMode);
            this.currencyType = a.getInt(R.styleable.XT9_currencyType, KeyboardEx.KeyboardSettings.CURRENCY_EURO.value);
            CharSequence[] langValues = a.getTextArray(R.styleable.XT9_language);
            this.mCoreLanguageId = Integer.decode(langValues[3].toString()).intValue();
            this.mLanguageId = Integer.toHexString(this.mCoreLanguageId);
            this.mNativeLanguageName = langValues[9].toString();
            String langAbbr = getLanguageAbbr(langValues[0].toString());
            this.mEnglishName = langValues[8].toString();
            this.mLangRegionCode = langValues[1].toString();
            this.mSupportsCaps = parseBool(langValues[4]);
            this.mSupportsAutoSpace = parseBool(langValues[5]);
            this.mEncoding = langValues[7].toString();
            this.mTermPunc = langValues[6];
            InputMethods.log.d("Language() creation of object for english name: ", this.mEnglishName, " coreLanguageId:", Integer.valueOf(this.mCoreLanguageId));
            String[] parts = this.mLangRegionCode.split(XMLResultsHandler.SEP_HYPHEN);
            String region = parts.length > 1 ? parts[1].toUpperCase() : null;
            if (region != null && this.mCoreLanguageId != 225 && this.mCoreLanguageId != 224 && this.mCoreLanguageId != 226) {
                langAbbr = String.format("%s(%s)", langAbbr, region.equals("GB") ? "UK" : region);
            }
            this.mLanguageAbbr = langAbbr;
            this.mDefaultEnabled = a.getBoolean(R.styleable.XT9_enabled, true);
            this.mIsTraceFeatureSupport = a.getBoolean(R.styleable.XT9_TraceFeatureSupport, this.mParent.mT9TraceBuildEnabled);
            this.mEnabledTrace = a.getBoolean(R.styleable.XT9_enableTrace, true);
            a.recycle();
            this.mEnabledPrefKey = LanguageList.composeLanguageEnabledSPKey(this.mEnglishName);
            this.mSupportsHwr = true;
            BuildInfo.HandwritingType hwType = IMEApplication.from(parent.mContext).getBuildInfo().getHandwritingType();
            if (hwType == BuildInfo.HandwritingType.NONE) {
                this.mSupportsHwr = false;
            } else if (hwType == BuildInfo.HandwritingType.CJK_ONLY) {
                this.mSupportsHwr = isCJK();
            }
            if (this.mSupportsHwr) {
                boolean hasHDB = false;
                Collection<DatabaseConfig.LanguageDB> langDbs = langToDbMap.get(this.mEnglishName);
                if (langDbs != null) {
                    Iterator<DatabaseConfig.LanguageDB> it = langDbs.iterator();
                    while (true) {
                        if (it.hasNext()) {
                            if (it.next().isHDB()) {
                                hasHDB = true;
                                break;
                            }
                        } else {
                            break;
                        }
                    }
                }
                if (!hasHDB) {
                    this.mSupportsHwr = false;
                }
            }
            Collection<DatabaseConfig.LanguageDB> langDbs2 = langToDbMap.get(this.mEnglishName);
            if (langDbs2 != null) {
                for (DatabaseConfig.LanguageDB langDb : langDbs2) {
                    if (langDb.isALMLDB() || langDb.isLDB()) {
                        this.dictionaryList.add(langDb.getFileName());
                    }
                }
            }
            String androidQualifier = langValues[2].toString().trim();
            if (androidQualifier.length() > 0) {
                this.locale = LocalizationUtils.forAndroidQualifier(androidQualifier);
            } else {
                this.locale = LocalizationUtils.forLanguageTag(this.mLangRegionCode);
            }
        }

        public Language(Language lang, String id, String displayName, String displayAbbr, String englishName, Locale locale) {
            this.mInputModes = new ArrayList();
            this.displayModes = new ArrayList();
            this.dictionaryList = new ArrayList();
            this.mLanguageId = id;
            this.mCoreLanguageId = lang.mCoreLanguageId;
            this.mParent = lang.mParent;
            this.mNativeLanguageName = displayName;
            this.mLanguageAbbr = displayAbbr;
            this.mEnglishName = englishName;
            this.mLangRegionCode = lang.mLangRegionCode;
            this.mSupportsCaps = lang.mSupportsCaps;
            this.mSupportsAutoSpace = lang.mSupportsAutoSpace;
            this.mEncoding = lang.mEncoding;
            this.mDefaultEnabled = lang.mDefaultEnabled;
            this.mIsTraceFeatureSupport = lang.mIsTraceFeatureSupport;
            this.mEnabledTrace = lang.mEnabledTrace;
            this.mEnabledPrefKey = lang.mEnabledPrefKey;
            this.mSupportsHwr = lang.mSupportsHwr;
            this.locale = locale;
            this.currencyType = lang.currencyType;
            this.mTermPunc = lang.mTermPunc;
        }

        public Language(Language lang) {
            this(lang, lang.getLanguageId(), lang.mNativeLanguageName, lang.mLanguageAbbr, lang.mEnglishName, lang.locale);
            this.mInputModes.addAll(lang.mInputModes);
            this.mDefaultInputMode = lang.mDefaultInputMode;
        }

        public CharSequence getTerminalPunctuation() {
            return this.mTermPunc;
        }

        public String getLanguageId() {
            return this.mLanguageId;
        }

        public int getCoreLanguageId() {
            return this.mCoreLanguageId;
        }

        public boolean usesLanguage(String languageName) {
            return this.mEnglishName.equals(languageName);
        }

        public boolean isSpeechFeatureEnabled() {
            return this.mParent.mSpeechBuildEnabled && !isChineseLanguage();
        }

        boolean isTraceFeatureSupport() {
            return this.mParent.mT9TraceBuildEnabled && this.mIsTraceFeatureSupport && !isChineseLanguage();
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public boolean supportsHwr() {
            return this.mSupportsHwr;
        }

        public List<String> getDictionaryList() {
            return this.dictionaryList;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public Locale createLocale() {
            Locale loc;
            String countryCode = getCountryCode(this.mLangRegionCode);
            if (!countryCode.isEmpty()) {
                loc = new Locale(isChineseLanguage() ? "zh" : this.mLanguageAbbr, countryCode);
            } else {
                loc = new Locale(isChineseLanguage() ? "zh" : this.mLanguageAbbr);
            }
            return loc;
        }

        public String getDisplayName() {
            return this.mParent.showNativeNameForDisplay ? this.mNativeLanguageName : getLocalizedDisplayName();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @TargetApi(14)
        public String getLocalizedDisplayName() {
            Context context = getContext();
            return (String) getSubtype().getDisplayName(context, context.getPackageName(), context.getApplicationInfo());
        }

        private int getSubtypeResId(Locale loc) {
            Context context = getContext();
            int i = R.string.ime_subtype;
            int identifier = context.getResources().getIdentifier("string/ime_subtype_" + loc.toString().toLowerCase(), null, context.getPackageName());
            return identifier == 0 ? i : identifier;
        }

        @TargetApi(14)
        private InputMethodSubtype createSubtype() {
            Locale loc = createLocale();
            return new InputMethodSubtype(getSubtypeResId(loc), 0, loc.toString(), "keyboard", "", false, false);
        }

        InputMethodSubtype getSubtype() {
            if (this.subtype == null) {
                this.subtype = createSubtype();
            }
            return this.subtype;
        }

        @TargetApi(11)
        public boolean matches(InputMethodSubtype other) {
            InputMethodSubtype st = getSubtype();
            return st.getLocale().equals(other.getLocale()) && other.getMode().equals(st.getMode());
        }

        private String getCountryCode(String langRegionCode) {
            String countryCode = "";
            if (langRegionCode == null || langRegionCode.isEmpty()) {
                return "";
            }
            String[] two = langRegionCode.split(XMLResultsHandler.SEP_HYPHEN);
            if (two != null) {
                if (two.length == 2) {
                    countryCode = two[1];
                } else if (two.length == 1 && "en".equals(two[0])) {
                    countryCode = "us";
                }
            }
            if (!countryCode.isEmpty() && countryCode.equals("en")) {
                countryCode = "us";
            }
            return countryCode;
        }

        private String getLanguageAbbr(String languageAbbr) {
            if (this.mCoreLanguageId == 224) {
                return CHINESE_TRAD_LANGUAGE_ABBR;
            }
            if (this.mCoreLanguageId == 225) {
                return CHINESE_SIMP_LANGUAGE_ABBR;
            }
            if (this.mCoreLanguageId == 226) {
                return CHINESE_HONGKONG_LANGUAGE_ABBR;
            }
            return languageAbbr;
        }

        List<InputMode> getAmbigousInputModes() {
            List<InputMode> ambigousModes = new ArrayList<>();
            for (InputMode inputMode : this.mInputModes) {
                if (!inputMode.mInputMode.equals(InputMethods.MULTITAP_INPUT_MODE) && !inputMode.mInputMode.equals(InputMethods.HANDWRITING_INPUT_MODE) && !InputMethods.isChineseInputModeHandwriting(inputMode.mInputMode)) {
                    ambigousModes.add(inputMode);
                }
            }
            return ambigousModes;
        }

        public List<InputMode> getAllInputModes() {
            return new ArrayList(this.mInputModes);
        }

        public List<InputMode> getChineseInputModes() {
            List<InputMode> ambigousModes = new ArrayList<>();
            boolean handwritingEnabled = UserPreferences.from(this.mParent.mContext).isHandwritingEnabled();
            for (InputMode inputMode : this.mInputModes) {
                if (inputMode.mInputMode.equals(InputMethods.CHINESE_INPUT_MODE_ZHUYIN_NINE_KEYS) || inputMode.mInputMode.equals(InputMethods.CHINESE_INPUT_MODE_ZHUYIN_QWERTY) || ((handwritingEnabled && inputMode.mInputMode.equals(InputMethods.CHINESE_INPUT_MODE_HANDWRITING_FULL_SCREEN)) || ((handwritingEnabled && inputMode.mInputMode.equals(InputMethods.CHINESE_INPUT_MODE_HANDWRITING_HALF_SCREEN)) || inputMode.mInputMode.equals(InputMethods.CHINESE_INPUT_MODE_CANGJIE_NINE_KEYS) || inputMode.mInputMode.equals(InputMethods.CHINESE_INPUT_MODE_CANGJIE_QWERTY) || inputMode.mInputMode.equals(InputMethods.CHINESE_INPUT_MODE_PINYIN_NINE_KEYS) || inputMode.mInputMode.equals(InputMethods.CHINESE_INPUT_MODE_PINYIN_QWERTY) || inputMode.mInputMode.equals(InputMethods.CHINESE_INPUT_MODE_QUICK_CANGJIE_NINE_KEYS) || inputMode.mInputMode.equals(InputMethods.CHINESE_INPUT_MODE_QUICK_CANGJIE_QWERTY) || inputMode.mInputMode.equals(InputMethods.CHINESE_INPUT_MODE_STROKE) || inputMode.mInputMode.equals(InputMethods.CHINESE_INPUT_MODE_DOUBLEPINYIN)))) {
                    ambigousModes.add(inputMode);
                }
            }
            return ambigousModes;
        }

        public List<InputMode> getKoreanInputModes() {
            List<InputMode> inputModes = new ArrayList<>();
            for (InputMode inputMode : this.mInputModes) {
                if (!inputMode.mInputMode.equals(InputMethods.MULTITAP_INPUT_MODE)) {
                    inputModes.add(inputMode);
                }
            }
            return inputModes;
        }

        public InputMode getHandwritingMode() {
            Iterator<InputMode> it = this.mInputModes.iterator();
            while (it.hasNext()) {
                InputMode inputMode = it.next();
                if (inputMode.mInputMode.equals(InputMethods.HANDWRITING_INPUT_MODE) || InputMethods.isChineseInputModeHandwriting(inputMode.mInputMode)) {
                    return inputMode;
                }
            }
            return null;
        }

        public List<InputMode> getDisplayModes() {
            return isChineseLanguage() ? getChineseInputModes() : this.displayModes;
        }

        public InputMode getCurrentInputMode() {
            if (this.mCurrentInputMode != null && this.mCurrentInputMode.isEnabled()) {
                return this.mCurrentInputMode;
            }
            SharedPreferences sp = AppPreferences.from(getContext()).getPreferences();
            InputMode inputMode = getInputMode(InputMethods.getSavedInputMode(sp, this.mLanguageId, this.mDefaultInputMode));
            if (inputMode == null) {
                inputMode = getInputMode(this.mDefaultInputMode);
            }
            if (!inputMode.isEnabled()) {
                Iterator<InputMode> it = this.mInputModes.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    InputMode swapInputMode = it.next();
                    if (swapInputMode.isEnabled() && !swapInputMode.isHandwriting()) {
                        inputMode = swapInputMode;
                        break;
                    }
                }
                InputMethods.saveInputMode(sp, this.mLanguageId, inputMode);
            }
            this.mCurrentInputMode = inputMode;
            return this.mCurrentInputMode;
        }

        public InputMode getCurrentInputModeNoHandwriting() {
            SharedPreferences sp = AppPreferences.from(getContext()).getPreferences();
            InputMode inputMode = getInputMode(InputMethods.getSavedInputModeNoHandwriting(sp, this.mLanguageId, this.mDefaultInputMode));
            if (inputMode == null) {
                inputMode = getInputMode(this.mDefaultInputMode);
            }
            this.mCurrentInputMode = inputMode;
            return this.mCurrentInputMode;
        }

        void saveAsCurrent() {
            InputMethods.saveLanguage(getContext(), this.mLanguageId);
        }

        protected void setEnabled(boolean enabled) {
            AppPreferences.from(getContext()).setBoolean(this.mEnabledPrefKey, enabled);
        }

        public boolean isEnabled() {
            return true;
        }

        public InputMode getInputMode(String inputModeName) {
            for (InputMode m : this.mInputModes) {
                if (m.mInputMode.equals(inputModeName)) {
                    return m;
                }
            }
            return null;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void addInputMode(InputMode inputMode) {
            if (!this.mInputModes.contains(inputMode)) {
                this.mInputModes.add(inputMode);
                if (!inputMode.isHandwriting()) {
                    if (this.mDefaultInputMode == null) {
                        this.mDefaultInputMode = inputMode.mInputMode;
                    }
                    if (!TextUtils.isEmpty(inputMode.getDisplayInputMode())) {
                        this.displayModes.add(inputMode);
                    }
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public Context getContext() {
            return this.mParent.mContext;
        }

        public boolean equals(Object obj) {
            return (obj instanceof Language) && ((Language) obj).mLanguageId.equals(this.mLanguageId);
        }

        public boolean equals(String langId) {
            return this.mLanguageId.equals(langId);
        }

        public boolean isCoreLanguage() {
            return true;
        }

        public int hashCode() {
            return this.mLanguageId.hashCode();
        }

        protected void setInputMode(String mode) {
            InputMode inputMode = getInputMode(mode);
            if (inputMode != null) {
                inputMode.saveAsCurrent();
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public InputMode toggleHandwritingAndInputMode() {
            InputMode nextMode;
            InputMode currentInputMode = getCurrentInputMode();
            if (currentInputMode.isHandwriting()) {
                String savedMode = InputMethods.getSavedInputModeNoHandwriting(AppPreferences.from(getContext()).getPreferences(), getLanguageId(), this.mDefaultInputMode);
                nextMode = getInputMode(savedMode);
            } else if (getHandwritingMode() != null && getHandwritingMode().isEnabled()) {
                nextMode = getHandwritingMode();
            } else {
                nextMode = currentInputMode;
            }
            nextMode.saveAsCurrent();
            return nextMode;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public InputMode switchToHandwritingModeCJK() {
            InputMode currentInputMode = getCurrentInputMode();
            InputMode nextMode = currentInputMode;
            if (isJapaneseLanguage()) {
                if (currentInputMode.isHandwriting()) {
                    List<InputMode> ambigInputModes = getAmbigousInputModes();
                    if (ambigInputModes.size() > 0) {
                        nextMode = ambigInputModes.get(0);
                        nextMode.saveAsCurrent();
                    }
                } else {
                    nextMode = getHandwritingMode();
                    nextMode.saveAsCurrent();
                }
            } else if (isChineseLanguage()) {
                if (currentInputMode.isHandwriting()) {
                    return nextMode;
                }
                AppPreferences appPrefs = AppPreferences.from(currentInputMode.getContext());
                if (appPrefs.getBoolean(AppPreferences.CJK_FULL_SCREEN_ENABLED + currentInputMode.mParent.getCoreLanguageId(), appPrefs.getDefaultFullscreenHandwriting())) {
                    nextMode = currentInputMode.mParent.getInputMode(InputMethods.CHINESE_INPUT_MODE_HANDWRITING_FULL_SCREEN);
                } else {
                    nextMode = currentInputMode.mParent.getInputMode(InputMethods.CHINESE_INPUT_MODE_HANDWRITING_HALF_SCREEN);
                }
                nextMode.saveAsCurrent();
            } else if (isKoreanLanguage()) {
                if (currentInputMode.isHandwriting()) {
                    List<InputMode> ambigInputModes2 = getAmbigousInputModes();
                    if (ambigInputModes2.size() > 0) {
                        nextMode = ambigInputModes2.get(0);
                        nextMode.saveAsCurrent();
                    }
                } else {
                    nextMode = getHandwritingMode();
                    nextMode.saveAsCurrent();
                }
            }
            return nextMode;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public boolean isRecaptureEnabled() {
            return isAutoSpaceSupported() || isNonSpacedLanguage();
        }

        String getTraceEnabledPrefKey() {
            return TRACE;
        }

        public void enableTrace(boolean enabled) {
            UserPreferences.from(getContext()).setBoolean(getTraceEnabledPrefKey(), enabled);
        }

        public String toString() {
            return this.mNativeLanguageName;
        }

        @Override // java.lang.Comparable
        public int compareTo(Language another) {
            return this.mNativeLanguageName.compareTo(another.mNativeLanguageName);
        }

        public boolean isCapitalizationSupported() {
            return this.mSupportsCaps;
        }

        public boolean isAutoSpaceSupported() {
            return this.mSupportsAutoSpace;
        }

        private boolean parseBool(CharSequence cs) {
            return !cs.equals("0");
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public String getIETFLanguageTag() {
            if (this.mLangRegionCode.indexOf(45) <= 1) {
                return this.mLangRegionCode + Document.ID_SEPARATOR + this.mLanguageAbbr.toUpperCase();
            }
            String[] parts = this.mLangRegionCode.split(XMLResultsHandler.SEP_HYPHEN);
            return parts[0] + Document.ID_SEPARATOR + parts[1].toUpperCase();
        }

        public boolean isLatinLanguage() {
            return this.mEncoding.startsWith("Latin");
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public boolean isGestureTipSupported() {
            return isLatinLanguage() || isJapaneseLanguage() || (isChineseLanguage() && (getCurrentInputMode().mInputMode.equals(InputMethods.CHINESE_INPUT_MODE_PINYIN_QWERTY) || getCurrentInputMode().mInputMode.equals(InputMethods.CHINESE_INPUT_MODE_DOUBLEPINYIN)));
        }

        boolean isCompatibleLanguage(Language language) {
            return (isLatinLanguage() && language.isLatinLanguage()) || (this.mEncoding.equals(language.mEncoding) && !isCJK()) || isKoreanCompatible(language);
        }

        private boolean isKoreanCompatible(Language language) {
            return isKoreanLanguage() && language.isEnglishLanguage();
        }

        public boolean isBilingualLanguage() {
            return false;
        }

        public XT9Status setLanguage(XT9CoreInput input, XT9CoreInput.XT9InputMode xt9InputMode) {
            int coreId;
            if (!DatabaseConfig.isUsingDeprecatedLanguageLDB(getContext(), this.mEnglishName) || (coreId = DatabaseConfig.getDeprecatedLanguageLDBID(getContext(), this.mEnglishName)) == -1) {
                InputMethods.log.d("setLanguage...core languge id.." + this.mCoreLanguageId);
                XT9Status language = input.setLanguage(this.mCoreLanguageId, 0, xt9InputMode);
                int deprecatedLanguageId = DatabaseConfig.getDeprecatedLanguageLDBID(getContext(), this.mEnglishName);
                if (deprecatedLanguageId != -1) {
                    String key = "dlm.already.swap." + deprecatedLanguageId;
                    if (!AppPreferences.from(getContext()).getBoolean(key, false)) {
                        InputMethods.log.i("swap dlm language from " + deprecatedLanguageId + " to " + this.mCoreLanguageId);
                        if (input.dlmSwapLanguage(deprecatedLanguageId, this.mCoreLanguageId)) {
                            InputMethods.log.i("backup dlm");
                            AppPreferences.from(getContext()).setBoolean(key, true);
                            return language;
                        }
                        return language;
                    }
                    return language;
                }
                return language;
            }
            InputMethods.log.d("setLanguage...deprecated languge id.." + coreId);
            return input.setLanguage(coreId, 0, xt9InputMode);
        }

        public XT9Status setLanguage(XT9CoreInput input) {
            return setLanguage(input, input.getDefaultInputMode(this.mCoreLanguageId));
        }
    }

    /* loaded from: classes.dex */
    public static class InputMode {
        private static final String AUTO_CORRECTION = "auto_correction";
        private static final String AUTO_SPACE = "auto_space";
        private static final String CORRECTION_LEVEL = "correction_level";
        private static String HANDWRITING_INPUT_AREA = "handwriting_input_area";
        public static final int JAPANESE_PORTRAIT_LAYOUT_FLICK = 17;
        public static final int JAPANESE_PORTRAIT_LAYOUT_KEYBOARD = 0;
        public static final int JAPANESE_PORTRAIT_LAYOUT_MASK = 16;
        public static final int JAPANESE_PORTRAIT_LAYOUT_MULTITAP = 16;
        public static final int JAPANESE_PORTRAIT_LAYOUT_MULTITAPANDFLICK = 18;
        private static final String KEYBOARD_LAYOUT = "keyboard_layout";
        private static final String MIX_LETTER_AND_ABC = "mix_letter_abc";
        private static final String MIX_LETTER_AND_DIGIT = "mix_letter_digit";
        private static final String MIX_LETTER_AND_DIGIT_CHINESE = "mix_letter_digit_chinese";
        private static final String MIX_LETTER_AND_DIGIT_ENGLISH = "mix_letter_digit_english";
        private static final String MIX_LETTER_AND_DIGIT_JAPANESE = "mix_letter_digit_japanese";
        private static final String MIX_LETTER_AND_INTEGRATED = "mix_letter_integrated";
        private static final String MIX_LETTER_AND_PUNCTUATION = "mix_letter_punctuation";
        private static final String MIX_LETTER_AND_SYMBOL = "mix_letter_and_symbol";
        private static final String NEXT_WORD_PREDICTION = "next_word_prediction";
        private static final String PREF_HANDWRITING_ON_CHINESE_KEYBOARD = "handwriting_on_chinese_keyboard";
        private static final String PREF_TRACE_ON_CHINESE_KEYBOARD = "trace_on_chinese_keyboard";
        private static final String RECAPTURE = "recapture";
        private static final String TRACE = "trace";
        private static final String TRACE_AUTO_ACCEPT = "trace_auto_accept";
        private static final String WORD_COMPLETION = "word_completion";
        private final CharSequence[] compatibleLanguages;
        private String encoding;
        private boolean isGlobal;
        public String mCurrentJapaneseLayout;
        protected boolean mDefaultEnabled;
        public final int mDefaultLayoutId;
        private int mDisplayInputMode;
        private String mDisplayInputModeString;
        public boolean mEnableJapaneseHiragana;
        public boolean mEnableJapaneseHiraganaSmall;
        public boolean mEnableJapaneseJIS;
        public boolean mEnableJapaneseJISLevel1;
        public boolean mEnableJapaneseJISLevel2;
        public boolean mEnableJapaneseJISLevel3;
        public boolean mEnableJapaneseJISLevel4;
        public boolean mEnableJapaneseKatakana;
        public boolean mEnableJapaneseKatakanaSmall;
        protected String mEnabledPrefKey;
        private boolean mEnabledTrace;
        private int mHandwritingInputArea;
        public int mId;
        public String mInputMode;
        public final List<Layout> mLayouts;
        public boolean mMixLetterAndABC;
        public boolean mMixLetterAndDigitChinese;
        public boolean mMixLetterAndDigitEnglish;
        public boolean mMixLetterAndDigitJapanese;
        public boolean mMixLetterAndIntegrated;
        public boolean mMixLetterAndPunctuation;
        public boolean mMixLetterAndSymbol;
        public final Language mParent;
        private boolean mTraceAutoAccept;

        public InputMode(Resources res, Language parent, XmlResourceParser parser) {
            this.mLayouts = new ArrayList();
            this.mId = 0;
            this.mCurrentJapaneseLayout = null;
            this.mParent = parent;
            TypedArray a = res.obtainAttributes(Xml.asAttributeSet(parser), R.styleable.XT9);
            this.mInputMode = a.getString(R.styleable.XT9_inputMode);
            this.mDefaultLayoutId = a.getInt(R.styleable.XT9_defaultLayoutId, 0);
            this.mDefaultEnabled = a.getBoolean(R.styleable.XT9_enabled, true);
            this.mDisplayInputMode = a.getResourceId(R.styleable.XT9_inputModeName, -1);
            boolean defaultEnabledTrace = this.mParent == null || this.mParent.mEnabledTrace;
            this.mEnabledTrace = a.getBoolean(R.styleable.XT9_enableTrace, defaultEnabledTrace);
            this.mTraceAutoAccept = a.getBoolean(R.styleable.XT9_traceAutoAccept, true);
            this.encoding = a.getString(R.styleable.XT9_encoding);
            if (this.encoding == null) {
                this.encoding = this.mParent != null ? this.mParent.mEncoding : "";
            }
            this.isGlobal = a.getBoolean(R.styleable.XT9_isGlobal, false);
            this.mHandwritingInputArea = 1;
            this.mEnabledPrefKey = getLanguageId() + "." + this.mInputMode + ".enabled";
            this.mMixLetterAndSymbol = true;
            this.mMixLetterAndPunctuation = true;
            this.mMixLetterAndDigitEnglish = false;
            this.mMixLetterAndDigitChinese = true;
            this.mMixLetterAndIntegrated = true;
            this.mMixLetterAndABC = true;
            this.mEnableJapaneseHiragana = true;
            this.mEnableJapaneseHiraganaSmall = true;
            this.mEnableJapaneseKatakana = true;
            this.mEnableJapaneseKatakanaSmall = true;
            this.mEnableJapaneseJIS = true;
            this.mEnableJapaneseJISLevel1 = true;
            this.mEnableJapaneseJISLevel2 = true;
            this.mEnableJapaneseJISLevel3 = true;
            this.mEnableJapaneseJISLevel4 = true;
            this.mMixLetterAndDigitJapanese = true;
            this.compatibleLanguages = a.getTextArray(R.styleable.XT9_compatibleLanguages);
            if (this.compatibleLanguages != null) {
                Arrays.sort(this.compatibleLanguages);
            }
            a.recycle();
            if (this.mInputMode == null && this.mParent != null) {
                this.mInputMode = this.mParent.mDefaultInputMode;
            }
        }

        public InputMode(InputMode inputMode, Language parent) {
            this(inputMode, parent, inputMode.mInputMode, inputMode.mDisplayInputModeString);
        }

        public InputMode(InputMode inputMode, Language parent, String id, String inputModeName) {
            this.mLayouts = new ArrayList();
            this.mId = 0;
            this.mCurrentJapaneseLayout = null;
            this.mParent = parent;
            this.mInputMode = id;
            this.mDefaultLayoutId = inputMode.mDefaultLayoutId;
            this.mDefaultEnabled = inputMode.mDefaultEnabled;
            this.mDisplayInputMode = inputMode.mDisplayInputMode;
            this.mDisplayInputModeString = inputModeName;
            this.mEnabledTrace = inputMode.mEnabledTrace;
            this.mTraceAutoAccept = inputMode.mTraceAutoAccept;
            this.mHandwritingInputArea = inputMode.mHandwritingInputArea;
            this.encoding = inputMode.encoding;
            this.isGlobal = inputMode.isGlobal;
            this.mEnabledPrefKey = getLanguageId() + "." + this.mInputMode + ".enabled";
            this.mMixLetterAndSymbol = inputMode.mMixLetterAndSymbol;
            this.mMixLetterAndPunctuation = inputMode.mMixLetterAndPunctuation;
            this.mMixLetterAndDigitEnglish = inputMode.mMixLetterAndDigitEnglish;
            this.mMixLetterAndDigitChinese = inputMode.mMixLetterAndDigitChinese;
            this.mMixLetterAndIntegrated = inputMode.mMixLetterAndIntegrated;
            this.mMixLetterAndABC = inputMode.mMixLetterAndABC;
            this.mEnableJapaneseHiragana = inputMode.mEnableJapaneseHiragana;
            this.mEnableJapaneseHiraganaSmall = inputMode.mEnableJapaneseHiraganaSmall;
            this.mEnableJapaneseKatakana = inputMode.mEnableJapaneseKatakana;
            this.mEnableJapaneseKatakanaSmall = inputMode.mEnableJapaneseKatakanaSmall;
            this.mEnableJapaneseJIS = inputMode.mEnableJapaneseJIS;
            this.mEnableJapaneseJISLevel1 = inputMode.mEnableJapaneseJISLevel1;
            this.mEnableJapaneseJISLevel2 = inputMode.mEnableJapaneseJISLevel2;
            this.mEnableJapaneseJISLevel3 = inputMode.mEnableJapaneseJISLevel3;
            this.mEnableJapaneseJISLevel4 = inputMode.mEnableJapaneseJISLevel4;
            this.mMixLetterAndDigitJapanese = inputMode.mMixLetterAndDigitJapanese;
            for (Layout layout : inputMode.mLayouts) {
                this.mLayouts.add(new Layout(layout, this));
            }
            this.compatibleLanguages = inputMode.compatibleLanguages;
        }

        public String getDisplayInputMode() {
            if (this.mDisplayInputMode == -1) {
                if (this.mDisplayInputModeString != null) {
                    return this.mDisplayInputModeString;
                }
                return this.mParent != null ? this.mParent.getDisplayName() : "";
            }
            String displayName = getContext().getResources().getString(this.mDisplayInputMode);
            return this.mDisplayInputModeString != null ? String.format(displayName, this.mDisplayInputModeString) : this.mParent != null ? String.format(displayName, this.mParent.getDisplayName()) : displayName;
        }

        public int getDisplayInputModeID() {
            return this.mDisplayInputMode;
        }

        public boolean isGlobal() {
            return this.isGlobal;
        }

        public boolean isMultitapMode() {
            return this.mInputMode.equals(InputMethods.MULTITAP_INPUT_MODE);
        }

        public boolean isHandwriting() {
            return this.mInputMode.equals(InputMethods.HANDWRITING_INPUT_MODE) || InputMethods.isChineseInputModeHandwriting(this.mInputMode);
        }

        public boolean isHandwritingUCRModeEnabled() {
            return isHandwriting() && UserPreferences.from(getContext()).isHwrUCRModeEnabled() && this.mParent != null && this.mParent.mEnglishName != null && (this.mParent.mEnglishName.contains("English") || this.mParent.mEnglishName.contains("French") || this.mParent.mEnglishName.contains("German") || this.mParent.mEnglishName.contains("Italian") || this.mParent.mEnglishName.contains("Spanish") || this.mParent.mEnglishName.contains("Arabic"));
        }

        public boolean isHandwritingEnabled() {
            if (isHandwriting()) {
                return UserPreferences.from(getContext()).isHandwritingEnabled();
            }
            return true;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public boolean isCompatibleLanguage(Language language) {
            if (this.compatibleLanguages != null) {
                return Arrays.binarySearch(this.compatibleLanguages, language.mEnglishName) >= 0;
            }
            return language.mEncoding.startsWith(this.encoding);
        }

        public final String getLanguageId() {
            return this.mParent != null ? this.mParent.mLanguageId : InputMethods.GLOBAL_LANG_ID;
        }

        public String getEnabledPrefKey() {
            return this.mEnabledPrefKey;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void addLayout(Layout layout) {
            if (!this.mLayouts.contains(layout)) {
                this.mLayouts.add(layout);
            } else {
                InputMethods.log.w(String.format("Duplicated layout(%#x) for inputMode(%s) - IGNORED", Integer.valueOf(layout.mLayoutId), this.mInputMode));
            }
        }

        public Layout getCurrentLayoutForCJK() {
            return getCurrentLayoutForCJK(getScreenOrientation());
        }

        public Layout getCurrentLayoutForCJK(int orientation) {
            setSymLayoutDisabledCJK();
            int layoutId = InputMethods.getKeyboardLayoutId(getContext(), getLanguageId(), getModeNameAsKey(), orientation, this.mDefaultLayoutId);
            Layout layout = findLayout(layoutId);
            if (layout == null || !layout.isEnabled()) {
                if (layout == null) {
                    layout = findLayout(this.mDefaultLayoutId);
                }
                if (layout != null && !layout.isEnabled()) {
                    layout = getNextEnabledLayout(layout);
                    layout.setEnabled(true);
                }
                if (layout == null && (layout = findLayout(this.mDefaultLayoutId)) != null) {
                    layout.saveAsCurrent();
                }
            }
            return layout;
        }

        private void setSymLayoutDisabledCJK() {
            for (Layout layout : this.mLayouts) {
                if (layout.mLayoutId == layout.getContext().getResources().getInteger(R.integer.symbols_keyboard_id)) {
                    layout.setEnabled(false);
                }
            }
        }

        public void setCurrentJapaneseLayout(String layout) {
            this.mCurrentJapaneseLayout = layout;
        }

        public Layout getCurrentLayout() {
            return getCurrentLayout(getScreenOrientation());
        }

        public Layout getCurrentLayout(int orientation) {
            if (this.mParent != null && this.mParent.isCJK()) {
                return getCurrentLayoutForCJK(orientation);
            }
            int layoutId = getKeyboardLayoutId(orientation, this.mDefaultLayoutId);
            Layout layout = findLayout(layoutId);
            if (layout == null) {
                layout = findLayout(this.mDefaultLayoutId);
            }
            if (layout == null && this.mLayouts != null && !this.mLayouts.isEmpty()) {
                layout = this.mLayouts.get(0);
            }
            if (layout == null) {
                InputMethods.log.e(String.format("Error: cannot find current or default layout for languageId(%s) with inputMode(%s)", getLanguageId(), this.mInputMode));
                return layout;
            }
            return layout;
        }

        public void saveAsCurrent() {
            setCurrent();
        }

        public void setCurrent() {
            InputMethods.saveInputMode(AppPreferences.from(getContext()).getPreferences(), getLanguageId(), this);
            if (AdsUtil.sAdsSupported) {
                IMEApplication.from(getContext()).getAdSessionTracker().setHandwriting(isHandwriting());
            }
            this.mParent.mCurrentInputMode = this;
        }

        public void setEnabled(boolean enabled) {
            AppPreferences.from(getContext()).setBoolean(this.mEnabledPrefKey, enabled);
            this.mParent.setEnabled(enabled);
        }

        public boolean isEnabled() {
            UserPreferences userPrefs = UserPreferences.from(getContext());
            boolean enabled = AppPreferences.from(getContext()).getBoolean(this.mEnabledPrefKey, this.mDefaultEnabled) && this.mParent.isEnabled();
            if (enabled && isHandwriting()) {
                return userPrefs.isHandwritingEnabled();
            }
            return enabled;
        }

        public String getRecapturePrefKey() {
            return this.mEnabledPrefKey + RECAPTURE;
        }

        public boolean isRecaptureEnabled() {
            return UserPreferences.from(getContext()).getBoolean(getRecapturePrefKey(), true);
        }

        public boolean isNextWordPredictionEnabled() {
            return UserPreferences.from(getContext()).isNextWordPredictionEnabled();
        }

        public String getAutoSpacePrefKey() {
            return this.mEnabledPrefKey + "auto_space";
        }

        public boolean isAutoSpaceEnabled() {
            return UserPreferences.from(getContext()).getBoolean(getAutoSpacePrefKey(), false);
        }

        public String getWordCompletionPrefKey() {
            return this.mEnabledPrefKey + WORD_COMPLETION;
        }

        public int getWordCompletionPoint() {
            return Integer.parseInt(UserPreferences.from(getContext()).getString(getWordCompletionPrefKey(), "1"));
        }

        public String getTraceEnabledPrefKey() {
            return this.mEnabledPrefKey + TRACE;
        }

        public boolean isTraceEnabled() {
            this.mEnabledTrace = UserPreferences.from(getContext()).getBoolean(getTraceEnabledPrefKey(), this.mEnabledTrace);
            return this.mEnabledTrace && this.mParent.isTraceFeatureSupport();
        }

        public String getTraceAutoAcceptEnabledPrefKey() {
            return this.mEnabledPrefKey + "trace_auto_accept";
        }

        public boolean isTraceAutoAcceptEnabled() {
            this.mTraceAutoAccept = UserPreferences.from(getContext()).getBoolean(getTraceAutoAcceptEnabledPrefKey(), this.mTraceAutoAccept);
            return this.mTraceAutoAccept;
        }

        private Layout getNextEnabledLayout(Layout currentLayout) {
            Layout nextLayout;
            int start = this.mLayouts.indexOf(currentLayout);
            if (start < 0) {
                start = 0;
            }
            int count = this.mLayouts.size();
            do {
                start = (start + 1) % count;
                nextLayout = this.mLayouts.get(start);
                if (nextLayout.isEnabled()) {
                    break;
                }
            } while (!nextLayout.equals(currentLayout));
            nextLayout.saveAsCurrent();
            return nextLayout;
        }

        public Layout getNextLayout() {
            return getNextEnabledLayout(getCurrentLayoutForCJK());
        }

        public int getDefaultPortraitLayoutOptions() {
            return InputMethods.getPortraitLayoutOptions(getContext(), getLanguageId(), this.mParent.mDefaultInputMode, 1, 0);
        }

        public int getDefaultPortraitKeypadOptions() {
            return InputMethods.getPortraitKeypadOptions(getContext(), getLanguageId(), this.mParent.mDefaultInputMode, 1, 18);
        }

        private int getKeyboardLayoutId(int orientation, int defaulKeyboardtLayoutId) {
            String key = composeKeyForKeyboardLayout(orientation);
            return Integer.decode(AppPreferences.from(getContext()).getString(key, String.valueOf(defaulKeyboardtLayoutId))).intValue();
        }

        public String getLetterAndSymbolEnabledPrefKey() {
            return MIX_LETTER_AND_SYMBOL;
        }

        public boolean isMixLetterAndSymbolEnabled() {
            this.mMixLetterAndSymbol = UserPreferences.from(getContext()).getBoolean(getLetterAndSymbolEnabledPrefKey(), this.mMixLetterAndSymbol);
            return this.mMixLetterAndSymbol;
        }

        public String getMixLetterAndPunctuationEnabledPrefKey() {
            return MIX_LETTER_AND_PUNCTUATION;
        }

        public boolean isMixLetterAndPunctuationEnabled() {
            this.mMixLetterAndPunctuation = UserPreferences.from(getContext()).getBoolean(getMixLetterAndPunctuationEnabledPrefKey(), this.mMixLetterAndPunctuation);
            return this.mMixLetterAndPunctuation;
        }

        public String getPortaitLayoutOptionsPrefKey() {
            return InputMethods.composeKeyForPortraitLayoutOptions(getLanguageId(), this.mInputMode, 1);
        }

        public String getPortaitKeypadOptionsPrefKey() {
            return InputMethods.composeKeyForPortraitKeypadOptions(getLanguageId(), this.mInputMode, 1);
        }

        private String composeKeyForKeyboardLayout(int orientation) {
            return "keyboard_layout." + InputMethods.composeIntKey(orientation);
        }

        public Layout findLayout(int layoutId) {
            for (Layout layout : this.mLayouts) {
                if (layout.mLayoutId == layoutId) {
                    return layout;
                }
            }
            return null;
        }

        public List<Layout> getKeyboardLayouts() {
            List<Layout> layouts = new ArrayList<>();
            for (Layout layout : this.mLayouts) {
                if (layout.mLayoutId == 1536 || layout.mLayoutId == 2304 || layout.mLayoutId == 2560 || layout.mLayoutId == 1792 || layout.mLayoutId == 1808 || layout.mLayoutId == 1824) {
                    layouts.add(layout);
                }
            }
            return layouts;
        }

        protected Context getContext() {
            return this.mParent.getContext();
        }

        protected int getScreenOrientation() {
            if (this.mParent == null || !this.mParent.isCK()) {
                return getContext().getResources().getConfiguration().orientation;
            }
            return 1;
        }

        public boolean equals(Object o) {
            if (!(o instanceof InputMode)) {
                return false;
            }
            InputMode inputMode = (InputMode) o;
            return inputMode.mInputMode.equals(this.mInputMode) && inputMode.mParent.equals(this.mParent);
        }

        protected boolean isAtLeastOneLayoutIsEnabled() {
            Iterator<Layout> it = this.mLayouts.iterator();
            while (it.hasNext()) {
                if (it.next().isEnabled()) {
                    return true;
                }
            }
            return false;
        }

        protected void ensureAtleastOneLayoutIsEnabled() {
            if (!isAtLeastOneLayoutIsEnabled()) {
                Layout defaultLayout = findLayout(this.mDefaultLayoutId);
                if (defaultLayout != null) {
                    defaultLayout.setEnabled(true);
                } else {
                    this.mLayouts.get(0).setEnabled(true);
                }
            }
        }

        public String getMixLetterAndABCEnabledPrefKey() {
            return this.mEnabledPrefKey + MIX_LETTER_AND_ABC;
        }

        public boolean isMixLetterAndABCEnabled() {
            this.mMixLetterAndABC = UserPreferences.from(getContext()).getBoolean(getMixLetterAndABCEnabledPrefKey(), this.mMixLetterAndABC);
            return this.mMixLetterAndABC;
        }

        public String getMixLetterAndDigitEnglishEnabledPrefKey() {
            return this.mEnabledPrefKey + MIX_LETTER_AND_DIGIT_ENGLISH;
        }

        public boolean isMixLetterAndDigitEnglishEnabled() {
            this.mMixLetterAndDigitEnglish = UserPreferences.from(getContext()).getBoolean(getMixLetterAndDigitEnglishEnabledPrefKey(), this.mMixLetterAndDigitEnglish);
            return this.mMixLetterAndDigitEnglish;
        }

        public String getMixLetterAndDigitChineseEnabledPrefKey() {
            return this.mEnabledPrefKey + MIX_LETTER_AND_DIGIT_CHINESE;
        }

        public boolean isMixLetterAndDigitChineseEnabled() {
            this.mMixLetterAndDigitChinese = UserPreferences.from(getContext()).getBoolean(getMixLetterAndDigitChineseEnabledPrefKey(), this.mMixLetterAndDigitChinese);
            return this.mMixLetterAndDigitChinese;
        }

        public String getMixLetterAndIntegratedEnabledPrefKey() {
            return this.mEnabledPrefKey + MIX_LETTER_AND_INTEGRATED;
        }

        public boolean isMixLetterAndIntegratedEnabled() {
            this.mMixLetterAndIntegrated = UserPreferences.from(getContext()).getBoolean(getMixLetterAndIntegratedEnabledPrefKey(), this.mMixLetterAndIntegrated);
            return this.mMixLetterAndIntegrated;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public String getModeNameAsKey() {
            return isMultitapMode() ? InputMethods.MULTITAP_INPUT_MODE : this.mInputMode;
        }

        public String getMixLetterAndDigitJapaneseEnabledPrefKey() {
            return this.mEnabledPrefKey + MIX_LETTER_AND_DIGIT_JAPANESE;
        }

        public boolean isMixLetterAndDigitJapaneseEnabled() {
            this.mMixLetterAndDigitJapanese = UserPreferences.from(getContext()).getBoolean(getMixLetterAndDigitJapaneseEnabledPrefKey(), this.mMixLetterAndDigitJapanese);
            return this.mMixLetterAndDigitJapanese;
        }

        public String getHandwritingOnKeyboardKey() {
            String str;
            Layout lt = getCurrentLayoutForCJK();
            if (lt != null && lt.mLayoutId == 2304) {
                if (this.mInputMode.equals(InputMethods.CHINESE_INPUT_MODE_PINYIN_QWERTY) || this.mInputMode.equals(InputMethods.CHINESE_INPUT_MODE_ZHUYIN_QWERTY) || this.mInputMode.equals(InputMethods.CHINESE_INPUT_MODE_DOUBLEPINYIN)) {
                    str = "hwrTrace.";
                } else {
                    str = "hwrOnly.";
                }
            } else {
                str = "hwrOnly.";
            }
            return str + PREF_HANDWRITING_ON_CHINESE_KEYBOARD;
        }

        public String getTraceOnKeyboardKey() {
            String str;
            Layout lt = getCurrentLayoutForCJK();
            if (lt != null && lt.mLayoutId == 2304) {
                if (this.mInputMode.equals(InputMethods.CHINESE_INPUT_MODE_PINYIN_QWERTY) || this.mInputMode.equals(InputMethods.CHINESE_INPUT_MODE_ZHUYIN_QWERTY) || this.mInputMode.equals(InputMethods.CHINESE_INPUT_MODE_DOUBLEPINYIN)) {
                    str = "hwrTrace.";
                } else {
                    str = "hwrOnly.";
                }
            } else {
                str = "hwrOnly.";
            }
            return str + PREF_TRACE_ON_CHINESE_KEYBOARD;
        }

        public int hashCode() {
            return this.mInputMode.hashCode();
        }
    }

    /* loaded from: classes.dex */
    public static class KoreanAmbigInputMode extends InputMode {
        public KoreanAmbigInputMode(Resources res, Language parent, XmlResourceParser parser) {
            super(res, parent, parser);
        }

        @Override // com.nuance.swype.input.InputMethods.InputMode
        public boolean isEnabled() {
            InputMode hwrInputMode = this.mParent.getHandwritingMode();
            boolean hwrEnabled = false;
            if (hwrInputMode != null) {
                hwrEnabled = hwrInputMode.isEnabled();
            }
            return AppPreferences.from(getContext()).getBoolean(this.mEnabledPrefKey, this.mDefaultEnabled) && (isAtLeastOneLayoutIsEnabled() || hwrEnabled) && this.mParent.isEnabled();
        }

        @Override // com.nuance.swype.input.InputMethods.InputMode
        public void setEnabled(boolean enabled) {
            if (enabled) {
                ensureAtleastOneLayoutIsEnabled();
            }
            super.setEnabled(enabled);
        }
    }

    /* loaded from: classes.dex */
    public static class KoreanHandwritingInputMode extends InputMode {
        public KoreanHandwritingInputMode(Resources res, Language parent, XmlResourceParser parser) {
            super(res, parent, parser);
        }

        @Override // com.nuance.swype.input.InputMethods.InputMode
        public void setEnabled(boolean enabled) {
            AppPreferences.from(getContext()).setBoolean(this.mEnabledPrefKey, enabled);
        }

        @Override // com.nuance.swype.input.InputMethods.InputMode
        public Layout getCurrentLayout() {
            if (this.mLayouts == null || this.mLayouts.size() <= 0) {
                return null;
            }
            return this.mLayouts.get(0);
        }
    }

    /* loaded from: classes.dex */
    public static class ChineseInputMode extends InputMode {
        public ChineseInputMode(Resources res, Language parent, XmlResourceParser parser) {
            super(res, parent, parser);
        }

        @Override // com.nuance.swype.input.InputMethods.InputMode
        public boolean isEnabled() {
            return AppPreferences.from(getContext()).getBoolean(this.mEnabledPrefKey, this.mDefaultEnabled) && isAtLeastOneLayoutIsEnabled();
        }

        @Override // com.nuance.swype.input.InputMethods.InputMode
        public void setEnabled(boolean enabled) {
            if (enabled) {
                ensureAtleastOneLayoutIsEnabled();
            }
            AppPreferences.from(getContext()).setBoolean(this.mEnabledPrefKey, enabled);
        }
    }

    /* loaded from: classes.dex */
    public static class ChineseHandwritingInputMode extends InputMode {
        public ChineseHandwritingInputMode(Resources res, Language parent, XmlResourceParser parser) {
            super(res, parent, parser);
            this.mEnabledPrefKey = getLanguageId() + ".handwriting.enabled";
        }

        @Override // com.nuance.swype.input.InputMethods.InputMode
        public void setEnabled(boolean enabled) {
            if (enabled) {
                ensureAtleastOneLayoutIsEnabled();
            }
            AppPreferences.from(getContext()).setBoolean(this.mEnabledPrefKey, enabled);
        }

        @Override // com.nuance.swype.input.InputMethods.InputMode
        public Layout getCurrentLayout() {
            if (this.mLayouts == null || this.mLayouts.size() <= 0) {
                return null;
            }
            return this.mLayouts.get(0);
        }
    }

    /* loaded from: classes.dex */
    public static class JapaneseInputMode extends InputMode {
        public JapaneseInputMode(Resources res, Language parent, XmlResourceParser parser) {
            super(res, parent, parser);
            TypedArray a = res.obtainAttributes(Xml.asAttributeSet(parser), R.styleable.XT9);
            this.mDefaultEnabled = a.getBoolean(R.styleable.XT9_enabled, true);
            AppPreferences.from(getContext()).setBoolean(this.mEnabledPrefKey, this.mDefaultEnabled);
            a.recycle();
        }

        @Override // com.nuance.swype.input.InputMethods.InputMode
        public void saveAsCurrent() {
            InputMethods.saveInputMode(AppPreferences.from(getContext()).getPreferences(), getLanguageId(), this);
            this.mParent.mCurrentInputMode = this;
        }

        @Override // com.nuance.swype.input.InputMethods.InputMode
        public void setEnabled(boolean enabled) {
            AppPreferences.from(getContext()).setBoolean(this.mEnabledPrefKey, enabled);
        }
    }

    /* loaded from: classes.dex */
    public static class JapaneseHandwritingInputMode extends JapaneseInputMode {
        public JapaneseHandwritingInputMode(Resources res, Language parent, XmlResourceParser parser) {
            super(res, parent, parser);
        }

        @Override // com.nuance.swype.input.InputMethods.InputMode
        public Layout getCurrentLayout() {
            if (this.mLayouts == null || this.mLayouts.size() <= 0) {
                return null;
            }
            return this.mLayouts.get(0);
        }
    }

    /* loaded from: classes.dex */
    public static class Layout {
        private boolean mDefaultEnabled;
        private final int mDisplayLayoutName;
        private String mEnabledPrefKey;
        public final int mLayoutId;
        public final int mLayoutResID;
        public final InputMode mParent;

        public Layout(Resources res, InputMode parent, XmlResourceParser parser) {
            this.mParent = parent;
            TypedArray a = res.obtainAttributes(Xml.asAttributeSet(parser), R.styleable.XT9);
            this.mLayoutId = a.getInt(R.styleable.XT9_layoutId, 0);
            this.mDisplayLayoutName = a.getResourceId(R.styleable.XT9_layoutName, -1);
            this.mLayoutResID = a.getResourceId(R.styleable.XT9_layout, 0);
            this.mDefaultEnabled = a.getBoolean(R.styleable.XT9_enabled, true);
            a.recycle();
            initEnabledPrefKey();
        }

        public Layout(Layout layout, InputMode parent) {
            this.mParent = parent;
            this.mLayoutId = layout.mLayoutId;
            this.mDisplayLayoutName = layout.mDisplayLayoutName;
            this.mLayoutResID = layout.mLayoutResID;
            this.mDefaultEnabled = layout.mDefaultEnabled;
            initEnabledPrefKey();
        }

        private void initEnabledPrefKey() {
            this.mEnabledPrefKey = getLanguageId() + "." + this.mParent.getModeNameAsKey() + InputMethods.composeIntKey(this.mLayoutId) + ".enabled";
        }

        public String getDisplayLayoutName() {
            return this.mDisplayLayoutName == -1 ? this.mParent.getDisplayInputMode() : getContext().getResources().getString(this.mDisplayLayoutName);
        }

        public int getDisplayLayoutNameID() {
            return this.mDisplayLayoutName;
        }

        public void saveAsCurrent() {
            saveAsCurrent(this.mParent.getScreenOrientation());
        }

        public void saveAsCurrent(int orientation) {
            InputMethods.saveKeyboardLayout(getContext(), getLanguageId(), this.mParent.getModeNameAsKey(), orientation, this.mLayoutId);
        }

        public void setEnabled(boolean enabled) {
            AppPreferences.from(getContext()).setBoolean(this.mEnabledPrefKey, enabled);
        }

        public boolean isEnabled() {
            return AppPreferences.from(getContext()).getBoolean(this.mEnabledPrefKey, this.mDefaultEnabled);
        }

        public Context getContext() {
            return this.mParent.getContext();
        }

        private String getLanguageId() {
            if (this.mParent != null && this.mParent.mParent != null) {
                return this.mParent.mParent.mLanguageId;
            }
            return InputMethods.GLOBAL_LANG_ID;
        }

        public boolean equals(Object o) {
            return (o instanceof Layout) && ((Layout) o).mLayoutId == this.mLayoutId;
        }

        public int hashCode() {
            return this.mLayoutId;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void saveInputMode(SharedPreferences sp, String languageId, InputMode inputMode) {
        SharedPreferences.Editor edit = sp.edit();
        boolean isHandwriting = inputMode.isHandwriting();
        String handwritingMode = isHandwriting ? inputMode.mInputMode : "";
        edit.putString(languageId + HANDWRITING_INPUT_MODE, handwritingMode);
        if (!isHandwriting) {
            edit.putString(languageId, inputMode.mInputMode);
        }
        SharedPreferencesEditorCompat.apply(edit);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String getSavedInputMode(SharedPreferences sp, String languageId, String defaultInputMode) {
        String handwritingMode = sp.getString(languageId + HANDWRITING_INPUT_MODE, "");
        return handwritingMode.length() > 0 ? handwritingMode : getSavedInputModeNoHandwriting(sp, languageId, defaultInputMode);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String getSavedInputModeNoHandwriting(SharedPreferences sp, String languageId, String defaultInputMode) {
        return sp.getString(languageId, defaultInputMode);
    }

    public static int getKeyboardLayoutId(Context context, String languageId, String inputMode, int orientation, int defaulKeyboardtLayoutId) {
        String key = composeKeyForKeyboardLayout(languageId, inputMode, orientation);
        return Integer.decode(AppPreferences.from(context).getString(key, Integer.toString(defaulKeyboardtLayoutId))).intValue();
    }

    public static int getPortraitLayoutOptions(Context context, String languageId, String inputMode, int orientation, int defaulPortaitLayoutOptionsId) {
        String key = composeKeyForPortraitLayoutOptions(languageId, inputMode, orientation);
        return Integer.decode(AppPreferences.from(context).getString(key, Integer.toString(defaulPortaitLayoutOptionsId))).intValue();
    }

    public static int getPortraitKeypadOptions(Context context, String languageId, String inputMode, int orientation, int defaulPortaitKeypadOptionsId) {
        String key = composeKeyForPortraitKeypadOptions(languageId, inputMode, orientation);
        return Integer.decode(AppPreferences.from(context).getString(key, Integer.toString(defaulPortaitKeypadOptionsId))).intValue();
    }

    public static void saveKeyboardLayout(Context context, String languageId, String inputMode, int orientation, int KeyboardLayoutId) {
        String key = composeKeyForKeyboardLayout(languageId, inputMode, orientation);
        String value = "0x" + Integer.toHexString(KeyboardLayoutId);
        AppPreferences.from(context).setString(key, value);
    }

    private static String composeKeyForKeyboardLayout(String languageId, String inputMode, int orientation) {
        return languageId + "." + inputMode + "." + composeIntKey(orientation);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String composeKeyForPortraitLayoutOptions(String languageId, String inputMode, int orientation) {
        return languageId + "." + inputMode + "." + composeIntKey(orientation) + "Options";
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String composeKeyForPortraitKeypadOptions(String languageId, String inputMode, int orientation) {
        return languageId + "." + inputMode + "." + composeIntKey(orientation) + "KeypadOptions";
    }

    private static String getSavedLanguage(Context context, String defaultLangId) {
        String[] recentLanguages = getRecentLanguages(context, defaultLangId);
        return recentLanguages.length > 0 ? recentLanguages[0] : defaultLangId;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void saveLanguage(Context context, String languageId) {
        saveLanguageAt(context, languageId, 0);
        mCurrentLanguageId = languageId;
    }

    private static void saveLanguageAt(Context context, String languageId, int index) {
        String recentLanguage;
        int i;
        if (index <= 4) {
            String[] recentLanguageList = getRecentLanguages(context, languageId);
            int recentCount = recentLanguageList.length;
            List<String> newRecentLanguages = new ArrayList<>();
            boolean added = false;
            int newRecentCount = 0;
            int i2 = 0;
            while (i2 < recentCount && newRecentCount <= 4) {
                if (newRecentCount == index) {
                    recentLanguage = languageId;
                    added = true;
                    i = i2;
                } else {
                    i = i2 + 1;
                    recentLanguage = recentLanguageList[i2];
                    if (recentLanguage.equals(languageId)) {
                        i2 = i;
                    }
                }
                newRecentLanguages.add(recentLanguage);
                newRecentCount++;
                i2 = i;
            }
            if (!added) {
                newRecentLanguages.add(languageId);
            }
            setRecentLanguages(context, newRecentLanguages);
        }
    }

    private static String[] getRecentLanguages(Context context, String defaultLangId) {
        if (recentLanguageCache == null) {
            initRecentLanguageCache(context, defaultLangId);
        }
        return recentLanguageCache;
    }

    private static void initRecentLanguageCache(Context context, String defaultLangId) {
        recentLanguageCache = AppPreferences.from(context).getUpgradedString(SETTING_CURRENT_LANGUAGE, defaultLangId, "%x").split(",");
        for (int i = 0; i < recentLanguageCache.length; i++) {
            String langID = recentLanguageCache[i];
            String supportedId = DatabaseConfig.mockDeprecatedLanguageID(context, langID);
            recentLanguageCache[i] = supportedId;
        }
    }

    public static void removeFromRecents(String langId) {
        List<String> recentList = new ArrayList<>(Arrays.asList(recentLanguageCache));
        recentList.remove(langId);
        recentLanguageCache = (String[]) recentList.toArray(new String[recentList.size()]);
    }

    public static void setRecentLanguages(Context context, List<String> languageIds) {
        StringBuilder newRecentLanguages = new StringBuilder();
        for (String langId : languageIds) {
            newRecentLanguages.append(langId);
            newRecentLanguages.append(",");
        }
        AppPreferences.from(context).setString(SETTING_CURRENT_LANGUAGE, newRecentLanguages.toString());
        int size = languageIds.size();
        if (recentLanguageCache == null || recentLanguageCache.length > size) {
            recentLanguageCache = new String[size];
        }
        recentLanguageCache = (String[]) languageIds.toArray(recentLanguageCache);
    }

    public boolean isRecentLanguagesBilingualCompatible() {
        for (Language language : getRecentLanguages()) {
            if (getCompatibleInputLanguages(language).size() > 0) {
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String composeIntKey(int value) {
        return Integer.toHexString(value);
    }
}
