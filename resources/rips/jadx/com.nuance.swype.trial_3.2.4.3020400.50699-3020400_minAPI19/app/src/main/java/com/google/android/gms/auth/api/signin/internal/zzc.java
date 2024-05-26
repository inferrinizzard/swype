package com.google.android.gms.auth.api.signin.internal;

import android.content.Intent;
import android.os.RemoteException;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzab;
import com.google.android.gms.internal.zzpm;
import java.util.Iterator;

/* loaded from: classes.dex */
public final class zzc implements GoogleSignInApi {

    /* loaded from: classes.dex */
    private abstract class zza<R extends Result> extends zzpm.zza<R, zzd> {
        public zza(GoogleApiClient googleApiClient) {
            super(Auth.GOOGLE_SIGN_IN_API, googleApiClient);
        }
    }

    @Override // com.google.android.gms.auth.api.signin.GoogleSignInApi
    public final Intent getSignInIntent(GoogleApiClient googleApiClient) {
        zzab.zzy(googleApiClient);
        return ((zzd) googleApiClient.zza(Auth.cA)).zzaga();
    }

    @Override // com.google.android.gms.auth.api.signin.GoogleSignInApi
    public final GoogleSignInResult getSignInResultFromIntent(Intent intent) {
        if (intent == null || !(intent.hasExtra("googleSignInStatus") || intent.hasExtra("googleSignInAccount"))) {
            return null;
        }
        GoogleSignInAccount googleSignInAccount = (GoogleSignInAccount) intent.getParcelableExtra("googleSignInAccount");
        Status status = (Status) intent.getParcelableExtra("googleSignInStatus");
        if (googleSignInAccount != null) {
            status = Status.sq;
        }
        return new GoogleSignInResult(googleSignInAccount, status);
    }

    @Override // com.google.android.gms.auth.api.signin.GoogleSignInApi
    public final PendingResult<Status> revokeAccess(GoogleApiClient googleApiClient) {
        zzk.zzbc(googleApiClient.getContext()).zzagl();
        Iterator<GoogleApiClient> it = GoogleApiClient.zzaoe().iterator();
        while (it.hasNext()) {
            it.next().zzaof();
        }
        return googleApiClient.zzd(new zza<Status>(googleApiClient) { // from class: com.google.android.gms.auth.api.signin.internal.zzc.3
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.google.android.gms.internal.zzpo
            public final /* synthetic */ Result zzc(Status status) {
                return status;
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.google.android.gms.internal.zzpm.zza
            public final /* bridge */ /* synthetic */ void zza(zzd zzdVar) throws RemoteException {
                zzd zzdVar2 = zzdVar;
                ((zzh) zzdVar2.zzasa()).zzc(new com.google.android.gms.auth.api.signin.internal.zza() { // from class: com.google.android.gms.auth.api.signin.internal.zzc.3.1
                    @Override // com.google.android.gms.auth.api.signin.internal.zza, com.google.android.gms.auth.api.signin.internal.zzg
                    public final void zzm(Status status) throws RemoteException {
                        zzc((AnonymousClass3) status);
                    }
                }, zzdVar2.ee);
            }
        });
    }

    @Override // com.google.android.gms.auth.api.signin.GoogleSignInApi
    public final PendingResult<Status> signOut(GoogleApiClient googleApiClient) {
        zzk.zzbc(googleApiClient.getContext()).zzagl();
        Iterator<GoogleApiClient> it = GoogleApiClient.zzaoe().iterator();
        while (it.hasNext()) {
            it.next().zzaof();
        }
        return googleApiClient.zzd(new zza<Status>(googleApiClient) { // from class: com.google.android.gms.auth.api.signin.internal.zzc.2
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.google.android.gms.internal.zzpo
            public final /* synthetic */ Result zzc(Status status) {
                return status;
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.google.android.gms.internal.zzpm.zza
            public final /* bridge */ /* synthetic */ void zza(zzd zzdVar) throws RemoteException {
                zzd zzdVar2 = zzdVar;
                ((zzh) zzdVar2.zzasa()).zzb(new com.google.android.gms.auth.api.signin.internal.zza() { // from class: com.google.android.gms.auth.api.signin.internal.zzc.2.1
                    @Override // com.google.android.gms.auth.api.signin.internal.zza, com.google.android.gms.auth.api.signin.internal.zzg
                    public final void zzl(Status status) throws RemoteException {
                        zzc((AnonymousClass2) status);
                    }
                }, zzdVar2.ee);
            }
        });
    }

    /* JADX WARN: Removed duplicated region for block: B:26:0x007a  */
    /* JADX WARN: Removed duplicated region for block: B:29:0x0099  */
    @Override // com.google.android.gms.auth.api.signin.GoogleSignInApi
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final com.google.android.gms.common.api.OptionalPendingResult<com.google.android.gms.auth.api.signin.GoogleSignInResult> silentSignIn(com.google.android.gms.common.api.GoogleApiClient r13) {
        /*
            r12 = this;
            r1 = 1
            r2 = 0
            com.google.android.gms.common.api.Api$zzf<com.google.android.gms.auth.api.signin.internal.zzd> r0 = com.google.android.gms.auth.api.Auth.cA
            com.google.android.gms.common.api.Api$zze r0 = r13.zza(r0)
            com.google.android.gms.auth.api.signin.internal.zzd r0 = (com.google.android.gms.auth.api.signin.internal.zzd) r0
            com.google.android.gms.auth.api.signin.GoogleSignInOptions r3 = r0.ee
            android.content.Context r0 = r13.getContext()
            com.google.android.gms.common.internal.zzab.zzy(r3)
            com.google.android.gms.auth.api.signin.internal.zzk r4 = com.google.android.gms.auth.api.signin.internal.zzk.zzbc(r0)
            com.google.android.gms.auth.api.signin.GoogleSignInOptions r5 = r4.zzagk()
            if (r5 == 0) goto L97
            android.accounts.Account r0 = r5.aL
            android.accounts.Account r6 = r3.aL
            if (r0 != 0) goto L90
            if (r6 != 0) goto L8e
            r0 = r1
        L26:
            if (r0 == 0) goto L97
            boolean r0 = r3.dP
            if (r0 != 0) goto L97
            boolean r0 = r3.dO
            if (r0 == 0) goto L3e
            boolean r0 = r5.dO
            if (r0 == 0) goto L97
            java.lang.String r0 = r3.dR
            java.lang.String r6 = r5.dR
            boolean r0 = r0.equals(r6)
            if (r0 == 0) goto L97
        L3e:
            java.util.HashSet r0 = new java.util.HashSet
            java.util.ArrayList r5 = r5.zzafq()
            r0.<init>(r5)
            java.util.HashSet r5 = new java.util.HashSet
            java.util.ArrayList r6 = r3.zzafq()
            r5.<init>(r6)
            boolean r0 = r0.containsAll(r5)
            if (r0 == 0) goto L97
            com.google.android.gms.auth.api.signin.GoogleSignInAccount r4 = r4.zzagj()
            if (r4 == 0) goto L97
            com.google.android.gms.common.util.zze r0 = com.google.android.gms.auth.api.signin.GoogleSignInAccount.dA
            long r6 = r0.currentTimeMillis()
            r8 = 1000(0x3e8, double:4.94E-321)
            long r6 = r6 / r8
            long r8 = r4.dF
            r10 = 300(0x12c, double:1.48E-321)
            long r8 = r8 - r10
            int r0 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
            if (r0 < 0) goto L95
            r0 = r1
        L6f:
            if (r0 != 0) goto L97
            com.google.android.gms.auth.api.signin.GoogleSignInResult r0 = new com.google.android.gms.auth.api.signin.GoogleSignInResult
            com.google.android.gms.common.api.Status r1 = com.google.android.gms.common.api.Status.sq
            r0.<init>(r4, r1)
        L78:
            if (r0 == 0) goto L99
            java.lang.String r1 = "Result must not be null"
            com.google.android.gms.common.internal.zzab.zzb(r0, r1)
            com.google.android.gms.common.api.PendingResults$zzc r1 = new com.google.android.gms.common.api.PendingResults$zzc
            r1.<init>(r13)
            r1.zzc(r0)
            com.google.android.gms.internal.zzqq r0 = new com.google.android.gms.internal.zzqq
            r0.<init>(r1)
        L8d:
            return r0
        L8e:
            r0 = r2
            goto L26
        L90:
            boolean r0 = r0.equals(r6)
            goto L26
        L95:
            r0 = r2
            goto L6f
        L97:
            r0 = 0
            goto L78
        L99:
            com.google.android.gms.auth.api.signin.internal.zzc$1 r0 = new com.google.android.gms.auth.api.signin.internal.zzc$1
            r0.<init>(r13)
            com.google.android.gms.internal.zzpm$zza r1 = r13.zzc(r0)
            com.google.android.gms.internal.zzqq r0 = new com.google.android.gms.internal.zzqq
            r0.<init>(r1)
            goto L8d
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.auth.api.signin.internal.zzc.silentSignIn(com.google.android.gms.common.api.GoogleApiClient):com.google.android.gms.common.api.OptionalPendingResult");
    }
}
