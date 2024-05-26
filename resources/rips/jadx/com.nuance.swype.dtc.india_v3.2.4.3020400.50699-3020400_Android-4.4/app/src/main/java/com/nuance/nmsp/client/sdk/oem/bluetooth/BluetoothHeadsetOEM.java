package com.nuance.nmsp.client.sdk.oem.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Looper;
import com.nuance.nmsp.client.sdk.common.oem.api.LogFactory;
import com.nuance.nmsp.client.sdk.oem.bluetooth.BluetoothHeadset;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

/* loaded from: classes.dex */
public class BluetoothHeadsetOEM {
    public static String ACTION_AUDIO_STATE_CHANGED;
    public static int AUDIO_STATE_CONNECTED;
    public static String EXTRA_AUDIO_STATE;
    private static final LogFactory.Log a = LogFactory.getLog(Bluetooth.class);
    private Context b;
    private boolean c;
    private BluetoothHeadset d;
    private boolean e;
    private boolean f = false;
    private Object g = null;
    private Object i = new Object();
    private final Reflection h = new Reflection();

    public BluetoothHeadsetOEM(Context context) {
        BluetoothAdapter bluetoothAdapter;
        this.d = null;
        this.e = false;
        this.b = context;
        if (AndroidVersion.SDK <= 10) {
            this.c = true;
            ACTION_AUDIO_STATE_CHANGED = BluetoothHeadset.ACTION_AUDIO_STATE_CHANGED;
            EXTRA_AUDIO_STATE = BluetoothHeadset.EXTRA_AUDIO_STATE;
            AUDIO_STATE_CONNECTED = BluetoothHeadset.AUDIO_STATE_CONNECTED;
            BluetoothHeadset.ServiceListener serviceListener = new BluetoothHeadset.ServiceListener() { // from class: com.nuance.nmsp.client.sdk.oem.bluetooth.BluetoothHeadsetOEM.1
                @Override // com.nuance.nmsp.client.sdk.oem.bluetooth.BluetoothHeadset.ServiceListener
                public final void onServiceConnected() {
                    if (BluetoothHeadsetOEM.a.isInfoEnabled()) {
                        BluetoothHeadsetOEM.a.info("BluetoothHeadsetOEM reflected onServiceConnected()");
                    }
                    synchronized (BluetoothHeadsetOEM.this.i) {
                        if (BluetoothHeadsetOEM.this.e) {
                            BluetoothHeadsetOEM.this.b();
                        } else {
                            BluetoothHeadsetOEM.a(BluetoothHeadsetOEM.this, true);
                            BluetoothHeadsetOEM.this.i.notify();
                        }
                    }
                }

                @Override // com.nuance.nmsp.client.sdk.oem.bluetooth.BluetoothHeadset.ServiceListener
                public final void onServiceDisconnected() {
                    if (BluetoothHeadsetOEM.a.isInfoEnabled()) {
                        BluetoothHeadsetOEM.a.info("BluetoothHeadsetOEM reflected onServiceDisconnected()");
                    }
                    synchronized (BluetoothHeadsetOEM.this.i) {
                        BluetoothHeadsetOEM.this.b();
                        if (!BluetoothHeadsetOEM.this.e) {
                            BluetoothHeadsetOEM.a(BluetoothHeadsetOEM.this, true);
                            BluetoothHeadsetOEM.this.i.notify();
                        }
                    }
                }
            };
            synchronized (this.i) {
                long currentTimeMillis = System.currentTimeMillis();
                this.d = new BluetoothHeadset(this.b, serviceListener);
                try {
                    this.i.wait(1000L);
                } catch (InterruptedException e) {
                    if (a.isErrorEnabled()) {
                        a.error("BluetoothHeadsetOEM reflected mHeadsetSyncObj.wait() threw exception:" + e);
                    }
                }
                if (!this.f) {
                    if (a.isErrorEnabled()) {
                        a.error("BluetoothHeadsetOEM reflected service NOT connected in time. Gave up!!!");
                    }
                    this.e = true;
                    b();
                } else if (a.isInfoEnabled()) {
                    a.info("BluetoothHeadsetOEM reflected service connection took " + (System.currentTimeMillis() - currentTimeMillis) + "ms");
                }
            }
            return;
        }
        this.c = false;
        Class<?> classForName = this.h.getClassForName("android.bluetooth.BluetoothHeadset");
        ACTION_AUDIO_STATE_CHANGED = (String) this.h.getFieldValue(classForName, "ACTION_AUDIO_STATE_CHANGED");
        EXTRA_AUDIO_STATE = (String) this.h.getFieldValue(classForName, "EXTRA_STATE");
        AUDIO_STATE_CONNECTED = ((Integer) this.h.getFieldValue(classForName, "STATE_AUDIO_CONNECTED")).intValue();
        if (AndroidVersion.SDK == 11 || AndroidVersion.SDK == 12 || AndroidVersion.SDK == 13) {
            final Object[] objArr = new Object[2];
            new Thread(new Runnable(this) { // from class: com.nuance.nmsp.client.sdk.oem.bluetooth.BluetoothHeadsetOEM.2
                @Override // java.lang.Runnable
                public final void run() {
                    Looper.prepare();
                    objArr[1] = BluetoothAdapter.getDefaultAdapter();
                    objArr[0] = new Boolean(true);
                    Looper.loop();
                }
            }).start();
            while (objArr[0] == null) {
                try {
                    Thread.sleep(10L);
                } catch (Exception e2) {
                }
            }
            bluetoothAdapter = (BluetoothAdapter) objArr[1];
        } else {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        Class<?> classForName2 = this.h.getClassForName("android.bluetooth.BluetoothProfile$ServiceListener");
        Object newProxyInstance = Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{classForName2}, new InvocationHandler() { // from class: com.nuance.nmsp.client.sdk.oem.bluetooth.BluetoothHeadsetOEM.3
            @Override // java.lang.reflect.InvocationHandler
            public final Object invoke(Object obj, Method method, Object[] objArr2) throws Throwable {
                if (!method.getName().equals("onServiceConnected")) {
                    if (!method.getName().equals("onServiceDisconnected")) {
                        return null;
                    }
                    if (BluetoothHeadsetOEM.a.isInfoEnabled()) {
                        BluetoothHeadsetOEM.a.info("BluetoothHeadsetOEM native onServiceDisconnected()");
                    }
                    synchronized (BluetoothHeadsetOEM.this.i) {
                        if (!BluetoothHeadsetOEM.this.e) {
                            BluetoothHeadsetOEM.a(BluetoothHeadsetOEM.this, true);
                            BluetoothHeadsetOEM.this.i.notify();
                        }
                    }
                    return null;
                }
                if (BluetoothHeadsetOEM.a.isInfoEnabled()) {
                    BluetoothHeadsetOEM.a.info("BluetoothHeadsetOEM native onServiceConnected()");
                }
                synchronized (BluetoothHeadsetOEM.this.i) {
                    if (BluetoothHeadsetOEM.this.e) {
                        BluetoothHeadsetOEM.this.a(objArr2[1]);
                    } else {
                        BluetoothHeadsetOEM.a(BluetoothHeadsetOEM.this, true);
                        BluetoothHeadsetOEM.this.g = objArr2[1];
                        BluetoothHeadsetOEM.this.i.notify();
                    }
                }
                return null;
            }
        });
        synchronized (this.i) {
            long currentTimeMillis2 = System.currentTimeMillis();
            Class<?> classForName3 = this.h.getClassForName("android.bluetooth.BluetoothProfile");
            Class<?> classForName4 = this.h.getClassForName("android.bluetooth.BluetoothAdapter");
            try {
                if (!((Boolean) this.h.getMethod(classForName4, "isEnabled", new Class[0]).invoke(bluetoothAdapter, new Object[0])).booleanValue()) {
                    return;
                }
            } catch (Exception e3) {
                e3.printStackTrace();
            }
            try {
                this.h.getMethod(classForName4, "getProfileProxy", Context.class, classForName2, Integer.TYPE).invoke(bluetoothAdapter, context, newProxyInstance, this.h.getFieldValue(classForName3, "HEADSET"));
            } catch (Exception e4) {
                e4.printStackTrace();
            }
            try {
                this.i.wait(1000L);
            } catch (InterruptedException e5) {
                if (a.isErrorEnabled()) {
                    a.error("BluetoothHeadsetOEM native mHeadsetSyncObj.wait() threw exception:" + e5);
                }
            }
            if (!this.f) {
                if (a.isErrorEnabled()) {
                    a.error("BluetoothHeadsetOEM native service NOT connected in time. Gave up!!!");
                }
                this.e = true;
            } else if (a.isInfoEnabled()) {
                a.info("BluetoothHeadsetOEM native service connection took " + (System.currentTimeMillis() - currentTimeMillis2) + "ms");
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void a(Object obj) {
        Class<?> classForName = this.h.getClassForName("android.bluetooth.BluetoothProfile");
        try {
            this.h.getMethodOrNull(this.h.getClassForName("android.bluetooth.BluetoothAdapter"), "closeProfileProxy", Integer.TYPE, classForName).invoke(BluetoothAdapter.getDefaultAdapter(), (Integer) this.h.getFieldValue(classForName, "HEADSET"), obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static /* synthetic */ boolean a(BluetoothHeadsetOEM bluetoothHeadsetOEM, boolean z) {
        bluetoothHeadsetOEM.f = true;
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void b() {
        if (this.d != null) {
            this.d.close();
            this.d = null;
        }
    }

    public void close() {
        if (this.c) {
            b();
        } else if (this.g != null) {
            a(this.g);
            this.g = null;
        }
    }

    public BluetoothDevice getConnectedDevice() {
        List list;
        if (this.c && this.d != null) {
            return this.d.getConnectedDevice();
        }
        if (this.c || this.g == null) {
            return null;
        }
        try {
            list = (List) this.h.getMethodOrNull(this.h.getClassForName("android.bluetooth.BluetoothProfile"), "getConnectedDevices", new Class[0]).invoke(this.g, new Object[0]);
        } catch (Exception e) {
            e.printStackTrace();
            list = null;
        }
        if (list == null || list.size() <= 0) {
            return null;
        }
        return (BluetoothDevice) list.get(0);
    }

    public boolean startVoiceRecognition() {
        if (this.c) {
            if (this.d == null) {
                return false;
            }
            return this.d.startVoiceRecognition();
        }
        if (!a.isErrorEnabled()) {
            return false;
        }
        a.error("BluetoothHeadsetOEM startVoiceRecognition() called on native headset!!!");
        return false;
    }

    public boolean stopVoiceRecognition() {
        if (this.c) {
            if (this.d == null) {
                return false;
            }
            return this.d.stopVoiceRecognition();
        }
        if (!a.isErrorEnabled()) {
            return false;
        }
        a.error("BluetoothHeadsetOEM stopVoiceRecognition() called on native headset!!!");
        return false;
    }
}
