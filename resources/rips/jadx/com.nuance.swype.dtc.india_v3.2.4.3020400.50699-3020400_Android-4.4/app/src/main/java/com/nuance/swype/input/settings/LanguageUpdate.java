package com.nuance.swype.input.settings;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.nuance.android.util.WeakReferenceHandler;
import com.nuance.connect.common.Integers;
import com.nuance.swype.connect.Connect;
import com.nuance.swype.connect.ConnectLegal;
import com.nuance.swype.connect.ConnectedStatus;
import com.nuance.swype.connect.SDKDownloadManager;
import com.nuance.swype.input.AppPreferences;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.InputMethods;
import com.nuance.swype.input.R;
import com.nuance.swype.preference.ConnectionAwarePreferences;
import com.nuance.swype.stats.StatisticsManager;
import com.nuance.swype.usagedata.UsageData;
import com.nuance.swype.util.LogManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/* loaded from: classes.dex */
public abstract class LanguageUpdate {
    static final /* synthetic */ boolean $assertionsDisabled;
    private static final String LANGUAGE_KEY = "language";
    private static final int MESSAGE_DATASET_INVALIDATED = 1;
    private static final int MESSAGE_LIST_UPDATED = 0;
    private static final int MESSAGE_TIMEOUT_LIST = 2;
    private static final int MESSAGE_UPDATE_STATUS = 4;
    private static final int REQUEST_CODE_LANGUAGE_INSTALL = 1;
    private static final int TIMEOUT_DELAY = 30000;
    private static final LogManager.Log log;
    final Activity activity;
    private LanguageListAdapter adapter;
    private Dialog connectDialog;
    private final ConnectedStatus connectedStatus;
    private final ConnectionAwarePreferences connection;
    private SDKDownloadManager dlManager;
    private boolean isRunning;
    private Handler.Callback callback = new Handler.Callback() { // from class: com.nuance.swype.input.settings.LanguageUpdate.1
        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    LanguageUpdate.log.d("dlManager.isLanguageSetupComplete() ", Boolean.valueOf(LanguageUpdate.this.dlManager.isLanguageSetupComplete()), " dlManager.isLanguageListAvailable() ", Boolean.valueOf(LanguageUpdate.this.dlManager.isLanguageListAvailable()));
                    if (!LanguageUpdate.this.dlManager.isLanguageSetupComplete() || !LanguageUpdate.this.dlManager.isLanguageListAvailable()) {
                        LanguageUpdate.this.weakHandler.removeMessages(0);
                        LanguageUpdate.this.weakHandler.sendMessageDelayed(LanguageUpdate.this.weakHandler.obtainMessage(0), 50L);
                    } else {
                        Map<Integer, SDKDownloadManager.DownloadableLanguage> list = LanguageUpdate.this.dlManager.getSettingDownloadLanguageList();
                        if (list.size() > 0) {
                            LanguageUpdate.this.displayTimeoutDialog = false;
                        }
                        LanguageUpdate.this.adapter.updateLanguages(list);
                        LanguageUpdate.this.adapter.notifyDataSetChanged();
                    }
                    return false;
                case 1:
                    LanguageUpdate.this.adapter.doNotifyDataSetInvalidated();
                    return false;
                case 2:
                    if (LanguageUpdate.this.displayTimeoutDialog && !LanguageUpdate.this.activity.isFinishing() && LanguageUpdate.this.isRunning) {
                        LanguageUpdate.this.showTimeoutDialog();
                    }
                    return false;
                case 3:
                default:
                    LanguageUpdate.log.d("No need to handle message: ", Integer.valueOf(msg.what));
                    return false;
                case 4:
                    LanguageUpdate.log.d("MESSAGE_UPDATE_STATUS ", Integer.valueOf(msg.arg1));
                    LanguageUpdate.this.adapter.updateStatus(msg.arg1);
                    return false;
            }
        }
    };
    private Handler weakHandler = WeakReferenceHandler.create(this.callback);
    private boolean displayTimeoutDialog = true;
    SDKDownloadManager.DataCallback dataCallback = new SDKDownloadManager.DataCallback() { // from class: com.nuance.swype.input.settings.LanguageUpdate.2
        @Override // com.nuance.swype.connect.SDKDownloadManager.DataCallback
        public void listUpdated() {
            LanguageUpdate.log.d("listUpdated()");
            LanguageUpdate.this.weakHandler.removeMessages(0);
            LanguageUpdate.this.weakHandler.sendMessage(LanguageUpdate.this.weakHandler.obtainMessage(0, 0, 0));
        }

        @Override // com.nuance.swype.connect.SDKDownloadManager.DataCallback
        public void languageUpdated(int languageId) {
            LanguageUpdate.log.d("languageUpdated()...languageId: ", Integer.valueOf(languageId));
            if (Looper.getMainLooper().equals(Looper.myLooper())) {
                LanguageUpdate.this.adapter.updateStatus(languageId);
            } else {
                LanguageUpdate.this.weakHandler.sendMessage(LanguageUpdate.this.weakHandler.obtainMessage(4, languageId, 0));
            }
        }

        @Override // com.nuance.swype.connect.SDKDownloadManager.DataCallback
        public void onError(int reason) {
            LanguageUpdate.log.d("Error when downloading language. Reason: " + reason);
            switch (reason) {
                case 2:
                    if (LanguageUpdate.this.connectDialog == null || !LanguageUpdate.this.connectDialog.isShowing()) {
                        LanguageUpdate.this.connectDialog = LanguageUpdate.this.createConnectDialog();
                        LanguageUpdate.this.connectDialog.show();
                        return;
                    }
                    return;
                default:
                    LanguageUpdate.log.d("Not handling specified");
                    return;
            }
        }
    };

    protected abstract ListView doGetListView();

    protected abstract void doStartActivityForResult(Intent intent, int i);

    protected abstract void showConnectDialog();

    protected abstract void showRemoveLanguageDialog(Bundle bundle);

    protected abstract void showTimeoutDialog();

    static {
        $assertionsDisabled = !LanguageUpdate.class.desiredAssertionStatus();
        log = LogManager.getLog("LanguageUpdate");
    }

    public LanguageUpdate(Activity activity) {
        this.activity = activity;
        this.connection = new ConnectionAwarePreferences(activity) { // from class: com.nuance.swype.input.settings.LanguageUpdate.3
            @Override // com.nuance.swype.preference.ConnectionAwarePreferences
            public void showConnectDialog() {
            }
        };
        this.connectedStatus = new ConnectedStatus(activity) { // from class: com.nuance.swype.input.settings.LanguageUpdate.4
            @Override // com.nuance.swype.connect.ConnectedStatus
            public void onConnectionChanged(boolean isConnected) {
                LanguageUpdate.log.d("onConnectionChanged(", Boolean.valueOf(isConnected), ")");
                if (LanguageUpdate.this.adapter != null) {
                    LanguageUpdate.this.adapter.onConnectionChanged();
                }
            }

            @Override // com.nuance.swype.connect.ConnectedStatus
            public void onInitialized() {
                super.onInitialized();
                if (LanguageUpdate.this.weakHandler != null) {
                    LanguageUpdate.this.weakHandler.removeMessages(0);
                    LanguageUpdate.this.weakHandler.sendEmptyMessage(0);
                }
            }

            @Override // com.nuance.swype.connect.ConnectedStatus
            public void onConnectionStatus(int status, String message) {
                LanguageUpdate.log.d("onConnectionStatus() status:" + ConnectedStatus.StatusStates.forId(status) + " message:" + message);
                if (status == 2) {
                    if (LanguageUpdate.this.connectDialog == null || !LanguageUpdate.this.connectDialog.isShowing()) {
                        LanguageUpdate.this.connectDialog = LanguageUpdate.this.createConnectDialogForLanguage();
                        LanguageUpdate.this.connectDialog.show();
                    }
                }
            }
        };
        this.dlManager = Connect.from(activity).getDownloadManager();
        this.adapter = new LanguageListAdapter(activity, this.dlManager, this);
        this.adapter.onConnectionChanged();
        this.dlManager.registerDataCallback(this.dataCallback);
        this.adapter.updateLanguages(this.dlManager.getSettingDownloadLanguageList());
        UsageData.recordScreenVisited(UsageData.Screen.DOWNLOAD_LANGUAGE);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onStart() {
        this.connectedStatus.register();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onPause() {
        this.isRunning = false;
        this.weakHandler.removeMessages(2);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onStop() {
        this.connectedStatus.unregister();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onDestroy() {
        this.dlManager.unregisterDataCallback(this.dataCallback);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onResume() {
        this.isRunning = true;
        log.d("LanguageUpdate connection.isConnected() ", Boolean.valueOf(this.connectedStatus.isConnected()));
        log.d("LanguageUpdate connection.isInitialized()", Boolean.valueOf(this.connectedStatus.isInitialized()));
        if (!this.dlManager.isLanguageListAvailable()) {
            this.weakHandler.removeMessages(2);
            this.weakHandler.sendMessageDelayed(Message.obtain(this.weakHandler, 2), 30000L);
            ListView v = doGetListView();
            if (v != null) {
                View empty = this.activity.findViewById(R.id.progressbar);
                v.setEmptyView(empty);
                return;
            }
            return;
        }
        if (!this.connectedStatus.isConnected()) {
            ListView v2 = doGetListView();
            if (v2 != null) {
                View empty2 = this.activity.findViewById(R.id.data_required);
                v2.setEmptyView(empty2);
                return;
            }
            return;
        }
        ListView v3 = doGetListView();
        if (v3 != null) {
            View empty3 = this.activity.findViewById(R.id.no_languages);
            v3.setEmptyView(empty3);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bundle extra;
        int languageId;
        switch (requestCode) {
            case 1:
                if (resultCode == -1 && (extra = data.getBundleExtra("result_data")) != null && (languageId = extra.getInt("language", Integers.STATUS_SUCCESS)) != Integer.MIN_VALUE) {
                    this.adapter.installLanguage(languageId);
                    return;
                }
                return;
            default:
                return;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onListItemClick(ListView l, View v, int position, long id) {
        this.adapter.onListItemClick(l, v, position, id);
    }

    protected boolean isLicensed() {
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Dialog createConnectDialog() {
        return this.connection.getConnectDialog().create();
    }

    protected Dialog createConnectDialogForLanguage() {
        return getLanguageDownloadFailedDialog(this.activity).create();
    }

    public AlertDialog.Builder getLanguageDownloadFailedDialog(Context ctx) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(ctx, android.R.style.Theme.DeviceDefault.Light.Dialog.MinWidth));
        builder.setIcon(R.drawable.icon_settings_error);
        builder.setTitle(R.string.error_connection_title);
        builder.setMessage(R.string.error_language_download_failure);
        builder.setPositiveButton(android.R.string.ok, (DialogInterface.OnClickListener) null);
        return builder;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Dialog createRemoveLanguageDialog(Bundle args) {
        if (!$assertionsDisabled && args == null) {
            throw new AssertionError();
        }
        final int languageId = args.getInt("language");
        InputMethods.Language language = this.dlManager.getLanguageFromId(languageId);
        if (language == null) {
            return null;
        }
        String msg = String.format(this.activity.getString(R.string.pref_remove_languages_dialog_desc), language.getDisplayName());
        return new AlertDialog.Builder(this.activity).setTitle(R.string.pref_remove_languages_dialog_title).setMessage(msg).setPositiveButton(R.string.yes_button, new DialogInterface.OnClickListener() { // from class: com.nuance.swype.input.settings.LanguageUpdate.5
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int whichButton) {
                LanguageUpdate.this.adapter.removeLanguage(languageId);
            }
        }).setNegativeButton(R.string.no_button, (DialogInterface.OnClickListener) null).create();
    }

    public Dialog timeoutDialog() {
        this.weakHandler.removeMessages(2);
        AlertDialog.Builder builder = new AlertDialog.Builder(this.activity);
        builder.setTitle(R.string.startup_internet_connection);
        builder.setMessage(R.string.startup_connection_error);
        builder.setIcon(R.drawable.icon_settings_error);
        builder.setPositiveButton(this.activity.getResources().getString(R.string.startup_close), new DialogInterface.OnClickListener() { // from class: com.nuance.swype.input.settings.LanguageUpdate.6
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface arg0, int arg1) {
                arg0.dismiss();
                LanguageUpdate.this.activity.finish();
            }
        });
        builder.setNegativeButton(this.activity.getResources().getString(R.string.startup_retry), new DialogInterface.OnClickListener() { // from class: com.nuance.swype.input.settings.LanguageUpdate.7
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface arg0, int arg1) {
                LanguageUpdate.this.weakHandler.sendMessageDelayed(Message.obtain(LanguageUpdate.this.weakHandler, 2), 30000L);
            }
        });
        return builder.create();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void cancelDownloadInProgress(int languageId) {
        log.d("cancelDownload() ", Integer.valueOf(languageId));
        this.dlManager.languageCancel(languageId, true);
        this.adapter.updateStatus(languageId);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean showLegalRequirements(int requestCode, boolean tosRequired, boolean optInRequired, Bundle resultData) {
        Intent intent = ConnectLegal.getLegalActivitiesStartIntent(this.activity, tosRequired, optInRequired, resultData);
        if (intent == null) {
            return false;
        }
        doStartActivityForResult(intent, requestCode);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class LanguageListAdapter extends BaseAdapter {
        private List<String> builtinLanguages;
        private Context context;
        private SDKDownloadManager downloadManager;
        private boolean initialized;
        private LanguageUpdate languageUpdateParent;
        private ListView parentListView;
        private final List<SDKDownloadManager.DownloadableLanguage> languages = new ArrayList();
        private final HashMap<Integer, SDKDownloadManager.DownloadableLanguage> langMap = new HashMap<>();
        private final int[] lock = new int[0];

        public LanguageListAdapter(Context context, SDKDownloadManager downloadManager, LanguageUpdate parent) {
            this.context = context;
            this.downloadManager = downloadManager;
            this.languageUpdateParent = parent;
            AppPreferences appPrefs = AppPreferences.from(context);
            this.builtinLanguages = appPrefs.getStrings(AppPreferences.BUILTIN_LANGUAGES, null);
        }

        public boolean updateLanguages(Map<Integer, SDKDownloadManager.DownloadableLanguage> updated) {
            boolean changed = false;
            synchronized (this.lock) {
                HashSet<Integer> oldKeys = new HashSet<>(this.langMap.keySet());
                oldKeys.removeAll(updated.keySet());
                if (oldKeys.size() > 0) {
                    changed = true;
                    Iterator<Integer> it = oldKeys.iterator();
                    while (it.hasNext()) {
                        Integer key = it.next();
                        SDKDownloadManager.DownloadableLanguage removed = this.langMap.remove(key);
                        if (removed != null) {
                            this.languages.remove(removed);
                        }
                    }
                }
                for (SDKDownloadManager.DownloadableLanguage language : updated.values()) {
                    if (!this.langMap.containsKey(Integer.valueOf(language.getCoreLanguageId()))) {
                        this.langMap.put(Integer.valueOf(language.getCoreLanguageId()), language);
                        this.languages.add(language);
                        changed = true;
                    }
                }
                Collections.sort(this.languages);
            }
            if (!this.initialized) {
                this.initialized = true;
                notifyDataSetChanged();
            } else if (changed) {
                notifyDataSetChanged();
            }
            return changed;
        }

        public void installLanguage(int languageId) {
            SDKDownloadManager.DownloadableLanguage lang;
            LanguageUpdate.log.d("installLanguage(", Integer.valueOf(languageId), ")");
            Bundle requestData = new Bundle();
            requestData.putInt("language", languageId);
            if (!this.languageUpdateParent.showLegalRequirements(1, true, false, requestData)) {
                synchronized (this.lock) {
                    lang = this.langMap.get(Integer.valueOf(languageId));
                }
                if (lang != null && !lang.showNoSpaceNotificationIfShortStorage()) {
                    this.downloadManager.languageDownload(languageId);
                    UsageData.recordLanguageDownloadRequest(lang.getEnglishName());
                    StatisticsManager.SessionStatsScribe sessionScribe = StatisticsManager.getSessionStatsScribe(this.context);
                    if (sessionScribe != null) {
                        sessionScribe.recordSettingsChange("Download Languages", languageId + " installed", " not installed");
                    }
                }
            }
        }

        public void removeLanguage(int languageId) {
            SDKDownloadManager.DownloadableLanguage lang;
            synchronized (this.lock) {
                lang = this.langMap.get(Integer.valueOf(languageId));
            }
            if (lang != null) {
                this.downloadManager.languageUninstall(languageId);
            }
        }

        @Override // android.widget.BaseAdapter, android.widget.Adapter
        public boolean isEmpty() {
            return !this.downloadManager.isLanguageListAvailable() || super.isEmpty();
        }

        @Override // android.widget.Adapter
        public int getCount() {
            if (this.languages == null) {
                return 0;
            }
            return this.languages.size();
        }

        @Override // android.widget.Adapter
        public Object getItem(int arg0) {
            SDKDownloadManager.DownloadableLanguage downloadableLanguage;
            synchronized (this.lock) {
                downloadableLanguage = this.languages == null ? null : this.languages.get(arg0);
            }
            return downloadableLanguage;
        }

        @Override // android.widget.Adapter
        public long getItemId(int arg0) {
            long coreLanguageId;
            synchronized (this.lock) {
                coreLanguageId = this.languages == null ? 0L : this.languages.get(arg0).getCoreLanguageId();
            }
            return coreLanguageId;
        }

        @Override // android.widget.Adapter
        @SuppressLint({"InflateParams"})
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            SDKDownloadManager.DownloadableLanguage lang;
            if (this.parentListView == null && parent != null && (parent instanceof ListView)) {
                this.parentListView = (ListView) parent;
            }
            if (convertView == null) {
                convertView = this.languageUpdateParent.activity.getLayoutInflater().inflate(R.layout.language_download_list_item, (ViewGroup) null);
                if (IMEApplication.from(this.languageUpdateParent.activity).isScreenLayoutTablet() && (convertView.getPaddingLeft() <= 16 || convertView.getPaddingStart() <= 16)) {
                    int padding = (int) (this.languageUpdateParent.activity.getResources().getDisplayMetrics().density * 32.0f);
                    convertView.setPaddingRelative(padding, convertView.getTop(), padding, convertView.getBottom());
                }
                holder = new ViewHolder();
                holder.icon = (ImageView) convertView.findViewById(R.id.icon);
                holder.icon.setImageResource(R.drawable.icon_settings_error);
                holder.title = (TextView) convertView.findViewById(R.id.title);
                holder.summary = (TextView) convertView.findViewById(R.id.summary);
                holder.progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar1);
                holder.cancel = (ImageView) convertView.findViewById(R.id.cancel);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            synchronized (this.lock) {
                lang = this.languages.get(position);
            }
            updateView(holder, lang);
            return convertView;
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes.dex */
        public static class ViewHolder {
            public ImageView cancel;
            public View.OnClickListener cancelListener;
            public ImageView icon;
            public int languageId;
            public SDKDownloadManager.DownloadableLanguage.Status previousStatus;
            public ProgressBar progressBar;
            public TextView summary;
            public TextView title;

            private ViewHolder() {
            }
        }

        private void updateView(ViewHolder holder, SDKDownloadManager.DownloadableLanguage lang) {
            if (lang != null && holder != null) {
                boolean refresh = false;
                final int languageId = lang.getCoreLanguageId();
                if (holder.languageId != languageId) {
                    synchronized (this.lock) {
                        holder.languageId = languageId;
                    }
                    holder.cancel.setOnClickListener(null);
                    holder.cancelListener = null;
                    holder.progressBar.setMax(100);
                    holder.progressBar.setProgress(0);
                    holder.title.setText(lang.getDisplayName());
                    holder.previousStatus = null;
                    refresh = true;
                }
                boolean statusChanged = lang.getStatus() != holder.previousStatus;
                boolean refresh2 = refresh || statusChanged;
                holder.previousStatus = lang.getStatus();
                if (refresh2 || holder.previousStatus.equals(SDKDownloadManager.DownloadableLanguage.Status.DOWNLOADING) || holder.previousStatus.equals(SDKDownloadManager.DownloadableLanguage.Status.STOPPED)) {
                    LanguageUpdate.log.d("updateView...lang.getStatus(): ", lang.getStatus());
                    switch (lang.getStatus()) {
                        case DOWNLOADING:
                        case STOPPED:
                            holder.icon.setVisibility(8);
                            if (lang.getProgress() > 0 && lang.getStatus() != SDKDownloadManager.DownloadableLanguage.Status.STOPPED) {
                                if (lang.getProgress() >= 95 && holder.cancel.getVisibility() == 0 && holder.cancelListener != null) {
                                    holder.cancel.setOnClickListener(holder.cancelListener);
                                }
                                holder.progressBar.setProgress(lang.getProgress());
                                holder.progressBar.setIndeterminate(false);
                            } else {
                                holder.progressBar.setProgress(0);
                                holder.progressBar.setIndeterminate(true);
                            }
                            holder.progressBar.setVisibility(0);
                            if (refresh2 || holder.cancel.getVisibility() != 0) {
                                holder.cancel.setVisibility(0);
                                if (holder.cancelListener == null) {
                                    holder.cancelListener = new View.OnClickListener() { // from class: com.nuance.swype.input.settings.LanguageUpdate.LanguageListAdapter.1
                                        @Override // android.view.View.OnClickListener
                                        public void onClick(View v) {
                                            LanguageListAdapter.this.languageUpdateParent.cancelDownloadInProgress(languageId);
                                        }
                                    };
                                }
                                holder.cancel.setOnClickListener(holder.cancelListener);
                            }
                            holder.summary.setVisibility(8);
                            return;
                        case INSTALLED:
                            holder.icon.setVisibility(8);
                            holder.summary.setVisibility(0);
                            holder.cancel.setVisibility(8);
                            holder.progressBar.setVisibility(8);
                            holder.summary.setText(R.string.pref_download_language_installed);
                            holder.cancel.setOnClickListener(null);
                            return;
                        case UPDATE_AVAILABLE:
                            holder.icon.setVisibility(8);
                            holder.summary.setVisibility(0);
                            holder.cancel.setVisibility(8);
                            holder.progressBar.setVisibility(8);
                            holder.summary.setText(R.string.pref_download_language_update);
                            holder.cancel.setOnClickListener(null);
                            return;
                        case UPDATED:
                            holder.icon.setVisibility(8);
                            holder.summary.setVisibility(0);
                            holder.cancel.setVisibility(8);
                            holder.progressBar.setVisibility(8);
                            holder.summary.setText(R.string.notification_successfully_updated);
                            holder.cancel.setOnClickListener(null);
                            return;
                        case COMPLETE:
                            holder.cancel.setOnClickListener(null);
                            holder.icon.setVisibility(8);
                            holder.cancel.setVisibility(8);
                            return;
                        case DOWNLOAD_FAILED_OFFLINE:
                            holder.icon.setVisibility(0);
                            return;
                        default:
                            holder.icon.setVisibility(8);
                            holder.summary.setVisibility(4);
                            holder.cancel.setVisibility(8);
                            holder.progressBar.setVisibility(8);
                            holder.cancel.setOnClickListener(null);
                            return;
                    }
                }
            }
        }

        protected void onListItemClick(ListView l, View v, int position, long id) {
            SDKDownloadManager.DownloadableLanguage lang;
            LanguageUpdate.log.d("adapter.onListItemClicked(", Long.valueOf(id), ")");
            synchronized (this.lock) {
                lang = this.langMap.get(Integer.valueOf((int) id));
            }
            if (lang != null) {
                switch (lang.getStatus()) {
                    case DOWNLOADING:
                        return;
                    case STOPPED:
                    default:
                        installLanguage(lang.getCoreLanguageId());
                        return;
                    case INSTALLED:
                        if (this.builtinLanguages == null || !this.builtinLanguages.contains(lang.getEnglishName())) {
                            Bundle args = new Bundle();
                            args.putInt("language", lang.getCoreLanguageId());
                            this.languageUpdateParent.showRemoveLanguageDialog(args);
                            return;
                        }
                        return;
                }
            }
        }

        @Override // android.widget.BaseAdapter
        public void notifyDataSetChanged() {
            if (Looper.myLooper() != Looper.getMainLooper()) {
                LanguageUpdate.log.e("DATASET CHANGED OFF MAIN THREAD");
            } else {
                super.notifyDataSetChanged();
            }
        }

        @Override // android.widget.BaseAdapter
        public void notifyDataSetInvalidated() {
            this.languageUpdateParent.weakHandler.removeMessages(1);
            this.languageUpdateParent.weakHandler.sendMessageDelayed(Message.obtain(this.languageUpdateParent.weakHandler, 1), 20L);
        }

        void doNotifyDataSetInvalidated() {
            if (Looper.myLooper() != Looper.getMainLooper()) {
                LanguageUpdate.log.e("DATASET INVALIDATED OFF MAIN THREAD");
            } else {
                super.notifyDataSetInvalidated();
            }
        }

        public void onConnectionChanged() {
            notifyDataSetChanged();
        }

        public void updateStatus(int languageId) {
            SDKDownloadManager.DownloadableLanguage lang;
            LanguageUpdate.log.d("updateStatus(", Integer.valueOf(languageId), ")");
            if (this.parentListView == null) {
                LanguageUpdate.log.d("no parent list");
                return;
            }
            synchronized (this.lock) {
                lang = this.langMap.get(Integer.valueOf(languageId));
            }
            if (lang.getStatus() == SDKDownloadManager.DownloadableLanguage.Status.INSTALLED) {
                List<InputMethods.Language> installedLang = new ArrayList<>();
                installedLang.add(lang.getLanguage());
                Locale currentLocale = this.context.getResources().getConfiguration().locale;
                if (InputMethods.from(this.context).findLanguageBestFitsCurrentLocale(currentLocale, installedLang) != null) {
                    InputMethods.from(this.context).setCurrentLanguage(installedLang.get(0).getLanguageId());
                }
            }
            View v = this.parentListView.getChildAt(this.languages.indexOf(lang) - this.parentListView.getFirstVisiblePosition());
            if (lang != null && v != null) {
                ViewHolder vh = (ViewHolder) v.getTag();
                updateView(vh, lang);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public BaseAdapter getAdapter() {
        return this.adapter;
    }
}
