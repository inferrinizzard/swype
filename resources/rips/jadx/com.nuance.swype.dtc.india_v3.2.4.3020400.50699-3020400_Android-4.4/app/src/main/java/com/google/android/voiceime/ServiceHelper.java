package com.google.android.voiceime;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import com.nuance.swype.util.LogManager;

/* loaded from: classes.dex */
public class ServiceHelper extends Service {
    static final LogManager.Log log = LogManager.getLog("ServiceHelper");
    private final IBinder mBinder = new ServiceHelperBinder();
    Callback mCallback;

    /* loaded from: classes.dex */
    public interface Callback {
        void onResult(String str);
    }

    @Override // android.app.Service
    public IBinder onBind(Intent arg0) {
        return this.mBinder;
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        log.d("#onCreate");
    }

    @Override // android.app.Service
    public void onDestroy() {
        super.onDestroy();
        log.d("#onDestroy");
    }

    /* loaded from: classes.dex */
    public class ServiceHelperBinder extends Binder {
        public ServiceHelperBinder() {
        }
    }
}
