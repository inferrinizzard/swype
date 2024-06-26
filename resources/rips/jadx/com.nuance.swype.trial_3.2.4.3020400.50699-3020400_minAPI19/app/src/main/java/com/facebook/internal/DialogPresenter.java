package com.facebook.internal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import com.facebook.FacebookActivity;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.internal.Utility;

/* loaded from: classes.dex */
public class DialogPresenter {

    /* loaded from: classes.dex */
    public interface ParameterProvider {
        Bundle getLegacyParameters();

        Bundle getParameters();
    }

    public static void setupAppCallForCannotShowError(AppCall appCall) {
        FacebookException e = new FacebookException("Unable to show the provided content via the web or the installed version of the Facebook app. Some dialogs are only supported starting API 14.");
        setupAppCallForValidationError(appCall, e);
    }

    public static void setupAppCallForValidationError(AppCall appCall, FacebookException validationError) {
        setupAppCallForErrorResult(appCall, validationError);
    }

    public static void present(AppCall appCall, Activity activity) {
        activity.startActivityForResult(appCall.getRequestIntent(), appCall.getRequestCode());
        appCall.setPending();
    }

    public static void present(AppCall appCall, FragmentWrapper fragmentWrapper) {
        fragmentWrapper.startActivityForResult(appCall.getRequestIntent(), appCall.getRequestCode());
        appCall.setPending();
    }

    public static boolean canPresentNativeDialogWithFeature(DialogFeature feature) {
        return getProtocolVersionForNativeDialog(feature) != -1;
    }

    public static boolean canPresentWebFallbackDialogWithFeature(DialogFeature feature) {
        return getDialogWebFallbackUri(feature) != null;
    }

    public static void setupAppCallForErrorResult(AppCall appCall, FacebookException exception) {
        if (exception != null) {
            Validate.hasFacebookActivity(FacebookSdk.getApplicationContext());
            Intent errorResultIntent = new Intent();
            errorResultIntent.setClass(FacebookSdk.getApplicationContext(), FacebookActivity.class);
            errorResultIntent.setAction(FacebookActivity.PASS_THROUGH_CANCEL_ACTION);
            NativeProtocol.setupProtocolRequestIntent(errorResultIntent, appCall.getCallId().toString(), null, NativeProtocol.getLatestKnownVersion(), NativeProtocol.createBundleForException(exception));
            appCall.setRequestIntent(errorResultIntent);
        }
    }

    public static void setupAppCallForWebDialog(AppCall appCall, String actionName, Bundle parameters) {
        Validate.hasFacebookActivity(FacebookSdk.getApplicationContext());
        Validate.hasInternetPermissions(FacebookSdk.getApplicationContext());
        Bundle intentParameters = new Bundle();
        intentParameters.putString(NativeProtocol.WEB_DIALOG_ACTION, actionName);
        intentParameters.putBundle(NativeProtocol.WEB_DIALOG_PARAMS, parameters);
        Intent webDialogIntent = new Intent();
        NativeProtocol.setupProtocolRequestIntent(webDialogIntent, appCall.getCallId().toString(), actionName, NativeProtocol.getLatestKnownVersion(), intentParameters);
        webDialogIntent.setClass(FacebookSdk.getApplicationContext(), FacebookActivity.class);
        webDialogIntent.setAction(FacebookDialogFragment.TAG);
        appCall.setRequestIntent(webDialogIntent);
    }

    public static void setupAppCallForWebFallbackDialog(AppCall appCall, Bundle parameters, DialogFeature feature) {
        Uri fallbackUrl;
        Validate.hasFacebookActivity(FacebookSdk.getApplicationContext());
        Validate.hasInternetPermissions(FacebookSdk.getApplicationContext());
        String featureName = feature.name();
        Uri fallbackUrl2 = getDialogWebFallbackUri(feature);
        if (fallbackUrl2 == null) {
            throw new FacebookException("Unable to fetch the Url for the DialogFeature : '" + featureName + "'");
        }
        int protocolVersion = NativeProtocol.getLatestKnownVersion();
        Bundle webParams = ServerProtocol.getQueryParamsForPlatformActivityIntentWebFallback(appCall.getCallId().toString(), protocolVersion, parameters);
        if (webParams == null) {
            throw new FacebookException("Unable to fetch the app's key-hash");
        }
        if (fallbackUrl2.isRelative()) {
            fallbackUrl = Utility.buildUri(ServerProtocol.getDialogAuthority(), fallbackUrl2.toString(), webParams);
        } else {
            fallbackUrl = Utility.buildUri(fallbackUrl2.getAuthority(), fallbackUrl2.getPath(), webParams);
        }
        Bundle intentParameters = new Bundle();
        intentParameters.putString("url", fallbackUrl.toString());
        intentParameters.putBoolean(NativeProtocol.WEB_DIALOG_IS_FALLBACK, true);
        Intent webDialogIntent = new Intent();
        NativeProtocol.setupProtocolRequestIntent(webDialogIntent, appCall.getCallId().toString(), feature.getAction(), NativeProtocol.getLatestKnownVersion(), intentParameters);
        webDialogIntent.setClass(FacebookSdk.getApplicationContext(), FacebookActivity.class);
        webDialogIntent.setAction(FacebookDialogFragment.TAG);
        appCall.setRequestIntent(webDialogIntent);
    }

    public static void setupAppCallForNativeDialog(AppCall appCall, ParameterProvider parameterProvider, DialogFeature feature) {
        Bundle params;
        Context context = FacebookSdk.getApplicationContext();
        String action = feature.getAction();
        int protocolVersion = getProtocolVersionForNativeDialog(feature);
        if (protocolVersion == -1) {
            throw new FacebookException("Cannot present this dialog. This likely means that the Facebook app is not installed.");
        }
        if (NativeProtocol.isVersionCompatibleWithBucketedIntent(protocolVersion)) {
            params = parameterProvider.getParameters();
        } else {
            params = parameterProvider.getLegacyParameters();
        }
        if (params == null) {
            params = new Bundle();
        }
        Intent intent = NativeProtocol.createPlatformActivityIntent(context, appCall.getCallId().toString(), action, protocolVersion, params);
        if (intent == null) {
            throw new FacebookException("Unable to create Intent; this likely means theFacebook app is not installed.");
        }
        appCall.setRequestIntent(intent);
    }

    private static Uri getDialogWebFallbackUri(DialogFeature feature) {
        String featureName = feature.name();
        String action = feature.getAction();
        Utility.DialogFeatureConfig config = Utility.getDialogFeatureConfig(FacebookSdk.getApplicationId(), action, featureName);
        if (config == null) {
            return null;
        }
        Uri fallbackUrl = config.getFallbackUrl();
        return fallbackUrl;
    }

    public static int getProtocolVersionForNativeDialog(DialogFeature feature) {
        String applicationId = FacebookSdk.getApplicationId();
        String action = feature.getAction();
        int[] featureVersionSpec = getVersionSpecForFeature(applicationId, action, feature);
        return NativeProtocol.getLatestAvailableProtocolVersionForAction(action, featureVersionSpec);
    }

    private static int[] getVersionSpecForFeature(String applicationId, String actionName, DialogFeature feature) {
        Utility.DialogFeatureConfig config = Utility.getDialogFeatureConfig(applicationId, actionName, feature.name());
        return config != null ? config.getVersionSpec() : new int[]{feature.getMinVersion()};
    }

    public static void logDialogActivity(Context context, String eventName, String outcome) {
        AppEventsLogger logger = AppEventsLogger.newLogger(context);
        Bundle parameters = new Bundle();
        parameters.putString(AnalyticsEvents.PARAMETER_DIALOG_OUTCOME, outcome);
        logger.logSdkEvent(eventName, null, parameters);
    }
}
