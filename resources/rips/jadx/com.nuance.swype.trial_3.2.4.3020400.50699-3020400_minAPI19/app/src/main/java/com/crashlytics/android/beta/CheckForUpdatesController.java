package com.crashlytics.android.beta;

import android.content.Context;
import io.fabric.sdk.android.services.common.CurrentTimeProvider;
import io.fabric.sdk.android.services.common.IdManager;
import io.fabric.sdk.android.services.network.HttpRequestFactory;
import io.fabric.sdk.android.services.persistence.PreferenceStore;
import io.fabric.sdk.android.services.settings.BetaSettingsData;

/* loaded from: classes.dex */
final class CheckForUpdatesController {
    final Beta beta;
    final BetaSettingsData betaSettings;
    final BuildProperties buildProps;
    final Context context;
    final CurrentTimeProvider currentTimeProvider;
    final HttpRequestFactory httpRequestFactory;
    final IdManager idManager;
    final PreferenceStore preferenceStore;

    public CheckForUpdatesController(Context context, Beta beta, IdManager idManager, BetaSettingsData betaSettings, BuildProperties buildProps, PreferenceStore prefsStore, CurrentTimeProvider currentTimeProvider, HttpRequestFactory httpRequestFactory) {
        this.context = context;
        this.beta = beta;
        this.idManager = idManager;
        this.betaSettings = betaSettings;
        this.buildProps = buildProps;
        this.preferenceStore = prefsStore;
        this.currentTimeProvider = currentTimeProvider;
        this.httpRequestFactory = httpRequestFactory;
    }
}
