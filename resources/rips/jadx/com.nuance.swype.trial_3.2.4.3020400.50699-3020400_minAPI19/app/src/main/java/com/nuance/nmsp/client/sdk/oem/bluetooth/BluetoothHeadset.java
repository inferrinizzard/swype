package com.nuance.nmsp.client.sdk.oem.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/* loaded from: classes.dex */
public class BluetoothHeadset {
    public static final String ACTION_AUDIO_STATE_CHANGED;
    public static final String ACTION_STATE_CHANGED;
    public static final int AUDIO_STATE_CONNECTED;
    public static final int AUDIO_STATE_DISCONNECTED;
    public static final String EXTRA_AUDIO_STATE;
    public static final String EXTRA_STATE;
    public static final int PRIORITY_OFF;
    public static final int PRIORITY_UNDEFINED;
    public static final int STATE_CONNECTED;
    public static final int STATE_CONNECTING;
    public static final int STATE_DISCONNECTED;
    public static final int STATE_ERROR;
    private static final Constructor<?> a;
    private static final Method b;
    private static final Method c;
    private static final Method d;
    private static final Method e;
    private static final Method f;
    private static final Method g;
    private static final Method h;
    private static final Class<?> i;
    private static final boolean j;
    private static final Method k;
    private static final Method l;
    private Object m;
    private ServiceListener n;

    /* loaded from: classes.dex */
    public interface ServiceListener {
        void onServiceConnected();

        void onServiceDisconnected();
    }

    static {
        Method method;
        boolean z;
        Reflection reflection = new Reflection();
        Class<?> classForName = reflection.getClassForName("android.bluetooth.BluetoothHeadset");
        Class<?> classForName2 = reflection.getClassForName("android.bluetooth.BluetoothHeadset$ServiceListener");
        a = reflection.getConstructor(classForName, Context.class, classForName2);
        Method methodOrNull = reflection.getMethodOrNull(classForName, "getState", new Class[0]);
        if (methodOrNull == null) {
            method = reflection.getMethod(classForName, "getState", BluetoothDevice.class);
            z = true;
        } else {
            method = methodOrNull;
            z = false;
        }
        b = method;
        j = z;
        e = reflection.getMethod(classForName, "getCurrentHeadset", new Class[0]);
        g = reflection.getMethod(classForName, "connectHeadset", BluetoothDevice.class);
        Method methodOrNull2 = reflection.getMethodOrNull(classForName, "disconnectHeadset", new Class[0]);
        if (methodOrNull2 == null) {
            methodOrNull2 = reflection.getMethod(classForName, "disconnectHeadset", BluetoothDevice.class);
        }
        f = methodOrNull2;
        c = reflection.getMethod(classForName, "startVoiceRecognition", new Class[0]);
        d = reflection.getMethod(classForName, "stopVoiceRecognition", new Class[0]);
        h = reflection.getMethod(classForName, "close", new Class[0]);
        k = reflection.getMethod(classForName, "getPriority", BluetoothDevice.class);
        l = reflection.getMethod(classForName, "setPriority", BluetoothDevice.class, Integer.TYPE);
        i = classForName2;
        ACTION_STATE_CHANGED = (String) reflection.getFieldValue(classForName, "ACTION_STATE_CHANGED");
        ACTION_AUDIO_STATE_CHANGED = (String) reflection.getFieldValue(classForName, "ACTION_AUDIO_STATE_CHANGED");
        EXTRA_STATE = (String) reflection.getFieldValue(classForName, "EXTRA_STATE");
        EXTRA_AUDIO_STATE = (String) reflection.getFieldValue(classForName, "EXTRA_AUDIO_STATE");
        STATE_ERROR = ((Integer) reflection.getFieldValue(classForName, "STATE_ERROR")).intValue();
        STATE_DISCONNECTED = ((Integer) reflection.getFieldValue(classForName, "STATE_DISCONNECTED")).intValue();
        STATE_CONNECTING = ((Integer) reflection.getFieldValue(classForName, "STATE_CONNECTING")).intValue();
        STATE_CONNECTED = ((Integer) reflection.getFieldValue(classForName, "STATE_CONNECTED")).intValue();
        AUDIO_STATE_DISCONNECTED = ((Integer) reflection.getFieldValue(classForName, "AUDIO_STATE_DISCONNECTED")).intValue();
        AUDIO_STATE_CONNECTED = ((Integer) reflection.getFieldValue(classForName, "AUDIO_STATE_CONNECTED")).intValue();
        PRIORITY_OFF = ((Integer) reflection.getFieldValue(classForName, "PRIORITY_OFF")).intValue();
        PRIORITY_UNDEFINED = ((Integer) reflection.getFieldValue(classForName, "PRIORITY_UNDEFINED", -1)).intValue();
    }

    public BluetoothHeadset(Context context, ServiceListener serviceListener) {
        this.n = serviceListener;
        try {
            this.m = a.newInstance(context, Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{i}, new InvocationHandler() { // from class: com.nuance.nmsp.client.sdk.oem.bluetooth.BluetoothHeadset.1
                @Override // java.lang.reflect.InvocationHandler
                public final Object invoke(Object obj, Method method, Object[] objArr) throws Throwable {
                    if (method.getName().equals("onServiceConnected")) {
                        if (BluetoothHeadset.this.n == null) {
                            return null;
                        }
                        BluetoothHeadset.this.n.onServiceConnected();
                        return null;
                    }
                    if (!method.getName().equals("onServiceDisconnected") || BluetoothHeadset.this.n == null) {
                        return null;
                    }
                    BluetoothHeadset.this.n.onServiceDisconnected();
                    return null;
                }
            }));
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public void close() {
        try {
            h.invoke(this.m, new Object[0]);
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public boolean connectHeadset(BluetoothDevice bluetoothDevice) {
        try {
            if (g != null) {
                return ((Boolean) g.invoke(this.m, bluetoothDevice)).booleanValue();
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        return false;
    }

    public void disconnectHeadset() {
        try {
            if (f != null) {
                f.invoke(this.m, new Object[0]);
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public void disconnectHeadset(BluetoothDevice bluetoothDevice) {
        try {
            if (f != null) {
                f.invoke(this.m, bluetoothDevice);
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public BluetoothDevice getConnectedDevice() {
        try {
            if (e != null) {
                return (BluetoothDevice) e.invoke(this.m, new Object[0]);
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        return null;
    }

    public int getPriority(BluetoothDevice bluetoothDevice) {
        int i2 = PRIORITY_UNDEFINED;
        try {
            if (k != null) {
                return ((Integer) k.invoke(this.m, bluetoothDevice)).intValue();
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        return i2;
    }

    public int getState() {
        int intValue;
        try {
            if (j) {
                intValue = ((Integer) b.invoke(this.m, e.invoke(this.m, new Object[0]))).intValue();
            } else {
                intValue = ((Integer) b.invoke(this.m, new Object[0])).intValue();
            }
            return intValue;
        } catch (Exception e2) {
            e2.printStackTrace();
            return 0;
        }
    }

    public boolean setPriority(BluetoothDevice bluetoothDevice, int i2) {
        try {
            if (l != null) {
                return ((Boolean) l.invoke(this.m, bluetoothDevice, Integer.valueOf(i2))).booleanValue();
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        return false;
    }

    public boolean startVoiceRecognition() {
        try {
            return ((Boolean) c.invoke(this.m, new Object[0])).booleanValue();
        } catch (Exception e2) {
            e2.printStackTrace();
            return false;
        }
    }

    public boolean stopVoiceRecognition() {
        try {
            return ((Boolean) d.invoke(this.m, new Object[0])).booleanValue();
        } catch (Exception e2) {
            e2.printStackTrace();
            return false;
        }
    }
}
