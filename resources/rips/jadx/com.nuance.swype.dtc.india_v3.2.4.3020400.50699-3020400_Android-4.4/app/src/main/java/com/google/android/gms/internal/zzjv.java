package com.google.android.gms.internal;

import android.os.Bundle;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

@zzin
/* loaded from: classes.dex */
public final class zzjv {
    public final Object zzail;
    public final zzjx zzaob;
    public boolean zzcfa;
    public final LinkedList<zza> zzcir;
    private final String zzcis;
    private final String zzcit;
    public long zzciu;
    public long zzciv;
    public long zzciw;
    public long zzcix;
    public long zzciy;
    public long zzciz;

    /* JADX INFO: Access modifiers changed from: private */
    @zzin
    /* loaded from: classes.dex */
    public static final class zza {
        public long zzcja = -1;
        public long zzcjb = -1;
    }

    private zzjv(zzjx zzjxVar, String str, String str2) {
        this.zzail = new Object();
        this.zzciu = -1L;
        this.zzciv = -1L;
        this.zzcfa = false;
        this.zzciw = -1L;
        this.zzcix = 0L;
        this.zzciy = -1L;
        this.zzciz = -1L;
        this.zzaob = zzjxVar;
        this.zzcis = str;
        this.zzcit = str2;
        this.zzcir = new LinkedList<>();
    }

    public zzjv(String str, String str2) {
        this(com.google.android.gms.ads.internal.zzu.zzft(), str, str2);
    }

    public final Bundle toBundle() {
        Bundle bundle;
        synchronized (this.zzail) {
            bundle = new Bundle();
            bundle.putString("seq_num", this.zzcis);
            bundle.putString("slotid", this.zzcit);
            bundle.putBoolean("ismediation", this.zzcfa);
            bundle.putLong("treq", this.zzciy);
            bundle.putLong("tresponse", this.zzciz);
            bundle.putLong("timp", this.zzciv);
            bundle.putLong("tload", this.zzciw);
            bundle.putLong("pcc", this.zzcix);
            bundle.putLong("tfetch", this.zzciu);
            ArrayList<? extends Parcelable> arrayList = new ArrayList<>();
            Iterator<zza> it = this.zzcir.iterator();
            while (it.hasNext()) {
                zza next = it.next();
                Bundle bundle2 = new Bundle();
                bundle2.putLong("topen", next.zzcja);
                bundle2.putLong("tclose", next.zzcjb);
                arrayList.add(bundle2);
            }
            bundle.putParcelableArrayList("tclick", arrayList);
        }
        return bundle;
    }
}
