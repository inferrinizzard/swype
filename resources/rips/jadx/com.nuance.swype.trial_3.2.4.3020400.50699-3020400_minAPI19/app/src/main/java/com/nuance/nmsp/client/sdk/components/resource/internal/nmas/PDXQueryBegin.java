package com.nuance.nmsp.client.sdk.components.resource.internal.nmas;

import com.nuance.connect.common.Strings;
import com.nuance.nmsp.client.sdk.common.defines.NMSPDefines;
import com.nuance.nmsp.client.sdk.common.oem.api.LogFactory;
import com.nuance.nmsp.client.sdk.components.core.internal.pdx.PDXDictionary;
import com.nuance.nmsp.client.sdk.components.core.pdx.Dictionary;
import com.nuance.nmsp.client.sdk.components.resource.internal.common.NMSPSession;
import com.nuance.nmsp.client.sdk.components.resource.nmas.NMASResource;
import com.nuance.swype.input.AppPreferences;
import com.nuance.swype.input.R;
import java.util.Enumeration;

/* loaded from: classes.dex */
public class PDXQueryBegin extends PDXMessage {
    private static final LogFactory.Log a = LogFactory.getLog(PDXQueryBegin.class);

    public PDXQueryBegin(String str, String str2, String str3, String str4, String str5, String str6, NMSPDefines.Codec codec, String str7, short s, short s2, String str8, String str9, String str10, String str11, byte[] bArr, String str12, String str13, Dictionary dictionary) {
        super((short) 514);
        if (a.isDebugEnabled()) {
            a.debug("PDXQueryBegin()");
        }
        put("uid", str, (short) 193);
        put("pdx_version", str2, (short) 193);
        put("client_version", str3, (short) 193);
        put("script_version", str4, (short) 193);
        put(Strings.MESSAGE_BUNDLE_LANGUAGE, str5, (short) 193);
        put("region", str6, (short) 193);
        put("device_codec", codec.getValue(), NMASResource.PDX_DATA_TYPE_INT);
        put(AppPreferences.DICTATION_LANGUAGE, str7, (short) 193);
        put("lcd_width", s, NMASResource.PDX_DATA_TYPE_INT);
        put("lcd_height", s2, NMASResource.PDX_DATA_TYPE_INT);
        if (str8 == null) {
            put("carrier", new byte[0], (short) 5);
        } else {
            put("carrier", str8, (short) 193);
        }
        put("phone_model", str9, (short) 193);
        put("phone_number", str10, (short) 193);
        put("original_session_id", str11, (short) 22);
        if (bArr != null) {
            put("new_session_id", NMSPSession.FormatUuid(bArr), (short) 193);
        }
        put("application", str12, (short) 22);
        put("nmaid", str12, (short) 22);
        put("command", str13, (short) 22);
        if (dictionary != null) {
            Enumeration keys = dictionary.keys();
            while (keys.hasMoreElements()) {
                String str14 = (String) keys.nextElement();
                switch (((PDXDictionary) dictionary).getClass(str14).getType()) {
                    case 4:
                        put(str14, dictionary.getByteString(str14), (short) 4);
                        break;
                    case 5:
                        put(str14, (byte[]) null, (short) 5);
                        break;
                    case 16:
                        a.error("PDXQueryBegin() Sequence not accepted in optionalKeys");
                        break;
                    case 22:
                        put(str14, dictionary.getAsciiString(str14), (short) 22);
                        break;
                    case R.styleable.ThemeTemplate_btnKeyboardKeyNormalTop /* 192 */:
                        put(str14, dictionary.getInteger(str14), NMASResource.PDX_DATA_TYPE_INT);
                        break;
                    case R.styleable.ThemeTemplate_btnKeyboardKeyNormal5row /* 193 */:
                        put(str14, dictionary.getUTF8String(str14), (short) 193);
                        break;
                    case 224:
                        a.error("PDXQueryBegin() Dictionary not accepted in optionalKeys");
                        break;
                }
            }
        }
    }
}
