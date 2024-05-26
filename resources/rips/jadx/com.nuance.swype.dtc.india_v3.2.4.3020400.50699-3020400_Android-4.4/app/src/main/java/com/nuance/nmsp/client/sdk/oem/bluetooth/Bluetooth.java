package com.nuance.nmsp.client.sdk.oem.bluetooth;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.nuance.nmsp.client.sdk.common.oem.api.LogFactory;

/* loaded from: classes.dex */
public abstract class Bluetooth {
    private static final LogFactory.Log a = LogFactory.getLog(Bluetooth.class);
    private Object b = new Object();
    protected Context mContext;
    protected BluetoothHeadsetOEM mHeadset;

    /* JADX INFO: Access modifiers changed from: protected */
    public Bluetooth(Context context) {
        this.mContext = context;
        this.mHeadset = new BluetoothHeadsetOEM(this.mContext);
    }

    public static Bluetooth createInstance(Context context) {
        int i = AndroidVersion.SDK;
        return (i < 8 || AndroidVersion.IS_BROKEN_HTC) ? new b(context) : i < 9 ? new c(context) : new a(context);
    }

    public void close() {
        if (this.mHeadset != null) {
            try {
                this.mHeadset.close();
            } catch (Throwable th) {
            }
            this.mHeadset = null;
        }
    }

    public abstract int getPlaybackStream();

    public abstract int getRecordingSource();

    public boolean isHeadsetConnected() {
        return this.mHeadset.getConnectedDevice() != null;
    }

    public void startBluetoothSco() {
        IntentFilter intentFilter = new IntentFilter(BluetoothHeadsetOEM.ACTION_AUDIO_STATE_CHANGED);
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { // from class: com.nuance.nmsp.client.sdk.oem.bluetooth.Bluetooth.1
            @Override // android.content.BroadcastReceiver
            public final void onReceive(Context context, Intent intent) {
                int intExtra = intent.getIntExtra(BluetoothHeadsetOEM.EXTRA_AUDIO_STATE, -1);
                if (Bluetooth.a.isInfoEnabled()) {
                    Bluetooth.a.info("BluetoothHeadset BroadcastReceiver " + intExtra);
                }
                synchronized (Bluetooth.this.b) {
                    if (intExtra == BluetoothHeadsetOEM.AUDIO_STATE_CONNECTED) {
                        if (Bluetooth.a.isInfoEnabled()) {
                            Bluetooth.a.info("BluetoothHeadset SCO connected. Notify wait lock.");
                        }
                        Bluetooth.this.b.notify();
                    }
                }
            }
        };
        synchronized (this.b) {
            this.mContext.registerReceiver(broadcastReceiver, intentFilter);
            long currentTimeMillis = System.currentTimeMillis();
            startBluetoothScoInternal();
            try {
                this.b.wait(3000L);
            } catch (InterruptedException e) {
                if (a.isErrorEnabled()) {
                    a.error("BluetoothHeadset mHeadsetScoSyncObj.wait() threw exception:" + e);
                }
            }
            if (a.isInfoEnabled()) {
                a.info("BluetoothHeadset " + (System.currentTimeMillis() - currentTimeMillis) + "ms to wait for SCO");
            }
        }
        this.mContext.unregisterReceiver(broadcastReceiver);
    }

    public abstract void startBluetoothScoInternal();

    public abstract void stopBluetoothSco();
}
