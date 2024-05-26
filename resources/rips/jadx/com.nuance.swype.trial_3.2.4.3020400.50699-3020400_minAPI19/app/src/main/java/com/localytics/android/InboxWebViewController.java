package com.localytics.android;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import com.localytics.android.CreativeManager;
import com.localytics.android.Localytics;
import com.localytics.android.MarketingWebView;
import java.io.File;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class InboxWebViewController implements Handler.Callback {
    static final String ARG_CAMPAIGN = "arg_campaign";
    private static final int NUM_CREATIVE_LOAD_TRIES = 3;
    private static final long RETRY_TIMEOUT = 1000;
    private InboxDetailCallback mActivityCallback;
    private InboxCampaign mCampaign;
    private InboxErrorImage mErrorImage;
    private boolean mIsAttached;
    private JavaScriptClient mJavaScriptClient;
    private Handler mMessageHandler;
    private ProgressBar mProgressBar;
    private MarketingWebView mWebView;
    private MarketingWebViewManager mWebViewManager;

    /* JADX INFO: Access modifiers changed from: package-private */
    public MarketingWebViewManager getWebViewManager() {
        return this.mWebViewManager;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public InboxCampaign getCampaign() {
        return this.mCampaign;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Multi-variable type inference failed */
    public void onAttach(Context context) {
        if (this.mWebViewManager == null) {
            this.mWebViewManager = new MarketingWebViewManager(LocalyticsManager.getInstance());
        }
        this.mWebViewManager.setContext(context);
        this.mIsAttached = true;
        if (context instanceof InboxDetailCallback) {
            this.mActivityCallback = (InboxDetailCallback) context;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onDetach() {
        this.mWebViewManager.setContext(null);
        this.mIsAttached = false;
        this.mActivityCallback = null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @TargetApi(11)
    public void onCreate(InboxDetailFragment fragment) {
        onCreate(fragment, fragment.getArguments());
        fragment.setRetainInstance(true);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onCreate(InboxDetailSupportFragment fragment) {
        onCreate(fragment, fragment.getArguments());
        fragment.setRetainInstance(true);
    }

    private void onCreate(Object fragment, Bundle arguments) {
        if (arguments != null) {
            InboxCampaign inboxCampaign = (InboxCampaign) arguments.getParcelable(ARG_CAMPAIGN);
            this.mCampaign = inboxCampaign;
            if (inboxCampaign != null) {
                this.mWebViewManager.setCampaign(this.mCampaign);
                this.mJavaScriptClient = this.mWebViewManager.getJavaScriptClient(null);
                Localytics.setInboxDetailFragmentDisplaying(fragment, true);
                this.mMessageHandler = new Handler(this);
                this.mWebViewManager.setMessageHandler(this.mMessageHandler);
                return;
            }
        }
        throw new RuntimeException("You must use InboxDetailFragment.newInstance(InboxCampaign campaign) and use FragmentManager to add it to your layout");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onDestroy(Object fragment) {
        Localytics.setInboxDetailFragmentDisplaying(fragment, false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @TargetApi(11)
    public View onCreateView(InboxDetailFragment fragment) {
        return onCreateView(fragment.getActivity());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public View onCreateView(Fragment fragment) {
        return onCreateView(fragment.getContext());
    }

    private View onCreateView(Context context) {
        FrameLayout rootView = new FrameLayout(context);
        rootView.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
        rootView.setBackgroundColor(0);
        FrameLayout.LayoutParams webViewParams = new FrameLayout.LayoutParams(-1, -1);
        webViewParams.gravity = 17;
        this.mWebView = new MarketingWebView(context, webViewParams);
        rootView.addView(this.mWebView);
        this.mWebView.setWebViewClient(new InboxWebViewClient(this.mWebViewManager));
        this.mWebView.addJavascriptInterface(this.mJavaScriptClient, "localytics");
        this.mWebView.setOnKeyListener(new View.OnKeyListener() { // from class: com.localytics.android.InboxWebViewController.1
            @Override // android.view.View.OnKeyListener
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == 0) {
                    WebView webView = (WebView) v;
                    switch (keyCode) {
                        case 4:
                            if (webView.canGoBack()) {
                                webView.goBack();
                                return true;
                            }
                        default:
                            return false;
                    }
                }
                return false;
            }
        });
        this.mProgressBar = new ProgressBar(context);
        FrameLayout.LayoutParams progressParams = new FrameLayout.LayoutParams(-2, -2);
        progressParams.gravity = 17;
        this.mProgressBar.setLayoutParams(progressParams);
        this.mProgressBar.setVisibility(8);
        rootView.addView(this.mProgressBar);
        this.mErrorImage = new InboxErrorImage(context);
        FrameLayout.LayoutParams errorImageParams = new FrameLayout.LayoutParams(Utils.dpToPx(32, context), Utils.dpToPx(32, context));
        errorImageParams.gravity = 17;
        this.mErrorImage.setLayoutParams(errorImageParams);
        this.mErrorImage.setVisibility(8);
        rootView.addView(this.mErrorImage);
        loadCreative(3);
        return rootView;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void loadCreative(final int numRetries) {
        final String url = this.mCampaign.getWebViewAttributes().get("html_url");
        if (!TextUtils.isEmpty(url)) {
            if (creativePathExists()) {
                this.mMessageHandler.obtainMessage(2, url).sendToTarget();
                return;
            } else if (numRetries > 0) {
                this.mProgressBar.setVisibility(0);
                Localytics.priorityDownloadCreative(this.mCampaign, new CreativeManager.FirstDownloadedCallback() { // from class: com.localytics.android.InboxWebViewController.2
                    @Override // com.localytics.android.CreativeManager.FirstDownloadedCallback
                    public void onFirstDownloaded() {
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() { // from class: com.localytics.android.InboxWebViewController.2.1
                            @Override // java.lang.Runnable
                            public void run() {
                                try {
                                    if (InboxWebViewController.this.mIsAttached) {
                                        if (InboxWebViewController.this.creativePathExists()) {
                                            InboxWebViewController.this.mProgressBar.setVisibility(8);
                                            InboxWebViewController.this.mMessageHandler.obtainMessage(2, url).sendToTarget();
                                        } else {
                                            InboxWebViewController.this.loadCreative(numRetries - 1);
                                        }
                                    }
                                } catch (Exception e) {
                                    Localytics.Log.e("InboxDetailFragment loadCreative exception", e);
                                }
                            }
                        }, 1000L);
                    }
                });
                return;
            } else {
                showError();
                return;
            }
        }
        showError();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean creativePathExists() {
        String url = this.mCampaign.getWebViewAttributes().get("html_url");
        return new File(Uri.parse(url).getPath()).exists();
    }

    private void showError() {
        this.mProgressBar.setVisibility(8);
        if (this.mActivityCallback != null) {
            this.mActivityCallback.onCreativeLoadError();
        } else {
            this.mErrorImage.setVisibility(0);
        }
    }

    @Override // android.os.Handler.Callback
    public boolean handleMessage(Message msg) {
        try {
            switch (msg.what) {
                case 2:
                    this.mWebView.loadUrl((String) msg.obj);
                    break;
            }
        } catch (Exception e) {
            Localytics.Log.e(String.format("Main handler can't handle message %s", String.valueOf(msg.what)), e);
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public final class InboxWebViewClient extends MarketingWebView.MarketingWebViewClient {
        InboxWebViewClient(MarketingWebViewManager manager) {
            super(manager);
        }

        @Override // android.webkit.WebViewClient
        public final void onPageFinished(WebView view, String url) {
            Localytics.Log.v("[InboxDetailFragment]: onPageFinished");
            String javascript = InboxWebViewController.this.mJavaScriptClient.getJavaScriptBridge();
            view.loadUrl(javascript);
        }
    }
}
