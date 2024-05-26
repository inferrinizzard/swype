package com.google.android.gms.auth.api.credentials.internal;

import android.app.PendingIntent;
import android.content.Intent;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.CredentialsApi;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.auth.api.credentials.PasswordSpecification;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.internal.zzab;

/* loaded from: classes.dex */
public final class zze implements CredentialsApi {
    @Override // com.google.android.gms.auth.api.credentials.CredentialsApi
    public final PendingIntent getHintPickerIntent(GoogleApiClient googleApiClient, HintRequest hintRequest) {
        zzab.zzb(googleApiClient, "client must not be null");
        zzab.zzb(hintRequest, "request must not be null");
        zzab.zzb(googleApiClient.zza(Auth.CREDENTIALS_API), "Auth.CREDENTIALS_API must be added to GoogleApiClient to use this API");
        googleApiClient.getContext();
        Auth.AuthCredentialsOptions authCredentialsOptions = ((zzg) googleApiClient.zza(Auth.cy)).f0do;
        return PendingIntent.getActivity(googleApiClient.getContext(), 2000, new Intent("com.google.android.gms.auth.api.credentials.PICKER").putExtra("com.google.android.gms.credentials.RequestType", "Hints").putExtra("com.google.android.gms.credentials.HintRequest", hintRequest).putExtra("com.google.android.gms.credentials.PasswordSpecification", PasswordSpecification.cZ), 268435456);
    }
}
