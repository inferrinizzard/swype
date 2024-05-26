package com.nuance.nmsp.client.sdk.components.resource.internal.nmas;

import com.facebook.share.internal.ShareConstants;

/* loaded from: classes.dex */
public class PDXEnrollmentAudio extends PDXMessage {
    public PDXEnrollmentAudio(byte[] bArr) {
        super((short) 1026);
        put(ShareConstants.WEB_DIALOG_PARAM_DATA, bArr, (short) 4);
    }
}
