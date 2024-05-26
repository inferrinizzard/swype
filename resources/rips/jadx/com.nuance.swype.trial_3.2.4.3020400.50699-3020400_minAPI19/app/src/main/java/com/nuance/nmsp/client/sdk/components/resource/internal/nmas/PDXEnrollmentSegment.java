package com.nuance.nmsp.client.sdk.components.resource.internal.nmas;

import com.nuance.nmsp.client.sdk.common.protocols.ProtocolDefines;
import com.nuance.swype.input.InputFieldInfo;

/* loaded from: classes.dex */
public class PDXEnrollmentSegment extends PDXMessage {
    public PDXEnrollmentSegment(String str, String str2) {
        super(ProtocolDefines.XMODE_VAP_COMMAND_PLAY_END);
        put(InputFieldInfo.INPUT_TYPE_TEXT, str, (short) 193);
        put("buffer_id", str2, (short) 193);
    }
}
