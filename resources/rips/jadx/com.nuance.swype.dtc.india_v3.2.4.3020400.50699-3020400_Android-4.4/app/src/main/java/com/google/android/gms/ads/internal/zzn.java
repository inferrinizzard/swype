package com.google.android.gms.ads.internal;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import com.facebook.applinks.AppLinkData;
import com.google.android.gms.ads.internal.zzf;
import com.google.android.gms.internal.zzdr;
import com.google.android.gms.internal.zzep;
import com.google.android.gms.internal.zzge;
import com.google.android.gms.internal.zzgn;
import com.google.android.gms.internal.zzgo;
import com.google.android.gms.internal.zzin;
import com.google.android.gms.internal.zzju;
import com.google.android.gms.internal.zzkd;
import com.google.android.gms.internal.zzlh;
import com.google.android.gms.internal.zzli;
import com.nuance.connect.comm.MessageAPI;
import java.io.ByteArrayOutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@zzin
/* loaded from: classes.dex */
public class zzn {
    private static zzep zza(final zzgn zzgnVar, final zzgo zzgoVar, final zzf.zza zzaVar) {
        return new zzep() { // from class: com.google.android.gms.ads.internal.zzn.5
            @Override // com.google.android.gms.internal.zzep
            public final void zza(zzlh zzlhVar, Map<String, String> map) {
                View view = zzlhVar.getView();
                if (view == null) {
                    return;
                }
                try {
                    if (zzgn.this != null) {
                        if (zzgn.this.getOverrideClickHandling()) {
                            zzn.zzb(zzlhVar);
                        } else {
                            zzgn.this.zzk(com.google.android.gms.dynamic.zze.zzac(view));
                            zzaVar.onClick();
                        }
                    } else if (zzgoVar != null) {
                        if (zzgoVar.getOverrideClickHandling()) {
                            zzn.zzb(zzlhVar);
                        } else {
                            zzgoVar.zzk(com.google.android.gms.dynamic.zze.zzac(view));
                            zzaVar.onClick();
                        }
                    }
                } catch (RemoteException e) {
                    zzkd.zzd("Unable to call handleClick on mapper", e);
                }
            }
        };
    }

    private static String zza(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        if (bitmap == null) {
            zzkd.zzcx("Bitmap is null. Returning empty string");
            return "";
        }
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        String encodeToString = Base64.encodeToString(byteArrayOutputStream.toByteArray(), 0);
        String valueOf = String.valueOf("data:image/png;base64,");
        String valueOf2 = String.valueOf(encodeToString);
        return valueOf2.length() != 0 ? valueOf.concat(valueOf2) : new String(valueOf);
    }

    static String zza(zzdr zzdrVar) {
        if (zzdrVar == null) {
            zzkd.zzcx("Image is null. Returning empty string");
            return "";
        }
        try {
            Uri uri = zzdrVar.getUri();
            if (uri != null) {
                return uri.toString();
            }
        } catch (RemoteException e) {
            zzkd.zzcx("Unable to get image uri. Trying data uri next");
        }
        return zzb(zzdrVar);
    }

    public static void zza(zzju zzjuVar, zzf.zza zzaVar) {
        if (zzjuVar == null || !zzg(zzjuVar)) {
            return;
        }
        zzlh zzlhVar = zzjuVar.zzbtm;
        View view = zzlhVar != null ? zzlhVar.getView() : null;
        if (view == null) {
            zzkd.zzcx("AdWebView is null");
            return;
        }
        try {
            List<String> list = zzjuVar.zzbon != null ? zzjuVar.zzbon.zzbni : null;
            if (list == null || list.isEmpty()) {
                zzkd.zzcx("No template ids present in mediation response");
                return;
            }
            zzgn zzmo = zzjuVar.zzboo != null ? zzjuVar.zzboo.zzmo() : null;
            zzgo zzmp = zzjuVar.zzboo != null ? zzjuVar.zzboo.zzmp() : null;
            if (list.contains(MessageAPI.DELAYED_FROM) && zzmo != null) {
                zzmo.zzl(com.google.android.gms.dynamic.zze.zzac(view));
                if (!zzmo.getOverrideImpressionRecording()) {
                    zzmo.recordImpression();
                }
                zzlhVar.zzuj().zza("/nativeExpressViewClicked", zza(zzmo, (zzgo) null, zzaVar));
                return;
            }
            if (!list.contains("1") || zzmp == null) {
                zzkd.zzcx("No matching template id and mapper");
                return;
            }
            zzmp.zzl(com.google.android.gms.dynamic.zze.zzac(view));
            if (!zzmp.getOverrideImpressionRecording()) {
                zzmp.recordImpression();
            }
            zzlhVar.zzuj().zza("/nativeExpressViewClicked", zza((zzgn) null, zzmp, zzaVar));
        } catch (RemoteException e) {
            zzkd.zzd("Error occurred while recording impression and registering for clicks", e);
        }
    }

    private static String zzb(zzdr zzdrVar) {
        String zza;
        try {
            com.google.android.gms.dynamic.zzd zzkt = zzdrVar.zzkt();
            if (zzkt == null) {
                zzkd.zzcx("Drawable is null. Returning empty string");
                zza = "";
            } else {
                Drawable drawable = (Drawable) com.google.android.gms.dynamic.zze.zzad(zzkt);
                if (drawable instanceof BitmapDrawable) {
                    zza = zza(((BitmapDrawable) drawable).getBitmap());
                } else {
                    zzkd.zzcx("Drawable is not an instance of BitmapDrawable. Returning empty string");
                    zza = "";
                }
            }
            return zza;
        } catch (RemoteException e) {
            zzkd.zzcx("Unable to get drawable. Returning empty string");
            return "";
        }
    }

    public static View zzf(zzju zzjuVar) {
        if (zzjuVar == null) {
            zzkd.e("AdState is null");
            return null;
        }
        if (zzg(zzjuVar) && zzjuVar.zzbtm != null) {
            return zzjuVar.zzbtm.getView();
        }
        try {
            com.google.android.gms.dynamic.zzd view = zzjuVar.zzboo != null ? zzjuVar.zzboo.getView() : null;
            if (view != null) {
                return (View) com.google.android.gms.dynamic.zze.zzad(view);
            }
            zzkd.zzcx("View in mediation adapter is null.");
            return null;
        } catch (RemoteException e) {
            zzkd.zzd("Could not get View from mediation adapter.", e);
            return null;
        }
    }

    public static boolean zzg(zzju zzjuVar) {
        return (zzjuVar == null || !zzjuVar.zzcby || zzjuVar.zzbon == null || zzjuVar.zzbon.zzbnf == null) ? false : true;
    }

    public static boolean zza(final zzlh zzlhVar, zzge zzgeVar, final CountDownLatch countDownLatch) {
        boolean z;
        try {
            View view = zzlhVar.getView();
            if (view == null) {
                zzkd.zzcx("AdWebView is null");
                z = false;
            } else {
                view.setVisibility(4);
                List<String> list = zzgeVar.zzbon.zzbni;
                if (list == null || list.isEmpty()) {
                    zzkd.zzcx("No template ids present in mediation response");
                    z = false;
                } else {
                    zzlhVar.zzuj().zza("/nativeExpressAssetsLoaded", new zzep() { // from class: com.google.android.gms.ads.internal.zzn.3
                        @Override // com.google.android.gms.internal.zzep
                        public final void zza(zzlh zzlhVar2, Map<String, String> map) {
                            countDownLatch.countDown();
                            zzlhVar2.getView().setVisibility(0);
                        }
                    });
                    zzlhVar.zzuj().zza("/nativeExpressAssetsLoadingFailed", new zzep() { // from class: com.google.android.gms.ads.internal.zzn.4
                        @Override // com.google.android.gms.internal.zzep
                        public final void zza(zzlh zzlhVar2, Map<String, String> map) {
                            zzkd.zzcx("Adapter returned an ad, but assets substitution failed");
                            countDownLatch.countDown();
                            zzlhVar2.destroy();
                        }
                    });
                    zzgn zzmo = zzgeVar.zzboo.zzmo();
                    zzgo zzmp = zzgeVar.zzboo.zzmp();
                    if (list.contains(MessageAPI.DELAYED_FROM) && zzmo != null) {
                        final com.google.android.gms.ads.internal.formats.zzd zzdVar = new com.google.android.gms.ads.internal.formats.zzd(zzmo.getHeadline(), zzmo.getImages(), zzmo.getBody(), zzmo.zzku(), zzmo.getCallToAction(), zzmo.getStarRating(), zzmo.getStore(), zzmo.getPrice(), null, zzmo.getExtras());
                        final String str = zzgeVar.zzbon.zzbnh;
                        zzlhVar.zzuj().zzbya = new zzli.zza() { // from class: com.google.android.gms.ads.internal.zzn.1
                            @Override // com.google.android.gms.internal.zzli.zza
                            public final void zza(zzlh zzlhVar2, boolean z2) {
                                try {
                                    JSONObject jSONObject = new JSONObject();
                                    jSONObject.put("headline", com.google.android.gms.ads.internal.formats.zzd.this.getHeadline());
                                    jSONObject.put("body", com.google.android.gms.ads.internal.formats.zzd.this.getBody());
                                    jSONObject.put("call_to_action", com.google.android.gms.ads.internal.formats.zzd.this.getCallToAction());
                                    jSONObject.put("price", com.google.android.gms.ads.internal.formats.zzd.this.getPrice());
                                    jSONObject.put("star_rating", String.valueOf(com.google.android.gms.ads.internal.formats.zzd.this.getStarRating()));
                                    jSONObject.put("store", com.google.android.gms.ads.internal.formats.zzd.this.getStore());
                                    jSONObject.put("icon", zzn.zza(com.google.android.gms.ads.internal.formats.zzd.this.zzku()));
                                    JSONArray jSONArray = new JSONArray();
                                    List images = com.google.android.gms.ads.internal.formats.zzd.this.getImages();
                                    if (images != null) {
                                        Iterator it = images.iterator();
                                        while (it.hasNext()) {
                                            jSONArray.put(zzn.zza(zzn.zzf(it.next())));
                                        }
                                    }
                                    jSONObject.put("images", jSONArray);
                                    jSONObject.put(AppLinkData.ARGUMENTS_EXTRAS_KEY, zzn.zzb(com.google.android.gms.ads.internal.formats.zzd.this.getExtras(), str));
                                    JSONObject jSONObject2 = new JSONObject();
                                    jSONObject2.put("assets", jSONObject);
                                    jSONObject2.put("template_id", MessageAPI.DELAYED_FROM);
                                    zzlhVar.zza("google.afma.nativeExpressAds.loadAssets", jSONObject2);
                                } catch (JSONException e) {
                                    zzkd.zzd("Exception occurred when loading assets", e);
                                }
                            }
                        };
                    } else if (!list.contains("1") || zzmp == null) {
                        zzkd.zzcx("No matching template id and mapper");
                        z = false;
                    } else {
                        final com.google.android.gms.ads.internal.formats.zze zzeVar = new com.google.android.gms.ads.internal.formats.zze(zzmp.getHeadline(), zzmp.getImages(), zzmp.getBody(), zzmp.zzky(), zzmp.getCallToAction(), zzmp.getAdvertiser(), null, zzmp.getExtras());
                        final String str2 = zzgeVar.zzbon.zzbnh;
                        zzlhVar.zzuj().zzbya = new zzli.zza() { // from class: com.google.android.gms.ads.internal.zzn.2
                            @Override // com.google.android.gms.internal.zzli.zza
                            public final void zza(zzlh zzlhVar2, boolean z2) {
                                try {
                                    JSONObject jSONObject = new JSONObject();
                                    jSONObject.put("headline", com.google.android.gms.ads.internal.formats.zze.this.getHeadline());
                                    jSONObject.put("body", com.google.android.gms.ads.internal.formats.zze.this.getBody());
                                    jSONObject.put("call_to_action", com.google.android.gms.ads.internal.formats.zze.this.getCallToAction());
                                    jSONObject.put("advertiser", com.google.android.gms.ads.internal.formats.zze.this.getAdvertiser());
                                    jSONObject.put("logo", zzn.zza(com.google.android.gms.ads.internal.formats.zze.this.zzky()));
                                    JSONArray jSONArray = new JSONArray();
                                    List images = com.google.android.gms.ads.internal.formats.zze.this.getImages();
                                    if (images != null) {
                                        Iterator it = images.iterator();
                                        while (it.hasNext()) {
                                            jSONArray.put(zzn.zza(zzn.zzf(it.next())));
                                        }
                                    }
                                    jSONObject.put("images", jSONArray);
                                    jSONObject.put(AppLinkData.ARGUMENTS_EXTRAS_KEY, zzn.zzb(com.google.android.gms.ads.internal.formats.zze.this.getExtras(), str2));
                                    JSONObject jSONObject2 = new JSONObject();
                                    jSONObject2.put("assets", jSONObject);
                                    jSONObject2.put("template_id", "1");
                                    zzlhVar.zza("google.afma.nativeExpressAds.loadAssets", jSONObject2);
                                } catch (JSONException e) {
                                    zzkd.zzd("Exception occurred when loading assets", e);
                                }
                            }
                        };
                    }
                    String str3 = zzgeVar.zzbon.zzbnf;
                    String str4 = zzgeVar.zzbon.zzbng;
                    if (str4 != null) {
                        zzlhVar.loadDataWithBaseURL(str4, str3, "text/html", "UTF-8", null);
                    } else {
                        zzlhVar.loadData(str3, "text/html", "UTF-8");
                    }
                    z = true;
                }
            }
        } catch (RemoteException e) {
            zzkd.zzd("Unable to invoke load assets", e);
            z = false;
        } catch (RuntimeException e2) {
            countDownLatch.countDown();
            throw e2;
        }
        if (!z) {
            countDownLatch.countDown();
        }
        return z;
    }

    static /* synthetic */ zzdr zzf(Object obj) {
        if (obj instanceof IBinder) {
            return zzdr.zza.zzy((IBinder) obj);
        }
        return null;
    }

    static /* synthetic */ JSONObject zzb(Bundle bundle, String str) throws JSONException {
        JSONObject jSONObject = new JSONObject();
        if (bundle == null || TextUtils.isEmpty(str)) {
            return jSONObject;
        }
        JSONObject jSONObject2 = new JSONObject(str);
        Iterator<String> keys = jSONObject2.keys();
        while (keys.hasNext()) {
            String next = keys.next();
            if (bundle.containsKey(next)) {
                if ("image".equals(jSONObject2.getString(next))) {
                    Object obj = bundle.get(next);
                    if (obj instanceof Bitmap) {
                        jSONObject.put(next, zza((Bitmap) obj));
                    } else {
                        zzkd.zzcx("Invalid type. An image type extra should return a bitmap");
                    }
                } else if (bundle.get(next) instanceof Bitmap) {
                    zzkd.zzcx("Invalid asset type. Bitmap should be returned only for image type");
                } else {
                    jSONObject.put(next, String.valueOf(bundle.get(next)));
                }
            }
        }
        return jSONObject;
    }

    static /* synthetic */ void zzb(zzlh zzlhVar) {
        View.OnClickListener zzuw = zzlhVar.zzuw();
        if (zzuw != null) {
            zzuw.onClick(zzlhVar.getView());
        }
    }
}
