package com.nuance.swype.input;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.util.SparseArray;
import com.nuance.android.compat.UserManagerCompat;
import com.nuance.connect.common.Strings;
import com.nuance.input.swypecorelib.XT9CoreAlphaInput;
import com.nuance.swype.util.LogManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParserException;

/* loaded from: classes.dex */
public class OemLdbWordsManager {
    public static final String SETTING_OME_CUSTOMIZED_LANGUAGE = "oem_customized_language";
    private static final String SETTING_OME_CUSTOMIZED_LANGUAGE_DELIMETER = ",";
    protected static final LogManager.Log log = LogManager.getLog("OemLdbWordsManager");
    private static OemLdbWordsManager mInstance;
    private Context mContext;
    private String[] mCustomizedLanguageCache;
    protected List<String> mCustomizedLanguages = new ArrayList();
    protected SparseArray<OemLdbWords> mLlangWordsMap;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class OemLdbWords {
        private static final String WORD_DELIMITER = ",";
        public List<String> mNewWords = new ArrayList();
        public List<String> mRemovedWords = new ArrayList();

        OemLdbWords() {
        }

        public void setNewWords(String newWords) {
            this.mNewWords.clear();
            String[] splitedWords = newWords.split(",");
            for (String str : splitedWords) {
                String word = str.trim();
                if (!word.isEmpty() && !this.mNewWords.contains(word)) {
                    this.mNewWords.add(word);
                }
            }
        }

        public void setRemovedWords(String removedWords) {
            this.mRemovedWords.clear();
            String[] splitedWords = removedWords.split(",");
            for (String str : splitedWords) {
                String word = str.trim();
                if (!word.isEmpty() && !this.mRemovedWords.contains(word)) {
                    this.mRemovedWords.add(word);
                }
            }
        }
    }

    private OemLdbWordsManager(Context context) {
        readCustimzedWords(context);
        this.mContext = context;
    }

    public static OemLdbWordsManager from(Context context) {
        if (mInstance == null) {
            mInstance = new OemLdbWordsManager(context);
        }
        return mInstance;
    }

    protected void readCustimzedWords(Context context) {
        OemLdbWords customizedWord;
        OemLdbWords customizedWord2;
        XmlResourceParser xmlparser = context.getResources().getXml(R.xml.oem_customized_words);
        if (this.mLlangWordsMap != null) {
            this.mLlangWordsMap.clear();
        }
        this.mLlangWordsMap = new SparseArray<>();
        int currentLangId = -1;
        while (true) {
            try {
                try {
                    int event = xmlparser.next();
                    if (event == 1) {
                        break;
                    }
                    if (event == 2) {
                        String tag = xmlparser.getName();
                        if (Strings.MESSAGE_BUNDLE_LANGUAGE.equalsIgnoreCase(tag)) {
                            try {
                                currentLangId = Integer.decode(xmlparser.getAttributeValue(null, "id")).intValue();
                                this.mLlangWordsMap.put(currentLangId, new OemLdbWords());
                            } catch (NumberFormatException e) {
                                currentLangId = -1;
                            }
                        } else if ("newwords".equalsIgnoreCase(tag)) {
                            String newWords = xmlparser.nextText();
                            if (currentLangId != -1 && (customizedWord = this.mLlangWordsMap.get(currentLangId)) != null) {
                                customizedWord.setNewWords(newWords);
                            }
                        } else if ("removedwords".equalsIgnoreCase(tag)) {
                            String removedWords = xmlparser.nextText();
                            if (currentLangId != -1 && (customizedWord2 = this.mLlangWordsMap.get(currentLangId)) != null) {
                                customizedWord2.setRemovedWords(removedWords);
                            }
                        }
                    } else if (event == 3 && Strings.MESSAGE_BUNDLE_LANGUAGE.equals(xmlparser.getName())) {
                        currentLangId = -1;
                    }
                } catch (IOException ex) {
                    log.e("readCustimzedWords...IOException: ", ex);
                    if (xmlparser != null) {
                        xmlparser.close();
                        return;
                    }
                    return;
                } catch (XmlPullParserException ex2) {
                    log.e("readCustimzedWords...XmlPullParserException: ", ex2);
                    if (xmlparser != null) {
                        xmlparser.close();
                        return;
                    }
                    return;
                }
            } catch (Throwable th) {
                if (xmlparser != null) {
                    xmlparser.close();
                }
                throw th;
            }
        }
        if (xmlparser != null) {
            xmlparser.close();
        }
    }

    public void AddOemLdbWordsForAlpha(XT9CoreAlphaInput alphaInput, int languageId) {
        OemLdbWords wordPairs;
        if (UserManagerCompat.isUserUnlocked(this.mContext) && !isLanguageCustomized(languageId) && (wordPairs = this.mLlangWordsMap.get(languageId)) != null) {
            if (!wordPairs.mNewWords.isEmpty()) {
                int size = wordPairs.mNewWords.size();
                for (int i = 0; i < size; i++) {
                    String word = wordPairs.mNewWords.get(i).trim();
                    if (word.isEmpty()) {
                        log.d("empty string found for add new words, position = " + i);
                    } else {
                        boolean ret = alphaInput.dlmAddNewWordForLanguage(word, languageId);
                        if (!ret) {
                            log.e("dlmAddNewWordForLanguage failed");
                            return;
                        }
                        log.d(word + " added " + ret);
                    }
                }
            }
            if (!wordPairs.mRemovedWords.isEmpty()) {
                int size2 = wordPairs.mRemovedWords.size();
                for (int i2 = 0; i2 < size2; i2++) {
                    String word2 = wordPairs.mRemovedWords.get(i2).trim();
                    if (word2.isEmpty()) {
                        log.d("empty string found for removed words, position = " + i2);
                    } else {
                        boolean ret2 = alphaInput.dlmAddBlackWordForLanguage(word2, languageId);
                        if (!ret2) {
                            log.e("dlmAddBlackWordForLanguage failed");
                            return;
                        }
                        log.d(word2 + " removed " + ret2, "  languageId:", Integer.valueOf(languageId));
                    }
                }
            }
            this.mCustomizedLanguages.add(Integer.toString(languageId));
            savemCustomizedLanguages();
        }
    }

    private String[] getCustomizedLanguages(Context context, String defaultLangId) {
        if (this.mCustomizedLanguageCache == null) {
            String languageSetting = AppPreferences.from(context).getUpgradedString(SETTING_OME_CUSTOMIZED_LANGUAGE, defaultLangId, "%x");
            log.d("getRecentLanguages...languageSetting..." + languageSetting);
            this.mCustomizedLanguageCache = languageSetting.split(",");
        }
        return this.mCustomizedLanguageCache;
    }

    private void setCustomizedLanguages(Context context, List<String> languageIds) {
        StringBuilder newmCustomizedLanguages = new StringBuilder();
        for (String langId : languageIds) {
            newmCustomizedLanguages.append(langId);
            newmCustomizedLanguages.append(",");
        }
        AppPreferences.from(context).setString(SETTING_OME_CUSTOMIZED_LANGUAGE, newmCustomizedLanguages.toString());
        int size = languageIds.size();
        if (this.mCustomizedLanguageCache == null || this.mCustomizedLanguageCache.length > size) {
            this.mCustomizedLanguageCache = new String[size];
        }
        this.mCustomizedLanguageCache = (String[]) languageIds.toArray(this.mCustomizedLanguageCache);
    }

    public boolean isLanguageCustomized(int languageId) {
        if (this.mCustomizedLanguages.isEmpty()) {
            for (String language : getCustomizedLanguages(this.mContext, "")) {
                if (!language.isEmpty()) {
                    this.mCustomizedLanguages.add(language);
                }
            }
        }
        return this.mCustomizedLanguages.contains(Integer.toString(languageId));
    }

    public void savemCustomizedLanguages() {
        setCustomizedLanguages(this.mContext, this.mCustomizedLanguages);
    }
}
