package com.nuance.swype.input;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.SparseArray;
import com.facebook.internal.ServerProtocol;
import com.nuance.connect.common.Strings;
import com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.xmlResults.XMLResultsHandler;
import com.nuance.swype.input.DatabaseConfig;
import com.nuance.swype.util.LogManager;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

/* loaded from: classes.dex */
public class LanguageList {
    public static final String LANGUAGE_LIST_FILE = "languagelist.xml";
    private static final LogManager.Log log = LogManager.getLog(Strings.MESSAGE_BUNDLE_LANGUAGE_LIST);
    protected static final LogManager.Trace trace = LogManager.getTrace();
    private List<String> builtinLanguages;
    private final Context context;
    private String defaultLanguage;
    private boolean isFakeDefaultLanguage;
    private List<String> languages;
    private List<String> supportedLanguages;

    public LanguageList(Context context) {
        this(context, false);
    }

    public LanguageList(Context context, boolean isRefresh) {
        this.isFakeDefaultLanguage = false;
        this.context = context;
        AppPreferences appPrefs = AppPreferences.from(context);
        this.defaultLanguage = appPrefs.getString(AppPreferences.DEFAULT_LANGUAGE, null);
        this.builtinLanguages = appPrefs.getStrings(AppPreferences.BUILTIN_LANGUAGES, null);
        this.languages = appPrefs.getStrings(AppPreferences.AVAILABLE_LANGUAGES, null);
        this.supportedLanguages = appPrefs.getStrings(AppPreferences.SUPPORTED_LANGUAGES, null);
        if (this.languages == null || this.builtinLanguages == null || this.defaultLanguage == null || this.supportedLanguages == null || this.languages.isEmpty() || this.builtinLanguages.isEmpty() || this.defaultLanguage.isEmpty() || this.supportedLanguages.isEmpty()) {
            if (!updateLanguageList(appPrefs)) {
                DatabaseConfig.clearExternalDataBasePath();
                updateLanguageList(appPrefs);
                return;
            }
            return;
        }
        if (isRefresh) {
            List<String> languagesDownloaded = new ArrayList<>(this.languages);
            languagesDownloaded.removeAll(this.builtinLanguages);
            if (languagesDownloaded.size() > 0) {
                checkLanguageFiles(context, languagesDownloaded);
            }
            String oldDefaultLang = this.defaultLanguage;
            updateLanguageList(appPrefs);
            this.languages.addAll(languagesDownloaded);
            removeDuplicates(this.languages);
            int index = oldDefaultLang.indexOf("_Std");
            if (index != -1) {
                oldDefaultLang = oldDefaultLang.substring(0, index);
                this.isFakeDefaultLanguage = true;
                log.d("oldDefaultLang Name:" + oldDefaultLang);
            }
            if (this.languages.contains(oldDefaultLang)) {
                this.defaultLanguage = oldDefaultLang;
            }
            updateListToAppPreferences(appPrefs);
        }
    }

    private void removeDuplicates(List<String> list) {
        Set<String> copy = new LinkedHashSet<>(list);
        list.clear();
        list.addAll(copy);
    }

    public String getDefaultLanguage() {
        return this.defaultLanguage;
    }

    public boolean isFakeDefaultLanguage() {
        return this.isFakeDefaultLanguage;
    }

    public List<String> getLanguageList() {
        return this.languages;
    }

    public List<String> getBuiltinLanguageList() {
        return this.builtinLanguages;
    }

    public List<String> getSupportedLanguageList() {
        return this.supportedLanguages;
    }

    public boolean addDownloadedLanguage(String languageName) {
        removeDuplicates(this.languages);
        if (!this.languages.contains(languageName)) {
            this.languages.add(languageName);
            persist();
        } else if (this.languages.contains(languageName + "_Std")) {
            this.languages.remove(languageName + "_Std");
            persist();
            return true;
        }
        return false;
    }

    public boolean removeDownloadedLanguage(String languageName) {
        Map<String, List<DatabaseConfig.LanguageDB>> databasesSuppressed = DatabaseConfig.getDeprecatedLanguageDBList(this.context);
        boolean isSuppressedLanguage = false;
        Iterator<Map.Entry<String, List<DatabaseConfig.LanguageDB>>> it = databasesSuppressed.entrySet().iterator();
        while (it.hasNext()) {
            if (it.next().getKey().contentEquals(languageName)) {
                isSuppressedLanguage = true;
            }
        }
        log.d("removeDownloadedLanguage...languageName..." + languageName + "..isSuppressedLanguage.." + isSuppressedLanguage);
        if (isSuppressedLanguage) {
            return false;
        }
        removeDuplicates(this.languages);
        if (this.languages.remove(languageName)) {
            persist();
        }
        return true;
    }

    private void persist() {
        AppPreferences.from(this.context).setStrings(AppPreferences.AVAILABLE_LANGUAGES, this.languages);
    }

    public static String composeLanguageEnabledSPKey(String languageName) {
        return languageName + ".enabled";
    }

    private boolean updateLanguageList(AppPreferences appPrefs) {
        ArrayList<String> languagesDownloaded = new ArrayList<>();
        if (this.languages != null) {
            languagesDownloaded = new ArrayList<>(this.languages);
        }
        this.languages = new ArrayList();
        AssetManager am = this.context.getAssets();
        InputStream is = null;
        File externalLanguageListFile = null;
        try {
            try {
                boolean readAssetLanguageList = !DatabaseConfig.foundFileInExternalPath(LANGUAGE_LIST_FILE);
                if (!readAssetLanguageList) {
                    String[] externalDatabasePath = DatabaseConfig.getExternalDatabasePath(this.context);
                    int length = externalDatabasePath.length;
                    int i = 0;
                    while (true) {
                        if (i >= length) {
                            break;
                        }
                        String path = externalDatabasePath[i];
                        if (path.isEmpty() || !new File(path, LANGUAGE_LIST_FILE).exists()) {
                            i++;
                        } else {
                            externalLanguageListFile = new File(path, LANGUAGE_LIST_FILE);
                            break;
                        }
                    }
                }
                is = readAssetLanguageList ? am.open(LANGUAGE_LIST_FILE) : new BufferedInputStream(new FileInputStream(externalLanguageListFile));
                if (is != null) {
                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    factory.setValidating(false);
                    XmlPullParser xmlparser = factory.newPullParser();
                    xmlparser.setInput(new BufferedInputStream(is, 8192), null);
                    while (true) {
                        int event = xmlparser.next();
                        if (event == 1) {
                            break;
                        }
                        if (event == 2) {
                            String tag = xmlparser.getName();
                            if ("Language".equals(tag)) {
                                String langName = xmlparser.getAttributeValue(null, Strings.MESSAGE_BUNDLE_LANGUAGE);
                                this.languages.add(langName);
                                boolean isEnabled = xmlparser.getAttributeValue(null, "enabled").equals(ServerProtocol.DIALOG_RETURN_SCOPES_TRUE);
                                String langEnableKey = composeLanguageEnabledSPKey(langName);
                                if (!appPrefs.contains(langEnableKey)) {
                                    appPrefs.setBoolean(langEnableKey, isEnabled);
                                }
                            } else if ("InputMethods".equals(tag)) {
                                this.defaultLanguage = xmlparser.getAttributeValue(null, "defaultLanguage");
                            }
                        }
                    }
                }
                if (is != null) {
                    is.close();
                }
                ArrayList<String> buildConfiguredLanguages = new ArrayList<>();
                InputStream is2 = null;
                try {
                    is2 = am.open("languagelist_supported.xml");
                    XmlPullParserFactory factory2 = XmlPullParserFactory.newInstance();
                    factory2.setValidating(false);
                    XmlPullParser xmlparser2 = factory2.newPullParser();
                    xmlparser2.setInput(new BufferedInputStream(is2, 8192), null);
                    while (true) {
                        int event2 = xmlparser2.next();
                        if (event2 == 1) {
                            break;
                        }
                        if (event2 == 2 && "Language".equals(xmlparser2.getName())) {
                            buildConfiguredLanguages.add(xmlparser2.getAttributeValue(null, Strings.MESSAGE_BUNDLE_LANGUAGE));
                        }
                    }
                    if (is2 != null) {
                        is2.close();
                    }
                    this.builtinLanguages = new ArrayList(this.languages);
                    this.supportedLanguages = new ArrayList();
                    Iterator<Map.Entry<String, List<DatabaseConfig.LanguageDB>>> it = DatabaseConfig.getLanguageDBList(this.context, null).entrySet().iterator();
                    while (it.hasNext()) {
                        String languageToCheck = it.next().getKey();
                        log.d("language to see if is just supported:", languageToCheck);
                        if (buildConfiguredLanguages.contains(languageToCheck)) {
                            log.d("language is supported:", languageToCheck);
                            this.supportedLanguages.add(languageToCheck);
                        } else {
                            log.d("language was not configured in build: ", languageToCheck);
                        }
                    }
                    languagesDownloaded.removeAll(this.builtinLanguages);
                    if (languagesDownloaded.size() > 0) {
                        checkLanguageFiles(this.context, languagesDownloaded);
                    }
                    this.languages.addAll(languagesDownloaded);
                    removeDuplicates(this.languages);
                    updateListToAppPreferences(appPrefs);
                    return true;
                } catch (Throwable th) {
                    if (is2 != null) {
                        is2.close();
                    }
                    throw th;
                }
            } catch (IOException ex) {
                log.e(ex.getMessage(), ex);
                return false;
            } catch (XmlPullParserException ex2) {
                log.e(ex2.getMessage(), ex2);
                return false;
            }
        } catch (Throwable th2) {
            if (is != null) {
                is.close();
            }
            throw th2;
        }
    }

    private void updateListToAppPreferences(AppPreferences appPrefs) {
        appPrefs.setStrings(AppPreferences.AVAILABLE_LANGUAGES, this.languages);
        appPrefs.setStrings(AppPreferences.BUILTIN_LANGUAGES, this.builtinLanguages);
        appPrefs.setString(AppPreferences.DEFAULT_LANGUAGE, this.defaultLanguage);
        appPrefs.setStrings(AppPreferences.SUPPORTED_LANGUAGES, this.supportedLanguages);
    }

    private void checkLanguageFiles(Context context, List<String> downloadedLanguages) {
        int index;
        Set<String> privateFileNames = IMEApplication.from(context).getPrivateFiles();
        log.d("checkLanguageFiles");
        Map<String, List<DatabaseConfig.LanguageDB>> databasesNeeded = DatabaseConfig.getLanguageDBList(context, downloadedLanguages);
        SparseArray<String> oldLanguageIdFileTable = DatabaseConfig.getOldLanguageIdAndFileMappingTable(context);
        List<String> addedList = new ArrayList<>();
        Map<String, List<DatabaseConfig.LanguageDB>> deprecatedDatabases = DatabaseConfig.getDeprecatedLanguageDBList(context);
        for (Map.Entry<String, List<DatabaseConfig.LanguageDB>> entry : databasesNeeded.entrySet()) {
            log.d("language key..." + entry.getKey());
            boolean ldbFound = false;
            for (DatabaseConfig.LanguageDB db : entry.getValue()) {
                if (db.isLDB() && (privateFileNames.contains(db.getFileName()) || (oldLanguageIdFileTable.get(db.getId()) != null && privateFileNames.contains(oldLanguageIdFileTable.get(db.getId()))))) {
                    ldbFound = true;
                    break;
                }
            }
            if (!ldbFound) {
                downloadedLanguages.remove(entry.getKey());
                log.d("remove language from list..." + entry.getKey());
            }
        }
        for (String language : downloadedLanguages) {
            if (deprecatedDatabases.get(language) != null && (index = language.indexOf("_Std")) != -1) {
                String ldbName = language.substring(0, index);
                log.d("checkLanguageFiles", "deprecated LDB Name:" + ldbName);
                if (!downloadedLanguages.contains(ldbName)) {
                    addedList.add(ldbName);
                    log.d("add deprecated language to list..." + ldbName);
                }
            }
        }
        if (!addedList.isEmpty()) {
            downloadedLanguages.addAll(addedList);
        }
    }

    public boolean isHdbShared(String langName) {
        Map<String, List<DatabaseConfig.LanguageDB>> databases = DatabaseConfig.getLanguageDBList(this.context, null);
        String hdbName = getHdbName(langName);
        for (Map.Entry<String, List<DatabaseConfig.LanguageDB>> entry : databases.entrySet()) {
            if (entry.getKey().compareTo(langName) != 0) {
                for (DatabaseConfig.LanguageDB db : entry.getValue()) {
                    if (db.isHDB() && db.getFileName().compareTo(hdbName) == 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public String getHdbName(String langName) {
        Map<String, List<DatabaseConfig.LanguageDB>> databases = DatabaseConfig.getLanguageDBList(this.context, null);
        for (Map.Entry<String, List<DatabaseConfig.LanguageDB>> entry : databases.entrySet()) {
            if (entry.getKey().compareTo(langName) == 0) {
                for (DatabaseConfig.LanguageDB db : entry.getValue()) {
                    if (db.isHDB()) {
                        String hdbName = db.getFileName();
                        return hdbName;
                    }
                }
                return "";
            }
        }
        return "";
    }

    public String getLanguageFlavor(String langName) {
        return DatabaseConfig.getLanguageFlavor(this.context, langName);
    }

    public Map<String, String> getInstalledDeprecatedLanguages() {
        log.d("getInstalledDeprecatedLanguages()");
        Map<String, String> deprecates = new HashMap<>();
        for (String language : this.languages) {
            if (isDeprecatedLang(language)) {
                log.d("processing deprecated language: ", language);
                String nonDeprecatedLanguage = getNonDeprecatedLanguageName(language);
                if (nonDeprecatedLanguage != null) {
                    String deprecatedLdb = DatabaseConfig.getDeprecatedLanguageLDBName(this.context, nonDeprecatedLanguage);
                    if (!this.builtinLanguages.contains(nonDeprecatedLanguage)) {
                        deprecates.put(language, deprecatedLdb);
                        log.d("adding ", language, XMLResultsHandler.SEP_SPACE, deprecatedLdb);
                    }
                }
            }
        }
        return deprecates;
    }

    public String getNonDeprecatedLanguageName(String deprecatedLanguage) {
        String language = isDeprecatedLang(deprecatedLanguage) ? deprecatedLanguage.substring(0, deprecatedLanguage.indexOf("_Std")) : null;
        if (language != null && !this.languages.contains(language)) {
            return null;
        }
        return language;
    }

    public boolean isDeprecatedLang(String language) {
        return language.indexOf("_Std") != -1;
    }
}
