package com.google.android.gms.ads.internal.formats;

import android.graphics.Point;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import com.google.android.gms.ads.internal.client.zzm;
import com.google.android.gms.ads.internal.zzu;
import com.google.android.gms.internal.zzdc;
import com.google.android.gms.internal.zzdt;
import com.google.android.gms.internal.zzin;
import com.google.android.gms.internal.zzkd;
import com.google.android.gms.internal.zzkh;
import com.google.android.gms.internal.zzlc;
import com.google.android.gms.internal.zzlh;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

@zzin
/* loaded from: classes.dex */
public class zzk extends zzdt.zza implements View.OnClickListener, View.OnTouchListener, ViewTreeObserver.OnGlobalLayoutListener, ViewTreeObserver.OnScrollChangedListener {
    private FrameLayout zzaiz;
    private zzh zzbfp;
    private final FrameLayout zzbgt;
    private zzb zzbgv;
    int zzbgx;
    int zzbgy;
    private final Object zzail = new Object();
    private Map<String, WeakReference<View>> zzbgu = new HashMap();
    boolean zzbgw = false;

    public zzk(FrameLayout frameLayout, FrameLayout frameLayout2) {
        this.zzbgt = frameLayout;
        this.zzaiz = frameLayout2;
        zzu.zzgk();
        zzlc.zza((View) this.zzbgt, (ViewTreeObserver.OnGlobalLayoutListener) this);
        zzu.zzgk();
        zzlc.zza((View) this.zzbgt, (ViewTreeObserver.OnScrollChangedListener) this);
        this.zzbgt.setOnTouchListener(this);
        this.zzbgt.setOnClickListener(this);
    }

    private void zzh(View view) {
        if (this.zzbfp != null) {
            zzh zzla = this.zzbfp instanceof zzg ? ((zzg) this.zzbfp).zzla() : this.zzbfp;
            if (zzla != null) {
                zzla.zzh(view);
            }
        }
    }

    private int zzx(int i) {
        return zzm.zziw().zzb(this.zzbfp.getContext(), i);
    }

    @Override // com.google.android.gms.internal.zzdt
    public void destroy() {
        synchronized (this.zzail) {
            if (this.zzaiz != null) {
                this.zzaiz.removeAllViews();
            }
            this.zzaiz = null;
            this.zzbgu = null;
            this.zzbgv = null;
            this.zzbfp = null;
        }
    }

    @Override // android.view.ViewTreeObserver.OnScrollChangedListener
    public void onScrollChanged() {
        synchronized (this.zzail) {
            if (this.zzbfp != null) {
                this.zzbfp.zzg(this.zzbgt);
            }
        }
    }

    @Override // com.google.android.gms.internal.zzdt
    public com.google.android.gms.dynamic.zzd zzap(String str) {
        com.google.android.gms.dynamic.zzd zzac;
        synchronized (this.zzail) {
            WeakReference<View> weakReference = this.zzbgu.get(str);
            zzac = com.google.android.gms.dynamic.zze.zzac(weakReference == null ? null : weakReference.get());
        }
        return zzac;
    }

    @Override // com.google.android.gms.internal.zzdt
    public void zzc(String str, com.google.android.gms.dynamic.zzd zzdVar) {
        View view = (View) com.google.android.gms.dynamic.zze.zzad(zzdVar);
        synchronized (this.zzail) {
            if (view == null) {
                this.zzbgu.remove(str);
            } else {
                this.zzbgu.put(str, new WeakReference<>(view));
                view.setOnTouchListener(this);
                view.setClickable(true);
                view.setOnClickListener(this);
            }
        }
    }

    @Override // com.google.android.gms.internal.zzdt
    public void zze(com.google.android.gms.dynamic.zzd zzdVar) {
        synchronized (this.zzail) {
            zzh(null);
            Object zzad = com.google.android.gms.dynamic.zze.zzad(zzdVar);
            if (!(zzad instanceof zzi)) {
                zzkd.zzcx("Not an instance of native engine. This is most likely a transient error");
                return;
            }
            if (this.zzaiz != null) {
                this.zzaiz.setLayoutParams(new FrameLayout.LayoutParams(0, 0));
                this.zzbgt.requestLayout();
            }
            this.zzbgw = true;
            final zzi zziVar = (zzi) zzad;
            if (this.zzbfp != null) {
                if (((Boolean) zzu.zzfz().zzd(zzdc.zzbch)).booleanValue()) {
                    this.zzbfp.zzb(this.zzbgt, this.zzbgu);
                }
            }
            if ((this.zzbfp instanceof zzg) && ((zzg) this.zzbfp).zzkz()) {
                ((zzg) this.zzbfp).zzc(zziVar);
            } else {
                this.zzbfp = zziVar;
                if (zziVar instanceof zzg) {
                    ((zzg) zziVar).zzc(null);
                }
            }
            if (((Boolean) zzu.zzfz().zzd(zzdc.zzbch)).booleanValue()) {
                this.zzaiz.setClickable(false);
            }
            this.zzaiz.removeAllViews();
            this.zzbgv = zziVar.zza(this);
            if (this.zzbgv != null) {
                this.zzbgu.put("1007", new WeakReference<>(this.zzbgv.zzbfc));
                this.zzaiz.addView(this.zzbgv);
            }
            zzkh.zzclc.post(new Runnable() { // from class: com.google.android.gms.ads.internal.formats.zzk.1
                @Override // java.lang.Runnable
                public final void run() {
                    zzlh zzlb = zziVar.zzlb();
                    if (zzlb == null || zzk.this.zzaiz == null) {
                        return;
                    }
                    zzk.this.zzaiz.addView(zzlb.getView());
                }
            });
            zziVar.zza(this.zzbgt, this.zzbgu, this, this);
            zzh(this.zzbgt);
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        Point point;
        synchronized (this.zzail) {
            if (this.zzbfp == null) {
                return;
            }
            JSONObject jSONObject = new JSONObject();
            for (Map.Entry<String, WeakReference<View>> entry : this.zzbgu.entrySet()) {
                View view2 = entry.getValue().get();
                if (view2 != null) {
                    if (this.zzbgv == null || !this.zzbgv.zzbfc.equals(view2)) {
                        point = new Point();
                        view2.getGlobalVisibleRect(new Rect(), point);
                    } else {
                        Point point2 = new Point();
                        this.zzbgt.getGlobalVisibleRect(new Rect(), point2);
                        Point point3 = new Point();
                        view2.getGlobalVisibleRect(new Rect(), point3);
                        point = new Point(point3.x - point2.x, point3.y - point2.y);
                    }
                    JSONObject jSONObject2 = new JSONObject();
                    try {
                        jSONObject2.put("width", zzx(view2.getWidth()));
                        jSONObject2.put("height", zzx(view2.getHeight()));
                        jSONObject2.put("x", zzx(point.x));
                        jSONObject2.put("y", zzx(point.y));
                        jSONObject.put(entry.getKey(), jSONObject2);
                    } catch (JSONException e) {
                        String valueOf = String.valueOf(entry.getKey());
                        zzkd.zzcx(valueOf.length() != 0 ? "Unable to get view rectangle for view ".concat(valueOf) : new String("Unable to get view rectangle for view "));
                    }
                }
            }
            JSONObject jSONObject3 = new JSONObject();
            try {
                jSONObject3.put("x", zzx(this.zzbgx));
                jSONObject3.put("y", zzx(this.zzbgy));
            } catch (JSONException e2) {
                zzkd.zzcx("Unable to get click location");
            }
            JSONObject jSONObject4 = new JSONObject();
            try {
                jSONObject4.put("width", zzx(this.zzbgt.getMeasuredWidth()));
                jSONObject4.put("height", zzx(this.zzbgt.getMeasuredHeight()));
            } catch (JSONException e3) {
                zzkd.zzcx("Unable to get native ad view bounding box");
            }
            if (this.zzbgv == null || !this.zzbgv.zzbfc.equals(view)) {
                this.zzbfp.zza(view, this.zzbgu, jSONObject, jSONObject3, jSONObject4);
            } else if (!(this.zzbfp instanceof zzg) || ((zzg) this.zzbfp).zzla() == null) {
                this.zzbfp.zza("1007", jSONObject, jSONObject3, jSONObject4);
            } else {
                ((zzg) this.zzbfp).zzla().zza("1007", jSONObject, jSONObject3, jSONObject4);
            }
        }
    }

    @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
    public void onGlobalLayout() {
        synchronized (this.zzail) {
            if (this.zzbgw) {
                int measuredWidth = this.zzbgt.getMeasuredWidth();
                int measuredHeight = this.zzbgt.getMeasuredHeight();
                if (measuredWidth != 0 && measuredHeight != 0 && this.zzaiz != null) {
                    this.zzaiz.setLayoutParams(new FrameLayout.LayoutParams(measuredWidth, measuredHeight));
                    this.zzbgw = false;
                }
            }
            if (this.zzbfp != null) {
                this.zzbfp.zzg(this.zzbgt);
            }
        }
    }

    @Override // android.view.View.OnTouchListener
    public boolean onTouch(View view, MotionEvent motionEvent) {
        synchronized (this.zzail) {
            if (this.zzbfp != null) {
                this.zzbgt.getLocationOnScreen(new int[2]);
                Point point = new Point((int) (motionEvent.getRawX() - r0[0]), (int) (motionEvent.getRawY() - r0[1]));
                this.zzbgx = point.x;
                this.zzbgy = point.y;
                MotionEvent obtain = MotionEvent.obtain(motionEvent);
                obtain.setLocation(point.x, point.y);
                this.zzbfp.zzb(obtain);
                obtain.recycle();
            }
        }
        return false;
    }
}
