package com.crashlytics.android;

/* loaded from: classes.dex */
final class BuildIdValidator {
    final String buildId;
    final boolean requiringBuildId;

    public BuildIdValidator(String buildId, boolean requiringBuildId) {
        this.buildId = buildId;
        this.requiringBuildId = requiringBuildId;
    }
}
