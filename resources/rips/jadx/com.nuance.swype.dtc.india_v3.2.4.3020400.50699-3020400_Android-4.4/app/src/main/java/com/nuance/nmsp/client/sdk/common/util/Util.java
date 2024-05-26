package com.nuance.nmsp.client.sdk.common.util;

import com.nuance.nmsp.client.sdk.common.defines.NMSPDefines;
import com.nuance.nmsp.client.sdk.common.oem.api.LogFactory;

/* loaded from: classes.dex */
public class Util {
    private static final LogFactory.Log a = LogFactory.getLog(Util.class);

    public static NMSPDefines.Codec adjustCodecForBluetooth(NMSPDefines.Codec codec) {
        NMSPDefines.Codec codec2 = (codec == NMSPDefines.Codec.SPEEX_16K || codec == NMSPDefines.Codec.SPEEX_11K) ? NMSPDefines.Codec.SPEEX_8K : (codec == NMSPDefines.Codec.PCM_16_11K || codec == NMSPDefines.Codec.PCM_16_16K || codec == NMSPDefines.Codec.PCM_16_22K || codec == NMSPDefines.Codec.PCM_16_32K) ? NMSPDefines.Codec.PCM_16_8K : codec;
        if (a.isInfoEnabled()) {
            a.info("adjustCodecForBluetooth() " + ((int) codec.getValue()) + " -> " + ((int) codec2.getValue()));
        }
        return codec2;
    }

    public static String escapeXML(String str) {
        if (a.isTraceEnabled()) {
            a.trace("Escaping XML reserved tokens (&, <, >, \" and ') of: " + str);
        }
        int i = 0;
        StringBuffer stringBuffer = new StringBuffer(str);
        while (i < stringBuffer.length()) {
            char charAt = stringBuffer.charAt(i);
            if (charAt == '&') {
                int i2 = i + 1;
                stringBuffer.insert(i2, "amp;");
                i = i2 + 4;
            } else if (charAt == '<') {
                stringBuffer.deleteCharAt(i);
                stringBuffer.insert(i, "&lt;");
                i += 4;
            } else if (charAt == '>') {
                stringBuffer.deleteCharAt(i);
                stringBuffer.insert(i, "&gt;");
                i += 4;
            } else if (charAt == '\"') {
                stringBuffer.deleteCharAt(i);
                stringBuffer.insert(i, "&quot;");
                i += 6;
            } else if (charAt == '\'') {
                stringBuffer.deleteCharAt(i);
                stringBuffer.insert(i, "&apos;");
                i += 6;
            } else {
                i++;
            }
        }
        String stringBuffer2 = stringBuffer.toString();
        if (a.isTraceEnabled()) {
            a.trace("Final output: " + stringBuffer2);
        }
        return stringBuffer2;
    }

    public static String getCodecStr(NMSPDefines.Codec codec) {
        short value = codec.getValue();
        return value == NMSPDefines.Codec.PCM_16_8K.getValue() ? "E_ENC_PCM_16_8K" : value == NMSPDefines.Codec.PCM_16_11K.getValue() ? "E_ENC_PCM_16_11K" : "UNKOWN_CODEC";
    }

    public static boolean isAMRCodec(NMSPDefines.Codec codec) {
        return false;
    }

    public static boolean isGSMCodec(NMSPDefines.Codec codec) {
        return codec.getValue() == NMSPDefines.Codec.MS_GSM_FR.getValue();
    }

    public static boolean isMP3Codec(NMSPDefines.Codec codec) {
        return false;
    }

    public static boolean isOpusCodec(NMSPDefines.Codec codec) {
        return codec.getValue() == NMSPDefines.Codec.OPUS_16K.getValue();
    }

    public static boolean isPCMCodec(NMSPDefines.Codec codec) {
        short value = codec.getValue();
        return value == NMSPDefines.Codec.PCM_16_8K.getValue() || value == NMSPDefines.Codec.PCM_16_11K.getValue() || value == NMSPDefines.Codec.PCM_16_16K.getValue() || value == NMSPDefines.Codec.PCM_16_32K.getValue();
    }

    public static boolean isSpeexCodec(NMSPDefines.Codec codec) {
        short value = codec.getValue();
        return value == NMSPDefines.Codec.SPEEX_8K.getValue() || value == NMSPDefines.Codec.SPEEX_11K.getValue() || value == NMSPDefines.Codec.SPEEX_16K.getValue();
    }
}
