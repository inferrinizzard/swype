package com.google.android.gms.ads.internal.client;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.mediation.MediationAdapter;
import com.google.android.gms.ads.mediation.NetworkExtras;
import com.google.android.gms.ads.mediation.admob.AdMobExtras;
import com.google.android.gms.ads.mediation.customevent.CustomEvent;
import com.google.android.gms.ads.search.SearchAdRequest;
import com.google.android.gms.internal.zzin;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@zzin
/* loaded from: classes.dex */
public final class zzad {
    public static final String DEVICE_ID_EMULATOR = zzm.zziw().zzcu("emulator");
    private final boolean zzakp;
    private final int zzaud;
    private final int zzaug;
    private final String zzauh;
    private final String zzauj;
    private final Bundle zzaul;
    private final String zzaun;
    private final boolean zzaup;
    private final Bundle zzavs;
    private final Map<Class<? extends NetworkExtras>, NetworkExtras> zzavt;
    private final SearchAdRequest zzavu;
    private final Set<String> zzavv;
    private final Set<String> zzavw;
    private final Date zzfp;
    private final Set<String> zzfr;
    private final Location zzft;

    /* loaded from: classes.dex */
    public static final class zza {
        private String zzauh;
        private String zzauj;
        private String zzaun;
        private boolean zzaup;
        private Date zzfp;
        private Location zzft;
        private final HashSet<String> zzavx = new HashSet<>();
        private final Bundle zzavs = new Bundle();
        private final HashMap<Class<? extends NetworkExtras>, NetworkExtras> zzavy = new HashMap<>();
        private final HashSet<String> zzavz = new HashSet<>();
        private final Bundle zzaul = new Bundle();
        private final HashSet<String> zzawa = new HashSet<>();
        private int zzaud = -1;
        private boolean zzakp = false;
        private int zzaug = -1;

        public final void setManualImpressionsEnabled(boolean z) {
            this.zzakp = z;
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Deprecated
        public final void zza(NetworkExtras networkExtras) {
            if (networkExtras instanceof AdMobExtras) {
                zza(AdMobAdapter.class, ((AdMobExtras) networkExtras).getExtras());
            } else {
                this.zzavy.put(networkExtras.getClass(), networkExtras);
            }
        }

        public final void zza(Class<? extends MediationAdapter> cls, Bundle bundle) {
            this.zzavs.putBundle(cls.getName(), bundle);
        }

        public final void zza(Date date) {
            this.zzfp = date;
        }

        public final void zzaf(String str) {
            this.zzavx.add(str);
        }

        public final void zzag(String str) {
            this.zzavz.add(str);
        }

        public final void zzah(String str) {
            this.zzavz.remove(str);
        }

        public final void zzai(String str) {
            this.zzauj = str;
        }

        public final void zzaj(String str) {
            this.zzauh = str;
        }

        public final void zzak(String str) {
            this.zzaun = str;
        }

        public final void zzal(String str) {
            this.zzawa.add(str);
        }

        public final void zzb(Location location) {
            this.zzft = location;
        }

        public final void zzb(Class<? extends CustomEvent> cls, Bundle bundle) {
            if (this.zzavs.getBundle("com.google.android.gms.ads.mediation.customevent.CustomEventAdapter") == null) {
                this.zzavs.putBundle("com.google.android.gms.ads.mediation.customevent.CustomEventAdapter", new Bundle());
            }
            this.zzavs.getBundle("com.google.android.gms.ads.mediation.customevent.CustomEventAdapter").putBundle(cls.getName(), bundle);
        }

        public final void zzf(String str, String str2) {
            this.zzaul.putString(str, str2);
        }

        public final void zzn(boolean z) {
            this.zzaug = z ? 1 : 0;
        }

        public final void zzo(boolean z) {
            this.zzaup = z;
        }

        public final void zzt(int i) {
            this.zzaud = i;
        }
    }

    public zzad(zza zzaVar) {
        this(zzaVar, null);
    }

    public zzad(zza zzaVar, SearchAdRequest searchAdRequest) {
        this.zzfp = zzaVar.zzfp;
        this.zzauj = zzaVar.zzauj;
        this.zzaud = zzaVar.zzaud;
        this.zzfr = Collections.unmodifiableSet(zzaVar.zzavx);
        this.zzft = zzaVar.zzft;
        this.zzakp = zzaVar.zzakp;
        this.zzavs = zzaVar.zzavs;
        this.zzavt = Collections.unmodifiableMap(zzaVar.zzavy);
        this.zzauh = zzaVar.zzauh;
        this.zzaun = zzaVar.zzaun;
        this.zzavu = searchAdRequest;
        this.zzaug = zzaVar.zzaug;
        this.zzavv = Collections.unmodifiableSet(zzaVar.zzavz);
        this.zzaul = zzaVar.zzaul;
        this.zzavw = Collections.unmodifiableSet(zzaVar.zzawa);
        this.zzaup = zzaVar.zzaup;
    }

    public final Date getBirthday() {
        return this.zzfp;
    }

    public final String getContentUrl() {
        return this.zzauj;
    }

    public final Bundle getCustomEventExtrasBundle(Class<? extends CustomEvent> cls) {
        Bundle bundle = this.zzavs.getBundle("com.google.android.gms.ads.mediation.customevent.CustomEventAdapter");
        if (bundle != null) {
            return bundle.getBundle(cls.getName());
        }
        return null;
    }

    public final Bundle getCustomTargeting() {
        return this.zzaul;
    }

    public final int getGender() {
        return this.zzaud;
    }

    public final Set<String> getKeywords() {
        return this.zzfr;
    }

    public final Location getLocation() {
        return this.zzft;
    }

    public final boolean getManualImpressionsEnabled() {
        return this.zzakp;
    }

    @Deprecated
    public final <T extends NetworkExtras> T getNetworkExtras(Class<T> cls) {
        return (T) this.zzavt.get(cls);
    }

    public final Bundle getNetworkExtrasBundle(Class<? extends MediationAdapter> cls) {
        return this.zzavs.getBundle(cls.getName());
    }

    public final String getPublisherProvidedId() {
        return this.zzauh;
    }

    public final boolean isDesignedForFamilies() {
        return this.zzaup;
    }

    public final boolean isTestDevice(Context context) {
        return this.zzavv.contains(zzm.zziw().zzaq(context));
    }

    public final String zzje() {
        return this.zzaun;
    }

    public final SearchAdRequest zzjf() {
        return this.zzavu;
    }

    public final Map<Class<? extends NetworkExtras>, NetworkExtras> zzjg() {
        return this.zzavt;
    }

    public final Bundle zzjh() {
        return this.zzavs;
    }

    public final int zzji() {
        return this.zzaug;
    }

    public final Set<String> zzjj() {
        return this.zzavw;
    }
}
