package com.google.android.gms.internal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.util.ArrayMap;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

/* loaded from: classes.dex */
public final class zzqv extends Fragment implements zzqk {
    private static WeakHashMap<FragmentActivity, WeakReference<zzqv>> vn = new WeakHashMap<>();
    private Bundle vp;
    private Map<String, zzqj> vo = new ArrayMap();
    private int zzblv = 0;

    public static zzqv zza(FragmentActivity fragmentActivity) {
        zzqv zzqvVar;
        WeakReference<zzqv> weakReference = vn.get(fragmentActivity);
        if (weakReference == null || (zzqvVar = weakReference.get()) == null) {
            try {
                zzqvVar = (zzqv) fragmentActivity.getSupportFragmentManager().findFragmentByTag("SupportLifecycleFragmentImpl");
                if (zzqvVar == null || zzqvVar.isRemoving()) {
                    zzqvVar = new zzqv();
                    fragmentActivity.getSupportFragmentManager().beginTransaction().add(zzqvVar, "SupportLifecycleFragmentImpl").commitAllowingStateLoss();
                }
                vn.put(fragmentActivity, new WeakReference<>(zzqvVar));
            } catch (ClassCastException e) {
                throw new IllegalStateException("Fragment with tag SupportLifecycleFragmentImpl is not a SupportLifecycleFragmentImpl", e);
            }
        }
        return zzqvVar;
    }

    @Override // android.support.v4.app.Fragment
    public final void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        super.dump(str, fileDescriptor, printWriter, strArr);
        Iterator<zzqj> it = this.vo.values().iterator();
        while (it.hasNext()) {
            it.next().dump(str, fileDescriptor, printWriter, strArr);
        }
    }

    @Override // android.support.v4.app.Fragment
    public final void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        Iterator<zzqj> it = this.vo.values().iterator();
        while (it.hasNext()) {
            it.next().onActivityResult(i, i2, intent);
        }
    }

    @Override // android.support.v4.app.Fragment
    public final void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.zzblv = 1;
        this.vp = bundle;
        for (Map.Entry<String, zzqj> entry : this.vo.entrySet()) {
            entry.getValue().onCreate(bundle != null ? bundle.getBundle(entry.getKey()) : null);
        }
    }

    @Override // android.support.v4.app.Fragment
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

    @Override // android.support.v4.app.Fragment
    public final void onStart() {
        super.onStop();
        this.zzblv = 2;
        Iterator<zzqj> it = this.vo.values().iterator();
        while (it.hasNext()) {
            it.next().onStart();
        }
    }

    @Override // android.support.v4.app.Fragment
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
    public final void zza(final String str, final zzqj zzqjVar) {
        if (this.vo.containsKey(str)) {
            throw new IllegalArgumentException(new StringBuilder(String.valueOf(str).length() + 59).append("LifecycleCallback with tag ").append(str).append(" already added to this fragment.").toString());
        }
        this.vo.put(str, zzqjVar);
        if (this.zzblv > 0) {
            new Handler(Looper.getMainLooper()).post(new Runnable() { // from class: com.google.android.gms.internal.zzqv.1
                @Override // java.lang.Runnable
                public final void run() {
                    if (zzqv.this.zzblv > 0) {
                        zzqjVar.onCreate(zzqv.this.vp != null ? zzqv.this.vp.getBundle(str) : null);
                    }
                    if (zzqv.this.zzblv >= 2) {
                        zzqjVar.onStart();
                    }
                    if (zzqv.this.zzblv >= 3) {
                        zzqjVar.onStop();
                    }
                }
            });
        }
    }

    @Override // com.google.android.gms.internal.zzqk
    public final /* synthetic */ Activity zzaqt() {
        return getActivity();
    }
}
