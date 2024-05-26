package com.localytics.android;

import android.content.Context;
import android.text.TextUtils;
import com.localytics.android.Localytics;
import com.nuance.swype.input.hardkey.HardKeyboardManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/* loaded from: classes.dex */
class Utils {
    private static final long FILE_COPY_BUFFER_SIZE = 1048576;

    /* loaded from: classes.dex */
    interface Mapper<T, K> {
        K transform(T t);
    }

    Utils() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean decompressZipFile(String zipFileDir, String unzipFileDir, String filename) {
        ZipInputStream zis = null;
        try {
            try {
                ZipInputStream zis2 = new ZipInputStream(new FileInputStream(zipFileDir + File.separator + filename));
                try {
                    byte[] buffer = new byte[8192];
                    while (true) {
                        ZipEntry ze = zis2.getNextEntry();
                        if (ze == null) {
                            try {
                                zis2.close();
                                return true;
                            } catch (IOException e) {
                                Localytics.Log.w("Caught IOException", e);
                                return false;
                            }
                        }
                        String entryName = unzipFileDir + File.separator + ze.getName();
                        if (!ze.isDirectory()) {
                            FileOutputStream fos = new FileOutputStream(entryName);
                            while (true) {
                                int byteRead = zis2.read(buffer, 0, 8192);
                                if (byteRead <= 0) {
                                    break;
                                }
                                fos.write(buffer, 0, byteRead);
                            }
                            fos.close();
                            zis2.closeEntry();
                        } else if (!new File(entryName).mkdir()) {
                            Localytics.Log.w(String.format("Could not create directory %s", entryName));
                        }
                    }
                } catch (IOException e2) {
                    e = e2;
                    zis = zis2;
                    Localytics.Log.w("Caught IOException", e);
                    if (zis != null) {
                        try {
                            zis.close();
                        } catch (IOException e3) {
                            Localytics.Log.w("Caught IOException", e3);
                            return false;
                        }
                    }
                    return false;
                } catch (Throwable th) {
                    th = th;
                    zis = zis2;
                    if (zis != null) {
                        try {
                            zis.close();
                        } catch (IOException e4) {
                            Localytics.Log.w("Caught IOException", e4);
                            return false;
                        }
                    }
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (IOException e5) {
            e = e5;
        }
    }

    static String getMD5CheckSum(File file) {
        try {
            MessageDigest md5Digest = MessageDigest.getInstance("MD5");
            return getFileCheckSum(md5Digest, file);
        } catch (NoSuchAlgorithmException e) {
            Localytics.Log.w("Caught NoSuchAlgorithmException", e);
            return "";
        }
    }

    private static String getFileCheckSum(MessageDigest digest, File file) {
        FileInputStream fis = null;
        try {
            try {
                FileInputStream fis2 = new FileInputStream(file);
                try {
                    byte[] buffer = new byte[HardKeyboardManager.META_CTRL_RIGHT_ON];
                    while (true) {
                        int read = fis2.read(buffer);
                        if (read != -1) {
                            digest.update(buffer, 0, read);
                        } else {
                            try {
                                break;
                            } catch (IOException e) {
                                Localytics.Log.w("Caught IOException", e);
                            }
                        }
                    }
                    fis2.close();
                    byte[] bytes = digest.digest();
                    StringBuilder sb = new StringBuilder();
                    for (byte b : bytes) {
                        sb.append(Integer.toString((b & 255) + 256, 16).substring(1));
                    }
                    return sb.toString();
                } catch (IOException e2) {
                    e = e2;
                    fis = fis2;
                    Localytics.Log.w("Caught IOException", e);
                    if (fis == null) {
                        return "";
                    }
                    try {
                        fis.close();
                        return "";
                    } catch (IOException e3) {
                        Localytics.Log.w("Caught IOException", e3);
                        return "";
                    }
                } catch (Throwable th) {
                    th = th;
                    fis = fis2;
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (IOException e4) {
                            Localytics.Log.w("Caught IOException", e4);
                        }
                    }
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (IOException e5) {
            e = e5;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void copyFile(File srcFile, File destFile) throws IOException {
        validateFileRequirements(srcFile, destFile);
        File parentDir = destFile.getParentFile();
        if (parentDir != null && !parentDir.mkdirs() && !parentDir.isDirectory()) {
            throw new IOException("Parent directory " + parentDir + "cannot be created");
        }
        FileChannel src = null;
        FileChannel dest = null;
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            FileInputStream fis2 = new FileInputStream(srcFile);
            try {
                FileOutputStream fos2 = new FileOutputStream(destFile);
                try {
                    src = fis2.getChannel();
                    dest = fos2.getChannel();
                    long pos = 0;
                    while (true) {
                        long copied = dest.transferFrom(src, pos, FILE_COPY_BUFFER_SIZE);
                        if (copied <= 0) {
                            break;
                        } else {
                            pos += copied;
                        }
                    }
                    if (src != null) {
                        src.close();
                    }
                    if (dest != null) {
                        dest.close();
                    }
                    fis2.close();
                    fos2.close();
                    String srcCheckSum = getMD5CheckSum(srcFile);
                    String dstCheckSum = getMD5CheckSum(destFile);
                    if (TextUtils.isEmpty(srcCheckSum)) {
                        throw new IOException("Failed to get checksum for source file " + srcFile);
                    }
                    if (TextUtils.isEmpty(dstCheckSum)) {
                        throw new IOException("Failed to get checksum for destination file " + destFile);
                    }
                    if (!srcCheckSum.equals(dstCheckSum)) {
                        throw new IOException(srcFile + " and " + destFile + " have different checksum");
                    }
                } catch (Throwable th) {
                    th = th;
                    fos = fos2;
                    fis = fis2;
                    if (src != null) {
                        src.close();
                    }
                    if (dest != null) {
                        dest.close();
                    }
                    if (fis != null) {
                        fis.close();
                    }
                    if (fos != null) {
                        fos.close();
                    }
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
                fis = fis2;
            }
        } catch (Throwable th3) {
            th = th3;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void copyDirectory(File srcDir, File destDir) throws IOException {
        validateDirectoryRequirements(srcDir, destDir);
        File[] srcFiles = srcDir.listFiles();
        if (srcFiles == null) {
            throw new IOException("Failed to list files from " + srcDir);
        }
        if (!destDir.exists()) {
            if (!destDir.mkdirs() && !destDir.isDirectory()) {
                throw new IOException("Failed to create directory " + destDir);
            }
        } else if (!destDir.isDirectory()) {
            throw new IOException(destDir + " exists but it's not a directory");
        }
        for (File srcFile : srcFiles) {
            File destFile = new File(destDir, srcFile.getName());
            if (srcFile.isFile()) {
                copyFile(srcFile, destFile);
            } else {
                copyDirectory(srcFile, destFile);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void deleteFile(File file) {
        File[] files;
        if (file.exists()) {
            if (file.isDirectory() && (files = file.listFiles()) != null) {
                for (File file2 : files) {
                    deleteFile(file2);
                }
            }
            if (!file.delete()) {
                Localytics.Log.w(String.format("Delete %s failed.", file));
            }
        }
    }

    private static void validateFileRequirements(File src, File dest) throws IOException {
        if (src == null) {
            throw new NullPointerException("Source file cannot be null");
        }
        if (dest == null) {
            throw new NullPointerException("Dest file cannot be null");
        }
        if (!src.exists()) {
            throw new FileNotFoundException("Source file " + src + " doesn't exist");
        }
    }

    private static void validateDirectoryRequirements(File srcDir, File destDir) throws IOException {
        validateFileRequirements(srcDir, destDir);
        if (!srcDir.isDirectory()) {
            throw new IOException("Source file " + srcDir + " exists but it's not a directory");
        }
        if (srcDir.getCanonicalPath().equals(destDir.getCanonicalPath())) {
            throw new IOException("Can't copy because source " + srcDir + " and dest " + destDir + " directory are same");
        }
        if (destDir.getCanonicalPath().startsWith(srcDir.getCanonicalPath())) {
            throw new IOException("Can't copy the source " + srcDir + " into itself");
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int dpToPx(int dp, Context context) {
        return Math.round(dp * context.getResources().getDisplayMetrics().density);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static <T, K> void map(Collection<T> from, Collection<K> to, Mapper<T, K> mapper) {
        for (T t : from) {
            to.add(mapper.transform(t));
        }
    }
}
