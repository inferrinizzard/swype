package com.nuance.nmdp.speechkit.oem;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;
import com.facebook.internal.ServerProtocol;
import com.nuance.nmsp.client.sdk.common.defines.NMSPDefines;
import com.nuance.nmsp.client.sdk.components.general.ResourceUtil;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Locale;

/* loaded from: classes.dex */
public class OemConfig {
    private final Context _appContext;
    private boolean _emulator = Build.PRODUCT.equals(ServerProtocol.DIALOG_PARAM_SDK_VERSION);

    public OemConfig(Object appContext) {
        this._appContext = (Context) appContext;
    }

    public String carrier() {
        String carrier = ((TelephonyManager) this._appContext.getSystemService("phone")).getNetworkOperatorName();
        if (carrier == null || carrier.length() == 0) {
            return "carrier";
        }
        return carrier;
    }

    public String defaultLanguage() {
        String lang;
        Locale l = Locale.getDefault();
        if (l == null || (lang = l.toString()) == null || lang.length() <= 0) {
            return null;
        }
        return lang;
    }

    public String model() {
        return Build.MODEL;
    }

    public String os() {
        return Build.VERSION.RELEASE;
    }

    public String locale() {
        String c;
        Locale l = Locale.getDefault();
        if (l == null || (c = l.getCountry()) == null || c.length() <= 0) {
            return null;
        }
        return c;
    }

    public String networkType() {
        String name;
        NetworkInfo ni = ((ConnectivityManager) this._appContext.getSystemService("connectivity")).getActiveNetworkInfo();
        if (ni == null || (name = ni.getTypeName()) == null || name.length() <= 0) {
            return null;
        }
        return ni.getTypeName();
    }

    public String uid() {
        String uid = ResourceUtil.getUniqueID(this._appContext);
        if (uid == null || uid.length() != 0) {
            return uid;
        }
        return null;
    }

    public NMSPDefines.Codec recorderCodec() {
        return (((AudioManager) this._appContext.getSystemService("audio")).isBluetoothScoOn() || this._emulator) ? NMSPDefines.Codec.SPEEX_8K : NMSPDefines.Codec.SPEEX_16K;
    }

    public NMSPDefines.Codec playerCodec() {
        return ((AudioManager) this._appContext.getSystemService("audio")).isBluetoothScoOn() ? NMSPDefines.Codec.SPEEX_8K : NMSPDefines.Codec.SPEEX_16K;
    }

    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:14:0x0033 -> B:5:0x0013). Please report as a decompilation issue!!! */
    public byte[] appName() {
        byte[] bArr;
        ApplicationInfo info;
        try {
            info = this._appContext.getApplicationInfo();
        } catch (UnsupportedEncodingException e) {
        }
        if (info.name != null) {
            bArr = info.name.getBytes("UTF-8");
        } else {
            if (info.packageName != null) {
                String[] parts = info.packageName.split(".");
                if (parts.length > 1) {
                    bArr = parts[parts.length - 1].getBytes("UTF-8");
                }
            }
            bArr = new byte[0];
        }
        return bArr;
    }

    public byte[] packageName() {
        try {
            ApplicationInfo info = this._appContext.getApplicationInfo();
            if (info.packageName != null) {
                return info.packageName.getBytes("UTF-8");
            }
        } catch (UnsupportedEncodingException e) {
        }
        return new byte[0];
    }

    public int seconds() {
        return Calendar.getInstance().get(13);
    }

    public int milliseconds() {
        return Calendar.getInstance().get(14);
    }

    public String defaultApn() {
        return null;
    }
}
