package com.google.android.gms.internal;

import com.facebook.internal.AnalyticsEvents;
import com.facebook.internal.NativeProtocol;

/* loaded from: classes.dex */
public enum zzng {
    CLIENT_LOGIN_DISABLED("ClientLoginDisabled"),
    DEVICE_MANAGEMENT_REQUIRED("DeviceManagementRequiredOrSyncDisabled"),
    SOCKET_TIMEOUT("SocketTimeout"),
    SUCCESS("Ok"),
    UNKNOWN_ERROR("UNKNOWN_ERR"),
    NETWORK_ERROR(NativeProtocol.ERROR_NETWORK_ERROR),
    SERVICE_UNAVAILABLE("ServiceUnavailable"),
    INTNERNAL_ERROR("InternalError"),
    BAD_AUTHENTICATION("BadAuthentication"),
    EMPTY_CONSUMER_PKG_OR_SIG("EmptyConsumerPackageOrSig"),
    NEEDS_2F("InvalidSecondFactor"),
    NEEDS_POST_SIGN_IN_FLOW("PostSignInFlowRequired"),
    NEEDS_BROWSER("NeedsBrowser"),
    UNKNOWN(AnalyticsEvents.PARAMETER_DIALOG_OUTCOME_VALUE_UNKNOWN),
    NOT_VERIFIED("NotVerified"),
    TERMS_NOT_AGREED("TermsNotAgreed"),
    ACCOUNT_DISABLED("AccountDisabled"),
    CAPTCHA("CaptchaRequired"),
    ACCOUNT_DELETED("AccountDeleted"),
    SERVICE_DISABLED(NativeProtocol.ERROR_SERVICE_DISABLED),
    NEED_PERMISSION("NeedPermission"),
    INVALID_SCOPE("INVALID_SCOPE"),
    USER_CANCEL("UserCancel"),
    PERMISSION_DENIED(NativeProtocol.ERROR_PERMISSION_DENIED),
    INVALID_AUDIENCE("INVALID_AUDIENCE"),
    UNREGISTERED_ON_API_CONSOLE("UNREGISTERED_ON_API_CONSOLE"),
    THIRD_PARTY_DEVICE_MANAGEMENT_REQUIRED("ThirdPartyDeviceManagementRequired"),
    DM_INTERNAL_ERROR("DeviceManagementInternalError"),
    DM_SYNC_DISABLED("DeviceManagementSyncDisabled"),
    DM_ADMIN_BLOCKED("DeviceManagementAdminBlocked"),
    DM_ADMIN_PENDING_APPROVAL("DeviceManagementAdminPendingApproval"),
    DM_STALE_SYNC_REQUIRED("DeviceManagementStaleSyncRequired"),
    DM_DEACTIVATED("DeviceManagementDeactivated"),
    DM_REQUIRED("DeviceManagementRequired"),
    ALREADY_HAS_GMAIL("ALREADY_HAS_GMAIL"),
    BAD_PASSWORD("WeakPassword"),
    BAD_REQUEST("BadRequest"),
    BAD_USERNAME("BadUsername"),
    DELETED_GMAIL("DeletedGmail"),
    EXISTING_USERNAME("ExistingUsername"),
    LOGIN_FAIL("LoginFail"),
    NOT_LOGGED_IN("NotLoggedIn"),
    NO_GMAIL("NoGmail"),
    REQUEST_DENIED("RequestDenied"),
    SERVER_ERROR("ServerError"),
    USERNAME_UNAVAILABLE("UsernameUnavailable"),
    GPLUS_OTHER("GPlusOther"),
    GPLUS_NICKNAME("GPlusNickname"),
    GPLUS_INVALID_CHAR("GPlusInvalidChar"),
    GPLUS_INTERSTITIAL("GPlusInterstitial"),
    GPLUS_PROFILE_ERROR("ProfileUpgradeError");

    private final String fs;

    zzng(String str) {
        this.fs = str;
    }

    public static boolean zza(zzng zzngVar) {
        return BAD_AUTHENTICATION.equals(zzngVar) || CAPTCHA.equals(zzngVar) || NEED_PERMISSION.equals(zzngVar) || NEEDS_BROWSER.equals(zzngVar) || USER_CANCEL.equals(zzngVar) || DEVICE_MANAGEMENT_REQUIRED.equals(zzngVar) || DM_INTERNAL_ERROR.equals(zzngVar) || DM_SYNC_DISABLED.equals(zzngVar) || DM_ADMIN_BLOCKED.equals(zzngVar) || DM_ADMIN_PENDING_APPROVAL.equals(zzngVar) || DM_STALE_SYNC_REQUIRED.equals(zzngVar) || DM_DEACTIVATED.equals(zzngVar) || DM_REQUIRED.equals(zzngVar) || THIRD_PARTY_DEVICE_MANAGEMENT_REQUIRED.equals(zzngVar);
    }

    public static boolean zzb(zzng zzngVar) {
        return NETWORK_ERROR.equals(zzngVar) || SERVICE_UNAVAILABLE.equals(zzngVar);
    }

    public static final zzng zzfx(String str) {
        zzng zzngVar = null;
        zzng[] values = values();
        int length = values.length;
        int i = 0;
        while (i < length) {
            zzng zzngVar2 = values[i];
            if (!zzngVar2.fs.equals(str)) {
                zzngVar2 = zzngVar;
            }
            i++;
            zzngVar = zzngVar2;
        }
        return zzngVar;
    }
}
