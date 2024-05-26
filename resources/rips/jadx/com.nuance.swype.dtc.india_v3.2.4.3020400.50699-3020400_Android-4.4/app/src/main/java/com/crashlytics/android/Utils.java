package com.crashlytics.android;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Comparator;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class Utils {
    public static void capFileCount(File directory, FilenameFilter filter, int maxAllowed, Comparator<File> fileComparator) {
        File[] sessionFiles = directory.listFiles(filter);
        if (sessionFiles != null && sessionFiles.length > maxAllowed) {
            Arrays.sort(sessionFiles, fileComparator);
            int i = sessionFiles.length;
            for (File file : sessionFiles) {
                if (i > maxAllowed) {
                    file.delete();
                    i--;
                } else {
                    return;
                }
            }
        }
    }
}
