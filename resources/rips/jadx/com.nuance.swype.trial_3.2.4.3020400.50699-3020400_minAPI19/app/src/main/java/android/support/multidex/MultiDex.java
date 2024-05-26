package android.support.multidex;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import dalvik.system.DexFile;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipFile;

/* loaded from: classes.dex */
public final class MultiDex {
    private static final String SECONDARY_FOLDER_NAME = "code_cache" + File.separator + "secondary-dexes";
    private static final Set<String> installedApk = new HashSet();
    private static final boolean IS_VM_MULTIDEX_CAPABLE = isVMMultidexCapable(System.getProperty("java.vm.version"));

    public static void install(Context context) {
        Log.i("MultiDex", "install");
        if (IS_VM_MULTIDEX_CAPABLE) {
            Log.i("MultiDex", "VM has multidex support, MultiDex support library is disabled.");
            return;
        }
        if (Build.VERSION.SDK_INT < 4) {
            throw new RuntimeException("Multi dex installation failed. SDK " + Build.VERSION.SDK_INT + " is unsupported. Min SDK version is 4.");
        }
        try {
            ApplicationInfo applicationInfo = getApplicationInfo(context);
            if (applicationInfo != null) {
                synchronized (installedApk) {
                    String apkPath = applicationInfo.sourceDir;
                    if (!installedApk.contains(apkPath)) {
                        installedApk.add(apkPath);
                        if (Build.VERSION.SDK_INT > 20) {
                            Log.w("MultiDex", "MultiDex is not guaranteed to work in SDK version " + Build.VERSION.SDK_INT + ": SDK version higher than 20 should be backed by runtime with built-in multidex capabilty but it's not the case here: java.vm.version=\"" + System.getProperty("java.vm.version") + "\"");
                        }
                        try {
                            ClassLoader loader = context.getClassLoader();
                            if (loader == null) {
                                Log.e("MultiDex", "Context class loader is null. Must be running in test mode. Skip patching.");
                                return;
                            }
                            try {
                                clearOldDexDir(context);
                            } catch (Throwable t) {
                                Log.w("MultiDex", "Something went wrong when trying to clear old MultiDex extraction, continuing without cleaning.", t);
                            }
                            File dexDir = new File(applicationInfo.dataDir, SECONDARY_FOLDER_NAME);
                            List<File> files = MultiDexExtractor.load(context, applicationInfo, dexDir, false);
                            if (checkValidZipFiles(files)) {
                                installSecondaryDexes(loader, dexDir, files);
                            } else {
                                Log.w("MultiDex", "Files were not valid zip files.  Forcing a reload.");
                                List<File> files2 = MultiDexExtractor.load(context, applicationInfo, dexDir, true);
                                if (checkValidZipFiles(files2)) {
                                    installSecondaryDexes(loader, dexDir, files2);
                                } else {
                                    throw new RuntimeException("Zip files were not valid.");
                                }
                            }
                            Log.i("MultiDex", "install done");
                        } catch (RuntimeException e) {
                            Log.w("MultiDex", "Failure while trying to obtain Context class loader. Must be running in test mode. Skip patching.", e);
                        }
                    }
                }
            }
        } catch (Exception e2) {
            Log.e("MultiDex", "Multidex installation failure", e2);
            throw new RuntimeException("Multi dex installation failed (" + e2.getMessage() + ").");
        }
    }

    private static ApplicationInfo getApplicationInfo(Context context) throws PackageManager.NameNotFoundException {
        try {
            PackageManager pm = context.getPackageManager();
            String packageName = context.getPackageName();
            if (pm == null || packageName == null) {
                return null;
            }
            return pm.getApplicationInfo(packageName, 128);
        } catch (RuntimeException e) {
            Log.w("MultiDex", "Failure while trying to obtain ApplicationInfo from Context. Must be running in test mode. Skip patching.", e);
            return null;
        }
    }

    private static boolean isVMMultidexCapable(String versionString) {
        boolean isMultidexCapable = false;
        if (versionString != null) {
            Matcher matcher = Pattern.compile("(\\d+)\\.(\\d+)(\\.\\d+)?").matcher(versionString);
            if (matcher.matches()) {
                try {
                    int major = Integer.parseInt(matcher.group(1));
                    int minor = Integer.parseInt(matcher.group(2));
                    isMultidexCapable = major > 2 || (major == 2 && minor > 0);
                } catch (NumberFormatException e) {
                }
            }
        }
        Log.i("MultiDex", "VM with version " + versionString + (isMultidexCapable ? " has multidex support" : " does not have multidex support"));
        return isMultidexCapable;
    }

    private static void installSecondaryDexes(ClassLoader loader, File dexDir, List<File> files) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, InvocationTargetException, NoSuchMethodException, IOException {
        IOException[] iOExceptionArr;
        if (!files.isEmpty()) {
            if (Build.VERSION.SDK_INT < 19) {
                if (Build.VERSION.SDK_INT >= 14) {
                    Object obj = findField(loader, "pathList").get(loader);
                    access$400(obj, "dexElements", (Object[]) findMethod(obj, "makeDexElements", ArrayList.class, File.class).invoke(obj, new ArrayList(files), dexDir));
                    return;
                }
                V4.install(loader, files);
                return;
            }
            Object obj2 = findField(loader, "pathList").get(loader);
            ArrayList arrayList = new ArrayList();
            access$400(obj2, "dexElements", (Object[]) findMethod(obj2, "makeDexElements", ArrayList.class, File.class, ArrayList.class).invoke(obj2, new ArrayList(files), dexDir, arrayList));
            if (arrayList.size() <= 0) {
                return;
            }
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                Log.w("MultiDex", "Exception in makeDexElement", (IOException) it.next());
            }
            Field findField = findField(loader, "dexElementsSuppressedExceptions");
            IOException[] iOExceptionArr2 = (IOException[]) findField.get(loader);
            if (iOExceptionArr2 == null) {
                iOExceptionArr = (IOException[]) arrayList.toArray(new IOException[arrayList.size()]);
            } else {
                IOException[] iOExceptionArr3 = new IOException[arrayList.size() + iOExceptionArr2.length];
                arrayList.toArray(iOExceptionArr3);
                System.arraycopy(iOExceptionArr2, 0, iOExceptionArr3, arrayList.size(), iOExceptionArr2.length);
                iOExceptionArr = iOExceptionArr3;
            }
            findField.set(loader, iOExceptionArr);
        }
    }

    private static boolean checkValidZipFiles(List<File> files) {
        Iterator i$ = files.iterator();
        while (i$.hasNext()) {
            if (!MultiDexExtractor.verifyZipFile(i$.next())) {
                return false;
            }
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Field findField(Object instance, String name) throws NoSuchFieldException {
        for (Class<?> clazz = instance.getClass(); clazz != null; clazz = clazz.getSuperclass()) {
            try {
                Field field = clazz.getDeclaredField(name);
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                return field;
            } catch (NoSuchFieldException e) {
            }
        }
        throw new NoSuchFieldException("Field " + name + " not found in " + instance.getClass());
    }

    private static Method findMethod(Object instance, String name, Class<?>... parameterTypes) throws NoSuchMethodException {
        for (Class<?> clazz = instance.getClass(); clazz != null; clazz = clazz.getSuperclass()) {
            try {
                Method method = clazz.getDeclaredMethod(name, parameterTypes);
                if (!method.isAccessible()) {
                    method.setAccessible(true);
                }
                return method;
            } catch (NoSuchMethodException e) {
            }
        }
        throw new NoSuchMethodException("Method " + name + " with parameters " + Arrays.asList(parameterTypes) + " not found in " + instance.getClass());
    }

    private static void clearOldDexDir(Context context) throws Exception {
        File dexDir = new File(context.getFilesDir(), "secondary-dexes");
        if (dexDir.isDirectory()) {
            Log.i("MultiDex", "Clearing old secondary dex dir (" + dexDir.getPath() + ").");
            File[] files = dexDir.listFiles();
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
            if (!dexDir.delete()) {
                Log.w("MultiDex", "Failed to delete secondary dex dir " + dexDir.getPath());
            } else {
                Log.i("MultiDex", "Deleted old secondary dex dir " + dexDir.getPath());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class V4 {
        static void install(ClassLoader loader, List<File> additionalClassPathEntries) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, IOException {
            int extraSize = additionalClassPathEntries.size();
            Field pathField = MultiDex.findField(loader, "path");
            StringBuilder path = new StringBuilder((String) pathField.get(loader));
            String[] extraPaths = new String[extraSize];
            File[] extraFiles = new File[extraSize];
            ZipFile[] extraZips = new ZipFile[extraSize];
            DexFile[] extraDexs = new DexFile[extraSize];
            ListIterator<File> iterator = additionalClassPathEntries.listIterator();
            while (iterator.hasNext()) {
                File additionalEntry = iterator.next();
                String entryPath = additionalEntry.getAbsolutePath();
                path.append(':').append(entryPath);
                int index = iterator.previousIndex();
                extraPaths[index] = entryPath;
                extraFiles[index] = additionalEntry;
                extraZips[index] = new ZipFile(additionalEntry);
                extraDexs[index] = DexFile.loadDex(entryPath, entryPath + ".dex", 0);
            }
            pathField.set(loader, path.toString());
            MultiDex.access$400(loader, "mPaths", extraPaths);
            MultiDex.access$400(loader, "mFiles", extraFiles);
            MultiDex.access$400(loader, "mZips", extraZips);
            MultiDex.access$400(loader, "mDexs", extraDexs);
        }
    }

    static /* synthetic */ void access$400(Object x0, String x1, Object[] x2) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field findField = findField(x0, x1);
        Object[] objArr = (Object[]) findField.get(x0);
        Object[] objArr2 = (Object[]) Array.newInstance(objArr.getClass().getComponentType(), objArr.length + x2.length);
        System.arraycopy(objArr, 0, objArr2, 0, objArr.length);
        System.arraycopy(x2, 0, objArr2, objArr.length, x2.length);
        findField.set(x0, objArr2);
    }
}
