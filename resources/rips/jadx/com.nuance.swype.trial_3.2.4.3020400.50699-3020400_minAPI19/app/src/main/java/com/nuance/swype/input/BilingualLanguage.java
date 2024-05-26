package com.nuance.swype.input;

import com.nuance.input.swypecorelib.XT9CoreInput;
import com.nuance.input.swypecorelib.XT9Status;
import com.nuance.swype.input.InputMethods;
import com.nuance.swype.util.LogManager;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/* loaded from: classes.dex */
public class BilingualLanguage extends InputMethods.Language {
    private static final String DISPLAY_LANGUAGE_DELIMITER = "/";
    private static final String LANGUAGE_DELIMITER = ":";
    private static final String SECONDARY_MODE_ID = "secondary_";
    private static final LogManager.Log log = LogManager.getLog("BilingualLanguage");
    private final InputMethods.Language firstLang;
    private final InputMethods.Language secondLang;

    public BilingualLanguage(InputMethods.Language firstLang, InputMethods.Language secondLang) {
        super(firstLang, getLanguageId(firstLang, secondLang), firstLang.mNativeLanguageName + DISPLAY_LANGUAGE_DELIMITER + secondLang.mNativeLanguageName, firstLang.mLanguageAbbr + DISPLAY_LANGUAGE_DELIMITER + secondLang.mLanguageAbbr, firstLang.mEnglishName + DISPLAY_LANGUAGE_DELIMITER + secondLang.mEnglishName, (secondLang.locale.getLanguage().equalsIgnoreCase("TR") || secondLang.locale.getLanguage().equalsIgnoreCase("AZ")) ? secondLang.locale : firstLang.locale);
        this.firstLang = new InputMethods.Language(firstLang);
        this.secondLang = new InputMethods.Language(secondLang);
        String firstLangDefault = firstLang.getCurrentInputModeNoHandwriting().mInputMode;
        List<InputMethods.InputMode> newModes = new ArrayList<>();
        int secondLangInsertIndex = 0;
        Set<String> modes = new HashSet<>();
        for (InputMethods.InputMode inputMode : firstLang.getAllInputModes()) {
            if (inputMode.isCompatibleLanguage(secondLang) || inputMode.isHandwriting()) {
                String modeName = inputMode.mInputMode;
                InputMethods.InputMode newMode = new InputMethods.InputMode(inputMode, this, modeName, firstLang.mNativeLanguageName);
                modes.add(newMode.getDisplayInputMode());
                newModes.add(newMode);
                if (this.mDefaultInputMode == null && modeName.equals(firstLangDefault)) {
                    this.mDefaultInputMode = modeName;
                }
                if (!inputMode.isGlobal()) {
                    secondLangInsertIndex++;
                }
            }
        }
        for (InputMethods.InputMode inputMode2 : secondLang.getAllInputModes()) {
            String modeName2 = inputMode2.getDisplayInputMode();
            if (!inputMode2.isHandwriting() && !modes.contains(modeName2) && inputMode2.isCompatibleLanguage(firstLang)) {
                newModes.add(secondLangInsertIndex, new InputMethods.InputMode(inputMode2, this, SECONDARY_MODE_ID + inputMode2.mInputMode, secondLang.mNativeLanguageName));
                secondLangInsertIndex++;
            }
        }
        if (this.mDefaultInputMode == null) {
            String firstLangDefault2 = firstLang.defaultGlobalInputMode;
            Iterator<InputMethods.InputMode> it = newModes.iterator();
            while (it.hasNext()) {
                String modeName3 = it.next().mInputMode;
                if (modeName3.equals(firstLangDefault2)) {
                    this.mDefaultInputMode = modeName3;
                }
            }
        }
        Iterator<InputMethods.InputMode> it2 = newModes.iterator();
        while (it2.hasNext()) {
            addInputMode(it2.next());
        }
    }

    @Override // com.nuance.swype.input.InputMethods.Language
    public boolean isBilingualLanguage() {
        return true;
    }

    public InputMethods.Language getFirstLanguage() {
        return this.firstLang;
    }

    public InputMethods.Language getSecondLanguage() {
        return this.secondLang;
    }

    @Override // com.nuance.swype.input.InputMethods.Language
    public CharSequence getTerminalPunctuation() {
        CharSequence punctuation = null;
        if (this.firstLang != null) {
            punctuation = this.firstLang.getTerminalPunctuation();
        }
        if ((punctuation == null || punctuation.length() == 0) && this.secondLang != null) {
            CharSequence punctuation2 = this.secondLang.getTerminalPunctuation();
            return punctuation2;
        }
        return punctuation;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.input.InputMethods.Language
    public String getLocalizedDisplayName() {
        return this.firstLang.getLocalizedDisplayName() + DISPLAY_LANGUAGE_DELIMITER + this.secondLang.getLocalizedDisplayName();
    }

    @Override // com.nuance.swype.input.InputMethods.Language
    public XT9Status setLanguage(XT9CoreInput input, XT9CoreInput.XT9InputMode xt9InputMode) {
        int coreId;
        int coreId2;
        int firstLangID = getCoreLanguageId();
        int secondLangID = this.secondLang.getCoreLanguageId();
        if (DatabaseConfig.isUsingDeprecatedLanguageLDB(getContext(), this.firstLang.mEnglishName) && (coreId2 = DatabaseConfig.getDeprecatedLanguageLDBID(getContext(), this.firstLang.mEnglishName)) != -1) {
            firstLangID = coreId2;
            log.d("setLanguage...first language is deprecated...deprecated languge id.." + firstLangID);
        }
        if (DatabaseConfig.isUsingDeprecatedLanguageLDB(getContext(), this.secondLang.mEnglishName) && (coreId = DatabaseConfig.getDeprecatedLanguageLDBID(getContext(), this.secondLang.mEnglishName)) != -1) {
            secondLangID = coreId;
            log.d("setLanguage...second language is deprecated...deprecated languge id.." + coreId);
        }
        log.d("setLanguage...first core languge id.." + getCoreLanguageId() + "..second core languge id.." + this.secondLang.getCoreLanguageId());
        return input.setLanguage(firstLangID, secondLangID, xt9InputMode);
    }

    @Override // com.nuance.swype.input.InputMethods.Language
    public XT9Status setLanguage(XT9CoreInput input) {
        return setLanguage(input, input.getDefaultInputMode(getCoreLanguageId()));
    }

    @Override // com.nuance.swype.input.InputMethods.Language
    public boolean isCoreLanguage() {
        return false;
    }

    public static String getLanguageId(InputMethods.Language firstLang, InputMethods.Language secondLang) {
        if (firstLang instanceof BilingualLanguage) {
            firstLang = ((BilingualLanguage) firstLang).firstLang;
        }
        return firstLang.getLanguageId() + LANGUAGE_DELIMITER + secondLang.getLanguageId();
    }

    public static String[] getLanguageIds(String languageId) {
        return languageId.split(LANGUAGE_DELIMITER);
    }

    @Override // com.nuance.swype.input.InputMethods.Language
    public boolean usesLanguage(String languageName) {
        return this.firstLang.usesLanguage(languageName) || this.secondLang.usesLanguage(languageName);
    }
}
