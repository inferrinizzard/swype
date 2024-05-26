package com.nuance.swype.connect;

import android.content.Context;
import com.nuance.swype.input.DatabaseConfig;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.LanguageList;
import com.nuance.swype.util.LogManager;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/* loaded from: classes.dex */
public class LanguageInstall {
    private static final String ALM_INCLUDED_IN_FILE = "ALM";
    private static final int BUFFER_SIZE = 20480;
    private static final String COMPRESSION_FILE_EXTENSION = ".mp3";
    private static final String GZIP_FILE_EXTENSION = ".gz";
    private static final String LANGUAGE_METADATA_FILE_EXT = ".mdf";
    private static final String REQUIRES_TAG = "[REQ]";
    private static final String STAGING_FOLDER = "languageDownload/";
    private static final LogManager.Log log = LogManager.getLog("LanguageInstall");
    private final Context context;
    private final List<String> deployedFileList;
    private final List<String> extractedFileList;
    private final String languageName;
    private String languagePackFileName;
    private final String metadataFileName;
    private final String nativeDataFileDir;
    private final String stagingFolderName;

    public LanguageInstall(String languageName, Context context) {
        this.context = context;
        File filesDir = context.getFilesDir();
        this.nativeDataFileDir = filesDir.getAbsolutePath();
        this.languageName = languageName;
        this.extractedFileList = new LinkedList();
        this.deployedFileList = new LinkedList();
        this.stagingFolderName = canonicalizeFileName(STAGING_FOLDER + languageName) + "/";
        this.metadataFileName = canonicalizeFileName(languageName + LANGUAGE_METADATA_FILE_EXT);
    }

    public boolean install(String tempPath) throws Exception {
        log.d("install " + this.languageName + " file=" + tempPath);
        List<String> filesToDelete = new LinkedList<>();
        try {
            filesToDelete = readMetadataFile();
        } catch (Exception e) {
        }
        for (String file : filesToDelete) {
            deleteFile(file);
        }
        deleteFile(this.metadataFileName);
        installPack(tempPath);
        return true;
    }

    public boolean installALM(String tempPath) throws Exception {
        IMEApplication app = IMEApplication.from(this.context);
        installPack(tempPath);
        DatabaseConfig.updateLanguage(this.context, this.languageName);
        app.refreshInputMethods();
        app.onUpdateLanguage(this.languageName);
        return true;
    }

    public synchronized void uninstall() throws Exception {
        LanguageList langList = new LanguageList(this.context);
        boolean isSharedHdb = langList.isHdbShared(this.languageName);
        String hdbName = langList.getHdbName(this.languageName);
        String hdbName2 = canonicalizeFileName(hdbName);
        boolean isRemoved = langList.removeDownloadedLanguage(this.languageName);
        List<String> filesToDelete = new LinkedList<>();
        try {
            filesToDelete = readMetadataFile();
        } catch (Exception e) {
        }
        if (isRemoved) {
            for (String file : filesToDelete) {
                if (!isSharedHdb || hdbName2.compareTo(file) != 0) {
                    deleteFile(file);
                }
            }
        }
        deleteFile(this.metadataFileName);
        if (isRemoved) {
            DatabaseConfig.updateLanguage(this.context, null);
            IMEApplication.from(this.context).refreshInputMethods();
        }
    }

    private boolean deleteFile(String fileName) {
        return new File(fileName).delete();
    }

    protected List<String> readMetadataFile(String fileName) throws IOException {
        List<String> fileList = new LinkedList<>();
        BufferedReader reader = null;
        try {
            try {
                BufferedReader reader2 = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF-8"), 8192);
                while (true) {
                    try {
                        String file = reader2.readLine();
                        if (file == null) {
                            break;
                        }
                        fileList.add(file);
                    } catch (UnsupportedEncodingException e) {
                        e = e;
                        reader = reader2;
                        log.e(e.getMessage());
                        if (reader != null) {
                            reader.close();
                        }
                        return fileList;
                    } catch (Throwable th) {
                        th = th;
                        reader = reader2;
                        if (reader != null) {
                            reader.close();
                        }
                        throw th;
                    }
                }
                reader2.close();
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (UnsupportedEncodingException e2) {
            e = e2;
        }
        return fileList;
    }

    protected List<String> readMetadataFile() throws IOException {
        return readMetadataFile(this.metadataFileName);
    }

    public void installPack(String languagePackFileName) throws Exception {
        boolean stagingFolderReady;
        this.languagePackFileName = languagePackFileName;
        if (fileExists(this.stagingFolderName)) {
            stagingFolderReady = deleteAllFiles(this.stagingFolderName);
        } else {
            stagingFolderReady = makeStagingFolder();
        }
        if (stagingFolderReady) {
            if (extractFiles() && moveExtractedFiles()) {
                createMetadataFile();
                deleteDownloadedFile();
            }
            deleteStagingFolder();
        }
    }

    private void deleteStagingFolder() throws IOException {
        deleteAllFiles(this.stagingFolderName);
        if (!new File(this.stagingFolderName).delete()) {
            log.e("Failed delete on staging folder " + this.stagingFolderName);
        }
    }

    private boolean makeStagingFolder() {
        return new File(this.stagingFolderName).mkdirs();
    }

    private boolean moveExtractedFiles() {
        boolean success = false;
        for (String fileName : this.extractedFileList) {
            if (fileName.startsWith(REQUIRES_TAG)) {
                String actualFileName = fileName.substring(5);
                this.deployedFileList.add(REQUIRES_TAG + canonicalizeFileName(actualFileName));
            } else {
                File file = new File(fileName);
                String name = file.getName();
                if (name.endsWith(GZIP_FILE_EXTENSION)) {
                    name = name.substring(0, name.length() - 3);
                }
                String name2 = getLdbNameFromLangXML(this.languageName, name);
                if (name2.compareTo("") == 0) {
                    name2 = file.getName();
                }
                String moveTo = canonicalizeFileName(name2);
                File newPath = new File(moveTo);
                success |= file.renameTo(newPath);
                if (!success) {
                    break;
                }
                this.deployedFileList.add(moveTo);
            }
        }
        return success;
    }

    private String getLdbNameFromLangXML(String lang, String fileName) {
        Map<String, List<DatabaseConfig.LanguageDB>> databases = DatabaseConfig.getLanguageDBList(this.context, null);
        String ldbName = "";
        boolean isRegularLdb = !fileName.contains(ALM_INCLUDED_IN_FILE);
        Iterator<Map.Entry<String, List<DatabaseConfig.LanguageDB>>> it = databases.entrySet().iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            Map.Entry<String, List<DatabaseConfig.LanguageDB>> entry = it.next();
            if (entry.getKey().compareToIgnoreCase(lang) == 0) {
                Iterator<DatabaseConfig.LanguageDB> it2 = entry.getValue().iterator();
                while (it2.hasNext()) {
                    if (it2.next().getFileName().compareTo(fileName) == 0) {
                        return "";
                    }
                }
                Iterator<DatabaseConfig.LanguageDB> it3 = entry.getValue().iterator();
                while (true) {
                    if (!it3.hasNext()) {
                        break;
                    }
                    DatabaseConfig.LanguageDB db = it3.next();
                    if (!db.isHDB() && !db.isTraceLDB()) {
                        if (isRegularLdb && db.isRegularLDB()) {
                            ldbName = db.getFileName();
                            break;
                        }
                        if (!isRegularLdb && db.isALMLDB()) {
                            ldbName = db.getFileName();
                            break;
                        }
                    }
                }
            }
        }
        return ldbName;
    }

    private boolean deleteAllFiles(String folderName) throws IOException {
        boolean success = false;
        File folder = new File(folderName);
        String[] fileList = folder.list();
        if (fileList != null && fileList.length > 0) {
            for (String fileName : fileList) {
                File file = new File(folder, fileName);
                success |= file.delete();
                if (!success) {
                    return success;
                }
            }
            return success;
        }
        return true;
    }

    protected boolean createMetadataFile() throws IOException {
        Writer writer = null;
        try {
            try {
                Writer writer2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.metadataFileName), "UTF-8"));
                try {
                    for (String fileName : this.deployedFileList) {
                        writer2.write(fileName);
                        writer2.write("\n");
                    }
                    writer2.close();
                    return true;
                } catch (UnsupportedEncodingException e) {
                    e = e;
                    writer = writer2;
                    log.e(e.getMessage());
                    if (writer == null) {
                        return false;
                    }
                    writer.close();
                    return false;
                } catch (Throwable th) {
                    th = th;
                    writer = writer2;
                    if (writer != null) {
                        writer.close();
                    }
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (UnsupportedEncodingException e2) {
            e = e2;
        }
    }

    protected boolean extractFiles() throws IOException {
        int entries = 0;
        int total = 0;
        ZipInputStream languagePackZipInputStream = null;
        try {
            InputStream languagePackInputStream = new FileInputStream(this.languagePackFileName);
            ZipInputStream languagePackZipInputStream2 = new ZipInputStream(new BufferedInputStream(languagePackInputStream, BUFFER_SIZE));
            try {
                byte[] fileDataBuffer = new byte[BUFFER_SIZE];
                String canonicalID = new File(getStagingFolder()).getCanonicalPath();
                do {
                    ZipEntry languageResourceZipEntry = languagePackZipInputStream2.getNextEntry();
                    if (languageResourceZipEntry == null) {
                        languagePackZipInputStream2.close();
                        return true;
                    }
                    String languageResourceFileName = languageResourceZipEntry.getName();
                    String outputFileName = getStagingFolder() + languageResourceFileName;
                    if (!isValidFile(outputFileName, canonicalID)) {
                        languagePackZipInputStream2.close();
                        return false;
                    }
                    FileOutputStream languageResourceOutputStream = new FileOutputStream(outputFileName);
                    OutputStream out = new BufferedOutputStream(languageResourceOutputStream, BUFFER_SIZE);
                    while (true) {
                        int bytesRead = languagePackZipInputStream2.read(fileDataBuffer);
                        if (bytesRead == -1) {
                            break;
                        }
                        out.write(fileDataBuffer, 0, bytesRead);
                        total += bytesRead;
                    }
                    out.close();
                    if (outputFileName.endsWith(".mp3")) {
                        String unzipFileName = outputFileName.substring(0, outputFileName.length() - 4);
                        File zippedLdb = new File(outputFileName);
                        InputStream ins = new FileInputStream(zippedLdb);
                        GZIPInputStream in = new GZIPInputStream(ins);
                        BufferedInputStream bis = new BufferedInputStream(in, BUFFER_SIZE);
                        FileOutputStream unzipFileStream = new FileOutputStream(unzipFileName);
                        OutputStream bos = new BufferedOutputStream(unzipFileStream, BUFFER_SIZE);
                        copy(bis, bos);
                        bos.close();
                        bis.close();
                        outputFileName = unzipFileName;
                    }
                    this.extractedFileList.add(outputFileName);
                    entries++;
                    if (entries > 1024) {
                        log.e("Security checking of install language failed, too many files ");
                        languagePackZipInputStream2.close();
                        return false;
                    }
                } while (total <= 104857600);
                log.e("Security checking of install language failed, files' size too large ");
                languagePackZipInputStream2.close();
                return false;
            } catch (Throwable th) {
                th = th;
                languagePackZipInputStream = languagePackZipInputStream2;
                if (languagePackZipInputStream != null) {
                    languagePackZipInputStream.close();
                }
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    protected boolean deleteDownloadedFile() {
        return new File(this.languagePackFileName).delete();
    }

    protected boolean fileExists(String name) {
        try {
            boolean isThere = new File(name).exists();
            return isThere;
        } catch (SecurityException ex) {
            log.e("Security exception in file.exists()", ex);
            return false;
        }
    }

    public String getStagingFolder() {
        return this.stagingFolderName;
    }

    protected void copy(InputStream in, OutputStream out) throws IOException {
        byte[] fileDataBuffer = new byte[BUFFER_SIZE];
        while (true) {
            int bytesRead = in.read(fileDataBuffer);
            if (bytesRead != -1) {
                out.write(fileDataBuffer, 0, bytesRead);
            } else {
                return;
            }
        }
    }

    public String canonicalizeFileName(String name) {
        if (name != null && name.endsWith(GZIP_FILE_EXTENSION)) {
            name = name.substring(0, name.length() - 3);
        }
        log.d("canonicalizeFilenName name=", name);
        return this.nativeDataFileDir + "/" + name;
    }

    private static boolean isAsciiPrintable(char ch) {
        return ch >= ' ' && ch < 127;
    }

    private static boolean isAsciiPrintable(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if (!isAsciiPrintable(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private static boolean isValidFile(String filename, String intendedDir) {
        if (!isAsciiPrintable(filename)) {
            log.e("Security checking of language resource name failed, no pure ASCII name");
            return false;
        }
        try {
            if (new File(filename).getCanonicalPath().startsWith(intendedDir)) {
                return true;
            }
            log.e("Security checking of language resource name failed: " + filename);
            return false;
        } catch (Exception e) {
            log.e("Security checking of language resource name failed, I/O exception");
            return false;
        }
    }
}
