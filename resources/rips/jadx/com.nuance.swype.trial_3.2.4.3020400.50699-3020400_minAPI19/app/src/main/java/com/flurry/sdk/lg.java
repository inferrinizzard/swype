package com.flurry.sdk;

import java.util.Timer;
import java.util.TimerTask;

/* loaded from: classes.dex */
final class lg {
    private Timer a;
    private a b;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class a extends TimerTask {
        a() {
        }

        @Override // java.util.TimerTask, java.lang.Runnable
        public final void run() {
            lg.this.a();
            kb.a().a(new lh());
        }
    }

    public final synchronized void a(long j) {
        boolean z;
        if (this.a != null) {
            z = true;
        } else {
            z = false;
        }
        if (z) {
            a();
        }
        this.a = new Timer("FlurrySessionTimer");
        this.b = new a();
        this.a.schedule(this.b, j);
    }

    public final synchronized void a() {
        if (this.a != null) {
            this.a.cancel();
            this.a = null;
        }
        this.b = null;
    }
}
