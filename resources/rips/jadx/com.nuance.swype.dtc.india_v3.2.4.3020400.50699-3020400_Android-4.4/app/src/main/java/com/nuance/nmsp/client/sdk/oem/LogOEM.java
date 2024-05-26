package com.nuance.nmsp.client.sdk.oem;

import android.util.Log;
import com.nuance.nmsp.client.sdk.common.oem.api.LogFactory;

/* loaded from: classes.dex */
public class LogOEM extends LogFactory.Log {
    private String a;

    public LogOEM(Class cls) {
        this.a = cls.getSimpleName();
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.LogFactory.Log
    public void debug(Object obj) {
        new StringBuilder("[").append(this.a).append("] ").append(obj.toString());
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.LogFactory.Log
    public void debug(Object obj, Throwable th) {
        th.printStackTrace();
        new StringBuilder("[").append(this.a).append("] ").append(obj.toString());
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.LogFactory.Log
    public void error(Object obj) {
        Log.e("NMSP", "[" + this.a + "] " + obj.toString());
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.LogFactory.Log
    public void error(Object obj, Throwable th) {
        th.printStackTrace();
        Log.e("NMSP", "[" + this.a + "] " + obj.toString(), th);
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.LogFactory.Log
    public void fatal(Object obj) {
        Log.e("NMSP", "[" + this.a + "] " + obj.toString());
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.LogFactory.Log
    public void fatal(Object obj, Throwable th) {
        th.printStackTrace();
        Log.e("NMSP", "[" + this.a + "] " + obj.toString(), th);
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.LogFactory.Log
    public void info(Object obj) {
        Log.i("NMSP", "[" + this.a + "] " + obj.toString());
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.LogFactory.Log
    public void info(Object obj, Throwable th) {
        th.printStackTrace();
        Log.i("NMSP", "[" + this.a + "] " + obj.toString(), th);
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.LogFactory.Log
    public boolean isDebugEnabled() {
        return Log.isLoggable("NMSP_", 3);
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.LogFactory.Log
    public boolean isErrorEnabled() {
        return Log.isLoggable("NMSP", 6);
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.LogFactory.Log
    public boolean isFatalEnabled() {
        return Log.isLoggable("NMSP", 6);
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.LogFactory.Log
    public boolean isInfoEnabled() {
        return Log.isLoggable("NMSP", 4);
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.LogFactory.Log
    public boolean isTraceEnabled() {
        return Log.isLoggable("NMSP_", 2);
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.LogFactory.Log
    public boolean isWarnEnabled() {
        return Log.isLoggable("NMSP", 5);
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.LogFactory.Log
    public void setCurrentSession(String str) {
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.LogFactory.Log
    public void trace(Object obj) {
        Log.v("NMSP_", "[" + this.a + "] " + obj.toString());
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.LogFactory.Log
    public void trace(Object obj, Throwable th) {
        th.printStackTrace();
        Log.v("NMSP_", "[" + this.a + "] " + obj.toString(), th);
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.LogFactory.Log
    public void unsetCurrentSession() {
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.LogFactory.Log
    public void warn(Object obj) {
        Log.w("NMSP", "[" + this.a + "] " + obj.toString());
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.LogFactory.Log
    public void warn(Object obj, Throwable th) {
        th.printStackTrace();
        Log.w("NMSP", "[" + this.a + "] " + obj.toString(), th);
    }
}
