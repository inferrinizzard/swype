package com.google.android.gms.ads.internal.util.client;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.google.android.gms.ads.doubleclick.PublisherInterstitialAd;
import com.google.android.gms.ads.internal.client.AdSizeParcel;
import com.google.android.gms.ads.search.SearchAdView;
import com.google.android.gms.common.util.zzs;
import com.google.android.gms.internal.zzin;
import com.google.api.client.http.ExponentialBackOffPolicy;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.StringTokenizer;

@zzin
/* loaded from: classes.dex */
public class zza {
    public static final Handler zzcnb = new Handler(Looper.getMainLooper());
    private static final String zzcnc = AdView.class.getName();
    private static final String zzcnd = InterstitialAd.class.getName();
    private static final String zzcne = PublisherAdView.class.getName();
    private static final String zzcnf = PublisherInterstitialAd.class.getName();
    private static final String zzcng = SearchAdView.class.getName();
    private static final String zzcnh = AdLoader.class.getName();

    /* renamed from: com.google.android.gms.ads.internal.util.client.zza$zza, reason: collision with other inner class name */
    /* loaded from: classes.dex */
    public interface InterfaceC0039zza {
        void zzcr(String str);
    }

    private void zza(ViewGroup viewGroup, AdSizeParcel adSizeParcel, String str, int i, int i2) {
        if (viewGroup.getChildCount() != 0) {
            return;
        }
        Context context = viewGroup.getContext();
        TextView textView = new TextView(context);
        textView.setGravity(17);
        textView.setText(str);
        textView.setTextColor(i);
        textView.setBackgroundColor(i2);
        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setBackgroundColor(i);
        int zza = zza(context, 3);
        frameLayout.addView(textView, new FrameLayout.LayoutParams(adSizeParcel.widthPixels - zza, adSizeParcel.heightPixels - zza, 17));
        viewGroup.addView(frameLayout, adSizeParcel.widthPixels, adSizeParcel.heightPixels);
    }

    public int zza(Context context, int i) {
        return zza(context.getResources().getDisplayMetrics(), i);
    }

    public int zza(DisplayMetrics displayMetrics, int i) {
        return (int) TypedValue.applyDimension(1, i, displayMetrics);
    }

    public void zza(Context context, String str, String str2, Bundle bundle, boolean z) {
        zza(context, str, str2, bundle, z, new InterfaceC0039zza() { // from class: com.google.android.gms.ads.internal.util.client.zza.1
            /* JADX WARN: Type inference failed for: r0v0, types: [com.google.android.gms.ads.internal.util.client.zza$1$1] */
            @Override // com.google.android.gms.ads.internal.util.client.zza.InterfaceC0039zza
            public final void zzcr(final String str3) {
                new Thread() { // from class: com.google.android.gms.ads.internal.util.client.zza.1.1
                    @Override // java.lang.Thread, java.lang.Runnable
                    public final void run() {
                        new zzc().zzcr(str3);
                    }
                }.start();
            }
        });
    }

    public void zza(Context context, String str, String str2, Bundle bundle, boolean z, InterfaceC0039zza interfaceC0039zza) {
        if (z) {
            Context applicationContext = context.getApplicationContext();
            if (applicationContext == null) {
                applicationContext = context;
            }
            bundle.putString("os", Build.VERSION.RELEASE);
            bundle.putString("api", String.valueOf(Build.VERSION.SDK_INT));
            bundle.putString("appid", applicationContext.getPackageName());
            if (str == null) {
                str = new StringBuilder(23).append(com.google.android.gms.common.zzc.zzang().zzbn(context)).append(".9452000").toString();
            }
            bundle.putString("js", str);
        }
        Uri.Builder appendQueryParameter = new Uri.Builder().scheme("https").path("//pagead2.googlesyndication.com/pagead/gen_204").appendQueryParameter("id", str2);
        for (String str3 : bundle.keySet()) {
            appendQueryParameter.appendQueryParameter(str3, bundle.getString(str3));
        }
        interfaceC0039zza.zzcr(appendQueryParameter.toString());
    }

    public void zza(ViewGroup viewGroup, AdSizeParcel adSizeParcel, String str) {
        zza(viewGroup, adSizeParcel, str, -16777216, -1);
    }

    public void zza(ViewGroup viewGroup, AdSizeParcel adSizeParcel, String str, String str2) {
        zzb.zzcx(str2);
        zza(viewGroup, adSizeParcel, str, -65536, -16777216);
    }

    public void zza(boolean z, HttpURLConnection httpURLConnection, String str) {
        httpURLConnection.setConnectTimeout(ExponentialBackOffPolicy.DEFAULT_MAX_INTERVAL_MILLIS);
        httpURLConnection.setInstanceFollowRedirects(z);
        httpURLConnection.setReadTimeout(ExponentialBackOffPolicy.DEFAULT_MAX_INTERVAL_MILLIS);
        if (str != null) {
            httpURLConnection.setRequestProperty("User-Agent", str);
        }
        httpURLConnection.setUseCaches(false);
    }

    public String zzaq(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        String string = contentResolver == null ? null : Settings.Secure.getString(contentResolver, "android_id");
        if (string == null || zztw()) {
            string = "emulator";
        }
        return zzcu(string);
    }

    public boolean zzar(Context context) {
        return com.google.android.gms.common.zzc.zzang().isGooglePlayServicesAvailable(context) == 0;
    }

    public boolean zzas(Context context) {
        if (context.getResources().getConfiguration().orientation != 2) {
            return false;
        }
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return ((int) (((float) displayMetrics.heightPixels) / displayMetrics.density)) < 600;
    }

    public int zzau(Context context) {
        int identifier = context.getResources().getIdentifier("navigation_bar_width", "dimen", "android");
        if (identifier > 0) {
            return context.getResources().getDimensionPixelSize(identifier);
        }
        return 0;
    }

    public int zzb(Context context, int i) {
        Display defaultDisplay = ((WindowManager) context.getSystemService("window")).getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        defaultDisplay.getMetrics(displayMetrics);
        return zzb(displayMetrics, i);
    }

    public int zzb(DisplayMetrics displayMetrics, int i) {
        return Math.round(i / displayMetrics.density);
    }

    public String zzcu(String str) {
        for (int i = 0; i < 2; i++) {
            try {
                MessageDigest messageDigest = MessageDigest.getInstance("MD5");
                messageDigest.update(str.getBytes());
                return String.format(Locale.US, "%032X", new BigInteger(1, messageDigest.digest()));
            } catch (NoSuchAlgorithmException e) {
            }
        }
        return null;
    }

    public boolean zztw() {
        return Build.DEVICE.startsWith("generic");
    }

    public boolean zztx() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    public boolean zzat(Context context) {
        int intValue;
        int intValue2;
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        Display defaultDisplay = ((WindowManager) context.getSystemService("window")).getDefaultDisplay();
        if (zzs.zzhb(17)) {
            defaultDisplay.getRealMetrics(displayMetrics);
            intValue = displayMetrics.heightPixels;
            intValue2 = displayMetrics.widthPixels;
        } else {
            try {
                intValue = ((Integer) Display.class.getMethod("getRawHeight", new Class[0]).invoke(defaultDisplay, new Object[0])).intValue();
                intValue2 = ((Integer) Display.class.getMethod("getRawWidth", new Class[0]).invoke(defaultDisplay, new Object[0])).intValue();
            } catch (Exception e) {
                return false;
            }
        }
        defaultDisplay.getMetrics(displayMetrics);
        return displayMetrics.heightPixels == intValue && displayMetrics.widthPixels == intValue2;
    }

    public String zza(StackTraceElement[] stackTraceElementArr, String str) {
        String str2;
        for (int i = 0; i + 1 < stackTraceElementArr.length; i++) {
            StackTraceElement stackTraceElement = stackTraceElementArr[i];
            String className = stackTraceElement.getClassName();
            if ("loadAd".equalsIgnoreCase(stackTraceElement.getMethodName()) && (zzcnc.equalsIgnoreCase(className) || zzcnd.equalsIgnoreCase(className) || zzcne.equalsIgnoreCase(className) || zzcnf.equalsIgnoreCase(className) || zzcng.equalsIgnoreCase(className) || zzcnh.equalsIgnoreCase(className))) {
                str2 = stackTraceElementArr[i + 1].getClassName();
                break;
            }
        }
        str2 = null;
        if (str != null) {
            StringTokenizer stringTokenizer = new StringTokenizer(str, ".");
            StringBuilder sb = new StringBuilder();
            int i2 = 2;
            if (stringTokenizer.hasMoreElements()) {
                sb.append(stringTokenizer.nextToken());
                while (true) {
                    int i3 = i2 - 1;
                    if (i2 <= 0 || !stringTokenizer.hasMoreElements()) {
                        break;
                    }
                    sb.append(".").append(stringTokenizer.nextToken());
                    i2 = i3;
                }
                str = sb.toString();
            }
            if (str2 != null && !str2.contains(str)) {
                return str2;
            }
        }
        return null;
    }
}
