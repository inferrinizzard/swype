package com.google.android.gms.auth.api.signin;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.zzab;
import com.google.android.gms.common.util.zze;
import com.google.android.gms.common.util.zzh;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GoogleSignInAccount extends AbstractSafeParcelable implements ReflectedParcelable {
    public static final Parcelable.Creator<GoogleSignInAccount> CREATOR = new zza();
    public static zze dA = zzh.zzavm();
    private static Comparator<Scope> dJ = new Comparator<Scope>() { // from class: com.google.android.gms.auth.api.signin.GoogleSignInAccount.1
        @Override // java.util.Comparator
        public final /* synthetic */ int compare(Scope scope, Scope scope2) {
            return scope.sp.compareTo(scope2.sp);
        }
    };
    String cY;
    List<Scope> cs;
    public String dB;
    String dC;
    Uri dD;
    String dE;
    public long dF;
    public String dG;
    String dH;
    String dI;
    final int versionCode;
    String zzbgg;

    /* JADX INFO: Access modifiers changed from: package-private */
    public GoogleSignInAccount(int i, String str, String str2, String str3, String str4, Uri uri, String str5, long j, String str6, List<Scope> list, String str7, String str8) {
        this.versionCode = i;
        this.zzbgg = str;
        this.cY = str2;
        this.dB = str3;
        this.dC = str4;
        this.dD = uri;
        this.dE = str5;
        this.dF = j;
        this.dG = str6;
        this.cs = list;
        this.dH = str7;
        this.dI = str8;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        zza.zza(this, parcel, i);
    }

    public static GoogleSignInAccount zzfo(String str) throws JSONException {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        JSONObject jSONObject = new JSONObject(str);
        String optString = jSONObject.optString("photoUrl", null);
        Uri parse = TextUtils.isEmpty(optString) ? null : Uri.parse(optString);
        long parseLong = Long.parseLong(jSONObject.getString("expirationTime"));
        HashSet hashSet = new HashSet();
        JSONArray jSONArray = jSONObject.getJSONArray("grantedScopes");
        int length = jSONArray.length();
        for (int i = 0; i < length; i++) {
            hashSet.add(new Scope(jSONArray.getString(i)));
        }
        String optString2 = jSONObject.optString("id");
        String optString3 = jSONObject.optString("tokenId", null);
        String optString4 = jSONObject.optString("email", null);
        String optString5 = jSONObject.optString("displayName", null);
        String optString6 = jSONObject.optString("givenName", null);
        String optString7 = jSONObject.optString("familyName", null);
        Long valueOf = Long.valueOf(parseLong);
        GoogleSignInAccount googleSignInAccount = new GoogleSignInAccount(3, optString2, optString3, optString4, optString5, parse, null, (valueOf == null ? Long.valueOf(dA.currentTimeMillis() / 1000) : valueOf).longValue(), zzab.zzhr(jSONObject.getString("obfuscatedIdentifier")), new ArrayList((Collection) zzab.zzy(hashSet)), optString6, optString7);
        googleSignInAccount.dE = jSONObject.optString("serverAuthCode", null);
        return googleSignInAccount;
    }

    public boolean equals(Object obj) {
        if (obj instanceof GoogleSignInAccount) {
            return ((GoogleSignInAccount) obj).zzafp().toString().equals(zzafp().toString());
        }
        return false;
    }

    public final JSONObject zzafp() {
        JSONObject jSONObject = new JSONObject();
        try {
            if (this.zzbgg != null) {
                jSONObject.put("id", this.zzbgg);
            }
            if (this.cY != null) {
                jSONObject.put("tokenId", this.cY);
            }
            if (this.dB != null) {
                jSONObject.put("email", this.dB);
            }
            if (this.dC != null) {
                jSONObject.put("displayName", this.dC);
            }
            if (this.dH != null) {
                jSONObject.put("givenName", this.dH);
            }
            if (this.dI != null) {
                jSONObject.put("familyName", this.dI);
            }
            if (this.dD != null) {
                jSONObject.put("photoUrl", this.dD.toString());
            }
            if (this.dE != null) {
                jSONObject.put("serverAuthCode", this.dE);
            }
            jSONObject.put("expirationTime", this.dF);
            jSONObject.put("obfuscatedIdentifier", this.dG);
            JSONArray jSONArray = new JSONArray();
            Collections.sort(this.cs, dJ);
            Iterator<Scope> it = this.cs.iterator();
            while (it.hasNext()) {
                jSONArray.put(it.next().sp);
            }
            jSONObject.put("grantedScopes", jSONArray);
            return jSONObject;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
