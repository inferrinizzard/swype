package com.facebook;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import com.facebook.internal.Utility;
import com.facebook.internal.Validate;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class ProfileManager {
    static final String ACTION_CURRENT_PROFILE_CHANGED = "com.facebook.sdk.ACTION_CURRENT_PROFILE_CHANGED";
    static final String EXTRA_NEW_PROFILE = "com.facebook.sdk.EXTRA_NEW_PROFILE";
    static final String EXTRA_OLD_PROFILE = "com.facebook.sdk.EXTRA_OLD_PROFILE";
    private static volatile ProfileManager instance;
    private Profile currentProfile;
    private final LocalBroadcastManager localBroadcastManager;
    private final ProfileCache profileCache;

    ProfileManager(LocalBroadcastManager localBroadcastManager, ProfileCache profileCache) {
        Validate.notNull(localBroadcastManager, "localBroadcastManager");
        Validate.notNull(profileCache, "profileCache");
        this.localBroadcastManager = localBroadcastManager;
        this.profileCache = profileCache;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ProfileManager getInstance() {
        if (instance == null) {
            synchronized (ProfileManager.class) {
                if (instance == null) {
                    LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(FacebookSdk.getApplicationContext());
                    instance = new ProfileManager(localBroadcastManager, new ProfileCache());
                }
            }
        }
        return instance;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Profile getCurrentProfile() {
        return this.currentProfile;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean loadCurrentProfile() {
        Profile profile = this.profileCache.load();
        if (profile == null) {
            return false;
        }
        setCurrentProfile(profile, false);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void setCurrentProfile(Profile currentProfile) {
        setCurrentProfile(currentProfile, true);
    }

    private void setCurrentProfile(Profile currentProfile, boolean writeToCache) {
        Profile oldProfile = this.currentProfile;
        this.currentProfile = currentProfile;
        if (writeToCache) {
            if (currentProfile != null) {
                this.profileCache.save(currentProfile);
            } else {
                this.profileCache.clear();
            }
        }
        if (!Utility.areObjectsEqual(oldProfile, currentProfile)) {
            sendCurrentProfileChangedBroadcast(oldProfile, currentProfile);
        }
    }

    private void sendCurrentProfileChangedBroadcast(Profile oldProfile, Profile currentProfile) {
        Intent intent = new Intent(ACTION_CURRENT_PROFILE_CHANGED);
        intent.putExtra(EXTRA_OLD_PROFILE, oldProfile);
        intent.putExtra(EXTRA_NEW_PROFILE, currentProfile);
        this.localBroadcastManager.sendBroadcast(intent);
    }
}
