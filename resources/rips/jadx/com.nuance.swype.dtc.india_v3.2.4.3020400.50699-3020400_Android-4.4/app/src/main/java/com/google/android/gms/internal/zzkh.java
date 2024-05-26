package com.google.android.gms.internal;

import android.R;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.support.customtabs.CustomTabsSession;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.PopupWindow;
import com.google.android.gms.ads.AdActivity;
import com.google.android.gms.ads.internal.ClientApi;
import com.google.android.gms.ads.internal.client.AdSizeParcel;
import com.google.android.gms.ads.internal.util.client.VersionInfoParcel;
import com.google.android.gms.ads.internal.util.client.zza;
import com.google.android.gms.internal.zzdq;
import com.google.api.client.http.ExponentialBackOffPolicy;
import com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.xmlResults.XMLResultsHandler;
import com.nuance.swype.input.hardkey.HardKeyboardManager;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@zzin
/* loaded from: classes.dex */
public final class zzkh {
    public static final Handler zzclc = new zzke(Looper.getMainLooper());
    private String zzbjf;
    private zzfs zzcea;
    private final Object zzail = new Object();
    private boolean zzcld = true;
    public boolean zzcle = false;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public final class zza extends BroadcastReceiver {
        private zza() {
        }

        public /* synthetic */ zza(zzkh zzkhVar, byte b) {
            this();
        }

        @Override // android.content.BroadcastReceiver
        public final void onReceive(Context context, Intent intent) {
            if ("android.intent.action.USER_PRESENT".equals(intent.getAction())) {
                zzkh.this.zzcld = true;
            } else if ("android.intent.action.SCREEN_OFF".equals(intent.getAction())) {
                zzkh.this.zzcld = false;
            }
        }
    }

    public static void runOnUiThread(Runnable runnable) {
        if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
            runnable.run();
        } else {
            zzclc.post(runnable);
        }
    }

    public static DisplayMetrics zza(WindowManager windowManager) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics;
    }

    public static String zza(zzlh zzlhVar, String str) {
        return zza(zzlhVar.getContext(), zzlhVar.zzul(), str);
    }

    public static String zza(InputStreamReader inputStreamReader) throws IOException {
        StringBuilder sb = new StringBuilder(8192);
        char[] cArr = new char[HardKeyboardManager.META_SELECTING];
        while (true) {
            int read = inputStreamReader.read(cArr);
            if (read == -1) {
                return sb.toString();
            }
            sb.append(cArr, 0, read);
        }
    }

    private JSONArray zza(Collection<?> collection) throws JSONException {
        JSONArray jSONArray = new JSONArray();
        Iterator<?> it = collection.iterator();
        while (it.hasNext()) {
            zza(jSONArray, it.next());
        }
        return jSONArray;
    }

    public static void zza(Activity activity, ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener) {
        Window window = activity.getWindow();
        if (window == null || window.getDecorView() == null || window.getDecorView().getViewTreeObserver() == null) {
            return;
        }
        window.getDecorView().getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);
    }

    public static void zza(Activity activity, ViewTreeObserver.OnScrollChangedListener onScrollChangedListener) {
        Window window = activity.getWindow();
        if (window == null || window.getDecorView() == null || window.getDecorView().getViewTreeObserver() == null) {
            return;
        }
        window.getDecorView().getViewTreeObserver().addOnScrollChangedListener(onScrollChangedListener);
    }

    public static void zza(Context context, String str, List<String> list) {
        Iterator<String> it = list.iterator();
        while (it.hasNext()) {
            new zzkq(context, str, it.next()).zzpy();
        }
    }

    public static void zza(List<String> list, String str) {
        Iterator<String> it = list.iterator();
        while (it.hasNext()) {
            new zzkq(it.next(), str).zzpy();
        }
    }

    private void zza(JSONObject jSONObject, String str, Object obj) throws JSONException {
        if (obj instanceof Bundle) {
            jSONObject.put(str, zzh((Bundle) obj));
            return;
        }
        if (obj instanceof Map) {
            jSONObject.put(str, zzam((Map<String, ?>) obj));
            return;
        }
        if (obj instanceof Collection) {
            if (str == null) {
                str = "null";
            }
            jSONObject.put(str, zza((Collection<?>) obj));
        } else if (obj instanceof Object[]) {
            jSONObject.put(str, zza(Arrays.asList((Object[]) obj)));
        } else {
            jSONObject.put(str, obj);
        }
    }

    public static boolean zza(PackageManager packageManager, String str, String str2) {
        return packageManager.checkPermission(str2, str) == 0;
    }

    public static boolean zza(View view, Context context) {
        KeyguardManager keyguardManager = null;
        Context applicationContext = context.getApplicationContext();
        PowerManager powerManager = applicationContext != null ? (PowerManager) applicationContext.getSystemService("power") : null;
        Object systemService = context.getSystemService("keyguard");
        if (systemService != null && (systemService instanceof KeyguardManager)) {
            keyguardManager = (KeyguardManager) systemService;
        }
        return zza(view, powerManager, keyguardManager);
    }

    public static boolean zza(ClassLoader classLoader, Class<?> cls, String str) {
        try {
            return cls.isAssignableFrom(Class.forName(str, false, classLoader));
        } catch (Throwable th) {
            return false;
        }
    }

    public static PopupWindow zza$490f73c3(View view, int i, int i2) {
        return new PopupWindow(view, i, i2, false);
    }

    public static boolean zzac(Context context) {
        boolean z;
        Intent intent = new Intent();
        intent.setClassName(context, AdActivity.CLASS_NAME);
        ResolveInfo resolveActivity = context.getPackageManager().resolveActivity(intent, 65536);
        if (resolveActivity == null || resolveActivity.activityInfo == null) {
            zzkd.zzcx("Could not find com.google.android.gms.ads.AdActivity, please make sure it is declared in AndroidManifest.xml.");
            return false;
        }
        if ((resolveActivity.activityInfo.configChanges & 16) == 0) {
            zzkd.zzcx(String.format("com.google.android.gms.ads.AdActivity requires the android:configChanges value to contain \"%s\".", "keyboard"));
            z = false;
        } else {
            z = true;
        }
        if ((resolveActivity.activityInfo.configChanges & 32) == 0) {
            zzkd.zzcx(String.format("com.google.android.gms.ads.AdActivity requires the android:configChanges value to contain \"%s\".", "keyboardHidden"));
            z = false;
        }
        if ((resolveActivity.activityInfo.configChanges & 128) == 0) {
            zzkd.zzcx(String.format("com.google.android.gms.ads.AdActivity requires the android:configChanges value to contain \"%s\".", "orientation"));
            z = false;
        }
        if ((resolveActivity.activityInfo.configChanges & 256) == 0) {
            zzkd.zzcx(String.format("com.google.android.gms.ads.AdActivity requires the android:configChanges value to contain \"%s\".", "screenLayout"));
            z = false;
        }
        if ((resolveActivity.activityInfo.configChanges & 512) == 0) {
            zzkd.zzcx(String.format("com.google.android.gms.ads.AdActivity requires the android:configChanges value to contain \"%s\".", "uiMode"));
            z = false;
        }
        if ((resolveActivity.activityInfo.configChanges & 1024) == 0) {
            zzkd.zzcx(String.format("com.google.android.gms.ads.AdActivity requires the android:configChanges value to contain \"%s\".", "screenSize"));
            z = false;
        }
        if ((resolveActivity.activityInfo.configChanges & HardKeyboardManager.META_SELECTING) != 0) {
            return z;
        }
        zzkd.zzcx(String.format("com.google.android.gms.ads.AdActivity requires the android:configChanges value to contain \"%s\".", "smallestScreenSize"));
        return false;
    }

    protected static String zzae(Context context) {
        return new WebView(context).getSettings().getUserAgentString();
    }

    public static AlertDialog.Builder zzaf(Context context) {
        return new AlertDialog.Builder(context);
    }

    public static zzcu zzag(Context context) {
        return new zzcu(context);
    }

    private static String zzah(Context context) {
        ActivityManager activityManager;
        ActivityManager.RunningTaskInfo runningTaskInfo;
        try {
            activityManager = (ActivityManager) context.getSystemService("activity");
        } catch (Exception e) {
        }
        if (activityManager == null) {
            return null;
        }
        List<ActivityManager.RunningTaskInfo> runningTasks = activityManager.getRunningTasks(1);
        if (runningTasks != null && !runningTasks.isEmpty() && (runningTaskInfo = runningTasks.get(0)) != null && runningTaskInfo.topActivity != null) {
            return runningTaskInfo.topActivity.getClassName();
        }
        return null;
    }

    public static AudioManager zzak(Context context) {
        return (AudioManager) context.getSystemService("audio");
    }

    public static int zzam(Context context) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        if (applicationInfo == null) {
            return 0;
        }
        return applicationInfo.targetSdkVersion;
    }

    public static boolean zzan(Context context) {
        try {
            context.getClassLoader().loadClass(ClientApi.class.getName());
            return false;
        } catch (ClassNotFoundException e) {
            return true;
        }
    }

    public static String zzb(String str, Map<String, String> map) {
        for (String str2 : map.keySet()) {
            str = str.replaceAll(String.format("(?<!@)((?:@@)*)@%s(?<!@)((?:@@)*)@", str2), String.format("$1%s$2", Uri.encode(map.get(str2))));
        }
        return str.replaceAll(String.format("(?<!@)((?:@@)*)@%s(?<!@)((?:@@)*)@", "[^@]+"), String.format("$1%s$2", "")).replaceAll("@@", "@");
    }

    public static void zzb(Activity activity, ViewTreeObserver.OnScrollChangedListener onScrollChangedListener) {
        Window window = activity.getWindow();
        if (window == null || window.getDecorView() == null || window.getDecorView().getViewTreeObserver() == null) {
            return;
        }
        window.getDecorView().getViewTreeObserver().removeOnScrollChangedListener(onScrollChangedListener);
    }

    public static void zzb(Context context, Intent intent) {
        try {
            context.startActivity(intent);
        } catch (Throwable th) {
            intent.addFlags(268435456);
            context.startActivity(intent);
        }
    }

    public static void zzb(Runnable runnable) {
        if (Looper.getMainLooper().getThread() != Thread.currentThread()) {
            runnable.run();
        } else {
            zzkg.zza(runnable);
        }
    }

    public static void zzc(Context context, String str, String str2) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(str2);
        zza(context, str, arrayList);
    }

    public static String zzco(String str) {
        return Uri.parse(str).buildUpon().query(null).build().toString();
    }

    public static int zzcp(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            String valueOf = String.valueOf(e);
            zzkd.zzcx(new StringBuilder(String.valueOf(valueOf).length() + 22).append("Could not parse value:").append(valueOf).toString());
            return 0;
        }
    }

    public static boolean zzcq(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        return str.matches("([^\\s]+(\\.(?i)(jpg|png|gif|bmp|webp))$)");
    }

    public static float zzey() {
        com.google.android.gms.ads.internal.zzo zzex = com.google.android.gms.ads.internal.zzu.zzgg().zzex();
        if (zzex == null || !zzex.zzez()) {
            return 1.0f;
        }
        return zzex.zzey();
    }

    public static Map<String, String> zzf(Uri uri) {
        if (uri == null) {
            return null;
        }
        HashMap hashMap = new HashMap();
        for (String str : com.google.android.gms.ads.internal.zzu.zzfs().zzg(uri)) {
            hashMap.put(str, uri.getQueryParameter(str));
        }
        return hashMap;
    }

    public static boolean zzfa() {
        com.google.android.gms.ads.internal.zzo zzex = com.google.android.gms.ads.internal.zzu.zzgg().zzex();
        if (zzex != null) {
            return zzex.zzfa();
        }
        return false;
    }

    private JSONObject zzh(Bundle bundle) throws JSONException {
        JSONObject jSONObject = new JSONObject();
        for (String str : bundle.keySet()) {
            zza(jSONObject, str, bundle.get(str));
        }
        return jSONObject;
    }

    public static int[] zzh(Activity activity) {
        View findViewById;
        Window window = activity.getWindow();
        return (window == null || (findViewById = window.findViewById(R.id.content)) == null) ? zzth() : new int[]{findViewById.getWidth(), findViewById.getHeight()};
    }

    public static int[] zzi(Activity activity) {
        int[] zzh = zzh(activity);
        return new int[]{com.google.android.gms.ads.internal.client.zzm.zziw().zzb(activity, zzh[0]), com.google.android.gms.ads.internal.client.zzm.zziw().zzb(activity, zzh[1])};
    }

    public static Bitmap zzk(View view) {
        view.setDrawingCacheEnabled(true);
        Bitmap createBitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        return createBitmap;
    }

    private static Bitmap zzl(View view) {
        Bitmap bitmap;
        try {
            int width = view.getWidth();
            int height = view.getHeight();
            if (width == 0 || height == 0) {
                zzkd.zzcx("Width or height of view is zero");
                bitmap = null;
            } else {
                bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.RGB_565);
                Canvas canvas = new Canvas(bitmap);
                view.layout(0, 0, width, height);
                view.draw(canvas);
            }
            return bitmap;
        } catch (RuntimeException e) {
            zzkd.zzb("Fail to capture the webview", e);
            return null;
        }
    }

    private static Bitmap zzm(View view) {
        Bitmap bitmap;
        RuntimeException e;
        try {
            boolean isDrawingCacheEnabled = view.isDrawingCacheEnabled();
            view.setDrawingCacheEnabled(true);
            Bitmap drawingCache = view.getDrawingCache();
            bitmap = drawingCache != null ? Bitmap.createBitmap(drawingCache) : null;
            try {
                view.setDrawingCacheEnabled(isDrawingCacheEnabled);
            } catch (RuntimeException e2) {
                e = e2;
                zzkd.zzb("Fail to capture the web view", e);
                return bitmap;
            }
        } catch (RuntimeException e3) {
            bitmap = null;
            e = e3;
        }
        return bitmap;
    }

    public static int zzn(View view) {
        if (view == null) {
            return -1;
        }
        ViewParent parent = view.getParent();
        while (parent != null && !(parent instanceof AdapterView)) {
            parent = parent.getParent();
        }
        if (parent == null) {
            return -1;
        }
        return ((AdapterView) parent).getPositionForView(view);
    }

    private static String zztd() {
        StringBuffer stringBuffer = new StringBuffer(256);
        stringBuffer.append("Mozilla/5.0 (Linux; U; Android");
        if (Build.VERSION.RELEASE != null) {
            stringBuffer.append(XMLResultsHandler.SEP_SPACE).append(Build.VERSION.RELEASE);
        }
        stringBuffer.append("; ").append(Locale.getDefault());
        if (Build.DEVICE != null) {
            stringBuffer.append("; ").append(Build.DEVICE);
            if (Build.DISPLAY != null) {
                stringBuffer.append(" Build/").append(Build.DISPLAY);
            }
        }
        stringBuffer.append(") AppleWebKit/533 Version/4.0 Safari/533");
        return stringBuffer.toString();
    }

    public static String zzte() {
        return UUID.randomUUID().toString();
    }

    public static String zztf() {
        UUID randomUUID = UUID.randomUUID();
        byte[] byteArray = BigInteger.valueOf(randomUUID.getLeastSignificantBits()).toByteArray();
        byte[] byteArray2 = BigInteger.valueOf(randomUUID.getMostSignificantBits()).toByteArray();
        String bigInteger = new BigInteger(1, byteArray).toString();
        for (int i = 0; i < 2; i++) {
            try {
                MessageDigest messageDigest = MessageDigest.getInstance("MD5");
                messageDigest.update(byteArray);
                messageDigest.update(byteArray2);
                byte[] bArr = new byte[8];
                System.arraycopy(messageDigest.digest(), 0, bArr, 0, 8);
                bigInteger = new BigInteger(1, bArr).toString();
            } catch (NoSuchAlgorithmException e) {
            }
        }
        return bigInteger;
    }

    public static String zztg() {
        String str = Build.MANUFACTURER;
        String str2 = Build.MODEL;
        return str2.startsWith(str) ? str2 : new StringBuilder(String.valueOf(str).length() + 1 + String.valueOf(str2).length()).append(str).append(XMLResultsHandler.SEP_SPACE).append(str2).toString();
    }

    private static int[] zzth() {
        return new int[]{0, 0};
    }

    public static Bundle zzti() {
        Bundle bundle = new Bundle();
        try {
            Debug.MemoryInfo memoryInfo = new Debug.MemoryInfo();
            Debug.getMemoryInfo(memoryInfo);
            bundle.putParcelable("debug_memory_info", memoryInfo);
            Runtime runtime = Runtime.getRuntime();
            bundle.putLong("runtime_free_memory", runtime.freeMemory());
            bundle.putLong("runtime_max_memory", runtime.maxMemory());
            bundle.putLong("runtime_total_memory", runtime.totalMemory());
        } catch (Exception e) {
            zzkd.zzd("Unable to gather memory stats", e);
        }
        return bundle;
    }

    public final void zza(final Context context, final String str, String str2, Bundle bundle, boolean z) {
        if (z) {
            com.google.android.gms.ads.internal.zzu.zzfq();
            bundle.putString("device", zztg());
            bundle.putString("eids", TextUtils.join(",", zzdc.zzjx()));
        }
        com.google.android.gms.ads.internal.client.zzm.zziw().zza(context, str, str2, bundle, z, new zza.InterfaceC0039zza() { // from class: com.google.android.gms.internal.zzkh.3
            @Override // com.google.android.gms.ads.internal.util.client.zza.InterfaceC0039zza
            public final void zzcr(String str3) {
                com.google.android.gms.ads.internal.zzu.zzfq();
                zzkh.zzc(context, str, str3);
            }
        });
    }

    public final JSONObject zzam(Map<String, ?> map) throws JSONException {
        try {
            JSONObject jSONObject = new JSONObject();
            for (String str : map.keySet()) {
                zza(jSONObject, str, map.get(str));
            }
            return jSONObject;
        } catch (ClassCastException e) {
            String valueOf = String.valueOf(e.getMessage());
            throw new JSONException(valueOf.length() != 0 ? "Could not convert map to JSON: ".concat(valueOf) : new String("Could not convert map to JSON: "));
        }
    }

    public final String zzg(final Context context, String str) {
        String str2;
        synchronized (this.zzail) {
            if (this.zzbjf != null) {
                str2 = this.zzbjf;
            } else {
                try {
                    this.zzbjf = com.google.android.gms.ads.internal.zzu.zzfs().getDefaultUserAgent(context);
                } catch (Exception e) {
                }
                if (TextUtils.isEmpty(this.zzbjf)) {
                    if (com.google.android.gms.ads.internal.client.zzm.zziw().zztx()) {
                        try {
                            this.zzbjf = zzae(context);
                        } catch (Exception e2) {
                            this.zzbjf = zztd();
                        }
                    } else {
                        this.zzbjf = null;
                        zzclc.post(new Runnable() { // from class: com.google.android.gms.internal.zzkh.2
                            @Override // java.lang.Runnable
                            public final void run() {
                                synchronized (zzkh.this.zzail) {
                                    zzkh.this.zzbjf = zzkh.zzae(context);
                                    zzkh.this.zzail.notifyAll();
                                }
                            }
                        });
                        while (this.zzbjf == null) {
                            try {
                                this.zzail.wait();
                            } catch (InterruptedException e3) {
                                this.zzbjf = zztd();
                                String valueOf = String.valueOf(this.zzbjf);
                                zzkd.zzcx(valueOf.length() != 0 ? "Interrupted, use default user agent: ".concat(valueOf) : new String("Interrupted, use default user agent: "));
                            }
                        }
                    }
                }
                String valueOf2 = String.valueOf(this.zzbjf);
                this.zzbjf = new StringBuilder(String.valueOf(valueOf2).length() + 11 + String.valueOf(str).length()).append(valueOf2).append(" (Mobile; ").append(str).append(")").toString();
                str2 = this.zzbjf;
            }
        }
        return str2;
    }

    public final void zza$59850860(Context context, String str, HttpURLConnection httpURLConnection) {
        httpURLConnection.setConnectTimeout(ExponentialBackOffPolicy.DEFAULT_MAX_INTERVAL_MILLIS);
        httpURLConnection.setInstanceFollowRedirects(false);
        httpURLConnection.setReadTimeout(ExponentialBackOffPolicy.DEFAULT_MAX_INTERVAL_MILLIS);
        httpURLConnection.setRequestProperty("User-Agent", zzg(context, str));
        httpURLConnection.setUseCaches(false);
    }

    public final void zza(final Context context, final List<String> list) {
        if ((context instanceof Activity) && !TextUtils.isEmpty(zzaqa.zzex((Activity) context))) {
            if (list == null) {
                zzkd.v("Cannot ping urls: empty list.");
            } else {
                if (!zzdq.zzo(context)) {
                    zzkd.v("Cannot ping url because custom tabs is not supported");
                    return;
                }
                final zzdq zzdqVar = new zzdq();
                zzdqVar.zzber = new zzdq.zza() { // from class: com.google.android.gms.internal.zzkh.1
                    @Override // com.google.android.gms.internal.zzdq.zza
                    public final void zzko() {
                    }

                    @Override // com.google.android.gms.internal.zzdq.zza
                    public final void zzkn() {
                        CustomTabsSession zzkl;
                        for (String str : list) {
                            String valueOf = String.valueOf(str);
                            zzkd.zzcw(valueOf.length() != 0 ? "Pinging url: ".concat(valueOf) : new String("Pinging url: "));
                            Uri parse = Uri.parse(str);
                            zzdq zzdqVar2 = zzdqVar;
                            if (zzdqVar2.zzbep != null && (zzkl = zzdqVar2.zzkl()) != null) {
                                zzkl.mayLaunchUrl$31eb0de9(parse);
                            }
                        }
                        zzdqVar.zzd((Activity) context);
                    }
                };
                zzdqVar.zze((Activity) context);
            }
        }
    }

    private void zza(JSONArray jSONArray, Object obj) throws JSONException {
        if (obj instanceof Bundle) {
            jSONArray.put(zzh((Bundle) obj));
            return;
        }
        if (obj instanceof Map) {
            jSONArray.put(zzam((Map<String, ?>) obj));
            return;
        }
        if (obj instanceof Collection) {
            jSONArray.put(zza((Collection<?>) obj));
            return;
        }
        if (!(obj instanceof Object[])) {
            jSONArray.put(obj);
            return;
        }
        JSONArray jSONArray2 = new JSONArray();
        for (Object obj2 : (Object[]) obj) {
            zza(jSONArray2, obj2);
        }
        jSONArray.put(jSONArray2);
    }

    public static int[] zzk(Activity activity) {
        View findViewById;
        Window window = activity.getWindow();
        int[] zzth = (window == null || (findViewById = window.findViewById(R.id.content)) == null) ? zzth() : new int[]{findViewById.getTop(), findViewById.getBottom()};
        return new int[]{com.google.android.gms.ads.internal.client.zzm.zziw().zzb(activity, zzth[0]), com.google.android.gms.ads.internal.client.zzm.zziw().zzb(activity, zzth[1])};
    }

    public static String zza(Context context, View view, AdSizeParcel adSizeParcel) {
        if (!((Boolean) com.google.android.gms.ads.internal.zzu.zzfz().zzd(zzdc.zzazx)).booleanValue()) {
            return null;
        }
        try {
            JSONObject jSONObject = new JSONObject();
            JSONObject jSONObject2 = new JSONObject();
            jSONObject2.put("width", adSizeParcel.width);
            jSONObject2.put("height", adSizeParcel.height);
            jSONObject.put("size", jSONObject2);
            jSONObject.put("activity", zzah(context));
            if (!adSizeParcel.zzaus) {
                JSONArray jSONArray = new JSONArray();
                while (view != null) {
                    Object parent = view.getParent();
                    if (parent != null) {
                        int indexOfChild = parent instanceof ViewGroup ? ((ViewGroup) parent).indexOfChild(view) : -1;
                        JSONObject jSONObject3 = new JSONObject();
                        jSONObject3.put("type", parent.getClass().getName());
                        jSONObject3.put("index_of_child", indexOfChild);
                        jSONArray.put(jSONObject3);
                    }
                    view = (parent == null || !(parent instanceof View)) ? null : (View) parent;
                }
                if (jSONArray.length() > 0) {
                    jSONObject.put("parents", jSONArray);
                }
            }
            return jSONObject.toString();
        } catch (JSONException e) {
            zzkd.zzd("Fail to get view hierarchy json", e);
            return null;
        }
    }

    private static String zza(Context context, zzas zzasVar, String str) {
        boolean z = false;
        if (zzasVar == null) {
            return str;
        }
        try {
            Uri parse = Uri.parse(str);
            if (zzasVar.zzc(parse)) {
                String[] strArr = zzas.zzafy;
                int length = strArr.length;
                int i = 0;
                while (true) {
                    if (i >= length) {
                        break;
                    }
                    if (parse.getPath().endsWith(strArr[i])) {
                        z = true;
                        break;
                    }
                    i++;
                }
            }
            if (z) {
                parse = zzasVar.zzb(parse, context);
            }
            return parse.toString();
        } catch (Exception e) {
            return str;
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:18:0x003d, code lost:            if (r0.importance != 100) goto L27;     */
    /* JADX WARN: Code restructure failed: missing block: B:20:0x0043, code lost:            if (r1.inKeyguardRestrictedInputMode() != false) goto L27;     */
    /* JADX WARN: Code restructure failed: missing block: B:21:0x0045, code lost:            r0 = (android.os.PowerManager) r6.getSystemService("power");     */
    /* JADX WARN: Code restructure failed: missing block: B:22:0x004e, code lost:            if (r0 != null) goto L25;     */
    /* JADX WARN: Code restructure failed: missing block: B:23:0x0050, code lost:            r0 = false;     */
    /* JADX WARN: Code restructure failed: missing block: B:24:0x0051, code lost:            if (r0 == false) goto L27;     */
    /* JADX WARN: Code restructure failed: missing block: B:26:?, code lost:            return true;     */
    /* JADX WARN: Code restructure failed: missing block: B:27:0x0055, code lost:            r0 = r0.isScreenOn();     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static boolean zzai(android.content.Context r6) {
        /*
            r2 = 0
            java.lang.String r0 = "activity"
            java.lang.Object r0 = r6.getSystemService(r0)     // Catch: java.lang.Throwable -> L5c
            android.app.ActivityManager r0 = (android.app.ActivityManager) r0     // Catch: java.lang.Throwable -> L5c
            java.lang.String r1 = "keyguard"
            java.lang.Object r1 = r6.getSystemService(r1)     // Catch: java.lang.Throwable -> L5c
            android.app.KeyguardManager r1 = (android.app.KeyguardManager) r1     // Catch: java.lang.Throwable -> L5c
            if (r0 == 0) goto L17
            if (r1 != 0) goto L19
        L17:
            r0 = r2
        L18:
            return r0
        L19:
            java.util.List r0 = r0.getRunningAppProcesses()     // Catch: java.lang.Throwable -> L5c
            if (r0 != 0) goto L21
            r0 = r2
            goto L18
        L21:
            java.util.Iterator r3 = r0.iterator()     // Catch: java.lang.Throwable -> L5c
        L25:
            boolean r0 = r3.hasNext()     // Catch: java.lang.Throwable -> L5c
            if (r0 == 0) goto L5a
            java.lang.Object r0 = r3.next()     // Catch: java.lang.Throwable -> L5c
            android.app.ActivityManager$RunningAppProcessInfo r0 = (android.app.ActivityManager.RunningAppProcessInfo) r0     // Catch: java.lang.Throwable -> L5c
            int r4 = android.os.Process.myPid()     // Catch: java.lang.Throwable -> L5c
            int r5 = r0.pid     // Catch: java.lang.Throwable -> L5c
            if (r4 != r5) goto L25
            int r0 = r0.importance     // Catch: java.lang.Throwable -> L5c
            r3 = 100
            if (r0 != r3) goto L5a
            boolean r0 = r1.inKeyguardRestrictedInputMode()     // Catch: java.lang.Throwable -> L5c
            if (r0 != 0) goto L5a
            java.lang.String r0 = "power"
            java.lang.Object r0 = r6.getSystemService(r0)     // Catch: java.lang.Throwable -> L5c
            android.os.PowerManager r0 = (android.os.PowerManager) r0     // Catch: java.lang.Throwable -> L5c
            if (r0 != 0) goto L55
            r0 = r2
        L51:
            if (r0 == 0) goto L5a
            r0 = 1
            goto L18
        L55:
            boolean r0 = r0.isScreenOn()     // Catch: java.lang.Throwable -> L5c
            goto L51
        L5a:
            r0 = r2
            goto L18
        L5c:
            r0 = move-exception
            r0 = r2
            goto L18
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.zzkh.zzai(android.content.Context):boolean");
    }

    public final void zzb(Context context, String str, String str2, Bundle bundle, boolean z) {
        if (((Boolean) com.google.android.gms.ads.internal.zzu.zzfz().zzd(zzdc.zzbau)).booleanValue()) {
            zza(context, str, str2, bundle, z);
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:14:0x002c, code lost:            r0 = null;     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static android.graphics.Bitmap zzaj(android.content.Context r3) {
        /*
            r1 = 0
            boolean r0 = r3 instanceof android.app.Activity
            if (r0 != 0) goto L6
        L5:
            return r1
        L6:
            com.google.android.gms.internal.zzcy<java.lang.Boolean> r0 = com.google.android.gms.internal.zzdc.zzbcb     // Catch: java.lang.RuntimeException -> L3d
            com.google.android.gms.internal.zzdb r2 = com.google.android.gms.ads.internal.zzu.zzfz()     // Catch: java.lang.RuntimeException -> L3d
            java.lang.Object r0 = r2.zzd(r0)     // Catch: java.lang.RuntimeException -> L3d
            java.lang.Boolean r0 = (java.lang.Boolean) r0     // Catch: java.lang.RuntimeException -> L3d
            boolean r0 = r0.booleanValue()     // Catch: java.lang.RuntimeException -> L3d
            if (r0 == 0) goto L2e
            android.app.Activity r3 = (android.app.Activity) r3     // Catch: java.lang.RuntimeException -> L3d
            android.view.Window r0 = r3.getWindow()     // Catch: java.lang.RuntimeException -> L3d
            if (r0 == 0) goto L44
            android.view.View r0 = r0.getDecorView()     // Catch: java.lang.RuntimeException -> L3d
            android.view.View r0 = r0.getRootView()     // Catch: java.lang.RuntimeException -> L3d
            android.graphics.Bitmap r0 = zzm(r0)     // Catch: java.lang.RuntimeException -> L3d
        L2c:
            r1 = r0
            goto L5
        L2e:
            android.app.Activity r3 = (android.app.Activity) r3     // Catch: java.lang.RuntimeException -> L3d
            android.view.Window r0 = r3.getWindow()     // Catch: java.lang.RuntimeException -> L3d
            android.view.View r0 = r0.getDecorView()     // Catch: java.lang.RuntimeException -> L3d
            android.graphics.Bitmap r0 = zzl(r0)     // Catch: java.lang.RuntimeException -> L3d
            goto L2c
        L3d:
            r0 = move-exception
            java.lang.String r2 = "Fail to capture screen shot"
            com.google.android.gms.internal.zzkd.zzb(r2, r0)
        L44:
            r0 = r1
            goto L2c
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.zzkh.zzaj(android.content.Context):android.graphics.Bitmap");
    }

    public final zzfs zzc(Context context, VersionInfoParcel versionInfoParcel) {
        zzfs zzfsVar;
        synchronized (this.zzail) {
            if (this.zzcea == null) {
                if (context.getApplicationContext() != null) {
                    context = context.getApplicationContext();
                }
                this.zzcea = new zzfs(context, versionInfoParcel, (String) com.google.android.gms.ads.internal.zzu.zzfz().zzd(zzdc.zzaxy));
            }
            zzfsVar = this.zzcea;
        }
        return zzfsVar;
    }

    public static boolean zza(View view, PowerManager powerManager, KeyguardManager keyguardManager) {
        boolean z;
        if (!com.google.android.gms.ads.internal.zzu.zzfq().zzcld) {
            if (keyguardManager == null ? false : keyguardManager.inKeyguardRestrictedInputMode()) {
                z = false;
                if (view.getVisibility() == 0 && view.isShown()) {
                    if ((powerManager != null || powerManager.isScreenOn()) && z) {
                        if (((Boolean) com.google.android.gms.ads.internal.zzu.zzfz().zzd(zzdc.zzbaq)).booleanValue() || view.getLocalVisibleRect(new Rect()) || view.getGlobalVisibleRect(new Rect())) {
                            return true;
                        }
                    }
                }
                return false;
            }
        }
        z = true;
        if (view.getVisibility() == 0) {
            if (powerManager != null || powerManager.isScreenOn()) {
                return ((Boolean) com.google.android.gms.ads.internal.zzu.zzfz().zzd(zzdc.zzbaq)).booleanValue() ? true : true;
            }
        }
        return false;
    }
}
