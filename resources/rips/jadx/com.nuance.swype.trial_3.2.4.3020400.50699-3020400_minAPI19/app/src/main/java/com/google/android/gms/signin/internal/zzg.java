package com.google.android.gms.signin.internal;

import android.accounts.Account;
import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.internal.ResolveAccountRequest;
import com.google.android.gms.common.internal.zzab;
import com.google.android.gms.common.internal.zzd;
import com.google.android.gms.common.internal.zzk;
import com.google.android.gms.common.internal.zzq;
import com.google.android.gms.internal.zzvu;
import com.google.android.gms.signin.internal.zze;

/* loaded from: classes.dex */
public final class zzg extends zzk<zze> implements zzvu {
    private final boolean aub;
    private final Bundle auc;
    private final com.google.android.gms.common.internal.zzg tN;
    private Integer ym;

    @Override // com.google.android.gms.internal.zzvu
    public final void connect() {
        zza(new zzd.zzi());
    }

    @Override // com.google.android.gms.internal.zzvu
    public final void zza(zzq zzqVar, boolean z) {
        try {
            ((zze) zzasa()).zza(zzqVar, this.ym.intValue(), z);
        } catch (RemoteException e) {
            Log.w("SignInClientImpl", "Remote service probably died when saveDefaultAccount is called");
        }
    }

    @Override // com.google.android.gms.common.internal.zzd, com.google.android.gms.common.api.Api.zze
    public final boolean zzafk() {
        return this.aub;
    }

    @Override // com.google.android.gms.internal.zzvu
    public final void zzbzo() {
        try {
            ((zze) zzasa()).zzza(this.ym.intValue());
        } catch (RemoteException e) {
            Log.w("SignInClientImpl", "Remote service probably died when clearAccountFromSessionStore is called");
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.gms.common.internal.zzd
    public final String zzqz() {
        return "com.google.android.gms.signin.service.START";
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.gms.common.internal.zzd
    public final String zzra() {
        return "com.google.android.gms.signin.internal.ISignInService";
    }

    public zzg(Context context, Looper looper, boolean z, com.google.android.gms.common.internal.zzg zzgVar, Bundle bundle, GoogleApiClient.ConnectionCallbacks connectionCallbacks, GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener) {
        super(context, looper, 44, zzgVar, connectionCallbacks, onConnectionFailedListener);
        this.aub = z;
        this.tN = zzgVar;
        this.auc = bundle;
        this.ym = zzgVar.ym;
    }

    /* JADX WARN: Illegal instructions before constructor call */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public zzg(android.content.Context r9, android.os.Looper r10, com.google.android.gms.common.internal.zzg r11, com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks r12, com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener r13) {
        /*
            r8 = this;
            r3 = 1
            com.google.android.gms.internal.zzvv r0 = r11.yl
            java.lang.Integer r1 = r11.ym
            android.os.Bundle r5 = new android.os.Bundle
            r5.<init>()
            java.lang.String r2 = "com.google.android.gms.signin.internal.clientRequestedAccount"
            android.accounts.Account r4 = r11.aL
            r5.putParcelable(r2, r4)
            if (r1 == 0) goto L1e
            java.lang.String r2 = "com.google.android.gms.common.internal.ClientSettings.sessionId"
            int r1 = r1.intValue()
            r5.putInt(r2, r1)
        L1e:
            if (r0 == 0) goto L76
            java.lang.String r1 = "com.google.android.gms.signin.internal.offlineAccessRequested"
            boolean r2 = r0.atS
            r5.putBoolean(r1, r2)
            java.lang.String r1 = "com.google.android.gms.signin.internal.idTokenRequested"
            boolean r2 = r0.dO
            r5.putBoolean(r1, r2)
            java.lang.String r1 = "com.google.android.gms.signin.internal.serverClientId"
            java.lang.String r2 = r0.dR
            r5.putString(r1, r2)
            java.lang.String r1 = "com.google.android.gms.signin.internal.usePromptModeForAuthCode"
            r5.putBoolean(r1, r3)
            java.lang.String r1 = "com.google.android.gms.signin.internal.forceCodeForRefreshToken"
            boolean r2 = r0.dQ
            r5.putBoolean(r1, r2)
            java.lang.String r1 = "com.google.android.gms.signin.internal.hostedDomain"
            java.lang.String r2 = r0.dS
            r5.putString(r1, r2)
            java.lang.String r1 = "com.google.android.gms.signin.internal.waitForAccessTokenRefresh"
            boolean r2 = r0.atT
            r5.putBoolean(r1, r2)
            java.lang.Long r1 = r0.atU
            if (r1 == 0) goto L66
            java.lang.String r1 = "com.google.android.gms.signin.internal.authApiSignInModuleVersion"
            java.lang.Long r2 = r0.atU
            long r6 = r2.longValue()
            r5.putLong(r1, r6)
        L66:
            java.lang.Long r1 = r0.atV
            if (r1 == 0) goto L76
            java.lang.String r1 = "com.google.android.gms.signin.internal.realClientLibraryVersion"
            java.lang.Long r0 = r0.atV
            long r6 = r0.longValue()
            r5.putLong(r1, r6)
        L76:
            r0 = r8
            r1 = r9
            r2 = r10
            r4 = r11
            r6 = r12
            r7 = r13
            r0.<init>(r1, r2, r3, r4, r5, r6, r7)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.signin.internal.zzg.<init>(android.content.Context, android.os.Looper, com.google.android.gms.common.internal.zzg, com.google.android.gms.common.api.GoogleApiClient$ConnectionCallbacks, com.google.android.gms.common.api.GoogleApiClient$OnConnectionFailedListener):void");
    }

    @Override // com.google.android.gms.internal.zzvu
    public final void zza(zzd zzdVar) {
        zzab.zzb(zzdVar, "Expecting a valid ISignInCallbacks");
        try {
            com.google.android.gms.common.internal.zzg zzgVar = this.tN;
            Account account = zzgVar.aL != null ? zzgVar.aL : new Account("<<default account>>", "com.google");
            ((zze) zzasa()).zza(new SignInRequest(new ResolveAccountRequest(account, this.ym.intValue(), "<<default account>>".equals(account.name) ? com.google.android.gms.auth.api.signin.internal.zzk.zzbc(getContext()).zzagj() : null)), zzdVar);
        } catch (RemoteException e) {
            Log.w("SignInClientImpl", "Remote service probably died when signIn is called");
            try {
                zzdVar.zzb(new SignInResponse());
            } catch (RemoteException e2) {
                Log.wtf("SignInClientImpl", "ISignInCallbacks#onSignInComplete should be executed from the same process, unexpected RemoteException.", e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.gms.common.internal.zzd
    public final Bundle zzaeu() {
        if (!getContext().getPackageName().equals(this.tN.bX)) {
            this.auc.putString("com.google.android.gms.signin.internal.realClientPackageName", this.tN.bX);
        }
        return this.auc;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.gms.common.internal.zzd
    public final /* synthetic */ IInterface zzbb(IBinder iBinder) {
        return zze.zza.zzkv(iBinder);
    }
}
