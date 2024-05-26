package com.google.android.gms.internal;

import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import com.google.android.gms.internal.zzae;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/* loaded from: classes.dex */
public abstract class zzaq extends zzao {
    private static Method zzafo;
    protected boolean zzafn;
    protected String zzafp;
    protected boolean zzafr;
    protected boolean zzafs;
    private static final String TAG = zzaq.class.getSimpleName();
    private static long startTime = 0;
    static boolean zzafq = false;
    protected static volatile zzax zzaey = null;
    protected static final Object zzaft = new Object();

    /* JADX INFO: Access modifiers changed from: protected */
    public zzaq(Context context, String str) {
        super(context);
        this.zzafn = false;
        this.zzafr = false;
        this.zzafs = false;
        this.zzafp = str;
        this.zzafn = false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public zzaq(Context context, String str, boolean z) {
        super(context);
        this.zzafn = false;
        this.zzafr = false;
        this.zzafs = false;
        this.zzafp = str;
        this.zzafn = z;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static synchronized void zza(Context context, boolean z) {
        synchronized (zzaq.class) {
            if (!zzafq) {
                startTime = Calendar.getInstance().getTime().getTime() / 1000;
                zzaey = zzb(context, z);
                zzafq = true;
            }
        }
    }

    @Override // com.google.android.gms.internal.zzao
    protected final zzae.zza zzc(Context context) {
        zzae.zza zzaVar = new zzae.zza();
        if (!TextUtils.isEmpty(this.zzafp)) {
            zzaVar.zzcs = this.zzafp;
        }
        zzax zzb = zzb(context, this.zzafn);
        zzb.zzcs();
        zza(zzb, zzaVar);
        zzb.zzct();
        return zzaVar;
    }

    @Override // com.google.android.gms.internal.zzao
    protected zzae.zza zzd(Context context) {
        zzae.zza zzaVar = new zzae.zza();
        if (!TextUtils.isEmpty(this.zzafp)) {
            zzaVar.zzcs = this.zzafp;
        }
        zzax zzb = zzb(context, this.zzafn);
        zzb.zzcs();
        try {
            List<Long> zza = zza(zzb, this.zzafd, this.zzafl);
            zzaVar.zzdf = zza.get(0);
            zzaVar.zzdg = zza.get(1);
            if (zza.get(2).longValue() >= 0) {
                zzaVar.zzdh = zza.get(2);
            }
            zzaVar.zzdv = zza.get(3);
            zzaVar.zzdw = zza.get(4);
        } catch (zzaw e) {
        }
        if (this.zzaff > 0) {
            zzaVar.zzea = Long.valueOf(this.zzaff);
        }
        if (this.zzafg > 0) {
            zzaVar.zzdz = Long.valueOf(this.zzafg);
        }
        if (this.zzafh > 0) {
            zzaVar.zzdy = Long.valueOf(this.zzafh);
        }
        if (this.zzafi > 0) {
            zzaVar.zzeb = Long.valueOf(this.zzafi);
        }
        if (this.zzafj > 0) {
            zzaVar.zzed = Long.valueOf(this.zzafj);
        }
        try {
            int size = this.zzafe.size() - 1;
            if (size > 0) {
                zzaVar.zzee = new zzae.zza.C0058zza[size];
                for (int i = 0; i < size; i++) {
                    List<Long> zza2 = zza(zzb, this.zzafe.get(i), this.zzafl);
                    zzae.zza.C0058zza c0058zza = new zzae.zza.C0058zza();
                    c0058zza.zzdf = zza2.get(0);
                    c0058zza.zzdg = zza2.get(1);
                    zzaVar.zzee[i] = c0058zza;
                }
            }
        } catch (zzaw e2) {
            zzaVar.zzee = null;
        }
        ArrayList arrayList = new ArrayList();
        if (zzb.zzagg != null) {
            int zzat = zzb.zzat();
            arrayList.add(new zzbi(zzb, zzaVar));
            arrayList.add(new zzbl(zzb, "BOJ3tjkSPl7I5QAxVTrljV+nhFJcIx2Q/kO7zmVvITuSUJHDxQAfVy/jOA0v9pYs", "zDiqPrOORPXZIrDdW7RtGAel/ckCjtoUBGAnfbt1Dbs=", zzaVar, zzat));
            arrayList.add(new zzbg(zzb, "L5suOEkl11eDXKBlerzt5uhrXmMUgM/zg/0p0sGGN2whxBlK6x9tQhm9iwezF+oF", "LKckwzQJypLfshE2gWJ2grGramB5zEfM8v/nJLs8qn0=", zzaVar, startTime, zzat));
            if (((Boolean) com.google.android.gms.ads.internal.zzu.zzfz().zzd(zzdc.zzbbo)).booleanValue()) {
                arrayList.add(new zzbf(zzb, "gK2idbBMZeT4po0Euq26hpNJPtYADahIoduvc+9lPfaj98i6doa/I7yVE/XdPW0n", "z9ajAOx5ZNdFgTi1Ek7alAp0ZDId6vOmAD0S3qFva+s=", zzaVar, zzat));
            }
            arrayList.add(new zzaz(zzb, "8XC+T5ZJmGqLgq9bGckp9QpSek/MdZkWG8J7h0S/jQT+nLtpFloYeQEp8BYxEnxS", "ybUDDh+rxXFJD95YxLPryhWUtCqqCbMTY7q0vd/SZrg=", zzaVar, zzat));
            if (((Boolean) com.google.android.gms.ads.internal.zzu.zzfz().zzd(zzdc.zzbbr)).booleanValue()) {
                arrayList.add(new zzbj(zzb, "58FzSXiuhwkPJhngNzopCJPRsb4QxaL4R9w88HtiTtPngj9cSA9bk253tVUsvdvn", "FSmb+R06dYXhtkSUnpYyddV7qH5CUpQhJaF+z8pCRgU=", zzaVar, zzat));
            }
        }
        zza(arrayList);
        zzb.zzct();
        return zzaVar;
    }

    private static zzax zzb(Context context, boolean z) {
        if (zzaey == null) {
            synchronized (zzaft) {
                if (zzaey == null) {
                    zzax zza = zzax.zza(context, "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", z);
                    List<Class> singletonList = Collections.singletonList(Context.class);
                    zza.zza("FpxkL8T4+4KvQMD9i+2pSkm0iSIrm7YPofeiNCCHrFTofQNLiBOBNdZP5Pz5i3jB", "WFZF3SqWUN2v06LedHaqBGrhE85rZge34n+mPFIupKU=", singletonList);
                    zza.zza("lLpTIaE60qRmDJilKTnB6dMslmEDCMG+aJ7xPwxeE01HtxatTPhAFeGxL2EFpKqq", "LwAyv7R7EEW6/T7p6KlsghmfaITLnkCV2ffewHyZJ4E=", singletonList);
                    zza.zza("WDNcg/qrUvZ/arv3KlIpjTJy2PLrL5zJRy6RhtE1BwGO7yUZTah8sIwGKrLNQxBR", "k1O5CScpibVjQ/AkqNOTz2L1NxiUwSDOHHe20mOBuZE=", singletonList);
                    zza.zza("0ghH/t1vDAhJsMBEq6AmO4wSmIDUPcR/Ca+bXIPvotbCa7WG11X2GY3bGGSFRSWI", "UxLcO2gMVJYxcPCRIA0oswQMYaa0vipadHgqkbOQas0=", singletonList);
                    zza.zza("keK3XK0wK2UU/nLysKwyCf+kkLkTOC2vAbYqwrBu7R43EUtKstTw0Ncacr5N836s", "waGnxH9bEhira4fPRFV2xqpjD0WpWaSdKy82IuNFwdk=", singletonList);
                    zza.zza("L05xKnfnYeHbEBu8dBVHLX06/lqdQ3wjnf1MDhbz8UyVOSERj3ew9tHuZCsFIQLQ", "alpbMH9e2rlIOT//YFdQTXyhxLWZXiidj7upE61JEUs=", singletonList);
                    zza.zza("DlgHLXqDLqm1GFukOQGzXSwndUeIXE36A5gg71kBD49NoHWHGWFjde/o+K/rzyX6", "rvMoYpysLXzhZ6Icu/Rx+8e3b8bA+ziXoZXmih9N3OA=", singletonList);
                    zza.zza("fBtIGCHte1o+4DEJXJKmN8amPgr7INw+5aLJWfquR6onmnM2N4yyUIpZKWlQd2MT", "UbZQKWrvtEsWMuqKUZ59vHKULlKuI6WSXmDlyXvVcPA=", singletonList);
                    zza.zza("8XC+T5ZJmGqLgq9bGckp9QpSek/MdZkWG8J7h0S/jQT+nLtpFloYeQEp8BYxEnxS", "ybUDDh+rxXFJD95YxLPryhWUtCqqCbMTY7q0vd/SZrg=", singletonList);
                    zza.zza("7scYqzyHRPBaZWFKJ3pOWHbR6Dbo5le4dynIUtP3L7pYFHAqNzdBRQatrNTDhiks", "vVlHDsOifDC8W64bgexaMgYAPimhsdV/psSFMo/Evqg=", Arrays.asList(MotionEvent.class, DisplayMetrics.class));
                    zza.zza("L5suOEkl11eDXKBlerzt5uhrXmMUgM/zg/0p0sGGN2whxBlK6x9tQhm9iwezF+oF", "LKckwzQJypLfshE2gWJ2grGramB5zEfM8v/nJLs8qn0=", Collections.emptyList());
                    zza.zza("6p7iCXfSUdPVZxKIMLb6kQ6w4NNjoD9lvLRgWpY6QIdh4oP6AFLdVKBHQR56jQnq", "am+Emx+Il9MpMu9RJG55dNiUlw7VvmwKoBU1NE91gtY=", Collections.emptyList());
                    zza.zza("BOJ3tjkSPl7I5QAxVTrljV+nhFJcIx2Q/kO7zmVvITuSUJHDxQAfVy/jOA0v9pYs", "zDiqPrOORPXZIrDdW7RtGAel/ckCjtoUBGAnfbt1Dbs=", Collections.emptyList());
                    zza.zza("gK2idbBMZeT4po0Euq26hpNJPtYADahIoduvc+9lPfaj98i6doa/I7yVE/XdPW0n", "z9ajAOx5ZNdFgTi1Ek7alAp0ZDId6vOmAD0S3qFva+s=", Collections.emptyList());
                    zza.zza("58FzSXiuhwkPJhngNzopCJPRsb4QxaL4R9w88HtiTtPngj9cSA9bk253tVUsvdvn", "FSmb+R06dYXhtkSUnpYyddV7qH5CUpQhJaF+z8pCRgU=", Collections.emptyList());
                    zzaey = zza;
                }
            }
        }
        return zzaey;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public List<Callable<Void>> zzb(zzax zzaxVar, zzae.zza zzaVar) {
        int zzat = zzaxVar.zzat();
        ArrayList arrayList = new ArrayList();
        arrayList.add(new zzbb(zzaxVar, "FpxkL8T4+4KvQMD9i+2pSkm0iSIrm7YPofeiNCCHrFTofQNLiBOBNdZP5Pz5i3jB", "WFZF3SqWUN2v06LedHaqBGrhE85rZge34n+mPFIupKU=", zzaVar, zzat));
        arrayList.add(new zzbg(zzaxVar, "L5suOEkl11eDXKBlerzt5uhrXmMUgM/zg/0p0sGGN2whxBlK6x9tQhm9iwezF+oF", "LKckwzQJypLfshE2gWJ2grGramB5zEfM8v/nJLs8qn0=", zzaVar, startTime, zzat));
        arrayList.add(new zzbl(zzaxVar, "BOJ3tjkSPl7I5QAxVTrljV+nhFJcIx2Q/kO7zmVvITuSUJHDxQAfVy/jOA0v9pYs", "zDiqPrOORPXZIrDdW7RtGAel/ckCjtoUBGAnfbt1Dbs=", zzaVar, zzat));
        arrayList.add(new zzbm(zzaxVar, "0ghH/t1vDAhJsMBEq6AmO4wSmIDUPcR/Ca+bXIPvotbCa7WG11X2GY3bGGSFRSWI", "UxLcO2gMVJYxcPCRIA0oswQMYaa0vipadHgqkbOQas0=", zzaVar, zzat));
        arrayList.add(new zzbn(zzaxVar, "6p7iCXfSUdPVZxKIMLb6kQ6w4NNjoD9lvLRgWpY6QIdh4oP6AFLdVKBHQR56jQnq", "am+Emx+Il9MpMu9RJG55dNiUlw7VvmwKoBU1NE91gtY=", zzaVar, zzat));
        arrayList.add(new zzba(zzaxVar, "WDNcg/qrUvZ/arv3KlIpjTJy2PLrL5zJRy6RhtE1BwGO7yUZTah8sIwGKrLNQxBR", "k1O5CScpibVjQ/AkqNOTz2L1NxiUwSDOHHe20mOBuZE=", zzaVar, zzat));
        arrayList.add(new zzbe(zzaxVar, "keK3XK0wK2UU/nLysKwyCf+kkLkTOC2vAbYqwrBu7R43EUtKstTw0Ncacr5N836s", "waGnxH9bEhira4fPRFV2xqpjD0WpWaSdKy82IuNFwdk=", zzaVar, zzat));
        arrayList.add(new zzbk(zzaxVar, "fBtIGCHte1o+4DEJXJKmN8amPgr7INw+5aLJWfquR6onmnM2N4yyUIpZKWlQd2MT", "UbZQKWrvtEsWMuqKUZ59vHKULlKuI6WSXmDlyXvVcPA=", zzaVar, zzat));
        arrayList.add(new zzaz(zzaxVar, "8XC+T5ZJmGqLgq9bGckp9QpSek/MdZkWG8J7h0S/jQT+nLtpFloYeQEp8BYxEnxS", "ybUDDh+rxXFJD95YxLPryhWUtCqqCbMTY7q0vd/SZrg=", zzaVar, zzat));
        arrayList.add(new zzbd(zzaxVar, "L05xKnfnYeHbEBu8dBVHLX06/lqdQ3wjnf1MDhbz8UyVOSERj3ew9tHuZCsFIQLQ", "alpbMH9e2rlIOT//YFdQTXyhxLWZXiidj7upE61JEUs=", zzaVar, zzat));
        arrayList.add(new zzbc(zzaxVar, "DlgHLXqDLqm1GFukOQGzXSwndUeIXE36A5gg71kBD49NoHWHGWFjde/o+K/rzyX6", "rvMoYpysLXzhZ6Icu/Rx+8e3b8bA+ziXoZXmih9N3OA=", zzaVar, zzat));
        if (((Boolean) com.google.android.gms.ads.internal.zzu.zzfz().zzd(zzdc.zzbbn)).booleanValue()) {
            arrayList.add(new zzbf(zzaxVar, "gK2idbBMZeT4po0Euq26hpNJPtYADahIoduvc+9lPfaj98i6doa/I7yVE/XdPW0n", "z9ajAOx5ZNdFgTi1Ek7alAp0ZDId6vOmAD0S3qFva+s=", zzaVar, zzat));
        }
        if (((Boolean) com.google.android.gms.ads.internal.zzu.zzfz().zzd(zzdc.zzbbq)).booleanValue()) {
            arrayList.add(new zzbj(zzaxVar, "58FzSXiuhwkPJhngNzopCJPRsb4QxaL4R9w88HtiTtPngj9cSA9bk253tVUsvdvn", "FSmb+R06dYXhtkSUnpYyddV7qH5CUpQhJaF+z8pCRgU=", zzaVar, zzat));
        }
        return arrayList;
    }

    protected void zza(zzax zzaxVar, zzae.zza zzaVar) {
        if (zzaxVar.zzagg == null) {
            return;
        }
        zza(zzb(zzaxVar, zzaVar));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static void zza(List<Callable<Void>> list) {
        ExecutorService executorService;
        if (zzaey == null || (executorService = zzaey.zzagg) == null || list.isEmpty()) {
            return;
        }
        try {
            executorService.invokeAll(list, ((Long) com.google.android.gms.ads.internal.zzu.zzfz().zzd(zzdc.zzbbj)).longValue(), TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            String.format("class methods got exception: %s", zzay.zza(e));
        }
    }

    private static List<Long> zza(zzax zzaxVar, MotionEvent motionEvent, DisplayMetrics displayMetrics) throws zzaw {
        Method zzc = zzaxVar.zzc("7scYqzyHRPBaZWFKJ3pOWHbR6Dbo5le4dynIUtP3L7pYFHAqNzdBRQatrNTDhiks", "vVlHDsOifDC8W64bgexaMgYAPimhsdV/psSFMo/Evqg=");
        zzafo = zzc;
        if (zzc == null || motionEvent == null) {
            throw new zzaw();
        }
        try {
            return (ArrayList) zzafo.invoke(null, motionEvent, displayMetrics);
        } catch (IllegalAccessException e) {
            throw new zzaw(e);
        } catch (InvocationTargetException e2) {
            throw new zzaw(e2);
        }
    }
}
