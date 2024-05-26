package com.nuance.swype.input.accessibility;

import android.content.Context;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import com.nuance.android.compat.AccessibilityManagerCompat;
import com.nuance.swype.input.InputMethods;
import com.nuance.swype.input.R;
import java.lang.ref.WeakReference;

/* loaded from: classes.dex */
public class AccessibilityInfo {
    private static boolean sLongPressEnabled;
    private static int sLongPressTimeout;
    private static int sShortLongPressTimeout;
    private AccessibilityLabel accessibilityLabel;
    private final boolean isAccessibilityEnabledInBuild;
    private boolean mCapsOnLongPressEnabled;
    private Context mContext;
    private boolean mDeviceExploreByTouchEnabled;
    private boolean mLanguangeSupported;
    private boolean mPhysicalKeyboardOn;
    private WeakReference<AccessibilityManager> mgrRef;

    public AccessibilityInfo(Context context) {
        this.mContext = context;
        this.accessibilityLabel = new AccessibilityLabel(context);
        this.isAccessibilityEnabledInBuild = context.getResources().getBoolean(R.bool.accessibility_keyboard_enabled);
        this.mCapsOnLongPressEnabled = context.getResources().getBoolean(R.bool.accessibility_caps_on_long_press);
        sLongPressEnabled = context.getResources().getBoolean(R.bool.accessibility_long_press);
        sShortLongPressTimeout = context.getResources().getInteger(R.integer.accessibility_short_press_timeout_ms);
        sLongPressTimeout = context.getResources().getInteger(R.integer.accessibility_long_press_timeout_ms);
        syncWithDeviceAccessiblityState();
        setLanguageSupported(true);
        setPhysicalKeyboardEnabled(false);
    }

    public void syncWithDeviceAccessiblityState() {
        AccessibilityManager mgr;
        this.mDeviceExploreByTouchEnabled = false;
        if (this.isAccessibilityEnabledInBuild) {
            if ((this.mgrRef == null || (mgr = this.mgrRef.get()) == null) && (mgr = (AccessibilityManager) this.mContext.getSystemService("accessibility")) != null) {
                this.mgrRef = new WeakReference<>(mgr);
            }
            if (mgr != null && mgr.isEnabled()) {
                this.mDeviceExploreByTouchEnabled = AccessibilityManagerCompat.isTouchExplorationEnabled(mgr);
                boolean isExploreByTouchEnabled = AccessibilityManagerCompat.isTouchExplorationEnabled(mgr);
                SettingsChangeListener.getInstance().setExploreByTouch(isExploreByTouchEnabled);
                boolean isTalkBackEnabled = AccessibilityManagerCompat.isTalkBackEnabled(mgr);
                SettingsChangeListener.getInstance().setTalkBack(isTalkBackEnabled);
            }
        }
    }

    public static boolean isExploreByTouchOn() {
        return SettingsChangeListener.isExploreByTouchOn();
    }

    public void requestSendAccessibilityEvent(AccessibilityEvent event) {
        AccessibilityManager accessibilityManager = (AccessibilityManager) this.mContext.getSystemService("accessibility");
        if (accessibilityManager != null && accessibilityManager.isEnabled()) {
            accessibilityManager.sendAccessibilityEvent(event);
        }
    }

    public AccessibilityLabel getAccessibilityLabel() {
        return this.accessibilityLabel;
    }

    public void interruptTalkback() {
        AccessibilityManager mgr;
        if (this.mgrRef != null && (mgr = this.mgrRef.get()) != null && AccessibilityManagerCompat.isTouchExplorationEnabled(mgr)) {
            mgr.interrupt();
        }
    }

    public boolean isCapsOnLongPressEnabled() {
        return this.mCapsOnLongPressEnabled;
    }

    public static boolean isLongPressEnabled() {
        return sLongPressEnabled;
    }

    public static int getShortLongPressTimeoutValue() {
        return sShortLongPressTimeout;
    }

    public static int getLongPressTimeoutValue() {
        return sLongPressTimeout;
    }

    public static boolean isLongPressAllowed() {
        return isLongPressEnabled();
    }

    public boolean isAccessibilitySupportEnabled() {
        return this.mDeviceExploreByTouchEnabled && this.mLanguangeSupported && !this.mPhysicalKeyboardOn;
    }

    public boolean isDeviceExploreByTouchEnabled() {
        return this.mDeviceExploreByTouchEnabled;
    }

    private void setLanguageSupported(boolean value) {
        this.mLanguangeSupported = value;
    }

    public void updateCurrentLanguage(InputMethods.Language language) {
        this.mLanguangeSupported = !language.isCJK();
    }

    public void setPhysicalKeyboardEnabled(boolean value) {
        this.mPhysicalKeyboardOn = value;
    }

    public void setExploreByTouch(boolean value) {
        this.mDeviceExploreByTouchEnabled = value;
    }

    public boolean isTalkBackOn() {
        AccessibilityManager mgr;
        if (this.isAccessibilityEnabledInBuild) {
            if ((this.mgrRef == null || (mgr = this.mgrRef.get()) == null) && (mgr = (AccessibilityManager) this.mContext.getSystemService("accessibility")) != null) {
                this.mgrRef = new WeakReference<>(mgr);
            }
            if (mgr != null && mgr.isEnabled()) {
                return AccessibilityManagerCompat.isTalkBackEnabled(mgr);
            }
        }
        return false;
    }
}
