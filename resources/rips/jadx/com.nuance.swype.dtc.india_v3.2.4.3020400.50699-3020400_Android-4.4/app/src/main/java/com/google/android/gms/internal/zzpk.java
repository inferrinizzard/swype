package com.google.android.gms.internal;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.SparseArray;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.xmlResults.XMLResultsHandler;
import com.nuance.swype.input.ThemeManager;
import java.io.FileDescriptor;
import java.io.PrintWriter;

/* loaded from: classes.dex */
public class zzpk extends zzpn {
    private final SparseArray<zza> sC;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class zza implements GoogleApiClient.OnConnectionFailedListener {
        public final int sD;
        public final GoogleApiClient sE;
        public final GoogleApiClient.OnConnectionFailedListener sF;

        public zza(int i, GoogleApiClient googleApiClient, GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener) {
            this.sD = i;
            this.sE = googleApiClient;
            this.sF = onConnectionFailedListener;
            googleApiClient.registerConnectionFailedListener(this);
        }

        @Override // com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener
        public final void onConnectionFailed(ConnectionResult connectionResult) {
            String valueOf = String.valueOf(connectionResult);
            new StringBuilder(String.valueOf(valueOf).length() + 27).append("beginFailureResolution for ").append(valueOf);
            zzpk.this.zzb(connectionResult, this.sD);
        }
    }

    private zzpk(zzqk zzqkVar) {
        super(zzqkVar);
        this.sC = new SparseArray<>();
        this.vm.zza("AutoManageHelper", this);
    }

    @Override // com.google.android.gms.internal.zzpn, com.google.android.gms.internal.zzqj
    public final void onStart() {
        super.onStart();
        boolean z = this.mStarted;
        String valueOf = String.valueOf(this.sC);
        new StringBuilder(String.valueOf(valueOf).length() + 14).append("onStart ").append(z).append(XMLResultsHandler.SEP_SPACE).append(valueOf);
        if (this.sL) {
            return;
        }
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 >= this.sC.size()) {
                return;
            }
            this.sC.valueAt(i2).sE.connect();
            i = i2 + 1;
        }
    }

    @Override // com.google.android.gms.internal.zzpn, com.google.android.gms.internal.zzqj
    public final void onStop() {
        super.onStop();
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 >= this.sC.size()) {
                return;
            }
            this.sC.valueAt(i2).sE.disconnect();
            i = i2 + 1;
        }
    }

    public final void zza(int i, GoogleApiClient googleApiClient, GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener) {
        com.google.android.gms.common.internal.zzab.zzb(googleApiClient, "GoogleApiClient instance cannot be null");
        com.google.android.gms.common.internal.zzab.zza(this.sC.indexOfKey(i) < 0, new StringBuilder(54).append("Already managing a GoogleApiClient with id ").append(i).toString());
        new StringBuilder(54).append("starting AutoManage for client ").append(i).append(XMLResultsHandler.SEP_SPACE).append(this.mStarted).append(XMLResultsHandler.SEP_SPACE).append(this.sL);
        this.sC.put(i, new zza(i, googleApiClient, onConnectionFailedListener));
        if (!this.mStarted || this.sL) {
            return;
        }
        String valueOf = String.valueOf(googleApiClient);
        new StringBuilder(String.valueOf(valueOf).length() + 11).append("connecting ").append(valueOf);
        googleApiClient.connect();
    }

    @Override // com.google.android.gms.internal.zzpn
    protected final void zzaoo() {
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 >= this.sC.size()) {
                return;
            }
            this.sC.valueAt(i2).sE.connect();
            i = i2 + 1;
        }
    }

    public static zzpk zza(zzqi zzqiVar) {
        zzqk zza2 = zzqiVar.vl instanceof FragmentActivity ? zzqv.zza((FragmentActivity) zzqiVar.vl) : zzql.zzt((Activity) zzqiVar.vl);
        zzpk zzpkVar = (zzpk) zza2.zza("AutoManageHelper", zzpk.class);
        return zzpkVar != null ? zzpkVar : new zzpk(zza2);
    }

    @Override // com.google.android.gms.internal.zzqj
    public final void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 >= this.sC.size()) {
                return;
            }
            zza valueAt = this.sC.valueAt(i2);
            printWriter.append((CharSequence) str).append("GoogleApiClient #").print(valueAt.sD);
            printWriter.println(":");
            valueAt.sE.dump(String.valueOf(str).concat(ThemeManager.NO_PRICE), fileDescriptor, printWriter, strArr);
            i = i2 + 1;
        }
    }

    @Override // com.google.android.gms.internal.zzpn
    protected final void zza(ConnectionResult connectionResult, int i) {
        Log.w("AutoManageHelper", "Unresolved error while connecting client. Stopping auto-manage.");
        if (i < 0) {
            Log.wtf("AutoManageHelper", "AutoManageLifecycleHelper received onErrorResolutionFailed callback but no failing client ID is set", new Exception());
            return;
        }
        zza zzaVar = this.sC.get(i);
        if (zzaVar != null) {
            zza zzaVar2 = this.sC.get(i);
            this.sC.remove(i);
            if (zzaVar2 != null) {
                zzaVar2.sE.unregisterConnectionFailedListener(zzaVar2);
                zzaVar2.sE.disconnect();
            }
            GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener = zzaVar.sF;
            if (onConnectionFailedListener != null) {
                onConnectionFailedListener.onConnectionFailed(connectionResult);
            }
        }
    }
}
