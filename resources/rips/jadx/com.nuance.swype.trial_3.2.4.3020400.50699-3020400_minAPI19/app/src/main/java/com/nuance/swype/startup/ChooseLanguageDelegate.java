package com.nuance.swype.startup;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import com.nuance.android.util.WeakReferenceHandler;
import com.nuance.swype.connect.Connect;
import com.nuance.swype.connect.ConnectedStatus;
import com.nuance.swype.connect.SDKDownloadManager;
import com.nuance.swype.input.AppPreferences;
import com.nuance.swype.input.InputMethods;
import com.nuance.swype.input.R;
import com.nuance.swype.util.LogManager;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public class ChooseLanguageDelegate extends StartupDelegate {
    private ConnectedStatus connectedStatus;
    private SDKDownloadManager dlManager;
    private ListView languageList;
    private ProgressBar progressBar;
    private Handler weakHandler;
    private static final LogManager.Log log = LogManager.getLog("ChooseLanguageDelegate");
    private static final Comparator<InputMethods.Language> displayLanguageCompare = new Comparator<InputMethods.Language>() { // from class: com.nuance.swype.startup.ChooseLanguageDelegate.4
        private final Collator c = Collator.getInstance();

        @Override // java.util.Comparator
        public final /* bridge */ /* synthetic */ int compare(InputMethods.Language language, InputMethods.Language language2) {
            return this.c.compare(language.getDisplayName(), language2.getDisplayName());
        }
    };
    private boolean connectionLossAcknowledged = false;
    private final SDKDownloadManager.DataCallback dataCallback = new SDKDownloadManager.DataCallback() { // from class: com.nuance.swype.startup.ChooseLanguageDelegate.1
        @Override // com.nuance.swype.connect.SDKDownloadManager.DataCallback
        public final void listUpdated() {
            ChooseLanguageDelegate.access$000(ChooseLanguageDelegate.this);
        }

        @Override // com.nuance.swype.connect.SDKDownloadManager.DataCallback
        public final void languageUpdated(int languageId) {
        }

        @Override // com.nuance.swype.connect.SDKDownloadManager.DataCallback
        public final void onError(int reason) {
        }
    };
    private final Handler.Callback callback = new Handler.Callback() { // from class: com.nuance.swype.startup.ChooseLanguageDelegate.2
        @Override // android.os.Handler.Callback
        public final boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 2:
                    if (!ChooseLanguageDelegate.this.connectedStatus.isConnected() || !ChooseLanguageDelegate.this.dlManager.isLanguageListAvailable()) {
                        AlertDialog.Builder builder = ChooseLanguageDelegate.this.timeoutDialog();
                        ChooseLanguageDelegate.this.showDialog(builder.create());
                        return false;
                    }
                    return false;
                default:
                    return false;
            }
        }
    };

    static /* synthetic */ boolean access$502$145c3a42(ChooseLanguageDelegate x0) {
        x0.connectionLossAcknowledged = true;
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ChooseLanguageDelegate newInstance(Bundle savedInstanceState) {
        ChooseLanguageDelegate f = new ChooseLanguageDelegate();
        f.setArguments(savedInstanceState);
        return f;
    }

    @Override // com.nuance.swype.startup.StartupDelegate, android.app.Fragment
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.dlManager = Connect.from(getActivity()).getDownloadManager();
        this.connectedStatus = new ConnectedStatus(getActivity()) { // from class: com.nuance.swype.startup.ChooseLanguageDelegate.3
            @Override // com.nuance.swype.connect.ConnectedStatus
            public final void onInitialized() {
                super.onInitialized();
            }
        };
    }

    @Override // com.nuance.swype.startup.StartupDelegate, android.app.Fragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            this.connectionLossAcknowledged = savedInstanceState.getBoolean("connection_loss_acknowledged", false);
        }
    }

    @Override // com.nuance.swype.startup.StartupDelegate, android.app.Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.weakHandler = WeakReferenceHandler.create(this.callback);
        loadTemplateToContentView(inflater, R.layout.startup_template, R.layout.startup_choose_language, R.string.startup_choose_lang);
        this.languageList = (ListView) this.view.findViewById(R.id.list);
        this.progressBar = (ProgressBar) this.view.findViewById(R.id.progressbar);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override // com.nuance.swype.startup.StartupDelegate, android.app.Fragment
    public void onResume() {
        super.onResume();
        showActiveLanguagesList();
        log.d("onResume: isConnected: " + this.connectedStatus.isConnected());
        log.d("onResume: isLanguageListAvailable: " + this.dlManager.isLanguageListAvailable());
        this.dlManager.registerDataCallback(this.dataCallback);
        this.connectedStatus.register();
    }

    @Override // com.nuance.swype.startup.StartupDelegate, android.app.Fragment
    public void onPause() {
        super.onPause();
        this.weakHandler.removeCallbacksAndMessages(null);
        this.dlManager.unregisterDataCallback(this.dataCallback);
        this.connectedStatus.unregister();
    }

    @Override // android.app.Fragment
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("connection_loss_acknowledged", this.connectionLossAcknowledged);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public AlertDialog.Builder timeoutDialog() {
        this.weakHandler.removeMessages(2);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.startup_internet_connection);
        builder.setMessage(R.string.startup_connection_error);
        builder.setIcon(R.drawable.icon_settings_error);
        builder.setPositiveButton(getActivity().getResources().getString(R.string.startup_close), new DialogInterface.OnClickListener() { // from class: com.nuance.swype.startup.ChooseLanguageDelegate.6
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface arg0, int arg1) {
                arg0.dismiss();
                ChooseLanguageDelegate.access$502$145c3a42(ChooseLanguageDelegate.this);
                ChooseLanguageDelegate.this.showActiveLanguagesList();
            }
        });
        builder.setNegativeButton(getActivity().getResources().getString(R.string.startup_retry), new DialogInterface.OnClickListener() { // from class: com.nuance.swype.startup.ChooseLanguageDelegate.7
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface arg0, int arg1) {
                arg0.dismiss();
                ChooseLanguageDelegate.log.d("onClick connectedStatus is:", Boolean.valueOf(ChooseLanguageDelegate.this.connectedStatus.isConnected()));
                ChooseLanguageDelegate.this.showActiveLanguagesList();
            }
        });
        return builder;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showActiveLanguagesList() {
        List<String> displayLanguageNames = new ArrayList<>();
        final List<InputMethods.Language> availableLanguages = getAvailableLanguages();
        Collections.sort(availableLanguages, displayLanguageCompare);
        List<InputMethods.Language> installedLanguage = InputMethods.from(getActivity()).getInputLanguages();
        for (int i = 0; i < installedLanguage.size(); i++) {
            availableLanguages.add(i, installedLanguage.get(i));
            displayLanguageNames.add(installedLanguage.get(i).getDisplayName());
        }
        removeDuplicate(availableLanguages);
        log.d("showActiveLanguagesList() isLanguageListAvailable=" + this.dlManager.isLanguageListAvailable());
        log.d("showActiveLanguagesList: connected: " + this.connectedStatus.isConnected());
        this.weakHandler.removeMessages(2);
        if (this.dlManager.isLanguageListAvailable()) {
            for (InputMethods.Language language : availableLanguages) {
                displayLanguageNames.add(language.getDisplayName());
            }
            removeDuplicate(displayLanguageNames);
        } else if (!this.connectionLossAcknowledged) {
            this.weakHandler.sendEmptyMessageDelayed(2, 30000L);
            this.progressBar.setVisibility(0);
            ArrayAdapter<String> ad = new ArrayAdapter<>(getActivity(), R.layout.startup_language_list, R.id.language_item, displayLanguageNames);
            this.languageList.setAdapter((ListAdapter) ad);
            this.languageList.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.nuance.swype.startup.ChooseLanguageDelegate.8
                @Override // android.widget.AdapterView.OnItemClickListener
                public final void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    ChooseLanguageDelegate.log.d("onItemClick: ", Integer.valueOf(arg2));
                    Map<Integer, SDKDownloadManager.DownloadableLanguage> downloadableLanguageMap = Connect.from(ChooseLanguageDelegate.this.getActivity()).getDownloadManager().getSettingDownloadLanguageList(false);
                    InputMethods.Language language2 = (InputMethods.Language) availableLanguages.get(arg2);
                    int languageId = language2.getCoreLanguageId();
                    SDKDownloadManager.DownloadableLanguage downloadableLanguage = downloadableLanguageMap.get(Integer.valueOf(languageId));
                    ChooseLanguageDelegate.log.d("onItemClick: language: ", language2.mEnglishName);
                    AppPreferences app = AppPreferences.from(ChooseLanguageDelegate.this.getActivity());
                    if (downloadableLanguage == null) {
                        ChooseLanguageDelegate.log.d("onItemClick: detected built-in language");
                        InputMethods.from(ChooseLanguageDelegate.this.getActivity()).setCurrentLanguage(language2.getLanguageId());
                        ChooseLanguageDelegate.this.mStartupSequenceInfo.setLanguageSelectedAndInstalled$1385ff();
                    } else if (downloadableLanguage.getStatus() == SDKDownloadManager.DownloadableLanguage.Status.INSTALLED || downloadableLanguage.getStatus() == SDKDownloadManager.DownloadableLanguage.Status.UPDATED || downloadableLanguage.getStatus() == SDKDownloadManager.DownloadableLanguage.Status.UPDATE_AVAILABLE || downloadableLanguage.getStatus() == SDKDownloadManager.DownloadableLanguage.Status.COMPLETE) {
                        ChooseLanguageDelegate.log.d("onItemClick: detected downloadable language as already installed");
                        InputMethods.from(ChooseLanguageDelegate.this.getActivity()).setCurrentLanguage(language2.getLanguageId());
                        ChooseLanguageDelegate.this.mStartupSequenceInfo.setLanguageSelectedAndInstalled$1385ff();
                    } else if (ChooseLanguageDelegate.access$700(ChooseLanguageDelegate.this)) {
                        ChooseLanguageDelegate.log.d("onItemClick: preparing downloadable language for download");
                        app.setStartupSequenceDownloadLanguageDisplayName(language2.getDisplayName());
                        app.setStartupSequenceDownloadLanguageID(String.valueOf(language2.getCoreLanguageId()));
                    } else {
                        return;
                    }
                    ChooseLanguageDelegate.this.mActivityCallbacks.startNextScreen(ChooseLanguageDelegate.this.mFlags, ChooseLanguageDelegate.this.mResultData);
                }
            });
        }
        this.progressBar.setVisibility(8);
        ArrayAdapter<String> ad2 = new ArrayAdapter<>(getActivity(), R.layout.startup_language_list, R.id.language_item, displayLanguageNames);
        this.languageList.setAdapter((ListAdapter) ad2);
        this.languageList.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.nuance.swype.startup.ChooseLanguageDelegate.8
            @Override // android.widget.AdapterView.OnItemClickListener
            public final void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                ChooseLanguageDelegate.log.d("onItemClick: ", Integer.valueOf(arg2));
                Map<Integer, SDKDownloadManager.DownloadableLanguage> downloadableLanguageMap = Connect.from(ChooseLanguageDelegate.this.getActivity()).getDownloadManager().getSettingDownloadLanguageList(false);
                InputMethods.Language language2 = (InputMethods.Language) availableLanguages.get(arg2);
                int languageId = language2.getCoreLanguageId();
                SDKDownloadManager.DownloadableLanguage downloadableLanguage = downloadableLanguageMap.get(Integer.valueOf(languageId));
                ChooseLanguageDelegate.log.d("onItemClick: language: ", language2.mEnglishName);
                AppPreferences app = AppPreferences.from(ChooseLanguageDelegate.this.getActivity());
                if (downloadableLanguage == null) {
                    ChooseLanguageDelegate.log.d("onItemClick: detected built-in language");
                    InputMethods.from(ChooseLanguageDelegate.this.getActivity()).setCurrentLanguage(language2.getLanguageId());
                    ChooseLanguageDelegate.this.mStartupSequenceInfo.setLanguageSelectedAndInstalled$1385ff();
                } else if (downloadableLanguage.getStatus() == SDKDownloadManager.DownloadableLanguage.Status.INSTALLED || downloadableLanguage.getStatus() == SDKDownloadManager.DownloadableLanguage.Status.UPDATED || downloadableLanguage.getStatus() == SDKDownloadManager.DownloadableLanguage.Status.UPDATE_AVAILABLE || downloadableLanguage.getStatus() == SDKDownloadManager.DownloadableLanguage.Status.COMPLETE) {
                    ChooseLanguageDelegate.log.d("onItemClick: detected downloadable language as already installed");
                    InputMethods.from(ChooseLanguageDelegate.this.getActivity()).setCurrentLanguage(language2.getLanguageId());
                    ChooseLanguageDelegate.this.mStartupSequenceInfo.setLanguageSelectedAndInstalled$1385ff();
                } else if (ChooseLanguageDelegate.access$700(ChooseLanguageDelegate.this)) {
                    ChooseLanguageDelegate.log.d("onItemClick: preparing downloadable language for download");
                    app.setStartupSequenceDownloadLanguageDisplayName(language2.getDisplayName());
                    app.setStartupSequenceDownloadLanguageID(String.valueOf(language2.getCoreLanguageId()));
                } else {
                    return;
                }
                ChooseLanguageDelegate.this.mActivityCallbacks.startNextScreen(ChooseLanguageDelegate.this.mFlags, ChooseLanguageDelegate.this.mResultData);
            }
        });
    }

    private static void removeDuplicate(List list) {
        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = list.size() - 1; j > i; j--) {
                if (list.get(j).equals(list.get(i))) {
                    list.remove(j);
                }
            }
        }
    }

    private List<InputMethods.Language> getAvailableLanguages() {
        List<InputMethods.Language> availableLanguages = new ArrayList<>();
        if (this.dlManager != null) {
            List<Integer> availableLanguageIds = new ArrayList<>(this.dlManager.getSettingDownloadLanguageList(false).keySet());
            if (availableLanguageIds.size() != 0) {
                SparseArray<InputMethods.Language> inputLanguages = new SparseArray<>();
                for (Map.Entry<String, InputMethods.Language> entry : InputMethods.from(getActivity()).getAllLanguages().entrySet()) {
                    inputLanguages.put(entry.getValue().getCoreLanguageId(), entry.getValue());
                }
                for (Integer langId : availableLanguageIds) {
                    InputMethods.Language language = inputLanguages.get(langId.intValue());
                    if (language != null) {
                        availableLanguages.add(language);
                    }
                }
                Collections.sort(availableLanguages, displayLanguageCompare);
            }
        }
        return availableLanguages;
    }

    static /* synthetic */ void access$000(ChooseLanguageDelegate x0) {
        x0.getActivity().runOnUiThread(new Runnable() { // from class: com.nuance.swype.startup.ChooseLanguageDelegate.5
            @Override // java.lang.Runnable
            public final void run() {
                ChooseLanguageDelegate.this.showActiveLanguagesList();
            }
        });
    }

    static /* synthetic */ boolean access$700(ChooseLanguageDelegate x0) {
        if (!x0.connectedStatus.isConnected()) {
            x0.showDialog(x0.timeoutDialog().create());
            return false;
        }
        return true;
    }
}
