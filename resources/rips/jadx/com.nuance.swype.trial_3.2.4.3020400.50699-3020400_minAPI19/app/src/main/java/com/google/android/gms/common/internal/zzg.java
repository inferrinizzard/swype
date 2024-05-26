package com.google.android.gms.common.internal;

import android.accounts.Account;
import android.view.View;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.internal.zzvv;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/* loaded from: classes.dex */
public final class zzg {
    public final Account aL;
    public final String bX;
    public final Set<Scope> rX;
    private final int rZ;
    private final View sa;
    final String sb;
    public final Set<Scope> yj;
    public final Map<Api<?>, zza> yk;
    public final zzvv yl;
    public Integer ym;

    /* loaded from: classes.dex */
    public static final class zza {
        public final Set<Scope> dT;
        public final boolean yn;
    }

    public zzg(Account account, Set<Scope> set, Map<Api<?>, zza> map, int i, View view, String str, String str2, zzvv zzvvVar) {
        this.aL = account;
        this.rX = set == null ? Collections.EMPTY_SET : Collections.unmodifiableSet(set);
        this.yk = map == null ? Collections.EMPTY_MAP : map;
        this.sa = view;
        this.rZ = i;
        this.bX = str;
        this.sb = str2;
        this.yl = zzvvVar;
        HashSet hashSet = new HashSet(this.rX);
        Iterator<zza> it = this.yk.values().iterator();
        while (it.hasNext()) {
            hashSet.addAll(it.next().dT);
        }
        this.yj = Collections.unmodifiableSet(hashSet);
    }
}
