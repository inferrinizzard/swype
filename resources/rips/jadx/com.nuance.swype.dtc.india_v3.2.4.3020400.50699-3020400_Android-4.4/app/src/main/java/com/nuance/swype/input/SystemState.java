package com.nuance.swype.input;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Vibrator;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import com.nuance.android.compat.SystemPropertiesProxy;
import com.nuance.android.compat.VibratorCompat;
import com.nuance.swype.util.AdsUtil;
import com.nuance.swype.util.LogManager;
import java.util.HashSet;
import java.util.Set;

/* loaded from: classes.dex */
public class SystemState {
    private AudioManager audioManager;
    private ConnectivityManager connectivityManager;
    private Context context;
    protected boolean hasVibrator;
    private boolean isNetworkConnected;
    private int ringerMode;
    private Vibrator vibrator;
    protected static final LogManager.Log log = LogManager.getLog("SystemState");
    private static String SYSPROP_IM_ENABLE_VIBRATE = "sys.im.enable.vibrator";
    private Set<Object> users = new HashSet();
    private final BroadcastReceiver receiver = new BroadcastReceiver() { // from class: com.nuance.swype.input.SystemState.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("android.media.RINGER_MODE_CHANGED")) {
                SystemState.this.updateRingerMode();
            } else if (action.equals("android.net.conn.CONNECTIVITY_CHANGE")) {
                SystemState.this.updateNetworkState();
            }
        }
    };

    public SystemState(Context ctx) {
        this.context = ctx;
        this.audioManager = (AudioManager) this.context.getSystemService("audio");
        this.vibrator = (Vibrator) this.context.getSystemService("vibrator");
        this.hasVibrator = this.vibrator != null && VibratorCompat.hasVibrator(this.vibrator);
        if (this.hasVibrator) {
            SystemPropertiesProxy sysProps = new SystemPropertiesProxy(ctx);
            this.hasVibrator = sysProps.getBoolean(SYSPROP_IM_ENABLE_VIBRATE, true);
        }
        this.connectivityManager = (ConnectivityManager) this.context.getSystemService("connectivity");
        updateRingerMode();
        updateNetworkState();
    }

    public void openWatch(Object user) {
        if (this.users.size() == 0) {
            log.d("open(): start watching state");
            this.context.registerReceiver(this.receiver, new IntentFilter("android.media.RINGER_MODE_CHANGED"));
            this.context.registerReceiver(this.receiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        }
        this.users.add(user);
    }

    public void closeWatch(Object user) {
        if (this.users.remove(user) && this.users.size() == 0) {
            log.d("close(): stop watching state");
            this.context.unregisterReceiver(this.receiver);
        }
    }

    protected ContentResolver getContentResolver() {
        return this.context.getContentResolver();
    }

    protected Context getContext() {
        return this.context;
    }

    private boolean getSysBool(String key, boolean defaultVal) {
        return Settings.System.getInt(getContentResolver(), key, defaultVal ? 1 : 0) != 0;
    }

    public boolean isSilentMode() {
        return 2 != this.ringerMode;
    }

    public boolean isKeySoundEnabled() {
        return (isSilentMode() || isPhoneCallActive()) ? false : true;
    }

    public boolean isVibrateEnabled() {
        return this.hasVibrator && isHapticFeedbackEnabled() && !isPhoneCallActive();
    }

    public boolean hasHapticHardwareSupport() {
        return this.hasVibrator;
    }

    public Vibrator getVibrator() {
        return this.vibrator;
    }

    public boolean isTouchTonesEnabled() {
        return !isSilentMode() && getSysBool("dtmf_tone", true);
    }

    public boolean isSoundEffectsEnabled() {
        return !isSilentMode() && getSysBool("sound_effects_enabled", true);
    }

    public boolean isHapticFeedbackEnabled() {
        return getSysBool("haptic_feedback_enabled", true);
    }

    public boolean isPhoneCallActive() {
        return getTelephonyManager().getCallState() != 0;
    }

    public boolean isNetworkActive() {
        return this.isNetworkConnected;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateRingerMode() {
        this.ringerMode = this.audioManager.getRingerMode();
        log.d("updateRingerMode(): ringer mode: ", Integer.valueOf(this.ringerMode));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateNetworkState() {
        NetworkInfo info = this.connectivityManager.getActiveNetworkInfo();
        this.isNetworkConnected = info != null && info.isConnected();
        if (AdsUtil.sAdsSupported) {
            IMEApplication.from(this.context).getAdSessionTracker().setNetworkConnected(this.isNetworkConnected);
        }
    }

    public int getNetworkOperatorMCC() {
        String networkOperator = getTelephonyManager().getNetworkOperator();
        if (networkOperator != null && networkOperator.length() > 3) {
            try {
                int mcc = Integer.parseInt(networkOperator.substring(0, 3));
                log.d("Country MCC code:", Integer.valueOf(mcc));
                return mcc;
            } catch (NumberFormatException e) {
                log.d("Could not parse networkOperator: " + networkOperator);
            }
        }
        return 0;
    }

    private TelephonyManager getTelephonyManager() {
        return (TelephonyManager) this.context.getApplicationContext().getSystemService("phone");
    }

    public int autoCapsValue() {
        return Settings.System.getInt(getContentResolver(), "auto_caps", -1);
    }

    public int autoReplaceValue() {
        return Settings.System.getInt(getContentResolver(), "auto_replace", -1);
    }

    public int autoPunctuateValue() {
        return Settings.System.getInt(getContentResolver(), "auto_punctuate", -1);
    }

    public int isKeyboardSoundEnabled() {
        return -1;
    }

    public void setKeyboardSound(boolean enabled) {
    }

    public int isKeyboardVibrateEnabled() {
        return -1;
    }

    public void setKeyboardVibrate(boolean enabled) {
    }
}
