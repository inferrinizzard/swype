package com.nuance.connect.util;

import com.nuance.connect.util.Logger;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Scanner;

/* loaded from: classes.dex */
public class FileUtils {
    public static final int BUFFER_SIZE = 1024;

    /* loaded from: classes.dex */
    public static abstract class CountingIterator<E> implements Iterator<E> {
        private int count = 0;

        public int getCount() {
            return this.count;
        }

        @Override // java.util.Iterator
        public E next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            this.count++;
            return null;
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:23:0x0066, code lost:            if (r0 != null) goto L20;     */
    /* JADX WARN: Code restructure failed: missing block: B:24:0x004e, code lost:            r0.close();     */
    /* JADX WARN: Code restructure failed: missing block: B:29:0x004c, code lost:            if (r0 == null) goto L21;     */
    /* JADX WARN: Removed duplicated region for block: B:34:0x0055  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static java.util.List<java.lang.String> convertFileToList(java.lang.String r6, boolean r7) {
        /*
            com.nuance.connect.util.Logger$LoggerType r0 = com.nuance.connect.util.Logger.LoggerType.OEM
            com.nuance.connect.util.Logger$Log r0 = com.nuance.connect.util.Logger.getLog(r0)
            java.lang.String r1 = "convertFileToList: "
            r0.w(r1, r6)
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            if (r6 == 0) goto L51
            java.io.File r3 = new java.io.File
            r3.<init>(r6)
            boolean r0 = r3.canRead()
            if (r0 == 0) goto L59
            r1 = 0
            java.util.Scanner r0 = new java.util.Scanner     // Catch: java.lang.Throwable -> L52 java.io.FileNotFoundException -> L6e
            java.lang.String r4 = "UTF-8"
            r0.<init>(r3, r4)     // Catch: java.lang.Throwable -> L52 java.io.FileNotFoundException -> L6e
        L27:
            boolean r1 = r0.hasNextLine()     // Catch: java.io.FileNotFoundException -> L3f java.lang.Throwable -> L69
            if (r1 == 0) goto L66
            java.lang.String r1 = r0.nextLine()     // Catch: java.io.FileNotFoundException -> L3f java.lang.Throwable -> L69
            if (r1 == 0) goto L66
            if (r7 == 0) goto L3b
            boolean r3 = r2.contains(r1)     // Catch: java.io.FileNotFoundException -> L3f java.lang.Throwable -> L69
            if (r3 != 0) goto L27
        L3b:
            r2.add(r1)     // Catch: java.io.FileNotFoundException -> L3f java.lang.Throwable -> L69
            goto L27
        L3f:
            r1 = move-exception
        L40:
            com.nuance.connect.util.Logger$LoggerType r1 = com.nuance.connect.util.Logger.LoggerType.OEM     // Catch: java.lang.Throwable -> L69
            com.nuance.connect.util.Logger$Log r1 = com.nuance.connect.util.Logger.getLog(r1)     // Catch: java.lang.Throwable -> L69
            java.lang.String r3 = "Error Processing File: "
            r1.e(r3, r6)     // Catch: java.lang.Throwable -> L69
            if (r0 == 0) goto L51
        L4e:
            r0.close()
        L51:
            return r2
        L52:
            r0 = move-exception
        L53:
            if (r1 == 0) goto L58
            r1.close()
        L58:
            throw r0
        L59:
            com.nuance.connect.util.Logger$LoggerType r0 = com.nuance.connect.util.Logger.LoggerType.OEM
            com.nuance.connect.util.Logger$Log r0 = com.nuance.connect.util.Logger.getLog(r0)
            java.lang.String r1 = "Error(2) Processing File: "
            r0.e(r1, r6)
            goto L51
        L66:
            if (r0 == 0) goto L51
            goto L4e
        L69:
            r1 = move-exception
            r5 = r1
            r1 = r0
            r0 = r5
            goto L53
        L6e:
            r0 = move-exception
            r0 = r1
            goto L40
        */
        throw new UnsupportedOperationException("Method not decompiled: com.nuance.connect.util.FileUtils.convertFileToList(java.lang.String, boolean):java.util.List");
    }

    public static void copy(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] bArr = new byte[1024];
        while (true) {
            int read = inputStream.read(bArr);
            if (read == -1) {
                return;
            } else {
                outputStream.write(bArr, 0, read);
            }
        }
    }

    public static boolean copyFile(File file, File file2) {
        FileInputStream fileInputStream;
        FileOutputStream fileOutputStream;
        FileOutputStream fileOutputStream2 = null;
        try {
            try {
                fileOutputStream = new FileOutputStream(file2);
                try {
                    fileInputStream = new FileInputStream(file);
                } catch (Throwable th) {
                    th = th;
                    fileInputStream = null;
                    fileOutputStream2 = fileOutputStream;
                }
            } catch (Throwable th2) {
                th = th2;
                fileInputStream = null;
            }
            try {
                copy(fileInputStream, fileOutputStream);
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
                if (fileInputStream == null) {
                    return true;
                }
                fileInputStream.close();
                return true;
            } catch (Throwable th3) {
                th = th3;
                fileOutputStream2 = fileOutputStream;
                if (fileOutputStream2 != null) {
                    fileOutputStream2.close();
                }
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                throw th;
            }
        } catch (FileNotFoundException e) {
            return false;
        } catch (IOException e2) {
            return false;
        }
    }

    public static boolean copyFile(String str, String str2) {
        return copyFile(new File(str), new File(str2));
    }

    public static boolean deleteFile(String str) {
        if (str == null || str.length() <= 0) {
            return true;
        }
        try {
            File file = new File(str);
            if (file.delete()) {
                return true;
            }
            Logger.getLog(Logger.LoggerType.OEM).e("Unable to delete file: ", str);
            return !file.exists();
        } catch (Exception e) {
            Logger.getLog(Logger.LoggerType.OEM).e("Unable to delete file: ", str);
            return false;
        }
    }

    public static int getNumberOfLines(File file) throws IOException {
        LineNumberReader lineNumberReader;
        try {
            lineNumberReader = new LineNumberReader(new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8")));
            try {
                if (lineNumberReader.skip(Long.MAX_VALUE) == Long.MAX_VALUE) {
                    Logger.getLog(Logger.LoggerType.OEM).w("warning: getNumberOfLines may be reporting incorrect value due to file size.");
                }
                int lineNumber = lineNumberReader.getLineNumber();
                if (lineNumberReader != null) {
                    lineNumberReader.close();
                }
                return lineNumber;
            } catch (Throwable th) {
                th = th;
                if (lineNumberReader != null) {
                    lineNumberReader.close();
                }
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
            lineNumberReader = null;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:39:0x007d  */
    /* JADX WARN: Removed duplicated region for block: B:42:0x0082 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static void persistListToFile(java.util.List<java.lang.String> r6, java.lang.String r7) {
        /*
            r0 = 0
            java.io.File r2 = new java.io.File
            r2.<init>(r7)
            if (r6 == 0) goto Le
            boolean r1 = r6.isEmpty()
            if (r1 == 0) goto L1b
        Le:
            com.nuance.connect.util.Logger$LoggerType r0 = com.nuance.connect.util.Logger.LoggerType.DEVELOPER
            com.nuance.connect.util.Logger$Log r0 = com.nuance.connect.util.Logger.getLog(r0)
            java.lang.String r1 = "Nothing to save: "
            r0.d(r1, r7)
        L1a:
            return
        L1b:
            java.io.FileOutputStream r1 = new java.io.FileOutputStream     // Catch: java.io.UnsupportedEncodingException -> L5d java.lang.Throwable -> L76 java.io.FileNotFoundException -> Laa
            r1.<init>(r2)     // Catch: java.io.UnsupportedEncodingException -> L5d java.lang.Throwable -> L76 java.io.FileNotFoundException -> Laa
            java.io.PrintWriter r2 = new java.io.PrintWriter     // Catch: java.lang.Throwable -> L93 java.io.UnsupportedEncodingException -> La5 java.io.FileNotFoundException -> Lad
            java.io.OutputStreamWriter r3 = new java.io.OutputStreamWriter     // Catch: java.lang.Throwable -> L93 java.io.UnsupportedEncodingException -> La5 java.io.FileNotFoundException -> Lad
            java.lang.String r4 = "UTF-8"
            r3.<init>(r1, r4)     // Catch: java.lang.Throwable -> L93 java.io.UnsupportedEncodingException -> La5 java.io.FileNotFoundException -> Lad
            r2.<init>(r3)     // Catch: java.lang.Throwable -> L93 java.io.UnsupportedEncodingException -> La5 java.io.FileNotFoundException -> Lad
            java.util.Iterator r3 = r6.iterator()     // Catch: java.io.FileNotFoundException -> L41 java.lang.Throwable -> L98 java.io.UnsupportedEncodingException -> La7
        L31:
            boolean r0 = r3.hasNext()     // Catch: java.io.FileNotFoundException -> L41 java.lang.Throwable -> L98 java.io.UnsupportedEncodingException -> La7
            if (r0 == 0) goto L88
            java.lang.Object r0 = r3.next()     // Catch: java.io.FileNotFoundException -> L41 java.lang.Throwable -> L98 java.io.UnsupportedEncodingException -> La7
            java.lang.String r0 = (java.lang.String) r0     // Catch: java.io.FileNotFoundException -> L41 java.lang.Throwable -> L98 java.io.UnsupportedEncodingException -> La7
            r2.println(r0)     // Catch: java.io.FileNotFoundException -> L41 java.lang.Throwable -> L98 java.io.UnsupportedEncodingException -> La7
            goto L31
        L41:
            r0 = move-exception
            r0 = r1
            r1 = r2
        L44:
            com.nuance.connect.util.Logger$LoggerType r2 = com.nuance.connect.util.Logger.LoggerType.OEM     // Catch: java.lang.Throwable -> L9a
            com.nuance.connect.util.Logger$Log r2 = com.nuance.connect.util.Logger.getLog(r2)     // Catch: java.lang.Throwable -> L9a
            java.lang.String r3 = "Could not persist to file: "
            r2.d(r3, r7)     // Catch: java.lang.Throwable -> L9a
            if (r1 == 0) goto L55
            r1.close()
        L55:
            if (r0 == 0) goto L1a
            r0.close()     // Catch: java.io.IOException -> L5b
            goto L1a
        L5b:
            r0 = move-exception
            goto L1a
        L5d:
            r1 = move-exception
            r1 = r0
        L5f:
            com.nuance.connect.util.Logger$LoggerType r2 = com.nuance.connect.util.Logger.LoggerType.OEM     // Catch: java.lang.Throwable -> La0
            com.nuance.connect.util.Logger$Log r2 = com.nuance.connect.util.Logger.getLog(r2)     // Catch: java.lang.Throwable -> La0
            java.lang.String r3 = "Encoding error in persist to file: "
            r2.d(r3, r7)     // Catch: java.lang.Throwable -> La0
            if (r0 == 0) goto L70
            r0.close()
        L70:
            if (r1 == 0) goto L1a
            r1.close()     // Catch: java.io.IOException -> L5b
            goto L1a
        L76:
            r1 = move-exception
            r2 = r0
            r5 = r0
            r0 = r1
            r1 = r5
        L7b:
            if (r2 == 0) goto L80
            r2.close()
        L80:
            if (r1 == 0) goto L85
            r1.close()     // Catch: java.io.IOException -> L86
        L85:
            throw r0
        L86:
            r1 = move-exception
            goto L85
        L88:
            if (r2 == 0) goto L8d
            r2.close()
        L8d:
            if (r1 == 0) goto L1a
            r1.close()     // Catch: java.io.IOException -> L5b
            goto L1a
        L93:
            r2 = move-exception
            r5 = r2
            r2 = r0
            r0 = r5
            goto L7b
        L98:
            r0 = move-exception
            goto L7b
        L9a:
            r2 = move-exception
            r5 = r2
            r2 = r1
            r1 = r0
            r0 = r5
            goto L7b
        La0:
            r2 = move-exception
            r5 = r2
            r2 = r0
            r0 = r5
            goto L7b
        La5:
            r2 = move-exception
            goto L5f
        La7:
            r0 = move-exception
            r0 = r2
            goto L5f
        Laa:
            r1 = move-exception
            r1 = r0
            goto L44
        Lad:
            r2 = move-exception
            r5 = r1
            r1 = r0
            r0 = r5
            goto L44
        */
        throw new UnsupportedOperationException("Method not decompiled: com.nuance.connect.util.FileUtils.persistListToFile(java.util.List, java.lang.String):void");
    }

    public static CountingIterator<String> streamFile(final String str, final boolean z) {
        Logger.getLog(Logger.LoggerType.OEM).w("streamFile: ", str);
        if (str != null) {
            File file = new File(str);
            if (file.canRead()) {
                try {
                    final Scanner scanner = new Scanner(file, "UTF-8");
                    return new CountingIterator<String>() { // from class: com.nuance.connect.util.FileUtils.1
                        boolean isClosed = false;

                        @Override // java.util.Iterator
                        public final boolean hasNext() {
                            return !this.isClosed && scanner.hasNext();
                        }

                        @Override // com.nuance.connect.util.FileUtils.CountingIterator, java.util.Iterator
                        public final String next() {
                            super.next();
                            try {
                                return scanner.nextLine();
                            } finally {
                                if (!scanner.hasNextLine()) {
                                    this.isClosed = true;
                                    scanner.close();
                                    if (z) {
                                        FileUtils.deleteFile(str);
                                    }
                                }
                            }
                        }

                        @Override // java.util.Iterator
                        public final void remove() {
                            Logger.getLog(Logger.LoggerType.OEM).e("streamFile: ", str, "; remove not supported");
                        }
                    };
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                Logger.getLog(Logger.LoggerType.OEM).e("Error(2) streamFile: ", str);
            }
        }
        return null;
    }
}
