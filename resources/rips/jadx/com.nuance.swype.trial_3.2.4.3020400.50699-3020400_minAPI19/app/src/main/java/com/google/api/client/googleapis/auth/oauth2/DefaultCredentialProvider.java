package com.google.api.client.googleapis.auth.oauth2;

/* loaded from: classes.dex */
final class DefaultCredentialProvider extends SystemEnvironmentProvider {
    private GoogleCredential cachedCredential = null;
    private Environment detectedEnvironment = null;

    /* loaded from: classes.dex */
    private enum Environment {
        UNKNOWN,
        ENVIRONMENT_VARIABLE,
        WELL_KNOWN_FILE,
        CLOUD_SHELL,
        APP_ENGINE,
        COMPUTE_ENGINE
    }
}
