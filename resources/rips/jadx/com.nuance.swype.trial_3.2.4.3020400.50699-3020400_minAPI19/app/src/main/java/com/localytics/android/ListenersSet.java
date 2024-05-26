package com.localytics.android;

import android.content.Context;
import com.localytics.android.Localytics;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class ListenersSet<T> {
    private final T mProxy;
    private final Set<T> mListenersSet = Collections.newSetFromMap(new WeakHashMap());
    private WeakReference<T> mDevListenerWeak = new WeakReference<>(null);
    private T mDevListenerStrong = null;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ListenersSet(Class<T> cls) {
        this.mProxy = (T) Proxy.newProxyInstance(cls.getClassLoader(), new Class[]{cls}, new ListenersProxy());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public T getProxy() {
        return this.mProxy;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void add(T listener) {
        this.mListenersSet.add(listener);
    }

    synchronized void remove(T listener) {
        this.mListenersSet.remove(listener);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void setDevListener(T listener) {
        this.mListenersSet.remove(getDevListener());
        if (listener instanceof Context) {
            this.mDevListenerWeak = new WeakReference<>(listener);
            this.mDevListenerStrong = null;
        } else {
            this.mDevListenerStrong = listener;
            this.mDevListenerWeak = new WeakReference<>(null);
        }
        if (listener != null) {
            this.mListenersSet.add(listener);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public T getDevListener() {
        return this.mDevListenerStrong != null ? this.mDevListenerStrong : this.mDevListenerWeak.get();
    }

    /* loaded from: classes.dex */
    private class ListenersProxy implements InvocationHandler {
        private ListenersProxy() {
        }

        @Override // java.lang.reflect.InvocationHandler
        public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
            List<T> strongListeners;
            synchronized (ListenersSet.this) {
                strongListeners = new LinkedList<>(ListenersSet.this.mListenersSet);
            }
            for (T listener : strongListeners) {
                try {
                    method.invoke(listener, objects);
                } catch (Exception e) {
                    Localytics.Log.e(String.format("%s method on listener threw exception", method.getName()), e);
                }
            }
            return null;
        }
    }
}
