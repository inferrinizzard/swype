package com.flurry.sdk;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import com.flurry.sdk.lj;
import com.nuance.swype.input.IME;

/* loaded from: classes.dex */
public class ji implements lj.a {
    private static ji a;
    private static final String b = ji.class.getSimpleName();
    private boolean g;
    private Location h;
    private Location l;
    private final int c = 3;
    private final long d = IME.RETRY_DELAY_IN_MILLIS;
    private final long e = 90000;
    private final long f = 0;
    private long i = 0;
    private boolean m = false;
    private int n = 0;
    private ka<ll> o = new ka<ll>() { // from class: com.flurry.sdk.ji.1
        @Override // com.flurry.sdk.ka
        public final /* synthetic */ void a(ll llVar) {
            if (ji.this.i <= 0 || ji.this.i >= System.currentTimeMillis()) {
                return;
            }
            kf.a(4, ji.b, "No location received in 90 seconds , stopping LocationManager");
            ji.this.g();
        }
    };
    private LocationManager j = (LocationManager) jr.a().a.getSystemService("location");
    private a k = new a();

    static /* synthetic */ int c(ji jiVar) {
        int i = jiVar.n + 1;
        jiVar.n = i;
        return i;
    }

    public static synchronized ji a() {
        ji jiVar;
        synchronized (ji.class) {
            if (a == null) {
                a = new ji();
            }
            jiVar = a;
        }
        return jiVar;
    }

    private ji() {
        li a2 = li.a();
        this.g = ((Boolean) a2.a("ReportLocation")).booleanValue();
        a2.a("ReportLocation", (lj.a) this);
        kf.a(4, b, "initSettings, ReportLocation = " + this.g);
        this.h = (Location) a2.a("ExplicitLocation");
        a2.a("ExplicitLocation", (lj.a) this);
        kf.a(4, b, "initSettings, ExplicitLocation = " + this.h);
    }

    public final synchronized void c() {
        kf.a(4, b, "Location update requested");
        if (this.n < 3 && !this.m && this.g && this.h == null) {
            Context context = jr.a().a;
            if (context.checkCallingOrSelfPermission("android.permission.ACCESS_FINE_LOCATION") == 0 || context.checkCallingOrSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == 0) {
                this.n = 0;
                String str = null;
                if (!a(context)) {
                    if (b(context)) {
                        str = "network";
                    }
                } else {
                    str = "passive";
                }
                if (!TextUtils.isEmpty(str)) {
                    this.j.requestLocationUpdates(str, IME.RETRY_DELAY_IN_MILLIS, 0.0f, this.k, Looper.getMainLooper());
                }
                this.l = a(str);
                this.i = System.currentTimeMillis() + 90000;
                kf.a(4, b, "Register location timer");
                lm.a().a(this.o);
                this.m = true;
                kf.a(4, b, "LocationProvider started");
            }
        }
    }

    public final synchronized void d() {
        kf.a(4, b, "Stop update location requested");
        g();
    }

    public final Location e() {
        String str;
        Location location = null;
        if (this.h != null) {
            return this.h;
        }
        if (this.g) {
            Context context = jr.a().a;
            if (!a(context) && !b(context)) {
                return null;
            }
            if (a(context)) {
                str = "passive";
            } else {
                str = b(context) ? "network" : null;
            }
            if (str != null) {
                Location a2 = a(str);
                if (a2 != null) {
                    this.l = a2;
                }
                location = this.l;
            }
        }
        kf.a(4, b, "getLocation() = " + location);
        return location;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void g() {
        if (this.m) {
            this.j.removeUpdates(this.k);
            this.n = 0;
            this.i = 0L;
            kf.a(4, b, "Unregister location timer");
            lm.a().b(this.o);
            this.m = false;
            kf.a(4, b, "LocationProvider stopped");
        }
    }

    private static boolean a(Context context) {
        return context.checkCallingOrSelfPermission("android.permission.ACCESS_FINE_LOCATION") == 0;
    }

    private static boolean b(Context context) {
        return context.checkCallingOrSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == 0;
    }

    private Location a(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        return this.j.getLastKnownLocation(str);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class a implements LocationListener {
        public a() {
        }

        @Override // android.location.LocationListener
        public final void onStatusChanged(String str, int i, Bundle bundle) {
        }

        @Override // android.location.LocationListener
        public final void onProviderEnabled(String str) {
        }

        @Override // android.location.LocationListener
        public final void onProviderDisabled(String str) {
        }

        @Override // android.location.LocationListener
        public final void onLocationChanged(Location location) {
            if (location != null) {
                ji.this.l = location;
            }
            if (ji.c(ji.this) >= 3) {
                kf.a(4, ji.b, "Max location reports reached, stopping");
                ji.this.g();
            }
        }
    }

    @Override // com.flurry.sdk.lj.a
    public final void a(String str, Object obj) {
        char c = 65535;
        switch (str.hashCode()) {
            case -864112343:
                if (str.equals("ReportLocation")) {
                    c = 0;
                    break;
                }
                break;
            case -300729815:
                if (str.equals("ExplicitLocation")) {
                    c = 1;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                this.g = ((Boolean) obj).booleanValue();
                kf.a(4, b, "onSettingUpdate, ReportLocation = " + this.g);
                return;
            case 1:
                this.h = (Location) obj;
                kf.a(4, b, "onSettingUpdate, ExplicitLocation = " + this.h);
                return;
            default:
                kf.a(6, b, "LocationProvider internal error! Had to be LocationCriteria, ReportLocation or ExplicitLocation key.");
                return;
        }
    }
}
