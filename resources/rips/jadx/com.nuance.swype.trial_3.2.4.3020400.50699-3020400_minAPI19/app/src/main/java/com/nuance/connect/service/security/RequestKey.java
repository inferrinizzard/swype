package com.nuance.connect.service.security;

/* loaded from: classes.dex */
public class RequestKey {
    private static String requestKey;

    private RequestKey() {
    }

    private static void generateKey() {
        requestKey = "R3JvbWl0LCB0aGF0J3MgaXRcISBDaGVlc2VcISBXZSdsbCBnbyBzb21ld2hlcmU";
    }

    public static String getKey() {
        if (requestKey == null) {
            generateKey();
        }
        return requestKey;
    }
}
