package com.google.android.gms.auth.api.signin;

import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;

/* loaded from: classes.dex */
public final class GoogleSignInResult implements Result {
    public Status bY;
    public GoogleSignInAccount dU;

    public GoogleSignInResult(GoogleSignInAccount googleSignInAccount, Status status) {
        this.dU = googleSignInAccount;
        this.bY = status;
    }

    @Override // com.google.android.gms.common.api.Result
    public final Status getStatus() {
        return this.bY;
    }
}
