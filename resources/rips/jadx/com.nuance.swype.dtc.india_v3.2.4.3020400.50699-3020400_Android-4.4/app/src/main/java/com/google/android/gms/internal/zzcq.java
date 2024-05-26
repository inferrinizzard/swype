package com.google.android.gms.internal;

import android.util.Base64OutputStream;
import com.google.android.gms.internal.zzct;
import com.nuance.swype.input.hardkey.HardKeyboardManager;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Locale;
import java.util.PriorityQueue;

@zzin
/* loaded from: classes.dex */
public final class zzcq {
    private final int zzatb;
    private final zzcp zzatd = new zzcs();
    private final int zzata = 6;
    private final int zzatc = 0;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class zza {
        ByteArrayOutputStream zzatf = new ByteArrayOutputStream(HardKeyboardManager.META_CTRL_ON);
        Base64OutputStream zzatg = new Base64OutputStream(this.zzatf, 10);

        public final String toString() {
            String str;
            try {
                this.zzatg.close();
            } catch (IOException e) {
                zzkd.zzb("HashManager: Unable to convert to Base64.", e);
            }
            try {
                this.zzatf.close();
                str = this.zzatf.toString();
            } catch (IOException e2) {
                zzkd.zzb("HashManager: Unable to convert to Base64.", e2);
                str = "";
            } finally {
                this.zzatf = null;
                this.zzatg = null;
            }
            return str;
        }
    }

    public zzcq(int i) {
        this.zzatb = i;
    }

    public final String zza(ArrayList<String> arrayList) {
        StringBuffer stringBuffer = new StringBuffer();
        Iterator<String> it = arrayList.iterator();
        while (it.hasNext()) {
            stringBuffer.append(it.next().toLowerCase(Locale.US));
            stringBuffer.append('\n');
        }
        return zzab(stringBuffer.toString());
    }

    private String zzab(String str) {
        String[] split = str.split("\n");
        if (split.length == 0) {
            return "";
        }
        zza zzaVar = new zza();
        PriorityQueue priorityQueue = new PriorityQueue(this.zzatb, new Comparator<zzct.zza>() { // from class: com.google.android.gms.internal.zzcq.1
            @Override // java.util.Comparator
            public final /* synthetic */ int compare(zzct.zza zzaVar2, zzct.zza zzaVar3) {
                zzct.zza zzaVar4 = zzaVar2;
                zzct.zza zzaVar5 = zzaVar3;
                int i = zzaVar4.zzatj - zzaVar5.zzatj;
                return i != 0 ? i : (int) (zzaVar4.value - zzaVar5.value);
            }
        });
        for (String str2 : split) {
            String[] zzad = zzcr.zzad(str2);
            if (zzad.length != 0) {
                zzct.zza(zzad, this.zzatb, this.zzata, priorityQueue);
            }
        }
        Iterator it = priorityQueue.iterator();
        while (it.hasNext()) {
            try {
                zzaVar.zzatg.write(this.zzatd.zzaa(((zzct.zza) it.next()).zzati));
            } catch (IOException e) {
                zzkd.zzb("Error while writing hash to byteStream", e);
            }
        }
        return zzaVar.toString();
    }
}
