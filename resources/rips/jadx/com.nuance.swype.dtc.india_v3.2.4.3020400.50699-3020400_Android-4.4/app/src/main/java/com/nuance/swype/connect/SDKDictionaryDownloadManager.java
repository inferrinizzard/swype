package com.nuance.swype.connect;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.SparseArray;
import com.nuance.android.compat.ActivityManagerCompat;
import com.nuance.android.compat.NotificationCompat;
import com.nuance.connect.common.Integers;
import com.nuance.swype.input.CategoryDBList;
import com.nuance.swype.input.InputMethods;
import com.nuance.swype.input.R;
import com.nuance.swype.input.settings.ChinesePrefsDispatch;
import com.nuance.swype.util.LogManager;
import com.nuance.swypeconnect.ac.ACChineseDictionaryDownloadService;
import com.nuance.swypeconnect.ac.ACException;
import com.nuance.swypeconnect.ac.ACManager;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/* loaded from: classes.dex */
public class SDKDictionaryDownloadManager {
    private static final LogManager.Log log = LogManager.getLog("SDKDictionaryDownloadManager");
    private Context context;
    NotificationManager notificationManager;
    private final Map<String, SDKDictionaryDownloadFileCallback> dictionaryDownloadList = new ConcurrentHashMap();
    private final SparseArray<RefreshLanguageRunnable> refreshLanguageRunnables = new SparseArray<>();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class RefreshLanguageRunnable implements Runnable {
        private final Context context;
        private final int languageId;

        RefreshLanguageRunnable(Context context, int languageId) {
            this.context = context;
            this.languageId = languageId;
        }

        @Override // java.lang.Runnable
        public final void run() {
            try {
                new CategoryDBList(this.context).postInstallRefresh(this.languageId);
            } catch (Exception e) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class SDKDictionaryDownloadFileCallback implements ACChineseDictionaryDownloadService.ACChineseDictionaryDownloadFileCallback {
        private ACChineseDictionaryDownloadService.ACChineseDictionary dictionary;
        private final Set<SDKDownloadStatusCallback> listeners = new HashSet();
        private NotificationCompat.Builder notificationBuilder;
        private int progress;

        public SDKDictionaryDownloadFileCallback() {
            this.notificationBuilder = new NotificationCompat.Builder(SDKDictionaryDownloadManager.this.getContext());
            Resources res = SDKDictionaryDownloadManager.this.getContext().getApplicationContext().getResources();
            if (res != null) {
                PendingIntent resultPendingIntent = PendingIntent.getActivity(SDKDictionaryDownloadManager.this.context, 0, new Intent(), 134217728);
                this.notificationBuilder.setContentText(res.getString(R.string.accessibility_voice_input_wait)).setSmallIcon(R.drawable.swype_icon).setProgress(0, 0, true).setContentIntent(resultPendingIntent).setOngoing(true);
            }
        }

        public ACChineseDictionaryDownloadService.ACChineseDictionary getDictionary() {
            return this.dictionary;
        }

        public void setDictionary(ACChineseDictionaryDownloadService.ACChineseDictionary dictionary) {
            this.dictionary = dictionary;
            if (this.notificationBuilder != null) {
                if (dictionary == null || dictionary.getName() == null) {
                    SDKDictionaryDownloadManager.log.d("[SDKDownloadFileCallback]setLanguageId ", "unknown language");
                    if (SDKDictionaryDownloadManager.this.notificationManager != null) {
                        SDKDictionaryDownloadManager.this.notificationManager.cancel(dictionary.getId());
                        return;
                    }
                    return;
                }
                this.notificationBuilder.setContentTitle(dictionary.getName());
                SDKDictionaryDownloadManager.this.notificationManager.notify(dictionary.getId(), this.notificationBuilder.build());
            }
        }

        private String getName() {
            return this.dictionary != null ? this.dictionary.getKey() : "null";
        }

        @Override // com.nuance.swypeconnect.ac.ACChineseDictionaryDownloadService.ACChineseDictionaryDownloadFileCallback
        public void downloadStopped(int reasonCode) {
            SDKDictionaryDownloadManager.log.d("[SDKDownloadFileCallback]", "[", getName(), "] ", "downloadStopped:", Integer.valueOf(reasonCode));
            Set<SDKDownloadStatusCallback> callbacksArray = new HashSet<>();
            synchronized (this.listeners) {
                callbacksArray.addAll(this.listeners);
            }
            Iterator<SDKDownloadStatusCallback> it = callbacksArray.iterator();
            while (it.hasNext()) {
                it.next().downloadStopped(reasonCode);
            }
            if (SDKDictionaryDownloadManager.this.getContext().getApplicationContext().getResources() != null && this.notificationBuilder != null) {
                SDKDictionaryDownloadManager.this.notificationManager.cancel(this.dictionary.getId());
            }
        }

        @Override // com.nuance.swypeconnect.ac.ACChineseDictionaryDownloadService.ACChineseDictionaryDownloadFileCallback
        public void downloadStarted() {
            SDKDictionaryDownloadManager.log.d("[SDKDownloadFileCallback]", "[", getName(), "] ", "downloadStarted");
            Set<SDKDownloadStatusCallback> callbacksArray = new HashSet<>();
            synchronized (this.listeners) {
                callbacksArray.addAll(this.listeners);
            }
            Iterator<SDKDownloadStatusCallback> it = callbacksArray.iterator();
            while (it.hasNext()) {
                it.next().downloadStarted();
            }
            Resources res = SDKDictionaryDownloadManager.this.getContext().getApplicationContext().getResources();
            if (res != null && this.notificationBuilder != null) {
                this.notificationBuilder.setContentText(res.getString(R.string.startup_lang_download_status));
                SDKDictionaryDownloadManager.this.notificationManager.notify(this.dictionary.getId(), this.notificationBuilder.build());
            }
        }

        @Override // com.nuance.swypeconnect.ac.ACChineseDictionaryDownloadService.ACChineseDictionaryDownloadFileCallback
        public void downloadPercentage(int percent) {
            SDKDictionaryDownloadManager.log.d("[SDKDownloadFileCallback]", "[", getName(), "] ", "downloadPercent:", Integer.valueOf(percent));
            Set<SDKDownloadStatusCallback> callbacksArray = new HashSet<>();
            synchronized (this.listeners) {
                callbacksArray.addAll(this.listeners);
            }
            Iterator<SDKDownloadStatusCallback> it = callbacksArray.iterator();
            while (it.hasNext()) {
                it.next().downloadPercentage(percent);
            }
            if (percent != this.progress) {
                this.progress = percent;
                if (this.notificationBuilder != null && !ActivityManagerCompat.isUserAMonkey()) {
                    this.notificationBuilder.setProgress(100, percent, false);
                    SDKDictionaryDownloadManager.this.notificationManager.notify(this.dictionary.getId(), this.notificationBuilder.build());
                }
            }
        }

        @Override // com.nuance.swypeconnect.ac.ACChineseDictionaryDownloadService.ACChineseDictionaryDownloadFileCallback
        public void downloadFailed(int reasonCode) {
            SDKDictionaryDownloadManager.log.d("[SDKDownloadFileCallback]", "[", getName(), "] ", "downloadFailed:", Integer.valueOf(reasonCode));
            Set<SDKDownloadStatusCallback> callbacksArray = new HashSet<>();
            synchronized (this.listeners) {
                callbacksArray.addAll(this.listeners);
            }
            Iterator<SDKDownloadStatusCallback> it = callbacksArray.iterator();
            while (it.hasNext()) {
                it.next().downloadFailed(reasonCode);
            }
            Resources res = SDKDictionaryDownloadManager.this.getContext().getApplicationContext().getResources();
            if (res != null && this.notificationBuilder != null) {
                this.notificationBuilder.setProgress(0, 0, false);
                this.notificationBuilder.setAutoCancel(true);
                this.notificationBuilder.setContentText(res.getString(R.string.accessibility_setting_fail_installation));
                SDKDictionaryDownloadManager.this.notificationManager.notify(this.dictionary.getId(), this.notificationBuilder.build());
            }
        }

        @Override // com.nuance.swypeconnect.ac.ACChineseDictionaryDownloadService.ACChineseDictionaryDownloadFileCallback
        public boolean downloadComplete(File file) {
            SDKDictionaryDownloadManager.log.d("[SDKDownloadFileCallback]", "[", getName(), "] ", "downloadComplete");
            Set<SDKDownloadStatusCallback> callbacksArray = new HashSet<>();
            synchronized (this.listeners) {
                callbacksArray.addAll(this.listeners);
            }
            Iterator<SDKDownloadStatusCallback> it = callbacksArray.iterator();
            while (it.hasNext()) {
                it.next().downloadComplete();
            }
            SDKDictionaryDownloadManager mgr = Connect.from(SDKDictionaryDownloadManager.this.context).getDictionaryDownloadManager();
            new InstallTask(this.dictionary, SDKDictionaryDownloadManager.this.getContext(), mgr).execute(file.getPath());
            Resources res = SDKDictionaryDownloadManager.this.getContext().getApplicationContext().getResources();
            if (res != null && this.notificationBuilder != null) {
                this.notificationBuilder.setProgress(0, 0, true);
                this.notificationBuilder.setContentText(res.getString(R.string.accessibility_setting_installing));
                SDKDictionaryDownloadManager.this.notificationManager.notify(this.dictionary.getId(), this.notificationBuilder.build());
            }
            return true;
        }

        public void addListener(SDKDownloadStatusCallback listener) {
            this.listeners.add(listener);
        }

        public void removeListener(SDKDownloadStatusCallback listener) {
            this.listeners.remove(listener);
        }

        void installed() {
            Set<SDKDownloadStatusCallback> callbacksArray = new HashSet<>();
            synchronized (this.listeners) {
                callbacksArray.addAll(this.listeners);
            }
            Iterator<SDKDownloadStatusCallback> it = callbacksArray.iterator();
            while (it.hasNext()) {
                it.next().downloadInstalled();
            }
            Resources res = SDKDictionaryDownloadManager.this.getContext().getApplicationContext().getResources();
            if (res != null && this.notificationBuilder != null) {
                Intent resultIntent = new Intent(SDKDictionaryDownloadManager.this.context, (Class<?>) ChinesePrefsDispatch.class).setFlags(268435456);
                PendingIntent resultPendingIntent = PendingIntent.getActivity(SDKDictionaryDownloadManager.this.context, 0, resultIntent, 134217728);
                this.notificationBuilder.setProgress(0, 0, false).setContentText(res.getString(R.string.pref_download_language_installed)).setAutoCancel(true).setOngoing(false).setContentIntent(resultPendingIntent);
                SDKDictionaryDownloadManager.this.notificationManager.notify(this.dictionary.getId(), this.notificationBuilder.build());
            }
        }
    }

    public SDKDictionaryDownloadManager(Context context, ACManager acManager) {
        this.context = context;
        this.notificationManager = (NotificationManager) context.getSystemService("notification");
    }

    public Context getContext() {
        return this.context;
    }

    public void dictionaryDownload(ACChineseDictionaryDownloadService.ACChineseDictionary dictionary, SDKDownloadStatusCallback statusCallback) {
        dictionaryDownload(dictionary);
        registerDictionaryDownloadListener(dictionary.getKey(), statusCallback);
    }

    public void dictionaryDownload(ACChineseDictionaryDownloadService.ACChineseDictionary dictionary) {
        if (dictionary == null) {
            log.e("dictionaryDownload null");
            return;
        }
        log.d("dictionaryDownload(", dictionary.getKey(), ")");
        synchronized (this.dictionaryDownloadList) {
            if (this.dictionaryDownloadList.get(dictionary.getKey()) == null) {
                log.d("dictionaryDownload(", dictionary, ") adding download to list");
                SDKDictionaryDownloadFileCallback dictionaryDownloadCallback = new SDKDictionaryDownloadFileCallback();
                dictionaryDownloadCallback.setDictionary(dictionary);
                ACChineseDictionaryDownloadService chineseService = Connect.from(this.context).getChineseDictionaryDownloadService();
                if (chineseService != null) {
                    try {
                        chineseService.downloadDictionary(dictionary, dictionaryDownloadCallback);
                        this.dictionaryDownloadList.put(dictionary.getKey(), dictionaryDownloadCallback);
                    } catch (ACException e) {
                        log.e("Error downloading chinese dictionary: " + e.getMessage());
                    }
                }
            } else {
                log.d("dictionaryDownload(", dictionary, ") dictionary already in download list, ignoring.");
            }
        }
    }

    public boolean dictionaryCancel(ACChineseDictionaryDownloadService.ACChineseDictionary dictionary) {
        ACChineseDictionaryDownloadService chineseService;
        if (dictionary == null) {
            log.e("dictionaryCancel dictionary is null");
        } else {
            synchronized (this.dictionaryDownloadList) {
                SDKDictionaryDownloadFileCallback downloadCallback = this.dictionaryDownloadList.get(dictionary.getKey());
                this.dictionaryDownloadList.remove(dictionary.getKey());
                if (downloadCallback != null && (chineseService = Connect.from(this.context).getChineseDictionaryDownloadService()) != null) {
                    try {
                        chineseService.cancelDownload(dictionary);
                    } catch (ACException e) {
                        log.e("Error canceling chinese dictionary: " + e.getMessage());
                    }
                }
            }
        }
        return false;
    }

    public void dictionaryInstallCompleted(ACChineseDictionaryDownloadService.ACChineseDictionary dictionary) {
        if (dictionary == null) {
            log.e("dictionaryInstallCompleted dictionary is null");
            return;
        }
        SDKDictionaryDownloadFileCallback callback = this.dictionaryDownloadList.get(dictionary.getKey());
        if (callback != null) {
            callback.installed();
        }
        synchronized (this.dictionaryDownloadList) {
            this.dictionaryDownloadList.remove(dictionary.getKey());
        }
    }

    public void dictionaryInstallFailed(ACChineseDictionaryDownloadService.ACChineseDictionary dictionary) {
        if (dictionary == null) {
            log.e("dictionaryInstallFailed dictionary is null");
            return;
        }
        synchronized (this.dictionaryDownloadList) {
            this.dictionaryDownloadList.remove(dictionary.getKey());
        }
    }

    public boolean registerDictionaryDownloadListener(String dictionaryKey, SDKDownloadStatusCallback statusCallback) {
        boolean z;
        synchronized (this.dictionaryDownloadList) {
            SDKDictionaryDownloadFileCallback dictionaryDownloadCallback = this.dictionaryDownloadList.get(dictionaryKey);
            if (dictionaryDownloadCallback != null) {
                dictionaryDownloadCallback.addListener(statusCallback);
                z = true;
            } else {
                z = false;
            }
        }
        return z;
    }

    public boolean unregisterDictionaryDownloadListener(String dictionaryKey, SDKDownloadStatusCallback statusCallback) {
        boolean z;
        synchronized (this.dictionaryDownloadList) {
            SDKDictionaryDownloadFileCallback dictionaryDownloadCallback = this.dictionaryDownloadList.get(dictionaryKey);
            if (dictionaryDownloadCallback != null) {
                dictionaryDownloadCallback.removeListener(statusCallback);
                z = true;
            } else {
                z = false;
            }
        }
        return z;
    }

    public List<String> getDictionaryDownloadList() {
        List<String> list = new ArrayList<>();
        synchronized (this.dictionaryDownloadList) {
            for (Map.Entry<String, SDKDictionaryDownloadFileCallback> entry : this.dictionaryDownloadList.entrySet()) {
                list.add(entry.getKey());
            }
        }
        log.d("getDictionaryDownloadList count=", Integer.valueOf(list.size()));
        return list;
    }

    /* loaded from: classes.dex */
    private static class InstallTask extends AsyncTask<String, Void, Integer> {
        private final Context context;
        private final ACChineseDictionaryDownloadService.ACChineseDictionary dictionary;
        SDKDictionaryDownloadManager manager;

        InstallTask(ACChineseDictionaryDownloadService.ACChineseDictionary dictionary, Context context, SDKDictionaryDownloadManager manager) {
            this.context = context;
            this.manager = manager;
            this.dictionary = dictionary;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Integer doInBackground(String... params) {
            String filePath = params[0];
            try {
                new CategoryDBList(this.context).installDownloadedCategoryDBFile(filePath, this.dictionary.getKey(), this.dictionary.getId(), this.dictionary.getLanguage());
                return Integer.valueOf(Integers.STATUS_SUCCESS);
            } catch (Exception e) {
                return 0;
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Integer result) {
            super.onPostExecute((InstallTask) result);
            if (result.intValue() == Integer.MIN_VALUE) {
                RefreshLanguageRunnable r = (RefreshLanguageRunnable) this.manager.refreshLanguageRunnables.get(this.dictionary.getLanguage());
                if (r == null) {
                    r = new RefreshLanguageRunnable(this.context, this.dictionary.getLanguage());
                    this.manager.refreshLanguageRunnables.put(this.dictionary.getLanguage(), r);
                }
                Handler h = new Handler();
                h.removeCallbacks(r);
                h.postDelayed(r, 1000L);
                this.manager.dictionaryInstallCompleted(this.dictionary);
                return;
            }
            this.manager.dictionaryInstallFailed(this.dictionary);
        }
    }

    public boolean registerDictionaryListCallback(ACChineseDictionaryDownloadService.ACChineseDictionaryDownloadListCallback callback, boolean sendNow) {
        ACChineseDictionaryDownloadService chineseService = Connect.from(this.context).getChineseDictionaryDownloadService();
        if (chineseService == null) {
            return false;
        }
        chineseService.registerCallback(callback, sendNow);
        return true;
    }

    public void unregisterDictionaryListCallback(ACChineseDictionaryDownloadService.ACChineseDictionaryDownloadListCallback callback) {
        ACChineseDictionaryDownloadService chineseService = Connect.from(this.context).getChineseDictionaryDownloadService();
        if (chineseService != null) {
            chineseService.unregisterCallback(callback);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void upgrade() {
        ACChineseDictionaryDownloadService chineseService = Connect.from(this.context).getChineseDictionaryDownloadService();
        if (chineseService == null) {
            log.e("Failed to upgrade chinese dictionaries: service not available");
            return;
        }
        List<ACChineseDictionaryDownloadService.ACChineseDictionary> totalDictionaries = new ArrayList<>();
        totalDictionaries.addAll(chineseService.getAvailableDictionaries());
        totalDictionaries.addAll(chineseService.getDownloadedDictionaries());
        totalDictionaries.addAll(chineseService.getUpdatableDictionaries());
        CategoryDBList cdbList = new CategoryDBList(this.context);
        InputMethods im = InputMethods.from(this.context);
        for (Map.Entry<String, List<String>> entry : cdbList.getAvailableCatDbList().entrySet()) {
            for (String catInfo : entry.getValue()) {
                int catId = cdbList.getFileId(catInfo);
                ACChineseDictionaryDownloadService.ACChineseDictionary found = null;
                Iterator<ACChineseDictionaryDownloadService.ACChineseDictionary> it = totalDictionaries.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    ACChineseDictionaryDownloadService.ACChineseDictionary d = it.next();
                    if (d.getId() == catId) {
                        log.d("Upgrade Dictionaries: Found=", d);
                        found = d;
                        break;
                    }
                }
                if (found != null) {
                    if (chineseService.getAvailableDictionaries().contains(found)) {
                        log.d("Upgrade Dictionaries: Redownloading=", found);
                        dictionaryDownload(found);
                    }
                } else {
                    log.d("Upgrade Dictionaries: Not found.  Removing=", catInfo);
                    InputMethods.Language lang = im.findLanguageFromName(entry.getKey());
                    cdbList.uninstallDownloadedCategoryDB(cdbList.getFileName(catInfo), cdbList.getFileId(catInfo), lang.mCoreLanguageId);
                }
            }
        }
    }
}
