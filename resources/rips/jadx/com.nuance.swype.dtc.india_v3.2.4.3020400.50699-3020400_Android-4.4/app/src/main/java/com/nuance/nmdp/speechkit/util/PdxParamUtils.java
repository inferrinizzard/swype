package com.nuance.nmdp.speechkit.util;

import com.nuance.nmdp.speechkit.transaction.TransactionBase;
import com.nuance.nmdp.speechkit.transaction.generic.IGenericParamValue;
import com.nuance.nmdp.speechkit.util.pdx.PdxValue;
import com.nuance.nmsp.client.sdk.components.core.pdx.Dictionary;
import com.nuance.nmsp.client.sdk.components.core.pdx.Sequence;
import com.nuance.swype.input.R;
import java.util.Enumeration;
import java.util.Map;

/* loaded from: classes.dex */
public final class PdxParamUtils {
    public static Dictionary createDictFromValue(TransactionBase transaction, Map<String, IGenericParamValue> map) {
        Dictionary ret = transaction.createDictionary();
        for (Map.Entry<String, IGenericParamValue> entry : map.entrySet()) {
            String key = entry.getKey();
            IGenericParamValue value = entry.getValue();
            switch (value.getType()) {
                case 0:
                    ret.addUTF8String(key, value.getStringValue());
                    break;
                case 1:
                    ret.addInteger(key, value.getIntValue());
                    break;
                case 2:
                    ret.addDictionary(key, createDictFromValue(transaction, value.getDictValue()));
                    break;
                case 3:
                    ret.addSequence(key, createSeqFromValue(transaction, value.getSeqValue()));
                    break;
                case 4:
                    ret.addByteString(key, value.getBytesValue());
                    break;
            }
        }
        return ret;
    }

    public static Sequence createSeqFromValue(TransactionBase transaction, java.util.List<IGenericParamValue> list) {
        Sequence ret = transaction.createSequence();
        for (IGenericParamValue value : list) {
            switch (value.getType()) {
                case 0:
                    ret.addUTF8String(value.getStringValue());
                    break;
                case 1:
                    ret.addInteger(value.getIntValue());
                    break;
                case 2:
                    ret.addDictionary(createDictFromValue(transaction, value.getDictValue()));
                    break;
                case 3:
                    ret.addSequence(createSeqFromValue(transaction, value.getSeqValue()));
                    break;
                case 4:
                    ret.addByteString(value.getBytesValue());
                    break;
            }
        }
        return ret;
    }

    public static PdxValue.Dictionary createFromPdx(Dictionary dict) {
        PdxValue.Dictionary ret = new PdxValue.Dictionary(null);
        Enumeration e = dict.keys();
        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            switch (dict.getType(key)) {
                case 4:
                    ret.put(key, dict.getByteString(key));
                    break;
                case 16:
                    ret.put(key, createFromPdx(dict.getSequence(key)));
                    break;
                case 22:
                    ret.put(key, dict.getAsciiString(key));
                    break;
                case R.styleable.ThemeTemplate_btnKeyboardKeyNormalTop /* 192 */:
                    ret.put(key, dict.getInteger(key));
                    break;
                case R.styleable.ThemeTemplate_btnKeyboardKeyNormal5row /* 193 */:
                    ret.put(key, dict.getUTF8String(key));
                    break;
                case 224:
                    ret.put(key, createFromPdx(dict.getDictionary(key)));
                    break;
                default:
                    Logger.warn("PdxValue", "Unsupported PDX type found in dictionary, skipping");
                    break;
            }
        }
        return ret;
    }

    public static PdxValue.Sequence createFromPdx(Sequence seq) {
        PdxValue.Sequence ret = new PdxValue.Sequence(null);
        for (int i = 0; i < seq.size(); i++) {
            switch (seq.getType(i)) {
                case 4:
                    ret.add(seq.getByteString(i));
                    break;
                case 16:
                    ret.add(createFromPdx(seq.getSequence(i)));
                    break;
                case 22:
                    ret.add(seq.getAsciiString(i));
                    break;
                case R.styleable.ThemeTemplate_btnKeyboardKeyNormalTop /* 192 */:
                    ret.add(seq.getInteger(i));
                    break;
                case R.styleable.ThemeTemplate_btnKeyboardKeyNormal5row /* 193 */:
                    ret.add(seq.getUTF8String(i));
                    break;
                case 224:
                    ret.add(createFromPdx(seq.getDictionary(i)));
                    break;
                default:
                    Logger.warn("PdxValue", "Unsupported PDX type found in sequence, skipping");
                    break;
            }
        }
        return ret;
    }
}
