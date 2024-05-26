package com.nuance.sns;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import com.nuance.dlm.ACCoreInputDLM;
import com.nuance.input.swypecorelib.SwypeCoreLibrary;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.R;
import com.nuance.swype.util.LogManager;
import com.nuance.swypeconnect.ac.ACScanner;
import com.nuance.swypeconnect.ac.ACScannerService;
import java.lang.ref.WeakReference;
import java.util.Date;

/* loaded from: classes.dex */
public abstract class SocialNetworkActivity extends FragmentActivity {
    private static final String LOGOUT_SEG = "logout";
    protected static final int PERMISSIONS_REQUEST_GET_ACCOUNTS = 3;
    protected static final int PERMISSIONS_REQUEST_READ_CALL_LOG = 2;
    protected static final int PERMISSIONS_REQUEST_READ_SMS = 1;
    protected static final String TAG = "ScannerApp";
    private static final LogManager.Log log = LogManager.getLog("SocialNetworkActivity");
    private boolean activityPaused;
    private String authUrl;
    private LinearLayout container;
    protected WeakReference<ACScanner> mScanner;
    protected ACScannerService mScannerService;
    protected ScraperStatus scraperStatus;
    protected boolean sentAuthUrlRequest;
    private ProgressDialog spinner;
    private WebView webView;
    protected boolean webViewLoaded;
    protected final int MAX_SMS_COUNT = R.styleable.ThemeTemplate_pressableBackgroundHighlight;
    protected final int MAX_GMAIL_COUNT = 50;
    protected final int MAX_CALLLOG_COUNT = 60;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v4.app.FragmentActivity, android.support.v4.app.BaseFragmentActivityDonut, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT < 21) {
            CookieSyncManager.createInstance(this);
        }
        this.mScannerService = IMEApplication.from(getApplicationContext()).getConnect().getScannerService();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SuppressLint({"SetJavaScriptEnabled"})
    public void initWebView() {
        requestWindowFeature(1);
        this.container = new LinearLayout(this);
        addContentView(this.container, new LinearLayout.LayoutParams(-1, -1));
        this.webView = new WebView(this);
        this.webView.setVerticalScrollBarEnabled(false);
        this.webView.setHorizontalScrollBarEnabled(false);
        this.webView.setWebViewClient(new WebViewClientCallback());
        this.webView.getSettings().setJavaScriptEnabled(true);
        this.webView.getSettings().setSavePassword(false);
        this.webView.getSettings().setAllowFileAccess(false);
        this.webView.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        this.container.addView(this.webView);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        this.activityPaused = false;
        this.spinner = new ProgressDialog(this);
        this.spinner.setMessage("Loading...");
        this.spinner.setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: com.nuance.sns.SocialNetworkActivity.1
            @Override // android.content.DialogInterface.OnCancelListener
            public void onCancel(DialogInterface dialog) {
                SocialNetworkActivity.this.closing();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    public void onPause() {
        super.onPause();
        dismissSpinner();
        this.activityPaused = true;
        if (Build.VERSION.SDK_INT < 21) {
            CookieSyncManager.getInstance().sync();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        super.onDestroy();
        destroyWebView();
        destroySpinner();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Type inference failed for: r0v2, types: [com.nuance.sns.SocialNetworkActivity$2] */
    public void sendAuthUrlRequest() {
        log.d("sendAuthUrlRequest...");
        if (this.sentAuthUrlRequest) {
            throw new IllegalStateException("sendAuthUrlRequest(): already started");
        }
        this.sentAuthUrlRequest = true;
        showSpinner();
        new Thread() { // from class: com.nuance.sns.SocialNetworkActivity.2
            /* JADX WARN: Type inference failed for: r3v1, types: [com.nuance.sns.SocialNetworkActivity$2$1] */
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                String result = null;
                try {
                    result = SocialNetworkActivity.this.onRequestAuthorizationUrl();
                } catch (Exception e) {
                    SocialNetworkActivity.log.e(e.getMessage());
                }
                SocialNetworkActivity.this.runOnUiThread(new Runnable() { // from class: com.nuance.sns.SocialNetworkActivity.2.1
                    private String url;

                    Runnable init(String url) {
                        this.url = url;
                        return this;
                    }

                    @Override // java.lang.Runnable
                    public void run() {
                        SocialNetworkActivity.this.authUrl = this.url != null ? this.url : "";
                        SocialNetworkActivity.this.processAuthUrlResult();
                    }
                }.init(result));
            }
        }.start();
    }

    protected void processAuthUrlResult() {
        log.d("processAuthUrlResult... authUrl:", this.authUrl);
        if (!this.webViewLoaded && this.webView != null && this.authUrl != null) {
            if (this.authUrl.length() > 0) {
                if (!this.activityPaused) {
                    this.webViewLoaded = true;
                    this.webView.loadUrl(this.authUrl);
                    return;
                }
                return;
            }
            closing();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void showAuthorizationLogin() {
        log.d("showAuthorizationLogin...");
        if (!this.sentAuthUrlRequest) {
            sendAuthUrlRequest();
        } else {
            processAuthUrlResult();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void closing() {
        if (!isFinishing()) {
            dismissSpinner();
            finish();
        }
    }

    private void destroyWebView() {
        if (this.webView != null) {
            this.webView.stopLoading();
            this.container.removeView(this.webView);
            this.webView.removeAllViews();
            this.webView.destroy();
            this.webView = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void dismissSpinner() {
        if (this.spinner != null && this.spinner.isShowing()) {
            this.spinner.dismiss();
        }
    }

    private void destroySpinner() {
        dismissSpinner();
        this.spinner = null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void showSpinner() {
        if (this.spinner != null && !this.spinner.isShowing()) {
            this.spinner.show();
        }
    }

    /* loaded from: classes.dex */
    private class WebViewClientCallback extends WebViewClient {
        private boolean logout;

        private WebViewClientCallback() {
        }

        @Override // android.webkit.WebViewClient
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            SocialNetworkActivity.log.i("shouldOverrideUrlLoading url  " + url);
            return SocialNetworkActivity.this.onHandleUrlCallback(url);
        }

        @Override // android.webkit.WebViewClient
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            SocialNetworkActivity.log.e("onReceivedError : " + description);
            SocialNetworkActivity.this.closing();
        }

        @Override // android.webkit.WebViewClient
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            SocialNetworkActivity.log.i("onPageStarted url  " + url);
            SocialNetworkActivity.this.showSpinner();
            Uri uri = Uri.parse(url);
            if (SocialNetworkActivity.LOGOUT_SEG.equals(uri.getLastPathSegment())) {
                SocialNetworkActivity.this.sentAuthUrlRequest = false;
                this.logout = true;
            }
        }

        @Override // android.webkit.WebViewClient
        public void onPageFinished(WebView view, String url) {
            SocialNetworkActivity.log.i("onPageFinished url  " + url);
            if (!this.logout) {
                SocialNetworkActivity.this.dismissSpinner();
            } else {
                this.logout = false;
            }
        }
    }

    protected boolean onHandleUrlCallback(String url) {
        return false;
    }

    protected String onRequestAuthorizationUrl() {
        return null;
    }

    /* loaded from: classes.dex */
    protected class ScannerCallBack implements ACScannerService.ACScannerCallback {
        /* JADX INFO: Access modifiers changed from: protected */
        public ScannerCallBack() {
        }

        @Override // com.nuance.swypeconnect.ac.ACScannerService.ACScannerCallback
        public void onStart() {
            SocialNetworkActivity.this.updateWorkingStatus(SocialNetworkActivity.this.scraperStatus, SocialNetworkActivity.this.getStringLastRun());
        }

        @Override // com.nuance.swypeconnect.ac.ACScannerService.ACScannerCallback
        public void onFinish() {
            SocialNetworkActivity.this.updateFinishedStatus(SocialNetworkActivity.this.scraperStatus, SocialNetworkActivity.this.getStringLastRun());
            SocialNetworkActivity.this.releaseScanner();
            SocialNetworkActivity.this.dismissSpinner();
            SocialNetworkActivity.this.closing();
        }

        @Override // com.nuance.swypeconnect.ac.ACScannerService.ACScannerCallback
        public void onFailure(int reasonCode, String message) {
            SocialNetworkActivity.log.e("AbstactScannerCallBack.onFailure() reason=" + reasonCode + "; message=" + message);
            SocialNetworkActivity.this.updateErrorStatus(SocialNetworkActivity.this.scraperStatus, SocialNetworkActivity.this.getStringLastRun());
            SocialNetworkActivity.this.releaseScanner();
            SocialNetworkActivity.this.dismissSpinner();
            SocialNetworkActivity.this.closing();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean startScanningService() {
        IMEApplication imeApp = IMEApplication.from(getApplicationContext());
        SwypeCoreLibrary coreLibrary = imeApp.getSwypeCoreLibMgr().getSwypeCoreLibInstance();
        if (this.mScannerService == null || this.mScanner.get() == null || coreLibrary.getAlphaCoreInstance() == null || imeApp.getConnect() == null || !imeApp.getConnect().isCreated()) {
            log.e("CAN NOT DO SCANNING!");
            return false;
        }
        if (IMEApplication.from(getApplicationContext()).getInputMethods().getCurrentInputLanguage().isKoreanLanguage()) {
            if (coreLibrary.getKoreanCoreInstance(coreLibrary.getAlphaCoreInstance()) == null) {
                log.e("Swype has no core engine for Korean.");
                return false;
            }
            ACCoreInputDLM.initializeACKoreanInput(imeApp.getConnect());
            this.mScanner.get().setScanCore(new int[]{2});
        } else {
            ACCoreInputDLM.initializeACAlphaInput(imeApp.getConnect());
            this.mScanner.get().setScanCore(new int[]{1});
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void updateWorkingStatus(ScraperStatus statusPreferences, String time) {
        statusPreferences.setScrapStatus(this, ScraperStatus.formatStatusValue(ScraperStatus.SCRAPING_STATUS_WORKING, time));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void updateErrorStatus(ScraperStatus statusPreferences, String time) {
        statusPreferences.setScrapStatus(this, ScraperStatus.formatStatusValue(ScraperStatus.SCRAPING_STATUS_FAILED, time));
    }

    protected void updateFinishedStatus(ScraperStatus statusPreferences, String time) {
        statusPreferences.setScrapStatus(this, ScraperStatus.formatStatusValue(ScraperStatus.SCRAPING_STATUS_FINISHED, time));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String getStringDateNow() {
        return Long.toString(new Date().getTime());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String getStringLastRun() {
        return (this.mScanner == null || this.mScanner.get() == null || this.mScanner.get().getLastRun() == null) ? getStringDateNow() : Long.toString(this.mScanner.get().getLastRun().getTime().getTime());
    }

    @Override // android.app.Activity
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void releaseScanner() {
        if (this.mScanner != null) {
            this.mScanner.clear();
        }
    }
}
