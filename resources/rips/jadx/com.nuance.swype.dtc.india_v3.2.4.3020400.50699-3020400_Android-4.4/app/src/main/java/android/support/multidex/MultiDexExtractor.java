package android.support.multidex;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.util.Log;
import com.facebook.GraphResponse;
import com.nuance.sns.ScraperStatus;
import com.nuance.swype.input.hardkey.HardKeyboardManager;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/* loaded from: classes.dex */
final class MultiDexExtractor {
    private static Method sApplyMethod;

    /* JADX INFO: Access modifiers changed from: package-private */
    public static List<File> load(Context context, ApplicationInfo applicationInfo, File dexDir, boolean forceReload) throws IOException {
        List<File> files;
        Log.i("MultiDex", "MultiDexExtractor.load(" + applicationInfo.sourceDir + ", " + forceReload + ")");
        File sourceApk = new File(applicationInfo.sourceDir);
        long currentCrc = ZipUtil.getZipCrc(sourceApk);
        if (currentCrc == -1) {
            currentCrc--;
        }
        if (!forceReload) {
            SharedPreferences multiDexPreferences = getMultiDexPreferences(context);
            if (!((multiDexPreferences.getLong("timestamp", -1L) == getTimeStamp(sourceApk) && multiDexPreferences.getLong("crc", -1L) == currentCrc) ? false : true)) {
                try {
                    files = loadExistingExtractions(context, sourceApk, dexDir);
                } catch (IOException ioe) {
                    Log.w("MultiDex", "Failed to reload existing extracted secondary dex files, falling back to fresh extraction", ioe);
                    files = performExtractions(sourceApk, dexDir);
                    putStoredApkInfo(context, getTimeStamp(sourceApk), currentCrc, files.size() + 1);
                }
                Log.i("MultiDex", "load found " + files.size() + " secondary dex files");
                return files;
            }
        }
        Log.i("MultiDex", "Detected that extraction must be performed.");
        files = performExtractions(sourceApk, dexDir);
        putStoredApkInfo(context, getTimeStamp(sourceApk), currentCrc, files.size() + 1);
        Log.i("MultiDex", "load found " + files.size() + " secondary dex files");
        return files;
    }

    private static List<File> loadExistingExtractions(Context context, File sourceApk, File dexDir) throws IOException {
        Log.i("MultiDex", "loading existing secondary dex files");
        String extractedFilePrefix = sourceApk.getName() + ".classes";
        int totalDexNumber = getMultiDexPreferences(context).getInt("dex.number", 1);
        List<File> files = new ArrayList<>(totalDexNumber);
        for (int secondaryNumber = 2; secondaryNumber <= totalDexNumber; secondaryNumber++) {
            String fileName = extractedFilePrefix + secondaryNumber + ".zip";
            File extractedFile = new File(dexDir, fileName);
            if (extractedFile.isFile()) {
                files.add(extractedFile);
                if (!verifyZipFile(extractedFile)) {
                    Log.i("MultiDex", "Invalid zip file: " + extractedFile);
                    throw new IOException("Invalid ZIP file.");
                }
            } else {
                throw new IOException("Missing extracted secondary dex file '" + extractedFile.getPath() + "'");
            }
        }
        return files;
    }

    private static long getTimeStamp(File archive) {
        long timeStamp = archive.lastModified();
        if (timeStamp == -1) {
            return timeStamp - 1;
        }
        return timeStamp;
    }

    private static List<File> performExtractions(File sourceApk, File dexDir) throws IOException {
        String extractedFilePrefix = sourceApk.getName() + ".classes";
        prepareDexDir(dexDir, extractedFilePrefix);
        List<File> files = new ArrayList<>();
        ZipFile apk = new ZipFile(sourceApk);
        int secondaryNumber = 2;
        try {
            ZipEntry dexFile = apk.getEntry(new StringBuilder("classes2.dex").toString());
            while (dexFile != null) {
                String fileName = extractedFilePrefix + secondaryNumber + ".zip";
                File extractedFile = new File(dexDir, fileName);
                files.add(extractedFile);
                Log.i("MultiDex", "Extraction is needed for file " + extractedFile);
                int numAttempts = 0;
                boolean isExtractionSuccessful = false;
                while (numAttempts < 3 && !isExtractionSuccessful) {
                    numAttempts++;
                    extract(apk, dexFile, extractedFile, extractedFilePrefix);
                    isExtractionSuccessful = verifyZipFile(extractedFile);
                    Log.i("MultiDex", "Extraction " + (isExtractionSuccessful ? GraphResponse.SUCCESS_KEY : ScraperStatus.SCRAPING_STATUS_FAILED) + " - length " + extractedFile.getAbsolutePath() + ": " + extractedFile.length());
                    if (!isExtractionSuccessful) {
                        extractedFile.delete();
                        if (extractedFile.exists()) {
                            Log.w("MultiDex", "Failed to delete corrupted secondary dex '" + extractedFile.getPath() + "'");
                        }
                    }
                }
                if (!isExtractionSuccessful) {
                    throw new IOException("Could not create zip file " + extractedFile.getAbsolutePath() + " for secondary dex (" + secondaryNumber + ")");
                }
                secondaryNumber++;
                dexFile = apk.getEntry("classes" + secondaryNumber + ".dex");
            }
            return files;
        } finally {
            try {
                apk.close();
            } catch (IOException e) {
                Log.w("MultiDex", "Failed to close resource", e);
            }
        }
    }

    private static void putStoredApkInfo(Context context, long timeStamp, long crc, int totalDexNumber) {
        SharedPreferences.Editor edit = getMultiDexPreferences(context).edit();
        edit.putLong("timestamp", timeStamp);
        edit.putLong("crc", crc);
        edit.putInt("dex.number", totalDexNumber);
        if (sApplyMethod != null) {
            try {
                sApplyMethod.invoke(edit, new Object[0]);
                return;
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e2) {
            }
        }
        edit.commit();
    }

    private static SharedPreferences getMultiDexPreferences(Context context) {
        return context.getSharedPreferences("multidex.version", Build.VERSION.SDK_INT < 11 ? 0 : 4);
    }

    private static void prepareDexDir(File dexDir, final String extractedFilePrefix) throws IOException {
        mkdirChecked(dexDir.getParentFile());
        mkdirChecked(dexDir);
        FileFilter filter = new FileFilter() { // from class: android.support.multidex.MultiDexExtractor.1
            @Override // java.io.FileFilter
            public final boolean accept(File pathname) {
                return !pathname.getName().startsWith(extractedFilePrefix);
            }
        };
        File[] files = dexDir.listFiles(filter);
        if (files == null) {
            Log.w("MultiDex", "Failed to list secondary dex dir content (" + dexDir.getPath() + ").");
            return;
        }
        for (File oldFile : files) {
            Log.i("MultiDex", "Trying to delete old file " + oldFile.getPath() + " of size " + oldFile.length());
            if (!oldFile.delete()) {
                Log.w("MultiDex", "Failed to delete old file " + oldFile.getPath());
            } else {
                Log.i("MultiDex", "Deleted old file " + oldFile.getPath());
            }
        }
    }

    private static void mkdirChecked(File dir) throws IOException {
        dir.mkdir();
        if (!dir.isDirectory()) {
            File parent = dir.getParentFile();
            if (parent == null) {
                Log.e("MultiDex", "Failed to create dir " + dir.getPath() + ". Parent file is null.");
            } else {
                Log.e("MultiDex", "Failed to create dir " + dir.getPath() + ". parent file is a dir " + parent.isDirectory() + ", a file " + parent.isFile() + ", exists " + parent.exists() + ", readable " + parent.canRead() + ", writable " + parent.canWrite());
            }
            throw new IOException("Failed to create cache directory " + dir.getPath());
        }
    }

    private static void extract(ZipFile apk, ZipEntry dexFile, File extractTo, String extractedFilePrefix) throws IOException, FileNotFoundException {
        InputStream in = apk.getInputStream(dexFile);
        File tmp = File.createTempFile(extractedFilePrefix, ".zip", extractTo.getParentFile());
        Log.i("MultiDex", "Extracting " + tmp.getPath());
        try {
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(tmp)));
            try {
                ZipEntry classesDex = new ZipEntry("classes.dex");
                classesDex.setTime(dexFile.getTime());
                out.putNextEntry(classesDex);
                byte[] buffer = new byte[HardKeyboardManager.META_CTRL_RIGHT_ON];
                for (int length = in.read(buffer); length != -1; length = in.read(buffer)) {
                    out.write(buffer, 0, length);
                }
                out.closeEntry();
                out.close();
                Log.i("MultiDex", "Renaming to " + extractTo.getPath());
                if (!tmp.renameTo(extractTo)) {
                    throw new IOException("Failed to rename \"" + tmp.getAbsolutePath() + "\" to \"" + extractTo.getAbsolutePath() + "\"");
                }
            } catch (Throwable th) {
                out.close();
                throw th;
            }
        } finally {
            closeQuietly(in);
            tmp.delete();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean verifyZipFile(File file) {
        ZipFile zipFile;
        try {
            try {
                zipFile = new ZipFile(file);
            } catch (ZipException ex) {
                Log.w("MultiDex", "File " + file.getAbsolutePath() + " is not a valid zip file.", ex);
            }
        } catch (IOException ex2) {
            Log.w("MultiDex", "Got an IOException trying to open zip file: " + file.getAbsolutePath(), ex2);
        }
        try {
            zipFile.close();
            return true;
        } catch (IOException e) {
            Log.w("MultiDex", "Failed to close zip file: " + file.getAbsolutePath());
            return false;
        }
    }

    private static void closeQuietly(Closeable closeable) {
        try {
            closeable.close();
        } catch (IOException e) {
            Log.w("MultiDex", "Failed to close resource", e);
        }
    }

    static {
        try {
            sApplyMethod = SharedPreferences.Editor.class.getMethod("apply", new Class[0]);
        } catch (NoSuchMethodException e) {
            sApplyMethod = null;
        }
    }
}
