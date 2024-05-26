package com.google.android.gms.internal;

import android.content.Context;
import com.nuance.connect.util.TimeConversion;
import com.nuance.swype.input.IME;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

@zzin
/* loaded from: classes.dex */
public final class zzdc {
    public static final zzcy<String> zzaxx = zzcy.zza(0, "gads:sdk_core_experiment_id");
    public static final zzcy<String> zzaxy = zzcy.zza(0, "gads:sdk_core_location", "https://googleads.g.doubleclick.net/mads/static/mad/sdk/native/sdk-core-v40.html");
    public static final zzcy<Boolean> zzaxz = zzcy.zza(0, "gads:request_builder:singleton_webview", (Boolean) false);
    public static final zzcy<String> zzaya = zzcy.zza(0, "gads:request_builder:singleton_webview_experiment_id");
    public static final zzcy<Boolean> zzayb = zzcy.zza(0, "gads:sdk_use_dynamic_module", (Boolean) true);
    public static final zzcy<String> zzayc = zzcy.zza(0, "gads:sdk_use_dynamic_module_experiment_id");
    public static final zzcy<Boolean> zzayd = zzcy.zza(0, "gads:sdk_crash_report_enabled", (Boolean) false);
    public static final zzcy<Boolean> zzaye = zzcy.zza(0, "gads:sdk_crash_report_full_stacktrace", (Boolean) false);
    public static final zzcy<String> zzayf = zzcy.zza(0, "gads:sdk_crash_report_class_prefix", "com.google.");
    public static final zzcy<Boolean> zzayg = zzcy.zza(0, "gads:block_autoclicks", (Boolean) false);
    public static final zzcy<String> zzayh = zzcy.zza(0, "gads:block_autoclicks_experiment_id");
    public static final zzcy<String> zzayi = zzcy.zzb(0, "gads:prefetch:experiment_id");
    public static final zzcy<String> zzayj = zzcy.zza(0, "gads:spam_app_context:experiment_id");
    public static final zzcy<Boolean> zzayk = zzcy.zza(0, "gads:spam_app_context:enabled", (Boolean) false);
    public static final zzcy<String> zzayl = zzcy.zza(0, "gads:video_stream_cache:experiment_id");
    public static final zzcy<Integer> zzaym = zzcy.zza(0, "gads:video_stream_cache:limit_count", 5);
    public static final zzcy<Integer> zzayn = zzcy.zza(0, "gads:video_stream_cache:limit_space", 8388608);
    public static final zzcy<Integer> zzayo = zzcy.zza(0, "gads:video_stream_exo_cache:buffer_size", 8388608);
    public static final zzcy<Long> zzayp = zzcy.zza(0, "gads:video_stream_cache:limit_time_sec", 300L);
    public static final zzcy<Long> zzayq = zzcy.zza(0, "gads:video_stream_cache:notify_interval_millis", 1000L);
    public static final zzcy<Integer> zzayr = zzcy.zza(0, "gads:video_stream_cache:connect_timeout_millis", 10000);
    public static final zzcy<Boolean> zzays = zzcy.zza(0, "gads:video:metric_reporting_enabled", (Boolean) false);
    public static final zzcy<String> zzayt = zzcy.zza(0, "gads:video:metric_frame_hash_times", "");
    public static final zzcy<Long> zzayu = zzcy.zza(0, "gads:video:metric_frame_hash_time_leniency", 500L);
    public static final zzcy<String> zzayv = zzcy.zzb(0, "gads:spam_ad_id_decorator:experiment_id");
    public static final zzcy<Boolean> zzayw = zzcy.zza(0, "gads:spam_ad_id_decorator:enabled", (Boolean) false);
    public static final zzcy<String> zzayx = zzcy.zzb(0, "gads:looper_for_gms_client:experiment_id");
    public static final zzcy<Boolean> zzayy = zzcy.zza(0, "gads:looper_for_gms_client:enabled", (Boolean) true);
    public static final zzcy<Boolean> zzayz = zzcy.zza(0, "gads:sw_ad_request_service:enabled", (Boolean) true);
    public static final zzcy<Boolean> zzaza = zzcy.zza(0, "gads:sw_dynamite:enabled", (Boolean) true);
    public static final zzcy<String> zzazb = zzcy.zza(0, "gad:mraid:url_banner", "https://googleads.g.doubleclick.net/mads/static/mad/sdk/native/mraid/v2/mraid_app_banner.js");
    public static final zzcy<String> zzazc = zzcy.zza(0, "gad:mraid:url_expanded_banner", "https://googleads.g.doubleclick.net/mads/static/mad/sdk/native/mraid/v2/mraid_app_expanded_banner.js");
    public static final zzcy<String> zzazd = zzcy.zza(0, "gad:mraid:url_interstitial", "https://googleads.g.doubleclick.net/mads/static/mad/sdk/native/mraid/v2/mraid_app_interstitial.js");
    public static final zzcy<Boolean> zzaze = zzcy.zza(0, "gads:enabled_sdk_csi", (Boolean) false);
    public static final zzcy<String> zzazf = zzcy.zza(0, "gads:sdk_csi_server", "https://csi.gstatic.com/csi");
    public static final zzcy<Boolean> zzazg = zzcy.zza(0, "gads:sdk_csi_write_to_file", (Boolean) false);
    public static final zzcy<Boolean> zzazh = zzcy.zza(0, "gads:enable_content_fetching", (Boolean) true);
    public static final zzcy<Integer> zzazi = zzcy.zza(0, "gads:content_length_weight", 1);
    public static final zzcy<Integer> zzazj = zzcy.zza(0, "gads:content_age_weight", 1);
    public static final zzcy<Integer> zzazk = zzcy.zza(0, "gads:min_content_len", 11);
    public static final zzcy<Integer> zzazl = zzcy.zza(0, "gads:fingerprint_number", 10);
    public static final zzcy<Integer> zzazm = zzcy.zza(0, "gads:sleep_sec", 10);
    public static final zzcy<Boolean> zzazn = zzcy.zza(0, "gad:app_index_enabled", (Boolean) true);
    public static final zzcy<Boolean> zzazo = zzcy.zza(0, "gads:app_index:without_content_info_present:enabled", (Boolean) true);
    public static final zzcy<Long> zzazp = zzcy.zza(0, "gads:app_index:timeout_ms", 1000L);
    public static final zzcy<String> zzazq = zzcy.zza(0, "gads:app_index:experiment_id");
    public static final zzcy<String> zzazr = zzcy.zza(0, "gads:kitkat_interstitial_workaround:experiment_id");
    public static final zzcy<Boolean> zzazs = zzcy.zza(0, "gads:kitkat_interstitial_workaround:enabled", (Boolean) true);
    public static final zzcy<Boolean> zzazt = zzcy.zza(0, "gads:interstitial_follow_url", (Boolean) true);
    public static final zzcy<Boolean> zzazu = zzcy.zza(0, "gads:interstitial_follow_url:register_click", (Boolean) true);
    public static final zzcy<String> zzazv = zzcy.zza(0, "gads:interstitial_follow_url:experiment_id");
    public static final zzcy<Boolean> zzazw = zzcy.zza(0, "gads:analytics_enabled", (Boolean) true);
    public static final zzcy<Boolean> zzazx = zzcy.zza(0, "gads:ad_key_enabled", (Boolean) false);
    public static final zzcy<Integer> zzazy = zzcy.zza(0, "gads:webview_cache_version", 0);
    public static final zzcy<Boolean> zzazz = zzcy.zza(1, "gads:webview_recycle:enabled", (Boolean) false);
    public static final zzcy<String> zzbaa = zzcy.zza(1, "gads:webview_recycle:experiment_id");
    public static final zzcy<String> zzbab = zzcy.zzb(0, "gads:pan:experiment_id");
    public static final zzcy<String> zzbac = zzcy.zza(0, "gads:native:engine_url", "//googleads.g.doubleclick.net/mads/static/mad/sdk/native/native_ads.html");
    public static final zzcy<Boolean> zzbad = zzcy.zza(0, "gads:ad_manager_creator:enabled", (Boolean) true);
    public static final zzcy<Boolean> zzbae = zzcy.zza(1, "gads:interstitial_ad_pool:enabled", (Boolean) false);
    public static final zzcy<Boolean> zzbaf = zzcy.zza(1, "gads:interstitial_ad_pool:enabled_for_rewarded", (Boolean) false);
    public static final zzcy<String> zzbag = zzcy.zza(1, "gads:interstitial_ad_pool:schema", "customTargeting");
    public static final zzcy<String> zzbah = zzcy.zza(1, "gads:interstitial_ad_pool:request_exclusions", "com.google.ads.mediation.admob.AdMobAdapter/_ad");
    public static final zzcy<Integer> zzbai = zzcy.zza(1, "gads:interstitial_ad_pool:max_pools", 3);
    public static final zzcy<Integer> zzbaj = zzcy.zza(1, "gads:interstitial_ad_pool:max_pool_depth", 2);
    public static final zzcy<Integer> zzbak = zzcy.zza(1, "gads:interstitial_ad_pool:time_limit_sec", 1200);
    public static final zzcy<String> zzbal = zzcy.zza(1, "gads:interstitial_ad_pool:ad_unit_exclusions", "(?!)");
    public static final zzcy<String> zzbam = zzcy.zza(1, "gads:spherical_video:vertex_shader", "");
    public static final zzcy<String> zzban = zzcy.zza(1, "gads:spherical_video:fragment_shader", "");
    public static final zzcy<String> zzbao = zzcy.zza(1, "gads:spherical_video:experiment_id");
    public static final zzcy<Boolean> zzbap = zzcy.zza(0, "gads:log:verbose_enabled", (Boolean) false);
    public static final zzcy<Boolean> zzbaq = zzcy.zza(1, "gads:include_local_global_rectangles", (Boolean) false);
    public static final zzcy<String> zzbar = zzcy.zza(1, "gads:include_local_global_rectangles:experiment_id");
    public static final zzcy<Boolean> zzbas = zzcy.zza(0, "gads:device_info_caching:enabled", (Boolean) true);
    public static final zzcy<Long> zzbat = zzcy.zza(0, "gads:device_info_caching_expiry_ms:expiry", 300000L);
    public static final zzcy<Boolean> zzbau = zzcy.zza(0, "gads:gen204_signals:enabled", (Boolean) false);
    public static final zzcy<Boolean> zzbav = zzcy.zza(0, "gads:webview:error_reporting_enabled", (Boolean) false);
    public static final zzcy<Boolean> zzbaw = zzcy.zza(0, "gads:adid_reporting:enabled", (Boolean) false);
    public static final zzcy<Boolean> zzbax = zzcy.zza(0, "gads:ad_settings_page_reporting:enabled", (Boolean) false);
    public static final zzcy<Boolean> zzbay = zzcy.zza(0, "gads:adid_info_gmscore_upgrade_reporting:enabled", (Boolean) false);
    public static final zzcy<Boolean> zzbaz = zzcy.zza(0, "gads:request_pkg:enabled", (Boolean) true);
    public static final zzcy<Boolean> zzbba = zzcy.zza(0, "gads:gmsg:disable_back_button:enabled", (Boolean) false);
    public static final zzcy<Boolean> zzbbb = zzcy.zza(0, "gads:gmsg:video_meta:enabled", (Boolean) true);
    public static final zzcy<String> zzbbc = zzcy.zza(0, "gads:gmsg:video_meta:experiment_id");
    public static final zzcy<Long> zzbbd = zzcy.zza(0, "gads:network:cache_prediction_duration_s", 300L);
    public static final zzcy<Boolean> zzbbe = zzcy.zza(0, "gads:mediation:dynamite_first:admobadapter", (Boolean) true);
    public static final zzcy<Boolean> zzbbf = zzcy.zza(0, "gads:mediation:dynamite_first:adurladapter", (Boolean) true);
    public static final zzcy<Long> zzbbg = zzcy.zza(0, "gads:ad_loader:timeout_ms", TimeConversion.MILLIS_IN_MINUTE);
    public static final zzcy<Long> zzbbh = zzcy.zza(0, "gads:rendering:timeout_ms", TimeConversion.MILLIS_IN_MINUTE);
    public static final zzcy<Boolean> zzbbi = zzcy.zza(0, "gads:adshield:enable_adshield_instrumentation", (Boolean) false);
    public static final zzcy<Long> zzbbj = zzcy.zza(1, "gads:gestures:task_timeout", IME.NEXT_SCAN_IN_MILLIS);
    public static final zzcy<String> zzbbk = zzcy.zza(1, "gads:gestures:encrypt_size_limit:experiment_id");
    public static final zzcy<Boolean> zzbbl = zzcy.zza(1, "gads:gestures:encrypt_size_limit:enabled", (Boolean) true);
    public static final zzcy<String> zzbbm = zzcy.zza(1, "gads:gestures:cpu:experiment_id");
    public static final zzcy<Boolean> zzbbn = zzcy.zza(1, "gads:gestures:cpu_query:enabled", (Boolean) false);
    public static final zzcy<Boolean> zzbbo = zzcy.zza(1, "gads:gestures:cpu_click:enabled", (Boolean) false);
    public static final zzcy<String> zzbbp = zzcy.zza(1, "gads:gestures:jbk:experiment_id");
    public static final zzcy<Boolean> zzbbq = zzcy.zza(1, "gads:gestures:jbk_query:enabled", (Boolean) false);
    public static final zzcy<Boolean> zzbbr = zzcy.zza(1, "gads:gestures:jbk_click:enabled", (Boolean) false);
    public static final zzcy<String> zzbbs = zzcy.zza(1, "gads:gestures:stk:experiment_id");
    public static final zzcy<Boolean> zzbbt = zzcy.zza(1, "gads:gestures:stk:enabled", (Boolean) false);
    public static final zzcy<Boolean> zzbbu = zzcy.zza(0, "gass:client:enabled", (Boolean) false);
    public static final zzcy<String> zzbbv = zzcy.zza(0, "gass:client:experiment_id");
    public static final zzcy<Boolean> zzbbw = zzcy.zza(0, "gass:enabled", (Boolean) false);
    public static final zzcy<Boolean> zzbbx = zzcy.zza(0, "gass:enable_int_signal", (Boolean) true);
    public static final zzcy<Boolean> zzbby = zzcy.zza(0, "gads:adid_notification:first_party_check:enabled", (Boolean) true);
    public static final zzcy<Boolean> zzbbz = zzcy.zza(0, "gads:edu_device_helper:enabled", (Boolean) true);
    public static final zzcy<Boolean> zzbca = zzcy.zza(0, "gads:support_screen_shot", (Boolean) true);
    public static final zzcy<Boolean> zzbcb = zzcy.zza(0, "gads:use_get_drawing_cache_for_screenshot:enabled", (Boolean) true);
    public static final zzcy<String> zzbcc = zzcy.zza(0, "gads:use_get_drawing_cache_for_screenshot:experiment_id");
    public static final zzcy<Long> zzbcd = zzcy.zza(0, "gads:js_flags:update_interval", TimeUnit.HOURS.toMillis(12));
    public static final zzcy<Boolean> zzbce = zzcy.zza(0, "gads:custom_render:ping_on_ad_rendered", (Boolean) false);
    public static final zzcy<Boolean> zzbcf = zzcy.zza(1, "gads:singleton_webview_native", (Boolean) false);
    public static final zzcy<String> zzbcg = zzcy.zza(1, "gads:singleton_webview_native:experiment_id");
    public static final zzcy<Boolean> zzbch = zzcy.zza(1, "gads:enable_untrack_view_native", (Boolean) true);
    public static final zzcy<Boolean> zzbci = zzcy.zza(1, "gads:reset_listeners_preparead_native", (Boolean) true);
    public static final zzcy<Boolean> zzbcj = zzcy.zza(0, "gads:method_tracing:enabled", (Boolean) false);
    public static final zzcy<Long> zzbck = zzcy.zza(0, "gads:method_tracing:duration_ms", 30000L);
    public static final zzcy<Integer> zzbcl = zzcy.zza(0, "gads:method_tracing:count", 5);
    public static final zzcy<Integer> zzbcm = zzcy.zza(0, "gads:method_tracing:filesize", 134217728);
    public static final zzcy<Boolean> zzbcn = zzcy.zza(1, "gads:auto_location_for_coarse_permission", (Boolean) false);
    public static final zzcy<String> zzbco = zzcy.zzb(1, "gads:auto_location_for_coarse_permission:experiment_id");
    public static final zzcy<Long> zzbcp = zzcy.zza(1, "gads:auto_location_timeout", IME.NEXT_SCAN_IN_MILLIS);
    public static final zzcy<String> zzbcq = zzcy.zzb(1, "gads:auto_location_timeout:experiment_id");
    public static final zzcy<Long> zzbcr = zzcy.zza(1, "gads:auto_location_interval", -1L);
    public static final zzcy<String> zzbcs = zzcy.zzb(1, "gads:auto_location_interval:experiment_id");
    public static final zzcy<Boolean> zzbct = zzcy.zza(1, "gads:fetch_app_settings_using_cld:enabled", (Boolean) false);
    public static final zzcy<String> zzbcu = zzcy.zza(1, "gads:fetch_app_settings_using_cld:enabled:experiment_id");
    public static final zzcy<Long> zzbcv = zzcy.zza(1, "gads:fetch_app_settings_using_cld:refresh_interval_ms", 7200000L);
    public static final zzcy<String> zzbcw = zzcy.zza(1, "gads:fetch_app_settings_using_cld:refresh_interval_ms:experiment_id");
    public static final zzcy<String> zzbcx = zzcy.zza(0, "gads:afs:csa:experiment_id");
    public static final zzcy<String> zzbcy = zzcy.zza(0, "gads:afs:csa_webview_gmsg_ad_failed", "gmsg://noAdLoaded");
    public static final zzcy<String> zzbcz = zzcy.zza(0, "gads:afs:csa_webview_gmsg_script_load_failed", "gmsg://scriptLoadFailed");
    public static final zzcy<String> zzbda = zzcy.zza(0, "gads:afs:csa_webview_gmsg_ad_loaded", "gmsg://adResized");
    public static final zzcy<String> zzbdb = zzcy.zza(0, "gads:afs:csa_webview_static_file_path", "/afs/ads/i/webview.html");
    public static final zzcy<String> zzbdc = zzcy.zza(0, "gads:afs:csa_webview_custom_domain_param_key", "csa_customDomain");
    public static final zzcy<Long> zzbdd = zzcy.zza(0, "gads:afs:csa_webview_adshield_timeout_ms", 1000L);
    public static final zzcy<Boolean> zzbde = zzcy.zza(0, "gads:afs:csa_ad_manager_enabled", (Boolean) true);
    public static final zzcy<Boolean> zzbdf = zzcy.zza(0, "gads:safe_browsing:reporting:malicious:enabled", (Boolean) false);
    public static final zzcy<Boolean> zzbdg = zzcy.zza(0, "gads:safe_browsing:reporting:full:enabled", (Boolean) false);
    public static final zzcy<Boolean> zzbdh = zzcy.zza(0, "gads:safe_browsing:screenshot:enabled", (Boolean) false);
    public static final zzcy<String> zzbdi = zzcy.zza(0, "gads:safe_browsing:reporting:url", "https://sb-ssl.google.com/safebrowsing/clientreport/malware");
    public static final zzcy<String> zzbdj = zzcy.zza(0, "gads:safe_browsing:api_key", "AIzaSyDRKQ9d6kfsoZT2lUnZcZnBYvH69HExNPE");
    public static final zzcy<Long> zzbdk = zzcy.zza(0, "gads:safe_browsing:safety_net:delay_ms", IME.NEXT_SCAN_IN_MILLIS);
    public static final zzcy<String> zzbdl = zzcy.zza(0, "gads:safe_browsing:experiment_id");
    public static final zzcy<Boolean> zzbdm = zzcy.zza(0, "gads:safe_browsing:debug", (Boolean) false);
    public static final zzcy<Boolean> zzbdn = zzcy.zza(0, "gads:webview_cookie:enabled", (Boolean) true);

    public static void initialize(final Context context) {
        zzkt.zzb(new Callable<Void>() { // from class: com.google.android.gms.internal.zzdc.1
            /* JADX INFO: Access modifiers changed from: private */
            @Override // java.util.concurrent.Callable
            /* renamed from: zzcx, reason: merged with bridge method [inline-methods] */
            public Void call() {
                zzdb zzfz = com.google.android.gms.ads.internal.zzu.zzfz();
                Context context2 = context;
                synchronized (zzfz.zzail) {
                    if (zzfz.zzamt) {
                        return null;
                    }
                    Context remoteContext = com.google.android.gms.common.zze.getRemoteContext(context2);
                    if (remoteContext == null) {
                        return null;
                    }
                    com.google.android.gms.ads.internal.zzu.zzfx();
                    zzfz.zzaxu = remoteContext.getSharedPreferences("google_ads_flags", 1);
                    zzfz.zzamt = true;
                    return null;
                }
            }
        });
    }

    public static List<String> zzjx() {
        return com.google.android.gms.ads.internal.zzu.zzfy().zzjx();
    }

    public static List<String> zzjy() {
        zzcz zzfy = com.google.android.gms.ads.internal.zzu.zzfy();
        List<String> zzjx = zzfy.zzjx();
        Iterator<zzcy<String>> it = zzfy.zzaxt.iterator();
        while (it.hasNext()) {
            String str = (String) com.google.android.gms.ads.internal.zzu.zzfz().zzd(it.next());
            if (str != null) {
                zzjx.add(str);
            }
        }
        return zzjx;
    }
}
