package io.fabric.sdk.android.services.common;

/* loaded from: classes.dex */
public final class ResponseParser {
    public static int parse(int statusCode) {
        if (statusCode >= 200 && statusCode <= 299) {
            return 0;
        }
        if (statusCode >= 300 && statusCode <= 399) {
            return 1;
        }
        if (statusCode < 400 || statusCode > 499) {
            return statusCode >= 500 ? 1 : 1;
        }
        return 0;
    }
}
