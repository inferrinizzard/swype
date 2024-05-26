package com.google.android.gms.internal;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.util.ArrayMap;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

@TargetApi(11)
/* loaded from: classes.dex */
public final class zzql extends Fragment implements zzqk {
    private static WeakHashMap<Activity, WeakReference<zzql>> vn = new WeakHashMap<>();
    private Bundle vp;
    private Map<String, zzqj> vo = new ArrayMap();
    private int zzblv = 0;

    public static zzql zzt(Activity activity) {
        zzql zzqlVar;
        WeakReference<zzql> weakReference = vn.get(activity);
        if (weakReference == null || (zzqlVar = weakReference.get()) == null) {
            try {
                zzqlVar = (zzql) activity.getFragmentManager().findFragmentByTag("LifecycleFragmentImpl");
                if (zzqlVar == null || zzqlVar.isRemoving()) {
                    zzqlVar = new zzql();
                    activity.getFragmentManager().beginTransaction().add(zzqlVar, "LifecycleFragmentImpl").commitAllowingStateLoss();
                }
                vn.put(activity, new WeakReference<>(zzqlVar));
            } catch (ClassCastException e) {
                throw new IllegalStateException("Fragment with tag LifecycleFragmentImpl is not a LifecycleFragmentImpl", e);
            }
        }
        return zzqlVar;
    }

    @Override // android.app.Fragment
    public final void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        super.dump(str, fileDescriptor, printWriter, strArr);
        Iterator<zzqj> it = this.vo.values().iterator();
        while (it.hasNext()) {
            it.next().dump(str, fileDescriptor, printWriter, strArr);
        }
    }

    @Override // android.app.Fragment
    public final void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        Iterator<zzqj> it = this.vo.values().iterator();
        while (it.hasNext()) {
            it.next().onActivityResult(i, i2, intent);
        }
    }

    @Override // android.app.Fragment
    public final void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.zzblv = 1;
        this.vp = bundle;
        for (Map.Entry<String, zzqj> entry : this.vo.entrySet()) {
            entry.getValue().onCreate(bundle != null ? bundle.getBundle(entry.getKey()) : null);
        }
    }

    @Override // android.app.Fragment
    public final void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        if (bundle == null) {
            return;
        }
        for (Map.Entry<String, zzqj> entry : this.vo.entrySet()) {
            Bundle bundle2 = new Bundle();
            entry.getValue().onSaveInstanceState(bundle2);
            bundle.putBundle(entry.getKey(), bundle2);
        }
    }

    @Override // android.app.Fragment
    public final void onStart() {
        super.onStop();
        this.zzblv = 2;
        Iterator<zzqj> it = this.vo.values().iterator();
        while (it.hasNext()) {
            it.next().onStart();
        }
    }

    @Override // android.app.Fragment
    public final void onStop() {
        super.onStop();
        this.zzblv = 3;
        Iterator<zzqj> it = this.vo.values().iterator();
        while (it.hasNext()) {
            it.next().onStop();
        }
    }

    @Override // com.google.android.gms.internal.zzqk
    public final <T extends zzqj> T zza(String str, Class<T> cls) {
        return cls.cast(this.vo.get(str));
    }

    @Override // com.google.android.gms.internal.zzqk
    public final Activity zzaqt() {
        return getActivity();
    }

    @Override // com.google.android.gms.internal.zzqk
    public final void zza(final String str, final zzqj zzqjVar) {
        if (this.vo.containsKey(str)) {
            throw new IllegalArgumentException(new StringBuilder(String.valueOf(str).length() + 59).append("LifecycleCallback with tag ").append(str).append(" already added to this fragment.").toString());
        }
        this.vo.put(str, zzqjVar);
        if (this.zzblv > 0) {
            new Handler(Looper.getMainLooper()).post(new Runnable() { // from class: com.google.android.gms.internal.zzql.1
                @Override // java.lang.Runnable
                public final void run() {
                    if (zzql.this.zzblv > 0) {
                        zzqjVar.onCreate(zzql.this.vp != null ? zzql.this.vp.getBundle(str) : null);
                    }
                    if (zzql.this.zzblv >= 2) {
                        zzqjVar.onStart();
                    }
                    if (zzql.this.zzblv >= 3) {
                        zzqjVar.onStop();
                    }
                }
            });
        }
    }
}
