package com.google.android.gms.common.api;

import com.nuance.connect.common.Strings;
import com.nuance.nmsp.client.sdk.components.resource.internal.nmas.PDXTransactionImpl;

/* loaded from: classes.dex */
public final class CommonStatusCodes {
    public static String getStatusCodeString(int i) {
        switch (i) {
            case -1:
                return "SUCCESS_CACHE";
            case 0:
                return Strings.STATUS_SUCCESS;
            case 1:
            case 9:
            case 11:
            case 12:
            default:
                return new StringBuilder(32).append("unknown status code: ").append(i).toString();
            case 2:
                return "SERVICE_VERSION_UPDATE_REQUIRED";
            case 3:
                return "SERVICE_DISABLED";
            case 4:
                return "SIGN_IN_REQUIRED";
            case 5:
                return "INVALID_ACCOUNT";
            case 6:
                return "RESOLUTION_REQUIRED";
            case 7:
                return "NETWORK_ERROR";
            case 8:
                return PDXTransactionImpl.INTERNAL_COMPLETION_CAUSE_INTERNAL_ERROR;
            case 10:
                return "DEVELOPER_ERROR";
            case 13:
                return "ERROR";
            case 14:
                return "INTERRUPTED";
            case 15:
                return "TIMEOUT";
            case 16:
                return "CANCELED";
            case 17:
                return "API_NOT_CONNECTED";
            case 18:
                return "DEAD_CLIENT";
        }
    }
}
