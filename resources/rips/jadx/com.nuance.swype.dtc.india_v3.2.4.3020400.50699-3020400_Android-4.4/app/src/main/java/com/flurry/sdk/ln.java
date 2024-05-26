package com.flurry.sdk;

/* loaded from: classes.dex */
public class ln {
    private static final String c = ln.class.getSimpleName();
    long a = 1000;
    boolean b = true;
    private boolean d = false;
    private lw e = new lw() { // from class: com.flurry.sdk.ln.1
        @Override // com.flurry.sdk.lw
        public final void a() {
            kb.a().a(new ll());
            if (ln.this.b && ln.this.d) {
                jr.a().a(ln.this.e, ln.this.a);
            }
        }
    };

    public final synchronized void a() {
        if (!this.d) {
            jr.a().a(this.e, this.a);
            this.d = true;
        }
    }

    public final synchronized void b() {
        if (this.d) {
            jr a = jr.a();
            lw lwVar = this.e;
            if (lwVar != null) {
                a.c.removeCallbacks(lwVar);
            }
            this.d = false;
        }
    }
}
