package com.google.android.gms.ads.internal.client;

import android.location.Location;
import android.os.Bundle;
import com.google.android.gms.internal.zzin;
import java.util.ArrayList;
import java.util.List;

@zzin
/* loaded from: classes.dex */
public final class zzf {
    private Bundle mExtras;
    private boolean zzakp;
    private long zzauc;
    private int zzaud;
    private List<String> zzaue;
    private boolean zzauf;
    private int zzaug;
    private String zzauh;
    private SearchAdRequestParcel zzaui;
    private String zzauj;
    private Bundle zzauk;
    private Bundle zzaul;
    private List<String> zzaum;
    private String zzaun;
    private String zzauo;
    private boolean zzaup;
    private Location zzft;

    public zzf() {
        this.zzauc = -1L;
        this.mExtras = new Bundle();
        this.zzaud = -1;
        this.zzaue = new ArrayList();
        this.zzauf = false;
        this.zzaug = -1;
        this.zzakp = false;
        this.zzauh = null;
        this.zzaui = null;
        this.zzft = null;
        this.zzauj = null;
        this.zzauk = new Bundle();
        this.zzaul = new Bundle();
        this.zzaum = new ArrayList();
        this.zzaun = null;
        this.zzauo = null;
        this.zzaup = false;
    }

    public zzf(AdRequestParcel adRequestParcel) {
        this.zzauc = adRequestParcel.zzatm;
        this.mExtras = adRequestParcel.extras;
        this.zzaud = adRequestParcel.zzatn;
        this.zzaue = adRequestParcel.zzato;
        this.zzauf = adRequestParcel.zzatp;
        this.zzaug = adRequestParcel.zzatq;
        this.zzakp = adRequestParcel.zzatr;
        this.zzauh = adRequestParcel.zzats;
        this.zzaui = adRequestParcel.zzatt;
        this.zzft = adRequestParcel.zzatu;
        this.zzauj = adRequestParcel.zzatv;
        this.zzauk = adRequestParcel.zzatw;
        this.zzaul = adRequestParcel.zzatx;
        this.zzaum = adRequestParcel.zzaty;
        this.zzaun = adRequestParcel.zzatz;
        this.zzauo = adRequestParcel.zzaua;
    }

    public final zzf zza(Location location) {
        this.zzft = location;
        return this;
    }

    public final zzf zzc(Bundle bundle) {
        this.zzauk = bundle;
        return this;
    }

    public final AdRequestParcel zzig() {
        return new AdRequestParcel(7, this.zzauc, this.mExtras, this.zzaud, this.zzaue, this.zzauf, this.zzaug, this.zzakp, this.zzauh, this.zzaui, this.zzft, this.zzauj, this.zzauk, this.zzaul, this.zzaum, this.zzaun, this.zzauo, false);
    }
}
