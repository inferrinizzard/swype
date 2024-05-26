package com.flurry.sdk;

import com.google.api.client.http.HttpMethods;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public class kn extends lx {
    static final String e = kn.class.getSimpleName();
    private int a;
    private int b;
    private HttpURLConnection d;
    public String f;
    public a g;
    public c k;
    public boolean l;
    public Exception o;
    public boolean r;
    public boolean t;
    private boolean x;
    private boolean y;
    public int h = 10000;
    public int i = 15000;
    public boolean j = true;
    private final jw<String, String> c = new jw<>();
    long m = -1;
    public long n = -1;
    public int p = -1;
    public final jw<String, String> q = new jw<>();
    private final Object z = new Object();
    public int s = 25000;
    private km A = new km(this);

    /* loaded from: classes.dex */
    public interface c {
        void a(kn knVar, InputStream inputStream) throws Exception;

        void a(OutputStream outputStream) throws Exception;

        void a$7aa0d203();
    }

    /* loaded from: classes.dex */
    public enum a {
        kUnknown,
        kGet,
        kPost,
        kPut,
        kDelete,
        kHead;

        @Override // java.lang.Enum
        public final String toString() {
            switch (this) {
                case kPost:
                    return "POST";
                case kPut:
                    return "PUT";
                case kDelete:
                    return "DELETE";
                case kHead:
                    return HttpMethods.HEAD;
                case kGet:
                    return "GET";
                default:
                    return null;
            }
        }
    }

    public final void a(String str, String str2) {
        this.c.a((jw<String, String>) str, str2);
    }

    public final boolean b() {
        boolean z;
        synchronized (this.z) {
            z = this.y;
        }
        return z;
    }

    public final boolean c() {
        return !e() && d();
    }

    public final boolean d() {
        return this.p >= 200 && this.p < 400 && !this.t;
    }

    public final boolean e() {
        return this.o != null;
    }

    public final List<String> a(String str) {
        return this.q.a(str);
    }

    public final void f() {
        kf.a(3, e, "Cancelling http request: " + this.f);
        synchronized (this.z) {
            this.y = true;
        }
        if (this.x) {
            return;
        }
        this.x = true;
        if (this.d == null) {
            return;
        }
        new Thread() { // from class: com.flurry.sdk.kn.1
            @Override // java.lang.Thread, java.lang.Runnable
            public final void run() {
                try {
                    if (kn.this.d != null) {
                        kn.this.d.disconnect();
                    }
                } catch (Throwable th) {
                }
            }
        }.start();
    }

    @Override // com.flurry.sdk.lw
    public void a() {
        try {
            if (this.f == null) {
                return;
            }
            if (!jk.a().b) {
                kf.a(3, e, "Network not available, aborting http request: " + this.f);
                return;
            }
            if (this.g == null || a.kUnknown.equals(this.g)) {
                this.g = a.kGet;
            }
            i();
            kf.a(4, e, "HTTP status: " + this.p + " for url: " + this.f);
        } catch (Exception e2) {
            kf.a(4, e, "HTTP status: " + this.p + " for url: " + this.f);
            kf.a(3, e, "Exception during http request: " + this.f, e2);
            this.b = this.d.getReadTimeout();
            this.a = this.d.getConnectTimeout();
            this.o = e2;
        } finally {
            this.A.a();
            h();
        }
    }

    @Override // com.flurry.sdk.lx
    public final void g() {
        f();
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void i() throws Exception {
        BufferedOutputStream bufferedOutputStream;
        BufferedInputStream bufferedInputStream;
        OutputStream outputStream = null;
        if (this.y) {
            return;
        }
        this.f = lr.a(this.f);
        try {
            this.d = (HttpURLConnection) new URL(this.f).openConnection();
            this.d.setConnectTimeout(this.h);
            this.d.setReadTimeout(this.i);
            this.d.setRequestMethod(this.g.toString());
            this.d.setInstanceFollowRedirects(this.j);
            this.d.setDoOutput(a.kPost.equals(this.g));
            this.d.setDoInput(true);
            for (Map.Entry<String, String> entry : this.c.b()) {
                this.d.addRequestProperty(entry.getKey(), entry.getValue());
            }
            if (!a.kGet.equals(this.g) && !a.kPost.equals(this.g)) {
                this.d.setRequestProperty("Accept-Encoding", "");
            }
            if (this.y) {
                return;
            }
            if (a.kPost.equals(this.g)) {
                try {
                    OutputStream outputStream2 = this.d.getOutputStream();
                    try {
                        bufferedOutputStream = new BufferedOutputStream(outputStream2);
                        try {
                            if (this.k != null && !b()) {
                                this.k.a(bufferedOutputStream);
                            }
                            lr.a(bufferedOutputStream);
                            lr.a(outputStream2);
                        } catch (Throwable th) {
                            th = th;
                            outputStream = outputStream2;
                            lr.a(bufferedOutputStream);
                            lr.a(outputStream);
                            throw th;
                        }
                    } catch (Throwable th2) {
                        th = th2;
                        bufferedOutputStream = null;
                        outputStream = outputStream2;
                    }
                } catch (Throwable th3) {
                    th = th3;
                    bufferedOutputStream = null;
                }
            }
            if (this.l) {
                this.m = System.currentTimeMillis();
            }
            if (this.r) {
                this.A.a(this.s);
            }
            this.p = this.d.getResponseCode();
            if (this.l && this.m != -1) {
                this.n = System.currentTimeMillis() - this.m;
            }
            this.A.a();
            for (Map.Entry<String, List<String>> entry2 : this.d.getHeaderFields().entrySet()) {
                Iterator<String> it = entry2.getValue().iterator();
                while (it.hasNext()) {
                    this.q.a((jw<String, String>) entry2.getKey(), it.next());
                }
            }
            if (!a.kGet.equals(this.g) && !a.kPost.equals(this.g)) {
                return;
            }
            if (this.y) {
                return;
            }
            try {
                InputStream inputStream = this.d.getInputStream();
                try {
                    BufferedInputStream bufferedInputStream2 = new BufferedInputStream(inputStream);
                    try {
                        if (this.k != null && !b()) {
                            this.k.a(this, bufferedInputStream2);
                        }
                        lr.a((Closeable) bufferedInputStream2);
                        lr.a((Closeable) inputStream);
                    } catch (Throwable th4) {
                        th = th4;
                        outputStream = inputStream;
                        bufferedInputStream = bufferedInputStream2;
                        lr.a((Closeable) bufferedInputStream);
                        lr.a(outputStream);
                        throw th;
                    }
                } catch (Throwable th5) {
                    th = th5;
                    bufferedInputStream = null;
                    outputStream = inputStream;
                }
            } catch (Throwable th6) {
                th = th6;
                bufferedInputStream = null;
            }
        } finally {
            j();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void h() {
        if (this.k != null && !b()) {
            this.k.a$7aa0d203();
        }
    }

    private void j() {
        if (!this.x) {
            this.x = true;
            if (this.d != null) {
                this.d.disconnect();
            }
        }
    }
}
