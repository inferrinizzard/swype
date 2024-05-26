package com.nuance.sns;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.zzab;
import com.google.android.gms.internal.zzqi;
import com.google.api.services.gmail.GmailScopes;
import com.nuance.sns.SocialNetworkActivity;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.R;
import com.nuance.swype.usagedata.UsageData;
import com.nuance.swype.util.LogManager;
import com.nuance.swypeconnect.ac.ACScannerException;
import com.nuance.swypeconnect.ac.ACScannerGmail2;
import com.nuance.swypeconnect.ac.ACScannerService;
import java.io.IOException;
import java.lang.ref.WeakReference;

/* loaded from: classes.dex */
public class GmailActivity extends SocialNetworkActivity implements GoogleApiClient.OnConnectionFailedListener {
    private static final int RC_SIGN_IN = 0;
    private static final int REQUEST_GOOGLE_PLAY_SERVICES = 100;
    private static final String STATE_SIGNING_IN = "signing_in";
    private static final LogManager.Log log = LogManager.getLog("GmailActivity");
    private String gmailAccount;
    private String idToken;
    private GoogleApiClient mGoogleApiClient;
    private int retryCount = 0;
    private boolean isSigningIn = false;
    private boolean isGoogleServiceAvailable = true;

    static /* synthetic */ int access$008(GmailActivity x0) {
        int i = x0.retryCount;
        x0.retryCount = i + 1;
        return i;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.sns.SocialNetworkActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.BaseFragmentActivityDonut, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getBaseContext());
        if (status != 0) {
            this.isGoogleServiceAvailable = false;
            log.e("Google play service is unavailable!");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog$2675af88(this, status);
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.nuance.sns.GmailActivity.1
                @Override // android.content.DialogInterface.OnDismissListener
                public void onDismiss(DialogInterface dialogInterface) {
                    GmailActivity.this.closing();
                }
            });
            dialog.show();
            return;
        }
        setContentView(R.layout.activity_dummy);
        GoogleSignInOptions.Builder builder = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN);
        builder.dT.add(GoogleSignInOptions.dL);
        GoogleSignInOptions gso = builder.requestScopes(new Scope(GmailScopes.GMAIL_READONLY), new Scope[0]).build();
        GoogleApiClient.Builder builder2 = new GoogleApiClient.Builder(this);
        zzqi zzqiVar = new zzqi(this);
        zzab.zzb(true, (Object) "clientId must be non-negative");
        builder2.sf = 0;
        builder2.sg = this;
        builder2.se = zzqiVar;
        this.mGoogleApiClient = builder2.addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();
        this.isSigningIn = savedInstanceState != null && savedInstanceState.getBoolean(STATE_SIGNING_IN, false);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    public void onStart() {
        super.onStart();
        if (this.isGoogleServiceAvailable) {
            checkSignIn();
        }
    }

    private void checkSignIn() {
        this.retryCount = 0;
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(this.mGoogleApiClient);
        if (opr.isDone()) {
            log.d("Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            showSpinner();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() { // from class: com.nuance.sns.GmailActivity.2
                @Override // com.google.android.gms.common.api.ResultCallback
                public void onResult(GoogleSignInResult googleSignInResult) {
                    GmailActivity.this.dismissSpinner();
                    if (!googleSignInResult.bY.isSuccess()) {
                        if (GmailActivity.this.retryCount == 0) {
                            GmailActivity.log.d("slient login Failed... do signIn");
                            GmailActivity.access$008(GmailActivity.this);
                            GmailActivity.this.signIn();
                            return;
                        }
                        GmailActivity.this.closing();
                        return;
                    }
                    GmailActivity.this.handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void signIn() {
        if (!this.isSigningIn) {
            this.isSigningIn = true;
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(this.mGoogleApiClient);
            startActivityForResult(signInIntent, 0);
        }
    }

    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.isSigningIn = false;
        if (requestCode == 0) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            log.d("onActivityResult()...");
            handleSignInResult(result);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initScanner() {
        this.scraperStatus = IMEApplication.from(getApplicationContext()).getScraperStatusFactory().getGmailStatusPreference();
        if (this.mScannerService != null) {
            try {
                this.mScanner = new WeakReference<>(this.mScannerService.getScanner(ACScannerService.ScannerType.GMAIL2));
                if (this.mScanner.get() != null) {
                    ((ACScannerGmail2) this.mScanner.get()).setScanType(new ACScannerGmail2.ACScannerGmailType[]{ACScannerGmail2.ACScannerGmailType.SENT});
                    ((ACScannerGmail2) this.mScanner.get()).setScanContentType(new ACScannerGmail2.ACScannerGmailContentType[]{ACScannerGmail2.ACScannerGmailContentType.TO});
                    this.mScanner.get().setMaxToProcess(50);
                }
            } catch (ACScannerException e) {
                log.e(e.getMessage());
                updateErrorStatus(this.scraperStatus, getStringLastRun());
                closing();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleSignInResult(GoogleSignInResult result) {
        String str;
        if (result != null) {
            log.d("handleSignInResult:" + result.bY.isSuccess());
            if (!result.bY.isSuccess()) {
                log.e("Sign in failed");
                closing();
                return;
            }
            GoogleSignInAccount acct = result.dU;
            if (acct == null) {
                str = null;
            } else {
                str = acct.dB;
            }
            this.gmailAccount = str;
            log.d("gmailAccount: " + this.gmailAccount);
            if (this.gmailAccount != null) {
                new Thread(new Runnable() { // from class: com.nuance.sns.GmailActivity.3
                    @Override // java.lang.Runnable
                    public void run() {
                        try {
                            GmailActivity.this.idToken = GoogleAuthUtil.getToken(GmailActivity.this.getApplicationContext(), GmailActivity.this.gmailAccount, "oauth2:https://www.googleapis.com/auth/gmail.readonly");
                            GmailActivity.log.d("Token: " + GmailActivity.this.idToken);
                            if (GmailActivity.this.idToken != null) {
                                GmailActivity.this.runOnUiThread(new Runnable() { // from class: com.nuance.sns.GmailActivity.3.1
                                    @Override // java.lang.Runnable
                                    public void run() {
                                        GmailActivity.this.initScanner();
                                        GmailActivity.this.closing();
                                        GmailActivity.this.startScanningService();
                                    }
                                });
                            }
                        } catch (GoogleAuthException e) {
                            GmailActivity.log.e("GoogleAuthException: " + e.getMessage());
                            try {
                                Auth.GoogleSignInApi.signOut(GmailActivity.this.mGoogleApiClient);
                            } catch (Exception e2) {
                                GmailActivity.log.e("Runtime exception: " + e.getMessage());
                            } finally {
                                GmailActivity.this.runOnUiThread(new Runnable() { // from class: com.nuance.sns.GmailActivity.3.2
                                    @Override // java.lang.Runnable
                                    public void run() {
                                        GmailActivity.this.signIn();
                                    }
                                });
                            }
                        } catch (IOException e3) {
                            GmailActivity.log.e(e3.getMessage());
                            GmailActivity.this.closing();
                        }
                    }
                }).start();
            }
        }
    }

    @Override // com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener
    public void onConnectionFailed(ConnectionResult connectionResult) {
        log.e("onConnectionFailed:" + connectionResult);
        int errorCode = connectionResult.ok;
        Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog$2675af88(this, errorCode);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.nuance.sns.GmailActivity.4
            @Override // android.content.DialogInterface.OnDismissListener
            public void onDismiss(DialogInterface dialogInterface) {
                GmailActivity.this.closing();
            }
        });
        dialog.show();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.sns.SocialNetworkActivity
    public boolean startScanningService() {
        if (super.startScanningService()) {
            try {
                ((ACScannerGmail2) this.mScanner.get()).setAccount(this.gmailAccount, this.idToken);
                this.mScanner.get().start(new SocialNetworkActivity.ScannerCallBack());
                UsageData.recordUsingGmailScanner();
                return true;
            } catch (ACScannerException e) {
                log.e("ACScannerException: " + e.getMessage());
            }
        }
        updateErrorStatus(this.scraperStatus, getStringLastRun());
        closing();
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_SIGNING_IN, this.isSigningIn);
    }
}
