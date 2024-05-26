package com.google.android.gms.ads.internal.overlay;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Looper;
import android.view.Display;
import android.view.WindowManager;
import com.google.android.gms.internal.zzin;

@zzin
/* loaded from: classes.dex */
final class zzv implements SensorEventListener {
    final SensorManager zzbui;
    private final Display zzbuk;
    private float[] zzbun;
    Handler zzbuo;
    zza zzbup;
    private final float[] zzbul = new float[9];
    private final float[] zzbum = new float[9];
    private final Object zzbuj = new Object();

    /* loaded from: classes.dex */
    interface zza {
        void zznz();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public zzv(Context context) {
        this.zzbui = (SensorManager) context.getSystemService("sensor");
        this.zzbuk = ((WindowManager) context.getSystemService("window")).getDefaultDisplay();
    }

    private void zzf(int i, int i2) {
        float f = this.zzbum[i];
        this.zzbum[i] = this.zzbum[i2];
        this.zzbum[i2] = f;
    }

    @Override // android.hardware.SensorEventListener
    public final void onAccuracyChanged(Sensor sensor, int i) {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void stop() {
        if (this.zzbuo == null) {
            return;
        }
        this.zzbui.unregisterListener(this);
        this.zzbuo.post(new Runnable() { // from class: com.google.android.gms.ads.internal.overlay.zzv.1
            @Override // java.lang.Runnable
            public final void run() {
                Looper.myLooper().quit();
            }
        });
        this.zzbuo = null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean zzb(float[] fArr) {
        boolean z = false;
        synchronized (this.zzbuj) {
            if (this.zzbun != null) {
                System.arraycopy(this.zzbun, 0, fArr, 0, this.zzbun.length);
                z = true;
            }
        }
        return z;
    }

    @Override // android.hardware.SensorEventListener
    public final void onSensorChanged(SensorEvent sensorEvent) {
        float[] fArr = sensorEvent.values;
        if (fArr[0] == 0.0f && fArr[1] == 0.0f && fArr[2] == 0.0f) {
            return;
        }
        synchronized (this.zzbuj) {
            if (this.zzbun == null) {
                this.zzbun = new float[9];
            }
        }
        SensorManager.getRotationMatrixFromVector(this.zzbul, fArr);
        switch (this.zzbuk.getRotation()) {
            case 1:
                SensorManager.remapCoordinateSystem(this.zzbul, 2, 129, this.zzbum);
                break;
            case 2:
                SensorManager.remapCoordinateSystem(this.zzbul, 129, 130, this.zzbum);
                break;
            case 3:
                SensorManager.remapCoordinateSystem(this.zzbul, 130, 1, this.zzbum);
                break;
            default:
                System.arraycopy(this.zzbul, 0, this.zzbum, 0, 9);
                break;
        }
        zzf(1, 3);
        zzf(2, 6);
        zzf(5, 7);
        synchronized (this.zzbuj) {
            System.arraycopy(this.zzbum, 0, this.zzbun, 0, 9);
        }
        if (this.zzbup != null) {
            this.zzbup.zznz();
        }
    }
}
