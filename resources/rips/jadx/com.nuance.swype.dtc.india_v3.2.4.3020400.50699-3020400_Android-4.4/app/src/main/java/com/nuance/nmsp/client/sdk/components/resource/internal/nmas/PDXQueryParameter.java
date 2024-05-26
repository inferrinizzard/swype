package com.nuance.nmsp.client.sdk.components.resource.internal.nmas;

import com.facebook.share.internal.ShareConstants;
import com.nuance.nmsp.client.sdk.common.oem.api.LogFactory;
import com.nuance.nmsp.client.sdk.components.resource.nmas.NMASResource;
import com.nuance.swype.input.InputFieldInfo;

/* loaded from: classes.dex */
public class PDXQueryParameter extends PDXMessage {
    private static final LogFactory.Log a = LogFactory.getLog(PDXQueryParameter.class);

    public PDXQueryParameter(PDXParam pDXParam) {
        super((short) 515);
        if (a.isDebugEnabled()) {
            a.debug("PDXQueryParameter()");
        }
        put("name", pDXParam.getName(), (short) 193);
        if (pDXParam.getType() == Byte.MAX_VALUE) {
            put("type", 5, NMASResource.PDX_DATA_TYPE_INT);
        } else {
            put("type", pDXParam.getType(), NMASResource.PDX_DATA_TYPE_INT);
        }
        switch (pDXParam.getType()) {
            case 1:
                put("buffer_id", ((PDXAudioParam) pDXParam).getBufferId(), NMASResource.PDX_DATA_TYPE_INT);
                return;
            case 2:
                put(InputFieldInfo.INPUT_TYPE_TEXT, ((PDXTextParam) pDXParam).getText(), (short) 193);
                return;
            case 3:
                put(InputFieldInfo.INPUT_TYPE_TEXT, ((PDXChoiceParam) pDXParam).getChoicename(), (short) 193);
                return;
            case 4:
                put(ShareConstants.WEB_DIALOG_PARAM_DATA, ((PDXDataParam) pDXParam).getData(), (short) 193);
                return;
            case 5:
                put("dict", ((PDXDictParam) pDXParam).getContent(), NMASResource.PDX_DATA_TYPE_DICT);
                return;
            case 6:
            case 7:
            case 8:
                put("dict", ((PDXSeqParam) pDXParam).getContent(), NMASResource.PDX_DATA_TYPE_DICT);
                return;
            case Byte.MAX_VALUE:
                put("dict", ((PDXTTSParam) pDXParam).getContent(), NMASResource.PDX_DATA_TYPE_DICT);
                return;
            default:
                a.error("PDXQueryParameter() Unknown parameter type: " + ((int) pDXParam.getType()) + ". ");
                return;
        }
    }
}
