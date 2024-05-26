package com.nuance.swype.input;

import android.content.Context;
import com.nuance.swype.input.InputMethods;
import com.nuance.swype.util.LogManager;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public class CategoryDBList {
    private static final LogManager.Log log = LogManager.getLog("CategoryDBList");
    AppPreferences appPrefs;
    private List<String> chineseLangNames;
    private final Context context;

    public CategoryDBList(Context context) {
        this(context, false);
    }

    public CategoryDBList(Context context, boolean isRefresh) {
        this.context = context;
        this.appPrefs = AppPreferences.from(context);
        this.chineseLangNames = new ArrayList();
        this.chineseLangNames.add("Chinese_CN");
        this.chineseLangNames.add("Chinese_HK");
        this.chineseLangNames.add("Chinese_TW");
    }

    private synchronized Map<String, List<String>> getCatDBs() {
        Map<String, List<String>> catDBs;
        catDBs = new HashMap<>();
        for (String lang : this.chineseLangNames) {
            List<String> cdbList = this.appPrefs.getStrings(AppPreferences.AVAILABLE_CATEGORYDBS + lang, null);
            if (cdbList != null) {
                catDBs.put(lang, cdbList);
            }
        }
        return catDBs;
    }

    public Map<String, List<String>> getAvailableCatDbList() {
        return getCatDBs();
    }

    public List<String> getAvailableCDBs(String languageName) {
        return getCatDBs().get(languageName);
    }

    public List<String> getShowableCDBs(String languageName) {
        Map<String, List<String>> catDBs = getCatDBs();
        List<String> lst = new ArrayList<>();
        if (catDBs.get(languageName) != null) {
            for (String db : catDBs.get(languageName)) {
                lst.add(db);
            }
        }
        return lst;
    }

    private synchronized void updateListToAppPreferences(Map<String, List<String>> catDBs) {
        for (Map.Entry<String, List<String>> entry : catDBs.entrySet()) {
            String lang = entry.getKey();
            List<String> cdbs = entry.getValue();
            if (lang != null && cdbs != null) {
                this.appPrefs.setStrings(AppPreferences.AVAILABLE_CATEGORYDBS + lang, cdbs);
            }
        }
    }

    public String getFileName(String cdbInfo) {
        String[] cdb;
        if (cdbInfo == null || (cdb = cdbInfo.split("\\|")) == null || cdb.length != 2) {
            return null;
        }
        return cdb[1];
    }

    public int getFileId(String cdbInfo) {
        String[] cdb;
        if (cdbInfo == null || (cdb = cdbInfo.split("\\|")) == null || cdb.length != 2) {
            return 0;
        }
        int id = Integer.parseInt(cdb[0]);
        return id;
    }

    public boolean isCategoryInstalled(String filename) {
        boolean installed = false;
        for (Map.Entry<String, List<String>> entry : getCatDBs().entrySet()) {
            String lang = entry.getKey();
            List<String> cdbs = entry.getValue();
            if (lang != null && cdbs != null) {
                Iterator<String> it = cdbs.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    if (it.next().endsWith(filename)) {
                        installed = true;
                        break;
                    }
                }
                if (installed) {
                    break;
                }
            }
        }
        log.d("isCategoryInstalled cat= ", filename, " installed=", Boolean.valueOf(installed));
        return installed;
    }

    public boolean isCategoryInstalled(int catId) {
        boolean installed = false;
        for (Map.Entry<String, List<String>> entry : getCatDBs().entrySet()) {
            String lang = entry.getKey();
            List<String> cdbs = entry.getValue();
            if (lang != null && cdbs != null) {
                Iterator<String> it = cdbs.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    String category = it.next();
                    if (getFileId(category) == catId) {
                        installed = true;
                        break;
                    }
                }
                if (installed) {
                    break;
                }
            }
        }
        log.d("isCategoryInstalled cat= ", Integer.valueOf(catId), " installed=", Boolean.valueOf(installed));
        return installed;
    }

    public boolean installDownloadedCategoryDBFile(String path, String fileName, int catId, int languageId) {
        String cdb = catId + "|" + fileName;
        boolean isAdded = false;
        InputMethods.Language installLang = InputMethods.from(this.context).findCoreInputLanguage(languageId);
        if (installLang == null) {
            return false;
        }
        synchronized (this) {
            Map<String, List<String>> catDBs = getCatDBs();
            List<String> cdbs = catDBs.get(installLang.mEnglishName);
            if (cdbs == null) {
                cdbs = new ArrayList<>();
                catDBs.put(installLang.mEnglishName, cdbs);
            }
            if (!cdbs.contains(cdb)) {
                cdbs.add(cdb);
                File source = new File(path);
                File destination = new File(this.context.getFilesDir(), fileName);
                log.d("    moved to destination: ", destination.toString());
                if (source.renameTo(destination)) {
                    isAdded = true;
                }
            } else {
                File source2 = new File(path);
                File destination2 = new File(this.context.getFilesDir(), fileName);
                log.d("    moved to destination: ", destination2.toString());
                if (source2.renameTo(destination2)) {
                    isAdded = true;
                }
            }
            if (isAdded && getSelectedCatDBCount(installLang) < 8) {
                this.appPrefs.setBoolean(fileName, true);
            }
            updateListToAppPreferences(catDBs);
        }
        return isAdded;
    }

    public synchronized void postInstallRefresh(int languageId) {
        InputMethods.Language installLang = InputMethods.from(this.context).findCoreInputLanguage(languageId);
        if (installLang != null && installLang.isChineseLanguage()) {
            DatabaseConfig.updateLanguage(this.context, installLang.mEnglishName);
        }
        IMEApplication.from(this.context).refreshInputMethods();
    }

    public synchronized boolean uninstallDownloadedCategoryDB(String fileName, int catId, int languageId) {
        boolean z = false;
        synchronized (this) {
            String cdb = catId + "|" + fileName;
            boolean isRemoved = false;
            InputMethods.Language uninstallLang = InputMethods.from(this.context).findCoreInputLanguage(languageId);
            if (uninstallLang != null) {
                Map<String, List<String>> catDBs = getCatDBs();
                List<String> cdbs = catDBs.get(uninstallLang.mEnglishName);
                if (cdbs != null && cdbs.contains(cdb)) {
                    if (this.appPrefs.getBoolean(fileName, false)) {
                        this.appPrefs.setBoolean(fileName, false);
                    }
                    cdbs.remove(cdb);
                    isRemoved = deleteFile(fileName);
                }
                updateListToAppPreferences(catDBs);
                z = isRemoved;
            }
        }
        return z;
    }

    private int getSelectedCatDBCount(InputMethods.Language lang) {
        int count = 0;
        List<String> cdbs = getCatDBs().get(lang.mEnglishName);
        if (cdbs != null && cdbs.size() > 0) {
            for (String cdb : cdbs) {
                String cdbName = getFileName(cdb);
                if (this.appPrefs.getBoolean(cdbName, false)) {
                    count++;
                }
            }
        }
        return count;
    }

    private boolean deleteFile(String fileName) {
        return new File(fileName).delete();
    }
}
