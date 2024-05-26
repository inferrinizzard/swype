package com.nuance.nmsp.client.sdk.components.core.calllog;

import com.nuance.nmsp.client.sdk.components.core.calllog.CalllogSender;
import com.nuance.nmsp.client.sdk.components.core.internal.calllog.CalllogSenderImpl;

/* loaded from: classes.dex */
public class CalllogSenderFactory {
    public static CalllogSender createCalllogSender(CalllogSender.SenderListener senderListener) {
        return new CalllogSenderImpl(senderListener);
    }
}
