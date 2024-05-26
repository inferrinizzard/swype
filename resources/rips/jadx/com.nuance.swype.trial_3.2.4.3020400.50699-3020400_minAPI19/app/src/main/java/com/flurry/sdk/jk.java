package com.flurry.sdk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/* loaded from: classes.dex */
public final class jk extends BroadcastReceiver {
    private static jk c;
    boolean a;
    public boolean b;
    private boolean d;

    public static synchronized jk a() {
        jk jkVar;
        synchronized (jk.class) {
            if (c == null) {
                c = new jk();
            }
            jkVar = c;
        }
        return jkVar;
    }

    /* loaded from: classes.dex */
    public enum a {
        NONE_OR_UNKNOWN(0),
        NETWORK_AVAILABLE(1),
        WIFI(2),
        CELL(3);

        public int e;

        a(int i) {
            this.e = i;
        }
    }

    private jk() {
        this.d = false;
        Context context = jr.a().a;
        this.d = context.checkCallingOrSelfPermission("android.permission.ACCESS_NETWORK_STATE") == 0;
        this.b = a(context);
        if (this.d) {
            d();
        }
    }

    private synchronized void d() {
        if (!this.a) {
            Context context = jr.a().a;
            this.b = a(context);
            context.registerReceiver(this, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
            this.a = true;
        }
    }

    @Override // android.content.BroadcastReceiver
    public final void onReceive(Context context, Intent intent) {
        boolean a2 = a(context);
        if (this.b != a2) {
            this.b = a2;
            jj jjVar = new jj();
            jjVar.a = a2;
            jjVar.b = c();
            kb.a().a(jjVar);
        }
    }

    private boolean a(Context context) {
        if (!this.d || context == null) {
            return true;
        }
        NetworkInfo activeNetworkInfo = f().getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public final a c() {
        if (!this.d) {
            return a.NONE_OR_UNKNOWN;
        }
        NetworkInfo activeNetworkInfo = f().getActiveNetworkInfo();
        if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
            return a.NONE_OR_UNKNOWN;
        }
        switch (activeNetworkInfo.getType()) {
            case 0:
            case 2:
            case 3:
            case 4:
            case 5:
                return a.CELL;
            case 1:
                return a.WIFI;
            case 6:
            case 7:
            default:
                if (activeNetworkInfo.isConnected()) {
                    return a.NETWORK_AVAILABLE;
                }
                return a.NONE_OR_UNKNOWN;
            case 8:
                return a.NONE_OR_UNKNOWN;
        }
    }

    private static ConnectivityManager f() {
        return (ConnectivityManager) jr.a().a.getSystemService("connectivity");
    }
}
