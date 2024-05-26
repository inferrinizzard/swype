package com.nuance.sns;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import com.nuance.sns.SocialNetworkActivity;
import com.nuance.swype.input.BuildInfo;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.util.LogManager;
import com.nuance.swypeconnect.ac.ACScannerException;
import com.nuance.swypeconnect.ac.ACScannerService;
import com.nuance.swypeconnect.ac.ACScannerTwitter;
import java.lang.ref.WeakReference;

/* loaded from: classes.dex */
public class TwitterActivity extends SocialNetworkActivity {
    private static final LogManager.Log log = LogManager.getLog("TwitterActivity");
    private Pair<String, String> accessToken;
    private String consumer_key;
    private String consumer_secret;
    private String requestToken;
    private ACScannerService.ACScannerCallback twitterCallback;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.sns.SocialNetworkActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.BaseFragmentActivityDonut, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWebView();
        this.scraperStatus = IMEApplication.from(getApplicationContext()).getScraperStatusFactory().getTwitterStatusPreference();
        this.twitterCallback = new SocialNetworkActivity.ScannerCallBack();
        this.consumer_key = BuildInfo.from(getApplicationContext()).getTwitterAPIKey();
        this.consumer_secret = BuildInfo.from(getApplicationContext()).getTwitterAPISecret();
        if (this.mScannerService != null) {
            try {
                this.mScanner = new WeakReference<>(this.mScannerService.getScanner(ACScannerService.ScannerType.TWITTER));
                if (this.mScanner.get() != null) {
                    ((ACScannerTwitter) this.mScanner.get()).setScanType(new ACScannerTwitter.ACTwitterScannerType[]{ACScannerTwitter.ACTwitterScannerType.DIRECT_MESSAGES, ACScannerTwitter.ACTwitterScannerType.FRIENDS, ACScannerTwitter.ACTwitterScannerType.MEMBERSHIPS, ACScannerTwitter.ACTwitterScannerType.STATUS});
                }
            } catch (ACScannerException e) {
                log.e(e.getMessage());
                updateErrorStatus(this.scraperStatus, getStringDateNow());
                closing();
            }
        }
    }

    @Override // com.nuance.sns.SocialNetworkActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        showAuthorizationLogin();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.sns.SocialNetworkActivity
    public boolean startScanningService() {
        if (!super.startScanningService() || this.mScanner.get() == null) {
            return false;
        }
        try {
            ((ACScannerTwitter) this.mScanner.get()).setTokens(this.consumer_key, this.consumer_secret, (String) this.accessToken.first, (String) this.accessToken.second);
            this.mScanner.get().start(this.twitterCallback);
            return true;
        } catch (ACScannerException e) {
            log.e("ACScannerException: " + e.getMessage());
            updateErrorStatus(this.scraperStatus, getStringDateNow());
            closing();
            return false;
        }
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [com.nuance.sns.TwitterActivity$1] */
    private void retrieveAccessToken(final String verifier) {
        new Thread() { // from class: com.nuance.sns.TwitterActivity.1
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                TwitterActivity.log.d("RequestToken: " + TwitterActivity.this.requestToken + " Verifier: " + verifier);
                TwitterActivity.this.accessToken = HttpUtils.getAccessToken(TwitterActivity.this.consumer_key, TwitterActivity.this.consumer_secret, TwitterActivity.this.requestToken, verifier);
                if (TwitterActivity.this.accessToken != null) {
                    TwitterActivity.this.runOnUiThread(new Runnable() { // from class: com.nuance.sns.TwitterActivity.1.1
                        @Override // java.lang.Runnable
                        public void run() {
                            TwitterActivity.this.closing();
                            TwitterActivity.this.startScanningService();
                        }
                    });
                }
            }
        }.start();
    }

    @Override // com.nuance.sns.SocialNetworkActivity
    public boolean onHandleUrlCallback(String url) {
        log.d("onHandleUrlCallback(): url: " + url);
        if (!TextUtils.isEmpty(url) && url.startsWith(HttpUtils.TWITTER_CALLBACK)) {
            Uri uri = Uri.parse(url);
            if (uri.getQueryParameter("denied") != null) {
                this.twitterCallback.onFailure(0, "Twitter: access denied");
                closing();
                return true;
            }
            if (uri.getQueryParameter("oauth_token") == null) {
                return true;
            }
            retrieveAccessToken(uri.getQueryParameter("oauth_verifier"));
            return true;
        }
        if (this.sentAuthUrlRequest) {
            return false;
        }
        this.webViewLoaded = false;
        sendAuthUrlRequest();
        return true;
    }

    @Override // com.nuance.sns.SocialNetworkActivity
    public String onRequestAuthorizationUrl() {
        log.d("onRequestAuthorizationUrl...");
        this.requestToken = HttpUtils.getRequestToken(this.consumer_key, this.consumer_secret);
        return HttpUtils.getAuthorizeUrl(this.requestToken);
    }
}
