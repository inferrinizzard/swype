package com.google.android.gms.auth.api.credentials;

import android.app.PendingIntent;
import com.google.android.gms.common.api.GoogleApiClient;

/* loaded from: classes.dex */
public interface CredentialsApi {
    PendingIntent getHintPickerIntent(GoogleApiClient googleApiClient, HintRequest hintRequest);
}
