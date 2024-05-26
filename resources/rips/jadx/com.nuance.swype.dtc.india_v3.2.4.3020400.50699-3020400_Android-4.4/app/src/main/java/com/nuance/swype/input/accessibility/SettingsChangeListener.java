package com.nuance.swype.input.accessibility;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public class SettingsChangeListener {
    private static SettingsChangeListener sInstance;
    private static boolean sIsExploreByTouchOn;
    private static boolean sIsScreenMagnificationOn;
    private static boolean sIsTalkBackOn;
    private ContentObserver contentObserver;
    private ContentResolver contentResolver;
    List<SystemSettingsChangeListener> mListeners = new ArrayList();
    public static final String ACCESSIBILITY_DISPLAY_MAGNIFICATION_ENABLED = "accessibility_display_magnification_enabled";
    private static final Uri magnificationEnableURL = Settings.Secure.getUriFor(ACCESSIBILITY_DISPLAY_MAGNIFICATION_ENABLED);
    public static final String ACCESSIBILITY_TOUCH_EXPLORATION_ENABLED = "touch_exploration_enabled";
    private static final Uri exploreByTouch = Settings.Secure.getUriFor(ACCESSIBILITY_TOUCH_EXPLORATION_ENABLED);
    private static final Object lockIsTalkBackOn = new Object();
    private static final Object lockIsExploreByTouchOn = new Object();
    private static final Object lockIsScreenMagnificationOn = new Object();

    /* loaded from: classes.dex */
    public interface SystemSettingsChangeListener {
        void onExploreByTouchChanged(boolean z);
    }

    protected SettingsChangeListener() {
    }

    public static boolean isScreenMagnificationOn() {
        return sIsScreenMagnificationOn;
    }

    public static boolean isExploreByTouchOn() {
        return sIsExploreByTouchOn;
    }

    public static boolean isTalkBackOn() {
        return sIsTalkBackOn;
    }

    public void setMagnification(boolean enabled) {
        synchronized (lockIsScreenMagnificationOn) {
            sIsScreenMagnificationOn = enabled;
        }
    }

    public void setTalkBack(boolean enabled) {
        synchronized (lockIsTalkBackOn) {
            sIsTalkBackOn = enabled;
        }
    }

    public void setExploreByTouch(boolean enabled) {
        boolean hasChanged = sIsExploreByTouchOn != enabled;
        synchronized (lockIsExploreByTouchOn) {
            sIsExploreByTouchOn = enabled;
        }
        if (hasChanged) {
            notifyExploreByTouchHasChanged();
        }
    }

    public void resetMagnification() {
        boolean value = getBooleanValue(ACCESSIBILITY_DISPLAY_MAGNIFICATION_ENABLED);
        setMagnification(value);
    }

    protected void resetExploreByTouch() {
        boolean value = getBooleanValue(ACCESSIBILITY_TOUCH_EXPLORATION_ENABLED);
        setExploreByTouch(value);
    }

    protected boolean getBooleanValue(String valueFor) {
        return readValue(valueFor) == 1;
    }

    protected int readValue(String valueFor) {
        try {
            int value = Settings.Secure.getInt(this.contentResolver, valueFor);
            return value;
        } catch (Settings.SettingNotFoundException e) {
            return 0;
        }
    }

    public static SettingsChangeListener getInstance() {
        if (sInstance == null) {
            sInstance = new SettingsChangeListener();
        }
        return sInstance;
    }

    public void registerObserver(Context context) {
        this.contentResolver = context.getApplicationContext().getContentResolver();
        resetMagnification();
        this.contentObserver = new ContentObserver(new Handler()) { // from class: com.nuance.swype.input.accessibility.SettingsChangeListener.1
            @Override // android.database.ContentObserver
            public void onChange(boolean selfChange) {
                SettingsChangeListener.this.resetMagnification();
                SettingsChangeListener.this.resetExploreByTouch();
            }
        };
        this.contentResolver.registerContentObserver(magnificationEnableURL, true, this.contentObserver);
        this.contentResolver.registerContentObserver(exploreByTouch, true, this.contentObserver);
    }

    public void unregisterObserver() {
        if (this.contentObserver != null) {
            this.contentResolver.unregisterContentObserver(this.contentObserver);
            this.contentObserver = null;
            this.contentResolver = null;
        }
    }

    public void addListener(SystemSettingsChangeListener listener) {
        if (this.mListeners != null && !this.mListeners.contains(listener)) {
            this.mListeners.add(listener);
        }
    }

    public void removeListener(SystemSettingsChangeListener listener) {
        if (this.mListeners != null && listener != null) {
            this.mListeners.remove(listener);
        }
    }

    private void notifyExploreByTouchHasChanged() {
        Iterator<SystemSettingsChangeListener> it = this.mListeners.iterator();
        while (it.hasNext()) {
            it.next().onExploreByTouchChanged(isExploreByTouchOn());
        }
    }
}
