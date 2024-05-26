package com.google.android.gms.internal;

import android.os.RemoteException;
import com.google.ads.mediation.AdUrlAdapter;
import com.google.ads.mediation.MediationAdapter;
import com.google.ads.mediation.MediationServerParameters;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.mediation.NetworkExtras;
import com.google.android.gms.ads.mediation.customevent.CustomEvent;
import com.google.android.gms.ads.mediation.customevent.CustomEventAdapter;
import com.google.android.gms.ads.mediation.customevent.CustomEventExtras;
import com.google.android.gms.internal.zzgj;
import java.util.Map;

@zzin
/* loaded from: classes.dex */
public final class zzgi extends zzgj.zza {
    public Map<Class<? extends NetworkExtras>, NetworkExtras> zzbpg;

    private <NETWORK_EXTRAS extends com.google.ads.mediation.NetworkExtras, SERVER_PARAMETERS extends MediationServerParameters> zzgk zzbo(String str) throws RemoteException {
        try {
            Class<?> cls = Class.forName(str, false, zzgi.class.getClassLoader());
            if (MediationAdapter.class.isAssignableFrom(cls)) {
                MediationAdapter mediationAdapter = (MediationAdapter) cls.newInstance();
                return new zzgv(mediationAdapter, (com.google.ads.mediation.NetworkExtras) this.zzbpg.get(mediationAdapter.getAdditionalParametersType()));
            }
            if (com.google.android.gms.ads.mediation.MediationAdapter.class.isAssignableFrom(cls)) {
                return new zzgq((com.google.android.gms.ads.mediation.MediationAdapter) cls.newInstance());
            }
            com.google.android.gms.ads.internal.util.client.zzb.zzcx(new StringBuilder(String.valueOf(str).length() + 64).append("Could not instantiate mediation adapter: ").append(str).append(" (not a valid adapter).").toString());
            throw new RemoteException();
        } catch (Throwable th) {
            return zzbp(str);
        }
    }

    private zzgk zzbp(String str) throws RemoteException {
        try {
            com.google.android.gms.ads.internal.util.client.zzb.zzcv("Reflection failed, retrying using direct instantiation");
        } catch (Throwable th) {
            com.google.android.gms.ads.internal.util.client.zzb.zzd(new StringBuilder(String.valueOf(str).length() + 43).append("Could not instantiate mediation adapter: ").append(str).append(". ").toString(), th);
        }
        if ("com.google.ads.mediation.admob.AdMobAdapter".equals(str)) {
            return new zzgq(new AdMobAdapter());
        }
        if ("com.google.ads.mediation.AdUrlAdapter".equals(str)) {
            return new zzgq(new AdUrlAdapter());
        }
        if ("com.google.android.gms.ads.mediation.customevent.CustomEventAdapter".equals(str)) {
            return new zzgq(new CustomEventAdapter());
        }
        if ("com.google.ads.mediation.customevent.CustomEventAdapter".equals(str)) {
            com.google.ads.mediation.customevent.CustomEventAdapter customEventAdapter = new com.google.ads.mediation.customevent.CustomEventAdapter();
            return new zzgv(customEventAdapter, (CustomEventExtras) this.zzbpg.get(customEventAdapter.getAdditionalParametersType()));
        }
        throw new RemoteException();
    }

    @Override // com.google.android.gms.internal.zzgj
    public final zzgk zzbm(String str) throws RemoteException {
        return zzbo(str);
    }

    @Override // com.google.android.gms.internal.zzgj
    public final boolean zzbn(String str) throws RemoteException {
        try {
            return CustomEvent.class.isAssignableFrom(Class.forName(str, false, zzgi.class.getClassLoader()));
        } catch (Throwable th) {
            com.google.android.gms.ads.internal.util.client.zzb.zzcx(new StringBuilder(String.valueOf(str).length() + 80).append("Could not load custom event implementation class: ").append(str).append(", assuming old implementation.").toString());
            return false;
        }
    }
}
