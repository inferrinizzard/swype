package com.google.android.gms.auth.api.signin;

import android.accounts.Account;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import com.google.android.gms.auth.api.signin.internal.zze;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.zzab;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GoogleSignInOptions extends AbstractSafeParcelable implements Api.ApiOptions.Optional, ReflectedParcelable {
    public static final Parcelable.Creator<GoogleSignInOptions> CREATOR;
    public static final GoogleSignInOptions DEFAULT_SIGN_IN;
    private static Comparator<Scope> dJ;
    public static final Scope dK = new Scope("profile");
    public static final Scope dL = new Scope("email");
    public static final Scope dM = new Scope("openid");
    public Account aL;
    private final ArrayList<Scope> dN;
    public boolean dO;
    public final boolean dP;
    final boolean dQ;
    public String dR;
    String dS;
    final int versionCode;

    /* loaded from: classes.dex */
    public static final class Builder {
        private Account aL;
        private boolean dO;
        private boolean dP;
        private boolean dQ;
        private String dR;
        private String dS;
        public Set<Scope> dT;

        public Builder() {
            this.dT = new HashSet();
        }

        public Builder(GoogleSignInOptions googleSignInOptions) {
            this.dT = new HashSet();
            zzab.zzy(googleSignInOptions);
            this.dT = new HashSet(googleSignInOptions.dN);
            this.dP = googleSignInOptions.dP;
            this.dQ = googleSignInOptions.dQ;
            this.dO = googleSignInOptions.dO;
            this.dR = googleSignInOptions.dR;
            this.aL = googleSignInOptions.aL;
            this.dS = googleSignInOptions.dS;
        }

        public final GoogleSignInOptions build() {
            if (this.dO && (this.aL == null || !this.dT.isEmpty())) {
                requestId();
            }
            return new GoogleSignInOptions((Set) this.dT, this.aL, this.dO, this.dP, this.dQ, this.dR, this.dS, (byte) 0);
        }

        public final Builder requestId() {
            this.dT.add(GoogleSignInOptions.dM);
            return this;
        }

        public final Builder requestScopes(Scope scope, Scope... scopeArr) {
            this.dT.add(scope);
            this.dT.addAll(Arrays.asList(scopeArr));
            return this;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public GoogleSignInOptions(int i, ArrayList<Scope> arrayList, Account account, boolean z, boolean z2, boolean z3, String str, String str2) {
        this.versionCode = i;
        this.dN = arrayList;
        this.aL = account;
        this.dO = z;
        this.dP = z2;
        this.dQ = z3;
        this.dR = str;
        this.dS = str2;
    }

    private GoogleSignInOptions(Set<Scope> set, Account account, boolean z, boolean z2, boolean z3, String str, String str2) {
        this(2, (ArrayList<Scope>) new ArrayList(set), account, z, z2, z3, str, str2);
    }

    /* synthetic */ GoogleSignInOptions(Set set, Account account, boolean z, boolean z2, boolean z3, String str, String str2, byte b) {
        this(set, account, z, z2, z3, str, str2);
    }

    public static GoogleSignInOptions zzfq(String str) throws JSONException {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        JSONObject jSONObject = new JSONObject(str);
        HashSet hashSet = new HashSet();
        JSONArray jSONArray = jSONObject.getJSONArray("scopes");
        int length = jSONArray.length();
        for (int i = 0; i < length; i++) {
            hashSet.add(new Scope(jSONArray.getString(i)));
        }
        String optString = jSONObject.optString("accountName", null);
        return new GoogleSignInOptions(hashSet, !TextUtils.isEmpty(optString) ? new Account(optString, "com.google") : null, jSONObject.getBoolean("idTokenRequested"), jSONObject.getBoolean("serverAuthRequested"), jSONObject.getBoolean("forceCodeForRefreshToken"), jSONObject.optString("serverClientId", null), jSONObject.optString("hostedDomain", null));
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        zzb.zza(this, parcel, i);
    }

    public final ArrayList<Scope> zzafq() {
        return new ArrayList<>(this.dN);
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        try {
            GoogleSignInOptions googleSignInOptions = (GoogleSignInOptions) obj;
            if (this.dN.size() != googleSignInOptions.zzafq().size() || !this.dN.containsAll(googleSignInOptions.zzafq())) {
                return false;
            }
            if (this.aL == null) {
                if (googleSignInOptions.aL != null) {
                    return false;
                }
            } else if (!this.aL.equals(googleSignInOptions.aL)) {
                return false;
            }
            if (TextUtils.isEmpty(this.dR)) {
                if (!TextUtils.isEmpty(googleSignInOptions.dR)) {
                    return false;
                }
            } else if (!this.dR.equals(googleSignInOptions.dR)) {
                return false;
            }
            if (this.dQ == googleSignInOptions.dQ && this.dO == googleSignInOptions.dO) {
                return this.dP == googleSignInOptions.dP;
            }
            return false;
        } catch (ClassCastException e) {
            return false;
        }
    }

    public int hashCode() {
        ArrayList arrayList = new ArrayList();
        Iterator<Scope> it = this.dN.iterator();
        while (it.hasNext()) {
            arrayList.add(it.next().sp);
        }
        Collections.sort(arrayList);
        return new zze().zzq(arrayList).zzq(this.aL).zzq(this.dR).zzba(this.dQ).zzba(this.dO).zzba(this.dP).eg;
    }

    public final JSONObject zzafp() {
        JSONObject jSONObject = new JSONObject();
        try {
            JSONArray jSONArray = new JSONArray();
            Collections.sort(this.dN, dJ);
            Iterator<Scope> it = this.dN.iterator();
            while (it.hasNext()) {
                jSONArray.put(it.next().sp);
            }
            jSONObject.put("scopes", jSONArray);
            if (this.aL != null) {
                jSONObject.put("accountName", this.aL.name);
            }
            jSONObject.put("idTokenRequested", this.dO);
            jSONObject.put("forceCodeForRefreshToken", this.dQ);
            jSONObject.put("serverAuthRequested", this.dP);
            if (!TextUtils.isEmpty(this.dR)) {
                jSONObject.put("serverClientId", this.dR);
            }
            if (!TextUtils.isEmpty(this.dS)) {
                jSONObject.put("hostedDomain", this.dS);
            }
            return jSONObject;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    static {
        Builder requestId = new Builder().requestId();
        requestId.dT.add(dK);
        DEFAULT_SIGN_IN = requestId.build();
        CREATOR = new zzb();
        dJ = new Comparator<Scope>() { // from class: com.google.android.gms.auth.api.signin.GoogleSignInOptions.1
            @Override // java.util.Comparator
            public final /* synthetic */ int compare(Scope scope, Scope scope2) {
                return scope.sp.compareTo(scope2.sp);
            }
        };
    }
}
