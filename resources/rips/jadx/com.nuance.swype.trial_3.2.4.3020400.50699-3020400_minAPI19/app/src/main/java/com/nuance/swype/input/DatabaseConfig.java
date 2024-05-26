package com.nuance.swype.input;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.util.SparseArray;
import com.nuance.android.compat.UserManagerCompat;
import com.nuance.connect.common.Strings;
import com.nuance.input.swypecorelib.SwypeCoreLibrary;
import com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.xmlResults.XMLResultsHandler;
import com.nuance.swype.connect.Connect;
import com.nuance.swype.input.hardkey.HardKeyboardManager;
import com.nuance.swype.util.LogManager;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.xmlpull.v1.XmlPullParserException;

/* loaded from: classes.dex */
public final class DatabaseConfig {
    private static final String DATABASE_CONFIG_FILE_NAME = "databases.conf";
    private static final String EXTRACT_DATABASE = "_data_extract.txt";
    private static EnumMap<DbType, SparseArray<LanguageDB>> configMap;
    private static Map<String, List<LanguageDB>> deprecatedLanguageDbList;
    private static Map<String, List<LanguageDB>> languageDbList;
    private static Map<String, String> languageFlavorList;
    private static final LogManager.Log log = LogManager.getLog("DatabaseConfig");
    protected static final LogManager.Trace trace = LogManager.getTrace();
    public static String[] EXTERNAL_LDB_DIRECTORY_ARRAY = {"/data/hw_init/system/lang/swype/", "/system/lang/swype/"};
    private static SparseArray<String> oldLangugeIdAndLdbFileMappingTable = null;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public enum DbType {
        HDB("hwr_db_template"),
        LDB("ldb"),
        CDB("cdb");

        final String section;

        DbType(String tag) {
            this.section = tag;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public enum DbInfo {
        LDB("ldb", DbType.LDB, true),
        ALM_LDB("almldb", DbType.LDB, true),
        TRACE_LDB1("traceldb1", DbType.LDB, true),
        TRACE_LDB2("traceldb2", DbType.LDB, true),
        HDB("hdb", DbType.HDB, true),
        CDB("cdb", DbType.CDB, true);

        private static final Map<String, DbInfo> tagToEnum = new HashMap();
        final boolean compressed;
        final String tag;
        final DbType type;

        static {
            for (DbInfo info : values()) {
                tagToEnum.put(info.tag, info);
            }
        }

        DbInfo(String tag, DbType type, boolean compressed) {
            this.tag = tag;
            this.type = type;
            this.compressed = compressed;
        }

        public static DbInfo fromTag(String tag) {
            return tagToEnum.get(tag);
        }
    }

    private DatabaseConfig() {
    }

    public static void refreshDatabaseConfig(Context context, long buildDate) {
        if (!AppPreferences.from(context.getApplicationContext()).isLoadExternalLDBEnabled()) {
            clearExternalDataBasePath();
        }
        if (checkIfDatabaseNeedsRefresh(context, buildDate)) {
            extractLanguageIdAndFileFromDatabaseConfig(context);
            try {
                build(context, true, null);
                writeBuildDate(context, buildDate);
            } catch (IOException ex) {
                log.e(ex.getMessage(), ex);
            }
        }
    }

    public static SparseArray<String> getOldLanguageIdAndFileMappingTable(Context context) {
        if (oldLangugeIdAndLdbFileMappingTable == null || oldLangugeIdAndLdbFileMappingTable.size() == 0) {
            extractLanguageIdAndFileFromDatabaseConfig(context);
        }
        return oldLangugeIdAndLdbFileMappingTable;
    }

    private static void extractLanguageIdAndFileFromDatabaseConfig(Context context) {
        oldLangugeIdAndLdbFileMappingTable = new SparseArray<>();
        BufferedReader reader = null;
        try {
            try {
                String file = context.getFileStreamPath(DATABASE_CONFIG_FILE_NAME).getPath();
                BufferedReader reader2 = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
                boolean inLdbSection = false;
                while (true) {
                    try {
                        String line = reader2.readLine();
                        if (line != null) {
                            String line2 = line.trim();
                            if (line2.length() != 0 && line2.charAt(0) != '#') {
                                if ("[ldb]".equals(line2)) {
                                    inLdbSection = true;
                                } else if ("[[hwr_db_template]]".equals(line2) || "[cdb]".equals(line2)) {
                                    inLdbSection = false;
                                } else if (inLdbSection) {
                                    String[] parts = line2.split(XMLResultsHandler.SEP_SPACE);
                                    if (parts.length >= 2) {
                                        int languageId = Integer.decode(parts[0].trim()).intValue();
                                        String languageFile = parts[1].trim();
                                        log.i("languageId: " + languageId + " languageFile: " + languageFile);
                                        oldLangugeIdAndLdbFileMappingTable.append(languageId, languageFile);
                                    }
                                }
                            }
                        } else {
                            try {
                                break;
                            } catch (IOException e) {
                                log.e(e);
                            }
                        }
                    } catch (IOException e2) {
                        ex = e2;
                        reader = reader2;
                        log.e(ex);
                        if (reader != null) {
                            try {
                                reader.close();
                            } catch (IOException e3) {
                                log.e(e3);
                            }
                        }
                        log.i("oldLangugeIdAndLdbFileMappingTable: " + oldLangugeIdAndLdbFileMappingTable);
                    } catch (Throwable th) {
                        th = th;
                        reader = reader2;
                        if (reader != null) {
                            try {
                                reader.close();
                            } catch (IOException e4) {
                                log.e(e4);
                            }
                        }
                        throw th;
                    }
                }
                reader2.close();
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (IOException e5) {
            ex = e5;
        }
        log.i("oldLangugeIdAndLdbFileMappingTable: " + oldLangugeIdAndLdbFileMappingTable);
    }

    public static synchronized void updateLanguage(Context context, String languageName) {
        synchronized (DatabaseConfig.class) {
            try {
                build(context, false, languageName);
                IMEApplication.from(context).getSwypeCoreLibMgr().getSwypeCoreLibInstance().refreshDatabaseConfigFile(getDatabaseConfigFile(context));
            } catch (IOException ex) {
                log.e(ex.getMessage(), ex);
            }
        }
    }

    public static synchronized void refreshDatabaseConfigInJNI(Context context) {
        synchronized (DatabaseConfig.class) {
            log.d("refreshDatabaseConfigInJNI...");
            IMEApplication.from(context).getSwypeCoreLibMgr().getSwypeCoreLibInstance().refreshDatabaseConfigFile(getDatabaseConfigFile(context));
        }
    }

    private static boolean checkIfDatabaseNeedsRefresh(Context context, long buildDate) {
        if (!UserManagerCompat.isUserUnlocked(context)) {
            return false;
        }
        try {
            String oldBuildDate = readBuildDate(context);
            return !String.valueOf(buildDate).equals(oldBuildDate);
        } catch (FileNotFoundException e) {
            return true;
        } catch (IOException ex) {
            log.e(ex.getMessage(), ex);
            return true;
        }
    }

    protected static String readBuildDate(Context context) throws IOException {
        Reader reader;
        InputStream in = context.openFileInput(EXTRACT_DATABASE);
        Reader reader2 = null;
        try {
            reader = new InputStreamReader(in, "UTF-8");
        } catch (IOException e) {
        } catch (Throwable th) {
            th = th;
        }
        try {
            String readBuildDate = readBuildDate(reader);
            try {
                reader.close();
                return readBuildDate;
            } catch (IOException e2) {
                return null;
            }
        } catch (IOException e3) {
            reader2 = reader;
            if (reader2 != null) {
                try {
                    reader2.close();
                } catch (IOException e4) {
                    return null;
                }
            }
            return null;
        } catch (Throwable th2) {
            th = th2;
            reader2 = reader;
            if (reader2 != null) {
                try {
                    reader2.close();
                } catch (IOException e5) {
                    return null;
                }
            }
            throw th;
        }
    }

    protected static String readBuildDate(Reader reader) throws IOException {
        StringBuilder sb = new StringBuilder();
        while (true) {
            int c = reader.read();
            if (c != -1) {
                sb.append((char) c);
            } else {
                return sb.toString();
            }
        }
    }

    protected static void writeBuildDate(Context context, long buildDate) throws IOException {
        OutputStream out = context.openFileOutput(EXTRACT_DATABASE, 0);
        Writer writer = new OutputStreamWriter(out, "UTF-8");
        try {
            writeBuildDate(writer, buildDate);
        } finally {
            writer.close();
        }
    }

    protected static void writeBuildDate(Writer writer, long buildDate) throws IOException {
        writer.write(String.valueOf(buildDate));
    }

    public static String getDatabaseConfigFile(Context context) {
        return context.getFileStreamPath(DATABASE_CONFIG_FILE_NAME).getPath();
    }

    public static String[] getExternalDatabasePath(Context context) {
        return foundFileInExternalPath(LanguageList.LANGUAGE_LIST_FILE) ? EXTERNAL_LDB_DIRECTORY_ARRAY : new String[0];
    }

    public static void clearExternalDataBasePath() {
        Arrays.fill(EXTERNAL_LDB_DIRECTORY_ARRAY, "");
    }

    public static boolean foundFileInExternalPath(String fileName) {
        for (String path : EXTERNAL_LDB_DIRECTORY_ARRAY) {
            if (!path.isEmpty() && fileName != null && !fileName.isEmpty() && new File(path, fileName).exists()) {
                return true;
            }
        }
        return false;
    }

    public static Map<String, List<LanguageDB>> getDeprecatedLanguageDBList(Context context) {
        if (deprecatedLanguageDbList == null) {
            deprecatedLanguageDbList = parseLanguageFiles(context, R.xml.langfiles_deprecated);
        }
        return deprecatedLanguageDbList;
    }

    public static Map<String, List<LanguageDB>> getLanguageDBList(Context context, List<String> targetLanguages) {
        if (languageDbList == null) {
            languageDbList = parseLanguageFiles(context, R.xml.langfiles);
        }
        return targetLanguages != null ? filterLanguageDbList(languageDbList, targetLanguages) : languageDbList;
    }

    public static String getLanguageFlavor(Context context, String langName) {
        if (languageFlavorList == null) {
            languageFlavorList = parseLanguageFlavors(context, R.xml.langflavors);
        }
        if (languageFlavorList != null) {
            return languageFlavorList.get(langName);
        }
        return null;
    }

    protected static Map<String, List<LanguageDB>> filterLanguageDbList(Map<String, List<LanguageDB>> langToDbs, List<String> languages) {
        Map<String, List<LanguageDB>> filtered = new HashMap<>(languages.size());
        for (String language : languages) {
            List<LanguageDB> list = langToDbs.get(language);
            if (list != null) {
                filtered.put(language, list);
            }
        }
        return filtered;
    }

    protected static Map<String, List<LanguageDB>> parseLanguageFiles(Context context, int resId) {
        XmlResourceParser xmlparser = context.getResources().getXml(resId);
        Map<String, List<LanguageDB>> langToDbs = new HashMap<>();
        String currentLangName = null;
        while (true) {
            try {
                int event = xmlparser.next();
                if (event == 1) {
                    break;
                }
                if (event == 2) {
                    String tag = xmlparser.getName();
                    DbInfo dbInfo = null;
                    if (Strings.MESSAGE_BUNDLE_LANGUAGE.equals(tag)) {
                        currentLangName = xmlparser.getAttributeValue(null, "name");
                        langToDbs.put(currentLangName, new ArrayList<>());
                    } else {
                        dbInfo = DbInfo.fromTag(tag);
                    }
                    if (dbInfo != null && currentLangName != null) {
                        String filename = xmlparser.getAttributeValue(null, "file");
                        int id = Integer.decode(xmlparser.getAttributeValue(null, "id")).intValue();
                        langToDbs.get(currentLangName).add(new LanguageDB(dbInfo, filename, id, null, null));
                    }
                } else if (event == 3 && Strings.MESSAGE_BUNDLE_LANGUAGE.equals(xmlparser.getName())) {
                    currentLangName = null;
                }
            } catch (IOException e) {
                if (xmlparser != null) {
                    xmlparser.close();
                }
            } catch (XmlPullParserException e2) {
                if (xmlparser != null) {
                    xmlparser.close();
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
        return langToDbs;
    }

    protected static Map<String, String> parseLanguageFlavors(Context context, int resId) {
        XmlResourceParser xmlparser = context.getResources().getXml(resId);
        Map<String, String> langFlavors = new HashMap<>();
        while (true) {
            try {
                int event = xmlparser.next();
                if (event == 1) {
                    break;
                }
                if (event == 2) {
                    String tag = xmlparser.getName();
                    if (Strings.MESSAGE_BUNDLE_LANGUAGE.equals(tag)) {
                        String currentLangName = xmlparser.getAttributeValue(null, "name");
                        String flavor = xmlparser.getAttributeValue(null, "flavor");
                        langFlavors.put(currentLangName, flavor);
                    }
                }
            } catch (IOException e) {
                if (xmlparser != null) {
                    xmlparser.close();
                }
            } catch (XmlPullParserException e2) {
                if (xmlparser != null) {
                    xmlparser.close();
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
        return langFlavors;
    }

    protected static Map<String, List<LanguageDB>> refreshCategoryDbs(Context context, Map<String, List<LanguageDB>> langToDbs) {
        CategoryDBList cdb = new CategoryDBList(context, false);
        Map<String, List<String>> cdbs = cdb.getAvailableCatDbList();
        for (Map.Entry<String, List<LanguageDB>> entry : langToDbs.entrySet()) {
            String languageName = entry.getKey();
            for (Map.Entry<String, List<String>> cdbEntry : cdbs.entrySet()) {
                String lang = cdbEntry.getKey();
                List<String> lst = cdbEntry.getValue();
                if (languageName.equals(lang)) {
                    for (String st : lst) {
                        String filename = cdb.getFileName(st);
                        int id = cdb.getFileId(st);
                        if (filename != null && id != 0) {
                            List<LanguageDB> dbs = entry.getValue();
                            boolean isFound = false;
                            Iterator<LanguageDB> it = dbs.iterator();
                            while (true) {
                                if (!it.hasNext()) {
                                    break;
                                }
                                LanguageDB db = it.next();
                                if (db.isCatDB() && db.getId() == id) {
                                    isFound = true;
                                    break;
                                }
                            }
                            if (!isFound) {
                                log.d("addCategoryDbFiles... add cdb filename: ", filename, " id: ", Integer.valueOf(id));
                                langToDbs.get(languageName).add(new LanguageDB(DbInfo.fromTag("cdb"), filename, id, null, null));
                            }
                        }
                    }
                }
            }
        }
        return langToDbs;
    }

    public static void build(Context context, boolean refreshAllDatabase, String languageToUpdate) throws IOException {
        log.d("build...refreshAllDatabase.." + refreshAllDatabase);
        if (configMap == null) {
            configMap = new EnumMap<>(DbType.class);
        }
        configMap.clear();
        for (DbType type : DbType.values()) {
            configMap.put((EnumMap<DbType, SparseArray<LanguageDB>>) type, (DbType) new SparseArray<>());
        }
        LanguageList languageList = new LanguageList(context, refreshAllDatabase);
        List<String> languages = languageList.getLanguageList();
        Map<String, List<LanguageDB>> langToDbMap = getLanguageDBList(context, null);
        Map<String, List<LanguageDB>> deprecatedLangToDbMap = getDeprecatedLanguageDBList(context);
        refreshCategoryDbs(context, langToDbMap);
        DbFileDetector fileDetector = new DbFileDetector(context, foundFileInExternalPath(LanguageList.LANGUAGE_LIST_FILE));
        List<String> builtinLanguages = languageList.getBuiltinLanguageList();
        for (String lang : languages) {
            List<LanguageDB> dbsForLang = langToDbMap.get(lang);
            if (dbsForLang == null) {
                log.e(String.format("Language '%s' undefined.", lang));
            } else {
                boolean isUpdatingLanguage = lang.equals(languageToUpdate);
                boolean isLDBAdded = false;
                for (LanguageDB db : dbsForLang) {
                    if (!isUpdatingLanguage && db.isLDB()) {
                        boolean isBuiltIn = builtinLanguages.contains(lang);
                        if (!fileDetector.fileExists(db.getFileName())) {
                            if (isBuiltIn) {
                                if (!fileDetector.dbFileIsInApk(db) && !fileDetector.dbFileIsInExternalPath(db.getFileName())) {
                                }
                            }
                        }
                    }
                    SparseArray<LanguageDB> arrayOfDbs = configMap.get(db.getType());
                    LanguageDB found = arrayOfDbs.get(db.getId());
                    if (found == null || !found.isALMLDB() || !db.isRegularLDB()) {
                        arrayOfDbs.put(db.getId(), db);
                        if (db.getType() == DbType.LDB) {
                            isLDBAdded = true;
                        }
                    }
                }
                if (!isLDBAdded) {
                    List<LanguageDB> dbsForDeprecatedLang = deprecatedLangToDbMap.get(lang + "_Std");
                    boolean oldLDBExists = false;
                    for (LanguageDB db2 : dbsForLang) {
                        SparseArray<LanguageDB> arrayOfDbs2 = configMap.get(db2.getType());
                        if (!isUpdatingLanguage && db2.isLDB() && arrayOfDbs2.get(db2.getId()) == null && oldLangugeIdAndLdbFileMappingTable != null && oldLangugeIdAndLdbFileMappingTable.get(db2.getId()) != null) {
                            oldLDBExists = true;
                        }
                    }
                    if (dbsForDeprecatedLang != null && !oldLDBExists) {
                        for (LanguageDB db3 : dbsForLang) {
                            String nonUpgradedDbFileName = db3.getFileName();
                            LanguageDB deprecatedDB = null;
                            if (!isUpdatingLanguage && db3.isLDB()) {
                                boolean isBuiltIn2 = builtinLanguages.contains(lang);
                                Iterator<LanguageDB> it = dbsForDeprecatedLang.iterator();
                                while (true) {
                                    if (!it.hasNext()) {
                                        break;
                                    }
                                    LanguageDB sdb = it.next();
                                    if (!db3.isRegularLDB() || !sdb.isRegularLDB()) {
                                        if (db3.isALMLDB() && sdb.isALMLDB()) {
                                            nonUpgradedDbFileName = sdb.dbFileName;
                                            deprecatedDB = sdb;
                                            break;
                                        }
                                    } else {
                                        nonUpgradedDbFileName = sdb.dbFileName;
                                        deprecatedDB = sdb;
                                        break;
                                    }
                                }
                                if (!fileDetector.fileExists(nonUpgradedDbFileName)) {
                                    if (isBuiltIn2 && fileDetector.dbFileIsInApk(db3)) {
                                    }
                                }
                            }
                            SparseArray<LanguageDB> arrayOfDbs3 = configMap.get(db3.getType());
                            LanguageDB found2 = arrayOfDbs3.get(db3.getId());
                            if (found2 == null || !found2.isALMLDB() || !db3.isRegularLDB()) {
                                log.d("add alias db..." + nonUpgradedDbFileName);
                                LanguageDB aliasDb = new LanguageDB(db3.info, nonUpgradedDbFileName, db3.getId(), db3.displayName, db3.displaySummary);
                                arrayOfDbs3.put(db3.getId(), aliasDb);
                                if (deprecatedDB != null) {
                                    arrayOfDbs3.put(deprecatedDB.getId(), deprecatedDB);
                                    deprecatedDB.isLDB();
                                }
                            }
                        }
                    } else {
                        for (LanguageDB db4 : dbsForLang) {
                            SparseArray<LanguageDB> arrayOfDbs4 = configMap.get(db4.getType());
                            if (!isUpdatingLanguage && db4.isLDB() && arrayOfDbs4.get(db4.getId()) == null && oldLangugeIdAndLdbFileMappingTable != null && oldLangugeIdAndLdbFileMappingTable.get(db4.getId()) != null) {
                                int languageId = db4.getId();
                                arrayOfDbs4.put(languageId, new LanguageDB(db4.info, oldLangugeIdAndLdbFileMappingTable.get(languageId), languageId, db4.displayName, db4.displaySummary));
                            }
                        }
                    }
                }
            }
        }
        log.d("begin to delete databases.conf");
        context.deleteFile(DATABASE_CONFIG_FILE_NAME);
        OutputStream out = context.openFileOutput(DATABASE_CONFIG_FILE_NAME, 0);
        Writer writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"), HardKeyboardManager.META_CTRL_ON);
        try {
            log.d("write database.conf start.");
            for (Map.Entry<DbType, SparseArray<LanguageDB>> entry : configMap.entrySet()) {
                log.d("write section: ", entry.getKey().section);
                log.d("write value: ", entry.getValue());
                writeSection(entry.getKey().section, entry.getValue(), writer, context);
            }
            writer.write(10);
            log.d("write database.conf done.");
        } finally {
            writer.close();
        }
    }

    public static boolean isUsingDeprecatedLanguageLDB(Context context, String language) {
        LanguageDB found;
        if (getDeprecatedLanguageDBList(context).get(language + "_Std") == null) {
            return false;
        }
        log.d("isUsingDeprecatedLanguageLDB...enter..");
        boolean returnVal = false;
        List<String> targetLanguage = new ArrayList<>();
        targetLanguage.add(language);
        Map<String, List<LanguageDB>> langToDbMap = getLanguageDBList(context, targetLanguage);
        if (configMap == null) {
            try {
                build(context, true, null);
            } catch (IOException ex) {
                log.e(ex.getMessage(), ex);
            }
        }
        List<LanguageDB> dbsForLang = langToDbMap.get(language);
        if (dbsForLang != null) {
            Iterator<LanguageDB> it = dbsForLang.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                LanguageDB db = it.next();
                if (db.getType() == DbType.LDB && (found = configMap.get(DbType.LDB).get(db.getId())) != null) {
                    log.d("isUsingDeprecatedLanguageLDB...db name in configMap.." + found.dbFileName);
                    log.d("isUsingDeprecatedLanguageLDB...db name in Language DB list.." + db.dbFileName);
                    if ((found.isALMLDB() && db.isALMLDB()) || (found.isRegularLDB() && db.isRegularLDB())) {
                        if (!db.dbFileName.contentEquals(found.dbFileName) && found.dbFileName.contains("Std_")) {
                            returnVal = true;
                            log.d("isUsingDeprecatedLanguageLDB...true");
                            break;
                        }
                    }
                }
            }
        }
        log.d("isUsingDeprecatedLanguageLDB...out..");
        return returnVal;
    }

    public static boolean isPossibleDeprecatedLanguage(Context context, String language) {
        return getDeprecatedLanguageDBList(context).get(new StringBuilder().append(language).append("_Std").toString()) != null;
    }

    public static int getDeprecatedLanguageLDBID(Context context, String language) {
        List<LanguageDB> langDBList = getDeprecatedLanguageDBList(context).get(language + "_Std");
        if (langDBList == null) {
            return -1;
        }
        for (LanguageDB db : langDBList) {
            if (db.isLDB()) {
                int LdbId = db.getId();
                return LdbId;
            }
        }
        return -1;
    }

    public static String getDeprecatedLanguageLDBName(Context context, String language) {
        List<LanguageDB> langDBList = getDeprecatedLanguageDBList(context).get(language + "_Std");
        if (langDBList == null) {
            return "";
        }
        LanguageDB bestMatch = null;
        for (LanguageDB db : langDBList) {
            if (bestMatch == null && db.isLDB()) {
                bestMatch = db;
            } else if (bestMatch == null || (db.isALMLDB() && bestMatch.isLDB())) {
                return db.getFileName();
            }
        }
        return bestMatch != null ? bestMatch.getFileName() : "";
    }

    public static String mockDeprecatedLanguageID(Context context, String deprecatedLangId) {
        boolean isDeprecatedLanguage = false;
        Map<String, List<LanguageDB>> databasesDeprecated = getDeprecatedLanguageDBList(context);
        String deprecatedLangName = null;
        for (Map.Entry<String, List<LanguageDB>> entry : databasesDeprecated.entrySet()) {
            Iterator<LanguageDB> it = entry.getValue().iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                LanguageDB db = it.next();
                if (db.isLDB() && deprecatedLangId.contentEquals(Integer.toHexString(db.getId()))) {
                    isDeprecatedLanguage = true;
                    String deprecatedLangName2 = entry.getKey();
                    deprecatedLangName = deprecatedLangName2;
                    break;
                }
            }
            if (isDeprecatedLanguage) {
                break;
            }
        }
        if (isDeprecatedLanguage) {
            int index = deprecatedLangName.indexOf("_Std");
            String supportedLanguageName = "";
            if (index != -1) {
                supportedLanguageName = deprecatedLangName.substring(0, index);
                log.d("supportedLanguageName: " + supportedLanguageName);
            }
            if (!supportedLanguageName.equals("")) {
                String supportedLanguageId = deprecatedLangId;
                Iterator<Map.Entry<String, List<LanguageDB>>> it2 = getLanguageDBList(context, null).entrySet().iterator();
                while (true) {
                    if (!it2.hasNext()) {
                        break;
                    }
                    Map.Entry<String, List<LanguageDB>> entry2 = it2.next();
                    if (entry2.getKey().contentEquals(supportedLanguageName)) {
                        Iterator<LanguageDB> it3 = entry2.getValue().iterator();
                        while (true) {
                            if (!it3.hasNext()) {
                                break;
                            }
                            LanguageDB db2 = it3.next();
                            if (db2.isLDB()) {
                                supportedLanguageId = Integer.toHexString(db2.getId());
                                break;
                            }
                        }
                    }
                }
                return supportedLanguageId;
            }
            return deprecatedLangId;
        }
        return deprecatedLangId;
    }

    private static void writeSection(String tag, SparseArray<LanguageDB> arrayOfDbs, Writer writer, Context context) throws IOException {
        writer.write(10);
        writer.write(91);
        writer.write(tag);
        writer.write(93);
        writer.write(10);
        int size = arrayOfDbs.size();
        for (int index = 0; index < size; index++) {
            LanguageDB db = arrayOfDbs.valueAt(index);
            writer.write(Integer.toString(db.getId()));
            log.d("id: ", Integer.valueOf(db.getId()));
            writer.write(32);
            writer.write(db.getFileName());
            log.d("name: ", db.getFileName());
            writer.write(10);
        }
    }

    /* loaded from: classes.dex */
    public static class LanguageDB {
        private final String dbFileName;
        private final int dbId;
        private final String displayName;
        private final String displaySummary;
        private final DbInfo info;

        LanguageDB(DbInfo info, String filename, int dbid, String displayName, String displaySummary) {
            this.info = info;
            this.dbFileName = filename;
            this.dbId = dbid;
            this.displayName = displayName;
            this.displaySummary = displaySummary;
        }

        DbType getType() {
            return this.info.type;
        }

        public String getFileName() {
            return this.dbFileName;
        }

        public String getDisplayName() {
            return this.displayName;
        }

        public String getDisplaySummary() {
            return this.displaySummary;
        }

        String getAssetFileName() {
            return this.info.compressed ? this.dbFileName + SwypeCoreLibrary.COMPRESSED_FILE_EXTENSION : this.dbFileName;
        }

        public int getId() {
            return this.dbId;
        }

        public boolean isLDB() {
            return this.info.type == DbType.LDB;
        }

        public boolean isCatDB() {
            return this.info.type == DbType.CDB;
        }

        public boolean isALMLDB() {
            return this.info == DbInfo.ALM_LDB;
        }

        public boolean isRegularLDB() {
            return this.info == DbInfo.LDB;
        }

        public boolean isHDB() {
            return this.info == DbInfo.HDB;
        }

        public boolean isTraceLDB() {
            return this.info == DbInfo.TRACE_LDB1 || this.info == DbInfo.TRACE_LDB2;
        }

        boolean isCompressed() {
            return this.info.compressed;
        }

        public String toString() {
            return String.format("[%s %s %#x]", this.info, this.dbFileName, Integer.valueOf(this.dbId));
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public static class DbFileDetector {
        private final IMEApplication app;
        private final Set<String> assetFileNames;
        private boolean loadExternalPath;
        private final Set<String> privateFileNames;

        public DbFileDetector(Context context, boolean loadExternal) {
            LogManager.Trace trace = DatabaseConfig.trace;
            this.app = IMEApplication.from(context);
            this.privateFileNames = this.app.getPrivateFiles();
            this.assetFileNames = this.app.getAssetFileNames("");
            this.loadExternalPath = loadExternal;
            LogManager.Trace trace2 = DatabaseConfig.trace;
        }

        protected boolean dbFileIsInApk(LanguageDB databaseElement) {
            return this.assetFileNames.contains(databaseElement.getAssetFileName());
        }

        protected boolean dbFileIsInExternalPath(String externalFile) {
            if (this.loadExternalPath) {
                return DatabaseConfig.foundFileInExternalPath(externalFile) | DatabaseConfig.foundFileInExternalPath(externalFile + SwypeCoreLibrary.COMPRESSED_FILE_EXTENSION);
            }
            return false;
        }

        public boolean fileExists(String fileName) {
            return this.privateFileNames.contains(fileName);
        }
    }

    public static void removeIncompatibleDBFiles(Context context, String language) {
        String[] languageList = null;
        if (language != null && !language.isEmpty()) {
            languageList = language.split("/");
            boolean fileDeleted = false;
            for (String languageName : languageList) {
                List<String> targetLanguage = new ArrayList<>();
                targetLanguage.add(languageName);
                List<LanguageDB> languageDB = getLanguageDBList(context, targetLanguage).get(languageName);
                if (languageDB != null) {
                    Iterator<LanguageDB> it = languageDB.iterator();
                    while (it.hasNext()) {
                        String fileName = it.next().getFileName();
                        String absoluteFileName = context.getFilesDir().getAbsolutePath() + "/" + fileName;
                        fileDeleted = new File(absoluteFileName).delete() || fileDeleted;
                    }
                    String absoluteFileName2 = context.getFilesDir().getAbsolutePath() + "/" + languageName + ".mdf";
                    fileDeleted = new File(absoluteFileName2).delete() || fileDeleted;
                }
            }
            if (!fileDeleted) {
                return;
            }
        }
        updateLanguage(context, null);
        if (oldLangugeIdAndLdbFileMappingTable != null) {
            oldLangugeIdAndLdbFileMappingTable.clear();
            oldLangugeIdAndLdbFileMappingTable = null;
        }
        getOldLanguageIdAndFileMappingTable(context);
        IMEApplication app = IMEApplication.from(context);
        app.refreshInputMethods();
        if (languageList != null) {
            app.onUpdateLanguage(languageList[0]);
            Connect.from(context).removeInvalidatedLanguage(app.getInputMethods().getLanguageIdIntByLanguageName(languageList[0]));
        }
    }
}
