package com.nuance.nmdp.speechkit.transaction.generic;

import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public interface IGenericParamValue {
    public static final int TYPE_BYTES = 4;
    public static final int TYPE_DICT = 2;
    public static final int TYPE_INT = 1;
    public static final int TYPE_SEQ = 3;
    public static final int TYPE_SEQ_CHUNK = 6;
    public static final int TYPE_SEQ_END = 7;
    public static final int TYPE_SEQ_START = 5;
    public static final int TYPE_STRING = 0;

    byte[] getBytesValue();

    Map<String, IGenericParamValue> getDictValue();

    int getIntValue();

    List<IGenericParamValue> getSeqValue();

    String getStringValue();

    int getType();
}
