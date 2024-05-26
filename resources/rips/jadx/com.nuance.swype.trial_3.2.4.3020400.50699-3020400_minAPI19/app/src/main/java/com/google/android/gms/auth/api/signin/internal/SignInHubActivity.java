package com.google.android.gms.auth.api.signin.internal;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.SignInAccount;
import com.google.android.gms.common.annotation.KeepName;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;

@KeepName
/* loaded from: classes.dex */
public class SignInHubActivity extends FragmentActivity {
    private zzk ej;
    private SignInConfiguration ek;
    private boolean el;
    private int em;
    private Intent en;

    private void zzagi() {
        getSupportLoaderManager().initLoader$71be8de6(new zza(this, (byte) 0));
    }

    private void zzde(int i) {
        Status status = new Status(i);
        Intent intent = new Intent();
        intent.putExtra("googleSignInStatus", status);
        setResult(0, intent);
        finish();
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean("signingInGoogleApiClients", this.el);
        if (this.el) {
            bundle.putInt("signInResultCode", this.em);
            bundle.putParcelable("signInResultData", this.en);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class zza implements LoaderManager.LoaderCallbacks<Void> {
        private zza() {
        }

        /* synthetic */ zza(SignInHubActivity signInHubActivity, byte b) {
            this();
        }

        @Override // android.support.v4.app.LoaderManager.LoaderCallbacks
        public final Loader<Void> onCreateLoader$e57f803() {
            return new zzb(SignInHubActivity.this, GoogleApiClient.zzaoe());
        }

        @Override // android.support.v4.app.LoaderManager.LoaderCallbacks
        public final /* synthetic */ void onLoadFinished$13079eae() {
            SignInHubActivity.this.setResult(SignInHubActivity.this.em, SignInHubActivity.this.en);
            SignInHubActivity.this.finish();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v4.app.FragmentActivity, android.support.v4.app.BaseFragmentActivityDonut, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.ej = zzk.zzbc(this);
        Intent intent = getIntent();
        if (!"com.google.android.gms.auth.GOOGLE_SIGN_IN".equals(intent.getAction())) {
            String valueOf = String.valueOf(intent.getAction());
            Log.e("AuthSignInClient", valueOf.length() != 0 ? "Unknown action: ".concat(valueOf) : new String("Unknown action: "));
            finish();
        }
        this.ek = (SignInConfiguration) intent.getParcelableExtra("config");
        if (this.ek == null) {
            Log.e("AuthSignInClient", "Activity started with invalid configuration.");
            setResult(0);
            finish();
            return;
        }
        if (bundle != null) {
            this.el = bundle.getBoolean("signingInGoogleApiClients");
            if (this.el) {
                this.em = bundle.getInt("signInResultCode");
                this.en = (Intent) bundle.getParcelable("signInResultData");
                zzagi();
                return;
            }
            return;
        }
        Intent intent2 = new Intent("com.google.android.gms.auth.GOOGLE_SIGN_IN");
        intent2.setPackage("com.google.android.gms");
        intent2.putExtra("config", this.ek);
        try {
            startActivityForResult(intent2, 40962);
        } catch (ActivityNotFoundException e) {
            Log.w("AuthSignInClient", "Could not launch sign in Intent. Google Play Service is probably being updated...");
            zzde(8);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    public void onActivityResult(int i, int i2, Intent intent) {
        setResult(0);
        switch (i) {
            case 40962:
                if (intent != null) {
                    SignInAccount signInAccount = (SignInAccount) intent.getParcelableExtra("signInAccount");
                    if (signInAccount != null && signInAccount.dV != null) {
                        GoogleSignInAccount googleSignInAccount = signInAccount.dV;
                        this.ej.zzb(googleSignInAccount, this.ek.ei);
                        intent.removeExtra("signInAccount");
                        intent.putExtra("googleSignInAccount", googleSignInAccount);
                        this.el = true;
                        this.em = i2;
                        this.en = intent;
                        zzagi();
                        return;
                    }
                    if (intent.hasExtra("errorCode")) {
                        zzde(intent.getIntExtra("errorCode", 8));
                        return;
                    }
                }
                zzde(8);
                return;
            default:
                return;
        }
    }
}
