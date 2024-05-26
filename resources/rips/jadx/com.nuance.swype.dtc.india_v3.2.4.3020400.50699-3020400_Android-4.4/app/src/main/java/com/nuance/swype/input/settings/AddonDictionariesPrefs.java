package com.nuance.swype.input.settings;

import android.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.nuance.android.util.WeakReferenceHandler;
import com.nuance.swype.connect.Connect;
import com.nuance.swype.connect.ConnectedStatus;
import com.nuance.swype.connect.SDKDictionaryDownloadManager;
import com.nuance.swype.connect.SDKDownloadStatusCallback;
import com.nuance.swype.input.CategoryDBList;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.InputMethods;
import com.nuance.swype.preference.ConnectionAwarePreferences;
import com.nuance.swype.preference.DialogPrefs;
import com.nuance.swype.stats.StatisticsManager;
import com.nuance.swype.util.LogManager;
import com.nuance.swypeconnect.ac.ACChineseDictionaryDownloadService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public abstract class AddonDictionariesPrefs extends DialogPrefs {
    static final /* synthetic */ boolean $assertionsDisabled;
    private static final String ADDON_DICTIONARIES_DISPLAY_NAME = "dictionary_addon_display_name";
    private static final String ADDON_DICTIONARIES_KEY = "dictionary_addon_key";
    private static final int MSG_HIDE_PROGRESSBAR = 102;
    private static final int MSG_SHOW_PROGRESSBAR = 101;
    private static Comparator<DownloadableDictionary> dictionaryNameComparator;
    private static final LogManager.Log log;
    private final Activity activity;
    private final ChineseDictionariesAdapter adapter;
    private final ACChineseDictionaryDownloadService chineseService;
    private final Connect connect;
    private final ConnectionAwarePreferences connectionPreferences;
    private final ConnectedStatus connectionStatus;
    private InputMethods.Language currentLanguage;
    private boolean isRunning;
    private ProgressBar progressBar;
    private Handler.Callback progessbarCallback = new Handler.Callback() { // from class: com.nuance.swype.input.settings.AddonDictionariesPrefs.1
        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message arg0) {
            switch (arg0.what) {
                case 101:
                    AddonDictionariesPrefs.this.progressBar.setVisibility(0);
                    return true;
                case 102:
                    AddonDictionariesPrefs.this.progressBar.setVisibility(4);
                    return true;
                default:
                    return false;
            }
        }
    };
    private Handler progressbarHandler = WeakReferenceHandler.create(this.progessbarCallback);
    private final ACChineseDictionaryDownloadService.ACChineseDictionaryDownloadListCallback chineseListCallback = new ACChineseDictionaryDownloadService.ACChineseDictionaryDownloadListCallback() { // from class: com.nuance.swype.input.settings.AddonDictionariesPrefs.2
        @Override // com.nuance.swypeconnect.ac.ACChineseDictionaryDownloadService.ACChineseDictionaryDownloadListCallback
        public void availableDictionaries(List<ACChineseDictionaryDownloadService.ACChineseDictionary> dictionaries) {
            if (dictionaries != null && dictionaries.size() > 0) {
                AddonDictionariesPrefs.this.displayTimeoutDialog = false;
            }
            if (AddonDictionariesPrefs.this.adapter != null) {
                AddonDictionariesPrefs.this.adapter.updateListData();
            }
        }

        @Override // com.nuance.swypeconnect.ac.ACChineseDictionaryDownloadService.ACChineseDictionaryDownloadListCallback
        public void updatableDictionaries(List<ACChineseDictionaryDownloadService.ACChineseDictionary> dictionaries) {
            if (dictionaries != null && dictionaries.size() > 0) {
                AddonDictionariesPrefs.this.displayTimeoutDialog = false;
            }
            if (AddonDictionariesPrefs.this.adapter != null) {
                AddonDictionariesPrefs.this.adapter.dataChanged();
            }
        }

        @Override // com.nuance.swypeconnect.ac.ACChineseDictionaryDownloadService.ACChineseDictionaryDownloadListCallback
        public void downloadedDictionaries(List<ACChineseDictionaryDownloadService.ACChineseDictionary> dictionaries) {
            if (dictionaries != null && dictionaries.size() > 0) {
                AddonDictionariesPrefs.this.displayTimeoutDialog = false;
            }
            if (AddonDictionariesPrefs.this.adapter != null) {
                AddonDictionariesPrefs.this.adapter.dataChanged();
            }
        }
    };
    private volatile boolean displayTimeoutDialog = true;
    private final Handler handler = new Handler();
    private Runnable timeoutRunnable = new Runnable() { // from class: com.nuance.swype.input.settings.AddonDictionariesPrefs.3
        @Override // java.lang.Runnable
        public void run() {
            if (AddonDictionariesPrefs.this.displayTimeoutDialog && !AddonDictionariesPrefs.this.activity.isFinishing() && AddonDictionariesPrefs.this.isRunning) {
                AddonDictionariesPrefs.this.showTimeoutDialog();
            }
        }
    };

    protected abstract void showConnectDialog();

    protected abstract void showRemoveDictionaryDialog(Bundle bundle);

    protected abstract void showTimeoutDialog();

    static {
        $assertionsDisabled = !AddonDictionariesPrefs.class.desiredAssertionStatus();
        log = LogManager.getLog("AddonDictionaries");
        dictionaryNameComparator = new Comparator<DownloadableDictionary>() { // from class: com.nuance.swype.input.settings.AddonDictionariesPrefs.9
            @Override // java.util.Comparator
            public final int compare(DownloadableDictionary arg0, DownloadableDictionary arg1) {
                return arg0.getDictionary().getName().compareTo(arg1.getDictionary().getName());
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AddonDictionariesPrefs(Activity activity) {
        this.activity = activity;
        createProgressBar(activity);
        this.connect = Connect.from(activity);
        this.connectionStatus = new ConnectedStatus(activity) { // from class: com.nuance.swype.input.settings.AddonDictionariesPrefs.4
            @Override // com.nuance.swype.connect.ConnectedStatus
            public void onConnectionChanged(boolean isConnected) {
                if (AddonDictionariesPrefs.this.adapter != null) {
                    AddonDictionariesPrefs.this.adapter.onConnectionChanged(AddonDictionariesPrefs.this.connectionStatus.isConnected());
                }
            }
        };
        this.connectionPreferences = new ConnectionAwarePreferences(activity) { // from class: com.nuance.swype.input.settings.AddonDictionariesPrefs.5
            @Override // com.nuance.swype.preference.ConnectionAwarePreferences
            public void showConnectDialog() {
                AddonDictionariesPrefs.this.showConnectDialog();
            }
        };
        this.chineseService = this.connect.getChineseDictionaryDownloadService();
        if (this.chineseService == null) {
            this.adapter = null;
            log.e("Unexpected failure when loading chinese addon dictionaries: Service is NULL!");
        } else {
            this.adapter = new ChineseDictionariesAdapter(activity, this.connect.getDictionaryDownloadManager(), this, this.progressbarHandler);
        }
        activity.setProgressBarIndeterminate(true);
    }

    public void onStart() {
        this.connectionStatus.register();
    }

    public void onResume() {
        this.isRunning = true;
        InputMethods inputMethods = InputMethods.from(this.activity);
        this.currentLanguage = inputMethods.getCurrentInputLanguage();
        if (this.connect != null && this.chineseService != null) {
            if (!this.chineseService.isDictionaryListAvailable()) {
                this.handler.removeCallbacks(this.timeoutRunnable);
                this.handler.postDelayed(this.timeoutRunnable, 30000L);
                this.progressbarHandler.removeMessages(101);
                this.progressbarHandler.sendMessageDelayed(this.progressbarHandler.obtainMessage(101), 50L);
            } else {
                this.progressbarHandler.removeMessages(102);
                this.progressbarHandler.sendMessageDelayed(this.progressbarHandler.obtainMessage(102), 50L);
            }
            this.chineseService.registerCallback(this.chineseListCallback, false);
        }
        if (this.adapter != null) {
            this.adapter.onConnectionChanged(this.connectionStatus.isConnected());
            this.adapter.updateListData();
        }
    }

    private ProgressBar createProgressBar(Activity activity) {
        FrameLayout rootContainer = (FrameLayout) activity.findViewById(R.id.content);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(-2, -2);
        lp.gravity = 17;
        this.progressBar = new ProgressBar(activity, null, R.attr.progressBarStyleLarge);
        this.progressBar.setVisibility(8);
        this.progressBar.setLayoutParams(lp);
        rootContainer.addView(this.progressBar);
        return this.progressBar;
    }

    public void onPause() {
        this.isRunning = false;
        this.handler.removeCallbacks(this.timeoutRunnable);
        if (this.chineseService != null) {
            this.chineseService.unregisterCallback(this.chineseListCallback);
        }
    }

    public void onStop() {
        this.connectionStatus.unregister();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Dialog timeoutDialog() {
        this.handler.removeCallbacks(this.timeoutRunnable);
        AlertDialog.Builder builder = new AlertDialog.Builder(this.activity);
        builder.setTitle(com.nuance.swype.input.R.string.startup_internet_connection);
        builder.setMessage(com.nuance.swype.input.R.string.startup_connection_error);
        builder.setIcon(com.nuance.swype.input.R.drawable.icon_settings_error);
        builder.setPositiveButton(this.activity.getResources().getString(com.nuance.swype.input.R.string.startup_close), new DialogInterface.OnClickListener() { // from class: com.nuance.swype.input.settings.AddonDictionariesPrefs.6
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface arg0, int arg1) {
                arg0.dismiss();
                AddonDictionariesPrefs.this.activity.finish();
            }
        });
        builder.setNegativeButton(this.activity.getResources().getString(com.nuance.swype.input.R.string.startup_retry), new DialogInterface.OnClickListener() { // from class: com.nuance.swype.input.settings.AddonDictionariesPrefs.7
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface arg0, int arg1) {
                AddonDictionariesPrefs.this.handler.postDelayed(AddonDictionariesPrefs.this.timeoutRunnable, 30000L);
            }
        });
        return builder.create();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Dialog createConnectDialog() {
        return this.connectionPreferences.getConnectDialog().create();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Dialog createRemoveDictionaryDialog(Bundle args) {
        if (!$assertionsDisabled && args == null) {
            throw new AssertionError();
        }
        final String key = args.getString(ADDON_DICTIONARIES_KEY);
        String dictionary = args.getString(ADDON_DICTIONARIES_DISPLAY_NAME);
        String msg = String.format(this.activity.getString(com.nuance.swype.input.R.string.pref_remove_languages_dialog_desc), dictionary);
        return new AlertDialog.Builder(this.activity).setTitle(com.nuance.swype.input.R.string.pref_remove_languages_dialog_title).setMessage(msg).setPositiveButton(com.nuance.swype.input.R.string.yes_button, new DialogInterface.OnClickListener() { // from class: com.nuance.swype.input.settings.AddonDictionariesPrefs.8
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int whichButton) {
                AddonDictionariesPrefs.this.removeDictionary(key);
            }
        }).setNegativeButton(com.nuance.swype.input.R.string.no_button, (DialogInterface.OnClickListener) null).create();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeDictionary(String dictKey) {
        if (this.chineseService != null) {
            ACChineseDictionaryDownloadService.ACChineseDictionary d = null;
            Iterator<ACChineseDictionaryDownloadService.ACChineseDictionary> it = this.chineseService.getDownloadedDictionaries().iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                ACChineseDictionaryDownloadService.ACChineseDictionary currentDict = it.next();
                if (currentDict.getKey().equals(dictKey)) {
                    d = currentDict;
                    break;
                }
            }
            if (d == null) {
                Iterator<ACChineseDictionaryDownloadService.ACChineseDictionary> it2 = this.chineseService.getUpdatableDictionaries().iterator();
                while (true) {
                    if (!it2.hasNext()) {
                        break;
                    }
                    ACChineseDictionaryDownloadService.ACChineseDictionary currentDict2 = it2.next();
                    if (currentDict2.getKey().equals(dictKey)) {
                        d = currentDict2;
                        break;
                    }
                }
            }
            if (d == null) {
                Iterator<ACChineseDictionaryDownloadService.ACChineseDictionary> it3 = this.chineseService.getAvailableDictionaries().iterator();
                while (true) {
                    if (!it3.hasNext()) {
                        break;
                    }
                    ACChineseDictionaryDownloadService.ACChineseDictionary currentDict3 = it3.next();
                    if (currentDict3.getKey().equals(dictKey)) {
                        d = currentDict3;
                        break;
                    }
                }
            }
            if (d != null) {
                this.chineseService.removeDictionary(d);
                new CategoryDBList(this.activity).uninstallDownloadedCategoryDB(dictKey, d.getId(), d.getLanguage());
                StatisticsManager.SessionStatsScribe sessionScribe = StatisticsManager.getSessionStatsScribe(this.activity);
                if (sessionScribe != null) {
                    sessionScribe.recordSettingsChange(ADDON_DICTIONARIES_KEY, d.getName() + " removed", "not removed");
                }
                if (this.adapter != null) {
                    this.adapter.removeDictionary(d);
                    return;
                }
                return;
            }
            log.e("Could not find dictionary to remove: " + dictKey);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public BaseAdapter getAdapter() {
        return this.adapter;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onListItemClick(ListView l, View v, int position, long id) {
        this.adapter.onListItemClick(l, v, position, id);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class ChineseDictionariesAdapter extends BaseAdapter {
        private static final int TYPE_ENTRY = 1;
        private static final int TYPE_HEADER = 0;
        private final CategoryDBList cdbList;
        private boolean connected;
        private SDKDictionaryDownloadManager downloadManager;
        private final Handler handler;
        private final AddonDictionariesPrefs parent;
        private List<String> categories = new ArrayList();
        private final Map<String, List<DownloadableDictionary>> dictionaries = new ConcurrentHashMap();
        private final Runnable dataChanged = new Runnable() { // from class: com.nuance.swype.input.settings.AddonDictionariesPrefs.ChineseDictionariesAdapter.1
            @Override // java.lang.Runnable
            public void run() {
                ChineseDictionariesAdapter.this.notifyDataSetChanged();
            }
        };
        private final Runnable listDataChanged = new Runnable() { // from class: com.nuance.swype.input.settings.AddonDictionariesPrefs.ChineseDictionariesAdapter.2
            @Override // java.lang.Runnable
            public void run() {
                ChineseDictionariesAdapter.this.updateData();
                ChineseDictionariesAdapter.this.notifyDataSetChanged();
            }
        };

        ChineseDictionariesAdapter(Context context, SDKDictionaryDownloadManager downloadManager, AddonDictionariesPrefs parent, Handler handler) {
            this.downloadManager = downloadManager;
            this.parent = parent;
            this.handler = handler;
            this.cdbList = new CategoryDBList(context);
            updateListData();
        }

        void dataChanged() {
            this.parent.runOnUIThread(this.dataChanged);
        }

        void updateListData() {
            this.parent.runOnUIThread(this.listDataChanged);
        }

        void removeDictionary(ACChineseDictionaryDownloadService.ACChineseDictionary d) {
            Iterator<Map.Entry<String, List<DownloadableDictionary>>> it = this.dictionaries.entrySet().iterator();
            while (it.hasNext()) {
                for (DownloadableDictionary dd : it.next().getValue()) {
                    if (dd.getDictionary().getKey().equals(d.getKey())) {
                        dd.setStatus(DownloadableDictionary.Status.AVAILABLE);
                        return;
                    }
                }
            }
        }

        synchronized void updateData() {
            Collection<? extends ACChineseDictionaryDownloadService.ACChineseDictionary> available = this.parent.chineseService.getAvailableDictionaries();
            Collection<? extends ACChineseDictionaryDownloadService.ACChineseDictionary> updatable = this.parent.chineseService.getUpdatableDictionaries();
            Collection<? extends ACChineseDictionaryDownloadService.ACChineseDictionary> downloaded = this.parent.chineseService.getDownloadedDictionaries();
            List<ACChineseDictionaryDownloadService.ACChineseDictionary> total = new ArrayList<>();
            total.addAll(available);
            total.addAll(updatable);
            total.addAll(downloaded);
            List<String> downloadingKeys = this.downloadManager.getDictionaryDownloadList();
            Set<DownloadableDictionary> totalDictionaries = new HashSet<>();
            for (ACChineseDictionaryDownloadService.ACChineseDictionary d : total) {
                if (this.parent.currentLanguage != null && d.getLanguage() == this.parent.currentLanguage.mCoreLanguageId && d.getCategory() != null && d.getName() != null) {
                    if (!this.categories.contains(d.getCategory())) {
                        this.categories.add(d.getCategory());
                    }
                    if (!this.dictionaries.containsKey(d.getCategory())) {
                        this.dictionaries.put(d.getCategory(), new ArrayList());
                    }
                    DownloadableDictionary dd = null;
                    List<DownloadableDictionary> list = this.dictionaries.get(d.getCategory());
                    Iterator<DownloadableDictionary> it = list.iterator();
                    while (true) {
                        if (!it.hasNext()) {
                            break;
                        }
                        DownloadableDictionary current = it.next();
                        if (current.getDictionary().getKey().equals(d.getKey())) {
                            dd = current;
                            break;
                        }
                    }
                    if (dd == null) {
                        DownloadableDictionary.Status status = DownloadableDictionary.Status.AVAILABLE;
                        if (this.cdbList.isCategoryInstalled(d.getId())) {
                            status = DownloadableDictionary.Status.INSTALLED;
                        } else if (downloadingKeys.contains(d.getKey())) {
                            status = DownloadableDictionary.Status.DOWNLOADING;
                        }
                        dd = new DownloadableDictionary(d, status, this);
                        if (status == DownloadableDictionary.Status.DOWNLOADING) {
                            this.downloadManager.registerDictionaryDownloadListener(d.getKey(), new DictionaryDownloadCallback(dd));
                        }
                        list.add(dd);
                    }
                    totalDictionaries.add(dd);
                }
            }
            for (Map.Entry<String, List<DownloadableDictionary>> entry : this.dictionaries.entrySet()) {
                entry.getValue().retainAll(totalDictionaries);
                if (entry.getValue().isEmpty()) {
                    this.dictionaries.remove(entry.getKey());
                }
            }
            Iterator<Map.Entry<String, List<DownloadableDictionary>>> it2 = this.dictionaries.entrySet().iterator();
            while (it2.hasNext()) {
                Collections.sort(it2.next().getValue(), AddonDictionariesPrefs.dictionaryNameComparator);
            }
            AddonDictionariesPrefs.log.d("updateData total=", Integer.valueOf(total.size()), " dictionaries=", Integer.valueOf(this.dictionaries.size()));
            Collections.sort(this.categories);
            if (total.size() == 0) {
                this.handler.removeMessages(101);
                this.handler.sendMessageDelayed(this.handler.obtainMessage(101), 50L);
            } else {
                this.handler.removeMessages(102);
                this.handler.sendMessageDelayed(this.handler.obtainMessage(102), 50L);
            }
        }

        @Override // android.widget.Adapter
        public int getCount() {
            int count = 0;
            for (Map.Entry<String, List<DownloadableDictionary>> d : this.dictionaries.entrySet()) {
                count = count + d.getValue().size() + 1;
            }
            return count;
        }

        @Override // android.widget.Adapter
        public Object getItem(int index) {
            if (index < 0) {
                AddonDictionariesPrefs.log.e("Too low; index=" + index);
                return null;
            }
            int count = 0;
            for (Map.Entry<String, List<DownloadableDictionary>> d : this.dictionaries.entrySet()) {
                if (count == index) {
                    return d.getKey();
                }
                int count2 = count + 1;
                if (index < d.getValue().size() + count2) {
                    return d.getValue().get(index - count2);
                }
                count = count2 + d.getValue().size();
            }
            AddonDictionariesPrefs.log.e("Too high; index=" + index);
            return null;
        }

        @Override // android.widget.Adapter
        public long getItemId(int index) {
            return index;
        }

        /* loaded from: classes.dex */
        private static class ViewHolder {
            public ImageView cancel;
            public ImageView icon;
            public ProgressBar progress;
            public TextView summary;
            public TextView title;

            private ViewHolder() {
            }
        }

        @Override // android.widget.Adapter
        @SuppressLint({"InflateParams"})
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            Object item = getItem(position);
            if (item instanceof DownloadableDictionary) {
                final DownloadableDictionary dictionary = (DownloadableDictionary) item;
                if (convertView == null || convertView.getTag() == null) {
                    convertView = this.parent.activity.getLayoutInflater().inflate(com.nuance.swype.input.R.layout.language_download_list_item, (ViewGroup) null);
                    if (IMEApplication.from(this.parent.activity).isScreenLayoutTablet() && (convertView.getPaddingLeft() <= 16 || convertView.getPaddingStart() <= 16)) {
                        int padding = (int) (this.parent.activity.getResources().getDisplayMetrics().density * 32.0f);
                        convertView.setPaddingRelative(padding, convertView.getTop(), padding, convertView.getBottom());
                    }
                    holder = new ViewHolder();
                    holder.icon = (ImageView) convertView.findViewById(com.nuance.swype.input.R.id.icon);
                    holder.icon.setImageResource(com.nuance.swype.input.R.drawable.icon_settings_error);
                    holder.title = (TextView) convertView.findViewById(com.nuance.swype.input.R.id.title);
                    holder.summary = (TextView) convertView.findViewById(com.nuance.swype.input.R.id.summary);
                    holder.progress = (ProgressBar) convertView.findViewById(com.nuance.swype.input.R.id.progressBar1);
                    holder.cancel = (ImageView) convertView.findViewById(com.nuance.swype.input.R.id.cancel);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                if (holder != null) {
                    if (holder.title != null) {
                        holder.title.setText(String.format("%s", dictionary.getDictionary().getName()));
                    }
                    switch (dictionary.getStatus()) {
                        case DOWNLOADING:
                            if (!this.connected) {
                                holder.icon.setVisibility(0);
                            } else {
                                holder.icon.setVisibility(8);
                            }
                            if (dictionary.getProgress() > 0) {
                                holder.progress.setProgress(dictionary.getProgress());
                                holder.progress.setMax(100);
                                holder.progress.setIndeterminate(false);
                            } else {
                                holder.progress.setIndeterminate(true);
                            }
                            holder.progress.setVisibility(0);
                            holder.cancel.setVisibility(0);
                            holder.cancel.setOnClickListener(new View.OnClickListener() { // from class: com.nuance.swype.input.settings.AddonDictionariesPrefs.ChineseDictionariesAdapter.3
                                @Override // android.view.View.OnClickListener
                                public void onClick(View v) {
                                    List<DownloadableDictionary> list = (List) ChineseDictionariesAdapter.this.dictionaries.get(dictionary.getDictionary().getCategory());
                                    if (list != null) {
                                        Iterator<DownloadableDictionary> it = list.iterator();
                                        while (true) {
                                            if (!it.hasNext()) {
                                                break;
                                            }
                                            DownloadableDictionary dd = it.next();
                                            if (dd.getDictionary().getKey().equals(dictionary.getDictionary().getKey())) {
                                                dd.setStatus(DownloadableDictionary.Status.AVAILABLE);
                                                break;
                                            }
                                        }
                                    }
                                    ChineseDictionariesAdapter.this.downloadManager.dictionaryCancel(dictionary.getDictionary());
                                }
                            });
                            holder.summary.setVisibility(8);
                            break;
                        case INSTALLED:
                            holder.icon.setVisibility(8);
                            holder.summary.setVisibility(0);
                            holder.cancel.setVisibility(8);
                            holder.progress.setVisibility(8);
                            holder.summary.setText(com.nuance.swype.input.R.string.pref_download_language_installed);
                            break;
                        case UPDATABLE:
                            holder.icon.setVisibility(8);
                            holder.summary.setVisibility(0);
                            holder.cancel.setVisibility(8);
                            holder.progress.setVisibility(8);
                            holder.summary.setText(com.nuance.swype.input.R.string.pref_download_language_installed);
                            break;
                        default:
                            holder.icon.setVisibility(8);
                            holder.summary.setVisibility(4);
                            holder.cancel.setVisibility(8);
                            holder.progress.setVisibility(8);
                            break;
                    }
                }
                View layout = convertView;
                return layout;
            }
            if (item instanceof String) {
                TextView view = (TextView) this.parent.activity.getLayoutInflater().inflate(R.layout.preference_category, (ViewGroup) null);
                view.setText(String.valueOf(item));
                if (IMEApplication.from(this.parent.activity).isScreenLayoutTablet() && (view.getPaddingLeft() <= 16 || view.getPaddingStart() <= 16)) {
                    int padding2 = (int) (this.parent.activity.getResources().getDisplayMetrics().density * 32.0f);
                    view.setPaddingRelative(padding2, view.getTop(), padding2, view.getBottom());
                }
                return view;
            }
            AddonDictionariesPrefs.log.e("Error!");
            return null;
        }

        void onListItemClick(ListView l, View v, int position, long id) {
            DownloadableDictionary d = (DownloadableDictionary) getItem(position);
            if (d != null) {
                switch (d.getStatus()) {
                    case DOWNLOADING:
                        return;
                    case INSTALLED:
                        Bundle args = new Bundle();
                        args.putString(AddonDictionariesPrefs.ADDON_DICTIONARIES_KEY, d.getDictionary().getKey());
                        args.putString(AddonDictionariesPrefs.ADDON_DICTIONARIES_DISPLAY_NAME, d.getDictionary().getName());
                        this.parent.showRemoveDictionaryDialog(args);
                        return;
                    default:
                        if (this.connected) {
                            installDictionary(d);
                            return;
                        } else {
                            this.parent.showConnectDialog();
                            return;
                        }
                }
            }
        }

        public void onConnectionChanged(boolean isConnected) {
            this.connected = isConnected;
            notifyDataSetChanged();
        }

        void installDictionary(DownloadableDictionary d) {
            AddonDictionariesPrefs.log.d("installDictionary(", d.getDictionary().getKey(), ")");
            d.setStatus(DownloadableDictionary.Status.DOWNLOADING);
            this.downloadManager.dictionaryDownload(d.getDictionary(), new DictionaryDownloadCallback(d));
        }

        @Override // android.widget.BaseAdapter, android.widget.ListAdapter
        public boolean isEnabled(int position) {
            return getItem(position) instanceof DownloadableDictionary;
        }

        @Override // android.widget.BaseAdapter, android.widget.ListAdapter
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override // android.widget.BaseAdapter, android.widget.Adapter
        public int getViewTypeCount() {
            return 2;
        }

        @Override // android.widget.BaseAdapter, android.widget.Adapter
        public int getItemViewType(int position) {
            return getItem(position) instanceof DownloadableDictionary ? 1 : 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class DownloadableDictionary {
        private final ACChineseDictionaryDownloadService.ACChineseDictionary dictionary;
        private final BaseAdapter parent;
        private int progress = -1;
        private Status status;

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: classes.dex */
        public enum Status {
            AVAILABLE,
            INSTALLED,
            UPDATABLE,
            DOWNLOADING,
            CANCELED
        }

        DownloadableDictionary(ACChineseDictionaryDownloadService.ACChineseDictionary d, Status s, BaseAdapter b) {
            this.dictionary = d;
            this.status = s;
            this.parent = b;
        }

        public ACChineseDictionaryDownloadService.ACChineseDictionary getDictionary() {
            return this.dictionary;
        }

        public Status getStatus() {
            return this.status;
        }

        public void setStatus(Status s) {
            this.status = s;
            this.parent.notifyDataSetChanged();
        }

        public void setProgress(int percent) {
            this.progress = percent;
            this.parent.notifyDataSetChanged();
        }

        public int getProgress() {
            return this.progress;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class DictionaryDownloadCallback extends SDKDownloadStatusCallback {
        private final DownloadableDictionary parent;

        DictionaryDownloadCallback(DownloadableDictionary parent) {
            super(String.valueOf(parent.getDictionary().getKey()), parent.getDictionary().getId());
            this.parent = parent;
        }

        @Override // com.nuance.swype.connect.SDKDownloadStatusCallback
        public void downloadComplete() {
            super.downloadComplete();
        }

        @Override // com.nuance.swype.connect.SDKDownloadStatusCallback
        public void downloadInstalled() {
            log.d("downloadInstalled() ", this.parent.getDictionary().getKey());
            super.downloadInstalled();
            this.parent.setStatus(DownloadableDictionary.Status.INSTALLED);
        }

        @Override // com.nuance.swype.connect.SDKDownloadStatusCallback
        public void downloadPercentage(int percent) {
            log.d("downloadPercentage(", Integer.valueOf(percent), ") ", this.parent.getDictionary().getKey());
            super.downloadPercentage(percent);
            this.parent.setProgress(percent);
        }

        @Override // com.nuance.swype.connect.SDKDownloadStatusCallback
        public void downloadFailed(int reasonCode) {
            super.downloadFailed(reasonCode);
            this.parent.setStatus(DownloadableDictionary.Status.AVAILABLE);
        }

        @Override // com.nuance.swype.connect.SDKDownloadStatusCallback
        public void downloadStarted() {
            super.downloadStarted();
            this.parent.setStatus(DownloadableDictionary.Status.DOWNLOADING);
        }

        @Override // com.nuance.swype.connect.SDKDownloadStatusCallback
        public void downloadStopped(int reasonCode) {
            super.downloadStopped(reasonCode);
            this.parent.setStatus(DownloadableDictionary.Status.AVAILABLE);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void runOnUIThread(Runnable r) {
        this.activity.runOnUiThread(r);
    }
}
