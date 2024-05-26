package com.google.android.gms.auth.api.signin.internal;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.internal.zzab;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public final class zzk {
    private static final Lock ep = new ReentrantLock();
    private static zzk eq;
    private final Lock er = new ReentrantLock();
    private final SharedPreferences es;

    private zzk(Context context) {
        this.es = context.getSharedPreferences("com.google.android.gms.signin", 0);
    }

    public static zzk zzbc(Context context) {
        zzab.zzy(context);
        ep.lock();
        try {
            if (eq == null) {
                eq = new zzk(context.getApplicationContext());
            }
            return eq;
        } finally {
            ep.unlock();
        }
    }

    private GoogleSignInAccount zzfs(String str) {
        String zzfu;
        if (TextUtils.isEmpty(str) || (zzfu = zzfu(zzy("googleSignInAccount", str))) == null) {
            return null;
        }
        try {
            return GoogleSignInAccount.zzfo(zzfu);
        } catch (JSONException e) {
            return null;
        }
    }

    private GoogleSignInOptions zzft(String str) {
        String zzfu;
        if (TextUtils.isEmpty(str) || (zzfu = zzfu(zzy("googleSignInOptions", str))) == null) {
            return null;
        }
        try {
            return GoogleSignInOptions.zzfq(zzfu);
        } catch (JSONException e) {
            return null;
        }
    }

    private String zzfu(String str) {
        this.er.lock();
        try {
            return this.es.getString(str, null);
        } finally {
            this.er.unlock();
        }
    }

    private void zzfw(String str) {
        this.er.lock();
        try {
            this.es.edit().remove(str).apply();
        } finally {
            this.er.unlock();
        }
    }

    private void zzx(String str, String str2) {
        this.er.lock();
        try {
            this.es.edit().putString(str, str2).apply();
        } finally {
            this.er.unlock();
        }
    }

    private static String zzy(String str, String str2) {
        String valueOf = String.valueOf(":");
        return new StringBuilder(String.valueOf(str).length() + 0 + String.valueOf(valueOf).length() + String.valueOf(str2).length()).append(str).append(valueOf).append(str2).toString();
    }

    public final GoogleSignInAccount zzagj() {
        return zzfs(zzfu("defaultGoogleSignInAccount"));
    }

    public final GoogleSignInOptions zzagk() {
        return zzft(zzfu("defaultGoogleSignInAccount"));
    }

    public final void zzb(GoogleSignInAccount googleSignInAccount, GoogleSignInOptions googleSignInOptions) {
        zzab.zzy(googleSignInAccount);
        zzab.zzy(googleSignInOptions);
        zzx("defaultGoogleSignInAccount", googleSignInAccount.dG);
        zzab.zzy(googleSignInAccount);
        zzab.zzy(googleSignInOptions);
        String str = googleSignInAccount.dG;
        String zzy = zzy("googleSignInAccount", str);
        JSONObject zzafp = googleSignInAccount.zzafp();
        zzafp.remove("serverAuthCode");
        zzx(zzy, zzafp.toString());
        zzx(zzy("googleSignInOptions", str), googleSignInOptions.zzafp().toString());
    }

    public final void zzagl() {
        String zzfu = zzfu("defaultGoogleSignInAccount");
        zzfw("defaultGoogleSignInAccount");
        if (TextUtils.isEmpty(zzfu)) {
            return;
        }
        zzfw(zzy("googleSignInAccount", zzfu));
        zzfw(zzy("googleSignInOptions", zzfu));
    }
}
