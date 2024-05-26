package com.facebook.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import com.facebook.AccessTokenSource;
import com.facebook.CustomTabMainActivity;
import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.FacebookRequestError;
import com.facebook.FacebookSdk;
import com.facebook.FacebookServiceException;
import com.facebook.internal.AnalyticsEvents;
import com.facebook.internal.NativeProtocol;
import com.facebook.internal.ServerProtocol;
import com.facebook.internal.Utility;
import com.facebook.internal.Validate;
import com.facebook.login.LoginClient;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class CustomTabLoginMethodHandler extends WebLoginMethodHandler {
    private static final int API_EC_DIALOG_CANCEL = 4201;
    private static final int CHALLENGE_LENGTH = 20;
    private static final String[] CHROME_PACKAGES = {"com.android.chrome", "com.chrome.beta", "com.chrome.dev"};
    public static final Parcelable.Creator<CustomTabLoginMethodHandler> CREATOR = new Parcelable.Creator() { // from class: com.facebook.login.CustomTabLoginMethodHandler.1
        @Override // android.os.Parcelable.Creator
        public final CustomTabLoginMethodHandler createFromParcel(Parcel source) {
            return new CustomTabLoginMethodHandler(source);
        }

        @Override // android.os.Parcelable.Creator
        public final CustomTabLoginMethodHandler[] newArray(int size) {
            return new CustomTabLoginMethodHandler[size];
        }
    };
    private static final String CUSTOM_TABS_SERVICE_ACTION = "android.support.customtabs.action.CustomTabsService";
    private static final int CUSTOM_TAB_REQUEST_CODE = 1;
    private String currentPackage;
    private String expectedChallenge;

    /* JADX INFO: Access modifiers changed from: package-private */
    public CustomTabLoginMethodHandler(LoginClient loginClient) {
        super(loginClient);
        this.expectedChallenge = Utility.generateRandomString(20);
    }

    @Override // com.facebook.login.LoginMethodHandler
    String getNameForLogging() {
        return "custom_tab";
    }

    @Override // com.facebook.login.WebLoginMethodHandler
    AccessTokenSource getTokenSource() {
        return AccessTokenSource.CHROME_CUSTOM_TAB;
    }

    @Override // com.facebook.login.WebLoginMethodHandler
    protected String getSSODevice() {
        return "chrome_custom_tab";
    }

    @Override // com.facebook.login.LoginMethodHandler
    boolean tryAuthorize(LoginClient.Request request) {
        if (!isCustomTabsAllowed()) {
            return false;
        }
        Bundle parameters = getParameters(request);
        Bundle parameters2 = addExtraParameters(parameters, request);
        Activity activity = this.loginClient.getActivity();
        Intent intent = new Intent(activity, (Class<?>) CustomTabMainActivity.class);
        intent.putExtra(CustomTabMainActivity.EXTRA_PARAMS, parameters2);
        intent.putExtra(CustomTabMainActivity.EXTRA_CHROME_PACKAGE, getChromePackage());
        this.loginClient.getFragment().startActivityForResult(intent, 1);
        return true;
    }

    private boolean isCustomTabsAllowed() {
        return isCustomTabsEnabled() && getChromePackage() != null && Validate.hasCustomTabRedirectActivity(FacebookSdk.getApplicationContext());
    }

    private boolean isCustomTabsEnabled() {
        Utility.FetchedAppSettings settings = Utility.getAppSettingsWithoutQuery(Utility.getMetadataApplicationId(this.loginClient.getActivity()));
        return settings != null && settings.getCustomTabsEnabled();
    }

    private String getChromePackage() {
        if (this.currentPackage != null) {
            return this.currentPackage;
        }
        Context context = this.loginClient.getActivity();
        Intent serviceIntent = new Intent(CUSTOM_TABS_SERVICE_ACTION);
        List<ResolveInfo> resolveInfos = context.getPackageManager().queryIntentServices(serviceIntent, 0);
        if (resolveInfos != null) {
            Set<String> chromePackages = new HashSet<>(Arrays.asList(CHROME_PACKAGES));
            Iterator i$ = resolveInfos.iterator();
            while (i$.hasNext()) {
                ServiceInfo serviceInfo = i$.next().serviceInfo;
                if (serviceInfo != null && chromePackages.contains(serviceInfo.packageName)) {
                    this.currentPackage = serviceInfo.packageName;
                    return this.currentPackage;
                }
            }
        }
        return null;
    }

    @Override // com.facebook.login.LoginMethodHandler
    boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != 1) {
            return super.onActivityResult(requestCode, resultCode, data);
        }
        LoginClient.Request request = this.loginClient.getPendingRequest();
        if (resultCode == -1) {
            onCustomTabComplete(data.getStringExtra(CustomTabMainActivity.EXTRA_URL), request);
            return true;
        }
        super.onComplete(request, null, new FacebookOperationCanceledException());
        return false;
    }

    private void onCustomTabComplete(String url, LoginClient.Request request) {
        if (url != null && url.startsWith(CustomTabMainActivity.getRedirectUrl())) {
            Uri uri = Uri.parse(url);
            Bundle values = Utility.parseUrlQueryString(uri.getQuery());
            values.putAll(Utility.parseUrlQueryString(uri.getFragment()));
            if (!validateChallengeParam(values)) {
                super.onComplete(request, null, new FacebookException("Invalid state parameter"));
                return;
            }
            String error = values.getString("error");
            if (error == null) {
                error = values.getString(NativeProtocol.BRIDGE_ARG_ERROR_TYPE);
            }
            String errorMessage = values.getString("error_msg");
            if (errorMessage == null) {
                errorMessage = values.getString(AnalyticsEvents.PARAMETER_SHARE_ERROR_MESSAGE);
            }
            if (errorMessage == null) {
                errorMessage = values.getString(NativeProtocol.BRIDGE_ARG_ERROR_DESCRIPTION);
            }
            String errorCodeString = values.getString(NativeProtocol.BRIDGE_ARG_ERROR_CODE);
            int errorCode = -1;
            if (!Utility.isNullOrEmpty(errorCodeString)) {
                try {
                    errorCode = Integer.parseInt(errorCodeString);
                } catch (NumberFormatException e) {
                    errorCode = -1;
                }
            }
            if (Utility.isNullOrEmpty(error) && Utility.isNullOrEmpty(errorMessage) && errorCode == -1) {
                super.onComplete(request, values, null);
                return;
            }
            if (error != null && (error.equals("access_denied") || error.equals("OAuthAccessDeniedException"))) {
                super.onComplete(request, null, new FacebookOperationCanceledException());
            } else if (errorCode == API_EC_DIALOG_CANCEL) {
                super.onComplete(request, null, new FacebookOperationCanceledException());
            } else {
                FacebookRequestError requestError = new FacebookRequestError(errorCode, error, errorMessage);
                super.onComplete(request, null, new FacebookServiceException(requestError, errorMessage));
            }
        }
    }

    @Override // com.facebook.login.LoginMethodHandler
    protected void putChallengeParam(JSONObject param) throws JSONException {
        param.put("7_challenge", this.expectedChallenge);
    }

    private boolean validateChallengeParam(Bundle values) {
        try {
            String stateString = values.getString(ServerProtocol.DIALOG_PARAM_STATE);
            if (stateString == null) {
                return false;
            }
            return new JSONObject(stateString).getString("7_challenge").equals(this.expectedChallenge);
        } catch (JSONException e) {
            return false;
        }
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    CustomTabLoginMethodHandler(Parcel source) {
        super(source);
        this.expectedChallenge = source.readString();
    }

    @Override // com.facebook.login.LoginMethodHandler, android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.expectedChallenge);
    }
}
