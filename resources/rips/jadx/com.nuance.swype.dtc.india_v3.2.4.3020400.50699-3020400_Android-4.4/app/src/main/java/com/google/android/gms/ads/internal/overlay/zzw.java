package com.google.android.gms.ads.internal.overlay;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Sensor;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import com.google.android.gms.ads.internal.overlay.zzv;
import com.google.android.gms.internal.zzcy;
import com.google.android.gms.internal.zzdc;
import com.google.android.gms.internal.zzin;
import com.google.android.gms.internal.zzkd;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.concurrent.CountDownLatch;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

@TargetApi(14)
@zzin
/* loaded from: classes.dex */
public class zzw extends Thread implements SurfaceTexture.OnFrameAvailableListener, zzv.zza {
    private static final float[] zzbur = {-1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, 1.0f, -1.0f};
    int zzaie;
    int zzaif;
    private final float[] zzbun;
    private final zzv zzbus;
    private final float[] zzbut;
    private final float[] zzbuu;
    private final float[] zzbuv;
    private final float[] zzbuw;
    private final float[] zzbux;
    private final float[] zzbuy;
    private float zzbuz;
    float zzbva;
    float zzbvb;
    private SurfaceTexture zzbvc;
    SurfaceTexture zzbvd;
    private int zzbve;
    private int zzbvf;
    private int zzbvg;
    private FloatBuffer zzbvh;
    private final CountDownLatch zzbvi;
    private final Object zzbvj;
    private EGL10 zzbvk;
    private EGLDisplay zzbvl;
    private EGLContext zzbvm;
    private EGLSurface zzbvn;
    private volatile boolean zzbvo;
    private volatile boolean zzbvp;

    private static void zza(float[] fArr, float f) {
        fArr[0] = 1.0f;
        fArr[1] = 0.0f;
        fArr[2] = 0.0f;
        fArr[3] = 0.0f;
        fArr[4] = (float) Math.cos(f);
        fArr[5] = (float) (-Math.sin(f));
        fArr[6] = 0.0f;
        fArr[7] = (float) Math.sin(f);
        fArr[8] = (float) Math.cos(f);
    }

    private static void zza(float[] fArr, float[] fArr2, float[] fArr3) {
        fArr[0] = (fArr2[0] * fArr3[0]) + (fArr2[1] * fArr3[3]) + (fArr2[2] * fArr3[6]);
        fArr[1] = (fArr2[0] * fArr3[1]) + (fArr2[1] * fArr3[4]) + (fArr2[2] * fArr3[7]);
        fArr[2] = (fArr2[0] * fArr3[2]) + (fArr2[1] * fArr3[5]) + (fArr2[2] * fArr3[8]);
        fArr[3] = (fArr2[3] * fArr3[0]) + (fArr2[4] * fArr3[3]) + (fArr2[5] * fArr3[6]);
        fArr[4] = (fArr2[3] * fArr3[1]) + (fArr2[4] * fArr3[4]) + (fArr2[5] * fArr3[7]);
        fArr[5] = (fArr2[3] * fArr3[2]) + (fArr2[4] * fArr3[5]) + (fArr2[5] * fArr3[8]);
        fArr[6] = (fArr2[6] * fArr3[0]) + (fArr2[7] * fArr3[3]) + (fArr2[8] * fArr3[6]);
        fArr[7] = (fArr2[6] * fArr3[1]) + (fArr2[7] * fArr3[4]) + (fArr2[8] * fArr3[7]);
        fArr[8] = (fArr2[6] * fArr3[2]) + (fArr2[7] * fArr3[5]) + (fArr2[8] * fArr3[8]);
    }

    private static void zzb(float[] fArr, float f) {
        fArr[0] = (float) Math.cos(f);
        fArr[1] = (float) (-Math.sin(f));
        fArr[2] = 0.0f;
        fArr[3] = (float) Math.sin(f);
        fArr[4] = (float) Math.cos(f);
        fArr[5] = 0.0f;
        fArr[6] = 0.0f;
        fArr[7] = 0.0f;
        fArr[8] = 1.0f;
    }

    private static void zzbx(String str) {
        int glGetError = GLES20.glGetError();
        if (glGetError != 0) {
            Log.e("SphericalVideoRenderer", new StringBuilder(String.valueOf(str).length() + 21).append(str).append(": glError ").append(glGetError).toString());
        }
    }

    private static int zzc(int i, String str) {
        int glCreateShader = GLES20.glCreateShader(i);
        zzbx("createShader");
        if (glCreateShader != 0) {
            GLES20.glShaderSource(glCreateShader, str);
            zzbx("shaderSource");
            GLES20.glCompileShader(glCreateShader);
            zzbx("compileShader");
            int[] iArr = new int[1];
            GLES20.glGetShaderiv(glCreateShader, 35713, iArr, 0);
            zzbx("getShaderiv");
            if (iArr[0] == 0) {
                Log.e("SphericalVideoRenderer", new StringBuilder(37).append("Could not compile shader ").append(i).append(":").toString());
                Log.e("SphericalVideoRenderer", GLES20.glGetShaderInfoLog(glCreateShader));
                GLES20.glDeleteShader(glCreateShader);
                zzbx("deleteShader");
                return 0;
            }
        }
        return glCreateShader;
    }

    private boolean zzpg() {
        boolean z = false;
        if (this.zzbvn != null && this.zzbvn != EGL10.EGL_NO_SURFACE) {
            z = this.zzbvk.eglMakeCurrent(this.zzbvl, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT) | false | this.zzbvk.eglDestroySurface(this.zzbvl, this.zzbvn);
            this.zzbvn = null;
        }
        if (this.zzbvm != null) {
            z |= this.zzbvk.eglDestroyContext(this.zzbvl, this.zzbvm);
            this.zzbvm = null;
        }
        if (this.zzbvl == null) {
            return z;
        }
        boolean eglTerminate = z | this.zzbvk.eglTerminate(this.zzbvl);
        this.zzbvl = null;
        return eglTerminate;
    }

    @Override // android.graphics.SurfaceTexture.OnFrameAvailableListener
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        this.zzbvg++;
        synchronized (this.zzbvj) {
            this.zzbvj.notifyAll();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void zzg(int i, int i2) {
        synchronized (this.zzbvj) {
            this.zzaie = i;
            this.zzaif = i2;
            this.zzbvo = true;
            this.zzbvj.notifyAll();
        }
    }

    @Override // com.google.android.gms.ads.internal.overlay.zzv.zza
    public void zznz() {
        synchronized (this.zzbvj) {
            this.zzbvj.notifyAll();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void zzow() {
        synchronized (this.zzbvj) {
            this.zzbvp = true;
            this.zzbvd = null;
            this.zzbvj.notifyAll();
        }
    }

    public SurfaceTexture zzox() {
        if (this.zzbvd == null) {
            return null;
        }
        try {
            this.zzbvi.await();
        } catch (InterruptedException e) {
        }
        return this.zzbvc;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public zzw(Context context) {
        super("SphericalVideoProcessor");
        this.zzbvh = ByteBuffer.allocateDirect(zzbur.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        this.zzbvh.put(zzbur).position(0);
        this.zzbun = new float[9];
        this.zzbut = new float[9];
        this.zzbuu = new float[9];
        this.zzbuv = new float[9];
        this.zzbuw = new float[9];
        this.zzbux = new float[9];
        this.zzbuy = new float[9];
        this.zzbuz = Float.NaN;
        this.zzbus = new zzv(context);
        this.zzbus.zzbup = this;
        this.zzbvi = new CountDownLatch(1);
        this.zzbvj = new Object();
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v105 */
    /* JADX WARN: Type inference failed for: r0v61 */
    /* JADX WARN: Type inference failed for: r0v62 */
    /* JADX WARN: Type inference failed for: r11v1 */
    /* JADX WARN: Type inference failed for: r11v3 */
    /* JADX WARN: Type inference failed for: r11v5 */
    /* JADX WARN: Type inference failed for: r11v6 */
    /* JADX WARN: Type inference failed for: r11v7 */
    /* JADX WARN: Type inference failed for: r11v8 */
    /* JADX WARN: Type inference failed for: r1v11 */
    /* JADX WARN: Type inference failed for: r1v14 */
    /* JADX WARN: Type inference failed for: r1v15 */
    /* JADX WARN: Type inference failed for: r1v16 */
    /* JADX WARN: Type inference failed for: r1v17 */
    /* JADX WARN: Type inference failed for: r1v18 */
    /* JADX WARN: Type inference failed for: r1v35 */
    /* JADX WARN: Type inference failed for: r1v8 */
    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
        ?? r1;
        int glCreateProgram;
        if (this.zzbvd == null) {
            zzkd.e("SphericalVideoProcessor started with no output texture.");
            this.zzbvi.countDown();
            return;
        }
        this.zzbvk = (EGL10) EGLContext.getEGL();
        this.zzbvl = this.zzbvk.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
        if (this.zzbvl == EGL10.EGL_NO_DISPLAY) {
            r1 = false;
        } else {
            if (this.zzbvk.eglInitialize(this.zzbvl, new int[2])) {
                int[] iArr = new int[1];
                EGLConfig[] eGLConfigArr = new EGLConfig[1];
                EGLConfig eGLConfig = (!this.zzbvk.eglChooseConfig(this.zzbvl, new int[]{12352, 4, 12324, 8, 12323, 8, 12322, 8, 12325, 16, 12344}, eGLConfigArr, 1, iArr) || iArr[0] <= 0) ? null : eGLConfigArr[0];
                if (eGLConfig == null) {
                    r1 = false;
                } else {
                    this.zzbvm = this.zzbvk.eglCreateContext(this.zzbvl, eGLConfig, EGL10.EGL_NO_CONTEXT, new int[]{12440, 2, 12344});
                    if (this.zzbvm == null || this.zzbvm == EGL10.EGL_NO_CONTEXT) {
                        r1 = false;
                    } else {
                        this.zzbvn = this.zzbvk.eglCreateWindowSurface(this.zzbvl, eGLConfig, this.zzbvd, null);
                        r1 = (this.zzbvn == null || this.zzbvn == EGL10.EGL_NO_SURFACE) ? false : this.zzbvk.eglMakeCurrent(this.zzbvl, this.zzbvn, this.zzbvn, this.zzbvm);
                    }
                }
            } else {
                r1 = false;
            }
        }
        zzcy<String> zzcyVar = zzdc.zzbam;
        int zzc = zzc(35633, !((String) com.google.android.gms.ads.internal.zzu.zzfz().zzd(zzcyVar)).equals(zzcyVar.zzaxq) ? (String) com.google.android.gms.ads.internal.zzu.zzfz().zzd(zzcyVar) : "attribute highp vec3 aPosition;varying vec3 pos;void main() {  gl_Position = vec4(aPosition, 1.0);  pos = aPosition;}");
        if (zzc == 0) {
            glCreateProgram = 0;
        } else {
            zzcy<String> zzcyVar2 = zzdc.zzban;
            int zzc2 = zzc(35632, !((String) com.google.android.gms.ads.internal.zzu.zzfz().zzd(zzcyVar2)).equals(zzcyVar2.zzaxq) ? (String) com.google.android.gms.ads.internal.zzu.zzfz().zzd(zzcyVar2) : "#extension GL_OES_EGL_image_external : require\n#define INV_PI 0.3183\nprecision highp float;varying vec3 pos;uniform samplerExternalOES uSplr;uniform mat3 uVMat;uniform float uFOVx;uniform float uFOVy;void main() {  vec3 ray = vec3(pos.x * tan(uFOVx), pos.y * tan(uFOVy), -1);  ray = (uVMat * ray).xyz;  ray = normalize(ray);  vec2 texCrd = vec2(    0.5 + atan(ray.x, - ray.z) * INV_PI * 0.5, acos(ray.y) * INV_PI);  gl_FragColor = vec4(texture2D(uSplr, texCrd).xyz, 1.0);}");
            if (zzc2 == 0) {
                glCreateProgram = 0;
            } else {
                glCreateProgram = GLES20.glCreateProgram();
                zzbx("createProgram");
                if (glCreateProgram != 0) {
                    GLES20.glAttachShader(glCreateProgram, zzc);
                    zzbx("attachShader");
                    GLES20.glAttachShader(glCreateProgram, zzc2);
                    zzbx("attachShader");
                    GLES20.glLinkProgram(glCreateProgram);
                    zzbx("linkProgram");
                    int[] iArr2 = new int[1];
                    GLES20.glGetProgramiv(glCreateProgram, 35714, iArr2, 0);
                    zzbx("getProgramiv");
                    if (iArr2[0] != 1) {
                        Log.e("SphericalVideoRenderer", "Could not link program: ");
                        Log.e("SphericalVideoRenderer", GLES20.glGetProgramInfoLog(glCreateProgram));
                        GLES20.glDeleteProgram(glCreateProgram);
                        zzbx("deleteProgram");
                        glCreateProgram = 0;
                    } else {
                        GLES20.glValidateProgram(glCreateProgram);
                        zzbx("validateProgram");
                    }
                }
            }
        }
        this.zzbve = glCreateProgram;
        GLES20.glUseProgram(this.zzbve);
        zzbx("useProgram");
        int glGetAttribLocation = GLES20.glGetAttribLocation(this.zzbve, "aPosition");
        GLES20.glVertexAttribPointer(glGetAttribLocation, 3, 5126, false, 12, (Buffer) this.zzbvh);
        zzbx("vertexAttribPointer");
        GLES20.glEnableVertexAttribArray(glGetAttribLocation);
        zzbx("enableVertexAttribArray");
        int[] iArr3 = new int[1];
        GLES20.glGenTextures(1, iArr3, 0);
        zzbx("genTextures");
        int i = iArr3[0];
        GLES20.glBindTexture(36197, i);
        zzbx("bindTextures");
        GLES20.glTexParameteri(36197, 10240, 9729);
        zzbx("texParameteri");
        GLES20.glTexParameteri(36197, 10241, 9729);
        zzbx("texParameteri");
        GLES20.glTexParameteri(36197, 10242, 33071);
        zzbx("texParameteri");
        GLES20.glTexParameteri(36197, 10243, 33071);
        zzbx("texParameteri");
        this.zzbvf = GLES20.glGetUniformLocation(this.zzbve, "uVMat");
        GLES20.glUniformMatrix3fv(this.zzbvf, 1, false, new float[]{1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f}, 0);
        ?? r0 = this.zzbve != 0;
        if (r1 != true || r0 != true) {
            String valueOf = String.valueOf(GLUtils.getEGLErrorString(this.zzbvk.eglGetError()));
            String concat = valueOf.length() != 0 ? "EGL initialization failed: ".concat(valueOf) : new String("EGL initialization failed: ");
            zzkd.e(concat);
            com.google.android.gms.ads.internal.zzu.zzft().zzb(new Throwable(concat), true);
            zzpg();
            this.zzbvi.countDown();
            return;
        }
        this.zzbvc = new SurfaceTexture(i);
        this.zzbvc.setOnFrameAvailableListener(this);
        this.zzbvi.countDown();
        zzv zzvVar = this.zzbus;
        if (zzvVar.zzbuo == null) {
            Sensor defaultSensor = zzvVar.zzbui.getDefaultSensor(11);
            if (defaultSensor == null) {
                zzkd.e("No Sensor of TYPE_ROTATION_VECTOR");
            } else {
                HandlerThread handlerThread = new HandlerThread("OrientationMonitor");
                handlerThread.start();
                zzvVar.zzbuo = new Handler(handlerThread.getLooper());
                if (!zzvVar.zzbui.registerListener(zzvVar, defaultSensor, 0, zzvVar.zzbuo)) {
                    zzkd.e("SensorManager.registerListener failed.");
                    zzvVar.stop();
                }
            }
        }
        try {
            this.zzbvo = true;
            while (!this.zzbvp) {
                zzoy();
                if (this.zzbvo) {
                    GLES20.glViewport(0, 0, this.zzaie, this.zzaif);
                    zzbx("viewport");
                    int glGetUniformLocation = GLES20.glGetUniformLocation(this.zzbve, "uFOVx");
                    int glGetUniformLocation2 = GLES20.glGetUniformLocation(this.zzbve, "uFOVy");
                    if (this.zzaie > this.zzaif) {
                        GLES20.glUniform1f(glGetUniformLocation, 0.87266463f);
                        GLES20.glUniform1f(glGetUniformLocation2, (this.zzaif * 0.87266463f) / this.zzaie);
                    } else {
                        GLES20.glUniform1f(glGetUniformLocation, (this.zzaie * 0.87266463f) / this.zzaif);
                        GLES20.glUniform1f(glGetUniformLocation2, 0.87266463f);
                    }
                    this.zzbvo = false;
                }
                try {
                    synchronized (this.zzbvj) {
                        if (!this.zzbvp && !this.zzbvo && this.zzbvg == 0) {
                            this.zzbvj.wait();
                        }
                    }
                } catch (InterruptedException e) {
                }
            }
        } catch (IllegalStateException e2) {
            zzkd.zzcx("SphericalVideoProcessor halted unexpectedly.");
        } catch (Throwable th) {
            zzkd.zzb("SphericalVideoProcessor died.", th);
            com.google.android.gms.ads.internal.zzu.zzft().zzb(th, true);
        } finally {
            this.zzbus.stop();
            this.zzbvc.setOnFrameAvailableListener(null);
            this.zzbvc = null;
            zzpg();
        }
    }

    private void zzoy() {
        while (this.zzbvg > 0) {
            this.zzbvc.updateTexImage();
            this.zzbvg--;
        }
        if (this.zzbus.zzb(this.zzbun)) {
            if (Float.isNaN(this.zzbuz)) {
                float[] fArr = this.zzbun;
                float[] fArr2 = {0.0f, 1.0f, 0.0f};
                float[] fArr3 = {(fArr[0] * fArr2[0]) + (fArr[1] * fArr2[1]) + (fArr[2] * fArr2[2]), (fArr[3] * fArr2[0]) + (fArr[4] * fArr2[1]) + (fArr[5] * fArr2[2]), (fArr[8] * fArr2[2]) + (fArr[6] * fArr2[0]) + (fArr[7] * fArr2[1])};
                this.zzbuz = -(((float) Math.atan2(fArr3[1], fArr3[0])) - 1.5707964f);
            }
            zzb(this.zzbux, this.zzbuz + this.zzbva);
        } else {
            zza(this.zzbun, -1.5707964f);
            zzb(this.zzbux, this.zzbva);
        }
        zza(this.zzbut, 1.5707964f);
        zza(this.zzbuu, this.zzbux, this.zzbut);
        zza(this.zzbuv, this.zzbun, this.zzbuu);
        zza(this.zzbuw, this.zzbvb);
        zza(this.zzbuy, this.zzbuw, this.zzbuv);
        GLES20.glUniformMatrix3fv(this.zzbvf, 1, false, this.zzbuy, 0);
        GLES20.glDrawArrays(5, 0, 4);
        zzbx("drawArrays");
        GLES20.glFinish();
        this.zzbvk.eglSwapBuffers(this.zzbvl, this.zzbvn);
    }
}
